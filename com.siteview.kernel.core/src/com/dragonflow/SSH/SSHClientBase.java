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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.recursionsw.jgl.Array;

public abstract class SSHClientBase {

    protected boolean sshSessionDebug;

    protected com.dragonflow.SiteView.Machine machine;

    protected int charactersRemoved;

    protected java.io.PrintWriter out;

    protected java.io.BufferedReader in;

    protected java.io.InputStream rawInput;

    protected java.io.InputStream errInput;

    protected String passwordPrompt;

    protected com.dragonflow.Utils.RemoteCommandLine commandLineParam;

    protected String writeNewline;

    protected long timeoutTime;

    protected boolean timedout;

    private boolean seenBeginMarker;

    private java.io.BufferedWriter fileOutput;

    private boolean writeToFile;

    protected static int SSH_SESSION_SLEEP = 100;

    public static final String DEFAULT_KEY_FILE;

    public static final int CLOSE_DELAY = 2000;

    protected static final int PrematureCommandPromptFound = 666;

    protected static String endMarker;

    protected static String beginMarker;

    protected static String endEcho;

    protected static String beginEcho;

    private static int BUF_SIZE = 1024;

    public SSHClientBase() {
        sshSessionDebug = false;
        machine = null;
        out = null;
        in = null;
        rawInput = null;
        errInput = null;
        passwordPrompt = null;
        commandLineParam = null;
        writeNewline = "";
        timeoutTime = 0L;
        timedout = false;
        seenBeginMarker = false;
        writeToFile = false;
    }

    protected void writeLine(String s) {
        out.print(s + writeNewline);
        out.flush();
    }

    /**
     * 
     * 
     * @return
     * @throws java.io.IOException
     */
    protected String readLine() throws java.io.IOException {
        StringBuffer stringbuffer;
        char ac[];
        stringbuffer = new StringBuffer(BUF_SIZE);
        Object obj = null;
        ac = new char[BUF_SIZE];
        if (in != null) {
            in.mark(BUF_SIZE);
        }
        while (in != null) {
            if (in.ready()) {
                int i = in.read(ac, 0, BUF_SIZE - stringbuffer.length());
                if (sshSessionDebug) {
                    com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": Readline: Read #" + i + " chars\n Created Buffer (" + new String(ac) + ")");
                }
                if (i == -1) {
                    String s = stringbuffer.toString();
                    if (sshSessionDebug) {
                        com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": Readline:Returning partial line:(" + s + ")");
                    }
                    return s;
                }
                stringbuffer.append(ac, 0, i);
                String s1 = returnLine(stringbuffer);
                if (s1 == null) {
                    if (stringbuffer.length() < BUF_SIZE) {
                        continue; /* Loop/switch isn't completed */
                    }
                    s1 = stringbuffer.toString();
                    com.dragonflow.Log.LogManager.log("Error", "SSHClientBase Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + " Line greater than max of " + BUF_SIZE + " Readline:Returning (" + s1 + ")");
                } else {
                    long l1 = 0L;
                    try {
                        in.reset();
                        l1 = in.skip(charactersRemoved);
                        if (l1 != (long) charactersRemoved) {
                            com.dragonflow.Log.LogManager.log("Error", "SSHClientBase buffer may be corrupted. Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": Chars removed:" + charactersRemoved + "Chars Skipped " + l1
                                    + " Readline:Returning (" + s1 + ")");
                        }
                    } catch (java.io.IOException ioexception) {
                        s1 = stringbuffer.toString();
                        com.dragonflow.Log.LogManager.log("Error", "SSHClientBase: Error reading input, possible data lost. Reason: " + ioexception.getMessage());
                    }
                    if (sshSessionDebug) {
                        com.dragonflow.Log.LogManager.log("RunMonitor", "SSHClientBase Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": Chars removed:" + charactersRemoved + "Chars Skipped " + l1 + " Readline:Returning (" + s1
                                + ")");
                    }
                }
                return s1;
            }
            if (sshSessionDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor", "Machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + ": Readline: waiting to read characters");
            }
            long l = getSleepingTime(SSH_SESSION_SLEEP);
            if (l == -1L) {
                return null;
            }
            try {
                java.lang.Thread.currentThread();
                java.lang.Thread.sleep(l);
            } catch (java.lang.InterruptedException interruptedexception) {
                com.dragonflow.Log.LogManager.log("Error", "SSHSession for " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost) + " interrupted during sleep");
            }
        }

        return null;
    }

    private String returnLine(StringBuffer stringbuffer) {
        String s = null;
        s = stringbuffer.toString();
        int i = s.indexOf('\n');
        if (i != -1) {
            charactersRemoved = i + 1;
            for (; i > 0 && s.charAt(i - 1) == '\r'; i --) {
            }
            String s1 = s.substring(0, i);
            s1 = s1.replaceAll(" \r", "");
            return s1;
        } else {
            return null;
        }
    }

    protected String translateBackspace(com.dragonflow.SiteView.Machine machine1, String s) {
        byte byte0 = 8;
        int i = 0;
        do {
            int j = s.indexOf(byte0, 1);
            if (j == -1) {
                break;
            }
            String s1 = s.substring(0, j - 1);
            String s2 = s.substring(j + 1);
            s = s1 + s2;
            i ++;
        } while (true);
        if (i != 0) {
            com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
            commandLineParam.traceMessage("removed " + i + " backspace characters", machine1, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
        }
        return s;
    }

    protected int flushInput(com.dragonflow.SiteView.Machine machine1) throws java.io.IOException {
        StringBuffer stringbuffer = new StringBuffer();
        for (; in.ready(); stringbuffer.append((char) in.read())) {
        }
        com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
        commandLineParam.traceMessage("flushed (" + stringbuffer + ")", machine1, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
        return stringbuffer.length();
    }

    protected long getSleepingTime(long l) {
        long l1 = java.lang.System.currentTimeMillis();
        if (timeoutTime <= l1) {
            timedout = true;
            return -1L;
        } else {
            return java.lang.Math.min(timeoutTime - l1, l);
        }
    }

    /**
     * 
     * 
     * @param s
     * @param array
     * @param printwriter
     * @return
     * @throws java.lang.Exception
     */
    protected int internalExec(String s, Array array, java.io.PrintWriter printwriter) throws java.lang.Exception {
        int i;
        i = com.dragonflow.SiteView.Monitor.kURLok;
        s = initializeFileForOutput(s);

        try {
            if (in.ready()) {
                long l;
                int j;
                while (in.ready()) {
                    flushInput(machine);
                    l = getSleepingTime(2000L);
                    if (l == -1L) {
                        j = com.dragonflow.SiteView.Monitor.kURLTimeoutError;
                        closeFileForOutuput();
                        return j;
                    }

                    java.lang.Thread.currentThread();
                    java.lang.Thread.sleep(l);
                }

            } else {
                commandLineParam.traceMessage("(input buffer empty)", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
            }

            boolean flag = true;
            if (machine.getProperty(com.dragonflow.SiteView.Machine.pOS).equals("NT") && machine.getProperty(com.dragonflow.SiteView.Machine.pMethod).equals("ssh")) {
                writeLine(s);
                commandLineParam.traceMessage(s, machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                while (flag) {
                    String s1 = readLine();
                    if (s1 == null) {
                        if (timedout) {
                            i = com.dragonflow.SiteView.Monitor.kURLTimeoutError;
                            commandLineParam.traceMessage("Connection Dropped, timeout", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                        } else {
                            commandLineParam.traceMessage("Connection Dropped", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                            i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
                        }
                        flag = false;
                    } else {
                        flag = processNTSSHLine(s1, printwriter, s, array);
                    }
                }
            } else {
                String s2 = beginEcho + "; " + s.trim() + "; " + endEcho;
                seenBeginMarker = false;
                commandLineParam.traceMessage(s2, machine, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
                writeLine(s2);
                while (flag) {
                    String s3 = readLine();
                    if (s3 == null) {
                        if (timedout) {
                            i = com.dragonflow.SiteView.Monitor.kURLTimeoutError;
                            commandLineParam.traceMessage("Connection Dropped, timeout", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                        } else {
                            commandLineParam.traceMessage("Connection Dropped", machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
                            i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
                        }
                        break;
                    }
                    flag = processUnixLine(s3, printwriter, array);
                }
            }
            closeFileForOutuput();
        } catch (Exception exception) {
            closeFileForOutuput();
            throw exception;
        }
        return i;
    }

    private String initializeFileForOutput(String s) {
        String s1 = com.dragonflow.Utils.RemoteCommandLine.extractDestFileIfAny(s);
        if (s1 != null) {
            java.io.File file = new File(s1);
            try {
                fileOutput = new BufferedWriter(new FileWriter(file, false));
                writeToFile = true;
            } catch (java.io.IOException ioexception) {
                com.dragonflow.Log.LogManager.log("Error", "SSH: Could not write output to file + " + s1 + ioexception.getMessage());
            }
            return com.dragonflow.Utils.RemoteCommandLine.unMangleCmdStringForFileGet(s);
        } else {
            writeToFile = false;
            return s;
        }
    }

    /**
     * 
     * 
     */
    private void closeFileForOutuput() {
        if (fileOutput != null) {
            try {
                fileOutput.flush();
                fileOutput.close();
                fileOutput = null;
            } catch (RuntimeException exception) {
                fileOutput = null;
                throw exception;
            } catch (java.io.IOException ioexception) {
                ioexception.printStackTrace();
                fileOutput = null;
            }
        }
        writeToFile = false;
    }

    private boolean processUnixLine(String s, java.io.PrintWriter printwriter, Array array) {
        boolean flag = true;
        s = translateBackspace(machine, s);
        com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
        commandLineParam.traceMessage(s, machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
        if (printwriter != null) {
            printwriter.println(s);
        }
        if (!seenBeginMarker && s.indexOf(beginMarker) >= 0 && s.indexOf("\"" + beginMarker) < 0 && s.indexOf(beginMarker + "\"") < 0) {
            seenBeginMarker = true;
        } else if (!seenBeginMarker || s.length() == 0) {
            flag = true;
        } else if (s.indexOf(endMarker) >= 0) {
            flag = false;
        } else {
            storeLine(s, array);
            flag = true;
        }
        return flag;
    }

    private boolean processNTSSHLine(String s, java.io.PrintWriter printwriter, String s1, Array array) {
        boolean flag = true;
        com.dragonflow.Utils.RemoteCommandLine _tmp = commandLineParam;
        commandLineParam.traceMessage(s, machine, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
        if (printwriter != null) {
            printwriter.println(s);
        }
        if (s.length() != 0) {
            if (s1.indexOf("perfex") == 0 && (s1.indexOf(" -h") > 6 || s1.indexOf(" -o ") > 6)) {
                if (s.indexOf("end perfex") >= 0) {
                    flag = false;
                }
            } else if (s1.indexOf("elog") > 0 || s1.indexOf("perfex") == 0 && s1.indexOf(" -s") > 0) {
                if (s.indexOf("end perfex") >= 0 || s.indexOf("end SendModem") >= 0 || s.indexOf("end Script") >= 0 || s.trim().equalsIgnoreCase("end script")) {
                    flag = false;
                }
            } else if (s.indexOf("end perfex") >= 0 || s.indexOf("end SendModem") >= 0 || s.indexOf("end Script") >= 0 || s.trim().equalsIgnoreCase("end script") || s.indexOf("Error") >= 0 || s.indexOf("exit") >= 0) {
                flag = false;
            } else if (s1.indexOf("srvrmgr") >= 0) {
                if (s.indexOf("returned") >= 0) {
                    flag = false;
                }
            } else {
                storeLine(s, array);
            }
        }
        return flag;
    }

    private void storeLine(String s, Array array) {
        if (writeToFile) {
            try {
                if (fileOutput != null) {
                    fileOutput.write(s + "\n");
                    if (array.size() == 0) {
                        array.add(" sIiTeSsCoPeReDiReCTtOkEN* ");
                    }
                } else if (array.size() == 0) {
                    array.add("SSH Error writing to file. Null Writer");
                }
            } catch (java.io.IOException ioexception) {
                array.add(ioexception.getMessage());
            }
        } else {
            array.add(s);
        }
    }

    protected boolean match(StringBuffer stringbuffer, String s) {
        String s1 = stringbuffer.toString();
        boolean flag = s1.indexOf(s) != -1;
        return flag;
    }

    protected int doLoginSequence(com.dragonflow.SiteView.Machine machine1, String s, String s1) throws java.lang.Exception {
        int i = com.dragonflow.SiteView.Monitor.kURLok;
        String s2 = machine1.getProperty("_connectDelay");
        com.dragonflow.SiteView.Machine _tmp = machine1;
        String s3 = machine1.getProperty(com.dragonflow.SiteView.Machine.pPrompt);
        if (s2.length() <= 0) {
            if (s1.length() > 0) {
                i = processSecondaryResponses(in, s3, s, s1);
            }
            if (i != 666) {
                i = readUntilPrompt(in, s3, commandLineParam.progressStream);
            } else if (i != 401) {
                i = com.dragonflow.SiteView.Monitor.kURLok;
            }
        } else {
            int j = com.dragonflow.Utils.TextUtils.toInt(machine1.getProperty("_connectDelay"));
            if (j <= 0 || j > 0x75300) {
                j = 3000;
            }
            int k = 0;
            for (int l = 0; in.ready() || l == 0 && k < 5; k ++) {
                l += flushInput(machine1);
                java.lang.Thread.sleep(j);
            }

        }
        return i;
    }

    protected int processSecondaryResponses(java.io.BufferedReader bufferedreader, String s, String s1, String s2) throws java.io.IOException {
        int i;
        String as[];
        String as1[];
        i = com.dragonflow.SiteView.Monitor.kURLok;
        as = com.dragonflow.Utils.TextUtils.split(s1, ",");
        as1 = com.dragonflow.Utils.TextUtils.split(s2, ",");
        if (as.length != as1.length) {
            com.dragonflow.Log.LogManager.log("Error", "The number of secondary responses does not equal the number of secondary prompts, unable to process, returning.");
            return i;
        }
        for (int j = 0; j < as1.length; j ++) {
            StringBuffer stringbuffer;
            commandLineParam.progressMessage("Looking for " + as[j] + " prompt...");
            stringbuffer = new StringBuffer();

            try {
                while (fillBuffer(bufferedreader, stringbuffer, null) == com.dragonflow.SiteView.Monitor.kURLok) {
                    if (match(stringbuffer, as[j])) {
                        commandLineParam.progressMessage("Sending " + as1[j] + ".");
                        if (as1[j].equals("\\n")) {
                            as1[j] = "";
                        }
                        writeLine(as1[j]);
                        break;
                    }
                    if (match(stringbuffer, s)) {
                        i = 666;
                        return i;
                    }

                    if (match(stringbuffer, passwordPrompt)) {
                        i = 401;
                        com.dragonflow.Log.LogManager.log("Error", "ssh(" + machine.getProperty("_host") + ") Logon failed");
                        return i;
                    }

                    stringbuffer = new StringBuffer();
                }
            } catch (java.io.IOException ioexception) {
                ioexception.printStackTrace();
                com.dragonflow.Log.LogManager.log("Error", "IO Exception thrown in SSHClientBase::processSecondaryPrompts()" + ioexception.getMessage());
            }
        }
        return i;
    }

    protected int readUntilPrompt(java.io.BufferedReader bufferedreader, String s, java.io.PrintWriter printwriter) throws java.io.IOException {
        int i = com.dragonflow.SiteView.Monitor.kURLok;
        commandLineParam.progressMessage("Waiting for prompt(" + s + ")...");
        StringBuffer stringbuffer = new StringBuffer();
        while ((i = fillBuffer(bufferedreader, stringbuffer, printwriter)) == com.dragonflow.SiteView.Monitor.kURLok || match(stringbuffer, s)) {
            if (match(stringbuffer, passwordPrompt)) {
                i = 401;
                com.dragonflow.Log.LogManager.log("Error", "ssh(" + machine.getProperty("_host") + ") Logon failed");
                break;
            }
        }
        return i;
    }

    protected void initShellEnvironment(com.dragonflow.SiteView.Machine machine1) throws java.lang.Exception {
        com.dragonflow.SiteView.Machine _tmp = machine1;
        String s = machine1.getProperty(com.dragonflow.SiteView.Machine.pInitShellEnvironment);
        if (s.length() != 0) {
            commandLineParam.progressMessage("Initializing shell environment with:" + s);
            com.dragonflow.Utils.RemoteCommandLine _tmp1 = commandLineParam;
            commandLineParam.traceMessage(s, machine1, com.dragonflow.Utils.RemoteCommandLine.TO_REMOTE);
            writeLine(s);
            com.dragonflow.Utils.RemoteCommandLine _tmp2 = commandLineParam;
            commandLineParam.traceMessage(readLine(), machine1, com.dragonflow.Utils.RemoteCommandLine.FROM_REMOTE);
            internalExec("echo start", new Array(), commandLineParam.progressStream);
        }
    }

    protected abstract int fillBuffer(java.io.BufferedReader bufferedreader, StringBuffer stringbuffer, java.io.PrintWriter printwriter) throws java.io.IOException;

    static {
        DEFAULT_KEY_FILE = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "identity ";
        endMarker = "siteview-command-end";
        beginMarker = "siteview-command-begin";
        endEcho = "echo \"" + endMarker + "\"";
        beginEcho = "echo \"" + beginMarker + "\"";
    }
}
