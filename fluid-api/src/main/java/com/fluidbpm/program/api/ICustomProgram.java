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

package com.fluidbpm.program.api;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;

import java.util.List;

/**
 * The interface to extend when creating
 * custom operations in a specific place in the Fluid workflow.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ICustomScheduledAction
 * @see ICustomWebAction
 * @see Form
 * @see FluidItem
 *
 */
public interface ICustomProgram extends IActionBase {

	/**
	 * <code>Execute Order (2)</code>
	 *
	 * <p>
	 * The Task Identifier when selecting the Program to Execute
	 * at the relevant {@code FlowStep}.
	 *
	 * @return The Fluid Implementation <code>Unique Action Identifier</code>.
	 * @see com.fluidbpm.program.api.vo.flow.FlowStep
	 */
	String getTaskIdentifier();

	/**
	 * <code>Execute Order (3)</code>
	 *
	 * <p>
	 * Execute the custom functionality and change properties on {@code fluidItemParam}.
	 *
	 * @param fluidItemParam The original Fluid workflow item at the Fluid Java Program Step with
	 *                       all its properties and states.
	 * @return A {@code List<FluidItem>} that may include new Fluid Items to create or update.
	 * @throws Exception When a exception is {@code throw}, the Fluid Workitem will move into an error state.
	 * @see FluidItem
	 */
	List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;

	/**
	 * <code>Execute Order (4)</code>
	 *
	 * <p>
	 * Generate a {@code UserQuery} {@code String} that may be executed against
	 * previous data stored in Fluid's workflow.
	 * This is ideal for identifying and preventing the creation of duplicated data.
	 *
	 * <p>
	 * Example: {@code [Field Name] = 'Field Value', [Field Name Two] = 'Field Value Two'}
	 *
	 * @param fluidItemParam The {@code FluidItem}
	 *
	 * @return The Fluid {@code UserQuery} that will be executed once the {@code execute(FluidItem fluidItemParam)}
	 *         {@code method} has finished.
	 *         The {@code executePerFluidItemQuery} {@code method} will be executed
	 *         for each of the {@code List<FluidItem>} items returned from the {@code execute} method.
	 *         The item must be part of the Workflow to qualify.
	 *
	 * @throws Exception When a exception is {@code throw}, the Fluid work-item will move into an error state.
	 *
	 * @see ICustomProgram#execute(FluidItem)
	 * @see ICustomProgram#postProcessFluidItem(FluidItem, List)
	 */
	String executePerFluidItemQuery(FluidItem fluidItemParam) throws Exception;

	/**
	 * <code>Execute Order (5)</code>
	 *
	 * <p>
	 * If a {@code null} is returned, the item will be completely ignored.
	 * No Create or Update will be performed on return value {@code FluidItem}.
	 *
	 * If the {@code FluidItem.setFlowJob()} is used, the newly created item will be sent to the
	 * described 'Flow'.
	 * In addition, if {@code setInCaseOfCreateLinkToParent()} is set to {@code true}, the
	 * child will be linked to the parent.
	 *
	 *
	 * <p>
	 * If an attachment is added to the any of the {@code executePerFluidItemQueryResultParam} items, the
	 * attachment will be added to that item if the type is (Document or Folder).
	 * The conversion type for {@code this} is called {@code FlowJobItemConverted.ConvertedType.AttachmentAddOnly}.
	 * This is an easy way to link attachments to {@code Document} items.
	 *
	 * @param fluidItemParam Each of the original items from {@code execute(FluidItem fluidItemParam)}. There is an option to set
	 *                       a parent for the Fluid item that most likely would originate from the {@code executePerFluidItemQueryResultParam}.
	 *                       This is a way of linking incoming or existing data with existing data in Fluid.
	 * @param executePerFluidItemQueryResultParam The {@code executePerFluidItemQuery(FluidItem fluidItemParam)} result. Only
	 *                                            attachments can be added <code>(No updates or creation of new or existing FluidItem's)</code>.
	 *
	 * @return {@code null} or {@code fluidItemParam} modified. If the Form id is set, an update will be performed, otherwise a create.
	 *
	 * @throws Exception When a exception is {@code throw}, the Fluid Workitem will move into an error state.
	 * @see FluidItem
	 * @see Form
	 */
	FluidItem postProcessFluidItem(FluidItem fluidItemParam,
												   List<FluidItem> executePerFluidItemQueryResultParam)
			throws Exception;
}
