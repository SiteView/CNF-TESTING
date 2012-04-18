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

import com.recursionsw.jgl.HashMap;

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage

public class portalGeneralPrefsPage extends COM.dragonflow.Page.prefsPage
{

    public portalGeneralPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/?account=" + request.getAccount(), "", 0);
    }

    void savePreferences()
    {
        String s = "";
        try
        {
            HashMap hashmap = getMasterConfig();
            String s1 = COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_httpPort");
            String s2 = request.getValue("httpPort").trim();
            if(!s1.equals(s2) && s2.length() > 0)
            {
                int i = COM.dragonflow.Properties.StringProperty.toInteger(s2);
                if(i == 0)
                {
                    s = s + "\tHTTP Port must be a number greater than 0";
                } else
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
            String s3 = request.getValue("authorizedIP");
            if(s3.length() > 0 && !COM.dragonflow.Utils.TextUtils.onlyChars(s3, "0123456789,.*"))
            {
                s = s + "\tThe IP Address allowed access contained illegal characters";
            }
            if(s3.length() == 0 && request.getValue("checkAddressAndLogin").length() > 0)
            {
                s = s + "\tTo enable <B>Require both IP address and Login</B> you must fill in authorized IP address(es)";
            }
            String s4 = request.getValue("license").trim();
            if(!COM.dragonflow.Utils.LUtils.setLicenseKey(s4, hashmap))
            {
                s = s + COM.dragonflow.Utils.LUtils.getLicenseErrorString(s4);
            }
            hashmap.put("_httpPort", s2);
            hashmap.put("_authorizedIP", s3);
            hashmap.put("_checkAddressAndLogin", request.getValue("checkAddressAndLogin"));
            hashmap.put("_localeEnabled", request.getValue("localeEnabled"));
            if(s.length() == 0)
            {
                saveMasterConfig(hashmap);
                if(!s1.equals(s2))
                {
                    byte byte0 = 15;
                    String s5 = "Waiting 15 seconds for " + COM.dragonflow.SiteView.Platform.productName + "'s web server to restart...";
                    if(s2.length() == 0)
                    {
                        s5 = "";
                        byte0 = 0;
                    } else
                    if(s1.length() == 0)
                    {
                        s5 = "Waiting 15 seconds for " + COM.dragonflow.SiteView.Platform.productName + "'s web server to start...";
                    }
                    String s6 = COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_webserverAddress");
                    String s7 = "http://" + s6 + ":" + s2 + "/";
                    printPreferencesSaved(s7, "<H2>Preferences have been saved</H2><P>" + s5, byte0);
                } else
                {
                    printPreferencesSaved();
                }
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
        printBodyHeader("Generate Preferences");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe General Preferences form\n");
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
        printBodyHeader("General Preferences");
        printButtonBar("GenCentraPrefs.htm", "");
        String s = "";
        String s1 = "";
        if(!COM.dragonflow.SiteView.Platform.isDemo())
        {
            s = COM.dragonflow.Utils.LUtils.getLicenseKey(hashmap);
            s1 = COM.dragonflow.Utils.LUtils.getLicenseSummary(hashmap, true);
        }
        outputStream.println("<!--LICENSE " + s + " LICENSE-->\n" + "<!--LICENSESUMMARY " + s1 + " LICENSESUMMARY-->\n" + "<p><CENTER><H2>General Preferences</H2></CENTER><p>\n" + getPagePOST("portalGeneralPrefs", "save") + "<DL>\n" + "<P><DT>License Number: <input type=text name=license size=30 value=\"" + s + "\">\n" + "<DD><A NAME=license>Enter</A> the license number for this copy of " + COM.dragonflow.SiteView.Platform.productName + ".  " + "The license number is given to you\n" + "when you purchase a license and is required after the " + 10 + " day evaluation period.\n" + "<BR><BR>" + s1);
        outputStream.print("</DL>\n <DL>\n<P><DT><input type=checkbox value=CHECKED name=localeEnabled " + COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_localeEnabled") + ">Enable locale-specific display of date and time\n" + "<DD>This option enables the display of dates and times in a locale-specific manner.\n" + "When this option is turned off, the format for the United States is used.\n" + "Check out the help page for more information about over-riding the default locale used by " + COM.dragonflow.SiteView.Platform.productName + ".\n" + "&nbsp;&nbsp;<BR><B>Note:</B> " + COM.dragonflow.SiteView.Platform.productName + " must be restarted after this option has been changed before it will become effective.\n");
        outputStream.print("</DL>\n<HR><CENTER><H3>Built-in Web Server</H3></CENTER><BR>\n" + COM.dragonflow.SiteView.Platform.productName + " contains a built-in web server to serve its pages. The options below control the port and\n" + "the access to the built-in server. <P><P>\n" + "<DL>\n" + "<P><DT>IP Addresses Allowed Access: <input type=text name=authorizedIP size=30 value=\"" + COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_authorizedIP") + "\"><P>\n" + "<DD>The IP address that is always allowed access to the " + COM.dragonflow.SiteView.Platform.productName + ". Use a trailing wildcard character to specify a range of IP addresses.\n" + "For example, 206.168.191.* allows access to the 206.168.191 subnet. To allow multiple IP address, separate them by commas.\n" + "<P><DT><input type=checkbox value=CHECKED name=checkAddressAndLogin " + COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_checkAddressAndLogin") + ">Require both IP address and Login\n" + "<DD>This options limits access to connections that have both an allowed IP address and a correct username and password.\n" + "When this option is turned off, it allows access to anyone who has either an allowed IP address or a valid username and password.\n" + "<P><DT>CentraScope Port: <input type=text name=httpPort size=10 value=\"" + COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_httpPort") + "\"> &nbsp;&nbsp;(current port: " + COM.dragonflow.Page.portalGeneralPrefsPage.getValue(hashmap, "_httpActivePort") + ")<P>\n" + "<DD>Enter the port number for the " + COM.dragonflow.SiteView.Platform.productName + " built-in web server. Leaving this blank will prevent " + COM.dragonflow.SiteView.Platform.productName + " from serving web pages.\n" + "</DL><p><BR>\n" + "<input type=submit value=\"Save Changes\">\n" + "</FORM>\n");
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
        COM.dragonflow.Page.portalGeneralPrefsPage portalgeneralprefspage = new portalGeneralPrefsPage();
        if(args.length > 0)
        {
            portalgeneralprefspage.args = args;
        }
        portalgeneralprefspage.handleRequest();
    }
}
