/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * LogPullerReadThread.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>LogPullerReadThread</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.StandardMonitor.URLScannerInputStream;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalSiteView, Monitor, AtomicMonitor, MonitorGroup,
// LogPuller, Portal, Platform, PortalSync,
// MasterConfig, LogReader, SiteViewObject

class LogPullerReadThread extends Thread {

    static int nextServer = 0;

    long pauseLogs;

    long skipConfigLimit;

    boolean done;

    int index;

    static HashMap portalConfigCheck = new HashMap();

    public static final int LOG_BYTES = 0;

    public static final int LOG_ERRORS = 1;

    public static final int LOG_COUNT = 2;

    public static final int LOG_UNCHANGED = 3;

    public static final int LOG_CATEGORY = 0;

    public static final int LOG_STATUS = 1;

    private static String fields[] = new String[100];

    static synchronized PortalSiteView nextServer() {
        Array array = LogPuller.getPortals();
        PortalSiteView portalsiteview = null;
        if (array.size() > 0) {
            int i = nextServer;
            do {
                portalsiteview = (PortalSiteView) array.at(nextServer);
                nextServer++;
                if (nextServer >= array.size()) {
                    nextServer = 0;
                }
                if (portalsiteview == null) {
                    continue;
                }
                String s = portalsiteview.getProperty("_logCollectorRefresh");
                long l = 60000L;
                if (s.length() != 0) {
                    l = TextUtils.toLong(s) * 1000L;
                }
                if ((Portal.centrascopeDebug & 1) != 0) {
                    PortalSiteView _tmp = portalsiteview;
                    System.out.println(TextUtils.dateToString(new Date())
                            + " **** nextServer "
                            + portalsiteview
                                    .getProperty(PortalSiteView.pServer)
                            + "("
                            + portalsiteview.getProperty("_loginAccount")
                            + "), lastLogTime="
                            + portalsiteview.logCollectorTimestamp
                            + ", refreshTime="
                            + l
                            + ", now="
                            + Platform.timeMillis()
                            + ", downloadInProgress="
                            + portalsiteview.downloadInProgress
                            + ", disabled="
                            + (portalsiteview.getProperty("_disabled")
                                    .length() <= 0 ? "false" : "true"));
                }
                if (portalsiteview.logCollectorTimestamp + l <= Platform
                        .timeMillis()
                        && !portalsiteview.downloadInProgress) {
                    if (portalsiteview.getProperty("_disabled").length() != 0) {
                        portalsiteview.setProperty(
                                PortalSiteView.pConnectState,
                                Monitor.DISABLED_CATEGORY);
                        portalsiteview.setProperty(
                                PortalSiteView.pStateString, "disabled");
                        updateStatus(portalsiteview,
                                "log collector disabled for "
                                        + portalsiteview
                                                .getProperty("_server"));
                    } else {
                        portalsiteview.downloadInProgress = true;
                        break;
                    }
                }
                portalsiteview = null;
            } while (i != nextServer);
            if ((Portal.centrascopeDebug & 1) != 0) {
                System.out.println(TextUtils.dateToString(new Date())
                        + " **** nextServer first=" + i + ", next="
                        + nextServer);
            }
        }
        return portalsiteview;
    }

    public LogPullerReadThread(long l, long l1, int i) {
        super((Runnable) null);
        done = false;
        index = 0;
        pauseLogs = l;
        index = i;
        skipConfigLimit = l1 / l + 1L;
    }

    public void run() {
        Platform.sleep(index % 60);
        while (!done) {
            long l = pauseLogs;
            PortalSiteView portalsiteview = nextServer();
            if (portalsiteview != null) {
                synchronized (portalsiteview.logCollectorLock) {
                    long l1 = Platform.timeMillis();
                    if ((Portal.centrascopeDebug & 1) != 0) {
                        PortalSiteView _tmp = portalsiteview;
                        System.out.println(TextUtils.dateToString(new Date())
                                + " **** checkServer(thread #"
                                + (index + 1)
                                + ") "
                                + portalsiteview
                                        .getProperty(PortalSiteView.pServer)
                                + "("
                                + portalsiteview.getProperty("_loginAccount")
                                + ") STARTED");
                    }
                    checkServer(portalsiteview);
                    if ((Portal.centrascopeDebug & 1) != 0) {
                        PortalSiteView _tmp1 = portalsiteview;
                        System.out.println(TextUtils.dateToString(new Date())
                                + " **** checkServer(thread #"
                                + (index + 1)
                                + ") "
                                + portalsiteview
                                        .getProperty(PortalSiteView.pServer)
                                + "("
                                + portalsiteview.getProperty("_loginAccount")
                                + ") COMPLETED ("
                                + (Platform.timeMillis() - l1) + "ms)");
                    }
                    l = (l1 + pauseLogs) - Platform.timeMillis();
                    if (l < 0L) {
                        l = 10000L;
                    }
                    portalsiteview.downloadInProgress = false;
                }
            }
            LogManager.log("RunMonitor", "log collector #" + (index + 1)
                    + " will run next in " + l / 1000L + " seconds");
            if ((Portal.centrascopeDebug & 1) != 0) {
                System.out.println(TextUtils.dateToString(new Date())
                        + " thread #" + (index + 1) + " will run next in " + l
                        / 1000L + " seconds");
            }
            Platform.sleep(l);
        }
    }

    void checkServer(PortalSiteView portalsiteview) {
        PortalSiteView _tmp = portalsiteview;
        String s = portalsiteview.getProperty(PortalSiteView.pServer);
        nextStatus(portalsiteview, "reading log names, " + s);
        SocketSession socketsession = SocketSession.getSession(null);
        String s1 = "";
        boolean flag = portalsiteview.getProperty("_autoSyncFromSiteView")
                .length() > 0;
        if (!portalGroupsExist(portalsiteview)) {
            flag = true;
        }
        if (flag) {
            long l = syncConfiguration(portalsiteview);
            if (l != (long) Monitor.kURLok) {
                s1 = "Could not access configuration files, "
                        + Monitor.lookupStatus(l);
            }
        }
        long al[] = new long[4];
        al[0] = 0L;
        al[1] = 0L;
        al[2] = 0L;
        al[3] = 0L;
        StringBuffer stringbuffer = new StringBuffer();
        fetchLogDirectory(portalsiteview, "", socketsession, al, stringbuffer);
        HashMap hashmap = portalsiteview.getMasterConfig();
        if (TextUtils.getValue(hashmap, "_logInGroup").length() > 0
                && (portalsiteview.getProperty("_loginAccount").equals("") || portalsiteview
                        .getProperty("_loginAccount").equals("administrator"))) {
            StringBuffer stringbuffer1 = new StringBuffer();
            PortalSync.listFiles(portalsiteview, "/accounts", stringbuffer1);
            String s3 = stringbuffer1.toString();
            Array array = Platform.split('\n', s3);
            for (int i = 0; i < array.size(); i++) {
                String s5 = ((String) array.at(i)).trim();
                fetchLogDirectory(portalsiteview, s5, socketsession, al,
                        stringbuffer);
            }

        }
        String s2 = "read " + al[2] + " logs, " + al[0] + " bytes, " + al[3]
                + " unchanged, " + al[1] + " errors";
        if (al[1] > 0L) {
            s2 = s2 + ", first error was " + stringbuffer.toString();
        }
        if (s1.length() > 0) {
            s2 = s2 + ", " + s1;
        }
        portalsiteview.setProperty(PortalSiteView.pStateString, s2);
        String s4 = Monitor.GOOD_CATEGORY;
        if (al[2] == 0L && al[1] > 0L) {
            s4 = Monitor.ERROR_CATEGORY;
        } else if (al[2] > 0L && al[1] > 0L) {
            s4 = Monitor.WARNING_CATEGORY;
        } else if (s1.length() > 0) {
            s4 = Monitor.ERROR_CATEGORY;
        }
        portalsiteview.setProperty(PortalSiteView.pConnectState, s4);
        updateStatus(portalsiteview, s2);
        socketsession.close();
    }

    void fetchLogDirectory(PortalSiteView portalsiteview, String s,
            SocketSession socketsession, long al[], StringBuffer stringbuffer) {
        Array array = new Array();
        long l = fetchLogList(portalsiteview, s, array, socketsession);
        if (l != 200L) {
            al[1]++;
            LogManager.log("RunMonitor", l + ", "
                    + portalsiteview.getProperty(PortalSiteView.pTitle));
            LogManager.log("Error", l + ", "
                    + portalsiteview.getProperty(PortalSiteView.pTitle));
            if (stringbuffer.length() == 0) {
                stringbuffer.append("Error reading log names: "
                        + Monitor.lookupStatus(l) + " (" + l + ")");
            }
        }
        long al1[];
        for (Enumeration enumeration = array.elements(); enumeration
                .hasMoreElements(); al[0] += al1[1]) {
            String s1 = (String) enumeration.nextElement();
            al1 = checkLog(portalsiteview, s, s1, socketsession);
            if (al1[0] == 201L) {
                al[3]++;
                continue;
            }
            if (al1[0] == 200L || al1[0] == 206L) {
                al[2]++;
                continue;
            }
            al[1]++;
            if (stringbuffer.length() == 0) {
                stringbuffer.append("Error reading log " + s1 + ", "
                        + Monitor.lookupStatus(al1[0]) + " (" + al1[0] + ")");
            }
        }

    }

    URLScannerInputStream getLogHeader(PortalSiteView portalsiteview,
            String s, String s1, SocketSession socketsession) {
        PortalSiteView _tmp = portalsiteview;
        String s2 = portalsiteview.getProperty(PortalSiteView.pServer);
        String s3 = portalsiteview.getProperty("_loginAccount");
        String s4 = portalsiteview.getProperty("_proxy");
        String s5 = portalsiteview.getProperty("_proxyusername");
        String s6 = portalsiteview.getProperty("_proxypassword");
        String s7 = portalsiteview.getProperty("_username");
        String s8 = portalsiteview.getProperty("_password");
        String s9 = portalsiteview.getProperty("_timeout");
        StringBuffer stringbuffer = new StringBuffer();
        String s10 = "http://";
        if (s2.indexOf("://") != -1) {
            s10 = "";
        }
        if (s.length() == 0) {
            s = s3;
        }
        String s11;
        if (s.length() > 0) {
            s11 = s10 + s2 + "/SiteView/accounts/" + s + "/logs/" + s1;
            if (s3.equals("") || s3.equals("administrator")) {
                s11 = s11 + "?account=administrator";
            }
        } else {
            s11 = s10 + s2 + "/SiteView/logs/" + s1;
        }
        String s12 = "";
        Array array = new Array();
        array.add("Method: HEAD");
        int i = 60000;
        if (s9.length() != 0) {
            i = TextUtils.toInt(s9) * 1000;
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (s7.length() == 0) {
            s7 = TextUtils.getValue(hashmap, "_adminUsername");
            s8 = TextUtils.getValue(hashmap, "_adminPassword");
        }
        long l = -1L;
        if (TextUtils.getValue(hashmap, "_logPullerMaxData").length() > 0) {
            l = TextUtils.toLong(TextUtils.getValue(hashmap,
                    "_logPullerMaxData"));
        }
        if (l < 1L) {
            l = 0x7a120L;
        }
        if ((Portal.centrascopeDebug & 1) != 0) {
            PortalSiteView _tmp1 = portalsiteview;
            System.out.println(TextUtils.dateToString(new Date())
                    + " **** log header URL "
                    + portalsiteview.getProperty(PortalSiteView.pServer)
                    + "(" + s1 + "), url=" + s11 + ", contentLength="
                    + stringbuffer.length());
        }
        long al[] = URLMonitor.checkURL(socketsession, s11, s12, "", s4, s5,
                s6, array, s7, s8, "", stringbuffer, l, "", 0, i, null);
        long l1 = al[0];
        if ((Portal.centrascopeDebug & 1) != 0) {
            PortalSiteView _tmp2 = portalsiteview;
            System.out.println(TextUtils.dateToString(new Date())
                    + " **** log header "
                    + portalsiteview.getProperty(PortalSiteView.pServer)
                    + "(" + s1 + "), status=" + l1 + ", contentLength="
                    + stringbuffer.length());
            PortalSiteView _tmp3 = portalsiteview;
            System.out.println(" **** log header content "
                    + portalsiteview.getProperty(PortalSiteView.pServer)
                    + "(" + s1 + ")\n" + stringbuffer);
        }
        URLScannerInputStream urlscannerinputstream = new URLScannerInputStream(
                stringbuffer, l1);
        urlscannerinputstream.parse();
        return urlscannerinputstream;
    }

    static String getDisplayLogPath(String s, String s1) {
        if (s.length() == 0) {
            return s1;
        } else {
            return "/accounts/" + s + "/logs/" + s1;
        }
    }

    long[] checkLog(PortalSiteView portalsiteview, String s, String s1,
            SocketSession socketsession) {
        PortalSiteView _tmp = portalsiteview;
        String s2 = portalsiteview.getProperty(PortalSiteView.pServer);
        PortalSiteView _tmp1 = portalsiteview;
        String s3 = portalsiteview.getProperty(PortalSiteView.pID);
        String s4 = getLogPath(s3, s, s1);
        File file = new File(s4);
        long l = file.length();
        long l1 = l;
        String s5 = "error";
        long l2 = 0L;
        long l3 = -1L;
        nextStatus(portalsiteview, "reading " + getDisplayLogPath(s, s1)
                + ", " + s2);
        long l4 = Platform.timeMillis();
        URLScannerInputStream urlscannerinputstream = getLogHeader(
                portalsiteview, s, s1, socketsession);
        l3 = urlscannerinputstream.status;
        if (l3 == 200L) {
            if (l != urlscannerinputstream.contentLength) {
                if (urlscannerinputstream.contentLength < l) {
                    l1 = 0L;
                }
                long l5 = urlscannerinputstream.contentLength;
                StringBuffer stringbuffer = new StringBuffer();
                l3 = fetchLog(portalsiteview, s, s1, l1, l5, stringbuffer,
                        socketsession);
                if (l3 == 200L || l3 == 206L) {
                    String s6 = HTTPUtils.getHTTPPart(stringbuffer.toString(),
                            false);
                    int i = s6.lastIndexOf('\n');
                    s6 = s6.substring(0, i + 1);
                    l2 = s6.length();
                    try {
                        if (l1 == 0L) {
                            FileUtils.writeFile(s4, s6);
                            s5 = "copied";
                        } else {
                            FileUtils.appendFile(s4, s6);
                            s5 = "appended";
                        }
                        try {
                            if (s1.startsWith("SiteView")) {
                                updateMonitorState(portalsiteview, s6, s4, s2);
                            }
                        } catch (NullPointerException nullpointerexception) {
                            LogManager.log("Error",
                                    "error updating monitor state, "
                                            + nullpointerexception + ", " + s4
                                            + ", " + s2);
                            l3 = -999L;
                        }
                    } catch (IOException ioexception) {
                        LogManager.log("Error", "error writing log, "
                                + ioexception + ", " + s4 + ", " + s2);
                        l3 = -999L;
                    }
                }
            } else {
                s5 = "unchanged";
                l3 = 201L;
            }
        }
        long l6 = Platform.timeMillis() - l4;
        if (l3 == 201L || l3 == 200L || l3 == 206L) {
            updateStatus(portalsiteview, s1 + " " + s5 + ", " + l1 + ":"
                    + (l1 + l2) + ", " + l6 + " ms, " + s2);
        } else {
            updateStatus(portalsiteview, s1 + " " + s5 + ", " + l1 + ":"
                    + (l1 + l2) + ", " + l6 + " ms, " + l3 + ", " + s2);
            LogManager.log("Error", s1 + " " + s5 + ", " + l1 + ":" + (l1 + l2)
                    + ", " + l6 + " ms, " + l3 + ", " + s2);
        }
        long al[] = new long[2];
        al[0] = l3;
        al[1] = l2;
        return al;
    }

    long fetchLog(PortalSiteView portalsiteview, String s, String s1, long l,
            long l1, StringBuffer stringbuffer, SocketSession socketsession) {
        PortalSiteView _tmp = portalsiteview;
        String s2 = portalsiteview.getProperty(PortalSiteView.pServer);
        String s3 = portalsiteview.getProperty("_proxy");
        String s4 = portalsiteview.getProperty("_proxyusername");
        String s5 = portalsiteview.getProperty("_proxypassword");
        String s6 = portalsiteview.getProperty("_username");
        String s7 = portalsiteview.getProperty("_password");
        String s8 = portalsiteview.getProperty("_timeout");
        String s9 = portalsiteview.getProperty("_loginAccount");
        String s10 = "http://";
        if (s2.indexOf("://") != -1) {
            s10 = "";
        }
        if (s.length() == 0) {
            s = s9;
        }
        String s11;
        if (s.length() > 0) {
            s11 = s10 + s2 + "/SiteView/accounts/" + s + "/logs/" + s1;
            if (s9.equals("") || s9.equals("administrator")) {
                s11 = s11 + "?account=administrator";
            }
        } else {
            s11 = s10 + s2 + "/SiteView/logs/" + s1;
        }
        String s12 = "";
        int i = 60000;
        if (s8.length() != 0) {
            i = TextUtils.toInt(s8) * 1000;
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (s6.length() == 0) {
            s6 = TextUtils.getValue(hashmap, "_adminUsername");
            s7 = TextUtils.getValue(hashmap, "_adminPassword");
        }
        long l2 = -1L;
        if (TextUtils.getValue(hashmap, "_logPullerMaxData").length() > 0) {
            l2 = TextUtils.toLong(TextUtils.getValue(hashmap,
                    "_logPullerMaxData"));
        }
        if (l2 < 1L) {
            l2 = 0x7a120L;
        }
        if (l + l2 < l1) {
            l1 = l + l2;
        }
        Array array = new Array();
        array.add("Custom-Header: Range: bytes=" + l + "-" + l1);
        long al[] = URLMonitor.checkURL(socketsession, s11, s12, "", s3, s4,
                s5, array, s6, s7, "", stringbuffer, l2, "", 0, i, null);
        long l3 = al[0];
        if ((Portal.centrascopeDebug & 1) != 0) {
            PortalSiteView _tmp1 = portalsiteview;
            System.out.println(TextUtils.dateToString(new Date())
                    + " **** log fetch "
                    + portalsiteview.getProperty(PortalSiteView.pServer)
                    + "(" + s1 + "), status=" + l3 + ", contentLength="
                    + stringbuffer.length());
        }
        return l3;
    }

    public boolean portalGroupsExist(PortalSiteView portalsiteview) {
        String s = Portal.getPortalSiteViewRootPath(portalsiteview
                .getProperty(PortalSiteView.pID))
                + "/groups";
        File file = new File(s);
        return file.exists();
    }

    public long syncConfiguration(PortalSiteView portalsiteview) {
        LogManager
                .log("Error",
                        "syncConfiguration(PortalSiteView server): CentraScope not supported!");
        return (long) Monitor.kURLUnknownError;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param portalsiteview
     * @param s
     * @param array
     * @param socketsession
     * @return
     */
    long fetchLogList(PortalSiteView portalsiteview, String s, Array array,
            SocketSession socketsession) {
        String s1 = portalsiteview.getProperty("_server");
        String s2 = portalsiteview.getProperty("_proxy");
        String s3 = portalsiteview.getProperty("_proxyusername");
        String s4 = portalsiteview.getProperty("_proxypassword");
        String s5 = portalsiteview.getProperty("_username");
        String s6 = portalsiteview.getProperty("_password");
        String s7 = portalsiteview.getProperty("_timeout");
        String s8 = portalsiteview.getProperty("_loginAccount");
        String s9 = "http://";
        if (s1.indexOf("://") != -1) {
            s9 = "";
        }
        String s10;
        if (s.length() > 0) {
            s10 = "/accounts/" + s + "/logs";
        } else {
            s10 = "/logs";
        }
        StringBuffer stringbuffer = new StringBuffer();
        String s11 = s9
                + s1
                + "/SiteView/cgi/go.exe/SiteView?page=file&operation=list&file="
                + s10;
        if (s8.length() > 0) {
            s11 = s11 + "&account=" + s8;
        } else {
            s11 = s11 + "&account=administrator";
        }
        String s12 = "";
        int i = 60000;
        if (s7.length() != 0) {
            i = TextUtils.toInt(s7) * 1000;
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (s5.length() == 0) {
            s5 = TextUtils.getValue(hashmap, "_adminUsername");
            s6 = TextUtils.getValue(hashmap, "_adminPassword");
        }
        long l = -1L;
        if (TextUtils.getValue(hashmap, "_logPullerMaxData").length() > 0) {
            l = TextUtils.toLong(TextUtils.getValue(hashmap,
                    "_logPullerMaxData"));
        }
        if (l < 1L) {
            l = 0x7a120L;
        }
        long al[] = URLMonitor.checkURL(socketsession, s11, s12, "", s2, s3,
                s4, null, s5, s6, "", stringbuffer, l, "", 0, i, null);
        long l1 = al[0];
        if (l1 == 200L) {
            String s13 = HTTPUtils.getHTTPPart(stringbuffer.toString(), false);
            Array array1 = Platform.split('\n', s13);
            Enumeration enumeration = array1.elements();
            while (enumeration.hasMoreElements()) {
                String s14 = (String) enumeration.nextElement();
                s14 = s14.trim();
                if (s14.indexOf("access.log") == -1
                        && s14.indexOf("RunMonitor.log") == -1) {
                    array.add(s14);
                }
            }
        }
        return l1;
    }

    String getLogPath(String s, String s1, String s2) {
        String s3 = Portal.getPortalSiteViewRootPath(s);
        File file = new File(s3);
        if (!file.exists()) {
            file.mkdir();
        }
        if (s1.length() == 0) {
            s3 = s3 + File.separator + "logs" + File.separator;
        } else {
            s3 = s3 + File.separator + "accounts";
            file = new File(s3);
            if (!file.exists()) {
                file.mkdir();
            }
            s3 = s3 + File.separator + s1;
            file = new File(s3);
            if (!file.exists()) {
                file.mkdir();
            }
            s3 = s3 + File.separator + "logs" + File.separator;
        }
        file = new File(s3);
        if (!file.exists()) {
            file.mkdir();
        }
        s3 = s3 + s2;
        return s3;
    }

    static void nextStatus(PortalSiteView portalsiteview, String s) {
        portalsiteview.logCollectorStatus = s;
        portalsiteview.logCollectorTimestamp = Platform.timeMillis();
        if ((Portal.centrascopeDebug & 1) != 0) {
            PortalSiteView _tmp = portalsiteview;
            System.out.println(TextUtils.dateToString(new Date())
                    + " **** nextStatus "
                    + portalsiteview.getProperty(PortalSiteView.pServer)
                    + "- " + s);
        }
    }

    static void updateStatus(PortalSiteView portalsiteview, String s) {
        nextStatus(portalsiteview, s);
        LogManager.log("RunMonitor", "log collector, " + s);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param portalsiteview
     * @param s
     * @param s1
     * @param s2
     */
    synchronized void updateMonitorState(PortalSiteView portalsiteview,
            String s, String s1, String s2) {
        byte byte0 = -1;
        int j = 0;
        int ai[] = new int[6];
        HashMap hashmap = new HashMap();
        
        while (true) {
            int i;
            if ((i = s.indexOf('\n', j)) == -1) {
                break;
            }
            String s3 = s.substring(j, i).trim();
            j = i + 1;
            try {
                if (LogReader.isDateLine(s3) && LogReader.getDateInfo(s3, ai)) {
                    int k = TextUtils.splitChar(s3, '\t', fields);
                    if (k > 5) {
                        String s5 = fields[5];
                        int l = s5.indexOf(":");
                        if (l != -1) {
                            s5 = s5.substring(0, l);
                        }
                        String s6 = fields[2] + SiteViewObject.ID_SEPARATOR
                                + s5;
                        Monitor monitor = (Monitor) portalsiteview
                                .getElement(s6);
                        if (monitor != null
                                && (monitor instanceof AtomicMonitor)) {
                            AtomicMonitor atomicmonitor = (AtomicMonitor) monitor;
                            Array array = atomicmonitor.getLogProperties();
                            Date date = LogReader.getDate(ai);
                            date = new Date(
                                    date.getTime()
                                            + 0x36ee80L
                                            * portalsiteview
                                                    .getPropertyAsLong(PortalSiteView.pTimezoneOffsetFromPortal));
                            Monitor _tmp = monitor;
                            long l1 = monitor
                                    .getPropertyAsLong(Monitor.pLastUpdate);
                            if (l1 < date.getTime()) {
                                Monitor _tmp1 = monitor;
                                monitor.setProperty(Monitor.pLastUpdate, ""
                                        + date.getTime());
                                Monitor _tmp2 = monitor;
                                String s7 = monitor
                                        .getProperty(Monitor.pGroupID);
                                if (s7 != null) {
                                    hashmap.put(s7, s7);
                                }
                                int i1 = array.size();
                                if (array.size() > k) {
                                    i1 = k;
                                }
                                int j1 = 0;
                                while (j1 < i1) {
                                    StringProperty stringproperty = (StringProperty) array
                                            .at(j1);
                                    if (stringproperty != Monitor.pOwnerID
                                            && stringproperty != Monitor.pName
                                            && stringproperty != Monitor.pID) {
                                        monitor.setProperty(stringproperty,
                                                fields[j1 + 1]);
                                    }
                                    j1++;
                                }
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                LogManager.log("Error", "error updating monitor state, "
                        + exception + ", " + s1 + ", " + s2);
            }
        } 
        
        Enumeration enumeration = hashmap.keys();
        while (enumeration.hasMoreElements()) {
            String s4 = (String) enumeration.nextElement();
            MonitorGroup monitorgroup = (MonitorGroup) portalsiteview
                    .getElement(Portal.getGroupID(s4));
            if (monitorgroup != null) {
                monitorgroup.saveDynamic();
            }
        }
    }

}
