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

import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.StandardAction.Page;

// Referenced classes of package COM.dragonflow.Page:
// settingsPrefsPage, supportPage, CGI

public class pagerPrefsPage extends COM.dragonflow.Page.settingsPrefsPage
{

    public static String type = "Pager";

    public pagerPrefsPage()
    {
    }

    public String saveAdditionalPagerPrefs()
    {
        HashMap hashmap = new HashMap();
        hashmap.put("_name", request.getValue("additionalPagerName").replace(' ', '_'));
        hashmap.put("_disabled", request.getValue("additionalPagerDisabled"));
        StringBuffer stringbuffer = new StringBuffer();
        String s = COM.dragonflow.Properties.ScheduleProperty.requestToScheduleString(request, stringbuffer);
        if(s.length() > 0)
        {
            hashmap.put("_schedule", s);
        }
        hashmap.put("_pagerSpeed", request.getValue("pagerSpeed"));
        hashmap.put("_pagerType", request.getValue("pagerType"));
        hashmap.put("_pagerAlphaPIN", request.getValue("pagerAlphaPIN"));
        hashmap.put("_pagerAlphaPhone", request.getValue("pagerAlphaPhone"));
        hashmap.put("_pagerDirectPhone", request.getValue("pagerDirectPhone"));
        hashmap.put("_pagerOptionPhone", request.getValue("pagerOptionPhone"));
        hashmap.put("_pagerOption", request.getValue("pagerOption"));
        hashmap.put("_pagerCustom", request.getValue("pagerCustom"));
        return saveAdditionalPrefs(type, request.getValue("additionalPager"), hashmap);
    }

    void printPreferencesSaved()
    {
        printRefreshPage(getPageLink("pagerPrefs", "test"), 0);
    }

    void savePreferences()
    {
        try
        {
            HashMap hashmap = getSettings();
            if(request.isSiteSeerAccount())
            {
                request.setValue("pagerAlphaPhone", COM.dragonflow.Utils.TextUtils.checkPhone(request.getValue("pagerAlphaPhone")));
                request.setValue("pagerDirectPhone", COM.dragonflow.Utils.TextUtils.checkPhone(request.getValue("pagerDirectPhone")));
                request.setValue("pagerOptionPhone", COM.dragonflow.Utils.TextUtils.checkPhone(request.getValue("pagerOptionPhone")));
            }
            if(request.getValue("additionalPager").length() > 0)
            {
                String s = saveAdditionalPagerPrefs();
                printRefreshPage(getPageLink("pagerPrefs", "test") + "&additionalPager=" + s, 0);
            } else
            {
                hashmap.put("_pagerPort", request.getValue("pagerPort"));
                hashmap.put("_pagerSpeed", request.getValue("pagerSpeed"));
                hashmap.put("_pagerType", request.getValue("pagerType"));
                hashmap.put("_pagerAlphaPIN", request.getValue("pagerAlphaPIN"));
                hashmap.put("_pagerAlphaPhone", request.getValue("pagerAlphaPhone"));
                hashmap.put("_pagerDirectPhone", request.getValue("pagerDirectPhone"));
                hashmap.put("_pagerOptionPhone", request.getValue("pagerOptionPhone"));
                hashmap.put("_pagerOption", request.getValue("pagerOption"));
                hashmap.put("_pagerCustom", request.getValue("pagerCustom"));
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
        String s1 = request.getValue("additionalPager");
        HashMap hashmap = getSettings();
        HashMap hashmap1 = null;
        Enumeration enumeration = COM.dragonflow.Page.pagerPrefsPage.getValues(hashmap, "_additionalPager");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap2 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            if(COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap2, "_id").equals(s1))
            {
                hashmap1 = hashmap2;
            }
        } while(true);
        if(request.isPost())
        {
            deleteAdditionalSetting(type, s1);
            printRefreshPage(getPageLink("pagerPrefs", "") + "#additionalPager", 0);
        } else
        {
            String s2 = "";
            if(hashmap1 != null)
            {
                s2 = COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_name");
            }
            outputStream.println("<FONT SIZE=+1>Are you sure you want to delete the pager settings for <B>" + s2 + "</B>?</FONT>" + "<p>" + getPagePOST("pagerPrefs", s) + "<input type=hidden name=additionalPager value=\"" + s1 + "\">" + "<TABLE WIDTH=\"100%\" BORDER=0><TR>" + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\"><input type=submit value=\"" + s + " Pager Setting\"></TD>" + "<TD WIDTH=\"6%\"></TD><TD ALIGN=RIGHT WIDTH=\"41%\"><A HREF=" + getPageLink("pagerPrefs", "") + ">Return to Pager Preferences</A></TD><TD WIDTH=\"6%\"></TD>" + "</TR></TABLE></FORM>");
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
                if(request.getValue("additionalPager").length() <= 0)
                {
                    break label0;
                }
                flag = true;
                Enumeration enumeration = COM.dragonflow.Page.pagerPrefsPage.getValues(((HashMap) (obj)), "_additionalPager");
                obj = null;
                if(request.getValue("_additionalPager").equals("new"))
                {
                    break label1;
                }
                String s1 = request.getValue("additionalPager");
                HashMap hashmap;
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break label1;
                    }
                    hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                } while(!COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap, "_id").equals(s1));
                obj = hashmap;
            }
            if(obj == null)
            {
                obj = new HashMapOrdered(true);
            }
        }
        String s = "";
        String s2 = "";
        String s3 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerType").equals("alpha") ? "CHECKED" : "";
        String s4 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerType").equals("direct") ? "CHECKED" : "";
        String s5 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerType").equals("option") ? "CHECKED" : "";
        String s6 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerType").equals("custom") ? "CHECKED" : "";
        if(!flag && !request.getPermission("_editPagerPort").equals("hidden"))
        {
            if(COM.dragonflow.SiteView.Platform.isWindows(platformOS()))
            {
                String s7 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM1") ? "SELECTED" : "";
                String s9 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM2") ? "SELECTED" : "";
                String s11 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM3") ? "SELECTED" : "";
                String s13 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM4") ? "SELECTED" : "";
                String s15 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM5") ? "SELECTED" : "";
                String s17 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM6") ? "SELECTED" : "";
                String s19 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM7") ? "SELECTED" : "";
                String s20 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort").equals("COM8") ? "SELECTED" : "";
                s = "<select name=pagerPort size=1><option " + s7 + ">COM1" + "<option " + s9 + ">COM2" + "<option " + s11 + ">COM3" + "<option " + s13 + ">COM4" + "<option " + s15 + ">COM5" + "<option " + s17 + ">COM6" + "<option " + s19 + ">COM7" + "<option " + s20 + ">COM8" + "</select>\n";
            } else
            {
                s = " <input type=text name=pagerPort size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerPort") + "\"><br>\n";
                if(COM.dragonflow.SiteView.Platform.isSolaris())
                {
                    s = s + "(for example, the path for port b is /dev/term/b)\n";
                } else
                if(COM.dragonflow.SiteView.Platform.isSGI())
                {
                    s = s + "(for example, the path for port 2 is /dev/ttym2)\n";
                } else
                if(COM.dragonflow.SiteView.Platform.isHP())
                {
                    s = s + "(for example, the path for port 2 is /dev/tty1p0)\n";
                }
            }
        }
        String s8 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerSpeed").equals("300") ? "SELECTED" : "";
        String s10 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerSpeed").equals("1200") ? "SELECTED" : "";
        String s12 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerSpeed").equals("2400") ? "SELECTED" : "";
        String s14 = COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerSpeed").equals("9600") ? "SELECTED" : "";
        if(COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerSpeed").length() == 0)
        {
            s10 = "SELECTED";
        }
        s2 = "<select name=pagerSpeed size=1><option " + s8 + ">300" + "<option " + s10 + ">1200" + "<option " + s12 + ">2400" + "<option " + s14 + ">9600" + "</select> baud\n";
        String s16 = "Default Pager Preferences";
        if(flag)
        {
            if(request.getValue("additionalPager").equals("new"))
            {
                s16 = "Add Additional Pager Settings";
            } else
            {
                s16 = "Edit Additional Pager Settings";
            }
        }
        printBodyHeader(s16);
        String s18 = "PagerPref.htm";
        if(flag)
        {
            s18 = "AddPager.htm";
        }
        printButtonBar(s18, "", getSecondNavItems(request));
        printPrefsBar("Pager");
        outputStream.println("<p><CENTER><H2>" + s16 + "</H2></CENTER><p>\n" + getPagePOST("pagerPrefs", "save"));
        if(flag)
        {
            outputStream.println("The name of these pager settings is used to specify pagers when adding alerts<BLOCKQUOTE>\nSetting Name <input type=text size=30 name=additionalPagerName value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_name") + "\">\n" + "</BLOCKQUOTE>\n" + "Disabling the pager settings prevents alert pages from being sent via that pager setting" + "<BLOCKQUOTE>\n" + "<input type=checkbox " + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_disabled") + " name=additionalPagerDisabled value=CHECKED>Disabled\n" + "</BLOCKQUOTE>\n" + "Most paging companies use 1200 baud - you only need to change the modem speed if your paging company requires it." + "<BLOCKQUOTE>\n" + s2 + "</BLOCKQUOTE>\n");
        } else
        {
            outputStream.println("<hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD>Modem port/path:&nbsp;</td><td>\n" + s + "</TD><td> SiteView Pager Alerts are sent using a modem connected directly to the SiteView server." + "</td></TR><TR><TD>Connection speed:&nbsp;</td><TD>" + s2 + "</td><td>Use the default speed of 1200 unless your paging service provider tells you otherwise.\n" + "</TD></TR></TABLE>\n");
        }
        outputStream.println("<h3>Pager Connection Options:</h3><hr><TABLE border=0 width=100% cellspacing=0><tr><td width=15%></td><td></td><td></td></tr>\n");
        outputStream.println("<tr><td colspan=2 valign=top><input type=radio name=pagerType value=alpha " + s3 + "><b>Modem-to-Modem Connection (Preferred)</b></td><td> Send alphanumeric pager messages by connecting to a modem at paging service.<p></td></tr>\n" + "<tr><td valign=top><p>Modem number:</p></td><td valign=top><input type=text name=pagerAlphaPhone size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerAlphaPhone") + "\"></td>\n" + "<td valign=top>Contact your paging service to find out the Modem Number (TAP/IXO number) for paging.  \n" + "<br>  <A href=/SiteView/docs/PagerPref.htm#ModemNumber TARGET=Help>Common pager service modem numbers</a></td></tr> \n" + "<tr><td valign=top><p>PIN number:</p></td><td> <input type=text name=pagerAlphaPIN size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerAlphaPIN") + "\"></td>\n" + "<td valign=top><p>Enter the last seven digits of your pager's PIN number.</td></tr>\n");
        outputStream.println("<tr><td colspan=3><hr></td></tr>\n<tr><td colspan=2 valign=top><input type=radio name=pagerType value=direct " + s4 + "><b>Dial and Enter Message</b></td>\n" + "<td valign=top>Use a direct phone number to send a numeric page.  Works with most local paging services.<p></td></tr>\n" + "<tr><td valign=top><p>Phone number:</p></td><td><input type=text name=pagerDirectPhone size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerDirectPhone") + "\"></td><td></td></tr>\n");
        outputStream.println("<tr><td colspan=3><hr></td></tr>\n<tr><td colspan=2 valign=top><input type=radio name=pagerType value=option " + s5 + "><b>Dial, Enter Command, and Enter Message</b></td>\n" + "<td valign=top>\n" + "Use a direct number but enter a command\n" + "before sending a page.  Use this option if your paging company requires a \n" + "PIN number before sending a page.<p></td></tr> \n" + "<tr><td valign=top><p>Phone number:</p></td><td valign=top> <input type=text name=pagerOptionPhone size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerOptionPhone") + "\"></td><td></td></tr>\n" + "<tr><td valign=top><p>Send page command:</p></td><td valign=top> <input type=text name=pagerOption size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerOption") + "\"></td><td></td></tr>\n");
        outputStream.println("<tr><td colspan=3><hr></td></tr>\n<tr><td colspan=2 valign=top><input type=radio name=pagerType value=custom " + s6 + "><b>Custom Modem Connection</b></td>\n" + "<td valign=top>Use this option if custom modem commands are required.<p></td></tr>\n" + "<tr><td valign=top><p>Modem command:</p></td> <td valign=top> <input type=text name=pagerCustom size=40 value=\"" + COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_pagerCustom") + "\"></td><td></td></tr>\n" + "<tr><td>&nbsp;</td><td colspan=2>The modem command string should contain the phone number to dial, any additional digits, followed by $message.  SiteView" + "replaces <tt>$message</tt> variable with the message specified in the Pager Alert definition" + "where the message should be inserted.  The comma character creates a short pause.<p>\n" + "For example: if the pager company's number is 123-4567, your pager PIN is 333-3333, and each command must be followed by the # key, the Modem command\n" + "might look like <tt>ATDT 123-4567,,333-3333#,,$message#</tt>\n" + "<p></td></tr>\n");
        outputStream.println("</TABLE>\n");
        if(flag)
        {
            outputStream.println("<input type=submit value=\"Save Additional Setting\">\n<input type=hidden name=additionalPager value=" + request.getValue("additionalPager") + ">\n");
        } else
        {
            outputStream.println("<input type=submit value=\"Save Changes\">\n");
        }
        if(flag && !request.getPermission("_pagerSchedule").equals("hidden"))
        {
            outputStream.println("<p><HR><CENTER><H3>Advanced Options</H3></CENTER><TABLE><TR><TD>Schedule</TD><TD>\n");
            COM.dragonflow.Properties.ScheduleProperty.printScheduleTable(outputStream, COM.dragonflow.Page.pagerPrefsPage.getValue(((HashMap) (obj)), "_schedule"), "");
            outputStream.println("</TD></TR><TR><TD COLSPAN=2>optional schedule for these settings to be enabled - for example, enable Sunday from 10:00 to 22:00</TD></TR></TABLE><P>");
            if(COM.dragonflow.SiteView.Platform.isSiteSeerAccount(request.getAccount()))
            {
                outputStream.print("<BR><B>Note:</B> Times entered must be Mountain Time Zone in the U.S.");
            }
        }
        outputStream.println("</FORM>\n");
        if(!flag)
        {
            Enumeration enumeration1 = COM.dragonflow.Page.pagerPrefsPage.getValues(((HashMap) (obj)), "_additionalPager");
            outputStream.print("<p><HR><A NAME=additionalPager>Additional pager settings allow you to create named settings, which you can then specify\nwhen creating pager alerts.</A><P><CENTER>\n<TABLE WIDTH=\"100%\" BORDER=1 cellspacing=0><CAPTION align=left><font size=4><b>Additional Pager Settings</b></font></CAPTION>\n<TR CLASS=\"tabhead\">\n<TH>Name</TH>\n<TH>Status</TH>\n<TH>Type</TH>\n<TH>Phone</TH>\n<TH>PIN</TH>\n<TH WIDTH=\"2%\">Edit</TH>\n<TH WIDTH=\"2%\">Test</TH>\n<TH WIDTH=\"1%\">Del</TH>\n");
            if(!enumeration1.hasMoreElements())
            {
                outputStream.println("<TR><TD></TD><TD></TD><TD align=center>no additional pager settings</TD><TD></TD><TD></TD></TR>\n");
            } else
            {
                HashMap hashmap1;
                for(; enumeration1.hasMoreElements(); outputStream.println("<TD align=center><A HREF=" + getPageLink("pagerPrefs", "Delete") + "&additionalPager=" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_id") + ">X</TD></TR>"))
                {
                    hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration1.nextElement());
                    String s21 = COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_name");
                    outputStream.println("<TR><TD><B>" + s21 + "</B></TD>");
                    if(COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_disabled").length() > 0)
                    {
                        outputStream.println("<TD><B>disabled</B></TD>");
                    } else
                    if(COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_schedule").length() > 0)
                    {
                        outputStream.println("<TD>scheduled</TD>");
                    } else
                    {
                        outputStream.println("<TD>enabled</TD>");
                    }
                    String s22 = COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerType");
                    if(s22.equals("alpha"))
                    {
                        outputStream.println("<TD>alphanumeric</TD><TD>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerAlphaPhone") + "</TD>" + "<TD>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerAlphaPIN") + "</TD>");
                    } else
                    if(s22.equals("direct"))
                    {
                        outputStream.println("<TD>numeric</TD><TD>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerDirectPhone") + "</TD>" + "<TD></TD>");
                    } else
                    if(s22.equals("option"))
                    {
                        outputStream.println("<TD>numeric</TD><TD>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerOptionPhone") + "</TD>" + "<TD>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerOption") + "</TD>");
                    } else
                    if(s22.equals("custom"))
                    {
                        outputStream.println("<TD>custom</TD><TD COLSPAN=2>" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_pagerCustom") + "</TD>");
                    } else
                    {
                        outputStream.println("<TD>no type selected</TD><TD></TD><TD></TD>");
                    }
                    outputStream.println("<TD align=center><A HREF=" + getPageLink("pagerPrefs", "") + "&additionalPager=" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_id") + ">Edit</TD>");
                    outputStream.println("<TD align=center><A HREF=" + getPageLink("pagerPrefs", "test") + "&additionalPager=" + COM.dragonflow.Page.pagerPrefsPage.getValue(hashmap1, "_id") + ">Test</TD>");
                }

            }
            outputStream.println("</TABLE></CENTER><P><A HREF=" + getPageLink("pagerPrefs", "") + "&additionalPager=new>Add</A> additional pager setting");
        }
        printFooter(outputStream);
    }

    COM.dragonflow.StandardAction.Page getPageObject(HashMap hashmap)
    {
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSettingsGroup();
        COM.dragonflow.StandardAction.Page page = new Page();
        if(hashmap != null)
        {
            Enumeration enumeration = (Enumeration) hashmap.keys();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s = (String)enumeration.nextElement();
                if(s.startsWith("_pager"))
                {
                    page.setProperty(s, (String)hashmap.get(s));
                }
            } while(true);
        }
        siteviewobject.addElement(page);
        return page;
    }

    String getAlertURL()
    {
        return getPageLink("alert", "List", request.getValue("view"));
    }

    void remotePage()
    {
        String s = request.getValue("init");
        String s1 = request.getValue("dial");
        String s2 = request.getValue("hangup");
        String s3 = request.getValue("speed");
        String s4 = request.getValue("port");
        printBodyHeader("Remote Pager");
        COM.dragonflow.StandardAction.Page page = getPageObject(null);
        if(s4.length() == 0)
        {
            s4 = page.getSetting("_pagerPort");
        }
        Array array = page.pagerExec(s4, s3, 0, s, s1, s2);
        outputStream.print("<PRE>");
        for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); outputStream.println(enumeration.nextElement())) { }
        outputStream.println("</PRE>");
        printFooter(outputStream);
    }

    void printTestForm()
    {
        HashMap hashmap = getAdditionalSettings(type, request.getValue("additionalPager"));
        String s = "Pager Test";
        if(hashmap != null)
        {
            s = s + " for " + ((String)hashmap.get("_name")).replace('_', ' ');
        }
        printBodyHeader(s);
        COM.dragonflow.StandardAction.Page page = getPageObject(hashmap);
        String s1 = "message";
        String s2 = page.getPagerCommand(s1);
        printButtonBar("AlertPage.htm", "");
        outputStream.print("<CENTER><H2>" + s + "</H2></CENTER><P>\n" + getPagePOST("pagerPrefs", "test", request.getValue("view")) + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n" + "Send a test message to the pager.\n" + "<p>\n" + "Enter the message to send.\n" + "<p>Message: <input type=text name=message size=50>\n" + "<br>\n" + "<p>Using Pager Command: " + s2 + "\n" + "<p>\n");
        outputStream.print("<p>&nbsp;</p><TABLE border=\"0\" width=\"90%\"><tr><td>\n<input type=hidden name=additionalPager value=" + request.getValue("additionalPager") + "></td>\n" + "<input type=submit name=send value=\"Send\"> a test Pager message\n" + "<td width=\"25%\">&nbsp;</td>\n" + "<td align=right><A HREF=" + getPageLink("pagerPrefs", "") + ">Back to Pager Preferences</A></TD>" + "</TR></TABLE></FORM>\n");
        printFooter(outputStream);
    }

    void testPage()
    {
        HashMap hashmap = getAdditionalSettings(type, request.getValue("additionalPager"));
        String s = "Pager Test";
        if(hashmap != null)
        {
            s = s + " for " + ((String)hashmap.get("_name")).replace('_', ' ');
        }
        printBodyHeader(s);
        outputStream.println("</H2></CENTER>\n<P>\nSending test page...\n<P>\n");
        outputStream.println("<HR>\n");
        outputStream.flush();
        boolean flag = true;
        if(!isPortalServerRequest())
        {
            printContentStartComment();
            COM.dragonflow.StandardAction.Page page = getPageObject(hashmap);
            if(request.getValue("pagerType").length() > 0)
            {
                page.setProperty("_pagerType", request.getValue("pagerType"));
                page.setProperty("_pagerAlphaPhone", request.getValue("pagerAlphaPhone"));
                page.setProperty("_pagerOptionPhone", request.getValue("pagerOptionPhone"));
                page.setProperty("_pagerDirectPhone", request.getValue("pagerOptionPhone"));
                page.setProperty("_pagerAlphaPIN", request.getValue("pagerAlphaPIN"));
                page.setProperty("_pagerOption", request.getValue("pagerOption"));
                page.setProperty("_pagerTimeout", request.getValue("pagerTimeout"));
                if(page.getProperty("_pagerType").equals("numeric"))
                {
                    if(page.getProperty("_pagerOption").length() > 0)
                    {
                        page.setProperty("_pagerType", "option");
                    } else
                    {
                        page.setProperty("_pagerType", "direct");
                    }
                }
                flag = false;
            }
            String s1 = request.getValue("message");
            String s3 = page.getPagerCommand(s1);
            Array array = page.pagerSend(s1);
            String s4 = "";
            String s5 = "";
            if(array != null)
            {
                Enumeration enumeration = (Enumeration) array.iterator();
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    String s6 = (String)enumeration.nextElement();
                    if(s6.indexOf("message rejected") < 0)
                    {
                        continue;
                    }
                    s5 = "The message was rejected - this is usually because the PIN number is incorrect or expired. If you're using a 10 digit PIN, try using just the last 7 digits, and no punctuation. If this does not work, try using all 10 digits of the PIN. Less commonly, this error can occur if the message is too long for the paging service.";
                    break;
                } while(true);
                for(Enumeration enumeration1 = (Enumeration) array.iterator(); enumeration1.hasMoreElements();)
                {
                    s4 = s4 + (String)enumeration1.nextElement() + "\n";
                }

            }
            if(s4.length() == 0)
            {
                outputStream.println("<p>\nThe test message was sent to the pager\n<p>\n");
            } else
            {
                outputStream.println("The test message to the pager could not be sent.\n<p>");
                if(s5.length() > 0)
                {
                    outputStream.println("<B>" + s5 + "</B>\n<p>");
                }
                outputStream.println("The error was: <P><PRE>" + s4 + "</PRE>\n" + "<p>\n" + "The pager command was: " + s3 + "\n" + "<p>\n" + "Suggestions:<ul>\n" + "<li>Check the cable connection to the server serial port." + "<li>Check the cable connection to the modem." + "<li>Check that the modem is powered on." + "<li>Check that the modem is connected to a phone line." + "<li>Listen to the modem.  You should hear a dial tone and then hear the modem dial.\n" + "<li>Listen after the modem dials.  For alphanumeric messages, you should hear a modem answer (a screeching noise)\n" + "<li>Look at the commands being sent and received.  If some characters\n" + "are missing (for example, 'cnct' instead of 'connect'), try using a 14.4 or slower modem.\n" + "Sometimes, there are flow control problems with higher speed modems." + "</ul><p>\n" + "If you've tried the suggestions above, and your paging setup still doesn't work, send a support request using\n" + "the following form.\n");
                StringBuffer stringbuffer = new StringBuffer("The test message to the pager could not be sent.\n\nThe error was: \n" + s4 + "\n" + "\n" + "The pager command was: " + s3 + "\n");
                COM.dragonflow.Page.supportPage.printShortForm(outputStream, request.getAccount(), stringbuffer);
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
        outputStream.println("</BODY>\n");
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        if(s.equals("remote"))
        {
            remotePage();
        } else
        if(s.equals("test"))
        {
            if(!request.actionAllowed("_preferenceTest"))
            {
                throw new HTTPRequestException(557);
            }
            if(request.isPost())
            {
                testPage();
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
                    printError("The link was incorrect", "unknown operation", "10");
                }
            } else
            {
                printForm();
            }
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.pagerPrefsPage pagerprefspage = new pagerPrefsPage();
        if(args.length > 0)
        {
            pagerprefspage.args = args;
        }
        pagerprefspage.handleRequest();
    }

}
