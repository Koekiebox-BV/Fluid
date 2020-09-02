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

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Fluid configurations.
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see ABaseFluidJSONObject
 */
@XmlRootElement
public class Configuration extends ABaseFluidJSONObject {
	public static final long serialVersionUID = 1L;

	private String key;
	private String value;

	/**
	 * The JSON mapping for the {@code Configuration} object.
	 */
	public static class JSONMapping {
		public static final String KEY = "key";
		public static final String VALUE = "value";
	}

	/**
	 * Default constructor.
	 */
	public Configuration() {
		super();
	}

	/**
	 * Sets the key and value.
	 * @param configKey The config key.
	 * @param configValue The value of the config.
	 */
	public Configuration(String configKey, String configValue) {
		super();
		this.setKey(configKey);
		this.setValue(configValue);
	}

	/**
	 * The unique Flow identifier.
	 *
	 * @param flowIdParam The Flow primary key.
	 */
	public Configuration(Long flowIdParam) {
		super();
		this.setId(flowIdParam);
	}

	/**
	 * Populates local variables with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public Configuration(JSONObject jsonObjectParam) {
		super(jsonObjectParam);

		if (this.jsonObject == null) {
			return;
		}

		//Key...
		if (!this.jsonObject.isNull(JSONMapping.KEY)) {
			this.setKey(this.jsonObject.getString(JSONMapping.KEY));
		}

		//Value...
		if (!this.jsonObject.isNull(JSONMapping.VALUE)) {
			this.setValue(this.jsonObject.getString(JSONMapping.VALUE));
		}
	}

	/**
	 * Gets the key of the Config.
	 *
	 * @return Config value.
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Sets the Key of the Config.
	 *
	 * @param keyParam Config value.
	 */
	public void setKey(String keyParam) {
		this.key = keyParam;
	}

	/**
	 * Gets the value of the Config.
	 *
	 * @return Flow name.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the Value of the Config.
	 *
	 * @param valueParam Config value.
	 */
	public void setValue(String valueParam) {
		this.value = valueParam;
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code Flow}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = super.toJsonObject();

		//Key...
		if (this.getKey() != null) {
			returnVal.put(JSONMapping.KEY,this.getKey());
		}

		//Value...
		if (this.getValue() != null) {
			returnVal.put(JSONMapping.VALUE, this.getValue());
		}

		return returnVal;
	}
}
