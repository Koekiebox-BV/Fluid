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

package com.fluid.program.api.vo.userquery;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * <p>
 *     Represents a Query that can be executed via the
 *     API. The {@code UserQuery}s are configured from the
 *     Administration.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 */
public class UserQuery extends ABaseFluidJSONObject {

    private String name;
    private String description;

    /**
     * The JSON mapping for the {@code UserQuery} object.
     */
    public static class JSONMapping
    {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public UserQuery(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(
                    JSONMapping.NAME));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(
                    JSONMapping.DESCRIPTION));
        }
    }

    /**
     * Gets {@code UserQuery} name.
     *
     * @return A {@code UserQuery}s name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets {@code UserQuery} name.
     *
     * @param nameParam A {@code UserQuery} name.
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets {@code UserQuery} description.
     *
     * @return A {@code UserQuery}s description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets {@code UserQuery} description.
     *
     * @param descriptionParam A {@code UserQuery}s description.
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code UserQuery}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME,this.getName());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION,this.getDescription());
        }

        return returnVal;
    }
}
