package com.fluidbpm.ws.client.v1.contract;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

/**
 * Enabled only by client-coverage. Flush before the test JVM exits: relying on
 * the agent's shutdown hook produced empty execution files in the local JDK 11 run.
 * Reflection keeps the normal test build independent of JaCoCo runtime classes.
 */
public final class ClientCoverageListener extends RunListener {
    @Override public void testRunFinished(Result result) throws Exception {
        Object agent = Class.forName("org.jacoco.agent.rt.RT").getMethod("getAgent").invoke(null);
        Class.forName("org.jacoco.agent.rt.IAgent").getMethod("dump", boolean.class).invoke(agent, false);
    }
}
