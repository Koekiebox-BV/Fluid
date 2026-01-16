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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**/
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
        this.fieldMetaData = this.setFFMetaData(this.ffMetaData);
    }

    private Map<String, Map<Long, String>> setMCField(List<ASNMultiChoiceField> mcFields) {
        Map<String, Map<Long, String>> returnVal = new HashMap<>();
        if (mcFields == null || mcFields.isEmpty()) return returnVal;

        mcFields.forEach(mcField -> {
            if ((mcField.getMultiChoices() == null || mcField.getMultiChoices().isEmpty()) ||
                    UtilGlobal.isBlank(mcField.getFieldName())) return;

            String fieldName = mcField.getFieldName();
            Map<Long, String> mcToMap = new HashMap<>();
            mcField.getMultiChoices().forEach(mcChoice -> {
                mcToMap.put(mcChoice.getId(), mcChoice.getAlias());
            });
            returnVal.put(fieldName, mcToMap);
        });
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

    public String getMetaDataValue(String fieldName) {
        if (true) return null;//TODO fix
        return this.fieldMetaData.get(fieldName);
    }

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

    public String getSelectedMultiChoiceFormValue(
            String fieldName, int selectedId
    ) {
        return null;//TODO 
    }

    public List<String> getAvailableMultiChoicesForm(String fieldName) {
        return null;
    }

    public List<String> getSelectedMultiChoiceFormValues(
            String fieldName, int[] selectedIds
    ) {
        return getSelectedMultiChoiceValues(this.multiChoicesForm, fieldName, selectedIds);
    }

    public int[] getMultiChoiceFormValues(
            String fieldName, List<String> selectedAliases
    ) {
        return new int[]{};
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
