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

package com.fluidbpm.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;

/**
 * <p>
 *     Represents a {@code MultiChoice} value at any level (Form, Route, User and Global).
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 * @version v1.8
 *
 * @see Field
 * @see Field#setFieldValue(Object)
 */
public class MultiChoice extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private List<String> availableMultiChoices;
    private String availableMultiChoicesCombined;

    private List<String> selectedMultiChoices;
    private String selectedMultiChoicesCombined;
    
    /**
     * The JSON mapping for the {@code MultiChoice} object.
     */
    public static class JSONMapping
    {
        public static final String AVAILABLE_MULTI_CHOICES = "availableMultiChoices";
        public static final String SELECTED_MULTI_CHOICES = "selectedMultiChoices";

        public static final String AVAILABLE_CHOICES = "availableChoices";
        public static final String SELECTED_CHOICES = "selectedChoices";

        public static final String AVAILABLE_CHOICES_COMBINED = "availableChoicesCombined";
        public static final String SELECTED_CHOICES_COMBINED = "selectedChoicesCombined";

        //For Payara mapping of [type] and [value]...
        public static final String TYPE = "type";
        public static final String TYPE_STRING = "string";

        public static final String VALUE = "value";
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
        else if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_CHOICES_COMBINED)) {

            String combinedAvailChoices =
                    this.jsonObject.getString(JSONMapping.AVAILABLE_CHOICES_COMBINED);

            if(combinedAvailChoices != null && !combinedAvailChoices.isEmpty())
            {
                this.availableMultiChoices = new ArrayList();

                String[] pipeSplit = combinedAvailChoices.split(UtilGlobal.REG_EX_PIPE);

                if(pipeSplit != null && pipeSplit.length > 0)
                {
                    for(int index = 0;index < pipeSplit.length;index++)
                    {
                        this.availableMultiChoices.add(pipeSplit[index]);
                    }
                }
            }
        }

        //Avail - Populate combined...
        if(this.availableMultiChoices != null)
        {
            this.availableMultiChoicesCombined =
                    this.combineStringArrayWith(
                            this.availableMultiChoices,
                            UtilGlobal.PIPE);
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
        else if (!this.jsonObject.isNull(JSONMapping.SELECTED_CHOICES_COMBINED)) {

            String combinedSelectedChoices =
                    this.jsonObject.getString(JSONMapping.SELECTED_CHOICES_COMBINED);

            if(combinedSelectedChoices != null && !combinedSelectedChoices.isEmpty())
            {
                this.selectedMultiChoices = new ArrayList();

                String[] pipeSplit = combinedSelectedChoices.split(UtilGlobal.REG_EX_PIPE);

                if(pipeSplit != null && pipeSplit.length > 0)
                {
                    for(int index = 0;index < pipeSplit.length;index++)
                    {
                        this.selectedMultiChoices.add(pipeSplit[index]);
                    }
                }
            }
        }

        //Selected - Populate combined...
        if(this.selectedMultiChoices != null)
        {
            this.selectedMultiChoicesCombined =
                    this.combineStringArrayWith(
                            this.selectedMultiChoices,
                            UtilGlobal.PIPE);
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
            List<String> availChoices = this.getAvailableMultiChoices();

            returnVal.put(JSONMapping.AVAILABLE_MULTI_CHOICES,
                    new JSONArray(availChoices.toArray()));

            returnVal.put(JSONMapping.AVAILABLE_CHOICES,
                    new JSONArray(availChoices.toArray()));

            returnVal.put(
                    JSONMapping.AVAILABLE_CHOICES_COMBINED,
                    this.combineStringArrayWith(
                            availChoices,
                            UtilGlobal.PIPE));
        }

        //Selected...
        if(this.getSelectedMultiChoices() != null)
        {
            List<String> selectChoices = this.getSelectedMultiChoices();

            returnVal.put(JSONMapping.SELECTED_MULTI_CHOICES,
                    new JSONArray(selectChoices.toArray()));

            returnVal.put(JSONMapping.SELECTED_CHOICES,
                    new JSONArray(selectChoices.toArray()));

            returnVal.put(
                    JSONMapping.SELECTED_CHOICES_COMBINED,
                    this.combineStringArrayWith(
                            selectChoices,
                            UtilGlobal.PIPE));
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

        return this.combineStringArrayWith(
                this.getSelectedMultiChoices(),
                UtilGlobal.COMMA_SPACE);
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

    /**
     * Gets Selected MultiChoices combined Text.
     *
     * @return {@code List} of selected multi choices combined.
     */
    public String getSelectedMultiChoicesCombined() {
        return this.selectedMultiChoicesCombined;
    }

    /**
     * Sets Selected MultiChoices combined.
     *
     * @param selectedMultiChoicesCombinedParam combined text of multi choices.
     */
    public void setSelectedMultiChoicesCombined(String selectedMultiChoicesCombinedParam) {
        this.selectedMultiChoicesCombined = selectedMultiChoicesCombinedParam;
    }

    /**
     * Gets Available MultiChoices combined Text.
     *
     * @return {@code List} of available multi choices combined.
     */
    public String getAvailableMultiChoicesCombined() {
        return this.availableMultiChoicesCombined;
    }

    /**
     * Sets Available MultiChoices combined.
     *
     * @param availableMultiChoicesCombinedParam combined text of multi choices.
     */
    public void setAvailableMultiChoicesCombined(String availableMultiChoicesCombinedParam) {
        this.availableMultiChoicesCombined = availableMultiChoicesCombinedParam;
    }

    /**
     * Combine {@code listToCombineParam} into a single {@code String}.
     *
     * @param listToCombineParam To combine.
     * @param separatorCharsParam The char used to separate with.
     *
     * @return The combined text value.
     */
    @XmlTransient
    public String combineStringArrayWith(
            List<String> listToCombineParam,
            String separatorCharsParam)
    {
        String returnValue = UtilGlobal.EMPTY;

        int lengthOfSepChars = (separatorCharsParam == null) ? 0 :
                separatorCharsParam.length();

        if(listToCombineParam != null && !listToCombineParam.isEmpty())
        {
            StringBuffer concatBuffer = new StringBuffer();
            for(String toAdd : listToCombineParam)
            {
                concatBuffer.append(toAdd);
                concatBuffer.append(separatorCharsParam);
            }

            String concatString = concatBuffer.toString();
            returnValue = concatString.substring(0, concatString.length() - lengthOfSepChars);
        }

        return returnValue;
    }
}
