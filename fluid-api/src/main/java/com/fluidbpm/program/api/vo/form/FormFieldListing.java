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

import com.fluidbpm.program.api.vo.ABaseListing;
import com.fluidbpm.program.api.vo.field.Field;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * Represents a {@code List} of {@code Field}s.
 * </p>
 *
 * @author jasonbruwer
 * @see Field
 * @see ABaseListing
 * @since v1.1
 */
@Getter
@Setter
public class FormFieldListing extends ABaseListing<Field> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public FormFieldListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormFieldListing(JSONObject jsonObjectParam) {
        super(jsonObjectParam);
    }

    /**
     * Converts the {@code jsonObjectParam} to a {@code Field} object.
     *
     * @param jsonObjectParam The JSON object to convert to {@code Field}.
     * @return New {@code Field} instance.
     */
    @Override
    @XmlTransient
    public Field getObjectFromJSONObject(JSONObject jsonObjectParam) {
        return new Field(jsonObjectParam);
    }
}
