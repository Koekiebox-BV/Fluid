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

/**
 * Created by jasonbruwer on 15/07/17.
 */
public interface ISyntax {

    /**
     *
     */
    public static final class ProcedureMapping{

        /**
         *
         */
        public static final class FormDefinition{
            public static final String GetFormDefinitions =
                    "Fluid_GetFormDefinitions";
        }

        /**
         *
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
         *
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
         *
         * @param aliasParam
         * @return
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
         *
         * @return
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
         *
         * @return
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
     *
     * @return
     */
    public abstract String getPreparedStatement();
}
