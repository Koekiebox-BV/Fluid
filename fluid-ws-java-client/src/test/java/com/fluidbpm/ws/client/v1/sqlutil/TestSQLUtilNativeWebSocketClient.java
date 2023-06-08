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

package com.fluidbpm.ws.client.v1.sqlutil;

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient;
import junit.framework.TestCase;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test the execution of native standard SQL as well as stored procedures.
 * Created by jasonbruwer on 14/12/22.
 */
@Log
public class TestSQLUtilNativeWebSocketClient extends ABaseLoggedInTestCase {
	private String serviceTicketHex;

	@Before
	@Override
	public void init() {
		if (this.isConnectionInValid) return;

		// Login:
		super.init();
	}

	/**
	 * Plain SQL (no stored proc)
	 */
	@Test
	public void testNativeSQLExecutionQuery() {
		if (this.isConnectionInValid) return;

		SQLUtilWebSocketExecuteNativeSQLClient webSocketClient = new SQLUtilWebSocketExecuteNativeSQLClient(
						BASE_URL,
						null,
						this.serviceTicketHex,
						TimeUnit.SECONDS.toMillis(60)
		);

		long start = System.currentTimeMillis();
		int numberOfRecords = 1;

		NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();
		nativeSQLQuery.setDatasourceName(FLUID_DS);
		nativeSQLQuery.setQuery("SELECT * FROM form_definition WHERE title = ?");

		List<SQLColumn> inputs = new ArrayList<>();
		inputs.add(new SQLColumn(null,1,Types.VARCHAR, "Email"));
		nativeSQLQuery.setSqlInputs(inputs);

		List<SQLResultSet> resultListing = webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

		long took = (System.currentTimeMillis() - start);
		start = System.currentTimeMillis();
		webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);
		long tookSecond = (System.currentTimeMillis() - start);
		webSocketClient.closeAndClean();

		log.info("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");
		this.validateResultSet(resultListing, 11);
		log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	/**
	 * Standard Stored Proc Execution.
	 */
	@Test
	public void testNativeSQLExecutionStoredProc() {
		if (this.isConnectionInValid) return;

		SQLUtilWebSocketExecuteNativeSQLClient webSocketClient =
				new SQLUtilWebSocketExecuteNativeSQLClient(
						BASE_URL,
						null,
						this.serviceTicketHex,
						TimeUnit.SECONDS.toMillis(60));

		long start = System.currentTimeMillis();
		int numberOfRecords = 1;

		NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();

		nativeSQLQuery.setDatasourceName(FLUID_DS);
		nativeSQLQuery.setStoredProcedure("{call Fluid_GetFormFieldsForFormDefinition(?)}");

		List<SQLColumn> inputs = new ArrayList<>();
		inputs.add(new SQLColumn(null,1,Types.BIGINT, 1L));
		nativeSQLQuery.setSqlInputs(inputs);

		List<SQLResultSet> resultListing = webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);
		long took = (System.currentTimeMillis() - start);
		start = System.currentTimeMillis();
		webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);
		long tookSecond = (System.currentTimeMillis() - start);
		webSocketClient.closeAndClean();

		log.info("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");
		this.validateResultSet(resultListing, 6);
		log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	/**
	 * Compressed Query.
	 */
	@Test
	public void testNativeSQLExecutionStoredProcCompressed() {
		if (this.isConnectionInValid) return;

		SQLUtilWebSocketExecuteNativeSQLClient webSocketClient =
				new SQLUtilWebSocketExecuteNativeSQLClient(
						BASE_URL,
						null,
						this.serviceTicketHex,
						TimeUnit.SECONDS.toMillis(60),
						true,"");

		long start = System.currentTimeMillis();
		int numberOfRecords = 1;

		NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();

		nativeSQLQuery.setDatasourceName(FLUID_DS);
		nativeSQLQuery.setStoredProcedure("{call Fluid_GetFormFieldsForFormDefinition(?)}");

		List<SQLColumn> inputs = new ArrayList<>();
		inputs.add(new SQLColumn(
				null,1,Types.BIGINT,
				1L));

		nativeSQLQuery.setSqlInputs(inputs);

		List<SQLResultSet> resultListing = webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

		long took = (System.currentTimeMillis() - start);

		start = System.currentTimeMillis();

		webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

		long tookSecond = (System.currentTimeMillis() - start);

		webSocketClient.closeAndClean();

		log.info("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");
		this.validateResultSet(resultListing, 6);
		log.info("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	private void validateResultSet(List<SQLResultSet> resultListing, int expectedColumns) {
		TestCase.assertNotNull(resultListing);
		TestCase.assertEquals(1, resultListing.size());
		resultListing.forEach(itm -> {
			itm.getListing().forEach(row -> {
				TestCase.assertNotNull(row.getSqlColumns());
				TestCase.assertEquals(expectedColumns, row.getSqlColumns().size());
			});
		});
	}
}
