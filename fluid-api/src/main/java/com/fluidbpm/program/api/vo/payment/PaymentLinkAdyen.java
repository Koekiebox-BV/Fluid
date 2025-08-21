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

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.form.Form;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 *     An Adyen Payment link that will forward a user to Adyen where a eCommerce transaction can be performed.
 *     See; https://docs.adyen.com/online-payments/pay-by-link/api
 *     https://docs.adyen.com/api-explorer/#/CheckoutService/paymentLinks
 * </p>
 *
 * @author jasonbruwer
 * @since v1.12
 *
 * @see Form
 */
@Getter
@Setter
public class PaymentLinkAdyen extends ABaseFluidJSONObject {
	public static final long serialVersionUID = 1L;
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
	 * @param jsonObjectParam The JSON Object.
	 */
	public PaymentLinkAdyen(JsonObject jsonObjectParam) {
		super(jsonObjectParam);
		if (this.jsonObject == null) return;

		if (!this.jsonObject.isNull(JSONMapping.PAYMENT_LINK)) {
			this.setPaymentLink(this.jsonObject.getString(JSONMapping.PAYMENT_LINK));
		}
	}

	/**
	 * Conversion to {@code JSONObject} from Java Object.
	 *
	 * @return {@code JSONObject} representation of {@code PaymentLinkAdyen}
	 * @throws JSONException If there is a problem with the JSON Body.
	 *
	 * @see ABaseFluidJSONObject#toJsonObject()
	 */
	@Override
	public JsonObject toJsonObject() throws JSONException {
		JsonObject returnVal = super.toJsonObject();
		if (this.getPaymentLink() != null) {
			returnVal.put(JSONMapping.PAYMENT_LINK, this.getPaymentLink());
		}
		return returnVal;
	}

	/**
	 * String value for a table field.
	 * @return JSON text from the table field.
	 */
	@Override
	public String toString() {
		JsonObject jsonObject = this.toJsonObject();
		if (jsonObject != null) return jsonObject.toString();
		return UtilGlobal.EMPTY;
	}
}
