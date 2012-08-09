package SiteView.ecc.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.Api.ApiRmiServer;
import COM.dragonflow.SiteViewException.SiteViewException;

public class MonitorServer {
	 static APIInterfaces rmiServer;
	  Registry registry;
	  String serverAddress="localhost";
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

	public List<Map<String, Object>> Group(){
		// TODO Auto-generated method stub
		try {
			return rmiServer.getTopLevelGroupInstances();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Map<String, Object>> GroupChild(String groupid){
		// TODO Auto-generated method stub
		try {
			return rmiServer.getChildGroupInstances(groupid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Map<String,Object>> Getmonitor(String groupid){
		try {
			return rmiServer.getMonitorsForGroup(groupid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void savaGroup(Map<String,Object> group) {
		// TODO Auto-generated method stub
		
	}

	public void deleteGroup(String groupid) throws RemoteException, SiteViewException {
		rmiServer.deleteGroup(groupid);		
	}
	
	public List<Map<String, Object>> getMonitorsForGroup(String groupid){
		try {
			return rmiServer.getMonitorsForGroup(groupid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args){ 
	//	try {
			MonitorServer m=new MonitorServer();
			List<HashMap<String, String>> list=ApiRmiServer.getMonitorsData();
			for(int i=0;i<list.size();i++){
				System.out.println(((Map<String, String>)list.get(i)).get("_id").toString());
			}
			//m.deleteGroup(((Map<String, String>)list.get(1)).get("_id").toString());
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println(1);
//		} catch (SiteViewException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println(2);
//		}
		
	}
	public List ss(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		return list;
	}
}
