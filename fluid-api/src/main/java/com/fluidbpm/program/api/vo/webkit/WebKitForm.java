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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import org.json.JSONObject;

public class WebKitForm extends ABaseFluidJSONObject {
	private String inputLayout;
	private Boolean displayDescription;

	/**
	 * The type of InputLayout.
	 *
	 */
	public static final class InputLayout {
		public static final String VERTICAL = "vertical";
		public static final String HORIZONTAL = "horizontal";
		public static final String ADVANCED = "advanced";
	}

	/**
	 * The JSON mapping for the {@code WebKitForm} object.
	 */
	public static class JSONMapping {
		public static final String INPUT_LAYOUT = "inputLayout";
		public static final String DISPLAY_DESCRIPTION = "displayDescription";

	}

	@Override
	public JSONObject getJSONObject() {
		return super.getJSONObject();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
