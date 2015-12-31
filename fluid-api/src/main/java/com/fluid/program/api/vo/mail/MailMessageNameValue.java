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

package com.fluid.program.api.vo.mail;

import com.fluid.program.api.vo.ABaseFluidVO;

/**
 *
 */
public class MailMessageNameValue extends ABaseFluidVO {

    private String name;
    private String value;

    /**
	 */
    public MailMessageNameValue() {
        super();
    }

    /**
     *
     * @param nameParam
     * @param valueParam
     */
    public MailMessageNameValue(String nameParam, String valueParam) {
        super();

        this.setName(nameParam);
        this.setValue(valueParam);
    }

    /**
     *
     * @param idParam
     */
    public MailMessageNameValue(Long idParam) {
        super(idParam);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param nameParam
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return this.value;
    }

    /**
     *
     * @param valueParam
     */
    public void setValue(String valueParam) {
        this.value = valueParam;
    }
}
