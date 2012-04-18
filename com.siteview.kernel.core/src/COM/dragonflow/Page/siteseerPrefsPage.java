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

import com.recursionsw.jgl.HashMap;

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class siteseerPrefsPage extends COM.dragonflow.Page.prefsPage
{

    public siteseerPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 0);
    }

    void savePreferences()
    {
        String s = "";
        try
        {
            HashMap hashmap = getMasterConfig();
            hashmap.put("_siteseerUsername", request.getValue("siteseerUsername"));
            hashmap.put("_siteseerPassword", COM.dragonflow.Properties.StringProperty.getPrivate(request, "siteseerPassword", "seerSuff", null, null));
            hashmap.put("_siteseerReadOnlyUsername", request.getValue("siteseerReadOnlyUsername"));
            hashmap.put("_siteseerReadOnlyPassword", COM.dragonflow.Properties.StringProperty.getPrivate(request, "siteseerReadOnlyPassword", "rSeerSuff", null, null));
            hashmap.put("_siteseerAccount", request.getValue("siteseerAccount").trim());
            hashmap.put("_siteseerHidden", request.getValue("siteseerHidden"));
            hashmap.put("_siteseerDisabled", request.getValue("siteseerDisabled"));
            hashmap.put("_siteseerLoginInURL", request.getValue("siteseerLoginInURL"));
            hashmap.put("_siteseerTitle", request.getValue("siteseerTitle"));
            hashmap.put("_siteseerProxy", request.getValue("siteseerProxy"));
            hashmap.put("_siteseerProxyUsername", request.getValue("siteseerProxyUsername"));
            hashmap.put("_siteseerProxyPassword", COM.dragonflow.Properties.StringProperty.getPrivate(request, "siteseerProxyPassword", "pSeerSuff", null, null));
            hashmap.put("_siteseerHost", request.getValue("siteseerHost"));
            if(s.length() == 0)
            {
                saveMasterConfig(hashmap);
                printPreferencesSaved();
            } else
            {
                printErrorPage(s);
            }
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
        }
    }

    void printErrorPage(String s)
    {
        printBodyHeader("SiteSeer Preferences");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe general preferences editing page\n");
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\t");
        outputStream.print("<UL>\n");
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].length() > 0)
            {
                outputStream.print("<LI><B>" + as[i] + "</B>\n");
            }
        }

        outputStream.print("</UL><HR></BODY>\n");
    }

    void printIntro()
    {
        printBodyHeader("Add SiteSeer");
        outputStream.println("<img src=\"/SiteView/htdocs/artwork/SS_add_SSr.gif\" border=\"0\" alt=\"Add SiteSeer Service\"><hr><table border=\"0\" bordercolor=\"blue\" width=\"100%\"><TR><td><h2><FONT COLOR=\"#3366CC\">Adding a SiteSeer Link to SiteView</h2></font>Subscribers to DragonFlow's SiteSeer service can link their SiteSeer Group into the SiteView interface by going to the <a href=/SiteView/cgi/go.exe/SiteView?page=siteseerPrefs&account=" + request.getAccount() + ">SiteSeer Preference page.</a>" + " This will give you a link on the SiteView main page" + " (and the SiteView <a href=/SiteView/docs/Multiview.htm>Multi-view page</a>) to your SiteSeer account." + "<p>SiteSeer gives you a directly compatible view of your web site from both inside and outside your firewall.<p>" + "<TR><td><h2><FONT COLOR=\"#3366CC\">DragonFlow's SiteSeer Service</font></h2>" + "<P>SiteSeer is a real-time web monitoring service offered by DragonFlow." + " Based on SiteView technology, <a href=\"http://www.dragonflow.com/products/siteseer/\">SiteSeer</a>" + " is a self-service solution that lets you verify the availability of your key web " + "services from remote locations across the Internet. Like SiteView, SiteSeer is designed" + " to be easy-to-use: you setup and control the monitoring with the following features and" + " benefits:</p>" + "<UL>" + "<LI>Verify that web pages, downloads, and e-mail are available from <B>outside your firewall</B>.</LI>" + "<li>Optionally check your system availability from multiple <B>worldwide locations</B>.</li>" + "<LI>Uncover <b>connectivity problems</b> that are undetected from inside the firewall.</LI>" + "<LI>Test <B>sequences of web pages</B> to verify that they're working. </li>" + "<LI>Verify web page content for specific text strings or markup, including error messages. </li>" + "<LI>Receive notification of problems via <B>e-mail or pager</B> (pager available in U.S. only). </LI>" + "<LI>Receive daily and weekly <B>availability reports</B>.</LI>" + "</UL>" + "<p>As with SiteView, you control your SiteSeer monitoring and data. By adding a connection to" + " your SiteSeer account to a SiteView server, you get a complete, directly compatible, " + "inside-and-outside view of the 24x7 availability of your web systems. </p>" + "<p>Sign up for a <b>FREE</b> ten day <a href=\"http://www.dragonflow.com/products/siteseer/\">" + "SiteSeer trial</a> today!</p>" + "</td></tr></table>");
        printFooter(outputStream);
    }

    void printForm()
    {
        HashMap hashmap = getMasterConfig();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerUsername");
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerPassword"), "siteseerPassword", "seerSuff", stringbuffer, stringbuffer1);
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerAccount");
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerHost").trim();
        if(s2.length() == 0)
        {
            s2 = "siteseer5.dragonflow.com";
        }
        printBodyHeader("SiteSeer Preferences");
        printButtonBar("SiteSeerPref.htm", "", getSecondNavItems(request));
        printPrefsBar("SiteSeer");
        outputStream.println("<p><CENTER><H2>SiteSeer Preferences</H2></CENTER><p> <p>You can integrate <a href=\"http://www.dragonflow.com/products/siteseer/\"> remote monitoring data</a> into the SiteView display.  SiteView will display the status of your SiteSeer monitors as a group on the Main SiteView view and the Multi-View panel. Complete the form below  to associate a SiteSeer account with this SiteView.<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=account value=administrator> <input type=hidden name=page value=siteseerPrefs> <input type=hidden name=operation value=save><TABLE border=0 width=100%><TR><TD WIDTH=\"225\"></TD><TD></TD><TD WIDTH=\"50\"></TD></TR><TR><TD COLSPAN=3><HR></TD></TR><TR><TD><B><U>Required Settings</U></B></TD></TR><TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Account: </TD><TD><input type=text name=siteseerAccount size=60 value=\"" + s1 + "\">" + "<br>Enter the name of your SiteSeer account - for example, mycompany.com</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Username:</TD>" + "<TD> <input type=text name=siteseerUsername size=60 value=\"" + s + "\">" + "<BR>Enter the username required to login to your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Password:</TD><TD>" + stringbuffer.toString() + " size=60>" + stringbuffer1.toString() + "<BR>Enter the password used to login to your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Host:</TD><TD>" + " <input type=text name=siteseerHost size=60 value=\"" + s2 + "\">" + "<BR>Enter the host name for the SiteSeer service.  For example, <tt>siteseer3.dragonflow.com</tt> or <tt>siteseer.dragonflow.com</tt></TD></TR>" + "<TR><TD></TD><TD><input type=submit value=\"Save Changes\"></TD></TR>" + "<TR><TD COLSPAN=3><HR></TD></TR>");
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerTitle").trim();
        if(s3.length() == 0)
        {
            s3 = "Siteseer";
        }
        String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerProxy");
        String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerProxyUsername");
        StringBuffer stringbuffer2 = new StringBuffer();
        StringBuffer stringbuffer3 = new StringBuffer();
        COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerProxyPassword"), "siteseerProxyPassword", "pSeerSuff", stringbuffer2, stringbuffer3);
        String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerHidden");
        String s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerDisabled");
        String s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerLoginInURL");
        String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerReadOnlyUsername");
        StringBuffer stringbuffer4 = new StringBuffer();
        StringBuffer stringbuffer5 = new StringBuffer();
        COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_siteseerReadOnlyPassword"), "siteseerReadOnlyPassword", "rSeerSuff", stringbuffer4, stringbuffer5);
        outputStream.println("<TR><TD><B><U>Advanced Options</U></B></TD></TR><TR><TD>&nbsp;</TD></TR><TR><TD ALIGN=LEFT VALIGN=TOP>Disabled:</TD><TD> <input type=checkbox value=CHECKED name=siteseerDisabled " + s7 + ">" + "Check this box to hide the SiteSeer group on the main SiteView panel</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Title:</TD><TD> " + "<input type=text name=siteseerTitle size=60 value=\"" + s3 + "\">" + "<BR>Enter a SiteView display title for your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Proxy:</TD><TD>" + " <input type=text name=siteseerProxy size=60 value=\"" + s4 + "\">" + "<BR>Enter proxy server name or address if use of a proxy is required to access your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Proxy Username:</TD>" + "<TD> <input type=text name=siteseerProxyUsername size=60 value=\"" + s5 + "\">" + "<BR>Enter the proxy user name if required to access your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Proxy Password:</TD><TD>" + stringbuffer2.toString() + " size=60>" + stringbuffer3.toString() + "<BR>Enter the proxy password if required to access your SiteSeer account</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>Hide SiteSeer Group:</TD><TD>" + "<input type=checkbox value=CHECKED name=siteseerHidden " + s6 + ">" + "Check this box to hide the SiteSeer group on the main SiteView panel</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>Automatic SiteSeer Login:</TD><TD>" + " <input type=checkbox value=CHECKED name=siteseerLoginInURL " + s8 + ">" + " Check this box to allow automatic login to SiteSeer by embedding the SiteSeer login and password in the URL</TD></TR>" + " <TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Read Only Username: </TD><TD>" + "<input type=text name=siteseerReadOnlyUsername size=60 value=\"" + s9 + "\">" + "<BR>Enter the user name used to login <B>read only</B> to your SiteSeer account" + " - this is used for Automatic SiteSeer Login when logged into SiteView as the \"user\" user.</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SiteSeer Read Only Password: </TD><TD>" + stringbuffer4.toString() + " size=60>" + stringbuffer5.toString() + "<BR>Enter the password used to login <B>read only</B> to your SiteSeer account</TD></TR>" + "<TR><TD></TD><TD><input type=submit value=\"Save Changes\"></TD></TR>" + "<TR><TD COLSPAN=3><HR></TD></TR></TABLE></FORM><br>");
        printFooter(outputStream);
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_preference"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("operation");
        if(request.isPost())
        {
            if(s.equals("save") || s.equals("setup"))
            {
                savePreferences();
            }
        } else
        if(s.equals("add"))
        {
            printIntro();
        } else
        {
            printForm();
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.siteseerPrefsPage siteseerprefspage = new siteseerPrefsPage();
        if(args.length > 0)
        {
            siteseerprefspage.args = args;
        }
        siteseerprefspage.handleRequest();
    }
}
