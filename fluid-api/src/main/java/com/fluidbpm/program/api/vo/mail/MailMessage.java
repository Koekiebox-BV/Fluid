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

import java.util.List;

import com.fluidbpm.program.api.vo.ABaseFluidVO;

/**
 * Fluid representation of a Email message.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see com.fluidbpm.program.api.vo.Attachment
 * @see MailMessageAttachment
 * @see MailMessageNameValue
 * @see com.fluidbpm.program.api.vo.Attachment
 * @see ABaseFluidVO
 */
public class MailMessage extends ABaseFluidVO {

    public static final long serialVersionUID = 1L;

    private List<MailMessageAttachment> attachments;
    private List<MailMessageNameValue> nameValues;

    private String mailTemplate;
    private String mailSMTPServer;
    private String[] recipients;

    /**
     * Default constructor.
     */
    public MailMessage() {
        super();
    }

    /**
     * Gets a list of Mail Message Attachments.
     *
     * @return Mail Message Attachments.
     */
    public List<MailMessageAttachment> getAttachments() {
        return this.attachments;
    }

    /**
     * Sets a list of Mail Message Attachments.
     *
     * @param attachmentsParam Mail Message Attachments.
     */
    public void setAttachments(List<MailMessageAttachment> attachmentsParam) {
        this.attachments = attachmentsParam;
    }

    /**
     * Gets a list of Mail Message Name Value pairs.
     *
     * @return Mail Name and Values.
     */
    public List<MailMessageNameValue> getNameValues() {
        return this.nameValues;
    }

    /**
     * Sets a list of Mail Message Name Value pairs.
     *
     * @param nameValuesParam Mail Name and Values.
     */
    public void setNameValues(List<MailMessageNameValue> nameValuesParam) {
        this.nameValues = nameValuesParam;
    }

    /**
     * Gets the Mail Template.
     *
     * @return Mail Template name.
     */
    public String getMailTemplate() {
        return this.mailTemplate;
    }

    /**
     * Sets the Mail Template.
     *
     * @param mailTemplateParam Mail Template name.
     */
    public void setMailTemplate(String mailTemplateParam) {
        this.mailTemplate = mailTemplateParam;
    }

    /**
     * Gets the Mail SMTP Server.
     *
     * @return Mail SMTP Server.
     */
    public String getMailSMTPServer() {
        return this.mailSMTPServer;
    }

    /**
     * Sets the Mail SMTP Server.
     *
     * @param mailSMTPServerParam Mail SMTP Server.
     */
    public void setMailSMTPServer(String mailSMTPServerParam) {
        this.mailSMTPServer = mailSMTPServerParam;
    }

    /**
     * Gets the list of Mail recipients.
     *
     * @return EMail addresses.
     */
    public String[] getRecipients() {
        return this.recipients;
    }

    /**
     * Sets the list of Mail recipients.
     *
     * @param recipientsParam EMail addresses.
     */
    public void setRecipients(String[] recipientsParam) {
        this.recipients = recipientsParam;
    }
}
