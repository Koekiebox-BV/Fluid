/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox B.V.
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

package com.fluidbpm.ws.client.migrator;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitUserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
import com.google.gson.JsonObject;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Migration class for user query related migrations.
 * @see UserQuery
 * @see UserQueryClient
 */
public class MigratorUserQuery {

    @Builder
    public static final class MigrateOptUserQuery {
        private String userQueryName;
        private String userQueryDescription;
        private String[] userQueryResultFields;
        private String[] userQueryRules;

        private boolean allowWebKitUpdate;
        private WebKitUserQuery webKitUserQuery;
    }

    @Builder
    public static final class MigrateOptRemoveUserQuery {
        private Long userQueryId;
        private String userQueryName;
    }

    /**Migrate a user query.
     * @param uqc {@code UserQueryClient}
     * @param opts {@code OptMigrateUserQuery}
     */
    public static void migrateUserQuery(
            UserQueryClient uqc, MigrateOptUserQuery opts
    ) {
        boolean isCreate = false;
        UserQuery toCreate = new UserQuery(opts.userQueryName);
        try {
            List<Field> inputs = new ArrayList<>();
            List<String> rules = new ArrayList<>();
            if (opts.userQueryResultFields != null) {
                for (String input : opts.userQueryResultFields) inputs.add(new Field(input));
            }
            if (opts.userQueryRules != null) rules.addAll(Arrays.asList(opts.userQueryRules));

            toCreate.setInputs(inputs);
            toCreate.setDescription(opts.userQueryDescription);
            toCreate.setRules(rules);
            uqc.createUserQuery(toCreate);
            isCreate = true;
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }

        if (opts.webKitUserQuery == null) return;
        if (!isCreate && !opts.allowWebKitUpdate) return;

        // WebKit is set, and updates are allowed, or this is a create:
        WebKitUserQuery existingWk = uqc.getUserQueryWebKit(toCreate.getName(), toCreate.getId());
        JsonObject existingJsonObj = existingWk.toJsonObject();
        JsonObject newJsonObj = opts.webKitUserQuery.toJsonObject();

        // Copy all the new fields:
        UtilGlobal.copyJSONFullMerge(newJsonObj, existingJsonObj);

        UserQuery newForm = new UserQuery(toCreate.getId());
        newForm.setName(toCreate.getName());
        newForm.setDescription(toCreate.getDescription());

        uqc.upsertUserQueryWebKit(new WebKitUserQuery(existingJsonObj, newForm));
    }

    /**Remove a user query.
     * @param uqc {@code UserQueryClient}
     * @param opts {@code MigrateOptRemoveUserQuery}
     */
    public static void removeUserQuery(
            UserQueryClient uqc, MigrateOptRemoveUserQuery opts
    ) {
        UserQuery toDelete = new UserQuery(opts.userQueryId, opts.userQueryName);
        uqc.deleteUserQuery(toDelete, true);
    }
}
