REM *****************************************************************************************************
REM * 				Business Availability Center for Siebel
REM * 				      Mercury AM - Copyright 2004
REM *
REM * This batch file copies the SARM binary files from the given Application Server directory,
REM * than copies the Web Server binary files, that were already copied to a temp dir under sitescope
REM * and run the sarmanalyzer in order to generate the User Session Trace xml files.
REM * In order to be invoked by a SiteScope script monitor, this file should reside under the
REM * 'templates.applications\scripts.siebel' directory of SiteScope.
REM *
REM * Usage:
REM *   %1 - sitescope temp dir
REM *   %2 - SARM analyzer path
REM *   %3 - User name
REM *   Iterate five params:
REM *   %4-<web server> %5-<web sarm file> %6-<app server> %7-<app server log dir> %8-<pid> .....
REM *
REM * Usage example:
REM *   C:\SiteScope\templates.applications\scripts.siebel\collect_sarm_web_files.bat \templates.eventlog
REM *       D:\sarm_analyzer sadmin sblgw S01_P2432_N0007.sarm sblapp1 \\sblapp1\SiebSrvr_logs 3546
REM *
REM * NOTE: this batch files supports only Siebel 7.5.3
REM *
REM ******************************************************************************************************



REM if first paramater is srm_rename go to srm_rename, run the sarm ananlyzer and rename the output xml. (This is the recursive call)
if "%1"=="srm_rename" goto srm_rename

REM Set paramters:
REM the batch file name
    set batch_name=%0
REM keep the sitescope temp dir, to which the files should be copied.
    set ss_temp_dir=..\%1
REM keep the sarm analyzer path
    set srm_analyzer_path=%2
REM keep the username
    set user=%3

REM shift 3 params.
    shift
    shift
    shift




REM loop all parameters on command line using shift, to go over all web servers.
	:LOOP
	if   "%1"==""  goto  DONE

	REM create a temp directory for the web server.
	mkdir %ss_temp_dir%\%1_%3\temp
	REM copy all SARM file with the given process id (pid) from the app server to the temp dir.
	copy %4\*P%5*.SARM %ss_temp_dir%\%1_%3\temp
	REM copy the web SARM file to the temp dir.
	copy %ss_temp_dir%\%1\%2 %ss_temp_dir%\%1_%3\temp

	REM run sarm analyzer on all app server sarm files in the folder to generarte the csv file.
	REM Recursivly call this batch with params srm_name <web server> <web csv file> <app server> <app server sarm file> <pid>
	cd  %ss_temp_dir%\%1_%3\temp

	for  %%f  in  (*P%5*.SARM)   do  call %batch_name% srm_rename %1 %2 %3 %%f %5

    cd ..\..\..\..\

	REM clear the temp dir so we could use it for the next web server
	REM cd %ss_temp_dir%\
	rmdir %ss_temp_dir%\%1_%3\temp /s /q

	REM Skip to next web server. Shift 5 params.
	shift
	shift
	shift
	shift
	shift
	goto LOOP

	:srm_rename
    set count =
	REM increase the counter
	set /a count=%count+1
	REM cd to the temp directory so that the <user>.xml will be generated to that directory.
	REM cd %ss_temp_dir%\%2_%4\temp\
	REM Run sarm analyzer on the file.

	%srm_analyzer_path%\sarmanalyzer.exe -w %3 -s %5 -u %user%
	REM rename <user>.xml to <user>_P<pid>_<counter>.xml
	copy %user%.xml  ..\%user%_P%6_%count%.xml

    return

	:done

