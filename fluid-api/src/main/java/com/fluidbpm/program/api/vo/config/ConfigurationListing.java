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

package com.fluidbpm.program.api.vo.config;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.fluidbpm.program.api.vo.ABaseListing;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code Configuration}s.
 * </p>
 *
 * @author jasonbruwer
 * @see Configuration
 * @see ABaseListing
 * @since v1.1
 */
public class ConfigurationListing extends ABaseGSONListing<Configuration> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ConfigurationListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ConfigurationListing(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code Configuration} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code Configuration}.
     * @return New {@code Configuration} instance.
     */
    @Override
    @XmlTransient
    public Configuration getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new Configuration(jsonObjectParam);
    }
}
