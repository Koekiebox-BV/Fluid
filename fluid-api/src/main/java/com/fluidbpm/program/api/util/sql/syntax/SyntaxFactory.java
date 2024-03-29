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

package com.fluidbpm.program.api.util.sql.syntax;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.exception.FluidSQLException;
import com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluidbpm.program.api.util.sql.syntax.impl.StoredProcedureSyntax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Factory class used to construct SQL stored procedures.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Connection
 * @see javax.sql.DataSource
 * @see PreparedStatement
 * @see java.sql.Statement
 */
public class SyntaxFactory {
	private static SyntaxFactory syntaxFactory;

	public static final String PLAIN = "Plain";
	public static final String SELECT_MANY = "Select Many";

	/**
	 * Default constructor.
	 */
	private SyntaxFactory()
	{
		super();
	}

	/**
	 * Gets the single instance of the {@code SyntaxFactory}.
	 *
	 * @return single instance of {@code this}.
	 */
	public static SyntaxFactory getInstance() {
		if (SyntaxFactory.syntaxFactory == null) {
			SyntaxFactory.syntaxFactory = new SyntaxFactory();
		}

		return SyntaxFactory.syntaxFactory;
	}

	/**
	 * Returns the {@code ISyntax} from the {@code sqlTypeParam} and {@code aliasParam}.
	 *
	 * @param sqlTypeParam The type of SQL DB engine.
	 * @param aliasParam The alias or stored procedure.
	 *
	 * @return implementation of {@code ISyntax}.
	 *
	 * @see ISyntax
	 * @see com.fluidbpm.program.api.util.sql.ABaseSQLUtil.SQLServerType
	 */
	public ISyntax getSyntaxFor(
			ABaseSQLUtil.SQLServerType sqlTypeParam,
			String aliasParam) {

		if (ISyntax.ProcedureMapping.isStoredProcedureMapping(aliasParam)) {
			return new StoredProcedureSyntax(
					aliasParam, ISyntax.ProcedureMapping.getParamCountForAlias(aliasParam));
		}

		throw new FluidSQLException(new SQLException("Unable to find Syntax for alias '"+
				aliasParam+"' and SQL Type '"+sqlTypeParam+"'."));
	}

	/**
	 * Returns the {@code ISyntax} from the {@code sqlTypeParam} and {@code formFieldMappingParam}.
	 *
	 * @param sqlTypeParam The type of SQL DB engine.
	 * @param formFieldMappingParam The Form Field mapping.
	 * @return implementation of {@code ISyntax}.
	 *
	 * @see ISyntax
	 * @see com.fluidbpm.program.api.util.sql.impl.SQLFormFieldUtil.FormFieldMapping
	 */
	public ISyntax getFieldValueSyntaxFor(
		ABaseSQLUtil.SQLServerType sqlTypeParam,
		SQLFormFieldUtil.FormFieldMapping formFieldMappingParam
	) {
		Long dataType = formFieldMappingParam.dataType;
		if (dataType == null) return null;

		switch (dataType.intValue()) {
			case UtilGlobal.FieldTypeId._1_TEXT:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_1_Text);
			case UtilGlobal.FieldTypeId._2_TRUE_FALSE:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_2_TrueFalse);
			case UtilGlobal.FieldTypeId._3_PARAGRAPH_TEXT:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_3_ParagraphText);
			case UtilGlobal.FieldTypeId._4_MULTI_CHOICE:
				if (this.isPlain(formFieldMappingParam.metaData)) {
					return this.getSyntaxFor(
							sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_4_MultiChoice);
				} else if (this.isSelectMany(formFieldMappingParam.metaData)) {
					return this.getSyntaxFor(
							sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldMultipleValue_4_MultiChoice);
				} else {
					throw new FluidSQLException(
							new SQLException("Data Type '"+
									dataType
									+"' does not support '"+
									formFieldMappingParam.metaData+"'."));
				}

			case UtilGlobal.FieldTypeId._5_DATE_TIME:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_5_DateTime);
			case UtilGlobal.FieldTypeId._6_DECIMAL:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_6_Decimal);
			case UtilGlobal.FieldTypeId._7_TABLE_FIELD:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_7_TableField);
			case UtilGlobal.FieldTypeId._8_TEXT_ENCRYPTED:
				return this.getSyntaxFor(sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_8_TextEncrypted);
			case UtilGlobal.FieldTypeId._9_LABEL:
				return null;
			default:
				throw new FluidSQLException(new SQLException(String.format("Data Type '%s' is not supported.", dataType)));
		}
	}

	/**
	 * Checks whether {@code textToCheckParam} is Plain.
	 *
	 * @param textToCheckParam The String to check.
	 *
	 * @return Whether the {@code textToCheckParam} is of type Plain.
	 */
	private boolean isPlain(String textToCheckParam) {
		if (textToCheckParam == null || textToCheckParam.trim().isEmpty()) {
			return false;
		}

		String toCheckLower = textToCheckParam.toLowerCase();
		return toCheckLower.startsWith(PLAIN.toLowerCase());
	}

	/**
	 * Checks whether {@code textToCheckParam} is Select Many.
	 *
	 * @param textToCheckParam The String to check.
	 *
	 * @return Whether the {@code textToCheckParam} is of type Select Many.
	 */
	private boolean isSelectMany(String textToCheckParam) {
		if (textToCheckParam == null || textToCheckParam.trim().isEmpty()) {
			return false;
		}

		String toCheckLower = textToCheckParam.toLowerCase();
		return toCheckLower.startsWith(SELECT_MANY.toLowerCase());
	}
}
