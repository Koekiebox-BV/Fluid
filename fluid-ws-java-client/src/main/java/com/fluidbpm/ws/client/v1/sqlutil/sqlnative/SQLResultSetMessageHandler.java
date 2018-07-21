package com.fluidbpm.ws.client.v1.sqlutil.sqlnative;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.ws.client.v1.websocket.AGenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * A handler for {@code SQLResultSet}'s.
 *
 * @author jasonbruwer on 2018-05-26
 * @since 1.8
 */
public class SQLResultSetMessageHandler extends AGenericListMessageHandler<SQLResultSet> {

    /**
     * Constructor for SQLResultSet callbacks.
     *
     * @param messageReceivedCallbackParam The callback events.
     * @param compressedResponseParam Compress the SQL Result in Base-64.
     */
    public SQLResultSetMessageHandler(
            IMessageReceivedCallback<SQLResultSet> messageReceivedCallbackParam,
            boolean compressedResponseParam) {

        super(messageReceivedCallbackParam, compressedResponseParam);
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
