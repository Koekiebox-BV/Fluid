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

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Attachment extends ABaseFluidJSONObject {

    private String version;
    private String name;
    private String path;
    private String contentType;

    private String attachmentDataBase64;

    /**
     *
     */
    public static class JSONMapping {
        public static final String VERSION = "version";
        public static final String NAME = "name";
        public static final String PATH = "path";
        public static final String CONTENT_TYPE = "contentType";
        public static final String ATTACHMENT_DATA_BASE64 = "attachmentDataBase64";
    }

    public Attachment() {
        super();
    }

    /**
     *
     * @param attachmentPath
     * @param attachmentNameParam
     */
    public Attachment(String attachmentPath, String attachmentNameParam) {
        super();
        this.setPath(attachmentPath);
        this.setName(attachmentNameParam);
    }

    /**
     *
     * @param attachmentPath
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
     *
     * @return
     */
    public String getVersion() {
        return this.version;
    }

    /**
     *
     * @param versionParam
     */
    public void setVersion(String versionParam) {
        this.version = versionParam;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param nameParam
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     *
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     *
     * @param pathParam
     */
    public void setPath(String pathParam) {
        this.path = pathParam;
    }

    /**
     *
     * @return
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     *
     * @param contentTypeParam
     */
    public void setContentType(String contentTypeParam) {
        this.contentType = contentTypeParam;
    }

    /**
     *
     * @return
     */
    public String getAttachmentDataBase64() {
        return this.attachmentDataBase64;
    }

    /**
     *
     * @param attachmentDataBase64Param
     */
    public void setAttachmentDataBase64(String attachmentDataBase64Param) {
        this.attachmentDataBase64 = attachmentDataBase64Param;
    }

    /**
     *
     * @param containingTextParam
     * @return
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
     *
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Attachment Data...
        if (this.getAttachmentDataBase64() != null) {
            returnVal.put(JSONMapping.ATTACHMENT_DATA_BASE64, this.getAttachmentDataBase64());
        }

        //Content Type...
        if (this.getContentType() != null) {
            returnVal.put(JSONMapping.CONTENT_TYPE, this.getContentType());
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

        return returnVal;
    }
}
