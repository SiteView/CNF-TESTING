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

import java.io.File;
import java.util.Date;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.algorithms.Sorting;

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.SiteView.GreaterDate;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class logPage extends COM.dragonflow.Page.CGI
{

    public logPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_logs"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getAccount();
        printBodyHeader("Log");
        COM.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if(request.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if(request.actionAllowed("_preference"))
        {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if(request.actionAllowed("_tools"))
        {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if(request.actionAllowed("_progress"))
        {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if(request.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        printButtonBar("Log.htm", "", menus1);
        outputStream.println("<p>\n<CENTER><H2>Logs</H2></CENTER><P>\n<p>\nChoose the log file to inspect.\n");
        outputStream.println("<DL>");
        String s1 = COM.dragonflow.SiteView.Platform.getURLPath("logs", s);
        String s2 = COM.dragonflow.SiteView.Platform.getDirectoryPath("logs", s);
        String s3 = COM.dragonflow.SiteView.Portal.cleanPortalServerID(request.getPortalServer());
        if(s3.length() > 0)
        {
            s1 = "/SiteView/portal/" + s3 + "/logs";
            s2 = COM.dragonflow.SiteView.Portal.getPortalSiteViewRootPath(s3) + java.io.File.separator + "logs";
        }
        String s4 = "";
        String s8 = "";
        s4 = "";
        s8 = "";
        String as[] = {
            "alert", "error", "RunMonitor", "topaz", "post", "url", "operator"
        };
        String as1[] = {
            "Alert log", "Error Log", "Run Monitor Log",/* COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Log",*/ "Post Log File", "URL Details", "Operator Log"
        };
        String as2[] = {
            "Every time an alert is sent, a decription of the alert is stored in the Alert log ", "If there is a problem executing alerts or with SiteView in general, a description of the error is saved in the Error log ", "Monitor processing is stored in the Run Monitor log.",/* COM.dragonflow.SiteView.TopazInfo.getTopazName() + " integrations log file.", */"A log of HTTP post requests.", "For each HTTP and HTTPS request made, details are saved in the URL log ", "Operator actions such as acknowledgment are saved in the Operator log "
        };
        for(int i = 0; i < as.length; i++)
        {
            java.io.File file = new File(s2 + "/" + as[i] + ".log");
            String s5 = "<B>(no entries in log yet)</B>";
            String s9 = as1[i];
            if(file.exists())
            {
                s5 = "(size is " + COM.dragonflow.Utils.TextUtils.bytesToString(file.length()) + ")";
                s9 = "<A HREF=" + s1 + "/" + as[i] + ".log?addPre=true target=\"child\">" + as1[i] + "</A>";
            }
            outputStream.println("<DT>" + s9 + "<DD>" + as2[i] + "<BR>" + s5 + "<p>\n");
        }

        java.io.File file2 = new File(s2);
        String as3[] = file2.list();
        Array array = new Array();
        if(as3 != null)
        {
            for(int j = 0; j < as3.length; j++)
            {
                if(!as3[j].startsWith("SiteView") || !as3[j].endsWith(".log"))
                {
                    continue;
                }
                java.util.Date date = null;
                if(as3[j].equals("SiteView.log"))
                {
                    date = new Date(0L);
                } else
                {
                    date = COM.dragonflow.Utils.TextUtils.fileNameToDay(as3[j].substring("SiteView".length()));
                }
                array.add(date);
            }

        }
        Sorting.sort(array, new GreaterDate());
        String s12 = "";
        if(array.size() == 0)
        {
            String s6 = "</B>";
            String s10 = "Monitor log";
            outputStream.println("<DT>Monitor log <B>(no log entries yet)</B>");
        } else
        {
            long l = 0L;
            outputStream.println("<DT><TABLE BORDER=\"0\">");
            for(int k = 0; k < array.size(); k++)
            {
                java.util.Date date1 = (java.util.Date)array.get(k);
                String s13 = "SiteView.log";
                String s14 = "Monitor log";
                if(date1.getTime() != 0L)
                {
                    s13 = "SiteView" + COM.dragonflow.Utils.TextUtils.dayToFileName(date1) + ".log";
                    s14 = COM.dragonflow.Utils.TextUtils.prettyDateDate(date1) + " " + s14;
                }
                java.io.File file1 = new File(s2 + java.io.File.separator + s13);
                long l1 = file1.length();
                l += l1;
                String s7 = "  (" + COM.dragonflow.Utils.TextUtils.bytesToString(l1) + ")";
                String s11 = "<A HREF=" + s1 + "/" + s13 + "?addPre=true target=\"child\">" + s14 + "</A>";
                outputStream.println("<TR><TD>" + s11 + "</TD><TD>" + s7 + "</TD></TR>");
            }

            outputStream.println("</TABLE>");
            s12 = "<BR>(total size of Monitor logs is " + COM.dragonflow.Utils.TextUtils.bytesToString(l) + ")";
        }
        outputStream.println("<DD>Every time a monitor is run, the results are stored in the current day's Monitor log" + s12 + "<P>\n");
        outputStream.println("</DL>");
        printFooter(outputStream);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.logPage logpage = new logPage();
        if(args.length > 0)
        {
            logpage.args = args;
        }
        logpage.handleRequest();
    }
}
