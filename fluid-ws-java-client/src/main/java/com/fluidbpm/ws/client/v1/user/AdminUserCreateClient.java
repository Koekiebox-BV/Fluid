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

package com.fluidbpm.ws.client.v1.user;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Client to create a {@code admin} user if not yet created.
 *
 * @author jasonbruwer
 * @since v1.8
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.user.User
 */
public class AdminUserCreateClient extends ABaseClientWS {

    /**
     * Constructor which sets the server URL.
     *
     * @param urlParam The server URL.
     */
    public AdminUserCreateClient(String urlParam) {
        super(urlParam);
    }

    /**
     * Create a new administrator user for Fluid.
     * This function only works if there are no pre-existing admin user.
     *
     * @param passwordParam The {@code admin} users password.
     * @return The newly created admin user.
     *
     * @see User
     */
    public User createAdminUser(String passwordParam) {

        User adminUserCreate = new User();
        adminUserCreate.setPasswordClear(passwordParam);

        return new User(this.putJson(
                adminUserCreate, WS.Path.User.Version1.userCreateAdmin()));
    }
}
