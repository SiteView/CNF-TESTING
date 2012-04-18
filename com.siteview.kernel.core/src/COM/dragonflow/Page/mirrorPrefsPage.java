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
import COM.dragonflow.Log.FileLogger;
import COM.dragonflow.SiteView.MirrorConfiguration;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class mirrorPrefsPage extends COM.dragonflow.Page.prefsPage
{

    private static boolean debug = false;

    public mirrorPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 0);
    }

    void printPreferencesSaved(String s, String s1, int i)
    {
        outputStream.println("<HEAD>\n<meta HTTP-EQUIV=\"Refresh\" CONTENT=\"" + i + "; URL=" + s + "\">\n" + "</HEAD>\n" + "<BODY BGCOLOR=\"#ffffff\">" + s1 + "</BODY>\n");
    }

    private void savePreferences(boolean flag)
    {
        if(flag)
        {
            COM.dragonflow.SiteView.MirrorConfiguration.createFailoverMG(this);
        }
        COM.dragonflow.SiteView.MirrorConfiguration.setMirrorConfig(this);
        if(flag)
        {
            outputStream.println("<HEAD>\n</HEAD>\n<BODY BGCOLOR=\"#ffffff\"><center><h1>Mirroring Primary SiteView</h1></center>HA SiteView will now start the initial mirroring of the primary machine. \n<p>This may take some time (ie 5 minutes or so). Please be patient.<br>\n");
            outputStream.flush();
            COM.dragonflow.SiteView.MirrorConfiguration mirrorconfiguration = new MirrorConfiguration(null, outputStream);
            try
            {
                COM.dragonflow.Log.LogManager.registerLogger("Mirror", new FileLogger(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "logs" + java.io.File.separator + "mirror.log", 0xf4240));
            }
            catch(java.lang.Exception exception) { }
            if(mirrorconfiguration != null)
            {
                mirrorconfiguration.execute();
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                COM.dragonflow.SiteView.SiteViewGroup _tmp = siteviewgroup;
                COM.dragonflow.SiteView.SiteViewGroup.SignalReload();
            }
            outputStream.println("</BODY>\n");
        }
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 0);
    }

    void printErrorPage(String s)
    {
        outputStream.print("<HEAD><TITLE>Fail-Over Preferences</TITLE>\n" + COM.dragonflow.Page.CGI.nocacheHeader + COM.dragonflow.SiteView.Platform.charSetTag + "</HEAD>\n" + "<BODY BGCOLOR=#FFFFFF>\n");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe Fail-Over Preferences form\n");
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

    void printForm()
        throws java.lang.Exception
    {
        printBodyHeader("Fail-Over Preferences");
        printButtonBar("MirrorPref.htm", "", getSecondNavItems(request));
        printPrefsBar("Fail-Over");
        if(!COM.dragonflow.Utils.LUtils.isHighAvailabilityLicense())
        {
            outputStream.println("<br><br><h2>Add the SiteView Fail-Over Module</h2></center><br><P>Add the optional SiteView Fail-Over service to create a fully redundant solution for monitoring  the availability of your mission-critical applications and servers.<p>The Fail-Over Module is an add-on SiteView module combined with additional DragonFlow services that provide:<UL><LI>Automatic failover from the primary SiteView to a secondary SiteView server if a primary monitoring server failure occurs<LI>Automatic mirroring of the monitoring, alerting, and reporting configurations to the secondary SiteView<LI>Initial setup of Fail-Over configuration from a DragonFlow service engineer<LI>24x7 SiteView technical support<LI>Annual SiteView Fail-Over testing and optimization from a DragonFlow service engineer</ul><p>For more information about adding the SiteView Fail-Over service, contact <A HREF=mailto:siteviewsales@merc-int.com>siteviewsales@merc-int.com</A><p>");
        } else
        if(request.getValue("operation").equals("initSetup"))
        {
            outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=page value=mirrorPrefs>\n" + "<input type=hidden name=operation value=initSetup>\n" + "<input type=hidden name=runmirror value=CHECKED>\n");
            outputStream.print("<p>SiteView can be setup in a Fail-Over configuration with a Primary SiteView server and a Secondary SiteView server.\n<p><HR><CENTER><H3>Mirroring</H3></CENTER><BR>\nEnable automatic mirroring of the primary SiteView configuration \nto this SiteView server (fail-over).  <p>\n<DL>\n<P><DT>Server: <input type=text name=server size=40 value=\"\"><P>\n<DD>The server address and SiteView port of the server to mirror configurations from (for example, demo." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8888)\n" + "<i>NOTE: Do not prefix the server name with http:// or \\\\</i>" + "<P><DT>Admin Login: <input type=text name=username size=40 value=\"\"><P>\n" + "<DD>The administrator username for the primary SiteView installation\n" + "<P><DT>Admin Password: <input type=password name=password size=40 value=\"\"><P>\n" + "<DD>The administrator password for the primary SiteView installation\n" + "<P><DT>Is SSL enabled:<input type=checkbox UNCHECKED name=isSecure ><P>\n" + "<DD>Check this box if the above SiteView server is secured. (ie uses HTTPS)\n");
            outputStream.print("<P><DT>Schedule:   <input type=text name=mirrorScheduleTime size=20 value=\"\"> Hours<P>\n<DD>Enter the time interval for mirroring from the primary SiteView. Value should be (1 to 23 hours). No decimals</DL><p><p><BR>\n<input type=submit value=\"Save\">\n</FORM>\n<br>\n");
        } else
        {
            HashMap hashmap = COM.dragonflow.SiteView.MirrorConfiguration.getMirrorConfig();
            StringBuffer stringbuffer = new StringBuffer();
            StringBuffer stringbuffer1 = new StringBuffer();
            COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Page.mirrorPrefsPage.getValue(hashmap, "password"), "password", "mirrorSuff", stringbuffer, stringbuffer1);
            String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "mirrorSchedule");
            String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\t ");
            String s1 = as.length <= 0 ? "" : as[as.length - 1];
            outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=page value=mirrorPrefs>\n" + "<input type=hidden name=operation value=save>\n");
            outputStream.print("<p>SiteView can be setup in a Fail-Over configuration with a Primary SiteView and a Secondary SiteView.\n<p><HR><CENTER><H3>Mirroring</H3></CENTER><BR>\nEnable automatic mirroring of the primary SiteView configuration \nto this SiteView server (fail-over).  <p>\n<DL>\n<P><DT>Server: <input type=text name=server size=40 value=\"" + COM.dragonflow.Page.mirrorPrefsPage.getValue(hashmap, "server") + "\"><P>\n" + "<DD>The server address and SiteView port of the server to mirror configurations from (for example, demo." + COM.dragonflow.SiteView.Platform.exampleDomain + ":8888)\n" + "<i>NOTE: Do not prefix the server name with http:// or \\\\</i>" + "<P><DT>Is SSL enabled:<input type=checkbox name=isSecure " + COM.dragonflow.Page.mirrorPrefsPage.getValue(hashmap, "isSecure") + "><P>\n" + "<DD>Check this box if the server is a secured.\n" + "<P><DT>Admin Login: <input type=text name=username size=40 value=\"" + COM.dragonflow.Page.mirrorPrefsPage.getValue(hashmap, "username") + "\"><P>\n" + "<DD>The administrator username for the primary SiteView installation\n" + "<P><DT>Admin Password: " + stringbuffer.toString() + " size=40><P>\n" + stringbuffer1.toString() + "<DD>The administrator password for the primary SiteView installation\n" + "<P><DT>Mirror every:   " + "<input type=text name=mirrorScheduleTime size=20 value=\"" + s1 + "\"> Hours<P>\n" + "<DD>Enter the time interval for mirroring from the primary SiteView.  Value should be (1 to 23 hours). No decimals" + "</DL><p>");
            outputStream.print("<input type=submit value=\"Save Changes\">\n</FORM>\n<br>\n");
        }
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
                savePreferences(false);
            } else
            if(s.equals("initSetup"))
            {
                savePreferences(true);
            }
        } else
        {
            printForm();
        }
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.mirrorPrefsPage mirrorprefspage = new mirrorPrefsPage();
        if(args.length > 0)
        {
            mirrorprefspage.args = args;
        }
        mirrorprefspage.handleRequest();
    }

    private void stat(String s)
    {
        if(debug)
        {
            java.lang.System.out.println(s);
        }
    }

    static 
    {
        String s = java.lang.System.getProperty("mirrorPrefsPage.debug", "false");
        if(s.equalsIgnoreCase("true"))
        {
            debug = true;
        }
    }
}
