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

package com.fluidbpm.program.api.vo.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Represents an Electronic Form with all possible Meta-Data for an item
 * in {@code Flow} / Workflow.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Form
 * @see Field
 * @see Attachment
 * @see FlowState
 * @see Properties
 */
@Getter
@Setter
public class FluidItem extends ABaseFluidJSONObject {
	public static final long serialVersionUID = 1L;

	private List<Field> userFields;
	private List<Field> routeFields;
	private List<Field> globalFields;

	private Form form;
	private List<Attachment> attachments;
	private List<FluidItemProperty> customProperties;

	private FlowState flowState;
	private String flow;
	private String step;

	private Date stepEnteredTime;

	private Boolean inCaseOfCreateLinkToParent;

	private Form tableFieldParentForm;
	private String tableFieldNameOnParentForm;

	/**
	 * The JSON mapping for the {@code FluidItem} object.
	 */
	public static class JSONMapping {
		public static final String CUSTOM_PROPERTIES = "customProperties";

		public static final String USER_FIELDS = "userFields";
		public static final String ROUTE_FIELDS = "routeFields";
		public static final String GLOBAL_FIELDS = "globalFields";

		public static final String STEP_ENTERED_TIME = "stepEnteredTime";

		public static final String FORM = "form";

		public static final String ATTACHMENTS = "attachments";
		public static final String FLOW_STATE = "flowState";
		public static final String FLOW = "flow";
		public static final String STEP = "step";
	}

	/**
	 * The JSON mapping for the {@code FluidItem} object as a flat object.
	 */
	public static class FlatFormJSONMapping {
		//Fluid Item...
		public static final String ID = "id";
		public static final String FLUID_ITEM_ID = "fluid_item_id";
		public static final String FLOW_STATE = "flow_state";

		//FIELDS
		//User...
		public static final String USER_FIELD_PREFIX = "user_field_";
		public static final String USER_FIELD_ID_PREFIX = "user_field_id_";

		//Route...
		public static final String ROUTE_FIELD_PREFIX = "route_field_";
		public static final String ROUTE_FIELD_ID_PREFIX = "route_field_id_";

		//Global...
		public static final String GLOBAL_FIELD_PREFIX = "global_field_";
		public static final String GLOBAL_FIELD_ID_PREFIX = "global_field_id_";
	}

	/**
	 * Additional properties for {@code this} {@code FluidItem}.
	 */
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class FluidItemProperty extends ABaseFluidJSONObject {
		public static final class Prop {
			public static final String FLOW_COMPLETE_PERCENTAGE = "Flow Complete Percentage";
		}

		private String name;
		private String value;

		/**
		 * The JSON mapping for the {@code FluidItemProperty} object.
		 */
		public static class JSONMapping {
			public static final String NAME = "name";
			public static final String VALUE = "value";
		}

		/**
		 * Populates local variables with {@code jsonObjectParam}.
		 *
		 * @param jsonObjectParam The JSON Object.
		 */
		public FluidItemProperty(JSONObject jsonObjectParam) {
			super(jsonObjectParam);
			if (this.jsonObject == null) return;

			//Name...
			if (!this.jsonObject.isNull(JSONMapping.NAME)) this.setName(this.jsonObject.getString(JSONMapping.NAME));

			//Value...
			if (!this.jsonObject.isNull(JSONMapping.VALUE)) this.setValue(this.jsonObject.getString(JSONMapping.VALUE));
		}

		/**
		 * Conversion to {@code JSONObject} from Java Object.
		 *
		 * @return {@code JSONObject} representation of {@code StepProperty}
		 * @throws JSONException If there is a problem with the JSON Body.
		 *
		 * @see ABaseFluidJSONObject#toJsonObject()
		 */
		@Override
		public JSONObject toJsonObject() throws JSONException {
			JSONObject returnVal = super.toJsonObject();

			//Name...
			if (this.getName() != null) returnVal.put(JSONMapping.NAME, this.getName());

			//Value...
			if (this.getValue() != null) returnVal.put(JSONMapping.VALUE, this.getValue());

			return returnVal;
		}
	}

	/**
	 * The {@code FlowState} that a {@code FluidItem}
	 * is currently in in terms of the Workflow.
	 *
	 * <table>
	 *     <caption>Flow States for an {@code FluidItem}.</caption>
	 *     <tr>
	 *         <th>Flow State</th>
	 *         <th>Description</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code NotInFlow}</td>
	 *         <td>Item that is not currently in Flow. Item could have been in flow
	 *         previously.</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code WorkInProgress}</td>
	 *         <td>Item that is currently part of a {@code Flow} and needs attention.</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code UserSend}</td>
	 *         <td>Electronic Form that is not currently part of a {@code Flow}, but shared with a
	 *         colleague.</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code UserSend}</td>
	 *         <td>Electronic Form that is <b>not</b> currently part of a {@code Flow}, but shared with a
	 *         colleague.</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code UserSendWorkInProgress}</td>
	 *         <td>Electronic Form that is currently part of a {@code Flow}, and shared with a
	 *         colleague.</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code Archive}</td>
	 *         <td>Electronic Form that has been completed and Archived for long term storage.</td>
	 *     </tr>
	 * </table>
	 */
	public enum FlowState {
		NotInFlow,
		WorkInProgress,
		UserSend,
		UserSendWorkInProgress,
		Archive;

		/**
		 * Returns the {@code FlowState} based on {@code flowStateStringParam}.
		 *
		 * The {@code flowStateStringParam} value is <b>not</b> case sensitive.
		 *
		 * @param flowStateStringParam The name of the {@code enum} {@code FlowState}.
		 * @return {@code FlowState}, if {@code enum} from {@code flowStateStringParam}
		 * is not found, {@code null} will be returned.
		 */
		public static FlowState valueOfSafe(String flowStateStringParam) {
			if (flowStateStringParam == null || flowStateStringParam.trim().isEmpty()) return null;

			String paramLower = flowStateStringParam.trim().toLowerCase();
			for (FlowState flowState : FlowState.values())
				if (paramLower.equals(flowState.name().toLowerCase())) return flowState;

			return null;
		}

		/**
		 * Inspect whether {@code flowStateParam} is a work-in-progress state.
		 *
		 * @param flowStateParam The state to check.
		 * @return Whether {@code flowStateParam} is work-in-progress.
		 */
		public static boolean isFlowStateWIP(FlowState flowStateParam) {
			if (flowStateParam == null) return false;

			switch (flowStateParam) {
				case WorkInProgress:
				case UserSendWorkInProgress:
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * Default constructor.
	 */
	public FluidItem() {
		super();
	}

	/**
	 * Sets the form on object creation.
	 *
	 * @param formParam The form to set.
	 *
	 * @see Form
	 */
	public FluidItem(Form formParam) {
		super();
		this.setForm(formParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FluidItem(JSONObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		//Custom Properties...
		if (!this.jsonObject.isNull(JSONMapping.CUSTOM_PROPERTIES)) {
			JSONArray jsonPropArray = this.jsonObject.getJSONArray(JSONMapping.CUSTOM_PROPERTIES);
			List<FluidItemProperty> fluidItemProperties = new ArrayList<>();
			for (int index = 0;index < jsonPropArray.length();index++) {
				fluidItemProperties.add(new FluidItemProperty(jsonPropArray.getJSONObject(index)));
			}
			this.setCustomProperties(fluidItemProperties);
		}

		//User Fields...
		if (!this.jsonObject.isNull(JSONMapping.USER_FIELDS)) {
			JSONArray fieldsArr = this.jsonObject.getJSONArray(JSONMapping.USER_FIELDS);
			List<Field> assUserFields = new ArrayList<>();
			for (int index = 0;index < fieldsArr.length();index++) {
				assUserFields.add(new Field(fieldsArr.getJSONObject(index)));
			}
			this.setUserFields(assUserFields);
		}

		//Route Fields...
		if (!this.jsonObject.isNull(JSONMapping.ROUTE_FIELDS)) {
			JSONArray fieldsArr = this.jsonObject.getJSONArray(JSONMapping.ROUTE_FIELDS);
			List<Field> assRouteFields = new ArrayList<>();
			for (int index = 0;index < fieldsArr.length();index++) {
				assRouteFields.add(new Field(fieldsArr.getJSONObject(index)));
			}
			this.setRouteFields(assRouteFields);
		}

		//Global Fields...
		if (!this.jsonObject.isNull(JSONMapping.GLOBAL_FIELDS)) {
			JSONArray fieldsArr = this.jsonObject.getJSONArray(JSONMapping.GLOBAL_FIELDS);
			List<Field> assGlobalFields = new ArrayList();
			for (int index = 0;index < fieldsArr.length();index++) {
				assGlobalFields.add(new Field(fieldsArr.getJSONObject(index)));
			}
			this.setGlobalFields(assGlobalFields);
		}

		//Attachments...
		if (!this.jsonObject.isNull(JSONMapping.ATTACHMENTS)) {
			JSONArray fieldsArr = this.jsonObject.getJSONArray(JSONMapping.ATTACHMENTS);
			List<Attachment> assAttachments = new ArrayList<>();
			for (int index = 0;index < fieldsArr.length();index++) {
				assAttachments.add(new Attachment(fieldsArr.getJSONObject(index)));
			}
			this.setAttachments(assAttachments);
		}

		//Form...
		if (!this.jsonObject.isNull(JSONMapping.FORM)) {
			this.setForm(new Form(this.jsonObject.getJSONObject(JSONMapping.FORM)));
		}

		//Flow...
		if (!this.jsonObject.isNull(JSONMapping.FLOW)) {
			this.setFlow(this.jsonObject.getString(JSONMapping.FLOW));
		}

		//Step...
		if (!this.jsonObject.isNull(JSONMapping.STEP)) {
			this.setStep(this.jsonObject.getString(JSONMapping.STEP));
		}

		//Flow State...
		if (!this.jsonObject.isNull(JSONMapping.FLOW_STATE)) {
			this.setFlowStateString(this.jsonObject.getString(JSONMapping.FLOW_STATE));
		}

		//Step Entered Time...
		this.setStepEnteredTime(this.getDateFieldValueFromFieldWithName(JSONMapping.STEP_ENTERED_TIME));
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code FluidItem}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Flow...
		if (this.getFlow() != null) returnVal.put(JSONMapping.FLOW, this.getFlow());

		//Step...
		if (this.getStep() != null) returnVal.put(JSONMapping.STEP, this.getStep());

		//Form...
		if (this.getForm() != null) returnVal.put(JSONMapping.FORM, this.getForm().toJsonObject());

		//Custom Properties...
		if (this.getCustomProperties() != null && !this.getCustomProperties().isEmpty()) {
			JSONArray propArr = new JSONArray();
			for (FluidItemProperty prop :this.getCustomProperties()) {
				propArr.put(prop.toJsonObject());
			}
			returnVal.put(JSONMapping.CUSTOM_PROPERTIES, propArr);
		}

		//User Fields...
		if (this.getUserFields() != null && !this.getUserFields().isEmpty()) {
			JSONArray fieldsArr = new JSONArray();
			for (Field toAdd :this.getUserFields()) {
				fieldsArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.USER_FIELDS, fieldsArr);
		}

		//Route Fields...
		if (this.getRouteFields() != null && !this.getRouteFields().isEmpty()) {
			JSONArray fieldsArr = new JSONArray();
			for (Field toAdd :this.getRouteFields()) {
				fieldsArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ROUTE_FIELDS, fieldsArr);
		}

		//Global Fields...
		if (this.getGlobalFields() != null && !this.getGlobalFields().isEmpty()) {
			JSONArray fieldsArr = new JSONArray();
			for (Field toAdd :this.getGlobalFields()) {
				fieldsArr.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.GLOBAL_FIELDS, fieldsArr);
		}

		//Attachments...
		if (this.getAttachments() != null) {
			JSONArray jsonArray = new JSONArray();
			for (Attachment toAdd : this.getAttachments()) {
				jsonArray.put(toAdd.toJsonObject());
			}
			returnVal.put(JSONMapping.ATTACHMENTS, jsonArray);
		}

		//Flow State...
		if (this.getFlowState() != null) returnVal.put(JSONMapping.FLOW_STATE, this.getFlowState().toString());

		//Step Entered Time...
		if (this.getStepEnteredTime() != null) {
			returnVal.put(JSONMapping.STEP_ENTERED_TIME, this.getDateAsObjectFromJson(this.getStepEnteredTime()));
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
	@JsonIgnore
	public JSONObject convertToFlatJSONObject() {
		JSONObject returnVal = new JSONObject();

		//Id...
		returnVal.put(FlatFormJSONMapping.FLUID_ITEM_ID, this.getId() == null ? JSONObject.NULL : this.getId());
		returnVal.put(FlatFormJSONMapping.ID, this.getId() == null ? JSONObject.NULL : this.getId());

		//Flow State...
		returnVal.put(FlatFormJSONMapping.FLOW_STATE, (this.getFlowState() == null) ?
				JSONObject.NULL : this.getFlowState().name());

		//Populate the Form...
		JSONObject formJSONObjFlat = (this.getForm() == null) ? null : this.getForm().convertToFlatJSONObject();

		if (formJSONObjFlat != null) {
			formJSONObjFlat.keySet().forEach((toAdd) -> returnVal.put(toAdd, formJSONObjFlat.get(toAdd)));
		}

		//Fields...
		UtilGlobal utilGlobal = new UtilGlobal();
		//Route Fields...
		if (this.getRouteFields() != null) {
			this.getRouteFields().forEach((routeFieldItem) -> {
				utilGlobal.setFlatFieldOnJSONObj(
						FlatFormJSONMapping.ROUTE_FIELD_PREFIX,
						FlatFormJSONMapping.ROUTE_FIELD_ID_PREFIX,
						routeFieldItem, returnVal
				);
			});
		}

		//User Fields...
		if (this.getUserFields() != null) {
			this.getUserFields().forEach((userFieldItem) -> {
				utilGlobal.setFlatFieldOnJSONObj(
						FlatFormJSONMapping.USER_FIELD_PREFIX,
						FlatFormJSONMapping.USER_FIELD_ID_PREFIX,
						userFieldItem, returnVal
				);
			});
		}

		//Global Fields...
		if (this.getGlobalFields() != null) {
			this.getGlobalFields().forEach((globalFieldItem) -> {
				utilGlobal.setFlatFieldOnJSONObj(
						FlatFormJSONMapping.GLOBAL_FIELD_PREFIX,
						FlatFormJSONMapping.GLOBAL_FIELD_ID_PREFIX,
						globalFieldItem, returnVal
				);
			});
		}
		return returnVal;
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the User Field as in Fluid.
	 * @return The value for the User Field as {@code String}.
	 *
	 * @see Field.Type#Text
	 */
	@XmlTransient
	@JsonIgnore
	public String getUserFieldValueAsString(String fieldNameParam) {
		Object obj = this.getFieldValueForField(fieldNameParam, this.getUserFields());
		if (obj == null) return null;
		return obj.toString();
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the User Field as in Fluid.
	 * @return The value for the User Field as {@code Object}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public Object getUserFieldValue(String fieldNameParam) {
		return this.getFieldValueForField(fieldNameParam, this.getUserFields());
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Route Field as in Fluid.
	 * @return The value for the Route Field as {@code Object}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public Object getRouteFieldValue(String fieldNameParam) {
		return this.getFieldValueForField(fieldNameParam, this.getRouteFields());
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @return The value for the Global Field as {@code Object}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public Object getGlobalFieldValue(String fieldNameParam) {
		return this.getFieldValueForField(fieldNameParam,this.getGlobalFields());
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Route Field as in Fluid.
	 * @return The value for the Route Field as {@code String}.
	 *
	 * @see Field.Type#Text
	 */
	@XmlTransient
	@JsonIgnore
	public String getRouteFieldValueAsString(String fieldNameParam) {
		Object obj = this.getFieldValueForField(fieldNameParam,this.getRouteFields());
		if (obj == null) return null;
		return obj.toString();
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Route Field as in Fluid.
	 * @return The value for the Route Field as {@code Double}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	@JsonIgnore
	public Double getRouteFieldValueAsDouble(String fieldNameParam) {
		Object obj = this.getRouteFieldValue(fieldNameParam);
		if (obj == null) {
			return null;
		} else if (obj instanceof Double) {
			return ((Double)obj);
		} else if (obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		return null;
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Route Field as in Fluid.
	 * @return The value for the Route Field as {@code Boolean}.
	 *
	 * @see Field.Type#TrueFalse
	 */
	@XmlTransient
	@JsonIgnore
	public Boolean getRouteFieldValueAsBoolean(String fieldNameParam) {
		Object obj = this.getRouteFieldValue(fieldNameParam);
		if (obj == null) {
			return null;
		} else if (obj instanceof Boolean) {
			return ((Boolean)obj);
		}
		return null;
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @return The value for the Global Field as {@code String}.
	 *
	 * @see Field.Type#Text
	 */
	@XmlTransient
	@JsonIgnore
	public String getGlobalFieldValueAsString(String fieldNameParam) {
		Object obj = this.getFieldValueForField(fieldNameParam, this.getGlobalFields());
		if (obj == null) return null;
		return obj.toString();
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @return The value for the Global Field as {@code Double}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	@JsonIgnore
	public Double getGlobalFieldValueAsDouble(String fieldNameParam) {
		Object obj = this.getFieldValueForField(fieldNameParam, this.getGlobalFields());
		if (obj == null) {
			return null;
		} else if (obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		return null;
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @return The value for the Global Field as {@code Integer}.
	 *
	 * @see Field.Type#Decimal
	 */
	@XmlTransient
	@JsonIgnore
	public Integer getGlobalFieldValueAsInt(String fieldNameParam) {
		Object obj = this.getFieldValueForField(fieldNameParam,this.getGlobalFields());
		if (obj == null) {
			return null;
		} else if (obj instanceof Number) {
			return ((Number)obj).intValue();
		}
		return null;
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
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @param fieldValueParam The value of the {@code Field}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public void setGlobalFieldValue(String fieldNameParam, Object fieldValueParam) {
		if (this.getGlobalFields() == null) this.setGlobalFields(new ArrayList<>());
		this.setFieldValue(this.getGlobalFields(), fieldNameParam, fieldValueParam, null);
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
	 *         <li>{@code getRouteFields()} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Route Field as in Fluid.
	 * @param fieldValueParam The value of the {@code Field}.
	 * @param typeParam The {@code Field.Type} of {@code Field}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public void setRouteFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
		if (this.getRouteFields() == null) this.setRouteFields(new ArrayList<>());
		this.setFieldValue(this.getRouteFields(), fieldNameParam, fieldValueParam, typeParam);
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
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @param fieldValueParam The value of the {@code Field}.
	 * @param typeParam The {@code Field.Type} of {@code Field}.
	 *
	 * @see Field.Type
	 */
	@XmlTransient
	@JsonIgnore
	public void setGlobalFieldValue(String fieldNameParam, Object fieldValueParam, Field.Type typeParam) {
		if (this.getGlobalFields() == null) this.setGlobalFields(new ArrayList<>());
		this.setFieldValue(this.getGlobalFields(), fieldNameParam, fieldValueParam, typeParam);
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
	 *         <li>{@code getUserFields()} is {@code null} or empty.</li>
	 *         <li>{@code toGetPropertyFromParam} is {@code null} or empty.</li>
	 *         <li>Field is not found by {@code fieldNameParam}.</li>
	 *     </ul>
	 *
	 * @param fieldNameParam The name of the Global Field as in Fluid.
	 * @param toGetPropertyFromParam The {@code List<Field>} where the value
	 *                               will be retrieved from.
	 *
	 * @return The value for the Field as {@code Object}.
	 *
	 * @see Field
	 */
	@XmlTransient
	@JsonIgnore
	public Object getFieldValueForField(String fieldNameParam, List<Field> toGetPropertyFromParam) {
		if (fieldNameParam == null || fieldNameParam.trim().isEmpty()) return null;
		if (toGetPropertyFromParam == null || toGetPropertyFromParam.isEmpty()) return null;

		String fieldNameLower = fieldNameParam.toLowerCase();

		Field fieldWithName =
				toGetPropertyFromParam.stream()
				.filter(itm -> itm.getFieldName() != null &&
						fieldNameLower.equals(itm.getFieldName().toLowerCase()))
				.findFirst()
				.orElse(null);
		if (fieldWithName == null) {
			return null;
		} else {
			return fieldWithName.getFieldValue();
		}
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

		List<Field> copyList = new ArrayList();
		copyList.addAll(fieldToSelectFrom);
		
		String fieldNameLower = fieldNameParam.toLowerCase();
		Field fieldWithName = copyList.stream()
						.filter(itm ->	itm.getFieldName() != null &&
								fieldNameLower.equals(itm.getFieldName().toLowerCase()))
						.findFirst()
						.orElse(null);
		if (fieldWithName == null) {
			fieldToSelectFrom.add(new Field(fieldNameParam, fieldValueParam, typeParam));
		} else {
			fieldWithName.setFieldValue(fieldValueParam);
			if (typeParam != null) fieldWithName.setTypeAsEnum(typeParam);
		}
	}

	/**
	 * Adds {@code toAddParam} to the list of {@code Attachment}s.
	 *
	 * @param toAddParam {@code Attachment} to add.
	 *
	 * @see Attachment
	 */
	@XmlTransient
	@JsonIgnore
	public void addAttachment(Attachment toAddParam) {
		if (!this.containsAttachments()) this.setAttachments(new ArrayList());
		this.getAttachments().add(toAddParam);
	}


	/**
	 * Adds {@code toAddParam} to the list of {@code FluidItemProperty}s.
	 *
	 * @param property {@code FluidItemProperty} to add.
	 *
	 * @see FluidItemProperty
	 */
	@XmlTransient
	@JsonIgnore
	public void addCustomProperty(FluidItemProperty property) {
		if (this.customProperties == null) this.setCustomProperties(new ArrayList());
		this.getCustomProperties().add(property);
	}

	/**
	 * Sets all the {@code Route}{@code Field}s.
	 *
	 * @param routeFieldsParam The new {@code Route}{@code Field}s.
	 *
	 * @see Field
	 */
	@XmlTransient
	@JsonIgnore
	public void setRouteFieldsArrayList(ArrayList<Field> routeFieldsParam) {
		this.routeFields = routeFieldsParam;
	}

	/**
	 * Gets the {@code FlowState}.
	 *
	 * @return {@code FlowState} for {@code this} {@code FluidItem}
	 *
	 * @see FlowState
	 */
	public FlowState getFlowState() {
		if (this.flowState == null && (this.getForm() != null && UtilGlobal.isNotBlank(this.getForm().getFlowState()))) {
			return FlowState.valueOf(this.getForm().getFlowState());
		}
		return this.flowState;
	}

	/**
	 * Sets the {@code FlowState}.
	 *
	 * @param flowStateParam {@code FlowState} for {@code this} {@code FluidItem}
	 *
	 * @see FlowState
	 */
	@XmlTransient
	@JsonIgnore
	public void setFlowStateString(String flowStateParam) {
		this.flowState = FlowState.valueOfSafe(flowStateParam);
	}

	/**
	 * Checks whether the {@code getAttachments()} is empty.
	 *
	 * @return If the list of attachments is empty.
	 */
	@XmlTransient
	@JsonIgnore
	public boolean containsAttachments() {
		return (this.attachments != null && !this.attachments.isEmpty());
	}

	/**
	 * If {@code this} is a newly created {@code FluidItem} as part of the
	 * {@code Flow} {@code ICustomProgram}. This will be used as a "flag" to
	 * make {@code this} {@code FluidItem} a descendant for the "parent"
	 * {@code FluidItem}.
	 *
	 * @return Whether {@code this} {@code FluidItem} should be linked to the "parent"
	 *
	 * @see Form
	 * @see com.fluidbpm.program.api.ICustomProgram
	 */
	public Boolean getInCaseOfCreateLinkToParent() {
		return this.inCaseOfCreateLinkToParent;
	}

	/**
	 * If {@code this} is a newly created {@code FluidItem} as part of the
	 * {@code Flow} {@code ICustomProgram}. This will be used as a "flag" to
	 * make {@code this} {@code FluidItem} a descendant for the "parent"
	 * {@code FluidItem}.
	 *
	 * @param inCaseOfCreateLinkToParentParam Whether {@code this} {@code FluidItem} should be linked to the "parent"
	 *
	 * @see Form
	 * @see com.fluidbpm.program.api.ICustomProgram
	 */
	public void setInCaseOfCreateLinkToParent(Boolean inCaseOfCreateLinkToParentParam) {
		this.inCaseOfCreateLinkToParent = inCaseOfCreateLinkToParentParam;
	}

	/**
	 * If {@code this} {@code FluidItem.getForm} is a {@code Field.Type.Table} {@code Field},
	 * the parent {@code Form} / Electronic Form needs to be set to indicate which {@code Form} is
	 * the container {@code Form}.
	 *
	 * @return The Table containing {@code Form}.
	 *
	 * @see Field.Type#Table
	 * @see Form
	 */
	public Form getTableFieldParentForm() {
		return this.tableFieldParentForm;
	}

	/**
	 * If {@code this} {@code FluidItem.getForm} is a {@code Field.Type.Table} {@code Field},
	 * the parent {@code Form} / Electronic Form needs to be set to indicate which {@code Form} is
	 * the container {@code Form}.
	 *
	 * @param tableFieldParentFormParam The Table containing {@code Form}.
	 *
	 * @see Field.Type#Table
	 * @see Form
	 */
	public void setTableFieldParentForm(Form tableFieldParentFormParam) {
		this.tableFieldParentForm = tableFieldParentFormParam;
	}

	/**
	 * If {@code this} {@code TableFieldParentForm} is set.
	 * The name of the {@code Field.Type.Table} {@code Field} also needs to be
	 * provided.
	 *
	 * Sets the name of the Table Field.
	 *
	 * @return The Table containing {@code Form}.
	 *
	 * @see Field.Type#Table
	 * @see Form
	 */
	public String getTableFieldNameOnParentForm() {
		return this.tableFieldNameOnParentForm;
	}

	/**
	 * If {@code this} {@code TableFieldParentForm} is set.
	 * The name of the {@code Field.Type.Table} {@code Field} also needs to be
	 * provided.
	 *
	 * Sets the name of the Table Field.
	 *
	 * @param tableFieldNameParam The Table containing {@code Form}.
	 *
	 * @see Field.Type#Table
	 * @see Form
	 */
	public void setTableFieldNameOnParentForm(String tableFieldNameParam) {
		this.tableFieldNameOnParentForm = tableFieldNameParam;
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
	@JsonIgnore
	public boolean equals(Object objParam) {
		if (objParam == null || this.getId() == null) return false;
		if (objParam instanceof FluidItem) {
			FluidItem paramCasted = (FluidItem)objParam;
			if (paramCasted.getId() == null) return false;

			return (this.getId().equals(paramCasted.getId()));
		}
		return false;
	}
}
