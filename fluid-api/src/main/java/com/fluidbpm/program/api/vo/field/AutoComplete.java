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

package com.fluidbpm.program.api.vo.field;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * POJO for an auto-complete feature.
 * Request query will return a list of possible values.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.12
 */
@Getter
@Setter
public class AutoComplete extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private Field formField;
    private String query;
    private Integer maxResults;
    private List<String> existingValues;

    /**
     * The JSON mapping for the {@code AutoComplete} object.
     */
    public static class JSONMapping {
        public static final String FORM_FIELD = "formField";
        public static final String QUERY = "query";
        public static final String MAX_RESULTS = "maxResults";
        public static final String EXISTING_VALUES = "existingValues";
    }


    /**
     * Default constructor. Sets empty result.
     */
    public AutoComplete() {
        super();
        this.setExistingValues(new ArrayList<>());
    }

    /**
     * Set the query for search criteria.
     *
     * @param formField  The form field to auto-complete for.
     * @param query      The query text to look for matching field values.
     * @param maxResults The maximum number of results to return.
     */
    public AutoComplete(Field formField, String query, int maxResults) {
        this();
        this.setFormField(formField);
        this.setQuery(query);
        this.setMaxResults(maxResults);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObject The JSON Object.
     */
    public AutoComplete(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        //Query...
        this.setQuery(this.getAsStringNullSafe(this.jsonObject, JSONMapping.QUERY));
        this.setMaxResults(this.getAsIntegerNullSafe(this.jsonObject, JSONMapping.MAX_RESULTS));
        this.setFormField(new Field(this.jsonObject.getAsJsonObject(JSONMapping.FORM_FIELD)));

        //Result...
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.EXISTING_VALUES)) {
            JsonArray array = this.jsonObject.getAsJsonArray(JSONMapping.EXISTING_VALUES);
            List<String> results = new ArrayList<>();
            array.forEach(el -> results.add(el.getAsString()));
            this.setExistingValues(results);
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code AutoComplete}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        //Query...
        returnVal.addProperty(JSONMapping.QUERY, this.getQuery());
        returnVal.addProperty(JSONMapping.MAX_RESULTS, this.getMaxResults());
        returnVal.add(JSONMapping.FORM_FIELD, this.getFormField().toJsonObject());

        //Inputs...
        if (this.getExistingValues() != null) {
            JsonArray jsonArray = new JsonArray();
            for (String toAdd : this.getExistingValues()) {
                jsonArray.add(toAdd);
            }
            returnVal.add(JSONMapping.EXISTING_VALUES, jsonArray);
        }
        return returnVal;
    }
}
