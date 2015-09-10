#!/bin/bash
#
# Run Gradle command to run unit tests
#

echo "Running Unit Tests..."

./gradlew clean test --stacktrace
