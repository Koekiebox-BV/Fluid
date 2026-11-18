package com.fluidbpm.ws.client.v1.contract;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.sqlutil.wrapper.SQLUtilWebSocketRESTWrapper;
import org.junit.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import static org.junit.Assert.*;

public class TestSqlWrapperRecovery {
    private boolean disabled, compressed, binary;

    @Before public void configure() {
        disabled = SQLUtilWebSocketRESTWrapper.DISABLE_WS;
        compressed = SQLUtilWebSocketRESTWrapper.COMPRESS_RSP;
        binary = SQLUtilWebSocketRESTWrapper.IS_MODE_WS_BINARY;
        SQLUtilWebSocketRESTWrapper.DISABLE_WS = false;
        SQLUtilWebSocketRESTWrapper.COMPRESS_RSP = false;
        SQLUtilWebSocketRESTWrapper.IS_MODE_WS_BINARY = false;
    }

    @After public void restore() {
        SQLUtilWebSocketRESTWrapper.DISABLE_WS = disabled;
        SQLUtilWebSocketRESTWrapper.COMPRESS_RSP = compressed;
        SQLUtilWebSocketRESTWrapper.IS_MODE_WS_BINARY = binary;
    }

    private SQLUtilWebSocketRESTWrapper client(LocalWebSocketFixture server) {
        return new SQLUtilWebSocketRESTWrapper(server.endpoint(), "AQID", 1500);
    }

    private void tableRead(SQLUtilWebSocketRESTWrapper client) {
        List<FormListing> result = client.getTableForms(true, 77L, new Form(203L));
        assertEquals(Long.valueOf(918), result.get(0).getListing().get(0).getId());
    }

    @Test(timeout = 20000) public void reconnectsAfterIdleDisconnectWithOriginalOptionsAndTicket() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            tableRead(client);
            String endpoint = server.uri;
            server.disconnectAll();
            tableRead(client);
            assertEquals(2, server.handshakes.get());
            assertEquals(endpoint, server.uri);
            assertEquals(2, server.requests.size());
            assertEquals(203L, server.requests.get(1).get("id").getAsLong());
            assertEquals(server.requests.get(0).get("serviceTicket"), server.requests.get(1).get("serviceTicket"));
            assertTrue(server.restRequests.isEmpty());
        }
    }

    @Test(timeout = 20000) public void retriesReadOnceWhenPeerClosesBeforeReply() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            server.disconnectReads.set(1);
            tableRead(client);
            assertEquals(2, server.handshakes.get());
            assertEquals(2, server.requests.size());
            assertTrue(server.restRequests.isEmpty());
        }
    }

    @Test(timeout = 20000) public void twoDisconnectsFallBackToRestAndDoNotKeepReconnecting() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            server.disconnectReads.set(Integer.MAX_VALUE);
            tableRead(client);
            tableRead(client);
            assertEquals(2, server.handshakes.get());
            assertEquals(2, server.requests.size());
            assertEquals(2, server.restRequests.size());
            assertEquals("AQID", server.restRequests.get(0).get("serviceTicket").getAsString());
            assertEquals(203L, server.restRequests.get(0).get("id").getAsLong());
            assertTrue(server.restUris.get(0).contains("get_table_forms_by_electronic_form_id"));
        }
    }

    @Test(timeout = 20000) public void unavailableWebSocketEndpointFallsBackToRest() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            server.handshakeStatus = 404;
            tableRead(client);
            assertEquals(2, server.handshakes.get());
            assertEquals(1, server.restRequests.size());
        }
    }

    @Test(timeout = 20000) public void stalledHandshakeHasBoundedRecoveryAndRestFallback() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture();
             SQLUtilWebSocketRESTWrapper client = new SQLUtilWebSocketRESTWrapper(server.endpoint(), "AQID", 100)) {
            server.stallHandshake = true;
            tableRead(client);
            assertEquals(2, server.handshakes.get());
            assertEquals(1, server.restRequests.size());
        }
    }

    @Test(timeout = 20000) public void serverAuthenticationAndPermissionFailuresAreNotRetried() throws Exception {
        for (int code : new int[]{FluidClientException.ErrorCode.SESSION_EXPIRED,
                FluidClientException.ErrorCode.LOGIN_FAILURE, FluidClientException.ErrorCode.PERMISSION_DENIED,
                FluidClientException.ErrorCode.FIELD_VALIDATE, FluidClientException.ErrorCode.IO_ERROR}) {
            try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
                server.errorCode = code;
                try { tableRead(client); fail("Expected server rejection"); }
                catch (FluidClientException expected) { assertEquals(code, expected.getErrorCode()); }
                assertEquals(1, server.handshakes.get());
                assertEquals(1, server.requests.size());
                assertTrue(server.restRequests.isEmpty());
            }
        }
    }

    @Test(timeout = 20000) public void handshakeAuthorizationRejectionIsNotRetriedOrSentToRest() throws Exception {
        for (int status : new int[]{401, 403}) {
            try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
                server.handshakeStatus = status;
                try { tableRead(client); fail("Expected handshake rejection"); }
                catch (FluidClientException expected) {
                    assertEquals(status == 401 ? FluidClientException.ErrorCode.LOGIN_FAILURE
                            : FluidClientException.ErrorCode.PERMISSION_DENIED, expected.getErrorCode());
                }
                assertEquals(1, server.handshakes.get());
                assertTrue(server.restRequests.isEmpty());
            }
        }
    }

    @Test(timeout = 20000) public void concurrentReadersShareReplacementConnection() throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(4);
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            tableRead(client);
            server.disconnectAll();
            CountDownLatch start = new CountDownLatch(1);
            List<Future<?>> reads = new ArrayList<>();
            for (int i = 0; i < 4; i++) reads.add(workers.submit(() -> {
                try { start.await(); } catch (InterruptedException e) { throw new AssertionError(e); }
                tableRead(client);
            }));
            start.countDown();
            for (Future<?> read : reads) read.get(5, TimeUnit.SECONDS);
            assertEquals(2, server.handshakes.get());
            assertEquals(5, server.requests.size());
            assertTrue(server.restRequests.isEmpty());
        } finally { workers.shutdownNow(); }
    }

    @Test(timeout = 30000) public void allFormDataReadPathsRecover() throws Exception {
        List<Consumer<SQLUtilWebSocketRESTWrapper>> reads = Arrays.asList(
                client -> assertNotNull(client.getAncestor(new Form(203L), true, true)),
                client -> assertEquals(1, client.getDescendants(true, true, true, false, new Form(203L)).size()),
                client -> assertEquals(1, client.getFormFields(true, new Form(203L)).size()),
                client -> assertEquals(1, client.getFormHistoryByForm(true, false, new Form(203L)).size()),
                client -> assertNotNull(client.getTableFormsPage(new Form(203L), true, 77, 10, 0)),
                client -> {
                    Form form = new Form(203L);
                    client.massPopulateFormFields(true, form);
                    assertEquals("response", form.getFormFields().get(0).getFieldName());
                });
        for (Consumer<SQLUtilWebSocketRESTWrapper> read : reads) {
            try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
                server.disconnectReads.set(1);
                read.accept(client);
                assertEquals(2, server.handshakes.get());
                assertTrue(server.restRequests.isEmpty());
            }
        }
    }

    @Test(timeout = 20000) public void nativeSqlIsNeverReplayedOnDisconnect() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            server.disconnectReads.set(Integer.MAX_VALUE);
            NativeSQLQuery query = new NativeSQLQuery();
            query.setDatasourceName("fixture");
            query.setQuery("SELECT 1");
            try { client.executeNativeSQL(query); fail("Expected disconnect"); }
            catch (FluidClientException expected) {
                // An abrupt socket close can surface as EOF or a reset, depending on OS/timing.
                int code = expected.getErrorCode();
                assertTrue("Expected a transport failure, got " + code,
                        code == FluidClientException.ErrorCode.IO_ERROR
                                || code == FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR);
            }
            assertEquals(1, server.handshakes.get());
            assertEquals(1, server.requests.size());
            assertTrue(server.restRequests.isEmpty());
        }
    }

    @Test(timeout = 20000) public void restPermissionFailureIsPropagatedAfterTransportFailure() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            server.disconnectReads.set(2);
            server.errorCode = FluidClientException.ErrorCode.PERMISSION_DENIED;
            try { tableRead(client); fail("Expected REST permission rejection"); }
            catch (FluidClientException expected) {
                assertEquals(FluidClientException.ErrorCode.PERMISSION_DENIED, expected.getErrorCode());
                assertEquals(1, expected.getSuppressed().length);
            }
            assertEquals(2, server.handshakes.get());
            assertEquals(1, server.restRequests.size());
            assertEquals("AQID", server.restRequests.get(0).get("serviceTicket").getAsString());
        }
    }

    @Test(timeout = 20000) public void changingReadOptionsReplacesTheEndpoint() throws Exception {
        try (LocalWebSocketFixture server = new LocalWebSocketFixture(); SQLUtilWebSocketRESTWrapper client = client(server)) {
            tableRead(client);
            String previous = server.uri;
            assertEquals(1, client.getTableForms(false, 78L, new Form(203L)).size());
            assertNotEquals(previous, server.uri);
            assertEquals(2, server.handshakes.get());
            assertTrue(server.restRequests.isEmpty());
        }
    }
}
