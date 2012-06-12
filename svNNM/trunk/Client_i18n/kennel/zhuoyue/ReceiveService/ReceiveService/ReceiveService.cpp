// ReceiveService.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include "odsService.h"
#include "odsError.h"
#include "odsLog.h"
#include "receiveService.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

typedef int (*lpXmpp)(char * serverName,char *clientName, char* clientPasswd,char* syslogMsg);

// 和服务相关的宏定义，具体的服务程序实现应当修改这几个宏的内容

// 服务程序名称
#define SRV_APPNAME						_T("SiteViewReceiveService")
// 要安装的 NT 服务名称
#define SRV_SERVICENAME					_T("SiteViewReceiveService")
// 要安装的 NT 服务的显示名称
#define SRV_SERVICEDISPLAYNAME			_T("SiteView ReceiveService")
// 服务程序的依赖关系
#define SRV_DEPENDENCIES				_T("\0\0")

CWinApp theApp;

static SERVICE_STATUS_HANDLE g_sshStatusHandle;
static BOOL g_bDebug = FALSE;
static DWORD g_dwCurrentState;

// Locker
HANDLE hMutex;

//
// 主函数，如不带参数，则以服务方式运行；
// 也可以带以下参数而以命令行方式运行
//
// options:
//		-install -remove -debug
//
//		-install 表示在 NT 系统中安装服务
//		-remove 表示在 NT 系统中删除服务
//		-debug 表示以命令行方式运行此程序，以便于调试
//

int _tmain(int argc, _TCHAR* argv[])
{
	int nRetCode = 0;

	// initialize MFC and print and error on failure
	if (!AfxWinInit(::GetModuleHandle(NULL), NULL, ::GetCommandLine(), 0))
	{		
		odsAddToSystemLog( SRV_APPNAME, ODS_MSG_INITMFC, EVENTLOG_ERROR_TYPE );
		return 0;
	}

	// 参数判断
	if ( (argc > 1) && ( *argv[1] == _T('-') || *argv[1] == _T('/') ) )
    {
        if ( _tcsicmp( _T("install"), argv[1] + 1 ) == 0 || _tcsicmp( _T("i"), argv[1] + 1 ) == 0 )
        {
            CmdInstallService();
        }
        else if ( _tcsicmp( _T("remove"), argv[1] + 1 ) == 0 || _tcsicmp( _T("r"), argv[1] + 1 ) == 0)
        {
            CmdRemoveService();
        }
        else if ( _tcsicmp( _T("debug"), argv[1] + 1 ) == 0 || _tcsicmp( _T("d"), argv[1] + 1 ) == 0 )
        {
            g_bDebug = TRUE;
            CmdDebugService(argc, argv);
        }
        else
        {
            goto dispatch;
        }
        return 0;
    }

    // 如果没有参数，则以服务方式运行
dispatch:
	SERVICE_TABLE_ENTRY dispatchTable[] =
    {
        { SRV_SERVICENAME, (LPSERVICE_MAIN_FUNCTION)service_main },
        { NULL, NULL }
    };

    if (!StartServiceCtrlDispatcher(dispatchTable))
	{
		DWORD code = GetLastError();
        odsAddToSystemLog( SRV_APPNAME, ODS_MSG_STARTSVCDISP, EVENTLOG_ERROR_TYPE );
		return 0;
	}

	return 0;
}

void WINAPI service_main(DWORD dwArgc, LPTSTR *lpszArgv)
{
	STODSERROR err;
	err.nCode = 0;

    // register our service control handler:
    //
    g_sshStatusHandle = RegisterServiceCtrlHandler( SRV_SERVICENAME, service_ctrl );

    if (!g_sshStatusHandle)
	{
		odsAddToSystemLog( SRV_APPNAME, ODS_MSG_REGSVCHCTRL, EVENTLOG_ERROR_TYPE );
        return;
	}

    // report the status to the service control manager.
    //
	g_dwCurrentState = SERVICE_START_PENDING;
    if (!odsReportStatusToSCMgr( g_sshStatusHandle, 
			g_dwCurrentState, 0, 5000, NULL ))
        goto cleanup;

    ServiceStart( dwArgc, lpszArgv, &err );

cleanup:

	ServiceCleanup();

    // try to report the stopped status to the service control manager.
    //
	g_dwCurrentState = SERVICE_STOPPED;
    odsReportStatusToSCMgr( g_sshStatusHandle, 
                            g_dwCurrentState, err.nCode, 0, NULL );

    return;
}

void WINAPI service_ctrl(DWORD dwCtrlCode)
{
    // Handle the requested control code.
    //
    switch(dwCtrlCode)
    {
        // Stop the service.
        //
        case SERVICE_CONTROL_STOP:
		case SERVICE_CONTROL_SHUTDOWN:

			g_dwCurrentState = SERVICE_STOPPED;//yi.duan
            odsReportStatusToSCMgr(g_sshStatusHandle, g_dwCurrentState, 0, 5000, NULL);
            ServiceStop();
            return;

        // Update the service status.
        //
        case SERVICE_CONTROL_INTERROGATE:
            break;

        // invalid control code
        //
        default:
            break;

    }    
	odsReportStatusToSCMgr(g_sshStatusHandle, g_dwCurrentState, 0, 5000, NULL);
}

void CmdInstallService()
{
    TCHAR szPath[512];
	STODSERROR err;

    if ( GetModuleFileName( NULL, szPath, 512 ) == 0 )
    {
        _tprintf(_T("Unable to install %s - can not get moudle file name\n"), SRV_SERVICEDISPLAYNAME );
        return;
    }

    if (!odsInstallService( NULL, SRV_SERVICENAME, szPath, SRV_SERVICEDISPLAYNAME, 
							SERVICE_DEMAND_START, SRV_DEPENDENCIES, &err) )
	{
		_tprintf(_T("(%d)%s\n"), err.nCode, err.strMsg );
		return;
	}
}

void CmdRemoveService()
{
	STODSERROR err;

	if (!odsRemoveService( NULL, SRV_SERVICENAME, 30000, &err ))
	{
		_tprintf(_T("(%d)%s\n"), err.nCode, err.strMsg );
		return;
	}    
}

void CmdDebugService(int argc, TCHAR ** argv)
{
	STODSERROR err;

    _tprintf(_T("Debugging %s.\n"), SRV_SERVICEDISPLAYNAME);

    SetConsoleCtrlHandler( ControlHandler, TRUE );

    ServiceStart( (DWORD) argc, argv, &err );

	ServiceCleanup();
}

BOOL WINAPI ControlHandler ( DWORD dwCtrlType )
{
    switch( dwCtrlType )
    {
        case CTRL_BREAK_EVENT:  // use Ctrl+C or Ctrl+Break to simulate
        case CTRL_C_EVENT:      // SERVICE_CONTROL_STOP in debug mode
            _tprintf(_T("Stopping %s.\n"), SRV_SERVICEDISPLAYNAME);
            ServiceStop();
            return TRUE;
            break;
    }
    return FALSE;
}

///////////////////////////////////////////////////////////////////////////////////////
// ServiceStart, ServiceCleanup and ServiceStop


static HANDLE hStopEvent;

void ServiceStart (DWORD dwArgc, LPTSTR *lpszArgv, STODSERROR* pstError)
{	
	// 服务初始化

	// 每一个初始化步骤前，都向服务控制器报告下一步操作最长要多长时间完成，
	// 以便服务控制器判断服务程序是否已失去响应
	if (!g_bDebug)
	{
		g_dwCurrentState = SERVICE_START_PENDING;
		if (!odsReportStatusToSCMgr(g_sshStatusHandle, g_dwCurrentState, 0, 5000, NULL ))            
			return;
	}

	// …………………………………………
	// 初始化终止事件
	hStopEvent = CreateEvent( NULL, FALSE, FALSE, "_ = STOP = _" );
	//m_hEventList[0] = CreateEvent(NULL, FALSE, FALSE, NULL); // terminate signal
	//// m_hEventList[1] = CreateEvent(NULL, FALSE, FALSE, NULL); // terminate signal for 514
	//m_hEventList[1] = CreateEvent(NULL, FALSE, FALSE, NULL); // PIPE server
	//m_hEventList[2] = CreateEvent(NULL, FALSE, FALSE, NULL); // PIPE writer
	

	// 进入服务程序的主流程

	// 向服务控制器报告服务程序已经处于正常运行的状态
	if (!g_bDebug)
	{
		g_dwCurrentState = SERVICE_RUNNING;
		if (!odsReportStatusToSCMgr(g_sshStatusHandle, g_dwCurrentState, 0, 5000, NULL ))            
			 return;
	}

	// ………………………………………… 
	// SiteView OnInit
	if (!OnInit())
	{
		DebugMsg("Init failed.");
		return;
	}

	// 下面是主流程的一个简单例子，每隔 10 秒就在 NT 的日志中写一条信息
	// 并检查退出标记，如果已发出终止服务的命令，就停止循环
	int i = 1;

	// 服务程序的主流程通常都是含有某个特定终止条件的无限循环结构
	Run();
	//for(;;)
	//{
	//	// 如果 hStopEvent 被激活，则停止循环
	//	if ( WaitForSingleObject( hStopEvent, 10000 ) != WAIT_TIMEOUT )
	//		break;

	//	char tmp[100];
	//	sprintf( tmp, "%d", i++ );
	//	odsAddToSystemLog( SRV_APPNAME, tmp, EVENTLOG_INFORMATION_TYPE );
	//	DebugMsg(tmp);
	//}    

     
	TerminateWinsock();

	return;
}

void ServiceCleanup()
{
	// 做服务终止时的系统清理工作

	// 每一个清理步骤前，都向服务控制器报告下一步操作最长要多长时间完成，
	// 以便服务控制器判断服务程序是否已失去响应
	if (!g_bDebug)
	{
		g_dwCurrentState = SERVICE_STOP_PENDING;
		odsReportStatusToSCMgr(g_sshStatusHandle, g_dwCurrentState, 0, 5000, NULL );
	}    

	// …………………………………………
	CloseHandle( hStopEvent );
}

void ServiceStop()
{
    // 停止服务，在这里激活终止主流程的特定条件

	// …………………………………………
	SetEvent( hStopEvent );
}

// SysLog 业务开始
BOOL OnInit()
{
	// Perform any initialization that needs to be done before intering the main loop
	char szError[MAX_EVENT];
	
	// Save this off so we can use it to write stuff to the registry.

	if( !InitWinsock( szError ) )
	{
		if(g_bDebug) 
		{
			DebugMsg(szError); 
		}
		return FALSE;
	}

	m_hEventList[0] = CreateEvent(NULL, FALSE, FALSE, NULL); // terminate signal
	// m_hEventList[1] = CreateEvent(NULL, FALSE, FALSE, NULL); // terminate signal for 514
	m_hEventList[1] = CreateEvent(NULL, FALSE, FALSE, NULL); // PIPE server
	m_hEventList[2] = CreateEvent(NULL, FALSE, FALSE, NULL); // PIPE writer
	
	hMutex = CreateMutex(NULL,FALSE,"SnareLock");
	if(hMutex == NULL)
	{
		if(g_bDebug) 
		{
			DebugMsg("I cannot create the 'Mutex' lock. This probably means that you already have another instance of this service running.\nPlease stop the other incarnation before continuing."); 
		}
		return FALSE;
	}

	InsertTable("syslog", 802);
	

	return true;
}

void DebugMsg(const char* pszFormat, ...)
{
	char buf[MAX_EVENT];
	char date[32];
	char time[32];
	
	SYSTEMTIME st;

	GetLocalTime(&st);
	GetDateFormat(LOCALE_SYSTEM_DEFAULT,0,&st,"yyyy-MM-dd",date,sizeof(date));
	GetTimeFormat(LOCALE_SYSTEM_DEFAULT,0,&st,"HH':'mm':'ss",time,sizeof(time));

	_snprintf(buf, MAX_EVENT, "ThreadId:%lu - %s %s): ", GetCurrentThreadId(),date,time);
	va_list arglist;
	va_start(arglist, pszFormat);
    _vsnprintf(&buf[strlen(buf)],MAX_EVENT-strlen(buf)-1,pszFormat,arglist);
	va_end(arglist);
    _snprintf(buf,MAX_EVENT,"%s\n",buf);

	if(buf) {
		OutputDebugString(buf);
		printf("%s",buf);
		fflush(stdout);
		//if(!this->DEBUGSET) {
		//	this->LogEvent(EVENTLOG_INFORMATION_TYPE,EVMSG_DEBUG,buf);
		//}
	}
}

BOOL InitWinsock( char *szError )
{
	WSAData wsData;

	if(!szError) return(FALSE);
	
	WORD wVersionRequested = WINSOCK_VERSION;
	
	if(WSAStartup(wVersionRequested, &wsData) != 0)
	{
		// :( error
		if( szError )
		{
			sprintf(szError,"WSAStartup failed: WSA ERROR: %d\r\n",
				WSAGetLastError());
			if (g_bDebug) 
			{ 
				if(szError)
				{
					DebugMsg(szError); 
				}
			}
		}
		return FALSE;
	}
	
	// all is well
	return TRUE;
}

void TerminateWinsock()
{
	// cancel blocking calls, if any
	WSACancelBlockingCall();
		
	// unload winsock
	WSACleanup();
}

void Run()
{
	InitFilterParam();

	std::list<string> keylist;
	std::list<string>::iterator keyitem;
	std::list<string> settingsValueList;
	std::list<string>::iterator valueItem;

	if(GetIniFileSections(keylist, "nnmsyslogalarm.ini"))
	{
		string matchStr = "";
		for(keyitem = keylist.begin(); keyitem != keylist.end(); keyitem ++)
		{
			DebugMsg("%s", (*keyitem).c_str());
			matchStr = GetIniFileString((*keyitem).c_str(),"settingsValue","null","nnmsyslogalarm.ini");
			DebugMsg( "matchStr:%s", matchStr.c_str());
			settingsValueList.push_back(matchStr);
		}
	}
	else
	{
		DebugMsg("read nnmsyslogalarm.ini failed!");
	}
	
//	// Define an eventlogrecord buffer of 8k.
//	// Should be enough for an overwhelming majority of circumstances.
//	// TCHAR EventLogRecordBuffer[MAX_EVENT] = "";
	TCHAR EventLogRecordBuffer2[MAX_EVENT] = "";
	char *pEvent = NULL;

	OVERLAPPED Overlapped;	// PIPE variable
	OVERLAPPED OverlappedWrite;	// PIPE variable

	// Destination for log events. Default it to something safe.
	// TCHAR lpszDestination[512] = "127.0.0.1";

//	int retval;

//	SOCKET snare_socket;	// 6161
//	SOCKET syslog_socket;	// 514
//	SOCKADDR_IN snare_in;
//	SOCKADDR_IN syslog_in;

//	SOCKADDR_IN recv_socket;
//	int recv_socketlen = sizeof(recv_socket);
	
	char *ipbuff;
	char ipbuffer[16];

	char SourceName[256] = "";
	char LastSourceName[256] = "";
	char LogType[256] = "";
	char LastLogType[256] = "";
	int criticality = 0;
	char Date[256] = "";
	char StartDate[256] = "";
	char CurrentDateTime[256] = "";

	char Path[4096] = ""; // 4k path
	char Filename[4096] = "";

	struct tm *LocalTime1 = NULL;
	int tyear = 0,tday = 0;

	time_t CurrentTime = 0;
	time_t LastTime = 0;
	
	int DateChanged = 0;
	int LogTypeChanged = 0;
	int SourceNameChanged = 0;


	DWORD dwWaitRes = 0;
	BOOL WritePipeConnected = FALSE;
	
	CurrentTime = time(NULL);
	LocalTime1 = localtime(&CurrentTime);
	strftime(StartDate, sizeof(StartDate),"%Y%m%d %H:%M:%S", LocalTime1);

	//if(!::CreateQueue("SiteView70-SysLog", 1))
	//	DebugMsg("Create SiteView70-SysLog queue failed");
	//else
	//	DebugMsg("Create SiteView70-SysLog ok");

	if(_beginthread(ReceiveThread, 0, (HANDLE) m_hEventList[0] ) ==-1) 
	{
		DebugMsg("Error in ReceiveThread thread creation");
		return;
	}
	
	if(_beginthread(SyslogThread, 0, (HANDLE) m_hEventList[0] ) ==-1) 
	{
		DebugMsg("Error in thread creation");
		return;
	}

	if(_beginthread(WinMsgThread, 0, (HANDLE) m_hEventList[0] ) ==-1) 
	{
		DebugMsg("Error in thread creation");
		return;
	}

	if(g_bDebug) 
	{ 
		DebugMsg("Creating named pipe."); 
	}

	g_Info.bTerminate = FALSE;
	
	if(g_bDebug) 
	{ 
		DebugMsg("Entering main loop."); 
	}

	// This is the service's main run loop.

	m_bIsRunning = 1;
    while (m_bIsRunning) 
	{
	//	MSG msg;
	//	while(GetMessage(&msg, NULL, 0, 0))
	//	{
	//		TranslateMessage(&msg);
	//		DispatchMessage(&msg);
	//		switch(msg.message )
	//		{
	//			case WM_QUIT:
	//				goto quit;
	//			default:
	//				break;
	//		}
	//	}
	//quit:
		// If we have been asked to terminate, do so.
		if(g_Info.bTerminate)
		{
			m_bIsRunning = 0;
			break;
		}	
		dwWaitRes = WaitForMultipleObjects(3,m_hEventList,FALSE,1000);

	
		if(dwWaitRes == WAIT_FAILED)
		{
			// Keep spinning.
			continue;
		}
		else if(dwWaitRes == WAIT_OBJECT_0 || g_Info.bTerminate == TRUE) 
		{
			m_bIsRunning = 0;
			g_Info.bTerminate = 1;
			// Terminate!!
			break;
		}
		else if (dwWaitRes == WAIT_OBJECT_0 + 1) 
		{
			// PIPE event.
			int piperc;
			if(g_bDebug)
			{ 
				DebugMsg("Pipe Event received"); 
			}

		
			continue;
		}
		else if (dwWaitRes == WAIT_OBJECT_0 + 2) 
		{
			//ResetEvent(m_hEventList[2]);
			WritePipeConnected = TRUE;
		}
		else if(dwWaitRes == WAIT_TIMEOUT) 
		{

			currentnode = head;

			while(currentnode) 
			{

				int OutputLength = strlen(currentnode->Line);
				pEvent = currentnode->Line;
				if(OutputLength) 
				{
					// Kill off any final newlines
					char *stringp = &currentnode->Line[OutputLength-1];
					while(stringp > currentnode->Line) 
					{
						if(*stringp =='\n') 
						{
							*stringp = '\0';
							OutputLength--;
						}
						else 
						{
							break;
						}
					}
				
					strncpy(SourceName,currentnode->IPAddress,sizeof(SourceName));

					criticality = 0;

					if(currentnode->LogType == 0) 
					{
						char *tabpos;
						int copysize = 0;

						strncpy(LogType,"Generic",sizeof(LogType));
						// 6161. There should be source DNS, type (criticality)
						stringp = currentnode->Line;

						// Is this from a snare reflector? If so, there's probably a [ip.address]\t at the
						// start of the message.
						if(*stringp == '[') 
						{
							stringp++;
							tabpos = strstr(stringp,"	");
							if(tabpos) 
							{
								copysize = tabpos-stringp-1;
								if(copysize > sizeof(SourceName)) 
								{
									copysize = sizeof(SourceName);
								}

								strncpy(SourceName,stringp,copysize);
								SourceName[copysize] = '\0';
								stringp = tabpos+1;
								pEvent = tabpos+1;
							} 
							else 
							{
								stringp--;
							}
						}
						

						tabpos = strstr(stringp,"	");
						if(tabpos) 
						{
							pEvent = tabpos+1;
							copysize = tabpos-stringp;
							if(copysize > sizeof(SourceName)) 
							{
								copysize = sizeof(SourceName);
							}

							strncpy(SourceName,stringp,copysize);
							SourceName[copysize] = '\0';

							stringp = tabpos+1;
							tabpos = strstr(stringp,"	");
							if(tabpos) 
							{
								pEvent = tabpos+1;
								copysize = tabpos-stringp;
								if(copysize > sizeof(LogType)) 
								{
									copysize = sizeof(LogType);
								}
								strncpy(LogType,stringp,copysize);
								
								stringp = tabpos+1;
								if((*stringp>= '0' && *stringp <= '9') && *(stringp+1) == '	') 
								{
									char critstr[2];
									critstr[0] = *stringp;
									critstr[1] = '\0';
									criticality = atoi(critstr);
									pEvent = stringp+2;
								}
							}
						}
					}
					else 
					{
						char *tabpos;
						int copysize = 0;

						pEvent = currentnode->Line;
						stringp = currentnode->Line;
						// Is this from a snare reflector? If so, there's probably a [ip.address]\t at the
						// start of the message.
						if(*stringp == '[') 
						{
							stringp++;
							tabpos = strstr(stringp,"	");
							if(tabpos) 
							{
								copysize = tabpos-stringp-1;
								if(copysize > sizeof(SourceName)) 
								{
									copysize = sizeof(SourceName);
								}
								strncpy(SourceName,stringp,copysize);
								SourceName[copysize] = '\0';
								stringp = tabpos+1;
								pEvent = tabpos+1;
							}
							else 
							{
								stringp--;
							}
						}
						
						strncpy(LogType,"Syslog",sizeof(LogType));
						criticality = 5;	// Syslog
					}

					if(pEvent > currentnode->Line+strlen(currentnode->Line)) 
					{
						// Oh dear - past the end. Jump back.
						pEvent = currentnode->Line+strlen(currentnode->Line);
					}
					

					{
						try 
						{
							//fprintf(OutputFile,"%s\n",currentnode->Line);
							//fflush(OutputFile);
							
							//解析
							string strLine = currentnode->Line;

							string strFicility = "", strLevel = "";
							int i = strLine.find(">");
							string strHead = strLine.substr(1, i - 1);
							int nHead = 0;
							sscanf(strHead .c_str(), "%d", &nHead);
							nFicility = nHead / 8;
							nLevel = nHead % 8;

							char tmpBuf[12]  = {0};
							sprintf(tmpBuf, "%d", nFicility);
							strFicility = tmpBuf;

							sprintf(tmpBuf, "%d", nLevel);
							strLevel = tmpBuf;
							
							EnterCriticalSection(&csFilter); 
							//条件匹配
							if((mapFacility.find(strFicility) != mapFacility.end()) && (mapSeverities.find(strLevel) != mapSeverities.end()))
							{
								TTime curTime = TTime::GetCurrentTimeEx();
								if (g_bDebug)
								{
									DebugMsg("Insert SysLog: Time %s, SrcIp %s, SysLogMsg %s", curTime.Format().c_str(), currentnode->IPAddress, currentnode->Line);
								}
//								if(InsertRecord("syslog", 0, curTime.Format(), currentnode->IPAddress, currentnode->Line, nFicility, nLevel))
//								{
									DebugMsg("Insert success!");
//yi.duan add send xmpp------------------------------------------------
//									std::list<string> keylist;
//									std::list<string>::iterator keyitem;
//									if(GetIniFileSections(keylist, "nnmsyslogalarm.ini"))
//									{
										//从ini初始化报警列表
										string matchStr = "";
										string matchIP = "";
										string matchValue = "";
										DebugMsg( "--------------------------------------");
//										for(keyitem = keylist.begin(); keyitem != keylist.end(); keyitem ++)
										for(valueItem = settingsValueList.begin(); valueItem != settingsValueList.end(); valueItem ++)
										{
											DebugMsg( "--------------------------------------222");
											DebugMsg("settingsValue = %s", (*valueItem).c_str());
//											matchStr = GetIniFileString((*keyitem).c_str(),"settingsValue","null","nnmsyslogalarm.ini");
											matchStr = *valueItem;
											DebugMsg( "matchStr:%s", matchStr.c_str());

											//matchStr = "192.168.0.238:down"
											if (matchStr!="null")
											{	
												matchIP = matchStr.substr(0,matchStr.find(":"));
												matchValue = matchStr.substr(matchStr.find(":")+1);
												//DebugMsg( "matchIP:%s", matchIP.c_str());
												//DebugMsg( "matchValue:%s", matchValue.c_str());
												
											}
											DebugMsg( "=================================");
											//if (strstr(currentnode->Line,matchIP.c_str()) && strstr(currentnode->Line,matchValue.c_str()))
											if (strstr(currentnode->Line, matchValue.c_str()))
											{
												DebugMsg("match syslog!!!");
												//get hostname begin
												char hostname[128];
												int iRet = 0;

												memset(hostname, 0, 128);
												iRet = gethostname(hostname, sizeof(hostname));
												if(iRet != 0 )
												{
													DebugMsg( "get hostname error:%d", iRet);
												}
												
												//get hostname end

												HINSTANCE   hInstance;
												lpXmpp     pTelnet = NULL;  //lpTelnet

												hInstance = ::LoadLibrary(_T("xmpp.dll"));         //lpTelnet 
												if (hInstance == NULL)
												{
													DebugMsg("LoadLibrary xmpp.dll error");
													FreeLibrary(hInstance);
												}

												pTelnet = (lpXmpp)GetProcAddress(hInstance,"Test");  

												char serverName[256] = {0};
												char clientName[256] = {0};
												sprintf(serverName, "%s@%s/%s", "test1",hostname,"Spark 2.6.3");
												sprintf(clientName, "%s@%s/%s", "test",hostname,"client");
												char* clientPasswd = "test";
												//char* syslogMsg = currentnode->Line;
												char syslogMsg[10240] = {0};
												sprintf(syslogMsg,"%s$$%s$$%s",matchIP.c_str(), currentnode->Line,(curTime.Format()).c_str());												
												int res = pTelnet(serverName,clientName,clientPasswd,syslogMsg);
												
												if (::FreeLibrary(hInstance)==0)  
												{//如果dll里面有线程，资源会被释放而出错，而进程没问题，！！！！！
													//主程序退出了线程回收了
													DebugMsg("FreeLibrary xmpp.dll error");
												}
											 }
											 else
											 {
												  DebugMsg("Not match !!!");
											 }

											 Sleep(50);
										 }//end for
									//}//end if read nnmsyslogalarm.ini
//									else
//									{
//										DebugMsg("read nnmsyslogalarm.ini failed!");
//									}
//yi.duan--------------------------------------------------------------
//								}
//								else
//								{
//									DebugMsg("Insert failed!");
//								}
							}
							LeaveCriticalSection(&csFilter);
						}
						catch (...) 
						{
							DebugMsg("Cannot write line to outputfile! Exception recorded.\n");
						}
					}
					
				
				}



				// Ok, we have everything we need out of the current node.
				// Remove it from the linked list.

				// HMM... we receive the access violation here!! (so violation must be happening in one of the other threads!

				dwWaitRes = WaitForSingleObject(hMutex,INFINITE);
				if(dwWaitRes == WAIT_OBJECT_0) 
				{
					// Ok, we have a lock.
					currentnode = head->next;
					
					
					// HMmmm.. this 'free' is causing an access violation?
					free(head);
					head = currentnode;
					if(head == NULL) 
					{
						// Last element? Reset tail too.
						tail = NULL;
					}
					ReleaseMutex(hMutex);
				}
			} // end while(currentnode)
		}
    }

//quit:
	if(g_bDebug) 
	{ 
		DebugMsg("SNARE Closing"); 
	}

	// Try and be nice about killing the linked list, by giving
	// other threads a few seconds to shutdown.
	dwWaitRes = WaitForSingleObject(hMutex,5000);

	// But even if we don't have the lock by now, try and kill our linked list anyway.
	while(head) 
	{
		currentnode = head->next;
		free(head);
		head = currentnode;
	}

	//if(hPipe != INVALID_HANDLE_VALUE) {
	//	CloseHandle(hPipe);
	//}
	//if(hWritePipe != INVALID_HANDLE_VALUE) {
	//	CloseHandle(hWritePipe);
	//}
	
	TerminateWinsock();

	if( m_hEventList[0] ) ::CloseHandle(m_hEventList[0]);
	if( m_hEventList[1] ) ::CloseHandle(m_hEventList[1]);
	if( m_hEventList[2] ) ::CloseHandle(m_hEventList[2]);
}

void InitFilterParam()
{
	InitializeCriticalSection(&csFilter);

	strKeepDay = GetIniFileString("DelCond", "KeepDay", "", "syslog.ini");
	if(strKeepDay.length() == 0)
		strKeepDay = "0";
	DebugMsg(strKeepDay.c_str());

	::string strSeveritiesValue = "";
	::string strFacilityValue = "";
	::string strTmp = "";

	unsigned int len;
	char buff[MAX_EVENT] = "";
	GetIniFileStruct("QueryCond", "Facility", buff, len, "syslog.ini");

	strFacilityValue.append(buff);
	DebugMsg(buff);

	memset(buff,0,MAX_EVENT);
	GetIniFileStruct("QueryCond", "Severities", buff, len, "syslog.ini");

	strSeveritiesValue.append(buff);
	DebugMsg(buff);

	delete[] buff;


	//try
	//{
	//	strFacilityValue = GetIniFileString("QueryCond", "Facility", "", "syslog.ini");	
	//}
	//catch(...)
	//{}
	//try
	//{
	//	strSeveritiesValue = GetIniFileString("QueryCond", "Severities", "", "syslog.ini");	
	//}
	//catch(...)
	//{}


	std::list<string> pTemptList;
	list <string>::iterator listitem;

	ParserToken(pTemptList, strFacilityValue.c_str(), ",");		
	for(listitem = pTemptList.begin(); listitem != pTemptList.end(); listitem++)
	{
		mapFacility[(*listitem)] = 0;
	}	

	pTemptList.clear();	
	ParserToken(pTemptList, strSeveritiesValue.c_str(), ",");		
	for(listitem = pTemptList.begin(); listitem != pTemptList.end(); listitem++)
	{
		mapSeverities[(*listitem)] = 0;
	}

	//设定定时刷新频率:1天
	svutil::TTime time = svutil::TTime::GetCurrentTimeEx();
	int nMinute = 60 - time.GetMinute();
	int nHour = 23 - time.GetHour();
	
	dwSleep =  (nHour * 60 + nMinute) * 60 * 1000;
	//hTime = SetTimer(NULL, NULL, (nHour * 60 + nMinute) * 60 * 1000, TimerProc);

	//MSG msg;
	//while (GetMessage(&msg, 0, 0, 0))
	//	DispatchMessage(&msg);
	//hTime = SetTimer(NULL, 1234, 5000, (TIMERPROC)TimerProc);

}

void ReceiveThread(HANDLE event)
{
	DWORD dwWaitResult;
	while(WaitForSingleObject(event,0)!=WAIT_OBJECT_0) 
	{	
		::Sleep(50);

		MQRECORD mrd;

		mrd=::BlockPopMessage("SiteView70-SysLog");

		if(mrd!=INVALID_VALUE)
		{
			//puts("Pop message failed");

			string label;
			svutil::TTime ct;
			unsigned int len=0;

			if(!::GetMessageData(mrd, label, ct, NULL, len))
			{
				DebugMsg("Get message data failed");
			}

			//printf("Data len is :%d\n",len);
			char * buf = NULL;
			buf = new char[len];

			if(!::GetMessageData(mrd, label, ct, buf, len))
			{
				DebugMsg("Get message data failed");
			}
			
			::CloseMQRecord(mrd);
			
			OutputDebugString(label.c_str());
			OutputDebugString("\n");
			string szSmsTo = "";
			std::list<string> listIni;
			std::list<string>::iterator listIniItem;
			EnterCriticalSection(&csFilter);
			if(label == "FacilityChange")
			{

				mapFacility.clear();

				//FacilityChange
				DebugMsg("SysLog FacilityChange\n");
				DebugMsg(buf);


				szSmsTo += buf;				
				ParserToken(listIni, szSmsTo.c_str(), ",");
				for(listIniItem = listIni.begin(); listIniItem!=listIni.end(); listIniItem++)
				{
					mapFacility[(*listIniItem)] = 0;
				}
			}
			else if(label == "SvrChange")
			{
				mapSeverities.clear();

				//SvrChange
				DebugMsg("SysLog SvrChange\n");
				DebugMsg(buf);

				szSmsTo += buf;
				ParserToken(listIni, szSmsTo.c_str(), ",");
				for(listIniItem = listIni.begin(); listIniItem!=listIni.end(); listIniItem++)
				{
					mapSeverities[(*listIniItem)] = 0;
				}	
			}
			else if(label == "KeepDayChange")
			{
				//KeepDayChange
				DebugMsg("KeepDayChange \n");
				DebugMsg(buf);

				szSmsTo += buf;
				strKeepDay = szSmsTo;
			}
			else
			{
				
			}
			LeaveCriticalSection(&csFilter);
		}

		dwWaitResult = WaitForSingleObject(hMutex,5000);
		if(dwWaitResult != WAIT_OBJECT_0) 
		{			
			continue;
		}

		// Release our lock.
		if(!ReleaseMutex(hMutex)) 
		{			
			::SetEvent(event);
			return;
		}
	}
}

//
void WinMsgThread(HANDLE event)
{
	DWORD dwWaitResult;
	while(WaitForSingleObject(event,0)!=WAIT_OBJECT_0) 
	{	
		if(bSet)		
			::Sleep(dwSleep);
		else		
		{
			//KillTimer(NULL, hTime);
			//SetTimer(NULL, NULL, 24*60*60*1000, TimerProc);
			//MSG msg;
			::Sleep(24*60*60*1000);
			bSet = false;
		}

		//获取当前时间并减去保持天数以得到删除记录的终止时间	
		//OutputDebugString("TimerProc\r\n");
		//OutputDebugString(strKeepDay.c_str());
		
		//删除记录
		int nKeepDay = 0;
		sscanf(strKeepDay.c_str(), "%d", &nKeepDay);
		
		if(nKeepDay != 0)
		{
			TTime curTime = TTime::GetCurrentTimeEx();
			
			TTimeSpan ts(0, nKeepDay*24, 0, 0);
			curTime -= ts;

			//OutputDebugString("Delete\r\n");
			DeleteRecords("syslog", curTime);
		}

		//MSG msg;
		//while(GetMessage(&msg, NULL, 0, 0))
		//{
		//	TranslateMessage(&msg);
		//	DispatchMessage(&msg);
		//	//switch(msg.message )
		//	//{
		//	//	case WM_QUIT:
		//	//		goto quit;
		//	//	default:
		//	//		break;
		//	//}
		//}

		dwWaitResult = WaitForSingleObject(hMutex,5000);
		if(dwWaitResult != WAIT_OBJECT_0) 
		{			
			continue;
		}
		
		// Release our lock.
		if(!ReleaseMutex(hMutex)) 
		{			
			::SetEvent(event);
			return;
		}
	}
}
//
void SyslogThread(HANDLE event)
{
	SOCKET syslog_socket;
	SOCKADDR_IN syslog_in;
	SOCKADDR_IN recv_socket;
	int recv_socketlen=sizeof(recv_socket);
	
	DWORD dwWaitResult;
	int retval;

	Node * newNode=NULL;
	char * ipbuff=NULL;

	fd_set rfds;
	struct timeval tv;

	// Set up our network ports.	
	syslog_socket = WSASocket(AF_INET, SOCK_DGRAM, IPPROTO_UDP, NULL, 0, WSA_FLAG_OVERLAPPED);

	if(!syslog_socket) 
	{
		printf("Cannot open sockets - Exiting.\n");
		return;
	}

	syslog_in.sin_family=AF_INET;
	syslog_in.sin_port = htons((u_short)514);
	syslog_in.sin_addr.s_addr=htonl(INADDR_ANY);

	if(bind(syslog_socket,(SOCKADDR *)&syslog_in, sizeof(syslog_in)) != 0) 
	{
		printf("Bind Failed - syslog socket. Do you already have something running on the syslog UDP port?\n");
		return;
	}

	// While we aren't being requested to terminate.
	while(WaitForSingleObject(event,0)!=WAIT_OBJECT_0) 
	{
		
		// Select.. if no data, continue.
		FD_ZERO(&rfds);
		FD_SET(syslog_socket,&rfds);
		tv.tv_sec=1;
		tv.tv_usec=0;

		
		retval=select(syslog_socket+1,&rfds,NULL,NULL,&tv);

		if(retval <= 0) 
		{
			// No data. keep spinning.
			continue;
		}
		
		// Request ownership of the mutex.
		// Note: Only circumstance where this may fail, is if the syslog grabs it first.
		dwWaitResult = WaitForSingleObject(hMutex,5000);
		if(dwWaitResult != WAIT_OBJECT_0) 
		{
			// Keep spinning.
			continue;
		}

		do
		{
			// Ok, we have data. Create a new node.
			newNode = (Node *) malloc(sizeof(Node));

			retval = recvfrom(syslog_socket,newNode->Line,sizeof(newNode->Line),0,
						(struct sockaddr *)&recv_socket,&recv_socketlen);

			if(retval > 0) 
			{
				newNode->Line[retval]='\0';
			}


			ipbuff=inet_ntoa(recv_socket.sin_addr);
			if(ipbuff) 
			{
				strncpy(newNode->IPAddress,ipbuff,sizeof(newNode->IPAddress));
			}
			else
			{
				strncpy(newNode->IPAddress,"\0",sizeof(newNode->IPAddress));
			}
		
			newNode->next=NULL;
			newNode->LogType=1;	// syslog.

			// Add the new node to the end of the list.
			// First, lock the mutex.
		
			// Add this to the list.
			if(tail) 
			{
				tail->next=newNode;
			}
			tail=newNode;

			if(!head) 
			{
				head=tail;
			}
			SyslogPackets++;
			
			if(SyslogPackets == ULONG_MAX) 
			{
				SyslogPackets=1;
			}
			
			FD_ZERO(&rfds);
			FD_SET(syslog_socket,&rfds);
			tv.tv_sec=1;
			tv.tv_usec=0;

			retval=select(syslog_socket+1,&rfds,NULL,NULL,&tv);
		} while(retval);

		// Release our lock.
		if(!ReleaseMutex(hMutex)) 
		{
			// Failed??? You're kidding me.
			// Better clear the events, since we're the only one with the lock, then die.
			Node * tempnode;

			currentnode = head;
			while(currentnode) 
			{
				tempnode=currentnode->next;
				free(currentnode);
				currentnode=tempnode;
			}
			closesocket(syslog_socket);
				
			::SetEvent(event);
			return;
		}
	}
	closesocket(syslog_socket);
}