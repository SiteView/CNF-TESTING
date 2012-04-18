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

import com.recursionsw.jgl.Array;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.SiteView.MirrorConfiguration;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class setupwzdPage extends COM.dragonflow.Page.CGI
{

    public setupwzdPage()
    {
    }

    void copyConfigs()
    {
        printRefreshHeader("", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 7);
        printBodyHeader("SiteView Copy Wizard");
        printButtonBar("UserGuide.htm", "");
        outputStream.println("<b>now calling copy config</b>\n");
        retrieveConfiguration();
        printFooter(outputStream);
        outputStream.flush();
    }

    void setupRemotes()
    {
        printRefreshHeader("", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 17);
        printBodyHeader("SiteView Setup Wizard");
        printButtonBar("UserGuide.htm", "");
        outputStream.println("<H1>SiteView Setup Wizard</H1>\n");
        outputStream.println("<b>Please wait a few seconds while the set up is being created...</b><p>\n");
        outputStream.println("<p>Found inputs: " + request.getValue("rmservers") + "<p>");
        printFooter(outputStream);
        outputStream.flush();
    }

    void printCopyForm(HashMap hashmap)
    {
        String s = (String)hashmap.get("_productName");
        if(s.length() > 0)
        {
            s = "SiteView";
        }
        printBodyHeader("Copy " + s + " Monitor Configurations");
        String s1 = "";
        try
        {
            StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + "setupCopyPage1.txt");
            s1 = stringbuffer.toString();
        }
        catch(java.io.IOException ioexception)
        {
            s1 = "unable to find content for Copy Wizard :" + ioexception;
        }
        outputStream.println("\n" + s1 + "\n");
    }

    void printMultiSetupForm(HashMap hashmap)
    {
        String s = (String)hashmap.get("_productName");
        if(s.length() > 0)
        {
            s = (String)hashmap.get("_productName");
        } else
        {
            s = "SiteView";
        }
        printBodyHeader("Setup Multiple " + s + " Monitor Configurations");
        String s1 = "";
        try
        {
            StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes" + java.io.File.separator + (String)hashmap.get("_startupPage3"));
            s1 = stringbuffer.toString();
        }
        catch(java.io.IOException ioexception)
        {
            s1 = "unable to find content for Multi-Setup Wizard :" + ioexception;
        }
        outputStream.println("\n" + s1 + "\n");
    }

    public void printBody()
    {
        Array array = new Array();
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        try
        {
            Array array1 = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes" + java.io.File.separator + "setup.config");
            HashMap hashmap = (HashMap)array1.front();
            if(request.isPost())
            {
                if(request.getValue("operation").indexOf("Copy") != -1)
                {
                    outputStream.println("In printBody as POST with op: =" + request.getValue("operation") + " proceeding with Copy\n<br>");
                    copyConfigs();
                } else
                if(request.getValue("operation").indexOf("Setupwzd") != -1)
                {
                    outputStream.println("In printBody as POST with op:= " + request.getValue("operation") + " proceeding to wizard\n<br>");
                    setupRemotes();
                }
            } else
            {
                String s = "SiteView";
                if(request.getValue("operation").indexOf("Copy") != -1)
                {
                    printCopyForm(hashmap);
                } else
                if(request.getValue("operation").indexOf("Setupwzd") != -1)
                {
                    printMultiSetupForm(hashmap);
                } else
                {
                    outputStream.println("Accessed directly as a GET with operation =" + request.getValue("operation") + " missed the choices\n<br>");
                    printRefreshHeader("", "/SiteView/cgi/go.exe/SiteView?page=setup&" + request.getAccountDirectory(), 10);
                }
                printFooter(outputStream);
            }
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.out.print("Unable to find wizard.config");
        }
    }

    boolean writeGroupFile(Array array, String s)
    {
        try
        {
            outputStream.println("<p>Adding the '" + s + "' monitor group ...<br>");
            outputStream.flush();
            COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/" + s + ".mg", array);
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println(exception + "An error occurred when trying to write group file: " + s);
        }
        return true;
    }

    jgl.HashMap createHistoryFrame(int i, String s)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("startHour", "now");
        hashmapordered.add("startDay", "today");
        hashmapordered.add("relative", "-1");
        hashmapordered.add("precision", "default");
        hashmapordered.add("monitors", s);
        hashmapordered.add("id", String.valueOf(i));
        hashmapordered.add("basic", "checked");
        return hashmapordered;
    }

    jgl.HashMap createDailyFrame(int i, String s)
    {
        HashMap hashmap = createHistoryFrame(i, s);
        hashmap.add("window", String.valueOf(0x15180));
        hashmap.add("schedule", "weekday\tM,T,W,R,F,S,U\t01:00");
        return hashmap;
    }

    jgl.HashMap createWeeklyFrame(int i, String s)
    {
        HashMap hashmap = createHistoryFrame(i, s);
        hashmap.add("window", String.valueOf(0x93a80));
        hashmap.add("schedule", "weekday\tU\t02:00");
        return hashmap;
    }

    void retrieveConfiguration()
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("server", request.getValue("server"));
        hashmapordered.put("addOnly", "true");
        hashmapordered.put("username", request.getValue("username"));
        hashmapordered.put("password", request.getValue("password"));
        hashmapordered.put("proxy", request.getValue("proxy"));
        hashmapordered.put("_version", "1.0");
        for(int i = 0; i < COM.dragonflow.SiteView.MirrorConfiguration.masterConfigExcludes.length; i++)
        {
            hashmapordered.add("masterExclusion", COM.dragonflow.SiteView.MirrorConfiguration.masterConfigExcludes[i]);
        }

        COM.dragonflow.SiteView.MirrorConfiguration mirrorconfiguration = new MirrorConfiguration(hashmapordered, outputStream);
        mirrorconfiguration.execute(true);
        if(mirrorconfiguration.status == (long)COM.dragonflow.SiteView.Monitor.kURLok)
        {
            outputStream.println("<P><B>Copy succeeded</B>");
            SiteViewMain.SiteViewSupport.CopyDefaultConfig();
            HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s2 = request.getValue("isI18N");
            hashmap.put("_isI18N", s2);
            COM.dragonflow.Utils.I18N.isI18N = s2.length() > 0;
            try
            {
                saveMasterConfig(hashmap);
            }
            catch(java.io.IOException ioexception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Error writing International flag(_isI18N) to Master.config: " + ioexception.getMessage());
            }
            COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
            COM.dragonflow.SiteView.User.unloadUsers();
            COM.dragonflow.SiteView.User.loadUsers();
            outputStream.flush();
        } else
        {
            String s = COM.dragonflow.SiteView.Monitor.lookupStatus(mirrorconfiguration.status);
            if(mirrorconfiguration.status == (long)COM.dragonflow.SiteView.Monitor.kMonitorSpecificError || mirrorconfiguration.status == 404L)
            {
                s = "version too old or address is invalid - SiteViews must be version 4.5 or later and the  address must only contain the IP address and port number";
            }
            outputStream.println("<P><B>Copy failed: " + s + "<P>You can <A HREF=/SiteView/>try this copy again</A>, or " + "you can manually copy the files from the " + "previous installation to this one.");
        }
        String s1 = "/SiteView/" + request.getAccountDirectory() + "/SiteView.html";
        outputStream.println("<P><A HREF=" + s1 + ">Go to SiteView main page</A>");
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        (new setupwzdPage()).handleRequest();
    }
}
