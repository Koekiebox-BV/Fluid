package com.fluid.ws.client.v1.websocket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.ws.Error;

/**
 * TODO Description comes here.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.0
 */
public abstract class GenericListMessageHandler<T extends ABaseFluidJSONObject>
        implements IMessageHandler {

    private List<T> returnValue;
    private List<Error> errors;

    private IMessageReceivedCallback<T> messageReceivedCallback;
    private boolean isConnectionClosed;

    /**
     *
     * @param messageReceivedCallbackParam
     */
    public GenericListMessageHandler(IMessageReceivedCallback<T> messageReceivedCallbackParam) {

        this.messageReceivedCallback = messageReceivedCallbackParam;
        this.returnValue = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.isConnectionClosed = false;
    }

    /**
     * @param messageParam
     */
    @Override
    public void handleMessage(String messageParam) {

        JSONObject jsonObject = new JSONObject(messageParam);

        Error fluidError = new Error(jsonObject);

        if(fluidError.getErrorCode() > 0)
        {
            this.errors.add(fluidError);

            //Do a message callback...
            if(this.messageReceivedCallback != null)
            {
                this.messageReceivedCallback.errorMessageReceived(fluidError);
            }
        }
        else
        {
            T messageForm = this.getNewInstanceBy(jsonObject);

            synchronized (this.returnValue)
            {
                this.returnValue.add(messageForm);
            }

            //Do a message callback...
            if(this.messageReceivedCallback != null)
            {
                this.messageReceivedCallback.messageReceived(messageForm);
            }
        }
    }

    /**
     *
     */
    @Override
    public void connectionClosed() {
        this.isConnectionClosed = true;
    }

    /**
     *
     * @return
     */
    public boolean isConnectionClosed() {
        return this.isConnectionClosed;
    }

    /**
     *
     * @return
     */
    public boolean hasErrorOccurred()
    {
        return !this.errors.isEmpty();
    }

    /**
     *
     * @return
     */
    public List<Error> getErrors() {
        return this.errors;
    }

    /**
     *
     * @param jsonObjectParam
     * @return
     */
    public abstract T getNewInstanceBy(JSONObject jsonObjectParam);

    /**
     *
     * @return
     */
    public int getAddedCount()
    {
        return this.returnValue.size();
    }

    /**
     *
     * @return
     */
    private List<String> getEchoMessagesFromReturnValue()
    {
        List<String> returnListing = new ArrayList<>();

        synchronized (this.returnValue)
        {
            for(T returnVal : this.returnValue)
            {
                if(returnVal.getEcho() == null)
                {
                    continue;
                }

                returnListing.add(returnVal.getEcho());
            }
        }

        return returnListing;
    }

    /**
     *
     * @return
     */
    public boolean doReturnValueEchoMessageContainAll(List<String> echoMessageParam)
    {
        if(echoMessageParam == null || echoMessageParam.isEmpty())
        {
            return false;
        }

        List<String> allReturnValueEchoMessages =
                this.getEchoMessagesFromReturnValue();

        for(String toCheckFor: echoMessageParam)
        {
            if(!allReturnValueEchoMessages.contains(toCheckFor))
            {
                return false;
            }
        }

        return true;
    }

    /**
     *
     */
    public void clear()
    {
        this.returnValue.clear();
    }

    /**
     *
     * @return
     */
    public List<T> getReturnValue() {
        return this.returnValue;
    }
}
