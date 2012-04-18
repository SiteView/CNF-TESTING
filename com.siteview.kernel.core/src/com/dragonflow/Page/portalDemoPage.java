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
import com.dragonflow.Utils.CommandLine;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class portalDemoPage extends com.dragonflow.Page.CGI
{

    public portalDemoPage()
    {
    }

    void doReset()
    {
        outputStream.println("<p>Copying Demo files...<p>");
        outputStream.flush();
        try
        {
            com.dragonflow.Utils.CommandLine commandline = new CommandLine();
            java.lang.System.out.println("Runing portal reset bat file ");
            commandline.exec("resetPortal.bat");
            java.lang.System.out.println("portal reset bat file finshed");
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
        outputStream.println("<p>Updating SiteView pages...<p>");
        outputStream.flush();
        try
        {
            HashMap hashmap = getMasterConfig();
            com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            siteviewgroup.startingUp = true;
            saveMasterConfig(hashmap);
            siteviewgroup.startingUp = false;
        }
        catch(java.lang.Exception exception1) { }
        outputStream.println("<p>Click <a href=\"http:/SiteView/cgi/go.exe/SiteView?page=portal&account=administrator\">here</a> to launch application</p>\n");
        outputStream.println("</BODY>\n");
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_demo"))
        {
            throw new HTTPRequestException(557);
        }
        if(request.isPost())
        {
            doReset();
        } else
        {
            printBodyHeader("Reset Demo");
            outputStream.println("<CENTER><H2>Reset Demo</H2></CENTER><P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>This form will reset the CentraScope demo back to its default configuration.<CENTER><p><input type=hidden name=page value=\"portalDemo\"><input type=hidden name=account value=\"administrator\"><input type=hidden name=operation value=\"setup\"><input type=submit value=\"Reset\"> Demo</CENTER></FORM>");
            printFooter(outputStream);
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        (new portalDemoPage()).handleRequest();
    }
}
