package com.fluid.program.api.util.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fluid.program.api.util.sql.ABaseSQLUtil;
import com.fluid.program.api.util.sql.exception.FluidSQLException;
import com.fluid.program.api.util.sql.syntax.ISyntax;
import com.fluid.program.api.util.sql.syntax.SyntaxFactory;
import com.fluid.program.api.vo.*;

/**
 * Created by jasonbruwer on 15/07/17.
 */
public class SQLFormFieldUtil extends ABaseSQLUtil {

    /**
     *
     */
    public static class FormFieldMapping extends ABaseFluidVO{

        public Long formDefinitionId;
        public Long formFieldId;
        public Long dataType;
        public String metaData;
        public String name;
        public String description;

        /**
         *
         * @param formDefinitionIdParam
         * @param formFieldIdParam
         * @param dataTypeParam
         * @param metaDataParam
         * @param nameParam
         * @param descriptionParam
         */
        public FormFieldMapping(
                Long formDefinitionIdParam,
                Long formFieldIdParam,
                Long dataTypeParam,
                String metaDataParam,
                String nameParam,
                String descriptionParam) {
            this.formDefinitionId = formDefinitionIdParam;
            this.formFieldId = formFieldIdParam;
            this.dataType = dataTypeParam;
            this.metaData = metaDataParam;
            this.name = nameParam;
            this.description = descriptionParam;
        }
    }

    /**
     *
     * @param connectionParam
     */
    public SQLFormFieldUtil(Connection connectionParam) {
        super(connectionParam);
    }

    /**
     *
     * @param electronicFormIdParam
     * @return
     */
    public List<FormFieldMapping> getFormFieldMappingForForm(
            Long electronicFormIdParam)
    {
        List<FormFieldMapping> returnVal = new ArrayList<FormFieldMapping>();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    ISyntax.ProcedureMapping.Field.GetFormFieldsForFormContainer);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1,electronicFormIdParam);

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            //Iterate each of the form containers...
            while (resultSet.next())
            {
                returnVal.add(
                        this.mapFormFieldMapping(resultSet));
            }
        }
        //
        catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        }
        //
        finally {
            this.closeStatement(preparedStatement);
        }

        return returnVal;
    }

    /**
     *
     * @param electronicFormIdParam
     * @return
     */
    public List<Field> getFormFields(
            Long electronicFormIdParam,
            boolean includeTableFieldsParam)
    {
        List<Field> returnVal = new ArrayList<Field>();

        if(electronicFormIdParam == null)
        {
            return returnVal;
        }

        List<FormFieldMapping> fieldMappings =
                this.getFormFieldMappingForForm(electronicFormIdParam);

        if(fieldMappings == null || fieldMappings.isEmpty())
        {
            return returnVal;
        }

        //Get the values for each of the fields...
        for(FormFieldMapping fieldMapping : fieldMappings)
        {
            if(!includeTableFieldsParam && fieldMapping.dataType == 7){//Table Field...
                continue;
            }

            Field fieldToAdd = this.getFormFieldValueFor(fieldMapping, electronicFormIdParam);
            if(fieldToAdd == null)
            {
                continue;
            }

            //When table field...
            if(includeTableFieldsParam && (fieldToAdd.getFieldValue() instanceof TableField))
            {
                TableField tableField = (TableField)fieldToAdd.getFieldValue();

                if(tableField.getTableRecords() != null && !tableField.getTableRecords().isEmpty())
                {
                    for(Form tableRecordForm : tableField.getTableRecords())
                    {
                        tableRecordForm.setFormFields(
                                this.getFormFields(tableRecordForm.getId(),false));
                    }
                }
            }

            returnVal.add(fieldToAdd);
        }

        return returnVal;
    }

    /**
     *
     * @param formFieldMappingParam
     * @return
     */
    public Field getFormFieldValueFor(
            FormFieldMapping formFieldMappingParam,
            Long formContainerIdParam)
    {

        Field returnVal = null;

        if(formFieldMappingParam == null)
        {
            return returnVal;
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            ISyntax syntax = SyntaxFactory.getInstance().getFieldValueSyntaxFor(
                    this.getSQLTypeFromConnection(),
                    formFieldMappingParam);

            preparedStatement = this.getConnection().prepareStatement(
                    syntax.getPreparedStatement());

            preparedStatement.setLong(1, formFieldMappingParam.formDefinitionId);
            preparedStatement.setLong(2, formFieldMappingParam.formFieldId);
            preparedStatement.setLong(3, formContainerIdParam);

            resultSet = preparedStatement.executeQuery();
            resultSet.beforeFirst();

            switch (formFieldMappingParam.dataType.intValue())
            {
                //Text...
                case 1:
                if(resultSet.next())
                {
                    returnVal = new Field(
                            formFieldMappingParam.name,
                            resultSet.getString(1),
                            Field.Type.Text);
                }
                break;
                //True False...
                case 2:
                if(resultSet.next())
                {
                    returnVal = new Field(
                            formFieldMappingParam.name,
                            resultSet.getBoolean(1),
                            Field.Type.TrueFalse);
                }
                break;
                //Paragraph Text...
                case 3:
                if(resultSet.next())
                {
                    returnVal = new Field(
                            formFieldMappingParam.name,
                            resultSet.getString(1),
                            Field.Type.ParagraphText);
                }
                break;
                //Multiple Choice...
                case 4:
                    MultiChoice multiChoice = new MultiChoice();

                    List<String> selectedValues = new ArrayList<String>();
                    while(resultSet.next())
                    {
                        selectedValues.add(resultSet.getString(1));
                    }

                    multiChoice.setSelectedMultiChoices(selectedValues);
                    if(!selectedValues.isEmpty())
                    {
                        returnVal = new Field(
                                formFieldMappingParam.name,
                                multiChoice);
                    }
                break;
                //Date Time...
                case 5:
                    if(resultSet.next())
                    {
                        returnVal = new Field(
                                formFieldMappingParam.name,
                                resultSet.getDate(1),
                                Field.Type.DateTime);
                    }
                break;
                case 6:
                    if(resultSet.next())
                    {
                        returnVal = new Field(
                                formFieldMappingParam.name,
                                resultSet.getDouble(1),
                                Field.Type.Decimal);
                    }
                break;
                case 7:
                    List<Long> formContainerIds = new ArrayList<Long>();
                    while(resultSet.next())
                    {
                        formContainerIds.add(resultSet.getLong(1));
                    }

                    if(!formContainerIds.isEmpty())
                    {
                        TableField tableField = new TableField();

                        List<Form> formRecords = new ArrayList<Form>();
                        for(Long formContainerId : formContainerIds)
                        {
                            Form toAdd = new Form();
                            toAdd.setId(formContainerId);
                            formRecords.add(toAdd);
                        }

                        tableField.setTableRecords(formRecords);

                        returnVal = new Field(
                                formFieldMappingParam.name,
                                tableField,
                                Field.Type.Table);
                    }
                break;
                default:
                    throw new SQLException("Unable to map '"+
                            formContainerIdParam.intValue() +"', to Form Field value.");
            }

            return returnVal;
        }
        //
        catch (SQLException sqlError) {
            throw new FluidSQLException(sqlError);
        }
        //
        finally {
            this.closeStatement(preparedStatement);
        }
    }

    /**
     *
     * @param resultSetParam
     * @return
     */
    private FormFieldMapping mapFormFieldMapping(ResultSet resultSetParam)
    throws SQLException{

        FormFieldMapping toAdd =
                new FormFieldMapping(
                        resultSetParam.getLong(1),
                        resultSetParam.getLong(2),
                        resultSetParam.getLong(3),
                        resultSetParam.getString(4),
                        resultSetParam.getString(5),
                        resultSetParam.getString(6));

        return toAdd;
    }

}
