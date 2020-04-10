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

package com.fluidbpm.program.api.vo.flow;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code FlowStepRule}s.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.6
 *
 * @see FlowStepRule
 * @see ABaseListing
 */
public class FlowStepRuleListing extends ABaseListing<FlowStepRule> {

	public static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FlowStepRuleListing() {
		super();
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public FlowStepRuleListing(JSONObject jsonObjectParam){
		super(jsonObjectParam);
	}

	/**
	 * Converts the {@code jsonObjectParam} to a {@code JobView} object.
	 *
	 * @param jsonObjectParam The JSON object to convert to {@code JobView}.
	 * @return New {@code JobView} instance.
	 */
	@Override
	@XmlTransient
	public FlowStepRule getObjectFromJSONObject(JSONObject jsonObjectParam) {
		return new FlowStepRule(jsonObjectParam);
	}
}
