// Gsm.h: interface for the CGsm class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_GSM_H__4655784A_96A6_4315_95AC_F05978428932__INCLUDED_)
#define AFX_GSM_H__4655784A_96A6_4315_95AC_F05978428932__INCLUDED_

#include "comm.h"	// Added by ClassView

#pragma once


#define GSM_7BIT        0
#define GSM_8BIT        4
#define GSM_UCS2        8

struct SM_param
{
//	int		iCodeType;	// 0: auto; 1: unicode; 2: 7bit
	BYTE	ucValidTime;
	CString	strNumber;
    CString	strText;
	CString strSMSC;
};

//struct Phone_info
//{
//	CString strPort;
//	CString strCorp;
//	CString strModel;
//	BOOL	bSupportPDU();
//};

class CGsm  
{
public:
	BOOL SendLongMsg(SM_param *pSm, BYTE byIndex, BYTE byMax);
	int GetSimCardFreeSpaceInfo( );
	int WriteLongMsg(SM_param *pSm, BYTE byIndex, BYTE byMax);
	BYTE GetMsgSectionCount( CString strMsg );

	void ResetMsgID();
	int  GetMsgID() { return m_iMsgID; };
	BOOL SendMsg(SM_param *pSm);
	void ErrorMsg( int iErrorNo );
	BOOL GetPhoneModel( CString &str );
	BOOL GetPhoneCorp( CString &str );
	BOOL DeleteCardMsg( int iIndex );
	BOOL SendCardMsg( int iIndex );
	int WriteMsg( SM_param *pSm );
	BOOL IfSupportPdu();
	int FreeCommand( CString strCommand, CString &strAns, int iWaitingTime = 5, CString strKeySuccess = _T( "OK" ), CString strKeyFailure = _T( "ERROR" ) );
	int CommandAT();
	void SetPortName( CString strPort ) { m_comm.SetPortName( strPort ); };
	CGsm();
	virtual ~CGsm();
	CComm m_comm;

enum{
		RETURN_SUCCESS		= 1,
		RETURN_FAILURE		= 0,
		RETURN_PORTERROR	= -1,
		RETURN_TIMEOUT		= -2,
		RETURN_OTHERERROR	= -3
	};


protected:
	int EncodePdu(struct SM_param *pSm, char *pDst, BYTE byIndex, BYTE byMax );
	char GetDCS( CString strMsg );
	CString GetMsgSection( CString strMsg, int iIndex );
	int Encode7bit(const char* pSrc, unsigned char* pDst, int nSrcLength);
	int InvertNumbers(CString strNum, char* pDst);
	int MakeLongMsgHead( BYTE *pBuf, char cDCS, UINT uiID, BYTE byIndex, BYTE byMax = 0 );
	int EncodeTPUD(CString strSrc, char cDCS, BYTE *pDst, int &iStrLength, BYTE byIndex = 1, BYTE byMax = 1 );
	char EncodeText(CString strSrc, int iCodeType, BYTE *pDst, int &iCodeLength, int &iStrLength );
	int EncodePdu( struct SM_param *pSm, char *pDst );
	int Bytes2String(const unsigned char* pSrc, char* pDst, int nSrcLength);

	int m_iMsgID;
};

#endif // !defined(AFX_GSM_H__4655784A_96A6_4315_95AC_F05978428932__INCLUDED_)
