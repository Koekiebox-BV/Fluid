package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes Thales HSM commands into the payShield TCP frame format.
 *
 * Per the payShield 10K Host Programmers Manual (section 2.1.2 "Sending Commands")
 * every command is prefixed with a 2-byte binary, big-endian LENGTH field:
 *
 * <pre>
 * Field    Size  Format  Description
 * LENGTH   2     Byte    Length of the COMMAND field
 * COMMAND  n     Byte    HSM command (message header + command code + data)
 * </pre>
 *
 * Example: an {@code NC} command with message header {@code 1234} is sent as
 * {@code 00 06 31 32 33 34 4E 43}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class ThalesFrameEncoder extends MessageToByteEncoder<byte[]> {

    /** Size of the binary length prefix in bytes. */
    public static final int LENGTH_FIELD_SIZE = 2;

    /** Largest payload that fits the 2-byte length prefix. */
    public static final int MAX_FRAME_LENGTH = 0xFFFF;

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) {
        if (msg.length > MAX_FRAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Thales frame too large: " + msg.length + " bytes (max " + MAX_FRAME_LENGTH + ")");
        }
        out.writeShort(msg.length);
        out.writeBytes(msg);
    }

    /**
     * Gets the total frame size including the length prefix.
     *
     * @param messageLength The message data length
     * @return Total frame size
     */
    public static int getFrameSize(int messageLength) {
        return LENGTH_FIELD_SIZE + messageLength;
    }
}
