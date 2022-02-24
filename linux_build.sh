#!/usr/bin/env bash
mvn clean && mvn clean install -U -Dgpg.skip

#PROPERTIES
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home

export WORKSPACE_HOME='/Users/jasonbruwer/GDrive/Workspace'
export WF_HOME='/Users/jasonbruwer/Applications/wildfly-20.0.1.Final'
export WL_HOME='/home/jbruwer/Oracle/Middleware/Oracle_Home'

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

echo '[INFO] Updating $WL_HOME'
echo $WL_HOME
cp -f $WORKSPACE_HOME/FlowJob/flow-job-setup/docker/koekiebox/fluid_base/external_lib/com/fluidbpm/fluid-*.jar $WL_HOME/user_projects/domains/fluid/lib/

echo '[INFO] *** *** *** ***'
echo '[INFO] * DONE. Date: '$(date)
echo '[INFO] *** *** *** ***'
