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

package com.fluidbpm.ws.client.v1.attachment;

import com.fluidbpm.program.api.vo.attachment.Attachment;
import com.fluidbpm.program.api.vo.attachment.AttachmentListing;
import com.fluidbpm.program.api.vo.form.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

import java.util.List;

/**
 * Used to upload attachment data for {@code Form}s.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.8
 * @version v1.8
 *
 * @see com.fluidbpm.program.api.vo.attachment.Attachment
 * @see ABaseClientWS
 */
public class AttachmentClient extends ABaseClientWS {

	/**
	 * Constructor that sets the Service Ticket from authentication.
	 *
	 * @param endpointBaseUrlParam URL to base endpoint.
	 * @param serviceTicketParam The Server issued Service Ticket.
	 */
	public AttachmentClient(String endpointBaseUrlParam,
							String serviceTicketParam) {
		super(endpointBaseUrlParam);

		this.setServiceTicket(serviceTicketParam);
	}

	/**
	 * Uploads a new Attachment.
	 * If there is an existing attachment with the same name, a new version will be
	 * uploaded.
	 *
	 * @param attachmentParam The attachment to upload.
	 * @return The created attachment.
	 *
	 * @see Attachment
	 */
	public Attachment createAttachment(Attachment attachmentParam) {
		if (attachmentParam != null && this.serviceTicket != null) {
			attachmentParam.setServiceTicket(this.serviceTicket);
		}

		return new Attachment(this.putJson(attachmentParam, WS.Path.Attachment.Version1.attachmentCreate()));
	}

	/**
	 * Retrieves a Attachment by Primary Key.
	 *
	 * @param attachmentIdParam The Attachment primary key.
	 * @param includeAttachmentDataParam Include the attachment data (Base-64).
	 *
	 * @return The Attachment associated with {@code attachmentIdParam}.
	 */
	public Attachment getAttachmentById(
		Long attachmentIdParam,
		boolean includeAttachmentDataParam
	) {
		Attachment attachment = new Attachment(attachmentIdParam);
		if (this.serviceTicket != null) {
			attachment.setServiceTicket(this.serviceTicket);
		}

		return new Attachment(this.postJson(
				attachment, WS.Path.Attachment.Version1.getById(
						includeAttachmentDataParam)));
	}

	/**
	 * Retrieves all the Attachments associated with Form {@code formParam}.
	 *
	 * @param form The Form to use for lookup.
	 * @param includeAttachmentDataParam Include the attachment data (Base-64).
	 *
	 * @return The Attachments associated with {@code formParam}.
	 */
	public List<Attachment> getAttachmentsByForm(
		Form form,
		boolean includeAttachmentDataParam
	) {
		Form formPost = new Form(form.getId());
		formPost.setServiceTicket(this.serviceTicket);

		AttachmentListing returnedListing =
				new AttachmentListing(postJson(
						formPost, WS.Path.Attachment.Version1.getAllByFormContainer(
								includeAttachmentDataParam,false)));

		return (returnedListing == null) ? null : returnedListing.getListing();
	}

	/**
	 * Retrieves only {@code image} Attachments by Primary Key.
	 *
	 * @param form The Form to use for lookup.
	 * @param includeAttachmentDataParam Include the attachment data (Base-64).
	 *
	 * @return The Attachment associated with {@code attachmentIdParam}.
	 */
	public List<Attachment> getImageAttachmentsByForm(
		Form form,
		boolean includeAttachmentDataParam
	) {
		Form formPost = new Form(form.getId());
		formPost.setServiceTicket(this.serviceTicket);

		AttachmentListing returnedListing =
				new AttachmentListing(postJson(
						formPost, WS.Path.Attachment.Version1.getAllByFormContainer(
								includeAttachmentDataParam, true)));

		return (returnedListing == null) ? null : returnedListing.getListing();
	}

	/**
	 * Delete an existing Attachment.
	 *
	 * @param attachmentParam The Attachment to delete.
	 * @return The deleted Attachment.
	 */
	public Attachment deleteAttachment(Attachment attachmentParam) {
		if (attachmentParam != null && this.serviceTicket != null) {
			attachmentParam.setServiceTicket(this.serviceTicket);
		}

		return new Attachment(this.postJson(
				attachmentParam, WS.Path.Attachment.Version1.attachmentDelete()));
	}

	/**
	 * Forcefully Delete an existing Attachment.
	 *
	 * Only 'admin' can forcefully delete a Attachment.
	 *
	 * @param attachmentParam The Attachment to delete.
	 * @return The deleted Attachment.
	 */
	public Attachment forceDeleteAttachment(Attachment attachmentParam) {
		if (attachmentParam != null && this.serviceTicket != null) {
			attachmentParam.setServiceTicket(this.serviceTicket);
		}

		return new Attachment(this.postJson(
				attachmentParam,
				WS.Path.Attachment.Version1.attachmentDelete(true)));
	}
}
