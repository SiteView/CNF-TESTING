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

public class telnetPage extends com.dragonflow.Page.CGI
{

    public telnetPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("server");
        String s1 = request.getValue("username");
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        String s2 = com.dragonflow.Properties.StringProperty.getPrivate(request, "password", "tNetSuff", stringbuffer, stringbuffer1);
        String s3 = request.getValue("command");
        String s4 = request.getValue("loginPrompt");
        if(s4.length() == 0)
        {
            s4 = com.dragonflow.StandardMonitor.TelnetMonitor.pLoginPrompt.getDefault();
        }
        String s5 = request.getValue("passwordPrompt");
        if(s5.length() == 0)
        {
            s5 = com.dragonflow.StandardMonitor.TelnetMonitor.pPasswordPrompt.getDefault();
        }
        String s6 = request.getValue("prompt");
        if(s6.length() == 0)
        {
            s6 = com.dragonflow.StandardMonitor.TelnetMonitor.pPrompt.getDefault();
        }
        printBodyHeader("Check Telnet Server");
        printButtonBar("Telnet.htm", "");
        outputStream.println("<p>\n<CENTER><H2>Check Telnet Server</H2></CENTER><P>\n<p>\n<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\nThis form will run a command on a remote machine via Telnet.  Enter the server (in the form\ndemo." + com.dragonflow.SiteView.Platform.exampleDomain + "), the command, and a user name and password\n" + "for that server.\n" + "<TABLE BORDER=0>\n" + "<TR><TD ALIGN=RIGHT>Telnet server:</TD><TD><input type=text name=server value=\"" + s + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>User name:</TD><TD><input type=text name=username value=\"" + s1 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Password:</TD><TD>" + stringbuffer.toString() + " size=30></TD></TR>\n" + stringbuffer1.toString() + "<TR><TD ALIGN=RIGHT>Command:</TD><TD><input type=text name=command value=\"" + s3 + "\" size=60></TD></TR>\n" + "<TR><TD></TD><TD></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Login Prompt:</TD><TD><input type=text name=loginPrompt value=\"" + s4 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Password Prompt:</TD><TD><input type=text name=passwordPrompt value=\"" + s5 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Prompt:</TD><TD><input type=text name=prompt value=\"" + s6 + "\" size=30></TD></TR>\n" + "</TABLE><p>\n" + "<input type=hidden name=page value=telnet>\n" + "<input type=hidden name=account value=\"" + request.getAccount() + "\">\n" + "<input type=submit value=\"Check Telnet Server\">\n" + "</FORM>\n");
        if(s.length() > 0)
        {
            outputStream.println("<PRE><B>Contacting Telnet server at " + s + "...</B>\n");
            long l = com.dragonflow.StandardMonitor.URLMonitor.DEFAULT_TIMEOUT;
            long l1 = com.dragonflow.SiteView.Platform.timeMillis();
            long l2 = l + com.dragonflow.SiteView.Platform.timeMillis();
            StringBuffer stringbuffer2 = new StringBuffer();
            int ai[] = com.dragonflow.StandardMonitor.TelnetMonitor.checkTelnet(s, s1, s2, s3, s4, s5, s6, stringbuffer2, l2, outputStream);
            int i = ai[0];
            long l3 = com.dragonflow.SiteView.Platform.timeMillis() - l1;
            String s7;
            if(i == com.dragonflow.StandardMonitor.TelnetMonitor.kURLok)
            {
                String s8 = com.dragonflow.Utils.TextUtils.floatToString((float)l3 / 1000F, 2) + " sec";
                s7 = "ok, " + s8;
            } else
            {
                s7 = com.dragonflow.StandardMonitor.URLMonitor.lookupStatus(i);
            }
            outputStream.print("\n<B>Result: " + s7 + "</B>\n\n");
            if(i == com.dragonflow.StandardMonitor.TelnetMonitor.kURLok)
            {
                outputStream.println("<B>Results");
                if(s3.length() > 0)
                {
                    outputStream.print(" of " + s3);
                }
                outputStream.println("</B>" + com.dragonflow.Utils.TextUtils.escapeHTML(stringbuffer2.toString()));
            }
            outputStream.print("</PRE>\n");
        }
        printFooter(outputStream);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.telnetPage telnetpage = new telnetPage();
        if(args.length > 0)
        {
            telnetpage.args = args;
        }
        telnetpage.handleRequest();
    }
}
