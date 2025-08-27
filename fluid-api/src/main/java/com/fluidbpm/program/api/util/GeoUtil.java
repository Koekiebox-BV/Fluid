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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Geo utility to set Latitude and Longitude based data for maps.
 * Any client may use the Geo utility to correctly supply lat, long, radius and address info as JSON text.
 *
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
     *
     * @param existingJson The JSON String.
     */
    public GeoUtil(String existingJson) {
        if (UtilGlobal.isBlank(existingJson)) {
            this.json = new JsonObject();
            return;
        }
        try {
            JsonElement parsed = JsonParser.parseString(existingJson);
            if (parsed != null && parsed.isJsonObject()) {
                this.json = parsed.getAsJsonObject();
            } else {
                this.json = new JsonObject();
                String[] existingVals = existingJson.split("\\|");
                if (existingVals.length > 1) {
                    try { this.setLatitude(Double.valueOf(existingVals[0])); } catch (NumberFormatException ignore) {}
                    try { this.setLongitude(Double.valueOf(existingVals[1])); } catch (NumberFormatException ignore) {}
                    if (existingVals.length > 2) this.setAddress(existingVals[2]);
                } else this.setAddress(existingJson);
            }
        } catch (Exception jsonErr) {
            this.json = new JsonObject();
            String[] existingVals = existingJson.split("\\|");
            if (existingVals != null && existingVals.length > 1) {
                try { this.setLatitude(Double.valueOf(existingVals[0])); } catch (NumberFormatException ignore) {}
                try { this.setLongitude(Double.valueOf(existingVals[1])); } catch (NumberFormatException ignore) {}
                if (existingVals.length > 2) this.setAddress(existingVals[2]);
            } else this.setAddress(existingJson);
        }
    }

    /**
     * Set the latitude on the JSON. {@code null} allowed.
     *
     * @param latitude The latitude.
     */
    public void setLatitude(Double latitude) {
        if (latitude == null) {
            this.json.add(JSONMapping.LAT, JsonNull.INSTANCE);
            return;
        }
        this.json.addProperty(JSONMapping.LAT, latitude);
    }

    /**
     * Fetch the latitude from JSON.
     *
     * @return The latitude or {@code 0.0} if not set.
     */
    public Double getLatitude() {
        if (this.json.has(JSONMapping.LAT)) {
            JsonElement e = this.json.get(JSONMapping.LAT);
            if (e != null && !e.isJsonNull()) return e.getAsDouble();
        }
        return 0.0;
    }

    /**
     * Set the longitude on the JSON. {@code null} allowed.
     *
     * @param longitude The longitude.
     */
    public void setLongitude(Double longitude) {
        if (longitude == null) {
            this.json.add(JSONMapping.LON, JsonNull.INSTANCE);
            return;
        }
        this.json.addProperty(JSONMapping.LON, longitude);
    }

    /**
     * Fetch the longitude from JSON.
     *
     * @return The longitude or {@code 0.0} if not set.
     */
    public Double getLongitude() {
        if (this.json.has(JSONMapping.LON)) {
            JsonElement e = this.json.get(JSONMapping.LON);
            if (e != null && !e.isJsonNull()) return e.getAsDouble();
        }
        return 0.0;
    }

    /**
     * Set the address on the JSON. {@code null} allowed.
     *
     * @param address The address.
     */
    public void setAddress(String address) {
        if (address == null) {
            this.json.add(JSONMapping.ADDR, JsonNull.INSTANCE);
            return;
        }

        if (address.length() > 150) address = address.substring(0, 150);

        this.json.addProperty(JSONMapping.ADDR, address);
    }

    /**
     * Fetch the address from JSON.
     *
     * @return The address or {@code empty} if not set.
     */
    public String getAddress() {
        if (this.json.has(JSONMapping.ADDR)) {
            JsonElement e = this.json.get(JSONMapping.ADDR);
            if (e != null && !e.isJsonNull()) return e.getAsString();
        }
        return UtilGlobal.EMPTY;
    }

    /**
     * Set the radius on the JSON. {@code null} allowed.
     *
     * @param radius The radius.
     */
    public void setRadius(Double radius) {
        if (radius == null) {
            this.json.add(JSONMapping.RADIUS, JsonNull.INSTANCE);
            return;
        }
        this.json.addProperty(JSONMapping.RADIUS, radius);
    }

    /**
     * Fetch the radius from JSON.
     *
     * @return The radius or {@code 0.0} if not set.
     */
    public Double getRadius() {
        if (this.json.has(JSONMapping.RADIUS)) {
            JsonElement e = this.json.get(JSONMapping.RADIUS);
            if (e != null && !e.isJsonNull()) return e.getAsDouble();
        }
        return 0.0;
    }

    /**
     * Verify whether latitude or longitude is not set.
     *
     * @return {@code true} if latitude or longitude is not set.
     */
    public boolean isLatOrLonNotSet() {
        double lat = this.getLatitude(), lon = this.getLongitude();
        return (lat == 0.0 || lon == 0.0);
    }

    /**
     * Return the latitude and longitude formatted as {@code "123,456"}
     *
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

        if (this.json.has(JSONMapping.RADIUS)) {
            JsonElement rEl = this.json.get(JSONMapping.RADIUS);
            if (rEl != null && !rEl.isJsonNull()) {
                if (rEl.getAsDouble() < 1) this.json.remove(JSONMapping.RADIUS);
            }
        }
        return this.json.toString();
    }
}
