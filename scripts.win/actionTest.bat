@echo off
rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  actionTest.bat -- example SiteView recovery script
rem
rem The parameters passed to the script by SiteView are:
rem
rem   1 the pathname of the scripts directory
rem   2 the name of the monitor that caused the alert
rem   3 the current status of the monitor
rem   4 the pathname to the alert message file
rem   5 the id of the monitor
rem   6 the group for the monitor
rem   7 the first additional parameter specified on the alert form
rem   ... any additional parameters specified on the alert form
rem
rem  to use this script:
rem     create a "Run Script actionTest.bat" alert
@echo on

setlocal
set scriptdir=%1
echo ##### actionTest.bat called ########## >> %scriptdir%\..\logs\actionTestOutput.txt
echo ##### actionTest.bat called ########## 2>> %scriptdir%\..\logs\actionTestError.txt

echo scriptPath: %1    >> %scriptdir%\..\logs\actionTestOutput.txt
echo monitorName: %2   >> %scriptdir%\..\logs\actionTestOutput.txt
echo monitorStatus: %3 >> %scriptdir%\..\logs\actionTestOutput.txt
echo messagePath: %4   >> %scriptdir%\..\logs\actionTestOutput.txt
echo monitorID: %5     >> %scriptdir%\..\logs\actionTestOutput.txt
echo monitorGroup: %6  >> %scriptdir%\..\logs\actionTestOutput.txt
echo param1: %7  >> %scriptdir%\..\logs\actionTestOutput.txt
echo param2: %8  >> %scriptdir%\..\logs\actionTestOutput.txt
echo param3: %9  >> %scriptdir%\..\logs\actionTestOutput.txt

rem NOTE: redirecting output for external commands only works if run by cmd
rem 
cmd /c "date /t >> %scriptdir%\..\logs\actionTestOutput.txt 2>> %scriptdir%\..\logs\actionTestError.txt"
cmd /c "time /t >> %scriptdir%\..\logs\actionTestOutput.txt 2>> %scriptdir%\..\logs\actionTestError.txt"

endlocal
