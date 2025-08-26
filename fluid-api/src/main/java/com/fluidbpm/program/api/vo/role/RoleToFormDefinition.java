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
public class RoleToFormDefinition extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Form formDefinition;

    private Boolean canCreate;
    private Boolean attachmentsCreateUpdate;
    private Boolean attachmentsView;

    /**
     * The JSON mapping for the {@code RoleToFormDefinition} object.
     */
    public static class JSONMapping {
        public static final String FORM_DEFINITION = "formDefinition";

        public static final String CAN_CREATE = "canCreate";
        public static final String ATTACHMENTS_CREATE_UPDATE = "attachmentsCreateUpdate";
        public static final String ATTACHMENTS_VIEW = "attachmentsView";
    }

    /**
     * Default constructor.
     */
    public RoleToFormDefinition() {
        super();
    }

    /**
     * Sets the Id associated with a 'Role To Form Definition'.
     *
     * @param roleToFormDefinitionIdParam RoleToFormDefinition Id.
     */
    public RoleToFormDefinition(Long roleToFormDefinitionIdParam) {
        super();
        this.setId(roleToFormDefinitionIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RoleToFormDefinition(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFormDefinition(this.extractObject(JSONMapping.FORM_DEFINITION, Form::new));
        this.setCanCreate(this.getAsBooleanNullSafe(JSONMapping.CAN_CREATE));
        this.setAttachmentsView(this.getAsBooleanNullSafe(JSONMapping.ATTACHMENTS_VIEW));
        this.setAttachmentsCreateUpdate(this.getAsBooleanNullSafe(JSONMapping.ATTACHMENTS_CREATE_UPDATE));
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
     * Gets whether the {@code Role} allow for creating or modifying
     * {@code Form} attachments.
     *
     * @return Does the {@code Role} allow for {@code Form} attachment create
     * or modification.
     */
    public Boolean isAttachmentsCreateUpdate() {
        return this.attachmentsCreateUpdate;
    }

    /**
     * Sets whether the {@code Role} allow for {@code Form} attachment
     * creation or modification.
     *
     * @param attachmentsCreateUpdateParam Does the {@code Role} allow for {@code Form} attachment
     *                                     creation or modification.
     */
    public void setAttachmentsCreateUpdate(Boolean attachmentsCreateUpdateParam) {
        this.attachmentsCreateUpdate = attachmentsCreateUpdateParam;
    }

    /**
     * Gets whether the {@code Role} allow for viewing {@code Form} attachments.
     *
     * @return Does the {@code Role} allow for {@code Form} attachment view.
     */
    public Boolean isAttachmentsView() {
        return this.attachmentsView;
    }

    /**
     * Sets whether the {@code Role} allow for {@code Form} attachment viewing.
     *
     * @param attachmentsViewParam Does the {@code Role} allow for
     *                             {@code Form} attachment viewing.
     */
    public void setAttachmentsView(Boolean attachmentsViewParam) {
        this.attachmentsView = attachmentsViewParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToFormDefinition}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsObj(JSONMapping.FORM_DEFINITION, returnVal, this::getFormDefinition);
        this.setAsProperty(JSONMapping.CAN_CREATE, returnVal, this.isCanCreate());
        this.setAsProperty(JSONMapping.ATTACHMENTS_VIEW, returnVal, this.isAttachmentsView());
        this.setAsProperty(JSONMapping.ATTACHMENTS_CREATE_UPDATE, returnVal, this.isAttachmentsCreateUpdate());
        return returnVal;
    }
}
