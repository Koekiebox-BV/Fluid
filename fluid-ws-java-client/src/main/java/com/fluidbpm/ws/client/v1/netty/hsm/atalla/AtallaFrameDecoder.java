package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * Decodes Atalla AT1000 response frames.
 *
 * The AT1000 host interface has no length prefix (Command Reference Manual, "Programming
 * guidelines"): a response is the text from {@code <} up to and including {@code >}, optionally
 * followed by CRLF (HSM option 23, on by default). Characters before {@code <} are ignored, so
 * the CRLF trailer of the previous response is discarded naturally. The emitted frame is
 * {@code <...>} without the trailer.
 *
 * Responses whose binary data happens to contain {@code >} are not supported by this decoder;
 * use the hexadecimal data formats for such commands.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaFrameDecoder extends ByteToMessageDecoder {

    /** Default upper bound on a single response, in bytes. */
    public static final int DEFAULT_MAX_FRAME_LENGTH = 64 * 1024;

    private final int maxFrameLength;

    /**
     * Decoder with the {@link #DEFAULT_MAX_FRAME_LENGTH default} frame limit.
     */
    public AtallaFrameDecoder() {
        this(DEFAULT_MAX_FRAME_LENGTH);
    }

    /**
     * @param maxFrameLength Largest response accepted before the connection is failed
     */
    public AtallaFrameDecoder(int maxFrameLength) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be positive: " + maxFrameLength);
        }
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.isReadable()) {
            int start = in.indexOf(in.readerIndex(), in.writerIndex(), (byte) AtallaCommand.START);
            if (start < 0) {
                // Nothing but inter-frame noise (CRLF trailer etc.); drop it.
                in.skipBytes(in.readableBytes());
                return;
            }
            if (start > in.readerIndex()) {
                in.readerIndex(start);
            }

            int end = in.indexOf(in.readerIndex(), in.writerIndex(), (byte) AtallaCommand.END);
            if (end < 0) {
                if (in.readableBytes() > maxFrameLength) {
                    int length = in.readableBytes();
                    in.skipBytes(length);
                    throw new TooLongFrameException("Atalla response exceeds " + maxFrameLength
                            + " bytes without a closing '>' (" + length + " buffered)");
                }
                // Wait for the rest of the frame
                return;
            }

            int length = end - in.readerIndex() + 1;
            if (length > maxFrameLength) {
                in.skipBytes(length);
                throw new TooLongFrameException("Atalla response of " + length
                        + " bytes exceeds " + maxFrameLength);
            }
            byte[] frame = new byte[length];
            in.readBytes(frame);
            out.add(frame);
        }
    }
}
