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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow;
import com.fluidbpm.program.api.vo.ws.auth.AppRequestToken;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;

import junit.framework.TestCase;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestSQLUtilNativeWebSocketClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    @Before
    public void init()
    {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        this.loginClient = new LoginClient(BASE_URL);
    }

    /**
     *
     */
    @After
    public void destroy()
    {
        this.loginClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    @Ignore
    public void testGetTableFormsWithSpecificId()
    {
        if(!this.isConnectionValid())
        {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();
        String serviceTicketHex = null;
        if(serviceTicket != null && !serviceTicket.isEmpty())
        {
            serviceTicketHex =
                    UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(serviceTicket));
        }

        SQLUtilWebSocketExecuteNativeSQLClient webSocketClient =
                new SQLUtilWebSocketExecuteNativeSQLClient(
                        BASE_URL,
                        null,
                        serviceTicketHex,
                        TimeUnit.SECONDS.toMillis(60));

        long start = System.currentTimeMillis();
        int numberOfRecords = 1;

        NativeSQLQuery nativeSQLQuery = new NativeSQLQuery();

        nativeSQLQuery.setDatasourceName("flow-job");
        nativeSQLQuery.setQuery("SELECT * FROM form_definitions WHERE title = ?");

        List<SQLColumn> inputs = new ArrayList<>();
        inputs.add(new SQLColumn(
                null,1,Types.VARCHAR,
                "Mal"));

        nativeSQLQuery.setSqlInputs(inputs);

        List<SQLResultSet> resultListing =
                webSocketClient.executeNativeSQLSynchronized(nativeSQLQuery);

        long took = (System.currentTimeMillis() - start);

        webSocketClient.closeAndClean();

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");

        if(resultListing != null)
        {
            System.out.println("Listing is '"+resultListing.size()+"' -> \n\n\n");

            for(SQLResultSet listing : resultListing)
            {
                //System.out.println("Response For ::: "+listing.getEcho());

                List<SQLRow> tableForms = listing.getListing();

                for(SQLRow form : tableForms)
                {
                    if(form.getSqlColumns() == null)
                    {
                        continue;
                    }

                    for(SQLColumn column : form.getSqlColumns())
                    {
                        System.out.println(
                                "|"+column.getColumnName()+"|"+
                                column.getColumnIndex()
                                +"| ->" +column.getSqlValue());
                    }
                }
            }
        }
        else
        {
            System.out.println("Nothing...");
        }

        System.out.println("Took '"+took+"' millis for '"+numberOfRecords+"' random records.");
    }
}
