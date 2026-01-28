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
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.transmission.ASNMapperBaseTransmission;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.BaseTransmission;
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
public class TestASNBaseTransmissionMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        BaseTransmission error = new BaseTransmission(121212L);
        error.setEcho("echo");
        error.setRequestUuid("ReReq");
        error.setServiceTicket("Srv");
        error.setLoggedInUserFromTicket(new User(1L, "user"));

        ASNMapperBaseTransmission mapper = new ASNMapperBaseTransmission(ASNGlobal.Type.FLUID_ITEM);

        byte[] raw = seqBytes(mapper.encode(error));
        BaseTransmission decoded = mapper.decode(raw);
        Assert.assertEquals("Decoded error id is not as expected.", error.getId(), decoded.getId());
        Assert.assertEquals("Decoded error logged in user is not as expected.", error.getLoggedInUserFromTicket().getId(), decoded.getLoggedInUserFromTicket().getId());
        Assert.assertEquals("Decoded error logged in user (username) is not as expected.", error.getLoggedInUserFromTicket().getUsername(), decoded.getLoggedInUserFromTicket().getUsername());
        Assert.assertEquals("Decoded error service ticket is not as expected.", error.getServiceTicket(), decoded.getServiceTicket());
        Assert.assertEquals("Decoded error request id is not as expected.", error.getRequestUuid(), decoded.getRequestUuid());
        Assert.assertEquals("Decoded error echo is not as expected.", error.getEcho(), decoded.getEcho());
    }
}
