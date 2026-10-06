package com.fluidbpm.ws.client.v1.netty.hsm.thales;

import com.fluidbpm.ws.client.v1.netty.hsm.common.HsmProtocol;
import io.netty.channel.ChannelHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * payShield host-command protocol for {@link com.fluidbpm.ws.client.v1.netty.hsm.common.AbstractHsmClient}:
 * 2-byte binary length framing and correlation by the site-configured message header, which
 * the HSM echoes back unmodified. A header is mandatory (no FIFO matching).
 *
 * @author jasonbruwer
 * @since 1.15
 */
final class ThalesProtocol implements HsmProtocol<ThalesCommand, ThalesResponse> {

    private final int headerLength;

    ThalesProtocol(int headerLength) {
        this.headerLength = headerLength;
    }

    @Override
    public String displayName() {
        return "Thales HSM";
    }

    @Override
    public Class<ThalesResponse> responseType() {
        return ThalesResponse.class;
    }

    @Override
    public List<ChannelHandler> newCodecHandlers() {
        return Arrays.<ChannelHandler>asList(
                // Inbound: strips 2-byte binary length prefix
                new ThalesFrameDecoder(),
                // Outbound: adds 2-byte binary length prefix
                new ThalesFrameEncoder(),
                // Inbound: raw bytes to ThalesResponse (header split off)
                new ThalesResponseDecoder(headerLength),
                // Outbound: ThalesCommand to header + code + data bytes
                new ThalesCommandEncoder()
        );
    }

    @Override
    public boolean requiresRequestId() {
        return true;
    }

    @Override
    public void validateRequestId(String requestId) {
        if (requestId.length() != headerLength) {
            throw new IllegalArgumentException("Request id '" + requestId + "' must be "
                    + headerLength + " characters to be used as the message header");
        }
    }

    /** Upper-case hex sequence, zero-padded/truncated to the header length. */
    @Override
    public String nextRequestId(int sequence) {
        String hex = Integer.toHexString(sequence).toUpperCase(Locale.ROOT);
        if (hex.length() >= headerLength) {
            return hex.substring(hex.length() - headerLength);
        }
        StringBuilder sb = new StringBuilder(headerLength);
        for (int i = hex.length(); i < headerLength; i++) sb.append('0');
        return sb.append(hex).toString();
    }

    @Override
    public void assignRequestId(ThalesCommand command, String requestId) {
        command.assignRequestId(requestId);
    }
}
