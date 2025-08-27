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
 * forbidden unless prior written permission is obtained from Koekiebox Innovations.
 */

package com.fluidbpm.program.api.vo.role;

import com.fluidbpm.program.api.util.UtilGlobal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin permissions enum.
 *
 * @author jasonbruwer on 2024-08-25
 * @since v1.13
 */
@Getter
@RequiredArgsConstructor
public enum AdminPermission {
    login("Login", "Log into Fluid."),
    change_own_password("Change Own Password", "Change Own Password."),
    //Flow Jobs...
    edit_flow_jobs("Create and Update Flows", "Create and Modify Flows."),
    view_flow_jobs("View Flows", "View Flows."),
    //Forms...
    edit_forms("Create and Update Forms", "Create and Modify Forms."),
    view_forms("View Forms", "View Forms."),
    //Fields...
    edit_fields("Create and Update Fields", "Create and Modify Fields."),
    view_fields("View Flow Fields", "View Fields."),
    //User...
    edit_users("Create and Update Users", "Create and Modify Users."),
    view_users("View Users", "View Users."),
    //User Queries...
    edit_user_queries("Create and Update User Queries", "Create and Modify User Queries."),
    view_user_queries("View User Queries", "View User Queries."),
    //Role...
    edit_roles("Create and Update Roles", "Create and Modify Roles."),
    view_roles("View Roles", "View Roles."),
    //Configuration...
    configuration("Access to Configuration", "Access to Configuration."),
    view_configurations("View Configurations", "View Configurations."),
    edit_config_general("Update General Configurations", "Modify General Configurations."),
    edit_config_display("Update Display Configurations", "Modify Display Configurations."),
    edit_config_content_storage("Update Content Storage Configurations", "Modify Content Storage Configurations."),
    edit_config_3rd_party_class_library("Update 3rd Party Libraries", "Modify 3rd Party Library Configurations."),
    edit_config_printing("Update Print Properties", "Modify Printing Configurations."),
    edit_config_cache_container("Update Cache Container Configurations", "Modify Cache Container Configurations."),
    edit_config_login("Update Login Properties", "Modify Login Configurations."),
    edit_smtp("Update Mail Transfer Configurations", "Modify Mail Transfer Configurations."),
    edit_config_data_source("Update Data Source Configurations", "Modify Data-Source Configurations."),
    edit_config_license("Update License Information", "Modify License Configurations."),
    edit_config_external_application("Update External Application", "Modify External Application Configurations."),
    //View Audit Log...
    all_outgoing_mails_and_notifications("Access to all Outgoing mails and Notifications", "Access to view outgoing mails and create, view and edit System notifications."),
    view_flow_monitor("View Flow Monitor Items", "View the Flow Items via Flow Monitor."),
    move_items_flow_monitor("Move Items using Flow Monitor", "Move Items between Step using Flow Monitor."),
    remove_items_flow_monitor("Remove Items using Flow Monitor", "Remove Items from Flows using Flow Monitor."),
    release_from_error_items_flow_monitor("Release Items from Error using Flow Monitor", "Remove Items from Error using Flow Monitor."),
    release_from_user_items_flow_monitor("Release Items from User using Flow Monitor", "Remove Items from User using Flow Monitor."),
    view_audit_log("View Audit Logs", "View Administration Audit Logs."),
    view_server_log("View Server Log", "View System Server Log."),
    view_global_fields("View Global Field", "View Global Fields."),
    edit_global_fields("Edit Global Field", "Edit Global Fields."),
    //Edit of Special Forms...
    //Email...
    edit_sys_forms("Edit System Forms", "Update System Forms."),
    view_sys_forms("View System Forms", "View System Forms."),
    //Print...
    print_forms("Print Electronic Forms", "Print Electronic Forms."),
    //Schedules...
    edit_schedules("Create and Update Schedules", "Create and Modify Schedules."),
    view_schedules("View Schedules", "View Schedules."),
    //Delete Form Container...
    delete_electronic_form("Delete Electronic Form", "Delete Electronic Form or Table Field."),
    //View own Profile...
    view_own_user_profile("View Own User Profile", "View Own User Profile."),
    //Reveal Encrypted Masked Field.
    reveal_masked_field("Reveal Masked Field", "Reveal a Masked Encrypted Field."),
    //Execute SQL Util
    execute_sql_util("SQL Util", "Execute SQL Util Queries."),
    //Logout...
    logout("Logout", "Log out of Fluid."),
    //Import and Export Template...
    import_export_fluid_template("Export and Import Fluid Template", "Export and Import Fluid Data Structure Templates."),
    //Polling Services...
    expose_polling_services("Expose Polling Services", "Expose the Polling Services for integration."),
    //Lock as User...
    lock_on_behalf("Lock Form on Behalf Of", "Lock a Form on behalf of someone else."),
    //Change another users password...
    change_others_password("Change another Users Password", "Change another users Password."),
    //Modify Blockchain Wallets...
    change_blockchain_wallets("Change Blockchain Wallets", "Change Blockchain Wallet properties."),
    apply_modules("Apply Modules", "Apply Out-Of_Box Modules."),
    administer_payments("Administer Payments", "Have visibility and administrative rights for Payment functions."),
    view_basic_configurations("View Basic Configurations", "View the basic Configurations for theming purposes."),
    ;
    private final String alias;
    private final String description;

    /**
     * @return name of the enum.
     */
    public String getValue() {
        return this.toString();
    }

    /**
     * @param adminPermissionName Enum from {@code adminPermissionName}
     * @return AdminPermission with name {@code adminPermissionName}.
     */
    public static AdminPermission valueOfSafe(String adminPermissionName) {
        if (UtilGlobal.isBlank(adminPermissionName)) return null;

        for (AdminPermission adminPermission : values()) {
            if (adminPermission.name().equals(adminPermissionName)) return adminPermission;
        }
        return null;
    }

    /**
     * Fetch the enum from the {@code adminPermissionAlias}
     *
     * @param adminPermissionAlias Alias to fetch with.
     * @return AdminPermission with name {@code adminPermissionAlias}.
     */
    public static AdminPermission getAdminPermissionByAlias(String adminPermissionAlias) {
        if (UtilGlobal.isBlank(adminPermissionAlias)) return null;
        String paramLower = adminPermissionAlias.toLowerCase();
        for (AdminPermission adminPermission : values()) {
            if (adminPermission.getAlias().toLowerCase().equals(paramLower)) {
                return adminPermission;
            }
        }
        return null;
    }
}
