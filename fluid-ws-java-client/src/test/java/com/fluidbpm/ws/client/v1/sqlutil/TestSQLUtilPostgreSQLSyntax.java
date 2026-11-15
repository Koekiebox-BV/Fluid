package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.util.sql.ABaseSQLUtil.SQLServerType;
import com.fluidbpm.program.api.util.sql.syntax.ISyntax;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import org.junit.Test;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;

/** Database-free coverage of the SQL utility contract shared by all database engines. */
public class TestSQLUtilPostgreSQLSyntax {
    private Map<String, Integer> routines() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put(ISyntax.ProcedureMapping.FormDefinition.GetFormDefinitions, 0);
        result.put(ISyntax.ProcedureMapping.Form.GetFormContainersTableFieldFormContainers, 1);
        result.put(ISyntax.ProcedureMapping.Form.GetFormContainersChildFormContainers, 1);
        result.put(ISyntax.ProcedureMapping.Form.GetFormContainersChildFormContainersWithStates, 1);
        result.put(ISyntax.ProcedureMapping.Form.GetFormContainersParentFormContainer, 1);
        result.put(ISyntax.ProcedureMapping.Form.GetFormContainerInfo, 1);
        result.put(ISyntax.ProcedureMapping.Field.GetFormDefinitionForFormContainer, 1);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldsForFormContainer, 1);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldsForFormDefinition, 1);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_1_Text, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_2_TrueFalse, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_3_ParagraphText, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_4_MultiChoice, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_5_DateTime, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_6_Decimal, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_7_TableField, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldValue_8_TextEncrypted, 3);
        result.put(ISyntax.ProcedureMapping.Field.GetFormFieldMultipleValue_4_MultiChoice, 3);
        result.put(ISyntax.ProcedureMapping.Form.GetTableFormsPage, 4);
        result.put(ISyntax.ProcedureMapping.Form.GetTableFormsCount, 2);
        return result;
    }

    @Test
    public void allAliasesUseTheCorrectDatabaseCallAndParameterCount() {
        Map<String, Integer> expected = routines();
        assertEquals(20, expected.size());
        assertEquals(expected.keySet(), new java.util.HashSet<>(
                java.util.Arrays.asList(ISyntax.ProcedureMapping.allAliases())));
        for (Map.Entry<String, Integer> entry : expected.entrySet()) {
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < entry.getValue(); i++) {
                if (i > 0) args.append(',');
                args.append('?');
            }
            String call = entry.getKey() + "(" + args + ")";
            for (SQLServerType type : new SQLServerType[]{
                    SQLServerType.PostgreSQL, SQLServerType.MySQL, SQLServerType.MicrosoftSQL}) {
                NativeSQLQuery query = SQLUtilTestQueries.routine(type, entry.getKey());
                String expectedSql = type == SQLServerType.PostgreSQL
                        ? "SELECT * FROM " + call : "{CALL " + call + "}";
                String sql = type == SQLServerType.PostgreSQL
                        ? query.getQuery() : query.getStoredProcedure();
                assertEquals(type + ": " + entry.getKey(), expectedSql,
                        sql.trim().replaceAll("\\s+", " "));
                assertEquals(type != SQLServerType.PostgreSQL, query.isTypeStoredProcedure());
                if (type == SQLServerType.PostgreSQL) assertNull(query.getStoredProcedure());
                else assertNull(query.getQuery());
            }
        }
    }

    @Test
    public void nullAndUnknownAliasesHaveNoParameters() {
        assertEquals(0, ISyntax.ProcedureMapping.getParamCountForAlias(null));
        assertEquals(0, ISyntax.ProcedureMapping.getParamCountForAlias(""));
        assertEquals(0, ISyntax.ProcedureMapping.getParamCountForAlias("unknown"));
    }

    @Test
    public void postgresIsRecognizedFromJdbcMetadata() {
        assertEquals(SQLServerType.PostgreSQL,
                SQLServerType.getSQLTypeFromProductName("PostgreSQL"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownDatabaseIsRejectedForNativeTests() {
        SQLUtilTestQueries.routine(SQLServerType.Unknown,
                ISyntax.ProcedureMapping.FormDefinition.GetFormDefinitions);
    }

    @Test
    public void configuredDatabaseSelectsTheNativeQueryPath() {
        String previous = System.getProperty("fluid.test.sqlServerType");
        try {
            System.setProperty("fluid.test.sqlServerType", "PostgreSQL");
            NativeSQLQuery query = SQLUtilTestQueries.routine(
                    ISyntax.ProcedureMapping.FormDefinition.GetFormDefinitions);
            assertFalse(query.isTypeStoredProcedure());
            assertNotNull(query.getQuery());
        } finally {
            if (previous == null) System.clearProperty("fluid.test.sqlServerType");
            else System.setProperty("fluid.test.sqlServerType", previous);
        }
    }
}
