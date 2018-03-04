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

package com.fluidbpm.program.api.vo.attachment;

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseListing;

/**
 * <p>
 *     Represents a {@code List} of {@code Attachment}s.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.8
 *
 * @see Attachment
 * @see ABaseListing
 */
public class AttachmentListing extends ABaseListing<Attachment> {

    public static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public AttachmentListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public AttachmentListing(JSONObject jsonObjectParam){
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code Attachment} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code Attachment}.
     * @return New {@code Attachment} instance.
     */
    @Override
    @XmlTransient
    public Attachment getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new Attachment(jsonObjectParam);
    }
}
