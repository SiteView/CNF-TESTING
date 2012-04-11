/*
 * 
 * Created on 2005-2-16 16:21:50
 *
 * PQVPrintXML.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PQVPrintXML</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalQueryVisitor, PortalFilter, PortalQuery, SiteViewGroup,
// MasterConfig, MonitorGroup, PortalSiteView, Monitor,
// Rule, Action, SiteViewObject, AtomicMonitor,
// Platform

public class PQVPrintXML extends PortalQueryVisitor {

    int level;

    HashMap propertyFilter;

    HashMap propertySetFilter;

    MonitorGroup lastGroupPrinted;

    String serverTitle;

    String linkBase;

    boolean addLinks;

    boolean addAckProperties;

    boolean includeBlankProperties;

    boolean includeEmptyGroups;

    boolean includeEmptyServers;

    public static String TRUE_STRING = "true";

    public static String XML_HEADER = "<?xml version=\"1.0\"?>";

    PrintWriter outputStream;

    StringBuffer buffer;

    static final int INDENT_STRING_COUNT = 25;

    static String INDENT_STRING;

    static String indentStrings[];

    public PQVPrintXML(PrintWriter printwriter) {
        level = 0;
        propertyFilter = null;
        propertySetFilter = null;
        lastGroupPrinted = null;
        serverTitle = "local";
        linkBase = "";
        addLinks = false;
        addAckProperties = false;
        includeBlankProperties = false;
        includeEmptyGroups = true;
        includeEmptyServers = true;
        outputStream = null;
        buffer = null;
        outputStream = printwriter;
    }

    public PQVPrintXML(StringBuffer stringbuffer) {
        level = 0;
        propertyFilter = null;
        propertySetFilter = null;
        lastGroupPrinted = null;
        serverTitle = "local";
        linkBase = "";
        addLinks = false;
        addAckProperties = false;
        includeBlankProperties = false;
        includeEmptyGroups = true;
        includeEmptyServers = true;
        outputStream = null;
        buffer = null;
        buffer = stringbuffer;
    }

    void initialize(HashMap hashmap, PortalQuery portalquery) {
        super.initialize(hashmap, portalquery);
        if (TextUtils.getValue(hashmap, "propertySet").length() == 0) {
            setupDefaultProperties(hashmap);
        } else if (TextUtils.getValue(hashmap, "propertySet").equals("all")) {
            hashmap.remove("propertySet");
        }
        propertySetFilter = PortalFilter.setupQueryMap(hashmap, "propertySet");
        if (propertySetFilter != null) {
            if (propertySetFilter.get("links") != null) {
                addLinks = true;
                propertySetFilter.remove("links");
            }
            if (propertySetFilter.get("alerts") != null) {
                portalQuery.addAlerts = true;
                propertySetFilter.remove("alerts");
            }
            if (propertySetFilter.get("acknowledges") != null) {
                addAckProperties = true;
                propertySetFilter.remove("acknowledges");
            }
            if (propertySetFilter.get("parameters") != null) {
                propertySetFilter.put("mainParameters", TRUE_STRING);
                propertySetFilter.put("secondaryParameters", TRUE_STRING);
            }
            if (propertySetFilter.get("state") != null) {
                propertySetFilter.put("mainState", TRUE_STRING);
                propertySetFilter.put("secondaryState", TRUE_STRING);
            }
            if (propertySetFilter.size() == 0) {
                propertySetFilter = null;
            }
        }
        propertyFilter = PortalFilter.setupQueryMap(hashmap, "property");
        if (TextUtils.getValue(hashmap, "includeBlankProperties").length() > 0) {
            includeBlankProperties = true;
        }
        if (portalquery.filteringOnData()) {
            includeEmptyGroups = false;
            includeEmptyServers = false;
        }
        if (TextUtils.getValue(hashmap, "excludeEmptyGroups").length() > 0) {
            includeEmptyGroups = false;
        }
        if (TextUtils.getValue(hashmap, "excludeEmptyServers").length() > 0) {
            includeEmptyServers = false;
        }
        if (TextUtils.getValue(hashmap, "includeEmptyGroups").length() > 0) {
            includeEmptyGroups = true;
        }
        if (TextUtils.getValue(hashmap, "includeEmptyServers").length() > 0) {
            includeEmptyServers = true;
        }
        linkBase = "http://"
                + TextUtils.getValue(portalQuery.config, "_webserverAddress");
        String s = TextUtils.getValue(portalQuery.config, "_httpActivePort");
        if (s.length() != 0 && !s.equals("80")) {
            linkBase += ":" + s;
        }
    }

    private void setupDefaultProperties(HashMap hashmap) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        HashMap hashmap1 = MasterConfig.getMasterConfig();
        String s = TextUtils.getValue(hashmap1,
                "_centrascopeDefaultXMLProperties");
        if (s.length() == 0) {
            s = "propertySet=mainParameters&propertySet=mainState";
            LogManager.log("Error",
                    "Missing CentraScope Default XML Properties, using " + s);
        }
        String as[] = TextUtils.split(s, "&");
        for (int i = 0; i < as.length; i++) {
            int j = as[i].lastIndexOf("=");
            if (j >= 0) {
                String s1 = as[i].substring(j + 1);
                String s2 = as[i].substring(0, j);
                hashmap.add(s2, s1);
            }
        }

    }

    private String getIndentString() {
        if (level < 25) {
            return indentStrings[level];
        }
        String s = "";
        for (int i = 0; i < level; i++) {
            s = s + INDENT_STRING;
        }

        return s;
    }

    private void println(String s) {
        if (outputStream != null) {
            String s1 = getIndentString();
            outputStream.print(s1);
            outputStream.println(s);
        }
        if (buffer != null) {
            String s2 = getIndentString();
            buffer.append(s2);
            buffer.append(s);
            buffer.append('\n');
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    boolean enterprisePre() {
        println(XML_HEADER);
        println("<enterprise>");
        level++;
        println("<account>" + portalQuery.request.getAccount() + "</account>");
        if (addLinks) {
            println("<categoryArtGood>" + MonitorGroup.getCategoryArt("good")
                    + "</categoryArtGood>");
            println("<categoryArtError>" + MonitorGroup.getCategoryArt("error")
                    + "</categoryArtError>");
            println("<categoryArtWarning>"
                    + MonitorGroup.getCategoryArt("warning")
                    + "</categoryArtWarning>");
            println("<categoryArtDisabled>"
                    + MonitorGroup.getCategoryArt("disabled")
                    + "</categoryArtDisabled>");
            println("<categoryArtRunning>"
                    + MonitorGroup.getCategoryArt("running")
                    + "</categoryArtRunning>");
            println("<categoryArtSmallGood>"
                    + MonitorGroup.getSmallCategoryArt("good")
                    + "</categoryArtSmallGood>");
            println("<categoryArtSmallError>"
                    + MonitorGroup.getSmallCategoryArt("error")
                    + "</categoryArtSmallError>");
            println("<categoryArtSmallWarning>"
                    + MonitorGroup.getSmallCategoryArt("warning")
                    + "</categoryArtSmallWarning>");
            println("<categoryArtSmallDisabled>"
                    + MonitorGroup.getSmallCategoryArt("disabled")
                    + "</categoryArtSmallDisabled>");
            println("<categoryArtSmallRunning>"
                    + MonitorGroup.getSmallCategoryArt("running")
                    + "</categoryArtSmallRunning>");
            println("<categoryArtTinyGood>"
                    + MonitorGroup.getTinyCategoryArt("good")
                    + "</categoryArtTinyGood>");
            println("<categoryArtTinyError>"
                    + MonitorGroup.getTinyCategoryArt("error")
                    + "</categoryArtTinyError>");
            println("<categoryArtTinyWarning>"
                    + MonitorGroup.getTinyCategoryArt("warning")
                    + "</categoryArtTinyWarning>");
            println("<categoryArtTinyDisabled>"
                    + MonitorGroup.getTinyCategoryArt("disabled")
                    + "</categoryArtTinyDisabled>");
            println("<categoryArtTinyRunning>"
                    + MonitorGroup.getTinyCategoryArt("running")
                    + "</categoryArtTinyRunning>");
        }
        Enumeration enumeration = portalQuery.request.getVariables();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            if (TextUtils.startsWithIgnoreCase(s, "select-")) {
                println("<" + s + ">" + portalQuery.request.getValue(s) + "</"
                        + s + ">");
            }
        }
        return true;
    }

    void enterprisePost() {
        level--;
        println("<view>" + portalQuery.request.getValue("view") + "</view>");
        println("</enterprise>");
    }

    boolean siteviewPre(PortalSiteView portalsiteview) {
        println("<siteview>");
        level++;
        println("<server>" + xmlEncode(portalsiteview.getProperty("_server"))
                + "</server>");
        if (serverTitle.length() > 0) {
            println("<serverTitle>"
                    + xmlEncode(portalsiteview.getProperty("_title"))
                    + "</serverTitle>");
        }
        println("<serverConnect>"
                + (portalsiteview.getProperty(PortalSiteView.pDisabled)
                        .length() <= 0 ? portalsiteview
                        .getProperty(PortalSiteView.pConnectState)
                        : "disabled") + "</serverConnect>");
        println("<serverAccess>"
                + (portalsiteview.getProperty(PortalSiteView.pReadOnly)
                        .length() <= 0 ? "full" : "read") + "</serverAccess>");
        printXML(portalsiteview);
        return portalQuery.findingGroups();
    }

    void siteviewPost(PortalSiteView portalsiteview) {
        level--;
        println("</siteview>");
    }

    boolean groupPre(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        if (includeEmptyGroups) {
            printGroupProperties(monitorgroup);
        }
        return portalQuery.findingMonitors();
    }

    void groupPost(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        if (lastGroupPrinted == monitorgroup) {
            level--;
            println("</group>");
        }
    }

    boolean monitorPre(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        if (lastGroupPrinted != monitorgroup) {
            printGroupProperties(monitorgroup);
        }
        println("<monitor>");
        level++;
        printXML(monitor);
        if (addLinks) {
            String s = monitor.getTableMoreLink(request);
            if (includeBlankProperties || s.length() > 0) {
                println("<toolsLink>" + xmlEncode(linkBase + s)
                        + "</toolsLink>");
            }
        }
        return true;
    }

    void monitorPost(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        level--;
        println("</monitor>");
    }

    boolean alertPre(Rule rule, Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        if (monitorgroup != null && lastGroupPrinted != monitorgroup) {
            printGroupProperties(monitorgroup);
        }
        println("<alert>");
        level++;
        println("<rule>" + xmlEncode(rule.getProperty(Rule.pExpression))
                + "</rule>");
        Action action = createAction(rule);
        println("<description>" + xmlEncode(action.getActionDescription())
                + "</description>");
        println("<type>" + action.getClassProperty("name") + "</type>");
        printXML(action);
        return true;
    }

    void alertPost(Rule rule, Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        level--;
        println("</alert>");
    }

    String getIndentHTML(int i) {
        String s = "";
        for (int j = 1; j <= i; j++) {
            s = s + "&nbsp;&nbsp;";
        }

        return s;
    }

    void printGroupProperties(MonitorGroup monitorgroup) {
        println("<group>");
        level++;
        printXML(monitorgroup);
        println("<groupIndent>"
                + xmlEncode(getIndentHTML(TextUtils.toInt(monitorgroup
                        .getProperty("groupLevel")))) + "</groupIndent>");
        lastGroupPrinted = monitorgroup;
    }

    boolean allowProperty(StringProperty stringproperty) {
        boolean flag = true;
        if (propertyFilter != null || propertySetFilter != null) {
            flag = false;
            if (propertyFilter != null
                    && propertyFilter.get(stringproperty.getName()) != null) {
                flag = true;
            }
            if (!flag && propertySetFilter != null) {
                if (stringproperty.isParameter) {
                    if (stringproperty.getOrder() > 0) {
                        flag = propertySetFilter.get("mainParameters") != null;
                    } else {
                        flag = propertySetFilter.get("secondaryParameters") != null;
                    }
                } else if (stringproperty.isStateProperty) {
                    if (stringproperty.getOrder() > 0) {
                        flag = propertySetFilter.get("mainState") != null;
                    } else {
                        flag = propertySetFilter.get("secondaryState") != null;
                    }
                }
            }
        }
        if (!flag && addAckProperties
                && stringproperty.getName().startsWith("acknowledge")) {
            flag = true;
        }
        return flag;
    }

    public static String xmlEncode(String s) {
        String s1 = "&";
        String s2 = "&amp;";
        String s3 = s;
        for (int i = -1; (i = s3.indexOf(s1, i + 1)) != -1;) {
            s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());
        }

        s3 = TextUtils.replaceString(s3, ">", "&gt;");
        s3 = TextUtils.replaceString(s3, "<", "&lt;");
        return s3;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param siteviewobject
     */
    void printXML(SiteViewObject siteviewobject) {
        println("<_class>" + xmlEncode(siteviewobject.getProperty("_class"))
                + "</_class>");
        Array array = siteviewobject.getProperties();
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration
                    .nextElement();
            if (allowProperty(stringproperty)) {
                String s = stringproperty.getName();
                s = s.replace('/', '-');
                String s1 = "";
                if (stringproperty == AtomicMonitor.pLastUpdate) {
                    if (siteviewobject
                            .getPropertyAsLong(AtomicMonitor.pLastUpdate) > 0L) {
                        long l = TextUtils.toLong(request
                                .getUserSetting("_timeOffset")) * 1000L;
                        s1 = TextUtils.prettyDate(new Date(siteviewobject
                                .getPropertyAsLong(AtomicMonitor.pLastUpdate)
                                + l));
                    }
                } else {
                    s1 = stringproperty.valueString(siteviewobject
                            .getProperty(stringproperty));
                }
                s1 = s1.replace('<', '*');
                s1 = s1.replace('>', '*');
                if (s1.length() != 0 || includeBlankProperties) {
                    if (s.equals("acknowledgeAlertDisabled")) {
                        Date date = TextUtils.stringToDate(s1);
                        if (date.getTime() > Platform.timeMillis()) {
                            println("<" + s + ">" + xmlEncode(s1) + "</" + s
                                    + ">");
                        }
                    } else {
                        println("<" + s + ">" + xmlEncode(s1) + "</" + s + ">");
                    }
                }
            }
        } 
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

    public static void main(String args[]) throws IOException {
    }

    static {
        INDENT_STRING = "  ";
        indentStrings = new String[25];
        String s = "";
        for (int i = 0; i < 25; i++) {
            indentStrings[i] = s;
            s = s + INDENT_STRING;
        }

    }
}
