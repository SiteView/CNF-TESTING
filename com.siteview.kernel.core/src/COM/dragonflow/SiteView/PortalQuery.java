/*
 * 
 * Created on 2005-2-16 16:18:25
 *
 * PortalQuery.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PortalQuery</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PQVPrintXML, PQVMatchingObjects, PortalFilter, PortalSiteView,
// MonitorGroup, Rule, Monitor, SubGroup,
// MasterConfig, User, PortalQueryVisitor, Portal,
// Action

public class PortalQuery {

    PortalFilter itemFilter;

    boolean addAlerts;

    public static final int FIND_NO_OBJECTS = 0;

    public static final int FIND_MONITORS = 1;

    public static final int FIND_GROUPS = 2;

    public static final int FIND_SERVERS = 4;

    public static final int FIND_ALL = 7;

    int findObjects;

    HTTPRequest request;

    HashMap config;

    PortalQueryVisitor visitor;

    boolean debug;

    public PortalQuery(HashMap hashmap, PrintWriter printwriter,
            HTTPRequest httprequest) {
        this(hashmap, ((PortalQueryVisitor) (new PQVPrintXML(printwriter))),
                httprequest);
    }

    public PortalQuery(HashMap hashmap, StringBuffer stringbuffer,
            HTTPRequest httprequest) {
        this(hashmap, ((PortalQueryVisitor) (new PQVPrintXML(stringbuffer))),
                httprequest);
    }

    public PortalQuery(HashMap hashmap, Array array, HTTPRequest httprequest) {
        this(hashmap, ((PortalQueryVisitor) (new PQVMatchingObjects(array))),
                httprequest);
    }

    public PortalQuery(HashMap hashmap, PortalQueryVisitor portalqueryvisitor,
            HTTPRequest httprequest) {
        itemFilter = null;
        addAlerts = false;
        findObjects = 7;
        request = null;
        config = null;
        visitor = null;
        debug = false;
        visitor = portalqueryvisitor;
        if (httprequest == null) {
            request = new HTTPRequest();
            request.setUser("administrator");
        } else {
            request = httprequest;
        }
        config = MasterConfig.getMasterConfig();
        itemFilter = new PortalFilter(hashmap);
        User user = request.getUser();
        PortalFilter portalfilter = new PortalFilter(user.getProperty("_query"));
        itemFilter.addFilter(portalfilter);
        if (TextUtils.getValue(hashmap, "serverDepth").length() > 0) {
            findObjects = 4;
        } else if (TextUtils.getValue(hashmap, "groupDepth").length() > 0) {
            findObjects = 6;
        }
        if (TextUtils.getValue(hashmap, "trace").length() > 0) {
            debug = true;
        }
        portalqueryvisitor.initialize(hashmap, this);
    }

    public boolean findingServers() {
        return (findObjects & 4) > 0;
    }

    public boolean findingGroups() {
        return (findObjects & 2) > 0;
    }

    public boolean findingMonitors() {
        return (findObjects & 1) > 0;
    }

    private boolean searchGroups() {
        return findingGroups() || findingMonitors();
    }

    public boolean filteringOnData() {
        return itemFilter.filteringOnData();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    public Array getServers() {
        Array array = new Array();
        Enumeration enumeration = Portal.getPortal().getElements();
        while (enumeration.hasMoreElements()) {
            PortalSiteView portalsiteview = (PortalSiteView) enumeration
                    .nextElement();
            if (itemFilter.itemAllowed(portalsiteview.getFullID())) {
                array.add(portalsiteview);
            }
        } 
        return array;
    }

    public void addFilter(PortalFilter portalfilter) {
        itemFilter.addFilter(portalfilter);
    }

    public Array getSortedGroupIDs(PortalSiteView portalsiteview) {
        Array array = new Array();
        int i = 0;
        Array array1 = portalsiteview.getGroups();
        for (int j = 0; j < array1.size(); j++) {
            MonitorGroup monitorgroup = (MonitorGroup) array1.at(j);
            if (monitorgroup.getProperty(MonitorGroup.pParent).length() == 0) {
                array.add(monitorgroup.getProperty(Monitor.pID));
                monitorgroup.setProperty("groupLevel", "0");
                findSubgroups(portalsiteview, monitorgroup, array, i + 1);
            }
        }

        return array;
    }

    public void printXML() {
        runQuery();
    }

    public void runQuery() {
        boolean flag = visitor.enterprisePre();
        if (flag) {
            Array array = getServers();
            PortalSiteView portalsiteview;
            for (Enumeration enumeration = array.elements(); enumeration
                    .hasMoreElements(); processSiteView(portalsiteview)) {
                portalsiteview = (PortalSiteView) enumeration.nextElement();
                if (debug) {
                    PortalSiteView _tmp = portalsiteview;
                    TextUtils.debugPrint("process siteview: "
                            + portalsiteview
                                    .getProperty(PortalSiteView.pTitle));
                }
            }

        }
        visitor.enterprisePost();
    }

    void processSiteView(PortalSiteView portalsiteview) {
        boolean flag = visitor.siteviewPre(portalsiteview);
        if (flag) {
            if (debug) {
                PortalSiteView _tmp = portalsiteview;
                TextUtils.debugPrint("search groups: "
                        + portalsiteview.getProperty(PortalSiteView.pTitle));
            }
            Array array = getSortedGroupIDs(portalsiteview);
            for (int i = 0; i < array.size(); i++) {
                String s = (String) array.at(i);
                MonitorGroup monitorgroup = (MonitorGroup) portalsiteview
                        .getElement(s);
                if (monitorgroup == null
                        || !itemFilter.itemAllowed(monitorgroup.getFullID())) {
                    continue;
                }
                if (debug) {
                    MonitorGroup _tmp1 = monitorgroup;
                    TextUtils.debugPrint("process group: "
                            + monitorgroup.getProperty(MonitorGroup.pName));
                }
                processGroup(monitorgroup, portalsiteview);
            }

            if (addAlerts) {
                Array array1 = portalsiteview.getElementsOfClass(
                        "COM.dragonflow.SiteView.Rule", false, false);
                Rule rule;
                for (Enumeration enumeration = array1.elements(); enumeration
                        .hasMoreElements(); processAlert(rule, null, null,
                        portalsiteview)) {
                    rule = (Rule) enumeration.nextElement();
                }

            }
        }
        visitor.siteviewPost(portalsiteview);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param monitorgroup
     * @param portalsiteview
     */
    void processGroup(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        boolean flag = visitor.groupPre(monitorgroup, portalsiteview);
        if (flag) {
            if (addAlerts) {
                Array array = monitorgroup.getElementsOfClass(
                        "COM.dragonflow.SiteView.Rule", false, false);
                Rule rule;
                for (Enumeration enumeration1 = array.elements(); enumeration1
                        .hasMoreElements(); processAlert(rule, null,
                        monitorgroup, portalsiteview)) {
                    rule = (Rule) enumeration1.nextElement();
                }

            }
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (itemFilter.itemAllowed(monitor.getFullID())
                        && itemFilter.monitorAllowed(monitor)) {
                    if (debug) {
                        Monitor _tmp = monitor;
                        TextUtils.debugPrint("process monitor: "
                                + monitor.getProperty(Monitor.pName));
                    }
                    processMonitor(monitor, monitorgroup, portalsiteview);
                }
            } 
        }
        visitor.groupPost(monitorgroup, portalsiteview);
    }

    void processMonitor(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        boolean flag = visitor.monitorPre(monitor, monitorgroup,
                portalsiteview);
        if (flag && addAlerts) {
            Array array = monitor.getElementsOfClass(
                    "COM.dragonflow.SiteView.Rule", false, false);
            Rule rule;
            for (Enumeration enumeration = array.elements(); enumeration
                    .hasMoreElements(); processAlert(rule, monitor,
                    monitorgroup, portalsiteview)) {
                rule = (Rule) enumeration.nextElement();
            }

        }
        visitor.monitorPost(monitor, monitorgroup, portalsiteview);
    }

    Action createAction(Rule rule) {
        String s = rule.getProperty(Rule.pAction);
        Action action = Action.createAction(s);
        action.setOwner(rule);
        int i = s.indexOf(" ");
        String as[];
        if (i >= 0) {
            as = TextUtils.split(s.substring(i + 1, s.length()));
        } else {
            as = new String[0];
        }
        Array array = new Array();
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        for (int j = 0; j < as.length; j++) {
            int k = as[j].indexOf("=");
            if (k == -1) {
                array.add(as[j]);
            } else {
                hashmapordered.add(as[j].substring(0, k), as[j]
                        .substring(k + 1));
            }
        }

        action.initializeFromArguments(array, hashmapordered);
        return action;
    }

    void processAlert(Rule rule, Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        visitor.alertPre(rule, monitor, monitorgroup, portalsiteview);
        visitor.alertPost(rule, monitor, monitorgroup, portalsiteview);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param portalsiteview
     * @param monitorgroup
     * @param array
     * @param i
     */
    void findSubgroups(PortalSiteView portalsiteview,
            MonitorGroup monitorgroup, Array array, int i) {
        Enumeration enumeration = monitorgroup.getMonitors();
        while (enumeration.hasMoreElements()) {
            Monitor monitor = (Monitor) enumeration.nextElement();
            if (monitor instanceof SubGroup) {
                String s = monitor.getProperty(SubGroup.pGroup);
                MonitorGroup monitorgroup1 = (MonitorGroup) portalsiteview
                        .getElement(s);
                if (monitorgroup1 != null) {
                    array.add(monitorgroup1.getProperty(Monitor.pID));
                    monitorgroup1.setProperty("groupLevel", "" + i);
                    findSubgroups(portalsiteview, monitorgroup1, array, i + 1);
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {
    }
}
