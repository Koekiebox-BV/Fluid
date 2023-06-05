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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow;
import com.fluidbpm.ws.client.v1.ABaseLoggedInTestCase;
import com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient;
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
public class TestSQLUtilNativeWebSocketClient extends ABaseLoggedInTestCase {
	private String serviceTicketHex;

	@Before
	@Override
	public void init() {
		if (!this.isConnectionValid()) return;

		// Login:
		super.init();

		this.serviceTicketHex = UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(this.serviceTicket));
	}

	/**
	 * Plain SQL (no stored proc)
	 */
	@Test
	public void testNativeSQLExecutionQuery() {
		if (!this.isConnectionValid()) return;

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
		nativeSQLQuery.setQuery("SELECT * FROM form_definition WHERE title = ?");

		List<SQLColumn> inputs = new ArrayList<>();
		inputs.add(new SQLColumn(
				null,1,Types.VARCHAR,
				"Email"));
		nativeSQLQuery.setSqlInputs(inputs);

		List<SQLResultSet> resultListing = webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

		long took = (System.currentTimeMillis() - start);

		start = System.currentTimeMillis();

		webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

		long tookSecond = (System.currentTimeMillis() - start);

		webSocketClient.closeAndClean();

		System.out.println("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");

		if (resultListing != null) {
			System.out.println("Listing is '"+resultListing.size()+"' -> \n\n\n");

			for (SQLResultSet listing : resultListing) {
				//System.out.println("Response For ::: "+listing.getEcho());
				List<SQLRow> tableForms = listing.getListing();

				if (tableForms == null) continue;

				for (SQLRow form : tableForms) {
					if (form.getSqlColumns() == null) continue;

					for (SQLColumn column : form.getSqlColumns()) {
						System.out.println("|"+column.getColumnName()+"|"+ column.getColumnIndex() +"| ->" +column.getSqlValue());
					}
				}
			}
		} else {
			System.out.println("Nothing...");
		}

		System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	/**
	 * Standard Stored Proc Execution.
	 */
	@Test
	public void testNativeSQLExecutionStoredProc() {
		if (!this.isConnectionValid()) return;

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

		System.out.println("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");

		if (resultListing != null) {
			System.out.println("Listing is '"+resultListing.size()+"' -> \n\n\n");

			for (SQLResultSet listing : resultListing) {
				//System.out.println("Response For ::: "+listing.getEcho());

				List<SQLRow> tableForms = listing.getListing();

				if (tableForms == null) continue;

				for (SQLRow form : tableForms) {
					if (form.getSqlColumns() == null) continue;

					for (SQLColumn column : form.getSqlColumns()) {
						System.out.println("|"+column.getColumnName()+"|"+ column.getColumnIndex() +"| ->" +column.getSqlValue());
					}
				}
			}
		} else {
			System.out.println("Nothing...");
		}

		System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}

	/**
	 * Compressed Query.
	 */
	@Test
	public void testNativeSQLExecutionStoredProcCompressed() {
		if (!this.isConnectionValid()) return;

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

		System.out.println("Took '"+took+"|"+tookSecond+"' millis for '"+numberOfRecords+"' random records.");

		if (resultListing != null) {
			System.out.println("Listing is '"+resultListing.size()+"' -> \n\n\n");

			for (SQLResultSet listing : resultListing) {
				List<SQLRow> tableForms = listing.getListing();

				if (tableForms == null) continue;

				for (SQLRow form : tableForms) {
					if (form.getSqlColumns() == null) continue;

					for (SQLColumn column : form.getSqlColumns()) {
						System.out.println("|"+column.getColumnName()+"|"+ column.getColumnIndex() +"| ->" +column.getSqlValue());
					}
				}
			}
		} else {
			System.out.println("Nothing...");
		}

		System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
	}
}
