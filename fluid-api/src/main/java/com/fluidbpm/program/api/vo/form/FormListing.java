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

package com.fluidbpm.program.api.vo.form;

import com.fluidbpm.program.api.vo.ABaseGSONListing;
import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code Form}s.
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @see ABaseGSONListing
 * @since v1.1
 */
public class FormListing extends ABaseGSONListing<Form> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public FormListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormListing(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code Form} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code Form}.
     * @return New {@code Form} instance.
     */
    @Override
    @XmlTransient
    public Form getObjectFromJSONObject(JsonObject jsonObjectParam) {
        return new Form(jsonObjectParam);
    }
}
