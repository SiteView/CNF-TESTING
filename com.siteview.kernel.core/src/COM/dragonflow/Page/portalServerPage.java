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

import com.recursionsw.jgl.Array;
import COM.dragonflow.SiteView.PortalSync;

// Referenced classes of package COM.dragonflow.Page:
// portalPreferencePage

public class portalServerPage extends COM.dragonflow.Page.portalPreferencePage
{

    public portalServerPage()
    {
    }

    String getTitle()
    {
        if(request.getValue("operation").indexOf("SiteSeer") >= 0)
        {
            return "SiteSeer Account";
        } else
        {
            return "SiteView Server";
        }
    }

    String getPageName()
    {
        return "portalServer";
    }

    String getHelpPage()
    {
        return "ScopePrefs.htm";
    }

    void verify(HashMap hashmap, HashMap hashmap1)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_server");
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logCollectorRefresh");
        if(s.length() == 0)
        {
            hashmap1.put("server", "server host name or IP address required");
        }
        if(s1.length() == 0 && s.toLowerCase().startsWith("siteseer"))
        {
            hashmap.put("_logCollectorRefresh", "300");
        }
        if(hashmap1.size() == 0 && COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_title").length() == 0)
        {
            hashmap.put("_title", s);
        }
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_proxy");
        if(s2.length() > 0)
        {
            if(COM.dragonflow.Utils.TextUtils.hasSpaces(s2))
            {
                hashmap1.put("proxy", "no spaces are allowed");
            } else
            if(s2.length() > 0)
            {
                int i = s2.indexOf(':');
                int j = -1;
                if(i != -1)
                {
                    j = COM.dragonflow.Utils.TextUtils.readInteger(s2, i + 1);
                }
                if(j == -1)
                {
                    hashmap1.put("proxy", "missing port number in Proxy address");
                }
            }
        }
    }

    void printListOperations()
    {
        outputStream.println("<A HREF=" + getPageLink(getPageName(), "Add") + ">Add</A> " + getTitle() + "\n");
        outputStream.println("<P><A HREF=" + getPageLink(getPageName(), "AddSiteSeer") + ">Add</A> SiteSeer Account\n");
    }

    void printBasicProperties(HashMap hashmap, HashMap hashmap1)
        throws java.io.IOException
    {
        if(request.getValue("operation").indexOf("SiteSeer") == -1)
        {
            printBasicSiteViewProperties(hashmap, hashmap1);
        } else
        {
            printBasicSiteSeerProperties(hashmap, hashmap1);
        }
    }

    void printBasicSiteViewProperties(HashMap hashmap, HashMap hashmap1)
        throws java.io.IOException
    {
        outputStream.println("Adding a SiteView server will allow the " + COM.dragonflow.SiteView.Platform.productName + " to connect to a\n" + "SiteView to retrieve it's current configurations\n" + "and to continuously retrieve the status of the monitoring data." + "<P>\n");
        outputStream.println("<TABLE>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Server Address</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=server size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_server") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>the server address and SiteView port of the server (for example, demo." + COM.dragonflow.SiteView.Platform.exampleDomain + ":80)</FONT></TD></TR>" + "<TR><TD><FONT SIZE=-1>to communicate with the remote SiteView using https include the protocol specifier in the server address " + "(for example, https://demo." + COM.dragonflow.SiteView.Platform.exampleDomain + ":80)</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "server") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Title</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=title size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_title") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional title for this item in the Multi-View window, the default title is the server address</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "title") + "</I></TD></TR>");
        outputStream.println("</TABLE>");
    }

    void printBasicSiteSeerProperties(HashMap hashmap, HashMap hashmap1)
        throws java.io.IOException
    {
        outputStream.println("Adding a SiteSeer account will allow the " + COM.dragonflow.SiteView.Platform.productName + " to connect to a\n" + "SiteSeer account to retrieve it's current configurations\n" + "and to continuously retrieve the status of the monitoring data." + "<P>\n");
        outputStream.println("<TABLE>");
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_server").toLowerCase();
        Array array = new Array();
        for(int i = 1; i <= 9; i++)
        {
            String s1 = "siteseer" + i + ".Dragonflow.com";
            if(i == 1)
            {
                s1 = "siteseer.Dragonflow.com";
            }
            array.add(s1);
            array.add(s1);
        }

        outputStream.println("<TR><TD ALIGN=RIGHT>Server Address</TD>");
        outputStream.println("<TD><TABLE><TR><TD ALIGN=LEFT>");
        outputStream.println("<select size=1 name=\"server\">");
        outputStream.println(COM.dragonflow.Page.portalServerPage.getOptionsHTML(array, s));
        outputStream.println("</select></TD></TR>");
        outputStream.println("</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "server") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Title</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=title size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_title") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional title for this item in the Multi-View window, the default title is the server address</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "title") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Account</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=loginAccount size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_loginAccount") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>SiteSeer accounts, enter the account name</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "loginAccount") + "</I></TD></TR>");
        outputStream.println("</TABLE>");
    }

    boolean hasAdvancedOptions()
    {
        return true;
    }

    void printAdvancedProperties(HashMap hashmap, HashMap hashmap1)
    {
        String s = request.getValue("operation");
        Array array = new Array();
        array.add("NT");
        array.add("Windows NT/2000");
        array.add("Sun");
        array.add("Solaris");
        String s1 = COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_platformOS");
        if(s1.length() == 0)
        {
            s1 = COM.dragonflow.SiteView.Machine.osToString(COM.dragonflow.SiteView.Platform.getLocalPlatform());
        }
        outputStream.println("<TABLE>");
        outputStream.println("<TR><TD ALIGN=RIGHT></TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=checkbox name=disabled size=50 " + (COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_disabled").length() <= 0 ? "" : "checked") + ">Disable</TD></TR>" + "<TR><TD><FONT SIZE=-1>temporarily disable downloads from this SiteView server</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "debug") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>User Name</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=username size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_username") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional user name for connecting to the SiteView server, the default is to use the administrator user name</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "username") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Password</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=password size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_password") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional password for connecting to the SiteView server, the default is to use the administrator password</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "password") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Proxy</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=proxy size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_proxy") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional proxy server to use including port number (for example, proxy." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8080)</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "proxy") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Proxy User Name</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=proxyusername size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_proxyusername") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional user name for connecting through a proxy server</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "proxyusername") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Proxy Password</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=proxypassword size=50 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_proxypassword") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional password for connecting through a proxy server</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "proxypassword") + "</I></TD></TR>");
        if(s.indexOf("SiteSeer") == -1)
        {
            outputStream.println("<TR><TD ALIGN=RIGHT>OS</TD><TD><TABLE><TR><TD ALIGN=LEFT><select name=platformOS size=1>" + COM.dragonflow.Page.portalServerPage.getOptionsHTML(array, s1) + "</select></TD></TR>" + "<TR><TD><FONT SIZE=-1>OS on the remote SiteView server</FONT></TD></TR>" + "</TABLE></TD><TD><I></I></TD></TR>");
        } else
        {
            outputStream.println("<input type=HIDDEN name=platformOS  value=\"NT\">");
        }
        outputStream.println("<TR><TD ALIGN=RIGHT>Timeout</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=timeout size=10 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_timeout") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional timeout for retrieving information from the SiteView server</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "timeout") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Log Refresh Interval</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=logCollectorRefresh size=10 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_logCollectorRefresh") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>optional interval, in seconds, that the logs should be refreshed from the SiteView server</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "logCollectorRefresh") + "</I></TD></TR>");
        outputStream.println("<TR><TD ALIGN=RIGHT>Time Zone Offset</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=timezoneOffsetFromPortal size=10 value=\"" + COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_timezoneOffsetFromPortal") + "\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>the difference in hours (+ or -) from the location of the remote SiteView relative to the CentraScope machine</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_timezoneOffsetFromPortal") + "</I></TD></TR>");
        if(s.indexOf("SiteSeer") == -1)
        {
            outputStream.println("<TR><TD ALIGN=RIGHT></TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=checkbox name=readOnly size=50 " + (COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_readOnly").length() <= 0 ? "" : "checked") + ">Read Only</TD></TR>" + "<TR><TD><FONT SIZE=-1>only allow CentraScope to read information from this server, and not change or save configurations</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "readOnly") + "</I></TD></TR>");
        } else
        {
            outputStream.println("<input type=HIDDEN name=readOnly  value=\"CHECKED\">");
        }
        outputStream.println("<TR><TD ALIGN=RIGHT></TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=checkbox name=trace size=50 " + (COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_trace").length() <= 0 ? "" : "checked") + ">Trace</TD></TR>" + "<TR><TD><FONT SIZE=-1>Trace requests to this server from CentraScope in the RunMonitor.log log file</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "trace") + "</I></TD></TR>");
        outputStream.println("</TABLE>");
    }

    jgl.HashMap fillInResultFrame(HashMap hashmap)
    {
        hashmap.put("_id", request.getValue("id"));
        hashmap.put("_server", request.getValue("server"));
        hashmap.put("_username", request.getValue("username"));
        hashmap.put("_password", request.getValue("password"));
        hashmap.put("_title", request.getValue("title"));
        hashmap.put("_proxy", request.getValue("proxy"));
        hashmap.put("_proxyusername", request.getValue("proxyusername"));
        hashmap.put("_proxypassword", request.getValue("proxypassword"));
        hashmap.put("_timeout", request.getValue("timeout"));
        hashmap.put("_logCollectorRefresh", request.getValue("logCollectorRefresh"));
        hashmap.put("_timezoneOffsetFromPortal", request.getValue("timezoneOffsetFromPortal"));
        hashmap.put("_loginAccount", request.getValue("loginAccount"));
        hashmap.put("_parent", request.getValue("parent"));
        hashmap.put("_disabled", request.getValue("disabled"));
        hashmap.put("_trace", request.getValue("trace"));
        hashmap.put("_readOnly", request.getValue("readOnly"));
        hashmap.put("_platformOS", request.getValue("platformOS"));
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_platformOS").length() == 0)
        {
            hashmap.put("_platformOS", COM.dragonflow.SiteView.Machine.osToString(COM.dragonflow.SiteView.Platform.getLocalPlatform()));
        }
        java.lang.Long long1 = new Long(COM.dragonflow.Utils.TextUtils.timeSeconds());
        hashmap.put("_lastUpdate", long1.toString());
        return hashmap;
    }

    String getConfigFilePath()
    {
        return COM.dragonflow.SiteView.Portal.PORTAL_SERVERS_CONFIG_PATH;
    }

    void printListHeader()
    {
        outputStream.println("<TH>Name</TH><TH>Monitors<TH>Status<TH>License<TH WIDTH=10%>Update</TH><TH WIDTH=10%>Edit</TH><TH WIDTH=10%>Sync</TH><TH WIDTH=3%>Del</TH>");
    }

    void printListItem(HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        String s1 = COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_title");
        if(COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_disabled").length() > 0)
        {
            s1 = s1 + " (disabled)";
        }
        if(COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_readOnly").length() > 0)
        {
            s1 = s1 + " <B>(read only)</B>";
        }
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&id=" + s + "&account=" + request.getAccount();
        String s3 = "<A href=" + s2 + "&operation=Delete>X</a>";
        String s4 = "<A href=" + s2 + "&operation=Sync>Sync from SiteView</a>";
        String s5 = "<A href=" + s2 + "&operation=Edit>Edit</a>";
        if(COM.dragonflow.Page.portalServerPage.getValue(hashmap, "_server").toLowerCase().startsWith("siteseer"))
        {
            s5 = "<A href=" + s2 + "&operation=EditSiteSeer>Edit</a>";
        }
        String s6 = "<A href=" + s2 + "&operation=Update>Update SiteView</a>";
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_readOnly").length() > 0)
        {
            s6 = "";
        }
        String s7 = "&nbsp;";
        String s8 = "&nbsp;";
        String s9 = "&nbsp;";
        try
        {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)COM.dragonflow.SiteView.Portal.getPortal().getElement(s + COM.dragonflow.SiteView.Portal.PORTAL_ID_SEPARATOR);
            if(portalsiteview != null)
            {
                s8 = portalsiteview.getMonitorCount() + " active, " + portalsiteview.getDisabledMonitorCount() + " disabled";
                s9 = portalsiteview.getProperty(COM.dragonflow.SiteView.PortalSiteView.pStateString);
            }
        }
        catch(java.lang.Exception exception) { }
        outputStream.println("<TR><TD align=left>" + s1 + "</TD>" + "<TD" + ">" + s8 + "</TD>" + "<TD" + ">" + s9 + "</TD>" + "<TD" + ">" + s7 + "</TD>" + "<TD" + ">" + s6 + "</TD>" + "<TD" + ">" + s5 + "</TD>" + "<TD" + ">" + s4 + "</TD>" + "<TD" + ">" + s3 + "</TD>" + "</TR>\n");
    }

    public static String createUniqueID(String s)
    {
        int i = s.indexOf("://");
        if(i != -1)
        {
            s = s.substring(i + 3);
        }
        int j = s.indexOf(":");
        if(j != -1)
        {
            s = s.substring(0, j);
        }
        s = COM.dragonflow.Utils.TextUtils.keepChars(s, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-");
        int k = 1;
        String s1 = s;
        for(java.io.File file = new File(COM.dragonflow.SiteView.Portal.getPortalSiteViewRootPath(s1)); file.exists(); file = new File(COM.dragonflow.SiteView.Portal.getPortalSiteViewRootPath(s1)))
        {
            s1 = s + "." + k;
            k++;
        }

        return s1;
    }

    void setUniqueID(HashMap hashmap, HashMap hashmap1)
    {
        hashmap1.put("_id", COM.dragonflow.Page.portalServerPage.createUniqueID(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_server")));
    }

    void preProcessAdd(HashMap hashmap)
    {
        hashmap.put("_readOnly", "true");
    }

    void postProcessAdd(HashMap hashmap)
    {
        COM.dragonflow.Page.portalServerPage.syncRemoteSiteView(hashmap, outputStream);
    }

    public void printOtherForm(String s)
        throws java.lang.Exception
    {
        if(s.equals("Sync"))
        {
            String s1 = request.getValue("id");
            if(request.isPost())
            {
                try
                {
                    Array array = COM.dragonflow.Properties.FrameFile.readFromFile(getConfigFilePath());
                    HashMap hashmap = COM.dragonflow.Page.portalServerPage.findFrameByID(array, s1);
                    COM.dragonflow.Page.portalServerPage.syncRemoteSiteView(hashmap, outputStream);
                    outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Back to " + getTitle() + "s</A>");
                }
                catch(java.lang.Exception exception)
                {
                    printError("There was a problem reading the server.", exception.toString(), "/SiteView/?account=" + request.getAccount());
                }
            } else
            {
                COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)COM.dragonflow.SiteView.Portal.getSiteViewForServerID(s1);
                printButtonBar(getHelpPage(), "");
                if(portalsiteview != null)
                {
                    outputStream.println("<P><FONT SIZE=+1>Are you sure you want to initialize the CentraScope configurations <B>from</B> the SiteView<B> " + portalsiteview.getProperty(COM.dragonflow.SiteView.PortalSiteView.pTitle) + "</B>?</FONT>" + "<P><B>Note:</B> Under normal usage, this only needs to occur once, when you add the server." + "<p>" + getPagePOST("portalServer", s) + "<INPUT TYPE=HIDDEN NAME=id VALUE=\"" + s1 + "\">" + "<TABLE WIDTH=100% BORDER=0><TR>" + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit VALUE=\"" + s + " " + portalsiteview.getProperty(COM.dragonflow.SiteView.PortalSiteView.pTitle) + "\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=" + "/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Return to Servers</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE></FORM>");
                } else
                {
                    outputStream.println("<HR><H2>Error</H2>Could not read SiteView for ID " + s1);
                }
                printFooter(outputStream);
            }
        } else
        if(s.equals("Update"))
        {
            String s2 = request.getValue("id");
            if(request.isPost())
            {
                try
                {
                    COM.dragonflow.SiteView.PortalSync portalsync = new PortalSync(outputStream);
                    portalsync.sync(s2);
                    outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Back to " + getTitle() + "s</A>");
                }
                catch(java.lang.Exception exception1)
                {
                    printError("There was a problem reading the server.", exception1.toString(), "/SiteView/?account=" + request.getAccount());
                }
            } else
            {
                COM.dragonflow.SiteView.PortalSiteView portalsiteview1 = (COM.dragonflow.SiteView.PortalSiteView)COM.dragonflow.SiteView.Portal.getSiteViewForServerID(s2);
                printButtonBar(getHelpPage(), "");
                if(portalsiteview1 != null)
                {
                    outputStream.println("<P><FONT SIZE=+1>Are you sure you want to force an update the configurations at the SiteView<B> " + portalsiteview1.getProperty(COM.dragonflow.SiteView.PortalSiteView.pTitle) + "</B> from this CentraScope?</FONT>" + "<P><B>Note:</B> Under normal usage, this occurs automatically.  Use this if the connection to the " + "SiteView was down, and you need to update all of your changes to the remote SiteView server." + "<p>" + getPagePOST("portalServer", s) + "<INPUT TYPE=HIDDEN NAME=id VALUE=\"" + s2 + "\">" + "<TABLE WIDTH=100% BORDER=0><TR>" + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit VALUE=\"" + s + " " + portalsiteview1.getProperty(COM.dragonflow.SiteView.PortalSiteView.pTitle) + "\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=" + "/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Return to Servers</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE></FORM>");
                } else
                {
                    outputStream.println("<HR><H2>Error</H2>Could not read SiteView for ID " + s2);
                }
                printFooter(outputStream);
            }
        } else
        {
            super.printOtherForm(s);
        }
    }

    public static void syncRemoteSiteView(HashMap hashmap, java.io.PrintWriter printwriter)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        java.io.File file = new File(COM.dragonflow.SiteView.Portal.getPortalSiteViewRootPath(s));
        if(!file.exists())
        {
            file.mkdir();
        }
        printwriter.println("<H2>Reading configuration from " + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_title") + "</H2>\n");
        COM.dragonflow.Log.LogManager.log("Error", "syncConfiguration(PortalSiteView server): CentraScope not supported!");
        printwriter.println("<P><B>Transfer failed: Not supported in this version</b>");
    }

    void postProcessDelete(HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        java.io.File file = new File(COM.dragonflow.SiteView.Portal.getPortalSiteViewRootPath(s));
        COM.dragonflow.Utils.FileUtils.delete(file);
    }

    public static void main(String args[])
    {
        (new portalServerPage()).handleRequest();
    }
}
