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
 *
 * The interface to extend when creating
 * custom operations in a specific place in the Fluid workflow.
 */
public interface ICustomProgram extends IActionBase {

    /**
     * 
     * @return
     */
    public abstract String getTaskIdentifier();

    /**
     * 
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;

    /**
     *
     * 
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract String executePerFluidItemQuery(FluidItem fluidItemParam) throws Exception;

    /**
     * @param fluidItemParam - Each of the original items from <code>execute(FluidItem fluidItemParam)</code>. There is an option to set
     *                       a parent for the Fluid item that most likely would originate from the <code>executePerFluidItemQueryResultParam</code>.
     * @param executePerFluidItemQueryResultParam - The <code>executePerFluidItemQuery(FluidItem fluidItemParam)</code> result. Only
     *                                            attachments can be added.
     * @return
     * @throws Exception
     */
    public abstract FluidItem postProcessFluidItem(FluidItem fluidItemParam,
                                                   List<FluidItem> executePerFluidItemQueryResultParam) throws Exception;
}
