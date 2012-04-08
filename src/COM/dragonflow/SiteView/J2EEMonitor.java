/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * J2EEMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>J2EEMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jgl.Array;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.BrowsableProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StatefulMonitor.J2EEConnection;
import COM.dragonflow.StatefulMonitor.StatefulConnection;
import COM.dragonflow.StatefulMonitor.StatefulConnectionUser;
import COM.dragonflow.StatefulMonitor.StatefulConnsMgr;
import COM.dragonflow.Utils.TextUtils;

//import com.dragonflow.topaz.j2ee.aggregator.Node;
//import com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, MonitorGroup, Monitor, BrowsableMonitor,
// SiteViewGroup, MasterConfig, TopazInfo

public abstract class J2EEMonitor extends AtomicMonitor implements
        StatefulConnectionUser, BrowsableMonitor {
    static class MonitorInfo {

        String target;

        Set counters;

        int interval;

        MonitorInfo(String s, Set set, int i) {
            target = s;
            counters = set;
            interval = i;
        }
    }

    protected class CounterResult {

        private String ART;

        private String HPS;

        public String getART() {
            return ART;
        }

        public String getHPS() {
            return HPS;
        }

        public CounterResult(String s, String s1) {
            super();
            ART = s;
            HPS = s1;
        }
    }

    private static Map activeMonitors = Collections
            .synchronizedMap(new HashMap());

    protected static final String NAME_STR = "_browseName";

    protected static final String ID_STR = "_browseID";

    protected static final String SERVLET_COUNTER = "J2EE: Web/group: Servlet";

    protected static final String JSP_COUNTER = "J2EE: Web/group: JSP";

    protected static final String SESSION_COUNTER = "J2EE: EJB/group: SessionBean";

    protected static final String ENTITY_COUNTER = "J2EE: EJB/group: EntityBean";

    protected static final String JDBC_COUNTER = "J2EE: DB/group: JDBC";

    protected static final String LOOKUP_COUNTER = "J2EE: JNDI/group: Lookup";

    protected static final String ART_HPS_ARRAY[] = { "ART", "HPS" };

    protected static StringProperty pHostname;

    protected static StringProperty pTarget;

    protected static StringProperty pPort;

    protected static BrowsableProperty pBrowseCounters;

    protected static StringProperty pCounterNames[];

    protected static StringProperty pCounterIDs[];

    protected static int nMaxCounters;

    protected static StringProperty pStatus;

    protected static final int ART_PERCISION = 2;

    protected static final int HPS_PERCISION = 5;

    protected static NumberFormat artFormat;

    protected static NumberFormat hpsFormat;

    protected static StatefulConnsMgr conMgr;

    public J2EEMonitor() {
    }

    public boolean isUsingCountersCache() {
        return false;
    }

    public Object getUniqueID() {
        return getFullID();
    }

    public Set getCounters() {
        HashSet hashset = new HashSet();
        int i = 0;
        do {
            if (i >= nMaxCounters) {
                break;
            }
            String s = getProperty(pCounterNames[i]);
            if (s.equals("")) {
                break;
            }
            hashset.add(s);
            i++;
        } while (true);
        return hashset;
    }

    public int getInterval() {
        return Integer.parseInt(getProperty(pFrequency));
    }

    public Object getConnID(Class class1) {
        return getProperty(pHostname) + ":" + getProperty(pPort);
    }

    public StatefulConnection createConnection(Class class1,
            StringBuffer stringbuffer) {
        return new J2EEConnection(this);
    }

    public String getHostname() {
        String s = getProperty(pTarget);
        if (s == null) {
            s = getProperty(pHostname);
        }
        return s;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pBrowseCounters) {
            String s1 = getProperty(pCounterIDs[0]);
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            int i = 0;
            do {
                if (i >= nMaxCounters) {
                    break;
                }
                String s2 = getProperty(pCounterIDs[i]);
                if (s2.length() == 0) {
                    break;
                }
                i++;
            } while (true);
            if (i > getMaxValues()) {
                hashmap.put(stringproperty, "You have selected " + i
                        + " counters. Maximum counters allowed is "
                        + getMaxValues());
            }
            return s;
        }
        if (stringproperty == pTarget && s.length() == 0) {
            return getProperty(pHostname);
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public void notifyDelete() {
        activeMonitors.remove(getFullID());
        conMgr.removeUserFromConn(this);
    }

    public void notifyAdd() {
        J2EEConnection j2eeconnection = (J2EEConnection) conMgr.getConnection(
                this, null);
        j2eeconnection.addMonitor(this);
    }

    public void notifyUpdate() {
        J2EEConnection j2eeconnection = (J2EEConnection) conMgr.getConnection(
                this, null);
        j2eeconnection.updateMonitor(this);
    }

    private int getNumCounters() {
        int i = 0;
        do {
            if (i >= getMaxCounters()) {
                break;
            }
            String s = getProperty(pCounterNames[i]);
            if (s.length() == 0) {
                break;
            }
            i++;
        } while (true);
        return i;
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pHostname);
        array.add(pPort);
        return array;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        J2EEConnection j2eeconnection = (J2EEConnection) conMgr.getConnection(
                this, null);
        return j2eeconnection.getMetricList(stringbuffer);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private boolean doIStillExist() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Enumeration enumeration = siteviewgroup.getMonitors();
        while (enumeration.hasMoreElements()) {
                MonitorGroup monitorgroup = (MonitorGroup) enumeration
                        .nextElement();
                Enumeration enumeration1 = monitorgroup.getMonitors();
                while (enumeration1.hasMoreElements()) {
                    Monitor monitor = (Monitor) enumeration1.nextElement();
                    if (monitor.getFullID().equals(getFullID())) {
                        return true;
                    }
                }
        }
        return false;
    }

    protected boolean update() {
        if (!doIStillExist()) {
            LogManager
                    .log(
                            "RunMonitor",
                            getProperty(pName)
                                    + ": Warning - update() called after monitor was removed. Ignoring");
            return true;
        }
        String s = (String) getConnID(null);
        int i = getInterval();
        Set set = getCounters();
        MonitorInfo monitorinfo = (MonitorInfo) activeMonitors.get(getFullID());
        if (monitorinfo == null || !monitorinfo.target.equals(s)) {
            activeMonitors.put(getFullID(), new MonitorInfo(s, set, i));
            notifyAdd();
        } else if (monitorinfo.interval != i
                || !monitorinfo.counters.equals(set)) {
            activeMonitors.put(getFullID(), new MonitorInfo(s, set, i));
            notifyUpdate();
        }
        Set set1 = getAllCounters();
        HashMap hashmap = new HashMap();
        J2EEConnection j2eeconnection = (J2EEConnection) conMgr.getConnection(
                this, null);
        String s1 = j2eeconnection.getLastError(this);
        String s2;
        for (Iterator iterator = set1.iterator(); iterator.hasNext(); hashmap
                .put(s2, new CounterResult("n/a", "n/a"))) {
            s2 = (String) iterator.next();
            String as[] = J2EEConnection.parseName(s2);
        }

//        SiteViewAggregator siteviewaggregator = j2eeconnection
//                .getAggregatorAndSwap(this);
//        try {
//            Node node = siteviewaggregator.getNode(new String[0]);
//            getResults(node, hashmap, null);
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
        processResults(hashmap, s1);
        return true;
    }

//    private void getResults(Node node, Map map, String s) {
//        if (s != null && map.containsKey(s)) {
//            Double double1 = (Double) node.getValue("ART");
//            Double double2 = (Double) node.getValue("HPS");
//            map.put(s, new CounterResult(artFormat.format(double1), hpsFormat
//                    .format(double2)));
//        }
//        Set set = node.getSubNodeNames();
//        Iterator iterator = set.iterator();
//        do {
//            if (!iterator.hasNext()) {
//                break;
//            }
//            String s1 = (String) iterator.next();
//            if (!s1.startsWith("timestamp")) {
//                Node node1 = node.getSubNode(s1);
//                String s2;
//                if (s == null) {
//                    s2 = J2EEConnection.escapeString(s1);
//                } else {
//                    s2 = s + "/" + J2EEConnection.escapeString(s1);
//                }
//                getResults(node1, map, s2);
//            }
//        } while (true);
//    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_J2EEMonitorMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public String getBrowseName() {
        return "_browseName";
    }

    public String getBrowseID() {
        return "_browseID";
    }

    public String setBrowseName(Array array) {
        return J2EEConnection.getNameFromArray(array);
    }

    public String setBrowseID(Array array) {
        return "*";
    }

    protected int getMaxValues() {
        return nMaxCounters;
    }

    public int getCostInLicensePoints() {
        int i = 0;
        for (int j = 0; j < nMaxCounters
                && getProperty(pCounterNames[j]).length() > 0; j++) {
            i++;
        }

        return i * 1;
    }

    protected abstract void processResults(Map map, String s);

    protected abstract Set getAllCounters();

    public String getTestURL() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer
                .append("/SiteView/cgi/go.exe/SiteView?page=J2EERepostMetrics");
        stringbuffer.append("&monitor=" + getFullID());
        stringbuffer.append("&action=prompt");
        return stringbuffer.toString();
    }

    public boolean isLoggingToTopaz() {
        return false;
    }

    public boolean isServerBased() {
        return true;
    }

    public boolean manageBrowsableSelectionsByID() {
        return false;
    }

    public boolean areBrowseIDsEqual(String s, String s1) {
        if (s == null || s1 == null) {
            return false;
        } else {
            return s.equals(s1);
        }
    }

    static {
        artFormat = NumberFormat.getNumberInstance();
        hpsFormat = NumberFormat.getNumberInstance();
        conMgr = StatefulConnsMgr
                .getManager(COM.dragonflow.StatefulMonitor.J2EEConnection.class);
        artFormat.setMaximumFractionDigits(2);
        artFormat.setMinimumFractionDigits(1);
        artFormat.setGroupingUsed(false);
        hpsFormat.setMaximumFractionDigits(5);
        hpsFormat.setMinimumFractionDigits(1);
        hpsFormat.setGroupingUsed(false);
        Array array = new Array();
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_J2EEMonitorMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 2500;
        }
        pHostname = new StringProperty("_server");
        pHostname.setDisplayText("Hostname", "the hostname of the server");
        pHostname.setParameterOptions(false, 4, false);
        array.add(pHostname);
        pPort = new StringProperty("_port", "2003");
        pPort.setDisplayText("Port", "port number on server");
        pPort.setParameterOptions(false, 4, false);
        array.add(pPort);
        pTarget = new StringProperty("_target");
        pTarget
                .setDisplayText("Target",
                        "the logical name of the server. If empty, the hostname will be used.");
        pTarget.setParameterOptions(true, 5, false);
        array.add(pTarget);
        pBrowseCounters = new BrowsableProperty("_browse", "browseName");
        pBrowseCounters.setDisplayText("Counters",
                "the current selection of counters.");
        pBrowseCounters.setParameterOptions(true, 1, false);
        array.add(pBrowseCounters);
        pStatus = new StringProperty("status");
        array.add(pStatus);
        pCounterNames = new StringProperty[nMaxCounters];
        pCounterIDs = new StringProperty[nMaxCounters];
        for (int i = 0; i < nMaxCounters; i++) {
            pCounterNames[i] = new StringProperty("_browseName" + (i + 1));
//            pCounterNames[i].setDisplayText("Counter " + (i + 1) + " Name",
//                    TopazInfo.getTopazName() + " Counter Name");
            pCounterNames[i].setParameterOptions(false, i + 1, false);
            array.add(pCounterNames[i]);
            pCounterIDs[i] = new StringProperty("_browseID" + (i + 1));
//            pCounterIDs[i].setDisplayText("Counter " + (i + 1) + " ID",
//                    TopazInfo.getTopazName() + " Counter ID");
            pCounterIDs[i].setParameterOptions(false, nMaxCounters + i + 1,
                    false);
            array.add(pCounterIDs[i]);
        }

        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int j = 0; j < array.size(); j++) {
            astringproperty[j] = (StringProperty) array.at(j);
        }

        addProperties("COM.dragonflow.SiteView.J2EEMonitor", astringproperty);
    }
}
