/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox B.V.
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

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import lombok.Builder;

import java.util.Arrays;

/**
 * Migration class for form migrations.
 *
 * @see Form
 */
public class MigratorPlan {
    @Builder
    public static final class MigrateOptRemovePlan {
        private MigratorField.MigrateOptRemoveField[] fields;
        private MigratorField.MigrateOptRemoveField[] tableFields;
        private MigratorForm.MigrateOptRemoveForm[] formDefs;
        private MigratorFlow.MigrateOptRemoveFlow[] flows;
    }

    @Builder
    public static final class MigrateOptPlan {
        private MigratorField.MigrateOptField[] formFieldsNonTable;
        private MigratorField.MigrateOptFieldTable[] formFieldsTable;
        private MigratorForm.MigrateOptForm[] formDefs;
        private MigratorThirdPartyLib.OptMigrateThirdPartLib[] thirdPartyLibs;
        private MigratorFlow.MigrateOptFlow[] flows;
    }

    /**
     * Perform an environment migration based on the {@code migratePlan}.
     * 
     * @param url Server URL to apply migration to.
     * @param serviceTicket The configuration session service ticket.
     * @param migratePlan The plan to migrate.
     */
    public static void migrate(
            String url,
            String serviceTicket,
            MigrateOptPlan migratePlan
    ) {
        try (
                ConfigurationClient cc = new ConfigurationClient(url, serviceTicket);
                FormFieldClient ffc = new FormFieldClient(url, serviceTicket);
                FormDefinitionClient fdc = new FormDefinitionClient(url, serviceTicket);
                FlowClient fc = new FlowClient(url, serviceTicket);
                FlowStepClient fsc = new FlowStepClient(url, serviceTicket);
                FlowStepRuleClient fsrc = new FlowStepRuleClient(url, serviceTicket)
        ) {
            // 1. Migrate non-table fields:
            if (migratePlan.formFieldsNonTable != null) Arrays.stream(migratePlan.formFieldsNonTable).forEach(itm -> {
                if (itm instanceof MigratorField.MigrateOptFieldText) {
                    MigratorField.migrateFieldText(ffc, (MigratorField.MigrateOptFieldText)itm);
                } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoicePlain) {
                    MigratorField.migrateFieldMultiChoicePlainOptionExists(
                            ffc, (MigratorField.MigrateOptFieldMultiChoicePlain)itm
                    );
                } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoiceSelectMany) {
                    MigratorField.migrateFieldMultiChoiceSelectManyOptionExists(
                            ffc, (MigratorField.MigrateOptFieldMultiChoiceSelectMany)itm
                    );
                } else if (itm instanceof MigratorField.MigrateOptFieldDecimal) {
                    MigratorField.migrateFieldDecimal(
                            ffc, (MigratorField.MigrateOptFieldDecimal)itm
                    );
                } else if (itm instanceof MigratorField.MigrateOptFieldDate) {
                    MigratorField.migrateFieldDateTime(
                            ffc, (MigratorField.MigrateOptFieldDate)itm
                    );
                } else if (itm instanceof MigratorField.MigrateOptFieldTrueFalse) {
                    MigratorField.migrateFieldTrueFalse(
                            ffc, (MigratorField.MigrateOptFieldTrueFalse)itm
                    );
                } else if (itm instanceof MigratorField.MigrateOptFieldLabel) {
                    MigratorField.migrateFieldLabel(
                            ffc, (MigratorField.MigrateOptFieldLabel)itm
                    );
                }
            });

            // 2. Migrate table fields:
            if (migratePlan.formFieldsTable != null) Arrays.stream(migratePlan.formFieldsTable).forEach(itm -> {
                MigratorField.migrateFieldTableField(fdc, ffc, itm);
            });

            // 3. Migrate form definitions:
            if (migratePlan.formDefs != null) Arrays.stream(migratePlan.formDefs).forEach(itm -> {
                MigratorForm.migrateFormDefinition(fdc, itm);
            });

            // 4. Migrate libs:
            if (migratePlan.thirdPartyLibs != null) Arrays.stream(migratePlan.thirdPartyLibs).forEach(itm -> {
                MigratorThirdPartyLib.migrateThirdPartyLib(cc, itm);
            });

            // 5. Migrate workflows:
            if (migratePlan.flows != null) Arrays.stream(migratePlan.flows).forEach(itm -> {
                MigratorFlow.migrateFlow(fc, fsc, fsrc, itm);
            });
        }
    }

    /**
     * Perform an environment migration based on the {@code migratePlan}.
     *
     * @param url Server URL to apply migration to.
     * @param serviceTicket The configuration session service ticket.
     * @param removePlan The plan to migrate.
     */
    public static void remove(
            String url,
            String serviceTicket,
            MigrateOptRemovePlan removePlan
    ) {
        try (
                ConfigurationClient cc = new ConfigurationClient(url, serviceTicket);
                FormFieldClient ffc = new FormFieldClient(url, serviceTicket);
                FormDefinitionClient fdc = new FormDefinitionClient(url, serviceTicket);
                FlowClient fc = new FlowClient(url, serviceTicket)
        ) {
            if (removePlan.formDefs != null) Arrays.stream(removePlan.formDefs).forEach(itm -> {
                MigratorForm.removeFormDefinition(fdc, itm);
            });
            if (removePlan.tableFields != null) Arrays.stream(removePlan.tableFields).forEach(itm -> {
                MigratorField.removeField(ffc, itm);
            });
            if (removePlan.fields != null) Arrays.stream(removePlan.fields).forEach(itm -> {
                MigratorField.removeField(ffc, itm);
            });
            if (removePlan.flows != null) Arrays.stream(removePlan.flows).forEach(itm -> {
                MigratorFlow.removeFlow(fc, itm);
            });
        }
    }
}
