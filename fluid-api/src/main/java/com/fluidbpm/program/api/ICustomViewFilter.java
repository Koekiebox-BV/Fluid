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

package com.fluidbpm.program.api;

import com.fluidbpm.program.api.vo.flow.JobView;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.user.User;

import java.util.List;

/**
 * Implement this <code>interface</code> when you want Fluid to make use of a custom
 * filter to filter when a VIEW is actioned.
 *
 * The schedules are configured within Fluid.
 *
 * @author jasonbruwer
 * @since v1.12
 *
 * @see Form
 * @see FluidItem
 * @see com.fluidbpm.program.api.vo.flow.JobView
 */
public interface ICustomViewFilter extends IActionBase {

	/**
	 * <code>Execute Order (2)</code>
	 *
	 * The name of the custom field filter in order to know which to execute.
	 *
	 * @return The Fluid Implementation <code>Unique View Filter Identifier</code>.
	 */
	String getFilterIdentifier();

	/**
	 * <code>Execute Order (3)</code>
	 *
	 * Performs the {@code execute} action to create a sub-set of items from {@code allItemsFromStep}.
	 * The custom view filter is ideal to cater for more complex filters not satisfied by the base workflow functionality.
	 *
	 * @param allItemsFromStep All the {@code FluidItem}'s from the step associated with {@code viewInvoked}.
	 * @param viewInvoked The {@code JobView} that was invoked.
	 * @param loggedInUser The currently logged in {@code User} who invoked the view.
	 * @return A {@code List<FluidItem>} list of items which need to be sub-set of {@code allItemsFromStep}
	 * @throws Exception If any problems occur during execution.
	 *         The FlowItem will be moved into an <code>Erroneous</code> state.
	 * @see FluidItem
	 * @see JobView
	 */
	List<FluidItem> execute(List<FluidItem> allItemsFromStep, JobView viewInvoked, User loggedInUser) throws Exception;
}
