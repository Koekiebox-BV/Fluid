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

import java.util.List;

import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;

/**
 * Implement this <code>interface</code> when you want Fluid to execute
 * a custom action when performing actions from a {@code Form}.
 *
 * Default action is <code>Save</code>.
 *
 * The schedules are configured within Fluid.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Form
 * @see FluidItem
 *
 */
public interface ICustomWebAction extends IActionBase {

	/**
	 * <code>Execute Order (2)</code>
	 *
	 * May be <code>'Save'</code> to apply when saving a Form.
	 * Any other <code>ActionIdentifier</code> will be part of an
	 * additional action that would not necessarily safe the form.
	 *
	 * @return The Fluid Implementation <code>Unique Action Identifier</code>.
	 */
	public abstract String getActionIdentifier();

	/**
	 * <code>Execute Order (3)</code>
	 *
	 * @return The {@code List<String>} of Fluid <code>Form Definition / Form Types</code>
	 *         That will be applicable to the <code>Custom Web Action</code>.
	 */
	public abstract List<String> getAllowedFormDefinitions();

	/**
	 * <code>Execute Order (4)</code>
	 *
	 * @param fluidItemParam The current open {@code FluidItem}.
	 * @return A {@code List<FluidItem>} that may include new Fluid Items to create or update.
	 * @throws Exception If any problems occur during execution.
	 *         The FlowItem will be moved into an <code>Erroneous</code> state.
	 * @see FluidItem
	 */
	public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;
}
