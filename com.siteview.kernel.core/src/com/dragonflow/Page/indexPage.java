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
import java.io.FileOutputStream;

import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class indexPage extends com.dragonflow.Page.CGI
{

    public indexPage()
    {
    }

    boolean hasGroups()
    {
        java.io.File file = new File(com.dragonflow.SiteView.Platform.getRoot() + "/groups");
        if(file.exists() && file.isDirectory())
        {
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                if(as[i].endsWith(".mg") && !com.dragonflow.SiteView.Health.isInHealth(as[i]))
                {
                    return true;
                }
            }

        }
        return false;
    }

    boolean hasSiteViews()
    {
        java.io.File file = new File(com.dragonflow.SiteView.Platform.getRoot() + "/portal");
        if(file.exists() && file.isDirectory())
        {
            String as[] = file.list();
            if(as != null)
            {
                for(int i = 0; i < as.length; i++)
                {
                    java.io.File file1 = new File(file, as[i]);
                    if(file1.isDirectory())
                    {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    boolean testWrite(String s)
    {
        boolean flag = true;
        try
        {
            java.io.FileOutputStream fileoutputstream = new FileOutputStream(s);
            fileoutputstream.write(65);
            fileoutputstream.close();
        }
        catch(java.lang.Exception exception)
        {
            flag = false;
        }
        try
        {
            (new File(s)).delete();
        }
        catch(java.lang.Exception exception1) { }
        return flag;
    }

    public void printLogin()
    {
    }

    public void printBody()
    {
        String s = com.dragonflow.SiteView.Platform.getRoot() + "/groups/test";
        String s1 = com.dragonflow.SiteView.Platform.getRoot() + "/htdocs/test";
        if(!testWrite(s) || !testWrite(s1))
        {
            outputStream.println("<BODY>" + com.dragonflow.SiteView.Platform.productName + "  requires that your web server has write permission to the " + s1 + " and " + s + " directories.</BODY>");
            return;
        }
        boolean flag = false;
        boolean flag1 = false;
        try
        {
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            if(hashmap.get("_installed") != null)
            {
                flag = true;
            }
            if(hashmap.get("_haInstall") != null)
            {
                flag1 = true;
            }
        }
        catch(java.lang.Exception exception) { }
        if(!flag)
        {
            outputStream.println("<BODY>There was a problem loading the " + com.dragonflow.SiteView.Platform.productName + " configuration file." + "<p>Please check that the " + com.dragonflow.SiteView.Platform.productName + " service is running and try again.</BODY>");
            return;
        }
        String s2 = flag1 ? "/SiteView/cgi/go.exe/SiteView?page=haLicense&account=administrator" : "/SiteView/cgi/go.exe/SiteView?page=setup&account=administrator";
        if(com.dragonflow.SiteView.Platform.isPortal())
        {
            s2 = "/SiteView/cgi/go.exe/SiteView?page=portalSetup&account=administrator";
            if(hasSiteViews())
            {
                s2 = "/SiteView/cgi/go.exe/SiteView?page=portal&account=" + request.getAccount();
            }
        }
		else //if(hasGroups() || com.dragonflow.TopazIntegration.MAManager.isAttached()) dingbing.xu delete this
        {
            s2 = "/SiteView/" + request.getAccountDirectory() + "/SiteView.html";
        }
        printRefreshPage(s2, 0);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        (new indexPage()).handleRequest();
    }
}
