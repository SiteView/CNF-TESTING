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

import java.util.Enumeration;
import java.util.Vector;


import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.SSHCommandLine;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;

// Referenced classes of package COM.dragonflow.Page:
// machineChooserPage, GreaterEqualProcessInfo, pingPage

public class servicePage extends COM.dragonflow.Page.machineChooserPage
{

    public servicePage()
    {
		
    }

    public static HashMap getProcessStats(String s, String s1)
    {
        HashMap hashmap = new HashMap();
        String s2 = s1;
        if(s2.startsWith("\\\\"))
        {
            s2 = s2.substring(2);
        }
        COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getNTMachine(s2);
        Array array = null;
        if(machine != null && COM.dragonflow.SiteView.Machine.isNTSSH(s2))
        {
            if(s.indexOf("\\\\" + s2) > 0)
            {
                s = COM.dragonflow.Utils.TextUtils.replaceString(s, "\\\\" + s2, "");
            }
            s = s.substring(s.indexOf("perfex"));
            COM.dragonflow.Utils.SSHCommandLine sshcommandline = new SSHCommandLine();
            array = sshcommandline.exec(s, machine, false);
        } else
        {
            COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
            array = array = commandline.exec(s);
        }
        Enumeration enumeration = (Enumeration) array.iterator();
        long l = -1L;
        HashMap hashmap1 = null;
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s3 = (String)enumeration.nextElement();
            if(s3.startsWith("object:") && s3.indexOf("232") > 0)
            {
                break;
            }
            if(l < 0L)
            {
                l = COM.dragonflow.Utils.TextUtils.findLong(s3, "PerfTime100nSec:", null);
            } else
            if(s3.startsWith("name: "))
            {
                String s4 = s3.substring(6);
                hashmap1 = new HashMap();
                hashmap1.put("name", s4);
            } else
            if(s3.length() == 0)
            {
                hashmap1 = null;
            } else
            if(hashmap1 != null)
            {
                long l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "6:", "% PERF_100NSEC_TIMER");
                if(l1 != -1L)
                {
                    hashmap1.put("cpu", "" + l1);
                }
                l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "184:", "PERF_COUNTER_RAWCOUNT");
                if(l1 != -1L)
                {
                    hashmap1.put("memory", "" + l1 / 1024L + "k");
                } else
                {
                    l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "184:", "PERF_COUNTER_LARGE_RAWCOUNT");
                    if(l1 != -1L)
                    {
                        hashmap1.put("memory", "" + l1 / 1024L + "k");
                    }
                }
                l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "784:", "PERF_COUNTER_RAWCOUNT");
                if(l1 != -1L)
                {
                    String s6;
                    if(l1 != 0L)
                    {
                        s6 = "" + l1;
                    } else
                    {
                        s6 = (String)hashmap1.get("name");
                    }
                    hashmap1.put("id", s6);
                    hashmap1.put("time", "" + l);
                    hashmap.put(s6, hashmap1);
                }
                l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "1410:", "PERF_COUNTER_RAWCOUNT");
                if(l1 != -1L)
                {
                    hashmap1.put("parent", "" + l1);
                }
                l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "952:", "PERF_COUNTER_RAWCOUNT");
                if(l1 != -1L)
                {
                    hashmap1.put("handles", "" + l1);
                }
                l1 = COM.dragonflow.Utils.TextUtils.findLong(s3, "680:", "PERF_COUNTER_RAWCOUNT");
                if(l1 != -1L)
                {
                    hashmap1.put("threads", "" + l1);
                }
            }
        } while(true);
        Enumeration enumeration1 = hashmap.elements();
        do
        {
            if(!enumeration1.hasMoreElements())
            {
                break;
            }
            HashMap hashmap2 = (HashMap)enumeration1.nextElement();
            String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "parent");
            HashMap hashmap3 = (HashMap)hashmap.get(s5);
            if(hashmap3 != null)
            {
                hashmap2.put("parent", hashmap3.get("name"));
            }
        } while(true);
        return hashmap;
    }

    public static void printProcessStats(int i, StringBuffer stringbuffer)
    {
        COM.dragonflow.Page.servicePage.printProcessStats(i, null, stringbuffer, "", true);
    }

    public static void printProcessStats(int i, java.io.PrintWriter printwriter, String s, boolean flag)
    {
        COM.dragonflow.Page.servicePage.printProcessStats(i, printwriter, null, s, flag);
    }

    public static void printProcessStats(int i, java.io.PrintWriter printwriter, StringBuffer stringbuffer, String s, boolean flag)
    {
        int j = COM.dragonflow.SiteView.Machine.getOS(s);
        String s1 = COM.dragonflow.SiteView.Platform.psCommand(s, flag);
        if(!flag || !COM.dragonflow.SiteView.Platform.isWindows(j))
        {
            boolean flag1 = false;
            try
            {
                if(printwriter != null)
                {
                    printwriter.println("<BR><PRE>");
                }
                COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
                Array array = commandline.exec(s1, s);
                Enumeration enumeration = (Enumeration) array.iterator();
                boolean flag2 = false;
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    String s2 = (String)enumeration.nextElement();
                    if(s2.indexOf("end perfex") < 0)
                    {
                        if(s2.length() > 0)
                        {
                            flag1 = true;
                            if(printwriter != null)
                            {
                                printwriter.println(COM.dragonflow.Utils.TextUtils.escapeHTML(s2));
                                printwriter.flush();
                            }
                            if(stringbuffer != null)
                            {
                                stringbuffer.append(s2 + "\n");
                            }
                            flag2 = false;
                        } else
                        if(flag2)
                        {
                            flag1 = true;
                            if(printwriter != null)
                            {
                                printwriter.println();
                            }
                            if(stringbuffer != null)
                            {
                                stringbuffer.append(s2 + "\n");
                            }
                            flag2 = false;
                        } else
                        {
                            flag2 = true;
                        }
                    }
                } while(true);
                if(printwriter != null)
                {
                    printwriter.println("</PRE><BR>");
                }
            }
            catch(java.lang.Exception exception)
            {
                if(!flag1)
                {
                    if(printwriter != null)
                    {
                        printwriter.println("<hr>Could not perform ps command: " + s1 + "<hr>");
                    }
                    if(stringbuffer != null)
                    {
                        stringbuffer.append("Could not perform ps command: " + s1);
                    }
                }
            }
        } else
        {
            int k = 16;
            long l = -1L;
            long al[] = new long[k];
            try
            {
                long al1[] = COM.dragonflow.SiteView.Platform.cpuUsed(s, 0L, 0L, al);
                if(al1 != null)
                {
                    l = al1[3];
                }
            }
            catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Failed to get CPU data for service details. Machine: " + s);
            }
            l = java.lang.Math.min(k, java.lang.Math.max(1L, l));
            HashMap hashmap = COM.dragonflow.Page.servicePage.getProcessStats(s1, s);
            COM.dragonflow.SiteView.Platform.sleep(i * 1000);
            HashMap hashmap1 = COM.dragonflow.Page.servicePage.getProcessStats(s1, s);
            if(printwriter != null)
            {
                printwriter.println("<br><table><tr><th>Process<th>CPU<th>Memory<th>Threads<th>Handles<th>ID<th>Parent Process</tr>");
            }
            if(stringbuffer != null)
            {
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "name", 20);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "cpu", 9);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "memory", 12);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "threads", 9);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "handles", 9);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "id", 9);
                COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "parent", 9);
                stringbuffer.append("\n\n");
            }
            Array array1 = new Array();
            for(Enumeration enumeration1 = hashmap1.elements(); enumeration1.hasMoreElements(); array1.add(enumeration1.nextElement())) { }
            Sorting.sort(array1, new GreaterEqualProcessInfo());
            Enumeration enumeration2 =  (Enumeration) array1.iterator();
            do
            {
                if(!enumeration2.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap2 = (HashMap)enumeration2.nextElement();
                String s3 = (String)hashmap2.get("id");
                if(!s3.equals("Idle"))
                {
                    HashMap hashmap3 = (HashMap)hashmap.get(s3);
                    HashMap hashmap4 = hashmap2;
                    if(s3.equals("_Total"))
                    {
                        hashmap3 = (HashMap)hashmap.get("Idle");
                        hashmap4 = (HashMap)hashmap1.get("Idle");
                    }
                    String s4 = "n/a";
                    if(hashmap3 != null)
                    {
                        long l1 = COM.dragonflow.Utils.TextUtils.toLong((String)hashmap3.get("time"));
                        long l2 = COM.dragonflow.Utils.TextUtils.toLong((String)hashmap4.get("time"));
                        long l3 = l2 - l1;
                        if(l3 > 0L)
                        {
                            long l4 = COM.dragonflow.Utils.TextUtils.toLong((String)hashmap3.get("cpu"));
                            long l5 = COM.dragonflow.Utils.TextUtils.toLong((String)hashmap4.get("cpu"));
                            if(l > 0L && l5 != 0L)
                            {
                                long l6 = (100L * (l5 - l4)) / (l3 * l);
                                if(s3.equals("_Total"))
                                {
                                    l6 = 100L - l6;
                                }
                                s4 = "" + l6 + "%";
                            } else
                            {
                                s4 = "n/a";
                            }
                        }
                    }
                    if(printwriter != null)
                    {
                        printwriter.println("<tr align=right><td>" + hashmap2.get("name") + "</td><td>" + s4 + "</td><td>" + hashmap2.get("memory") + "</td><td>" + hashmap2.get("threads") + "</td><td>" + hashmap2.get("handles") + "</td><td>" + hashmap2.get("id") + "</td><td> " + hashmap2.get("parent") + "</td></tr>");
                    }
                    if(stringbuffer != null)
                    {
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "name"), 20);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, "" + s4, 9);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "memory"), 12);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "threads"), 9);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "handles"), 9);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "id"), 9);
                        COM.dragonflow.Utils.TextUtils.appendStringLeftJustify(stringbuffer, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "parent"), 20);
                        stringbuffer.append("\n");
                    }
                }
            } while(true);
            if(printwriter != null)
            {
                printwriter.println("</table><br>");
            }
        }
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("machine");
        if(s.length() == 0)
        {
            s = request.getValue("remoteMachine");
        }
        String s1 = request.getValue("option");
        if(COM.dragonflow.SiteView.Platform.isNTRemote(s))
        {
            s = COM.dragonflow.Utils.TextUtils.keepChars(s, ".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\\");
        }
        printBodyHeader("Services");
        printButtonBar("Services.htm", "");
        outputStream.println("<p>\n<CENTER><H2>Services</H2></CENTER><P>\n<p>\n" + getPagePOST("service", "") + "The Services tool allows you to view the services running on the server.\n" + "<p>\n");
        outputStream.println("<DT>By default, it will show the services running on this server.");
        if(COM.dragonflow.SiteView.Platform.isWindows(platformOS()))
        {
            outputStream.println("To view the services running on another NT server, enter the name of that server.  For example, \\\\TEST<p>");
        }
        outputStream.println("If there are \"Remote UNIX\" machines defined they will appear in a dropdown selection. You may select a Remote UNIX to see the processes running on the remote computer. You may not enter a Remote UNIX in the entry field.<p>");
        String s2 = "Server Name: ";
        java.util.Vector vector = addServers(new Vector(), "_remoteMachine");
        if(vector.size() > 0)
        {
            outputStream.println("Choose a remote server from the list to view the services on that machine.<P>");
            boolean flag = false;
            String s4 = "";
            outputStream.println("<select size=1 name=remoteMachine>");
            for(int j = 0; j < vector.size(); j += 2)
            {
                s4 = "";
                String s6 = (String)vector.elementAt(j);
                if(s6.equals(s))
                {
                    flag = true;
                    s4 = "SELECTED ";
                }
                outputStream.println("<OPTION " + s4 + "value=\"" + s6 + "\">" + vector.elementAt(j + 1));
            }

            s4 = "";
            if(!flag)
            {
                s4 = "SELECTED ";
            }
            outputStream.println("<OPTION " + s4 + "value=\"\">Other</select>");
            s2 = "Other Server: ";
        }
        if(COM.dragonflow.SiteView.Platform.isWindows(platformOS()))
        {
            String s3 = s;
            if(COM.dragonflow.SiteView.Platform.isCommandLineRemote(s))
            {
                s3 = "";
            }
            outputStream.println(s2 + "<input type=text name=machine value=\"" + s3 + "\" size=60></DT>\n");
        }
        outputStream.println("<p>\n<input type=hidden name=detail value=" + s1 + ">\n" + "<input type=submit value=\"Show Services\">\n" + "</FORM>\n");
        boolean flag1 = s1.equals("detail");
        int i = 0;
        if(request.getValue("sleep").length() > 0)
        {
            i = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("sleep"));
        }
        if(i <= 0)
        {
            i = 5;
        }
        if(!isPortalServerRequest())
        {
            printContentStartComment();
            COM.dragonflow.Page.servicePage.printProcessStats(i, outputStream, s, flag1);
            printContentEndComment();
        } else
        {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)getSiteView();
            if(portalsiteview != null)
            {
                String s7 = "_centrascopeTestMatch";
                if(s1.equals("detail"))
                {
                    s7 = "_centrascopeServiceDetailMatch";
                }
                String s8 = portalsiteview.getURLContentsFromRemoteSiteView(request, s7);
                outputStream.println(s8);
            }
        }
        String s5 = getPageLink("service", "") + "&machine=" + s;
        if(!s1.equals("detail"))
        {
            outputStream.println("<P>Go to <a href=" + s5 + "&option=detail>Process Detail</a><P>");
        }
        if(s1.length() != 0)
        {
            outputStream.println("<P>Go to <a href=" + s5 + ">Services</a><P>");
        }
        printFooter(outputStream);
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
