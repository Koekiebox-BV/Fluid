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

package com.fluidbpm.program.api.vo.thirdpartylib;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * 3rd Party library uploaded through the Fluid core.
 *
 * @author jasonbruwer on 2017-03-10
 * @see ABaseFluidGSONObject
 * @since v1.4
 */
@NoArgsConstructor
@Setter
@Getter
public class ThirdPartyLibrary extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String filename;
    private String sha256sum;
    private String description;
    private String libraryDataBase64;
    private Boolean addToolsToClassPath;

    private Date dateCreated;
    private Date dateLastUpdated;

    /**
     * The JSON mapping for the {@code ThirdPartyLibrary} object.
     */
    public static class JSONMapping {
        public static final String FILENAME = "filename";
        public static final String DESCRIPTION = "description";
        public static final String SHA_256_SUM = "sha256sum";
        public static final String ADD_TOOLS_TO_CLASS_PATH = "addToolsToClassPath";
        public static final String LIBRARY_DATA_BASE64 = "libraryDataBase64";

        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
    }

    /**
     * Sets the Id associated with a Third party library.
     *
     * @param thirdPartyLibIdParam Third Party Library Id.
     */
    public ThirdPartyLibrary(Long thirdPartyLibIdParam) {
        super();
        this.setId(thirdPartyLibIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ThirdPartyLibrary(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        //Filename...
        this.setFilename(this.getAsStringNullSafe(JSONMapping.FILENAME));

        //Description...
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));

        //Sha-256...
        this.setSha256sum(this.getAsStringNullSafe(JSONMapping.SHA_256_SUM));

        //Add to classpath...
        this.setAddToolsToClassPath(this.getAsBooleanNullSafe(JSONMapping.ADD_TOOLS_TO_CLASS_PATH));

        //Data Base-64...
        this.setLibraryDataBase64(this.getAsStringNullSafe(JSONMapping.LIBRARY_DATA_BASE64));

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_CREATED));

        //Date Last Updated...
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_LAST_UPDATED));
    }

    /**
     * Gets if tools should be added to classpath.
     *
     * @return A {@code Sha-256} of the data in {@code Hex}.
     */
    public Boolean isAddToolsToClassPath() {
        return this.addToolsToClassPath;
    }

    /**
     * Set the library data.
     *
     * @param libraryData Lib data to convert to B64 and set.
     */
    public void setLibraryData(byte[] libraryData) {
        if (libraryData == null || libraryData.length == 0) return;

        this.setLibraryDataBase64(BaseEncoding.base64().encode(libraryData));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code ThirdPartyLibrary}
     * 
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.FILENAME, returnVal, this.getFilename());
        this.setAsProperty(JSONMapping.DESCRIPTION, returnVal, this.getDescription());
        this.setAsProperty(JSONMapping.SHA_256_SUM, returnVal, this.getSha256sum());
        this.setAsProperty(JSONMapping.ADD_TOOLS_TO_CLASS_PATH, returnVal, this.isAddToolsToClassPath());
        this.setAsProperty(JSONMapping.LIBRARY_DATA_BASE64, returnVal, this.getLibraryDataBase64());
        this.setAsProperty(JSONMapping.DATE_CREATED, returnVal, this.getDateCreated());
        this.setAsProperty(JSONMapping.DATE_LAST_UPDATED, returnVal, this.getDateLastUpdated());

        return returnVal;
    }
}
