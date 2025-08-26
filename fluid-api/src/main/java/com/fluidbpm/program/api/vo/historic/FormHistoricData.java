/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2018] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.historic;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;

import java.util.Date;

/**
 * <p>
 * Value Object for Form historic information.
 * </p>
 *
 * @author jasonbruwer
 * @version v1.8
 * @see FormHistoricDataListing
 * @since v1.8 2018-01-26
 */
public class FormHistoricData extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String dateAndFieldName;

    @Getter
    @Setter
    private Date date;

    @Getter
    @Setter
    private Long dateTimestamp;

    @Getter
    @Setter
    private String formContainerFieldValuesJSON;

    @Getter
    @Setter
    private String logEntryType;

    @Getter
    @Setter
    private String historicEntryType;

    @Getter
    @Setter
    private Boolean isFieldDifferentFromPrevious;

    @Getter
    @Setter
    private Boolean isFieldTypeSignature;

    @Getter
    @Setter
    private Boolean isEscapeText;

    @Getter
    @Setter
    private String description;

    //------------- Relationships -------------//
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private Field field;

    //------------- For Create -------------//
    @Getter
    @Setter
    private Form formForAuditCreate;

    //------------- Static Labels -------------//
    private static final String CURRENT_LABEL = "Current";
    private static final String CURRENT_DESC = "The most recent field values for the form.";
    private static final String NA_LABEL = "-";

    /**
     * The type of {@code Form} log entry.
     */
    public static class HistoricEntryType {
        public static final String NULL = "Null";
        public static final String FORM_CONTAINER = "FormContainer";
        public static final String FORM_CONTAINER_LOG_ENTRY = "FormContainerLogEntry";
        public static final String FIELD_AND_VALUE = "FieldAndValue";
        public static final String FIELD_AND_VALUE_EXTENDED = "FieldAndValueExtended";
    }

    /**
     * The JSON mapping for the {@code FormHistoricData} object.
     */
    public static class JSONMapping {
        public static final String DATE_AND_FIELD_NAME = "dateAndFieldName";
        public static final String DATE = "date";
        public static final String FORM_CONTAINER_FIELD_VALUES_JSON = "formContainerFieldValuesJSON";
        public static final String LOG_ENTRY_TYPE = "logEntryType";
        public static final String DESCRIPTION = "description";
        public static final String HISTORIC_ENTRY_TYPE = "historicEntryType";
        public static final String USER = "user";
        public static final String FORM_FOR_AUDIT_CREATE = "formForAuditCreate";
        public static final String FIELD = "field";

        public static final String IS_FIELD_DIFFERENT_FROM_PREVIOUS = "isFieldDifferentFromPrevious";
        public static final String IS_FIELD_TYPE_SIGNATURE = "isFieldTypeSignature";
        public static final String IS_ESCAPE_TEXT = "isEscapeText";
    }

    /**
     * Default constructor.
     */
    public FormHistoricData() {
        super();
    }

    /**
     * Instance with audit data and date.
     *
     * @param fluidFormAuditData The {@code Form} audit data.
     * @param auditDate          The date the audit needs to be logged as.
     */
    public FormHistoricData(Form fluidFormAuditData, Date auditDate) {
        this();
        this.setFormForAuditCreate(fluidFormAuditData);
        this.setDate(auditDate);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormHistoricData(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setDate(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE));
        this.setDateAndFieldName(this.getAsStringNullSafe(JSONMapping.DATE_AND_FIELD_NAME));
        this.setFormContainerFieldValuesJSON(this.getAsStringNullSafe(JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON));
        this.setLogEntryType(this.getAsStringNullSafe(JSONMapping.LOG_ENTRY_TYPE));
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));
        this.setHistoricEntryType(this.getAsStringNullSafe(JSONMapping.HISTORIC_ENTRY_TYPE));
        this.setUser(this.extractObject(JSONMapping.USER, User::new));
        this.setFormForAuditCreate(this.extractObject(JSONMapping.FORM_FOR_AUDIT_CREATE, Form::new));
        this.setField(this.extractObject(JSONMapping.FIELD, Field::new));

        this.setIsFieldDifferentFromPrevious(this.getAsBooleanNullSafe(JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS));
        this.setIsFieldTypeSignature(this.getAsBooleanNullSafe(JSONMapping.IS_FIELD_TYPE_SIGNATURE));
        this.setIsEscapeText(this.getAsBooleanNullSafe(JSONMapping.IS_ESCAPE_TEXT));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.DATE, returnVal, this.getDateAsLongFromJson(this.getDate()));
        this.setAsProperty(JSONMapping.DATE_AND_FIELD_NAME, returnVal, this.getDateAndFieldName());
        this.setAsProperty(JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON, returnVal, this.getFormContainerFieldValuesJSON());
        this.setAsProperty(JSONMapping.LOG_ENTRY_TYPE, returnVal, this.getLogEntryType());
        this.setAsProperty(JSONMapping.DESCRIPTION, returnVal, this.getDescription());
        this.setAsProperty(JSONMapping.HISTORIC_ENTRY_TYPE, returnVal, this.getHistoricEntryType());
        this.setAsObj(JSONMapping.USER, returnVal, this::getUser);
        this.setAsObj(JSONMapping.FIELD, returnVal, this::getField);
        this.setAsProperty(JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS, returnVal, this.getIsFieldDifferentFromPrevious());
        this.setAsProperty(JSONMapping.IS_FIELD_TYPE_SIGNATURE, returnVal, this.getIsFieldTypeSignature());
        this.setAsProperty(JSONMapping.IS_ESCAPE_TEXT, returnVal, this.getIsEscapeText());
        this.setAsObj(JSONMapping.FORM_FOR_AUDIT_CREATE, returnVal, this::getFormForAuditCreate);
        return returnVal;
    }
}
