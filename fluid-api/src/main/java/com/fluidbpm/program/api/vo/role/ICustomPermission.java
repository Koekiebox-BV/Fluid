/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2025] Koekiebox (Pty) Ltd
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

/**
 * Represents a custom permission entity that provides a mechanism
 * for retrieving a specific permission in the form of a string.
 * Implementing classes are expected to define the logic for what
 * the permission represents and how it is retrieved.
 */
public interface ICustomPermission {
    /**
     * Retrieves the permission as a string.
     * @return the permission associated with the implementing entity.
     */
    String getPermission();

    /**
     * Retrieves the ordinal value associated with the implementing entity.
     * The ordinal is typically used to indicate the position or sequence
     * of the entity, often within an enumeration or ordered context.
     * @return the ordinal value of the entity.
     */
    int ordinal();
}
