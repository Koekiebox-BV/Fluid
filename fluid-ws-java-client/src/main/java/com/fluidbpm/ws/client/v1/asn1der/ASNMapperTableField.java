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

package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.program.api.vo.field.TableField;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/**
 */
public class ASNMapperTableField extends ASNBaseMapper<TableField> {
    private final ASNMapperForm asnMapForm;

    public ASNMapperTableField(ASNMapperForm asnMapForm) {
        super(InitType.NONE);
        this.asnMapForm = asnMapForm;
    }

    public static class Map extends ASNBaseMapper.Map {
        public static final int SUM_DECIMALS = 0;
        public static final int RECORDS = 1;
    }

    @Override
    public TableField decode(byte[] der) {
        return null;
    }

    public TableField decode(ASN1Sequence sequence) {
        TableField returnVal = new TableField();

        //TODO ssdf

        return returnVal;
    }

    @Override
    public DERSequence encode(TableField vo) {
        return null;
    }
}
