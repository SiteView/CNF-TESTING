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
import SiteViewMain.ServicePrinter;

// Referenced classes of package COM.dragonflow.ProcessUtils:
// WaitForInput, WaitForInputEvents, ProcessMgr, ProcessPool

public class SingleProcess implements COM.dragonflow.ProcessUtils.WaitForInputEvents {

    static final String EXIT_CODE_PREFEIX = "EXIT_CODE:";

    static final int TIMEOUT_EXIT_CODE = 999;

    static final String PROCESS_POOL_END_TASK = "end task in process pool";

    static final String PROCESS_POOL_TIMEOUT = "process timeout in process pool";

    private static int counter = 0;

    private java.lang.Process proc;

    private java.io.OutputStream out;

    private java.io.InputStream error;

    private java.io.InputStream in;

    private java.io.BufferedReader dis;

    private long exitTime;

    private String executablePath;

    private boolean available;

    private COM.dragonflow.ProcessUtils.ProcessPool pool;

    private long startWaiting;

    private String currCmd;

    private int processID;

    public SingleProcess(String s, COM.dragonflow.ProcessUtils.ProcessPool processpool) {
        proc = null;
        out = null;
        error = null;
        in = null;
        dis = null;
        exitTime = COM.dragonflow.ProcessUtils.ProcessMgr.processTimeout <= 0 ? -1L : java.lang.System.currentTimeMillis() + (long) (COM.dragonflow.ProcessUtils.ProcessMgr.processTimeout * 1000);
        executablePath = "";
        available = true;
        pool = null;
        startWaiting = 0L;
        currCmd = "";
        try {
            processID = counter ++;
            pool = processpool;
            executablePath = s;
            proc = COM.dragonflow.Utils.CommandLine.execSync(s + " -pool " + COM.dragonflow.ProcessUtils.ProcessMgr.processKillTimeout);
            out = proc.getOutputStream();
            error = proc.getErrorStream();
            in = proc.getInputStream();
            dis = COM.dragonflow.Utils.FileUtils.MakeInputReader(in);
            COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = SiteViewMain.ServicePrinter.servicePrinterThreadPool.getThread();
            SiteViewMain.ServicePrinter serviceprinter = new ServicePrinter();
            serviceprinter.stream = COM.dragonflow.Utils.FileUtils.MakeInputReader(error);
            singlethread.activate(serviceprinter);
        } catch (java.io.IOException ioexception) {
            onFailure("ProcessPool: Failed to initialize process, exception: " + ioexception.getMessage());
        }
    }

    public boolean exec(String s, jgl.Array array, int ai[]) {
        available = false;
        currCmd = s;
        if (exitTime > 0L && java.lang.System.currentTimeMillis() > exitTime) {
            try {
                out.write((executablePath + " -exitPool").getBytes());
                out.write(0);
                out.flush();
            } catch (java.lang.Exception exception) {
                onFailure("ProcessPool: Failed to write to stdout: -exitPool, exception: " + exception.getMessage());
                return false;
            }
            return false;
        }
        boolean flag = false;
        try {
            proc.exitValue();
        } catch (java.lang.IllegalThreadStateException illegalthreadstateexception) {
            flag = true;
        }
        if (!flag) {
            onFailure("ProcessPool: Failed to execute: " + s + ", Process " + processID + " is not alive.");
            return false;
        }
        try {
            out.write(s.getBytes());
            out.write(0);
            out.flush();
        } catch (java.io.IOException ioexception) {
            onFailure("ProcessPool: Failed to write to stdout: " + s + ", Process=" + processID + ", exception: " + ioexception.getMessage());
            return false;
        }
        do {
            String s1;
            try {
                s1 = dis.readLine();
            } catch (java.io.IOException ioexception1) {
                onFailure("ProcessPool: Failed to read line: " + s + ", Process=" + processID + ", exception: " + ioexception1.getMessage());
                return false;
            }
            if (s1 == null) {
                onFailure("ProcessPool: Failed to read output, got to the end of stream: " + s + ", Process=" + processID);
                return false;
            }
            if (s1.equals("end task in process pool")) {
                try {
                    s1 = dis.readLine();
                } catch (java.io.IOException ioexception2) {
                    onFailure("ProcessPool: Failed to read exit code: " + s + ", Process=" + processID + ", exception: " + ioexception2.getMessage());
                    return false;
                }
                int i = -1;
                if (s1 != null && s1.startsWith("EXIT_CODE:")) {
                    i = COM.dragonflow.Utils.TextUtils.toInt(s1.substring("EXIT_CODE:".length()));
                }
                if (ai != null) {
                    ai[0] = i;
                    if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel == 4) {
                        COM.dragonflow.Log.LogManager.log("Error", "exit code for cmd: " + s + " = " + i);
                    }
                }
                if (i == 999) {
                    startTimeoutThread();
                } else {
                    available = true;
                }
                break;
            }
            if (array != null) {
                array.add(s1);
                if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel == 4) {
                    COM.dragonflow.Log.LogManager.log("Error", s1);
                }
            }
        } while (true);
        return true;
    }

    private void onFailure(String s) {
        s = s.replaceAll("-p\\s+\\S+", "-p ####");
        COM.dragonflow.Log.LogManager.log("Error", s);
        onExit();
        if (proc != null) {
            proc.destroy();
        }
        available = false;
    }

    public void stop() {
        exec(executablePath + " -exitPool", null, null);
        onExit();
    }

    public boolean available() {
        return available;
    }

    private void startTimeoutThread() {
        COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = SiteViewMain.ServicePrinter.servicePrinterThreadPool.getThread();
        startWaiting = java.lang.System.currentTimeMillis();
        if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel >= 2) {
            COM.dragonflow.Log.LogManager.log("Error", "Info: ProcessPool: starting timeout thread for command: " + currCmd + ", Process=" + processID);
        }
        COM.dragonflow.ProcessUtils.WaitForInput waitforinput = new WaitForInput(in, "end task in process pool", "process timeout in process pool", this);
        singlethread.activate(waitforinput);
    }

    public void onInputReceived(int i) {
        long l = java.lang.System.currentTimeMillis() - startWaiting;
        if (i == 0) {
            available = true;
            pool.addProcessToIdle(this);
            if (COM.dragonflow.ProcessUtils.ProcessMgr.logLevel >= 2) {
                COM.dragonflow.Log.LogManager.log("Error", "Info: ProcessPool: got out of timeout, back to pool. waited: " + l + " millisec for command: " + currCmd + ", Process=" + processID);
            }
        } else {
            onFailure("ProcessPool: reached extended timeout, waited: " + l + "milliseconds, Process=" + processID);
        }
        pool.counterLock.release();
        onExit();
    }

    private void onExit() {
        try {
            if (out != null) {
                out.close();
            }
            if (dis != null) {
                dis.close();
            }
            if (error != null) {
                error.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (java.io.IOException ioexception) {
            COM.dragonflow.Log.LogManager.log("Error", "ProcessPool: Failed to close streams, exception: " + ioexception.getMessage() + ", Process=" + processID);
        }
    }

}
