/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.SSH;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Vector;

import com.dragonflow.Utils.CounterLock;

import com.recursionsw.jgl.Array;

// Referenced classes of package com.dragonflow.SSH:
// ISSHSession, SSHPlinkClient, SSHJavaClient, SSHCloser,
// ISSHCloseable, SSHManager

public class SSHRemote implements com.dragonflow.SSH.ISSHCloseable {

    private com.dragonflow.Utils.CounterLock lock;

    private boolean caching;

    private java.util.Vector idleSessions;

    private com.dragonflow.SiteView.Machine machine;

    private int timeout;

    private boolean closing;

    public SSHRemote(com.dragonflow.SiteView.Machine machine1, int i) {
        lock = null;
        caching = false;
        idleSessions = new Vector();
        closing = false;
        com.dragonflow.SSH.SSHManager sshmanager = com.dragonflow.SSH.SSHManager.getInstance();
        int j = 0;
        if (sshmanager.globalNumClientsIsSet()) {
            j = com.dragonflow.Utils.TextUtils.toInt(sshmanager.getNumClientConnections());
        } else {
            j = machine1.getPropertyAsInteger(com.dragonflow.SiteView.Machine.pSSHConnectionsLimit);
        }
        if (j < 1) {
            j = 1;
        }
        lock = new CounterLock(j);
        if (sshmanager.globalCacheIsSet()) {
            caching = sshmanager.globalCacheSetting();
        } else {
            caching = machine1.getProperty(com.dragonflow.SiteView.Machine.pSSHConnectionCaching).length() == 0;
        }
        machine = machine1;
        timeout = i;
        if (com.dragonflow.SSH.SSHManager.debug) {
            com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: creating new remote: " + machine1.getProperty(com.dragonflow.SiteView.Machine.pHost) + " timeout=" + i + " limit=" + j + " caching=" + caching);
        }
    }

    private com.dragonflow.SSH.ISSHSession getSession(com.dragonflow.Utils.RemoteCommandLine remotecommandline, boolean flag, java.io.PrintWriter printwriter, boolean aflag[]) {
        aflag[0] = false;
        com.dragonflow.SSH.ISSHSession isshsession = null;
        if (closing) {
            return null;
        }
        long l = java.lang.System.currentTimeMillis();
        if (!flag) {
            lock.get();
        }
        long l1 = java.lang.System.currentTimeMillis() - l;
        if (l1 > 30000L) {
            com.dragonflow.Log.LogManager.log("Error", "Got to the limit of SSH connections: " + lock.max + " for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ", waited: " + l1
                    + " milliseconds for the next available connection. Consider increasing the max number of SSH connections " + "allowed for this remote.");
        }
        if (!caching || flag) {
            isshsession = createSession(remotecommandline, printwriter);
        } else {
            synchronized (idleSessions) {
                int i = idleSessions.size();
                if (i > 0) {
                    isshsession = (com.dragonflow.SSH.ISSHSession) idleSessions.remove(i - 1);
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

    private void release(com.dragonflow.SSH.ISSHSession isshsession, boolean flag, boolean flag1) {
        if (!caching || closing || flag || flag1) {
            if (com.dragonflow.SSH.SSHManager.debug) {
                com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: closing session for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ", caching=" + caching + ", forceClose=" + flag + ", closing=" + closing);
            }
            isshsession.close();
        } else {
            idleSessions.add(isshsession);
        }
        if (!flag1) {
            lock.release();
        }
    }

    public int execute(com.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, boolean flag, java.io.PrintWriter printwriter, Array array) {
        int i = -1;
        if (closing) {
            return i;
        }
        boolean aflag[] = new boolean[1];
        com.dragonflow.SSH.ISSHSession isshsession = getSession(remotecommandline, flag, printwriter, aflag);
        if (isshsession != null) {
            i = isshsession.execute(remotecommandline, s, timeout, printwriter, array);
            boolean flag1 = i != 0;
            release(isshsession, flag1, flag);
            if (flag1 && aflag[0] && !closing && i != com.dragonflow.SiteView.Monitor.kURLTimeoutError) {
                if (com.dragonflow.SSH.SSHManager.debug) {
                    com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: failed to execute cmd: " + s + " for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ", trying again");
                }
                com.dragonflow.SSH.ISSHSession isshsession1 = getSession(remotecommandline, flag, printwriter, aflag);
                if (isshsession1 != null) {
                    i = isshsession1.execute(remotecommandline, s, timeout, printwriter, array);
                    release(isshsession1, i != 0, flag);
                }
            }
            if (i != 0) {
                com.dragonflow.Log.LogManager.log("Error", "SSHRemote Error: failed to execute the command: " + s + " on remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost)
                        + (i != com.dragonflow.SiteView.Monitor.kURLTimeoutError ? ", status code: " + i : ", timeout"));
            }
        }
        return i;
    }

    private com.dragonflow.SSH.ISSHSession createSession(com.dragonflow.Utils.RemoteCommandLine remotecommandline, java.io.PrintWriter printwriter) {
        com.dragonflow.SSH.SSHManager sshmanager = com.dragonflow.SSH.SSHManager.getInstance();
        String s = null;
        if (sshmanager.globalClientIsSet()) {
            s = sshmanager.getClientOverride();
        } else {
            s = machine.getProperty(com.dragonflow.SiteView.Machine.pSSHClient);
        }
        java.lang.Object obj = null;
        if (s == null || s.length() == 0 || s.equals("plink")) {
            obj = new SSHPlinkClient(machine);
        } else if (s.equals("java")) {
            obj = new SSHJavaClient(machine);
        }
        if (obj != null) {
            if (com.dragonflow.SSH.SSHManager.debug) {
                com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: create session: " + s + " for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost));
            }
            boolean flag = ((com.dragonflow.SSH.ISSHSession) (obj)).connect(remotecommandline, machine, timeout, printwriter);
            if (!flag) {
                if (com.dragonflow.SSH.SSHManager.debug) {
                    com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: create session: " + s + " for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + " failed!");
                }
                return null;
            }
        }
        return ((com.dragonflow.SSH.ISSHSession) (obj));
    }

    /**
     * 
     */
    public void close() {
        java.lang.Thread athread[];
        long l;
        long l1;
        if (com.dragonflow.SSH.SSHManager.debug) {
            com.dragonflow.Log.LogManager.log("Error", "SSHManager Info: closing all connections for remote: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost));
        }
        closing = true;
        athread = new java.lang.Thread[idleSessions.size()];
        for (int i = 0; i < idleSessions.size(); i ++) {
            java.lang.Thread thread = new Thread(new SSHCloser((com.dragonflow.SSH.ISSHCloseable) idleSessions.get(i)));
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
