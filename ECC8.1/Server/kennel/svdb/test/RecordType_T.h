#ifndef SVDBTEST_RECORDTYPE_T_
#define	SVDBTEST_RECORDTYPE_T_
#include "../svdbtype.h"
#include <vector>
#include "../libutil/time.h"
#include <cc++/thread.h>
#include <windows.h>
#include <fcntl.h>

using namespace std;

#define UNITLEN		12
#define	NAMELEN		50
#define LABELLEN	100

#define MAXINT		2147483647
#define UMAXINT		4294967295

#define MINPAGESIZE 1024
#define MAXPAGESIZE	1024*24

#define MAXTABLE	40000

class DataType_T
{
public:
	DataType_T(void){
		type=nulltype;
		memset(unit,0,UNITLEN);

	};
	~DataType_T(void){

	};
	DataType_T(const DataType_T &dt)
	{
		type=dt.type;
		memcpy(unit,dt.unit,UNITLEN);
		memcpy(name,dt.name,NAMELEN);
		memcpy(label,dt.label,LABELLEN);
	}

	DataType_T &operator=(const DataType_T &dt)
	{
		type=dt.type;
		memcpy(unit,dt.unit,UNITLEN);
		memcpy(name,dt.name,NAMELEN);
		memcpy(label,dt.label,LABELLEN);
		return *this;
	}
	enum valuetype{
		nulltype=0,
		inttype=1,
		floattype=2,
		stringtype=3
	};


	valuetype type;
	char  unit[UNITLEN];
	char  name[NAMELEN];
	char  label[LABELLEN];

};

///建索引时可以使用

class RecordPos_T
{
public:
	RecordPos_T(){
		m_page=0;
		m_beginpos=0;
		m_datalen=0;
		m_indexkey=DataType_T::nulltype;
		m_indexdata=NULL;
	    m_indexdatalen=0;



	};
	RecordPos(const RecordPos_T &rpt)
	{
		m_beginpos=rpt.m_beginpos;
		m_datalen=rpt.m_datalen;
		m_indexkey=rpt.m_indexkey;
		if((rpt.m_indexdata)&&(rpt.m_indexdatalen>0))
		{
			m_indexdata=new char[m_indexdatalen];
			memmove(m_indexdata,rpt.m_indexdata,rpt.m_indexdatalen);
			m_indexdatalen=rpt.m_indexdatalen;
		}
			
	}
	RecordPos_T &operator=(const RecordPos_T &rpt)
	{
		m_beginpos=rpt.m_beginpos;
		m_datalen=rpt.m_datalen;
		m_indexkey=rpt.m_indexkey;
		if((rpt.m_indexdata)&&(rpt.m_indexdatalen>0))
		{
			m_indexdata=new char[m_indexdatalen];
			memmove(m_indexdata,rpt.m_indexdata,rpt.m_indexdatalen);
			m_indexdatalen=rpt.m_indexdatalen;
		}

		return *this;
	}

	bool operator >(const RecordPos_T &rpt) const
	{
		switch(m_indexkey)
		{
		case DataType_T::floattype:
			{
			if((m_indexdata)||(rpt.m_indexdata==NULL))
				return false;
				return (*(float *)m_indexdata)>(*(float *)rpt.m_indexdata);
			}
		case DataType_T::inttype:
			{
			if((m_indexdata)||(rpt.m_indexdata==NULL))
				return false;
			int *pt=(int*)m_indexdata;
				int *ptr=(int*)rpt.m_indexdata;
				return (*pt)>(*ptr);
			/*	int temp=0;
				int tempr=0;
				memmove(temp,m_indexdata,sizeof(int));
				memmove(tempr,rpt.m_indexdata,sizeof(int));
				return temp>tempr;*/

			}
		case DataType_T::stringtype:
			{
				if((m_indexdata)||(rpt.m_indexdata==NULL))
					return false;
				svutil::word temp=m_indexdata;
				svutil::word tempr=rpt.m_indexdata;
				return temp>tempr;
			}
		default:
			return false;
		}

	}
 
	bool operator <(const RecordPos_T &rpt) const
	{
		switch(m_indexkey)
		{
		case DataType_T::floattype:
			{
			if((m_indexdata)||(rpt.m_indexdata==NULL))
				return false;
			return (*(float *)m_indexdata)<(*(float *)rpt.m_indexdata);
			}
		case DataType_T::inttype:
			{
		    	if((m_indexdata)||(rpt.m_indexdata==NULL))
			    	return false;
		    	int *pt=(int*)m_indexdata;
				int *ptr=(int*)rpt.m_indexdata;
				return (*pt)<(*ptr);
			/*	int temp=0;
				int tempr=0;
				memmove(temp,m_indexdata,sizeof(int));
				memmove(tempr,rpt.m_indexdata,sizeof(int));
				return temp>tempr;*/

			}
		case DataType_T::stringtype:
			{
				if((m_indexdata)||(rpt.m_indexdata==NULL))
					return false;
				svutil::word temp=m_indexdata;
				svutil::word tempr=rpt.m_indexdata;
				return temp<tempr;
			}
		default:
			return false;
		}

	}
	bool operator == (const RecordPos_T &rpt) const
	{
		switch(m_indexkey)
		{
		case DataType_T::floattype:
			{
			if((m_indexdata)||(rpt.m_indexdata==NULL))
				return false;
			return (*(float *)m_indexdata)==(*(float *)rpt.m_indexdata);
			}
		case DataType_T::inttype:
			{
		    	if((m_indexdata)||(rpt.m_indexdata==NULL))
			    	return false;
		    	int *pt=(int*)m_indexdata;
				int *ptr=(int*)rpt.m_indexdata;
				return (*pt)==(*ptr);
			/*	int temp=0;
				int tempr=0;
				memmove(temp,m_indexdata,sizeof(int));
				memmove(tempr,rpt.m_indexdata,sizeof(int));
				return temp>tempr;*/

			}
		case DataType_T::stringtype:
			{
				if((m_indexdata)||(rpt.m_indexdata==NULL))
					return false;
				svutil::word temp=m_indexdata;
				svutil::word tempr=rpt.m_indexdata;
				return temp==tempr;
			}
		default:
			return false;
		}
	}
	int m_page;
	int	m_beginpos;
	int	m_datalen;
	char *m_indexdata;
	int m_indexdatalen;


	int m_indexkey;

};

class RecordType_T
{
public:
	RecordType_T(void){
		m_monitortype=0;
		m_monitorid="";
	};
	~RecordType_T(void){
	};

	int	m_monitortype;
	std::string m_monitorid;
//	char *m_monitorid;

	std::vector<DataType_T *> m_data;

};

class Page_T;

class PagePool_T;

struct DYN_T{
	DYN_T()
	{
		m_state=0;
		m_laststatekeeptimes=0;
		m_displaystr=NULL;
	}
	~DYN_T()
	{
		if(m_displaystr!=NULL)
			free(m_displaystr);
	}

	svutil::TTime m_time;
	int	 m_state;
	svutil::TTimeSpan m_keeplaststatetime;
	unsigned int m_laststatekeeptimes;
	char *m_displaystr;

};


class Table_T
{
public:
	Table_T()
	{
		m_firstpage=-1;
		m_currentpage=-1;
		m_hasindex=0;
		m_page=NULL;
		m_pagepool=NULL;
		m_laststatekeeptimes=0;
		m_state=0;
		m_laststatechangetime=svutil::TTime::GetCurrentTimeEx();
		m_time=svutil::TTime::GetCurrentTimeEx();
		m_displaystr="default str";
	}
	~Table_T()
	{
		;
	}
	int m_firstpage;
	int	m_currentpage;
	svutil::TTime m_createtime;
	int m_hasindex;

	RecordType_T m_type;

	PagePool_T *m_pagepool;
	Page_T *m_page;

	std::vector<DataType_T *> m_indextype;
	
	svutil::TTime m_time;
	int	 m_state;
	svutil::TTime m_laststatechangetime;
	unsigned int m_laststatekeeptimes;
	string m_displaystr;


	bool CreateObjByPage(Page_T *pgage);
	bool FlushData();
	int GetRawDataSize();
	bool InsertRecord(int state,char *data,int len);
	int QueryRecordByTime(svutil::TTime begin,svutil::TTime end,char *buf,int buflen);
	Page_T *GetPreRecordPage(Page_T *ppage);
	Page_T *GetNextPage(Page_T *ppage);

	int GetSingleRecord(Page_T *ppage,int pos,char *buf,int buflen);

	int QueryDyn(char *buf,int &buflen);

	int GetDynSize(void);



};
struct TablePos_T
{
public:
	TablePos_T(){;};
	TablePos_T(const TablePos_T&tpt)
	{
		monitorid=tpt.monitorid;
		firstpos=tpt.firstpos;
	}
	string monitorid;
	int	firstpos;
};

typedef svutil::hashtable<svutil::word,Table_T *> TABLEPOS;


struct DBhead_T
{
	DBhead_T()
	{
		m_version=1;
		m_currentid=0;
		m_monitorcount=0;
		memset(m_dbfilename,0,256);

	}
	int m_version;
	int m_currentid;
	int m_monitorcount;
	char m_dbfilename[256];

};

class DB_T
{
public:
	DB_T()
	{
		m_tables.resetsize(maxtablesize);
		m_dbfilename="";
	//	currentid=0;
		m_dbheadfile="";
		m_pagepool=NULL;
		m_headpool=NULL;
		m_dbh=NULL;

	}

	DB_T(string dbfilename,string dbheadfile)
	{
		m_dbfilename=dbfilename;
		m_dbheadfile=dbheadfile;

	//	currentid=0;
		m_tables.resetsize(maxtablesize);
	}
	~DB_T()
	{
		FlashData();
		if(m_pagepool)
			delete m_pagepool;
		if(m_headpool)
			delete m_headpool;
	}
	enum { maxtablesize = 5771 };

	struct DBhead_T *m_dbh;

	TABLEPOS m_tables;
	PagePool_T *m_pagepool;
	PagePool_T *m_headpool;
	string	m_dbfilename;
	string	m_dbheadfile;

	bool Load();
	bool FlashData();
	bool CreateNew(string dbheadfile,string dbfilename,int pagesize,int pagecount);
	bool InsertTable(string monitorid,int monitortype);
	bool InsertRecord(string monitorid,int state,char *data,int datalen);
	int QueryRecordByTime(string monitorid,svutil::TTime begin,svutil::TTime end,char *buf,int buflen);
	int QueryDyn(string monitorid,char *buf,int&buflen);

};



class Record_T
{
public:
	RecordType_T *m_prrt;
public:
	int	m_datalen;
	int	m_state;
	svutil::TTime m_time;
	int m_pre;
	int m_prepage;



	char *m_data;

};

class RecordSet_T
{
public:
	RecordType_T *m_prtt; 
	std::list<Record_T *> m_Rlist;

};

#define CROSSPAGEUP		0x00000001
#define CROSSPAGEDOWN	0x00000002
#define PAGEUNUSED		0x00000004
#define BADPAGE			0x00000008
#define PAGEFULL		0x00000010

#define F_SET(x,f) (x) |= (f)
#define F_CLR(x,f) (x) &= ~(f)
#define F_ISSET(x,f) ((x) & (f))


class PagePool_T;
struct PageHead_T{
	PageHead_T()
	{
		m_pos=-1;
		m_unused=1;
		m_pagesize=0;
		m_datasize=0;
		m_nextrecordpage=-1;
		m_prerecordpage=-1;
		m_recordcount=0;
		m_recordtype=0;
		m_currentdatapos=0;
		m_currentdatalen=0;
		m_tablesize=0;
		m_badpage=0;
		m_flag=0;
		F_SET(m_flag,PAGEUNUSED);
		m_lastrecordpos=-1;
		memset(m_span,0,456);
	}
	~PageHead_T(){;};
	int m_pos;
	int m_unused;
	int m_pagesize;
	int m_datasize;
	int m_nextrecordpage;
	int m_prerecordpage;
	int m_recordcount;
	int m_recordtype;
	int m_currentdatapos;
	int m_currentdatalen;
	int m_tablesize;        //only use dbhead
	int m_badpage;
	unsigned int m_flag;
	int m_lastrecordpos;
	char m_span[456];
};
class Page_T
{
public:
	Page_T()
	{
		m_Head.m_pos=0;
		m_Head.m_unused=1;
		m_data=NULL;
		m_ischange=false;
		m_isuse=false;
	}
	Page_T(PagePool_T *ppt,int index)
	{
		m_ppt=ppt;
		m_Head.m_pos=0;
		m_Head.m_unused=1;
		m_data=NULL;
		m_isuse=false;
		m_ischange=false;


	}
	~Page_T()
	{
		if(m_ischange)
			swapout(false);
		if(m_data)
			delete [] m_data;
	}
	struct PageHead_T m_Head;

	char *m_data;
	bool m_ischange;
	bool m_isuse;
	PagePool_T *m_ppt;
	
	bool swapin();
	bool swapout(bool isdel);
};

class PagePool_T
{
public:
	struct PagePoolHead_T{
		PagePoolHead_T()
		{
			m_version=1;
			m_PageCount=0;
			m_PageSize=0;
			memset(m_span,0,500);
		}
		~PagePoolHead_T()
		{
			;
		}
		int m_version;
		int m_PageCount;
		int	m_PageSize;
		char m_span[500];
	};


	PagePool_T()
	{
		m_LastPage==NULL;
		m_Head.m_PageSize=0;
		m_Head.m_PageCount=0;
		m_hd=NULL;
	}
	PagePool_T(int pagesize,int pagecount)
	{
		m_Head.m_PageSize=pagesize;
		m_Head.m_PageCount=pagecount;
		m_LastPage==NULL;
		m_hd=NULL;
	}
	~PagePool_T()
	{
		for(int i=0;i<m_PagePool.size();i++)
		{
			if(m_PagePool[i]!=NULL)
				delete m_PagePool[i];
		}

		if(m_hd)
			::CloseHandle(m_hd);
	}
	//FILE *m_pf;
	HANDLE m_hd;
//	std::list<Page_T *> m_PageList;
	std::vector<Page_T *> m_PagePool;
	std::list<Page_T *> m_FreePageList;


	Page_T *m_LastPage;

	ost::Mutex m_Mutex;
	ost::Mutex m_IOMutex;

	Page_T *GetFree(void){

		ost::MutexLock lock(m_Mutex);
		if(m_FreePageList.size()>0)
		{
			Page_T *pt=m_FreePageList.front();
			m_FreePageList.pop_front();
			pt->swapin();
			if(pt->m_isuse)
				return NULL;
			pt->m_isuse=true;
			return pt;
		}
		return NULL;
	}
	bool PutFree(Page_T *pt)
	{
		ost::MutexLock lock(m_Mutex);
		m_FreePageList.push_back(pt);
		return true;

	}

	bool RemoveFromFreePages(int pos)
	{
		ost::MutexLock lock(m_Mutex);
		std::list<Page_T *>::iterator it;
		for(it=m_FreePageList.begin();it!=m_FreePageList.end();it++)
		{
			if((*it)->m_Head.m_pos==pos)
	    		m_FreePageList.erase(it);
			return true;
		}
		return false;
		
	}

	Page_T *Get(int pos)
	{
		if((pos<0)||(pos>=m_Head.m_PageCount))
			return NULL;

		ost::MutexLock lock(m_Mutex);
		Page_T *pt=m_PagePool[pos];
		if(pt->m_isuse)
			return NULL;
		pt->m_isuse=true;
		if(pt->m_data==NULL)
			pt->swapin();
		if(F_ISSET(pt->m_Head.m_flag,PAGEUNUSED))
	    	RemoveFromFreePages(pos);
		return pt;
		
	}

	Page_T *Get(int pos,bool readonly)
	{
		if(!readonly)
			return Get(pos);
		if((pos<0)||(pos>=m_Head.m_PageCount))
			return NULL;
		Page_T *pt=m_PagePool[pos];
		return pt;
	}

	bool FlushBuffer()
	{
		if(m_hd==NULL)
			return false;
		return ::FlushFileBuffers(m_hd);
	}

	bool Put(Page_T *pt,bool isch,bool isdel)
	{
		ost::MutexLock lock(m_Mutex);
		pt->m_isuse=false;
		if(!isch)
		{
			if(isdel)
			{
				if(pt->m_data!=NULL)
				{
					delete [] pt->m_data;
					pt->m_data=NULL;
				}
			}
			return true;	
		}
		pt->m_ischange=true;
		pt->m_isuse=false;
		pt->m_Head.m_unused=0;
		F_CLR(pt->m_Head.m_flag,PAGEUNUSED);
		return pt->swapout(isdel);
	}
	void InitPool()
	{
		if(m_Head.m_PageCount<=0)
			return;
		m_PagePool.resize(m_Head.m_PageCount);
		for(int i=0;i<m_Head.m_PageCount;i++)
			m_PagePool[i]=NULL;
	}

	bool LoadPage(const char *filepath)
	{
	    ost::MutexLock lock(m_Mutex);
		DWORD dwcd=OPEN_EXISTING;
		HANDLE hd=::CreateFile(filepath,GENERIC_WRITE|GENERIC_READ,FILE_SHARE_READ|FILE_SHARE_WRITE,NULL,dwcd,FILE_ATTRIBUTE_NORMAL,NULL);
		if(hd == INVALID_HANDLE_VALUE)
		{
			printf("Open file failed error id:%d\n",::GetLastError());
			return false;
		}

		DWORD rlen=0;

		if(!::ReadFile(hd,&m_Head,sizeof(struct PagePoolHead_T),&rlen,NULL))
		{
		     printf("Read head failed errorid:%d\n",::GetLastError());
			 return false;
		}

		if(m_Head.m_version!=1)
		{
			puts("文件版本错误");
			return false;
		}
		if((m_Head.m_PageCount<=0)||(m_Head.m_PageSize<MINPAGESIZE))
		{
			puts("文件格出错");
			return false;

		}


		InitPool();
		char *buf = new char[m_Head.m_PageSize];

		memset(buf,0,m_Head.m_PageSize);

		for(int i=0;i<m_Head.m_PageCount;i++)
		{
			if(!::SetFilePointer(hd,i*m_Head.m_PageSize+sizeof(struct PagePoolHead_T),NULL,FILE_BEGIN))
			{
				printf("Load page -- move file pointer failed errorid:%d\n",::GetLastError());
				return false;
			}
			if(!::ReadFile(hd,buf,sizeof(PageHead_T),&rlen,NULL))
			{
				printf("Read file failed errorid:%d\n",::GetLastError());
				delete [] buf;
				return false;
			}
			if(((PageHead_T*)buf)->m_pos!=i)
			{
				printf("Load page info failed\n");
				delete [] buf;
				return false;
			}

			Page_T * pg=new Page_T(this,i);
			memcpy(&(pg->m_Head),buf,sizeof(PageHead_T));
			if(((PageHead_T*)buf)->m_unused==1)
				m_FreePageList.push_back(pg);

			m_PagePool[i]=pg;
            			
		}

		delete [] buf;
		m_hd=hd;

		return true;

	}

	bool CreatePage(const char * filepath,bool overlay)
	{
		ost::MutexLock lock(m_Mutex);
		InitPool();

		DWORD dwcd;
		if(overlay)
			dwcd=CREATE_ALWAYS;
		else
			dwcd=CREATE_NEW;
		
		HANDLE hd=::CreateFile(filepath,GENERIC_WRITE|GENERIC_READ,FILE_SHARE_READ|FILE_SHARE_WRITE,NULL,dwcd,FILE_ATTRIBUTE_NORMAL,NULL);
		if(hd == INVALID_HANDLE_VALUE)
		{
			printf("Create file failed:%d\n",::GetLastError());
			return false;
		}

		LONG size=m_Head.m_PageSize * m_Head.m_PageCount+sizeof(PagePoolHead_T);
		if((size>MAXINT-2)||(size<1))
		{
			printf("Size too big");
			return false;
		}

		

		DWORD pos=::SetFilePointer(hd,size,NULL,FILE_BEGIN);
		if(pos==INVALID_SET_FILE_POINTER)
		{
			printf("Set file size failed,error:%d\n",::GetLastError());
			return false;
		}
		::SetEndOfFile(hd);

		::SetFilePointer(hd,0,0,FILE_BEGIN);

		DWORD dwlen=0;

		if(!::WriteFile(hd,&m_Head,sizeof(PagePoolHead_T),&dwlen,NULL))
		{
			printf("Write head failed errorid:%d\n",::GetLastError());
			return false;
		}

		char *buf = new char[m_Head.m_PageSize];

		memset(buf,0,m_Head.m_PageSize);

		for(int i=0;i<m_Head.m_PageCount;i++)
		{
			((PageHead_T *)buf)->m_pos=i;
			((PageHead_T *)buf)->m_unused=1;
			 F_SET(((PageHead_T *)buf)->m_flag,PAGEUNUSED);
			((PageHead_T *)buf)->m_datasize=m_Head.m_PageSize-sizeof(struct PageHead_T);
			((PageHead_T *)buf)->m_pagesize=m_Head.m_PageSize;
			((PageHead_T *)buf)->m_lastrecordpos=-1;
			((PageHead_T *)buf)->m_prerecordpage=-1;

			if(!::WriteFile(hd,buf,m_Head.m_PageSize,&dwlen,NULL))
			{
				printf("Write file failed errorid:%d\n",::GetLastError());
				delete [] buf;
				::CloseHandle(hd);
				return false;
			}

			Page_T * pg=new Page_T(this,i);
			if(pg)
			{
				memcpy(&(pg->m_Head),buf,sizeof(PageHead_T));
				m_FreePageList.push_back(pg);

				m_PagePool[i]=pg;
			}
            			
		}

		delete [] buf;
		::SetFilePointer(hd,0,0,FILE_BEGIN);

		m_hd=hd;

		return true;


	}

	bool AddNewPages(int count)
	{
		if(count<1)
			return false;
		if(m_hd==NULL)
			return false;
		LONG size=m_Head.m_PageSize * (m_Head.m_PageCount+count)+sizeof(PagePoolHead_T);
		if((size>MAXINT-2)||(size<1))
		{
			printf("Size too big");
			return false;
		}

		ost::MutexLock lock(m_Mutex);


/*		m_PagePool.resize(m_Head.m_PageCount+count);
		for(int c=m_Head.m_PageCount;c<m_Head.m_PageCount+count;c++)
			m_PagePool[c]=NULL;
*/
		DWORD oldend=::SetFilePointer(m_hd,0,NULL,FILE_END);
		if(oldend==INVALID_SET_FILE_POINTER)
		{
			printf("Seek file failed errid:%d\n",::GetLastError());
			return false;
		}

		size=m_Head.m_PageSize*count;

		DWORD newpos=::SetFilePointer(m_hd,size,NULL,FILE_CURRENT);
		if(newpos==INVALID_SET_FILE_POINTER)
		{
			printf("Seek file failed2 errid:%d\n",::GetLastError());
			return false;

		}

		if(newpos!=m_Head.m_PageSize * (m_Head.m_PageCount+count)+sizeof(PagePoolHead_T))
		{
			printf("Create file sapce failed newpos:%d\n",newpos);
			newpos=::SetFilePointer(m_hd,oldend,NULL,FILE_BEGIN);
			::SetEndOfFile(m_hd);
			return false;
		}

		::SetEndOfFile(m_hd);

		newpos=::SetFilePointer(m_hd,oldend,NULL,FILE_BEGIN);
		
		if(newpos==INVALID_SET_FILE_POINTER)
		{
			printf("Seek file failed3 errid:%d\n",::GetLastError());
			return false;

		}

		char *buf = new char[m_Head.m_PageSize];

		memset(buf,0,m_Head.m_PageSize);

		m_PagePool.resize(m_Head.m_PageCount+count);
		for(int i=m_Head.m_PageCount;i<m_Head.m_PageCount+count;i++)
			m_PagePool[i]=NULL;

		DWORD dwlen=0;

		for(int i=m_Head.m_PageCount;i<m_Head.m_PageCount+count;i++)
		{
			((PageHead_T *)buf)->m_pos=i;
			((PageHead_T *)buf)->m_unused=1;
			 F_SET(((PageHead_T *)buf)->m_flag,PAGEUNUSED);
			((PageHead_T *)buf)->m_datasize=m_Head.m_PageSize-sizeof(struct PageHead_T);
			((PageHead_T *)buf)->m_pagesize=m_Head.m_PageSize;
			((PageHead_T *)buf)->m_lastrecordpos=-1;
			((PageHead_T *)buf)->m_prerecordpage=-1;



			if(!::WriteFile(m_hd,buf,m_Head.m_PageSize,&dwlen,NULL))
			{
				printf("Write file failed errorid:%d\n",::GetLastError());
				delete [] buf;
				::CloseHandle(m_hd);
				return false;
			}

			Page_T * pg=new Page_T(this,i);
			pg->m_Head.m_pos=i;
			if(pg)
			{
				memcpy(&(pg->m_Head),buf,sizeof(PageHead_T));
				m_FreePageList.push_back(pg);
				
				m_PagePool[i]=pg;
			}

            			
		}

		m_Head.m_PageCount+=count;
        delete [] buf;
		::SetFilePointer(m_hd,0,0,FILE_BEGIN);
		if(!::WriteFile(m_hd,&m_Head,sizeof(PagePoolHead_T),&dwlen,NULL))
		{
			printf("Write head failed errorid:%d\n",::GetLastError());
			return false;
		}

		::SetFilePointer(m_hd,0,0,FILE_BEGIN);

		return true;
	}
	void PutPageSize(int PageCount,int PageSize)
	{
		if((PageSize<MINPAGESIZE)||(PageCount<=0))
			return;
		m_Head.m_PageCount=PageCount;
		m_Head.m_PageSize=PageSize;
	}
	void GetPageSize(int &PageCount,int &PageSize)
	{
		PageCount=m_Head.m_PageCount;
		PageSize=m_Head.m_PageSize;
	}

	PagePoolHead_T &GetPagePoolHead()
	{
		return m_Head;
	}
protected:
		struct PagePoolHead_T m_Head;


};
bool Page_T::swapout(bool isdel)
{
	ost::MutexLock lock(m_ppt->m_IOMutex);
	if(m_ischange)
	{
		int pos=m_Head.m_pos*m_ppt->GetPagePoolHead().m_PageSize+sizeof(struct PagePool_T::PagePoolHead_T);
		m_ischange=false;
		if(!::SetFilePointer(m_ppt->m_hd,pos,NULL,FILE_BEGIN))
			return false;
		DWORD size=0;
		if(!::WriteFile(m_ppt->m_hd,&m_Head,sizeof(PageHead_T),&size,NULL))
			return false;
		if(m_data!=NULL)
		{
			if(!::WriteFile(m_ppt->m_hd,m_data,m_ppt->GetPagePoolHead().m_PageSize-sizeof(PageHead_T),&size,NULL))
			{
				printf("swap(write) failed errorid:%d\n",::GetLastError());
				return false;
			}
		}
	}

	if(isdel)
		if(m_data!=NULL)
		{
			delete [] m_data;
			m_data=NULL;
		}

	return true;

}
bool Page_T::swapin()
{
	ost::MutexLock lock(m_ppt->m_IOMutex);

	if(m_data==NULL)
		m_data=new char[m_ppt->GetPagePoolHead().m_PageSize-sizeof(PageHead_T)];
	memset(m_data,0,m_ppt->GetPagePoolHead().m_PageSize-sizeof(PageHead_T));

	int pos=m_Head.m_pos*m_ppt->GetPagePoolHead().m_PageSize+sizeof(struct PagePool_T::PagePoolHead_T)+sizeof(PageHead_T);

	if(!::SetFilePointer(m_ppt->m_hd,pos,NULL,FILE_BEGIN))
	{
		printf("swapin failed move file pointor error errorid :%d\n",::GetLastError());
		return false;
	}

	DWORD size=0;

	if(!::ReadFile(m_ppt->m_hd,m_data,m_ppt->GetPagePoolHead().m_PageSize-sizeof(PageHead_T),&size,NULL))
	{
		printf("swapin failed read file failed:%d\n",::GetLastError());
		return false;
	}

	return true;
}

struct TableDataHead_T{
	TableDataHead_T()
	{
		m_datalen=0;
		m_nextpage=-1;
	}
	int m_datalen;
	int m_nextpage;
};

bool DB_T::Load()
{

	if(m_headpool!=NULL)
		return false;
	if(m_pagepool!=NULL)
		return false;
	m_headpool = new PagePool_T();
	if(m_headpool==NULL)
		return false;


	if(!m_headpool->LoadPage(m_dbheadfile.c_str()))
		return false;


	Page_T *phead=m_headpool->Get(0);
	if(phead==NULL)
		return false;
	if(phead->m_data==NULL)
		return false;

	m_dbh=(DBhead_T*)phead->m_data;

	m_pagepool=new PagePool_T();
	if(m_pagepool==NULL)
		return false;


	if(!m_pagepool->LoadPage(m_dbh->m_dbfilename))
		return false;
	this->m_dbfilename=m_dbh->m_dbfilename;




	m_headpool->Put(phead,false,false);

	for(int i=1;i<m_headpool->GetPagePoolHead().m_PageCount;i++)
	{
		Page_T *pt=m_headpool->Get(i,false);
		if(pt==NULL)
			return false;

		if((pt->m_Head.m_unused==1)||(pt->m_Head.m_badpage==1))
		{
			m_headpool->Put(pt,false,true);
			m_headpool->PutFree(pt);
			continue;
		}

		Table_T *ptable=new Table_T();
		if(!ptable)
			return false;

		if(!ptable->CreateObjByPage(pt))
			return false;
		if(!m_headpool->Put(pt,false,true))
			return false;

		if(ptable->m_type.m_monitorid.empty())
			return false;

		ptable->m_pagepool=m_pagepool;

		if(!m_tables.insert(ptable->m_type.m_monitorid,ptable))
			return false;

	}


	

	return true;
}
bool DB_T::FlashData()
{
	if(m_headpool==NULL)
		return false;

	TABLEPOS::iterator it;
	while(m_tables.findnext(it))
	{
		(*it).getvalue()->FlushData();
	}

	return true;
}
bool DB_T::CreateNew(string dbheadfile,string dbfilename,int pagesize,int pagecount)
{
	if(m_headpool!=NULL)
		return false;
	m_headpool=new PagePool_T(1024*2,1000);
	if(!m_headpool)
		return false;
	this->m_dbfilename=dbfilename;
	this->m_dbheadfile=dbheadfile;
	if(!m_headpool->CreatePage(dbheadfile.c_str(),true))
		return false;

	Page_T *ppt=m_headpool->Get(0);
	if(ppt==NULL)
		return false;

	DBhead_T *pdh=(DBhead_T *)ppt->m_data;
	pdh->m_currentid=0;
	pdh->m_monitorcount=0;
	pdh->m_version=1;
	strcpy(pdh->m_dbfilename,dbfilename.c_str());

	m_headpool->Put(ppt,true,true);

	m_pagepool =new PagePool_T(pagesize,pagecount);
	if(!m_pagepool)
		return false;
	if(!m_pagepool->CreatePage(dbfilename.c_str(),true))
		return false;

	return true;
}

bool Table_T::CreateObjByPage(Page_T *pgage)
{
	if(pgage==NULL)
		return false;
	if(pgage->m_data==NULL)
		return false;

	if((pgage->m_Head.m_tablesize>pgage->m_Head.m_datasize)||(pgage->m_Head.m_tablesize<sizeof(Page_T)))
		return false;

	char *pt=pgage->m_data;
	memmove(&m_createtime,pt,sizeof(svutil::TTime));
	pt+=sizeof(svutil::TTime);

	memmove(&m_firstpage,pt,sizeof(int));
	pt+=sizeof(int);

	memmove(&m_currentpage,pt,sizeof(int));
	pt+=sizeof(int);

	memmove(&m_hasindex,pt,sizeof(int));
	pt+=sizeof(int);

	memmove(&(m_type.m_monitortype),pt,sizeof(int));
	pt+=sizeof(int);

	m_type.m_monitorid=pt;
	pt+=m_type.m_monitorid.size()+1;

	int count=0;

	memmove(&count,pt,sizeof(int));
	pt+=sizeof(int);
	if(pt-pgage->m_data>pgage->m_Head.m_tablesize)
		return false;


    DataType_T *pdt=NULL;
	for(int i=0;i<count;i++)
	{
		pdt=new DataType_T();

		if(pdt==NULL)
			return false;
		memmove(pdt,pt,sizeof(DataType_T));
		m_type.m_data.push_back(pdt);

		pt+=sizeof(DataType_T);
		if(pt-pgage->m_data>pgage->m_Head.m_tablesize)
			return false;

	}

	if(m_hasindex)
	{
		memmove(&count,pt,sizeof(int));
		pt+=sizeof(int);
		for(int i=0;i<count;i++)
		{
			pdt=new DataType_T();
			if(pdt==NULL)
				return false;
			memmove(pdt,pt,sizeof(DataType_T));

			m_indextype.push_back(pdt);
			pt+=sizeof(DataType_T);
			if(pt-pgage->m_data>pgage->m_Head.m_tablesize)
				return false;
		}
	}

	m_page=pgage;



	return true;

}

int Table_T::GetRawDataSize()
{
	int len=0,tlen=sizeof(int);

	len+=sizeof(svutil::TTime);
	len+=tlen;
	len+=tlen;
	len+=tlen;
	len+=tlen;

	len+=m_type.m_monitorid.size()+1;
    
	len+=tlen;
	int count=m_type.m_data.size();
	len+=sizeof(DataType_T)*count;

	if(m_hasindex)
	{
		len+=tlen;
		count=m_indextype.size();
		len+=sizeof(DataType_T)*count;
	}

	return len;

}

bool Table_T::FlushData()
{
	Page_T *pt=m_page->m_ppt->Get(m_page->m_Head.m_pos);
	if(pt==NULL)
		return false;
	if(pt->m_data==NULL)
	{
		m_page->m_ppt->Put(pt,false,true);
		return false;
	}

	int tlen=sizeof(int);
	int count=0;
	int tablesize=GetRawDataSize();

	if(pt->m_Head.m_datasize<tablesize)
	{
		pt->m_Head.m_badpage=1;
		F_SET(pt->m_Head.m_flag,BADPAGE);
		m_page->m_ppt->Put(pt,true,true);
		return false;
	}

	char *pdata=pt->m_data;

	memmove(pdata,&m_createtime,sizeof(svutil::TTime));
	pdata+=sizeof(svutil::TTime);

	memmove(pdata,&m_firstpage,tlen);
	pdata+=tlen;

	memmove(pdata,&m_currentpage,tlen);
	pdata+=tlen;

	memmove(pdata,&m_hasindex,tlen);
	pdata+=tlen;

	memmove(pdata,&m_type.m_monitortype,tlen);
	pdata+=tlen;

	strcpy(pdata,m_type.m_monitorid.c_str());
	pdata+=m_type.m_monitorid.size();
	pdata[0]='\0';
	pdata++;

	count=m_type.m_data.size();
	memmove(pdata,&count,tlen);
	pdata+=tlen;

	for(int i=0;i<count;i++)
	{
		memmove(pdata,m_type.m_data[i],sizeof(DataType_T));
		pdata+=sizeof(DataType_T);
	}

	if(m_hasindex)
	{
		count=m_indextype.size();
		memmove(pdata,&count,tlen);
		pdata+=tlen;
		for(int i=0;i<count;i++)
		{
			memmove(pdata,m_indextype[i],sizeof(DataType_T));
			pdata+=sizeof(DataType_T);
		}
	}

	pt->m_Head.m_tablesize=tablesize;

	return m_page->m_ppt->Put(pt,true,true);

}

bool DB_T::InsertTable(string monitorid,int monitortype)
{
	if(monitorid.empty()||monitortype<1)
		return false;

	if(m_tables.find(monitorid)!=NULL)
	{
		puts("Table has exist");
		return false;
	}

	if((m_pagepool==NULL)||(m_headpool==NULL))
		return false;



	Table_T *ptable=new Table_T();
	if(!ptable)
		return false;

	ptable->m_hasindex=0;

	Page_T *firstpage=m_pagepool->GetFree();
	if(firstpage==NULL)
	{
		if(!m_pagepool->AddNewPages(1000))
		{
			puts("Add new page failed");
			return false;
		}
		firstpage=m_pagepool->GetFree();
		if(firstpage==NULL)
		{
			puts("Get data free page failed");
			return false;
		}
	}
	ptable->m_firstpage=firstpage->m_Head.m_pos;
	ptable->m_currentpage=ptable->m_firstpage;
	m_pagepool->Put(firstpage,true,false);

	ptable->m_type.m_monitorid=monitorid;
	ptable->m_type.m_monitortype=monitortype;

	/***************************************************
	需要跟monitortemplet连起来，这里的代码只做模拟
	****************************************************/
	DataType_T *pdtt = new DataType_T();
	pdtt->type=DataType_T::inttype;
	strcpy(pdtt->label,"CPU利用率");
	strcpy(pdtt->unit,"%%");
	strcpy(pdtt->name,"utilize");
	ptable->m_type.m_data.push_back(pdtt);

	pdtt = new DataType_T();

	pdtt->type=DataType_T::stringtype;
	strcpy(pdtt->label,"描述");
//	strcpy(pdtt->unit,"%%");
	strcpy(pdtt->name,"description");
	ptable->m_type.m_data.push_back(pdtt);
	ptable->m_type.m_data.push_back(pdtt);

	pdtt = new DataType_T();

	pdtt->type=DataType_T::floattype;
	strcpy(pdtt->label,"这是一个float值");
	strcpy(pdtt->unit,"元");
	strcpy(pdtt->name,"floatvalue");
	ptable->m_type.m_data.push_back(pdtt);

	/**********************************************************/



	ptable->m_createtime=svutil::TTime::GetCurrentTimeEx();
	ptable->m_page=m_headpool->GetFree();
	if(ptable->m_page==NULL)
	{
		if(m_headpool->GetPagePoolHead().m_PageCount<MAXTABLE)
		{
			int flen=MAXTABLE-m_headpool->GetPagePoolHead().m_PageCount;
			int count=flen > 10 ? 10 : flen;
			if(!m_headpool->AddNewPages(count))
			{
				printf("Add new page failed");
				delete ptable;
				return false;
			}
			ptable->m_page=m_headpool->GetFree();
			if(ptable->m_page==NULL)
			{
				puts("Get page failed");
				return false;
			}
		}
	}
	m_headpool->Put(ptable->m_page,false,false);
	ptable->m_pagepool=m_pagepool;

	if(!ptable->FlushData())
	{
		puts("flash data failed");
	}

	m_tables[monitorid]=ptable;

	return true;

}

bool DB_T::InsertRecord(string monitorid,int state,char *data,int datalen)
{
	Table_T **ptable=m_tables.find(monitorid);

	if(ptable==NULL)
	{
		puts("Table no exist");
		return false;
	}

	return (*ptable)->InsertRecord(state,data,datalen);
    
}
struct RecordHead_T
{
	int prercord;
	int state;
	svutil::TTime createtime;
	int datalen;

};

bool Table_T::InsertRecord(int state,char *data,int len)
{
	//need lock;

	Page_T *ppage=m_pagepool->Get(this->m_currentpage);


	if(ppage==NULL)
	{
		puts("Get page failed");
		return false;
	}

	if(F_ISSET(ppage->m_Head.m_flag,BADPAGE))
		return false;

	RecordHead_T rht={0};
	rht.state=state;
	rht.createtime=svutil::TTime::GetCurrentTimeEx();
	rht.datalen=len;
	if(ppage->m_Head.m_currentdatalen==0)
	{
		if(F_ISSET(ppage->m_Head.m_flag,CROSSPAGEUP))
	    	rht.prercord=-1;
		else
			rht.prercord=-2;
	}
	else
    	rht.prercord=ppage->m_Head.m_currentdatapos-ppage->m_Head.m_currentdatalen;
	int dlen=sizeof(RecordHead_T)+len;

	char *pt=ppage->m_data+ppage->m_Head.m_currentdatapos;
	int freesize=ppage->m_Head.m_datasize-ppage->m_Head.m_currentdatapos;

	ppage->m_Head.m_lastrecordpos=ppage->m_Head.m_currentdatapos;

	if(dlen<freesize)
	{
		memcpy(pt,&rht,sizeof(RecordHead_T));
		pt+=sizeof(RecordHead_T);	
		memcpy(pt,data,len);

		ppage->m_Head.m_currentdatalen=dlen;
		ppage->m_Head.m_currentdatapos+=dlen;
		ppage->m_Head.m_recordcount++;

		m_pagepool->Put(ppage,true,false);

		m_time=svutil::TTime::GetCurrentTimeEx();
		if(m_state!=state)
		{
			m_laststatechangetime=m_time;
			m_laststatekeeptimes=1;
		}else
			m_laststatekeeptimes++;

    	m_state=state;


		return true;


	}else if(freesize>=sizeof(RecordHead_T))
	{
		memcpy(pt,&rht,sizeof(RecordHead_T));
		pt+=sizeof(RecordHead_T);
		F_SET(ppage->m_Head.m_flag,CROSSPAGEDOWN);

		freesize=freesize-sizeof(RecordHead_T);
		char *pdd=data;
		int mlen=len;
		if(freesize>0)
		{
			memcpy(pt,pdd,freesize);
			mlen=len-freesize;
			pt+=freesize;
			pdd+=freesize;
		}
		int pos=0;
		Page_T *ptem=NULL;
		while(true)
		{
			Page_T *pnext=m_pagepool->GetFree();
			if(pnext==NULL)
			{
				if(!m_pagepool->AddNewPages(1000))
				{
					puts("Add new page failed");
					F_SET(ppage->m_Head.m_flag,PAGEFULL);
					F_SET(ppage->m_Head.m_flag,BADPAGE);
					m_pagepool->Put(ppage,true,true);
					return false;
				}
				pnext=m_pagepool->GetFree();
				if(pnext==NULL)
				{
					puts("Get new page failed");
					F_SET(ppage->m_Head.m_flag,PAGEFULL);
					F_SET(ppage->m_Head.m_flag,BADPAGE);
					m_pagepool->Put(ppage,true,true);
					return false;

				}
			}
			if(pos==0)
			{
				ppage->m_Head.m_nextrecordpage=pnext->m_Head.m_pos;
				pnext->m_Head.m_prerecordpage=ppage->m_Head.m_pos;
//				ptem=pnext;
			}else
			{
				pnext->m_Head.m_prerecordpage=ptem->m_Head.m_pos;
				ptem->m_Head.m_nextrecordpage=pnext->m_Head.m_pos;
//				ppage->m_Head.m_recordcount++;
				m_pagepool->Put(ptem,true,true);
			}

			ptem=pnext;
			pos++;

			F_SET(pnext->m_Head.m_flag,CROSSPAGEUP);

			if(mlen<=pnext->m_Head.m_datasize)
			{
				memmove(pnext->m_data,pdd,mlen);
				pdd+=mlen;

			   pnext->m_Head.m_currentdatalen=0;
		       pnext->m_Head.m_currentdatapos=mlen;

			   ppage->m_Head.m_recordcount++;
			   m_pagepool->Put(ppage,true,true);
			   m_pagepool->Put(pnext,true,false);
			   m_currentpage=pnext->m_Head.m_pos;

			//   FlushData();

			   m_time=svutil::TTime::GetCurrentTimeEx();
			   m_state=state;
			   if(m_state!=state)
			   {
				   m_laststatechangetime=m_time;
				   m_laststatekeeptimes=1;
			   }else
				   m_laststatekeeptimes++;

			   return true;
			}else{
				F_SET(pnext->m_Head.m_flag,CROSSPAGEDOWN);
				F_SET(pnext->m_Head.m_flag,PAGEFULL);
				memmove(pnext->m_data,pdd,pnext->m_Head.m_pagesize);
				pdd+=pnext->m_Head.m_pagesize;

			   pnext->m_Head.m_currentdatalen=0;
		       pnext->m_Head.m_currentdatapos=pnext->m_Head.m_pagesize;

			   mlen=mlen-pnext->m_Head.m_pagesize;
			}


		}

	}else
	{
		F_SET(ppage->m_Head.m_flag,PAGEFULL);
		Page_T *pnext=m_pagepool->GetFree();
		if(pnext==NULL)
		{
			if(!m_pagepool->AddNewPages(1000))
			{
				puts("Add new page failed");
				m_pagepool->Put(ppage,true,true);
				return false;
			}
			pnext=m_pagepool->GetFree();
			if(pnext==NULL)
			{
				puts("Get new page failed");
				m_pagepool->Put(ppage,true,true);
				return false;

			}
		}

		ppage->m_Head.m_lastrecordpos=ppage->m_Head.m_currentdatapos-ppage->m_Head.m_currentdatalen;

		ppage->m_Head.m_nextrecordpage=pnext->m_Head.m_pos;
		pnext->m_Head.m_prerecordpage=ppage->m_Head.m_pos;
		m_currentpage=pnext->m_Head.m_pos;

		m_pagepool->Put(ppage,true,true);
		m_pagepool->Put(pnext,true,false);
		//FlushData();
		return InsertRecord(state,data,len);

	}

	return true;
}

int DB_T::QueryRecordByTime(string monitorid,svutil::TTime begin,svutil::TTime end,char *buf,int buflen)
{
	Table_T **ptable=m_tables.find(monitorid);

	if(ptable==NULL)
	{
		puts("Table no exist");
		return false;
	}

	return (*ptable)->QueryRecordByTime(begin,end,buf,buflen);

}

Page_T *Table_T::GetPreRecordPage(Page_T *ppage)
{
	if(ppage==NULL)
		return NULL;

	Page_T *pt=ppage;
	int i=0;

	
	do{
		int pos=pt->m_Head.m_prerecordpage;
		if(pos<0)
			return NULL;

		if(i>0)
	    	m_pagepool->Put(pt,false,false);

		i++;
		pt=m_pagepool->Get(pos);
		if(pt==NULL)
			return NULL;
		if(pt->m_data==NULL)
		{
			m_pagepool->Put(pt,false,true);
			return NULL;
		}
	}while(pt->m_Head.m_lastrecordpos<0);

	return pt;
}
Page_T *Table_T::GetNextPage(Page_T *ppage)
{
	if(ppage==NULL)
		return NULL;
	if(ppage->m_Head.m_nextrecordpage<1)
		return NULL;

	return m_pagepool->Get(ppage->m_Head.m_nextrecordpage);


}



int Table_T::QueryRecordByTime(svutil::TTime begin,svutil::TTime end,char *buf,int buflen)
{
	if(begin>end)
		return -1;

	Page_T *pt=this->m_pagepool->Get(this->m_currentpage);
	if(pt==NULL)
		return -2;

	if(pt->m_data==NULL)
	{
		m_pagepool->Put(pt,false,true);
		return -3;
	}
	if(pt->m_Head.m_lastrecordpos<0)
	{
		Page_T *ptemp=GetPreRecordPage(pt);
		if(ptemp==NULL)
			return -2;
		m_pagepool->Put(pt,false,false);
		pt=ptemp;
	}

	int i=0;

	char *ptbuf=buf;
	int pos=pt->m_Head.m_lastrecordpos;
	char *pc=pt->m_data+pos;
	int rlen=0,blen=buflen;
	int prep=0;
	while(true)
	{
		svutil::TTime rtime=((RecordHead_T*)pc)->createtime;
		if(rtime<begin)
		{
			m_pagepool->Put(pt,false,true);
			break;
		}
		prep=((RecordHead_T*)pc)->prercord;

		if((rtime>=begin)&&(rtime<=end))
		{
			rlen=GetSingleRecord(pt,pos,ptbuf,blen);
			if(rlen<0)
			{
				m_pagepool->Put(pt,false,true);
				return rlen;
			}
			ptbuf+=rlen;
			blen-=rlen;
			i++;
		}

		if(prep>=0)
		{
			pc=pt->m_data+prep;
			pos=prep;
			continue;
		}else
		{
			Page_T *ptem=GetPreRecordPage(pt);
			m_pagepool->Put(pt,false,true);
			if(ptem==NULL)
				break;
			pt=ptem;
			pos=pt->m_Head.m_lastrecordpos;
			pc=pt->m_data+pos;
		}

	}

	return i;

}
/*
int Table_T::GetSingleRecord(Page_T *ppage,int pos,char *buf,int buflen)
{
	if(ppage==NULL)
		return -1;
	if(ppage->m_data==NULL)
		return -2;
	if(pos<0||pos>ppage->m_Head.m_datasize)
		return -3;
	char *pt=ppage->m_data+pos;

	int len=((RecordHead_T*)pt)->datalen;
	len+=sizeof(RecordHead_T)-sizeof(int);

	if(len>buflen)
		return -4;
    
	if(len+sizeof(int)<ppage->m_Head.m_datasize-pos)
	{
		memmove(buf,pt+sizeof(int),len);
		return len;
	}else if(F_ISSET(ppage->m_Head.m_flag,CROSSPAGEDOWN))
	{
		int csize=ppage->m_Head.m_datasize-pos-sizeof(int);
		char *ptbuf=buf;
		memmove(ptbuf,pt+sizeof(int),csize);
		ptbuf+=csize;
		int mlen=len-csize;
		Page_T *ptpt=ppage;
		while(true)
		{
			Page_T *ptt=GetNextPage(ptpt);
			if(ptt==NULL)
				return -5;
			if(ptpt!=ppage)
			{
				m_pagepool->Put(ptpt,false,true);

			}
			if(F_ISSET(ptt->m_Head.m_flag,CROSSPAGEUP))
			{
				if(mlen<=ptt->m_Head.m_datasize)
				{
					memmove(ptbuf,ptt->m_data,mlen);
					m_pagepool->Put(ptt,false,true);
					return len;
				}else
				{
					memmove(ptbuf,ptt->m_data,ptt->m_Head.m_datasize);
					ptbuf+=ptt->m_Head.m_datasize;
					ptpt=ptt;
					mlen=mlen-ptt->m_Head.m_datasize;
				}

			}

		}
	}else
		return -6;

}
*/
int Table_T::GetSingleRecord(Page_T *ppage,int pos,char *buf,int buflen)
{
	if(ppage==NULL)
		return -1;
	if(ppage->m_data==NULL)
		return -2;
	if(pos<0||pos>ppage->m_Head.m_datasize)
		return -3;
	char *pt=ppage->m_data+pos;

	int len=((RecordHead_T*)pt)->datalen;
	len+=sizeof(RecordHead_T);

	if(len>buflen)
		return -4;
    
	if(len<ppage->m_Head.m_datasize-pos)
	{
		memmove(buf,pt,len);
		return len;
	}else if(F_ISSET(ppage->m_Head.m_flag,CROSSPAGEDOWN))
	{
		int csize=ppage->m_Head.m_datasize-pos;
		char *ptbuf=buf;
		memmove(ptbuf,pt,csize);
		ptbuf+=csize;
		int mlen=len-csize;
		Page_T *ptpt=ppage;
		while(true)
		{
			Page_T *ptt=GetNextPage(ptpt);
			if(ptt==NULL)
				return -5;
			if(ptpt!=ppage)
			{
				m_pagepool->Put(ptpt,false,true);

			}
			if(F_ISSET(ptt->m_Head.m_flag,CROSSPAGEUP))
			{
				if(mlen<=ptt->m_Head.m_datasize)
				{
					memmove(ptbuf,ptt->m_data,mlen);
					m_pagepool->Put(ptt,false,true);
					return len;
				}else
				{
					memmove(ptbuf,ptt->m_data,ptt->m_Head.m_datasize);
					ptbuf+=ptt->m_Head.m_datasize;
					ptpt=ptt;
					mlen=mlen-ptt->m_Head.m_datasize;
				}

			}

		}
	}else
		return -6;

}
int DB_T::QueryDyn(string monitorid,char *buf,int&buflen)
{
	Table_T **ptable=m_tables.find(monitorid);

	if(ptable==NULL)
	{
		puts("Table no exist");
		return false;
	}

	return (*ptable)->QueryDyn(buf,buflen);

}

int Table_T::QueryDyn(char *buf,int &buflen)
{
	int dlen=GetDynSize();
	if(buflen<dlen)
		return -4;

	char *pt=buf;
	memmove(pt,&m_time,sizeof(svutil::TTime));
	pt+=sizeof(svutil::TTime);
	memmove(pt,&m_state,sizeof(int));
	pt+=sizeof(int);
	svutil::TTimeSpan ts=svutil::TTime::GetCurrentTimeEx()-m_laststatechangetime;
	memmove(pt,&ts,sizeof(svutil::TTimeSpan));
	pt+=sizeof(svutil::TTimeSpan);
	memmove(pt,&m_laststatekeeptimes,sizeof(unsigned int));
	pt+=sizeof(unsigned int);
	strcpy(pt,m_displaystr.c_str());
	pt+=m_displaystr.size();
	pt[0]='\0';
	pt++;

	buflen=dlen;
	return dlen;
}

int Table_T::GetDynSize(void)
{
	int len=0,tlen=sizeof(int);


	len+=sizeof(svutil::TTime);  //createtime;
	len+=tlen;    //state;
	len+=sizeof(svutil::TTimeSpan);  //keep last state time
	len+=sizeof(unsigned int);
	len+=m_displaystr.size()+1;

	return len;

}

#endif
