package com.siteview.ecc.rcp.cnf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.omg.CORBA.Current;

import siteview.IAutoTaskExtension;
import system.Math;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;
import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import Siteview.Windows.Forms.ConnectionBroker;
//import COM.dragonflow.StandardMonitor.IEnumerator;
//import COM.dragonflow.StandardMonitor.ISiteviewApi;
//import COM.dragonflow.StandardMonitor.SiteviewQuery;
//import COM.dragonflow.StandardMonitor.XmlElement;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectCollection;
import Siteview.Api.Field;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.Relationship;

public class AddMonitorBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public static String oid="1.3.6.1.2.1.1.2";
	public AddMonitorBundle() {}

	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
		try {
			addMonitor("GroupId="+groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	void addMonitor(String s) throws SiteViewException,
			ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
			rmiServer.adjustGroups("", s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getMonitorType(String monitorType) {
		String filePath; 
		String RootFilePath=System.getProperty("user.dir");
		filePath = RootFilePath+"\\itsm_siteview9.2.properties"; 
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(monitorType);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getMonitorParam(String param) {
		String filePath; 

		String RootFilePath=System.getProperty("user.dir");
		filePath =RootFilePath+ "\\itsm_eccmonitorparams.properties"; 
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(param);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private static String getMonitorReturnItem(String param)
	{
		String filePath;
		String RootFilePath=System.getProperty("user.dir");
		filePath =RootFilePath +"\\itsm_monitorreturnitem.properties";
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(param);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private void FilterMonitor(Map<String, String> map,String monitortype)
	{
		if(monitortype.equals("File"))
		{
			String cccf=map.get("CheckContentChangesFile").toString();
			if(cccf.equals("compare to last contents"))
			{
				map.remove("CheckContentChangesFile");
				map.put("_checkContent", "on");
			}
			else if(cccf.equals("compare to saved contents")||cccf.equals("reset saved contents"))
			{ 
				map.remove("CheckContentChangesFile");
				map.put("_checkContent", "baseline");
			}
			else if(cccf.equals("no content checking"))
			{
				map.remove("CheckContentChangesFile");
			}
			if(map.get("NoErroronFileNotFound").toString().equals("true"))
			{
				map.remove("NoErroronFileNotFound");
				map.put("_noFileCheckExist", "on");
			}
		}
		
		//mail监测器字段过滤
		if(map.get("_useIMAP")!=null && map.get("_useIMAP").equals("IMAP4")){
			map.put("_useIMAP", "true");
		}else if(map.get("_useIMAP")!=null && map.get("_useIMAP").equals("POP3")){
			map.remove("_useIMAP");
		}
		if(map.get("_receiveOnly")!=null && map.get("_receiveOnly").equals("Send & Receive")){
			map.remove("_receiveOnly");
		}
		//eBusiness Chain监测器字段过滤
		if(map.get("_whenError")!=null && map.get("_whenError").equals("continue")){
			map.remove("_whenError");
		}
		if(map.get("_delay")!=null && (!map.get("_delay").equals(""))){
			float f=Float.parseFloat(map.get("_delay"));
			int j=(int)f;
			map.put("_delay", j+"");
		}
		
		int j;//时间单位转化
		if(!map.get("_frequency").equals("")){
			if(map.get("timeUnitSelf").equals("Minute")){
				float i=Float.parseFloat(map.get("_frequency"))*60;
				j=(int)i;
				map.put("_frequency", j+"");
			}else if(map.get("timeUnitSelf").equals("Hour")){
				float i=Float.parseFloat(map.get("_frequency"))*3600;
				j=(int)i;
				map.put("_frequency", j+"");
			}else if(map.get("timeUnitSelf").equals("Day")){
				float i=Float.parseFloat(map.get("_frequency"))*86400;
				j=(int)i;
				map.put("_frequency", j+"");
			}else if(map.get("timeUnitSelf").equals("Second")){
				float i=Float.parseFloat(map.get("_frequency"));
				j=(int)i;
				map.put("_frequency", j+"");
			}
		}
		if(!map.get("_errorFrequency").equals("")){
			if(map.get("ErrorFrequency").equals("Minute")){ 
				float i=Float.parseFloat(map.get("_errorFrequency"))*60; 
				j=(int)i;
				map.put("_errorFrequency", j+"");
			}else if(map.get("ErrorFrequency").equals("Hour")){
				float i=Float.parseFloat(map.get("_errorFrequency"))*3600;
				j=(int)i;
				map.put("_errorFrequency", j+"");
			}else if(map.get("ErrorFrequency").equals("Day")){
				float i=Float.parseFloat(map.get("_errorFrequency"))*86400;
				j=(int)i;
				map.put("_errorFrequency", j+"");
			}else if(map.get("ErrorFrequency").equals("Second")){
				float i=Float.parseFloat(map.get("_errorFrequency"));
				j=(int)i;
				map.put("_errorFrequency", j+"");
			}
		}
		//如有超时字段，将该转换成int类型存储到siteview9.2中				 
		if(map.get("_timeout")!=null){
			float timeoutdb=Float.parseFloat(map.get("_timeout").toString());
			int timeout=(int)timeoutdb; 
			map.put("_timeout", timeout+"");
		}
		if(map.get("_retryDelay")!=null)
		{
			float retryDelay=Float.parseFloat(map.get("_retryDelay").toString());
			int retry=(int)retryDelay; 
			map.put("_retryDelay", retry+"");
		}
		//过滤url监测器
		if(map.get("_checkContent")!=null && map.get("_checkContent").equals("no content checking")){
			map.remove("_checkContent");
		}else if(map.get("_checkContent")!=null && (map.get("_checkContent").equals("compare to saved contents")||map.get("_checkContent").equals("reset saved contents"))){
			map.put("_checkContent", "baseline");
			map.put("_checkContentResetTime", System.currentTimeMillis()+"");
		}else if(map.get("_checkContent")!=null && map.get("_checkContent").equals("compare to last contents")){
			map.put("_checkContent", "on");
			map.put("_checkContentResetTime", System.currentTimeMillis()+"");
		}
		if(map.get("_URLDropDownEncodePostData")!=null && map.get("_URLDropDownEncodePostData").equals("Use content-type:")){
			map.put("_URLDropDownEncodePostData", "contentTypeUrlencoded");
		}else if(map.get("_URLDropDownEncodePostData")!=null && map.get("_URLDropDownEncodePostData").equals("force url encoding")){
			map.put("_URLDropDownEncodePostData", "forceEncode");
		}else if(map.get("_URLDropDownEncodePostData")!=null && map.get("_URLDropDownEncodePostData").equals("force No url encoding")){
			map.put("_URLDropDownEncodePostData", "forceNoEncode");
		}
		if(map.get("_whenToAuthenticate")!=null && map.get("_whenToAuthenticate").equals("Use Global Preference")){
			map.remove("_whenToAuthenticate");
		}else if(map.get("_whenToAuthenticate")!=null && map.get("_whenToAuthenticate").equals("Authenticate first request")){
			map.put("_whenToAuthenticate", "authOnFirst");
		}else if(map.get("_whenToAuthenticate")!=null && map.get("_whenToAuthenticate").equals("Authenticate if requested")){
			map.put("_whenToAuthenticate", "authOnSecond");
		}
		//过滤linkCheck字段
		if(map.get("MaxHops")!=null && map.get("MaxHops").equals("no limit")){
			if(map.get("_maxSearchDepth").equals("")){
				map.put("_maxSearchDepth", "100");
			}
		}else if(map.get("MaxHops")!=null && map.get("MaxHops").equals("main page links")){
			if(map.get("_maxSearchDepth").equals("")){
				map.put("_maxSearchDepth", "1");
			}
		}
		if(map.get("_maxLinks")!=null && (!map.get("_maxLinks").equals(""))){
			float f=Float.parseFloat(map.get("_maxLinks"));
			j=(int)f;
			map.put("_maxLinks", j+"");
		}
		//Windows Performance Counter过滤
		if(map.get("_pmcfile")!=null && map.get("_pmcfile").equals("(Custom Object)")){
			map.put("_pmcfile","none");
		}
		if(map.get("_scale")!=null && map.get("_scale").equals("")){
			if(map.get("Scale").equals("kilobytes")){
				map.put("_scale", "9.765625E-4");
			}else if(map.get("Scale").equals("megabytes")){
				map.put("_scale", "9.536743E-7");
			}else if(map.get("Scale").equals("Other")){
				map.remove("_scale");
			}else{
				map.put("_scale", map.get("Scale"));
			}
		}
	}
	public void setOid(Map<String,String> map)
	{
		String host,community;
		int timeout=1,retry=2;
		timeout=800;//Integer.valueOf(map.get("_timeout"));
		retry=800;//Integer.valueOf(map.get("_retryDelay"));
		host=map.get("_host");
		community=map.get("_community");
		String sysoid="";
		map.put("_oid", oid);
		try {
			sysoid = rmiServer.getSysOid(map);
			map.put("_oid", getOid(sysoid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	public static String getOid(String sysoid)
	{
		ISiteviewApi siteviewApi=ConnectionBroker.get_SiteviewApi();
		BusinessObject bo;
		SiteviewQuery siteviewquery_interfaceTable = new SiteviewQuery();
		siteviewquery_interfaceTable.AddBusObQuery("SpecialOidList", QueryInfoToGet.All);
		XmlElement xmlElementscanconfigid = siteviewquery_interfaceTable.get_CriteriaBuilder().FieldAndValueExpression(
		"SysOid", Operators.Equals, sysoid);  
		siteviewquery_interfaceTable.set_BusObSearchCriteria(xmlElementscanconfigid);
		ICollection interfaceTableCollection = siteviewApi.get_BusObService().get_SimpleQueryResolver()
		.ResolveQueryToBusObList(siteviewquery_interfaceTable);
		IEnumerator interfaceTableIEnum = interfaceTableCollection.GetEnumerator(); 
		 
		String spevalue="";
		while(interfaceTableIEnum.MoveNext())
		{
			bo =(BusinessObject)interfaceTableIEnum.get_Current();
			spevalue = bo.GetField("SpeValue").get_NativeValue().toString();  
		} 
		String returnlist[]=spevalue.split(",");
		for(int i=0;i<returnlist.length;i++)
		{
			if(returnlist[i].contains("cpuDutyUsed"))
			{
				return returnlist[i].substring(returnlist[i].indexOf("=")+1,returnlist[i].length());
			}
		}
//
		return null;
	}  
	
	public static void main(String args[]) { 
		System.out.println(System.currentTimeMillis());
	}
}
