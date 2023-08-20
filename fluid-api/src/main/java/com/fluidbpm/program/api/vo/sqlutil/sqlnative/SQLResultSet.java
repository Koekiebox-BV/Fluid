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

package com.fluidbpm.program.api.vo.sqlutil.sqlnative;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code SQLRow}s.
 * </p>
 *
 * @author jasonbruwer on 2018-05-26
 * @since v1.8
 *
 * @see SQLRow
 * @see ABaseListing
 */
public class SQLResultSet extends ABaseListing<SQLRow> {

	public static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SQLResultSet() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public SQLResultSet(JSONObject jsonObjectParam){
		super(jsonObjectParam);
	}

	/**
	 * Converts the {@code jsonObjectParam} to a {@code SQLRow} object.
	 *
	 * @param jsonObjectParam The JSON object to convert to {@code SQLRow}.
	 * @return New {@code SQLRow} instance.
	 */
	@Override
	@XmlTransient
	public SQLRow getObjectFromJSONObject(JSONObject jsonObjectParam) {
		return new SQLRow(jsonObjectParam);
	}
}
