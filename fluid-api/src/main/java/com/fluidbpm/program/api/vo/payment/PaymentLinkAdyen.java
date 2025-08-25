/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2021] Koekiebox (Pty) Ltd
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

package com.fluidbpm.program.api.vo.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <p>
 * An Adyen Payment link that will forward a user to Adyen where a eCommerce transaction can be performed.
 * See; https://docs.adyen.com/online-payments/pay-by-link/api
 * https://docs.adyen.com/api-explorer/#/CheckoutService/paymentLinks
 * </p>
 *
 * @author jasonbruwer
 * @see Form
 * @since v1.12
 */
@Getter
@Setter
public class PaymentLinkAdyen extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;
    private String paymentLink;

    /**
     * The JSON mapping for the {@code PaymentLinkAdyen} object.
     */
    public static class JSONMapping {
        public static final String PAYMENT_LINK = "paymentLink";
    }

    /**
     * Default constructor.
     */
    public PaymentLinkAdyen() {
        super();
    }

    /**
     * Constructor to create {@code PaymentLinkAdyen} with a payment link.
     *
     * @param paymentLink The unique payment link
     */
    public PaymentLinkAdyen(String paymentLink) {
        super();
        this.setPaymentLink(paymentLink);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public PaymentLinkAdyen(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setPaymentLink(this.getAsStringNullSafe(JSONMapping.PAYMENT_LINK));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code PaymentLinkAdyen}
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        this.setAsProperty(JSONMapping.PAYMENT_LINK, returnVal, this.getPaymentLink());
        return returnVal;
    }

    /**
     * String value for a table field.
     *
     * @return JSON text from the table field.
     */
    @Override
    public String toString() {
        JsonObject jsonObject = this.toJsonObject();
        if (jsonObject != null) return jsonObject.toString();
        return UtilGlobal.EMPTY;
    }
}
