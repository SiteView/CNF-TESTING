
package COM.dragonflow.Api;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Group;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

public class ApiRmiServer extends java.rmi.server.UnicastRemoteObject implements APIInterfaces{
	String address;
	Registry registry; 
	APIGroup apigroup;
	APIMonitor apimonitor;


	public ApiRmiServer() throws RemoteException{
		try{  
			address = (InetAddress.getLocalHost()).toString();
		}
		catch(Exception e){
			System.out.println("can't get inet address.");
		}
		int port=3232; 
		System.out.println("this address=" + address +  ",port=" + port);
		try{
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("kernelApiRmiServer", this);
			
			apigroup = new APIGroup();
			apimonitor = new APIMonitor();
		}
		catch(RemoteException e){
			System.out.println("remote exception"+ e);
		}
	}


	public ArrayList<MonitorGroup> getAllGroupInstances() throws SiteViewException {
		ArrayList<MonitorGroup> mg = apigroup.getAllGroupInstances();
		return mg;
	}


	public ArrayList<HashMap<String, String>> getTopLevelAllowedGroupInstances() throws RemoteException, SiteViewException {		
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		try
		{
			APIGroup apimg = new APIGroup();
			ArrayList<MonitorGroup> mg = apigroup.getTopLevelAllowedGroupInstances();

			for(MonitorGroup group:mg)
			{
				HashMap<String, String> nodedata = new HashMap<String, String>();
				nodedata.put(new String("Name"), group.getProperty(MonitorGroup.pName));
				nodedata.put(new String("GroupID"), group.getProperty(MonitorGroup.pGroupID));
				list.add(nodedata);
			}
		} catch (java.lang.Exception e)
		{
			System.out.println(e);
		}
		return list;
		
	}
	
	static public ArrayList<HashMap<String, String>> getMonitorsData()
	{		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try
		{
			APIMonitor apim = new APIMonitor();
			Collection collection = apim.getAllMonitors();
			Vector vector = (Vector) collection;

			Monitor monitor;
			int index = 0;
			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				monitor = (Monitor) iterator.next();
				
				HashMap<String, String> ndata = new HashMap<String, String>();
				ndata.put(new String("Name"), monitor.getProperty(AtomicMonitor.pName));
				ndata.put(new String("GroupID"), monitor.getProperty(AtomicMonitor.pOwnerID));
				ndata.put(new String("MonitorID"), monitor.getProperty(AtomicMonitor.pID));
				ndata.put(new String("Type"), monitor.getProperty(AtomicMonitor.pClass));
				list.add(ndata);
			}
		} catch (java.lang.Exception e)
		{
			System.out.println(e);
		}
		return list;
	}
}
