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

import java.net.Socket;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh.SSHConsoleClient;
import com.mindbright.ssh.SSHRSAKeyFile;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2HostKeyVerifier;
import com.mindbright.ssh2.SSH2Preferences;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;

// Referenced classes of package COM.dragonflow.SSH:
// SSHClientBase, SSHConnectionProperties, ISSHSession

public class SSHJavaClient extends COM.dragonflow.SSH.SSHClientBase implements COM.dragonflow.SSH.ISSHSession, com.mindbright.ssh.SSHAuthenticator, com.mindbright.ssh.SSHInteractor, com.mindbright.ssh.SSHClientUser {

    private String windowsShellCommand;

    private boolean isWindowsSSH;

    private COM.dragonflow.SSH.SSHConnectionProperties connectionProperties;

    private com.mindbright.ssh2.SSH2Transport transport;

    private com.mindbright.ssh2.SSH2SimpleClient client;

    private com.mindbright.sshcommon.SSHConsoleRemote console;

    private String host;

    static byte seed[] = (new RandomSeed()).getBytesBlocking(20, false);

    com.mindbright.util.SecureRandomAndPad secureRandom;

    static final boolean $assertionsDisabled; /* synthetic field */

    public SSHJavaClient(COM.dragonflow.SiteView.Machine machine) {
        windowsShellCommand = "cmd.exe";
        isWindowsSSH = false;
        secureRandom = new SecureRandomAndPad(new SecureRandom(seed));
        this.machine = machine;
        sshSessionDebug = machine.getProperty("_debug").equals("true");
        String s = machine.getProperty("_windowsShellCommand");
        if (s.length() > 0) {
            windowsShellCommand = s;
        }
    }

    public void close() {
        try {
            if (commandLineParam != null) {
                COM.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
                commandLineParam.traceMessage("close ssh connection", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
            }
            writeLine("exit");
            java.lang.Thread.currentThread();
            java.lang.Thread.sleep(2000L);
            flushInput(machine);
        } catch (java.lang.Exception exception) {
        }
        if (out != null) {
            try {
                if (commandLineParam != null) {
                    COM.dragonflow.Utils.RemoteCommandLine _tmp1 = commandLineParam;
                    commandLineParam.traceMessage("closing output", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                }
                out.close();
            } catch (java.lang.Exception exception1) {
            }
            out = null;
        }
        if (in != null) {
            try {
                if (commandLineParam != null) {
                    COM.dragonflow.Utils.RemoteCommandLine _tmp2 = commandLineParam;
                    commandLineParam.traceMessage("closing input", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                }
                in.close();
            } catch (java.lang.Exception exception2) {
            }
            in = null;
        }
        if (console != null) {
            console.close();
        }
        if (transport != null) {
            transport.normalDisconnect("Done with connection");
        }
    }

    public boolean connect(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, COM.dragonflow.SiteView.Machine machine, int i, java.io.PrintWriter printwriter) {
        timeoutTime = java.lang.System.currentTimeMillis() + (long) i;
        commandLineParam = remotecommandline;
        int j = -1;
        try {
            j = internalConnect(machine, printwriter);
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "SSHPlinkClient: Failed to connect to machine: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ", exception: " + exception.getMessage());
        }
        if (j == COM.dragonflow.SiteView.Monitor.kURLok) {
            return true;
        } else {
            close();
            return false;
        }
    }

    /**
     * 
     */
    public int execute(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, int i, java.io.PrintWriter printwriter, jgl.Array array) {
        timeoutTime = java.lang.System.currentTimeMillis() + (long) i;
        commandLineParam = remotecommandline;
        try {
            int j = internalExec(s, array, printwriter);
            if (j == COM.dragonflow.SiteView.Monitor.kURLok) {
                return 0;
            }
            return j;
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "SSHPlinkClient: Failed to execute command: " + s + " on machine: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ", exception: " + exception.getMessage());
            return -1;
        }
    }

    public static String checkClient() {
        return "";
    }

    protected int internalConnect(COM.dragonflow.SiteView.Machine machine, java.io.PrintWriter printwriter) throws java.lang.Exception {
        int i = COM.dragonflow.SiteView.Monitor.kURLok;
        boolean flag = machine.getProperty(COM.dragonflow.SiteView.Machine.pSSHForceVersion2).length() <= 0;
        COM.dragonflow.SiteView.Machine _tmp = machine;
        String s = machine.getProperty(COM.dragonflow.SiteView.Machine.pSecondaryPrompt);
        COM.dragonflow.SiteView.Machine _tmp1 = machine;
        String s1 = machine.getProperty(COM.dragonflow.SiteView.Machine.pSecondaryResponse);
        COM.dragonflow.SiteView.Machine _tmp2 = machine;
        String s2 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPrompt);
        COM.dragonflow.SiteView.Machine _tmp3 = machine;
        passwordPrompt = machine.getProperty(COM.dragonflow.SiteView.Machine.pPasswordPrompt);
        isWindowsSSH = machine.getProperty(COM.dragonflow.SiteView.Machine.pOS).equals("NT");
        connectionProperties = new SSHConnectionProperties(machine);
        boolean flag1 = false;
        if (flag) {
            flag1 = doVersionOneConnect(connectionProperties, false);
        }
        if (!flag1 && !doVersionTwoConnect(connectionProperties, true)) {
            return COM.dragonflow.SiteView.Monitor.kURLNoConnectionError;
        }
        if (isWindowsSSH) {
            writeNewline = "\r\n";
            return COM.dragonflow.SiteView.Monitor.kURLok;
        }
        writeNewline = "\n";
        if (i == COM.dragonflow.SiteView.Monitor.kURLok && s1.length() > 0) {
            i = processSecondaryResponses(in, s2, s, s1);
        }
        if (i == COM.dragonflow.SiteView.Monitor.kURLok) {
            initShellEnvironment(machine);
        }
        if (sshSessionDebug) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + this.machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + "Done with internal connect, status = " + i);
        }
        return i;
    }

    boolean doVersionOneConnect(COM.dragonflow.SSH.SSHConnectionProperties sshconnectionproperties, boolean flag) {
        boolean flag1 = false;
        console = null;
        try {
            console = new SSHConsoleClient(getSrvHost(), getSrvPort(), this, this);
            if (isWindowsSSH && console.command(windowsShellCommand) || console.shell()) {
                out = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(console.getStdIn());
                in = COM.dragonflow.Utils.FileUtils.MakeInputReader(console.getStdOut());
                if (out != null && in != null) {
                    flag1 = true;
                } else if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            } else if (flag) {
                throw new Exception("Couldn't start terminal!");
            }
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "SSH: An Connection error occured: " + exception.getMessage() + "connecting to host " + host);
            if (console != null) {
                console.close();
            }
            flag1 = false;
        }
        return flag1;
    }

    /**
     * 
     * 
     * @param sshconnectionproperties
     * @param flag
     * @return
     */
    boolean doVersionTwoConnect(COM.dragonflow.SSH.SSHConnectionProperties sshconnectionproperties, boolean flag) {
        boolean flag1 = false;
        try {
            int i = sshconnectionproperties.getPort();
            String s = sshconnectionproperties.getUsername();
            host = sshconnectionproperties.getTarget();
            com.mindbright.ssh2.SSH2Preferences ssh2preferences = new SSH2Preferences(sshconnectionproperties);
            transport = new SSH2Transport(new Socket(host, i), ssh2preferences, secureRandom);
            String s1 = sshconnectionproperties.getProperty("fingerprint." + host + "." + i);
            if (s1 != null) {
                transport.setEventHandler(new SSH2HostKeyVerifier(s1));
            }
            client = null;
            if (!sshconnectionproperties.isPasswordAuthentication()) {
                String s2 = sshconnectionproperties.getKeyFile();
                String s4 = sshconnectionproperties.getPassword();
                client = new SSH2SimpleClient(transport, s, s2, s4);
            } else {
                String s3 = sshconnectionproperties.getPassword();
                client = new SSH2SimpleClient(transport, s, s3);
            }
            com.mindbright.ssh2.SSH2ConsoleRemote ssh2consoleremote = new SSH2ConsoleRemote(client.getConnection());
            if (isWindowsSSH && ssh2consoleremote.command(windowsShellCommand) || ssh2consoleremote.shell(true)) {
                console = ssh2consoleremote;
                out = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(console.getStdIn());
                in = COM.dragonflow.Utils.FileUtils.MakeInputReader(console.getStdOut());
                if (in != null && out != null) {
                    flag1 = true;
                } else if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            } else if (flag) {
                throw new Exception("Couldn't start terminal!");
            }
        } catch (java.lang.Exception exception) {
            if (flag) {
                java.lang.System.out.println("An error occured: " + exception.getMessage());
                COM.dragonflow.Log.LogManager.log("Error", "SSH: An Connection error occured: " + exception.getMessage() + "connecting to host " + host);
            }
        }

        return flag1;
    }

    /**
     * 
     */
    protected int fillBuffer(java.io.BufferedReader bufferedreader, StringBuffer stringbuffer, java.io.PrintWriter printwriter) throws java.io.IOException {
        int i;
        char ac[];
        ac = new char[100];
        boolean flag = false;
        i = COM.dragonflow.SiteView.Monitor.kURLok;
        if (sshSessionDebug) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Getting ready to read ");
        }
        while (bufferedreader != null) {
            if (bufferedreader.ready()) {
                int j = bufferedreader.read(ac);
                if (j <= 0) {
                    continue;
                }
                String s = new String(ac);
                if (sshSessionDebug) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Read #" + j + "chars\n Created Buffer (" + s + ")");
                }
                commandLineParam.traceMessage(s, machine, COM.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                if (printwriter != null) {
                    printwriter.print(s);
                }
                stringbuffer.append(s);
                if (COM.dragonflow.SiteView.Platform.isWindows()) {
                    if (match(stringbuffer, "Bad host name") || match(stringbuffer, "gethostbyname") || match(stringbuffer, "Host does not exist")) {
                        i = COM.dragonflow.SiteView.Monitor.kURLBadHostNameError;
                    } else if (match(stringbuffer, "connect()")) {
                        i = COM.dragonflow.SiteView.Monitor.kURLNoRouteToHostError;
                    } else if (match(stringbuffer, "denied")) {
                        i = 401;
                    } else if (match(stringbuffer, "refused") || match(stringbuffer, "Refused")) {
                        i = COM.dragonflow.SiteView.Monitor.kURLNoConnectionError;
                    }
                }
                break;
            }
            if (sshSessionDebug) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ": Readline: waiting to read characters");
            }
            long l = getSleepingTime(COM.dragonflow.SSH.SSHClientBase.SSH_SESSION_SLEEP);
            if (l == -1L) {
                return COM.dragonflow.SiteView.Monitor.kURLTimeoutError;
            }
            try {
                java.lang.Thread.currentThread();
                java.lang.Thread.sleep(l);
            } catch (java.lang.InterruptedException interruptedexception) {
                COM.dragonflow.Log.LogManager.log("Error", "SSHSession for " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + " interrupted during sleep");
            }
        }
        return i;
    }

    public String getUsername(com.mindbright.ssh.SSHClientUser sshclientuser) throws java.io.IOException {
        return connectionProperties.getUsername();
    }

    public String getPassword(com.mindbright.ssh.SSHClientUser sshclientuser) throws java.io.IOException {
        return connectionProperties.getPassword();
    }

    public String getChallengeResponse(com.mindbright.ssh.SSHClientUser sshclientuser, String s) throws java.io.IOException {
        return null;
    }

    public int[] getAuthTypes(com.mindbright.ssh.SSHClientUser sshclientuser) {
        return com.mindbright.ssh.SSH.getAuthTypes(connectionProperties.getAuthenticationMethod());
    }

    public int getCipher(com.mindbright.ssh.SSHClientUser sshclientuser) {
        return com.mindbright.ssh.SSH.getCipherType(connectionProperties.getCipher());
    }

    public com.mindbright.ssh.SSHRSAKeyFile getIdentityFile(com.mindbright.ssh.SSHClientUser sshclientuser) throws java.io.IOException {
        String s = connectionProperties.getKeyFile();
        com.mindbright.ssh.SSHRSAKeyFile sshrsakeyfile = new SSHRSAKeyFile(s);
        return sshrsakeyfile;
    }

    public String getIdentityPassword(com.mindbright.ssh.SSHClientUser sshclientuser) throws java.io.IOException {
        return connectionProperties.getPassword();
    }

    public boolean verifyKnownHosts(com.mindbright.jca.security.interfaces.RSAPublicKey rsapublickey) throws java.io.IOException {
        return true;
    }

    public String getSrvHost() throws java.io.IOException {
        return connectionProperties.getTarget();
    }

    public int getSrvPort() {
        return connectionProperties.getPort();
    }

    public java.net.Socket getProxyConnection() throws java.io.IOException {
        return null;
    }

    public String getDisplay() {
        return null;
    }

    public int getMaxPacketSz() {
        return 0;
    }

    public int getAliveInterval() {
        return 0;
    }

    public int getCompressionLevel() {
        return 0;
    }

    public boolean wantX11Forward() {
        return false;
    }

    public boolean wantPTY() {
        return true;
    }

    public com.mindbright.ssh.SSHInteractor getInteractor() {
        return this;
    }

    public void startNewSession(com.mindbright.ssh.SSHClient sshclient) {
    }

    public void sessionStarted(com.mindbright.ssh.SSHClient sshclient) {
    }

    public void connected(com.mindbright.ssh.SSHClient sshclient) {
    }

    public void open(com.mindbright.ssh.SSHClient sshclient) {
    }

    public void disconnected(com.mindbright.ssh.SSHClient sshclient, boolean flag) {
    }

    public void report(String s) {
    }

    public void alert(String s) {
    }

    public void propsStateChanged(com.mindbright.ssh.SSHPropertyHandler sshpropertyhandler) {
    }

    public boolean askConfirmation(String s, boolean flag) {
        return flag;
    }

    public boolean licenseDialog(String s) {
        return false;
    }

    public boolean quietPrompts() {
        return true;
    }

    public String promptLine(String s, String s1) throws java.io.IOException {
        return null;
    }

    public String promptPassword(String s) throws java.io.IOException {
        return null;
    }

    public boolean isVerbose() {
        return false;
    }

    static {
        $assertionsDisabled = !(COM.dragonflow.SSH.SSHJavaClient.class).desiredAssertionStatus();
    }
}
