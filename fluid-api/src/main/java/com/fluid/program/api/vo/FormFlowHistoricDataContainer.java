package com.fluid.program.api.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jasonbruwer on 2015/12/28.
 */
public class FormFlowHistoricDataContainer extends ABaseFluidJSONObject {

    private List<FormFlowHistoricData> formFlowHistoricDatas;

    /**
     *
     * @param formFlowHistoricDatasParam
     */
    public FormFlowHistoricDataContainer(
            List<FormFlowHistoricData> formFlowHistoricDatasParam) {
        super();

        this.formFlowHistoricDatas = formFlowHistoricDatasParam;
    }

    /**
     *
     * @param jsonObjectParam
     * @throws JSONException
     */
    public FormFlowHistoricDataContainer(JSONObject jsonObjectParam) throws JSONException {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //TODO add the fields...
    }

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String DATE_CREATED = "formFlowHistoricDatas";
    }

    /**
     *
     * @return
     */
    public List<FormFlowHistoricData> getFormFlowHistoricDatas() {
        return this.formFlowHistoricDatas;
    }

    /**
     *
     * @param formFlowHistoricDatasParam
     */
    public void setFormFlowHistoricDatas(List<FormFlowHistoricData> formFlowHistoricDatasParam) {
        this.formFlowHistoricDatas = formFlowHistoricDatasParam;
    }
}
