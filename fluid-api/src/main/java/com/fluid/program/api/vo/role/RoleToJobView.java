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

package com.fluid.program.api.vo.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.Form;

/**
 * <p>
 *     Represents what Views a {@code Role} has access to.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Form
 * @see Role
 */
public class RoleToJobView extends ABaseFluidJSONObject {

    private String rule;
    private String viewName;

    /**
     * The JSON mapping for the {@code RoleToJobView} object.
     */
    public static class JSONMapping
    {
        public static final String RULE = "rule";
        public static final String VIEW_NAME = "viewName";
    }

    /**
     * Default constructor.
     */
    public RoleToJobView() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RoleToJobView(JSONObject jsonObjectParam){
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

        return returnVal;
    }
}
