package com.fluidbpm.ws.client.v1.websocket;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.compress.CompressedResponse;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.ws.client.FluidClientException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Base list message handler.
 *
 * @author jasonbruwer on 2016/03/11.
 * @since 1.1
 *
 * @param <T> The response object generic.
 *
 * @see CompletableFuture
 */
public abstract class AGenericListMessageHandler<T extends ABaseFluidJSONObject> implements IMessageResponseHandler {

	private WebSocketClient webSocketClient;
	
	private final List<T> returnValue;
	private List<Error> errors;

	private IMessageReceivedCallback<T> messageReceivedCallback;
	private boolean isConnectionClosed;

	private Set<String> expectedEchoMessagesBeforeComplete;
	private CompletableFuture<List<T>> completableFuture;

	private boolean compressedResponse;

	public static Charset CHARSET = null;

	/**
	 * Constructor for SQLResultSet callbacks.
	 *
	 * @param messageReceivedCallbackParam The callback events.
	 * @param webSocketClientParam The web-socket client.
	 * @param compressedResponseParam Compress the SQL Result in Base-64.
	 */
	public AGenericListMessageHandler(
		IMessageReceivedCallback<T> messageReceivedCallbackParam,
		WebSocketClient webSocketClientParam,
		boolean compressedResponseParam
	) {
		this(messageReceivedCallbackParam, webSocketClientParam);
		this.compressedResponse = compressedResponseParam;
	}

	/**
	 * Message handler with callback.
	 *
	 * @param messageReceivedCallbackParam The message callback observer.
	 * @param webSocketClientParam The web-socket client.
	 */
	public AGenericListMessageHandler(
		IMessageReceivedCallback<T> messageReceivedCallbackParam,
		WebSocketClient webSocketClientParam
	) {
		this.messageReceivedCallback = messageReceivedCallbackParam;
		this.webSocketClient = webSocketClientParam;
		this.returnValue = new CopyOnWriteArrayList();
		this.errors = new CopyOnWriteArrayList();
		this.expectedEchoMessagesBeforeComplete = new CopyOnWriteArraySet();
		this.isConnectionClosed = false;
	}

	/**
	 * Checks whether {@code this} message handler can process
	 * the message {@code messageParam}
	 *
	 * @param message The message to check for qualification.
	 * @return The JSONObject.
	 *
	 * @see JSONObject
	 */
	public Object doesHandlerQualifyForProcessing(String message) {
		JsonObject jsonObject = null;
		try {
			jsonObject = new JSONObject(message);
		} catch (JSONException jsonExcept) {
			throw new FluidClientException(
					"Unable to parse ["+message+"]. "+ jsonExcept.getMessage(),
					jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
		}

		Error fluidError = new Error(jsonObject);
		if (fluidError.getErrorCode() > 0) return fluidError;

		String echo = fluidError.getEcho();
		if (this.expectedEchoMessagesBeforeComplete.contains(echo)) return jsonObject;

		return null;
	}

	/**
	 * 'Handles' the message.
	 * If there was an error, the object will be Error
	 * If there was no error, the object will be JSONObject
	 *
	 * @param objectToProcess The message to handle in JSON format.
	 *
	 * @see Error
	 * @see JSONObject
	 */
	@Override
	public void handleMessage(Object objectToProcess) {
		//There is an error...
		if (objectToProcess instanceof Error) {
			Error fluidError = ((Error)objectToProcess);
			this.errors.add(fluidError);

			//Do a message callback...
			if (this.messageReceivedCallback != null) this.messageReceivedCallback.errorMessageReceived(fluidError);

			//If complete future is provided...
			if (this.completableFuture != null) {
				this.completableFuture.completeExceptionally(
						new FluidClientException(fluidError.getErrorMessage(), fluidError.getErrorCode()));
			}
		} else {
			//No Error...
			JsonObject jsonObject = (JSONObject)objectToProcess;

			//Uncompress the compressed response...
			if (this.compressedResponse) {
				CompressedResponse compressedResponse = new CompressedResponse(jsonObject);

				byte[] compressedJsonList =
						UtilGlobal.decodeBase64(compressedResponse.getDataBase64());
				byte[] uncompressedJson = null;
				try {
					uncompressedJson = this.uncompress(compressedJsonList);
				} catch (IOException eParam) {
					throw new FluidClientException(
							"I/O issue with uncompress. "+eParam.getMessage(),
							eParam,
							FluidClientException.ErrorCode.IO_ERROR);
				}

				jsonObject = new JSONObject(new String(uncompressedJson));
			}

			T messageForm = this.getNewInstanceBy(jsonObject);

			//Add to the list of return values...
			this.returnValue.add(messageForm);

			//Do a message callback...
			if (this.messageReceivedCallback != null) this.messageReceivedCallback.messageReceived(messageForm);

			//Completable future is set, and all response messages received...
			if (this.completableFuture != null) {
				String echo = messageForm.getEcho();
				if (echo != null && !echo.trim().isEmpty()) this.expectedEchoMessagesBeforeComplete.remove(echo);

				if (this.webSocketClient.getSentMessages() == this.webSocketClient.getReceivedMessages()) {
					//Sent and received messages match...
					this.completableFuture.complete(this.returnValue);
				} else if (this.expectedEchoMessagesBeforeComplete.isEmpty()) {
					//All expected messages received...
					this.completableFuture.complete(this.returnValue);
				}
			}
		}
	}

	/**
	 * Retrieve the stored instance of {@code CompletableFuture}.
	 * @return local instance of CompletableFuture.
	 *
	 * @see CompletableFuture
	 */
	public CompletableFuture<List<T>> getCF() {
		if (this.completableFuture == null) this.completableFuture = new CompletableFuture<>();

		return this.completableFuture;
	}

	/**
	 * Event for when connection is closed.
	 */
	@Override
	public void connectionClosed() {
		this.isConnectionClosed = true;

		if (this.completableFuture != null) {
			//If there was no error...
			if (this.getErrors().isEmpty()) {
				this.completableFuture.complete(this.returnValue);
			} else {
				//there was an error...
				Error firstFluidError = this.getErrors().get(0);
				this.completableFuture.completeExceptionally(new FluidClientException(
						firstFluidError.getErrorMessage(),
						firstFluidError.getErrorCode()));
			}
		}
	}

	/**
	 * Checks whether the connection to the server is closed.
	 *
	 * @return Whether connection is closed.
	 */
	public boolean isConnectionClosed() {
		return this.isConnectionClosed;
	}

	/**
	 * Checks to see whether there are error responses.
	 *
	 * @return Whether there is an error.
	 */
	public boolean hasErrorOccurred()
	{
		return !this.errors.isEmpty();
	}

	/**
	 * Adds the {@code expectedMessageEchoParam} echo to expect as a
	 * return value before the Web Socket operation may be regarded as complete.
	 *
	 * @param expectedMessageEchoParam The echo to expect.
	 */
	public void addExpectedMessage(String expectedMessageEchoParam) {
		if (expectedMessageEchoParam == null || expectedMessageEchoParam.trim().isEmpty()) return;

		this.expectedEchoMessagesBeforeComplete.add(expectedMessageEchoParam);
	}

	/**
	 * Get the list of remaining messages to receive.
	 *
	 * @return The message waiting for responses.
	 */
	public Set<String> getExpectedEchoMessagesBeforeComplete() {
		return this.expectedEchoMessagesBeforeComplete;
	}

	/**
	 * Error listing.
	 *
	 * @return The error that occured.
	 */
	public List<Error> getErrors() {
		return this.errors;
	}

	/**
	 * Create a new instance of {@code T}.
	 *
	 * @param jsonObjectParam The {@code JSONObject} to create an object from.
	 * @return An instance of {@code ABaseFluidJSONObject}.
	 *
	 * @see ABaseFluidJSONObject
	 * @see JSONObject
	 */
	public abstract T getNewInstanceBy(JsonObject jsonObjectParam);

	/**
	 * Gets the size of the return value.
	 *
	 * @return Added Count.
	 */
	public int getAddedCount() {
		return this.returnValue.size();
	}

	/**
	 * Gets a list of echo messages of the current return values.
	 *
	 * @return The return value echo messages.
	 */
	private List<String> getEchoMessagesFromReturnValue() {
		List<String> returnListing = new ArrayList();

		if (this.returnValue == null) return returnListing;

		Iterator<T> iterForReturnVal =
				this.returnValue.iterator();

		//Only add where the ECHO message is set...
		while (iterForReturnVal.hasNext()) {
			T returnVal = iterForReturnVal.next();
			if (returnVal.getEcho() == null) continue;

			returnListing.add(returnVal.getEcho());
		}

		return returnListing;
	}

	/**
	 * Checks the local return value echo messages if all
	 * of them contain {@code echoMessageParam}.
	 *
	 * @param echoMessage The echo messages to check.
	 *
	 * @return Whether local return value echo messages contain {@code echoMessageParam}.
	 */
	public boolean doReturnValueEchoMessageContainAll(
		List<String> echoMessage
	) {
		if (echoMessage == null || echoMessage.isEmpty()) return false;

		List<String> allReturnValueEchoMessages =
				this.getEchoMessagesFromReturnValue();

		for (String toCheckFor: echoMessage) {
			if (!allReturnValueEchoMessages.contains(toCheckFor)) return false;
		}

		return true;
	}

	/**
	 * Clears the return value.
	 */
	public void clear() {
		this.returnValue.clear();
	}

	/**
	 * Gets the return value.
	 *
	 * @return The return value listing.
	 */
	public List<T> getReturnValue() {
		return this.returnValue;
	}

	/**
	 * Uncompress the raw {@code compressedBytesParam}.
	 *
	 * @param compressedBytesParam The compressed bytes to uncompress.
	 *
	 * @return Uncompressed bytes.
	 *
	 * @throws IOException - If there is an issue during the un-compression.
	 */
	protected byte[] uncompress(byte[] compressedBytesParam) throws IOException {
		return UtilGlobal.uncompress(compressedBytesParam, CHARSET);
	}
}
