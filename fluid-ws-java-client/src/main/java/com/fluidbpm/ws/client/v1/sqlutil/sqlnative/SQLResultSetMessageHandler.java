package com.fluidbpm.ws.client.v1.sqlutil.sqlnative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.compress.CompressedResponse;
import com.fluidbpm.program.api.vo.sqlutil.sqlnative.SQLResultSet;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * A handler for {@code SQLResultSet}'s.
 *
 * @author jasonbruwer on 2018-05-26
 * @since 1.8
 */
public class SQLResultSetMessageHandler extends GenericListMessageHandler<SQLResultSet> {

    private boolean compressedResponse;

    /**
     * Constructor for SQLResultSet callbacks.
     *
     * @param messageReceivedCallbackParam The callback events.
     * @param compressedResponseParam Compress the SQL Result in Base-64.
     */
    public SQLResultSetMessageHandler(
            IMessageReceivedCallback<SQLResultSet> messageReceivedCallbackParam,
            boolean compressedResponseParam) {

        super(messageReceivedCallbackParam);
        this.compressedResponse = compressedResponseParam;
    }

    /**
     * New {@code SQLResultSet} by {@code jsonObjectParam}
     *
     * @param jsonObjectParam The JSON Object to parse.
     * @return new {@code SQLResultSet}.
     */
    @Override
    public SQLResultSet getNewInstanceBy(JSONObject jsonObjectParam) {

        if(this.compressedResponse){

            CompressedResponse compressedResponse =
                    new CompressedResponse(jsonObjectParam);

            byte[] compressedJsonList =
                    UtilGlobal.decodeBase64(compressedResponse.getDataBase64());

            byte[] uncompressedJson = null;
            try {
                uncompressedJson = this.uncompress(compressedJsonList);
            } catch (IOException eParam) {
                throw new FluidClientException(
                        "I/O issue with uncompress. "+eParam.getMessage(),
                        eParam,
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            return new SQLResultSet(new JSONObject(new String(uncompressedJson)));
        }

        return new SQLResultSet(jsonObjectParam);
    }

    /**
     * Uncompress the raw {@code compressedBytesParam}.
     *
     * @param compressedBytesParam The compressed bytes to uncompress.
     *
     * @return Uncompressed bytes.
     *
     * @throws IOException - If there is an issue during the un-compression.
     */
    private byte[] uncompress(byte[] compressedBytesParam)
    throws IOException {

        byte[] buffer = new byte[1024];

        byte[] returnVal = null;

        //get the zip file content
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(compressedBytesParam));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();
        if(ze == null){

            return returnVal;
        }
        
        int len;
        while ((len = zis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }

        zis.closeEntry();
        zis.close();

        bos.flush();
        bos.close();

        returnVal = bos.toByteArray();

        return returnVal;
    }
}
