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

package com.fluidbpm.ws.client.v1.sqlutil.wrapper;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormFieldListing;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecordPage;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.fluidbpm.ws.client.v1.form.FormContainerClient;
import com.fluidbpm.ws.client.v1.form.WebSocketGetFormHistoryByFormClient;
import com.fluidbpm.ws.client.v1.sqlutil.*;
import com.fluidbpm.ws.client.v1.sqlutil.sqlnative.SQLUtilWebSocketExecuteNativeSQLClient;
import com.fluidbpm.ws.client.v1.websocket.WebSocketClient;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.function.Function;

/**
 * Wrapper class used for when WebSockets is not a
 * possibility, the wrapper will fallback to REST.
 *
 * @author jasonbruwer on 3/20/18.
 * @since 1.8
 */
public class SQLUtilWebSocketRESTWrapper extends ABaseClientWS implements Closeable {
    private final String baseURL;
    private final User loggedInUser;

    private final long timeoutMillis;

    //Clients...
    private final ReadClient<SQLUtilWebSocketGetAncestorClient> getAncestorClient = new ReadClient<>();
    private final ReadClient<SQLUtilWebSocketGetDescendantsClient> getDescendantsClient = new ReadClient<>();
    private final ReadClient<SQLUtilWebSocketGetTableFormsClient> getTableFormsClient = new ReadClient<>();
    private final ReadClient<SQLUtilWebSocketGetFormFieldsClient> getFormFieldsClient = new ReadClient<>();
    private final ReadClient<WebSocketGetFormHistoryByFormClient> getFormHistoryByFormClient = new ReadClient<>();

    private SQLUtilWebSocketExecuteNativeSQLClient sqlUtilWebSocketExecNativeClient = null;

    private final SQLUtilClient sqlUtilClient;
    private final FormContainerClient fcClient;

    public static boolean COMPRESS_RSP = true;
    public static String COMPRESS_RSP_CHARSET = UtilGlobal.EMPTY;

    //Mode...
    private volatile Mode mode = null;
    private WebSocketClient.Mode wsClientMode = null;

    public static boolean DISABLE_WS;
    public static boolean IS_MODE_WS_BINARY;

    /**
     * Aliases for properties used by the {@code SQLUtilWebSocketRESTWrapper}.
     */
    private static class PropName {
        /**
         * Keep Web Sockets disabled.
         */
        public static final String FLUID_API_DISABLE_WEB_SOCKETS = "FluidAPIDisableWebSocket";
        public static final String FLUID_API_WEB_SOCKET_BINARY = "FluidAPIWebSocketBinary";
    }

    //Assign the property whether ti dis
    static {
        try {
            DISABLE_WS = Boolean.parseBoolean(System.getProperty(
                    PropName.FLUID_API_DISABLE_WEB_SOCKETS,
                    String.valueOf(false)).trim()
            );
        } catch (NumberFormatException nfe) {
            DISABLE_WS = false;
        }

        try {
            IS_MODE_WS_BINARY = Boolean.parseBoolean(System.getProperty(
                    PropName.FLUID_API_WEB_SOCKET_BINARY,
                    String.valueOf(false)).trim()
            );
        } catch (NumberFormatException nfe) {
            IS_MODE_WS_BINARY = false;
        }
    }

    /**
     * The mode.
     */
    private enum Mode {
        WebSocketActive,
        RESTfulActive
    }

    /**
     * New wrapper instance.
     *
     * @param baseURL Example {@code https://[instance].fluidbpm.com/fluid-ws/}
     * @param serviceTicket The service ticket for the logged in user.
     * @param timeoutMillis The timeout of the request in millis.
     */
    public SQLUtilWebSocketRESTWrapper(String baseURL, String serviceTicket, long timeoutMillis) {
        super(baseURL, serviceTicket);
        this.baseURL = baseURL;

        this.loggedInUser = new User();
        this.loggedInUser.setServiceTicket(serviceTicket);
        this.timeoutMillis = timeoutMillis;

        this.sqlUtilClient = new SQLUtilClient(this.baseURL, this.loggedInUser.getServiceTicket());
        this.fcClient = new FormContainerClient(this.baseURL, this.loggedInUser.getServiceTicket());
    }

    /**
     * New wrapper instance.
     *
     * @param baseURL Example {@code https://[instance].fluidbpm.com/fluid-ws/}
     * @param loggedInUser The currently logged in user.
     * @param timeoutMillis The timeout of the request in millis.
     */
    public SQLUtilWebSocketRESTWrapper(String baseURL, User loggedInUser, long timeoutMillis) {
        super(baseURL, loggedInUser == null ? null : loggedInUser.getServiceTicket());
        this.baseURL = baseURL;
        this.loggedInUser = loggedInUser;
        this.timeoutMillis = timeoutMillis;

        this.sqlUtilClient = new SQLUtilClient(this.baseURL, this.loggedInUser.getServiceTicket());
        this.fcClient = new FormContainerClient(this.baseURL, this.loggedInUser.getServiceTicket());
    }

    /**
     * Retrieves the Ancestor for the {@code formToGetAncestorForParam}.
     *
     * @param formToGetAncestorFor The Fluid Form to get Ancestor for.
     * @param includeFieldData Should Ancestor (Form) Field data be included?
     * @param includeTableFields Should Table Record (Form) Field data be included?
     *
     * @return The {@code formToGetAncestorForParam} Ancestor as {@code Form}'s.
     */
    public Form getAncestor(Form formToGetAncestorFor, boolean includeFieldData, boolean includeTableFields) {
        Form form = formToGetAncestorFor == null ? null : new Form(formToGetAncestorFor.getId());
        return this.readWithRecovery(this.getAncestorClient,
                Arrays.asList(includeFieldData, includeTableFields, COMPRESS_RSP, COMPRESS_RSP_CHARSET),
                () -> new SQLUtilWebSocketGetAncestorClient(this.baseURL, null,
                        this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                        includeFieldData, includeTableFields, COMPRESS_RSP, COMPRESS_RSP_CHARSET),
                client -> client.getAncestorSynchronized(form),
                () -> this.sqlUtilClient.getAncestor(form, includeFieldData, includeTableFields));
    }

    /**
     * Retrieves all the Descendants (Forms) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Ancestor (Form) Field data be included?
     * @param includeTableFields Should Table Record (Form) Field data be included?
     * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
     * @param massFetch Is the fetch a large fetch.
     * @param formsToGetDescFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
     */
    public List<FormListing> getDescendants(
            boolean includeFieldData,
            boolean includeTableFields,
            boolean includeTableFieldFormRecordInfo,
            boolean massFetch,
            Form ... formsToGetDescFor
    ) {
        if (formsToGetDescFor == null || formsToGetDescFor.length == 0) return null;
        Form[] forms = minimalForms(formsToGetDescFor);
        return this.readWithRecovery(this.getDescendantsClient,
                Arrays.asList(includeFieldData, includeTableFields, includeTableFieldFormRecordInfo,
                        massFetch, COMPRESS_RSP, COMPRESS_RSP_CHARSET),
                () -> new SQLUtilWebSocketGetDescendantsClient(this.baseURL, null,
                        this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                        includeFieldData, includeTableFields, includeTableFieldFormRecordInfo, massFetch,
                        COMPRESS_RSP, COMPRESS_RSP_CHARSET),
                client -> client.getDescendantsSynchronized(forms),
                () -> {
                    List<FormListing> result = new ArrayList<>();
                    for (Form form : forms) {
                        result.add(listing(this.sqlUtilClient.getDescendants(form, includeFieldData,
                                includeTableFields, includeTableFieldFormRecordInfo)));
                    }
                    return result;
                });
    }

    /**
     * Retrieves a database-backed page of table records.
     * Count and page are separate reads and may differ during concurrent modifications.
     *
     * @param parent The parent form containing the ID.
     * @param includeFields Whether to populate field data for the returned records.
     * @param definitionId Form definition filter; nonpositive values include all definitions.
     * @param limit Maximum number of records to retrieve.
     * @param offset Zero-based offset of the first record.
     * @return The requested page and total matching record count.
     */
    public FormListing getTableFormsPage(
            Form parent,
            boolean includeFields,
            long definitionId,
            int limit,
            int offset
    ) {
        TableRecordPage.validate(limit, offset);
        // Page options are part of the endpoint, so this client lives for one operation.
        ReadClient<SQLUtilWebSocketGetTableFormsPageClient> page = new ReadClient<>();
        try {
            return this.readWithRecovery(page, Arrays.asList(includeFields, definitionId, limit, offset),
                    () -> new SQLUtilWebSocketGetTableFormsPageClient(this.baseURL,
                            this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                            includeFields, definitionId, limit, offset),
                    client -> client.getTableFormsPage(parent),
                    () -> this.sqlUtilClient.getTableFormsPage(parent, includeFields, definitionId, limit, offset));
        } finally {
            page.clear();
        }
    }

    /**
     * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetTableFormsFor} as {@code Form}'s.
     */
    public List<FormListing> getTableForms(boolean includeFieldData, List<Form> formsToGetTableFormsFor) {
        if (formsToGetTableFormsFor == null) return null;
        return this.getTableForms(includeFieldData, null, formsToGetTableFormsFor.toArray(new Form[]{}));
    }

    /**
     * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
     */
    public List<FormListing> getTableForms(boolean includeFieldData, Form ... formsToGetTableFormsFor) {
        return this.getTableForms(includeFieldData, null, formsToGetTableFormsFor);
    }

    /**
     * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formDefFilter The filter for form definitions.
     * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
     */
    public List<FormListing> getTableForms(boolean includeFieldData, Long formDefFilter, List<Form> formsToGetTableFormsFor) {
        if (formsToGetTableFormsFor == null) return null;
        return this.getTableForms(includeFieldData, formDefFilter, formsToGetTableFormsFor.toArray(new Form[]{}));
    }

    /**
     * Retrieves all the Table (Forms) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formDefFilter The filter for form definitions.
     * @param formsToGetTableFormsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetDescForParam} Descendants as {@code Form}'s.
     */
    public List<FormListing> getTableForms(
            boolean includeFieldData,
            Long formDefFilter,
            Form ... formsToGetTableFormsFor
    ) {
        if (formsToGetTableFormsFor == null || formsToGetTableFormsFor.length == 0) return null;
        Form[] forms = minimalForms(formsToGetTableFormsFor);
        WebSocketClient.Mode socketMode = readSocketMode();
        return this.readWithRecovery(this.getTableFormsClient,
                Arrays.asList(includeFieldData, formDefFilter, COMPRESS_RSP, COMPRESS_RSP_CHARSET, socketMode),
                () -> new SQLUtilWebSocketGetTableFormsClient(this.baseURL, null,
                        this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                        includeFieldData, formDefFilter, COMPRESS_RSP, COMPRESS_RSP_CHARSET, socketMode),
                client -> client.getTableFormsSynchronized(forms),
                () -> {
                    List<FormListing> result = new ArrayList<>();
                    for (Form form : forms) {
                        result.add(listing(this.sqlUtilClient.getTableForms(form,
                                includeFieldData, Optional.ofNullable(formDefFilter))));
                    }
                    return result;
                });
    }

    /**
     * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
     *
     * @param includeTableFieldData Should Table Record Field data be included?
     * @param formsToGetFieldsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetFieldsForParam} Fields as {@code Field}'s.
     */
    public List<FormFieldListing> getFormFields(boolean includeTableFieldData, List<Form> formsToGetFieldsFor) {
        if (formsToGetFieldsFor == null) return null;
        return this.getFormFields(includeTableFieldData, formsToGetFieldsFor.toArray(new Form[]{}));
    }

    /**
     * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
     *
     * @param includeTableFieldData Should Table Record Field data be included?
     * @param formsToGetFieldsFor The Fluid Form to get Descendants for.
     *
     * @return The {@code formsToGetFieldsForParam} Fields as {@code Field}'s.
     */
    public List<FormFieldListing> getFormFields(boolean includeTableFieldData, Form ... formsToGetFieldsFor) {
        if (formsToGetFieldsFor == null || formsToGetFieldsFor.length == 0) return null;
        Form[] forms = minimalForms(formsToGetFieldsFor);
        WebSocketClient.Mode socketMode = readSocketMode();
        return this.readWithRecovery(this.getFormFieldsClient,
                Arrays.asList(includeTableFieldData, COMPRESS_RSP, COMPRESS_RSP_CHARSET, socketMode),
                () -> new SQLUtilWebSocketGetFormFieldsClient(this.baseURL, null,
                        this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                        includeTableFieldData, COMPRESS_RSP, COMPRESS_RSP_CHARSET, socketMode),
                client -> client.getFormFieldsSynchronized(forms),
                () -> {
                    List<FormFieldListing> result = new ArrayList<>();
                    for (Form form : forms) {
                        List<Field> fields = this.sqlUtilClient.getFormFields(form, includeTableFieldData);
                        FormFieldListing entry = new FormFieldListing();
                        entry.setEcho(form.getEcho());
                        entry.setListing(fields);
                        entry.setListingCount(fields == null ? 0 : fields.size());
                        result.add(entry);
                    }
                    return result;
                });
    }

    /**
     * Retrieves all the (FormHistoricData) for the {@code formsToGetFieldsFor}.
     *
     * @param includeCurrent Include the current field values.
     * @param labelFieldName Make use of label field names.
     * @param formsToGetHistoryFor The Fluid Form to get History for.
     *
     * @return The {@code formsToGetHistoryFor} Historic Data as {@code FormHistoricData}'s.
     */
    public List<FormHistoricDataListing> getFormHistoryByForm(
            boolean includeCurrent,
            boolean labelFieldName,
            List<Form> formsToGetHistoryFor
    ) {
        if (formsToGetHistoryFor == null) return null;
        return this.getFormHistoryByForm(includeCurrent, labelFieldName, formsToGetHistoryFor.toArray(new Form[]{}));
    }

    /**
     * Retrieves all the (FormHistoricData) for the {@code formsToGetFieldsFor}.
     *
     * @param includeCurrent Include the current field values.
     * @param labelFieldName Make use of label field names.
     * @param formsToGetHistoryFor The Fluid Form to get History for.
     *
     * @return The {@code formsToGetHistoryFor} Historic Data as {@code FormHistoricData}'s.
     */
    public List<FormHistoricDataListing> getFormHistoryByForm(
            boolean includeCurrent,
            boolean labelFieldName,
            Form ... formsToGetHistoryFor
    ) {
        if (formsToGetHistoryFor == null || formsToGetHistoryFor.length == 0) return null;
        Form[] forms = minimalForms(formsToGetHistoryFor);
        WebSocketClient.Mode socketMode = readSocketMode();
        return this.readWithRecovery(this.getFormHistoryByFormClient,
                Arrays.asList(includeCurrent, labelFieldName, socketMode),
                () -> new WebSocketGetFormHistoryByFormClient(this.baseURL, null,
                        this.loggedInUser.getServiceTicketAsHexUpper(), this.timeoutMillis,
                        includeCurrent, labelFieldName, socketMode),
                client -> client.getByFormSynchronized(forms),
                () -> {
                    List<FormHistoricDataListing> result = new ArrayList<>();
                    for (Form form : forms) {
                        List<FormHistoricData> values = this.fcClient.getFormAndFieldHistoricData(
                                form, includeCurrent, labelFieldName);
                        FormHistoricDataListing entry = new FormHistoricDataListing();
                        entry.setListing(values);
                        entry.setListingCount(values == null ? 0 : values.size());
                        result.add(entry);
                    }
                    return result;
                });
    }

    /**
     * Executes all the sql queries {@code nativeSQLQueriesParam}.
     *
     * @param nativeSQLQueries The queries to execute.
     *
     * @return Each fo the ResultSets for {@code nativeSQLQueriesParam}.
     *
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow
     */
    public List<SQLResultSet> executeNativeSQL(NativeSQLQuery ... nativeSQLQueries) {
        //NATIVE SQL QUERIES...
        try {
            //When mode is null or [WebSocketActive]...
            this.initGetNativeSQLClient();
        } catch (FluidClientException clientExcept) {
            if (clientExcept.getErrorCode() !=
                    FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR) throw clientExcept;
            this.mode = Mode.RESTfulActive;
        }

        if (nativeSQLQueries == null || nativeSQLQueries.length < 1) return null;

        if (this.sqlUtilWebSocketExecNativeClient != null) {
            return this.sqlUtilWebSocketExecNativeClient.executeNativeSQLSynchronized(nativeSQLQueries);
        } else {
            List<SQLResultSet> returnVal = new ArrayList<>();
            for (NativeSQLQuery sqlToExec : nativeSQLQueries) {
                SQLResultSet resultSet = this.sqlUtilClient.executeSQL(sqlToExec);
                returnVal.add(resultSet);
            }
            return returnVal;
        }
    }

    /**
     * Executes all the sql queries {@code nativeSQLQueriesParam}.
     *
     * @param nativeSQLQueries The queries to execute.
     *
     * @return Each fo the ResultSets for {@code nativeSQLQueriesParam}.
     *
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLColumn
     * @see com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLRow
     */
    public List<SQLResultSet> executeNativeSQLUpdate(NativeSQLQuery ... nativeSQLQueries) {
        List<SQLResultSet> returnVal = new ArrayList<>();
        for (NativeSQLQuery sqlToExec : nativeSQLQueries) {
            SQLResultSet resultSet = this.sqlUtilClient.executeSQLUpdate(sqlToExec);
            returnVal.add(resultSet);
        }
        return returnVal;
    }


    /**
     * Init and retrieve {@code SQLUtilWebSocketExecuteNativeSQLClient}.
     * @return local instance of {@code SQLUtilWebSocketExecuteNativeSQLClient}
     * @see SQLUtilWebSocketExecuteNativeSQLClient
     */
    public SQLUtilWebSocketExecuteNativeSQLClient initGetNativeSQLClient() {
        this.clearClientsIfRest();

        if (this.sqlUtilWebSocketExecNativeClient == null && Mode.RESTfulActive != this.mode) {
            this.sqlUtilWebSocketExecNativeClient = new SQLUtilWebSocketExecuteNativeSQLClient(
                    this.baseURL,
                    null,
                    this.loggedInUser.getServiceTicketAsHexUpper(),
                    this.timeoutMillis,
                    COMPRESS_RSP,
                    COMPRESS_RSP_CHARSET
            );
            this.mode = Mode.WebSocketActive;
        }
        return this.sqlUtilWebSocketExecNativeClient;
    }

    /**
     * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formsToPopulateFormFieldsFor The Fluid Form to get Descendants for.
     */
    public void massPopulateFormFields(boolean includeFieldData, List<Form> formsToPopulateFormFieldsFor) {
        if (formsToPopulateFormFieldsFor == null) return;
        this.massPopulateFormFields(includeFieldData, formsToPopulateFormFieldsFor.toArray(new Form[]{}));
    }

    /**
     * Retrieves all the (Fields) for the {@code formsToGetDescForParam}.
     *
     * @param includeFieldData Should Field data be included?
     * @param formsToPopulateFormFieldsFor The Fluid Form to get Descendants for.
     */
    public void massPopulateFormFields(boolean includeFieldData, Form ... formsToPopulateFormFieldsFor) {
        //Nothing to do...
        if (formsToPopulateFormFieldsFor == null || formsToPopulateFormFieldsFor.length < 1) return;

        //Populate a known echo for all of the local form caches...
        Form[] formsToFetchForLocalCacheArr = new Form[formsToPopulateFormFieldsFor.length];
        for (int index = 0; index < formsToFetchForLocalCacheArr.length; index++) {
            formsToFetchForLocalCacheArr[index] = new Form(formsToPopulateFormFieldsFor[index].getId());
            formsToFetchForLocalCacheArr[index].setEcho(UtilGlobal.randomUUID());
        }

        List<FormFieldListing> listingReturnFieldValsPopulated =
                this.getFormFields(includeFieldData, formsToFetchForLocalCacheArr);

        // Populate each of the form from the param:
        for (Form formToSetFieldsOn : formsToPopulateFormFieldsFor) {
            formToSetFieldsOn.setFormFields(
                    this.getFieldValuesForFormFromCache(
                            formToSetFieldsOn.getId(),
                            listingReturnFieldValsPopulated,
                            formsToFetchForLocalCacheArr
                    )
            );
        }
    }

    /**
     * Populate the field values from the cache.
     *
     * @param formId The id of the form to populate.
     * @param listingReturnFieldsPopulated The cache of field values.
     * @param formsToFetchForLocalCacheArr The forms that contain the ids and echos.
     *
     * @return The field values for form with id {@code formIdParam}.
     */
    private List<Field> getFieldValuesForFormFromCache(
            Long formId,
            List<FormFieldListing> listingReturnFieldsPopulated,
            Form[] formsToFetchForLocalCacheArr
    ) {
        if (formId == null || formId.longValue() < 1) return null;
        if (listingReturnFieldsPopulated == null || listingReturnFieldsPopulated.isEmpty()) return null;
        if (formsToFetchForLocalCacheArr == null || formsToFetchForLocalCacheArr.length == 0) return null;

        for (Form formIter : formsToFetchForLocalCacheArr) {
            //Form is a match...
            if (formId.equals(formIter.getId())) {
                String echoToUse = formIter.getEcho();
                for (FormFieldListing fieldListing : listingReturnFieldsPopulated) {
                    if (echoToUse.equals(fieldListing.getEcho())) return fieldListing.getListing();
                }
            }
        }
        return null;
    }

    /** One lock per read endpoint protects replacement without serializing unrelated reads. */
    private static final class ReadClient<C extends ABaseClientWebSocket<?, ?>> {
        private C client;
        private List<?> options;
        private boolean restOnly;

        private void discard() {
            if (client != null) client.closeAndClean();
            client = null;
            options = null;
        }

        private synchronized void clear() {
            discard();
            restOnly = false;
        }
    }

    /** Recovery is used only by form-data reads, never by native SQL or writes. */
    private <C extends ABaseClientWebSocket<?, ?>, T> T readWithRecovery(
            ReadClient<C> cached, List<?> options, Supplier<C> connect,
            Function<C, T> read, Supplier<T> rest) {
        synchronized (cached) {
            if (DISABLE_WS || this.mode == Mode.RESTfulActive || cached.restOnly) {
                cached.discard();
                return rest.get();
            }
            FluidClientException transportFailure = null;
            for (int attempt = 0; attempt < 2; attempt++) {
                try {
                    if (cached.client != null && (!cached.client.isConnectionValid()
                            || !options.equals(cached.options))) cached.discard();
                    if (cached.client == null) {
                        cached.client = connect.get();
                        cached.options = options;
                    }
                    return read.apply(cached.client);
                } catch (FluidClientException failure) {
                    if (Thread.currentThread().isInterrupted() || !isTransportFailure(failure, cached.client)) {
                        throw failure;
                    }
                    transportFailure = failure;
                    cached.discard();
                }
            }
            // Avoid repeatedly reconnecting to an unavailable endpoint for this client's lifetime.
            cached.restOnly = true;
            try {
                return rest.get();
            } catch (RuntimeException failure) {
                failure.addSuppressed(transportFailure);
                throw failure;
            }
        }
    }

    private static boolean isTransportFailure(FluidClientException failure, ABaseClientWebSocket<?, ?> client) {
        int code = failure.getErrorCode();
        return code == FluidClientException.ErrorCode.WEB_SOCKET_IO_ERROR
                || code == FluidClientException.ErrorCode.WEB_SOCKET_DEPLOY_ERROR
                || (code == FluidClientException.ErrorCode.IO_ERROR
                    && client != null && !client.isConnectionValid());
    }

    private static WebSocketClient.Mode readSocketMode() {
        return IS_MODE_WS_BINARY ? WebSocketClient.Mode.Binary : WebSocketClient.Mode.Text;
    }

    private static Form[] minimalForms(Form[] forms) {
        Form[] result = new Form[forms.length];
        for (int index = 0; index < forms.length; index++) {
            result[index] = new Form(forms[index].getId());
            result[index].setEcho(forms[index].getEcho());
        }
        return result;
    }

    private static FormListing listing(List<Form> forms) {
        FormListing result = new FormListing();
        result.setListing(forms);
        result.setListingCount(forms == null ? 0 : forms.size());
        return result;
    }

    /**
     * When mode is disabled WebSockets, the clients will be closed and cleared ({@code null}).
     */
    private void clearClientsIfRest() {
        if (DISABLE_WS && this.mode != Mode.RESTfulActive) {
            this.closeAndClean();
            this.mode = Mode.RESTfulActive;
        }

        this.wsClientMode = WebSocketClient.Mode.Text;
        if (IS_MODE_WS_BINARY) this.wsClientMode = WebSocketClient.Mode.Binary;
    }

    /**
     * Close any clients used during lifetime of {@code this} object.
     */
    public void closeAndClean() {
        if (this.sqlUtilClient != null) this.sqlUtilClient.closeAndClean();
        if (this.fcClient != null) this.fcClient.closeAndClean();

        // WebSocket clients:
        this.getAncestorClient.clear();
        this.getDescendantsClient.clear();
        this.getTableFormsClient.clear();
        this.getFormFieldsClient.clear();
        if (this.sqlUtilWebSocketExecNativeClient != null) this.sqlUtilWebSocketExecNativeClient.closeAndClean();
        this.sqlUtilWebSocketExecNativeClient = null;
        this.getFormHistoryByFormClient.clear();
    }

    /**
     * @see Closeable#close()
     */
    @Override
    public void close() {
        this.closeAndClean();
    }
}
