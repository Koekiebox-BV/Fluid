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

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Factory class used to construct SQL stored procedures.
 *
 * @author jasonbruwer
 * @see Connection
 * @see javax.sql.DataSource
 * @see PreparedStatement
 * @see java.sql.Statement
 * @see SyntaxFactory
 * @see com.fluidbpm.program.api.util.sql.syntax.impl.StoredProcedureSyntax
 * @since v1.0
 */
public interface ISyntax {
    /**
     * Mapping of the Fluid internally used Stored Procedures.
     */
    public static final class ProcedureMapping {
        /**
         * Stored Procedures for Form Definitions.
         *
         * @see com.fluidbpm.program.api.vo.form.Form
         */
        public static final class FormDefinition {
            public static final String GetFormDefinitions =
                    "Fluid_GetFormDefinitions";
        }

        /**
         * Stored Procedures for Form.
         *
         * @see com.fluidbpm.program.api.vo.form.Form
         */
        public static final class Form {
            public static final String GetTableFormsPage = "Fluid_GetTableFormsPage";
            public static final String GetTableFormsCount = "Fluid_GetTableFormsCount";
            public static final String GetFormContainersTableFieldFormContainers =
                    "Fluid_GetFormContainersTableFieldFormContainers";

            public static final String GetFormContainersChildFormContainers =
                    "Fluid_GetFormContainersChildFormContainers";

            public static final String GetFormContainersChildFormContainersWithStates =
                    "Fluid_GetFormContainersChildFormContainersWithStates";

            public static final String GetFormContainersParentFormContainer =
                    "Fluid_GetFormContainersParentFormContainer";

            public static final String GetFormContainerInfo = "Fluid_GetFormContainerInfo";
        }

        /**
         * Stored Procedures for Field.
         *
         * @see com.fluidbpm.program.api.vo.field.Field
         */
        public static final class Field {
            public static final String GetFormDefinitionForFormContainer =
                    "Fluid_GetFormDefinitionForFormContainer";

            public static final String GetFormFieldsForFormContainer =
                    "Fluid_GetFormFieldsForFormContainer";

            public static final String GetFormFieldsForFormDefinition =
                    "Fluid_GetFormFieldsForFormDefinition";

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

            public static final String GetFormFieldValue_8_TextEncrypted =
                    "Fluid_GetFormFieldValueTextEncrypted";
        }

        /**
         * Checks whether stored procedure is
         * part of the Fluid Stored Procedure mapping.
         *
         * @param alias Stored Procedure.
         * @return Whether stored procedure is part of {@code this} mapping.
         */
        public static boolean isStoredProcedureMapping(String alias) {
            if (UtilGlobal.isBlank(alias)) return false;
            for (String aliasIter : allAliases()) if (aliasIter.equals(alias)) return true;
            return false;
        }

        /**
         * List of Stored Procedures.
         *
         * @return Stored Procedure names.
         */
        public static String[] allAliases() {
            return new String[]{
                    FormDefinition.GetFormDefinitions,
                    Form.GetTableFormsPage,
                    Form.GetTableFormsCount,
                    Form.GetFormContainersTableFieldFormContainers,
                    Form.GetFormContainersChildFormContainers,
                    Form.GetFormContainersChildFormContainersWithStates,
                    Form.GetFormContainersParentFormContainer,
                    Form.GetFormContainerInfo,
                    Field.GetFormFieldsForFormDefinition,
                    Field.GetFormFieldsForFormContainer,
                    Field.GetFormDefinitionForFormContainer,
                    Field.GetFormFieldValue_1_Text,
                    Field.GetFormFieldValue_2_TrueFalse,
                    Field.GetFormFieldValue_3_ParagraphText,
                    Field.GetFormFieldValue_4_MultiChoice,
                    Field.GetFormFieldMultipleValue_4_MultiChoice,
                    Field.GetFormFieldValue_5_DateTime,
                    Field.GetFormFieldValue_6_Decimal,
                    Field.GetFormFieldValue_7_TableField,
                    Field.GetFormFieldValue_8_TextEncrypted,
            };
        }

        /**
         * Gets the parameter count for stored procedure {@code alias}.
         *
         * @param alias The stored procedure alias.
         * @return Number of parameters, or zero for a null or unknown alias.
         */
        public static int getParamCountForAlias(String alias) {
            if (alias == null) return 0;

            switch (alias) {
                case Form.GetTableFormsPage:
                    return 4;
                case Form.GetTableFormsCount:
                    return 2;
                case Form.GetFormContainersTableFieldFormContainers:
                case Form.GetFormContainersChildFormContainers:
                case Form.GetFormContainersChildFormContainersWithStates:
                case Form.GetFormContainersParentFormContainer:
                case Form.GetFormContainerInfo:
                case Field.GetFormFieldsForFormContainer:
                case Field.GetFormFieldsForFormDefinition:
                case Field.GetFormDefinitionForFormContainer:
                    return 1;
                case Field.GetFormFieldValue_1_Text:
                case Field.GetFormFieldValue_2_TrueFalse:
                case Field.GetFormFieldValue_3_ParagraphText:
                case Field.GetFormFieldValue_4_MultiChoice:
                case Field.GetFormFieldMultipleValue_4_MultiChoice:
                case Field.GetFormFieldValue_5_DateTime:
                case Field.GetFormFieldValue_6_Decimal:
                case Field.GetFormFieldValue_7_TableField:
                case Field.GetFormFieldValue_8_TextEncrypted:
                    return 3;
                case FormDefinition.GetFormDefinitions:
                default:
                    return 0;
            }
        }

    }

    /**
     * Gets the SQL Prepared Statement to execute against the DBMS engine.
     *
     * @return Complete SQL Prepared statement.
     * @see PreparedStatement
     */
    public abstract String getPreparedStatement();
}
