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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * <p>
 * Represents any list returned from the Web Service as {@code T}.
 * </p>
 *
 * @author jasonbruwer
 * @see JsonObject
 * @since v1.13
 */
@Getter
@Setter
public abstract class ABaseGSONListing<T extends ABaseFluidGSONObject> extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private List<T> listing;
    private Integer listingCount;
    private Integer listingIndex;
    private Integer listingPage;

    /**
     * The JSON mapping for the {@code ABaseListing} object.
     */
    public static class JSONMapping {
        public static final String LISTING = "listing";
        public static final String LISTING_COUNT = "listingCount";
        public static final String LISTING_INDEX = "listingIndex";
        public static final String LISTING_PAGE = "listingPage";
    }

    /**
     * Default constructor.
     */
    public ABaseGSONListing() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     * @param jsonObject The JSON Object.
     */
    public ABaseGSONListing(JsonObject jsonObject) {
        super(jsonObject);
        if (this.jsonObject == null) return;

        this.setListing(this.extractObjects(JSONMapping.LISTING, this::getObjectFromJSONObject));
        if (this.isPropertyNull(this.jsonObject, JSONMapping.LISTING_COUNT)) {
            this.setListingCount(this.getListing().size());
        } else {
            this.setListingCount(this.jsonObject.get(JSONMapping.LISTING_COUNT).getAsInt());
        }

        this.setListingIndex(this.getAsIntegerNullSafe(JSONMapping.LISTING_INDEX));
        this.setListingPage(this.getAsIntegerNullSafe(JSONMapping.LISTING_PAGE));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code RoleToFormDefinition}
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        int listingCountFromListing = this.setAsObjArray(JSONMapping.LISTING, returnVal, this::getListing);
        this.setAsProperty(JSONMapping.LISTING_COUNT, returnVal, this.getListingCount() == null
                ? listingCountFromListing : this.getListingCount());
        this.setAsProperty(JSONMapping.LISTING_INDEX, returnVal, this.getListingIndex());
        this.setAsProperty(JSONMapping.LISTING_PAGE, returnVal, this.getListingPage());
        return returnVal;
    }

    /**
     * Provides the sub-class with a {@code JSONObject}, and
     * expects a {@code Java} object in return.
     *
     * @param jsonObjectParam The JSON object to convert to {@code T}.
     * @return T from constructor.
     */
    @XmlTransient
    @JsonIgnore
    public abstract T getObjectFromJSONObject(JsonObject jsonObjectParam);

    /**
     * Returns whether the listing is empty.
     *
     * @return {@code true} if the listing is {@code null} or empty.
     */
    @XmlTransient
    @JsonIgnore
    public boolean isListingEmpty() {
        return (this.listing == null || this.listing.isEmpty());
    }
}
