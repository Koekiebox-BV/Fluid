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
import com.fluidbpm.program.api.vo.mail.MailMessage;
import com.fluidbpm.program.api.vo.user.User;

/**
 * Implement this interface when you want Fluid to execute
 * a custom scheduled job action.
 *
 * The schedules are configured within Fluid.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see ICustomProgram
 * @see ICustomWebAction
 * @see Form
 * @see FluidItem
 * @see com.fluidbpm.program.api.vo.flow.Flow
 * @see com.fluidbpm.program.api.vo.flow.FlowStep
 * @see com.fluidbpm.program.api.vo.flow.FlowStepRule
 */
public interface ICustomScheduledAction extends IActionBase {

	/**
	 * <code>Execute Order (2)</code>
	 *
	 * <p>
	 * The Unique Schedule Action Identifier.
	 *
	 * @return The Fluid Implementation <code>Unique Action Identifier</code>.
	 */
	public abstract String getActionIdentifier();

	/**
	 * <code>Execute Order (3)</code>
	 *
	 * <p>
	 * The Query {@code String} to use for the {@code execute(List<FluidItem> fluidItemsParam)}
	 * method.
	 *
	 * <p>Examples:
	 *
	 * <p>[id] = '850354', [Last Name] = 'Creep'</p>
	 * <p>[Form Type] = 'Email', [Last Name] = 'Creep'</p>
	 *
	 * @return A query {@code String} in Fluid UserQuery format.
	 * @throws Exception When a exception is {@code throw}, the Fluid Workitem will move into an error state.
	 */
	public abstract String fluidItemQuery() throws Exception;

	/**
	 * <code>Execute Order (4)</code>
	 *
	 * <p>
	 * Once can make use of the {@code ICustomScheduledAction} to perform mass
	 * updates on a lot of Workflow entries at once.
	 *
	 * This is ideal for performing updates on a whole set of Forms such as daily calculated
	 * interest rates or leave accumulated.
	 *
	 * @param fluidItemsParam The {@code String fluidItemQuery()} result.
	 * @return A {@code List<FluidItem>} that may include new Fluid Items to create or update.
	 * @throws Exception When a exception is {@code throw}, the Fluid workitem will move into an error state.
	 */
	public abstract List<FluidItem> execute(List<FluidItem> fluidItemsParam) throws Exception;

	/**
	 * <code>Execute Order (5)</code>
	 *
	 * <p>
	 * Override this {@code method} to make changes to Fluid users.
	 *
	 * @param usersParam The list of users in the system.
	 * @return The updated users by the custom program. If a {@code null} is returned.
	 * The update will be ignored.
	 * @throws Exception When a exception is {@code throw}, the Fluid workitem will move into an error state.
	 * @see User
	 */
	public abstract List<User> executeUsers(List<User> usersParam) throws Exception;

	/**
	 * <code>Execute Order (6)</code>
	 *
	 * <p>
	 * Override this <code>method</code> to send emails after
	 * the processing of the scheduled job.
	 *
	 * @return A {@code MailMessage} that may include emails to be sent after processing.
	 * @throws Exception When a exception is {@code throw}, the Fluid workitem will move into an error state.
	 * @see MailMessage
	 */
	public abstract List<MailMessage> getMailMessagesToSend() throws Exception;

	/**
	 * <code>Execute Order (7)</code>
	 *
	 * <p>
	 * Trace data to be stored at the time of Schedule Task execution for auditing purposes.
	 *
	 * @return The execution result to be stored after the execution of all tasks
	 * are completed successfully.
	 */
	public abstract String getExecutionResult();
}
