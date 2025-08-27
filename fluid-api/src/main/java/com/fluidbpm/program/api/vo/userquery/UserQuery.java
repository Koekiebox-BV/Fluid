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

package com.fluidbpm.program.api.vo.userquery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Represents a Query that can be executed via the
 * API. The {@code UserQuery}s are configured from the
 * Administration.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 */
@Getter
@Setter
public class UserQuery extends ABaseGSONListing<FluidItem> {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    private List<String> rules;
    private List<Field> inputs;

    private Date dateCreated;
    private Date dateLastUpdated;

    private static final String LEFT_SQ_BRACKET = "[";
    private static final String RIGHT_SQ_BRACKET = "]";
    private static final String INPUT_VALUE = "INPUT_VALUE";

    /**
     * The JSON mapping for the {@code UserQuery} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String INPUTS = "inputs";
        public static final String RULES = "rules";

        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
    }

    /**
     * Default constructor.
     */
    public UserQuery() {
        super();
    }

    /**
     * Sets the Id associated with a 'User Query'.
     *
     * @param userQueryId UserQuery Id.
     */
    public UserQuery(Long userQueryId) {
        super();
        this.setId(userQueryId);
    }

    /**
     * Sets the Id and name associated with a 'User Query'.
     *
     * @param userQueryId UserQuery Id.
     * @param name        UserQuery Name.
     */
    public UserQuery(Long userQueryId, String name) {
        super();
        this.setId(userQueryId);
        this.setName(name);
    }

    /**
     * Easy access to execute user query.
     *
     * @param name   Name of query to execute.
     * @param inputs Input values for query.
     */
    public UserQuery(String name, List<Field> inputs) {
        super();
        this.setName(name);
        this.setInputs(inputs);
    }

    /**
     * Easy access to execute user query.
     *
     * @param name   Name of query to execute.
     * @param inputs Input values for query.
     */
    public UserQuery(String name, Field... inputs) {
        super();
        this.setName(name);
        List<Field> inputsList = new ArrayList<>();
        if (inputs != null) inputsList.addAll(Arrays.asList(inputs));

        this.setInputs(inputsList);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public UserQuery(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setDescription(this.getAsStringNullSafe(JSONMapping.DESCRIPTION));
        this.setInputs(this.extractObjects(JSONMapping.INPUTS, Field::new));
        this.setRules(this.extractStrings(JSONMapping.RULES));
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code UserQuery}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.NAME, returnVal, this.getName());
        this.setAsProperty(JSONMapping.DESCRIPTION, returnVal, this.getDescription());
        this.setAsObjArray(JSONMapping.INPUTS, returnVal, this::getInputs);
        this.setAsStringArray(JSONMapping.RULES, returnVal, this.getRules());
        this.setAsProperty(JSONMapping.DATE_CREATED, returnVal, (this.getDateCreated()));
        this.setAsProperty(JSONMapping.DATE_LAST_UPDATED, returnVal, (this.getDateLastUpdated()));
        return returnVal;
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code FluidItem} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code T}.
     * @return new instance of {@code FluidItem}.
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public FluidItem getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new FluidItem(jsonObjectParam);
    }

    /**
     * Extract the field names where the value of the rule is of type {@code INPUT_VALUE}.
     *
     * @return List of {@code FormField} names.
     */
    @XmlTransient
    @JsonIgnore
    public List<String> getRuleFieldNamesWhereTypeInput() {
        if (this.getRules() == null || this.getRules().isEmpty()) return new ArrayList<>();

        List<String> formFieldNamesWhereValueTypeInput = new ArrayList<>();

        for (String rule : this.getRules()) {
            int firstIndexOfBracket = rule.indexOf(LEFT_SQ_BRACKET);
            int lastIndexOfBracket = rule.indexOf(RIGHT_SQ_BRACKET);

            String formFieldName = rule.substring(firstIndexOfBracket, lastIndexOfBracket + 1);
            String[] wordsInRule = rule.split(UtilGlobal.REG_EX_SPACE);
            if (wordsInRule == null || wordsInRule.length == 0) continue;

            String lastVal = wordsInRule[wordsInRule.length - 1].toUpperCase().trim();
            if (INPUT_VALUE.equals(lastVal)) formFieldNamesWhereValueTypeInput.add(formFieldName);
        }
        return formFieldNamesWhereValueTypeInput;
    }
}
