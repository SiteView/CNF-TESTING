//
// service.h - 服务程序框架
//


#if !defined(AFX_SERVICE_H__40095FEA_5F70_11D4_B3A2_0080C88FC26A__INCLUDED_)
#define AFX_SERVICE_H__40095FEA_5F70_11D4_B3A2_0080C88FC26A__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#define MAX_EVENT 8192

#include "odsError.h"

#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <windows.h>
#include <time.h>
#include <tchar.h>
#include <limits.h>
#include <process.h>
#include <sys/stat.h>
#include <map>
#include <list>
#include <string>
#include <String.h>

#include "../include/svapi.h"
#include "../include/svdbapi.h"


using namespace std;
using namespace svutil;

////////////////////////////////////////////////////////////////////////
//全局变量

// Linked list. NOTE: ALL Changes to these variables should be locked in a Mutex.
struct _node {
	char Line[MAX_EVENT];	// Could malloc and free this instead.
	int LogType;			// 0 for snare, 1 for syslog?
	char IPAddress[64];		// technically, we only need 16 for ipv4..
	struct _node *next;
};

typedef struct _node Node;
static Node *head = NULL;
static Node *tail = NULL;
static Node *currentnode = NULL;
int NodeCounter = 0;

	int nFicility;
	int nLevel;
//
//unsigned long SnarePackets=0;
unsigned long SyslogPackets=0;
//
CRITICAL_SECTION csFilter; 

typedef struct
{
	SOCKET hSocket;
	BOOL bTerminate;
} ThreadStruct;

////void    SnareThread				(HANDLE event);
//void    SyslogThread			(HANDLE event);
//void    ReceiveThread			(HANDLE event);
//void    WinMsgThread			(HANDLE event);
//
HANDLE m_hEventList[3]; // Three elements.
ThreadStruct	g_Info;
BOOL m_bIsRunning;
map<string, int, less<string> > mapFacility;
map<string, int, less<string> > mapSeverities;
//
//UINT hTime;
DWORD dwSleep = 0;
bool bSet = true;
//VOID CALLBACK TimerProc(HWND hwnd, UINT uMsg, UINT idEvent, DWORD dwTime);
::string strKeepDay;
//////////////SysLog日志数据库的插入函数///////

// 函数定义
void WINAPI service_main(DWORD dwArgc, LPTSTR *lpszArgv);
void WINAPI service_ctrl(DWORD dwCtrlCode);
void CmdInstallService();
void CmdRemoveService();
void CmdDebugService(int argc, TCHAR ** argv);
BOOL WINAPI ControlHandler ( DWORD dwCtrlType );
void ServiceStart (DWORD dwArgc, LPTSTR *lpszArgv, STODSERROR* pstError);
void ServiceCleanup();
void ServiceStop();

// SysLog
BOOL OnInit();
void DebugMsg(const char* pszFormat, ...);
BOOL InitWinsock( char *szError );
void TerminateWinsock();

void Run();
void InitFilterParam();
void ReceiveThread(HANDLE event);
void WinMsgThread(HANDLE event);
void SyslogThread(HANDLE event);

#endif // !defined(AFX_SERVICE_H__40095FEA_5F70_11D4_B3A2_0080C88FC26A__INCLUDED_)


bool ParserToken(list<string >&pTokenList, const char * pQueryString, char *pSVSeps)
{
    char * token = NULL;
    // duplicate string
	char * cp = ::strdup(pQueryString);
    if (cp)
    {
        char * pTmp = cp;
        if (pSVSeps) // using separators
            token = strtok( pTmp , pSVSeps);
        else // using separators
			return false;
            //token = strtok( pTmp, chDefSeps);
        // every field
        while( token != NULL )
        {
            //triml(token);
            //AddListItem(token);
			pTokenList.push_back(token);
            // next field
            if (pSVSeps)
                token = strtok( NULL , pSVSeps);
            else
               return false;
				//token = strtok( NULL, chDefSeps);
        }
        // free memory
        free(cp);
    }
    return true;
}

struct RecordHead
{

    int prercord;

    int state;

    TTime createtime;

    int datalen;
}; 

//
char *buildbuf(int data,char *pt,int buflen)
{

    if(pt==NULL)
        return NULL;

    if(buflen<sizeof(int))
        return NULL;

    memmove(pt,&data,sizeof(int));

    pt+=sizeof(int);

    return pt;
}

//
char *buildbuf(float data,char *pt,int buflen)
{

    if(pt==NULL)

        return NULL;

    if(buflen<sizeof(float))

        return NULL;

    memmove(pt,&data,sizeof(float));

    pt+=sizeof(float);

    return pt;
}

//
char *buildbuf(string data,char *pt,int buflen)
{

    if(pt==NULL)

        return NULL;

    if(buflen<(data.size()+1))

        return NULL;

    strcpy(pt,data.c_str());

    pt+=data.size();

    pt[0]='\0';

    pt++;

    return pt;
}


//插记录到SysLog表
bool InsertRecord(string strTableName, int nSysLogIndex, string strTime, string strSrcIp, string strSysLogMsg, int nFicility, int nLevel)
{
    char data[1024]={0};
    RecordHead *prd=(RecordHead*)data;
    char *pt=data+sizeof(RecordHead);
    char *pm=NULL;

	DebugMsg("Insert SysLog: Time %s, SrcIp %s, SysLogMsg %s ,nFicility = %d, nLevel = %d", 
		strTime.c_str(), strSrcIp.c_str(), strSysLogMsg.c_str(),nFicility,nLevel);

	if((pm=::buildbuf(nSysLogIndex,pt,1024))==NULL)
    {
        //puts("build alertindex failed");
        return false;
	}
	
	if((pm=::buildbuf(strTime,pm,1024))==NULL)
    {
        //puts("build alerttime failed");
        return false;
	}
    
	if((pm=::buildbuf(strSrcIp,pm,1024))==NULL)
    {
        //puts("build rulename failed");
        return false;
	}

	if((pm=::buildbuf(strSysLogMsg,pm,1024))==NULL)
    {
        //puts("build entityname failed");
        return false;
	}
	
	if((pm=::buildbuf(nFicility,pm,1024))==NULL)
    {
        //puts("build monitorname failed");

        return false;
	}
	
	if((pm=::buildbuf(nLevel,pm,1024))==NULL)
    {
        //puts("build alertreceivename failed");
        return false;
	}
	
    prd->datalen=pm-pt;
    prd->state=1;
    prd->createtime=svutil::TTime::GetCurrentTimeEx();

    strcpy(pm,"DynString");

    int len=pm-data;
    len+=strlen(pm)+1;

	DebugMsg("state = %d, datalen = %d, createTime = %s",
		 prd->state,  prd->datalen, (prd->createtime).Format().c_str()); 

    if(!::AppendRecord(strTableName,data,len))
    {
        //puts("Append record failed");
        return false;
    }
	//else
 //       puts("Append OK");

	return true;
}

