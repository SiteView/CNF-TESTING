package com.siteview.ecc.rcp.cnf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
//		Relationship bo1=bo.GetRelationship("");
//		BusinessObjectCollection bo2=bo1.get_BusinessObjects();
		for (int i = 0; i < bo.get_FieldNames().get_Count(); i++) {
			String s=al.get_Item(i).toString();
			if(s.equals("frequency")){
				s="pFrequency";
			}else if(s.equals("DNSServer")){
				s="pServer";
			}else if(s.equals("depends_condition")){
				s="pDependsCondition";
			}else if(s.equals("URLLinkCheck")){
				s="pURL";
			}else if(s.equals("TimeoutLinkCheck")){
				s="pTimeout";
			}
				map.put(s,
					bo.GetField(al.get_Item(i).toString()).get_NativeValue()
							.toString());
				System.out.println("key:"+s+"->value:"+bo.GetField(al.get_Item(i).toString()).get_NativeValue());
			
		}
		// System.out.print(al);
		// System.out.println(bo.GetField("Host").get_NativeValue());
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
//			List<Map<String, Object>> groups = rmiServer.getTopLevelGroupInstances();
			// call the remote method
			List<Map<String, String>> paramlist = new ArrayList<Map<String, String>>();
			paramlist.add(map);
			String monitorType = getMonitorType(map.get("EccType"));
			rmiServer.createMonitor(monitorType, "lili", paramlist);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println(e);
		}

	}

	private static String getMonitorType(String monitorType) {
		String filePath;
		filePath = "D:/siteview9.2¡ªitsm/itsm_siteview9.2.properties";
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
	public static void main(String args[]){
		getMonitorType("Memory");
	}
}
