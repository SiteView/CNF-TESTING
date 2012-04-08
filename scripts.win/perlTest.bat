@echo off
rem  CAUTION:  
rem    DO NOT MODIFY THIS FILE.
rem    INSTALLING NEW VERSIONS WILL OVERWRITE THIS FILE
rem
rem  PERLTEST.BAT -- example alert script that calls perl
rem
rem  to use this script, create a "Run Script perlTest.bat" alert
rem
rem  assumes: nt perl is installed on your system
rem
@echo on

perl %1/perlTest.pl %1 %2 %3

