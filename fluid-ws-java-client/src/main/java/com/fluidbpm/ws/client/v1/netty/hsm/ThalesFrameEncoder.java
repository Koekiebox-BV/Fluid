package com.fluidbpm.ws.client.v1.netty.hsm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * Encodes Thales HSM commands into the proper frame format.
 * Thales protocol uses a 4-byte ASCII header containing the message length,
 * followed by the command data.
 *
 * Format: [4-byte length header][command data]
 * Example: "0010" + "NOTEST DATA" = "0010NOTEST DATA"
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesFrameEncoder extends MessageToByteEncoder<byte[]> {

    private static final int HEADER_LENGTH = 4;

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        // Calculate message length (excluding header)
        int messageLength = msg.length;

        // Create length header as 4-digit ASCII string (e.g., "0012" for 12 bytes)
        String lengthHeader = String.format("%04d", messageLength);

        // Write header
        out.writeBytes(lengthHeader.getBytes(StandardCharsets.US_ASCII));

        // Write message data
        out.writeBytes(msg);
    }

    /**
     * Gets the total frame size including header.
     *
     * @param messageLength The message data length
     * @return Total frame size
     */
    public static int getFrameSize(int messageLength) {
        return HEADER_LENGTH + messageLength;
    }
}
