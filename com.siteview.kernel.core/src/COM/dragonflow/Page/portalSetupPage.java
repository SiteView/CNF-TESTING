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

import com.recursionsw.jgl.Array;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI, portalServerPage

public class portalSetupPage extends COM.dragonflow.Page.CGI
{

    public portalSetupPage()
    {
    }

    jgl.Array getDefaultServers(Array array)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("_id", "demo.Dragonflow.com");
        hashmapordered.add("_lastUpdate", "983491357");
        hashmapordered.add("_server", "demo.Dragonflow.com:80");
        hashmapordered.add("_readOnly", "true");
        hashmapordered.add("_title", "Dragonflow SiteView Demo");
        array.add(hashmapordered);
        COM.dragonflow.Properties.HashMapOrdered hashmapordered1 = new HashMapOrdered(true);
        hashmapordered1.add("_id", "siteseer4.Dragonflow.com");
        hashmapordered1.add("_password", "(0x)LGMOOCNJMFMEOCNDNELGNH");
        hashmapordered1.add("_lastUpdate", "983491074");
        hashmapordered1.add("_logCollectorRefresh", "300");
        hashmapordered1.add("_server", "siteseer4.Dragonflow.com");
        hashmapordered1.add("_username", "CentraScope");
        hashmapordered1.add("_readOnly", "true");
        hashmapordered1.add("_loginAccount", "CentraScopeDemo");
        hashmapordered1.add("_title", "SiteSeer Demo");
        array.add(hashmapordered1);
        return array;
    }

    jgl.Array getSiteViewServer(Array array)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("_server", request.getValue("server").trim());
        String s = COM.dragonflow.Page.portalServerPage.createUniqueID(request.getValue("server").trim());
        hashmapordered.add("_id", s);
        hashmapordered.add("_readOnly", "true");
        hashmapordered.add("_title", s);
        hashmapordered.add("_username", request.getValue("username"));
        hashmapordered.add("_password", request.getValue("password"));
        hashmapordered.add("_proxy", request.getValue("proxy"));
        array.add(hashmapordered);
        return array;
    }

    jgl.Array getSiteSeerAccount(Array array)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("_server", request.getValue("siteseerserver").trim());
        String s = COM.dragonflow.Page.portalServerPage.createUniqueID(request.getValue("siteseerserver").trim());
        hashmapordered.add("_id", s);
        hashmapordered.add("_readOnly", "true");
        hashmapordered.add("_title", s);
        hashmapordered.add("_loginAccount", request.getValue("siteseerloginAccount"));
        hashmapordered.add("_username", request.getValue("siteseerusername"));
        hashmapordered.add("_password", request.getValue("siteseerpassword"));
        hashmapordered.add("_proxy", request.getValue("siteseerproxy"));
        hashmapordered.add("_logCollectorRefresh", "300");
        array.add(hashmapordered);
        return array;
    }

    public void printBody()
    {
        if(request.isPost())
        {
            printRefreshHeader("", getPageLink("portal", ""), 7);
            Array array = new Array();
            array.add(new HashMapOrdered(true));
            if(request.getValue("server").length() > 0)
            {
                getSiteViewServer(array);
            }
            if(request.getValue("siteseerloginAccount").length() > 0)
            {
                getSiteSeerAccount(array);
            }
            if(array.size() == 1)
            {
                getDefaultServers(array);
            }
            for(int i = 1; i < array.size(); i++)
            {
                HashMap hashmap = (HashMap)array.get(i);
                COM.dragonflow.Page.portalServerPage.syncRemoteSiteView(hashmap, outputStream);
                outputStream.println("<P>");
            }

            String s = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/portal.config";
            try
            {
                COM.dragonflow.Properties.FrameFile.writeToFile(s, array);
            }
            catch(java.io.IOException ioexception) { }
            COM.dragonflow.SiteView.Portal.signalReload();
            outputStream.println("<p>Will open CentraScope Home View within next 10 seconds...<p>");
            outputStream.flush();
            outputStream.println("</BODY>\n");
        } else
        {
            printBodyHeader("Starting CentraScope the First Time");
            outputStream.println("<CENTER><H2>Starting CentraScope the First Time</H2></CENTER><P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>Congratulations!  You've completed the CentraScope installation and you're ready to start using CentraScope for the first time.<P>CentraScope acts as a portal into a comprehensive monitoring environment using Dragonflow Software's eBusiness monitoring solutions. <A HREF=/SiteView/docs/portal/CentraStart.htm TARGET=\"Help\">CentraScope</A> provides:<UL><LI>real-time viewing of multiple and distributed monitoring servers through a single user interface<LI>aggregated data from the remote servers for centralized reporting<LI>centralized configuration for all remote SiteView servers</UL><P>Below, you may add a particular remote <A HREF=#addSiteView>SiteView server</A> or <A HREF=#addSiteSeer>SiteSeer account</a> to this CentraScope, or you may allow demonstration monitoring servers to be added automatically. When you click the <b>Start</b> or <B>Initialize</B>buttons on this page, there will be a short pause while CentraScope reads the current configurations from the remote monitoring servers you've chosen, and then you'll see the CentraScope Home View.<P>The latest monitoring data is retrieved at regular intervals from the remote monitoring servers and copied to the CentraScope server. One of the key features of CentraScope is the customizable user interface. The CentraScope display is controlled by <A HREF=/SiteView/docs/portal/CentraViews.htm TARGET=\"Help\">view</A> definitions. Each view definition is made up of several layout cells. The content of each cell is determined by markup instructions expressed either as HTML segments or XSL stylesheets.<P>The CentraScope Home View is your entry point to CentraScope. From the Home View, you can view real-time monitoring status, access historical reports, or display CentraScope's context-sensitive on-line <a href=/SiteView/docs/portal/CentraScopeTOC.htm target=\"Help\">help</A>. From the CentraScope <A HREF=/SiteView/docs/portal/CentraPrefs.htm TARGET=\"Help\">Preferences</A> Page you can quickly and easily add new SiteView servers and edit custome views.<P><p><CENTER><input type=hidden name=page value=\"portalSetup\"><input type=hidden name=account value=\"administrator\">\n<input type=hidden name=operation value=\"setup\"><input type=submit value=\"Start\"> using CentraScope</CENTER>");
            outputStream.println("<p><a name=addSiteView><h2>Add SiteView Server</h2></a>If you have an existing SiteView server you would like to use with CentraScope, enter the information below, and click Update, and the configuration files will be transferred to this installation of CentraScope<P>");
            outputStream.println("<TABLE>");
            outputStream.println("<TR><TD ALIGN=RIGHT>SiteView Server Address</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=server size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>the server address and SiteView port of the server to transfer configurations from(for example, demo." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8888)</FONT></TD></TR>" + "</TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Administrator User Name</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=username size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>administrator user name on the SiteView server</FONT></TD></TR></TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Administrator Password</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=password size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>administrator password on the SiteView server</FONT></TD></TR></TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Proxy</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=proxy size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>optional proxy server to use including port number (for example, proxy." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8080)</FONT></TD></TR>" + "</TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("</TABLE><P><CENTER><input type=submit value=\"Initialize CentraScope\"> from remote SiteView</CENTER>");
            outputStream.println("<p><a name=addSiteSeer><h2>Add SiteSeer Account</h2></a>If you have an existing SiteSeer account you would like to use with CentraScope, enter the information below, and click Update, and the SiteSeer configuration will be transferred to this installation of CentraScope<P>");
            outputStream.println("<TABLE>");
            Array array1 = new Array();
            for(int j = 1; j <= 9; j++)
            {
                String s1 = "siteseer" + j + ".Dragonflow.com";
                if(j == 1)
                {
                    s1 = "siteseer.Dragonflow.com";
                }
                array1.add(s1);
                array1.add(s1);
            }

            outputStream.println("<TR><TD ALIGN=RIGHT>Server Address</TD>");
            outputStream.println("<TD><TABLE><TR><TD ALIGN=LEFT>");
            outputStream.println("<select size=1 name=\"siteseerserver\">");
            outputStream.println(COM.dragonflow.Page.portalSetupPage.getOptionsHTML(array1, "siteseer5.Dragonflow.com"));
            outputStream.println("</select></TD></TR>");
            outputStream.println("</TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Account</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=siteseerloginAccount size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>SiteSeer accounts, enter the account name</FONT></TD></TR></TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Account User Name</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=siteseerusername size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>administrator user name on the SiteView server</FONT></TD></TR></TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Account Password</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=siteseerpassword size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>administrator password on the SiteView server</FONT></TD></TR></TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("<TR><TD ALIGN=RIGHT>Proxy</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=siteseerproxy size=50 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>optional proxy server to use including port number (for example, proxy." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8080)</FONT></TD></TR>" + "</TABLE></TD><TD><I></I></TD></TR>");
            outputStream.println("</TABLE><P><CENTER><input type=submit value=\"Initialize CentraScope\"> from SiteSeer account</CENTER>");
            outputStream.println("</FORM>");
            printFooter(outputStream);
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        (new portalSetupPage()).handleRequest();
    }
}
