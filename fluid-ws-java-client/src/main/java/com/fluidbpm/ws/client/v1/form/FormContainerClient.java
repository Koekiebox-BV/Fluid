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

package com.fluidbpm.ws.client.v1.form;

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.form.FormListing;
import com.fluidbpm.program.api.vo.form.TableRecord;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricData;
import com.fluidbpm.program.api.vo.historic.FormFlowHistoricDataListing;
import com.fluidbpm.program.api.vo.historic.FormHistoricData;
import com.fluidbpm.program.api.vo.historic.FormHistoricDataListing;
import com.fluidbpm.program.api.vo.item.CustomWebAction;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java Web Service Client for Electronic Form related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FormContainer
 * @see Form
 */
public class FormContainerClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrl URL to base endpoint.
     * @param serviceTicket The Server issued Service Ticket.
     */
    public FormContainerClient(String endpointBaseUrl, String serviceTicket) {
        super(endpointBaseUrl);
        this.setServiceTicket(serviceTicket);
    }

    /**
     * Create a new Form Container / Electronic Forms.
     *
     * @param form The Form to create.
     * @param addToPersonalInventory Should the form be added to the users P/I after creation.
     *
     * @return Created Form Container / Electronic Form.
     *
     * @see Field
     */
    public Form createFormContainer(Form form, boolean addToPersonalInventory) {
        return new Form(this.putJson(
            this.clearForRestCreateUpdate(form),
            WS.Path.FormContainer.Version1.formContainerCreate(addToPersonalInventory, false))
        );
    }

    /**
     * Create a new Form Container / Electronic Forms.
     *
     * @param form The Form to create.
     * @param addToPersonalInventory Should the form be added to the users P/I after creation.
     * @param removeLockAfterCreate Should the form not be unlocked after the creation?
     *
     * @return Created Form Container / Electronic Form.
     *
     * @see Field
     */
    public Form createFormContainer(
            Form form,
            boolean addToPersonalInventory,
            boolean removeLockAfterCreate
    ) {
        return new Form(this.putJson(
                this.clearForRestCreateUpdate(form),
                WS.Path.FormContainer.Version1.formContainerCreate(addToPersonalInventory, removeLockAfterCreate))
        );
    }

    /**
     * Create a new Form Container / Electronic Forms.
     * The Form will not be added to the P/I.
     *
     * @param form The Form to create.
     * @return Created Form Container / Electronic Form.
     *
     * @see Field
     */
    public Form createFormContainer(Form form) {
        return this.createFormContainer(
            this.clearForRestCreateUpdate(form),
            false
        );
    }

    /**
     * Create a new history record for a {@code Form}.
     *
     * @param formHistory The FormHistoricData to create.
     * @return Created Form Historic Data.
     *
     * @see FormHistoricData
     */
    public FormHistoricData createFormHistoricData(FormHistoricData formHistory) {
        if (formHistory != null) {
            formHistory.setServiceTicket(this.serviceTicket);
            formHistory.setFormForAuditCreate(this.clearForRestCreateUpdate(formHistory.getFormForAuditCreate()));
        }

        return new FormHistoricData(this.putJson(formHistory, WS.Path.FormHistory.Version1.formHistoryCreate()));
    }

    /**
     * Create a new Table Record.
     *
     * @param tableRecord The Table Record to create.
     * @return Created Table Record.
     *
     * @see TableRecord
     */
    public TableRecord createTableRecord(TableRecord tableRecord) {
        if (tableRecord != null) {
            tableRecord.setServiceTicket(this.serviceTicket);
            tableRecord.setFormContainer(this.clearForRestCreateUpdate(tableRecord.getFormContainer()));
            tableRecord.setParentFormContainer(this.clearForRestIdOnly(tableRecord.getParentFormContainer()));
        }

        return new TableRecord(this.putJson(
                tableRecord,
                WS.Path.FormContainerTableRecord.Version1.formContainerTableRecordCreate())
        );
    }

    /**
     * Update a Form Container / Electronic Form.
     * The table record forms may also be updated with
     *
     * @param form The Form to update.
     * @return Updated Form Container / Electronic Form.
     *
     * @see Field
     * @see TableRecord
     * @see Form
     */
    public Form updateFormContainer(Form form) {
        return new Form(this.postJson(
            this.clearForRestCreateUpdate(form),
            WS.Path.FormContainer.Version1.formContainerUpdate())
        );
    }

    /**
     * Execute the Custom Program with action alias {@code customWebActionParam}.
     * This method may be used for Form and Table Records.
     *
     * @param customWebAction The custom web action name. Action identifier.
     * @param form The Form to send for 3rd Party execution.
     *
     * @return Result after the 3rd Party Custom Web Action completed.
     *
     * @see Field
     * @see Form
     * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
     */
    public CustomWebAction executeCustomWebAction(String customWebAction, Form form) {
        return this.executeCustomWebAction(
            customWebAction,
            false,
            null,
            form
        );
    }

    /**
     * Execute the Custom Program with action alias {@code customWebActionParam}.
     * This method may be used for Form and Table Records.
     *
     * @param customWebAction The custom web action name. Action identifier.
     * @param isTableRecord Is the form a table record form.
     * @param formContainerTableRecordBelongsTo The parent form container if table record.
     * @param form The Form to send for 3rd Party execution.
     *
     * @return Result after the 3rd Party Custom Web Action completed.
     *
     * @see Field
     * @see Form
     * @see TableRecord
     * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
     */
    public CustomWebAction executeCustomWebAction(
        String customWebAction,
        boolean isTableRecord,
        Long formContainerTableRecordBelongsTo,
        Form form
    ) {
        if (customWebAction == null || customWebAction.trim().isEmpty()) {
            throw new FluidClientException(
                    "Custom Web Action is mandatory.", FluidClientException.ErrorCode.FIELD_VALIDATE
            );
        }

        if (form == null) {
            throw new FluidClientException(
                    "Form is mandatory.", FluidClientException.ErrorCode.FIELD_VALIDATE
            );
        }
        boolean formFieldsSet = form.getFormFields() != null;

        CustomWebAction action = new CustomWebAction();
        action.setTaskIdentifier(customWebAction);
        action.setServiceTicket(this.serviceTicket);

        // Cache the Multi Choice Available items:
        Map<String, List<String>> mapAvail = new HashMap<>();
        Map<String, String> mapAvailComb = new HashMap<>();
        if (formFieldsSet) {
            form.getFormFields().stream()
                    .filter(itm -> (itm.getTypeAsEnum() != null && itm.getTypeAsEnum() == Field.Type.MultipleChoice) &&
                            itm.getFieldValueAsMultiChoice() != null)
                    .forEach(itm -> {
                        MultiChoice multiChoice = itm.getFieldValueAsMultiChoice();
                        if (multiChoice.getAvailableMultiChoices() != null &&
                                !multiChoice.getAvailableMultiChoices().isEmpty()) {
                            mapAvail.put(itm.getFieldName(), multiChoice.getAvailableMultiChoices());
                        }
                        if (multiChoice.getAvailableMultiChoicesCombined() != null) {
                            mapAvailComb.put(itm.getFieldName(), multiChoice.getAvailableMultiChoicesCombined());
                        }
                    });
        }

        action.setForm(this.clearForRestCreateUpdate(form));
        action.setIsTableRecord(isTableRecord);
        action.setFormTableRecordBelongsTo(formContainerTableRecordBelongsTo);

        try {
            return new CustomWebAction(this.postJson(action, WS.Path.FormContainer.Version1.executeCustomWebAction()));
        } catch (Exception err) {
            if (formFieldsSet) {
                form.getFormFields().stream()
                        .filter(itm -> (itm.getTypeAsEnum() != null && itm.getTypeAsEnum() == Field.Type.MultipleChoice) &&
                                itm.getFieldValueAsMultiChoice() != null)
                        .forEach(itm -> {
                            MultiChoice multiChoice = itm.getFieldValueAsMultiChoice();
                            multiChoice.setAvailableMultiChoices(mapAvail.get(itm.getFieldName()));
                            multiChoice.setAvailableMultiChoicesCombined(mapAvailComb.get(itm.getFieldName()));
                        });
            }
            throw err;
        }
    }

    /**
     * Deletes the Form Container provided.
     * Id must be set on the Form Container.
     *
     * @param form The Form Container to Delete.
     * @return The deleted Form Container.
     */
    public Form deleteFormContainer(Form form) {
        return new Form(
            this.postJson(this.clearForRestIdOnly(form),
            WS.Path.FormContainer.Version1.formContainerDelete())
        );
    }

    /**
     * Retrieves Electronic Form Workflow historic information.
     *
     * The Form Id must be provided.
     *
     * @param form The form to retrieve historic data for.
     * @return Electronic Form Workflow historic data.
     */
    public List<FormFlowHistoricData> getFormFlowHistoricData(Form form) {
        return new FormFlowHistoricDataListing(this.postJson(
            this.clearForRestIdOnly(form),
            WS.Path.FlowItemHistory.Version1.getByFormContainer())
        ).getListing();
    }

    /**
     * Retrieves Electronic Form and Field historic information.
     *
     * The Form Id must be provided.
     *
     * @param includeCurrent Whether the current values should be included.
     *
     * @param form The form to retrieve historic data for.
     * @return Electronic Form and Field historic data.
     */
    public List<FormHistoricData> getFormAndFieldHistoricData(Form form, boolean includeCurrent) {
        return this.getFormAndFieldHistoricData(form, includeCurrent, false);
    }

    /**
     * Retrieves Electronic Form and Field historic information.
     *
     * The Form Id must be provided.
     *
     * @param includeCurrent Whether the current values should be included.
     * @param labelFieldName Should the label field name be used instead of the system name.
     *
     * @param form The form to retrieve historic data for.
     * @return Electronic Form and Field historic data.
     */
    public List<FormHistoricData> getFormAndFieldHistoricData(
        Form form,
        boolean includeCurrent,
        boolean labelFieldName
    ) {
        return new FormHistoricDataListing(this.postJson(
            this.clearForRestIdOnly(form),
            WS.Path.FormHistory.Version1.getByFormContainer(includeCurrent, labelFieldName))
        ).getListing();
    }

    /**
     * Retrieves Electronic Form and Field historic information for
     * the most recent modification.
     *
     * The Form Id must be provided.
     *
     * @param form The form to retrieve historic data for.
     *
     * @return Electronic Form and Field historic data.
     */
    public FormHistoricData getMostRecentFormAndFieldHistoricData(Form form) {
        return new FormHistoricData(this.postJson(
                this.clearForRestIdOnly(form),
                WS.Path.FormHistory.Version1.getByMostRecentByFormContainer()));
    }

    /**
     * Retrieves the Form Container by Primary key.
     *
     * @param formContainerId The Form Container primary key.
     * @return Form by Primary key.
     */
    public Form getFormContainerById(Long formContainerId) {
        return this.getFormContainerById(formContainerId, false);
    }

    /**
     * Retrieves the Form Container by Primary key.
     *
     * @param formContainerId The Form Container primary key.
     * @param executeCalculatedLabels Should calculated labels be executed.
     * @return Form by Primary key.
     */
    public Form getFormContainerById(Long formContainerId, boolean executeCalculatedLabels) {
        Form form = new Form(formContainerId);
        form.setServiceTicket(this.serviceTicket);

        return new Form(this.postJson(
            form,
            WS.Path.FormContainer.Version1.getById(executeCalculatedLabels))
        );
    }

    /**
     * Fetch the parent/ancestor form for {@code form}
     *
     * @param form The child form.
     *
     * @return List of forms where child is {@code form}
     *
     * @see Form
     */
    public List<Form> getAncestorFor(Form form) {
        return new FormListing(this.postJson(
            this.clearForRestIdOnly(form),
            WS.Path.FormContainer.Version1.getAncestorByChild())
        ).getListing();
    }

    /**
     * Performs a lookup on Title only.
     *
     * @param titleLookupText The lookup value. Matches will be found where title contains lookup text.
     * @param limit The max number of results.
     * @param offset The starting offset.
     *
     * @return FormListing where title contains {@code titleLookupText}
     *
     * @see FormListing
     */
    public FormListing getFormContainersByTitleContains(String titleLookupText, int limit, int offset) {
        Form form = new Form();
        form.setTitle(titleLookupText);
        form.setServiceTicket(this.serviceTicket);

        return new FormListing(this.postJson(
            form,
            WS.Path.FormContainer.Version1.getByTitleContains(limit, offset))
        );
    }

    /**
     * Lock the provided form container for logged in user.
     *
     * @param form The form to lock.
     * @param jobView If retrieved from a view, the lock to view from.
     *
     * @return The locked form.
     */
    public Form lockFormContainer(Form form, JobView jobView) {
        return this.lockFormContainer(form, jobView, null);
    }

    /**
     * Lock the provided form container for logged in user.
     * If {@code userToLockAsParam} is provided and valid, that user will be used instead.
     *
     * @param form The form to lock.
     * @param jobView If retrieved from a view, the lock to view from.
     * @param userToLockAs The form will be locked as this user.
     *                          The logged in user must have permission to perform this action.
     *
     * @return The locked form.
     */
    public Form lockFormContainer(Form form, JobView jobView, User userToLockAs) {
        Long jobViewId = (jobView == null) ? null : jobView.getId();
        Long lockAsUserId = (userToLockAs == null) ? null : userToLockAs.getId();

        try {
            return new Form(this.postJson(
                this.clearForRestIdOnly(form),
                WS.Path.FormContainer.Version1.lockFormContainer(jobViewId, lockAsUserId))
            );
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Unlock the provided form container from the logged in user.
     * Item will not be removed from users Personal Inventory.
     *
     * @param form The form to unlock.
     *
     * @return The un-locked form.
     */
    public Form unLockFormContainer(Form form) {
        return this.unLockFormContainer(form, null,true,false);
    }

    /**
     * Unlock the provided form container from the logged in user.
     * Item will not be removed from users Personal Inventory.
     *
     * @param form The form to unlock.
     * @param unlockAsync Should unlock be performed asynchronous.
     *
     * @return The un-locked form.
     */
    public Form unLockFormContainer(Form form, boolean unlockAsync) {
        return this.unLockFormContainer(form, null, unlockAsync, false);
    }

    /**
     * Unlock the provided form container from the logged in user.
     * The unlock will be performed asynchronous.
     * Item will not be removed from users Personal Inventory.
     *
     * @param form The form to unlock.
     * @param userToUnLockAs The form will be un-locked as this user.
     *                          The logged in user must have permission to perform this action.
     *
     * @return The un-locked form.
     */
    public Form unLockFormContainer(Form form, User userToUnLockAs) {
        return this.unLockFormContainer(form, userToUnLockAs, true, false);
    }

    /**
     * Unlock the provided form container from the logged in user.
     * The unlock will be performed asynchronous.
     * Item will not be removed from users Personal Inventory.
     *
     * @param form The form to unlock.
     * @param userToUnLockAs The form will be un-locked as this user.
     *                          The logged in user must have permission to perform this action.
     * @param unlockAsync Should unlock be performed asynchronous.
     *
     * @return The un-locked form.
     */
    public Form unLockFormContainer(
        Form form,
        User userToUnLockAs,
        boolean unlockAsync
    ) {
        return this.unLockFormContainer(
                this.clearForRestIdOnly(form),
                userToUnLockAs,
                unlockAsync,
                false);
    }

    /**
     * Unlock the provided form container from the logged in user.
     *
     * @param form The form to unlock.
     * @param userToUnLockAs The form will be un-locked as this user.
     *                          The logged in user must have permission to perform this action.
     * @param unlockAsync Should the unlock be performed asynchronous.
     * @param removeFromPersonalInventory Remove from Personal Inventory when unlocked.
     *
     * @return The un-locked form.
     */
    public Form unLockFormContainer(
        Form form,
        User userToUnLockAs,
        boolean unlockAsync,
        boolean removeFromPersonalInventory
    ) {
        Long unLockAsUserId = (userToUnLockAs == null) ? null : userToUnLockAs.getId();

        try {
            return new Form(this.postJson(
                    this.clearForRestIdOnly(form),
                    WS.Path.FormContainer.Version1.unLockFormContainer(
                            unLockAsUserId,
                            unlockAsync,
                            removeFromPersonalInventory)));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Create a PDF version of the attachment for form {@code formToPrint}
     * @param formToPrint The form to print.
     * @param includeCompanyLogo Should the company logo be included.
     * @param includeAncestor Should the ancestor form be included.
     * @param includeDescendants Should the descendant form/s be included.
     * @param includeFormProperties Should the form properties be included.
     *
     * @return Attachment with PDF content.
     *
     * @see Attachment
     */
    public Attachment printForm(
        Form formToPrint,
        boolean includeCompanyLogo,
        boolean includeAncestor,
        boolean includeDescendants,
        boolean includeFormProperties
    ) {
        try {
            return new Attachment(this.postJson(
                    this.clearForRestIdOnly(formToPrint),
                    WS.Path.FormContainer.Version1.printAsPDFAttachment(
                            includeCompanyLogo,
                            includeAncestor,
                            includeDescendants,
                            includeFormProperties)));
        } catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Clear redundant JSON payloads to improve performance.
     * @param form To sanitise prior to sending through REST interface.
     */
    private Form clearForRestCreateUpdate(Form form) {
        if (form == null || form.getFormFields() == null) return form;

        form.setServiceTicket(this.serviceTicket);
        form.getFormFields().stream()
                .filter(itm -> (itm.getTypeAsEnum() != null && itm.getTypeAsEnum() == Field.Type.MultipleChoice) &&
                    itm.getFieldValueAsMultiChoice() != null)
                .map(Field::getFieldValueAsMultiChoice)
                .forEach(multiChoice -> {
                    multiChoice.setAvailableMultiChoices(null);
                    multiChoice.setAvailableMultiChoicesCombined(null);
                });
        return form;
    }

    /**
     * Only send the id and service ticket from {@code form}.
     * @param form To sanitise prior to sending through REST interface.
     */
    private Form clearForRestIdOnly(Form form) {
        if (form == null) return null;

        Form returnVal = new Form(form.getId());
        returnVal.setFormType(form.getFormType());
        returnVal.setFormTypeId(form.getFormTypeId());
        returnVal.setServiceTicket(this.serviceTicket);

        return returnVal;
    }
}