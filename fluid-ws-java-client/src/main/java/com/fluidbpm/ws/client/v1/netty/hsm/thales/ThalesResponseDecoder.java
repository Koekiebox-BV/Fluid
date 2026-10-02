package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Decodes raw response bytes (already deframed by {@link ThalesFrameDecoder})
 * into {@link ThalesResponse} objects, splitting off the message header that the
 * HSM echoes back so the client can correlate the response with its request.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesResponseDecoder extends MessageToMessageDecoder<byte[]> {

    private final int headerLength;

    /**
     * Decoder for an HSM configured without a message header.
     */
    public ThalesResponseDecoder() {
        this(0);
    }

    /**
     * @param headerLength The message header length configured on the HSM (0-255).
     */
    public ThalesResponseDecoder(int headerLength) {
        this.headerLength = headerLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) {
        out.add(new ThalesResponse(msg, headerLength));
    }
}
