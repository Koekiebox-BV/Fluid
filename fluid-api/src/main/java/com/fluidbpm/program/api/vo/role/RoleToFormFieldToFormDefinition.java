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
 * Represents the permission relationship between
 * {@code Role} to {@code Field} and {@code Form}.
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @see Field
 * @see Role
 * @since v1.1
 */
public class RoleToFormFieldToFormDefinition extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private FormFieldToFormDefinition formFieldToFormDefinition;

    private Boolean canView;
    private Boolean canCreateAndModify;

    /**
     * The JSON mapping for the {@code RoleToFormFieldToFormDefinition} object.
     */
    public static class JSONMapping {
        public static final String FORM_FIELD_TO_FORM_DEFINITION = "formFieldToFormDefinition";
        public static final String CAN_VIEW = "canView";
        public static final String CAN_CREATE_AND_MODIFY = "canCreateAndModify";
    }

    /**
     * Default constructor.
     */
    public RoleToFormFieldToFormDefinition() {
        super();
    }

    /**
     * Sets the Id associated with a 'Form Field To Form Definition'.
     *
     * @param roleToFormFieldToFormDefinitionIdParam Field Id.
     */
    public RoleToFormFieldToFormDefinition(Long roleToFormFieldToFormDefinitionIdParam) {
        super();

        this.setId(roleToFormFieldToFormDefinitionIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RoleToFormFieldToFormDefinition(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFormFieldToFormDefinition(this.extractObject(JSONMapping.FORM_FIELD_TO_FORM_DEFINITION, FormFieldToFormDefinition::new));
        this.setCanView(this.getAsBooleanNullSafe(JSONMapping.CAN_VIEW));
        this.setCanCreateAndModify(this.getAsBooleanNullSafe(JSONMapping.CAN_CREATE_AND_MODIFY));
    }

    /**
     * Gets whether a {@code Field} may be viewed.
     *
     * @return Whether the {@code Form}, {@code Field} and role
     * relationship allows for viewing.
     * @see Field
     * @see Form
     * @see Role
     */
    public Boolean isCanView() {
        return this.canView;
    }

    /**
     * Gets whether a {@code Field} may be viewed.
     *
     * @param canViewParam Whether the {@code Form}, {@code Field} and role
     *                     relationship allows for viewing.
     * @see Field
     * @see Form
     * @see Role
     */
    public void setCanView(Boolean canViewParam) {
        this.canView = canViewParam;
    }

    /**
     * Gets whether a {@code Field} may created and modified.
     *
     * @return Whether the {@code Form}, {@code Field} and role
     * relationship allows for creation and modification.
     * @see Field
     * @see Form
     * @see Role
     */
    public Boolean isCanCreateAndModify() {
        return this.canCreateAndModify;
    }

    /**
     * Gets whether a {@code Field} may created and modified.
     *
     * @param canCreateAndModifyParam Whether the {@code Form}, {@code Field} and role
     *                                relationship allows for creation and modification.
     * @see Field
     * @see Form
     * @see Role
     */
    public void setCanCreateAndModify(Boolean canCreateAndModifyParam) {
        this.canCreateAndModify = canCreateAndModifyParam;
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code RoleToFormFieldToFormDefinition}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.CAN_CREATE_AND_MODIFY, returnVal, this.isCanCreateAndModify());
        this.setAsProperty(JSONMapping.CAN_VIEW, returnVal, this.isCanView());
        this.setAsObj(JSONMapping.FORM_FIELD_TO_FORM_DEFINITION, returnVal, this::getFormFieldToFormDefinition);

        return returnVal;
    }
}