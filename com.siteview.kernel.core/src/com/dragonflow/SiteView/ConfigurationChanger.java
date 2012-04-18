/*
 * 
 * Created on 2005-2-15 12:38:23
 *
 * ConfigurationChanger.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>ConfigurationChanger</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.Api.Alert;
import com.dragonflow.ConfigurationManager.CfgChangesSink;
import com.dragonflow.ConfigurationManager.InternalIdsManager;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.FrameFile;
import com.dragonflow.Properties.HashMapOrdered;
import com.dragonflow.Properties.JdbcConfig;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.SiteView:
// MonitorGroup, Monitor, SubGroup, AtomicMonitor,
// TopazConfigurator, SiteViewGroup, monitorUtils, TopazInfo,
// Portal, Platform, PortalSync, SiteViewObject,
// MasterConfig

public class ConfigurationChanger {

    boolean configInTopaz;

    String operation;

    Array monitorIDList;

    Array groupIDList;

    String toGroupID;

    String partialToGroupID;

    String replaces[];

    String timedDisable;

    String alertDisable;

    public Array newGroupNameList;

    public Array newMonitorNameList;

    public PrintWriter outputStream;

    HTTPRequest request;

    private static boolean debug = false;

    private static Vector cfgChangesSinks = new Vector();

    static HashMap EXCLUDE_MAP;

    HashMap groups;

    public ConfigurationChanger() {
        configInTopaz = false;
        operation = "";
        monitorIDList = null;
        groupIDList = null;
        toGroupID = "";
        partialToGroupID = "";
        replaces = null;
        timedDisable = "";
        alertDisable = "";
        newGroupNameList = null;
        newMonitorNameList = null;
        outputStream = null;
        request = null;
        groups = new HashMap();
    }

    public ConfigurationChanger(Array array, Array array1) {
        configInTopaz = false;
        operation = "";
        monitorIDList = null;
        groupIDList = null;
        toGroupID = "";
        partialToGroupID = "";
        replaces = null;
        timedDisable = "";
        alertDisable = "";
        newGroupNameList = null;
        newMonitorNameList = null;
        outputStream = null;
        request = null;
        groups = new HashMap();
        groupIDList = array;
        monitorIDList = array1;
//        configInTopaz = TopazConfigurator.configInTopazAndRegistered();
    }

    public static void delete(Array array, Array array1, HTTPRequest httprequest, PrintWriter printwriter) throws Exception {
        change("delete", array, array1, httprequest, printwriter);
    }

    public static void deleteGroup(String s, HTTPRequest httprequest, PrintWriter printwriter) throws Exception {
        Array array = new Array();
        array.add(s);
        delete(array, new Array(), httprequest, printwriter);
    }

    public static void deleteMonitor(String s, HTTPRequest httprequest, PrintWriter printwriter) throws Exception {
        Array array = new Array();
        array.add(s);
        delete(new Array(), array, httprequest, printwriter);
    }

    public static void change(String s, Array array, Array array1, HTTPRequest httprequest, PrintWriter printwriter) throws Exception {
        ConfigurationChanger configurationchanger = new ConfigurationChanger(array, array1);
        configurationchanger.outputStream = printwriter;
        configurationchanger.request = httprequest;
        configurationchanger.perform(s);
    }

    void initializeOperation() {
        if (request == null) {
            request = new HTTPRequest();
            request.setUser("administrator");
        }
        if (toGroupID.length() > 0) {
            partialToGroupID = CGI.getGroupIDRelative(toGroupID);
        }
        if (groupIDList == null) {
            groupIDList = new Array();
        }
        if (newGroupNameList == null) {
            newGroupNameList = new Array();
            for (int i = 0; i < groupIDList.size(); i ++) {
                newGroupNameList.add("");
            }

        }
        if (monitorIDList == null) {
            monitorIDList = new Array();
        }
        if (newMonitorNameList == null) {
            newMonitorNameList = new Array();
            for (int j = 0; j < monitorIDList.size(); j ++) {
                newMonitorNameList.add("");
            }

        }
        if (replaces == null) {
            replaces = new String[0];
        }
    }

    void perform(String s) throws Exception {
        operation = s;
        initializeOperation();
        if (s.equalsIgnoreCase("Delete")) {
            deleteInternal();
        } else if (s.startsWith("Replace") || s.startsWith("Disable") || s.startsWith("Enable") || s.startsWith("Refresh")) {
            modify(s);
//        } else if (s.startsWith("Set") || s.equals(managePage.TOPAZ_LOGGING_OP)) {
//            toTopaz(s);
        } else {
            move(s);
        }
    }

    void toTopaz(String s) {
        if (monitorIDList.size() > 0) {
            printProgressMessage(s + "for Monitor" + (monitorIDList.size() <= 1 ? "" : "s") + "<BR>");
            setMonitorTopazLogFilter(s);
        }
        if (groupIDList.size() > 0) {
            printProgressMessage(s + " group" + (groupIDList.size() <= 1 ? "" : "s") + "<BR>");
            for (int i = 0; i < groupIDList.size(); i ++) {
                String s1 = (String) groupIDList.get(i);
                setGroupTopazLogFilter(s1, s);
            }

        }
    }

    private void setMonitorTopazLogFilter(String s) {
        HashMap hashmap = new HashMap();
        for (int i = 0; i < monitorIDList.size(); i ++) {
            String s1 = (String) monitorIDList.get(i);
            int j = s1.indexOf(' ');
            if (j < 0) {
                continue;
            }
            if (debug) {
                TextUtils.debugPrint(operation + " MONITOR=" + s1);
                TextUtils.debugPrint("Timed:" + timedDisable + " Alert:" + alertDisable);
            }
            String s3 = s1.substring(0, j);
            String s4 = s1.substring(j + 1);
            if (groupIDList.contains(s3)) {
                continue;
            }
            Array array;
            if ((array = (Array) hashmap.get(s3)) == null) {
                array = new Array();
                hashmap.put(s3, array);
            }
            array.add(s4);
        }

        String s2;
        for (Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements(); setMonitorTopazLogProperties(s2, (Array) hashmap.get(s2), s)) {
            s2 = (String) enumeration.nextElement();
        }

    }

    private boolean monitorPassFilter(AtomicMonitor atomicmonitor) {
        String s = request.getValue("monitorTypeSelect");
        String s1 = request.getValue("monitorNameSelect");
        String s2 = request.getValue("machineNameSelect");
        if (s1.length() > 0) {
            String s3 = atomicmonitor.getProperty(Monitor.pName) + atomicmonitor.getProperty(Monitor.pDescription);
            Array array = new Array();
            int i = TextUtils.matchExpression(s3, s1, array, new StringBuffer());
            if (i != Monitor.kURLok) {
                i = TextUtils.matchExpression(s3, s1, array, new StringBuffer());
            }
            if (i != Monitor.kURLok) {
                return false;
            }
        }
        if (s2.length() > 0) {
            String s4 = atomicmonitor.getHostname();
            Array array1 = new Array();
            int j = TextUtils.matchExpression(s4, s2, array1, new StringBuffer());
            if (j != Monitor.kURLok) {
                j = TextUtils.matchExpression(s4, s2, array1, new StringBuffer());
            }
            if (j != Monitor.kURLok) {
                return false;
            }
        }
        return s.length() <= 0 || s.indexOf((String) atomicmonitor.getClassProperty("class")) != -1;
    }

    /**
     * 
     * 
     * @param s
     * @param array
     */
    private void createFilteredMonitorList(String s, Array array) {
        try {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(s);
            if (monitorgroup != null) {
                Enumeration enumeration = monitorgroup.getMonitors();
                while (enumeration.hasMoreElements()) {
                    Monitor monitor = (Monitor) enumeration.nextElement();
                    if (monitor instanceof SubGroup) {
                        String s1 = monitor.getProperty("_group");
                        if (s1.length() != 0) {
                            createFilteredMonitorList(s1, array);
                        }
                    } else {
                        array.add(monitor);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setGroupTopazLogFilter(String s, String s1) {
        try {
            Array array = getGroupFrames(s);
            String s2 = CGI.getGroupName(array, s);
            if (debug) {
                TextUtils.debugPrint(s1 + " GROUP=" + s);
                TextUtils.debugPrint("Timed:" + timedDisable + " Alert:" + alertDisable);
            }
            printProgressMessage(s1 + " for group: " + s2 + "<BR>");
            setGroupTopazLogProperties(s, true, s1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 
     * 
     * @param s
     * @param flag
     * @param s1
     */
    private void setGroupTopazLogProperties(String s, boolean flag, String s1) {
        Array array = new Array();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup.getElement(s);
        if (monitorgroup != null) {
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if ((monitor instanceof SubGroup) && flag) {
                    String s2 = monitor.getProperty("_group");
                    if (s2.length() != 0) {
                        setGroupTopazLogProperties(s2, flag, s1);
                    }
                } else {
                    array.add(monitor.getSetting(Monitor.pID.getName()));
                }
            }
        }
        if (array.size() > 0) {
            setMonitorTopazLogProperties(s, array, s1);
        }
    }

    private void setMonitorTopazLogProperties(String s, Array array, String s1) {
        try {
            String s2 = CGI.getGroupFilePath(s, request);
            Array array1 = FrameFile.readFromFile(s2);
            try {
                for (int i = 0; i < array.size(); i ++) {
                    Array array2 = getGroupFrames(s);
                    HashMap hashmap = CGI.findMonitor(array2, (String) array.get(i));
                    AtomicMonitor atomicmonitor = (AtomicMonitor) AtomicMonitor.createMonitor(hashmap, "");
                    setTopazLogProperties(atomicmonitor, array1, s1);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            FrameFile.writeToFile(s2, array1);
            SiteViewGroup.updateStaticPages(s);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private void setTopazLogProperties(AtomicMonitor atomicmonitor, Array array, String s) {
        String s1 = atomicmonitor.getProperty(AtomicMonitor.pID);
        int i = 0;
        try {
            i = monitorUtils.findMonitorIndex(array, s1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        HashMap hashmap = (HashMap) array.get(i);
        atomicmonitor.unsetProperty(AtomicMonitor.pOnlyLogMonitorData);
        atomicmonitor.unsetProperty(AtomicMonitor.pOnlyLogStatusChanges);
        atomicmonitor.unsetProperty(AtomicMonitor.pOnlyLogThresholdMeas);
        atomicmonitor.unsetProperty(AtomicMonitor.pNotLogToTopaz);
        hashmap.remove("_logOnlyThresholdMeas");
        hashmap.remove("_onlyStatusChanges");
        hashmap.remove("_logOnlyMonitorData");
        hashmap.remove("_notLogToTopaz");
        String s2 = "Report Everything";
        if (request.getValue("logOptions").equals("logOnlyThresholdMeas")) {
            s2 = "Log Only Threshold Measurements";
            atomicmonitor.setProperty(AtomicMonitor.pOnlyLogThresholdMeas, "true");
            hashmap.put("_logOnlyThresholdMeas", "true");
        } else if (request.getValue("logOptions").equals("onlyStatusChanges")) {
            s2 = "Log Only Status Changes";
            atomicmonitor.setProperty(AtomicMonitor.pOnlyLogStatusChanges, "true");
            hashmap.put("_onlyStatusChanges", "true");
        } else if (request.getValue("logOptions").equals("logOnlyMonitorData")) {
            s2 = "Log Only Monitor Data";
            atomicmonitor.setProperty(AtomicMonitor.pOnlyLogMonitorData, "true");
            hashmap.put("_logOnlyMonitorData", "true");
        } else if (request.getValue("logOptions").equals("notLogToTopaz")) {
//            s2 = "Not Log to " + TopazInfo.getTopazName() + " for Monitor";
            atomicmonitor.setProperty(AtomicMonitor.pNotLogToTopaz, "true");
            hashmap.put("_notLogToTopaz", "true");
        }
        printProgressMessage(s + " to <b>" + s2 + "</b> for monitor: " + TextUtils.getValue(hashmap, "_name") + "<BR>");
    }

    void print() {
        PrintWriter printwriter = outputStream;
        if (printwriter == null) {
            printwriter = new PrintWriter(System.out);
        }
        printwriter.println("Configuration Changer");
        printwriter.println(operation + "\n");
        if (groupIDList.size() > 0) {
            printwriter.println("Groups: " + groupIDList);
        }
        if (monitorIDList.size() > 0) {
            printwriter.println("Monitors: " + monitorIDList);
        }
        if (toGroupID.length() > 0) {
            printwriter.println("To Group: " + toGroupID);
        }
        if (replaces != null) {
            int i = replaces.length / 2;
            for (int j = 0; j < i; j ++) {
                printwriter.println("Replace \"" + replaces[j] + "\" with \"" + replaces[j + i] + "\"");
            }

        }
        if (timedDisable.length() > 0) {
            printwriter.println("Disable until: " + timedDisable);
        }
        if (alertDisable.length() > 0) {
            printwriter.println("Disable alerts until: " + alertDisable);
        }
        printwriter.println();
    }

    void deleteInternal() throws Exception {
        if (monitorIDList.size() > 0) {
            printProgressMessage("Deleting monitor" + (monitorIDList.size() <= 1 ? "" : "s") + "<BR>");
            for (int i = 0; i < monitorIDList.size(); i ++) {
                String s = (String) monitorIDList.get(i);
                deleteMonitorInternal(s.replace('/', ' '));
            }

            saveGroups();
        }
        if (groupIDList.size() > 0) {
            printProgressMessage("Deleting group" + (groupIDList.size() <= 1 ? "" : "s") + "<BR>");
            for (int j = 0; j < groupIDList.size(); j ++) {
                String s1 = (String) groupIDList.get(j);
                deleteGroupInternal(s1);
            }

            saveGroups();
            SiteViewGroup.updateStaticPages(request);
        }
    }

    void modify(String s) throws Exception {
        String s1 = "";
        if (s.startsWith("Disable")) {
            s1 = "Disable";
        } else if (s.startsWith("Enable")) {
            s1 = "Enable";
        } else if (s.startsWith("Refresh")) {
            s1 = "Refresh";
        } else if (s.startsWith("Replace")) {
            s1 = "Replace";
        }
        if (monitorIDList.size() > 0) {
            printProgressMessage(s1 + " monitor" + (monitorIDList.size() <= 1 ? "" : "s") + "<BR>");
            for (int i = 0; i < monitorIDList.size(); i ++) {
                String s2 = (String) monitorIDList.get(i);
                disableMonitor(s2, s1, replaces, timedDisable, alertDisable);
            }

        }
        if (groupIDList.size() > 0) {
            printProgressMessage(s1 + " group" + (groupIDList.size() <= 1 ? "" : "s") + "<BR>");
            for (int j = 0; j < groupIDList.size(); j ++) {
                String s3 = (String) groupIDList.get(j);
                disableGroup(s3, s1, replaces, timedDisable, alertDisable);
            }

        }
        saveGroups();
    }

    void move(String s) throws Exception {
        boolean flag = s.startsWith("Move");
        if (groupIDList.size() > 0) {
            if (debug) {
                TextUtils.debugPrint(s + " GROUP(s)");
            }
            manageGroups(groupIDList, newGroupNameList, toGroupID, flag, replaces);
        }
        if (monitorIDList.size() > 0) {
            if (debug) {
                TextUtils.debugPrint(s + " MONITOR(s)");
            }
            manageMonitors(monitorIDList, newMonitorNameList, toGroupID, flag, replaces);
        }
    }

    private Array getGroupFrames(String s) throws Exception {
        Array array = (Array) groups.get(s);
        if (array == null) {
            array = CGI.ReadGroupFrames(s, request);
            groups.put(s, array);
            printProgressMessage("Read group configuration for " + CGI.getGroupName(array, s) + "<BR>");
        }
        return array;
    }

    /**
     * 
     * 
     * @throws Exception
     */
    public void saveGroups() throws Exception {
        Enumeration enumeration = (Enumeration) groups.keys();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            Array array = (Array) groups.get(s);
            if (array != null) {
                printProgressMessage("Saving group configuration for " + CGI.getGroupName(array, s) + "<BR>");
                CGI.WriteGroupFrames(s, array, request);
                if (debug) {
                    TextUtils.debugPrint("SAVED GROUP=" + s);
                }
            } else {
                System.out.println("TRIED TO SAVE OUT EMPTY GROUP=" + s);
            }
        }
        SiteViewGroup.updateStaticPages();
        groups.clear();
    }

    boolean exceedsLicenseLimit(Array array, String s) {
        boolean flag = LUtils.wouldExceedLimit(array, s);
        if (flag) {
            String s1 = LUtils.getLicenseExceededHTML();
            outputStream.println(s1);
        }
        return flag;
    }

    public Array manageMonitors(Array array, Array array1, String s, boolean flag, String as[]) throws Exception {
        if (!flag && exceedsLicenseLimit(array, null)) {
            return null;
        }
        Array array2 = new Array();
        for (int i = 0; i < array.size(); i ++) {
            String s1 = (String) array.get(i);
            String s2 = (String) array1.get(i);
            if (debug) {
                TextUtils.debugPrint("MONITORSPEC=" + s1);
            }
            String s4 = "";
            String s5 = "";
            int k = s1.indexOf(' ');
            if (k < 0) {
                continue;
            }
            s4 = s1.substring(0, k);
            s5 = s1.substring(k + 1);
            if (flag && s4.equals(s)) {
                if (debug) {
                    TextUtils.debugPrint("MOVING " + s1 + " TO SELF GROUP");
                }
                continue;
            }
            Array array4 = getGroupFrames(s4);
            int l = CGI.findMonitorIndex(array4, s5);
            if (l < 1) {
                continue;
            }
            HashMap hashmap2 = (HashMap) array4.get(l);
            if (flag) {
                if (debug) {
                    TextUtils.debugPrint("REMOVING MONITOR - INDEX=" + l);
                }
                AtomicMonitor atomicmonitor = (AtomicMonitor) AtomicMonitor.createMonitor(hashmap2, request.getPortalServer());
                atomicmonitor.notifyMove((String) hashmap2.get("_id"));
                array4.remove(l);
            } else {
                if (debug) {
                    TextUtils.debugPrint("COPYING MONITOR");
                }
                hashmap2 = copyHashMap(hashmap2);
                if (as.length > 0 && Monitor.isMonitorFrame(hashmap2)) {
                    printProgressMessage(TextUtils.replaceInHashMap(hashmap2, as, EXCLUDE_MAP));
                }
                hashmap2.put("_name", s2);
                int i1 = InternalIdsManager.getInstance().getNextSiteviewId();
                hashmap2.put(AtomicMonitor.pUniqueInternalId.getName(), "" + i1);
            }
            array2.add(hashmap2);
        }

        Array array3 = getGroupFrames(s);
        if (debug) {
            TextUtils.debugPrint("READ TOGROUP=" + s);
        }
        HashMap hashmap = CGI.findMonitor(array3, "_config");
        String s3 = TextUtils.getValue(hashmap, "_nextID");
        if (s3.length() == 0) {
            s3 = "1";
        }
        for (int j = 0; j < array2.size(); j ++) {
            HashMap hashmap1 = (HashMap) array2.get(j);
            hashmap1.put("_id", s3);
            s3 = TextUtils.increment(s3);
            array3.add(hashmap1);
        }

        hashmap.put("_nextID", s3);
        printProgressMessage("Saving group configuration for " + CGI.getGroupName(array3, s) + "<BR>");
        CGI.WriteGroupFrames(s, array3, request);
        SiteViewGroup.updateStaticPages(s);
        if (debug) {
            TextUtils.debugPrint("SAVED TOGROUP" + s);
        }
        groups.remove(s);
        if (flag) {
            saveGroups();
        }
        return array3;
    }

    public void manageGroups(Array array, Array array1, String s, boolean flag, String as[]) throws Exception {
        Array array2 = new Array();
        for (int i = 0; i < array.size(); i ++) {
            String s1 = (String) array.get(i);
            String s2 = CGI.getGroupIDRelative(s1);
            String s3 = (String) array1.get(i);
            if (s3.length() == 0) {
                s3 = s1;
            }
            if (debug) {
                TextUtils.debugPrint("GROUPID=" + s1);
            }
            Array array4 = getGroupFrames(s1);
            if (!flag && exceedsLicenseLimit(array4, s1)) {
                return;
            }
            HashMap hashmap1 = CGI.findMonitor(array4, "_config");
            String s5 = I18N.toDefaultEncoding(TextUtils.getValue(hashmap1, "_parent"));
            String s6 = CGI.getGroupIDFull(s5, s1);
            Object obj = null;
            if (s5.length() > 0) {
                if (debug) {
                    TextUtils.debugPrint("PARENTID=" + s6);
                }
                Array array5 = getGroupFrames(s6);
                int k = CGI.findSubGroupIndex(array5, s2);
                if (k >= 1) {
                    obj = (HashMap) array5.get(k);
                    if (flag) {
                        if (debug) {
                            TextUtils.debugPrint("REMOVING SUBGROUP - INDEX=" + k);
                        }
                        array5.remove(k);
                    }
                }
            }
            if (!flag) {
                hashmap1.put("_name", s3);
            }
            if (s.length() > 0) {
                if (debug) {
                    TextUtils.debugPrint("CREATING SUBGROUP FRAME");
                }
                obj = new HashMapOrdered(true);
                ((HashMap) (obj)).put("_name", CGI.getGroupName(hashmap1, s1));
                ((HashMap) (obj)).put("_class", "SubGroup");
                ((HashMap) (obj)).put("_group", I18N.toNullEncoding(s2));
                array2.add(obj);
                hashmap1.put("_parent", partialToGroupID);
            } else {
                hashmap1.remove("_parent");
            }
            if (flag) {
                continue;
            }
            String s7 = TextUtils.keepChars(s3, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:");
            String s8 = Portal.getServerID(s);
            if (s8.length() > 0) {
                s7 = Portal.makePortalID(s8, s7, "");
            }
            duplicateGroupFile(s7, array4, ((HashMap) (obj)), as);
        }

        boolean flag1 = Portal.isPortalID(s) && Portal.getGroupID(s).length() > 0;
        if (flag1) {
            Array array3 = getGroupFrames(s);
            if (debug) {
                TextUtils.debugPrint("READ TOGROUP=" + s);
            }
            HashMap hashmap = CGI.findMonitor(array3, "_config");
            String s4 = TextUtils.getValue(hashmap, "_nextID");
            if (s4.length() == 0) {
                s4 = "1";
            }
            for (int j = 0; j < array2.size(); j ++) {
                HashMap hashmap2 = (HashMap) array2.get(j);
                hashmap2.put("_id", s4);
                s4 = TextUtils.increment(s4);
                array3.add(hashmap2);
            }

            hashmap.put("_nextID", s4);
            printProgressMessage("Saving group configuration for " + CGI.getGroupName(array3, s) + "<BR>");
            CGI.WriteGroupFrames(s, array3, request);
            SiteViewGroup.updateStaticPages(s);
            groups.remove(s);
        }
        if (flag) {
            saveGroups();
        }
    }

    /**
     * 
     * 
     * @param s
     * @param array
     * @param hashmap
     * @param as
     * @throws Exception
     */
    void duplicateGroupFile(String s, Array array, HashMap hashmap, String as[]) throws Exception {
        if (debug) {
            TextUtils.debugPrint("DUPLICATING GROUP=" + s);
        }
        String s1 = CGI.getGroupIDRelative(s);
        printProgressMessage("Duplicating group configuration for " + CGI.getGroupName(array, s1) + "<BR>");
        s = CGI.computeGroupName(s, request);
        s1 = CGI.getGroupIDRelative(s);
        if (as.length > 0) {
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap) enumeration.nextElement();
                if (as.length > 0 && Monitor.isMonitorFrame(hashmap1)) {
                    printProgressMessage(TextUtils.replaceInHashMap(hashmap1, as, EXCLUDE_MAP));
                }
            }
        }
        if (debug) {
            TextUtils.debugPrint("NEW GROUP ID=" + s);
        }
        if (hashmap != null) {
            hashmap.put("_group", I18N.toNullEncoding(s1));
        }
        for (int i = 1; i < array.size(); i ++) {
            HashMap hashmap2 = (HashMap) array.get(i);
            if (!TextUtils.getValue(hashmap2, "_class").equals("SubGroup")) {
                continue;
            }
            String s2 = TextUtils.getValue(hashmap2, "_group");
            String s3 = CGI.getGroupIDFull(s2, s);
            Array array3 = null;
            try {
                array3 = CGI.ReadGroupFrames(s3, request);
            } catch (Exception exception) {
                array3 = null;
            }
            if (array3 != null && array3.size() > 0) {
                HashMap hashmap4 = (HashMap) array3.get(0);
                hashmap4.put("_parent", I18N.toNullEncoding(s1));
                duplicateGroupFile(s3, array3, hashmap2, as);
            }
        }

        if (debug) {
            TextUtils.debugPrint("SAVING GROUPID=" + s);
        }
        CGI.WriteGroupFrames(s, array, request);
        SiteViewGroup.updateStaticPages(s);
        Array array1 = getGroupFrames(s);
        Enumeration enumeration1 =  (Enumeration) array1.iterator();
        Array array2 = new Array();
        while (enumeration1.hasMoreElements()) {
            HashMap hashmap3 = (HashMap) enumeration1.nextElement();
            String s4 = TextUtils.getValue(hashmap3, "_class");
            if (!s4.equals("SubGroup")) {
                SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                String s5 = TextUtils.getValue(hashmap3, "_id");
                AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s + SiteViewGroup.ID_SEPARATOR + s5);
                if (atomicmonitor != null) {
                    array2.add(atomicmonitor);
                    if (atomicmonitor.isDispatcher()) {
                        siteviewgroup.checkDispatcherForStart(atomicmonitor);
                    }
                }
            }
        }
    }

    private Array getReportFrameList() {
        Array array = null;
        try {
            if (!request.isStandardAccount()) {
                array = (Array) groups.get(request.getAccount());
                if (array == null) {
                    array = CGI.ReadGroupFrames(request.getAccount(), request);
                }
            } else {
                String s = Platform.getRoot() + File.separator + "groups" + File.separator + "history.config";
                File file = new File(s);
                if (!file.exists()) {
                    array = new Array();
                } else {
                    array = FrameFile.readFromFile(s);
                }
            }
        } catch (IOException ioexception) {
            array = new Array();
        }
        return array;
    }

    private void saveReportFrameList(Array array, String s) {
        try {
            if (!Platform.isStandardAccount(s)) {
                groups.remove(s);
                groups.put(s, array);
            } else {
                FrameFile.writeToFile(Platform.getRoot() + File.separator + "groups" + File.separator + "history.config", array);
            }
        } catch (IOException ioexception) {
        }
    }

    private void deleteGroupFromReport(String s) {
        deleteMonitorFromReport(s);
    }

    /**
     * 
     * 
     * @param s
     */
    private void deleteMonitorFromReport(String s) {
        Object obj = null;
        Object obj1 = null;
        int i = s.indexOf(' ');
        String s4;
        if (i < 0) {
            String s1 = s;
            s4 = s1;
            printProgressMessage("Checking reports for group: " + I18N.toNullEncoding(s1) + "<BR>");
        } else {
            String s2 = s.substring(0, i);
            String s3 = s.substring(i + 1);
            s4 = s3 + " " + s2;
            printProgressMessage("Checking reports...<BR>");
        }
        Array array = new Array();
        Array array1 = getReportFrameList();
        for (Enumeration enumeration =  (Enumeration) array1.iterator(); enumeration.hasMoreElements();) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            if (!Monitor.isReportFrame(hashmap)) {
                array.add(hashmap);
            } else {
                HashMap hashmap1 = copyHashMap(hashmap);
                hashmap1.remove("monitors");
                String s5 = TextUtils.getValue(hashmap1, "title");
                Enumeration enumeration1 = (Enumeration) hashmap.values("monitors");
                while (enumeration1.hasMoreElements()) {
                    String s6 = (String) enumeration1.nextElement();
                    String as[] = TextUtils.split(s6);
                    if (as.length == 1) {
                        if (s6.equals(s4)) {
                            printProgressMessage("Deleting group from report: " + s5 + "<BR>");
                        } else {
                            hashmap1.add("monitors", s6);
                        }
                    } else if (as.length > 1) {
                        if (s6.equals(s4)) {
                            printProgressMessage("Deleting monitor from report: " + s5 + "<BR>");
                        } else if (as[1].equals(s4)) {
                            printProgressMessage("Deleting group from report: " + s5 + "<BR>");
                        } else {
                            hashmap1.add("monitors", s6);
                        }
                    }
                }
                enumeration1 = (Enumeration) hashmap1.values("monitors");
                if (enumeration1.hasMoreElements()) {
                    array.add(hashmap1);
                } else {
                    printProgressMessage("No monitors in report; Deleting report: " + s5 + "<BR>");
                }
            }
        }

        saveReportFrameList(array, request.getAccount());
    }

    private void deleteMonitorInternal(String s) throws Exception {
        if (debug) {
            TextUtils.debugPrint("DELETING MONITOR " + s);
        }
        int i = s.indexOf(' ');
        if (i >= 0) {
            String s1 = s.substring(0, i);
            String s2 = s.substring(i + 1);
            Object obj = null;
            try {
                Array array = getGroupFrames(s1);
                int j = CGI.findMonitorIndex(array, s2);
                if (j >= 1) {
                    SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                    AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s1 + SiteViewGroup.ID_SEPARATOR + s2);
                    if (atomicmonitor != null) {
//                        if (!TopazConfigurator.delMonitors.contains(atomicmonitor)) {
//                            TopazConfigurator.delMonitors.add(atomicmonitor);
//                        }
                        siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                    }
                    array.remove(j);
                    if (debug) {
                        TextUtils.debugPrint("REMOVING MONITOR " + s + " - INDEX=" + j);
                    }
                } else if (debug) {
                    TextUtils.debugPrint("COULD NOT FIND " + s + " TO DELETE ");
                }
            } catch (Exception exception) {
                if (debug) {
                    TextUtils.debugPrint("WARNING - ERROR TRYING TO DELETE MON " + s + " : " + exception);
                }
            }
        }
    }

    /**
     * 
     * 
     * @param s
     * @throws Exception
     */
    private void deleteGroupInternal(String s) throws Exception {
        if (debug) {
            TextUtils.debugPrint("DELETING GROUP " + s);
        }
        File file = new File(CGI.getGroupFilePath(s, request));
        if (debug) {
            TextUtils.debugPrint("DELETING GROUP IN FILE " + file);
        }
        if (!file.exists()) {
            return;
        }
        Array array = getGroupFrames(s);
        Enumeration enumeration = (Enumeration) array.iterator();
        HashMap hashmap = (HashMap) enumeration.nextElement();
        String s1 = I18N.toDefaultEncoding(TextUtils.getValue(hashmap, "_parent"));
        if (s1.length() != 0) {
            s1 = CGI.getGroupIDFull(s1, s);
            Array array1 = getGroupFrames(s1);
            int i = CGI.findSubGroupIndex(array1, I18N.toNullEncoding(CGI.getGroupIDRelative(s)));
            if (i >= 1) {
                array1.remove(i);
            }
        }
        Array array2 = new Array();
        printProgressMessage("Deleting group configuration for " + CGI.getGroupName(array, s) + "<BR>");
        while (enumeration.hasMoreElements()) {
            HashMap hashmap1 = (HashMap) enumeration.nextElement();
            String s3 = TextUtils.getValue(hashmap1, "_class");
            if (s3.equals("SubGroup")) {
                String s4 = I18N.toDefaultEncoding(TextUtils.getValue(hashmap1, "_group"));
                if (s4.length() != 0) {
                    array2.add(CGI.getGroupIDFull(s4, s));
                }
            } else {
                SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                String s5 = TextUtils.getValue(hashmap1, "_id");
                AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s + SiteViewGroup.ID_SEPARATOR + s5);
                if (atomicmonitor != null) {
                    siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                }
            }
        }

        for (int j = 0; j < array2.size(); j ++) {
            deleteGroupInternal((String) array2.get(j));
        }

        if (JdbcConfig.configInDB()) {
            JdbcConfig.deleteGroupFromDB(s);
        }
        if (CGI.isPortalServerRequest(request)) {
            String s2 = Portal.cleanPortalServerID(request.getPortalServer());
            PortalSync.deleteGroup(s2, CGI.getGroupIDRelative(s));
        }
        deleteGroupFromReport(s);
        if (debug) {
            TextUtils.debugPrint("DELETING FILE " + file);
        }
        file.delete();
        File file1 = new File(CGI.getGroupFilePath(s, request, ".mg.bak"));
        if (file1.exists()) {
            file1.delete();
        }
        File file2 = new File(CGI.getGroupFilePath(s, request, ".dyn"));
        if (file2.exists()) {
            file2.delete();
        }
        File file3 = new File(CGI.getGroupFilePath(s, request, ".dyn.bak"));
        if (file3.exists()) {
            file3.delete();
        }
        groups.remove(s);
    }

    /**
     * 
     * 
     * @param hashmap
     * @return
     */
    public HashMap copyHashMap(HashMap hashmap) {
        Object obj;
        if (hashmap instanceof HashMapOrdered) {
            obj = new HashMapOrdered(true);
        } else {
            obj = new HashMap();
        }
        Object obj1;
        Enumeration enumeration = (Enumeration) hashmap.keys();
        while (enumeration.hasMoreElements()) {
            obj1 = enumeration.nextElement();
            ((HashMap) (obj)).put(obj1, hashmap.get(obj1));
        }

        return ((HashMap) (obj));
    }

    public void disableGroup(String s, String s1, String as[], String s2, String s3) throws Exception {
        Array array = getGroupFrames(s);
        String s4 = CGI.getGroupName(array, s);
        String s5 = "";
        if (Portal.isPortalID(s)) {
            s5 = Portal.makePortalID(Portal.getServerID(s), "", "");
        }
        if (debug) {
            TextUtils.debugPrint(s1 + " GROUP=" + s);
            TextUtils.debugPrint("Timed:" + s2 + " Alert:" + s3);
        }
        printProgressMessage(s1 + " " + s4 + " Group<BR>");
        for (int i = 1; i < array.size(); i ++) {
            HashMap hashmap = (HashMap) array.get(i);
            String s6 = TextUtils.getValue(hashmap, "_class");
            if (s6.equals("SubGroup")) {
                String s7 = I18N.toDefaultEncoding(TextUtils.getValue(hashmap, "_group"));
                if (s7.length() != 0) {
                    disableGroup(s5 + s7, s1, as, s2, s3);
                }
                continue;
            }
            if (s1.equals("Disable")) {
                if (s3.length() > 0) {
                    hashmap.put("_alertDisabled", s3);
                } else if (s2.length() == 0) {
                    hashmap.put("_disabled", "checked");
                } else {
                    hashmap.put("_timedDisable", s2);
                }
            } else if (s1.equals("Enable")) {
                hashmap.put("_timedDisable", "");
                hashmap.put("_alertDisabled", "");
                if (s2.length() == 0) {
                    hashmap.put("_disabled", "");
                }
            } else if (s1.equals("Replace") && as.length > 0 && Monitor.isMonitorFrame(hashmap)) {
                printProgressMessage(TextUtils.replaceInHashMap(hashmap, as, EXCLUDE_MAP));
            }
            refreshMonitor(s, TextUtils.getValue(hashmap, "_id"), s1);
        }

    }

    void refreshMonitor(String s, String s1, String s2) {
        String s3 = s + SiteViewGroup.ID_SEPARATOR + s1;
        SiteViewObject siteviewobject = Portal.getSiteViewForID(s);
        Monitor monitor = (Monitor) siteviewobject.getElement(s3);
        String s4 = "0";
        if (s2.equals("Refresh")) {
            s4 = "-1";
        }
        if (monitor != null) {
            monitor.setProperty(Monitor.pLastUpdate, s4);
        }
    }

    public void disableMonitor(String s, String s1, String as[], String s2, String s3) throws Exception {
        int i = s.indexOf(' ');
        if (i >= 0) {
            if (debug) {
                TextUtils.debugPrint(s1 + " MONITOR=" + s);
                TextUtils.debugPrint("Timed:" + s2 + " Alert:" + s3);
            }
            String s4 = s.substring(0, i);
            String s5 = s.substring(i + 1);
            Array array = getGroupFrames(s4);
            HashMap hashmap = CGI.findMonitor(array, s5);
            printProgressMessage(s1 + " " + TextUtils.getValue(hashmap, "_name") + "<BR>");
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup.getElement(s4 + SiteViewGroup.ID_SEPARATOR + s5);
            if (s1.equals("Disable")) {
                if (atomicmonitor != null) {
                    siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                }
                if (s3.length() > 0) {
                    hashmap.put("_alertDisabled", s3);
                } else if (s2.length() == 0) {
                    hashmap.put("_disabled", "checked");
                } else {
                    hashmap.put("_timedDisable", s2);
                }
            } else if (s1.equals("Enable")) {
                hashmap.put("_timedDisable", "");
                hashmap.put("_alertDisabled", "");
                if (s2.length() == 0) {
                    hashmap.put("_disabled", "");
                }
                if (atomicmonitor != null && atomicmonitor.isDispatcher()) {
                    siteviewgroup.checkDispatcherForStart(atomicmonitor);
                }
            } else if (s1.equals("Replace") && as.length > 0 && Monitor.isMonitorFrame(hashmap)) {
                printProgressMessage(TextUtils.replaceInHashMap(hashmap, as, EXCLUDE_MAP));
            }
            refreshMonitor(s4, s5, s1);
        }
    }

    void printProgressMessage(String s) {
        if (outputStream != null && s.length() > 0) {
            outputStream.println(s);
            outputStream.flush();
            LogManager.log("RunMonitor", "progress: " + s);
        }
    }

    public static void main(String args[]) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage ConfigurationChanger operation ");
            System.exit(0);
        }
        String s = args[0];
        if (s.equals("test")) {
            System.out.println("Testing...");
            // NOTE: by FooSleeper
            // Such JUnit test case dose not exist
            // TestRunner.run(com.dragonflow.SiteView.ConfigurationChangerUnitTest.class);
            System.exit(0);
        }
        Array array = new Array();
        Array array1 = new Array();
        Array array2 = new Array();
        Array array3 = new Array();
        String s1 = "";
        Array array4 = new Array();
        Array array5 = new Array();
        String s3 = "";
        String s5 = "";
        for (int i = 1; i < args.length; i ++) {
            if (args[i].equalsIgnoreCase("-item")) {
                i ++;
                if (args[i].indexOf("/") >= 0) {
                    array.add(args[i].replace('/', ' '));
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        array2.add(args[++ i]);
                    } else {
                        array2.add("");
                    }
                    continue;
                }
                array1.add(args[i]);
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    array3.add(args[++ i]);
                } else {
                    array3.add("");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-to")) {
                String s2 = args[++ i];
                continue;
            }
            if (args[i].equalsIgnoreCase("-replace")) {
                array4.add(args[++ i]);
                array5.add(args[++ i]);
                continue;
            }
            if (!args[i].equalsIgnoreCase("-until")) {
                continue;
            }
            if (s.toLowerCase().indexOf("disable alert") >= 0) {
                String s4 = args[++ i];
                continue;
            }
            String s6;
            if (s.toLowerCase().indexOf("disable") >= 0) {
                s6 = args[++ i];
            }
        }

        try {
            change(s, array1, array, null, new PrintWriter(System.out, true));
        } catch (Exception exception) {
            System.out.println("Exception changing configurations\n\n" + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public static void initialize() {
        validateConfigurationChanges();
    }

    public static void saveGroupMonitors(MonitorGroup monitorgroup) {
        Array array = null;
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Starting to save monitor group: " + monitorgroup.getProperty(MonitorGroup.pName));
//        }
        String s = monitorgroup.getProperty(MonitorGroup.pID);
        if (s.equals("SiteView")) {
            MasterConfig.saveMasterConfig(monitorgroup.getValuesTable());
            return;
        }
        try {
            array = CGI.ReadGroupFrames(s, null);
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("Successfully read group file for: " + monitorgroup.getProperty(MonitorGroup.pName));
//            }
        } catch (IOException ioexception) {
//            TopazFileLogger.getLogger().severe("Error reading group file: " + ioexception);
            LogManager.logException(ioexception);
        }
        if (array == null) {
            return;
        }
        String s1 = monitorgroup.getProperty(MonitorGroup.pID);
        monitorgroup.unsetProperty(MonitorGroup.pID);
        HashMap hashmap = new HashMap(monitorgroup.getValuesTable());
        monitorgroup.setProperty(MonitorGroup.pID, s1);
        array.replace(array.get(0), hashmap);
        java.util.HashMap hashmap1 = getMonitorFramesById(s, array);
        Vector vector = new Vector();
        vector.add(monitorgroup);
        Vector vector1 = new Vector();
        getGroupsMonitors(vector, vector1, null, false);
        for (int i = 0; i < vector1.size(); i ++) {
            AtomicMonitor atomicmonitor = (AtomicMonitor) vector1.elementAt(i);
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("Processing the monitor id for: " + atomicmonitor.getTopazInternalName());
//            }
            int j = processMonitorId(atomicmonitor, array, i == 0);
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("The ID returned for: " + atomicmonitor.getTopazInternalName() + " was: " + j);
//            }
            HashMap hashmap2 = (HashMap) hashmap1.get(new Integer(j));
            if (hashmap2 != null) {
                array.replace(hashmap2, atomicmonitor.getValuesTable());
//                if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                    TopazFileLogger.getLogger().fine("Group exists, replacing: " + atomicmonitor.getTopazInternalName());
//                }
                continue;
            }
            array.add(atomicmonitor.getValuesTable());
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("Group does not exist, adding: " + atomicmonitor.getTopazInternalName());
//            }
        }

        try {
            CGI.WriteGroupFrames(s, array, null);
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("Successfully wrote group file for: " + monitorgroup.getProperty(MonitorGroup.pName));
//            }
        } catch (IOException ioexception1) {
            LogManager.logException(ioexception1);
//            TopazFileLogger.getLogger().severe("Error writing group file: " + ioexception1);
        }
//        if (TopazFileLogger.getLogger().isLoggable(Level.INFO)) {
//            TopazFileLogger.getLogger().info("Finished saving monitor group: " + monitorgroup.getProperty(MonitorGroup.pName));
//        }
    }

    private static int processMonitorId(AtomicMonitor atomicmonitor, Array array, boolean flag) {
        String s = atomicmonitor.getProperty(AtomicMonitor.pID);
        int i = 0;
        if (s != null) {
            i = TextUtils.toInt(s);
        }
        if (i > 0) {
            return i;
        }
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Monitor does not contain an ID, getting the group's next ID value: " + atomicmonitor.getProperty(AtomicMonitor.pName));
//        }
        return getNextId(array, flag);
    }

    public static java.util.HashMap getMonitorFramesById(String s, Array array) {
        java.util.HashMap hashmap = new java.util.HashMap();
        for (int i = 1; i < array.size(); i ++) {
            HashMap hashmap1 = (HashMap) array.get(i);
            String s1 = (String) hashmap1.get("_class");
            if (s1 == null || s1.equalsIgnoreCase("SubGroup")) {
                continue;
            }
            String s2 = (String) hashmap1.get(Monitor.pID.getName());
            if (s2 == null) {
                continue;
            }
            int j = TextUtils.toInt(s2);
            if (j <= 0) {
                LogManager.log("error", "frame " + i + " in group " + s + " does not contain an _id");
            } else {
                hashmap.put(new Integer(j), hashmap1);
            }
        }

        return hashmap;
    }

    private static int getNextId(Array array, boolean flag) {
        HashMap hashmap = (HashMap) array.get(0);
        int i = 1;
        int j = TextUtils.toInt(TextUtils.getValue(hashmap, SiteViewObject.pNextID.getName()));
        if (j > i) {
            i = j;
        }
        if (!flag) {
            return i;
        }
        for (int k = 1; k < array.size(); k ++) {
            HashMap hashmap1 = (HashMap) array.get(k);
            int l = TextUtils.toInt((String) hashmap1.get(Monitor.pID.getName()));
            if (l >= i) {
                i = l + 1;
            }
        }

        if (i > j) {
            hashmap.put(SiteViewObject.pNextID.getName(), String.valueOf(i + 1));
        }
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINEST)) {
//            TopazFileLogger.getLogger().finest("Returning the next group id: " + i);
//        }
        return i;
    }

    /**
     * 
     * 
     * @param vector
     * @param vector1
     */
    public static void getCurrentConfiguration(Vector vector, Vector vector1) {
//        if (TopazFileLogger.getLogger().isLoggable(Level.INFO)) {
//            TopazFileLogger.getLogger().info("Getting current cached configuration.");
//        }
        Enumeration enumeration = SiteViewGroup.currentSiteView().getMonitors();
        getUniqueGroupsVector(enumeration, vector);
        for (int i = 0; i < vector.size(); i ++) {
            MonitorGroup monitorgroup = (MonitorGroup) vector.elementAt(i);
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Object obj = enumeration1.nextElement();
                if (obj instanceof AtomicMonitor) {
                    vector1.add(obj);
                }
            }
        }

    }

    /**
     * 
     * 
     * @param enumeration
     * @param vector
     * @return
     */
    public static Vector getUniqueGroupsVector(Enumeration enumeration, Vector vector) {
        HashSet hashset = new HashSet();
        while (enumeration.hasMoreElements()) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            String s = monitorgroup.getProperty(MonitorGroup.pID);
            if (s != null) {
                s = s.toLowerCase();
                if (!hashset.add(s)) {
//                    TopazFileLogger.getLogger().severe("Duplicate group was returned from SiteViewGroup.currentSiteView().getMonitors(): " + s);
                } else {
                    vector.add(monitorgroup);
                }
            }
        }
        return vector;
    }

    /**
     * 
     * 
     * @param vector
     * @param vector1
     * @param vector2
     * @param set
     */
    public static void getCurrentConfiguration(Vector vector, Vector vector1, Vector vector2, Set set) {
//        if (TopazFileLogger.getLogger().isLoggable(Level.INFO)) {
//            TopazFileLogger.getLogger().info("Getting current cached configuration.");
//        }
        Alert.getInstance().getAlerts(SiteViewGroup.currentSiteView(), null, vector2, set);
        Enumeration enumeration = SiteViewGroup.currentSiteView().getMonitors();
        getUniqueGroupsVector(enumeration, vector);
        for (int i = 0; i < vector.size(); i ++) {
            MonitorGroup monitorgroup = (MonitorGroup) vector.elementAt(i);
            Alert.getInstance().getAlerts(monitorgroup, null, vector2, set);
        }

        for (int j = 0; j < vector.size(); j ++) {
            MonitorGroup monitorgroup1 = (MonitorGroup) vector.elementAt(j);
            Enumeration enumeration1 = monitorgroup1.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Object obj = enumeration1.nextElement();
                if (obj instanceof AtomicMonitor) {
                    vector1.add(obj);
                    Alert.getInstance().getAlerts(monitorgroup1, (Monitor) obj, vector2, set);
                }
            }
        }

    }

    private static void printMonitorProps(Vector vector) {
        if (vector == null) {
            return;
        }
        for (int i = 0; i < vector.size(); i ++) {
            Monitor monitor = (Monitor) vector.elementAt(i);
//            TopazFileLogger.getLogger().fine("Got configuration for monitor: \"" + monitor.getProperty(Monitor.pName) + "\"");
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().finest("Monitor properties for \"" + monitor.getProperty(Monitor.pName) + "\"" + " : " + monitor.getValuesTable().toString());
//            }
        }

    }

    private static void printMonitorGroupProps(Vector vector) {
        if (vector == null) {
            return;
        }
        for (int i = 0; i < vector.size(); i ++) {
            MonitorGroup monitorgroup = (MonitorGroup) vector.elementAt(i);
//            TopazFileLogger.getLogger().fine("Got configuration for group: " + monitorgroup.getProperty(MonitorGroup.pName) + "\"");
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().finest("Group properties for \"" + monitorgroup.getProperty(MonitorGroup.pName) + "\"" + " : " + monitorgroup.getValuesTable().toString());
//            }
        }

    }

    /**
     * 
     * 
     * @param monitorgroup
     * @param vector
     * @param vector1
     */
    private static void addGroupTree(MonitorGroup monitorgroup, Vector vector, Vector vector1) {
        vector.add(monitorgroup);
        Enumeration enumeration = monitorgroup.getMonitors();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof SubGroup) {
                MonitorGroup monitorgroup1 = ((SubGroup) obj).lookupGroup();
                if (monitorgroup1 != null) {
                    addGroupTree(monitorgroup1, vector, vector1);
                }
            } else {
                vector1.add(obj);
            }
        }
    }

    public static void registerCfgChangesSink(CfgChangesSink cfgchangessink) {
        cfgChangesSinks.add(cfgchangessink);
    }

    public static void unregisterCfgChangesSink(CfgChangesSink cfgchangessink) {
        cfgChangesSinks.remove(cfgchangessink);
    }

    private static Collection validateConfigurationChanges() {
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Starting validation of configuration changes.");
//        }
        Collection collection = InternalIdsManager.getInstance().validateInternalIds();
//        if (collection.size() > 0 && TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Changes to groups were made while validating ID's, saving changes to storage.");
//        }
        MonitorGroup monitorgroup;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); saveGroupMonitors(monitorgroup)) {
            monitorgroup = (MonitorGroup) iterator.next();
        }

//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Finished validation of configuration changes.");
//        }
        return collection;
    }

    public static Collection notifyAdjustedGroups(Array array, Array array1, Array array2) {
//        if (TopazFileLogger.getLogger().isLoggable(Level.INFO)) {
//            TopazFileLogger.getLogger().info("Received notification that SiteView configuration changes have occurred.");
//        }
        Vector vector = getGroupsFromFile(array);
        Vector vector1 = getGroupsFromFile(array1);
        Vector vector2 = new Vector();
        for (int i = 0; i < array2.size(); i ++) {
            String s = getGroupIdFromFile((File) array2.get(i));
            if (s != null) {
                vector2.add(s);
            }
        }

        boolean flag = false;
        if (listContainsFile(array, "master.config") || listContainsFile(array1, "master.config")) {
            flag = true;
        }
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Checking to see if groups have been changed.");
//        }
        if (vector.size() == 0 && vector1.size() == 0 && vector2.size() == 0 && !flag) {
//            if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//                TopazFileLogger.getLogger().fine("No groups were changed, no notification needed.");
//            }
            return null;
        }
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINE)) {
//            TopazFileLogger.getLogger().fine("Changes to groups detected, notifying adjusted groups.");
//        }
        Collection collection = validateConfigurationChanges();
        Vector vector3 = new Vector();
        HashSet hashset = new HashSet();
        MonitorGroup monitorgroup;
        for (Iterator iterator = vector.iterator(); iterator.hasNext(); hashset.add(monitorgroup.getProperty(MonitorGroup.pID))) {
            monitorgroup = (MonitorGroup) iterator.next();
        }

        Iterator iterator1 = collection.iterator();
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            MonitorGroup monitorgroup1 = (MonitorGroup) iterator1.next();
            if (!hashset.contains(monitorgroup1.getProperty(MonitorGroup.pID))) {
                vector3.add(monitorgroup1);
                hashset.add(monitorgroup1.getProperty(MonitorGroup.pID));
            }
        } while (true);
        iterator1 = vector1.iterator();
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            MonitorGroup monitorgroup2 = (MonitorGroup) iterator1.next();
            if (!hashset.contains(monitorgroup2.getProperty(MonitorGroup.pID))) {
                vector3.add(monitorgroup2);
                hashset.add(monitorgroup2.getProperty(MonitorGroup.pID));
            }
        } while (true);
//        if (TopazFileLogger.getLogger().isLoggable(Level.FINEST)) 
        {
            StringBuffer stringbuffer = new StringBuffer();
            Iterator iterator2 = vector3.iterator();
            do {
                if (!iterator2.hasNext()) {
                    break;
                }
                MonitorGroup monitorgroup3 = (MonitorGroup) iterator2.next();
                stringbuffer.append(monitorgroup3.getProperty(MonitorGroup.pName));
                if (iterator2.hasNext()) {
                    stringbuffer.append(", ");
                }
            } while (true);
//            TopazFileLogger.getLogger().finest("The following groups were changed: " + stringbuffer);
        }
        for (int j = 0; j < cfgChangesSinks.size(); j ++) {
            CfgChangesSink cfgchangessink = (CfgChangesSink) cfgChangesSinks.elementAt(j);
            cfgchangessink.notifyAdjustedGroups(SiteViewGroup.currentSiteView(), vector, vector3, vector2);
        }

        return getFilesFromMonitorGroups(collection);
    }

    private static boolean listContainsFile(Array array, String s) {
        for (int i = 0; array != null && i < array.size(); i ++) {
            File file = (File) array.get(i);
            if (file.getAbsolutePath().endsWith(s)) {
                return true;
            }
        }

        return false;
    }

    private static Collection getFilesFromMonitorGroups(Collection collection) {
        Vector vector = new Vector();
        MonitorGroup monitorgroup;
        for (Iterator iterator = vector.iterator(); iterator.hasNext(); vector.add(getGroupFile(monitorgroup))) {
            monitorgroup = (MonitorGroup) iterator.next();
        }

        return vector;
    }

    /**
     * 
     * 
     * @param collection
     * @return
     */
    private static Vector findCachedGroups(Collection collection) {
        HashSet hashset = new HashSet(collection);
        Vector vector = new Vector();
        Enumeration enumeration = SiteViewGroup.currentSiteView().getMonitors();
        while (enumeration.hasMoreElements() && hashset.size() != 0) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration.nextElement();
            String s = monitorgroup.getProperty(MonitorGroup.pID);
            if (hashset.contains(s)) {
                vector.add(monitorgroup);
                hashset.remove(s);
            }
        }
        return vector;
    }

    private static Vector getGroupsFromFile(Array array) {
        Vector vector = new Vector();
        for (int i = 0; array != null && i < array.size(); i ++) {
            File file = (File) array.get(i);
            String s = getGroupIdFromFile(file);
            if (s != null) {
                vector.add(s);
            }
        }

        return findCachedGroups(vector);
    }

    private static String getGroupIdFromFile(File file) {
        int i = file.getName().indexOf(".mg");
        if (i < 0) {
            return null;
        } else {
            return file.getName().substring(0, i);
        }
    }

    private static File getGroupFile(MonitorGroup monitorgroup) {
        String s = monitorgroup.getProperty(MonitorGroup.pID);
        return new File(CGI.getGroupFilePath(s, null));
    }

    public static void getGroupsMonitors(Collection collection, Vector vector, Vector vector1, boolean flag) {
        getGroupsMonitors(collection, vector, vector1, flag, new HashSet());
    }

    /**
     * 
     * 
     * @param collection
     * @param vector
     * @param vector1
     * @param flag
     * @param hashset
     */
    private static void getGroupsMonitors(Collection collection, Vector vector, Vector vector1, boolean flag, HashSet hashset) {
        Vector vector3 = new Vector();
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            MonitorGroup monitorgroup = (MonitorGroup) iterator.next();
            if (monitorgroup.equals(SiteViewGroup.currentSiteView())) {
                SiteViewGroup.currentSiteView();
                SiteViewGroup.getGroupsMonitors(vector, vector1, flag);
                return;
            }
            hashset.add(monitorgroup);
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Object object = enumeration.nextElement();
                if (object != null) {
                    boolean isSubGroup = false;
                    MonitorGroup subGroup = null;
                    if (flag || vector1 != null) {
                        isSubGroup = object instanceof SubGroup;
                        if (isSubGroup) {
                            subGroup = ((SubGroup) object).lookupGroup();
                            if (hashset.contains(subGroup)) {
                                String string = "Error";
                                StringBuffer stringbuffer = (new StringBuffer().append("ConfigurationChanger: circular groups found, ignoring the subgroup: "));
                                if (subGroup != null) {
                                    // TODO need review
                                    /* empty */
                                }
                                LogManager.log(string, stringbuffer.append(subGroup.getProperty(MonitorGroup.pID)).toString());
                                continue;
                            }
                        }
                    }
                    if (vector != null && object instanceof AtomicMonitor) {
                        vector.add(object);
                    } else if (vector1 != null && isSubGroup && subGroup != null) {
                        vector1.add(subGroup);
                    }
                    if (flag && isSubGroup && subGroup != null) {
                        vector3.add(subGroup);
                    }
                }
            }
        }
        if (flag && vector3.size() > 0) {
            getGroupsMonitors(vector3, vector, vector1, true, hashset);
        }
    }

    static {
        if (System.getProperty("ConfigurationChanger.debug") != null) {
            debug = true;
        }
        EXCLUDE_MAP = null;
        EXCLUDE_MAP = new HashMap();
        EXCLUDE_MAP.put("_parent", "exclude");
        EXCLUDE_MAP.put("_group", "exclude");
        EXCLUDE_MAP.put("_class", "exclude");
        EXCLUDE_MAP.put("_id", "exclude");
        EXCLUDE_MAP.put("_nextID", "exclude");
        EXCLUDE_MAP.put("_nextConditionID", "exclude");
    }
}
