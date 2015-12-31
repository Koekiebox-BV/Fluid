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
