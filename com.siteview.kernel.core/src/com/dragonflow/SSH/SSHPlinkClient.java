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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.recursionsw.jgl.Array;

// Referenced classes of package com.dragonflow.SSH:
// SSHClientBase, ISSHSession

public class SSHPlinkClient extends com.dragonflow.SSH.SSHClientBase implements com.dragonflow.SSH.ISSHSession {

    private java.lang.Process process;

    private static boolean noPortCheck = false;

    public SSHPlinkClient(com.dragonflow.SiteView.Machine machine) {
        process = null;
        this.machine = machine;
        sshSessionDebug = machine.getProperty("_debug").equals("true");
    }

    public boolean connect(com.dragonflow.Utils.RemoteCommandLine remotecommandline, com.dragonflow.SiteView.Machine machine, int i, java.io.PrintWriter printwriter) {
        timeoutTime = java.lang.System.currentTimeMillis() + (long) i;
        commandLineParam = remotecommandline;
        int j = -1;
        String s = portOk(machine);
        if (s.length() > 0) {
            com.dragonflow.Log.LogManager.log("Error", s);
            return false;
        }
        try {
            j = internalConnect(machine, printwriter);
        } catch (java.lang.Exception exception) {
            com.dragonflow.Log.LogManager.log("Error", "SSHPlinkClient: Failed to connect to machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ", exception: " + exception.getMessage());
        }
        if (j == com.dragonflow.SiteView.Monitor.kURLok) {
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
    public int execute(com.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, int i, java.io.PrintWriter printwriter, Array array) {
        timeoutTime = java.lang.System.currentTimeMillis() + (long) i;
        commandLineParam = remotecommandline;
        try {
            int j = internalExec(s, array, printwriter);
            if (j == com.dragonflow.SiteView.Monitor.kURLok) {
                return 0;
            }
            return j;
        } catch (java.lang.Exception exception) {
            com.dragonflow.Log.LogManager.log("Error", "SSHPlinkClient: Failed to execute command: " + s + " on machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ", exception: " + exception.getMessage());
            return -1;
        }
    }

    public static String checkClient() {
        String s;
        if (com.dragonflow.SiteView.Platform.isWindows()) {
            s = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "tools" + java.io.File.separator + "plink.exe";
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
        if (com.dragonflow.SiteView.Platform.isWindows()) {
            return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "tools" + java.io.File.separator + "plink.exe -ssh " + getKeyFileOption(s) + getPortOption() + "$user$@$host$ -pw \"$password$\"";
        }
        if (!(new File(s1)).exists()) {
            s1 = "/usr/bin/ssh";
        }
        return "$root$/tools/sshstart " + s1 + " ssh " + getKeyFileOption(s) + getPortOption() + "-l " + "$user$ -o FallBackToRsh=no $host$";
    }

    private String getKeyFileOption(String s) {
        String s1 = "";
        if (s != null && s.equals("keyfile")) {
            s1 = "-i " + machine.getProperty(com.dragonflow.SiteView.Machine.pSshKeyFile);
        }
        return s1 + " ";
    }

    private String getPortOption() {
        String s = "";
        int i = machine.getPropertyAsInteger(com.dragonflow.SiteView.Machine.pSSHPort);
        if (i != 22 && machine.getProperty(com.dragonflow.SiteView.Machine.pHost).indexOf(" -P") == -1) {
            s = (com.dragonflow.SiteView.Platform.isWindows() ? " -P " : " -p ") + i + " ";
        }
        return s;
    }

    private int internalConnect(com.dragonflow.SiteView.Machine machine, java.io.PrintWriter printwriter) throws java.lang.Exception {
        int i = com.dragonflow.SiteView.Monitor.kURLok;
        String s = machine.getProperty("_sshCommand");
        com.dragonflow.SiteView.Machine _tmp = machine;
        String s1 = machine.getProperty(com.dragonflow.SiteView.Machine.pPassword);
        String s2 = machine.getProperty(com.dragonflow.SiteView.Machine.pHost);
        com.dragonflow.SiteView.Machine _tmp1 = machine;
        String s3 = machine.getProperty(com.dragonflow.SiteView.Machine.pSshAuthMethod);
        com.dragonflow.SiteView.Machine _tmp2 = machine;
        String s4 = machine.getProperty(com.dragonflow.SiteView.Machine.pSecondaryPrompt);
        com.dragonflow.SiteView.Machine _tmp3 = machine;
        String s5 = machine.getProperty(com.dragonflow.SiteView.Machine.pSecondaryResponse);
        com.dragonflow.SiteView.Machine _tmp4 = machine;
        String s6 = machine.getProperty(com.dragonflow.SiteView.Machine.pPrompt);
        com.dragonflow.SiteView.Machine _tmp5 = machine;
        passwordPrompt = machine.getProperty(com.dragonflow.SiteView.Machine.pPasswordPrompt);
        String s7 = machine.getProperty("_sshNewline");
        if (s7.length() != 0) {
            writeNewline = "" + (char) com.dragonflow.Utils.TextUtils.toInt(s7);
        } else {
            writeNewline = java.lang.System.getProperty("line.separator");
        }
        if (writeNewline == null || writeNewline.length() <= 0) {
            writeNewline = "\n";
        }
        if (s.length() == 0) {
            s = getDefaultCommandLine(s3);
        }
        s = com.dragonflow.Utils.TextUtils.replaceVariable(s, "$root$", com.dragonflow.SiteView.Platform.getRoot());
        s = com.dragonflow.Utils.TextUtils.replaceVariable(s, "$host$", s2);
        com.dragonflow.SiteView.Machine _tmp6 = machine;
        String s8 = machine.getProperty(com.dragonflow.SiteView.Machine.pLogin);
        if (s8.length() == 0) {
            s = com.dragonflow.Utils.TextUtils.replaceString(s, "-l $user$", "");
        } else {
            s = com.dragonflow.Utils.TextUtils.replaceVariable(s, "$user$", s8);
        }
        String s9 = s;
        if (s.indexOf("$password$") != -1) {
            s = com.dragonflow.Utils.TextUtils.replaceVariable(s, "$password$", s1);
            s1 = "";
        }
        commandLineParam.progressMessage("Connecting to " + s2 + "...");
        com.dragonflow.Utils.RemoteCommandLine _tmp7 = commandLineParam;
        commandLineParam.traceMessage(s9, machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
        process = com.dragonflow.Utils.CommandLine.execSync(s);
        out = com.dragonflow.Utils.FileUtils.MakeOutputWriter(process.getOutputStream());
        rawInput = process.getInputStream();
        in = com.dragonflow.Utils.FileUtils.MakeInputReader(rawInput);
        errInput = process.getErrorStream();
        StringBuffer stringbuffer = new StringBuffer();
        if (machine.getProperty(com.dragonflow.SiteView.Machine.pOS).equals("NT") && machine.getProperty(com.dragonflow.SiteView.Machine.pMethod).equals("ssh") && com.dragonflow.SiteView.Platform.isWindows()) {
            return com.dragonflow.SiteView.Monitor.kURLok;
        }
        if (s1.length() > 0) {
            commandLineParam.progressMessage("Waiting for password prompt...");
            if (machine.getProperty(com.dragonflow.SiteView.Machine.pOS).equals("NT") && machine.getProperty(com.dragonflow.SiteView.Machine.pMethod).equals("ssh")) {
                passwordPrompt = "assword:";
            }
            do {
                i = fillBuffer(in, stringbuffer, commandLineParam.progressStream);
                if (i != com.dragonflow.SiteView.Monitor.kURLok) {
                    break;
                }
                if (match(stringbuffer, passwordPrompt)) {
                    commandLineParam.progressMessage("Sending password");
                    com.dragonflow.Utils.RemoteCommandLine _tmp8 = commandLineParam;
                    commandLineParam.traceMessage("(password)", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                    writeLine(s1);
                    String s10 = readLine();
                    if (s10 == null) {
                        if (timedout) {
                            i = com.dragonflow.SiteView.Monitor.kURLTimeoutError;
                            com.dragonflow.Utils.RemoteCommandLine _tmp9 = commandLineParam;
                            commandLineParam.traceMessage("Connection Dropped, timeout", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                        } else {
                            com.dragonflow.Utils.RemoteCommandLine _tmp10 = commandLineParam;
                            commandLineParam.traceMessage("Connection Dropped", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                            i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
                        }
                    } else {
                        com.dragonflow.Utils.RemoteCommandLine _tmp11 = commandLineParam;
                        commandLineParam.traceMessage(s10, machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
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
                    com.dragonflow.Utils.RemoteCommandLine _tmp12 = commandLineParam;
                    commandLineParam.traceMessage("yes", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                    writeLine("yes");
                    stringbuffer = new StringBuffer();
                }
            } while (true);
        } else {
            flushstderr(true);
            i = doLoginSequence(machine, s4, s5);
        }
        if (i == com.dragonflow.SiteView.Monitor.kURLok) {
            initShellEnvironment(machine);
        }
        if (sshSessionDebug) {
            com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + this.machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + "Done with internal connect");
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
                    com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
                    commandLineParam.traceMessage(s1, machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                }
            } while (true);
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
    }

    private void internalClose() {
        try {
            if (commandLineParam != null) {
                com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
                commandLineParam.traceMessage("close ssh connection", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
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
                    com.dragonflow.Utils.RemoteCommandLine _tmp1 = commandLineParam;
                    commandLineParam.traceMessage("closing output", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                }
                out.close();
            } catch (java.lang.Exception exception1) {
            }
            out = null;
        }
        if (in != null) {
            try {
                if (commandLineParam != null) {
                    com.dragonflow.Utils.RemoteCommandLine _tmp2 = commandLineParam;
                    commandLineParam.traceMessage("closing input", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                }
                in.close();
            } catch (java.lang.Exception exception2) {
            }
            in = null;
        }
        if (com.dragonflow.SiteView.Platform.isWindows()) {
            if (process != null) {
                try {
                    if (commandLineParam != null) {
                        com.dragonflow.Utils.RemoteCommandLine _tmp3 = commandLineParam;
                        commandLineParam.traceMessage("closing ssh process", machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
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

    private String portOk(com.dragonflow.SiteView.Machine machine) {
        if (noPortCheck || machine.getProperty("_sshCommand").length() != 0) {
            return "";
        }
        String s = machine.getProperty(com.dragonflow.SiteView.Machine.pHost);
        String s1 = "";
        int i = machine.getPropertyAsInteger(com.dragonflow.SiteView.Machine.pSSHPort);
        java.net.Socket socket = null;
        String as[] = com.dragonflow.Utils.TextUtils.split(s, " ");
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
            s1 = "SSHPlinkClient Error: SSH Connection down, UnknownHostException from port(" + i + ") on machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pName);
        } catch (java.net.SocketTimeoutException sockettimeoutexception) {
            s1 = "SSHPlinkClient Error: SSH Connection down, SocketTimeoutException from port(" + i + ") on machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pName);
        } catch (java.io.IOException ioexception) {
            s1 = "SSHPlinkClient Error: SSH Connection down, IOException(" + ioexception.getMessage() + ") from port(" + i + ") on machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pName);
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
            com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Getting ready to read ");
        }
        int j = bufferedreader.read(ac);
        int i;
        String s;
        if (j > 0) {
            i = com.dragonflow.SiteView.Monitor.kURLok;
            s = new String(ac);
            if (sshSessionDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": FillBuffer: Read #" + j + "chars\n Created Buffer (" + s + ")");
            }
        } else {
            i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
            s = "Connection dropped by remote host";
            com.dragonflow.Log.LogManager.log("Error", "ssh(" + machine.getProperty("_host") + ") " + s);
        }
        com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
        commandLineParam.traceMessage(s, machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
        if (printwriter != null) {
            printwriter.print(s);
        }
        stringbuffer.append(s);
        if (com.dragonflow.SiteView.Platform.isWindows()) {
            if (match(stringbuffer, "Bad host name") || match(stringbuffer, "gethostbyname") || match(stringbuffer, "Host does not exist")) {
                i = com.dragonflow.SiteView.Monitor.kURLBadHostNameError;
            } else if (match(stringbuffer, "connect()")) {
                i = com.dragonflow.SiteView.Monitor.kURLNoRouteToHostError;
            } else if (match(stringbuffer, "denied")) {
                i = 401;
            } else if (match(stringbuffer, "refused") || match(stringbuffer, "Refused")) {
                i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
            }
        }
        return i;
    }

    static {
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if (siteviewgroup.getProperty("_SSHNoPortCheck").length() != 0) {
            noPortCheck = true;
        }
    }
}
