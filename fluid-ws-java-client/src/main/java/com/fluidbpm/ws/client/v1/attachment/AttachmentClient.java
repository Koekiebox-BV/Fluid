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
	 * @param endpointBaseUrl URL to base endpoint.
	 * @param serviceTicket The Server issued Service Ticket.
	 */
	public AttachmentClient(String endpointBaseUrl, String serviceTicket) {
		super(endpointBaseUrl);
		this.setServiceTicket(serviceTicket);
	}

	/**
	 * Uploads a new Attachment.
	 * If there is an existing attachment with the same name, a new version will be
	 * uploaded.
	 *
	 * @param attachment The attachment to upload.
	 * @return The created attachment.
	 *
	 * @see Attachment
	 */
	public Attachment createAttachment(Attachment attachment) {
		if (attachment != null) attachment.setServiceTicket(this.serviceTicket);
		return new Attachment(this.putJson(attachment, WS.Path.Attachment.Version1.attachmentCreate()));
	}

	/**
	 * Retrieves a Attachment by Primary Key.
	 *
	 * @param attachmentId The Attachment primary key.
	 * @param includeAttachmentData Include the attachment data (Base-64).
	 *
	 * @return The Attachment associated with {@code attachmentIdParam}.
	 */
	public Attachment getAttachmentById(Long attachmentId, boolean includeAttachmentData) {
		Attachment attachment = new Attachment(attachmentId);
		attachment.setServiceTicket(this.serviceTicket);

		return new Attachment(this.postJson(
				attachment, WS.Path.Attachment.Version1.getById(includeAttachmentData)));
	}

	/**
	 * Retrieves all the Attachments associated with Form {@code formParam}.
	 *
	 * @param form The Form to use for lookup.
	 * @param includeAttachmentData Include the attachment data (Base-64).
	 *
	 * @return The Attachments associated with {@code formParam}.
	 */
	public List<Attachment> getAttachmentsByForm(Form form, boolean includeAttachmentData) {
		Form formPost = new Form(form.getId());
		formPost.setServiceTicket(this.serviceTicket);

		AttachmentListing returnedListing =
				new AttachmentListing(postJson(
						formPost, WS.Path.Attachment.Version1.getAllByFormContainer(
								includeAttachmentData,false)));

		return (returnedListing == null) ? null : returnedListing.getListing();
	}

	/**
	 * Retrieves only {@code image} Attachments by Primary Key.
	 *
	 * @param form The Form to use for lookup.
	 * @param includeAttachmentData Include the attachment data (Base-64).
	 *
	 * @return The Attachment associated with {@code attachmentIdParam}.
	 */
	public List<Attachment> getImageAttachmentsByForm(Form form, boolean includeAttachmentData) {
		Form formPost = new Form(form.getId());
		formPost.setServiceTicket(this.serviceTicket);

		AttachmentListing returnedListing =
				new AttachmentListing(postJson(formPost, WS.Path.Attachment.Version1.getAllByFormContainer(
						includeAttachmentData, true)));

		return (returnedListing == null) ? null : returnedListing.getListing();
	}

	/**
	 * Delete an existing Attachment.
	 *
	 * @param attachment The Attachment to delete.
	 * @return The deleted Attachment.
	 */
	public Attachment deleteAttachment(Attachment attachment) {
		Attachment toPost = new Attachment(attachment == null ? null : attachment.getId());
		toPost.setServiceTicket(this.serviceTicket);

		return new Attachment(this.postJson(toPost, WS.Path.Attachment.Version1.attachmentDelete()));
	}

	/**
	 * Forcefully Delete an existing Attachment.
	 *
	 * Only 'admin' can forcefully delete a Attachment.
	 *
	 * @param attachment The Attachment to delete.
	 * @return The deleted Attachment.
	 */
	public Attachment forceDeleteAttachment(Attachment attachment) {
		Attachment toPost = new Attachment(attachment == null ? null : attachment.getId());
		toPost.setServiceTicket(this.serviceTicket);

		return new Attachment(this.postJson(toPost, WS.Path.Attachment.Version1.attachmentDelete(true)));
	}
}
