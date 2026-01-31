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

        // Valid return types:
        public static final int FLUID_ITEM = -2;//-1 is reserved for not set.
        public static final int FORM = -3;
        public static final int FIELD = -4;
        public static final int FORM_HISTORIC_DATA_LISTING = -5;
        public static final int FORM_LISTING = -6;
        public static final int FORM_FIELD_LISTING = -7;
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

    public static final class Path {
        public static final class Form {
            public static final String FORM = "";
        }
    }
}
