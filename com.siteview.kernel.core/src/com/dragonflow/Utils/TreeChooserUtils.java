/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import com.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;
import com.recursionsw.jgl.predicates.LessString;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils, I18N

public class TreeChooserUtils {

    private com.dragonflow.HTTP.HTTPRequest request;

    private java.io.PrintWriter outputStream;

    private HashMap groupHashMap;

    private Array conditions;

    com.dragonflow.SiteView.SiteViewObject siteview;

    public TreeChooserUtils(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.PrintWriter printwriter, Array array) {
        request = null;
        outputStream = null;
        groupHashMap = null;
        conditions = null;
        siteview = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        request = httprequest;
        outputStream = printwriter;
        conditions = array;
    }

    public TreeChooserUtils(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.PrintWriter printwriter, HashMap hashmap) {
        request = null;
        outputStream = null;
        groupHashMap = null;
        conditions = null;
        siteview = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        request = httprequest;
        outputStream = printwriter;
        groupHashMap = hashmap;
    }

    public void createTree() {
        String s = request.getValue("page");
        String s1 = request.getValue("view");
        Array array = new Array();
        HashMap hashmap = new HashMap();
        String s2 = prepareTree(s, s1, array, hashmap);
        HashMap hashmap1 = new HashMap();
        String s3;
        for (Enumeration enumeration = request.getValues("monitorID"); enumeration.hasMoreElements(); hashmap1.put(s3, "checked")) {
            s3 = (String) enumeration.nextElement();
        }

        String s4;
        for (Enumeration enumeration1 = request.getValues("groupID"); enumeration1.hasMoreElements(); hashmap1.put(s4, "checked")) {
            s4 = (String) enumeration1.nextElement();
        }

        if (s.equals("alert")) {
            if (s1.equals("Group")) {
                outputStream
                        .println("Select one or more groups and monitors and then click the <input type=\"button\" value=\"Add\"> button below. The Alert Utilities do not apply to the selected groups and monitors, but perform general functions.  Click the <img src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\"> to expand a group and the <img src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\"> to collapse a group.<br><br>");
                outputStream.println("<table>");
                processGroupView(hashmap, hashmap1);
            } else if (s1.equals("Alert")) {
                outputStream
                        .println("Select one or more alerts and then click the <input type=\"button\" value=\"Disable\"> or the<input type=\"button\" value=\"Enable\"> buttons below.  The Alert Utilities do not apply to the selected alerts, but perform general functions.  Click the <img src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\"> to expand an alert and the <img src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\"> to collapse a alert.<br><br>");
                outputStream.println("<table border=\"2\" cellspacing=\"0\" width=\"100%\">");
                outputStream.println("<tr class=\"tabhead\"><th>On</th><th>Group</th><th>For</th><th>Do</th><th>History</th><th>Edit</th><th>Test</th><th>Delete</th></tr>");
                processAlertView(hashmap, hashmap1);
            }
        } else if (s.equals("report")) {
            if (s1.equals("Group")) {
                outputStream.println("<br>To view management reports and summaries, click the link in the <b>Report</b> field of each description.");
                outputStream
                        .println("<br>Select one or more groups and monitors and then click the <input type=\"button\" value=\"Add\"> or the<input type=\"button\" value=\"Quick\"> buttons below. The Report Utilities do not apply to the selected groups and monitors, but perform general functions.  Click the <img src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\"> to expand a group and the <img src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\"> to collapse a group.<br><br>");
                outputStream.println("<table>");
                processGroupView(hashmap, hashmap1);
            } else if (s1.equals("Report")) {
                outputStream.println("<br>To view management reports and summaries, click the link in the <b>Report</b> field of each description.");
                outputStream.println("Click the <img src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\"> to expand an report and the <img src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\"> to collapse a report.<br><br>");
                outputStream.println("<table border=\"1\" cellspacing=\"0\" width=\"100%\">");
                outputStream.println("<tr class=\"tabhead\"><th width=\"50%\">Reports</th><th>Time Period</th><th>Edit</th><th>Delete</th></tr>");
                processReportView(hashmap, hashmap1);
            }
        }
        outputStream.println("</table>");
        try {
            com.dragonflow.Properties.FrameFile.writeToFile(s2, array);
        } catch (java.io.IOException ioexception) {
        }
    }

    private String prepareTree(String s, String s1, Array array, HashMap hashmap) {
        String s2;
        if (com.dragonflow.SiteView.Platform.isStandardAccount(request.getAccount())) {
            s2 = com.dragonflow.SiteView.Platform.getDirectoryPath("groups", request.getAccount());
        } else {
            s2 = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "accounts" + java.io.File.separator + request.getAccount();
        }
        String s3 = s2 + java.io.File.separator + s + s1 + "Tree.dyn";
        try {
            Array array1 = com.dragonflow.Properties.FrameFile.readFromFile(s3);
            for (int i = 0; i < array1.size(); i ++) {
                array.add(array1.get(i));
            }

        } catch (java.lang.Exception exception) {
        }
        updateCurrentStateFrames(array, hashmap);
        return s3;
    }

    private void updateCurrentStateFrames(Array array, HashMap hashmap) {
        String s = request.getAccount();
        HashMap hashmap1 = new HashMap();
        for (int i = 0; i < array.size(); i ++) {
            HashMap hashmap2 = (HashMap) array.get(i);
            if (com.dragonflow.Utils.TextUtils.getValue(hashmap2, "_user").equals(s)) {
                hashmap1 = hashmap2;
            }
        }

        if (hashmap1.isEmpty()) {
            array.add(hashmap1);
            hashmap1.put("_user", s);
        }
        Enumeration enumeration = request.getVariables();
        while (enumeration.hasMoreElements()) {
            String s1 = com.dragonflow.HTTP.HTTPRequest.decodeString((String) enumeration.nextElement(), com.dragonflow.Utils.I18N.nullEncoding());
            if (s1.startsWith(com.dragonflow.Page.managePage.OPEN_VARIABLE)) {
                String s2 = s1.substring(com.dragonflow.Page.managePage.OPEN_VARIABLE.length(), s1.length() - 2);
                if (!com.dragonflow.Utils.TextUtils.getValue(hashmap1, s2).equals("open")) {
                    hashmap1.put(s2, "open");
                }
            }
            if (s1.startsWith(com.dragonflow.Page.managePage.CLOSE_VARIABLE)) {
                String s3 = s1.substring(com.dragonflow.Page.managePage.CLOSE_VARIABLE.length(), s1.length() - 2);
                if (!com.dragonflow.Utils.TextUtils.getValue(hashmap1, s3).equals("close")) {
                    hashmap1.remove(s3);
                }
            }
        } 
        String s4;
        for (Enumeration enumeration1 = (Enumeration) hashmap1.keys(); enumeration1.hasMoreElements(); hashmap.put(s4, hashmap1.get(s4))) {
            s4 = (String) enumeration1.nextElement();
        }

    }

    private void processGroupView(HashMap hashmap, HashMap hashmap1) {
        com.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        Array array = getGroupNameList(hashmapordered);
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();) {
            String s = (String) enumeration.nextElement();
            Enumeration enumeration1 = (Enumeration) hashmapordered.values(s);
            while (enumeration1.hasMoreElements()) {
                com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) enumeration1.nextElement();
                String s2 = monitorgroup.getProperty(com.dragonflow.SiteView.Monitor.pID);
                String s1 = monitorgroup.getProperty(com.dragonflow.SiteView.Monitor.pName);
                printGroupGroupView(s2, s1, hashmap, hashmap1, 0, "Group");
            }
        }

    }

    private void processAlertView(HashMap hashmap, HashMap hashmap1) {
        Enumeration enumeration = (Enumeration) conditions.iterator();
        while (enumeration.hasMoreElements()) {
            HashMap hashmap2 = (HashMap) enumeration.nextElement();
            Array array = (Array) hashmap2.get("printable");
            String s = (String) hashmap2.get("fullID");
            boolean flag = hashmap.get(s) != null;
            int i = 0;
            String s1 = getIndentHTML(i);
            outputStream.print("<tr><td><table><tr><td>" + s1 + "</td><td>");
            printOpenClose(flag, s);
            outputStream.print("</td><td><input type=checkbox name=alert value=\"" + s + "\"></td><td>" + array.get(0) + "</td></tr></table></td>");
            for (int j = 1; j < array.size(); j ++) {
                outputStream.print(array.get(j));
            }

            outputStream.println("</tr>");
            if (flag) {
                String s2 = (String) hashmap2.get("group");
                Array array1 = new Array();
                if (s2.equals("_master")) {
                    String s3 = (String) hashmap2.get("contents");
                    if (s3 != null) {
                        Array array2 = com.dragonflow.SiteView.Platform.split(',', s3);
                        for (int l = 0; l < array2.size(); l ++) {
                            Array array3 = com.dragonflow.SiteView.Platform.split(' ', (String) array2.get(l));
                            if (!array1.contains(array3.get(0))) {
                                array1.add(array3.get(0));
                            }
                        }

                    } else {
                        com.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
                        Array array4 = getGroupNameList(hashmapordered);
                        for (Enumeration enumeration1 = (Enumeration) array4.iterator(); enumeration1.hasMoreElements();) {
                            String s5 = (String) enumeration1.nextElement();
                            Enumeration enumeration2 = (Enumeration) hashmapordered.values(s5);
                            while (enumeration2.hasMoreElements()) {
                                com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) enumeration2.nextElement();
                                array1.add(monitorgroup.getProperty(com.dragonflow.SiteView.Monitor.pName));
                            }
                        }

                    }
                } else {
                    array1 = com.dragonflow.SiteView.Platform.split(',', s2);
                }
                outputStream.println("<tr><td colspan=\"8\"><table width=\"100%\">");
                for (int k = 0; k < array1.size(); k ++) {
                    String s4 = (String) array1.get(k);
                    printGroupAlertView(s + "/" + s4, hashmap2, hashmap, hashmap1, i + 4, "Alert");
                }

                outputStream.println("</table></td></tr>");
            }
        } 
    }

    private void processReportView(HashMap hashmap, HashMap hashmap1) {
        Enumeration enumeration = (Enumeration) conditions.iterator();
        while (enumeration.hasMoreElements()) {
            HashMap hashmap2 = (HashMap) enumeration.nextElement();
            Array array = (Array) hashmap2.get("printable");
            String s = (String) hashmap2.get("id");
            boolean flag = hashmap.get(s) != null;
            int i = 0;
            String s1 = getIndentHTML(i);
            outputStream.print("<tr><td><table><tr><td>" + s1 + "</td><td>");
            printOpenClose(flag, s);
            outputStream.print("</td><td>" + array.get(0) + "</td></tr></table></td>");
            for (int j = 1; j < array.size(); j ++) {
                outputStream.print(array.get(j));
            }

            outputStream.println("</tr>");
            if (flag) {
                Array array1 = new Array();
                try {
                    String s2 = (String) hashmap2.get("monitors");
                    Array array2 = com.dragonflow.SiteView.Platform.split(' ', s2);
                    if (array2.size() == 1) {
                        array1.add(s2);
                    } else {
                        array1.add(array2.get(1));
                    }
                } catch (java.lang.ClassCastException classcastexception) {
                    Array array3 = (Array) hashmap2.get("monitors");
                    Enumeration enumeration1 = (Enumeration) array3.iterator();
                    while (enumeration1.hasMoreElements()) {
                        String s4 = (String) enumeration1.nextElement();
                        Array array4 = com.dragonflow.SiteView.Platform.split(' ', s4);
                        if (array4.size() == 1) {
                            if (!array1.contains(s4)) {
                                array1.add(s4);
                            }
                        } else if (!array1.contains(array4.get(1))) {
                            array1.add(array4.get(1));
                        }
                    }
                }
                outputStream.println("<tr><td colspan=\"8\"><table width=\"100%\">");
                for (int k = 0; k < array1.size(); k ++) {
                    String s3 = (String) array1.get(k);
                    printGroupReportView(s + "/" + s3, hashmap2, hashmap, hashmap1, i + 4, "Report");
                }

                outputStream.println("</table></td></tr>");
            }
        } 
    }

    private Array getGroupNameList(HashMap hashmap) {
        boolean flag = true;
        Array array = null;
        array = com.dragonflow.Page.CGI.getAllowedGroupIDsForAccount(request);
        Enumeration enumeration = (Enumeration) array.iterator();
        Array array1 = new Array();
        String s = "";
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s1);
            if (monitorgroup != null && (!flag || monitorgroup.getProperty(com.dragonflow.SiteView.MonitorGroup.pParent).length() <= 0)) {
                String s2 = com.dragonflow.Page.CGI.getGroupPath(monitorgroup, com.dragonflow.Page.CGI.getGroupIDFull(s1, siteview), false);
                hashmap.add(s2 + " (" + s1 + ")", monitorgroup);
                if (!s1.equals(s)) {
                    array1.add(s2 + " (" + s1 + ")");
                }
                s = s1;
            }
        } 
        Sorting.sort(array1, new LessString());
        return array1;
    }

    private void printGroupGroupView(String s, String s1, HashMap hashmap, HashMap hashmap1, int i, String s2) {
        String s3 = printGroup(s, s1, hashmap, hashmap1, i, s2);
        boolean flag = hashmap.get(s) != null;
        if (flag) {
            Array array = (Array) groupHashMap.get(s);
            if (array != null) {
                outputStream.println("<tr><td><table cellpadding=\"0\">");
                for (int j = 0; j < array.size(); j ++) {
                    printAlertOrReport((Array) array.get(j), s3);
                }

                outputStream.println("</table></td></tr>");
            }
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s);
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) enumeration.nextElement();
                if (monitor instanceof com.dragonflow.SiteView.SubGroup) {
                    String s4 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.SubGroup.pGroup));
                    com.dragonflow.SiteView.MonitorGroup monitorgroup1 = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s4);
                    String s6 = monitorgroup1.getProperty(com.dragonflow.SiteView.Monitor.pName);
                    if (monitorgroup1 != null) {
                        printGroupGroupView(s4, s6, hashmap, hashmap1, i + 2, s2);
                    }
                } else {
                    printMonitor(monitor, hashmap1, s, s2, s3);
                    String s5 = s + " " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pID);
                    Array array1 = (Array) groupHashMap.get(s5);
                    if (array1 != null) {
                        outputStream.println("<tr><td><table cellpadding=\"0\">");
                        for (int k = 0; k < array1.size(); k ++) {
                            printAlertOrReport((Array) array1.get(k), getIndentHTML(i + 6));
                        }

                        outputStream.println("</table></td></tr>");
                    }
                }
            }
        }
    }

    private void printGroupAlertView(String s, HashMap hashmap, HashMap hashmap1, HashMap hashmap2, int i, String s1) {
        String s2 = s.substring(0, s.lastIndexOf('/'));
        String s3 = s.substring(s.lastIndexOf('/') + 1);
        String s4 = com.dragonflow.Page.CGI.getGroupFullName(com.dragonflow.Utils.I18N.toDefaultEncoding(com.dragonflow.Page.CGI.getGroupIDFull(s3, siteview)));
        printGroup(s, s4, hashmap1, hashmap2, i, s1);
        boolean flag = hashmap1.get(s) != null;
        if (flag) {
            String s5 = (String) hashmap.get("monitor");
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s3);
            Array array = new Array();
            if (s5.equals("_config")) {
                String s6 = (String) hashmap.get("contents");
                if (s6 == null || s6.length() == 0) {
                    com.dragonflow.SiteView.Monitor monitor1;
                    for (Enumeration enumeration = monitorgroup.getMonitors(); enumeration.hasMoreElements(); array.add(monitor1)) {
                        monitor1 = (com.dragonflow.SiteView.Monitor) enumeration.nextElement();
                    }

                } else {
                    Array array1 = com.dragonflow.SiteView.Platform.split(',', s6);
                    for (int k = 0; k < array1.size(); k ++) {
                        if (((String) array1.get(k)).indexOf(s3) >= 0) {
                            Array array2 = com.dragonflow.SiteView.Platform.split(' ', (String) array1.get(k));
                            if (array2.size() > 1) {
                                com.dragonflow.SiteView.Monitor monitor3 = (com.dragonflow.SiteView.Monitor) monitorgroup.getElementByID((String) array2.get(1));
                                array.add(monitor3);
                            } else {
                                com.dragonflow.SiteView.Monitor monitor4;
                                for (Enumeration enumeration1 = monitorgroup.getMonitors(); enumeration1.hasMoreElements(); array.add(monitor4)) {
                                    monitor4 = (com.dragonflow.SiteView.Monitor) enumeration1.nextElement();
                                }

                            }
                        }
                    }

                }
            } else {
                com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) monitorgroup.getElementByID(s5);
                array.add(monitor);
            }
            String s7 = getIndentHTML(i + 2);
            for (int j = 0; j < array.size(); j ++) {
                com.dragonflow.SiteView.Monitor monitor2 = (com.dragonflow.SiteView.Monitor) array.get(j);
                if (monitor2 instanceof com.dragonflow.SiteView.SubGroup) {
                    String s8 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor2.getProperty(com.dragonflow.SiteView.SubGroup.pGroup));
                    com.dragonflow.SiteView.MonitorGroup monitorgroup1 = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s8);
                    if (monitorgroup1 != null) {
                        hashmap.remove("contents");
                        printGroupAlertView(s2 + "/" + s8, hashmap, hashmap1, hashmap2, i + 2, s1);
                    }
                } else {
                    printMonitor(monitor2, hashmap2, s3, s1, s7);
                }
            }

        }
    }

    private void printGroupReportView(String s, HashMap hashmap, HashMap hashmap1, HashMap hashmap2, int i, String s1) {
        String s2 = s.substring(0, s.lastIndexOf('/'));
        String s3 = s.substring(s.lastIndexOf('/') + 1);
        String s4 = com.dragonflow.Page.CGI.getGroupFullName(com.dragonflow.Utils.I18N.toDefaultEncoding(com.dragonflow.Page.CGI.getGroupIDFull(s3, siteview)));
        printGroup(s, s4, hashmap1, hashmap2, i, s1);
        boolean flag = hashmap1.get(s) != null;
        if (flag) {
            Array array = new Array();
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteview.getElementByID(s3);
            try {
                String s5 = (String) hashmap.get("monitors");
                getReportMonitors(s5, monitorgroup, array);
            } catch (java.lang.ClassCastException classcastexception) {
                Array array1 = (Array) hashmap.get("monitors");
                String s7;
                for (Enumeration enumeration =  (Enumeration) array1.iterator(); enumeration.hasMoreElements(); getReportMonitors(s7, monitorgroup, array)) {
                    s7 = (String) enumeration.nextElement();
                }

            }
            String s6 = getIndentHTML(i + 2);
            for (int j = 0; j < array.size(); j ++) {
                com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) array.get(j);
                if (monitor instanceof com.dragonflow.SiteView.SubGroup) {
                    String s8 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.SubGroup.pGroup));
                    com.dragonflow.SiteView.MonitorGroup monitorgroup1 = (com.dragonflow.SiteView.MonitorGroup) siteview.getElement(s8);
                    if (monitorgroup1 != null) {
                        hashmap.remove("monitors");
                        printGroupReportView(s2 + "/" + s8, hashmap, hashmap1, hashmap2, i + 2, s1);
                    }
                } else {
                    printMonitor(monitor, hashmap2, s3, s1, s6);
                }
            }

        }
    }

    private void getReportMonitors(String s, com.dragonflow.SiteView.MonitorGroup monitorgroup, Array array) {
        if (s != null) {
            Array array1 = com.dragonflow.SiteView.Platform.split(' ', s);
            if (array1.size() == 1) {
                if (s.equals(monitorgroup.getProperty(com.dragonflow.SiteView.Monitor.pID))) {
                    com.dragonflow.SiteView.Monitor monitor2;
                    for (Enumeration enumeration1 = monitorgroup.getMonitors(); enumeration1.hasMoreElements(); array.add(monitor2)) {
                        monitor2 = (com.dragonflow.SiteView.Monitor) enumeration1.nextElement();
                    }

                }
            } else if (array1.get(1).equals(monitorgroup.getProperty(com.dragonflow.SiteView.Monitor.pID))) {
                com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) monitorgroup.getElementByID((String) array1.get(0));
                array.add(monitor);
            }
        } else {
            com.dragonflow.SiteView.Monitor monitor1;
            for (Enumeration enumeration = monitorgroup.getMonitors(); enumeration.hasMoreElements(); array.add(monitor1)) {
                monitor1 = (com.dragonflow.SiteView.Monitor) enumeration.nextElement();
            }

        }
    }

    private String getIndentHTML(int i) {
        int j = i * 11;
        if (j == 0) {
            j = 1;
        }
        return "<img src=/SiteView/htdocs/artwork/empty1111.gif height=11 width=" + j + " border=0>";
    }

    private void printOpenClose(boolean flag, String s) {
        if (flag) {
            outputStream.print("<input type=image name=close" + s + " src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\" border=0>");
        } else {
            outputStream.print("<input type=image name=open" + s + " src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\" border=0>");
        }
    }

    private String printGroup(String s, String s1, HashMap hashmap, HashMap hashmap1, int i, String s2) {
        boolean flag = hashmap.get(s) != null;
        String s3 = getIndentHTML(i);
        String s4 = com.dragonflow.SiteView.Platform.getURLPath("htdocs", request.getAccount()) + "/Detail";
        outputStream.print("<TR><TD>");
        outputStream.print(s3);
        printOpenClose(flag, s);
        if (s2.equals("Group")) {
            outputStream.print("<input type=checkbox name=groupID value=\"" + s + "\" " + com.dragonflow.Utils.TextUtils.getValue(hashmap1, s) + ">");
            outputStream.print("<B><A HREF=" + s4 + com.dragonflow.HTTP.HTTPRequest.encodeString(com.dragonflow.Utils.I18N.toDefaultEncoding(s)) + ".html>" + s1);
        } else {
            String s5 = s.substring(s.lastIndexOf('/') + 1);
            outputStream.print("<B><A HREF=" + s4 + com.dragonflow.HTTP.HTTPRequest.encodeString(com.dragonflow.Utils.I18N.toDefaultEncoding(s5)) + ".html>" + s1);
        }
        outputStream.println("</A></B></TD></TR>");
        s3 = getIndentHTML(i + 3);
        return s3;
    }

    private void printMonitor(com.dragonflow.SiteView.Monitor monitor, HashMap hashmap, String s, String s1, String s2) {
        outputStream.print("<TR><TD>");
        outputStream.print(s2);
        String s3 = s + " " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pID);
        if (s1.equals("Group")) {
            outputStream.print("<input type=checkbox name=monitorID value=\"" + s3 + "\" " + com.dragonflow.Utils.TextUtils.getValue(hashmap, s3) + ">");
        }
        outputStream.print(monitor.getProperty(com.dragonflow.SiteView.Monitor.pName));
        if (monitor.getProperty(com.dragonflow.SiteView.Monitor.pDisabled).length() > 0) {
            outputStream.println(" <B>(disabled)</B>");
        }
//		else if (com.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings().isConnected() && monitor.getProperty(com.dragonflow.SiteView.AtomicMonitor.pNotLogToTopaz).length() > 0) {
//            outputStream.println(" <B>(logging to " + com.dragonflow.SiteView.TopazInfo.getTopazName() + " disabled)</B>");
//        }
        outputStream.println("</TD></TR>");
    }

    private void printAlertOrReport(Array array, String s) {
        outputStream.println("<tr>");
        outputStream.println("<td>" + s + "</td>");
        for (int i = 0; i < array.size(); i ++) {
            outputStream.println((String) array.get(i));
            outputStream.println("<td>&nbsp;&nbsp;</td>");
        }

        outputStream.println("</tr>");
    }
}
