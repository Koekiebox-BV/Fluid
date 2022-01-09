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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseListing;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.item.FluidItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     Represents a Query that can be executed via the
 *     API. The {@code UserQuery}s are configured from the
 *     Administration.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 */
public class UserQuery extends ABaseListing<FluidItem> {
	public static final long serialVersionUID = 1L;

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
	 * @param name UserQuery Name.
	 */
	public UserQuery(Long userQueryId, String name) {
		super();
		this.setId(userQueryId);
		this.setName(name);
	}

	/**
	 * Easy access to execute user query.
	 * @param name Name of query to execute.
	 * @param inputs Input values for query.
	 */
	public UserQuery(String name, List<Field> inputs) {
		super();
		this.setName(name);
		this.setInputs(inputs);
	}

	/**
	 * Easy access to execute user query.
	 * @param name Name of query to execute.
	 * @param inputs Input values for query.
	 */
	public UserQuery(String name, Field ... inputs) {
		super();
		this.setName(name);
		List<Field> inputsList = new ArrayList<>();
		if (inputs != null && inputs.length > 0) {
			for (Field toAdd : inputs) {
				inputsList.add(toAdd);
			}
		}
		this.setInputs(inputsList);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public UserQuery(JSONObject jsonObjectParam){
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		//Name...
		if (!this.jsonObject.isNull(JSONMapping.NAME)) {
			this.setName(this.jsonObject.getString(JSONMapping.NAME));
		}

		//Description...
		if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
			this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
		}

		//Inputs...
		if (!this.jsonObject.isNull(JSONMapping.INPUTS)) {
			JSONArray fieldsArr = this.jsonObject.getJSONArray(
					JSONMapping.INPUTS);
			List<Field> assFields = new ArrayList();
			for (int index = 0;index < fieldsArr.length();index++) {
				assFields.add(new Field(fieldsArr.getJSONObject(index)));
			}
			this.setInputs(assFields);
		}

		//Rules...
		if (!this.jsonObject.isNull(JSONMapping.RULES)) {
			JSONArray rulesArr = this.jsonObject.getJSONArray(
					JSONMapping.RULES);
			List<String> rules = new ArrayList();
			for (int index = 0;index < rulesArr.length();index++) {
				rules.add(rulesArr.getString(index));
			}
			this.setRules(rules);
		}

		//Date Created...
		this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));

		//Date Last Updated...
		this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));
	}

	/**
	 * Gets {@code UserQuery} name.
	 *
	 * @return A {@code UserQuery}s name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets {@code UserQuery} name.
	 *
	 * @param nameParam A {@code UserQuery} name.
	 */
	public void setName(String nameParam) {
		this.name = nameParam;
	}

	/**
	 * Gets {@code UserQuery} description.
	 *
	 * @return A {@code UserQuery}s description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets {@code UserQuery} description.
	 *
	 * @param descriptionParam A {@code UserQuery}s description.
	 */
	public void setDescription(String descriptionParam) {
		this.description = descriptionParam;
	}

	/**
	 * Sets {@code UserQuery} input {@code Field}s.
	 *
	 * @return A {@code UserQuery}s input {@code Field}s.
	 *
	 * @see Field
	 */
	public List<Field> getInputs() {
		return this.inputs;
	}

	/**
	 * Sets {@code UserQuery} input {@code Field}s.
	 *
	 * @param inputsParam A {@code UserQuery}s input {@code Field}s.
	 *
	 * @see Field
	 */
	public void setInputs(List<Field> inputsParam) {
		this.inputs = inputsParam;
	}

	/**
	 * Sets {@code UserQuery} rules.
	 *
	 * @return A {@code UserQuery}s rules.
	 */
	public List<String> getRules() {
		return this.rules;
	}

	/**
	 * Sets {@code UserQuery} rules.
	 *
	 * @param rulesParam A {@code UserQuery}s rules.
	 */
	public void setRules(List<String> rulesParam) {
		this.rules = rulesParam;
	}

	/**
	 * Gets The {@code Date} the User Query
	 * was created.
	 *
	 * @return Date Created.
	 */
	public Date getDateCreated() {
		return this.dateCreated;
	}

	/**
	 * Sets The {@code Date} the User Query
	 * was created.
	 *
	 * @param dateCreatedParam Date Created.
	 */
	public void setDateCreated(Date dateCreatedParam) {
		this.dateCreated = dateCreatedParam;
	}

	/**
	 * Gets The {@code Date} the User Query
	 * was last updated.
	 *
	 * @return Date Last Updated.
	 */
	public Date getDateLastUpdated() {
		return this.dateLastUpdated;
	}

	/**
	 * Sets The {@code Date} the User Query
	 * was last updated.
	 *
	 * @param dateLastUpdatedParam Date Last Updated.
	 */
	public void setDateLastUpdated(Date dateLastUpdatedParam) {
		this.dateLastUpdated = dateLastUpdatedParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code UserQuery}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {

		JSONObject returnVal = super.toJsonObject();

		//Name...
		if (this.getName() != null) returnVal.put(JSONMapping.NAME,this.getName());

		//Description...
		if (this.getDescription() != null) returnVal.put(JSONMapping.DESCRIPTION,this.getDescription());

		//Inputs...
		if (this.getInputs() != null) {
			JSONArray jsonArray = new JSONArray();
			for (Field toAdd : this.getInputs()) jsonArray.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.INPUTS, jsonArray);
		}

		//Rules...
		if (this.getRules() != null) {
			JSONArray jsonArray = new JSONArray();
			for (String toAdd : this.getRules()) jsonArray.put(toAdd);
			returnVal.put(JSONMapping.RULES, jsonArray);
		}

		//Date Created...
		if (this.getDateCreated() != null)
			returnVal.put(JSONMapping.DATE_CREATED,
					this.getDateAsLongFromJson(this.getDateCreated()));

		//Date Last Updated...
		if (this.getDateLastUpdated() != null)
			returnVal.put(JSONMapping.DATE_LAST_UPDATED,
					this.getDateAsLongFromJson(this.getDateLastUpdated()));

		return returnVal;
	}

	/**
	 * Converts the {@code jsonObjectParam} to a {@code FluidItem} object.
	 *
	 * @param jsonObjectParam The JSON object to convert to {@code T}.
	 * @return new instance of {@code FluidItem}.
	 */
	@Override
	public FluidItem getObjectFromJSONObject(JSONObject jsonObjectParam) {
		return new FluidItem(jsonObjectParam);
	}

	/**
	 * Extract the field names where the value of the rule is of type {@code INPUT_VALUE}.
	 * @return List of {@code FormField} names.
	 */
	@XmlTransient
	public List<String> getRuleFieldNamesWhereTypeInput() {
		if (this.getRules() == null || this.getRules().isEmpty()) return new ArrayList<>();

		List<String> formFieldNamesWhereValueTypeInput = new ArrayList<>();

		for (String rule : this.getRules()) {
			int firstIndexOfBracket = rule.indexOf(LEFT_SQ_BRACKET);
			int lastIndexOfBracket =rule.indexOf(RIGHT_SQ_BRACKET);

			String formFieldName = rule.substring(firstIndexOfBracket,lastIndexOfBracket+1);
			String[] wordsInRule = rule.split(UtilGlobal.REG_EX_SPACE);
			if (wordsInRule == null || wordsInRule.length == 0) continue;

			String lastVal = wordsInRule[wordsInRule.length - 1].toUpperCase().trim();
			if (INPUT_VALUE.equals(lastVal)) formFieldNamesWhereValueTypeInput.add(formFieldName);
		}
		return formFieldNamesWhereValueTypeInput;
	}
}
