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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents what a {@code Role} permits a {@code User} to do
 * with a {@code Form} Definition type.
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @see Role
 * @since v1.1
 */
@Getter
@Setter
public class FormFieldToFormDefinition extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private Form formDefinition;
    private Field formField;
    private Boolean canCreate;

    /**
     * The JSON mapping for the {@code RoleToFormDefinition} object.
     */
    public static class JSONMapping {
        public static final String FORM_DEFINITION = "formDefinition";
        public static final String FORM_FIELD = "formField";
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
    public FormFieldToFormDefinition(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        //Form Definition...
        this.setFormDefinition(this.extractObject(this.jsonObject, JSONMapping.FORM_DEFINITION, Form::new));

        //Can Create...
        this.setCanCreate(this.getAsBooleanNullSafe(this.jsonObject, JSONMapping.CAN_CREATE));

        //Form Field...
        this.setFormField(this.extractObject(this.jsonObject, JSONMapping.FORM_FIELD, Field::new));
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
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code RoleToFormDefinition}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        
        //Can Create...
        if (this.isCanCreate() != null) {
            returnVal.addProperty(JSONMapping.CAN_CREATE, this.isCanCreate().booleanValue());
        }

        //Form Definition...
        if (this.getFormDefinition() != null) {
            returnVal.add(JSONMapping.FORM_DEFINITION, this.getFormDefinition().toJsonObject());
        }

        //Field...
        if (this.getFormField() != null) {
            returnVal.add(JSONMapping.FORM_FIELD, this.getFormField().toJsonObject());
        }

        return returnVal;
    }
}