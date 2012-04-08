/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * ScriptMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ScriptMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.OSAdapter;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Portal;
import COM.dragonflow.SiteView.PortalSiteView;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.RemoteFile;
import COM.dragonflow.Utils.SSHCommandLine;
import COM.dragonflow.Utils.ScriptMonitorCache;
import COM.dragonflow.Utils.TextUtils;
import COM.oroinc.text.perl.Perl5Util;

public class ScriptMonitor extends ServerMonitor {

    static final String DEFAULT_TIMEOUT = "-1";

    static final String DEFAULT_SCRIPT_FOLDER = "scripts";

    static StringProperty pLocalScriptLocation;

    static StringProperty pScript;

    static StringProperty pExpression;

    static StringProperty pParameters;

    static StringProperty pMaxMeasurement;

    static StringProperty pRemoteScript;

    static StringProperty pCacheLife;

    static StringProperty pTimeout;

    static StringProperty pRoundTripTime;

    static StringProperty pStatus;

    static StringProperty pStatusLabel;

    static StringProperty pScriptOutput;

    static StringProperty pValueLabels;

    static NumericProperty pMatchValue[];

    static int maxNumberOfMatches;

    private static final String VALUE = "value";

    private String replacementChars[];

    private static final String replacementCharsTag = "_scriptMonitorReplacementChars";

    private Object replacementCharsSync;

    private boolean replacementCharsInitialized;

    private static final String replacementCharsDelimiter = " ";

    HashMap labelsCache;

    static HashMap scriptCommands = new HashMap();

    static final int NUM_TRYS_FOR_FILE_NOT_FOUND = 10;

    static int useStatusAndRoundtrip = 0;

    private static final String SCRIPT_MONITOR_USE_STATUS_AND_ROUNDTRIP = "_scriptMonitorUseStatusAndRoundtrip";

    public ScriptMonitor() {
        replacementChars = null;
        replacementCharsSync = new Object();
        replacementCharsInitialized = false;
        labelsCache = null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String[] getReplacementChars() {
        synchronized (replacementCharsSync) {
            if (replacementCharsInitialized) {
                return replacementChars;
            }
            String s = getSetting("_scriptMonitorReplacementChars");
            if (s != null && s.length() > 0) {
                String as[] = TextUtils.split(s, " ");
                if (as != null) {
                    Vector vector = new Vector();
                    for (int i = 0; i < as.length; i ++) {
                        String s1 = as[i];
                        if (s1 == null) {
                            continue;
                        }
                        String s2 = s1.trim();
                        if (s2.length() > 0) {
                            vector.add(s2);
                        }
                    }

                    if (vector.size() > 0) {
                        replacementChars = new String[vector.size()];
                        for (int j = 0; j < vector.size(); j ++) {
                            replacementChars[j] = (String) vector.get(j);
                        }

                    }
                }
            }
            replacementCharsInitialized = true;
            return replacementChars;
        }
    }

    String getParameters() {
        String s = I18N.toDefaultEncoding(getProperty("_parameters"));
        if (TextUtils.isSubstituteExpression(s)) {
            s = TextUtils.substitute(s);
        }
        s = createFromTemplate(s);
        if (TextUtils.hasChars(s, "`;&|<>")) {
            LogManager.log("Error", "Removed illegal characters from script monitor parameters \"" + s + "\"" + " for monitor " + getProperty(pName));
            s = TextUtils.removeChars(s, "`;&|<>");
        }
        return s;
    }

    synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            if (getProperty(pValueLabels).length() > 0) {
                String as[] = TextUtils.split(getProperty(pValueLabels), ",");
                for (int i = 0; i < as.length; i ++) {
                    String s1 = as[i];
                    String s2 = getNameFromIndex(i);
                    labelsCache.add(s2, s1.trim());
                }

            } else {
                for (int j = 0; j < maxNumberOfMatches; j ++) {
                    String s = getNameFromIndex(j);
                    labelsCache.add(s, s);
                }

            }
            Array array = getProperties();
            Enumeration enumeration = array.elements();
            do {
                if (!enumeration.hasMoreElements()) {
                    break;
                }
                StringProperty stringproperty = (StringProperty) enumeration.nextElement();
                if (stringproperty.isThreshold() && stringproperty.getName().indexOf("value") < 0) {
                    labelsCache.add(stringproperty.getLabel(), stringproperty.getLabel());
                }
            } while (true);
        }
        return labelsCache;
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if (s1.length() == 0) {
            s1 = s;
        }
        return s1;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        if (stringproperty == pStatus) {
            return getProperty(pStatusLabel);
        }
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if (s1.length() != 0) {
            return s1;
        }
        if (flag && s.indexOf("value") < 0) {
            return "";
        } else {
            return s;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     * @throws IOException
     */
    private String getCommandFromLocalFile(String s) throws IOException {
        String s1;
        s1 = (String) scriptCommands.get(s);
        if (s1 == null) {
            try {
                s1 = FileUtils.readFile(Platform.getUsedDirectoryPath("scripts.remote", "") + "//" + s).toString();
            } catch (FileNotFoundException filenotfoundexception) {

                int i = 0;
                for (; i < 10; i ++) {
                    try {
                        s1 = FileUtils.readFile(Platform.getUsedDirectoryPath("scripts.remote", "") + "//" + s).toString();
                        if (s1 == null) {
                            continue;
                        }
                    } catch (FileNotFoundException filenotfoundexception1) {
                        try {
                            Thread.currentThread();
                            Thread.sleep(100L);
                        } catch (InterruptedException interruptedexception) {
                            interruptedexception.printStackTrace();
                        }
                    }
                }

                if (i == 10) {
                    throw filenotfoundexception;
                }
            }
            scriptCommands.add(s, s1);
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        long l = Platform.timeMillis();
        int i = -1;
        String[] as = (new String[] { "" });
        String s = getProperty(pExpression);
        long l1 = getPropertyAsLong(pMaxMeasurement);
        int j = getPropertyAsInteger(pCacheLife);
        boolean flag = j > 0;
        Array array = new Array();
        String s1 = "";

        try {
            String s2 = I18N.toDefaultEncoding(getProperty(pScript));
            String s3 = I18N.toDefaultEncoding(getProperty(pRemoteScript));
            String s4 = null;
            String s7 = I18N.toDefaultEncoding(getProperty(pLocalScriptLocation));
            if (s7.length() == 0) {
                s7 = "scripts";
            } else if (s7.indexOf("../") >= 0 || s7.indexOf("..\\") >= 0) {
                failMonitorRun(i, "Illegal Script Location contains ../ construct");
                return true;
            }

            s1 = getProperty(pMachineName);
            if (Machine.isNTSSH(s1) && s2.equals("USE COMMAND")) {
                failMonitorRun(i, "Can't use this option with remote NT ssh connection");
                return true;
            }

            if (Platform.isRemote(s1) && Machine.getMachine(s1) == null) {
                failMonitorRun(i, "Remote host unreachable");
                return true;
            }

            if (Platform.isNTRemote(s1) && !Machine.isNTSSH(s1)) {
                failMonitorRun(i, "NT host must be configured as NT ssh remote ");
                return true;
            }

            if (s3.equals("none") && s2.equals("USE COMMAND")) {
                failMonitorRun(i, "Need to specify a script");
                return true;
            }

            if (s3.length() != 0 && !s3.equals("none")) {
                try {
                    s4 = getCommandFromLocalFile(s3);
                    s4 = TextUtils.replaceParameters(s4, getParameters(), getReplacementChars());
                } catch (IOException ioexception) {
                    LogManager.log("Error", " File load error " + ioexception.toString());
                    setProperty(pNoData, "n/a");
                }
            } else {
                if (!Platform.isRemote(s1)) {
                    String s8 = Platform.getRoot() + "/" + s7 + "/" + s2;
                    File file = new File(s8);
                    s4 = file.getAbsolutePath();
                } else {
                    String s9 = "scripts" + Machine.getMachinePathSeparator(s1) + s2;
                    OSAdapter osadapter = Machine.getAdapter(s1);
                    if (osadapter != null) {
                        CommandLine commandline = new CommandLine();
                        String s11 = osadapter.getCommandSetting("fileExists", "changeDirectory");
                        if (s11.length() > 0) {
                            s4 = s11;
                        } else {
                            s4 = "/usr/bin/cd";
                        }
                        commandline.exec(s4, s1, Platform.getLock(s1));
                    }
                    RemoteFile remotefile = new RemoteFile(s1, s9);
                    s4 = remotefile.getFullPath();
                }
                s4 = s4 + " " + getParameters();
                System.out.println("Script: " + s4);
            }
            LogManager.log("RunMonitor", "Script monitor command: " + s4 + ", machine: " + s1);
            String s10 = s1;
            if (s10.startsWith("\\\\")) {
                s10 = s10.substring(2);
            }
            Machine machine = Machine.getNTMachine(s10);
            int j1 = getPropertyAsInteger(pTimeout) * 1000;
            if (j1 < 0) {
                j1 = getSettingAsLong("_scriptMonitorTimeout", -1) * 1000;
            }
            String s12 = getScriptServerName(s1);
            String s14 = getProperty(pScript).equals("USE COMMAND") ? getProperty(pRemoteScript) : getProperty(pScript);
            ScriptMonitorCache scriptmonitorcache = new ScriptMonitorCache(s12, s14, getPropertyAsInteger(pCacheLife));
            if (!flag && alertDebug) {
                System.out.println("Caching is disabled.");
            }
            boolean flag1 = scriptmonitorcache.isFresh();
            if (flag && flag1 && scriptmonitorcache.getExitValue() == 0) {
                if (alertDebug) {
                    System.out.println("Caching is enabled, the cache life time is set to: " + getPropertyAsInteger(pCacheLife));
                }
                if (alertDebug) {
                    System.out.println("The cache is still good, not exec'ing script, cache last modified on: " + scriptmonitorcache.getLastModDate());
                }
                array = scriptmonitorcache.getOutput();
                i = scriptmonitorcache.getExitValue();
            } else if (machine != null && Machine.isNTSSH(s10)) {
                if (s4.indexOf("\\\\" + s10) > 0) {
                    s4 = TextUtils.replaceString(s4, "\\\\" + s10, "");
                }
                s4 = "scripts\\" + s4.substring(s4.indexOf(s2));
                if (j1 > 0) {
                    s4 = CommandLine.getExecSyncCmd(s10, s4, j1, true);
                }
                SSHCommandLine sshcommandline = new SSHCommandLine();
                array = sshcommandline.exec(s4, machine, false);
                i = sshcommandline.exitValue;
            } else {
                CommandLine commandline1 = new CommandLine();
                array = commandline1.exec(s4, s1, Platform.monitorLock, j1);
                i = commandline1.getExitValue();
            }
            if (flag && !flag1) {
                if (alertDebug) {
                    System.out.println("Exec'd script and updating cache.");
                }
                scriptmonitorcache.update(i, array);
            }
            if (i < 0) {
                String s16 = "Failed to run script";
                if (i == Monitor.kURLTimeoutError) {
                    s16 = "Script timed out";
                }
                failMonitorRun(i, s16);
                return true;
            }

            StringBuffer stringbuffer1 = new StringBuffer();
            String s17;
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                s17 = (String) enumeration.nextElement();
                LogManager.log("RunMonitor", "Script monitor machine " + s1 + " output: " + s17);
                if (s17.startsWith(CommandLine.PERFEX_EXECSYNC_TIMEOUT)) {
                    failMonitorRun(-1, "Error: Timeout");
                    return true;
                }
                if (s17.indexOf("not found") > 0 || s17.indexOf("Not Found") > 0 || s17.indexOf("denied") > 0 || s17.indexOf("Denied") > 0 || s17.indexOf("cannot execute") > 0 || s17.indexOf("such file or directory") > 0) {
                    failMonitorRun(-1, s17);
                    return true;
                }

                stringbuffer1.append(s17);
                stringbuffer1.append("\n");
                if (s.length() > 0 && !TextUtils.isRegularExpression(s) && s17.indexOf(s) != -1) {
                    long l4 = TextUtils.findLong(s17, "", "");
                    if (l4 != -1L) {
                        as[0] = String.valueOf(l4);
                    }
                }
            }

            if (TextUtils.isSubstituteExpression(s)) {
                s = TextUtils.substitute(s, this);
            }
            if (s.length() > 0 && TextUtils.isRegularExpression(s)) {
                String s18 = stringbuffer1.toString();
                Perl5Util perl5util = new Perl5Util();
                if (perl5util.match(s, s18)) {
                    int i2 = perl5util.groups();
                    if (i2 > 0) {
                        if (i2 == 1) {
                            as = new String[i2];
                            as[0] = perl5util.group(0);
                        } else {
                            if (-- i2 > maxNumberOfMatches) {
                                i2 = maxNumberOfMatches;
                            }
                            as = new String[i2];
                            for (int j2 = 0; j2 < as.length && j2 < maxNumberOfMatches; j2 ++) {
                                as[j2] = perl5util.group(j2 + 1);
                            }

                        }
                    }
                } else {
                    failMonitorRun(-1, "Content Match Error");
                    return true;
                }
            }
        } catch (Exception exception) {
            LogManager.log("RunMonitor", "Script monitor error: " + exception + " machine: " + s1);
            setProperty(pNoData, "n/a");
        }

        LogManager.log("RunMonitor", "Script monitor exit: " + i + " machine: " + s1);
        setProperty(pScriptOutput, "");
        if (as[0].trim().length() == 0) {
            as[0] = "n/a";
            long l2 = getSettingAsLong("_scriptMonitorLinesToSave", 25);
            String s5 = "";
            for (int k = 0; k < array.size() && (long) k < l2; k ++) {
                s5 = s5 + array.at(k);
                s5 = s5 + "^";
            }

            s5 = s5.replace('\r', ' ');
            s5 = s5.replace('\n', '^');
            setProperty(pScriptOutput, s5);
        }
        long l3 = Platform.timeMillis() - l;
        String s6 = TextUtils.floatToString((float) l3 / 1000F, 2) + " sec";
        if (stillActive()) {
            synchronized (this) {
                for (int i1 = 0; i1 < as.length; i1 ++) {
                    setProperty(pMatchValue[i1], as[i1]);
                }

                setProperty(pStatus, i);
                if (i != 0) {
                    setProperty(pNoData, "n/a");
                }
                setProperty(pRoundTripTime, l3);
                setProperty(pMeasurement, getMeasurement(pRoundTripTime, l1));
                if (s.length() > 0) {
                    HashMap hashmap = getLabels();
                    StringBuffer stringbuffer = new StringBuffer();
                    for (int k1 = 0; k1 < as.length; k1 ++) {
                        String s13 = getNameFromIndex(k1);
                        String s15 = (String) hashmap.get(s13);
                        if (s15 == null) {
                            s15 = s13;
                        }
                        stringbuffer.append(s15 + "=" + as[k1] + (k1 >= as.length - 1 ? "" : ","));
                    }

                    setProperty(pStateString, stringbuffer);
                } else {
                    setProperty(pStateString, "exit: " + i + ", " + s6);
                }
            }
        }
        return true;
    }

    private void failMonitorRun(int i, String s) {
        setProperty(pStatus, i);
        setProperty(pStateString, s);
        for (int j = 0; j < pMatchValue.length; j ++) {
            NumericProperty numericproperty = pMatchValue[j];
            setProperty(numericproperty, "n/a");
        }

        setProperty(pRoundTripTime, "n/a");
    }

    private static String getNameFromIndex(int i) {
        return "value" + (i == 0 ? "" : (i + 1) + "");
    }

    private String getScriptServerName(String s) {
        String s1 = "";
        if (s == null) {
            return s1;
        }
        if (s.length() == 0) {
            s1 = "localhost";
        } else {
            int i = -1;
            if ((i = s.lastIndexOf(":")) != -1) {
                s1 = s.substring(i + 1);
            } else if ((i = s.lastIndexOf("\\")) != -1) {
                s1 = s.substring(i + 1);
            }
        }
        return s1;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pScript) {
            Vector vector = getScriptList(getProperty(pMachineName), getProperty(pLocalScriptLocation), httprequest);
            vector.addElement("USE COMMAND");
            vector.addElement("USE COMMAND");
            return vector;
        }
        if (scalarproperty == pRemoteScript) {
            Vector vector1 = new Vector();
            File file = new File(Platform.getUsedDirectoryPath("scripts.remote", httprequest.getAccount()));
            String as[] = file.list();
            if (as != null) {
                vector1.addElement("none");
                vector1.addElement("none");
                for (int i = 0; i < as.length; i ++) {
                    String s = I18N.toNullEncoding(as[i]);
                    if (Platform.isUnix() && s.startsWith(".")) {
                        continue;
                    }
                    File file1 = new File(file, as[i]);
                    if (!file1.isDirectory()) {
                        vector1.addElement(s);
                        vector1.addElement(s);
                    }
                }

            }
            return vector1;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public static Vector getScriptList(String s, String s1, HTTPRequest httprequest) {
        Vector vector = new Vector();
        String s2 = s1;
        if (s2 == null || s2.length() == 0) {
            s2 = "scripts";
        }
        s = Machine.getFullMachineID(s, httprequest);
        if (Machine.isPortalMachineID(s)) {
            String s3 = Machine.getServerIDFromMachineID(s);
            PortalSiteView portalsiteview = (PortalSiteView) Portal.getPortal().getElement(s3);
            if (portalsiteview != null) {
                String s4 = "/SiteView/cgi/go.exe/SiteView?page=remoteOp&operation=scripts&machineID=" + Machine.getMachineFromMachineID(s) + "&account=administrator";
                Array array2 = portalsiteview.sendURLToRemoteSiteView(s4, null);
                for (int i1 = 0; i1 < array2.size(); i1 ++) {
                    vector.addElement(array2.at(i1));
                }

            } else {
                LogManager.log("Error", "Could not find SiteView ID: " + s3);
            }
        } else if (Machine.isNTSSH(s)) {
            RemoteFile remotefile = new RemoteFile(s, "scripts");
            Array array = remotefile.listFiles();
            for (int j = 0; j < array.size(); j ++) {
                String s5 = I18N.toNullEncoding((String) array.at(j));
                if (!s5.endsWith(".txt") && !s5.endsWith("directory.bat") && (s5.endsWith(".bat") || s5.endsWith(".vbs") || s5.endsWith(".exe") || s5.endsWith(".pl") || s5.endsWith(".sh"))) {
                    String as1[] = TextUtils.split(s5, " ");
                    s5 = as1[as1.length - 1].trim();
                    vector.addElement(s5);
                    vector.addElement(s5);
                }
            }

        } else if (Platform.isCommandLineRemote(s)) {
            RemoteFile remotefile1 = new RemoteFile(s, "scripts");
            int i = Machine.getOS(s);
            Array array1 = remotefile1.listFiles();
            for (int l = 0; l < array1.size(); l ++) {
                String s7 = I18N.toNullEncoding((String) array1.at(l));
                if (!s7.endsWith(".txt") && (!Platform.isUnix(i) || !s7.startsWith("."))) {
                    vector.addElement(s7);
                    vector.addElement(s7);
                }
            }

        } else {
            File file = new File(Platform.getUsedDirectoryPath(s2, httprequest.getAccount()));
            String as[] = file.list();
            for (int k = 0; k < as.length; k ++) {
                String s6 = I18N.toNullEncoding(as[k]);
                if (s6.endsWith(".txt") || Platform.isUnix() && s6.startsWith(".")) {
                    continue;
                }
                File file1 = new File(file, as[k]);
                if (!file1.isDirectory()) {
                    vector.addElement(s6);
                    vector.addElement(s6);
                }
            }

        }
        return vector;
    }

    public static Vector getScriptList(String s, HTTPRequest httprequest) {
        return getScriptList(s, null, httprequest);
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        if (useStatusAndRoundtrip()) {
            array.add(pStatus);
            array.add(pRoundTripTime);
        }
        addValuesPropertiesToArray(array);
        return array;
    }

    private static boolean useStatusAndRoundtrip() {
        if (useStatusAndRoundtrip == 0) {
            useStatusAndRoundtrip = TextUtils.getValue(MasterConfig.getMasterConfig(), "_scriptMonitorUseStatusAndRoundtrip").equalsIgnoreCase("false") ? -1 : 1;
        }
        return useStatusAndRoundtrip != -1;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pParameters) {
            if (TextUtils.hasChars(s, "`;&")) {
                hashmap.put(stringproperty, "script parameters have illegal characters");
            }
        } else if (stringproperty == pExpression) {
            String s1 = TextUtils.legalMatchString(s);
            if (s1.length() > 0) {
                hashmap.put(stringproperty, s1);
            }
        } else if (stringproperty == pLocalScriptLocation) {
            String s2 = getProperty(pLocalScriptLocation);
            if (s2.length() == 0) {
                hashmap.put(stringproperty, "Specified local Scripts location must be non zero-length");
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (!getProperty(((StringProperty) (pMatchValue[0]))).equals("n/a")) {
                return "";
            }
            String s = "";
            long l = getSettingAsLong("_scriptMonitorLinesToSave", 25);
            if (l != 0L) {
                String s1 = getProperty(pScriptOutput);
                s1 = TextUtils.replaceString(s1, "^", Platform.FILE_NEWLINE);
                s = "Output of Script:\n" + s1 + "\n";
            }
            return s;
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = getStatePropertyObjectsArray();
        return array.elements();
    }

    private Array getStatePropertyObjectsArray() {
        Array array = new Array();
        addValuesPropertiesToArray(array);
        if (useStatusAndRoundtrip()) {
            array.add(pRoundTripTime);
            array.add(pStatus);
        }
        return array;
    }

    public int getCostInLicensePoints() {
        int i = getExtractedNumber(getProperty(pExpression));
        int j = 1;
        if (i > 4) {
            j = i - 3;
        }
        return j;
    }

    private void addValuesPropertiesToArray(Array array) {
        String s = getProperty(pExpression);
        if (s.length() > 0) {
            int i = 0;
            array.add(pMatchValue[i ++]);
            for (int j = getExtractedNumber(s); i < j && i < pMatchValue.length; i ++) {
                array.add(pMatchValue[i]);
            }

        }
    }

    private int getExtractedNumber(String s) {
        int i = 0;
        for (int j = 0; j < s.length(); j ++) {
            char c = s.charAt(j);
            if (c == '(' && j > 0 && s.charAt(j - 1) != '\\') {
                i ++;
            }
        }

        return i;
    }

    public boolean isMultiThreshold() {
        return true;
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty, true);
    }

    static {
        maxNumberOfMatches = 30;
        ArrayList arraylist = new ArrayList();
        pScript = new ScalarProperty("_script", "");
        if (Platform.isWindows()) {
            pScript.setDisplayText("Script", "the script from the scripts directory to run (example: scriptTest.bat), or choose <a href=\"\\SiteView\\docs\\ScriptMon.htm#script\" TARGET=Help>USE COMMAND </a>for the command file below.");
        } else {
            pScript.setDisplayText("Script", "the script from the scripts directory to run (example: scriptTest.sh),  or choose <a href=\"\\SiteView\\docs\\ScriptMon.htm#script\" TARGET=Help>USE COMMAND </a>for the command file below.");
        }
        pScript.setParameterOptions(true, 2, false);
        arraylist.add(pScript);
        pParameters = new StringProperty("_parameters", "");
        pParameters.setDisplayText("Parameters",
                "additional parameters to pass to the script. Optionally, use a <a href=/SiteView/docs/regexp.htm>regular expression</a> to insert date and time variables into the parameters <br>(e.g s/$month$ $day$ $year$/)");
        pParameters.setParameterOptions(true, 3, false);
        arraylist.add(pParameters);
        pLocalScriptLocation = new StringProperty("_localScriptLocation", "scripts");
        pLocalScriptLocation.setParameterOptions(false, true, 4, false);
        arraylist.add(pLocalScriptLocation);
        pRemoteScript = new ScalarProperty("_remotescript", "none");
        pRemoteScript.setDisplayText("USE COMMAND Script File", "The file that contains a command to be run on the remote UNIX machine.");
        pRemoteScript.setParameterOptions(true, 4, true);
        arraylist.add(pRemoteScript);
        pCacheLife = new NumericProperty("_cacheLife", "0", "seconds");
        pCacheLife.setDisplayText("Cache Life",
                "The lifetime, in seconds, of the cache, each monitor run will check if the cache life has expired, if it has not then the cache data will be used, otherwise the script will be executed to update the cache.");
        pCacheLife.setParameterOptions(true, 5, true);
        arraylist.add(pCacheLife);
        pExpression = new StringProperty("_expression");
        pExpression.setDisplayText("Match Expression", "optional Perl regular expression to match against the output of the script or the command from the command file, to extract values (example: /(\\d*) File.*([\\d,]*) bytes/).");
        pExpression.setParameterOptions(true, 6, true);
        arraylist.add(pExpression);
        pMaxMeasurement = new NumericProperty("_maxMeasurement", "0", "milleseconds");
        pMaxMeasurement.setDisplayText("Maximum for Measurement",
                "optional value to specify as maximum for gauge display in milleseconds (example: if the runtime of the script is 4 seconds and this value is set at 8 seconds (8000ms) than the gauge will show at 50%");
        pMaxMeasurement.setParameterOptions(true, 7, true);
        arraylist.add(pMaxMeasurement);
        pValueLabels = new StringProperty("_valeLabels", "");
        pValueLabels.setDisplayText("Match Value Labels", "Labels for the values matched on the script output, separated by a \",\"");
        pValueLabels.setParameterOptions(true, 3, true);
        arraylist.add(pValueLabels);
        pTimeout = new NumericProperty("_timeout", "-1", "seconds");
        pTimeout.setDisplayText("Timeout", "The total time, in seconds, to wait for a successful script run. Default value is -1 (no timeout).");
        pTimeout.setParameterOptions(false, 2, true);
        arraylist.add(pTimeout);
        int i = 1;
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(i ++);
        arraylist.add(pRoundTripTime);
        pStatus = new StringProperty("status");
        pStatus.setIsThreshold(true);
        arraylist.add(pStatus);
        pStatusLabel = new StringProperty("_statusLabel", "status");
        pStatusLabel.setParameterOptions(false, 3, true);
        arraylist.add(pStatusLabel);
        pScriptOutput = new StringProperty("scriptOutput");
        arraylist.add(pScriptOutput);
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (hashmap != null) {
            int j = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxNumScriptMatches"));
            if (j > 0) {
                maxNumberOfMatches = j;
            }
        }
        pMatchValue = new NumericProperty[maxNumberOfMatches];
        int k = TextUtils.toInt(TextUtils.getValue(hashmap, "_defaultPrecision"));
        for (int l = 0; l < pMatchValue.length; l ++) {
            String s = getNameFromIndex(l);
            pMatchValue[l] = new NumericProperty(s);
            pMatchValue[l].setStateOptions(i ++);
            pMatchValue[l].setIsThreshold(true);
            if (k > 0) {
                pMatchValue[l].defaultPrecision = k;
            }
            arraylist.add(pMatchValue[l]);
        }

        StringProperty astringproperty[] = new StringProperty[arraylist.size()];
        for (int i1 = 0; i1 < arraylist.size(); i1 ++) {
            astringproperty[i1] = (StringProperty) arraylist.get(i1);
        }

        String s1 = (COM.dragonflow.StandardMonitor.ScriptMonitor.class).getName();
        addProperties(s1, astringproperty);
        addClassElement(s1, Rule.stringToClassifier("status != 0\terror"));
        addClassElement(s1, Rule.stringToClassifier("status == 0\tgood"));
        setClassProperty(s1, "description", "Verifies that a script or batch file can be run. Can also be used to automatically run scripts.");
        setClassProperty(s1, "help", "ScriptMon.htm");
        setClassProperty(s1, "title", "Script");
        setClassProperty(s1, "class", "ScriptMonitor");
        setClassProperty(s1, "target", "_script");
        setClassProperty(s1, "classType", "advanced");
        setClassProperty(s1, "loadable", "true");
        setClassProperty(s1, "topazName", "Script");
        setClassProperty(s1, "topazType", "System Resources");
    }
}
