/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MirrorConfiguration.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MirrorConfiguration</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.ApacheHttpClientUtils.ApacheHttpMethod;
import COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils;
import COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.mirrorPrefsPage;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, AtomicMonitor, Platform, SiteViewGroup,
// MasterConfig, Monitor, TopazInfo

public class MirrorConfiguration extends Action {

    private static final String MIRROR_MODE_SETTING = "mirrorMode";

    public static final String HA_INSTALL_SETTING = "_haInstall";

    public static String failOver = null;

    private static HashMap mirrorConfig = null;

    private static String mirrorVersion = "1.0";

    private static boolean debug = false;

    public static final String masterConfigExcludes[] = { "_httpPort",
            "_httpSecurePort", "_license", "_adminURL", "_httpListenerIP",
            "_userURL", "_webserverAddress", "_httpActivePort",
            "_httpsActivePort", "_httpSecurePort" };

    public long status;

    PrintWriter traceStream;

    PrintStream mainTrace;

    private static Boolean isHAVersion = null;

    private static Boolean isHAinControl = null;

    private static final boolean notBinary = false;

    static String MIRROR_CONFIG_FILENAME = "mirror.config";

    static String START_TAG = "<HTML>";

    static String END_TAG = "</HTML>";

    static int byteCount = 0;

    private static String copyTest = System.getProperty("CopyTest.UT", "false");

    public MirrorConfiguration() {
        this(getMirrorConfig(), null);
    }

    public MirrorConfiguration(HashMap hashmap) {
        status = URLMonitor.kURLok;
        traceStream = null;
        mainTrace = null;
        runType = 1;
        if (hashmap != null) {
            mirrorConfig = (HashMap) hashmap.clone();
        }
        traceStream = null;
        mainTrace = System.out;
    }

    public MirrorConfiguration(HashMap hashmap, PrintWriter printwriter) {
        status = URLMonitor.kURLok;
        traceStream = null;
        mainTrace = null;
        runType = 1;
        if (hashmap != null) {
            mirrorConfig = (HashMap) hashmap.clone();
        }
        traceStream = printwriter;
    }

    private static String fixPath(String s) {
        String s1 = new String(s);
        String s2 = s1.replace('\\', '/');
        if (Platform.isWindows()) {
            return TextUtils.replaceString(s2, "/", "\\\\");
        } else {
            return s2;
        }
    }

    public static HashMap getMirrorConfig() {
        if (mirrorConfig == null || mirrorConfig.size() == 0) {
            mirrorConfig = new HashMapOrdered(true);
            String s = fixPath(Platform.getRoot() + "/groups/"
                    + MIRROR_CONFIG_FILENAME);
            try {
                Array array = FrameFile.readFromFile(s);
                if (array.size() > 0) {
                    mirrorConfig = (HashMap) array.at(0);
                }
                isHAVersion = Boolean.TRUE;
            } catch (Exception exception) {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                exception
                        .printStackTrace(new PrintStream(bytearrayoutputstream));
                LogManager.log("Error", "Could not read MirrorConfig:  from "
                        + s + ": " + exception.getMessage());
                MailUtils
                        .sendEmailWarning(
                                "Fatal ERROR! High Availability Failover is not working!",
                                "Your High Availability SiteView Server has lost its configuration file. \nMirror.Config: "
                                        + s
                                        + ": "
                                        + exception.getMessage()
                                        + "\n"
                                        + bytearrayoutputstream.toString()
                                        + "\n"
                                        + "Please re-enter your High Availability perferences.\n"
                                        + "In a browser, on the HA server, go to Perferences/High Availability and re-enter the fields");
                isHAVersion = Boolean.FALSE;
            }
        }
        return mirrorConfig;
    }

    public static void saveMirrorConfig(HashMap hashmap) {
        if (hashmap == null) {
            return;
        }
        HashMap hashmap1 = getMirrorConfig();
        Enumeration enumeration = hashmap.keys();
        String s;
        Object obj;
        for (Enumeration enumeration1 = hashmap.elements(); enumeration
                .hasMoreElements()
                && enumeration1.hasMoreElements(); hashmap1.put(s, obj)) {
            s = (String) enumeration.nextElement();
            obj = enumeration1.nextElement();
        }

        String s1 = fixPath(Platform.getRoot() + "/groups/"
                + MIRROR_CONFIG_FILENAME);
        try {
            FrameFile.writeToFile(s1, hashmap1);
            isHAVersion = Boolean.TRUE;
        } catch (Exception exception) {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(bytearrayoutputstream));
            LogManager.log("Error", "Could not write " + MIRROR_CONFIG_FILENAME
                    + " to " + s1 + ": " + exception.getMessage());
            MailUtils
                    .sendEmailWarning(
                            "Fatal ERROR! High Availability Failover is not working!",
                            "Your High Availability SiteView Server Cannot write to its configuration file. \nMirror.Config: "
                                    + s1
                                    + ": "
                                    + exception.getMessage()
                                    + "\n"
                                    + bytearrayoutputstream.toString()
                                    + "\n"
                                    + "Please delete SiteView\\groups\\mirror.config\n"
                                    + "Then re-enter your High Availability perferences.\n"
                                    + "In a browser, on the HA server, go to Perferences/High Availability and re-enter the fields");
            isHAVersion = Boolean.FALSE;
        }
        mirrorConfig = hashmap1;
    }

    public static boolean isHAInControl() {
        if (isHAinControl == null) {
            HashMap hashmap = getMirrorConfig();
            String s = TextUtils.getValue(hashmap, "mirrorMode");
            if (s.equalsIgnoreCase("failover")) {
                isHAinControl = Boolean.TRUE;
            } else {
                isHAinControl = Boolean.FALSE;
            }
        }
        return isHAinControl.booleanValue();
    }

    private static boolean isHAVersion() {
        if (isHAVersion == null) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            isHAVersion = new Boolean(siteviewgroup.getSetting("_haInstall")
                    .equals("true"));
        }
        return isHAVersion.booleanValue();
    }

    public static void setMirrorStateToHAInControl(boolean flag) {
        String s = "mirror";
        if (flag) {
            s = "failover";
            isHAinControl = Boolean.TRUE;
        } else {
            isHAinControl = Boolean.FALSE;
        }
        getMirrorConfig();
        if (mirrorConfig.size() > 0) {
            mirrorConfig.put("mirrorMode", s);
            saveMirrorConfig(mirrorConfig);
            LogManager.log("Mirror", "Switching mode to " + s);
        }
    }

    public static long getRestartDelay() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("_restartDelay", 24);
        if (!isHAVersion()) {
            return l;
        }
        if (!isHAInControl()) {
            l = getMirrorSchedule();
        } else {
            l = 99L;
        }
        return l;
    }

    public static long getMirrorSchedule() {
        long l = 24L;
        String s = "";
        if (mirrorConfig != null) {
            s = TextUtils.getValue(mirrorConfig, "mirrorSchedule");
        }
        String as[] = TextUtils.split(s, "\t ");
        if (as.length > 0) {
            l = TextUtils.toInt(as[as.length - 1]);
            if (l < 1L) {
                l = 1L;
            }
            if (l > 48L) {
                l = 48L;
            }
        }
        return l;
    }

    public static void createFailoverMG(mirrorPrefsPage mirrorprefspage) {
        int i = 1;
        byte byte0 = 2;
        byte byte1 = 3;
        String s = mirrorprefspage.request.getValue("isSecure").length() <= 0 ? "http://"
                : "https://";
        String s1 = mirrorprefspage.request.getValue("server");
        int j = s1.toLowerCase().indexOf("//");
        if (j < 0) {
            j = s1.toLowerCase().indexOf("\\\\");
        }
        if (j >= 0) {
            s1 = s1.substring(j + 2);
        }
        Array array = new Array();
        HashMap hashmap = new HashMap();
        hashmap.put("_name", "config");
        hashmap.put("_nextID", "4");
        hashmap.put("_dependsCondition", "good");
        array.add(hashmap);
        HashMap hashmap1 = new HashMap();
        hashmap1.put("_id", i + "");
        hashmap1.put("_internalId", i + "");
        hashmap1.put("_class", "URLMonitor");
        hashmap1.put("_name", "Fail Over");
        hashmap1.put("_url", s + s1 + "/SiteView/htdocs/Progress.html");
        hashmap1.put("_password", StringProperty.getPrivate(
                mirrorprefspage.request, "password", "mirrorSuff", null, null));
        hashmap1.put("_username", mirrorprefspage.request.getValue("username"));
        hashmap1.put("_frequency", "600");
        hashmap1.put("_timeout", "300");
        hashmap1.put("_dependsCondition", "good");
        hashmap1.put("_notLogToTopaz", "on");
        Array array1 = new Array();
        array1
                .add("category eq 'error' and errorCount == 1\tdisable enable Failover/"
                        + byte0 + "\t3");
        array1
                .add("category eq 'good' and goodCount == 1 and errorCount >= 1\tdisable enable Failover/"
                        + byte1 + "\t4");
        hashmap1.put("_alertCondition", array1);
        array.add(hashmap1);
        String s2 = SiteViewGroup.cCurrentSiteView
                .getSetting("_httpActivePort");
        String s3 = "http://localhost:" + s2;
        array.add(create_RestartTopazMonitor(s3, byte0));
        array.add(create_ResetTopaz_CopyConfig_Monitor(s3, byte1));
        try {
            FrameFile.writeToFile(fixPath(Platform.getRoot()
                    + "/groups/Failover.mg"), array);
        } catch (Exception exception) {
            LogManager.log("Error", "Could not init/write "
                    + Platform.getRoot() + "/groups/Failover.mg" + ": "
                    + exception.getMessage());
            MailUtils.sendEmailWarning(
                    "Fatal ERROR! High Availability Failover is not working!",
                    "Your High Availability SiteView Server Cannot write to the "
                            + Platform.getRoot() + "/groups/Failover.mg"
                            + " file. \n" + "Please delete "
                            + Platform.getRoot() + "/groups/Failover.mg" + "\n"
                            + "Then restart the HA service.\n");
            isHAVersion = Boolean.FALSE;
        }
        SiteViewGroup.updateStaticPages("Failover");
    }

    public static void createMirrorConfig() {
        HashMap hashmap = null;
        String s = fixPath(Platform.getRoot() + "/groups/Failover.mg");
        File file = new File(s);
        if (file.exists()) {
            try {
                Array array = FrameFile.readFromFile(s);
                if (array.size() > 1) {
                    hashmap = (HashMap) array.at(1);
                    String s1 = TextUtils.getValue(hashmap, "_password");
                    String s2 = TextUtils.getValue(hashmap, "_username");
                    String s3 = TextUtils.getValue(hashmap, "_url");
                    String as[] = TextUtils.split(s3, "/");
                    if (as.length > 1) {
                        String s4 = as[0].startsWith("https") ? "true" : "";
                        String s5 = as[1];
                        setMirrorConfig("4", s5, s2, s1, s4);
                    }
                }
            } catch (Exception exception) {
            }
        }
        if (hashmap == null) {
            MailUtils
                    .sendEmailWarning(
                            "Fatal ERROR! High Availability Failover is not working!",
                            "Your High Availability SiteView Server Cannot find the "
                                    + Platform.getRoot()
                                    + "/groups/Failover.mg"
                                    + " file. \n"
                                    + "Please use your browser and go to preferences/High Availability and re-enter the fields");
        }
    }

    public static void setMirrorConfig(mirrorPrefsPage mirrorprefspage) {
        String s = "" + mirrorprefspage.request.getValue("mirrorScheduleTime");
        String s1 = mirrorprefspage.request.getValue("server");
        int i = s1.toLowerCase().indexOf("//");
        if (i < 0) {
            i = s1.toLowerCase().indexOf("\\\\");
        }
        if (i >= 0) {
            s1 = s1.substring(i + 2);
        }
        String s2 = mirrorprefspage.request.getValue("username");
        String s3 = StringProperty.getPrivate(mirrorprefspage.request,
                "password", "mirrorSuff", null, null);
        String s4 = mirrorprefspage.request.getValue("isSecure");
        if (s4.equalsIgnoreCase("on")) {
            s4 = "CHECKED";
        } else if (s4.length() == 0) {
            s4 = "";
        }
        setMirrorConfig(s, s1, s2, s3, s4);
    }

    private static void setMirrorConfig(String s, String s1, String s2,
            String s3, String s4) {
        String s5 = s;
        String s6 = "error";
        String s7 = "Failover/1";
        mirrorConfig = null;
        mirrorConfig = getMirrorConfig();
        mirrorConfig.put("_version", mirrorVersion);
        mirrorConfig.put("_globalDependsOn", s7);
        mirrorConfig.put("_globalDependsCondition", s6);
        mirrorConfig.put("mirrorMode", "mirror");
        mirrorConfig.put("mirrorSchedule", s5);
        mirrorConfig.put("server", s1);
        mirrorConfig.put("username", s2);
        mirrorConfig.put("password", s3);
        mirrorConfig.put("isSecure", s4);
        Array array = (Array) mirrorConfig.get("masterExclusion");
        for (int i = 0; i < masterConfigExcludes.length; i++) {
            if (array == null || !array.contains(masterConfigExcludes[i])) {
                mirrorConfig.add("masterExclusion", masterConfigExcludes[i]);
            }
        }

        saveMirrorConfig(mirrorConfig);
    }

    public boolean execute() {
        return execute(false);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param flag
     * @return
     */
    public boolean execute(boolean flag) {
        status = URLMonitor.kURLok;
        getMirrorConfig();
        String s = TextUtils.getValue(mirrorConfig, "_version");
        if (mirrorConfig.size() == 0 || !s.equals(mirrorVersion)) {
            createMirrorConfig();
        }
        if (TextUtils.getValue(mirrorConfig, "mirrorMode").equalsIgnoreCase(
                "failover")) {
            return true;
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Object obj = null;
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        Enumeration enumeration = mirrorConfig.values("masterExclusion");
        if (enumeration.hasMoreElements()) {
            Object obj1 = enumeration.nextElement();
            if (obj1 instanceof Array) {
                Array array = (Array) obj1;
                for (int i = 0; array != null && i < array.size(); i++) {
                    String s5 = (String) array.at(i);
                    String s6 = siteviewgroup.getSetting(s5);
                    hashmapordered.put(s5, s6);
                }

            } else if (obj1 instanceof String) {
                do {
                    String s1 = (String) obj1;
                    String s3 = siteviewgroup.getSetting(s1);
                    hashmapordered.put(s1, s3);
                    if (enumeration.hasMoreElements()) {
                        obj1 = enumeration.nextElement();
                    }
                } while (enumeration.hasMoreElements());
            }
        }
        if (hashmapordered.size() == 0) {
            LogManager.log("Error",
                    "MirrorConfig corrupt: cannot find masterExclusions");
        }
        String s2 = String.valueOf(getMirrorSchedule());
        String s4 = TextUtils.getValue(mirrorConfig, "server");
        LogManager.log("RunMonitor", "Mirroring configuration started from: "
                + s4);
        LogManager.log("Mirror", "Mirroring configuration started from: " + s4);
        HashMap hashmap = new HashMap();
        Object obj2 = mirrorConfig.get("fileExclusion");
        if (obj2 instanceof Array) {
            Array array1 = (Array) obj2;
            for (int j = 0; array1 != null && j < array1.size(); j++) {
                String s9 = (String) array1.at(j);
                hashmap.put(s9.toUpperCase(), new Boolean("false"));
            }

        } else if (obj2 instanceof String) {
            String s7 = (String) obj2;
            hashmap.put(s7.toUpperCase(), new Boolean("false"));
        }
        hashmap.put("MIRROR.CONFIG", new Boolean("true"));
        hashmap.put("MONPID", new Boolean("true"));
        hashmap.put("PID", new Boolean("true"));
        hashmap.put("*.DYN", new Boolean("true"));
        hashmap.put("*.BAK", new Boolean("true"));
        hashmap.put("SERVERKEYSTORE", new Boolean("true"));
        String s8 = TextUtils.getValue(mirrorConfig, "_globalDependsOn");
        int k = s8.indexOf("/");
        if (k >= 0) {
            s8 = s8.substring(0, k);
        }
        if (s8.length() > 0) {
            failOver = s8 + ".mg";
            hashmap.put(failOver.toUpperCase(), new Boolean("true"));
        }
        HashMap hashmap1 = new HashMap();
        Object obj3 = mirrorConfig.get("fileExclusionRegex");
        if (obj3 instanceof Array) {
            Array array2 = (Array) obj3;
            for (int l = 0; array2 != null && l < array2.size(); l++) {
                String s12 = (String) array2.at(l);
                hashmap1.put(s12.toUpperCase(), new Boolean("false"));
            }

        } else if (obj3 instanceof String) {
            String s10 = (String) obj3;
            hashmap1.put(s10.toUpperCase(), new Boolean("false"));
        }
        printProgress("Syncing configurations with SiteView at "
                + TextUtils.getValue(mirrorConfig, "server"));
        status = copyFilesToTmp(mirrorConfig, hashmap, hashmap1);
        if (status == (long) URLMonitor.kURLok) {
            tmpToPermFileCopy(mirrorConfig, hashmap, hashmap1);
            MasterConfig.clearConfigCache();
            siteviewgroup.loadSettings();
            String s11;
            String s13;
            for (Enumeration enumeration1 = hashmapordered.keys(); enumeration1
                    .hasMoreElements(); siteviewgroup.setProperty(s11, s13)) {
                s11 = (String) enumeration1.nextElement();
                s13 = (String) hashmapordered.get(s11);
            }

            if (!flag) {
                siteviewgroup.setProperty("_globalDependsOn", TextUtils
                        .getValue(mirrorConfig, "_globalDependsOn"));
                siteviewgroup.setProperty("_globalDependsCondition", TextUtils
                        .getValue(mirrorConfig, "_globalDependsCondition"));
                siteviewgroup.setProperty("_restartDelay", s2);
                if (LUtils
                        .getLicenseType(siteviewgroup.getSetting("_license")) == -1) {
                    siteviewgroup.setProperty("_licenseOld", siteviewgroup
                            .getSetting("_license"));
                    siteviewgroup.setProperty("_installTime", ""
                            + TextUtils.timeSeconds());
                    siteviewgroup.setProperty("_installed", ""
                            + TextUtils.prettyDate());
                    siteviewgroup.setProperty("_license", "");
                }
                siteviewgroup.setProperty("_haInstall", "true");
                siteviewgroup.setProperty("_onlyLogEnabledMonitors", "true");
                Enumeration enumeration2 = siteviewgroup.getMonitors();
                while (enumeration2.hasMoreElements()) {
                    Object obj4 = enumeration2.nextElement();
                    if (obj4 instanceof AtomicMonitor) {
                        AtomicMonitor atomicmonitor = (AtomicMonitor) obj4;
                        if (atomicmonitor.isDispatcher()) {
                            atomicmonitor
                                    .setProperty(Monitor.pDisabled, "true");
                        }
                    }
                }
            }
            siteviewgroup.saveSettings();
            LogManager.log("Mirror", "Mirroring configuration completed " + s4);
            LogManager.log("Error", "Mirroring configuration completed " + s4);
            printProgress("Retrieval complete");
        } else {
            LogManager.log("Error", "error contacting mirror server " + s4
                    + ": " + status);
            LogManager.log("RunMonitor", "error contacting mirror server " + s4
                    + ": " + status);
            LogManager.log("Mirror", "error contacting mirror server " + s4
                    + ": " + status);
            printProgress("*****Retrieval INCOMPLETE! Reverting to older version");
        }
        return true;
    }

    private boolean copyGroupsOnly() {
        status = URLMonitor.kURLok;
        getMirrorConfig();
        String s = TextUtils.getValue(mirrorConfig, "_version");
        if (mirrorConfig.size() == 0 || !s.equals(mirrorVersion)) {
            System.out.println("Cannot find configuration");
            return false;
        }
        HashMap hashmap = new HashMap();
        hashmap.put("MIRROR.CONFIG", "true");
        hashmap.put("MONPID", "true");
        hashmap.put("PID", "true");
        hashmap.put("*.DYN", "true");
        hashmap.put("*.BAK", "true");
        hashmap.put("SERVERKEYSTORE", "true");
        HashMap hashmap1 = new HashMap();
        hashmap.put("*.BAK.*", new Boolean("true"));
        System.out.println("Syncing configurations with SiteView at "
                + TextUtils.getValue(mirrorConfig, "server"));
        status = copyDirectory(mirrorConfig, "/groups/",
                "monitors and preferences", hashmap, hashmap1, false);
        if (status == 200L) {
            if (status != 200L) {
                System.out
                        .println("*****Retrieval INCOMPLETE! Reverting to older version");
                return false;
            }
        } else {
            System.out
                    .println("*****Retrieval INCOMPLETE! Reverting to older version");
            return false;
        }
//        if (reportToTopaz()) {
//            hashmap.put("HISTORY", "true");
//            status = copyDirectory(mirrorConfig,
//                    "/cache/persistent/ConfigurationManager/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap, hashmap1, true);
//            if (debug && status != 200L) {
//                System.out.println("*****Syncing " + TopazInfo.getTopazName()
//                        + " repositories 1 failed");
//                return false;
//            }
//            status = copyDirectory(mirrorConfig,
//                    "/cache/persistent/TopazConfiguration/current/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap, hashmap1, true);
//            if (debug && status != 200L) {
//                System.out.println("*****Syncing " + TopazInfo.getTopazName()
//                        + " repositories 2 failed");
//                return false;
//            }
//        }
//        System.out.println("Retrieval complete");
        return true;
    }

    private long copyFilesToTmp(HashMap hashmap, HashMap hashmap1,
            HashMap hashmap2) {
        HashMap hashmap3 = new HashMap();
        hashmap3.put("BROWSABLE", new Boolean("true"));
        long l = copyDirectory(hashmap, "/groups/", "monitors and preferences",
                hashmap1, hashmap2, false);
        if (l == 200L && hashmap.get("groupsOnly") == null) {
            l = copyDirectory(hashmap, "/templates.eventlog/",
                    "Event Log Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.history/",
                    "Report Mail Templates", false);
            l = copyDirectory(hashmap, "/templates.historyGraphics/",
                    "Report Graphics", false);
            l = copyDirectory(hashmap, "/templates.mail/",
                    "E-mail Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.os/",
                    "Unix Remote Server Adaptors", false);
            l = copyDirectory(hashmap, "/templates.page/",
                    "Pager Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.perfmon/",
                    "Performance Counter Monitor Settings", hashmap3,
                    new HashMap(), false);
            l = copyDirectory(hashmap, "/templates.perfmon/browsable/",
                    "Browsable Performance Counter Monitor Settings", false);
            l = copyDirectory(hashmap, "/templates.post/",
                    "POST Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.certificates/",
                    "Installed Certificates", false);
            l = copyDirectory(hashmap, "/templates.script/",
                    "Script Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.snmp/",
                    "SNMP Alert Templates", false);
            l = copyDirectory(hashmap, "/templates.sound/",
                    "Sound Alert Sounds", false);
            l = copyDirectory(hashmap, "/templates.wsdl/", "WSDL Files", false);
            l = copyDirectory(hashmap, "/scripts/",
                    "Script Alert and Script Monitor Scripts", false);
            l = copyDirectory(hashmap, "/scripts.remote/", "Remote scripts",
                    false);
        }
//        if (reportToTopaz()) {
//            long l1 = copyDirectory(hashmap,
//                    "/cache/persistent/ConfigurationManager/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
//            if (debug && l1 != 200L) {
//                error("*****Syncing "
//                        + TopazInfo.getTopazName()
//                        + " repositories in /cache/persistent/ConfigurationManager failed ("
//                        + l1 + ") - ignore if primary is not reporting to "
//                        + TopazInfo.getTopazName());
//            }
            hashmap1.put("HISTORY", new Boolean("true"));
            hashmap1.put("CURRENT", new Boolean("true"));
//            l1 = copyDirectory(hashmap,
//                    "/cache/persistent/TopazConfiguration/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
            hashmap1.remove("CURRENT");
            hashmap1.remove("HISTORY");
//            if (debug && l1 != 200L) {
//                error("*****Syncing "
//                        + TopazInfo.getTopazName()
//                        + " repositories in /cache/persistent/TopazConfiguration/current failed ("
//                        + l1 + ") - ignore if primary is not reporting to "
//                        + TopazInfo.getTopazName());
//            }
//            l1 = copyDirectory(hashmap,
//                    "/cache/persistent/TopazConfiguration/current/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
//            if (debug && l1 != 200L) {
//                error("*****Syncing "
//                        + TopazInfo.getTopazName()
//                        + " repositories in /cache/persistent/TopazConfiguration failed ("
//                        + l1 + ") - ignore if primary is not reporting to "
//                        + TopazInfo.getTopazName());
//            }
//        }
        return l;
    }

    private long tmpToPermFileCopy(HashMap hashmap, HashMap hashmap1,
            HashMap hashmap2) {
        long l = copyToPermDirectory(hashmap, "/groups/",
                "monitors and preferences", hashmap1, hashmap2, false);
        if (l == 200L && hashmap.get("groupsOnly") == null) {
            l = copyToPermDirectory(hashmap, "/templates.eventlog/",
                    "Event Log Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.history/",
                    "Report Mail Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.historyGraphics/",
                    "Report Graphics", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.mail/",
                    "E-mail Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.os/",
                    "Unix Remote Server Adaptors", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.page/",
                    "Pager Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.perfmon/",
                    "Performance Counter Monitor Settings", hashmap1, hashmap2,
                    false);
            l = copyToPermDirectory(hashmap, "/templates.perfmon/browsable/",
                    "Browsable Performance Counter Monitor Settings", hashmap1,
                    hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.post/",
                    "POST Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.certificates/",
                    "Installed Certificates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.script/",
                    "Script Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.snmp/",
                    "SNMP Alert Templates", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.sound/",
                    "Sound Alert Sounds", hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/templates.wsdl/", "WSDL Files",
                    hashmap1, hashmap2, false);
            l = copyToPermDirectory(hashmap, "/scripts/",
                    "Script Alert and Script Monitor Scripts", hashmap1,
                    hashmap2, false);
            l = copyToPermDirectory(hashmap, "/scripts.remote/",
                    "Remote scripts", hashmap1, hashmap2, false);
        }
//        if (reportToTopaz()) {
//            l = copyToPermDirectory(hashmap,
//                    "/cache/persistent/ConfigurationManager/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
//            if (debug && l != 200L) {
//                error("*****Copying "
//                        + TopazInfo.getTopazName()
//                        + " repositories to perm, in /cache/persistent/ConfigurationManager failed");
//                return l;
//            }
            hashmap1.put("HISTORY", new Boolean("true"));
            hashmap1.put("CURRENT", new Boolean("true"));
//            l = copyToPermDirectory(hashmap,
//                    "/cache/persistent/TopazConfiguration/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
            hashmap1.remove("CURRENT");
            hashmap1.remove("HISTORY");
//            if (debug && l != 200L) {
//                error("*****Copying "
//                        + TopazInfo.getTopazName()
//                        + " repositories to perm, in /cache/persistent/TopazConfiguration/ failed");
//                return l;
//            }
//            l = copyToPermDirectory(hashmap,
//                    "/cache/persistent/TopazConfiguration/current/", TopazInfo
//                            .getTopazName()
//                            + " repositories", hashmap1, hashmap2, true);
//            if (debug && l != 200L) {
//                error("*****Copying "
//                        + TopazInfo.getTopazName()
//                        + " repositories to perm, in /cache/persistent/TopazConfiguration/current/ failed");
//            }
//        }
        return l;
    }

    private void setDirWritable(String s) {
        String s1 = "attrib -R " + s + "*.*";
        if (!Platform.isWindows()) {
            s1 = "chmod u-r *";
        }
        CommandLine commandline = new CommandLine();
        commandline.exec(s1);
    }

    private void cleanDirectory(HashMap hashmap, String s, HashMap hashmap1,
            HashMap hashmap2) {
        File file = new File(s);
        if (file.exists()) {
            setDirWritable(s);
            String as[] = file.list();
            for (int i = 0; i < as.length; i++) {
                Exception exception = null;
                if (excludeFile(as[i], hashmap1, true)
                        || excludeFileRegEx(as[i], hashmap2, true)) {
                    continue;
                }
                File file1 = new File(s, as[i]);
                if (!file1.exists()) {
                    continue;
                }
                try {
                    file1.delete();
                } catch (Exception exception1) {
                    exception = exception1;
                }
                if (file1.exists()) {
                    String s1 = "File Still found";
                    if (exception != null) {
                        s1 = exception.toString();
                    }
                    LogManager.log("Mirror", "error deleting "
                            + file1.getAbsolutePath() + s1);
                } else {
                    LogManager.log("Mirror", "deleted "
                            + file1.getAbsolutePath());
                }
            }

        }
    }

    boolean excludeFile(String s, HashMap hashmap, boolean flag) {
        if (hashmap.get(s.toUpperCase()) != null) {
            if (flag) {
                return ((Boolean) hashmap.get(s.toUpperCase())).booleanValue();
            } else {
                return true;
            }
        }
        if (s.lastIndexOf(".") > 0) {
            String s1 = "*" + s.substring(s.lastIndexOf("."));
            if (hashmap.get(s1.toUpperCase()) != null) {
                if (flag) {
                    return ((Boolean) hashmap.get(s1.toUpperCase()))
                            .booleanValue();
                } else {
                    return true;
                }
            }
        }
        return excludeItem(s, "File");
    }

    boolean excludeFileRegEx(String s, HashMap hashmap, boolean flag) {
        String s1 = s.toUpperCase();
        for (Enumeration enumeration = hashmap.keys(); enumeration
                .hasMoreElements();) {
            String s2 = (String) enumeration.nextElement();
            Array array = new Array();
            TextUtils.matchExpression(s1, s2, array);
            if (array.size() > 0) {
                return !flag;
            }
        }

        return false;
    }

    boolean excludeDirectory(String s) {
        return excludeItem(s, "Directory");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @return
     */
    boolean excludeItem(String s, String s1) {
        //TODO need review
        label0: {
            String s2 = "mirrorExclude" + s1;
            if (mirrorConfig.get(s2) == null) {
                break label0;
            }
            Enumeration enumeration = mirrorConfig.values(s2);
            String s3;
            do {
                if (!enumeration.hasMoreElements()) {
                    break label0;
                }
                s3 = (String) enumeration.nextElement();
            } while (!TextUtils.match(s, s3));
            return true;
        }
        return false;
    }

    private String getDestinationPath(HashMap hashmap) {
        String s = TextUtils.getValue(hashmap, "target");
        if (s.length() > 0) {
            return s;
        } else {
            return Platform.getRoot() + "/tmp";
        }
    }

    private void logError(String s) {
        LogManager.log("Error", s);
        LogManager.log("RunMonitor", s);
        LogManager.log("Mirror", s);
    }

    private long copyDirectory(HashMap hashmap, String s, String s1,
            boolean flag) {
        return copyDirectory(hashmap, s, s1, new HashMap(), new HashMap(), flag);
    }

    private long copyDirectory(HashMap hashmap, String s, String s1,
            HashMap hashmap1, HashMap hashmap2, boolean flag) {
        long l = 200L;
        if (excludeDirectory(s)) {
            return l;
        }
        LogManager.log("Mirror", "Copying (" + s1 + ") directory " + s);
        printProgress("Syncing " + s1);
        StringBuffer stringbuffer = new StringBuffer();
        l = getContent(hashmap, s, "list", stringbuffer);
        if (l != 200L) {
            return l;
        }
        String s2 = stringbuffer.toString().trim();
        if (s2.startsWith("file load error") || s2.startsWith("error:")) {
            return (long) URLMonitor.kMonitorSpecificError;
        }
        String as[] = TextUtils.split(s2);
        String s3 = fixPath(getDestinationPath(mirrorConfig) + s).trim();
        File file = new File(s3);
        if (!file.exists()) {
            file.mkdirs();
        }
        cleanDirectory(hashmap, s3, new HashMap(), new HashMap());
        for (int i = 0; i < as.length; i++) {
            if (excludeFile(as[i], hashmap1, false)
                    || excludeFileRegEx(as[i], hashmap2, false)) {
                continue;
            }
            String s4 = (s3 + as[i]).trim();
            String s5 = (s + as[i]).trim();
            StringBuffer stringbuffer1 = new StringBuffer();
            if (flag) {
                l = getBinaryContentToFile(hashmap, s5, s4);
                if (debug && l != 200L) {
                    error("Info - failed retrieving TopazConfig binary file "
                            + s5 + " (status=" + l + ")");
                }
                continue;
            }
            l = getContent(hashmap, s5, "get", stringbuffer1);
            if (l == 200L) {
                try {
                    String s6 = stringbuffer1.toString().trim();
                    FileUtils.writeFile(s4, s6);
                    LogManager.log("Mirror", "wrote " + s4);
                    continue;
                } catch (IOException ioexception) {
                    printProgress("Error writing file " + s4);
                    logError("error writing mirror file " + s4 + ": "
                            + ioexception.getMessage());
                }
            } else {
                printProgress("Error retrieving file "
                        + URLMonitor.lookupStatus(l));
                logError("error retrieving mirror file: " + l);
            }
        }

        return l;
    }

    private long copyToPermDirectory(HashMap hashmap, String s, String s1,
            HashMap hashmap1, HashMap hashmap2, boolean flag) {
        long l = 200L;
        String s2 = "beginning";
        if (excludeDirectory(s)) {
            return l;
        }
        String s3 = fixPath(getDestinationPath(mirrorConfig) + s).trim();
        File file = new File(s3);
        if (!file.exists()) {
            return l;
        }
        String as[] = file.list();
        String s4 = fixPath(Platform.getRoot() + s).trim();
        File file1 = new File(s4);
        if (!file1.exists()) {
            file1.mkdir();
        }
        cleanDirectory(hashmap, s4, hashmap1, hashmap2);
        for (int i = 0; i < as.length;i++) {
            String s5 = s3 + as[i].trim();
            File file2 = new File(s5);
            if (!file2.exists() || file2.isDirectory()) {
                continue;
            }
            String s6 = s4 + as[i].trim();
            try {
                if (flag) {
                    FileUtils.copyBinaryFile(s5, s6);
                } else {
                    s2 = "reading";
                    StringBuffer stringbuffer = FileUtils.readFile(s5);
                    s2 = "writing";
                    FileUtils.writeFile(s6, stringbuffer.toString().trim());
                }
                LogManager.log("Mirror", "wrote " + s + as[i].trim());
                continue;
            } catch (IOException ioexception) {
                if (s2.equals("beginning")) {
                    continue;
                }
                if (s2.equals("reading")) {
                    printProgress("Error reading file " + s5);
                    logError("error reading mirror file " + s5 + ": "
                            + ioexception.getMessage());
                } else {
                    printProgress("Error writing file " + s6);
                    logError("error writing mirror file " + s6 + ": "
                            + ioexception.getMessage());
                }
                //i++; dingbing.xu
            }
        }

        return l;
    }

    private long getContent(HashMap hashmap, String s, String s1,
            StringBuffer stringbuffer) {
        if (hashmap.size() == 0) {
            return 0L;
        }
        HashMap hashmap1 = MasterConfig.getMasterConfig();
        String s2 = TextUtils.getValue(hashmap, "server");
        String s3 = TextUtils.getValue(hashmap, "isSecure");
        String s4 = "";
        if (s2.indexOf("://") == -1) {
            s4 = s3.length() <= 0 ? "http://" : "https://";
        }
        String s5 = TextUtils.getValue(hashmap, "loginAccount");
        String s6 = s4 + s2 + "/SiteView/cgi/go.exe/SiteView"
                + "?page=file&account=" + s5 + "&file=" + s + "&operation="
                + s1;
        String s7 = "";
        String s8 = TextUtils.getValue(hashmap, "proxy");
        String s9 = TextUtils.getValue(hashmap, "proxyusername");
        String s10 = TextUtils.getValue(hashmap, "proxypassword");
        String s11 = TextUtils.getValue(hashmap, "username");
        String s12 = TextUtils.getValue(hashmap, "password");
        String s13 = TextUtils.getValue(hashmap, "timeout");
        int i = 60000;
        if (s13.length() != 0) {
            i = TextUtils.toInt(s13) * 1000;
        }
        if (s11.length() == 0) {
            s11 = TextUtils.getValue(hashmap1, "_adminUsername");
            s12 = TextUtils.getValue(hashmap1, "_adminPassword");
        }
        long l = -1L;
        if (TextUtils.getValue(hashmap1, "_overviewMaxData").length() > 0) {
            l = TextUtils.toLong(TextUtils.getValue(hashmap1,
                    "_overviewMaxData"));
        }
        if (l < 1L) {
            l = 0x7a120L;
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        Array array = null;
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, s6, s7, "", s8, s9, s10,
                array, s11, s12, "", stringbuffer1, l, "", 0, i, null);
        socketsession.close();
        stringbuffer.setLength(0);
        if (al[0] == 200L) {
            String s14 = URLMonitor.getHTTPContent(stringbuffer1.toString())
                    .trim();
            if (s14.startsWith(START_TAG)) {
                s14 = s14.substring(START_TAG.length()).trim();
            }
            if (s14.endsWith(END_TAG)) {
                int j = s14.lastIndexOf(END_TAG);
                if (j >= 0) {
                    s14 = s14.substring(0, j).trim();
                }
            }
            stringbuffer.append(s14);
        }
        return al[0];
    }

    private long getBinaryContentToFile(HashMap hashmap, String s, String s1) {
        if (hashmap.size() == 0) {
            return 0L;
        }
        HashMap hashmap1 = MasterConfig.getMasterConfig();
        String s2 = TextUtils.getValue(hashmap, "server");
        String s3 = TextUtils.getValue(hashmap, "isSecure");
        String s4 = "";
        if (s2.indexOf("://") == -1) {
            s4 = s3.length() <= 0 ? "http://" : "https://";
        }
        String s5 = TextUtils.getValue(hashmap, "loginAccount");
        String s6 = s4 + s2 + "/SiteView/cgi/go.exe/SiteView"
                + "?page=file&account=" + s5 + "&file=" + s
                + "&operation=getbin";
        StringBuffer stringbuffer = new StringBuffer();
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s6,
                TextUtils.getValue(hashmap, "username"), TextUtils.getValue(
                        hashmap, "password"), "", TextUtils.getValue(hashmap,
                        "proxy"), TextUtils.getValue(hashmap, "proxyusername"),
                TextUtils.getValue(hashmap, "proxypassword"), null);
        if (httprequestsettings.getAuthUserName().length() == 0) {
            httprequestsettings.setAuthUserName(TextUtils.getValue(hashmap1,
                    "_adminUsername"));
            httprequestsettings.setAuthPassword(TextUtils.getValue(hashmap1,
                    "_adminPassword"));
        }
        ApacheHttpMethod apachehttpmethod = ApacheHttpUtils.getRequest(
                httprequestsettings, stringbuffer);
        byte abyte0[] = apachehttpmethod.getResponseBody();
        long l = apachehttpmethod.getStatusCode();
        if (l == 200L && abyte0 != null) {
            FileUtils.writeBytesToFile(abyte0, s1);
        }
        return l;
    }

    void printProgress(String s) {
        if (traceStream != null) {
            traceStream.println(s + "<BR>" + "\n");
            traceStream.flush();
        } else if (mainTrace != null) {
            mainTrace.println(s);
        }
    }

    public String toString() {
        return "mirror configuration";
    }

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out
                    .println("Mirror SiteView Server: -server:copyFromServer -user:me -password:pass -proxy:address -target:groupFilesPath");
            System.exit(0);
        }
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        String args1[] = { "-server:", "-user:", "-password:", "-proxy:",
                "-target:" };
        String args2[] = { "server", "username", "password", "proxy", "target" };
        for (int i = 0; i < args2.length; i++) {
            hashmapordered.put(args2[i], "");
        }

        System.out.println("Mirror SiteView: parse args");
        for (int j = 0; j < args.length; j++) {
            for (int l = 0; l < args1.length; l++) {
                if (args[j].startsWith(args1[l])) {
                    String s1 = args[j].substring(args[j].indexOf(args1[l])
                            + args1[l].length());
                    hashmapordered.put(args2[l], s1);
                    System.out.println("Found: " + args2[l] + " = " + s1);
                }
            }

        }

        hashmapordered.put("addOnly", "true");
        hashmapordered.put("_version", "1.0");
        for (int k = 0; k < masterConfigExcludes.length; k++) {
            hashmapordered.add("masterExclusion", masterConfigExcludes[k]);
        }

        MirrorConfiguration mirrorconfiguration = new MirrorConfiguration(
                hashmapordered);
        String s = "1060206940800.dcf";
        String s2 = "d:\\wstest2\\sccs\\app\\SiteView\\tmp\\cache\\persistent\\TopazConfiguration\\current\\"
                + s;
        String s3 = "d:\\myws\\sccs\\app\\SiteView\\cache\\persistent\\TopazConfiguration\\settings";
        if (copyTest.equalsIgnoreCase("true")) {
            try {
                System.out.println("Doing just binary file copies...");
                String s4 = "This is a test by Pedro Wong";
                mirrorPrefsPage mirrorprefspage = new mirrorPrefsPage();
                createFailoverMG(mirrorprefspage);
            } catch (Exception exception) {
                System.out.println(exception);
            }
        } else {
            mirrorconfiguration.copyGroupsOnly();
            String s5 = "http://localhost:8888/SiteView/cgi/go.exe/SiteView?page=file&account=&file=/cache/persistent/ConfigurationManager/InternalIdsManager/&operation=getbin";
            String s6 = "http://localhost:8888/SiteView/cgi/go.exe/SiteView?page=file&account=&file=/cache/persistent/ConfigurationManager/peter/&operation=getbin";
            StringBuffer stringbuffer = new StringBuffer();
            System.out.println("Doing binary file transfer over the wire...");
            if (mirrorconfiguration.status == (long) Monitor.kURLok) {
                System.out.println("Copy succeeded");
                System.exit(0);
            } else {
                String s7 = Monitor.lookupStatus(mirrorconfiguration.status);
                if (mirrorconfiguration.status == (long) Monitor.kMonitorSpecificError
                        || mirrorconfiguration.status == 404L) {
                    s7 = "version too old or address is invalid - SiteViews must be version 4.5 or later and the address must only contain the IP address and port number";
                }
                System.out.println("Copy failed: " + s7);
                System.exit(0);
            }
        }
    }

    private boolean reportToTopaz() {
        return true;
    }

    private static HashMap create_RestartTopazMonitor(String s, int i) {
        HashMap hashmap = new HashMap();
//        hashmap.put("_name", "ReStart " + TopazInfo.getTopazName()
//                + " Reporting upon Primary Failure");
        hashmap.put("_id", "" + i);
        hashmap.add("_internalId", "" + i);
        hashmap.put("_class", "URLMonitor");
        hashmap.put("_alertCondition",
                "category eq 'good' and goodCount >= 1\tdisable disable Failover/"
                        + i + "\t1");
        hashmap.put("_encoding", "Cp1252");
        hashmap.put("_nextConditionID", "2");
        hashmap
                .put(
                        "_url",
                        s
                                + "/SiteView/cgi/go.exe/SiteView?page=HATransition&operation=restartTopazReporting");
        hashmap.put("_frequency", "60");
        hashmap.put("_disabled", "true");
        hashmap.put("_dependsCondition", "good");
        hashmap.put("_timeout", "55");
        hashmap.put("_monitorRunCount", "0");
        hashmap.put("_notLogToTopaz", "on");
        return hashmap;
    }

    private static HashMap create_ResetTopaz_CopyConfig_Monitor(String s, int i) {
        HashMap hashmap = new HashMap();
//        hashmap.put("_name", "Reset " + TopazInfo.getTopazName()
//                + " / Copy Config upon Primary Startup");
        hashmap.put("_id", "" + i);
        hashmap.add("_internalId", "" + i);
        hashmap.put("_class", "URLSequenceMonitor");
        hashmap.put("_postData2", "_disabled=");
        hashmap.put("_alertCondition",
                "category eq 'good' and goodCount >= 1\tdisable disable Failover/"
                        + i + "\t1");
        hashmap.put("_resumeStep", "0");
        hashmap.put("_encoding", "Cp1252");
        hashmap.put("_nextConditionID", "2");
        hashmap.put("_frequency", "60");
        hashmap.put("_disabled", "on");
        hashmap.put("_dependsCondition", "good");
        hashmap.put("_timeout", "55");
        hashmap.put("_referenceType1", "url");
        hashmap.put("_referenceType2", "form");
        hashmap.put("_referenceType3", "form");
        hashmap.put("_referenceType4", "url");
        hashmap
                .put(
                        "_reference1",
                        s
                                + "/SiteView/cgi/go.exe/SiteView?page=topazPrefs&account=administrator");
        hashmap.put("_reference2", "Reset");
        hashmap.put("_reference3", "Reset Settings");
        hashmap
                .put(
                        "_reference4",
                        s
                                + "/SiteView/cgi/go.exe/SiteView?page=HATransition&operation=copyConfiguration");
        hashmap.put("_notLogToTopaz", "on");
        Array array = new Array();
        array.add("roundTripTime > 100000000\terror");
        array.add("totalErrors < 10\tgood");
        hashmap.put("_classifier", array);
        return hashmap;
    }

    private void error(String s) {
        System.out.println(s);
        LogManager.log("Error", s);
    }

    static {
        String s = System.getProperty("MirrorConfiguration.debug", "false");
        if (s.equalsIgnoreCase("true")) {
            debug = true;
        }
    }
}
