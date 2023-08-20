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

package com.fluidbpm.program.api.vo.thirdpartylib;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * 3rd Party library Task Identifier information.
 *
 * @author jasonbruwer on 2021-01-16
 * @since v1.11
 *
 * @see ABaseFluidJSONObject
 */
@Getter
@Setter
public class ThirdPartyLibraryTaskIdentifier extends ABaseFluidJSONObject {
	private String libraryFilename;
	private String librarySha256sum;
	private String libraryDescription;

	private ThirdPartyLibraryTaskType thirdPartyLibraryTaskType;
	private String taskIdentifier;
	private List<Form> formDefinitions;

	/**
	 * Types of 3rd party programs.
	 */
	public enum ThirdPartyLibraryTaskType {
		CustomProgram,
		CustomWebAction,
		CustomScheduledAction,
		CalculatedFieldAction
	}

	/**
	 * The JSON mapping for the {@code ThirdPartyLibraryTaskIdentifier} object.
	 */
	public static class JSONMapping {
		public static final String LIBRARY_FILENAME = "libraryFilename";
		public static final String LIBRARY_SHA256SUM = "librarySha256sum";
		public static final String LIBRARY_DESCRIPTION = "libraryDescription";

		public static final String THIRD_PARTY_LIBRARY_TASK_TYPE = "thirdPartyLibraryTaskType";
		public static final String TASK_IDENTIFIER = "taskIdentifier";
		public static final String FORM_DEFINITIONS = "formDefinitions";
	}

	/**
	 * Sets the Id associated with a Third party library.
	 *
	 * @param thirdPartyLibIdParam Third Party Library Id.
	 */
	public ThirdPartyLibraryTaskIdentifier(Long thirdPartyLibIdParam) {
		super();
		this.setId(thirdPartyLibIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public ThirdPartyLibraryTaskIdentifier(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (!this.jsonObject.isNull(JSONMapping.LIBRARY_FILENAME)) {
			this.setLibraryFilename(this.jsonObject.getString(JSONMapping.LIBRARY_FILENAME));
		}

		if (!this.jsonObject.isNull(JSONMapping.LIBRARY_SHA256SUM)) {
			this.setLibrarySha256sum(this.jsonObject.getString(JSONMapping.LIBRARY_SHA256SUM));
		}

		if (!this.jsonObject.isNull(JSONMapping.LIBRARY_DESCRIPTION)) {
			this.setLibraryDescription(this.jsonObject.getString(JSONMapping.LIBRARY_DESCRIPTION));
		}

		if (!this.jsonObject.isNull(JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE)) {
			this.setThirdPartyLibraryTaskType(this.jsonObject.getEnum(ThirdPartyLibraryTaskType.class,
					JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE));
		}

		if (!this.jsonObject.isNull(JSONMapping.TASK_IDENTIFIER)) {
			this.setTaskIdentifier(this.jsonObject.getString(JSONMapping.TASK_IDENTIFIER));
		}

		if (!this.jsonObject.isNull(JSONMapping.FORM_DEFINITIONS)) {
			JSONArray jsonArray = this.jsonObject.getJSONArray(JSONMapping.FORM_DEFINITIONS);
			List<Form> objs = new ArrayList();
			for (int index = 0;index < jsonArray.length();index++) objs.add(new Form(jsonArray.getJSONObject(index)));
			this.setFormDefinitions(objs);
		}
	}

	/**
	 * Default constructor.
	 */
	public ThirdPartyLibraryTaskIdentifier() {
		super();
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code ThirdPartyLibraryTaskIdentifier}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	@XmlTransient
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		if (this.getLibraryFilename() != null) returnVal.put(JSONMapping.LIBRARY_FILENAME, this.getLibraryFilename());

		if (this.getLibrarySha256sum() != null) returnVal.put(JSONMapping.LIBRARY_SHA256SUM, this.getLibrarySha256sum());

		if (this.getLibraryDescription() != null) returnVal.put(JSONMapping.LIBRARY_DESCRIPTION, this.getLibraryDescription());

		if (this.getThirdPartyLibraryTaskType() != null) returnVal.put(JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE, this.getThirdPartyLibraryTaskType());

		if (this.getTaskIdentifier() != null) returnVal.put(JSONMapping.TASK_IDENTIFIER, this.getTaskIdentifier());
		
		if (this.getFormDefinitions() != null && !this.getFormDefinitions().isEmpty()) {
			JSONArray jsonArray = new JSONArray();
			for (Form toAdd : this.getFormDefinitions()) jsonArray.put(toAdd.toJsonObject());
			returnVal.put(JSONMapping.FORM_DEFINITIONS, jsonArray);
		}

		return returnVal;
	}
}
