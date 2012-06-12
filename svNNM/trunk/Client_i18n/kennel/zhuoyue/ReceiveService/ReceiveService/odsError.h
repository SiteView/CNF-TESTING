////////////////////////////////////////////////////////////////////////
//
// odsError.h
//
//		ODS 有关错误处理的定义文件
//
// by Wang Yong Gang, 1999-10-15
//
////////////////////////////////////////////////////////////////////////


#if !defined(_ODS_20_ERROR_H_)
#define _ODS_20_ERROR_H_


#define LEN_ERR_MSG			200

////////////////////////////////////////////////////////////////////////
// 结构定义

// 错误信息结构
typedef struct tabSTODSERROR
{
	DWORD nCode;									// 错误代码
	TCHAR strMsg[LEN_ERR_MSG + 1];					// 错误描述
}STODSERROR;

////////////////////////////////////////////////////////////////////////
// 宏定义

// 错误信息和错误代码
#include "odsErrorCode.h"

// 其他宏定义

#define FORMAT_WITH_SYSTEM					1
#define FORMAT_WITH_STRING					2
#define FORMAT_WITH_SYSTEM_AND_STRING		( FORMAT_WITH_SYSTEM | FORMAT_WITH_STRING )

////////////////////////////////////////////////////////////////////////
// 函数定义

//-------------------------------------------------------------
//	用缺省的语言取得系统错误码对应的错误信息( 格式 1 )
//-------------------------------------------------------------
void odsGetSysErrMsg( DWORD dwErrCode, CString& strMsg );

//-------------------------------------------------------------
//	用缺省的语言取得系统错误码对应的错误信息( 格式 1 )
//-------------------------------------------------------------
void odsGetSysErrMsg( DWORD dwErrCode, LPTSTR strBuf, int lenMsg );

//-------------------------------------------------------------
// 在错误信息中填充错误代码和错误信息，strMsg为NULL时只填充错误代码
//-------------------------------------------------------------
void odsMakeError( STODSERROR* pstError, DWORD nCode, LPCTSTR strMsg );

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
void odsFormatError( STODSERROR* pstError, UINT uFlag, LPCTSTR lpszFormat, ...);

#endif // _ODS_20_ERROR_H_