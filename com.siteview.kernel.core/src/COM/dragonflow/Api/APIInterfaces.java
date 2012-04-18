
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

public interface APIInterfaces extends Remote
{
	ArrayList<MonitorGroup> getAllGroupInstances()  throws RemoteException, SiteViewException;
	ArrayList<HashMap<String,String>> getTopLevelAllowedGroupInstances()  throws RemoteException,SiteViewException;
	ArrayList getChildGroupInstances(String strId)  throws RemoteException,SiteViewException;
	ArrayList getChildMonitors(String strId)  throws RemoteException,SiteViewException;	

}
