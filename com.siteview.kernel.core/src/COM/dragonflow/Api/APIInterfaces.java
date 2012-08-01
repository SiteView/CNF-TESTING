
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import jgl.Array;
import jgl.HashMap;

import COM.dragonflow.SiteView.Machine;
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
	void createMonitor(String monitorType, String groupid,List<Map<String, String>> paramlist) throws RemoteException,SiteViewException;
	String getMonitorCounters(Map parmsmap)throws RemoteException,SiteViewException;
	String getSysOid(Map<String,String> map)throws Exception;
	void adjustGroups(String s,String s1,String s2)throws Exception;
	void writeRemoteMachineToFile(String remoteMachineInfo)throws Exception;
	String[] doTestMachine(String s,String hostname)throws Exception;
}
