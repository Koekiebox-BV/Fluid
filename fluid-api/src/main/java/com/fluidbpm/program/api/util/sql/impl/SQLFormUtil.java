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

package com.fluidbpm.program.api.util.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fluidbpm.program.api.util.IFormAction;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.exception.FluidSQLException;
import com.fluidbpm.program.api.util.sql.syntax.ISyntax;
import com.fluidbpm.program.api.util.sql.syntax.SyntaxFactory;
import com.fluidbpm.program.api.vo.Field;
import com.fluidbpm.program.api.vo.Form;

/**
 * SQL Utility class used for {@code Form} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseSQLUtil
 * @see Form
 * @see IFormAction
 */
public class SQLFormUtil extends ABaseSQLUtil implements IFormAction {

    private SQLFormDefinitionUtil formDefUtil = null;
    private SQLFormFieldUtil fieldUtil = null;

    /**
     * New instance using provided {@code connectionParam}.
     *
     * @param connectionParam SQL Connection to use.
     * @param cacheUtilParam The Cache Util for better performance.
     */
    public SQLFormUtil(Connection connectionParam, CacheUtil cacheUtilParam) {
        super(connectionParam, cacheUtilParam);

        this.formDefUtil = new SQLFormDefinitionUtil(connectionParam);
        this.fieldUtil = new SQLFormFieldUtil(connectionParam, cacheUtilParam);
    }

    /**
     * New instance using provided {@code connectionParam}.
     *
     * @param connectionParam SQL Connection to use.
     */
    public SQLFormUtil(Connection connectionParam) {
        this(connectionParam, null);
    }

    /**
     * Retrieves the Table field records as {@code List<Form>}.
     *
     * @param electronicFormIdParam The Form Identifier.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
     * @return {@code List<Form>} records.
     */
    public List<Form> getFormTableForms(
            Long electronicFormIdParam,
            boolean includeFieldDataParam)
    {
        List<Form> returnVal = new ArrayList();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        Map<Long,String> definitionAndTitle =
                this.formDefUtil.getFormDefinitionIdAndTitle();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersTableFieldFormContainers);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1, electronicFormIdParam);

            resultSet = preparedStatement.executeQuery();

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
                for(Form form : returnVal)
                {
                    List<Field> formFields = this.fieldUtil.getFormFields(
                                    form.getId(),
                                    false);
                    form.setFormFields(formFields);
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
     * Gets the descendants for the {@code electronicFormIdsParam} Forms.
     *
     * @param electronicFormIdsParam Identifiers for the Forms to retrieve.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code List<Form>} table fields.
     * @return {@code List<Form>} descendants.
     *
     * @see Form
     */
    @Override
    public List<Form> getFormDescendants(
            List<Long> electronicFormIdsParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {

        if(electronicFormIdsParam == null || electronicFormIdsParam.isEmpty())
        {
            return null;
        }

        List<Form> returnVal = new ArrayList();

        for(Long electronicFormId : electronicFormIdsParam)
        {
            List<Form> forTheCycle = this.getFormDescendants(
                    electronicFormId, includeFieldDataParam, includeTableFieldsParam);

            if(forTheCycle == null)
            {
                continue;
            }

            returnVal.addAll(forTheCycle);
        }

        return returnVal;
    }

    /**
     * Gets the descendants for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormIdParam Identifier for the Form.
     * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code List<Form>} table fields.
     * @return {@code List<Form>} descendants.
     *
     * @see Form
     */
    public List<Form> getFormDescendants(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        List<Form> returnVal = new ArrayList();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        Map<Long,String> definitionAndTitle =
                this.formDefUtil.getFormDefinitionIdAndTitle();

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
                for(Form form : returnVal)
                {
                    List<Field> formFields =
                            this.fieldUtil.getFormFields(
                                    form.getId(),
                                    includeTableFieldsParam);
                    form.setFormFields(formFields);
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
     * Gets the ancestor for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormIdParam Identifier for the Form.
     * @param includeFieldDataParam Whether to populate the return {@code Form} fields.
     * @param includeTableFieldsParam Whether to populate the return {@code Form} table fields.
     * @return {@code Form} descendants.
     *
     * @see Form
     */
    public Form getFormAncestor(
            Long electronicFormIdParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam)
    {
        if(electronicFormIdParam == null)
        {
            return null;
        }

        Form returnVal = null;

        Map<Long,String> definitionAndTitle =
                this.formDefUtil.getFormDefinitionIdAndTitle();

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

            //Iterate each of the form containers...
            if (resultSet.next())
            {
                returnVal = this.mapFormContainerTo(
                        definitionAndTitle,
                        resultSet);
            }

            //When field data must also be included...
            if(includeFieldDataParam && returnVal != null)
            {
                returnVal.setFormFields(
                        this.fieldUtil.getFormFields(
                                returnVal.getId(), includeTableFieldsParam));
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
     * Maps the Form to the provided Definition-Id and Title.
     *
     * @param definitionAndTitleParam Form Definition Id and Title mapping.
     * @param resultSetParam Result Set used to populate {@code Form} with.
     *
     * @return Form
     *
     * @throws SQLException If no mapping exists for Form Type.
     *
     * @see ResultSet
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

        if(created != null)
        {
            toAdd.setDateCreated(new Date(created.getTime()));
        }

        if(lastUpdated != null)
        {
            toAdd.setDateLastUpdated(new Date(lastUpdated.getTime()));
        }

        return toAdd;
    }

}
