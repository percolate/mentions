#!/bin/bash
#
# Run Gradle command to generate Jacoco report
#

$GRADLE_HOME/bin/gradle mentions:clean mentions:coverageReport

mkdir -p $CIRCLE_ARTIFACTS/coverageReport/
mv ./mentions/build/reports/jacoco/coverageReport/coverageReport.xml $_

