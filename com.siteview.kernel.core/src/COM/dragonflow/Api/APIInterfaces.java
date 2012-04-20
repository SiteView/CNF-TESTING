
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

public interface APIInterfaces extends Remote
{
	ArrayList<Map<String, Object>> getAllGroupInstances()  throws RemoteException, SiteViewException;
	ArrayList<Map<String, Object>> getTopLevelGroupInstances()  throws RemoteException,SiteViewException;
    
}
