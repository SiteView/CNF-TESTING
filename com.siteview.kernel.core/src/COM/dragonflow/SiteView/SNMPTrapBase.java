/*
 * 
 * Created on 2005-2-16 16:54:17
 *
 * SNMPTrapBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SNMPTrapBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.FileLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Braf;
import COM.dragonflow.Utils.TextUtils;

import com.netaphor.snmp.GenericAddress;
import com.netaphor.snmp.Session;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, SNMPTrapListenerThread, MasterConfig, Platform,
// Rule, Monitor

public abstract class SNMPTrapBase extends AtomicMonitor {

    public static final String pMatchDesc[] = {
            "_match",
            null,
            null,
            "Content Match",
            "by default, all SNMP Traps received will be matched.  Optionally, enter the text to match in an SNMP Trap or a <a href=/SiteView/docs/regexp.htm>regular expression.</a>" };

    public static final String pAlertingDesc[] = {
            "_alerting",
            "",
            null,
            "Run Alerts",
            "How alerts for this monitor are triggered by the following options:  <TR><TD>(1) For <b>'for each SNMP Trap entry matched and report status'</b> the monitor triggers alerts for every matching SNMP trap entry found based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n<TR><TD>(2) For <b>'once, after all SNMP Traps have been checked'</b>, the traps received since the last monitor run are checked and alerts are triggered based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n" };

    public static final String pValueLabelsDesc[] = { "_valueLabels", "", null,
            "Match Value Labels",
            "Labels for the values matched on the script output, separated by a \",\"" };

    public static final String pLinesDesc[] = { "lineCount", null, "lines" };

    public static final String pMatchesDesc[] = { "matchCount", null, "matches" };

    public static final String pMatchDetailsDesc[] = { "matchDetails" };

    public static final String pLastAlertsPerMinuteDesc[] = {
            "lastAlertsPerMinute", "0", "matches/min" };

    public static final String pLastLinesPerMinuteDesc[] = {
            "lastLinesPerMinute", "0", "lines/min" };

    public static final String pLastModDateDesc[] = { "lastModDate" };

    public static final String pLastFilePositionDesc[] = { "lastFilePosition",
            "-1" };

    public static final String pLastMeasurementDesc[] = { "lastMeasurement",
            "0" };

    public static final String pMessageDesc[] = { "message" };

    public static final String pValueDesc[] = { "value" };

    public static final String pValue2Desc[] = { "value2" };

    public static final String pValue3Desc[] = { "value3" };

    public static final String pValue4Desc[] = { "value4" };

    static SNMPTrapListenerThread listener = null;

    public static String listenerError = null;

    public static String listenerStatus = "Receiving SNMP Traps is not active";

    HashMap labelsCache;

    public SNMPTrapBase() {
        labelsCache = null;
    }

    protected abstract StringProperty getValue2Property();

    protected abstract StringProperty getValue3Property();

    protected abstract StringProperty getValueProperty();

    protected abstract StringProperty getValue4Property();

    protected abstract StringProperty getAlertingProperty();

    protected abstract StringProperty getLastLinesPerMinuteProperty();

    protected abstract StringProperty getLastAlertsPerMinuteProperty();

    protected abstract StringProperty getLastFilePositionProperty();

    protected abstract StringProperty getMessageProperty();

    protected abstract StringProperty getLinesProperty();

    protected abstract StringProperty getMatchesProperty();

    protected abstract StringProperty getMatchDetailsProperty();

    protected abstract StringProperty getLastMeasurementProperty();

    protected abstract StringProperty getValueLabelsProperty();

    protected abstract StringProperty getMatchProperty();

    protected String getLogFileName() {
        return "log.config";
    }

    public static String getBasePropertyName(String as[]) {
        return as[0];
    }

    public static String getBasePropertyDefValue(String as[]) {
        return as[1];
    }

    public static String getBasePropertyLabel(String as[]) {
        return as[2];
    }

    public static String getBasePropertyTitle(String as[]) {
        return as[3];
    }

    public static String getBasePropertyDesc(String as[]) {
        return as[4];
    }

    public static Observable getTrapSubject() {
        return listener;
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        if (listener == null && listenerError == null) {
            try {
                HashMap hashmap1 = MasterConfig.getMasterConfig();
                listener = new SNMPTrapListenerThread();
                Session session = new Session();
                long l = getSettingAsLong("_snmpTrapListener");
                String s = TextUtils.getValue(hashmap1, "_snmpTrapListenerIP");
                InetAddress inetaddress = null;
                if (s.length() <= 0) {
                    inetaddress = InetAddress.getLocalHost();
                } else {
                    inetaddress = InetAddress.getByName(s);
                }
                GenericAddress genericaddress = null;
                long l1 = l;
                if (l1 == 0L) {
                    l1 = 162L;
                }
                String s1 = inetaddress.getHostAddress();
                if (l > 0L) {
                    genericaddress = new GenericAddress(s1 + ":" + l);
                } else {
                    genericaddress = new GenericAddress(s1);
                }
                boolean flag = session.enableTrapReception(genericaddress,
                        listener);
                if (!flag) {
                    listenerError = "Address in use. ";
                } else {
                    listenerStatus = "Receiving SNMP traps at address: "
                            + inetaddress + ", port: " + l1;
                    LogManager.log("RunMonitor", listenerStatus);
                }
                int i = getSettingAsLong("_maxAuxLogSize", 0xf4240);
                FileLogger filelogger = new FileLogger(getLogPath(), i);
                LogManager.registerLogger("SNMPTrap", filelogger);
            } catch (Exception exception) {
                listenerError = exception.getMessage();
            }
        }
        if (listenerError != null) {
            LogManager.log("Error", "Unable to receive SNMP traps, "
                    + listenerError);
        }
    }

    public boolean runOwnRules() {
        return getProperty(getAlertingProperty()).equals("each");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            if (getProperty(getValueLabelsProperty()).length() > 0) {
                String as[] = TextUtils.split(
                        getProperty(getValueLabelsProperty()), ",");
                labelsCache.add("value", as[0].trim());
                if (as.length >= 2) {
                    labelsCache.add("value2", as[1].trim());
                }
                if (as.length >= 3) {
                    labelsCache.add("value3", as[2].trim());
                }
                if (as.length >= 4) {
                    labelsCache.add("value4", as[3].trim());
                }
            } else {
                labelsCache.add("value", "value");
                labelsCache.add("value2", "value2");
                labelsCache.add("value3", "value3");
                labelsCache.add("value4", "value4");
            }
            Array array = getProperties();
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                StringProperty stringproperty = (StringProperty) enumeration
                        .nextElement();
                if (stringproperty.isThreshold()
                        && stringproperty.getName().indexOf("value") == -1) {
                    labelsCache.add(stringproperty.getLabel(), stringproperty
                            .getLabel());
                }
            }
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
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if (s1.length() != 0) {
            return s1;
        }
        if (flag) {
            return "";
        } else {
            return s;
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == getAlertingProperty()) {
            Vector vector = new Vector();
            vector.addElement("each");
            vector.addElement("for each SNMP Trap matched");
            vector.addElement("once");
            vector.addElement("once, after all SNMP Traps have been checked");
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public static String getLogPath() {
        return Platform.getRoot() + File.separator + "logs" + File.separator
                + "snmptrap.log";
    }

    public String getHostname() {
        return "SNMPTrapHost";
    }

    protected boolean update() {
        String s = getLogPath();
        long l = getPropertyAsLong(getLastMeasurementProperty());
        Object obj = null;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        float f = -1F;
        float f1 = -1F;
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = "";
        if (listenerError != null) {
            s1 = listenerError;
        } else {
            long l4 = getSettingAsLong("_SNMPTrapMonitorDetailsMax", 1000);
            long al[] = checkLog(s, stringbuffer, l4);
            if (al != null) {
                l1 = al[0];
                l2 = al[1];
                l3 = al[2];
            } else {
                s1 = "unable to read SNMP Trap log";
            }
        }
        setProperty(getMatchDetailsProperty(), stringbuffer.toString().replace(
                '\n', '^'));
        float f2 = (l3 - l) / 1000L;
        if (f2 > 0.0F) {
            f = 60F * ((float) l1 / f2);
            if (f < 0.0F) {
                f = 0.0F;
            }
            f1 = 60F * ((float) l2 / f2);
            if (f1 < 0.0F) {
                f1 = 0.0F;
            }
        }
        if (runOwnRules()) {
            setProperty(pCategory, GOOD_CATEGORY);
        }
        if (stillActive()) {
            synchronized (this) {
                if (s1.length() == 0) {
                    setProperty(getLastAlertsPerMinuteProperty(), String
                            .valueOf(f));
                    setProperty(getLastLinesPerMinuteProperty(), String
                            .valueOf(f1));
                    setProperty(getLastMeasurementProperty(), String
                            .valueOf(l3));
                    setProperty(getMatchesProperty(), l1);
                    setProperty(getLinesProperty(), l2);
                    setProperty(pMeasurement, getMeasurement(
                            getLastLinesPerMinuteProperty(), 10L));
                    setProperty(pStateString, l1 + " matches, "
                            + TextUtils.floatToString(f, 2) + " matches/min, "
                            + TextUtils.floatToString(f1, 2)
                            + " trap entries/min");
                } else {
                    setProperty(pStateString, s1);
                    setProperty(pNoData, "n/a");
                    setProperty(pMeasurement, 0);
                    setProperty(getLastAlertsPerMinuteProperty(), "n/a");
                    setProperty(getLastLinesPerMinuteProperty(), "n/a");
                    setProperty(getMatchesProperty(), "n/a");
                    setProperty(getLinesProperty(), "n/a");
                    setProperty(getLastMeasurementProperty(), String.valueOf(0));
                }
            }
        }
        return true;
    }

    long[] checkLog(String s, StringBuffer stringbuffer, long l) {
        Braf braf = null;
        File file = new File(s);
        long al[] = null;
        setProperty(getMessageProperty(), "");
        try {
            long l1 = -1L;
            try {
                l1 = Long.parseLong(getProperty(getLastFilePositionProperty()));
            } catch (NumberFormatException numberformatexception) {
            }
            long l2 = file.length();
            long l3 = Platform.timeMillis();
            long l4 = 0L;
            long l5 = 0L;
            if (l1 == -1L) {
                LogManager.log("RunMonitor", s + ": starting at end of log");
                l1 = l2;
            } else {
                if (l1 > l2) {
                    l1 = 0L;
                }
                LogManager.log("RunMonitor", s + ": reading from offset " + l1);
                if ((new File(s)).exists()) {
                    braf = new Braf(s, l1);
                    String s1;
                    while ((s1 = braf.readLine()) != null && stillActive()) {
                        l4++;
                        l5 += forwardAlerts(s1, "", stringbuffer, l);
                    }
                }
                LogManager.log("RunMonitor", s + ": read " + l4
                        + " lines from offset " + l1 + " to " + l2);
            }
            setProperty(getLastFilePositionProperty(), String.valueOf(l2));
            al = new long[3];
            al[0] = l5;
            al[1] = l4;
            al[2] = l3;
        } catch (Exception exception) {
            LogManager.log("Error", "reading log file: " + s + ", e:"
                    + exception);
        }
        try {
            if (braf != null) {
                braf.close();
            }
        } catch (Exception exception1) {
        }
        return al;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(getLastAlertsPerMinuteProperty());
        array.add(getLastLinesPerMinuteProperty());
        array.add(getValueProperty());
        array.add(getValue2Property());
        array.add(getValue3Property());
        array.add(getValue4Property());
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        if (getProperty(getValueProperty()).length() > 0) {
            array.add(getValueProperty());
        }
        if (getProperty(getValue2Property()).length() > 0) {
            array.add(getValue2Property());
        }
        if (getProperty(getValue3Property()).length() > 0) {
            array.add(getValue3Property());
        }
        if (getProperty(getValue4Property()).length() > 0) {
            array.add(getValue4Property());
        }
        array.add(getLastAlertsPerMinuteProperty());
        array.add(getLastLinesPerMinuteProperty());
        return array.elements();
    }

    boolean checkExpression(String s, String s1, int i) {
        Rule rule = Rule.stringToAction(s1 + "\t\t" + getProperty(pID));
        rule.setProperty(pID, i);
        addElement(rule);
        boolean flag = rule.match(this);
        if (flag) {
            IncrementAlertProperties(rule.getFullID(), s);
        }
        removeElement(rule);
        return flag;
    }

    void runAction(String s, int i) {
        Rule rule = Rule.stringToAction("always\t" + s + "\t"
                + getProperty(pID));
        rule.setProperty(pID, i);
        addElement(rule);
        rule.doAction(this);
        removeElement(rule);
    }

    int forwardAlerts(String s, String s1, StringBuffer stringbuffer, long l) {
        int i = 0;
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = getProperty(getMatchProperty());
        Array array = new Array();
        StringBuffer stringbuffer1 = new StringBuffer();
        int j = TextUtils.matchExpression(s, s6, array, stringbuffer1);
        if (j == Monitor.kURLok) {
            i++;
            boolean flag = false;
            if (getSetting("_showValsInMessage").length() > 0) {
                flag = true;
            }
            StringBuffer stringbuffer2 = new StringBuffer();
            if (array.size() == 1) {
                s2 = (String) array.at(0);
                if (flag) {
                    stringbuffer2.append("match1: " + array.at(0));
                }
            } else {
                if (array.size() >= 1) {
                    s2 = (String) array.at(0);
                    if (flag) {
                        stringbuffer2.append("match1: " + array.at(0));
                    }
                }
                if (array.size() >= 2) {
                    s3 = (String) array.at(1);
                    if (flag) {
                        stringbuffer2.append(" match2: " + array.at(1));
                    }
                }
                if (array.size() >= 3) {
                    s4 = (String) array.at(2);
                    if (flag) {
                        stringbuffer2.append(" match3: " + array.at(2));
                    }
                }
                if (array.size() >= 4) {
                    s5 = (String) array.at(3);
                    if (flag) {
                        stringbuffer2.append(" match4: " + array.at(3));
                    }
                }
            }
            setProperty(getValueProperty(), s2);
            setProperty(getValue2Property(), s3);
            setProperty(getValue3Property(), s4);
            setProperty(getValue4Property(), s5);
            setProperty(getMessageProperty(), s + stringbuffer2);
            if (runOwnRules()) {
                String s7 = ERROR_CATEGORY;
                ResetCategoryProperties(s7);
                if (getSetting("_alertNoPrefix").length() > 0) {
                    setProperty(pStateString, s);
                } else {
                    setProperty(pStateString, "matched: " + s);
                }
                setProperty(pCategory, s7);
                IncrementCategoryProperties(s7, s7);
                runActionRules(this, getProperty(pCategory));
            } else {
                TextUtils.appendAndTerminateString(s, stringbuffer, l,
                        " _SNMPTrapMonitorDetailsMax=" + l
                                + " defined in groups/master.config file.");
            }
        }
        return i;
    }

    public boolean hasProperty(String s) {
        return getPropertyObject(s) != null;
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (getProperty(pCategory).equals("good"))
                return "";
            String string = getProperty(getMatchDetailsProperty());
            String string1 = "";
            if (string.length() > 0) {
                string = " " + string;
                string = string.replaceAll("\\^", "\n ");
                string1 = "Matched SNMP Traps:\n" + string + "\n";
            }
            return string1;
        }
        String string;
        try {
            string = super.getProperty(stringproperty);
        } catch (NullPointerException nullpointerexception) {
            nullpointerexception.printStackTrace();
            System.out.println("The property in question was: "
                    + stringproperty);
            throw nullpointerexception;
        }
        return string;
    }

    public String getProperty(String s) throws NullPointerException {
        String s1 = "message[";
        if (s.startsWith(s1)) {
            String s2 = "";
            int i = TextUtils.readInteger(s, s1.length());
            Array array = Platform
                    .split(',', getProperty(getMessageProperty()));
            if (i >= 0 && i < array.size()) {
                s2 = (String) array.at(i);
            }
            return s2;
        } else {
            return super.getProperty(s);
        }
    }

    protected String getBrand() {
        return "SNMP Trap Monitor";
    }

    static {
        StringProperty astringproperty[] = new StringProperty[0];
        String s = (COM.dragonflow.SiteView.SNMPTrapBase.class).getName();
        addProperties(s, astringproperty);
    }
}
