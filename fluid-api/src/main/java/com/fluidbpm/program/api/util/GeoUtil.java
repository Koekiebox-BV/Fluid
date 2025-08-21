/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2021] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Geo utility to set Latitude and Longitude based data for maps.
 * The Geo utility may be utilized by any client to correctly supply lat, long, radius and address info as JSON text.
 * @since 1.12
 */
public class GeoUtil {
	private JsonObject json;

	/**
	 * Mapping field names for the JSON address based field.
	 */
	public static final class JSONMapping {
		public static final String LAT = "lat";//Latitude
		public static final String LON = "lon";//Longitude
		public static final String ADDR = "addr";//Address
		public static final String RADIUS = "rad";//Radius
	}
	
	/**
	 * Create an empty JSON state.
	 */
	public GeoUtil() {
		this(UtilGlobal.EMPTY);
	}
	
	/**
	 * Set {@code json} content based on {@code existingJson}.
	 * @param existingJson The JSON String.
	 */
	public GeoUtil(String existingJson) {
		if (UtilGlobal.isBlank(existingJson)) this.json = new JSONObject();
		else {
			try {
				this.json = new JSONObject(existingJson);
			} catch (JSONException jsonErr) {
				this.json = new JSONObject();
				String[] existingVals = existingJson.split("\\|");
				if (existingVals != null && existingVals.length > 1) {
					this.setLatitude(Double.valueOf(existingVals[0]));
					this.setLongitude(Double.valueOf(existingVals[1]));

					if (existingVals.length > 2) this.setAddress(existingVals[2]);
				} else this.setAddress(existingJson);
			}
		}
	}

	/**
	 * Set the latitude on the JSON. {@code null} allowed.
	 * @param latitude The latitude.
	 */
	public void setLatitude(Double latitude) {
		if (latitude == null) {
			this.json.put(JSONMapping.LAT, JSONObject.NULL);
			return;
		}
		this.json.put(JSONMapping.LAT, latitude);
	}

	/**
	 * Fetch the latitude from JSON.
	 * @return The latitude or {@code 0.0} if not set.
	 */
	public Double getLatitude() {
		if (this.json.has(JSONMapping.LAT) && !this.json.isNull(JSONMapping.LAT)) return this.json.getDouble(JSONMapping.LAT);
		return 0.0;
	}

	/**
	 * Set the longitude on the JSON. {@code null} allowed.
	 * @param longitude The longitude.
	 */
	public void setLongitude(Double longitude) {
		if (longitude == null) {
			this.json.put(JSONMapping.LON, JSONObject.NULL);
			return;
		}
		this.json.put(JSONMapping.LON, longitude);
	}

	/**
	 * Fetch the longitude from JSON.
	 *
	 * @return The longitude or {@code 0.0} if not set.
	 */
	public Double getLongitude() {
		if (this.json.has(JSONMapping.LON) && !this.json.isNull(JSONMapping.LON)) return this.json.getDouble(JSONMapping.LON);
		return 0.0;
	}

	/**
	 * Set the address on the JSON. {@code null} allowed.
	 * @param address The address.
	 */
	public void setAddress(String address) {
		if (address == null) {
			this.json.put(JSONMapping.ADDR, JSONObject.NULL);
			return;
		}

		if (address.length() > 150) address = address.substring(0, 150);

		this.json.put(JSONMapping.ADDR, address);
	}

	/**
	 * Fetch the address from JSON.
	 *
	 * @return The address or {@code empty} if not set.
	 */
	public String getAddress() {
		if (this.json.has(JSONMapping.ADDR) && !this.json.isNull(JSONMapping.ADDR)) return this.json.getString(JSONMapping.ADDR);
		return UtilGlobal.EMPTY;
	}

	/**
	 * Set the radius on the JSON. {@code null} allowed.
	 * @param radius The radius.
	 */
	public void setRadius(Double radius) {
		if (radius == null) {
			this.json.put(JSONMapping.RADIUS, JSONObject.NULL);
			return;
		}
		this.json.put(JSONMapping.RADIUS, radius);
	}

	/**
	 * Fetch the radius from JSON.
	 * @return The radius or {@code 0.0} if not set.
	 */
	public Double getRadius() {
		if (this.json.has(JSONMapping.RADIUS) && !this.json.isNull(JSONMapping.RADIUS)) return this.json.getDouble(JSONMapping.RADIUS);
		return 0.0;
	}

	/**
	 * Verify whether latitude or longitude is not set.
	 * @return {@code true} if latitude or longitude is not set.
	 */
	public boolean isLatOrLonNotSet() {
		double lat = this.getLatitude(), lon = this.getLongitude();
		return (lat == 0.0 || lon == 0.0);
	}

	/**
	 * Return the latitude and longitude formatted as {@code "123,456"}
	 * @return {@code latitude,longitude}
	 */
	public String getMapCenterLatLong() {
		return String.format("%s,%s", this.getLatitude(), this.getLongitude());
	}

	/**
	 * @return JSON text version of the JSONObject.
	 */
	@Override
	public String toString() {
		double lat = this.getLatitude(), lon = this.getLongitude();
		if (lat == 0.0 && lon == 0.0) return UtilGlobal.EMPTY;

		if (this.json.has(JSONMapping.RADIUS) && this.json.getDouble(JSONMapping.RADIUS) < 1) {
			this.json.remove(JSONMapping.RADIUS);
		}

		return this.json.toString();
	}
}
