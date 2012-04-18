/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.util.HashMap;
import java.util.Vector;

import com.dragonflow.Properties.NumericProperty;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class perfmonPage extends com.dragonflow.Page.CGI {

    static final String ERROR_OBJECT_BROWSE = "Failed to retrieve available objects. Monitor returned the following error: ";

    static final String ERROR_GET_INSTANCES = "Failed to retrieve available instances. Monitor returned the following error: ";

    static final String ERROR_GET_COUNTERS = "Failed to retrieve available counters. Monitor returned the following error: ";

    static final String TAG_INSTANCES_URL = "<%loadInstancesUrl%>";

    static final String TAG_COUNTERS_URL = "<%loadCountersUrl%>";

    static final String TAG_OBJECTS_SELECT = "<%objectsSelect%>";

    static final String TAG_MSMTS_LIST = "<%measurementsList%>";

    static final String TAG_FORM_HEADER = "<%formHeader%>";

    static final String PERFMONPAGE_HELP = "PerfmonCountersPage.htm";

    static final String MSMTS_SELECTIONS_PAGE;

    static final String PERFMONPAGE_BODY_HEADER = "Select Performence Counters";

    public static final String PERFMONPAGE_NAME = "perfmon";

    public static final String SUFFIX_COUNTER = "Counter";

    public static final String SUFFIX_INSTANCE = "Instance";

    public static final String SUFFIX_OBJECT = "Object";

    public static final String SUFFIX_DESCRIPTION = "Description";

    public static final String PREFIX_MSMT = "perfmonMsmt";

    public static final String PREFIX_COUNTER = "counter";

    public static final String PREFIX_INSTANCE = "instance";

    public static final String PARAM_OBJECT_NAME = "perfmonObject";

    public static final String PARAM_MSMT_COUNTER = "perfmonMsmtCounter";

    public static final String PARAM_MSMT_INSTANCE = "perfmonMsmtInstance";

    public static final String PARAM_MSMT_OBJECT = "perfmonMsmtObject";

    public static final String PARAM_MSMT_DESCRIPTION = "perfmonMsmtDescription";

    public static final String PARAM_PAGE_OP = "perfmonPageOp";

    public static final String PARAM_CACHE_ID = "perfmonCacheId";

    public static final String PARAM_CLASS = "perfmonClass";

    public static final String OP_SAVE_MSMTS = "saveMsmts";

    public static final String OP_SELECT_MSMTS = "selectMsmts";

    public static final String OP_LOAD_INSTANCES = "loadInstances";

    public static final String OP_LOAD_COUNTERS = "loadCounters";

    public static final String FLAG_HAVE_INSTANCES = "haveInstances";

    private static java.lang.Object framesSync = new Object();

    public perfmonPage() {
    }

    /**
     * 
     */
    public void printBody() throws java.lang.Exception {
        String s = request.getValue("perfmonClass");
        com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase = (com.dragonflow.SiteView.PerfmonMonitorBase) com.dragonflow.SiteView.AtomicMonitor
                .MonitorCreate(s, request);
        String s1 = request.getValue("perfmonPageOp");
        if (s1.equals("loadInstances")) {
            loadInstances(request.getValue("perfmonObject"), perfmonmonitorbase);
        } else if (s1.equals("loadCounters")) {
            loadCounters(request.getValue("perfmonObject"), perfmonmonitorbase);
        } else if (s1.equals("selectMsmts")) {
            selectMeasurements(perfmonmonitorbase);
        } else {
            java.util.HashMap hashmap = perfmonmonitorbase
                    .getRequiredRequestCreationProps();
            String s2 = "";
            java.util.Set set = hashmap.entrySet();
            java.util.Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) iterator
                        .next();
                s2 = s2 + entry.getKey() + "=" + entry.getValue();
                if (iterator.hasNext()) {
                    s2 = s2 + "&";
                }
            }
            printRefreshPage(perfmonmonitorbase.getReturnUrl() + "&" + s2, 0);
        }
    }

    /**
     * 
     * 
     * @param s
     * @param perfmonmonitorbase
     */
    private void loadInstances(String s,
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        java.util.HashMap hashmap;
        java.lang.Object aobj[];
        synchronized (framesSync) {
            hashmap = new HashMap();
            hashmap.put("haveInstances", "false");
            if (s.trim().length() == 0) {
                outputStream.println("");
            } else {
                if (request.getValue("printProgress") != null
                        && request.getValue("printProgress").equals("true")) {
                    outputStream.println("Retrieving instances...");
                } else {
                    StringBuffer stringbuffer = new StringBuffer();
                    aobj = new Object[1];
                    if (perfmonmonitorbase.getAvailableInstances(s, aobj,
                            stringbuffer)) {
                        outputStream
                                .println("Failed to retrieve available instances. Monitor returned the following error: "
                                        + stringbuffer);
                        outputStream.println(com.dragonflow.Utils.HtmlUtil
                                .createHiddenInputs(hashmap));
                        return;
                    }
                    com.dragonflow.Properties.StringProperty astringproperty[] = (com.dragonflow.Properties.StringProperty[]) aobj[0];
                    if (astringproperty != null && astringproperty.length > 0) {
                        outputStream
                                .println(com.dragonflow.Utils.HtmlUtil
                                        .createCheckboxList(astringproperty,
                                                "instance"));
                    } else {
                        outputStream
                                .println("No instances available for the currently selected object");
                    }
                    hashmap.put("haveInstances", "true");
                }
            }

            outputStream.println(com.dragonflow.Utils.HtmlUtil
                    .createHiddenInputs(hashmap));
        }
    }

    private void loadCounters(String s,
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        synchronized (framesSync) {
            if (s.trim().length() == 0) {
                outputStream.println("");
            } else if (request.getValue("printProgress") != null
                    && request.getValue("printProgress").equals("true")) {
                outputStream.println("Retrieving counters...");
            } else {
                StringBuffer stringbuffer = new StringBuffer();
                Object aobj[] = new Object[1];
                if (!perfmonmonitorbase.getAvailableCounters(s, aobj,
                        stringbuffer)) {
                    outputStream
                            .println("Failed to retrieve available counters. Monitor returned the following error: "
                                    + stringbuffer);
                }
                com.dragonflow.Properties.StringProperty astringproperty[] = (com.dragonflow.Properties.StringProperty[]) aobj[0];
                if (astringproperty != null && astringproperty.length > 0) {
                    outputStream.println(com.dragonflow.Utils.HtmlUtil
                            .createCheckboxList(astringproperty, "counter"));
                }
            }
        }
    }

    private void printPerfmonCommonPageHeader(
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        printBodyHeader("Select Performence Counters");
        printButtonBar("PerfmonCountersPage.htm", "");
        outputStream.println("<H2>"
                + (String) perfmonmonitorbase.getClassProperty("description")
                + "</H2><P>\n");
    }

    private String createHtmlFormHeader(
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        String s = getPagePOST("perfmon", "") + "\n";
        java.util.HashMap hashmap = perfmonmonitorbase
                .getRequiredRequestCreationProps();
        s = s + com.dragonflow.Utils.HtmlUtil.createHiddenInputs(hashmap) + "\n";
        s = s + "<INPUT type=hidden name='perfmonPageOp' value='saveMsmts'>\n";
        return s;
    }

    void selectMeasurements(
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        printPerfmonCommonPageHeader(perfmonmonitorbase);
        StringBuffer stringbuffer = null;
        try {
            stringbuffer = com.dragonflow.Utils.FileUtils
                    .readFile(MSMTS_SELECTIONS_PAGE);
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
            return;
        }
        java.util.HashMap hashmap = perfmonmonitorbase
                .getRequiredRequestCreationProps();
        String s = createLoadLink("loadInstances", hashmap);
        int i = stringbuffer.indexOf("<%loadInstancesUrl%>");
        stringbuffer.replace(i, i + "<%loadInstancesUrl%>".length(), s);
        String s1 = createLoadLink("loadCounters", hashmap);
        i = stringbuffer.indexOf("<%loadCountersUrl%>");
        stringbuffer.replace(i, i + "<%loadCountersUrl%>".length(), s1);
        String s2 = createHtmlObjectSelect(perfmonmonitorbase);
        i = stringbuffer.indexOf("<%objectsSelect%>");
        stringbuffer.replace(i, i + "<%objectsSelect%>".length(), s2);
        String s3 = createHtmlFormHeader(perfmonmonitorbase);
        i = stringbuffer.indexOf("<%formHeader%>");
        stringbuffer.replace(i, i + "<%formHeader%>".length(), s3);
        String s4 = createHtmlMeasurements(perfmonmonitorbase);
        i = stringbuffer.indexOf("<%measurementsList%>");
        stringbuffer.replace(i, i + "<%measurementsList%>".length(), s4);
        outputStream.println(stringbuffer);
    }

    private String createLoadLink(String s, java.util.HashMap hashmap) {
        String s1 = getPageLink("perfmon", "");
        s1 = s1 + "&perfmonPageOp=" + s;
        java.util.Set set = hashmap.entrySet();
        for (java.util.Iterator iterator = set.iterator(); iterator.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
            s1 = s1 + "&" + entry.getKey() + "=" + entry.getValue();
        }

        s1 = s1 + "&perfmonObject=";
        return s1;
    }

    private String createHtmlMeasurements(
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        com.dragonflow.Properties.StringProperty astringproperty[] = perfmonmonitorbase
                .getPerfmonMeasurements();
        StringBuffer stringbuffer = new StringBuffer(
                "<TABLE BORDER=0 CELLSPACING=1 CELLPADDING=1>\n");
        for (int i = 0; i < astringproperty.length; i++) {
            String as[] = com.dragonflow.Utils.ArgsPackagerUtil
                    .unpackageArgsToStrArray(astringproperty[i].getLabel());
            stringbuffer
                    .append("<TR>\n<TD>\n<input type=button onclick='removeMeasurement("
                            + i
                            + ")' value=X>\n"
                            + "<input type=hidden name='perfmonMsmtDescription"
                            + i
                            + "' id='perfmonMsmtDescription"
                            + i
                            + "' value='"
                            + com.dragonflow.Utils.TextUtils
                                    .escapeHTML(astringproperty[i]
                                            .getDescription())
                            + "'>\n"
                            + "<input type=hidden name='perfmonMsmtObject"
                            + i
                            + "' id='perfmonMsmtObject"
                            + i
                            + "' value='"
                            + com.dragonflow.Utils.TextUtils.escapeHTML(as[0])
                            + "'>\n"
                            + "<input type=hidden name='perfmonMsmtInstance"
                            + i
                            + "' id='perfmonMsmtInstance"
                            + i
                            + "' value='"
                            + com.dragonflow.Utils.TextUtils.escapeHTML(as[1])
                            + "'>\n"
                            + "<input type=hidden name='perfmonMsmtCounter"
                            + i
                            + "' id='perfmonMsmtCounter"
                            + i
                            + "' value='"
                            + com.dragonflow.Utils.TextUtils.escapeHTML(as[2])
                            + "'>\n"
                            + "</TD>\n"
                            + "<TD title='"
                            + com.dragonflow.Utils.TextUtils
                                    .escapeHTML(astringproperty[i]
                                            .getDescription())
                            + "'>"
                            + com.dragonflow.Utils.TextUtils
                                    .escapeHTML(com.dragonflow.SiteView.PerfmonMonitorBase
                                            .getMsmtLabel(astringproperty[i]))
                            + "\n" + "</TD>\n" + "</TR>\n");
        }

        stringbuffer.append("</TABLE>");
        return stringbuffer.toString();
    }

    private String createHtmlObjectSelect(
            com.dragonflow.SiteView.PerfmonMonitorBase perfmonmonitorbase) {
        StringBuffer stringbuffer = new StringBuffer();
        Object aobj[] = new Object[1];
        if (!perfmonmonitorbase.getAvailableObjects(aobj, stringbuffer)) {
            return "Failed to retrieve available objects. Monitor returned the following error: "
                    + stringbuffer.toString();
        }
        StringBuffer stringbuffer1 = new StringBuffer(
                "<SELECT width='100%' id=objectsSelect onchange='loadInstancesAndCounters()'>\n");
        stringbuffer1.append("<OPTION value=''></OPTION>\n");
        com.dragonflow.Properties.StringProperty astringproperty[] = (com.dragonflow.Properties.StringProperty[]) ((com.dragonflow.Properties.StringProperty[]) aobj[0])
                .clone();
        com.dragonflow.Utils.StringPropertyUtil.sortPropsArray(astringproperty);
        for (int i = 0; astringproperty != null && i < astringproperty.length; i++) {
            String s = astringproperty[i].getName();
            stringbuffer1.append("<OPTION value='"
                    + com.dragonflow.Utils.TextUtils.escapeHTML(s) + "'>"
                    + com.dragonflow.Utils.TextUtils.escapeHTML(s)
                    + "</OPTION>\n");
        }

        stringbuffer1.append("</SELECT>\n");
        return stringbuffer1.toString();
    }

    public static com.dragonflow.Properties.StringProperty[] getNewMeasurementsFromRequest(
            com.dragonflow.HTTP.HTTPRequest httprequest) {
        java.util.Vector vector = new Vector();
        int i = 0;
        while (true) {
            String as[] = new String[3];
            as[0] = httprequest.getValue("perfmonMsmtObject" + i);
            if (as[0] != null && as[0].length() != 0) {
                as[1] = httprequest.getValue("perfmonMsmtInstance" + i);
                as[2] = httprequest.getValue("perfmonMsmtCounter" + i);
                com.dragonflow.Properties.NumericProperty numericproperty = new NumericProperty(
                        "_perfmonMsmtProp" + i);
                String s = com.dragonflow.Utils.ArgsPackagerUtil.packageArgs(as,
                        0, 2);
                numericproperty.setLabel(s);
                numericproperty.setDescription(httprequest
                        .getValue("perfmonMsmtDescription" + i));
                vector.add(numericproperty);
                i++;
            } else {
                com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[vector
                        .size()];
                vector.toArray(astringproperty);
                return astringproperty;
            }
        }
    }

    public static String getPerfmonUniqueIdFromRequest(
            com.dragonflow.HTTP.HTTPRequest httprequest) {
        return httprequest.getValue("perfmonCacheId");
    }

    static {
        MSMTS_SELECTIONS_PAGE = com.dragonflow.SiteView.Platform.getRoot()
                + java.io.File.separator + "htdocs" + java.io.File.separator
                + "templates" + java.io.File.separator
                + "perfmonMsmtsSelections";
    }
}
