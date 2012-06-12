// test.cpp : Defines the entry point for the console application.
//

//#include "stdafx.h"
#include <vector>
//#include <xtree>
#include <hash_map>
#include <set>
#include "../libutil/hashtable.h"
#include "../libutil/strkey.h"
#include "../libutil/bstree.h"
#include "../libutil/buffer.h"
//#include <Windows.h>

#include <list>

#include <cc++/thread.h>
#include <cc++/file.h>
#include <cc++/socket.h>

//#include "../libcos/File.h"
#include "../svapi/svapi.h"
#include "../StringMap.h"
#include "../MonitorTemplet.h"
#include "../SVSE.h"
#include "../Monitor.h"
#include "../Group.h"
#include "../Entity.h"
#include "../Resource.h"
#include "RecordType_T.h"

#include "../svapi/svdbapi.h"
//#include "../svapi/time.h"
#include "../RecordType.h"
#include "../util.h"

#ifdef WIN32
#include "Winsock2.h"
#pragma comment(lib,"Ws2_32.lib")
#endif


using namespace std;
using namespace stdext;
using namespace svutil;
//using namespace cos;
using namespace ost;

int *p=NULL;

int &testa(void)
{
	p=new int;
	*p=20;
	return *p;

}

void test1()
{

	int &k=testa();
	printf("k is :%d\n",k);
	k=30;
	printf("p is :%d\n",*p);
}

void testmyhashtable()
{
	svutil::hashtable<svutil::word,int> myht(1379199);;
	word key;

	std::list<word> listw;
	
	char buf[100]={0};

	DWORD dwb=0,dwe=0;

	dwb=GetTickCount();
	for(int i=0;i<10000000;i++)
	{
		sprintf(buf,"0_%d_4_%d",i,i*2);
		myht.insert(buf,i);
	//	listw.push_back(buf);
		memset(buf,0,100);
	}
	dwe=::GetTickCount();

	printf("create time :%d\n",dwe-dwb);

	memset(buf,0,100);

	for(i=0;i<100;i++)
	{
		sprintf(buf,"0_%d_4_%d",i,i*i);
		printf("%s value :%d\n",buf,myht[buf]);
		memset(buf,0,100);
	}

//	myht.Debug_print();

	int z=34560;

	sprintf(buf,"0_%d_4_%d",z,z*2);

	dwb=::GetTickCount();
	int *k=myht.find(buf);
	dwe=::GetTickCount();

	if(k)
	  printf("the value is :%d,time is :%d\n",*k,dwe-dwb);
	else
		printf("find failed,time:%d\n",dwe-dwb);

	key=buf;
	std::list<word>::iterator it;
	dwb=::GetTickCount();
	for(it=listw.begin();it!=listw.end();it++)
	{
		if(key==*it)
		{
			puts("list find ok");
			break;
		}
	}
	dwe=::GetTickCount();

	printf("list cost time:%d\n",dwe-dwb);

	

    dwb=::GetTickCount();
	z=myht.getvaluecount();
	dwe=::GetTickCount();

	printf("z is :%d,time is :%d\n",z,dwe-dwb);

		::Sleep(2000000);




}
void testhashtable(void)
{
	svutil::hashtable<svutil::word,char *> strhash;
	strhash["first"]=_strdup("ddddd");
	strhash["second"]=_strdup("kkkk");
	char **p=strhash.find("firstw");
	if(p!=NULL)
		printf("find ok value:%s\n",*p);

	printf("first value:%s,second value:%s\n",(char *)strhash["first"],(char *)strhash["second"]);

}

void testelement()
{
	svutil::hashelement<svutil::word,int> element,et2;
	word key="first";
	element.setkey(key);
	element.setvalue(20);
	key="zzzzz";
	et2.setkey(key);
	et2.setvalue(30);

	if(element<et2)
		puts("it is <");
	else
		puts("it is >");

	printf("element key:%s,value:%d,et2 key is :%s,value:%d\n",(char *)element.getkey(),(int)element.getvalue(),
		(char *)et2.getkey(),(int)et2.getvalue());
}


void testtree()
{
	int tl[]={10,20,8,7,9,15,17,16,21,29,3,27,32,14};
	sorttree<int> testt;
	for(int i=0;i<14;i++)
		testt.InsertNode(tl[i]);

	printf("tree nodes :%d\n",testt.GetNodesCount());
	printf("tree leafs:%d\n",testt.GetLeafsCount());

}

/*void testfile()
{
	TFile file("D:\\test\\testfile.txt");

	char *p="this is a test!";

	TFile::Error err=file.append(p,strlen(p));
	if(err!=TFile::errSuccess)
		puts("写入文件失败!");
    
}*/

void testfile()
{
	ost::ThreadFile file("D:\\test\\testfile.txt");

	char *p="this is a test!\r\n";
	
	for(int i=0;i<10;i++)
	{
		ThreadFile::Error err=file.append(p,strlen(p));
		if(err!=File::errSuccess)
			puts("写文件失败");
		else
			puts("write ok");
	}


}

/*class SerialBase
{
public:
	virtual	unsigned int	GetDataSize(void)=0;
	virtual char*	GetDataBlock(char *lpbuf,unsigned int bufsize)=0;
	virtual	BOOL	CreateObjectByData(const char *lpbuf,unsigned int bufsize)=0;


};


class stringhashtable :public svutil::hashtable<svutil::word,svutil::word>, public SerialBase
{
public:
	stringhashtable(){;};
	stringhashtable(int size):svutil::hashtable<svutil::word,svutil::word>(size)
	{
		;
	}
	~stringhashtable(){;};
	int counts()
	{
		return getvaluecount();
	}

	unsigned int GetDataSize(void){ 
		unsigned int len=0;
		len+=sizeof(unsigned int)*2;
		iterator it;
		while(findnext(it))
		{
			len+=strlen((*it).getkey())+1;
			len+=strlen((*it).getvalue())+1;
		}

		return len;
	};
	char * GetDataBlock(char *lpbuf,unsigned int bufsize){
		unsigned int count=size();
		int tlen=sizeof(unsigned int);
		char *pt=lpbuf;

		memcpy(pt,&count,tlen);
		pt+=tlen;
		count=spacesize();
		memcpy(pt,&count,tlen);
		pt+=tlen;
		iterator it;
		unsigned int len=tlen*2;
		while(findnext(it))
		{
			char *p=(*it).getkey();
			len+=strlen(p)+1;
			if(len<=bufsize)
			{
				strcpy(pt,p);
				pt+=strlen(p);
				pt[0]='\0';
				pt++;
			}else
				return NULL;
			p=(*it).getvalue();

			len+=strlen(p)+1;
			if(len<=bufsize)
			{
				strcpy(pt,p);
				pt+=strlen(p);
				pt[0]='\0';
				pt++;
			}else
				return NULL;

		}

		return lpbuf;
	};
	BOOL CreateObjectByData(const char *lpbuf,unsigned int bufsize){
		if(!lpbuf)
			return false;
		int tlen=sizeof(unsigned int);
		unsigned int count=0,spcount=0;
		const char *pt=lpbuf;
		memcpy(&count,pt,tlen);
		pt+=tlen;
		memcpy(&spcount,pt,tlen);
		pt+=tlen;
		resetsize(spcount);
		unsigned int len=0;
		len+=tlen*2;
        for(unsigned int i=0;i<count;i++)
		{
			if(len+strlen(pt)+1>bufsize)
			{
				clearnodes();
				return false;
			}
            const char *p=pt;
			int n=strlen(p)+1;
			pt+=n;
			len+=n;
			if(len+strlen(pt)+1>bufsize)
			{
				clearnodes();
				return false;
			}
			insert(p,pt);
			n=strlen(pt)+1;
			pt+=n;
			len+=n;		

		}

		return true;

		
	};

};

void teststringhashtable()
{
	stringhashtable st(1247),clone;
	
	st.insert("this is a test","this value1");
	puts(st["this is a test"]);
	st["this is a test"]="value2";
	st["ssss"]="value3";
	st["dddd"]="value4";
	st["共产党"]="value5";
	st["国家总理"]="温家宝";

	printf("DataSize is :%d,value:%s\n",st.GetDataSize(),(char *)st["this is a test"]);

	char key[100]={0};
	char value[100]={0};
	for(int i=0;i<1000;i++)
	{
		sprintf(key,"12_%d_4_%d",i,i*i);
		sprintf(value,"%d",i);

		st[key]=value;
		memset(key,0,100);
		memset(value,0,100);
	}


	unsigned int len=st.GetDataSize();
	buffer buf;
	buf.checksize(len);
	if(!st.GetDataBlock(buf,len))
	{
		puts("GetData failed");
		return ;
	}

	if(!clone.CreateObjectByData(buf,len))
	{
		puts("Create obj failed");
		return;
	}

	stringhashtable::iterator it;
	while(clone.findnext(it))
	{
		printf("key is:%s -- value is:%s\n",(char *)(*it).getkey(),(char *)(*it).getvalue());
	}

	puts("successed");


}*/

void teststdhashtable()
{
/*	stdext::hash_map<svutil::word,svutil::word> hast;
	hast["ssss"]="value1";
	hast["ddds"]="value2";
	char buf[10]={0};
	strcpy(buf,"ssss");
	puts(hast[buf]);

*/

}

void visitint(const int &n)
{
	printf("v:%d\n",n);
}

void testmytree()
{
	int ele[]={34,28,45,42,23,24,29,35,59,55,62,18,27,77};
	svutil::sorttree<int> mytree;
	for(int i=0;i<14;i++)
		mytree.InsertNode(ele[i]);

	mytree.preorder(&visitint);


	int key=34;

	svutil::treenode<int> *pn=NULL;

/*	pn=mytree.findnextnode(key);
	if(pn==NULL)
		puts("find failed");
	else
		printf("find ok value is:%d\n",(int)pn->getelement());*/

	pn=mytree.findnextnode(key);
	while(pn!=NULL)
	{
		key=pn->getelement();
		printf("n:%d\n",key);
		pn=mytree.findnextnode(key);
	}

	


}

void testhashtableiterator()
{
	svutil::hashtable<svutil::word,svutil::word> test;
	test["first"]="1";
	test["second"]="2";
	test["third"]="3";

	char buf[100]={0};
	char value[100]={0};
	for(int i=0;i<1000;i++)
	{
		sprintf(buf,"12_%d_4_%d",i,i*i);
		sprintf(value,"%d",i);

		test[buf]=value;
		memset(buf,0,100);
		memset(value,0,100);
	}

	svutil::hashtable<svutil::word,svutil::word>::iterator it;
//	if(test.findfirst(it))
//	{
//		printf("the key:%s,the value:%s\n",(char*)(*it).getkey(),(char *)(*it).getvalue());
	   int n=0;
		while(test.findnext(it)!=NULL)
		{
			printf("the key:%s,the value:%s\n",(char*)(*it).getkey(),(char *)(*it).getvalue());
			n++;
		}

		printf("total count:%d\n",n);
		printf("hash table size:%d\n",test.size());
//	}
}

void testbuffer()
{
	svutil::buffer buf;
	if(!buf.checksize(30))
	{
		puts("alloc buffer failed");
		return;
	}

	strcpy(buf,"this is a buffer test");
	puts(buf);
	if(!buf.checksize(20))
	{
		puts("alloc buffer failed");
		return;
	}

	strcpy(buf,"next");
	puts(buf);
	if(!buf.resetsize(45))
	{
		puts("alloc buffer failed");
		return;
	}
	buf[(unsigned int)100]='a';
	buf[(unsigned int)101]='b';

	buffer buft=buf;

	puts(buf);

	puts(buft);
	printf("buf size:%d\n",buf.size());

}

void testcast(void)
{
	char *p="ddddddd";

	//int k=reinpreter_cast<int>(p);

	void *pv=static_cast<void *>(p);
	puts((char *)pv);

	unsigned long k=reinterpret_cast<unsigned long>(p);
	printf("k is :%d\n",k);

	//try{

    	char *pw=reinterpret_cast<char *>(200);
	  // printf("pw is :%s\n",pw);
/*	}catch(...)
	{
		puts("Exception");
	}*/
		puts("Exception");

}

void testsvapi(void)
{
	StringMap *map=new StringMap();
	map->insert("公司","游龙科技");
	(*map)["总裁"]="张泽军";
	(*map)["销售经理"]="曾云";
	(*map)["技术经理"]="王朋";

	StringMap ::iterator it;
	
	while(map->findnext(it))
	{
		printf("%s:\t\t%s\n",(*it).getkey().getword(),(*it).getvalue().getword());
	}

	MAPNODE obj=reinterpret_cast<MAPNODE>(map);
	
	PAIRLIST list;
	if(!EnumNodeAttrib(obj,list))
	{
		puts("Enum node failed");
		return;
	}

	PAIRLIST::iterator itp;
	for(itp=list.begin();itp!=list.end();itp++)
		printf("%s:\t%s\n",(*itp).name.c_str(),(*itp).value.c_str());

	string value="";

	if(!::FindNodeValue(obj,"技术经理",value))
		puts("find failed");
	else
		puts(value.c_str());

	printf("obj size :%d\n",::GetNodeSize(obj));

	if(!::DeleteNodeAttrib(obj,"销售经理"))
	   puts("Delete failed");
	else
	{
		if(::FindNodeValue(obj,"销售经理",value))
		{
			puts("delete fail");
			printf("find value:%s\n",value.c_str());
		}
		else
			puts("delete ok");

	}
	it.clear();
	while(map->findnext(it))
		printf("%s:\t\t%s\n",(*it).getkey().getword(),(*it).getvalue().getword());

	::CloseMapNodeObject(obj);






}

void testnewstringmap(void)
{
	StringMap str;

/*	str["one"]="1";
	str["two"]="2";
	str["three"]="3";
	str["four"]="4";
	str["five"]="5";
	str["six"]="6";
	str["seven"]="7";
	str["eight"]="8";

	str["two"]="9";*/


	StringMap::iterator it;
//	while(str.findnext(it))
//		printf("%s = %s\n",(*it).getkey().getword(),(*it).getvalue().getword());

/*	if(!str.erase("five"))
		puts("erase failed");
	str.erase("one");
	str.erase("three");

	if(!str.erase("dddd"))
		puts("erase dddd failed");*/

	it.clear();

	puts("after erase");

	while(str.findnext(it))
		printf("%s = %s\n",(*it).getkey().getword(),(*it).getvalue().getword());

	char key[100]={0};
	char value[100]={0};

	for(int i=0;i<100;i++)
	{
		sprintf(key,"key%d",i);
		sprintf(value,"%d",i);
		str[key]=value;
	}

	for(i=0;i<100;i++)
	{
		sprintf(key,"key%d",i);
		sprintf(value,"%d",i+2);
		str[key]=value;
	}

	memset(key,0,100);
	memset(value,0,100);

	for(i=0;i<100;i++)
	{
		if(i%2==0)
		{
			sprintf(key,"key%d",i);
			str.erase(key);
		}
	}

	memset(key,0,100);
	memset(value,0,100);

	for(i=0;i<100;i++)
	{
		if(i%2==0)
		{
			sprintf(key,"key%d",i);
			sprintf(value,"%d",i);
			str[key]=value;

		}
	}

	it.clear();

	puts("Begin");
	i=0;
	while(str.findnext(it))
	{
		printf("%s = %s\n",(*it).getkey().getword(),(*it).getvalue().getword());
		i++;
	}

	printf("total count :%d\n",i);



}

void testlistitemapi(void)
{
	
	STRMAPLIST strl;
	
	StringMap *pmap=new StringMap();
	pmap->insert("name","one");
	pmap->insert("second","2");
	StringMap *pm=new StringMap();
	pm->insert("name","two");


	strl.push_back(pmap);
	strl.push_back(pm);

	pm= new StringMap();
	pm->insert("name","three");
	strl.push_back(pm);

	pm = new StringMap();
	pm->insert("name","four");
	strl.push_back(pm);

	

	STRMAPLIST::iterator *it=new STRMAPLIST::iterator();
	(*it)=strl.begin();

	LISTITEM item;
	item.iterator=reinterpret_cast<OBJECT>(it);
	item.list=reinterpret_cast<OBJECT>(&strl);


	MAPNODE ma; 
	while((ma=::FindNext(item))!=INVALID_VALUE)
	{
		puts("***************item******************");
		string value;
		if(::FindNodeValue(ma,"name",value))
		{
			printf("value:%s\n",value.c_str());
			if(value.compare("four")==0)
			{
				puts("get two");
			/*	if(!::DeleteItem(item))
					puts("delete failed");
				else
					puts("delete ok");*/

				StringMap *pis=new StringMap();
				pis->insert("name","insertitem");
				MAPNODE mid=reinterpret_cast<MAPNODE>(pis);
				if(::InsertItemToList(item,mid))
					puts("insert ok");
				else
					puts("insert failed");

				break;
			}
		}
		else
			puts("find failed");

	}

	puts("//////////////////////////////////////////////////////////////////");

	STRMAPLIST::iterator ito;
	for(ito=strl.begin();ito!=strl.end();ito++)
	{
		printf("value:%s\n",(*ito)->find("name")->getword());
	}

	


    
/*	while(true)
	{
		MAPNODE mn=::FindNext(item);
		if(mn!=INVALID_VALUE)
		{
			printf("node size :%d\n",::GetNodeSize(mn));
			string value;
			if(::FindNodeValue(mn,"name",value))
				printf("value :%s\n",value.c_str());
			else
				puts("find value failed");
			if(!::DeleteItem(item))
				puts("delete failed");
			else
				puts("delete ok");
		}else
		{
			puts("find end");
			break;
		}
	}

	printf("strl size:%d\n",strl.size());

	printf("listitem size :%d\n",::GetListItemSize(item));

	MAPNODE newitem=::CreateNewMapNode();
	::AddItemToList(item,newitem);

	MAPNODE newitem2=::CreateNewMapNode();
	::AddItemToList(item,newitem2);

	printf("strl size :%d\n",strl.size());

*/

}

OBJECT CreateMonitorTemplettt(int id)
{
	MonitorTemplet *ml=new MonitorTemplet();
	ml->PutID(id);

	StringMap &prop=ml->GetProperty();
	prop.insert("Name","CPU");
	prop.insert("Label","Cpu");
	prop.insert("Description","监测CPU的使用率 远程NT,UNIX主机");
	prop.insert("N_ExecuteAssemblyName","NTPerfTest.dll");
	prop.insert("N_ExecuteFunc","GetAllCPURate");

	prop.insert("U_ExecuteAssemblyName","Monitor.dll");
	prop.insert("SiteViewPointNumer","1");
	prop.insert("VisioKey","monitor_machine");
	prop.insert("Class","telnet");

////////

	STRMAPLIST &param=ml->GetParameter();
	StringMap *pmap=new StringMap();
	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","主机名（所要监测的服务器）");
	pmap->insert("RunInMonitor","true");
	pmap->insert("name","_MachineName");
	pmap->insert("DataLength","100");

	pmap->insert("IsNumeric","false");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","210");
	pmap->insert("ControlHeight","21");

	param.push_back(pmap);

///////////////

	pmap=new StringMap();

	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","监测频率（监测器的监测频率）");
	pmap->insert("RunInMonitor","false");
	pmap->insert("name","_frequency");
	pmap->insert("DataLength","10");

	pmap->insert("IsNumeric","true");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","150");
	pmap->insert("ControlHeight","21");

	param.push_back(pmap);

//////////////

	pmap=new StringMap();

	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","标题（监测器名称）");
	pmap->insert("RunInMonitor","false");
	pmap->insert("name","mCurrentName");
	pmap->insert("DataLength","10");

	pmap->insert("IsNumeric","true");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","150");
	pmap->insert("ControlHeight","21");

	param.push_back(pmap);

/////////

	STRMAPLIST &adparam=ml->GetAdvanceParameter();
	pmap=new StringMap();

	pmap=new StringMap();
	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","主机名（所要监测的服务器）");
	pmap->insert("RunInMonitor","true");
	pmap->insert("name","_MachineName");
	pmap->insert("DataLength","100");

	pmap->insert("IsNumeric","false");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","210");
	pmap->insert("ControlHeight","21");

	adparam.push_back(pmap);

///////////////

	pmap=new StringMap();

	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","监测频率（监测器的监测频率）");
	pmap->insert("RunInMonitor","false");
	pmap->insert("name","_frequency");
	pmap->insert("DataLength","10");

	pmap->insert("IsNumeric","true");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","150");
	pmap->insert("ControlHeight","21");

	adparam.push_back(pmap);

//////////////

	pmap=new StringMap();

	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","标题（监测器名称）");
	pmap->insert("RunInMonitor","false");
	pmap->insert("name","mCurrentName");
	pmap->insert("DataLength","10");

	pmap->insert("IsNumeric","true");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","150");
	pmap->insert("ControlHeight","21");

	adparam.push_back(pmap);

///////////////

	STRMAPLIST &ret=ml->GetReturn();

	pmap=new StringMap();
	pmap->insert("Type","Float");
	pmap->insert("Name","utilization");
	pmap->insert("Label","CPU使用率(%)");
	pmap->insert("Primary","1");
	pmap->insert("Unit","(%)");
	pmap->insert("Drawtable","1");
	pmap->insert("Drawimage","1");
	
	ret.push_back(pmap);

//////////////

	StringMap **cond=ml->GetAlertCondition();

	pmap=new StringMap();
	pmap->insert("ShowText","错误（设置错误条件,满足此条件则该监测状态为错误，表现为红色）");
	pmap->insert("name","_errorParameter");
	pmap->insert("DefaultVlaue","[utilization == 100]");

	cond[0]=pmap;

//////////

	pmap=new StringMap();
	pmap->insert("ShowText","错误（设置错误条件,满足此条件则该监测状态为错误，表现为红色）");
	pmap->insert("name","_warningParameter");
	pmap->insert("DefaultVlaue","[utilization == 100]");

	cond[1]=pmap;

////////////////

	pmap=new StringMap();
	pmap->insert("ShowText","错误（设置错误条件,满足此条件则该监测状态为错误，表现为红色）");
	pmap->insert("name","_goodParameter");
	pmap->insert("DefaultVlaue","[utilization == 100]");

	cond[2]=pmap;

	OBJECT obj=reinterpret_cast<OBJECT>(ml);
	return obj;
}

void testmonitortempletapi(void)
{
//	OBJECT mlobj=::CreateMonitorTemplettt(20);
//	OBJECT mlobj=::CreateMonitorTemplet(10);
	OBJECT mlobj=::GetMonitorTemplet(161,"default","127.0.0.1");

	if(mlobj==INVALID_VALUE)
	{
		puts("Get monitor templet failed");
		return;
	}

	MAPNODE ma=::GetMTMainAttribNode(mlobj);

	if(ma==INVALID_VALUE)
	{
		puts("Get ma failed");
		return;
	}

	PAIRLIST alist;

	puts("**************************Property*************************");

	if(!::EnumNodeAttrib(ma,alist))
	{
		puts("Get ma list failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=alist.begin();it!=alist.end();it++)
	{
		printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());
	}

	LISTITEM param;
	if(!::FindMTParameterFirst(mlobj,param))
	{
		puts("Find mtp first error");
		return;
	}
	puts("*******************Parameter**************************");

	while((ma=::FindNext(param))!=INVALID_VALUE)
	{
		puts("/////////sub item////");
		alist.clear();
		if(::EnumNodeAttrib(ma,alist))
		{
			for(it=alist.begin();it!=alist.end();it++)
		        printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

		}else
			puts("enum failed");


	}

	::ReleaseItemList(param);

	puts("*********************ADParameter*************************");

	if(!::FindMTAdvanceParameterFirst(mlobj,param))
	{
		puts("Find admtp first error");
		return;
	}

	while((ma=::FindNext(param))!=INVALID_VALUE)
	{
		puts("/////////sub item////");
		alist.clear();
		if(::EnumNodeAttrib(ma,alist))
		{
			for(it=alist.begin();it!=alist.end();it++)
		        printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

		}else
			puts("enum failed");


	}
	::ReleaseItemList(param);


	puts("********************ErrorAlertCondition********************");

	if((ma=::GetMTErrorAlertCondition(mlobj))==INVALID_VALUE)
	{
		puts("Get ErroralertCondition failed");
		return ;
	}

	alist.clear();
	if(::EnumNodeAttrib(ma,alist))
	{
		for(it=alist.begin();it!=alist.end();it++)
			printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

	}else
		puts("enum failed");

	puts("********************WarningAlertCondition********************");

	if((ma=::GetMTErrorAlertCondition(mlobj))==INVALID_VALUE)
	{
		puts("Get ErroralertCondition failed");
		return ;
	}

	alist.clear();
	if(::EnumNodeAttrib(ma,alist))
	{
		for(it=alist.begin();it!=alist.end();it++)
			printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

	}else
		puts("enum failed");

	puts("********************GoodAlertCondition********************");

	if((ma=::GetMTErrorAlertCondition(mlobj))==INVALID_VALUE)
	{
		puts("Get ErroralertCondition failed");
		return ;
	}

	alist.clear();
	if(::EnumNodeAttrib(ma,alist))
	{
		for(it=alist.begin();it!=alist.end();it++)
			printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

	}else
		puts("enum failed");

	puts("*********************Return*************************");

	if(!::FindMTReturnFirst(mlobj,param))
	{
		puts("Find admtp first error");
		return;
	}

	while((ma=::FindNext(param))!=INVALID_VALUE)
	{
		puts("/////////sub item////");
		alist.clear();
		if(::EnumNodeAttrib(ma,alist))
		{
			for(it=alist.begin();it!=alist.end();it++)
		        printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

		}else
			puts("enum failed");


	}
	::ReleaseItemList(param);





	CloseMonitorTemplet(mlobj);


	
}

void testupdatemonitortemplet(void)
{
	OBJECT mlobj=::CreateMonitorTemplettt(50);
	if(mlobj==INVALID_VALUE)
	{
		puts("Get monitor templet failed");
		return;
	}
	if(::SubmitMonitorTemplet(mlobj))
	   puts("Submit ok");
	else
		puts("Submit failed");


}

OBJECT testcreatese(string label,S_UINT id)
{
	SVSE *pSE=new SVSE();
	pSE->PutLabel(label.c_str());
	pSE->PutID(id);

	OBJECT obj=reinterpret_cast<OBJECT>(pSE);
	return obj;

}

void testupdatesvse()
{
	OBJECT seobj=testcreatese("localhsot",1);
	if(seobj==INVALID_VALUE)
	{
		puts("Create svse failed");
		return;
	}

	if(::SubmitSVSE(seobj))
		puts("submitsvse ok");
	else
		puts("submitsvse failed");


}

void testsvseapi()
{
	char buf[100]={0};
	sprintf(buf,"%d",2);
	OBJECT seobj=::GetSVSE(buf);
	if(seobj==INVALID_VALUE)
	{
		puts("Get svse failed");
		return;
	}

	string label=::GetSVSELabel(seobj);
	cout<<"label:"<<label.c_str()<<endl;

	::CloseSVSE(seobj);

	OBJECT newse=::CreateSVSE("new svse");
	if(newse==INVALID_VALUE)
	{
		puts("Create svse failed");
		return;
	}

	string id=::AddNewSVSE(newse);

	cout<<"newsvse id:"<<id.c_str()<<endl;

}

void testresourceapi(void)
{
	OBJECT rcobj=::CreateResource("zzzzz");
	if(rcobj==INVALID_VALUE)
	{
		puts("Create resource failed");
		return;
	}

	MAPNODE map=::GetResourceNode(rcobj);
	if(map==INVALID_VALUE)
	{
		puts("GetResourceNode failed");
		return ;
	}
	char name[100]={0};
	char value[100]={0};
	for(int i=0;i<10;i++)
	{
		sprintf(name,"SITEVIEW_RESOURCE_%d",i);
		sprintf(value,"测试资源字符:%d",i*i);
		if(!::AddNodeAttrib(map,name,value))
		{
			puts("Add node attrib error");
			return;
		}
	}
	if(!::SubmitResource(rcobj,"192.168.6.46"))
		puts("submit resource failed");

	::CloseResource(rcobj);


}

void test_Resource()
{
	OBJECT rcobj=::LoadResource("chinese","192.168.6.90");
	if(rcobj==INVALID_VALUE)
	{
		puts("Get resource failed");
		return;
	}

	MAPNODE ma=::GetResourceNode(rcobj);

	if(ma==INVALID_VALUE)
	{
		puts("Get resource node failed");
		return;
	}

	::AddNodeAttrib(ma,"SITEVIEW_RESOURCE_2","kkkddd");

	if(!::SubmitResource(rcobj,"127.0.0.1"))
		puts("submit resource failed");

	::CloseResource(rcobj);


}

void testresourceapi2(void)
{
	OBJECT rcobj=::LoadResource("chinese");
	if(rcobj==INVALID_VALUE)
	{
		puts("Load resource failed");
		return;
	}

	MAPNODE map=::GetResourceNode(rcobj);

	if(map==INVALID_VALUE)
	{
		puts("GetResourceNode failed");
		return ;
	}

	string value="";
	if(!::FindNodeValue(map,"SITEVIEW_RESOURCE_2",value))
		puts("findnodevalue failed");
	else
		cout<<"the value:"<<value.c_str()<<endl;

	PAIRLIST retlist;

	if(!::EnumResourceAttrib(rcobj,retlist))
	{
		puts("Enum resource failed");
		return;
	}
	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;



}

void testgroupapi(void)
{
	OBJECT groupobj=::CreateGroup();
	if(groupobj==INVALID_VALUE)
	{
		puts("Create group failed");
		return;
	}

	MAPNODE map=GetGroupMainAttribNode(groupobj);

	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	::AddNodeAttrib(map,"Title","服务器组");
	::AddNodeAttrib(map,"DependSon","");
	::AddNodeAttrib(map,"DependScondition","1");
	::AddNodeAttrib(map,"ParentId","1");

	string gid=::AddNewGroup(groupobj,"1.1");

	cout<<"gid:"<<gid.c_str()<<endl;

	::CloseGroup(groupobj);



}

void testgroupapi2(void)
{
	OBJECT groupobj=::GetGroup("1.1");
	if(groupobj==INVALID_VALUE)
	{
		puts("Create group failed");
		return;
	}

	MAPNODE map=GetGroupMainAttribNode(groupobj);

	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	PAIRLIST retlist;
	if(!::EnumNodeAttrib(map,retlist))
	{
		puts("enum node failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	std::list<string> idlist;

	if(!::GetSubGroupsIDByGroup(groupobj,idlist))
	{
		puts("Get Sub group failed");
		return;
	}

	std::list<string>::iterator itid;
	for(itid=idlist.begin();itid!=idlist.end();itid++)
		printf("sub group:%s\n",(*itid).c_str());

}

void testentityapi(void)
{
	OBJECT entityobj=::CreateEntity();
	if(entityobj==INVALID_VALUE)
	{
		puts("Create entity failed");
		return;
	}

	MAPNODE map=GetEntityMainAttribNode(entityobj);
	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	::AddNodeAttrib(map,"SystemType","Windows");
	::AddNodeAttrib(map,"Lable","localhost");
	::AddNodeAttrib(map,"LoginName","administrator");
	::AddNodeAttrib(map,"FindName","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt","# $ >");

	string entityid=::AddNewEntity(entityobj,"1");

	printf("entityid:%s\n",entityid.c_str());
    
	::CloseEntity(entityobj);

	OBJECT obj=::GetSVSE("1");
	if(obj==INVALID_VALUE)
	{
		puts("Get SVSE failed");
		return;
	}else
		puts("Get ok");


}

void testentityapi2(void)
{
	OBJECT entityobj=::GetEntity("1.1.6");
	if(entityobj==INVALID_VALUE)
	{
		puts("Get entity failed");
		return;
	}

	MAPNODE map=GetEntityMainAttribNode(entityobj);
	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	PAIRLIST retlist;
	if(!::EnumNodeAttrib(map,retlist))
	{
		puts("enum node failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	::CloseSVSE(entityobj);



}

void testmonitorapi()
{
	OBJECT monitorobj=::CreateMonitor();

	if(monitorobj==INVALID_VALUE)
	{
		puts("Create monitor failed");
		return;
	}

	MAPNODE map=GetMonitorMainAttribNode(monitorobj);

	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	::AddNodeAttrib(map,"SystemType","Windows");
	::AddNodeAttrib(map,"Lable","localhost");
	::AddNodeAttrib(map,"LoginName","administrator");
	::AddNodeAttrib(map,"FindName","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt","# $ >");

	map=GetMonitorParameter(monitorobj);

	if(map==INVALID_VALUE)
	{
		puts("GetParameter failed");
		return ;
	}

	::AddNodeAttrib(map,"SystemType2","Windows");
	::AddNodeAttrib(map,"Lable2","localhost");
	::AddNodeAttrib(map,"LoginName2","administrator");
	::AddNodeAttrib(map,"FindName2","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt2","# $ >");

	map=GetMonitorAdvanceParameterNode(monitorobj);

	if(map==INVALID_VALUE)
	{
		puts("GetAdvanceParameter failed");
		return ;
	}

	::AddNodeAttrib(map,"SystemType3","Windows");
	::AddNodeAttrib(map,"Lable3","localhost");
	::AddNodeAttrib(map,"LoginName3","administrator");
	::AddNodeAttrib(map,"FindName3","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt3","# $ >");

	map=GetMonitorErrorAlertCondition(monitorobj);

	if(map==INVALID_VALUE)
	{
		puts("GetErrorAlertCondition failed");
		return ;
	}

	::AddNodeAttrib(map,"SystemType4","Windows");
	::AddNodeAttrib(map,"Lable4","localhost");
	::AddNodeAttrib(map,"LoginName4","administrator");
	::AddNodeAttrib(map,"FindName4","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt4","# $ >");

	map=GetMonitorWarningAlertCondition(monitorobj);

	if(map==INVALID_VALUE)
	{
		puts("GeWarningAlertCondition failed");
		return ;
	}
	::AddNodeAttrib(map,"SystemType5","Windows");
	::AddNodeAttrib(map,"Lable5","localhost");
	::AddNodeAttrib(map,"LoginName5","administrator");
	::AddNodeAttrib(map,"FindName5","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt5","# $ >");

	map=GetMonitorGoodAlertCondition(monitorobj);
	if(map==INVALID_VALUE)
	{
		puts("GeWarningAlertCondition failed");
		return ;
	}
	::AddNodeAttrib(map,"SystemType6","Windows");
	::AddNodeAttrib(map,"Lable6","localhost");
	::AddNodeAttrib(map,"LoginName6","administrator");
	::AddNodeAttrib(map,"FindName6","LNJ4NNcOLcLPReKR");
	::AddNodeAttrib(map,"prompt6","# $ >");



	string monitorid=::AddNewMonitor(monitorobj,"1.1.6");

	::CloseMonitor(monitorobj);

	printf("monitor id:%s\n",monitorid.c_str());

}

void testmonitorapi2(void)
{
	OBJECT monitorobj=::GetMonitor("1.1.6.2","default","192.168.6.171");
	if(monitorobj==INVALID_VALUE)
	{
		puts("Get entity failed");
		return;
	}

	MAPNODE map=GetMonitorMainAttribNode(monitorobj);
	if(map==INVALID_VALUE)
	{
		puts("GetMainAttrib failed");
		return ;
	}

	PAIRLIST retlist;
	if(!::EnumNodeAttrib(map,retlist))
	{
		puts("enum node failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	map=GetMonitorErrorAlertCondition(monitorobj);
	if(map==INVALID_VALUE)
	{
		puts("GetErrorAlertCondition failed");
		return ;
	}

	retlist.clear();

	if(!::EnumNodeAttrib(map,retlist))
	{
		puts("enum node failed");
		return;
	}

	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;



	::CloseMonitor(monitorobj);


}

void testgetcountapi(void)
{
	PAIRLIST retlist;
	if(!::GetAllSVSEInfo(retlist))
	{
		
		puts("getsvseinfo failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	puts("///////////////////////////////////////////////////");

	retlist.clear();

	if(!::GetAllEntitysInfo(retlist,"Lable"))
	{
		puts("getentityinfo failed");
		return;

	}
	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	retlist.clear();

	if(!::GetAllMonitorsInfo(retlist,"SystemType"))
	{
		puts("get monitor info failed");
		return;
	}

	puts("//////////////////monitor//////////////////////");

	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	retlist.clear();
	if(!GetAllResourceInfo(retlist))
	{
		puts("Get resource info error");
		return;
	}

	puts("//////////////////////resource////////////////////////");

	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;

	puts("/////////////////////group/////////////////////////");

	retlist.clear();
	if(!::GetAllGroupsInfo(retlist,"Title"))
	{
		puts("Get group info error");
		return;
	}

	for(it=retlist.begin();it!=retlist.end();it++)
		cout<<"name: "<<(*it).name.c_str()<<"\tvalue:"<<(*it).value.c_str()<<endl;


}

void testdeleteapi(void)
{
	if(!::DeleteSVMonitor("1.1.6.7"))
	{
		puts("Delete monitor failed");
		return;
	}

	puts("Delete ok");

	testgetcountapi();

}

void testenumentitysubmonitor(void)
{
	OBJECT entityobj=::GetEntity("1.1.6");
	if(entityobj==INVALID_VALUE)
	{
		puts("Get entity failed");
		return;
	}

	std::list<string> mid;


	if(!::GetSubMonitorsIDByEntity(entityobj,mid))
	{
		puts("Get Sub monitor id error");
		return ;
	}

	std::list<string>::iterator it;
	for(it=mid.begin();it!=mid.end();it++)
	{
		puts((*it).c_str());
	}

	
}

void testenumgroupsubentity(void)
{
	OBJECT groupobj=::GetGroup("1.1");
	if(groupobj==INVALID_VALUE)
	{
		puts("Get group failed");
		return;
	}

	std::list<string> mid;
	if(!::GetSubEntitysIDByGroup(groupobj,mid))
	{
		puts("Get sub entity id failed");
		return;
	}


	std::list<string>::iterator it;
	for(it=mid.begin();it!=mid.end();it++)
	{
		puts((*it).c_str());
	}



}

void testdeleteentity(void)
{
	if(!::DeleteEntity("1.1.4"))
	{
		puts("delete entity failed");
		return;
	}

	testenumgroupsubentity();

}

void testenumsvsesubgroup(void)
{
	OBJECT svseobj=::GetSVSE("1");
	if(svseobj==INVALID_VALUE)
	{
		puts("Get group failed");
		return;
	}

	std::list<string> mid;
	
	if(!::GetSubGroupsIDBySE(svseobj,mid))
	{
		puts("Get sub entity id failed");
		return;
	}


	std::list<string>::iterator it;
	for(it=mid.begin();it!=mid.end();it++)
	{
		puts((*it).c_str());
	}


}

void testenumgroupsubgroup(void)
{
	OBJECT groupobj=::GetGroup("1.1.1.1","default","192.168.6.171");
	if(groupobj==INVALID_VALUE)
	{
		puts("Get group failed");
		return;
	}

	std::list<string> mid;
	if(!::GetSubGroupsIDByGroup(groupobj,mid))
	{
		puts("Get sub entity id failed");
		return;
	}


	std::list<string>::iterator it;
	for(it=mid.begin();it!=mid.end();it++)
	{
		puts((*it).c_str());
	}

}

void testdeletegroup(void)
{
	if(!::DeleteGroup("1.1.1"))
	{
		puts("delete group failed");
		return;
	}

	testenumgroupsubgroup();

}

void testcreatemonitortemplet(void)
{
	OBJECT obj=::CreateMonitorTemplet(2);
	if(obj==INVALID_VALUE)
	{
		puts("Create obj failed");
		return;
	}

	MAPNODE node=::GetMTMainAttribNode(obj);
	if(node==INVALID_VALUE)
	{
		puts("GetMTMainAttribNode failed");
		return;
	}

	if(!::AddNodeAttrib(node,"Name","CPU"))
	{
		puts("AddNode attrib failed");
			return ;
	}
    ::AddNodeAttrib(node,"Label","Cpu");
    ::AddNodeAttrib(node,"Description","监测CPU的使用率 远程NT,UNIX主机");
    ::AddNodeAttrib(node,"N_ExecuteAssemblyName","NTPerfTest.dll");

	LISTITEM list;
	if(!::FindMTParameterFirst(obj,list))
	{
		puts("Find parameter failed");
		return;
	}

	node=::CreateNewMapNode();
	if(node==INVALID_VALUE)
	{
		puts("Create map node failed");
		return;
	}

    ::AddNodeAttrib(node,"AllowNull","false");
    ::AddNodeAttrib(node,"ShowText","监测频率（监测器的监测频率）");
    ::AddNodeAttrib(node,"RunInMonitor","false");

	if(!AddItemToList(list,node))
	{
		puts("Add item to list failed");
		return ;
	}

/*	if(!::SubmitMonitorTemplet(obj,"default","192.168.6.161"))
	{
		puts("Submit monitor templet failed");
		return ;
	}*/

	if(!::SubmitMonitorTemplet(obj))
	{
		puts("Submit monitor templet failed");
		return ;
	}

	::CloseMonitorTemplet(obj);
	




	
}
void testtempletenum(void)
{
	OBJECT obj=::GetMonitorTemplet(1,"default","192.168.6.161");
	if(obj==INVALID_VALUE)
	{
		puts("Get obj failed");
		return;
	}
	MAPNODE node=::GetMTMainAttribNode(obj);
	if(node==INVALID_VALUE)
	{
		puts("Get main attrib failed");
		return;
	}

	PAIRLIST retlist;
	if(!::EnumNodeAttrib(node,retlist))
	{
		puts("enum failed");
		return;
	}

	PAIRLIST::iterator it;
	for(it=retlist.begin();it!=retlist.end();it++)
	{
		printf("name=%s\tvalue=%s\n",(*it).name.c_str(),(*it).value.c_str());

	}

	::CloseMonitorTemplet(obj);


}

void testgeneralapi(void)
{
	printf("root path:%s\n",::GetSiteViewRootPath().c_str());
}

void testtaskapi(void)
{
	OBJECT tobj=::CreateTask("aaaa");
	if(tobj==INVALID_VALUE)
	{
		puts("Create task failed");
		return;
	}

	if(!SetTaskValue("Monday","A#disable#7:00-8:00",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}

	if(!SetTaskValue("Tuesday","A#enable#7:00-8:00",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}

	if(!SetTaskValue("Wednesday","",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}
	if(!SetTaskValue("Thursday","",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}
	if(!SetTaskValue("Friday","A#enable#10:00-23:00",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}
	if(!SetTaskValue("Saturday","A#disable#7:00-19:00",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}
	if(!SetTaskValue("Sunday","A#disable#7:00-20:00",tobj))
	{
		puts("SetTaskValue failed");
		return;
	}

	if(!::SubmitTask(tobj))
	{
		puts("Submit failed");
		return;
	}
	
}

void testtaskapi2(void)
{
	std::list<string> lstret;

	if(!::GetAllTaskName(lstret))
	{
		puts("GetAllTaskName failed");
		return;
	}
	std::list<string>::iterator it;
	for(it=lstret.begin();it!=lstret.end();it++)
	{
		printf("%s\n",(*it).c_str());

	}

	OBJECT taskobj=GetTask("cccc");
	if(taskobj==INVALID_VALUE)
	{
		puts("Get task 'bbbb' failed");
		return;
	}


	if(!::EditTask(taskobj,"cccc"))
	{
		puts("Edit task failed");
		return;
	}

	printf(" monday is:%s\n tuesday:%s\n wednesday:%s\n Thursday:%s\n Friday:%s\n saturday:%s\n sunday:%s\n",
		::GetTaskValue("Monday",taskobj).c_str(),
		::GetTaskValue("Tuesday",taskobj).c_str(),
		::GetTaskValue("Wednesday",taskobj).c_str(),
		::GetTaskValue("Thursday",taskobj).c_str(),
		::GetTaskValue("Friday",taskobj).c_str(),
		::GetTaskValue("Saturday",taskobj).c_str(),
		::GetTaskValue("Sunday",taskobj).c_str());

}

void enumGroup(const char* szGroupID)
{
    list<string> lsGroupID;
    list<string>::iterator lstItem;
    if(szGroupID != NULL)
    {
        string szId = szGroupID;
        OBJECT group = GetGroup(szId);
        if(group != INVALID_VALUE)
        {
            if(GetSubGroupsIDByGroup(group, lsGroupID))
            {
                for(lstItem = lsGroupID.begin(); lstItem != lsGroupID.end(); lstItem ++)
                {
                    printf("--------- sub group index-----------\n%s\n", (*lstItem).c_str());

                    OBJECT  groupnode = GetGroup((*lstItem).c_str());
                    if(groupnode != INVALID_VALUE)
                    {
                        MAPNODE node = GetGroupMainAttribNode(groupnode);
                        if(node != INVALID_VALUE)
                        {
                            string szName = "";
                            FindNodeValue(node, "sv_name", szName);
                            printf("-------- group name -----------\n%s\n", szName.c_str());
                        }
                        CloseGroup(groupnode);                
                    }
                    enumGroup((*lstItem).c_str());       
                }
            }
            CloseGroup(group);  
        }        
    }
}

void delEntity(string &szIndex)
{
    list<string> lstMonitors;
    list<string>::iterator lstItem;
    if(!szIndex.empty())
    {
        OBJECT entity = GetEntity(szIndex);
        if (entity != INVALID_VALUE)
        {
            if(GetSubMonitorsIDByEntity(entity, lstMonitors))
            {
                for(lstItem = lstMonitors.begin(); lstItem != lstMonitors.end(); lstItem ++)
                {
                    string szMonitorID = (*lstItem).c_str();
                    DeleteSVMonitor(szMonitorID);
                }
            }
        }
        CloseEntity(entity);
        DeleteEntity(szIndex);
    }
}


void delGroup(string &szIndex)
{
    list<string> lsGroupID;
    list<string> lsEntityID;
    list<string>::iterator lstItem;
    if(!szIndex.empty())
    {
        OBJECT group = GetGroup(szIndex);
        if(group != INVALID_VALUE)
        {
            if(GetSubGroupsIDByGroup(group, lsGroupID))
            {
                for(lstItem = lsGroupID.begin(); lstItem != lsGroupID.end(); lstItem ++)
                {
                    string szSubGroupID = (*lstItem).c_str();
                    //printf("sub group ID : %s\n", szSubGroupID.c_str());
                    delGroup(szSubGroupID);      
                }                
            }
            if(GetSubEntitysIDByGroup(group, lsEntityID))            
            {
                for(lstItem = lsEntityID.begin(); lstItem != lsEntityID.end(); lstItem ++)
                {
                    string szEntityID = (*lstItem).c_str();
                    printf("sub entity ID : %s\n", szEntityID.c_str());
                    delEntity(szEntityID);      
                }            
            }
            CloseGroup(group);
            printf("delete group: %s\n", szIndex.c_str());
            DeleteGroup(szIndex); 
        }        
    }
}


void testdelandenumgroup(void)
{
    string strId = "1.1";
    delGroup(strId);
    //enumGroup(strId.c_str());

 //   ::Sleep(1000*2);
    
    OBJECT root = GetSVSE("1");
    list<string> lsGroupID;
    list<string>::iterator lstItem;
    if (root != INVALID_VALUE)
    {
        if(GetSubGroupsIDBySE(root, lsGroupID))
        {
            for(lstItem = lsGroupID.begin(); lstItem != lsGroupID.end(); lstItem ++)
            {
                OBJECT  groupnode = GetGroup((*lstItem).c_str());
                printf("--------- sub group index-----------\n%s\n", (*lstItem).c_str());
                //string strID = (*lstItem).c_str();
                enumGroup((*lstItem).c_str());
            }
        }
        CloseSVSE(root);
    }

}

void testinifileapi(void)
{
/*	if(!::WriteIniFileString("testsection","testkey","this is a value","testini.ini"))
	{
		puts("Write ini file failed");
		return ;
	}
	if(!::WriteIniFileString("testsection","testkey1","this is a value1","testini.ini"))
	{
		puts("Write ini file failed");
		return ;
	}
	if(!::WriteIniFileString("testsection","testkey2","this is a value2","testini.ini"))
	{
		puts("Write ini file failed");
		return ;
	}

	if(!::WriteIniFileInt("testsection","intkey",27,"testini.ini"))
	{
		puts("Write int failed");
		return;
	}

	if(!::WriteIniFileInt("intsection","intkey",18,"testini.ini"))
	{
		puts("Write int failed");
		return;
	}*/
	int type=::GetIniFileValueType("newtestsection","testkey2","testini.ini");
	printf("type is:%d\n",type);

	string str=::GetIniFileString("newtestsection","testkey9","come here","testini.ini");
	if(str.empty())
	{
		puts("Get string failed");
		return ;
	}

	StringMap *pmap=new StringMap();

	pmap->insert("AllowNull","false");
	pmap->insert("ShowText","标题（监测器名称）");
	pmap->insert("RunInMonitor","false");
	pmap->insert("name","mCurrentName");
	pmap->insert("DataLength","10");

	pmap->insert("IsNumeric","true");
	pmap->insert("DecimalDigits","0");

	pmap->insert("ControlWidth","150");
	pmap->insert("ControlHeight","21");

	
	buffer buf;
	S_UINT len=0;
	len=pmap->GetRawDataSize();
	if(!buf.checksize(len))
	{
		puts("Memory allow failed");
		return;
	}

	if(pmap->GetRawData(buf,len)==NULL)
	{
		puts("Get raw data failed");
		return;
	}

	StringMap clone;
	if(!clone.CreateObjectByRawData(buf,len))
	{
		puts("Create object 1 failed");
		return;
	}

/*	if(!::WriteIniFileStruct("testsection","binkey",buf,len,"testini.ini"))
	{
		puts("Write bin data failed");
		return;
	}
*/

/*	if(!::EditIniFileSection("testsection","newtestsection","testini.ini"))
	{
		puts("Edit failed");
		return;
	}*/

/*	if(!::EditIniFileKey("newtestsection","intkey","newintkey","testini.ini"))
	{
		puts("Edit key failed");
		return;
	}*/

	int nret=::GetIniFileInt("newtestsection","newintkey",-1,"testini.ini");


	printf("The value is:%s,int value :%d\n",str.c_str(),nret);

	S_UINT dlen=0;

	if(!::GetIniFileStruct("newtestsection","binkey",NULL,dlen,"testini.ini"))
	{
		puts("Get data failed");
		return ;
	}
	printf("len is:%d,dlen is :%d\n",len,dlen);

	buffer dbuf;
	if(!dbuf.checksize(dlen))
	{
		puts("memory allow failed 2");
	}

	if(!::GetIniFileStruct("newtestsection","binkey",dbuf,dlen,"testini.ini"))
	{
		puts("Get data failed 2");
		return;
	}

	StringMap smap;
	if(!smap.CreateObjectByRawData(dbuf,dlen))
	{
		puts("Create object failed");
		return;
	}

	StringMap::iterator it;
	while(smap.findnext(it))
	{
		printf("%s=%s\n",(*it).getkey().getword(),(*it).getvalue().getword());
	}


	std::list<string> listsection;

	if(!::GetIniFileSections(listsection,"testini.ini"))
	{
		puts("Get sections failed");
		return;
	}

	std::list<string>::iterator its;
	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}

	listsection.clear();

	if(!::GetIniFileKeys("testsection",listsection,"testini.ini"))
	{
		printf("Get key name failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Key name:%s\n",(*its).c_str());
	}

	puts("end");


}

void Testmaxinifile()
{
//	::WriteIniFileString("userright","string","this is a test","userright.ini");
	puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	std::list<string> listsection;

	if(!::GetIniFileSections(listsection,"alert.ini"))
	{
		puts("Get sections failed");
		return;
	}

	std::list<string>::iterator its;
	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();

    puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	if(!::GetIniFileSections(listsection,"TXTTemplate.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();

puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	if(!::GetIniFileSections(listsection,"smskey.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();
puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	if(!::GetIniFileSections(listsection,"smsphoneset.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();
puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	if(!::GetIniFileSections(listsection,"userright.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();
puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

	if(!::GetIniFileSections(listsection,"email.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();

puts(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	if(!::GetIniFileSections(listsection,"user.ini"))
	{
		puts("Get sections failed");
		return;
	}

	for(its=listsection.begin();its!=listsection.end();its++)
	{
		printf("Section name:%s\n",(*its).c_str());
	}
	listsection.clear();
}

void testentitygroupapi()
{
	OBJECT egobj=::CreateEntityGroup("Server2");
	if(egobj==INVALID_VALUE)
	{
		puts("Create entity group failed");
		return ;
	}

	MAPNODE md=::GetEntityGroupMainAttribNode(egobj);
	if(md==INVALID_VALUE)
	{
		puts("Get entity group main attrib node failed");
		return ;
	}

	if(!::AddNodeAttrib(md,"Name","服务器"))
	{
		puts("Add node attrib failed");
		return;
	}
	::AddNodeAttrib(md,"ControlViewId","");
	::AddNodeAttrib(md,"des","包括Windwos服务器和UNIX服务器");
	::AddNodeAttrib(md,"parentid","");

	if(!AddSubEntityTempletIDToEG(egobj,"_win"))
	{
		puts("Add sub entitytemplet failed");
		return;
	}

	if(!AddSubEntityTempletIDToEG(egobj,"_unix"))
	{
		puts("Add sub entitytemplet failed2");
		return;
	}

	if(!::SubmitEntityGroup(egobj))
	{
		puts("Submit failed");
		return;
	}


}
void testentitygroupgetapi(void)
{
	OBJECT egobj=::GetEntityGroup("Server2");

	if(egobj==INVALID_VALUE)
	{
		puts("Get entity group failed");
		return;
	}

	MAPNODE md=GetEntityGroupMainAttribNode(egobj);
	if(md==INVALID_VALUE)
	{
		puts("Get entity group main attrib node failed");
		return ;
	}

	PAIRLIST retl;
	if(!::EnumNodeAttrib(md,retl))
	{
		puts("enum node attrib failed");
		return;
	}
	puts("////////////////////Property/////////////////////");

	PAIRLIST::iterator it;
	for(it=retl.begin();it!=retl.end();it++)
		printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

	std::list<string> idlist;

	if(!::GetSubEntityTempletIDByEG(idlist,egobj))
	{
		puts("Get sub entity templet failed");
		return;
	}

	puts("////////////////////Sub entity templet id///////////");;
	std::list<string>::iterator itl;
	for(itl=idlist.begin();itl!=idlist.end();itl++)
		printf("%s\n",(*itl).c_str());

	::CloseEntityGroup(egobj);

}

void testentitygroupdelete(void)
{
	if(!::DeleteEntityGroup("Server"))
	{
		puts("Delete entity group failed");
		return;
	}

	testentitygroupgetapi();

}

void testentitytempletapi(void)
{
	OBJECT etobj=::CreateEntityTemplet("_unix");
	if(etobj==INVALID_VALUE)
	{
		puts("Create entity group failed");
		return ;
	}

	MAPNODE md=::GetEntityTempletMainAttribNode(etobj);
	if(md==INVALID_VALUE)
	{
		puts("Get entity templet main attrib node failed");
		return ;
	}

	if(!::AddNodeAttrib(md,"Name","windows"))
	{
		puts("Add node attrib failed");
		return;
	}
	::AddNodeAttrib(md,"ControlViewId","");
	::AddNodeAttrib(md,"des","域名解析服务器");
	::AddNodeAttrib(md,"parentid","Server");

	LISTITEM item;
	if(!::FindETContrlFirst(etobj,item))
	{
		puts("Find et contrl first failed");
		return;
	}

	MAPNODE mb=::CreateNewMapNode();
	if(mb==INVALID_VALUE)
	{
		puts("Create new map node failed ");
		return;
	}

	if(!::AddNodeAttrib(mb,"ShowText","Windows 服务器"))
	{
		puts("Add node attrib failed");
		return;
	}
    ::AddNodeAttrib(mb,"ShowText","Windows 服务器");
    ::AddNodeAttrib(mb,"ControlName","SystemType");
    ::AddNodeAttrib(mb,"ControlType","13");
    ::AddNodeAttrib(mb,"DefaultVlaue","Windows");

	if(!::AddItemToList(item,mb))
	{
		puts("Add item to list failed");
		return ;
	}

	mb=::CreateNewMapNode();
	if(mb==INVALID_VALUE)
	{
		puts("Create new map node failed2 ");
		return;
	}

	if(!::AddNodeAttrib(mb,"ShowText","对应服务器名称"))
	{
		puts("Add node attrib failed2");
		return;
	}
    ::AddNodeAttrib(mb,"ShowText","Windows 服务器");
    ::AddNodeAttrib(mb,"ControlName","IPAddress");
    ::AddNodeAttrib(mb,"ControlType","10");
    ::AddNodeAttrib(mb,"Description","主机名或IP地址");
    ::AddNodeAttrib(mb,"DataLength","100");
    ::AddNodeAttrib(mb,"Description","主机名或IP地址");

	if(!::AddItemToList(item,mb))
	{
		puts("Add item to list failed2");
		return ;
	}

	mb=::CreateNewMapNode();
	if(mb==INVALID_VALUE)
	{
		puts("Create new map node failed3 ");
		return;
	}

	if(!::AddNodeAttrib(mb,"ShowText","登录名"))
	{
		puts("Add node attrib failed3");
		return;
	}
    ::AddNodeAttrib(mb,"ShowText","登录名");
    ::AddNodeAttrib(mb,"ControlName","IPAddress");
    ::AddNodeAttrib(mb,"ControlType","10");
    ::AddNodeAttrib(mb,"Description","主机名或IP地址");
    ::AddNodeAttrib(mb,"DataLength","100");
    ::AddNodeAttrib(mb,"Description","请输入远程Windows服务器的登录名（如果管理员是域管理员输入格式如: DOMAIN\\Administrator）");

	if(!::AddItemToList(item,mb))
	{
		puts("Add item to list failed3");
		return ;
	}

	if(!::AddSubMonitorTypeToET(etobj,10))
	{
		puts("Add sub monitor type failed");
		return ;
	}
	if(!::AddSubMonitorTypeToET(etobj,11))
	{
		puts("Add sub monitor type failed");
		return ;
	}
	if(!::AddSubMonitorTypeToET(etobj,13))
	{
		puts("Add sub monitor type failed");
		return ;
	}
	if(!::AddSubMonitorTypeToET(etobj,16))
	{
		puts("Add sub monitor type failed");
		return ;
	}

	if(!::SubmitEntityTemplet(etobj))
	{
		puts("Submit entity templet failed");
		return ;
	}

	::CloseEntityTemplet(etobj);

	puts("OK");
	
}

void testentitytempletgetapi()
{
	OBJECT etobj=::GetEntityTemplet("_unix");
	if(etobj==INVALID_VALUE)
	{
		puts("Create entity group failed");
		return ;
	}

	MAPNODE md=::GetEntityTempletMainAttribNode(etobj);
	if(md==INVALID_VALUE)
	{
		puts("Get entity templet main attrib node failed");
		return ;
	}

	PAIRLIST retl;
	if(!::EnumNodeAttrib(md,retl))
	{
		puts("enum node attrib failed");
		return;
	}
	puts("////////////////////Property/////////////////////");

	PAIRLIST::iterator it;
	for(it=retl.begin();it!=retl.end();it++)
		printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());

	LISTITEM item;
	if(!::FindETContrlFirst(etobj,item))
	{
		puts("Find et contrl first failed");
		return;
	}

    MAPNODE ma;
	while((ma=::FindNext(item))!=INVALID_VALUE)
	{
		puts("///////////////sub contrl////////////////");
		retl.clear();
		if(!::EnumNodeAttrib(ma,retl))
		{
			puts("enum node attrib failed2");
			return;
		}
		for(it=retl.begin();it!=retl.end();it++)
			printf("%s=%s\n",(*it).name.c_str(),(*it).value.c_str());
	}

	std::list<int> mtlist;
	if(!::GetSubMonitorTypeByET(etobj,mtlist))
	{
		puts("Get sub monitor type failed");
		return;
	}

	puts("***********************sub monitor type***********************");

	std::list<int>::iterator itmt;
	for(itmt=mtlist.begin();itmt!=mtlist.end();itmt++)
	   printf("%d\n",(*itmt));

	::CloseEntityTemplet(etobj);


}

void testentitytempletdelete(void)
{
	if(!::DeleteEntityTemplet("_unix"))
	   puts("delete failed");
	else
		puts("delete ok");

	testentitytempletgetapi();

}

void testvector()
{
	std::vector<int> zz;
	zz.resize(100);
	zz[2]=12;
	zz.push_back(23);

	zz.resize(200);
	printf("zz size:%d,zz2value:%d\n",zz.size(),zz[2]);

}
class test_rset
{
public:
	test_rset()
	{
		m_int = 12;
		m_str = "this is a test";
	}
	~test_rset()
	{
	};

	test_rset * operator ->()
	{
		return this;
	}

    int operator [](const string str)
	{
		if(str.compare("m_int")==0)
			return m_int;

		return 0;

	}
	template <class _RT>
		_RT GetValue(string name,_RT defautret)
	{
		if(name.compare("int")==0)
			return m_int;
		else
			return defautret;
	}


public:
	int m_int;
	string m_str;

};

void testmapfile(void)
{
//	ost::ThreadFile file("d:\\100.txt");
//	ost::MappedFile mfile("d:\\100.txt",ost::File::accessReadWrite,1024*1024*100*10*2);

//	char *p = (char *)mfile.fetch();
//	sprintf(p,"%s","这是一个内存映射文件的测试");
//	char *pt=p+1024*1024*100*10*2;

//	sprintf(pt,"%s","这是文件的中间部分");

//	printf(pt);

	//ost::MappedFile(


	HANDLE hfile=::CreateFile("d:\\testmap2.txt",ost::File::accessReadWrite,FILE_SHARE_READ|FILE_SHARE_WRITE,NULL,OPEN_ALWAYS,FILE_ATTRIBUTE_NORMAL | FILE_FLAG_RANDOM_ACCESS,NULL);
	if(hfile == INVALID_HANDLE_VALUE)
	{
		puts("CreateFile failed");
		return;
	}
	LONG k=1024*1024*100*10;
	printf("k is :%d\n",k);
	LONG len=2;
	::SetFilePointer(hfile,k,&len,FILE_BEGIN);
	printf("error:%d\n",::GetLastError());
	::SetEndOfFile(hfile);

	

	::Sleep(10000000);

}

bool WriteSector(BYTE bDrive, DWORD dwStartSector, WORD wSectors, LPBYTE lpSectBuff)
{
	if (bDrive == 0) return 0;

	char devName[] = "\\\\.\\A:";

	devName[4] ='A' + bDrive - 1;

	HANDLE hDev = CreateFile(devName, GENERIC_WRITE, FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);

    if (hDev == INVALID_HANDLE_VALUE) return 0;

	SetFilePointer(hDev, 512 * dwStartSector, 0, FILE_BEGIN);

	DWORD dwCB;
	bool bRet = WriteFile(hDev, lpSectBuff, 512 * wSectors, &dwCB, NULL);

	CloseHandle(hDev);

	return bRet;
}

bool ReadSector(BYTE bDrive, DWORD dwStartSector, WORD wSectors, LPBYTE lpSectBuff)
{
	if (bDrive == 0) return 0;
	char devName[] = "\\\\.\\A:";
	devName[4] ='A' + bDrive - 1;
	puts(devName);
	HANDLE hDev = CreateFile(devName, GENERIC_READ, FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);
	if (hDev == INVALID_HANDLE_VALUE) return 0;
	SetFilePointer(hDev, 512 * dwStartSector, 0, FILE_BEGIN);
	DWORD dwCB;
	BOOL bRet = ReadFile(hDev, lpSectBuff, 512 * wSectors, &dwCB, NULL);
	CloseHandle(hDev);
	return bRet;

}

void testdisksector(void)
{
/*	DWORD dwSe,dwBPS,dwFreeC,dwToc;
	if(::GetDiskFreeSpace("D:",&dwSe,&dwBPS,&dwFreeC,&dwToc))
	{
		printf("%d-%d-%d-%d\n",dwSe,dwBPS,dwFreeC,dwToc);
	}
	*/

/*	for(char a=20; a<127 ; a++)
	{
		printf("%d:%c  ",a,a);
		if(a%20==19)
			printf("\r\n");
	}
	puts("\r");
	return;
*/
	BYTE buf[512*10]={'\0'};
	if(!ReadSector(7,753450,10,buf))
	{
		printf("Read sector failed");
		return;
	}

	char str[2048*10]={0};
	BYTE *pt=buf;
	//BYTE *pend=buf+512;
	BYTE *pc=pt;
	for(int i=0;i<512*10;i++)
	{
		sprintf(str,"%s %02X",str,*pt++);
		if(i%16==15)
		{
			strcat(str,"\t");
			for(int n=0;n<16;n++)
			    if(*pc>=20)
					sprintf(str,"%s %c",str,*pc++);
				else
					strcat(str," .");

			pc=pt;

	    	strcat(str,"\r\n");
		}
		else
		  if(i%8==7)
			strcat(str," -");
	}

	printf("Sector data :\n%s\n",str);

}

void TestPage(void)
{
	PagePool_T mempool(1024*8,20);

	DWORD bt=::GetTickCount();

	if(!mempool.CreatePage("D:\\mempage\\mempool.txt",true))
	{
		puts("Create mempool failed");
		return;
	}

	DWORD be=::GetTickCount();
	printf("Cost time :%d\n",be-bt);

	int i=0;

	for(;;)
	{
		i++;
		Page_T *pt=mempool.GetFree();
		if(pt==NULL)
		{
			printf("Get free mem page failed,index:%d\n",i);
			return;
		}
		sprintf(pt->m_data,"这是一个页面测试index:%d",i);
    	mempool.Put(pt,true,true);
	}

}

void TestPageLoad()
{
	PagePool_T mempool;
	if(!mempool.LoadPage("D:\\mempage\\mempool.txt"))
	{
		puts("Create mempool failed");
		return;
	}

/*	Page_T *punt=mempool.GetFree();

	mempool.Put(punt,false,true);

     if(punt!=NULL)
	 {
		 printf("index is :%d\n",punt->m_Head.m_pos);
	 }
*/
	::srand((unsigned int)time(NULL));

	DWORD bt=::GetTickCount();
	for(int i=0;i<300;i++)
	{
		int n=rand()%30;
		Page_T *pt=mempool.Get(n);

		if(pt==NULL)
		{
			printf("Get page failed :%d\n",n);
			return;
		}
		if(pt->m_Head.m_unused)
			printf("此页面为空:%d\n",n);
		else
	       printf("data is :%s\n",pt->m_data);
    	mempool.Put(pt,false,false);

	}
	DWORD be=::GetTickCount();

	printf("Cost time :%d\n",be-bt);
	
    ::Sleep(20000000);


}

void testfilefind()
{
	WIN32_FIND_DATA fd;
	HANDLE fr=::FindFirstFile("c:\\jdk1.3\\*.*",&fd);
	while(::FindNextFile(fr,&fd))
	{
		if(fd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)
			printf("目录:%s\n",fd.cFileName);
		else
	    	printf("文件:%s\n",fd.cFileName);

	}

}
void testaddnewpage()
{
	PagePool_T mempool;
	if(!mempool.LoadPage("D:\\mempage\\mempool.txt"))
	{
		puts("Create mempool failed");
		return;
	}

	if(!mempool.AddNewPages(10))
	{
		puts("Add new page failed");
		return ;
	}




}

void testpagesize()
{
	 SYSTEM_INFO 	sSysInfo;
	 GetSystemInfo(&sSysInfo);
	 printf("page size is :%d\n",sSysInfo.dwPageSize);

}

void testloaddb();
bool testinserttable(int count);

void testcreatedb()
{
	{
		DB_T db;

		if(!db.CreateNew("D:\\mempage\\testdbhead.db","D:\\mempage\\test.db",1024*4,1000))
		{
			puts("Create db filed");
			return ;
		}
	}
//	testloaddb();

	if(!testinserttable(1000))
		return ;


}
char *buildbuf(int data,char *pt,int buflen)
{
	if(pt==NULL)
		return NULL;
	if(buflen<sizeof(int))
		return NULL;
	memmove(pt,&data,sizeof(int));
	pt+=sizeof(int);
	return pt;
}
char *buildbuf(float data,char *pt,int buflen)
{
	if(pt==NULL)
		return NULL;
	if(buflen<sizeof(float))
		return NULL;
	memmove(pt,&data,sizeof(float));
	pt+=sizeof(float);
	return pt;

}

char *buildbuf(string data,char *pt,int buflen)
{
	if(pt==NULL)
		return NULL;
	if(buflen<data.size()+1)
		return NULL;
	strcpy(pt,data.c_str());
	pt+=data.size();
	pt[0]='\0';
	pt++;
	return pt;

}

bool testinserttable(int count)
{
	DB_T db;
	db.m_dbheadfile="D:\\mempage\\testdbhead.db";
	if(!db.Load())
	{
		puts("Load db failed");
		return false;
	}

	char table[100]={0};
	for(int i=1;i<=count;i++)
	{
		sprintf(table,"1.2.3.5.%d",i);
		if(!db.InsertTable(table,i))
		{
			printf("Insert table failed :%s\n",table);
			return false;
		}
	}

	return true;


}

void testloaddb()
{
	DB_T db;
	db.m_dbheadfile="D:\\mempage\\testdbhead.db";
	if(!db.Load())
	{
		puts("Load db failed");
		return ;
	}

	if(!db.InsertTable("1.2.3.4.6",5))
	{
		puts("Create table failed");
		return ;
	}

/*	svutil::buffer buf(1024);

	int k=23;
	string str="this is a data test";
	float z=24.56;

	char *pt=NULL;
	char *pbegin=buf.getbuffer();

	if((pt=buildbuf(k,buf,buf.size()))==NULL)
	{
		puts("buildbuf failed int");
		return ;
	}

	if((pt=buildbuf(str,pt,buf.size()-(pt-pbegin)))==NULL)
	{
		puts("buildbuf failed string");
		return ;

	}
	
	if((pt=buildbuf(z,pt,buf.size()-(pt-pbegin)))==NULL)
	{
		puts("buildbuf failed string");
		return ;
	}

	if(!db.InsertRecord("1.2.3.4.6",1,buf,pt-pbegin))
	{
		puts("Insert record failed");
		return;
	}
*/

	puts("load ok");
}

bool builddyn(char *buf,int datalen,DYN_T&dyn)
{
	int len=sizeof(DYN_T)-sizeof(int);
	if(datalen<sizeof(DYN_T)-sizeof(int))
		return false;

    char *pt=buf;
	memmove(&dyn.m_time,pt,sizeof(svutil::TTime));
	pt+=sizeof(svutil::TTime);
	memmove(&dyn.m_state,pt,sizeof(int));
	pt+=sizeof(int);
	memmove(&dyn.m_keeplaststatetime,pt,sizeof(svutil::TTimeSpan));
	pt+=sizeof(svutil::TTimeSpan);
	memmove(&dyn.m_laststatekeeptimes,pt,sizeof(unsigned int));
	pt+=sizeof(unsigned int);
	dyn.m_displaystr=_strdup(pt);

	return true;
}

bool testinsertdb(DB_T &db,string table,int state,int iv,string sv,float fv)
{
	svutil::buffer buf(1024);

	int k=iv;
	string str=sv;
	float z=fv;

	char *pt=NULL;
	char *pbegin=buf.getbuffer();

	if((pt=buildbuf(k,buf,buf.size()))==NULL)
	{
		puts("buildbuf failed int");
		return false;
	}

	if((pt=buildbuf(str,pt,buf.size()-(pt-pbegin)))==NULL)
	{
		puts("buildbuf failed string");
		return false;

	}
	
	if((pt=buildbuf(z,pt,buf.size()-(pt-pbegin)))==NULL)
	{
		puts("buildbuf failed string");
		return false;
	}

	if(!db.InsertRecord(table,state,buf,pt-pbegin))
	{
		puts("Insert record failed");
		return  false;
	}
	return true;

}

void testdbquerydyn()
{
	DB_T db;
	db.m_dbheadfile="D:\\mempage\\testdbhead.db";
	if(!db.Load())
	{
		puts("Load db failed");
		return ;
	}

	buffer buf(1024);
	int datalen=1024;

	if(db.QueryDyn("1.2.3.4.6",buf,datalen)<0)
	{
		printf("Query dyn failed\n");
		return ;
	}

	DYN_T dyn;
	if(!builddyn(buf,datalen,dyn))
	{
		puts("Build dyn failed");
		return;
	}

	printf("Create time :%s,state:%d,keepstatetime:%d,keeptimes:%d,displaystr:%s\n",
		dyn.m_time.Format(),
		dyn.m_state,
		dyn.m_keeplaststatetime.GetTotalMinutes(),
		dyn.m_laststatekeeptimes,
		dyn.m_displaystr);


}

int parsebuf(char *buf,int buflen,int &ret)
{
	if((buf==NULL)||(buflen<sizeof(int)))
		return 0;
	char *pt=buf;
	memmove(&ret,pt,sizeof(int));
	return sizeof(int);
}
int parsebuf(char *buf,int buflen,float &ret)
{
	if((buf==NULL)||(buflen<sizeof(float)))
		return 0;
	char *pt=buf;
	memmove(&ret,pt,sizeof(float));
	return sizeof(float);
}
int parsebuf(char *buf,int buflen,string &ret)
{
	if((buf==NULL)||(buflen<sizeof(int)))
		return 0;
	char *pt=buf;
	ret=pt;
	return strlen(ret.c_str())+1;
}

void testquerydbrecord(string table)
{
	DWORD dbegin,dend;
	DB_T db;
	db.m_dbheadfile="D:\\mempage\\testdbhead.db";
	dbegin=::GetTickCount();
	if(!db.Load())
	{
		puts("Load db failed");
		return ;
	}

	dend=::GetTickCount();

	printf("Load cost time :%d\n",dend-dbegin);

	int buflen=1024*1024*20;

	buffer buf;
	if(!buf.checksize(buflen))
	{
		puts("allow memory failed");
		return ;
	}

	svutil::TTime begin;
	svutil::TTime end=svutil::TTime::GetCurrentTimeEx();
	int count=0;

	dbegin=::GetTickCount();

	if((count=db.QueryRecordByTime(table,begin,end,buf,buflen))<=0)
	{
		puts("Query record failed");
		return ;
	}
	printf("count :%d\n",count);
	dend=::GetTickCount();

	printf("Query record cost time :%d\n",dend-dbegin);

	char *pt=buf.getbuffer();
	int dlen=0;

	for(int i=0;i<count;i++)
	{
		int iv=0;
		float fv=0;
		string sv;
		RecordHead_T *prht=(RecordHead_T *)pt;
     	pt+=sizeof(RecordHead_T);
		if((dlen=parsebuf(pt,buflen,iv))==0)
		{
			puts("parsebuf int failed");
			return ;
		}
		pt+=dlen;
		
		if((dlen=parsebuf(pt,buflen,sv))==0)
		{
			puts("parsebuf string failed");
			return ;
		}
		pt+=dlen;
		
		if((dlen=parsebuf(pt,buflen,fv))==0)
		{
			puts("parsebuf float failed");
			return ;
		}

		pt+=dlen;
	/*	printf("Record: craeate time:%s,state:%d,datalen:%dintvalue:%d,strvalue:%s,floatvalue:%0.2f\n",
			prht->createtime.Format(),prht->state,prht->datalen,
			iv,sv.c_str(),fv);
*/
	}

	printf("\n*******************Total count :%d*****************************\n",count);


    
}

void InsertRecord()
{
	DB_T db;
	db.m_dbheadfile="D:\\mempage\\testdbhead.db";
	if(!db.Load())
	{
		puts("Load db failed");
		return ;
	}

	string table="1.2.3.4.6";
	int state=0;
	int iv=0;
	string sv="";
	float fv=0.00;
	char buf[1024]={0};
	char tb[100]={0};

	for(int i=0;i<4000;i++)
	{
		state=i%4;
		iv=i*i;

		sprintf(buf,"<++++++++这是第%d数据+++++++++>",i);

		sv=buf;

		fv=(i*i)/(i+1);
		for(int n=1;n<=1000;n++)
		{
			sprintf(tb,"1.2.3.5.%d",n);
			if(!testinsertdb(db,tb,state,iv,sv,fv))
			{
				printf("insert db failed record:%d\n",i);
				return ;
			}
		}

	}


}

void testcreatetableapi(string table)
{
	if(!::InsertTable(table,5))
	{
		puts("InsertTable failed");
		return;
	}else
		puts("Insert ok");

}

void testappendrecordapi(string table)
{
	char data[1024]={0};
	RecordHead *prd=(RecordHead*)data;
	char *pt=data+sizeof(RecordHead);
	char *pm=NULL;

	float fv=100;

	if((pm=::buildbuf(fv,pt,1024))==NULL)
	{
		puts("build buf failed");
		return ;
	}
	fv=40;

	if((pm=::buildbuf(fv,pm,1024))==NULL)
	{
		puts("build buf failed");
		return ;

	}

	fv=200;

	if((pm=::buildbuf(fv,pm,1024))==NULL)
	{
		puts("build buf failed");
		return ;

	}
	prd->datalen=pm-pt;
	prd->state=1;
	prd->createtime=svutil::TTime::GetCurrentTimeEx();

	strcpy(pm,"包成功率(%)=100%,数据往返时间(ms)=40(ms),状态值(200表示成功 300表示出错)=200");


	int len=pm-data;
	len+=strlen(pm)+1;

	if(!::AppendRecord(table,data,len))
	{
		puts("Append record failed");
		return;
	}else
		puts("Append OK");

}

void testinserterrorrecordapi(string table)
{
	char data[1024]={0};
	RecordHead *prd=(RecordHead*)data;
	char *pt=data+sizeof(RecordHead);
	char *pm=NULL;

	prd->state=5;
	prd->createtime=svutil::TTime::GetCurrentTimeEx();
	strcpy(pt,"error=connect to server failed");
	pt+=strlen(pt)+1;
	prd->datalen=pt-data-sizeof(RecordHead);

	strcpy(pt,"error=connect to server failed");
	pt+=strlen(pt)+1;
	

	int len=pt-data;

	if(!::AppendRecord(table,data,len))
	{
		puts("Append record failed");
		return;
	}else
		puts("Append OK");

}

void testqueryrecordapi(string table)
{
	TTimeSpan ts(10,10,0,0);
	RECORDSET rds=::QueryRecords(table,ts,"default","192.168.6.33");
/*	TTime begin;
	TTime end=TTime::GetCurrentTimeEx();
	DWORD db=0,de=0;
	db=::GetTickCount();
	RECORDSET rds=::QueryRecords(table,begin,end);
	de=::GetTickCount();
	printf(" Cost time :%d\n",de-db);*/

	if(rds==INVALID_VALUE)
	{
		puts("Query failed");
		return ;
	}

	std::list<string> flist;
	if(!::GetReocrdSetField(rds,flist))
	{
		puts("Get recordset filed failed");
		return;
	}

	std::list<string>::iterator it;
	for(it=flist.begin();it!=flist.end();it++)
	{
		printf("Filed:%s\n",(*it).c_str());
	}

	LISTITEM item;
	if(!::FindRecordFirst(rds,item))
	{
		puts("Find list failed");
		return;
	}
	RECORD rdobj;
	while((rdobj=::FindNextRecord(item))!=INVALID_VALUE)
	{

		TTime ctm;

		if(!::GetRecordCreateTime(rdobj,ctm))
		{
			puts("Get record create time failed");
			return;
		}

		printf("Record create time:%s\n",ctm.Format().c_str());

		int state=0;
		string dstr;

		if(!::GetRecordDisplayString(rdobj,state,dstr))
		{
			puts("Get record display string failed");
			return ;
		}

		printf("Record state :%d,dstr:%s\n",state,dstr.c_str());
	}

	size_t count=0;
	if(!::GetRecordCount(rds,count))
	{
		puts("Get record count failed");
		return;
	}
	printf("record count :%d\n",count);


	::ReleaseRecordList(item);

	::CloseRecordSet(rds);

	::Sleep(1000000);




}

void testquerydynapi(string table)
{
	SVDYN dyn;
	if(!::GetSVDYN(table,dyn))
	{
		puts("Get dyn failed");
		return;
	}

	printf("Time:%s,State:%d,KeepTime:%d,KeepTimes:%d\n",dyn.m_time.Format().c_str(),
		dyn.m_state,dyn.m_keeplaststatetime.GetTotalMinutes(),dyn.m_laststatekeeptimes);

	if(dyn.m_displaystr!=NULL)
		printf("dstr:%s\n",dyn.m_displaystr);
	else
		printf("dstr is NULL\n");


	
}

void testdeletetable(string table)
{
	if(!::DeleteTable(table))
	{
		puts("delete table failed");
		return;
	}else
		puts("Delete table OK");
}

void testinsertrecords(string table,int count)
{
	for(int i=0;i<count;i++)
	{
		::Sleep(100);
		testappendrecordapi(table);
	}

	puts("Insert ok");
}

void testmemory()
{
	for(int i=0;i<10000;i++)
	{
		OBJECT obj=::GetMonitorTemplet(5);
		if(obj==INVALID_VALUE)
		{
			puts("get filed");
			return;
		}
		::CloseMonitorTemplet(obj);
	}
}

DWORD WINAPI testmutexthreadinsertdb(LPVOID param)
{
	char *table=(char *) param;
	printf("table is:%s\n",table);

	::Sleep(1000);

	testinsertrecords(table,10000);

	return 1;

}
DWORD WINAPI testmutexthreadqdyndb(LPVOID param)
{
	char *table=(char *) param;
	printf("table is:%s\n",table);

	::Sleep(1000);

	while(true)
	{
		::Sleep(2000);
		testquerydynapi(table);
	}

	return 1;

}

void testmutilthreadacessdb()
{
	char *table="1.2.3.8";

	DWORD dwid;

	for(int i=0;i<20;i++)
	{
		::CreateThread(NULL,0,&testmutexthreadqdyndb,table,0,&dwid);
	}

	::Sleep(400000000);
}

void test_mq_create(string queuename,int type)
{
	if(!::CreateQueue(queuename,type,"default","192.168.6.51"))
		puts("Create queue failed");
	else
		puts("Create ok");
}

void test_mq_push(string queuename)
{
	char buf[256]={0};
	for(int i=0;i<100000;i++)
	{
		memset(buf,0,256);
		sprintf(buf,"%d:This is a svmq test 北京游龙网网络科技有限公司",i);
		if(!::PushMessage(queuename,"mytest",buf,strlen(buf)+1,"default","192.168.6.51"))
		{
			puts("Push data failed");
			return;
		}
		if(i%20==0)
	    	printf("Insert record :%d\n",i);

//		::Sleep(1000);
	}
	puts("Push ok");
}

void test_mq_pop(string queuename)
{
	char buf[256]={0};
	string label;
	svutil::TTime ct;
	S_UINT buflen=256;
	while(::PopMessage(queuename,label,ct,buf,buflen))
	{
		printf("My pop message:%s\n",buf);
		buflen=256;

	}
	puts("Pop ok");
}

void test_mq_blockpop(string queuename)
{
	char buf[256]={0};
	string label;
	svutil::TTime ct;
	S_UINT buflen=256;
	while(::BlockPopMessage(queuename,label,ct,buf,buflen,"default","192.168.6.90"))
	{
		printf("My pop message:%s\n",buf);
		buflen=256;

	}
	puts("Pop ok");


}
void test_mq_getqueuename()
{
	std::list<string> namelist;
	if(!::GetAllQueueNames(namelist))
	{
		puts("Get queue names failed");
		return ;
	}

	std::list<string>::iterator it;
	for(it=namelist.begin();it!=namelist.end();it++)
		puts((*it).c_str());
}

void test_mq_getmessagecount(string queuename)
{
	unsigned int count=0;
	if(!::GetMQRecordCount(queuename,count))
	{
		puts("Get mq record count failed");
		return ;
	}

	printf("%s's record count is:%d\n",queuename.c_str(),count);
}

void test_mq_clearqueue(string queuename)
{
	if(!::ClearQueueMessage(queuename))
	{
		puts("Clear queue Message failed");
		return;
	}

	puts("clear ok");

	test_mq_getmessagecount(queuename);

}

void test_mq_deletequeue(string queuename)
{
	if(!::DeleteQueue(queuename))
	{
		puts("delete queue failed");
		return;
	}

	puts("delete ok");
}

void test_mq_popbyobject(string queuename)
{
	MQRECORD mrd=::PopMessage(queuename);
	if(mrd==INVALID_VALUE)
	{
		puts("Pop message failed");
		return;
	}

	string label;
	svutil::TTime ct;
	unsigned int len=0;

	if(!::GetMessageData(mrd,label,ct,NULL,len))
	{
		puts("Get message data failed");
		return;
	}

	printf("Data len is :%d\n",len);
	svutil::buffer buf(len);

	if(!::GetMessageData(mrd,label,ct,buf,len))
	{
		puts("Get message data failed");
		return;
	}

	::CloseMQRecord(mrd);

	printf("label:%s,ct:%s,data:%s\n",label.c_str(),ct.Format().c_str(),buf.getbuffer());


}

void test_mq_blockpopbyobject(string queuename)
{
	MQRECORD mrd;

	while((mrd=::BlockPopMessage(queuename))!=INVALID_VALUE)
	{
		if(mrd==INVALID_VALUE)
		{
			puts("Pop message failed");
			return;
		}

		string label;
		svutil::TTime ct;
		unsigned int len=0;

		if(!::GetMessageData(mrd,label,ct,NULL,len))
		{
			puts("Get message data failed");
			return;
		}

		printf("Data len is :%d\n",len);
		svutil::buffer buf(len);

		if(!::GetMessageData(mrd,label,ct,buf,len))
		{
			puts("Get message data failed");
			return;
		}

		::CloseMQRecord(mrd);

		printf("label:%s,ct:%s,data:%s\n",label.c_str(),ct.Format().c_str(),buf.getbuffer());
	}
}
void test_mq_peekmessage(string queuename)
{
	char buf[256]={0};
	string label;
	svutil::TTime ct;
	S_UINT buflen=256;
	if(::PeekMQMessage(queuename,label,ct,buf,buflen))
	{
		printf("My peek message:%s\n",buf);
		buflen=256;

	}
	puts("Peek ok");

}

void test_mq_blockpeekmessage(string queuename)
{
	char buf[256]={0};
	string label;
	svutil::TTime ct;
	S_UINT buflen=256;
	if(::BlockPeekMQMessage(queuename,label,ct,buf,buflen))
	{
		printf("My blockpeek message:%s\n",buf);
		buflen=256;

	}
	puts("Block peek ok");

}
void test_mq_peekbyobject(string queuename)
{
	MQRECORD mrd=::PeekMQMessage(queuename);
	if(mrd==INVALID_VALUE)
	{
		puts("Peek message failed");
		return;
	}

	string label;
	svutil::TTime ct;
	unsigned int len=0;

	if(!::GetMessageData(mrd,label,ct,NULL,len))
	{
		puts("Get message data failed");
		return;
	}

	printf("Data len is :%d\n",len);
	svutil::buffer buf(len);

	if(!::GetMessageData(mrd,label,ct,buf,len))
	{
		puts("Get message data failed");
		return;
	}

	::CloseMQRecord(mrd);

	printf("label:%s,ct:%s,data:%s\n",label.c_str(),ct.Format().c_str(),buf.getbuffer());

}
void test_mq_blockpeekbyobject(string queuename)
{
	MQRECORD mrd;

	if((mrd=::BlockPeekMQMessage(queuename))!=INVALID_VALUE)
	{
		if(mrd==INVALID_VALUE)
		{
			puts("Pop message failed");
			return;
		}

		string label;
		svutil::TTime ct;
		unsigned int len=0;

		if(!::GetMessageData(mrd,label,ct,NULL,len))
		{
			puts("Get message data failed");
			return;
		}

		printf("Data len is :%d\n",len);
		svutil::buffer buf(len);

		if(!::GetMessageData(mrd,label,ct,buf,len))
		{
			puts("Get message data failed");
			return;
		}

		::CloseMQRecord(mrd);

		printf("label:%s,ct:%s,data:%s\n",label.c_str(),ct.Format().c_str(),buf.getbuffer());
	}

}

void test_refersh()
{
	test_mq_push("SiteView70_RefreshInfo");

}

void test_TTime()
{
	svutil::TTime tm;
	printf("%s\n",tm.Format().c_str());

	tm=TTime(1982,2,21,23,23,0);
	printf("%s\n",tm.Format().c_str());

}

void test_inibug()
{
	string value=::GetIniFileString("InterFace_433_1","HistoryValue","","snmp_192.168.6.2_0.ini");
	printf("value is:%s",value.c_str());
}

void test_stdlist()
{
	std::list<int *> lst;
	std::list<int *>::iterator it,itt;

	int *pn=new int;
	*pn=1;
	lst.push_back(pn);
	pn=new int;
	*pn=2;
	lst.push_back(pn);
	pn=new int;
	*pn=3;
	lst.push_back(pn);
	pn=new int;
	*pn=4;
	lst.push_back(pn);
	int i=0;
	while(i<3)
	{
		i++;
		it=lst.begin();
		while(it!=lst.end())
		{
			itt=it;
			int*pi=*it++;
			if((*pi==2)||(*pi==3))
			{
				delete pi;
				lst.erase(itt);
				continue;
			}
			printf("str:%d\n",*pi);
		}

	}

}

void test_stdlistbackfront()
{
	std::list<string> lst;
	std::list<string>::iterator it;

	lst.push_back("1");
	lst.push_back("2");
	lst.push_back("3");
	lst.push_back("4");

	string str;
	str=lst.front();

	lst.pop_front();

	lst.push_front(str);

	while(lst.size()>0)
	{
		str=lst.front();
		lst.pop_front();

		printf("value is:%s\n",str.c_str());
	}
}
void test_maxint()
{
	unsigned int rd=4294967295;
	printf("value :%u\n",rd);
}

void test_stringrfind()
{
	string str="c:\\123\\456\\";
//	printf("size:%d,rfind:%d,find last:%d\n",str.size(),str.rfind("\\"),str.find_last_of("\\"));

	int pos=str.rfind("\\");
	if(pos==str.size()-1)
		str.erase(str.rfind("\\"),1);

	printf("str:%s\n",str.c_str());
}

void test_startprocess()
{
	char queuename[256]={0};
	sprintf(queuename,"SiteView_RunMonitor_3345");
	char strCommandLine[1024]={0};
	sprintf(strCommandLine,"%s\\fcgi-bin\\MonitorSchedule.exe -e %s",::GetSiteViewRootPath().c_str(),queuename);
//	sprintf(strCommandLine,"\"%s\\fcgi-bin\\MonitorSchedule.exe\"",(char *)g_strRootPath);
	PROCESS_INFORMATION pi;
	ZeroMemory( &pi, sizeof(PROCESS_INFORMATION) );
	STARTUPINFO si;
	ZeroMemory( &si, sizeof(si) );
	si.cb=sizeof(STARTUPINFO);
	char strDir[256]={0};
	sprintf(strDir,"%s\\fcgi-bin\\",::GetSiteViewRootPath().c_str());
	printf("CommandLine :%s\n",strCommandLine);
	printf("dir :%s\n",strDir);

	if(!::CreateProcess(NULL,
		strCommandLine,
		NULL,
		NULL,
		FALSE,
		/*CREATE_NO_WINDOW*/CREATE_NEW_CONSOLE,
		NULL,
		strDir,
		&si,&pi))
	{
		
		DWORD dw=GetLastError();
		printf("error:%d\n",dw);
		::DeleteQueue(queuename);

		return ;

//		throw MSException("Create process failed in RunInProcess function");
	}

	puts("start ok");


}

void test_dbwaitmode2()
{
	std::list<string> namelist;

	if(!GetAllTableNames(namelist))
	{
		puts("Get All table names failed");
		return;
	}

	printf("size :%d\n",namelist.size());

	std::list<string>::iterator it;
	for(it=namelist.begin();it!=namelist.end();it++)
	{

		string table=*it;

		TTimeSpan ts(3,10,0,0);
		RECORDSET rds=::QueryRecords(table,ts);

		if(rds==INVALID_VALUE)
		{
			puts("Query records failed");
			return;
		}
		puts("query r ok");
	}



}
class mysocket : public ost::Socket
{
public:
	mysocket(){;};
	mysocket(SOCKET sock):Socket(sock)
	{
		;
	}
	~mysocket();



};

void test_commoncsocket_listen()
{
	in_addr ina;
	ina.S_un.S_addr;

	IPV4Address vb;
	vb=htonl(INADDR_ANY);

	ost::TCPSocket tsocket(vb,8934);
	while(tsocket.isPendingConnection(TIMEOUT_INF))
	{
		SOCKET sock=accept(tsocket.getSocket(),NULL,NULL);
		printf("socket is :%d\n",(int)sock);
		

	}

	puts("失败");

}

class mystream : public ost::SimpleTCPStream
{
public:
	mystream(){};
	~mystream(){};

	mystream(const IPV4Host &host,tpport_t port,size_t size=512):SimpleTCPStream(host,port,size)
	{
		;
	}


	bool mconnect(const IPV4Host &host,tpport_t port,size_t size=512)
	{
		this->getErrorNumber();
		Connect(host,port,size);
		return true;;
	}
    
	size_t WriteData(const void *Source, size_t Size, timeout_t timeout=0)
	{
		return writeData(Source,Size,timeout);
	}
	size_t ReadData(void *Target, size_t Size, char Separator=0, timeout_t timeout=0)
	{
		return readData(Target,Size,Separator,timeout);
	}




};

void test_commoncsocket_connect()
{
	IPV4Host ad;
	ad="127.0.0.1";
	tpport_t port=8934;

	try{
	mystream ms(ad,port);
	if(ms.isConnected())
	{
		puts("connect ok");
	}else
		puts("connect failed");
	}catch(...)
	{
		puts("exception");
	}


}

void test_stdset()
{
	std::set<int> kk;

	int k=14;
	kk.insert(k);
	k=20;
	kk.insert(k);
	k=10;
	kk.insert(k);
	k=40;
	kk.insert(k);
	k=30;
	kk.insert(k);
	k=10;
	kk.insert(k);

	std::set<int>::iterator it;
	for(it=kk.begin();it!=kk.end();it++)
		printf("the value:%d\n",*it);

	std::list<int> lint;

	lint.push_back(14);
	lint.push_back(20);
	lint.push_back(10);
	lint.push_back(40);
	lint.push_back(30);
	lint.push_back(10);

	std::list<int>::iterator lit;
	for(lit=lint.begin();lit!=lint.end();lit++)
		printf("the value:%d\n",*lit);
	




}


int main(int argc, char* argv[])
{
	test_stdset();

//	test_commoncsocket_connect();

//	test_commoncsocket_listen();

//	testmonitortempletapi();

//	test_Resource();

//	testresourceapi();

//	test_dbwaitmode2();

//	test_startprocess();

//1	test_stringrfind();

//	test_maxint();

//	test_stdlistbackfront();

//	test_stdlist();

//	test_inibug();

//	test_TTime();


//	test_refersh();

	//test_mq_create("TestSocketSpeed",1);

	//test_mq_push("TestSocketSpeed");

//	test_mq_pop("big_mq_len=1500_1");

//	test_mq_blockpop("SiteView70-Alert");

//	test_mq_getqueuename();

//	test_mq_getmessagecount("RefreshMessage");

//	test_mq_clearqueue("RefreshMessage");

//	test_mq_deletequeue("RefreshMessage");

//	test_mq_popbyobject("RefreshMessage");

//	test_mq_blockpopbyobject("test-9-1");

//	test_mq_peekmessage("RefreshMessage");

//	test_mq_blockpeekmessage("RefreshMessage");

//	test_mq_peekbyobject("RefreshMessage");

//	test_mq_blockpeekbyobject("RefreshMessage");






//	testmutilthreadacessdb();
//	testmemory();

//	::Sleep(100000000);
//	testinsertrecords("1.1.4.1",200);
//	testqueryrecordapi("1.6.17.1");

//	testdeletetable("1.2.1");

//	testquerydynapi("1.2.1");
//	testcreatetableapi("1.1.4.1");

//	testinserterrorrecordapi("1.2.3.8");
//	testappendrecordapi("1.2.3.8");
	

//	testcreatetableapi();
	
//	testcreatedb();
//	InsertRecord();

//	testquerydbrecord("1.2.3.5.1000");
//	testdbquerydyn();

//	testloaddb();
//	if(!testinserttable(1000))
//		puts("inserttable failed");

//	testpagesize();
//	testaddnewpage();
//	TestPage();
//	TestPageLoad();
//	testfilefind();

//	Testmaxinifile();
	//TestPageLoad();

	//testdisksector();

//	testmapfile();

//	test_rset test;
//	printf("int is :%d,str is :%s\n",test->m_int,test->m_str.c_str());


//	int n=test.GetValue("int",0);
//	printf("k is :%d\n",n);
//	testvector();

//	testtaskapi2();
//	testinifileapi();
//	testentitytempletdelete();
//	testentitytempletgetapi();
//	testentitytempletapi();

//	testentitygroupgetapi();

//	testentitygroupdelete();
//	testentitygroupapi();
//	testlistitemapi();

//	testinifileapi();

//	testdelandenumgroup();

//	testtaskapi2();
//	testgeneralapi();

//	testmonitortempletapi();

//	testentityapi();
//	testtempletenum();

//	testmonitorapi2();
//	testenumgroupsubgroup();
//	testgroupapi();
//	testcreatemonitortemplet();
//	testdeletegroup();
//	testenumsvsesubgroup();
//	testdeleteentity();
//	testenumgroupsubentity();
//	testenumentitysubmonitor();
//	testgetcountapi();
//	testdeleteapi();
//	testgetcountapi();
//	for(int i=0;i<1000;i++)
//    	testgetcountapi();

//	testmonitorapi2();

//	testmonitorapi();

//	testentityapi2();

//   testentityapi();
//	testgroupapi2();
//	testgroupapi();
//	testresourceapi2();
//	testresourceapi();
//	testsvseapi();
//	testupdatesvse();
//	testmonitortempletapi();
//	testupdatemonitortemplet();

	//testref();

	//test1();

	//testmyhashtable();
	//testtree();

	//testelement();

	//testhashtable();

	//testfile();

	//teststringhashtable();

	//teststdhashtable();

	//testmytree();

	//testhashtableiterator();

	//testbuffer();

	//testcast();

//	testsvapi();

	//testnewstringmap();

	//testlistitemapi();

	//testmonitortempletapi();



	return 1;

/*	std::hash_map<char *,char *> map;

	map["dddd"]="sdddd";
	printf("value is :%s\n",map["dddss"]);
*/
	
	/*std::vector<int> dd;
	dd.reserve(10);
	dd[0]=12;
	dd[1]=23;
	dd[2]=3;
	std::vector<int>::iterator it;
	for(int i=0;i<dd.capacity();i++)
	{
		dd[i]=1;
		printf("value is :%d\n",dd[i]);
	}

	dd.resize(20,10);
	for(int i=0;i<dd.capacity();i++)
	{
		printf("value :%d\n",dd[i]);
	}
	//dd[4]=5;
	printf("size is :%d,value:%d\n",dd.capacity(),dd.size());*/




}

