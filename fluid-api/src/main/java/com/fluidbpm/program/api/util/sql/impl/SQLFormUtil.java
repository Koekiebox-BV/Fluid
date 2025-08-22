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

import com.fluidbpm.program.api.util.IFormAction;
import com.fluidbpm.program.api.util.cache.CacheUtil;
import com.fluidbpm.program.api.util.sql.ABaseSQLUtil;
import com.fluidbpm.program.api.util.sql.exception.FluidSQLException;
import com.fluidbpm.program.api.util.sql.syntax.ISyntax;
import com.fluidbpm.program.api.util.sql.syntax.SyntaxFactory;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SQL Utility class used for {@code Form} related actions.
 *
 * @author jasonbruwer
 * @see ABaseSQLUtil
 * @see Form
 * @see IFormAction
 * @since v1.0
 */
public class SQLFormUtil extends ABaseSQLUtil implements IFormAction {

    private SQLFormDefinitionUtil formDefUtil = null;
    private SQLFormFieldUtil fieldUtil = null;

    /**
     * Contains the SQL column indexes for {@code Form}.
     */
    public static class SQLColumnIndex {
        public static final int _01_FORM_ID = 1;
        public static final int _02_FORM_TYPE = 2;
        public static final int _03_TITLE = 3;
        public static final int _04_CREATED = 4;
        public static final int _05_LAST_UPDATED = 5;
        public static final int _06_CURRENT_USER_ID = 6;

        //additional...
        public static final int _07_FORM_CONTAINER_STATE = 7;
        public static final int _08_FORM_CONTAINER_FLOW_STATE = 8;
    }

    /**
     * New instance using provided {@code connectionParam}.
     *
     * @param connectionParam SQL Connection to use.
     * @param cacheUtilParam  The Cache Util for better performance.
     */
    public SQLFormUtil(Connection connectionParam, CacheUtil cacheUtilParam) {
        super(connectionParam, cacheUtilParam);

        this.formDefUtil = new SQLFormDefinitionUtil(connectionParam);
        this.fieldUtil = new SQLFormFieldUtil(connectionParam, cacheUtilParam, this.formDefUtil);
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
     * @param electronicFormId The Form Identifier.
     * @param includeFieldData Whether to populate the return {@code List<Form>} fields.
     * @return {@code List<Form>} records.
     */
    public List<Form> getFormTableForms(Long electronicFormId, boolean includeFieldData) {
        List<Form> returnVal = new ArrayList();
        if (electronicFormId == null) return returnVal;

        Map<Long, String> definitionAndTitle = this.formDefUtil.getFormDefinitionIdAndTitle();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersTableFieldFormContainers);

            preparedStatement = this.getConnection().prepareStatement(syntax.getPreparedStatement());
            preparedStatement.setLong(1, electronicFormId);

            resultSet = preparedStatement.executeQuery();
            //Iterate each of the form containers...
            while (resultSet.next()) returnVal.add(this.mapFormContainerTo(definitionAndTitle, resultSet));

            //When field data must also be included...
            if (includeFieldData) {
                for (Form form : returnVal) {
                    List<Field> formFields = this.fieldUtil.getFormFields(
                            form.getId(),
                            false,
                            false
                    );
                    form.setFormFields(formFields);
                }
            }
        } catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        } finally {
            this.closeStatement(preparedStatement, resultSet);
        }
        return returnVal;
    }

    /**
     * Gets the descendants for the {@code electronicFormIdsParam} Forms.
     *
     * @param electronicFormIds               Identifiers for the Forms to retrieve.
     * @param includeFieldData                Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFields              Whether to populate the return {@code List<Form>} table fields.
     * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
     * @return {@code List<Form>} descendants.
     * @see Form
     */
    @Override
    public List<Form> getFormDescendants(
            List<Long> electronicFormIds,
            boolean includeFieldData,
            boolean includeTableFields,
            boolean includeTableFieldFormRecordInfo
    ) {
        if (electronicFormIds == null || electronicFormIds.isEmpty()) return null;

        List<Form> returnVal = new ArrayList();

        for (Long electronicFormId : electronicFormIds) {
            List<Form> forTheCycle = this.getFormDescendants(
                    electronicFormId,
                    includeFieldData,
                    includeTableFields,
                    includeTableFieldFormRecordInfo
            );
            if (forTheCycle == null) continue;

            returnVal.addAll(forTheCycle);
        }
        return returnVal;
    }

    /**
     * Gets the descendants for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormId                Identifier for the Form.
     * @param includeFieldData                Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFields              Whether to populate the return {@code List<Form>} table fields.
     * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
     * @return {@code List<Form>} descendants.
     * @see Form
     */
    public List<Form> getFormDescendants(
            Long electronicFormId,
            boolean includeFieldData,
            boolean includeTableFields,
            boolean includeTableFieldFormRecordInfo
    ) {
        List<Form> returnVal = new ArrayList();
        if (electronicFormId == null) return returnVal;

        Map<Long, String> definitionAndTitle = this.formDefUtil.getFormDefinitionIdAndTitle();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersChildFormContainers
            );
            preparedStatement = this.getConnection().prepareStatement(syntax.getPreparedStatement());
            preparedStatement.setLong(1, electronicFormId);

            resultSet = preparedStatement.executeQuery();

            //Iterate each of the form containers...
            while (resultSet.next()) {
                Form mappedForm = this.mapFormContainerTo(definitionAndTitle, resultSet);
                if (mappedForm == null) continue;

                //Ancestor...
                mappedForm.setAncestorId(electronicFormId);
                returnVal.add(mappedForm);
            }

            //When field data must also be included...
            if (includeFieldData) {
                for (Form form : returnVal) {
                    List<Field> formFields =
                            this.fieldUtil.getFormFields(
                                    form.getId(),
                                    includeTableFields,
                                    includeTableFieldFormRecordInfo);
                    form.setFormFields(formFields);
                }
            }
        } catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        } finally {
            this.closeStatement(preparedStatement, resultSet);
        }

        return returnVal;
    }

    /**
     * Gets the descendants for the {@code electronicFormIdParam} Form with the
     * FormContainer and FormContainerFlow state.
     *
     * @param electronicFormId   Identifier for the Form.
     * @param includeFieldData   Whether to populate the return {@code List<Form>} fields.
     * @param includeTableFields Whether to populate the return {@code List<Form>} table fields.
     * @return {@code List<Form>} descendants.
     * @see Form
     */
    @Deprecated
    public List<Form> getFormDescendantsWithStates(
            Long electronicFormId,
            boolean includeFieldData,
            boolean includeTableFields
    ) {
        List<Form> returnVal = new ArrayList();
        if (electronicFormId == null) return returnVal;

        Map<Long, String> definitionAndTitle = this.formDefUtil.getFormDefinitionIdAndTitle();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersChildFormContainersWithStates
            );
            preparedStatement = this.getConnection().prepareStatement(syntax.getPreparedStatement());
            preparedStatement.setLong(1, electronicFormId);

            resultSet = preparedStatement.executeQuery();

            //Iterate each of the form containers...
            while (resultSet.next()) {
                Form mappedForm = this.mapFormContainerTo(definitionAndTitle, resultSet);
                if (mappedForm == null) continue;

                //Map the states...
                this.mapFormContainerStatesTo(mappedForm, resultSet);

                //Ancestor...
                mappedForm.setAncestorId(electronicFormId);
                returnVal.add(mappedForm);
            }

            //When field data must also be included...
            if (includeFieldData) {
                for (Form form : returnVal) {
                    List<Field> formFields = this.fieldUtil.getFormFields(
                            form.getId(), includeTableFields, false);
                    form.setFormFields(formFields);
                }
            }
        } catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        } finally {
            this.closeStatement(preparedStatement, resultSet);
        }

        return returnVal;
    }

    /**
     * Gets the ancestor for the {@code electronicFormIdParam} Form.
     *
     * @param electronicFormId   Identifier for the Form.
     * @param includeFieldData   Whether to populate the return {@code Form} fields.
     * @param includeTableFields Whether to populate the return {@code Form} table fields.
     * @return {@code Form} descendants.
     * @see Form
     */
    public Form getFormAncestor(Long electronicFormId, boolean includeFieldData, boolean includeTableFields) {
        if (electronicFormId == null) return null;

        Form returnVal = null;
        Map<Long, String> definitionAndTitle = this.formDefUtil.getFormDefinitionIdAndTitle();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Form.GetFormContainersParentFormContainer
            );

            preparedStatement = this.getConnection().prepareStatement(syntax.getPreparedStatement());
            preparedStatement.setLong(1, electronicFormId);

            resultSet = preparedStatement.executeQuery();

            //Iterate each of the form containers...
            if (resultSet.next()) returnVal = this.mapFormContainerTo(definitionAndTitle, resultSet);

            //When field data must also be included...
            if (includeFieldData && returnVal != null) {
                returnVal.setFormFields(
                        this.fieldUtil.getFormFields(
                                returnVal.getId(),
                                includeTableFields,
                                false)
                );
            }
            return returnVal;
        } catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        } finally {
            this.closeStatement(preparedStatement, resultSet);
        }
    }

    /**
     * Maps the Form to the provided Definition-Id and Title.
     *
     * @param definitionAndTitle Form Definition Id and Title mapping.
     * @param resultSet          Result Set used to populate {@code Form} with.
     * @return Form
     * @throws SQLException If no mapping exists for Form Type.
     * @see ResultSet
     */
    private Form mapFormContainerTo(Map<Long, String> definitionAndTitle, ResultSet resultSet) throws SQLException {
        Long formId = resultSet.getLong(SQLColumnIndex._01_FORM_ID);
        Long formTypeId = resultSet.getLong(SQLColumnIndex._02_FORM_TYPE);
        String formType = definitionAndTitle.get(formTypeId);

        String title = resultSet.getString(SQLColumnIndex._03_TITLE);
        Date created = resultSet.getDate(SQLColumnIndex._04_CREATED);
        Date lastUpdated = resultSet.getDate(SQLColumnIndex._05_LAST_UPDATED);
        Long currentUserId = resultSet.getLong(SQLColumnIndex._06_CURRENT_USER_ID);

        if (formType == null) {
            throw new SQLException(String.format("No mapping found for Form Type '%s'.", formTypeId));
        }

        Form toAdd = new Form(formType);
        toAdd.setFormTypeId(formTypeId);

        toAdd.setId(formId);
        toAdd.setTitle(title);

        // Created:
        if (created != null) toAdd.setDateCreated(new Date(created.getTime()));

        // Last Updated:
        if (lastUpdated != null) toAdd.setDateLastUpdated(new Date(lastUpdated.getTime()));

        // Current User:
        if (currentUserId != null && currentUserId.longValue() > 0) {
            User currentUser = new User();
            currentUser.setId(currentUserId);
            toAdd.setCurrentUser(currentUser);
        }

        return toAdd;
    }

    /**
     * Maps the Form states with the {@code resultSetParam}.
     *
     * @param resultSetParam Result Set used to populate {@code Form} with.
     * @return Form
     * @throws SQLException If no mapping exists for Form Type.
     * @see ResultSet
     */
    private void mapFormContainerStatesTo(
            Form previousMappedForm,
            ResultSet resultSetParam
    ) throws SQLException {
        if (previousMappedForm == null) return;

        //Form Container State...
        Long formContainerState = resultSetParam.getLong(
                SQLColumnIndex._07_FORM_CONTAINER_STATE);
        long formContStateId = (formContainerState == null) ?
                0 : formContainerState.longValue();
        if (formContStateId > 0) {
            if (formContStateId == 1) {
                previousMappedForm.setState(Form.State.OPEN);
            } else if (formContStateId == 2) {
                previousMappedForm.setState(Form.State.LOCKED);
            }
        }

        Long formContainerFlowState = resultSetParam.getLong(
                SQLColumnIndex._08_FORM_CONTAINER_FLOW_STATE);
        long formContFlowStateId = (formContainerFlowState == null) ?
                0 : formContainerFlowState.longValue();
        if (formContFlowStateId > 0) {
            if (formContFlowStateId == 1) {
                previousMappedForm.setFlowState(
                        FluidItem.FlowState.NotInFlow.name());
            } else if (formContFlowStateId == 2) {
                previousMappedForm.setFlowState(
                        FluidItem.FlowState.WorkInProgress.name());
            } else if (formContFlowStateId == 3) {
                previousMappedForm.setFlowState(
                        FluidItem.FlowState.UserSend.name());
            } else if (formContFlowStateId == 4) {
                previousMappedForm.setFlowState(
                        FluidItem.FlowState.UserSendWorkInProgress.name());
            } else if (formContFlowStateId == 5) {
                previousMappedForm.setFlowState(FluidItem.FlowState.Archive.name());
            }
        }
    }
}
