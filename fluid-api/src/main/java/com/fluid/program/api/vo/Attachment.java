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

package com.fluid.program.api.vo;

import java.io.File;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an Fluid Field for Form, User, Route and Global.
 *
 * {@code Field} can be part of Electronic Form or Form Definition in Fluid.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Form
 * @see FluidItem
 * @see com.fluid.program.api.vo.mail.MailMessage
 * @see com.fluid.program.api.vo.mail.MailMessageAttachment
 */
public class Attachment extends ABaseFluidJSONObject {

    private String version;
    private String name;
    private String path;
    private String contentType;

    private Date dateLastUpdated;
    private Date dateCreated;

    private String attachmentDataBase64;

    /**
     * The JSON mapping for the {@code Attachment} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String PATH = "path";

        public static final String VERSION = "version";
        public static final String CONTENT_TYPE = "contentType";

        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
        public static final String DATE_CREATED = "dateCreated";

        public static final String ATTACHMENT_DATA_BASE64 = "attachmentDataBase64";
    }

    /**
     * Default constructor.
     */
    public Attachment() {
        super();
    }

    /**
     * Sets the Path and Name of {@code this} {@code Attachment}.
     *
     * @param attachmentPath Sets the {@code Attachment} path.
     * @param attachmentNameParam Sets the {@code Attachment} name.
     */
    public Attachment(String attachmentPath, String attachmentNameParam) {
        super();
        this.setPath(attachmentPath);
        this.setName(attachmentNameParam);
    }

    /**
     * Sets the Path {@code this} {@code Attachment}.
     *
     * @param attachmentPath The path to the {@code Attachment}.
     */
    public Attachment(String attachmentPath) {
        super();
        this.setPath(attachmentPath);

        File theFile = new File(attachmentPath);
        if(theFile.exists() && theFile.isFile())
        {
            this.setName(theFile.getName());
        }
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Attachment(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(JSONMapping.NAME));
        }

        //Path...
        if (!this.jsonObject.isNull(JSONMapping.PATH)) {
            this.setPath(this.jsonObject.getString(JSONMapping.PATH));
        }

        //Version...
        if (!this.jsonObject.isNull(JSONMapping.VERSION)) {
            this.setVersion(this.jsonObject.getString(JSONMapping.VERSION));
        }

        //Content Type...
        if (!this.jsonObject.isNull(JSONMapping.CONTENT_TYPE)) {
            this.setContentType(this.jsonObject.getString(JSONMapping.CONTENT_TYPE));
        }

        //Date Created...
        if (!this.jsonObject.isNull(JSONMapping.DATE_CREATED)) {
            this.setDateCreated(new Date(
                    this.jsonObject.getLong(JSONMapping.DATE_CREATED)));
        }

        //Date Last Updated...
        if (!this.jsonObject.isNull(JSONMapping.DATE_LAST_UPDATED)) {
            this.setDateLastUpdated(new Date(
                    this.jsonObject.getLong(JSONMapping.DATE_LAST_UPDATED)));
        }

        //Attachment Data...
        if (!this.jsonObject.isNull(JSONMapping.ATTACHMENT_DATA_BASE64)) {
            this.setAttachmentDataBase64(this.jsonObject.getString(
                    JSONMapping.ATTACHMENT_DATA_BASE64));
        }
    }

    /**
     * Gets the version of {@code this} {@code Attachment}.
     *
     * @return Attachment version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the version of {@code this} {@code Attachment}.
     *
     * @param versionParam Attachment version.
     */
    public void setVersion(String versionParam) {
        this.version = versionParam;
    }

    /**
     * Gets the name of {@code this} {@code Attachment}.
     *
     * @return Attachment name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of {@code this} {@code Attachment}.
     *
     * @param nameParam Attachment name.
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets the path of {@code this} {@code Attachment}.
     *
     * @return Attachment path.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Sets the path of {@code this} {@code Attachment}.
     *
     * @param pathParam Attachment path.
     */
    public void setPath(String pathParam) {
        this.path = pathParam;
    }

    /**
     * Gets the content-type of {@code this} {@code Attachment}.
     *
     * @return Attachment Content / Mime Type.
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Sets the content-type of {@code this} {@code Attachment}.
     *
     * @param contentTypeParam Attachment Content / Mime Type.
     */
    public void setContentType(String contentTypeParam) {
        this.contentType = contentTypeParam;
    }

    /**
     * Gets the {@code Date} when {@code this} {@code Attachment} was last updated.
     *
     * @return Attachment Last Updated Date.
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     * Sets the {@code Date} when {@code this} {@code Attachment} was last updated.
     *
     * @param dateLastUpdatedParam Attachment Last Updated Date.
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     * Gets the {@code Date} when {@code this} {@code Attachment} was created.
     *
     * @return Attachment Created Date.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets the {@code Date} when {@code this} {@code Attachment} was created.
     *
     * @param dateCreatedParam Attachment Created Date.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * <p>
     * The data for {@code this} {@code Attachment} in Base-64 encoding.
     *
     * <p>
     * Find out more at;
     * https://en.wikipedia.org/wiki/Base64
     *
     * @return Attachment Data in Base-64 encoding.
     */
    public String getAttachmentDataBase64() {
        return this.attachmentDataBase64;
    }

    /**
     * <p>
     * The data for {@code this} {@code Attachment} in Base-64 encoding.
     *
     * <p>
     * Find out more at;
     * https://en.wikipedia.org/wiki/Base64
     *
     * @param attachmentDataBase64Param Attachment Data in Base-64 encoding.
     */
    public void setAttachmentDataBase64(String attachmentDataBase64Param) {
        this.attachmentDataBase64 = attachmentDataBase64Param;
    }

    /**
     * Checks to see whether {@code this} {@code Attachment} name contains
     * the value {@code containingTextParam}.
     *
     * @param containingTextParam The text to check for <b>(not case sensitive)</b>.
     * @return Whether the {@code Attachment} name contains {@code containingTextParam}.
     */
    public boolean doesNameContain(String containingTextParam) {
        if (this.getName() == null || this.getName().trim().isEmpty()) {
            return false;
        }

        if (containingTextParam == null || containingTextParam.trim().isEmpty()) {
            return false;
        }

        String paramLower = containingTextParam.toLowerCase();

        String nameLower = this.getName().toLowerCase();

        return nameLower.contains(paramLower);
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Attachment}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Attachment Data...
        if (this.getAttachmentDataBase64() != null) {
            returnVal.put(JSONMapping.ATTACHMENT_DATA_BASE64, this.getAttachmentDataBase64());
        }

        //Name...
        if (this.getName() != null) {
            returnVal.put(JSONMapping.NAME, this.getName());
        }

        //Path...
        if (this.getPath() != null) {
            returnVal.put(JSONMapping.PATH, this.getName());
        }

        //Version...
        if (this.getVersion() != null) {
            returnVal.put(JSONMapping.VERSION, this.getVersion());
        }

        //Date Created...
        if (this.getDateCreated() != null) {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateCreated().getTime());
        }

        //Date Last Updated...
        if (this.getDateLastUpdated() != null) {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED,
                    this.getDateLastUpdated().getTime());
        }

        return returnVal;
    }
}
