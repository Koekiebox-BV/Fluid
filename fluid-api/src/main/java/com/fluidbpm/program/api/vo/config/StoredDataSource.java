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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A configured JDBC data source. Supply an ID to update an existing entry.
 * Passwords are supplied in clear text and omitted from upsert responses.
 */
@Getter
@Setter
@XmlRootElement
public class StoredDataSource extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private String alias;
    private String driverClass;
    private String connectURI;
    private String username;
    private String password;
    private int maxTotal = 20;
    private int defaultMaxWaitMillis = 9000;
    private int defaultMaxIdle = 10;

    public static class JSONMapping {
        public static final String ALIAS = "alias";
        public static final String DRIVER_CLASS = "driverClass";
        public static final String CONNECT_URI = "connectURI";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String MAX_TOTAL = "maxTotal";
        public static final String DEFAULT_MAX_WAIT_MILLIS = "defaultMaxWaitMillis";
        public static final String DEFAULT_MAX_IDLE = "defaultMaxIdle";
    }

    public StoredDataSource() {
        super();
    }

    public StoredDataSource(Long id) {
        super();
        this.setId(id);
    }

    public StoredDataSource(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setAlias(this.getAsStringNullSafe(JSONMapping.ALIAS));
        this.setDriverClass(this.getAsStringNullSafe(JSONMapping.DRIVER_CLASS));
        this.setConnectURI(this.getAsStringNullSafe(JSONMapping.CONNECT_URI));
        this.setUsername(this.getAsStringNullSafe(JSONMapping.USERNAME));
        this.setPassword(this.getAsStringNullSafe(JSONMapping.PASSWORD));
        this.setMaxTotal(this.getAsIntegerNullSafeStrictVal(JSONMapping.MAX_TOTAL, 20));
        this.setDefaultMaxWaitMillis(this.getAsIntegerNullSafeStrictVal(JSONMapping.DEFAULT_MAX_WAIT_MILLIS, 9000));
        this.setDefaultMaxIdle(this.getAsIntegerNullSafeStrictVal(JSONMapping.DEFAULT_MAX_IDLE, 10));
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.ALIAS, returnVal, this.getAlias());
        this.setAsProperty(JSONMapping.DRIVER_CLASS, returnVal, this.getDriverClass());
        this.setAsProperty(JSONMapping.CONNECT_URI, returnVal, this.getConnectURI());
        this.setAsProperty(JSONMapping.USERNAME, returnVal, this.getUsername());
        this.setAsProperty(JSONMapping.PASSWORD, returnVal, this.getPassword());
        this.setAsProperty(JSONMapping.MAX_TOTAL, returnVal, this.getMaxTotal());
        this.setAsProperty(JSONMapping.DEFAULT_MAX_WAIT_MILLIS, returnVal, this.getDefaultMaxWaitMillis());
        this.setAsProperty(JSONMapping.DEFAULT_MAX_IDLE, returnVal, this.getDefaultMaxIdle());
        return returnVal;
    }
}
