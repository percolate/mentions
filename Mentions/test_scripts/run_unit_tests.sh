#!/bin/bash
#
# Run Gradle command to run unit tests
#

echo "Running Unit Tests..."

if [[ "$CIRCLE_BRANCH" = "master" ]]; then
  echo "Skipping tests on prod builds (on master branch)"
else
  ./gradlew test --stacktrace
fi
