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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import siteview.IAutoTaskExtension;
import system.Math;
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
			String javakey = this.getMonitorParam(ecckey);
//			relationship=this.getAlertConditionUnit(javakey);
			if(ecckey.equals("EccType"))
			{
				monitortype =bo.GetField("EccType").get_NativeValue()
						.toString();
			}
			
			if (javakey != null) {
				if(javakey.equals("_notLogToTopaz")||javakey.equals("_verifyError")||javakey.equals("_disabled")){
					if(bo.GetField(al.get_Item(i).toString()).get_NativeValue().toString().equals("true")){
						map.put(javakey,"on");
					}else{
						continue;
					}
				}else{
					map.put(javakey, bo.GetField(al.get_Item(i).toString())
							.get_NativeValue().toString());
				}
				
			} else {
				map.put(ecckey, bo.GetField(al.get_Item(i).toString())
						.get_NativeValue().toString());
			} 
		} 
		relationship=this.getAlertConditionUnit(monitortype);
		Relationship rs = bo.GetRelationship(relationship);
		BusinessObjectCollection list = rs.get_BusinessObjects();
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
				map.put("_classifier", returnitem + " "+comparison+" "+ parameter + " " + "good");
			} else if (status.equals("Warning")) {
				map.put("_classifier", returnitem + " "+comparison+" "+ parameter + " " + "warning");
			} else if (status.equals("Error")) {
				map.put("_classifier", returnitem += " "+comparison+" "+ parameter + " " + "error");
			} else if (status.equals("Empty")) {
				map.put("_classifier", returnitem += " "+comparison+" "+ parameter + " " + "empty");
			}

		}
		
		if(map.get("timeUnitSelf").equals("Minute")){
			Double i=Double.parseDouble(map.get("_frequency"))*60;
			map.put("_frequency", i+"");
		}else if(map.get("timeUnitSelf").equals("Hour")){
			Double i=Double.parseDouble(map.get("_frequency"))*3600;
			map.put("_frequency", i+"");
		}else if(map.get("timeUnitSelf").equals("Day")){
			Double i=Double.parseDouble(map.get("_frequency"))*86400;
			map.put("_frequency", i+"");
		}
		
		if(map.get("ErrorFrequency").equals("Minute")){
			Double i=Double.parseDouble(map.get("_errorFrequency"))*60;
			map.put("_errorFrequency", i+"");
		}else if(map.get("ErrorFrequency").equals("Hour")){
			Double i=Double.parseDouble(map.get("_errorFrequency"))*3600;
			map.put("_errorFrequency", i+"");
		}else if(map.get("ErrorFrequency").equals("Day")){
			Double i=Double.parseDouble(map.get("_errorFrequency"))*86400;
			map.put("_errorFrequency", i+"");
		}
		
		try {
			addMonitor(map);
			
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

	void addMonitor(Map<String, String> map) throws SiteViewException,
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
	//根据监测器类型得到该监测器报警条件关系的名称
	private static String getAlertConditionUnit(String param)
	{
		String filePath;
		String RootFilePath=System.getProperty("user.dir");
		filePath =RootFilePath +"\\itsm_monitoralertconditionUnit.properties";
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
	public static void main(String args[]) {
		
	}
}
