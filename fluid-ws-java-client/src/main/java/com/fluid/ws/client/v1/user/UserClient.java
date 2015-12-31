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

package com.fluid.ws.client.v1.user;

import org.json.JSONException;

import com.fluid.program.api.vo.User;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Created by jasonbruwer on 15/01/04.
 */
public class UserClient extends ABaseClientWS {

    /**
     *
     */
    public UserClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public UserClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     *
     * @param usernameParam
     * @return
     */
    public User getUserInformationWhereUsername(String usernameParam)
    {
        User userToGetInfoFor = new User();
        userToGetInfoFor.setUsername(usernameParam);

        if(this.serviceTicket != null)
        {
            userToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new User(this.postJson(
                    userToGetInfoFor, WS.Path.User.Version1.userInformation()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }


}
