
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
	int getNumOfMonitorsForGroup(String groupid)  throws RemoteException,SiteViewException;
	void deleteGroup(String groupId)  throws RemoteException,SiteViewException;
	void deleteMonitor(String monitorId,String groupId)  throws RemoteException,SiteViewException;
	boolean trylogin(String strUser, String strPwd) throws RemoteException,SiteViewException;	
}
