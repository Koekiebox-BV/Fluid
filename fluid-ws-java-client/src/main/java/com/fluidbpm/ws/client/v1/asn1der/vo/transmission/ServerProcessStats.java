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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents statistics relevant to the processing of server-side logic.
 *
 * This class extends {@code ABaseFluidVO}, inheriting properties like {@code id}, {@code serviceTicket},
 * and {@code requestUuid}, while adding specific metrics associated with the server's request and response processing.
 * It provides detailed time measurements for various stages of the request-response lifecycle, making it useful
 * for performance analysis and optimization.
 *
 * Key metrics include:
 * - Timestamps for when the application logic received and responded to a request.
 * - Duration taken by the server to process the request.
 * - Time spent decoding the incoming request and encoding the outgoing response.
 *
 * These metrics can be used to diagnose bottlenecks, analyze performance trends, and improve the overall
 * efficiency of the server's processing workflow.
 */
@Getter
@Setter
@AllArgsConstructor
public class ServerProcessStats extends ABaseFluidVO {
    private static final long serialVersionUID = 1L;

    // Timestamp received and responded timestamps:
    private long appLogicTsReceived;
    private long appLogicTsResponded;

    // Processing Duration:
    private long processingDurationMs;

    // Parsing of the request and response:
    private long decodeRequestDurationMs;
    private long encodeResponseDurationMs;
}
