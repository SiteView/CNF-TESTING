/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.Utils.Braf;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class supportPage extends com.dragonflow.Page.CGI
{

    public supportPage()
    {
    }

    public static void printShortForm(java.io.PrintWriter printwriter, String s, StringBuffer stringbuffer)
    {
        printwriter.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView>\n<p>Send this error to SiteView Support by using the <B>Send Support Request</B> button.</p>\n<br><br><B>Message:</B><BR>\n<textarea cols=70 rows=8 name=message></textarea><DL><DT><B>Reply-to:</B> <input type=text name=cc size=40><BR>\n<DD>Send reply back to this email address.<P>\n</DL><input type=submit\tvalue=\"Send Support Request\"><P><input type=hidden name=page value=support><input type=hidden name=account value=" + s + ">" + "<input type=hidden name=attachPrefs value=true>" + "<input type=hidden name=operation value=Send>" + "<input type=hidden name=message2 value=\"" + com.dragonflow.Utils.TextUtils.escapeHTML(stringbuffer.toString()) + "\"><BR>" + "</FORM>" + "<p>\n" + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView>\n" + "To generate a message to send to SiteView Support, use the <B>Create Support Request</B> button.\n" + "<input type=submit\tvalue=\"Create Support Request\"><P>" + "<input type=hidden name=page value=support>" + "<input type=hidden name=account value=" + s + ">" + "<input type=hidden name=operation value=Send>" + "<input type=hidden name=format value=text>" + "<input type=hidden name=message value=\"" + com.dragonflow.Utils.TextUtils.escapeHTML(stringbuffer.toString()) + "\"><BR>" + "</FORM>" + "<p>\n");
    }

    void printMessageForm()
    {
        String s = "Support Request";
        printBodyHeader(s);
        printButtonBar("SupportRequest.htm", "");
        outputStream.println("<h2>" + com.dragonflow.SiteView.Platform.productName + " " + s + "</h2>\n");
        outputStream.println("<p>Use this form for submitting a support request or forwarding " + com.dragonflow.SiteView.Platform.productName + " files to Dragonflow Technical Support.\n");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=page value=support>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=operation value=Send>\n");
        String s1 = request.getValue("allowLogin").length() <= 0 ? "" : "checked";
        String s2 = request.getValue("attachPrefs").length() <= 0 ? "" : "checked";
        String s3 = request.getValue("attachGroups").length() <= 0 ? "" : "checked";
        String s4 = request.getValue("attachErrorLog").length() <= 0 ? "" : "checked";
        String s5 = request.getValue("attachAlertLog").length() <= 0 ? "" : "checked";
        String s6 = request.getValue("attachRunMonitorLog").length() <= 0 ? "" : "checked";
        String s7 = request.getValue("attachSiteViewLog").length() <= 0 ? "" : "checked";
        String s8 = request.getValue("attachYestSiteViewLog").length() <= 0 ? "" : "checked";
        outputStream.println("<BLOCKQUOTE><DL>\n<DT><B>Reply-to:</B> <input type=text name=cc size=40><BR>\n<DD>Send reply back to this email address.<P>\n<DT><B>Message Text:</B><BR>\n<DD><textarea cols=70 rows=8 name=message>" + request.getValue("message") + "</textarea><BR>\n" + "<DT><input type=checkbox name=allowLogin " + s1 + ">" + com.dragonflow.SiteView.Platform.productName + " can be accessed over the Internet<BR>\n" + "<DD>Check this box if your " + com.dragonflow.SiteView.Platform.productName + " is available over the Internet, and you can allow a " + com.dragonflow.SiteView.Platform.productName + " customer support engineer to directly access it.\n" + "(recommended for quickest resolution)\n" + "</DL></BLOCKQUOTE>\n" + "<H3>Message Options</H3>\n" + "<p>Select the " + com.dragonflow.SiteView.Platform.productName + " configuration and log files you want to send with this e-mail message.\n" + "<BLOCKQUOTE><DL>\n" + "<DT><B>Attachments:</B>\n" + "<DD><TABLE>\n" + "<TR><TD><input type=checkbox name=attachPrefs " + s2 + ">Attach Preferences (General, Mail, Pager, etc.)<BR>\n" + "<TR><TD><input type=checkbox name=attachGroups " + s3 + ">Attach Monitor Configurations<BR>\n" + "<TR><TD><input type=checkbox name=attachErrorLog " + s4 + ">Attach Error Log &nbsp;&nbsp;&nbsp;(attach last <input type=text size=5 name=attachErrorLogSize value=10>K of the file)<BR>\n" + "<TR><TD><input type=checkbox name=attachAlertLog " + s5 + ">Attach Alert Log &nbsp;&nbsp;&nbsp;(attach last <input type=text size=5 name=attachAlertLogSize value=10>K of the file)<BR>\n" + "<TR><TD><input type=checkbox name=attachRunMonitorLog " + s6 + ">Attach Run Log &nbsp;&nbsp;&nbsp;(attach last <input type=text size=5 name=attachRunMonitorLogSize value=10>K of the file)<BR>\n" + "<TR><TD><input type=checkbox name=attachSiteViewLog " + s7 + ">Attach Monitoring Log &nbsp;&nbsp;&nbsp;(attach last <input type=text size=5 name=attachSiteViewLogSize value=10>K of the file)<BR>\n" + "<TR><TD><input type=checkbox name=attachYestSiteViewLog " + s8 + ">Attach Yesterday's Monitoring Log &nbsp;&nbsp;&nbsp;(attach last <input type=text size=5 name=attachYestSiteViewLogSize value=10>K of the file)<BR>\n" + "</TABLE>\n" + "</DL></BLOCKQUOTE>\n" + "<P><input type=submit name=send value=\"Send Support Request\"><P>\n" + "</FORM>");
        printFooter(outputStream);
    }

    void printSentMail()
    {
        String s = "Sending Support Request";
        printBodyHeader(s);
        printButtonBar("NoDoc.htm", "");
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s1 = siteviewgroup.getSetting("_fromAddress");
        String s2 = request.getValue("cc");
        if(s2.length() > 0)
        {
            s1 = s2;
        }
        String s3 = "";
        String s4 = request.getPermission("_partner");
        if(s4.length() > 0)
        {
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s4);
            if(monitorgroup != null)
            {
                s3 = monitorgroup.getProperty("_partnerSupportAddress");
            }
        }
        if(s3.length() == 0)
        {
            s3 = com.dragonflow.SiteView.Platform.supportEmail;
        }
        outputStream.println("<P><BR><BR>Creating support request...</P><br>");
        String s5 = "----" + com.dragonflow.SiteView.Platform.productName + " " + com.dragonflow.SiteView.Platform.timeMillis();
        String s6 = com.dragonflow.SiteView.Platform.productName + " Support Request from " + s1 + " [attachments]" + s5;
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("This is a multi-part message in MIME format.\n");
        stringbuffer.append("\n");
        stringbuffer.append("--" + s5 + "\n");
        stringbuffer.append("Content-Type: text/plain; charset=us-ascii\n");
        stringbuffer.append("Content-Transfer-Encoding: 7bit\n");
        stringbuffer.append("\n");
        stringbuffer.append("message: " + request.getValue("message") + "\n" + request.getValue("message2"));
        stringbuffer.append("\n");
        stringbuffer.append("email=" + siteviewgroup.getSetting("_autoEmail") + "\n");
        stringbuffer.append("license: " + com.dragonflow.Utils.LUtils.getLicenseKey() + "\n");
        stringbuffer.append("version: " + com.dragonflow.SiteView.Platform.getVersion() + "\n");
        if(request.getValue("allowLogin").length() > 0)
        {
            stringbuffer.append("remoteLogin: permission granted\n");
            request.setValue("attachPrefs", "true");
        } else
        {
            stringbuffer.append("remoteLogin: no\n");
        }
        stringbuffer.append("os: " + java.lang.System.getProperty("os.name") + " " + java.lang.System.getProperty("os.version") + "\n");
        stringbuffer.append("date: " + com.dragonflow.Utils.TextUtils.prettyDate() + "\n");
        stringbuffer.append("adminName: " + siteviewgroup.getSetting("_adminName") + "\n");
        stringbuffer.append("account: " + request.getValue("account") + "\n");
        stringbuffer.append("serverName: " + siteviewgroup.getSetting("_webserverAddress") + "\n");
        stringbuffer.append("serverPort: " + siteviewgroup.getSetting("_httpActivePort") + "\n");
        stringbuffer.append("serverPreferredPort: " + siteviewgroup.getSetting("_httpPort") + "\n");
        stringbuffer.append("remoteNTServers: " + com.dragonflow.SiteView.ServerMonitor.remoteNTServers() + "\n");
        stringbuffer.append("remoteUnixServers: " + com.dragonflow.SiteView.ServerMonitor.remoteUnixServers() + "\n");
        stringbuffer.append("server: " + com.dragonflow.SiteView.Platform.getDefaultIPAddress() + "\n");
        stringbuffer.append("groups: " + siteviewgroup.getGroupCount() + "\n");
        stringbuffer.append("activeMonitors: " + siteviewgroup.getMonitorCount() + "\n");
        stringbuffer.append("disabledMonitors: " + siteviewgroup.getDisabledMonitorCount() + "\n");
        stringbuffer.append("sysinfo: " + com.dragonflow.SiteView.Platform.getSysInfo() + "\n");
        stringbuffer.append("installed: " + siteviewgroup.getSetting("_installed") + "\n");
        stringbuffer.append("installDate: " + siteviewgroup.getSetting("_installTime") + "\n");
        if(request.getValue("attachPrefs").length() > 0)
        {
            String s7 = com.dragonflow.SiteView.Platform.getRoot() + "/groups/";
            String s14 = "master.config";
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s14, s7, s5));
            s14 = "history.config";
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s14, s7, s5));
            s14 = "multi.config";
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s14, s7, s5));
            s7 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            s14 = "monitorCount.log";
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s14, s7, s5));
        }
        if(request.getValue("attachGroups").length() > 0)
        {
            String s8 = com.dragonflow.SiteView.Platform.getRoot() + "/groups/";
            java.io.File file = new File(s8);
            String as[] = file.list();
            if(as != null)
            {
                for(int i = 0; i < as.length; i++)
                {
                    if(as[i].endsWith(".mg"))
                    {
                        stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(as[i], s8, s5));
                    }
                }

            }
        }
        if(request.getValue("attachErrorLog").length() > 0)
        {
            String s9 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            String s15 = "error.log";
            long l1 = com.dragonflow.Utils.TextUtils.toLong(request.getValue("attachErrorLogSize"));
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s15, s9, s5, l1));
        }
        if(request.getValue("attachAlertLog").length() > 0)
        {
            String s10 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            String s16 = "alert.log";
            long l2 = com.dragonflow.Utils.TextUtils.toLong(request.getValue("attachAlertLogSize"));
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s16, s10, s5, l2));
        }
        if(request.getValue("attachRunMonitorLog").length() > 0)
        {
            String s11 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            String s17 = "RunMonitor.log";
            long l3 = com.dragonflow.Utils.TextUtils.toLong(request.getValue("attachRunMonitorLogSize"));
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s17, s11, s5, l3));
        }
        if(request.getValue("attachSiteViewLog").length() > 0)
        {
            String s12 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            String s18 = "SiteView" + com.dragonflow.Utils.TextUtils.dayToFileName() + ".log";
            long l4 = com.dragonflow.Utils.TextUtils.toLong(request.getValue("attachSiteViewLogSize"));
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s18, s12, s5, l4));
        }
        if(request.getValue("attachYestSiteViewLog").length() > 0)
        {
            String s13 = com.dragonflow.SiteView.Platform.getRoot() + "/logs/";
            long l = com.dragonflow.SiteView.Platform.makeDate().getTime() - (long)(com.dragonflow.Utils.TextUtils.DAY_SECONDS * 1000);
            String s20 = "SiteView" + com.dragonflow.Utils.TextUtils.dayToFileName(new Date(l)) + ".log";
            long l5 = com.dragonflow.Utils.TextUtils.toLong(request.getValue("attachYestSiteViewLogSize"));
            stringbuffer.append(com.dragonflow.Page.supportPage.mimeAttachFile(s20, s13, s5, l5));
        }
        stringbuffer.append("\n--" + s5 + "--\n");
        if(request.getValue("format").equals("text"))
        {
            outputStream.println("<p>To submit your support request, copy the message below and e-mail it\nto <A HREF=\"mailto:" + s3 + "\">" + s3 + "</A>");
            if(request.getValue("returnName").length() > 0)
            {
                outputStream.println("<P><A HREF=\"" + request.getValue("returnURL") + "\">" + request.getValue("returnName") + "</A></p>");
            }
            outputStream.println("<HR><PRE>\n");
            outputStream.println(com.dragonflow.Utils.TextUtils.escapeHTML(stringbuffer.toString()));
            outputStream.println("</PRE><HR>\n");
        } else
        {
            outputStream.println("<p>Sending support request to " + s3 + " from " + s1 + "...</P>");
            HashMap hashmap = new HashMap(getMasterConfig());
            hashmap.put("_fromAddress", s1);
            hashmap.put("_hideServerInSubject", "true");
            String s19 = stringbuffer.toString();
            Array array = new Array();
            String s21 = com.dragonflow.Utils.MailUtils.mail(hashmap, s3, s6, s19, s2, array, false);
            if(s21.length() == 0)
            {
                outputStream.println("<B>Support Request Sent</B>\n<p>For customers who have purchased Customer Care, we typically answer questions within one business day.<p>For evaluation licenses, we typically answer questions within two business days.");
            } else
            {
                outputStream.println("<B>Support Request Not Sent</B>\n<HR>\n<br>The support request was NOT sent.  Please contact us by sending email to " + s3 + "\n" + "<p>\n" + "The error was: " + s21 + ".<P>\n" + "The detail of the mail attempt was:\n" + "<HR><PRE>\n");
                for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); outputStream.println(enumeration.nextElement())) { }
                outputStream.println("\n</PRE><HR><P>\n");
            }
            if(request.getValue("returnName").length() > 0)
            {
                outputStream.println("<A HREF=\"" + request.getValue("returnURL") + "\">" + request.getValue("returnName") + "</A>");
            }
        }
        printFooter(outputStream);
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_support"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("operation");
        if(s.equals("Send"))
        {
            printSentMail();
        } else
        if(s.equals(""))
        {
            printMessageForm();
        } else
        {
            printError("<p>The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
        }
    }

    public static StringBuffer mimeAttachFile(String s, String s1, String s2)
    {
        return com.dragonflow.Page.supportPage.mimeAttachFile(s, s1, s2, -1L);
    }

    public static StringBuffer mimeAttachFile(String s, String s1, String s2, long l)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s3 = s1 + s;
        stringbuffer.append("\n--" + s2 + "\n");
        stringbuffer.append("Content-Type: text/plain; charset=us-ascii\n");
        stringbuffer.append("Content-Transfer-Encoding: 7bit\n");
        stringbuffer.append("Content-Disposition: attachment;\n");
        stringbuffer.append("     filename=\"" + s + "\"\n");
        stringbuffer.append("\n");
        try
        {
            if(l < 0L)
            {
                StringBuffer stringbuffer1 = com.dragonflow.Utils.FileUtils.readFile(s3);
                stringbuffer.append(stringbuffer1.toString());
            } else
            {
                java.io.File file = new File(s3);
                long l1 = file.length();
                l *= 1024L;
                com.dragonflow.Utils.Braf braf = new Braf(file.getAbsolutePath(), l1 - l);
                String s4 = braf.readLine();
                do
                {
                    if(s4 == null)
                    {
                        break;
                    }
                    s4 = braf.readLine();
                    if(s4 == null)
                    {
                        break;
                    }
                    stringbuffer.append(s4);
                    stringbuffer.append("\n");
                } while(true);
                braf.close();
            }
        }
        catch(java.io.IOException ioexception)
        {
            stringbuffer.append("Could not read " + s3 + ": " + ioexception);
        }
        return stringbuffer;
    }

    public static void main(String args[])
    {
        com.dragonflow.Page.supportPage supportpage = new supportPage();
        if(args.length > 0)
        {
            supportpage.args = args;
        }
        supportpage.handleRequest();
    }
}
