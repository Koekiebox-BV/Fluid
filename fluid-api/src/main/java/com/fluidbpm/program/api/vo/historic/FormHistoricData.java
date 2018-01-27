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

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.user.User;

/**
 * <p>
 * Value Object for Form historic information.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.8 2018-01-26
 * @version v1.8
 *
 * @see FormHistoricDataListing
 */
public class FormHistoricData extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String dateAndFieldName;
    private Date date;
    private String formContainerFieldValuesJSON;

    private String logEntryType;
    private String historicEntryType;
    
    private String description;

    private Boolean isFieldDifferentFromPrevious;
    private Boolean isFieldTypeSignature;
    private Boolean isEscapeText;

    //------------- Relationships -------------//
    private User user;
    private Field field;

    //------------- Static Labels -------------//
    private static final String CURRENT_LABEL = "Current";
    private static final String CURRENT_DESC = "The most recent field values for the form.";
    private static final String NA_LABEL = "-";

    /**
     * The type of {@code Form} log entry.
     */
    public static class HistoricEntryType{

        public static final String NULL = "Null";
        public static final String FORM_CONTAINER = "FormContainer";
        public static final String FORM_CONTAINER_LOG_ENTRY = "FormContainerLogEntry";
        public static final String FIELD_AND_VALUE = "FieldAndValue";
        public static final String FIELD_AND_VALUE_EXTENDED = "FieldAndValueExtended";
    }
    
    /**
     * The JSON mapping for the {@code FormHistoricData} object.
     */
    public static class JSONMapping
    {
        public static final String DATE_AND_FIELD_NAME = "dateAndFieldName";
        public static final String DATE = "date";
        public static final String FORM_CONTAINER_FIELD_VALUES_JSON = "formContainerFieldValuesJSON";
        public static final String LOG_ENTRY_TYPE = "logEntryType";
        public static final String DESCRIPTION = "description";
        public static final String HISTORIC_ENTRY_TYPE = "historicEntryType";
        public static final String USER = "user";
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
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormHistoricData(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Date...
        this.setDate(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE));

        //Date and Time Field Name...
        if (!this.jsonObject.isNull(JSONMapping.DATE_AND_FIELD_NAME)) {
            this.setDateAndFieldName(this.jsonObject.getString(
                    JSONMapping.DATE_AND_FIELD_NAME));
        }

        //Form Container Field values JSON...
        if (!this.jsonObject.isNull(JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON)) {
            this.setFormContainerFieldValuesJSON(this.jsonObject.getString(
                    JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON));
        }
        
        //Log Entry Type...
        if (!this.jsonObject.isNull(JSONMapping.LOG_ENTRY_TYPE)) {
            this.setLogEntryType(this.jsonObject.getString(
                    JSONMapping.LOG_ENTRY_TYPE));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(
                    JSONMapping.DESCRIPTION));
        }

        //Historic Entry Type...
        if (!this.jsonObject.isNull(JSONMapping.HISTORIC_ENTRY_TYPE)) {
            this.setHistoricEntryType(this.jsonObject.getString(
                    JSONMapping.HISTORIC_ENTRY_TYPE));
        }

        //User...
        if (!this.jsonObject.isNull(JSONMapping.USER)) {
            this.setUser(new User(this.jsonObject.getJSONObject(
                    JSONMapping.USER)));
        }

        //Field...
        if (!this.jsonObject.isNull(JSONMapping.FIELD)) {
            this.setField(new Field(this.jsonObject.getJSONObject(
                    JSONMapping.FIELD)));
        }

        //Field Different from Previous...
        if (!this.jsonObject.isNull(JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS)) {
            this.setFieldDifferentFromPrevious(this.jsonObject.getBoolean(
                    JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS));
        }

        //Field Type Signature...
        if (!this.jsonObject.isNull(JSONMapping.IS_FIELD_TYPE_SIGNATURE)) {
            this.setFieldTypeSignature(this.jsonObject.getBoolean(
                    JSONMapping.IS_FIELD_TYPE_SIGNATURE));
        }

        //Escape Text...
        if (!this.jsonObject.isNull(JSONMapping.IS_ESCAPE_TEXT)) {
            this.setEscapeText(
                    this.jsonObject.getBoolean(
                            JSONMapping.IS_ESCAPE_TEXT));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricData}.
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Date...
        if(this.getDate() != null)
        {
            returnVal.put(JSONMapping.DATE,
                    this.getDateAsLongFromJson(this.getDate()));
        }

        //Date and Time Field Name...
        if(this.getDateAndFieldName() != null)
        {
            returnVal.put(JSONMapping.DATE_AND_FIELD_NAME,
                    this.getDateAndFieldName());
        }

        //Form Container Field Values JSON...
        if(this.getFormContainerFieldValuesJSON() != null)
        {
            returnVal.put(JSONMapping.FORM_CONTAINER_FIELD_VALUES_JSON,
                    this.getFormContainerFieldValuesJSON());
        }

        //Log Entry Type...
        if(this.getLogEntryType() != null)
        {
            returnVal.put(JSONMapping.LOG_ENTRY_TYPE,
                    this.getLogEntryType());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION,
                    this.getDescription());
        }

        //Historic Entry Type...
        if(this.getHistoricEntryType() != null)
        {
            returnVal.put(JSONMapping.HISTORIC_ENTRY_TYPE,
                    this.getHistoricEntryType());
        }

        //User...
        if(this.getUser() != null)
        {
            returnVal.put(JSONMapping.USER,
                    this.getUser().toJsonObject());
        }

        //Field...
        if(this.getField() != null)
        {
            returnVal.put(JSONMapping.FIELD,
                    this.getField().toJsonObject());
        }

        //Different from Previous...
        if(this.isFieldDifferentFromPrevious() != null)
        {
            returnVal.put(JSONMapping.IS_FIELD_DIFFERENT_FROM_PREVIOUS,
                    this.isFieldDifferentFromPrevious());
        }

        //Field type Signature...
        if(this.isFieldTypeSignature() != null)
        {
            returnVal.put(JSONMapping.IS_FIELD_TYPE_SIGNATURE,
                    this.isFieldTypeSignature());
        }

        //Escape Text...
        if(this.isEscapeText() != null)
        {
            returnVal.put(
                    JSONMapping.IS_ESCAPE_TEXT,
                    this.isEscapeText());
        }

        return returnVal;
    }

    /**
     * Gets Date.
     *
     * @return {@code Date} of when Historic entry.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Sets Date.
     *
     * @param dateParam {@code Date} of when Historic entry.
     */
    public void setDate(Date dateParam) {
        this.date = dateParam;
    }

    /**
     * Gets the Date and Field name.
     *
     * @return Date and Field name.
     */
    public String getDateAndFieldName() {
        return this.dateAndFieldName;
    }

    /**
     * Sets Date and Field name.
     *
     * @param dateAndFieldNameParam Date and Field name.
     */
    public void setDateAndFieldName(String dateAndFieldNameParam) {
        this.dateAndFieldName = dateAndFieldNameParam;
    }

    /**
     * Gets the Field values as {@code JSON}.
     *
     * @return JSON value of form field.
     */
    public String getFormContainerFieldValuesJSON() {
        return this.formContainerFieldValuesJSON;
    }

    /**
     * Sets the Field values as {@code JSON}.
     *
     * @param formContainerFieldValuesJSONParam JSON value of form field.
     */
    public void setFormContainerFieldValuesJSON(String formContainerFieldValuesJSONParam) {
        this.formContainerFieldValuesJSON = formContainerFieldValuesJSONParam;
    }

    /**
     * Gets the Historic Entry Type.
     *
     * @return Historic entry type.
     */
    public String getHistoricEntryType() {
        return this.historicEntryType;
    }

    /**
     * Sets the Historic Entry Type.
     *
     * @param historicEntryTypeParam Historic entry type.
     */
    public void setHistoricEntryType(String historicEntryTypeParam) {
        this.historicEntryType = historicEntryTypeParam;
    }

    /**
     * Gets whether field is different from previous.
     *
     * @return Whether record has changed value.
     */
    public Boolean isFieldDifferentFromPrevious() {
        return this.isFieldDifferentFromPrevious;
    }

    /**
     * Gets whether field is different from previous.
     *
     * @return Whether record has changed value.
     */
    public Boolean getFieldDifferentFromPrevious() {
        return this.isFieldDifferentFromPrevious;
    }

    /**
     * Sets whether field is different from previous.
     *
     * @param fieldDifferentFromPreviousParam Whether record has changed value.
     */
    public void setFieldDifferentFromPrevious(
            Boolean fieldDifferentFromPreviousParam) {

        this.isFieldDifferentFromPrevious = fieldDifferentFromPreviousParam;
    }

    /**
     * Gets whether field is of type signature.
     *
     * @return Whether record is of type signature.
     */
    public Boolean isFieldTypeSignature() {
        return this.isFieldTypeSignature;
    }

    /**
     * Gets whether field is of type signature.
     *
     * @return Whether record is of type signature.
     */
    public Boolean getFieldTypeSignature() {
        return this.isFieldTypeSignature;
    }

    /**
     * Sets whether field is of type signature.
     *
     * @param fieldTypeSignatureParam Whether record is of type signature.
     */
    public void setFieldTypeSignature(Boolean fieldTypeSignatureParam) {
        this.isFieldTypeSignature = fieldTypeSignatureParam;
    }

    /**
     * Gets whether field should be escaped.
     *
     * @return Whether field {@code HTML} should be escaped.
     */
    public Boolean isEscapeText() {
        return this.isEscapeText;
    }

    /**
     * Gets whether field should be escaped.
     *
     * @return Whether field {@code HTML} should be escaped.
     */
    public Boolean getEscapeText() {
        return this.isEscapeText;
    }

    /**
     * Sets whether field should be escaped.
     *
     * @param escapeTextParam Whether field {@code HTML} should be escaped.
     */
    public void setEscapeText(Boolean escapeTextParam) {
        this.isEscapeText = escapeTextParam;
    }

    /**
     * Gets Type of Historic entry.
     *
     * @return Type of historic log entry.
     */
    public String getLogEntryType() {
        return this.logEntryType;
    }

    /**
     * Sets Type of Historic entry.
     *
     * @param logEntryTypeParam Type of historic log entry.
     */
    public void setLogEntryType(String logEntryTypeParam) {
        this.logEntryType = logEntryTypeParam;
    }

    /**
     * Gets Description.
     *
     * @return Description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets Description.
     *
     * @param descriptionParam Description.
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
    }

    
    /**
     * Gets {@code User} responsible for historic entry.
     *
     * @return Person or User responsible for historic data.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets {@code User} responsible for historic entry.
     *
     * @param userParam Person or User responsible for historic data.
     */
    public void setUser(User userParam) {
        this.user = userParam;
    }

    /**
     * Gets {@code Field} historic entry belongs to.
     *
     * @return The {@code Field} returned.
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Sets {@code Field} historic entry belongs to.
     *
     * @param fieldParam The {@code Field} to set.
     */
    public void setField(Field fieldParam) {
        this.field = fieldParam;
    }
}
