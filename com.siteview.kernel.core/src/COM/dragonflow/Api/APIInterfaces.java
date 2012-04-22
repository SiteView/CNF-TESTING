
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import COM.dragonflow.SiteViewException.SiteViewException;

public interface APIInterfaces extends Remote
{
	List<Map<String, Object>> getAllGroupInstances()  throws RemoteException, SiteViewException;
	List<Map<String, Object>> getTopLevelGroupInstances()  throws RemoteException,SiteViewException;
	List<Map<String, Object>> getChildGroupInstances(String groupid)  throws RemoteException,SiteViewException;
	List<Map<String, Object>> getMonitorsForGroup(String groupid)  throws RemoteException,SiteViewException;
	
}
