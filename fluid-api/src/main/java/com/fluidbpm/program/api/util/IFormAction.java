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

package com.fluidbpm.program.api.util;

import com.fluidbpm.program.api.vo.form.Form;

import java.util.List;

/**
 * Any form related actions.
 *
 * @author jasonbruwer on 2016/11/11.
 * @since 1.0
 */
public interface IFormAction extends IAction {

	/**
	 * Gets the ancestor for the {@code electronicFormIdParam} Form.
	 *
	 * @param electronicFormIdParam Identifier for the Form.
	 * @param includeFieldDataParam Whether to populate the return {@code Form} fields.
	 * @param includeTableFieldsParam Whether to populate the return {@code Form} table fields.
	 *
	 * @return {@code Form} descendants.
	 *
	 * @see Form
	 */
	Form getFormAncestor(
			Long electronicFormIdParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam);

	/**
	 * Retrieves the Table field records as {@code List<Form>}.
	 *
	 * @param electronicFormIdParam The Form Identifier.
	 * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
	 * @return {@code List<Form>} records.
	 */
	List<Form> getFormTableForms(Long electronicFormIdParam, boolean includeFieldDataParam);

	/**
	 * Gets the descendants for the {@code electronicFormIdParam} Form.
	 *
	 * @param electronicFormId Identifier for the Form.
	 * @param includeFieldData Whether to populate the return {@code List<Form>} fields.
	 * @param includeTableFields Whether to populate the return {@code List<Form>} table fields.
	 * @param includeTableFieldFormRecordInfo Does table record form data need to be included.
	 *
	 * @return {@code List<Form>} descendants.
	 *
	 * @see Form
	 */
	List<Form> getFormDescendants(
			Long electronicFormId,
			boolean includeFieldData,
			boolean includeTableFields,
			boolean includeTableFieldFormRecordInfo);

	/**
	 * Gets the descendants for the {@code electronicFormIdsParam} Forms.
	 *
	 * @param electronicFormIdsParam Identifiers for the Forms to retrieve.
	 * @param includeFieldDataParam Whether to populate the return {@code List<Form>} fields.
	 * @param includeTableFieldsParam Whether to populate the return {@code List<Form>} table fields.
	 * @param includeTableFieldFormRecordInfoParam Does table record form data need to be included.
	 *
	 * @return {@code List<Form>} descendants.
	 *
	 * @see Form
	 */
	List<Form> getFormDescendants(
			List<Long> electronicFormIdsParam,
			boolean includeFieldDataParam,
			boolean includeTableFieldsParam,
			boolean includeTableFieldFormRecordInfoParam);
}
