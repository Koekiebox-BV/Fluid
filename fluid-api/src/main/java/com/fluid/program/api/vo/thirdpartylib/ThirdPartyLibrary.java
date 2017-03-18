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

package com.fluid.program.api.vo.thirdpartylib;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;

/**
 * 3rd Party library uploaded through the Fluid core.
 *
 * {@code Field} can be part of Electronic Form or Form Definition in Fluid.
 *
 * @author jasonbruwer on 2017-03-10
 * @since v1.4
 *
 * @see Form
 * @see FluidItem
 * @see ABaseFluidJSONObject
 */
public class ThirdPartyLibrary extends ABaseFluidJSONObject {

    private String filename;
    private String description;
    private String libraryDataBase64;

    /**
     * The JSON mapping for the {@code Field} object.
     */
    public static class JSONMapping
    {
        public static final String FILENAME= "filename";
        public static final String DESCRIPTION = "description";
        public static final String LIBRARY_DATA_BASE64= "libraryDataBase64";
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

        //Data Base-64...
        if (!this.jsonObject.isNull(JSONMapping.LIBRARY_DATA_BASE64)) {
            this.setLibraryDataBase64(this.jsonObject.getString(JSONMapping.LIBRARY_DATA_BASE64));
        }
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

        //Library Data in Base-64...
        if(this.getLibraryDataBase64() != null)
        {
            returnVal.put(JSONMapping.LIBRARY_DATA_BASE64,
                    this.getLibraryDataBase64());
        }

        return returnVal;
    }
}
