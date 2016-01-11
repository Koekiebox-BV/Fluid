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
import org.json.JSONObject;

import com.fluid.program.api.vo.User;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for User related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.User
 * @see User
 */
public class UserClient extends ABaseClientWS {

    /**
     * Default constructor.
     */
    public UserClient() {
        super();
    }

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public UserClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Retrieves user information for the provided {@code usernameParam}.
     *
     * @param usernameParam The username of the user to retrieve info for.
     * @return User information.
     *
     * @see User
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
