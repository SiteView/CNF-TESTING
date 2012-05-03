package COM.dragonflow.Api;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

public class ApiRmiServer extends java.rmi.server.UnicastRemoteObject implements
		APIInterfaces {
	String hostname;
	Registry registry;
	APIGroup apigroup;
	APIMonitor apimonitor;

	public ApiRmiServer() throws RemoteException {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			// byte[] ipAddr = addr.getAddress();

			// Get hostname
			hostname = addr.getHostName();
		} catch (Exception e) {
			System.out.println("can't get inet address.");
		}
		int port = 3232;
		System.out.println("RMI server start at this address=" + hostname
				+ ",port=" + port);
		try {
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("kernelApiRmiServer", this);

			apigroup = new APIGroup();
			apimonitor = new APIMonitor();
		} catch (RemoteException e) {
			System.out.println("remote exception" + e);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public List<Map<String, Object>> getAllGroupInstances()
			throws SiteViewException {
		List<MonitorGroup> mgs = apigroup.getAllGroupInstances();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (MonitorGroup mg : mgs) {
			SSInstanceProperty assinstanceproperty1[] = apigroup
					.getInstanceProperties(mg.getProperty("_id"),
							APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
			Map<String, Object> nodedata = new HashMap<String, Object>();
			for (int k = 0; k < assinstanceproperty1.length; k++) {
				nodedata.put(assinstanceproperty1[k].getName(),
						assinstanceproperty1[k].getValue());
			}

			nodedata.put("_id", mg.getFullID());
			list.add(nodedata);
		}
		return list;
	}

	public List<Map<String, Object>> getTopLevelGroupInstances()
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			APIGroup apimg = new APIGroup();
			ArrayList<MonitorGroup> mgs = apigroup.getTopLevelGroupInstances();

			for (MonitorGroup mg : mgs) {
				SSInstanceProperty assinstanceproperty1[] = apigroup
						.getInstanceProperties(mg.getProperty("_id"),
								APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
				Map<String, Object> nodedata = new HashMap<String, Object>();
				for (int k = 0; k < assinstanceproperty1.length; k++) {
					nodedata.put(assinstanceproperty1[k].getName(),
							assinstanceproperty1[k].getValue());
				}
				nodedata.put("_id", mg.getFullID());
				list.add(nodedata);
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;

	}

	public List<Map<String, Object>> getChildGroupInstances(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apigroup.getChildGroupInstances(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					MonitorGroup mg = (MonitorGroup) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apigroup
							.getInstanceProperties(mg.getProperty("_id"),
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						nodedata.put(assinstanceproperty1[k].getName(),
								assinstanceproperty1[k].getValue());
					}
					nodedata.put("_id", mg.getFullID());
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public void deleteGroup(String groupID) throws RemoteException,
			SiteViewException {
		apigroup.delete(groupID);
	}

	public void deleteMonitor(String monitorID, String groupID)
			throws RemoteException, SiteViewException {
		apimonitor.delete(monitorID, groupID);
	}

	public List<Map<String, Object>> getMonitorsForGroup(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apimonitor.getMonitorsForGroup(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					AtomicMonitor monitor = (AtomicMonitor) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apimonitor
							.getInstanceProperties(monitor.getProperty("_id"),
									groupID,
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						nodedata.put(assinstanceproperty1[k].getName(),
								assinstanceproperty1[k].getValue());
					}
					nodedata.put("_id", monitor.getFullID());
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public int getNumOfMonitorsForGroup(String groupID) throws RemoteException,
			SiteViewException {
		return apigroup.getNumOfMonitorsForGroup(groupID);
	}

	static public ArrayList<HashMap<String, String>> getMonitorsData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			APIMonitor apim = new APIMonitor();
			Collection collection = apim.getAllMonitors();
			Vector vector = (Vector) collection;

			Monitor monitor;
			int index = 0;
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				monitor = (Monitor) iterator.next();

				HashMap<String, String> ndata = new HashMap<String, String>();
				ndata.put(new String("Name"),
						monitor.getProperty(AtomicMonitor.pName));
				ndata.put(new String("GroupID"),
						monitor.getProperty(AtomicMonitor.pOwnerID));
				ndata.put(new String("MonitorID"),
						monitor.getProperty(AtomicMonitor.pID));
				ndata.put(new String("Type"),
						monitor.getProperty(AtomicMonitor.pClass));
				list.add(ndata);
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public boolean trylogin(String strUser, String strPwd)
			throws RemoteException, SiteViewException {
		jgl.Array array = COM.dragonflow.SiteView.User.findUsersForLogin(
				strUser, strPwd);
		if (array.size() > 0)
			return true;
		else
			return false;
	}

	public void createMonitor(String monitorType, String groupid,
			List<Map<String, String>> paramlist) throws RemoteException,
			SiteViewException {
		// TODO Auto-generated method stub
		int size=getPropertyCount(paramlist);
		SSInstanceProperty[] assinstanceproperty = new SSInstanceProperty[size]; 
				int j = 0;
		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
//			assinstanceproperty = new SSInstanceProperty[map.size()];
			
			for (Entry<String, String> entry : map.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				assinstanceproperty[j] = new SSInstanceProperty(k, v);
				j++;
			}
		}
		COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apimonitor
				.create(monitorType, groupid, assinstanceproperty);
	}
	private int getPropertyCount(List<Map<String, String>> paramlist)
	{
		int total=0;
		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
//			assinstanceproperty = new SSInstanceProperty[map.size()];
			total+=map.size(); 
		}
		
		return total;
	}


}
