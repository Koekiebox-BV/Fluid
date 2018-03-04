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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.attachment.Attachment;

/**
 * Fluid representation of a Email message.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Attachment
 * @see MailMessageAttachment
 * @see MailMessageNameValue
 * @see Attachment
 * @see ABaseFluidVO
 */
public class MailMessage extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private List<MailMessageAttachment> attachments;
    private List<MailMessageNameValue> nameValues;
    private String[] recipients;
    
    private String mailTemplate;
    private String mailSMTPServer;

    /**
     * The JSON mapping for the {@code MailMessage} object.
     */
    public static class JSONMapping
    {
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
    public MailMessage(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Mail Template...
        if (!this.jsonObject.isNull(JSONMapping.MAIL_TEMPLATE)) {

            this.setMailTemplate(this.jsonObject.getString(
                    JSONMapping.MAIL_TEMPLATE));
        }

        //SMTP Server...
        if (!this.jsonObject.isNull(JSONMapping.MAIL_SMTP_SERVER)) {

            this.setMailSMTPServer(this.jsonObject.getString(
                    JSONMapping.MAIL_SMTP_SERVER));
        }

        //Recipients...
        if (!this.jsonObject.isNull(JSONMapping.RECIPIENTS)) {

            JSONArray fluidItemsArr = this.jsonObject.getJSONArray(
                            JSONMapping.RECIPIENTS);

            String[] listOfItems = new String[fluidItemsArr.length()];
            for(int index = 0;index < fluidItemsArr.length();index++)
            {
                listOfItems[index] = fluidItemsArr.getString(index);
            }

            this.setRecipients(listOfItems);
        }

        //Attachments...
        if (!this.jsonObject.isNull(JSONMapping.ATTACHMENTS)) {

            JSONArray fluidItemsArr = this.jsonObject.getJSONArray(
                    JSONMapping.ATTACHMENTS);

            List<MailMessageAttachment> listOfItems = new ArrayList();
            for(int index = 0;index < fluidItemsArr.length();index++)
            {
                listOfItems.add(new MailMessageAttachment(
                        fluidItemsArr.getJSONObject(index)));
            }

            this.setAttachments(listOfItems);
        }

        //Name Values...
        if (!this.jsonObject.isNull(JSONMapping.NAME_VALUES)) {

            JSONArray fluidItemsArr = this.jsonObject.getJSONArray(
                    JSONMapping.NAME_VALUES);

            List<MailMessageNameValue> listOfItems = new ArrayList();
            for(int index = 0;index < fluidItemsArr.length();index++)
            {
                listOfItems.add(new MailMessageNameValue(
                        fluidItemsArr.getJSONObject(index)));
            }

            this.setNameValues(listOfItems);
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MailMessage}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Mail Template...
        if(this.getMailTemplate() != null)
        {
            returnVal.put(JSONMapping.MAIL_TEMPLATE, this.getMailTemplate());
        }

        //Mail SMTP Server...
        if(this.getMailSMTPServer() != null)
        {
            returnVal.put(JSONMapping.MAIL_SMTP_SERVER, this.getMailSMTPServer());
        }

        //Recipients...
        if(this.getRecipients() != null && this.getRecipients().length > 0)
        {
            JSONArray jsonArray = new JSONArray();

            for(String item : this.getRecipients())
            {
                jsonArray.put(item);
            }

            returnVal.put(JSONMapping.RECIPIENTS, jsonArray);
        }

        //Attachments...
        if(this.getAttachments() != null && !this.getAttachments().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(MailMessageAttachment item : this.getAttachments())
            {
                jsonArray.put(item.toJsonObject());
            }

            returnVal.put(JSONMapping.ATTACHMENTS, jsonArray);
        }

        //Name Values...
        if(this.getNameValues() != null &&
                !this.getNameValues().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(MailMessageNameValue item : this.getNameValues())
            {
                jsonArray.put(item.toJsonObject());
            }

            returnVal.put(JSONMapping.NAME_VALUES, jsonArray);
        }

        return returnVal;
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
