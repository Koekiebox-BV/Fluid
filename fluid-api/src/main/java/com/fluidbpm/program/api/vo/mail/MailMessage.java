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
import java.util.List;

/**
 * Fluid representation of a Email message.
 *
 * @author jasonbruwer
 * @see Attachment
 * @see MailMessageAttachment
 * @see MailMessageNameValue
 * @see ABaseFluidVO
 * @since v1.0
 */
@Getter
@Setter
public class MailMessage extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private List<MailMessageAttachment> attachments;
    private List<MailMessageNameValue> nameValues;
    private String[] recipients;

    private String mailTemplate;
    private String mailSMTPServer;

    /**
     * The JSON mapping for the {@code MailMessage} object.
     */
    public static class JSONMapping {
        public static final String ATTACHMENTS = "attachments";
        public static final String NAME_VALUES = "nameValues";

        public static final String MAIL_TEMPLATE = "mailTemplate";
        public static final String MAIL_SMTP_SERVER = "mailSMTPServer";
        public static final String RECIPIENTS = "recipients";
    }

    /**
     * Default constructor.
     */
    public MailMessage() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public MailMessage(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setMailTemplate(this.getAsStringNullSafe(JSONMapping.MAIL_TEMPLATE));
        this.setMailSMTPServer(this.getAsStringNullSafe(JSONMapping.MAIL_SMTP_SERVER));
        this.setRecipients(this.extractStrings(JSONMapping.RECIPIENTS).toArray(new String[0]));
        this.setAttachments(this.extractObjects(JSONMapping.ATTACHMENTS, MailMessageAttachment::new));
        this.setNameValues(this.extractObjects(JSONMapping.NAME_VALUES, MailMessageNameValue::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code MailMessage}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.MAIL_TEMPLATE, returnVal, this.getMailTemplate());
        this.setAsProperty(JSONMapping.MAIL_SMTP_SERVER, returnVal, this.getMailSMTPServer());
        this.setAsStringArray(JSONMapping.RECIPIENTS, returnVal, this.getRecipients());
        this.setAsObjArray(JSONMapping.ATTACHMENTS, returnVal, this::getAttachments);
        this.setAsObjArray(JSONMapping.NAME_VALUES, returnVal, this::getNameValues);
        return returnVal;
    }
}
