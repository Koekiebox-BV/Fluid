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

import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import static com.fluidbpm.ws.client.v1.asn1der.ASNBaseMapper.seqBytes;

/**
 * Test class for verifying the encoding and decoding functionality of the
 * {@link ASNMapperFluidItemCustomProperty} class using the FluidItem.FluidItemProperty model.
 * This test ensures that the encoding and subsequent decoding of custom properties
 * preserve the original data accurately and without changes.
 *
 * The test operates in the following manner:
 * 1. Creates an instance of {@link FluidItem.FluidItemProperty} and assigns test values to its properties.
 * 2. Uses the {@link ASNMapperFluidItemCustomProperty} to encode the FluidItem property into a byte array.
 * 3. Decodes the byte array back into a {@link FluidItem.FluidItemProperty} instance.
 * 4. Validates that the encoded and then decoded object retains the same properties as the original.
 *
 * Assertions are employed to ensure:
 * - The property name of the decoded object matches that of the original object.
 * - The property value of the decoded object matches that of the original object.
 *
 * This test class extends {@link ABaseTestCase} for common configuration and utility setup
 * required by Fluid WS-related test cases.
 *
 * Dependencies:
 * - {@link ASNMapperFluidItemCustomProperty} for encoding and decoding operations.
 * - {@link Assert} for test case validations.
 */
public class TestASNFluidItemCustomPropertyMapper extends ABaseTestCase {

    @Test
    public void testEncodeDecode() {
        FluidItem.FluidItemProperty prop = new FluidItem.FluidItemProperty();
        prop.setName("i am a name");
        prop.setValue("i am a value");

        ASNMapperFluidItemCustomProperty mapper = new ASNMapperFluidItemCustomProperty();

        byte[] raw = seqBytes(mapper.encode(prop));
        FluidItem.FluidItemProperty decoded = mapper.decode(raw);

        Assert.assertEquals("Decoded prop name is not as expected.", prop.getName(), decoded.getName());
        Assert.assertEquals("Decoded prop value is not as expected.", prop.getValue(), decoded.getValue());
    }
}
