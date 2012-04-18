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

import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Page:
// machineChooserPage, CGI

public class serverPage extends com.dragonflow.Page.machineChooserPage
{

    public static long clientID = 0L;
    static HashMap storeReturnURL = null;
    static HashMap thisURL = null;

    public serverPage()
    {
    }

    public com.dragonflow.Page.CGI.menus getNavItems(com.dragonflow.HTTP.HTTPRequest httprequest)
    {
        com.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if(httprequest.actionAllowed("_preference"))
        {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if(httprequest.actionAllowed("_tools"))
        {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if(httprequest.actionAllowed("_progress"))
        {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        return menus1;
    }

    public static String getReturnURL(HashMap hashmap, String s)
    {
        if(hashmap != null && hashmap.count(s) > 0)
        {
            return (String)hashmap.get(s);
        } else
        {
            return "";
        }
    }

    public static void setReturnURL(HashMap hashmap, String s)
    {
        if(hashmap == null)
        {
            hashmap = new HashMap();
        }
        hashmap.put(String.valueOf(clientID), s);
    }

    public static void removeReturnURL(HashMap hashmap, String s)
    {
        if(hashmap != null && hashmap.count(s) > 0)
        {
            hashmap.remove(s);
        }
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(storeReturnURL == null)
        {
            storeReturnURL = new HashMap();
        }
        if(thisURL == null)
        {
            thisURL = new HashMap();
        }
        String s = "Choose Server";
        String s1 = null;
        String s2 = new String(request.rawURL);
        if(request.getValue("pop").equals("true"))
        {
            String s3 = request.getValue("storeID");
            s1 = new String(com.dragonflow.Page.serverPage.getReturnURL(storeReturnURL, s3));
            com.dragonflow.Page.serverPage.removeReturnURL(storeReturnURL, s3);
            com.dragonflow.Page.serverPage.removeReturnURL(thisURL, s3);
            if(s2.indexOf("pop") >= 0)
            {
                s2 = s2.substring(0, s2.indexOf("&pop"));
            }
        } else
        {
            s1 = request.getValue("returnURL");
        }
        if(!request.isPost())
        {
            printBodyHeader(s);
        }
        if(request.isPost() && !request.getValue("pop").equals("true"))
        {
            String s4 = request.getValue("otherServer");
            if(s4.length() == 0)
            {
                s4 = request.getValue("server");
                if(s4.equals("Other"))
                {
                    s4 = "";
                }
            } else
            if(!s4.startsWith("\\\\") && !s4.startsWith("http://"))
            {
                s4 = "\\\\" + s4;
            }
            int i = 0;
            Array array = new Array();
            if(com.dragonflow.SiteView.SiteViewGroup.currentSiteView().internalServerActive())
            {
                i = com.dragonflow.SiteView.Platform.CheckPermissions(s4, getMasterConfig(), array);
            }
            if(i != 0)
            {
                int j = s1.indexOf("&server=");
                if(j != -1)
                {
                    int l = s1.indexOf("&", j + 1);
                    if(l == -1)
                    {
                        l = s1.length();
                    }
                    s1 = s1.substring(0, j) + s1.substring(l, s1.length());
                }
                String s6;
                if(i == 53)
                {
                    s6 = "remote server not found";
                } else
                if(i == 1723)
                {
                    s6 = "remote server not accepting performance monitoring connections";
                } else
                if(i == 5)
                {
                    s6 = "access permissions error";
                } else
                {
                    s6 = "unknown error (" + i + ")";
                }
                outputStream.println("<hr><br>SiteView was unable to connect to the remote server. <p>Reason: <b>" + s6 + ".</b><br><hr>");
                if(i == 5)
                {
                    outputStream.println("<br><p>To monitor a remote NT server, the SiteView service has to run as a user who has permission to monitor the remote machine.&nbsp; (This is generally an administrator account and is the same permission needed to access a remote server using the PerfMon tool)<P><br><DT>To change the user SiteView runs as:</DT><DD>1) Open the Services control panel</DD><DD>2) Select the SiteView service and press the Startup... button</DD><DD>3) Choose the Log On As This Account radio button</DD><DD>4) Enter a username and password that has access to the remote server</DD><DD>5) Press the OK button to close the Startup... dialog</DD><DD>6) Press the Stop button to stop the SiteView service</DD><DD>7) Press the Start button to start the SiteView service</DD><DD>8) Press the Close button to close the Services control panel</DD><DD>9) Choose the Open SiteView item from the Programs section of the Start Menu.</DD>");
                }
                outputStream.println("<br><br>Go <a href=" + s1 + ">Back</a> to the Monitor page." + "");
            } else
            {
                if(!s4.equals("this server") && com.dragonflow.Utils.TextUtils.hasSpaces(s4))
                {
                    s4 = com.dragonflow.Utils.TextUtils.removeChars(s4, " ");
                }
                int k = s1.indexOf("&server=");
                if(k != -1)
                {
                    int i1 = s1.indexOf("&", k + 1);
                    if(i1 == -1)
                    {
                        i1 = s1.length();
                    }
                    s1 = s1.substring(0, k) + s1.substring(i1, s1.length());
                }
                if(isPortalServerRequest())
                {
                    s4 = com.dragonflow.SiteView.Machine.getMachineFromMachineID(s4);
                }
                s1 = s1 + "&server=" + com.dragonflow.HTTP.HTTPRequest.encodeString(s4);
                autoFollowPortalRefresh = false;
                printRefreshPage(s1, 0);
            }
        } else
        {
            String s5 = "Monitors.htm#remote";
            com.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar(s5, "", menus1);
            outputStream.println("<H2>Choose Server</H2><P>\nSiteView can monitor objects on this server and on other servers.  Choose the server from which information will be retrieved.<p>");
            outputStream.println(getPagePOST("server", "") + "<input type=hidden name=returnURL value=" + s1 + ">\n");
            outputStream.println("<TABLE><TR><TD ALIGN=RIGHT>Server</TD><TD ALIGN=LEFT><SELECT name=server size=1>");
            boolean flag = false;
            java.util.Vector vector = null;
            if(request.getValue("noNTRemote").length() == 0)
            {
                String s7 = request.getValue("returnURL");
                if(request.getValue("returnURL").indexOf("ScriptMonitor") > 0)
                {
                    vector = new Vector();
                    vector.addElement("this server");
                    vector.addElement("this server");
                    vector = addNTSSHServers(vector, "_remoteNTMachine");
                } else
                {
                    HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
                    if(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_NTSSHHideLocalServers").length() > 0)
                    {
                        vector = new Vector();
                        vector.addElement("this server");
                        vector.addElement("this server");
                    } else
                    {
                        vector = getLocalServers();
                    }
                    vector = addServers(vector, "_remoteNTMachine");
                }
            } else
            {
                vector = new Vector();
                vector.addElement("this server");
                vector.addElement("this server");
            }
            boolean flag1 = request.getValue("noremote").length() == 0;
            if(flag1)
            {
                vector = addServers(vector, "_remoteMachine");
            }
            boolean flag2 = request.getValue("noNTRemote").length() == 0 && !com.dragonflow.SiteView.Platform.isUnix();
            String s8 = request.getValue("server");
            if(s8.length() == 0)
            {
                s8 = "this server";
            } else
            {
                s8 = com.dragonflow.SiteView.Machine.getFullMachineID(s8, request);
            }
            for(int j1 = 0; j1 < vector.size(); j1 += 2)
            {
                String s10 = "";
                String s12 = (String)vector.elementAt(j1);
                if(s12.equals(s8))
                {
                    flag = true;
                    s10 = "SELECTED ";
                }
                outputStream.println("<OPTION " + s10 + "value=\"" + s12 + "\">" + vector.elementAt(j1 + 1));
            }

            String s9 = "";
            String s11 = "";
            if(!flag && s8.length() != 0 && flag2)
            {
                s9 = s8;
                s11 = "SELECTED ";
            }
            if(flag2)
            {
                outputStream.println("<OPTION " + s11 + "value=\"Other\">Other");
            }
            outputStream.println("</SELECT>");
            outputStream.println("</TD></TR>\n<TR><TD> </TD><TD><FONT SIZE=-1>the server to monitor</TD></TR>");
            if(flag2)
            {
                outputStream.println("<TR><TD ALIGN=RIGHT>Other Server</TD><TD ALIGN=LEFT><input type=text name=otherServer value=\"" + s9 + "\" size=30></TD></TR>" + "<TR><TD> </TD><TD><FONT SIZE=-1>optional field for entering NT servers outside of the current domain." + "  For example, \\\\TEST.  Leave this field empty to use server from popup.</FONT></TD></TR>");
            }
            outputStream.print("</TABLE>");
            outputStream.println("</tr></table><br><input type=submit value=Choose> Server</input>\n</FORM><p>\n");
            outputStream.print("<a href=" + s1 + ">Return Back</a>");
            HashMap hashmap1 = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            if(request.getValue("noNTRemote").length() == 0 && com.dragonflow.Utils.TextUtils.getValue(hashmap1, "_allowUnixToNT").length() > 0)
            {
                outputStream.println("<p>" + getPagePOST("ntmachine", "") + "<input type=hidden name=backURL value=" + s2 + ">\n" + "<input type=hidden name=storeURL value=" + s1 + ">\n" + "<input type=submit value=\"Setup NT Remote\"> " + "Set up remote NT with a different user/password then the one used in SiteView Services</input>\n" + "</form>\n");
            }
            if(flag1)
            {
                outputStream.println("<p>" + getPagePOST("machine", "") + "<input type=hidden name=backURL value=" + s2 + ">\n" + "<input type=hidden name=storeURL value=" + s1 + ">\n" + "<input type=submit value=\"Setup Unix Remote\"> " + "Set up remote Unix machine with a different user/password then the one used in SiteView Services</input>\n" + "</form>\n");
            }
            if(flag2)
            {
                outputStream.println("<p><b>Note:</b> Monitoring <B>remote NT</B> servers requires that the SiteView service runs in a user account that has permission to access the NT performance registry on the remote server. <p>To change the user account for SiteView:<UL><LI>Open the Services control panel<LI>Select the SiteView service from the list of services<LI>Press Stop and confirm to stop the SiteView service<LI>Press the Startup button to open the startup dialog<LI>Choose the Log On As This Account radio button<LI>Enter an account name and password valid on both the local server and the remote servers<LI>Press OK to close the startup dialog<LI>Press Start to start the SiteView service</UL><p><br>");
            }
            if(com.dragonflow.SiteView.SiteViewGroup.allowRemoteCommandLine() && flag1)
            {
                outputStream.println("<p><b>Note:</b> Monitoring <B>remote Unix</B> servers requires that access for those machines is defined. If a remote server is defined, it will appear in the Server popup above - if you do not see the server in the popup menu:\n<OL><LI>click the SiteView button on the button bar\n<LI>click the Preferences link when the SiteView main page appears\n<LI>click the Unix Remote Servers link.</OL>\nThis page will allow you define your Unix remote server(s).  When you are\ndone defining the server(s), <B>Add</B> the monitor again, and you will be able to choose that server.");
            }
        }
        printFooter(outputStream);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.serverPage serverpage = new serverPage();
        if(args.length > 0)
        {
            serverpage.args = args;
        }
        serverpage.handleRequest();
    }

}
