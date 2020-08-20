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

package com.fluidbpm.program.api.vo.webkit;

import com.fluidbpm.program.api.vo.ABaseListing;
import com.fluidbpm.program.api.vo.role.Role;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

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
public class WebKitFormListing extends ABaseListing<Role> {
    public static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public WebKitFormListing() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public WebKitFormListing(JSONObject jsonObjectParam){
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
