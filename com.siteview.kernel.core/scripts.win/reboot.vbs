'********************************************************************
'*
'* File:           reboot.vbs
'* Created:        Jan 2001
'* Version:        1.0
'*
'*  Main Function:  Restarts a service.
'*
'*  Arguments     <server> 
'*
'*  Warning:  This script make use of WMI api if you want to run
'*            script on an NT 4 machine you need to install WMI Core 
'*            Software Installation for NT 4.  
'*            See http://msdn.microsoft.com/downloads/sdks/wmi/download.asp
'*
'********************************************************************

set args = Wscript.arguments
strServer = args.item(7)


Set OpSysSet = GetObject("winmgmts:{impersonationLevel=impersonate,(RemoteShutdown)}//" & strServer)
for each OpSys in OpSysSet.InstancesOf ("Win32_OperatingSystem")
   OpSys.Win32ShutDown(6)
Next

