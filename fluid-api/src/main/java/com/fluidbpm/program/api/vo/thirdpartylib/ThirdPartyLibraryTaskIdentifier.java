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

package com.fluidbpm.program.api.vo.thirdpartylib;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.IEnum;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * 3rd Party library Task Identifier information.
 *
 * @author jasonbruwer on 2021-01-16
 * @see ABaseFluidGSONObject
 * @since v1.11
 */
@Getter
@Setter
public class ThirdPartyLibraryTaskIdentifier extends ABaseFluidGSONObject {
    private String libraryFilename;
    private String librarySha256sum;
    private String libraryDescription;

    private ThirdPartyLibraryTaskType thirdPartyLibraryTaskType;
    private String taskIdentifier;
    private List<Form> formDefinitions;
    private Long thirdPartyLibraryId;

    /**
     * Types of 3rd party programs.
     */
    public enum ThirdPartyLibraryTaskType implements IEnum {
        CustomProgram,
        CustomWebAction,
        CustomScheduledAction,
        CalculatedFieldAction
    }

    /**
     * The JSON mapping for the {@code ThirdPartyLibraryTaskIdentifier} object.
     */
    public static class JSONMapping {
        public static final String LIBRARY_FILENAME = "libraryFilename";
        public static final String LIBRARY_SHA256SUM = "librarySha256sum";
        public static final String LIBRARY_DESCRIPTION = "libraryDescription";

        public static final String THIRD_PARTY_LIBRARY_TASK_TYPE = "thirdPartyLibraryTaskType";
        public static final String TASK_IDENTIFIER = "taskIdentifier";
        public static final String FORM_DEFINITIONS = "formDefinitions";
        public static final String THIRD_PARTY_LIBRARY_ID = "thirdPartyLibraryId";
    }

    /**
     * Sets the Id associated with a Third party library.
     *
     * @param thirdPartyLibIdParam Third Party Library Id.
     */
    public ThirdPartyLibraryTaskIdentifier(Long thirdPartyLibIdParam) {
        super();
        this.setId(thirdPartyLibIdParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ThirdPartyLibraryTaskIdentifier(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setLibraryFilename(this.getAsStringNullSafe(JSONMapping.LIBRARY_FILENAME));
        this.setLibrarySha256sum(this.getAsStringNullSafe(JSONMapping.LIBRARY_SHA256SUM));
        this.setLibraryDescription(this.getAsStringNullSafe(JSONMapping.LIBRARY_DESCRIPTION));
        this.setTaskIdentifier(this.getAsStringNullSafe(JSONMapping.TASK_IDENTIFIER));
        this.setThirdPartyLibraryId(this.getAsLongNullSafe(JSONMapping.THIRD_PARTY_LIBRARY_ID));

        String taskTypeStr = this.getAsStringNullSafe(JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE);
        if (taskTypeStr != null && !taskTypeStr.isEmpty()) {
            this.setThirdPartyLibraryTaskType(ThirdPartyLibraryTaskType.valueOf(taskTypeStr));
        }
        this.setFormDefinitions(this.extractObjects(JSONMapping.FORM_DEFINITIONS, Form::new));
    }

    /**
     * Default constructor.
     */
    public ThirdPartyLibraryTaskIdentifier() {
        super();
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code ThirdPartyLibraryTaskIdentifier}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.THIRD_PARTY_LIBRARY_ID, returnVal, this.getThirdPartyLibraryId());
        this.setAsProperty(JSONMapping.LIBRARY_FILENAME, returnVal, this.getLibraryFilename());
        this.setAsProperty(JSONMapping.LIBRARY_SHA256SUM, returnVal, this.getLibrarySha256sum());
        this.setAsProperty(JSONMapping.LIBRARY_DESCRIPTION, returnVal, this.getLibraryDescription());
        this.setAsProperty(JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE, returnVal, this.getThirdPartyLibraryTaskType());
        this.setAsProperty(JSONMapping.TASK_IDENTIFIER, returnVal, this.getTaskIdentifier());
        this.setAsObjArray(JSONMapping.FORM_DEFINITIONS, returnVal, this::getFormDefinitions);

        if (this.getThirdPartyLibraryId() != null)
            returnVal.addProperty(JSONMapping.THIRD_PARTY_LIBRARY_ID, this.getThirdPartyLibraryId());
        if (this.getLibraryFilename() != null) {
            returnVal.addProperty(JSONMapping.LIBRARY_FILENAME, this.getLibraryFilename());
        }
        if (this.getLibrarySha256sum() != null)
            returnVal.addProperty(JSONMapping.LIBRARY_SHA256SUM, this.getLibrarySha256sum());
        if (this.getLibraryDescription() != null)
            returnVal.addProperty(JSONMapping.LIBRARY_DESCRIPTION, this.getLibraryDescription());
        if (this.getThirdPartyLibraryTaskType() != null) {
            returnVal.addProperty(JSONMapping.THIRD_PARTY_LIBRARY_TASK_TYPE, this.getThirdPartyLibraryTaskType().toString());
        }
        if (this.getTaskIdentifier() != null) {
            returnVal.addProperty(JSONMapping.TASK_IDENTIFIER, this.getTaskIdentifier());
        }
        if (this.getFormDefinitions() != null && !this.getFormDefinitions().isEmpty()) {
            returnVal.add(JSONMapping.FORM_DEFINITIONS, this.toJsonObjArray(this.getFormDefinitions()));
        }
        return returnVal;
    }
}
