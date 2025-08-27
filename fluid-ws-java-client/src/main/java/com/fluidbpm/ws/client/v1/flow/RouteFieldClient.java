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

package com.fluidbpm.ws.client.v1.flow;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.RouteFieldListing;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewGroup;
import com.fluidbpm.program.api.vo.ws.WS.Path.RouteField.Version1;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseFieldClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Java Web Service Client for Route Field related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 * @version v1.8
 *
 * @see JsonObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.RouteField
 * @see Field
 * @see ABaseFieldClient
 */
public class RouteFieldClient extends ABaseFieldClient {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public RouteFieldClient(
			String endpointBaseUrlParam,
			String serviceTicketParam) {
		super(endpointBaseUrlParam);

		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Create a new Plain Text field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldTextPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.Text);
			routeFieldParam.setTypeMetaData(FieldMetaData.Text.PLAIN);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new True False field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldTrueFalse(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.TrueFalse);
			routeFieldParam.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Paragraph Text field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldParagraphTextPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
			routeFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Paragraph HTML field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldParagraphTextHTML(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
			routeFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Multi Choice field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @param multiChoiceValuesParam The available multi choice values.
	 * @return Created Field.
	 */
	public Field createFieldMultiChoicePlain(
			Field routeFieldParam,
			List<String> multiChoiceValuesParam
	) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (multiChoiceValuesParam == null) {
			multiChoiceValuesParam = new ArrayList();
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
			routeFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
			routeFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Multi Choice select Many field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @param multiChoiceValuesParam The available multi choice values.
	 * @return Created Field.
	 */
	public Field createFieldMultiChoiceSelectMany(
			Field routeFieldParam,
			List<String> multiChoiceValuesParam
	) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (multiChoiceValuesParam == null ||
				multiChoiceValuesParam.isEmpty()) {
			throw new FluidClientException(
					"No Multi-choice values provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
			routeFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
			routeFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Date only field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldDateTimeDate(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.DateTime);
			routeFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Date and time field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldDateTimeDateAndTime(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.DateTime);
			routeFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Create a new Decimal field.
	 *
	 * @param routeFieldParam Field to Create.
	 * @return Created Field.
	 */
	public Field createFieldDecimalPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.Decimal);
			routeFieldParam.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
		}

		return new Field(this.putJson(
				routeFieldParam, Version1.routeFieldCreate()));
	}

	/**
	 * Update an existing Text field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldTextPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.Text);
			routeFieldParam.setTypeMetaData(FieldMetaData.Text.PLAIN);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing True False field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldTrueFalse(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.TrueFalse);
			routeFieldParam.setTypeMetaData(FieldMetaData.TrueFalse.TRUE_FALSE);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Paragraph Text field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldParagraphTextPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
			routeFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.PLAIN);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Paragraph HTML field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldParagraphTextHTML(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.ParagraphText);
			routeFieldParam.setTypeMetaData(FieldMetaData.ParagraphText.HTML);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Multi Choice field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @param multiChoiceValuesParam New available Multi-choices.
	 * @return Updated Field.
	 */
	public Field updateFieldMultiChoicePlain(
			Field routeFieldParam,
			List<String> multiChoiceValuesParam
	) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (multiChoiceValuesParam == null ||
				multiChoiceValuesParam.isEmpty()) {
			throw new FluidClientException(
					"No Multi-choice values provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
			routeFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.PLAIN);
			routeFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Multi Choice select many field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @param multiChoiceValuesParam New available Multi-choices.
	 * @return Updated Field.
	 */
	public Field updateFieldMultiChoiceSelectMany(
			Field routeFieldParam,
			List<String> multiChoiceValuesParam
	) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (multiChoiceValuesParam == null ||
				multiChoiceValuesParam.isEmpty()) {
			throw new FluidClientException(
					"No Multi-choice values provided.",
					FluidClientException.ErrorCode.FIELD_VALIDATE);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.MultipleChoice);
			routeFieldParam.setTypeMetaData(FieldMetaData.MultiChoice.SELECT_MANY);
			routeFieldParam.setFieldValue(new MultiChoice(multiChoiceValuesParam));
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Date field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldDateTimeDate(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.DateTime);
			routeFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Date and Time field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldDateTimeDateAndTime(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.DateTime);
			routeFieldParam.setTypeMetaData(FieldMetaData.DateTime.DATE_AND_TIME);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing Decimal field.
	 *
	 * @param routeFieldParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldDecimalPlain(Field routeFieldParam) {
		if (routeFieldParam != null && this.serviceTicket != null) {
			routeFieldParam.setServiceTicket(this.serviceTicket);
		}

		if (routeFieldParam != null) {
			routeFieldParam.setTypeAsEnum(Field.Type.Decimal);
			routeFieldParam.setTypeMetaData(FieldMetaData.Decimal.PLAIN);
		}

		return new Field(this.postJson(
				routeFieldParam, Version1.routeFieldUpdate()));
	}

	/**
	 * Update an existing 'Route field' value.
	 *
	 * @param routeFieldValueParam Field to Update.
	 * @return Updated Field.
	 */
	public Field updateFieldValue(Field routeFieldValueParam) {
		if (routeFieldValueParam != null && this.serviceTicket != null) {
			routeFieldValueParam.setServiceTicket(this.serviceTicket);
		}

		return new Field(this.postJson(
				routeFieldValueParam, Version1.routeFieldUpdateValue()));
	}

	/**
	 * Create an new 'Route field' value.
	 *
	 * @param routeFieldValueToCreateParam Field to Create.
	 * @param fluidItemParam Fluid item to create field for.
	 * @return Created Field.
	 */
	public Field createFieldValue(
			Field routeFieldValueToCreateParam,
			FluidItem fluidItemParam) {
		if (routeFieldValueToCreateParam != null && this.serviceTicket != null) {
			routeFieldValueToCreateParam.setServiceTicket(this.serviceTicket);
		}

		Long fluidItmId = (fluidItemParam == null) ? null : fluidItemParam.getId();

		return new Field(this.putJson(
				routeFieldValueToCreateParam,
				Version1.routeFieldCreateValue(fluidItmId)));
	}

	/**
	 * Retrieves field information by {@code fieldIdParam}.
	 *
	 * @param fieldId The field Primary Key.
	 * @return Field Definition by primary key.
	 */
	public Field getFieldById(Long fieldId) {
		Field field = new Field(fieldId);
		//Set for Payara server...
		field.setFieldValue(new MultiChoice());
		field.setServiceTicket(this.serviceTicket);
		return new Field(this.postJson(field, Version1.getById()));
	}

	/**
	 * Retrieves field information by {@code fieldIdParam}.
	 *
	 * @param fieldName The field name.
	 * @return Field by name.
	 */
	public Field getFieldByName(String fieldName) {
		Field field = new Field(fieldName);
		//Set for Payara server...
		field.setFieldValue(new MultiChoice());
		field.setServiceTicket(this.serviceTicket);
		return new Field(this.postJson(field, Version1.getById()));
	}

	/**
	 * Retrieves field listing by {@code viewGroup}.
	 *
	 * @param viewGroup The view group to fetch fields for.
	 * @return Fields by view group.
	 */
	public RouteFieldListing getFieldsByViewGroup(WebKitViewGroup viewGroup) {
		if (viewGroup == null) {
			return null;
		}
		//Set for Payara server...
		viewGroup.setServiceTicket(this.serviceTicket);
		return new RouteFieldListing(this.postJson(viewGroup, Version1.getByViewGroup()));
	}

	/**
	 * Retrieve the Route field values for {@code fluidItemParam}.
	 *
	 * If the id is not set, the {@code Form} id must be set.
	 *
	 * @param fluidItemParam The fluid item to get route fields for.
	 *
	 * @return Route Fields by the FluidItem {@code fluidItemParam}.
	 *
	 * @see RouteFieldListing
	 * @see Field
	 */
	public List<Field> getRouteFieldValuesBy(FluidItem fluidItemParam) {
		if (this.serviceTicket != null && fluidItemParam != null) {
			fluidItemParam.setServiceTicket(this.serviceTicket);
		}

		return new RouteFieldListing(this.postJson(
				fluidItemParam, Version1.getValuesBy())).getListing();
	}

	/**
	 * Deletes a field from Fluid.
	 *
	 * @param fieldParam The field to delete. Important that Id is set.
	 * @return Deleted Field.
	 */
	public Field deleteField(Field fieldParam) {
		if (fieldParam != null && this.serviceTicket != null) {
			fieldParam.setServiceTicket(this.serviceTicket);
		}

		return new Field(this.postJson(fieldParam, Version1.routeFieldDelete()));
	}

	/**
	 * Forcefully Deletes a field from Fluid.
	 *
	 * Only 'admin' is allowed to make this call.
	 *
	 * @param fieldParam The field to delete. Important that Id is set.
	 * @return Deleted Field.
	 */
	public Field forceDeleteField(Field fieldParam) {
		if (fieldParam != null && this.serviceTicket != null) {
			fieldParam.setServiceTicket(this.serviceTicket);
		}

		return new Field(this.postJson(
				fieldParam, Version1.routeFieldDelete(true)));
	}
}
