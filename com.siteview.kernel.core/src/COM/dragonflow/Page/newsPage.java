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

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class newsPage extends COM.dragonflow.Page.CGI
{

    public newsPage()
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
        String s2 = COM.dragonflow.Utils.TextUtils.enlighten(request.getValue("password"));
        String s3 = request.getValue("newsgroups");
        printBodyHeader("Check News Server");
        String s4 = new String("");
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printButtonBar("News.htm", "");
            s4 = COM.dragonflow.SiteView.Platform.exampleDomain;
        } else
        {
            outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
            s4 = "merc-int.com";
        }
        outputStream.println("<p>\n<CENTER><H2>Check News Server</H2></CENTER><P>\n<p>\n" + getPagePOST("news", "") + "News clients access news groups by making requests to news servers (using the NNTP protocol).\n" + "The Server specifies which\n" + "server to contact.\n" + "This form will send informational requests to a News server.  Enter the server (in the form\n" + "news." + s4 + " or news." + s4 + ":7777).\n" + "<P>You may optionally provide a one or more news groups (separated by commas), and/or a user name and password.\n" + "<TABLE BORDER=\"0\">\n" + "<TR><TD ALIGN=RIGHT>News server:</TD><TD><input type=text name=server value=\"" + s + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>News groups (optional):</TD><TD><input type=text name=newsgroups value=\"" + s3 + "\" size=60></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>User name (optional):</TD><TD><input type=text name=username value=\"" + s1 + "\" size=30></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Password (optional):</TD><TD><input type=text name=password value=\"" + s2 + "\" size=30></TD></TR>\n" + "</TABLE>\n" + "<p>\n" + "<input type=submit value=\"Check News Group\" class=\"VerBl8\">\n" + "</FORM>\n");
        if(s.length() > 0)
        {
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                outputStream.println("<PRE>Contacting news server at " + s + "...\n");
                String s5 = COM.dragonflow.StandardMonitor.NewsMonitor.newsStatus(s, s1, s2, s3, outputStream);
                outputStream.print("\nResult: " + s5 + "\n\n" + "</PRE>\n");
                printContentEndComment();
            } else
            {
                COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)getSiteView();
                if(portalsiteview != null)
                {
                    String s6 = portalsiteview.getURLContentsFromRemoteSiteView(request, "_centrascopeToolMatch");
                    outputStream.println(s6);
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
        COM.dragonflow.Page.newsPage newspage = new newsPage();
        if(args.length > 0)
        {
            newspage.args = args;
        }
        newspage.handleRequest();
    }
}
