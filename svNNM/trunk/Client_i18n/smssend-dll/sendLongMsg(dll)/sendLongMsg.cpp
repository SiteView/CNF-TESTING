// sendLongMsg.cpp : 定义 DLL 应用程序的入口点。
//

#include "stdafx.h"
#include <atlstr.h>
#include "Gsm.h"

BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
    return TRUE;
}

//just long message
extern "C" __declspec(dllexport) 
bool sendLongMessage(char *portName, char *strRecvPhone , char* content)
{
	SM_param pSm;
	pSm.strText		= content;
	pSm.strNumber	= strRecvPhone;
	pSm.ucValidTime	= 0; //有效时间5分钟

	CGsm gsm;
	gsm.SetPortName(portName);
	gsm.m_comm.SetRate("9600");

	//CString strPortName = gsm.m_comm.GetPortName();
	//if( gsm.m_comm.OpenComm(strPortName.GetBuffer(strPortName.GetLength()), CBR_9600, NOPARITY, 8, ONESTOPBIT) == FALSE )
	//{
	//	printf("串口初始化失败");
	//	return FALSE;
	//}

	int   iSectionCount = gsm.GetMsgSectionCount(content);
	for(int i=1; i<=iSectionCount; i++ )
	{
		BOOL b = gsm.SendLongMsg( &pSm, (BYTE)i, (BYTE)iSectionCount );
		if( b == false )
		{
			printf("发送失败");
			return false;
		}
		else
		{
			printf("发送成功");
		}
	} 
	return true;
}

extern "C" __declspec(dllexport) 
bool TestSendLongMessage()
{
	CString content = "警报 警报警报警警报警报警警报警报警警报警报警警报警报警警报警报警警报警报警pNetTraffic(portName, phoneNumber,content)警报警报警报警报警报警报警报警报警报警报;";
	////m_smsPort.SendMsg("15073150641",content,70);

	SM_param pSm;
	pSm.strText		= content;
	pSm.strNumber	= "13170484066";
	pSm.ucValidTime	= 0; //有效时间5分钟

	CGsm gsm;
	gsm.SetPortName("COM1");

	const TCHAR *port = _T("COM1");
	if( gsm.m_comm.OpenComm(port, CBR_9600,NOPARITY,8, ONESTOPBIT) == FALSE )
	{
		printf("串口初始化失败");
		return FALSE;
	}

	int   iSectionCount = gsm.GetMsgSectionCount(content);

	for(int i=1; i<=iSectionCount; i++ )
	{
		BOOL b = gsm.SendLongMsg( &pSm, (BYTE)i, (BYTE)iSectionCount );
		if( b == FALSE )
		{
			printf("发送失败");
			gsm.m_comm.CloseComm();
			return FALSE;
		}
		else
		{
			printf("发送成功");
		}
	} 
	gsm.m_comm.CloseComm();
	return TRUE;
}

