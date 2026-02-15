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

package com.fluidbpm.ws.client.v1.asn1der.vo.transmission;

import com.fluidbpm.program.api.vo.ABaseFluidVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**/
@Getter
@Setter
@RequiredArgsConstructor
public class ServerProcessStats extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;

    // Timestamp received and responded timestamps:
    private final long appLogicTsReceived;
    private final long appLogicTsResponded;

    // Processing Duration:
    private final long processingDurationMs;

    // Parsing of the request and response:
    private final long decodeRequestDurationMs;
    private final long encodeResponseDurationMs;
}
