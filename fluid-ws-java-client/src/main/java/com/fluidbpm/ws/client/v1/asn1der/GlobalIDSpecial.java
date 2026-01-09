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
public class GlobalIDSpecial {
    public static int ERROR_TYPE = -666;
    public static int TAG_PAYLOAD_POPULATE = -100000;
}
