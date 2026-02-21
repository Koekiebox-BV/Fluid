package com.fluidbpm.ws.client.v1.netty.hsm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Encodes ThalesCommand objects into byte arrays for transmission.
 * This encoder converts the high-level ThalesCommand object into
 * the raw bytes that will be framed by ThalesFrameEncoder.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesCommandEncoder extends MessageToMessageEncoder<ThalesCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ThalesCommand msg, List<Object> out) throws Exception {
        // Convert the command to bytes
        byte[] commandBytes = msg.toBytes();

        // Add to output - will be processed by ThalesFrameEncoder
        out.add(commandBytes);
    }
}
