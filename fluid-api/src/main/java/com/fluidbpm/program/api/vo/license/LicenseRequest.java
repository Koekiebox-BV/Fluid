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

package com.fluidbpm.program.api.vo.license;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * License request for Fluid.
 *
 * @author jasonbruwer on 2018-04-29
 * @see ABaseFluidVO
 * @since v1.8
 */
@Getter
@Setter
public class LicenseRequest extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String machineName;
    private String licenseType;
    private String licenseCipherText;
    private Integer userCount;
    private Date dateValidFrom;
    private Date dateValidTo;

    /**
     * The JSON mapping for the {@code LicenseRequest} object.
     */
    public static class JSONMapping {
        public static final String MACHINE_NAME = "machineName";
        public static final String LICENSE_TYPE = "licenseType";
        public static final String USER_COUNT = "userCount";
        public static final String DATE_VALID_FROM = "dateValidFrom";
        public static final String DATE_VALID_TO = "dateValidTo";
        public static final String LICENSE_CIPHER_TEXT = "licenseCipherText";
    }

    /**
     * Default constructor.
     */
    public LicenseRequest() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public LicenseRequest(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setMachineName(this.getAsStringNullSafe(JSONMapping.MACHINE_NAME));
        this.setLicenseType(this.getAsStringNullSafe(JSONMapping.LICENSE_TYPE));
        this.setLicenseCipherText(this.getAsStringNullSafe(JSONMapping.LICENSE_CIPHER_TEXT));
        this.setUserCount(this.getAsIntegerNullSafe(JSONMapping.USER_COUNT));
        this.setDateValidFrom(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_VALID_FROM));
        this.setDateValidTo(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_VALID_TO));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code LicenseRequest}
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.MACHINE_NAME, returnVal, this.getMachineName());
        this.setAsProperty(JSONMapping.LICENSE_CIPHER_TEXT, returnVal, this.getLicenseCipherText());
        this.setAsProperty(JSONMapping.LICENSE_TYPE, returnVal, this.getLicenseType());
        this.setAsProperty(JSONMapping.USER_COUNT, returnVal, this.getUserCount());
        this.setAsProperty(JSONMapping.DATE_VALID_FROM, returnVal, (this.getDateValidFrom()));
        this.setAsProperty(JSONMapping.DATE_VALID_TO, returnVal, (this.getDateValidTo()));

        return returnVal;
    }
}
