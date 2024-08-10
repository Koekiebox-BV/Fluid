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
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.flow.FlowClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepClient;
import com.fluidbpm.ws.client.v1.flow.FlowStepRuleClient;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
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
     * Migrate a flow.
     *
     * @param fc {@code FlowClient}
     * @param opts {@code OptFlowMigrate}
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
            }
        }
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
                            String.format("Rule Type '%s' is not supported", ruleType),
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
    public static void removeFlow(
            FlowClient fc, MigratorFlow.MigrateOptRemoveFlow opts
    ) {
        Flow toDelete = new Flow(opts.flowId, opts.flowName);
        fc.forceDeleteFlow(toDelete);
    }
}
