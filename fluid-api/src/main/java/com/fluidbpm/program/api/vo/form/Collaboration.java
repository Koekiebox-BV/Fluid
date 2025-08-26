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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Represents a Collaboration of a Form between multiple users.
 *
 * @author jasonbruwer
 * @see Form
 * @see User
 * @since v1.9
 */
@Getter
@Setter
public class Collaboration extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

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
        public static final String FORM_CONTAINER = "formContainer";
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
     * Sets the collaboration for a creation.
     *
     * @param formContainerParam The Form for the collaboration.
     * @param fromUserParam      The from user.
     * @param toUserParam        The to user.
     * @see Form
     * @see User
     */
    public Collaboration(Form formContainerParam, User fromUserParam, User toUserParam) {
        this();
        this.setFormContainer(formContainerParam);
        this.setFromUser(fromUserParam);
        this.setToUser(toUserParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Collaboration(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        // Form Container
        this.setFormContainer(this.extractObject(JSONMapping.FORM_CONTAINER, Form::new));

        // From User
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.FROM_USER)) {
            JsonObject jsonObj = this.jsonObject.getAsJsonObject(JSONMapping.FROM_USER);
            User fromUser = new User();
            if (this.isPropertyNotNull(jsonObj, User.JSONMapping.Elastic.USER_ID)) {
                fromUser.setId(jsonObj.get(User.JSONMapping.Elastic.USER_ID).getAsLong());
            } else if (this.isPropertyNotNull(jsonObj, ABaseFluidGSONObject.JSONMapping.ID)) {
                fromUser.setId(jsonObj.get(ABaseFluidGSONObject.JSONMapping.ID).getAsLong());
            }
            if (this.isPropertyNotNull(jsonObj, User.JSONMapping.USERNAME)) {
                fromUser.setUsername(jsonObj.get(User.JSONMapping.USERNAME).getAsString());
            }
            this.setFromUser(fromUser);
        }

        // To User
        if (this.isPropertyNotNull(this.jsonObject, JSONMapping.TO_USER)) {
            JsonObject jsonObj = this.jsonObject.getAsJsonObject(JSONMapping.TO_USER);
            User toUser = new User();
            if (this.isPropertyNotNull(jsonObj, User.JSONMapping.Elastic.USER_ID)) {
                toUser.setId(jsonObj.get(User.JSONMapping.Elastic.USER_ID).getAsLong());
            } else if (this.isPropertyNotNull(jsonObj, ABaseFluidGSONObject.JSONMapping.ID)) {
                toUser.setId(jsonObj.get(ABaseFluidGSONObject.JSONMapping.ID).getAsLong());
            }
            if (this.isPropertyNotNull(jsonObj, User.JSONMapping.USERNAME)) {
                toUser.setUsername(jsonObj.get(User.JSONMapping.USERNAME).getAsString());
            }
            this.setToUser(toUser);
        }

        this.setDateRead(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_READ));
        this.setDateSent(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_SENT));
        this.setMessage(this.getAsStringNullSafe(JSONMapping.MESSAGE));
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Field}
     * 
     * 
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();

        this.setAsObj(JSONMapping.FORM_CONTAINER, returnVal, this::getFormContainer);
        this.setAsProperty(JSONMapping.MESSAGE, returnVal, this.getMessage());
        this.setAsProperty(JSONMapping.DATE_READ, returnVal, this.getDateAsLongFromJson(this.getDateRead()));
        this.setAsProperty(JSONMapping.DATE_SENT, returnVal, this.getDateAsLongFromJson(this.getDateSent()));
        this.setAsObj(JSONMapping.FROM_USER, returnVal, this::getFromUser);
        this.setAsObj(JSONMapping.TO_USER, returnVal, this::getToUser);

        return returnVal;
    }
}
