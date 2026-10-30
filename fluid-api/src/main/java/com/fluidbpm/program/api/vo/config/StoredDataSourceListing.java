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
import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code StoredDataSource}s.
 * </p>
 *
 * @author jasonbruwer
 * @see StoredDataSource
 * @since v1.1
 */
public class StoredDataSourceListing extends ABaseGSONListing<StoredDataSource> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public StoredDataSourceListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public StoredDataSourceListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code StoredDataSource} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code StoredDataSource}.
     * @return New {@code StoredDataSource} instance.
     */
    @Override
    @XmlTransient
    public StoredDataSource getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new StoredDataSource(jsonObjectParam);
    }
}
