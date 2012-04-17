package COM.dragonflow.Api;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import COM.dragonflow.SiteViewException.SiteViewException;

public class APIRmiClient{
	
  static public void main(String args[]) throws SiteViewException{
	  APIInterfaces rmiServer;
	  Registry registry;
	  String serverAddress= "localhost";
	  String serverPort="3232";

	  System.out.println(serverAddress + ":" + serverPort);
	  try{
		  registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
		  
		  rmiServer=(APIInterfaces)(registry.lookup("rmiServer"));
		  // call the remote method
		  ArrayList<HashMap<String, String>> groups = rmiServer.getTopLevelGroupInstances();
		  for(HashMap<String, String> group : groups) {
			  System.out.println("name:"+group.get("Name")+",id:"+group.get("GroupID"));
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