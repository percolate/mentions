#!/bin/bash
#
# Upload Jacoco report to Codecov
#

./test_scripts/codecov -t $CODECOV_TOKEN -f $CIRCLE_ARTIFACTS/coverageReport/coverageReport.xml