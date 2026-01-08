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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.user.User;
import com.fluidbpm.ws.client.FluidClientException;
import org.bouncycastle.asn1.*;

import java.io.IOException;

public abstract class ASNBaseMapper<T extends ABaseFluidVO> {
    public static class Map {
        public static int START = 0;

        public static final int ID = START++;
        public static final int SERVICE_TICKET = START++;
        public static final int REQ_UUID = START++;
        public static final int ECHO = START++;
        public static final int USER = START++;

        public static final int CONTINUE = (START+1);
    }

    public abstract T decode(byte[] der);
    public abstract byte[] encode(T vo);

    protected void popFirstFields(T vo, ASN1Sequence seq) {
        vo.setId(asLong(seq.getObjectAt(ASNMapperUser.Map.ID), ABaseFluidGSONObject.JSONMapping.ID));
        vo.setServiceTicket(asUtf8(seq.getObjectAt(ASNBaseMapper.Map.SERVICE_TICKET), ABaseFluidGSONObject.JSONMapping.SERVICE_TICKET));
        vo.setRequestUuid(asUtf8(seq.getObjectAt(ASNBaseMapper.Map.REQ_UUID), ABaseFluidGSONObject.JSONMapping.REQUEST_UUID));
        vo.setEcho(asUtf8(seq.getObjectAt(ASNBaseMapper.Map.ECHO), ABaseFluidGSONObject.JSONMapping.ECHO));

        ASN1Sequence seqUser = ASN1Sequence.getInstance(seq.getObjectAt(ASNBaseMapper.Map.USER));
        vo.setLoggedInUserFromTicket(new User(
                asLong(seqUser.getObjectAt(ASNMapperUser.Map.ID), ABaseFluidGSONObject.JSONMapping.ID)
        ));

        if (seqUser.size() > 1) {
            vo.getLoggedInUserFromTicket().setUsername(
                    asUtf8(seqUser.getObjectAt(ASNMapperUser.Map.USERNAME), User.JSONMapping.USERNAME)
            );
        }
    }

    public static ASN1EncodableVector initVector(ABaseFluidVO vo) {
        ASN1EncodableVector vect = new ASN1EncodableVector();
        vect.add(new ASN1Integer(vo.getId() == null ? -1 : vo.getId()));
        vect.add(new DERUTF8String(vo.getServiceTicket() == null ? UtilGlobal.EMPTY : vo.getServiceTicket()));
        vect.add(new DERUTF8String(vo.getRequestUuid() == null ? UtilGlobal.EMPTY : vo.getRequestUuid()));
        vect.add(new DERUTF8String(vo.getEcho() == null ? UtilGlobal.EMPTY : vo.getEcho()));

        // User:
        User userToUse = vo.getLoggedInUserFromTicket();
        if (userToUse == null) userToUse = new User();

        ASN1EncodableVector vectUser = new ASN1EncodableVector();
        vect.add(new ASN1Integer(userToUse.getId() == null ? -1 : userToUse.getId()));

        if (userToUse.getUsername() != null) {
            vectUser.add(new DERTaggedObject(true, ASNMapperUser.Map.USERNAME, new DERUTF8String(userToUse.getUsername())));
        }
        vect.add(new DERSequence(vectUser));
        return vect;
    }

    protected ASN1Sequence initSeq(byte[] der) {
        try {
            return (ASN1Sequence)ASN1Primitive.fromByteArray(der);
        } catch (IOException ioErr) {
            throw new FluidClientException(ioErr.getMessage(), ioErr, FluidClientException.ErrorCode.ASN_1_ERROR);
        }
    }

    protected int asInt(ASN1Encodable e, String field) {
        if (e instanceof ASN1Integer) {
            ASN1Integer i = (ASN1Integer) e;
            return i.getValue().intValueExact();
        }
        throw new FluidClientException("Field " + field + " expected INTEGER, got " + e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR);
    }

    protected long asLong(ASN1Encodable e, String field) {
        if (e instanceof ASN1Integer) {
            ASN1Integer i = (ASN1Integer) e;
            return i.getValue().longValueExact();
        }
        throw new FluidClientException("Field " + field + " expected INTEGER, got " + e.getClass().getSimpleName(),
                FluidClientException.ErrorCode.ASN_1_ERROR);
    }

    protected ASN1TaggedObject asTagged(ASN1Encodable e, String field) {
        return ASN1TaggedObject.getInstance(e);
    }

    protected boolean asBool(ASN1Encodable e, String field) {
        if (e instanceof ASN1Boolean b) return b.isTrue();
        // Sometimes booleans come wrapped/universal — handle generic primitive:
        ASN1Primitive p = e.toASN1Primitive();
        if (p instanceof ASN1Boolean b2) return b2.isTrue();
        throw new IllegalArgumentException("Field " + field + " expected BOOLEAN, got " + e.getClass().getSimpleName());
    }

    protected String asUtf8(ASN1Encodable e, String field) {
        ASN1Primitive p = e.toASN1Primitive();
        if (p instanceof DERUTF8String s) return s.getString();
        if (p instanceof ASN1String s) return s.getString(); // fallback for other string types
        throw new IllegalArgumentException("Field " + field + " expected UTF8String, got " + e.getClass().getSimpleName());
    }
}
