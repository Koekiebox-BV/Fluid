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

package com.fluidbpm.program.api.util.sql.impl;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.exception.FluidSQLException;
import com.fluidbpm.program.api.util.sql.syntax.ISyntax;
import com.fluidbpm.program.api.util.sql.syntax.SyntaxFactory;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.field.TableField;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.common.io.BaseEncoding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * SQL Utility class used for {@code Field} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseSQLUtil
 * @see Field
 */
public class SQLFormFieldUtil extends ABaseSQLUtil {

	private Map<Long, List<FormFieldMapping>> localDefinitionToFieldsMapping;
	private SQLFormDefinitionUtil sqlFormDefinitionUtil;

	/**
	 * Fluid mapping for a Form Field.
	 *
	 * @see ABaseFluidVO
	 */
	public static class FormFieldMapping extends ABaseFluidVO {
		public Long formDefinitionId;
		public Long formFieldId;
		public Long dataType;
		public String metaData;
		public String name;
		public String description;

		/**
		 * Form Field mapping.
		 *
		 * @param formDefinitionIdParam Form Definition Identifier.
		 * @param formFieldIdParam Form Field Identifier.
		 * @param dataTypeParam Form Field Data Type.
		 * @param metaDataParam Form Field Meta-Data.
		 * @param nameParam Form Field Name.
		 * @param descriptionParam Form Field Description.
		 *
		 * @see Field.Type
		 */
		public FormFieldMapping(
			Long formDefinitionIdParam,
			Long formFieldIdParam,
			Long dataTypeParam,
			String metaDataParam,
			String nameParam,
			String descriptionParam
		) {
			this.formDefinitionId = formDefinitionIdParam;
			this.formFieldId = formFieldIdParam;
			this.dataType = dataTypeParam;
			this.metaData = metaDataParam;
			this.name = nameParam;
			this.description = descriptionParam;
		}
	}

	/**
	 * New FormField util instance using {@code connectionParam}.
	 *
	 * @param connectionParam SQL Connection to use for Fields.
	 */
	public SQLFormFieldUtil(Connection connectionParam) {
		this(connectionParam, null);
	}

	/**
	 * New FormField util instance using {@code connectionParam}.
	 *
	 * @param connectionParam SQL Connection to use for Fields.
	 * @param cacheUtilParam The Cache Util for better performance.
	 */
	public SQLFormFieldUtil(Connection connectionParam, CacheUtil cacheUtilParam) {
		super(connectionParam, cacheUtilParam);
		this.localDefinitionToFieldsMapping = new HashMap();
	}

	/**
	 * New FormField util instance using {@code connectionParam}.
	 *
	 * @param connectionParam SQL Connection to use for Fields.
	 * @param cacheUtilParam The Cache Util for better performance.
	 * @param sqlFormDefinitionUtilParam The SQL Form Def utility for form definitions.
	 *                                   Useful for Table Fields.
	 */
	public SQLFormFieldUtil(
			Connection connectionParam,
			CacheUtil cacheUtilParam,
			SQLFormDefinitionUtil sqlFormDefinitionUtilParam) {
		this(connectionParam, cacheUtilParam);
		this.sqlFormDefinitionUtil = sqlFormDefinitionUtilParam;
	}

	/**
	 * Retrieve the Form Field mappings for Electronic Form {@code electronicFormIdParam}.
	 *
	 * @param electronicFormIdParam The Electronic Form to get Form Field mappings for.
	 * @return List of Form Field mappings.
	 */
	public List<FormFieldMapping> getFormFieldMappingForForm(Long electronicFormIdParam) {
		List<FormFieldMapping> returnVal = new ArrayList();

		if (electronicFormIdParam == null) return returnVal;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			Long formDefinitionId = this.getFormDefinitionId(electronicFormIdParam);

			//Local Mapping...
			//When we have the key by definition, we can just return.
			if (this.localDefinitionToFieldsMapping.containsKey(formDefinitionId)) {
				return this.localDefinitionToFieldsMapping.get(formDefinitionId);
			}

			ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
					this.getSQLTypeFromConnection(),
					ISyntax.ProcedureMapping.Field.GetFormFieldsForFormContainer);

			preparedStatement = this.getConnection().prepareStatement(syntax.getPreparedStatement());

			preparedStatement.setLong(1, electronicFormIdParam);

			resultSet = preparedStatement.executeQuery();

			//Iterate each of the form containers...
			while (resultSet.next()) {
				returnVal.add(this.mapFormFieldMapping(resultSet));
			}

			//Cache the mapping...
			this.localDefinitionToFieldsMapping.put(formDefinitionId, returnVal);

			return returnVal;
		} catch (SQLException sqlError) {
			throw new FluidSQLException(sqlError);
		} finally {
			this.closeStatement(preparedStatement, resultSet);
		}
	}

	/**
	 * Retrieve the Form Field mappings for Form Definition {@code electronicFormDefinitionIdParam}.
	 *
	 * @param formDefinitionIdParam The Electronic Form Definition
	 *                                        to get Form Field mappings for.
	 * @return List of Form Field mappings.
	 */
	public List<FormFieldMapping> getFormFieldMappingForFormDefinition(
			Long formDefinitionIdParam
	) {
		List<FormFieldMapping> returnVal = new ArrayList();

		if (formDefinitionIdParam == null || formDefinitionIdParam.longValue() < 1) return returnVal;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			//Local Mapping...
			//When we have the key by definition, we can just return.
			if (this.localDefinitionToFieldsMapping.containsKey(formDefinitionIdParam)) {
				return this.localDefinitionToFieldsMapping.get(formDefinitionIdParam);
			}

			ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
					this.getSQLTypeFromConnection(),
					ISyntax.ProcedureMapping.Field.GetFormFieldsForFormDefinition);

			preparedStatement = this.getConnection().prepareStatement(
					syntax.getPreparedStatement());

			preparedStatement.setLong(1, formDefinitionIdParam);

			resultSet = preparedStatement.executeQuery();

			//Iterate each of the form containers...
			while (resultSet.next()) {
				returnVal.add(this.mapFormFieldMapping(resultSet));
			}

			this.localDefinitionToFieldsMapping.put(formDefinitionIdParam, returnVal);

			return returnVal;
		} catch (SQLException sqlError) {
			throw new FluidSQLException(sqlError);
		} finally {
			this.closeStatement(preparedStatement, resultSet);
		}
	}

	/**
	 * Retrieves the Form Definition Id for the
	 * Electronic Form with id {@code electronicFormIdParam}.
	 *
	 * @param electronicFormIdParam The Electronic Form to fetch fields for.
	 * @return The Form Definition for Electronic Form {@code electronicFormIdParam}.
	 */
	public Long getFormDefinitionId(Long electronicFormIdParam) {
		if (electronicFormIdParam == null) return null;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet;
		try {
			ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
					this.getSQLTypeFromConnection(),
					ISyntax.ProcedureMapping.Field.GetFormDefinitionForFormContainer);

			preparedStatement = this.getConnection().prepareStatement(
					syntax.getPreparedStatement());

			preparedStatement.setLong(1, electronicFormIdParam);

			resultSet = preparedStatement.executeQuery();

			//Iterate each of the form containers...
			while (resultSet.next()) {
				return resultSet.getLong(1);
			}
			return null;
		} catch (SQLException sqlError) {
			throw new FluidSQLException(sqlError);
		} finally {
			this.closeStatement(preparedStatement);
		}
	}

	/**
	 * Retrieves the Form Fields {@code VALUES} for the
	 * Electronic Form with id {@code electronicFormIdParam}.
	 *
	 * @param electronicFormIdParam The Electronic Form to fetch fields for.
	 * @param includeTableFieldsParam Whether to populate the table fields.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 *
	 * @return The Form Fields for Electronic Form {@code electronicFormIdParam}.
	 */
	public List<Field> getFormFields(
		Long electronicFormIdParam,
		boolean includeTableFieldsParam,
		boolean includeTableFieldFormRecordInfoParam
	) {
		List<Field> returnVal = new ArrayList();
		if (electronicFormIdParam == null) return returnVal;

		List<FormFieldMapping> fieldMappings = this.getFormFieldMappingForForm(electronicFormIdParam);
		if (fieldMappings == null || fieldMappings.isEmpty()) return returnVal;

		//Get the values for each of the fields...
		for (FormFieldMapping fieldMapping : fieldMappings) {
			//Skip if ignore Table Fields...
			if (!includeTableFieldsParam && fieldMapping.dataType == UtilGlobal.FieldTypeId._7_TABLE_FIELD) {//Table Field...
				continue;
			}

			Field fieldToAdd = this.getFormFieldValueFor(
					fieldMapping,
					electronicFormIdParam,
					includeTableFieldFormRecordInfoParam);

			if (fieldToAdd == null) continue;

			//When table field...
			if (includeTableFieldsParam && (fieldToAdd.getFieldValue() instanceof TableField)) {
				TableField tableField = (TableField)fieldToAdd.getFieldValue();
				if (tableField.getTableRecords() != null && !tableField.getTableRecords().isEmpty()) {
					for (Form tableRecordForm : tableField.getTableRecords()) {
						tableRecordForm.setFormFields(
								this.getFormFields(
										tableRecordForm.getId(),
										false,
										false));
					}
				}
			}

			returnVal.add(fieldToAdd);
		}

		return returnVal;
	}

	/**
	 * Retrieves the Form Field value for Electronic Form {@code formContainerIdParam}.
	 *
	 * @param formFieldMappingParam The mapping to use.
	 * @param formContainerIdParam The Electronic Form Id.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 *
	 * @return Populated {@code Field} value.
	 */
	public Field getFormFieldValueFor(
		FormFieldMapping formFieldMappingParam,
		Long formContainerIdParam,
		boolean includeTableFieldFormRecordInfoParam
	) {
		if (formFieldMappingParam == null) return null;

		//First attempt to fetch from the cache...
		if (this.getCacheUtil() != null) {
			CacheUtil.CachedFieldValue cachedFieldValue =
					this.getCacheUtil().getCachedFieldValueFrom(
						formFieldMappingParam.formDefinitionId,
						formContainerIdParam,
						formFieldMappingParam.formFieldId
					);

			if (cachedFieldValue != null) {
				Field field = cachedFieldValue.getCachedFieldValueAsField();
				if (field != null) {
					field.setFieldName(formFieldMappingParam.name);
					return field;
				}
			}
		}

		//Now use a database lookup...
		Field returnVal = null;
		PreparedStatement preparedStatement = null, preparedStatementForTblInfo = null;
		ResultSet resultSet = null,resultSetForTblInfo = null;
		try {
			ISyntax syntax = SyntaxFactory.getInstance().getFieldValueSyntaxFor(
					this.getSQLTypeFromConnection(), formFieldMappingParam);

			if (syntax != null) {
				preparedStatement = this.getConnection().prepareStatement(
						syntax.getPreparedStatement());

				preparedStatement.setLong(1, formFieldMappingParam.formDefinitionId);
				preparedStatement.setLong(2, formFieldMappingParam.formFieldId);
				preparedStatement.setLong(3, formContainerIdParam);

				resultSet = preparedStatement.executeQuery();
			}

			final int dataTypeIntVal = formFieldMappingParam.dataType.intValue();
			switch (dataTypeIntVal) {
				//Text...
				case UtilGlobal.FieldTypeId._1_TEXT:
				if (resultSet.next()) {
					returnVal = new Field(
							formFieldMappingParam.name,
							resultSet.getString(1),
							Field.Type.Text);
				}
				break;
				//True False...
				case UtilGlobal.FieldTypeId._2_TRUE_FALSE:
				if (resultSet.next()) {
					returnVal = new Field(
							formFieldMappingParam.name,
							resultSet.getBoolean(1),
							Field.Type.TrueFalse);
				}
				break;
				//Paragraph Text...
				case UtilGlobal.FieldTypeId._3_PARAGRAPH_TEXT:
				if (resultSet.next()) {
					returnVal = new Field(
							formFieldMappingParam.name,
							resultSet.getString(1),
							Field.Type.ParagraphText);
				}
				break;
				//Multiple Choice...
				case UtilGlobal.FieldTypeId._4_MULTI_CHOICE:
					MultiChoice multiChoice = new MultiChoice();

					List<String> selectedValues = new ArrayList();
					while (resultSet.next()) {
						selectedValues.add(resultSet.getString(1));
					}

					multiChoice.setSelectedMultiChoices(selectedValues);
					if (!selectedValues.isEmpty()) {
						returnVal = new Field(
								formFieldMappingParam.name,
								multiChoice);
					}
				break;
				//Date Time...
				case UtilGlobal.FieldTypeId._5_DATE_TIME:
					if (resultSet.next()) {
						Date date = resultSet.getTimestamp(1);
						returnVal = new Field(
								formFieldMappingParam.name,
								date,
								Field.Type.DateTime);
					}
				break;
				//Decimal...
				case UtilGlobal.FieldTypeId._6_DECIMAL:
					if (resultSet.next()) {
						returnVal = new Field(
								formFieldMappingParam.name,
								resultSet.getDouble(1),
								Field.Type.Decimal);
					}
				break;
				//Table Field...
				case UtilGlobal.FieldTypeId._7_TABLE_FIELD:
					List<Long> formContainerIds = new ArrayList();
					while (resultSet.next()) {
						formContainerIds.add(resultSet.getLong(1));
					}

					//Break if empty...
					if (formContainerIds.isEmpty()) break;

					TableField tableField = new TableField();

					final List<Form> formRecords = new ArrayList();

					//Populate all the ids for forms...
					formContainerIds.forEach(formContId -> {
						formRecords.add(new Form(formContId));
					});

					//Retrieve the info for the table record...
					if (includeTableFieldFormRecordInfoParam) {
						ISyntax syntaxForFormContInfo = SyntaxFactory.getInstance().getSyntaxFor(
								this.getSQLTypeFromConnection(),
								ISyntax.ProcedureMapping.Form.GetFormContainerInfo);

						preparedStatementForTblInfo = this.getConnection().prepareStatement(
								syntaxForFormContInfo.getPreparedStatement());

						for (Form formRecordToSetInfoOn : formRecords) {
							preparedStatementForTblInfo.setLong(
									1, formRecordToSetInfoOn.getId());

							resultSetForTblInfo = preparedStatementForTblInfo.executeQuery();
							if (resultSetForTblInfo.next()) {
								Long formTypeId = resultSetForTblInfo.getLong(
										SQLFormUtil.SQLColumnIndex._02_FORM_TYPE);

								formRecordToSetInfoOn.setFormTypeId(formTypeId);
								formRecordToSetInfoOn.setFormType(
										this.sqlFormDefinitionUtil == null ? null :
												this.sqlFormDefinitionUtil.getFormDefinitionIdAndTitle().get(formTypeId));

								formRecordToSetInfoOn.setTitle(resultSetForTblInfo.getString(SQLFormUtil.SQLColumnIndex._03_TITLE));

								Date created = resultSetForTblInfo.getTimestamp(SQLFormUtil.SQLColumnIndex._04_CREATED);
								Date lastUpdated = resultSetForTblInfo.getTimestamp(SQLFormUtil.SQLColumnIndex._05_LAST_UPDATED);

								//Created...
								if (created != null) formRecordToSetInfoOn.setDateCreated(new Date(created.getTime()));

								//Last Updated...
								if (lastUpdated != null) formRecordToSetInfoOn.setDateLastUpdated(new Date(lastUpdated.getTime()));
							}
						}
					}

					tableField.setTableRecords(formRecords);

					returnVal = new Field(
							formFieldMappingParam.name,
							tableField,
							Field.Type.Table);
				break;
				//Text Encrypted...
				case UtilGlobal.FieldTypeId._8_TEXT_ENCRYPTED:
					if (ENCRYPTED_FIELD_KEY == null) throw new SQLException("Unable decrypt encrypted field if key is not set.");

					if (resultSet.next()) {
						String encryptedText = resultSet.getString(1);
						byte[] decryptedBytes = this.decryptECB(BaseEncoding.base16().decode(encryptedText));
						returnVal = new Field(
								formFieldMappingParam.name,
								new String(decryptedBytes),
								Field.Type.TextEncrypted);
					}
				break;
				//Label...
				case UtilGlobal.FieldTypeId._9_LABEL:
					returnVal = new Field(
							formFieldMappingParam.name,
							formFieldMappingParam.description,
							Field.Type.Label);
				break;
				default:
					throw new SQLException("Unable to map '"+
							formContainerIdParam.intValue() +"' on data type '"+
							dataTypeIntVal+"' to Form Field value for SQL.");
			}
			return returnVal;
		} catch (SQLException sqlError) {
			throw new FluidSQLException(sqlError);
		} finally {
			this.closeStatement(preparedStatement);
			this.closeStatement(preparedStatementForTblInfo);
		}
	}

	/**
	 * Maps a {@code ResultSet} to a new instance of {@code FormFieldMapping}.
	 *
	 * @param resultSetParam The {@code ResultSet} used to create an {@code FormFieldMapping}.
	 * @return FormFieldMapping from {@code ResultSet}.
	 * @throws SQLException
	 *
	 * @see ResultSet
	 * @see FormFieldMapping
	 */
	private FormFieldMapping mapFormFieldMapping(ResultSet resultSetParam)
	throws SQLException {
		return new FormFieldMapping(
				resultSetParam.getLong(1),
				resultSetParam.getLong(2),
				resultSetParam.getLong(3),
				resultSetParam.getString(4),
				resultSetParam.getString(5),
				resultSetParam.getString(6));
	}
}
