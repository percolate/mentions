#!/bin/bash
#
# Run Gradle command to generate Jacoco report
#

./gradlew mentions:clean mentions:coverageReport

mv -p $CIRCLE_ARTIFACTS/coverage_report/ ./mentions/build/reports/jacoco/coverageReport/coverageReport.xml

