package com.fluidbpm.ws.client.v1.netty.hsm.atalla;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmProtocol;
import io.netty.channel.ChannelHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * AT1000 host-command protocol for {@link com.fluidbpm.ws.client.v1.netty.hsm.common.AbstractHsmClient}:
 * {@code <id#field#...#>} text framing and correlation by the optional context tag
 * ({@code ^tag}) that the HSM returns unmodified. When context tags are disabled, untagged
 * commands are matched in send order.
 *
 * @author jasonbruwer
 * @since 1.15
 */
final class AtallaProtocol implements HsmProtocol<AtallaCommand, AtallaResponse> {

    private static final int TAG_LENGTH = 4;

    private final boolean contextTagEnabled;
    private final int maxResponseLength;

    AtallaProtocol(boolean contextTagEnabled, int maxResponseLength) {
        this.contextTagEnabled = contextTagEnabled;
        this.maxResponseLength = maxResponseLength;
    }

    @Override
    public String displayName() {
        return "Atalla HSM";
    }

    @Override
    public Class<AtallaResponse> responseType() {
        return AtallaResponse.class;
    }

    @Override
    public List<ChannelHandler> newCodecHandlers() {
        return Arrays.<ChannelHandler>asList(
                // Inbound: isolates one <...> message per frame, dropping the optional CRLF trailer
                new AtallaFrameDecoder(maxResponseLength),
                // Inbound: converts a frame to an AtallaResponse (fields and context tag split out)
                new AtallaResponseDecoder(),
                // Outbound: converts AtallaCommand objects to raw bytes (no length prefix)
                new AtallaCommandEncoder()
        );
    }

    @Override
    public boolean requiresRequestId() {
        return contextTagEnabled;
    }

    @Override
    public void validateRequestId(String requestId) {
        AtallaCommand.validateText("Context tag", requestId);
    }

    /** Upper-case hex sequence, zero-padded to {@value #TAG_LENGTH} characters, wrapping at 0xFFFF. */
    @Override
    public String nextRequestId(int sequence) {
        String hex = Integer.toHexString(sequence & 0xFFFF).toUpperCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(TAG_LENGTH);
        for (int i = hex.length(); i < TAG_LENGTH; i++) sb.append('0');
        return sb.append(hex).toString();
    }

    @Override
    public void assignRequestId(AtallaCommand command, String requestId) {
        command.assignContextTag(requestId);
    }
}
