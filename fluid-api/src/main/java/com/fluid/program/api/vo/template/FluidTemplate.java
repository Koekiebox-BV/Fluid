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
import com.fluid.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluid.program.api.vo.userquery.UserQuery;

/**
 * Fluid Template used to import and export configurations;
 *
 * {@code Flow}'s
 * {@code Form}'s
 * {@code Field}'s
 * {@code UserQuery}'s
 *
 * @see com.fluid.program.api.vo.Form
 * @see com.fluid.program.api.vo.Field
 *
 * @see com.fluid.program.api.vo.userquery.UserQuery
 * @see com.fluid.program.api.vo.flow.Flow
 * @see com.fluid.program.api.vo.thirdpartylib.ThirdPartyLibrary
 *
 * @see com.fluid.program.api.vo.ABaseFluidJSONObject
 *
 * @author jasonbruwer on 2017/01/14.
 * @since 1.4
 */
public class FluidTemplate extends ABaseFluidJSONObject{

    private String templateName;
    private String templateDescription;

    private List<Form> formsAndFields;
    private List<UserQuery> userQueries;
    private List<Flow> flows;

    private List<ThirdPartyLibrary> thirdPartyLibraries;

    private List<Field> userFields;
    private List<Field> routeFields;
    private List<Field> globalFields;

    /**
     * The JSON mapping for the {@code FluidTemplate} object.
     */
    public static final class JSONMapping {
        public static final String TEMPLATE_NAME = "templateName";
        public static final String TEMPLATE_DESCRIPTION = "templateDescription";

        public static final String FORMS_AND_FIELDS = "formsAndFields";
        public static final String USER_QUERIES = "userQueries";
        public static final String FLOWS = "flows";

        public static final String THIRD_PARTY_LIBRARIES = "thirdPartyLibraries";

        public static final String USER_FIELDS = "userFields";
        public static final String ROUTE_FIELDS = "routeFields";
        public static final String GLOBAL_FIELDS = "globalFields";
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

        //Template Name...
        if (!jsonObjectParam.isNull(JSONMapping.TEMPLATE_NAME)) {
            this.setTemplateName(jsonObjectParam.getString(
                    JSONMapping.TEMPLATE_NAME));
        }

        //Template Description...
        if (!jsonObjectParam.isNull(JSONMapping.TEMPLATE_DESCRIPTION)) {
            this.setTemplateDescription(jsonObjectParam.getString(
                    JSONMapping.TEMPLATE_DESCRIPTION));
        }

        //Forms and Fields...
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

        //User Queries...
        if (!this.jsonObject.isNull(JSONMapping.USER_QUERIES)) {

            JSONArray userQueries = this.jsonObject.getJSONArray(
                    JSONMapping.USER_QUERIES);

            List<UserQuery> listOfUserQueries = new ArrayList();
            for(int index = 0;index < userQueries.length();index++)
            {
                listOfUserQueries.add(new UserQuery(userQueries.getJSONObject(index)));
            }

            this.setUserQueries(listOfUserQueries);
        }

        //Flows...
        if (!this.jsonObject.isNull(JSONMapping.FLOWS)) {

            JSONArray flows = this.jsonObject.getJSONArray(
                    JSONMapping.FLOWS);

            List<Flow> listOfFlows = new ArrayList();
            for(int index = 0;index < flows.length();index++)
            {
                listOfFlows.add(new Flow(flows.getJSONObject(index)));
            }

            this.setFlows(listOfFlows);
        }

        //Third Party Libraries...
        if (!this.jsonObject.isNull(JSONMapping.THIRD_PARTY_LIBRARIES)) {

            JSONArray thirdPartyLibs = this.jsonObject.getJSONArray(
                    JSONMapping.THIRD_PARTY_LIBRARIES);

            List<ThirdPartyLibrary> listOfThirdPartyLibs = new ArrayList();
            for(int index = 0;index < thirdPartyLibs.length();index++)
            {
                listOfThirdPartyLibs.add(
                        new ThirdPartyLibrary(thirdPartyLibs.getJSONObject(index)));
            }

            this.setThirdPartyLibraries(listOfThirdPartyLibs);
        }

        //User Fields...
        if (!this.jsonObject.isNull(JSONMapping.USER_FIELDS)) {

            JSONArray userFields = this.jsonObject.getJSONArray(
                    JSONMapping.USER_FIELDS);

            List<Field> listOfFields = new ArrayList();
            for(int index = 0;index < userFields.length();index++)
            {
                listOfFields.add(new Field(userFields.getJSONObject(index)));
            }
            this.setUserFields(listOfFields);
        }

        //Route Fields...
        if (!this.jsonObject.isNull(JSONMapping.ROUTE_FIELDS)) {

            JSONArray routeFields = this.jsonObject.getJSONArray(
                    JSONMapping.ROUTE_FIELDS);

            List<Field> listOfFields = new ArrayList();
            for(int index = 0;index < routeFields.length();index++)
            {
                listOfFields.add(new Field(routeFields.getJSONObject(index)));
            }
            this.setRouteFields(listOfFields);
        }

        //Global Fields...
        if (!this.jsonObject.isNull(JSONMapping.GLOBAL_FIELDS)) {

            JSONArray globalFields = this.jsonObject.getJSONArray(
                    JSONMapping.GLOBAL_FIELDS);

            List<Field> listOfFields = new ArrayList();
            for(int index = 0;index < globalFields.length();index++)
            {
                listOfFields.add(new Field(globalFields.getJSONObject(index)));
            }
            this.setGlobalFields(listOfFields);
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

        //Template Name...
        if(this.getTemplateName() != null)
        {
            returnVal.put(JSONMapping.TEMPLATE_NAME,
                    this.getTemplateName());
        }

        //Template Description...
        if(this.getTemplateDescription() != null)
        {
            returnVal.put(JSONMapping.TEMPLATE_DESCRIPTION,
                    this.getTemplateDescription());
        }

        //Forms and Fields...
        if(this.getFormsAndFields() != null && !this.getFormsAndFields().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(Form form : this.getFormsAndFields())
            {
                jsonArray.put(form.toJsonObject());
            }

            returnVal.put(JSONMapping.FORMS_AND_FIELDS, jsonArray);
        }

        //User Queries...
        if(this.getUserQueries() != null && !this.getUserQueries().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(UserQuery userQuery : this.getUserQueries())
            {
                jsonArray.put(userQuery.toJsonObject());
            }

            returnVal.put(JSONMapping.USER_QUERIES, jsonArray);
        }

        //Flows...
        if(this.getFlows() != null && !this.getFlows().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(Flow flow : this.getFlows())
            {
                jsonArray.put(flow.toJsonObject());
            }

            returnVal.put(JSONMapping.FLOWS, jsonArray);
        }

        //Third Party Libraries...
        if(this.getThirdPartyLibraries() != null &&
                !this.getThirdPartyLibraries().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(ThirdPartyLibrary thirdPartyLibrary : this.getThirdPartyLibraries())
            {
                jsonArray.put(thirdPartyLibrary.toJsonObject());
            }

            returnVal.put(JSONMapping.THIRD_PARTY_LIBRARIES, jsonArray);
        }

        //User Fields...
        if(this.getUserFields() != null && !this.getUserFields().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(Field field : this.getUserFields())
            {
                jsonArray.put(field.toJsonObject());
            }

            returnVal.put(JSONMapping.USER_FIELDS, jsonArray);
        }

        //Route Fields...
        if(this.getRouteFields() != null && !this.getRouteFields().isEmpty())
        {
            JSONArray fieldsArr = new JSONArray();
            for(Field toAdd :this.getRouteFields())
            {
                fieldsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.ROUTE_FIELDS, fieldsArr);
        }

        //Global Fields...
        if(this.getGlobalFields() != null && !this.getGlobalFields().isEmpty())
        {
            JSONArray fieldsArr = new JSONArray();
            for(Field toAdd :this.getGlobalFields())
            {
                fieldsArr.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.GLOBAL_FIELDS, fieldsArr);
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
     * Gets Description of the template.
     *
     * @return Template description.
     */
    public String getTemplateDescription() {
        return this.templateDescription;
    }

    /**
     * Sets Description of the template.
     *
     * @param templateDescriptionParam Fluid Template description.
     */
    public void setTemplateDescription(String templateDescriptionParam) {
        this.templateDescription = templateDescriptionParam;
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
     * @param formsAndFieldsParam Fluid Forms and Fields.
     */
    public void setFormsAndFields(List<Form> formsAndFieldsParam) {
        this.formsAndFields = formsAndFieldsParam;
    }

    /**
     * Sets the template {@code UserQuery}.
     *
     * @return User Queries.
     */
    public List<UserQuery> getUserQueries() {
        return this.userQueries;
    }

    /**
     * Sets the template {@code UserQuery}.
     *
     * @param userQueriesParam User Queries.
     */
    public void setUserQueries(List<UserQuery> userQueriesParam) {
        this.userQueries = userQueriesParam;
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

    /**
     * Gets the Third Party Libraries.
     *
     * @return 3rd Party Libraries.
     */
    public List<ThirdPartyLibrary> getThirdPartyLibraries() {
        return this.thirdPartyLibraries;
    }

    /**
     * Sets the Third Party libraries.
     *
     * @param thirdPartyLibrariesParam 3rd Party Libraries.
     */
    public void setThirdPartyLibraries(List<ThirdPartyLibrary> thirdPartyLibrariesParam) {
        this.thirdPartyLibraries = thirdPartyLibrariesParam;
    }

    /**
     * Gets the User {@code Field}s.
     *
     * @return User fields.
     */
    public List<Field> getUserFields() {
        return this.userFields;
    }

    /**
     * Sets the User {@code Field}s.
     *
     * @param userFieldsParam User fields.
     */
    public void setUserFields(List<Field> userFieldsParam) {
        this.userFields = userFieldsParam;
    }

    /**
     * Gets the Route {@code Field}s.
     *
     * @return Route fields.
     */
    public List<Field> getRouteFields() {
        return this.routeFields;
    }

    /**
     * Sets the Route {@code Field}s.
     *
     * @param routeFieldsParam Route fields.
     */
    public void setRouteFields(List<Field> routeFieldsParam) {
        this.routeFields = routeFieldsParam;
    }

    /**
     * Gets the Global {@code Field}s.
     *
     * @return Global fields.
     */
    public List<Field> getGlobalFields() {
        return this.globalFields;
    }

    /**
     * Sets the Global {@code Field}s.
     *
     * @param globalFieldsParam Global fields.
     */
    public void setGlobalFields(List<Field> globalFieldsParam) {
        this.globalFields = globalFieldsParam;
    }
}
