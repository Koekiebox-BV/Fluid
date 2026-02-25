/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.google.common.io.BaseEncoding;
import org.bouncycastle.asn1.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for validating the functionality of the ASNBaseMapper class.
 * This class verifies both encoding and decoding of {@code Error} objects into and from
 * ASN.1 structures. It also tests the population of base fields from the ASN.1 sequences.
 *
 * <h2>Responsibilities:</h2>
 * - Setup and instantiation of {@code ASNBaseMapper} in the {@code @Before} method.
 * - Testing the initialization of ASN.1 sequences.
 * - Validating ASN.1 sequence encoding and decoding consistency.
 * - Testing base field population from ASN.1 sequences and its accuracy.
 *
 * <h2>Test Scenarios:</h2>
 * 1. {@code testInitVector()}:
 *    - Validates that the ASN.1 sequence initialized from an {@code Error} object
 *      has the expected structure.
 *    - Checks the encoded byte content of the ASN.1 sequence.
 *
 * 2. {@code testPopBaseFields()}:
 *    - Populates base fields of an {@code Error} object from an ASN.1 sequence
 *      and verifies the values post-decoding.
 *    - Asserts that encoded and decoded structures maintain data consistency.
 *
 * This test class extends {@code ABaseTestCase} to utilize shared configuration settings
 * and utility methods for testing purposes.
 */
public class TestASNBaseMapper extends ABaseTestCase {
    private ASNBaseMapper<Error> base;

    @Before
    public void setup() {
        this.base = new ASNBaseMapper<Error>(ASNBaseMapper.InitType.NONE) {
            @Override
            public Error decode(byte[] der) {
                ASN1Sequence seq = this.initSeq(der);
                return this.decode(seq);
            }

            @Override
            public Error decode(ASN1Sequence seq) {
                Error returnVal = new Error();
                this.popBaseFields(returnVal, seq);
                return returnVal;
            }

            @Override
            public DERSequence encode(Error vo) {
                return new DERSequence(initVector(vo));
            }

            @Override
            public void popBaseFields(Error vo, ASN1Sequence seq) {
                super.popBaseFields(vo, seq);
            }
        };
    }

    @Test
    public void testInitVector() throws IOException {
        Error error = new Error();
        ASN1Sequence seq = new DERSequence(this.base.initVector(error));
        byte[] encoded = seq.getEncoded(ASN1Encoding.DER);
        String asciiHex = BaseEncoding.base16().encode(encoded);

        Assert.assertEquals("Expected ASN1Seq!", "30", asciiHex.substring(0,2));
        Assert.assertEquals("Initial vector size is not as expected.", ASNBaseMapper.Map.START, seq.size());
    }

    @Test
    public void testPopBaseFieldsEncodeDecode() throws IOException {
        Error error = new Error();
        User u = new User(3L, "kb");
        error.setLoggedInUserFromTicket(u);
        error.setId(33L);
        String servTicket = UUID.randomUUID().toString();
        String reqId = UUID.randomUUID().toString();
        String echo = "EchO";
        error.setServiceTicket(servTicket);
        error.setRequestUuid(reqId);
        error.setEcho(echo);
        ASN1Sequence seq = new DERSequence(this.base.initVector(error));

        this.base.popBaseFields(error, seq);

        byte[] encoded = seq.getEncoded(ASN1Encoding.DER);
        String asciiHex = BaseEncoding.base16().encode(encoded);

        Assert.assertEquals("Expected ASN1Seq!", "30", asciiHex.substring(0,2));
        Assert.assertEquals("Initial vector size is not as expected.", ASNBaseMapper.Map.START, seq.size());

        Error decoded = this.base.decode(encoded);
        Assert.assertEquals("Decoded error id is not as expected.", error.getId(), decoded.getId());
        Assert.assertEquals("Decoded error logged in user is not as expected.", error.getLoggedInUserFromTicket().getId(), decoded.getLoggedInUserFromTicket().getId());
        Assert.assertEquals("Decoded error logged in user (username) is not as expected.", error.getLoggedInUserFromTicket().getUsername(), decoded.getLoggedInUserFromTicket().getUsername());
        Assert.assertEquals("Decoded error service ticket is not as expected.", error.getServiceTicket(), decoded.getServiceTicket());
        Assert.assertEquals("Decoded error request id is not as expected.", error.getRequestUuid(), decoded.getRequestUuid());
        Assert.assertEquals("Decoded error echo is not as expected.", error.getEcho(), decoded.getEcho());

        byte[] encodedAgain = seqBytes(this.base.encode(decoded));
        String asciiHexEncoded = BaseEncoding.base16().encode(encodedAgain);
        Assert.assertEquals("Encoding and decoding does not match.", asciiHex, asciiHexEncoded);
    }
}
