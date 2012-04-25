/*
 * server side object --> server side map list -> rmi -> client side map list -> client side object
with the client side object, the methonds can be used

 * */

package COM.dragonflow.Api;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.jglUtils;

public class APIRmiClient{
	
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

	  }
	  catch(RemoteException e){
		  e.printStackTrace();
	  }
	  catch(NotBoundException e){
		  System.err.println(e);
	  }
  }
} 