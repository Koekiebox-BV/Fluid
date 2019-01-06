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

package com.fluidbpm.program.api;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * The base <code>interface</code> for all Fluid Java Custom Programs.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ICustomProgram
 * @see ICustomScheduledAction
 * @see ICustomWebAction
 */
public interface IActionBase extends Serializable {

	/**
	 * <code>Execute Order (1)</code>
	 *
	 * <p>
	 * Acts as a constructor that provides a list of configured {@code dataSourcesParam}
	 * in Fluid. Including the {@code Datasource} to the <code>Fluid Core</code> {@code DataSource}.
	 *
	 * This method will always be <code>executed</code> first when the Fluid Third Party Library
	 * executes any of the sub Custom Programs.
	 *
	 * The following properties are set by default;
	 *
	 * {@code JAR_LOCATION} - The relative path of the jar file executed as part of a sub-action.
	 * {@code TABLE_RECORD_PARENT_FORM_CONTAINER_ID} - The parent form container id of
	 * table records for custom form actions.
	 * {@code VALID_DATA_SOURCES} - A comma separated list of valid data-sources.
	 * {@code EXECUTION_ORIGIN} -
	 *                              default : Complete classpath in jar
	 *                              internal-runner : Web Socket on internal server.
	 *                              external-runner : Web Socket on external server.
	 *
	 * {@code SECRET_INSTANCE_UUID} - The secret UUID for the destination server to trust the request.
	 *
	 *
	 * @see javax.sql.DataSource
	 *
	 * @param propertiesParam Properties applicable to each custom program.
	 * @param dataSourcesParam A list of created JDBC Data Sources in the system.
	 *
	 * @throws Exception If a problem occurs.
	 *
	 */
	public abstract void init(
			Properties propertiesParam,
			List<DataSource> dataSourcesParam)
	throws Exception;

	/**
	 * <code>Execute Order (LAST)</code>
	 *
	 * <p>
	 * Any cleanup code may be placed in the <code>destroy</code> method.
	 * This is the last method executed before the Fluid framework takes over.
	 *
	 * @throws Exception If a problem occurs.
	 *
	 */
	public abstract void destroy() throws Exception;
}
