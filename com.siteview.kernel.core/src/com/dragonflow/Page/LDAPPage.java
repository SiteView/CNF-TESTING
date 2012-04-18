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

public class LDAPPage extends com.dragonflow.Page.CGI
{

    public LDAPPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        String s = com.dragonflow.Properties.StringProperty.getPrivate(request, "securityCredential", "ldapSuff", stringbuffer, stringbuffer1);
        String s1 = request.getValue("urlProvider");
        String s2 = request.getValue("securityPrincipal");
        String s3 = request.getValue("ldapQuery");
        String s4 = request.getValue("ldapFilter");
        printBodyHeader("Authenticate a user of a LDAP server.");
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printButtonBar("LDAP.htm", "");
        } else
        {
            outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
        }
        outputStream.println("<p>\n<CENTER><H2>LDAP Authentication Test</H2></CENTER><P>\n<p>\n" + getPagePOST("LDAP", "") + "This form performs a user authentication of a LDAP server." + "<p><TABLE BORDER=0>\n" + "<TR><TD ALIGN=RIGHT>Security Principal:</TD><TD><input type=text name=securityPrincipal value=\"" + s2 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Security Credential:</TD><TD>" + stringbuffer.toString() + " size=40></TD></TR>\n" + stringbuffer1.toString() + "<TR><TD ALIGN=RIGHT>URL Provider Address:</TD><TD><input type=text name=urlProvider value=\"" + s1 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>LDAP Query:</TD><TD><input type=text name=ldapQuery value=\"" + s3 + "\" size=40></TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Search Filter:</TD><TD><input type=text name=ldapFilter value=\"" + s4 + "\" size=40></TD></TR>\n" + "<TR><TD></TD><TD></TD></TR>\n" + "</TABLE><p>\n" + "<input type=submit value=\"Authenticate User\" class=\"VerBl8\">\n" + "</FORM>\n");
        if(s2.length() > 0)
        {
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                String as[] = com.dragonflow.StandardMonitor.LDAPMonitor.authenticate(s1, s2, s, s3, s4);
                outputStream.println("</pre>");
                if(as[0].length() > 0)
                {
                    outputStream.println("<h3>Authentication of the user: " + s2 + " failed.  " + as[0] + "</h3>");
                } else
                {
                    outputStream.println("<h3>Authentication of the user: " + s2 + " succeeded.   " + as[1] + "</h3>");
                }
                printContentEndComment();
            } else
            {
                com.dragonflow.SiteView.PortalSiteView portalsiteview = (com.dragonflow.SiteView.PortalSiteView)getSiteView();
                if(portalsiteview != null)
                {
                    String s5 = portalsiteview.getURLContentsFromRemoteSiteView(request, "_centrascopeToolMatch");
                    outputStream.println(s5);
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
        com.dragonflow.Page.LDAPPage ldappage = new LDAPPage();
        if(args.length > 0)
        {
            ldappage.args = args;
        }
        ldappage.handleRequest();
    }
}
