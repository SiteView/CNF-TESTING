/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * J2EERealtimeMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>J2EERealtimeMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.BrowsableCache;
import COM.dragonflow.SiteView.J2EEAttachObserver;
import COM.dragonflow.SiteView.J2EEMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.SubGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.StatefulMonitor.J2EEConnection;
import COM.dragonflow.Utils.J2EEDummyMonitor;
import COM.dragonflow.Utils.LUtils;

//import com.dragonflow.topaz.j2ee.comm.HashedSample;

public class J2EERealtimeMonitor extends J2EEMonitor {

    public static final int QUALITY_GOOD = 1;

    public static final int QUALITY_WARNING = 2;

    public static final int QUALITY_ERROR = 3;

    public static final int AVAILABILITY_UNKNOWN = 0;

    public static final int AVAILABILITY_GOOD = 1;

    public static final int AVAILABILITY_WARNING = 2;

    public static final int AVAILABILITY_ERROR = 3;

    private static Map availabilityCodeMap;

    private static final String GROUP_STR = "group:";

    private static final String GROUP_MARKER = " group: ";

    private static final String MONITOR_TYPE = "J2EE_ONLINE";

    private static final String TARGET_LABEL = "Target";

    private static final String MONITOR_LABEL = "Monitor";

    private static final String SESSION_LABEL = "session";

    private static J2EEAttachObserver attachObserver = null;

    private static ScalarProperty pAvailabilityMonitor;

    private static NumericProperty pAvailabilityIndicator;

    private Object groups[];

    private Map groupsMap;

    private Array pGroupStates;

    private String groupStateValues[];

    private Set allGroups;

    public J2EERealtimeMonitor() {
        groupsMap = new HashMap();
        allGroups = new HashSet();
    }

    public void initialize(jgl.HashMap hashmap) {
        super.initialize(hashmap);
        groups = getMonitoredGroups();
        groupStateValues = new String[groups.length];
        pGroupStates = makeGroupStatePropertyArray(groups);
        for (int i = 0; i < groups.length; i ++) {
            groupStateValues[i] = "0.0";
        }

    }

    public void setProperty(StringProperty stringproperty, String s) {
        String s1 = stringproperty.getName();
        if (s1.startsWith("group:")) {
            int i = s1.lastIndexOf(":");
            String s2 = s1.substring(i + 1);
            int j = Integer.parseInt(s2);
            groupStateValues[j] = s;
        } else {
            super.setProperty(stringproperty, s);
        }
    }

    public String getProperty(StringProperty stringproperty) {
        String s = stringproperty.getName();
        if (s.startsWith("group:")) {
            return getProperty(s);
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public String getProperty(String s) {
        if (s.startsWith("group:")) {
            int i = s.lastIndexOf(":");
            String s1 = s.substring(i + 1);
            int j = Integer.parseInt(s1);
            return groupStateValues[j];
        } else {
            return super.getProperty(s);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        if (stillActive()) {
            synchronized (this) {
                String s = (String) getConnID(null);
                if (attachObserver == null) {
                    attachObserver = new J2EEAttachObserver(this);
                    attachObserver.start();
                    setProperty(pStatus, "OK");
//                    setProperty(pStateString, "Connecting to " + TopazInfo.getTopazName());
                } else if (attachObserver.getStatus() != 128L) {
                    setProperty(pStatus, "OK");
//                    setProperty(pStateString, "Connecting to " + TopazInfo.getTopazName());
                }
                return super.update();
            }
        }
        return true;
    }

    private Map getRules() {
        HashMap hashmap = new HashMap();
        Enumeration enumeration = getRules(1);
        for (int i = 0; i < groups.length; i ++) {
            List alist[] = new List[3];
            alist[0] = new ArrayList();
            alist[1] = new ArrayList();
            alist[2] = new ArrayList();
            hashmap.put(groups[i], alist);
        }

        do {
            if (!enumeration.hasMoreElements()) {
                break;
            }
            Rule rule = (Rule) enumeration.nextElement();
            String s = rule.getProperty("_expression");
            String s1 = rule.getProperty("_action");
            int j = s.indexOf(' ');
            int k = s.lastIndexOf(' ');
            if (j >= 0 && k >= 0) {
                String s2 = s.substring(0, j);
                if (s2.startsWith("group:")) {
                    int l = s2.indexOf(':');
                    int i1 = Integer.parseInt(s2.substring(l + 1));
                    String s3 = (String) groups[i1];
                    List alist1[] = (List[]) hashmap.get(s3);
                    if (s1.endsWith("error")) {
                        alist1[2].add(rule);
                    } else if (s1.endsWith("warning")) {
                        alist1[1].add(rule);
                    } else if (s1.endsWith("good")) {
                        alist1[0].add(rule);
                    }
                }
            }
        } while (true);
        return hashmap;
    }

    protected void processResults(Map map, String s) {
        String s1 = getProperty(pAvailabilityMonitor);
        int i;
        if (s1 != null && s1.length() > 0) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s1);
            if (atomicmonitor != null) {
                Integer integer = (Integer) availabilityCodeMap.get(atomicmonitor.getProperty(pCategory));
                if (integer == null) {
                    i = 0;
                } else {
                    i = integer.intValue();
                }
            } else {
                i = 0;
            }
        } else {
            i = s != null ? 3 : 1;
        }
        setProperty(pAvailabilityIndicator, i);
        if (attachObserver == null || attachObserver.getStatus() != 128L) {
            return;
        }
        ArrayList arraylist = new ArrayList();
        long l = Platform.timeMillis() / 1000L + attachObserver.mTimeDiff / 1000L;
        Map map1 = getRules();
        HashMap hashmap = new HashMap();
        Iterator iterator = map.entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
            COM.dragonflow.SiteView.J2EEMonitor.CounterResult counterresult = (COM.dragonflow.SiteView.J2EEMonitor.CounterResult) entry.getValue();
            String s2 = (String) entry.getKey();
            Map map2 = getElementMap(J2EEConnection.parseName(s2));
//            HashedSample hashedsample = new HashedSample(l, "J2EE_ONLINE");
//            double d = getDouble(counterresult.getART());
//            hashedsample.addValue("ART", new Double(d));
//            hashedsample.addValue("HPS", new Double(getDouble(counterresult.getHPS())));
//            String s4 = (String) map2.get("J2EE") + " " + (String) map2.get("group");
//            hashedsample.addValue("Quality", new Integer(getQuality(map1, s4, d)));
//            hashedsample.addValue("TimeStamp", new Long(l));
//            if (s != null) {
//                hashedsample.addValue("Error", new Integer(3));
//                hashedsample.addValue("ErrorMessage", s);
//            }
//            hashedsample.setMeasurementValue("TimeStamp", String.valueOf(l));
//            hashedsample.setMeasurementValue("Target", getHostname());
//            hashedsample.setMeasurementValue("Monitor", "J2EE_ONLINE");
//            hashedsample.setMeasurementValue("session", String.valueOf(TopazAPI.profile_id));
            java.util.Map.Entry entry1;
//            for (Iterator iterator1 = map2.entrySet().iterator(); iterator1.hasNext(); hashedsample.setMeasurementValue((String) entry1.getKey(), (String) entry1.getValue())) {
//                entry1 = (java.util.Map.Entry) iterator1.next();
//            }
//
//            arraylist.add(hashedsample);
//            Double double2 = (Double) hashmap.get(s4);
//            if (double2 == null || double2.doubleValue() < d) {
//                hashmap.put(s4, new Double(d));
//            }
        } while (true);
        for (int j = 0; j < groups.length; j ++) {
            Double double1 = (Double) hashmap.get(groups[j]);
            NumericProperty numericproperty = (NumericProperty) groupsMap.get(groups[j]);
            if (numericproperty == null) {
                return;
            }
            if (double1 == null) {
                setProperty(numericproperty, "0.0");
            } else {
                setProperty(numericproperty, double1.floatValue());
            }
        }

        attachObserver.send(arraylist);
        if (s == null) {
            StringBuffer stringbuffer = new StringBuffer("Max ART: ");
            String s3 = "";
            for (int k = 0; k < pGroupStates.size(); k ++) {
                stringbuffer.append(s3 + groups[k] + ": " + getProperty((StringProperty) pGroupStates.at(k)));
                s3 = ", ";
            }

            if (attachObserver.getStatus() != 128L) {
//                LogManager.log("error", "Disconnected from " + TopazInfo.getTopazName() + ", attempting to reconnect");
//                stringbuffer.append("    Failed to send to " + TopazInfo.getTopazName());
            }
            setProperty(pStateString, stringbuffer.toString());
            setProperty(pStatus, "OK");
        } else {
            setProperty(pStateString, s);
            setProperty(pStatus, "error");
        }
    }

    private Map getElementMap(String as[]) {
        HashMap hashmap = new HashMap();
        for (int i = 0; i < as.length; i ++) {
            int j = as[i].indexOf(':');
            if (j >= 0) {
                String s = as[i].substring(0, j);
                String s1 = as[i].substring(j + 2);
                hashmap.put(s, s1);
            }
        }

        return hashmap;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private static double getDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0D;
        }
    }

    private int getQuality(Map map, String s, double d) {
        List alist[] = (List[]) map.get(s);
        NumericProperty numericproperty = (NumericProperty) groupsMap.get(s);
        setProperty(numericproperty, (float) d);
        int i = 3;
        do {
            if (i < 1) {
                break;
            }
            List list = alist[i - 1];
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Rule rule = (Rule) iterator.next();
                boolean flag = rule.match(this);
                if (flag) {
                    return i;
                }
            }

            i --;
        } while (true);
        return 1;
    }

    protected Set getAllCounters() {
        Set set = getCounters();
        return set;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = pGroupStates;
        if (array == null || array.size() == 0) {
            array = makeGroupStatePropertyArray(getMonitoredGroups());
        }
        if (array.size() == 0) {
            array = makeGroupStatePropertyArray(allGroups.toArray());
        }
        Array array1 = new Array(array);
        array1.add(pAvailabilityIndicator);
        return array1.elements();
    }

    private Array makeGroupStatePropertyArray(Object aobj[]) {
        Array array = new Array();
        for (int i = 0; i < aobj.length; i ++) {
            NumericProperty numericproperty = new NumericProperty("group:" + i);
            numericproperty.setDisplayText(aobj[i].toString(), "Maximum ART for " + aobj[i] + " Counters");
            numericproperty.defaultPrecision = 2;
            numericproperty.isTopazLogProperty = false;
            numericproperty.setStateOptions(1);
            groupsMap.put(aobj[i], numericproperty);
            array.add(numericproperty);
        }

        return array;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        int i = pGroupStates.size();
        for (int j = 0; j < i; j ++) {
            array.add(pGroupStates.at(j));
        }

        array.add(pAvailabilityIndicator);
        return array;
    }

    private Object[] getMonitoredGroups() {
        HashSet hashset = new HashSet();
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < getMaxValues(); i ++) {
            String s = getProperty(pCounterNames[i]);
            if (s.length() == 0) {
                break;
            }
            String s1 = J2EEConnection.getGroupFromCounterName(s);
            if (s1 != null && !hashset.contains(s1)) {
                arraylist.add(s1);
                hashset.add(s1);
            }
        }

        String as[] = new String[arraylist.size()];
        int j = arraylist.size();
        for (int k = 0; k < j; k ++) {
            as[k] = (String) arraylist.get(k);
        }

        return as;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap) {
        s = super.verify(stringproperty, s, httprequest, hashmap);
        if (stringproperty == pTarget) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            for (Enumeration enumeration = siteviewgroup.getMonitors(); enumeration.hasMoreElements();) {
                MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
                Enumeration enumeration1 = monitorgroup.getMonitors();
                while (enumeration1.hasMoreElements()) {
                    Monitor monitor = (Monitor) enumeration1.nextElement();
                    if ((monitor instanceof J2EERealtimeMonitor) && !monitor.getFullID().equalsIgnoreCase(getFullID())) {
                        String s1 = monitor.getProperty("_target");
                        if (s1.equals(s)) {
                            hashmap.put(stringproperty, "There is already J2EE Realtime Monitor defined for this target. Only one monitor can exist per target");
                        }
                    }
                }
            }

        }
        return s;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        Vector vector = new Vector();
        if (scalarproperty == pAvailabilityMonitor) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            vector.addElement("");
            vector.addElement("Self");
            String s = getProperty(pHostname);
            if (s.length() == 0) {
                String s1 = httprequest.getValue("uniqueID");
                jgl.HashMap hashmap = BrowsableCache.getCache(s1, true, false);
                jgl.HashMap hashmap1 = (jgl.HashMap) hashmap.get("cParms");
                s = (String) hashmap1.get("_server");
                jgl.HashMap hashmap2 = (jgl.HashMap) hashmap.get("selectNames");
                String s2;
                for (Enumeration enumeration2 = hashmap2.keys(); enumeration2.hasMoreElements(); allGroups.add(J2EEConnection.getGroupFromCounterName(HTTPRequest.decodeString(s2)))) {
                    s2 = (String) enumeration2.nextElement();
                }

            }
            if (s != null && s.length() > 0) {
                for (Enumeration enumeration = siteviewgroup.getMonitors(); enumeration.hasMoreElements();) {
                    MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
                    Enumeration enumeration1 = monitorgroup.getMonitors();
                    while (enumeration1.hasMoreElements()) {
                        Monitor monitor = (Monitor) enumeration1.nextElement();
                        if (!(monitor instanceof SubGroup) && !(monitor instanceof J2EEMonitor) && s.equalsIgnoreCase(monitor.getHostname())) {
                            vector.addElement(monitorgroup.getProperty(pGroupID) + "/" + monitor.getProperty(pID));
                            vector.addElement(monitorgroup.getProperty(pName) + ":" + monitor.getProperty(pName));
                        }
                    }
                }

            }
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public HashMap getTopazMeasurements() {
        HashMap hashmap = new HashMap();
        String s = getTopazCounterLabel(pAvailabilityIndicator);
//        TopazMeasurement topazmeasurement = new TopazMeasurement(getTopazCounterInternalName(pAvailabilityIndicator), s, getTopazCounterDescription(pAvailabilityIndicator), s, false);
//        hashmap.put(s, topazmeasurement);
        return hashmap;
    }

    static {
        availabilityCodeMap = new HashMap();
        Array array = new Array();
        availabilityCodeMap.put(Monitor.GOOD_CATEGORY, new Integer(1));
        availabilityCodeMap.put(Monitor.WARNING_CATEGORY, new Integer(2));
        availabilityCodeMap.put(Monitor.ERROR_CATEGORY, new Integer(3));
        pAvailabilityIndicator = new NumericProperty("_availabilityIndicator");
        pAvailabilityIndicator.setDisplayText("Availability Indicator", "");
        pAvailabilityIndicator.setParameterOptions(false, 1, false);
        array.add(pAvailabilityIndicator);
        pAvailabilityMonitor = new ScalarProperty("_availabilityMonitor");
//        pAvailabilityMonitor.setDisplayText("Availability Monitor", "will use the status of specified monitor to send availability information to " + TopazInfo.getTopazName() + ". Self means use monitors own status for this purpose");
        pAvailabilityMonitor.setParameterOptions(true, 5, false);
        array.add(pAvailabilityMonitor);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i ++) {
            astringproperty[i] = (StringProperty) array.at(i);
        }

        addProperties("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", Rule.stringToClassifier("status != OK\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "description", "Monitor J2EE systems.");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "help", "J2EERealTimeMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "title", "J2EE (Realtime)");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "class", "J2EERealtimeMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "target", "_target");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "topazName", "J2EE (Realtime)");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "topazType", "Web Application Server");
        if (LUtils.isValidSSforXLicense(new J2EEDummyMonitor())) {
            setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "loadable", "true");
        } else {
            setClassProperty("COM.dragonflow.StandardMonitor.J2EERealtimeMonitor", "loadable", "false");
        }
    }
}
