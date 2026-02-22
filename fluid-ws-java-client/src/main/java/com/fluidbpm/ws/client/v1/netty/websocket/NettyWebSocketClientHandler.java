package com.fluidbpm.ws.client.v1.netty.websocket;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper;
import com.fluidbpm.ws.client.v1.asn1der.ASNGlobal;
import com.fluidbpm.ws.client.v1.asn1der.ASNMapperError;
import com.fluidbpm.ws.client.v1.stats.PerfStats;
import com.fluidbpm.ws.client.v1.websocket.IMessageResponseHandler;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.bouncycastle.asn1.ASN1Sequence;

import java.util.ArrayList;
import java.util.Map;

/**
 * Netty WebSocket client handler for processing WebSocket frames.
 * Handles both text and binary message types with appropriate callbacks.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class NettyWebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final WebSocketClientHandshaker handshaker;
    private final Map<String, ? extends IMessageResponseHandler> messageHandlers;
    private ChannelPromise handshakeFuture;
    private final WebSocketClient.Mode mode;

    private int sentMessages = 0;
    private int receivedMessages = 0;

    /**
     * Constructs a NettyWebSocketClientHandler with the specified handshaker, handlers, and mode.
     *
     * @param handshaker The WebSocket handshaker for protocol negotiation
     * @param messageHandlers Map of message handlers for processing responses
     * @param mode The mode (Text or Binary) for message processing
     */
    public NettyWebSocketClientHandler(
            WebSocketClientHandshaker handshaker,
            Map<String, ? extends IMessageResponseHandler> messageHandlers,
            WebSocketClient.Mode mode
    ) {
        this.handshaker = handshaker;
        this.messageHandlers = messageHandlers;
        this.mode = mode;
    }

    /**
     * Returns the handshake future promise.
     *
     * @return ChannelPromise for handshake completion
     */
    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (this.messageHandlers != null) {
            this.messageHandlers.values().forEach(IMessageResponseHandler::connectionClosed);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            handleTextMessage(textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            byte[] bytes = new byte[binaryFrame.content().readableBytes()];
            binaryFrame.content().readBytes(bytes);
            handleBinaryMessage(bytes);
        } else if (frame instanceof PongWebSocketFrame) {
            // Pong frame received
        } else if (frame instanceof CloseWebSocketFrame) {
            ch.close();
        }
    }

    /**
     * Handles text messages received from the WebSocket.
     *
     * @param message The text message content
     */
    private void handleTextMessage(String message) {
        this.receivedMessages++;

        boolean handlerFoundForMsg = false;
        for (IMessageResponseHandler handler : new ArrayList<>(this.messageHandlers.values())) {
            Object qualifyObj = handler.doesHandlerQualifyForProcessing(message);
            if (qualifyObj instanceof Error) {
                handler.handleMessage(qualifyObj);
            } else if (qualifyObj instanceof JsonObject) {
                handler.handleMessage(qualifyObj);
                handlerFoundForMsg = true;
                break;
            }
        }

        if (!handlerFoundForMsg) {
            throw new FluidClientException(
                    "(Text): No handler found for message;\n" + message,
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }
    }

    /**
     * Handles binary messages received from the WebSocket.
     *
     * @param message The binary message payload
     */
    private void handleBinaryMessage(byte[] message) {
        this.receivedMessages++;
        PerfStats.increment(PerfStats.Label.Asn1Der_BytesReceive, message.length);

        System.out.println("Zool-Receive (b64): "+ message.length);

        String on = PerfStats.timedStart();

        ASNMapperError initial = new ASNMapperError();
        ASN1Sequence asn1Seq = initial.initSeq(message);
        int typeCode = initial.asInt(asn1Seq.getObjectAt(ASNBaseMapper.Map.ID), "Type Code");
        String echo = initial.asGeneralTxt(asn1Seq.getObjectAt(ASNBaseMapper.Map.ECHO), "Echo");

        Object qualifyObj = null;
        IMessageResponseHandler handler = null;
        if (typeCode == ASNGlobal.Type.ERROR_TYPE) {
            for (IMessageResponseHandler handlerErr : new ArrayList<>(this.messageHandlers.values())) {
                qualifyObj = handlerErr.doesHandlerQualifyForProcessing(message);
                if (qualifyObj != null) {
                    handler = handlerErr;
                    break;
                }
            }
        } else {
            if (UtilGlobal.isNotBlank(echo)) {
                PerfStats.timedStop(PerfStats.Label.Asn1Der_RoundRobin, echo);
                handler = this.messageHandlers.get(echo);
            }
            if (handler == null) {
                throw new FluidClientException(
                        "(Binary): No handler found for message (" + typeCode + ");\n" + BaseEncoding.base16().encode(message),
                        FluidClientException.ErrorCode.IO_ERROR);
            }
            qualifyObj = handler.doesHandlerQualifyForProcessing(message);
        }

        if (qualifyObj == null) {
            throw new FluidClientException(
                    "(Binary): No qualified object found;\n" + BaseEncoding.base16().encode(message),
                    FluidClientException.ErrorCode.IO_ERROR
            );
        }

        PerfStats.timedStop(PerfStats.Label.Asn1DerMapper_DoesHandlerQualify, on);
        String tsHandleMsg = PerfStats.timedStart();
        handler.handleMessage(qualifyObj);
        PerfStats.timedStop(PerfStats.Label.Asn1DerMapper_HandleMessage, tsHandleMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }

        System.err.println("WS error: " + ctx.channel().id() + " -> ");
        cause.printStackTrace();

        for (IMessageResponseHandler handler : new ArrayList<>(this.messageHandlers.values())) {
            Error err = new Error(FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR, cause.getMessage());
            handler.handleMessage(err);
        }

        ctx.close();
    }

    /**
     * Gets the count of sent messages.
     *
     * @return The number of messages sent
     */
    public int getSentMessages() {
        return sentMessages;
    }

    /**
     * Gets the count of received messages.
     *
     * @return The number of messages received
     */
    public int getReceivedMessages() {
        return receivedMessages;
    }

    /**
     * Increments the sent messages counter.
     */
    public void incrementSentMessages() {
        this.sentMessages++;
    }
}
