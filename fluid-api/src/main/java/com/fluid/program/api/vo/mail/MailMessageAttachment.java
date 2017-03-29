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
 * Fluid representation of a Email message attachment.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see com.fluid.program.api.vo.Attachment
 * @see MailMessage
 * @see MailMessageNameValue
 * @see com.fluid.program.api.vo.Attachment
 * @see ABaseFluidVO
 */
public class MailMessageAttachment extends ABaseFluidVO {

    public static final long serialVersionUID = 1L;

    private String attachmentPath;

    /**
     * Default constructor.
     */
    public MailMessageAttachment() {
        super();
    }

    /**
     * Sets the path to the attachment.
     *
     * @param attachmentPathParam Path to the attachment.
     */
    public MailMessageAttachment(String attachmentPathParam) {
        super();

        this.setAttachmentPath(attachmentPathParam);
    }

    /**
     * Gets the path to the attachment.
     *
     * @return Path to the attachment.
     */
    public String getAttachmentPath() {
        return this.attachmentPath;
    }

    /**
     * Sets the path to the attachment.
     *
     * @param attachmentPathParam Path to the attachment.
     */
    public void setAttachmentPath(String attachmentPathParam) {
        this.attachmentPath = attachmentPathParam;
    }
}
