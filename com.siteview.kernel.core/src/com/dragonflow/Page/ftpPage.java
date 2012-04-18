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

import com.recursionsw.jgl.HashMap;

import com.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class ftpPage extends com.dragonflow.Page.CGI
{

    public ftpPage()
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
        String s2 = com.dragonflow.Properties.StringProperty.getPrivate(request, "password", "ftpSuff", stringbuffer, stringbuffer1);
        String s3 = request.getValue("filename");
        boolean flag = request.getValue("passive").length() > 0;
        String s4 = "";
        if(flag)
        {
            s4 = "checked";
        }
        String s5 = request.getValue("proxy");
        String s6 = request.getValue("proxyUser");
        StringBuffer stringbuffer2 = new StringBuffer();
        StringBuffer stringbuffer3 = new StringBuffer();
        String s7 = com.dragonflow.Properties.StringProperty.getPrivate(request, "proxyPassword", "pFtpSuff", stringbuffer2, stringbuffer3);
        printBodyHeader("Check FTP Server");
        String s8 = new String("");
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printButtonBar("FTP.htm", "");
            s8 = com.dragonflow.SiteView.Platform.exampleDomain;
        } else
        {
            outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
            s8 = "merc-int.com";
        }
        outputStream.println("<p>\n<CENTER><H2>Check FTP Server</H2></CENTER><P>\n<p>\n" + getPagePOST("ftp", "") + "This form will send a file retrieval request to an FTP server.  Enter the server " + "(for example, ftp." + s8 + "), the name of the file to retrieve, and a user name (usually \"anonymous\") and password\n" + "(usually an e-mail address) for that server.\n" + "<TABLE BORDER=\"0\">" + "<TR><TD ALIGN=RIGHT>FTP server:</TD><TD><input type=text name=server value=\"" + s + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>File name:</TD><TD><input type=text name=filename value=\"" + s3 + "\" size=60></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>User name:</TD><TD><input type=text name=username value=\"" + s1 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Password:</TD><TD>" + stringbuffer.toString() + " size=30></TD></TR>\n" + stringbuffer1.toString() + "<TR><TD></TD><TD></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Use Passive:</TD><TD><input type=checkbox name=passive value=checked " + s4 + "></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Proxy (optional):</TD><TD><input type=text name=proxy value=\"" + s5 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Proxy User Name(optional):</TD><TD><input type=text name=proxyUser value=\"" + s6 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Proxy Password (optional):</TD><TD>" + stringbuffer2.toString() + " size=30></TD></TR>\n" + stringbuffer3.toString() + "</TABLE><P>\n" + "<input type=submit value=\"Check FTP Server\" class=\"VerBl8\">\n" + "</FORM>\n");
        if(s.length() > 0)
        {
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                outputStream.println("<PRE><B>Contacting FTP server at " + s + "...</B>\n");
                int i = com.dragonflow.StandardMonitor.URLMonitor.DEFAULT_TIMEOUT;
                long l = com.dragonflow.SiteView.Platform.timeMillis();
                StringBuffer stringbuffer4 = new StringBuffer();
                StringBuffer stringbuffer5 = new StringBuffer();
                HashMap hashmap = getMasterConfig();
                long l1 = -1L;
                long l2 = com.dragonflow.Utils.TextUtils.toLong(com.dragonflow.Page.ftpPage.getValue(hashmap, "_ftpDownloadLimit"));
                if(com.dragonflow.Page.ftpPage.getValue(hashmap, "_urlContentMatchMax").length() > 0)
                {
                    l1 = com.dragonflow.Utils.TextUtils.toLong(com.dragonflow.Page.ftpPage.getValue(hashmap, "_urlContentMatchMax"));
                }
                if(l1 < 1L)
                {
                    l1 = 50000L;
                }
                long al[] = com.dragonflow.StandardMonitor.FTPMonitor.checkFTP(s, s5, s6, s7, flag, s3, s1, s2, "", stringbuffer5, l1, stringbuffer4, i, l2, outputStream, null);
                long l3 = al[0];
                long l4 = al[2];
                long l5 = com.dragonflow.SiteView.Platform.timeMillis() - l;
                String s10;
                if(l3 == (long)com.dragonflow.StandardMonitor.FTPMonitor.kURLok)
                {
                    String s11 = com.dragonflow.Utils.TextUtils.floatToString((float)l5 / 1000F, 2) + " sec";
                    s10 = "ok, " + s11 + ", " + com.dragonflow.Utils.TextUtils.bytesToString(l4);
                } else
                if(l3 == (long)com.dragonflow.StandardMonitor.FTPMonitor.kMonitorSpecificError)
                {
                    s10 = stringbuffer4.toString();
                } else
                {
                    s10 = com.dragonflow.StandardMonitor.URLMonitor.lookupStatus(l3);
                }
                outputStream.print("\n<B>Result: " + s10 + "</B>\n\n");
                if(l3 == (long)com.dragonflow.StandardMonitor.FTPMonitor.kURLok)
                {
                    outputStream.println("<B>Contents of " + s3 + "</B>\n\n" + com.dragonflow.Utils.TextUtils.escapeHTML(com.dragonflow.Utils.I18N.StringToUnicode(stringbuffer5.toString(), com.dragonflow.Utils.I18N.nullEncoding())));
                }
                outputStream.print("</PRE>\n");
                printContentEndComment();
            } else
            {
                com.dragonflow.SiteView.PortalSiteView portalsiteview = (com.dragonflow.SiteView.PortalSiteView)getSiteView();
                if(portalsiteview != null)
                {
                    String s9 = portalsiteview.getURLContentsFromRemoteSiteView(request, "_centrascopeToolMatch");
                    outputStream.println(s9);
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
        com.dragonflow.Page.ftpPage ftppage = new ftpPage();
        if(args.length > 0)
        {
            ftppage.args = args;
        }
        ftppage.handleRequest();
    }
}
