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
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
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
        this.setDateAndFieldName(this.getAsStringNullSafe(this.jsonObject, JSONMapping.DATE_AND_FIELD_NAME));
        this.setFormContainerFieldValuesJSON(this.getAsStringNullSafe(this.jsonObject, JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON));
        this.setLogEntryType(this.getAsStringNullSafe(this.jsonObject, JSONMapping.LOG_ENTRY_TYPE));
        this.setDescription(this.getAsStringNullSafe(this.jsonObject, JSONMapping.DESCRIPTION));
        this.setHistoricEntryType(this.getAsStringNullSafe(this.jsonObject, JSONMapping.HISTORIC_ENTRY_TYPE));
        this.setUser(this.extractObject(this.jsonObject, JSONMapping.USER, User::new));
        this.setFormForAuditCreate(this.extractObject(this.jsonObject, JSONMapping.FORM_FOR_AUDIT_CREATE, Form::new));
        this.setField(this.extractObject(this.jsonObject, JSONMapping.FIELD, Field::new));

        this.setIsFieldDifferentFromPrevious(this.getAsBooleanNullSafe(this.jsonObject, JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS));
        this.setIsFieldTypeSignature(this.getAsBooleanNullSafe(this.jsonObject, JSONMapping.IS_FIELD_TYPE_SIGNATURE));
        this.setIsEscapeText(this.getAsBooleanNullSafe(this.jsonObject, JSONMapping.IS_ESCAPE_TEXT));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() throws JSONException {
        JsonObject returnVal = super.toJsonObject();

        //Date...
        if (this.getDate() != null) {
            returnVal.addProperty(JSONMapping.DATE, this.getDateAsLongFromJson(this.getDate()));
        }

        //Date and Time Field Name...
        if (this.getDateAndFieldName() != null) {
            returnVal.addProperty(JSONMapping.DATE_AND_FIELD_NAME, this.getDateAndFieldName());
        }

        //Form Container Field Values JSON...
        if (this.getFormContainerFieldValuesJSON() != null) {
            returnVal.addProperty(JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON, this.getFormContainerFieldValuesJSON());
        }

        //Log Entry Type...
        if (this.getLogEntryType() != null) {
            returnVal.addProperty(JSONMapping.LOG_ENTRY_TYPE, this.getLogEntryType());
        }

        //Description...
        if (this.getDescription() != null) {
            returnVal.addProperty(JSONMapping.DESCRIPTION, this.getDescription());
        }

        //Historic Entry Type...
        if (this.getHistoricEntryType() != null) {
            returnVal.addProperty(JSONMapping.HISTORIC_ENTRY_TYPE, this.getHistoricEntryType());
        }

        //User...
        if (this.getUser() != null) {
            returnVal.add(JSONMapping.USER, this.getUser().toJsonObject());
        }

        //Field...
        if (this.getField() != null) {
            returnVal.add(JSONMapping.FIELD, this.getField().toJsonObject());
        }

        //Different from Previous...
        if (this.getIsFieldDifferentFromPrevious() != null) {
            returnVal.addProperty(JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS, this.getIsFieldDifferentFromPrevious());
        }

        //Field type Signature...
        if (this.getIsFieldTypeSignature() != null) {
            returnVal.addProperty(JSONMapping.IS_FIELD_TYPE_SIGNATURE, this.getIsFieldTypeSignature());
        }

        //Escape Text...
        if (this.getIsEscapeText() != null) {
            returnVal.addProperty(JSONMapping.IS_ESCAPE_TEXT, this.getIsEscapeText());
        }

        //Form for Audit Create...
        if (this.getFormForAuditCreate() != null) {
            returnVal.add(JSONMapping.FORM_FOR_AUDIT_CREATE, this.getFormForAuditCreate().toJsonObject());
        }
        return returnVal;
    }
}
