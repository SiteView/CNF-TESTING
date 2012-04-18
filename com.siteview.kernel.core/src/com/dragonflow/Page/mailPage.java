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

import com.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class mailPage extends com.dragonflow.Page.CGI
{

    public mailPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("receiveOnly");
        String s1 = request.getValue("useImap");
        String s2 = s.equals("receive") ? " selected" : "";
        String s3 = s.equals("send") ? " selected" : "";
        String s4 = s.length() != 0 ? "" : " selected";
        String s5 = s1.length() <= 0 ? "" : " selected";
        String s6 = s1.length() != 0 ? "" : " selected";
        String s7 = request.getValue("contentMatch");
        String s8 = request.getValue("to");
        String s9 = request.getValue("smtpServer");
        String s10 = request.getValue("popServer");
        String s11 = request.getValue("popAccount");
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        String s12 = com.dragonflow.Properties.StringProperty.getPrivate(request, "popPassword", "popSuff", stringbuffer, stringbuffer1);
        String s13 = request.getValue("timeout");
        String s14 = request.getValue("logDetails");
        String s15 = "";
        if(s14.length() > 0)
        {
            s15 = "CHECKED";
        }
        if(com.dragonflow.Utils.TextUtils.toInt(s13) == 0)
        {
            s13 = "300";
        }
        String s16 = request.getValue("delay");
        if(com.dragonflow.Utils.TextUtils.toInt(s16) == 0)
        {
            s16 = "10";
        }
        printBodyHeader("Send and Receive Mail Test");
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printButtonBar("Mail.htm", "");
        } else
        {
            outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
        }
        outputStream.println("<p>\n<CENTER><H2>Send and Receive Mail Test</H2></CENTER><P>\n<p>\n" + getPagePOST("mail", "") + "This form performs a roundtrip test of your mail server by sending a message and then retrieving it." + "<p><TABLE BORDER=0>\n" + "<TR><TD ALIGN=RIGHT>Message:</TD><TD><select size=1 name=receiveOnly>\n" + "<option value=\"\" " + s4 + ">Send and Receive\n" + "<option value=\"receive\" " + s2 + ">Receive Only\n" + "<option value=\"send\" " + s3 + ">Send Only\n" + "</select></TD></TR>" + "<TR><TD ALIGN=RIGHT>Send To Address:</TD><TD><input type=text name=to value=\"" + s8 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Sending Mail Server (SMTP):</TD><TD><input type=text name=smtpServer value=\"" + s9 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Receiving Server Type:</TD><TD><select size=1 name=useImap>\n" + "<option value=\"\" " + s6 + ">POP3\n" + "<option value=\"true\" " + s5 + ">IMAP4\n" + "</select></TD></TR>" + "<TR><TD ALIGN=RIGHT>Receiving Mail Server:</TD><TD><input type=text name=popServer value=\"" + s10 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Receiving Mail Server User Name:</TD><TD><input type=text name=popAccount value=\"" + s11 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Receiving Mail Server Password:</TD><TD>" + stringbuffer.toString() + " size=40></TD></TR>\n" + stringbuffer1.toString() + "<TR><TD></TD><TD></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Timeout:</TD><TD><input type=text name=timeout value=\"" + s13 + "\" size=10></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Retrieve Pause:</TD><TD><input type=text name=delay value=\"" + s16 + "\" size=10></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Content Match:</TD><TD><input type=text name=contentMatch value=\"" + s7 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Show Details:</TD><TD><input type=checkbox name=logDetails " + s15 + " size=10></TD></TR>\n" + "</TABLE><p>\n" + "<input type=submit value=\"Check Mail Server\" class=\"VerBl8\">\n" + "</FORM>\n");
        if(s10.length() > 0 || s.equals("send"))
        {
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                boolean flag = s.equals("receive");
                boolean flag1 = s.equals("send");
                String s18 = "This is a test message (" + java.lang.System.currentTimeMillis() % 100L + ") from the " + com.dragonflow.SiteView.Platform.productName + " Mail Tool page.";
                outputStream.println("<pre>");
                java.lang.Object aobj[] = com.dragonflow.StandardMonitor.MailMonitor.roundTripMail(null, outputStream, s8, s9, s10, s11, s12, s18, com.dragonflow.Utils.TextUtils.toInt(s16), com.dragonflow.Utils.TextUtils.toInt(s13), s14.length() != 0, flag, flag1, s1.length() > 0, s7, "", "", "");
                outputStream.println("</pre>");
                int i = ((java.lang.Long)aobj[0]).intValue();
                String s19 = (String)aobj[1];
                String s20 = (String)aobj[2];
                if(s19.length() != 0)
                {
                    outputStream.println("<h3>The message could not be sent: " + s19 + "</h3>");
                } else
                if(s20.length() != 0)
                {
                    outputStream.println("<h3>The message was sent but not retrieved: " + s20 + "</h3>");
                } else
                if(!flag && !flag1)
                {
                    outputStream.println("<h3>Message sent and retrieved: " + com.dragonflow.Utils.TextUtils.floatToString((float)i / 1000F, 2) + " sec" + "</h3>");
                } else
                if(flag)
                {
                    outputStream.println("<h3>Message retrieved: " + com.dragonflow.Utils.TextUtils.floatToString((float)i / 1000F, 2) + " sec" + "</h3>");
                } else
                {
                    outputStream.println("<h3>Message sent: " + com.dragonflow.Utils.TextUtils.floatToString((float)i / 1000F, 2) + " sec" + "</h3>");
                }
                printContentEndComment();
            } else
            {
                com.dragonflow.SiteView.PortalSiteView portalsiteview = (com.dragonflow.SiteView.PortalSiteView)getSiteView();
                if(portalsiteview != null)
                {
                    String s17 = portalsiteview.getURLContentsFromRemoteSiteView(request, "_centrascopeToolMatch");
                    outputStream.println(s17);
                }
            }
        }
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printFooter(outputStream);
        } else
        {
            outputStream.println("</BODY>");
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.mailPage mailpage = new mailPage();
        if(args.length > 0)
        {
            mailpage.args = args;
        }
        mailpage.handleRequest();
    }
}
