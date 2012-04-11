@echo off
rem    
rem    MODIFY THIS FILE TO INCLUDE A VALID MAIL SERVER ADDRESS AND VALID EMAIL ACCOUNT
rem    THEN RENAME THE FILE AND SAVE IT IN THE SAME FILE FOLDER AS THE ORIGINAL FILE.
rem    INSTALLING NEW VERSIONS OF SiteView WILL OVERWRITE THIS FILE NAME
rem
rem  mailtest.bat -- test script to send an email message
rem
@echo on

setlocal
set scriptdir=%1

..\java\bin\java -cp . COM.dragonflow.Utils.SMTP test mailserver.yourdomain.com emailtestacount@yourdomain.com

endlocal
