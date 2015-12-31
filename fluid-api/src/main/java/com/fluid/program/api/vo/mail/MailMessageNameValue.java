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
