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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

// Referenced classes of package COM.dragonflow.SSH:
// SSHClientBase, ISSHSession

public class SSHPlinkClient extends COM.dragonflow.SSH.SSHClientBase implements COM.dragonflow.SSH.ISSHSession {

    private java.lang.Process process;

    private static boolean noPortCheck = false;

    public SSHPlinkClient(COM.dragonflow.SiteView.Machine machine) {
        process = null;
        this.machine = machine;
        sshSessionDebug = machine.getProperty("_debug").equals("true");
    }

    public boolean connect(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, COM.dragonflow.SiteView.Machine machine, int i, java.io.PrintWriter printwriter) {
        timeoutTime = java.lang.System.currentTimeMillis() + (long) i;
        commandLineParam = remotecommandline;
        int j = -1;
        String s = portOk(machine);
        if (s.length() > 0) {
            COM.dragonflow.Log.LogManager.log("Error", s);
            return false;
        }
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

    public void close() {
        internalClose();
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
        String s;
        if (COM.dragonflow.SiteView.Platform.isWindows()) {
            s = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "tools" + java.io.File.separator + "plink.exe";
            if ((new File(s)).exists()) {
                s = "";
            }
        } else {
            s = "/usr/local/bin/ssh";
            String s1 = "/usr/bin/ssh";
            if ((new File(s)).exists() || (new File(s1)).exists()) {
                s = "";
            }
        }
        return s;
    }

    private String getDefaultCommandLine(String s) {
        String s1 = "/usr/local/bin/ssh";
        if (COM.dragonflow.SiteView.Platform.isWindows()) {
            return COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "tools" + java.io.File.separator + "plink.exe -ssh " + getKeyFileOption(s) + getPortOption() + "$user$@$host$ -pw \"$password$\"";
        }
        if (!(new File(s1)).exists()) {
            s1 = "/usr/bin/ssh";
        }
        return "$root$/tools/sshstart " + s1 + " ssh " + getKeyFileOption(s) + getPortOption() + "-l " + "$user$ -o FallBackToRsh=no $host$";
    }

    private String getKeyFileOption(String s) {
        String s1 = "";
        if (s != null && s.equals("keyfile")) {
            s1 = "-i " + machine.getProperty(COM.dragonflow.SiteView.Machine.pSshKeyFile);
        }
        return s1 + " ";
    }

    private String getPortOption() {
        String s = "";
        int i = machine.getPropertyAsInteger(COM.dragonflow.SiteView.Machine.pSSHPort);
        if (i != 22 && machine.getProperty(COM.dragonflow.SiteView.Machine.pHost).indexOf(" -P") == -1) {
            s = (COM.dragonflow.SiteView.Platform.isWindows() ? " -P " : " -p ") + i + " ";
        }
        return s;
    }

    private int internalConnect(COM.dragonflow.SiteView.Machine machine, java.io.PrintWriter printwriter) throws java.lang.Exception {
        int i = COM.dragonflow.SiteView.Monitor.kURLok;
        String s = machine.getProperty("_sshCommand");
        COM.dragonflow.SiteView.Machine _tmp = machine;
        String s1 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPassword);
        String s2 = machine.getProperty(COM.dragonflow.SiteView.Machine.pHost);
        COM.dragonflow.SiteView.Machine _tmp1 = machine;
        String s3 = machine.getProperty(COM.dragonflow.SiteView.Machine.pSshAuthMethod);
        COM.dragonflow.SiteView.Machine _tmp2 = machine;
        String s4 = machine.getProperty(COM.dragonflow.SiteView.Machine.pSecondaryPrompt);
        COM.dragonflow.SiteView.Machine _tmp3 = machine;
        String s5 = machine.getProperty(COM.dragonflow.SiteView.Machine.pSecondaryResponse);
        COM.dragonflow.SiteView.Machine _tmp4 = machine;
        String s6 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPrompt);
        COM.dragonflow.SiteView.Machine _tmp5 = machine;
        passwordPrompt = machine.getProperty(COM.dragonflow.SiteView.Machine.pPasswordPrompt);
        String s7 = machine.getProperty("_sshNewline");
        if (s7.length() != 0) {
            writeNewline = "" + (char) COM.dragonflow.Utils.TextUtils.toInt(s7);
        } else {
            writeNewline = java.lang.System.getProperty("line.separator");
        }
        if (writeNewline == null || writeNewline.length() <= 0) {
            writeNewline = "\n";
        }
        if (s.length() == 0) {
            s = getDefaultCommandLine(s3);
        }
        s = COM.dragonflow.Utils.TextUtils.replaceVariable(s, "$root$", COM.dragonflow.SiteView.Platform.getRoot());
        s = COM.dragonflow.Utils.TextUtils.replaceVariable(s, "$host$", s2);
        COM.dragonflow.SiteView.Machine _tmp6 = machine;
        String s8 = machine.getProperty(COM.dragonflow.SiteView.Machine.pLogin);
        if (s8.length() == 0) {
            s = COM.dragonflow.Utils.TextUtils.replaceString(s, "-l $user$", "");
        } else {
            s = COM.dragonflow.Utils.TextUtils.replaceVariable(s, "$user$", s8);
        }
        String s9 = s;
        if (s.indexOf("$password$") != -1) {
            s = COM.dragonflow.Utils.TextUtils.replaceVariable(s, "$password$", s1);
            s1 = "";
        }
        commandLineParam.progressMessage("Connecting to " + s2 + "...");
        COM.dragonflow.Utils.RemoteCommandLine _tmp7 = commandLineParam;
        commandLineParam.traceMessage(s9, machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
        process = COM.dragonflow.Utils.CommandLine.execSync(s);
        out = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(process.getOutputStream());
        rawInput = process.getInputStream();
        in = COM.dragonflow.Utils.FileUtils.MakeInputReader(rawInput);
        errInput = process.getErrorStream();
        StringBuffer stringbuffer = new StringBuffer();
        if (machine.getProperty(COM.dragonflow.SiteView.Machine.pOS).equals("NT") && machine.getProperty(COM.dragonflow.SiteView.Machine.pMethod).equals("ssh") && COM.dragonflow.SiteView.Platform.isWindows()) {
            return COM.dragonflow.SiteView.Monitor.kURLok;
        }
        if (s1.length() > 0) {
            commandLineParam.progressMessage("Waiting for password prompt...");
            if (machine.getProperty(COM.dragonflow.SiteView.Machine.pOS).equals("NT") && machine.getProperty(COM.dragonflow.SiteView.Machine.pMethod).equals("ssh")) {
                passwordPrompt = "assword:";
            }
            do {
                i = fillBuffer(in, stringbuffer, commandLineParam.progressStream);
                if (i != COM.dragonflow.SiteView.Monitor.kURLok) {
                    break;
                }
                if (match(stringbuffer, passwordPrompt)) {
                    commandLineParam.progressMessage("Sending password");
                    COM.dragonflow.Utils.RemoteCommandLine _tmp8 = commandLineParam;
                    commandLineParam.traceMessage("(password)", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                    writeLine(s1);
                    String s10 = readLine();
                    if (s10 == null) {
                        if (timedout) {
                            i = COM.dragonflow.SiteView.Monitor.kURLTimeoutError;
                            COM.dragonflow.Utils.RemoteCommandLine _tmp9 = commandLineParam;
                            commandLineParam.traceMessage("Connection Dropped, timeout", machine, COM.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                        } else {
                            COM.dragonflow.Utils.RemoteCommandLine _tmp10 = commandLineParam;
                            commandLineParam.traceMessage("Connection Dropped", machine, COM.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                            i = COM.dragonflow.SiteView.Monitor.kURLNoConnectionError;
                        }
                    } else {
                        COM.dragonflow.Utils.RemoteCommandLine _tmp11 = commandLineParam;
                        commandLineParam.traceMessage(s10, machine, COM.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                        if (printwriter != null) {
                            printwriter.println(s10);
                        }
                        if (s10.indexOf("denied") != -1) {
                            i = 401;
                        } else {
                            i = doLoginSequence(machine, s4, s5);
                        }
                    }
                    break;
                }
                if (match(stringbuffer, "(yes/no)") || match(stringbuffer, "(y/n)") || match(stringbuffer, "yes")) {
                    commandLineParam.progressMessage("Confirming Add To Known Hosts");
                    COM.dragonflow.Utils.RemoteCommandLine _tmp12 = commandLineParam;
                    commandLineParam.traceMessage("yes", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                    writeLine("yes");
                    stringbuffer = new StringBuffer();
                }
            } while (true);
        } else {
            flushstderr(true);
            i = doLoginSequence(machine, s4, s5);
        }
        if (i == COM.dragonflow.SiteView.Monitor.kURLok) {
            initShellEnvironment(machine);
        }
        if (sshSessionDebug) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + this.machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + "Done with internal connect");
        }
        return i;
    }

    private void flushstderr() {
        flushstderr(false);
    }

    private void flushstderr(boolean flag) {
        java.io.BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(errInput));
        String s = "";
        try {
            if (flag && !bufferedreader.ready()) {
                java.lang.Thread.currentThread();
                java.lang.Thread.sleep(2000L);
            }
            do {
                if (!bufferedreader.ready()) {
                    break;
                }
                String s1 = bufferedreader.readLine();
                s1.trim();
                if (s1.length() > 0) {
                    COM.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
                    commandLineParam.traceMessage(s1, machine, COM.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                }
            } while (true);
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
    }

    private void internalClose() {
        try {
            if (commandLineParam != null) {
                COM.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
                commandLineParam.traceMessage("close ssh connection", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
            }
            writeLine("exit\n");
            java.lang.Thread.currentThread();
            java.lang.Thread.sleep(2000L);
            flushInput(machine);
            flushstderr();
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
        if (COM.dragonflow.SiteView.Platform.isWindows()) {
            if (process != null) {
                try {
                    if (commandLineParam != null) {
                        COM.dragonflow.Utils.RemoteCommandLine _tmp3 = commandLineParam;
                        commandLineParam.traceMessage("closing ssh process", machine, COM.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                    }
                    process.destroy();
                } catch (java.lang.Exception exception3) {
                }
                process = null;
            }
        } else {
            process = null;
        }
    }

    private String portOk(COM.dragonflow.SiteView.Machine machine) {
        if (noPortCheck || machine.getProperty("_sshCommand").length() != 0) {
            return "";
        }
        String s = machine.getProperty(COM.dragonflow.SiteView.Machine.pHost);
        String s1 = "";
        int i = machine.getPropertyAsInteger(COM.dragonflow.SiteView.Machine.pSSHPort);
        java.net.Socket socket = null;
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, " ");
        int j = 0;
        do {
            if (j >= as.length) {
                break;
            }
            if (as[j].toLowerCase().equals("-p")) {
                s = as[0];
                i = java.lang.Integer.parseInt(as[j + 1]);
                break;
            }
            j ++;
        } while (true);
        try {
            java.net.InetAddress inetaddress = java.net.InetAddress.getByName(s);
            String s2 = inetaddress.getHostAddress();
            java.net.InetSocketAddress inetsocketaddress = new InetSocketAddress(s2, i);
            socket = new Socket();
            int k = (int) (timeoutTime - java.lang.System.currentTimeMillis());
            socket.connect(inetsocketaddress, k);
        } catch (java.net.UnknownHostException unknownhostexception) {
            s1 = "SSHPlinkClient Error: SSH Connection down, UnknownHostException from port(" + i + ") on machine: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pName);
        } catch (java.net.SocketTimeoutException sockettimeoutexception) {
            s1 = "SSHPlinkClient Error: SSH Connection down, SocketTimeoutException from port(" + i + ") on machine: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pName);
        } catch (java.io.IOException ioexception) {
            s1 = "SSHPlinkClient Error: SSH Connection down, IOException(" + ioexception.getMessage() + ") from port(" + i + ") on machine: " + machine.getProperty(COM.dragonflow.SiteView.Machine.pName);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (java.io.IOException ioexception1) {
                }
                socket = null;
            }
        }
        return s1;
    }

    protected int fillBuffer(java.io.BufferedReader bufferedreader, StringBuffer stringbuffer, java.io.PrintWriter printwriter) throws java.io.IOException {
        char ac[] = new char[100];
        flushstderr();
        if (sshSessionDebug) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Getting ready to read ");
        }
        int j = bufferedreader.read(ac);
        int i;
        String s;
        if (j > 0) {
            i = COM.dragonflow.SiteView.Monitor.kURLok;
            s = new String(ac);
            if (sshSessionDebug) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(COM.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Read #" + j + "chars\n Created Buffer (" + s + ")");
            }
        } else {
            i = COM.dragonflow.SiteView.Monitor.kURLNoConnectionError;
            s = "Connection dropped by remote host";
            COM.dragonflow.Log.LogManager.log("Error", "ssh(" + machine.getProperty("_host") + ") " + s);
        }
        COM.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
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
        return i;
    }

    static {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if (siteviewgroup.getProperty("_SSHNoPortCheck").length() != 0) {
            noPortCheck = true;
        }
    }
}
