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
REM *   %4 - Start time
REM *   %5 - End time
REM *   Iterate five params:
REM *   %6-<web server> %7-<web csv file> %8-<app server> %9-<app server log dir> %10-<pid> .....
REM *
REM * Usage example:
REM *   C:\SiteScope\templates.applications\scripts.siebel\collect_sarm_web_files.bat \templates.eventlog
REM *       D:\sarm_analyzer sadmin "2004-07-25 10:30:00" "2004-07-25 11:30:00"
REM *       sblgw S01_P2432_N0007.sarm sblapp1 \\sblapp1\SiebSrvr_logs 3546
REM *
REM * NOTE: this batch files supports only Siebel 7.7
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
REM keep the Start Date
    set start_time=%4
REM keep the End date
    set end_time=%5
REM shift 5 params.
    shift
    shift
    shift
    shift
    shift

    set count = 1

REM loop all parameters on command line using shift, to go over all web servers.
	:LOOP
	if   "%1"==""  goto  DONE


	REM iterate all files in the app server,  recursivley call to this batch file with "srm_rename".
	REM pass as params: web server, SARM binary web file, application server, pid, SARM binary application server's file.
	for  %%f  in (%4\*P*%5*_N*.SARM) do call %batch_name% srm_rename %1 %2 %3 %5 %%f


	REM Skip to next web server. Shift 5 params.
	shift
	shift
	shift
	shift
	shift
	goto LOOP

	REM the recursive call.
	:srm_rename
    echo sarm_rename
	REM increase the counter
    set /a count=%count+1

    REM create a temp directory for the web server.
    mkdir %ss_temp_dir%\%2_%4\temp
    REM copy the sarm file from the application server to the temp dir.
    copy %6 %ss_temp_dir%\%2_%4\temp
    REM copy the web SARM file to the temp dir.
    copy %ss_temp_dir%\%2\%3 %ss_temp_dir%\%2_%4\temp

    REM run the sarm analyzer
    %srm_analyzer_path%\sarmanalyzer.exe -o %ss_temp_dir%\%2_%4\%user%_P%5_%count%.xml -u %user% -i %ss_temp_dir%\%2_%4\temp

    REM clear the temp dir so we could use it for the next web server
    rmdir %ss_temp_dir%\%2_%4\temp /s /q

	:done

