#ifndef	SVDB_MONITORPOOL_
#define SVDB_MONITORPOOL_

#include "PoolBase.h"
#include "SerialBase.h"
#include "svdbtype.h"
#include "Monitor.h"
#include <cc++/file.h>
#include "util.h"

typedef svutil::hashtable<word,Monitor *> MONITORMAP;

class MonitorPool :
	public PoolBase2,public SerialBase
{
public:
	MonitorPool(void);
	MonitorPool(word filepath);
	MonitorPool(MONITORMAP & inputgmap)
	{
		m_hashtablesize=hashtablesize;
		m_monitors.resetsize(m_hashtablesize);
		m_changed=false;

		needToDel=false;
		MONITORMAP::iterator git;
		while(inputgmap.findnext(git))
			m_monitors.insert((*git).getkey().getword(),(*git).getvalue());
	}
	~MonitorPool(void);

	enum{ hashtablesize=3777 };

	bool Load(void);
	bool Submit(std::string modifyid="");

	bool LoadFile(string fileName);
	bool SubmitToFile(string fileName, MonitorPool * dataPool );

	S_UINT	GetRawDataSize(void);
	char*	GetRawData(char *lpbuf,S_UINT bufsize);
	BOOL	CreateObjectByRawData(const char *lpbuf,S_UINT bufsize);

	bool push(Monitor *pm);
	bool PushData(const char *buf,S_UINT len);

	inline bool Find(word id)
	{
		Monitor **pm=m_monitors.find(id);
		return pm!=NULL;
	}

	Monitor *GetMonitor(word id)
	{
		Monitor **pm=m_monitors.find(id);
		if(pm==NULL)
			return NULL;
		m_changed=true;
		return *pm;
	}

	bool GetMonitorData(word id,char *buf,S_UINT &len);

	bool DeleteMonitor(word id);

	bool GetInfo(word infoname,StringMap &map)
	{
		MONITORMAP::iterator it;
		while(m_monitors.findnext(it))
		{
			StringMap &tm=(*it).getvalue()->GetProperty();
			if(tm.find(infoname)!=NULL)
				map[(*it).getkey().getword()]=tm[infoname];
			else
				map[(*it).getkey().getword()]="";


		}
		
		return true;
	}

	bool GetInfoBySE(int seid,word infoname,StringMap &map)
	{
		if(seid<1)
			return false;

		MONITORMAP::iterator it;
		while(m_monitors.findnext(it))
		{
			if(atoi(::GetTopID((*it).getkey()))==seid)
			{

				StringMap &tm=(*it).getvalue()->GetProperty();
				if(tm.find(infoname)!=NULL)
					map[(*it).getkey().getword()]=tm[infoname];
				else
					map[(*it).getkey().getword()]="";
			}

		}

		return true;

	}


	void DisplayAllData(void)
	{
		puts("------------------------------------------Begin----------------------------------------");
		printf("Hash table size :%d\n",m_hashtablesize);
		puts("---------------------------------------------------------------------------------------");
		MONITORMAP::iterator it;
		while(m_monitors.findnext(it))
		{
			(*it).getvalue()->DisplayAllData();
		}
	}


protected:
	MONITORMAP m_monitors;
	S_UINT	m_hashtablesize;
	bool needToDel;

};

#endif
