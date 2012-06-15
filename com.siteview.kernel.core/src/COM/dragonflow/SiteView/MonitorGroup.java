
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MonitorGroup</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.Filtering;
import jgl.HashMap;
import jgl.Reversing;
import jgl.Sorting;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Log.DailyFileLogger;
import COM.dragonflow.Log.FileLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.monitorSetPage;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.FrequencyProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardAction.UpdateGroup;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.LocaleUtils;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.itsm.data.JDBCForSQL;
import SiteViewMain.SiteViewSupport;

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor, HistoryReport, RuleGroupIs, Rule,
// SubGroup, AtomicMonitor, GreaterEqualMonitorName, SiteViewGroup,
// Machine, Scheduler, Platform, Portal,
// SiteViewObject, PortalSync, MasterConfig, TopazInfo,
// ConfigurationChanger

public class MonitorGroup extends Monitor {

    public static StringProperty pLastSaved;

    public static StringProperty pMaxErrorCount;

    public static StringProperty pMonitorsInError;

    public static StringProperty pMonitorsInGroup;

    public static StringProperty pGroupErrorDisplay;

    public static StringProperty pFrequency;

    public static StringProperty pHealth;

    public static StringProperty pParent;

    public static StringProperty pAccountName;

    static int minGroupInterval;

    private UpdateGroup updateGroup;

    File file;

    public static String accountDisabled = "account disabled";

    private static int columnNum;

    public static int CATEGORY_COLUMN;

    public static int ACKNOWLEDGE_COLUMN;

    public static int GAUGE_COLUMN;

    public static int STATUS_COLUMN;

    public static int NAME_COLUMN;

    public static int MORE_COLUMN;

    public static int UPDATED_COLUMN;

    public static int GROUP_COLUMN;

    public static int REFRESH_COLUMN;

    public static int EDIT_COLUMN;

    public static int DELETE_COLUMN;

    public static int ALERT_COLUMN;

    public static int REPORT_COLUMN;

    public static int SIMPLE_NAME_COLUMN;

    public static int MACHINE_COLUMN;

    public static int CUSTOM_COLUMN;

    public static String columnText[] = { "State", "Ack", "Gauge", "Status", "Name", "More", "Updated", "Group", "Refresh", "Edit", "Del", "Alerts", "Reports", "Name", "Machine", "Custom" };

    public MonitorGroup() {
        updateGroup = null;
        file = null;
    }

    public CGI.menus getNavItems(HTTPRequest httprequest) {
        CGI.menus menus1 = new CGI.menus();
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if (httprequest.actionAllowed("_preference")) {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if (httprequest.actionAllowed("_tools")) {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if (httprequest.actionAllowed("_progress")) {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        return menus1;
    }

    public String getHostname() {
    	String hostname = "";
    	try {
    	    InetAddress addr = InetAddress.getLocalHost();

    	    // Get IP Address
    	    byte[] ipAddr = addr.getAddress();

    	    // Get hostname
    	    hostname = addr.getHostName();
    	} catch (UnknownHostException e) {
    	}
    	return hostname;
//        return "GroupHost";
    }

    protected void startGroup() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        if (siteviewgroup.dontStartGroups) {
            return;
        } else {
            startMonitor();
            startScheduler();
            writeAllHTML();
            return;
        }
    }

    protected Array calculateIPAddresses() {
        return new Array();
    }

    protected void startMonitor() {
        super.startMonitor();
        Enumeration enumeration = getMultipleValues("_remoteMachine");
        if (enumeration.hasMoreElements()) {
            Machine.registerMachines(enumeration, getProperty(pID));
        }
        Monitor monitor;
        for (Enumeration enumeration1 = getMonitors(); enumeration1.hasMoreElements(); monitor.startMonitor()) {
            monitor = (Monitor) enumeration1.nextElement();
        }

        HistoryReport historyreport;
        for (Enumeration enumeration2 = getReports(); enumeration2.hasMoreElements(); historyreport.schedule()) {
            historyreport = (HistoryReport) enumeration2.nextElement();
        }

    }

    protected void stopGroup() {
        if (updateGroup != null) {
            SiteViewGroup.currentSiteView().monitorScheduler.unschedule(updateGroup);
        }
        stopMonitor();
    }

    protected void stopMonitor() {
        super.stopMonitor();
        Monitor monitor;
        for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements(); monitor.stopMonitor()) {
            monitor = (Monitor) enumeration.nextElement();
        }

        HistoryReport historyreport;
        for (Enumeration enumeration1 = getReports(); enumeration1.hasMoreElements(); historyreport.unschedule()) {
            historyreport = (HistoryReport) enumeration1.nextElement();
        }

    }

    public File getFile() {
        return file;
    }

    public SiteViewObject getParent() {
        String s = getProperty(pParent);
        if (s.length() == 0) {
            if (Platform.isPortal()) {
                s = getFullID();
                if (s.indexOf(Portal.PORTAL_ID_SEPARATOR) == s.length() - 1) {
                    return null;
                } else {
                    return Portal.getSiteViewForID(getFullID());
                }
            } else {
                return null;
            }
        } else {
            SiteViewObject siteviewobject = Portal.getSiteViewForID(getFullID());
            return siteviewobject.getElement(s);
        }
    }

    public static MonitorGroup loadGroup(String s, String s1) {
        return loadGroup(s, s1, true);
    }

    public static MonitorGroup loadGroup(String s, String groupid, boolean flag) {
        MonitorGroup monitorgroup = null;
        try {
            monitorgroup = new MonitorGroup();
            monitorgroup.setProperty("_id", s);
            monitorgroup.setProperty("_name", s);
            monitorgroup.file = new File(groupid);
            monitorgroup.readMonitors(groupid, s);
            monitorgroup.readDynamic();
            monitorgroup.initialize(monitorgroup.getValuesTable());
        } catch (FileNotFoundException filenotfoundexception) {
            monitorgroup = null;
            if (flag) {
                LogManager.log("Error", "Error loading group, not found: " + groupid);
                filenotfoundexception.printStackTrace();
            }
        } catch (Exception exception) {
            monitorgroup = null;
            if (flag) {
                LogManager.log("Error", "Error loading group, file: " + groupid + ", error: " + exception);
                exception.printStackTrace();
            }
        }
        return monitorgroup;
    }


    void readMonitors(String s, String s1) throws IOException {
        Array array = FrameFile.readFromFile(s);
        String s2 = "";
        if (Platform.isPortal()) {
            s2 = PortalSync.getPortalServerFromPath(s);
        }
        Enumeration enumeration = array.elements();
        if (enumeration.hasMoreElements()) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            hashmap.put("_id", s1);
            String s3 = (String) hashmap.get("_name");
            if (s3 == null || s3.equals("config") || s3.length() == 0) {
                hashmap.put("_name", s1);
            }
            setValuesTable(hashmap);
        }
        while (enumeration.hasMoreElements()) {
            try {
                HashMap hashmap1 = (HashMap) enumeration.nextElement();
                if (isMonitorFrame(hashmap1)) {
                    Monitor monitor = null;
                    monitor = createMonitor(hashmap1, s2);
                    monitor.group = this;
                    addElement(monitor);
                } else if (isReportFrame(hashmap1)) {
                    HistoryReport historyreport = HistoryReport.createHistoryReportObject(hashmap1);
                    addElement(historyreport);
                }
            } catch (ClassNotFoundException classnotfoundexception) {
                LogManager.log("Error", "Error loading monitor, not found: " + classnotfoundexception.getMessage());
            } catch (Exception exception) {
                LogManager.log("Error", "Error loading monitor, " + exception);
                System.err.println("Error loading monitor, " + exception);
                exception.printStackTrace();
            }
        }
    }

    private String getDynamicPath() {
        String s = file.getAbsolutePath();
        int i = s.lastIndexOf(".");
        return s.substring(0, i) + ".dyn";
    }

    void readDynamic() {
        String s = getDynamicPath();
        if(s.contains(".dyn")&&!s.contains("__Health__")){
        	s=file.getName();
        }
        int i = 0;
        if ((new File(s)).exists()) {
            try {
              Array array = FrameFile.readFromFile(s);
      //         Array array=readFromFile(s);
                Enumeration enumeration = array.elements();
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap = (HashMap) enumeration.nextElement();
                    String s1 = (String) hashmap.get("id");
                    i ++;
                    if (s1 == null) {
                        LogManager.log("Error reading monitor, " + i + ", file: " + s);
                    } else if (s1.equals("-1")) {
                        mergeFromHashMap(hashmap);//setValuesTable(hashmap, true)
                    } else {
                        Monitor monitor = (Monitor) getElementByID(s1);
                        if (monitor != null) {
                            monitor.mergeFromHashMap(hashmap);
                        }
                    }
                }
            } catch (Exception ioexception) {
                LogManager.log("Error", "Error reading group, file: " + s + ", error: " + ioexception);
            }
        }
    }

	public synchronized void saveDynamic() {
        Array array = new Array();
        setProperty("id", "-1");
        array.add(getValuesTable());
        Array array1 = getElementsOfClass("COM.dragonflow.SiteView.Monitor", false);
        Monitor monitor;
        for (Enumeration enumeration = array1.elements(); enumeration.hasMoreElements(); array.add(monitor.getValuesTable())) {
            monitor = (Monitor) enumeration.nextElement();
            monitor.setProperty("id", monitor.getProperty(pID));
        }

        String s = getDynamicPath();
        
        try {
            FrameFile.writeToFile(s, array, "_", false);
        } catch (IOException ioexception) {
            System.gc();
            Platform.sleep(1000L);
            LogManager.log("Error", "Error writing group, file(running garbage collection): " + s + ", error: " + ioexception);
            try {
                FrameFile.writeToFile(s, array, "_", false);
            } catch (IOException ioexception1) {
                LogManager.log("Error", "No luck, still error writing group, file: " + s + ", error: " + ioexception);
            }
        }
        setProperty(pLastSaved, String.valueOf(Platform.timeMillis()));
    }

    public Enumeration getParentActionRules() {
        if (getProperty(pParent).length() > 0) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(I18N.toDefaultEncoding(getProperty(pParent)));
            if (monitorgroup != null) {
                Array array = monitorgroup.getElementsOfClass("COM.dragonflow.SiteView.Rule", false, false);
                Array array1 = (Array) Filtering.select(array, new RuleGroupIs(2));
                Rule rule;
                for (Enumeration enumeration = monitorgroup.getParentActionRules(); enumeration.hasMoreElements(); array1.add(rule)) {
                    rule = (Rule) enumeration.nextElement();
                }

                return array1.elements();
            }
        }
        return cEmptyArray.elements();
    }

    /**
     * 
     */
    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        String s = "";
        if (stringproperty == pCategory) {
            String s1 = "nodata";
            for (Enumeration enumeration = getMonitors(); enumeration.hasMoreElements();) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (!monitor.isDisabled()) {
                    s1 = Monitor.getWorstCategory(s1, monitor.getProperty(pCategory));
                    String s4 = (String) Monitor.categoryMap.get(Monitor.WORST_CATEGORY);
                    if (s1.equals(s4)) {
                        return s1;
                    }
                }
            }

            s = s1;
        } else if (stringproperty == pLastCategory) {
            s = super.getProperty(stringproperty);
        } else if (stringproperty == pAlertDisabled) {
            String s2 = "";
            Enumeration enumeration1 = getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor1 = (Monitor) enumeration1.nextElement();
                s2 = monitor1.getProperty(pAlertDisabled);
                if (s2.length() == 0) {
                    break;
                }
            }
            s = s2;
        } else if (stringproperty == pRunning) {
            String s3 = "";
            Enumeration enumeration2 = getMonitors();
            while (enumeration2.hasMoreElements()) {
                Monitor monitor2 = (Monitor) enumeration2.nextElement();
                s3 = monitor2.getProperty(pRunning);
                if (s3.length() == 0) {
                    break;
                }
            }
            s = s3;
        } else if (stringproperty == pMeasurement) {
            int i = 0;
            Enumeration enumeration3 = getMonitors();
            while (enumeration3.hasMoreElements()) {
                Monitor monitor3 = (Monitor) enumeration3.nextElement();
                int j1 = monitor3.getPropertyAsInteger(pMeasurement);
                if (j1 > i) {
                    i = j1;
                }
            }
            s = "" + i;
        } else if (stringproperty == pMaxErrorCount) {
            int j = 0;
            int i1 = 0;
            synchronized (Monitor.countLock) {
                Enumeration enumeration6 = getMonitors();
                while (enumeration6.hasMoreElements()) {
                    Monitor monitor5 = (Monitor) enumeration6.nextElement();
                    int k1 = monitor5.getPropertyAsInteger(pErrorCount);
                    if (!monitor5.getProperty(pCategory).equals(Monitor.ERROR_CATEGORY)) {
                        k1 = 0;
                    }
                    if (k1 > j) {
                        j = k1;
                        i1 = 1;
                    } else if (k1 == j) {
                        i1 ++;
                    }
                }
            }
            if (j > 0 && i1 > 1) {
                j = 0x98967f;
            }
            s = "" + j;
        } else if (stringproperty == pMonitorsInGroup) {
            int k = 0;
            for (Enumeration enumeration4 = getMonitors(); enumeration4.hasMoreElements();) {
                enumeration4.nextElement();
                k ++;
            }

            s = "" + k;
        } else if (stringproperty == pGroupErrorDisplay) {
            int l = 0;
            synchronized (Monitor.countLock) {
                Enumeration enumeration5 = getMonitors();
                while (enumeration5.hasMoreElements()) {
                    Monitor monitor4 = (Monitor) enumeration5.nextElement();
                    if (monitor4.getProperty(pCategory).equals(Monitor.ERROR_CATEGORY)) {
                        l ++;
                    }
                }
            }
            s = "" + l;
        } else if (stringproperty == pGroupID) {
            if (Platform.isPortal()) {
                s = getFullID();
            } else {
                s = getProperty(pID);
            }
        } else if (stringproperty == pSiteViewLogfilePath) {
            if (getSetting("_logInGroup").length() > 0) {
                s = Platform.getRoot() + File.separator + "accounts" + File.separator + I18N.toDefaultEncoding(getProperty(pID)) + File.separator + "logs" + File.separator + "SiteView.log";
            }
        } else if (stringproperty == pSiteViewLogName) {
            s = getLogName("SiteViewLog");
        } else if (stringproperty == pURLLogName) {
            s = getLogName("URL");
        } else if (stringproperty == pErrorLogName) {
            s = getLogName("Error");
        } else if (stringproperty == pAlertLogName) {
            s = getLogName("Alert");
        } else {
            s = super.getProperty(stringproperty);
        }
        return s;
    }

    public static MonitorGroup getMonitorGroup(String groupid) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        SiteViewObject siteviewobject = siteviewgroup.getElementByID(groupid);
        MonitorGroup monitorgroup = null;
        if (siteviewobject instanceof SubGroup) {
            SubGroup subgroup = (SubGroup) siteviewobject;
            monitorgroup = subgroup.lookupGroup();
        } else if (siteviewobject instanceof MonitorGroup) {
            monitorgroup = (MonitorGroup) siteviewobject;
        }
        return monitorgroup;
    }

    public String getLogName(String s) {
        String s1 = s;
        String s2 = "";
        String s3 = getSetting("_logInGroup", false);
        String s4 = I18N.toDefaultEncoding(getProperty(pID));
        String s5;
        if (Platform.isSiteSeerServer() && s3.length() <= 0) {
            s5 = getProperty(pAccountName);
        } else if (s3.length() > 0) {
            s5 = s4;
        } else {
            s5 = "";
        }
        if (s5.length() > 0) {
            s1 = s1 + s5;
            synchronized (getClass()) {
                if (!LogManager.loggerRegistered(s1)) {
                    int i = TextUtils.toInt(getSetting("_maxLogSize"));
                    int j = TextUtils.toInt(getSetting("_logKeepDays"));
                    long l = TextUtils.toLong(getSetting("_dailyLogTotalLimit"));
                    int k = TextUtils.toInt(getSetting("_dailyLogKeepDays"));
                    String s6 = getSetting("_dailySiteViewLogs");
                    String s7 = "SiteView.log";
                    if (s.equals("Error")) {
                        s7 = "error.log";
                        s2 = "Error";
                        s6 = "";
                    } else if (s.equals("Alert")) {
                        s7 = "alert.log";
                        s2 = "Alert";
                        s6 = "";
                    } else if (s.equals("URL")) {
                        s7 = "url.log";
                        s2 = "URL";
                        s6 = "";
                    } else if (s.equals("SiteViewLog")) {
                        s2 = "AccountSiteViewLog";
                    }
                    String s8 = Platform.getDirectoryPath("logs", s5);
                    String s9 = s8 + File.separator + s7;
                    try {
                        File file1 = new File(s8);
                        boolean flag = file1.exists();
                        if (!flag) {
                            flag = file1.mkdirs();
                            if (!flag) {
                                LogManager.log("Error", "Could not create log directory: " + file1.getAbsolutePath());
                            } else {
                                Platform.chmod(file1, "rwx");
                            }
                        }
                        if (flag) {
                            // Object obj = null;
                            // TODO need review
                            COM.dragonflow.Log.BaseFileLogger basefilelogger;
                            if (s6.length() > 0) {
                                basefilelogger = new DailyFileLogger(s9, l, k);
                            } else {
                                basefilelogger = new FileLogger(s9, i, j);
                            }
                            basefilelogger.setEchoTo(s2);
                            LogManager.registerLogger(s1, basefilelogger);
                        }
                    } catch (Exception e) {
                        System.err.println("Could not open SiteView.log at " + s9);
                    }
                }
            }
        }
        return s1;
    }

    public String findLogInGroup(String s) {
        String s1 = "";
        if (s != null && s.length() > 0) {
            String groupfile = Platform.getRoot() + File.separator + "groups" + File.separator + s + ".mg";
            try {
                Array array = FrameFile.readFromFile(groupfile);
                HashMap hashmap = (HashMap) array.at(0);
                String s3 = (String) hashmap.get("_logInGroup");
                if (s3 != null && s3.length() > 0) {
                    s1 = s;
                } else {
                    s1 = findLogInGroup(I18N.toDefaultEncoding((String) hashmap.get("_parent")));
                }
            } catch (IOException ioexception) {
                LogManager.log("Error", "Error getting group: " + ioexception);
            } catch (Exception exception) {
                LogManager.log("Error", "Error finding _logInGroup: " + exception);
            }
        }
        return s1;
    }

    public String printAlertIconLink(HTTPRequest httprequest, String s, String s1, Array array) {
        boolean flag = false;
        if (s1.equals("_config") || s1.equals(s)) {
            flag = false;
        }
        StringBuffer stringbuffer = createAlertIconLink(flag, httprequest);
        return stringbuffer.toString();
    }

    public void writeAllHTML() {
        if (SiteViewGroup.shouldWriteHTML()) {
            writeHTML("administrator", "true");
            if (Platform.isUserAccessAllowed()) {
                writeHTML("user", "true");
            }
        }
    }

    void writeHTML(String s, String s1) {
        HTTPRequest httprequest = new HTTPRequest();
        httprequest.setValue("noRefresh", s1);
        httprequest.setUser(s);
        String s2 = htmlPath(httprequest);
        try {
            PrintWriter printwriter = FileUtils.MakeOutputWriter(new FileOutputStream(s2));
            printwriter.println("<HTML>");
            try {
                printPage(printwriter, httprequest);
            } catch (Exception exception) {
                printwriter.println("<hr>There was an unexpected problem:<br>" + exception.toString() + "<hr>");
            }
            printwriter.println("</HTML>");
            printwriter.close();
            Platform.chmod(s2, "rw");
        } catch (IOException ioexception) {
            message("Could not write file: " + s2 + ", " + ioexception);
        }
    }

    public String htmlPath(HTTPRequest httprequest) {
        String s = HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pID)));
        return Platform.getRoot() + "/" + httprequest.getAccountDirectory() + "/Detail" + s + ".html";
    }

    public static String getGaugeArt(String s) {
        int i = TextUtils.toInt(s);
        i = Math.min(i, 10);
        i = Math.max(i, 0);
        return "/SiteView/htdocs/artwork/gauge" + i + ".gif";
    }

    public static String getCategoryAlt(String s) {
        if (s.equals("good")) {
            return "g";
        }
        if (s.equals("error")) {
            return "E";
        }
        if (s.equals("warning")) {
            return "W";
        }
        if (s.equals("running")) {
            return "R";
        } else {
            return " ";
        }
    }

    public static String getCategoryArt(String s) {
        if (s.equals("good")) {
            return "/SiteView/htdocs/artwork/okay.gif";
        }
        if (s.equals("error")) {
            return "/SiteView/htdocs/artwork/error.gif";
        }
        if (s.equals("warning")) {
            return "/SiteView/htdocs/artwork/warning.gif";
        }
        if (s.equals("running")) {
            return "/SiteView/htdocs/artwork/running.gif";
        } else {
            return "/SiteView/htdocs/artwork/disabled.gif";
        }
    }

    public static String getReportArt(String s) {
        return "/SiteView/htdocs/artwork/reporticon" + s + ".gif";
    }

    public static String getAckArt(String s) {
        if (s.equals("AcknowledgeClear")) {
            return "/SiteView/htdocs/artwork/acked.gif";
        } else {
            return "/SiteView/htdocs/artwork/ack.gif";
        }
    }

    public static String getSmallCategoryArt(String s) {
        if (s.equals("good")) {
            return "/SiteView/htdocs/artwork/smallokay.gif";
        }
        if (s.equals("error")) {
            return "/SiteView/htdocs/artwork/smallerror.gif";
        }
        if (s.equals("warning")) {
            return "/SiteView/htdocs/artwork/smallwarning.gif";
        }
        if (s.equals("running")) {
            return "/SiteView/htdocs/artwork/smallrunning.gif";
        } else {
            return "/SiteView/htdocs/artwork/smalldisabled.gif";
        }
    }

    public static String getTinyCategoryArt(String s) {
        if (s.equals("good")) {
            return "/SiteView/htdocs/artwork/okaysm.gif";
        }
        if (s.equals("error")) {
            return "/SiteView/htdocs/artwork/errorsm.gif";
        }
        if (s.equals("warning")) {
            return "/SiteView/htdocs/artwork/warningsm.gif";
        }
        if (s.equals("running")) {
            return "/SiteView/htdocs/artwork/runningsm.gif";
        } else {
            return "/SiteView/htdocs/artwork/disabledsm.gif";
        }
    }

    public static void printFooter(PrintWriter printwriter, int i, HTTPRequest httprequest) {
        long l = TextUtils.toLong(httprequest.getUserSetting("_timeOffset")) * 1000L;
        String s = TextUtils.prettyDate(Platform.makeDate(), l);
        printwriter.println("<CENTER><font size=-1>(" + Platform.refreshLabel(i) + LocaleUtils.getResourceBundle().getString("LastRefresh") + " " + s + ")</font>\n");
        CGI.printFooter(printwriter, httprequest);
    }

    public boolean groupDisabled() {
        return whyGroupDisabled().length() > 0;
    }

    public String whyGroupDisabled() {
        String s = "";
        if (getProperty(pDisabled).length() > 0) {
            s = accountDisabled;
        }
        return s;
    }

    public boolean tryOwnerForSettings() {
        return getProperty("_account").length() == 0;
    }

    public void printPage(PrintWriter printwriter, HTTPRequest httprequest) throws Exception {
        printPage(printwriter, httprequest, httprequest.getValue("_health").length() > 0);
    }

    /**
     * 
     * 
     * @param printwriter
     * @param httprequest
     * @param flag
     * @throws Exception
     */
    public void printPage(PrintWriter printwriter, HTTPRequest httprequest, boolean flag) throws Exception {
        // TODO need review
        flag = flag || getProperty(pID).equals("__Health__");
        if (flag) {
            httprequest.setValue("_health", "true");
        }
        String s = getProperty(pName);
        String s1 = I18N.toDefaultEncoding(getProperty(pID));
        if (!CGI.isGroupAllowedForAccount(s1, httprequest) && Platform.isStandardAccount(httprequest.getAccount())) {
            throw new HTTPRequestException(557);
        }
        String s2 = HTTPRequest.encodeString(s1);
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        int i = getSettingAsLong("_groupRefreshRate", 60);
        if (flag) {
            CGI.printBodyHeader(printwriter, "SiteView Health", siteviewgroup.refreshTag(i, "/SiteView/" + httprequest.getAccountDirectory() + "/Detail" + s2 + ".html?_health=true"), I18N.nullEncoding());
            CGI.menus menus1 = getNavItems(httprequest);
            CGI.printButtonBar(printwriter, "Health.htm", "Health", httprequest, MasterConfig.getMasterConfig(), menus1, false);
        } else {
            CGI.printBodyHeader(printwriter, s + " Detail", siteviewgroup.refreshTag(i, "Detail" + s2 + ".html"));
            CGI.menus menus2 = getNavItems(httprequest);
            CGI.printButtonBar(printwriter, "Group.htm#detail", "", httprequest, MasterConfig.getMasterConfig(), menus2, false);
        }
        Array array = CGI.getGroupFilterForAccount(httprequest);
        String s3;
        if (array.size() > 0 && !array.at(0).equals(httprequest.getAccount())) {
            s3 = CGI.getGroupFullName(I18N.toDefaultEncoding(getProperty(pID)));
        } else {
            s3 = CGI.getGroupFullLinks(this, httprequest);
        }
        String s4 = "";
        String s5 = getSetting(AtomicMonitor.DEPENDS_ON);
        if (s5.length() > 0) {
            AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s5.replace(' ', '/'));
            if (atomicmonitor != null) {
                String s6 = getHistoryParam(atomicmonitor.getProperty(pID), atomicmonitor.getProperty(pGroupID));
                MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(atomicmonitor.getProperty(pGroupID));
                String s7 = getSetting(AtomicMonitor.DEPENDS_CONDITION);
                String s9 = "Ok";
                if (s7.equals("error")) {
                    s9 = "Error";
                }
                s4 = "<p>(Depends on " + s9 + " for <A HREF=/SiteView/cgi/go.exe/SiteView?page=adhocReport&" + s6 + "&account=" + httprequest.getAccount() + "&group=" + s2 + ">" + atomicmonitor.getProperty(pName) + "</A>" + " in group " + "<A HREF="
                        + CGI.getGroupDetailURL(httprequest, atomicmonitor.getProperty(pGroupID)) + ">" + monitorgroup.getProperty(pName) + "</A>)</p>";
            }
        }
        int j = 1;
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = getMultipleValues("_description");
        if (enumeration.hasMoreElements()) {
            String s8;
            for (s8 = (String) enumeration.nextElement(); enumeration.hasMoreElements(); s8 = s8 + enumeration.nextElement()) {
            }
            stringbuffer.append("<p><center><TABLE border=1 cellspacing=0>");
            if (s8.indexOf(":") != -1 && s8.indexOf("<") == -1 && s8.indexOf(">") == -1) {
                for (Enumeration enumeration1 = getMultipleValues("_description"); enumeration1.hasMoreElements();) {
                    String s10 = (String) enumeration1.nextElement();
                    String s12 = "";
                    int k = s10.indexOf(':');
                    if (k != -1) {
                        s12 = s10.substring(k + 1);
                        s10 = s10.substring(0, k);
                    }
                    stringbuffer.append("<tr><td><b>&nbsp;" + s10 + "&nbsp;</b></td><td>&nbsp;" + s12 + "&nbsp;</td></tr>");
                    j ++;
                }

            } else {
                stringbuffer.append("<tr><td colspan=\"2\">" + s8 + "</td></tr>");
            }
            stringbuffer.append("</TABLE></center>");
        }
        int ai[] = { CATEGORY_COLUMN, ACKNOWLEDGE_COLUMN, GAUGE_COLUMN, ALERT_COLUMN, REPORT_COLUMN, STATUS_COLUMN, NAME_COLUMN, MORE_COLUMN, EDIT_COLUMN, REFRESH_COLUMN, CUSTOM_COLUMN, UPDATED_COLUMN, DELETE_COLUMN };
        String s11 = "";
        String s13 = "";
        Array array1 = new Array();
        SiteViewGroup siteviewgroup1 = SiteViewGroup.currentSiteView();
        Monitor monitor = (Monitor) siteviewgroup1.getElement(s1);
        String s14 = flag ? "Health" : "Group";
        if (siteviewgroup.getSetting("_alertIconLink").length() > 0) {
            getGroupAlerts(s1, array1);
            s11 = "<td valign=\"middle\" align=\"right\"><p style=\"font-weight: bold; font-family: 'Arial', 'Helvetica', sans-serif;\">" + s14 + " Alerts :</td>" + printAlertIconLink(httprequest, s1, "_config", array1);
            j += 2;
        }
        if (siteviewgroup.getSetting("_reportIconLink").length() > 0) {
            s13 = "<td valign=\"middle\" align=\"right\"><p style=\"font-weight: bold; font-family: 'Arial', 'Helvetica', sans-serif;\">" + s14 + " Reports :</td>" + printTableReportEntry(true, httprequest);
            j += 2;
        }
        String s15 = "<table border=\"0\" width=\"90%\"><tr><td colspan=" + j + ">" + stringbuffer.toString() + "</td></tr><tr><td><h2>" + (flag ? "" : "Monitors in the Group: ") + s3 + "</h2></td>" + s11 + s13 + "</tr></table>";
        String s16 = printMonitorTable(printwriter, httprequest, s15, s4, ai, getMonitors(), array1, false);
        printCategoryInsertHTML(s16, this, printwriter);
        printwriter.println("<p><font size=\"-1\">Click the state icon for monitor and alert disable/enable options. </font></p>");
        if (((MonitorGroup) monitor).groupSchedulerActive(false)) {
            printwriter.println("<tr><td><b>Note that this group is using a group schedule, so the monitors of this group will not run according to their own schedule.</b></td></tr>");
        }
        printwriter.println("<hr>");
        String s17 = flag ? "&_health=true" : "";
        String s18 = flag ? "Health " : "";
        printwriter.println("<TABLE BORDER=0 CELLSPACING=4 WIDTH=100%>");
        if (httprequest.actionAllowed("_monitorEdit") || httprequest.actionAllowed("_groupEdit")) {
            String s19 = flag ? "Health Monitoring" : "Group";
            printwriter.print("<tr><td colspan=2><font size=+1><b>Add to " + s19 + ":</b></font></td></tr>");
        } else if (!httprequest.actionAllowed("_groupEdit")) {
            printwriter.print("<tr><td colspan=2><font size=+1><b>" + s14 + " Actions:</b></font></td></tr>");
        }
        if (httprequest.actionAllowed("_monitorEdit")) {
            if (flag && s1.equals("__Health__")) {
                printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=AddDefaultMonitors&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + "&parent=" + s2
                        + ">Default Monitors</A></td> <td>Append a set of default Heath Monitors</td></tr>");
            }
            printwriter.print("<tr><td width=15%><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=AddList&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">" + "Monitor</A> </td> <td>Add a new monitor instance to this " + s18
                    + "group</td></tr>");
            if (!Platform.isSiteSeerServer() && !flag) {
                printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitorSet&operation=AddSet&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">" + "Monitor Set</A> </td><td>Add a new Monitor Set instance to this "
                        + s18 + "group</td></tr>");
                String as[] = (new File(monitorSetPage.SOLUTIONS_DIR)).list();
                if (as != null && as.length > 0) {
                    printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitorSet&operation=PickSolution&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">"
                            + "Solution Template</A> </td><td>Add a new Monitoring Solution to this " + s18 + "group</td></tr>");
                }
            }
        }
        if (httprequest.actionAllowed("_groupEdit")) {
            if (!httprequest.getPermission("_link", "deleteGroup").equals("hidden")) {
                printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Add&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + "&parent=" + s2 + ">Subgroup</A></td> <td>Create a new subgroup within this " + s18
                        + "group</td></tr>");
                printwriter.print("<tr><td colspan=2><hr></td></tr>");
                printwriter.print("<tr><td colspan=2><font size=+1><b>" + s14 + " Actions:</b></font></td></tr>");
                if (!s1.equals("__Health__")) {
                    printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Edit&group=" + s2 + "&account=" + httprequest.getAccount() + s17);
                    printwriter.println(">Edit</A> </td> <td>Edit the Name, Description, or Dependencies of this " + s18 + "group</td></tr>");
                }
            } else {
                printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Edit&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">Rename</A> </td> <td>Change the Name of this " + s18 + "group</td></tr>");
            }
        }
        String s20 = monitor.getProperty(Monitor.pName);
        if (s20 == null || s20.equals("config") || s20.length() == 0) {
            s20 = I18N.toNullEncoding(s1);
        }
        String s21 = s2 + "&account=" + httprequest.getAccount() + s17 + "&returnURL=" + I18N.toNullEncoding(s1) + "&returnLabel=" + HTTPRequest.encodeString(I18N.UnicodeToString(s20, I18N.nullEncoding()), I18N.nullEncoding());
        if (httprequest.actionAllowed("_groupEdit") && flag && s1.equals("__Health__")) {
            String s22 = siteviewgroup.getSetting("_healthDisableLogging").length() <= 0 ? "Disable" : "Enable";
            String s23 = siteviewgroup.getSetting("_healthDisableLogging").length() <= 0 ? "Disable logging on all health monitors.<br><b>NOTE: This will also disable reports for Health monitors.</b>"
                    : "Enable logging on all health monitors.<br><b>NOTE: Reports will only work for Health groups from the time logging is enabled.</b>";
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=flipHealthLogging>" + s22 + " Health Logging</A> </td> <td>" + s23 + "</td></tr>");
        }
        if (httprequest.actionAllowed("_groupDisable") && !httprequest.getPermission("_link", "disableGroup").equals("hidden")) {
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation=Disable&group0=" + s21 + ">Disable</A> </td> <td>Disable all the monitors in this " + s18 + "group or temporarily disable alerts "
                    + "for monitors in this " + s18 + "group</td></tr>");
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation=Enable&group0=" + s21 + ">Enable</A> </td> <td>Enable all the monitors previously disabled in this " + s18 + "group or enable "
                    + "temporarily disabled alerts</td></tr>");
        }
//        if (httprequest.actionAllowed("_groupEdit") && TopazManager.getInstance().getTopazServerSettings().isConnected()) {
//            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation=" + URLEncoder.encode(managePage.TOPAZ_LOGGING_OP) + "&group0=" + s21 + ">Enable Logging  To " + TopazInfo.getTopazName() + "</A> </td> <td>"
//                    + TopazInfo.getTopazName() + " logging options for this " + s18 + "group</td></tr>");
//        }
        if (httprequest.actionAllowed("_groupRefresh") && siteviewgroup.internalServerActive()) {
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation=Refresh&group=" + s21 + ">Refresh</A> </td><td>Refresh all the monitors in this " + s18 + "group</td></tr>");
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (TextUtils.getValue(hashmap, "_acknowledgeMonitors").equalsIgnoreCase("CHECKED") && httprequest.actionAllowed("_monitorAcknowledge") && siteviewgroup.internalServerActive()) {
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation=Acknowledge&group0=" + s21 + ">Acknowledge</A></td><td>Acknowledge the state of all the monitors in this " + s18 + "group</td></tr>");
        }
        if (httprequest.actionAllowed("_groupEdit")) {
            printwriter.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=reorder&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">Reorder</A></td><td>Edit the display order of monitors in this " + s18 + "group</td></tr>");
        }
        if (httprequest.actionAllowed("_groupEdit") && !httprequest.getPermission("_link", "deleteGroup").equals("hidden") && !s1.equals(httprequest.getAccount())) {
            if (s1.equals("__Health__")) {
                printwriter.println("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Delete&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">Delete</A></td><td>Delete " + "all subgroups and monitors in " + s18
                        + "<br></td></tr>");
            } else {
                printwriter.println("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Delete&group=" + s2 + "&account=" + httprequest.getAccount() + s17 + ">Delete</A></td><td>Delete this " + s18 + "group and all monitors in this "
                        + s18 + "group<br></td></tr>");
            }
        }
        if (httprequest.actionAllowed("_groupEdit")) {
            printwriter.print("<tr><td colspan=2><hr></td></tr>");
            printwriter.print("<tr><td colspan=2><font size=+1><b>View:</b></font></td></tr>");
        }
        String s24 = "<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=alert&operation=List&view=Group&account=" + httprequest.getAccount() + "&groupFilter=" + s2 + ">Alerts</A> </td><td>View alerts for this " + s18 + "group</td></tr>";
        String s25 = "";
        Enumeration enumeration2;
        if (Platform.isStandardAccount(httprequest.getAccount())) {
            enumeration2 = siteviewgroup.getReports();
        } else {
            MonitorGroup monitorgroup1 = (MonitorGroup) siteviewgroup.getElement(httprequest.getAccount());
            enumeration2 = monitorgroup1.getReports();
        }

        while (enumeration2.hasMoreElements()) {
            HistoryReport historyreport = (HistoryReport) enumeration2.nextElement();
            Enumeration enumeration3 = historyreport.getMultipleValues(HistoryReport.pTargets);
            if (enumeration3.hasMoreElements()) {
                String s26 = (String) enumeration3.nextElement();
                if (s26.equals(I18N.toDefaultEncoding(getProperty(pID))) && !enumeration3.hasMoreElements()) {
                    int l = historyreport.getPropertyAsInteger(HistoryReport.pWindow);
                    String s27 = "";
                    if (l == 0x15180) {
                        s27 = "Daily Report";
                    } else if (l == 0x93a80) {
                        s27 = "Weekly Report";
                    } else if (l == 0x278d00) {
                        s27 = "Monthly Report";
                    } else {
                        s27 = TextUtils.secondsToString(l);
                    }
                    String s28 = "htdocs";
                    if (httprequest.getAccount().equals("user")) {
                        s28 = "userhtml";
                    }
                    File file1 = new File(Platform.getDirectoryPath(s28 + "/Reports-" + historyreport.getProperty(HistoryReport.pQueryID), httprequest.getAccount()) + "/index.html");
                    if (file1.exists()) {
                        if (s25.length() > 0) {
                            s25 = s25 + ", ";
                        }
                        s25 = s25 + "<A HREF=" + Platform.getURLPath(s28 + "/Reports-" + historyreport.getProperty(HistoryReport.pQueryID), httprequest.getAccount()) + "/index.html target=reports>" + s27 + "</A>";
                    }
                }
            }
        }

        if (httprequest.actionAllowed("_alertList")) {
            printwriter.println("<tr><td>" + s24 + "</td></tr>");
        }
        if (s25.length() > 0) {
            s25 = "<tr><td>" + s25 + "<td>View reports for this " + s18 + "group</td></tr>";
        }
        printwriter.println(s25);
        if ((httprequest.actionAllowed("_groupEdit") || httprequest.actionAllowed("_groupDisable") || httprequest.actionAllowed("_groupRefresh")) && !httprequest.getPermission("_link", "deleteGroup").equals("hidden")) {
            printwriter.print("<tr><td colspan=2><hr></td></tr>");
            printwriter.println("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&account=" + httprequest.getAccount() + s17 + ">Manage</A></td> <td>Move, duplicate, delete, disable/enable monitors in any group</td></tr>");
        }
        if (httprequest.actionAllowed("_groupEdit")) {
            printwriter.println("</table><hr>");
        }
        printFooter(printwriter, i, httprequest);
    }

    public static String printMonitorTable(PrintWriter printwriter, HTTPRequest httprequest, String s, String s1, int ai[], Enumeration enumeration) {
        Array array = new Array();
        boolean flag = false;
        return printMonitorTable(printwriter, httprequest, s, s1, ai, enumeration, array, flag);
    }

    /**
     * 
     * 
     * @param printwriter
     * @param httprequest
     * @param s
     * @param s1
     * @param ai
     * @param enumeration
     * @param array
     * @param flag
     * @return
     */
    public static String printMonitorTable(PrintWriter printwriter, HTTPRequest httprequest, String s, String s1, int ai[], Enumeration enumeration, Array array, boolean flag) {
        // TODO need review
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        HashMap hashmap = MasterConfig.getMasterConfig();
        Monitor monitor = null;
        if (enumeration.hasMoreElements()) {
            monitor = (Monitor) enumeration.nextElement();
        }
        String s2 = TextUtils.getValue(hashmap, "_customGroupTableTag");
        printwriter.print(s);
        if (s1.length() > 0) {
            printwriter.print(s1);
        }
        if (s2.length() > 0) {
            expandField(httprequest, printwriter, monitor, s2, "", "", "");
        } else {
            printwriter.print("<TABLE WIDTH=\"100%\" BORDER=1 CELLSPACING=0><TR class=\"tabhead\">");
        }
        boolean aflag[] = new boolean[ai.length];
        for (int i = 0; i < aflag.length; i ++) {
            aflag[i] = true;
        }

        String s3 = "";
        String s4 = "";
        String s5 = Monitor.NODATA_CATEGORY;
        for (int j = 0; j < ai.length; j ++) {
            String s7 = columnText[ai[j]];
            if (ai[j] == REFRESH_COLUMN && httprequest.getValue("noRefresh").length() > 0) {
                aflag[j] = false;
                continue;
            }
            if ((ai[j] == EDIT_COLUMN || ai[j] == DELETE_COLUMN) && !httprequest.actionAllowed("_monitorEdit")) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == REFRESH_COLUMN && !httprequest.actionAllowed("_monitorRefresh")) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == ACKNOWLEDGE_COLUMN && (!TextUtils.getValue(hashmap, "_acknowledgeMonitors").equalsIgnoreCase("CHECKED") || !httprequest.actionAllowed("_monitorAcknowledge"))) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == ALERT_COLUMN && !TextUtils.getValue(hashmap, "_alertIconLink").equalsIgnoreCase("CHECKED")) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == REPORT_COLUMN && !TextUtils.getValue(hashmap, "_reportIconLink").equalsIgnoreCase("CHECKED")) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == MORE_COLUMN && !httprequest.actionAllowed("_monitorTools")) {
                aflag[j] = false;
                continue;
            }
            if (ai[j] == CUSTOM_COLUMN) {
                String s9 = siteviewgroup.getSetting("_monitorTableCustom");
                if (s9.length() == 0) {
                    aflag[j] = false;
                    continue;
                }
                String as[] = TextUtils.split(s9, "|");
                s3 = as[0];
                if (as.length > 1) {
                    s7 = as[1];
                }
                if (as.length > 2) {
                    s4 = as[2];
                }
            }
            if (ai[j] != GAUGE_COLUMN || TextUtils.getValue(hashmap, "_displayGauges").equalsIgnoreCase("CHECKED")) {
                printwriter.print("<TH>" + s7 + "</TH>\n");
            }
        }

        printwriter.print("</TR>");
        if (monitor == null) {
            if (s.indexOf("Browse") > 0) {
                printwriter.println("<TR><TD COLSPAN=" + ai.length + " align=center><p><b>No monitors found for match criteria.</b><br> Use the <b>Category to Show/Hide</b> "
                        + " selection above to select the monitor status category to display</p></TD></TR>\n");
            } else {
                printwriter.println("<TR><TD COLSPAN=" + ai.length + " align=center><p><b>No monitors currently defined.</b><br> Use the <b>Add to Group</b> " + " actions below to add monitors or create subgroups</p></TD></TR>\n");
            }
        } else {
            while (true) {
                String s6 = escapeHTML(monitor.getProperty(pName));
                String s8 = I18N.toDefaultEncoding(monitor.getProperty(Monitor.pGroupID));
                Monitor monitor1 = (Monitor) siteviewgroup.getElement(s8);
                String s10 = I18N.toNullEncoding(s8);
                if (monitor1 != null) {
                    s10 = monitor1.getProperty(Monitor.pName);
                }
                String s11 = HTTPRequest.encodeString(s8);
                String s12 = "Detail" + s11 + ".html";
                String s13 = monitor.getProperty(pCategory);
                String s14 = "&nbsp;";
                if (monitor.getPropertyAsLong(pLastUpdate) > 0L) {
                    long l = TextUtils.toLong(httprequest.getUserSetting("_timeOffset")) * 1000L;
                    s14 = TextUtils.prettyDate(new Date(monitor.getPropertyAsLong(pLastUpdate) + l));
                }
                s5 = Monitor.getWorstCategory(s5, s13);
                boolean flag1 = monitor.isDisabled();
                boolean flag2 = monitor.isAlertTemporarilyDisabled();
                if (flag1) {
                    s6 = "<B>(disabled)</B>" + s6;
                } else if (flag2) {
                    String s15 = monitor.getProperty(pAlertDisabled);
                    int k = s15.indexOf(";");
                    int i1 = s15.indexOf("*");
                    if (k > 0 && (i1 == -1 || i1 != -1 && k < i1)) {
                        s15 = s15.substring(k + 1);
                    }
                    i1 = s15.indexOf("*");
                    if (i1 > 0) {
                        String s19 = "";
                        s19 = s15.substring(i1 + 1);
                        s15 = s15.substring(0, i1);
                        s15 = s15 + " [" + s19 + "]";
                    }
                    s6 = "<B>(Alerts Disabled Until:" + s15 + ")</B>\n<BR>" + s6;
                }
                String s16 = monitor.getProperty(pMeasurement);
                if (flag1) {
                    s16 = "";
                    s13 = "nodata";
                }
                String s17;
                if (s16.equals("")) {
                    s17 = "<img src=/SiteView/htdocs/artwork/empty55.gif height=5 width=5>";
                } else {
                    s17 = "<img height=24 width=39 src=" + getGaugeArt(s16) + " alt=" + s16 + ">";
                }
                String s18 = monitor.getProperty(pDescription);
                if (s18.length() != 0) {
                    s18 = s13 + ", " + s18;
                } else {
                    s18 = s13;
                }
                String s20 = TextUtils.getValue(hashmap, "_customGroupRow");
                if (s20.length() > 0) {
                    expandField(httprequest, printwriter, monitor, s20, s3, "", "");
                } else {
                    printwriter.print("<TR>");
                }
                for (int j1 = 0; j1 < ai.length; j1 ++) {
                    if (!aflag[j1]) {
                        continue;
                    }
                    if (ai[j1] == CATEGORY_COLUMN) {
                        monitor.printTableCategoryEntry(printwriter, httprequest, flag1, flag2, s18, s13);
                        continue;
                    }
                    if (ai[j1] == GAUGE_COLUMN) {
                        if (TextUtils.getValue(hashmap, "_displayGauges").equalsIgnoreCase("CHECKED")) {
                            monitor.printTableGaugeEntry(printwriter, s17);
                        }
                        continue;
                    }
                    if (ai[j1] == STATUS_COLUMN) {
                        monitor.printTableStatusEntry(printwriter, httprequest);
                        continue;
                    }
                    if (ai[j1] == ACKNOWLEDGE_COLUMN) {
                        boolean flag3 = monitor.isAcknowledged();
                        String s22 = monitor.getProperty(pAcknowledgeComment);
                        String s23 = monitor.getProperty(pAcknowledgeUser);
                        monitor.printTableAckEntry(printwriter, httprequest, flag3, s22, s23);
                        continue;
                    }
                    if (ai[j1] == NAME_COLUMN) {
                        monitor.printTableNameEntry(printwriter, httprequest, s6);
                        continue;
                    }
                    if (ai[j1] == SIMPLE_NAME_COLUMN) {
                        printwriter.println("<TD>" + s6 + "</TD>");
                        continue;
                    }
                    if (ai[j1] == MACHINE_COLUMN) {
                        printwriter.println("<TD>" + monitor.getHostname() + "</TD>");
                        continue;
                    }
                    if (ai[j1] == ALERT_COLUMN) {
                        if (TextUtils.getValue(hashmap, "_alertIconLink").equalsIgnoreCase("CHECKED")) {
                            Array array1 = getMonitorAlerts(httprequest, s8, monitor.getProperty(pID), array);
                            monitor.printTableAlertEntry(printwriter, httprequest, array1);
                        }
                        continue;
                    }
                    if (ai[j1] == REPORT_COLUMN) {
                        if (TextUtils.getValue(hashmap, "_reportIconLink").equalsIgnoreCase("CHECKED")) {
                            printwriter.println(monitor.printTableReportEntry(false, httprequest));
                        }
                        continue;
                    }
                    if (ai[j1] == MORE_COLUMN) {
                        if (httprequest.actionAllowed("_monitorTools")) {
                            monitor.printTableMoreEntry(printwriter, httprequest);
                        }
                        continue;
                    }
                    if (ai[j1] == UPDATED_COLUMN) {
                        monitor.printTableUpdatedEntry(printwriter, s14);
                        continue;
                    }
                    if (ai[j1] == REFRESH_COLUMN) {
                        if (httprequest.actionAllowed("_monitorRefresh")) {
                            monitor.printTableRefreshEntry(printwriter, httprequest);
                        }
                        continue;
                    }
                    if (ai[j1] == EDIT_COLUMN) {
                        if (httprequest.actionAllowed("_monitorEdit")) {
                            monitor.printTableEditEntry(printwriter, httprequest);
                        }
                        continue;
                    }
                    if (ai[j1] == DELETE_COLUMN) {
                        if (httprequest.actionAllowed("_monitorEdit")) {
                            monitor.printTableDeleteEntry(printwriter, httprequest);
                        }
                        continue;
                    }
                    if (ai[j1] == GROUP_COLUMN) {
                        printwriter.println("<TD><A HREF=/SiteView/" + httprequest.getAccountDirectory() + "/" + s12 + ">" + s10 + "</A></TD>\n");
                        continue;
                    }
                    if (ai[j1] == CUSTOM_COLUMN) {
                        String s21 = monitor.getProperty(s3);
                        if (s21.length() == 0) {
                            s21 = "&nbsp;";
                        }
                        if (s4.length() == 0) {
                            printwriter.print("<TD>" + s21 + "</TD>");
                        } else {
                            expandField(httprequest, printwriter, monitor, s4, s3, "<td>", "</td>");
                        }
                    } else {
                        printwriter.print("<TD>Unknown Column</TD>");
                    }
                }

                printwriter.println("</TR>");
                if (!enumeration.hasMoreElements()) {
                    break;
                }
                monitor = (Monitor) enumeration.nextElement();
            }
        }
        printwriter.println("</TABLE>");
        return s5;
    }

    /**
     * 
     * 
     * @param httprequest
     * @param printwriter
     * @param monitor
     * @param s
     * @param s1
     * @param s2
     * @param s3
     */
    private static void expandField(HTTPRequest httprequest, PrintWriter printwriter, Monitor monitor, String s, String s1, String s2, String s3) {
        boolean flag = s.indexOf("$table$") != -1;
        int i = 0;
        while (i < s.length()) {
            if (s.substring(i).indexOf("$") < 0) {
                break;
            }
            int j = s.substring(i).indexOf("$");
            if (j >= s.substring(i).length() - 1) {
                break;
            }
            int l = j + s.substring(i).substring(j + 1).indexOf("$") + 1;
            if (l < 0) {
                break;
            }
            String s5 = s.substring(i).substring(j + 1, l);
            String s6 = monitor.getProperty(s5);
            int i1 = 0;
            if (!s6.toLowerCase().equals("table")) {
                if (flag) {
                    if (s6.length() > 0) {
                        if (i + l + 1 >= s.length()) {
                            s = s.substring(0, i + j) + s6;
                        } else {
                            i1 = s6.length() - s5.length() - 2;
                            s = s.substring(0, i + j) + s6 + s.substring(i + l + 1);
                        }
                    }
                } else if (i + l + 1 >= s.length()) {
                    s = s.substring(0, i + j) + s6;
                } else {
                    i1 = s6.length() - s5.length() - 2;
                    s = s.substring(0, i + j) + s6 + s.substring(i + l + 1);
                }
            }
            i += l + i1 + 1;
        }

        int k = s.toLowerCase().indexOf("$table$");
        if (s1.equals("_monitorEditCustom") && k >= 0) {
            String s4 = "";
            s4 = s.substring(k + 7);
            printwriter.print("<TD>" + s.substring(0, k));
            HashMap hashmap = CGI.loadMasterConfig(httprequest);
            Enumeration enumeration = hashmap.values("_monitorEditCustom");
            while (enumeration.hasMoreElements()) {
                String s7 = (String) enumeration.nextElement();
                String as[] = TextUtils.split(s7, "|");
                String s8 = as[0];
                String s9 = as[1];
                String s10 = monitor.getProperty(s8);
                if (s10 != null && s10.length() > 0) {
                    printwriter.print("<TR><TD>" + s9 + "</TD><TD>" + s10 + "</TD></TR>");
                }
            }
            printwriter.print(s4 + "</TD>");
        } else {
            printwriter.print(s2 + s + s3);
        }
    }

    public static void printCategoryInsertHTML(String s, SiteViewObject siteviewobject, PrintWriter printwriter) {
        String s1 = siteviewobject.getSetting("_" + s + "InsertHTML");
        if (s1.length() > 0) {
            printwriter.println(s1);
            return;
        } else {
            return;
        }
    }

    static boolean isParentPresent(Array array, String s) {
        for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            if (CGI.isRelated(s, s1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * 
     * @param httprequest
     * @param enumeration
     * @return
     */
    public static Array getAllowedGroups(HTTPRequest httprequest, Enumeration enumeration) {
        Array array = new Array();
        Array array1 = CGI.getGroupFilterForAccount(httprequest);
        HashMap hashmap = new HashMap();
        if (!Platform.isStandardAccount(httprequest.getAccount()) && array1.contains(httprequest.getAccount())) {
            return array = null;
        }
        if (!Platform.isStandardAccount(httprequest.getAccount())) {
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (monitor.getProperty("_class").equals("SubGroup")) {
                    getSubgroup(monitor, hashmap);
                }
            }
            enumeration = hashmap.keys();
        }

        while (enumeration.hasMoreElements()) {
            Monitor monitor1 = (Monitor) enumeration.nextElement();
            String s = I18N.toDefaultEncoding(monitor1.getProperty("_group"));
            String s1 = (String) hashmap.get(monitor1);
            if (array1.size() != 0 ? CGI.allowedByGroupFilter(s, array1) && !isParentPresent(array1, s1) : s1.length() == 0) {
                array.add(monitor1);
            }
        }
        Sorting.sort(array, new GreaterEqualMonitorName());
        Reversing.reverse(array);
        return array;
    }

    /**
     * 
     * 
     * @param monitor
     * @param hashmap
     */
    public static void getSubgroup(Monitor monitor, HashMap hashmap) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s = I18N.toDefaultEncoding(monitor.getProperty("_group"));
        MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElementByID(s);
        try {
            String s1 = I18N.toDefaultEncoding(monitorgroup.getProperty("_parent"));
            hashmap.add(monitor, s1);
            Enumeration enumeration = monitorgroup.getMonitors();
            if (enumeration.hasMoreElements()) {
                while (enumeration.hasMoreElements()) {
                    Monitor monitor1 = (Monitor) enumeration.nextElement();
                    if (monitor1.getProperty("_class").equals("SubGroup")) {
                        getSubgroup(monitor1, hashmap);
                    }
                }
            } else {
                return;
            }
        } catch (NullPointerException nullpointerexception) {
            System.out.println("Subgroup " + s + " can not be retrieved");
            nullpointerexception.printStackTrace();
            return;
        }
    }

    /**
     * 
     * 
     * @param monitorgroup
     * @param array
     */
    public static void getChildren(MonitorGroup monitorgroup, Array array) {
        Enumeration enumeration = monitorgroup.getMonitors();
        if (enumeration.hasMoreElements()) {
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (monitor.getProperty("_class").equals("SubGroup")) {
                    getChildren((MonitorGroup) monitor, array);
                }
            }
        } else {
            return;
        }
    }

    public void getGroupDepends(Array array, Array array1, boolean flag) {
        String s = getProperty(AtomicMonitor.DEPENDS_ON);
        if (s != null && s.length() > 0) {
            array.add(s);
            array1.add(getProperty(AtomicMonitor.DEPENDS_CONDITION));
        }
        if (!flag) {
            return;
        }
        String s2 = I18N.toDefaultEncoding(getProperty("_parent"));
        if (s2 == null || s2.length() == 0) {
            MonitorGroup monitorgroup = (MonitorGroup) getOwner();
            if (monitorgroup != null) {
                String s1 = monitorgroup.getProperty(AtomicMonitor.DEPENDS_ON);
                if (s1 != null && s1.length() > 0) {
                    array.add(s1);
                    array1.add(getProperty(AtomicMonitor.DEPENDS_CONDITION));
                }
            }
            return;
        }
        MonitorGroup monitorgroup1 = SiteViewGroup.currentSiteView().getGroup(s2);
        if (monitorgroup1 == null) {
            return;
        } else {
            monitorgroup1.getGroupDepends(array, array1, true);
            return;
        }
    }

    /**
     * 
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        PrintWriter printwriter = FileUtils.MakeOutputWriter(System.out);
        PrintWriter printwriter1 = FileUtils.MakeOutputWriter(System.err);
        printwriter.println("\n-----------------------------------");
        printwriter.println("       Performing Time Tests");
        printwriter.println("-----------------------------------");
        SiteViewSupport.InitProcess();
        SiteViewSupport.InitProcess2();
        long l = Platform.timeMillis();
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        long l4 = 0L;
        Array array = new Array();
        String s = "";
        if (args[0].equals("-d")) {
            s = args[1];
            if (!s.endsWith("/") && !s.endsWith("\\")) {
                s = s + "/";
            }
            File file1 = new File(args[1]);
            String args1[] = file1.list();
            if (args1 == null) {
                printwriter.println("Groups Directory " + s + " does not exist");
                System.exit(2);
            }
            for (int j = 0; j < args1.length; j ++) {
                if (args1[j].endsWith(".mg")) {
                    array.add(s + args1[j]);
                }
            }

        } else {
            for (int i = 0; i < args.length; i ++) {
                array.add(args[i]);
            }

        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        File file2 = null;
        if (s.length() > 0) {
            file2 = new File(s + "master.config");
        }
        try {
            HashMap hashmap = null;
            if (file2 != null && file2.exists()) {
                Array array1 = FrameFile.readFromFile(file2.getAbsolutePath());
                if (array1.size() > 0) {
                    HashMap hashmap1 = (HashMap) array1.at(0);
                    hashmap = (HashMap) MasterConfig.getMasterConfig().clone();
                    String s4;
                    for (Enumeration enumeration1 = hashmap1.keys(); enumeration1.hasMoreElements(); hashmap.put(s4, hashmap1.get(s4))) {
                        s4 = (String) enumeration1.nextElement();
                    }

                    MasterConfig.saveMasterConfig(hashmap);
                }
            }
            hashmap = (HashMap) MasterConfig.getMasterConfig().clone();
            Machine.registerMachines(hashmap.values("_remoteMachine"));
            Enumeration enumeration = hashmap.values("_urlLocation");
            HTTPUtils.locations = new Array();
            if (enumeration.hasMoreElements()) {
                String s2;
                for (; enumeration.hasMoreElements(); HTTPUtils.locationMap.add(HTTPUtils.getLocationID(s2), s2)) {
                    s2 = (String) enumeration.nextElement();
                    HTTPUtils.locations.add(s2);
                }

            }
        } catch (Exception exception) {
            System.out.println("Exception getting master.config: " + exception);
            System.exit(0);
        }
        for (int k = 0; k < array.size(); k ++) {
            String s1 = (String) array.at(k);
            File file3 = new File(s1);
            String s3 = file3.getName();
            s3 = s3.substring(0, s3.lastIndexOf('.'));
            long l7 = Platform.timeMillis();
            MonitorGroup monitorgroup = loadGroup(s3, s1);
            siteviewgroup.addElement(monitorgroup);
            long l8 = Platform.timeMillis();
            long l9 = 0L;
            long l10 = 0L;
            long l11 = 0L;
            long l12 = 0L;
            long l13 = l8 - l7;
            printwriter.println("\n  " + s3);
            printwriter.println("    " + l13 + " ms for load");
            Enumeration enumeration2 = monitorgroup.getMonitors();
            boolean flag = true;
            long l14 = Platform.timeMillis();
            printwriter1.print("Running " + s3 + " group");
            while (flag && enumeration2.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration2.nextElement();
                if (monitor instanceof AtomicMonitor) {
                    printwriter1.print(".");
                    AtomicMonitor atomicmonitor = (AtomicMonitor) monitor;
                    AtomicMonitor _tmp = atomicmonitor;
                    int j1 = atomicmonitor.getPropertyAsInteger(AtomicMonitor.pFrequency);
                    if (j1 == 0) {
                        printwriter1.print("s");
                        Monitor _tmp1 = monitor;
                        printwriter.println("      monitor skipped: " + monitor.getProperty(Monitor.pName));
                    } else if (atomicmonitor.isDisabled()) {
                        l12 ++;
                        printwriter1.print("d");
                        Monitor _tmp2 = monitor;
                        printwriter.println("      monitor disabled: " + monitor.getProperty(Monitor.pName));
                    } else {
                        atomicmonitor.run();
                        long l17 = Platform.timeMillis();
                        long l18 = l17 - l14;
                        printwriter.println("      " + l18 + " ms, " + atomicmonitor.getProperty("_name") + " " + atomicmonitor.getProperty("_description"));
                        l14 = l17;
                        l10 ++;
                        long l19 = Platform.timeMillis();
                        String s5 = atomicmonitor.matchExpects();
                        if (s5.length() > 0) {
                            printwriter.println("********** TEST FAILED *********");
                            printwriter.println(s5);
                            l11 ++;
                            HashMap hashmap2 = monitor.getValuesTable();
                            Object obj;
                            for (Enumeration enumeration3 = hashmap2.keys(); enumeration3.hasMoreElements(); printwriter.println(obj + "=" + hashmap2.get(obj))) {
                                obj = enumeration3.nextElement();
                            }

                            printwriter.println("*********************************");
                            printwriter1.print("F");
                        } else {
                            printwriter1.print("p");
                        }
                        printwriter1.flush();
                        l9 += Platform.timeMillis() - l19;
                    }
                }
            }
            printwriter1.println("DONE");
            long l15 = Platform.timeMillis();
            long l16 = l15 - l8 - l9;
            printwriter.println("    " + l16 + " ms total for update");
            l1 += l9;
            l2 += l10;
            l3 += l11;
            l4 += l12;
        }

        long l5 = Platform.timeMillis();
        long l6 = l5 - l - l1;
        printwriter.println("\n  " + l6 + " ms total test time");
        printwriter.println("-----------------------------------");
        printwriter.print("TEST SUMMARY: " + l3 + " FAILED, " + (l2 - l3) + " PASSED, " + l4 + " DISABLED");
        int i1 = 0;
        if (l3 > 0L) {
            i1 = 1;
        }
        printwriter.flush();
        printwriter1.flush();
        System.exit(i1);
    }

	public String getTopazParent() {
        return getProperty(pParent);
    }

    
	protected void startGroupScheduler(boolean flag) {
        startScheduler();
        if (flag) {
            Vector vector = new Vector();
            Vector vector1 = new Vector();
            vector1.add(this);
            ConfigurationChanger.getGroupsMonitors(vector1, null, vector, true);
            for (int i = 0; i < vector.size(); i ++) {
                ((MonitorGroup) vector.get(i)).startScheduler();
            }

        }
    }

    private void startScheduler() {
        long l = getPropertyAsLong(pFrequency);
        if (l > 0L && l >= (long) minGroupInterval) {
            l *= 1000L;
            if (updateGroup == null) {
                updateGroup = new UpdateGroup(this);
                long l1 = getPropertyAsLong(pLastUpdate);
                long l2 = Platform.timeMillis();
                long l3 = (l1 + l) - l2;
                if (l1 > l2) {
                    l1 = l2;
                } else if (l1 <= 0L) {
                    SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                    long l5 = getSettingAsLong("_monitorDelayBetweenRefresh", 1000) * siteviewgroup.monitorsStarted;
                    siteviewgroup.monitorsStarted ++;
                    l1 = (l2 - l) + l5;
                } else if (l3 < 5000L) {
                    long l4 = (long) (2D + Math.random() * (double) getSettingAsLong("_initialMonitorDelay", 60));
                    l1 = (l2 - l) + l4 * 1000L;
                }
                SiteViewGroup.currentSiteView().monitorScheduler.scheduleRepeatedPeriodicAction(updateGroup, l, l1);
            }
        }
    }

    public boolean groupSchedulerActive(boolean flag) {
        MonitorGroup monitorgroup = this;
        for (MonitorGroup monitorgroup1 = null; monitorgroup != null && monitorgroup != monitorgroup1 && monitorgroup != SiteViewGroup.currentSiteView(); monitorgroup = (MonitorGroup) getParent()) {
            long l = monitorgroup.getPropertyAsLong(pFrequency);
            if (l > 0L && l >= (long) minGroupInterval) {
                return true;
            }
            if (!flag) {
                return false;
            }
            monitorgroup1 = monitorgroup;
        }

        return false;
    }

    public boolean isHealthGroup() {
        String s = getProperty(pHealth);
        return s.length() != 0;
    }

    public boolean isTopLevelGroup() {
        SiteViewObject siteviewobject = getParent();
        return siteviewobject == null || siteviewobject.equals(SiteViewGroup.currentSiteView());
    }

    static {
        minGroupInterval = 60;
        pLastSaved = new NumericProperty("lastSaved", "0");
        pParent = new StringProperty("_parent");
        pHealth = new StringProperty("_health");
        pAccountName = new StringProperty("_accountName");
        pMaxErrorCount = new NumericProperty("maxErrorCount", "0");
        pMonitorsInError = new NumericProperty("monitorsInError", "0");
        pMonitorsInGroup = new NumericProperty("monitorsInGroup", "0");
        pGroupErrorDisplay = new NumericProperty("groupErrorDisplay", "0");
        pFrequency = new FrequencyProperty("_frequency", "");
        pFrequency.setLabel("Refresh Group every");
        pFrequency.setDescription("Refresh all the monitors in this group according to this schedule. This frequency should not be less than " + minGroupInterval + " seconds." + " When defining a group schedule, the monitors of this group will not "
                + "run by their own schedule.");
        StringProperty astringproperty[] = { pLastSaved, pMaxErrorCount, pMonitorsInError, pMonitorsInGroup, pGroupErrorDisplay, pParent, pHealth, pAccountName, pFrequency };
        addProperties("COM.dragonflow.SiteView.MonitorGroup", astringproperty);
        columnNum = 0;
        CATEGORY_COLUMN = columnNum ++;
        ACKNOWLEDGE_COLUMN = columnNum ++;
        GAUGE_COLUMN = columnNum ++;
        STATUS_COLUMN = columnNum ++;
        NAME_COLUMN = columnNum ++;
        MORE_COLUMN = columnNum ++;
        UPDATED_COLUMN = columnNum ++;
        GROUP_COLUMN = columnNum ++;
        REFRESH_COLUMN = columnNum ++;
        EDIT_COLUMN = columnNum ++;
        DELETE_COLUMN = columnNum ++;
        ALERT_COLUMN = columnNum ++;
        REPORT_COLUMN = columnNum ++;
        SIMPLE_NAME_COLUMN = columnNum ++;
        MACHINE_COLUMN = columnNum ++;
        CUSTOM_COLUMN = columnNum ++;
    }

	public static MonitorGroup loadGroup(String s, ResultSet rs, boolean flag) {
		MonitorGroup monitorgroup = null;
		String groupid;
        try {
            monitorgroup = new MonitorGroup();
            monitorgroup.setProperty("_id", s);
            monitorgroup.setProperty("_name", s);
            groupid=rs.getString("RecId");
            String s1="_encoding=GBK,_dependsCondition="+
            rs.getString("DependsCondition")+",_fileEncoding=UTF-8,_name="+s;
            if(rs.getString("DependsOn")!=null&&!rs.getString("DependsOn").equals("")){
            	s1=s1+",_dependsOn="+rs.getString("DependsOn");
            }
            if(rs.getString("Description")!=null&&!rs.getString("Description").equals("")){
            	s1=s1+",_description="+rs.getString("Description");
            }
            if(rs.getInt("RefreshGroup")!=0){
            	int i=rs.getInt("RefreshGroup");
            	if(rs.getString("RefreshGroupUtil").equals("Minute")){
            		i=i*60;
            	}else if(rs.getString("RefreshGroupUtil").equals("Hour")){
            		i=i*3600;
            	}else if(rs.getString("RefreshGroupUtil").equals("Day")){
            		i=i*86400;
            	}
            	s1=s1+",_frequency="+i;           	
            }
            s1=s1+",#,";
            monitorgroup.file = new File(s);
            monitorgroup.readMonitors(groupid,s,s1);
            monitorgroup.readDynamic();
            monitorgroup.initialize(monitorgroup.getValuesTable());
        } catch (FileNotFoundException filenotfoundexception) {
            monitorgroup = null;
            if (flag) {
 //               LogManager.log("Error", "Error loading group, not found: " + groupid);
                filenotfoundexception.printStackTrace();
            }
        } catch (Exception exception) {
            monitorgroup = null;
            if (flag) {
            //    LogManager.log("Error", "Error loading group, file: " + groupid + ", error: " + exception);
                exception.printStackTrace();
            }
        }
        return monitorgroup;
	}
	
	  void readMonitors(String s, String s1, String s12) throws IOException {
	  //      Array array = FrameFile.readFromFile("C:\\Documents and Settings\\Administrator.DRAGONFL-FC1FAA\\git\\siteview9.2\\com.siteview.kernel.core\\groups\\"+s1+".mg");
		  Array array = FrameFile.readDataBase(s12,s);
	        String s2 = "";
	        if (Platform.isPortal()) {
	            s2 = PortalSync.getPortalServerFromPath(s);
	        }
	        Enumeration enumeration = array.elements();
	        if (enumeration.hasMoreElements()) {
	            HashMap hashmap = (HashMap) enumeration.nextElement();
	            hashmap.put("_id", s);
	            String s3 = (String) hashmap.get("_name");
	            if (s3 == null || s3.equals("config") || s3.length() == 0) {
	                hashmap.put("_name", s1);
	            }
	            setValuesTable(hashmap);
	        }
	        while (enumeration.hasMoreElements()) {
	            try {
	                HashMap hashmap1 = (HashMap) enumeration.nextElement();
	                if (isMonitorFrame(hashmap1)) {
	                    Monitor monitor = null;
	                    monitor = createMonitor(hashmap1, s2);
	                    monitor.group = this;
	                    addElement(monitor);
	                } else if (isReportFrame(hashmap1)) {
	                    HistoryReport historyreport = HistoryReport.createHistoryReportObject(hashmap1);
	                    addElement(historyreport);
	                }
	            } catch (ClassNotFoundException classnotfoundexception) {
	                LogManager.log("Error", "Error loading monitor, not found: " + classnotfoundexception.getMessage());
	            } catch (Exception exception) {
	                LogManager.log("Error", "Error loading monitor, " + exception);
	                System.err.println("Error loading monitor, " + exception);
	                exception.printStackTrace();
	            }
	        }
	    }
}
