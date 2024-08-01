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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    public static final class MigrateOptFlow {
        private String flowName;
        private String flowDescription;

        private MigrateOptFlowStep[] flowSteps;

        /**Set steps as parameter list.
         * @param flowSteps steps to set.
         */
        public void fieldsParam(MigrateOptFlowStep ... flowSteps) {
            this.flowSteps = flowSteps;
        }

        /**
         * @return {code true} if steps present, otherwise {@code false}.
         */
        public boolean hasSteps() {
            return this.flowSteps != null && this.flowSteps.length > 0;
        }
    }

    @Builder
    public static final class MigrateOptFlowStep {
        private String stepName;
        private String stepDescription;
        private StepType stepType;

        private String[] flowRulesEntry;
        private String[] flowRulesView;
        private String[] flowRulesExit;

        private FlowStep.StepProperty[] properties;

        /**Set steps as parameter list.
         * @param rules steps to set.
         */
        public void flowRulesEntryParam(String ... rules) {
            this.flowRulesEntry = rules;
        }

        /**Set steps as parameter list.
         * @param name to add.
         * @param value to add.
         */
        public MigrateOptFlowStep.MigrateOptFlowStepBuilder propertiesAdd(String name, String value) {
            if (this.properties == null) {
                this.properties = new FlowStep.StepProperty[1];
                this.properties[0] = new FlowStep.StepProperty(name, value);
                return builder();
            }

            FlowStep.StepProperty[] arrNew = new FlowStep.StepProperty[this.properties.length + 1];
            System.arraycopy(this.properties, 0, arrNew, 0, this.properties.length);
            arrNew[this.properties.length] = new FlowStep.StepProperty(name, value);
            this.properties = arrNew;
            return builder();
        }
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
                step = fsc.createFlowStep(toCreate);
            } catch (FluidClientException fce) {
                if (fce.getErrorCode() != FluidClientException.ErrorCode.DUPLICATE) throw fce;
                step = fsc.getFlowStepByStep(new FlowStep(stepToMigrate.stepName, flow));
            }

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
                fsrc,
                step,
                fsrc::deleteFlowStepViewRule,
                fsrc::updateFlowStepViewRule,
                fsrc::createFlowStepViewRule,
                toCreate
        );
    }

    private static void mergeRules(
            FlowStepRuleClient fsrc,
            FlowStep step,
            Consumer<? super FlowStepRule> actionDelete,
            Consumer<? super FlowStepRule> actionUpdate,
            Consumer<? super FlowStepRule> actionCreate,
            String ... toCreate
    ) {
        List<FlowStepRule> existing;
        try {
            existing = fsrc.getExitRulesByStep(step);
        } catch (FluidClientException fce) {
            if (fce.getErrorCode() != FluidClientException.ErrorCode.NO_RESULT) throw fce;
            existing = new ArrayList<>();
        }

        List<FlowStepRule>
                rulesDelete = new ArrayList<>(),
                rulesCreate = new ArrayList<>(),
                rulesUpdate = new ArrayList<>();
        for (int ruleNr = 1; ruleNr <= toCreate.length; ruleNr++) {
            String ruleToVerify = toCreate[ruleNr];
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
                    rulesUpdate.add(existingToVerify);
                }
            }
        }

        // Extras to remove:
        if (existing.size() > toCreate.length) {
            for (int iRem = toCreate.length;iRem < existing.size();iRem++) {
                rulesDelete.add(existing.get(iRem));
            }
        }

        rulesDelete.forEach(actionDelete);
        rulesUpdate.forEach(actionUpdate);
        rulesCreate.forEach(actionCreate);
    }
}
