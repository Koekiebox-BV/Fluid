package com.fluid.program.api;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

/**
 * 
 */
public interface IActionBase extends Serializable {

    /**
     * Acts as a constructor that provides.
     *
     * <code>Execute Order (1)</code>
     *
     * @param dataSourcesParam A list of created JDBC Data Sources in the system.
     */
    public abstract void init(List<DataSource> dataSourcesParam);
}
