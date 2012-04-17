
package COM.dragonflow.Api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface APIInterfaces extends Remote
{
	Collection getAllGroupInstances()  throws RemoteException;
	Collection getTopLevelGroupInstances()  throws RemoteException;
    
}
