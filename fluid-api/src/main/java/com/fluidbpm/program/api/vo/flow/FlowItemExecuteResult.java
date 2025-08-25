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

package com.fluidbpm.program.api.vo.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fluidbpm.program.api.vo.ABaseFluidGSONObject;
import com.fluidbpm.program.api.vo.item.FluidItem;
import com.fluidbpm.program.api.vo.mail.MailMessage;
import com.fluidbpm.program.api.vo.user.User;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * <p>
 * Container <code>POJO</code> used to send back result from
 * {@code FlowItemExecutePacket}.
 * </p>
 *
 * @author jasonbruwer
 * @see FlowItemExecutePacket
 * @since v1.0
 */
@Getter
@Setter
public class FlowItemExecuteResult extends ABaseFluidGSONObject {
    private static final long serialVersionUID = 1L;

    private FlowStepRule flowStepRule;
    private FluidItem fluidItem;
    private List<FluidItem> fluidItems;

    private String assignmentRuleValue;
    private String statementResultAsString;

    //regards to schedule and flow-program...
    //flow-program...
    private String executePerFluidItemQuery;
    private Boolean progressToNextPhase;

    //schedule-program...
    private String fluidItemQuery;
    private String executionResult;
    private List<User> executeUsers;
    private User loggedInUser;
    private JobView view;
    private List<MailMessage> mailMessagesToSend;

    /**
     * The JSON mapping for the {@code FlowItemExecuteResult} object.
     */
    public static class JSONMapping {
        public static final String FLOW_STEP_RULE = "flowStepRule";
        public static final String FLUID_ITEM = "fluidItem";
        public static final String LOGGED_IN_USER = "loggedInUser";
        public static final String VIEW = "view";
        public static final String FLUID_ITEMS = "fluidItems";
        public static final String ASSIGNMENT_RULE_VALUE = "assignmentRuleValue";
        public static final String STATEMENT_RESULT_AS_STRING = "statementResultAsString";

        //flow programs...
        public static final String EXECUTE_PER_FLUID_ITEM_QUERY = "executePerFluidItemQuery";
        public static final String PROGRESS_TO_NEXT_PHASE = "progressToNextPhase";

        //schedule programs...
        public static final String FLUID_ITEM_QUERY = "fluidItemQuery";
        public static final String EXECUTION_RESULT = "executionResult";
        public static final String EXECUTE_USERS = "executeUsers";
        public static final String MAIL_MESSAGES_TO_SEND = "mailMessagesToSend";
    }

    /**
     * Default constructor.
     */
    public FlowItemExecuteResult() {
        super();
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public FlowItemExecuteResult(JsonObject jsonObjectParam) {
        super(jsonObjectParam);
        if (this.jsonObject == null) return;

        this.setFluidItem(this.extractObject(JSONMapping.FLUID_ITEM, FluidItem::new));
        this.setLoggedInUser(this.extractObject(JSONMapping.LOGGED_IN_USER, User::new));
        this.setView(this.extractObject(JSONMapping.VIEW, JobView::new));
        this.setFlowStepRule(this.extractObject(JSONMapping.FLOW_STEP_RULE, FlowStepRule::new));
        this.setAssignmentRuleValue(this.getAsStringNullSafe(JSONMapping.ASSIGNMENT_RULE_VALUE));
        this.setStatementResultAsString(this.getAsStringNullSafe(JSONMapping.STATEMENT_RESULT_AS_STRING));
        this.setExecutePerFluidItemQuery(this.getAsStringNullSafe(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY));
        this.setProgressToNextPhase(this.getAsBooleanNullSafe(JSONMapping.PROGRESS_TO_NEXT_PHASE));
        this.setFluidItemQuery(this.getAsStringNullSafe(JSONMapping.FLUID_ITEM_QUERY));
        this.setExecutionResult(this.getAsStringNullSafe(JSONMapping.EXECUTION_RESULT));
        this.setFluidItems(this.extractObjects(JSONMapping.FLUID_ITEMS, FluidItem::new));
        this.setExecuteUsers(this.extractObjects(JSONMapping.EXECUTE_USERS, User::new));
        this.setMailMessagesToSend(this.extractObjects(JSONMapping.MAIL_MESSAGES_TO_SEND, MailMessage::new));
    }

    /**
     * Conversion to {@code JsonObject} from Java Object.
     *
     * @return {@code JsonObject} representation of {@code FlowItemExecuteResult}
     * @see ABaseFluidGSONObject#toJsonObject()
     */
    @Override
    @XmlTransient
    @JsonIgnore
    public JsonObject toJsonObject() {
        JsonObject returnVal = super.toJsonObject();
        
        this.setAsObj(JSONMapping.FLUID_ITEM, returnVal, this::getFluidItem);
        this.setAsObj(JSONMapping.LOGGED_IN_USER, returnVal, this::getLoggedInUser);
        this.setAsObj(JSONMapping.VIEW, returnVal, this::getView);
        this.setAsObj(JSONMapping.FLOW_STEP_RULE, returnVal, this::getFlowStepRule);
        this.setAsProperty(JSONMapping.ASSIGNMENT_RULE_VALUE, returnVal, this.getAssignmentRuleValue());
        this.setAsProperty(JSONMapping.STATEMENT_RESULT_AS_STRING, returnVal, this.getStatementResultAsString());
        this.setAsProperty(JSONMapping.EXECUTE_PER_FLUID_ITEM_QUERY, returnVal, this.getExecutePerFluidItemQuery());
        this.setAsProperty(JSONMapping.FLUID_ITEM_QUERY, returnVal, this.getFluidItemQuery());
        this.setAsProperty(JSONMapping.EXECUTION_RESULT, returnVal, this.getExecutionResult());
        this.setAsProperty(JSONMapping.PROGRESS_TO_NEXT_PHASE, returnVal, this.getProgressToNextPhase());
        this.setAsObjArray(JSONMapping.FLUID_ITEMS, returnVal, this::getFluidItems);
        this.setAsObjArray(JSONMapping.EXECUTE_USERS, returnVal, this::getExecuteUsers);
        this.setAsObjArray(JSONMapping.MAIL_MESSAGES_TO_SEND, returnVal, this::getMailMessagesToSend);
        
        return returnVal;
    }
}