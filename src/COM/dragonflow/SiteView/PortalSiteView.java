/*
 * 
 * Created on 2005-2-16 16:19:34
 *
 * PortalSiteView.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PortalSiteView</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// MonitorGroup, Monitor, SubGroup, Portal,
// Platform, SiteViewGroup

public class PortalSiteView extends MonitorGroup {

    public static StringProperty pServer;

    public static StringProperty pTitle;

    public static StringProperty pUserName;

    public static StringProperty pPassword;

    public static StringProperty pProxy;

    public static StringProperty pProxyUserName;

    public static StringProperty pProxyPassword;

    public static StringProperty pTimeout;

    public static StringProperty pLoginAccount;

    public static StringProperty pDisabled;

    public static StringProperty pReadOnly;

    public static StringProperty pTimezoneOffsetFromPortal;

    public static StringProperty pLastSiteviewUpdate = new StringProperty(
            "_lastUpdate");

    public static StringProperty pConnectState;

    public static StringProperty pLogCollectorRefresh;

    public static StringProperty pPlatformOS;

    public static StringProperty pPlatformVersion;

    String rootPath;

    public Object logCollectorLock;

    public String logCollectorStatus;

    public long logCollectorTimestamp;

    public boolean downloadInProgress;

    HashMap masterConfig;

    long masterConfigLastModified;

    long templatesConfigLastModified;

    private static String CLASS_MATCH_REGEX = "/class=([a-zA-Z]+)/s";

    private HashMap monitorClassFilterCache;

    private HashMap actionClassFilterCache;

    public PortalSiteView() {
        rootPath = "";
        logCollectorLock = new Object();
        logCollectorStatus = "log collector starting...";
        logCollectorTimestamp = 0L;
        downloadInProgress = false;
        masterConfig = null;
        masterConfigLastModified = 0L;
        templatesConfigLastModified = 0L;
        monitorClassFilterCache = null;
        actionClassFilterCache = null;
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        rootPath = Portal.getPortalSiteViewRootPath(getProperty(pID));
    }

    void loadAllGroups() {
        removeAllElements();
        Array array = getGroupFileIDs();
        for (int i = 0; i < array.size(); i++) {
            String s = (String) array.at(i);
            loadGroup(s);
        }

    }

    MonitorGroup loadGroup(String s) {
        String s1 = rootPath + File.separator + "groups" + File.separator + s
                + ".mg";
        LogManager.log("RunMonitor", "loading portal group: "
                + getProperty(pID) + ":" + s);
        MonitorGroup monitorgroup = MonitorGroup.loadGroup(s, s1);
        if (monitorgroup != null) {
            File file = new File(s1);
            monitorgroup.setProperty("groupFileLastModified", ""
                    + file.lastModified());
            addElement(monitorgroup);
            monitorgroup.startGroup();
        }
        return monitorgroup;
    }

    void removeGroup(String s) {
        SiteViewObject siteviewobject = getElement(s);
        if (siteviewobject != null) {
            removeElement(siteviewobject);
        }
    }

    public boolean isSiteSeerServer() {
        String s = "/siteseer[\\d]*\\.Dragonflow\\.com/i";
        return TextUtils.match(getProperty(pServer), s);
    }

    public Array getTopLevelGroups() {
        return getGroups(false);
    }

    public Array getGroups() {
        return getGroups(true);
    }

    public HashMap getMasterConfig() {
        if (masterConfig == null) {
            masterConfig = loadMasterConfig();
        }
        return masterConfig;
    }

    public long getMasterConfigLastModified() {
        if (masterConfigLastModified == 0L) {
            getMasterConfig();
        }
        return masterConfigLastModified;
    }

    public void clearConfigCache() {
        masterConfig = null;
    }

    public HashMap loadMasterConfig() {
        HashMap hashmap = new HashMap();
        try {
            String s = rootPath + File.separator + "groups" + File.separator
                    + "master.config";
            File file = new File(s);
            if (file.exists()) {
                masterConfigLastModified = file.lastModified();
                Array array = FrameFile.readFromFile(s);
                hashmap = (HashMap) array.front();
            }
            LogManager.log("RunMonitor", "loading portal master.config: "
                    + getProperty(pID));
        } catch (Exception exception) {
            LogManager.log("Error", "error loading portal master.config: "
                    + getProperty(pID) + ", " + exception);
            LogManager.log("RunMonitor", "error loading portal master.config: "
                    + getProperty(pID) + ", " + exception);
        }
        return hashmap;
    }

    public void saveMasterConfig() {
        String s = rootPath + File.separator + "groups" + File.separator
                + "master.config";
        try {
            Array array = new Array();
            array.add(getMasterConfig());
            FrameFile.writeToFile(s, array, "_", true, true);
            Platform.chmod(s, "rw");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Could not write " + getProperty(pID)
                    + " master.config file at:" + s);
        }
    }

    public long getTemplatesConfigLastModified() {
        if (masterConfigLastModified == 0L) {
            String s = rootPath + File.separator + "groups" + File.separator
                    + "templates.config";
            File file = new File(s);
            if (file.exists()) {
                templatesConfigLastModified = file.lastModified();
            }
        }
        return templatesConfigLastModified;
    }

    public HashMap getMachineEntry(String s) {
        HashMap hashmap = getMasterConfig();
        for (Enumeration enumeration = hashmap.values("_remoteMachine"); enumeration
                .hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            HashMap hashmap1 = TextUtils.stringToHashMap(s1);
            if (s.equals(TextUtils.getValue(hashmap1, "_id"))) {
                return hashmap1;
            }
        }

        return null;
    }

    public int getMonitorCount() {
        int i = 0;
        for (Enumeration enumeration = getElements(); enumeration
                .hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration
                    .nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup) && !monitor.isDisabled()) {
                    i++;
                }
            }
        }

        return i;
    }

    public int getDisabledMonitorCount() {
        int i = 0;
        for (Enumeration enumeration = getElements(); enumeration
                .hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration
                    .nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (monitor.isDisabled()) {
                    i++;
                }
            }
        }

        return i;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param flag
     * @return
     */
    public Array getGroups(boolean flag) {
        Array array = new Array();
        Enumeration enumeration = getElements();
        while (enumeration.hasMoreElements()) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration
                    .nextElement();
            if (flag
                    || monitorgroup.getProperty(MonitorGroup.pParent).length() == 0) {
                array.add(monitorgroup);
            }
        }
        return array;
    }

    public Array getGroupFileIDs() {
        File file = new File(rootPath + File.separator + "groups");
        Array array = new Array();
        if (file.exists()) {
            String as[] = file.list();
            for (int i = 0; i < as.length; i++) {
                if (as[i].endsWith(".mg")) {
                    int j = as[i].lastIndexOf(".mg");
                    String s = as[i].substring(0, j);
                    array.add(s);
                }
            }

        }
        return array;
    }

    public String getFullID() {
        return getProperty(pID) + Portal.PORTAL_ID_SEPARATOR;
    }

    public HashMap getActionClassFilter() {
        if (actionClassFilterCache == null) {
            actionClassFilterCache = getClassFilter("actionClasses");
        }
        return actionClassFilterCache;
    }

    public HashMap getMonitorClassFilter() {
        if (monitorClassFilterCache == null) {
            monitorClassFilterCache = getClassFilter("monitorClasses");
        }
        return monitorClassFilterCache;
    }

    private HashMap getClassFilter(String s) {
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=remoteOp&account=administrator&operation="
                + s;
        Array array = sendURLToRemoteSiteView(s1, null);
        HashMap hashmap = null;
        for (int i = 0; i < array.size(); i++) {
            String s2 = ((String) array.at(i)).trim();
            if (s2.indexOf("error:") >= 0) {
                hashmap = null;
                break;
            }
            if (hashmap == null) {
                hashmap = new HashMap();
            }
            hashmap.put(s2, "true");
        }

        return hashmap;
    }

    public String expandPartialURL(String s) {
        String s1 = getProperty(pServer);
        String s2 = getProperty(pLoginAccount);
        if (s2.length() == 0) {
            s2 = "administrator";
        }
        String s3 = "http://";
        if (s1.indexOf("://") != -1) {
            s3 = "";
        }
        String s4 = s3 + s1 + s;
        if (!s2.equals("administrator")) {
            String s5 = "account=" + s2;
            s4 = TextUtils.replaceString(s4, "account=administrator", s5);
        }
        return s4;
    }

    public Array sendURLToRemoteSiteView(String s, CommandLine commandline) {
        s = expandPartialURL(s);
        Array array = CommandLine.runHTTPCommand(s, getProperty(pUserName),
                getProperty(pPassword), getProperty(pProxy),
                getProperty(pProxyUserName), getProperty(pProxyPassword),
                getProperty(pTimeout), commandline);
        if (getSetting("_trace").length() > 0) {
            LogManager.log("RunMonitor", "Remote SiteView : "
                    + getProperty(pTitle));
            LogManager.log("RunMonitor", "Remote SiteView URL: " + s);
            if (commandline != null) {
                LogManager.log("RunMonitor", "Remote SiteView Status: "
                        + commandline.exitValue + " in " + commandline.duration
                        + "ms");
            }
            LogManager.log("RunMonitor", "Remote SiteView Result: \n");
            for (int i = 0; i < array.size(); i++) {
                LogManager
                        .log("RunMonitor", "Remote SiteView: " + array.at(i));
            }

        }
        return array;
    }

    public String getURLContentsFromRemoteSiteView(HTTPRequest httprequest,
            String s) {
        String s1 = httprequest.rawURL;
        String s2 = "account=" + httprequest.getAccount();
        Array array = null;
        if (httprequest.isPost()) {
            String s3 = httprequest.queryString;
            String s5 = "&portalserver="
                    + URLEncoder.encode(httprequest.getPortalServer());
            if ((Portal.centrascopeDebug & 4) != 0) {
                TextUtils
                        .debugPrint("Get URL Contents From Remote SiteView QueryString="
                                + s3);
            }
            s3 = TextUtils.replaceString(s3, s2, "account=administrator");
            s3 = TextUtils.replaceString(s3, s5, "");
            String as[] = TextUtils.split(s3, "&");
            array = new Array();
            for (int i = 0; i < as.length; i++) {
                array.add(httprequest.decodeString(as[i]));
            }

        } else {
            String s4 = "&portalserver=" + httprequest.getPortalServer();
            s1 = TextUtils.replaceString(s1, s2, "account=administrator");
            s1 = TextUtils.replaceString(s1, s4, "");
        }
        if ((Portal.centrascopeDebug & 4) != 0) {
            TextUtils.debugPrint("Get URL Contents From Remote SiteView URL1="
                    + s1);
            TextUtils
                    .debugPrint("Get URL Contents From Remote SiteView Post Data="
                            + array);
        }
        return getURLContentsFromRemoteSiteView(s1, array, s);
    }

    public String getURLContentsFromRemoteSiteView(String s, String s1) {
        return getURLContentsFromRemoteSiteView(s, null, s1);
    }

    public String getURLContentsFromRemoteSiteView(String s, Array array,
            String s1) {
        s = expandPartialURL(s);
        StringBuffer stringbuffer = new StringBuffer();
        String s2 = "";
        SocketSession socketsession = SocketSession.getSession(null);
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("urlContentMatchMax", 51200);
        int i = TextUtils.toInt(getProperty(pTimeout)) * 1000;
        if (i <= 0) {
            i = URLMonitor.DEFAULT_TIMEOUT;
        }
        if ((Portal.centrascopeDebug & 4) != 0) {
            TextUtils.debugPrint("Get URL Contents From Remote SiteView URL2="
                    + s);
        }
        long al[] = URLMonitor.checkURL(socketsession, s, "", "",
                getProperty(pProxy), getProperty(pProxyUserName),
                getProperty(pProxyPassword), array, getProperty(pUserName),
                getProperty(pPassword), "", stringbuffer, l, "", 0, i, null);
        long l1 = al[0];
        socketsession.close();
        if (l1 == (long) URLMonitor.kURLok) {
            String s3 = URLMonitor.getHTTPContent(stringbuffer.toString());
            Array array1 = new Array();
            String s4 = CGI.CONTENT_REGEX;
            int j = TextUtils.matchExpression(s3, s4, array1,
                    new StringBuffer(), "");
            if (j != URLMonitor.kURLok) {
                array1.clear();
                s4 = getSetting(s1);
                if ((Portal.centrascopeDebug & 4) != 0) {
                    TextUtils
                            .debugPrint("Get URL Contents From Remote SiteView Match String="
                                    + s4);
                }
                j = TextUtils.matchExpression(s3, s4, array1,
                        new StringBuffer(), "");
            }
            if (j == URLMonitor.kURLok) {
                if (array1.size() > 0) {
                    s2 = (String) array1.at(0);
                } else {
                    s2 = s3;
                }
            } else {
                s2 = "<H2>Could not find from the " + getProperty(pTitle)
                        + " SiteView</H2>\n" + "<P><B>URL:</B> "
                        + TextUtils.escapeHTML(s) + "\n" + "<P><B>Match:</B> "
                        + TextUtils.escapeHTML(s4) + "\n"
                        + "<P><B>Contents of page</B><P>\n" + "<HR><PRE>"
                        + TextUtils.escapeHTML(s3) + "</PRE><HR>";
            }
            if (getSetting("_trace").length() > 0) {
                LogManager.log("RunMonitor", "Remote SiteView: "
                        + getProperty(pTitle));
                LogManager.log("RunMonitor", "Remote SiteView URL: " + s);
                LogManager.log("RunMonitor", "Remote SiteView Status: " + l1
                        + " in " + al[1] + "ms");
                LogManager
                        .log("RunMonitor", "Remote SiteView Result: \n" + s3);
            }
        } else {
            s2 = "<H2>Could not retrieve information from the "
                    + getProperty(pTitle) + " SiteView</H2>\n"
                    + "<P>The error was " + Monitor.lookupStatus(l1);
        }
        return s2;
    }

    static {
        pServer = new StringProperty("_server");
        pTitle = new StringProperty("_title");
        pUserName = new StringProperty("_username");
        pPassword = new StringProperty("_password");
        pProxy = new StringProperty("_proxy");
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyPassword = new StringProperty("_proxypassword");
        pTimeout = new StringProperty("_timeout");
        pTimezoneOffsetFromPortal = new StringProperty(
                "_timezoneOffsetFromPortal");
        pLoginAccount = new StringProperty("_loginAccount");
        pDisabled = new BooleanProperty("_disabled");
        pReadOnly = new BooleanProperty("_readOnly");
        pConnectState = new StringProperty("connectState");
        pLogCollectorRefresh = new StringProperty("_logCollectorRefresh");
        pPlatformOS = new StringProperty("_platformOS");
        StringProperty astringproperty[] = { pServer, pTitle, pUserName,
                pPassword, pProxy, pProxyUserName, pProxyPassword, pTimeout,
                pLoginAccount, pDisabled, pReadOnly, pTimezoneOffsetFromPortal,
                pConnectState, pLogCollectorRefresh, pPlatformOS };
        addProperties("COM.dragonflow.SiteView.PortalSiteView",
                astringproperty);
    }
}