//////////////////////////////////////////////////////////////
//
// odsFileSystem.cpp
//
//		和文件系统相关的函数
//
// by Wang Yong Gang, 1999-10-16
// 在 张军 1998/05/20 版本基础上完成
//
//////////////////////////////////////////////////////////////

#include "StdAfx.h"
#include "odsFileSystem.h"

//------------------------------------------------------------
// 删除路径名最后的反斜线(格式1)
//------------------------------------------------------------
void odsRemoveLastSlash( LPTSTR lpszDirectory )
{
	int	i = lstrlen(lpszDirectory);
	if (i == 0)
		return;
 
	// 如果最后一个字符是(\)，则删除它	
	if ( lpszDirectory[i - 1] == _T('\\') )
		lpszDirectory[i - 1] = _T('\0');	
}

//------------------------------------------------------------
// 删除路径名最后的反斜线(格式2)
//------------------------------------------------------------
void odsRemoveLastSlash( CString& strDirectory )
{
	TCHAR* p = strDirectory.GetBuffer(0);
	odsRemoveLastSlash( p );
	strDirectory.ReleaseBuffer();
}

//------------------------------------------------------------
// 判断由路径名和文件名合成的全称文件名长度是否超过了系统允许的最大长度
//------------------------------------------------------------
BOOL odsIsFullNameOverflow( LPCTSTR lpszDirectoryName, LPCTSTR lpszFileName )
{	
	return ( lstrlen(lpszDirectoryName) + lstrlen(lpszFileName) + 1
				> _MAX_PATH ) ? TRUE : FALSE;
}

//------------------------------------------------------------
// 从文件全路径名中分离出不包含路径的文件名
//------------------------------------------------------------ 
void odsGetFileNameFromPath( LPCTSTR lpszFileFullPath, LPTSTR lpszFileName )
{
	TCHAR szFileName[_MAX_PATH];
	TCHAR szExtName[_MAX_PATH];

	//从文件全路径中分离出文件名和扩展名
	_tsplitpath( lpszFileFullPath, NULL, NULL, szFileName, szExtName );
	
	//以文件名和扩展名合成文件名
	wsprintf( lpszFileName, _T("%s%s"), szFileName, szExtName );	
}

//------------------------------------------------------------
// 判断两个目录是否在同一个驱动器上
//------------------------------------------------------------
BOOL odsIsDirInSameDriver( LPCTSTR lpszDirectory1, LPCTSTR lpszDirectory2 )
{
	TCHAR	szDriver1[_MAX_DRIVE + 1];
	TCHAR	szDriver2[_MAX_DRIVE + 1];

	// 从路径中取出驱动器号
	_tsplitpath( lpszDirectory1, szDriver1, NULL, NULL, NULL );
	_tsplitpath( lpszDirectory2, szDriver2, NULL, NULL, NULL );

	//比较驱动器号是否相等
	if ( _tcsicmp(szDriver1, szDriver2) == 0 )
		return TRUE;
	else
		return FALSE;
}

//------------------------------------------------------------
// 取得指定路径的根目录
//------------------------------------------------------------
void odsGetRootDir( LPCTSTR lpszDirectoryName, LPTSTR lpszRootDirectory )
{
	TCHAR	szDrive[_MAX_DRIVE];
	szDrive[0] = _T('\0');

	//分解目录
	_tsplitpath( lpszDirectoryName, szDrive, NULL, NULL, NULL );

	//生成根目录
	wsprintf(lpszRootDirectory, _T("%s\\"), szDrive);
}

//------------------------------------------------------------
// 判断指定的文件是否存在
//------------------------------------------------------------
BOOL odsIsFileExist( LPCTSTR lpszFileName )
{
	WIN32_FIND_DATA	FindData;
	HANDLE			hFindData;

	//执行查找操作
	hFindData = FindFirstFile
			 (
			  lpszFileName,		//要查找的文件名称
			  &FindData			//返回的查找结果
			 );

	//如果没有找到
	if (hFindData == INVALID_HANDLE_VALUE)
		return FALSE;

	//关闭查找句柄
	FindClose(hFindData);

	//判断找到的文件类型是否是文件
	if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		return FALSE;

	return TRUE;
}

//------------------------------------------------------------
// 判断指定的目录是否存在
//------------------------------------------------------------
BOOL odsIsDirExist( LPCTSTR lpszDirectoryName )
{
	WIN32_FIND_DATA	FindData;
	HANDLE				hFindData;
	TCHAR				szRootDirectory[_MAX_DRIVE + 2];

	//判断是否是根目录
	odsGetRootDir( lpszDirectoryName, szRootDirectory );
	if (_tcsicmp(lpszDirectoryName, szRootDirectory) == 0)
		return TRUE; 

	//执行查找操作
	hFindData = FindFirstFile
			 (
			  lpszDirectoryName,	//要查找的目录名称
			  &FindData				//返回的查找结果
			 );

	//如果没有找到
	if (hFindData == INVALID_HANDLE_VALUE)
		return FALSE;

	//关闭查找句柄
	FindClose(hFindData);

	//判断找到的文件是否是目录
	if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		return TRUE;
	else
		return FALSE;
}


//------------------------------------------------------------
// 取指定文件的大小( 返回 0xFFFFFFFF 表示失败 )，
//		lpFileSizeHigh 可以为 NULL
//------------------------------------------------------------
DWORD odsGetFileSize( LPCTSTR lpszFileName, LPDWORD lpFileSizeHigh )
{
	WIN32_FIND_DATA	FindData;
	HANDLE			hFindData;

	//执行查找操作
	hFindData = FindFirstFile
			 (
			  lpszFileName,		//要查找的文件名称
			  &FindData			//返回的查找结果
			 );

	//如果没有找到
	if ( hFindData == INVALID_HANDLE_VALUE )
	{
		if (lpFileSizeHigh != NULL)
			(*lpFileSizeHigh) = 0xFFFFFFFF;
		return 0xFFFFFFFF;
	}

	//关闭查找句柄
	FindClose(hFindData);

	// 如果找到的是目录，也返回失败
	if ( FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY )
	{
		if (lpFileSizeHigh != NULL)
			(*lpFileSizeHigh) = 0xFFFFFFFF;
		return 0xFFFFFFFF;
	}

	if (lpFileSizeHigh != NULL)
		(*lpFileSizeHigh) = FindData.nFileSizeHigh;
	return FindData.nFileSizeLow;
}

//------------------------------------------------------------
// 达到目录所在的硬盘剩余空间的大小，失败则返回负数
//------------------------------------------------------------
__int64 odsGetDiskFreeSpace( LPCTSTR strDir )
{
	ULARGE_INTEGER ul1, ul2;
	if (!GetDiskFreeSpaceEx( strDir, &ul1, &ul2, NULL ))
		return (__int64)( -1 );
	else
		return (__int64)( ul1.QuadPart );
}

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
	)
{
	LPTSTR				lpszExistFileName;
	LPTSTR				lpszNewFileName;
	LPTSTR				lpszPath;
	TCHAR				lpszBuffer[ 3 * _MAX_PATH ];

	lpszPath = lpszBuffer;
	lpszExistFileName = lpszBuffer + _MAX_PATH;
	lpszNewFileName = lpszBuffer + _MAX_PATH * 2;

	BOOL				bReturnCode = TRUE;
	WIN32_FIND_DATA		FindData;
	HANDLE				hFindData;

	//判断原始路径是否存在
	if (!odsIsDirExist(lpszSourceDirectory))
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return FALSE;
	}
 
	//生成查找的文件名
	wsprintf( lpszPath, _T("%s\\*.*"), lpszSourceDirectory );

	//判断目标目录是否存在，如果不存在则创建它
	if (!odsIsDirExist(lpszDestinationDirectory))
	{
		if (!CreateDirectory(lpszDestinationDirectory, NULL))
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_CREATEDIR );
			odsMakeError( pstError, ODS_ERR_CREATEDIR, NULL );
			return FALSE;
		}
	}
	else if ( !bOverwriteExist )
	{
		odsMakeError( pstError, ODS_ERR_OVERWRITE, ODS_MSG_OVERWRITE );
		return FALSE;
	}

	//执行查找
	hFindData = FindFirstFile(lpszPath, &FindData);
	if (hFindData == INVALID_HANDLE_VALUE)
	{
		odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_BROWSEDIR );
		odsMakeError( pstError, ODS_ERR_BROWSEDIR, NULL );		
		return FALSE;
	}

	bReturnCode = TRUE;

	do
	{
		//判断是否是本级目录
		if (lstrcmp(FindData.cFileName, _T(".")) == 0)
			continue;

		//判断是否是上级目录
		if (lstrcmp(FindData.cFileName, _T("..")) == 0)
			continue;

		//判断要生成的文件名的长度
		if( odsIsFullNameOverflow(lpszDestinationDirectory, FindData.cFileName) )
		{
			odsMakeError( pstError, ODS_ERR_NAMEOVERFLOW, ODS_MSG_NAMEOVERFLOW );
			bReturnCode = FALSE;
			break;
		}

		//生成查到的文件名
		wsprintf(lpszExistFileName, _T("%s\\%s"),lpszSourceDirectory, FindData.cFileName);
		//生成要创建的文件名
		wsprintf(lpszNewFileName, _T("%s\\%s"), lpszDestinationDirectory, FindData.cFileName);

		//根据查到文件的类型，做不同的操作
		if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		{
			if ( bIncludeSubDir )
				bReturnCode = odsCopyDirectory
						  (
						   lpszExistFileName, 
						   lpszNewFileName, 
						   bIncludeSubDir, 
						   bOverwriteExist,
						   pstError
						  );
		}
		else
		{
			if (! CopyFile( lpszExistFileName, lpszNewFileName, !bOverwriteExist ) )
			{
				odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_COPYDIR );
				odsMakeError( pstError, ODS_ERR_COPYDIR, NULL );
				bReturnCode = FALSE;
			}

			// 为防止从只读介质上复制到硬盘后，文件带有只读属性而无法修改
			// 需要在此处判断并去除文件的只读属性
			DWORD dwAttr = GetFileAttributes( lpszNewFileName );
			if ( dwAttr & FILE_ATTRIBUTE_READONLY )
				SetFileAttributes( lpszNewFileName, dwAttr & (~FILE_ATTRIBUTE_READONLY) );
		}

		if (!bReturnCode)
			break;
	}while ( FindNextFile(hFindData, &FindData) );

	// 关闭查找句柄
	FindClose(hFindData);
 	
	return(bReturnCode);
}


//------------------------------------------------------------
// 删除指定的目录
//	bIncludeSubDir = TRUE,  删除指定的目录及其所有子目录（包括所有文件）
//	bIncludeSubDir = FALSE, 仅删除指定目录中的文件，不删除子目录和子目录
//							内的文件，也不删除指定目录本身。
//------------------------------------------------------------
BOOL odsRemoveDirectory( LPCTSTR lpszDirectoryName, BOOL bIncludeSubDir, STODSERROR* pstError )
{
	BOOL				bReturnCode = TRUE;	
	WIN32_FIND_DATA		FindData;
	HANDLE				hFindData;	
	TCHAR				szPath[_MAX_PATH];

	//判断目录是否存在
	if( !odsIsDirExist(lpszDirectoryName) )
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return(FALSE);
	}

	//生成查找的文件名
	wsprintf(szPath, _T("%s\\*.*"), lpszDirectoryName);
	
	//进行查找操作
	hFindData = FindFirstFile
				 (
				  szPath,
				  &FindData
				 );

	//如果找到文件
	if(hFindData != INVALID_HANDLE_VALUE)
	{
		do
		{
			//判断是否是本级目录
			if (lstrcmp(FindData.cFileName, _T(".")) == 0)
				continue;

			//判断是否是上级目录
			if (lstrcmp(FindData.cFileName, _T("..")) == 0)
				continue;

			//生成要删除的文件名
			wsprintf(szPath, _T("%s\\%s"), lpszDirectoryName, FindData.cFileName);

			//判断文件的类型
			if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
			{				
				if ( bIncludeSubDir )
					bReturnCode = odsRemoveDirectory(szPath, TRUE, pstError);
			}
			else
			{
				if (! DeleteFile( szPath ) )
				{
					odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_REMOVEDIR );
					odsMakeError( pstError, ODS_ERR_REMOVEDIR, NULL );
					bReturnCode = FALSE;
				}				
			}

			if ( !bReturnCode )			
				break;			
		}while (FindNextFile(hFindData, &FindData));

		//关闭查找句柄
		FindClose(hFindData);		
	}	// end of if (hFindData != INVALID_HANDLE_VALUE)

	if (bReturnCode )
	{
		//删除指定目录本身
		if ( bIncludeSubDir )
			bReturnCode = RemoveDirectory(lpszDirectoryName);
		if (!bReturnCode)
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_REMOVEDIR );
			odsMakeError( pstError, ODS_ERR_REMOVEDIR, NULL );
		}
	}
	
	return(bReturnCode);
}

//------------------------------------------------------------ 
// 设置目录内所有文件的属性
//	bIncludeSubDir			是否包含子目录内的文件
//------------------------------------------------------------ 
BOOL odsSetFileAttributesInDir( LPCTSTR lpszDirectoryName, DWORD dwFileAttributes, BOOL bIncludeSubDir, STODSERROR* pstError )
{
	BOOL				bReturnCode = TRUE;	
	WIN32_FIND_DATA		FindData;
	HANDLE				hFindData;	
	TCHAR				szPath[_MAX_PATH];

	//判断目录是否存在
	if( !odsIsDirExist(lpszDirectoryName) )
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return(FALSE);
	}

	//生成查找的文件名
	wsprintf(szPath, _T("%s\\*.*"), lpszDirectoryName);
	
	//进行查找操作
	hFindData = FindFirstFile
				 (
				  szPath,
				  &FindData
				 );

	//如果找到文件
	if(hFindData != INVALID_HANDLE_VALUE)
	{
		do
		{
			//判断是否是本级目录
			if (lstrcmp(FindData.cFileName, _T(".")) == 0)
				continue;

			//判断是否是上级目录
			if (lstrcmp(FindData.cFileName, _T("..")) == 0)
				continue;

			//生成要设置属性的文件名
			wsprintf(szPath, _T("%s\\%s"), lpszDirectoryName, FindData.cFileName);

			//判断文件的类型
			if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
			{				
				if ( bIncludeSubDir )
					bReturnCode = odsSetFileAttributesInDir(szPath, dwFileAttributes, TRUE, pstError);
			}
			else
			{
				if (!SetFileAttributes( szPath, dwFileAttributes ))
				{
					odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_SETATTR );
					odsMakeError( pstError, ODS_ERR_SETATTR, NULL );
					bReturnCode = FALSE;
				}
			}

			if ( !bReturnCode )
				break;			
		}while (FindNextFile(hFindData, &FindData));

		//关闭查找句柄
		FindClose(hFindData);		
	}	// end of if (hFindData != INVALID_HANDLE_VALUE)

	return(bReturnCode);
}

//------------------------------------------------------------
// 移动目录 - bDirect 指明是直接移动还是先复制再删除
//------------------------------------------------------------
BOOL odsMoveDirectory
		( 
			LPCTSTR lpszSourceDirectory, 
			LPCTSTR lpszDestinationDirectory, 
			BOOL bDirect,
			STODSERROR* pstError 
		)
{
	BOOL	bReturnCode;

	//判断目录是否存在
	if( !odsIsDirExist(lpszSourceDirectory) )
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return(FALSE);
	}

	if ( bDirect && odsIsDirInSameDriver( lpszSourceDirectory, lpszDestinationDirectory) )
	{
		//执行移动操作
		bReturnCode = MoveFile
			   (
			    lpszSourceDirectory,
				lpszDestinationDirectory
			   );
		if ( !bReturnCode )
		{
			odsFormatError( pstError, FORMAT_WITH_SYSTEM_AND_STRING, ODS_MSG_MOVEDIR );
			odsMakeError( pstError, ODS_ERR_MOVEDIR, NULL );
		}
	}
	else
	{
		//执行拷贝操作
		bReturnCode = odsCopyDirectory
			   (
				(LPSTR)lpszSourceDirectory,
				(LPSTR)lpszDestinationDirectory,
				TRUE,									// include sub dir
				TRUE,									// overwrite
				pstError
			   );

		if ( bReturnCode )
		{
			//执行删除操作
			bReturnCode = odsRemoveDirectory
			   (
			    (LPSTR)lpszSourceDirectory, 
				TRUE,
				pstError
			   );
		}
	}

	return(bReturnCode);
}


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
		)
{
	BOOL				bReturnCode = TRUE;
	WIN32_FIND_DATA		FindData;
	HANDLE				hFindData;	
	TCHAR				szPath[_MAX_PATH];

	pstError->nCode = 0; pstError->strMsg[0] = _T('\0');

	//判断目录是否存在
	if ( !odsIsDirExist(lpszDirectoryName) )
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return(FALSE);
	}

	//生成查找的文件名
	wsprintf(szPath, _T("%s\\*.*"), lpszDirectoryName);

	//进行查找操作
	hFindData = FindFirstFile
			 (
			  szPath,
			  &FindData
			 );

	//如果没有找到
	if (hFindData == INVALID_HANDLE_VALUE)
		return(TRUE);

	bReturnCode = TRUE;
	do
	{
		//判断是否是本级目录
		if (lstrcmp(FindData.cFileName, _T(".")) == 0)
			continue;

		//判断是否是上级目录
		if(lstrcmp(FindData.cFileName, _T("..")) == 0)
			continue;

		//生成找到的文件名
		wsprintf(szPath, _T("%s\\%s"), lpszDirectoryName, FindData.cFileName);

		//如果是子目录
		if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		{
			if (dwFileType & FILE_ATTRIBUTE_DIRECTORY)
				bReturnCode = FileBrowseFunc(szPath, pstError);

			if (bReturnCode  && bIncludeSubDir )
				bReturnCode = odsBrowseDirectory(szPath, dwFileType, bIncludeSubDir, 
												 FileBrowseFunc, pstError );
		}
		else
		{
			if (FindData.dwFileAttributes & dwFileType)
				bReturnCode = FileBrowseFunc(szPath, pstError);
		}

		if(!bReturnCode)
			break;
	}while (FindNextFile(hFindData, &FindData) );

	//关闭查找句柄
	FindClose(hFindData);

	return(bReturnCode);
}


//------------------------------------------------------------ 
// 取得指定目录的属性
//		包括此目录包含的子目录的个数，文件的个数，以及目录以字节为单位的大小
//------------------------------------------------------------ 
BOOL odsGetDirProperty( LPCTSTR lpszDirectoryName, BOOL bIncludeSubDir, 
					 STDIRPROPERTY* lpDirectoryProperty, STODSERROR* pstError )
{
	BOOL				bReturnCode = TRUE;
	WIN32_FIND_DATA		FindData;
	HANDLE				hFindData;
	TCHAR				szPath[_MAX_PATH];
	STDIRPROPERTY		DirectoryProperty;

	lpDirectoryProperty->dwDirNum = 0;
	lpDirectoryProperty->dwFileNum = 0;
	lpDirectoryProperty->i64Size = 0;

	//判断目录是否存在
	if( !odsIsDirExist(lpszDirectoryName) )
	{
		odsMakeError( pstError, ODS_ERR_NODIR, ODS_MSG_NODIR );
		return(FALSE);
	}

	//生成查找的文件名
	wsprintf(szPath, _T("%s\\*.*"), lpszDirectoryName);

	//进行查找操作
	hFindData = FindFirstFile
			 (
			  szPath,
			  &FindData
			 );

	//如果没有找到
	if (hFindData == INVALID_HANDLE_VALUE)
		return(TRUE);

	bReturnCode = TRUE;

	do
	{
		//判断是否是本级目录
		if (lstrcmp(FindData.cFileName, _T(".")) == 0)
			continue;

		//判断是否是上级目录
		if (lstrcmp(FindData.cFileName, _T("..")) == 0)
			continue;

		//生成找到的文件名
		wsprintf(szPath, _T("%s\\%s"), lpszDirectoryName, FindData.cFileName);

		//判断文件的类型
		if (FindData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
		{
			lpDirectoryProperty->dwDirNum ++;
			if (bIncludeSubDir )
			{
				bReturnCode = odsGetDirProperty( szPath, TRUE, &DirectoryProperty, pstError );
				if( bReturnCode )
				{
					lpDirectoryProperty->dwDirNum += DirectoryProperty.dwDirNum;
					lpDirectoryProperty->dwFileNum += DirectoryProperty.dwFileNum;
					lpDirectoryProperty->i64Size += DirectoryProperty.i64Size;
				}
			}
		}
		else
		{
			lpDirectoryProperty->dwFileNum ++;
			lpDirectoryProperty->i64Size += 
				((__int64)FindData.nFileSizeHigh * (__int64)MAXDWORD) + (__int64)FindData.nFileSizeLow;
		}

		if (!bReturnCode)
			break;

	}while(FindNextFile(hFindData, &FindData) );

	//关闭查找句柄
	FindClose(hFindData);

	return(bReturnCode);
} 

