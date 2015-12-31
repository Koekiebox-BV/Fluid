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
 * Created by jasonbruwer on 15/05/20.
 */
public class MailMessageAttachment extends ABaseFluidVO {

    private String attachmentPath;

    public MailMessageAttachment() {
        super();
    }

    /**
     *
     * @param attachmentPathParam
     */
    public MailMessageAttachment(String attachmentPathParam) {
        super();

        this.setAttachmentPath(attachmentPathParam);
    }

    /**
     *
     * @return
     */
    public String getAttachmentPath() {
        return this.attachmentPath;
    }

    /**
     *
     * @param attachmentPathParam
     */
    public void setAttachmentPath(String attachmentPathParam) {
        this.attachmentPath = attachmentPathParam;
    }
}
