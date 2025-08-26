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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents what Views a {@code Role} has access to.
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @see Role
 * @since v1.1
 */
@Getter
@Setter
public class RoleToJobView extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private JobView jobView;
    private Role role;

    /**
     * The JSON mapping for the {@code RoleToJobView} object.
     */
    public static class JSONMapping {
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
    public RoleToJobView(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setJobView(this.extractObject(JSONMapping.JOB_VIEW, JobView::new));
        this.setRole(this.extractObject(JSONMapping.ROLE, Role::new));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToJobView}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsObj(JSONMapping.JOB_VIEW, returnVal, this::getJobView);
        this.setAsObj(JSONMapping.ROLE, returnVal, this::getRole);
        return returnVal;
    }
}
