REM *****************************************************************************************************
REM * 				Business Availability Center for Siebel
REM * 				      Mercury AM - Copyright 2004
REM *
REM * This batch file copies the SARM binary files from the given Web Server directory,
REM * and run the sarmanalyzer in order to generate the csv files.
REM * In order to be invoked by a SiteScope script monitor, this file should reside under the
REM * 'templates.applications\scripts.siebel' directory of SiteScope.
REM * Usage:
REM * %1 - sitescope temp dir
REM * %2 - Siebel version, possible values: Siebel753 Siebel77
REM * %3 - SARM analyzer path
REM * %4..... - web server + web server log dir
REM *
REM * Usage example:
REM *   C:\SiteScope\templates.applications\scripts.siebel\collect_sarm_web_files.bat \templates.eventlog
REM *       Siebel753 D:\sarm_analyzer sblgw \\sblgw\SWE_logs
REM *
REM ******************************************************************************************************

REM echo ON
REM Set paramters:
REM keep the sitescope temp dir, to which the files should be copied.
    set ss_temp_dir=..\%1
REM keep the siebel version
    set sbl_version=%2
REM keep the sarm analyzer path
    set srm_analyzer_path=%3
REM shift 3 params.
    shift
    shift
    shift

REM loop all parameters on command line using shift, to go over all web servers.
	:LOOP
	if   "%1"==""  goto  DONE

	REM create a temp directory for the web server.
	mkdir  %ss_temp_dir%\%1
	REM copy all SARM file from the web server to the temp dir.
	copy %2\*.SARM %ss_temp_dir%\%1

	if "%sbl_version%"=="Siebel753" goto SBL_753
	if "%sbl_version%"=="Siebel77" goto SBL_77

	:SBL_753
	REM run sarm analyzer on all files in the folder to generarte the csv file.
	for  %%f  in  (%ss_temp_dir%\%1\*.SARM)   do  %srm_analyzer_path%\sarmanalyzer.exe -csv -prop %%f > %%f.csv
    goto end
    :SBL_77
    REM run sarm analyzer on all files in the folder to generarte the csv file.
	for  %%f  in  (%ss_temp_dir%\%1\*.SARM)   do  %srm_analyzer_path%\sarmanalyzer.exe -o %%f.csv -b -d csv -f %%f

    :end
	REM skip to next web server.
	shift
	shift
	goto loop
	:done
