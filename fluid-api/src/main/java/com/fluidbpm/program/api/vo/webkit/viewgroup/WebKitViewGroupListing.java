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

package com.fluidbpm.program.api.vo.webkit.viewgroup;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code WebKitViewGroup}s.
 * </p>
 *
 * @author jasonbruwer
 * @see WebKitViewGroup
 * @since v1.1
 */
public class WebKitViewGroupListing extends ABaseGSONListing<WebKitViewGroup> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public WebKitViewGroupListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public WebKitViewGroupListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code WebKitViewGroup} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code WebKitViewGroup}.
     * @return New {@code WebKitViewGroup} instance.
     */
    @Override
    @XmlTransient
    public WebKitViewGroup getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new WebKitViewGroup(jsonObjectParam);
    }
}
