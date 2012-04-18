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

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class logPrefsPage extends COM.dragonflow.Page.prefsPage
{

    public logPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        if(COM.dragonflow.SiteView.Platform.isPortal())
        {
            printPreferencesSaved("/SiteView/?account=" + request.getAccount(), "", 0);
        } else
        {
            printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 0);
        }
    }

    void savePreferences()
    {
        long l = COM.dragonflow.Utils.TextUtils.toLong(request.getValue("dailyLogTotalLimit"));
        long l1 = COM.dragonflow.Utils.TextUtils.toLong(request.getValue("dailyLogKeepDays"));
        if(l1 == 0L && request.getValue("dailyLogKeepDays").length() > 0)
        {
            String s = "Invalid value entered for: Daily Logs To Keep = " + request.getValue("dailyLogKeepDays");
            printErrorPage(s);
            return;
        }
        if(l > 0L && l < 24000L)
        {
            String s1 = "Maximum Size of Logs entered ( " + l + " ) is too small. Log file size limits less than 1000000 bytes (1 Mb) restrict data available for " + " SiteView reports and troubleshooting. " + " We recommend that you leave this value blank to have SiteView ignore log file size. " + " (If you enter a value for this option, the minimum value is 24000 bytes.) ";
            printErrorPage(s1);
            return;
        }
        if(l == 0L && request.getValue("dailyLogTotalLimit").length() > 0)
        {
            String s2 = "Invalid value entered for: Maximum Size of Logs = " + request.getValue("dailyLogTotalLimit");
            printErrorPage(s2);
            return;
        }
        try
        {
            HashMap hashmap = getMasterConfig();
            hashmap.put("_dailyLogKeepDays", request.getValue("dailyLogKeepDays"));
            hashmap.put("_dailyLogTotalLimit", request.getValue("dailyLogTotalLimit"));
            hashmap.put("_logJdbcPasswordSiteViewLog", COM.dragonflow.Properties.StringProperty.getPrivate(request, "logJdbcPasswordSiteViewLog", "logSuff", null, null));
            hashmap.put("_logJdbcURLSiteViewLog", request.getValue("logJdbcURLSiteViewLog"));
            hashmap.put("_logJdbcURLBackupSiteViewLog", request.getValue("logJdbcURLBackupSiteViewLog"));
            hashmap.put("_logJdbcUserSiteViewLog", request.getValue("logJdbcUserSiteViewLog"));
            hashmap.put("_logJdbcDriverSiteViewLog", request.getValue("logJdbcDriverSiteViewLog"));
            saveMasterConfig(hashmap);
            printPreferencesSaved();
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
        }
    }

    void printErrorPage(String s)
    {
        printBodyHeader("Log Preferences");
        outputStream.println("<p><CENTER><H2>Log Preferences</H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe Log Preferences form\n");
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
    {
        HashMap hashmap = getMasterConfig();
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_logJdbcPasswordSiteViewLog"), "logJdbcPasswordSiteViewLog", "logSuff", stringbuffer, stringbuffer1);
        printBodyHeader("Log Preferences");
        printButtonBar("LogPref.htm", "", getSecondNavItems(request));
        printPrefsBar("Log");
        outputStream.print(getPagePOST("logPrefs", "save") + "<CENTER><H2>SiteView Log File Preferences</H2></CENTER><BR>\n" + "<p>Sustained operations monitoring of web systems can generate a large amount" + " of data. SiteView  stores this data in log files on the SiteView machine. " + " The following settings allow you to limit how much data is saved in SiteView " + " log files.</p>");
        outputStream.print(" <hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> Daily Logs To Keep:</TD><TD VALIGN=TOP> <input type=text name=dailyLogKeepDays size=10 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_dailyLogKeepDays") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the number of days of monitoring data to keep. " + " Once a day, SiteView deletes any logs older than the specified number of days.</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Maximum Size of Logs: </TD><TD VALIGN=TOP> " + "<input type=text name=dailyLogTotalLimit size=10 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_dailyLogTotalLimit") + "\"><P>\n" + "</TD><TD VALIGN=TOP>Optional, enter the maximum size, in bytes, allowed for all " + " monitoring logs. Once a day, SiteView checks the total size of all monitoring logs " + " and removes any old logs that are over the maximum size. If blank, the size is not checked." + "</TD></TR> </table>");
        outputStream.print("<HR><CENTER><H2>Database Logging</H2></CENTER>\n<p>SiteView can optionally log a copy of all the monitoring data into a database. Any database which supports the JDBC standards can be used, including Microsoft SQL Server and Oracle.\n<br><b>(Note:</b> changes to Database Logging preferences do not take effect until SiteView is restarted)\n<hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Connection URL: </TD><TD VALIGN=TOP><input type=text name=logJdbcURLSiteViewLog size=40 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_logJdbcURLSiteViewLog") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the URL to the database connection (for example, if the ODBC connection is called SiteViewLog, the URL would be jdbc:odbc:SiteViewLog)</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Driver:" + "</TD><TD VALIGN=TOP> <input type=text name=logJdbcDriverSiteViewLog size=40 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_logJdbcDriverSiteViewLog") + "\">\n" + "</TD><TD VALIGN=TOP>Driver used to connect to the database</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Username: " + "</TD><TD VALIGN=TOP><input type=text name=logJdbcUserSiteViewLog size=40 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_logJdbcUserSiteViewLog") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the username used to connect to the database" + "(applies to primary and backup databases).</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Password:" + "</TD><TD VALIGN=TOP> " + stringbuffer.toString() + " size=40>\n" + stringbuffer1.toString() + "</TD><TD VALIGN=TOP>Enter the password used to connect to the database " + "(applies to primary and backup databases).</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Backup Database Connection URL: " + "</TD><TD VALIGN=TOP> <input type=text name=logJdbcURLBackupSiteViewLog size=40 value=\"" + COM.dragonflow.Page.logPrefsPage.getValue(hashmap, "_logJdbcURLBackupSiteViewLog") + "\">\n" + "</TD><TD VALIGN=TOP> Optional, enter the database URL to use if the main database connection is not available</TD></TR>\n" + "</TABLE><hr><BR>\n" + "<input type=submit value=\"Save Changes\">\n" + "</FORM>\n");
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
        {
            printForm();
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.logPrefsPage logprefspage = new logPrefsPage();
        if(args.length > 0)
        {
            logprefspage.args = args;
        }
        logprefspage.handleRequest();
    }
}
