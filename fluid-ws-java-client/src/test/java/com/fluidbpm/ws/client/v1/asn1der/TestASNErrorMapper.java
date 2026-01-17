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

import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * This class is a JUnit test case that validates the encode and decode functionality
 * of the {@code ASNMapperError} class, specifically for the {@code Error} object.
 * The test ensures that the encoded data can be accurately decoded back to its
 * original form, preserving the integrity of the {@code Error} object's fields.
 *
 * It inherits from the {@code ABaseTestCase}, which provides base functionality
 * for web service-related tests.
 */
public class TestASNErrorMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        Error error = new Error();
        error.setErrorCode(123);
        error.setErrorMessage("This is an error message");

        ASNMapperError mapper = new ASNMapperError();

        byte[] raw = seqBytes(mapper.encode(error));
        Error decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded error code is not as expected.", error.getErrorCode(), decoded.getErrorCode());
        Assert.assertEquals("Decoded error message in user is not as expected.", error.getErrorMessage(), decoded.getErrorMessage());
    }
}
