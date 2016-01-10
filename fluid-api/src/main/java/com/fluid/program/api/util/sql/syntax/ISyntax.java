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

import java.sql.Connection;
import java.sql.PreparedStatement;

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
 * @see SyntaxFactory
 * @see com.fluid.program.api.util.sql.syntax.impl.StoredProcedureSyntax
 */
public interface ISyntax {

    /**
     * Mapping of the Fluid internally used Stored Procedures.
     */
    public static final class ProcedureMapping{

        /**
         * Stored Procedures for Form Definitions.
         *
         * @see com.fluid.program.api.vo.Form
         */
        public static final class FormDefinition{
            public static final String GetFormDefinitions =
                    "Fluid_GetFormDefinitions";
        }

        /**
         * Stored Procedures for Form.
         *
         * @see com.fluid.program.api.vo.Form
         */
        public static final class Form{
            public static final String GetFormContainersTableFieldFormContainers =
                    "Fluid_GetFormContainersTableFieldFormContainers";

            public static final String GetFormContainersChildFormContainers =
                    "Fluid_GetFormContainersChildFormContainers";

            public static final String GetFormContainersParentFormContainer =
                    "Fluid_GetFormContainersParentFormContainer";

        }

        /**
         * Stored Procedures for Field.
         *
         * @see com.fluid.program.api.vo.Field
         */
        public static final class Field{
            public static final String GetFormFieldsForFormContainer =
                    "Fluid_GetFormFieldsForFormContainer";

            //Field Values...
            public static final String GetFormFieldValue_1_Text =
                    "Fluid_GetFormFieldValueText";

            public static final String GetFormFieldValue_2_TrueFalse =
                    "Fluid_GetFormFieldValueTrueFalse";

            public static final String GetFormFieldValue_3_ParagraphText =
                    "Fluid_GetFormFieldValueParagraphText";

            public static final String GetFormFieldValue_4_MultiChoice =
                    "Fluid_GetFormFieldValueMultiChoice";

            public static final String GetFormFieldMultipleValue_4_MultiChoice =
                    "Fluid_GetFormFieldMultipleValueMultiChoice";

            public static final String GetFormFieldValue_5_DateTime =
                    "Fluid_GetFormFieldValueDateTime";

            public static final String GetFormFieldValue_6_Decimal =
                    "Fluid_GetFormFieldValueDecimal";

            public static final String GetFormFieldValue_7_TableField =
                    "Fluid_GetFormFieldValueTableField";
        }

        /**
         * Checks whether stored procedure is
         * part of the Fluid Stored Procedure mapping.
         *
         * @param aliasParam Stored Procedure.
         * @return Whether stored procedure is part of {@code this} mapping.
         */
        public static boolean isStoredProcedureMapping(
                String aliasParam)
        {
            if(aliasParam == null || aliasParam.trim().isEmpty())
            {
                return false;
            }

            for(String alias :  allAliases())
            {
                if(alias.equals(aliasParam))
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * List of Stored Procedures.
         *
         * @return Stored Procedure names.
         */
        public static String[] allAliases()
        {
            return new String[]{
                    FormDefinition.GetFormDefinitions,
                    Form.GetFormContainersTableFieldFormContainers,
                    Form.GetFormContainersChildFormContainers,
                    Form.GetFormContainersParentFormContainer,
                    Field.GetFormFieldsForFormContainer,
                    Field.GetFormFieldValue_1_Text,
                    Field.GetFormFieldValue_2_TrueFalse,
                    Field.GetFormFieldValue_3_ParagraphText,
                    Field.GetFormFieldValue_4_MultiChoice,
                    Field.GetFormFieldMultipleValue_4_MultiChoice,
                    Field.GetFormFieldValue_5_DateTime,
                    Field.GetFormFieldValue_6_Decimal,
                    Field.GetFormFieldValue_7_TableField,
            };
        }

        /**
         * Gets the parameter count for Stored Procedure {@code aliasParam}.
         *
         * @return Number of parameters for Stored Procedure {@code aliasParam}.
         */
        public static int getParamCountForAlias(String aliasParam)
        {
            //Forms...
            if(Form.GetFormContainersTableFieldFormContainers.equals(aliasParam))
            {
                return 1;
            }
            else if(Form.GetFormContainersChildFormContainers.equals(aliasParam))
            {
                return 1;
            }
            else if(Form.GetFormContainersParentFormContainer.equals(aliasParam))
            {
                return 1;
            }
            //Fields...
            else if(Field.GetFormFieldsForFormContainer.equals(aliasParam))
            {
                return 1;
            }
            //Specific Values...
            else if(Field.GetFormFieldValue_1_Text.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_2_TrueFalse.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_3_ParagraphText.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_4_MultiChoice.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldMultipleValue_4_MultiChoice.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_5_DateTime.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_6_Decimal.equals(aliasParam))
            {
                return 3;
            }
            else if(Field.GetFormFieldValue_7_TableField.equals(aliasParam))
            {
                return 3;
            }

            return 0;
        }
    }

    /**
     * Gets the SQL Prepared Statement to execute against the DBMS engine.
     *
     * @return Complete SQL Prepared statement.
     *
     * @see PreparedStatement
     */
    public abstract String getPreparedStatement();
}
