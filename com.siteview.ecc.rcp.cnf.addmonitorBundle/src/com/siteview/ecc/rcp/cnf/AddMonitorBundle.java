package com.siteview.ecc.rcp.cnf;

import java.io.BufferedInputStream;
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
		Relationship rs = bo.GetRelationship("MemoryContainsAlarmMemory");
		BusinessObjectCollection list = rs.get_BusinessObjects();
		for (int i = 0; i < bo.get_FieldNames().get_Count(); i++) {
			String ecckey = al.get_Item(i).toString();
			String javakey = this.getMonitorParam(ecckey);
			// System.out.println("itsmkey:" + ecckey + " ; 9.2key:" + javakey);
			/** Memory monitor data example begin **/
			// map.put("class", "MemoryMonitor");
			// map.put("page", "monitor");
			// map.put("account", "administrator");
			// map.put("ordering", "4");
			// map.put("_schedule", "");
			// map.put("error-comparison0", ">");
			// map.put("warning-condition0", "default");
			// map.put("group", "testGroup");
			// map.put("_errorFrequency", "");
			// map.put("_frequency", "10");
			// map.put("_description", "none");
			// map.put("id", "");
			// map.put("good-parameter0", "");
			// map.put("error-parameter0", "");
			// map.put("_errorFrequencyUnits", "minutes");
			// map.put("_dependsCondition", "good");
			// map.put("rview", "");
			// map.put("_machine", "\\\\192.168.9.131");
			// map.put("counters", "");
			// map.put("_monitorDescription", "");
			// map.put("good-comparison0", ">=");
			// map.put("_name", "MeM:192.168.9.131");
			// map.put("detail", "");
			// map.put("_dependsOn", "");
			// map.put("operation", "RefreshMonitor");
			/** Memory monitor data example end **/
			if (javakey != null) {
				map.put(javakey, bo.GetField(al.get_Item(i).toString())
						.get_NativeValue().toString());
			} else {
				map.put(ecckey, bo.GetField(al.get_Item(i).toString())
						.get_NativeValue().toString());
			}
			// System.out.println("key:>>"
			// + al.get_Item(i).toString()
			// + "value:>>"
			// + bo.GetField(al.get_Item(i).toString()).get_NativeValue()
			// .toString());
		}
		for (int i = 0; i < list.get_Count(); i++) {
			BusinessObject bosun = (BusinessObject) list.get_Item(i);
			String status = bosun.GetField("AlarmStatus").get_NativeValue()
					.toString();
			String comparison = bosun.GetField("Operator").get_NativeValue()
					.toString();
			String parameter = bosun.GetField("AlramValue").get_NativeValue()
					.toString();
			String returnitem = bosun.GetField("MemoryReturnItem").get_NativeValue().toString();
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
		filePath = "itsm_siteview9.2.properties";
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
		filePath = "itsm_eccmonitorparams.properties";
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
		System.out.println(getMonitorType("Memory"));

	}
}
