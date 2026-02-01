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

package com.fluidbpm.ws.client.v1.asn1der.vo;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**/
@Getter
@Setter
@RequiredArgsConstructor
public class RequestObject extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;
    private final String path;
    private final List<RequestParameter> requestParameters;

    public RequestObject() {
        this(UtilGlobal.EMPTY, new ArrayList<>());
    }

    /**
     * Retrieves the value of a request parameter identified by its alias.
     * If no parameter is found for the given alias, an empty string is returned.
     *
     * @param alias the alias of the request parameter to retrieve; must not be null or blank
     * @return the value of the request parameter as a string, or an empty string if no matching parameter is found
     */
    public String getRequestParamString(String alias) {
        RequestParameter rp = paramByAlias(alias);
        if (rp == null) return UtilGlobal.EMPTY;

        return rp.getValue();
    }

    /**
     * Retrieves the boolean value of a request parameter identified by its alias.
     * If no parameter is found for the given alias, false is returned.
     *
     * @param alias the alias of the request parameter to retrieve; must not be null or blank
     * @return the boolean value of the request parameter, or false if no matching parameter is found
     */
    public boolean getRequestParamBoolean(String alias) {
        RequestParameter rp = paramByAlias(alias);
        if (rp == null) return false;

        return rp.getValueBoolean();
    }

    /**
     * Retrieves the integer value of a request parameter identified by its alias.
     * If no parameter is found for the given alias, -1 is returned.
     *
     * @param alias the alias of the request parameter to retrieve; must not be null or blank
     * @return the integer value of the request parameter, or -1 if no matching parameter is found
     */
    public int getRequestParamInteger(String alias) {
        RequestParameter rp = paramByAlias(alias);
        if (rp == null) return -1;

        return rp.getValueInteger();
    }

    /**
     * Retrieves the long value of a request parameter identified by its alias.
     * If no parameter is found for the given alias, -1 is returned.
     *
     * @param alias the alias of the request parameter to retrieve; must not be null or blank
     * @return the long value of the request parameter, or -1 if no matching parameter is found
     */
    public long getRequestParamLong(String alias) {
        RequestParameter rp = paramByAlias(alias);
        if (rp == null) return -1L;

        return rp.getValueLong();
    }

    /**
     * Retrieves a {@link RequestParameter} object from the list of request parameters
     * that matches the given alias, ignoring case sensitivity.
     *
     * @param alias the alias of the {@link RequestParameter} to retrieve; must not be null or blank
     * @return the {@link RequestParameter} that matches the alias, or null if no matching parameter is found.
     *         Returns null if the list of request parameters is empty or the alias is blank.
     */
    private RequestParameter paramByAlias(String alias) {
        if (this.requestParameters == null || this.requestParameters.isEmpty()) return null;
        else if (UtilGlobal.isBlank(alias)) return null;

        return this.requestParameters.stream()
                .filter(itm -> itm.getAlias().equalsIgnoreCase(alias))
                .findFirst()
                .orElse(null);
    }
}
