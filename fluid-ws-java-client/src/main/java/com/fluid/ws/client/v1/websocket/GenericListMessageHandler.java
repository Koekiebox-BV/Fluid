package com.fluid.ws.client.v1.websocket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.ws.Error;

/**
 * Base list message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 */
public abstract class GenericListMessageHandler<T extends ABaseFluidJSONObject>
        implements IMessageHandler {

    private final List<T> returnValue;
    private List<Error> errors;

    private IMessageReceivedCallback<T> messageReceivedCallback;
    private boolean isConnectionClosed;

    /**
     * Message handler with callback.
     *
     * @param messageReceivedCallbackParam The message callback observer.
     */
    public GenericListMessageHandler(IMessageReceivedCallback<T> messageReceivedCallbackParam) {

        this.messageReceivedCallback = messageReceivedCallbackParam;
        this.returnValue = new ArrayList();
        this.errors = new ArrayList();
        this.isConnectionClosed = false;
    }

    /**
     * 'Handles' the message.
     *
     * @param messageParam The message to handle.
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
     * Event for when connection is closed.
     */
    @Override
    public void connectionClosed() {
        this.isConnectionClosed = true;
    }

    /**
     * Checks whether the connection to the server is closed.
     *
     * @return Whether connection is closed.
     */
    public boolean isConnectionClosed() {
        return this.isConnectionClosed;
    }

    /**
     * Checks to see whether there are error responses.
     *
     * @return Whether there is an error.
     */
    public boolean hasErrorOccurred()
    {
        return !this.errors.isEmpty();
    }

    /**
     * Error listing.
     *
     * @return The error that occured.
     */
    public List<Error> getErrors() {
        return this.errors;
    }

    /**
     * Create a new instance of {@code T}.
     *
     * @param jsonObjectParam The {@code JSONObject} to create an object from.
     * @return An instance of {@code ABaseFluidJSONObject}.
     *
     * @see ABaseFluidJSONObject
     * @see JSONObject
     */
    public abstract T getNewInstanceBy(JSONObject jsonObjectParam);

    /**
     * Gets the size of the return value.
     *
     * @return Added Count.
     */
    public int getAddedCount()
    {
        return this.returnValue.size();
    }

    /**
     * Gets a list of echo messages of the current return values.
     *
     * @return The return value echo messages.
     */
    private List<String> getEchoMessagesFromReturnValue()
    {
        List<String> returnListing = new ArrayList();

        if(this.returnValue == null)
        {
            return returnListing;
        }

        Iterator<T> iterForReturnVal =
                this.returnValue.iterator();

        //Only add where the ECHO message is set...
        while(iterForReturnVal.hasNext())
        {
            T returnVal = iterForReturnVal.next();

            if(returnVal.getEcho() == null)
            {
                continue;
            }

            returnListing.add(returnVal.getEcho());
        }

        return returnListing;
    }

    /**
     * Checks the local return value echo messages if all
     * of them contain {@code echoMessageParam}.
     *
     * @param echoMessageParam The echo messages to check.
     *
     * @return Whether local return value echo messages contain {@code echoMessageParam}.
     */
    public boolean doReturnValueEchoMessageContainAll(
            List<String> echoMessageParam)
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
     * Clears the return value.
     */
    public void clear()
    {
        this.returnValue.clear();
    }

    /**
     * Gets the return value.
     *
     * @return The return value listing.
     */
    public List<T> getReturnValue() {
        return this.returnValue;
    }
}
