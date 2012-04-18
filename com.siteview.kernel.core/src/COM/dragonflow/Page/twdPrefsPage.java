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

import java.util.StringTokenizer;

import com.recursionsw.jgl.Array;

import COM.dragonflow.HTTP.HTTPRequestException;
//import COM.dragonflow.TopazWatchdog.WatchdogException;
import COM.dragonflow.Utils.URLInfo;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class twdPrefsPage extends COM.dragonflow.Page.prefsPage
{

    static Array profileList = null;
    static String PREFIX = "http://";
    static String PREFIXSSL = "https://";
    static String SUFFIX = "/topaz/";
    static int topaz45HostMasks[] = {
        32, 1024, 64, 512, 16, 1, 2048, 8, 128, 16384, 
        0x20000, 8192
    };
    static int topaz50HostMasks[] = {
        0x1000000, 512, 0x2000000, 1, 8, 16384
    };

    public twdPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory() + "/SiteView.html", "", 0);
    }

    void printErrorPage(String s)
    {
//        outputStream.print("<HEAD><TITLE>" + COM.dragonflow.SiteView.TopazInfo.getTopazName() + "/ActiveWatch Console Connection</TITLE>\n" + COM.dragonflow.Page.CGI.nocacheHeader + COM.dragonflow.SiteView.Platform.charSetTag + "</HEAD>\n" + "<BODY BGCOLOR=#FFFFFF>\n");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe previous form\n");
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

    String getServer(String s)
    {
        COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo(s);
        return urlinfo.getRawHost();
    }

    void printPageHeader()
    {
//        printBodyHeader(COM.dragonflow.SiteView.TopazInfo.getTopazName());
        printButtonBar("topazWatchDog.htm", "", getSecondNavItems(request));
//        printPrefsBar(COM.dragonflow.SiteView.TopazInfo.getTopazName());
        String s = request.getValue("topazMS");
        outputStream.print("<FORM NAME=frmWatchdogSettings ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=page value=twdPrefs>\n" + "<input type=hidden name=" + "topazMS" + " value=\"" + s + "\">\n" + "<INPUT TYPE='HIDDEN' NAME='disabledHostsIds' ID='disabledHostsIds'>\n" + "<INPUT TYPE='HIDDEN' NAME='enabledHostsIds'  ID='enabledHostsIds'>\n");
    }

    void printForm()
        throws java.lang.Exception
    {
        printPageHeader();
//        outputStream.print("<BR><BR><TABLE width=\"80%\">\n<TR><TD COLSPAN=3 align=center><H2>" + COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " Settings</H2></TD></TR>\n" + "<TR><TD COLSPAN=3 align=center><HR></TD></TR>\n" + "</TABLE>\n");
        outputStream.print(getJavaScriptForAvaluatingWatchdodSettingsParameters());
        printMonitoredTopazServices();
        outputStream.print("<TR><P></TR>");
        printTopazMachinesView();
        outputStream.print("</FORM>");
        printFooter(outputStream);
    }

    private void printTopazMachinesView()
    {
//        HashMap hashmap = null;
//        try
//        {
//            hashmap = COM.dragonflow.TopazWatchdog.TopazHostsConfigurator.getAllTopazHostsToMonitor();
//        }
//        catch(COM.dragonflow.SiteView.TopazAPIException topazapiexception)
//        {
//            hashmap = null;
//        }
//        if(null == hashmap || hashmap.size() == 0)
//        {
//            return;
//        }
//        Enumeration enumeration = (Enumeration) hashmap.keys();
//        int i = hashmap.size();
//        outputStream.print("<INPUT TYPE='HIDDEN' ID='numberOfHosts' value='" + i + "'>\n" + "<TABLE width=\"80%\" border=2>\n" + "<TR align=\"middle\"><th colspan=\"3\" bgcolor=\"darkblue\"> <font color=\"white\"> " + COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " Machine View</font></th></TR>\n" + "<TR><TH align=left>Name</TH><TH align=left>Location</TH><TH align=left>" + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Services</TH></TR>\n");
//        StringBuffer stringbuffer = new StringBuffer();
//        int j = 0;
//        for(; enumeration.hasMoreElements(); stringbuffer.append("</TR>\n"))
//        {
//            j++;
//            String s1 = (String)enumeration.nextElement();
//            HashMap hashmap1 = (HashMap)hashmap.get(s1);
//            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "hostname");
//            String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "hostlocation");
//            int k = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "HostType"));
//            String s4 = COM.dragonflow.TopazWatchdog.WatchdogUtils.getHostActiveTypesStringForUi(k);
//            String s;
//            if(COM.dragonflow.TopazWatchdog.WatchdogConfig.isHostDisabled(s1))
//            {
//                s = " ";
//            } else
//            {
//                s = " CHECKED ";
//            }
//            String s5 = "host_id_" + s1;
//            String s6 = "host_" + j;
//            stringbuffer.append("<TR>\n");
//            stringbuffer.append("<TD><INPUT TYPE=checkbox ").append(s);
//            stringbuffer.append(" NAME=").append(s5).append(" ID=").append(s6);
//            stringbuffer.append(">").append(s2).append("</TD>\n");
//            stringbuffer.append("<TD>").append(s3).append("</TD>\n");
//            stringbuffer.append("<TD>").append(s4).append("</TD>\n");
//        }
//
//        stringbuffer.append("</TABLE>\n");
//        outputStream.print(stringbuffer.toString());
//        outputStream.print("<P><INPUT type=submit name=operation value=\"Save Settings\" onClick=\"getWatchdogSettings()\">\n<BR><B>Note:</B> Configuration update may take over a minute to complete.\n");
    }

    private String getJavaScriptForAvaluatingWatchdodSettingsParameters()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<SCRIPT language=JavaScript>\n");
        stringbuffer.append("function getWatchdogSettings()\n");
        stringbuffer.append("{\n");
        stringbuffer.append(" var numberOfTopazServices = document.getElementById(\"numberOfTopazServices\");\n");
        stringbuffer.append(" var varMonitoredHostsTypeMask = document.getElementById(\"monitoredHostsTypeMask\");\n");
        stringbuffer.append(" var num1 = new Number(numberOfTopazServices.value);\n");
        stringbuffer.append(" var returnedHostsTypeMask = new Number(0);\n");
        stringbuffer.append("\tfor(var i = 1; i <= num1; i++)\n");
        stringbuffer.append("\t{\n");
        stringbuffer.append("\t\tvar topazServiceId = \"twd_service_\" + i;\n");
        stringbuffer.append("\t\tvar topazServiceCheckbox = document.getElementById(topazServiceId);\n");
        stringbuffer.append("\t\tif (topazServiceCheckbox.checked == true)\n");
        stringbuffer.append("\t\t{\n");
        stringbuffer.append("\t\t\tvar serviceName = topazServiceCheckbox.name;\n");
        stringbuffer.append("\t\t\tvar hostTypeMaskString = serviceName.substring(10, serviceName.length);\n");
        stringbuffer.append("\t\t\tvar hostTypeMaskNumber = new Number(hostTypeMaskString);\n");
        stringbuffer.append("\t\t\treturnedHostsTypeMask |= hostTypeMaskNumber;\n");
        stringbuffer.append("\t\t}\n");
        stringbuffer.append("\t}\n");
        stringbuffer.append("\tvarMonitoredHostsTypeMask.value = returnedHostsTypeMask;\n");
        stringbuffer.append(" var numberOfHostsElement = document.getElementById(\"numberOfHosts\");\n");
        stringbuffer.append(" var num2 = 0;\n");
        stringbuffer.append(" if (numberOfHostsElement != null) \n");
        stringbuffer.append("\t{\n");
        stringbuffer.append("   num2 = new Number(numberOfHostsElement.value);\n");
        stringbuffer.append("\t}\n");
        stringbuffer.append("\tvar disabledHostsIdsElement = document.getElementById(\"disabledHostsIds\");\n");
        stringbuffer.append("\tvar enabledHostsIdsElement = document.getElementById(\"enabledHostsIds\");\n");
        stringbuffer.append(" var strDisabledHostIds = \"\";\n");
        stringbuffer.append(" var strEnabledHostIds = \"\";\n");
        stringbuffer.append("\tfor(var i = 1; i <= num2; i++)\n");
        stringbuffer.append("\t{\n");
        stringbuffer.append("\t\tvar host = \"host_\" + i;\n");
        stringbuffer.append("\t\tvar hostCheckboxElement = document.getElementById(host);\n");
        stringbuffer.append("\t\tvar hostName = hostCheckboxElement.name;\n");
        stringbuffer.append("\t\t// the hostName is in the format of \"host_id_15\", \"host_id_34\". etc.\n");
        stringbuffer.append("\t\t// host_id_15\n");
        stringbuffer.append("\t\t// 01234567890123\n");
        stringbuffer.append("\t\tvar hostId = hostName.substring(8, hostName.length);\n");
        stringbuffer.append("\t\tif (hostCheckboxElement.checked)\n");
        stringbuffer.append("\t\t{\n");
        stringbuffer.append("\t\t\t// this host is not desabled. Do nothing.\n");
        stringbuffer.append("\t\t\tstrEnabledHostIds = strEnabledHostIds + hostId + \",\";\n");
        stringbuffer.append("\t\t}\n");
        stringbuffer.append("\t\telse\n");
        stringbuffer.append("\t\t{\n");
        stringbuffer.append("\t\t\tstrDisabledHostIds = strDisabledHostIds + hostId + \",\";\n");
        stringbuffer.append("\t\t}\n");
        stringbuffer.append("\t}\n");
        stringbuffer.append("\tif (strDisabledHostIds.lenght != 0)\n");
        stringbuffer.append("\t{\n");
        stringbuffer.append("\t\tstrDisabledHostIds = strDisabledHostIds.substring(0, strDisabledHostIds.length - 1);\n");
        stringbuffer.append("\t}\n");
        stringbuffer.append("\tif (strEnabledHostIds.lenght != 0)\n");
        stringbuffer.append("\t{\n");
        stringbuffer.append("\t\tstrEnabledHostIds = strEnabledHostIds.substring(0, strEnabledHostIds.length - 1);\n");
        stringbuffer.append("\t}\n");
        stringbuffer.append("\tdisabledHostsIdsElement.value = strDisabledHostIds;\n");
        stringbuffer.append("\tenabledHostsIdsElement.value = strEnabledHostIds;\n");
        stringbuffer.append(" document.frmWatchdogSettings.submit();\n");
        stringbuffer.append("}\n");
        stringbuffer.append("</SCRIPT>\n");
        return stringbuffer.toString();
    }

    private void printMonitoredTopazServices()
///        throws COM.dragonflow.TopazWatchdog.WatchdogException
    {
//        StringBuffer stringbuffer = new StringBuffer();
//        int ai[] = null;
//        switch(COM.dragonflow.SiteView.TopazInfo.getTopazVersion())
//        {
//        case 45: // '-'
//            ai = topaz45HostMasks;
//            break;
//
//        case 50: // '2'
//            ai = topaz50HostMasks;
//            break;
//
//        default:
//            throw new WatchdogException(COM.dragonflow.SiteView.TopazInfo.getTopazName() + " version " + COM.dragonflow.SiteView.TopazInfo.getTopazVersion() + " is not supported");
//        }
//        stringbuffer.append("<INPUT TYPE='HIDDEN' ID='numberOfTopazServices' value='" + ai.length + "'>\n");
//        stringbuffer.append("<INPUT TYPE='HIDDEN' NAME='monitoredHostsTypeMask' ID='monitoredHostsTypeMask'>\n");
//        stringbuffer.append("<h3>Monitor the following " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " services:\n<h3>");
//        stringbuffer.append("<TABLE width=\"50%\" border=0>\n");
//        stringbuffer.append("<TR>\n");
//        stringbuffer.append(getHostCheckboxItems(ai));
//        stringbuffer.append("</TABLE>\n");
//        outputStream.print(stringbuffer.toString());
//        outputStream.print("<P><INPUT type=submit name=operation value=\"Save Settings\" onClick=\"getWatchdogSettings()\">\n<BR><BR><B>Note:</B> Changes in the above settings affect the " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " machine view, and may take over a minute to complete.\n");
    }

    private String getHostCheckboxItems(int ai[])
    {
        StringBuffer stringbuffer = new StringBuffer();
//        int i = COM.dragonflow.TopazWatchdog.WatchdogConfig.getMonitoredHostsTypeMask();
//        for(int j = 0; j < ai.length; j++)
//        {
//            if((j & 1) == 0)
//            {
//                stringbuffer.append("<TR>\n");
//            }
//            String s = " ";
//            if((i & ai[j]) != 0)
//            {
//                s = " CHECKED ";
//            }
//            String s1 = COM.dragonflow.TopazWatchdog.WatchdogConfig.getHostTypeDisplayString(ai[j]);
//            stringbuffer.append("<TD><INPUT type=checkbox").append(s).append("name=host_type_" + ai[j] + " id=twd_service_" + (j + 1) + ">" + s1 + "</TD>\n");
//            if((j & 1) == 1)
//            {
//                stringbuffer.append("<TR>\n");
//            }
//        }
//
//        if((ai.length & 1) == 1)
//        {
//            stringbuffer.append("<TR>\n");
//        }
        return stringbuffer.toString();
    }

    private void saveSettings()
    {
        printPageHeader();
//        COM.dragonflow.TopazWatchdog.TopazHostsConfigurator.init();
        boolean flag = false;
        String s = request.getValue("monitoredHostsTypeMask");
        String s1 = request.getValue("disabledHostsIds");
        String s2 = request.getValue("enabledHostsIds");
        try
        {
            int i = java.lang.Integer.parseInt(s);
//            int j = COM.dragonflow.TopazWatchdog.WatchdogConfig.getMonitoredHostsTypeMask();
//            if(j != i)
//            {
//                COM.dragonflow.TopazWatchdog.WatchdogConfig.setMonitoredHostsTypeMask(i);
//                flag = true;
//            }
            java.util.StringTokenizer stringtokenizer = null;
            stringtokenizer = new StringTokenizer(s1, ",", false);
            do
            {
                if(!stringtokenizer.hasMoreTokens())
                {
                    break;
                }
                String s6 = stringtokenizer.nextToken();
//                if(COM.dragonflow.TopazWatchdog.WatchdogConfig.addToDisabledHosts(s6))
//                {
//                    flag = true;
//                }
            } while(true);
            stringtokenizer = new StringTokenizer(s2, ",", false);
            do
            {
                if(!stringtokenizer.hasMoreTokens())
                {
                    break;
                }
                String s7 = stringtokenizer.nextToken();
//                if(COM.dragonflow.TopazWatchdog.WatchdogConfig.removeFromDisabledHosts(s7))
//                {
//                    flag = true;
//                }
            } while(true);
        }
        catch(java.lang.NumberFormatException numberformatexception)
        {
            COM.dragonflow.Log.LogManager.log("TopazWatchdog", "Cannot parse Watchdog settings parameters.");
        }
//        outputStream.println("<h3>" + COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " Configuration Result:</h3>");
        outputStream.flush();
//        if(!COM.dragonflow.TopazWatchdog.WatchdogConfig.isTopazWatchdogEnabled())
//        {
//            String s3 = COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " was not configured. <BR>\n" + "Please <B>configure</B> " + COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " and try again.<BR>\n";
//            outputStream.println(s3);
//            outputStream.flush();
//        } else
//        if(flag)
//        {
//            COM.dragonflow.TopazWatchdog.TopazHostsConfigurator.configureAll(true, true, outputStream);
//        } else
//        {
//            outputStream.println("Watchdog configuration remains unchanged.");
//        }
        String s4 = getPageLink("twdPrefs", "showWatchdogSettings");
//        outputStream.print("<P><A href=" + s4 + "> <B> Edit " + COM.dragonflow.TopazWatchdog.WatchdogConfig.getWatchdogDisplayedName() + " Settings </B> </A>\n");
        String s5 = getPageLink("topazPrefs", "");
//        outputStream.print("<P><A href=" + s5 + "> <B> " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Server Registration </B> </A>\n");
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
            if(s.equals("Save Settings"))
            {
                saveSettings();
            }
            if(!s.equals("Save Profile"));
            if(!s.equals("reSync"));
        } else
        {
            printForm();
        }
    }

    public static void main(String args[])
    {
        (new twdPrefsPage()).handleRequest();
    }

}
