/*
 * 
 * Created on 2005-2-16 16:17:55
 *
 * PortalFilter.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PortalFilter</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Utils.Expression;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// MonitorGroup, PortalSiteView, Monitor, AtomicMonitor,
// Portal

public class PortalFilter {

    HashMap rawQuery;

    String rawQueryString;

    HashMap itemFilter;

    HashMap monitorFilter;

    Array additionalFilters;

    boolean anyDataFilters;

    HashMap categoryFilter;

    HashMap typeFilter;

    String nameFilter;

    String statusFilter;

    Array expressionFilter;

    Array expressionStringFilter;

    HashMap propertyFilter;

    HashMap propertySetFilter;

    boolean oneLevel;

    public PortalFilter(String s) {
        this(queryStringToMap(s));
        rawQueryString = s;
    }

    public PortalFilter(HashMap hashmap) {
        rawQuery = null;
        rawQueryString = "";
        itemFilter = null;
        monitorFilter = null;
        additionalFilters = null;
        anyDataFilters = false;
        categoryFilter = null;
        typeFilter = null;
        nameFilter = "";
        statusFilter = "";
        expressionFilter = null;
        expressionStringFilter = null;
        propertyFilter = null;
        propertySetFilter = null;
        oneLevel = false;
        rawQuery = hashmap;
        oneLevel = TextUtils.getValue(hashmap, "oneLevel").length() > 0;
        categoryFilter = setupQueryMap(hashmap, "category");
        typeFilter = setupQueryMap(hashmap, "type");
        nameFilter = TextUtils.getValue(hashmap, "name");
        statusFilter = TextUtils.getValue(hashmap, "status");
        if (TextUtils.getValue(hashmap, "exp").length() > 0) {
            expressionFilter = new Array();
            expressionStringFilter = new Array();
            for (Enumeration enumeration = hashmap.values("exp"); enumeration
                    .hasMoreElements();) {
                String s = (String) enumeration.nextElement();
                Object obj = null;
                try {
                    Expression expression = Expression.parseString(s);
                    expressionFilter.add(expression);
                    expressionStringFilter.add(s);
                } catch (Exception exception) {
                    LogManager.log("RunMonitor",
                            "Parse error in XML Query expression: " + s);
                }
            }

        }
        anyDataFilters = categoryFilter != null || typeFilter != null
                || nameFilter.length() > 0 || statusFilter.length() > 0
                || expressionFilter != null || expressionStringFilter != null;
        String as[];
        for (Enumeration enumeration1 = hashmap.values("item"); enumeration1
                .hasMoreElements(); addItemToFilter(as[0], as[1], as[2])) {
            String s1 = (String) enumeration1.nextElement();
            as = Portal.getIDComponents(s1);
        }

        if (monitorFilter != null && itemFilter == null) {
            itemFilter = new HashMap();
        }
    }

    public void addFilter(PortalFilter portalfilter) {
        if (additionalFilters == null) {
            additionalFilters = new Array();
        }
        additionalFilters.add(portalfilter);
    }

    public PortalFilter() {
        rawQuery = null;
        rawQueryString = "";
        itemFilter = null;
        monitorFilter = null;
        additionalFilters = null;
        anyDataFilters = false;
        categoryFilter = null;
        typeFilter = null;
        nameFilter = "";
        statusFilter = "";
        expressionFilter = null;
        expressionStringFilter = null;
        propertyFilter = null;
        propertySetFilter = null;
        oneLevel = false;
    }

    void addItemToFilter(String s, String s1, String s2) {
        if (s.length() > 0) {
            if (s1.length() > 0 && s2.length() > 0) {
                if (monitorFilter == null) {
                    monitorFilter = new HashMap();
                }
                monitorFilter.put(Portal.makePortalID(s, s1, s2), "true");
                monitorFilter.put(Portal.makePortalID(s, s1, ""), "true");
                MonitorGroup monitorgroup = (MonitorGroup) Portal.getPortal()
                        .getElement(Portal.makePortalID(s, s1, ""));
                do {
                    if (monitorgroup == null) {
                        break;
                    }
                    String s3 = monitorgroup.getProperty(MonitorGroup.pParent);
                    if (s3.length() == 0) {
                        break;
                    }
                    String s4 = Portal.makePortalID(s, s3, "");
                    monitorFilter.put(s4, "true");
                    monitorgroup = (MonitorGroup) Portal.getPortal()
                            .getElement(s4);
                } while (true);
                monitorFilter.put(Portal.makePortalID(s, "", ""), "true");
            } else {
                if (itemFilter == null) {
                    itemFilter = new HashMap();
                }
                if (s1.length() == 0) {
                    itemFilter.put(s, "all");
                } else {
                    Object obj = itemFilter.get(s);
                    HashMap hashmap = null;
                    if (obj == null) {
                        hashmap = new HashMap();
                        itemFilter.put(s, hashmap);
                    } else {
                        if (obj instanceof String) {
                            return;
                        }
                        hashmap = (HashMap) obj;
                    }
                    if (s2.length() == 0) {
                        hashmap.put(s1, "all");
                    } else {
                        Object obj1 = hashmap.get(s2);
                        HashMap hashmap1 = null;
                        if (obj1 == null) {
                            hashmap1 = new HashMap();
                            hashmap.put(s1, hashmap1);
                        } else {
                            if (obj1 instanceof String) {
                                return;
                            }
                            hashmap1 = (HashMap) obj1;
                        }
                        hashmap1.put(s2, "all");
                    }
                }
            }
        }
    }

    public static HashMap queryStringToMap(String s) {
        String s1 = null;
        if (Portal.isQueryID(s)) {
            HashMap hashmap = Portal.getQuery(s);
            if (hashmap != null) {
                s1 = TextUtils.getValue(hashmap, "_query");
            } else {
                LogManager.log("Error", "Could not find query " + s);
                s1 = "item=";
            }
        } else {
            s1 = s;
        }
        String as[] = TextUtils.split(s1, "&");
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        for (int i = 0; i < as.length; i++) {
            int j = as[i].indexOf("=");
            if (j >= 0) {
                String s2 = as[i].substring(0, j);
                String s3 = as[i].substring(j + 1);
                hashmapordered.add(s2, HTTPRequest.decodeString(s3));
            }
        }

        return hashmapordered;
    }

    boolean itemAllowed(String s) {
        String as[] = Portal.getIDComponents(s);
        return itemAllowed(as[0], as[1], as[2]);
    }

    boolean itemAllowed(String s, String s1, String s2) {
        boolean flag = allowedByMonitorFilter(s, s1, s2)
                || allowedByItemFilter(s, s1, s2);
        if (flag && additionalFilters != null) {
            int i = 0;
            do {
                if (i >= additionalFilters.size()) {
                    break;
                }
                PortalFilter portalfilter = (PortalFilter) additionalFilters
                        .at(i);
                flag = portalfilter.itemAllowed(s, s1, s2);
                if (!flag) {
                    break;
                }
                i++;
            } while (true);
        }
        return flag;
    }

    boolean monitorAllowed(Monitor monitor) {
        boolean flag = true;
        if (anyDataFilters) {
            flag = allowMonitor(monitor);
        }
        if (flag && additionalFilters != null) {
            int i = 0;
            do {
                if (i >= additionalFilters.size()) {
                    break;
                }
                PortalFilter portalfilter = (PortalFilter) additionalFilters
                        .at(i);
                flag = portalfilter.allowMonitor(monitor);
                if (!flag) {
                    break;
                }
                i++;
            } while (true);
        }
        return flag;
    }

    boolean filteringOnData() {
        boolean flag = true;
        if (anyDataFilters) {
            return true;
        }
        if (flag && additionalFilters != null) {
            for (int i = 0; i < additionalFilters.size(); i++) {
                PortalFilter portalfilter = (PortalFilter) additionalFilters
                        .at(i);
                if (portalfilter.filteringOnData()) {
                    return true;
                }
            }

        }
        return false;
    }

    boolean allowedByMonitorFilter(String s, String s1, String s2) {
        if (monitorFilter == null) {
            return false;
        } else {
            return monitorFilter.get(Portal.makePortalID(s, s1, s2)) != null;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param s2
     * @return
     */
    boolean allowedByItemFilter(String s, String s1, String s2) {
        //TODO need review
        Object obj1;
        boolean flag;
        label0: {
            if (itemFilter == null) {
                return true;
            }
            Object obj = itemFilter.get(s);
            if (obj == null) {
                return false;
            }
            if (obj instanceof String) {
                return true;
            }
            if (s1.length() == 0) {
                return true;
            }
            PortalSiteView portalsiteview = (PortalSiteView) Portal
                    .getPortal().getElement(s + Portal.PORTAL_ID_SEPARATOR);
            if (portalsiteview == null) {
                return false;
            }
            HashMap hashmap = (HashMap) obj;
            obj1 = hashmap.get(s1);
            flag = false;
            if (obj1 != null) {
                flag = true;
                break label0;
            }
            if (oneLevel) {
                break label0;
            }
            Enumeration enumeration = hashmap.keys();
            String s3;
            do {
                if (!enumeration.hasMoreElements()) {
                    break label0;
                }
                s3 = (String) enumeration.nextElement();
            } while (!groupsAreRelated(portalsiteview, s1, s3));
            flag = true;
        }
        if (!flag) {
            return false;
        }
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return true;
        }
        if (s2.length() == 0) {
            return true;
        } else {
            HashMap hashmap1 = (HashMap) obj1;
            return hashmap1.get(s2) != null;
        }
    }

    public static boolean groupsAreRelated(PortalSiteView portalsiteview,
            String s, String s1) {
        do {
            if (s.length() == 0) {
                break;
            }
            if (s.equals(s1)) {
                return true;
            }
            Monitor monitor = (Monitor) portalsiteview.getElement(s);
            if (monitor == null) {
                break;
            }
            s = monitor.getProperty("_parent");
        } while (true);
        return false;
    }

    boolean allowMonitor(Monitor monitor) {
        if (categoryFilter != null
                && categoryFilter.get(monitor.getProperty(Monitor.pCategory)) == null) {
            return false;
        }
        if (nameFilter.length() > 0
                && !TextUtils.match(monitor.getProperty(Monitor.pName),
                        nameFilter)) {
            return false;
        }
        if (monitor instanceof AtomicMonitor) {
            if (typeFilter != null
                    && typeFilter
                            .get(monitor.getProperty(AtomicMonitor.pClass)) == null) {
                return false;
            }
            if (statusFilter.length() > 0
                    && !TextUtils.match(monitor
                            .getProperty(AtomicMonitor.pStateString),
                            statusFilter)) {
                return false;
            }
        }
        if (expressionFilter != null) {
            boolean flag = false;
            for (int i = 0; i < expressionFilter.size(); i++) {
                Expression expression = (Expression) expressionFilter.at(i);
                try {
                    Boolean boolean1 = (Boolean) expression
                            .interpretExpression(monitor, null);
                    if (boolean1.booleanValue()) {
                        flag = true;
                    }
                } catch (Exception exception) {
                    LogManager.log("RunMonitor",
                            "Interpreter exception in XML Query: "
                                    + exception.getMessage());
                }
            }

            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public static HashMap setupQueryMap(HashMap hashmap, String s) {
        HashMapOrdered hashmapordered = null;
        String s1;
        for (Enumeration enumeration = hashmap.values(s); enumeration
                .hasMoreElements(); hashmapordered.put(s1, "true")) {
            s1 = (String) enumeration.nextElement();
            if (hashmapordered == null) {
                hashmapordered = new HashMapOrdered(true);
            }
        }

        return hashmapordered;
    }

    public String getDescription() {
        if (Portal.isQueryID(rawQueryString)) {
            HashMap hashmap = Portal.getQuery(rawQueryString);
            if (hashmap != null) {
                return TextUtils.getValue(hashmap, "_title");
            } else {
                return "could not find query ID " + rawQueryString;
            }
        }
        Enumeration enumeration = rawQuery.values("item");
        StringBuffer stringbuffer = new StringBuffer();
        String s = "all servers";
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            String as[] = Portal.getIDComponents(s1);
            Object obj = null;
            Object obj1 = null;
            Object obj2 = null;
            if (as[0].length() > 0) {
                PortalSiteView portalsiteview = (PortalSiteView) Portal
                        .getPortal().getElementByID(as[0]);
                if (portalsiteview != null) {
                    if (as[1].length() > 0) {
                        MonitorGroup monitorgroup = (MonitorGroup) portalsiteview
                                .getElementByID(as[1]);
                        if (monitorgroup != null) {
                            if (as[2].length() > 0) {
                                Monitor monitor = (Monitor) monitorgroup
                                        .getElementByID(as[2]);
                                if (monitor != null) {
                                    TextUtils
                                            .addToBuffer(
                                                    stringbuffer,
                                                    "Monitor \""
                                                            + monitor
                                                                    .getProperty(Monitor.pName)
                                                            + "\" in Group \""
                                                            + monitorgroup
                                                                    .getProperty(Monitor.pName)
                                                            + "\" on Server \""
                                                            + portalsiteview
                                                                    .getProperty(PortalSiteView.pTitle)
                                                            + "\"");
                                } else {
                                    TextUtils
                                            .addToBuffer(
                                                    stringbuffer,
                                                    "Missing Monitor ID \""
                                                            + as[2]
                                                            + "\" in Group \""
                                                            + monitorgroup
                                                                    .getProperty(Monitor.pName)
                                                            + "\" on Server \""
                                                            + portalsiteview
                                                                    .getProperty(PortalSiteView.pTitle)
                                                            + "\"");
                                }
                            } else {
                                TextUtils
                                        .addToBuffer(
                                                stringbuffer,
                                                "Group \""
                                                        + monitorgroup
                                                                .getProperty(Monitor.pName)
                                                        + "\" on Server \""
                                                        + portalsiteview
                                                                .getProperty(PortalSiteView.pTitle)
                                                        + "\"");
                            }
                        } else {
                            TextUtils
                                    .addToBuffer(
                                            stringbuffer,
                                            "Missing Group ID \""
                                                    + as[1]
                                                    + "\" on Server \""
                                                    + portalsiteview
                                                            .getProperty(PortalSiteView.pTitle)
                                                    + "\"");
                        }
                    } else {
                        TextUtils.addToBuffer(stringbuffer, "Server \""
                                + portalsiteview
                                        .getProperty(PortalSiteView.pTitle)
                                + "\"");
                    }
                } else {
                    TextUtils.addToBuffer(stringbuffer, "Missing Server ID \""
                            + as[0] + "\"");
                }
            } else {
                return s;
            }
        }
        return stringbuffer.toString();
    }

    public void print() {
        if (itemFilter == null) {
            System.out.println("All servers allowed");
        } else {
            for (Enumeration enumeration = itemFilter.keys(); enumeration
                    .hasMoreElements();) {
                String s = (String) enumeration.nextElement();
                Object obj = itemFilter.get(s);
                if (obj instanceof String) {
                    System.out.println(s + ": all groups allowed");
                } else {
                    System.out.println(s);
                    HashMap hashmap = (HashMap) obj;
                    Enumeration enumeration3 = hashmap.keys();
                    while (enumeration3.hasMoreElements()) {
                        String s1 = (String) enumeration3.nextElement();
                        Object obj1 = hashmap.get(s1);
                        if (obj1 instanceof String) {
                            System.out.println("  " + s1
                                    + ": all monitors allowed");
                        } else {
                            System.out.println("  " + s1);
                            HashMap hashmap1 = (HashMap) obj1;
                            Enumeration enumeration4 = hashmap1.keys();
                            while (enumeration4.hasMoreElements()) {
                                String s2 = (String) enumeration4.nextElement();
                                System.out.println("    " + s2);
                            }
                        }
                    }
                }
            }

        }
        if (nameFilter.length() > 0) {
            System.out.println("Name: " + nameFilter);
        }
        if (statusFilter.length() > 0) {
            System.out.println("Status: " + statusFilter);
        }
        if (categoryFilter != null) {
            System.out.print("Category:");
            for (Enumeration enumeration1 = categoryFilter.keys(); enumeration1
                    .hasMoreElements(); System.out.print(" "
                    + enumeration1.nextElement())) {
            }
            System.out.println();
        }
        if (typeFilter != null) {
            System.out.print("Monitor Type:");
            for (Enumeration enumeration2 = typeFilter.keys(); enumeration2
                    .hasMoreElements(); System.out.print(" "
                    + enumeration2.nextElement())) {
            }
            System.out.println();
        }
        if (additionalFilters != null) {
            System.out.println("additional filters:");
            for (int i = 0; i < additionalFilters.size(); i++) {
                PortalFilter portalfilter = (PortalFilter) additionalFilters
                        .at(i);
                System.out
                        .println("---------------- additional filter -------------------");
                portalfilter.print();
                System.out
                        .println("------------------------------------------------------");
            }

        }
    }
}
