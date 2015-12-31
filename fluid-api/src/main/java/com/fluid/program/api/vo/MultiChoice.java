package com.fluid.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class MultiChoice extends ABaseFluidJSONObject {

    private List<String> availableMultiChoices;
    private List<String> selectedMultiChoices;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String AVAILABLE_MULTI_CHOICES = "availableMultiChoices";
        public static final String SELECTED_MULTI_CHOICES = "selectedMultiChoices";
    }

    /**
     *
     */
    public MultiChoice() {
        super();
    }

    /**
     *
     * @param selectedMultiChoicesParam
     */
    public MultiChoice(List<String> selectedMultiChoicesParam)
    {
        this.selectedMultiChoices = selectedMultiChoicesParam;
    }

    /**
     *
     * @param selectedMultiChoiceValueParam
     */
    public MultiChoice(String selectedMultiChoiceValueParam)
    {
        this.selectedMultiChoices = new ArrayList<String>();
        if(selectedMultiChoiceValueParam != null)
        {
            this.selectedMultiChoices.add(selectedMultiChoiceValueParam);
        }
    }

    /**
     *
     * @param jsonObjectParam
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

            this.availableMultiChoices = new ArrayList<String>();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.availableMultiChoices.add(arrayOfString.getString(index));
            }
        }

        //Selected Multiple Choices...
        if (!this.jsonObject.isNull(JSONMapping.SELECTED_MULTI_CHOICES)) {

            JSONArray arrayOfString =
                    this.jsonObject.getJSONArray(JSONMapping.SELECTED_MULTI_CHOICES);

            this.selectedMultiChoices = new ArrayList<String>();

            for(int index = 0;index < arrayOfString.length();index++)
            {
                this.selectedMultiChoices.add(arrayOfString.getString(index));
            }
        }
    }

    /**
     *
     * @return
     * @throws org.json.JSONException
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
        }

        //Selected...
        if(this.getSelectedMultiChoices() != null)
        {
            returnVal.put(JSONMapping.SELECTED_MULTI_CHOICES,
                    new JSONArray(this.getSelectedMultiChoices().toArray()));
        }

        return returnVal;
    }


    /**
     *
     * @return
     */
    public List<String> getAvailableMultiChoices() {
        return this.availableMultiChoices;
    }

    /**
     *
     * @param availableMultiChoices
     */
    public void setAvailableMultiChoices(List<String> availableMultiChoices) {
        this.availableMultiChoices = availableMultiChoices;
    }

    /**
     *
     * @return
     */
    public List<String> getSelectedMultiChoices() {
        return this.selectedMultiChoices;
    }

    /**
     *
     * @param selectedMultiChoices
     */
    public void setSelectedMultiChoices(List<String> selectedMultiChoices) {
        this.selectedMultiChoices = selectedMultiChoices;
    }

    /**
     *
     * @return
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
            return toString.substring(0,toString.length() - 2);
        }

        return toString;
    }
}
