#!/bin/bash
#
# Runs all the test scripts in order. It halts execution
# when a scripts fails, i.e. returns exit code 1. On success (exit code 0),
# the next script is executed.
#

function check_last_command_for_errors {
  if [ $? -ne 0 ]; then
    echo -e "\n$1\n"
    exit 1
  fi
}

./test_scripts/1-run_unit_tests.sh
check_last_command_for_errors "Unit tests failed."

./test_scripts/2-generate_test_coverage.sh
check_last_command_for_errors "Failed to generate Jacoco report."