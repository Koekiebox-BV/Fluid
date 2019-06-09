#!/usr/bin/env bash
mvn clean && mvn clean install -U

echo '[INFO] Updating jar in FlowJob'
rm -rf /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*.jar
cp fluid-api/target/fluid-api-*.jar /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm
cp fluid-ws-java-client/target/fluid-ws-java-client-*.jar /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm

rm -rf /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*-sources.jar
rm -rf /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*-javadoc.jar

echo '[INFO] Updating WildFly-17'
cp -f /Users/jasonbruwer/Workspace/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*.jar /Users/jasonbruwer/Applications/wildfly-17.0.0.Alpha1/modules/com/fluidbpm/main/

echo '[INFO] *** *** *** ***'
echo '[INFO] * DONE. Date: '$(date)
echo '[INFO] *** *** *** ***'
