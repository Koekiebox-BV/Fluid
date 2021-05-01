![Fluid Logo](content/fluid_logo_new.png "Fluid") ![Koekiebox Logo](content/koekiebox.png "Koekiebox")

[![label](https://img.shields.io/badge/fluid--api-v1.12-orange.svg?style=plastic)]()
[![label](https://img.shields.io/badge/license-GPL--3-blue.svg?style=plastic)]()
[![label](https://img.shields.io/badge/javadoc-Fluid--API-green.svg?style=plastic)](https://github.com/Koekiebox-PTY-LTD/Fluid/releases/download/v1.12/fluid-api-1.12-javadoc.jar)
[![label](https://img.shields.io/badge/javadoc-Fluid--WS--Client-green.svg?style=plastic)](https://github.com/Koekiebox-PTY-LTD/Fluid/releases/download/v1.12/fluid-ws-java-client-1.12-javadoc.jar)
[![label](https://img.shields.io/badge/docker--store-Fluid--BPM-red.svg?style=plastic)](https://store.docker.com/images/fluid-bpm)

# *** Fluid ***  

On demand Business Process Management, Content Management System.

Components include;

## 1. Fluid Java API

*   The core `API`.

## 2. Fluid API Java Web-Service Client

*   The full Fluid API `Java Web-Service` for Flow, Form Definition, Field.
*   The full Fluid API `JUnit Web-Service` test suite.

## Source Type

*   Java `1.8`
*   Maven `3.3.9`
 
## Project Build

1. Clone the repo: `git clone git@github.com:Koekiebox-PTY-LTD/Fluid.git` 
2. Install [Maven](https://maven.apache.org/install.html).
3. Navigate to local project folder: `cd Fluid`
4. Run `mvn clean install`

## Test Results and Package Info

### Surefire Reports
* [Surefire Report for Fluid API](fluid-api/target/site/surefire-report.html) ran via command `mvn surefire-report:report`
* [Surefire Report for Fluid Java Web Service Client](fluid-ws-java-client/target/site/surefire-report.html) ran via command `mvn surefire-report:report`

### JavaDoc
* [Java Documentation for Fluid API](fluid-api/target/site/apidocs/index.html) ran via command `mvn javadoc:javadoc`
* [Java Documentation for Fluid Java Web Service Client](fluid-ws-java-client/target/site/apidocs/index.html) ran via command `mvn javadoc:javadoc`

### Release Info
* [Release Information for Fluid API](fluid-api/target/site/index.html)
* [Release Information for Fluid Java Web Service Client](fluid-ws-java-client/target/site/index.html)
