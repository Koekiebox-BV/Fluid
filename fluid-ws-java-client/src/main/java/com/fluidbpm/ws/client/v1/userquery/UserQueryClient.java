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

package com.fluidbpm.ws.client.v1.userquery;

import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.userquery.UserQueryListing;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitUserQuery;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitUserQueryListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Java Web Service Client for {@code UserQuery} related actions.
 *
 * @author jasonbruwer
 * @see JsonObject
 * @see WS.Path.UserQuery
 * @see UserQuery
 * @since v1.0
 */
public class UserQueryClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam   The Server issued Service Ticket.
     */
    public UserQueryClient(
            String endpointBaseUrlParam,
            String serviceTicketParam
    ) {
        super(endpointBaseUrlParam);
        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code UserQuery}.
     *
     * @param userQueryParam The {@code UserQuery} to create.
     * @return The Created User Query.
     * @see com.fluidbpm.program.api.vo.userquery.UserQuery
     */
    public UserQuery createUserQuery(UserQuery userQueryParam) {
        if (userQueryParam != null) userQueryParam.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.putJson(userQueryParam, WS.Path.UserQuery.Version1.userQueryCreate()));
    }

    /**
     * Updates an existing {@code UserQuery}.
     *
     * @param userQueryParam The {@code UserQuery} to update.
     * @return The Updated UserQuery.
     * @see UserQuery
     */
    public UserQuery updateUserQuery(UserQuery userQueryParam) {
        if (userQueryParam != null && this.serviceTicket != null) userQueryParam.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.postJson(userQueryParam, WS.Path.UserQuery.Version1.userQueryUpdate()));
    }

    /**
     * Deletes the {@code UserQuery} provided.
     * Id must be set on the {@code UserQuery}.
     *
     * @param userQueryToDeleteParam The UserQuery to Delete.
     * @return The deleted UserQuery.
     */
    public UserQuery deleteUserQuery(
            UserQuery userQueryToDeleteParam
    ) {
        if (userQueryToDeleteParam != null) userQueryToDeleteParam.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.postJson(userQueryToDeleteParam, WS.Path.UserQuery.Version1.userQueryDelete()));
    }

    /**
     * Deletes the {@code UserQuery} provided.
     * Id must be set on the {@code UserQuery}.
     *
     * @param userQueryToDeleteParam The UserQuery to Delete.
     * @param forcefullyDeleteParam  Delete the UserQuery forcefully.
     * @return The deleted UserQuery.
     */
    public UserQuery deleteUserQuery(
            UserQuery userQueryToDeleteParam,
            boolean forcefullyDeleteParam
    ) {
        if (userQueryToDeleteParam != null) userQueryToDeleteParam.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.postJson(userQueryToDeleteParam,
                WS.Path.UserQuery.Version1.userQueryDelete(forcefullyDeleteParam)));
    }

    /**
     * Retrieves User Query information for the provided {@code userQueryIdParam}.
     *
     * @param userQueryIdParam The ID of the {@code UserQuery} to retrieve info for.
     * @return UserQuery information.
     * @see UserQuery
     */
    public UserQuery getUserQueryById(Long userQueryIdParam) {
        UserQuery userQueryToGetInfoFor = new UserQuery();
        userQueryToGetInfoFor.setId(userQueryIdParam);
        userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.postJson(userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getById()));
    }

    /**
     * Retrieves User Query information for the provided {@code userQueryNameParam}.
     *
     * @param userQueryName The Name of the {@code UserQuery} to retrieve info for.
     * @return UserQuery information.
     * @see UserQuery
     */
    public UserQuery getUserQueryByName(String userQueryName) {
        UserQuery userQueryToGetInfoFor = new UserQuery();
        userQueryToGetInfoFor.setName(userQueryName);
        userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new UserQuery(this.postJson(userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getById()));
    }

    /**
     * Retrieves all user query information.
     *
     * @return UserQuery information.
     * @see List<UserQuery>
     */
    public List<UserQuery> getAllUserQueries() {
        UserQuery userQueryToGetInfoFor = new UserQuery();
        userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new UserQueryListing(this.postJson(userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getAllUserQueries()))
                .getListing();
    }

    /**
     * Retrieve the User Query WebKit configuration.
     *
     * @return The complete user query config.
     * @see WebKitUserQueryListing
     * @see List<WebKitUserQuery>
     */
    public List<WebKitUserQuery> getUserQueryWebKit() {
        UserQuery userQuery = new UserQuery();
        userQuery.setServiceTicket(this.serviceTicket);
        return new WebKitUserQueryListing(
                this.postJson(userQuery, WS.Path.UserQuery.Version1.getAllUserQueriesWebKit())
        ).getListing();
    }

    /**
     * Update and insert the Flow View Group configuration.
     *
     * @param listing The ViewGroupWebKit listing to upsert.
     * @return The complete view group config.
     * @see WebKitUserQueryListing
     * @see List<WebKitUserQuery>
     */
    public List<WebKitUserQuery> upsertUserQueryWebKit(WebKitUserQueryListing listing) {
        if (listing == null) return null;
        listing.setServiceTicket(this.serviceTicket);
        return new WebKitUserQueryListing(
                this.postJson(listing, WS.Path.UserQuery.Version1.userQueryWebKitUpsert())
        ).getListing();
    }

    /**
     * Retrieves all user queries for logged in {@code User}.
     *
     * @return UserQuery information.
     * @see UserQueryListing
     */
    public List<UserQuery> getAllUserQueriesByLoggedInUser() {
        UserQuery userQueryToGetInfoFor = new UserQuery();
        userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);

        return new UserQueryListing(this.postJson(
                userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getAllUserQueriesByLoggedInUser())
        ).getListing();
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam The UserQuery to execute.
     * @return The UserQuery result.
     * @see FluidItemListing
     * @see List<FluidItem>
     */
    public List<FluidItem> executeUserQuery(UserQuery queryToExecuteParam) {
        return this.executeUserQuery(queryToExecuteParam, true);
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam     The UserQuery to execute.
     * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
     * @return The UserQuery result.
     * @see FluidItemListing
     */
    public List<FluidItem> executeUserQuery(
            UserQuery queryToExecuteParam,
            boolean populateAncestorIdParam
    ) {
        return this.executeUserQuery(
                queryToExecuteParam,
                populateAncestorIdParam,
                -1,
                -1,
                false
        ).getListing();
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam     The UserQuery to execute.
     * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
     * @param queryLimitParam         The query limit.
     * @param offsetParam             The query offset.
     * @return The UserQuery result.
     * @see FluidItemListing
     */
    public List<FluidItem> executeUserQuery(
            UserQuery queryToExecuteParam,
            boolean populateAncestorIdParam,
            int queryLimitParam,
            int offsetParam
    ) {
        return this.executeUserQuery(
                queryToExecuteParam,
                populateAncestorIdParam,
                queryLimitParam,
                offsetParam,
                false
        ).getListing();
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam     The UserQuery to execute.
     * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
     * @param queryLimitParam         The query limit.
     * @param offsetParam             The query offset.
     * @param forceUseDatabaseParam   Force to use underlying database.
     * @return The UserQuery result.
     * @see FluidItemListing
     */
    public FluidItemListing executeUserQuery(
            UserQuery queryToExecuteParam,
            boolean populateAncestorIdParam,
            int queryLimitParam,
            int offsetParam,
            boolean forceUseDatabaseParam
    ) {
        if (queryToExecuteParam != null) queryToExecuteParam.setServiceTicket(this.serviceTicket);

        return new FluidItemListing(this.postJson(
                queryToExecuteParam, WS.Path.UserQuery.Version1.executeUserQuery(
                        populateAncestorIdParam,
                        forceUseDatabaseParam,
                        queryLimitParam,
                        offsetParam))
        );
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam     The UserQuery to execute.
     * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
     * @param executeCalculatedLabels Execute the calculate labels.
     * @param queryLimitParam         The query limit.
     * @param offsetParam             The query offset.
     * @param forceUseDatabaseParam   Force to use underlying database.
     * @return The UserQuery result.
     * @see FluidItemListing
     * @see List<FluidItem>
     */
    public List<FluidItem> executeUserQuery(
            UserQuery queryToExecuteParam,
            boolean populateAncestorIdParam,
            boolean executeCalculatedLabels,
            int queryLimitParam,
            int offsetParam,
            boolean forceUseDatabaseParam
    ) {
        if (queryToExecuteParam != null) queryToExecuteParam.setServiceTicket(this.serviceTicket);

        return new FluidItemListing(this.postJson(
                queryToExecuteParam, WS.Path.UserQuery.Version1.executeUserQuery(
                        populateAncestorIdParam,
                        forceUseDatabaseParam,
                        executeCalculatedLabels,
                        queryLimitParam,
                        offsetParam))
        ).getListing();
    }
}
