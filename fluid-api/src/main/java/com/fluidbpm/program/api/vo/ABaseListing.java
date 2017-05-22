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

import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     Represents any list returned from the Web Service as {@code T}.
 * </p>
 *
 * @author jasonbruwer
 * @since v1.1
 *
 * @see JSONObject
 *
 */
public abstract class ABaseListing<T extends ABaseFluidJSONObject> extends ABaseFluidJSONObject{

    public static final long serialVersionUID = 1L;

    private List<T> listing;

    private Integer listingCount;
    private Integer listingIndex;
    private Integer listingPage;

    /**
     * The JSON mapping for the {@code ABaseListing} object.
     */
    public static class JSONMapping
    {
        public static final String LISTING = "listing";

        public static final String LISTING_COUNT = "listingCount";
        public static final String LISTING_INDEX = "listingIndex";
        public static final String LISTING_PAGE = "listingPage";
    }

    /**
     * Default constructor.
     */
    public ABaseListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public ABaseListing(JSONObject jsonObjectParam){
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Listing...
        int listingArrCount = 0;
        if (!this.jsonObject.isNull(JSONMapping.LISTING)) {

            JSONArray listingArray = this.jsonObject.getJSONArray(
                    JSONMapping.LISTING);
            listingArrCount = listingArray.length();

            List<T> listing = new ArrayList();

            for(int index = 0;index < listingArrCount;index++)
            {
                listing.add(this.getObjectFromJSONObject(
                        listingArray.getJSONObject(index)));
            }

            this.setListing(listing);
        }

        //Listing Count...
        if (this.jsonObject.isNull(JSONMapping.LISTING_COUNT)) {

            this.setListingCount(listingArrCount);
        }
        else
        {
            this.setListingCount(this.jsonObject.getInt(JSONMapping.LISTING_COUNT));
        }

        //Listing Index...
        if (!this.jsonObject.isNull(JSONMapping.LISTING_INDEX)) {

            this.setListingIndex(this.jsonObject.getInt(JSONMapping.LISTING_INDEX));
        }

        //Listing Page...
        if (!this.jsonObject.isNull(JSONMapping.LISTING_PAGE)) {

            this.setListingPage(this.jsonObject.getInt(JSONMapping.LISTING_PAGE));
        }
    }

    /**
     * Gets the listing of {@code T} objects.
     *
     * @return List of {@code T} type objects.
     */
    public List<T> getListing() {
        return this.listing;
    }

    /**
     * Sets the listing of {@code T} objects.
     *
     * @param listingParam List of {@code T} type objects.
     */
    public void setListing(List<T> listingParam) {
        this.listing = listingParam;
    }

    /**
     * Gets the listing count.
     *
     * @return Listing count.
     */
    public Integer getListingCount() {
        return this.listingCount;
    }

    /**
     * Sets the listing count.
     *
     * @param listingCountParam Listing count.
     */
    public void setListingCount(Integer listingCountParam) {
        this.listingCount = listingCountParam;
    }

    /**
     * Gets the listing index.
     *
     * @return Listing index.
     */
    public Integer getListingIndex() {
        return this.listingIndex;
    }

    /**
     * Sets the listing index.
     *
     * @param listingIndexParam Listing index.
     */
    public void setListingIndex(Integer listingIndexParam) {
        this.listingIndex = listingIndexParam;
    }

    /**
     * Gets the listing page.
     *
     * @return Listing page.
     */
    public Integer getListingPage() {
        return listingPage;
    }

    /**
     * Sets the listing page.
     *
     * @param listingPageParam Listing page.
     */
    public void setListingPage(Integer listingPageParam) {
        this.listingPage = listingPageParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToFormDefinition}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    public JSONObject toJsonObject() throws JSONException {

        JSONObject returnVal = super.toJsonObject();

        //Listing...
        int listingCountFromListing = 0;
        if(this.getListing() != null && !this.getListing().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();
            listingCountFromListing = this.getListing().size();
            for(T toAdd :this.getListing())
            {
                jsonArray.put(toAdd.toJsonObject());
            }

            returnVal.put(JSONMapping.LISTING, jsonArray);
        }

        //Listing count...
        if(this.getListingCount() == null)
        {
            returnVal.put(JSONMapping.LISTING_COUNT, new Integer(listingCountFromListing));
        }
        else
        {
            returnVal.put(JSONMapping.LISTING_COUNT, this.getListingCount());
        }

        //Listing index...
        if(this.getListingIndex() != null)
        {
            returnVal.put(JSONMapping.LISTING_INDEX, this.getListingIndex());
        }

        //Listing page...
        if(this.getListingIndex() != null)
        {
            returnVal.put(JSONMapping.LISTING_PAGE, this.getListingPage());
        }

        return returnVal;
    }

    /**
     * Provides the sub-class with a {@code JSONObject}, and
     * expects a {@code Java} object in return.
     *
     * @param jsonObjectParam The JSON object to convert to {@code T}.
     * @return T from constructor.
     *
     * @see JSONObject
     */
    @XmlTransient
    public abstract T getObjectFromJSONObject(JSONObject jsonObjectParam);
}
