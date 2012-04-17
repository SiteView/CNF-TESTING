package COM.dragonflow.Api;

import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;

public class APIRmiClient{
	
  static public void main(String args[]){
	  APIInterfaces rmiServer;
	  Registry registry;
	  String serverAddress=args[0];
	  String serverPort=args[1];
	  String text=args[2];
	  System.out.println("sending " + text + " to " +serverAddress + ":" + serverPort);
	  try{
		  registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
		  
	  rmiServer=(APIInterfaces)(registry.lookup("rmiServer"));
	  // call the remote method
	  rmiServer.getTopLevelGroupInstances();
	  }
	  catch(RemoteException e){
	  e.printStackTrace();
	  }
	  catch(NotBoundException e){
	  System.err.println(e);
	  }
	  }
} 