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

import java.util.Enumeration;
import java.util.Hashtable;

import com.recursionsw.jgl.Array;
import com.dragonflow.SiteView.Machine;
import com.dragonflow.Utils.CommandLine;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class perfCounterPage extends com.dragonflow.Page.CGI
{

    static String oldMachineName = "";
    static String cmdToPrint = "";
    String username;
    String password;
    com.dragonflow.SiteView.Machine m;

    public perfCounterPage()
    {
        username = "";
        password = "";
        m = new Machine();
    }

    public void printBody()
        throws java.lang.Exception
    {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        printBodyHeader("NT Performance Counters");
        printButtonBar("PerfCounters.htm", "");
        outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + " &group=" + request.getValue("group") + ">Diagnostic Tools</a></center><p>");
        String s = request.getValue("group");
        if(s.length() > 0)
        {
            outputStream.println("<H2>Return to group : <A HREF=" + com.dragonflow.Page.CGI.getGroupDetailURL(request, com.dragonflow.Utils.I18N.toDefaultEncoding(s)) + ">" + com.dragonflow.Page.CGI.getGroupName(com.dragonflow.Utils.I18N.toDefaultEncoding(s)) + "</a></H2>\n");
        }
        String s1 = request.getValue("machineName");
        if(s1.equals("(this server)"))
        {
            s1 = "";
        }
        username = "";
        password = "";
        username = new String(request.getValue("userName"));
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        password = com.dragonflow.Properties.StringProperty.getPrivate(request, "password", "perfSuff", stringbuffer, stringbuffer1);
        String s2 = request.getValue("counterObject");
        if(s2.equals(""))
        {
            s2 = "(Choose a counter object)";
        }
        String s3 = "";
        if(s1.length() > 0 && !s1.equals("(this server)"))
        {
            if(!s1.startsWith("\\\\"))
            {
                s1 = "\\\\" + s1;
            }
            s3 = s1;
        } else
        {
            s3 = "(this server)";
        }
        String s4 = "<option value=\"(No Counter Objects available)\">(NO COUNTERS OBJECTS AVAILABLE using this username and password)</option>";
        String s5 = com.dragonflow.SiteView.Platform.perfexCommand(s1) + " -u -p -h";
        com.dragonflow.Utils.CommandLine commandline = new CommandLine();
        Array array = execNTorSSH(s1, s5, commandline);
        String s6 = com.dragonflow.SiteView.Platform.perfexCommand(s1);
        if(username.length() > 0)
        {
            s6 = s6 + " -u " + username;
        }
        if(password.length() > 0)
        {
            s6 = s6 + " -p " + password;
        }
        s6 = s6 + " -h";
        array = execNTorSSH(s1, s6, commandline);
        if(array == null)
        {
            array = new Array();
        }
        Enumeration enumeration = (Enumeration) array.iterator();
        java.util.Hashtable hashtable = new Hashtable();
        if(array != null && array.size() > 3)
        {
            s4 = "<option value=\"(Choose a counter object)\">(Choose a counter object)</option>";
        }
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s7 = (String)enumeration.nextElement();
            if(flag3)
            {
                if(s7.startsWith("("))
                {
                    int i = s7.indexOf(" ");
                    if(i != -1)
                    {
                        java.lang.Integer integer = new Integer(s7.substring(1, i - 1));
                        java.lang.Integer integer1 = new Integer(integer.intValue() - 1);
                        String s12 = (String)hashtable.get("(" + integer1.toString() + ")");
                        if(s12 != null)
                        {
                            hashtable.put(s12, s7.substring(i + 1));
                        }
                    }
                } else
                if(s7.startsWith(" "))
                {
                    flag1 = false;
                }
            }
            if(flag1)
            {
                if(s7.startsWith("("))
                {
                    int j = s7.indexOf(" ");
                    if(j != -1)
                    {
                        hashtable.put(s7.substring(0, j), s7.substring(j + 1));
                    }
                } else
                if(s7.startsWith(" "))
                {
                    flag1 = false;
                }
            }
            if(flag && s7.startsWith("\""))
            {
                int k = s7.substring(2).indexOf("\"");
                if(k != -1)
                {
                    String s9 = s7.substring(1, k + 2);
                    s4 = s4 + "<option value=\"" + s9 + "\"";
                    if(s2.equals(s9))
                    {
                        s4 = s4 + " selected ";
                        flag4 = true;
                    }
                    s4 = s4 + ">" + s9 + "</option>";
                }
            }
            if(s7.startsWith("Counter Names"))
            {
                flag1 = true;
            }
            if(s7.startsWith("Help Names"))
            {
                flag3 = true;
            }
            if(s7.startsWith("The available performance counter objects are"))
            {
                flag = true;
            }
        } while(true);
        boolean flag5 = false;
        String s8 = "";
        String s10 = "";
        StringBuffer stringbuffer2 = new StringBuffer("");
        if(s1.equals(oldMachineName) || flag4)
        {
            String s11 = com.dragonflow.SiteView.Platform.perfexCommand(s1);
            if(username.length() > 0)
            {
                int l = username.indexOf("\\");
                String s14 = username;
                if(l >= 0)
                {
                    s14 = username.substring(0, l) + "\\\\" + username.substring(l + 1);
                }
                s11 = s11 + "-u " + s14 + " ";
            }
            if(password.length() > 0)
            {
                s11 = s11 + "-p " + password + " ";
            }
            s11 = s11 + " -o " + s2;
            com.dragonflow.Utils.CommandLine commandline1 = new CommandLine();
            Array array1 = execNTorSSH(s1, s11, commandline1);
            Enumeration enumeration1 =  (Enumeration) array1.iterator();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                String s16 = (String)enumeration1.nextElement();
                stringbuffer2.append(s16 + "<BR>\n");
                if(s16.startsWith("object:"))
                {
                    flag2 = true;
                    flag5 = true;
                }
                int i1 = s16.indexOf(":");
                if(flag2)
                {
                    if(s16.startsWith("object:") || s16.startsWith("name:"))
                    {
                        s8 = s8 + "<TR><TD>" + s16 + "</TD><TD></TD><TD></TD></TR>";
                    } else
                    if(s16.length() > 0 && i1 != -1)
                    {
                        s8 = s8 + "<TR><TD>";
                        if(i1 != -1)
                        {
                            s8 = s8 + s16.substring(0, i1);
                        }
                        s8 = s8 + "</TD><TD>";
                        int j1 = s16.substring(i1 + 2).indexOf(" ");
                        if(j1 != -1)
                        {
                            s8 = s8 + s16.substring(i1 + 2, i1 + 2 + j1);
                        }
                        s8 = s8 + "</TD><TD>";
                        String s17 = null;
                        if(i1 != -1)
                        {
                            s17 = (String)hashtable.get(s16.substring(0, i1));
                        }
                        if(s17 != null && !s17.equals(""))
                        {
                            s8 = s8 + s17 + "</TD></TR>";
                        } else
                        {
                            s8 = s8 + "not available</TD></TR>";
                        }
                    }
                }
            } while(true);
        }
        if(!flag5)
        {
            s8 = s8 + "<TR><TD>***PLEASE*** SELECT A COUNTER from \"Counter Object Name:\"</TD><TD>(n\\a)</TD><TD>(n\\a)</TD></TR>";
        }
        oldMachineName = s1;
        String s13 = "&nbsp;&nbsp;NetBIOS connection";
        if(m.getProperty(com.dragonflow.SiteView.Machine.pMethod).equals("ssh"))
        {
            s13 = "&nbsp;&nbsp;SSH connection";
        }
        outputStream.println("<p>\n<CENTER><H2>NT Performance Counter Test</H2></CENTER><P>\n<p>\n" + getPagePOST("perfCounter", "") + "<input type=hidden name=group value=" + request.getValue("group") + ">\n" + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n" + "This is an interface to perfex.exe found in the \\SiteView\\tools\\ directory. \n" + "Supplying a machine name will list all NT performance counter objects available \n" + "on this machine.  Selecting the \"List Objects and Enumerate Counters\" button \n" + "will enumerate each individual NT performance counter and its current value for the \n" + "selected counter object.  If there are no counter objects available for this machine, \n" + "the drop down list that contains the counter objects will indicate this situation.  When \n" + "exceptions and errors are encountered, the information is printed and a suggested \n" + "resolution is given to help with troubleshooting. This tool can be a great troubleshooting \n" + "tool when dealing with remote registry connections to read performance counters." + "<p><b>Troubleshooting Hint:</b> If you see the message \"(NO COUNTERS OBJECTS AVAILABLE using this \n" + "username and password)\" in your drop down list for \"Counter objects:\" and you haven't supplied a \n" + "username and password, make sure to follow one of the suggestions below to insure that you have access \n" + "to the remote machine's registry you are setting up.<p><ol><li>Setup a SiteView NT remote connection to \n" + "the remote machine that has local administrator privileges.</li><li>Run the SiteView service as a \n" + "user that has access to your remote machines.</li></ol>\n" + "<p>\n" + "<p><TABLE BORDER=0>\n" + "<TR><TD ALIGN=RIGHT>Machine Name:<TD><input type=text name=machineName value=\"" + s3 + "\" size=60>" + s13 + "<TR><TD ALIGN=RIGHT>Admin User Account:<TD><input type=text name=userName value=\"" + username + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Password:<TD>" + stringbuffer.toString() + " size=60><br>\n" + stringbuffer1.toString() + "<TR><TD ALIGN=RIGHT>Counter Object Name:<TD><SELECT name=counterObject>" + s4 + "</SELECT><br>\n" + "</TABLE><p>\n");
        if(s2.trim().length() > 0 && !flag4 && s2.indexOf("(Choose a") < 0)
        {
            outputStream.println("<P>\nRequired Counter Object: \"" + s2 + "\" not available, check remote computer counters.<BR>\n" + "</P>\n");
        }
        String s15 = "";
        outputStream.println("<P>\nThe perfex command is:<BR>" + s15 + "\n" + "</P>\n");
        outputStream.println("<input type=submit value=\"List Objects and Enumerate Counters\" class=\"VerBl8\">\n</FORM>\n");
        outputStream.println("<p><TABLE BORDER=2 cellspacing=0>\n<TR><TD><B>Counter Name</B></TD><TD><B>Counter Value</B></TD><TD><B>Counter Description</B></TD></TR>" + s8 + "</TABLE><p>\n");
        outputStream.println("<BR><P>\nThe perfex command is:<BR>" + s15 + "\n" + "</P>\n" + "<P>The perfex response is:<BR>" + stringbuffer2.toString() + "</P>\n");
        printFooter(outputStream);
    }

    private Array execNTorSSH(String s, String s1, com.dragonflow.Utils.CommandLine commandline)
    {
        String s2 = s;
        if(s2.startsWith("\\\\"))
        {
            s2 = s2.substring(2);
        }
        com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getNTMachine(s2);
        Array array = new Array();
        boolean flag = false;
        if(machine == null || !com.dragonflow.SiteView.Machine.isNTSSH(s))
        {
            array = commandline.exec(s1, com.dragonflow.SiteView.Platform.getLock(s));
        }
        cmdToPrint = s1;
        if(array == null)
        {
            array = new Array();
        }
        return array;
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.perfCounterPage perfcounterpage = new perfCounterPage();
        if(args.length > 0)
        {
            perfcounterpage.args = args;
        }
        perfcounterpage.handleRequest();
    }

}
