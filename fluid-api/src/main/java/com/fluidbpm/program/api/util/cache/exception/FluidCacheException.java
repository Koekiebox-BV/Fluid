/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
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
package com.fluidbpm.program.api.util.cache.exception;

import java.sql.SQLException;

import com.fluidbpm.program.api.util.exception.UtilException;

/**
 * Exception class related to Caching Exceptions exclusively.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see UtilException
 * @see SQLException
 */
public class FluidCacheException extends UtilException {

    /**
     * Constructor used to wrap a traditional {@code Exception}
     * into an {@code FluidCacheException}.
     *
     * @param messageParam Exception message.
     */
    public FluidCacheException(String messageParam) {
        super("Cache Problem: "+messageParam ,ErrorCode.CACHE);
    }

    /**
     * Constructor used to wrap a traditional {@code Exception}
     * into an {@code FluidCacheException}.
     *
     * @param messageParam Exception message.
     * @param exceptionParam Traditional Java Exception.
     */
    public FluidCacheException(String messageParam, Exception exceptionParam) {
        super("Cache Problem: "+messageParam,exceptionParam ,ErrorCode.CACHE);
    }
}
