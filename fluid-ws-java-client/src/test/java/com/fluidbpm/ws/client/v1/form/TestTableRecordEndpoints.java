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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.form.TableRecordListing;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.ABaseTestCase;
import com.fluidbpm.ws.client.v1.asn1der.ws.WebSocketASNDERClient;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilClient;
import com.fluidbpm.ws.client.v1.sqlutil.SQLUtilWebSocketGetTableFormsPageClient;
import com.fluidbpm.ws.client.v1.user.LoginClient;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.MultipleFailureException;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Exercises the deployed batch and paging endpoints against the configured database.
 * CircleCI supplies FLUID_WS_URL for each database job. Without an explicit URL these
 * tests are skipped; with one, authentication, connectivity and endpoint failures fail the test.
 * Run only against a disposable test installation: each case creates and removes its own data.
 */
@RunWith(Parameterized.class)
public class TestTableRecordEndpoints extends ABaseTestCase {
    private enum Protocol { REST, JSON_WEB_SOCKET, ASN1_DER }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> protocols() {
        return Arrays.asList(new Object[]{Protocol.REST}, new Object[]{Protocol.JSON_WEB_SOCKET},
                new Object[]{Protocol.ASN1_DER});
    }

    private final Protocol protocol;
    private final List<Field> fields = new ArrayList<>();
    private final List<Form> definitions = new ArrayList<>();
    private FormContainerClient forms;
    private FormDefinitionClient formDefinitions;
    private FormFieldClient formFields;
    private SQLUtilClient sql;
    private RawTableClient raw;
    private WebSocketTableRecordBatchCreateClient text;
    private WebSocketASNDERClient der;
    private String ticket;
    private String hexTicket;
    private String prefix;
    private Form childDefinition;
    private Form parent;
    private Form otherParent;
    private Field textField;
    private Field amountField;
    private Field tableField;

    public TestTableRecordEndpoints(Protocol protocol) {
        this.protocol = protocol;
    }

    @Before
    public void createFixture() {
        String configuredUrl = System.getProperty("FLUID_WS_URL");
        Assume.assumeTrue("Set -DFLUID_WS_URL to a disposable Fluid test server",
                configuredUrl != null && !configuredUrl.trim().isEmpty());
        prefix = "JUnit Table Page " + UUID.randomUUID().toString();
        try (LoginClient login = new LoginClient(BASE_URL)) {
            ticket = login.login(USERNAME, PASSWORD).getServiceTicket();
        }
        assertNotNull("The configured test server must authenticate", ticket);
        hexTicket = UtilGlobal.encodeBase16(UtilGlobal.decodeBase64(ticket));
        forms = new FormContainerClient(BASE_URL, ticket);
        formDefinitions = new FormDefinitionClient(BASE_URL, ticket);
        formFields = new FormFieldClient(BASE_URL, ticket);
        sql = new SQLUtilClient(BASE_URL, ticket);
        raw = new RawTableClient(BASE_URL, ticket);

        textField = formFields.createFieldTextPlain(field(prefix + " Text"));
        fields.add(textField);
        amountField = formFields.createFieldDecimalPlain(field(prefix + " Amount"));
        fields.add(amountField);
        Form child = new Form(prefix + " Child");
        child.setFormDescription("Temporary batch and paging endpoint test definition.");
        child.setFormFields(Arrays.asList(textField, amountField));
        childDefinition = formDefinitions.createFormDefinition(child);
        definitions.add(childDefinition);

        tableField = formFields.createFieldTable(field(prefix + " Rows"), childDefinition, false);
        fields.add(tableField);
        Form parentDefinition = new Form(prefix + " Parent");
        parentDefinition.setFormDescription("Temporary batch and paging endpoint test parent.");
        parentDefinition.setFormFields(Collections.singletonList(tableField));
        parentDefinition = formDefinitions.createFormDefinition(parentDefinition);
        definitions.add(parentDefinition);
        parent = forms.createFormContainer(new Form(parentDefinition.getFormType(), prefix + " Parent One"));
        otherParent = forms.createFormContainer(new Form(parentDefinition.getFormType(), prefix + " Parent Two"));

        if (protocol == Protocol.JSON_WEB_SOCKET) {
            text = new WebSocketTableRecordBatchCreateClient(BASE_URL, hexTicket, 30000);
        } else if (protocol == Protocol.ASN1_DER) {
            der = new WebSocketASNDERClient(BASE_URL, hexTicket, 30000);
            der.requestFullPayloadPopulate();
        }
    }

    @Test(timeout = 120000)
    public void createsBatchesAndReadsOrderedPagesWithPersistedFields() {
        List<Long> expectedIds = new ArrayList<>();
        for (int[] range : new int[][]{{0, 2}, {2, 5}}) {
            List<TableRecord> records = new ArrayList<>();
            for (int index = range[0]; index < range[1]; index++) records.add(row(parent, index));
            TableRecordListing created = create(batch(records));
            assertEquals(Integer.valueOf(records.size()), created.getListingCount());
            assertEquals(records.size(), created.getListing().size());
            for (int index = 0; index < records.size(); index++) {
                TableRecord saved = created.getListing().get(index);
                assertNotNull(saved.getFormContainer().getId());
                assertEquals(parent.getId(), saved.getParentFormContainer().getId());
                assertEquals(tableField.getId(), saved.getParentFormField().getId());
                assertValues(saved.getFormContainer(), range[0] + index);
                expectedIds.add(saved.getFormContainer().getId());
            }
        }
        create(batch(Collections.singletonList(row(otherParent, 99))));
        List<Long> fetchedIds = new ArrayList<>();
        for (int offset = 0; offset < 5; offset += 2) {
            FormListing page = page(parent, true, childDefinition.getId(), 2, offset);
            assertEquals(Integer.valueOf(5), page.getListingCount());
            assertEquals(Integer.valueOf(offset), page.getListingIndex());
            assertEquals(Integer.valueOf(offset / 2), page.getListingPage());
            assertEquals(Math.min(2, 5 - offset), page.getListing().size());
            for (int index = 0; index < page.getListing().size(); index++) {
                Form saved = page.getListing().get(index);
                fetchedIds.add(saved.getId());
                assertValues(saved, offset + index);
            }
        }
        assertEquals("No omitted, repeated or reordered records", expectedIds, fetchedIds);
        FormListing idsOnly = page(parent, false, -1, 2, 0);
        assertEquals(Integer.valueOf(5), idsOnly.getListingCount());
        for (Form saved : idsOnly.getListing()) {
            assertTrue(saved.getFormFields() == null || saved.getFormFields().isEmpty());
        }
        FormListing empty = page(parent, true, -1, 2, 20);
        assertEquals(Integer.valueOf(5), empty.getListingCount());
        assertTrue(empty.getListing().isEmpty());
        FormListing filtered = page(parent, true, definitions.get(1).getId(), 2, 0);
        assertEquals(Integer.valueOf(0), filtered.getListingCount());
        assertTrue(filtered.getListing().isEmpty());
        assertEquals(Integer.valueOf(1), page(otherParent, true, -1, 2, 0).getListingCount());
        // An independent REST fetch confirms that WebSocket results are persisted.
        assertEquals(expectedIds.get(0), sql.getTableFormsPage(parent, true, -1, 1, 0).getListing().get(0).getId());
    }

    @Test(timeout = 120000)
    public void rejectsMalformedRecordsAndInvalidRestBoundsBeforeAnyWrites() {
        TableRecord invalid = row(parent, 1);
        invalid.setParentFormContainer(null);
        expectServerError(() -> create(batch(Arrays.asList(row(parent, 0), invalid))), true);
        assertNoChildRowsPersisted();
        if (protocol == Protocol.REST) {
            // Bypass typed-client validation so these assertions exercise server-side guards.
            for (int[] bounds : new int[][]{{0, 0}, {251, 0}, {25, -1}}) {
                expectServerError(() -> raw.page(parent, bounds[0], bounds[1]), true);
            }
            expectServerError(() -> raw.create(batch(Collections.emptyList())), true);
            expectServerError(() -> raw.create(batch(Collections.nCopies(251, row(parent, 0)))), true);
            assertNoChildRowsPersisted();
        }
    }

    @Test(timeout = 120000)
    public void rollsBackEarlierRecordWhenLaterPersistenceFails() {
        // A positive but nonexistent parent passes shape validation, then fails in persistence.
        TableRecord invalid = row(new Form(Long.MAX_VALUE), 1);
        expectServerError(() -> create(batch(Arrays.asList(row(parent, 0), invalid))), false);
        assertNoChildRowsPersisted();
    }

    private static Field field(String name) {
        Field field = new Field(name);
        field.setFieldDescription("Temporary field for batch and paging endpoint tests.");
        return field;
    }

    private TableRecord row(Form owner, int index) {
        Form child = new Form(childDefinition.getFormType(), prefix + " Row " + index);
        child.setFieldValue(textField.getFieldName(), "Value " + index + " – café", Field.Type.Text);
        child.setFieldValue(amountField.getFieldName(), index + 0.25, Field.Type.Decimal);
        return new TableRecord(child, new Form(owner.getId()), new Field(tableField.getId(), tableField.getFieldName()));
    }

    private static TableRecordListing batch(List<TableRecord> records) {
        TableRecordListing batch = new TableRecordListing();
        batch.setListing(records);
        return batch;
    }

    private TableRecordListing create(TableRecordListing batch) {
        switch (protocol) {
            case JSON_WEB_SOCKET: return text.createTableRecordsBatch(batch);
            case ASN1_DER: return der.createTableRecordsBatch(batch);
            default: return forms.createTableRecordsBatch(batch);
        }
    }

    private FormListing page(Form owner, boolean includeFields, long definition, int limit, int offset) {
        switch (protocol) {
            case JSON_WEB_SOCKET:
                try (SQLUtilWebSocketGetTableFormsPageClient client = new SQLUtilWebSocketGetTableFormsPageClient(
                        BASE_URL, hexTicket, 30000, includeFields, definition, limit, offset)) {
                    return client.getTableFormsPage(new Form(owner.getId()));
                }
            case ASN1_DER: return der.getTableFormsPage(new Form(owner.getId()), includeFields, definition, limit, offset);
            default: return sql.getTableFormsPage(owner, includeFields, definition, limit, offset);
        }
    }

    private void assertValues(Form saved, int index) {
        assertEquals("Value " + index + " – café", saved.getFieldValueAsString(textField.getFieldName()));
        assertEquals(index + 0.25, saved.getFieldValueAsDouble(amountField.getFieldName()), 0.000001);
    }

    private static void expectServerError(Runnable operation, boolean validation) {
        try {
            operation.run();
            fail("The deployed endpoint must reject this request");
        } catch (FluidClientException error) {
            for (Throwable cause = error; cause != null; cause = cause.getCause()) {
                assertFalse("A timeout is not a server rejection", cause instanceof java.util.concurrent.TimeoutException);
            }
            assertFalse("A missing response is not a server rejection", error.getMessage().startsWith("No complete table-operation response"));
            assertFalse("A missing response is not a server rejection", error.getMessage().startsWith("Missing batch response"));
            assertFalse("A disconnect is not a server rejection", error.getMessage().contains("closed by the server prior to the response"));
            assertNotEquals("A connection failure is not a server rejection", FluidClientException.ErrorCode.CONNECT_ERROR, error.getErrorCode());
            if (validation) assertEquals(FluidClientException.ErrorCode.FIELD_VALIDATE, error.getErrorCode());
        }
    }

    private SQLResultSet rowsForDefinition(Form definition) {
        NativeSQLQuery query = new NativeSQLQuery();
        query.setDatasourceName(FLUID_DS);
        query.setQuery("SELECT id FROM form_container WHERE form_definition = ? ORDER BY id");
        query.setSqlInputs(Collections.singletonList(new SQLColumn("definition", 1, Types.BIGINT, definition.getId())));
        return sql.executeSQL(query);
    }

    private void assertNoChildRowsPersisted() {
        assertEquals(Integer.valueOf(0), sql.getTableFormsPage(parent, true, -1, 25, 0).getListingCount());
        // Check the table itself as well: a failed batch must not leave unlinked child forms.
        assertTrue("A failed batch left child forms in SQL", rowsForDefinition(childDefinition).getListing().isEmpty());
    }

    @After
    public void removeFixture() throws Exception {
        List<Throwable> failures = new ArrayList<>();
        for (Form definition : definitions) {
            cleanup(failures, () -> rowsForDefinition(definition).getListing().forEach(row -> {
                Object id = row.getSqlColumns().get(0).getSqlValue();
                forms.deleteFormContainer(new Form(((Number) id).longValue()));
            }));
        }
        // Remove the parent definition/table field before the referenced child definition.
        for (int index = definitions.size() - 1; index >= 0; index--) {
            Form definition = definitions.get(index);
            cleanup(failures, () -> formDefinitions.deleteFormDefinition(definition));
            if (index == 1) cleanup(failures, () -> formFields.deleteField(tableField));
        }
        for (Field field : fields) {
            if (field != tableField || definitions.size() < 2) cleanup(failures, () -> formFields.deleteField(field));
        }
        for (AutoCloseable client : new AutoCloseable[]{text, der, raw, sql, forms, formDefinitions, formFields}) {
            if (client != null) {
                try { client.close(); } catch (Exception error) { failures.add(error); }
            }
        }
        MultipleFailureException.assertEmpty(failures);
    }

    private static void cleanup(List<Throwable> failures, Runnable operation) {
        try { operation.run(); } catch (Exception error) { failures.add(error); }
    }

    /** Raw REST requests intentionally bypass client-side validation. */
    private static class RawTableClient extends ABaseClientWS {
        RawTableClient(String endpoint, String ticket) {
            super(endpoint);
            this.setServiceTicket(ticket);
        }

        JsonObject create(TableRecordListing request) {
            request.setServiceTicket(this.serviceTicket);
            return this.putJson(request, WS.Path.Version.VERSION_1 + WS.Path.FormContainerTableRecord.Version1.ROOT
                    + WS.Path.FormContainerTableRecord.Version1.BATCH);
        }

        JsonObject page(Form parent, int limit, int offset) {
            Form request = new Form(parent.getId());
            request.setServiceTicket(this.serviceTicket);
            return this.postJson(request, WS.Path.Version.VERSION_1 + WS.Path.SQLUtil.Version1.ROOT
                    + WS.Path.SQLUtil.Version1.SQL_UTIL_FORM_GET_TABLE_FORMS_PAGE
                    + "?" + WS.Path.FlowItem.Version1.QueryParam.QUERY_LIMIT + "=" + limit
                    + "&" + WS.Path.FlowItem.Version1.QueryParam.OFFSET + "=" + offset);
        }
    }
}
