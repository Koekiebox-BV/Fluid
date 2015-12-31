package com.fluid.program.api.vo.mail;

import java.util.List;

import com.fluid.program.api.vo.ABaseFluidVO;

/**
 * Created by jasonbruwer on 15/05/20.
 */
public class MailMessage extends ABaseFluidVO {

    private List<MailMessageAttachment> attachments;
    private List<MailMessageNameValue> nameValues;

    private String mailTemplate;
    private String mailSMTPServer;
    private String[] recipients;

    /**
     *
     */
    public MailMessage() {
        super();
    }

    /**
     *
     * @return
     */
    public List<MailMessageAttachment> getAttachments() {
        return this.attachments;
    }

    /**
     *
     * @param attachmentsParam
     */
    public void setAttachments(List<MailMessageAttachment> attachmentsParam) {
        this.attachments = attachmentsParam;
    }

    /**
     *
     * @return
     */
    public List<MailMessageNameValue> getNameValues() {
        return this.nameValues;
    }

    /**
     *
     * @param nameValuesParam
     */
    public void setNameValues(List<MailMessageNameValue> nameValuesParam) {
        this.nameValues = nameValuesParam;
    }

    /**
     *
     * @return
     */
    public String getMailTemplate() {
        return this.mailTemplate;
    }

    /**
     *
     * @param mailTemplateParam
     */
    public void setMailTemplate(String mailTemplateParam) {
        this.mailTemplate = mailTemplateParam;
    }

    /**
     *
     * @return
     */
    public String getMailSMTPServer() {
        return this.mailSMTPServer;
    }

    /**
     *
     * @param mailSMTPServerParam
     */
    public void setMailSMTPServer(String mailSMTPServerParam) {
        this.mailSMTPServer = mailSMTPServerParam;
    }

    /**
     *
     * @return
     */
    public String[] getRecipients() {
        return this.recipients;
    }

    /**
     *
     * @param recipientsParam
     */
    public void setRecipients(String[] recipientsParam) {
        this.recipients = recipientsParam;
    }
}
