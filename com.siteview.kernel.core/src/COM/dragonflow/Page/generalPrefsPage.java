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

import java.net.ServerSocket;

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class generalPrefsPage extends COM.dragonflow.Page.prefsPage
{

    private static final String DETACH_MA_OP = "Detach";

    public generalPrefsPage()
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

    boolean savePreferences()
    {
        boolean flag = false;
        String s = "";
        try
        {
            HashMap hashmap = getMasterConfig();
            String s1 = request.getValue("license");
            String s2 = request.getValue("licenseForX");
            if(s1.indexOf("PE") >= 0 && s2.length() > 0)
            {
                s = s + "\tNo optional monitors are available with the Personal Edition license.";
                hashmap = getMasterConfig();
                hashmap.put("_licenseForX", "");
                saveMasterConfig(hashmap);
            }
            String s3 = COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_httpPort");
            String s4 = request.getValue("httpPort").trim();
            if(!s3.equals(s4) && s4.length() > 0)
            {
                int i = COM.dragonflow.Properties.StringProperty.toInteger(s4);
                if(i == 0)
                {
                    s = s + "\tHTTP Port must be a number greater than 0";
                } else
                if(i != -1)
                {
                    try
                    {
                        java.net.ServerSocket serversocket = new ServerSocket(i);
                        serversocket.close();
                    }
                    catch(java.io.IOException ioexception1)
                    {
                        s = s + "\tPort " + i + " could not be opened: " + ioexception1.getMessage();
                    }
                }
            }
            String s5 = request.getValue("webserverAddress");
            String s6 = request.getValue("authorizedIP");
            int j = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("groupsPerRow"));
            int k = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("backups2Keep"));
            if(s6.length() > 0 && !COM.dragonflow.Utils.TextUtils.onlyChars(s6, "0123456789,.*"))
            {
                s = s + "\tThe IP Address allowed access contained illegal characters";
            }
            if(s6.length() == 0 && request.getValue("checkAddressAndLogin").length() > 0)
            {
                s = s + "\tTo enable <B>Require both IP address and Login</B> you must fill in authorized IP address(es)";
            }
            if(j < 1 || j > 20)
            {
                s = s + "\tNumber of gauges per row is out of range (" + j + "), adjusting to (1..20)";
                j = java.lang.Math.max(1, java.lang.Math.min(20, j));
            }
            if(k < 1 || k > 80)
            {
                s = s + "\tNumber of backups per file is out of range (" + k + "), resetting to 1";
                k = 1;
            }
            hashmap.put("_httpPort", s4);
            hashmap.put("_webserverAddress", s5);
            hashmap.put("_authorizedIP", s6);
            hashmap.put("_checkAddressAndLogin", request.getValue("checkAddressAndLogin"));
            hashmap.put("_createStaticHTML", request.getValue("createStaticHTML"));
            hashmap.put("_localeEnabled", request.getValue("localeEnabled"));
            hashmap.put("_acknowledgeMonitors", request.getValue("acknowledgeMonitors"));
            hashmap.put("_alertIconLink", request.getValue("alertIconLink"));
            hashmap.put("_reportIconLink", request.getValue("reportIconLink"));
            hashmap.put("_displayGauges", request.getValue("displayGauges"));
            hashmap.put("_isI18N", request.getValue("isI18N"));
            COM.dragonflow.Utils.I18N.isI18N = request.getValue("isI18N").length() > 0;
            hashmap.put("_mainGaugesPerRow", java.lang.Integer.toString(j));
            hashmap.put("_backups2Keep", java.lang.Integer.toString(k));
            hashmap.put("_suspendMonitors", request.getValue("suspendMonitors"));
            hashmap.put("_defaultAuthUsername", request.getValue("defaultAuthUsername"));
            hashmap.put("_defaultAuthPassword", request.getValue("defaultAuthPassword"));
            hashmap.put("_defaultAuthWhenToAuthenticate", request.getValue("defaultAuthWhenToAuthenticate"));
            String s7 = request.getValue("license").trim();
            if(!COM.dragonflow.Utils.LUtils.setLicenseKey("_license", s7, hashmap))
            {
                s = s + COM.dragonflow.Utils.LUtils.getLicenseErrorString(s7);
            }
            if(!COM.dragonflow.Utils.LUtils.getMonitorTypeString(s7).equals("unknown"))
            {
                s = s + "\n License entered not for standard SiteView, type = " + COM.dragonflow.Utils.LUtils.getMonitorTypeString(s7) + "\n ";
            }
            String s8 = request.getValue("licenseForX");
            String s9 = "";
            if(s8.length() > 0)
            {
                String as[] = COM.dragonflow.Utils.TextUtils.split(s8, ",");
                for(int l = 0; l < as.length; l++)
                {
                    if(!COM.dragonflow.Utils.LUtils.setLicenseKey("_licenseForX", as[l].trim(), hashmap))
                    {
                        s = s + COM.dragonflow.Utils.LUtils.getLicenseErrorString(as[l]);
                        continue;
                    }
                    if(COM.dragonflow.Utils.LUtils.getMonitorTypeString(as[l].trim()).equals("unknown"))
                    {
                        s = s + "\n Invalid license key for optional monitoring, type=" + COM.dragonflow.Utils.LUtils.getMonitorTypeString(s7) + "\n ";
                        continue;
                    }
                    if(l > 0)
                    {
                        s9 = s9 + "," + as[l];
                    } else
                    {
                        s9 = s9 + as[l];
                    }
                }

            }
            hashmap.put("_licenseForX", s9);
            hashmap = SiteViewMain.UpdateConfig.manageConverterLink(hashmap);
            if(s.length() == 0)
            {
                saveMasterConfig(hashmap);
                COM.dragonflow.Properties.FrameFile.removeOlderBackFiles(k);
                printPreferencesSaved();
                flag = true;
            } else
            {
                printErrorPage(s);
                flag = false;
            }
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
            flag = false;
        }
        return flag;
    }

    void printErrorPage(String s)
    {
        COM.dragonflow.Page.generalPrefsPage.printRefreshHeader(outputStream, "General Preferences", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 10);
        outputStream.println("<p><CENTER><H2>Errors Detected</H2></CENTER>\n<HR>There were errors with the information you entered:");
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\t");
        outputStream.print("<UL>");
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].length() > 0)
            {
                outputStream.print("<LI><B>" + as[i] + "</B>\n");
            }
        }

        outputStream.print("</UL><HR><p>Use your browser's back button to return\n the SiteView <b>General Preferences</b> form  or click the  <a href=\"/SiteView/cgi/go.exe/SiteView?page=generalPrefs&account=" + request.getAccount() + "\">back</a> link." + " <HR></BODY>\n");
    }

    boolean checkLicenseChange(String s)
    {
        boolean flag = false;
        String s1 = request.getValue("licenseForX");
        if(s.length() != s1.length())
        {
            flag = true;
        } else
        if(s1.length() > 0 && s.length() > 0)
        {
            String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
            int i = 0;
            for(int j = 0; j < as.length; j++)
            {
                for(int k = 0; k < as1.length; k++)
                {
                    if(as1[k].trim().equals(as[j].trim()))
                    {
                        i++;
                    }
                }

            }

            if(i != as1.length)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        }
        return flag;
    }

    void confirmRestart()
    {
        printBodyHeader("Confirm SiteView Restart");
        String s = "<input type=\"hidden\" name=license size=30 value=\"" + request.getValue("license") + "\">" + "<input type=\"hidden\" name=licenseForX size=30 value=\"" + request.getValue("licenseForX") + "\">" + "<input type=\"hidden\" name=localeEnabled value=\"" + request.getValue("localeEnabled") + "\">" + "<input type=\"hidden\" name=isI18N value=\"" + request.getValue("isI18N") + "\">" + "<input type=\"hidden\" name=acknowledgeMonitors value=\"" + request.getValue("acknowledgeMonitors") + "\">" + "<input type=\"hidden\" name=groupsPerRow size=5 value=\"" + request.getValue("groupsPerRow") + "\">" + "<input type=\"hidden\" name=backups2Keep size=5 value=\"" + request.getValue("backups2Keep") + "\">" + "<input type=\"hidden\" name=displayGauges value=\"" + request.getValue("displayGauges") + "\">" + "<input type=\"hidden\" name=authorizedIP size=30 value=\"" + request.getValue("authorizedIP") + "\">" + "<input type=\"hidden\" name=checkAddressAndLogin value=\"" + request.getValue("checkAddressAndLogin") + "\">" + "<input type=\"hidden\" name=httpPort size=10 value=\"" + request.getValue("httpPort") + "\">" + "<input type=\"hidden\" name=createStaticHTML value=\"" + request.getValue("createStaticHTML") + "\">" + "<input type=\"hidden\" name=webserverAddress size=40 value=\"" + request.getValue("webserverAddress") + "\">" + "<input type=\"hidden\" name=suspendMonitors value=\"" + request.getValue("suspendMonitors") + "\">" + "<input type=\"hidden\" name=defaultAuthUsername value=\"" + request.getValue("defaultAuthUsername") + "\">" + "<input type=\"hidden\" name=defaultAuthPassword value=\"" + request.getValue("defaultAuthPassword") + "\">" + "<input type=\"hidden\" name=defaultAuthWhenRequested value=\"" + request.getValue("defaultAuthWhenRequested") + "\">";
        outputStream.print("<h2>Update SiteView Licensing</h2><p>In order to update your SiteView licensing, it will be necessary to restart the SiteView service. Click <b>Restart Now</b> to restart the service now. Click <b>Do Not Restart</b> to save your changes but not restart SiteView.</p> <p><b>NOTE: LICENSING CHANGES WILL NOT BECOME EFFECTIVE UNTIL AFTER SITEVIEW  HAS BEEN RESTARTED</b></p><hr> <table border=\"0\" width=\"90%\"><tr>");
        outputStream.print("<td>" + getPagePOST("generalPrefs", "restart") + s + "<p>\n" + "<input type=submit value=\"Restart Now\">\n" + "</FORM>\n</td>");
        getPagePOST("generalPrefs", "saveonly");
        outputStream.print("<td>" + getPagePOST("generalPrefs", "saveonly") + s + "<p>\n" + "<input type=submit value=\"Do Not Restart\">\n" + "</FORM>\n</td>");
        outputStream.print("</tr></table><hr>");
        printFooter(outputStream);
    }

    void printForm(HashMap hashmap)
    {
        printBodyHeader("General Preferences");
        printButtonBar("GenPref.htm", "", getSecondNavItems(request));
        printPrefsBar("General");
        outputStream.println("<p><CENTER><H2>General Preferences</H2></CENTER><p>\n" + getPagePOST("generalPrefs", "save"));
        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";
        if(!COM.dragonflow.SiteView.Platform.isDemo())
        {
            s = COM.dragonflow.Utils.LUtils.getLicenseKey(hashmap);
            s1 = COM.dragonflow.Utils.LUtils.getLicenseForXKey(hashmap);
            if(s1.length() > 0)
            {
                s2 = COM.dragonflow.Utils.LUtils.getLicenseSummary(s, true, COM.dragonflow.SiteView.Platform.productName, true);
                String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
                for(int j = 0; j < as.length; j++)
                {
                    s3 = s3 + COM.dragonflow.Utils.LUtils.getLicenseSummary(as[j].trim(), true, COM.dragonflow.SiteView.Platform.productName, true) + ",  ";
                    s2 = s2 + COM.dragonflow.Utils.LUtils.getLicenseSummary(as[j].trim(), true, COM.dragonflow.SiteView.Platform.productName, true) + ",  ";
                }

            } else
            {
                s2 = COM.dragonflow.Utils.LUtils.getLicenseSummary(s, true, COM.dragonflow.SiteView.Platform.productName, false);
                s3 = "";
            }
        }
        outputStream.println("<!--LICENSE " + s + " LICENSE-->\n" + "<!--LICENSESUMMARY " + s2 + " LICENSESUMMARY-->\n" + "<!--LICENSExSUMMARY " + s3 + " LICENSExSUMMARY-->\n" + "<TABLE border=0 width=100% cellspacing=0 cellpadding=5>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>License Number:</TD><TD VALIGN=TOP> " + " <input type=text name=license size=30 value=\"" + s + "\">\n" + "</TD><TD VALIGN=TOP><A NAME=license>Enter</A> the license number for this copy of SiteView. " + "  The license number is given to you when you purchase a license and is required after the " + 10 + "  day evaluation period.</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>License Status:</TD>" + "<TD VALIGN=TOP colspan=2>" + s2 + "</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Option Licenses:</TD><TD VALIGN=TOP> " + " <input type=text name=licenseForX size=30 value=\"" + s1 + "\">\n" + "</TD><TD VALIGN=TOP><A NAME=license>Enter</A> the license or licenses for SiteView optional monitors. " + " Separate multiple licenses with a \",\" (comma).</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Options Enabled:</TD>" + "<TD VALIGN=TOP colspan=2>" + s3 + "</TD></TR>" + "<tr><td colspan=3><hr></td></tr>\n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Locale-specific Date and Time:</TD> <TD VALIGN=TOP><input type=checkbox value=CHECKED name=localeEnabled " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_localeEnabled") + ">" + " </TD><TD VALIGN=TOP>Enables the display of dates and times" + " in a locale-specific manner. The default is the United States format.</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%></TD><TD VALIGN=TOP colspan=2>" + "See the General Preferences Help page for more information about over-riding" + "the default time and date locale used by " + COM.dragonflow.SiteView.Platform.productName + ".</TD></TR>" + "<tr><td colspan=3><hr></td></tr> \n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>International Version</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=isI18N " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_isI18N") + ">" + "</TD><TD VALIGN=TOP>Enables SiteView to work with multiple character sets. " + "When checked, all character encodings will be honored (for example, foreign language web pages). " + " When unchecked, only the local system encoding is honored." + "</TD></TR><tr><td colspan=3><hr></td></tr>\n");
        int i = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_backups2Keep"));
        if(i < 1 || i > 80)
        {
            i = 1;
        }
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Number of backups per file: </TD><TD VALIGN=TOP> <input type=text name=backups2Keep size=5 value=\"" + i + "\">\n" + "</TD><TD VALIGN=TOP>Enter the number of backups per file that you would like to keep. " + "SiteView will name them with a .bak.1, .bak.2,...bak.#, where 1 is the lastest backup file.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Operator Acknowledgement</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=acknowledgeMonitors " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_acknowledgeMonitors") + ">" + "</TD><TD VALIGN=TOP>Enables Operator Acknowledgement Functionality. " + " Allows users to click on a monitor status icon and enter an acknowledgement" + " text and time.</TD></TR>\n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Alert Icons</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=alertIconLink " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_alertIconLink") + ">" + "</TD><TD VALIGN=TOP>Enables display of alert information for " + " groups and monitors.</TD></TR>\n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Report Icons</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=reportIconLink " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_reportIconLink") + ">" + "</TD><TD VALIGN=TOP>Enables display of report information for " + " groups and monitors.</TD></TR>\n");
        int k = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mainGaugesPerRow"));
        if(k < 1 || k > 20)
        {
            k = 3;
        }
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Groups per Row: </TD><TD VALIGN=TOP> <input type=text name=groupsPerRow size=5 value=\"" + k + "\">\n" + "</TD><TD VALIGN=TOP>Enter the number of group names to display in " + "each row on the main SiteView Screen.</TD></TR>");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Blue Gauges</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=displayGauges " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_displayGauges") + ">" + "</TD><TD VALIGN=TOP>Enable the display blue gauges for monitors and groups." + " </TD></TR>" + "<tr><td colspan=3><hr></td></tr>\n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Suspend Monitors</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=suspendMonitors " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_suspendMonitors") + ">" + "</TD><TD VALIGN=TOP>Suspend all monitors. " + "When checked, all monitors will be suspended. " + "When unchecked, all monitors will start running according to their previous configuration. " + "<tr><td colspan=3><hr></td></tr>\n");
        String s4 = COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_defaultAuthWhenToAuthenticate").equals(COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[2]) ? "SELECTED " : "";
        String s5 = COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_defaultAuthWhenToAuthenticate").equals(COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[4]) ? "SELECTED " : "";
        if(COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_defaultAuthWhenToAuthenticate").length() == 0)
        {
            s4 = "SELECTED ";
        }
        String s6 = "<select name=defaultAuthWhenToAuthenticate size=1><option " + s4 + "value=" + COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[2] + ">" + COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[3] + "<option " + s5 + "value=" + COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[4] + ">" + COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[5] + "</select>\n";
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP COLSPAN=3><b><u>Default Authentication Credentials</u></b> <a href=/SiteView/docs/GenPref.htm#authenticate TARGET=Help>(see the User's Guide for more information)</a></TD><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Username:</TD><TD VALIGN=TOP>  <input type=text name=defaultAuthUsername size=30 value=\"" + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_defaultAuthUsername") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the default username to be used for authentication with remote systems.  Both \"username\" and \"DOMAIN\\username\" are valid formats." + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Password:</TD><TD VALIGN=TOP> " + " <input type=password name=defaultAuthPassword size=30 value=\"" + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_defaultAuthPassword") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the default password to be used for authentication with remote systems." + "</TD></TR>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>When to Authenticate:</TD><TD VALIGN=TOP> " + s6 + "\n" + "</TD><TD VALIGN=TOP>When are the user/password to be sent with URL requests? " + "              <font size=-1>See the <a href=\"/SiteView/docs/URLMon.htm#authorization\" TARGET=Help>" + "              URL Monitor documentation</a></b> for important information.</font>" + "              <p></p>" + "</TD></TR>" + "</TABLE>\n");
        outputStream.print("<hr><br>\n<input type=submit value=\"Save Changes\">\n<HR><CENTER><H2>SiteView Security and Web Server</H2></CENTER>\n<p>" + COM.dragonflow.SiteView.Platform.productName + " contains a built-in web server to serve its pages." + " The options below control the port and the access requirements to the\n" + COM.dragonflow.SiteView.Platform.productName + " Web Server.  " + COM.dragonflow.SiteView.Platform.productName + " pages may " + " also be served through a standalone, third party web server.</P><hr>\n");
        outputStream.print("<TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>IP Addresses Allowed:</TD><TD VALIGN=TOP>  <input type=text name=authorizedIP size=30 value=\"" + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_authorizedIP") + "\"><P>\n" + "</TD><TD VALIGN=TOP>Enter the IP address that is always allowed access to SiteView. </TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%></TD><TD VALIGN=TOP colspan=2>" + " Use a trailing wildcard character to specify a range of IP addresses. " + " For example, 206.168.191.* allows access to the 206.168.191 subnet. " + " To allow multiple IP addresses, separate them by commas.  When using this option, you also need to check " + " the <b>Require IP address and Login</b> option below to.</TD></TR>");
        outputStream.print("<tr><td colspan=3><hr></td></tr><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Require IP address and Login</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=checkAddressAndLogin " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_checkAddressAndLogin") + ">" + "</TD><TD VALIGN=TOP>Check this option to limit SiteView access to connections " + " that have both an allowed IP address and a correct username and password.</TD></TR>\n" + " <TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%></TD><TD VALIGN=TOP colspan=2>" + " When this option is turned off, access is allowed to anyone who has either" + " an allowed IP address (as specified above) or a valid User Profile " + " (with <a href=\"/SiteView/docs/UserPref.htm\" target=help>username and password</a>).</TD></TR><tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>" + COM.dragonflow.SiteView.Platform.productName + "  Port: </TD>" + "<TD VALIGN=TOP> <input type=text name=httpPort size=10 value=\"" + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_httpPort") + "\">" + " <br>(current port: " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_httpActivePort") + ")<P>\n" + "</TD><TD VALIGN=TOP>Enter the port number for the " + COM.dragonflow.SiteView.Platform.productName + " built-in web server.</TD></TR>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%></TD><TD VALIGN=TOP colspan=2>" + " <b>Note:</b> Leaving this field blank will prevent " + COM.dragonflow.SiteView.Platform.productName + " from serving web pages.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>");
        outputStream.print("<TR><TD VALIGN=TOP colspan=3><b><u>Serving SiteView through an external Web server</u></b></TD></TR><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Static HTML Pages</TD><TD VALIGN=TOP><input type=checkbox value=CHECKED name=createStaticHTML " + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_createStaticHTML") + ">" + "</TD><TD VALIGN=TOP>Select this option if you plan to serve SiteView information" + " using a external  web server application.</TD></TR>\n");
        outputStream.print("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>Web Server Address:</TD><TD VALIGN=TOP> <input type=text name=webserverAddress size=40 value=\"" + COM.dragonflow.Page.generalPrefsPage.getValue(hashmap, "_webserverAddress") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the domain name or address of the web server" + " that you want to use to serve SiteView pages." + "</TD></TR></TABLE>\n");
        outputStream.print("<hr><p>\n<input type=submit value=\"Save Changes\">\n</FORM>\n");
        printFooter(outputStream);
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_preference") && !request.actionAllowed("_detachFromMA"))
        {
            throw new HTTPRequestException(557);
        }
        try
        {
            HashMap hashmap = getMasterConfig();
            String s = request.getValue("operation");
//            if(COM.dragonflow.TopazIntegration.MAManager.isAttached())
//            {
//                handleDetachForm();
//            } else
            if(request.isPost())
            {
                String s1 = COM.dragonflow.Utils.LUtils.getLicenseForXKey(hashmap);
                if(savePreferences())
                {
                    if(s.equals("save") || s.equals("setup"))
                    {
                        String s2 = request.getValue("licenseForX");
                        if(s1.length() > 0 || s2.length() > 0)
                        {
                            if(checkLicenseChange(s1) && s2.length() > 0)
                            {
                                COM.dragonflow.Utils.LUtils.addMonitorType(s2);
                            }
                            printPreferencesSaved();
                        }
                    } else
                    if(s.equals("restart"))
                    {
                        SiteViewMain.SiteViewSupport.ShutdownProcess();
                    } else
                    {
                        printPreferencesSaved();
                    }
                }
            } else
            {
                printForm(hashmap);
            }
        }
        catch(java.lang.Exception exception)
        {
            printError("Error reading master.config file", "File error: master.config", "7");
        }
    }

    private void handleDetachForm()
    {
        if(request.getValue("operation").equals("Detach"))
        {
//            COM.dragonflow.TopazIntegration.MAManager.releaseSiteView();
            printPreferencesSaved();
        } else
        {
            printBodyHeader("General Preferences");
            printButtonBar("GenPref.htm", "", getSecondNavItems(request));
            outputStream.print("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=page value=generalPrefs>\n");
            outputStream.print("<TABLE><TR><TD></TD><TD></TD><TD WIDTH=\"50\"></TD></TR>\n<TR><TD></TD><TD><H2>Detach from Monitoring Administration</H2></TD></TR>\n<TR><TD COLSPAN=2 ALIGN=LEFT>Detach SiteView from the Monitoring Administration server.</TD></TR>\n<TR><TD COLSPAN=3><HR></TD></TR>\n");
            outputStream.print("<TR><TD>Detach from Monitoring Administration: :</TD><TD><input type=submit name=operation value=\"Detach\" style=\"WIDTH: 147px; HEIGHT: 24px\" size=46></TD></TR>");
            outputStream.print("<TR><TD>Note: To resume remote control, please perform an Attach operation from the Monitoring Administration server</TD></TR>");
            outputStream.print("</TABLE>");
            printFooter(outputStream);
        }
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.generalPrefsPage generalprefspage = new generalPrefsPage();
        if(args.length > 0)
        {
            generalprefspage.args = args;
        }
        generalprefspage.handleRequest();
    }
}
