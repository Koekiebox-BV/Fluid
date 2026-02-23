package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Decodes raw byte arrays into ThalesResponse objects.
 * This decoder converts the raw response bytes (already deframed by ThalesFrameDecoder)
 * into high-level ThalesResponse objects.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesResponseDecoder extends MessageToMessageDecoder<byte[]> {

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        // Convert raw bytes to ThalesResponse object
        ThalesResponse response = new ThalesResponse(msg);

        // Add to output
        out.add(response);
    }
}
