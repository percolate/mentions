#!/bin/bash
#
# Upload Jacoco report to Codecov
#

FILE="../mentions/build/reports/jacoco/coverageReport/coverageReport.xml"

if [ -f $FILE ];
then
    ./codecov -t $CODECOV_TOKEN -f FILE
else 
    return 0;
fi
