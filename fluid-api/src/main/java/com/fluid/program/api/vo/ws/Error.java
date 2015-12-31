package com.fluid.program.api.vo.ws;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class Error extends ABaseFluidJSONObject {

    private int errorCode;
    private String errorMessage;


    /**
     *
     */
    public static class JSONMapping
    {
        public static final String ERROR_CODE = "errorCode";
        public static final String ERROR_MESSAGE = "errorMessage";
    }

    /**
     *
     */
    public Error() {
        super();
    }

    /**
     *
     * @param errorCode
     * @param errorMessage
     */
    public Error(int errorCode, String errorMessage) {
        this.setErrorCode(errorCode);
        this.setErrorMessage(errorMessage);
    }

    /**
     *
     * @return
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     *
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     *
     * @return
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     *
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
