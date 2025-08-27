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

package com.fluidbpm.program.api.vo.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary;
import com.fluidbpm.program.api.vo.webkit.form.WebKitForm;
import com.fluidbpm.program.api.vo.webkit.userquery.WebKitUserQuery;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Fluid Template used to import and export configurations;
 * <p>
 * {@code Flow}'s
 * {@code Form}'s
 * {@code Field}'s
 * {@code UserQuery}'s
 *
 * @author jasonbruwer on 2017/01/14.
 * @see Form
 * @see Field
 * @see com.fluidbpm.program.api.vo.userquery.UserQuery
 * @see com.fluidbpm.program.api.vo.flow.Flow
 * @see com.fluidbpm.program.api.vo.thirdpartylib.ThirdPartyLibrary
 * @see com.fluidbpm.program.api.vo.ABaseFluidGSONObject
 * @since 1.4
 */
@Getter
@Setter
@NoArgsConstructor
public class FluidTemplate extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private String templateName;
    private String templateDescription;
    private String templateComment;

    private List<WebKitForm> formsAndFields;
    private List<WebKitUserQuery> userQueries;
    private List<Flow> flows;

    private List<ThirdPartyLibrary> thirdPartyLibraries;

    private List<Field> userFields;
    private List<Field> routeFields;
    private List<Field> globalFields;

    /**
     * The JSON mapping for the {@code FluidTemplate} object.
     */
    public static final class JSONMapping {
        public static final String TEMPLATE_NAME = "templateName";
        public static final String TEMPLATE_DESCRIPTION = "templateDescription";
        public static final String TEMPLATE_COMMENT = "templateComment";

        public static final String FORMS_AND_FIELDS = "formsAndFields";
        public static final String USER_QUERIES = "userQueries";
        public static final String FLOWS = "flows";

        public static final String THIRD_PARTY_LIBRARIES = "thirdPartyLibraries";

        public static final String USER_FIELDS = "userFields";
        public static final String ROUTE_FIELDS = "routeFields";
        public static final String GLOBAL_FIELDS = "globalFields";

        public static final String VIEW_GROUPS = "webKitViewGroups";
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FluidTemplate(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setTemplateName(this.getAsStringNullSafe(JSONMapping.TEMPLATE_NAME));
        this.setTemplateDescription(this.getAsStringNullSafe(JSONMapping.TEMPLATE_DESCRIPTION));
        this.setTemplateComment(this.getAsStringNullSafe(JSONMapping.TEMPLATE_COMMENT));

        this.setFormsAndFields(this.extractObjects(JSONMapping.FORMS_AND_FIELDS, WebKitForm::new));
        this.setUserQueries(this.extractObjects(JSONMapping.USER_QUERIES, WebKitUserQuery::new));
        this.setFlows(this.extractObjects(JSONMapping.FLOWS, Flow::new));
        this.setThirdPartyLibraries(this.extractObjects(JSONMapping.THIRD_PARTY_LIBRARIES, ThirdPartyLibrary::new));
        this.setUserFields(this.extractObjects(JSONMapping.USER_FIELDS, Field::new));
        this.setRouteFields(this.extractObjects(JSONMapping.ROUTE_FIELDS, Field::new));
        this.setGlobalFields(this.extractObjects(JSONMapping.GLOBAL_FIELDS, Field::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FluidTemplate}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.TEMPLATE_NAME, returnVal, this.getTemplateName());
        this.setAsProperty(JSONMapping.TEMPLATE_DESCRIPTION, returnVal, this.getTemplateDescription());
        this.setAsProperty(JSONMapping.TEMPLATE_COMMENT, returnVal, this.getTemplateComment());
        this.setAsObjArray(JSONMapping.FORMS_AND_FIELDS, returnVal, this::getFormsAndFields);
        this.setAsObjArray(JSONMapping.USER_QUERIES, returnVal, this::getUserQueries);
        this.setAsObjArray(JSONMapping.FLOWS, returnVal, this::getFlows);
        this.setAsObjArray(JSONMapping.THIRD_PARTY_LIBRARIES, returnVal, this::getThirdPartyLibraries);
        this.setAsObjArray(JSONMapping.USER_FIELDS, returnVal, this::getUserFields);
        this.setAsObjArray(JSONMapping.ROUTE_FIELDS, returnVal, this::getRouteFields);
        this.setAsObjArray(JSONMapping.GLOBAL_FIELDS, returnVal, this::getGlobalFields);
        return returnVal;
    }
}
