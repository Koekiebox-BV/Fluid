package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesCommandEncoder;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesFrameDecoder;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesFrameEncoder;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesResponse;
import com.fluidbpm.ws.client.v1.netty.hsm.thales.ThalesResponseDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Verifies the payShield TCP framing and header handling against the
 * example in the Host Programmers Manual section 2.1.2:
 * NC with header 1234 is sent as 00 06 31 32 33 34 4E 43.
 */
public class TestThalesFraming {

    private static byte[] drain(ByteBuf buf) {
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        buf.release();
        return b;
    }

    @Test
    public void encodesManualExample() {
        EmbeddedChannel ch = new EmbeddedChannel(new ThalesFrameEncoder(), new ThalesCommandEncoder());
        assertTrue(ch.writeOutbound(new ThalesCommand("NC", "", "1234")));
        byte[] wire = drain(ch.readOutbound());
        assertArrayEquals(new byte[]{0x00, 0x06, 0x31, 0x32, 0x33, 0x34, 0x4E, 0x43}, wire);
        ch.finish();
    }

    @Test
    public void encodesWithoutHeader() {
        EmbeddedChannel ch = new EmbeddedChannel(new ThalesFrameEncoder(), new ThalesCommandEncoder());
        ch.writeOutbound(new ThalesCommand("NO", "00"));
        assertArrayEquals(new byte[]{0x00, 0x04, 'N', 'O', '0', '0'}, drain(ch.readOutbound()));
        ch.finish();
    }

    @Test
    public void rejectsOversizedFrame() {
        EmbeddedChannel ch = new EmbeddedChannel(new ThalesFrameEncoder(), new ThalesCommandEncoder());
        try {
            ch.writeOutbound(new ThalesCommand("NO", new byte[0x10000 - 1]));
            fail("expected oversize rejection");
        } catch (EncoderException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
        ch.finishAndReleaseAll();
    }

    @Test
    public void decodesSplitFrameAndHeader() {
        EmbeddedChannel ch = new EmbeddedChannel(new ThalesFrameDecoder(), new ThalesResponseDecoder(4));
        byte[] body = "1234ND00ABCDEF".getBytes(StandardCharsets.ISO_8859_1);

        // first only the length prefix and one byte of body arrives
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{0x00, (byte) body.length, body[0]}));
        assertNull(ch.readInbound());

        ch.writeInbound(Unpooled.wrappedBuffer(body, 1, body.length - 1));
        ThalesResponse r = ch.readInbound();
        assertEquals("1234", r.getHeader());
        assertEquals("1234", r.getRequestId());
        assertEquals("ND", r.getResponseCode());
        assertEquals("00", r.getErrorCode());
        assertEquals("ABCDEF", r.getResponseData());
        assertTrue(r.isSuccess());
        ch.finish();
    }

    @Test
    public void decodesTwoFramesInOneRead() {
        EmbeddedChannel ch = new EmbeddedChannel(new ThalesFrameDecoder(), new ThalesResponseDecoder(4));
        byte[] a = "0001NP00A".getBytes(StandardCharsets.ISO_8859_1);
        byte[] b = "0002NP00B".getBytes(StandardCharsets.ISO_8859_1);
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(a.length).writeBytes(a).writeShort(b.length).writeBytes(b);
        ch.writeInbound(buf);
        ThalesResponse r1 = ch.readInbound();
        ThalesResponse r2 = ch.readInbound();
        assertEquals("0001", r1.getHeader());
        assertEquals("A", r1.getResponseData());
        assertEquals("0002", r2.getHeader());
        assertEquals("B", r2.getResponseData());
        assertNull(ch.readInbound());
        ch.finish();
    }

    @Test
    public void preservesBinaryPayloadBothWays() {
        byte[] all = new byte[256];
        for (int i = 0; i < 256; i++) all[i] = (byte) i;

        ThalesCommand cmd = new ThalesCommand("EW", all, "ABCD");
        byte[] wire = cmd.toWireBytes();
        assertEquals(4 + 2 + 256, wire.length);
        for (int i = 0; i < 256; i++) assertEquals((byte) i, wire[6 + i]);

        byte[] raw = new byte[4 + 4 + 256];
        System.arraycopy("ABCDEX00".getBytes(StandardCharsets.ISO_8859_1), 0, raw, 0, 8);
        System.arraycopy(all, 0, raw, 8, 256);
        ThalesResponse r = new ThalesResponse(raw, 4);
        assertArrayEquals(all, r.getResponseDataBytes());
        assertEquals("EX", r.getResponseCode());
    }

    @Test
    public void shortResponseIsMalformed() {
        ThalesResponse r = new ThalesResponse("12NP".getBytes(StandardCharsets.ISO_8859_1), 4);
        assertFalse(r.isSuccess());
        assertEquals(ThalesResponse.MALFORMED_ERROR_CODE, r.getErrorCode());
    }

    @Test
    public void noHeaderResponseHasNullRequestId() {
        ThalesResponse r = new ThalesResponse("NP00X".getBytes(StandardCharsets.ISO_8859_1));
        assertNull(r.getRequestId());
        assertEquals("NP", r.getResponseCode());
        assertEquals("X", r.getResponseData());
    }
}
