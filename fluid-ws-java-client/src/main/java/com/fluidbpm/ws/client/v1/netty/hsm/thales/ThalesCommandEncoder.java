package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Encodes ThalesCommand objects into byte arrays for transmission.
 * The produced bytes are {@code [message header][command code][command data]};
 * {@link ThalesFrameEncoder} then adds the binary length prefix.
 *
 * @author jasonbruwer
 * @since 1.14
 */
public class ThalesCommandEncoder extends MessageToMessageEncoder<ThalesCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ThalesCommand msg, List<Object> out) {
        out.add(msg.toWireBytes());
    }
}
