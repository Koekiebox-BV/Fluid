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

import com.fluidbpm.program.api.vo.flow.JobView;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.function.Supplier;

/**
 * A specialized mapper for handling ASN.1 encoding and decoding of {@link JobView} objects.
 * This class provides mapping logic for translating tagged ASN.1 fields into {@link JobView}
 * instances and vice versa.
 */
public class ASNMapperJobView extends ASNBaseTaggedMapper<JobView> {
    public static class Map extends ASNBaseMapper.Map {
        public static final int RULE = 1;
        public static final int VIEW_NAME = 2;
        public static final int VIEW_GROUP_NAME = 3;
        public static final int VIEW_STEP_NAME = 4;
        public static final int VIEW_FLOW_NAME = 5;
        public static final int VIEW_PRIORITY = 6;
        public static final int VIEW_ORDER = 7;
        public static final int VIEW_TYPE = 8;
    }

    /**
     * Constructs a new instance of the ASNMapperJobView class with the initialization type set to ID_ONLY.
     */
    public ASNMapperJobView() {
        super(InitType.ID_ONLY);
    }

    /**
     * Provides a supplier that creates new instances of the {@link JobView} class.
     *
     * @return A {@code Supplier} that generates new {@link JobView} instances.
     */
    @Override
    protected Supplier<JobView> supplierForInstance() {
        return JobView::new;
    }

    /**
     * Maps a decoded tagged ASN.1 object to a {@link JobView} instance based on its tag number.
     *
     * @param tag The tagged ASN.1 object containing the tag number, associated data,
     *            and the {@link JobView} instance to populate.
     * @return Always returns {@code null}.
     */
    @Override
    protected Void mapDecodedTaggedObject(TagObj<JobView> tag) {
        assert tag != null : "Arguments cannot be null.";

        JobView toPop = tag.getToPopulate();
        ASN1Object obj = tag.getObj();

        assert toPop != null && obj != null : "TagObj instances cannot be null.";

        switch (tag.getTagNo()) {
            case Map.RULE:
                toPop.setRule(asGeneralTxt(obj, JobView.JSONMapping.RULE));
                break;
            case Map.VIEW_NAME:
                toPop.setViewName(asGeneralTxt(obj, JobView.JSONMapping.VIEW_NAME));
                break;
            case Map.VIEW_GROUP_NAME:
                toPop.setViewGroupName(asGeneralTxt(obj, JobView.JSONMapping.VIEW_GROUP_NAME));
                break;
            case Map.VIEW_STEP_NAME:
                toPop.setViewStepName(asGeneralTxt(obj, JobView.JSONMapping.VIEW_STEP_NAME));
                break;
            case Map.VIEW_FLOW_NAME:
                toPop.setViewFlowName(asGeneralTxt(obj, JobView.JSONMapping.VIEW_FLOW_NAME));
                break;
            case Map.VIEW_PRIORITY:
                toPop.setViewPriority(asInt(obj, JobView.JSONMapping.VIEW_PRIORITY));
                break;
            case Map.VIEW_ORDER:
                toPop.setViewOrder(asLong(obj, JobView.JSONMapping.VIEW_ORDER));
                break;
            case Map.VIEW_TYPE:
                toPop.setViewType(asGeneralTxt(obj, JobView.JSONMapping.VIEW_TYPE));
                break;
        }
        return null;
    }

    /**
     * Encodes a tagged ASN.1 object from the provided {@link JobView} item and appends it to
     * the given {@link ASN1EncodableVector}.
     *
     * @param item The {@link JobView} instance to encode.
     * @param vect The {@link ASN1EncodableVector} to which the encoded object will be appended.
     */
    @Override
    protected void encodeTaggedObject(JobView item, ASN1EncodableVector vect) {
        assert item != null && vect != null : "Arguments cannot be null.";
        assert vect.size() > 0 : "Vector size should be greater than zero.";

        if (item.getRule() != null) {
            vect.add(new DERTaggedObject(true, Map.RULE, new DERGeneralString(item.getRule())));
        }

        if (item.getViewName() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_NAME, new DERGeneralString(item.getViewName())));
        }

        if (item.getViewGroupName() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_GROUP_NAME, new DERGeneralString(item.getViewGroupName())));
        }

        if (item.getViewStepName() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_STEP_NAME, new DERGeneralString(item.getViewStepName())));
        }

        if (item.getViewFlowName() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_FLOW_NAME, new DERGeneralString(item.getViewFlowName())));
        }

        if (item.getViewPriority() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_PRIORITY, new ASN1Integer(item.getViewPriority())));
        }

        if (item.getViewOrder() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_ORDER, new ASN1Integer(item.getViewOrder())));
        }

        if (item.getViewType() != null) {
            vect.add(new DERTaggedObject(true, Map.VIEW_TYPE, new DERGeneralString(item.getViewType())));
        }
    }
}
