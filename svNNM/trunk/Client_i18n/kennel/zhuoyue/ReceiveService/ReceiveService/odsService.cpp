//////////////////////////////////////////////////////////////////////////
//
// odsService.cpp
//
//		有关 NT 服务控制的通用函数实现
//
// by Wang Yong Gang, 1999-10-27
// 在 张军 1998/05/17 以及 VC Sample RpcSvc 基础上完成
//
//////////////////////////////////////////////////////////////////////////

#include "StdAfx.h"
#include "odsService.h"

//-----------------------------------------------------------------------
// odsServiceMessageBox				- 服务中弹出的对话框
//-----------------------------------------------------------------------
void odsServiceMessageBox( LPCTSTR lpszText )
{
	OSVERSIONINFO	osVersionInfo;
	UINT			uType;

	uType = MB_OK | MB_ICONSTOP;

	osVersionInfo.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
	if (!GetVersionEx(&osVersionInfo))	
		return;

	if (osVersionInfo.dwMajorVersion < 4)
		uType |= MB_SERVICE_NOTIFICATION_NT3X;
	else
		uType |= MB_SERVICE_NOTIFICATION;

	MessageBox( NULL, lpszText, _T("SERVICE"), uType );
}

//-----------------------------------------------------------------------
// odsReportStatusToSCMgr				- 将自己的状态通知服务控制器
//
// 参数：
//		hServiceStatus					- 服务状态句柄
//		dwCurrentState					- 要设置的状态
//		DWORD dwErrorCode				- 错误码，0 表示无错误
//		dwWaitHint						- 控制器等待间隔
//		pstError						- 接收错误信息指针（可以为 NULL ）
// 返回：
//		bSuccess						- 成功与否
//-----------------------------------------------------------------------
BOOL odsReportStatusToSCMgr(SERVICE_STATUS_HANDLE hServiceStatus,
							DWORD dwCurrentState,
							DWORD dwErrorCode,
							DWORD dwWaitHint,
							STODSERROR* pstError )
{
    static DWORD dwCheckPoint = 0;    
	SERVICE_STATUS	ssStatus;

	memset( &ssStatus, 0, sizeof(ssStatus) );
 
	// 
	// * 注意：如果下面的设置中没有 SERVICE_ACCEPT_SHUTDOWN，则服务在运行时将
	// * 不接收 NT 重新启动的控制消息，而此时将不做 Cleanup 的工作，与 ORACLE 
	// * 的连接没有释放。将造成 ORACLE 服务端的垃圾进程保留，重启动多次后，会
	// * 耗尽 ORACLE 的服务端进程，使 ORACLE 瘫痪。
	//
    if ( dwCurrentState == SERVICE_RUNNING )  
        ssStatus.dwControlsAccepted = SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN;
    else
        ssStatus.dwControlsAccepted = 0;

	ssStatus.dwServiceType = SERVICE_WIN32_OWN_PROCESS;
    ssStatus.dwCurrentState = dwCurrentState;

	if (dwErrorCode != 0)
		ssStatus.dwWin32ExitCode = ERROR_SERVICE_SPECIFIC_ERROR;
	else
		ssStatus.dwWin32ExitCode = NO_ERROR;
	ssStatus.dwServiceSpecificExitCode = dwErrorCode;
    ssStatus.dwWaitHint = dwWaitHint;

    if ( ( dwCurrentState == SERVICE_RUNNING ) ||
         ( dwCurrentState == SERVICE_STOPPED ) ||
		 ( dwCurrentState == SERVICE_PAUSED ) )
        dwCheckPoint = 0;
    else
        dwCheckPoint++;
	ssStatus.dwCheckPoint = dwCheckPoint;

    // Report the status of the service to the service control manager.
    //
    if (!SetServiceStatus( hServiceStatus, &ssStatus)) 
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_REPORTSTATUS );
        odsMakeError( pstError, ODS_ERR_REPORTSTATUS, NULL );
		return FALSE;
    }    
    else
		return TRUE;
}


//-----------------------------------------------------------------------
// odsInstallService				- 安装服务
// 
// 参数：
//		strComputerName				- 计算机名（NULL表示本地计算机）
//		strServiceName				- 服务名称
//		strServicePath				- 服务路径
//		strDisplayName				- 服务的显示名称
//		dwStartType					- 服务的启动方式
//									- SERVICE_AUTO_START 或 SERVICE_DEMAND_START
//		strDependencies				- 服务的依赖关系（可以为 NULL ）
//		pstError					- 接收错误信息指针（可以为 NULL ）
// 返回：
//		bSuccess					- 成功与否
//-----------------------------------------------------------------------
BOOL odsInstallService( LPCTSTR strComputerName, 
					   LPCTSTR strServiceName, 
					   LPCTSTR strServicePath, 
					   LPCTSTR strDisplayName, 
					   DWORD dwStartType,
					   LPCTSTR strDependencies,
					   STODSERROR* pstError )
{
	BOOL		bRet = TRUE;
    SC_HANDLE   schService;
    SC_HANDLE   schSCManager;

    schSCManager = OpenSCManager(
                        strComputerName,        // machine (NULL == local)
                        NULL,                   // database (NULL == default)
                        SC_MANAGER_ALL_ACCESS   // access required
                        );
    if ( schSCManager )
    {
        schService = CreateService(
            schSCManager,               // SCManager database
            strServiceName,		        // name of service
            strDisplayName,				// name to display
            SERVICE_ALL_ACCESS,         // desired access
            SERVICE_WIN32_OWN_PROCESS,  // service type
            dwStartType,				// start type
            SERVICE_ERROR_NORMAL,       // error control type
            strServicePath,             // service's binary
            NULL,                       // no load ordering group
            NULL,                       // no tag identifier
            strDependencies,			// dependencies
            NULL,                       // LocalSystem account
            NULL);                      // no password

        if ( schService )
        {            
            CloseServiceHandle(schService);
        }
        else
        {
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_INSTSERVICE );
            odsMakeError( pstError, ODS_ERR_INSTSERVICE, NULL );
			bRet = FALSE;
        }

        CloseServiceHandle(schSCManager);
    }
    else
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSCM );
		odsMakeError( pstError, ODS_ERR_OPENSCM, NULL );
		bRet = FALSE;
	}
	return bRet;
}


//----------------------------------------------------------------
// odsRemoveService					- 删除某计算机上的服务
//
// 参数：
//		strComputerName				- 计算机名，为 NULL 时表示本地计算机
//		strServiceName				- 服务名
//		dwWaitTime					- 等待时间
//		pstError					- 错误信息结构指针，可以为 NULL
// 返回：
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsRemoveService( LPCTSTR strComputerName, LPCTSTR strServiceName, DWORD dwWaitTime, STODSERROR* pstError )
{
	BOOL			bRet = TRUE;
	BOOL			bStopped = FALSE;
    SC_HANDLE		schService;
    SC_HANDLE		schSCManager;
	SERVICE_STATUS	ssStatus;
	DWORD			dwSleepLeft = dwWaitTime;
	DWORD			dwSleep = min( 1000, dwSleepLeft );

    schSCManager = OpenSCManager(
                        strComputerName,        // machine (NULL == local)
                        NULL,                   // database (NULL == default)
                        SC_MANAGER_ALL_ACCESS   // access required
                        );
    if ( schSCManager )
    {
        schService = OpenService( schSCManager, strServiceName, SERVICE_ALL_ACCESS );

        if (schService)
        {
            // try to stop the service
            if ( ControlService( schService, SERVICE_CONTROL_STOP, &ssStatus ) )
            {
                do
				{
					Sleep( dwSleep );
					dwSleepLeft -= dwSleep;
					dwSleep = min( 1000, dwSleepLeft );

					QueryServiceStatus( schService, &ssStatus );
                
                    if ( ssStatus.dwCurrentState == SERVICE_STOPPED )
					{
						bStopped = TRUE;
						break;
					}
                }while( dwSleepLeft > 0 );
            }
            // now remove the service
            if( !DeleteService(schService) )
			{
				odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_DELSERVICE );
				odsMakeError( pstError, ODS_ERR_DELSERVICE, NULL );
				bRet = FALSE;
			}

            CloseServiceHandle(schService);
        }
        else
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSERVICE );
            odsMakeError( pstError, ODS_ERR_OPENSERVICE, NULL );
			bRet = FALSE;
		}

        CloseServiceHandle(schSCManager);
    }
    else
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSCM );
		odsMakeError( pstError, ODS_ERR_OPENSCM, NULL );
		bRet = FALSE;
	}

	return bRet;
}

//----------------------------------------------------------------
// odsGetServiceStatus			- 取得指定机器上的服务程序的状态
//
// 参数:
//		lpszComputerName		- 服务程序所在的计算机名称，如果为 NULL
//								- 则指本地机器
//		lpszServiceName			- 服务程序名称
//		lpdwServiceStatus		- 返回服务程序的状态
//		pstError				- 错误信息结构指针，可以为 NULL//
// 返回值:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsGetServiceStatus( LPCTSTR strComputerName, LPCTSTR strServiceName, 
						  LPDWORD lpdwServiceStatus, STODSERROR* pstError )
{
	BOOL			bRet = TRUE;
	SC_HANDLE		schSCManager;			//服务控制管理器的句柄
	SC_HANDLE		schService;				//服务程序的句柄
	SERVICE_STATUS	ssServiceStatus;		//服务状态结构
	
	schSCManager = OpenSCManager(
                        strComputerName,				// machine (NULL == local)
                        NULL,							// database (NULL == default)
                        GENERIC_READ				    // access required
                        );
    if ( schSCManager )
    {
        schService = OpenService( schSCManager, strServiceName, GENERIC_READ );

        if (schService)
        {
            if ( QueryServiceStatus(schService, &ssServiceStatus) )
			{
				if (lpdwServiceStatus != NULL)
					(*lpdwServiceStatus) = ssServiceStatus.dwCurrentState;
			}
			else
			{
				odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_QUERYSERVICE );
				odsMakeError( pstError, ODS_ERR_QUERYSERVICE, NULL );
				bRet = FALSE;
			}
            CloseServiceHandle(schService);
        }
        else
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSERVICE );
            odsMakeError( pstError, ODS_ERR_OPENSERVICE, NULL );
			bRet = FALSE;
		}

        CloseServiceHandle(schSCManager);
    }
    else
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSCM );
		odsMakeError( pstError, ODS_ERR_OPENSCM, NULL );
		bRet = FALSE;
	}
	
	return bRet;
}

//----------------------------------------------------------------
// odsStartService					- 启动服务
//
// 参数:
//		lpszComputerName			- 服务程序所在的计算机名称，如果为 NULL
//									- 则指本地计算机
//		lpszServiceName				- 要启动服务的名称
//		dwWaitTime					- 等待服务启动的时间，以(毫秒)为单位
//		pstError					- 错误信息结构指针，可以为 NULL
//
// 返回:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsStartService(LPCTSTR strComputerName, LPCTSTR strServiceName, 
				   DWORD dwWaitTime, STODSERROR* pstError)
{
	BOOL			bRet = TRUE;
	BOOL			bStarted = FALSE;
    SC_HANDLE		schService;
    SC_HANDLE		schSCManager;
	SERVICE_STATUS	ssStatus;
	DWORD			dwSleepLeft = dwWaitTime;
	DWORD			dwSleep = min( 1000, dwSleepLeft );

    schSCManager = OpenSCManager(
                        strComputerName,        // machine (NULL == local)
                        NULL,                   // database (NULL == default)
                        SC_MANAGER_ALL_ACCESS   // access required
                        );
    if ( schSCManager )
    {
        schService = OpenService( schSCManager, strServiceName, SERVICE_ALL_ACCESS );

        if (schService)
        {
            // try to stop the service
            if (  StartService( schService, 0, NULL ) )
            {
                do
				{
					Sleep( dwSleep );
					dwSleepLeft -= dwSleep;
					dwSleep = min( 1000, dwSleepLeft );

					QueryServiceStatus( schService, &ssStatus );
                
                    if ( ssStatus.dwCurrentState == SERVICE_RUNNING )
					{
						bStarted = TRUE;
						break;
					}
                }while( dwSleepLeft > 0 );
            }
			if ( !bStarted )
			{
				odsMakeError( pstError, ODS_ERR_STARTSERVICE, ODS_MSG_STARTSERVICE );
				bRet = FALSE;
			} 
            CloseServiceHandle(schService);
        }
        else
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSERVICE );
            odsMakeError( pstError, ODS_ERR_OPENSERVICE, NULL );
			bRet = FALSE;
		}

        CloseServiceHandle(schSCManager);
    }
    else
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSCM );
		odsMakeError( pstError, ODS_ERR_OPENSCM, NULL );
		bRet = FALSE;
	}

	return bRet;
}

//----------------------------------------------------------------
// odsStopService					- 停止服务
//
// 参数:
//		lpszComputerName			- 服务程序所在的计算机名称，如果为 NULL
//									- 则指本地计算机
//		lpszServiceName				- 要启动服务的名称
//		dwWaitTime					- 等待服务启动的时间，以(毫秒)为单位
//		pstError					- 错误信息结构指针，可以为 NULL
//
// 返回:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsStopService( LPCTSTR strComputerName, LPCTSTR strServiceName, 
					DWORD dwWaitTime, STODSERROR* pstError )
{
	BOOL			bRet = TRUE;
	BOOL			bStopped = FALSE;
    SC_HANDLE		schService;
    SC_HANDLE		schSCManager;
	SERVICE_STATUS	ssStatus;
	DWORD			dwSleepLeft = dwWaitTime;
	DWORD			dwSleep = min( 1000, dwSleepLeft );

    schSCManager = OpenSCManager(
                        strComputerName,        // machine (NULL == local)
                        NULL,                   // database (NULL == default)
                        SC_MANAGER_ALL_ACCESS   // access required
                        );
    if ( schSCManager )
    {
        schService = OpenService( schSCManager, strServiceName, SERVICE_ALL_ACCESS );

        if (schService)
        {
            // try to stop the service
            if ( ControlService( schService, SERVICE_CONTROL_STOP, &ssStatus ) )
            {
                do
				{
					Sleep( dwSleep );
					dwSleepLeft -= dwSleep;
					dwSleep = min( 1000, dwSleepLeft );

					QueryServiceStatus( schService, &ssStatus );
                
                    if ( ssStatus.dwCurrentState == SERVICE_STOPPED )
					{
						bStopped = TRUE;
						break;
					}
                }while( dwSleepLeft > 0 );                
            }
			if ( !bStopped )
			{
				odsMakeError( pstError, ODS_ERR_STOPSERVICE, ODS_MSG_STOPSERVICE );
				bRet = FALSE;
			} 
            CloseServiceHandle(schService);
        }
        else
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSERVICE );
            odsMakeError( pstError, ODS_ERR_OPENSERVICE, NULL );
			bRet = FALSE;
		}

        CloseServiceHandle(schSCManager);
    }
    else
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_OPENSCM );
		odsMakeError( pstError, ODS_ERR_OPENSCM, NULL );
		bRet = FALSE;
	}

	return bRet;
}