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

package com.fluidbpm.program.api.vo.thirdpartylib;

import com.fluidbpm.program.api.vo.ABaseListing;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code ThirdPartyLibraryTaskIdentifier}s.
 * </p>
 *
 * @author jasonbruwer
 * @see ThirdPartyLibraryTaskIdentifier
 * @see ABaseListing
 * @since v1.11
 */
public class ThirdPartyLibraryTaskIdentifierListing extends ABaseListing<ThirdPartyLibraryTaskIdentifier> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ThirdPartyLibraryTaskIdentifierListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ThirdPartyLibraryTaskIdentifierListing(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code ThirdPartyLibraryTaskIdentifier} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code ThirdPartyLibraryTaskIdentifier}.
     * @return New {@code ThirdPartyLibraryTaskIdentifier} instance.
     */
    @Override
    @XmlTransient
    public ThirdPartyLibraryTaskIdentifier getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new ThirdPartyLibraryTaskIdentifier(jsonObjectParam);
    }
}
