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

package com.fluidbpm.program.api.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     Functions as a container object for {@code List} of {@code FormFlowHistoricData}s.
 *
 *     Makes it easier to create an expected message format when dealing with
 *     messages conversions in RESTful Web Services.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see FormFlowHistoricData
 */
public class FormFlowHistoricDataContainer extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private List<FormFlowHistoricData> formFlowHistoricDatas;

    /**
     * The JSON mapping for the {@code FormFlowHistoricDataContainer} object.
     */
    public static class JSONMapping
    {
        public static final String FORM_FLOW_HISTORIC_DATAS = "formFlowHistoricDatas";
    }

    /**
     * Sets FormFlowHistoricData.
     *
     * @param formFlowHistoricDatasParam {@code List} of {@code FormFlowHistoricData}.
     *
     * @see FormFlowHistoricData
     */
    public FormFlowHistoricDataContainer(
            List<FormFlowHistoricData> formFlowHistoricDatasParam) {
        super();

        this.formFlowHistoricDatas = formFlowHistoricDatasParam;
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FormFlowHistoricDataContainer(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Form Flow Historic Data...
        if (!this.jsonObject.isNull(JSONMapping.FORM_FLOW_HISTORIC_DATAS)) {

            JSONArray arrayOfObjects =
                    this.jsonObject.getJSONArray(JSONMapping.FORM_FLOW_HISTORIC_DATAS);

            this.formFlowHistoricDatas = new ArrayList();

            for(int index = 0;index < arrayOfObjects.length();index++)
            {
                this.formFlowHistoricDatas.add(new FormFlowHistoricData(
                        arrayOfObjects.getJSONObject(index)));
            }
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code FormFlowHistoricDataContainer}.
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Form Flow Historic Data...
        if(this.getFormFlowHistoricDatas() != null && !this.getFormFlowHistoricDatas().isEmpty())
        {
            JSONArray arrayOfHistoricData = new JSONArray();

            for(FormFlowHistoricData historyData : this.getFormFlowHistoricDatas())
            {
                arrayOfHistoricData.put(historyData.toJsonObject());
            }

            returnVal.put(JSONMapping.FORM_FLOW_HISTORIC_DATAS, arrayOfHistoricData);
        }

        return returnVal;
    }

    /**
     * Gets FormFlowHistoricData.
     *
     * @return {@code List} of {@code FormFlowHistoricData}.
     *
     * @see FormFlowHistoricData
     */
    public List<FormFlowHistoricData> getFormFlowHistoricDatas() {
        return this.formFlowHistoricDatas;
    }

    /**
     * Sets FormFlowHistoricData.
     *
     * @param formFlowHistoricDatasParam {@code List} of {@code FormFlowHistoricData}.
     *
     * @see FormFlowHistoricData
     */
    public void setFormFlowHistoricDatas(List<FormFlowHistoricData> formFlowHistoricDatasParam) {
        this.formFlowHistoricDatas = formFlowHistoricDatasParam;
    }
}
