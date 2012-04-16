/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.SSH;

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

// Referenced classes of package COM.dragonflow.SSH:
// ISSHSession, SSHPlinkClient, SSHJavaClient, SSHCloser,
// ISSHCloseable, SSHManager

public class SSHRemote implements COM.dragonflow.SSH.ISSHCloseable {

    private COM.dragonflow.Utils.CounterLock lock;

    private boolean caching;

    private java.util.Vector idleSessions;

    private COM.dragonflow.SiteView.Machine machine;

    private int timeout;

    private boolean closing;

    public SSHRemote(COM.dragonflow.SiteView.Machine machine1, int i) {
        lock = null;
        caching = false;
        idleSessions = new Vector();
        closing = false;
        COM.dragonflow.SSH.SSHManager sshmanager = COM.dragonflow.SSH.SSHManager.getInstance();
        int j = 0;
        if (sshmanager.globalNumClientsIsSet()) {
            j = COM.dragonflow.Utils.TextUtils.toInt(sshmanager.getNumClientConnections());
        } else {
            j = machine1.getPropertyAsInteger(COM.dragonflow.SiteView.Machine.pSSHConnectionsLimit);
        }
        if (j < 1) {
            j = 1;
        }
        lock = new CounterLock(j);
        if (sshmanager.globalCacheIsSet()) {
            caching = sshmanager.globalCacheSetting();
        } else {
            caching = machine1.getProperty(COM.dragonflow.SiteView.Machine.pSSHConnectionCaching).length() == 0;
        }
        machine = machine1;
        timeout = i;
        if (COM.dragonflow.SSH.SSHManager.debug) {
            COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: creating new remote: " + machine1.getProperty(COM.dragonflow.SiteView.Machine.pHost) + " timeout=" + i + " limit=" + j + " caching=" + caching);
        }
    }

    private COM.dragonflow.SSH.ISSHSession getSession(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, boolean flag, java.io.PrintWriter printwriter, boolean aflag[]) {
        aflag[0] = false;
        COM.dragonflow.SSH.ISSHSession isshsession = null;
        if (closing) {
            return null;
        }
        long l = java.lang.System.currentTimeMillis();
        if (!flag) {
            lock.get();
        }
        long l1 = java.lang.System.currentTimeMillis() - l;
        if (l1 > 30000L) {
            COM.dragonflow.Log.LogManager.log("Error", "Got to the limit of SSH connections: " + lock.max + " for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ", waited: " + l1
                    + " milliseconds for the next available connection. Consider increasing the max number of SSH connections " + "allowed for this remote.");
        }
        if (!caching || flag) {
            isshsession = createSession(remotecommandline, printwriter);
        } else {
            synchronized (idleSessions) {
                int i = idleSessions.size();
                if (i > 0) {
                    isshsession = (COM.dragonflow.SSH.ISSHSession) idleSessions.remove(i - 1);
                    aflag[0] = true;
                }
            }
            if (isshsession == null) {
                isshsession = createSession(remotecommandline, printwriter);
            }
        }
        if (isshsession == null && !flag) {
            lock.release();
        }
        return isshsession;
    }

    private void release(COM.dragonflow.SSH.ISSHSession isshsession, boolean flag, boolean flag1) {
        if (!caching || closing || flag || flag1) {
            if (COM.dragonflow.SSH.SSHManager.debug) {
                COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: closing session for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ", caching=" + caching + ", forceClose=" + flag + ", closing=" + closing);
            }
            isshsession.close();
        } else {
            idleSessions.add(isshsession);
        }
        if (!flag1) {
            lock.release();
        }
    }

    public int execute(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, boolean flag, java.io.PrintWriter printwriter, jgl.Array array) {
        int i = -1;
        if (closing) {
            return i;
        }
        boolean aflag[] = new boolean[1];
        COM.dragonflow.SSH.ISSHSession isshsession = getSession(remotecommandline, flag, printwriter, aflag);
        if (isshsession != null) {
            i = isshsession.execute(remotecommandline, s, timeout, printwriter, array);
            boolean flag1 = i != 0;
            release(isshsession, flag1, flag);
            if (flag1 && aflag[0] && !closing && i != COM.dragonflow.SiteView.Monitor.kURLTimeoutError) {
                if (COM.dragonflow.SSH.SSHManager.debug) {
                    COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: failed to execute cmd: " + s + " for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ", trying again");
                }
                COM.dragonflow.SSH.ISSHSession isshsession1 = getSession(remotecommandline, flag, printwriter, aflag);
                if (isshsession1 != null) {
                    i = isshsession1.execute(remotecommandline, s, timeout, printwriter, array);
                    release(isshsession1, i != 0, flag);
                }
            }
            if (i != 0) {
                COM.dragonflow.Log.LogManager.log("Error", "SSHRemote Error: failed to execute the command: " + s + " on remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost)
                        + (i != COM.dragonflow.SiteView.Monitor.kURLTimeoutError ? ", status code: " + i : ", timeout"));
            }
        }
        return i;
    }

    private COM.dragonflow.SSH.ISSHSession createSession(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, java.io.PrintWriter printwriter) {
        COM.dragonflow.SSH.SSHManager sshmanager = COM.dragonflow.SSH.SSHManager.getInstance();
        String s = null;
        if (sshmanager.globalClientIsSet()) {
            s = sshmanager.getClientOverride();
        } else {
            s = machine.getProperty(COM.dragonflow.SiteView.Machine.pSSHClient);
        }
        java.lang.Object obj = null;
        if (s == null || s.length() == 0 || s.equals("plink")) {
            obj = new SSHPlinkClient(machine);
        } else if (s.equals("java")) {
            obj = new SSHJavaClient(machine);
        }
        if (obj != null) {
            if (COM.dragonflow.SSH.SSHManager.debug) {
                COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: create session: " + s + " for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost));
            }
            boolean flag = ((COM.dragonflow.SSH.ISSHSession) (obj)).connect(remotecommandline, machine, timeout, printwriter);
            if (!flag) {
                if (COM.dragonflow.SSH.SSHManager.debug) {
                    COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: create session: " + s + " for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + " failed!");
                }
                return null;
            }
        }
        return ((COM.dragonflow.SSH.ISSHSession) (obj));
    }

    /**
     * 
     */
    public void close() {
        java.lang.Thread athread[];
        long l;
        long l1;
        if (COM.dragonflow.SSH.SSHManager.debug) {
            COM.dragonflow.Log.LogManager.log("Error", "SSHManager Info: closing all connections for remote: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost));
        }
        closing = true;
        athread = new java.lang.Thread[idleSessions.size()];
        for (int i = 0; i < idleSessions.size(); i ++) {
            java.lang.Thread thread = new Thread(new SSHCloser((COM.dragonflow.SSH.ISSHCloseable) idleSessions.get(i)));
            thread.start();
            athread[i] = thread;
        }

        l = java.lang.System.currentTimeMillis() + 4000L;
        l1 = 2000L;

        for (int j = 0; j < athread.length; j ++) {
            long l3;
            try {
                long l2 = l - java.lang.System.currentTimeMillis();
                l3 = l1 >= l2 ? l2 : l1;
                if (l3 < 0L) {
                    break;
                } else {
                    athread[j].join(l3);
                }
            } catch (java.lang.InterruptedException interruptedexception) {
                break;
            }
        }
    }
}
