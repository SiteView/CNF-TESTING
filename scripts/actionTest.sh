#!/bin/sh
#
#  CAUTION:  
#    DO NOT MODIFY THIS FILE.
#    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
#
# actionTest.sh -- example SiteView recovery script
#
# The parameters passed to the script by SiteView are:
#
#   1 the pathname of the scripts directory
#   2 the name of the monitor that caused the alert
#   3 the current status of the monitor
#   4 the pathname to the alert message file
#   5 the id of the monitor
#   6 the group for the monitor
#   7 the first additional parameter specified on the alert form
#   ... any additional parameters specified on the alert form
#
# to use this script:
#    create a "Run Script actionTest.sh" alert

echo ##### actionTest.sh called ########## >> $1/../logs/actionTestOutput.txt

echo scriptPath: $1    >> $1/../logs/actionTestOutput.txt
echo monitorName: $2   >> $1/../logs/actionTestOutput.txt
echo monitorStatus: $3 >> $1/../logs/actionTestOutput.txt
echo messagePath: $4   >> $1/../logs/actionTestOutput.txt
echo monitorID: $5     >> $1/../logs/actionTestOutput.txt
echo monitorGroup: $6  >> $1/../logs/actionTestOutput.txt
echo param1: $7        >> $1/../logs/actionTestOutput.txt
echo param2: $8        >> $1/../logs/actionTestOutput.txt
echo param3: $9        >> $1/../logs/actionTestOutput.txt

date >> $1/../logs/actionTestOutput.txt

