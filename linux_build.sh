#!/usr/bin/env bash
mvn clean && mvn clean install -U -Dgpg.skip

#PROPERTIES
export WORKSPACE_HOME='/home/jbruwer/GoogleDrive/Workspace'
export WF_HOME='/home/jbruwer/Applications/wildfly-20.0.1.Final'

echo '[INFO] Updating jar in FlowJob'
rm -rf $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*.jar

echo '[INFO] Copying Fluid-API'
cp fluid-api/target/fluid-api-*.jar $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm
echo '[INFO] Copying Fluid-WS Client'
cp fluid-ws-java-client/target/fluid-ws-java-client-*.jar $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm

echo '[INFO] Cleanup'
rm -rf $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*-sources.jar
rm -rf $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*-javadoc.jar

echo '[INFO] Updating $WF_HOME'
echo $WF_HOME
cp -f $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*.jar $WF_HOME/modules/com/fluidbpm/main/

echo '[INFO] *** *** *** ***'
echo '[INFO] * DONE. Date: '$(date)
echo '[INFO] *** *** *** ***'
