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

package com.fluid.program.api.util.sql.syntax;

import java.sql.SQLException;

import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.exception.FluidSQLException;
import com.fluid.program.api.util.sql.impl.SQLFormFieldUtil;
import com.fluid.program.api.util.sql.syntax.impl.StoredProcedureSyntax;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class SyntaxFactory {
    private static SyntaxFactory syntaxFactory;

    /**
     *
     */
    private SyntaxFactory()
    {
        super();
    }

    /**
     *
     * @return
     */
    public static SyntaxFactory getInstance()
    {
        if(SyntaxFactory.syntaxFactory == null)
        {
            SyntaxFactory.syntaxFactory = new SyntaxFactory();
        }

        return SyntaxFactory.syntaxFactory;
    }

    /**
     *
     * @param sqlTypeParam
     * @param aliasParam
     * @return
     */
    public ISyntax getSyntaxFor(
            ABaseSQLUtil.SQLType sqlTypeParam,
            String aliasParam)
    {

        if(ISyntax.ProcedureMapping.isStoredProcedureMapping(aliasParam))
        {
            return new StoredProcedureSyntax(
                    aliasParam,
                    ISyntax.ProcedureMapping.getParamCountForAlias(aliasParam));
        }

        throw new FluidSQLException(
                new SQLException("Unable to find Syntax for alias '"+
                        aliasParam+"' and SQL Type '"+sqlTypeParam+"'."));
    }

    /**
     *
     * @param formFieldMappingParam
     * @return
     */
    public ISyntax getFieldValueSyntaxFor(
            ABaseSQLUtil.SQLType sqlTypeParam,
            SQLFormFieldUtil.FormFieldMapping formFieldMappingParam)
    {
        Long dataType = formFieldMappingParam.dataType;
        if(dataType == null)
        {
            return null;
        }

        switch (dataType.intValue())
        {
            case 1:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_1_Text);
            case 2:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_2_TrueFalse);
            case 3:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_3_ParagraphText);
            case 4:
                if(this.isPlain(formFieldMappingParam.metaData))
                {
                    return this.getSyntaxFor(
                            sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_4_MultiChoice);
                }
                else if(this.isSelectMany(formFieldMappingParam.metaData))
                {
                    return this.getSyntaxFor(
                            sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldMultipleValue_4_MultiChoice);
                }
                else
                {
                    throw new FluidSQLException(
                            new SQLException("Data Type '"+
                                    dataType
                                    +"' does not support '"+
                                    formFieldMappingParam.metaData+"'."));
                }

            case 5:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_5_DateTime);
            case 6:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_6_Decimal);
            case 7:
                return this.getSyntaxFor(
                        sqlTypeParam, ISyntax.ProcedureMapping.Field.GetFormFieldValue_7_TableField);
            default:
                throw new FluidSQLException(
                        new SQLException("Data Type '"+
                                dataType
                                +"' is not supported."));

        }

    }


    public static final String PLAIN = "Plain";
    public static final String SELECT_MANY = "Select Many";

    /**
     *
     * @param textToCheckParam
     * @return
     */
    private final boolean isPlain(String textToCheckParam)
    {
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty())
        {
            return false;
        }

        String toCheckLower = textToCheckParam.toLowerCase();

        return toCheckLower.startsWith(PLAIN.toLowerCase());
    }

    /**
     *
     * @param textToCheckParam
     * @return
     */
    private final boolean isSelectMany(String textToCheckParam)
    {
        if(textToCheckParam == null || textToCheckParam.trim().isEmpty())
        {
            return false;
        }

        String toCheckLower = textToCheckParam.toLowerCase();

        return toCheckLower.startsWith(SELECT_MANY.toLowerCase());
    }
}
