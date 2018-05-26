package com.fluidbpm.ws.client.v1.sqlutil.sqlnative;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * A handler for {@code SQLResultSet}'s.
 *
 * @author jasonbruwer on 2018-05-26
 * @since 1.8
 */
public class SQLResultSetMessageHandler extends GenericListMessageHandler<SQLResultSet> {

    /**
     * Constructor for SQLResultSet callbacks.
     *
     * @param messageReceivedCallbackParam The callback events.
     */
    public SQLResultSetMessageHandler(IMessageReceivedCallback<SQLResultSet> messageReceivedCallbackParam) {

        super(messageReceivedCallbackParam);
    }

    /**
     * New {@code SQLResultSet} by {@code jsonObjectParam}
     *
     * @param jsonObjectParam The JSON Object to parse.
     * @return new {@code SQLResultSet}.
     */
    @Override
    public SQLResultSet getNewInstanceBy(JSONObject jsonObjectParam) {
        return new SQLResultSet(jsonObjectParam);
    }
}
