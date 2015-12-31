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

import java.util.List;

import com.fluid.program.api.vo.FluidItem;

/**
 * @author jasonbruwer
 * @since 2015-05-15
 *
 * Implement this interface when you want Fluid to execute
 * a custom action when performing actions from a Form.
 *
 * Default action is <code>Save</code>.
 *
 * The schedules are configured within Fluid.
 */
public interface ICustomWebAction extends IActionBase {

    /**
     * May be 'Form Save' to apply when saving a Form.
     *
     * @return
     */
    public abstract String getActionIdentifier();

    /**
     *
     * @return
     */
    public abstract List<String> getAllowedFormDefinitions();

    /**
     * <code>Execute Order (2)</code>
     *
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;
}
