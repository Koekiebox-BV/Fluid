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

package com.fluidbpm.program.api.vo.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *     Represents a {@code MultiChoice} value at any level (Form, Route, User and Global).
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 * @version v1.8
 *
 * @see Field
 * @see Field#setFieldValue(Object)
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class MultiChoice extends ABaseFluidJSONObject {
	public static final long serialVersionUID = 1L;

	private List<String> availableMultiChoices;
	private String availableMultiChoicesCombined;

	private List<String> selectedMultiChoices;
	private String selectedMultiChoicesCombined;

	/**
	 * The JSON mapping for the {@code MultiChoice} object.
	 */
	public static class JSONMapping {
		public static final String AVAILABLE_MULTI_CHOICES = "availableMultiChoices";
		public static final String SELECTED_MULTI_CHOICES = "selectedMultiChoices";

		public static final String AVAILABLE_CHOICES = "availableChoices";
		public static final String SELECTED_CHOICES = "selectedChoices";

		public static final String AVAILABLE_CHOICES_COMBINED = "availableChoicesCombined";
		public static final String SELECTED_CHOICES_COMBINED = "selectedChoicesCombined";

		//For Payara mapping of [type] and [value]...
		public static final String TYPE = "type";
		public static final String TYPE_STRING = "string";
		public static final String VALUE = "value";
	}

	/**
	 * Local available constructor for cloning {@code this}.
	 * @param toClone The {@code MultiChoice} to clone.
	 */
	private MultiChoice(MultiChoice toClone) {
		this();
		if (toClone == null) return;
		this.setId(toClone.getId());
		this.setAvailableMultiChoices(toClone.getAvailableMultiChoices() == null ? null :
						new ArrayList<>(toClone.getAvailableMultiChoices()));
		this.setSelectedMultiChoices(toClone.getSelectedMultiChoices() == null ? null :
						new ArrayList<>(toClone.getSelectedMultiChoices()));
		this.availableMultiChoicesCombined = toClone.availableMultiChoicesCombined;
		this.selectedMultiChoicesCombined = toClone.selectedMultiChoicesCombined;
	}

	/**
	 * Default constructor.
	 */
	public MultiChoice() {
		super();
	}

	/**
	 * Sets the Selected MultiChoices for when multiple selections are supported.
	 *
	 * @param selectedMultiChoicesParam List of Selected {@code MultiChoices}s.
	 */
	public MultiChoice(List<String> selectedMultiChoicesParam) {
		this.selectedMultiChoices = selectedMultiChoicesParam;
	}

	/**
	 * Sets the Selected and Available MultiChoices for when multiple selections are supported.
	 *
	 * @param selectedMultiChoicesParam List of Selected {@code MultiChoices}s.
	 * @param availableMultiChoicesParam List of Available {@code MultiChoices}s.
	 */
	public MultiChoice(List<String> selectedMultiChoicesParam, List<String> availableMultiChoicesParam) {
		this.selectedMultiChoices = selectedMultiChoicesParam;
		this.availableMultiChoices = availableMultiChoicesParam;
	}

	/**
	 * Sets the Selected MultiChoices for when multiple selections are supported.
	 *
	 * @param selectedMultiChoiceValueParam Selected {@code MultiChoices} {@code String} value.
	 */
	public MultiChoice(String selectedMultiChoiceValueParam) {
		this.selectedMultiChoices = new ArrayList();
		if (selectedMultiChoiceValueParam != null) this.selectedMultiChoices.add(selectedMultiChoiceValueParam);
	}

	/**
	 * Sets the Selected MultiChoices for when multiple selections are supported.
	 *
	 * @param selectedMultiChoiceValuesParam Selected {@code MultiChoices} {@code String[]} value.
	 */
	public MultiChoice(String ... selectedMultiChoiceValuesParam) {
		this.selectedMultiChoices = new ArrayList();
		if (selectedMultiChoiceValuesParam != null)
			for (String str : selectedMultiChoiceValuesParam) this.selectedMultiChoices.add(str);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public MultiChoice(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		//Available Multiple Choices...
		if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_MULTI_CHOICES)) {
			JSONArray arrayOfString =
					this.jsonObject.getJSONArray(JSONMapping.AVAILABLE_MULTI_CHOICES);

			this.availableMultiChoices = new ArrayList();
			for (int index = 0;index < arrayOfString.length();index++) {
				this.availableMultiChoices.add(arrayOfString.getString(index));
			}
		} else if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_CHOICES)) {
			JSONArray arrayOfString =
					this.jsonObject.getJSONArray(JSONMapping.AVAILABLE_CHOICES);
			this.availableMultiChoices = new ArrayList();
			for (int index = 0;index < arrayOfString.length();index++) {
				this.availableMultiChoices.add(arrayOfString.getString(index));
			}
		} else if (!this.jsonObject.isNull(JSONMapping.AVAILABLE_CHOICES_COMBINED)) {
			String combinedAvailChoices =
					this.jsonObject.getString(JSONMapping.AVAILABLE_CHOICES_COMBINED);
			if (combinedAvailChoices != null && !combinedAvailChoices.isEmpty()) {
				this.availableMultiChoices = new ArrayList();

				String[] pipeSplit = combinedAvailChoices.split(UtilGlobal.REG_EX_PIPE);
				if (pipeSplit != null && pipeSplit.length > 0)
					for (int index = 0;index < pipeSplit.length;index++) this.availableMultiChoices.add(pipeSplit[index]);
			}
		}

		//Avail - Populate combined...
		if (this.availableMultiChoices != null)
			this.availableMultiChoicesCombined = this.combineStringArrayWith(this.availableMultiChoices, UtilGlobal.PIPE);

		//Selected Multiple Choices...
		if (!this.jsonObject.isNull(JSONMapping.SELECTED_MULTI_CHOICES)) {
			JSONArray arrayOfString =
					this.jsonObject.getJSONArray(JSONMapping.SELECTED_MULTI_CHOICES);
			this.selectedMultiChoices = new ArrayList();

			for (int index = 0;index < arrayOfString.length();index++)
				this.selectedMultiChoices.add(arrayOfString.getString(index));

		} else if (!this.jsonObject.isNull(JSONMapping.SELECTED_CHOICES)) {
			JSONArray arrayOfString =
					this.jsonObject.getJSONArray(JSONMapping.SELECTED_CHOICES);

			this.selectedMultiChoices = new ArrayList();
			for (int index = 0;index < arrayOfString.length();index++)
				this.selectedMultiChoices.add(arrayOfString.getString(index));
		} else if (!this.jsonObject.isNull(JSONMapping.SELECTED_CHOICES_COMBINED)) {
			String combinedSelectedChoices =
					this.jsonObject.getString(JSONMapping.SELECTED_CHOICES_COMBINED);
			if (combinedSelectedChoices != null && !combinedSelectedChoices.isEmpty()) {
				this.selectedMultiChoices = new ArrayList();
				String[] pipeSplit = combinedSelectedChoices.split(UtilGlobal.REG_EX_PIPE);
				if (pipeSplit != null && pipeSplit.length > 0)
					for (int index = 0;index < pipeSplit.length;index++) this.selectedMultiChoices.add(pipeSplit[index]);
			}
		}

		//Selected - Populate combined...
		if (this.selectedMultiChoices != null)
			this.selectedMultiChoicesCombined = this.combineStringArrayWith(this.selectedMultiChoices, UtilGlobal.PIPE);
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code MultiChoice}.
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Available...
		if (this.getAvailableMultiChoices() != null) {
			List<String> availChoices = this.getAvailableMultiChoices();
			returnVal.put(JSONMapping.AVAILABLE_MULTI_CHOICES,
					new JSONArray(availChoices.toArray()));
			returnVal.put(JSONMapping.AVAILABLE_CHOICES,
					new JSONArray(availChoices.toArray()));
			returnVal.put(JSONMapping.AVAILABLE_CHOICES_COMBINED,
					this.combineStringArrayWith(availChoices, UtilGlobal.PIPE)
			);
		}

		//Selected...
		if (this.getSelectedMultiChoices() != null) {
			List<String> selectChoices = this.getSelectedMultiChoices();
			returnVal.put(JSONMapping.SELECTED_MULTI_CHOICES,
					new JSONArray(selectChoices.toArray()));
			returnVal.put(JSONMapping.SELECTED_CHOICES,
					new JSONArray(selectChoices.toArray()));
			returnVal.put(JSONMapping.SELECTED_CHOICES_COMBINED,
					this.combineStringArrayWith(selectChoices, UtilGlobal.PIPE)
			);
		}

		return returnVal;
	}

	/**
	 * Constructs a {@code String} value for the selected MultiChoices.
	 *
	 * @return Comma separated list of Selected MultiChoices.
	 *
	 * Have a look at {@code MultiChoice#getSelectedMultiChoices()}.
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public String toString() {
		return this.combineStringArrayWith(this.getSelectedMultiChoices(), UtilGlobal.COMMA_SPACE);
	}

	/**
	 * Gets Selected MultiChoice.
	 *
	 * @return {@code String} Single value of selected multi choice.
	 */
	public String getSelectedMultiChoice() {
		if (this.selectedMultiChoices == null) this.selectedMultiChoices = new ArrayList<>();
		if (this.selectedMultiChoices.isEmpty()) return null;
		return this.selectedMultiChoices.get(0);
	}

	/**
	 * Gets Selected MultiChoice.
	 *
	 * @return {@code String} Single value of selected multi choice.
	 */
	@XmlTransient
	@JsonIgnore
	public String getSelectedMultiChoicesTxt() {
		if (this.selectedMultiChoices == null) this.selectedMultiChoices = new ArrayList<>();
		if (this.selectedMultiChoices.isEmpty()) return null;

		return this.selectedMultiChoices.stream().collect(Collectors.joining(", "));
	}

	/**
	 * Verify whether the selected values are empty.
	 * 
	 * @return {@code true} if empty, otherwise {@code false}.
	 */
	@XmlTransient
	@JsonIgnore
	public boolean isSelectedValuesEmpty() {
		return  (this.getSelectedMultiChoices() == null || this.getSelectedMultiChoices().isEmpty());
	}

	/**
	 * Sets Selected MultiChoice.
	 *
	 * @param selectedMultiChoice Singe value for selected multi choices.
	 */
	public void setSelectedMultiChoice(String selectedMultiChoice) {
		if (this.selectedMultiChoices == null) this.selectedMultiChoices = new ArrayList<>();
		this.selectedMultiChoices.clear();

		if (selectedMultiChoice == null) return;
		this.selectedMultiChoices.add(selectedMultiChoice);
	}

	/**
	 * Combine {@code listToCombineParam} into a single {@code String}.
	 *
	 * @param listToCombineParam To combine.
	 * @param separatorCharsParam The char used to separate with.
	 *
	 * @return The combined text value.
	 */
	@XmlTransient
	@JsonIgnore
	public String combineStringArrayWith(List<String> listToCombineParam, String separatorCharsParam) {
		String returnValue = UtilGlobal.EMPTY;

		int lengthOfSepChars = (separatorCharsParam == null) ? 0 : separatorCharsParam.length();
		if (listToCombineParam != null && !listToCombineParam.isEmpty()) {
			StringBuffer concatBuffer = new StringBuffer();
			for (String toAdd : listToCombineParam) {
				concatBuffer.append(toAdd);
				concatBuffer.append(separatorCharsParam);
			}

			String concatString = concatBuffer.toString();
			returnValue = concatString.substring(0, concatString.length() - lengthOfSepChars);
		}

		return returnValue;
	}

	/**
	 * @return Cloned object from {@code this}
	 */
	@XmlTransient
	@JsonIgnore
	public MultiChoice cloneMultiChoice() {
		return new MultiChoice(this);
	}

	/**
	 * @return Cloned object from {@code this}
	 */
	@XmlTransient
	@JsonIgnore
	@Override
	public MultiChoice clone() {
		return new MultiChoice(this);
	}

	/**
	 * JSF getter for clone MultiChoice.
	 * @return {@code this#cloneMultiChoice}
	 */
	@XmlTransient
	@JsonIgnore
	public MultiChoice getCloneMultiChoice() {
		return this.cloneMultiChoice();
	}

	/**
	 * Compare this multi choice selected values against {@code compareAgainst}.
	 * 
	 * @param compareAgainst The multi-choice to compare against.
	 * @return {@code true} if {@code this} is equal to {@code compareAgainst}
	 */
	@XmlTransient
	@JsonIgnore
	public boolean selectedEquals(MultiChoice compareAgainst) {
		if (compareAgainst == null) return false;

		List<String> selected = this.getSelectedMultiChoices(), compareSelected = compareAgainst.getSelectedMultiChoices();
		if (selected != null && compareSelected != null) {
			if (selected.size() != compareSelected.size()) return false;

			for (int index = 0;index < selected.size();index++) {
				if (!selected.get(index).equals(compareSelected.get(index))) return false;
			}
			return true;
		} else if (this.getSelectedMultiChoice() != null && compareAgainst.getSelectedMultiChoice() != null) {
			return this.getSelectedMultiChoice().equals(compareAgainst.getSelectedMultiChoice());
		} else return false;
	}
}
