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

import com.dragonflow.SiteView.PortalSync;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class portalOpPage extends com.dragonflow.Page.CGI
{

    public portalOpPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        if(s.equals("Sync"))
        {
            doSync();
        } else
        {
            printError("The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
        }
    }

    public void doSync()
    {
        outputStream.println("<PRE>");
        com.dragonflow.SiteView.PortalSync portalsync = new PortalSync(outputStream);
        portalsync.sync();
        String s = request.getValue("rview");
        outputStream.println("</PRE>");
        outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=portal&view=" + s + "&account=" + request.getAccount() + ">\n" + "Return to Portal</A>");
    }

    public static void main(String args[])
    {
        (new portalOpPage()).handleRequest();
    }
}
