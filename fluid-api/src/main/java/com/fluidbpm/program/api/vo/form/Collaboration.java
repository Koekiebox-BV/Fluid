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

package com.fluidbpm.program.api.vo.form;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.user.User;

/**
 * Represents a Collaboration of a Form between multiple users.
 *
 * @author jasonbruwer
 * @since v1.9
 *
 * @see Form
 * @see User
 */
public class Collaboration extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private Form formContainer;
    private User fromUser;
    private User toUser;

    private Date dateSent;
    private Date dateRead;
    private String message;

    /**
     * The JSON mapping for the {@code Collaboration} object.
     */
    public static class JSONMapping {
        public static final String FORM_CONTAINER= "formContainer";
        public static final String FROM_USER = "fromUser";
        public static final String TO_USER = "toUser";
        public static final String DATE_SENT = "dateSent";
        public static final String DATE_READ = "dateRead";
        public static final String MESSAGE = "message";
    }

    /**
     * Default constructor.
     */
    public Collaboration() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Collaboration(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        //Form Container...
        if (!this.jsonObject.isNull(JSONMapping.FORM_CONTAINER)) {
            this.setFormContainer(
                    new Form(this.jsonObject.getJSONObject(JSONMapping.FORM_CONTAINER)));
        }

        //From User...
        if (!this.jsonObject.isNull(JSONMapping.FROM_USER)) {

            JSONObject jsonObj = this.jsonObject.getJSONObject(
                    JSONMapping.FROM_USER);
            User fromUser = new User();

            //User Id
            if (!jsonObj.isNull(User.JSONMapping.Elastic.USER_ID)) {
                fromUser.setId(jsonObj.getLong(User.JSONMapping.Elastic.USER_ID));
            }
            //Id is set, make use of that instead...
            else if (!jsonObj.isNull(
                    ABaseFluidJSONObject.JSONMapping.ID)) {
                fromUser.setId(jsonObj.getLong(ABaseFluidJSONObject.JSONMapping.ID));
            }

            //Username
            if (!jsonObj.isNull(User.JSONMapping.USERNAME)) {
                fromUser.setUsername(jsonObj.getString(User.JSONMapping.USERNAME));
            }

            this.setFromUser(fromUser);
        }

        //To User...
        if (!this.jsonObject.isNull(JSONMapping.TO_USER)) {

            JSONObject jsonObj = this.jsonObject.getJSONObject(JSONMapping.TO_USER);
            User toUser = new User();

            //User Id
            if (!jsonObj.isNull(User.JSONMapping.Elastic.USER_ID)) {
                toUser.setId(jsonObj.getLong(User.JSONMapping.Elastic.USER_ID));
            }
            //Id is set, make use of that instead...
            else if (!jsonObj.isNull(
                    ABaseFluidJSONObject.JSONMapping.ID)) {
                toUser.setId(jsonObj.getLong(ABaseFluidJSONObject.JSONMapping.ID));
            }

            //Username
            if (!jsonObj.isNull(User.JSONMapping.USERNAME)) {
                toUser.setUsername(jsonObj.getString(User.JSONMapping.USERNAME));
            }

            this.setToUser(toUser);
        }

        //Date Read...
        this.setDateRead(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_READ));

        //Date Sent...
        this.setDateSent(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_SENT));

        //Message...
        if (!this.jsonObject.isNull(JSONMapping.MESSAGE)) {
            this.setMessage(this.jsonObject.getString(JSONMapping.MESSAGE));
        }
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Field}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Form Container...
        if(this.getFormContainer() != null) {
            returnVal.put(JSONMapping.FORM_CONTAINER,
                    this.getFormContainer().toJsonObject());
        }

        //Message...
        if(this.getMessage() != null) {
            returnVal.put(JSONMapping.MESSAGE, this.getMessage());
        }

        //Date Read...
        if(this.getDateRead() != null) {
            returnVal.put(JSONMapping.DATE_READ,
                    this.getDateAsLongFromJson(this.getDateRead()));
        }

        //Date Sent...
        if(this.getDateSent() != null) {
            returnVal.put(JSONMapping.DATE_SENT,
                    this.getDateAsLongFromJson(this.getDateSent()));
        }

        //From User...
        if(this.getFromUser() != null) {
            returnVal.put(
                    JSONMapping.FROM_USER,
                    this.getFromUser().toJsonObject());
        }

        //To User...
        if(this.getToUser() != null) {
            returnVal.put(
                    JSONMapping.TO_USER,
                    this.getToUser().toJsonObject());
        }

        return returnVal;
    }

    /**
     * Gets the electronic Form Container.
     *
     * @return The Form.
     *
     * @see Form
     */
    public Form getFormContainer() {
        return this.formContainer;
    }

    /**
     * Sets the electronic Form Container.
     *
     * @param formContainerParam The Form.
     *
     * @see Form
     */
    public void setFormContainer(Form formContainerParam) {
        this.formContainer = formContainerParam;
    }

    /**
     * Gets the {@code User} who shared the {@code Form}.
     *
     * @return The 'FROM' User.
     *
     * @see User
     */
    public User getFromUser() {
        return this.fromUser;
    }

    /**
     * Sets the {@code User} who shared the {@code Form}.
     *
     * @param fromUserParam The 'FROM' User.
     *
     * @see User
     */
    public void setFromUser(User fromUserParam) {
        this.fromUser = fromUserParam;
    }

    /**
     * Gets the {@code User} who the {@code Form} is shared with.
     *
     * @return The 'TO' User.
     *
     * @see User
     */
    public User getToUser() {
        return this.toUser;
    }

    /**
     * Sets the {@code User} who the {@code Form} is shared with.
     *
     * @param toUserParam The 'TO' User.
     *
     * @see User
     */
    public void setToUser(User toUserParam) {
        this.toUser = toUserParam;
    }

    /**
     * Gets The {@code Date} the Collaboration was sent.
     *
     * @return Date Sent.
     */
    public Date getDateSent() {
        return this.dateSent;
    }

    /**
     * Sets The {@code Date} the Collaboration was sent.
     *
     * @param dateSentParam Date Sent.
     */
    public void setDateSent(Date dateSentParam) {
        this.dateSent = dateSentParam;
    }

    /**
     * Gets The {@code Date} the Collaboration was read by the 'TO' {@code User}.
     *
     * @return Date Read.
     */
    public Date getDateRead() {
        return this.dateRead;
    }

    /**
     * Sets The {@code Date} the Collaboration was read by the 'TO' {@code User}.
     *
     * @param dateReadParam Date Read.
     */
    public void setDateRead(Date dateReadParam) {
        this.dateRead = dateReadParam;
    }

    /**
     * Gets the message intended for the 'TO' {@code User}.
     *
     * @return Message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message intended for the 'TO' {@code User}.
     *
     * @param messageParam The message for the 'TO' {@code User}.
     */
    public void setMessage(String messageParam) {
        this.message = messageParam;
    }
}
