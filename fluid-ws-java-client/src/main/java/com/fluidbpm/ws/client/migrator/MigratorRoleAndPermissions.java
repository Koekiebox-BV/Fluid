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
import com.fluidbpm.program.api.vo.role.Role;
import com.fluidbpm.program.api.vo.role.RoleToFormDefinition;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.role.RoleClient;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Migration class for role and permission related migrations.
 * @see Role
 * @see RoleClient
 */
public class MigratorRoleAndPermissions {
    @Builder
    public static final class MigrateOptRole {
        private String roleName;
        private String roleDescription;
        private String[] adminPermissions;
        private String[] userQueries;
        private MigrateOptRoleView[] views;
        private MigrateOptRoleFormDef roleToFormDef;
    }

    @Builder
    public static final class MigrateOptRoleFormDef {
        private String[] canCreate;
        private String[] attachmentsCreateUpdate;
        private String[] attachmentsView;
        private MigrateOptRoleFormField[] formFields;

        public boolean[] getOptionsForFormDef(String formDef) {
            return new boolean[] {
                    this.listContainFormDef(this.canCreate, formDef),
                    this.listContainFormDef(this.attachmentsCreateUpdate, formDef),
                    this.listContainFormDef(this.attachmentsView, formDef),
            };
        }

        public String[] allFormDefsUnique() {
            List<String> returnVal = new ArrayList<>();
            if (this.canCreate != null) {
                for (String verify: this.canCreate) {
                    if (this.isNotInList(returnVal, verify)) returnVal.add(verify);
                }
            }
            if (this.attachmentsCreateUpdate != null) {
                for (String verify: this.attachmentsCreateUpdate) {
                    if (this.isNotInList(returnVal, verify)) returnVal.add(verify);
                }
            }
            if (this.attachmentsView != null) {
                for (String verify: this.attachmentsView) {
                    if (this.isNotInList(returnVal, verify)) returnVal.add(verify);
                }
            }
            if (returnVal.isEmpty()) return null;
            return returnVal.toArray(new String[]{});
        }

        private boolean listContainFormDef(String[] list, String formDef) {
            if (list == null || list.length == 0) return false;
            if (UtilGlobal.isBlank(formDef)) return false;
            for (String itm : list) if (formDef.equalsIgnoreCase(itm)) return true;
            return false;
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
    public static final class MigrateOptRoleFormField {
        private String formDef;
        private String[] fieldsModifyAndView;
        private String[] fieldsView;
    }

    @Builder
    public static final class MigrateOptRoleView {
        private String flow;
        private String step;
        private String viewRule;
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
        try {
            // Admin Permissions:
            List<String> adminPermissions = new ArrayList<>();
            List<RoleToFormDefinition> roleToFormDef = new ArrayList<>();
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

            Role toCreate = new Role(opts.roleName);
            toCreate.setAdminPermissions(adminPermissions);
            toCreate.setRoleToFormDefinitions(roleToFormDef);
            toCreate.setDescription(opts.roleDescription);
            rc.createRole(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
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
