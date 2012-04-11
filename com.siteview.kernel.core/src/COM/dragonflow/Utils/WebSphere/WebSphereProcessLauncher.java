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

import java.io.File;
import java.util.HashMap;

// Referenced classes of package COM.dragonflow.Utils.WebSphere:
// WebSphereProcessProperties

public class WebSphereProcessLauncher {

    public static final int PROCESS_STARTED = 0;

    public static final int PROCESS_ALREADY_RUNNING = 1;

    public static final int PROCESS_FAILED = 2;

    private static java.util.Map webSphereRMIProcesses = java.util.Collections.synchronizedMap(new HashMap());

    private static java.rmi.registry.Registry registry = null;

    private static int registryPort = -1;

    private static int defaultRegistryPort;

    public WebSphereProcessLauncher() {
    }

    public static synchronized int launch(COM.dragonflow.Utils.WebSphere.WebSphereProcessProperties websphereprocessproperties) {
        if (registry == null) {
            try {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "No registry found, attempting to create one on port " + defaultRegistryPort);
                registryPort = COM.dragonflow.Utils.WebSphere.WebSphereProcessLauncher.createRegistry(defaultRegistryPort);
            } catch (java.rmi.RemoteException remoteexception) {
                COM.dragonflow.Log.LogManager.log("Error", "WebSphereProcessLauncher failed to create RMI registry.  Exception was: " + remoteexception.getMessage());
                return 2;
            }
        }
        COM.dragonflow.Utils.WebSphere.WebSphereProcessProperties websphereprocessproperties1 = (COM.dragonflow.Utils.WebSphere.WebSphereProcessProperties) webSphereRMIProcesses.get(websphereprocessproperties.getHashKeyAsInteger());
        if (websphereprocessproperties1 != null && websphereprocessproperties1.isRunning()) {
            return 1;
        }
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes");
        try {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Starting new WebSphereService[" + websphereprocessproperties.getHashKey() + "] in IBM JVM with command line: " + websphereprocessproperties.getCommandLine());
            java.lang.Process process = java.lang.Runtime.getRuntime().exec(websphereprocessproperties.getCommandLineArray(), null, file);
            websphereprocessproperties.registerNewProcess(process);
        } catch (java.io.IOException ioexception) {
            COM.dragonflow.Log.LogManager.log("Error", "Could not start IBM JVM for WebSphere RMI service.  Execption was: " + ioexception.getMessage());
            return 2;
        }
        webSphereRMIProcesses.put(websphereprocessproperties.getHashKeyAsInteger(), websphereprocessproperties);
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param i
     * @return
     * @throws java.rmi.RemoteException
     */
    private static int createRegistry(int i) throws java.rmi.RemoteException {
        while (true) {
            try {
                registry = java.rmi.registry.LocateRegistry.createRegistry(i);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "WebSphereProcessLauncher successfully created RMI registry on port " + i + ".");
                return i;
            } catch (java.rmi.RemoteException remoteexception) {
                if (remoteexception.getCause() instanceof java.net.BindException) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "WebSphereProcessLauncher failed to create RMI registry on port " + i + " because the port was already in use. ");
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
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_webSphereRMIRegistryPort");
        if (s.length() > 0) {
            try {
                defaultRegistryPort = java.lang.Integer.parseInt(s);
            } catch (java.lang.NumberFormatException numberformatexception) {
                defaultRegistryPort = 1099;
                COM.dragonflow.Log.LogManager.log("Error", "Could not parse WebSphere rmiregistry port from master.config:" + numberformatexception + ", using default of " + defaultRegistryPort + ".");
            }
        }
    }
}
