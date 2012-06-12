#include "svapi.h"
#include <StringMap.h>
#include <Group.h>
#include "QueryData.h"

SVAPI_API
OBJECT	GetGroup(string groupid,string user,string addr)
{
	if(groupid.empty()||user.empty()||addr.empty())
		return INVALID_VALUE;
	if(user.size()>MAXUSERLEN)
		return INVALID_VALUE;

	SVDBQUERY query={0};
	query.len=sizeof(SVDBQUERY);
	query.datatype=S_GROUP;
	strcpy(query.idcuser,user.c_str());
	query.querytype=QUERY_GET;
	strcpy(query.qstr,groupid.c_str());

	QueryData qd;
	S_UINT len=0;
	char *pdata=NULL;

	if(qd.Query(&query,(void **)&pdata,len,addr))
	{
		Group *pg=new Group();
		if(pg)
		{
			OBJECT ret=INVALID_VALUE;
			try{
				if(pg->CreateObjectByRawData(pdata,len))
					ret=reinterpret_cast<OBJECT>(pg);
				delete [] pdata;
			}catch(...)
			{
				return INVALID_VALUE;
			}
			return ret;

		}
	}
	return INVALID_VALUE;

}
SVAPI_API 
bool GetAllGroupsInfo(PAIRLIST &retlist,string infoname,string user,string addr)
{
	if(user.empty()||addr.empty())
		return false;

	if(user.size()>MAXUSERLEN)
		return INVALID_VALUE;


	SVDBQUERY querybuf={0};
	querybuf.len = sizeof(SVDBQUERY);
	querybuf.querytype=QUERY_INFO;
	querybuf.datatype=S_GROUP;
	strcpy(querybuf.qstr,infoname.c_str());
	strcpy(querybuf.idcuser,user.c_str());

    QueryData qd;
	S_UINT len=0;
	char *pdata=NULL;
	if(qd.Query(&querybuf,(void **)&pdata,len,addr))
	{
		StringMap *pmtp=new StringMap();
		if(pmtp)
		{
			MAPNODE ret=INVALID_VALUE;
			if(pdata!=NULL)
			{
				try{
					if(pmtp->CreateObjectByRawData(pdata,len))
					{
	     				delete [] pdata;
						ret=reinterpret_cast<MAPNODE>(pmtp);
						bool bret=::EnumNodeAttrib(ret,retlist);
						delete pmtp;
						return bret;
					}
				}catch(...)
				{
					return false;
				}
			    delete [] pdata;
			}
			delete pmtp;
		}
	}
	
	return false;
}


SVAPI_API
OBJECT CreateGroup(void)
{

	try{
		Group *pg=new Group();
		if(pg==NULL)
			return INVALID_VALUE;
		OBJECT ret=reinterpret_cast<OBJECT>(pg);
		return ret;
	}catch(...)
	{
		return INVALID_VALUE;
	}

	return INVALID_VALUE;

}

SVAPI_API
MAPNODE GetGroupMainAttribNode(OBJECT groupobj)
{
	if(groupobj==INVALID_VALUE)
		return INVALID_VALUE;
	try{
		Group *pg=reinterpret_cast<Group *>(groupobj);
		MAPNODE ret=reinterpret_cast<MAPNODE>(&(pg->GetProperty()));
		return ret;

	}catch(...)
	{
		return INVALID_VALUE;
	}
	return INVALID_VALUE;


}


SVAPI_API
bool DeleteGroup(string groupid,string user,string addr)
{
	if(groupid.empty()||user.empty()||addr.empty())
		return false;

	SVDBQUERY querybuf={0};
	querybuf.len = sizeof(SVDBQUERY);
	querybuf.querytype=QUERY_DELETE;
	querybuf.datatype=S_GROUP;
	strcpy(querybuf.qstr,groupid.c_str());
	strcpy(querybuf.idcuser,user.c_str());

    QueryData qd;
	S_UINT len=0;
	char *pdata=NULL;
	if(qd.Query(&querybuf,(void **)&pdata,len,addr))
	{
		if(pdata)
		{
			if(len>0)
			{
				int *pret=(int*)pdata;
				if(*pret==SVDB_OK)
				{
					delete [] pdata;
					return true;
				}
			}
			delete [] pdata;
		}
	}

	return false;

}


SVAPI_API
bool CloseGroup(OBJECT &groupobj)
{
	if(groupobj==INVALID_VALUE)
		return INVALID_VALUE;
	try{
		Group *pg=reinterpret_cast<Group *>(groupobj);
		if(pg)
			delete pg;
        groupobj=INVALID_VALUE;
	}catch(...)
	{
		return false;
	}

	return true;

}

SVAPI_API
bool GetSubGroupsIDByGroup(OBJECT groupobj,std::list<string> &idlist)
{
	if(groupobj==INVALID_VALUE)
		return false;
	try{

		Group *pg=reinterpret_cast<Group *>(groupobj);
		
		WORDLIST gl=pg->GetSubGroups();
		WORDLIST::iterator it;
		for(it=gl.begin();it!=gl.end();it++)
			idlist.push_back((*it).getword());
		
	}catch(...)
	{
       return false;
	}
     return true;


}
SVAPI_API
bool GetSubGroupsIDByGroupEx(OBJECT groupobj,std::list<char *> &idlist)
{
	if(groupobj==INVALID_VALUE)
		return false;
	try{

		Group *pg=reinterpret_cast<Group *>(groupobj);
		
		WORDLIST gl=pg->GetSubGroups();
		WORDLIST::iterator it;
		for(it=gl.begin();it!=gl.end();it++)
			idlist.push_back(_strdup((*it).getword()));
		
	}catch(...)
	{
       return false;
	}
     return true;


}

SVAPI_API
bool GetSubEntitysIDByGroup(OBJECT groupobj,std::list<string> &idlist)
{
	if(groupobj==INVALID_VALUE)
		return false;
	try{

		Group *pg=reinterpret_cast<Group *>(groupobj);
		
		WORDLIST gl=pg->GetEntitys();
		WORDLIST::iterator it;
		for(it=gl.begin();it!=gl.end();it++)
			idlist.push_back((*it).getword());
		
	}catch(...)
	{
       return false;
	}
     return true;


}


SVAPI_API
string AddNewGroup(OBJECT groupobj,string pid,string user,string addr)
{
	if(groupobj==INVALID_VALUE)
		return "";
	if(user.empty()||addr.empty())
		return "";

	try{
		Group *pmtp=reinterpret_cast<Group *>(groupobj);
		S_UINT len=pmtp->GetRawDataSize();
		chen::buffer buf;
		if(!buf.checksize(len))
			return "";
		if(pmtp->GetRawData(buf,len)==NULL)
			return "";

		SVDBQUERY querybuf={0};
		querybuf.len = sizeof(SVDBQUERY);
		querybuf.querytype=QUERY_ADDNEW;
		querybuf.datatype=S_GROUP;
		strcpy(querybuf.qstr,pid.c_str());
		querybuf.datalen=len;
		strcpy(querybuf.idcuser,user.c_str());

		QueryData qd;
		SVDBRESULT ret={0};
		if(!qd.Update(&querybuf,buf,len,&ret,addr))
			return "";
		return ret.info;
	}catch(...)
	{
		return "";
	}


	return "";

}

SVAPI_API
bool SubmitGroup(OBJECT groupobj,string user,string addr)
{
	if(groupobj==INVALID_VALUE)
		return false;
	if(user.empty()||addr.empty())
		return false;
	try{
		Group *pmtp=reinterpret_cast<Group *>(groupobj);
		S_UINT len=pmtp->GetRawDataSize();
		chen::buffer buf;
		if(!buf.checksize(len))
			return false;
		if(pmtp->GetRawData(buf,len)==NULL)
			return false;

		SVDBQUERY querybuf={0};
		querybuf.len = sizeof(SVDBQUERY);
		querybuf.querytype=QUERY_UPDATE;
		querybuf.datatype=S_GROUP;
		strcpy(querybuf.qstr,pmtp->GetID().getword());
		querybuf.datalen=len;
		strcpy(querybuf.idcuser,user.c_str());

		QueryData qd;
		SVDBRESULT ret={0};
		return qd.Update(&querybuf,buf,len,&ret,addr);
	}catch(...)
	{
		return false;
	}


	return true;
}

