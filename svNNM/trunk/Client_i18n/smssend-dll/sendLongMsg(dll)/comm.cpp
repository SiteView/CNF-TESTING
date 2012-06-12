//comm.cpp
#include "stdafx.h"
#include "comm.h"

CComm::CComm()
{
	m_strPort = _T("COM1");
	m_strRate = _T("38400");
}

CComm::~CComm()
{

}

BOOL CComm::OpenComm(int nParity, int nByteSize, int nStopBits)
{
	int nBaudRate;
	swscanf( m_strRate.GetBuffer(m_strRate.GetLength()), _T("%d"),&nBaudRate);

	BOOL b = OpenComm(m_strPort.GetBuffer(m_strPort.GetLength()),nBaudRate,nParity,nByteSize,nStopBits);

	m_strPort.ReleaseBuffer();
	
	return b;
}
// open port
// pPort: name，"COM1" or "\\.\COM1"
// nBaudRate: 
// nParity: 
// nByteSize: 
// nStopBits: 
BOOL CComm::OpenComm(const TCHAR* pszPortName, int nBaudRate, int nParity, int nByteSize, int nStopBits )
{
    DCB dcb;        // 串口控制块
    COMMTIMEOUTS timeouts = {    // 串口超时控制参数
        MAXDWORD,        // 读字符间隔超时时间: 100 ms
        0,          // 读操作时每字符的时间: 1 ms (n个字符总共为n ms)
        0,        // 基本的(额外的)读超时时间: 500 ms
        1,          // 写操作时每字符的时间: 1 ms (n个字符总共为n ms)
        200};       // 基本的(额外的)写超时时间: 100 ms
    
//	::EnterCriticalSection(&m_csComm);
	CString tempPort;
	tempPort.Format(_T("\\\\.\\%s"),pszPortName);
	printf("tempPort=%s\n",tempPort.GetBuffer(tempPort.GetLength()));
    m_hComm = CreateFile(tempPort,    // 串口名称或设备路径
						GENERIC_READ | GENERIC_WRITE,		// 读写方式
					    0,									// 共享方式：独占
						NULL,								// 默认的安全描述符
						OPEN_EXISTING,						// 创建方式
						0,									// 不需设置文件属性
						NULL);								// 不需参照模板文件
    
    if(m_hComm == INVALID_HANDLE_VALUE) return FALSE;        // 打开串口失败
    
    GetCommState(m_hComm, &dcb);        // 取DCB
    
    dcb.BaudRate = nBaudRate;
    dcb.ByteSize = nByteSize;
    dcb.Parity   = nParity;
    dcb.StopBits = (int)nStopBits;
	dcb.XonLim = 0;
	dcb.XoffLim = 0;
	dcb.XonChar = (char) 11;
	dcb.XoffChar= (char) 13;
    
    if(SetCommState(m_hComm, &dcb)==0)
	{
		return FALSE;	// 设置DCB
	}        
    if(SetupComm(m_hComm, 8192, 8192)==0)
	{
		return FALSE;	// 设置输入输出缓冲区大小
	}
    if(SetCommTimeouts(m_hComm, &timeouts)==0)
	{
		return FALSE;	// 设置超时
	}
	if(PurgeComm(m_hComm,PURGE_TXABORT|PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR)==0)
	{					//冲干净
		return FALSE;
	}
//	::LeaveCriticalSection(&m_csComm);
	    
    return TRUE;
}
    
// 关闭串口
BOOL CComm::CloseComm()
{
	BOOL b = CloseHandle(m_hComm);
	return b;
}

// 写串口
// pData: 待写的数据缓冲区指针
// nLength: 待写的数据长度
void CComm::WriteComm(void* pData, int nLength)
{
    DWORD dwNumWrite;    // 串口发出的数据长度
    
    WriteFile(m_hComm, pData, (DWORD)nLength, &dwNumWrite, NULL);
}
    
// 读串口
// pData: 待读的数据缓冲区指针
// nLength: 待读的最大数据长度
// 返回: 实际读入的数据长度
int CComm::ReadComm(void* pData, int nLength)
{
    DWORD dwNumRead;    // 串口收到的数据长度

    if( ReadFile(m_hComm, pData, (DWORD)nLength, &dwNumRead, NULL) == FALSE )
		return 0;

    return (int)dwNumRead;
}

void CComm::SetPortName(CString strPortName)
{
	this->m_strPort = strPortName;
}

void CComm::SetRate(CString strRate)
{
	this->m_strRate = strRate;
}

void CComm::ClearPort(BOOL bInBuffer)
{
	if( bInBuffer )
		PurgeComm(m_hComm,PURGE_RXCLEAR);
	else
		PurgeComm(m_hComm,PURGE_TXCLEAR);

}

CString CComm::GetPortName()
{
	return m_strPort;
}

//int CComm::ListPorts(CComboBox &combox)
//{
//	int j = 0;
//	for( int i = 1; i <= 256; i++ )
//	{
//		CString strPort;
//		strPort.Format( _T("COM%d"), i );
//		TCHAR wszPath[MAX_PATH];
//		if( ::QueryDosDevice( strPort, wszPath, MAX_PATH ) )
//		{
//			combox.InsertString( j++, strPort );
//		}
//	}
//	return j;
//}

void CComm::WriteUnicodeString(CString str)
{

    char *psz = NULL;
	psz = new char[ str.GetLength() + 2 ];
	if( psz == NULL )
		return;

	int iLength = ::WideCharToMultiByte( CP_ACP, 0, str, str.GetLength(),
								(char*)psz, str.GetLength(), NULL, NULL );

	DWORD dwNumWrite;    // 串口发出的数据长度

	WriteFile(m_hComm, psz, (DWORD)iLength, &dwNumWrite, NULL);

	delete psz;
	psz = NULL;
}
