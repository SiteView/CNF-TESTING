////////////////////////////////////////////////////////////////////////
//
// odsErrorCode.h
//
//		ODS 错误代码和错误信息表
//
// by Wang Yong Gang, 1999-10-15
//
////////////////////////////////////////////////////////////////////////

#if !defined(_ODS_20_ERROR_CODE_H_)
#define _ODS_20_ERROR_CODE_H_


// 错误码基点
#define ODS_ERR_BASE			6000

// 以下是所有错误代码以及错误信息的定义
#define ODS_ERR_OK				0
#define ODS_MSG_OK				_T("成功")	

#define ODS_ERR_NET				( ODS_ERR_BASE + 1 )
#define ODS_MSG_NET				_T("网络通讯错误")

#define ODS_ERR_DUPBATCH		( ODS_ERR_BASE + 2 )
#define ODS_MSG_DUPBATCH		_T("批信息重复")

#define ODS_ERR_NOSRV			( ODS_ERR_BASE + 3 )
#define ODS_MSG_NOSRV			_T("在指定的计算机上无法找到文档服务")

#define ODS_ERR_BADPARAM		( ODS_ERR_BASE + 4 )
#define ODS_MSG_BADPARAM		_T("参数非法")

#define ODS_ERR_CREATEDIR		( ODS_ERR_BASE + 6 )
#define ODS_MSG_CREATEDIR		_T("无法创建目录")

#define ODS_ERR_OVERWRITE		( ODS_ERR_BASE + 7 )
#define ODS_MSG_OVERWRITE		_T("目录或文件已经存在，无法覆盖")

#define ODS_ERR_EMPTYDIR		( ODS_ERR_BASE + 8 )
#define ODS_MSG_EMPTYDIR		_T("目录为空")

#define ODS_ERR_NAMEOVERFLOW	( ODS_ERR_BASE + 9 )
#define ODS_MSG_NAMEOVERFLOW	_T("目录名或文件名太长，操作无法完成")

#define ODS_ERR_COPYDIR			( ODS_ERR_BASE + 10 )
#define ODS_MSG_COPYDIR			_T("复制目录时文件系统发生错误")

#define ODS_ERR_REMOVEDIR		( ODS_ERR_BASE + 11 )
#define ODS_MSG_REMOVEDIR		_T("删除目录时文件系统发生错误")

#define ODS_ERR_MOVEDIR			( ODS_ERR_BASE + 12 )
#define ODS_MSG_MOVEDIR			_T("移动目录时文件系统发生错误")

#define ODS_ERR_NODIR			( ODS_ERR_BASE + 13 )
#define ODS_MSG_NODIR			_T("指定的目录不存在")

#define ODS_ERR_NOFILE			( ODS_ERR_BASE + 14 )
#define ODS_MSG_NOFILE			_T("文件不存在")

#define ODS_ERR_BROWSEDIR		( ODS_ERR_BASE + 15 )
#define ODS_MSG_BROWSEDIR		_T("浏览目录时文件系统发生错误")

#define ODS_ERR_READFILE		( ODS_ERR_BASE + 16 )
#define ODS_MSG_READFILE		_T("读文件时错误")

#define ODS_ERR_WRITEFILE		( ODS_ERR_BASE + 17 )
#define ODS_MSG_WRITEFILE		_T("写文件时错误")

#define ODS_ERR_OPENFILE		( ODS_ERR_BASE + 18 )
#define ODS_MSG_OPENFILE		_T("打开文件时错误")

#define ODS_ERR_POSFILE			( ODS_ERR_BASE + 19 )
#define ODS_MSG_POSFILE			_T("移动文件指针失败")

#define ODS_ERR_MALLOC			( ODS_ERR_BASE + 20 )
#define ODS_MSG_MALLOC			_T("内存不足，分配内存错误")

#define ODS_ERR_RECURSUBDIR		( ODS_ERR_BASE + 21 )
#define ODS_MSG_RECURSUBDIR		_T("要归并的目录中有子目录，无法完成归并")

#define ODS_ERR_LARGE			( ODS_ERR_BASE + 22 )
#define ODS_MSG_LARGE			_T("文件太大，操作无法完成")

#define ODS_ERR_UNKNOWNPACK		( ODS_ERR_BASE + 23 )
#define ODS_MSG_UNKNOWNPACK		_T("卷的归并格式无法识别")

#define ODS_ERR_SETATTR			( ODS_ERR_BASE + 24 )
#define ODS_MSG_SETATTR			_T("无法设置文件属性")

#define ODS_ERR_OPENSCM			( ODS_ERR_BASE + 25 )
#define ODS_MSG_OPENSCM			_T("无法打开指定计算机上的服务控制器")

#define ODS_ERR_OPENSERVICE		( ODS_ERR_BASE + 26 )
#define ODS_MSG_OPENSERVICE		_T("无法打开指定的服务")

#define ODS_ERR_DELSERVICE		( ODS_ERR_BASE + 27 )
#define ODS_MSG_DELSERVICE		_T("无法删除指定的服务")

#define ODS_ERR_STOPSERVICE		( ODS_ERR_BASE + 28 )
#define ODS_MSG_STOPSERVICE		_T("无法停止指定的服务")

#define ODS_ERR_REPORTSTATUS	( ODS_ERR_BASE + 29 )
#define ODS_MSG_REPORTSTATUS	_T("无法向服务控制器报告服务状态")

#define ODS_ERR_INSTSERVICE		( ODS_ERR_BASE + 30 )
#define ODS_MSG_INSTSERVICE		_T("无法安装指定的服务")

#define ODS_ERR_STARTSERVICE	( ODS_ERR_BASE + 31 )
#define ODS_MSG_STARTSERVICE	_T("无法启动指定的服务")

#define ODS_ERR_QUERYSERVICE	( ODS_ERR_BASE + 32 )
#define ODS_MSG_QUERYSERVICE	_T("无法取得指定服务的状态信息")

#define ODS_ERR_INITMFC			( ODS_ERR_BASE + 33 )
#define ODS_MSG_INITMFC			_T("程序无法运行:可能是系统中安装的 MFC 库的版本太旧")

#define ODS_ERR_STARTSVCDISP	( ODS_ERR_BASE + 34 )
#define ODS_MSG_STARTSVCDISP	_T("无法启动服务程序的主函数")

#define ODS_ERR_REGSVCHCTRL		( ODS_ERR_BASE + 35 )
#define ODS_MSG_REGSVCHCTRL		_T("无法注册服务程序的控制函数")

#define ODS_ERR_RPCUSEPROTOCOL	( ODS_ERR_BASE + 36 )
#define ODS_MSG_RPCUSEPROTOCOL	_T("无法配置 RPC 使用的协议")

#define ODS_ERR_RPCREGIF		( ODS_ERR_BASE + 37 )
#define ODS_MSG_RPCREGIF		_T("无法注册 RPC 接口")

#define ODS_ERR_RPCLISTEN		( ODS_ERR_BASE + 38 )
#define ODS_MSG_RPCLISTEN		_T("无法开始 RPC 侦听")

#define ODS_ERR_REGISTRY		( ODS_ERR_BASE + 39 )
#define ODS_MSG_REGISTRY		_T("操作注册表失败")

#define ODS_ERR_CREATEMUTEX		( ODS_ERR_BASE + 40 )
#define ODS_MSG_CREATEMUTEX		_T("无法创建互斥量")

#define ODS_ERR_MAXCONNECT		( ODS_ERR_BASE + 41 )
#define ODS_MSG_MAXCONNECT		_T("受客户连接数或数据库连接数限制，无法响应客户的连接请求")

#define ODS_ERR_MAXDOCOPENED	( ODS_ERR_BASE + 42 )
#define ODS_MSG_MAXDOCOPENED	_T("受同一客户同时打开的文档数限制，无法打开更多的文档")

#define ODS_ERR_LOSTCLIENT		( ODS_ERR_BASE + 43 )
#define ODS_MSG_LOSTCLIENT		_T("客户连接异常中断，可能是客户程序发生了异常")

#define ODS_ERR_RPCBINDING		( ODS_ERR_BASE + 44 )
#define ODS_MSG_RPCBINDING		_T("无法连接文档服务:无法完成 RPC 绑定")

#define ODS_ERR_PING			( ODS_ERR_BASE + 45 )
#define ODS_MSG_PING			_T("无法连接文档服务:服务可能没有运行")

#define ODS_ERR_GETCOMPUTER		( ODS_ERR_BASE + 46 )
#define ODS_MSG_GETCOMPUTER		_T("无法得到计算机名")

#define ODS_ERR_DBCONNECT		( ODS_ERR_BASE + 47 )
#define ODS_MSG_DBCONNECT		_T("无法连接数据库")

#define ODS_ERR_DATABASE		( ODS_ERR_BASE + 48 )
#define ODS_MSG_DATABASE		_T("数据库操作失败")

#define ODS_ERR_FILETRANS		( ODS_ERR_BASE + 49 )
#define ODS_MSG_FILETRANS		_T("文件传输时错误，发送和接受的字节数不匹配")

#define ODS_ERR_NOSPACE			( ODS_ERR_BASE + 50 )
#define ODS_MSG_NOSPACE			_T("设备上已没有可用空间，无法接收或移动文档")

#define ODS_ERR_NOCDGROUP		( ODS_ERR_BASE + 51 )
#define ODS_MSG_NOCDGROUP		_T("指定的光盘组不存在")

#define ODS_ERR_NOVOLUME		( ODS_ERR_BASE + 52 )
#define ODS_MSG_NOVOLUME		_T("指定的卷不存在")

#define ODS_ERR_NODOCUMENT		( ODS_ERR_BASE + 53 )
#define ODS_MSG_NODOCUMENT		_T("指定的文档不存在，或正被其他程序使用")

#define ODS_ERR_NODEVICE		( ODS_ERR_BASE + 54 )
#define ODS_MSG_NODEVICE		_T("指定的设备不存在")

#define ODS_ERR_LOCATION		( ODS_ERR_BASE + 55 )
#define ODS_MSG_LOCATION		_T("定位信息格式不正确")

#define ODS_ERR_DIFFPACK		( ODS_ERR_BASE + 56 )
#define ODS_MSG_DIFFPACK		_T("要求的归并格式和卷当前的归并格式不符")

#define ODS_ERR_UNZIP			( ODS_ERR_BASE + 57 )
#define ODS_MSG_UNZIP			_T("无法从 ZIP 文件中解压缩出指定的文件")

#define ODS_ERR_TEMPFILE		( ODS_ERR_BASE + 58 )
#define ODS_MSG_TEMPFILE		_T("操作系统无法创建临时文件")

#define ODS_ERR_DEVREADONLY		( ODS_ERR_BASE + 59 )
#define ODS_MSG_DEVREADONLY		_T("文档处于只读介质(如光盘)上，无法修改")

#define ODS_ERR_DOCREADONLY		( ODS_ERR_BASE + 60 )
#define ODS_MSG_DOCREADONLY		_T("文档以只读方式打开，无法修改")

#define ODS_ERR_SINGLEBACK		( ODS_ERR_BASE + 61 )
#define ODS_MSG_SINGLEBACK		_T("文档中不能存在孤立的负数页号")

#define ODS_ERR_FILENAMEDUP		( ODS_ERR_BASE + 62 )
#define ODS_MSG_FILENAMEDUP		_T("文档内不能有重名文件")

#define ODS_ERR_VOLMAKECD		( ODS_ERR_BASE + 63 )
#define ODS_MSG_VOLMAKECD		_T("文档所在的卷正在刻盘，拒绝访问")

#define ODS_ERR_DEVNOTREADONLY	( ODS_ERR_BASE + 64 )
#define ODS_MSG_DEVNOTREADONLY	_T("文档并非处于只读介质(如光盘)上，无法废弃或增补")

#define ODS_ERR_NEEDNAMEONCOPY	( ODS_ERR_BASE + 65 )
#define ODS_MSG_NEEDNAMEONCOPY	_T("在同一光盘组中复制文档时，必须给出新的文档名称")


#endif //_ODS_20_ERROR_CODE_H_