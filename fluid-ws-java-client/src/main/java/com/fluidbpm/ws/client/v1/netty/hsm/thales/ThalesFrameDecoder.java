package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Decodes payShield TCP response frames.
 *
 * Per the payShield 10K Host Programmers Manual (section 2.1.3 "Returning Responses")
 * every response is prefixed with a 2-byte binary, big-endian LENGTH field followed by
 * the RESPONSE bytes (message header + response code + error code + data).
 * The length prefix is stripped; the RESPONSE bytes are passed on unchanged.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class ThalesFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until we have the 2-byte length prefix
        if (in.readableBytes() < ThalesFrameEncoder.LENGTH_FIELD_SIZE) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readUnsignedShort();

        // Wait until the complete message has arrived
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] messageData = new byte[messageLength];
        in.readBytes(messageData);
        out.add(messageData);
    }
}
