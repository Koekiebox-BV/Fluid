/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2021] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code CustomWebAction} for executing custom web actions on the host.
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @since v1.11
 */
@Getter
@Setter
public class CustomWebAction extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private Form form;
    private String taskIdentifier;
    private Boolean isTableRecord;
    private Long formTableRecordBelongsTo;

    private Long executionTimeMillis;

    /**
     * The JSON mapping for the {@code CustomWebAction} object.
     */
    public static class JSONMapping {
        public static final String FORM = "form";
        public static final String TASK_IDENTIFIER = "taskIdentifier";
        public static final String IS_TABLE_RECORD = "isTableRecord";
        public static final String FORM_TABLE_RECORD_BELONGS_TO = "formTableRecordBelongsTo";
        public static final String EXECUTION_TIME_MILLIS = "executionTimeMillis";
    }

    /**
     * Default constructor.
     */
    public CustomWebAction() {
        super();
    }

    /**
     * Constructor to create {@code CustomWebAction} with records.
     *
     * @param form           The Form item to apply on custom action.
     * @param taskIdentifier The unique task identifier to execute
     */
    public CustomWebAction(Form form, String taskIdentifier) {
        super();
        this.setForm(form);
        this.setTaskIdentifier(taskIdentifier);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public CustomWebAction(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setTaskIdentifier(this.getAsStringNullSafe(JSONMapping.TASK_IDENTIFIER));
        this.setExecutionTimeMillis(this.getAsLongNullSafe(JSONMapping.EXECUTION_TIME_MILLIS));
        this.setIsTableRecord(this.getAsBooleanNullSafe(JSONMapping.IS_TABLE_RECORD));
        this.setFormTableRecordBelongsTo(this.getAsLongNullSafe(JSONMapping.FORM_TABLE_RECORD_BELONGS_TO));
        this.setForm(this.extractObject(JSONMapping.FORM, Form::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code CustomWebAction}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.TASK_IDENTIFIER, returnVal, this.getTaskIdentifier());
        this.setAsProperty(JSONMapping.EXECUTION_TIME_MILLIS, returnVal, this.getExecutionTimeMillis());
        this.setAsProperty(JSONMapping.IS_TABLE_RECORD, returnVal, this.getIsTableRecord());
        this.setAsProperty(JSONMapping.FORM_TABLE_RECORD_BELONGS_TO, returnVal, this.getFormTableRecordBelongsTo());
        this.setAsObj(JSONMapping.FORM, returnVal, this::getForm);
        return returnVal;
    }

    /**
     * String value for a table field.
     *
     * @return JSON text from the table field.
     */
    @Override
    public String toString() {
        JsonObject jsonObject = this.toJsonObject();
        if (jsonObject != null) return jsonObject.toString();
        return UtilGlobal.EMPTY;
    }
}