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

import com.fluidbpm.program.api.util.sql.ABaseSQLUtil.SQLServerType;
import com.fluidbpm.program.api.util.sql.syntax.SyntaxFactory;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.NativeSQLQuery;

/** Native SQL tests must select the query/procedure path for the target database. */
final class SQLUtilTestQueries {
    private SQLUtilTestQueries() { }

    static NativeSQLQuery routine(String alias) {
        SQLServerType type = SQLServerType.valueOf(
                System.getProperty("fluid.test.sqlServerType", "MySQL"));
        return routine(type, alias);
    }

    static NativeSQLQuery routine(SQLServerType type, String alias) {
        if (type == null || type == SQLServerType.Unknown) {
            throw new IllegalArgumentException("A supported SQL server type is required.");
        }
        String sql = SyntaxFactory.getInstance().getSyntaxFor(type, alias).getPreparedStatement();
        NativeSQLQuery query = new NativeSQLQuery();
        if (type == SQLServerType.PostgreSQL) {
            query.setQuery(sql);
        } else {
            query.setStoredProcedure(sql);
        }
        return query;
    }
}
