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

package com.fluidbpm.program.api.vo.historic;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code FormFlowHistoricData}s.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.8
 *
 * @see FormFlowHistoricData
 * @see ABaseListing
 */
public class FormFlowHistoricDataListing extends ABaseListing<FormFlowHistoricData> {

	public static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FormFlowHistoricDataListing() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FormFlowHistoricDataListing(JSONObject jsonObjectParam){
		super(jsonObjectParam);
	}

	/**
	 * Converts the {@code jsonObjectParam} to a {@code FormFlowHistoricData} object.
	 *
	 * @param jsonObjectParam The JSON object to convert to {@code FormFlowHistoricData}.
	 * @return New {@code FormFlowHistoricData} instance.
	 */
	@Override
	@XmlTransient
	public FormFlowHistoricData getObjectFromJSONObject(JSONObject jsonObjectParam) {
		return new FormFlowHistoricData(jsonObjectParam);
	}
}
