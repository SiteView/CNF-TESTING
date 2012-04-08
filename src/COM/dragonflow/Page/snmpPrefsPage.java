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
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.StandardAction.SNMPTrap;

// Referenced classes of package COM.dragonflow.Page:
// settingsPrefsPage, CGI

public class snmpPrefsPage extends COM.dragonflow.Page.settingsPrefsPage
{

    public static String type = "SNMP";

    public snmpPrefsPage()
    {
    }

    public String saveAdditionalSNMPPrefs()
    {
        jgl.HashMap hashmap = new HashMap();
        hashmap.put("_name", request.getValue("additionalSNMPName").replace(' ', '_'));
        hashmap.put("_disabled", request.getValue("additionalSNMPDisabled"));
        hashmap.put("_snmpHost", request.getValue("snmpHost"));
        hashmap.put("_snmpCommunity", request.getValue("snmpCommunity"));
        if(request.getValue("snmpObjectID").equals("0"))
        {
            hashmap.put("_snmpObjectID", request.getValue("snmpObjectIDOther"));
        } else
        {
            hashmap.put("_snmpObjectID", request.getValue("snmpObjectID"));
        }
        hashmap.put("_snmpGeneric", request.getValue("snmpGeneric"));
        hashmap.put("_snmpSpecific", request.getValue("snmpSpecific"));
        hashmap.put("_snmpTrapVersion", request.getValue("snmpTrapVersion"));
        return saveAdditionalPrefs(type, request.getValue("additionalSNMP"), hashmap);
    }

    void printPreferencesSaved()
    {
        printRefreshPage(getPageLink("snmpPrefs", "test"), 0);
    }

    void savePreferences()
    {
        try
        {
            jgl.HashMap hashmap = getSettings();
            if(request.getValue("additionalSNMP").length() > 0)
            {
                String s = saveAdditionalSNMPPrefs();
                printRefreshPage(getPageLink("snmpPrefs", "test") + "&additionalSNMP=" + s, 0);
            } else
            {
                hashmap.put("_snmpHost", request.getValue("snmpHost"));
                hashmap.put("_snmpCommunity", request.getValue("snmpCommunity"));
                if(request.getValue("snmpObjectID").equals("0"))
                {
                    hashmap.put("_snmpObjectID", request.getValue("snmpObjectIDOther"));
                } else
                {
                    hashmap.put("_snmpObjectID", request.getValue("snmpObjectID"));
                }
                hashmap.put("_snmpGeneric", request.getValue("snmpGeneric"));
                hashmap.put("_snmpSpecific", request.getValue("snmpSpecific"));
                hashmap.put("_snmpTrapVersion", request.getValue("snmpTrapVersion"));
                saveSettings(hashmap);
                printPreferencesSaved();
            }
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
        }
    }

    void printDeleteForm(String s)
    {
        String s1 = request.getValue("additionalSNMP");
        jgl.HashMap hashmap = getSettings();
        jgl.HashMap hashmap1 = null;
        Enumeration enumeration = COM.dragonflow.Page.snmpPrefsPage.getValues(hashmap, "_additionalSNMP");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            jgl.HashMap hashmap2 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            if(COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap2, "_id").equals(s1))
            {
                hashmap1 = hashmap2;
            }
        } while(true);
        if(request.isPost())
        {
            deleteAdditionalSetting(type, s1);
            printRefreshPage(getPageLink("snmpPrefs", "") + "#additionalSNMP", 0);
        } else
        {
            String s2 = "";
            if(hashmap1 != null)
            {
                s2 = COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_name");
            }
            outputStream.println("<FONT SIZE=+1>Are you sure you want to delete the SNMP settings for <B>" + s2 + "</B>?</FONT>" + "<p>" + getPagePOST("snmpPrefs", s) + "<input type=hidden name=additionalSNMP value=\"" + s1 + "\">" + "<TABLE WIDTH=\"100%\" BORDER=0><TR>" + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\"><input type=submit value=\"" + s + " SNMP Setting\"></TD>" + "<TD WIDTH=\"6%\"></TD><TD ALIGN=RIGHT WIDTH=\"41%\"><A HREF=" + getPageLink("snmpPrefs", "") + ">Return to SNMP Preferences</A></TD><TD WIDTH=\"6%\"></TD>" + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    void printForm()
    {
        java.lang.Object obj;
        boolean flag;
label0:
        {
label1:
            {
                obj = getSettings();
                flag = false;
                if(request.getValue("additionalSNMP").length() <= 0)
                {
                    break label0;
                }
                flag = true;
                Enumeration enumeration = COM.dragonflow.Page.snmpPrefsPage.getValues(((jgl.HashMap) (obj)), "_additionalSNMP");
                obj = null;
                if(request.getValue("_additionalSNMP").equals("new"))
                {
                    break label1;
                }
                String s1 = request.getValue("additionalSNMP");
                jgl.HashMap hashmap;
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break label1;
                    }
                    hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                } while(!COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap, "_id").equals(s1));
                obj = hashmap;
            }
            if(obj == null)
            {
                obj = new HashMapOrdered(true);
            }
        }
        String s = "Default SNMP Preferences";
        if(flag)
        {
            if(request.getValue("additionalSNMP").equals("new"))
            {
                s = "Add Additional SNMP Settings";
            } else
            {
                s = "Edit Additional SNMP Settings";
            }
        }
        String s2 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpHost");
        String s3 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpCommunity");
        if(s3.length() == 0)
        {
            s3 = "public";
        }
        String s4 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpObjectID");
        if(s4.length() == 0)
        {
            s4 = COM.dragonflow.StandardAction.SNMPTrap.OIDS[0][1];
        }
        String s5 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpGeneric");
        if(s5.length() == 0)
        {
            s5 = "2";
        }
        String s6 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpSpecific");
        if(s6.length() == 0)
        {
            s6 = "0";
        }
        String s7 = COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_snmpTrapVersion");
        if(s7.length() == 0)
        {
            s7 = "V1";
        }
        printBodyHeader(s);
        String s8 = "snmpPref.htm";
        if(flag)
        {
            s8 = "AddSNMP.htm";
        }
        printButtonBar(s8, "", getSecondNavItems(request));
        printPrefsBar("SNMP");
        outputStream.println("<p><CENTER><H2>" + s + "</H2></CENTER><p>\n" + "<p>These settings allow you to integrate SiteView with a" + " SNMP management console using SiteView SNMP Trap Alerts.</p> " + getPagePOST("snmpPrefs", "save"));
        if(flag)
        {
            outputStream.println("The name of these SNMP settings is used to specify SNMP settings when adding alerts<BLOCKQUOTE>\nSetting Name <input type=text size=30 name=additionalSNMPName value=\"" + COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_name") + "\">\n" + "</BLOCKQUOTE>\n" + "Disabling the SNMP settings prevents alert traps from being sent via that SNMP setting" + "<BLOCKQUOTE>\n" + "<input type=checkbox " + COM.dragonflow.Page.snmpPrefsPage.getValue(((jgl.HashMap) (obj)), "_disabled") + " name=additionalSNMPDisabled value=CHECKED>Disabled\n" + "</BLOCKQUOTE>\n");
        }
        outputStream.println("<hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD ALIGN=LEFT VALIGN=TOP width=20%>Send to Host: </td><TD ALIGN=LEFT VALIGN=TOP><input type=text name=snmpHost size=30 value=\"" + s2 + "\">" + "</TD><TD ALIGN=LEFT VALIGN=TOP>Enter the host name or IP address of the machine " + "running the SNMP console that will receive SNMP traps. For example," + " snmp.this-company.com or 206.168.191.20.</td></tr>" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SNMP Object ID:</td><TD ALIGN=LEFT VALIGN=TOP>\n" + "<select name=snmpObjectID size=1>\n");
        String s9 = "SELECTED";
        for(int i = 0; i < COM.dragonflow.StandardAction.SNMPTrap.OIDS.length; i++)
        {
            String s10 = "";
            if(COM.dragonflow.StandardAction.SNMPTrap.OIDS[i][1].equals(s4))
            {
                s10 = "SELECTED";
                s9 = "";
            }
            outputStream.println("<option " + s10 + " value=" + COM.dragonflow.StandardAction.SNMPTrap.OIDS[i][1] + ">" + COM.dragonflow.StandardAction.SNMPTrap.OIDS[i][0] + "</option>");
        }

        outputStream.println("<option " + s9 + " value=0>other...</option>");
        if(s9.length() > 0)
        {
            s9 = s4;
        }
        outputStream.println("</select> <BR> Other: <input type=text name=snmpObjectIDOther size=25 value=\"" + s9 + "\">\n" + "</td><TD ALIGN=LEFT VALIGN=TOP>" + " Select or enter the SNMP object that is sending the trap." + " For example .1.3.6.1.2.1.1 is the &quot;system&quot; object from MIB-II" + " (RFC 1213).</td></tr><tr><td colspan=3><hr></td></tr>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP>SNMP Community: </td><TD ALIGN=LEFT VALIGN=TOP>" + "<input type=text name=snmpCommunity size=25 value=\"" + s3 + "\"><P>" + "</td><TD ALIGN=LEFT VALIGN=TOP>The SNMP community name used for this trap - " + "usually this is &quot;public&quot;.</td></tr><tr><td colspan=3><hr></td></tr>\n" + "<TR><TD ALIGN=LEFT VALIGN=TOP>Trap ID: </td>" + "<TD ALIGN=LEFT VALIGN=TOP>Generic: <select name=snmpGeneric size=1>\n");
        for(int j = 0; j < COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS.length; j++)
        {
            String s11 = "";
            if(COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS[j][1].equals(s5))
            {
                s11 = "SELECTED";
            }
            outputStream.println("<option " + s11 + " value=" + COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS[j][1] + ">" + COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS[j][0] + "</option>");
        }

        outputStream.println("</select> <br> Specific: <input type=text name=snmpSpecific size=10 value=" + s6 + ">\n" + "</td><TD ALIGN=LEFT VALIGN=TOP>Generic trap type." + "  If the generic trap type is &quot;enterprise specific&quot;," + " then fill in the specific trap type, which is a number." + "</td></tr><tr><td colspan=3><hr></td></tr>\n");
        outputStream.println("<TR><TD ALIGN=LEFT VALIGN=TOP>SNMP Trap version:</td><TD ALIGN=LEFT VALIGN=TOP>\n<select name=snmpTrapVersion size=1>\n");
        for(int k = 0; k < COM.dragonflow.StandardAction.SNMPTrap.VERSION.length; k++)
        {
            String s12 = "";
            if(COM.dragonflow.StandardAction.SNMPTrap.VERSION[k][1].equals(s7))
            {
                s12 = "SELECTED";
            }
            outputStream.println("<option " + s12 + " value=" + COM.dragonflow.StandardAction.SNMPTrap.VERSION[k][1] + ">" + COM.dragonflow.StandardAction.SNMPTrap.VERSION[k][0] + "</option>");
        }

        outputStream.println("</select><TD ALIGN=LEFT VALIGN=TOP>This describes what version the SNMP trap will be formatted in when sending to your SNMP host.</td></tr><tr><td colspan=3><hr></td></tr></table><br>\n");
        if(flag)
        {
            outputStream.println("<input type=submit value=\"Save Additional Setting\">\n<input type=hidden name=additionalSNMP value=" + request.getValue("additionalSNMP") + ">\n");
        } else
        {
            outputStream.println("<input type=submit value=\"Save Changes\">\n");
        }
        outputStream.print("</FORM><br>\n");
        if(!flag)
        {
            Enumeration enumeration1 = COM.dragonflow.Page.snmpPrefsPage.getValues(((jgl.HashMap) (obj)), "_additionalSNMP");
            outputStream.print("<p><HR><A NAME=additionalSNMP>Additional SNMP settings allow you to create named settings, which you can then specify\nwhen creating SNMP trap alerts.</A><P><CENTER>\n<TABLE WIDTH=\"100%\" BORDER=2 cellspacing=0><CAPTION align=left><font size=4><b>Additional SNMP Settings</b></font></CAPTION>\n<TR CLASS=\"tabhead\">\n<TH>Name</TH>\n<TH>Host</TH>\n<TH>Object ID</TH>\n<TH>Trap</TH>\n<TH WIDTH=\"2%\">Edit</TH>\n<TH WIDTH=\"2%\">Test</TH>\n<TH WIDTH=\"1%\">Del</TH>\n");
            if(!enumeration1.hasMoreElements())
            {
                outputStream.println("<TR><TD></TD><TD></TD><TD align=center>no additional SNMP settings</TD><TD></TD><TD></TD></TR>\n");
            } else
            {
                jgl.HashMap hashmap1;
                for(; enumeration1.hasMoreElements(); outputStream.println("<TD align=center><A HREF=" + getPageLink("snmpPrefs", "Delete") + "&additionalSNMP=" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_id") + ">X</TD></TR>"))
                {
                    hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration1.nextElement());
                    String s13 = COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_name");
                    if(COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_disabled").length() > 0)
                    {
                        s13 = "(disabled) " + s13;
                    }
                    outputStream.println("<TR><TD><B>" + s13 + "</B></TD>");
                    outputStream.println("<TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpHost"));
                    outputStream.println("<TD>" + getObjectName(COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpObjectID")) + "</TD>");
                    if(COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpGeneric").equals("6"))
                    {
                        outputStream.println("<TD>enterprise specific trap " + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpSpecific") + "</TD>");
                    } else
                    {
                        outputStream.println("<TD>" + getTrapName(COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpGeneric")));
                    }
                    outputStream.println("<TD align=center><A HREF=" + getPageLink("snmpPrefs", "") + "&additionalSNMP=" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_id") + ">Edit</A></TD>");
                    outputStream.println("<TD align=center><A HREF=" + getPageLink("snmpPrefs", "test") + "&additionalSNMP=" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_id") + ">Test</TD>");
                }

            }
            outputStream.println("</TABLE></CENTER><P><A HREF=" + getPageLink("snmpPrefs", "") + "&additionalSNMP=new>Add</A> additional SNMP setting");
        }
        printFooter(outputStream);
    }

    String getAlertURL()
    {
        return getPageLink("alert", "List", request.getValue("view"));
    }

    void printTestForm()
    {
        jgl.HashMap hashmap = getAdditionalSettings(type, request.getValue("additionalSNMP"));
        String s = "SNMP Trap Test";
        if(hashmap != null)
        {
            s = s + " for " + ((String)hashmap.get("_name")).replace('_', ' ');
        }
        printBodyHeader(s);
        jgl.HashMap hashmap1 = hashmap;
        if(hashmap == null)
        {
            hashmap1 = getSettings();
        }
        printButtonBar("AlertSNMPTrap.htm", "");
        outputStream.print("<CENTER><H2>" + s + "</H2></CENTER><P>\n" + getPagePOST("snmpPrefs", "test", request.getValue("view")) + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n" + "Send a test SNMP trap.\n" + "<p>\n" + "Enter a message to be sent with the trap.\n" + "<p>SNMP Message: <input type=text name=message size=50>\n" + "<P>\n" + "<TABLE BORDER=0>" + "<TR><TD>Send to Host:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpHost") + "</TD></TR>\n" + "<TR><TD>SNMP Object ID:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpObjectID") + "</TD></TR>\n" + "<TR><TD>SNMP Community:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpCommunity") + "</TD></TR>\n" + "<TR><TD>Generic Trap ID:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpGeneric") + "</TD></TR>\n" + "<TR><TD>Specific Trap ID:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpSpecific") + "</TD></TR>\n" + "<TR><TD>Specific Trap Version:</TD><TD>" + COM.dragonflow.Page.snmpPrefsPage.getValue(hashmap1, "_snmpTrapVersion") + "</TD></TR>\n" + "</TABLE>\n");
        outputStream.print("<p>&nbsp;</p><TABLE border=\"0\" width=\"90%\"><tr><td>\n<input type=hidden name=additionalSNMP value=" + request.getValue("additionalSNMP") + ">\n" + "<input type=submit name=send value=\"Send\"> a test SNMP trap</td>\n" + "<td width=\"25%\">&nbsp;</td>\n" + "<td align=right><A HREF=" + getPageLink("snmpPrefs", "") + ">Back to SNMP Preferences</A></TD>" + "</TR></TABLE></FORM>\n");
        printFooter(outputStream);
    }

    COM.dragonflow.StandardAction.SNMPTrap getSNMPTrapObject(jgl.HashMap hashmap)
    {
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSettingsGroup();
        COM.dragonflow.StandardAction.SNMPTrap snmptrap = new SNMPTrap();
        if(hashmap != null)
        {
            Enumeration enumeration = hashmap.keys();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s = (String)enumeration.nextElement();
                if(s.startsWith("_snmp"))
                {
                    snmptrap.setProperty(s, (String)hashmap.get(s));
                }
            } while(true);
        }
        siteviewobject.addElement(snmptrap);
        return snmptrap;
    }

    void testTrap()
    {
        jgl.HashMap hashmap = getAdditionalSettings(type, request.getValue("additionalSNMP"));
        String s = "SNMP Trap Test";
        if(hashmap != null)
        {
            s = s + " for " + ((String)hashmap.get("_name")).replace('_', ' ');
        }
        outputStream.println("<CENTER><H2>" + s + "</H2></CENTER>\n" + "<HR>\n");
        boolean flag = true;
        if(!isPortalServerRequest())
        {
            printContentStartComment();
            String s1 = request.getValue("message");
            COM.dragonflow.StandardAction.SNMPTrap snmptrap = getSNMPTrapObject(hashmap);
            if(request.getValue("snmpHost").length() > 0)
            {
                snmptrap.setProperty("_snmpHost", request.getValue("snmpHost"));
                snmptrap.setProperty("_snmpCommunity", request.getValue("snmpCommunity"));
                snmptrap.setProperty("_snmpGeneric", request.getValue("snmpGeneric"));
                snmptrap.setProperty("_snmpSpecific", request.getValue("snmpSpecific"));
                snmptrap.setProperty("_snmpObjectID", request.getValue("snmpObjectID"));
                flag = false;
            }
            jgl.Array array = new Array();
            array.add(s1);
            snmptrap.setArgs(array);
            String s3 = snmptrap.sendTrap(s1);
            if(s3.length() == 0)
            {
                printRefreshHeader(s, getAlertURL(), 10);
                outputStream.println("The test trap was sent successfully.\n<p>(" + snmptrap + ")\n");
            } else
            {
                printBodyHeader(s);
                outputStream.println("The SNMP Trap could not be sent.\n<p>\nThe error was: " + s3 + ".\n" + "<p>\n");
            }
            printContentEndComment();
        } else
        {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)getSiteView();
            if(portalsiteview != null)
            {
                String s2 = portalsiteview.getURLContentsFromRemoteSiteView(request, "_centrascopeRefreshMatch");
                outputStream.println(s2);
            }
        }
        if(flag)
        {
            outputStream.println("<HR>\n<A HREF=" + getAlertURL() + ">Back to Alerts</A><BR>\n");
        } else
        {
            outputStream.println("<HR>\nPlease use your browser's Back feature to return to your SiteSeer home page.<BR>\n");
        }
        outputStream.println("</BODY>");
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        if(s.equals("test"))
        {
            if(!request.actionAllowed("_preferenceTest"))
            {
                throw new HTTPRequestException(557);
            }
            if(request.isPost())
            {
                testTrap();
            } else
            {
                printTestForm();
            }
        } else
        {
            if(!request.actionAllowed("_preference"))
            {
                throw new HTTPRequestException(557);
            }
            if(s.equals("Delete"))
            {
                printDeleteForm(s);
            } else
            if(request.isPost())
            {
                if(s.equals("save") || s.equals("setup"))
                {
                    savePreferences();
                } else
                {
                    printError("The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
                }
            } else
            {
                printForm();
            }
        }
    }

    String getObjectName(String s)
    {
        for(int i = 0; i < COM.dragonflow.StandardAction.SNMPTrap.OIDS.length; i++)
        {
            if(COM.dragonflow.StandardAction.SNMPTrap.OIDS[i][1].equals(s))
            {
                return COM.dragonflow.StandardAction.SNMPTrap.OIDS[i][0];
            }
        }

        return s;
    }

    String getTrapName(String s)
    {
        for(int i = 0; i < COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS.length; i++)
        {
            if(COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS[i][1].equals(s))
            {
                return COM.dragonflow.StandardAction.SNMPTrap.TRAPIDS[i][0];
            }
        }

        return s;
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.snmpPrefsPage snmpprefspage = new snmpPrefsPage();
        if(args.length > 0)
        {
            snmpprefspage.args = args;
        }
        snmpprefspage.handleRequest();
    }

}
