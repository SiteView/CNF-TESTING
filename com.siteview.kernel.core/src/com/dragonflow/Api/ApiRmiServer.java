
package com.dragonflow.Api;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Group;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.MonitorGroup;
import com.dragonflow.SiteViewException.SiteViewException;

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

		ArrayList<MonitorGroup> mg = (ArrayList<MonitorGroup>) apigroup.getAllGroupInstances();

		//ArrayList<MonitorGroup> mg = apigroup.getAllGroupInstances();

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
//				group.getChildren(monitorgroup, array)
//				if(group.getParent() != null)
//					nodedata.put(new String("Parent"), group.getParent().getProperty(MonitorGroup.pGroupID));
				list.add(nodedata);
			}
		} catch (java.lang.Exception e)
		{
			System.out.println(e);
		}
		return list;
		
	}

	public ArrayList<HashMap<String, String>> getChildGroupInstances(String strId) throws RemoteException, SiteViewException {		
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		try
		{
			APIGroup apimg = new APIGroup();
			Collection collection = (Collection)apigroup.getChildGroupInstances(strId);
//			Vector vector = (Vector) collection;

			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				MonitorGroup group = (MonitorGroup) iterator.next();
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
	
	public ArrayList<HashMap<String, String>> getChildMonitors(String strId) throws RemoteException, SiteViewException {		
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		try
		{
			APIMonitor apimonitor = new APIMonitor();
			Collection collection = (Collection)apimonitor.getChildMonitors(strId);
//			Vector vector = (Vector) collection;

			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				Monitor monitor = (Monitor) iterator.next();
				HashMap<String, String> nodedata = new HashMap<String, String>();
				nodedata.put(new String("Name"), monitor.getProperty(Monitor.pName));
				nodedata.put(new String("MonitorID"), monitor.getProperty(Monitor.pID));
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
