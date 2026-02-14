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

/**/
@Getter
@Setter
@RequiredArgsConstructor
public class RequestParameter extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;
    private final String alias;
    private final String value;

    public RequestParameter(String alias, Object value) {
        this(alias, value == null ? null : value.toString());
    }

    /**
     * Converts the value of this parameter to a boolean safely.
     *
     * @return true if the value represents a true state, false otherwise. Returns false for null or non-boolean strings.
     */
    public boolean getValueBoolean() {
        return UtilGlobal.toBooleanSafe(this.value);
    }

    /**
     * Converts the value of this parameter to an integer safely.
     *
     * @return the integer representation of the value. Returns 0 if the value is null or cannot be parsed as an integer.
     */
    public int getValueInteger() {
        return UtilGlobal.toIntSafe(this.value);
    }

    /**
     * Converts the value of this parameter to a long safely.
     *
     * @return the long representation of the value. Returns 0L if the value is null or cannot be parsed as a long.
     */
    public long getValueLong() {
        return UtilGlobal.toLongSafe(this.value);
    }
}
