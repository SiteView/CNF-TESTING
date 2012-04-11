/*
 * 
 * Created on 2005-2-15 10:37:08
 *
 * Action.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Action</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, RetryScheduler, Platform, SiteViewGroup,
// Monitor, AtomicMonitor, MonitorGroup, Rule,
// MasterConfig

public abstract class Action extends SiteViewObject {

    public Action() {
        triggerCount = 0;
        successCount = 0;
        state = 1;
        runType = 3;
        runCount = 0;
        maxRuns = 1;
        attemptDelay = 60000L;
        messageBuffer = null;
        kill = false;
        traceStream = null;
    }

    public void setUserData(String s, Object obj) {
        userData.put(s, obj);
    }

    public Object getUserData(String s) {
        return userData.get(s);
    }

    public void removeUserData(String s) {
        userData.remove(s);
    }

    public static Action createAction(String s) {
        return createAction(s, true);
    }

    public static Action createActionObject(String s) {
        return createAction(s, false);
    }

    public static Action createAction(String s, boolean flag) {
        Array array = Platform.split(' ', s);
        String s1 = (String) array.popFront();
        Action action = null;
        try {
            Class class1;
            try {
                class1 = Class.forName("COM.dragonflow.StandardAction." + s1);
            } catch (ClassNotFoundException classnotfoundexception1) {
                class1 = Class.forName("CustomAction." + s1);
            }
            action = (Action) class1.newInstance();
            if (flag)
                action.setArgs(array);
        } catch (ClassNotFoundException classnotfoundexception) {
            LogManager.log("Error", "Class not found " + s1);
        } catch (Exception exception) {
            System.err.println("Error instantiating " + s1);
            exception.printStackTrace();
        }
        return action;
    }

    void setKill(boolean flag) {
        kill = flag;
    }

    public abstract boolean execute();

    public void initializeFromArguments(Array array, HashMap hashmap) {
    }

    public String getActionString() {
        return "";
    }

    public String getActionDescription() {
        return "";
    }

    public boolean defaultsAreSet(HashMap hashmap) {
        return true;
    }

    public boolean showOptionalProperties() {
        return false;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule1) {
        rule = rule1;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor1) {
        monitor = monitor1;
    }

    public void setTraceStream(PrintWriter printwriter) {
        traceStream = printwriter;
    }

    public SiteViewObject getSettingsObject() {
        if (rule != null)
            return rule;
        if (monitor != null)
            return monitor;
        else
            return SiteViewGroup.currentSiteView();
    }

    public void appendProperty(StringBuffer stringbuffer,
            StringProperty stringproperty) {
        if (stringbuffer.length() > 0)
            stringbuffer.append(" ");
        stringbuffer.append(stringproperty.getName());
        stringbuffer.append("=");
        stringbuffer.append(getProperty(stringproperty));
    }

    public void setArgs(Array array) {
        int i = array.size();
        do {
            if (--i < 0)
                break;
            String s = (String) array.at(i);
            int j = s.indexOf('=');
            if (j > 0) {
                array.remove(i);
                String s1 = s.substring(0, j);
                String s2 = "";
                if (j + 1 < s.length())
                    s2 = s.substring(j + 1);
                addProperty(s1, s2);
            }
        } while (true);
        args = new String[array.size()];
        array.copyTo(args);
    }

    synchronized void trigger() {
        triggerCount++;
        boolean flag = false;
        switch (runType) {
        case 1: // '\001'
            flag = true;
            break;

        case 2: // '\002'
            if (triggerCount == 1)
                flag = true;
            break;

        case 3: // '\003'
            if (successCount == 0)
                flag = true;
            break;
        }
        if (flag && triggerCount > runCount) {
            state = 2;
            messageBuffer = new StringBuffer();
            if (execute()) {
                state = 4;
                successCount++;
            } else {
                state = 3;
            }
            if (state == 3 && runType == 3 && triggerCount < maxRuns)
                retryScheduler.schedulePeriodicAction(this, attemptDelay,
                        Platform.timeMillis());
            if (messageBuffer.length() > 0) {
                String s = messageBuffer.toString();
                String s1 = s
                        + ", "
                        + I18N.toDefaultEncoding(monitor
                                .getProperty(Monitor.pName)) + " "
                        + monitor.getProperty(SiteViewObject.pOwnerID) + " #"
                        + monitor.getProperty(AtomicMonitor.pSample);
                if (state == 3)
                    LogManager.log(getSetting(Monitor.pErrorLogName), s1);
                if (traceStream != null)
                    traceStream.println(TextUtils.escapeHTML(s1));
                LogManager.log("Progress", monitor
                        .getProperty(SiteViewObject.pOwnerID)
                        + "\t" + monitor.getProperty(Monitor.pName) + "\t" + s);
            }
            messageBuffer = null;
        }
    }

    protected String createMessage(StringBuffer stringbuffer, String s,
            String s1) {
        String s2 = "";
        try {
            String s3 = "";
            if (monitor != null) {
                String s4 = monitor.getSetting(MonitorGroup.pAccountName);
                if (s4 == null || s4.length() == 0)
                    s3 = Platform.getUsedDirectoryPath(s, monitor
                            .getProperty(Monitor.pGroupID))
                            + File.separator + s1;
                else
                    s3 = Platform.getUsedDirectoryPath(s, s4) + File.separator
                            + s1;
            } else {
                s3 = Platform.getRoot() + File.separator + s + File.separator
                        + s1;
            }
            stringbuffer.append(monitor.createFromTemplateFile(s3));
        } catch (FileNotFoundException filenotfoundexception) {
            s2 = "missing template: " + s1;
        } catch (IOException ioexception) {
            s2 = "error reading template: " + s1;
        }
        return s2;
    }

    public void logAlert(String s) {
        logAlert(s, true);
    }

    public void logAlert(String s, boolean flag) {
        LogManager.log(getSetting(Monitor.pAlertLogName), (flag ? I18N
                .toDefaultEncoding(s) : s)
                + Platform.FILE_NEWLINE);
        if (traceStream != null)
            traceStream.println(TextUtils.escapeHTML(s));
    }

    public String baseAlertLogEntry(String s, String s1, boolean flag) {
        return "alert"
                + Platform.FILE_NEWLINE
                + " alert-type: "
                + s
                + Platform.FILE_NEWLINE
                + (flag ? "" : " alert-failed: true" + Platform.FILE_NEWLINE)
                + (traceStream != null ? " alert-test: true"
                        + Platform.FILE_NEWLINE : "")
                + " alert-message: "
                + I18N.toDefaultEncoding(s1)
                + Platform.FILE_NEWLINE
                + " alert-monitor: "
                + I18N.toDefaultEncoding(monitor.getProperty(Monitor.pName))
                + Platform.FILE_NEWLINE
                + " alert-group: "
                + I18N.toDefaultEncoding(CGI.getGroupFullName(I18N
                        .toDefaultEncoding(monitor
                                .getProperty(SiteViewObject.pOwnerID))))
                + Platform.FILE_NEWLINE + " alert-id: " + rule.getFullID()
                + ":" + getClassProperty("class") + Platform.FILE_NEWLINE
                + " alert-monitor-id: " + monitor.getFullID() + ":"
                + monitor.getProperty(AtomicMonitor.pSample)
                + Platform.FILE_NEWLINE;
    }

    static void setupAlertSummaryFormat(String s) {
        boolean flag = false;
        int j = 0;
        do {
            int i = s.indexOf('<', j);
            if (i == -1)
                break;
            String s1 = s.substring(j, i);
            if (s1.length() > 0)
                summaryFormat.add(s1);
            j = s.indexOf('>', i + 1);
            if (j == -1)
                break;
            summaryFormat.add("alert-" + s.substring(i + 1, j));
            j++;
        } while (true);
        String s2 = s.substring(j);
        if (s2.length() > 0)
            summaryFormat.add(s2);
    }

    public String alertLogEntrySummary(HashMap hashmap) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < summaryFormat.size(); i++) {
            String s = (String) summaryFormat.at(i);
            if (s.startsWith("alert-"))
                stringbuffer.append(TextUtils.getValue(hashmap, s));
            else
                stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }

    public static boolean isBaseEntry(String s) {
        return baseEntries.get(s) != null;
    }

    public String toString() {
        return "generic SiteView action";
    }

    public static RetryScheduler retryScheduler;

    public static StringProperty pName;

    public static StringProperty pUIContext;

    public static StringProperty pUniqueId;

    public static final int kPending = 1;

    public static final int kRunning = 2;

    public static final int kFailed = 3;

    public static final int kSucceeded = 4;

    public static final int kRunEverytime = 1;

    public static final int kRunOnce = 2;

    public static final int kRunUntilSuccess = 3;

    public static String EQUALS_SUBTITUTE = "$eQ$";

    protected static Object snmpClass;

    public static HashMap userData = new HashMap();

    protected Monitor monitor;

    public String args[];

    protected Rule rule;

    protected int triggerCount;

    protected int successCount;

    protected int state;

    protected int runType;

    protected int runCount;

    protected int maxRuns;

    protected long attemptDelay;

    protected StringBuffer messageBuffer;

    protected boolean kill;

    PrintWriter traceStream;

    static HashMap baseEntries;

    static String DEFAULT_ALERT_SUMMARY_FORMAT;

    static Array summaryFormat = new Array();

    static {
        pName = new StringProperty("_name", "", "");
        pName.setParameterOptions(false, true, 100, false);
        pUIContext = new StringProperty("_UIContext", "", "");
        pUIContext.setParameterOptions(false, true, 101, false);
        pUniqueId = new StringProperty("_uniqueID", "", "");
        pUniqueId.setParameterOptions(false, true, 102, false);
        StringProperty astringproperty[] = { pName, pUIContext, pUniqueId };
        addProperties((COM.dragonflow.SiteView.Action.class).getName(),
                astringproperty);
        retryScheduler = new RetryScheduler();
        retryScheduler.startScheduler("Action Retry Scheduler");
        baseEntries = new HashMap();
        DEFAULT_ALERT_SUMMARY_FORMAT = "<type>: <message>";
        baseEntries.add("alert-type", "true");
        baseEntries.add("alert-message", "true");
        baseEntries.add("alert-monitor", "true");
        baseEntries.add("alert-group", "true");
        baseEntries.add("alert-id", "true");
        baseEntries.add("alert-monitor-id", "true");
        baseEntries.add("alert-failed", "true");
        baseEntries.add("alert-test", "true");
        String s = "";
        try {
            HashMap hashmap = MasterConfig.getMasterConfig();
            s = TextUtils.getValue(hashmap, "_alertSummaryFormat");
        } catch (Exception exception) {
        }
        if (s.length() == 0)
            s = DEFAULT_ALERT_SUMMARY_FORMAT;
        setupAlertSummaryFormat(s);
    }
}
