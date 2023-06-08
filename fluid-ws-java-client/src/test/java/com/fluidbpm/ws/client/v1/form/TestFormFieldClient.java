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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonbruwer on 15/12/28.
 */
public class TestFormFieldClient extends ABaseLoggedInTestCase {

	private LoginClient loginClient;

	/**
	 *
	 */
	public static final class TestStatics{
		public static final String FIELD_NAME = "JUnit Form Field";
		public static final String FIELD_DESCRIPTION = "JUnit Form Field Description.";

		public static final String FIELD_NAME_UPDATE = "JUnit Form Field UPDATED";
		public static final String FIELD_DESCRIPTION_UPDATE = "JUnit Form Field Description UPDATED.";

		/**
		 *
		 */
		public static final class Text{

			public static final String MASKED_VALUE = "999999999";
			public static final String MASKED_VALUE_UPDATE = "(999) 999999";

			public static final String BARCODE_VALUE = "int2of5";
			public static final String BARCODE_VALUE_UPDATE = "code39";
		}

		/**
		 *
		 */
		public static final class MultiChoice{

			public static final String OPTION_1 = "Andrew";
			public static final String OPTION_2 = "Benny";
			public static final String OPTION_3 = "Charles";
			public static final String OPTION_4 = "Derick";
			public static final String OPTION_5 = "Edwin";
			public static final String OPTION_6 = "Frik";

			/**
			 *
			 */
			public static final List CREATE_LIST = new ArrayList();
			static {
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_1);
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_2);
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_3);
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_4);
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_5);
				CREATE_LIST.add(TestStatics.MultiChoice.OPTION_6);
			}

			/**
			 *
			 */
			public static final List UPDATE_LIST = new ArrayList();
			static {
				UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_1);
				UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_2);
				UPDATE_LIST.add(TestStatics.MultiChoice.OPTION_3);
			}
		}

		/**
		 *
		 */
		public static final class Decimal{
			public static final String PREFIX = "$";
			public static final String PREFIX_UPDATE = "R";

			public static final double STEP_FACTOR = 0.0;
			public static final double STEP_FACTOR_UPDATE = 0.01;

			public static final double MIN_VALUE = 0.0;
			public static final double MIN_VALUE_UPDATE = 0.1;

			public static final double MAX_VALUE = 1.0;
			public static final double MAX_VALUE_UPDATE = 10.51;

			private static final String CREATE_META_AFTER =
					"_Min["+MIN_VALUE+"]_Max["+MAX_VALUE+"]_StepFactor["+STEP_FACTOR+"]_Prefix["+PREFIX+"]";

			public static final String SPINNER_STATIC_LINE =
					FormFieldClient.FieldMetaData.Decimal.SPINNER +CREATE_META_AFTER;

			public static final String SLIDER_STATIC_LINE =
					FormFieldClient.FieldMetaData.Decimal.SLIDER +
							"_Min["+MIN_VALUE+"]_Max["+MAX_VALUE+"]_StepFactor["+STEP_FACTOR+"]_Prefix[]";

			public static final String RATING_STATIC_LINE =
					FormFieldClient.FieldMetaData.Decimal.RATING +
							"_Min["+MIN_VALUE+"]_Max["+MAX_VALUE+"]_StepFactor[0.0]_Prefix[]";

			private static final String CREATE_META_AFTER_UPDATE =
					"_Min["+MIN_VALUE_UPDATE+"]_Max["+MAX_VALUE_UPDATE+"]_StepFactor["+STEP_FACTOR_UPDATE+"]_Prefix["+
							PREFIX_UPDATE +"]";

			public static final String SPINNER_STATIC_LINE_UPDATE =
					FormFieldClient.FieldMetaData.Decimal.SPINNER +CREATE_META_AFTER_UPDATE;

			public static final String SLIDER_STATIC_LINE_UPDATE =
					FormFieldClient.FieldMetaData.Decimal.SLIDER +
							"_Min["+MIN_VALUE_UPDATE+"]_Max["+MAX_VALUE_UPDATE+"]_StepFactor["+STEP_FACTOR_UPDATE+"]_Prefix[]";

			public static final String RATING_STATIC_LINE_UPDATE =
					FormFieldClient.FieldMetaData.Decimal.RATING +
							"_Min["+MIN_VALUE_UPDATE+"]_Max["+MAX_VALUE_UPDATE+"]_StepFactor[0.0]_Prefix[]";
		}

		/**
		 *
		 */
		public static final class TableField{
			public static final boolean SUM_DECIMALS = false;
			public static final boolean SUM_DECIMALS_UPDATE = true;

			/**
			 *
			 * @param formDefinitionId
			 * @return
			 */
			public static String createMetaExpectancy(long formDefinitionId, boolean sumDecimalsParam)
			{
				return ("" +formDefinitionId + "_SumDecimals["+sumDecimalsParam+"]");

			}
		}
	}

	/**
	 *
	 */
	@Before
	public void init()
	{
		ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

		this.loginClient = new LoginClient(BASE_URL);
	}

	/**
	 *
	 */
	@After
	public void destroy()
	{
		this.loginClient.closeAndClean();
	}

	/**
	 *
	 */
	@Test
	public void testFormField_TextPlain_CRUD()
	{
		if (this.isConnectionInValid) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTextPlain(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.PLAIN, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTextPlain(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.PLAIN, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.PLAIN, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_TextMasked_CRUD()
	{
		if (this.isConnectionInValid) return;

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTextMasked(
				toCreate, TestStatics.Text.MASKED_VALUE);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getFieldType());

		String maskedMetaDataVal = FormFieldClient.FieldMetaData.Text.MASKED.concat(TestStatics.Text.MASKED_VALUE);
		TestCase.assertEquals("'Type Meta-Data' mismatch.", maskedMetaDataVal, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTextMasked(
				createdField, TestStatics.Text.MASKED_VALUE_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getFieldType());

		String maskedMetaDataValUpdate =
				FormFieldClient.FieldMetaData.Text.MASKED.concat(TestStatics.Text.MASKED_VALUE_UPDATE);

		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				maskedMetaDataValUpdate, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				maskedMetaDataValUpdate, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_TextBarcode_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTextBarcode(
				toCreate, TestStatics.Text.BARCODE_VALUE);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getFieldType());

		String maskedMetaDataVal = FormFieldClient.FieldMetaData.Text.BARCODE.concat(TestStatics.Text.BARCODE_VALUE);
		TestCase.assertEquals("'Type Meta-Data' mismatch.", maskedMetaDataVal, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTextBarcode(
				createdField, TestStatics.Text.BARCODE_VALUE_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getFieldType());

		String maskedMetaDataValUpdate =
				FormFieldClient.FieldMetaData.Text.BARCODE.concat(TestStatics.Text.BARCODE_VALUE_UPDATE);

		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				maskedMetaDataValUpdate, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				maskedMetaDataValUpdate, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_TextLatitudeAndLongitude_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTextLatitudeAndLongitude(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Text.toString(), createdField.getFieldType());

		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.LATITUDE_AND_LONGITUDE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTextLatitudeAndLongitude(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Text.toString(), updatedField.getFieldType());

		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.LATITUDE_AND_LONGITUDE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Text.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Text.LATITUDE_AND_LONGITUDE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}


	/**
	 *
	 */
	@Test
	public void testFormField_TrueFalse_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTrueFalse(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.TrueFalse.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTrueFalse(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.TrueFalse.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.TrueFalse.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.TrueFalse.TRUE_FALSE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_ParagraphTextPlain_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldParagraphTextPlain(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.PLAIN, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldParagraphTextPlain(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.PLAIN, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.PLAIN, byIdField.getTypeMetaData());

		//5. Get by Name...
		Field byNameField = formFieldClient.getFieldByName(updatedField.getFieldName());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byNameField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byNameField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byNameField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byNameField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.PLAIN, byNameField.getTypeMetaData());

		//6. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_ParagraphTextHTML_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldParagraphTextHTML(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.ParagraphText.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.HTML, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldParagraphTextHTML(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.ParagraphText.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.HTML, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.ParagraphText.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.ParagraphText.HTML, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_MultiChoicePlain_CRUD() {
		if (this.isConnectionInValid) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Multi Choice...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldMultiChoicePlain(
				toCreate, TestStatics.MultiChoice.CREATE_LIST);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN, createdField.getTypeMetaData());

		if (!(createdField.getFieldValue() instanceof MultiChoice)) {
			TestCase.fail("Return value for field Object must be 'MultiChoice'. Type is '"+
					(createdField.getFieldValue() == null ? "not-set": createdField.getFieldValue().getClass())+"'.");
		}

		MultiChoice casted = (MultiChoice)createdField.getFieldValue();

		TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
		TestCase.assertEquals("The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.CREATE_LIST.size(),
				casted.getSelectedMultiChoices().size());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldMultiChoicePlain(
				createdField, TestStatics.MultiChoice.UPDATE_LIST);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN, updatedField.getTypeMetaData());

		if (!(updatedField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
				castedUpdated.getSelectedMultiChoices());
		TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedUpdated.getSelectedMultiChoices().size());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN, byIdField.getTypeMetaData());

		if (!(byIdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
				castedById.getSelectedMultiChoices());
		TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedById.getSelectedMultiChoices().size());

		//5. Get by Name...
		Field byNameField = formFieldClient.getFieldByName(byIdField.getFieldName());

		TestCase.assertNotNull("BY_NAME: The field must be set.",
				byNameField);

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_MultiChoicePlainWithSearch_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldMultiChoicePlainWithSearch(
				toCreate, TestStatics.MultiChoice.CREATE_LIST);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN_SEARCH, createdField.getTypeMetaData());

		if (!(createdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice casted = (MultiChoice)createdField.getFieldValue();

		TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
		TestCase.assertEquals("The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.CREATE_LIST.size(),
				casted.getSelectedMultiChoices().size());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldMultiChoicePlainWithSearch(
				createdField, TestStatics.MultiChoice.UPDATE_LIST);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN_SEARCH, updatedField.getTypeMetaData());

		if (!(updatedField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
				castedUpdated.getSelectedMultiChoices());
		TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedUpdated.getSelectedMultiChoices().size());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.PLAIN_SEARCH, byIdField.getTypeMetaData());

		if (!(byIdField.getFieldValue() instanceof MultiChoice)) {
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
				castedById.getSelectedMultiChoices());
		TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedById.getSelectedMultiChoices().size());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_MultiChoiceSelectMany_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldMultiChoiceSelectMany(
				toCreate, TestStatics.MultiChoice.CREATE_LIST);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, createdField.getTypeMetaData());

		if (!(createdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice casted = (MultiChoice)createdField.getFieldValue();

		TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
		TestCase.assertEquals("The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.CREATE_LIST.size(),
				casted.getSelectedMultiChoices().size());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldMultiChoiceSelectMany(
				createdField, TestStatics.MultiChoice.UPDATE_LIST);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, updatedField.getTypeMetaData());

		if (!(updatedField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
				castedUpdated.getSelectedMultiChoices());
		TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedUpdated.getSelectedMultiChoices().size());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY, byIdField.getTypeMetaData());

		if (!(byIdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
				castedById.getSelectedMultiChoices());
		TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedById.getSelectedMultiChoices().size());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_MultiChoiceSelectManyWithSearch_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldMultiChoiceSelectManyWithSearch(
				toCreate, TestStatics.MultiChoice.CREATE_LIST);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.MultipleChoice.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY_SEARCH, createdField.getTypeMetaData());

		if (!(createdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice casted = (MultiChoice)createdField.getFieldValue();

		TestCase.assertNotNull("The 'Selected Multi Choices' needs to be set.", casted.getSelectedMultiChoices());
		TestCase.assertEquals("The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.CREATE_LIST.size(),
				casted.getSelectedMultiChoices().size());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldMultiChoiceSelectManyWithSearch(
				createdField, TestStatics.MultiChoice.UPDATE_LIST);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY_SEARCH, updatedField.getTypeMetaData());

		if (!(updatedField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedUpdated = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("UPDATE: The 'Selected Multi Choices' needs to be set.",
				castedUpdated.getSelectedMultiChoices());
		TestCase.assertEquals("UPDATE: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedUpdated.getSelectedMultiChoices().size());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.MultipleChoice.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.MultiChoice.SELECT_MANY_SEARCH, byIdField.getTypeMetaData());

		if (!(byIdField.getFieldValue() instanceof MultiChoice))
		{
			TestCase.fail("Return value for field Object must be 'MultiChoice'.");
		}

		MultiChoice castedById = (MultiChoice)updatedField.getFieldValue();

		TestCase.assertNotNull("BY_ID: The 'Selected Multi Choices' needs to be set.",
				castedById.getSelectedMultiChoices());
		TestCase.assertEquals("BY_ID: The number of Multi Choices is invalid.",
				TestStatics.MultiChoice.UPDATE_LIST.size(),
				castedById.getSelectedMultiChoices().size());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DateTimeDate_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDateTimeDate(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDateTimeDate(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DateTimeDateAndTime_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDateTimeDateAndTime(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.DateTime.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDateTimeDateAndTime(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.DateTime.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.DateTime.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.DateTime.DATE_AND_TIME, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DecimalPlain_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDecimalPlain(toCreate);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Decimal.PLAIN, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDecimalPlain(createdField);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Decimal.PLAIN, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				FormFieldClient.FieldMetaData.Decimal.PLAIN, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DecimalSpinner_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDecimalSpinner(
				toCreate,
				TestStatics.Decimal.MIN_VALUE,
				TestStatics.Decimal.MAX_VALUE,
				TestStatics.Decimal.STEP_FACTOR,
				TestStatics.Decimal.PREFIX);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SPINNER_STATIC_LINE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDecimalSpinner(
				createdField,
				TestStatics.Decimal.MIN_VALUE_UPDATE,
				TestStatics.Decimal.MAX_VALUE_UPDATE,
				TestStatics.Decimal.STEP_FACTOR_UPDATE,
				TestStatics.Decimal.PREFIX_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SPINNER_STATIC_LINE_UPDATE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SPINNER_STATIC_LINE_UPDATE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DecimalSlider_CRUD()
	{
		if (this.isConnectionInValid)
		{
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDecimalSlider(
				toCreate,
				TestStatics.Decimal.MIN_VALUE,
				TestStatics.Decimal.MAX_VALUE,
				TestStatics.Decimal.STEP_FACTOR);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SLIDER_STATIC_LINE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDecimalSlider(
				createdField,
				TestStatics.Decimal.MIN_VALUE_UPDATE,
				TestStatics.Decimal.MAX_VALUE_UPDATE,
				TestStatics.Decimal.STEP_FACTOR_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SLIDER_STATIC_LINE_UPDATE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.SLIDER_STATIC_LINE_UPDATE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_DecimalRating_CRUD() {
		if (this.isConnectionInValid) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL, serviceTicket);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldDecimalRating(
				toCreate,
				TestStatics.Decimal.MIN_VALUE,
				TestStatics.Decimal.MAX_VALUE);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Decimal.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.", TestStatics.Decimal.RATING_STATIC_LINE, createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldDecimalRating(
				createdField,
				TestStatics.Decimal.MIN_VALUE_UPDATE,
				TestStatics.Decimal.MAX_VALUE_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Decimal.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.RATING_STATIC_LINE_UPDATE, updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Decimal.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				TestStatics.Decimal.RATING_STATIC_LINE_UPDATE, byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());
	}

	/**
	 *
	 */
	@Test
	public void testFormField_Table_CRUD()
	{
		if (this.isConnectionInValid) {
			return;
		}

		AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
		TestCase.assertNotNull(appRequestToken);

		String serviceTicket = appRequestToken.getServiceTicket();

		FormFieldClient formFieldClient = new FormFieldClient(BASE_URL,serviceTicket);
		FormDefinitionClient formDefinitionClient = new FormDefinitionClient(BASE_URL, serviceTicket);

		//0. Text Plain...
		Field tempTextPlainToCreate = new Field();
		tempTextPlainToCreate.setFieldName("Junit Plain tester bra zool");
		tempTextPlainToCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		tempTextPlainToCreate = formFieldClient.createFieldTextPlain(tempTextPlainToCreate);

		Form formDefinition = new Form();
		formDefinition.setFormType("JUnit Test Table Field");
		formDefinition.setFormDescription("JUnit Test Table Field");

		List<Field> frmFields = new ArrayList<Field>();
		frmFields.add(tempTextPlainToCreate);
		formDefinition.setFormFields(frmFields);

		formDefinition = formDefinitionClient.createFormDefinition(formDefinition);

		//1. Text...
		Field toCreate = new Field();
		toCreate.setFieldName(TestStatics.FIELD_NAME);
		toCreate.setFieldDescription(TestStatics.FIELD_DESCRIPTION);

		//2. Create...
		Field createdField = formFieldClient.createFieldTable(
				toCreate, formDefinition, TestStatics.TableField.SUM_DECIMALS);

		TestCase.assertNotNull("The 'Id' needs to be set.", createdField.getId());
		TestCase.assertEquals("'Name' mismatch.", TestStatics.FIELD_NAME, createdField.getFieldName());
		TestCase.assertEquals("'Description' mismatch.", TestStatics.FIELD_DESCRIPTION, createdField.getFieldDescription());
		TestCase.assertEquals("'Type' mismatch.", Field.Type.Table.toString(), createdField.getFieldType());
		TestCase.assertEquals("'Type Meta-Data' mismatch.",
				TestStatics.TableField.createMetaExpectancy(
						formDefinition.getId(), TestStatics.TableField.SUM_DECIMALS),
				createdField.getTypeMetaData());

		//3. Update...
		createdField.setFieldName(TestStatics.FIELD_NAME_UPDATE);
		createdField.setFieldDescription(TestStatics.FIELD_DESCRIPTION_UPDATE);
		Field updatedField = formFieldClient.updateFieldTable(
				createdField, formDefinition, TestStatics.TableField.SUM_DECIMALS_UPDATE);

		TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", updatedField.getId());
		TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.FIELD_NAME_UPDATE, updatedField.getFieldName());
		TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.FIELD_DESCRIPTION_UPDATE, updatedField.getFieldDescription());
		TestCase.assertEquals("UPDATE: 'Type' mismatch.", Field.Type.Table.toString(), updatedField.getFieldType());
		TestCase.assertEquals("UPDATE: 'Type Meta-Data' mismatch.",
				TestStatics.TableField.createMetaExpectancy(
						formDefinition.getId(), TestStatics.TableField.SUM_DECIMALS_UPDATE),
				updatedField.getTypeMetaData());

		//4. Get by Id...
		Field byIdField = formFieldClient.getFieldById(updatedField.getId());

		TestCase.assertNotNull("BY_ID: The 'Id' needs to be set.", byIdField.getId());
		TestCase.assertNotNull("BY_ID: The 'Name' needs to be set.", byIdField.getFieldName());
		TestCase.assertNotNull("BY_ID: The 'Description' needs to be set.", byIdField.getFieldDescription());
		TestCase.assertEquals("BY_ID: 'Type' mismatch.", Field.Type.Table.toString(), byIdField.getFieldType());
		TestCase.assertEquals("BY_ID: 'Type Meta-Data' mismatch.",
				TestStatics.TableField.createMetaExpectancy(
						formDefinition.getId(), TestStatics.TableField.SUM_DECIMALS_UPDATE), byIdField.getTypeMetaData());

		//5. Delete...
		Field deletedField = formFieldClient.deleteField(byIdField);
		TestCase.assertNotNull("DELETE: The 'Id' needs to be set.", deletedField.getId());

		//Delete the other temp field...
		formDefinitionClient.deleteFormDefinition(formDefinition);
		formFieldClient.deleteField(tempTextPlainToCreate);

	}
}
