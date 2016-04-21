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
package com.fluid.program.api.util.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.exception.FluidSQLException;
import com.fluid.program.api.util.sql.syntax.ISyntax;
import com.fluid.program.api.util.sql.syntax.SyntaxFactory;
import com.fluid.program.api.vo.Form;

/**
 * SQL Utility class used for {@code Form} Definition related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseSQLUtil
 * @see Form
 */
public class SQLFormDefinitionUtil extends ABaseSQLUtil {

    /**
     * New FormDefinition util instance using {@code connectionParam}.
     *
     * @param connectionParam SQL Connection to use for Fields.
     */
    public SQLFormDefinitionUtil(Connection connectionParam) {
        super(connectionParam);
    }


    /**
     * Retrieves the Form Definition and Title mapping
     * currently stored in Fluid.
     *
     * @return Form Definition Id and Title.
     */
    public Map<Long,String> getFormDefinitionIdAndTitle()
    {
        Map<Long,String> returnVal = new HashMap<Long,String>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.FormDefinition.GetFormDefinitions);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            //Iterate each of the form containers...
            while (resultSet.next())
            {
                Long id = resultSet.getLong(1);
                String title = resultSet.getString(2);

                returnVal.put(id,title);
            }
        }
        //
        catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        }
        //
        finally {
            this.closeStatement(preparedStatement,resultSet);
        }

        return returnVal;
    }
}
