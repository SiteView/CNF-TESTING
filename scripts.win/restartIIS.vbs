'********************************************************************
'*
'* File:           restartIIS.vbs
'* Created:        Jan 2001
'* Version:        1.0
'*
'*  Main Function:  Restarts IIS service.
'*
'*  Warning:  This script make use of WMI api if you want to run
'*            script on an NT 4 machine you need to install WMI Core 
'*            Software Installation for NT 4.  
'*            See http://msdn.microsoft.com/downloads/sdks/wmi/download.asp                
'*
'********************************************************************


 ON ERROR RESUME NEXT
 
 dim strSever,strService,i
 
 
 strService = "World Wide Web Publishing Service" 'Change this line to service name of the the service that you want to reset
 strServer = "YourSever" ' Change this line server that you want preform the action on
  
  
  Set ServiceSet = GetObject("winmgmts:{impersonationLevel=impersonate}//" & strServer).ExecQuery("select * from Win32_Service where Description='" & strService &"'") 
  

  for each Service in ServiceSet
  	
  	        RetVal = Service.StopService()
  	        
  	        wscript.sleep (5000) '.5 sec delay is needed to for the service to stop
  	                             'before we try to restart it
  	
  	        RetVal = Service.StartService()
  	        
  next
  

  
   
   
 