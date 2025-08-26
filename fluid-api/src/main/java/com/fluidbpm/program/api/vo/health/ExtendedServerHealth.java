/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2023] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.health;

import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.health.thirdparty.ExternalRunnerHealth;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Extended server info status for a Fluid instance.
 *
 * @author jasonbruwer on 2023-06-20.
 * @see ABaseFluidGSONObject
 * @since 1.13
 */
@Getter
@Setter
@NoArgsConstructor
public class ExtendedServerHealth extends ABaseFluidGSONObject {
    private Date timestampHealthStart;
    private Date timestampHealthEnd;

    private List<DSHealth> dsHealth;
    private CacheHealth cacheHealth;
    private ESHealth esHealth;
    private List<DSHealth> smtpHealth;
    private List<DSHealth> imapPopHealth;
    private ExternalRunnerHealth externalRunnerHealth;
    private DSHealth contentStorageHealth;

    private DSHealth raygunHealth;
    private DSHealth googleMapsApiHealth;

    /**
     * The JSON mapping for the {@code ExtendedServerHealth} object.
     */
    public static class JSONMapping {
        public static final String TIMESTAMP_START = "timestampHealthStart";
        public static final String TIMESTAMP_END = "timestampHealthEnd";
        public static final String DS_HEALTH = "dsHealth";
        public static final String CACHE_HEALTH = "cacheHealth";
        public static final String ES_HEALTH = "esHealth";
        public static final String SMTP_HEALTH = "smtpHealth";
        public static final String IMAP_HEALTH = "imapPopHealth";
        public static final String EXT_RUNNER_HEALTH = "externalRunnerHealth";
        public static final String CONTENT_HEALTH = "contentStorageHealth";
        public static final String RAYGUN_HEALTH = "raygunHealth";
        public static final String GOOGLE_MAPS_API_HEALTH = "googleMapsApiHealth";
    }

    /**
     * Populates local variables with {@code jsonObject}.
     *
     * @param jsonObject The JSON Object.
     */
    public ExtendedServerHealth(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setTimestampHealthStart(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP_START));
        this.setTimestampHealthEnd(this.getDateFieldValueFromFieldWithName(JSONMapping.TIMESTAMP_END));
        this.setDsHealth(this.extractObjects(JSONMapping.DS_HEALTH, DSHealth::new));
        this.setSmtpHealth(this.extractObjects(JSONMapping.SMTP_HEALTH, DSHealth::new));
        this.setImapPopHealth(this.extractObjects(JSONMapping.IMAP_HEALTH, DSHealth::new));
        this.setCacheHealth(this.extractObject(JSONMapping.CACHE_HEALTH, CacheHealth::new));
        this.setEsHealth(this.extractObject(JSONMapping.ES_HEALTH, ESHealth::new));
        this.setExternalRunnerHealth(this.extractObject(JSONMapping.EXT_RUNNER_HEALTH, ExternalRunnerHealth::new));
        this.setContentStorageHealth(this.extractObject(JSONMapping.CONTENT_HEALTH, DSHealth::new));
        this.setRaygunHealth(this.extractObject(JSONMapping.RAYGUN_HEALTH, DSHealth::new));
        this.setGoogleMapsApiHealth(this.extractObject(JSONMapping.GOOGLE_MAPS_API_HEALTH, DSHealth::new));
    }

    /**
     * Conversion to JsonObject from Java Object.
     *
     * @return JsonObject representation of this object.
     * 
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.TIMESTAMP_START, returnVal, this.getDateAsLongFromJson(this.getTimestampHealthStart()));
        this.setAsProperty(JSONMapping.TIMESTAMP_END, returnVal, this.getDateAsLongFromJson(this.getTimestampHealthEnd()));
        this.setAsObjArray(JSONMapping.DS_HEALTH, returnVal, this::getDsHealth);
        this.setAsObj(JSONMapping.CACHE_HEALTH, returnVal, this::getCacheHealth);
        this.setAsObj(JSONMapping.ES_HEALTH, returnVal, this::getEsHealth);
        this.setAsObj(JSONMapping.EXT_RUNNER_HEALTH, returnVal, this::getExternalRunnerHealth);
        this.setAsObjArray(JSONMapping.SMTP_HEALTH, returnVal, this::getSmtpHealth);
        this.setAsObjArray(JSONMapping.IMAP_HEALTH, returnVal, this::getImapPopHealth);
        this.setAsObj(JSONMapping.CONTENT_HEALTH, returnVal, this::getContentStorageHealth);
        this.setAsObj(JSONMapping.RAYGUN_HEALTH, returnVal, this::getRaygunHealth);
        this.setAsObj(JSONMapping.GOOGLE_MAPS_API_HEALTH, returnVal, this::getGoogleMapsApiHealth);

        return returnVal;
    }
}
