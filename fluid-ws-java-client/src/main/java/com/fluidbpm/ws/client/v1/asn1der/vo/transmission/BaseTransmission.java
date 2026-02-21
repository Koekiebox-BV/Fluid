/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2026] Koekiebox (Pty) Ltd
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

package com.fluidbpm.ws.client.v1.asn1der.vo.transmission;

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.ws.client.v1.asn1der.vo.RequestObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an empty value object extending the base fluid value object.
 * This class inherits common properties and functionality from {@code ABaseFluidVO}.
 * It can be used as a placeholder or base implementation for objects with no additional properties.
 *
 * @see ABaseFluidVO
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BaseTransmission extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;

    private PayloadPopulate payloadPopulate;//6
    private RequestObject requestObject;//7
    private ABaseFluidVO transmissionObject;//8
    private ServerProcessStats serverProcessStats;//9

    /**
     * Sets the Id associated with any Fluid entity.
     * @param id Unique Identifier.
     */
    public BaseTransmission(Long id) {
        super(id);
    }

    /**
     * Sets the Id associated with any Fluid entity.
     * @param id Unique Identifier.
     */
    public BaseTransmission(int id) {
        super((long) id);
    }
}
