package com.fluid.program.api.vo.flow;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluid.program.api.vo.ABaseFluidJSONObject;

/**
 *
 */
public class Flow extends ABaseFluidJSONObject {

    private String name;
    private String description;
    private Date dateCreated;
    private Date dateLastUpdated;

    /**
     *
     */
    public static class JSONMapping
    {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
    }

    /**
     *
     */
    public Flow() {
        super();
    }

    /**
     *
     * @param flowIdParam
     */
    public Flow(Long flowIdParam) {
        super();

        this.setId(flowIdParam);
    }

    /**
     *
     * @param jsonObjectParam
     */
    public Flow(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(JSONMapping.NAME));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
        }

        //Date Created...
        if (!this.jsonObject.isNull(JSONMapping.DATE_CREATED)) {
            this.setDateCreated(
                    this.getLongAsDateFromJson(this.jsonObject.getLong(JSONMapping.DATE_CREATED))
            );
        }

        //Date Last Updated...
        if (!this.jsonObject.isNull(JSONMapping.DATE_LAST_UPDATED)) {
            this.setDateLastUpdated(
                    this.getLongAsDateFromJson(this.jsonObject.getLong(JSONMapping.DATE_LAST_UPDATED))
            );
        }
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
    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @param descriptionParam
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
    }

    /**
     *
     * @return
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     *
     * @param dateCreatedParam
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     *
     * @return
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     *
     * @param dateLastUpdatedParam
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     *
     * @return
     * @throws org.json.JSONException
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME,this.getName());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION, this.getDescription());
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(
                    this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(
                    this.getDateLastUpdated()));
        }

        return returnVal;
    }
}
