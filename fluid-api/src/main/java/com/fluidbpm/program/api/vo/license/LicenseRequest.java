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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * License request for Fluid.
 *
 * @author jasonbruwer on 2018-04-29
 * @see ABaseFluidVO
 * @since v1.8
 */
public class LicenseRequest extends ABaseFluidJSONObject {
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
    public LicenseRequest(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if (this.jsonObject == null) {
            return;
        }

        //Machine Name...
        if (!this.jsonObject.isNull(JSONMapping.MACHINE_NAME)) {
            this.setMachineName(this.jsonObject.getString(
                    JSONMapping.MACHINE_NAME));
        }

        //License Type...
        if (!this.jsonObject.isNull(JSONMapping.LICENSE_TYPE)) {
            this.setLicenseType(this.jsonObject.getString(
                    JSONMapping.LICENSE_TYPE));
        }

        //License Cipher Text...
        if (!this.jsonObject.isNull(JSONMapping.LICENSE_CIPHER_TEXT)) {
            this.setLicenseCipherText(this.jsonObject.getString(
                    JSONMapping.LICENSE_CIPHER_TEXT));
        }

        //User Count...
        if (!this.jsonObject.isNull(JSONMapping.USER_COUNT)) {
            this.setUserCount(this.jsonObject.getInt(
                    JSONMapping.USER_COUNT));
        }

        //Date Valid From...
        this.setDateValidFrom(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.DATE_VALID_FROM));

        //Date Valid To...
        this.setDateValidTo(
                this.getDateFieldValueFromFieldWithName(
                        JSONMapping.DATE_VALID_TO));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code LicenseRequest}
     * @throws JSONException If there is a problem with the JSON Body.
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnVal = super.toJsonObject();

        //Machine Name...
        if (this.getMachineName() != null) {
            returnVal.put(JSONMapping.MACHINE_NAME,
                    this.getMachineName());
        }

        //Cipher Text...
        if (this.getLicenseCipherText() != null) {
            returnVal.put(JSONMapping.LICENSE_CIPHER_TEXT,
                    this.getLicenseCipherText());
        }

        //License Type...
        if (this.getLicenseType() != null) {
            returnVal.put(JSONMapping.LICENSE_TYPE,
                    this.getLicenseType());
        }

        //User Count...
        if (this.getUserCount() != null) {
            returnVal.put(JSONMapping.USER_COUNT,
                    this.getUserCount());
        }

        //Date Valid From...
        if (this.getDateValidFrom() != null) {
            returnVal.put(JSONMapping.DATE_VALID_FROM,
                    this.getDateAsObjectFromJson(this.getDateValidFrom()));
        }

        //Date Valid To...
        if (this.getDateValidTo() != null) {
            returnVal.put(JSONMapping.DATE_VALID_TO,
                    this.getDateAsObjectFromJson(this.getDateValidTo()));
        }

        return returnVal;
    }

    /**
     * Gets the Machine name.
     *
     * @return Machine name.
     */
    public String getMachineName() {
        return this.machineName;
    }

    /**
     * Sets the Machine Name.
     *
     * @param machineNameParam The name of the machine.
     */
    public void setMachineName(String machineNameParam) {
        this.machineName = machineNameParam;
    }

    /**
     * Gets the License type.
     *
     * @return License type.
     */
    public String getLicenseType() {
        return this.licenseType;
    }

    /**
     * Sets the license type.
     *
     * @param licenseTypeParam The type of license.
     */
    public void setLicenseType(String licenseTypeParam) {
        this.licenseType = licenseTypeParam;
    }

    /**
     * Gets the user count.
     *
     * @return The number of users.
     */
    public Integer getUserCount() {
        return this.userCount;
    }

    /**
     * Sets the user count.
     *
     * @param userCountParam The number of users.
     */
    public void setUserCount(Integer userCountParam) {
        this.userCount = userCountParam;
    }

    /**
     * Gets the Valid From date.
     *
     * @return The from date.
     */
    public Date getDateValidFrom() {
        return this.dateValidFrom;
    }

    /**
     * Sets the Valid From date.
     *
     * @param dateValidFromParam The from date.
     */
    public void setDateValidFrom(Date dateValidFromParam) {
        this.dateValidFrom = dateValidFromParam;
    }

    /**
     * Gets the Valid to date.
     *
     * @return The to date.
     */
    public Date getDateValidTo() {
        return this.dateValidTo;
    }

    /**
     * Sets the Valid to date.
     *
     * @param dateValidToParam The to date.
     */
    public void setDateValidTo(Date dateValidToParam) {
        this.dateValidTo = dateValidToParam;
    }

    /**
     * Gets the license cipher text.
     *
     * @return The license cypher text.
     */
    public String getLicenseCipherText() {
        return this.licenseCipherText;
    }

    /**
     * Sets the license cipher text.
     *
     * @param licenseCipherTextParam The license cypher text.
     */
    public void setLicenseCipherText(String licenseCipherTextParam) {
        this.licenseCipherText = licenseCipherTextParam;
    }
}
