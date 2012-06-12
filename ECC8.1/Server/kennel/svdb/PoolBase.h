#ifndef	SVDB_POOLBASE_
#define	SVDB_POOLBASE_

#include "svdbtype.h"

#include <cc++/thread.h>

using namespace ost;


class PoolBase
{
public:
	PoolBase(void);
	 ~PoolBase(void);

	PoolBase(word filepath){ m_FilePath=filepath;}

	virtual bool Load()=0;
	virtual bool Submit()=0;
	void PutFilePath(word filepath){
		if(filepath==m_FilePath)
			return;
		m_FilePath=filepath;
		m_changed=true;
	}
	word GetFilePath(void){return m_FilePath;}


protected:
	word	m_FilePath;
	Mutex	m_UpdateLock;
	bool	m_loaded;
	bool	m_changed;
	

};


class PoolBase2
{
public:
	PoolBase2(void);
	 ~PoolBase2(void);

	PoolBase2(word filepath){ m_FilePath=filepath;}

	virtual bool Load()=0;
	virtual bool Submit(std::string modifyid="")=0;
	void PutFilePath(word filepath){
		if(filepath==m_FilePath)
			return;
		m_FilePath=filepath;
		m_changed=true;
	}
	word GetFilePath(void){return m_FilePath;}


protected:
	word	m_FilePath;
	Mutex	m_UpdateLock;
	bool	m_loaded;
	bool	m_changed;
	

};

#endif
