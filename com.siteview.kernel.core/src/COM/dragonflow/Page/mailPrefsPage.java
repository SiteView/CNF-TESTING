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
import java.util.Vector;

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// settingsPrefsPage, CGI

public class mailPrefsPage extends COM.dragonflow.Page.settingsPrefsPage
{

    public static String type = "Mail";

    public mailPrefsPage()
    {
    }

    void printPreferencesSaved()
    {
        printRefreshPage(getPageLink("mailPrefs", "test"), 0);
    }

    public void saveAdditionalMailPrefs()
    {
        HashMap hashmap = null;
        String s = request.getValue("additionalMail");
        if(!s.equals("new"))
        {
            hashmap = getAdditionalSettings(type, s);
        }
        if(hashmap == null)
        {
            hashmap = new HashMap();
        }
        hashmap.put("_name", request.getValue("additionalMailName"));
        hashmap.put("_disabled", request.getValue("additionalMailDisabled"));
        hashmap.put("_template", request.getValue("additionalMailTemplate"));
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = COM.dragonflow.Properties.ScheduleProperty.requestToScheduleString(request, stringbuffer);
        if(stringbuffer.length() > 0)
        {
            printError("The schedule has an invalid time format", stringbuffer.toString(), getPageLink("mailPrefs", ""));
            return;
        }
        if(s1.length() > 0)
        {
            hashmap.put("_schedule", s1);
        }
        String s2 = COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("email"));
        hashmap.put("_email", s2);
        s = saveAdditionalPrefs(type, s, hashmap);
        printRefreshPage(getPageLink("mailPrefs", ""), 0);
    }

    void printDeleteForm(String s)
    {
        String s1 = request.getValue("additionalMail");
        HashMap hashmap = getSettings();
        HashMap hashmap1 = null;
        Enumeration enumeration = COM.dragonflow.Page.mailPrefsPage.getValues(hashmap, "_additionalMail");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap2 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            if(COM.dragonflow.Page.mailPrefsPage.getValue(hashmap2, "_id").equals(s1))
            {
                hashmap1 = hashmap2;
            }
        } while(true);
        if(request.isPost())
        {
            deleteAdditionalSetting(type, s1);
            printRefreshPage(getPageLink("mailPrefs", "") + "#additionalMail", 0);
        } else
        {
            String s2 = "";
            if(hashmap1 != null)
            {
                s2 = COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_name");
            }
            outputStream.println("<FONT SIZE=+1>Are you sure you want to delete the e-mail settings for <B>" + s2 + "</B>?</FONT>" + "<p>" + getPagePOST("mailPrefs", s) + "<input type=hidden name=additionalMail value=\"" + s1 + "\">" + "<TABLE WIDTH=\"100%\" BORDER=0><TR>" + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\"><input type=submit value=\"" + s + " E-mail Setting\"></TD>" + "<TD WIDTH=\"6%\"></TD><TD ALIGN=RIGHT WIDTH=\"41%\"><A HREF=" + getPageLink("mailPrefs", "") + ">Return to Mail Preferences</A></TD><TD WIDTH=\"6%\"></TD>" + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    void savePreferences()
    {
        try
        {
            HashMap hashmap = getMasterConfig();
            hashmap.put("_mailServer", request.getValue("mailServer"));
            hashmap.put("_mailServerBackup", request.getValue("mailServerBackup"));
            hashmap.put("_fromAddress", COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("fromAddress")));
            hashmap.put("_autoEmail", COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("autoEmail")));
            hashmap.put("_autoDaily", request.getValue("autoDaily"));
            hashmap.put("_autoStartup", request.getValue("autoStartup"));
            saveMasterConfig(hashmap);
            printPreferencesSaved();
        }
        catch(java.io.IOException ioexception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", "10");
        }
    }

    void printForm()
    {
        HashMap hashmap = getSettings();
        printBodyHeader("E-mail Preferences");
        printButtonBar("MailPref.htm", "", getSecondNavItems(request));
        printPrefsBar("E-mail");
        outputStream.println("<p><CENTER><H2>E-mail Preferences</H2></CENTER><p>\n");
        if(request.isStandardAccount())
        {
            String s = "";
            String s1 = COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailServer");
            if(s1.length() == 0 || s1.equals("mail.dragonflow.com") || s1.equals("gateway.dragonflow.com") || s1.equals("gateway.Dragonflow.com"))
            {
                s = "<blockquote><B>Note: Please enter the name of your local mail server so " + COM.dragonflow.SiteView.Platform.productName + " will be able to send email alerts and messages.</B></blockquote>";
            }
            outputStream.println(getPagePOST("mailPrefs", "save") + "<hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%><P>Mail Server Domain Name:</TD>" + "<TD VALIGN=TOP><input type=text name=mailServer size=35 value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailServer") + "\">\n" + "</TD><TD VALIGN=TOP>" + "Enter the domain name of the mail server to use for sending SiteView messages" + " and alerts.  This server should be an SMTP mail server. For example," + " gateway.this-company.com.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>" + "Administrator E-mail Address:</TD><TD VALIGN=TOP>" + "<input type=text name=autoEmail size=35 value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_autoEmail") + "\">\n" + "</TD><TD VALIGN=TOP>Enter the administrator e-mail address where " + COM.dragonflow.SiteView.Platform.productName + " will send status messages." + " For example, " + COM.dragonflow.SiteView.Platform.supportEmail + ".\n" + "</TD></TR>\n" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> " + COM.dragonflow.SiteView.Platform.productName + " Status Messages</TD><TD VALIGN=TOP>" + "<input type=checkbox name=autoDaily value=checked " + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_autoDaily") + ">Daily Status\n" + "<BR><input type=checkbox name=autoStartup value=checked " + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_autoStartup") + "> " + COM.dragonflow.SiteView.Platform.productName + " Starts/Restarts\n" + "</TD><TD VALIGN=TOP>" + "Select which regular" + COM.dragonflow.SiteView.Platform.productName + " status messages you would like to receive.</TD></TR>" + "<tr><td colspan=3><hr></td></tr>");
            outputStream.println("<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%>From Email Address:</TD><TD VALIGN=TOP> <input type=text name=fromAddress size=35 value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_fromAddress") + "\">\n" + "</TD><TD VALIGN=TOP>(optional) Enter the email address used as the From Address for  " + COM.dragonflow.SiteView.Platform.productName + " messages. " + "This is used to make it easier to browse or sort email generated by " + COM.dragonflow.SiteView.Platform.productName + ".\n" + "For example, " + COM.dragonflow.SiteView.Platform.supportEmail + ".</TD></TR>\n" + "<tr><td colspan=3><hr></td></tr>" + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> Backup Mail Server Domain Name: </TD><TD VALIGN=TOP>" + " <input type=text name=mailServerBackup size=40 value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailServerBackup") + "\">\n" + "</TD><TD VALIGN=TOP>(optional) Enter the domain name of a backup mail server" + " to use for sending messages when the primary mail server cannot be reached." + " This server should be an SMTP mail server. For example," + COM.dragonflow.SiteView.Platform.exampleMailServer + ".\n" + "</TD></TR></TABLE><hr>" + "<br><input type=submit value=\"Save Changes\">\n" + "<p>\n" + "</FORM>\n");
            outputStream.println("</TABLE></CENTER><P><A HREF=" + getPageLink("mailPrefs", "additionalMail") + "&additionalMail=new>Add</A> additional e-mail settings \n");
        }
        Enumeration enumeration = COM.dragonflow.Page.mailPrefsPage.getValues(hashmap, "_additionalMail");
        outputStream.print("<p><HR><A NAME=additionalMail>Additional e-mail settings allow you to create <b>email list, template to use</b>, and <b>schedule</b>, which you can then specify\nwhen creating e-mail alerts.</A><P><CENTER>\n<TABLE WIDTH=\"100%\" BORDER=2 cellspacing=0><CAPTION align=left><font size=4><b>Additional E-mail Settings</b></font></CAPTION>\n<TR CLASS=\"tabhead\">\n<TH>Name</TH>\n<TH>Status</TH>\n<TH>E-mail</TH>\n<TH WIDTH=\"2%\">Edit</TH>\n<TH WIDTH=\"1%\">Del</TH>\n");
        if(!enumeration.hasMoreElements())
        {
            outputStream.println("<TR><TD COLSPAN=5 ALIGN=CENTER>no additional e-mail settings</TD></TR>\n");
        } else
        {
            HashMap hashmap1;
            for(; enumeration.hasMoreElements(); outputStream.println("<TD align=center><A HREF=" + getPageLink("mailPrefs", "Delete") + "&additionalMail=" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_id") + ">X</TD></TR>"))
            {
                hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                String s2 = COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_name");
                outputStream.println("<TR><TD><B>" + s2 + "</B></TD>");
                if(COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_disabled").length() > 0)
                {
                    outputStream.println("<TD><B>disabled</B></TD>");
                } else
                if(COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_schedule").length() > 0)
                {
                    outputStream.println("<TD>scheduled</TD>");
                } else
                {
                    outputStream.println("<TD>enabled</TD>");
                }
                outputStream.println("<TD>" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_email") + "</TD>");
                outputStream.println("<TD align=center><A HREF=" + getPageLink("mailPrefs", "additionalMail") + "&additionalMail=" + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap1, "_id") + ">Edit</TD>");
            }

        }
        outputStream.println("</TABLE></CENTER><P><A HREF=" + getPageLink("mailPrefs", "additionalMail") + "&additionalMail=new>Add</A> additional e-mail setting like <b>email list, template to use, and schedule</b>");
        printFooter(outputStream);
    }

    void printAdditionalSettingForm()
    {
        java.lang.Object obj = getSettings();
        String s = request.getValue("additionalMail");
        Enumeration enumeration = COM.dragonflow.Page.mailPrefsPage.getValues(((HashMap) (obj)), "_additionalMail");
        obj = null;
        java.lang.Object obj1;
        if(!s.equals("new"))
        {
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                obj1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                if(!COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj1)), "_id").equals(s))
                {
                    continue;
                }
                obj = obj1;
                break;
            } while(true);
        }
        if(obj == null)
        {
            obj = new HashMapOrdered(true);
        }
        if(s.equals("new"))
        {
            obj1 = "Add Additional E-mail Setting";
        } else
        {
            obj1 = "Edit Additional E-mail Setting";
        }
        printBodyHeader(((String) (obj1)));
        String s1 = "AddMail.htm";
        printButtonBar(s1, "", getSecondNavItems(request));
        printPrefsBar("Mail");
        outputStream.println("<p><CENTER><H2>" + obj1 + "</H2></CENTER><p>\n" + getPagePOST("mailPrefs", "saveAdditional") + "<input type=hidden name=additionalMail value=" + request.getValue("additionalMail") + ">\n");
        outputStream.println("The name of these e-mail settings is used to specify e-mail targets when adding alerts<BLOCKQUOTE>\nSetting Name <input type=text size=30 name=additionalMailName value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj)), "_name") + "\">\n" + "</BLOCKQUOTE>\n" + "The e-mail addresses that alerts will be sent to when using the e-mail setting. Separate multiple addresses with commas (example: " + COM.dragonflow.SiteView.Platform.supportEmail + ", " + COM.dragonflow.SiteView.Platform.salesEmail + ")" + "<BLOCKQUOTE>\n" + "E-mail To: <input type=text size=60 name=email value=\"" + COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj)), "_email") + "\">\n" + "</BLOCKQUOTE>\n" + "Disabling the e-mail settings prevents alert e-mail messages from being sent via that e-mail setting" + "<BLOCKQUOTE>\n" + "<input type=checkbox " + COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj)), "_disabled") + " name=additionalMailDisabled value=CHECKED>Disabled\n" + "</BLOCKQUOTE>\n");
        java.util.Vector vector = COM.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.mail", request);
        java.util.Vector vector1 = new Vector();
        vector1.addElement("");
        vector1.addElement("use template from alert");
        for(int i = 0; i < vector.size(); i++)
        {
            vector1.addElement(vector.elementAt(i));
            vector1.addElement(vector.elementAt(i));
        }

        outputStream.println("<input type=submit value=\"Save Additional Setting\">\n<p><HR><CENTER><H3>Advanced Options</H3></CENTER><TABLE><TR><TD>Template</TD><TD><select size=1 name=additionalMailTemplate>" + COM.dragonflow.Page.mailPrefsPage.getOptionsHTML(vector1, COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj)), "_template")) + "</SELECT></TD></TR>\n" + "<TR><TD COLSPAN=2>optional template to use with this e-mail settings - if a template is selected, this template will be used " + "for sending the alert to this e-mail setting.</TD></TR>");
        if(!request.getPermission("_mailSchedule").equals("hidden"))
        {
            outputStream.println("<TR><TD>Schedule</TD><TD>\n");
            COM.dragonflow.Properties.ScheduleProperty.printScheduleTable(outputStream, COM.dragonflow.Page.mailPrefsPage.getValue(((HashMap) (obj)), "_schedule"), "");
            outputStream.println("</TD></TR><TR><TD COLSPAN=2>optional schedule for these settings to be enabled - for example, enable Sunday from 10:00 to 22:00</TD></TR><TR></TR>");
        }
        outputStream.println("</TABLE><P>\n</FORM>\n");
        if(COM.dragonflow.SiteView.Platform.isSiteSeerAccount(request.getAccount()))
        {
            outputStream.print("<BR><B>Note:</B> Times entered must be Mountain Time Zone in the U.S.");
        }
        printFooter(outputStream);
    }

    String getAlertURL()
    {
        return getPageLink("alert", "List", request.getValue("view"));
    }

    void printTestForm()
	{
		
	    printBodyHeader("Mail Test");
	    HashMap hashmap = getMasterConfig();
	    printButtonBar("AlertMailto.htm", "");
	    outputStream.print("<CENTER><H2>Mail Test</H2></CENTER><P>\n" + getPagePOST("mailPrefs", "test", request.getValue("view")) + "Send a test message using e-mail.\n" + "<p>\n" + "Enter the e-mail address where the message will be sent.  For example, " + COM.dragonflow.SiteView.Platform.supportEmail + ".  Separate multiple addresses using commas.\n" + "<p> E-mail Address: <input type=text name=email size=50>\n" + "<p> Mail Server: " + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailServer") + "\n" + "<p> Backup Mail Server: " + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailServerBackup") + "\n");
	    if(COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailAlertCC").length() > 0)
	    {
	        outputStream.print("<P> CC: " + COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailAlertCC") + "\n");
	    }
	    outputStream.print("<p>&nbsp;</p><TABLE border=\"0\" width=\"90%\"><tr><td>\n<input type=submit name=send value=\"Send\"> a test e-mail message</td><td width=\"25%\">&nbsp;</td>\n<td align=right><A HREF=" + getPageLink("mailPrefs", "") + ">Back to Mail Preferences</A></TD>" + "</TR></TABLE></FORM>\n");
	    printFooter(outputStream);
	}

    void testEmail()
    {
        outputStream.println("<CENTER><H2>Mail Test</H2></CENTER>\n<HR>\n");
        if(!isPortalServerRequest())
        {
            printContentStartComment();
            String s = COM.dragonflow.SiteView.Platform.productName + " test message";
            String s1 = "This is a test.  This is only a test.\n\n - " + COM.dragonflow.SiteView.Platform.productName;
            HashMap hashmap = getMasterConfig();
            String s3 = COM.dragonflow.Page.mailPrefsPage.getValue(hashmap, "_mailAlertCC");
            Array array = new Array();
            String s4 = COM.dragonflow.Utils.MailUtils.mail(hashmap, request.getValue("email"), s, s1, s3, array);
            if(s4.length() == 0)
            {
                outputStream.println("<h3>Mail Test Completed</h3><p>The test message was sent to " + request.getValue("email") + ".\n");
            } else
            {
                printBodyHeader("Mail Test");
                outputStream.println("<h3>Mail Test Completed</h3><p>The test message to " + request.getValue("email") + " could not be sent.\n" + "<p>\n" + "The error was: " + s4 + ".\n" + "<P>");
                if(request.getPermission("_support").equals("true"))
                {
                    outputStream.println(getPagePOST("support", "Send") + "<input type=submit\tvalue=\"Create Support Request\"><P>" + "If can't get your test e-mails to work, you can create a support request by\n" + "by clicking on the button above.\n" + "<input type=hidden name=attachPrefs value=true>" + "<input type=hidden name=format value=text>" + "<input type=hidden name=returnName value=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML("Back to Mail Preferences") + "\">" + "<input type=hidden name=returnURL value=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML(getPageLink("mailPrefs", "")) + "\">" + "<input type=hidden name=message value=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML("MAIL TEST FAILED\n\n" + s4) + "\"><BR>" + "</FORM>" + "<p>\n");
                }
            }
            outputStream.println("<p><PRE>\n");
            String s5;
            for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); outputStream.println(COM.dragonflow.Utils.TextUtils.escapeHTML(s5)))
            {
                s5 = (String)enumeration.nextElement();
            }

            outputStream.println("\n</PRE>");
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
        outputStream.println("<HR><A HREF=" + getAlertURL() + ">Back to Alerts</A><BR>\n" + "</BODY>\n");
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
                testEmail();
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
                if(s.equals("test"))
                {
                    testEmail();
                } else
                if(s.equals("saveAdditional"))
                {
                    saveAdditionalMailPrefs();
                } else
                {
                    printError("The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
                }
            } else
            if(s.equals("additionalMail"))
            {
                printAdditionalSettingForm();
            } else
            {
                printForm();
            }
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.mailPrefsPage mailprefspage = new mailPrefsPage();
        if(args.length > 0)
        {
            mailprefspage.args = args;
        }
        mailprefspage.handleRequest();
    }

}
