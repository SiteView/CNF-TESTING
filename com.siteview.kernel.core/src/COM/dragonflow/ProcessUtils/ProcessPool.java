/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.ProcessUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Vector;

import COM.dragonflow.Utils.CounterLock;

import com.recursionsw.jgl.Array;

// Referenced classes of package COM.dragonflow.ProcessUtils:
// SingleProcess, ProcessMgr

public class ProcessPool {

    private java.util.Vector idleProcesses;

    private String exePath;

    COM.dragonflow.Utils.CounterLock counterLock;

    static int counter = 0;

    boolean stopProcessPool;

    public ProcessPool(String s) {
        idleProcesses = new Vector();
        exePath = "";
        counterLock = null;
        stopProcessPool = false;
        exePath = s;
        counterLock = new CounterLock(COM.dragonflow.ProcessUtils.ProcessMgr.maxProcessesPerPool);
    }

    public boolean exec(String s, Array array, int ai[], COM.dragonflow.SiteView.AtomicMonitor atomicmonitor) throws java.io.IOException {
        long l = 0L;
        long l1 = 0L;
        if (stopProcessPool) {
            return false;
        }
        boolean flag = false;
        if (atomicmonitor != null) {
            atomicmonitor.currentStatus = "getting a process from the process pool...";
        }
        l = java.lang.System.currentTimeMillis();
        counterLock.get();
        l1 = java.lang.System.currentTimeMillis();
        long l2 = l1 - l;
        if (l2 > 30000L) {
            COM.dragonflow.Log.LogManager.log("Error", "Got to the limit of processes in the process pool, waited: " + l2 + " milliseconds for the next available process. Consider increasing the max number of processes allowed"
                    + " in the process pool (_processPoolMaxPerPool in master.config)");
        }
        if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel >= 3) {
            COM.dragonflow.Log.LogManager.log("Error", "Info: ProcessPool: lock duration: " + l2 + " milliseconds, command: " + s);
        }
        COM.dragonflow.ProcessUtils.SingleProcess singleprocess = getProcess();
        if (singleprocess != null) {
            if (atomicmonitor != null) {
                atomicmonitor.currentStatus = "start executing a task in a process from the process pool...";
            }
            flag = singleprocess.exec(s, array, ai);
            if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel >= 3) {
                long l3 = java.lang.System.currentTimeMillis() - l1;
                COM.dragonflow.Log.LogManager.log("Error", "Info: ProcessPool: execution duration: " + l3 + " milliseconds, command: " + s);
            }
            if (atomicmonitor != null) {
                atomicmonitor.currentStatus = "done executing a task in a process from the process pool...";
            }
            if (stopProcessPool) {
                singleprocess.stop();
            } else if (flag) {
                if (singleprocess.available()) {
                    addProcessToIdle(singleprocess);
                    counterLock.release();
                }
            } else {
                counterLock.release();
            }
        }
        return flag;
    }

    private COM.dragonflow.ProcessUtils.SingleProcess getProcess() {
        COM.dragonflow.ProcessUtils.SingleProcess singleprocess = null;
        synchronized (idleProcesses) {
            int i = idleProcesses.size();
            if (i != 0) {
                singleprocess = (COM.dragonflow.ProcessUtils.SingleProcess) idleProcesses.remove(i - 1);
            }
        }
        if (singleprocess == null) {
            singleprocess = new SingleProcess(exePath, this);
        }
        return singleprocess;
    }

    protected void addProcessToIdle(COM.dragonflow.ProcessUtils.SingleProcess singleprocess) {
        synchronized (idleProcesses) {
            idleProcesses.add(singleprocess);
        }
    }

    public void stopProcesses() {
        stopProcessPool = true;
        for (int i = 0; i < idleProcesses.size(); i ++) {
            ((COM.dragonflow.ProcessUtils.SingleProcess) idleProcesses.get(i)).stop();
        }

    }

}
