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

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI, treeControl

public class vMachinePage extends COM.dragonflow.Page.prefsPage {

    private static final String SET = "_set";

    private static final String ADD = "Add";

    private static final String DELETE = "Delete";

    private static final String LIST = "List";

    private static final String EDIT = "Edit";

    private static final String RECHECK = "ReCheck";

    public static String dynamicFilePath;

    public vMachinePage() {
    }

    String getMachineID() {
        String s = request.getValue("id");
        return s;
    }

    String getIDName() {
        return "id";
    }

    String getPage() {
        return "vMachine";
    }

    String getListTitle() {
        return "Dynamic Update Sets";
    }

    String getDynamicPage() {
        return "DynamicUpdate.htm";
    }

    String getPrefTitle() {
        return "Dynamic Update";
    }

    String getPrintFormSubmit() {
        return "Dynamic Update Set Definition";
    }

    public void printBody() throws Exception {
        String s = request.getValue("id");
        String s1 = request.getValue("operation");
        if (s1.length() == 0) {
            s1 = "List";
        }
        if (!request.actionAllowed("_preference") && s.length() == 0) {
            throw new HTTPRequestException(557);
        }
        if (s1.equals("List")) {
            printListForm(s1);
        } else if (s1.equals("Add")) {
            printAddForm(s1);
        } else if (s1.equals("Delete")) {
            printDeleteForm(s1);
        } else if (s1.equals("Edit")) {
            printAddForm(s1);
        } else if (s1.equals("ReCheck")) {
            printAddForm(s1);
        } else {
            printError("The link was incorrect", "unknown operation",
                    "/SiteView/" + request.getAccountDirectory()
                            + "/SiteView.html");
        }
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param httprequest
     * @param cgi
     * @param flag
     * @return
     */
    public static boolean createGroupFromRequest(String s, String s1,
            COM.dragonflow.HTTP.HTTPRequest httprequest,
            COM.dragonflow.Page.CGI cgi, boolean flag) {
        String s2 = httprequest.getValue("groupName");
        s1 = COM.dragonflow.Utils.I18N
                .toDefaultEncoding(COM.dragonflow.Utils.TextUtils
                        .keepChars(s2,
                                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
        String s3 = httprequest.getValue("parent");
        s1 = COM.dragonflow.Page.vMachinePage.getGroupIDFull(s1,
                COM.dragonflow.Utils.I18N.toDefaultEncoding(s3));
        String s4 = COM.dragonflow.SiteView.Platform.getRoot()
                + java.io.File.separator + "groups" + java.io.File.separator
                + s1 + ".mg";
        try {
            java.io.File file = new File(s4);
            if (file.exists() && !flag) {
                return false;
            }
        } catch (Exception exception) {
            /* empty */
        }

        s1 = cgi.computeGroupName(s1);
        Array array = new Array();
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(
                true);
        hashmapordered.put("_name", "config");
        if (s3 != null && s3.length() != 0) {
            hashmapordered.put("_parent", COM.dragonflow.Page.vMachinePage
                    .getGroupIDRelative(s3));
            try {
                Array array1 = cgi.ReadGroupFrames(s3);
                HashMap hashmap = (HashMap) array1.get(0);
                String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                        "_nextID");
                if (s5.length() == 0) {
                    s5 = "1";
                }
                HashMap hashmap1 = new HashMap();
                hashmap1.put("_id", s5);
                hashmap1.put("_class", "SubGroup");
                hashmap1.put("_group", COM.dragonflow.Page.vMachinePage
                        .getGroupIDRelative(s1));
                hashmap1.put("_name", s2);
                array1.insert(array1.size(),array1.size(), hashmap1);
                String s6 = COM.dragonflow.Utils.TextUtils.increment(s5);
                hashmap.put("_nextID", s6);
                cgi.WriteGroupFrames(s3, array1);
            } catch (java.io.IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
        if (!s2.equals(COM.dragonflow.Page.vMachinePage.getGroupIDRelative(s1))) {
            hashmapordered.put("_name", s2);
        }
        array.add(hashmapordered);
        hashmapordered.put("_nextID", "1");
        hashmapordered.put("_dependsOn", httprequest.getValue("_dependsOn"));
        hashmapordered.put("_dependsCondition", httprequest
                .getValue("_dependsCondition"));
        hashmapordered
                .put("_httpCharSet", httprequest.getValue("_httpCharSet"));
        hashmapordered.put("__template", httprequest.getValue("__template"));
        try {
            cgi.WriteGroupFrames(s1, array);
            COM.dragonflow.SiteView.SiteViewGroup
                    .updateStaticPages(COM.dragonflow.Page.CGI.getGroupIDFull(
                            s1, httprequest.getPortalServer()));
            COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
        return true;
    }

    /**
     * CAUTION: Dercompiled by hand.
     * 
     * @param s
     * @param s1
     * @param httprequest
     * @param printwriter
     * @param flag
     * @return
     */
    public static boolean postToGroupPage(String s, String s1,
            COM.dragonflow.HTTP.HTTPRequest httprequest,
            java.io.PrintWriter printwriter, boolean flag) {
        httprequest.requestMethod = "POST";
        httprequest.setValue("page", "group");
        if (!httprequest.getValue("operation").equals("Add")) {
            httprequest.setValue("operation", "Add");
        }
        httprequest.setUser("administrator");
        httprequest.setValue("groupName", s);
        httprequest.setValue("parent", s1);
        COM.dragonflow.Page.CGI cgi;
        try {
            Class class1 = Class.forName("COM.dragonflow.Page.groupPage");
            cgi = (COM.dragonflow.Page.CGI) class1.newInstance();
            cgi.request = httprequest;
            return COM.dragonflow.Page.vMachinePage.createGroupFromRequest(
                    "Add", s, httprequest, cgi, flag);
        } catch (ClassNotFoundException e) {
            String s2 = "";
            if (httprequest != null) {
                s2 = httprequest.getURL();
            }
            COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(httprequest, 404,
                    s2, e, printwriter);
        } catch (Exception e) {
            String s3 = "";
            if (httprequest != null) {
                s3 = httprequest.getURL();
            }
            COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(httprequest, 999,
                    s3, e, printwriter);
        }
        return true;
    }

    public static Array readDynamicSets() {
        Array array = new Array();
        java.io.File file = new File(dynamicFilePath);
        if (file.exists()) {
            try {
                array = COM.dragonflow.Properties.FrameFile
                        .readFromFile(dynamicFilePath);
            } catch (Exception exception) {
            }
        }
        return array;
    }

    void printListForm(String s) throws Exception {
        String s1 = request.getValue("id");
        String s2 = getListTitle();
        printBodyHeader(s2);
        printButtonBar(getDynamicPage(), "", getSecondNavItems(request));
        if (s1.length() == 0) {
            printPrefsBar(getPrefTitle());
        }
        Array array = getListTableHeaders();
        outputStream
                .println("<p><H2>"
                        + s2
                        + "</H2><P>Use SiteView to query a SNMP MIB or JDBC Database for"
                        + " new server addresses and create new monitors dynamically for the server using"
                        + " <a href=/SiteView/docs/MonitorSet.htm>Monitor Sets</a></p>"
                        + "<TABLE WIDTH=100% BORDER=1 cellspacing=0><TR>");
        for (int i = 0; i < array.size(); i++) {
            outputStream.println("<TH align=left>" + array.get(i) + "</TH>");
        }

        outputStream.println("</TR>");
        Array array1 = COM.dragonflow.Page.vMachinePage.readDynamicSets();
        if (array1.size() == 0) {
            outputStream
                    .println("<TR><TD> </TD><TD align=center>no Dynamic Update Sets defined</TD><TD> </TD></TR>\n");
        } else {
            for (Enumeration enumeration =  (Enumeration) array1.iterator(); enumeration
                    .hasMoreElements(); outputStream.println("</TR>")) {
                HashMap hashmap = (HashMap) enumeration.nextElement();
                Array array2 = getListTableRow(hashmap);
                outputStream.println("<TR>");
                for (int j = 0; j < array2.size(); j++) {
                    outputStream.println("<TD align=left>" + array2.get(j)
                            + "</TD>");
                }

            }

        }
        outputStream.println("</TABLE><BR>");
        if (request.actionAllowed("_preference")) {
            outputStream.println("<A HREF=" + getPageLink(getPage(), "Add")
                    + ">Add</A> a Dynamic Update Set\n" + "<br><br>\n");
        }
        printFooter(outputStream);
    }

    void printDeleteForm(String s) throws Exception {
        String s1 = request.getValue("id");
        if (request.isPost()
                && COM.dragonflow.Page.treeControl.notHandled(request)) {
            deleteDynamicSet(s1);
            printRefreshPage(getPageLink(getPage(), "List"), 0);
        } else {
            outputStream
                    .println("<FONT SIZE=+1>Are you sure you want to delete the dynamic update?</FONT><p><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=page value=vMachine><input type=hidden name=operation value=\""
                            + s
                            + "\">"
                            + "<input type=hidden name=id value=\""
                            + s1
                            + "\">"
                            + "<input type=hidden name=account value="
                            + request.getAccount()
                            + ">"
                            + "<TABLE WIDTH=\"100%\" BORDER=0><TR>"
                            + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\"><input type=submit value=\""
                            + s
                            + " Dynamic Set\"></TD>"
                            + "<TD WIDTH=\"6%\"></TD><TD ALIGN=RIGHT WIDTH=\"41%\"><A HREF=/SiteView/cgi/go.exe/SiteView?page=vMachine&operation=List&account="
                            + request.getAccount()
                            + ">Return to Detail</A></TD><TD WIDTH=\"6%\"></TD>"
                            + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    void deleteDynamicSet(String s) {
        adjustDynamicConfig(null, s);
    }

    void addDynamicSet(HashMap hashmap) {
        adjustDynamicConfig(hashmap, null);
    }

    void changeDynamicSet(HashMap hashmap) {
        adjustDynamicConfig(hashmap, COM.dragonflow.Utils.TextUtils.getValue(
                hashmap, "_id"));
    }

    void adjustDynamicConfig(HashMap hashmap, String s) {
        Array array = null;
        array = COM.dragonflow.Page.vMachinePage.readDynamicSets();
        Array array1;
        if (hashmap != null && s == null) {
            try {
                HashMap hashmap1 = getMasterConfig();
                if (!request.isStandardAccount()) {
                    hashmap1 = (HashMap) array.get(0);
                } else {
                    hashmap1 = getMasterConfig();
                }
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap1,
                        "_nextDynamicID");
                if (s.length() == 0) {
                    s = "10";
                }
                hashmap1.put("_nextDynamicID", COM.dragonflow.Utils.TextUtils
                        .increment(s));
                if (request.isStandardAccount()) {
                    saveMasterConfig(hashmap1);
                }
                hashmap.put("_id", s);
            } catch (java.io.IOException ioexception) {
                System.err.println("Could not read Dynamic configuration");
            }
            array1 = array;
            array1.add(hashmap);
        } else {
            array1 = new Array();
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap2 = (HashMap) enumeration.nextElement();
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2,
                        "_id");
                if (s1 != null
                        && s1.equals(s)
                        && COM.dragonflow.SiteView.Monitor
                                .isDynamicFrame(hashmap2)) {
                    if (hashmap != null) {
                        array1.add(hashmap);
                    }
                } else {
                    array1.add(hashmap2);
                }
            } 
        }
        saveDynamicFrameList(array1, request.getAccount());
    }

    public void saveDynamicFrameList(Array array, String s) {
        try {
            if (!COM.dragonflow.SiteView.Platform.isStandardAccount(s)) {
                WriteGroupFrames(s, array);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s);
            } else {
                COM.dragonflow.Properties.FrameFile.writeToFile(
                        COM.dragonflow.SiteView.Platform.getRoot()
                                + java.io.File.separator + "groups"
                                + java.io.File.separator + "dynamic.config",
                        array);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
            }
        } catch (java.io.IOException ioexception) {
        }
    }

    void displayForm(HashMap hashmap, String s) {
        java.util.Vector vector = COM.dragonflow.SiteView.SiteViewGroup
                .getTemplateList("templates.sets", request);
        String s1 = "";
        String s2 = (String) hashmap.get("_set");
        for (int i = 0; i < vector.size(); i++) {
            String s4 = (String) vector.elementAt(i);
            boolean flag = s4.equals(s2);
            s1 = s1
                    + COM.dragonflow.Page.vMachinePage.getListOptionHTML(s4, s4,
                            flag);
        }

        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field("Template Set: ", "<select size=1 name=set>"
                                + s1 + "</select>\n",
                                "Select the Monitor Set Template used to create new monitors.\n"));
        String s3 = COM.dragonflow.Utils.TextUtils
                .getValue(hashmap, "_groupSet");
        if (s.equals("Add") && (s3.length() < 0 || s3.indexOf("$NODE-IP$") < 0)) {
            s3 = "$NODE-IP$";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Monitor Set Subgroup Name: ",
                                "<input type=text name=groupSet size=50 value=\""
                                        + s3 + "\">",
                                "Monitor group name to be assigned to each set of monitors created using the above template. Default is IP address. Enter an optional text string to be appended to default name.\n"));
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Group Name: ",
                                "<input type=text name=group size=50 value=\""
                                        + COM.dragonflow.Utils.TextUtils
                                                .getValue(hashmap, "_group")
                                        + "\">",
                                "Enter a name for the group to be created to contain all subgroups created using the template selected above."));
        String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_parent");
        try {
            if (COM.dragonflow.Page.treeControl.useTree()) {
                Array array = new Array();
                if (s5.length() > 0) {
                    array.add(s5);
                }
                StringBuffer stringbuffer = new StringBuffer();
                COM.dragonflow.Page.treeControl treecontrol = new treeControl(
                        request, "_group", false);
                treecontrol
                        .process(
                                "SiteView Parent Group: ",
                                "",
                                "Select an existing SiteView group to which the above container group will be added as a subgroup. Choose <tt>SiteView Panel</tt> to create a new group on the SiteView Main Panel",
                                array, null, null, 2, this, stringbuffer);
                outputStream.println(stringbuffer);
            } else {
                outputStream
                        .println(COM.dragonflow.Page.vMachinePage
                                .field(
                                        "SiteView Parent Group: ",
                                        "<select size=1 name=parent>"
                                                + getMonitorOptionsHTML(s5, "",
                                                        18) + "</select>\n",
                                        "Select an existing SiteView group to which the above container group will be added as a subgroup. Choose <tt>SiteView Panel</tt> to create a new group on the SiteView Main Panel"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_frequency");
        if (s6.length() <= 0) {
            s6 = "21600";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Update Frequency: ",
                                "<input type=text name=frequency size=50 value=\""
                                        + s6 + "\">",
                                "Frequency (in seconds) that SiteView will query the MIB or database for new nodes and create monitor sets for new nodes."));
        String s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_excludeIP");
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Exclude IP: ",
                                "<input type=text name=excludeIP size=50 value=\""
                                        + s7 + "\">",
                                "Enter IP addresses to be excluded from the Update Set, for example, the default gateway IP. To exclude multiple IP address, separate them by commas"));
        String s8 = COM.dragonflow.Page.vMachinePage.getValue(hashmap, "_name");
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Title: ",
                                "<input type=text name=name size=50 value=\""
                                        + s8 + "\">",
                                "Optional title for this Dynamic Update Set. The default title is the server address"));
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "",
                                "<HR><CENTER><H3>SNMP MIB Search</H3></CENTER>\n",
                                "<p><font size=-1>The Dynamic Update can walk a SNMP MIB to find new IP Addresses and automatically create monitors for new servers.</font>\n"));
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Server Address: ",
                                "<input type=text name=host size=50 value=\""
                                        + COM.dragonflow.Utils.TextUtils
                                                .getValue(hashmap, "_host")
                                        + "\">",
                                "The server or console where the SNMP MIB is found. Enter the UNC style name (For example:<tt>\\servername</tt>) or the IP address."));
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        Enumeration enumeration = siteviewgroup
                .getMultipleValues("_dynamicOID");
        java.util.Vector vector1 = new Vector();
        for (; enumeration.hasMoreElements(); vector1
                .addElement((String) enumeration.nextElement())) {
        }
        String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_oid");
        s1 = "";
        vector1.addElement("other");
        for (int j = 0; j < vector1.size(); j++) {
            String s11 = (String) vector1.elementAt(j);
            boolean flag1 = s11.equals(s9);
            s1 = s1
                    + COM.dragonflow.Page.vMachinePage.getListOptionHTML(s11,
                            s11, flag1);
        }

        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "SNMP Object ID: ",
                                "<select size=1 name=oid>"
                                        + s1
                                        + "</select> Other: <INPUT TYPE=TEXT NAME=otheroid"
                                        + " SIZE=50 VALUE=\""
                                        + COM.dragonflow.Utils.TextUtils
                                                .getValue(hashmap, "_otheroid")
                                        + "\">\n",
                                "The root OID for the object that returns the node IP addresses to be monitored"));
        String s10 = COM.dragonflow.Page.vMachinePage.getValue(hashmap,
                "_poolIncluded").length() <= 0 ? "" : "CHECKED";
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Pool Included",
                                "<input type=checkbox name=poolIncluded " + s10
                                        + ">",
                                "Check this if the pool IP Address is included in the result of the OID walk (Note: for F5 servers)."));
        String s12 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_community");
        if (s12.length() <= 0) {
            s12 = "public";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field("Community: ",
                                "<input type=text name=community size=50 value=\""
                                        + s12 + "\">",
                                "The community variable for the SNMP MIB. The default is <tt>public</tt>"));
        if (s12.length() <= 0) {
            s12 = "public";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "",
                                "<HR><CENTER><H3>Database Search</H3></CENTER>\n",
                                "<p><font size=-1>The Dynamic Update can query a database table to find new IP Addresses and automatically create monitors for new servers.</font>\n"));
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "Database Connection URL: ",
                                "<input type=text name=dbConnectionURL size=50 value=\""
                                        + COM.dragonflow.Utils.TextUtils
                                                .getValue(hashmap,
                                                        "_dbConnectionURL")
                                        + "\">",
                                "Enter the URL to the database connection (for example,jdbc:inetdae:myserver.mycompany.com:1433?database=master)\n"));
        String s13 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_dbDriver");
        if (s13.length() <= 0) {
            s13 = "com.inet.tds.TdsDriver";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field("Database Driver: ",
                                "<input type=text name=dbDriver size=40 value=\""
                                        + s13 + "\">",
                                "Driver used to connect to the database (for example, com.inet.tds.TdsDriver) "));
        outputStream
                .println(COM.dragonflow.Page.vMachinePage
                        .field(
                                "SQL Query: ",
                                "<input type=text name=dbSqlQuery size=40 value=\""
                                        + COM.dragonflow.Utils.TextUtils
                                                .getValue(hashmap,
                                                        "_dbSqlQuery") + "\">",
                                "SQL query to run against a table in the database that returns the list of IP addresses to be monitored."));
        outputStream.println(COM.dragonflow.Page.vMachinePage.field(
                "Database Username: ",
                "<input type=text name=dbUserName size=40 value=\""
                        + COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                                "_dbUserName") + "\">",
                "Enter the username for connecting to the database."));
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        COM.dragonflow.Properties.StringProperty.getPrivate(
                COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_dbPassword"),
                "dbPassword", "vMachSuff", stringbuffer1, stringbuffer2);
        outputStream.println(COM.dragonflow.Page.vMachinePage.field(
                "Database Password: ", stringbuffer1.toString() + " size=40>"
                        + stringbuffer2.toString(),
                "Enter the password used to connect to the database."));
        String s14 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_dbConnectTimeout");
        if (s14.length() <= 0) {
            s14 = "60";
        }
        outputStream
                .println(COM.dragonflow.Page.vMachinePage.field(
                        "Connection Timeout: ",
                        "<input type=text name=dbConnectTimeout size=40 value=\""
                                + s14 + "\">",
                        "Connection timeout to use for the database."));
        String s15 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_dbQueryTimeout");
        if (s15.length() <= 0) {
            s15 = "60";
        }
        outputStream.println(COM.dragonflow.Page.vMachinePage.field(
                "Query Timeout: ",
                "<input type=text name=dbQueryTimeout size=40 value=\"" + s15
                        + "\">", "Query timeout to use for the database."));
    }

    public static String field(String s, String s1, String s2) {
        return "<TR><TD ALIGN=RIGHT VALIGN=TOP>" + s + "</TD>"
                + "<TD><TABLE><TR><TD ALIGN=LEFT VALIGN=TOP>" + s1
                + "</TD></TR>" + "<TR><TD><FONT SIZE=-1>" + s2
                + "</FONT></TD></TR>" + "</TABLE></TD><TD><I></I></TD></TR>";
    }

    public static String getListOptionHTML(String s, String s1, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<option ");
        if (flag) {
            stringbuffer.append("SELECTED ");
        }
        stringbuffer.append("value=");
        stringbuffer.append(s);
        stringbuffer.append(">");
        stringbuffer.append(s1);
        stringbuffer.append("</option>\n");
        return stringbuffer.toString();
    }

    public static HashMap findDynamicFrame(String s) {
        HashMap hashmap = new HashMap();
        Array array = null;
        try {
            array = COM.dragonflow.Properties.FrameFile
                    .readFromFile(COM.dragonflow.SiteView.Platform.getRoot()
                            + java.io.File.separator + "groups"
                            + java.io.File.separator + "dynamic.config");
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap) enumeration.nextElement();
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1,
                        "_id");
                if (s1.equals(s)) {
                    hashmap = hashmap1;
                }
            }
        } catch (java.io.IOException ioexception) {
            array = new Array();
        }
        return hashmap;
    }

    void printAddForm(String s) throws Exception {
        HashMap hashmap = new HashMap();
        String s1 = getMachineID();
        if (request.isPost()
                && COM.dragonflow.Page.treeControl.notHandled(request)) {
            if (request.getValue("group").length() <= 0) {
                printError("Need to have a group name", "Group name missing",
                        getPageLink(getPage(), "List"));
                return;
            }
            saveAddProperties(hashmap, s1);
            if (s.equals("Edit")) {
                changeDynamicSet(hashmap);
            } else if (s.equals("Add")) {
                addDynamicSet(hashmap);
                COM.dragonflow.Page.vMachinePage.postToGroupPage(
                        (String) hashmap.get("_group"), (String) hashmap
                                .get("_parent"), request, outputStream, true);
            }
            COM.dragonflow.SiteView.SiteViewGroup.currentSiteView()
                    .loadDynamic(hashmap);
            printRefreshPage(getPageLink(getPage(), "List"), 0);
            return;
        }
        if (s.equals("ReCheck")) {
            hashmap = COM.dragonflow.Page.vMachinePage.findDynamicFrame(s1);
            printProgressMessage("Checking for new nodes....");
            COM.dragonflow.SiteView.SiteViewGroup.currentSiteView()
                    .loadDynamic(hashmap);
            printRefreshPage(getPageLink(getPage(), "List"), 0);
            return;
        }
        String s2 = "Run";
        if (s.equals("Edit")) {
            s2 = "Update";
            hashmap = COM.dragonflow.Page.vMachinePage.findDynamicFrame(s1);
        } else {
            s2 = "Add";
            hashmap = new HashMap();
        }
        String s3 = s2;
        printBodyHeader(s3);
        printButtonBar(getDynamicPage(), "");
        outputStream.println("<p><H2>" + s3 + "</H2>\n");
        String s4 = (String) hashmap.get("_nodes");
        if (s4 == null) {
            s4 = "";
        }
        outputStream.println(getPagePOST(getPage(), s)
                + "<input type=hidden name=" + getIDName() + " value=" + s1
                + ">\n" + "<input type=hidden name=nodes value="
                + COM.dragonflow.Utils.TextUtils.escapeHTML(s4) + ">\n");
        outputStream.println("<TABLE>");
        displayForm(hashmap, s);
        outputStream.println("</TABLE>");
        outputStream
                .println("<TABLE WIDTH=100%><TR><TD><input type=submit value=\""
                        + s2
                        + "\"> "
                        + getPrintFormSubmit()
                        + "\n"
                        + "</TD></TR></TABLE>");
        printFooter(outputStream);
    }

    void saveAddProperties(HashMap hashmap, String s) {
        String s1 = request.getValue("host");
        hashmap.put("_id", s);
        hashmap.put("_host", s1);
        hashmap.put("_parent", request.getValue("parent"));
        hashmap.put("_nodes", request.getValue("nodes"));
        hashmap.put("_oid", request.getValue("oid"));
        hashmap.put("_otheroid", request.getValue("otheroid"));
        hashmap.put("_community", request.getValue("community"));
        hashmap.put("_name", request.getValue("name"));
        hashmap.put("_set", request.getValue("set"));
        hashmap.put("_group", request.getValue("group"));
        hashmap.put("_groupSet", request.getValue("groupSet"));
        hashmap.put("_excludeIP", request.getValue("excludeIP"));
        hashmap.put("_frequency", request.getValue("frequency"));
        hashmap.put("_dbConnectionURL", request.getValue("dbConnectionURL"));
        hashmap.put("_poolIncluded", request.getValue("poolIncluded"));
        hashmap.put("_dbDriver", request.getValue("dbDriver"));
        hashmap.put("_dbSqlQuery", request.getValue("dbSqlQuery"));
        hashmap.put("_dbUserName", request.getValue("dbUserName"));
        hashmap.put("_dbPassword", COM.dragonflow.Properties.StringProperty
                .getPrivate(request, "dbPassword", "vMachSuff", null, null));
        hashmap.put("_dbPassword", request.getValue("dbPassword"));
        hashmap.put("_dbQueryTimeout", request.getValue("dbQueryTimeout"));
        hashmap.put("_dbConnectTimeout", request.getValue("dbConnectTimeout"));
    }

    Array getListTableHeaders() {
        Array array = new Array();
        array.add(new String("Name"));
        array.add(new String("Server"));
        if (request.actionAllowed("_preference")) {
            array.add(new String("Edit"));
            array.add(new String("Del"));
            array.add(new String("Check"));
        }
        return array;
    }

    Array getListTableRow(HashMap hashmap) {
        Array array = new Array();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        String s1 = COM.dragonflow.Page.vMachinePage.getValue(hashmap, "_name");
        if (s1.length() == 0) {
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
        }
        if (s1.length() <= 0) {
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                    "_dbConnectionURL");
        }
        array.add(new String(s1));
        array.add(new String(COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_host").length() <= 0 ? COM.dragonflow.Utils.TextUtils
                .getValue(hashmap, "_dbConnectionURL")
                : COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host")));
        if (request.actionAllowed("_preference")) {
            array.add(new String("<A href=" + getPageLink("vMachine", "Edit")
                    + "&id=" + s + ">Edit</a>"));
            array.add(new String("<A href=" + getPageLink("vMachine", "Delete")
                    + "&id=" + s + ">X</a>"));
            array.add(new String("<A href="
                    + getPageLink("vMachine", "ReCheck") + "&id=" + s
                    + ">Re-Check</a>"));
        }
        return array;
    }

    public static void main(String args[]) {
        (new vMachinePage()).handleRequest();
    }

    static {
        dynamicFilePath = COM.dragonflow.SiteView.Platform.getRoot()
                + java.io.File.separator + "groups" + java.io.File.separator
                + "dynamic.config";
    }
}
