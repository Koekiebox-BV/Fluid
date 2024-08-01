/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Type of barcodes supported in Fluid.
 */
@RequiredArgsConstructor
@Getter
public enum BarcodeType {
    int2of5("int2of5", "Interleaved 2 of 5"),
    codabar("codabar", "Codabar"),
    code39("code39", "Code 39"),
    code128("code128", "Code 128"),
    ean8("ean8", "EAN-8"),
    ean13("ean13", "EAN-13"),
    upca("upca", "UPC-A (PNG)"),
    upce("upce", "UPC-E (Vertical)"),
    pdf417("pdf417", "PDF417"),
    datamatrix("datamatrix", "DataMatrix"),
    postnet("postnet", "Postnet"),
    qr("qr", "QR");

    private final String value;
    private final String alias;

    /**
     * @param value barcode value to fetch.
     * @return type or {@code null} if none found.
     */
    public static BarcodeType valueOfSafe(String value) {
        if (value == null) return null;

        String valueLower = value.toLowerCase();
        for (BarcodeType type : BarcodeType.values()) {
            if (type.getValue().toLowerCase().equals(valueLower)) return type;
        }
        return null;
    }
}
