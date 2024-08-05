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
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import lombok.Builder;

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
    public static final class MigrateOptForm {
        private String formType;
        private String formDescription;
        private String[] fields;
    }

    @Builder
    public static final class MigrateOptRemoveForm {
        private Long formId;
        private String formName;
    }

    public static void migrateFormDefinition(
            FormDefinitionClient fdc,
            MigrateOptForm opts
    ) {
        Form form = new Form(opts.formType);
        List<Field> formFields = new ArrayList<>();
        if (opts.fields != null) {
            for (String fieldToIncl : opts.fields) formFields.add(new Field(fieldToIncl));
        }
        form.setFormFields(formFields);
        form.setFormDescription(opts.formDescription);

        try {
            fdc.createFormDefinition(form);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;

            Form tablFieldForm = fdc.getFormDefinitionByName(opts.formType);
            List<Field> existingFields = tablFieldForm.getFormFields();
            if (existingFields != null) {
                AtomicBoolean updateRequired = new AtomicBoolean(false);
                List<Field> toPushToServer = new ArrayList<>(existingFields);

                existingFields.stream()
                        .map(Field::getFieldName)
                        .filter(existing -> {
                            if (existing == null) return false;
                            return formFields.stream()
                                    .filter(itm -> existing.equalsIgnoreCase(itm.getFieldName()))
                                    .findFirst()
                                    .orElse(null) == null;
                        }).forEach(nonExist -> {
                            toPushToServer.add(new Field(nonExist));
                            updateRequired.set(true);
                        });

                if (updateRequired.get()) {
                    form.setFormFields(toPushToServer);

                    // Push new fields to Fluid.
                    fdc.updateFormDefinition(form);
                }
            }
        }
    }

    /**
     * Remove a form definition.
     *
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
