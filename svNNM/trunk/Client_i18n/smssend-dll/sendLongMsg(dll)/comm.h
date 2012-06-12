//comm.h
#include <atlstr.h>

#pragma once

class CComm
{
public:
	CComm();
	~CComm();
public:
	CString m_strRate;
	CString m_strPort;
	

public:
	void WriteUnicodeString( CString str );
	//int ListPorts( CComboBox& combox );
	CString GetPortName();
	void ClearPort(BOOL bInBuffer = TRUE);
	void SetRate(CString strRate);
	void SetPortName(CString strPortName);
	BOOL OpenComm(int nParity=0, int nByteSize=8, int nStopBits=ONESTOPBIT);
	BOOL OpenComm(const TCHAR* pszPortName, int nBaudRate, int nParity=0, int nByteSize=8, int nStopBits=ONESTOPBIT);
	BOOL CloseComm();
	void WriteComm(void* pData, int nLength);
	int  ReadComm(void* pData, int nLength);

private:
	HANDLE m_hComm;
//	CRITICAL_SECTION m_csComm;		// COMM¡ŸΩÁ∂Œ

	};