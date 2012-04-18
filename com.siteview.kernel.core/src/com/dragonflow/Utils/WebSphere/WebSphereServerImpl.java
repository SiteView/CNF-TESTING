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

import java.rmi.RemoteException;

// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WebSphereServer, WebSphereConnectionProperties, WebSphereCounter

public class WebSphereServerImpl extends java.rmi.server.UnicastRemoteObject
    implements com.dragonflow.Utils.WebSphere.WebSphereServer
{

    private com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties connProps;
    private com.dragonflow.SiteView.WebSphereMonitorImpl wsMonitoring;

    public WebSphereServerImpl(com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties websphereconnectionproperties)
        throws java.rmi.RemoteException
    {
        connProps = websphereconnectionproperties;
        String s = "com.dragonflow.SiteView.WebSphereMonitor" + connProps.getAPI();
        if(wsMonitoring == null)
        {
            try
            {
                wsMonitoring = (com.dragonflow.SiteView.WebSphereMonitorImpl)java.lang.Class.forName(s).getConstructor(new java.lang.Class[] {
                    String.class, String.class, String.class, String.class
                }).newInstance(new java.lang.Object[] {
                    connProps.getServerName(), java.lang.Integer.toString(connProps.getPort()), connProps.getUsername(), connProps.getPassword()
                });
            }
            catch(java.lang.Exception exception)
            {
                java.lang.System.err.println("Exception occurred while instantiating " + s + ": " + exception.getMessage());
                java.lang.System.err.println("Exception toString(): " + exception.toString());
                exception.printStackTrace(java.lang.System.err);
                java.rmi.RemoteException remoteexception = new RemoteException("A remote WebSphereServer could not be created due to an exception: " + exception);
                throw remoteexception;
            }
        }
    }

    public String getBrowseData()
        throws java.rmi.RemoteException
    {
        StringBuffer stringbuffer = new StringBuffer();
        if(wsMonitoring.getCounterList(stringbuffer))
        {
            return stringbuffer.toString();
        } else
        {
            java.rmi.RemoteException remoteexception = new RemoteException(stringbuffer.toString());
            throw remoteexception;
        }
    }

    public com.dragonflow.Utils.WebSphere.WebSphereCounter[] getCounters(com.dragonflow.Utils.WebSphere.WebSphereCounter awebspherecounter[])
        throws java.rmi.RemoteException
    {
        return wsMonitoring.getCounterValues(awebspherecounter);
    }

    public String getServerName()
        throws java.rmi.RemoteException
    {
        return connProps.getServerName();
    }
}
