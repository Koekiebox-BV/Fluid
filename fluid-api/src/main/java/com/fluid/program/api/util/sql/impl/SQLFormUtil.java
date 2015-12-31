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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.exception.FluidSQLException;
import com.fluid.program.api.util.sql.syntax.ISyntax;
import com.fluid.program.api.util.sql.syntax.SyntaxFactory;
import com.fluid.program.api.vo.Field;
import com.fluid.program.api.vo.Form;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class SQLFormUtil extends ABaseSQLUtil {

    /**
     *
     * @param connectionParam
     */
    public SQLFormUtil(Connection connectionParam) {
        super(connectionParam);
    }

    /**
     *
     * @param electronicFormIdParam
     * @param includeFieldDataParam
     * @return
     */
    public List<Form> getFormTableForms(
            Long electronicFormIdParam,
            boolean includeFieldDataParam)
    {
        List<Form> returnVal = new ArrayList<Form>();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        SQLFormDefinitionUtil formDefUtl = new SQLFormDefinitionUtil(this.getConnection());

        Map<Long,String> definitionAndTitle =
                formDefUtl.getFormDefinitionIdAndTitle();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersTableFieldFormContainers);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1,electronicFormIdParam);

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            //Iterate each of the form containers...
            while (resultSet.next())
            {
                returnVal.add(this.mapFormContainerTo(
                        definitionAndTitle,
                        resultSet));
            }

            //When field data must also be included...
            if(includeFieldDataParam)
            {
                SQLFormFieldUtil fieldUtil = new SQLFormFieldUtil(this.getConnection());

                if(returnVal != null)
                {
                    for(Form form : returnVal)
                    {
                        List<Field> formFields =
                                fieldUtil.getFormFields(
                                        form.getId(),
                                        false);
                        form.setFormFields(formFields);
                    }
                }
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

    /**
     *
     * @param electronicFormIdParam
     * @param includeFieldDataParam
     * @param includeTableFieldsParam
     * @return
     */
    public List<Form> getFormDescendants(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        List<Form> returnVal = new ArrayList<Form>();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        SQLFormDefinitionUtil formDefUtl = new SQLFormDefinitionUtil(this.getConnection());

        Map<Long,String> definitionAndTitle =
                formDefUtl.getFormDefinitionIdAndTitle();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersChildFormContainers);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1,electronicFormIdParam);

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            //Iterate each of the form containers...
            while (resultSet.next())
            {
                returnVal.add(this.mapFormContainerTo(
                        definitionAndTitle,
                        resultSet));
            }

            //When field data must also be included...
            if(includeFieldDataParam)
            {
                SQLFormFieldUtil fieldUtil = new SQLFormFieldUtil(this.getConnection());

                if(returnVal != null)
                {
                    for(Form form : returnVal)
                    {
                        List<Field> formFields =
                                fieldUtil.getFormFields(
                                        form.getId(),
                                        includeTableFieldsParam);
                        form.setFormFields(formFields);
                    }
                }
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

    /**
     *
     * @param electronicFormIdParam
     * @param includeFieldDataParam
     * @param includeTableFieldsParam
     * @return
     */
    public Form getFormAncestor(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        Form returnVal = null;

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        SQLFormDefinitionUtil formDefUtl = new SQLFormDefinitionUtil(this.getConnection());

        Map<Long,String> definitionAndTitle =
                formDefUtl.getFormDefinitionIdAndTitle();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersParentFormContainer);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1,electronicFormIdParam);

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            //Iterate each of the form containers...
            if (resultSet.next())
            {
                returnVal = this.mapFormContainerTo(
                        definitionAndTitle,
                        resultSet);
            }

            //When field data must also be included...
            if(includeFieldDataParam)
            {
                SQLFormFieldUtil fieldUtil = new SQLFormFieldUtil(this.getConnection());

                if(returnVal != null)
                {
                    List<Field> formFields =
                            fieldUtil.getFormFields(
                                    returnVal.getId(),
                                    includeTableFieldsParam);
                    returnVal.setFormFields(formFields);
                }
            }

            return returnVal;
        }
        //
        catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        }
        //
        finally {
            this.closeStatement(preparedStatement,resultSet);
        }
    }

    /**
     *
     * @param definitionAndTitleParam
     * @param resultSetParam
     * @return
     * @throws SQLException
     */
    private Form mapFormContainerTo(
            Map<Long,String> definitionAndTitleParam,
            ResultSet resultSetParam)
    throws SQLException
    {
        Long formId = resultSetParam.getLong(1);
        String formType = definitionAndTitleParam.get(
                resultSetParam.getLong(2));
        String title = resultSetParam.getString(3);
        Date created = resultSetParam.getDate(4);
        Date lastUpdated = resultSetParam.getDate(5);

        if(formType == null)
        {
            throw new SQLException("No mapping found for Form Type '"+
                    resultSetParam.getLong(1)+"'.");
        }

        Form toAdd = new Form(formType);
        toAdd.setId(formId);
        toAdd.setTitle(title);
        toAdd.setDateCreated(created);
        toAdd.setDateLastUpdated(lastUpdated);

        return toAdd;
    }

}
