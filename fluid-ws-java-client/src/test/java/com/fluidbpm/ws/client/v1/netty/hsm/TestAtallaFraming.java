package com.fluidbpm.ws.client.v1.netty.hsm;

import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaCommand;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaCommandEncoder;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaFrameDecoder;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaResponse;
import com.fluidbpm.ws.client.v1.netty.hsm.atalla.AtallaResponseDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Verifies the AT1000 text framing against the examples in the Command Reference Manual:
 * {@code <00#Hello#>} is sent as {@code 3C 30 30 23 48 65 6C 6C 6F 23 3E}, and the echo reply
 * to {@code <00#This is a test.#>} is {@code <00#000081#This is a test.#>}.
 */
public class TestAtallaFraming {

    private static byte[] drain(ByteBuf buf) {
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        buf.release();
        return b;
    }

    private static byte[] ascii(String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }

    @Test
    public void encodesManualEchoExample() {
        EmbeddedChannel ch = new EmbeddedChannel(new AtallaCommandEncoder());
        assertTrue(ch.writeOutbound(AtallaCommand.Commands.echo("Hello")));
        byte[] wire = drain(ch.readOutbound());
        assertArrayEquals(new byte[]{0x3C, 0x30, 0x30, 0x23, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x23, 0x3E}, wire);
        ch.finish();
    }

    @Test
    public void encodesFieldsEmptyFieldsAndContextTag() {
        AtallaCommand cmd = new AtallaCommand("10",
                Arrays.asList("1PUNE000", "1KDNE000,E801D324A94F85BC5833A73B5E804ACF5B87CBA329D1A74C,7CA7039EDE2AB473"),
                "Generate KPE for ATM 325");
        assertEquals("<10#1PUNE000#1KDNE000,E801D324A94F85BC5833A73B5E804ACF5B87CBA329D1A74C,7CA7039EDE2AB473"
                + "#^Generate KPE for ATM 325#>", new String(cmd.toWireBytes(), StandardCharsets.ISO_8859_1));

        // Empty fields are kept: <10#3PUNE000##D#>
        assertEquals("<10#3PUNE000##D#>", new AtallaCommand("10", "3PUNE000", "", "D").getFullCommand());
        // No fields: <1101#>
        assertEquals("<1101#>", AtallaCommand.Commands.softwareVersion().getFullCommand());
        // Null field is sent as empty
        assertEquals("<10#A##>", new AtallaCommand("10", "A", null).getFullCommand());
    }

    @Test
    public void rejectsReservedCharactersAndBadIds() {
        for (String bad : new String[]{"a#b", "a<b", "a>b", "a^b", "a\rb"}) {
            try {
                new AtallaCommand("00", bad);
                fail("expected rejection of " + bad);
            } catch (IllegalArgumentException expected) {
                // ok
            }
        }
        try {
            new AtallaCommand("0", "x");
            fail("1-char id must be rejected");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            new AtallaCommand("12345");
            fail("5-char id must be rejected");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            AtallaCommand.Commands.echo("");
            fail("empty echo must be rejected");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void decodesSplitFrameAndDropsCrlfTrailer() {
        EmbeddedChannel ch = new EmbeddedChannel(new AtallaFrameDecoder(), new AtallaResponseDecoder());
        byte[] body = ascii("<00#000081#This is a test.#>\r\n");

        // first only a few bytes arrive
        ch.writeInbound(Unpooled.wrappedBuffer(body, 0, 7));
        assertNull(ch.readInbound());

        ch.writeInbound(Unpooled.wrappedBuffer(body, 7, body.length - 7));
        AtallaResponse r = ch.readInbound();
        assertEquals("00", r.getResponseId());
        assertEquals("<00#000081#This is a test.#>", r.getFullResponse());
        assertTrue(r.isSuccess());
        assertFalse(r.isError());
        assertEquals("00", r.getErrorCode());
        assertEquals("81", r.getSoftwareVersion());
        assertEquals("This is a test.", r.getEchoMessage());
        assertNull(r.getContextTag());
        assertNull(ch.readInbound());
        ch.finish();
    }

    @Test
    public void decodesTwoFramesInOneReadAndIgnoresLeadingNoise() {
        EmbeddedChannel ch = new EmbeddedChannel(new AtallaFrameDecoder(), new AtallaResponseDecoder());
        ch.writeInbound(Unpooled.wrappedBuffer(ascii("\r\n<00#000085#A#^0001#>\r\n<00#000085#B#^0002#>\r\n")));
        AtallaResponse r1 = ch.readInbound();
        AtallaResponse r2 = ch.readInbound();
        assertEquals("0001", r1.getContextTag());
        assertEquals("A", r1.getEchoMessage());
        assertEquals("0002", r2.getRequestId());
        assertEquals("B", r2.getEchoMessage());
        assertNull(ch.readInbound());
        ch.finish();
    }

    @Test
    public void decodesWithoutCrlf() {
        EmbeddedChannel ch = new EmbeddedChannel(new AtallaFrameDecoder(), new AtallaResponseDecoder());
        ch.writeInbound(Unpooled.wrappedBuffer(ascii("<2101#Atalla HSM AT1000-AKB Version: 8.53#B19C#3#>")));
        AtallaResponse r = ch.readInbound();
        assertEquals("2101", r.getResponseId());
        assertEquals(3, r.getFieldCount());
        assertEquals("Atalla HSM AT1000-AKB Version: 8.53", r.getField(1));
        assertEquals("B19C", r.getField(2));
        assertEquals("3", r.getField(3));
        assertNull(r.getField(4));
        assertNull(r.getErrorCode());
        assertTrue(r.isSuccess());
        ch.finish();
    }

    @Test
    public void rejectsOversizedFrame() {
        EmbeddedChannel ch = new EmbeddedChannel(new AtallaFrameDecoder(16));
        try {
            ch.writeInbound(Unpooled.wrappedBuffer(ascii("<00#AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")));
            fail("expected oversize rejection");
        } catch (DecoderException e) {
            assertTrue(e instanceof TooLongFrameException || e.getCause() instanceof TooLongFrameException);
        }
        ch.finishAndReleaseAll();
    }

    @Test
    public void parsesManualErrorExamples() {
        // Length out of range in field 2, software version 8.00
        AtallaResponse r = new AtallaResponse(ascii("<00#010280#>"));
        assertTrue(r.isErrorFormat());
        assertTrue(r.isError());
        assertFalse(r.isSuccess());
        assertEquals("01", r.getErrorCode());
        assertEquals("02", r.getErrorFieldNumber());
        assertEquals("80", r.getSoftwareVersion());
        assertEquals("Length out of range", r.getErrorMessage());
        assertNull(r.getErrorDetail());
        assertNull(r.getEchoMessage());

        // Value out of range in field 0 with detailed error 0201 (option 21)
        AtallaResponse d = new AtallaResponse(ascii("<00#030080#0201##>"));
        assertEquals("03", d.getErrorCode());
        assertEquals("00", d.getErrorFieldNumber());
        assertEquals("Value out of range", d.getErrorMessage());
        assertEquals("0201", d.getErrorDetail());
    }

    @Test
    public void parsesContextTagAndEmptyFields() {
        AtallaResponse r = new AtallaResponse(ascii("<20#KEY1##DE9D#^Generate KPE for ATM 325#>"));
        assertEquals("20", r.getResponseId());
        assertEquals("Generate KPE for ATM 325", r.getContextTag());
        assertEquals(3, r.getFieldCount());
        assertEquals("KEY1", r.getField(1));
        assertEquals("", r.getField(2));
        assertEquals("DE9D", r.getField(3));
        assertEquals("No error", r.getErrorMessage());
    }

    @Test
    public void malformedResponseIsAnError() {
        AtallaResponse r = new AtallaResponse(ascii("garbage"));
        assertTrue(r.isMalformed());
        assertFalse(r.isSuccess());
        assertEquals(AtallaResponse.MALFORMED_ERROR_CODE, r.getErrorCode());
        assertEquals(AtallaResponse.MALFORMED_RESPONSE_ID, r.getResponseId());

        AtallaResponse noTerminator = new AtallaResponse(ascii("<20#A>"));
        assertTrue(noTerminator.isMalformed());
        assertFalse(noTerminator.isSuccess());
    }
}
