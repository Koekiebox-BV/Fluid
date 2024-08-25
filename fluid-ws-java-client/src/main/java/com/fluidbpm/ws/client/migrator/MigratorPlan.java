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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.v1.config.ConfigurationClient;
import com.fluidbpm.ws.client.v1.config.GlobalFieldClient;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.fluidbpm.ws.client.v1.flow.RouteFieldClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.role.RoleClient;
import com.fluidbpm.ws.client.v1.user.UserFieldClient;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        private MigratorThirdPartyLib.MigrateOptRemoveThirdPartLib[] thirdPartyLibs;
        private MigratorUserQuery.MigrateOptRemoveUserQuery[] userQueries;
        private MigratorRoleAndPermissions.MigrateOptRemoveRole[] roles;
    }

    @Builder
    @Data
    public static final class MigrateOptPlan {
        private MigratorField.MigrateOptField[] formFieldsNonTable;
        private MigratorField.MigrateOptFieldTable[] formFieldsTable;
        private MigratorForm.MigrateOptForm[] formDefs;
        private MigratorField.MigrateOptField[] userFields;
        private MigratorField.MigrateOptField[] routeFields;
        private MigratorField.MigrateOptField[] globalFields;
        private MigratorThirdPartyLib.MigrateOptThirdPartLib[] thirdPartyLibs;
        private MigratorFlow.MigrateOptFlow[] flows;
        private MigratorUserQuery.MigrateOptUserQuery[] userQueries;
        private MigratorRoleAndPermissions.MigrateOptRole[] roles;
    }

    /**
     * Perform an environment migration based on the {@code migratePlan}.
     * 
     * @param url Server URL to apply migration to.
     * @param serviceTicket The configuration session service ticket.
     * @param migratePlan The plan to migrate.
     */
    public static void migrate(String url, String serviceTicket, MigrateOptPlan migratePlan) {
        try (
                ConfigurationClient cc = new ConfigurationClient(url, serviceTicket);
                FormFieldClient ffc = new FormFieldClient(url, serviceTicket);
                UserFieldClient ufc = new UserFieldClient(url, serviceTicket);
                RouteFieldClient rfc = new RouteFieldClient(url, serviceTicket);
                GlobalFieldClient gfc = new GlobalFieldClient(url, serviceTicket);
                FormDefinitionClient fdc = new FormDefinitionClient(url, serviceTicket);
                FlowClient fc = new FlowClient(url, serviceTicket);
                FlowStepClient fsc = new FlowStepClient(url, serviceTicket);
                FlowStepRuleClient fsrc = new FlowStepRuleClient(url, serviceTicket);
                UserQueryClient uqc = new UserQueryClient(url, serviceTicket);
                RoleClient rc = new RoleClient(url, serviceTicket)
        ) {
            // 1. Migrate non-table fields:
            if (migratePlan.formFieldsNonTable != null) Arrays.stream(migratePlan.formFieldsNonTable).forEach(itm -> {
                MigratorField.migrateFormField(ffc, itm);
            });

            // 2. Migrate table fields:
            if (migratePlan.formFieldsTable != null) Arrays.stream(migratePlan.formFieldsTable).forEach(itm -> {
                MigratorField.migrateFieldTableField(fdc, ffc, itm);
            });

            // 3. Migrate form definitions:
            Map<String, String[]> formDefToAssociatedFlows = new HashMap<>();
            if (migratePlan.formDefs != null) Arrays.stream(migratePlan.formDefs).forEach(itm -> {
                if (itm.getAssociatedFlows() != null && UtilGlobal.isNotBlank(itm.getFormType())) {
                    formDefToAssociatedFlows.put(itm.getFormType(), itm.getAssociatedFlows());
                    itm.setAssociatedFlows(null);
                }
                MigratorForm.migrateFormDefinition(fdc, itm);
            });

            // 4. Migrate other fields:
            if (migratePlan.userFields != null) Arrays.stream(migratePlan.userFields).forEach(itm -> {
                MigratorField.migrateUserField(ufc, itm);
            });
            if (migratePlan.routeFields != null) Arrays.stream(migratePlan.routeFields).forEach(itm -> {
                MigratorField.migrateRouteField(rfc, itm);
            });
            if (migratePlan.globalFields != null) Arrays.stream(migratePlan.globalFields).forEach(itm -> {
                MigratorField.migrateGlobalField(gfc, itm);
            });

            // 5. Migrate libs:
            if (migratePlan.thirdPartyLibs != null) Arrays.stream(migratePlan.thirdPartyLibs).forEach(itm -> {
                MigratorThirdPartyLib.migrateThirdPartyLib(cc, itm);
            });

            // 6. Migrate user queries:
            if (migratePlan.userQueries != null) Arrays.stream(migratePlan.userQueries).forEach(itm -> {
                MigratorUserQuery.migrateUserQuery(uqc, itm);
            });

            // 7. Migrate workflows:
            if (migratePlan.flows != null) Arrays.stream(migratePlan.flows).forEach(itm -> {
                MigratorFlow.migrateFlow(fc, fsc, fsrc, itm);
            });

            // 8. Set the Associated Flows after flows are created:
            if (!formDefToAssociatedFlows.isEmpty()) {
                formDefToAssociatedFlows.forEach((formDefName, assFlows) -> {
                    MigratorForm.MigrateOptForm formDefItm = Arrays.stream(migratePlan.formDefs)
                            .filter(itm -> itm.getFormType().equals(formDefName))
                            .findFirst()
                            .orElse(null);
                    if (formDefItm != null) {
                        formDefItm.setAssociatedFlows(assFlows);
                        MigratorForm.migrateFormDefinition(fdc, formDefItm);
                    }
                });
            }

            // 9. Migrate roles and permissions:
            if (migratePlan.roles != null) Arrays.stream(migratePlan.roles).forEach(itm -> {
                MigratorRoleAndPermissions.migrateRole(rc, itm);
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
                FlowClient fc = new FlowClient(url, serviceTicket);
                UserQueryClient uqc = new UserQueryClient(url, serviceTicket);
                RoleClient rc = new RoleClient(url, serviceTicket)
        ) {
            if (removePlan.roles != null) Arrays.stream(removePlan.roles).forEach(itm -> {
                MigratorRoleAndPermissions.removeRole(rc, itm);
            });
            if (removePlan.formDefs != null) Arrays.stream(removePlan.formDefs).forEach(itm -> {
                MigratorForm.removeFormDefinition(fdc, itm);
            });
            if (removePlan.tableFields != null) Arrays.stream(removePlan.tableFields).forEach(itm -> {
                MigratorField.removeField(ffc, itm);
            });
            if (removePlan.userQueries != null) Arrays.stream(removePlan.userQueries).forEach(itm -> {
                MigratorUserQuery.removeUserQuery(uqc, itm);
            });
            if (removePlan.fields != null) Arrays.stream(removePlan.fields).forEach(itm -> {
                MigratorField.removeField(ffc, itm);
            });
            if (removePlan.flows != null) Arrays.stream(removePlan.flows).forEach(itm -> {
                MigratorFlow.removeFlow(fc, itm);
            });
            if (removePlan.thirdPartyLibs != null) Arrays.stream(removePlan.thirdPartyLibs).forEach(itm -> {
                MigratorThirdPartyLib.removeLibrary(cc, itm);
            });
        }
    }
}
