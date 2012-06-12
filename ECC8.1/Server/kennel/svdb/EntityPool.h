#ifndef	SVDB_ENTITYPOOL_
#define SVDB_ENTITYPOOL_

#include "PoolBase.h"
#include "SerialBase.h"
#include "svdbtype.h"
#include "Entity.h"
#include <cc++/file.h>

typedef svutil::hashtable<word,Entity *> ENTITYMAP;

class EntityPool :
	public PoolBase2,public SerialBase
{
public:
	EntityPool(void);
	EntityPool(word filepath);
	EntityPool(ENTITYMAP & inputgmap)
	{
		m_hashtablesize=hashtablesize;
		m_entitys.resetsize(m_hashtablesize);
		m_changed=false;

		needToDel=false;
		ENTITYMAP::iterator git;
		while(inputgmap.findnext(git))
			m_entitys.insert((*git).getkey().getword(),(*git).getvalue());
	}
	~EntityPool(void);

	enum{ hashtablesize=1277 };

	bool Load(void);
	bool Submit(std::string modifyid="");

	bool LoadFile(string fileName);
	bool SubmitToFile(string fileName, EntityPool * dataPool );

	S_UINT	GetRawDataSize(void);
	char*	GetRawData(char *lpbuf,S_UINT bufsize);
	BOOL	CreateObjectByRawData(const char *lpbuf,S_UINT bufsize);

	bool push(Entity *pm);
	bool PushData(const char *buf,S_UINT len);

	Entity *GetEntity(word id)
	{
		Entity **pm=m_entitys.find(id);
		if(pm==NULL)
			return NULL;
		m_changed=true;
		return *pm;
	}

	bool GetEntityData(word id,char *buf,S_UINT &len);

	word GetNextSubID(word entityid)
	{
		MutexLock lock(m_UpdateLock);
		Entity *pe=GetEntity(entityid);
		if(pe)
		{
			m_changed=true;
			return pe->GetNextMonitorID();
		}
		return "";
	}

	bool PutSubMonitor(word entityid,word monitorid)
	{
		MutexLock lock(m_UpdateLock);
		Entity *pe=GetEntity(entityid);
		if(pe)
		{
			pe->GetMonitors().push_back(monitorid);
			m_changed=true;
			return true;
		}
		return false;

	}

	bool Find(word id)
	{
		Entity **pe=m_entitys.find(id);
		return pe!=NULL;
	}

	bool DeleteEntity(word id);

	void DisplayAllData(void)
	{
		puts("------------------------------------------Begin----------------------------------------");
		printf("Hash table size :%d\n",m_hashtablesize);
		puts("---------------------------------------------------------------------------------------");
		ENTITYMAP::iterator it;
		while(m_entitys.findnext(it))
		{
			(*it).getvalue()->DisplayAllData();
		}
	}

	bool GetInfo(word infoname,StringMap &map)
	{

		ENTITYMAP::iterator it;
		while(m_entitys.findnext(it))
		{
			StringMap &tm=(*it).getvalue()->GetProperty();
			if(tm.find(infoname)!=NULL)
				map[(*it).getkey().getword()]=tm[infoname];
			else
				map[(*it).getkey().getword()]="";


		}
		
		return true;

	}

	bool DeleteSubMonitorID(word id)
	{
		MutexLock lock(m_UpdateLock);
		word entityid=::GetParentID(id);
		Entity **pe=m_entitys.find(entityid);
		if(pe==NULL)
			return false;

		if((*pe)->DeleteSubMonitorID(id))
			m_changed=true;
		else
			return false;
		return true;
	}



protected:
	ENTITYMAP m_entitys;
	S_UINT	m_hashtablesize;
	bool needToDel;

};

#endif
