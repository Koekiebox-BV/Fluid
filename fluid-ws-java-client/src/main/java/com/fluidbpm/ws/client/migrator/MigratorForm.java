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
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Migration class for form migrations.
 *
 * @see Form
 */
public class MigratorForm {
    @Builder
    @Data
    public static final class MigrateOptForm {
        private String formType;
        private String formDescription;
        private String[] fields;
        private String[] associatedFlows;
    }

    @Builder
    public static final class MigrateOptRemoveForm {
        private Long formId;
        private String formName;
    }

    public static void migrateFormDefinition(FormDefinitionClient fdc, MigrateOptForm opts) {
        Form form = new Form(opts.formType);
        List<Field> fields = new ArrayList<>();
        if (opts.fields != null) {
            for (String fieldToIncl : opts.fields) fields.add(new Field(fieldToIncl));
        }
        form.setFormFields(fields);
        form.setFormDescription(opts.formDescription);
        List<Flow> flows = new ArrayList<>();
        if (opts.associatedFlows != null) {
            for (String flowToAss : opts.associatedFlows) flows.add(new Flow(flowToAss));
        }
        form.setAssociatedFlows(flows);

        try {
            fdc.createFormDefinition(form);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            Form existingForm = fdc.getFormDefinitionByName(opts.formType);
            form.setId(existingForm.getId());
            form.setFormTypeId(existingForm.getFormTypeId());
            form.setFormType(existingForm.getFormType());

            List<Field> existingFields = existingForm.getFormFields() == null ?
                    new ArrayList<>() : existingForm.getFormFields();
            AtomicBoolean updateRequired = new AtomicBoolean(false);
            List<Field> toPushToServerFields = new ArrayList<>(existingFields);
            fields.forEach(itm -> {
                String existingExists = existingFields.stream()
                        .map(Field::getFieldName)
                        .filter(existing -> existing.equalsIgnoreCase(itm.getFieldName()))
                        .findFirst().orElse(null);
                if (existingExists == null) {
                    toPushToServerFields.add(itm);
                    updateRequired.set(true);
                }
            });
            form.setFormFields(toPushToServerFields);

            List<Flow> existingFlows = existingForm.getAssociatedFlows() == null ?
                    new ArrayList<>() : existingForm.getAssociatedFlows();
            List<Flow> toPushToServerFlows = new ArrayList<>(existingFlows);
            flows.forEach(itm -> {
                String existingExists = existingFlows.stream()
                        .map(Flow::getName)
                        .filter(existing -> existing.equalsIgnoreCase(itm.getName()))
                        .findFirst().orElse(null);
                if (existingExists == null) {
                    toPushToServerFlows.add(itm);
                    updateRequired.set(true);
                }
            });
            form.setAssociatedFlows(toPushToServerFlows);

            if (updateRequired.get()) fdc.updateFormDefinition(form);
        }
    }

    /**Remove a form definition.
     * @param fdc {@code FormDefinitionClient}
     * @param opts {@code MigrateOptRemoveForm}
     */
    public static void removeFormDefinition(
            FormDefinitionClient fdc, MigratorForm.MigrateOptRemoveForm opts
    ) {
        Form toDelete = new Form(opts.formName);
        toDelete.setFormTypeId(opts.formId);
        fdc.deleteFormDefinition(toDelete);
    }
}
