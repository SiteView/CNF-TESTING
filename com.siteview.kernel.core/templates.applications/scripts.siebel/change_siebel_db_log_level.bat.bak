@echo off

REM *****************************************************************************************************************************************************************************
REM * 				Business Availability Center for Siebel										 				*
REM * 				      Mercury AM - Copyright 2004														*
REM * 															      		      	 				*
REM * This batch file increases or decreases Siebel event level for the specified Siebel Componenet.                     		      	 				*
REM * In order to be invoked by a SiteScope script monitor, this file should reside under the 'templates.applications\scripts.siebel' directory of SiteScope.			* 
REM * 															      		      	 				*
REM * Usage example: 															      	 				*
REM * C:\SiteScope\templates.applications\scripts.siebel\change_siebel_db_log_level.bat C:\sea752\tools\client\BIN sblgw siebel_ent sblapp1 sadmin sadmin enable sccobjmgr_enu 	*
REM * 															      		      	 				*
REM * Operation exit code is passed to SiteScope monitor. Possible exit codes:									 				*
REM * 	0  - Operation is successful. That is, if SrvrMgr was able to connect to at least 1 application server AND the requested component       				*
REM * 	     event log level was set successfully.												 				*
REM * 	10 - Operation failed. This might be due to wrong parameters:										 				*
REM * 		* Wrong connectivity parameters - srvrmgr_path / gw_srvr / en_srvr / app_srvr / username / password is invalid. Hence, no 	 				*
REM * 		  application server can be connected to.											 				*
REM * 		* Wrong component name - thus component event log level cannot be changed.							 				*
REM * 															      		      	 				*
REM * Note: We write to file only the output of the first operation since the 2 other operations would yield exactly the same error (if any).    				*
REM * 	    That is, in order to increase the performance of this batch file.									 				*				 
REM * 															      		      	 				*
REM *****************************************************************************************************************************************************************************

REM @echo on

REM *** Step 1: Prepare parameters for connection to Siebel Server Manager & execution commands ***

setlocal
set srvrmgr_path=%1
set gw_srvr=%2
set en_srvr=%3
set app_srvr=%4
set username=%5
set password=%6
set log_mode=%7
set comp_name=%8

REM *** Step 2: Delete the operation output file - in case it exists from previous executions ***

del /f change_siebel_db_log_level_output.txt	

REM *** Step 3: Prepare log level values ***

if %7 == disable goto disable

set ObjMgrsqllog_loglevel=4
set EventContext_loglevel=3
set ObjMgrSessionInfo_loglevel=3

goto connect

:disable

set ObjMgrsqllog_loglevel=1
set EventContext_loglevel=1
set ObjMgrSessionInfo_loglevel=1


:connect

REM *** Step 4: If app_srvr means 'all servers in the enterprise', do not pass srvrapp_srvr parameter ***

if %4 == all_app_servers goto connect_to_all_app_servers

REM *** Step 5: Connect to Siebel Server Manager (specific app_srvr) CLI tool and send single commands: ***

%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /s %app_srvr% /u %username% /p %password% /c "change evtloglvl ObjMgrsqllog=%ObjMgrsqllog_loglevel% for comp %comp_name%" /o change_siebel_db_log_level_output.txt
%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /s %app_srvr% /u %username% /p %password% /c "change evtloglvl EventContext=%EventContext_loglevel% for comp %comp_name%"
%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /s %app_srvr% /u %username% /p %password% /c "change evtloglvl ObjMgrSessionInfo=%ObjMgrSessionInfo_loglevel% for comp %comp_name%"

goto check_errors

:connect_to_all_app_servers

REM *** Step 6: Connect to Siebel Server Manager (all app_srvr) CLI tool and send single commands: ***

%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /u %username% /p %password% /c "change evtloglvl ObjMgrsqllog=%ObjMgrsqllog_loglevel% for comp %comp_name%" /o change_siebel_db_log_level_output.txt
%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /u %username% /p %password% /c "change evtloglvl EventContext=%EventContext_loglevel% for comp %comp_name%"
%srvrmgr_path%\srvrmgr.exe /g %gw_srvr% /e %en_srvr% /u %username% /p %password% /c "change evtloglvl ObjMgrSessionInfo=%ObjMgrSessionInfo_loglevel% for comp %comp_name%"

REM *** Step 7: Check error / success.

:check_errors
find /c /i "Connected to 0 server" change_siebel_db_log_level_output.txt
if not %errorlevel% == 1 goto failure

find /c /i "success" change_siebel_db_log_level_output.txt
if not %errorlevel% == 0 goto failure

REM *** Step 8: Return respected exit code.

:success
echo SUCCESS
exit 0
goto end

:failure
echo FAILURE
exit 10
goto end

:end
