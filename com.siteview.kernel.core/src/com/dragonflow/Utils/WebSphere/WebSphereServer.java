/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;


// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WebSphereCounter

public interface WebSphereServer
    extends java.rmi.Remote
{

    public abstract com.dragonflow.Utils.WebSphere.WebSphereCounter[] getCounters(com.dragonflow.Utils.WebSphere.WebSphereCounter awebspherecounter[])
        throws java.rmi.RemoteException;

    public abstract String getBrowseData()
        throws java.rmi.RemoteException;

    public abstract String getServerName()
        throws java.rmi.RemoteException;
}
