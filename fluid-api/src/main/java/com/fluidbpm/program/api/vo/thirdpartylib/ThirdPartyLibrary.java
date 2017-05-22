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

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * 3rd Party library uploaded through the Fluid core.
 *
 * @author jasonbruwer on 2017-03-10
 * @since v1.4
 *
 * @see ABaseFluidJSONObject
 */
public class ThirdPartyLibrary extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String filename;
    private String sha256sum;
    private String description;
    private String libraryDataBase64;
    private Boolean addToolsToClassPath;

    private Date dateCreated;
    private Date dateLastUpdated;

    /**
     * The JSON mapping for the {@code Field} object.
     */
    public static class JSONMapping
    {
        public static final String FILENAME= "filename";
        public static final String DESCRIPTION = "description";
        public static final String SHA_256_SUM = "sha256sum";
        public static final String ADD_TOOLS_TO_CLASS_PATH = "addToolsToClassPath";
        public static final String LIBRARY_DATA_BASE64= "libraryDataBase64";

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
    public ThirdPartyLibrary(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        //Filename...
        if (!this.jsonObject.isNull(JSONMapping.FILENAME)) {
            this.setFilename(this.jsonObject.getString(JSONMapping.FILENAME));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
        }

        //Sha-256...
        if (!this.jsonObject.isNull(JSONMapping.SHA_256_SUM)) {
            this.setSha256sum(this.jsonObject.getString(JSONMapping.SHA_256_SUM));
        }

        //Add to classpath...
        if (!this.jsonObject.isNull(JSONMapping.ADD_TOOLS_TO_CLASS_PATH)) {
            this.setAddToolsToClassPath(
                    this.jsonObject.getBoolean(JSONMapping.ADD_TOOLS_TO_CLASS_PATH));
        }
        
        //Data Base-64...
        if (!this.jsonObject.isNull(JSONMapping.LIBRARY_DATA_BASE64)) {
            this.setLibraryDataBase64(this.jsonObject.getString(JSONMapping.LIBRARY_DATA_BASE64));
        }

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_CREATED));

        //Date Last Updated...
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
                JSONMapping.DATE_LAST_UPDATED));
    }

    /**
	 * Default constructor.
	 */
    public ThirdPartyLibrary() {
        super();
    }

    /**
     * Gets the name of {@code this} {@code 3rd Party Library}.
     *
     * @return The Field Name.
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Sets the name of {@code this} {@code 3rd Party Library}.
     *
     * @param filenameParam The Library Filename.
     */
    public void setFilename(String filenameParam) {
        this.filename = filenameParam;
    }

    /**
     * Gets {@code ThirdPartyLibrary} description.
     *
     * @return A {@code ThirdPartyLibrary}s description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets {@code ThirdPartyLibrary} description.
     *
     * @param descriptionParam A {@code ThirdPartyLibrary}s description.
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
    }

    /**
     * Gets {@code ThirdPartyLibrary} data in {@code Base-64}.
     *
     * @return A {@code ThirdPartyLibrary}s Base-64.
     */
    public String getLibraryDataBase64() {
        return this.libraryDataBase64;
    }

    /**
     * Sets {@code ThirdPartyLibrary} data in {@code Base-64}.
     *
     * @param libraryDataBase64Param A {@code ThirdPartyLibrary}s Base-64.
     */
    public void setLibraryDataBase64(String libraryDataBase64Param) {
        this.libraryDataBase64 = libraryDataBase64Param;
    }

    /**
     * Gets {@code Sha-256} of the data in {@code Hex}.
     *
     * @return A {@code Sha-256} of the data in {@code Hex}.
     */
    public String getSha256sum() {
        return this.sha256sum;
    }

    /**
     * Sets {@code Sha-256} of the data in {@code Hex}.
     *
     * @param sha256sumParam The {@code ThirdPartyLibrary} data SHA-256 in {@code Hex}.
     */
    public void setSha256sum(String sha256sumParam) {
        this.sha256sum = sha256sumParam;
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
     * Sets if tools should be added to classpath.
     *
     * @param addToolsToClassPathParam The tools be added.
     */
    public void setAddToolsToClassPath(Boolean addToolsToClassPathParam) {
        this.addToolsToClassPath = addToolsToClassPathParam;
    }

    /**
     * Gets The {@code Date} the 3rd Party Lib was created.
     *
     * @return Date Created.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets The {@code Date} the 3rd Party Lib was created.
     *
     * @param dateCreatedParam Date Created.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * Gets The {@code Date} the 3rd Party Lib was last updated.
     *
     * @return Date Last Updated.
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     * Sets The {@code Date} the 3rd Party Lib was last updated.
     *
     * @param dateLastUpdatedParam Date Last Updated.
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Field}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Filename...
        if(this.getFilename() != null)
        {
            returnVal.put(JSONMapping.FILENAME, this.getFilename());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION, this.getDescription());
        }

        //Sha-256 SUM...
        if(this.getSha256sum() != null)
        {
            returnVal.put(JSONMapping.SHA_256_SUM, this.getSha256sum());
        }

        //Add Tools to Classpath...
        if(this.isAddToolsToClassPath() != null)
        {
            returnVal.put(JSONMapping.ADD_TOOLS_TO_CLASS_PATH, this.isAddToolsToClassPath());
        }

        //Library Data in Base-64...
        if(this.getLibraryDataBase64() != null)
        {
            returnVal.put(JSONMapping.LIBRARY_DATA_BASE64,
                    this.getLibraryDataBase64());
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED,
                    this.getDateAsLongFromJson(this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED,
                    this.getDateAsLongFromJson(this.getDateLastUpdated()));
        }

        return returnVal;
    }
}
