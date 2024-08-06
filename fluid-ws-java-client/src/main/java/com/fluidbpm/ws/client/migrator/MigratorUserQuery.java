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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.userquery.UserQueryClient;
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
        try {
            List<Field> inputs = new ArrayList<>();
            List<String> rules = new ArrayList<>();
            if (opts.userQueryResultFields != null) {
                for (String input : opts.userQueryResultFields) inputs.add(new Field(input));
            }
            if (opts.userQueryRules != null) rules.addAll(Arrays.asList(opts.userQueryRules));

            UserQuery toCreate = new UserQuery(opts.userQueryName, inputs);
            toCreate.setDescription(opts.userQueryDescription);
            toCreate.setRules(rules);
            uqc.createUserQuery(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
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
