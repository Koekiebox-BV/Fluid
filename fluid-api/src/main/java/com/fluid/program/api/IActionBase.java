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

package com.fluid.program.api;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

/**
 * The base <code>interface</code> for all Fluid Java Custom Programs.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ICustomProgram
 * @see ICustomScheduledAction
 * @see ICustomWebAction
 */
public interface IActionBase extends Serializable {

    /**
     * <code>Execute Order (1)</code>
     *
     * <p>
     * Acts as a constructor that provides a list of configured {@code dataSourcesParam}
     * in Fluid. Including the <code>Datasource</code> to the <code>Fluid Core</code> {@code DataSource}.
     *
     * This method will always be <code>executed</code> first when the Fluid Third Party Library
     * executes any of the sub Custom Programs.
     *
     * @see javax.sql.DataSource
     * @param dataSourcesParam A list of created JDBC Data Sources in the system.
     *
     */
    public abstract void init(List<DataSource> dataSourcesParam);
}
