
package COM.dragonflow.Api;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import COM.dragonflow.SiteView.AtomicMonitor;
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
			registry.rebind("kernelRmiServer", this);
			
			apigroup = new APIGroup();
			apimonitor = new APIMonitor();
		}
		catch(RemoteException e){
			System.out.println("remote exception"+ e);
		}
	}


	public ArrayList<Map<String, Object>> getAllGroupInstances() throws SiteViewException {	
		ArrayList<MonitorGroup> mgs = apigroup.getAllGroupInstances();
        ArrayList list = new ArrayList();
		for (MonitorGroup mg:mgs) {
			SSInstanceProperty assinstanceproperty1[] = apigroup.getInstanceProperties(mg.getProperty("_id"), APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
            Map<String,Object> hashmap1 = new HashMap();
            for (int k = 0; k < assinstanceproperty1.length; k ++) {
                hashmap1.put(assinstanceproperty1[k].getName(), assinstanceproperty1[k].getValue());
            }

            hashmap1.put("_id", mg.getFullID());
            list.add(hashmap1);
		}
		return list;
	}


	public ArrayList<Map<String, Object>> getTopLevelGroupInstances() throws RemoteException, SiteViewException {		
		
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try
		{
			APIGroup apimg = new APIGroup();
			ArrayList<MonitorGroup> mg = apigroup.getTopLevelGroupInstances();

			for(MonitorGroup group:mg)
			{
				Map<String, Object> nodedata = new HashMap<String, Object>();
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
