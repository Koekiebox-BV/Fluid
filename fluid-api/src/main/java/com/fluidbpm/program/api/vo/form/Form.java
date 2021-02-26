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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidElasticSearchJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.user.User;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * <p>
 * Represents an Electronic Form and Form Definition in Fluid.
 *
 * Depending on whether the {@code Form} is used for the;
 *
 * <ul>
 *     <li>
 *         Web Service for Document creation.
 *     </li>
 *     <li>
 *         Web Service for Form Definition adjustment.
 *     </li>
 *     <li>
 *         Third Party class loader for Flow Programs.
 *     </li>
 *     <li>
 *         Third Party class loader for Web Form Actions.
 *     </li>
 * </ul>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see com.fluidbpm.program.api.vo.historic.FormFlowHistoricDataListing
 * @see Field
 * @see Flow
 */
@Getter
@Setter
public class Form extends ABaseFluidElasticSearchJSONObject {

	public static final long serialVersionUID = 1L;

	private String formType;
	private Long formTypeId;

	private String formDescription;

	private String title;

	private String flowState;
	private String state;
	private User currentUser;

	private Date dateCreated;
	private Date dateLastUpdated;

	private List<Field> formFields;
	private List<Flow> associatedFlows;

	//Specifically for Elasticsearch...
	private Long ancestorId;
	private List<Long> descendantIds;
	private Long tableFieldParentId;
	private String ancestorLabel;
	private String descendantsLabel;
	private Boolean numberInputs;

	private static final String EMPTY_TITLE_MARKER = "[No Title from Custom Program]";

	/**
	 * The JSON mapping for the {@code Form} object.
	 */
	public static class JSONMapping {
		public static final String FORM_TYPE = "formType";
		public static final String FORM_TYPE_ID = "formTypeId";
		public static final String FORM_DESCRIPTION = "formDescription";

		public static final String TITLE = "title";
		public static final String STATE = "state";
		public static final String FLOW_STATE = "flowState";
		public static final String CURRENT_USER = "currentUser";

		public static final String DATE_CREATED = "dateCreated";
		public static final String DATE_LAST_UPDATED = "dateLastUpdated";

		public static final String FORM_FIELDS = "formFields";
		public static final String ASSOCIATED_FLOWS = "associatedFlows";

		//Labels...
		public static final String ANCESTOR_LABEL = "ancestorLabel";
		public static final String DESCENDANTS_LABEL = "descendantsLabel";
		public static final String NUMBER_INPUTS = "numberInputs";

		//Fields used for Search engine indexing...
		public static final String TABLE_FIELD_PARENT_ID = "tableFieldParentId";
		public static final String ANCESTOR_ID = "ancestorId";
		public static final String DESCENDANT_IDS = "descendantIds";

		public static final String _PARENT = "_parent";
	}

	/**
	 * The JSON mapping for the {@code Form} object as a flat object.
	 */
	public static class FlatFormJSONMapping {
		//Form...
		public static final String FORM_ID = "form_id";
		public static final String FORM_TITLE = "form_title";
		public static final String FORM_STATE = "form_state";
		public static final String FORM_TYPE = "form_type";
		public static final String FORM_FLOW_STATE = "form_flow_state";
		public static final String FORM_DATE_CREATED = "form_date_created";
		public static final String FORM_DATE_LAST_UPDATED = "form_date_last_updated";

		//Fields
		public static final String FORM_FIELD_PREFIX = "form_field_";
		public static final String FORM_FIELD_ID_PREFIX = "form_field_id_";
	}

	/**
	 * The state the {@code Form} can be in.
	 */
	public static class State {
		public static final String OPEN = "Open";//1
		public static final String LOCKED = "Locked";//2
	}

	/**
	 * Default constructor.
	 */
	public Form() {
		super();
	}

	/**
	 * Sets the Id associated with either the Form or Form Definition.
	 *
	 * @param formIdParam Form / Form Definition Id.
	 */
	public Form(Long formIdParam) {
		super();

		this.setId(formIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public Form(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) {
			return;
		}

		//Form Description...
		if (!this.jsonObject.isNull(JSONMapping.FORM_DESCRIPTION)) {
			this.setFormDescription(this.jsonObject.getString(JSONMapping.FORM_DESCRIPTION));
		}

		//Ancestor Label...
		if (!this.jsonObject.isNull(JSONMapping.ANCESTOR_LABEL)) {
			this.setAncestorLabel(
					this.jsonObject.getString(JSONMapping.ANCESTOR_LABEL));
		}

		//Descendant Label...
		if (!this.jsonObject.isNull(JSONMapping.DESCENDANTS_LABEL)) {
			this.setDescendantsLabel(
					this.jsonObject.getString(JSONMapping.DESCENDANTS_LABEL));
		}

		//Number Inputs...
		if (!this.jsonObject.isNull(JSONMapping.NUMBER_INPUTS)) {
			this.setNumberInputs(this.jsonObject.getBoolean(
					JSONMapping.NUMBER_INPUTS));
		}

		//Title...
		if (!this.jsonObject.isNull(JSONMapping.TITLE)) {
			this.setTitle(this.jsonObject.getString(JSONMapping.TITLE));
		}

		//Flow State...
		if (!this.jsonObject.isNull(JSONMapping.FLOW_STATE)) {
			this.setFlowState(this.jsonObject.getString(JSONMapping.FLOW_STATE));
		}

		//State...
		if (!this.jsonObject.isNull(JSONMapping.STATE)) {
			this.setState(this.jsonObject.getString(JSONMapping.STATE));
		}

		//User...
		if (!this.jsonObject.isNull(JSONMapping.CURRENT_USER)) {
			JSONObject jsonObj = this.jsonObject.getJSONObject(JSONMapping.CURRENT_USER);
			User currentUser = new User();

			//User Id
			if (!jsonObj.isNull(User.JSONMapping.Elastic.USER_ID)) {
				currentUser.setId(jsonObj.getLong(
						User.JSONMapping.Elastic.USER_ID));
			} else if (!jsonObj.isNull(
					ABaseFluidJSONObject.JSONMapping.ID)) {
				//Id is set, make use of that instead...
				currentUser.setId(jsonObj.getLong(ABaseFluidJSONObject.JSONMapping.ID));
			}

			//Username
			if (!jsonObj.isNull(User.JSONMapping.USERNAME)) {
				currentUser.setUsername(jsonObj.getString(User.JSONMapping.USERNAME));
			}

			this.setCurrentUser(currentUser);
		}

		//Form Type...
		if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE)) {
			this.setFormType(this.jsonObject.getString(JSONMapping.FORM_TYPE));
		}

		//Form Type Id...
		if (!this.jsonObject.isNull(JSONMapping.FORM_TYPE_ID)) {
			this.setFormTypeId(this.jsonObject.getLong(JSONMapping.FORM_TYPE_ID));
		}

		//Date Created...
		this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));

		//Date Last Updated...
		this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(
				JSONMapping.DATE_LAST_UPDATED));

		//Associated Flows...
		if (!this.jsonObject.isNull(JSONMapping.ASSOCIATED_FLOWS)) {
			JSONArray associatedJobsArr = this.jsonObject.getJSONArray(
					JSONMapping.ASSOCIATED_FLOWS);

			List<Flow> assFlowsObj = new ArrayList<>();
			for (int index = 0;index < associatedJobsArr.length();index++) {
				assFlowsObj.add(new Flow(associatedJobsArr.getJSONObject(index)));
			}

			this.setAssociatedFlows(assFlowsObj);
		}

		//Form Fields...
		if (!this.jsonObject.isNull(JSONMapping.FORM_FIELDS)) {
			JSONArray formFieldsArr = this.jsonObject.getJSONArray(
					JSONMapping.FORM_FIELDS);

			List<Field> assFormFields = new ArrayList<>();
			for (int index = 0;index < formFieldsArr.length();index++) {
				assFormFields.add(new Field(formFieldsArr.getJSONObject(index)));
			}

			this.setFormFields(assFormFields);
		}

		//Ancestor...
		if (this.jsonObject.isNull(JSONMapping.ANCESTOR_ID)) {
			this.setAncestorId(null);
		} else {
			this.setAncestorId(this.jsonObject.getLong(
					JSONMapping.ANCESTOR_ID));
		}

		//Table Field Parent Id...
		if (this.jsonObject.isNull(JSONMapping.TABLE_FIELD_PARENT_ID)) {
			this.setTableFieldParentId(null);
		} else {
			this.setTableFieldParentId(this.jsonObject.getLong(
					JSONMapping.TABLE_FIELD_PARENT_ID));
		}

		//Descendant Ids...
		if (this.jsonObject.isNull(JSONMapping.DESCENDANT_IDS)) {
			this.setDescendantIds(null);
		} else {
			JSONArray jsonArray = this.jsonObject.getJSONArray(
					JSONMapping.DESCENDANT_IDS);
			List<Long> descendantIds = new ArrayList<>();
			for (int index = 0;index < jsonArray.length();index++) {
				descendantIds.add(jsonArray.getLong(index));
			}

			if (descendantIds.isEmpty()) {
				this.setDescendantIds(null);
			} else {
				this.setDescendantIds(descendantIds);
			}
		}
	}

	/**
	 * Constructor that also sets The Form Definition
	 * associated with the {@code Form}.
	 *
	 * @param formTypeParam The Form Definition.
	 */
	public Form(String formTypeParam) {
		this.setFormType(formTypeParam);
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as one of the {@code Field.Type}s.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	public Object getFieldValueForField(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValue();
	}

	/**
	 * <p>
	 *     Returns the {@code Field} of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The Form Field.
	 *
	 * @see Field
	 */
	@XmlTransient
	public Field getField(String fieldNameParam) {
		if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) {
			return null;
		}

		if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
			return null;
		}

		String fieldNameLower = fieldNameParam.toLowerCase().trim();
		Field fieldWithName =
				this.getFormFields().stream()
						.filter(itm -> itm.getFieldName() != null &&
								fieldNameLower.equals(itm.getFieldName().toLowerCase().trim()))
						.findFirst()
						.orElse(null);
		return fieldWithName;
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code String}.
	 *
	 * @see Field.Type#Text
	 */
	@XmlTransient
	public String getFieldValueAsString(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsString();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code TableField}.
	 *
	 * @see Field.Type#Table
	 * @see TableField
	 */
	@XmlTransient
	public TableField getFieldValueAsTableField(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsTableField();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code MultiChoice}.
	 *
	 * @see Field.Type#MultipleChoice
	 * @see MultiChoice
	 */
	@XmlTransient
	public MultiChoice getFieldValueAsMultiChoice(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsMultiChoice();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Date}.
	 *
	 * @see Field.Type#DateTime
	 */
	@XmlTransient
	public Date getFieldValueAsDate(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsDate();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Boolean}.
	 *
	 * @see Field.Type#TrueFalse
	 */
	@XmlTransient
	public Boolean getFieldValueAsBoolean(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsBoolean();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Double}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	public Double getFieldValueAsDouble(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsDouble();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *         <li>Field Value is not of type {@code Number}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Integer}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	public Integer getFieldValueAsInt(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsInteger();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *         <li>Field Value is not of type {@code Number}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Long}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	public Long getFieldValueAsLong(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsLong();
	}

	/**
	 * <p>
	 *     Returns the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     A {@code null} will be returned if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *         <li>Field Value is not of type {@code Number}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @return The value for the Form Field as {@code Number}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	public Number getFieldValueAsNumber(String fieldNameParam) {
		Field fieldWithName = this.getField(fieldNameParam);
		return (fieldWithName == null) ? null : fieldWithName.getFieldValueAsNumber();
	}

	/**
	 * <p>
	 *     Sets the value of the {@code fieldNameParam} requested.
	 * <p>
	 *     If there is an existing value, the value will be override with
	 *     the value of {@code fieldValueParam}.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <p>
	 *     The value won't be set if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @param fieldValueParam The value of the {@code Field}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	public void setFieldValue(String fieldNameParam, Object fieldValueParam) {
		if (this.getFormFields() == null) this.setFormFields(new ArrayList());

		this.setFieldValue(this.getFormFields(), fieldNameParam, fieldValueParam, null);
	}

	/**
	 * Set the field value to {@code fieldValueParam} where name is {@code fieldNameParam}
	 * on the list {@code fieldToSelectFrom}.
	 *
	 * @param fieldToSelectFrom The field listing to set or add to.
	 * @param fieldNameParam The name of the field.
	 * @param fieldValueParam The new value.
	 * @param typeParam The type of field.
	 */
	private void setFieldValue(
		List<Field> fieldToSelectFrom,
		String fieldNameParam,
		Object fieldValueParam,
		Field.Type typeParam
	) {
		if (fieldToSelectFrom == null) return;

		if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) return;

		String fieldNameLower = fieldNameParam.toLowerCase();
		List<Field> copyList = new ArrayList();
		copyList.addAll(fieldToSelectFrom);

		Field fieldWithName = copyList.stream()
						.filter(itm ->	itm.getFieldName() != null &&
								fieldNameLower.equals(itm.getFieldName().toLowerCase()))
						.findFirst()
						.orElse(null);
		if (fieldWithName == null) {
			fieldToSelectFrom.add(new Field(fieldNameParam, fieldValueParam, typeParam));
		} else {
			fieldWithName.setFieldValue(fieldValueParam);
			if (typeParam != null) {
				fieldWithName.setTypeAsEnum(typeParam);
			}
		}
	}

	/**
	 * Remove the {@code Form} field where the name is {@code fieldNameParam}.
	 * 
	 * @param fieldNameParam The name of the field to remove.
	 */
	public void removeField(String fieldNameParam) {
		if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) return;
		if (this.getFormFields() == null || this.getFormFields().isEmpty()) return;

		String fieldNameLower = fieldNameParam.toLowerCase();
		this.getFormFields().removeIf(fieldItm -> fieldItm.getFieldName() != null &&
				fieldNameLower.equals(fieldItm.getFieldName().toLowerCase()));
	}

	/**
	 * <p>
	 *     Sets the value of the {@code fieldNameParam} requested.
	 *
	 * <p>
	 *     If there is an existing value, the value will be override with
	 *     the value of {@code fieldValueParam}.
	 *
	 * <p>
	 *     The {@code fieldNameParam} <b>is not</b> case sensitive.
	 *
	 * <br>
	 *
	 * <p>
	 *     The value won't be set if;
	 *     <ul>
	 *         <li>{@code fieldNameParam} is {@code null} or empty.</li>
	 *         <li>{@code getFormFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Form Field as in Fluid.
	 * @param fieldValueParam The value of the {@code Field}.
	 * @param typeParam The {@code Field.Type} of {@code Field}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	public void setFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
		if (fieldNameParam == null) return;
		if (this.getFormFields() == null) this.setFormFields(new ArrayList());
		
		this.setFieldValue(this.getFormFields(), fieldNameParam, fieldValueParam, typeParam);
	}

	/**
	 * <p>
	 *     Determine whether the current {@code Form} Type / Definition is
	 *     of type {@code formTypeParam}
	 *
	 * <p>
	 *     If the {@code formTypeParam} is {@code null} or empty, {@code false}
	 *     will be returned.
	 *
	 * @param formTypeParam String value of the Form Definition.
	 * @return Whether the {@code Form} is of type {@code formTypeParam}
	 */
	@XmlTransient
	public boolean isFormType(String formTypeParam) {
		if ((formTypeParam == null || formTypeParam.trim().isEmpty()) ||
				(this.getFormType() == null || this.getFormType().trim().isEmpty())) return false;

		return formTypeParam.toLowerCase().equals(getFormType().toLowerCase());
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code Form}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Form Type...
		if (this.getFormType() != null) returnVal.put(JSONMapping.FORM_TYPE, this.getFormType());

		//Form Type Id...
		if (this.getFormTypeId() != null) returnVal.put(JSONMapping.FORM_TYPE_ID, this.getFormTypeId());

		//Title...
		if (this.getTitle() != null) returnVal.put(JSONMapping.TITLE, this.getTitle());

		//Form Description...
		if (this.getFormDescription() != null) returnVal.put(JSONMapping.FORM_DESCRIPTION, this.getFormDescription());

		//Ancestor Label...
		if (this.getAncestorLabel() != null) returnVal.put(JSONMapping.ANCESTOR_LABEL, this.getAncestorLabel());

		//Descendant Label...
		if (this.getDescendantsLabel() != null) returnVal.put(JSONMapping.DESCENDANTS_LABEL, this.getDescendantsLabel());

		//Number Inputs...
		if (this.getNumberInputs() != null) returnVal.put(JSONMapping.NUMBER_INPUTS, this.getNumberInputs());

		//Date Created...
		if (this.getDateCreated() != null) returnVal.put(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(this.getDateCreated()));

		//Date Last Updated...
		if (this.getDateLastUpdated() != null) returnVal.put(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(this.getDateLastUpdated()));


		//Current User...
		if (this.getCurrentUser() != null) returnVal.put(JSONMapping.CURRENT_USER, this.getCurrentUser().toJsonObject());

		//State...
		if (this.getState() != null) returnVal.put(JSONMapping.STATE, this.getState());

		//Flow State...
		if (this.getFlowState() != null) returnVal.put(JSONMapping.FLOW_STATE, this.getFlowState());

		//Form Fields...
		if (this.getFormFields() != null && !this.getFormFields().isEmpty()) {
			JSONArray formFieldsArr = new JSONArray();
			for (Field toAdd :this.getFormFields()) formFieldsArr.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.FORM_FIELDS, formFieldsArr);
		}

		//Associated Flows...
		if (this.getAssociatedFlows() != null && !this.getAssociatedFlows().isEmpty()) {
			JSONArray assoJobsArr = new JSONArray();
			for (Flow toAdd :this.getAssociatedFlows()) assoJobsArr.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.ASSOCIATED_FLOWS, assoJobsArr);
		}

		//Ancestor...
		if (this.getAncestorId() != null) returnVal.put(JSONMapping.ANCESTOR_ID, this.getAncestorId());

		//Table Field Parent Id...
		if (this.getTableFieldParentId() != null) returnVal.put(JSONMapping.TABLE_FIELD_PARENT_ID, this.getTableFieldParentId());

		//Descendant Ids...
		if (this.getDescendantIds() != null && !this.getDescendantIds().isEmpty()) {
			JSONArray array = new JSONArray();
			for (Long formId : this.getDescendantIds()) array.put(formId);
			returnVal.put(JSONMapping.DESCENDANT_IDS, array);
		}

		return returnVal;
	}

	/**
	 * Creates the mapping object required by Elastic Search when making
	 * use of enhanced data-types.
	 *
	 * See {@code https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html}.
	 *
	 * @return {@code JSONObject} representation of {@code Form} for
	 * ElasticSearch mapping.
	 *
	 * @throws JSONException If there is a problem with the JSON Body.
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonMappingForElasticSearch() throws JSONException {

		JSONObject returnVal = new JSONObject();

		//Id...
		{
			JSONObject idJsonObj = new JSONObject();
			idJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.LONG);
			returnVal.put(ABaseFluidJSONObject.JSONMapping.ID, idJsonObj);
		}

		//Form Type...
		{
			JSONObject formTypeJsonObj = new JSONObject();
			formTypeJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.KEYWORD);
			returnVal.put(JSONMapping.FORM_TYPE, formTypeJsonObj);
		}

		//Form Type Id...
		{
			JSONObject formTypeIdJsonObj = new JSONObject();
			formTypeIdJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.LONG);
			returnVal.put(JSONMapping.FORM_TYPE_ID, formTypeIdJsonObj);
		}

		//Title...
		{
			JSONObject titleJsonObj = new JSONObject();
			titleJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.TEXT);
			returnVal.put(JSONMapping.TITLE, titleJsonObj);
		}

		//Form Description...
		{
			JSONObject formDescJsonObj = new JSONObject();
			formDescJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.KEYWORD);
			returnVal.put(JSONMapping.FORM_DESCRIPTION, formDescJsonObj);
		}

		//State...
		{
			JSONObject stateJsonObj = new JSONObject();
			stateJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.KEYWORD);
			returnVal.put(JSONMapping.STATE, stateJsonObj);
		}

		//Flow State...
		{
			JSONObject flowStateJsonObj = new JSONObject();
			flowStateJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.KEYWORD);
			returnVal.put(JSONMapping.FLOW_STATE, flowStateJsonObj);
		}

		//Current User...
		{
			JSONObject currentUserJsonObj = new JSONObject();
			currentUserJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.OBJECT);

			JSONObject properties = new JSONObject();

			//Current User Id...
			JSONObject currentUserUserIdJsonObj = new JSONObject();
			currentUserUserIdJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.LONG);
			properties.put(User.JSONMapping.Elastic.USER_ID, currentUserUserIdJsonObj);

			//Current User Id...
			JSONObject currentUserUsernameJsonObj = new JSONObject();
			currentUserUsernameJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.KEYWORD);
			properties.put(User.JSONMapping.USERNAME, currentUserUsernameJsonObj);

			currentUserJsonObj.put(
					ABaseFluidJSONObject.JSONMapping.Elastic.PROPERTIES,
					properties);

			returnVal.put(JSONMapping.CURRENT_USER, currentUserJsonObj);
		}

		//Date Created...
		{
			JSONObject dateCreatedJsonObj = new JSONObject();
			dateCreatedJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.DATE);
			returnVal.put(JSONMapping.DATE_CREATED, dateCreatedJsonObj);
		}

		//Date Last Updated...
		{
			JSONObject dateLastUpdatedJsonObj = new JSONObject();
			dateLastUpdatedJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.DATE);
			returnVal.put(JSONMapping.DATE_LAST_UPDATED, dateLastUpdatedJsonObj);
		}

		//Get the listing of form fields...
		if (this.getFormFields() != null && !this.getFormFields().isEmpty()) {
			for (Field toAdd : this.getFormFields()) {
				JSONObject convertedField = toAdd.toJsonMappingForElasticSearch();
				if (convertedField == null) {
					continue;
				}

				String fieldNameAsCamel = toAdd.getFieldNameAsUpperCamel();
				returnVal.put(fieldNameAsCamel, convertedField);
			}
		}

		//Ancestor Obj...
		{
			JSONObject ancestorJsonObj = new JSONObject();
			ancestorJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.LONG);
			returnVal.put(JSONMapping.ANCESTOR_ID, ancestorJsonObj);
		}

		//Table field parent id...
		{
			JSONObject tblFieldParentIdJsonObj = new JSONObject();
			tblFieldParentIdJsonObj.put(
					Field.JSONMapping.Elastic.MAPPING_ONLY_TYPE,
					Field.ElasticSearchType.LONG);
			returnVal.put(JSONMapping.TABLE_FIELD_PARENT_ID, tblFieldParentIdJsonObj);
		}

		return returnVal;
	}

	/**
	 * Conversion to {@code JSONObject} for storage in ElasticCache for {@code Form}.
	 *
	 * @return {@code JSONObject} representation of {@code Form}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 * @see Form
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonForElasticSearch() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Form Type...
		if (this.getFormType() != null) {
			returnVal.put(JSONMapping.FORM_TYPE, this.getFormType());
		}

		//Form Type Id...
		if (this.getFormTypeId() != null) {
			returnVal.put(JSONMapping.FORM_TYPE_ID, this.getFormTypeId());
		}

		//Title...
		if (this.getTitle() != null) {
			returnVal.put(JSONMapping.TITLE, this.getTitle());
		}

		//Form Description...
		if (this.getFormDescription() != null) {
			returnVal.put(JSONMapping.FORM_DESCRIPTION, this.getFormDescription());
		}

		//State...
		if (this.getState() != null) {
			returnVal.put(JSONMapping.STATE, this.getState());
		}

		//Flow State...
		if (this.getFlowState() != null) {
			returnVal.put(JSONMapping.FLOW_STATE, this.getFlowState());
		}

		//Current User...
		JSONObject currentUserJsonObj = new JSONObject();
		if (this.getCurrentUser() == null) {
			currentUserJsonObj.put(
					User.JSONMapping.Elastic.USER_ID, JSONObject.NULL);
			currentUserJsonObj.put(User.JSONMapping.USERNAME, JSONObject.NULL);
		} else {
			//Id...
			if (this.getCurrentUser().getId() == null ||
					this.getCurrentUser().getId().longValue() < 1) {
				currentUserJsonObj.put(
						User.JSONMapping.Elastic.USER_ID,
						JSONObject.NULL);
			} else {
				currentUserJsonObj.put(
						User.JSONMapping.Elastic.USER_ID,
						this.getCurrentUser().getId());
			}

			//Username...
			if (this.getCurrentUser().getUsername() == null ||
					this.getCurrentUser().getUsername().trim().isEmpty()) {
				currentUserJsonObj.put(User.JSONMapping.USERNAME, JSONObject.NULL);
			} else {
				currentUserJsonObj.put(User.JSONMapping.USERNAME, this.getCurrentUser().getUsername());
			}
		}
		returnVal.put(JSONMapping.CURRENT_USER, currentUserJsonObj);

		//Date Created...
		if (this.getDateCreated() != null) {
			returnVal.put(JSONMapping.DATE_CREATED,
					this.getDateAsLongFromJson(this.getDateCreated()));
		}

		//Date Last Updated...
		if (this.getDateLastUpdated() != null) {
			returnVal.put(JSONMapping.DATE_LAST_UPDATED,
					this.getDateAsLongFromJson(this.getDateLastUpdated()));
		}

		//Form Fields...
		if (this.getFormFields() != null && !this.getFormFields().isEmpty()) {
			this.getFormFields().forEach(toAdd ->{
				JSONObject convertedFieldObj = toAdd.toJsonForElasticSearch();
				if (convertedFieldObj == null) {
					return;
				}

				Iterator<String> iterKeys = convertedFieldObj.keys();
				while(iterKeys.hasNext()) {
					String key = iterKeys.next();
					returnVal.put(key, convertedFieldObj.get(key));
				}
			});
		}

		//Ancestor...
		Long ancestorIdLcl = this.getAncestorId();
		if (ancestorIdLcl != null) {
			returnVal.put(JSONMapping.ANCESTOR_ID, ancestorIdLcl);
		}

		//Table Field Parent Id...
		if (this.getTableFieldParentId() != null) {
			returnVal.put(JSONMapping.TABLE_FIELD_PARENT_ID,
					this.getTableFieldParentId());
		}

		//Descendant Ids...
		if (this.getDescendantIds() != null && !this.getDescendantIds().isEmpty()) {
			JSONArray array = new JSONArray();
			for (Long formId : this.getDescendantIds()) {
				array.put(formId);
			}

			returnVal.put(JSONMapping.DESCENDANT_IDS, array);
		}

		return returnVal;
	}

	/**
	 * Serialize {@code this} object into a JSONObject.
	 *
	 * Any fields provided with a Java {@code null} value will be stored
	 * as {@code JSONObject.Null}. {@code Field.Type.Table} fields are not supported and will be skipped.
	 *
	 * @return Flat {@code JSON} object (No inner fields).
	 *
	 * @see JSONObject
	 */
	@XmlTransient
	public JSONObject convertToFlatJSONObject() {
		JSONObject returnVal = new JSONObject();

		//Id...
		returnVal.put(
				FlatFormJSONMapping.FORM_ID,
				this.getId() == null ?
						JSONObject.NULL : this.getId());

		//Title...
		returnVal.put(
				FlatFormJSONMapping.FORM_TITLE,
				this.getTitle() == null ?
						JSONObject.NULL : this.getTitle());

		//Form Type...
		returnVal.put(
				FlatFormJSONMapping.FORM_TYPE,
				this.getFormType() == null ?
						JSONObject.NULL : this.getFormType());

		//State...
		returnVal.put(
				FlatFormJSONMapping.FORM_STATE,
				this.getState() == null ?
						JSONObject.NULL : this.getState());

		//Form Flow State...
		returnVal.put(
				FlatFormJSONMapping.FORM_FLOW_STATE,
				this.getFlowState() == null ?
						JSONObject.NULL : this.getFlowState());

		//Date Created...
		returnVal.put(
				FlatFormJSONMapping.FORM_DATE_CREATED,
				(this.getDateCreated() == null) ?
						JSONObject.NULL: this.getDateCreated().getTime());

		//Date Last Updated...
		returnVal.put(
				FlatFormJSONMapping.FORM_DATE_LAST_UPDATED,
				(this.getDateLastUpdated() == null) ?
						JSONObject.NULL: this.getDateLastUpdated().getTime());

		//Form Fields...
		if (this.getFormFields() == null || this.getFormFields().isEmpty()) {
			return returnVal;
		}

		//Set the form fields...
		UtilGlobal utilGlobal = new UtilGlobal();
		this.getFormFields().forEach(
				(formFieldItem) -> {
					utilGlobal.setFlatFieldOnJSONObj(
							FlatFormJSONMapping.FORM_FIELD_PREFIX,
							FlatFormJSONMapping.FORM_FIELD_ID_PREFIX,
							formFieldItem,
							returnVal
					);
				}
		);

		return returnVal;
	}

	/**
	 * Populate the object based on the ElasticSearch JSON structure.
	 *
	 * @param jsonObjectParam The JSON object to populate from.
	 * @param formFieldsParam The Form Fields to use for mapping.
	 *
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	public void populateFromElasticSearchJson(JSONObject jsonObjectParam, List<Field> formFieldsParam) throws JSONException {
		this.jsonObject = jsonObjectParam;
		if (jsonObjectParam == null) return;

		//Id...
		if (jsonObjectParam.isNull(ABaseFluidJSONObject.JSONMapping.ID)) {
			this.setId(null);
		} else {
			this.setId(jsonObjectParam.getLong(ABaseFluidJSONObject.JSONMapping.ID));
		}

		//Form Type...
		if (jsonObjectParam.isNull(JSONMapping.FORM_TYPE)) {
			this.setFormType(null);
		} else {
			this.setFormType(jsonObjectParam.getString(JSONMapping.FORM_TYPE));
		}

		//Form Type Id...
		if (jsonObjectParam.isNull(JSONMapping.FORM_TYPE_ID)) {
			this.setFormTypeId(null);
		} else {
			this.setFormTypeId(jsonObjectParam.getLong(JSONMapping.FORM_TYPE_ID));
		}

		//Title...
		if (jsonObjectParam.isNull(JSONMapping.TITLE)) {
			this.setTitle(null);
		} else {
			this.setTitle(jsonObjectParam.getString(JSONMapping.TITLE));
		}

		//Flow State...
		if (jsonObjectParam.isNull(JSONMapping.FLOW_STATE)) {
			this.setFlowState(null);
		} else {
			this.setFlowState(jsonObjectParam.getString(JSONMapping.FLOW_STATE));
		}

		//State...
		if (jsonObjectParam.isNull(JSONMapping.STATE)) {
			this.setState(null);
		} else {
			this.setState(jsonObjectParam.getString(JSONMapping.STATE));
		}

		//Form Description...
		if (jsonObjectParam.isNull(JSONMapping.FORM_DESCRIPTION)) {
			this.setFormDescription(null);
		} else {
			this.setFormDescription(jsonObjectParam.getString(
					JSONMapping.FORM_DESCRIPTION));
		}

		//Date Created...
		if (jsonObjectParam.isNull(JSONMapping.DATE_CREATED)) {
			this.setDateCreated(null);
		} else {
			this.setDateCreated(new Date(jsonObjectParam.getLong(JSONMapping.DATE_CREATED)));
		}

		//Date Last Updated...
		if (jsonObjectParam.isNull(JSONMapping.DATE_LAST_UPDATED)) {
			this.setDateLastUpdated(null);
		} else {
			this.setDateLastUpdated(new Date(
					jsonObjectParam.getLong(JSONMapping.DATE_LAST_UPDATED)));
		}

		//Current User...
		if (jsonObjectParam.isNull(JSONMapping.CURRENT_USER)) {
			this.setCurrentUser(null);
		} else {
			JSONObject currUserJsonObj =
					jsonObjectParam.getJSONObject(JSONMapping.CURRENT_USER);

			User currentUser = new User();
			if (!currUserJsonObj.isNull(User.JSONMapping.Elastic.USER_ID)) {
				currentUser.setId(currUserJsonObj.getLong(User.JSONMapping.Elastic.USER_ID));
			}

			if (!currUserJsonObj.isNull(User.JSONMapping.USERNAME)) {
				currentUser.setUsername(currUserJsonObj.getString(User.JSONMapping.USERNAME));
			}

			this.setCurrentUser(currentUser);
		}

		//Form Fields...
		if (formFieldsParam == null || formFieldsParam.isEmpty()) {
			this.setFormFields(null);
		} else {
			//There are fields...
			List<Field> fieldsToSet = new ArrayList();

			for (Field formField : formFieldsParam) {
				Field fieldToAdd = formField.populateFromElasticSearchJson(jsonObjectParam);
				if (fieldToAdd == null) {
					continue;
				}

				fieldsToSet.add(fieldToAdd);
			}

			//Confirm there are values...
			if (fieldsToSet.isEmpty()) {
				this.setFormFields(null);
			} else {
				this.setFormFields(fieldsToSet);
			}
		}

		//Ancestor...
		if (jsonObjectParam.isNull(JSONMapping.ANCESTOR_ID)) {
			this.setAncestorId(null);
		} else {
			this.setAncestorId(jsonObjectParam.getLong(
					JSONMapping.ANCESTOR_ID));
		}

		//Table Field Parent Id...
		if (jsonObjectParam.isNull(JSONMapping.TABLE_FIELD_PARENT_ID)) {
			this.setTableFieldParentId(null);
		} else {
			this.setTableFieldParentId(jsonObjectParam.getLong(
					JSONMapping.TABLE_FIELD_PARENT_ID));
		}

		//Descendant Ids...
		if (jsonObjectParam.isNull(JSONMapping.DESCENDANT_IDS)) {
			this.setDescendantIds(null);
		} else {
			Object objectDescendantIds =
					jsonObjectParam.get(JSONMapping.DESCENDANT_IDS);

			List<Long> descendantIds = new ArrayList();
			//Array...
			if (objectDescendantIds instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) objectDescendantIds;

				for (int index = 0;index < jsonArray.length();index++) {
					descendantIds.add(jsonArray.getLong(index));
				}
			} else if (objectDescendantIds instanceof Number) {
				//Number...
				descendantIds.add(((Number)objectDescendantIds).longValue());
			}

			if (descendantIds.isEmpty()) {
				this.setDescendantIds(null);
			} else {
				this.setDescendantIds(descendantIds);
			}
		}
	}

	/**
	 * Converts the {@code getFormType} to upper_camel_case.
	 *
	 * @return {@code getFieldName()} as upper_camel_case.
	 */
	@XmlTransient
	public String getFormTypeAsUpperCamel() {
		return new UtilGlobal().toCamelUpperCase(this.getFormType());
	}

	/**
	 * JSON {@code String} value for this form.
	 *
	 * @return JSON value of form.
	 */
	@Override
	@XmlTransient
	public String toString() {
		JSONObject jsonObj = this.toJsonObject();
		if (jsonObj == null) return null;
		return jsonObj.toString();
	}

	/**
	 * Compares {@code objParam} against {@code this} to see
	 * if they are equal.
	 *
	 * @param objParam The object to compare against.
	 * @return Whether {@code objParam} is equal.
	 */
	@Override
	@XmlTransient
	public boolean equals(Object objParam) {
		if (objParam == null || this.getId() == null) return false;
		if (objParam instanceof Form) {
			Form paramCasted = (Form)objParam;
			if (paramCasted.getId() == null) return false;
			return (this.getId().equals(paramCasted.getId()));
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object. This method is
	 * supported for the benefit of hash tables such as those provided by
	 * {@link java.util.HashMap}.
	 *
	 * @return The hascode of {@code this} object.
	 */
	@Override
	@XmlTransient
	public int hashCode() {
		int hash = 10000000;
		if (this.getId() == null) {
			return hash;
		}
		hash += this.getId().hashCode();
		return hash;
	}

	/**
	 * Gets the Form Type / Form Definition of {@code this} {@code Form}.
	 *
	 * @return Form Type / Form Definition.
	 */
	public String getFormType() {
		return this.formType;
	}

	/**
	 * Sets the Form Type / Form Definition of {@code this} {@code Form}.
	 *
	 * @param formTypeParam Form Type / Form Definition.
	 */
	public void setFormType(String formTypeParam) {
		this.formType = formTypeParam;
	}

	/**
	 * Gets the Form Type / Form Definition Id of {@code this} {@code Form}.
	 *
	 * @return Form Type / Form Definition Id.
	 */
	public Long getFormTypeId() {
		return this.formTypeId;
	}

	/**
	 * Sets the Form Type / Form Definition Id of {@code this} {@code Form}.
	 *
	 * @param formTypeIdParam Form Type / Form Definition Id.
	 */
	public void setFormTypeId(Long formTypeIdParam) {
		this.formTypeId = formTypeIdParam;
	}

	/**
	 * <p>Sets the Form Title as in Fluid.
	 *
	 * <p>
	 *     If {@code titleParam} is {@code null}, the
	 *     Title will be set to <br>
	 *     "[No Title from Custom Program]"
	 *
	 * @param titleParam Form Title.
	 */
	public void setTitle(String titleParam) {
		if (titleParam == null) {
			this.title = EMPTY_TITLE_MARKER;
			return;
		}
		this.title = titleParam;
	}

	/**
	 * Create a {@code Map} from the Form fields.
	 * @return {@code Map} with field name as key and {@code Field} as value.
	 */
	@XmlTransient
	public Map<String, Field> getFormFieldsAsMap() {
		List<Field> fields = this.getFormFields();
		Map<String, Field> returnVal = new HashMap<>();
		if (fields == null || fields.isEmpty()) return returnVal;

		fields.stream().forEach(field -> {
			returnVal.put(field.getFieldName(), field);
		});
		return returnVal;
	}

	/**
	 * Prints all the Fields and their values to the standard
	 * {@code System.out}.
	 */
	@XmlTransient
	public void printFormFields() {
		System.out.println("\n\n*** PRINTING FORM FIELDS ***");
		if (this.getFormFields() != null) {
			for (Field formField : this.getFormFields()) {
				System.out.println("Field : '"+formField.getFieldName()+"' with value: "+
						formField.getFieldValue());
			}
		}
	}
}
