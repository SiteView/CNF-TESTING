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

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, serverPage, CGI

public abstract class remoteBase extends COM.dragonflow.Page.prefsPage {

    private static final String DEFAULT_PASSWORD = "*********";

    private static final String SSH_AUTH_METHOD_FORM_VARIABLE = "sshAuthMethod";

    private static final String PASSWORD_FORM_VARIABLE = "password";

    private static final String SSH_CLIENT_METHOD_VARIABLE = "sshClient";

    private static final String KEYFILE_FORM_VARIABLE = "keyFile";

    private static final String FORCE_VERSION2_FORM_VARIABLE = "version2";

    private static final String DISABLE_CACHE_FORM_VARIABLE = "disableCache";

    private static final String CONNECTIONS_LIMIT = "sshConnectionsLimit";

    private static final String COMMAND_LINE_FORM_VARIABLE = "sshCommandLine";

    private static final String PORT_FORM_VARIABLE = "sshPort";

    public remoteBase() {
    }

    abstract jgl.Array getListTableHeaders(String s);

    abstract jgl.Array getListTableRow(jgl.HashMap hashmap, String s);

    abstract void doTest(COM.dragonflow.SiteView.Machine machine);

    abstract String getTestMachineID();

    abstract String getTestMachineName();

    abstract String getTestDescription();

    abstract COM.dragonflow.SiteView.Machine getTestMachine();

    abstract String getTestCentra();

    abstract String getHelpPage();

    abstract String getPage();

    abstract String getIDName();

    abstract String getNextMachineName();

    abstract String getRemoteName();

    abstract String getListTitle();

    abstract String getListSubtitle();

    abstract String getPrefTitle();

    abstract String getPrintFormSubmit();

    abstract boolean displayFormTable(jgl.HashMap hashmap, String s);

    void saveAddProperties(String s, jgl.HashMap hashmap,
            String s1) {
        hashmap.put(COM.dragonflow.SiteView.Machine.pSshAuthMethod.getName(),
                request.getValue("sshAuthMethod"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSshKeyFile.getName(),
                request.getValue("keyFile"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSSHClient.getName(),
                request.getValue("sshClient"));
        hashmap.put(
                COM.dragonflow.SiteView.Machine.pSSHForceVersion2.getName(),
                request.getValue("version2"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSSHConnectionCaching
                .getName(), request.getValue("disableCache"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSSHConnectionsLimit
                .getName(), request.getValue("sshConnectionsLimit"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSSHCommandLine.getName(),
                request.getValue("sshCommandLine"));
        hashmap.put(COM.dragonflow.SiteView.Machine.pSSHPort.getName(), request
                .getValue("sshPort"));
        String s2 = request.getValue("password");
        if (!s2.equals("*********")) {
            hashmap
                    .put(COM.dragonflow.SiteView.Machine.pPassword.getName(),
                            s2);
        }
    }

    String getMachineID() {
        String s = request.getValue(getIDName());
        return s;
    }

    jgl.HashMap getTestMachineFrame() {
        return findMachine(getFrames(), getMachineID());
    }

    jgl.Array getFrames() {
        jgl.Array array = new Array();
        try {
            array = readMachines(getRemoteName());
        } catch (java.lang.Exception exception) {
        }
        return array;
    }

    String getNextMachineID(jgl.HashMap hashmap) {
        String s = null;
        if (hashmap != null) {
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                    getNextMachineName());
        } else {
            s = request.getValue(getNextMachineName());
        }
        return s;
    }

    String getReturnLink(boolean flag, String s) {
        if (s.length() > 0) {
            if (flag) {
                return "<input type=hidden name=storeID value=" + s + ">\n";
            } else {
                return "&storeID=" + s;
            }
        } else {
            return "";
        }
    }

    void printBackLink() {
        String s = request.getValue("backURL");
        String s1 = request.getValue("backLabel");
        String s2 = request.getValue("backDescription");
        String s3 = request.getValue("backOperation");
        String s4 = request.getValue("storeID");
        String s5;
        if (s.length() == 0) {
            s5 = getPagePOST(getPage(), s3) + getReturnLink(true, s4);
        } else {
            s5 = "<FORM ACTION=" + s + ">\n";
        }
        if (s1.length() == 0) {
            s1 = "Back to Remote Machines";
        }
        if (s3.length() == 0) {
            s3 = "List";
        }
        outputStream.println(s5 + "<input type=submit value=\"" + s1 + "\">"
                + s2 + "</input>\n</form>");
    }

    void formLogin(jgl.HashMap hashmap) {
        String s = getPage();
        String s1 = "";
        if (s.indexOf("ntmachine") != -1) {
            s1 = "Enter the login for the remote server. <br>For a domain login, include the domain name before the user login (example: <i>domainname\\user</i>). <br>For a local or standalone login, include the machine name before the user login  (example: <i>machinename\\user</i>).";
        } else if (s.indexOf("machine") != -1) {
            s1 = "Enter the login to use for connecting to the remote server";
        } else {
            s1 = "Enter the login for the remote server";
        }
        outputStream.println(field("Login",
                "<input type=text name=login size=50 value=\""
                        + COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                                "_login") + "\">", s1));
        String s2 = "";
        if (COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pPassword.getName()) != "") {
            s2 = "*********";
        }
        outputStream
                .println(field("Password",
                        "<input type=password name=password size=50 value=\""
                                + s2 + "\">",
                        "The password for the remote server or the passphrase for the SSH key file."));
        outputStream
                .println(field(
                        "Title",
                        "<input type=text name=title size=50 value=\""
                                + COM.dragonflow.Page.remoteBase.getValue(
                                        hashmap, "_name") + "\">",
                        "Optional title describing the remote server. The default title is the server address"));
    }

    boolean dependentCheck() {
        if (request.getValue("method").equals("ssh")) {
            COM.dragonflow.SSH.SSHManager.getInstance().deleteRemote(
                    getRemoteUniqueId());
            String s = COM.dragonflow.SSH.SSHManager
                    .getInstance()
                    .checkClient(
                            request
                                    .getValue(COM.dragonflow.SiteView.Machine.pSSHClient
                                            .getName()));
            if (s.length() != 0) {
                String s1 = "SSH client not found";
                printBodyHeader(s1);
                printButtonBar("Remote.htm", "");
                outputStream.println("<H2>" + s1 + "</H2>\n");
                outputStream
                        .println("<p>To monitor using SSH, SiteView requires that the ssh client is installed at <blockquote>"
                                + s + "</blockquote>");
                printFooter(outputStream);
                return true;
            }
        }
        return false;
    }

    String field(String s, String s1,
            String s2) {
        return "<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + s + "</TD>"
                + "<TD><TABLE><TR><TD ALIGN=LEFT>" + s1 + "</TD></TR>"
                + "<TR><TD><FONT SIZE=-1>" + s2 + "</FONT></TD></TR>"
                + "</TABLE></TD><TD><I></I></TD></TR>";
    }

    public void printBody() throws java.lang.Exception {
        String s = request.getValue("storeID");
        String s1 = request.getValue("operation");
        if (s1.length() == 0) {
            s1 = "List";
        }
        if (s1.equals("Test")) {
            if (!request.actionAllowed("_preferenceTest") && s.length() == 0) {
                throw new HTTPRequestException(557);
            }
            printTestForm(s1);
        } else {
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
            } else {
                printError("The link was incorrect", "unknown operation",
                        "/SiteView/" + request.getAccountDirectory()
                                + "/SiteView.html");
            }
        }
    }

    void printListForm(String s) throws java.io.IOException {
        String s1 = request.getValue("backURL");
        String s3 = request.getValue("storeURL");
        String s4 = request.getValue("storeID");
        if (s1.length() > 0) {
            COM.dragonflow.Page.serverPage.clientID++;
            COM.dragonflow.Page.serverPage.setReturnURL(
                    COM.dragonflow.Page.serverPage.storeReturnURL, s3);
            COM.dragonflow.Page.serverPage.setReturnURL(
                    COM.dragonflow.Page.serverPage.thisURL, s1);
            s4 = String
                    .valueOf(COM.dragonflow.Page.serverPage.clientID);
        }
        String s5 = getListTitle();
        String s6 = getListSubtitle();
        printBodyHeader(s5);
        printButtonBar(getHelpPage(), "", getSecondNavItems(request));
        if (s4.length() == 0) {
            printPrefsBar(getPrefTitle());
        }
        jgl.Array array = getListTableHeaders(s4);
        outputStream
                .println("<H2>"
                        + s5
                        + "</H2> "
                        + "<p>"
                        + s6
                        + "</p>"
                        + "<TABLE WIDTH=100% BORDER=2 cellspacing=0><TR CLASS=\"tabhead\">");
        for (int i = 0; i < array.size(); i++) {
            outputStream.println("<TH align=left>" + array.at(i) + "</TH>");
        }

        outputStream.println("</TR>");
        jgl.Array array1 = readMachines(getRemoteName());
        if (array1.size() == 0) {
            outputStream
                    .println("<TR><TD> </TD><TD align=center>no remote servers defined</TD><TD> </TD></TR>\n");
        } else {
            for (Enumeration enumeration = array1.elements(); enumeration
                    .hasMoreElements(); outputStream.println("</TR>")) {
                jgl.HashMap hashmap = (jgl.HashMap) enumeration.nextElement();
                jgl.Array array2 = getListTableRow(hashmap, s4);
                outputStream.println("<TR>");
                for (int j = 0; j < array2.size(); j++) {
                    outputStream.println("<TD align=left>" + array2.at(j)
                            + "</TD>");
                }

            }

        }
        outputStream.println("</TABLE><BR>");
        if (request.actionAllowed("_preference") || s4.length() > 0) {
            outputStream.println("<A HREF=" + getPageLink(getPage(), "Add")
                    + getReturnLink(false, s4) + ">Add</A> a Remote Machine\n"
                    + "<br><br>\n");
        }
        if (s4.length() > 0) {
            String s2 = new String(COM.dragonflow.Page.serverPage
                    .getReturnURL(COM.dragonflow.Page.serverPage.thisURL, s4));
            outputStream.println("<a href=\"" + s2 + "&pop=true&storeID=" + s4
                    + "\">Return to Choose Server</a>");
        }
        printFooter(outputStream);
    }

    void printForm(String s, jgl.Array array, jgl.HashMap hashmap)
            throws java.lang.Exception {
        String s1 = "";
        String s2 = getMachineID();
        String s3 = COM.dragonflow.Page.remoteBase.getSubmitName(s);
        String s4 = request.getValue("storeID");
        jgl.HashMap hashmap1;
        if (s.equals("Edit")) {
            hashmap1 = findMachine(array, s2);
            s1 = s3 + " Remote Server : " + hashmap1.get("_name");
        } else {
            hashmap1 = new HashMap();
            String s5 = request.getValue("host");
            hashmap1.put("_host", s5);
            s1 = s3 + " Remote Server";
        }
        printBodyHeader(s1);
        printButtonBar(getHelpPage(), "");
        outputStream.println("<p><H2>" + s1 + "</H2>\n");
        outputStream.println(getPagePOST(getPage(), s));
        outputStream.println("<input type=hidden name=backURL value=\""
                + request.getValue("backURL") + "\">\n"
                + "<input type=hidden name=backLabel value=\""
                + request.getValue("backLabel") + "\">");
        if (s.equals("Edit")) {
            outputStream.println("<input type=hidden name=" + getIDName()
                    + " value=" + s2 + ">");
        }
        outputStream.println(getReturnLink(true, s4));
        outputStream.println("<TABLE>");
        boolean flag = displayFormTable(hashmap1, s);
        outputStream.println("</TABLE>");
        if (flag) {
            outputStream.println("<TABLE WIDTH=100%><TR><TD>"
                    + getReturnLink(true, s4) + "<input type=submit value=\""
                    + s3 + "\"> " + getPrintFormSubmit() + "\n"
                    + "</TD></TR></TABLE>");
        } else {
            printBackLink();
        }
        printFooter(outputStream);
    }

    private jgl.Array parseHosts(String s) {
        jgl.Array array = new Array();
        if (s.indexOf(",") != -1) {
            array = COM.dragonflow.Utils.TextUtils.splitArray(s, ",");
        } else {
            array.add(s.trim());
        }
        return array;
    }

    private int getIndex(jgl.Array array, jgl.HashMap hashmap) {
        if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id").length() == 0) {
            return -1;
        }
        for (int i = 0; i < array.size(); i++) {
            jgl.HashMap hashmap1 = (jgl.HashMap) array.at(i);
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id").equals(
                    COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id"))) {
                return i;
            }
        }

        return -1;
    }

    void printAddForm(String s) throws java.lang.Exception {
        jgl.Array array = getFrames();
        if (request.isPost()) {
            if (dependentCheck()) {
                return;
            }
            String s1 = null;
            Object obj = null;
            jgl.HashMap hashmap1 = null;
            int i = -1;
            jgl.Array array1 = parseHosts(request.getValue("host"));
            boolean flag = array1.size() > 1;
            for (int j = 0; j < array1.size(); j++) {
                jgl.HashMap hashmap;
                if (s.equals("Add")) {
                    if (hashmap1 == null) {
                        hashmap1 = getMasterConfig();
                    }
                    s1 = getNextMachineID(hashmap1);
                    hashmap = new HashMap();
                    if (s1.length() == 0) {
                        s1 = "10";
                    }
                    hashmap1.put(getNextMachineName(),
                            COM.dragonflow.Utils.TextUtils.increment(s1));
                } else {
                    s1 = getMachineID();
                    hashmap = findMachine(array, s1);
                    i = getIndex(array, hashmap);
                    array.remove(hashmap);
                }
                hashmap.put("_host", array1.at(j).toString().trim());
                if (request.getValue("title").length() == 0) {
                    hashmap.put("_name", hashmap.get("_host"));
                } else if (flag) {
                    hashmap.put("_name", request.getValue("title") + " - "
                            + hashmap.get("_host"));
                } else {
                    hashmap.put("_name", request.getValue("title"));
                }
                saveAddProperties(s, hashmap, s1);
                if (s.equals("Add")) {
                    array.add(hashmap);
                } else {
                    array.insert(i, hashmap);
                }
            }

            saveMachines(array, getRemoteName());
            String s2 = "";
            if (request.getValue("addtest").indexOf("add") != -1
                    || array1.size() > 1) {
                s2 = getPageLink(getPage(), "List")
                        + "&"
                        + getIDName()
                        + "="
                        + s1
                        + "&backURL="
                        + request.getValue("backURL")
                        + "&backLabel="
                        + java.net.URLEncoder.encode(request
                                .getValue("backLabel"))
                        + "&backDescription="
                        + java.net.URLEncoder.encode(request
                                .getValue("backDescription"))
                        + getReturnLink(false, request.getValue("storeID"));
            } else {
                s2 = getPageLink(getPage(), "Test")
                        + "&"
                        + getIDName()
                        + "="
                        + s1
                        + "&backURL="
                        + request.getValue("backURL")
                        + "&backLabel="
                        + java.net.URLEncoder.encode(request
                                .getValue("backLabel"))
                        + "&backDescription="
                        + java.net.URLEncoder.encode(request
                                .getValue("backDescription"))
                        + getReturnLink(false, request.getValue("storeID"));
            }
            autoFollowPortalRefresh = false;
            printRefreshPage(s2, 0);
        } else {
            printForm(s, array, new HashMap());
        }
    }

    void printDeleteForm(String s) throws java.lang.Exception {
        String s1 = getMachineID();
        String s2 = "ID " + s1;
        jgl.Array array = readMachines(getRemoteName());
        jgl.HashMap hashmap = findMachine(array, s1);
        if (hashmap != null) {
            s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_name");
            if (s2.length() == 0) {
                s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
            }
        }
        if (request.isPost()) {
            try {
                COM.dragonflow.SSH.SSHManager.getInstance().deleteRemote(
                        getRemoteUniqueId());
                array.remove(hashmap);
                saveMachines(array, getRemoteName());
                autoFollowPortalRefresh = false;
                printRefreshPage(getPageLink(getPage(), "List")
                        + getReturnLink(false, request.getValue("storeID")), 0);
            } catch (java.lang.Exception exception) {
                printError("There was a problem deleting the remote server.",
                        exception.toString(), "/SiteView/"
                                + request.getAccountDirectory()
                                + "/SiteView.html");
            }
        } else {
            printBodyHeader("Delete Confirmation");
            outputStream
                    .println("<FONT SIZE=+1>Are you sure you want to delete the remote server <B>"
                            + s2
                            + "</B>?</FONT>"
                            + "<p>"
                            + getPagePOST(getPage(), s)
                            + "<input type=hidden name="
                            + getIDName()
                            + " value=\""
                            + s1
                            + "\">"
                            + getReturnLink(true, request.getValue("storeID"))
                            + "<TABLE WIDTH=100% BORDER=0><TR>"
                            + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit value=\""
                            + s
                            + "\"></TD>"
                            + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF="
                            + getPageLink(getPage(), "List")
                            + ">Return to Detail</A></TD><TD WIDTH=6%></TD>"
                            + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    public void printTestForm(String s) {
        printBodyHeader("checking the remote connection");
        printButtonBar(getHelpPage(), "");
        outputStream.println("<h2>Remote Machine Connection Test</h2>");
        outputStream
                .println("<p>This will test to see if the remote monitoring connection to:<b> "
                        + getTestMachineName()
                        + "</b> is\n"
                        + "working properly.\n");
        outputStream.println(getTestDescription());
        outputStream
                .println("<table border=\"0\" width=\"99%\"><tr><td width=\"50%\">&nbsp;</td><td align=\"right\">");
        printBackLink();
        outputStream.println("</td></tr></table>");
        if (!isPortalServerRequest()) {
            printContentStartComment();
            doTest(getTestMachine());
            printContentEndComment();
        } else {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView) getSiteView();
            if (portalsiteview != null) {
                String s1 = portalsiteview
                        .getURLContentsFromRemoteSiteView(request,
                                getTestCentra());
                outputStream.println(s1);
            }
        }
        printBackLink();
        outputStream.flush();
    }

    private static String _stripDoubleSlash(String s) {
        if (s.length() >= 2 && s.substring(0, 2).equals("\\\\")) {
            s = s.substring(2);
        }
        return s;
    }

    protected String getSshAuthMethodHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSshAuthMethod.getName());
        if (s.length() == 0) {
            s = "password";
        }
        return field("SSH Authentication Method",
                "<select name=sshAuthMethod size=1>"
                        + COM.dragonflow.Page.remoteBase.getOptionsHTML(
                                COM.dragonflow.SiteView.Machine
                                        .getAllowedSshAuthMethods(), s),
                "Select the method to use to authenticate to the remote server.");
    }

    protected String getSshClientHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSSHClient.getName());
        if (request.getValue("operation").equals("Add")) {
            s = "java";
        } else if (s.length() == 0) {
            s = "plink";
        }
        jgl.Array array = COM.dragonflow.SiteView.Machine
                .getAllowedSshConnectionMethods();
        boolean flag = false;
        for (int i = 0; i < array.size(); i ++) {
            if (array.at(i).equals(s)) {
                flag = true;
                break;
            }
        } 
        
        if (!flag) {
            s = "";
        }
        return field("SSH Client", "<select name=sshClient size=1>"
                + COM.dragonflow.Page.remoteBase.getOptionsHTML(array, s),
                "Select the client to use to connect to the remote server.");
    }

    protected String getSSHKeyFileHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSshKeyFile.getName());
        if (s.length() == 0) {
            s = COM.dragonflow.SiteView.Machine.pSshKeyFile.getDefault();
        }
        return field(
                "Key File for SSH connections",
                "<input type=text name=keyFile size=50 value=\"" + s + "\">",
                "Enter the path to the file containing the private key for this SSH connection. <b> Only valid if authentication method is \"Key File\".");
    }

    protected String getSSHCommandLine(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSSHCommandLine.getName());
        if (s.length() == 0) {
            s = COM.dragonflow.SiteView.Machine.pSSHCommandLine.getDefault();
        }
        return field(
                "Custom Commandline",
                "<input type=text name=sshCommandLine size=50 value=\"" + s
                        + "\">",
                "Enter the command for execution of external ssh client.  For substituion with above options use $host$, $user$ and $password$ respectivly. <b> This setting is for only for connections using an external process.</b>");
    }

    protected String getNumConnectionseHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSSHConnectionsLimit.getName());
        if (s.length() == 0) {
            s = COM.dragonflow.SiteView.Machine.pSSHConnectionsLimit
                    .getDefault();
        }
        return field("Connection Limit",
                "<input type=text name=sshConnectionsLimit size=2 value=\"" + s
                        + "\">",
                "Enter the maximum number of open connections allowed for this remote.");
    }

    protected String getPortNumberHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSSHPort.getName());
        if (s.length() == 0) {
            s = COM.dragonflow.SiteView.Machine.pSSHPort.getDefault();
        }
        return field(
                "Port Number",
                "<input type=text name=sshPort size=2 value=\"" + s + "\">",
                "Enter the port number that the ssh server is running on. <b> Default is 22. </b>");
    }

    protected String getSSHForceVersion2Html(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                COM.dragonflow.SiteView.Machine.pSSHForceVersion2.getName());
        if (s.length() > 0) {
            s = " CHECKED";
        }
        return field(
                "SSH Version 2 Only",
                "<input type=checkbox name=version2" + s + ">",
                "Check this box to force SSH to only use SSH protocol version 2. <b> ( This SSH option is only supported using the internal java libraries connection method) </b>");
    }

    protected String getDisableCacheHtml(jgl.HashMap hashmap) {
        String s = COM.dragonflow.Utils.TextUtils
                .getValue(hashmap,
                        COM.dragonflow.SiteView.Machine.pSSHConnectionCaching
                                .getName());
        if (s.length() > 0) {
            s = " CHECKED";
        }
        return field("Disable Connection Caching",
                "<input type=checkbox name=disableCache" + s + ">",
                "Check this box to disable caching of SSH connections. ");
    }

    public String getRemoteUniqueId() {
        return request.getValue("ntMachineID");
    }

    protected StringBuffer getAdnvacedSSHOptions(jgl.HashMap hashmap) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer
                .append("<TR><TD ALIGN=\"CENTER\" VALIGN=\"CENTER\" colspan=3><HR><H3>SSH Advanced Options</H3></TD></TR>");
        stringbuffer.append(getSshClientHtml(hashmap));
        stringbuffer.append(getPortNumberHtml(hashmap));
        stringbuffer.append(getDisableCacheHtml(hashmap));
        stringbuffer.append(getNumConnectionseHtml(hashmap));
        stringbuffer.append(getSshAuthMethodHtml(hashmap));
        stringbuffer.append(getSSHKeyFileHtml(hashmap));
        stringbuffer.append(getSSHForceVersion2Html(hashmap));
        stringbuffer.append(getSSHCommandLine(hashmap));
        return stringbuffer;
    }
}
