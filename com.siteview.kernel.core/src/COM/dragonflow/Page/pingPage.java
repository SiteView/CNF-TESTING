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

import com.recursionsw.jgl.Array;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class pingPage extends COM.dragonflow.Page.CGI
{

    public pingPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("host");
        String s1 = request.getValue("location");
        s = COM.dragonflow.Utils.TextUtils.keepChars(s, ".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        printBodyHeader("Ping");
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printButtonBar("Ping.htm", "");
        }
        String s2 = "";
        if(s1.length() != 0)
        {
            s2 = "&location=" + s1;
            for(s2 = s2.trim(); s2.indexOf(" ") >= 0; s2 = s2.substring(0, s2.indexOf(" ")) + s2.substring(s2.indexOf(" ") + 1)) { }
        }
        if(!request.getValue("AWRequest").equals("yes"))
        {
            outputStream.println("<center><b>Ping</b> | <a href=" + getPageLink("trace", "") + "&host=" + s + s2 + "&group=" + request.getValue("group") + ">TraceRoute</a></center><P>");
        } else
        {
            outputStream.println("<center><b>Ping</b> | <a href=" + getPageLink("trace", "") + "&host=" + s + s2 + "&AWRequest=yes" + ">TraceRoute</a>" + " | " + " <a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a>" + "</center><P>");
        }
        outputStream.println("<p>\n<CENTER><H2>Ping</H2></CENTER><P>\n<p>\n" + getPagePOST("ping", "") + "Ping is a tool that sends a packet to another location and back to the sender.\n" + "It shows you the roundtrip time along the path.\n" + "When there is a problem with the network, ping can tell you if another\n" + "location is reachable.\n" + "<p>This form will do a ping from this server to another location.  Enter\n" + "the domain name or ip address of the other location.\n" + "<p>\n" + "Domain Name or IP address: <input type=text name=host value=\"" + s + "\" size=60>\n" + "<p>\n");
        boolean flag = !isPortalServerRequest();
        try
        {
            java.lang.Class.forName("COM.dragonflow.StandardMonitor.URLRemoteMonitor");
        }
        catch(java.lang.ClassNotFoundException classnotfoundexception)
        {
            flag = false;
        }
        if(flag)
        {
            outputStream.print(COM.dragonflow.Utils.HTTPUtils.locationsHTML(s1, request));
        }
        outputStream.print("<input type=submit value=\"Ping\" class=\"VerBl8\">\n</FORM>\n");
        if(s.length() > 0)
        {
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                String s3 = "";
                String s4 = "";
                if(s1.length() > 0)
                {
                    s3 = COM.dragonflow.Utils.HTTPUtils.getPingURLByID(s1);
                    s4 = COM.dragonflow.Utils.HTTPUtils.getLocationNameByID(s1);
                }
                if(s4.length() > 0)
                {
                    s4 = " from " + s4;
                }
                outputStream.println("<PRE>Pinging " + s + s4 + "...\n\n");
                outputStream.flush();
                if(s3.length() == 0)
                {
                    String s6 = COM.dragonflow.SiteView.Platform.pingCommand(s, 5000, 5, 64);
                    boolean flag1 = false;
                    try
                    {
                        java.lang.Process process = COM.dragonflow.Utils.CommandLine.execSync(s6);
                        boolean flag2 = false;
                        java.io.BufferedReader bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
                        String s9;
                        while((s9 = bufferedreader.readLine()) != null) 
                        {
                            if(s9.length() > 0)
                            {
                                flag1 = true;
                                outputStream.println(COM.dragonflow.Utils.TextUtils.escapeHTML(s9));
                                outputStream.flush();
                                flag2 = false;
                            } else
                            if(flag2)
                            {
                                flag1 = true;
                                outputStream.println();
                                flag2 = false;
                            } else
                            {
                                flag2 = true;
                            }
                        }
                        bufferedreader.close();
                    }
                    catch(java.io.IOException ioexception)
                    {
                        if(!flag1)
                        {
                            outputStream.println("Could not perform ping command: " + s6);
                        }
                    }
                } else
                {
                    StringBuffer stringbuffer = new StringBuffer();
                    String s7 = "";
                    String s8 = "";
                    String s10 = "";
                    String s11 = "";
                    String s12 = "";
                    String s13 = "";
                    String s14 = "";
                    String s15 = "";
                    long l = 50000L;
                    Array array = null;
                    StringBuffer stringbuffer1 = new StringBuffer(s);
                    COM.dragonflow.Utils.SocketSession socketsession = COM.dragonflow.Utils.SocketSession.getSession(null);
                    long al[] = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s, s8, s10, s12, s13, s14, array, s7, s11, s3, stringbuffer, l, s15, 0, 0x1d4c0, stringbuffer1);
                    socketsession.close();
                    long l1 = al[0];
                    String s16;
                    if(l1 != 200L)
                    {
                        s16 = stringbuffer.toString();
                        outputStream.print("Error, " + COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(l1) + "\n\n");
                    } else
                    {
                        s16 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
                    }
                    if(stringbuffer != null)
                    {
                        outputStream.print(COM.dragonflow.Utils.TextUtils.escapeHTML(s16));
                    }
                }
                outputStream.println("</PRE>");
                printContentEndComment();
            } else
            {
                COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)getSiteView();
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
        COM.dragonflow.Page.pingPage pingpage = new pingPage();
        if(args.length > 0)
        {
            pingpage.args = args;
        }
        pingpage.handleRequest();
    }
}
