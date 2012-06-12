////////////////////////////////////////////////////////////////////////
//
// odsFileSystem.h
//
//		ODS 有关文件系统操作的定义文件
//
// by Wang Yong Gang, 1999-10-16
//
////////////////////////////////////////////////////////////////////////


#if !defined(_ODS_20_FILESYSTEM_H_)
#define _ODS_20_FILESYSTEM_H_

#include "odsError.h"

////////////////////////////////////////////////////////////////////////
// 类型定义

//用于 odsBrowseDirectory 的回调函数定义
typedef BOOL (*FILEBROWSEFUNC) (LPCTSTR lpszFileName, STODSERROR* pstError );

//目录属性结构定义
typedef struct tagSTDIRPROPERTY
{
	 DWORD		dwDirNum;			//包含子目录的个数
	 DWORD		dwFileNum;			//包含文件的个数
	 __int64	i64Size;			//目录的大小，以字节为单位
}STDIRPROPERTY;


////////////////////////////////////////////////////////////////////////
// 函数说明

//------------------------------------------------------------
// 删除路径名最后的反斜线(格式1)
//------------------------------------------------------------
void odsRemoveLastSlash( LPTSTR lpszDirectory );

//------------------------------------------------------------
// 删除路径名最后的反斜线(格式2)
//------------------------------------------------------------
void odsRemoveLastSlash( CString& strDirectory );

//------------------------------------------------------------
// 判断由路径名和文件名合成的全称文件名长度是否
// 超过了系统允许的最大长度
//------------------------------------------------------------
BOOL odsIsFullNameOverflow( LPCTSTR lpszDirectoryName, LPCTSTR lpszFileName );

//------------------------------------------------------------
// 从文件全路径名中分离出不包含路径的文件名
//------------------------------------------------------------ 
void odsGetFileNameFromPath( LPCTSTR lpszFileFullPath, LPTSTR lpszFileName );

//------------------------------------------------------------
// 判断两个目录是否在同一个驱动器上
//------------------------------------------------------------
BOOL odsIsDirInSameDriver( LPCTSTR lpszDirectory1, LPCTSTR lpszDirectory2 );

//------------------------------------------------------------
// 取得指定路径的根目录
//------------------------------------------------------------
void odsGetRootDir( LPCTSTR lpszDirectoryName, LPTSTR lpszRootDirectory );

//------------------------------------------------------------
// 判断指定的文件是否存在
//------------------------------------------------------------
BOOL odsIsFileExist( LPCTSTR lpszFileName );

//------------------------------------------------------------
// 判断指定的目录是否存在
//------------------------------------------------------------
BOOL odsIsDirExist( LPCTSTR lpszDirectoryName );

//------------------------------------------------------------
// 取指定文件的大小( 返回 0xFFFFFFFF 表示失败 )，
// lpFileSizeHigh 可以为 NULL
//------------------------------------------------------------
DWORD odsGetFileSize( LPCTSTR lpszFileName, LPDWORD lpFileSizeHigh );

//------------------------------------------------------------
// 达到目录所在的硬盘剩余空间的大小，失败则返回负数
//------------------------------------------------------------
__int64 odsGetDiskFreeSpace( LPCTSTR strDir );

//------------------------------------------------------------
// 将目录中的内容从原始目录拷贝到目标目录，且可包含子目录
//------------------------------------------------------------
BOOL odsCopyDirectory
	( 
		LPCTSTR		lpszSourceDirectory,			// 原始目录
		LPCTSTR		lpszDestinationDirectory,		// 目的目录
		BOOL		bIncludeSubDir,					// 是否包含子目录
		BOOL		bOverwriteExist,				// 是否覆盖已有的目录及文件
		STODSERROR* pstError						// 返回错误信息
	);

//------------------------------------------------------------
// 删除指定的目录
//	bIncludeSubDir = TRUE,  删除指定的目录及其所有子目录（包括所有文件）
//	bIncludeSubDir = FALSE, 仅删除指定目录中的文件，不删除子目录和子目录
//							内的文件，也不删除指定目录本身。
//------------------------------------------------------------
BOOL odsRemoveDirectory( LPCTSTR lpszDirectoryName, BOOL bIncludeSubDir, STODSERROR* pstError );

//------------------------------------------------------------
// 移动目录 - bDirect 指明是直接移动还是先复制再删除
//------------------------------------------------------------
BOOL odsMoveDirectory
		( 
			LPCTSTR lpszSourceDirectory, 
			LPCTSTR lpszDestinationDirectory, 
			BOOL bDirect,
			STODSERROR* pstError 
		);

//------------------------------------------------------------ 
// 设置目录内所有文件的属性
//	bIncludeSubDir			是否包含子目录内的文件
//------------------------------------------------------------ 
BOOL odsSetFileAttributesInDir( LPCTSTR lpszDirectoryName, DWORD dwFileAttributes, BOOL bIncludeSubDir, STODSERROR* pstError );

//------------------------------------------------------------ 
// 取得指定目录的属性
//		包括此目录包含的子目录的个数，文件的个数，以及目录以字节为单位的大小
//------------------------------------------------------------ 
BOOL odsGetDirProperty( LPCTSTR lpszDirectoryName, BOOL bIncludeSubDir, 
					 STDIRPROPERTY* lpDirectoryProperty, STODSERROR* pstError );

//------------------------------------------------------------
// 浏览目录及其子目录中指定类型的文件
// 参数说明:
// 	lpszDirectoryName	要浏览的目录名称
//	dwFileType			要浏览文件的类型，可为以下值或它们的组合
//							FILE_ATTRIBUTE_ARCHIVE 
//							FILE_ATTRIBUTE_COMPRESSED 
//							FILE_ATTRIBUTE_DIRECTORY 
//							FILE_ATTRIBUTE_HIDDEN 
//							FILE_ATTRIBUTE_NORMAL 
//							FILE_ATTRIBUTE_OFFLINE 
//							FILE_ATTRIBUTE_READONLY 
//							FILE_ATTRIBUTE_SYSTEM 
//							FILE_ATTRIBUTE_TEMPORARY 
//	bIncludeSubDir		是否包含子目录中文件的标志
//							TRUE	包含子目录中的文件
//							FALSE	不包含子目录中的文件
//	FileBrowseFunc		回调函数的指针，在浏览过程中，浏览函数会将找到符合条件的
//						文件名传给此函数，此回调函数可做相应的操作，不可以为 NULL
//	pstError			错误信息结构指针，可以为 NULL
//------------------------------------------------------------ 
BOOL odsBrowseDirectory
		(
			LPCTSTR lpszDirectoryName, 
			DWORD dwFileType, 
			BOOL bIncludeSubDir, 
			FILEBROWSEFUNC FileBrowseFunc, 
			STODSERROR* pstError
		);


#endif // _ODS_20_FILESYSTEM_H_