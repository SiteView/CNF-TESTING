'********************************************************************
'*
'* File:           diskSpace.vbs
'* Created:        Jan 2001
'* Version:        1.0
'*
'*  Main Function:  checks the Deskspace of a system.
'*
'*  Arguments     <server> <drive>
'*
'*  Warning:  This script make use of WMI api if you want to run
'*            script on an NT 4 machine you need to install WMI Core 
'*            Software Installation for NT 4.  
'*            See http://msdn.microsoft.com/downloads/sdks/wmi/download.asp
'*
'********************************************************************

set args = Wscript.arguments
dim strFreeSpace
strServer = args(0)
strDrive = args(1)

Set DiskSet = GetObject("winmgmts:{impersonationLevel=impersonate}//" & strServer).ExecQuery("select FreeSpace,Size,Name from Win32_LogicalDisk where Name='" & strDrive & ":'")
for each Disk in DiskSet
   
      strFreeSpace = Disk.FreeSpace/Disk.Size * 100
      WScript.Echo "Drive " & Disk.Name & " % free =" & strFreeSpace
   
Next

