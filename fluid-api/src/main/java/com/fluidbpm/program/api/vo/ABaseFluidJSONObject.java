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

package com.fluidbpm.program.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *     The Base class for any sub-class that wants to make use of the
 *     JSON based message format used by the Fluid RESTful Web Service.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ABaseFluidVO
 * @see JSONObject
 */
public abstract class ABaseFluidJSONObject extends ABaseFluidVO {

	public static final long serialVersionUID = 1L;
	public static int JSON_INDENT_FACTOR = 2;

	@XmlTransient
	@JsonIgnore
	protected JSONObject jsonObject;

	@XmlTransient
	@JsonIgnore
	public boolean jsonIncludeAll = false;

	private static String DATE_FORMAT_001 = "yyyy-MM-dd'T'HH:mm:sss";//2022-02-07T13:37:22.557Z[UTC]
	private static String DATE_FORMAT_002 = "yyyy-MM-dd'T'HH:mm:ssZ";//2022-02-07T13:37:22.55UTC
	private static String DATE_FORMAT_003 = "yyyy-MM-dd'T'HH:mm:ss";
	private static String DATE_FORMAT_004 = "h:mma";
	private static String DATE_FORMAT_005 = "yyyy-MM-dd";

	private static String[] SUPPORTED_FORMATS = {
			DATE_FORMAT_001, DATE_FORMAT_002, DATE_FORMAT_003, DATE_FORMAT_004,DATE_FORMAT_005
	};

	/**
	 * The JSON mapping for the {@code ABaseFluidJSONObject} object.
	 */
	public static class JSONMapping {
		public static final String ID = "id";
		public static final String SERVICE_TICKET = "serviceTicket";
		public static final String REQUEST_UUID = "requestUuid";
		public static final String ECHO = "echo";

		/**
		 * Elastic specific properties.
		 */
		public static final class Elastic {
			public static final String PROPERTIES = "properties";
			public static final String FORM_INDEX_PREFIX = "form_";
		}
	}

	/**
	 * Default constructor.
	 */
	public ABaseFluidJSONObject() {
		super();
	}

	/**
	 * Populates local variables Id and Service Ticket with {@code jsonObjectParam}.
	 *
	 * @param jsonObjectParam The JSON Object.
	 */
	public ABaseFluidJSONObject(JSONObject jsonObjectParam) {
		this();

		this.jsonObject = jsonObjectParam;
		if (this.jsonObject == null) return;

		//Id...
		if (!this.jsonObject.isNull(JSONMapping.ID)) {
			Object idObject = this.jsonObject.get(JSONMapping.ID);
			if (idObject instanceof Number) {
				//Long Id...
				this.setId(this.jsonObject.getLong(JSONMapping.ID));
			} else if (idObject instanceof String) {
				//String Id...
				String idStr = this.jsonObject.getString(JSONMapping.ID);
				try {
					this.setId(Long.parseLong(idStr));
				} catch (NumberFormatException nfe) {
					this.setId(null);
				}
			} else {
				throw new IllegalArgumentException(
						"Unable to parse Field '"+JSONMapping.ID+"'.");
			}
		}

		//Service Ticket...
		if (!this.jsonObject.isNull(JSONMapping.SERVICE_TICKET)) {
			this.setServiceTicket(this.jsonObject.getString(JSONMapping.SERVICE_TICKET));
		}

		//Request UUID...
		if (!this.jsonObject.isNull(JSONMapping.REQUEST_UUID)) {
			this.setRequestUuid(this.jsonObject.getString(JSONMapping.REQUEST_UUID));
		}

		//Echo...
		if (!this.jsonObject.isNull(JSONMapping.ECHO)) {
			this.setEcho(this.jsonObject.getString(JSONMapping.ECHO));
		}
	}

	/**
	 * <p>
	 * Base {@code toJsonObject} that creates a {@code JSONObject}
	 * with the Id and ServiceTicket set.
	 * </p>
	 *
	 * @return {@code JSONObject} representation of {@code ABaseFluidJSONObject}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see org.json.JSONObject
	 */
	@XmlTransient
	@JsonIgnore
	public JSONObject toJsonObject() throws JSONException {
		JSONObject returnVal = new JSONObject();

		//Id...
		if (this.getId() != null) {
			returnVal.put(JSONMapping.ID,this.getId());
		}

		//Service Ticket...
		if (this.getServiceTicket() != null) {
			returnVal.put(JSONMapping.SERVICE_TICKET, this.getServiceTicket());
		}

		//Request UUID...
		if (this.getRequestUuid() != null) {
			returnVal.put(JSONMapping.REQUEST_UUID, this.getRequestUuid());
		}

		//Echo...
		if (this.getEcho() != null) {
			returnVal.put(JSONMapping.ECHO, this.getEcho());
		}

		return returnVal;
	}

	/**
	 * Converts the {@code Long} timestamp into a {@code Date} object.
	 *
	 * Returns {@code null} if {@code longValueParam} is {@code null}.
	 *
	 * @param longValueParam The milliseconds since January 1, 1970, 00:00:00 GMT
	 * @return {@code Date} Object from {@code longValueParam}
	 *
	 */
	@XmlTransient
	@JsonIgnore
	private Date getLongAsDateFromJson(Long longValueParam) {
		if (longValueParam == null) return null;
		return new Date(longValueParam);
	}

	/**
	 * Retrieves the value of field {@code fieldNameParam} as a timestamp.
	 *
	 * @param fieldNameParam The name of the JSON field to retrieve.
	 * @return The value of the JSON Object as a {@code java.util.Date}.
	 */
	@XmlTransient
	@JsonIgnore
	public Date getDateFieldValueFromFieldWithName(String fieldNameParam) {
		if ((fieldNameParam == null || fieldNameParam.trim().isEmpty()) ||
				(this.jsonObject == null || this.jsonObject.isNull(fieldNameParam))) {
			return null;
		}

		Object objectAtIndex = this.jsonObject.get(fieldNameParam);

		if (objectAtIndex instanceof Number) {
			return this.getLongAsDateFromJson(((Number)objectAtIndex).longValue());
		} else if (objectAtIndex instanceof String) {
			Date validDate = null;
			for (String format : SUPPORTED_FORMATS) {
				try {
					validDate = new SimpleDateFormat(format).parse((String)objectAtIndex);
					if (validDate != null) break;
				} catch (ParseException parseExcept) {
					validDate = null;
				}
			}
			return validDate;
		}
		return null;
	}

	/**
	 * Converts the {@code Date} object into a {@code Long} timestamp.
	 *
	 * Returns {@code null} if {@code dateValueParam} is {@code null}.
	 *
	 * @param dateValueParam {@code Long} Object from {@code dateValueParam}
	 * @return The milliseconds since January 1, 1970, 00:00:00 GMT
	 */
	@XmlTransient
	@JsonIgnore
	public Long getDateAsLongFromJson(Date dateValueParam) {
		if (dateValueParam == null) return null;
		return dateValueParam.getTime();
	}

	/**
	 * Converts the {@code Date} object into a {@code Long} timestamp.
	 *
	 * Returns {@code null} if {@code dateValueParam} is {@code null}.
	 *
	 * @param dateValue {@code Long} Object from {@code dateValueParam}
	 * @return The milliseconds since January 1, 1970, 00:00:00 GMT
	 */
	@XmlTransient
	@JsonIgnore
	public Object getDateAsObjectFromJson(Date dateValue) {
		return this.getDateAsLongFromJson(dateValue);

		//return new SimpleDateFormat(DATE_FORMAT_001).format(dateValue);
		//return dateValue.toString();
	}

	/**
	 * Returns the local JSON object.
	 * Only set through constructor.
	 *
	 * @return The local set {@code JSONObject} object.
	 */
	@XmlTransient
	@JsonIgnore
	public JSONObject getJSONObject() {
		return this.jsonObject;
	}

	/**
	 * Return the Text representation of {@code this} object.
	 *
	 * @return JSON body of {@code this} object.
	 */
	@Override
	@XmlTransient
	@JsonIgnore
	public String toString() {
		JSONObject jsonObject = this.toJsonObject();
		return (jsonObject == null) ? null : jsonObject.toString(JSON_INDENT_FACTOR);
	}
}
