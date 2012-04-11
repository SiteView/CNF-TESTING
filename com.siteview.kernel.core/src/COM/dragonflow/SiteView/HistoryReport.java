/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * HistoryReport.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>HistoryReport</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.awt.Color;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import jgl.Array;
import jgl.ArrayIterator;
import jgl.HashMap;
import jgl.Reversing;
import jgl.Sorting;
import COM.dragonflow.Chart.BarChart;
import COM.dragonflow.Chart.DrawerGD;
import COM.dragonflow.Chart.LineChart;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.reportPage;
import COM.dragonflow.Page.treeControl;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.Resource.SiteViewResource;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.Utils.Base64Encoder;
import COM.dragonflow.Utils.DebugWatcher;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.TextUtils;
import COM.sun.jimi.core.ImageAccessException;
import COM.sun.jimi.core.Jimi;
import COM.sun.jimi.core.raster.JimiRasterImage;

import com.netaphor.smtp.Attachment;
import com.netaphor.smtp.Client;
import com.netaphor.smtp.SMTPException;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, MonitorGroup, GenerateHistoryReport, SampleCollector,
// Monitor, RealTimeReportingMonitor, RealTimeMonitorReader, SiteViewLogReader,
// SubGroup, GreaterDate, HistorySummaryCollector, Rule,
// SiteViewGroup, Platform, Scheduler, AtomicMonitor,
// MasterConfig, AlertReport, TopazAPI, Action

public class HistoryReport extends SiteViewObject {

    public static final int MONTH_DAYS[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static final int MINUTE_SECONDS = 60;

    public static final int HOUR_SECONDS = 3600;

    public static final int DAY_SECONDS = 0x15180;

    public static final int WEEK_SECONDS = 0x93a80;

    public static final int MONTH_SECONDS = 0x278d00;

    public static final int DEFAULT_SAMPLE_COUNT = 48;

    static final int LEGEND_WIDTH = 110;

    static final int GRAPH_WIDTH = 360;

    static final int GRAPH_HEIGHT = 200;

    static final int TICK_COUNT = 4;

    static final String GRAPHIC_DIRECTORY = "/SiteView/htdocs/artwork/";

    static final String GRAPH_LINK_PREFIX = "Graph";

    static final String TABLE_LINK_PREFIX = "Table";

    static final String HTML_SUFFIX = ".html";

    static final String TEXT_SUFFIX = ".txt";

    static final String XML_SUFFIX = ".xml";

    static final String EMAIL_SUFFIX = ".eml";

    static final String PEAK_GRAPHIC = "/SiteView/htdocs/artwork/gray.gif";

    static final String EMPTY_GRAPHIC = "/SiteView/htdocs/artwork/empty.gif";

    static final String TICK_GRAPHIC = "/SiteView/htdocs/artwork/gray.gif";

    static final String NOTICK_GRAPHIC = "/SiteView/htdocs/artwork/empty.gif";

    public static StringProperty pTargets;

    public static StringProperty pWindow;

    public static StringProperty pPrecision;

    public static StringProperty pFormat;

    public static StringProperty pVMax;

    public static StringProperty pStartDay;

    public static StringProperty pStartHour;

    public static StringProperty pDirection;

    public static StringProperty pEmail;

    public static StringProperty pAttachReport;

    public static StringProperty pEmailData;

    public static StringProperty pSpreadsheet;

    public static StringProperty pXML;

    public static StringProperty pXMLEmailData;

    public static StringProperty pSpreadsheetDump;

    public static StringProperty pReportType;

    public static StringProperty pCluster;

    public static StringProperty pDisabled;

    public static StringProperty pBasicPropertiesOnly;

    public static StringProperty pQueryID;

    public static StringProperty pQuery;

    public static StringProperty pSchedule;

    public static StringProperty pTimeOffset;

    public static StringProperty pShowAlertsOnly;

    public static StringProperty pDescription;

    public static StringProperty pStatusFilter;

    public static StringProperty pScheduleFilter;

    public static StringProperty pBestCaseCalc;

    static StringProperty pRealTime;

    static StringProperty pRealTimeRefresh;

    public static StringProperty pUsingDefaultTime;

    public static StringProperty pStartTime;

    public static StringProperty pEndTime;

    public static StringProperty pMultipleMonitors;

    public static StringProperty pHistoryTarget;

    public static StringProperty pTitle;

    public static StringProperty pDefaultTitle;

    public static StringProperty pReportPeriod;

    public static StringProperty pThresholdSummaryText;

    public static StringProperty pSummaryText;

    public static StringProperty pErrorTimeSummaryText;

    public static StringProperty pBasicAlertSummaryText;

    public static StringProperty pDetailAlertSummaryText;

    public static StringProperty pVirtualPath;

    public static StringProperty pTextReportVirtualPath;

    public static StringProperty pIndexVirtualPath;

    public static StringProperty pXMLReportVirtualPath;

    public static StringProperty pReadOnlyVirtualPath;

    public static StringProperty pReadOnlyTextReportVirtualPath;

    public static StringProperty pReadOnlyIndexVirtualPath;

    public static StringProperty pReadOnlyXMLReportVirtualPath;

    public static StringProperty pFilePath;

    public static StringProperty pReportDirectory;

    public static StringProperty pReportVirtual;

    public static StringProperty pAccount;

    public static StringProperty pUsingDefaultTimeParameters;

    public static StringProperty pSiteSeerRegistration;

    public static StringProperty pMailTemplate;

    public static StringProperty pIsAdhoc;

    public static StringProperty pWarningNotIncluded;

    public static StringProperty pUpTimeIncludeWarning;

    public static StringProperty pFailureNotIncluded;

    static HashMap graphicMap;

    static HashMap altMap;

    static String alternateLogFile = "";

    private static String logoFile = "logo.gif";

    static boolean debug = false;

    Array collectors;

    Date reportDate;

    Thread thread;

    Action action;

    PrintWriter liveStream;

    String backHTML;

    String logInAccount;

    boolean autoScale;

    HTTPRequest request;

    Array errorList;

    Array warningList;

    Array goodList;

    static boolean logoLoaded = false;

    static JimiRasterImage logoImage = null;

    static int logoWidth;

    static int logoHeight;

    public String reportTableHTML;

    public String reportTableHeaderHTML;

    public String reportTableDataHTML;

    public String disabledColor;

    public String warningColor;

    public String errorColor;

    public String goodColor;

    public String nodataColor;

    public Color disabledRGB;

    public Color warningRGB;

    public Color errorRGB;

    public Color goodRGB;

    public Color nodataRGB;

    public Color backgroundRGB;

    public Color maxRGB;

    static final boolean $assertionsDisabled; /* synthetic field */

    public HistoryReport() {
        collectors = null;
        reportDate = null;
        thread = null;
        action = null;
        liveStream = null;
        backHTML = null;
        logInAccount = "administrator";
        autoScale = false;
        request = null;
        errorList = null;
        warningList = null;
        goodList = null;
        reportTableHTML = "";
        reportTableHeaderHTML = "";
        reportTableDataHTML = "";
        disabledColor = "";
        warningColor = "";
        errorColor = "";
        goodColor = "";
        nodataColor = "";
    }

    public static void generateReportFromQueryID(String s, PrintWriter printwriter, String s1, HashMap hashmap) throws SiteViewException {
        returnGenerateReportFromQueryID(s, printwriter, s1, hashmap);
    }

    public static HistoryReport returnGenerateReportFromQueryID(String s, PrintWriter printwriter, String s1) throws SiteViewException {
        return returnGenerateReportFromQueryID(s, printwriter, s1, null);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param printwriter
     * @param s1
     * @param hashmap
     * @return
     * @throws SiteViewException
     */
    public static HistoryReport returnGenerateReportFromQueryID(String s, PrintWriter printwriter, String s1, HashMap hashmap) throws SiteViewException {
        HistoryReport historyreport = null;
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Object obj = siteviewgroup;
        if (!Platform.isStandardAccount(s1)) {
            obj = (MonitorGroup) siteviewgroup.getElement(s1);
        }
        Enumeration enumeration = ((MonitorGroup) (obj)).getReports();
        if (!enumeration.hasMoreElements() && obj == siteviewgroup) {
            siteviewgroup.loadHistoryNoSchedule();
            enumeration = ((MonitorGroup) (obj)).getReports();
        }
        while (enumeration.hasMoreElements()) {
            HistoryReport historyreport1 = (HistoryReport) enumeration.nextElement();
            if (!s.equals(historyreport1.getProperty(pQueryID))) {
                continue;
            }
            historyreport = historyreport1;
            if (hashmap != null) {
                String s2;
                Enumeration enumeration1 = hashmap.keys();
                while (enumeration1.hasMoreElements()) {
                    s2 = (String) enumeration1.nextElement();
                    historyreport1.setProperty(s2, (String) hashmap.get(s2));
                }

            }
            historyreport.liveStream = printwriter;
            historyreport.createFromQuery(s1);
            historyreport.liveStream = null;
            break;
        }

        if (historyreport == null) {
            String as[] = { s, s1 };
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_REPORT_NO_DATA, as);
        } else {
            return historyreport;
        }
    }

    public static void generateReportFromRequest(HTTPRequest httprequest, PrintWriter printwriter, String s) throws SiteViewException {
        returnReportFromRequest(httprequest, printwriter, s);
    }

    public static HistoryReport returnReportFromRequest(HTTPRequest httprequest, PrintWriter printwriter, String s) throws SiteViewException {
        HistoryReport historyreport = new HistoryReport();
        historyreport.liveStream = printwriter;
        historyreport.backHTML = s;
        historyreport.request = httprequest;
        if (httprequest.getValue("id") != null && httprequest.getValue("id").length() > 0) {
            historyreport.setProperty(pQueryID, httprequest.getValue("id"));
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Object obj = siteviewgroup;
        if (!httprequest.isStandardAccount()) {
            obj = (MonitorGroup) siteviewgroup.getElement(httprequest.getAccount());
        }
        historyreport.setOwner(((SiteViewObject) (obj)));
        String s1;
        Enumeration enumeration = httprequest.getValues("groups");
        while (enumeration.hasMoreElements()) {
            s1 = (String) enumeration.nextElement();
            historyreport.addProperty(pTargets, s1);
        }

        String s2;
        Enumeration enumeration1 = httprequest.getValues("monitors");
        while (enumeration1.hasMoreElements()) {
            s2 = null;
            if (treeControl.useTree()) {
                s2 = HTTPRequest.decodeString((String) enumeration1.nextElement());
            } else {
                s2 = (String) enumeration1.nextElement();
            }
            historyreport.addProperty(pTargets, s2);
        }

        historyreport.setProperty(pStartHour, httprequest.getValue(pStartHour.getName()));
        historyreport.setProperty(pStartDay, httprequest.getValue(pStartDay.getName()));
        historyreport.setProperty(pPrecision, httprequest.getValue(pPrecision.getName()));
        historyreport.setProperty(pFormat, httprequest.getValue(pFormat.getName()));
        historyreport.setProperty(pVMax, httprequest.getValue(pVMax.getName()));
        historyreport.setProperty(pWindow, httprequest.getValue(pWindow.getName()));
        historyreport.setProperty(pDirection, httprequest.getValue(pDirection.getName()));
        historyreport.setProperty(pSpreadsheetDump, httprequest.getValue(pSpreadsheetDump.getName()));
        setReportType(historyreport, httprequest);
        historyreport.setProperty(pAccount, httprequest.getAccount());
        historyreport.setProperty(pDefaultTitle, "Summary");
        historyreport.setProperty(pTitle, httprequest.getValue(pTitle.getName()));
        historyreport.setProperty(pDescription, httprequest.getValue(pDescription.getName()));
        historyreport.setProperty(pQuery, httprequest.getValue(pQuery.getName()));
        historyreport.setProperty(pIsAdhoc, httprequest.getValue(pIsAdhoc.getName()));
        historyreport.setProperty(pStatusFilter, httprequest.getValue(pStatusFilter.getName()));
        historyreport.setProperty(pScheduleFilter, httprequest.getValue(pScheduleFilter.getName()));
        historyreport.setProperty(pRealTime, httprequest.getValue(pRealTime.getName()));
        historyreport.setProperty(pRealTimeRefresh, httprequest.getValue(pRealTimeRefresh.getName()));
        if (httprequest.getValue(pEmail.getName()).length() > 0) {
            historyreport.setProperty(pEmail, httprequest.getValue(pEmail.getName()));
            historyreport.setProperty(pAttachReport, "true");
        }
        if (httprequest.getValue("detailed").length() == 0) {
            historyreport.setProperty(pBasicPropertiesOnly, "checked");
        }
        if (httprequest.getValue("bestCaseCalc").length() > 0) {
            historyreport.setProperty(pBestCaseCalc, "checked");
        }
        boolean flag = httprequest.getValue("showThresholdSummary").length() > 0;
        boolean flag1 = httprequest.getValue("showSummaryTable").length() > 0;
        boolean flag2 = httprequest.getValue("showErrorTimeSummary").length() > 0;
        boolean flag3 = httprequest.getValue("showGraphs").length() > 0;
        boolean flag4 = httprequest.getValue("showGraphs").length() > 0;
        boolean flag5 = httprequest.getValue("showTables").length() > 0;
        boolean flag6 = httprequest.getValue("showErrors").length() > 0;
        boolean flag7 = httprequest.getValue("showWarnings").length() > 0;
        boolean flag8 = httprequest.getValue("showGoods").length() > 0;
        boolean flag9 = httprequest.getValue("showAlerts").length() > 0;
        boolean flag10 = httprequest.getValue("warningNotIncluded").length() > 0;
        boolean flag11 = httprequest.getValue("upTimeIncludeWarning").length() > 0;
        boolean flag12 = httprequest.getValue("failureNotIncluded").length() > 0;
        boolean flag13 = flag || flag1 || flag2 || flag3 || flag4 || flag5 || flag6 || flag8 || flag7 || flag9;
        if (flag13) {
            if (flag) {
                historyreport.setProperty("_showReportThresholdSummary", "checked");
            }
            if (!flag1) {
                historyreport.setProperty("_hideReportSummary", "checked");
            }
            if (flag2) {
                historyreport.setProperty("_showReportErrorTimeSummary", "checked");
            }
            if (!flag4) {
                historyreport.setProperty("_hideReportGraphs", "checked");
            }
            if (!flag3) {
                historyreport.setProperty("_hideReportCharts", "checked");
            }
            if (!flag5) {
                historyreport.setProperty("_hideReportTables", "checked");
            }
            if (!flag6) {
                historyreport.setProperty("_hideReportErrors", "checked");
            }
            if (!flag8) {
                historyreport.setProperty("_hideReportGoods", "checked");
            }
            if (!flag7) {
                historyreport.setProperty("_hideReportWarnings", "checked");
            }
            if (flag9) {
                historyreport.setProperty("_showReportAlerts", httprequest.getValue("alertDetailLevel"));
            }
            if (flag10) {
                historyreport.setProperty(pWarningNotIncluded, "checked");
            }
            if (flag11) {
                historyreport.setProperty(pUpTimeIncludeWarning, "checked");
            }
            if (flag12) {
                historyreport.setProperty(pFailureNotIncluded, "checked");
            }
        }
        historyreport.createFromQuery("");
        return historyreport;
    }

    private static void setReportType(HistoryReport historyreport, HTTPRequest httprequest) {
        String s = httprequest.getValue("reportFrmType");
        if (s.length() > 0 && !s.equals("html")) {
            historyreport.setProperty(pReportType, s);
        } else {
            historyreport.setProperty(pReportType, httprequest.getValue(pReportType.getName()));
        }
    }

    public static HistoryReport createHistoryReportObject(HashMap hashmap) {
        HistoryReport historyreport = new HistoryReport();
        historyreport.setOwner(SiteViewGroup.currentSiteView());
        historyreport.readFromHashMap(hashmap);
        historyreport.initialize(hashmap);
        return historyreport;
    }

    public boolean isDisabled() {
        boolean flag = false;
        if (getOwner() != null && (getOwner() instanceof MonitorGroup)) {
            flag = ((MonitorGroup) getOwner()).groupDisabled();
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        boolean flag1 = siteviewgroup.isGloballyDisabled().length() > 0;
        return flag || flag1 || getProperty(pDisabled).length() > 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    void readFromHashMap(HashMap hashmap) {
        Enumeration enumeration = hashmap.values("groups");
        while (enumeration.hasMoreElements()) {
            hashmap.add("monitors", enumeration.nextElement());
        }
        super.readFromHashMap(hashmap);
    }

    public void schedule() {
        if (action == null) {
            if (getOwner() != SiteViewGroup.currentSiteView() && getProperty(pAccount).length() == 0) {
                setProperty(pAccount, getOwner().getProperty(pID));
            }
            File file = new File(Platform.getDirectoryPath("htdocs", getProperty(pAccount)) + File.separator + "Reports-" + getProperty(pQueryID));
            Date date = null;
            String s = "Report-";
            Date date1 = Platform.makeDate();
            if (file.exists()) {
                String as[] = file.list();
                for (int i = 0; i < as.length; i ++) {
                    if (!as[i].startsWith(s) || !as[i].endsWith(".html")) {
                        continue;
                    }
                    Date date2 = TextUtils.fileNameToDate(as[i].substring(s.length()));
                    if ((date == null || date.before(date2)) && date2.before(date1)) {
                        date = date2;
                    }
                }

            }
            long l = 0L;
            if (date != null) {
                l = date.getTime();
            }
            action = new GenerateHistoryReport(this);
            SiteViewGroup.currentSiteView().reportScheduler.scheduleRepeatedAction(action, getProperty(pSchedule), l);
        }
    }

    public void unschedule() {
        SiteViewGroup.currentSiteView().reportScheduler.unschedule(action);
        action = null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @throws SiteViewException
     */
    public void initialize() throws SiteViewException {
        if (getOwner() != SiteViewGroup.currentSiteView() && getProperty(pAccount).length() == 0) {
            setProperty(pAccount, getOwner().getProperty(pID));
        }
        if (getProperty(pAccount).length() == 0) {
            setProperty(pAccount, "administrator");
        }
        collectors = new Array();
        reportDate = Platform.makeDate();
        Array array = getProperties();
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (!stringproperty.isParameter) {
                unsetProperty(stringproperty);
            }
        }

        if (getPropertyAsInteger(pTimeOffset) == 0) {
            setProperty(pTimeOffset, getSetting("_timeOffset"));
        }
        long l = getPropertyAsLong(pPrecision);
        initializeSampleCollectors();
        initializeTimeParameters();
        initializeFilePaths();
        Enumeration enumeration1 = collectors.elements();
        long l1 = getPropertyAsLong(pWindow);
        long l2 = getSettingAsLong("_chartWidth", 600) - 75;
        while (enumeration1.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration1.nextElement();
            int i = samplecollector.getMonitor().getPropertyAsInteger(AtomicMonitor.pFrequency);
            if (i > 0 && (l <= 0L || l > (long) i)) {
                long l3 = l1 / (long) i;
                if (l3 > l2 / 3L) {
                    samplecollector.clearSampleBuffer();
                }
            }
        }

        if (getSetting("_reportAutoScale").length() > 0) {
            autoScale = true;
        } else {
            autoScale = false;
        }
        if (getProperty(pTitle).length() == 0) {
            String s = getProperty(pDefaultTitle);
            setProperty(pTitle, getProperty(pDefaultTitle) + " for " + getProperty(pHistoryTarget));
            if (getProperty(pShowAlertsOnly).length() == 0) {
                setProperty(pTitle, s + " for " + getProperty(pHistoryTarget));
            } else {
                setProperty(pTitle, s + " of Alerts");
            }
        }
    }

    private RealTimeReportingMonitor getRealTimeReportingMonitor() {
        if (getProperty(pRealTime).length() == 0) {
            return null;
        }
        String as[] = getProperty(pTargets).split(" ");
        if (as.length != 2) {
            return null;
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s = I18N.toDefaultEncoding(as[1]);
        MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(s);
        if (monitorgroup != null) {
            String s1 = s + SiteViewGroup.ID_SEPARATOR + as[0];
            Monitor monitor = (Monitor) siteviewgroup.getElement(s1);
            if (monitor != null && (monitor instanceof RealTimeReportingMonitor)) {
                return (RealTimeReportingMonitor) monitor;
            }
        }
        return null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @throws SiteViewException
     */
    public void createFromQuery(String s) throws SiteViewException {

        initialize();
        String s1 = getProperty(pFilePath) + ".html";
        synchronized (FileUtils.getFileLock(s1)) {
            File file = new File(s1);
            if (getProperty(pRealTime).length() > 0 && file.exists()) {
                RealTimeReportingMonitor realtimereportingmonitor = getRealTimeReportingMonitor();
                if (realtimereportingmonitor == null) {
                    String as[] = { getProperty(pTargets) };
                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_REPORT_REALTIME_NO_MONITORS, as);
                }
                long l1 = file.lastModified();
                long l = realtimereportingmonitor.lastRunTime();
                if (l1 > l) {
                    if (liveStream == null) {
                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_REPORT_REALTIME_NO_OUTPUT);
                    }
                    printReportTop(s, liveStream);
                    printReportBottom(getProperty(pVirtualPath), liveStream);

                    return;
                }
            }

            FileOutputStream fileoutputstream = null;
            PrintWriter printwriter = null;
            try {
                fileoutputstream = new FileOutputStream(file);
                printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
                if (liveStream != null) {
                    printReportTop(s, liveStream);
                }
                createReport(printwriter);
                if (liveStream != null) {
                    printReportBottom(getProperty(pVirtualPath), liveStream);
                }
            } catch (IOException ioexception) {
                String as1[] = { file.getAbsolutePath() };
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_REPORT_CANNOT_WRITE, as1);
            } finally {
                if (printwriter != null) {
                    printwriter.close();
                }
                if (fileoutputstream != null) {
                    try {
                        fileoutputstream.close();
                    } catch (IOException ioexception1) {
                        LogManager.log("error", "Cannot close file: " + file.getAbsolutePath());
                    }
                }
            }
            LogManager.log("RunMonitor", "Created report in file: " + file.getAbsolutePath());
        }

        if (getProperty(pIsAdhoc).length() <= 0) {
            generateIndexPage(this, getProperty(pAccount), getProperty(pQueryID), false, "true");
            if (Platform.isUserAccessAllowed()) {
                generateIndexPage(this, "user", getProperty(pQueryID), false, "");
            }
        }
        return;
    }

    private void printReportTop(String s, PrintWriter printwriter) {
        String s1 = getProperty(pVirtualPath);
        if (s.length() > 0 && !s.equals("administrator")) {
            s1 = TextUtils.replaceString(s1, "/accounts/administrator/", "/accounts/" + s + "/");
            s1 = TextUtils.replaceString(s1, "/SiteView/htdocs/", "/SiteView/accounts/" + s + "/htdocs/");
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.createFromQuery(): liveURL=" + s1, null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.createFromQuery(): liveAccount=" + s, null);
        }
        printwriter.println("<HTML>");
        CGI.printHeadTag(printwriter, "Generating report...", "");
        printwriter.println("<BODY BGCOLOR=\"#ffffff\" ONLOAD=window.location.replace(\"" + s1 + "\")>");
        printwriter.flush();
    }

    private void printReportBottom(String s, PrintWriter printwriter) {
        printwriter.println("<P>Graph complete. If your browser does not refresh <A HREF=" + s + ">click here to see graph</A>");
        printwriter.println("</BODY></HTML>");
        printwriter.flush();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private HashMap getCustomFormat(String s) {
        if (s.length() > 0) {
            Enumeration enumeration = getMultipleSettings("_reportFormat");
            HashMap hashmap;
            String s3;
            while (enumeration.hasMoreElements()) {
                String s1 = (String) enumeration.nextElement();
                hashmap = TextUtils.stringToHashMap(s1);
                Enumeration enumeration1 = hashmap.keys();
                while (enumeration1.hasMoreElements()) {
                    String s2 = (String) enumeration1.nextElement();
                    String s4 = (String) hashmap.get(s2);
                    if (s4 != null && s4.indexOf('_') != -1) {
                        hashmap.put(s2, s4.replace('_', ' '));
                    }
                }
                s3 = TextUtils.getValue(hashmap, "_id");
                if (s3.equals(s)) {
                    return hashmap;
                }
            }
        }
        return new HashMap();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @throws SiteViewException
     */
    private void createReport(PrintWriter printwriter) throws SiteViewException {
        if (collectors.size() == 0 && getProperty(pShowAlertsOnly).length() == 0) {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_REPORT_NO_MONITORS);
        }
        printwriter.print("<HTML>");
        CGI.printBodyHeader(printwriter, getProperty(pTitle), "");
        String s = "";
        if (getPropertyAsInteger(pRealTimeRefresh) > 0) {
            s = "<meta HTTP-EQUIV=\"Refresh\" CONTENT=\"" + getPropertyAsInteger(pRealTimeRefresh) + "; URL=" + request.getRawURL() + "\">\r";
        }
        CGI.printBodyHeader(printwriter, getProperty(pTitle), s);
        if (liveStream != null) {
            liveStream.print("<H3>Generating " + getProperty(pTitle) + "...</H3>\n");
            liveStream.flush();
        }
        printwriter.println(Platform.licenseHeader(MasterConfig.getMasterConfig(), true, logInAccount));
        File file = new File(Platform.getDirectoryPath("logs", logInAccount) + File.separator + "SiteView.log");
        if (alternateLogFile.length() > 0) {
            file = new File(alternateLogFile);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.createReport(): USING LOG FILE FOR REPORT: " + file, null);
        }
        if (liveStream != null) {
            liveStream.print("<P>\n");
        }
        Date date = new Date(getPropertyAsLong(pStartTime) * 1000L);
        Date date1 = new Date(getPropertyAsLong(pEndTime) * 1000L);
        long l = getSettingAsLong("_maxReportErrors", 100);
        long l1 = getSettingAsLong("_maxReportWarnings", 100);
        long l2 = getSettingAsLong("_maxReportGoods", 100);
        if (debug) {
            TextUtils.debugPrint("*** HistoryReport.createReport(): now: " + TextUtils.prettyDate(Platform.makeDate()) + " start: " + date + " end: " + date1, null);
        }
        if (errorList == null) {
            errorList = new Array();
        }
        if (warningList == null) {
            warningList = new Array();
        }
        if (goodList == null) {
            goodList = new Array();
        }
        HashMap hashmap = new HashMap();
        String s1 = "";
        if (getProperty(pScheduleFilter).length() != 0) {
            String s2 = getProperty(pScheduleFilter);
            if (debug) {
                TextUtils.debugPrint("HistoryReport.createReport(): We have a schedule to work with (" + s2 + ")", null);
            }
            int i = s2.indexOf("_id=");
            if (i != -1) {
                String s6 = s2.substring(i + 4);
                String s3 = getScheduleSettings(s6);
                HashMap hashmap1 = TextUtils.stringToHashMap(s3);
                s3 = TextUtils.getValue(hashmap1, "_schedule");
                s1 = "<BR><FONT=-1>Show data for period: " + TextUtils.getValue(hashmap1, "_name") + "</FONT>";
                if (debug) {
                    TextUtils.debugPrint("HistoryReport.createReport(): schedulestr (" + s6 + ") = " + s3, null);
                }
                hashmap = ScheduleProperty.scheduleStringToHashMap(s3);
            }
        }
        boolean flag = getSetting("bestCaseCalc").length() > 0;
        if (getPropertyAsBoolean(pRealTime)) {
            SampleCollector asamplecollector[] = new SampleCollector[collectors.size()];
            collectors.copyTo(asamplecollector);
            RealTimeMonitorReader realtimemonitorreader = liveStream == null ? new RealTimeMonitorReader() : new RealTimeMonitorReader(liveStream);
            realtimemonitorreader.process(asamplecollector, date, date1, getPropertyAsInteger(pPrecision));
            setProperty(pStartTime, date.getTime() / 1000L);
            setProperty(pEndTime, date1.getTime() / 1000L);
        } else {
            SiteViewLogReader siteviewlogreader = liveStream == null ? new SiteViewLogReader(file) : new SiteViewLogReader(file, liveStream);
            siteviewlogreader.process(collectors, date, date1, getPropertyAsInteger(pPrecision), errorList, warningList, goodList, l, l1, l2, hashmap, flag);
        }
        String s4 = "/SiteView/docs/History.htm";
        if (getSetting("_account").length() > 0) {
            s4 = "/SiteView/docs/History.htm";
        }
        String s5 = dateString(date) + " to " + dateString(date1);
        int j = (int) getPropertyAsLong(pTimeOffset);
        if (j != 0) {
            s5 = s5 + ", " + Platform.timeZoneName(j);
        }
        setProperty(pReportPeriod, s5);
        String s7 = getProperty(pAccount);
        boolean flag1 = getSetting("_showReportThresholdSummary").length() > 0;
        boolean flag2 = getSetting("_hideReportSummary").length() == 0;
        boolean flag3 = getSetting("_showReportErrorTimeSummary").length() > 0;
        boolean flag4 = getSetting("_hideReportCharts").length() == 0;
        boolean flag5 = getSetting("_hideReportGraphs").length() == 0;
        boolean flag6 = getSetting("_hideReportTables").length() == 0;
        boolean flag7 = getSetting("_hideReportErrors").length() == 0;
        boolean flag8 = getSetting("_hideReportWarnings").length() == 0;
        boolean flag9 = getSetting("_hideReportGoods").length() == 0;
        boolean flag10 = getSetting("_showReportAlerts").length() > 0;
        boolean flag11 = getSetting("_warningNotIncluded").length() > 0;
        boolean flag12 = getSetting("_upTimeIncludeWarning").length() > 0;
        boolean flag13 = getSetting("_hideReportLinks").length() == 0 && getProperty(pAttachReport).length() == 0;
        reportTableHTML = getStringProperty(getSetting("_reportTableHTML"), reportTableHTML);
        reportTableHeaderHTML = getStringProperty(getSetting("_reportTableHeaderHTML"), reportTableHeaderHTML);
        reportTableDataHTML = getStringProperty(getSetting("_reportTableDataHTML"), reportTableDataHTML);
        errorColor = getStringProperty(getSetting("_reportTableErrorColor"), errorColor);
        warningColor = getStringProperty(getSetting("_reportTableWarningColor"), warningColor);
        goodColor = getStringProperty(getSetting("_reportTableGoodColor"), goodColor);
        nodataColor = getStringProperty(getSetting("_reportTableNoDataColor"), nodataColor);
        disabledColor = getStringProperty(getSetting("_reportTableDisabledColor"), disabledColor);
        goodRGB = makeColor(getSetting("_reportGraphGoodColor"));
        errorRGB = makeColor(getSetting("_reportGraphErrorColor"));
        warningRGB = makeColor(getSetting("_reportGraphWarningColor"));
        nodataRGB = makeColor(getSetting("_reportGraphNoDataColor"));
        disabledRGB = makeColor(getSetting("_reportGraphDisabledColor"));
        backgroundRGB = makeColor(getSetting("_reportGraphBackgroundColor"));
        maxRGB = makeColor(getSetting("_reportGraphMaxColor"));
        HashMap hashmap2 = getCustomFormat(getProperty(pFormat));
        reportTableHTML = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableHTML"), reportTableHTML);
        reportTableHeaderHTML = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableHeaderHTML"), reportTableHeaderHTML);
        reportTableDataHTML = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableDataHTML"), reportTableDataHTML);
        errorColor = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableErrorColor"), errorColor);
        warningColor = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableWarningColor"), warningColor);
        goodColor = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableGoodColor"), goodColor);
        nodataColor = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableNoDataColor"), nodataColor);
        disabledColor = getStringProperty(TextUtils.getValue(hashmap2, "_reportTableDisabledColor"), disabledColor);
        goodRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphGoodColor"), goodRGB);
        errorRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphErrorColor"), errorRGB);
        warningRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphWarningColor"), warningRGB);
        nodataRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphNoDataColor"), nodataRGB);
        disabledRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphDisabledColor"), disabledRGB);
        backgroundRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphBackgroundColor"), backgroundRGB);
        maxRGB = getColorProperty(TextUtils.getValue(hashmap2, "_reportGraphMaxColor"), maxRGB);
        if (flag11 || flag12) {
            warningColor = goodColor;
            warningRGB = goodRGB;
        }
        Array array = new Array();
        array.add(getProperty(pFilePath) + ".html");
        FileUtils.copyFile(Platform.getRoot() + File.separator + "htdocs" + File.separator + "artwork" + File.separator + logoFile, getProperty(pReportDirectory) + File.separator + logoFile);
        String s8 = Platform.getDirectoryPath("templates.historyGraphics", s7);
        File file1 = new File(s8);
        if (file1.exists()) {
            String as[] = file1.list();
            for (int k = 0; k < as.length; k ++) {
                String s11 = getProperty(pReportDirectory) + File.separator + as[k];
                FileUtils.copyFile(s8 + File.separator + as[k], s11);
                array.add(s11);
            }

        }
        FileUtils.copyFile(Platform.getRoot() + File.separator + "htdocs" + File.separator + "artwork" + File.separator + logoFile, getProperty(pReportDirectory) + File.separator + logoFile);
        String s9 = "";
        s9 = "<IMG SRC=\"" + logoFile + "\"> ";
        String s10 = "";
        if (getProperty(pStatusFilter).length() != 0) {
            boolean flag14 = getProperty(pStatusFilter).indexOf("good") != -1;
            boolean flag15 = getProperty(pStatusFilter).indexOf("warning") != -1;
            boolean flag16 = getProperty(pStatusFilter).indexOf("error") != -1;
            Array array2 = new Array();
            Enumeration enumeration2 = collectors.elements();
            while (enumeration2.hasMoreElements()) {
                SampleCollector samplecollector3 = (SampleCollector) enumeration2.nextElement();
                if (flag14 && samplecollector3.getGoodTime() > 0) {
                    array2.add(samplecollector3);
                } else if (flag15 && samplecollector3.getWarningTime() > 0) {
                    array2.add(samplecollector3);
                } else if (flag16 && samplecollector3.getErrorTime() > 0) {
                    array2.add(samplecollector3);
                }
            }
            collectors = array2;
            if (collectors.size() == 0) {
                flag13 = false;
                flag10 = false;
            }
            s10 = "<BR><FONT=-1>Show only " + getProperty(pStatusFilter) + " monitors</FONT>";
        }
        Array array1 = null;
        if (flag10) {
            HashMap hashmap3 = new HashMap(true);
            Monitor monitor = null;
            String s16 = "";
            for (int j1 = 0; j1 < collectors.size(); j1 ++) {
                SampleCollector samplecollector4 = (SampleCollector) collectors.at(j1);
                if (samplecollector4.monitor != monitor) {
                    s16 = samplecollector4.monitor.getFullID();
                }
                monitor = samplecollector4.monitor;
                if (!s16.equals("")) {
                    hashmap3.add("alert-monitor-id", s16);
                }
                s16 = "";
            }

            array1 = AlertReport.readAlertData(hashmap3, date, date1, getProperty(pAccount), hashmap);
        }
        if (liveStream != null && (getPropertyAsBoolean(pSpreadsheetDump) || getProperty(pReportType).indexOf("textFile") >= 0)) {
            textHistoryTable(printwriter);
        } else if (liveStream != null && getProperty(pReportType).indexOf("xmlFile") >= 0) {
            xmlHistoryTable(printwriter, array1, date, date1, getPropertyAsInteger(pTimeOffset));
        } else {
            if (s7.length() > 0 && Platform.isSiteSeerAccount(s7)) {
                SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(s7);
                if (monitorgroup != null) {
                    String s17 = monitorgroup.getProperty("_partner");
                    if (s17.length() > 0) {
                        MonitorGroup monitorgroup1 = (MonitorGroup) siteviewgroup.getElement(s17);
                        if (monitorgroup1 != null) {
                            printwriter.println(monitorgroup1.getProperty("_partnerReportHeaderHTML"));
                        }
                    }
                }
            }
            printwriter.print(getSetting("_reportHeaderHTML"));
            if (flag13) {
                printwriter.print("<TABLE WIDTH=\"100%\" BORDER=0><TR>\n<TD ALIGN=LEFT WIDTH=\"33%\">");
                String s12 = "";
                if (flag6) {
                    s12 = s12 + "<A HREF=#historyTable>Table Format</A>";
                }
                if (flag7 && errorList != null) {
                    if (s12.length() > 0) {
                        s12 = s12 + "<BR>";
                    }
                    s12 = s12 + "<A HREF=#errorTable>Error List</A>";
                }
                if (flag8 && warningList != null) {
                    if (s12.length() > 0) {
                        s12 = s12 + "<BR>";
                    }
                    s12 = s12 + "<A HREF=#warningTable>Warning List</A>";
                }
                if (flag9 && goodList != null) {
                    if (s12.length() > 0) {
                        s12 = s12 + "<BR>";
                    }
                    s12 = s12 + "<A HREF=#goodTable>Good List</A>";
                }
                if (flag10) {
                    if (s12.length() > 0) {
                        s12 = s12 + "<BR>";
                    }
                    s12 = s12 + "<A HREF=#alertReport>Alerts</A>";
                }
                printwriter.print(s12 + "</TD>\n");
                printwriter.print("<TD ALIGN=CENTER WIDTH=\"34%\">" + (backHTML != null ? backHTML : "<A HREF=\"#\" onClick=\"history.go(-1)\">Index of Reports</A>") + "</TD>\n");
                if (request != null && request.getValue("aboutLink").length() > 0 || getProperty("aboutLink").length() > 0) {
                    printwriter.print("<TD ALIGN=RIGHT WIDTH=\"33%\"><A HREF=\"" + s4 + "\" TARGET=\"Help\">About Reports</A></TD>\n");
                } else {
                    printwriter.print("<TD ALIGN=RIGHT WIDTH=\"33%\"></TD>\n");
                }
                printwriter.print("</TR></TABLE><p>");
            }
            printwriter.print(getSetting("_reportHTML") + "<table align=\"center\" border=\"0\"><tr><td><h2 align=\"center\">" + getProperty(pTitle) + "</h2><td></tr>" + "<tr><td><p align=\"center\">\n" + "(information from " + s5 + ") " + s1 + s10
                    + "</td></tr>" + "<tr><td><p align=\"center\"><B>" + getProperty(pDescription) + "</B></p></td></tr></table>");
            printwriter.print("\n<!--TIMEPERIOD\n" + dateStringDate(date) + "\t" + dateStringTime(date) + "\t" + dateStringDate(date1) + "\t" + dateStringTime(date1) + "\nENDTIMEPERIOD-->\n");
            printwriter.print("\n<!--OVERVIEW\n" + getSummaryString() + "ENDOVERVIEW-->\n");
            if (collectors.size() == 0) {
                printwriter.println("<P><HR><H3>No " + getProperty(pStatusFilter) + " monitors during the time period of this report</H3><HR>");
            } else {
                if (debug) {
                    TextUtils.debugPrint("HistoryReport dumping SampleCollectors START", "indent");
                    for (Enumeration enumeration = collectors.elements(); enumeration.hasMoreElements(); TextUtils.debugPrint("HistoryReport Individual Collector Dump END", "unindent")) {
                        TextUtils.debugPrint("HistoryReport Individual Collector Dump START", "indent");
                        SampleCollector samplecollector1 = (SampleCollector) enumeration.nextElement();
                        samplecollector1.print();
                    }

                    TextUtils.debugPrint("HistoryReport dumping SampleCollectors END", "unindent");
                }
                if (flag1) {
                    HTMLThresholdSummary(printwriter);
                }
                if (flag2) {
                    HTMLSummaryTable(printwriter);
                }
                if (flag3) {
                    HTMLErrorTimeSummary(printwriter);
                }
                if (getProperty(pReportType).indexOf("lineGraph") >= 0) {
                    if (flag4) {
                        if (getPropertyAsBoolean(pRealTime)) {
                            RealTimeHTMLChart(printwriter, array);
                        } else {
                            HTMLChart(printwriter, array);
                        }
                    }
                } else if (flag5) {
                    SampleCollector samplecollector = (SampleCollector) collectors.front();
                    if (samplecollector.getBucketCount() >= 3) {
                        Enumeration enumeration1 = collectors.elements();
                        int i1 = 1;
                        SampleCollector samplecollector2;
                        for (; enumeration1.hasMoreElements(); imageBarGraph(printwriter, array, samplecollector2, i1 ++)) {
                            samplecollector2 = (SampleCollector) enumeration1.nextElement();
                        }

                    }
                }
                if (flag6) {
                    HTMLTable(printwriter);
                }
                if (flag7) {
                    HTMLErrorTable(printwriter);
                }
                if (flag8) {
                    HTMLWarningTable(printwriter);
                }
                if (flag9) {
                    HTMLGoodTable(printwriter);
                }
                if (flag10) {
                    String s13 = getProperty("_showReportAlerts");
                    try {
                        AlertReport.generateReport(array1, printwriter, s13, true, date, date1, getPropertyAsInteger(pTimeOffset), reportTableHTML, reportTableHeaderHTML, reportTableDataHTML);
                    } catch (OutOfMemoryError outofmemoryerror) {
                        LogManager.log("Error", "Error generating alert report section - out of memory: " + getProperty(pAccount) + "-" + getProperty(pQueryID));
                        array1 = new Array();
                    }
                }
            }
        }
        if (!Platform.isSiteSeerAccount(s7)) {
            printwriter.print("<p><center>" + s9 + "</center>" + getSetting("_reportFooterHTML"));
            CGI.printFooter(printwriter, s7, false, false);
        } else {
            CGI.printFooter(printwriter, s7, false, true);
        }
        String s14 = getProperty(pEmail);
        if (collectors.size() == 0 && getSetting("_reportNoEmailEmpty").length() > 0) {
            s14 = "";
            LogManager.log("RunMonitor", "Report " + getProperty(pID) + " not sent because no monitors satisified criteria");
        }
        if (s14.length() > 0) {
            printwriter.flush();
            String s15 = getProperty(pTitle);
            String s18 = getProperty(pMailTemplate);
            createEmailSummaryMessage();
            if (flag10 && array1 != null) {
                try {
                    String as1[] = AlertReport.createSummaryMessage(array1, getPropertyAsLong(pTimeOffset) * 1000L);
                    setProperty(pBasicAlertSummaryText, as1[0]);
                    setProperty(pDetailAlertSummaryText, as1[1]);
                } catch (OutOfMemoryError outofmemoryerror1) {
                    LogManager.log("Error", "Error generating alert summary - out of memory: " + getProperty(pAccount) + "-" + getProperty(pQueryID));
                }
            }
            try {
                String s19 = createFromTemplateFile(Platform.getUsedDirectoryPath("templates.history", getProperty(pAccount)) + File.separator + s18).toString();
                String s20;
                if (getProperty(pAttachReport).length() > 0) {
                    array.add(getProperty(pReportDirectory) + File.separator + logoFile);
                    StringBuffer stringbuffer = new StringBuffer(s19);
                    String s21 = getProperty(pFilePath) + ".eml";
                    s15 = addAttachments(stringbuffer, s15, array, s21);
                    s19 = stringbuffer.toString();
                    HashMap hashmap4 = new HashMap(MasterConfig.getMasterConfig());
                    hashmap4.put("_hideServerInSubject", "true");
                    s20 = MailUtils.mail(hashmap4, s14, s15, s19, "", null, false, s7, s21);
                } else {
                    s20 = MailUtils.mail(MasterConfig.getMasterConfig(), s14, s15, s19, "", null, false, s7);
                }
                if (s20.length() != 0) {
                    LogManager.log("Error", "error sending email summary: " + s20);
                } else {
                    LogManager.log("RunMonitor", "sent report email, " + s14 + ", " + s15);
                }
            } catch (FileNotFoundException filenotfoundexception) {
                LogManager.log("Error", "Could not find mail template file: " + s18);
            } catch (IOException ioexception) {
                LogManager.log("Error", "Error reading mail template file: " + s18);
            } catch (OutOfMemoryError outofmemoryerror2) {
                String as2[] = { getProperty(pAccount), getProperty(pQueryID) };
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_OP_SS_REPORT_OUT_OF_MEMORY, as2);
            }
        }
        if (getPropertyAsBoolean(pSpreadsheet) || getProperty(pReportType).indexOf("textFile") >= 0) {
            textHistoryTable(null);
        }
        if (getPropertyAsBoolean(pXML) || getProperty(pReportType).indexOf("xmlFile") >= 0) {
            xmlHistoryTable(null, array1, date, date1, getPropertyAsInteger(pTimeOffset));
        }
        collectors = null;
        errorList = null;
        warningList = null;
    }

    private Color getColorProperty(String s, Color color) {
        return s.length() <= 0 ? color : makeColor(s);
    }

    private String getStringProperty(String s, String s1) {
        return s.length() <= 0 ? s1 : s;
    }

    private float getGraphMaximum(StringProperty stringproperty, Monitor monitor, float f) {
        String s = stringproperty.displayValueToNativeValue(getProperty(pVMax));
        float f1 = stringproperty.getDisplayMaximum(monitor, s);
        float f2 = f;
        if (f1 > 0.0F) {
            f2 = f1;
        } else {
            float f3 = StringProperty.toFloat(stringproperty.valueOnlyString(f2));
            if (Float.isNaN(f2) || f3 <= 1.0F) {
                f2 = StringProperty.toFloat(stringproperty.displayValueToNativeValue("1"));
                if (f2 < 1.0F) {
                    f2 = 1.0F;
                }
            } else if (!autoScale) {
                String s1 = getSetting("_reportScale");
                long l = 0L;
                if (s1.length() > 0) {
                    l = TextUtils.getClosestNumber(s1, (long) f3);
                }
                if (l == 0L) {
                    l = StringProperty.closestPowerOfTen(f3);
                }
                f2 = StringProperty.toFloat(stringproperty.displayValueToNativeValue(String.valueOf(l)));
            }
        }
        return f2;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @param array
     */
    public void HTMLChart(PrintWriter printwriter, Array array) {
        HashMap hashmap = new HashMap();
        boolean flag = getProperty(pReportType).indexOf("similarProperties") >= 0;
        boolean flag1 = getProperty(pReportType).indexOf("multipleMonitors") >= 0;
        Enumeration enumeration = collectors.elements();
        int i = 1;
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (samplecollector.isNumeric() && hashmap.get(samplecollector.getIDString()) == null) {
                Array array1 = new Array();
                boolean flag2 = true;
                boolean flag3 = true;
                StringProperty stringproperty = null;
                Monitor monitor = null;
                Enumeration enumeration1 = collectors.elements();
                while (enumeration1.hasMoreElements()) {
                    SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
                    if (samplecollector1.isNumeric() && hashmap.get(samplecollector1.getIDString()) == null && (samplecollector1.getMonitor() == samplecollector.getMonitor() || flag1)
                            && (samplecollector1.getProperty() == samplecollector.getProperty() || flag && samplecollector1.getProperty().isSimilarTo(samplecollector.getProperty()))) {
                        array1.add(samplecollector1);
                        if (stringproperty != null && stringproperty != samplecollector1.getProperty()) {
                            flag2 = false;
                        }
                        stringproperty = samplecollector1.getProperty();
                        if (monitor != null && monitor != samplecollector1.getMonitor()) {
                            flag3 = false;
                        }
                        monitor = samplecollector1.getMonitor();
                        hashmap.put(samplecollector1.getIDString(), "graphed");
                    }
                }
                if (array1.size() > 0) {
                    imageLineGraph(printwriter, array, array1, flag2, flag3, i ++);
                }
            }
        }
    }

    public void RealTimeHTMLChart(PrintWriter printwriter, Array array) {
        RealTimeReportingMonitor realtimereportingmonitor = getRealTimeReportingMonitor();
        if (realtimereportingmonitor == null) {
            LogManager.log("Error", "RealTimeHTMLChart called in a HistoryReport with no RealTimeReportingMonitor");
            return;
        }
        java.util.HashMap hashmap = new java.util.HashMap();
        for (int i = 0; i < collectors.size(); i ++) {
            SampleCollector samplecollector = (SampleCollector) collectors.at(i);
            StringProperty stringproperty = samplecollector.getProperty();
            if (stringproperty != null) {
                hashmap.put(stringproperty.getName(), samplecollector);
            }
        }

        int j = 1;
        java.util.HashMap hashmap1 = new java.util.HashMap();
        Array array1 = new Array();
        boolean flag = getProperty(pReportType).indexOf("similarProperties") >= 0;
        for (int k = 0; k < collectors.size(); k ++) {
            SampleCollector samplecollector1 = (SampleCollector) collectors.at(k);
            boolean flag1 = hashmap1.get(samplecollector1.getIDString()) == null;
            if (!samplecollector1.isNumeric() || !flag1) {
                continue;
            }
            array1.clear();
            if (flag) {
                StringProperty astringproperty[] = realtimereportingmonitor.getPropertiesOnSameGraph(samplecollector1.getProperty());
                for (int l = 0; l < astringproperty.length; l ++) {
                    SampleCollector samplecollector2 = (SampleCollector) hashmap.get(astringproperty[l].getName());
                    if (!$assertionsDisabled && samplecollector2 == null) {
                        throw new AssertionError("A RealTimeReportingMonitor has requested a non-real-time property be graphed in a real-time report!");
                    }
                    if (samplecollector2 != null) {
                        array1.add(samplecollector2);
                        hashmap1.put(samplecollector2.getIDString(), "graphed");
                    }
                }

            } else {
                array1.add(samplecollector1);
                hashmap1.put(samplecollector1.getIDString(), "graphed");
            }
            if (array1.size() > 0) {
                imageLineGraph(printwriter, array, array1, false, false, j ++);
            }
        }

    }

    public void imageLineGraph(PrintWriter printwriter, Array array, Array array1, boolean flag, boolean flag1, int i) {
        if (array1.size() == 0) {
            return;
        }
        int j = getSettingAsLong("_chartPadPerLine", 18);
        long l = getPropertyAsLong(pTimeOffset);
        float f = 0.0F;
        int k = array1.size();
        int i1 = getSettingAsLong("_chartWidth", 600);
        int j1 = getSettingAsLong("_chartHeight", 300) + j * k;
        SampleCollector samplecollector = null;
        for (int k1 = 0; k1 < array1.size(); k1 ++) {
            SampleCollector samplecollector1 = (SampleCollector) array1.at(k1);
            printwriter.println("<A NAME=Graph" + samplecollector1.getMonitorFullID() + "/" + samplecollector1.getProperty().getName() + "> </A>");
        }

        Enumeration enumeration = array1.elements();
        Hashtable hashtable = new Hashtable();
        for (int l1 = 1; enumeration.hasMoreElements(); l1 ++) {
            SampleCollector samplecollector2 = (SampleCollector) enumeration.nextElement();
            StringProperty stringproperty1 = samplecollector2.getProperty();
            String s = samplecollector2.getMonitorName();
            if (!flag) {
                s = s + "(" + samplecollector2.getPropertyLabel() + ")";
            }
            String s2 = "n/a";
            if (!Float.isNaN(samplecollector2.getAverage())) {
                s2 = stringproperty1.valueString(samplecollector2.getAverage());
            }
            String s3 = "n/a";
            if (!Float.isNaN(samplecollector2.getMaximum())) {
                s3 = stringproperty1.valueString(samplecollector2.getMaximum());
                s3 = s3 + " at " + dateString(samplecollector2.getMaximumTime() * 1000L);
                if (samplecollector2.getMaximum() > f) {
                    f = samplecollector2.getMaximum();
                }
            }
            String s5 = samplecollector2.getSampleBuffer().toString();
            String s7 = "";
            if (samplecollector2.getMaxSampleBuffer() != null) {
                s7 = samplecollector2.getMaxSampleBuffer().toString();
            }
            hashtable.put("name" + l1, s);
            hashtable.put("average" + l1, s2);
            hashtable.put("maximum" + l1, s3);
            hashtable.put("data" + l1, s5);
            if (s7.length() > 0) {
                hashtable.put("maxdata" + l1, s7);
            }
            hashtable.put("averagedSamples" + l1, "" + samplecollector2.samplesAreAveraged());
            if (!flag1) {
                continue;
            }
            long l2 = samplecollector2.getTotalTime();
            float f2 = 100F * ((float) samplecollector2.getErrorTime() / (float) l2);
            if (f2 > 100F) {
                f2 = 100F;
            }
            String s12 = "none";
            if (samplecollector2.getErrorTime() > 0) {
                s12 = secondsToString(samplecollector2.getErrorTime());
            }
            hashtable.put("errorTime" + l1, s12);
            hashtable.put("errorPercentage" + l1, TextUtils.floatToString(f2, NumericProperty.percentPrecision));
        }

        samplecollector = (SampleCollector) array1.at(0);
        StringProperty stringproperty = samplecollector.getProperty();
        f = getGraphMaximum(stringproperty, samplecollector.getMonitor(), f);
        float f1 = StringProperty.toFloat(stringproperty.valueOnlyString(f));
        if (Float.isNaN(f) || (double) f1 < 1.0D) {
            f = StringProperty.toFloat(stringproperty.displayValueToNativeValue("1"));
            if ((double) f < 1.0D) {
                f = 1.0F;
            }
        }
        String s1 = samplecollector.getMonitorName();
        boolean flag2 = getProperty(pReportType).indexOf("multipleMonitors") >= 0;
        if (flag2 && array1.size() != 1) {
            s1 = getProperty(pTitle);
        }
        String s4 = TextUtils.dateToString(getPropertyAsLong(pStartTime) * 1000L);
        String s6 = TextUtils.dateToString(getPropertyAsLong(pEndTime) * 1000L);
        String s8 = samplecollector.getPropertyLabel();
        String s9 = stringproperty.valueOnlyString(f);
        if (getProperty(pRealTime).length() > 0 && (samplecollector.getMonitor() instanceof RealTimeReportingMonitor)) {
            RealTimeReportingMonitor realtimereportingmonitor = (RealTimeReportingMonitor) samplecollector.getMonitor();
            s8 = realtimereportingmonitor.getRTGraphLabel(samplecollector.getProperty());
            long l3 = realtimereportingmonitor.getRTVerticalMax(samplecollector.getProperty());
            if (l3 > 0L) {
                s9 = Long.toString(l3);
            }
        }
        hashtable.put("title", s1);
        hashtable.put("startTime", s4);
        hashtable.put("endTime", s6);
        hashtable.put("propertyName", s8);
        hashtable.put("timeOffset", "" + l);
        hashtable.put("vertMax", s9);
        String s10 = getSetting("_reportImageType");
        String s11 = getProperty(pVirtualPath);
        String s13 = "" + i;
        if (getProperty(pQueryID).equals("0") && getProperty(pRealTime).length() == 0) {
            s13 = s13 + "-" + Long.toHexString(Platform.timeMillis());
        }
        String s14 = getProperty(pFilePath) + "-" + s13 + "." + s10;
        int i2 = s14.lastIndexOf(File.separator);
        if (i2 >= 0) {
            s11 = s14.substring(i2 + 1);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageLineGraph(): FILEPATH=" + getProperty(pFilePath), null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageLineGraph(): IMAGEFILEPATH=" + s14, null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageLineGraph(): IMAGEVIRTUALPATH=" + s11, null);
        }
        try {
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): DRAWING GRAPH " + Platform.timeMillis(), null);
            }
            LineChart linechart = new LineChart(i1, j1, hashtable, this);
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): DREW CHART " + Platform.timeMillis(), null);
            }
            DrawerGD drawergd = linechart.getDrawer();
            int ai[] = drawergd.getPixels();
            i1 = drawergd.getWidth();
            j1 = drawergd.getHeight();
            drawergd = null;
            if (getSetting("_reportHideLogo").length() == 0) {
                addLogo(ai, i1, j1);
            }
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): GOT PIXELS " + Platform.timeMillis(), null);
            }
            MemoryImageSource memoryimagesource = new MemoryImageSource(i1, j1, ai, 0, i1);
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): DRAWING IMAGE IN " + s14, null);
            }
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): STARTING IMAGE WRITE " + Platform.timeMillis(), null);
            }
            ai = null;
            Jimi.putImage(memoryimagesource, s14);
            memoryimagesource = null;
            array.add(s14);
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageLineGraph(): DONE WITH IMAGE WRITE " + Platform.timeMillis(), null);
            }
        } catch (Exception exception) {
            LogManager.log("Error", "line chart error, " + s14 + ", " + exception + ", " + FileUtils.stackTraceText(exception));
        }
        String s15 = "";
        if (getProperty(pRealTime).length() > 0) {
            s15 = "?nr=" + (new Date()).getTime();
        }
        printwriter.println("<CENTER>" + samplecollector.getMonitorDescription() + "<IMG SRC=\"" + s11 + s15 + "\"></CENTER><p>\n");
    }

    private static void addLogo(int ai[], int i, int j) throws ImageAccessException {
        if (!logoLoaded) {
            try {
                logoLoaded = true;
                logoImage = Jimi.getRasterImage(Platform.getRoot() + File.separator + "htdocs" + File.separator + "artwork" + File.separator + logoFile, 6);
                logoImage.waitInfoAvailable();
                logoWidth = logoImage.getWidth();
                logoHeight = logoImage.getHeight();
            } catch (Exception exception) {
                LogManager.log("Error", "report error, unable to load logo, " + exception.getMessage() + ", " + exception);
            }
        }
        if (logoImage != null) {
            int k = 0;
            int l = 0;
            for (int i1 = 0; i1 < logoHeight; i1 ++) {
                for (int j1 = 0; j1 < logoWidth; j1 ++) {
                    int k1 = logoImage.getPixelRGB(j1, i1);
                    int l1 = i1 + l;
                    if (l1 < j && l1 >= 0 && k1 != -1) {
                        ai[(i1 + l) * i + j1 + k] = k1;
                    }
                }

            }

        }
    }

    public void imageBarGraph(PrintWriter printwriter, Array array, SampleCollector samplecollector, int i) {
        int j = getSettingAsLong("_chartPadPerLine", 18);
        long l = getPropertyAsLong(pTimeOffset);
        float f = 0.0F;
        int k = 1;
        int i1 = getSettingAsLong("_chartWidth", 600);
        int j1 = getSettingAsLong("_chartHeight", 300) + j * k;
        printwriter.println("<A NAME=Graph" + samplecollector.getMonitorFullID() + "/" + samplecollector.getProperty().getName() + "> </A>");
        Hashtable hashtable = new Hashtable();
        int k1 = 1;
        StringProperty stringproperty = samplecollector.getProperty();
        String s = samplecollector.getPropertyLabel();
        String s1 = samplecollector.getMonitorName() + " (" + s + ")";
        String s2 = "n/a";
        if (!Float.isNaN(samplecollector.getAverage())) {
            s2 = stringproperty.valueFormattedString(samplecollector.getAverage());
        }
        String s3 = "n/a";
        String s4 = "";
        if (!Float.isNaN(samplecollector.getMaximum())) {
            s3 = stringproperty.valueFormattedString(samplecollector.getMaximum());
            s4 = dateString(samplecollector.getMaximumTime() * 1000L);
            if (samplecollector.getMaximum() > f) {
                f = samplecollector.getMaximum();
            }
        }
        String s5 = "n/a";
        String s6 = "";
        if (!Float.isNaN(samplecollector.getMinimum())) {
            s5 = stringproperty.valueString(samplecollector.getMinimum());
            s6 = dateString(samplecollector.getMinimumTime() * 1000L);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): GETTING SAMPLE BUFFER", null);
        }
        String s7 = samplecollector.getSampleBuffer().toString();
        String s8 = "";
        if (samplecollector.getMaxSampleBuffer() != null) {
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageBarGraph(): GETTING MAX SAMPLE BUFFER", null);
            }
            s8 = samplecollector.getMaxSampleBuffer().toString();
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): GOT SAMPLE BUFFER", null);
        }
        long l1 = samplecollector.getTotalTime();
        float f1 = 100F * ((float) samplecollector.getErrorTime() / (float) l1);
        if (f1 > 100F) {
            f1 = 100F;
        }
        String s9 = "none";
        if (samplecollector.getErrorTime() > 0) {
            s9 = secondsToString(samplecollector.getErrorTime());
        }
        hashtable.put("name" + k1, s1);
        hashtable.put("average" + k1, s2);
        hashtable.put("maximum" + k1, s3);
        hashtable.put("maximumTime" + k1, s4);
        hashtable.put("minimum" + k1, s5);
        hashtable.put("minimumTime" + k1, s6);
        hashtable.put("errorTime" + k1, s9);
        hashtable.put("errorPercentage" + k1, TextUtils.floatToString(f1, NumericProperty.percentPrecision));
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): ADDING DATA", null);
        }
        hashtable.put("data" + k1, s7);
        if (s8.length() > 0) {
            hashtable.put("maxdata" + k1, s8);
        }
        hashtable.put("averagedSamples" + k1, "" + samplecollector.samplesAreAveraged());
        hashtable.put("bucketCount" + k1, "" + samplecollector.getBucketCount());
        f = getGraphMaximum(stringproperty, samplecollector.getMonitor(), f);
        float f2 = StringProperty.toFloat(stringproperty.valueOnlyString(f));
        if (Float.isNaN(f) || (double) f2 < 1.0D) {
            f = StringProperty.toFloat(stringproperty.displayValueToNativeValue("1"));
            if ((double) f < 1.0D) {
                f = 1.0F;
            }
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): GETTING TITLE ETC", null);
        }
        String s10 = samplecollector.getMonitorName();
        String s11 = TextUtils.dateToString(getPropertyAsLong(pStartTime) * 1000L);
        String s12 = TextUtils.dateToString(getPropertyAsLong(pEndTime) * 1000L);
        String s13 = samplecollector.getPropertyLabel();
        String s14 = stringproperty.valueOnlyString(f);
        hashtable.put("title", s10);
        hashtable.put("startTime", s11);
        hashtable.put("endTime", s12);
        hashtable.put("propertyName", s13);
        hashtable.put("timeOffset", "" + l);
        hashtable.put("vertMax", s14);
        String s15 = getSetting("_reportImageType");
        if (s15.length() == 0) {
            s15 = ".jpg";
        }
        String s16 = getProperty(pVirtualPath);
        String s17 = "" + i;
        if (getProperty(pQueryID).equals("0") && getProperty(pRealTime).length() == 0) {
            s17 = s17 + "-" + Long.toHexString(Platform.timeMillis());
        }
        String s18 = getProperty(pFilePath) + "-" + s17 + "." + s15;
        int i2 = s18.lastIndexOf(File.separator);
        if (i2 >= 0) {
            s16 = s18.substring(i2 + 1);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): bar FILEPATH=" + getProperty(pFilePath), null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): IMAGEFILEPATH=" + s18, null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): IMAGEVIRTUALPATH=" + s16, null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): DRAWING IMAGE IN " + s18, null);
        }
        if (debug) {
            TextUtils.debugPrint("HistoryReport.imageBarGraph(): GENERATING IMAGE", null);
        }
        try {
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageBarGraph(): STARTING IMAGE WRITE " + Platform.timeMillis(), null);
            }
            BarChart barchart = new BarChart(i1, j1, hashtable, this);
            DrawerGD drawergd = barchart.getDrawer();
            int ai[] = drawergd.getPixels();
            i1 = drawergd.getWidth();
            j1 = drawergd.getHeight();
            drawergd = null;
            if (getSetting("_reportHideLogo").length() == 0) {
                addLogo(ai, i1, j1);
            }
            MemoryImageSource memoryimagesource = new MemoryImageSource(i1, j1, ai, 0, i1);
            ai = null;
            Jimi.putImage(memoryimagesource, s18);
            memoryimagesource = null;
            if (debug) {
                TextUtils.debugPrint("HistoryReport.imageBarGraph(): DONE WITH IMAGE WRITE " + Platform.timeMillis(), null);
            }
            array.add(s18);
        } catch (Throwable throwable) {
            LogManager.log("Error", "bar chart error, " + s18 + ", " + throwable + ", " + FileUtils.stackTraceText(throwable));
        }
        printwriter.println("<CENTER><H3>" + s10 + "</H3></CENTER>");
        String s19 = samplecollector.getMonitorDescription();
        if (s19 != null && s19.length() > 0) {
            printwriter.println("<CENTER><H5>" + s19 + "</H5></CENTER>");
        }
        printwriter.println("<CENTER>" + samplecollector.getMonitorDescription() + "<IMG SRC=\"" + s16 + "\"></CENTER><p>\n");
    }

    public void HTMLTable(PrintWriter printwriter) {
        int i = 1;
        int k;
        for (int j = collectors.size(); j > 0; j -= k) {
            k = 5;
            if (j < k) {
                k = j;
            }
            HTMLTable(printwriter, i, k);
            i += k;
        }

    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private Color makeColor(String s) {
        try {
            if (s.startsWith("#") && s.length() == 7) {
                return new Color((int) TextUtils.readHex(s.substring(1)));
            }
        } catch (Exception e) {
            /* empty */
        }
        return new Color(0, 0, 0);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @param i
     * @param j
     * @param k
     */
    public void HTMLTableHeader(PrintWriter printwriter, int i, int j, int k) {
        if (k != 0) {
            printwriter.print("</TABLE><br><br>");
        }
        String s = "WIDTH=\"100%\"";
        String s1 = "";
        if (k == 0) {
            s1 = "<A name=historyTable></A>";
        }
        printwriter.print(s1 + "<P>\n" + "<TABLE " + s + " " + reportTableHTML + ">\n");
        printwriter.print("<TR " + reportTableHeaderHTML + "><TH WIDTH=\"20%\"" + ">Time</TH>\n");
        int l = 80 / j;
        int i1 = 0;
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (++ i1 < i) {
                continue;
            }
            if (i1 >= i + j) {
                break;
            }
            Monitor monitor = samplecollector.monitor;
            String s2;
            if (monitor != null) {
                s2 = monitor.GetPropertyLabel(samplecollector.getProperty());
            } else {
                s2 = samplecollector.getProperty().printString();
            }
            s2 = TextUtils.toInitialUpper(s2);
            String s3 = "<A NAME=Table" + samplecollector.getMonitorFullID() + "/" + samplecollector.getProperty().getName() + " </A>";
            if (getPropertyAsBoolean(pMultipleMonitors)) {
                String s4 = samplecollector.getMonitorName();
                printwriter.print("<TH  WIDTH=\"" + l + "%\"" + ">" + s3 + s4 + "<BR>" + s2 + "</TH>\n");
            } else {
                printwriter.print("<TH  WIDTH=\"" + l + "%\"" + ">" + s3 + s2 + "</TH>\n");
            }
        }
        printwriter.print("</TR>\n");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @param i
     * @param j
     */
    public void HTMLTable(PrintWriter printwriter, int i, int j) {
        printwriter.print("<CENTER>\n");
        Enumeration enumeration = collectors.elements();
        SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
        int k = samplecollector.getBucketCount();
        if (k > 48) {
            k = 30;
        } else {
            k = 24;
        }
        for (int l = 0; l < samplecollector.getBucketCount(); l ++) {
            if (l % k == 0) {
                HTMLTableHeader(printwriter, i, j, l);
            }
            printwriter.print("<TR " + reportTableDataHTML + "><TD>" + dateString(samplecollector.getBucketStartTime(l) * 1000L) + "</TD>");
            Enumeration enumeration1 = collectors.elements();
            int i1 = 0;
            while (enumeration1.hasMoreElements()) {
                SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
                if (++ i1 < i) {
                    continue;
                }
                if (i1 >= i + j) {
                    break;
                }
                String s = null;
                String s1 = samplecollector1.getWorstCategory(l);
                if (samplecollector1.isNumeric()) {
                    s = samplecollector1.getProperty().valueFormattedString(samplecollector1.getAverage(l));
                } else {
                    s = samplecollector1.getTotalValue(l);
                }
                if (s1.equals("error")) {
                    printwriter.print("<TD BGCOLOR=\"" + errorColor + "\" ALIGN=RIGHT><B>" + s + "</B></TD>");
                } else if (s1.equals("warning")) {
                    printwriter.print("<TD BGCOLOR=\"" + warningColor + "\" ALIGN=RIGHT>" + s + "</TD>");
                } else if (s1.equals("nodata")) {
                    printwriter.print("<TD BGCOLOR=\"" + nodataColor + "\" ALIGN=RIGHT>&nbsp;</TD>");
                } else if (s1.equals("disabled")) {
                    printwriter.print("<TD BGCOLOR=\"" + disabledColor + "\" ALIGN=RIGHT>disabled</TD>");
                } else {
                    printwriter.print("<TD BGCOLOR=\"" + goodColor + "\" ALIGN=RIGHT>" + s + "</TD>");
                }
            }
            printwriter.print("</TR>\n");
        }

        printwriter.print("</TABLE><br><br></CENTER>\n");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLGoodTable(PrintWriter printwriter) {
        if (goodList == null) {
            return;
        }
        String s = "WIDTH=\"100%\"";
        String s1 = "";
        if (goodList.size() >= getSettingAsLong("_maxReportGoods", 100)) {
            s1 = "First " + getSettingAsLong("_maxReportGoods", 100) + " ";
        }
        printwriter.print("<A name=goodTable></A><P>\n<TABLE " + s + " " + reportTableHTML + ">\n" + "<CAPTION><B>" + s1 + "Goods from " + getProperty(pReportPeriod) + "</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                + "><TH WIDTH=\"20%\">Time</TH><TH>Monitor</TH><TH>Status</TH></TR>\n");
        if (goodList.size() == 0) {
            printwriter.println("<TR " + reportTableDataHTML + "><TD COLSPAN=3 ALIGN=CENTER>No Goods</TD></TR>");
        } else {
            String as[] = new String[3];
            Enumeration enumeration = goodList.elements();
            while (enumeration.hasMoreElements()) {
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                try {
                    printwriter.println("<TR BGCOLOR=\"" + goodColor + "\"><TD>" + dateString(StringProperty.toLong(as[0]) * 1000L) + "</TD><TD>" + new String(as[1].getBytes("ISO-8859-1"), I18N.nullEncoding()) + "</TD><TD>" + as[2] + "</TD></TR>");
                } catch (UnsupportedEncodingException unsupportedencodingexception) {
                    printwriter.println("<TR BGCOLOR=\"" + goodColor + "\"><TD>" + dateString(StringProperty.toLong(as[0]) * 1000L) + "</TD><TD>" + I18N.toNullEncoding(as[1]) + "</TD><TD>" + I18N.toNullEncoding(as[2]) + "</TD></TR>");
                    LogManager.log("Error", "component unsupported Null encoding or ISO-8859-1 encoding in HistoryReport ");
                }
            }

        }
        printwriter.println("</TABLE>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLWarningTable(PrintWriter printwriter) {
        if (warningList == null) {
            return;
        }
        String s = "WIDTH=\"100%\"";
        String s1 = "";
        if (warningList.size() >= getSettingAsLong("_maxReportWarnings", 100)) {
            s1 = "First " + getSettingAsLong("_maxReportWarnings", 100) + " ";
        }
        printwriter.print("<A name=warningTable></A><P>\n<TABLE " + s + " " + reportTableHTML + ">\n" + "<CAPTION><B>" + s1 + "Warnings from " + getProperty(pReportPeriod) + "</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                + "><TH WIDTH=\"20%\">Time</TH><TH>Monitor</TH><TH>Status</TH></TR>\n");
        if (warningList.size() == 0) {
            printwriter.println("<TR " + reportTableDataHTML + "><TD COLSPAN=3 ALIGN=CENTER>No warnings</TD></TR>");
        } else {
            String as[] = new String[3];
            Enumeration enumeration = warningList.elements();
            while (enumeration.hasMoreElements()) {
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                printwriter.println("<TR BGCOLOR=\"" + warningColor + "\"><TD>" + dateString(StringProperty.toLong(as[0]) * 1000L) + "</TD><TD>" + I18N.toNullEncoding(as[1]) + "</TD><TD>" + I18N.toNullEncoding(as[2]) + "</TD></TR>");
            }

        }
        printwriter.println("</TABLE>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLErrorTable(PrintWriter printwriter) {
        if (errorList == null) {
            return;
        }
        String s = "WIDTH=\"100%\"";
        String s1 = "";
        if (errorList.size() >= getSettingAsLong("_maxReportErrors", 100)) {
            s1 = "First " + getSettingAsLong("_maxReportErrors", 100) + " ";
        }
        printwriter.print("<A name=errorTable></A><P>\n<TABLE " + s + " " + reportTableHTML + ">\n" + "<CAPTION><B>" + s1 + "Errors from " + getProperty(pReportPeriod) + "</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                + "><TH WIDTH=\"20%\">Time</TH><TH>Monitor</TH><TH>Status</TH></TR>\n");
        if (errorList.size() == 0) {
            printwriter.println("<TR " + reportTableDataHTML + "><TD COLSPAN=3 ALIGN=CENTER>No errors</TD></TR>");
        } else {
            String as[] = new String[3];
            Enumeration enumeration = errorList.elements();
            while (enumeration.hasMoreElements()) {
                printwriter.println("<TR BGCOLOR=\"" + errorColor + "\"><TD>" + dateString(StringProperty.toLong(as[0]) * 1000L) + "</TD><TD>" + I18N.toNullEncoding(as[1]) + "</TD><TD>" + I18N.toNullEncoding(as[2]) + "</TD></TR>");
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
            }

        }
        printwriter.println("</TABLE>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLSummaryTable(PrintWriter printwriter) {
        boolean flag = getSetting("_hideReportLinks").length() == 0;
        boolean flag1 = getSetting("_hideReportCharts").length() == 0;
        boolean flag2 = getSetting("_hideReportGraphs").length() == 0;
        boolean flag3 = getProperty(pWarningNotIncluded).length() > 0;
        boolean flag4 = getProperty(pUpTimeIncludeWarning).length() > 0;
        if (flag && !flag1 && !flag2) {
            flag = false;
        }
        if (!flag3) {
            printwriter.println("<P><CENTER>\n<A NAME=uptimeSummary> </A>\n<TABLE WIDTH=\"100%\" " + reportTableHTML + "><CAPTION><B>Uptime Summary</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                    + "><TH>Name</TH><TH>Uptime %</TH><TH>Error %</TH><TH>Warning %</TH><TH>Last</TH></TR>");
        } else {
            printwriter.println("<P><CENTER>\n<A NAME=uptimeSummary> </A>\n<TABLE WIDTH=\"100%\" " + reportTableHTML + "><CAPTION><B>Uptime Summary</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                    + "><TH>Name</TH><TH>Uptime %</TH><TH>Error %</TH><TH>Last</TH></TR>");
        }
        String s = "";
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s.equals(samplecollector.getMonitorName())) {
                s = samplecollector.getMonitorName();
                printwriter.print("<TR " + reportTableDataHTML + "><TD>" + s + "</TD>");
                long l = samplecollector.getTotalTime();
                int i = samplecollector.getGoodTime();
                i += flag4 ? samplecollector.getWarningTime() : 0;
                String as[] = new String[3];
                as = calculateUpTime(i, samplecollector.getWarningTime(), samplecollector.getErrorTime(), l);
                if (!flag3) {
                    printwriter.print("<TD ALIGN=RIGHT>" + as[0] + "</TD>");
                    printwriter.print("<TD ALIGN=RIGHT>" + as[2] + "</TD>");
                    printwriter.print("<TD ALIGN=RIGHT>" + as[1] + "</TD>");
                } else {
                    printwriter.print("<TD ALIGN=RIGHT>" + as[0] + "</TD>");
                    printwriter.print("<TD ALIGN=RIGHT>" + as[2] + "</TD>");
                }
                String s4 = samplecollector.getLastCategory();
                if (s4.equals(Monitor.NODATA_CATEGORY)) {
                    s4 = "no data";
                } else if (!s4.equals(Monitor.GOOD_CATEGORY)) {
                    s4 = s4.toUpperCase();
                }
                printwriter.println("<TD ALIGN=RIGHT>" + s4 + "</TD></TR>");
            }
        }

        printwriter.println("</TABLE><P><A NAME=readingsSummary> </A>\n<TABLE WIDTH=\"100%\" " + reportTableHTML + "><CAPTION><B>Measurement Summary</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                + "><TH>Name</TH><TH>Measurement</TH><TH>Max</TH><TH>Avg</TH><TH>Last</TH></TR>");
        Enumeration enumeration1 = collectors.elements();
        while (enumeration1.hasMoreElements()) {
            SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
            String s1 = "Graph" + samplecollector1.getMonitorFullID() + "/" + samplecollector1.getProperty().getName();
            printwriter.print("<TR " + reportTableDataHTML + "><TD>");
            boolean flag5 = !getPropertyAsBoolean(pBasicPropertiesOnly);
            String s2 = "";
            String s3 = "";
            String s5 = "";
            if (flag5) {
                if (samplecollector1.getProperty().isPrimaryStateProperty) {
                    s3 = "<B>";
                    s5 = "</B>";
                } else {
                    s2 = "&nbsp;&nbsp;&nbsp;";
                }
            }
            if (flag) {
                printwriter.print("<A HREF=#" + s1 + ">");
            }
            printwriter.print(s2 + s3 + samplecollector1.getMonitorName() + s5);
            if (flag) {
                printwriter.print("</A>");
            }
            printwriter.print("</TD><TD>" + s2 + s3 + samplecollector1.getPropertyLabel() + s5 + "</TD>");
            if (samplecollector1.isNumeric()) {
                printwriter.print("<TD ALIGN=RIGHT>" + s3 + samplecollector1.getProperty().valueFormattedString(samplecollector1.getMaximum()) + s5 + "</TD>" + "<TD ALIGN=RIGHT>" + s3
                        + samplecollector1.getProperty().valueFormattedString(samplecollector1.getAverage()) + s5 + "</TD>" + "<TD ALIGN=RIGHT>" + s3 + samplecollector1.getProperty().valueString(samplecollector1.getLastValue()) + s5 + "</TD>");
            } else {
                printwriter.print("<TD ALIGN=RIGHT>" + s3 + samplecollector1.getTotalValue() + s5 + "</TD>" + "<TD ALIGN=RIGHT></TD>" + "<TD ALIGN=RIGHT>" + s3 + samplecollector1.getLastValue() + s5 + "</TD>");
            }
            printwriter.println("</TR>");
        }

        printwriter.println("</TABLE></CENTER><P>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLErrorTimeSummary(PrintWriter printwriter) {
        printwriter.println("<P><CENTER>\n<A NAME=errorTimeSummary> </A>\n<TABLE WIDTH=\"100%\" " + reportTableHTML + "><CAPTION><B>Time in Error Summary</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML + "><TH>Name</TH><TH>Time in Error</TH></TR>");
        String s = "";
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s.equals(samplecollector.getMonitorName())) {
                s = samplecollector.getMonitorName();
                printwriter.print("<TR " + reportTableDataHTML + "><TD>" + samplecollector.getMonitorName() + "</TD>");
                printwriter.println("<TD>" + secondsToString(samplecollector.getErrorTime()) + "</TD></TR>");
            }
        }
        printwriter.println("</TABLE></CENTER><P>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void HTMLThresholdSummary(PrintWriter printwriter) {
        printwriter.println("<P><CENTER>\n<A NAME=thresholdSummary> </A>\n<TABLE WIDTH=\"100%\" " + reportTableHTML + "><CAPTION><B>Monitor Threshold Summary</B></CAPTION>\n" + "<TR " + reportTableHeaderHTML
                + "><TH>Name</TH><TH>Error if</TH><TH>Warning if</TH><TH>Good if</TH></TR>");
        String s = "";
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s.equals(samplecollector.getMonitor().getFullID())) {
                s = samplecollector.getMonitor().getFullID();
                Monitor monitor = samplecollector.getMonitor();
                String s1 = monitor.getProperty(Monitor.pName);
                Array array = new Array();
                getThresholdList(monitor, array);
                int i = 0;
                while (i < array.size()) {
                    HashMap hashmap = (HashMap) array.at(i);
                    if (hashmap != null) {
                        String s2 = hashmap.get("error") == null ? "&nbsp;" : (String) hashmap.get("error");
                        String s3 = hashmap.get("warning") == null ? "&nbsp;" : (String) hashmap.get("warning");
                        String s4 = hashmap.get("good") == null ? "&nbsp;" : (String) hashmap.get("good");
                        String s5 = i != 0 ? "&nbsp;" : s1;
                        printwriter.println("<tr " + reportTableDataHTML + ">" + "<td>" + s5 + "</td>" + "<td>" + s2 + "</td>" + "<td>" + s3 + "</td>" + "<td>" + s4 + "</td>" + "</tr>");
                    }
                    i ++;
                }
            }
        }
        printwriter.println("</TABLE></CENTER><P>");
    }

    private String dateString(long l) {
        return dateString(l, " ");
    }

    private String dateString(Date date) {
        return dateString(date, " ");
    }

    private String dateString(Date date, String s) {
        return dateString(date.getTime(), s);
    }

    private String dateString(long l, String s) {
        long l1 = getPropertyAsLong(pTimeOffset) * 1000L;
        return TextUtils.prettyDate(new Date(l + l1), s);
    }

    private String dateStringTime(Date date) {
        long l = getPropertyAsLong(pTimeOffset) * 1000L;
        return TextUtils.prettyDateTime(new Date(date.getTime() + l));
    }

    private String dateStringDate(Date date) {
        long l = getPropertyAsLong(pTimeOffset) * 1000L;
        return TextUtils.prettyDateDate(new Date(date.getTime() + l));
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String getSummaryString() {
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            stringbuffer.append(samplecollector.getMonitorFullID());
            stringbuffer.append("\t");
            stringbuffer.append(samplecollector.getMonitorName());
            stringbuffer.append("\t");
            stringbuffer.append(samplecollector.getWorstCategory());
            stringbuffer.append("\t");
            if (samplecollector.isNumeric()) {
                stringbuffer.append(samplecollector.getProperty().valueString(samplecollector.getAverage()));
                stringbuffer.append("\t");
                stringbuffer.append(samplecollector.getProperty().valueString(samplecollector.getMaximum()));
            } else {
                stringbuffer.append(samplecollector.getTotalValue());
                stringbuffer.append("\t ");
            }
            stringbuffer.append("\n");
        }

        return stringbuffer.toString();
    }

    private String[] calculateUpTime(int i, int j, int k, long l) {
        String as[] = new String[3];
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;
        if (l != 0L) {
            f = 100F * ((float) i / (float) l);
            f1 = 100F * ((float) j / (float) l);
            f2 = 100F * ((float) k / (float) l);
            if (f > 100F) {
                f = 100F;
            }
            if (f < 0.0F) {
                f = 0.0F;
            }
            if (f1 > 100F) {
                f1 = 100F;
            }
            if (f1 < 0.0F) {
                f1 = 0.0F;
            }
            if (f2 > 100F) {
                f2 = 100F;
            }
            if (f2 < 0.0F) {
                f2 = 0.0F;
            }
            try {
                if (!TextUtils.getuseDecFormat()) {
                    f = (new Float(TextUtils.floatToString(f, NumericProperty.percentPrecision))).floatValue();
                    f1 = (new Float(TextUtils.floatToString(f1, NumericProperty.percentPrecision))).floatValue();
                    f2 = (new Float(TextUtils.floatToString(f2, NumericProperty.percentPrecision))).floatValue();
                }
                if ((double) (f + f1 + f2) < 100D) {
                    f = 100F - f1 - f2;
                }
            } catch (NumberFormatException numberformatexception) {
                f = 0.0F;
                f1 = 0.0F;
                f2 = 0.0F;
            }
        }
        as[0] = TextUtils.floatToFormattedString(f, NumericProperty.percentPrecision);
        as[1] = TextUtils.floatToFormattedString(f1, NumericProperty.percentPrecision);
        as[2] = TextUtils.floatToFormattedString(f2, NumericProperty.percentPrecision);
        return as;
    }

    private void createEmailSummaryMessage() {
        String s = createSummaryMessage(" ", true);
        setProperty(pSummaryText, s);
        String s1 = createErrorTimeSummary(" ", true);
        setProperty(pErrorTimeSummaryText, s1);
        String s2 = createThresholdSummary(" ", true);
        setProperty(pThresholdSummaryText, s2);
    }

    private String createSpreadsheetSummary(String s) {
        return createSummaryMessage(s, false);
    }

    private String createErrorTimeSpreadsheetSummary(String s) {
        return createErrorTimeSummary(s, false);
    }

    private String createThresholdSpreadsheetSummary(String s) {
        return createThresholdSummary(s, false);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @return
     */
    private String createSummaryMessage(String s, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        byte byte0 = 7;
        byte byte1 = 10;
        String s1 = "";
        String s2 = System.getProperty("line.separator");
        if (flag) {
            Enumeration enumeration = collectors.elements();
            while (enumeration.hasMoreElements()) {
                SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
                String s3 = samplecollector.getPropertyLabel();
                if (s3.length() > l) {
                    l = s3.length();
                }
                s3 = samplecollector.getMonitorName();
                if (s3.length() > i1) {
                    i1 = s3.length();
                }
                if (samplecollector.isNumeric()) {
                    String s4 = samplecollector.getProperty().valueFormattedString(samplecollector.getAverage());
                    if (s4.length() > i) {
                        i = s4.length();
                    }
                    s4 = samplecollector.getProperty().valueFormattedString(samplecollector.getMaximum());
                    if (s4.length() > j) {
                        j = s4.length();
                    }
                    s4 = samplecollector.getProperty().valueString(samplecollector.getLastValue());
                    if (s4.length() > k) {
                        k = s4.length();
                    }
                } else {
                    String s5 = samplecollector.getProperty().valueString(samplecollector.getTotalValue());
                    if (s5.length() > j) {
                        j = s5.length();
                    }
                    s5 = samplecollector.getProperty().valueString(samplecollector.getLastValue());
                    if (s5.length() > k) {
                        k = s5.length();
                    }
                }
            }
        } else {
            i = -1;
            j = -1;
            k = -1;
            l = -1;
            i1 = -1;
            byte0 = -1;
            byte1 = -1;
        }
        TextUtils.appendStringLeftJustify(stringbuffer, "Name", i1);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Uptime %", byte1);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Error %", byte1);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Warning %", byte1);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Last", byte0);
        stringbuffer.append(s2);

        Enumeration enumeration1 = collectors.elements();
        while (enumeration1.hasMoreElements()) {
            SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
            if (!s1.equals(samplecollector1.getMonitorName())) {
                s1 = samplecollector1.getMonitorName();
                TextUtils.appendStringLeftJustify(stringbuffer, s1, i1);
                stringbuffer.append(s);
                boolean flag1 = getProperty(pUpTimeIncludeWarning).length() > 0;
                int j1 = samplecollector1.getGoodTime();
                j1 += flag1 ? samplecollector1.getWarningTime() : 0;
                long l1 = samplecollector1.getTotalTime();
                String as[] = new String[3];
                as = calculateUpTime(j1, samplecollector1.getWarningTime(), samplecollector1.getErrorTime(), l1);
                TextUtils.appendStringRightJustify(stringbuffer, as[0], byte1);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, as[2], byte1);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, as[1], byte1);
                stringbuffer.append(s);
                String s6 = samplecollector1.getLastCategory();
                if (!s6.equals("good")) {
                    s6 = s6.toUpperCase();
                }
                TextUtils.appendStringRightJustify(stringbuffer, s6, byte0);
                stringbuffer.append(s2);
            }
        }

        stringbuffer.append(s2);
        TextUtils.appendStringLeftJustify(stringbuffer, "Name", i1);
        stringbuffer.append(s);
        TextUtils.appendStringLeftJustify(stringbuffer, "Measurement", l);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Max", j);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Avg", i);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Last", k);
        stringbuffer.append(s2);
        for (Enumeration enumeration2 = collectors.elements(); enumeration2.hasMoreElements(); stringbuffer.append(s2)) {
            SampleCollector samplecollector2 = (SampleCollector) enumeration2.nextElement();
            TextUtils.appendStringLeftJustify(stringbuffer, samplecollector2.getMonitorName(), i1);
            stringbuffer.append(s);
            TextUtils.appendStringLeftJustify(stringbuffer, samplecollector2.getPropertyLabel(), l);
            stringbuffer.append(s);
            if (samplecollector2.isNumeric()) {
                TextUtils.appendStringRightJustify(stringbuffer, samplecollector2.getProperty().valueString(samplecollector2.getMaximum()), j);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, samplecollector2.getProperty().valueString(samplecollector2.getAverage()), i);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, samplecollector2.getProperty().valueString(samplecollector2.getLastValue()), k);
            } else {
                TextUtils.appendStringRightJustify(stringbuffer, samplecollector2.getTotalValue(), j);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, " ", i);
                stringbuffer.append(s);
                TextUtils.appendStringRightJustify(stringbuffer, samplecollector2.getLastValue(), k);
            }
        }

        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @return
     */
    private String createErrorTimeSummary(String s, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        int j = 0;
        int k = 0;
        String s1 = System.getProperty("line.separator");
        if (flag) {
            Enumeration enumeration = collectors.elements();
            while (enumeration.hasMoreElements()) {
                SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
                String s2 = samplecollector.getPropertyLabel();
                i = Math.max(s2.length(), i);
                s2 = samplecollector.getMonitorName();
                j = Math.max(s2.length(), j);
                if (samplecollector.isNumeric()) {
                    String s3 = samplecollector.getProperty().valueString(secondsToString(samplecollector.getErrorTime()));
                    k = Math.max(s3.length(), k);
                } else {
                    String s4 = samplecollector.getProperty().valueString(secondsToString(samplecollector.getErrorTime()));
                    k = Math.max(s4.length(), k);
                }
            }

        } else {
            i = -1;
            j = -1;
            k = -1;
        }
        Enumeration enumeration1 = collectors.elements();
        TextUtils.appendStringLeftJustify(stringbuffer, "Name", j);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Time in Error", i);
        stringbuffer.append(s1);
        for (; enumeration1.hasMoreElements(); stringbuffer.append(s1)) {
            SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
            TextUtils.appendStringLeftJustify(stringbuffer, samplecollector1.getMonitorName(), j);
            stringbuffer.append(s);
            TextUtils.appendStringRightJustify(stringbuffer, secondsToString(samplecollector1.getErrorTime()), k);
        }

        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @return
     */
    private String createThresholdSummary(String s, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = System.getProperty("line.separator");
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        String s2 = "";
        Enumeration enumeration = collectors.elements();
        Array array = new Array();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s2.equals(samplecollector.getMonitorName())) {
                HashMap hashmap = new HashMap();
                s2 = samplecollector.getMonitorName();
                Monitor monitor = samplecollector.getMonitor();
                String s3 = monitor.getProperty(Monitor.pName);
                Array array1 = new Array();
                getThresholdList(monitor, array1);
                hashmap.put(s3, array1);
                array.add(hashmap);
            }
        }

        if (flag) {
            for (int i1 = 0; i1 < array.size(); i1 ++) {
                HashMap hashmap1 = (HashMap) array.at(i1);
                for (Enumeration enumeration1 = hashmap1.keys(); enumeration1.hasMoreElements();) {
                    String s4 = (String) enumeration1.nextElement();
                    i = s4.length() <= i ? i : s4.length();
                    Array array2 = (Array) hashmap1.get(s4);
                    int k1 = 0;
                    while (k1 < array2.size()) {
                        HashMap hashmap3 = (HashMap) array2.at(k1);
                        if (hashmap3 != null) {
                            String s6 = hashmap3.get("error") == null ? "" : (String) hashmap3.get("error");
                            String s7 = hashmap3.get("warning") == null ? "" : (String) hashmap3.get("warning");
                            String s8 = hashmap3.get("good") == null ? "" : (String) hashmap3.get("good");
                            j = s6.length() <= j ? j : s6.length();
                            k = s7.length() <= k ? k : s7.length();
                            l = s8.length() <= l ? l : s8.length();
                        }
                        k1 ++;
                    }
                }

            }

        } else {
            i = -1;
            j = -1;
            k = -1;
            l = -1;
        }
        TextUtils.appendStringLeftJustify(stringbuffer, "Name", i);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Error if", j);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Warning if", k);
        stringbuffer.append(s);
        TextUtils.appendStringRightJustify(stringbuffer, "Good if", l);
        stringbuffer.append(s1);
        for (int j1 = 0; j1 < array.size(); j1 ++) {
            HashMap hashmap2 = (HashMap) array.at(j1);
            for (Enumeration enumeration2 = hashmap2.keys(); enumeration2.hasMoreElements();) {
                String s5 = (String) enumeration2.nextElement();
                Array array3 = (Array) hashmap2.get(s5);
                int l1 = 0;
                while (l1 < array3.size()) {
                    HashMap hashmap4 = (HashMap) array3.at(l1);
                    if (hashmap4 != null) {
                        TextUtils.appendStringLeftJustify(stringbuffer, s5, i);
                        stringbuffer.append(s);
                        TextUtils.appendStringLeftJustify(stringbuffer, hashmap4.get("error") == null ? "" : (String) hashmap4.get("error"), j);
                        stringbuffer.append(s);
                        TextUtils.appendStringLeftJustify(stringbuffer, hashmap4.get("warning") == null ? "" : (String) hashmap4.get("warning"), k);
                        stringbuffer.append(s);
                        TextUtils.appendStringLeftJustify(stringbuffer, hashmap4.get("good") == null ? "" : (String) hashmap4.get("good"), l);
                        stringbuffer.append(s1);
                    }
                    l1 ++;
                }
            }

        }

        return stringbuffer.toString();
    }

    private String createXMLWarningSummary() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<warnings>\n");
        if (warningList != null) {
            String s = "";
            if (errorList.size() >= getSettingAsLong("_maxReportWarnings", 100)) {
                s = "First " + getSettingAsLong("_maxReportWarnings", 100) + " ";
            }
            s = s + "Warnings from " + getProperty(pReportPeriod);
            stringbuffer.append(TextUtils.escapeXML("description", s));
            String as[] = new String[3];
            for (Enumeration enumeration = warningList.elements(); enumeration.hasMoreElements(); stringbuffer.append("</warning>\n")) {
                stringbuffer.append("<warning>\n");
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                stringbuffer.append(TextUtils.escapeXML("date", dateString(StringProperty.toLong(as[0]) * 1000L)) + "\n");
                stringbuffer.append(TextUtils.escapeXML("monitor", as[1]) + "\n");
                stringbuffer.append(TextUtils.escapeXML("message", as[2]) + "\n");
            }

        }
        stringbuffer.append("</warnings>\n");
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String createXMLSummaryMessage() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<summary>\n");
        String s = "";
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s.equals(samplecollector.getMonitorName())) {
                s = samplecollector.getMonitorName();
                stringbuffer.append("<row>\n");
                stringbuffer.append("<name>" + s + "</name>\n");
                long l = samplecollector.getTotalTime();
                stringbuffer.append("<uptime>");
                String as[] = new String[3];
                boolean flag = getProperty(pUpTimeIncludeWarning).length() > 0;
                int i = samplecollector.getGoodTime();
                i += flag ? samplecollector.getWarningTime() : 0;
                as = calculateUpTime(i, samplecollector.getWarningTime(), samplecollector.getErrorTime(), l);
                TextUtils.appendStringRightJustify(stringbuffer, as[0], -1);
                stringbuffer.append("</uptime><error>");
                TextUtils.appendStringRightJustify(stringbuffer, as[2], -1);
                stringbuffer.append("</error><warning>");
                TextUtils.appendStringRightJustify(stringbuffer, as[1], -1);
                stringbuffer.append("</warning><last>");
                String s1 = samplecollector.getLastCategory();
                if (!s1.equals("good")) {
                    s1 = s1.toUpperCase();
                }
                TextUtils.appendStringRightJustify(stringbuffer, s1, -1);
                stringbuffer.append("</last>\n");
                stringbuffer.append("</row>\n");
            }
        }

        for (Enumeration enumeration1 = collectors.elements(); enumeration1.hasMoreElements(); stringbuffer.append("</measurement>\n")) {
            SampleCollector samplecollector1 = (SampleCollector) enumeration1.nextElement();
            stringbuffer.append("<measurement>\n");
            stringbuffer.append("<monitor>" + samplecollector1.getMonitorName() + "</monitor>\n");
            stringbuffer.append("<label>" + samplecollector1.getPropertyLabel() + "</label>\n");
            if (samplecollector1.isNumeric()) {
                stringbuffer.append("<max>" + samplecollector1.getProperty().valueFormattedString(samplecollector1.getMaximum()) + "</max>");
                stringbuffer.append("<ave>" + samplecollector1.getProperty().valueFormattedString(samplecollector1.getAverage()) + "</ave>");
                stringbuffer.append("<last>" + samplecollector1.getProperty().valueString(samplecollector1.getLastValue()) + "</last>\n");
            } else {
                stringbuffer.append("<total>" + samplecollector1.getProperty().valueString(samplecollector1.getTotalValue()) + "</total>\n");
                stringbuffer.append("<last>" + samplecollector1.getProperty().valueString(samplecollector1.getLastValue()) + "</last>\n");
            }
        }

        stringbuffer.append("</summary>\n");
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String createXMLErrorTimeSummaryMessage() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<errorTimeSummary>\n");
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            stringbuffer.append("<row>\n");
            stringbuffer.append("<name>" + samplecollector.getMonitorName() + "</name>\n");
            stringbuffer.append("<errorTime>" + secondsToString(samplecollector.getErrorTime()) + "</errorTime>");
            stringbuffer.append("</row>\n");
        }

        stringbuffer.append("</errorTimeSummary>\n");
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String createXMLErrorSummary() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<errors>\n");
        if (errorList != null) {
            String s = "";
            if (errorList.size() >= getSettingAsLong("_maxReportErrors", 100)) {
                s = "First " + getSettingAsLong("_maxReportErrors", 100) + " ";
            }
            s = s + "Errors from " + getProperty(pReportPeriod);
            stringbuffer.append(TextUtils.escapeXML("description", s));
            String as[] = new String[3];
            Enumeration enumeration = errorList.elements();
            while (enumeration.hasMoreElements()) {
                stringbuffer.append("<error>\n");
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                stringbuffer.append(TextUtils.escapeXML("date", dateString(StringProperty.toLong(as[0]) * 1000L)) + "\n");
                stringbuffer.append(TextUtils.escapeXML("monitor", as[1]) + "\n");
                stringbuffer.append(TextUtils.escapeXML("message", as[2]) + "\n");
                stringbuffer.append("</error>\n");
            }

        }
        stringbuffer.append("</errors>\n");
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private String createXMLThresholdSummaryMessage() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<monitorThresholdSummary>\n");
        String s = "";
        Enumeration enumeration = collectors.elements();
        while (enumeration.hasMoreElements()) {
            SampleCollector samplecollector = (SampleCollector) enumeration.nextElement();
            if (!s.equals(samplecollector.getMonitorName())) {
                s = samplecollector.getMonitorName();
                Monitor monitor = samplecollector.getMonitor();
                String s1 = monitor.getProperty(Monitor.pName);
                Array array = new Array();
                getThresholdList(monitor, array);
                for (int i = 0; i < array.size(); i ++) {
                    HashMap hashmap = (HashMap) array.at(i);
                    if (hashmap != null) {
                        String s2 = hashmap.get("error") == null ? "" : (String) hashmap.get("error");
                        String s3 = hashmap.get("warning") == null ? "" : (String) hashmap.get("warning");
                        String s4 = hashmap.get("good") == null ? "" : (String) hashmap.get("good");
                        stringbuffer.append("<row>\n");
                        stringbuffer.append(TextUtils.escapeXML("name", s1) + "\n");
                        stringbuffer.append(TextUtils.escapeXML("errorThreshold", s2));
                        stringbuffer.append(TextUtils.escapeXML("warningThreshold", s3));
                        stringbuffer.append(TextUtils.escapeXML("goodThreshold", s4));
                        stringbuffer.append("</row>\n");
                    }
                }
            }
        }
        stringbuffer.append("</monitorThresholdSummary>\n");
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private String createErrorSummary(String s) {
        String s1 = System.getProperty("line.separator");
        StringBuffer stringbuffer = new StringBuffer();
        if (errorList != null) {
            String s2 = "";
            if (errorList.size() >= getSettingAsLong("_maxReportErrors", 100)) {
                stringbuffer.append("First " + getSettingAsLong("_maxReportErrors", 100) + " ");
            }
            stringbuffer.append(s2 + "Errors from " + getProperty(pReportPeriod));
            stringbuffer.append(s1 + "Time" + s + "Monitor" + s + "Status" + s1);
            String as[] = new String[3];
            Enumeration enumeration = errorList.elements();
            while (enumeration.hasMoreElements()) {
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                stringbuffer.append(dateString(StringProperty.toLong(as[0]) * 1000L));
                stringbuffer.append(s);
                stringbuffer.append(as[1]);
                stringbuffer.append(s);
                stringbuffer.append(as[2]);
                stringbuffer.append(s1);
            }

        }
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private String createWarningSummary(String s) {
        String s1 = System.getProperty("line.separator");
        StringBuffer stringbuffer = new StringBuffer();
        if (warningList != null) {
            String s2 = "";
            if (warningList.size() >= getSettingAsLong("_maxReportWarnings", 100)) {
                stringbuffer.append("First " + getSettingAsLong("_maxReportWarnings", 100) + " ");
            }
            stringbuffer.append(s2 + "Warnings from " + getProperty(pReportPeriod));
            stringbuffer.append(s1 + "Time" + s + "Monitor" + s + "Status" + s1);
            String as[] = new String[3];
            Enumeration enumeration = warningList.elements();
            while (enumeration.hasMoreElements()) {
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                stringbuffer.append(dateString(StringProperty.toLong(as[0]) * 1000L));
                stringbuffer.append(s);
                stringbuffer.append(as[1]);
                stringbuffer.append(s);
                stringbuffer.append(as[2]);
                stringbuffer.append(s1);
            }

        }
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private String createGoodSummary(String s) {
        String s1 = System.getProperty("line.separator");
        StringBuffer stringbuffer = new StringBuffer();
        if (goodList != null) {
            String s2 = "";
            if (goodList.size() >= getSettingAsLong("_maxReportGoods", 100)) {
                stringbuffer.append("First " + getSettingAsLong("_maxReportGoods", 100) + " ");
            }
            stringbuffer.append(s2 + "OKs from " + getProperty(pReportPeriod));
            stringbuffer.append(s1 + "Time" + s + "Monitor" + s + "Status" + s1);
            String as[] = new String[3];
            Enumeration enumeration = goodList.elements();
            while (enumeration.hasMoreElements()) {
                int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
                if (i < 3) {
                    as[2] = "";
                }
                stringbuffer.append(dateString(StringProperty.toLong(as[0]) * 1000L));
                stringbuffer.append(s);
                stringbuffer.append(as[1]);
                stringbuffer.append(s);
                stringbuffer.append(as[2]);
                stringbuffer.append(s1);
            }

        }
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     */
    private void textHistoryTable(PrintWriter printwriter) {
        boolean flag = getSetting("_showReportThresholdSummary").length() > 0;
        boolean flag1 = getSetting("_hideReportSummary").length() == 0;
        boolean flag2 = getSetting("_showReportErrorTimeSummary").length() > 0;
        boolean flag3 = getSetting("_hideReportTables").length() == 0 || getSetting("_hideReportGraphs").length() == 0 || getSetting("_hideReportCharts").length() == 0;
        boolean flag4 = getSetting("_hideReportErrors").length() == 0;
        boolean flag5 = getSetting("_hideReportWarnings").length() == 0;
        boolean flag6 = getSetting("_hideReportGoods").length() == 0;
        boolean flag7 = getSetting("_reportSendFileAsAttachment").length() != 0;
        String s = getSetting("_reportDelimiter");
        if (s.length() == 0) {
            s = ",";
        }
        FileOutputStream fileoutputstream = null;
        String s1 = "";
        try {
            if (printwriter == null) {
                s1 = getProperty(pFilePath) + ".txt";
                fileoutputstream = new FileOutputStream(s1);
                printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            }
            if (printwriter != null && fileoutputstream == null) {
                printwriter.print("\n\n<PRE>\n");
            }
            if (flag3) {
                printwriter.print("time");
                SampleCollector samplecollector = null;
                Enumeration enumeration = collectors.elements();
                while (enumeration.hasMoreElements()) {
                    SampleCollector samplecollector1 = (SampleCollector) enumeration.nextElement();
//                    String s13 = TextUtils.toInitialUpper(TopazAPI.getTopazName(samplecollector1.getMonitor(), samplecollector1.getProperty()));
                    printwriter.print(s);
                    if (getPropertyAsBoolean(pMultipleMonitors)) {
//                        printwriter.print(samplecollector1.getMonitorName() + " " + s13);
                    } else {
//                        printwriter.print(s13);
                    }
                    if (samplecollector == null) {
                        samplecollector = samplecollector1;
                    }
                }

                enumeration = collectors.elements();
                while (enumeration.hasMoreElements()) {
                    SampleCollector samplecollector2 = (SampleCollector) enumeration.nextElement();
//                    String s14 = TextUtils.toInitialUpper(TopazAPI.getTopazName(samplecollector2.getMonitor(), samplecollector2.getProperty()));
                    printwriter.print(s);
                    if (getPropertyAsBoolean(pMultipleMonitors)) {
//                        printwriter.print(samplecollector2.getMonitorName() + " " + s14);
                    } else {
//                        printwriter.print(s14);
                    }
                    if (samplecollector == null) {
                        samplecollector = samplecollector2;
                    }
                }
                printwriter.println("");

                for (int i = 0; samplecollector != null && i < samplecollector.getBucketCount(); i ++) {
                    printwriter.print(dateString(samplecollector.getBucketStartTime(i) * 1000L));
                    for (Enumeration enumeration1 = collectors.elements(); enumeration1.hasMoreElements();) {
                        SampleCollector samplecollector3 = (SampleCollector) enumeration1.nextElement();
                        String s17 = null;
                        String s19 = samplecollector3.getWorstCategory(i);
                        if (samplecollector3.isNumeric()) {
                            s17 = samplecollector3.getProperty().valueFormattedString(samplecollector3.getAverage(i));
                        } else {
                            s17 = samplecollector3.getTotalValue(i);
                        }
                        printwriter.print(s);
                        if (s19.equals("nodata")) {
                            printwriter.print("no data");
                        } else {
                            printwriter.print(s17);
                        }
                    }

                    for (Enumeration enumeration2 = collectors.elements(); enumeration2.hasMoreElements();) {
                        SampleCollector samplecollector4 = (SampleCollector) enumeration2.nextElement();
                        String s18 = null;
                        String s20 = samplecollector4.getWorstCategory(i);
                        if (samplecollector4.isNumeric()) {
                            s18 = samplecollector4.getProperty().valueOnlyString(samplecollector4.getAverage(i));
                        } else {
                            s18 = samplecollector4.getTotalValue(i);
                        }
                        printwriter.print(s);
                        if (s20.equals("nodata")) {
                            printwriter.print("no data");
                        } else {
                            printwriter.print(s18);
                        }
                    }

                    printwriter.println("");
                }

            }
            if (flag) {
                String s2 = createThresholdSpreadsheetSummary(s);
                printwriter.println("");
                printwriter.println(s2);
            }
            if (flag1) {
                String s3 = createSpreadsheetSummary(s);
                printwriter.println("");
                printwriter.println(s3);
            }
            if (flag2) {
                String s4 = createErrorTimeSpreadsheetSummary(s);
                printwriter.println("");
                printwriter.println(s4);
            }
            if (flag4) {
                String s5 = createErrorSummary(s);
                printwriter.println("");
                printwriter.println(s5);
            }
            if (flag5) {
                String s6 = createWarningSummary(s);
                printwriter.println("");
                printwriter.println(s6);
            }
            if (flag6) {
                String s7 = createGoodSummary(s);
                printwriter.println("");
                printwriter.println(s7);
            }
        } catch (FileNotFoundException filenotfoundexception) {
            LogManager.log("Error", "Could not write history file: " + getProperty(pFilePath) + ".txt");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error writing history file: " + getProperty(pFilePath) + ".txt" + ": " + ioexception.getMessage());
        } finally {
            try {
                if (printwriter != null) {
                    printwriter.flush();
                }
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
            } catch (IOException ioexception1) {
                LogManager.log("Error", "Cannot close history file: " + getProperty(pFilePath));
            }
        }
        if (printwriter != null && fileoutputstream == null) {
            printwriter.print("\n\n</PRE>\n\n");
        }
        String s8 = getProperty(pEmailData);
        if (s8.length() > 0) {
            if (flag7) {
                String s9 = "Comma-delimited file is attached";
                String s11 = getProperty(pTitle);
                String s15 = sendMail(MasterConfig.getMasterConfig(), s8, s11, s9, s1);
                if (s15.length() != 0) {
                    LogManager.log("Error", "error sending report data email, " + s15);
                } else {
                    LogManager.log("RunMonitor", "sent report data email, " + s8 + ", " + s11);
                }
            } else {
                try {
                    String s10 = FileUtils.readFile(s1).toString();
                    String s12 = getProperty(pTitle);
                    String s16 = MailUtils.mail(MasterConfig.getMasterConfig(), s8, s12, s10, "", null, false);
                    if (s16.length() != 0) {
                        LogManager.log("Error", "error sending report data email, " + s16);
                    } else {
                        LogManager.log("RunMonitor", "sent report data email, " + s8 + ", " + s12);
                    }
                } catch (Exception exception) {
                    LogManager.log("Error", "error creating report data email, " + exception);
                }
            }
        }
    }

    private String sendMail(HashMap hashmap, String s, String s1, String s2, String s3) {
        String s4 = TextUtils.getValue(hashmap, "_mailServer");
        if (s4.length() == 0) {
            return "missing mail server";
        }
        String s6 = sendMailMsg(hashmap, s4, s, s1, s2, s3);
        if (s6.length() != 0) {
            String s5 = TextUtils.getValue(hashmap, "_mailServerBackup");
            if (s5.length() > 0) {
                LogManager.log("Error", "Using backup mail server (" + s6 + ") for " + s1 + " To: " + s);
                s6 = sendMailMsg(hashmap, s5, s, s1, s2, s3);
            }
        }
        return s6;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @return
     */
    private String sendMailMsg(HashMap hashmap, String s, String s1, String s2, String s3, String s4) {
        String as[];
        String s5;
        s1 = TextUtils.toEmailList(s1);
        as = TextUtils.split(s1, ",");
        s5 = TextUtils.getValue(hashmap, "_fromAddress");
        if (s5.length() == 0 && as.length > 0) {
            s5 = as[0];
        }
        String s6 = "";
        if (TextUtils.getValue(hashmap, "_hideServerInSubject").length() == 0) {
            s6 = TextUtils.getValue(hashmap, "_webserverAddress");
            int i = s6.indexOf(":");
            if (i >= 0) {
                s6 = s6.substring(0, i);
            }
        }
        if (s6.length() != 0) {
            s2 = s2 + " (" + s6 + ")";
        }

        try {
            Client client = new Client(s);
            if (s4.length() > 0) {
                String s7;
                Object obj = null;
                int j = s4.lastIndexOf(File.separator);
                if (j < 0) {
                    s7 = s4;
                } else {
                    s7 = s4.substring(j + 1);
                }

                FileInputStream fileinputstream;
                try {
                    fileinputstream = new FileInputStream(s4);
                } catch (FileNotFoundException filenotfoundexception) {
                    return "Attachment file could not be found";
                }
                Attachment aattachment[] = new Attachment[1];
                aattachment[0] = new Attachment(s7, s7, fileinputstream);
                client.setAttachments(aattachment);
            }
            client.setSender(s5);
            client.setSubject(s2);
            client.setToRecipients(as);
            client.start();
            client.write(s3);
            client.finish();
        } catch (SMTPException smtpexception) {
            return smtpexception.getMessage();
        }
        return "";
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @param array
     * @param date
     * @param date1
     * @param i
     */
    private void xmlHistoryTable(PrintWriter printwriter, Array array, Date date, Date date1, int i) {
        boolean flag = getSetting("_showReportThresholdSummary").length() > 0;
        boolean flag1 = getSetting("_hideReportSummary").length() == 0;
        boolean flag2 = getSetting("_showReportErrorTimeSummary").length() > 0;
        boolean flag3 = getSetting("_hideReportTables").length() == 0 || getSetting("_hideReportGraphs").length() == 0 || getSetting("_hideReportCharts").length() == 0;
        boolean flag4 = getSetting("_hideReportErrors").length() == 0;
        boolean flag5 = getSetting("_hideReportWarnings").length() == 0;
        boolean flag6 = getSetting("_showReportAlerts").length() != 0;
        boolean flag7 = getSetting("_reportSendFileAsAttachment").length() != 0;
        String s = getSetting("_reportDelimiter");
        if (s.length() == 0) {
            s = ",";
        }
        FileOutputStream fileoutputstream = null;
        String s1 = "";
        try {
            if (printwriter == null) {
                s1 = getProperty(pFilePath) + ".xml";
                fileoutputstream = new FileOutputStream(s1);
                printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            }
            printwriter.println("<?xml version=\"1.0\"?>");
            printwriter.println("<report>");
            if (collectors.size() == 0) {
                printwriter.println("<rows>");
                printwriter.println("</rows>");
            } else {
                if (flag) {
                    printwriter.println(createXMLThresholdSummaryMessage());
                }
                if (flag1) {
                    printwriter.println(createXMLSummaryMessage());
                }
                if (flag2) {
                    printwriter.println(createXMLErrorTimeSummaryMessage());
                }
                if (flag3) {
                    SampleCollector samplecollector = null;
                    Enumeration enumeration = collectors.elements();
                    while (enumeration.hasMoreElements()) {
                        SampleCollector samplecollector1 = (SampleCollector) enumeration.nextElement();
                        if (samplecollector == null) {
                            samplecollector = samplecollector1;
                        }
                    }

                    printwriter.println("<rows>");
                    for (int j = 0; j < samplecollector.getBucketCount(); j ++) {
                        printwriter.println("<row>");
                        printwriter.print(TextUtils.escapeXML("date", dateString(samplecollector.getBucketStartTime(j) * 1000L)));
                        for (Enumeration enumeration1 = collectors.elements(); enumeration1.hasMoreElements(); printwriter.println("</sample>")) {
                            printwriter.println("<sample>");
                            SampleCollector samplecollector2 = (SampleCollector) enumeration1.nextElement();
                            String s9 = TextUtils.toInitialUpper(samplecollector2.getProperty().printString());
                            if (getPropertyAsBoolean(pMultipleMonitors)) {
                                s9 = samplecollector2.getMonitorName() + " " + s9;
                            }
                            printwriter.println(TextUtils.escapeXML("label", s9));
                            String s10 = null;
                            String s11 = samplecollector2.getWorstCategory(j);
                            if (samplecollector2.isNumeric()) {
                                s10 = samplecollector2.getProperty().valueFormattedString(samplecollector2.getAverage(j));
                            } else {
                                s10 = samplecollector2.getTotalValue(j);
                            }
                            if (s11.equals("nodata")) {
                                s10 = "no data";
                            }
                            printwriter.print(TextUtils.escapeXML("value", s10));
                            if (samplecollector2.isNumeric()) {
                                s10 = samplecollector2.getProperty().valueOnlyString(samplecollector2.getAverage(j));
                            } else {
                                s10 = samplecollector2.getTotalValue(j);
                            }
                            if (s11.equals("nodata")) {
                                s10 = "no data";
                            }
                            printwriter.print(TextUtils.escapeXML("data", s10));
                        }

                        printwriter.println("</row>");
                    }

                    printwriter.println("</rows>");
                }
                if (flag4) {
                    printwriter.println(createXMLErrorSummary());
                }
                if (flag5) {
                    printwriter.println(createXMLWarningSummary());
                }
                if (flag6 && array != null) {
                    AlertReport.generateXMLReport(array, printwriter, date, date1, i);
                }
            }
            printwriter.print("</report>");
        } catch (FileNotFoundException filenotfoundexception) {
            LogManager.log("Error", "Could not write history file: " + getProperty(pFilePath) + ".txt");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error writing history file: " + getProperty(pFilePath) + ".txt" + ": " + ioexception.getMessage());
        } finally {
            try {
                if (printwriter != null) {
                    printwriter.flush();
                }
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
            } catch (IOException ioexception1) {
                LogManager.log("Error", "Cannot close history file: " + getProperty(pFilePath));
            }
        }
        String s2 = getProperty(pXMLEmailData);
        if (s2.length() > 0) {
            if (flag7) {
                String s3 = "XML file is attached";
                String s5 = getProperty(pTitle);
                String s7 = sendMail(MasterConfig.getMasterConfig(), s2, s5, s3, s1);
                if (s7.length() != 0) {
                    LogManager.log("Error", "error sending report data email, " + s7);
                } else {
                    LogManager.log("RunMonitor", "sent report data email, " + s2 + ", " + s5);
                }
            } else {
                try {
                    String s4 = FileUtils.readFile(s1).toString();
                    String s6 = getProperty(pTitle);
                    String s8 = MailUtils.mail(MasterConfig.getMasterConfig(), s2, s6, s4, "", null, false);
                    if (s8.length() != 0) {
                        LogManager.log("Error", "error sending report data email, " + s8);
                    } else {
                        LogManager.log("RunMonitor", "sent report data email, " + s2 + ", " + s6);
                    }
                } catch (Exception exception) {
                    LogManager.log("Error", "error creating report data email, " + exception);
                }
            }
        }
    }

    private void initializeFilePaths() {
        String s = "htdocs";
        if (getProperty(pQueryID).equals("0") && getProperty(pAccount).equals("user")) {
            s = "userhtml";
        }
        File file = new File(Platform.getDirectoryPath(s, getProperty(pAccount)) + File.separator + "Reports-" + getProperty(pQueryID));
        String s1 = Platform.getURLPath(s, getProperty(pAccount)) + "/Reports-" + getProperty(pQueryID);
        if (!file.exists()) {
            boolean flag = file.mkdirs();
            if (!flag) {
                LogManager.log("Error", "Could not create report directory: " + file.getAbsolutePath());
            }
            Platform.chmod(file, "rwx");
        }
        String s2;
        if (getProperty(pRealTime).length() > 0) {
            s2 = getProperty(pTargets).replace(' ', '_');
        } else {
            s2 = TextUtils.dateToFileName(reportDate);
        }
        setProperty(pReportDirectory, file.getAbsolutePath());
        setProperty(pReportVirtual, s1 + "/");
        setProperty(pVirtualPath, s1 + "/Report-" + s2 + ".html");
        setProperty(pTextReportVirtualPath, s1 + "/Report-" + s2 + ".txt");
        setProperty(pXMLReportVirtualPath, s1 + "/Report-" + s2 + ".xml");
        setProperty(pIndexVirtualPath, s1 + "/index.html");
        setProperty(pReadOnlyVirtualPath, Platform.getURLPath("userhtml", getProperty(pAccount)) + "/Reports-" + getProperty(pQueryID) + "/Report-" + s2 + ".html");
        setProperty(pReadOnlyTextReportVirtualPath, Platform.getURLPath("userhtml", getProperty(pAccount)) + "/Reports-" + getProperty(pQueryID) + "/Report-" + s2 + ".txt");
        setProperty(pReadOnlyXMLReportVirtualPath, Platform.getURLPath("userhtml", getProperty(pAccount)) + "/Reports-" + getProperty(pQueryID) + "/Report-" + s2 + ".xml");
        setProperty(pReadOnlyIndexVirtualPath, Platform.getURLPath("userhtml", getProperty(pAccount)) + "/Reports-" + getProperty(pQueryID) + "/index.html");
        setProperty(pFilePath, file.getAbsolutePath() + File.separator + "Report-" + s2);
    }

    private void initializeTimeParameters() {
        int i = getPropertyAsInteger(pWindow);
        String s = getProperty(pWindow);
        if (i == 0 && s.equalsIgnoreCase("monthToDate")) {
            Date date = Platform.makeDate();
            GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTime(date);
            i = (gregoriancalendar.get(5) - 1) * 0x15180;
            setProperty(pWindow, i);
        }
        if (i <= 0) {
            Enumeration enumeration = collectors.elements();
            int j;
            SampleCollector samplecollector;
            for (j = 0x7fffffff; enumeration.hasMoreElements(); j = Math.min(j, samplecollector.getMonitor().getPropertyAsInteger(AtomicMonitor.pFrequency))) {
                samplecollector = (SampleCollector) enumeration.nextElement();
            }

            int k = getSettingAsLong("_reportDefaultSamples", 48);
            if (j == 0x7fffffff || j <= 0) {
                j = 600;
            }
            i = j * k;
            setProperty(pWindow, i);
            setProperty(pEndTime, Math.round(Platform.timeMillis() / 1000L));
            setProperty(pStartTime, getPropertyAsInteger(pEndTime) - getPropertyAsInteger(pWindow));
            setProperty(pPrecision, j);
            setProperty(pDirection, "back");
            setProperty(pUsingDefaultTimeParameters, "true");
        } else {
            Date date1 = Platform.makeDate();
            GregorianCalendar gregoriancalendar1 = new GregorianCalendar();
            gregoriancalendar1.setTime(date1);
            long l = 0L;
            long l1 = 0L;
            if (getProperty(pStartDay).equalsIgnoreCase("today")) {
                l = (new Date(date1.getYear(), date1.getMonth(), date1.getDate())).getTime() / 1000L;
            } else {
                l = TextUtils.stringToDateSeconds(getProperty(pStartDay));
            }
            if (getProperty(pStartHour).equalsIgnoreCase("now")) {
                l1 = date1.getSeconds() + date1.getMinutes() * 60 + date1.getHours() * 3600;
            } else {
                l1 = TextUtils.stringToDaySeconds(getProperty(pStartHour));
                String s1 = getProperty(pSchedule);
                if (s1.length() > 0) {
                    String as[] = TextUtils.split(getProperty(pSchedule));
                    int k2 = 0;
                    try {
                        k2 = Integer.parseInt(as[2]);
                    } catch (NumberFormatException numberformatexception) {
                    }
                    if (l1 < 0L && k2 < 0) {
                        l1 += 0x15180L;
                    } else if (l1 > (long) k2) {
                        l1 -= 0x15180L;
                    }
                }
            }
            setProperty(pStartTime, l + l1);
            if (getProperty(pPrecision).equalsIgnoreCase("default")) {
                switch (i) {
                case 3600:
                    setProperty(pPrecision, 60);
                    break;

                case 86400:
                    setProperty(pPrecision, 1800);
                    break;

                case 604800:
                    setProperty(pPrecision, 14400);
                    break;

                case 2592000:
                    setProperty(pPrecision, 43200);
                    break;

                default:
                    int i1 = (i / 40 / 60) * 60;
                    if (i1 == 0) {
                        i1 = 60;
                    }
                    setProperty(pPrecision, i1);
                    break;
                }
            } else {
                int j1 = StringProperty.toInteger(getProperty(pPrecision));
                int i2 = 100;
                if (getSetting("_reportMaxBuckets").length() > 0) {
                    i2 = StringProperty.toInteger(getSetting("_reportMaxBuckets"));
                    if (i2 <= 0) {
                        i2 = 100;
                    }
                    if (i2 < 3) {
                        i2 = 3;
                    }
                }
                if (i / j1 > i2) {
                    j1 = Math.round(i / i2);
                    j1 = (j1 / 60) * 60;
                    if (j1 == 0) {
                        j1 = 60;
                    }
                }
                setProperty(pPrecision, j1);
            }
            if (i == 0x278d00) {
                int k1 = date1.getMonth();
                if (date1.getDate() == 1) {
                    int j2 = k1 - 1;
                    if (j2 < 0) {
                        j2 = 11;
                    }
                    i = 0x15180 * MONTH_DAYS[j2];
                } else if (date1.getDate() >= 28) {
                    i = 0x15180 * MONTH_DAYS[date1.getMonth()];
                }
            }
            if (getPropertyAsInteger(pDirection) > 0) {
                setProperty(pDirection, "forward");
                setProperty(pEndTime, getPropertyAsInteger(pStartTime) + i);
            } else {
                setProperty(pDirection, "back");
                setProperty(pEndTime, getProperty(pStartTime));
                setProperty(pStartTime, getPropertyAsInteger(pEndTime) - i);
            }
        }
    }

    public void addCollector(HashMap hashmap, Monitor monitor, StringProperty stringproperty, boolean flag) {
        String s = monitor.getFullID() + " " + stringproperty.getName();
        boolean flag1 = getProperty(pWarningNotIncluded).length() > 0;
        boolean flag2 = getProperty(pFailureNotIncluded).length() > 0;
        if (hashmap.get(s) == null) {
            SampleCollector samplecollector = new SampleCollector(monitor, stringproperty, flag, flag1, flag2);
            hashmap.put(s, samplecollector);
            collectors.add(samplecollector);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @throws SiteViewException
     */
    public void initializeSampleCollectors() throws SiteViewException {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        boolean flag = false;
        boolean flag1 = false;
        MonitorGroup monitorgroup = null;
        Monitor monitor = null;
        HashMap hashmap = new HashMap();
        Array array = new Array();
        boolean flag2 = !getPropertyAsBoolean(pBasicPropertiesOnly);
        Enumeration enumeration = getMultipleValues(pTargets);
        boolean flag3 = false;
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            if (s.equals("_master")) {
                flag3 = true;
                break;
            }
        }

        if (flag3) {
            Array array2 = new Array();
            Array array3 = siteviewgroup.getElementsOfClass("COM.dragonflow.SiteView.MonitorGroup", false);
            String s2;
            for (Enumeration enumeration1 = array3.elements(); enumeration1.hasMoreElements(); array2.add(s2)) {
                MonitorGroup monitorgroup1 = (MonitorGroup) enumeration1.nextElement();
                s2 = monitorgroup1.getFullID();
            }

            enumeration = array2.elements();
        } else {
            enumeration = getMultipleValues(pTargets);
        }
        java.util.HashMap hashmap1 = new java.util.HashMap();
        boolean flag4 = getProperty(pReportType).indexOf("lineGraph") >= 0;
        while (array.size() > 0) {
            if (array.size() > 0) {
                Array array1 = array;
                enumeration = array1.elements();
                array = new Array();
            }
            while (enumeration.hasMoreElements()) {
                String s1 = (String) enumeration.nextElement();
                String as[] = TextUtils.split(s1);
                Object obj = null;
                Object obj1 = null;
                if (as.length == 1) {
                    MonitorGroup monitorgroup2 = (MonitorGroup) siteviewgroup.getElement(I18N.toDefaultEncoding(as[0]));
                    if (monitorgroup2 != null) {
                        if (monitorgroup == null) {
                            monitorgroup = monitorgroup2;
                        } else if (monitorgroup2 != monitorgroup) {
                            flag = true;
                        }
                        String s4 = monitorgroup2.getProperty(MonitorGroup.pAccountName);
                        if (monitorgroup2.getSetting("_logInGroup", false).length() > 0) {
                            logInAccount = monitorgroup2.getProperty(pID);
                        } else if (s4.length() > 0) {
                            logInAccount = s4;
                        }
                        Enumeration enumeration2 = monitorgroup2.getMonitors();
                        while (enumeration2.hasMoreElements()) {
                            Monitor monitor1 = (Monitor) enumeration2.nextElement();
                            if (monitor1 instanceof SubGroup) {
                                array.add(monitor1.getProperty(SubGroup.pGroup));
                            } else {
                                Enumeration enumeration3;
                                if (!"".equals(getProperty(pRealTime)) && (monitor1 instanceof RealTimeReportingMonitor)) {
                                    enumeration3 = ((RealTimeReportingMonitor) monitor1).getRealTimeProperties();
                                } else {
                                    enumeration3 = monitor1.getStatePropertyObjects(flag2);
                                }
                                if (enumeration3.hasMoreElements()) {
                                    while (enumeration3.hasMoreElements()) {
                                        StringProperty stringproperty = (StringProperty) enumeration3.nextElement();
                                        addCollector(hashmap, monitor1, stringproperty, flag4);
                                    }
                                } else {
                                    LogManager.log("Error", "Monitor has no default property: " + monitor1);
                                }
                            }
                        }
                    } else {
                        String as1[] = { as[0] };
                        hashmap1.put(as[0], SiteViewResource.getFormattedString((new Long(SiteViewErrorCodes.ERR_PARAM_API_REPORT_MISSING_GROUP)).toString(), as1));
                    }
                } else if (as.length > 1) {
                    String s5 = I18N.toDefaultEncoding(as[1]);
                    String s3 = s5 + SiteViewGroup.ID_SEPARATOR + as[0];
                    MonitorGroup monitorgroup3 = (MonitorGroup) siteviewgroup.getElement(s5);
                    if (monitorgroup3 != null) {
                        Monitor monitor2 = (Monitor) siteviewgroup.getElement(s3);
                        if (monitor2 != null) {
                            if (monitor == null) {
                                monitor = monitor2;
                            } else if (monitor2 != monitor) {
                                flag1 = true;
                            }
                            String s6 = monitorgroup3.getProperty(MonitorGroup.pAccountName);
                            if (monitorgroup3.getSetting("_logInGroup", false).length() > 0) {
                                logInAccount = monitorgroup3.getProperty(pID);
                            } else if (s6.length() > 0) {
                                logInAccount = s6;
                            }
                            if (as.length == 2) {
                                Enumeration enumeration4;
                                if (!"".equals(getProperty(pRealTime)) && (monitor2 instanceof RealTimeReportingMonitor)) {
                                    enumeration4 = ((RealTimeReportingMonitor) monitor2).getRealTimeProperties();
                                } else {
                                    enumeration4 = monitor2.getStatePropertyObjects(flag2);
                                }
                                if (enumeration4.hasMoreElements()) {
                                    while (enumeration4.hasMoreElements()) {
                                        StringProperty stringproperty1 = (StringProperty) enumeration4.nextElement();
                                        addCollector(hashmap, monitor2, stringproperty1, flag4);
                                    }
                                }
                            } else {
                                StringProperty stringproperty2 = monitor2.getPropertyObject(as[2]);
                                addCollector(hashmap, monitor2, stringproperty2, flag4);
                            }
                        } else {
                            String as2[] = { s5 };
                            hashmap1.put(s5, SiteViewResource.getFormattedString((new Long(SiteViewErrorCodes.ERR_PARAM_API_REPORT_TAB_MISSING_MONITOR)).toString(), as2));
                        }
                    } else {
                        String as3[] = { s5 };
                        hashmap1.put(s5, SiteViewResource.getFormattedString((new Long(SiteViewErrorCodes.ERR_PARAM_API_REPORT_MISSING_GROUP)).toString(), as3));
                    }
                } else {
                    LogManager.log("Error", "History target specifier incorrect: " + s1);
                }
            }
        }

        if (!flag1 && !flag) {
            if (monitorgroup != null && monitor == null) {
                setProperty(pHistoryTarget, monitorgroup.getProperty(Monitor.pName) + " Group");
            } else if (monitorgroup == null && monitor != null) {
                setProperty(pHistoryTarget, monitor.getProperty(Monitor.pName));
                setProperty(pMultipleMonitors, "false");
            }
        }
        if (hashmap1.size() > 0) {
            if (collectors.size() == 0) {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_OP_SS_REPORT_MUTLTIPLE_ERRORS, hashmap1);
            }
            LogManager.log("Error", "Warning for report " + getProperty(pQueryID) + ": " + hashmap1.toString());
        }
    }

    static String secondsToString(int i) {
        String s = "minutes";
        float f = 0.0F;
        if (i >= 0x2a300) {
            s = "days";
            f = (float) i / 86400F;
        } else if (i >= 7200) {
            s = "hours";
            f = (float) i / 3600F;
        } else {
            f = (float) i / 60F;
        }
        return TextUtils.floatToString(f, 1) + " " + s;
    }

    private static void fileOut(PrintWriter printwriter, PrintWriter printwriter1, String s) {
        printwriter.print(s);
        printwriter1.print(s);
    }

    public static void generateIndexPage(String s, String s1, String s2) {
        generateIndexPage(null, s, s1, true, s2);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param historyreport
     * @param s
     * @param s1
     * @param flag
     * @param s2
     */
    public static void generateIndexPage(HistoryReport historyreport, String s, String s1, boolean flag, String s2) {
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (s.length() == 0) {
            s = s2.length() != 0 ? "administrator" : "user";
        }
        HTTPRequest httprequest = new HTTPRequest();
        httprequest.setUser(s);
        String s3 = "Reports-" + s1;
        File file = new File(Platform.getDirectoryPath("htdocs", s) + File.separator + s3);
        File file1 = new File(Platform.getDirectoryPath("userhtml", s) + File.separator + s3);
        File file2 = file;
        if (s.equals("user")) {
            if (!file.exists()) {
                boolean flag1 = file.mkdirs();
                if (!flag1) {
                    LogManager.log("Error", "Could not create report directory: " + file.getAbsolutePath());
                    return;
                }
                Platform.chmod(file, "rwx");
            }
            file2 = file1;
        }

        if (!file2.exists()) {
            boolean flag2 = file2.mkdirs();
            if (!flag2) {
                LogManager.log("Error", "Could not create report directory: " + file2.getAbsolutePath());
                return;
            }
            Platform.chmod(file2, "rwx");
        }

        File file3 = new File(file, "latest.html");
        if (!flag && historyreport != null) {
            String s4 = "<meta http-equiv=\"Expires\" content=\"0\">\n<meta http-equiv=\"Pragma\" content=\"no-cache\">\n";
            String as[][] = { { "<TITLE>", "<TITLE>Latest " }, { "</HEAD>", s4 + " </HEAD>" }, { "<CENTER><H2>", "<CENTER><H2>Latest " } };
            FileUtils.copyFile(historyreport.getProperty(pFilePath) + ".html", file3.getAbsolutePath(), as);
            if ((new File(historyreport.getProperty(pFilePath) + ".txt")).exists()) {
                File file4 = new File(file, "latest.txt");
                FileUtils.copyFile(historyreport.getProperty(pFilePath) + ".txt", file4.getAbsolutePath());
            }
            if ((new File(historyreport.getProperty(pFilePath) + ".xml")).exists()) {
                File file5 = new File(file, "latest.xml");
                FileUtils.copyFile(historyreport.getProperty(pFilePath) + ".xml", file5.getAbsolutePath());
            }
        }
        String s5 = "Report-";
        if (s.equals("user")) {
            String as1[][] = { { "/htdocs/Report", "/userhtml/Report" } };
            String as3[] = file.list();
            String as4[] = file1.list();
            if (as4 == null) {
                as4 = new String[0];
            }
            HashMap hashmap1 = new HashMap();
            for (int k = 0; k < as4.length; k ++) {
                if (as4[k].startsWith(s5)) {
                    hashmap1.put(as4[k], "yes");
                }
            }

            for (int l = 0; l < as3.length; l ++) {
                if (as3[l].startsWith(s5) && hashmap1.get(as3[l]) == null) {
                    File file8 = new File(file1, as3[l]);
                    String as5[][] = (String[][]) null;
                    if (as3[l].endsWith(".html")) {
                        as5 = as1;
                    }
                    FileUtils.copyFile(new File(file, as3[l]), file8, as5);
                    Platform.chmod(file8, "rw");
                } else {
                    hashmap1.remove(as3[l]);
                }
            }

            FileUtils.copyFile(new File(Platform.getRoot() + File.separator + "htdocs" + File.separator + "artwork" + File.separator + logoFile), new File(file1, logoFile));
            Enumeration enumeration = hashmap1.keys();
            while (enumeration.hasMoreElements()) {
                File file9 = new File(file1, (String) enumeration.nextElement());
                boolean flag4 = file9.delete();
                if (!flag4) {
                    LogManager.log("Error", "Could not remove old report file: " + file9.getAbsolutePath());
                }
            }

            File file10 = new File(file1, "latest.html");
            if (!file10.exists() || file10.lastModified() != file3.lastModified()) {
                FileUtils.copyFile(file3, file10, as1);
                Platform.chmod(file10, "rw");
            }
        }
        String as2[] = file2.list();
        Array array = new Array();
        for (int i = 0; i < as2.length; i ++) {
            if (as2[i].startsWith(s5) && as2[i].endsWith(".html")) {
                Date date = TextUtils.fileNameToDate(as2[i].substring(s5.length()));
                array.add(date);
            }
        }

        Sorting.sort(array, new GreaterDate());
        Reversing.reverse(array);
        if (!s.equals("user")) {
            int j = 10;
            if (hashmap != null) {
                String s6 = TextUtils.getValue(hashmap, "_maximumReports");
                if (s6 != null && s6.length() > 0) {
                    j = Math.max(3, StringProperty.toInteger(s6));
                }
            }

            while (array.size() > j) {
                Date date1 = (Date) array.popFront();
                String s7 = s5 + TextUtils.dateToFileName(date1);
                File file11 = new File(file, s7 + ".html");
                boolean flag5 = file11.delete();
                if (!flag5) {
                    LogManager.log("Error", "Could not remove old report file: " + file11.getAbsolutePath());
                }
                File file12 = new File(file, s7 + ".txt");
                file12.delete();
                File file13 = new File(file, s7 + ".eml");
                file13.delete();
                File file14 = new File(file, s7 + ".xml");
                file14.delete();
                String s9 = s5 + TextUtils.dateToFileName(date1) + "-";
                String as6[] = file.list();
                if (as6 != null) {
                    for (int i1 = 0; i1 < as6.length; i1 ++) {
                        if (as6[i1].startsWith(s9)) {
                            File file15 = new File(file, as6[i1]);
                            boolean flag6 = file15.delete();
                            if (!flag6) {
                                LogManager.log("Error", "Could not remove old image file: " + file15.getAbsolutePath());
                            }
                        }

                    }
                }
            }
        }
        boolean flag3 = historyreport == null ? false : historyreport.getProperty(pWarningNotIncluded).length() > 0;
        File file6 = new File(file2, "index.html");
        File file7 = new File(file2, "Findex.html");
        FileOutputStream fileoutputstream = null;
        PrintWriter printwriter = null;
        FileOutputStream fileoutputstream1 = null;
        PrintWriter printwriter1 = null;
        String s8 = historyreport == null ? "BORDER=1 " : historyreport.reportTableHTML;
        String s10 = historyreport == null ? "BORDER=1 " : historyreport.reportTableHeaderHTML;
        String s11 = historyreport == null ? "BORDER=1 " : historyreport.reportTableDataHTML;
        try {
            fileoutputstream = new FileOutputStream(file6);
            printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            fileoutputstream1 = new FileOutputStream(file7);
            printwriter1 = FileUtils.MakeOutputWriter(fileoutputstream1);
            String s12 = "Management Reports";
            String s13 = "";
            if (historyreport != null) {
                s13 = historyreport.getProperty(pDescription);
                s12 = historyreport.getProperty(pTitle);
            }
            fileOut(printwriter, printwriter1, "<HTML>");
            CGI.printBodyHeader(printwriter, s12, "");
            CGI.printBodyHeader(printwriter1, s12, "");
            if (s.equals("user") || TextUtils.getValue(hashmap, "_userDisableReportIndexToolbar").length() == 0) {
                CGI.printButtonBar(printwriter, "HistRprt.htm", "", httprequest, MasterConfig.getMasterConfig());
            }
            String s14 = TextUtils.getValue(hashmap, httprequest.actionAllowed("_reportGenerate") ? "_reportIndexHTML" : "_userReportIndexHTML");
            fileOut(printwriter, printwriter1, s14 + "<H2>" + s12 + "</H2><p>" + s13 + "<p>\n");
            if (array.size() <= 0) {
                fileOut(printwriter, printwriter1, "<CENTER><H4>No reports have been generated</H2></CENTER>\n");
            } else {
                fileOut(printwriter, printwriter1, "<A HREF=" + Platform.getURLPath(accountToDirectory(s), s) + "/" + s3 + "/latest.html><B>Most Recent Report</B></A>\n");
                if (!Platform.isStandardAccount(httprequest.getAccount())) {
                    String s15 = TextUtils.getValue(hashmap, "_nodeChanged");
                    if (s15 != null && s15.length() > 0) {
                        fileOut(printwriter, printwriter1, "<A HREF=" + s15 + ">" + "<BR><B>Interpreting your reports: SiteSeer Global PoP transition details</B></A>\n");
                    }
                }
                fileOut(printwriter, printwriter1, "<P><CENTER>\n<A NAME=uptimeSummary> </A>\n<TABLE WIDTH=\"100%\" " + s8 + "><CAPTION><B>Report Summary</B></CAPTION>\n");
                Enumeration enumeration1 = array.elements();
                String s16 = "";
                boolean flag7 = false;
                HistorySummaryCollector historysummarycollector = new HistorySummaryCollector(s, s1);
                while (enumeration1.hasMoreElements()) {
                    Date date2 = (Date) enumeration1.nextElement();
                    historysummarycollector.add(date2);
                    String s19 = historysummarycollector.getStartDate(date2);
                    if (!flag7 && s16.length() > 0 && s19.equals(s16)) {
                        flag7 = true;
                    }
                    s16 = s19;
                }
                fileOut(printwriter, printwriter1, "<TR " + s10 + "><TH>&nbsp;</TH>");
                String s20;
                Enumeration enumeration2 = historysummarycollector.getMonitorNames();
                while (enumeration2.hasMoreElements()) {
                    s20 = (String) enumeration2.nextElement();
                    fileOut(printwriter, printwriter1, "<TH COLSPAN=2>" + s20 + "</TH>");
                }

                fileOut(printwriter, printwriter1, "</TR>\n<TR " + s11 + "><TD><B>Information For</B></TD>");
                Enumeration enumeration3 = historysummarycollector.getMonitorNames();
                while (enumeration3.hasMoreElements()) {
                    enumeration3.nextElement();
                    fileOut(printwriter, printwriter1, "<TD ALIGN=RIGHT>avg</TD><TD ALIGN=RIGHT>peak</TD>");
                }

                fileOut(printwriter, printwriter1, "</TR>\n");
                ArrayIterator arrayiterator = array.end();
                arrayiterator.retreat();

                while (true) {
                    Date date3 = (Date) arrayiterator.get();
                    fileOut(printwriter, printwriter1, "<TR " + s11 + "><TD><A HREF=" + dateToReportURLBase(s, s1, date3) + ".html>");
                    if (flag7) {
                        fileOut(printwriter, printwriter1, historysummarycollector.getStartTime(date3) + " " + historysummarycollector.getStartDate(date3) + " - " + historysummarycollector.getEndTime(date3) + " "
                                + historysummarycollector.getEndDate(date3));
                    } else {
                        fileOut(printwriter, printwriter1, historysummarycollector.getStartDate(date3));
                    }
                    fileOut(printwriter, printwriter1, "</A>");
                    String s21 = dateToReportPathBase(s, s1, date3) + ".txt";
                    String s22 = dateToReportPathBase(s, s1, date3) + ".xml";
                    boolean flag8 = (new File(s21)).exists();
                    boolean flag9 = (new File(s22)).exists();
                    if (flag8 || flag9) {
                        String s23 = " (";
                        if (flag8) {
                            s23 = s23 + "<a href=" + dateToReportURLBase(s, s1, date3) + ".txt>text</a>";
                        }
                        if (flag9) {
                            if (flag8) {
                                s23 = s23 + " ";
                            }
                            s23 = s23 + "<a href=" + dateToReportURLBase(s, s1, date3) + ".xml>xml</a>";
                        }
                        s23 = s23 + ")";
                        fileOut(printwriter, printwriter1, s23);
                    }
                    fileOut(printwriter, printwriter1, "</TD>");
                    String s25;
                    String s27;
                    String s28;
                    Enumeration enumeration4 = historysummarycollector.getMonitorIDs();
                    while (enumeration4.hasMoreElements()) {
                        String s24 = (String) enumeration4.nextElement();
                        s25 = "#FFFFFF";
                        String s26 = historysummarycollector.getCategory(date3, s24);
                        if (s26.equals("error")) {
                            s25 = TextUtils.getValue(hashmap, "_reportTableErrorColor");
                        } else if (s26.equals("warning")) {
                            if (!flag3) {
                                s25 = TextUtils.getValue(hashmap, "_reportTableWarningColor");
                            } else {
                                s25 = TextUtils.getValue(hashmap, "_reportTableGoodColor");
                            }
                        }
                        if (s25.length() > 0) {
                            s25 = "BGCOLOR=\"" + s25 + "\"";
                        }
                        s27 = historysummarycollector.getAverage(date3, s24);
                        if (s27.length() == 0) {
                            s27 = "&nbsp;";
                        }
                        s28 = historysummarycollector.getMaximum(date3, s24);
                        if (s28.length() == 0) {
                            s28 = "&nbsp;";
                        }
                        fileOut(printwriter, printwriter1, "<TD ALIGN=RIGHT " + s25 + ">" + s27 + "</TD><TD ALIGN=RIGHT " + s25 + ">" + s28 + "</TD>");
                    }

                    fileOut(printwriter, printwriter1, "</TR>\n");
                    if (arrayiterator.atBegin()) {
                        break;
                    }
                    arrayiterator.retreat();
                }

                fileOut(printwriter, printwriter1, "</TABLE></CENTER>\n");
            }

            for (int j1 = 0; j1 < 2; j1 ++) {
                String s17 = "";
                if (j1 == 0) {
                    s17 = "<input type=hidden name=mainLink value=true>";
                    s17 = s17 + "<input type=hidden name=aboutLink value=true>";
                }
                if (!httprequest.actionAllowed("_reportGenerate")) {
                    continue;
                }
                String s18 = "<P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=page value=adhocReport><input type=hidden name=queryID value=" + s1 + ">" + "<input type=hidden name=htmlFile value=yes>" + s17
                        + "<input type=hidden name=account value=" + httprequest.getAccount() + ">" + "<input type=submit value=Generate></FORM>" + " Management Report Now"
                        + " - this will immediately generate and save this report, using the most current data\n" + "(<B>Note: </B>This may take a few moments, depending on the speed of the SiteView machine, "
                        + "the number of monitors and the time period of the report)\n";
                if (j1 == 0) {
                    printwriter.println(s18);
                } else {
                    printwriter1.println(s18);
                }
            }

            CGI.printFooter(printwriter, s);
            CGI.printFooter(printwriter1, s);
            fileOut(printwriter, printwriter1, "</BODY></HTML>\n");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error generating index page " + file6 + ": " + ioexception.getMessage());
        } finally {
            if (printwriter != null) {
                try {
                    printwriter.close();
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }
            if (fileoutputstream != null) {
                try {
                    fileoutputstream.close();
                } catch (IOException ioexception1) {
                }
            }
            if (file6.exists()) {
                Platform.chmod(file6, "rw");
            }
            if (printwriter1 != null) {
                try {
                    printwriter1.close();
                } catch (Exception exception2) {
                    exception2.printStackTrace();
                }
            }
            if (fileoutputstream1 != null) {
                try {
                    fileoutputstream1.close();
                } catch (IOException ioexception2) {
                }
            }
            if (file7.exists()) {
                Platform.chmod(file7, "rw");
            }
        }
    }

    public static void cleanAllTemporaryReportFiles() {
        File file = new File(Platform.getRoot());
        int i = cleanTemporaryReportFiles(file);
        File file1 = new File(file, "accounts");
        if (file1.exists()) {
            String as[] = file1.list();
            if (as != null) {
                for (int j = 0; j < as.length; j ++) {
                    File file2 = new File(file1, as[j]);
                    if (file2.isDirectory()) {
                        cleanTemporaryReportFiles(file2);
                    }
                }

            }
        }
        if (i > 0) {
            LogManager.log("RunMonitor", "removed " + i + " temporary report files");
        }
    }

    public static int cleanTemporaryReportFiles(File file) {
        int i = 0;
        File file1 = new File(file, "htdocs");
        File file2 = new File(file1, "Reports-0");
        if (file2.exists() && file2.isDirectory()) {
            String as[] = file2.list();
            for (int j = 0; as != null && j < as.length; j ++) {
                if (!isFlipperQuickReport(as[j])) {
                    FileUtils.delete(new File(file2, as[j]));
                    i ++;
                }
            }

        }
        File file3 = new File(file, "userhtml");
        file2 = new File(file3, "Reports-0");
        if (file2.exists() && file2.isDirectory()) {
            String as1[] = file2.list();
            for (int k = 0; as1 != null && k < as1.length; k ++) {
                if (!isFlipperQuickReport(as1[k])) {
                    FileUtils.delete(new File(file2, as1[k]));
                    i ++;
                }
            }

        }
        return i;
    }

    private static boolean isFlipperQuickReport(String s) {
        Array array = reportPage.getReportFrames(null);
        for (int i = 0; i < array.size(); i ++) {
            HashMap hashmap = (HashMap) array.at(i);
            if (hashmap.get("isQuick") == null || ((String) hashmap.get("isQuick")).length() <= 0) {
                continue;
            }
            String s1 = (String) hashmap.get("path");
            s1 = s1.substring(s1.indexOf("t-") + 2);
            if (s.indexOf(s1) >= 0) {
                return true;
            }
        }

        return false;
    }

    public static String accountToDirectory(String s) {
        if (s.equals("user")) {
            return "userhtml";
        } else {
            return "htdocs";
        }
    }

    static String dateToReportURLBase(String s, String s1, Date date) {
        return Platform.getURLPath(accountToDirectory(s), s) + "/Reports-" + s1 + "/Report-" + TextUtils.dateToFileName(date);
    }

    static String dateToReportPathBase(String s, String s1, Date date) {
        return Platform.getDirectoryPath(accountToDirectory(s), s) + File.separator + "Reports-" + s1 + File.separator + "Report-" + TextUtils.dateToFileName(date);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringbuffer
     * @param s
     * @param array
     * @param s1
     * @return
     * @throws IOException
     */
    String addAttachments(StringBuffer stringbuffer, String s, Array array, String s1) throws IOException {
        String s2 = "----SiteViewRelated" + Platform.timeMillis();
        s = s + "[attachments]" + s2;
        RandomAccessFile randomaccessfile = null;
        try {
            randomaccessfile = new RandomAccessFile(s1, "rw");
            randomaccessfile.seek(randomaccessfile.length());
            stringbuffer.setLength(0);
            randomaccessfile.writeBytes("This is a multi-part message in MIME format.\n\n");
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s3 = (String) enumeration.nextElement();
                if ((new File(s3)).exists()) {
                    String s4 = s3;
                    int i = s4.lastIndexOf(File.separator);
                    if (i != -1) {
                        s4 = s4.substring(i + 1);
                    }
                    randomaccessfile.writeBytes("--" + s2 + "\n");
                    if (s3.endsWith(".html")) {
                        randomaccessfile.writeBytes("Content-Type: text/html;\n     charset=us-ascii\nContent-Transfer-Encoding: 7bit\nContent-Location: " + s4 + "\n" + "\n" + "\n" + FileUtils.readFile(s3) + "\n");
                    } else {
                        String s5 = "application/octet-stream";
                        int j = s3.lastIndexOf(".");
                        if (j != -1) {
                            s5 = "image/" + s3.substring(j + 1);
                        }
                        if (s5.equals("image/jpg")) {
                            s5 = "image/jpeg";
                        }
                        randomaccessfile.writeBytes("Content-Type: " + s5 + ";\n" + "     name=\"" + s4 + "\"\n" + "Content-Transfer-Encoding: base64\n" + "Content-Location: " + s4 + "\n" + "\n");
                        appendBinaryAttachment(randomaccessfile, s3);
                        randomaccessfile.writeBytes("\n");
                    }
                }
            }

            randomaccessfile.writeBytes("--" + s2 + "--\n");
            if (randomaccessfile != null) {
                randomaccessfile.close();
            }
        } catch (IOException e) {
            if (randomaccessfile != null) {
                randomaccessfile.close();
            }
        } finally {
            if (randomaccessfile != null) {
                randomaccessfile.close();
            }
        }
        return s;
    }

    public static void appendBinaryAttachment(RandomAccessFile randomaccessfile, String s) throws IOException {
        StringBuffer stringbuffer = FileUtils.readFile(s);
        Base64Encoder base64encoder = new Base64Encoder(stringbuffer.toString());
        stringbuffer = null;
        String s1 = base64encoder.processString();
        randomaccessfile.writeBytes(s1);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param monitor
     * @param array
     */
    private void getThresholdList(Monitor monitor, Array array) {
        Array array1 = new Array();
        Array array2 = new Array();
        Array array3 = new Array();
        Enumeration enumeration = monitor.getClassifiers();
        String s = "SetProperty category error";
        String s1 = "SetProperty category warning";
        String s2 = "SetProperty category good";
        String s3 = "none";
        String s4 = "none";
        String s5 = "none";
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        while (enumeration.hasMoreElements()) {
            Rule rule = (Rule) enumeration.nextElement();
            if (s.equals(rule.getProperty(Rule.pAction)) || s1.equals(rule.getProperty(Rule.pAction)) || s2.equals(rule.getProperty(Rule.pAction))) {
                if (rule.getOwner() != monitor) {
                    if (s.equals(rule.getProperty(Rule.pAction)) && !flag) {
                        if (rule.getProperty(Rule.pDisplayThis).length() > 0) {
                            s3 = rule.getProperty(Rule.pDisplayThis);
                        } else {
                            s3 = rule.getProperty(Rule.pExpression);
                        }
                        flag = true;
                    } else if (s1.equals(rule.getProperty(Rule.pAction)) && !flag1) {
                        if (rule.getProperty(Rule.pDisplayThis).length() > 0) {
                            s4 = rule.getProperty(Rule.pDisplayThis);
                        } else {
                            s4 = rule.getProperty(Rule.pExpression);
                        }
                        flag1 = true;
                    } else if (s2.equals(rule.getProperty(Rule.pAction)) && !flag2) {
                        if (rule.getProperty(Rule.pDisplayThis).length() > 0) {
                            s5 = rule.getProperty(Rule.pDisplayThis);
                        } else {
                            s5 = rule.getProperty(Rule.pExpression);
                        }
                        flag2 = true;
                    }
                } else if (s.equals(rule.getProperty(Rule.pAction))) {
                    array1.add(rule.getProperty(Rule.pExpression));
                } else if (s1.equals(rule.getProperty(Rule.pAction))) {
                    array2.add(rule.getProperty(Rule.pExpression));
                } else if (s2.equals(rule.getProperty(Rule.pAction))) {
                    array3.add(rule.getProperty(Rule.pExpression));
                }
            }
        }

        if (array1.isEmpty()) {
            array1.add(s3);
        }
        if (array2.isEmpty()) {
            array2.add(s4);
        }
        if (array3.isEmpty()) {
            array3.add(s5);
        }
        int i = array1.size();
        int j = array2.size();
        int k = array3.size();
        int l = i <= j ? j : i;
        l = l <= k ? k : l;
        Enumeration enumeration1 = array1.elements();
        Enumeration enumeration2 = array2.elements();
        Enumeration enumeration3 = array3.elements();
        for (int i1 = 0; i1 < l; i1 ++) {
            HashMap hashmap = new HashMap();
            if (enumeration1.hasMoreElements()) {
                hashmap.add("error", enumeration1.nextElement());
            }
            if (enumeration2.hasMoreElements()) {
                hashmap.add("warning", enumeration2.nextElement());
            }
            if (enumeration3.hasMoreElements()) {
                hashmap.add("good", enumeration3.nextElement());
            }
            array.pushBack(hashmap);
        }

    }

    public static void main(String args[]) throws IOException {
        try {
            String s = "administrator";
            if (args.length > 2 && args[1].equals("-a")) {
                s = args[2];
            } else if (args.length > 1) {
                alternateLogFile = args[1];
            }
            PrintWriter printwriter = FileUtils.MakeOutputWriter(System.out);
            generateReportFromQueryID(args[0], printwriter, s, null);
            printwriter.flush();
        } catch (Exception exception) {
            System.err.println("Error creating report: " + exception.getMessage());
        }
        DebugWatcher.main(null);
    }

    static {
        $assertionsDisabled = !(COM.dragonflow.SiteView.HistoryReport.class).desiredAssertionStatus();
        pTargets = new StringProperty("monitors");
        pTargets.isParameter = true;
        pWindow = new NumericProperty("window", "0", "seconds");
        pWindow.isParameter = true;
        pPrecision = new NumericProperty("precision", "0", "seconds");
        pPrecision.isParameter = true;
        pFormat = new StringProperty("format");
        pFormat.isParameter = true;
        pVMax = new NumericProperty("vmax", "0");
        pVMax.isParameter = true;
        pStartDay = new NumericProperty("startDay", "today");
        pStartDay.isParameter = true;
        pStartHour = new NumericProperty("startHour", "now");
        pStartHour.isParameter = true;
        pDirection = new StringProperty("relative", "back");
        pDirection.isParameter = true;
        pEmail = new StringProperty("email", "");
        pEmail.isParameter = true;
        pIsAdhoc = new StringProperty("isadhoc", "");
        pIsAdhoc.isParameter = true;
        pAttachReport = new StringProperty("attachReport", "");
        pAttachReport.isParameter = true;
        pEmailData = new StringProperty("emailData", "");
        pEmailData.isParameter = true;
        pSpreadsheet = new StringProperty("tabfile", "");
        pSpreadsheet.isParameter = true;
        pXML = new StringProperty("xmlfile", "");
        pXML.isParameter = true;
        pXMLEmailData = new StringProperty("xmlEmailData", "");
        pXMLEmailData.isParameter = true;
        pSpreadsheetDump = new StringProperty("tabfiledump", "");
        pSpreadsheetDump.isParameter = true;
        pReportType = new StringProperty("reportType", "");
        pReportType.isParameter = true;
        pCluster = new StringProperty("cluster", "");
        pCluster.isParameter = true;
        pDisabled = new StringProperty("disabled", "");
        pDisabled.isParameter = true;
        pWarningNotIncluded = new StringProperty("_warningNotIncluded", "");
        pWarningNotIncluded.isParameter = true;
        pUpTimeIncludeWarning = new StringProperty("_upTimeIncludeWarning", "");
        pUpTimeIncludeWarning.isParameter = true;
        pFailureNotIncluded = new StringProperty("_failureNotIncluded", "");
        pFailureNotIncluded.isParameter = true;
        pBasicPropertiesOnly = new StringProperty("basic", "");
        pBasicPropertiesOnly.isParameter = true;
        pQueryID = new StringProperty("id", "0");
        pQueryID.isParameter = true;
        pQuery = new StringProperty("query", "");
        pQuery.isParameter = true;
        pSchedule = new StringProperty("schedule", "");
        pSchedule.isParameter = true;
        pMailTemplate = new StringProperty("mailTemplate", "HistoryMail");
        pMailTemplate.isParameter = true;
        pTitle = new StringProperty("title", "");
        pTitle.isParameter = true;
        pDescription = new StringProperty("description", "");
        pDescription.isParameter = true;
        pTimeOffset = new NumericProperty("timeOffset", "0", "seconds");
        pTimeOffset.isParameter = true;
        pShowAlertsOnly = new StringProperty("showAlertsOnly");
        pShowAlertsOnly.isParameter = true;
        pStatusFilter = new StringProperty("statusFilter", "");
        pStatusFilter.isParameter = true;
        pScheduleFilter = new StringProperty("schedFilter", "");
        pScheduleFilter.isParameter = true;
        pBestCaseCalc = new StringProperty("bestCaseCalc", "");
        pBestCaseCalc.isParameter = true;
        pRealTime = new StringProperty("realTime", "");
        pRealTime.isParameter = true;
        pRealTimeRefresh = new StringProperty("realTimeRefresh", "");
        pRealTimeRefresh.isParameter = true;
        pStartTime = new NumericProperty("startTime");
        pEndTime = new NumericProperty("endTime");
        pUsingDefaultTimeParameters = new StringProperty("usingDefaultTimeParameters", "false");
        pDefaultTitle = new StringProperty("defaultTitle", "Management Report");
        pMultipleMonitors = new StringProperty("multipleMonitors", "true");
        pHistoryTarget = new StringProperty("historyTarget", "Multiple Monitors");
        pReportPeriod = new StringProperty("reportPeriod", "");
        pThresholdSummaryText = new StringProperty("thresholdSummary", "");
        pSummaryText = new StringProperty("summary", "");
        pErrorTimeSummaryText = new StringProperty("errorTimeSummary", "");
        pBasicAlertSummaryText = new StringProperty("basicAlertSummary", "");
        pDetailAlertSummaryText = new StringProperty("detailAlertSummary", "");
        pVirtualPath = new StringProperty("reportURL", "");
        pTextReportVirtualPath = new StringProperty("textReportURL", "");
        pIndexVirtualPath = new StringProperty("reportIndexURL", "");
        pXMLReportVirtualPath = new StringProperty("xmlReportURL", "");
        pReadOnlyVirtualPath = new StringProperty("userReportURL", "");
        pReadOnlyTextReportVirtualPath = new StringProperty("userTextReportURL", "");
        pReadOnlyIndexVirtualPath = new StringProperty("userReportIndexURL", "");
        pReadOnlyXMLReportVirtualPath = new StringProperty("userXMLReportURL", "");
        pFilePath = new StringProperty("reportPath", "");
        pReportDirectory = new StringProperty("reportDirectory", "");
        pReportVirtual = new StringProperty("reportVirtual", "");
        pSiteSeerRegistration = new StringProperty("siteseerRegistration", "");
        pAccount = new StringProperty("account", "");
        pAccount.isParameter = true;
        StringProperty astringproperty[] = { pFailureNotIncluded, pWarningNotIncluded, pUpTimeIncludeWarning, pTargets, pWindow, pPrecision, pFormat, pVMax, pStartDay, pStartHour, pDirection, pEmail, pAttachReport, pEmailData, pDisabled,
                pBasicPropertiesOnly, pQueryID, pQuery, pSpreadsheet, pXML, pXMLEmailData, pSpreadsheetDump, pReportType, pCluster, pSchedule, pMailTemplate, pTimeOffset, pShowAlertsOnly, pStatusFilter, pScheduleFilter, pBestCaseCalc, pStartTime,
                pEndTime, pUsingDefaultTimeParameters, pIsAdhoc, pTitle, pDescription, pDefaultTitle, pMultipleMonitors, pHistoryTarget, pReportPeriod, pThresholdSummaryText, pSummaryText, pErrorTimeSummaryText, pBasicAlertSummaryText,
                pDetailAlertSummaryText, pVirtualPath, pTextReportVirtualPath, pIndexVirtualPath, pXMLReportVirtualPath, pReadOnlyVirtualPath, pReadOnlyTextReportVirtualPath, pReadOnlyIndexVirtualPath, pReadOnlyXMLReportVirtualPath, pFilePath,
                pReportDirectory, pReportVirtual, pAccount, pSiteSeerRegistration };
        addProperties("COM.dragonflow.SiteView.HistoryReport", astringproperty);
        graphicMap = new HashMap();
        graphicMap.add("error", "/SiteView/htdocs/artwork/red.gif");
        graphicMap.add("good", "/SiteView/htdocs/artwork/green.gif");
        graphicMap.add("warning", "/SiteView/htdocs/artwork/yellow.gif");
        graphicMap.add("nodata", "/SiteView/htdocs/artwork/empty.gif");
        graphicMap.add("disabled", "/SiteView/htdocs/artwork/blue.gif");
        altMap = new HashMap();
        altMap.add("error", "E");
        altMap.add("good", "O");
        altMap.add("warning", "W");
        altMap.add("nodata", "");
        String s = System.getProperty("HistoryReport.debug");
        if (s != null && s.length() > 0) {
            debug = true;
        }
    }
}
