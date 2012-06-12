// Gsm.cpp: implementation of the CGsm class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"

#include "Gsm.h"
#include "time.h"
#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
//#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CGsm::CGsm()
{
	m_comm.SetPortName( _T( "COM3" ) );
	ResetMsgID();
}

CGsm::~CGsm()
{

}

int CGsm::CommandAT()
{
	CString strCommand, strAns;
	strCommand = _T( "AT\r" );
	int iSuccess = FreeCommand( strCommand, strAns, 2 );
	if( iSuccess == FALSE )
		iSuccess = FreeCommand( strCommand, strAns, 2 );
	return iSuccess;
}

//strCommand:		in unicode;
//strAns:			in unicode;
//strKeySuccess:	in unicode;
//strKeyFailure:	in unicode;
int CGsm::FreeCommand(CString strCommand, CString &strAns, int iWaitingTime, CString strKeySuccess, CString strKeyFailure)
{

	char *psz = NULL;
	psz = new char[ strCommand.GetLength() + 2 ];
	if( psz == NULL )
		return RETURN_OTHERERROR;

	int iLength = ::WideCharToMultiByte( CP_ACP, 0, strCommand, strCommand.GetLength(),
		(char*)psz, strCommand.GetLength(), NULL, NULL );

	if( m_comm.OpenComm( ) == FALSE )
	{
		delete psz;
		psz = NULL;
		return RETURN_PORTERROR;
	}
	m_comm.WriteComm( psz, iLength );

	// read data
	int iTimeOut = iWaitingTime * 10;	// 值为0时表示超时。
	const BUFFER_LENGTH = 1000;
	char buffer[BUFFER_LENGTH];
	ZeroMemory( buffer, BUFFER_LENGTH );
	int iIndex = 0;
	while( strAns.Find( strKeySuccess ) < 0 && strAns.Find( strKeyFailure ) < 0 )
	{
		// read;
		BYTE byte;

		iTimeOut = iWaitingTime * 10;	// 值为0时表示超时。
		while( m_comm.ReadComm( &byte, 1 ) == FALSE && iTimeOut > 0 )
		{
			iTimeOut --;
			::Sleep( 100 );
		}

		if( iTimeOut <= 0 )
		{
			break;
		}

		if( iIndex < BUFFER_LENGTH - 1 )
		{
			buffer[ iIndex++ ] = byte;
		}
		WCHAR wchar[ BUFFER_LENGTH * 2 ];
		::MultiByteToWideChar(CP_ACP, 0, buffer, -1, wchar, BUFFER_LENGTH * 2);
		strAns = wchar;
	}
	m_comm.ClearPort( );
	m_comm.CloseComm();
	delete psz;
	psz = NULL;


	if( iTimeOut <= 0 )
	{
		return RETURN_TIMEOUT;
	}
	if( strAns.Find( strKeySuccess ) >= 0 )
	{
		return RETURN_SUCCESS;
	}
	if( strAns.Find( strKeyFailure ) >= 0 )
	{
		return RETURN_FAILURE;
	}
	return RETURN_OTHERERROR;
}

BOOL CGsm::IfSupportPdu()
{
	CString strCommand, strAns;
	strCommand = _T( "AT+CMGF=0\r" );
	BOOL bSuccess = FreeCommand( strCommand, strAns, 1 );
	return bSuccess;
}

// PDU编码，用于编制、发送短消息
// sm: 源PDU参数指针
// pDst: 目标PDU串指针
// 返回: 目标PDU串长度
int CGsm::EncodePdu(struct SM_param *pSm, char *pDst)
{
	if( pSm == NULL )
	{
		return 0;
	}
    int nLength = 0;			// 内部用的串长度
    int nDstLength = 0;			// 目标PDU串长度
    BYTE buf[256];				// 内部用的缓冲区

    // SMSC地址信息段
    nLength = pSm->strSMSC.GetLength();    // SMSC地址字符串的长度
    if(nLength>0)
	{
		buf[0] = (char)((nLength & 1) == 0 ? nLength : nLength + 1) / 2 + 1;    // SMSC地址信息长度
		buf[1] = 0x91;        // 固定: 用国际格式号码
		nDstLength = Bytes2String(buf, pDst, 2);        // 转换2个字节到目标PDU串
		nDstLength += InvertNumbers(pSm->strSMSC, &pDst[nDstLength] );    // 转换SMSC到目标PDU串
	}
	else
	{
		buf[0]=0;
		nDstLength = Bytes2String(buf, pDst, 1);        // 转换2个字节到目标PDU串
	}

	// TPDU段基本参数、目标地址等
	nLength = pSm->strNumber.GetLength();				// TP-DA地址字符串的长度
	buf[0] = 0x11;										// 是发送短信(TP-MTI=01)，TP-VP用相对格式(TP-VPF=10)
	buf[1] = 0;											// TP-MR=0
	buf[2] = (char)nLength;								// 目标地址数字个数(TP-DA地址字符串真实长度)
	buf[3] = 0x81;										// 固定: 不用国际格式号码
	nDstLength += Bytes2String( buf, &pDst[nDstLength], 4 );			// 转换4个字节到目标PDU串
	nDstLength += InvertNumbers( pSm->strNumber, &pDst[nDstLength] );	// 转换TP-DA到目标PDU串

//    // TPDU段协议标识、编码方式、用户信息等
	int iCodeLength = 0;
	int iStrLength  = 0;
	buf[0] = 0;        // 协议标识(TP-PID)
	buf[1] = EncodeText(pSm->strText, GetDCS( pSm->strText ), &buf[4], iCodeLength, iStrLength );        // 用户信息编码方式(TP-DCS)
	buf[2] = pSm->ucValidTime;            // 有效期(TP-VP)
	buf[3] = iStrLength;

	nDstLength += Bytes2String(buf, &pDst[nDstLength], iCodeLength + 4 );        // 转换该段数据到目标PDU串
    
	return nDstLength;
}

// 字节数据转换为可打印字符串
// 如：{0xC8, 0x32, 0x9B, 0xFD, 0x0E, 0x01} --> "C8329BFD0E01" 
// pSrc: 源数据指针
// pDst: 目标字符串指针
// nSrcLength: 源数据长度
// 返回: 目标字符串长度
int CGsm::Bytes2String(const unsigned char *pSrc, char *pDst, int nSrcLength)
{
	if( pSrc == NULL || pDst == NULL )
	{
		return 0;
	}

	CString strHex, strTemp;
	for( int i = 0; i < nSrcLength; i++ )
	{
		strTemp.Format( _T("%.2X"), (unsigned char)pSrc[ i ] );
		strHex += strTemp;
	}

	int iLength = ::WideCharToMultiByte( CP_ACP, 0, strHex, strHex.GetLength(),
								(char*)pDst, strHex.GetLength() + 1, NULL, NULL );
	return iLength;
}

// 正常顺序的字符串转换为两两颠倒的字符串，若长度为奇数，补'F'凑成偶数
// 如："8613851872468" --> "683158812764F8"
// strNum: 源字符串
// pDst: 目标字符串指针
// 返回: 目标字符串长度
int CGsm::InvertNumbers(CString strNum, char *pDst)
{
	if( pDst == NULL )
	{
		return 0;
	}

    char ch;          // 用于保存一个字符

	if( strNum.GetLength() & 1 )
	{
		strNum += _T( "F" );
	}
    
    // 两两颠倒
    for(int i=0; i< strNum.GetLength() ;i+=2)
    {
        ch		= (char)strNum[i];		// 保存先出现的字符
        *pDst++ = (char)strNum[i+1];	// 复制后出现的字符
        *pDst++ = ch;					// 复制先出现的字符
    }
    
    // 输出字符串加个结束符
    *pDst = '\0';
    
    // 返回目标字符串长度
    return strNum.GetLength();
}

//#define GSM_7BIT        0
//#define GSM_8BIT        4
//#define GSM_UCS2        8
//return: 用户信息编码方式(TP-DCS)
char CGsm::EncodeText(CString strSrc, int iCodeType, BYTE *pDst, int &iCodeLength, int &iStrLength )
{
	if( pDst == NULL )
		return 0;

	iCodeLength = 0;
	iStrLength  = 0;
	char cTP_DCS = GSM_7BIT;
	switch( iCodeType )
	{ // 1: unicode; 2: 7bit
	case 1:
		cTP_DCS = GSM_UCS2;
		break;
	case 2:
		cTP_DCS = GSM_7BIT;
		break;
	default:
		// auto
		break;
	}

	for( int i = 0; i < strSrc.GetLength(); i ++ )
	{
		TCHAR tc = strSrc.GetAt( i );
		if( tc >= 128 || tc < 0 )
		{
			cTP_DCS = GSM_UCS2;

			if( strSrc.GetLength() > 70 )
			{
				strSrc = strSrc.Left( 70 );
			}
			break;
		}
	}

	if( cTP_DCS == GSM_UCS2 )
	{
		for( int i = 0; i < strSrc.GetLength(); i ++ )
		{
			USHORT tc = strSrc.GetAt( i );
			BYTE byHi = HIBYTE( tc );
			BYTE byLo = LOBYTE( tc );
			
			*(pDst++) = byHi;
			*(pDst++) = byLo;

		}
		iCodeLength = strSrc.GetLength() * 2;
		iStrLength  = iCodeLength;
	}
	else if( cTP_DCS == GSM_7BIT )
	{
		char *pSrc = NULL;
		pSrc = new char[ strSrc.GetLength() + 1 ];
		if( pSrc != NULL )
		{
			ZeroMemory( pSrc, strSrc.GetLength() + 1 );
			int iLength = ::WideCharToMultiByte( CP_ACP, 0, strSrc, strSrc.GetLength(),
										pSrc, strSrc.GetLength() + 1, NULL, NULL );
			iCodeLength = Encode7bit( pSrc, pDst, iLength + 1 );

			delete pSrc;
		}
		iStrLength = strSrc.GetLength();
	}
	return cTP_DCS;
}

//return: if it equals 0, writing is unsuccessful, otherwise it indecade d order
int CGsm::WriteMsg(SM_param *pSm)
{
	if( pSm == NULL )
	{
		return 0;
	}

    int nPduLength;        // PDU串长度
    unsigned char nSmscLength;    // SMSC串长度
    char pdu[512];         // PDU串
	ZeroMemory( pdu, 512 );
	int index=0;				//if it equals 0, writing is unsuccessful, otherwise it indecade d order
	CString strCommand, strAns;

	nPduLength = EncodePdu(pSm, pdu);		// 根据PDU参数，编码PDU串

    nSmscLength = pSm->strSMSC.GetLength();
	if( nSmscLength & 1 )
	{ // 奇数
		nSmscLength ++;
	}
	nSmscLength ++;			// 本身长度

    // 命令中的长度，不包括SMSC信息长度，以数据字节计
	strCommand.Format( _T("AT+CMGW=%d\r"), nPduLength / 2 - nSmscLength );

	if( FreeCommand( strCommand, strAns, 5, _T("\r\n> ") ) != RETURN_SUCCESS )
	{
		return index;
	}

	::MultiByteToWideChar(CP_ACP, 0, pdu , -1, strCommand.GetBuffer( nPduLength * 2 ), nPduLength * 2 );
	strCommand.ReleaseBuffer();

    strCommand += _T("\x01a\r");					// 以Ctrl-Z结束

	if( FreeCommand( strCommand, strAns, 6 ) != RETURN_SUCCESS )
	{
		return index;
	}

    if( strAns.Find( _T("CMGW:") ) >= 0 )
	{
		strAns.Delete( 0, strAns.Find( _T("CMGW:") ) + 5 );
//		while( strAns.Right( 1 ) >= '9' || strAns.Right( 1 ) <= '0' )
//		{
//			strAns.Delete( strAns.GetLength() - 1 );
//		}
		swscanf( strAns, _T("%d"), &index );
	}
	return index;
}

BOOL CGsm::SendCardMsg(int iIndex)
{
	CString strCommand, strAns;
	strCommand.Format( _T("AT+CMSS=%d\r"), iIndex );

	if( FreeCommand( strCommand, strAns, 10 ) == RETURN_SUCCESS )
	{
		return TRUE;
	}
	else
	{
		return FALSE;
	}

}

BOOL CGsm::DeleteCardMsg( int iIndex )
{
	CString strCommand, strAns;
	strCommand.Format( _T("AT+CMGD=%d\r"), iIndex );
	if( FreeCommand( strCommand, strAns, 6 ) == RETURN_SUCCESS )
	{
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}

// 7-bit编码
// pSrc: 源字符串指针
// pDst: 目标编码串指针
// nSrcLength: 源字符串长度
// 返回: 目标编码串长度
int CGsm::Encode7bit(const char *pSrc, unsigned char *pDst, int nSrcLength)
{
    int nSrc;        // 源字符串的计数值
    int nDst;        // 目标编码串的计数值
    int nChar;       // 当前正在处理的组内字符字节的序号，范围是0-7
    unsigned char nLeft;    // 上一字节残余的数据
    
    // 计数值初始化
    nSrc = 0;
    nDst = 0;
    
    // 将源串每8个字节分为一组，压缩成7个字节
    // 循环该处理过程，直至源串被处理完
    // 如果分组不到8字节，也能正确处理
    while(nSrc<nSrcLength)
    {
        // 取源字符串的计数值的最低3位
        nChar = nSrc & 7;
    
        // 处理源串的每个字节
        if(nChar == 0)
        {
            // 组内第一个字节，只是保存起来，待处理下一个字节时使用
            nLeft = *pSrc;
        }
        else
        {
            // 组内其它字节，将其右边部分与残余数据相加，得到一个目标编码字节
            *pDst = (*pSrc << (8-nChar)) | nLeft;
    
            // 将该字节剩下的左边部分，作为残余数据保存起来
            nLeft = *pSrc >> nChar;
            // 修改目标串的指针和计数值 
			pDst++;
            nDst++; 
        } 
        
        // 修改源串的指针和计数值
        pSrc++; nSrc++;
    }
    
    // 返回目标串长度
    return nDst; 
}

BOOL CGsm::GetPhoneCorp(CString &str)
{
	str.Empty();
	BOOL bRe = FALSE;

	CString strCommand, strAns;
	strCommand = _T( "AT+CGMI\r" );

	//从str中去掉+CGMI 和 OK，然后在把所有的回车换行符号都去掉。
	if( FreeCommand( strCommand, strAns, 2 ) == RETURN_SUCCESS )
	{
		CString strKey;
		strKey = _T("CGMI");
		if( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( 0, strAns.Find( strKey ) + strKey.GetLength() );
		}

		strKey = _T("OK");
		if( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		strKey = _T("\r");
		while( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		strKey = _T("\n");
		while( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		str = strAns;
		bRe = TRUE;
	}

	return bRe;
}

BOOL CGsm::GetPhoneModel(CString &str)
{
	str.Empty();
	BOOL bRe = FALSE;

	CString strCommand, strAns;
	strCommand = _T( "AT+CGMM\r" );

	//从str中去掉+CGMI 和 OK，然后在把所有的回车换行符号都去掉。
	if( FreeCommand( strCommand, strAns, 2 ) == RETURN_SUCCESS )
	{
		CString strKey;
		strKey = _T("CGMM");
		if( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( 0, strAns.Find( strKey ) + strKey.GetLength() );
		}

		strKey = _T("OK");
		if( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		strKey = _T("\r");
		while( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		strKey = _T("\n");
		while( strAns.Find( strKey ) >= 0 )
		{
			strAns.Delete( strAns.Find( strKey ), strKey.GetLength() );
		}

		str = strAns;
		bRe = TRUE;
	}

	return bRe;
}



//void CGsm::ErrorMsg(int iErrorNo)
//{
//	switch( iErrorNo )
//	{
//	case CGsm::RETURN_SUCCESS:
//		break;
//	case CGsm::RETURN_FAILURE:
//		::AfxMessageBox( _T("未知设备！") );
//		break;
//	case CGsm::RETURN_PORTERROR:
//		::AfxMessageBox( _T("打开端口失败！") );
//		break;
//	case CGsm::RETURN_TIMEOUT:
//		::AfxMessageBox( _T("通讯超时！") );
//		break;
//	default:
//		::AfxMessageBox( _T("未知错误！") );
//		break;
//	}
//}

BOOL CGsm::SendMsg(SM_param *pSm)
{
	if( pSm == NULL )
	{
		return FALSE;
	}

    int nPduLength;        // PDU串长度
    unsigned char nSmscLength;    // SMSC串长度
    char pdu[512];         // PDU串
	ZeroMemory( pdu, 512 );
	CString strCommand, strAns;

	nPduLength = EncodePdu(pSm, pdu);		// 根据PDU参数，编码PDU串

    nSmscLength = pSm->strSMSC.GetLength();
	if( nSmscLength & 1 )
	{ // 奇数
		nSmscLength ++;
	}
	nSmscLength ++;			// 本身长度

    // 命令中的长度，不包括SMSC信息长度，以数据字节计
	strCommand.Format( _T("AT+CMGS=%d\r"), nPduLength / 2 - nSmscLength );

	if( FreeCommand( strCommand, strAns, 5, _T("\r\n> ") ) != RETURN_SUCCESS )
	{
		return FALSE;
	}

	::MultiByteToWideChar(CP_ACP, 0, pdu , -1, strCommand.GetBuffer( nPduLength * 2 ), nPduLength * 2 );
	strCommand.ReleaseBuffer();

    strCommand += _T("\x01a\r");					// 以Ctrl-Z结束

	if( FreeCommand( strCommand, strAns, 6 ) != RETURN_SUCCESS )
	{
		return FALSE;
	}
	else
	{
		return TRUE;	
	}
}

void CGsm::ResetMsgID()
{
	srand( (unsigned)time( NULL ) );
	m_iMsgID = rand();
}

BYTE CGsm::GetMsgSectionCount(CString strMsg)
{
	int iSingleLength  = 70;
	int iSectionLength = iSingleLength - 3;
	switch( GetDCS( strMsg ) )
	{
	case GSM_7BIT:
		iSingleLength  = 160;
		iSectionLength = iSingleLength - 7;
		break;
	case GSM_UCS2:
		iSingleLength  = 70;
		iSectionLength = iSingleLength - 3;
		break;
	default:
		break;
	}

	if( strMsg.GetLength() <= iSingleLength )
	{
		return 1;
	}
	else
	{
		return BYTE( ( strMsg.GetLength() + iSectionLength - 1 ) / iSectionLength );
	}
}

//return: 用户信息编码方式(TP-DCS)
char CGsm::GetDCS(CString strMsg)
{
	char cTP_DCS = GSM_7BIT;

	for( int i = 0; i < strMsg.GetLength(); i ++ )
	{
		TCHAR tc = strMsg.GetAt( i );
		if( tc >= 128 || tc < 0 )
		{
			cTP_DCS = GSM_UCS2;
			break;
		}
	}
	
	return cTP_DCS;
}

CString CGsm::GetMsgSection( CString strMsg, int iIndex )
{
	CString strSec;

	if( iIndex <= 0 || iIndex > GetMsgSectionCount( strMsg ) )
	{
		return strSec;
	}

	int iSingleLength  = 70;
	int iSectionLength = iSingleLength - 3;
	switch( GetDCS( strMsg ) )
	{
	case GSM_7BIT:
		iSingleLength  = 160;
		iSectionLength = iSingleLength - 7;
		break;
	case GSM_UCS2:
		iSingleLength  = 70;
		iSectionLength = iSingleLength - 3;
		break;
	default:
		break;
	}

	if( strMsg.GetLength() <= iSingleLength )
	{
		return strSec;
	}

	strSec = strMsg.Mid( ( iIndex - 1 ) * iSectionLength, iSectionLength );

	return strSec;

}

// return: HeadLength
int CGsm::MakeLongMsgHead(BYTE *pBuf, char cDCS, UINT uiID, BYTE byIndex, BYTE byMax)
{
	int iLength = 0;
	if( pBuf == NULL )
	{
		return iLength;
	}
	if( byMax == 1 )
	{
		return iLength;
	}

	iLength ++;
	if( cDCS == GSM_7BIT )
	{
		pBuf[ iLength++ ] = 0x00;
		pBuf[ iLength++ ] = 0x03;
		pBuf[ iLength++ ] = (BYTE)uiID;
	}
	else if( cDCS == GSM_UCS2 )
	{
//		pBuf[ iLength++ ] = 0x08;
//		pBuf[ iLength++ ] = 0x04;
		pBuf[ iLength++ ] = 0x00;
		pBuf[ iLength++ ] = 0x03;
		pBuf[ iLength++ ] = (BYTE)uiID;
//		*( USHORT *)(&pBuf[ iLength ]) = uiID;
//		iLength += 2;
	}
	else
	{
		return 0;
	}

	pBuf[ iLength++ ] = byMax;
	pBuf[ iLength++ ] = byIndex;
	pBuf[ 0 ] = iLength - 1;
	return iLength;
}

int CGsm::WriteLongMsg(SM_param *pSm, BYTE byIndex, BYTE byMax)
{
// 05-05-18 Add start // bug: byMax == 1时，信息内容为空。
	if( byMax == 1 )
	{
		return WriteMsg( pSm );
	}
// 05-05-18 Add End
	if( pSm == NULL )
	{
		return 0;
	}

    int nPduLength;        // PDU串长度
    unsigned char nSmscLength;    // SMSC串长度
    char pdu[512];         // PDU串
	ZeroMemory( pdu, 512 );
	int index=0;				//if it equals 0, writing is unsuccessful, otherwise it indecade d order
	CString strCommand, strAns;

	nPduLength = EncodePdu(pSm, pdu, byIndex, byMax );		// 根据PDU参数，编码PDU串

    nSmscLength = pSm->strSMSC.GetLength();
	if( nSmscLength & 1 )
	{ // 奇数
		nSmscLength ++;
	}
	nSmscLength ++;			// 本身长度

    // 命令中的长度，不包括SMSC信息长度，以数据字节计
	strCommand.Format( _T("AT+CMGW=%d\r"), nPduLength / 2 - nSmscLength );

	if( FreeCommand( strCommand, strAns, 5, _T("\r\n> ") ) != RETURN_SUCCESS )
	{
		return index;
	}

	::MultiByteToWideChar(CP_ACP, 0, pdu , -1, strCommand.GetBuffer( nPduLength * 2 ), nPduLength * 2 );
	strCommand.ReleaseBuffer();

    strCommand += _T("\x01a\r");					// 以Ctrl-Z结束

	if( FreeCommand( strCommand, strAns, 6 ) != RETURN_SUCCESS )
	{
		return index;
	}

    if( strAns.Find( _T("CMGW:") ) >= 0 )
	{
		strAns.Delete( 0, strAns.Find( _T("CMGW:") ) + 5 );
//		while( strAns.Right( 1 ) >= '9' || strAns.Right( 1 ) <= '0' )
//		{
//			strAns.Delete( strAns.GetLength() - 1 );
//		}
		swscanf( strAns, _T("%d"), &index );
	}
	return index;
}

// PDU编码，用于编制、发送短消息
// sm: 源PDU参数指针
// pDst: 目标PDU串指针
// 返回: 目标PDU串长度
int CGsm::EncodePdu(SM_param *pSm, char *pDst, BYTE byIndex, BYTE byMax)
{
	if( pSm == NULL )
	{
		return 0;
	}
    int nLength = 0;			// 内部用的串长度
    int nDstLength = 0;			// 目标PDU串长度
    BYTE buf[256];				// 内部用的缓冲区

    // SMSC地址信息段
    nLength = pSm->strSMSC.GetLength();    // SMSC地址字符串的长度
    if(nLength>0)
	{
		buf[0] = (char)((nLength & 1) == 0 ? nLength : nLength + 1) / 2 + 1;    // SMSC地址信息长度
		buf[1] = 0x91;        // 固定: 用国际格式号码
		nDstLength = Bytes2String(buf, pDst, 2);        // 转换2个字节到目标PDU串
		nDstLength += InvertNumbers(pSm->strSMSC, &pDst[nDstLength] );    // 转换SMSC到目标PDU串
	}
	else
	{
		buf[0]=0;
		nDstLength = Bytes2String(buf, pDst, 1);        // 转换2个字节到目标PDU串
	}

	// TPDU段基本参数、目标地址等
	nLength = pSm->strNumber.GetLength();				// TP-DA地址字符串的长度
//	buf[0] = 0x11;										// 是发送短信(TP-MTI=01)，TP-VP用相对格式(TP-VPF=10)
//	buf[1] = 0;											// TP-MR=0
	buf[0] = 0x51;										// 是发送短信(TP-MTI=**** **01)，TP-VP用相对格式(TP-VPF=***1 0***)
														// 多条信息( TP-MMS = *1** **** )
	buf[1] = 0xFF;										// TP-MR
	buf[2] = (char)nLength;								// 目标地址数字个数(TP-DA地址字符串真实长度)
	buf[3] = 0x81;										// 固定: 不用国际格式号码
	nDstLength += Bytes2String( buf, &pDst[nDstLength], 4 );			// 转换4个字节到目标PDU串
	nDstLength += InvertNumbers( pSm->strNumber, &pDst[nDstLength] );	// 转换TP-DA到目标PDU串

//    // TPDU段协议标识、编码方式、用户信息等
	int iCodeLength = 0;
	int iStrLength  = 0;

//	int iHeadLength = MakeLongMsgHead( &buf[4], GetDCS( pSm->strText ), m_iMsgID, byIndex, byMax );
//	EncodeText( GetMsgSection( pSm->strText, byIndex ), 
//				GetDCS( pSm->strText ), 
//				&buf[ 4 + iHeadLength ], 
//				iCodeLength, 
//				iStrLength );
//	iCodeLength += iHeadLength;
//	iStrLength  += iHeadLength;

	iCodeLength = EncodeTPUD( GetMsgSection( pSm->strText, byIndex ), 
								GetDCS( pSm->strText ), 
								&buf[ 4 ], 
								iStrLength,
								byIndex,
								byMax );

	buf[0] = 0;        // 协议标识(TP-PID)
	buf[1] = GetDCS( pSm->strText );        // 用户信息编码方式(TP-DCS)
	buf[2] = pSm->ucValidTime;            // 有效期(TP-VP)
	buf[3] = iStrLength;
	nDstLength += Bytes2String(buf, &pDst[nDstLength], iCodeLength + 4 );        // 转换该段数据到目标PDU串
    
	return nDstLength;
}

// function: 对文字部分进行编码，包括长信息的标志块
// param:
//		strSrc: 文字内容。小于等于一条短信的长度。如果有多余，则会被过滤掉。
//		cDCS:   编码方式
//		pDst:   目标内存块指针
//		iStrLength: 编码后的字符串理论长度（出口参数）
//		byIndex:    长信息情况下的信息编号
//		byMax:      长信息情况下的最大信息数量
//return:     编码的长度
//逻辑：
//
//
int CGsm::EncodeTPUD(CString strSrc, char cDCS, BYTE *pDst, int &iStrLength, BYTE byIndex, BYTE byMax )
{
	if( pDst == NULL )
		return 0 ;
	iStrLength = 0;
	int iCodeLength = 0;

	if( byMax > 1 )
	{ // 给字符串增加一些冗余数据，编码后，用长信息数据块覆盖掉冗余数据。
		if( cDCS == GSM_7BIT )
		{
			strSrc = _T("0000000") + strSrc;
		}
		else
		{ // GSM_UCS2
			strSrc = _T("000") + strSrc;
		}
	}

	if( byIndex < 1 || byIndex > byMax )
	{
		byIndex = 1;
	}

	EncodeText( strSrc, 
				cDCS, 
				pDst, 
				iCodeLength, 
				iStrLength );

	MakeLongMsgHead( pDst, cDCS, m_iMsgID, byIndex, byMax );
	return iCodeLength;
}

// Return:	<0:	error!
//			other :	Free SMS space.
int CGsm::GetSimCardFreeSpaceInfo()
{
	int iRe = -1;
	int iTotal, iUsed;
	iTotal = iUsed = 0;

	CString strCommand, strAns;
	strCommand = _T( "AT+CPMS=\"SM\"\r" );
	int iCommandSuccess = FALSE;
	for( int i = 0; i < 3 && iCommandSuccess != RETURN_SUCCESS; i ++ )
	{
		iCommandSuccess = FreeCommand( strCommand, strAns, 2 );
	}

	if( iCommandSuccess == RETURN_SUCCESS )
	{
		strAns = strAns.Mid( strAns.Find( _T("+CPMS:") ) + 6 );
		if( strAns.Find( _T("\"SM\"") ) >= 0 )
		{
			strAns = strAns.Mid( strAns.Find( _T("\"SM\"") ) + 4 );
			swscanf( strAns, _T(",%d,%d"), &iUsed, &iTotal );
			iRe = iTotal - iUsed;
		}
	}

	return iRe;
}

BOOL CGsm::SendLongMsg(SM_param *pSm, BYTE byIndex, BYTE byMax)
{
	if( pSm == NULL )
	{
		return FALSE;
	}

	int nPduLength;        // PDU串长度
	unsigned char nSmscLength;    // SMSC串长度
	char pdu[512];         // PDU串
	ZeroMemory( pdu, 512 );
	int index=0;				//if it equals 0, writing is unsuccessful, otherwise it indecade d order
	CString strCommand, strAns;

	nPduLength = EncodePdu(pSm, pdu, byIndex, byMax );		// 根据PDU参数，编码PDU串

	nSmscLength = pSm->strSMSC.GetLength();
	if( nSmscLength & 1 )
	{ // 奇数
		nSmscLength ++;
	}
	nSmscLength ++;			// 本身长度

	// 命令中的长度，不包括SMSC信息长度，以数据字节计
	strCommand.Format( _T("AT+CMGS=%d\r"), nPduLength / 2 - nSmscLength );

	if( FreeCommand( strCommand, strAns, 5, _T("\r\n> ") ) != RETURN_SUCCESS )
	{
		return FALSE;
	}

	::MultiByteToWideChar(CP_ACP, 0, pdu , -1, strCommand.GetBuffer( nPduLength * 2 ), nPduLength * 2 );
	strCommand.ReleaseBuffer();

	strCommand += _T("\x01a\r");					// 以Ctrl-Z结束

	if( FreeCommand( strCommand, strAns, 10 ) == RETURN_SUCCESS )
	{
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}
