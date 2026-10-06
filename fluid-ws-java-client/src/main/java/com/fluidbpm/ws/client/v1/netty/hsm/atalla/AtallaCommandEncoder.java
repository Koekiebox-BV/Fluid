package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes {@link AtallaCommand} objects for transmission.
 *
 * The AT1000 host interface is plain text with no length prefix: the bytes written are
 * exactly {@code <CMDID#field#...#[^tag#]>} (Command Reference Manual, "Command and
 * response format"). Nothing may follow the closing {@code #>}.
 *
 * @author jasonbruwer
 * @since 1.15
 */
public class AtallaCommandEncoder extends MessageToByteEncoder<AtallaCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AtallaCommand msg, ByteBuf out) {
        out.writeBytes(msg.toWireBytes());
    }
}
