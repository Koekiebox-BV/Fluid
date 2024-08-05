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
	}

	@Test
	public void testMigratePlan() {
		if (this.isConnectionInValid) return;

		MigratorPlan.MigrateOptPlan plan = MigratorPlan.MigrateOptPlan.builder()
				//Fields:
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
														String.format("ROUTE TO '%s'", Template.Flow.Step.EXIT)
												})
												.build()
								})
								.build()
				})
				.build();

		MigratorPlan.migrate(BASE_URL, ADMIN_SERVICE_TICKET, plan);
	}


	/**
	 * Teardown.
	 */
	@After
	@Override
	public void destroy() {
		if (this.isConnectionInValid) return;

		MigratorPlan.MigrateOptRemovePlan remPlan = MigratorPlan.MigrateOptRemovePlan.builder()
				.fields(new MigratorField.MigrateOptRemoveField[] {
						MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_PLAIN).build(),
						MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_ENCRYPTED).build(),
						MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_PARAGRAPH).build(),
						MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TEXT_LAT_LONG).build(),
				})
				.tableFields(new MigratorField.MigrateOptRemoveField[] {
						MigratorField.MigrateOptRemoveField.builder().fieldName(Template.Field.TABLE).build(),
				})
				.formDefs(new MigratorForm.MigrateOptRemoveForm[]{
						MigratorForm.MigrateOptRemoveForm.builder().formName(Template.Form.FORM_DEF).build(),
						MigratorForm.MigrateOptRemoveForm.builder().formName(Template.Form.FORM_DEF_TABLE).build()
				})
				.build();

		MigratorPlan.remove(BASE_URL, ADMIN_SERVICE_TICKET, remPlan);
	}
}
