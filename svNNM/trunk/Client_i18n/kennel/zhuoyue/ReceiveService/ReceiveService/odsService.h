//////////////////////////////////////////////////////////////////////////
//
// odsService.h 
//
//		有关 NT 服务控制的通用函数定义
//
// by Wang Yong Gang, 1999-10-27
// 在 张军 1998/05/17 以及 VC Sample RpcSvc 基础上完成
//
//////////////////////////////////////////////////////////////////////////

#if !defined(_ODS_20_SERVICE_H_)
#define _ODS_20_SERVICE_H_

#include "odsError.h"

//-----------------------------------------------------------------------
// odsServiceMessageBox				- 服务中弹出的对话框
//-----------------------------------------------------------------------
void odsServiceMessageBox( LPCTSTR lpszText );

//----------------------------------------------------------------
// odsGetServiceStatus			- 取得指定机器上的服务程序的状态
//
// 参数:
//		lpszComputerName		- 服务程序所在的计算机名称，如果为 NULL
//								- 则指本地机器
//		lpszServiceName			- 服务程序名称
//		lpdwServiceStatus		- 返回服务程序的状态
//		pstError				- 错误信息结构指针，可以为 NULL//
// 返回值:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsGetServiceStatus( LPCTSTR strComputerName, LPCTSTR strServiceName, 
						  LPDWORD lpdwServiceStatus, STODSERROR* pstError );

//-----------------------------------------------------------------------
// odsReportStatusToSCMgr				- 将自己的状态通知服务控制台
//
// 参数：
//		hServiceStatus					- 服务状态句柄
//		dwCurrentState					- 要设置的状态
//		DWORD dwErrorCode				- 错误码，0 表示无错误
//		dwWaitHint						- 控制器等待间隔
//		pstError						- 接收错误信息指针（可以为 NULL ）
// 返回：
//		bSuccess						- 成功与否
//-----------------------------------------------------------------------
BOOL odsReportStatusToSCMgr(SERVICE_STATUS_HANDLE hServiceStatus,
							DWORD dwCurrentState,
							DWORD dwErrorCode,
							DWORD dwWaitHint,
							STODSERROR* pstError );

//-----------------------------------------------------------------------
// odsInstallService				- 安装服务
// 
// 参数：
//		strComputerName				- 计算机名（NULL表示本地计算机）
//		strServiceName				- 服务名称
//		strServicePath				- 服务路径
//		strDisplayName				- 服务的显示名称
//		dwStartType					- 服务的启动方式
//									- SERVICE_AUTO_START 或 SERVICE_DEMAND_START
//		strDependencies				- 服务的依赖关系（可以为 NULL ）
//		pstError					- 接收错误信息指针（可以为 NULL ）
// 返回：
//		bSuccess					- 成功与否
//-----------------------------------------------------------------------
BOOL odsInstallService( LPCTSTR strComputerName, 
					   LPCTSTR strServiceName, 
					   LPCTSTR strServicePath, 
					   LPCTSTR strDisplayName, 
					   DWORD dwStartType,
					   LPCTSTR strDependencies,
					   STODSERROR* pstError );

//----------------------------------------------------------------
// odsRemoveService					- 删除某计算机上的服务
//
// 参数：
//		strComputerName				- 计算机名，为 NULL 时表示本地计算机
//		strServiceName				- 服务名
//		dwWaitTime					- 等待时间
//		pstError					- 错误信息结构指针，可以为 NULL
// 返回：
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsRemoveService( LPCTSTR strComputerName, LPCTSTR strServiceName, DWORD dwWaitTime, STODSERROR* pstError );

//----------------------------------------------------------------
// odsStartService					- 启动服务
//
// 参数:
//		lpszComputerName			- 服务程序所在的计算机名称，如果为 NULL
//									- 则指本地计算机
//		lpszServiceName				- 要启动服务的名称
//		dwWaitTime					- 等待服务启动的时间，以(毫秒)为单位
//		pstError					- 错误信息结构指针，可以为 NULL
//
// 返回:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsStartService(LPCTSTR strComputerName, LPCTSTR strServiceName, 
				   DWORD dwWaitTime, STODSERROR* pstError);

//----------------------------------------------------------------
// odsStopService					- 停止服务
//
// 参数:
//		lpszComputerName			- 服务程序所在的计算机名称，如果为 NULL
//									- 则指本地计算机
//		lpszServiceName				- 要启动服务的名称
//		dwWaitTime					- 等待服务启动的时间，以(毫秒)为单位
//		pstError					- 错误信息结构指针，可以为 NULL
//
// 返回:
//		bSuccess					- 成功与否
//----------------------------------------------------------------
BOOL odsStopService( LPCTSTR strComputerName, LPCTSTR strServiceName, 
					DWORD dwWaitTime, STODSERROR* pstError );

#endif // _ODS_20_SERVICE_H_