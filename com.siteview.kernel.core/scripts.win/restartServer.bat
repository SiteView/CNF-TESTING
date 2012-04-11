@echo off
rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  restartServer.BAT -- script to shutdown a remote server
rem 
rem   assumes: shutdown.exe from NT resource kit is at c:\shutdown.exe
rem        
rem   (stores a log of all shutdowns in SiteView\scripts\shutdown.log)
rem
@echo on

echo #### Shutdown Action ##### >> %1\..\logs\shutdown.log
echo Monitor: %2 >> %1\..\logs\shutdown.log
echo Path: %1 >> %1\..\logs\shutdown.log
date /t >> %1\..\logs\shutdown.log
time /t >> %1\..\logs\shutdown.log

c:\shutdown.exe /L /R /Y >> %1\..\logs\shutdown.log
