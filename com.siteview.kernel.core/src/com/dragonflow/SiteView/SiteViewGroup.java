/*
 * 
 * Created on 2005-2-16 16:51:15
 *
 * SiteViewGroup.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>SiteViewGroup</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.HashMapIterator;
import com.recursionsw.jgl.algorithms.Reversing;
import com.recursionsw.jgl.algorithms.Sorting;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.HTTP.HTTPServer;
import com.dragonflow.Log.DailyFileLogger;
import com.dragonflow.Log.FileLogger;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Log.SSEELogger;
import com.dragonflow.Page.CGI;
import com.dragonflow.Page.GenerateProgress;
import com.dragonflow.Page.accountPrefsPage;
import com.dragonflow.Page.monitorPage;
import com.dragonflow.Page.overviewPage;
import com.dragonflow.Page.reportPage;
import com.dragonflow.ProcessUtils.ProcessMgr;
import com.dragonflow.Properties.FrameFile;
import com.dragonflow.Properties.JdbcConfig;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SSH.SSHManager;
import com.dragonflow.StandardMonitor.URLListMonitor;
import com.dragonflow.StandardMonitor.URLMonitor;
import com.dragonflow.StandardMonitor.URLSequenceMonitor;
import com.dragonflow.Utils.CommandLine;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.Utils.LocaleUtils;
import com.dragonflow.Utils.MailUtils;
import com.dragonflow.Utils.TelnetCommandLine;
import com.dragonflow.Utils.TempFileManager;
import com.dragonflow.Utils.TextUtils;
import SiteViewMain.UpdateConfig;


// Referenced classes of package com.dragonflow.SiteView:
// MonitorGroup, ProgressLogger, Scheduler, EnterpriseImportMail,
// StatusMail, DetectStateChange, FindRunningMonitors, Monitor,
// ShutdownThread, SubGroup, DispatcherSitter, FindGroupWithFileVisitor,
// VirtualMachine, HistoryReport, PortalSiteView, GreaterEqualMonitorName,
// DispatcherMonitor, AtomicMonitor, MirrorConfiguration, User,
// Platform, DetectConfigurationChange, Health, Portal,
// MasterConfig, SiteViewMail, ConfigurationChanger, LogPuller,
// SiteViewObject, MonitorQueue, RollingAverage, TopazConfigurator,
// TopazInfo, TopazAPI, Machine, Server,
// PortalSync, SiteViewLogReader

public class SiteViewGroup extends MonitorGroup {

    public static final String SITEVIEW_GROUP_ID = "SiteView";

    public static final String GROUP_FILE_SUFFIX = ".mg";

    public static final String GROUPS_DIRECTORY = "groups";

    public static final String SETTINGS_FILENAME = "master.config";

    public static final String HISTORY_FILENAME = "history.config";

    public static final String DYNAMIC_FILENAME = "dynamic.config";

    public static final String DISPATCHER_CLIENT_FILENAME = "tss_mon";

    public static final String DRAGONFLOW_SUPPORT_SITE = "http://support.dragonflow.com";

    static final String API_LISTENER = "_apiListener";

    static Thread apiListener = null;

    public static SiteViewGroup cCurrentSiteView = null;

    static boolean allGroupsLoaded = false;

    public static int totalPointsUsed = 0;

    private static final int ROW_NORMAL = 35;

    private boolean bHasCircularGroups;

    static StringProperty pWebserverAddress;

    static StringProperty pAdminURL;

    static HashMap rtCfg = null;

    File groupsDirectory;

    public boolean startingUp;

    public boolean dontStartGroups;

    public int monitorsStarted;

    public DispatcherSitter dispatcherSitter;

    GenerateProgress progressGenerator;

    public Scheduler monitorScheduler;

    public Scheduler maintenanceScheduler;

    public Scheduler reportScheduler;

    public Scheduler vMachineScheduler;

    public Scheduler healthScheduler;

    public Scheduler jdbcConfigScheduler;

    public Scheduler topazSyncScheduler;

    public DetectConfigurationChange checkConfiguration;

    public HTTPServer httpServer;

    public HTTPServer httpsServer;

    public static boolean enableRemoteCommandLine = true;

    public static String siteviewHostname = "localhost";

    private static boolean displayGauges = true;

    private static ArrayList killableProcesses = new ArrayList(5);

    public static boolean killProcessesIsSet;

    private static String doNotKillProcesses;

    public boolean stopping;

    SSEELogger sseeLogger;

//    TopazLogger topazLogger;

    HashMap groupsCache;

    String progressSummary;

    public static final String accountDirectory = "/SiteView/accounts/";

    static final boolean $assertionsDisabled; /* synthetic field */

    public SiteViewGroup() {
        bHasCircularGroups = false;
        groupsDirectory = null;
        startingUp = true;
        dontStartGroups = true;
        monitorsStarted = 0;
        dispatcherSitter = null;
        progressGenerator = null;
        monitorScheduler = null;
        maintenanceScheduler = null;
        reportScheduler = null;
        vMachineScheduler = null;
        healthScheduler = null;
        jdbcConfigScheduler = null;
        topazSyncScheduler = null;
        checkConfiguration = null;
        httpServer = null;
        httpsServer = null;
        stopping = false;
        sseeLogger = null;
        groupsCache = new HashMap();
        progressSummary = "Starting...";
        String s = Platform.getRoot();
        groupsDirectory = new File(s + File.separator + "groups");
        if (pSiteViewLogfilePath == null) {
            pSiteViewLogfilePath = new StringProperty("_siteviewLogfilePath");
        }
        setProperty(pSiteViewLogfilePath, s + File.separator + "logs" + File.separator + "SiteView.log");
        if (pSiteViewLogName == null) {
            pSiteViewLogName = new StringProperty("_siteViewLogName");
        }
        setProperty(pSiteViewLogName, "SiteViewLog");
        if (pURLLogName == null) {
            pURLLogName = new StringProperty("_urlLogName");
        }
        setProperty(pURLLogName, "URL");
        if (pErrorLogName == null) {
            pErrorLogName = new StringProperty("_errorLogName");
        }
        setProperty(pErrorLogName, "Error");
        if (pAlertLogName == null) {
            pAlertLogName = new StringProperty("_alertLogName");
        }
        setProperty(pAlertLogName, "Alert");
        loadSettings();
        checkConfiguration = DetectConfigurationChange.getInstance();
        Health.getHealth();
    }

    public boolean internalServerActive() {
        return httpServer != null || httpsServer != null;
    }

    public com.dragonflow.Page.CGI.menus getNavItems(HTTPRequest httprequest) {
        com.dragonflow.Page.CGI.menus menus1 = new com.dragonflow.Page.CGI.menus();
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if (httprequest.actionAllowed("_preference")) {
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if (httprequest.actionAllowed("_tools")) {
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if (httprequest.actionAllowed("_progress")) {
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new com.dragonflow.Page.CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        return menus1;
    }

    public static void SignalReload() {
        if (Platform.isPortal()) {
            Portal.signalReload();
        } else {
            SiteViewGroup siteviewgroup = currentSiteView();
            if (siteviewgroup.internalServerActive()) {
                if (siteviewgroup.dontStartGroups && !siteviewgroup.startingUp) {
                    LogManager.log("RunMonitor", "Group file modified, will reload after initialization");
                } else {
                    siteviewgroup.checkConfiguration.execute();
                }
            }
        }
    }

    public static SiteViewObject currentSiteViewForID(String s) {
        if (s == null || s.length() == 0) {
            return currentSiteView();
        } else {
            return currentSiteView();
        }
    }

    public static SiteViewGroup currentSiteView() {
        if (cCurrentSiteView == null) {
            cCurrentSiteView = new SiteViewGroup();
        }
        return cCurrentSiteView;
    }

    public static String getServerID() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        return getServerID(hashmap);
    }

    public static String getServerID(HashMap hashmap) {
        String s = TextUtils.getValue(hashmap, "_serverID");
        if (s.length() == 0) {
            s = TextUtils.getValue(hashmap, "_webserverAddress");
        }
        return s;
    }

    public static boolean allowRemoteCommandLine() {
        return enableRemoteCommandLine;
    }

    public boolean isStartingUp() {
        return startingUp;
    }

    public static Vector getTemplateList(String s, HTTPRequest httprequest) {
        File file = new File(Platform.getUsedDirectoryPath(s, httprequest));
        Vector vector = new Vector();
        String as[] = file.list();
        for (int i = 0; i < as.length; i ++) {
            vector.addElement(as[i]);
        }

        return vector;
    }

    public File getGroupsDirectory() {
        return groupsDirectory;
    }

    public void startSiteView() {
        if (cCurrentSiteView != this) {
            cCurrentSiteView = this;
            if (getSetting("_httpPort").length() > 0) {
                startHTTPServer();
            }
            if (getSetting("_httpSecurePort").length() > 0) {
                startHTTPSServer();
            }
//            if (Platform.licenseExpired()) {
//                HTTPRequestThread.setLicenseExpired(true);
//                LogManager.log("RunMonitor", "License Expired: only starting the limited HTTP server, no monitors are running.");
//                LogManager.log("Error", "License Expired: only starting the limited HTTP server, no monitors are running.");
//                return;
//            }
//        }
//		startingUp = false;
//			return;
            String s = getSetting("_startupScript");
            if (s.length() > 0) {
                LogManager.log("RunMonitor", "Running startup script " + s);
                CommandLine commandline = new CommandLine();
                Array array = commandline.exec(s);
                long l = commandline.getExitValue();
                for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); LogManager.log("RunMonitor", "   " + enumeration.nextElement())) 
				{
                }
                if (l == 0L) {
                    LogManager.log("RunMonitor", "Startup script completed: exit (" + l + ")");
                } else {
                    LogManager.log("RunMonitor", "STARTUP SCRIPT FAILED: exit (" + l + ")");
                    LogManager.log("Error", "STARTUP SCRIPT FAILED: exit (" + l + ")");
                }
            }
            Thread thread = new Thread(new TempFileManager());
            thread.start();/*模板文件管理线程启动*/
            rtCfg = loadRTConfig(MasterConfig.getMasterConfig());
//            MdrvMain.initialize();
            String s1 = (String) rtCfg.get("_apiListener");
            if (s1 != null) {
                String as[] = TextUtils.split(s1, ";");
                for (int i = 0; i < as.length; i ++) {
                    if (as[i].length() <= 0) {
                        continue;
                    }
                    Object obj = null;
                    try {
                        Class class1 = Class.forName(as[i]);
                        apiListener = (Thread) class1.newInstance();
                        apiListener.start();
                    } catch (Exception exception) {
                        LogManager.log("Error", "Failed to initiate API listener: " + exception.getClass().getName() + ": " + exception.getMessage());
                    }
                }

            }
            if (DispatcherSitter.isDispatcherInstalled()) {
                clearDispatcher();
                DispatcherSitter.hardKillProcessOnNT("tss_dispatcher", 5);
                DispatcherSitter.hardKillProcessOnNT("tss_monitor", 0);
            }
            ProgressLogger progresslogger = new ProgressLogger();
            LogManager.registerLogger("Progress", progresslogger);
            reportScheduler = new Scheduler();
            vMachineScheduler = new Scheduler();
            monitorScheduler = new Scheduler();
            maintenanceScheduler = new Scheduler();
            HistoryReport.cleanAllTemporaryReportFiles();
            if (getSetting("_disableLoginPage").length() > 0) {
                HTTPRequest.allowCookieLogin = false;
                LogManager.log("RunMonitor", " Disabled login form");
            }
            if (getSetting("_installTime").length() == 0) {
                setProperty("_installTime", "" + TextUtils.timeSeconds());
                setProperty("_installed", "" + TextUtils.prettyDate());
                saveSettings();
                SiteViewMail.firstTimeMail(this, "SiteView Registration", "siteviewsales@merc-int.com");
            }
            boolean flag = (new File(Platform.getRoot() + File.separator + "groups" + File.separator + "mirror.config")).exists();
            if (flag) {
                doHA();
            }
            maintenanceScheduler.startScheduler("Maintenance Scheduler");
            if (getSetting("_configJdbcURL").length() > 0) {
                StartDDC();
            }
            checkConfiguration.execute();
            if (DispatcherSitter.isDispatcherInstalled() && getDispatcherMonitorCount() > 0) {
                startDispatcher();
                notifyDispatcherAtStartup();
            }
            if (getSetting("_enterpriseImportServer").length() != 0) {
                int j = getSettingAsLong("_enterpriseImportFrequency", 600) * 1000;
                String s2 = getSetting("_enterpriseImportServer");
                String s3 = getSetting("_enterpriseImportAccount");
                String s4 = getSetting("_enterpriseImportPassword");
                maintenanceScheduler.scheduleRepeatedPeriodicAction(new EnterpriseImportMail(s2, s3, s4), j);
            }
            if (getSetting("_autoDaily").length() != 0) {
                maintenanceScheduler.scheduleRepeatedDailyAction(new StatusMail(), "m,t,w,r,f,s,u", "07:07");
            }
            int k = getSettingAsLong("_fileCheckFrequency", 120) * 1000;
            int i1 = getSettingAsLong("_progressCheckFrequency", 120) * 1000;
            boolean flag1 = TextUtils.getValue(rtCfg, "_disableRepeatedSchedules").equalsIgnoreCase("true");
			//flag1 = true;
			if (!flag1) {
                maintenanceScheduler.scheduleRepeatedPeriodicAction(new DetectStateChange(), k);
            }
            HTTPRequest httprequest = new HTTPRequest();
            httprequest.setUser("administrator");
            HTTPRequest httprequest1 = new HTTPRequest();
            httprequest1.setUser("user");
            if (getSetting("_createStaticHTML").length() == 0) {
                httprequest1.setValue("hideState", "true");
                httprequest.setValue("hideState", "true");
            }
            createMonitorCountLog();
            if (getSetting("_autoStartup").length() != 0) {
                if (!$assertionsDisabled && (getMonitorCount() < 0 || totalPointsUsed <= 0) && getMonitorCount() != totalPointsUsed) {
                    throw new AssertionError("point count is zero and monitor count non-zero; Monitor count: " + getMonitorCount() + " Point count: " + totalPointsUsed);
                }
                SiteViewMail.statusMail(this, "started");
            }
            progressGenerator = new GenerateProgress(new File(Platform.getRoot() + File.separator + "htdocs" + File.separator + "Progress.html"), httprequest);
            progressGenerator.execute();
            maintenanceScheduler.scheduleRepeatedPeriodicAction(progressGenerator, i1);
            if (Platform.isUserAccessAllowed()) {
                GenerateProgress generateprogress = new GenerateProgress(new File(Platform.getRoot() + File.separator + "userhtml" + File.separator + "Progress.html"), httprequest1);
                generateprogress.execute();
                if (!flag1) {
                    maintenanceScheduler.scheduleRepeatedPeriodicAction(generateprogress, i1);
                }
            }
            ConfigurationChanger.initialize();
            if (!flag || MirrorConfiguration.isHAInControl()) {
//                TopazManager.getInstance().restart();
            }
            if (Platform.isPortal()) {
                LogManager.log("RunMonitor", "Starting " + Platform.productName + ".");
                Portal.getPortal();
                LogPuller.startPulling();
            }
            String s5 = getSetting("_initializationURL");
            if (s5.length() > 0) {
                LogManager.log("RunMonitor", "Running initialization URL: " + s5);
                URLMonitor.checkURL(s5);
            }
            monitorScheduler.startScheduler("Monitor Scheduler");
            reportScheduler.startScheduler("Report Scheduler");
            vMachineScheduler.startScheduler("Dynamic Update Scheduler");
            loadDynamic();
            LogManager.log("RunMonitor", "  ");
            LogManager.log("RunMonitor", Platform.productName + " is active...");
            long l1 = (monitorScheduler.getNextTime() - Platform.timeMillis()) / 1000L;
            if (l1 > 15L) {
                String s6 = monitorScheduler.getNextActionDescription();
                LogManager.log("RunMonitor", "  waiting " + l1 + " seconds: will " + s6);
            }
            if (getSetting("_noHttpAccessDuringInitialization").length() == 0) {
                startingUp = false;
            }
            checkForCircularGroups();
            startMonitor();
            monitorsStarted = 0;
            currentSiteView().startGroupScheduler(true);
            startingUp = false;
            dontStartGroups = false;
            LogManager.log("RunMonitor", Platform.productName + " Startup Completed");
            SignalReload();
            if (!flag1) {
                maintenanceScheduler.scheduleRepeatedPeriodicAction(checkConfiguration, k);
            }
        } else {
            LogManager.log("RunMonitor", Platform.productName + " started already");
        }
    }

    public boolean hasCircularGroups() {
        return bHasCircularGroups;
    }

    /**
     * 
     * 
     * @return
     */
    public boolean checkForCircularGroups() {
        HashSet hashset = new HashSet();
		Enumeration enumeration = getElements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if ((obj instanceof MonitorGroup) && !hashset.contains(obj)) {
                MonitorGroup monitorgroup = (MonitorGroup) obj;
                HashSet hashset1 = new HashSet();
                hashset1.add(monitorgroup);
                for (SiteViewObject siteviewobject = monitorgroup.getParent(); siteviewobject != null;) {
                    if (hashset1.contains(siteviewobject)) {
                        String s = siteviewobject.getProperty("_id");
                        LogManager.log("Error", "Circular group hierarchy detected involving group " + s);
                        bHasCircularGroups = true;
                        return true;
                    }
                    if ((com.dragonflow.SiteView.MonitorGroup.class).isInstance(siteviewobject)) {
                        siteviewobject = ((MonitorGroup) siteviewobject).getParent();
                    } else {
                        siteviewobject = null;
                    }
                }

                hashset.add(hashset1);
            }
        }
        bHasCircularGroups = false;
        return bHasCircularGroups;
    }

    public void StartDDC() {
        int i = getSettingAsLong("_configJdbcCheckFrequency", 120) * 1000;
        jdbcConfigScheduler.scheduleRepeatedPeriodicAction(JdbcConfig.getJdbcConfig(), i);
        jdbcConfigScheduler.startScheduler("DDC Scheduler");
        JdbcConfig.inInitStage = false;
    }

    public void StopDDC() {
        jdbcConfigScheduler.stopScheduler();
        JdbcConfig.inInitStage = true;
    }

    void startHTTPSServer() {
        int i = 0;
        String s = getSetting("_httpSecurePort");
        if (s.length() > 0) {
            i = StringProperty.toInteger(s);
        }
        if (i == -1) {
            LogManager.log("RunMonitor", " access using HTTPS disabled");
            return;
        }
        int j = i;
        String s1 = getSetting("_httpsActivePort");
        if (s1.length() > 0) {
            j = StringProperty.toInteger(s1);
        }
        int k = 0;
        String s2 = getSetting("_httpsMaxConnections");
        if (s2.length() > 0) {
            k = StringProperty.toInteger(s2);
        }
        String s3 = getSetting("_httpsFreezePort");
        if (httpsServer != null) {
            httpsServer.stopServer();
        }
        httpsServer = null;
        if (i != 0) {
            httpsServer = new HTTPServer(i, k, true, getSetting("_httpKeepAliveEnabled").length() > 0);
            if (getSetting("_httpKeepAliveEnabled").length() > 0) {
                HTTPRequest.useHTTP11 = true;
            }
            httpsServer.addCGIVirtualDirectory("/SiteView/cgi", Platform.getRoot() + File.separator + "cgi");
            httpsServer.addVirtualDirectory("/SiteView", Platform.getRoot());
            String s4 = getSetting("_httpDocumentTypes");
            if (s4.length() > 0) {
                String as[] = TextUtils.split(s4, ",");
                for (int i1 = 0; i1 < as.length; i1 ++) {
                    int k1 = as[i1].indexOf("=");
                    if (k1 >= 0) {
                        String s5 = as[i1].substring(0, k1);
                        String s6 = as[i1].substring(k1 + 1);
                        httpsServer.addType(s5, s6);
                    }
                }

            }
            boolean flag = false;
            int j1 = 0;
            byte byte0 = 20;
            int l1 = 30000;
            String s7 = "";
            while (!flag) {
                try {
                    httpsServer.port = i;
                    if (s3.length() == 0) {
                        httpsServer.port += j1;
                    }
                    httpsServer.startServer();
                    flag = true;
                    continue;
                } catch (Exception exception) {
                    s7 = exception.toString();
                }
                if (++ j1 == byte0) {
                    break;
                }
                if (s3.length() != 0) {
                    LogManager.log("Error", "retry #" + j1 + " in " + l1 / 1000 + " seconds: could not start HTTP server on port " + i + ", " + s7);
                    Platform.sleep(l1);
                }
            }

            if (!flag) {
                LogManager.log("Error", "Could not start HTTPS server on port " + i + ", " + s7);
                SiteViewMail.portErrorMail(this, s7, i, i);
            } else {
                setProperty("_httpsActivePort", "" + httpsServer.port);
                long l2 = TextUtils.toLong(getSetting("_readmeDate"));
                long l3 = (new File(Platform.getRoot() + "/ReadMe.htm")).lastModified();
                String s8 = getProperty("_webserverAddress");
                String s9 = getProperty("_lockWebserverAddress");
                if (s9.length() == 0 && (!TextUtils.hasLetters(s8) || s8.equals("localhost"))) {
                    boolean flag1 = false;
                    Array array = Platform.getIPAddresses("");
                    Enumeration enumeration = (Enumeration) array.iterator();
                    while (enumeration.hasMoreElements()) {
                        String s12 = (String) enumeration.nextElement();
                        if (s8.equals(s12)) {
                            flag1 = true;
                        }
                    }
                    if (!flag1) {
                        s8 = Platform.getDefaultIPAddress();
                        setProperty("_webserverAddress", s8);
                    }
                }
                String s10 = getProperty("_adminURL");
                String s11 = mainSecureURL();
                setProperty("_adminURL", s11);
                setProperty("_userURL", s11 + "/userhtml/SiteView.html");
                if (!s11.equals(s10) || l3 != l2) {
                    LogManager.log("Error", "Updating URL in Readme.htm to " + s11);
                    UpdateConfig.updateFiles("https", "" + httpsServer.port, s8, getSetting("_adminRedirectFile"), getSetting("_userRedirectFile"));
                    setProperty("_readmeDate", "" + (new File(Platform.getRoot() + "/ReadMe.htm")).lastModified());
                }
                saveSettings();
                if (httpsServer.port != j) {
                    LogManager.log("Error", "Changing HTTP Server to port " + httpsServer.port + ", " + s7);
                    SiteViewMail.portChangedMail(this, s7, j, httpsServer.port);
                }
            }
        }
        siteviewHostname = getSetting("_webserverAddress");
        int l = siteviewHostname.indexOf(":");
        if (l >= 0) {
            siteviewHostname = siteviewHostname.substring(0, l);
        }
        LogManager.log("RunMonitor", "  ");
        LogManager.log("RunMonitor", " For a Secure Connection ");
        LogManager.log("RunMonitor", "Open your web browser to:");
        LogManager.log("RunMonitor", "  " + mainSecureURL());
    }

    void startHTTPServer() {
        int i = 0;
        String httpPort = getSetting("_httpPort");
        if (httpPort.length() > 0) {
            i = StringProperty.toInteger(httpPort);
        }
        if (i == -1) {
            LogManager.log("RunMonitor", " access using HTTP disabled");
            return;
        }
        int j = i;
        String httpActivePort = getSetting("_httpActivePort");
        if (httpActivePort.length() > 0) {
            j = StringProperty.toInteger(httpActivePort);
        }
        int k = 0;
        String httpMaxConnections = getSetting("_httpMaxConnections");
        if (httpMaxConnections.length() > 0) {
            k = StringProperty.toInteger(httpMaxConnections);
        }
        String s3 = getSetting("_postLogFile");
        if (s3.length() > 0) {
            String s4 = getSetting("_createDailyPostLog");
            long l = getSettingAsLong("_dailyLogTotalLimit", 0xf4240);
            int j1 = getSettingAsLong("_dailyLogKeepDays", 40);
            int i2 = getSettingAsLong("_maxAuxLogSize", 0xf4240);
            HTTPRequest.logPOSTs = true;
            try {
                s3 = Platform.getRoot() + File.separator + "logs" + File.separator + "post.log";
                if (s4.length() > 0) {
                    DailyFileLogger dailyfilelogger = new DailyFileLogger(s3, l, j1);
                    LogManager.registerLogger("POST", dailyfilelogger);
                } else {
                    FileLogger filelogger = new FileLogger(s3, i2);
                    LogManager.registerLogger("POST", filelogger);
                }
            } catch (IOException ioexception) {
                LogManager.log("Error", "Could not open POST request log file " + s3);
            }
        }
        if (getSetting("_centrascopeAccessOnly").length() > 0) {
            HTTPRequest.portalAccessOnly = true;
            LogManager.log("Error", "Allow full access to CentraScope only");
        }
        String s5 = getSetting("_httpFreezePort");
        if (httpServer != null) {
            httpServer.stopServer();
        }
        httpServer = null;
        if (i != 0) {
            httpServer = new HTTPServer(i, k, false, getSetting("_httpKeepAliveEnabled").length() > 0);
            httpServer.addCGIVirtualDirectory("/SiteView/cgi", Platform.getRoot() + File.separator + "cgi");
            httpServer.addVirtualDirectory("/SiteView", Platform.getRoot());
            if (getSetting("_httpKeepAliveEnabled").length() > 0) {
                HTTPRequest.useHTTP11 = true;
            }
            String s6 = getSetting("_httpDocumentTypes");
            if (s6.length() > 0) {
                String as[] = TextUtils.split(s6, ",");
                for (int k1 = 0; k1 < as.length; k1 ++) {
                    int j2 = as[k1].indexOf("=");
                    if (j2 >= 0) {
                        String s7 = as[k1].substring(0, j2);
                        String s8 = as[k1].substring(j2 + 1);
                        httpServer.addType(s7, s8);
                    }
                }

            }
            boolean flag = false;
            int l1 = 0;
            byte byte0 = 20;
            int k2 = 30000;
            String s9 = "";
            do {
                if (flag) {
                    break;
                }
                try {
                    httpServer.port = i;
                    if (s5.length() == 0) {
                        httpServer.port += l1;
                    }
                    httpServer.startServer();
                    flag = true;
                    continue;
                } catch (Exception exception) {
                    s9 = exception.toString();
                }
                if (++ l1 == byte0) {
                    break;
                }
                if (s5.length() != 0) {
                    LogManager.log("Error", "retry #" + l1 + " in " + k2 / 1000 + " seconds: could not start HTTP server on port " + i + ", " + s9);
                    Platform.sleep(k2);
                }
            } while (true);
            if (!flag) {
                LogManager.log("Error", "Could not start HTTP server on port " + i + ", " + s9);
                SiteViewMail.portErrorMail(this, s9, i, i);
            } else {
                setProperty("_httpActivePort", "" + httpServer.port);
                long l2 = TextUtils.toLong(getSetting("_readmeDate"));
                long l3 = (new File(Platform.getRoot() + "/ReadMe.htm")).lastModified();
                String s10 = getProperty("_webserverAddress");
                String s11 = getProperty("_lockWebserverAddress");
                if (s11.length() == 0 && (!TextUtils.hasLetters(s10) || s10.equals("localhost"))) {
                    boolean flag1 = false;
                    Array array = Platform.getIPAddresses("");
                    Enumeration enumeration = (Enumeration) array.iterator();
                    while (enumeration.hasMoreElements()) {
                        String s14 = (String) enumeration.nextElement();
                        if (s10.equals(s14)) {
                            flag1 = true;
                        }
                    }

                    if (!flag1) {
                        s10 = Platform.getDefaultIPAddress();
                        setProperty("_webserverAddress", s10);
                    }
                }
                String s12 = getProperty("_adminURL");
                String s13 = mainURL();
                setProperty("_adminURL", s13);
                setProperty("_userURL", s13 + "/userhtml/SiteView.html");
                if (!s13.equals(s12) || l3 != l2) {
                    LogManager.log("Error", "Updating URL in Readme.htm to " + s13);
                    UpdateConfig.updateFiles("http", "" + httpServer.port, s10, getSetting("_adminRedirectFile"), getSetting("_userRedirectFile"));
                    setProperty("_readmeDate", "" + (new File(Platform.getRoot() + "/ReadMe.htm")).lastModified());
                }
                saveSettings();
                if (httpServer.port != j) {
                    LogManager.log("Error", "Changing HTTP Server to port " + httpServer.port + ", " + s9);
                    SiteViewMail.portChangedMail(this, s9, j, httpServer.port);
                }
            }
        }
        siteviewHostname = getSetting("_webserverAddress");
        int i1 = siteviewHostname.indexOf(":");
        if (i1 >= 0) {
            siteviewHostname = siteviewHostname.substring(0, i1);
        }
        LogManager.log("RunMonitor", "  ");
        LogManager.log("RunMonitor", "Open your web browser to:");
        LogManager.log("RunMonitor", "  " + mainURL());
    }

    void logRunningMonitors() {
        FindRunningMonitors findrunningmonitors = new FindRunningMonitors();
        acceptVisitor(findrunningmonitors);
        Enumeration enumeration = findrunningmonitors.getResultsElements();
        if (enumeration.hasMoreElements()) {
            String s;
            String s1;
            String s3;
            for (; enumeration.hasMoreElements(); LogManager.log("Error", Platform.productName + " shutting down, monitor still running, " + s3 + ":" + s + ", " + s1)) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                s = monitor.getProperty(Monitor.pName);
                s1 = monitor.currentStatus;
                long l = monitor.getPropertyAsLong(Monitor.pLastUpdate);
                if (l > 0L) {
                    String s2 = TextUtils.prettyDate(new Date(l));
                    s1 = s1 + " (previous update at " + s2 + ")";
                }
                s3 = monitor.getProperty(SiteViewObject.pOwnerID);
                Monitor monitor1 = (Monitor) getElement(s3);
                if (monitor1 != null) {
                    s3 = monitor1.getProperty(Monitor.pName);
                }
            }

        }
    }

    public void stopSiteView(boolean flag, String s) {
        int i = (int) getSettingAsLong("_shutdownTimeout");
        if (i <= 0) {
            i = 60;
        }
        ShutdownThread shutdownthread = new ShutdownThread(flag ? 0 : i - 20, s);
        try {
            synchronized (shutdownthread) {
                shutdownthread.start();
                shutdownthread.wait(i * 1000);
            }
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
    }

    public void stopSiteViewNoTimeout(int i, String s) {
        stopping = true;
        if (Platform.isPortal()) {
            LogPuller.stopPulling();
        }
        if (reportScheduler != null) {
            reportScheduler.stopScheduler();
        }
        if (maintenanceScheduler != null) {
            maintenanceScheduler.stopScheduler();
        }
        if (vMachineScheduler != null) {
            vMachineScheduler.stopScheduler();
        }
        if (monitorScheduler != null) {
            monitorScheduler.stopScheduler();
        }
        if (topazSyncScheduler != null) {
            topazSyncScheduler.stopScheduler();
        }
        if (dispatcherSitter != null) {
            clearDispatcher();
        }
//        MdrvMain.terminate();
        if (jdbcConfigScheduler != null) {
            StopDDC();
        }
        MonitorQueue.stopQueue();
        ProcessMgr.stopAllProcesses();
//        TopazManager.getInstance().terminate();
        if (i > 0) {
            LogManager.log("Error", "Waiting " + i + " secs to allow monitors to complete.");
            Platform.sleep(i * 1000);
        } else {
            LogManager.log("Error", "Shutting down quickly, not allowing monitors to complete.");
        }
        logRunningMonitors();
        stopMonitor();
        SSHManager.getInstance().closeAll();
        TelnetCommandLine.closeAll();
//        SiebelCmdLineSession.closeall();
        if (i > 0) {
            if (httpServer != null) {
                httpServer.stopServer();
            }
            if (httpsServer != null) {
                httpsServer.stopServer();
            }
        }
        writeMonitorStats();
        stopping = false;
        if (cCurrentSiteView == this) {
            cCurrentSiteView = null;
        }
        LogManager.log("error", s);
        LogManager.shutdown();
    }

    public static void setupKillableProcesses() {
        switch (Platform.getOs()) {
        case 1: // '\001'
            addKillableProcess("plink.exe");
            addKillableProcess("srvrmgr.exe");
            addKillableProcess("ping");
            addKillableProcess("SendModem.exe");
            addKillableProcess("perfex.exe");
        // fall through

        case 2: // '\002'
        case 6: // '\006'
        default:
            return;
        }
    }

    public static void addKillableProcess(String s) {
        if (doNotKillProcesses.indexOf(s) == -1) {
            killableProcesses.add(s);
        }
    }

    public static void killProcesses() {
        String as[] = new String[2];
        as[0] = Platform.killCommand();
        if (as[0] == null) {
            LogManager.log("Errror", "Unsupported platform no kill command available");
            return;
        }
        java.util.ListIterator listiterator = killableProcesses.listIterator();
        Runtime runtime = Runtime.getRuntime();
        Vector vector = new Vector();
        while (listiterator.hasNext()) {
            as[1] = (String) listiterator.next();
            try {
                vector.add(runtime.exec(as));
            } catch (IOException ioexception) {
                LogManager.log("Error", "Unable to kill potential zombie process.");
                ioexception.printStackTrace();
            }
        }
        for (int i = 0; i < vector.size();i ++) {
            try {
                ((Process) vector.get(i)).waitFor();
                continue;
            } catch (InterruptedException interruptedexception) {
                LogManager.log("Error", "Failed to wait for the kill process during killing of zombie processes");
                interruptedexception.printStackTrace();
                //i ++;dingbing.xu
            }
        }

    }

    public static void writeMonitorStats() {
        float f = AtomicMonitor.monitorStats.getMaximumCountPerTimePeriod();
        long l = AtomicMonitor.monitorStats.getMaximumCountPerTimePeriodTime();
        float f1 = AtomicMonitor.monitorStats.getMaximum();
        long l1 = AtomicMonitor.monitorStats.getMaximumTime();
        float f2 = AtomicMonitor.monitorStats.getAverage();
        int i = MonitorQueue.maxReadyMonitors;
        long l2 = MonitorQueue.maxReadyMonitorsTime;
        LogManager.log("RunMonitor", "Average Monitors Running: " + TextUtils.floatToString(f2, 2));
        LogManager.log("RunMonitor", "Peak Monitors Per Minute: " + TextUtils.floatToString(f, 2) + " at " + TextUtils.prettyDate(l));
        LogManager.log("RunMonitor", "Peak Monitors Running: " + f1 + " at " + TextUtils.prettyDate(l1));
        LogManager.log("RunMonitor", "Peak Monitors Waiting: " + i + " at " + TextUtils.prettyDate(l2));
    }

    public static boolean shouldWriteHTML() {
        SiteViewGroup siteviewgroup = currentSiteView();
        return !siteviewgroup.internalServerActive() || siteviewgroup.getSetting("_createStaticHTML").length() > 0;
    }

    public static void saveMonitorPID(int i) {
        String s = Platform.getRoot() + "/groups/monpid";
        try {
            FileOutputStream fileoutputstream = new FileOutputStream(s);
            PrintWriter printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            printwriter.println(i);
            printwriter.close();
            fileoutputstream.close();
        } catch (Exception exception) {
            System.out.println("error writing process id: " + s);
            System.out.println(exception);
        }
    }

    public synchronized void unregisterSSEELogger() {
        LogManager.unregisterLogger("SSEELog", sseeLogger);
        LogManager.unregisterLogger("SiteViewLog", sseeLogger);
        sseeLogger.close();
        sseeLogger = null;
    }

    public synchronized void registerSSEELogger(String s, int i, String s1, int j, String s2) throws Exception {
        try {
            if (sseeLogger != null) {
                sseeLogger.reinitialize();
            } else {
                sseeLogger = new SSEELogger(s, i, s1, j, s2);
                LogManager.registerLogger("SSEELog", sseeLogger);
                LogManager.registerLogger("SiteViewLog", sseeLogger);
            }
            File file = new File(Platform.getRoot() + "/groups");
            String as[] = file.list();
            SiteViewGroup siteviewgroup = currentSiteView();
            for (int k = 0; k < as.length; k ++) {
                if (as[k].endsWith(".mg")) {
                    int l = as[k].lastIndexOf(".mg");
                    String s3 = as[k].substring(0, l);
                    s3 = I18N.toDefaultEncoding(s3);
                    MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElementByID(s3);
                    if (LogManager.loggerRegistered("SSEELog")) {
                        Enumeration enumeration = monitorgroup.getMonitors();
                        while (enumeration.hasMoreElements()) {
                            Monitor monitor = (Monitor) enumeration.nextElement();
                            if (!(monitor instanceof SubGroup)) {
                                LogManager.log("SSEELog", monitor);
                            }
                        }
                    }
                }
            }

        } catch (Exception exception) {
            LogManager.log("Error", "Could not register Frieda logger  exception: " + exception.getMessage());
            throw exception;
        }
    }

    public synchronized void registerToTopaz() {
//        TopazConfigurator.hardSync = true;
        if (topazSyncScheduler == null || !topazSyncScheduler.isRunning()) {
            try {
                if (topazSyncScheduler == null) {
                    topazSyncScheduler = new Scheduler();
                }
                int i = getMonitorCount();
                long l = 0L;
                if (i < 500) {
                    l = getSettingAsLong("_topazSyncFrequency", 600) * 1000;
                } else {
                    l = getSettingAsLong("_topazSyncFrequency", 1200) * 1000;
                }
//                LogManager.log("RunMonitor", TopazInfo.getTopazName() + " Sync Scheduler Starting. ");
//                topazSyncScheduler.startScheduler(TopazInfo.getTopazName() + " Sync Scheduler", true);
//                topazSyncScheduler.scheduleRepeatedPeriodicAction(TopazConfigurator.getTopazConfigurator(), l);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
//        if (TopazAPI.registerTopazSiteView().length() <= 0) 
        {
            try {
                LogManager.registerLogger("Topaz", new FileLogger(Platform.getRoot() + File.separator + "logs" + File.separator + "topaz.log", TextUtils.toInt(getSetting("_maxAuxLogSize"))));
            } catch (Exception exception1) {
//                LogManager.log("Error", "Not able to register " + TopazInfo.getTopazName() + " logger " + exception1.getMessage());
            }
//            TopazAPI.setSessionID(TopazManager.getInstance().getTopazServerSettings().getProfileName());
        }
//        try {
//            if (topazLogger == null) {
//                topazLogger = new TopazLogger();
//                LogManager.registerLogger("SiteViewLog", topazLogger);
//            }
//        } catch (Exception exception2) {
////            LogManager.log("RunMonitor", "Could not register " + TopazInfo.getTopazName() + " logger " + " exception: " + exception2.getMessage());
//        }
    }

    public static int monitorPID() {
        int i = 0;
        String s = Platform.getRoot() + "/groups/monpid";
        try {
            if ((new File(s)).exists() && Platform.isService()) {
                FileInputStream fileinputstream = new FileInputStream(s);
                BufferedReader bufferedreader = FileUtils.MakeInputReader(fileinputstream);
                String s1 = bufferedreader.readLine();
                if (Platform.getOs() == 1 && s1.startsWith("username=")) {
                    Platform.userName = s1.substring("username=".length());
                    s1 = bufferedreader.readLine();
                }
                i = TextUtils.toInt(s1);
                bufferedreader.close();
                fileinputstream.close();
            }
        } catch (Exception exception) {
            System.out.println("error reading parent process id: " + s);
            System.out.println(exception);
        }
        return i;
    }

    public void loadSettings() {
        removeAllRules();
        MasterConfig.clearConfigCache();
        readFromHashMap(MasterConfig.getMasterConfig());
        initialize(MasterConfig.getMasterConfig());
        Machine.registerMachines(getMultipleValues("_remoteMachine"));
        setProperty(pID, "SiteView");
    }

    public void saveSettings() {
        String s = groupsDirectory.getAbsolutePath() + File.separator + "master.config";
        try {
            Array array = new Array();
            array.add(getValuesTable());
            FrameFile.writeToFile(s, array, "_", true, true);
            Platform.chmod(s, "rw");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Could not write master.config file at:" + s);
        }
    }

    public String mainSecureURL() {
        String s = "https://" + getSetting("_webserverAddress");
        String s1 = getSetting("_httpsActivePort");
        if (s1.length() != 0 && !s1.equals("80")) {
            s = s + ":" + s1;
        }
        s = s + "/SiteView";
        return s;
    }

    public String mainURL() {
        String s = "http://" + getSetting("_webserverAddress");
        String s1 = getSetting("_httpActivePort");
        if (s1.length() != 0 && !s1.equals("80")) {
            s = s + ":" + s1;
        }
        s = s + "/SiteView";
        return s;
    }

    public Array getGroupFiles() {
        Array array = new Array();
        if (groupsDirectory.exists() && groupsDirectory.isDirectory()) {
            File file = new File(groupsDirectory, "master.config");
            if (file.exists()) {
                array.add(file);
            }
            file = new File(groupsDirectory, "dynamic.config");
            if (file.exists()) {
                array.add(file);
            }
            file = new File(groupsDirectory, "history.config");
            if (file.exists()) {
                array.add(file);
            }
            file = new File(groupsDirectory, "users.config");
            if (file.exists()) {
                array.add(file);
            }
            file = new File(groupsDirectory, "multi.config");
            if (file.exists()) {
                array.add(file);
            }
            file = new File(groupsDirectory, TEMPLATES_FILE);
            if (file.exists()) {
                array.add(file);
            }
            String as[] = groupsDirectory.list();
            for (int i = 0; i < as.length; i ++) {
                if (as[i].endsWith(".mg")) {
                    array.add(new File(groupsDirectory, as[i]));
                }
            }

        }
        return array;
    }

    public Array getGroupIDs() {
        Array array;
        if (!allGroupsLoaded) {
            array = getGroupFileIDs();
            for (int i = 0; i < array.size(); i ++) {
                array.put(i, I18N.toNullEncoding((String) array.get(i)));
            }

        } else {
            array = new Array();
            Monitor monitor;
            for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements(); array.add(monitor.getProperty("_id"))) {
                monitor = (Monitor) enumeration.nextElement();
            }

        }
        return array;
    }

    public Array getGroupFileIDs() {
        Array array = new Array();
        File file = new File(Platform.getRoot() + "/groups");
        String as[] = file.list();
        for (int i = 0; i < as.length; i ++) {
            if (as[i].endsWith(".mg")) {
                int j = as[i].lastIndexOf(".mg");
                String s = as[i].substring(0, j);
                array.add(s);
            }
        }

        return array;
    }

    public void clearDispatcher() {
        if (DispatcherSitter.isDispatcherRunning()) {
            if (dispatcherSitter == null) {
                dispatcherSitter = new DispatcherSitter();
            }
            dispatcherSitter.kill();
            Platform.sleep(1000L);
        }
    }

    public void startDispatcher() {
        if (Platform.isWindows() && DispatcherSitter.isDispatcherRunning()) {
            return;
        } else {
            dispatcherSitter = new DispatcherSitter();
            dispatcherSitter.setPriority(8);
            dispatcherSitter.start();
            Platform.sleep(5000L);
            return;
        }
    }

    void loadAllGroups() {
        removeAllElements();
        Array array = getGroupFileIDs();
        String s;
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); loadGroup(s)) {
            s = I18N.toNullEncoding((String) enumeration.nextElement());
        }

    }

    public MonitorGroup loadGroup(String s) {
        message("Loading group: " + I18N.toDefaultEncoding(s));
        MonitorGroup monitorgroup = MonitorGroup.loadGroup(s, Platform.getRoot() + File.separator + "groups" + File.separator + I18N.toDefaultEncoding(s) + ".mg", false);
        if (monitorgroup != null) {
            addElement(monitorgroup);
            User.registerUsers(monitorgroup, I18N.toDefaultEncoding(monitorgroup.getProperty(pID)), monitorgroup.getMultipleValues("_user"), monitorgroup.getValuesTable());
        }
        return monitorgroup;
    }

    /**
     * 
     * 
     * @param array
     */
    protected void removeGroups(Array array) {
        Enumeration enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            File file = (File) enumeration.nextElement();
            String s = file.getName();
            if ("history.config".equals(file.getName())) {
                LogManager.log("Debug", "Removing history");
                removeHistory();
            } else if ("dynamic.config".equals(file.getName())) {
                LogManager.log("Debug", "Removing dynamic");
                removeDynamic();
            } else if ("multi.config".equals(s)) {
                Server.unloadServers();
            } else if ("users.config".equals(s)) {
                User.unloadUsers();
            } else if (!"master.config".equals(s)) {
                if (TEMPLATES_FILE.equals(s)) {
                    resetTemplateCache();
                } else {
                    FindGroupWithFileVisitor findgroupwithfilevisitor = new FindGroupWithFileVisitor(file);
                    acceptVisitor(findgroupwithfilevisitor);
                    if (findgroupwithfilevisitor.getResult() != null) {
                        LogManager.log("Debug", "Removing group for: " + file.getAbsolutePath());
                        MonitorGroup monitorgroup = (MonitorGroup) findgroupwithfilevisitor.getResult();
                        monitorgroup.saveDynamic();
                        monitorgroup.stopGroup();
                        User.unregisterUsers(monitorgroup.getProperty(pID));
                        monitorgroup.getOwner().removeElement(monitorgroup);
                    } else {
                        LogManager.log("Debug", "Could not find group for file: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private Enumeration addGroups(Array array) {
        Array array1 = new Array();
        Enumeration enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            File file = (File) enumeration.nextElement();
            String s = file.getName();
            if (s.equals("master.config")) {
                LogManager.log("RunMonitor", "Loading master.config");
                String s1 = getSetting("_httpPort");
                loadSettings();
                if (!s1.equals(getSetting("_httpPort")) && getSetting("_httpPort").length() > 0) {
                    startHTTPServer();
                }
                String s2 = getSetting("_httpSecurePort");
                if (!s2.equals(getSetting("_httpSecurePort")) && getSetting("_httpSecurePort").length() > 0) {
                    startHTTPSServer();
                }
            } else if (s.equals("history.config")) {
                LogManager.log("RunMonitor", "Loading history.config");
                loadHistory();
            } else if (s.equals("dynamic.config")) {
                LogManager.log("RunMonitor", "Loading dynamic.config");
                loadDynamicNoSchedule();
            } else if (s.equals("users.config")) {
                User.loadUsers();
            } else if (s.equals("multi.config")) {
                Server.loadServers();
            } else if (s.equals(TEMPLATES_FILE)) {
                resetTemplateCache();
            } else {
                LogManager.log("Debug", "Adding group for: " + file.getAbsolutePath());
                int i = s.lastIndexOf(".mg");
                String s3 = I18N.toNullEncoding(s.substring(0, i));
                MonitorGroup monitorgroup = loadGroup(s3);
                if (monitorgroup != null) {
                    array1.add(monitorgroup);
                }
            }
        }
        return  (Enumeration) array1.iterator();
    }

    public synchronized void adjustGroups(Array array, Array array1, Array array2, HashMap hashmap) {
        monitorScheduler.suspendScheduler();
        monitorsStarted = 0;
        removeGroups(array2);
        removeGroups(array1);
        Enumeration enumeration = addGroups(array1);
        Enumeration enumeration1 = addGroups(array);
        Collection collection = ConfigurationChanger.notifyAdjustedGroups(array, array1, array2);
        if (collection != null) {
            File file;
            for (Iterator iterator = collection.iterator(); iterator.hasNext(); hashmap.put(file.getAbsolutePath(), new Long(file.lastModified()))) {
                file = (File) iterator.next();
            }

        }
        MonitorGroup monitorgroup;
        for (; enumeration.hasMoreElements(); monitorgroup.startGroup()) {
            monitorgroup = (MonitorGroup) enumeration.nextElement();
        }

        MonitorGroup monitorgroup1;
        for (; enumeration1.hasMoreElements(); monitorgroup1.startGroup()) {
            monitorgroup1 = (MonitorGroup) enumeration1.nextElement();
        }

        if (array.size() > 0 || array1.size() > 0 || array2.size() > 0) {
            Monitor.clearGlobalDepends();
            groupsCache = new HashMap();
            writeAllHTML();
            if (!isStartingUp()) {
                createMonitorCountLog();
            }
        }
        monitorScheduler.resumeScheduler();
        allGroupsLoaded = true;
    }

    public MonitorGroup getGroup(String s) {
        MonitorGroup monitorgroup = (MonitorGroup) groupsCache.get(s);
        if (monitorgroup == null) {
            monitorgroup = (MonitorGroup) getElement(s);
            if (s != null && monitorgroup != null) {
                groupsCache.put(s, monitorgroup);
            }
        }
        return monitorgroup;
    }

    public void loadDynamicNoSchedule() {
        loadDynamic(false);
    }

    public void loadDynamic() {
        loadDynamic(true);
    }

    public void loadDynamic(HashMap hashmap) {
        VirtualMachine virtualmachine = VirtualMachine.createVirtualMachineObject(hashmap);
        addElement(virtualmachine);
        virtualmachine.schedule();
    }

    void loadDynamic(boolean flag) {
        String s = groupsDirectory.getAbsolutePath() + File.separator + "dynamic.config";
        file = new File(s);
        if (file.exists()) {
            try {
                Array array = FrameFile.readFromFile(s);
                Enumeration enumeration = (Enumeration) array.iterator();
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap = (HashMap) enumeration.nextElement();
                    VirtualMachine virtualmachine = VirtualMachine.createVirtualMachineObject(hashmap);
                    addElement(virtualmachine);
                    if (flag) {
                        virtualmachine.schedule();
                    }
                }
            } catch (IOException e) {
                message("Could not read dynamic.config file at:" + s);
            }
        }
    }

    public void removeDynamic() {
        VirtualMachine virtualmachine;
        for (Enumeration enumeration = getDynamics(); enumeration.hasMoreElements(); virtualmachine.getOwner().removeElement(virtualmachine)) {
            virtualmachine = (VirtualMachine) enumeration.nextElement();
            virtualmachine.unschedule();
        }

    }

    void loadHistoryNoSchedule() {
        loadHistory(false);
    }

    void loadHistory() {
        loadHistory(true);
    }

    void loadHistory(boolean flag) {
        String s = groupsDirectory.getAbsolutePath() + File.separator + "history.config";
        file = new File(s);
        if (file.exists()) {
            try {
                Array array = FrameFile.readFromFile(s);
                Enumeration enumeration = (Enumeration) array.iterator();
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap = (HashMap) enumeration.nextElement();
                    HistoryReport historyreport = HistoryReport.createHistoryReportObject(hashmap);
                    addElement(historyreport);
                    if (flag) {
                        historyreport.schedule();
                    }
                }
            } catch (IOException ioexception) {
                message("Could not read history.config file at:" + s);
            }
        }
    }

    void removeHistory() {
        HistoryReport historyreport;
        for (Enumeration enumeration = getReports(); enumeration.hasMoreElements(); historyreport.getOwner().removeElement(historyreport)) {
            historyreport = (HistoryReport) enumeration.nextElement();
            historyreport.unschedule();
        }

    }

    public synchronized SiteViewObject getElementByID(String s) {
        return super.getElementByID(s);
    }

    synchronized SiteViewObject getElement(String as[], int i) {
        if (as.length == 0) {
            return this;
        }
        I18N.test(as[0], 0);
        if (as[0].equalsIgnoreCase(I18N.toDefaultEncoding(getProperty(pID)))) {
            if (as.length == 1) {
                return this;
            }
            String as1[] = new String[as.length - 1];
            for (int j = 1; j < as.length; j ++) {
                as1[j - 1] = as[j];
            }

            as = as1;
        }
        SiteViewObject siteviewobject = super.getElement(as, 0);
        if (siteviewobject == null) {
            Object obj = getElementByID(as[0]);
            if (obj == null) {
                obj = loadGroup(I18N.toNullEncoding(as[0]));
            }
            siteviewobject = super.getElement(as, 0);
        }
        return siteviewobject;
    }

    public SiteViewObject resolveObjectReference(String s) {
        return getElement(s);
    }

    public static void updateStaticPages(String s) {
        SiteViewGroup siteviewgroup = currentSiteView();
        SiteViewObject siteviewobject = Portal.getSiteViewForID(s);
        s = CGI.getGroupIDRelative(s);
        MonitorGroup monitorgroup = null;
        if (siteviewgroup.internalServerActive()) {
            SiteViewGroup _tmp = siteviewgroup;
            SignalReload();
            monitorgroup = (MonitorGroup) siteviewobject.getElementByID(s);
        } else {
            monitorgroup = MonitorGroup.loadGroup(I18N.toNullEncoding(s), Platform.getRoot() + File.separator + "groups" + File.separator + s + ".mg");
        }
        if (siteviewobject instanceof PortalSiteView) {
            PortalSiteView portalsiteview = (PortalSiteView) siteviewobject;
            PortalSync.updateStaticPagesSiteView(portalsiteview, s);
        }
        if (monitorgroup != null) {
            monitorgroup.writeAllHTML();
        }
    }

    public static void updateStaticPages() {
        updateStaticPages((HTTPRequest) null);
    }

    public static void updateStaticPages(HTTPRequest httprequest) {
        SiteViewGroup siteviewgroup = currentSiteView();
        if (siteviewgroup.internalServerActive()) {
            SiteViewGroup _tmp = siteviewgroup;
            SignalReload();
        } else {
            siteviewgroup.loadAllGroups();
        }
        if (httprequest != null && CGI.isPortalServerRequest(httprequest)) {
            PortalSiteView portalsiteview = (PortalSiteView) Portal.getSiteViewForID(httprequest.getPortalServer());
            if (portalsiteview != null) {
                PortalSync.updateStaticPagesSiteView(portalsiteview);
            }
        }
        siteviewgroup.writeAllHTML();
    }

    public String htmlPath(HTTPRequest httprequest) {
        return Platform.getRoot() + "/" + httprequest.getAccountDirectory() + "/SiteView.html";
    }

    public void writeAllHTML() {
        super.writeAllHTML();
        if (shouldWriteHTML() && Platform.isUserAccessAllowed()) {
            String s = Platform.getRoot() + "/userhtml/Reports.html";
            try {
                PrintWriter printwriter = FileUtils.MakeOutputWriter(new FileOutputStream(s));
                try {
                    HTTPRequest httprequest = new HTTPRequest();
                    httprequest.setUser("user");
                    reportPage reportpage = new reportPage();
                    reportpage.request = httprequest;
                    reportpage.printBody(printwriter);
                } catch (Exception exception) {
                    printwriter.println("<hr>There was an unexpected problem:<br>" + exception.toString() + "<hr>");
                }
                printwriter.close();
                Platform.chmod(s, "rw");
            } catch (IOException ioexception) {
                message("Could not write file: " + s + ", " + ioexception);
            }
        }
    }

    public void printSpecial(String s, PrintWriter printwriter, HTTPRequest httprequest) throws Exception {
        if (s.indexOf("SiteView.html") != -1 && s.indexOf("/Detail") == -1) {
            if (httprequest.isSiteSeerAccount()) {
                accountPrefsPage.printForm(printwriter, httprequest);
            } else if (httprequest.isStandardAccount()) {
                printPage(printwriter, httprequest);
            } else {
                String s1 = httprequest.getAccount();
                MonitorGroup monitorgroup = (MonitorGroup) getElementByID(s1);
                if (monitorgroup == null) {
                    throw new HTTPRequestException(404);
                }
                monitorgroup.printPage(printwriter, httprequest);
            }
        } else if (s.indexOf("Reports.html") != -1 && s.indexOf("/Detail") == -1) {
            reportPage reportpage = new reportPage();
            reportpage.request = httprequest;
            reportpage.printBody(printwriter);
        } else if (s.indexOf("/logs/") != -1) {
            SiteViewLogReader.printLog(s, printwriter, httprequest);
        } else if (s.indexOf("htdocs/Reports-") != -1) {
            reportPage.printReportPage(printwriter, s, httprequest);
        } else if (s.indexOf("Progress.html") != -1 && s.indexOf("/Detail") == -1) {
            if (!httprequest.actionAllowed("_progress")) {
                throw new HTTPRequestException(557);
            }
            if (progressGenerator == null) {
                throw new HTTPRequestException(404);
            }
            progressGenerator.generate(printwriter, httprequest);
        } else {
            int i = s.indexOf("/Detail");
            int j = s.indexOf(".html");
            if (i == -1 || j == -1) {
                throw new HTTPRequestException(404);
            }
            String s2 = s.substring(i + "/Detail".length(), j);
            MonitorGroup monitorgroup1 = (MonitorGroup) getElementByID(s2);
            if (monitorgroup1 == null) {
                throw new HTTPRequestException(404);
            }
            monitorgroup1.printPage(printwriter, httprequest);
        }
    }

    boolean isParentInFilter(Array array, String s) {
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            if (CGI.isRelated(s, s1)) {
                return true;
            }
        }

        return false;
    }
    public Array getTopLevelMonitors(HTTPRequest httprequest) {
        Enumeration enumeration = getMonitors();
        Array array = new Array();
        Array array1 = CGI.getGroupFilterForAccount(httprequest);
        while (enumeration.hasMoreElements()) {
            Monitor monitor = (Monitor) enumeration.nextElement();
            String health = monitor.getProperty("_health");
            String parent = monitor.getProperty("_parent");
            if (array1.size() != 0 ? CGI.allowedByGroupFilter(I18N.toDefaultEncoding(monitor.getProperty(pID)), array1) && !isParentInFilter(array1, parent) : parent.length() == 0 && health.length() == 0) {
                array.add(monitor);
            }
        }
        Sorting.sort(array, new GreaterEqualMonitorName());
        Reversing.reverse(array);
        return array;
    }

    public String refreshTag(int i, String s) {
        if (i == 0) {
            return "";
        }
        if (stopping && i < 600) {
            i = 600;
        }
        return "<meta HTTP-EQUIV=\"Refresh\" CONTENT=\"" + i + "; URL=" + s + "\">\n";
    }

    private String fixNameLengths(MonitorGroup monitorgroup) {
        int i = getSettingAsLong("_maxWordLength", 8);
        i = Math.max(3, Math.min(50, i));
        int j = getSettingAsLong("_maxNumberGroupNameLines", 2);
        j = Math.max(1, Math.min(10, j));
        String s = monitorgroup.getProperty(pName);
        return s;
    }

    private String createNewTableRow(int i) {
        return "</tr>\n\n<tr><td><img src=/SiteView/htdocs/artwork/empty51.gif height=" + i + " width=1></td></tr><tr>";
    }

    private int printSiteSeerGauge(StringBuffer stringbuffer, HTTPRequest httprequest, boolean flag, int i, int j, String s) {
        if (!flag) {
            String s1 = getSetting("_siteseerAccount");
            if (s1.length() > 0 && getSetting("_siteseerDisabled").length() == 0) {
                if (i % j == 0) {
                    stringbuffer.append(createNewTableRow(35));
                }
                String s2 = overviewPage.fetchSiteSeerCategory(MasterConfig.getMasterConfig());
                String s4 = "";
                if (getSetting("_siteseerLoginInURL").length() > 0) {
                    String s5 = URLEncoder.encode(getSetting("_siteseerUsername"));
                    String s7 = URLEncoder.encode(getSetting("_siteseerPassword"));
                    if (s.equals("user")) {
                        String s9 = URLEncoder.encode(getSetting("_siteseerReadOnlyUsername"));
                        if (s9.length() > 0) {
                            s5 = s9;
                            s7 = URLEncoder.encode(getSetting("_siteseerReadOnlyPassword"));
                        }
                    }
                    s5 = TextUtils.replaceString(s5, "+", "%20");
                    s7 = TextUtils.replaceString(s7, "+", "%20");
                    s4 = s5 + ":" + s7 + "@";
                }
                String s6 = "http://" + s4 + getSetting("_siteseerHost") + "/SiteView/accounts/" + s1 + "/htdocs/SiteView.html?account=" + s1;
                String s8 = getSetting("_siteseerTitle");
                String s10 = "0";
                if (displayGauges) {
                    stringbuffer.append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=95>\n<tr><td valign=top colspan=2><a href=" + s6 + "><img border=0 src=" + getGaugeArt(s10) + " alt=" + s10 + " height=48 width=78></a></td></tr>\n"
                            + "<tr><td valign=top><a href=" + s6 + "><img border=0 src=" + getCategoryArt(s2) + " alt=" + getCategoryAlt(s2) + " height=21 width=20></a></td>\n" + "<td valign=top width=75><a href=" + s6 + " title=\"" + s8
                            + "\" style=\"color:#33AAFF\"><b>" + s8 + "</b></a></td></tr></table></td>\n");
                } else {
                    stringbuffer.append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=95>\n<tr><td valign=top><a href=" + s6 + "><img border=0 src=" + getCategoryArt(s2) + " alt=" + getCategoryAlt(s2)
                            + " height=21 width=20></a></td>\n" + "<td valign=top width=75><a href=" + s6 + " title=\"" + s8 + "\" style=\"color:#33AAFF\"><b>" + s8 + "</b></a></td></tr></table></td>\n");
                }
                i ++;
                stringbuffer.append(createNewTableRow(35));
                if (i % j > 0) {
                    i += j - i % j;
                }
            } else if (httprequest.actionAllowed("_preference")) {
                stringbuffer.append(createNewTableRow(35));
                if (i % j > 0) {
                    i += j - i % j;
                }
                String s3 = "";
                if (s1.length() == 0) {
                    s3 = "&operation=add";
                }
                stringbuffer
                        .append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=128>\n<tr><td valign=top><img src=/SiteView/htdocs/artwork/empty201.gif height=20 width=1></td>\n<td align=left valign=top width=96><a href=/SiteView/cgi/go.exe/SiteView?page=siteseerPrefs"
                                + s3 + "&account=" + httprequest.getAccount() + ">" + "<img src=/SiteView/htdocs/artwork/add_SSr.gif height=17 width=97 border=0></a></td></tr></table></td>\n\n");
                i ++;
            }
        }
        return i;
    }

    private int printCreateGroupGauge(StringBuffer stringbuffer, HTTPRequest httprequest, int i) {
        String s = "";
        Array array = CGI.getGroupFilterForAccount(httprequest);
        if (httprequest.actionAllowed("_groupEdit") && array.size() == 0) {
            s = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Add&account=" + httprequest.getAccount() + "><img src=/SiteView/htdocs/artwork/create_group.gif height=17 width=97 border=0></A>";
        }
        stringbuffer.append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=128>\n<tr><td valign=top width=128>" + s + "</td></tr></table></td>\n\n");
        s = " ";
        return ++ i;
    }

    public void printPage(PrintWriter printwriter, HTTPRequest httprequest) throws Exception 
    {	
        Array array = getTopLevelMonitors(httprequest);
        String s = httprequest.getAccount();
        if (array.size() == 0 && s.startsWith("login")) {
            Array array1 = CGI.getGroupFilterForAccount(httprequest);
            String s1 = (String) array1.get(0);
            MonitorGroup monitorgroup = (MonitorGroup) getElementByID(s1);
            if (monitorgroup == null) {
                throw new HTTPRequestException(404);
            } else {
                monitorgroup.printPage(printwriter, httprequest);
                return;
            }
        }
        int j = getSettingAsLong("_mainGaugesPerRow", 3);
        int k = getSettingAsLong("_mainRefreshRate", 60);
        boolean flag = getSetting("_siteseerHidden").length() > 0;
        displayGauges = getSetting("_displayGauges").equalsIgnoreCase("CHECKED");
        printwriter.println("<HTML>");
        CGI.printHeadTag(printwriter, Platform.productName, refreshTag(k, "SiteView.html"));
        String s2 = getSetting("_mainBodyTag");
        if (s2.length() == 0) {
            s2 = "<BODY link=#1155BB alink=#1155BB vlink=#1155BB>\n";
        }
        printwriter.println(s2);
        boolean flag1 = false;
        com.dragonflow.Page.CGI.menus menus1 = getNavItems(httprequest);
        if (flag1) {
            printwriter.println("<TABLE border=0 cellpadding=0 cellspacing=0 width=600>\n<TR><TD>");
            CGI.printButtonBar(printwriter, "GetStart.htm#viewer", "SiteView", httprequest, MasterConfig.getMasterConfig(), menus1, flag1);
            printwriter.println("</TD></TR></TABLE>\n");
        } else {
            CGI.printButtonBar(printwriter, "GetStart.htm#viewer", "SiteView", httprequest, MasterConfig.getMasterConfig(), menus1, flag1);
        }
        printwriter.println("<table border=0 cellspacing=0 width=600><tr><td valign=top colspan=4>\n");
        printwriter.println("<tr><td colspan=4><img align=left src=/SiteView/htdocs/artwork/empty51.gif height=40 width=75 border></td></tr>\n");
        printwriter.println("</table>\n");
        printwriter.println("<table border=0 cellspacing=0 width=600><tr>\n");
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<table align=center bgcolor=#000000 width=350><tr><td></td>");
        int l = 0;
        String s3 = Monitor.NODATA_CATEGORY;
        if (array.size() > 0) {
            MonitorGroup monitorgroup1 = (MonitorGroup) array.get(0);
            String s4 = monitorgroup1.getProperty(pName).trim();
            for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();) {
                if (l % j == 0) {
                    stringbuffer.append(createNewTableRow(35));
                }
                l ++;
                MonitorGroup monitorgroup2 = (MonitorGroup) enumeration.nextElement();
                String s8 = fixNameLengths(monitorgroup2);
                s8 = s8.trim();
                String s10 = monitorgroup2.getProperty(pName);
                s10 = s10.trim();
                String s12 = I18N.toDefaultEncoding(monitorgroup2.getProperty(pID));
                String s15 = monitorgroup2.getProperty(pCategory);
                String s16 = monitorgroup2.getProperty(pMeasurement);
                String s17 = "Detail" + s12 + ".html";
                s17 = HTTPRequest.encodeString(s17);
                s3 = Monitor.getWorstCategory(s3, s15);
                if (displayGauges) {
                    stringbuffer.append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=128>\n<tr><td valign=top colspan=2><a href=" + s17 + "><img border=0 src=" + getGaugeArt(s16) + " alt=" + s16
                            + " height=48 width=78></a></td></tr>\n" + "<tr><td valign=top><a href=" + s17 + ">" + monitorgroup2.progressArt() + "<img border=0 src=" + getCategoryArt(s15) + " alt=" + s15 + " height=21 width=20></a></td>\n"
                            + "<td valign=top width=105><a href=" + s17 + " title=\"" + s10 + "\"" + " style=\"color:#33AAFF\"><b>" + s8 + "</b></a></td></tr></table></td>\n");
                } else {
                    stringbuffer.append("<td valign=top><table cellpadding=1 border=0 cellspacing=0 width=128>\n<tr><td valign=top><a href=" + s17 + ">" + monitorgroup2.progressArt() + "<img border=0 src=" + getCategoryArt(s15) + " alt=" + s15
                            + " height=21 width=20></a></td>\n" + "<td valign=top width=105><a href=" + s17 + " title=\"" + s10 + "\"" + " style=\"color:#33AAFF\"><b>" + s8 + "</b></a></td></tr></table></td>\n");
                }
            }

            if (array.size() == 1 && s4.indexOf("Examples") != -1) {
                stringbuffer.append(createNewTableRow(35));
                stringbuffer.append("<td valign=top><table cellpadding=5 border=0 cellspacing=5 width=460>\n<tr><td valign=top><p style=\"color:#33AAFF\"><b>Only the \"" + s4.toString() + "\" group is currently defined.</b><br><br> "
                        + " To get started:<br>" + " 1. Click the <b>" + s4.toString() + "</b> group name to view the subgroups and monitors in this group<br>" + " 2. Click the <b>SiteView</b> button to return to this view<br>"
                        + " 3. Click the <b>Create Group</b> link to create more groups<br>" + " 4. Add monitors or subgroups to the new groups<br>" + "</td></tr>\n" + "</table></td>\n");
            }
        } else {
            stringbuffer.append(createNewTableRow(35));
//            if (!MAManager.isAttached()) {
//                stringbuffer
//                        .append("<td valign=top><table cellpadding=5 border=0 cellspacing=5 width=460>\n<tr><td valign=top><p style=\"color:#33AAFF\"><b>No groups or monitors are currently defined.</b><br><br>  To get started:<br> 1. Click the <b>Create Group</b> link to create a new group<br> 2. Add monitors or subgroups to the group<br> 3. Click the <b>SiteView</b> button to return to this view and create more groups</td></tr>\n</table></td>\n");
//            } else {
//                stringbuffer.append("<td valign=top><table cellpadding=5 border=0 cellspacing=5 width=460>\n<tr><td valign=top><p style=\"color:#33AAFF\"><b>No groups or monitors are currently defined.</b><br><br>  SiteView is being controlled by "
//                        + TopazInfo.getTopazName() + " Configuration Management.<br><br>" + " * Please use " + TopazInfo.getTopazName() + " Configuration Console to add new monitors or subgroups.<br>" + "</td></tr>\n" + "</table></td>\n");
//            }
        }
        int i = l;
        l = printSiteSeerGauge(stringbuffer, httprequest, flag, l, j, s);
        if (l == i) {
            stringbuffer.append(createNewTableRow(35));
        }
        l = printCreateGroupGauge(stringbuffer, httprequest, l);
        StringBuffer stringbuffer1 = new StringBuffer();
        stringbuffer1
                .append("<!-- * * * * * *  * * * * * * * Start of Dashboard BORDER CONTAINER Tables * * * * * * *   -->\n<table border=0 cellpadding=0 cellspacing=0 align=center><tr><td rowspan=8 colspan=8 valign=bottom align=right><img src=/SiteView/htdocs/artwork/modUpperLeft.gif border=0></td><td><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td><td rowspan=8 colspan=8 valign=bottom align=left><img src=/SiteView/htdocs/artwork/modUpperRight.gif border=0></td></tr>\n<tr><td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=3 width=78 border=0></td></tr>\n<tr><td bgcolor=#787878><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n<tr><td bgcolor=#9A9A9A><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n");
        String mainPageTitle = getSetting("_mainPageTitle");
        if (mainPageTitle.length() <= 0) {
            mainPageTitle = "Main View";
        }
        String s6 = "";
        if (httprequest.actionAllowed("_alertEdit") && httprequest.actionAllowed("_alertList") && getSetting("_alertIconLink").length() > 0) {
            Array array2 = new Array();
            getGroupAlerts("SiteView", array2);
            s6 = "<td valign=\"middle\" align=\"right\"><p style=\"color: #a5a5a5; font-size:8pt; font-weight: bold; font-family: 'Arial', 'Helvetica', sans-serif;\">Global Alerts: </td>" 
            + printAlertIconLink(httprequest, "_master", "_config", array2);
        }
        stringbuffer1
                .append("<tr><td>\n<table bgcolor=\"#5A5A5A\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" ><tr><td bgcolor=#5A5A5A><img src=SiteView/htdocs/artwork/spacer11.gif height=32 width=1 border=0></td><td align=\"left\"><img border=0 src=\""
                        + getCategoryArt(s3)
                        + "\" alt=\"worst reported status: "
                        + s3
                        + "\" height=21 width=20></td>"
                        + "<td valign=\"middle\">"
                        + "<p style=\"color: #a5a5a5; font-weight: bold; font-family: 'Arial', 'Helvetica', sans-serif;\">"
                        + mainPageTitle
                        + "</td> " + s6 + "</tr>\n" + "</table>\n</td></tr>\n");
        stringbuffer1
                .append("\n<tr><td bgcolor=#3F3F3F><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n<tr><td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=5 width=78 border=0></td></tr>\n<tr><td bgcolor=#000000><img src=/SiteView/htdocs/artwork/spacer11.gif height=15 width=78 border=0></td></tr>\n<!-- start the middle section -->\n<!-- left hand side columns -->\n<tr>    <td width=1><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=1 border=0></td>        <td bgcolor=#4F4F4F><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=4 border=0></td>        <td bgcolor=#9A9A9A><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=2 border=0></td>        <td bgcolor=#787878><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=2 border=0></td>        <td bgcolor=#5A5A5A><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=20 border=0></td>    <td bgcolor=#3F3F3F><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=5 border=0></td>    <td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=3 border=0></td>        <td bgcolor=#000000><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=15 border=0></td>    <td align=\"center\" bgcolor=\"#000000\">");
        printwriter.println(stringbuffer1.toString());
        printwriter.println(stringbuffer.toString());
        printwriter.println("</tr>\n</table>\n");
        printwriter
                .println("\t<!-- End of the DASHBOARD TABLE close the BORDER tables -->    </td>    <!-- right hand side table -->        <td bgcolor=#000000><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=15 border=0></td>        <td bgcolor=#4F4F4F><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=4 border=0></td>        <td bgcolor=#8A8A8A><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=1 border=0></td>        <td bgcolor=#787878><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=2 border=0></td>        <td bgcolor=#5A5A5A><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=18 border=0></td>    <td bgcolor=#3F3F3F><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=4 border=0></td>    <td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=3 border=0></td>        <td><img src=/SiteView/htdocs/artwork/spacer11.gif height=79 width=1 border=0></td></tr>\n<tr><td rowspan=10 colspan=8 valign=top align=right><img src=/SiteView/htdocs/artwork/modLowerLeft.gif border=0></td><td bgcolor=#000000><img src=/SiteView/htdocs/artwork/spacer11.gif height=16 width=79 border=0></td><td rowspan=10 colspan=8 valign=top align=left><img src=/SiteView/htdocs/artwork/modLowerRight.gif border=0></td></tr>\n<tr><td bgcolor=#474747><img src=/SiteView/htdocs/artwork/spacer11.gif height=4 width=78 border=0></td></tr>\n<tr><td bgcolor=#787878><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n<tr><td bgcolor=#9A9A9A><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n<tr><td bgcolor=#787878><img src=/SiteView/htdocs/artwork/spacer11.gif height=2 width=78 border=0></td></tr>\n<tr><td bgcolor=#5A5A5A valign=top><img src=/SiteView/htdocs/artwork/spacer11.gif height=24 width=78 border=0></td></tr>\n<tr><td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=2 width=78 border=0></td></tr>\n<tr><td bgcolor=#3F3F3F><img src=/SiteView/htdocs/artwork/spacer11.gif height=5 width=78 border=0></td></tr>\n<tr><td bgcolor=#303030><img src=/SiteView/htdocs/artwork/spacer11.gif height=2 width=78 border=0></td></tr>\n<tr><td><img src=/SiteView/htdocs/artwork/spacer11.gif height=1 width=78 border=0></td></tr>\n</table>\n");
        String s7 = I18N.toNullEncoding(TextUtils.prettyDate());
        String s9 = getSetting("_mainHTML");
        String s11 = "";
        if (Platform.isDemo()) {
            s11 = "<TR><TD><p class=license align=center><a href=/SiteView/cgi/go.exe/SiteView?page=demo&account=" + httprequest.getAccount() + ">" + LocaleUtils.getResourceBundle().getString("ResetDemo") + "</a></p></td></tr>\n";
        } else if (httprequest.actionAllowed("_license")) {
            String s13 = LUtils.getLicenseKey();
            if (s13.length() == 0 && Platform.isDemo()) {
                s13 = LUtils.generateLicenseKey(25, 1, 99, 10);
            }
            s11 = "\n<TR><TD><p class=license align=center>" + LUtils.getLicenseSummary() + "</p></TD></TR>\n";
            if (s13.startsWith("PE")) {
                s11 = s11
                        + "<TR><TD><table><tr><td><p class=license align=center>To upgrade to a standard version of SiteView, click <a href=\"http://www.dragonflow.com/us/company/corporate-info/contact-us/\">here.</a></td></tr><tr><td><p class=license align=center>If your license has expired, contact SiteView customer support <a href=\"http://support.dragonflow.com\">here</a> to get a new Personal Edition license.</td></tr></table></TD></TR>";
            }
        }
        String s14 = "";
        if (httprequest.actionAllowed("_progress")) {
            s14 = "<TR><TD><p class=progress align=center><b><a href=Progress.html?hideState=true>" + progressSummary + "</a></b></p></td></tr>\n";
        }
        printwriter.print("<img src=/SiteView/htdocs/artwork/empty651.gif height=45 width=1><TABLE class=layout align=center border=0 cellpadding=12 cellspacing=0 width=600>" + s9 + s11 + s14 + "<TR><TD><p class=fine align=CENTER>" + "<font size=-1>"
                + Platform.refreshLabel(k) + LocaleUtils.getResourceBundle().getString("LastRefresh") + " " + s7 + "</font></p></td></tr>\n");
        printwriter.print("<TR><TD><p class=fine align=center>" + LocaleUtils.getResourceBundle().getString("FindOutWhatsInThisRelease") + " <a href=\"/SiteView/ReleaseNotes.htm\" TARGET=Help>"
                + LocaleUtils.getResourceBundle().getString("GoToTheReleaseNotes") + "</a></p></td></tr>\n");
        printwriter.print("</TABLE>");
        printCategoryInsertHTML(s3, this, printwriter);
        CGI.printFooter(printwriter, httprequest, true);
        printwriter.flush();
    }

    public String licenseParagraph(HTTPRequest httprequest) {
        String s = "";
        if (httprequest.getAccount().equals("administrator")) {
            int i = LUtils.getLicenseType(MasterConfig.getMasterConfig());
            if (i == 0 || i == 1) {
                String s1 = LUtils.getLicenseSummary(MasterConfig.getMasterConfig(), true);
                s = "<hr><CENTER>" + s1 + "<br>Order <a href=" + Platform.homeURLPrefix + "/OrderOpts.htm>Online</a> or send e-mail to <a href=mailto:" + Platform.salesEmail + ">" + Platform.salesEmail + "</a></a>" + "</CENTER><hr>";
            }
        }
        return s;
    }

    public int getGroupCount() {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            enumeration.nextElement();
            i ++;
        }

        return i;
    }

    public int getAIMsCount() {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup) && monitor.getClassPropertyString("classType").equals("application")) {
                    i ++;
                }
            }
        }

        return i;
    }

    public int getMonitorCount() {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup) && !monitor.isDisabled()) {
                    i ++;
                }
            }
        }

        return i;
    }

    public void notifyDispatcherAtStartup() {
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup) && !monitor.isDisabled() && (monitor instanceof DispatcherMonitor) && ((DispatcherMonitor) monitor).isDispatcher()) {
                    if (getDispatcherMonitorInstanceCount(monitor) == 1) {
                        DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.INIT_MON, (DispatcherMonitor) monitor);
                    }
                    DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.INIT_CONN, (DispatcherMonitor) monitor);
                }
            }
        }

    }

    public void notifyMonitorDisable(AtomicMonitor atomicmonitor) {
        checkDispatcherForClear(atomicmonitor);
        atomicmonitor.notifyDisable();
    }

    public void notifyMonitorDeletion(AtomicMonitor atomicmonitor) {
        checkDispatcherForClear(atomicmonitor);
        atomicmonitor.notifyDelete();
    }

    private void checkDispatcherForClear(AtomicMonitor atomicmonitor) {
        if (!atomicmonitor.isDispatcher()) {
            return;
        }
        DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.TERMINATE_CONN, atomicmonitor);
        if (getDispatcherMonitorInstanceCount(atomicmonitor) - 1 <= 0) {
            DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.TERMINATE_MON, atomicmonitor);
        }
        if (getDispatcherMonitorCount() - 1 <= 0) {
            clearDispatcher();
        }
    }

    public void checkDispatcherForStart(AtomicMonitor atomicmonitor) {
        if (!atomicmonitor.isDispatcher()) {
            return;
        }
        if (getDispatcherMonitorCount() + 1 > 0) {
            startDispatcher();
        }
        if (getDispatcherMonitorInstanceCount(atomicmonitor) == 0) {
            DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.INIT_MON, atomicmonitor);
        }
    }

    public int getDispatcherMonitorInstanceCount(Monitor monitor) {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor1 = (Monitor) enumeration1.nextElement();
                if (!(monitor1 instanceof SubGroup) && (monitor1 instanceof DispatcherMonitor) && ((DispatcherMonitor) monitor1).isDispatcher() && !monitor1.isDisabled() && monitor1.getProperty("_class").equals(monitor.getProperty("_class"))) {
                    i ++;
                }
            }
        }

        return i;
    }

    public int getDispatcherMonitorCount() {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup) && (monitor instanceof AtomicMonitor) && ((AtomicMonitor) monitor).isDispatcher() && !monitor.isDisabled()) {
                    i ++;
                }
            }
        }

        return i;
    }

    public int getDisabledMonitorCount() {
        int i = 0;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if ((monitor instanceof AtomicMonitor) && monitor.isDisabled()) {
                    i ++;
                }
            }
        }

        return i;
    }

    public int getReportCount() {
        int i = 0;
        for (Enumeration enumeration = getReports(); enumeration.hasMoreElements();) {
            enumeration.nextElement();
            i ++;
        }

        return i;
    }

    public int getAlertCount() {
        int i = 0;
        i += getElementsOfClass("com.dragonflow.SiteView.Rule", false, false).size();
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            i += monitorgroup.getElementsOfClass("com.dragonflow.SiteView.Rule", false, false).size();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                i += monitor.getElementsOfClass("com.dragonflow.SiteView.Rule", false, false).size();
            }
        }

        return i;
    }

    public String simpleMail(String s, String s1, String s2) {
        return simpleMail(s, s1, s2, "");
    }

    public String simpleMail(String s, String s1, String s2, String s3) {
        return MailUtils.mail(MasterConfig.getMasterConfig(), s, s1, s2, s3);
    }

    public void setProgressSummary(String s) {
        progressSummary = s;
    }

    public int checkAuthorization(HTTPRequest httprequest) throws HTTPRequestException {
        String s = httprequest.getURL();
        if (s.startsWith("/SiteView/htdocs/artwork/")) {
            return 200;
        }
        if (s.startsWith("/SiteView/templates.sound")) {
            return 200;
        }
        if (s.startsWith("/SiteView/htdocs/templates.view/")) {
            return 200;
        }
        if (s.startsWith("/SiteView/htdocs/js/")) {
            return 200;
        }
        if (httprequest.getValue("page").equals("login")) {
            return 200;
        }
        if (httprequest.getValue("page").equals("xmlApi")) {
            return 200;
        }
        if (Platform.isPortal() && httprequest.getPortalServer().length() > 0) {
            PortalSiteView portalsiteview = (PortalSiteView) Portal.getSiteViewForServerID(httprequest.getPortalServer());
            if (portalsiteview == null) {
                throw new HTTPRequestException(557);
            }
            if (portalsiteview.getProperty(PortalSiteView.pReadOnly).length() > 0) {
                throw new HTTPRequestException(557);
            }
        }
        if (s.startsWith("/SiteView/docs/")) {
            return 200;
        }
        if (s.equals("/SiteView/docs/UGtoc.htm") || s.equals("/SiteView/README.htm") || s.equals("/SiteView/ReleaseNotes.htm") || s.equals("/SiteView/docs/UserGuide.htm")) {
            return 200;
        }
        if (s.startsWith("/SiteView/classes/COM/dragonflow/Chart/")) {
            return 200;
        }
        if (httprequest.getValue("page").equals("SiteSeer")) {
            return 200;
        }
        if (s.equals("/SiteView/license.txt")) {
            return 200;
        }
        if (s.equals("/SiteView/license.html")) {
            return 200;
        }
		setupUser(httprequest);
        boolean flag = checkIP(httprequest);
        boolean flag1 = checkUser(httprequest);
        boolean flag2;
        if (getSetting("_checkAddressAndLogin").length() > 0) {
            flag2 = flag && flag1;
        } else {
            flag2 = flag || flag1;
        }
        if (!flag2 && !flag1) {
            return 401;
        }
        if (!flag2 && !flag) {
            return 403;
        }
        User user = httprequest.user;
        if (user == null) {
            return 556;
        }
        return user.getProperty("_disabled").length() <= 0 ? 200 : 555;
    }

    public void restartHAScheduler() {
    }

    private void doHA() {
        if (!LUtils.isHighAvailabilityLicense()) {
            return;
        }
        MirrorConfiguration mirrorconfiguration = new MirrorConfiguration();
        try {
            LogManager.registerLogger("Mirror", new FileLogger(Platform.getRoot() + File.separator + "logs" + File.separator + "mirror.log", TextUtils.toInt(getSetting("_maxAuxLogSize"))));
        } catch (Exception exception) {
        }
        if (mirrorconfiguration != null) {
            mirrorconfiguration.execute();
        }
    }

    public static void setupUser(HTTPRequest httprequest) {
        if (httprequest.getAccount().length() > 0) {
            httprequest.setUser(httprequest.getAccount());
        } else {
            httprequest.setUser("administrator");
        }
    }

    boolean checkUser(HTTPRequest httprequest) {
        String s = httprequest.getAccount();
        if (s.equals("administrator")) {
            String s1 = getSetting("_httpAdminRealm");
            if (s1.length() == 0) {
                s1 = "SiteView Administrator";
            }
            httprequest.setRealm(s1);
        } else if (s.equals("user")) {
            String s2 = getSetting("_httpUserRealm");
            if (s2.length() == 0) {
                s2 = "SiteView User";
            }
            httprequest.setRealm(s2);
        } else {
            httprequest.setRealm("SiteView Account " + s);
        }
        Enumeration enumeration = User.getUsersForAccount(s);
        if (enumeration.hasMoreElements()) {
            while (enumeration.hasMoreElements()) {
                User user = (User) enumeration.nextElement();
                if (user != null && user.authenticate(httprequest)) {
                    httprequest.setUser(user);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    boolean checkIP(HTTPRequest httprequest) {
        String s = httprequest.getRemoteAddress();
        if (httprequest.getValue("page").equals("SiteSeer")) {
            return true;
        }
        String s1 = getSetting("_authorizedIP");
        if (s1.length() == 0) {
            return false;
        }
        String as[] = TextUtils.split(s1, ",");
        for (int i = 0; i < as.length; i ++) {
            int j = as[i].indexOf("*");
            if (j >= 0) {
                as[i] = as[i].substring(0, j);
            }
            if (s.startsWith(as[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * 
     * @return
     */
    public int createMonitorCountLog() {
        if (Platform.isPortal()) {
            return 0;
        }
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        boolean flag = false;
        Hashtable hashtable = new Hashtable();
        Hashtable hashtable1 = new Hashtable();
        SiteViewGroup siteviewgroup = currentSiteView();
        Array array = getGroupIDs();
        label0: for (int j1 = 0; j1 < array.size(); j1 ++) {
            String s1 = I18N.toDefaultEncoding((String) array.get(j1));
            MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(s1);
            if (monitorgroup == null) {
                continue;
            }
            Enumeration enumeration = monitorgroup.getMonitors();
            // TODO need review
            do {
                Monitor monitor;
                do {
                    int k1;
                    String s5;
                    do {
                        if (!enumeration.hasMoreElements()) {
                            continue label0;
                        }
                        monitor = (Monitor) enumeration.nextElement();
                        k1 = 1;
                        s5 = (String) monitor.getClassProperty("class");
                    } while (s5 == null);
                    i ++;
                    int l1 = monitor.getCostInLicensePoints();
                    j += l1;
                    int i1;
                    if (hashtable1.containsKey(s5)) {
                        int i2 = ((Integer) hashtable1.get(s5)).intValue();
                        i1 = l1 + i2;
                    } else {
                        i1 = l1;
                    }
                    if (hashtable.containsKey(s5)) {
                        k1 = ((Integer) hashtable.get(s5)).intValue();
                        k1 ++;
                    }
                    hashtable.put(s5, new Integer(k1));
                    hashtable1.put(s5, new Integer(i1));
                    if (monitor instanceof URLSequenceMonitor) {
                        int j2 = monitor.getCostInLicensePoints();
                        k += j2;
                    }
                } while (!(monitor instanceof URLListMonitor));
                int k2 = ((URLListMonitor) monitor).getNumberOfUrls();
                l += k2;
            } while (true);
        }

        String s = "monitorCount.log";
        String s2 = Platform.getRoot() + File.separator + "logs" + File.separator + s;
        PrintWriter printwriter = null;
        try {
            if (s2 != null) {
                printwriter = FileUtils.MakeOutputWriter(new FileOutputStream(s2));
                printwriter.println("START");
                String s3;
                try {
                    InetAddress inetaddress = InetAddress.getLocalHost();
                    s3 = inetaddress.getHostName();
                } catch (UnknownHostException unknownhostexception) {
                    s3 = "localhost";
                }
                printwriter.println("Server: " + s3);
                Date date = new Date();
                String s4 = date.toString();
                printwriter.println("Date: " + s4);
                printwriter.println("Total monitors: " + i);
                printwriter.println("Total URLSequence Steps: " + k);
                printwriter.println("Total URLList Items: " + l);
                printwriter.println("Total Points Used: " + j);
                printwriter.println("");
                HashMap hashmap = MasterConfig.getMasterConfig();
                String s7 = (String) hashmap.get("_extendedMonitorCountLog");
                String s6;
                if (s7 != null && s7.length() > 0) {
                    Array array1 = monitorPage.getMonitorClasses();
                    Array array2 = sortClassList(array1);
                    s6 = s4 + ": ";
                    for (int l2 = 0; l2 < array1.size(); l2 ++) {
                        String s9 = (String) array2.get(l2);
                        Integer integer = (Integer) hashtable.get(s9);
                        Integer integer1 = (Integer) hashtable1.get(s9);
                        String s11 = TextUtils.numberToString(0);
                        String s13 = TextUtils.numberToString(0);
                        if (integer != null) {
                            s11 = TextUtils.numberToString(integer.intValue());
                        }
                        if (integer1 != null) {
                            s13 = TextUtils.numberToString(integer1.intValue());
                        }
                        printwriter.println(s9 + ": " + s11 + " Monitors, " + s13 + " Points");
                        s6 = s6 + s9.charAt(0);
                        s6 = s6 + s11;
                    }

                } else {
                    s6 = s4 + ": ";
                    Enumeration enumeration1 = hashtable.keys();
                    for (Enumeration enumeration2 = hashtable.elements(); enumeration1.hasMoreElements() && enumeration2.hasMoreElements();) {
                        String s8 = (String) enumeration1.nextElement();
                        int i3 = ((Integer) enumeration2.nextElement()).intValue();
                        String s10 = TextUtils.numberToString(i3);
                        Integer integer2 = (Integer) hashtable1.get(s8);
                        String s12 = TextUtils.numberToString(0);
                        if (integer2 != null) {
                            s12 = TextUtils.numberToString(integer2.intValue());
                        }
                        printwriter.println(s8 + ": " + s10 + " Monitors, " + s12 + " Points");
                        s6 = s6 + s8.charAt(0);
                        s6 = s6 + s10;
                    }

                }
                s6 = TextUtils.obscure(s6);
                printwriter.println("");
                printwriter.println(s6);
            }
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error writing " + s2 + ": " + ioexception.getMessage());
            ioexception.printStackTrace();
        } finally {
            if (printwriter != null) {
                printwriter.println("END");
                printwriter.flush();
                printwriter.close();
            }
        }
        totalPointsUsed = j;
        return i;
    }

    /**
     * 
     * 
     * @param array
     * @return
     */
    private Array sortClassList(Array array) {
        Array array1 = new Array();
        boolean flag = false;
        Enumeration enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            Class class1 = (Class) enumeration.nextElement();
            try {
                AtomicMonitor atomicmonitor = (AtomicMonitor) class1.newInstance();
                String s = (String) atomicmonitor.getClassProperty("class");
                if (s != null && s.length() > 0) {
                    int i = 0;
                    while (i < array1.size()) {
                        String s1 = (String) array1.get(i);
                        if (s.toLowerCase().compareTo(s1.toLowerCase()) <= 0) {
                            array1.insert(i,i, s);
                            break;
                        }
                        i ++;
                    }
                    if (i == array1.size()) {
                        array1.add(s);
                    }
                }
            } catch (Exception exception) {
            }
        }
        return array1;
    }

    public static HashMap loadRTConfig() {
        return loadRTConfig((HashMap) MasterConfig.getMasterConfig().clone());
    }

    public static HashMap loadRTConfig(HashMap hashmap) {
        HashMap hashmap1 = new HashMap();
        if (Platform.customConfigPath.length() > 0) {
            Array array = null;
            try {
                array = FrameFile.readFromFile(Platform.customConfigPath);
            } catch (Exception exception) {
            }
            if (array != null && array.size() > 0) {
                hashmap1 = (HashMap) array.get(0);
            }
            for (HashMapIterator hashmapiterator = hashmap1.begin(); !hashmapiterator.atEnd(); hashmapiterator.advance()) {
                hashmap.put(hashmapiterator.key(), hashmapiterator.value());
            }

        }
        return hashmap;
    }

    /**
     * 
     * 
     * @return
     */
    public Vector getTopLevelGroups() {
        Enumeration enumeration = getElements();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof MonitorGroup) {
                boolean flag = ((MonitorGroup) obj).isHealthGroup();
                if (((MonitorGroup) obj).isTopLevelGroup() && !flag) {
                    vector.add(obj);
                }
            }
        }
        return vector;
    }

    /**
     * 
     * 
     * @return
     */
    public Vector getAtomicMonitors() {
        Enumeration enumeration = getElements();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof AtomicMonitor) {
                vector.add(obj);
            }
        }
        return vector;
    }

    public void stopTopazConfiguratorScheduler() {
        if (topazSyncScheduler != null) {
            topazSyncScheduler.stopScheduler();
            topazSyncScheduler = null;
        }
    }

    public static void getGroupsMonitors(Vector vector, Vector vector1, boolean flag) {
        Vector vector2 = currentSiteView().getTopLevelGroups();
        ConfigurationChanger.getGroupsMonitors(vector2, vector, vector1, flag);
        if (vector1 != null) {
            vector1.addAll(vector2);
        }
    }

    static {
        $assertionsDisabled = !(com.dragonflow.SiteView.SiteViewGroup.class).desiredAssertionStatus();
        pWebserverAddress = new StringProperty("_webserverAddress", "localhost");
        pAdminURL = new StringProperty("_adminURL", "http://localhost:8888/SiteView");
        StringProperty astringproperty[] = { pWebserverAddress, pAdminURL };
        addProperties("com.dragonflow.SiteView.SiteViewGroup", astringproperty);
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (hashmap.get("_killProcesses") != null && ((String) hashmap.get("_killProcesses")).length() > 0) {
            killProcessesIsSet = true;
        } else {
            killProcessesIsSet = false;
        }
        if (hashmap.get("_doNotkillProcesses") != null) {
            doNotKillProcesses = (String) hashmap.get("_doNotkillProcesses");
        } else {
            doNotKillProcesses = "";
        }
    }
}
