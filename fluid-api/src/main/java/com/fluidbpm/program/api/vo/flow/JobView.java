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

package com.fluidbpm.program.api.vo.flow;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * Represents a {@code JobView}.
 * </p>
 *
 * @author jasonbruwer
 * @version v1.8
 * @see com.fluidbpm.program.api.vo.role.RoleToJobView
 * @since v1.1
 */
@Getter
@Setter
public class JobView extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private String rule;
    private String viewName;
    private String viewGroupName;
    private String viewStepName;
    private String viewFlowName;

    private Integer viewPriority;
    private Long viewOrder;
    private String viewType;

    /**
     * The JSON mapping for the {@code RoleToJobView} object.
     */
    public static class JSONMapping {
        public static final String RULE = "rule";
        public static final String VIEW_NAME = "viewName";
        public static final String VIEW_GROUP_NAME = "viewGroupName";
        public static final String VIEW_STEP_NAME = "viewStepName";
        public static final String VIEW_FLOW_NAME = "viewFlowName";

        public static final String VIEW_PRIORITY = "viewPriority";
        public static final String VIEW_ORDER = "viewOrder";
        public static final String VIEW_TYPE = "viewType";
    }

    /**
     * Default constructor.
     */
    public JobView() {
        super();
    }

    /**
     * Sets the Id associated with a 'Job View'.
     *
     * @param jobViewIdParam RoleToJobView Id.
     */
    public JobView(Long jobViewIdParam) {
        super();
        this.setId(jobViewIdParam);
    }

    /**
     * Sets the Name associated with a 'Job View'.
     *
     * @param jobViewName Job View name.
     */
    public JobView(String jobViewName) {
        super();
        this.setViewName(jobViewName);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public JobView(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setRule(this.getAsStringNullSafe(JSONMapping.RULE));
        this.setViewName(this.getAsStringNullSafe(JSONMapping.VIEW_NAME));
        this.setViewGroupName(this.getAsStringNullSafe(JSONMapping.VIEW_GROUP_NAME));
        this.setViewFlowName(this.getAsStringNullSafe(JSONMapping.VIEW_FLOW_NAME));
        this.setViewStepName(this.getAsStringNullSafe(JSONMapping.VIEW_STEP_NAME));
        this.setViewPriority(this.getAsIntegerNullSafe(JSONMapping.VIEW_PRIORITY));
        this.setViewOrder(this.getAsLongNullSafe(JSONMapping.VIEW_ORDER));
        this.setViewType(this.getAsStringNullSafe(JSONMapping.VIEW_TYPE));
    }

    /**
     * Gets the Name of the View Group.
     *
     * @return View Name.
     */
    public String getViewGroupName() {
        return (this.viewGroupName == null) ? UtilGlobal.EMPTY : this.viewGroupName;
    }

    /**
     * Gets the priority in the Group for the View.
     * @return View Priority.
     */
    public Integer getViewPriority() {
        return (this.viewPriority == null) ? 0 : this.viewPriority;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToJobView}
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.RULE, returnVal, this.getRule());
        this.setAsProperty(JSONMapping.VIEW_NAME, returnVal, this.getViewName());
        this.setAsProperty(JSONMapping.VIEW_GROUP_NAME, returnVal, this.getViewGroupName());
        this.setAsProperty(JSONMapping.VIEW_FLOW_NAME, returnVal, this.getViewFlowName());
        this.setAsProperty(JSONMapping.VIEW_STEP_NAME, returnVal, this.getViewStepName());
        this.setAsProperty(JSONMapping.VIEW_PRIORITY, returnVal, this.getViewPriority());
        this.setAsProperty(JSONMapping.VIEW_ORDER, returnVal, this.getViewOrder());
        this.setAsProperty(JSONMapping.VIEW_TYPE, returnVal, this.getViewType());
        return returnVal;
    }
}
