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
import com.fluidbpm.ws.client.v1.config.GlobalFieldClient;
import com.fluidbpm.ws.client.v1.flow.RouteFieldClient;
import com.fluidbpm.ws.client.v1.form.FormDefinitionClient;
import com.fluidbpm.ws.client.v1.form.FormFieldClient;
import com.fluidbpm.ws.client.v1.user.UserFieldClient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fluidbpm.ws.client.migrator.MigratorForm.migrateFormDefinition;

/**
 * Migration class for form field migrations.
 *
 * @see com.fluidbpm.program.api.vo.form.Form
 * @see com.fluidbpm.program.api.vo.field.Field
 */
public class MigratorField {
    public static abstract class MigrateOptField {
        // Don nothing.
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldText extends MigrateOptField {
        private String fieldName;
        private String fieldDescription;
        private boolean encrypted;
        private boolean latAndLong;
        private boolean paragraphText;
        private String maskedValue;
        private BarcodeType barcodeType;
    }

    @Builder
    @Data
    public static class MigrateOptFieldMCBase {
        private String fieldName;
        private String fieldDescription;
        private List<String> choices;
        private String[] choicesArr;
        private String choice;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldMultiChoicePlain extends MigrateOptField {
        private MigrateOptFieldMCBase baseInfo;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldMultiChoiceSelectMany extends MigrateOptField {
        private MigrateOptFieldMCBase baseInfo;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldDate extends MigrateOptField {
        private String fieldName;
        private String fieldDescription;
        private boolean dateOnly;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldDecimal extends MigrateOptField {
        private String fieldName;
        private String fieldDescription;
        private boolean spinner;
        private boolean currency;//TODO
        private double valMin;
        private double valMax;
        private double valStepFactor;
        private String prefix;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldLabel extends MigrateOptField {
        private String fieldName;
        private String fieldDescription;
    }

    @Builder
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static final class MigrateOptFieldTrueFalse extends MigrateOptField {
        private String fieldName;
        private String fieldDescription;
    }

    @Builder
    @Data
    public static final class MigrateOptFieldTable {
        private String fieldName;
        private String formType;
        private String fieldAndFormDescription;
        private String[] fields;
        private boolean sumDecimals;
    }

    @Builder
    public static final class MigrateOptRemoveField {
        private Long fieldId;
        private String fieldName;
    }

    /**
     * Migrate user field.
     * @param ufc {@code UserFieldClient}
     * @param itm {@code MigratorField.MigrateOptField}
     */
    public static void migrateUserField(
            UserFieldClient ufc, MigratorField.MigrateOptField itm
    ) {
        if (itm instanceof MigratorField.MigrateOptFieldText) {
            MigratorField.migrateFieldText(ufc, (MigratorField.MigrateOptFieldText) itm);
        } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoicePlain) {
            MigratorField.migrateFieldMultiChoicePlainOptionExists(
                    ufc, (MigratorField.MigrateOptFieldMultiChoicePlain) itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoiceSelectMany) {
            MigratorField.migrateFieldMultiChoiceSelectManyOptionExists(
                    ufc, (MigratorField.MigrateOptFieldMultiChoiceSelectMany) itm
            );
        } else {
            throw new FluidClientException(
                    String.format("User: Type '%s' is not supported.", itm),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
            );
        }
    }

    /**
     * Migrate route field.
     * @param rfc {@code RouteFieldClient}
     * @param itm {@code MigratorField.MigrateOptField}
     */
    public static void migrateRouteField(
            RouteFieldClient rfc, MigratorField.MigrateOptField itm
    ) {
        if (itm instanceof MigratorField.MigrateOptFieldText) {
            MigratorField.migrateFieldText(rfc, (MigratorField.MigrateOptFieldText) itm);
        } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoicePlain) {
            MigratorField.migrateFieldMultiChoicePlainOptionExists(
                    rfc, (MigratorField.MigrateOptFieldMultiChoicePlain) itm
            );
        } else {
            throw new FluidClientException(
                    String.format("Route: Type '%s' is not supported.", itm),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
            );
        }
    }

    /**
     * Migrate global field.
     * @param gfc {@code GlobalFieldClient}
     * @param itm {@code MigratorField.MigrateOptField}
     */
    public static void migrateGlobalField(
            GlobalFieldClient gfc, MigratorField.MigrateOptField itm
    ) {
        if (itm instanceof MigratorField.MigrateOptFieldText) {
            MigratorField.migrateFieldText(gfc, (MigratorField.MigrateOptFieldText)itm);
        } else {
            throw new FluidClientException(
                    String.format("Global: Type '%s' is not supported.", itm),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
            );
        }
    }


    /**
     * Migrate form field.
     * @param ffc {@code FormFieldClient}
     * @param itm {@code MigratorField.MigrateOptField}
     */
    public static void migrateFormField(
            FormFieldClient ffc, MigratorField.MigrateOptField itm
    ) {
        if (itm instanceof MigratorField.MigrateOptFieldText) {
            MigratorField.migrateFieldText(ffc, (MigratorField.MigrateOptFieldText)itm);
        } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoicePlain) {
            MigratorField.migrateFieldMultiChoicePlainOptionExists(
                    ffc, (MigratorField.MigrateOptFieldMultiChoicePlain)itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldMultiChoiceSelectMany) {
            MigratorField.migrateFieldMultiChoiceSelectManyOptionExists(
                    ffc, (MigratorField.MigrateOptFieldMultiChoiceSelectMany)itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldDecimal) {
            MigratorField.migrateFieldDecimal(
                    ffc, (MigratorField.MigrateOptFieldDecimal)itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldDate) {
            MigratorField.migrateFieldDateTime(
                    ffc, (MigratorField.MigrateOptFieldDate)itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldTrueFalse) {
            MigratorField.migrateFieldTrueFalse(
                    ffc, (MigratorField.MigrateOptFieldTrueFalse)itm
            );
        } else if (itm instanceof MigratorField.MigrateOptFieldLabel) {
            MigratorField.migrateFieldLabel(
                    ffc, (MigratorField.MigrateOptFieldLabel)itm
            );
        } else {
            throw new FluidClientException(
                    String.format("Form: Type '%s' is not supported.", itm),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
            );
        }
    }

    /**
     * Migrate a True/False field.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldTrueFalse(
            FormFieldClient ffc, MigrateOptFieldTrueFalse opts
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
            FormFieldClient ffc, MigrateOptFieldLabel opts
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
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldText(FormFieldClient ffc, MigrateOptFieldText opts) {
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
            } else if (opts.paragraphText) {
                ffc.createFieldParagraphTextPlain(toCreate);
            } else {
                ffc.createFieldTextPlain(toCreate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Text plain field.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldText(UserFieldClient ufc, MigrateOptFieldText opts) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);
            if (opts.paragraphText) {
                ufc.createFieldParagraphTextPlain(toCreate);
            } else {
                ufc.createFieldTextPlain(toCreate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Text plain field.
     * @param rfc {@code RouteFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldText(RouteFieldClient rfc, MigrateOptFieldText opts) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);
            if (opts.paragraphText) {
                rfc.createFieldParagraphTextPlain(toCreate);
            } else {
                rfc.createFieldTextPlain(toCreate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Text plain field.
     * @param gfc {@code GlobalFieldClient}
     * @param opts {@code OptFieldMigrate}
     */
    public static void migrateFieldText(GlobalFieldClient gfc, MigrateOptFieldText opts) {
        try {
            Field toCreate = new Field(opts.fieldName, null, Field.Type.Text);
            toCreate.setFieldDescription(opts.fieldDescription);
            if (opts.paragraphText) {
                gfc.createFieldParagraphTextPlain(toCreate);
            } else {
                gfc.createFieldTextPlain(toCreate);
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**
     * Migrate a Date field. Date field may or may not include time.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldDateMigrate}
     */
    public static void migrateFieldDateTime(
            FormFieldClient ffc, MigrateOptFieldDate opts
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
     * Migrate a Date field. Date field may or may not include time.
     *
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldDateMigrate}
     */
    public static void migrateFieldDecimal(
            FormFieldClient ffc, MigrateOptFieldDecimal opts
    ) {
        try {
            Field toMigrate = new Field(opts.fieldName, null, Field.Type.DateTime);
            toMigrate.setFieldDescription(opts.fieldDescription);
            if (opts.spinner) {
                ffc.createFieldDecimalSpinner(toMigrate, opts.valMin, opts.valMax, opts.valStepFactor, opts.prefix);
            } else if (opts.currency) {
                ffc.createFieldDecimalSpinner(toMigrate, opts.valMin, opts.valMax, opts.valStepFactor, opts.prefix);
            } else {
                ffc.createFieldDecimalPlain(toMigrate);
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
            FormFieldClient ffc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        try {
            Field toMigrate = new Field(base.fieldName, null, Field.Type.MultipleChoice);
            toMigrate.setFieldDescription(base.fieldDescription);
            List<String> choices = choicesAsListCombined(base);

            return ffc.createFieldMultiChoicePlain(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ffc.getFieldByName(base.fieldName);
        }
    }

    /**
     * Migrate a Multi-Choice field. Also ensures that provided option exists.
     * @param rfc {@code RouteFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoicePlainOptionExists(
            RouteFieldClient rfc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        List<String> choicesToEnsure = choicesAsListCombined(base);
        Field field = migrateFieldMultiChoicePlain(rfc, opts);
        List<String> available = field.getFieldValueAsMultiChoice().getAvailableMultiChoices() == null ?
                new ArrayList<>() : field.getFieldValueAsMultiChoice().getAvailableMultiChoices();

        choicesToEnsure.forEach(choice -> {
            if (!available.contains(choice)) {
                available.add(choice);
                rfc.updateFieldMultiChoicePlain(field, available);
            }
        });
    }

    /**
     * Migrate a Multi-Choice field. Also ensures that provided option exists.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoicePlainOptionExists(
            UserFieldClient ufc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        List<String> choicesToEnsure = choicesAsListCombined(base);
        Field field = migrateFieldMultiChoicePlain(ufc, opts);
        List<String> available = field.getFieldValueAsMultiChoice().getAvailableMultiChoices() == null ?
                new ArrayList<>() : field.getFieldValueAsMultiChoice().getAvailableMultiChoices();

        choicesToEnsure.forEach(choice -> {
            if (!available.contains(choice)) {
                available.add(choice);
                ufc.updateFieldMultiChoicePlain(field, available);
            }
        });
    }

    /**
     * Migrate a Multi-Choice field.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code OptFieldMultiChoiceCreate}
     * @return Created or updated {@code Field}
     */
    public static Field migrateFieldMultiChoicePlain(
            UserFieldClient ufc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        try {
            Field toMigrate = new Field(base.fieldName, null, Field.Type.MultipleChoice);
            toMigrate.setFieldDescription(base.fieldDescription);
            List<String> choices = choicesAsListCombined(base);
            return ufc.createFieldMultiChoicePlain(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ufc.getFieldByName(base.fieldName);
        }
    }

    /**
     * Migrate a Multi-Choice field.
     * @param rfc {@code RouteFieldClient}
     * @param opts {@code OptFieldMultiChoiceCreate}
     * @return Created or updated {@code Field}
     */
    public static Field migrateFieldMultiChoicePlain(
            RouteFieldClient rfc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        try {
            Field toMigrate = new Field(base.fieldName, null, Field.Type.MultipleChoice);
            toMigrate.setFieldDescription(base.fieldDescription);
            List<String> choices = choicesAsListCombined(base);
            return rfc.createFieldMultiChoicePlain(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return rfc.getFieldByName(base.fieldName);
        }
    }

    /**Migrate a Multi-Choice field.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     * @return Migrated or updated {@code Field}
     */
    public static Field migrateFieldMultiChoiceSelectMany(
            FormFieldClient ffc, MigrateOptFieldMultiChoiceSelectMany opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        try {
            Field toMigrate = new Field(base.fieldName, null, Field.Type.MultipleChoice);
            toMigrate.setFieldDescription(base.fieldDescription);
            List<String> choices = choicesAsListCombined(base);

            return ffc.createFieldMultiChoiceSelectMany(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ffc.getFieldByName(base.fieldName);
        }
    }

    /**Migrate a Multi-Choice field.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     * @return Migrated or updated {@code Field}
     */
    public static Field migrateFieldMultiChoiceSelectMany(
            UserFieldClient ufc, MigrateOptFieldMultiChoiceSelectMany opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        try {
            Field toMigrate = new Field(base.fieldName, null, Field.Type.MultipleChoice);
            toMigrate.setFieldDescription(base.fieldDescription);
            List<String> choices = choicesAsListCombined(base);
            return ufc.createFieldMultiChoiceSelectMany(toMigrate, choices);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            return ufc.getFieldByName(base.fieldName);
        }
    }

    /**Migrate a Multi-Choice field. Also ensures that provided option exists.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoicePlainOptionExists(
            FormFieldClient ffc, MigrateOptFieldMultiChoicePlain opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;

        List<String> choicesToEnsure = choicesAsListCombined(base);
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

    /**Migrate a Multi-Choice field. Also ensures that provided option exists.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoiceSelectManyOptionExists(
            FormFieldClient ffc, MigrateOptFieldMultiChoiceSelectMany opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        List<String> choicesToEnsure = choicesAsListCombined(base);

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

    /**Migrate a Multi-Choice field. Also ensures that provided option exists.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldMultiChoiceSelectManyOptionExists(
            UserFieldClient ufc, MigrateOptFieldMultiChoiceSelectMany opts
    ) {
        MigrateOptFieldMCBase base = opts.baseInfo;
        List<String> choicesToEnsure = choicesAsListCombined(base);

        Field field = migrateFieldMultiChoiceSelectMany(ufc, opts);
        List<String> available = field.getFieldValueAsMultiChoice().getAvailableMultiChoices() == null ?
                new ArrayList<>() : field.getFieldValueAsMultiChoice().getAvailableMultiChoices();

        choicesToEnsure.forEach(choice -> {
            if (!available.contains(choice)) {
                available.add(choice);
                ufc.updateFieldMultiChoicePlain(field, available);
            }
        });
    }

    /**Migrate a Table field.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code OptFieldMultiChoiceMigrate}
     */
    public static void migrateFieldTableField(
            FormDefinitionClient fdc, FormFieldClient ffc, MigrateOptFieldTable opts
    ) {
        try {
            migrateFormDefinition(
                    fdc,
                    MigratorForm.MigrateOptForm.builder()
                            .fields(opts.fields)
                            .formType(opts.formType)
                            .formDescription(opts.fieldAndFormDescription)
                            .build()
            );

            // Fetch the newly created or existing form definition:
            Form formDefByName = fdc.getFormDefinitionByName(opts.formType);

            Field toMigrate = new Field(opts.fieldName, null, Field.Type.Table);
            toMigrate.setFieldDescription(opts.fieldAndFormDescription);
            ffc.createFieldTable(toMigrate, formDefByName, opts.sumDecimals);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
        }
    }

    /**Remove a field.
     * @param ufc {@code UserFieldClient}
     * @param opts {@code MigrateOptRemoveField}
     */
    public static void removeField(
            UserFieldClient ufc, MigrateOptRemoveField opts
    ) {
        ufc.forceDeleteField(new Field(opts.fieldId, opts.fieldName));
    }

    /**Remove a field.
     * @param ffc {@code FormFieldClient}
     * @param opts {@code MigrateOptRemoveField}
     */
    public static void removeField(
            FormFieldClient ffc, MigrateOptRemoveField opts
    ) {
        ffc.forceDeleteField(new Field(opts.fieldId, opts.fieldName));
    }

    private static List<String> choicesAsListCombined(MigrateOptFieldMCBase base) {
        List<String> choicesToEnsure = new ArrayList<>();
        if (UtilGlobal.isNotBlank(base.choice)) choicesToEnsure.add(base.choice);
        if (base.choices != null) choicesToEnsure.addAll(base.choices);
        if (base.choicesArr != null) choicesToEnsure.addAll(Arrays.asList(base.choicesArr));
        return choicesToEnsure;
    }
}
