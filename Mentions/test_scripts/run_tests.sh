#!/bin/bash
#
# Run Gradle command to run tests.
#

printf "Running tests\n"
$GRADLE_HOME/bin/gradle --continue coverage
