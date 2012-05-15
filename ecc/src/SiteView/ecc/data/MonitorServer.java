package SiteView.ecc.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;

public class MonitorServer {
	 APIInterfaces rmiServer;
	  Registry registry;
	  String serverAddress= "localhost";
	  String serverPort="3232";
	public MonitorServer(){
		 try{
			  registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
			  
			  rmiServer=(APIInterfaces)(registry.lookup("kernelApiRmiServer"));
		 }
		  catch(RemoteException e){
			  e.printStackTrace();
		  }
		  catch(NotBoundException e){
			  System.err.println(e);
		  }
	}

	public List Group() throws RemoteException, SiteViewException {
		// TODO Auto-generated method stub
		return rmiServer.getTopLevelGroupInstances();
	}

	public List<Map<String, Object>> GroupChild(String groupid) throws RemoteException, SiteViewException {
		// TODO Auto-generated method stub
		return rmiServer.getChildGroupInstances(groupid);
	}
	
	public List<Map<String,Object>> Getmonitor(String groupid) throws RemoteException, SiteViewException{
		return rmiServer.getMonitorsForGroup(groupid);
	}
}
