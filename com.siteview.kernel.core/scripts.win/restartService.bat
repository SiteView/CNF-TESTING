@echo off
rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  RESTARTSERVICE.BAT -- script to restart an NT service
rem
rem  to use this script:
rem    create a monitor whose title is the Service Name
rem    create a "Run Script RestartService.bat" alert for that monitor
@echo on

net stop %2
net start %2
