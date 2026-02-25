/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;

/**
 * The GlobalIDSpecial class provides special purpose constants
 * used for identifying error types and payload-related tags
 * across the system. These constants are commonly utilized
 * to standardize error handling and tagging mechanisms in
 * various components of the architecture.
 *
 * Fields:
 * - {@code ERROR_TYPE}: Represents a predefined error type identifier
 *   with a value of {@code -666}, primarily used to indicate
 *   a specific error condition in system operations.
 * - {@code TAG_PAYLOAD_POPULATE}: Represents a special tag that
 *   corresponds to a payload population process with a value
 *   of {@code -100000}, enabling components to manage or flag
 *   payloads distinctly during processing.
 */
public class ASNGlobal {
    /**
     * The Type class defines a collection of constant values used for identifying
     * specific categories or conditions within the system.
     *
     * Constants:
     * - {@code ERROR_TYPE}: Represents a predefined error type identifier with a
     *   value of {@code -666}. This constant is primarily utilized to standardize
     *   error handling behaviors and signal specific error conditions in the system.
     */
    public static final class Type {
        public static final int UNKNOWN = -777;
        public static final int ERROR_TYPE = -666;
        public static final int SKIP_TRANSMISSION_OBJ = -555;

        // Valid return types:
        public static final int FLUID_ITEM = -2;//-1 is reserved for not set.
        public static final int FORM = -3;
        public static final int FIELD = -4;
        public static final int FORM_HISTORIC_DATA_LISTING = -5;
        public static final int FORM_LISTING = -6;
        public static final int FORM_FIELD_LISTING = -7;
        public static final int ATTACHMENT = -8;
        public static final int ATTACHMENT_LISTING = -9;
        public static final int JOB_VIEW = -10;
        public static final int JOB_VIEW_LISTING = -11;
        public static final int TABLE_RECORD = -12;
        public static final int CUSTOM_WEB_ACTION = -13;
        public static final int FLUID_ITEM_LISTING = -14;
        public static final int USER_QUERY = -15;
        public static final int USER_QUERY_LISTING = -16;
        public static final int ROLE = -17;
        public static final int USER = -18;
    }

    /**
     * The Tag class defines a collection of constant values associated with
     * tagging mechanisms in the system. These constants serve as identifiers,
     * enabling components to standardize processing workflows and manage
     * tagged data consistently across the architecture.
     *
     * Constants:
     * - {@code TAG_PAYLOAD_POPULATE}: Represents a special tag used during
     *   the payload population process. It is assigned a value of {@code -100000}
     *   and is leveraged to categorize or flag payload-related operations.
     */
    public static final class Tag {
        public static final int TAG_PAYLOAD_POPULATE = -100000;
    }

    /**
     * This class serves as a namespace for organizing static nested classes that
     * represent various elements and entities in the application.
     */
    public static final class Path {
        public static final String WS_TRANSMISSION = "/web_socket/v1/transmission/";

        public static String transmissionPath(String servTicketHex) {
            return WS_TRANSMISSION.concat(servTicketHex).concat(
                    "?web_socket_encoding_mode=".concat(WebSocketClient.Mode.Binary.name())
            );
        }

        public static final class General {
            public static final String GENERAL_PAYLOAD_POPULATE = "general/payload_populate";
        }

        public static final class Attachment {
            public static final String ATTACHMENT_IMAGES_BY_FORM = "attachments/image_by_form";
            public static final String ATTACHMENTS_BY_FORM = "attachments/by_form";
            public static final String ATTACHMENT_CREATE = "attachments/create";
            public static final String ATTACHMENT_DELETE = "attachments/delete";
        }

        public static final class FlowItem {
            public static final String ITEM_CREATE = "flow_item/create";
            public static final String ITEMS_FOR_VIEW = "flow_item/items_for_view";
            public static final String ITEM_BY_FORM_ID = "flow_item/by_form_id";
            public static final String ITEM_SEND_ON = "flow_item/send_on";
            public static final String ITEM_SEND_TO_FLOW = "flow_item/send_to_flow";
        }

        public static final class FormContainer {
            public static final String FORM_CONT_HISTORIC_DATA = "form_container/historic_data_by_form";
            public static final String FORM_CONT_BY_TITLE = "form_container/by_title";
            public static final String FORM_CONT_ANCESTOR = "form_container/ancestor";
            public static final String FORM_CONT_PRINT = "form_container/print";
            public static final String FORM_CONT_LOCK = "form_container/lock";
            public static final String FORM_CONT_UNLOCK = "form_container/unlock";
            public static final String FORM_CONT_CREATE = "form_container/create";
            public static final String FORM_CONT_UPDATE = "form_container/update";
            public static final String FORM_CONT_DELETE = "form_container/delete";
            public static final String FORM_CONT_EXEC_CUSTOM_ACTION = "form_container/exec_custom_action";
            public static final String FORM_CONT_CREATE_TABLE_RECORD = "form_container/create_table_record";

            public static final String FORM_CONT_GET_TABLE_FORMS = "form_container/get_table_forms";
            public static final String FORM_CONT_EXEC_NATIVE_SQL = "form_container/exec_native_sql";
        }

        public static final class FormDefinition {
            public static final String FORM_DEF_BY_LOGGED_IN_USER_CREATE_INST_OF = "form_definition/by_logged_in_user_create_inst_of";
            public static final String FORM_DEF_BY_LOGGED_IN_USER_VIEW_ATTACHMENTS = "form_definition/by_logged_in_user_view_attachments";
            public static final String FORM_DEF_BY_LOGGED_IN_USER_EDIT_ATTACHMENTS = "form_definition/by_logged_in_user_edit_attachments";
            public static final String FORM_DEF_BY_LOGGED_IN_USER = "form_definition/by_logged_in_user";
            public static final String FORM_DEF_BY_ID = "form_definition/by_id";
        }

        public static final class FormField {
            public static final String FORM_FIELD_BY_FORM_NAMES_AND_LOGGED_IN_USER = "form_field/by_form_names_and_logged_in_user";
            public static final String FORM_FIELD_BY_USER_QUERIES = "form_field/by_user_queries";
            public static final String FORM_FIELD_BY_NAME = "form_field/by_name";
            public static final String FORM_FIELD_BY_ID = "form_field/by_id";
        }

        public static final class Role {
            public static final String ROLE_BY_NAME = "role/by_name";
        }

        public static final class PersonalInventory {
            public static final String PI_BY_USER = "personal_inventory/by_user";
            public static final String PI_REMOVE_FORM = "personal_inventory/remove_form";
            public static final String PI_CLEAR_FORMS = "personal_inventory/clear_forms";
        }

        public static final class UserQuery {
            public static final String UQ_BY_LOGGED_IN_USER = "user_query/by_logged_in_user";
            public static final String UQ_BY_NAME = "user_query/by_name";
            public static final String UQ_EXEC = "user_query/exec_query";
        }
    }
}
