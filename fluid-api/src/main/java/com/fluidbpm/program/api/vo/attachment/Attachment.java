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

package com.fluidbpm.program.api.vo.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.Date;

/**
 * Represents an Fluid Field for Form, User, Route and Global.
 * <p>
 * {@code Field} can be part of Electronic Form or Form Definition in Fluid.
 *
 * @author jasonbruwer
 * @see Form
 * @see FluidItem
 * @see com.fluidbpm.program.api.vo.mail.MailMessage
 * @see com.fluidbpm.program.api.vo.mail.MailMessageAttachment
 * @since v1.0
 */
@Getter
@Setter
public class Attachment extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private String version;
    private String name;
    private String path;
    private String contentType;

    private Date dateLastUpdated;
    private Date dateCreated;

    private String attachmentDataBase64;
    private Long formId;

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

        public static final String FORM_ID = "formId";
        public static final String ATTACHMENT_DATA_BASE64 = "attachmentDataBase64";
    }

    /**
     * Default constructor.
     */
    public Attachment() {
        super();
    }

    /**
     * Sets the Id associated with the Attachment.
     *
     * @param attachmentIdParam Attachment Id.
     */
    public Attachment(Long attachmentIdParam) {
        super();
        this.setId(attachmentIdParam);
    }

    /**
     * Sets the Path and Name of {@code this} {@code Attachment}.
     *
     * @param attachmentPath      Sets the {@code Attachment} path.
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
        if (theFile.exists() && theFile.isFile()) {
            this.setName(theFile.getName());
        }
    }

    /**
     * Create a clone from {@code toClone}
     *
     * @param toClone
     */
    private Attachment(Attachment toClone) {
        if (toClone == null) return;
        this.setAttachmentDataBase64(toClone.getAttachmentDataBase64());
        this.setContentType(toClone.getContentType());
        this.setDateCreated(toClone.getDateCreated() == null ? null : new Date(toClone.getDateCreated().getTime()));
        this.setDateLastUpdated(toClone.getDateLastUpdated() == null ? null : new Date(toClone.getDateLastUpdated().getTime()));
        this.setFormId(toClone.getFormId());
        this.setName(toClone.getName());
        this.setPath(toClone.getPath());
        this.setVersion(toClone.getVersion());
        this.setId(toClone.getId());
        this.setEcho(toClone.getEcho());
        this.setServiceTicket(toClone.getServiceTicket());
        this.setRequestUuid(toClone.getRequestUuid());
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Attachment(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(this.jsonObject, JSONMapping.NAME));
        this.setPath(this.getAsStringNullSafe(this.jsonObject, JSONMapping.PATH));
        this.setVersion(this.getAsStringNullSafe(this.jsonObject, JSONMapping.VERSION));
        this.setContentType(this.getAsStringNullSafe(this.jsonObject, JSONMapping.CONTENT_TYPE));
        this.setFormId(this.getAsLongNullSafe(this.jsonObject, JSONMapping.FORM_ID));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
        this.setAttachmentDataBase64(this.getAsStringNullSafe(this.jsonObject, JSONMapping.ATTACHMENT_DATA_BASE64));
    }

    /**
     * Checks to see whether {@code this} {@code Attachment} name contains
     * the value {@code containingTextParam}.
     *
     * @param containingTextParam The text to check for <b>(not case sensitive)</b>.
     * @return Whether the {@code Attachment} name contains {@code containingTextParam}.
     */
    @XmlTransient
    @JsonIgnore
    public boolean doesNameContain(String containingTextParam) {
        if (this.getName() == null || this.getName().trim().isEmpty()) return false;

        if (containingTextParam == null || containingTextParam.trim().isEmpty()) return false;

        String paramLower = containingTextParam.toLowerCase();
        String nameLower = this.getName().toLowerCase();

        return nameLower.contains(paramLower);
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Attachment}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @JsonIgnore
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        //Attachment Data...
        if (this.getAttachmentDataBase64() != null) {
            returnVal.addProperty(JSONMapping.ATTACHMENT_DATA_BASE64, this.getAttachmentDataBase64());
        }

        //Content Type...
        if (this.getContentType() != null) {
            returnVal.addProperty(JSONMapping.CONTENT_TYPE, this.getContentType());
        }

        //Form Id...
        if (this.getFormId() != null) {
            returnVal.addProperty(JSONMapping.FORM_ID, this.getFormId());
        }

        //Name...
        if (this.getName() != null) {
            returnVal.addProperty(JSONMapping.NAME, this.getName());
        }

        //Path...
        if (this.getPath() != null) {
            returnVal.addProperty(JSONMapping.PATH, this.getPath());
        }

        //Version...
        if (this.getVersion() != null) {
            returnVal.addProperty(JSONMapping.VERSION, this.getVersion());
        }

        //Date Created...
        if (this.getDateCreated() != null) {
            returnVal.addProperty(
                    JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated())
            );
        }

        //Date Last Updated...
        if (this.getDateLastUpdated() != null) {
            returnVal.addProperty(
                    JSONMapping.DATE_LAST_UPDATED,
                    this.getDateAsLongFromJson(this.getDateLastUpdated())
            );
        }
        return returnVal;
    }

    /**
     * Clone {@code this} object.
     *
     * @return cloned instance of {@code Attachment}.
     */
    @Override
    @JsonIgnore
    public Attachment clone() {
        return new Attachment(this);
    }

    /**
     * Verifies whether attachment file type is {@code image/*}.
     *
     * @return {@code true} if content type is image.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFileTypeImage() {
        if (this.getContentType() == null || this.getContentType().trim().isEmpty()) return false;
        return (this.getContentType().trim().toLowerCase().startsWith("image"));
    }

    /**
     * Verifies whether attachment type is {@code application/pdf}.
     *
     * @return {@code true} if content type is PDF.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFileTypePDF() {
        return "application/pdf".equals(this.getContentType().trim().toLowerCase());
    }

    /**
     * Verifies whether attachment type is Microsoft MS Word.
     *
     * @return {@code true} if content type is Microsoft Word Document.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFileTypeMSWord() {
        String contentTypeLower = this.getContentType() == null ? null : this.getContentType().trim().toLowerCase();
        if (UtilGlobal.isBlank(contentTypeLower)) return false;
        switch (contentTypeLower) {
            case "application/msword":
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
                return true;
            default:
                return false;
        }
    }

    /**
     * Verifies whether attachment type is Microsoft MS Word.
     *
     * @return {@code true} if content type is Microsoft Word Document.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFileTypeMSExcel() {
        String contentTypeLower = this.getContentType() == null ? null : this.getContentType().trim().toLowerCase();
        if (UtilGlobal.isBlank(contentTypeLower)) return false;
        switch (contentTypeLower) {
            case "application/vnd.ms-excel":
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
                return true;
            default:
                return false;
        }
    }

    /**
     * Verifies whether attachment type is {@code application/json}.
     *
     * @return {@code true} if content type is JSON.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isFileTypeJSON() {
        return "application/json".equals(this.getContentType().trim().toLowerCase());
    }

    /**
     * Friendly name for content-type.
     *
     * @return String - The user friendly name for the content type.
     */
    @XmlTransient
    @JsonIgnore
    public String getContentTypeFriendly() {
        if (this.isFileTypeImage()) return "Image";
        if (this.isFileTypePDF()) return "PDF";
        if (this.isFileTypeMSWord()) return "Office Word";
        if (this.isFileTypeJSON()) return "JSON";
        if (this.isFileTypeMSExcel()) return "Office Excel";

        return this.getContentType();
    }

    /**
     * Convert the Base64 encoded attachment data to binary.
     *
     * @return {@code byte[]}
     */
    @XmlTransient
    @JsonIgnore
    public byte[] getAttachmentDataRAW() {
        if (UtilGlobal.isBlank(this.getAttachmentDataBase64())) return null;
        return BaseEncoding.base64().decode(this.getAttachmentDataBase64());
    }

    /**
     * @return Extension from filename.
     */
    @XmlTransient
    @JsonIgnore
    public String getExtensionFromFilename() {
        if (this.getName() == null) return null;
        int indexOf = this.getName().lastIndexOf(".");
        if (indexOf > -1) return this.getName().substring(indexOf);
        return null;
    }
}
