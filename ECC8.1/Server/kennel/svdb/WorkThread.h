#ifndef	SVDB_WORKTHREAD_
#define	SVDB_WORKTHREAD_
#include "svdbtype.h"
#include "ThreadEx.h"
#include "libutil/buffer.h"
#include "SvdbMain.h"
#include "QueryProtocol.h"
#include "MySocket.h"

class ThreadContrl;

class WorkThread :
	public ThreadEx
{
public:
	WorkThread(void);
	~WorkThread(void);
	WorkThread(ThreadContrl *pTC,int trdid=0);

	void run(void);

	void trigger(void)
	{
		m_Event.signal();
	}

	void SetSvdbMain(SvdbMain *pMain)
	{
		m_pMain=pMain;
	}

#ifdef WIN32

	inline void SetPipeHandle(HANDLE hPipe)
	{
		m_pipe=hPipe;
	}

#endif

	void SetSocket(SOCKET sock)
	{
		m_sock.SetSocket(sock);
		m_sock.SetNoDelay(true);
	}

	bool m_isSocket;


protected:

	void RunByPipe();
	void RunBySocket();

	void ParseQuery();

	void ProcessMonitorTemplet();
	void ProcessMonitor();
	void ProcessGroup();
	void ProcessEntity();
	void ProcessResource();
	void ProcessSVSE();
	void ProcessIniFile();
	void ProcessTask();
	void ProcessEntityGroup();
	void ProcessEntityTemplet();
	void ProcessDBQuery();
	void ProcessQueueQuery();

	ost::Event m_Event;
	svutil::buffer m_querybuf;
	svutil::buffer	m_buf;
	S_UINT			m_datalen;
	SVDBQUERY m_qinfo;

	SvdbMain	*m_pMain;
	ThreadContrl *m_pTC;

	int threadid;
	void IsSendOk(bool isok);
	void IsUpdateOk(bool isok);
	void IsDBUpdateOk(string id="",bool isok=true);

private:

	bool NotifyChange(string name,string optype,string id);
	bool NotifyIniChangeToQueue(string filename,string opt,string section,string key);
	bool NotiyDBRecord(string table,svutil::buffer &data,S_UINT datalen);
	string GetProperSeId(void);
	bool CopyIniFile(string filecopy,string uid);
	ost::Mutex	m_UpdateLockCopyIni;

#ifdef WIN32
	bool SendBuf();
	bool ReadBuf(int len);
	bool CheckIOEnd();
	HANDLE	m_pipe;
#endif
	bool SocketSendBuf();
	bool SocketReadBuf(int len);
	bool SocketCheckIOEnd();
	MySocket m_sock;


};


#endif
