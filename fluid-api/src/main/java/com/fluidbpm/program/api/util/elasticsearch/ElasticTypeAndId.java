package com.fluidbpm.program.api.util.elasticsearch;

import com.fluidbpm.program.api.vo.ABaseFluidVO;

/**
 * The ElasticSearch Id and type combination.
 *
 * @author jasonbruwer on 2016/08/25.
 * @since 1.3
 */
public class ElasticTypeAndId extends ABaseFluidVO {

    private String type;

    /**
     * Constructor for new Elasticsearch type mapping with id.
     *
     * @param idParam   The Fluid Id.
     * @param typeParam The Form Definition Id as a {@code Long}.
     */
    public ElasticTypeAndId(Long idParam, String typeParam) {
        super(idParam);
        this.setType(typeParam);
    }

    /**
     * Gets the Form Definition Id as a {@code String}.
     *
     * @return Type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the Form Definition Id as a {@code String}.
     *
     * @param typeParam Type.
     */
    public void setType(String typeParam) {
        this.type = typeParam;
    }
}
