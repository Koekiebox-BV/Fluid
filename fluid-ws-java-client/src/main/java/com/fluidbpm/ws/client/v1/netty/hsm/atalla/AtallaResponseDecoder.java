package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Decodes {@code <...>} frames (already isolated by {@link AtallaFrameDecoder}) into
 * {@link AtallaResponse} objects, splitting out the fields and the echoed context tag
 * so the client can correlate the response with its request.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaResponseDecoder extends MessageToMessageDecoder<byte[]> {

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) {
        out.add(new AtallaResponse(msg));
    }
}
