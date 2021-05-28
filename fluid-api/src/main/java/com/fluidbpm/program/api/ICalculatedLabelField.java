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

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.item.FluidItem;

/**
 * Implement this <code>interface</code> when you want Fluid populate a
 * label only field.
 * The value of the field will be calculated based on {@code Field}s of a {@code Form}.
 *
 * The fields are configured within Fluid.
 *
 * @author jasonbruwer
 * @since v1.8
 *
 * @see Form
 * @see Field
 *
 */
public interface ICalculatedLabelField extends IActionBase {

	/**
	 * <code>Execute Order (2)</code>
	 *
	 * The Fluid {@code Field} associated with the {@code sub-class} implementation.
	 * If there are conflicting field names, only the first will be executed.
	 *
	 * @return The Fluid Implementation <code>Field Identifier</code>.
	 */
	String getLabelFieldApplicable();

	/**
	 * <code>Execute Order (3)</code>
	 *
	 * @param fluidItemParam {@code FluidItem} that may be used to calculate the field label.
	 *
	 * @return The value of the field. String and primitive data types are supported.
	 * @throws Exception If any problems occur during execution.
	 *         The field with have a value of {@code -Error Retrieving Value-}
	 *
	 * @see FluidItem
	 * @see Field
	 */
	Object calculateFieldValue(FluidItem fluidItemParam) throws Exception;
}
