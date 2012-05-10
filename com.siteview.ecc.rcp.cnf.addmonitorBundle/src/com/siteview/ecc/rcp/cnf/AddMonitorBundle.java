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
import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectCollection;
import Siteview.Api.Relationship;

public class AddMonitorBundle implements IAutoTaskExtension {

	public AddMonitorBundle() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String run(Map<String, Object> params) {
		// TODO Auto-generated method stub 
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		system.Collections.ArrayList al = new system.Collections.ArrayList();
		al.AddRange(bo.get_FieldNames());
		Map<String, String> map = new HashMap<String, String>();
		String monitortype=null;
		String relationship=null;
		for (int i = 0; i < bo.get_FieldNames().get_Count(); i++) {
			String ecckey = al.get_Item(i).toString();
			String javakey=this.getMonitorParam(ecckey);
//			relationship=this.getAlertConditionUnit(javakey);
			if(ecckey.equals("EccType"))
			{
				monitortype =bo.GetField("EccType").get_NativeValue()
						.toString();
			}
	 
			if (javakey != null) {
				String value;
				//判断值是否是逻辑值，如果是则对值进行替换，否则不变
				if(bo.GetField(al.get_Item(i).toString()).get_NativeValue().toString().equals("true")){
					value="on";
				}else if(bo.GetField(al.get_Item(i).toString()).get_NativeValue().toString().equals("false")){
					continue;
				}else{
					value=bo.GetField(al.get_Item(i).toString())
							.get_NativeValue().toString();
				}
					map.put(javakey,value );

				
			} else {
				map.put(ecckey, bo.GetField(al.get_Item(i).toString())
						.get_NativeValue().toString());
			} 
		} 
		
		ICollection relationships=bo.get_RelationshipNames();
		system.Collections.ArrayList a2 = new system.Collections.ArrayList();
		a2.AddRange(relationships);
		Map<String,String> goodmap=new HashMap<String,String>();
		Map<String,String> warningmap=new HashMap<String,String>();
		Map<String,String> errormap=new HashMap<String,String>();
		for(int j=0;j<a2.get_Count();j++){
			Relationship rs1=bo.GetRelationship(a2.get_Item(j).toString());
			BusinessObjectCollection list=rs1.get_BusinessObjects();
			if(a2.get_Item(j).toString().contains("Alarm")){
				for (int i = 0; i < list.get_Count(); i++) {
					BusinessObject bosun = (BusinessObject) list.get_Item(i);
					String status = bosun.GetField("AlarmStatus").get_NativeValue()
							.toString();
					String comparison = bosun.GetField("Operator").get_NativeValue()
							.toString();
					String parameter = bosun.GetField("AlramValue").get_NativeValue()
							.toString();
					String returnitemstr=getMonitorReturnItem(monitortype);
					String returnitem = bosun.GetField(returnitemstr).get_NativeValue().toString();
					String isAnd=bosun.GetField("isAnd").get_NativeValue().toString();
					if (status.equals("Good")) { 
						goodmap.put("_classifier", returnitem + " "+comparison+" "+ parameter + "	" + "good");
					} else if (status.equals("Warning")) {
						warningmap.put("_classifier", returnitem + " "+comparison+" "+ parameter + "	" + "warning");
					} else if (status.equals("Error")) { 
						errormap.put("_classifier", returnitem += " "+comparison+" "+ parameter +"	"+ "error");
					} else if (status.equals("Empty")) {
//						alertlist.add(returnitem + " "+comparison+" "+ parameter + "	" + "empty");
//						map.put("_classifier", returnitem += " "+comparison+" "+ parameter + " " + "empty");
					}

				}
			}else{
				String couter="";
				for(int i = 0; i < list.get_Count(); i++){
					BusinessObject bosun=(BusinessObject)list.get_Item(i);
					couter=couter+bosun.GetField("Name").get_NativeValue().toString();
					System.out.println(couter);
				}
				map.put("_couter",couter);
			}
		}		
		//过滤监测器
		FilterMonitor(map,monitortype);
		try {

			addMonitor(map,goodmap,warningmap,errormap); 
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	void addMonitor(Map<String, String> map,Map<String, String> goodmap,Map<String, String> warningmap,Map<String, String> errormap) throws SiteViewException,
			ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		APIInterfaces rmiServer;
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());

			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
			// List<Map<String, Object>> groups =
			// rmiServer.getTopLevelGroupInstances();
			// call the remote method
			List<Map<String, String>> paramlist = new ArrayList<Map<String, String>>();
			paramlist.add(map);
			paramlist.add(errormap);
			paramlist.add(warningmap);
			paramlist.add(goodmap); 
			String monitorType = getMonitorType(map.get("class"));
			rmiServer.createMonitor(monitorType, map.get("group"), paramlist);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println(e);
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
	public static void main(String args[]) { 
		System.out.println(System.currentTimeMillis());
	}
}
