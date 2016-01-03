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
 * The interface to extend when creating
 * custom operations in a specific place in the Fluid workflow.
 *
 * @author jasonbruwer
 * @since v1.0
 * @see ICustomScheduledAction
 * @see ICustomWebAction
 * @see com.fluid.program.api.vo.Form
 * @see com.fluid.program.api.vo.FluidItem
 *
 */
public interface ICustomProgram extends IActionBase {

    /**
     * <code>Execute Order (2)</code>
     *
     * The Task Identifier when selecting the Program to Execute
     * at the relevant {@code FlowStep}.
     * 
     * @return The Fluid Implementation <code>Unique Action Identifier</code>.
     * @see com.fluid.program.api.vo.flow.FlowStep
     */
    public abstract String getTaskIdentifier();

    /**
     * <code>Execute Order (3)</code>
     * 
     * @param fluidItemParam The original Fluid workflow item at the Fluid Java Program Step with
     *                       all its properties and states.
     * @return A {@code List<FluidItem>} that may include new Fluid Items to create or update.
     * @throws Exception When a exception is {@code throw}, the Fluid Workitem will move into an error state.
     * @see FluidItem
     */
    public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;

    /**
     * Generate a {@code UserQuery} {@code String} that may be executed against
     * previous data stored in Fluid's workflow.
     * This is ideal for identifying and preventing the creation of duplicated data.
     *
     * <code>Execute Order (4)</code>
     *
     * @param fluidItemParam The {@code FluidItem}
     *
     * @return The Fluid {@code UserQuery} that will be executed once the {@code execute(FluidItem fluidItemParam)}
     *         <code>method</code> has finished.
     *         The {@code executePerFluidItemQuery} <code>method</code> will be executed
     *         for each of the {@code List<FluidItem>} items returned from the {@code execute} method.
     *         The item must be part of the Workflow to qualify.
     *
     * @throws Exception When a exception is {@code throw}, the Fluid workitem will move into an error state.
     *
     * @see ICustomProgram#execute(FluidItem)
     * @see ICustomProgram#postProcessFluidItem(FluidItem, List)
     */
    public abstract String executePerFluidItemQuery(FluidItem fluidItemParam) throws Exception;

    /**
     * <code>Execute Order (5)</code>
     *
     * If a <code>null</code> is returned, the item will be completely ignored.
     * No Creates or Updates will be performed.
     *
     * @param fluidItemParam Each of the original items from <code>execute(FluidItem fluidItemParam)</code>. There is an option to set
     *                       a parent for the Fluid item that most likely would originate from the <code>executePerFluidItemQueryResultParam</code>.
     *                       This is a way of linking incoming or existing data with existing data in Fluid.
     * @param executePerFluidItemQueryResultParam The <code>executePerFluidItemQuery({@code FluidItem} fluidItemParam)</code> result. Only
     *                                            attachments can be added <code>(No updates or creation of new or existing FluidItem's)</code>.
     * @return {@code null} or {@code fluidItemParam} modified.
     * @throws Exception When a exception is {@code throw}, the Fluid workitem will move into an error state.
     * @see FluidItem
     * @see com.fluid.program.api.vo.Form
     */
    public abstract FluidItem postProcessFluidItem(FluidItem fluidItemParam,
                                                   List<FluidItem> executePerFluidItemQueryResultParam) throws Exception;
}
