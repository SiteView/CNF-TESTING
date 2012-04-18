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
// WebSphereConnectionProperties, WebSphereServer

public interface WebSphereService extends java.rmi.Remote {

    public static final String URL_PROPERTY = "registryURL";

    public static final String DEBUG_PROPERTY = "debug";

    public static final String HEART_BEAT_FREQUENCY = "heartBeatFrequency";

    public static final String HOST = "host";

    public static final String PORT = "port";

    public static final String TOKEN_PROPERTY = "token";

    public abstract com.dragonflow.Utils.WebSphere.WebSphereServer getServer(com.dragonflow.Utils.WebSphere.WebSphereConnectionProperties websphereconnectionproperties) throws java.rmi.RemoteException;

    public abstract long getToken() throws java.rmi.RemoteException;
}
