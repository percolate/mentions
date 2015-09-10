#!/bin/bash
#
# Upload Jacoco report to Codecov
#

FILE="./mentions/build/reports/jacoco/coverageReport/coverageReport.xml"

if [ -a $FILE ];
then
    ./test_scripts/codecov -t $CODECOV_TOKEN -f FILE
else 
    exit 1 
fi
