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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.attachment.Attachment;

/**
 * Fluid representation of a Email message attachment.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Attachment
 * @see MailMessage
 * @see MailMessageNameValue
 * @see Attachment
 * @see ABaseFluidVO
 */
public class MailMessageAttachment extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String attachmentPath;
    private String attachmentDataBase64;

    /**
     * The JSON mapping for the {@code MailMessageAttachment} object.
     */
    public static class JSONMapping
    {
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
    public MailMessageAttachment(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if (this.jsonObject == null) {
            return;
        }

        //Attachment Path...
        if (!this.jsonObject.isNull(
                JSONMapping.ATTACHMENT_PATH)) {

            this.setAttachmentPath(this.jsonObject.getString(
                    JSONMapping.ATTACHMENT_PATH));
        }

        //Attachment Data-64...
        if (!this.jsonObject.isNull(
                JSONMapping.ATTACHMENT_DATA_BASE64)) {

            this.setAttachmentDataBase64(this.jsonObject.getString(
                    JSONMapping.ATTACHMENT_DATA_BASE64));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MailMessageAttachment}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Attachment Path...
        if (this.getAttachmentPath() != null)
        {
            returnVal.put(JSONMapping.ATTACHMENT_PATH,
                    this.getAttachmentPath());
        }

        //Attachment Data Base64...
        if (this.getAttachmentDataBase64() != null)
        {
            returnVal.put(JSONMapping.ATTACHMENT_DATA_BASE64,
                    this.getAttachmentDataBase64());
        }

        return returnVal;
    }
    
    /**
     * Gets the path to the attachment.
     *
     * @return Path to the attachment.
     */
    public String getAttachmentPath() {
        return this.attachmentPath;
    }

    /**
     * Sets the path to the attachment.
     *
     * @param attachmentPathParam Path to the attachment.
     */
    public void setAttachmentPath(String attachmentPathParam) {
        this.attachmentPath = attachmentPathParam;
    }

    /**
     * Gets the attachment data in {@code Base-64} format.
     *
     * @return Base-64 attachment content.
     */
    public String getAttachmentDataBase64() {
        return this.attachmentDataBase64;
    }

    /**
     * Sets the attachment data in {@code Base-64} format.
     *
     * @param attachmentDataBase64Param Base-64 attachment content.
     */
    public void setAttachmentDataBase64(String attachmentDataBase64Param) {
        this.attachmentDataBase64 = attachmentDataBase64Param;
    }
}
