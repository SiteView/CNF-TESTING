//////////////////////////////////////////////////////////////////////////
//
// odsLog.h 
//
//		有关程序日志（使用数据库日志和NT系统日志两种方式）的操作
//
// by Wang Yong Gang, 1999-10-27
// 在 张军 1998/05/31 基础上完成
//
//////////////////////////////////////////////////////////////////////////

#if !defined(_ODS_20_LOG_H_)
#define _ODS_20_LOG_H_

//-------------------------------------------------------------------
// odsAddToSystemLog			- 在 NT 的日志表中增加一条信息
//
// 参数：
//		strSourceName			- 事件源名称
//		strMsg					- 信息内容
//		wType					- 类型，可以为以下几种
//								- EVENTLOG_ERROR_TYPE
//								- EVENTLOG_WARNING_TYPE
//								- EVENTLOG_INFORMATION_TYPE
//								- EVENTLOG_AUDIT_SUCCESS
//								- EVENTLOG_AUDIT_FAILURE
//-------------------------------------------------------------------
void odsAddToSystemLog( LPCTSTR strSourceName, LPCTSTR strMsg, WORD wType );

#endif // _ODS_20_LOG_H_