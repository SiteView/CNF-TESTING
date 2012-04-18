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

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// portalChooserPage

public class portalManagePage extends COM.dragonflow.Page.portalChooserPage
{

    public static boolean debug = false;
    private static final int ACTION_DISABLE = 1;
    private static final int ACTION_ENABLE = 2;
    private static final int ACTION_REFRESH = 3;
    private static final int ACTION_REPLACE = 4;
    private static String ACTION_NAMES[] = {
        "Enable", "Disable", "Refresh", "Replace"
    };
    private static HashMap EXCLUDE_MAP;
    private HashMap groups;

    public portalManagePage()
    {
        super("Manage Monitors and Groups", "ManageMon.htm", false);
        groups = new HashMap();
    }

    protected void printButtons(java.io.PrintWriter printwriter)
    {
        printButton(printwriter, "Move", "#move", "For groups, make the selected groups a subgroup of a specified group.<BR>For monitors, move the selected monitors to a different group.");
        printButton(printwriter, "Copy", "#copy", "For groups, copy the selected groups and all of their subgroups.<BR>For monitors, copy the selected monitors to a different group.");
        printButton(printwriter, "Duplicate", "#duplicate", "For groups, copy and rename the selected groups and all of their subgroups.<BR>For monitors, copy and rename the selected monitors.");
        printButton(printwriter, "Delete", "#delete", "For groups, delete the selected groups and all of their subgroups.<BR>For monitors, delete the selected monitors.");
        printButton(printwriter, "Disable", "#disable", "For groups, disable all monitors in the selected groups and in all of their subgroups.<BR>For monitors, disable the selected monitors.");
        printButton(printwriter, "Enable", "#enable", "For groups, enable all monitors in the selected groups and in all of their subgroups.<BR>For monitors, enable the selected monitors.");
        printButton(printwriter, "Refresh", "#refresh", "For groups, refresh all monitors in the selected groups and in all of their subgroups.<BR>For monitors, refresh the selected monitors.");
        printButton(printwriter, "Replace", "#replace", "Replace values in the selected groups and monitors");
    }

    protected void handlePost(String s, Array array, Array array1, Array array2)
        throws java.lang.Exception
    {
        printRefreshHeader(s + " Items", getReturnURL(), 10);
        if(debug)
        {
            COM.dragonflow.Utils.TextUtils.debugPrint("IS POST TO PORTAL MANAGE PAGE");
        }
        printProgressMessage("<P><FONT SIZE=+1><B>" + s + "</B></FONT><P>");
        if(!s.startsWith("Delete") && (s.startsWith("Replace") || s.startsWith("Disable") || s.startsWith("Enable") || s.startsWith("Refresh")))
        {
            byte byte0 = 0;
            if(s.startsWith("Disable"))
            {
                byte0 = 1;
            } else
            if(s.startsWith("Enable"))
            {
                byte0 = 2;
            } else
            if(s.startsWith("Refresh"))
            {
                byte0 = 3;
            } else
            if(s.startsWith("Replace"))
            {
                byte0 = 4;
            }
            String as[] = getReplaces();
            if(array2.size() > 0)
            {
                printProgressMessage(byte0 + " monitor" + (array2.size() <= 1 ? "" : "s") + "<BR>");
                COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
                for(Enumeration enumeration =  (Enumeration) array2.iterator(); enumeration.hasMoreElements(); modifyMonitor(atomicmonitor, byte0, as))
                {
                    atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)enumeration.nextElement();
                }

            }
            if(array1.size() > 0)
            {
                printProgressMessage(byte0 + " group" + (array1.size() <= 1 ? "" : "s") + "<BR>");
                COM.dragonflow.SiteView.MonitorGroup monitorgroup;
                for(Enumeration enumeration1 =  (Enumeration) array1.iterator(); enumeration1.hasMoreElements(); modifyGroup(monitorgroup, byte0, as))
                {
                    monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)enumeration1.nextElement();
                }

            }
            saveGroups();
        }
        printProgressMessage("<P><B>Done</B><P>");
        outputStream.println("<A HREF=" + getReturnURL() + ">" + getReturnLabel() + "</A><P>");
        outputStream.println("</BODY></HTML>");
    }

    protected void performOperation(String s, Enumeration enumeration)
        throws java.lang.Exception
    {
        printBodyHeader(s + " Items");
        String s1 = helpFileName;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        if(s.startsWith("Move"))
        {
            flag1 = true;
            flag2 = true;
            s1 = s1 + "#move";
        } else
        if(s.startsWith("Copy"))
        {
            flag = true;
            s1 = s1 + "#copy";
        } else
        if(s.startsWith("Duplicate"))
        {
            s1 = s1 + "#duplicate";
            flag = true;
        } else
        if(s.startsWith("Delete"))
        {
            s1 = s1 + "#delete";
        } else
        if(s.startsWith("Disable"))
        {
            s1 = s1 + "#disable";
        } else
        if(s.startsWith("Enable"))
        {
            s1 = s1 + "#enable";
        } else
        if(s.startsWith("Refresh"))
        {
            s1 = s1 + "#refresh";
        } else
        if(s.startsWith("Replace"))
        {
            flag3 = true;
            s1 = s1 + "#replace";
        }
        printButtonBar(s1, "");
        outputStream.println("<FONT SIZE=+1><B>" + s + " the items:</B></FONT><BR>");
        printPostFormHeader(outputStream);
        outputStream.println("<TABLE BORDER=1><TR><TH>items</TH>");
        if(flag)
        {
            outputStream.print("<TH>New Name</TH>");
        }
        outputStream.println("</TR>");
        java.lang.Object obj;
        for(; enumeration.hasMoreElements(); printPostFormItem(outputStream, obj))
        {
            obj = enumeration.nextElement();
            outputStream.print("<TR><TD>");
            outputStream.print((String)obj);
            outputStream.println("</TD></TR>");
        }

        outputStream.println("</TABLE>");
        if(flag1 || flag || flag2)
        {
            String s2;
            if(flag1)
            {
                s2 = "to";
            } else
            {
                s2 = "into";
            }
            outputStream.println("<P>" + s2 + " Group <select name=toGroupID size=1>" + "</select>");
        }
        if(flag)
        {
            outputStream.println("<p><HR><H3>Advanced Options</H3>");
        }
        if(flag || flag3)
        {
            outputStream.println("<P><TABLE BORDER=0><TR><TD ALIGN=RIGHT><B>Find</B></TD><TD><input type=text size=50 name=find></TD></TR><TR><TD ALIGN=RIGHT><B>Replace With</B></TD><TD><input type=text size=50 name=replace></TD></TR><TR><TD>&nbsp;<TD><FONT SIZE=-1>find and replace text in monitors and groups\n- for example, replace server1.mycompany.com with server2.mycompany.com, or replace \\\\SERVER1 with \\\\SERVER2</TD></TR></TABLE>");
        }
        outputStream.println("<P><TABLE WIDTH=100% BORDER=0><TR><TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit VALUE=\"" + s + " items\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=" + getReturnURL() + ">" + getReturnLabel() + "</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE>");
        printPostFormFooter(outputStream);
    }

    private String getReturnURL()
    {
        String s = request.getValue("returnURL");
        if(s.length() == 0)
        {
            s = "/SiteView/cgi/go.exe/SiteView?page=portalManage";
        } else
        if(s.startsWith("Detail") || s.equals("SiteView"))
        {
            s = COM.dragonflow.SiteView.Platform.getURLPath("htdocs", request.getAccount()) + "/" + s + ".html";
        }
        return s;
    }

    private String getReturnLabel()
    {
        String s = request.getValue("returnLabel");
        if(s.length() == 0)
        {
            s = "Return to Previous";
        } else
        {
            s = "Return to " + s;
        }
        return s;
    }

    private String[] getReplaces()
    {
        String as[] = new String[0];
        String s = request.getValue("find");
        String s1 = request.getValue("replace");
        if(s.length() > 0)
        {
            as = new String[2];
            as[0] = s;
            as[1] = s1;
            COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getMachineByName(s);
            if(machine != null)
            {
                COM.dragonflow.SiteView.Machine machine1 = COM.dragonflow.SiteView.Machine.getMachineByName(s1);
                if(machine1 != null)
                {
                    as = new String[6];
                    as[0] = s;
                    as[1] = COM.dragonflow.SiteView.Machine.REMOTE_PREFIX + machine.getProperty(COM.dragonflow.SiteView.Machine.pID);
                    as[2] = machine.getProperty(COM.dragonflow.SiteView.Machine.pHost);
                    as[3] = s1;
                    as[4] = COM.dragonflow.SiteView.Machine.REMOTE_PREFIX + machine1.getProperty(COM.dragonflow.SiteView.Machine.pID);
                    as[5] = machine1.getProperty(COM.dragonflow.SiteView.Machine.pHost);
                }
            }
        } else
        {
            as = new String[0];
        }
        return as;
    }

    private Array getGroupFrames(String s)
        throws java.lang.Exception
    {
        Array array = (Array)groups.get(s);
        if(array == null)
        {
            array = ReadGroupFrames(s);
            groups.put(s, array);
            printProgressMessage("Read group configuration for " + COM.dragonflow.Page.portalManagePage.getGroupName(array, s) + "<BR>");
        }
        return array;
    }

    private void saveGroups()
        throws java.lang.Exception
    {
        Enumeration enumeration = (Enumeration) groups.keys();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            Array array = (Array)groups.get(s);
            if(array != null)
            {
                printProgressMessage("Saving group configuration for " + COM.dragonflow.Page.portalManagePage.getGroupName(array, s) + "<BR>");
                WriteGroupFrames(s, array);
                if(debug)
                {
                    COM.dragonflow.Utils.TextUtils.debugPrint("SAVED GROUP=" + s);
                }
            } else
            {
                java.lang.System.out.println("TRIED TO SAVE OUT EMPTY GROUP=" + s);
            }
        } while(true);
        COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
        groups.clear();
    }

    private void refreshMonitor(String s, String s1, int i)
    {
        String s2 = s + COM.dragonflow.SiteView.SiteViewGroup.ID_SEPARATOR + s1;
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)siteviewgroup.getElement(s2);
        refreshMonitor(monitor, i);
    }

    private void refreshMonitor(COM.dragonflow.SiteView.Monitor monitor, int i)
    {
        String s = "0";
        if(i == 3)
        {
            s = "-1";
        }
        if(monitor != null)
        {
            monitor.setProperty(COM.dragonflow.SiteView.Monitor.pLastUpdate, s);
        }
    }

    private void modifyMonitor(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, int i, String as[])
        throws java.lang.Exception
    {
        String s = atomicmonitor.getFullID();
        int j = s.indexOf(' ');
        if(j >= 0)
        {
            if(debug)
            {
                COM.dragonflow.Utils.TextUtils.debugPrint(ACTION_NAMES[i] + " MONITOR=" + s);
            }
            String s1 = s.substring(0, j);
            String s2 = s.substring(j + 1);
            Array array = getGroupFrames(s1);
            HashMap hashmap = COM.dragonflow.Page.portalManagePage.findMonitor(array, s2);
            printProgressMessage(ACTION_NAMES[i] + " " + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_name") + "<BR>");
            if(i == 1)
            {
                hashmap.put("_disabled", "checked");
            } else
            if(i == 1)
            {
                hashmap.put("_disabled", "");
            } else
            if(i == 4 && as.length > 0 && COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
            {
                printProgressMessage(COM.dragonflow.Utils.TextUtils.replaceInHashMap(hashmap, as, EXCLUDE_MAP));
            }
            refreshMonitor(atomicmonitor, i);
        }
    }

    private void modifyGroup(COM.dragonflow.SiteView.MonitorGroup monitorgroup, int i, String as[])
        throws java.lang.Exception
    {
        modifyGroup(monitorgroup.getFullID(), i, as);
    }

    private void modifyGroup(String s, int i, String as[])
        throws java.lang.Exception
    {
        Array array = getGroupFrames(s);
        String s1 = COM.dragonflow.Page.portalManagePage.getGroupName(array, s);
        if(debug)
        {
            COM.dragonflow.Utils.TextUtils.debugPrint(ACTION_NAMES[i] + " GROUP=" + s);
        }
        printProgressMessage(ACTION_NAMES[i] + " " + s1 + " Group<BR>");
        for(int j = 1; j < array.size(); j++)
        {
            HashMap hashmap = (HashMap)array.get(j);
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_class");
            if(s2.equals("SubGroup"))
            {
                String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_group");
                if(s3.length() != 0)
                {
                    modifyGroup(s3, i, as);
                }
                continue;
            }
            if(i == 1)
            {
                hashmap.put("_disabled", "checked");
            } else
            if(i == 2)
            {
                hashmap.put("_disabled", "");
            } else
            if(i == 4 && as.length > 0 && COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
            {
                printProgressMessage(COM.dragonflow.Utils.TextUtils.replaceInHashMap(hashmap, as, EXCLUDE_MAP));
            }
            refreshMonitor(s, COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id"), i);
        }

    }

    static 
    {
        EXCLUDE_MAP = null;
        EXCLUDE_MAP = new HashMap();
        EXCLUDE_MAP.put("_parent", "exclude");
        EXCLUDE_MAP.put("_group", "exclude");
        EXCLUDE_MAP.put("_class", "exclude");
        EXCLUDE_MAP.put("_id", "exclude");
        EXCLUDE_MAP.put("_nextID", "exclude");
        EXCLUDE_MAP.put("_nextConditionID", "exclude");
    }
}
