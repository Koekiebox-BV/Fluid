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

package com.fluid.program.api.vo.template;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.userquery.UserQuery;

/**
 * Fluid Template used to import and export configurations;
 *
 * --> Flow's
 * --> Form
 * --> Field
 * --> UserQuery
 *
 * @see com.fluid.program.api.vo.Form
 * @see com.fluid.program.api.vo.Field
 *
 * @see com.fluid.program.api.vo.userquery.UserQuery
 * @see com.fluid.program.api.vo.flow.Flow
 *
 * @see com.fluid.program.api.vo.ABaseFluidJSONObject
 *
 * @author jasonbruwer on 2017/01/14.
 * @since 1.4
 */
public class FluidTemplate extends ABaseFluidJSONObject{

    private String templateName;

    private List<Form> formsAndFields;
    private List<Field> userFields;
    private List<Field> routeFields;
    private List<Field> globalFields;
    private List<UserQuery> userQueries;

    private List<Flow> flows;

    /**
     * The JSON mapping for the {@code FluidTemplate} object.
     */
    public static final class JSONMapping {
        public static final String TEMPLATE_NAME = "templateName";

        public static final String FORMS_AND_FIELDS = "formsAndFields";
        public static final String USER_FIELDS = "userFields";
        public static final String ROUTE_FIELDS = "routeFields";
        public static final String GLOBAL_FIELDS = "globalFields";
        public static final String USER_QUERIES = "userQueries";

        public static final String FLOWS = "flows";
    }

    /**
     * Default constructor.
     */
    public FluidTemplate() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FluidTemplate(JSONObject jsonObjectParam) {
        super();

        //Entry Rules...
        if (!this.jsonObject.isNull(JSONMapping.FORMS_AND_FIELDS)) {

            JSONArray formsAndFields = this.jsonObject.getJSONArray(
                    JSONMapping.FORMS_AND_FIELDS);

            List<Form> listOfForms = new ArrayList();
            for(int index = 0;index < formsAndFields.length();index++)
            {
                listOfForms.add(new Form(formsAndFields.getJSONObject(index)));
            }

            this.setFormsAndFields(listOfForms);
        }

        //Template Name...
        if (!jsonObjectParam.isNull(JSONMapping.TEMPLATE_NAME)) {
            this.setTemplateName(jsonObjectParam.getString(
                    JSONMapping.TEMPLATE_NAME));
        }

    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FluidTemplate}
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = new JSONObject();

        //Forms and Fields...
        if(this.getFormsAndFields() != null &&
                !this.getFormsAndFields().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(Form form : this.getFormsAndFields())
            {
                jsonArray.put(form.toJsonObject());
            }

            returnVal.put(JSONMapping.FORMS_AND_FIELDS, jsonArray);
        }

        //Template Name...
        if(this.getTemplateName() != null)
        {
            returnVal.put(JSONMapping.TEMPLATE_NAME,
                    this.getTemplateName());
        }

        return returnVal;
    }

    /**
     * Gets Name of the template.
     *
     * @return Template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }

    /**
     * Sets Name of the template.
     *
     * @param templateNameParam Fluid Template name.
     */
    public void setTemplateName(String templateNameParam) {
        this.templateName = templateNameParam;
    }

    /**
     * Template Forms and Fields.
     *
     * @return Forms and Fields.
     */
    public List<Form> getFormsAndFields() {
        return this.formsAndFields;
    }

    /**
     * Sets Form's and Fields for the template.
     *
     * @param formsAndFieldsParam Fluid Forms and Fields name.
     */
    public void setFormsAndFields(List<Form> formsAndFieldsParam) {
        this.formsAndFields = formsAndFieldsParam;
    }

    /**
     * Gets Flows.
     *
     * @return Template name.
     */
    public List<Flow> getFlows() {
        return this.flows;
    }

    /**
     * Sets Flows for the template.
     *
     * @param flowsParam Flow's for the template.
     */
    public void setFlows(List<Flow> flowsParam) {
        this.flows = flowsParam;
    }
}
