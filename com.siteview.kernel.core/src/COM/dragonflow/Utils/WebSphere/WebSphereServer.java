/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.WebSphere;


// Referenced classes of package COM.dragonflow.Utils.WebSphere:
// WebSphereCounter

public interface WebSphereServer
    extends java.rmi.Remote
{

    public abstract COM.dragonflow.Utils.WebSphere.WebSphereCounter[] getCounters(COM.dragonflow.Utils.WebSphere.WebSphereCounter awebspherecounter[])
        throws java.rmi.RemoteException;

    public abstract String getBrowseData()
        throws java.rmi.RemoteException;

    public abstract String getServerName()
        throws java.rmi.RemoteException;
}
