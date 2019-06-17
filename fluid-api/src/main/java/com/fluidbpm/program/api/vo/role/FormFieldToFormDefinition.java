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

package com.fluidbpm.program.api.vo.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;

/**
 * <p>
 *     Represents what a {@code Role} permits a {@code User} to do
 *     with a {@code Form} Definition type.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Form
 * @see Role
 */
public class FormFieldToFormDefinition extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private Form formDefinition;
    private Boolean canCreate;

    /**
     * The JSON mapping for the {@code RoleToFormDefinition} object.
     */
    public static class JSONMapping
    {
        public static final String FORM_DEFINITION = "formDefinition";
        public static final String CAN_CREATE = "canCreate";
    }

    /**
     * Default constructor.
     */
    public FormFieldToFormDefinition() {
        super();
    }

    /**
     * Sets the Id associated with a 'Form Field To Form Definition'.
     *
     * @param formFieldToFormDefinitionIdParam Field Id.
     */
    public FormFieldToFormDefinition(Long formFieldToFormDefinitionIdParam) {
        super();

        this.setId(formFieldToFormDefinitionIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormFieldToFormDefinition(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if (this.jsonObject == null)
        {
            return;
        }

        //Form Definition...
        if (!this.jsonObject.isNull(JSONMapping.FORM_DEFINITION)) {
            this.setFormDefinition(new Form(this.jsonObject.getJSONObject(
                    JSONMapping.FORM_DEFINITION)));
        }

        //Can Create...
        if (!this.jsonObject.isNull(JSONMapping.CAN_CREATE)) {
            this.setCanCreate(this.jsonObject.getBoolean(JSONMapping.CAN_CREATE));
        }
    }

    /**
     * Gets the {@code Form} Definition associated with the
     * {@code Role} to Form Definition permission.
     *
     * @return {@code Form} Definition.
     */
    public Form getFormDefinition() {
        return this.formDefinition;
    }

    /**
     * Sets the {@code Form} Definition associated with the
     * {@code Role} to Form Definition permission.
     *
     * @param formDefinitionParam {@code Form} Definition.
     */
    public void setFormDefinition(Form formDefinitionParam) {
        this.formDefinition = formDefinitionParam;
    }

    /**
     * Gets whether the {@code Role} allow for {@code Form} creation.
     *
     * @return Does the {@code Role} allow for {@code Form} creation.
     */
    public Boolean isCanCreate() {
        return this.canCreate;
    }

    /**
     * Sets whether the {@code Role} allow for {@code Form} creation.
     *
     * @param canCreateParam Does the {@code Role} allow for {@code Form} creation.
     */
    public void setCanCreate(Boolean canCreateParam) {
        this.canCreate = canCreateParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToFormDefinition}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Can Create...
        if (this.isCanCreate() != null)
        {
            returnVal.put(JSONMapping.CAN_CREATE,
                    this.isCanCreate().booleanValue());
        }

        //Form Definition...
        if (this.getFormDefinition() != null)
        {
            returnVal.put(JSONMapping.FORM_DEFINITION,
                    this.getFormDefinition().toJsonObject());
        }

        return returnVal;
    }
}
