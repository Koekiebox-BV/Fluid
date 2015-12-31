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
