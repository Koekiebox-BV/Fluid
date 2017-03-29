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

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code Role}s.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see Role
 * @see ABaseListing
 */
public class RoleListing extends ABaseListing<Role> {

    public static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public RoleListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public RoleListing(JSONObject jsonObjectParam){
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code Role} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code Role}.
     * @return New {@code Role} instance.
     */
    @Override
    @XmlTransient
    public Role getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new Role(jsonObjectParam);
    }
}
