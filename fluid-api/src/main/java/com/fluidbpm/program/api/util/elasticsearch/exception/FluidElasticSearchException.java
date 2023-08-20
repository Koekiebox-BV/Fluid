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

package com.fluidbpm.program.api.util.elasticsearch.exception;

import java.sql.SQLException;

import com.fluidbpm.program.api.util.exception.UtilException;

/**
 * Exception class related to ElasticSearch exclusively.
 *
 * @author jasonbruwer
 * @since v1.3
 *
 * @see UtilException
 * @see SQLException
 */
public class FluidElasticSearchException extends UtilException {

	/**
	 * Constructor used to wrap a traditional {@code Exception}
	 * into an {@code FluidElasticSearchException}.
	 *
	 * @param messageParam Exception message.
	 */
	public FluidElasticSearchException(String messageParam) {
		super("ElasticSearch Problem: "+messageParam ,ErrorCode.ELASTIC);
	}

	/**
	 * Constructor used to wrap a traditional {@code Exception}
	 * into an {@code FluidElasticSearchException}.
	 *
	 * @param messageParam Exception message.
	 * @param exceptionParam Traditional Java Exception.
	 */
	public FluidElasticSearchException(String messageParam, Exception exceptionParam) {
		super("ElasticSearch Problem: "+messageParam,exceptionParam ,ErrorCode.ELASTIC);
	}
}
