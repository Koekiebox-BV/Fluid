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

package com.fluidbpm.program.api.vo.form;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an Fluid Table Record.
 *
 * @author jasonbruwer
 * @see Form
 * @see Field
 * @since v1.1
 */
@Getter
@Setter
@NoArgsConstructor
public class TableRecord extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private Form formContainer;
    private Form parentFormContainer;
    private Field parentFormField;

    /**
     * The JSON mapping for the {@code TableRecord} object.
     */
    public static class JSONMapping {
        public static final String FORM_CONTAINER = "formContainer";
        public static final String PARENT_FORM_CONTAINER = "parentFormContainer";
        public static final String PARENT_FORM_FIELD = "parentFormField";
    }

    /**
     * Sets the Id associated with a Field.
     *
     * @param tableRecordIdParam Field Id.
     */
    public TableRecord(Long tableRecordIdParam) {
        super();
        this.setId(tableRecordIdParam);
    }

    /**
     * Set {@code formContainerParam}, {@code parentFormContainer} and {@code parentFormField} to
     * create a complete {@code TableRecord}.
     *
     * @param formContainerParam  The form container with the table record fields.
     * @param parentFormContainer The containing form.
     * @param parentFormField     The Field the table record belongs to in the {@code formContainerParam}.
     * @see Form
     * @see Field
     */
    public TableRecord(Form formContainerParam, Form parentFormContainer, Field parentFormField) {
        super();
        this.setFormContainer(formContainerParam);
        this.setParentFormContainer(parentFormContainer);
        this.setParentFormField(parentFormField);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public TableRecord(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFormContainer(this.extractObject(JSONMapping.FORM_CONTAINER, Form::new));
        this.setParentFormContainer(this.extractObject(JSONMapping.PARENT_FORM_CONTAINER, Form::new));
        this.setParentFormField(this.extractObject(JSONMapping.PARENT_FORM_FIELD, Field::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code Field}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsObj(JSONMapping.FORM_CONTAINER, returnVal, this::getFormContainer);
        this.setAsObj(JSONMapping.PARENT_FORM_CONTAINER, returnVal, this::getParentFormContainer);
        this.setAsObj(JSONMapping.PARENT_FORM_FIELD, returnVal, this::getParentFormField);
        return returnVal;
    }
}