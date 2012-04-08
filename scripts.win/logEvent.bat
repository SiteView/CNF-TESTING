<HTML>
@echo off
rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  logEvent.bat -- write alert to NT Event Log
rem
rem   arguments:
rem     scriptPath -- pathname to script folder
rem     monitorName -- name of monitor that caused alert
rem     monitorName -- name of monitor that caused alert
rem     alertFile -- file containing alert description
rem
rem  to use this script:
rem     create a "Run Script logEvent.bat" alert
@echo on

%1\..\tools\sendmodem -f %4
</HTML>
