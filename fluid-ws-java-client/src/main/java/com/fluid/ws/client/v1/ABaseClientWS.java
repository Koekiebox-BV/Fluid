package com.fluid.ws.client.v1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import com.fluid.GitDescribe;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;
import com.fluid.program.api.vo.ws.Error;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.FluidClientException;

/**
 * Created by jasonbruwer on 14/12/21.
 */
public class ABaseClientWS {

    public static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    public static final String CONTENT_TYPE_HEADER = "Content-type";

    private static String endpointUrl = "http://localhost:8080/fluid-ws/";

    protected String serviceTicket;

    private static String EQUALS = "=";
    private static String AMP = "&";


    /**
     *
     */
    public static class FormNameValue{
        private String name;
        private String value;

        /**
         *
         * @param nameParam
         * @param valueParam
         */
        public FormNameValue(String nameParam, String valueParam) {
            this.name = nameParam;
            this.value = valueParam;
            this.value = ABaseClientWS.encodeParam(this.value);
        }

        /**
         *
         * @return
         */
        public String getName() {
            return this.name;
        }

        /**
         *
         * @return
         */
        public String getValue() {
            return value;
        }
    }

    /**
     *
     */
    private enum HttpMethod
    {
        GET,POST,PUT,DELETE
    }

    /**
     *
     */
    public ABaseClientWS() {
        super();
    }

    /**
     *
     * @param endpointUrlParam
     */
    public ABaseClientWS(String endpointUrlParam) {
        this();
        ABaseClientWS.endpointUrl = endpointUrlParam;
    }

    /**
     *
     * @param postfixUrlParam
     * @return
     */
    public JSONObject getJson(
            String postfixUrlParam) {

        return this.getJson(false,postfixUrlParam);
    }

        /**
         *
         * @param skipCheckConnectionValidParam
         * @param postfixUrlParam
         * @return
         */
    public JSONObject getJson(
            boolean skipCheckConnectionValidParam,
            String postfixUrlParam) {

        //
        if(!skipCheckConnectionValidParam && !this.isConnectionValid())
        {
            throw new FluidClientException(
                    "Unable to reach service at '"+
                            endpointUrl.concat(postfixUrlParam)+"'.",
                    FluidClientException.ErrorCode.CONNECT_ERROR);
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(endpointUrl.concat(postfixUrlParam));

            // Create a custom response handler
            ResponseHandler<String> responseHandler = this.getJsonResponseHandler(
                    endpointUrl.concat(postfixUrlParam));

            String responseBody = this.executeHttp(httpclient,httpGet,responseHandler,postfixUrlParam);

            if(responseBody == null || responseBody.trim().isEmpty())
            {
                throw new FluidClientException(
                        "No response data from '"+
                                endpointUrl.concat(postfixUrlParam)+"'.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            JSONObject jsonOjb = new JSONObject(responseBody);

            if(jsonOjb.isNull(Error.JSONMapping.ERROR_CODE))
            {
                return jsonOjb;
            }

            int errorCode = jsonOjb.getInt(Error.JSONMapping.ERROR_CODE);

            if(errorCode > 0)
            {
                String errorMessage = (jsonOjb.isNull(Error.JSONMapping.ERROR_MESSAGE)
                        ? "Not set":
                        jsonOjb.getString(Error.JSONMapping.ERROR_MESSAGE));

                throw new FluidClientException(errorMessage, errorCode);
            }

            return jsonOjb;
        }
        //
        catch (JSONException jsonExcept)
        {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
        //
        finally {
            try {
                httpclient.close();
            }
            //
            catch (IOException e) {
                throw new FluidClientException(e.getMessage(),
                        FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }

    /**
     *
     * @param httpClientParam
     * @param httpUriRequestParam
     * @param responseHandlerParam
     * @param postfixUrlParam
     * @return
     */
    private String executeHttp(
            HttpClient httpClientParam,
            HttpUriRequest httpUriRequestParam,
            ResponseHandler responseHandlerParam,
            String postfixUrlParam)
    {
        try {
            Object returnedObj = httpClientParam.execute(httpUriRequestParam, responseHandlerParam);
            if(returnedObj instanceof String)
            {
                return (String)returnedObj;
            }

            throw new FluidClientException(
                    "Expected 'String' got '"+(
                            (returnedObj == null) ? null:returnedObj.getClass().getName()),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        //
        catch (IOException e) {

            if(e instanceof UnknownHostException)
            {
                throw new FluidClientException(
                        "Unable to reach host '"+
                                endpointUrl.concat(postfixUrlParam)+"'. "+e.getMessage(),
                        FluidClientException.ErrorCode.CONNECT_ERROR);
            }

            if(e instanceof ConnectException)
            {
                throw new FluidClientException(e.getMessage(),
                        FluidClientException.ErrorCode.CONNECT_ERROR);
            }

            throw new FluidClientException(e.getMessage(), FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     *
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject postJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //No need to check connection...
        return this.postJson(false, baseDomainParam,postfixUrlParam);
    }

    /**
     *
     * @param checkConnectionValidParam
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject postJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.POST,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }


    /**
     *
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject deleteJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //No need to check connection...
        return this.deleteJson(false, baseDomainParam, postfixUrlParam);
    }

    /**
     *
     * @param checkConnectionValidParam
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject deleteJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.DELETE,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }

    /**
     *
     * @param checkConnectionValidParam
     * @param formNameValuesParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject postForm(
            boolean checkConnectionValidParam,
            List<FormNameValue> formNameValuesParam,
            String postfixUrlParam) {

        return this.executeForm(
                HttpMethod.POST,
                checkConnectionValidParam,
                formNameValuesParam,
                ContentType.APPLICATION_FORM_URLENCODED,
                postfixUrlParam);
    }


    /**
     *
     * @param checkConnectionValidParam
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject putJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.PUT,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }


    /**
     *
     * @param baseDomainParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject putJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //Create without connection check...
        return this.putJson(false, baseDomainParam, postfixUrlParam);
    }

    /**
     *
     * @param httpMethodParam
     * @param checkConnectionValidParam
     * @param baseDomainParam
     * @param contentTypeParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject executeJson(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        //Validate that something is set.
        if(baseDomainParam == null)
        {
            throw new FluidClientException("No JSON body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        String bodyJsonString = baseDomainParam.toJsonObject().toString();

        return this.executeString(httpMethodParam,checkConnectionValidParam,
                bodyJsonString, contentTypeParam, postfixUrlParam);
    }

    /**
     *
     * @param httpMethodParam
     * @param checkConnectionValidParam
     * @param formNameValuesParam
     * @param contentTypeParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject executeForm(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            List<FormNameValue> formNameValuesParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        //
        if(formNameValuesParam == null || formNameValuesParam.isEmpty())
        {
            throw new FluidClientException("No 'Name and Value' body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        StringBuilder strBuilder = new StringBuilder();

        for(FormNameValue nameValue : formNameValuesParam)
        {
            if(nameValue.getName() == null || nameValue.getName().trim().isEmpty())
            {
                continue;
            }

            if(nameValue.getValue() == null)
            {
                continue;
            }

            strBuilder.append(nameValue.getName());
            strBuilder.append(EQUALS);
            strBuilder.append(nameValue.getValue());
            strBuilder.append(AMP);
        }

        String bodyJsonString = strBuilder.toString();
        bodyJsonString = bodyJsonString.substring(0, bodyJsonString.length() - 1);

        return this.executeString(httpMethodParam,checkConnectionValidParam,
                bodyJsonString, contentTypeParam, postfixUrlParam);
    }

    /**
     *
     * @param httpMethodParam
     * @param checkConnectionValidParam
     * @param baseDomainParam
     * @param contentTypeParam
     * @param postfixUrlParam
     * @return
     */
    protected JSONObject executeString(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            String baseDomainParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        if(baseDomainParam == null || baseDomainParam.isEmpty())
        {
            throw new FluidClientException("No JSON body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        //Check connection...
        if(!checkConnectionValidParam && !this.isConnectionValid())
        {
            throw new FluidClientException(
                    "Unable to reach service at '"+
                            endpointUrl.concat(postfixUrlParam)+"'.",
                    FluidClientException.ErrorCode.CONNECT_ERROR);
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpUriRequest uriRequest = null;

            //POST...
            if(httpMethodParam == HttpMethod.POST)
            {
                //When its html Form Data...
                if(contentTypeParam == ContentType.APPLICATION_FORM_URLENCODED)
                {
                    RequestBuilder builder = RequestBuilder.post().setUri(
                            endpointUrl.concat(postfixUrlParam));

                    builder = this.addParamsToBuildFromString(builder,baseDomainParam);

                    uriRequest = builder.build();
                }
                //JSON or any other...
                else
                {
                    uriRequest = new HttpPost(endpointUrl.concat(postfixUrlParam));
                }

                uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
            }
            //PUT...
            else if(httpMethodParam == HttpMethod.PUT)
            {
                if(contentTypeParam == ContentType.APPLICATION_FORM_URLENCODED)
                {
                    RequestBuilder builder = RequestBuilder.put().setUri(
                            endpointUrl.concat(postfixUrlParam));

                    builder = this.addParamsToBuildFromString(builder, baseDomainParam);
                    uriRequest = builder.build();
                }
                else
                {
                    uriRequest = new HttpPut(endpointUrl.concat(postfixUrlParam));
                    uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
                }
            }
            //DELETE...
            else if(httpMethodParam == HttpMethod.DELETE)
            {
                uriRequest = new HttpDelete(endpointUrl.concat(postfixUrlParam));
                uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
            }

            //Check that the URI request is set.
            if(uriRequest == null)
            {
                throw new FluidClientException(
                        "URI Request is not set for HTTP Method '"+httpMethodParam+"'.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }

            //When HttpEntity Enclosing Request Base...
            if(uriRequest instanceof HttpEntityEnclosingRequestBase)
            {
                HttpEntity httpEntity = new StringEntity(baseDomainParam, contentTypeParam);
                ((HttpEntityEnclosingRequestBase)uriRequest).setEntity(httpEntity);
            }

            // Create a custom response handler
            ResponseHandler<String> responseHandler = this.getJsonResponseHandler(
                    endpointUrl.concat(postfixUrlParam));

            String responseBody = this.executeHttp(httpclient, uriRequest,
                    responseHandler, postfixUrlParam);;

            if(responseBody == null || responseBody.trim().isEmpty())
            {
                throw new FluidClientException(
                        "No response data from '"+
                                endpointUrl.concat(postfixUrlParam)+"'.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            JSONObject jsonOjb = new JSONObject(responseBody);
            if(jsonOjb.isNull(Error.JSONMapping.ERROR_CODE))
            {
                return jsonOjb;
            }

            int errorCode = jsonOjb.getInt(Error.JSONMapping.ERROR_CODE);
            if(errorCode > 0)
            {
                String errorMessage = (jsonOjb.isNull(Error.JSONMapping.ERROR_MESSAGE)
                        ? "Not set":
                        jsonOjb.getString(Error.JSONMapping.ERROR_MESSAGE));

                throw new FluidClientException(errorMessage, errorCode);
            }

            return jsonOjb;
        }
        //Invalid JSON Body...
        catch (JSONException jsonExcept)
        {
            throw new FluidClientException(jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
        //Fluid Client Exception...
        catch (FluidClientException fluidClientExcept)
        {
            throw fluidClientExcept;
        }
        //Other Exceptions...
        catch (Exception otherExcept)
        {
            throw new FluidClientException(otherExcept.getMessage(),
                    otherExcept, FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        //
        finally {
            try {
                httpclient.close();
            }
            //
            catch (IOException e) {
                throw new FluidClientException(e.getMessage(),e,
                        FluidClientException.ErrorCode.IO_ERROR);
            }
        }
    }

    /**
     *
     * @param builderParam
     * @param formDataToAddParam
     * @return
     */
    private RequestBuilder addParamsToBuildFromString(
            RequestBuilder builderParam,
            String formDataToAddParam)
    {
        String[] nameValuePairs = formDataToAddParam.split("\\&");

        if(nameValuePairs != null && nameValuePairs.length > 0)
        {
            for(String nameValuePair : nameValuePairs)
            {
                String[] nameValuePairArr = nameValuePair.split("\\=");
                if(nameValuePairArr.length > 1)
                {
                    String name = nameValuePairArr[0];
                    String value = nameValuePairArr[1];

                    builderParam = builderParam.addParameter(name, value);
                }
            }
        }

        return builderParam;
    }

    /**
     *
     * @param urlCalledParam
     * @return
     */
    private ResponseHandler<String> getJsonResponseHandler(final String urlCalledParam)
    {
        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            /**
             *
             * @param response
             * @return
             * @throws java.io.IOException
             */
            public String handleResponse(final HttpResponse response) throws IOException {

                int status = response.getStatusLine().getStatusCode();
                if (status == 404) {
                    throw new FluidClientException(
                            "Endpoint for Service not found. URL ["+
                                    urlCalledParam+"].",
                            FluidClientException.ErrorCode.CONNECT_ERROR);
                }
                //
                else if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();

                    String responseJsonString = entity != null ?
                            EntityUtils.toString(entity) : null;

                    return responseJsonString;
                }
                //Bad Request... Server Side Error meant for client...
                else if (status == 400) {
                    HttpEntity entity = response.getEntity();

                    String responseJsonString = entity != null ?
                            EntityUtils.toString(entity) : null;

                    return responseJsonString;
                }
                //
                else {
                    HttpEntity entity = response.getEntity();

                    String responseString = (entity != null) ?
                            EntityUtils.toString(entity) : null;

                    throw new FluidClientException(
                            "Unexpected response status: " + status+". "
                            +response.getStatusLine().getReasonPhrase()+". \nResponse Text ["+
                                    responseString+"]",
                            FluidClientException.ErrorCode.IO_ERROR);
                }
            }
        };

        return responseHandler;
    }

    /**
     *
     * @param paramParam
     * @return
     */
    public static String encodeParam(String paramParam)
    {
        if(paramParam == null)
        {
            return null;
        }

        try {
            return URLEncoder.encode(paramParam,"UTF-8");
        }
        //
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @return
     */
    public boolean isConnectionValid()
    {
        //Init the session to get the salt...
        try{
            this.getJson(true, WS.Path.Test.Version1.testConnection());
        }
        //
        catch (FluidClientException flowJobExcept)
        {
            if(flowJobExcept.getErrorCode() == FluidClientException.ErrorCode.CONNECT_ERROR)
            {
                return false;
            }

            throw flowJobExcept;
        }

        return true;
    }

    /**
     *
     * @param baseDomainParam
     * @return
     */
    protected boolean isError(ABaseFluidJSONObject baseDomainParam)
    {
        if(baseDomainParam == null)
        {
            return false;
        }

        //
        if(baseDomainParam instanceof Error && ((Error)baseDomainParam).getErrorCode() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     *
     * @return
     */
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    /**
     *
     * @param serviceTicketParam
     */
    public void setServiceTicket(String serviceTicketParam) {
        this.serviceTicket = serviceTicketParam;
    }

    /**
     *
     * @param textParam
     * @return
     */
    protected final boolean isEmpty(String textParam) {
        return (textParam == null) ? true : textParam.trim().isEmpty();
    }

    /**
     *
     * @return
     */
    public String getFluidAPIVersion()
    {
        return GitDescribe.GIT_DESCRIBE;
    }
}
