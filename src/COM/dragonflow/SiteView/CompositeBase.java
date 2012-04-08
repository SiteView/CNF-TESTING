/*
 * 
 * Created on 2005-2-15 12:36:29
 *
 * CompositeBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>CompositeBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PercentProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.StandardMonitor.CompositeMonitor;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, MonitorGroup, Monitor, SubGroup,
// SiteViewObject, SiteViewGroup

public abstract class CompositeBase extends AtomicMonitor {

    public static StringProperty pItems;

    static StringProperty pItemsChecked;

    static StringProperty pItemsInError;

    static StringProperty pItemsInWarning;

    static StringProperty pItemsInGood;

    static StringProperty pPercentGood;

    static StringProperty pPercentError;

    static StringProperty pPercentWarning;

    static StringProperty pNameItemsInError;

    static StringProperty pNameItemsInWarning;

    public static final int MONITORS_CHECKED = 0;

    public static final int MONITORS_ERROR = 1;

    public static final int MONITORS_WARNING = 2;

    public static final int MONITORS_GOOD = 3;

    public static final int GROUPS_CHECKED = 4;

    public static final int GROUPS_ERROR = 5;

    public static final int GROUPS_WARNING = 6;

    public static final int GROUPS_GOOD = 7;

    public static final int STAT_COUNT = 8;

    public static final int ERROR_NAMES = 0;

    public static final int WARNING_NAMES = 1;

    public static final int STRING_COUNT = 2;

    static String STEP_REFERENCE = "step";

    public CompositeBase() {
    }

    protected void updateStats(int ai[], String as[], Monitor monitor) {
        String s = monitor.getProperty(pCategory);
        byte byte0 = 0;
        if (monitor instanceof MonitorGroup) {
            byte0 = 4;
        }
        if (!s.equals(NODATA_CATEGORY)) {
            ai[0 + byte0]++;
        }
        if (s.equals(ERROR_CATEGORY)) {
            ai[1 + byte0]++;
            if (as[0].length() > 0) {
                as[0] += ", ";
            }
            as[0] += monitor.getProperty(pName) + ": "
                    + monitor.getProperty(pStateString);
        } else if (s.equals(WARNING_CATEGORY)) {
            ai[2 + byte0]++;
            if (as[1].length() > 0) {
                as[1] += ", ";
            }
            as[1] += monitor.getProperty(pName) + ": "
                    + monitor.getProperty(pStateString);
        } else if (s.equals(GOOD_CATEGORY)) {
            ai[3 + byte0]++;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param ai
     * @param as
     * @param monitorgroup
     */
    protected void checkGroup(int ai[], String as[], MonitorGroup monitorgroup) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Enumeration enumeration = monitorgroup.getMonitors();
        while (enumeration.hasMoreElements()) {
            Monitor monitor = (Monitor) enumeration.nextElement();
            if (monitor != this) {
                if (monitor instanceof SubGroup) {
                    Monitor monitor1 = (Monitor) siteviewgroup
                            .getElement(monitor.getProperty(SubGroup.pGroup));
                    if (monitor1 != null && (monitor1 instanceof MonitorGroup)) {
                        checkGroup(ai, as, (MonitorGroup) monitor1);
                    }
                } else {
                    updateStats(ai, as, monitor);
                }
            }
        }
    }

    protected int[] initializeStats() {
        int ai[] = new int[8];
        for (int i = 0; i < ai.length; i++) {
            ai[i] = 0;
        }

        return ai;
    }

    protected String[] initializeNameList() {
        String as[] = new String[2];
        for (int i = 0; i < as.length; i++) {
            as[i] = "";
        }

        return as;
    }

    protected void updateProperties(int ai[], String as[], boolean flag) {
        if (stillActive()) {
            synchronized (this) {
                int i = ai[0] + ai[4];
                int j = ai[1] + ai[5];
                int k = ai[2] + ai[6];
                int l = ai[3] + ai[7];
                setProperty(pItemsChecked, i);
                setProperty(pItemsInError, j);
                setProperty(pItemsInWarning, k);
                setProperty(pItemsInGood, l);
                TextUtils.debugPrint(" error " + as[0]);
                setProperty(pNameItemsInError, as[0]);
                setProperty(pNameItemsInWarning, as[1]);
                if (i == 0) {
                    setProperty(pStateString, "no items checked");
                    setProperty(pPercentGood, "n/a");
                    setProperty(pPercentWarning, "n/a");
                    setProperty(pPercentError, "n/a");
                    setProperty(pNoData, "n/a");
                    setProperty(pMeasurement, 0);
                } else {
                    String s = "";
                    String s1 = "";
                    String s3 = "";
                    if (ai[0] > 0 && ai[4] > 0) {
                        String s2 = " monitor";
                        String s4 = " group";
                    }
                    if (ai[5] > 0) {
                        if (s.length() > 0) {
                            s = s + ", ";
                        }
                        s = s + "" + ai[1] + " in error (" + as[0] + ")";
                    }
                    if (ai[1] > 0) {
                        if (s.length() > 0) {
                            s = s + ", ";
                        }
                        s = s + "" + ai[1] + " in error (" + as[0] + ")";
                    }
                    if (ai[6] > 0) {
                        if (s.length() > 0) {
                            s = s + ", ";
                        }
                        s = s + "" + ai[2] + " in warning (" + as[1] + ")";
                    }
                    if (ai[2] > 0) {
                        if (s.length() > 0) {
                            s = s + ", ";
                        }
                        s = s + "" + ai[2] + " in warning (" + as[1] + ")";
                    }
                    if (s.length() == 0) {
                        s = "all OK";
                    }
                    String s5 = "";
                    if (ai[4] > 0) {
                        if (s5.length() > 0) {
                            s5 = s5 + " and ";
                        }
                        s5 = s5 + itemString(ai[4], " group");
                    }
                    if (ai[0] > 0) {
                        if (s5.length() > 0) {
                            s5 = s5 + " and ";
                        }
                        s5 = s5 + itemString(ai[0], " monitor");
                    }
                    String s6 = flag ? "run" : "checked";
                    s = s5 + " " + s6 + ", " + s;
                    setProperty(pPercentGood, TextUtils.floatToString(
                            ((float) l / (float) i) * 100F, 0));
                    setProperty(pPercentWarning, TextUtils.floatToString(
                            ((float) k / (float) i) * 100F, 0));
                    setProperty(pPercentError, TextUtils.floatToString(
                            ((float) j / (float) i) * 100F, 0));
                    setProperty(pStateString, getProperty(pPercentGood)
                            + "% OK, " + s);
                    setProperty(pMeasurement, getMeasurement(pPercentGood));
                }
            }
        }
    }

    protected void checkSequentially(int ai[], String as[], boolean flag,
            long l, String s) {
        Array array = getMonitorsToRun();
        long l1 = getSettingAsLong("_CompositeStartupTime", 500);
        long l2 = getSettingAsLong("_CompositeCheckDelay", 500);
        SocketSession socketsession = null;
        if (flag) {
            socketsession = SocketSession.getSession(this);
            socketsession.allowClose = false;
        }
        int i = 0;
        AtomicMonitor atomicmonitor = (AtomicMonitor) array.at(i);
        do {
            if (atomicmonitor == null) {
                break;
            }
            try {
                if (socketsession != null) {
                    atomicmonitor.cachedSocketSession = socketsession;
                }
                Object obj = this;
                SiteViewObject siteviewobject = temporaryOwner;
                if (siteviewobject != null
                        && (this instanceof CompositeMonitor)) {
                    obj = siteviewobject;
                }
                atomicmonitor.setTemporaryOwner(((SiteViewObject) (obj)));
                boolean flag1 = atomicmonitor.runUpdate(true);
                if (flag1) {
                    progressString += "Running monitor "
                            + atomicmonitor.getProperty(pName) + "\n";
                    setProperty(pStateString, "checking "
                            + atomicmonitor.getProperty(pName) + "...");
                    try {
                        Thread.sleep(l1);
                    } catch (InterruptedException interruptedexception1) {
                    }
                    while (atomicmonitor.getProperty(pRunning).length() > 0) {
                        try {
                            Thread.sleep(l2);
                        } catch (InterruptedException interruptedexception2) {
                        }
                    }
                }
            } finally {
                updateStats(ai, as, atomicmonitor);
                atomicmonitor.setTemporaryOwner(null);
                if (socketsession != null) {
                    atomicmonitor.cachedSocketSession = null;
                }
            }
            i++;
            if (atomicmonitor.getProperty(pCategory).equals(ERROR_CATEGORY)) {
                if (s.equals("stop")) {
                    i = array.size() + 1;
                    progressString += "Skipping remaining monitors\n";
                } else if (s.equals("last") && i < array.size()) {
                    progressString += "Skipping to last monitor\n";
                    i = array.size() - 1;
                }
            }
            if (i < array.size()) {
                atomicmonitor = (AtomicMonitor) array.at(i);
            } else {
                atomicmonitor = null;
            }
            if (l > 0L && atomicmonitor != null) {
                try {
                    Thread.sleep(l);
                } catch (InterruptedException interruptedexception) {
                }
            }
        } while (true);
        if (socketsession != null) {
            socketsession.allowClose = true;
            socketsession.close();
        }
    }

    public SiteViewObject resolveObjectReference(String s) {
        SiteViewObject siteviewobject = null;
        if (s.startsWith(STEP_REFERENCE)) {
            int i = TextUtils.readInteger(s, STEP_REFERENCE.length());
            if (i >= 1) {
                Array array = getMonitorsToRun();
                if (i <= array.size()) {
                    siteviewobject = (SiteViewObject) array.at(i - 1);
                }
            }
        }
        return siteviewobject;
    }

    protected void addToMonitorList(Monitor monitor, Array array,
            HashMap hashmap) {
        if (monitor == this) {
            return;
        }
        String s = monitor.getProperty(pGroupID) + " "
                + monitor.getProperty(pID);
        if (hashmap.get(s) == null) {
            hashmap.put(s, monitor);
            array.add(monitor);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    protected Array getMonitorsToRun() {
        HashMap hashmap = new HashMap();
        Array array = new Array();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Enumeration enumeration = getMultipleValues(pItems);
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            String as[] = TextUtils.split(s);
            Object obj = null;
            Object obj1 = null;
            if (as.length == 1) {
                MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                        .getElement(as[0]);
                if (monitorgroup == null) {
                    continue;
                }
                Enumeration enumeration1 = monitorgroup.getMonitors();
                while (enumeration1.hasMoreElements()) {
                    Monitor monitor = (Monitor) enumeration1.nextElement();
                        if (monitor instanceof AtomicMonitor) {
                            addToMonitorList(monitor, array, hashmap);
                        }
                } 
            }
        else if (as.length > 1) {
                String s1 = as[0] + SiteViewGroup.ID_SEPARATOR + as[1];
                MonitorGroup monitorgroup1 = (MonitorGroup) siteviewgroup
                        .getElement(as[0]);
                if (monitorgroup1 != null) {
                    Monitor monitor1 = (Monitor) siteviewgroup.getElement(s1);
                    if (monitor1 != null) {
                        addToMonitorList(monitor1, array, hashmap);
                    }
                }
            }
        }
        return array;
    }

    String itemString(int i, String s) {
        if (s.length() == 0) {
            return "" + i;
        }
        if (i > 1) {
            return "" + i + s + "s";
        } else {
            return "" + i + s;
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pPercentGood);
        array.add(pPercentError);
        array.add(pPercentWarning);
        array.add(pItemsInError);
        array.add(pItemsInWarning);
        array.add(pItemsInGood);
        array.add(pItemsChecked);
        return array;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pItems) {
            if (s.trim().length() == 0) {
                hashmap.put(stringproperty, "no monitors selected");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String defaultTitle() {
        Enumeration enumeration = getMultipleValues(pItems);
        int i = 0;
        int j = 0;
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            if (s.indexOf(' ') >= 0) {
                i++;
            } else {
                j++;
            }
        }
        String s1 = "";
        if (j == 1) {
            s1 = s1 + "1 group";
        } else if (j > 1) {
            s1 = s1 + "" + j + " groups";
        }
        if (i == 1) {
            if (s1.length() > 0) {
                s1 = s1 + " and ";
            }
            s1 = s1 + "1 monitor";
        } else if (i > 1) {
            if (s1.length() > 0) {
                s1 = s1 + " and ";
            }
            s1 = s1 + "" + i + " monitors";
        }
        return s1;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pItems) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            Array array = CGI.getAllowedGroupIDsForAccount(httprequest);
            Enumeration enumeration = array.elements();
            Vector vector = new Vector();
            Vector vector1 = new Vector();
            String s = getFullID();
            while (enumeration.hasMoreElements()) {
                MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                        .getElement((String) enumeration.nextElement());
                if (monitorgroup != null) {
                    vector.addElement(monitorgroup.getProperty(pGroupID));
                    vector.addElement(monitorgroup.getProperty(pName));
                    Enumeration enumeration2 = monitorgroup.getMonitors();
                    while (enumeration2.hasMoreElements()) {
                        Monitor monitor = (Monitor) enumeration2.nextElement();
                        if (!(monitor instanceof SubGroup)
                                && !s.equals(monitor.getFullID())) {
                            vector1.addElement(monitor.getProperty(pGroupID)
                                    + " " + monitor.getProperty(pID));
                            vector1.addElement(monitorgroup.getProperty(pName)
                                    + ": " + monitor.getProperty(pName));
                        }
                    }
                }
            }
            for (Enumeration enumeration1 = vector1.elements(); enumeration1
                    .hasMoreElements(); vector.addElement(enumeration1
                    .nextElement())) {
            }
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            return getProperty(pNameItemsInError);
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public int getCostInLicensePoints() {
        return 0;
    }

    static {
        pItems = new ScalarProperty("_item", "");
        ((ScalarProperty) pItems).multiple = true;
        ((ScalarProperty) pItems).listSize = 8;
        pItems
                .setDisplayText("Items",
                        "select one or more groups and/or monitors that will be checked");
        pItems.setParameterOptions(true, 1, false);
        pPercentGood = new PercentProperty("percentGood");
        pPercentGood.setLabel("% items OK");
        pPercentGood.setStateOptions(1);
        pPercentError = new PercentProperty("percentError");
        pPercentError.setLabel("% items in error");
        pPercentError.setIsThreshold(true);
        pPercentWarning = new PercentProperty("percentWarning");
        pPercentWarning.setLabel("% items in warning");
        pPercentWarning.setIsThreshold(true);
        pItemsInError = new NumericProperty("itemsInError", "0");
        pItemsInError.setLabel("items in error");
        pItemsInError.setStateOptions(2);
        pNameItemsInError = new StringProperty("nameItemsInError", "");
        pNameItemsInError.setLabel("name of the items in error");
        pNameItemsInError.setStateOptions(3);
        pNameItemsInWarning = new StringProperty("nameItemsInWarning", "");
        pNameItemsInWarning.setLabel("name of the items in warning");
        pNameItemsInWarning.setStateOptions(4);
        pItemsInWarning = new NumericProperty("itemsInWarning", "0");
        pItemsInWarning.setLabel("items in warning");
        pItemsInWarning.setIsThreshold(true);
        pItemsInGood = new NumericProperty("itemsInGood", "0");
        pItemsInGood.setLabel("items OK");
        pItemsInGood.setIsThreshold(true);
        pItemsChecked = new NumericProperty("itemsChecked", "0");
        pItemsChecked.setLabel("items checked");
        pItemsChecked.setIsThreshold(true);
        StringProperty astringproperty[] = { pItems, pPercentError,
                pPercentWarning, pPercentGood, pItemsInError,
                pNameItemsInError, pNameItemsInWarning, pItemsInWarning,
                pItemsInGood, pItemsChecked };
        addProperties("COM.dragonflow.SiteView.CompositeBase", astringproperty);
    }
}
