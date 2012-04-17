
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import COM.dragonflow.SiteViewException.SiteViewException;

public interface APIInterfaces extends Remote
{
	Collection getAllGroupInstances()  throws RemoteException, SiteViewException;
	ArrayList getTopLevelGroupInstances()  throws RemoteException,SiteViewException;
    
}
