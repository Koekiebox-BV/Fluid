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
import com.fluid.program.api.vo.User;
import com.fluid.program.api.vo.mail.MailMessage;

/**
 * @author jasonbruwer
 * @since 2015-05-15
 *
 * Implement this interface when you want Fluid to execute
 * a custom scheduled job action.
 *
 * The schedules are configured within Fluid.
 */
public interface ICustomScheduledAction extends IActionBase {

    /**
     * <code>Execute Order (1)</code>
     *
     * The Unique Schedule Action Identifier.
     *
     * @return
     */
    public abstract String getActionIdentifier();

    /**
     * <code>Execute Order (3)</code>
     *
     * The Query <code>java.lang.String</code> to use for the
     * <code>List<FluidItem> execute(List<FluidItem> fluidItemsParam)</code> method.
     *
     * @return
     * @throws Exception
     */
    public abstract String fluidItemQuery() throws Exception;

    /**
     * <code>Execute Order (4)</code>
     *
     * @param fluidItemsParam The <code>String fluidItemQuery()</code> result.
     * @return
     * @throws Exception
     */
    public abstract List<FluidItem> execute(List<FluidItem> fluidItemsParam) throws Exception;

    /**
     * <code>Execute Order (5)</code>
     *
     * @param usersParam The list of users in the system.
     * @return The updated users by the custom program. If a <code>null</code> is returned.
     * The update will be ignored.
     * @throws Exception
     */
    public abstract List<User> executeUsers(List<User> usersParam) throws Exception;

    /**
     * <code>Execute Order (6)</code>
     *
     * @return
     * @throws Exception
     */
    public abstract List<MailMessage> getMailMessagesToSend() throws Exception;

    /**
     * <code>Execute Order (7)</code>
     *
     * @return The execution result to be stored after the execution of all tasks
     * are completed successfully.
     */
    public abstract String getExecutionResult();
}
