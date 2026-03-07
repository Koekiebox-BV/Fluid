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

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 *
 */
public class TestASNMapperFactory extends ABaseTestCase {

    @Test
    public void testFluidItem() {
        ASNMapperFactory factory = new ASNMapperFactory(ASNGlobal.Type.FLUID_ITEM);

        Form form = new Form("Form Type", "ThisIsTitle");
        form.setFieldValue("Name", "Jackie", Field.Type.Text);
        FluidItem itm = new FluidItem(form);

        itm.setAttachments(new ArrayList<>());
        byte[] attBytes = "Hello world!".getBytes();
        itm.getAttachments().add(new Attachment(attBytes, "hello.txt", "text/plain"));

        byte[] raw = factory.writeObjectForSend(itm);
        Assert.assertNotNull(raw);
    }
}
