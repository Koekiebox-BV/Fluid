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

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ABaseFluidVO;
import com.fluidbpm.program.api.vo.attachment.Attachment;

/**
 * Fluid Mail Message Name and Value.
 *
 * Email Template {{name}} values gets replaced with the
 * {@code MailMessageNameValue}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see Attachment
 * @see MailMessage
 * @see MailMessageAttachment
 * @see Attachment
 * @see ABaseFluidVO
 */
public class MailMessageNameValue extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * The JSON mapping for the {@code MailMessageNameValue} object.
     */
    public static class JSONMapping
    {
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
     * @param nameParam The Name of the value to replace.
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
    public MailMessageNameValue(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if (this.jsonObject == null) {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {

            this.setName(this.jsonObject.getString(
                    JSONMapping.NAME));
        }

        //Value...
        if (!this.jsonObject.isNull(JSONMapping.VALUE)) {

            this.setValue(this.jsonObject.getString(
                    JSONMapping.VALUE));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code MailMessageNameValue}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME, this.getName());
        }

        //Value...
        if(this.getValue() != null)
        {
            returnVal.put(JSONMapping.VALUE, this.getValue());
        }

        return returnVal;
    }

    /**
     * Gets the name of the name-value to replace.
     *
     * @return The Name of the value to replace.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the name-value to replace.
     *
     * @param nameParam The Name of the value to replace.
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets the replacement value.
     *
     * @return The replacement value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the replacement value.
     *
     * @param valueParam The replacement value.
     */
    public void setValue(String valueParam) {
        this.value = valueParam;
    }
}
