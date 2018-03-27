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

package com.fluidbpm.program.api.vo.role;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;

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

    public static final long serialVersionUID = 1L;

    private JobView jobView;
    private Role role;

    /**
     * The JSON mapping for the {@code RoleToJobView} object.
     */
    public static class JSONMapping
    {
        public static final String JOB_VIEW = "jobView";
        public static final String ROLE = "role";
    }

    /**
     * Default constructor.
     */
    public RoleToJobView() {
        super();
    }

    /**
     * Sets the Id associated with a 'Role To Job View'.
     *
     * @param roleToJobViewIdParam RoleToJobView Id.
     */
    public RoleToJobView(Long roleToJobViewIdParam) {
        super();

        this.setId(roleToJobViewIdParam);
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

        //Job View...
        if (!this.jsonObject.isNull(JSONMapping.JOB_VIEW)) {
            this.setJobView(new JobView(this.jsonObject.getJSONObject(
                    JSONMapping.JOB_VIEW)));
        }

        //Role...
        if (!this.jsonObject.isNull(JSONMapping.ROLE)) {
            this.setRole(new Role(this.jsonObject.getJSONObject(
                    JSONMapping.ROLE)));
        }
    }

    /**
     * Gets the View.
     *
     * @return View Rule.
     */
    public JobView getJobView() {
        return this.jobView;
    }

    /**
     * Sets the Role for the View.
     *
     * @param jobViewParam View Rule.
     */
    public void setJobView(JobView jobViewParam) {
        this.jobView = jobViewParam;
    }

    /**
     * Gets the Role for the View.
     *
     * @return View Rule.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Sets the Role for the View.
     *
     * @param roleParam View Rule.
     */
    public void setRole(Role roleParam) {
        this.role = roleParam;
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

        //Job View...
        if(this.getJobView() != null)
        {
            returnVal.put(JSONMapping.JOB_VIEW,
                    this.getJobView().toJsonObject());
        }

        //Role...
        if(this.getRole() != null)
        {
            returnVal.put(JSONMapping.ROLE,
                    this.getRole().toJsonObject());
        }

        return returnVal;
    }
}
