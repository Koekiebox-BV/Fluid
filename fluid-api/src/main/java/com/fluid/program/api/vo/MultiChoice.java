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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     Represents a {@code MultiChoice} value at any level (Form, Route, User and Global).
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Field
 * @see Field#setFieldValue(Object)
 */
public class MultiChoice extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private List<String> availableMultiChoices;
    private List<String> selectedMultiChoices;

    /**
     * The JSON mapping for the {@code MultiChoice} object.
     */
    public static class JSONMapping
    {
        public static final String AVAILABLE_MULTI_CHOICES = "availableMultiChoices";
        public static final String SELECTED_MULTI_CHOICES = "selectedMultiChoices";

        public static final String AVAILABLE_CHOICES = "availableChoices";
        public static final String SELECTED_CHOICES = "selectedChoices";
    }

    /**
     * Default constructor.
     */
    public MultiChoice() {
        super();
    }

    /**
     * Sets the Selected MultiChoices for when multiple selections are supported.
     *
     * @param selectedMultiChoicesParam List of Selected {@code MultiChoices}s.
     */
    public MultiChoice(List<String> selectedMultiChoicesParam)
    {
        this.selectedMultiChoices = selectedMultiChoicesParam;
    }

    /**
     * Sets the Selected MultiChoices for when multiple selections are supported.
     *
     * @param selectedMultiChoiceValueParam Selected {@code MultiChoices} {@code String} value.
     */
    public MultiChoice(String selectedMultiChoiceValueParam)
    {
        this.selectedMultiChoices = new ArrayList();
        if(selectedMultiChoiceValueParam != null)
        {
            this.selectedMultiChoices.add(selectedMultiChoiceValueParam);
        }
    }

    /**
     * Populates local variables with {@code jsonObjectParam}
     *
     * @param jsonObjectParam The JSON Object.
     */
    public MultiChoice(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Available Multiple Choices...
        if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_MULTI_CHOICES)) {

            JSONArray arrayOfString =
                    this.jsonObject.getJSONArray(JSONMapping.AVAILABLE_MULTI_CHOICES);

            this.availableMultiChoices = new ArrayList();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.availableMultiChoices.add(arrayOfString.getString(index));
            }
        }
        else if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_CHOICES)) {

            JSONArray arrayOfString =
                    this.jsonObject.getJSONArray(JSONMapping.AVAILABLE_CHOICES);

            this.availableMultiChoices = new ArrayList();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.availableMultiChoices.add(arrayOfString.getString(index));
            }
        }

        //Selected Multiple Choices...
        if (!this.jsonObject.isNull(JSONMapping.SELECTED_MULTI_CHOICES)) {

            JSONArray arrayOfString =
                    this.jsonObject.getJSONArray(JSONMapping.SELECTED_MULTI_CHOICES);

            this.selectedMultiChoices = new ArrayList();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.selectedMultiChoices.add(arrayOfString.getString(index));
            }
        }
        else if (!this.jsonObject.isNull(JSONMapping.SELECTED_CHOICES)) {

            JSONArray arrayOfString =
                    this.jsonObject.getJSONArray(JSONMapping.SELECTED_CHOICES);

            this.selectedMultiChoices = new ArrayList();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.selectedMultiChoices.add(arrayOfString.getString(index));
            }
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MultiChoice}.
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Available...
        if(this.getAvailableMultiChoices() != null)
        {
            returnVal.put(JSONMapping.AVAILABLE_MULTI_CHOICES,
                    new JSONArray(this.getAvailableMultiChoices().toArray()));

            returnVal.put(JSONMapping.AVAILABLE_CHOICES,
                    new JSONArray(this.getAvailableMultiChoices().toArray()));
        }

        //Selected...
        if(this.getSelectedMultiChoices() != null)
        {
            returnVal.put(JSONMapping.SELECTED_MULTI_CHOICES,
                    new JSONArray(this.getSelectedMultiChoices().toArray()));

            returnVal.put(JSONMapping.SELECTED_CHOICES,
                    new JSONArray(this.getSelectedMultiChoices().toArray()));
        }

        return returnVal;
    }

    /**
     * Constructs a {@code String} value for the selected MultiChoices.
     *
     * @return Comma separated list of Selected MultiChoices.
     *
     * @see MultiChoice#getSelectedMultiChoices()
     */
    @Override
    public String toString() {

        StringBuilder theBuilder = new StringBuilder();

        if(this.getSelectedMultiChoices() != null && !this.getSelectedMultiChoices().isEmpty())
        {
            for(String selected : this.getSelectedMultiChoices())
            {
                theBuilder.append(selected);
                theBuilder.append(", ");
            }
        }

        String toString = theBuilder.toString();

        if(toString.isEmpty())
        {
            return toString;
        }

        if(toString.length() > 2)
        {
            return toString.substring(0, toString.length() - 2);
        }

        return toString;
    }

    /**
     * Gets Available MultiChoices.
     *
     * @return {@code List} of available multi choices.
     */
    public List<String> getAvailableMultiChoices() {
        return this.availableMultiChoices;
    }

    /**
     * Sets Available MultiChoices.
     *
     * @param availableMultiChoices {@code List} of available multi choices.
     */
    public void setAvailableMultiChoices(List<String> availableMultiChoices) {
        this.availableMultiChoices = availableMultiChoices;
    }

    /**
     * Gets Selected MultiChoices.
     *
     * @return {@code List} of selected multi choices.
     */
    public List<String> getSelectedMultiChoices() {
        return this.selectedMultiChoices;
    }

    /**
     * Sets Selected MultiChoices.
     *
     * @param selectedMultiChoices {@code List} of selected multi choices.
     */
    public void setSelectedMultiChoices(List<String> selectedMultiChoices) {
        this.selectedMultiChoices = selectedMultiChoices;
    }
}
