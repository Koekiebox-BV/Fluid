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
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.role.*;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.role.RoleClient;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Migration class for role and permission related migrations.
 * @see Role
 * @see RoleClient
 */
public class MigratorRoleAndPermissions {
    @Builder
    @Data
    public static final class MigrateOptRole {
        private String roleName;
        private String roleDescription;
        private String[] adminPermissions;
        private String[] userQueries;
        private MigrateOptRoleView[] views;
        private MigrateOptRoleFormDef roleToFormDef;
        private boolean allowUpdate;
    }

    @Builder
    @Data
    public static final class MigrateOptRoleFormDef {
        private String[] canCreate;
        private String[] attachmentsCreateUpdate;
        private String[] attachmentsView;
        private MigrateOptRoleFormField[] formFields;

        private boolean[] getOptionsForFormDef(String formDef) {
            return new boolean[] {
                    this.listContainFormDef(this.canCreate, formDef),
                    this.listContainFormDef(this.attachmentsCreateUpdate, formDef),
                    this.listContainFormDef(this.attachmentsView, formDef),
            };
        }

        private boolean listContainFormDef(String[] list, String formDef) {
            if (list == null || list.length == 0) return false;
            if (UtilGlobal.isBlank(formDef)) return false;
            for (String itm : list) if (formDef.equalsIgnoreCase(itm)) return true;
            return false;
        }

        private String[] allFormDefsUnique() {
            List<String> returnVal = new ArrayList<>();
            this.addIfNotInList(returnVal, this.canCreate);
            this.addIfNotInList(returnVal, this.attachmentsCreateUpdate);
            this.addIfNotInList(returnVal, this.attachmentsView);

            if (returnVal.isEmpty()) return null;
            return returnVal.toArray(new String[]{});
        }

        private void addIfNotInList(List<String> returnVal, String[] arr) {
            if (arr == null) return;
            for (String verify: arr) {
                if (UtilGlobal.isBlank(verify)) continue;
                if (this.isNotInList(returnVal, verify)) returnVal.add(verify);
            }
        }

        private boolean isNotInList(List<String> returnVal, String verify) {
            String existing = returnVal.stream()
                    .filter(itm -> itm.equalsIgnoreCase(verify))
                    .findFirst()
                    .orElse(null);
            return existing == null;
        }
    }

    @Builder
    @Data
    public static final class MigrateOptRoleFormField {
        private String formDef;
        private String[] fieldsModifyAndView;
        private String[] fieldsView;

        public String[] allFieldsUnique() {
            List<String> returnVal = new ArrayList<>();
            this.addIfNotInList(returnVal, this.fieldsModifyAndView);
            this.addIfNotInList(returnVal, this.fieldsView);

            if (returnVal.isEmpty()) return null;
            return returnVal.toArray(new String[]{});
        }

        private void addIfNotInList(List<String> returnVal, String[] arr) {
            if (arr == null) return;
            for (String verify: arr) {
                if (UtilGlobal.isBlank(verify)) continue;
                if (this.isNotInList(returnVal, verify)) returnVal.add(verify);
            }
        }

        private boolean isNotInList(List<String> returnVal, String verify) {
            String existing = returnVal.stream()
                    .filter(itm -> itm.equalsIgnoreCase(verify))
                    .findFirst()
                    .orElse(null);
            return existing == null;
        }

        public boolean[] getOptionsForField(String field) {
            return new boolean[] {
                    this.listContainField(this.fieldsModifyAndView, field),
                    this.listContainField(this.fieldsView, field),
            };
        }

        private boolean listContainField(String[] list, String field) {
            if (list == null || list.length == 0) return false;
            if (UtilGlobal.isBlank(field)) return false;
            for (String itm : list) if (field.equalsIgnoreCase(itm)) return true;
            return false;
        }
    }

    @Builder
    @Data
    public static final class MigrateOptRoleView {
        private String flow;
        private String step;
        private String[] viewNames;
    }

    @Builder
    public static final class MigrateOptRemoveRole {
        private Long roleId;
        private String roleName;
    }

    /**Migrate a role.
     * @param rc {@code RoleClient}
     * @param opts {@code MigrateOptRole}
     */
    public static void migrateRole(RoleClient rc, MigrateOptRole opts) {
        List<String> adminPermissions = new ArrayList<>();
        List<RoleToFormDefinition> roleToFormDef = new ArrayList<>();
        List<RoleToUserQuery> userQueries = new ArrayList<>();
        List<RoleToFormFieldToFormDefinition> formFields = new ArrayList<>();
        List<RoleToJobView> jobViews = new ArrayList<>();
        try {
            // Admin Permissions:
            if (opts.adminPermissions != null) {
                for (String permission : opts.adminPermissions) {
                    if (UtilGlobal.isBlank(permission)) continue;
                    adminPermissions.add(permission);
                }
            }

            // Form Definitions:
            String[] allFormDefs;
            if (opts.roleToFormDef != null &&
                    ((allFormDefs = opts.roleToFormDef.allFormDefsUnique()) != null)) {
                for (String formDef : allFormDefs) {
                    boolean[] createAttCUAttView = opts.roleToFormDef.getOptionsForFormDef(formDef);

                    RoleToFormDefinition toAdd = new RoleToFormDefinition();
                    toAdd.setCanCreate(createAttCUAttView[0]);
                    toAdd.setAttachmentsCreateUpdate(createAttCUAttView[1]);
                    toAdd.setAttachmentsView(createAttCUAttView[2]);
                    toAdd.setFormDefinition(new Form(formDef));
                    roleToFormDef.add(toAdd);
                }
            }

            // User Queries:
            if (opts.userQueries != null) {
                for (String uq : opts.userQueries) {
                    RoleToUserQuery toAdd = new RoleToUserQuery();
                    toAdd.setUserQuery(new UserQuery(uq));
                    userQueries.add(toAdd);
                }
            }

            // Form Fields:
            if (opts.roleToFormDef != null && opts.roleToFormDef.formFields != null) {
                for (MigrateOptRoleFormField formField : opts.roleToFormDef.formFields) {
                    String[] combinedFields = formField.allFieldsUnique();
                    if (combinedFields == null) continue;

                    for (String field: combinedFields) {
                        boolean[] perm = formField.getOptionsForField(field);

                        RoleToFormFieldToFormDefinition toAdd = new RoleToFormFieldToFormDefinition();
                        toAdd.setFormFieldToFormDefinition(new FormFieldToFormDefinition());
                        toAdd.getFormFieldToFormDefinition().setFormDefinition(new Form(formField.formDef));
                        toAdd.getFormFieldToFormDefinition().setFormField(new Field(field));
                        toAdd.setCanCreateAndModify(perm[0]);
                        toAdd.setCanView(perm[1]);
                        formFields.add(toAdd);
                    }
                }
            }

            // Job View:
            if (opts.views != null) {
                for (MigrateOptRoleView view : opts.views) {
                    if (UtilGlobal.isNotBlank(
                            view.flow,
                            view.step
                    ) && view.viewNames != null) {
                        for (String viewName : view.viewNames) {
                            if (UtilGlobal.isBlank(viewName)) continue;

                            RoleToJobView toAdd = new RoleToJobView();
                            toAdd.setJobView(new JobView(viewName));
                            toAdd.getJobView().setViewStepName(view.step);
                            toAdd.getJobView().setViewFlowName(view.flow);
                            jobViews.add(toAdd);
                        }
                    }
                }
            }

            Role toCreate = new Role(opts.roleName);
            toCreate.setDescription(opts.roleDescription);
            toCreate.setAdminPermissions(adminPermissions);
            toCreate.setRoleToFormDefinitions(roleToFormDef);
            toCreate.setRoleToFormFieldToFormDefinitions(formFields);
            toCreate.setRoleToUserQueries(userQueries);
            toCreate.setRoleToJobViews(jobViews);

            rc.createRole(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            if (!opts.allowUpdate) return;

            Role existing = rc.getRoleByName(opts.roleName);

            existing.setDescription(opts.roleDescription);
            existing.setAdminPermissions(adminPermissions);
            existing.setAdminPermissions(adminPermissions);
            existing.setRoleToFormDefinitions(roleToFormDef);
            existing.setRoleToFormFieldToFormDefinitions(formFields);
            existing.setRoleToUserQueries(userQueries);
            existing.setRoleToJobViews(jobViews);

            rc.updateRole(existing);
        }
    }

    /**Remove a role.
     * @param rc {@code RoleClient}
     * @param opts {@code MigrateOptRemoveThirdPartLib}
     */
    public static void removeRole(RoleClient rc, MigrateOptRemoveRole opts) {
        Role toDelete = new Role(opts.roleId);
        toDelete.setName(opts.roleName);
        rc.deleteRole(toDelete, true);
    }
}
