@echo off

REM **********************************************************************************************************************************************
REM * 				Business Availability Center for Siebel										 *
REM * 				      Mercury AM - Copyright 2004										 *
REM * 															      		      	 *
REM * This batch file copies a remote file to another folder (local or remote), which is a sibling of the working directory. That is,		 *
REM * if you call "copy_remote_file.bat a.txt my_folder", a.txt would be copied to my_folder folder which is located one level above the 	 *
REM * current directory.															 *
REM * It is used, for instance, by the Siebel Database Breakdown feature in order to copy a Siebel log file from the Application Server		 *
REM * to SiteScope's local folder or to Topaz's Graph Server local folder.									 *
REM * In order to be invoked by a SiteScope script monitor, this file should reside under the 'templates.applications\scripts.siebel' directory  *
REM * of SiteScope.		      	 													 * 
REM * 															      		      	 *
REM * Usage example: 															      	 *
REM * C:\SiteScope\templates.applications\scripts.siebel\copy_remote_file.bat \\sblapp1\log\SCCObjMgr_enu_93480.log templates.eventlog		 *
REM * 															      		      	 *
REM **********************************************************************************************************************************************

REM@echo on

REM *** Step 1: Prepare parameters of source & destination file ***

setlocal
set src_path=%1
set dest_path=%2

REM *** Step 2: Copy the source file to the new target folder ***

copy %src_path% ..\%dest_path%
