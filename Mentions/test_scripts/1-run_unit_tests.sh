#!/bin/bash
#
# Run Gradle command to run unit tests
#

echo "Running Unit Tests..."

$GRADLE_HOME/gradle clean test --stacktrace
