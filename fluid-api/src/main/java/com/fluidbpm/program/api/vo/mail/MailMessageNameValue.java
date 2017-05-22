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

import com.fluidbpm.program.api.vo.ABaseFluidVO;

/**
 * Fluid Mail Message Name and Value.
 *
 * Email Template {{name}} values gets replaced with the
 * {@code MailMessageNameValue}.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see com.fluidbpm.program.api.vo.Attachment
 * @see MailMessage
 * @see MailMessageAttachment
 * @see com.fluidbpm.program.api.vo.Attachment
 * @see ABaseFluidVO
 */
public class MailMessageNameValue extends ABaseFluidVO {

    public static final long serialVersionUID = 1L;

    private String name;
    private String value;

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
     * Sets the id.
     *
     * @param idParam The Primary Key.
     */
    public MailMessageNameValue(Long idParam) {
        super(idParam);
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
