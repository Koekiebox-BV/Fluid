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

package com.fluidbpm.program.api.vo.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Fluid Mail Message Name and Value.
 * <p>
 * Email Template {{name}} values gets replaced with the
 * {@code MailMessageNameValue}.
 *
 * @author jasonbruwer
 * @see Attachment
 * @see MailMessage
 * @see MailMessageAttachment
 * @see Attachment
 * @see ABaseFluidVO
 * @since v1.0
 */
@Getter
@Setter
public class MailMessageNameValue extends ABaseFluidGSONObject {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * The JSON mapping for the {@code MailMessageNameValue} object.
     */
    public static class JSONMapping {
        public static final String NAME = "name";
        public static final String VALUE = "value";
    }

    /**
     * Default constructor.
     */
    public MailMessageNameValue() {
        super();
    }

    /**
     * Sets the name and value used against the template.
     *
     * @param nameParam  The Name of the value to replace.
     * @param valueParam The replacement value.
     */
    public MailMessageNameValue(String nameParam, String valueParam) {
        super();
        this.setName(nameParam);
        this.setValue(valueParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public MailMessageNameValue(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setName(this.getAsStringNullSafe(JSONMapping.NAME));
        this.setValue(this.getAsStringNullSafe(JSONMapping.VALUE));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code MailMessageNameValue}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsProperty(JSONMapping.NAME, returnVal, this.getName());
        this.setAsProperty(JSONMapping.VALUE, returnVal, this.getValue());

        return returnVal;
    }
}
