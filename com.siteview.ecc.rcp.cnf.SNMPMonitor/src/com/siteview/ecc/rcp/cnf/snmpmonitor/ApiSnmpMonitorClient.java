package com.siteview.ecc.rcp.cnf.snmpmonitor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.jglUtils;

public class ApiSnmpMonitorClient {
	
	  static public void main(String args[]) throws SiteViewException, ClassNotFoundException, IllegalAccessException, InstantiationException{
		  APIInterfaces rmiServer;
		  Registry registry;
		  String serverAddress= "localhost";
		  String serverPort="3232";

		  System.out.println(serverAddress + ":" + serverPort);
		  try{
			  registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
			  
			  rmiServer=(APIInterfaces)(registry.lookup("kernelApiRmiServer"));
			  // call the remote method
			  List<Map<String, Object>> groups = rmiServer.getTopLevelGroupInstances();
			  for(Map<String, Object> group : groups) {
				  System.out.println("name:"+group.get("_name")+",id:"+group.get("_id"));
			  }
			  
			  List<Map<String,Object>> mgList = rmiServer.getAllGroupInstances();
			  List<MonitorGroup> mgobjList = new  ArrayList<MonitorGroup>(); 
			  
			  List<Map<String,Object>> submgList = rmiServer.getChildGroupInstances("test");
			  List<MonitorGroup> submgobjList = new  ArrayList<MonitorGroup>(); 
			  List<Monitor> mobjList = new  ArrayList<Monitor>(); 
			  
			  for(Map<String, Object> mg:mgList) {
				  mg.put("_class","MonitorGroup");
				  mobjList.add((MonitorGroup) SiteViewObject.createObject(jglUtils.toJgl(mg)));
			  }
			  
			  for(MonitorGroup mgObj:mgobjList) {
				  System.out.println("name:"+mgObj.getProperty("_name")+",id:"+mgObj.getProperty("_id"));
			  }
			  
			  List<Map<String, Object>> monitorList =  rmiServer.getMonitorsForGroup("test");
			  List<AtomicMonitor> monitorObjList = new  ArrayList<AtomicMonitor>(); 
			  for(Map<String, Object> monitor:monitorList) {
				  monitor.put("_class","PingMonitor");
				  AtomicMonitor m = (AtomicMonitor) SiteViewObject.createObject(jglUtils.toJgl(monitor));
				  mobjList.add(m) ;
			  }
			  
			  for(Monitor monitorObj:mobjList) {
				  System.out.println("name:"+monitorObj.getProperty("_name")+",id:"+monitorObj.getProperty("_id"));
			  }
			  Map<String, String> map=new HashMap<String, String>();
			  map.put("_host","192.168.0.248");
			  map.put("_oidIndex","0");
			  map.put("_timeout","5");
			  map.put("_snmpversion","v2");
			  map.put("_retryDelay","1");
			  map.put("_community","dragon");
			  map.put("_frequency","600");
			  map.put("error-condition0","default");
			  map.put("good-condition0","default");
			  map.put("warning-condition0","default");
			  List<Map<String, String>> paramlist=new ArrayList<Map<String,String>>();
			  paramlist.add(map);
			  for(Map<String, Object> group : groups) {
				  rmiServer.createMonitor("SNMPMonitor", group.get("_id").toString(), paramlist);
			  }
		  }
		  catch(RemoteException e){
			  e.printStackTrace();
		  }
		  catch(NotBoundException e){
			  System.err.println(e);
		  }
	  }
}
