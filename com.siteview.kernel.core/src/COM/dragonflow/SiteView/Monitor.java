/*
 * 
 * Created on 2005-2-15 10:46:30
 *
 * Monitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Monitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.algorithms.Filtering;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.Api.Alert;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.reportPage;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.StandardMonitor.NTCounterMonitor;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, MonitorGroup, RuleGroupIs, Rule,
// SubGroup, AtomicMonitor, ScheduleManager, Platform,
// SiteViewGroup, Portal, MasterConfig

public abstract class Monitor extends SiteViewObject {

    public Monitor() {
        cachedSocketSession = null;
        partOfGlobalDepends = false;
        computeGroupCount = false;
    }

    public abstract String getHostname();

    public void setProperty(StringProperty stringproperty, String s)
            throws NullPointerException {
        if ((stringproperty == pLastUpdate || stringproperty == pForceRefresh)
                && getOwner() != null)
            getOwner().setProperty(stringproperty, s);
        super.setProperty(stringproperty, s);
    }

    public static boolean isContentError(long l) {
        return l == (long) kURLNoStatusError
                || l == (long) kURLContentMatchError
                || l == (long) kURLContentErrorFound
                || l == (long) kURLUnknownError
                || l == (long) kURLContentElementMissing
                || l == (long) kURLContentChangedError
                || l == (long) kXMLElementMatchError
                || l == (long) kXMLElementNotFoundError
                || l == (long) kXMLFormatError || l == 302L || l == 303L
                || l == 307L || l == 301L || l == 400L || l == 500L
                || l == 501L;
    }

    public static boolean isContentMatchStatus(long l) {
        return l == (long) kURLok || l == (long) kXMLFormatError
                || l == (long) kXMLElementMatchError
                || l == (long) kXMLElementNotFoundError;
    }

    public static boolean isErrorMatchStatus(long l) {
        return l == (long) kURLContentErrorFound;
    }

    public static String lookupStatus(long l) {
        Object obj = statusMapping.get(Long.toString(l));
        String s;
        if (obj == null)
            s = "unknown error (" + l + ")";
        else
            s = (String) obj;
        return s;
    }

    public static boolean isKnownStatus(long l) {
        return statusMapping.get(Long.toString(l)) != null;
    }

    public String getSchedule() {
        ScheduleManager schedulemanager = ScheduleManager.getInstance();
        return schedulemanager.getSchedule(this);
    }

    public boolean isOnAbsoluteSchedule() {
        return getSchedule().startsWith("*");
    }

    public boolean isOnAbsoluteSchedule(String s) {
        return s.startsWith("*");
    }

    public boolean isScheduleEnabled() {
        return ScheduleProperty.isEnabled(getSchedule());
    }

    public boolean isScheduleEnabled(String s) {
        return ScheduleProperty.isEnabled(s);
    }

    public boolean isAlertTemporarilyDisabled() {
        String s = getProperty(pAlertDisabled);
        if (s.length() > 0) {
            int i = s.indexOf(";");
            int j = s.indexOf("*");
            if (i > 0) {
                if (j == -1 || j != -1 && i < j) {
                    Date date = TextUtils.stringToDate(s.substring(0, i));
                    Date date3 = TextUtils.stringToDate(s.substring(i + 1));
                    if (date3.getTime() > Platform.timeMillis()
                            && date.getTime() < Platform.timeMillis())
                        return true;
                } else {
                    Date date1 = TextUtils.stringToDate(s);
                    if (date1.getTime() > Platform.timeMillis())
                        return true;
                }
            } else {
                Date date2 = TextUtils.stringToDate(s);
                if (date2.getTime() > Platform.timeMillis())
                    return true;
            }
        }
        return false;
    }

    public boolean isAcknowledged() {
        String s = getProperty(pAcknowledged);
        return s.length() > 0;
    }

    public boolean isDisabled() {
        return whyDisabled().length() > 0;
    }

    public String whyDisabled() {
        String s = isGloballyDisabled();
        if (s.length() == 0 && (MonitorGroup) getOwner() != null)
            s = ((MonitorGroup) getOwner()).whyGroupDisabled();
        if (s.length() == 0) {
            String s1 = getProperty(pDisabledDescription);
            if (getProperty(pDisabled).length() > 0) {
                s = "disabled manually";
                if (s1.length() > 0)
                    s = s + " , " + s1;
            } else {
                String s2 = getProperty(pTimedDisable);
                if (s2.length() > 0)
                    if (s2.indexOf(";") >= 0) {
                        int i = s2.indexOf(";");
                        String s4 = s2.substring(0, i);
                        String s5 = s2.substring(i + 1);
                        Date date1 = TextUtils.stringToDate(s5);
                        Date date2 = TextUtils.stringToDate(s4);
                        if (date1 != null
                                && date1.getTime() > Platform.timeMillis()
                                && date2 != null
                                && date2.getTime() <= Platform.timeMillis()) {
                            s = "disabled until "
                                    + TextUtils.prettyDate(date1,
                                            getSettingAsLong("_timeOffset"));
                            if (s1.length() > 0)
                                s = s + " , " + s1;
                        }
                    } else {
                        Date date = TextUtils.stringToDate(s2);
                        if (date.getTime() > Platform.timeMillis()) {
                            s = "disabled until "
                                    + TextUtils.prettyDate(date,
                                            getSettingAsLong("_timeOffset"));
                            if (s1.length() > 0)
                                s = s + " , " + s1;
                        }
                    }
                if (s.length() == 0) {
                    String s3 = getSchedule();
                    if (s3 != null && s3.length() > 0
                            && !isOnAbsoluteSchedule(s3)
                            && !isScheduleEnabled(s3))
                        s = DISABLED_BY_SCHEDULE;
                }
            }
            if (s.length() == 0 && s1.indexOf("Disabled due to Skips >") >= 0)
                s = s1;
        }
        return s;
    }

    public static void clearGlobalDepends() {
        resetGlobalDependsMonitor = true;
    }

    /**
     * 
     * 
     * @return
     */
    public String isGloballyDisabled() {
        SiteViewGroup siteviewgroup;
        String s = this.getSetting("_globalDependsOn");
        if (s == null) {
            LogManager.log("Error",
                    "***Global depends: globalDependsOn==null: ID is " + s);
            return "";
        }
        siteviewgroup = SiteViewGroup.currentSiteView();
        if (siteviewgroup == null) {
            LogManager.log("Error",
                    "***Global depends: siteView==null: ID is " + s);
            return "";
        }

        synchronized (Monitor.globalDependsLock) {
            if (Monitor.globalDependsMonitor == null
                    || Monitor.resetGlobalDependsMonitor) {
                if (s.length() > 0) {
                    Monitor.globalDependsMonitor = (Monitor) siteviewgroup
                            .getElement(s);
                    if (Monitor.globalDependsMonitor == null) {
                        LogManager
                                .log("Error",
                                        "Global depends on monitor missing: ID is "
                                                + s);
                        return "";
                    } else {
                        LogManager.log("RunMonitor", "Global depends active, "
                                + s);
                        resetGlobalDependsMonitor = false;
                    }
                } else {
                    return "";
                }
            }
        }

        if (Monitor.globalDependsMonitor == null) {
            LogManager.log("Error", "Global depends on monitor missing: ID is "
                    + s);
            return "";
        }

        String s1 = globalDependsMonitor.getProperty(pGroupID);
        String s2 = globalDependsMonitor.getProperty(pRunning);
        String s3 = globalDependsMonitor.getProperty(pCategory);
        String s4 = globalDependsMonitor.getProperty(pLastCategory);
        if (s1 == null || s2 == null || s3 == null || s4 == null) {
            LogManager.log("Error",
                    "***Global depends monitor property is null: ID is " + s);
            return "";
        }

        String s5 = getProperty(pGroupID);
        if (s5 == null) {
            LogManager.log("Error", "***Global depends: groupID==null: ID is "
                    + s);
            return "";
        }
        partOfGlobalDepends = CGI.isRelated(s5, s1);

        if (!partOfGlobalDepends) {
            String s6 = siteviewgroup.getSetting("_globalDependsCondition");
            if (s2.length() > 0 && s3.equalsIgnoreCase("nodata"))
                wasGloballyDisabled = !s4.equalsIgnoreCase(s6);
            else
                wasGloballyDisabled = !s3.equalsIgnoreCase(s6);
            if (wasGloballyDisabled)
                return "disabled globally";
        }
        return "";
    }

    public static String lookupStatusHelp(long l) {
        Object obj = statusHelpMapping.get(Long.toString(l));
        String s;
        if (obj == null)
            s = "No more information is available for this error (" + l + ")";
        else
            s = (String) obj;
        return s;
    }

    public String diagnostic(String s, int i) {
        String s1 = DIAGNOSTIC_HEADER;
        s1 = s1 + Platform.FILE_NEWLINE + lookupStatusHelp(i)
                + Platform.FILE_NEWLINE;
        if (i == kURLTimeoutError || i == kURLNoRouteToHostError
                || i == kURLSecurityMismatch || i == kURLNoConnectionError
                || i == kURLBadHostNameError)
            s1 = s1 + diagnosticPing(s, i);
        s1 = s1 + "";
        return s1;
    }

    public String diagnosticPing(String s, int i) {
        String s1 = "";
        byte byte0 = 5;
        int ai[] = Platform.ping(s, 5000, byte0, 64);
        int j = ai[0];
        int k = ai[1];
        float f = 0.0F;
        if (k > 0)
            f = (float) j / (float) k / 1000F;
        if (k == byte0)
            s1 = s1
                    + Platform.FILE_NEWLINE
                    + "A ping of the internet connection to "
                    + s
                    + " was ok (average of "
                    + TextUtils.floatToString(f, 2)
                    + " seconds)."
                    + Platform.FILE_NEWLINE
                    + Platform.FILE_NEWLINE
                    + "This suggests that the problem may be a server application that has stopped running"
                    + Platform.FILE_NEWLINE
                    + "or is too busy to handle requests."
                    + Platform.FILE_NEWLINE;
        else if (k > 0)
            s1 = s1
                    + Platform.FILE_NEWLINE
                    + "A ping of the internet connection to "
                    + s
                    + " succeeded on only "
                    + k
                    + " of "
                    + byte0
                    + " attempts (average of "
                    + TextUtils.floatToString(f, 2)
                    + " seconds)."
                    + Platform.FILE_NEWLINE
                    + Platform.FILE_NEWLINE
                    + "This suggests that the problem may be a network that is too busy to transfer all the packets."
                    + Platform.FILE_NEWLINE + diagnosticTraceRoute(s);
        else
            s1 = s1
                    + Platform.FILE_NEWLINE
                    + "A ping of the internet connection to "
                    + s
                    + " was not successful."
                    + Platform.FILE_NEWLINE
                    + Platform.FILE_NEWLINE
                    + "This suggests that the problem may be a failure of some piece of the network or the server"
                    + Platform.FILE_NEWLINE
                    + "(it also may be that a firewall is blocking ping access to this server)"
                    + Platform.FILE_NEWLINE + diagnosticTraceRoute(s);
        return s1;
    }

    public String diagnosticTraceRoute(String s) {
        String s1 = "";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s2 = siteviewgroup.getSetting("_tracerouteCommand");
        Array array = Platform.traceRoute(s, s2);
        s1 = s1
                + Platform.FILE_NEWLINE
                + "Trace Route results for "
                + s
                + Platform.FILE_NEWLINE
                + Platform.FILE_NEWLINE
                + "This trace shows you the path to "
                + s
                + "."
                + Platform.FILE_NEWLINE
                + "Each line shows how long it took to get to the next step along the path."
                + Platform.FILE_NEWLINE
                + "If the trace does not end at "
                + s
                + ", then the last line shows the last step along the path."
                + Platform.FILE_NEWLINE
                + "The times are in milliseconds -- a large change shows which step is the slowest part of the connection."
                + Platform.FILE_NEWLINE + Platform.FILE_NEWLINE;
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();) {
            String s3 = (String) enumeration.nextElement();
            s3 = s3.replace('\n', ' ');
            s1 = s1 + "  " + s3 + Platform.FILE_NEWLINE;
        }

        return s1;
    }

    public static Monitor createMonitor(HashMap hashmap, String s)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        if (s.length() > 0)
            hashmap.put("__portalServerID", s);
        return (Monitor) createObject(hashmap, false, cMonitorPackages);
    }

    public void preprocessValuesTable(HashMap hashmap) {
        hashmap.remove("__portalServerID");
        super.preprocessValuesTable(hashmap);
    }

    public boolean isPropertyExcluded(StringProperty stringproperty,
            HTTPRequest httprequest) {
        if (stringproperty == pTemplateID) {
            Array array = PropertiedObject.getTemplateConfigFileList();
            return array.size() == 0;
        } else {
            return super.isPropertyExcluded(stringproperty, httprequest);
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pSchedule) {
            Vector vector = new Vector();
            vector.addElement("");
            vector.addElement("every day, all day");
            HashMap hashmap;
            for (Enumeration enumeration = CGI.getValues(cgi.getSettings(),
                    "_additionalSchedule"); enumeration.hasMoreElements(); vector
                    .addElement(TextUtils.getValue(hashmap, "_name"))) {
                String s1 = (String) enumeration.nextElement();
                hashmap = TextUtils.stringToHashMap(s1);
                vector.addElement("_id=" + TextUtils.getValue(hashmap, "_id"));
            }

            return vector;
        }
        if (scalarproperty == pTemplateID) {
            String s = "";
            if (Platform.isPortal() && Portal.isPortalID(getFullID()))
                s = Portal.getServerID(getFullID());
            return getTemplateList(s);
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public static String getWorstCategory(String s, String s1) {
        Integer integer = (Integer) categoryMap.get(s);
        Integer integer1 = (Integer) categoryMap.get(s1);
        if (integer == null) {
            LogManager.log("Error", "Unknown category: " + s);
            return s1;
        }
        if (integer1 == null) {
            LogManager.log("Error", "Unknown category: " + s1);
            return s;
        }
        if (integer.intValue() > integer1.intValue())
            return s;
        else
            return s1;
    }

    public static boolean isMonitorFrame(HashMap hashmap) {
        return hashmap.get("_class") != null;
    }

    public static boolean isReportFrame(HashMap hashmap) {
        return hashmap.get("startHour") != null
                && hashmap.get("precision") != null;
    }

    public static boolean isDynamicFrame(HashMap hashmap) {
        return hashmap.get("_set") != null && hashmap.get("_oid") != null;
    }

    public String getAddPage() {
        return "monitor";
    }

    public String getTestURL() {
        return null;
    }

    public String getTestTitle() {
        return "Tools";
    }

    /**
     * 
     */
    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        Enumeration enumeration = (Enumeration) hashmap.values("_alertCondition");
        computeGroupCount = false;
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            Rule rule = Rule.stringToAction(s);
            if (rule != null) {
                addElement(rule);
                setComputeGroupCounts(rule);
            }
        } 
        
        Enumeration enumeration1 = (Enumeration) hashmap.values("_classifier");
        while (enumeration1.hasMoreElements()) {
            Object obj = enumeration1.nextElement();
            if (obj instanceof Array) {
                Array array = (Array) obj;
                int i = 0;
                while (i < array.size()) {
                    String s2 = (String) array.get(i);
                    Rule rule1 = Rule.stringToClassifier(s2);
                    if (rule1 != null)
                        addElement(rule1);
                    i++;
                }
            } else if (obj instanceof String) {
                String s1 = (String) obj;
                Rule rule2 = Rule.stringToClassifier(s1);
                if (rule2 != null)
                    addElement(rule2);
            }
        } 
    }

    protected void setComputeGroupCounts(Rule rule) {
        String s = rule.getProperty(Rule.pExpression);
        if (s.indexOf("group.monitorsInGroup") != -1
                || s.indexOf("group.monitorsInError") != -1
                || s.indexOf("group.maxErrorCount") != -1) {
            computeGroupCount = true;
            s = TextUtils.replaceString(s, "group.monitorsInGroup",
                    "monitorsInGroup");
            s = TextUtils.replaceString(s, "group.monitorsInError",
                    "monitorsInError");
            s = TextUtils.replaceString(s, "group.maxErrorCount",
                    "maxErrorCount");
            rule.setProperty(Rule.pExpression, s);
        }
    }

    public void signalMonitor() {
    }

    public void initializeTemplate() {
        initializeTemplate(getTreeSetting("__template"));
    }

    protected void startMonitor() {
        initializeTemplate();
    }

    protected void stopMonitor() {
    }

    public String getHistoryParam(String s, String s1) {
        return "monitors=" + s + "+" + s1;
    }

    protected void message(String s) {
        LogManager.log("RunMonitor", s);
    }

    public Enumeration getMonitors() {
        Array array = getElementsOfClass("COM.dragonflow.SiteView.Monitor",
                false);
        return (Enumeration) array.iterator();
    }

    public int countMonitors() {
        return countMonitors(null);
    }

    public int countMonitors(String s) {
        return countMonitorsOfClass("", s);
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        if (stringproperty == null)
            return "";
        String s = stringproperty.printString();
        if (flag && (stringproperty instanceof NumericProperty)) {
            NumericProperty numericproperty = (NumericProperty) stringproperty;
            String s1 = numericproperty.getUnits();
            if (s1 != null && s1.length() > 0)
                s = s + "(" + s1 + ")";
        }
        return s;
    }

    public String GetPropertyLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty, false);
    }

    public int countMonitorsOfClass(String s) {
        return countMonitorsOfClass(s, null);
    }

    public int countMonitorsOfClass(String s, String s1) {
        return getMonitorsOfClass(s, s1).size();
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @return
     */
    public Array getMonitorsOfClass(String s, String s1) {
        Array array = new Array();
        Enumeration enumeration;
        if (s1 != null && s1.length() > 0) {
            HashMap hashmap = new HashMap();
            Array array1 = new Array();
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                    .getElementByID(s1);
            if (monitorgroup != null) {
                for (Enumeration enumeration1 = monitorgroup.getMonitors(); enumeration1
                        .hasMoreElements();) {
                    Monitor monitor1 = (Monitor) enumeration1.nextElement();
                    if (monitor1.getProperty("_class").equals("SubGroup"))
                        MonitorGroup.getSubgroup(monitor1, hashmap);
                    else
                        array1.add(monitor1);
                }

                for (Enumeration enumeration2 = (Enumeration) hashmap.keys(); enumeration2
                        .hasMoreElements();) {
                    String s2 = I18N.toDefaultEncoding(((Monitor) enumeration2
                            .nextElement()).getProperty("_group"));
                    MonitorGroup monitorgroup1 = (MonitorGroup) siteviewgroup
                            .getElementByID(s2);
                    Enumeration enumeration3 = monitorgroup1.getMonitors();
                    while (enumeration3.hasMoreElements()) {
                        Monitor monitor2 = (Monitor) enumeration3.nextElement();
                        if (!monitor2.getProperty("_class").equals("SubGroup"))
                            array1.add(monitor2);
                    }
                }

            } else {
                LogManager.log("RunMonitor", "Unable to retrieve group " + s1);
            }
            enumeration =  (Enumeration) array1.iterator();
        } else {
            enumeration = getMonitors();
        }
        
        while (enumeration.hasMoreElements()) {
            Monitor monitor = (Monitor) enumeration.nextElement();
            if (s.length() == 0 || monitor.getClassProperty("class").equals(s)) {
                array.add(monitor);
            }
        } 
        return array;
    }

    public SiteViewObject getParent() {
        return getOwner();
    }

    public Enumeration getReports() {
        Array array = getElementsOfClass(
                "COM.dragonflow.SiteView.HistoryReport", false);
        return (Enumeration) array.iterator();
    }

    public Enumeration getDynamics() {
        Array array = getElementsOfClass(
                "COM.dragonflow.SiteView.VirtualMachine", false);
        return (Enumeration) array.iterator();
    }

    public Enumeration getRules(int i) {
        String s = getSetting("_noCheckDefaultThresholds");
        boolean flag = s.length() > 0;
        if (flag)
            if (s.toLowerCase().trim().equals("true"))
                flag = false;
            else if (s.toLowerCase().trim().equals("false"))
                flag = true;
            else
                flag = false;
        return getRules(i, flag);
    }

    public Enumeration getParentActionRules() {
        SiteViewObject siteviewobject = getOwner();
        if (owner instanceof MonitorGroup)
            return ((MonitorGroup) siteviewobject).getParentActionRules();
        else
            return cEmpty(Enumeration) array.iterator();
    }

    /**
     * 
     * 
     * @param i
     * @param flag
     * @return
     */
    protected Enumeration getRules(int i, boolean flag) {
        boolean flag1 = i != 1 || flag;
        Array array = getElementsOfClass("COM.dragonflow.SiteView.Rule", true,
                flag1);
        Array array1 = (Array) Filtering.select(array, new RuleGroupIs(i));
        if (i == 1 && !flag) {
            Enumeration enumeration =  (Enumeration) array1.iterator();
            Hashtable hashtable = new Hashtable();
            Rule rule;
            for (; enumeration.hasMoreElements(); hashtable.put(rule
                    .getProperty(Rule.pAction), "seen"))
                rule = (Rule) enumeration.nextElement();

            Array array2 = (Array) getClassProperty("elements");
            if (array2 != null) {
                Enumeration enumeration1 =  (Enumeration) array2.iterator();
                while (enumeration1.hasMoreElements()) {
                    Object obj = enumeration1.nextElement();
                    if (obj instanceof Rule) {
                        Rule rule1 = (Rule) obj;
                        if (rule1.getRuleGroup() == 1)
                            if (rule1.isDefaultRule) {
                                if (hashtable.get(rule1
                                        .getProperty(Rule.pAction)) == null)
                                    array1.add(rule1);
                            } else {
                                array1.add(rule1);
                            }
                    }
                }
            }
        } else if (i == 2) {
            Enumeration enumeration2 = getParentActionRules();
            if (enumeration2.hasMoreElements()) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                int j = -1;
                int k = 0;
                do {
                    if (k >= array1.size())
                        break;
                    Rule rule2 = (Rule) array1.get(k);
                    if (rule2.getOwner() == siteviewgroup) {
                        j = k;
                        break;
                    }
                    k++;
                } while (true);
                if (j == -1)
                    j = array1.size();
                for (int l = 0; enumeration2.hasMoreElements(); l++) {
                    Rule rule3 = (Rule) enumeration2.nextElement();
                    array1.insert(j + l, rule3);
                }

            }
        }
        return  (Enumeration) array1.iterator();
    }

    protected void removeAllRules() {
        Array array = getElementsOfClass("COM.dragonflow.SiteView.Rule", false);
        Rule rule;
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration
                .hasMoreElements(); removeElement(rule))
            rule = (Rule) enumeration.nextElement();

    }

    void runRules(int i, Monitor monitor) {
        runRules(i, monitor, null);
    }

    /**
     * 
     * 
     * @param i
     * @param monitor
     * @param s
     */
    void runRules(int i, Monitor monitor, String s) {
        if (rtConfig == null) {
            rtConfig = SiteViewGroup.loadRTConfig();
            bDisableRules = TextUtils.getValue(rtConfig, "_disableRules")
                    .equalsIgnoreCase("true");
        }
        if (AtomicMonitor.alertDebug)
            LogManager.log("RunMonitor", "alertDebug: "
                    + AtomicMonitor.alertDebugId + "Rules Disabled?: '"
                    + bDisableRules + "'");
        if (bDisableRules)
            return;
        Enumeration enumeration = getRules(i);
        Array array = new Array();
        Array array1 = new Array();
        if (i == 1
                && (SiteViewGroup.currentSiteView().getSetting("_errorOnly")
                        .length() > 0
                        || SiteViewGroup.currentSiteView().getSetting(
                                "_NTCounterErrorOnly").length() > 0 /*|| TopazManager
                        .getInstance().getTopazServerSettings().isConnected()
                        && (TopazManager.getInstance().getTopazServerSettings()
                                .getTopazIntegrationVersion() == 0 || supportTopazIndividualCounterQuality())*/)) {
            Enumeration enumeration2 = getRules(i);
            while (enumeration2.hasMoreElements()) {
                Rule rule3 = (Rule) enumeration2.nextElement();
                boolean flag1 = rule3.match(monitor);
                String s1 = rule3.getProperty(Rule.pExpression);
                String s2 = rule3.getProperty(Rule.pAction);
                String as[] = TextUtils.tokenize(s1);
                if (!as[0].equalsIgnoreCase("always")) {
                    if (s2.indexOf("error") >= 0 && flag1) {
                        monitor.propErrorStatusMap.put(as[0],
                                new Boolean(flag1));
                    }
                    else if (s2.indexOf("warning") >= 0 && flag1) {
                        monitor.propWarningStatusMap.put(as[0], new Boolean(
                                flag1));
                    }
                    else {
                        monitor.propGoodStatusMap.put(as[0], new Boolean(flag1));
                    }
                }
            } 
        }
        
        while (enumeration.hasMoreElements()) {
            Rule rule = (Rule) enumeration.nextElement();
            boolean flag = rule.match(monitor);
            if (AtomicMonitor.alertDebug)
                LogManager.log("RunMonitor", "alertDebug: "
                        + AtomicMonitor.alertDebugId + " rule check: "
                        + rule.getProperty(pOwnerID) + " CON="
                        + monitor.getProperty(pName) + "  "
                        + rule.getProperty(Rule.pExpression) + "  "
                        + rule.getProperty(Rule.pAction));
            if (flag) {
                if (AtomicMonitor.alertDebug)
                    LogManager.log("RunMonitor", "alertDebug: "
                            + AtomicMonitor.alertDebugId + " rule match: "
                            + rule.getProperty(pOwnerID) + " CON="
                            + monitor.getProperty(pName) + "  "
                            + rule.getProperty(Rule.pExpression) + "  "
                            + rule.getProperty(Rule.pAction));
                if (array.size() == 0)
                    array.add(rule);
                else if (!rule.getStopOnMatch())
                    array.add(rule);
                if (i == 1)
                    if (array1.size() == 0)
                        array1.add(rule);
                    else if (!onlyInvokeRuleAppliedForFirstViolator())
                        array1.add(rule);
                if (rule.getStopOnMatch()
                        && onlyInvokeRuleAppliedForFirstViolator())
                    break;
            }
            if (AtomicMonitor.alertDebug)
                LogManager.log("RunMonitor", "alertDebug: "
                        + AtomicMonitor.alertDebugId + " ---End of Check--- ");
        } 
        
        enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            Rule rule1 = (Rule) enumeration.nextElement();
            rule1.doAction(monitor);
            if (s != null) {
                if (AtomicMonitor.alertDebug)
                    LogManager.log("RunMonitor", "alertDebug: "
                            + AtomicMonitor.alertDebugId
                            + " SENDING ALERT rule alert increment: "
                            + rule1.getFullID() + ", expression: "
                            + rule1.getProperty(Rule.pExpression));
                IncrementAlertProperties(rule1.getFullID(), s);
            }
        }
        
        Rule rule2;
        for (Enumeration enumeration1 = (Enumeration) array1.iterator(); enumeration1
                .hasMoreElements(); ruleApplied(rule2))
            rule2 = (Rule) enumeration1.nextElement();

    }

    protected boolean onlyInvokeRuleAppliedForFirstViolator() {
        return true;
    }

    public Enumeration getClassifiers() {
        return getRules(1, true);
    }

    public Enumeration getActionRules() {
        return getRules(2);
    }

    public void runClassifiers(Monitor monitor) {
        if (monitor.hasValue(pBaselineDate)) {
            if (AtomicMonitor.alertDebug)
                LogManager.log("RunMonitor", "alertDebug: "
                        + AtomicMonitor.alertDebugId + " Baseline info found.");
            StringProperty stringproperty = null;
            Enumeration enumeration = monitor.getStatePropertyObjects(false);
            if (enumeration.hasMoreElements())
                stringproperty = (StringProperty) enumeration.nextElement();
            String s = monitor.getProperty(stringproperty);
            if (AtomicMonitor.alertDebug)
                LogManager.log("RunMonitor", "alertDebug: "
                        + AtomicMonitor.alertDebugId
                        + " Baseline property found: '" + s + "'");
            if ((stringproperty instanceof NumericProperty)
                    && TextUtils.isNumber(s)) {
                String s1 = monitor.getProperty(pBaselineMean);
                String s2 = monitor.getProperty(pBaselineStdDev);
                float f = TextUtils.toFloat(s);
                float f1 = TextUtils.toFloat(s2);
                float f2 = TextUtils.toFloat(s1);
                if ((double) (f2 + f1) > 0.0D) {
                    float f3 = f / (f2 + f1);
                    monitor.setProperty(pNumStdDev, f3);
                }
                if ((double) f2 > 0.0D) {
                    float f4 = (float) ((double) (f / f2) * 100D - 100D);
                    monitor.setProperty(pNumPercent, f4);
                }
            }
        }
        runRules(1, monitor);
    }

    public void runActionRules(Monitor monitor, String s) {
        if (AtomicMonitor.alertDebug)
            LogManager.log("RunMonitor", "alertDebug: "
                    + AtomicMonitor.alertDebugId
                    + " isAlertTemporarilyDisabled?: '"
                    + isAlertTemporarilyDisabled() + "'");
        if (isAlertTemporarilyDisabled()) {
            return;
        } else {
            runRules(2, monitor, s);
            return;
        }
    }

    public void ResetCategoryProperties(String s) {
        String s1 = s + "Count";
        String s2 = s + "TimeSinceFirst";
        setProperty(s1, String.valueOf(0));
        if (s.equals("error"))
            setProperty("warningCount", String.valueOf(0));
        if (s.equals("warning"))
            setProperty("errorCount", String.valueOf(0));
        setProperty(s2, String.valueOf(Platform.timeMillis()));
        Array array = getCurrentPropertyNames();
        for (int i = 0; i < array.size(); i++) {
            String s3 = (String) array.get(i);
            if (s3.indexOf("AlertCount") != -1)
                unsetProperty(s3);
            if (s3.indexOf("TimeSinceAlert") != -1)
                unsetProperty(s3);
        }

    }

    public void IncrementCategoryProperties(String s, String s1) {
        synchronized (countLock) {
            String s2 = s1 + "Count";
            if (!s1.equals(s))
                ResetCategoryProperties(s1);
            incrementProperty(s2);
        }
    }

    public String getProperty(String s) throws NullPointerException {
        boolean flag = false;
        String s1;
        if (s.equals("timeSinceUpdate")) {
            flag = true;
            s1 = getProperty("last");
        } else {
            s1 = super.getProperty(s);
            if (s.indexOf("TimeSinceFirst") != -1)
                flag = true;
            else if (s.indexOf("TimeSinceAlert") != -1
                    && !s.endsWith("TimeSinceAlert"))
                flag = true;
        }
        if (flag) {
            long l = (Platform.timeMillis() - TextUtils.toLong(s1)) / 1000L;
            if (l < 0L)
                l = 0L;
            s1 = "" + l;
        }
        return s1;
    }

    public void IncrementAlertProperties(String s, String s1) {
        synchronized (countLock) {
            String s2 = s1 + "AlertCount" + s;
            String s3 = s1 + "TimeSinceAlert" + s;
            setProperty(s3, String.valueOf(Platform.timeMillis()));
            incrementProperty(s2);
        }
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pSchedule) {
            if (s.length() == 0) {
                StringBuffer stringbuffer = new StringBuffer();
                s = ScheduleProperty.requestToScheduleString(httprequest,
                        stringbuffer);
                if (stringbuffer.length() > 0)
                    hashmap.put(stringproperty, stringbuffer.toString());
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pCategory);
        array.add(pOwnerID);
        array.add(pName);
        array.add(pStateString);
        array.add(pID);
        return array;
    }

    public String defaultTitle() {
        String s = (String) getClassProperty("target");
        String s1 = "";
        if (s != null)
            s1 = getProperty(s);
        if (s1.length() == 0)
            return (String) getClassProperty("title");
        else
            return getClassProperty("title") + ": " + s1;
    }

    public String getMeasurement(StringProperty stringproperty) {
        return getMeasurement(stringproperty, 0L);
    }

    public String getMeasurement(StringProperty stringproperty, long l) {
        String s = getClassPropertyString("class");
        if (s.length() > 0) {
            String s1 = getSetting("_measurementProperty" + s);
            if (s1.length() > 0) {
                StringProperty stringproperty1 = getPropertyObject(s1);
                if (stringproperty1 != null)
                    stringproperty = stringproperty1;
            }
        }
        if (stringproperty instanceof NumericProperty) {
            float f = TextUtils.toFloat(getProperty(stringproperty));
            float f1 = stringproperty.getDisplayMaximum(this);
            if (f1 <= 0.0F)
                if (l > 0L)
                    f1 = l;
                else
                    f1 = StringProperty.closestPowerOfTen(f);
            int i = Math.round((f / f1) * 10F);
            if (i > 10)
                i = 10;
            if (i < 0)
                i = 0;
            return String.valueOf(i);
        } else {
            return "0";
        }
    }

    public boolean dependsOnAreOK(boolean flag) {
        return true;
    }

    public String progressArt() {
        String s = "";
        if (isAlertTemporarilyDisabled()) {
            String s1 = getProperty(pAlertDisabled);
            String s2 = "";
            int i = s1.indexOf(";");
            int j = s1.indexOf("*");
            if (i > 0 && (j == -1 || j != -1 && i < j))
                s1 = s1.substring(i + 1);
            j = s1.indexOf("*");
            if (j > 0) {
                s2 = " [" + s1.substring(j + 1) + "]";
                s1 = s1.substring(0, j);
            }
            s = s
                    + "<IMG BORDER=0 SRC=/SiteView/htdocs/artwork/alertDisable.gif ALT=\"alerts disabled until ";
            s = s + s1 + s2;
            s = s + "\">";
        }
        if (getProperty(pRunning).length() != 0)
            s = s
                    + "<IMG BORDER=0 SRC=/SiteView/htdocs/artwork/running.gif ALT=\"+\">";
        return s;
    }

    public static String getGroupDetailLink(HTTPRequest httprequest,
            MonitorGroup monitorgroup) {
        return CGI.getGroupDetailURL(httprequest, I18N.UnicodeToString(
                monitorgroup.getProperty(pID), I18N.nullEncoding()));
    }

    public static String getGaugeHTML(HTTPRequest httprequest, Monitor monitor) {
        String s = monitor.getProperty(pMeasurement);
        if (s.length() == 0)
            return "/SiteView/htdocs/artwork/empty55.gif";
        else
            return MonitorGroup.getGaugeArt(s);
    }

    protected void printTableCategoryEntry(PrintWriter printwriter,
            HTTPRequest httprequest, boolean flag, boolean flag1, String s,
            String s1) {
        String s2 = "";
        String s3 = "";
        String s4 = getProperty(pGroupID);
        s4 = HTTPRequest.encodeString(I18N.UnicodeToString(s4, I18N
                .nullEncoding()));
        String s5 = HTTPRequest.encodeString(I18N.UnicodeToString(
                getProperty(pID), I18N.nullEncoding()));
        String s6 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        if (httprequest.actionAllowed("_monitorDisable")
                || httprequest.actionAllowed("_groupDisable")) {
            String s7;
            String s8;
            String s9;
            if (this instanceof SubGroup) {
                String s10 = getProperty("_name");
                s7 = isDisabled() ? "Enable" : "Disable";
                String s11 = I18N.toDefaultEncoding(getProperty(pGroupID));
                try {
                    Array array = CGI.ReadGroupFrames(s11, httprequest);
                    HashMap hashmap = CGI.findMonitor(array, s5);
                    s10 = TextUtils.getValue(hashmap, "_group");
                } catch (Exception exception) {
                    System.out
                            .println("***Couldn't get subgroup file name! parent="
                                    + s11);
                }
                s8 = "&group="
                        + HTTPRequest.encodeString(I18N.UnicodeToString(s10,
                                I18N.nullEncoding()));
                s9 = getProperty("_name");
            } else {
                s7 = !flag && !flag1 ? "Disable" : "Enable";
                s8 = "&monitor=" + s4 + "+" + s5;
                s9 = "Group+Detail";
            }
            s2 = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation="
                    + s7
                    + s8
                    + "&account="
                    + httprequest.getAccount()
                    + s6
                    + "&returnURL="
                    + s4
                    + "&returnLabel="
                    + s9
                    + "&disabled="
                    + flag
                    + "&alertTempDisabled="
                    + flag1
                    + "&category="
                    + s1
                    + "&fromDetail=true" + ">";
            s3 = "</A>";
        }
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s2
                + progressArt() + "<IMG BORDER=0 SRC="
                + MonitorGroup.getCategoryArt(s1) + " ALT=\"" + s + "\">" + s3
                + "</TD>");
    }

    public static Array getMonitorAlerts(HTTPRequest httprequest, String s,
            String s1, Array array) {
        try {
            Array array1 = CGI.ReadGroupFrames(s, httprequest);
            HashMap hashmap = CGI.findMonitor(array1, s1);
            Array array2;
            for (Enumeration enumeration = (Enumeration) hashmap.values("_alertCondition"); enumeration.hasMoreElements(); array.add(array2)) {
                array2 = Platform.split('\t', (String) enumeration
                        .nextElement());
                array2.add(s + " " + s1);
            }

        } catch (Exception exception) {
            System.out.println("error reading group frames : " + exception);
        }
        return array;
    }

    public static boolean getGroupAlerts(String s, Array array) {
        boolean flag = false;
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Array array1;
        for (Enumeration enumeration = siteviewgroup
                .getMultipleValues("_alertCondition"); enumeration
                .hasMoreElements(); array.add(array1)) {
            array1 = Platform.split('\t', (String) enumeration.nextElement());
            array1.add("_master");
        }

        if (!s.equalsIgnoreCase("SiteView") && !s.equalsIgnoreCase("_master")) {
            Monitor monitor = (Monitor) siteviewgroup.getElement(s);
            Array array2;
            for (Enumeration enumeration1 = monitor
                    .getMultipleValues("_alertCondition"); enumeration1
                    .hasMoreElements(); array.add(array2)) {
                array2 = Platform.split('\t', (String) enumeration1
                        .nextElement());
                array2.add(s);
            }

        }
        return flag;
    }

    public static String getAlertArt(String s) {
        return "/SiteView/htdocs/artwork/alerticon" + s + ".gif";
    }

    protected void printTableAlertEntry(PrintWriter printwriter,
            HTTPRequest httprequest, Array array) {
        StringBuffer stringbuffer = createAlertIconLink(true, httprequest);
        printwriter.print(stringbuffer.toString());
    }

    protected StringBuffer createAlertIconLink(boolean flag,
            HTTPRequest httprequest) {
        String s = getProperty(pGroupID);
        String s1 = HTTPRequest.encodeString(I18N.UnicodeToString(
                getProperty(pID), I18N.nullEncoding()));
        String s2 = "";
        boolean flag1 = false;
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        Vector vector2 = new Vector();
        String s3 = s;
        if (!s1.equals("_config"))
            s3 = s3 + " " + s1;
        flag1 = filterAlertsByTarget(Alert.getInstance().getAlertsByTarget(
                "_master"), s3, vector);
        filterAlertsByTarget(Alert.getInstance().getAlertsByTarget(s), s3,
                vector1);
        vector2 = Alert.getInstance().getAlertsByTarget(s3);
        Vector vector3 = null;
        if (flag)
            vector3 = vector2;
        else
            vector3 = vector1;
        i = vector3.size();
        if (flag) {
            stringbuffer.append("<td align=\"center\" bgcolor=\"#000000\">");
            s2 = httprequest.getValue("_health").length() <= 0 ? ""
                    : "&_health=true";
        } else {
            stringbuffer.append("<td>");
        }
        Object obj = null;
        String s4 = "";
        String s5 = "";
        String s6 = "";
        String s7 = "";
        if (i == 1) {
            Alert alert = (Alert) vector3.elementAt(0);
            s5 = alert.getExpressionStr() + " : " + alert.getActionStr();
            s7 = getAlertArt("1");
            s6 = "Edit";
            s4 = "&group=" + s + "&monitor=" + s1 + "&id=" + alert.getIDStr();
        } else if (i > 1) {
            s5 = i + " specific alert(s) exist for this element";
            s7 = getAlertArt("2");
            s6 = "List";
        } else if (i == 0) {
            if (s.equals("_master") || s.equals("SiteView")) {
                if (vector.size() == 0) {
                    s7 = getAlertArt("0");
                    s5 = "no alerts configured for this element, click to add an alert";
                    s6 = "AddList";
                } else if (vector.size() == 1) {
                    Alert alert1 = (Alert) vector.elementAt(0);
                    s7 = getAlertArt("1");
                    s5 = alert1.getExpressionStr() + " : "
                            + alert1.getActionStr();
                    s6 = "Edit";
                    s4 = "&group=" + s + "&monitor=" + s1 + "&id="
                            + alert1.getIDStr();
                } else {
                    s7 = getAlertArt("2");
                    s5 = "alert(s) exist at siteview level containing this element";
                    s6 = "List";
                }
            } else if (vector1.size() == 0 && !flag1) {
                s7 = getAlertArt("0");
                s6 = "AddList";
                s5 = "no alerts configured for this element, click to add an alert";
            } else if (vector1.size() == 0 && flag1) {
                s7 = getAlertArt("H");
                s5 = "alert(s) exist at siteview level containing this element";
                s6 = "List";
            } else if (vector1.size() >= 1) {
                s7 = getAlertArt("H");
                s5 = "alert(s) exist at siteview level containing this element";
                if (!flag) {
                    s6 = "List";
                    if (vector1.size() == 1) {
                        Alert alert2 = (Alert) vector1.elementAt(0);
                        s7 = getAlertArt("1");
                        s5 = alert2.getExpressionStr() + " : "
                                + alert2.getActionStr();
                        s4 = "&group=" + s + "&monitor=" + s1 + "&id="
                                + alert2.getIDStr();
                        s6 = "Edit";
                    }
                } else {
                    s6 = "AddList";
                }
            } else {
                s7 = getAlertArt("0");
                s5 = "no alerts configured for this element, click to add an alert";
                s6 = "Add";
            }
        } else {
            s5 = "no alerts configured for this element, click to add an alert";
            s6 = "AddList";
        }
        String s8 = "";
        if (httprequest.actionAllowed("_alertEdit")) {
            s8 = "<img border=\"0\" src=\"" + s7 + "\" alt=\"" + s5 + "\"> ";
            stringbuffer
                    .append("<a href=/SiteView/cgi/go.exe/SiteView?page=alert&operation="
                            + s6
                            + "&account="
                            + httprequest.getAccount()
                            + s2
                            + s4 + ">" + s8);
        } else {
            stringbuffer.append(s8);
        }
        stringbuffer.append("</td>");
        return stringbuffer;
    }

    protected boolean filterAlertsByTarget(Vector vector, String s,
            Vector vector1) {
        boolean flag = false;
        for (int i = 0; i < vector.size(); i++) {
            Alert alert = (Alert) vector.elementAt(i);
            String s1 = alert.getIncludeFilter();
            if (!s1.equals("") && s1.indexOf(s) == -1)
                continue;
            if (vector1 != null)
                vector1.add(alert);
            flag = true;
        }

        return flag;
    }

    /**
     * 
     * 
     * @param flag
     * @param httprequest
     * @return
     */
    protected String printTableReportEntry(boolean flag, HTTPRequest httprequest) {
        String s = getProperty(pGroupID);
        s = HTTPRequest.encodeString(I18N.UnicodeToString(s, I18N
                .nullEncoding()));
        String s1 = HTTPRequest.encodeString(I18N.UnicodeToString(
                getProperty(pID), I18N.nullEncoding()));
        StringBuffer stringbuffer = new StringBuffer();
        String s2 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        Array array = reportPage.getReportFrames(httprequest.getAccount());
        int i = 0;
        Object obj = null;
        String s3 = "";
        String s4 = "title, report type, schedule";
        Object obj1 = null;
        for (int j = 0; j < array.size(); j++) {
            HashMap hashmap = (HashMap) array.get(j);
            Enumeration enumeration = (Enumeration) hashmap.values("monitors");
            while (enumeration.hasMoreElements()) {
                String as[] = TextUtils.split((String) enumeration
                        .nextElement(), " ");
                if (as.length == 2 && !flag) {
                    if (as[1].length() > 0 && as[1].equals(s)
                            && as[0].equals(s1)) {
                        s3 = (String) hashmap.get("id");
                        s4 = (String) hashmap.get("title") + " : "
                                + (String) hashmap.get("reportType") + " : "
                                + (String) hashmap.get("schedule");
                        i++;
                    }
                } else if (as.length == 1 && flag && as[0].length() > 0
                        && as[0].equals(s)) {
                    s3 = (String) hashmap.get("id");
                    s4 = (String) hashmap.get("title") + " : "
                            + (String) hashmap.get("reportType") + " : "
                            + (String) hashmap.get("schedule");
                    i++;
                }
            } 
        }

        String s5 = "1";
        boolean flag1 = httprequest.actionAllowed("_reportEdit")
                && httprequest.actionAllowed("_reportGenerate");
        if (i == 0)
            s5 = "0";
        else if (i == 1)
            s5 = "1";
        else
            s5 = "2";
        if (!flag)
            stringbuffer
                    .append("<td align=\"center\" valign=\"middle\" bgcolor=\"#000000\">");
        else
            stringbuffer.append("<td align=\"center\" valign=\"middle\">");
        if (i == 1) {
            String s6 = "<img border=\"0\" src=\""
                    + MonitorGroup.getReportArt(s5) + "\" alt=\"" + s4 + "\">";
            if (flag1)
                stringbuffer
                        .append("<a href=/SiteView/cgi/go.exe/SiteView?page=report&account="
                                + httprequest.getAccount()
                                + s2
                                + "&queryID="
                                + s3 + "&operation=Edit>" + s6);
            else
                stringbuffer.append(s6);
        } else if (i > 1) {
            String s7 = "<img border=\"0\" src=\""
                    + MonitorGroup.getReportArt(s5) + "\" alt=\"" + i
                    + " reports configured for this monitor\" > ";
            if (flag1)
                stringbuffer
                        .append("<a href=/SiteView/cgi/go.exe/SiteView?page=report&operation=List&account="
                                + httprequest.getAccount() + s2 + ">" + s7);
            else
                stringbuffer.append(s7);
        } else {
            String s8 = "<img border=\"0\" src=\""
                    + MonitorGroup.getReportArt(s5)
                    + "\" alt=\"no reports configured for this monitor, click to add an alert\"> ";
            if (flag1)
                stringbuffer
                        .append("<a href=/SiteView/cgi/go.exe/SiteView?page=report&operation=AddList&account="
                                + httprequest.getAccount() + s2 + ">" + s8);
            else
                stringbuffer.append(s8);
        }
        stringbuffer.append("</td>");
        return stringbuffer.toString();
    }

    public String getTableStatusString(HTTPRequest httprequest) {
        if (isDisabled())
            return whyDisabled();
        String s = getProperty(pStateString);
        float f = TextUtils.toFloat(getProperty(pBaselineMean));
        if (f > 0.0F)
            s = s + " (static baseline, " + f + ")";
        if ((this instanceof NTCounterMonitor) || (this instanceof URLMonitor)) {
            int i = TextUtils
                    .toInt(getProperty(NTCounterMonitor.pMonitorRunCount));
            if (i == 0)
                i = TextUtils.toInt(getProperty(URLMonitor.pMonitorRunCount));
            if (i != 0)
                s = s + " (rolling baseline)";
        }
        if (httprequest.getValue("maxStatusSize").length() > 0) {
            int j = TextUtils.toInt(httprequest.getValue("maxStatusSize"));
            if (j > 0 && s.length() > j)
                s = s.substring(0, j);
        }
        if (enableLinkInStatus())
            return s;
        else
            return escapeHTML(s);
    }

    public static String escapeHTML(String s) {
        if (s.indexOf('<') == -1 && s.indexOf('>') == -1)
            return s;
        StringBuffer stringbuffer = new StringBuffer(s.length());
        int i = 0;
        do {
            if (i >= s.length())
                break;
            char c = s.charAt(i++);
            if (TextUtils.isDoubleByte(c)) {
                stringbuffer.append(c);
                if (i < s.length())
                    stringbuffer.append(s.charAt(i++));
            } else if (c == '>')
                TextUtils.escapeChar(c, stringbuffer);
            else if (c == '<')
                TextUtils.escapeChar(c, stringbuffer);
            else
                stringbuffer.append(c);
        } while (true);
        return stringbuffer.toString();
    }

    protected void printTableStatusEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        boolean flag = isDisabled();
        String s = flag ? "nodata" : getProperty(pCategory);
        String s1 = getTableStatusString(httprequest);
        String s2 = getStatusStringDelimiterRegex();
        if (s2.length() > 0)
            s1 = s1.replaceAll(s2, "<BR>");
        if (s.equals("good")) {
            int i = getPropertyAsInteger(pGoodTextLimit);
            if (i > 0 && i < s1.length())
                s1 = s1.substring(0, i) + " ...";
            printwriter.println("<TD>" + s1 + "</TD>");
        } else {
            int j = getPropertyAsInteger(pErrorTextLimit);
            if (j > 0 && j < s1.length())
                s1 = s1.substring(0, j) + " ...";
            printwriter.println("<TD><B>" + s1 + "</B></TD>");
        }
    }

    public String getStatusStringDelimiterRegex() {
        if (statusStringDelimiter == null) {
            HashMap hashmap = MasterConfig.getMasterConfig();
            if (TextUtils.getValue(hashmap, "_noSplitStatusString").length() > 0)
                statusStringDelimiter = "";
            else
                statusStringDelimiter = ",";
        }
        return statusStringDelimiter;
    }

    public String getTableRecentLink(HTTPRequest httprequest) {
        String s = "";
        String s1 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        if (httprequest.actionAllowed("_monitorRecent")) {
            String s2 = I18N.toDefaultEncoding(getProperty(pGroupID));
            s2 = HTTPRequest.encodeString(s2);
            String s3 = I18N.toDefaultEncoding(getProperty(pID));
            String s4 = getHistoryParam(s3, s2);
            s = "/SiteView/cgi/go.exe/SiteView?page=adhocReport&" + s4
                    + "&account=" + httprequest.getAccount() + s1 + "&group="
                    + s2 + "&detailed=true";
        }
        return s;
    }

    protected void printTableNameEntry(PrintWriter printwriter,
            HTTPRequest httprequest, String s) {
        String s1 = getProperty(pMonitorDescription);
        String s2 = getTableRecentLink(httprequest);
        if (s2.length() > 0)
            s = "<A HREF=" + s2 + " TARGET=reports>" + s + "</A>";
        printwriter.print("<TD ALIGN=LEFT>" + s + s1 + "</TD>");
    }

    protected void printTableRefreshEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        printwriter.print("<TD CELLVALIGN=MIDDLE>");
        String s = I18N.toDefaultEncoding(getProperty(pGroupID));
        s = HTTPRequest.encodeString(s);
        String s1 = I18N.toDefaultEncoding(getProperty(pID));
        String s2 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        long l = 0L;
        if (this instanceof AtomicMonitor)
            l = getPropertyAsLong(AtomicMonitor.pFrequency);
        boolean flag = whyDisabled().startsWith(DISABLED_BY_SCHEDULE);
        if (isDisabled() && !flag || l == 0L || !dependsOnAreOK(false)) {
            printwriter.print("&nbsp;");
        } else {
            boolean flag1 = flag || isOnAbsoluteSchedule();
            printwriter
                    .print("<a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=RefreshMonitor"
                            + s2
                            + "&refresh=true&"
                            + (flag1 ? "forceRefresh=true&" : "")
                            + "group="
                            + s
                            + "&"
                            + "id="
                            + s1
                            + "&"
                            + "account="
                            + httprequest.getAccount()
                            + (flag1 ? ">Run Once</a>" : ">Refresh</a>"));
        }
        printwriter.print("</TD>");
    }

    protected void printTableEditEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = I18N.toDefaultEncoding(getProperty(pGroupID));
        s = HTTPRequest.encodeString(s);
        String s1 = getProperty(pID);
        String s2 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        printwriter
                .print("<TD ALIGN=CENTER><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Edit&group="
                        + s
                        + "&id="
                        + s1
                        + "&account="
                        + httprequest.getAccount() + s2 + ">Edit</A></TD>");
    }

    protected void printTableDeleteEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        String s1 = I18N.toDefaultEncoding(getProperty(pGroupID));
        s1 = HTTPRequest.encodeString(s1);
        String s2 = getProperty(pID);
        printwriter
                .print("<TD ALIGN=CENTER><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Delete&group="
                        + s1
                        + "&id="
                        + s2
                        + "&account="
                        + httprequest.getAccount() + s + ">X</A>" + "</TD>");
    }

    public String getTableMoreLink(HTTPRequest httprequest) {
        String s = "";
        String s1 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        if (httprequest.actionAllowed("_monitorTools")) {
            s = getTestURL();
            String s2 = I18N.toDefaultEncoding(getProperty(pGroupID));
            s2 = HTTPRequest.encodeString(s2);
            if (s != null)
                s = s + "&group=" + s2 + "&account=" + httprequest.getAccount()
                        + s1;
            else
                s = "";
        }
        return escapeHTML(s);
    }

    protected void printTableMoreEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = getTableMoreLink(httprequest);
        if (s.length() > 0)
            printwriter.print("<TD><A HREF=" + s + ">"
                    + escapeHTML(getTestTitle()) + "</A></TD>");
        else
            printwriter.print("<TD>&nbsp;</TD>");
    }

    protected void printTableAckEntry(PrintWriter printwriter,
            HTTPRequest httprequest, boolean flag, String s, String s1) {
        String s2 = "";
        String s3 = "";
        String s4 = HTTPRequest.encodeString(I18N.UnicodeToString(
                getProperty(pGroupID), I18N.nullEncoding()));
        String s5 = getProperty(pID);
        String s8 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        String s6;
        String s7;
        if (!flag) {
            s6 = "Acknowledge";
            s7 = "Acknowledge Monitor State";
            if (httprequest.actionAllowed("_monitorAcknowledge")) {
                s2 = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation="
                        + s6
                        + "&monitor="
                        + s4
                        + "+"
                        + s5
                        + "&account="
                        + httprequest.getAccount()
                        + s8
                        + "&returnURL="
                        + s4
                        + "&returnLabel=" + "Group+Detail" + ">";
                s3 = "</A>";
            }
        } else {
            s6 = "AcknowledgeClear";
            s7 = "Acknowledged by: " + s1 + ", " + s;
            if (httprequest.actionAllowed("_monitorAcknowledge")) {
                s2 = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation="
                        + s6
                        + "&monitor="
                        + s4
                        + "+"
                        + s5
                        + "&account="
                        + httprequest.getAccount()
                        + s8
                        + "&returnURL="
                        + s4
                        + "&returnLabel=" + "Group+Detail" + ">";
                s3 = "</A>";
            }
        }
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s2
                + "<IMG BORDER=0 SRC=" + MonitorGroup.getAckArt(s6) + " ALT=\""
                + s7 + "\">" + s3 + "</TD>");
    }

    protected void printTableUpdatedEntry(PrintWriter printwriter, String s) {
        printwriter.print("<TD><SMALL>" + s + "</SMALL></TD>");
    }

    protected void printTableGaugeEntry(PrintWriter printwriter, String s) {
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s + "</TD>");
    }

    public int getCostInLicensePoints() {
        return 1;
    }

    public boolean enableLinkInStatus() {
        return false;
    }

    public String getTopazInternalName() {
        return getProperty(pID);
    }

    public String getTopazDisplayName() {
        return getProperty(pName);
    }

    public String getTopazDescription() {
        return getProperty(pDescription);
    }

    public String getTopazParent() {
        return null;
    }

    protected boolean supportTopazIndividualCounterQuality() {
        return false;
    }

    protected void ruleApplied(Rule rule) {
    }

    public void resetCounters() {
    }

    public MonitorGroup group;

    public static final String NO_ALERT_ICON = "0";

    public static final String SINGLE_ALERT_ICON = "1";

    public static final String MULTI_ALERT_ICON = "2";

    public static final String PHANTOM_ALERT_ICON = "H";

    public static HashMap categoryMap;

    public static String FILTERED_CATEGORY;

    public static String DISABLED_CATEGORY;

    public static String NODATA_CATEGORY;

    public static String GOOD_CATEGORY;

    public static String WARNING_CATEGORY;

    public static String ERROR_CATEGORY;

    public static String WORST_CATEGORY;

    public static String FAILURE = "failure";

    public static String NON_FAILURE = "nonfailure";

    public static String DISABLED_BY_SCHEDULE = "disabled by schedule";

    static String cMonitorPackages[] = { "COM.dragonflow.StandardMonitor.",
            "COM.dragonflow.SiteView.", "CustomMonitor." };

    public static final String DIAGNOSTIC_HEADER;

    public static final String DIAGNOSTIC_FOOTER = "";

    public static int kSOAPFaultError = -983;

    public static int kSSL2NotFoundError = -984;

    public static int kSSLNotFoundError = -985;

    public static int kXMLElementNotFoundError;

    public static int kXMLFormatError;

    public static int kXMLElementMatchError;

    public static int kURLContentErrorFound;

    public static int kURLContentElementMissing;

    public static int kURLNoStatusError;

    public static int kMonitorSpecificError;

    public static int kURLNoRouteToHostError;

    public static int kDLLCrashedError;

    public static int kURLContentChangedError;

    public static int kURLTimeoutError;

    public static int kURLBadHostNameError;

    public static int kURLNoConnectionError;

    public static int kURLContentMatchError;

    public static int kURLUnknownError = -1000;

    public static int kDNSIPAddressMismatch;

    public static int kURLRemoteMonitoringError;

    public static int kURLSecurityMismatch;

    public static int kURLCertificateExpired;

    public static int kURLCertificateNameError;

    public static int kURLMissingClientCertificate;

    public static int kURLInvalidCA;

    public static int kURLok;

    protected static final int nPointsPerMonitor = 1;

    protected static final int nPointsPerURL = 1;

    protected static final int nPointsPerMetric = 1;

    static HashMap statusMapping;

    static HashMap statusHelpMapping;

    static HashMap rtConfig = null;

    static boolean bDisableRules = false;

    private static String statusStringDelimiter = null;

    public SocketSession cachedSocketSession;

    static Monitor globalDependsMonitor = null;

    static boolean resetGlobalDependsMonitor = false;

    static Long globalDependsLock = new Long(0L);

    public static boolean wasGloballyDisabled = false;

    public boolean partOfGlobalDepends;

    public static Object countLock = new Object();

    public static StringProperty pName;

    public static StringProperty pMonitorDescription;

    public static StringProperty pDescription;

    public static StringProperty pDisabled;

    public static StringProperty pAlertDisabled;

    public static StringProperty pTimedDisable;

    public static StringProperty pDisabledDescription;

    public static StringProperty pVerifyError;

    public static StringProperty pSchedule;

    public static StringProperty pGoodTextLimit;

    public static StringProperty pErrorTextLimit;

    public static StringProperty pTemplateID;

    public static StringProperty pCategory;

    public static StringProperty pRunning;

    public static StringProperty pLastCategory;

    public static StringProperty pStateString;

    public static StringProperty pMeasurement;

    public static StringProperty pRawMeasurement;

    public static StringProperty pErrorCount;

    public static StringProperty pWarningCount;

    public static StringProperty pErrorDuration;

    public static StringProperty pWarningDuration;

    public static StringProperty pGoodCount;

    public static StringProperty pLastUpdate;

    public static StringProperty pForceRefresh;

    public static StringProperty pDiagnosticText;

    public static StringProperty pDiagnosticTraceRoute;

    public static StringProperty pSiteViewLogfilePath;

    public static StringProperty pEncoding = new StringProperty("_encoding", "");

    public static StringProperty pSiteViewLogName;

    public static StringProperty pURLLogName;

    public static StringProperty pErrorLogName;

    public static StringProperty pAlertLogName;

    public static StringProperty pBaselineMean;

    public static StringProperty pBaselineStdDev;

    public static StringProperty pBaselineDate;

    public static StringProperty pNumStdDev;

    public static StringProperty pNumPercent;

    public static StringProperty pAcknowledged;

    public static StringProperty pAcknowledgedDelay;

    public static StringProperty pAcknowledgedState;

    public static StringProperty pAcknowledgeComment;

    public static StringProperty pAcknowledgeUser;

    public static StringProperty pAcknowledgeUserIP;

    public static StringProperty pAcknowledgeAlertDisabled;

    public boolean computeGroupCount;

    static {
        FILTERED_CATEGORY = "filtered";
        DISABLED_CATEGORY = "disabled";
        NODATA_CATEGORY = "nodata";
        GOOD_CATEGORY = "good";
        WARNING_CATEGORY = "warning";
        ERROR_CATEGORY = "error";
        WORST_CATEGORY = "worst";
        DIAGNOSTIC_HEADER = "More information:" + Platform.FILE_NEWLINE;
        kXMLElementNotFoundError = -986;
        kXMLFormatError = -987;
        kXMLElementMatchError = -988;
        kURLContentErrorFound = -989;
        kURLContentElementMissing = -990;
        kURLNoStatusError = -991;
        kMonitorSpecificError = -992;
        kURLNoRouteToHostError = -993;
        kDLLCrashedError = -994;
        kURLContentChangedError = -995;
        kURLTimeoutError = -996;
        kURLBadHostNameError = -997;
        kURLNoConnectionError = -998;
        kURLContentMatchError = -999;
        kDNSIPAddressMismatch = -1001;
        kURLRemoteMonitoringError = -1002;
        kURLSecurityMismatch = 12157;
        kURLCertificateExpired = 12037;
        kURLCertificateNameError = 12038;
        kURLMissingClientCertificate = 12044;
        kURLInvalidCA = 12045;
        kURLok = 200;
        statusMapping = new HashMap();
        statusMapping.put(Integer.toString(kURLok), "ok");
        statusMapping.put(Integer.toString(201), "created");
        statusMapping.put(Integer.toString(202), "accepted");
        statusMapping.put(Integer.toString(203), "non-authoratative");
        statusMapping.put(Integer.toString(204), "no content");
        statusMapping.put(Integer.toString(205), "reset content");
        statusMapping.put(Integer.toString(206), "partial content");
        statusMapping.put(Integer.toString(301), "document moved");
        statusMapping.put(Integer.toString(302), "document moved");
        statusMapping.put(Integer.toString(303), "document moved");
        statusMapping.put(Integer.toString(307), "document moved");
        statusMapping.put(Integer.toString(400), "bad request");
        statusMapping.put(Integer.toString(401), "unauthorized");
        statusMapping.put(Integer.toString(403), "forbidden");
        statusMapping.put(Integer.toString(404), "not found");
        statusMapping.put(Integer.toString(407),
                "proxy authentication required");
        statusMapping.put(Integer.toString(500), "server error");
        statusMapping.put(Integer.toString(501), "not implemented");
        statusMapping.put(Integer.toString(502),
                "proxy received invalid response");
        statusMapping.put(Integer.toString(503), "server busy");
        statusMapping.put(Integer.toString(504), "proxy timed out");
        statusMapping.put(Integer.toString(kMonitorSpecificError), "error");
        statusMapping.put(Integer.toString(kURLContentElementMissing),
                "content element missing");
        statusMapping.put(Integer.toString(kURLContentChangedError),
                "content changed");
        statusMapping.put(Integer.toString(kURLTimeoutError),
                "timed out reading");
        statusMapping
                .put(Integer.toString(kXMLFormatError), "XML format error");
        statusMapping.put(Integer.toString(kXMLElementNotFoundError),
                "XML element not found");
        statusMapping.put(Integer.toString(kXMLElementMatchError),
                "XML element value mismatch");
        statusMapping.put(Integer.toString(kURLContentMatchError),
                "content match error");
        statusMapping.put(Integer.toString(kURLContentErrorFound),
                "content error found");
        statusMapping.put(Integer.toString(kURLNoConnectionError),
                "unable to connect to server");
        statusMapping.put(Integer.toString(kURLBadHostNameError),
                "unknown host name");
        statusMapping.put(Integer.toString(kDLLCrashedError),
                "internal error in WinInet library");
        statusMapping.put(Integer.toString(kURLNoRouteToHostError),
                "unable to reach server");
        statusMapping.put(Integer.toString(kURLNoStatusError),
                "no status in reply from server");
        statusMapping.put(Integer.toString(kDNSIPAddressMismatch),
                "ip address does not match");
        statusMapping
                .put(Integer.toString(kURLSecurityMismatch),
                        "insufficient encryption, probably needs 128 bit Internet Explorer");
        statusMapping.put(Integer.toString(kURLCertificateExpired),
                "secure certificate expired");
        statusMapping.put(Integer.toString(kURLCertificateNameError),
                "secure certificate name does not match host name");
        statusMapping.put(Integer.toString(kURLMissingClientCertificate),
                "requires client certificate authentification");
        statusMapping.put(Integer.toString(kURLInvalidCA),
                "certificate authority not registered in Internet Explorer");
        statusMapping.put(Integer.toString(kURLRemoteMonitoringError),
                "unable to connect to remote monitoring server");
        statusHelpMapping = new HashMap();
        statusHelpMapping
                .put(
                        Integer.toString(400),
                        "This message is returned by CGI scripts or other programs that create web pages."
                                + Platform.FILE_NEWLINE
                                + "It may also be returned by a web server that is having problems."
                                + Platform.FILE_NEWLINE
                                + "Check that the correct parameters are being passed."
                                + Platform.FILE_NEWLINE
                                + "Check the URL using a browser for additional information."
                                + Platform.FILE_NEWLINE
                                + "Check the web server error log for additional information."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(401),
                        "This message is returned when the web server requires authorization information."
                                + Platform.FILE_NEWLINE
                                + "Check that the correct username and password as being sent."
                                + Platform.FILE_NEWLINE
                                + "Check that your IP address is allowed access to the server."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(403),
                        "This message is returned by the web server when access to this URL is not allowed."
                                + Platform.FILE_NEWLINE
                                + "Check the web server configuration."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(404),
                        "This message is returned by the web server when it cannot find the URL requested."
                                + Platform.FILE_NEWLINE
                                + "Check that the URL name and spelling are correct."
                                + Platform.FILE_NEWLINE
                                + "Check that the file exists on the web server."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(407),
                        "This message is returned when the proxy server requires authorization information."
                                + Platform.FILE_NEWLINE
                                + "Check that the correct proxy username and password as being sent."
                                + Platform.FILE_NEWLINE
                                + "Check that your IP address is allowed access to the proxy."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(500),
                        "This message is returned by CGI scripts or other programs that create web pages."
                                + Platform.FILE_NEWLINE
                                + "It may also be returned by a web server that is having problems."
                                + Platform.FILE_NEWLINE
                                + "Check that the correct parameters are being passed."
                                + Platform.FILE_NEWLINE
                                + "Check the URL using a browser for additional information."
                                + Platform.FILE_NEWLINE
                                + "Check the web server error log for additional information."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(503),
                        "This message is returned when the web server it to busy to handle your request."
                                + Platform.FILE_NEWLINE
                                + "Check the CPU usage on your server."
                                + Platform.FILE_NEWLINE
                                + "Check for other processes that using resources."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLContentChangedError),
                        "This message is returned when the content of a URL changes."
                                + Platform.FILE_NEWLINE
                                + "Check the URL using a browser to view the new contents."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLContentElementMissing),
                        "This message is returned when a link, form, or frame is not found in the HTML on a page."
                                + Platform.FILE_NEWLINE
                                + "Check the HTML content for that step by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLContentMatchError),
                        "This message is returned when the match string is not found in the HTML content."
                                + Platform.FILE_NEWLINE
                                + "Check the HTML content by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLContentErrorFound),
                        "This message is returned when an error string is found in the HTML content."
                                + Platform.FILE_NEWLINE
                                + "Check the HTML content by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kXMLFormatError),
                        "This message is returned when a syntax problem is found in the XML content."
                                + Platform.FILE_NEWLINE
                                + "Check the XML content by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kXMLElementNotFoundError),
                        "This message is returned when an item is not found in the XML content."
                                + Platform.FILE_NEWLINE
                                + "Check the XML content by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kXMLElementMatchError),
                        "This message is returned when the value of an item in an XML document is incorrect"
                                + Platform.FILE_NEWLINE
                                + "Check the XML content by using the View Source command from a browser."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLBadHostNameError),
                        "This message is returned when DNS cannot find an IP address for a name."
                                + Platform.FILE_NEWLINE
                                + "Check the network connection to the DNS server."
                                + Platform.FILE_NEWLINE
                                + "Check that the DNS server for that domain is running."
                                + Platform.FILE_NEWLINE
                                + "Check that DNS is properly configured on the SiteView machine."
                                + Platform.FILE_NEWLINE
                                + "Check that the name exists in the configuration for that domain."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLNoConnectionError),
                        "This message is returned when the server does not accept the connection."
                                + Platform.FILE_NEWLINE
                                + "Check that the server and server process are running."
                                + Platform.FILE_NEWLINE
                                + "Check the load on the server."
                                + Platform.FILE_NEWLINE
                                + "Check the load on the network."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping.put(Integer.toString(kURLTimeoutError),
                "This message is returned when a connection is made but not completed."
                        + Platform.FILE_NEWLINE
                        + "Check the load on the network."
                        + Platform.FILE_NEWLINE
                        + "Check the load on the server."
                        + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kDLLCrashedError),
                        "This message is returned when a Windows library has an internal problem."
                                + Platform.FILE_NEWLINE
                                + "If this problem occurs repeatedly, please report it to "
                                + Platform.supportEmail + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLNoRouteToHostError),
                        "This message is returned when there is not a network path that reaches the server."
                                + Platform.FILE_NEWLINE
                                + "Check the network connection to the server."
                                + Platform.FILE_NEWLINE
                                + "Check that firewalls and routers allow access to the server."
                                + Platform.FILE_NEWLINE
                                + "Check the load on the network."
                                + Platform.FILE_NEWLINE
                                + "Check that the server is running."
                                + Platform.FILE_NEWLINE);
        statusHelpMapping
                .put(
                        Integer.toString(kURLNoStatusError),
                        "This message is returned when the server returns an incomplete response."
                                + Platform.FILE_NEWLINE
                                + "It may be caused by a busy server or network."
                                + Platform.FILE_NEWLINE
                                + "It may also be caused by a problem in a CGI script."
                                + Platform.FILE_NEWLINE
                                + "Check the URL using a browser for additional information."
                                + Platform.FILE_NEWLINE
                                + "Check the load on the network."
                                + Platform.FILE_NEWLINE
                                + "Check the load on the server."
                                + Platform.FILE_NEWLINE);
        pName = new StringProperty("_name", "");
        pName.setDisplayText("Title",
                "title that should appear in the Monitor table (optional)");
        pName.setParameterOptions(true, 102, false);
        pDisabled = new BooleanProperty("_disabled");
        pDisabled.setDisplayText("Disable",
                "temporarily disable monitor sampling and alerting");
        pDisabled.setParameterOptions(true, 1, true);
        pAlertDisabled = new StringProperty("_alertDisabled");
        pAlertDisabled.setConfigurable(true);
        pVerifyError = new BooleanProperty("_verifyError");
        pVerifyError
                .setDisplayText(
                        "Verify Error",
                        "if the monitor detects an error, immediately perform the check again to verify the error.\n<TR><TD>WARNINGS: </TD></TR>\n<TR><TD>(1) Activating this feature will cause the logging of this monitor's initial error state to be skipped. Potentially valuable statistical information will be lost. </TD></TR>\n<TR><TD>(2) This feature causes immediate re-execution of the monitor. Promiscuous use of this feature may delay the scheduled runs of other monitors. </TD></TR>\n<TR><TD>(3) This feature is independent from the Update every (on errors) feature below.</TD></TR>");
        pVerifyError.setParameterOptions(true, 102, true);
        pSchedule = new ScheduleProperty("_schedule", "");
        pSchedule
                .setDisplayText(
                        "Schedule",
                        "schedule for the monitor to be enabled - for example, \"weekdays, 9-6\" enables the monitor to run from 9am to 6pm, Monday - Friday");
        pSchedule.setParameterOptions(true, 104, true);
        pMonitorDescription = new StringProperty("_monitorDescription", "");
        pMonitorDescription
                .setDisplayText(
                        "Monitor Description",
                        "additional description of monitor that appears on Monitor Detail page (optional)");
        pMonitorDescription.setParameterOptions(true, 105, true);
        pDescription = new StringProperty("_description", "");
        pDescription
                .setDisplayText(
                        "Report Description",
                        "additional description of monitor that appears on Reports and in monitor popup info (optional)");
        pDescription.setParameterOptions(true, 106, true);
        pTemplateID = new ScalarProperty("__template", "");
        pTemplateID.setDisplayText("Monitor Template",
                "monitor template to use for this monitor");
        pTemplateID.setParameterOptions(true, 107, true);
        pTimedDisable = new StringProperty("_timedDisable", "");
        pTimedDisable.setConfigurable(true);
        pDisabledDescription = new StringProperty("_disabledDescription", "");
        pDisabledDescription.setConfigurable(true);
        pGoodTextLimit = new StringProperty("_goodTextLimit", "0");
        pErrorTextLimit = new StringProperty("_errorTextLimit", "0");
        pSiteViewLogfilePath = new StringProperty("_siteviewLogfilePath");
        pSiteViewLogName = new StringProperty("_siteViewLogName");
        pURLLogName = new StringProperty("_urlLogName");
        pErrorLogName = new StringProperty("_errorLogName");
        pAlertLogName = new StringProperty("_alertLogName");
        pStateString = new StringProperty("stateString", "no data");
        pCategory = new StringProperty("category", "nodata");
        pRunning = new StringProperty("monitorRunning", "");
        pLastCategory = new StringProperty("lastCategory", "nodata");
        pMeasurement = new NumericProperty("measurement");
        pRawMeasurement = new NumericProperty("rawMeasurement", "0");
        pErrorCount = new NumericProperty("errorCount", "0");
        pWarningCount = new NumericProperty("warningCount", "0");
        pGoodCount = new NumericProperty("goodCount", "0");
        pErrorDuration = new StringProperty("errorTimeSinceFirst");
        pWarningDuration = new StringProperty("warningTimeSinceFirst");
        pLastUpdate = new NumericProperty("last", "0", "milliseconds");
        pForceRefresh = new StringProperty("forceRefresh");
        pDiagnosticText = new StringProperty("diagnosticText", "");
        pDiagnosticTraceRoute = new StringProperty("diagnosticTraceRoute", "");
        pBaselineMean = new NumericProperty("_baselineMean", "0");
        pBaselineStdDev = new NumericProperty("_baselineStdDev", "0");
        pBaselineDate = new NumericProperty("_baselineDate");
        pNumStdDev = new NumericProperty("_numStdDev", "0");
        pNumStdDev.setIsThreshold(true);
        pNumStdDev.setLabel("# std deviations from baseline");
        pNumPercent = new NumericProperty("_numPercent", "0");
        pNumPercent.setIsThreshold(true);
        pNumPercent.setLabel("% difference from baseline");
        pAcknowledged = new StringProperty("acknowledged");
        pAcknowledgedDelay = new NumericProperty("acknowledgedDelay", "0",
                "milliseconds");
        pAcknowledgedState = new StringProperty("acknowledgedState");
        pAcknowledgeComment = new StringProperty("acknowledgeComment");
        pAcknowledgeUser = new StringProperty("acknowledgeUser");
        pAcknowledgeUserIP = new StringProperty("acknowledgeUserIP");
        pAcknowledgeAlertDisabled = new StringProperty(
                "acknowledgeAlertDisabled");
        StringProperty astringproperty[] = { pCategory, pLastCategory,
                pRunning, pName, pDisabled, pAlertDisabled, pTimedDisable,
                pVerifyError, pMonitorDescription, pDescription, pSchedule,
                pStateString, pMeasurement, pRawMeasurement, pErrorCount,
                pWarningCount, pErrorDuration, pWarningDuration, pGoodCount,
                pLastUpdate, pForceRefresh, pDiagnosticText,
                pDiagnosticTraceRoute, pSiteViewLogfilePath,
                pSiteViewLogName, pURLLogName, pErrorLogName, pAlertLogName,
                pTemplateID, pDisabledDescription, pGoodTextLimit,
                pErrorTextLimit, pBaselineMean, pBaselineStdDev, pBaselineDate,
                pNumStdDev, pNumPercent, pAcknowledged, pAcknowledgedState,
                pAcknowledgeComment, pAcknowledgedDelay, pAcknowledgeUser,
                pAcknowledgeUserIP, pAcknowledgeAlertDisabled };
        addProperties("COM.dragonflow.SiteView.Monitor", astringproperty);
        categoryMap = new HashMap();
        categoryMap.add(FILTERED_CATEGORY, new Integer(0));
        categoryMap.add(NODATA_CATEGORY, new Integer(1));
        categoryMap.add(DISABLED_CATEGORY, new Integer(2));
        categoryMap.add(GOOD_CATEGORY, new Integer(3));
        categoryMap.add(WARNING_CATEGORY, new Integer(4));
        categoryMap.add(ERROR_CATEGORY, new Integer(5));
        categoryMap.add(WORST_CATEGORY, new String(ERROR_CATEGORY));
    }
}
