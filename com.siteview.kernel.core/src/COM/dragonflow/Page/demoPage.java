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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.recursionsw.jgl.HashMap;

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class demoPage extends COM.dragonflow.Page.CGI
{

    public demoPage()
    {
    }

    void doReset()
    {
        printRefreshHeader("", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html?openMultiView", 0);
        outputStream.println("<b>Please wait a few seconds while the demo is being reset...</b><p>\n");
        outputStream.flush();
        outputStream.println("<p>Removing old Demo files...<p>");
        outputStream.flush();
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/groups");
        String as[] = file.list();
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].indexOf("users.config") < 0 && as[i].indexOf("pid") == -1)
            {
                java.io.File file2 = new File(file, as[i]);
                outputStream.println("<br>deleting " + file2);
                file2.delete();
            }
        }

        outputStream.println("<p>Copying Demo files...<p>");
        outputStream.flush();
        java.io.File file1 = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/../DemoGroups");
        as = file1.list();
        for(int j = 0; j < as.length; j++)
        {
            if(as[j].indexOf("users.config") < 0)
            {
                java.io.File file3 = new File(file1, as[j]);
                java.io.File file4 = new File(file, as[j]);
                outputStream.println("<br>copying " + file3 + " to " + file4);
                COM.dragonflow.Page.demoPage.copyFile(file3, file4, outputStream);
            }
        }

        outputStream.println("<p>Updating SiteView pages...<p>");
        outputStream.flush();
        try
        {
            HashMap hashmap = getMasterConfig();
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            siteviewgroup.startingUp = true;
            saveMasterConfig(hashmap);
            siteviewgroup.startingUp = false;
        }
        catch(java.lang.Exception exception) { }
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
            outputStream.println("<CENTER><H2>Reset Demo</H2></CENTER><P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>This form will reset the SiteView demo back to its default configuration.<CENTER><p><input type=hidden name=page value=\"demo\"><input type=hidden name=account value=\"administrator\"><input type=hidden name=operation value=\"setup\"><input type=submit value=\"Reset\"> Demo</CENTER></FORM>");
            printFooter(outputStream);
        }
    }

    public static boolean copyFile(java.io.File file, java.io.File file1, java.io.PrintWriter printwriter)
    {
        java.io.FileInputStream fileinputstream = null;
        java.io.FileOutputStream fileoutputstream = null;
        boolean flag = false;
        if(!file.exists())
        {
            printwriter.println("Could not find source file for copy: " + file.getAbsolutePath());
            return false;
        }
        if(!file.canRead())
        {
            printwriter.println("Can not read source file for copy: " + file.getAbsolutePath());
            return false;
        }
        if(file1.exists() && !file1.canWrite())
        {
            printwriter.println("Can not write destination file for copy: " + file1.getAbsolutePath());
            return false;
        }
        try
        {
            fileinputstream = new FileInputStream(file);
            java.io.BufferedInputStream bufferedinputstream = new BufferedInputStream(fileinputstream, 16384);
            fileoutputstream = new FileOutputStream(file1);
            java.io.BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(fileoutputstream, 16384);
            int i = 0;
            byte abyte0[] = new byte[16384];
            while((i = bufferedinputstream.read(abyte0)) != -1) 
            {
                bufferedoutputstream.write(abyte0, 0, i);
            }
            bufferedoutputstream.flush();
            flag = true;
        }
        catch(java.io.FileNotFoundException filenotfoundexception)
        {
            printwriter.println("File not found exception in copying " + file.getName() + " to " + file1.getName());
        }
        catch(java.io.IOException ioexception)
        {
            printwriter.println("IO Exception copying " + file.getName() + " to " + file1.getName());
        }
        finally
        {
            if(fileinputstream != null)
            {
                try
                {
                    fileinputstream.close();
                }
                catch(java.io.IOException ioexception1) { }
            }
            if(fileoutputstream != null)
            {
                try
                {
                    fileoutputstream.close();
                }
                catch(java.io.IOException ioexception2) { }
            }
        }
        return flag;
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        (new demoPage()).handleRequest();
    }
}
