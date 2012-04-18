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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.Properties.HashMapOrdered;
import com.dragonflow.SiteView.MonitorGroup;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.SiteView.SiteViewGroup;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.Page:
// CGI, treeControl

public class groupPage extends com.dragonflow.Page.CGI {

    public groupPage() {
    }

    public CGI.menus getNavItems(HTTPRequest httprequest) {
        com.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page",
                    "Browse Monitors"));
        }
        if (httprequest.actionAllowed("_preference")) {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page",
                    "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page",
                    "Add/Edit Remote Win NT/2000 profiles"));
        }
        if (httprequest.actionAllowed("_tools")) {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools",
                    "operation", "Use monitor diagnostic tools"));
        }
        if (httprequest.actionAllowed("_progress")) {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url",
                    "View current monitoring progress"));
        }
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "",
                    "page", "View current monitor settings"));
        }
        return menus1;
    }

    void printForm(String s, String s1, String s2) throws Exception {
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        String s7 = "";
        String s8 = "";
        String s9 = s;
        String s10 = I18N.toDefaultEncoding(request.getValue("parent"));
        if (s10.length() > 0) {
            s10 = com.dragonflow.Page.groupPage.getGroupName(s10);
        }
        com.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
        if (s.equals("Edit")) {
            s9 = "Update";
            printButtonBar("EditGroup.htm#rename", "", menus1);
        } else {
            printButtonBar("EditGroup.htm#how", "", menus1);
        }
        if (s1.length() != 0) {
            HashMap hashmap = null;
            try {
                Array array1 = ReadGroupFrames(s1);
                Enumeration enumeration =  (Enumeration) array1.iterator();
                hashmap = (HashMap) enumeration.nextElement();
            } catch (IOException ioexception) {
                System.out.println("***id: " + s1 + ", error: " + ioexception);
            }
            if (hashmap != null) {
                String s11 = com.dragonflow.Page.groupPage
                        .getGroupIDRelative(s1);
                s4 = s11;
                s3 = com.dragonflow.Page.groupPage.getGroupName(hashmap, s11);
                s5 = HTTPRequest.decodeString(com.dragonflow.Utils.TextUtils
                        .getValue(hashmap, "_dependsOn"));
                s6 = TextUtils.getValue(hashmap, "_dependsCondition");
                Array array2 = TextUtils.getMultipleValues(hashmap,
                        "_description");
                for (Enumeration enumeration1 =  (Enumeration) array2.iterator(); enumeration1
                        .hasMoreElements();) {
                    s7 = s7 + enumeration1.nextElement() + "\n";
                }

                s8 = TextUtils.getValue(hashmap, "__template");
            }
        }
        if (s2.length() > 0) {
            printErrorHeader();
        }
        Array array = getAllowedGroupIDs();
        String s12 = request.getValue("group");
        outputStream.println("<H2>");
        if (request.getValue("_health").length() == 0) {
            outputStream.println(s
                    + (s10.length() <= 0 ? " Group to :" : " Subgroup to : "));
        } else {
            outputStream.println(s + ": ");
        }
        if (s12.length() > 0) {
            outputStream.println("<A HREF="
                    + CGI.getGroupDetailURL(request, s1) + ">" + s3 + "</A>");
        } else {
            outputStream.println("<A HREF=/SiteView/"
                    + request.getAccountDirectory() + "/SiteView.html"
                    + ">SiteView</A>");
        }
        outputStream.println("</H2>");
        String s13 = request.getValue("_health").length() <= 0 ? ""
                : "<input type=hidden name=_health value=true>\n";
        outputStream.println(getPagePOST("group", s)
                + "<INPUT TYPE=HIDDEN NAME=group VALUE=\"" + s12 + "\">"
                + "<INPUT TYPE=HIDDEN NAME=parent VALUE=\""
                + request.getValue("parent") + "\">" + s13
                + "<TABLE><TR><TD ALIGN=RIGHT>");
        outputStream
                .println((s10.length() <= 0 ? " Group" : " Subgroup")
                        + " Name:</TD>"
                        + "<TD ALIGN=LEFT><input type=text name=groupName size=15 VALUE=\""
                        + (s.equals("Edit") ? s3 : "")
                        + "\"></TD><TD><I>"
                        + s2
                        + "</I></TD></TR>"
                        + "<TR><TD></TD><TD><FONT SIZE=-1>"
                        + "Valid group name characters are alphanumeric characters, dashes (-), underscores (_), periods (.), and spaces( )."
                        + " <code><font size=2>[a-zA-Z0-9-_. ]</font></code></TD></TR>"
                        + "<TR><TD><BR></TD></TR>"
                        + "</TABLE><BR><input type=submit VALUE=\"" + s9
                        + "\"> " + (s10.length() <= 0 ? " Group" : " Subgroup")
                        + "<p>");
        outputStream
                .println("<p><HR><CENTER><H3>Advanced Options</H3></CENTER><TABLE>");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Description:</TD><TD ALIGN=LEFT><TEXTAREA name=_description rows=4 cols=70>"
                        + (s10.length() <= 0 ? s7 : "")
                        + "</textarea></TD></TR>"
                        + "<TR><TD></TD><TD><FONT SIZE=-1>Enter an optional description that is displayed at the top of the Group Detail page. "
                        + "You can enter a multi-row, two column table by entering decription name value pairs seperated by a ':' Placed one pair per line.</TD></TR>"
                        + "<TR><TD><BR></TD></TR>");
        if (array.size() > 0) {
            String s14 = "";
            String s15 = "";
            String s16 = "";
            if (s6.equals("error")) {
                s15 = "selected";
            } else {
                s14 = "selected";
            }
            s16 = "<option value=good " + s14
                    + ">OK</option><option value=error " + s15
                    + ">Error</option>";
            if (com.dragonflow.Page.treeControl.useTree()) {
                StringBuffer stringbuffer = new StringBuffer();
                Array array3 = new Array();
                if (s5.length() > 0) {
                    array3.add(s5);
                }
                com.dragonflow.Page.treeControl treecontrol = new treeControl(
                        request, "_dependsOn", true);
                treecontrol
                        .process(
                                "Depends On: ",
                                "",
                                "Choose the monitor to that controls whether monitors in this group are enabled.",
                                array3, null, null, 5, this, stringbuffer);
                outputStream.println(stringbuffer);
            } else {
                outputStream
                        .println("<TR><TD ALIGN=RIGHT>Depends On: </TD><TD ALIGN=LEFT><select size=1 name=_dependsOn>"
                                + getMonitorOptionsHTML(s5, s4, 5)
                                + "</select></TD></TR>"
                                + "<TR><TD></TD><TD><FONT SIZE=-1>Choose the monitor to that controls whether monitors in this group are enabled.</td></tr>"
                                + "<TR><TD ALIGN=RIGHT>Depends Condition: </TD><TD ALIGN=LEFT><select size=1 name=_dependsCondition>"
                                + s16
                                + "</select></TD></TR>"
                                + "<TR><TD></TD><TD><FONT SIZE=-1>If OK, this group is only enabled if the Depends On monitor is OK.  Use this to disable monitoring when a router or an entire server goes down.  <br><br>If Error, this group is only enabled when the Depends On monitor is in Error.  Use this to enable \"failover\" monitoring if another monitoring system goes down</td></tr>"
                                + "<TR><TD><BR></TD></TR>");
            }
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                .getElement(s1);
        MonitorGroup.pFrequency.printProperty(null, outputStream, monitorgroup,
                request, new HashMap(), true);
        String s17 = getMonitorTemplateOptionsHTML(s8);
        if (s17.length() > 0) {
            outputStream
                    .println("<TR><TD ALIGN=RIGHT>Settings Template:</TD><TD ALIGN=LEFT><SELECT name=__template size=1>"
                            + s17
                            + "</select></TD></TR>"
                            + "<TR><TD></TD><TD><FONT SIZE=-1>Monitor settings template to apply to monitors in this group and its subgroups</TD></TR>"
                            + "<TR><TD><BR></TD></TR>");
        }
        outputStream.println("</TABLE><BR><input type=submit VALUE=\"" + s9
                + "\"> Group<p>" + "</FORM>");
        printFooter(outputStream);
    }

    void printAddForm(String s, String s1) throws java.lang.Exception {
        String s2 = com.dragonflow.Utils.I18N.toNullEncoding(s1);
        if (request.isPost()) {
            String s3 = request.getValue("groupName");
            if (com.dragonflow.Utils.I18N.isI18N) {
                s1 = FileUtils.getGroupNewFileName();
            } else {
                s1 = I18N.toDefaultEncoding(s3);
            }
            if (s3.length() == 0
                    && com.dragonflow.Page.treeControl.notHandled(request)) {
                printForm(s, s3, "a group must have a name");
            } else if (s3.equalsIgnoreCase("siteview")
                    && com.dragonflow.Page.treeControl.notHandled(request)) {
                printForm(s, s3,
                        "a group cannot be named SiteView - please choose another name");
            } else {
                String s4 = request.getValue("parent");
                String s5 = I18N.toDefaultEncoding(s4);
                s1 = com.dragonflow.Page.groupPage.getGroupIDFull(s1, s5);
                if (request != null
                        && !Platform.isStandardAccount(request.getAccount())) {
                    s1 = computeGroupName(request.getAccount() + "$" + s1);
                } else {
                    s1 = computeGroupName(s1);
                }
                Array array1 = new Array();
                HashMapOrdered hashmapordered = new HashMapOrdered(true);
                if (!Platform.isStandardAccount(request.getAccount())) {
                    hashmapordered.put(MonitorGroup.pAccountName, request
                            .getAccount());
                }
                hashmapordered.put("_name", "config");
                if (s5 != null && s5.length() != 0) {
                    hashmapordered.put("_parent", com.dragonflow.Page.groupPage
                            .getGroupIDRelative(s4));
                    try {
                        Array array2 = ReadGroupFrames(s5);
                        HashMap hashmap = (HashMap) array2.get(0);
                        String s8 = com.dragonflow.Utils.TextUtils.getValue(
                                hashmap, "_nextID");
                        if (s8.length() == 0) {
                            s8 = "1";
                        }
                        HashMap hashmap1 = new HashMap();
                        hashmap1.put("_id", s8);
                        hashmap1.put("_class", "SubGroup");
                        hashmap1.put("_group", I18N
                                .toNullEncoding(com.dragonflow.Page.groupPage
                                        .getGroupIDRelative(s1)));
                        hashmap1.put("_name", s3);
                        hashmap1.put("_encoding", I18N.nullEncoding());
                        array2.insert(array2.size(), array2.size(), hashmap1);
                        String s10 = TextUtils.increment(s8);
                        hashmap.put("_nextID", s10);
                        WriteGroupFrames(s5, array2);
                    } catch (java.io.IOException ioexception) {
                    }
                }
                if (!s3.equals(com.dragonflow.Page.groupPage
                        .getGroupIDRelative(I18N.toNullEncoding(s1)))) {
                    hashmapordered.put("_name", s3);
                }
                array1.add(hashmapordered);
                hashmapordered.put("_nextID", "1");
                String s6 = request.getValue("_dependsOn");
                hashmapordered.put("_dependsOn", s6.equals("_none") ? ""
                        : ((java.lang.Object) (s6)));
                hashmapordered.put("_dependsCondition", request
                        .getValue("_dependsCondition"));
                hashmapordered.put("_httpCharSet", request
                        .getValue("_httpCharSet"));
                hashmapordered
                        .put("__template", request.getValue("__template"));
                hashmapordered.put("_encoding", com.dragonflow.Utils.I18N
                        .nullEncoding());
                hashmapordered.put("_name", s3);
                String s7 = request.getValue("_frequency");
                if (s7.length() > 0) {
                    String s9 = request.getValue("_frequencyUnits");
                    int j = com.dragonflow.Properties.FrequencyProperty
                            .toSeconds(com.dragonflow.Utils.TextUtils.toInt(s7),
                                    s9);
                    s7 = "" + j;
                    hashmapordered.put("_frequency", s7);
                }
                updateDescription(hashmapordered, request
                        .getValue("_description"));
                try {
                    WriteGroupFrames(s1, array1);
                    com.dragonflow.SiteView.SiteViewGroup
                            .updateStaticPages(CGI.getGroupIDFull(s1, request
                                    .getPortalServer()));
                    com.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
                    if (com.dragonflow.Page.treeControl.notHandled(request)) {
                        printRefreshPage(com.dragonflow.Page.CGI
                                .getGroupDetailURL(request, s1), 0);
                    } else {
                        printForm("Add", s1, "");
                    }
                } catch (java.lang.Exception exception) {
                    printError("There was a problem creating the group.",
                            exception.toString(), "/SiteView/"
                                    + request.getAccountDirectory()
                                    + "/SiteView.html");
                    java.lang.System.out.println("<PRE>");
                    exception.printStackTrace();
                    java.lang.System.out.println("</PRE>");
                }
            }
        } else {
            int i = TextUtils.toInt(request.getPermission("_maximumGroups"));
            if (i > 0) {
                Array array = getAllowedGroupIDs();
                if (array.size() >= i) {
                    printTooManyGroups(i);
                    return;
                }
            }
            printForm("Add", s1, "");
        }
    }

    String getMonitorOptions(String s, Array array, String s1)
            throws Exception {
        String s2 = "<option value=\"\">none";
        Enumeration enumeration = (Enumeration) array.iterator();
        String s3 = "";
        String s4 = "";
        if (s1.length() > 0) {
            String as[] = TextUtils.split(s1);
            if (as.length == 2) {
                s4 = as[0];
                s3 = as[1];
            }
        }
        while (enumeration.hasMoreElements()) {
            String s5 = (String) enumeration.nextElement();
            if (!s5.equals(s)) {
                Array array1 = ReadGroupFrames(s5);
                Enumeration enumeration1 = com.dragonflow.Page.groupPage
                        .getMonitors(array1);
                enumeration1.nextElement();
                String s6 = com.dragonflow.Page.groupPage.getGroupFullName(s5);
                while (enumeration1.hasMoreElements()) {
                    HashMap hashmap = (HashMap) enumeration1
                            .nextElement();
                    if (!com.dragonflow.Utils.TextUtils.getValue(hashmap,
                            "_class").equals("SubGroup")) {
                        String s7 = "";
                        if (s5.equals(s4) && s3.equals(hashmap.get("_id"))) {
                            s7 = "selected";
                        }
                        s2 = s2 + "<option " + s7 + " value=\"" + s5 + " "
                                + hashmap.get("_id") + "\">";
                        s2 = s2 + s6 + ": ";
                        s2 = s2 + TextUtils.getValue(hashmap, "_name");
                    }
                }
            }
        }
        return s2;
    }

    String getCharSetOptionsHTML(String s) {
        HashMap hashmap = getMasterConfig();
        java.util.Vector vector = new Vector();
        String s2;
        for (Enumeration enumeration = (Enumeration) hashmap
                .values("_httpCharSetOption"); enumeration.hasMoreElements(); vector
                .addElement(s2)) {
            String s1 = (String) enumeration.nextElement();
            String as[] = com.dragonflow.Utils.TextUtils.split(s1, ",");
            s2 = as[0];
            String s3 = "";
            if (as.length > 1) {
                s3 = as[1];
            }
            vector.addElement(s3);
        }

        if (vector.size() == 0) {
            vector.addElement("");
            vector.addElement("default");
        }
        if (s.length() == 0) {
            s = (String) vector.elementAt(0);
        }
        return com.dragonflow.Page.groupPage.getOptionsHTML(vector, s);
    }

    String getMonitorTemplateOptionsHTML(String s) {
        Array array = com.dragonflow.Properties.PropertiedObject
                .getTemplateConfigFileList();
        if (array.size() == 0) {
            return "";
        }
        java.util.Vector vector = com.dragonflow.Properties.PropertiedObject
                .getTemplateList(request.getPortalServer());
        if (s.length() == 0) {
            s = (String) vector.elementAt(0);
        }
        return com.dragonflow.Page.groupPage.getOptionsHTML(vector, s);
    }

    void printTooManyGroups(int i) {
        com.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
        printButtonBar("Group.htm#how", "", menus1);
        outputStream.print("<HR>");
        outputStream.print("<HR><P><A HREF=/SiteView/?account="
                + request.getAccount() + ">Return to SiteView</A>\n");
        printFooter(outputStream);
    }

    void updateDescription(HashMap hashmap, String s) {
        hashmap.remove("_description");
        Array array = com.dragonflow.SiteView.Platform.split('\n', s);
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration
                .hasMoreElements(); hashmap.add("_description", enumeration
                .nextElement())) {
        }
    }

    void printEditForm(String s, String s1) throws java.lang.Exception {
        String s2 = com.dragonflow.Utils.I18N.toDefaultEncoding(s1);
        boolean flag = request.getValue("_health").length() > 0;
        if (request.isPost()) {
            if (flag && s2.equals("__Health__")) {
                HashMap hashmap = new HashMap();
                hashmap.put("_healthDisableLogging", request
                        .getValue("disableLogging"));
                hashmap.put("_healthTemplateSet", request
                        .getValue("templateSet"));
                String s4 = com.dragonflow.SiteView.Health.getHealth().update(
                        hashmap, request.getAccount());
                if (s4.length() == 0) {
                    printError(request.getValue("templateSet")
                            + " is already in Health.", "",
                            com.dragonflow.Page.CGI.getGroupDetailURL(request,
                                    s2));
                } else {
                    printRefreshPage(com.dragonflow.Page.CGI.getGroupDetailURL(
                            request, s2), 0);
                }
                return;
            }
            try {
                String s3 = request.getValue("groupName");
                if (s3.length() == 0
                        && !com.dragonflow.Page.treeControl.notHandled(request)) {
                    printForm(s, s1, "a group must have a name");
                } else {
                    Array array = ReadGroupFrames(s2);
                    Enumeration enumeration = (Enumeration) array.iterator();
                    HashMap hashmap1 = (HashMap) enumeration
                            .nextElement();
                    hashmap1.put("_name", s3);
                    hashmap1.put("_dependsOn", request.getValue("_dependsOn"));
                    hashmap1.put("_dependsCondition", request
                            .getValue("_dependsCondition"));
                    hashmap1.put("_httpCharSet", request
                            .getValue("_httpCharSet"));
                    hashmap1.put("__template", request.getValue("__template"));
                    hashmap1.put("_encoding", com.dragonflow.Utils.I18N
                            .nullEncoding());
                    String s5 = request.getValue("_frequency");
                    if (s5.length() > 0) {
                        String s6 = request.getValue("_frequencyUnits");
                        int i = com.dragonflow.Properties.FrequencyProperty
                                .toSeconds(com.dragonflow.Utils.TextUtils
                                        .toInt(s5), s6);
                        s5 = "" + i;
                        hashmap1.put("_frequency", s5);
                    }
                    com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                            .currentSiteView();
                    com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteviewgroup
                            .getElement(s2);
                    com.dragonflow.SiteView.MonitorGroup _tmp = monitorgroup;
                    monitorgroup.setProperty(
                            com.dragonflow.SiteView.MonitorGroup.pLastUpdate,
                            "-1");
                    updateDescription(hashmap1, request
                            .getValue("_description"));
                    String s7 = com.dragonflow.Utils.I18N
                            .toDefaultEncoding(com.dragonflow.Utils.TextUtils
                                    .getValue(hashmap1, "_parent"));
                    if (s7.length() != 0) {
                        Array array1 = ReadGroupFrames(s7);
                        int j = com.dragonflow.Page.groupPage.findSubGroupIndex(
                                array1, s1);
                        HashMap hashmap2 = (HashMap) array1.get(j);
                        hashmap2.put("_name", s3);
                        hashmap2.put("_encoding", com.dragonflow.Utils.I18N
                                .nullEncoding());
                        WriteGroupFrames(s7, array1);
                        com.dragonflow.SiteView.SiteViewGroup
                                .updateStaticPages(s7);
                    }
                    WriteGroupFrames(s2, array);
                    com.dragonflow.SiteView.SiteViewGroup
                            .updateStaticPages(com.dragonflow.Page.CGI
                                    .getGroupIDFull(s2, request
                                            .getPortalServer()));
                    com.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
                    if (com.dragonflow.Page.treeControl.notHandled(request)) {
                        printRefreshPage(com.dragonflow.Page.CGI
                                .getGroupDetailURL(request, s2), 0);
                    } else {
                        printForm("Edit", s2, "");
                    }
                }
            } catch (java.lang.Exception exception) {
                printError("There was a problem renaming the group.", exception
                        .toString(), "/SiteView/"
                        + request.getAccountDirectory() + "/SiteView.html");
                java.lang.System.out.println("<PRE>");
                exception.printStackTrace();
                java.lang.System.out.println("</PRE>");
            }
        } else {
            printForm("Edit", s2, "");
        }
    }

    void printDeleteForm(String s, String s1) {
        String s2 = com.dragonflow.Utils.I18N.toDefaultEncoding(s1);
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) siteviewgroup
                .getElement(s2);
        String s3 = monitor.getProperty("_parent");
        String s4 = monitor.getProperty(com.dragonflow.SiteView.Monitor.pName);
        if (s4 == null || s4.equals("config") || s4.length() == 0) {
            s4 = s1;
        }
        if (request.isPost()
                && com.dragonflow.Page.treeControl.notHandled(request)) {
            try {
                com.dragonflow.SiteView.ConfigurationChanger.deleteGroup(s2,
                        request, null);
                if (s3 == null || s3.length() <= 0) {
                    printRefreshPage(
                            "/SiteView/" + request.getAccountDirectory()
                                    + "/SiteView.html", 0);
                } else {
                    String s5 = request.getValue("_health").length() <= 0 ? ""
                            : "?_health=true";
                    printRefreshPage("/SiteView/"
                            + request.getAccountDirectory()
                            + "/Detail"
                            + com.dragonflow.HTTP.HTTPRequest
                                    .encodeString(com.dragonflow.Utils.I18N
                                            .UnicodeToString(s3,
                                                    com.dragonflow.Utils.I18N
                                                            .nullEncoding()))
                            + ".html" + s5, 0);
                }
            } catch (java.lang.Exception exception) {
                printError("There was a problem deleting the group.", exception
                        .toString(), "/SiteView/"
                        + request.getAccountDirectory() + "/SiteView.html");
            }
        } else {
            com.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("EditGroup.htm#delete", "", menus1);
            String s6 = com.dragonflow.Page.groupPage.getGroupName(s2);
            String s7 = "group";
            outputStream.println("<H2>Delete Group: <A HREF="
                    + com.dragonflow.Page.CGI.getGroupDetailURL(request, s2)
                    + ">" + s4 + "</A>");
            String s8 = request.getValue("_health").length() <= 0 ? ""
                    : "<input type=hidden name=_health value=true>\n";
            outputStream
                    .println("<p>Are you sure you want to delete the "
                            + s7
                            + " \"<B>"
                            + s6
                            + "</B>\"?</p>"
                            + "<p>"
                            + getPagePOST("group", s)
                            + "<INPUT TYPE=HIDDEN NAME=group VALUE=\""
                            + s1
                            + "\">"
                            + s8
                            + "<TABLE WIDTH=100% BORDER=0><TR>"
                            + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit VALUE=\""
                            + s + " " + s6 + "\"></TD>"
                            + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%>"
                            + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    private void healthAttributes(boolean flag) {
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        HashMap hashmap = new HashMap();
        String s = siteviewgroup.getSetting("_healthDisableLogging");
        if (flag) {
            s = s.length() <= 0 ? "CHECKED" : "";
        } else {
            hashmap.put("_healthTemplateSet", "SiteViewApp");
        }
        hashmap.put("_healthDisableLogging", s);
        try {
            com.dragonflow.SiteView.Health.getHealth().update(hashmap,
                    request.getAccount());
        } catch (com.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            com.dragonflow.Log.LogManager.log("error",
                    "Could not update Health. Error: "
                            + siteviewexception.getMessage());
        }
        printRefreshPage(com.dragonflow.Page.CGI.getGroupDetailURL(request,
                "__Health__"), 0);
    }

    public void printBody() throws java.lang.Exception {
        String s = request.getValue("operation");
        String s1 = s + " Group";
        if (!request.isPost()) {
            printBodyHeader(s1);
        }
        String s2 = request.getValue("group");
        if (s.equals("Delete")) {
            if (!request.actionAllowed("_groupEdit")) {
                throw new HTTPRequestException(557);
            }
            printDeleteForm(s, s2);
        } else if (s.equals("Edit")) {
            if (!request.actionAllowed("_groupEdit")) {
                throw new HTTPRequestException(557);
            }
            printEditForm(s, s2);
        } else if (s.equals("Add")) {
            if (!request.actionAllowed("_groupEdit")) {
                throw new HTTPRequestException(557);
            }
            printAddForm(s, com.dragonflow.Utils.I18N.toDefaultEncoding(s2));
        } else if (s.equals("AddDefaultMonitors")) {
            healthAttributes(false);
        } else if (s.equals("flipHealthLogging")) {
            healthAttributes(true);
        } else {
            printError("The link was incorrect", "unknown operation",
                    "/SiteView/" + request.getAccountDirectory()
                            + "/SiteView.html");
        }
    }

    public static void main(String args[]) {
        (new groupPage()).handleRequest();
    }
}
