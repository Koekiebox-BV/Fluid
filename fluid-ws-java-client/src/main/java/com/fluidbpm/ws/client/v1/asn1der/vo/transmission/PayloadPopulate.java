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

package com.fluidbpm.ws.client.v1.asn1der.vo.transmission;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PayloadPopulate is a data object used to model and manage the processing
 * of various multi-choice fields and metadata related to forms, users,
 * routes, and global entities. This class extends {@code ABaseFluidVO}
 * and inherits its base properties.
 *
 * The primary purpose of this class is to:
 * - Store and process information associated with multi-choice fields.
 * - Convert and map multi-choice data into structured formats.
 * - Provide methods to retrieve available and selected values from multi-choice fields.
 * - Handle metadata for form fields and enable metadata retrieval.
 *
 * Key responsibilities include:
 * - Mapping multi-choice fields into a structured {@code Map<String, Map<Long, String>>} format.
 * - Extracting available and selected values for multi-choice fields.
 * - Handling associated metadata for form fields and mapping them for easy access.
 *
 * The class supports multiple types of multi-choice fields:
 * - Form fields
 * - User fields
 * - Route fields
 * - Global fields
 */
@Getter
public class PayloadPopulate extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;

    private List<ASNMultiChoiceField> mcFormField;
    private List<ASNMultiChoiceField> mcUserField;
    private List<ASNMultiChoiceField> mcRouteField;
    private List<ASNMultiChoiceField> mcGlobalField;
    private List<FormFieldMetaData> ffMetaData;

    // FieldName, ChoiceId, Alias
    private Map<String, Map<Long, String>> multiChoicesForm;
    private Map<String, Map<Long, String>> multiChoicesUser;
    private Map<String, Map<Long, String>> multiChoicesRoute;
    private Map<String, Map<Long, String>> multiChoicesGlobal;
    // Reverse....
    private Map<String, Map<String, Long>> multiChoicesFormRev;
    private Map<String, Map<String, Long>> multiChoicesUserRev;
    private Map<String, Map<String, Long>> multiChoicesRouteRev;
    private Map<String, Map<String, Long>> multiChoicesGlobalRev;

    private Map<String, String> fieldMetaData;

    public PayloadPopulate() {
        this(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public PayloadPopulate(
            List<ASNMultiChoiceField> mcFormField,
            List<ASNMultiChoiceField> mcUserField,
            List<ASNMultiChoiceField> mcRouteField,
            List<ASNMultiChoiceField> mcGlobalField,
            List<FormFieldMetaData> ffMetaData
    ) {
        this.mcFormField = mcFormField;
        this.mcUserField = mcUserField;
        this.mcRouteField = mcRouteField;
        this.mcGlobalField = mcGlobalField;
        this.ffMetaData = ffMetaData;

        this.multiChoicesForm = this.setMCField(this.mcFormField);
        this.multiChoicesUser = this.setMCField(this.mcUserField);
        this.multiChoicesRoute = this.setMCField(this.mcRouteField);
        this.multiChoicesGlobal = this.setMCField(this.mcGlobalField);
        // Reverse....
        this.multiChoicesFormRev = this.setMCFieldRev(this.mcFormField);
        this.multiChoicesUserRev = this.setMCFieldRev(this.mcUserField);
        this.multiChoicesRouteRev = this.setMCFieldRev(this.mcRouteField);
        this.multiChoicesGlobalRev = this.setMCFieldRev(this.mcGlobalField);
        this.fieldMetaData = this.setFFMetaData(this.ffMetaData);
    }

    private Map<String, Map<Long, String>> setMCField(List<ASNMultiChoiceField> mcFields) {
        if (mcFields == null || mcFields.isEmpty()) return Collections.emptyMap();

        Map<String, Map<Long, String>> returnVal = new HashMap<>();
        for (ASNMultiChoiceField mcField : mcFields) {
            if (mcField.getMultiChoices() == null || mcField.getMultiChoices().isEmpty() ||
                    UtilGlobal.isBlank(mcField.getFieldName())) {
                continue;
            }

            Map<Long, String> mcToMap = mcField.getMultiChoices().stream()
                    .collect(Collectors.toMap(
                            ASNMultiChoice::getId,
                            ASNMultiChoice::getAlias
                    ));
            returnVal.put(mcField.getFieldName(), mcToMap);
        }
        return returnVal;
    }

    private Map<String, Map<String, Long>> setMCFieldRev(List<ASNMultiChoiceField> mcFields) {
        if (mcFields == null || mcFields.isEmpty()) return Collections.emptyMap();

        Map<String, Map<String, Long>> returnVal = new HashMap<>();
        for (ASNMultiChoiceField mcField : mcFields) {
            if (mcField.getMultiChoices() == null || mcField.getMultiChoices().isEmpty() ||
                    UtilGlobal.isBlank(mcField.getFieldName())) {
                continue;
            }

            Map<String, Long> mcToMap = mcField.getMultiChoices().stream()
                    .collect(Collectors.toMap(
                            ASNMultiChoice::getAlias,
                            ASNMultiChoice::getId
                    ));
            returnVal.put(mcField.getFieldName(), mcToMap);
        }
        return returnVal;
    }

    private Map<String, String> setFFMetaData(List<FormFieldMetaData> ffMetaData) {
        Map<String, String> returnVal = new HashMap<>();
        if (ffMetaData == null || ffMetaData.isEmpty()) return returnVal;

        ffMetaData.forEach(ffMeta -> {
            if (UtilGlobal.isBlank(ffMeta.getFieldName()) || UtilGlobal.isBlank(ffMeta.getMetaData())) return;
            returnVal.put(ffMeta.getFieldName(), ffMeta.getMetaData());
        });
        return returnVal;
    }

    /**
     * Retrieves the metadata value associated with the specified field name.
     *
     * @param fieldName the name of the field for which the metadata value is to be retrieved
     * @return the metadata value associated with the given field name, or null if no such value exists
     */
    public String getMetaDataValue(String fieldName) {
        return this.fieldMetaData.get(fieldName);
    }

    /**
     * Retrieves a list of multi-choice route values associated with the specified field name.
     *
     * @param fieldName the name of the field for which multi-choice route values are to be retrieved
     * @return a list of multi-choice route values associated with the given field name, or an empty list if no values exist
     */
    public List<String> getMultiChoiceRouteValues(String fieldName) {
        return getMultiChoiceValues(this.multiChoicesRoute, fieldName);
    }

    public List<String> getSelectedMultiChoiceRouteValues(
            String fieldName, int[] selectedIds
    ) {
        return getSelectedMultiChoiceValues(this.multiChoicesRoute, fieldName, selectedIds);
    }

    public List<Integer> getSelectedMultiChoiceRouteIdValues(
            String fieldName, String[] selectedAliases
    ) {
        //TODO return getSelectedMultiChoiceValues(this.multiChoicesRoute, fieldName, selectedIds);
        return null;
    }

    public List<String> getMultiChoiceFormValues(String fieldName) {
        return getMultiChoiceValues(this.multiChoicesForm, fieldName);
    }

    public List<String> getSelectedMultiChoiceFormValues(
            String fieldName, List<Long> selectedIds
    ) {
        Map<String, Long> forField = this.multiChoicesFormRev.get(fieldName);
        if (forField == null) return new ArrayList<>();

        return forField.entrySet().stream()
                .filter(entry -> selectedIds.contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableMultiChoicesForm(String fieldName) {
        if (this.multiChoicesFormRev == null) return new ArrayList<>();
        Map<String, ?> choices = this.multiChoicesFormRev.get(fieldName);
        return choices != null ? new ArrayList<>(choices.keySet()) : new ArrayList<>();
    }

    public long[] getMultiChoiceFormValues(
            String fieldName, List<String> selectedAliases
    ) {
        if (selectedAliases == null || selectedAliases.isEmpty()) return new long[]{};

        Map<String, Long> forField = this.multiChoicesFormRev.get(fieldName);
        if (forField == null) return new long[]{};

        return selectedAliases.stream()
                .map(forField::get)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .toArray();
    }

    private List<String> getMultiChoiceValues(
            Map<String, Map<Long, String>> multiChoices,
            String fieldName
    ) {
        Map<Long, String> map = multiChoices.get(fieldName);
        return map == null ? new ArrayList<>() : new ArrayList<>(map.values());
    }

    private List<String> getSelectedMultiChoiceValues(
            Map<String, Map<Long, String>> multiChoices,
            String fieldName,
            int[] selectedIds
    ) {
        Map<Long, String> map = multiChoices.get(fieldName);

        List<String> selectedValues = new ArrayList<>();
        for (int id : selectedIds) selectedValues.add(map.get(id));
        return selectedValues;
    }
}
