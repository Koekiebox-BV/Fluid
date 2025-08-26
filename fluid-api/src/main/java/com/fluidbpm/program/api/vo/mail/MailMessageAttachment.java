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

package com.fluidbpm.program.api.vo.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Fluid representation of a Email message attachment.
 *
 * @author jasonbruwer
 * @see Attachment
 * @see MailMessage
 * @see MailMessageNameValue
 * @see Attachment
 * @see ABaseFluidVO
 * @since v1.0
 */
@Getter
@Setter
public class MailMessageAttachment extends ABaseFluidGSONObject {

    private static final long serialVersionUID = 1L;

    private String attachmentPath;
    private String attachmentDataBase64;

    /**
     * The JSON mapping for the {@code MailMessageAttachment} object.
     */
    public static class JSONMapping {
        public static final String ATTACHMENT_PATH = "attachmentPath";
        public static final String ATTACHMENT_DATA_BASE64 = "attachmentDataBase64";
    }

    /**
     * Default constructor.
     */
    public MailMessageAttachment() {
        super();
    }

    /**
     * Sets the path to the attachment.
     *
     * @param attachmentPathParam Path to the attachment.
     */
    public MailMessageAttachment(String attachmentPathParam) {
        super();
        this.setAttachmentPath(attachmentPathParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public MailMessageAttachment(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setAttachmentPath(this.getAsStringNullSafe(JSONMapping.ATTACHMENT_PATH));
        this.setAttachmentDataBase64(this.getAsStringNullSafe(JSONMapping.ATTACHMENT_DATA_BASE64));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code MailMessageAttachment}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.ATTACHMENT_PATH, returnVal, this.getAttachmentPath());
        this.setAsProperty(JSONMapping.ATTACHMENT_DATA_BASE64, returnVal, this.getAttachmentDataBase64());

        return returnVal;
    }
}
