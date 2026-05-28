/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2024] Koekiebox B.V.
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

package com.fluidbpm.ws.client.migrator;

import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.flow.Flow;
import com.fluidbpm.program.api.vo.flow.FlowStep;
import com.fluidbpm.program.api.vo.flow.FlowStepRule;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewGroup;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewGroupListing;
import com.fluidbpm.program.api.vo.webkit.viewgroup.WebKitViewSub;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Migration class for workflow related migrations.
 *
 * @see Flow
 * @see com.fluidbpm.program.api.vo.flow.FlowStep
 * @see com.fluidbpm.program.api.vo.flow.FlowStepRule
 */
public class MigratorFlow {
    public enum StepType {
        Introduction, Exit, Assignment, MailCapture, SendMail, JavaProgram, ItemClone
    }

    @Builder
    @Data
    public static final class MigrateOptFlow {
        private String flowName;
        private String flowDescription;
        private MigrateOptFlowStep[] flowSteps;

        /**
         * @return {code true} if steps present, otherwise {@code false}.
         */
        public boolean hasSteps() {
            return this.flowSteps != null && this.flowSteps.length > 0;
        }
    }

    @Builder
    @Data
    public static final class MigrateWebKitViewGroup {
        private WebKitViewGroup[] groups;
        private boolean allowWebKitUpdate;

        /**
         * @return {code true} if groups present, otherwise {@code false}.
         */
        public boolean hasGroups() {
            return this.groups != null && this.groups.length > 0;
        }

        /**
         * Converts the current object into a {@code WebKitViewGroupListing} containing all groups present in this instance.
         * If the {@code groups} attribute is {@code null}, an empty {@code WebKitViewGroupListing} is returned.
         *
         * @return A {@code WebKitViewGroupListing} instance populated with the groups from this object,
         * or an empty instance if no groups are present.
         */
        public List<WebKitViewGroup> toListing() {
            List<WebKitViewGroup> returnVal = new ArrayList<>();
            if (this.groups != null) {
                returnVal.addAll(Arrays.asList(this.groups));
            }
            return returnVal;
        }
    }

    @Builder
    @Data
    public static final class MigrateOptFlowStep {
        private String stepName;
        private String stepDescription;
        private StepType stepType;

        private String[] flowRulesEntry;
        private String[] flowRulesView;
        private String[] flowRulesExit;

        private FlowStep.StepProperty[] properties;
    }

    @Builder
    public static final class MigrateOptRemoveFlow {
        private Long flowId;
        private String flowName;
    }

    /**
     * Migrates a flow by creating or updating the specified flow, its steps,
     * and associated rules. The method ensures that all components of the flow
     * (steps and rules) are properly set up based on the provided migration
     * options.
     *
     * @param fc the FlowClient used to manage flows, including creating and
     *           fetching flow definitions
     * @param fsc the FlowStepClient used to create and retrieve individual
     *            steps within a flow
     * @param fsrc the FlowStepRuleClient used to manage rules associated
     *             with specific flow steps
     * @param opts the migration options containing details about the flow
     *             and its steps, including their configurations and rules
     */
    public static void migrateFlow(
            FlowClient fc,
            FlowStepClient fsc,
            FlowStepRuleClient fsrc,
            MigrateOptFlow opts
    ) {
        Flow flow;
        try {
            flow = fc.createFlow(new Flow(opts.flowName, opts.flowDescription));
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
            flow = fc.getFlowByName(opts.flowName);
        }

        if (!opts.hasSteps()) return;

        for (MigrateOptFlowStep stepToMigrate : opts.flowSteps) {
            // Step:
            FlowStep step;
            try {
                FlowStep toCreate = new FlowStep(stepToMigrate.stepName, stepToMigrate.stepDescription);
                toCreate.setFlow(flow);
                if (stepToMigrate.stepType != null) {
                    toCreate.setFlowStepType(stepToMigrate.stepType.name());
                }
                if (stepToMigrate.properties != null) {
                    toCreate.setStepProperties(Stream.of(stepToMigrate.properties).collect(Collectors.toList()));
                }
                step = fsc.createFlowStep(toCreate);
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
                step = fsc.getFlowStepByStep(new FlowStep(stepToMigrate.stepName, flow));
            }
            step.setFlow(flow);

            // Rules:
            StepType type = StepType.valueOf(step.getFlowStepType());
            switch (type) {
                case Introduction:
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                case Assignment:
                    mergeEntryRules(fsrc, step, stepToMigrate.flowRulesEntry);
                    mergeViewRules(fsrc, step, stepToMigrate.flowRulesView);
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                case Exit:
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                case JavaProgram:
                    mergeEntryRules(fsrc, step, stepToMigrate.flowRulesEntry);
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                case SendMail:
                    mergeEntryRules(fsrc, step, stepToMigrate.flowRulesEntry);
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                case ItemClone:
                    mergeExitRules(fsrc, step, stepToMigrate.flowRulesExit);
                break;
                default: throw new FluidClientException(
                        String.format("Migration-Flow: Step type '%s' is not supported.", type),
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
                );
            }
        }
    }

    /**
     * Migrates WebKit view groups by creating or updating the specified view groups
     * based on the provided options. This method ensures that any new view groups
     * are inserted, while existing ones are identified for potential updates
     * or merging.
     *
     * @param fc the FlowClient used to manage WebKit view groups, providing methods
     *           to fetch and update view group listings.
     * @param opts the migration options containing details about the WebKit view
     *             groups to be processed, including their configurations and new
     *             desired state.
     */
    public static void migrateWebKitViewGroups(FlowClient fc, MigrateWebKitViewGroup opts) {
        List<WebKitViewGroup> existing = fc.getViewGroupsWebKit();
        List<WebKitViewGroup> provided = opts.toListing();
        List<WebKitViewGroup> toUpsert = new ArrayList<>();

        provided.forEach(itm -> {
            if (UtilGlobal.isBlank(itm.getJobViewGroupName())) return;

            WebKitViewGroup exists = existing.stream()
                    .filter(itmExists -> itm.getJobViewGroupName().equalsIgnoreCase(
                            itmExists.getJobViewGroupName()
                    ))
                    .findFirst()
                    .orElse(null);
            if (exists == null) {
                 toUpsert.add(itm);
            } else {
                List<WebKitViewSub> existingSubs = exists.getWebKitViewSubs();
                if (existingSubs == null || existingSubs.isEmpty()) {
                    //no subs yet, use the provided
                    toUpsert.add(itm);
                    return;
                }

                // Existing View Group, but updates not allowed.
                if (!opts.allowWebKitUpdate) return;

                //Merge:
                JsonObject existingJsonObj = exists.toJsonObject();
                JsonObject newJsonObj = itm.toJsonObject();

                // Copy all the new fields:
                UtilGlobal.copyJSONFullMerge(newJsonObj, existingJsonObj);

                toUpsert.add(new WebKitViewGroup(existingJsonObj));
            }
        });
        fc.upsertViewGroupsWebKit(new WebKitViewGroupListing(toUpsert));
    }


    private static void mergeExitRules(
            FlowStepRuleClient fsrc,
            FlowStep step,
            String ... toCreate
    ) {
        mergeRules(
                RuleType.Exit,
                fsrc,
                step,
                fsrc::deleteFlowStepExitRule,
                fsrc::updateFlowStepExitRule,
                fsrc::createFlowStepExitRule,
                toCreate
        );
    }

    private static void mergeEntryRules(
            FlowStepRuleClient fsrc,
            FlowStep step,
            String ... toCreate
    ) {
        mergeRules(
                RuleType.Entry,
                fsrc,
                step,
                fsrc::deleteFlowStepEntryRule,
                fsrc::updateFlowStepEntryRule,
                fsrc::createFlowStepEntryRule,
                toCreate
        );
    }

    private static void mergeViewRules(
            FlowStepRuleClient fsrc,
            FlowStep step,
            String ... toCreate
    ) {
        mergeRules(
                RuleType.View,
                fsrc,
                step,
                fsrc::deleteFlowStepViewRule,
                fsrc::updateFlowStepViewRule,
                fsrc::createFlowStepViewRule,
                toCreate
        );
    }

    public enum RuleType {
        Entry, Exit, View
    }

    /**
     * Merges given rules into the existing rules for a specific flow step
     * based on the provided rule type. This includes creating new rules,
     * updating existing rules, and deleting rules that are no longer needed.
     *
     * @param ruleType the type of rules to manage (Entry, Exit, or View).
     * @param fsrc the FlowStepRuleClient to be used for fetching and updating rules.
     * @param step the flow step for which the rules are being managed.
     * @param actionDelete a consumer to handle deletion of unnecessary rules.
     * @param actionUpdate a consumer to handle updates to existing rules.
     * @param actionCreate a consumer to handle creation of new rules.
     * @param toCreate a variable-length array of rule definitions to be compared
     *                 with the existing rules and applied to the specified step.
     */
    private static void mergeRules(
            RuleType ruleType,
            FlowStepRuleClient fsrc,
            FlowStep step,
            Consumer<? super FlowStepRule> actionDelete,
            Consumer<? super FlowStepRule> actionUpdate,
            Consumer<? super FlowStepRule> actionCreate,
            String ... toCreate
    ) {
        if (toCreate == null || toCreate.length == 0) return;
        List<FlowStepRule> existing;
        try {
            switch (ruleType) {
                case Entry:
                    existing = fsrc.getEntryRulesByStep(step);
                break;
                case Exit:
                    existing = fsrc.getExitRulesByStep(step);
                break;
                case View:
                    existing = fsrc.getViewRulesByStep(step);
                    if (existing.get(0).getOrder() == 0) existing.remove(0);
                break;
                default:
                    throw new FluidClientException(
                            String.format("Rule Type '%s' is not supported.", ruleType),
                            FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR
                    );
            }
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            existing = new ArrayList<>();
        }

        List<FlowStepRule>
                rulesDelete = new ArrayList<>(),
                rulesCreate = new ArrayList<>(),
                rulesUpdate = new ArrayList<>();
        for (int ruleNr = 1; ruleNr <= toCreate.length; ruleNr++) {
            String ruleToVerify = toCreate[ruleNr - 1];
            if (UtilGlobal.isBlank(ruleToVerify)) continue;
            
            if (existing.size() < ruleNr) {
                rulesCreate.add(new FlowStepRule(
                        step.getFlow(),
                        step,
                        ruleToVerify,
                        ruleNr
                ));
            } else {
                FlowStepRule existingToVerify = existing.get(ruleNr-1);
                String existingRawRule = existingToVerify.getRule();
                if (!existingRawRule.trim().equalsIgnoreCase(ruleToVerify.trim())) {
                    existingToVerify.setRule(ruleToVerify);
                    existingToVerify.setFlowStep(step);
                    existingToVerify.setFlow(step.getFlow());
                    rulesUpdate.add(existingToVerify);
                }
            }
        }

        // Extras to remove:
        if (existing.size() > toCreate.length) {
            for (int iRem = toCreate.length; iRem < existing.size(); iRem++) {
                rulesDelete.add(existing.get(iRem));
            }
        }

        rulesDelete.forEach(actionDelete);
        rulesUpdate.forEach(actionUpdate);
        rulesCreate.forEach(actionCreate);
    }

    /**
     * Remove a workflow.
     *
     * @param fc {@code FlowClient}
     * @param opts {@code MigrateOptRemoveForm}
     */
    public static void removeFlow(FlowClient fc, MigratorFlow.MigrateOptRemoveFlow opts) {
        Flow toDelete = new Flow(opts.flowId, opts.flowName);
        fc.forceDeleteFlow(toDelete);
    }
}
