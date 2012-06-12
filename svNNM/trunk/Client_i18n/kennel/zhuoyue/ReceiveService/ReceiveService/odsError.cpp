//////////////////////////////////////////////////////////////
//
// odsError.cpp
//
//		和错误处理相关的函数
//
// by Wang Yong Gang, 1999-10-15
// 在 张军 1998/05/06 版本基础上完成
//
//////////////////////////////////////////////////////////////

#include "StdAfx.h"
#include "odsError.h"

//-------------------------------------------------------------
//	用缺省的语言取得系统错误码对应的错误信息( 格式 1 )
//-------------------------------------------------------------
void odsGetSysErrMsg( DWORD dwErrCode, CString& strMsg )
{
	LANGID dwDefaultLanguageID;		//系统缺省的语言ID
	TCHAR strBuf[ LEN_ERR_MSG + 1 ];
	memset( strBuf, 0, sizeof(TCHAR) * ( LEN_ERR_MSG + 1 ) );

	dwDefaultLanguageID = GetSystemDefaultLangID();
	
	DWORD dwReturnCode = FormatMessage
				(
				 FORMAT_MESSAGE_FROM_SYSTEM |
				 FORMAT_MESSAGE_MAX_WIDTH_MASK,	//Flags
				 NULL,							//No message source
				 dwErrCode,						//Message ID
				 dwDefaultLanguageID,			//Language ID
				 strBuf,						//Pointer of message buffer
				 LEN_ERR_MSG,					//Message buffer size
				 NULL							//No arguments
				);

	if( dwReturnCode == 0 )
		strMsg = _T("取得系统错误信息失败，或没有相应的错误信息!");
	else
		strMsg = strBuf;
}

//-------------------------------------------------------------
//	用缺省的语言取得系统错误码对应的错误信息( 格式 2 )
//-------------------------------------------------------------
void odsGetSysErrMsg( DWORD dwErrCode, LPTSTR strBuf, int lenMsg )
{
	LANGID dwDefaultLanguageID;		//系统缺省的语言ID
	if (strBuf == NULL) 
		return;

	memset( strBuf, 0, lenMsg );

	dwDefaultLanguageID = GetSystemDefaultLangID();
	
	DWORD dwReturnCode = FormatMessage
				(
				 FORMAT_MESSAGE_FROM_SYSTEM |
				 FORMAT_MESSAGE_MAX_WIDTH_MASK,	//Flags
				 NULL,							//No message source
				 dwErrCode,						//Message ID
				 dwDefaultLanguageID,			//Language ID
				 strBuf,						//Pointer of message buffer
				 lenMsg,						//Message buffer size
				 NULL							//No arguments
				);

	if( dwReturnCode == 0 )
		lstrcpyn( strBuf, _T("取得系统错误信息失败，或没有相应的错误信息!"), lenMsg );	
}

//-------------------------------------------------------------
// 在错误信息中填充错误代码和错误信息，strMsg为NULL时只填充错误代码
//-------------------------------------------------------------
void odsMakeError( STODSERROR* pstError, DWORD nCode, LPCTSTR strMsg )
{
	if (pstError == NULL)
		return;
	pstError->nCode = nCode;
	if (strMsg != NULL)
		lstrcpyn( pstError->strMsg, strMsg, LEN_ERR_MSG );
}

//-------------------------------------------------------------
// 按指定的方式，用错误信息来填充错误信息结构
//
// uFlag 可以取的值有：
//		FORMAT_WITH_SYSTEM						- 取系统错误信息
//												- ( pstError->nCode 将被置换成系统错误代码 )
//		FORMAT_WITH_STRING						- 自己生成错误信息
//												- ( pstError->nCode 不变 )
//		FORMAT_WITH_SYSTEM_AND_STRING			- 将系统错误信息和自己生成的错误信息结合起来
//												- ( pstError->nCode 将被置换成系统错误代码 )
// 参数 lpszFormat, ... 的定义同 printf 中的格式串定义
//-------------------------------------------------------------
void odsFormatError( STODSERROR* pstError, UINT uFlag, LPCTSTR lpszFormat, ...)
{ 
	TCHAR lpszTempBuffer[LEN_ERR_MSG + 1];
	TCHAR lpszTempSystemBuffer[LEN_ERR_MSG + 1];
	
	va_list arg_list;

	if (pstError == NULL)
		return;

	// pstError->nCode = 0;
	pstError->strMsg[0] = _T('\0');

	lpszTempBuffer[0] = _T('\0');
	lpszTempSystemBuffer[0] = _T('\0');

	if (uFlag & FORMAT_WITH_STRING)
	{
		if (lpszFormat != NULL)
		{
			va_start(arg_list, lpszFormat);
			_vsntprintf(lpszTempBuffer, LEN_ERR_MSG, lpszFormat, arg_list);
			lpszTempBuffer[LEN_ERR_MSG] = _T('\0');
			va_end(arg_list);
		}
		if (uFlag & FORMAT_WITH_SYSTEM)			
			lstrcat(lpszTempBuffer, _T(":"));
	}

	if (uFlag & FORMAT_WITH_SYSTEM)
	{
		pstError->nCode = GetLastError();
		odsGetSysErrMsg( pstError->nCode, lpszTempSystemBuffer, LEN_ERR_MSG );				
	}

	if ( lstrlen( lpszTempBuffer ) + lstrlen( lpszTempSystemBuffer ) > LEN_ERR_MSG )
		lpszTempSystemBuffer[LEN_ERR_MSG - lstrlen(lpszTempBuffer)] = _T('\0');	
	lstrcat(lpszTempBuffer, lpszTempSystemBuffer);	

	lstrcpyn(pstError->strMsg, lpszTempBuffer, LEN_ERR_MSG);
}
