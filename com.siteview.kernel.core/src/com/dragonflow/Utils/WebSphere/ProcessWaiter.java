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
// WebSphereProcessProperties

public class ProcessWaiter extends java.lang.Thread {

    com.dragonflow.Utils.WebSphere.WebSphereProcessProperties wsProcessProps;

    java.lang.Process process;

    public ProcessWaiter(com.dragonflow.Utils.WebSphere.WebSphereProcessProperties websphereprocessproperties) {
        wsProcessProps = websphereprocessproperties;
        process = wsProcessProps.getProcess();
    }

    public ProcessWaiter(java.lang.Process process1) {
        wsProcessProps = null;
        process = process1;
    }

    public void run() {
        for (boolean flag = false; !flag; flag = waitForProcess()) {
        }
        if (process.exitValue() != 0) {
            com.dragonflow.Log.LogManager.log("Error", "Exit code of WebSphereService process was nonzero: " + process.exitValue());
        }
        if (wsProcessProps != null) {
            wsProcessProps.setRunning(false);
        }
    }

    private boolean waitForProcess() {
        try {
            process.waitFor();
        } catch (java.lang.InterruptedException interruptedexception) {
            com.dragonflow.Log.LogManager.log("Error", "Caught InterruptedException while waiting for WebSphereService Process, will wait again.");
            return false;
        }
        return true;
    }
}
