rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  RESTARTIIS.BAT -- script to restart the IIS web server service

net stop "World Wide Web Publishing Service"
net start "World Wide Web Publishing Service"