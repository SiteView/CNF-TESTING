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

public class jdbcConfigPrefsPage extends COM.dragonflow.Page.prefsPage
{

    public jdbcConfigPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 20);
    }

    void savePreferences()
    {
        try
        {
            HashMap hashmap = getMasterConfig();
            hashmap.put("_configJdbcPassword", COM.dragonflow.Properties.StringProperty.getPrivate(request, "configJdbcPassword", "jdbcSuff", null, null));
            hashmap.put("_configJdbcURL", request.getValue("configJdbcURL"));
            hashmap.put("_configJdbcUser", request.getValue("configJdbcUser"));
            hashmap.put("_configJdbcDriver", request.getValue("configJdbcDriver"));
            hashmap.put("_configJdbcCheckFrequency", request.getValue("configJdbcCheckFrequency"));
            hashmap.put("_jdbcConfigdisabled", request.getValue("jdbcConfigdisabled"));
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            saveMasterConfig(hashmap);
            if(request.getValue("initializeDatabaseFromFiles").length() > 0)
            {
                initializeDatabase();
            }
            int i = siteviewgroup.getSettingAsLong("_fileCheckFrequency", 120) * 1000;
            if(siteviewgroup.maintenanceScheduler != null)
            {
                siteviewgroup.maintenanceScheduler.scheduleRepeatedPeriodicAction(siteviewgroup.checkConfiguration, i);
            }
            printPreferencesSaved();
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
        }
    }

    void initializeDatabase()
    {
        COM.dragonflow.Properties.JdbcConfig.inInitStage = true;
        boolean flag = false;
        COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc config, initializing database from configuration files");
        try
        {
            COM.dragonflow.Properties.JdbcConfig.getJdbcConfig();
            COM.dragonflow.Properties.JdbcConfig.syncDBFromFiles();
            COM.dragonflow.Properties.JdbcConfig.inInitStage = false;
            return;
        }
        catch(java.lang.Exception exception)
        {
            if(!flag)
            {
                printErrorPage("Could not sync from db " + exception.getMessage());
            }
        }
    }

    void printErrorPage(String s)
    {
        printBodyHeader("Database Driven Configuration");
        outputStream.println("<p><CENTER><H2>Database Driven Configuration: Error</H2></CENTER>\n<HR><p>There were errors in information or connection.  Use your browser's back button to return\n  to the Configuration Database Preferences form. <hr>\n");
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
        COM.dragonflow.Properties.StringProperty.getPrivate(COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcPassword"), "configJdbcPassword", "jdbcSuff", stringbuffer, stringbuffer1);
        printBodyHeader("Database Driven Configuration");
        printButtonBar("DBDrivenConfigs.htm", "", getSecondNavItems(request));
        printPrefsBar("DDC");
        String s = "";
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_configJdbcURL").length() == 0)
        {
            s = "CHECKED";
        }
        outputStream.print(getPagePOST("jdbcConfigPrefs", "save") + "<HR><CENTER><H2>Database Driven Configuration</H2></CENTER><BR>\n" + "SiteView can optionally read monitor settings and configurations " + " from a database, and save configurations" + " to a database. Any database which supports the ODBC or JDBC standards can be used, including Microsoft SQL Server and Oracle.\n" + "<p>For additional information on this feature go to our Customer Support <a href=\"http://support.dragonflow.com/\">Knowledge Base</a> and   " + " search for Storing monitor configurations in a database. " + "<p><b>(note:</b> changes to Configuration Database preferences do not take effect until the next time SiteView is restarted)\n" + "<hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Connection URL:</TD>" + "<TD VALIGN=TOP><input type=text name=configJdbcURL size=40 value=\"" + COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcURL") + "\">\n" + "</TD><TD VALIGN=TOP>" + "Enter the URL to the database connection</TD></TR>" + "<tr><td>&nbsp;</td><td spancol=2>If you're using an ODBC connection: use - <b><i>jdbc:odbc:myDSNName</i></b><br>" + "If you're using a SQL connection: use - <b><i>jdbc:inetdae:myserver.mycompany.com:1433?database=master</i></b></TD></TR>" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Driver:</TD>" + "<TD VALIGN=TOP><input type=text name=configJdbcDriver size=40 value=\"" + COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcDriver") + "\">\n" + "</TD><TD VALIGN=TOP>" + "Driver used to connect to the database</TD></TR>" + "<tr><td>&nbsp;</td><td spancol=2>If you're using an ODBC connection: use - <b><i>sun.jdbc.odbc.JdbcOdbcDriver</i></b><br>" + "If you're using a SQL connection: use - <b><i>com.inet.tds.TdsDriver</i></b><br>" + "For all other DBs, go to their web site and install their JDBC thin driver.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Username:</TD>" + "<TD VALIGN=TOP><input type=text name=configJdbcUser size=40 value=\"" + COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcUser") + "\">\n" + "</TD><TD VALIGN=TOP>" + "Enter the username used to connect to the database.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Database Password:</TD>" + "<TD VALIGN=TOP>" + stringbuffer.toString() + " size=40>\n" + stringbuffer1.toString() + "</TD><TD VALIGN=TOP>" + "Enter the password used to connect to the database.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>" + "</table>\n" + "<p><table><tr><td><input type=submit name=submit value=\"Save Changes\"></td>\n");
        if(COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcURL").length() > 0 && COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcDriver").length() > 0)
        {
            outputStream.print("<td><input type=submit name=submit value=\"Remove DataBase Connection\"></td>");
            outputStream.print("<td><input type=submit name=submit value=\"Sync SiteView From DB\"></td>");
            if(COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcSyncDBFromFiles").length() > 0)
            {
                outputStream.print("<td><input type=submit name=submit value=\"Sync DB From SiteView\"></td>");
            }
        }
        outputStream.print("</tr></table>");
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_jdbcConfigdisabled");
        String s2 = "";
        if(s1.length() > 0)
        {
            s2 = "CHECKED";
        }
        outputStream.print("<p><HR><CENTER><H3>Advanced Options</H3></CENTER><DL><P><DT><input type=checkbox name=jdbcConfigdisabled " + s2 + ">Disable syncing configuration files from database.<P>\n" + "<P><DT><input type=checkbox name=initializeDatabaseFromFiles " + s + ">Initialize database from current configuration files<P>\n" + "<DD>If you would like the database initialized with the current contents of the configuration files, check this box.\n" + "<B>Warning: checking this box will delete the current configurations stored in the database for this server - we advise using this feature\n" + "only when initially setting up database configuration.</B>" + "</DL>" + "<P><DT>Configuration Update Frequency: <input type=text name=configJdbcCheckFrequency size=40 value=\"" + COM.dragonflow.Page.jdbcConfigPrefsPage.getValue(hashmap, "_configJdbcCheckFrequency") + "\"><P>\n" + "<DD>Enter the configuration update frequnce (seconds).\n" + "</FORM>\n" + "<br>\n" + "<CENTER><B>Version " + COM.dragonflow.SiteView.Platform.getVersion() + "</B><p>\n");
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
                if(request.getValue("submit").startsWith("Save"))
                {
                    if(COM.dragonflow.Properties.JdbcConfig.getJdbcConfig().testDBConnection(request.getValue("configJdbcURL"), request.getValue("configJdbcDriver"), request.getValue("configJdbcUser"), COM.dragonflow.Properties.StringProperty.getPrivate(request, "configJdbcPassword", "jdbcSuff", null, null)))
                    {
                        COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().StartDDC();
                    } else
                    {
                        COM.dragonflow.Page.jdbcConfigPrefsPage.removeDBConnection(request);
                        COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().StopDDC();
                    }
                } else
                if(request.getValue("submit").startsWith("Remove"))
                {
                    COM.dragonflow.Page.jdbcConfigPrefsPage.removeDBConnection(request);
                    COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().StopDDC();
                } else
                if(request.getValue("submit").startsWith("Sync SiteView"))
                {
                    COM.dragonflow.Properties.JdbcConfig.getJdbcConfig().execute();
                } else
                {
                    COM.dragonflow.Properties.JdbcConfig.syncDBFromSS();
                }
                printRefreshPage("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 0);
            }
        } else
        {
            printForm();
        }
    }

    public static void removeDBConnection(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        HashMap hashmap = COM.dragonflow.Page.CGI.loadMasterConfig(httprequest);
        hashmap.put("_configJdbcPassword", "");
        hashmap.put("_configJdbcURL", "");
        hashmap.put("_configJdbcUser", "");
        hashmap.put("_configJdbcDriver", "com.inet.tds.TdsDriver");
        try
        {
            COM.dragonflow.Page.CGI.saveMasterConfig(hashmap, httprequest);
        }
        catch(java.lang.Exception exception) { }
        COM.dragonflow.Properties.JdbcConfig.getJdbcConfig().close();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.jdbcConfigPrefsPage jdbcconfigprefspage = new jdbcConfigPrefsPage();
        if(args.length > 0)
        {
            jdbcconfigprefspage.args = args;
        }
        jdbcconfigprefspage.handleRequest();
    }
}
