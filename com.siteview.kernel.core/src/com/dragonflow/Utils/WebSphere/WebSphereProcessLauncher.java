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

import java.io.File;
import java.util.Map;

import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WebSphereProcessProperties

public class WebSphereProcessLauncher {

    public static final int PROCESS_STARTED = 0;

    public static final int PROCESS_ALREADY_RUNNING = 1;

    public static final int PROCESS_FAILED = 2;

    private static Map webSphereRMIProcesses = java.util.Collections.synchronizedMap(new java.util.HashMap());

    private static java.rmi.registry.Registry registry = null;

    private static int registryPort = -1;

    private static int defaultRegistryPort;

    public WebSphereProcessLauncher() {
    }

    public static synchronized int launch(com.dragonflow.Utils.WebSphere.WebSphereProcessProperties websphereprocessproperties) {
        if (registry == null) {
            try {
                com.dragonflow.Log.LogManager.log("RunMonitor", "No registry found, attempting to create one on port " + defaultRegistryPort);
                registryPort = com.dragonflow.Utils.WebSphere.WebSphereProcessLauncher.createRegistry(defaultRegistryPort);
            } catch (java.rmi.RemoteException remoteexception) {
                com.dragonflow.Log.LogManager.log("Error", "WebSphereProcessLauncher failed to create RMI registry.  Exception was: " + remoteexception.getMessage());
                return 2;
            }
        }
        com.dragonflow.Utils.WebSphere.WebSphereProcessProperties websphereprocessproperties1 = (com.dragonflow.Utils.WebSphere.WebSphereProcessProperties) webSphereRMIProcesses.get(websphereprocessproperties.getHashKeyAsInteger());
        if (websphereprocessproperties1 != null && websphereprocessproperties1.isRunning()) {
            return 1;
        }
        java.io.File file = new File(com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes");
        try {
            com.dragonflow.Log.LogManager.log("RunMonitor", "Starting new WebSphereService[" + websphereprocessproperties.getHashKey() + "] in IBM JVM with command line: " + websphereprocessproperties.getCommandLine());
            java.lang.Process process = java.lang.Runtime.getRuntime().exec(websphereprocessproperties.getCommandLineArray(), null, file);
            websphereprocessproperties.registerNewProcess(process);
        } catch (java.io.IOException ioexception) {
            com.dragonflow.Log.LogManager.log("Error", "Could not start IBM JVM for WebSphere RMI service.  Execption was: " + ioexception.getMessage());
            return 2;
        }
        webSphereRMIProcesses.put(websphereprocessproperties.getHashKeyAsInteger(), websphereprocessproperties);
        return 0;
    }

    /**
     * 
     * 
     * @param i
     * @return
     * @throws java.rmi.RemoteException
     */
    private static int createRegistry(int i) throws java.rmi.RemoteException {
        while (true) {
            try {
                registry = java.rmi.registry.LocateRegistry.createRegistry(i);
                com.dragonflow.Log.LogManager.log("RunMonitor", "WebSphereProcessLauncher successfully created RMI registry on port " + i + ".");
                return i;
            } catch (java.rmi.RemoteException remoteexception) {
                if (remoteexception.getCause() instanceof java.net.BindException) {
                    com.dragonflow.Log.LogManager.log("RunMonitor", "WebSphereProcessLauncher failed to create RMI registry on port " + i + " because the port was already in use. ");
                    i ++;
                } else {
                    throw remoteexception;
                }
            }
        }

    }

    public static int getRegistryPort() {
        return registryPort;
    }

    static {
        defaultRegistryPort = 1099;
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_webSphereRMIRegistryPort");
        if (s.length() > 0) {
            try {
                defaultRegistryPort = java.lang.Integer.parseInt(s);
            } catch (java.lang.NumberFormatException numberformatexception) {
                defaultRegistryPort = 1099;
                com.dragonflow.Log.LogManager.log("Error", "Could not parse WebSphere rmiregistry port from master.config:" + numberformatexception + ", using default of " + defaultRegistryPort + ".");
            }
        }
    }
}
