/*
 * 
 * Created on 2005-2-6 16:01:28
 *
 * AtomicMonitor.java
 *
 * History:
 *
 */

/**
 * Comment for <code>AtomicMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
package COM.dragonflow.SiteView;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Log.Logger;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.managePage;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.FrequencyProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Properties.treeProperty;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteViewException.SiteViewAvailabilityException;
import COM.dragonflow.SiteViewException.SiteViewError;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.StandardAction.UpdateMonitor;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.ThreadPool;
import COM.dragonflow.itsm.data.JDBCForSQL;
import SiteViewMain.SiteViewSupport;

//import com.dragonflow.infra.xdr_utils.Variant;

// import jgl.Array;

public  class AtomicMonitor extends Monitor implements Runnable,
        IPropertyFilter, IErrorCodeProvider, IObjectWithUniqueId {
    public static StringProperty pFrequency;

    public static StringProperty pNotLogToTopaz;

    public static StringProperty pOnlyLogStatusChanges;

    public static StringProperty pOnlyLogThresholdMeas;

    public static StringProperty pOnlyLogMonitorData;

    public static StringProperty pErrorFrequency;

    public static StringProperty pClass;

    public static StringProperty pMaximumRunTime;

    public static StringProperty pMaxErrorCount;

    public static StringProperty pMonitorsInError;

    public static StringProperty pMonitorsInGroup;

    public static NumericProperty pSample;

    public static NumericProperty pMonitorDoneTime;

    public static treeProperty pDependsOn;

    public static ScalarProperty pDependsCondition;

    public static StringProperty pGetHostName;

    public static StringProperty pNoData;

    public static StringProperty pUniqueInternalId;

    public static StringProperty pCustomData;

    public static StringProperty pOperationalErrorCode;

    public static StringProperty pAvailabilityErrorCode;

    public static StringProperty pApplicationErrorCode;

    public static StringProperty pApplicationErrorMessage;

    public static StringProperty pArgsForFormattedError;

    public static final String RUNNING_STRING = "RUNNING";

    static boolean dependsOnRecursive = false;

    static Boolean alwaysReportStatus = null;

    public static String DEPENDS_ON;

    public static String DEPENDS_CONDITION;

    public static String DEPENDS_PREFIX = "disabled, depended upon monitor ";

    public static final String STATUS_OK_PROP_NAME = "status";

    public static final String STATUS_OK = "OK";

    public static final String STATUS_ok = "ok";

    public static final String STATUS_WARNING = "Warning";

    public static final String STATUS_ERROR = "Error";

    public static final String INTERNAL_ID_KEY = "_internalId";

    public static final int MONITOR_DEBUG_LEVEL_NONE = 0;

    public static final int MONITOR_DEBUG_LEVEL_ERROR = 1;

    public static final int MONITOR_DEBUG_LEVEL_WARNING = 2;

    public static final int MONITOR_DEBUG_LEVEL_INFO = 3;

    public static final int FILTER_VALUE_ALL = 0;

    public static final int FILTER_VALUE_BASIC = 1;

    public static final int FILTER_VALUE_ADVANCED = 2;

    public static ThreadPool monitorThreadsPool = new ThreadPool("Monitors",
            null);

    static int minMonitorInterval = 15;

    public static int monitorDebugLevel = 0;

    public static boolean alertDebug;

    public static String alertDebugId = null;

    static String defaultTopazHostName;

    public ThreadPool.SingleThread thread = null;

    boolean running = false;

    UpdateMonitor action;

    public String progressString = "";

    public static RollingAverage monitorStats = new RollingAverage(10, 60L);;

    long okFrequency;

    long errorFrequency;

    long currentFrequency;

    private int monitorSkips = 0;

    public static int maxMonitorSkips = 6;

    public static CounterLock monitorThrottleExclusive = new CounterLock(1);

    static boolean simulateMonitorRun = false;

    static int simulateMonitorRunDuration = 0;

    static String simulateMonitorRunExe = "";

    static boolean simulateMonitorRunSet = false;

    static boolean lastGroupCategoryEnabled = false;

    static int disabledTotal = 0;

    public int maxErrorCount = 0;

    public int monitorsInError = 0;

    public int monitorsInGroup = 0;

    public StringProperty getLocationProperty(StringProperty stringproperty,
            String string) {
        if (string.length() > 0) {
            StringProperty locationProperty = getPropertyObject(stringproperty
                    .getName()
                    + string);
            if (locationProperty != null)
                stringproperty = locationProperty;
        }
        return stringproperty;
    }

    public String verify(StringProperty stringproperty, String string,
            HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pFrequency) {
            String string_1_ = httprequest.getValue(stringproperty.getName()
                    + "Units");
            int i = FrequencyProperty.toSeconds(TextUtils.toInt(string),
                    string_1_);
            if (i < minMonitorInterval && i != 0)
                hashmap.put(stringproperty, i + "  was less than 15 seconds");
            return "" + i;
        }
        if (stringproperty == pErrorFrequency) {
            if (string.length() == 0)
                return string;
            String string_2_ = httprequest.getValue(stringproperty.getName()
                    + "Units");
            int i = FrequencyProperty.toSeconds(TextUtils.toInt(string),
                    string_2_);
            if (i < minMonitorInterval)
                hashmap.put(stringproperty, i + "  was less than 15 seconds");
            return "" + i;
        }
        return super.verify(stringproperty, string, httprequest, hashmap);
    }

    public void verifyAll(jgl.HashMap hashmap) {
        /* empty */
    }

    protected boolean update() throws SiteViewException {
        return false;
    }

    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        /* empty */
    }

    public boolean isDispatcher() {
        return false;
    }

    public boolean testUpdate() {
        progressString = "";
        boolean bool = false;
        try {
            bool = update();
        } catch (SiteViewException siteviewexception) {
            siteviewexception.printStackTrace();
        }
        runClassifiers(this);
        return bool;
    }

    void recalculateFrequency(String string) {
        currentFrequency = okFrequency;
        if (errorFrequency != 0L && !string.equals("good"))
            currentFrequency = errorFrequency;
    }

    protected void startMonitor() {
        super.startMonitor();
        if (action == null) {
            if (!Platform.isPortal()) {
                action = new UpdateMonitor(this);
                String disabledReason = whyDisabled();
                if (!disabledReason.equals(MonitorGroup.accountDisabled)) {
                    okFrequency = getPropertyAsLong(pFrequency) * 1000L;
                    errorFrequency = getPropertyAsLong(pErrorFrequency) * 1000L;
                    long lastUpdate = getPropertyAsLong(pLastUpdate);
                    boolean forceRefresh = getProperty(pForceRefresh).length() > 0;
                    if (lastUpdate > Platform.timeMillis())
                        lastUpdate = Platform.timeMillis();
                    recalculateFrequency(getProperty(pCategory));
                    long currentMillis = Platform.timeMillis();
                    long interval = lastUpdate + currentFrequency
                            - currentMillis;
                    if (SiteViewSupport.isStartingUp()
                            && (disabledReason.length() == 0 || forceRefresh)) {
                        if (lastUpdate <= 0L) {
                            SiteViewGroup siteviewgroup = SiteViewGroup
                                    .currentSiteView();
                            long monitorDelayBetweenRefresh = (long) ((getSettingAsLong(
                                    "_monitorDelayBetweenRefresh", 1000)) * siteviewgroup.monitorsStarted);
                            lastUpdate = currentMillis - currentFrequency
                                    + monitorDelayBetweenRefresh;
                            siteviewgroup.monitorsStarted++;
                        } else if (interval < 5000L) {
                            long randomDelay = (long) (2.0 + (Math.random() * (double) (getSettingAsLong(
                                    "_initialMonitorDelay", 60))));
                            lastUpdate = currentMillis - currentFrequency
                                    + randomDelay * 1000L;
                        }
                    }
                    String schedule = getSchedule();
                    if (schedule.startsWith("*") && !forceRefresh) {
                        SiteViewGroup.currentSiteView().monitorScheduler
                                .scheduleAbsolutePeriodicAction(action,
                                        schedule, lastUpdate);
                        if (!ScheduleProperty.isEnabled(schedule)
                                && !forceRefresh)
                            progressString = "Monitor will not run due to current schedule.\nTo run monitor push the \"Run Once\" link on the Detail Page\n\n";
                    } else if (currentFrequency != 0L)
                        SiteViewGroup.currentSiteView().monitorScheduler
                                .scheduleRepeatedPeriodicAction(action,
                                        currentFrequency, lastUpdate);
                }
            }
        }
    }

    protected void stopMonitor() {
        super.stopMonitor();
        stopThread();
        SiteViewGroup.currentSiteView().monitorScheduler.unschedule(action);
        action = null;
    }

    public boolean isRunning() {
        return running == true;
    }

    protected void stopThread() {
        if (thread != null) {
            thread.stopSingleThread();
            thread = null;
        }
        running = false;
    }

    public int getMonitorSkips() {
        return monitorSkips;
    }

    public static int releaseExclusive(AtomicMonitor atomicmonitor) {
        return monitorThrottleExclusive.release();
    }

    public boolean runUpdate(boolean bool) {
        boolean updated = false;
        if (running && !isDisabled() && !isSuspended()) {
            monitorSkips++;
            LogManager.log("Error", ("skipped #" + monitorSkips
                    + ", monitor still running: " + getFullID() + ":("
                    + getProperty(pName) + "), " + currentStatus));
            if (monitorSkips >= maxMonitorSkips
                    && !SiteViewSupport.shuttingDown) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                if (siteviewgroup.getProperty("_shutdownOnSkips").length() > 0)
//                        || MAManager.isAttached()) {
                {
                    LogManager
                            .log(
                                    "RunMonitor",
                                    (Platform.productName + " shutting down, exceeded maxMonitorSkips limit"));
                    LogManager
                            .log(
                                    "Error",
                                    (Platform.productName + " shutting down, exceeded maxMonitorSkips limit"));
                    //SiteViewSupport.ShutdownProcess();/*delete by dingbing.xu*/
					return false;
                } else {
                    setDisabledProperties("Disabled due to Skips > "
                            + maxMonitorSkips);
                    setProperty(pDisabledDescription,
                            "Disabled due to Skips > " + maxMonitorSkips);
                    setProperty(pDisabled, "on");
                    LogManager.log("RunMonitor", (Platform.productName
                            + " Disabling monitor: " + getProperty(pName)
                            + " exceeded maxMonitorSkips limit("
                            + maxMonitorSkips + ")"));
                    LogManager.log("Error", (Platform.productName
                            + " Disabling monitor: " + getProperty(pName)
                            + " exceeded maxMonitorSkips limit("
                            + maxMonitorSkips + ")"));
                    String string = (getOwner() != null ? getOwner()
                            .getProperty(pName) : "");
                    MailUtils
                            .sendEmailWarning(
                                    (Platform.productName
                                            + " Disabling monitor: "
                                            + getProperty(pName)
                                            + " exceeded maxMonitorSkips limit("
                                            + maxMonitorSkips + ")"),
                                    ("SiteView has detected a monitor that is either setup incorrectly or is having problems.\nYou can adjust the frequency of the monitor or set up dependencies so that the monitor doesn't continue to run.\n\nGroup: "
                                            + string
                                            + "\n"
                                            + "Monitor: "
                                            + getProperty(pName)
                                            + "\n"
                                            + "Status: "
                                            + getProperty(pStateString)
                                            + "\n\n" + "This is an automatic email from SiteView.\n"));
                }
            }
        } else {
            monitorSkips = 0;
            boolean forceRefresh = getProperty(pForceRefresh).length() > 0;
            String string = getProperty(pDisabledDescription);
            if (string.indexOf("Disabled due to Skips >") >= 0) {
                unsetProperty(pDisabledDescription);
                unsetProperty(pDisabled);
                LogManager.log("RunMonitor",
                        (Platform.productName + " Reenabling monitor: "
                                + getProperty(pName) + " No longer running"));
                LogManager.log("Error",
                        (Platform.productName + " Reenabling monitor: "
                                + getProperty(pName) + " No longer running"));
            }
            if (isSuspended())
                progressString = "Monitor is suspended";
            else if (isDisabled() && !forceRefresh) {
                setDisabledProperties(whyDisabled());
                progressString = whyDisabled() + "\n";
            } else if (!getDependsOnSignal(new ArrayList(), false))
                progressString = "Depends On Monitor Disabled\n";
            else {
                if (!ScheduleProperty.isEnabled(this.getSchedule())
                        && !forceRefresh)
                    progressString = "Monitor will not run due to current schedule.\nTo run monitor push the \"Run Once\" link on the Detail Page\n\n";
                else {
                    updated = true;
                    running = true;
                    if (partOfGlobalDepends)
                        bool = true;
                    if (runExclusively())
                        monitorThrottleExclusive.get();
                    MonitorQueue.add(this, bool);
                }
            }
        }
        return updated;
    }

    public boolean runOwnRules() {
        if (alertDebug)
            LogManager.log("RunMonitor", ("alertDebug: " + alertDebugId
                    + " Running own rules if not blank: '"
                    + getClassPropertyString("runOwnRules") + "'"));
        return getClassPropertyString("runOwnRules").length() > 0;
    }

    private boolean reportStatus() {
        if (alwaysReportStatus == null) {
            String string = TextUtils.getValue(MasterConfig.getMasterConfig(),
                    "_alwaysReportStatus").trim();
            if (string.equals("false"))
                alwaysReportStatus = new Boolean(false);
            else
                alwaysReportStatus = new Boolean(true);
        }
        return alwaysReportStatus.booleanValue();
    }

    public void monitorUpdate() {
        try {
            setProperty(pLastCategory, getProperty(pCategory));
            unsetProperty(pNoData);
            setProperty(pOperationalErrorCode, pOperationalErrorCode
                    .getDefault());
            setProperty(pAvailabilityErrorCode, pAvailabilityErrorCode
                    .getDefault());
            setProperty(pApplicationErrorCode, pApplicationErrorCode
                    .getDefault());
            setProperty(pApplicationErrorMessage, pApplicationErrorMessage
                    .getDefault());
            if (!simulateMonitorRunSet) {
                simulateMonitorRunSet = true;
                jgl.HashMap hashmap = MasterConfig.getMasterConfig();
                simulateMonitorRun = TextUtils.getValue(hashmap,
                        "_simulateMonitorRun").length() > 0;
                if (simulateMonitorRun) {
                    simulateMonitorRunDuration = TextUtils.toInt(TextUtils
                            .getValue(hashmap, "_simulateMonitorRunDuration"));
                    simulateMonitorRunExe = TextUtils.getValue(hashmap,
                            "_simulateMonitorRunExe");
                }
            }
            if (simulateMonitorRun)
                SimulatedRun.simulateRun(this, simulateMonitorRunDuration,
                        simulateMonitorRunExe);
            else {
                try {
                    update();
                } catch (SiteViewAvailabilityException siteviewavailabilityexception) {
                    setProperty(pAvailabilityErrorCode,
                            siteviewavailabilityexception.getErrorCode());
                    setProperty(pApplicationErrorCode,
                            siteviewavailabilityexception
                                    .getApplicationErrorCode());
                    setProperty(pApplicationErrorMessage,
                            siteviewavailabilityexception
                                    .getApplicationErrorMessage());
                    try {
                        setPropertyWithObject(pArgsForFormattedError,
                                siteviewavailabilityexception
                                        .getFormattedErrorArgs());
                    } catch (IOException ioexception) {
                        LogManager.log("Error",
                                "Unable to save formatted args. ");
                    }
                } catch (SiteViewOperationalException siteviewoperationalexception) {
                    setProperty(pOperationalErrorCode,
                            siteviewoperationalexception.getErrorCode());
                    setProperty(pApplicationErrorCode,
                            siteviewoperationalexception
                                    .getApplicationErrorCode());
                    setProperty(pApplicationErrorMessage,
                            siteviewoperationalexception
                                    .getApplicationErrorMessage());
                    try {
                        setPropertyWithObject(pArgsForFormattedError,
                                siteviewoperationalexception
                                        .getFormattedErrorArgs());
                    } catch (IOException ioexception) {
                        LogManager.log("Error",
                                "Unable to save formatted args. ");
                    }
                } catch (SiteViewException siteviewexception) {
//                    assert false : "MonitorUpdate: Unhandled exception during monitor run";
                }
            }
            setProperty(pGetHostName, getHostname());
            if (!runOwnRules())
                setProperty(pCategory, "error");
        } catch (Throwable throwable) {
            setProperty(pCategory, "error");
            setProperty(pStateString, throwable);
            setProperty(pMeasurement, 0);
            throwable.printStackTrace();
            LogManager.log("Error", ("Update Monitor: " + getProperty(pName)
                    + ", error: " + throwable));
        }
    }

    /**
     * 
     */
    public void run() {
        try {
            this.currentStatus = "monitor thread started...";
            if (this.runExclusively()) {
                this.currentStatus = "ready, waiting for Dialup monitors to finish...";
                SiteViewGroup siteViewGroup = SiteViewGroup
                        .currentSiteView();
                siteViewGroup.monitorScheduler.suspendScheduler();
                long exclusiveMonitorTimeout = this.getSettingAsLong(
                        "_exclusiveMonitorTimeout", 300) * 1000;
                long startTime = Platform.timeMillis();
                while (MonitorQueue.getRunningCount() <= 1) { // line:50
                    long randomInterval = 1000L + (long) (Math.random() * 500D);
                    Thread.sleep(randomInterval);
                    if (Platform.timeMillis() - startTime <= exclusiveMonitorTimeout) // line:84
                        continue;
                    LogManager.log("Error",
                            "Exclusive monitor timed out waiting to start: "
                                    + getProperty(pName));
                    return;
                }
            }
            // line:121

            if (this.isDispatcher()) {
                this.currentStatus = "starting the dispatcher process...";
                SiteViewGroup siteViewGroup = SiteViewGroup
                        .currentSiteView();
                siteViewGroup.startDispatcher();
            }
            // line:142

            this.progressString = "STARTING MONITOR: " + getProperty(pName)
                    + "\n";

            if (alertDebug) {
                this.currentStatus = "inside alert debug...";
                SiteViewMain.MD5 md5 = new SiteViewMain.MD5(
                        this.progressString);
                alertDebugId = md5.asHex().substring(0, 7);
                LogManager.log("RunMonitor", "alertDebug: " + alertDebugId
                        + " '" + this.progressString + "'");
            }
            // line:256

            this.incrementProperty(pSample);
            String selfCategory = this.getProperty(pCategory);
            if (lastGroupCategoryEnabled && this.getOwner() != null) {
                String ownerCategory = this.getOwner().getProperty(pCategory);
                this.getOwner().setProperty(pLastCategory, ownerCategory);
            }
            // line:306

            this.currentStatus = "starting the monitor update...";
            this.monitorUpdate();
            this.currentStatus = "monitor update done...";
            if (!this.stillActive())
                return;
            // line:336

            String categoryValue = "";
            if (!this.runOwnRules()
                    || (this.runOwnRules() && this.reportStatus())) {
                this.currentStatus = "checking thresholds...";
                if (alertDebug)
                    LogManager
                            .log(
                                    "RunMonitor",
                                    "alertDebug: "
                                            + alertDebugId
                                            + " Running classifiers (GOOD/WARNING/ERROR/NODATA): '"
                                            + this.currentStatus + "'");
                this.runClassifiers(this);
            }
            // line:417
            categoryValue = this.getProperty(pCategory);
            jgl.Array array = new jgl.Array();
            jgl.Array array1 = new jgl.Array();
            this.getDependencies(array, array1);
            if (array.size() > 0) {
                this.currentStatus = "checking depends-on condition...";
                if (!this.stillActive()) {
                    return;
                }
                // line:473
                String condition = AtomicMonitor.DEPENDS_CONDITION;
                if (!condition.equals("error") && categoryValue.equals("error")
                        && !selfCategory.equals("error")
                        && !this.getDependsOnSignal(new ArrayList(), true))
                    return;
            }
            // line:529

            if (!this.runOwnRules()) {
                if (this.getPropertyAsBoolean(pVerifyError)
                        || categoryValue.equals("error")
                        || selfCategory.equals("error")) {
                    this.currentStatus = "verifying error...";
                    Platform.sleep(StringProperty.toInteger(this
                            .getSetting("_verifySleepDuration")));
                    this.monitorUpdate();
                    if (!this.stillActive()) {
                        if (alertDebug) {
                            LogManager
                                    .log(
                                            "RunMonitor",
                                            "alertDebug: "
                                                    + alertDebugId
                                                    + " Monitor NOT still active, I quit, Not checking alerts.");
                        }
                        // line:631
                        return;
                    }
                    // line:635
                    this.runClassifiers(this);
                    categoryValue = this.getProperty(pCategory);
                }
                this.IncrementCategoryProperties(selfCategory, categoryValue);
                if (this.shouldComputeGroupCounts()) {
                    this.computeGroupCounts();
                }
            } else { // line:668
                categoryValue = this.getProperty(pCategory);
            }
            // line:676
            this.progressString += "<b>Monitor: " + categoryValue + ", "
                    + this.getProperty(pStateString) + "</b>\n";
            this.currentStatus = "logging the results to SiteView daily log...";
            LogManager.log(this.getSetting(pSiteViewLogName), this);
            savaLog(this);
            long l2 = Platform.timeMillis();
            this.setProperty(pMonitorDoneTime, String.valueOf(l2));
            if (!this.runOwnRules()) {
                this.currentStatus = "checking for alerts...";
                this.progressString += "Checking for Alerts\n";
                if (alertDebug) {
                    LogManager.log("RunMonitor", "alertDebug: " + alertDebugId
                            + " Running runActionRules: '" + this.currentStatus
                            + "'");
                }
                // line:849
                this.runActionRules(this, categoryValue);
            }
            // line:855
            if (!(this.getSetting("_acknowledgeManuallyClear").length() > 0)) {
                this.acknowledgmentCheck(categoryValue);
            }
            // line:873
            this.progressString += "Monitor done.\n\n";
            this.currentStatus = "saving results...";
            long updateTime = Platform.timeMillis();
            this.setProperty(pLastUpdate, String.valueOf(updateTime));
            this.setProperty(pForceRefresh, "");
            this.reschedule(categoryValue, updateTime);

            SiteViewGroup siteViewGroup2 = SiteViewGroup.currentSiteView();
            if (siteViewGroup2.monitorScheduler != null) {
                long monitorInterval = (siteViewGroup2.monitorScheduler
                        .getNextTime() - Platform.timeMillis()) / 1000L;
                if (monitorInterval >= minMonitorInterval) {
                    String desc = siteViewGroup2.monitorScheduler
                            .getNextActionDescription();
                    LogManager.log("RunMonitor", "  waiting " + monitorInterval
                            + " seconds: will " + desc);
                }
            }
            // line:1028
            LogManager.log("Progress", this);
        } catch (InterruptedException e) {
            LogManager.log("Error", "Monitor: " + this.getProperty(pName)
                    + ", error: " + e + ", details: "
                    + FileUtils.stackTraceText(e));
        }
        // line:1108:
        finally {
            if (this.runExclusively()) {
                releaseExclusive(this);
            }
            // line:1122;
            if (this.releaseOnExit()) {
                MonitorQueue.release(this);
            }
            // line:1134
            //monitorStats.add(MonitorQueue.getRunningCount(), Platform
              //      .timeMillis());
            System.out.println(MonitorQueue.getRunningCount());
            this.running = false;
            this.thread = null;
            if (alertDebug) {
                LogManager.log("RunMonitor", "alertDebug: " + alertDebugId
                        + " END OF THIS MONITOR");
            }
            // line:1194
            if (this.runExclusively()) {
                SiteViewGroup siteViewGroup3 = SiteViewGroup
                        .currentSiteView();
                siteViewGroup3.monitorScheduler.resumeScheduler();
            }
            // line:1214
            synchronized (this) {
                notifyAll();
            }
            this.currentStatus = "done, getting out of monitor thread...";
        }
    }

    void reschedule(String string, long l) {
        String schedule = getSchedule();
        if (!schedule.startsWith("*")) {
            long frequency = currentFrequency;
            recalculateFrequency(string);
            if (frequency != currentFrequency) {
                UpdateMonitor updatemonitor = action;
                SiteViewGroup.currentSiteView().monitorScheduler
                        .unschedule(updatemonitor);
                if (currentFrequency != 0L && updatemonitor != null)
                    SiteViewGroup.currentSiteView().monitorScheduler
                            .scheduleRepeatedPeriodicAction(updatemonitor,
                                    currentFrequency, l);
            }
        }
    }

    public void saveMonitor(jgl.HashMap hashmap) {
        try {
            String monitorFilePath = (Platform.getRoot() + File.separator
                    + "groups" + File.separator
                    + group.getProperty(Monitor.pID) + ".mg");
            jgl.Array array = FrameFile.readFromFile(monitorFilePath);
            jgl.HashMap monitors = CGI.findMonitor(array,
                    getProperty(Monitor.pID));
            Enumeration enumeration = hashmap.keys();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                monitors.put(key, hashmap.get(key));
            }
            FrameFile.writeToFile(monitorFilePath, array);
        } catch (Exception exception) {
            LogManager.log("Error", ("A problem has occurred updating " + group
                    .getProperty(Monitor.pName)));
        }
    }

    public void acknowledgmentCheck(String string) {
        if (isAcknowledged()) {
            String acknowledgedState = getProperty(pAcknowledgedState);
            jgl.HashMap masterConfig = MasterConfig.getMasterConfig();
            long acknowledgeTimeLimit = (TextUtils.toLong(TextUtils.getValue(
                    masterConfig, "_acknowledgeTimeLimit")));
            boolean bool = acknowledgedState.equals(string);
            boolean underTimeLimit = false;
            if (acknowledgeTimeLimit > 0L
                    && Platform.timeMillis()
                            - TextUtils
                                    .stringToDate(getProperty(pAcknowledged))
                                    .getTime() > acknowledgeTimeLimit * 60000L)
                underTimeLimit = true;
            if (!bool || underTimeLimit) {
                unsetProperty(pAcknowledged);
                unsetProperty(pAcknowledgedDelay);
                unsetProperty(pAcknowledgedState);
                unsetProperty(pAcknowledgeComment);
                unsetProperty(pAcknowledgeUser);
                unsetProperty(pAcknowledgeUserIP);
                MonitorGroup monitorgroup = (MonitorGroup) getOwner();
                if (isAlertTemporarilyDisabled()
                        && getProperty(pAcknowledgeAlertDisabled).length() > 0) {
                    unsetProperty(pAlertDisabled);
                    unsetProperty(pTimedDisable);
                    unsetProperty(pDisabled);
                    jgl.HashMap newConfig = new jgl.HashMap();
                    newConfig.put("_alertDisabled", "");
                    newConfig.put("_timedDisable", "");
                    newConfig.put("_disabled", "");
                    saveMonitor(newConfig);
                }
                unsetProperty(pAcknowledgeAlertDisabled);
                if (monitorgroup != null)
                    monitorgroup.saveDynamic();
                managePage.acknowledgeLog("AcknowledgeClear", getFullID(), "",
                        "", "", "", "");
            }
        }
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pRunning) {
            if (isRunning())
                return "RUNNING";
            return "";
        }
        if (stringproperty == pMaxErrorCount)
            return "" + maxErrorCount;
        if (stringproperty == pMonitorsInError)
            return "" + monitorsInError;
        if (stringproperty == pMonitorsInGroup)
            return "" + monitorsInGroup;
        if (stringproperty == pCustomData)
            return getCustomData();
        else
            return super.getProperty(stringproperty);
    }

    public String getCustomData() {
        return "";
    }

    public boolean releaseOnExit() {
        if (Thread.currentThread() instanceof ThreadPool.SingleThread) {
            ThreadPool.SingleThread singlethread = (ThreadPool.SingleThread) Thread
                    .currentThread();
            if (singlethread.getPoolName().equals("Monitors"))
                return singlethread.getCustomProperty();
        }
        return false;
    }

    public boolean stillActive() {
        if (Thread.currentThread() instanceof ThreadPool.SingleThread) {
            ThreadPool.SingleThread singlethread = (ThreadPool.SingleThread) Thread
                    .currentThread();
            if (singlethread.getPoolName().equals("Monitors"))
                return singlethread.isSingleThreadAlive();
        }
        return true;
    }

    public boolean runExclusively() {
        return false;
    }

    public int getMaxCounters() {
        return 0;
    }

    public void getDependencies(jgl.Array dependsOns,
            jgl.Array dependsConditions) {
        String dependsOnProperty = this.getProperty(DEPENDS_ON);
        if (dependsOnProperty != null && dependsOnProperty.length() > 0) {
            dependsOns.add(dependsOnProperty);
            dependsConditions.add(this.getProperty(DEPENDS_CONDITION));
            if (!dependsOnRecursive)
                return;
        }
        MonitorGroup monitorgroup = (MonitorGroup) getOwner();
        if (monitorgroup != null)
            monitorgroup.getGroupDepends(dependsOns, dependsConditions,
                    dependsOnRecursive);
    }

    public boolean getDependsOnSignal(List list, boolean bool) {
        jgl.Array dependsOns = new jgl.Array();
        jgl.Array dependsConditions = new jgl.Array();
        getDependencies(dependsOns, dependsConditions);
        int size = dependsOns.size();
        if (size == 0)
            return true;
        if (list.contains(getFullID())) {
            LogManager
                    .log("Error",
                            ("Encountered circular dependencies on monitor "
                                    + getFullID() + ", monitor did not run: "
                                    + getFullID() + ":(" + getProperty(pName)
                                    + "), " + currentStatus));
            LogManager
                    .log("RunMonitor",
                            ("Encountered circular dependencies on monitor "
                                    + getFullID() + ", monitor did not run: "
                                    + getFullID() + ":(" + getProperty(pName)
                                    + "), " + currentStatus));
            return false;
        }
        list.add(getFullID());
        for (int i = 0; i < size; i++) {
            String dependsOn = (String) dependsOns.at(i);
            String dependsCondition = (String) dependsConditions.at(i);
            String category = GOOD_CATEGORY;
            if (dependsCondition.equals("error"))
                category = ERROR_CATEGORY;
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            AtomicMonitor dependentMonitor = ((AtomicMonitor) siteviewgroup
                    .getElement(dependsOn.replace(' ', '/')));
            if (dependentMonitor != null) {
                if (!dependentMonitor.getProperty(pCategory).equals(category)) {
                    setDependsOnProperties(category);
                    return false;
                }
                if (bool) {
                    startAndWait(dependentMonitor);
                    if (!dependentMonitor.getProperty(pCategory).equals(
                            category)) {
                        setDependsOnProperties(category);
                        return false;
                    }
                }

                if (!dependentMonitor.getDependsOnSignal(list, bool)) {
                    setDependsOnProperties(category);
                    return false;
                }
            } else
                LogManager.log("Error", (getFullID() + ": ("
                        + getProperty(pName)
                        + ") could not find dependent monitor " + dependsOn));
        }
        return true;
    }

    private void setDisabledProperties(String string) {
        disabledTotal++;
        setProperty(pCategory, "nodata");
        setProperty(pStateString, string);
        setProperty(pMeasurement, 0);
        ResetCategoryProperties("good");
        ResetCategoryProperties("warning");
        ResetCategoryProperties("error");
        if (getSetting("_onlyLogEnabledMonitors").length() == 0) {
            setProperty(pCategory, Monitor.DISABLED_CATEGORY);
            LogManager.log(getSetting(pSiteViewLogName), this);
            setProperty(pCategory, "nodata");
        }
    }

    public void setDependsOnProperties(String string) {
        disabledTotal++;
        String depends = DEPENDS_PREFIX + "is not OK";
        if (string.equals(ERROR_CATEGORY))
            depends = DEPENDS_PREFIX + "not in error";
        setDisabledProperties(depends);
    }

    synchronized void startAndWait(AtomicMonitor atomicMonitor) {
        boolean running = atomicMonitor.isRunning();
        if (!running)
            running = atomicMonitor.runUpdate(true);
        if (running) {
            try {
                synchronized (atomicMonitor) {
                    atomicMonitor.wait();
                }
            } catch (Exception exception) {
                LogManager.log("Error", (getProperty(pName)
                        + " exception waiting: " + exception));
            }
        }
    }

    public boolean shouldComputeGroupCounts() {
        MonitorGroup monitorgroup = (MonitorGroup) getOwner();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        return (monitorgroup != null && monitorgroup.computeGroupCount || siteviewgroup.computeGroupCount);
    }

    public void computeGroupCounts() {
        maxErrorCount = 0;
        monitorsInGroup = 0;
        monitorsInError = 0;
        int i = 0;
        MonitorGroup monitorgroup = (MonitorGroup) getOwner();
        synchronized (Monitor.countLock) {
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (!monitor.isDisabled()) {
                    monitorsInGroup++;
                    int errorCount = monitor.getPropertyAsInteger(pErrorCount);
                    if (monitor.getProperty(pCategory).equals(
                            Monitor.ERROR_CATEGORY)) {
                        if (errorCount > 0)
                            monitorsInError++;
                    } else
                        errorCount = 0;
                    if (errorCount > maxErrorCount) {
                        maxErrorCount = errorCount;
                        i = 1;
                    } else if (errorCount == maxErrorCount)
                        i++;
                }
            }
        }
        if (maxErrorCount > 0 && i > 1)
            maxErrorCount = 9999999;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        Vector vector = new Vector();
        if (scalarproperty == pDependsCondition) {
            vector.addElement("good");
            vector.addElement("OK");
            vector.addElement("error");
            vector.addElement("Error");
            return vector;
        }
        if (scalarproperty == pDependsOn) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            Enumeration monitorgroups = siteviewgroup.getMonitors();
            vector.addElement("");
            vector.addElement("None");
            while (monitorgroups.hasMoreElements()) {
                MonitorGroup monitorgroup = (MonitorGroup) monitorgroups
                        .nextElement();
                Enumeration monitors = monitorgroup.getMonitors();
                while (monitors.hasMoreElements()) {
                    Monitor monitor = (Monitor) monitors.nextElement();
                    String string = monitor.getProperty("_class");
                    if (!getFullID().equalsIgnoreCase(monitor.getFullID())
                            && !string.equalsIgnoreCase("SubGroup")) {
                        vector.addElement(monitorgroup.getProperty(pGroupID)
                                + " " + monitor.getProperty("_id"));
                        vector.addElement(monitorgroup.getProperty("_name")
                                + ":" + monitor.getProperty("_name"));
                    }
                }
            }
            return vector;
        }
        return super.getScalarValues(scalarproperty, httprequest, cgi);
    }

    public static AtomicMonitor MonitorCreate(String monitorClassName,
            HTTPRequest httprequest) throws Exception {
        AtomicMonitor atomicmonitor = MonitorCreate(monitorClassName);
        atomicmonitor.onMonitorCreateFromPage(httprequest);
        return atomicmonitor;
    }

    public static AtomicMonitor MonitorCreate(String monitorClassName)
            throws Exception {
        Class monitorClass = null;
        try {
            monitorClass = Class.forName("CustomMonitor." + monitorClassName);
        } catch (Throwable throwable) {
            /* empty */
        }
        if (monitorClass == null)
            monitorClass = Class.forName("COM.dragonflow.StandardMonitor."
                    + monitorClassName);
        AtomicMonitor atomicmonitor = (AtomicMonitor) monitorClass
                .newInstance();
        atomicmonitor.setProperty(pID, 0);
        return atomicmonitor;
    }

    public static AtomicMonitor MonitorCreate(jgl.Array array,
            String monitorID, String portalServerID, HTTPRequest httprequest)
            throws Exception {
        AtomicMonitor atomicmonitor = MonitorCreate(array, monitorID,
                portalServerID);
        atomicmonitor.onMonitorCreateFromPage(httprequest);
        return atomicmonitor;
    }

    public static AtomicMonitor MonitorCreate(jgl.Array array,
            String monitorID, String portalServerID) throws Exception {
        jgl.HashMap hashmap = monitorUtils.findMonitor(array, monitorID);
        AtomicMonitor atomicmonitor = (AtomicMonitor) Monitor.createMonitor(
                hashmap, portalServerID);
        return atomicmonitor;
    }

    public static void saveThresholds(Monitor monitor, HTTPRequest httprequest,
            jgl.HashMap errorLogs) {
        monitor.unsetProperty("_classifier");
        String[] strings = { "error", "warning", "good" };
        for (int i = 0; i < strings.length; i++) {
            if (httprequest.hasValue(strings[i] + "-condition")) {
                String conditionValue = httprequest.getValue(strings[i]
                        + "-condition");
                if (!conditionValue.equals("default")) {
                    String comparison = httprequest.getValue(strings[i]
                            + "-comparison");
                    String parameter = httprequest.getValue(strings[i]
                            + "-parameter");
                    if (parameter.trim().length() == 0)
                        errorLogs.put(strings[i] + "-parameter",
                                "threshold value was missing");
                    try {
                        if (!parameter.startsWith("'"))
                            Float.valueOf(parameter);
                    } catch (NumberFormatException numberformatexception) {
                        errorLogs.put(strings[i] + "-parameter",
                                "threshold value was not a number");
                    }
                    String thresholdValue = (conditionValue + " " + comparison
                            + " " + parameter + "\t" + strings[i]);
                    monitor.addProperty("_classifier", thresholdValue);
                    Rule rule = Rule.stringToClassifier(thresholdValue);
                    if (rule != null)
                        monitor.addElement(rule);
                }
            }
        }
    }

    public static void saveThresholds(Monitor monitor, jgl.HashMap hashmap,
            jgl.HashMap errorLogs) {
        monitor.unsetProperty("_classifier");
        String[] strings = { "error", "warning", "good" };
        for (int i = 0; i < strings.length; i++) {
            String condition = strings[i] + "-condition";
            String conditionValue = TextUtils.getValue(hashmap, condition);
            if (conditionValue.length() != 0
                    && !conditionValue.equals("default")) {
                String comparison = strings[i] + "-comparison";
                String parameter = strings[i] + "-parameter";
                String comparisonValue = TextUtils
                        .getValue(hashmap, comparison);
                String parameterValue = TextUtils.getValue(hashmap, parameter);
                if (parameterValue.trim().length() == 0)
                    errorLogs.put(strings[i] + "-parameter",
                            "threshold value was missing");
                try {
                    if (!parameterValue.startsWith("'"))
                        Float.valueOf(parameterValue);
                } catch (NumberFormatException numberformatexception) {
                    errorLogs.put(strings[i] + "-parameter",
                            "threshold value was not a number");
                }
                String thresholdValue = (conditionValue + " " + comparisonValue
                        + " " + parameterValue + "\t" + strings[i]);
                monitor.addProperty("_classifier", thresholdValue);
                Rule rule = Rule.stringToClassifier(thresholdValue);
                if (rule != null)
                    monitor.addElement(rule);
            }
        }
    }

    public static void saveClassifier(Monitor monitor, jgl.HashMap hashmap,
            jgl.HashMap errorLogs) {
        monitor.unsetProperty("_classifier");
        jgl.Array array = TextUtils.getMultipleValues(hashmap, "_classifier");
        for (int i = 0; i < array.size(); i++) {
            String string = (String) array.at(i);
            if (string.length() != 0) {
                String[] strings = TextUtils.split(string);
                if (strings.length < 4)
                    errorLogs.put("_classifier", ("expecting four tokens "
                            + strings.length + " found"));
                try {
                    if (!strings[2].startsWith("'"))
                        Float.valueOf(strings[2]);
                } catch (NumberFormatException numberformatexception) {
                    errorLogs.put("_classifier", ("threshold value ("
                            + strings[2] + ") is not a number"));
                }
                monitor.addProperty("_classifier", string);
                Rule rule = Rule.stringToClassifier(string);
                if (rule != null)
                    monitor.addElement(rule);
            }
        }
    }

    public static void checkThresholds(jgl.HashMap hashmap,
            jgl.HashMap errorLogs) {
        String[] strings = { "error", "warning", "good" };
        for (int i = 0; i < strings.length; i++) {
            String condition = strings[i] + "-condition";
            String conditionValue = TextUtils.getValue(hashmap, condition);
            if (conditionValue.length() != 0
                    && !conditionValue.equals("default")) {
                String parameter = strings[i] + "-parameter";
                String parameterValue = TextUtils.getValue(hashmap, parameter);
                if (parameterValue.trim().length() == 0)
                    errorLogs.put(strings[i] + "-parameter",
                            "threshold value was missing");
                try {
                    if (!parameterValue.startsWith("'"))
                        Float.valueOf(parameterValue);
                } catch (NumberFormatException numberformatexception) {
                    errorLogs
                            .put(parameter, "threshold value was not a number");
                }
            }
        }
        jgl.Array array = TextUtils.getMultipleValues(hashmap, "_classifier");
        for (int i = 0; i < array.size(); i++) {
            String classifier = (String) array.at(i);
            if (classifier.length() != 0) {
                String[] classifierTokens = TextUtils.split(classifier);
                if (classifierTokens.length < 4)
                    errorLogs.put("_classifier", ("expecting four tokens "
                            + classifierTokens.length + " found"));
                try {
                    if (!classifierTokens[2].startsWith("'"))
                        Float.valueOf(classifierTokens[2]);
                } catch (NumberFormatException numberformatexception) {
                    errorLogs.put("_classifier", ("threshold value ("
                            + classifierTokens[2] + ") is not a number"));
                }
            }
        }
    }

    public static void saveCustomProperties(jgl.HashMap hashmap,
            Monitor monitor, HTTPRequest httprequest) {
        Enumeration enumeration = hashmap.values("_monitorEditCustom");
        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            String[] strings = TextUtils.split(string, "|");
            String propertyName = strings[0];
            if (propertyName.length() > 0) {
                if (!propertyName.startsWith("_"))
                    propertyName = "_" + propertyName;
                if (monitor.propertyInTemplate(propertyName)) {
                    String propertyValue = httprequest.getValue(propertyName);
                    propertyValue = propertyValue.replace('\r', ' ');
                    propertyValue = propertyValue.replace('\n', ' ');
                    monitor.setProperty(propertyName, httprequest
                            .getValue(propertyName));
                }
            }
        }
    }

    public static int saveOrdering(Monitor monitor, HTTPRequest httprequest) {
        int i = TextUtils.toInt(httprequest.getValue("ordering"));
        if (i <= 0)
            i = 1;
        return i;
    }

    public ThreadPool.SingleThread getThread() {
        ThreadPool.SingleThread singlethread = monitorThreadsPool.getThread();
        singlethread.setNameIfNeeded(getProperty(pName) + "("
                + getProperty(pGroupID) + "/" + getProperty(pID) + ") ");
        singlethread.setPriorityIfNeeded(7);
        thread = singlethread;
        return thread;
    }

    public boolean isEditableProperty(StringProperty stringproperty,
            HTTPRequest httprequest) {
        return true;
    }

    public jgl.Array getPropertiesToPassBetweenPages(HTTPRequest httprequest) {
        return new jgl.Array();
    }

    public void notifyAdd() {
        /* empty */
    }

    public void notifyDelete() {
        /* empty */
    }

    public void notifyDisable() {
        /* empty */
    }

    public void notifyEnable() {
        /* empty */
    }

    public void notifyMove(String string) {
        /* empty */
    }

    public void notifyUpdate() {
        /* empty */
    }

    public void loadPropsFromRequest(HTTPRequest httprequest, jgl.Array array) {
        for (int i = 0; i < array.size(); i++) {
            StringProperty stringproperty = (StringProperty) array.at(i);
            setProperty(stringproperty, httprequest.getValue(stringproperty
                    .getName()));
        }
    }

    public String getTopazTarget() {
        String string = getHostname();
        if (string == null || string.length() <= 0)
            string = defaultTopazHostName;
        string = string.trim();
        if (string.startsWith("\\"))
            string = string.substring(2);
        return string.toLowerCase();
    }

    public String getTopazMonitorClass() {
        return I18N.toDefaultEncoding((String) getClassProperty("title"));
    }

    public String getTopazMonitorClassDisplayName() {
        String string = (String) getClassProperty("topazName");
        if (string == null || string.length() <= 0)
            string = (String) getClassProperty("class");
        return string;
    }

    public int getUniqueInternalId() {
        String string = this.getProperty("_internalId");
        if (string != null) {
            int i = TextUtils.toInt(string);
            if (i > 0)
                return i;
        }
        return 0;
    }

    public String getTopazParent() {
        return getProperty(pGroupID);
    }

    public String getTopazStateString() {
        return getProperty(pStateString);
    }

    public int getTopazQuality() {
        String string = getProperty(pCategory);
        int i;
        if (string.equals("good"))
            i = 1;
        else if (string.equals("warning"))
            i = 2;
        else if (string.startsWith("error"))
            i = 3;
        else
            i = 0;
        return i;
    }

    public void setMeasurementsValues(java.util.HashMap hashmap) {
        Collection collection = hashmap.values();
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
//            TopazMeasurement topazmeasurement = (TopazMeasurement) iterator
//                    .next();
//            StringProperty stringproperty = getTopazValueProperty(topazmeasurement
//                    .getInternalName());
//            if (stringproperty == null)
//                iterator.remove();
//            else {
//                String string = stringproperty
//                        .valueOnlyString(getProperty(stringproperty));
//                topazmeasurement.setValue(string);
//                if (supportTopazIndividualCounterQuality()) {
//                    if (propErrorStatusMap.get(stringproperty.getName()) != null)
//                        topazmeasurement.setCounterQuality(3);
//                    else if (propWarningStatusMap.get(stringproperty.getName()) != null)
//                        topazmeasurement.setCounterQuality(2);
//                    else if (propGoodStatusMap.get(stringproperty.getName()) != null)
//                        topazmeasurement.setCounterQuality(1);
//                    else
//                        topazmeasurement.setCounterQuality(0);
//                }
//                topazmeasurement
//                        .setCustomData(getTopazCounterCustomData(stringproperty));
//            }
        }
    }

//    protected Variant getTopazCounterCustomData(StringProperty stringproperty) {
//        return null;
//    }

    public java.util.HashMap getTopazMeasurements() {
        java.util.HashMap hashmap = new java.util.HashMap();
        Enumeration rules = getRules(1);
        while (rules.hasMoreElements()) {
            String string = ((Rule) rules.nextElement())
                    .getProperty(Rule.pExpression);
            String[] strings = TextUtils.tokenize(string);
            if (strings.length > 2 && strings[2] != null
                    && !strings[2].equals("n/a"))
                hashmap.put(strings[0], null);
        }
        java.util.HashMap topazMeasurements = new java.util.HashMap();
        Enumeration stateProperties = getStatePropertyObjects();
        while (stateProperties.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) stateProperties
                    .nextElement();
            if (stringproperty != null) {
//                String string = getTopazCounterLabel(stringproperty);
//                TopazMeasurement topazmeasurement = new TopazMeasurement(
//                        getTopazCounterInternalName(stringproperty), string,
//                        getTopazCounterDescription(stringproperty),
//                        getTopazCounterCategory(stringproperty),
//                        isCounterThreshold(stringproperty, hashmap));
//                topazMeasurements.put(string, topazmeasurement);
            }
        }
        return topazMeasurements;
    }

    public int getTopazFilter() {
        if (getProperty(pNotLogToTopaz).length() > 0)
            return 0;
        if (getProperty(pOnlyLogStatusChanges).length() > 0)
            return 3;
        if (getProperty(pOnlyLogThresholdMeas).length() > 0)
            return 4;
        if (getProperty(pOnlyLogMonitorData).length() > 0)
            return 2;
        return 1;
    }

    public StringProperty getTopazValueProperty(String string) {
        return getPropertyObject(string);
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return stringproperty.getLabel();
    }

    public String getTopazCounterInternalName(StringProperty stringproperty) {
        return stringproperty.getName();
    }

    public String getTopazCounterDescription(StringProperty stringproperty) {
        String string = stringproperty.getDescription();
        if (string.equalsIgnoreCase("no description"))
            string = "";
        return string;
    }

    public String getTopazCounterCategory(StringProperty stringproperty) {
        return getTopazCounterLabel(stringproperty);
    }

    public String getTopazInternalName() {
        return getProperty(pUniqueInternalId);
    }

    private boolean isCounterThreshold(StringProperty stringproperty,
            java.util.HashMap hashmap) {
        return stringproperty.isThreshold()
                && hashmap.containsKey(stringproperty.getName());
    }

    public boolean isMultiThreshold() {
        Object object = getClassProperty("classType");
        return object != null && object.equals("application");
    }

    public boolean isEmsMaufacturer() {
        return false;
    }

    private void addBasedOnExcludeAttribs(jgl.Array array,
            StringProperty stringproperty, Vector vector, boolean bool, int i) {
        boolean exclude = true;
        if (vector.contains(stringproperty.getName()))
            exclude = false;
        else if (bool) {
            String string = getProperty(stringproperty);
            if (string == null || string.length() == 0)
                exclude = false;
        }
        if (exclude) {
            if (i == 1) {
                if (stringproperty.isAdvanced)
                    exclude = false;
            } else if (i == 2 && !stringproperty.isAdvanced)
                exclude = false;
        }
        if (exclude)
            array.add(stringproperty);
    }

    public Enumeration getConfigurationAddProperties(Vector vector,
            boolean bool, int i) {
        jgl.Array properties = getProperties();
        jgl.Array configurationAddProperties = new jgl.Array();
        for (int j = 0; j < properties.size(); j++) {
            StringProperty stringproperty = (StringProperty) properties.at(j);
            if (stringproperty.isConfigurable && stringproperty.isParameter)
                addBasedOnExcludeAttribs(configurationAddProperties,stringproperty, vector, bool, i);
        }
        configurationAddProperties.add(new StringProperty("_classifier"));
        return configurationAddProperties.elements();
    }

    public Enumeration getConfigurationEditProperties(Vector vector,
            boolean bool, int i) {
        jgl.Array properties = getProperties();
        jgl.Array configurationEditProperties = new jgl.Array();
        for (int j = 0; j < properties.size(); j++) {
            StringProperty stringproperty = (StringProperty) properties.at(j);
            if (stringproperty.isConfigurable && stringproperty.isParameter
                    && stringproperty.isEditable)
                addBasedOnExcludeAttribs(configurationEditProperties,
                        stringproperty, vector, bool, i);
        }
        configurationEditProperties.add(new StringProperty("_classifier"));
        return configurationEditProperties.elements();
    }

    public Enumeration getConfigurationAllProperties(Vector vector, boolean bool) {
        jgl.Array properties = getProperties();
        jgl.Array configurationAllProperties = new jgl.Array();
        for (int i = 0; i < properties.size(); i++) {
            StringProperty stringproperty = (StringProperty) properties.at(i);
            if (stringproperty.isParameter)
                addBasedOnExcludeAttribs(configurationAllProperties,
                        stringproperty, vector, bool, 0);
        }
        configurationAllProperties.add(new StringProperty("_classifier"));
        return configurationAllProperties.elements();
    }

    public Enumeration getConfigurationRequiredProperties(String string)
            throws Exception {
        Class monitorClass = Class.forName(string);
        AtomicMonitor atomicmonitor = (AtomicMonitor) monitorClass
                .newInstance();
        jgl.Array properties = atomicmonitor.getProperties();
        jgl.Array configurationRequiredProperties = new jgl.Array();
        HTTPRequest httprequest = new HTTPRequest();
        for (int i = 0; i < properties.size(); i++) {
            jgl.HashMap hashmap = new jgl.HashMap();
            StringProperty stringproperty = (StringProperty) properties.at(i);
            verify(stringproperty, getProperty(stringproperty), httprequest,
                    hashmap);
            if (hashmap.size() > 0)
                configurationRequiredProperties.add(stringproperty);
        }
        return configurationRequiredProperties.elements();
    }

    public Enumeration getMeasurementProperties(Vector vector, boolean bool) {
        Enumeration enumeration = getStatePropertyObjects();
        jgl.Array array = new jgl.Array();
        while (enumeration.hasMoreElements())
            addBasedOnExcludeAttribs(array, ((StringProperty) enumeration
                    .nextElement()), vector, bool, 0);
        return array.elements();
    }

    public Enumeration getRuntimeProperties(Vector vector, boolean bool) {
        jgl.Array properties = getProperties();
        jgl.Array runtimeProperties = new jgl.Array();
        for (int i = 0; i < properties.size(); i++) {
            StringProperty stringproperty = (StringProperty) properties.at(i);
            if (!stringproperty.isParameter)
                addBasedOnExcludeAttribs(runtimeProperties, stringproperty,
                        vector, bool, 0);
        }
        return runtimeProperties.elements();
    }

    public boolean errorConditionIsMet(long l) {
        return (getPropertyAsLong(pAvailabilityErrorCode) == l || getPropertyAsLong(pOperationalErrorCode) == l);
    }

    public boolean collectionErrorOccurred() {
        return getCollectionError() != null;
    }

    public SiteViewError getCollectionError() {
        SiteViewError siteviewerror = getOperationalError();
        if (siteviewerror != null)
            return siteviewerror;
        else
            return getAvailabilityError();
    }

    private SiteViewError getAvailabilityError() {
        long l = getPropertyAsLong(pAvailabilityErrorCode);
        SiteViewError siteviewerror = null;
        boolean bool = false;
        if (l != SiteViewErrorCodes.NO_ERROR) {
            siteviewerror = new SiteViewError(1, l,
                    getPropertyAsLong(pApplicationErrorCode),
                    getProperty(pApplicationErrorMessage));
            if (getProperty(pArgsForFormattedError).length() > 0) {
                try {
                    String[] strings = ((String[]) getPropertyAsObject(pArgsForFormattedError));
                    if (strings != null && strings.length > 0)
                        siteviewerror.setArgsForFormattedError(strings);
                    bool = true;
                } catch (ClassCastException classcastexception) {
                    /* empty */
                }
            }
        } else if (getProperty(pNoData).length() > 0)
            siteviewerror = new SiteViewError(1,
                    SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
        if (siteviewerror != null
                && !bool
                && (siteviewerror.getErrorCode() == SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL))
            siteviewerror
                    .setArgsForFormattedError(new String[] { getProperty(pStateString) });
        return siteviewerror;
    }

    private SiteViewError getOperationalError() {
        long l = getPropertyAsLong(pOperationalErrorCode);
        if (l != SiteViewErrorCodes.NO_ERROR) {
            SiteViewError siteviewerror = new SiteViewError(2, l,
                    getPropertyAsLong(pOperationalErrorCode),
                    getProperty(pApplicationErrorMessage));
            if (getProperty(pArgsForFormattedError).length() > 0) {
                try {
                    String[] strings = ((String[]) getPropertyAsObject(pArgsForFormattedError));
                    if (strings != null && strings.length > 0)
                        siteviewerror.setArgsForFormattedError(strings);
                } catch (ClassCastException classcastexception) {
                    /* empty */
                }
            }
            return siteviewerror;
        } else {
            return null;
        }
    }

    public long[] getSupportedErrorConditions() {
        long[] ls = { SiteViewErrorCodes.NO_ERROR,
                SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL };
        return ls;
    }

    public static boolean isSuspended() {
        return (TextUtils.getValue(MasterConfig.getMasterConfig(),
                "_suspendMonitors").length() > 0);
    }

    public void setUniqueInternalId(int i) {
        setProperty(pUniqueInternalId, i);
    }

    public SiteViewObject getParentGroup() {
        return getParent();
    }

    public String getName() {
        return getProperty(pName);
    }

    static {
        DEPENDS_ON = "_dependsOn";
        DEPENDS_CONDITION = "_dependsCondition";

        alertDebug = false;

        pFrequency = new FrequencyProperty("_frequency", "600");
        pFrequency.setDisplayText("Update every",
                "amount of time between checks of a monitor");
        pFrequency.setParameterOptions(true, 101, false);
        pErrorFrequency = new FrequencyProperty("_errorFrequency", "");
        pErrorFrequency
                .setDisplayText(
                        "Update every (on errors)",
                        "the amount of time between checks whenever the status of the monitor is not ok; the Update value from above is used if this is left blank");
        pErrorFrequency.setParameterOptions(true, 103, true);
        pClass = new StringProperty("_class");
        pNotLogToTopaz = new BooleanProperty("_notLogToTopaz");
//        pNotLogToTopaz.setDisplayText("Stop Logging To "
//                + TopazInfo.getTopazName(),
//                "Check this box to not log the data for this monitor to "
//                        + TopazInfo.getTopazName());
        pNotLogToTopaz.setParameterOptions(true, 104, true);
        pOnlyLogStatusChanges = new BooleanProperty("_onlyStatusChanges");
        pOnlyLogStatusChanges.setParameterOptions(false, true, 105, true);
        pOnlyLogThresholdMeas = new BooleanProperty("_logOnlyThresholdMeas");
        pOnlyLogThresholdMeas.setParameterOptions(false, true, 106, true);
        pOnlyLogMonitorData = new BooleanProperty("_logOnlyMonitorData");
        pOnlyLogMonitorData.setParameterOptions(false, true, 107, true);
        pUniqueInternalId = new StringProperty("_internalId");
        pCustomData = new StringProperty("customData");
        pCustomData.setIsStateProperty(false);
        pCustomData.setParameterOptions(false, false, 200, true);
        pMaximumRunTime = new NumericProperty("_maxrun", "600", "seconds");
        pSample = new NumericProperty("sample", "0");
        pMonitorDoneTime = new NumericProperty("monitorDoneTime", "0",
                "milliseconds");
        pGetHostName = new StringProperty("gethostname");
        pNoData = new StringProperty("nodata", "");
        pMaxErrorCount = new NumericProperty("maxErrorCount", "0");
        pMonitorsInError = new NumericProperty("monitorsInError", "0");
        pMonitorsInGroup = new NumericProperty("monitorsInGroup", "0");
        pOperationalErrorCode = new NumericProperty("_operationalErrorCode", ""
                + SiteViewErrorCodes.NO_ERROR);
        pAvailabilityErrorCode = new NumericProperty("_availabilityErrorCode",
                "" + SiteViewErrorCodes.NO_ERROR);
        pApplicationErrorCode = new StringProperty("_applicationErrorCode");
        pApplicationErrorMessage = new StringProperty(
                "_applicationErrorMessage");
        pArgsForFormattedError = new StringProperty("_argsForFormattedError");

        if (!Platform.isSiteSeerServer()) {
            pDependsOn = new treeProperty(DEPENDS_ON);
            pDependsOn.setParameterOptions(true, 108, true);
            pDependsOn.setDisplayText("Depends On",
                    "Choose the monitor that this monitor depends on");
            pDependsCondition = new ScalarProperty(DEPENDS_CONDITION);
            pDependsCondition.setParameterOptions(true, 109, true);
            pDependsCondition
                    .setDisplayText("Depends Condition",
                            "If OK, this monitor is only enabled if the Depends On monitor is OK.");
            StringProperty[] stringpropertys = { pFrequency, pClass,
                    pErrorFrequency, pMaxErrorCount, pMonitorsInError,
                    pMonitorsInGroup, pMaximumRunTime, pSample,
                    pMonitorDoneTime, pDependsOn, pDependsCondition,
                    pGetHostName, pNotLogToTopaz, pNoData,
                    pOnlyLogStatusChanges, pOnlyLogThresholdMeas,
                    pOnlyLogMonitorData, pUniqueInternalId, pCustomData,
                    pOperationalErrorCode, pAvailabilityErrorCode,
                    pApplicationErrorCode, pApplicationErrorMessage,
                    pArgsForFormattedError };
            addProperties("COM.dragonflow.SiteView.AtomicMonitor",
                    stringpropertys);
        } else {
            StringProperty[] stringpropertys = { pFrequency, pClass,
                    pErrorFrequency, pMaxErrorCount, pMonitorsInError,
                    pMonitorsInGroup, pMaximumRunTime, pSample,
                    pMonitorDoneTime, pGetHostName, pNoData, pUniqueInternalId,
                    pOperationalErrorCode, pAvailabilityErrorCode,
                    pApplicationErrorCode, pApplicationErrorMessage };
            addProperties("COM.dragonflow.SiteView.AtomicMonitor",
                    stringpropertys);
        }

        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        if (TextUtils.getValue(hashmap, "_dependsOnRecursive").length() > 0)
            dependsOnRecursive = true;
        String string = System.getProperty("Alert.debug");
        if (string != null) {
            alertDebug = true;
            System.out.println("Alert.debug= '" + alertDebug + "'");
        }
        try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            defaultTopazHostName = inetaddress.getHostName();
        } catch (UnknownHostException unknownhostexception) {
            defaultTopazHostName = "localhost";
            LogManager.logException(unknownhostexception);
        }
        int i = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_monitorMinInterval"));
        if (i > 0)
            minMonitorInterval = i;
        int j = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_monitorDebugLevel"));
        if (j > 0)
            monitorDebugLevel = j;
    }

	@Override
	public String getHostname() {
		// TODO Auto-generated method stub
		return null;
	}
	//往sql数据库中写入日志文件
	 public static void savaLog(PropertiedObject propertiedobject) {
			// TODO Auto-generated method stub
	    	String category=propertiedobject.getProperty("category");
	    	String MonitorName=propertiedobject.getProperty("_name");
	    	String stateString=propertiedobject.getProperty("stateString")+"*"+propertiedobject.getProperty("sample");
	    	if(!propertiedobject.getProperty("percentFull").equals("")){
	    		stateString=stateString+"*"+propertiedobject.getProperty("percentFull");
	    	}
	    	if(!propertiedobject.getProperty("freeSpace").equals("")){
	    		stateString=stateString+"*"+propertiedobject.getProperty("freeSpace");
	    	}
	    	if(!propertiedobject.getProperties("percentGood").equals("")){
	    		stateString=stateString+"*"+propertiedobject.getProperty("percentGood");
	    	}
	    	String MonitorId=propertiedobject.getProperty("_id");
	    	String ownerID=propertiedobject.getProperty("ownerID");
	    	String RecId=UUID.randomUUID().toString();
	    	RecId=RecId.replaceAll("-", "");
	    	long time=System.currentTimeMillis();		
	    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	Timestamp CreatedDateTime=new Timestamp(time);
	    	String sql="insert into MonitorLog(RecId,ownerID,MonitorStatus,MonitorName,MonitorId,MonitorMassage,CreatedDateTime)" +
	    			"values('"+RecId+"','"+ownerID+"','"+category+"','"+MonitorName+"','"+MonitorId+"','"+stateString+"','"+Timestamp.valueOf(f.format(CreatedDateTime))+"')";
	    	JDBCForSQL.savaLog(sql);	
		}
}
