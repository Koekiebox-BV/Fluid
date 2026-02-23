package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Decodes Thales HSM response frames.
 * Thales protocol uses a 4-byte ASCII header containing the message length,
 * followed by the response data.
 *
 * Format: [4-byte length header][response data]
 * Example: "0010NPTEST DATA" means 10 bytes follow the header
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesFrameDecoder extends ByteToMessageDecoder {

    private static final int HEADER_LENGTH = 4;
    private static final int MAX_FRAME_LENGTH = 65535; // 64KB max message

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Wait until we have at least the header (4 bytes)
        if (in.readableBytes() < HEADER_LENGTH) {
            return;
        }

        // Mark the current position
        in.markReaderIndex();

        // Read the length header (4 ASCII digits)
        byte[] headerBytes = new byte[HEADER_LENGTH];
        in.readBytes(headerBytes);
        String lengthStr = new String(headerBytes, StandardCharsets.US_ASCII);

        // Parse the length
        int messageLength;
        try {
            messageLength = Integer.parseInt(lengthStr);
        } catch (NumberFormatException e) {
            // Invalid header - reset and skip this byte
            in.resetReaderIndex();
            in.readByte();
            return;
        }

        // Validate length
        if (messageLength < 0 || messageLength > MAX_FRAME_LENGTH) {
            throw new IllegalStateException("Invalid frame length: " + messageLength);
        }

        // Wait until we have the complete message
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        // Read the message data
        byte[] messageData = new byte[messageLength];
        in.readBytes(messageData);

        // Add to output
        out.add(messageData);
    }
}
