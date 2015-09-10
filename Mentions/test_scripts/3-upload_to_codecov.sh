#!/bin/bash
#
# Upload Jacoco report to Codecov
#

FILE="../mentions/build/reports/jacoco/coverageReport/coverageReport.xml"

if [ -f $FILE ];
then
    ./codecov -t $CODECOV_TOKEN -f FILE
else 
    echo "Coverage report was not generated. Fail to upload to Codecov."
fi
