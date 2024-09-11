/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.migrator;

import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.role.AdminPermission;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import org.junit.After;
import org.junit.Test;

import java.util.UUID;

public class TestMigratorPlan extends ABaseLoggedInTestCase {
    private static class Template {
        private static class Field {
            public static final String TEXT_PLAIN = "Migrate Text Plain";
            public static final String TEXT_ENCRYPTED = "Migrate Text Encrypted";
            public static final String TEXT_PARAGRAPH = "Migrate Text Paragraph";
            public static final String TEXT_LAT_LONG = "Migrate Lat Long";

            public static final String TABLE = "Migrate Table Field";
        }

        private static class FieldUser {
            public static final String NATIONALITIES = "Nationalities";
        }

        private static class Form {
            public static final String FORM_DEF = "Migrate Form Def";
            public static final String FORM_DEF_TABLE = Field.TABLE;
        }

        private static class Flow {
            public static final String NAME = "Migration Flow";
            private static class Step {
                public static final String INTRO = "Introduction";
                public static final String ASSIGN = "Assign To Me";
                public static final String EXIT = "Exit";
            }
        }

        private static class UserQuery {
            public static final String FETCH_FORM_DEF = String.format("Fetch %s", Template.Form.FORM_DEF);
        }

        private static class Role {
            public static final String ONE = "Role Numbre Uno";
        }
    }

    @Test
    public void testMigratePlan() {
        if (isConnectionInValid) return;

        MigratorPlan.MigrateOptPlan plan = MigratorPlan.MigrateOptPlan.builder()
                // Fields:
                .formFieldsNonTable(new MigratorField.MigrateOptField[] {
                        MigratorField.MigrateOptFieldText.builder()
                                .fieldName(Template.Field.TEXT_PLAIN)
                                .fieldDescription(Template.Field.TEXT_PLAIN)
                                .build(),
                        MigratorField.MigrateOptFieldText.builder()
                                .fieldName(Template.Field.TEXT_ENCRYPTED)
                                .fieldDescription(Template.Field.TEXT_ENCRYPTED)
                                .encrypted(true)
                                .build(),
                        MigratorField.MigrateOptFieldText.builder()
                                .fieldName(Template.Field.TEXT_PARAGRAPH)
                                .fieldDescription(Template.Field.TEXT_PARAGRAPH)
                                .paragraphText(true)
                                .build(),
                        MigratorField.MigrateOptFieldText.builder()
                                .fieldName(Template.Field.TEXT_LAT_LONG)
                                .fieldDescription(Template.Field.TEXT_LAT_LONG)
                                .latAndLong(true)
                                .build(),
                })
                // Table Fields:
                .formFieldsTable(new MigratorField.MigrateOptFieldTable[] {
                        MigratorField.MigrateOptFieldTable.builder()
                                .fieldName(Template.Field.TABLE)
                                .formType(Template.Form.FORM_DEF_TABLE)
                                .fieldAndFormDescription(Template.Field.TABLE)
                                .fields(new String[] {
                                        Template.Field.TEXT_PLAIN, Template.Field.TEXT_LAT_LONG
                                })
                                .build()
                })
                // Form Definitions:
                .formDefs(new MigratorForm.MigrateOptForm[] {
                        MigratorForm.MigrateOptForm.builder()
                                .formType(Template.Form.FORM_DEF)
                                .formDescription(Template.Form.FORM_DEF)
                                .fields(new String[]{
                                        Template.Field.TEXT_PLAIN,
                                        Template.Field.TEXT_ENCRYPTED,
                                        Template.Field.TEXT_PARAGRAPH,
                                        Template.Field.TEXT_LAT_LONG,
                                        Template.Field.TABLE,
                                })
                                .associatedFlows(new String[]{Template.Flow.NAME})
                                .build()
                })
                // Workflows:
                .flows(new MigratorFlow.MigrateOptFlow[]{
                        MigratorFlow.MigrateOptFlow.builder()
                                .flowName(Template.Flow.NAME)
                                .flowDescription(Template.Flow.NAME)
                                .flowSteps(new MigratorFlow.MigrateOptFlowStep[]{
                                        // Introduction:
                                        MigratorFlow.MigrateOptFlowStep.builder()
                                                .stepName(Template.Flow.Step.INTRO)
                                                .stepType(MigratorFlow.StepType.Introduction)
                                                .flowRulesExit(new String[] {
                                                        String.format("ROUTE TO '%s'", Template.Flow.Step.ASSIGN)
                                                })
                                                .build(),
                                        // Assign:
                                        MigratorFlow.MigrateOptFlowStep.builder()
                                                .stepName(Template.Flow.Step.ASSIGN)
                                                .stepType(MigratorFlow.StepType.Assignment)
                                                .stepDescription("Wowzers, this is nice!")
                                                .flowRulesEntry(new String[] {
                                                        String.format("SET FORM.%s TO '%s'", Template.Field.TEXT_PLAIN, UUID.randomUUID()),
                                                        String.format("SET FORM.%s TO '%s'", Template.Field.TEXT_ENCRYPTED, UUID.randomUUID())
                                                })
                                                .flowRulesView(new String[] {
                                                        String.format("VIEW '%s'", Template.Flow.Step.ASSIGN),
                                                        "VIEW 'This is another view'"
                                                })
                                                .flowRulesExit(new String[] {
                                                        String.format("SET FORM.%s TO '%s' IF(FORM.%s EQUAL '1234')",
                                                                Template.Field.TEXT_PLAIN,
                                                                UUID.randomUUID(),
                                                                Template.Field.TEXT_PLAIN
                                                        ),
                                                        String.format("ROUTE TO '%s' IF(FORM.%s EQUAL 'cool')",
                                                                "Print The Value",
                                                                Template.Field.TEXT_PLAIN
                                                        ),
                                                        String.format("ROUTE TO '%s'", Template.Flow.Step.EXIT)
                                                })
                                                .build(),
                                        // Program:
                                        MigratorFlow.MigrateOptFlowStep.builder()
                                                .stepName("Print The Value")
                                                .stepType(MigratorFlow.StepType.JavaProgram)
                                                .stepDescription("Print the values from the form.")
                                                .properties(new FlowStep.StepProperty[]{
                                                        new FlowStep.StepProperty(FlowStep.StepProperty.PropName.TaskIdentifier, "Print Form Values")
                                                })
                                                .flowRulesExit(new String[] {"ROUTE TO 'Exit'"})
                                                .build()
                                })
                                .build()
                })
                // User Queries:
                .userQueries(new MigratorUserQuery.MigrateOptUserQuery[]{
                        MigratorUserQuery.MigrateOptUserQuery.builder()
                                .userQueryName(Template.UserQuery.FETCH_FORM_DEF)
                                .userQueryDescription("Testing migration.")
                                .userQueryRules(new String[]{
                                        String.format("[Form Type] = '%s'", Template.Form.FORM_DEF)
                                })
                                .userQueryResultFields(new String[]{
                                        Template.Field.TEXT_PLAIN,
                                        Template.Field.TEXT_ENCRYPTED,
                                        Template.Field.TEXT_PARAGRAPH
                                })
                                .build()
                })
                // Libraries:
                .thirdPartyLibs(new MigratorThirdPartyLib.MigrateOptThirdPartLib[]{
                        MigratorThirdPartyLib.MigrateOptThirdPartLib.builder()
                                .libContentClasspath("META-INF/third-party-library/printformfieldsflowprogram-1.0-SNAPSHOT-jar-with-dependencies.jar")
                                .filename("printformfieldsflowprogram-1.0-SNAPSHOT-jar-with-dependencies.jar")
                                .description("Third party library for testing.")
                                .build()
                })
                .userFields(
                        new MigratorField.MigrateOptField[]{
                                MigratorField.MigrateOptFieldMultiChoiceSelectMany.builder()
                                        .baseInfo(MigratorField.MigrateOptFieldMCBase.builder()
                                                .fieldName(Template.FieldUser.NATIONALITIES)
                                                .fieldDescription("Fintech associated with a user.")
                                                .choicesArr(new String[]{
                                                        "South African",
                                                        "Italian",
                                                        "Dutch",
                                                        "Indian"
                                                })
                                                .build())
                                        .build()
                        }
                )
                // Roles:
                .roles(new MigratorRoleAndPermissions.MigrateOptRole[]{
                        MigratorRoleAndPermissions.MigrateOptRole.builder()
                                .roleName("Role Numbre Uno")
                                .roleDescription("The first Role to have been created.")
                                .adminPermissions(new AdminPermission[] {
                                        AdminPermission.login,
                                        AdminPermission.change_own_password,
                                        AdminPermission.view_flow_monitor
                                })
                                .views(new MigratorRoleAndPermissions.MigrateOptRoleView[]{
                                        MigratorRoleAndPermissions.MigrateOptRoleView.builder()
                                                .flow(Template.Flow.NAME)
                                                .step(Template.Flow.Step.ASSIGN)
                                                .viewNames(new String[] {
                                                        Template.Flow.Step.ASSIGN
                                                })
                                                .build()
                                })
                                .userQueries(new String[]{
                                        Template.UserQuery.FETCH_FORM_DEF
                                })
                                .roleToFormDef(
                                        new MigratorRoleAndPermissions.MigrateOptRoleFormDef(
                                                new String[]{Template.Form.FORM_DEF},//Can Create
                                                new String[]{Template.Form.FORM_DEF},// Attachment Create+Update
                                                new String[]{Template.Form.FORM_DEF},// Attachment View
                                                new MigratorRoleAndPermissions.MigrateOptRoleFormField[] {
                                                        MigratorRoleAndPermissions.MigrateOptRoleFormField.builder()
                                                                .formDef(Template.Form.FORM_DEF)
                                                                .fieldsModifyAndView(new String[]{
                                                                        Template.Field.TEXT_PLAIN,
                                                                        Template.Field.TEXT_ENCRYPTED
                                                                })
                                                                .fieldsView(new String[]{
                                                                        Template.Field.TEXT_PARAGRAPH,
                                                                        Template.Field.TEXT_LAT_LONG,
                                                                })
                                                                .build(),
                                                        MigratorRoleAndPermissions.MigrateOptRoleFormField.builder()
                                                                .formDef(Template.Form.FORM_DEF_TABLE)
                                                                .fieldsView(new String[]{
                                                                        Template.Field.TEXT_PLAIN
                                                                })
                                                                .build(),
                                                }
                                        )
                                )
                                .allowUpdate(false)
                                .build()
                })
                .build();

        MigratorPlan.migrate(BASE_URL, ADMIN_SERVICE_TICKET, plan);

        plan.getRoles()[0].setAllowUpdate(true);
        plan.getRoles()[0].setAdminPermissions(new AdminPermission[] {
                AdminPermission.edit_sys_forms,
                AdminPermission.print_forms,
                AdminPermission.execute_sql_util,
                AdminPermission.logout
        });

        MigratorField.MigrateOptFieldMultiChoiceSelectMany manyNat =
                (MigratorField.MigrateOptFieldMultiChoiceSelectMany)plan.getUserFields()[0];
        manyNat.getBaseInfo().setChoicesArr(new String[]{
                "Japanese"
        });

        MigratorPlan.migrate(BASE_URL, ADMIN_SERVICE_TICKET, plan);
    }


    /**
     * Teardown.
     */
    @After
    @Override
    public void destroy() {
        if (isConnectionInValid) return;

        MigratorPlan.MigrateOptRemovePlan remPlan = MigratorPlan.MigrateOptRemovePlan.builder()
                .fields(new MigratorField.MigrateOptRemoveField[] {
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_PLAIN).build(),
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_ENCRYPTED).build(),
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_PARAGRAPH).build(),
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_LAT_LONG).build(),
                })
                .userFields(new MigratorField.MigrateOptRemoveField[] {
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.FieldUser.NATIONALITIES).build()
                })
                .tableFields(new MigratorField.MigrateOptRemoveField[] {
                        MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TABLE).build(),
                })
                .formDefs(new MigratorForm.MigrateOptRemoveForm[]{
                        MigratorForm.MigrateOptRemoveForm.builder().formName(Template.Form.FORM_DEF).build(),
                        MigratorForm.MigrateOptRemoveForm.builder().formName(Template.Form.FORM_DEF_TABLE).build()
                })
                .flows(new MigratorFlow.MigrateOptRemoveFlow[]{
                        MigratorFlow.MigrateOptRemoveFlow.builder()
                                .flowName(Template.Flow.NAME)
                                .build()
                })
                .thirdPartyLibs(new MigratorThirdPartyLib.MigrateOptRemoveThirdPartLib[]{
                        MigratorThirdPartyLib.MigrateOptRemoveThirdPartLib.builder()
                                .thirdPartyFilename("printformfieldsflowprogram-1.0-SNAPSHOT-jar-with-dependencies.jar")
                                .build()
                })
                .userQueries(new MigratorUserQuery.MigrateOptRemoveUserQuery[]{
                        MigratorUserQuery.MigrateOptRemoveUserQuery.builder()
                                .userQueryName(String.format("Fetch %s", Template.Form.FORM_DEF))
                                .build()
                })
                .roles(new MigratorRoleAndPermissions.MigrateOptRemoveRole[]{
                        MigratorRoleAndPermissions.MigrateOptRemoveRole.builder()
                                .roleName(Template.Role.ONE)
                                .build()
                })
                .build();

        MigratorPlan.remove(BASE_URL, ADMIN_SERVICE_TICKET, remPlan);
    }
}
