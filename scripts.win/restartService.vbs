'********************************************************************
'*
'* File:           restartService.vbs
'* Created:        Jan 2001
'* Version:        1.0
'*
'*  Main Function:  Restarts a service.
'*
'*  Arguments     <server> <service>
'*  
'*  Example:      To restart IIS on a server named "Server1" 
'*                cscript restartService My_Server IIS  
'*
'*
'*
'*  Warning:  This script make use of WMI api if you want to run
'*            script on an NT 4 machine you need to install WMI Core 
'*            Software Installation for NT 4.  
'*            See http://msdn.microsoft.com/downloads/sdks/wmi/download.asp
'********************************************************************
set args = Wscript.arguments

 ON ERROR RESUME NEXT
 
 dim strSever,strService,i
 
 'Change these two lines for the desierd server and sevice
 'Note: When calling this scritps from a SiteView Script Alert arg 7 and arg 8 are the first two argements pass to 
 '      the script from the Parameters filed on the "Define Script Alert" window.
 '      Arg 7=Service
 '      Arg 8=Sever
 strService = args(7)
 strServer = args(8)
  
  
  Set ServiceSet = GetObject("winmgmts:{impersonationLevel=impersonate}//" & strServer).ExecQuery("select * from Win32_Service where Description='" & strService &"'") 
  
  'Note do not enable the WScript.Echo staments when using this scipt with SiteView.
  'If you do the script will display a dialog window that SiteView can not respond to and
  'will cuase the script to hang.
  
  i=0
  for each Service in ServiceSet
  	i = i + 1
  	        RetVal = Service.StopService()
  	if RetVal = 0 then 
  		'WScript.Echo strService & " Service stopped" 
  	elseif RetVal = 5 then 
  		'WScript.Echo strService & " Service already stopped" 
  	end if
  	         RetVal = Service.StartService()
  	         'WScript.Echo strService & " Service started" 
  next
  
  if i = 0 then
    'WScript.Echo strService & " Service not found" 
  end if