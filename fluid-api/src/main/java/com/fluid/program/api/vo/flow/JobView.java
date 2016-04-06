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

package com.fluid.program.api.vo.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * <p>
 *     Represents a {@code JobView}.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see com.fluid.program.api.vo.role.RoleToJobView
 */
public class JobView extends ABaseFluidJSONObject {

    private String rule;
    private String viewName;
    private String viewType;

    /**
     * The JSON mapping for the {@code RoleToJobView} object.
     */
    public static class JSONMapping
    {
        public static final String RULE = "rule";
        public static final String VIEW_NAME = "viewName";
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
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public JobView(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Rule...
        if (!this.jsonObject.isNull(JSONMapping.RULE)) {
            this.setRule(this.jsonObject.getString(JSONMapping.RULE));
        }

        //View Name...
        if (!this.jsonObject.isNull(JSONMapping.VIEW_NAME)) {
            this.setViewName(this.jsonObject.getString(JSONMapping.VIEW_NAME));
        }

        //View Type...
        if (!this.jsonObject.isNull(JSONMapping.VIEW_TYPE)) {
            this.setViewType(this.jsonObject.getString(JSONMapping.VIEW_TYPE));
        }
    }

    /**
     * Gets the Rule for the View.
     *
     * @return View Rule.
     */
    public String getRule() {
        return this.rule;
    }

    /**
     * Sets the Rule for the View.
     *
     * @param ruleParam View Rule.
     */
    public void setRule(String ruleParam) {
        this.rule = ruleParam;
    }

    /**
     * Gets the Name of the View.
     *
     * @return View Name.
     */
    public String getViewName() {
        return this.viewName;
    }

    /**
     * Gets the Name of the View.
     *
     * @param viewNameParam View Name.
     */
    public void setViewName(String viewNameParam) {
        this.viewName = viewNameParam;
    }

    /**
     * Gets the Type of the View.
     *
     * @return Type Name.
     */
    public String getViewType() {
        return this.viewType;
    }

    /**
     * Gets the Type of the View.
     *
     * @param viewTypeParam Type Name.
     */
    public void setViewType(String viewTypeParam) {
        this.viewType = viewTypeParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToJobView}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Rule...
        if(this.getRule() != null)
        {
            returnVal.put(JSONMapping.RULE, this.getRule());
        }

        //View Name...
        if(this.getViewName() != null)
        {
            returnVal.put(JSONMapping.VIEW_NAME, this.getViewName());
        }

        //View Type...
        if(this.getViewType() != null)
        {
            returnVal.put(JSONMapping.VIEW_TYPE, this.getViewType());
        }

        return returnVal;
    }
}
