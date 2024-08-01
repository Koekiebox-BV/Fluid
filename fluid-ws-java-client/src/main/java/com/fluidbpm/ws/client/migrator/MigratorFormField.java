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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.field.BarcodeType;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import static com.fluidbpm.ws.client.migrator.MigratorForm.migrateFormDefinition;

/**
 * Migration class for form field migrations.
 *
 * @see com.fluidbpm.program.api.vo.form.Form
 * @see com.fluidbpm.program.api.vo.field.Field
 */
public class MigratorFormField {
    @Builder
    public static final class OptFieldMultiChoiceMigrate {
        private String fieldName;
        private String fieldDescription;
        private List<String> choices;
        private String choice;
    }

    @Builder
    public static final class OptFieldTableMigrate {
        private String fieldName;
        private String formType;
        private String fieldAndFormDescription;
        private String[] fields;

        public void fieldsParam(String ... fields) {
            this.fields = fields;
        }
    }

    @Builder
    public static final class OptFieldDateMigrate {
        private String fieldName;
        private String fieldDescription;
        private boolean dateOnly;
    }

    @Builder
    public static final class OptFieldTextMigrate {
        private String fieldName;
        private String fieldDescription;
        private boolean encrypted;
        private boolean latAndLong;
        private String maskedValue;
        private BarcodeType barcodeType;
    }

    @Builder
    public static final class OptFieldLabelMigrate {
        private String fieldName;
        private String fieldDescription;
    }

    @Builder
    public static final class OptFieldMigrate {
        private String fieldName;
        private String fieldDescription;
    }

    /**
     * Migrate a True/False field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldTrueFalse(
            FormFieldClient ffc, OptFieldMigrate opts
    ) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);

            ffc.createFieldTrueFalse(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Label plain field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldLabelMigrate}
     */
    public static void migrateFieldLabel(
            FormFieldClient ffc, OptFieldLabelMigrate opts
    ) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);

            ffc.createFieldLabelPlain(toCreate);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Text plain field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldText(
            FormFieldClient ffc, OptFieldTextMigrate opts
    ) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);

            if (opts.encrypted) {
                if (UtilGlobal.isBlank(opts.maskedValue)) {
                    ffc.createFieldTextEncryptedPlain(toCreate);
                } else {
                    ffc.createFieldTextEncryptedMasked(toCreate, opts.maskedValue);
                }
            } else if (UtilGlobal.isNotBlank(opts.maskedValue)) {
                ffc.createFieldTextMasked(toCreate, opts.maskedValue);
            } else if (opts.barcodeType != null) {
                ffc.createFieldTextBarcode(toCreate, opts.barcodeType.getValue());
            } else if (opts.latAndLong) {
                ffc.createFieldTextLatitudeAndLongitude(toCreate);
            } else {
                ffc.createFieldTextPlain(toCreate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Date field. Date field may or may not include time.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldDateMigrate}
     */
    public static void migrateFieldDateTime(
            FormFieldClient ffc, OptFieldDateMigrate opts
    ) {
        try {
            Field toMigrate = new Field(opts.fieldName, null, Field.Type.DateTime);
            toMigrate.setFieldDescription(opts.fieldDescription);
            if (opts.dateOnly) {
                ffc.createFieldDateTimeDate(toMigrate);
            } else {
                ffc.createFieldDateTimeDateAndTime(toMigrate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Multi-Choice field.
     * 
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceCreate}
     * @return Created or updated {@code Field}
     */
    public static Field migrateFieldMultiChoicePlain(
            FormFieldClient ffc, OptFieldMultiChoiceMigrate opts
    ) {
        try {
            Field toMigrate = new Field(opts.fieldName, null, Field.Type.Text);
            toMigrate.setFieldDescription(opts.fieldDescription);
            List<String> choices = new ArrayList<>();

            if (UtilGlobal.isNotBlank(opts.choice)) choices.add(opts.choice);
            if (opts.choices != null) choices.addAll(opts.choices);

            return ffc.createFieldMultiChoicePlain(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ffc.getFieldByName(opts.fieldName);
        }
    }

    /**
     * Migrate a Multi-Choice field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     * @return Migrated or updated {@code Field}
     */
    public static Field migrateFieldMultiChoiceSelectMany(
            FormFieldClient ffc, OptFieldMultiChoiceMigrate opts
    ) {
        try {
            Field toMigrate = new Field(opts.fieldName, null, Field.Type.Text);
            toMigrate.setFieldDescription(opts.fieldDescription);
            List<String> choices = new ArrayList<>();

            if (UtilGlobal.isNotBlank(opts.choice)) choices.add(opts.choice);
            if (opts.choices != null) choices.addAll(opts.choices);

            return ffc.createFieldMultiChoiceSelectMany(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ffc.getFieldByName(opts.fieldName);
        }
    }

    /**
     * Migrate a Multi-Choice field. Also ensures that provided option exists.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoicePlainOptionExists(
            FormFieldClient ffc, OptFieldMultiChoiceMigrate opts
    ) {
        List<String> choicesToEnsure = new ArrayList<>();
        if (UtilGlobal.isNotBlank(opts.choice)) choicesToEnsure.add(opts.choice);
        if (opts.choices != null) choicesToEnsure.addAll(opts.choices);

        Field field = migrateFieldMultiChoicePlain(ffc, opts);
        List<String> available = field.getFieldValueAsMultiChoice().getAvailableMultiChoices() == null ?
        new ArrayList<>() : field.getFieldValueAsMultiChoice().getAvailableMultiChoices();

        choicesToEnsure.forEach(choice -> {
            if (!available.contains(choice)) {
                available.add(choice);
                ffc.updateFieldMultiChoicePlain(field, available);
            }
        });
    }

    /**
     * Migrate a Multi-Choice field. Also ensures that provided option exists.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoiceSelectManyOptionExists(
            FormFieldClient ffc, OptFieldMultiChoiceMigrate opts
    ) {
        List<String> choicesToEnsure = new ArrayList<>();
        if (UtilGlobal.isNotBlank(opts.choice)) choicesToEnsure.add(opts.choice);
        if (opts.choices != null) choicesToEnsure.addAll(opts.choices);

        Field field = migrateFieldMultiChoiceSelectMany(ffc, opts);
        List<String> available = field.getFieldValueAsMultiChoice().getAvailableMultiChoices() == null ?
                new ArrayList<>() : field.getFieldValueAsMultiChoice().getAvailableMultiChoices();

        choicesToEnsure.forEach(choice -> {
            if (!available.contains(choice)) {
                available.add(choice);
                ffc.updateFieldMultiChoicePlain(field, available);
            }
        });
    }


    /**
     * Migrate a Table field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldTableField(
            FormDefinitionClient fdc, FormFieldClient ffc, OptFieldTableMigrate opts
    ) {
        try {
            migrateFormDefinition(
                    fdc,
                    MigratorForm.OptFormMigrate.builder()
                            .fields(opts.fields)
                            .formType(opts.formType)
                            .formDescription(opts.fieldAndFormDescription)
                            .build()
            );

            Field toMigrate = new Field(opts.fieldName, null, Field.Type.Table);
            toMigrate.setFieldDescription(opts.fieldAndFormDescription);

            ffc.createFieldTable(toMigrate, new Form(opts.formType), false);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }
    

}
