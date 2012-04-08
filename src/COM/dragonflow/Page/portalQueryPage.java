/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.util.Enumeration;
import java.util.Vector;

import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// portalPreferencePage, portalChooserPage, monitorPage

public class portalQueryPage extends COM.dragonflow.Page.portalPreferencePage {

    public portalQueryPage() {
    }

    String getTitle() {
        return "Query";
    }

    String getPageName() {
        return "portalQuery";
    }

    String getHelpPage() {
        return "QueryPrefs.htm";
    }

    void printBasicProperties(jgl.HashMap hashmap, jgl.HashMap hashmap1)
            throws java.io.IOException {
        String s = COM.dragonflow.Page.portalQueryPage.getValue(hashmap,
                "_query");
        if (request.getValue("item").length() > 0) {
            s = "";
            for (Enumeration enumeration = request.getValues("item"); enumeration
                    .hasMoreElements();) {
                String s1 = (String) enumeration.nextElement();
                if (s.length() > 0) {
                    s = s + "&";
                }
                s = s + "item=" + s1;
            }

        }
        outputStream.println("<INPUT TYPE=HIDDEN NAME=query VALUE=\"" + s
                + "\">");
        outputStream
                .println("<P>Queries are named sets of groups or monitors from one or more SiteView servers that can be used \nto filter data for reports or custom user views.\n<P>\n");
        outputStream.println("<TABLE>");
        COM.dragonflow.Page.portalChooserPage.printQueryDefine(outputStream,
                request, s);
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Title</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=title size=50 value=\""
                        + COM.dragonflow.Page.portalQueryPage.getValue(hashmap,
                                "_title")
                        + "\"></TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>Enter a title for this query. The query title is used to reference queries when they<br> are applied to view definitions.</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I>"
                        + COM.dragonflow.Utils.TextUtils.getValue(hashmap1,
                                "title") + "</I></TD></TR>");
        outputStream.println("</TABLE>");
    }

    boolean hasAdvancedOptions() {
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    void printAdvancedProperties(jgl.HashMap hashmap, jgl.HashMap hashmap1) {
        jgl.HashMap hashmap2 = COM.dragonflow.SiteView.PortalFilter
                .queryStringToMap(COM.dragonflow.Utils.TextUtils.getValue(
                        hashmap, "_query"));
        outputStream.println("<TABLE border=\"0\">");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Name Match</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=nameMatch size=50 value=\""
                        + COM.dragonflow.Page.portalQueryPage.getValue(hashmap2,
                                "name")
                        + "\"></TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>If a string or regular expression is entered, only return monitors whose name matches</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I>"
                        + COM.dragonflow.Utils.TextUtils.getValue(hashmap1,
                                "nameMatch") + "</I></TD></TR>");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Status Match</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=statusMatch size=50 value=\""
                        + COM.dragonflow.Page.portalQueryPage.getValue(hashmap2,
                                "status")
                        + "\"></TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>If a string or regular expression is entered, only return monitors whose status string matches</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I>"
                        + COM.dragonflow.Utils.TextUtils.getValue(hashmap1,
                                "nameMatch") + "</I></TD></TR>");
        jgl.HashMap hashmap3 = new HashMap();
        for (Enumeration enumeration = hashmap2.values("category"); enumeration
                .hasMoreElements(); hashmap3.add(enumeration.nextElement(),
                "true")) {
        }
        Vector vector = new Vector();
        vector.addElement("");
        vector.addElement("Any");
        vector.addElement("error");
        vector.addElement("Error");
        vector.addElement("warning");
        vector.addElement("Warning");
        vector.addElement("good");
        vector.addElement("OK");
        vector.addElement("disabled");
        vector.addElement("Disabled");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Status Filter</TD><TD><TABLE><TR><TD ALIGN=LEFT><select name=categoryMatch multiple size=5>"
                        + COM.dragonflow.Page.portalQueryPage.getOptionsHTML(
                                vector, hashmap3)
                        + "</select>"
                        + "</TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>Hold the \"Ctrl\" key to select multiple statuses to filter on</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I></I></TD></TR>");

        jgl.HashMap hashmap4 = new HashMap();
        for (Enumeration enumeration1 = hashmap2.values("type"); enumeration1
                .hasMoreElements(); hashmap4.add(enumeration1.nextElement(),
                "true")) {
        }
        Vector vector1 = new Vector();
        vector1.addElement("");
        vector1.addElement("Any");
        jgl.Array array = COM.dragonflow.Page.monitorPage.getMonitorClasses();
        Enumeration enumeration2 = array.elements();
        Class class1;

        while (enumeration2.hasMoreElements()) {
            class1 = (Class) enumeration2.nextElement();
            COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
            String s;
            try {
                atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor) class1
                        .newInstance();
                s = request.getPermission("_monitorType",
                        (String) atomicmonitor.getClassProperty("class"));
                if (s.length() == 0) {
                    s = request.getPermission("_monitorType", "default");
                }
                if (!s.equals("hidden")) {
                    vector1.addElement(atomicmonitor.getClassProperty("class"));
                    vector1.addElement(atomicmonitor.getClassProperty("title"));
                }
            } catch (Exception exception) {
                System.out.println("Could not create instance of " + class1);
            }
        }

        outputStream
                .println("<TR><TD ALIGN=RIGHT>Monitor Type Filter</TD><TD><TABLE><TR><TD ALIGN=LEFT><select name=monitorType multiple size=5>"
                        + COM.dragonflow.Page.portalQueryPage.getOptionsHTML(
                                vector1, hashmap4)
                        + "</select>"
                        + "</TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>Hold the \"Ctrl\" key to select multiple monitor types to filter on</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I></I></TD></TR>");
        jgl.HashMap hashmap5 = new HashMap();
        for (Enumeration enumeration3 = hashmap2.values("propertySet"); enumeration3
                .hasMoreElements(); hashmap5.add(enumeration3.nextElement(),
                "true")) {
        }
        Vector vector2 = new Vector();
        vector2.addElement("default");
        vector2.addElement("Default");
        vector2.addElement("mainParameters");
        vector2.addElement("common configuration parameters");
        vector2.addElement("mainState");
        vector2.addElement("common monitor data");
        vector2.addElement("links");
        vector2.addElement("SiteView URL links (tools and status icons)");
        vector2.addElement("acknowledges");
        vector2.addElement("acknowledgement status");
        vector2.addElement("secondaryParameters");
        vector2.addElement("detail configuration parameters (rarely used)");
        vector2.addElement("secondaryState");
        vector2.addElement("detail common monitor data (rarely used)");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Property Sets</TD><TD><TABLE><TR><TD ALIGN=LEFT><select name=propertySet multiple size=5>"
                        + COM.dragonflow.Page.portalQueryPage.getOptionsHTML(
                                vector2, hashmap5)
                        + "</select>"
                        + "</TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>Hold the \"Ctrl\" key to select multiple property sets</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I></I></TD></TR>");
        jgl.HashMap hashmap6 = new HashMap();
        for (Enumeration enumeration4 = hashmap2.values("property"); enumeration4
                .hasMoreElements(); hashmap6.add(enumeration4.nextElement(),
                "true")) {
        }
        Vector vector3 = new Vector();
        vector3.addElement("default");
        vector3.addElement("Default");
        vector3.addElement("_group");
        vector3.addElement("group ID (_group)");
        vector3.addElement("stateString");
        vector3.addElement("status (stateString)");
        vector3.addElement("category");
        vector3.addElement("error/warning/good (category)");
        vector3.addElement("last");
        vector3.addElement("last update time (last)");
        vector3.addElement("_name");
        vector3.addElement("name (_name)");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Properties</TD><TD><TABLE><TR><TD ALIGN=LEFT><select name=property multiple size=5>"
                        + COM.dragonflow.Page.portalQueryPage.getOptionsHTML(
                                vector3, hashmap6)
                        + "</select>"
                        + "</TD></TR>"
                        + "<TR><TD><FONT SIZE=-1>Hold the \"Ctrl\" key to select multiple property</FONT></TD></TR>"
                        + "</TABLE></TD><TD><I></I></TD></TR>");
        return;
    }

    jgl.HashMap fillInResultFrame(jgl.HashMap hashmap) {
        String s = request.getValue("query");
        jgl.HashMap hashmap1 = COM.dragonflow.SiteView.PortalFilter
                .queryStringToMap(s);
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(
                true);
        for (Enumeration enumeration = hashmap1.values("item"); enumeration
                .hasMoreElements(); hashmapordered.add("item", enumeration
                .nextElement())) {
        }
        if (request.getValue("nameMatch").length() > 0) {
            hashmapordered.add("name", request.getValue("nameMatch"));
        }
        if (request.getValue("statusMatch").length() > 0) {
            hashmapordered.add("status", request.getValue("statusMatch"));
        }
        for (Enumeration enumeration1 = request.getValues("monitorType"); enumeration1
                .hasMoreElements(); hashmapordered.add("type", enumeration1
                .nextElement())) {
        }
        for (Enumeration enumeration2 = request.getValues("categoryMatch"); enumeration2
                .hasMoreElements(); hashmapordered.add("category", enumeration2
                .nextElement())) {
        }
        for (Enumeration enumeration3 = request.getValues("propertySet"); enumeration3
                .hasMoreElements(); hashmapordered.add("propertySet",
                enumeration3.nextElement())) {
        }
        for (Enumeration enumeration4 = request.getValues("property"); enumeration4
                .hasMoreElements(); hashmapordered.add("property", enumeration4
                .nextElement())) {
        }
        s = "";
        for (Enumeration enumeration5 = hashmapordered.keys(); enumeration5
                .hasMoreElements();) {
            String s1 = (String) enumeration5.nextElement();
            Enumeration enumeration6 = hashmapordered.values(s1);
            while (enumeration6.hasMoreElements()) {
                String s2 = (String) enumeration6.nextElement();
                if (s2.length() > 0) {
                    if (s.length() > 0) {
                        s = s + "&";
                    }
                    s = s + s1 + "=" + java.net.URLEncoder.encode(s2);
                }
            }
        }

        hashmap.put("_id", request.getValue("id"));
        hashmap.put("_query", s);
        hashmap.put("_title", request.getValue("title"));
        return hashmap;
    }

    String getConfigFilePath() {
        return COM.dragonflow.SiteView.Platform.getRoot()
                + java.io.File.separator + "groups" + java.io.File.separator
                + "query.config";
    }

    void printListHeader() {
        outputStream
                .println("<TH>Name</TH><TH WIDTH=10%>Edit</TH><TH WIDTH=3%>Del</TH>");
    }

    void printListItem(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        String s1 = COM.dragonflow.Page.portalQueryPage.getValue(hashmap,
                "_title");
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=" + getPageName()
                + "&id=" + s + "&account=" + request.getAccount();
        String s3 = "<A href=" + s2 + "&operation=Delete>X</a>";
        String s4 = "<A href=" + s2 + "&operation=Edit>Edit</a>";
        outputStream.println("<TR><TD align=left>" + s1 + "</TD>" + "<TD" + ">"
                + s4 + "</TD>" + "<TD" + ">" + s3 + "</TD>" + "</TR>\n");
    }

    void verify(jgl.HashMap hashmap, jgl.HashMap hashmap1) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_title");
        if (s.length() == 0) {
            hashmap1.put("title", "query title required");
        }
    }

    public static void main(String args[]) {
        (new portalQueryPage()).handleRequest();
    }
}
