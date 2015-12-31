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

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class SQLFormDefinitionUtil extends ABaseSQLUtil {

    /**
     *
     * @param connectionParam
     */
    public SQLFormDefinitionUtil(Connection connectionParam) {
        super(connectionParam);
    }


    /**
     *
     * @return
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
