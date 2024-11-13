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

package com.fluidbpm.ws.client.v1.config;

import com.fluidbpm.program.api.vo.config.GlobalFieldListing;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField.Version1;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField.Version1.globalFieldCreate;
import static com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField.Version1.globalFieldUpdate;

/**
 * Java Web Service Client for Global Field related actions.
 *
 * @author jasonbruwer on 2018-05-05
 * @version v1.8
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.GlobalField
 * @see Field
 * @see ABaseFieldClient
 */
public class GlobalFieldClient extends ABaseFieldClient {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public GlobalFieldClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Create a new Multi Choice field.
	 * @param globalField Field to Create.
	 * @param multiChoiceValues The available multi choice values.
	 * @return Created Field.
	 */
	public Field createFieldMultiChoicePlain(Field globalField, List<String> multiChoiceValues) {
		if (multiChoiceValues == null) multiChoiceValues = new ArrayList();

		if (globalField != null) {
			globalField.setServiceTicket(this.serviceTicket);
			globalField.setTypeAsEnum(Field.Type.MultipleChoice);
			globalField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
			globalField.setFieldValue(new MultiChoice(multiChoiceValues));
		}
		return new Field(this.putJson(globalField, globalFieldCreate()));
	}

	/**
	 * Create a new Plain Text field.
	 * @param globalField Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldTextPlain(Field globalField) {
		if (globalField != null) {
			globalField.setServiceTicket(this.serviceTicket);
			globalField.setTypeAsEnum(Field.Type.Text);
			globalField.setTypeMetaData(FieldMetaData.Text.PLAIN);
		}
		return new Field(this.putJson(
				globalField, WS.Path.GlobalField.Version1.globalFieldCreate())
		);
	}

	/**
	 * Create a new True False field.
	 * @param globalField Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldTrueFalse(Field globalField) {
		if (globalField != null) {
			globalField.setServiceTicket(this.serviceTicket);
			globalField.setTypeAsEnum(Field.Type.TrueFalse);
			globalField.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
		}
		return new Field(this.putJson(globalField, WS.Path.GlobalField.Version1.globalFieldCreate()));
	}

	/**
	 * Create a new Paragraph Text field.
	 * @param globalField Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldParagraphTextPlain(Field globalField) {
		if (globalField != null) {
			globalField.setServiceTicket(this.serviceTicket);
			globalField.setTypeAsEnum(Field.Type.ParagraphText);
			globalField.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
		}

		return new Field(this.putJson(
				globalField, WS.Path.FormField.Version1.formFieldCreate())
		);
	}

	/**
	 * Update an existing Multi Choice field.
	 * @param globalField Field to Update.
	 * @param multiChoiceValues New available Multi-choices.
	 * @return Updated Field.
	 */
	public Field updateFieldMultiChoicePlain(
		Field globalField,
		List<String> multiChoiceValues
	) {
		if (multiChoiceValues == null || multiChoiceValues.isEmpty()) {
			throw new FluidClientException(
					"No Multi-choice values provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE
			);
		}

		List<String> beforeAvail = null, beforeSelected = null;
		if (globalField != null) {
			globalField.setServiceTicket(this.serviceTicket);
			globalField.setTypeAsEnum(Field.Type.MultipleChoice);
			globalField.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);

			if (globalField.getFieldValue() instanceof MultiChoice) {
				MultiChoice casted = (MultiChoice)globalField.getFieldValue();
				beforeAvail = casted.getAvailableMultiChoices();
				beforeSelected = casted.getSelectedMultiChoices();
			}

			globalField.setFieldValue(new MultiChoice(multiChoiceValues));
		}

		Field returnVal = new Field(this.postJson(globalField, globalFieldUpdate()));
		if (globalField != null) globalField.setFieldValue(new MultiChoice(beforeSelected, beforeAvail));

		return returnVal;
	}

	/**
	 * Update an existing Global field value.
	 * @param globalFieldValue Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldValue(Field globalFieldValue) {
		if (globalFieldValue != null && this.serviceTicket != null) {
			globalFieldValue.setServiceTicket(this.serviceTicket);
		}
		return new Field(this.postJson(globalFieldValue, Version1.globalFieldUpdateValue()));
	}

	/**
	 * Retrieves field value by {@code fieldNameParam}.
	 * @param fieldName The field name.
	 * @return Field by primary key.
	 */
	public Field getFieldValueByName(String fieldName) {
		Field field = new Field(fieldName);
		return this.getFieldValueBy(field);
	}

	/**
	 * Retrieves field value by {@code fieldIdParam}.
	 * @param fieldId The field id.
	 * @return Field value by Global field primary key.
	 */
	public Field getFieldValueByFieldId(Long fieldId)
	{
		return this.getFieldValueBy(new Field(fieldId));
	}

	/**
	 * Retrieves field value by {@code fieldParam}.
	 *
	 * @param field The field name.
	 * @return Field by primary key.
	 */
	private Field getFieldValueBy(Field field) {
		if (field != null) {
			//Set for Payara server...
			field.setFieldValue(new MultiChoice());
			if (this.serviceTicket != null) field.setServiceTicket(this.serviceTicket);
		}
		return new Field(this.postJson(field, Version1.getValueBy()));
	}

	/**
	 * Retrieve all the Global field values.
	 * @return Global Fields in the destination system.
	 * @see GlobalFieldListing
	 * @see Field
	 */
	public List<Field> getAllGlobalFieldValues() {
		Field field = new Field();

		//Set for Payara server...
		field.setFieldValue(new MultiChoice());

		if (this.serviceTicket != null) field.setServiceTicket(this.serviceTicket);

		return new GlobalFieldListing(this.postJson(field, Version1.getAllValues())).getListing();
	}

	/**
	 * Retrieve all the Global fields.
	 * @return Global Fields in the destination system.
	 * @see GlobalFieldListing
	 * @see Field
	 */
	public List<Field> getAllGlobalFields() {
		Field field = new Field();
		//Set for Payara server...
		field.setFieldValue(new MultiChoice());
		field.setServiceTicket(this.serviceTicket);

		return new GlobalFieldListing(this.postJson(field, Version1.getAllFields())).getListing();
	}
}
