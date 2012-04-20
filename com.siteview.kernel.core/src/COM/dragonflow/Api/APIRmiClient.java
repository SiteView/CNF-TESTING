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
import java.util.Map;

import jgl.HashMap;

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
		  
		  rmiServer=(APIInterfaces)(registry.lookup("kernelRmiServer"));
		  // call the remote method
		  ArrayList<Map<String, Object>> groups = rmiServer.getTopLevelGroupInstances();
		  for(Map<String, Object> group : groups) {
			  System.out.println("name:"+group.get("Name")+",id:"+group.get("GroupID"));
		  }
		  
		  ArrayList<Map<String,Object>> mgList = rmiServer.getAllGroupInstances();
		  ArrayList<MonitorGroup> mgobjList = new  ArrayList<MonitorGroup>(); 
		  
		  for(Map<String, Object> mg:mgList) {
			  mg.put("_class","MonitorGroup");
			  mgobjList.add((MonitorGroup) SiteViewObject.createObject(jglUtils.toJgl(mg)));
		  }
		  
		  for(MonitorGroup mgObj:mgobjList) {
			  System.out.println("name:"+mgObj.getProperty("_name")+",id:"+mgObj.getProperty("_id"));
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