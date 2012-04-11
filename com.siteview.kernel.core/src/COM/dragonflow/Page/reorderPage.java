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

import jgl.Array;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.SiteView.CompareSlot;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class reorderPage extends COM.dragonflow.Page.CGI
    implements jgl.BinaryPredicate
{

    static String POSITION_VARIABLE = "pos";
    public static final int SORT_NAME = 1;
    public static final int SORT_STATUS = 2;
    public static final int SORT_MONITOR_TYPE = 3;
    public static final int SORT_CATEGORY = 4;
    public static final int SORT_GROUP = 5;
    public static final int SORT_ID = 6;
    public static final int SORT_MACHINE = 7;
    int sortKey;
    int secondaryKey;

    public reorderPage()
    {
        sortKey = 4;
        secondaryKey = 1;
    }

    public COM.dragonflow.Page.CGI.menus getNavItems(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        COM.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
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

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_groupEdit"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("group");
        String s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s);
        if(s1.length() > 0 && !COM.dragonflow.Page.CGI.isGroupAllowedForAccount(s1, request))
        {
            throw new HTTPRequestException(557);
        }
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSiteView();
        COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewobject.getElement(COM.dragonflow.Page.reorderPage.getGroupIDRelative(s1));
        if(monitorgroup == null)
        {
            COM.dragonflow.Page.reorderPage.printError(outputStream, "Could not find group", "group with id " + s + " was not found.", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
        } else
        if(request.isPost())
        {
            jgl.Array array = ReadGroupFrames(s1);
            jgl.Array array1 = new Array();
            jgl.Array array2 = new Array();
            for(int i = 0; i < array.size(); i++)
            {
                jgl.HashMap hashmap = (jgl.HashMap)array.at(i);
                if(i == 0)
                {
                    array1.add(hashmap);
                    continue;
                }
                if(!COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
                {
                    array1.add(hashmap);
                } else
                {
                    array2.add(hashmap);
                }
            }

            String s3 = request.getValue("operation");
            if(s3.indexOf("Alpha") >= 0)
            {
                jgl.Sorting.sort(array2, new CompareSlot("_name", COM.dragonflow.SiteView.CompareSlot.DIRECTION_LESS, COM.dragonflow.SiteView.CompareSlot.STRING_COMPARE));
            } else
            if(s3.indexOf("Position") >= 0)
            {
                for(Enumeration enumeration = request.getVariables(); enumeration.hasMoreElements(); jgl.Sorting.sort(array2, new CompareSlot("sortKey", COM.dragonflow.SiteView.CompareSlot.DIRECTION_LESS, COM.dragonflow.SiteView.CompareSlot.NUMERIC_COMPARE)))
                {
                    String s5 = (String)enumeration.nextElement();
                    if(!s5.startsWith(POSITION_VARIABLE))
                    {
                        continue;
                    }
                    String s6 = s5.substring(POSITION_VARIABLE.length());
                    int l = COM.dragonflow.Page.reorderPage.findMonitorIndex(array, s6);
                    if(l >= 0)
                    {
                        jgl.HashMap hashmap1 = (jgl.HashMap)array.at(l);
                        hashmap1.put("sortKey", request.getValue(s5));
                    }
                }

            }
            for(Enumeration enumeration1 = array2.elements(); enumeration1.hasMoreElements(); array1.add(enumeration1.nextElement())) { }
            WriteGroupFrames(s1, array1);
            COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s1);
            printRefreshHeader("", COM.dragonflow.SiteView.Monitor.getGroupDetailLink(request, monitorgroup), 0);
        } else
        {
            printBodyHeader("Reorder Group");
            COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("EditGroup.htm#reorder", s1, menus1);
            String s2 = monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pName);
            jgl.Array array3 = COM.dragonflow.Utils.TextUtils.enumToArray(monitorgroup.getMonitors());
            int j = array3 == null ? 0 : array3.size();
            outputStream.println("<H2>Reorder Monitors in <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, COM.dragonflow.Utils.I18N.UnicodeToString(s1)) + ">" + s2 + "</H2><P>" + "Reorder monitors in the group by <A HREF=#position>changing their position</A>, or <A HREF=#sort>sorting</A> them alphabetically by name." + "<P>");
            if(s2.length() > 0)
            {
                outputStream.println("<A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, COM.dragonflow.Utils.I18N.UnicodeToString(s1)) + ">Return to Detail</A><P>");
            } else
            {
                outputStream.println("<A HREF=/SiteView/" + request.getAccountDirectory() + "/SiteView.html" + ">Return to Main Page</A><P>");
            }
            String s4 = request.getValue("_health").length() <= 0 ? "" : "<input type=hidden name=_health value=true>";
            if(j > 1)
            {
                outputStream.println(getPagePOST("reorder", "") + "<INPUT TYPE=HIDDEN NAME=group VALUE=" + s + ">" + s4 + "<HR><A NAME=position><B>By Position</B></A>\n" + "<BLOCKQUOTE><DL>\n" + "<DT>\n" + "\t<INPUT TYPE=SUBMIT NAME=operation VALUE=\"Reorder by Position\">\n" + "<DD>Set the new position(s) of the monitors based on the positions set in the table below.\n" + "</DL></BLOCKQUOTE>\n");
                outputStream.println("<TABLE BORDER=1 cellspacing=0>");
                for(int k = 0; k < j; k++)
                {
                    COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)array3.at(k);
                    outputStream.println("<TR><TD><SELECT SIZE=1 NAME=pos" + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pID) + ">" + getPositionOptions(j, k + 1) + "</SELECT></TD>" + "<TD>" + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName) + "</TD></TR>");
                }

                outputStream.println("</TABLE><P><HR><A NAME=sort><B>Sort</B></A>\n<BLOCKQUOTE><DL>\n<DT>\n\t<INPUT TYPE=SUBMIT NAME=operation VALUE=\"Reorder Alphabetically\"><DD>Sort the monitors in alphabetical order based on their names.\n</DL></BLOCKQUOTE>\n");
            } else
            {
                outputStream.println("<b>Two or more monitors are needed in order to reorder them.</b><p>");
            }
            printFooter(outputStream);
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.reorderPage reorderpage = new reorderPage();
        if(args.length > 0)
        {
            reorderpage.args = args;
        }
        reorderpage.handleRequest();
    }

    String getPositionOptions(int i, int j)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<OPTION VALUE=\"0\">first</OPTION>\n");
        for(int k = 1; k <= i; k++)
        {
            String s = "";
            if(k == j)
            {
                s = "SELECTED";
            }
            stringbuffer.append("<OPTION VALUE=\"" + k + "\" " + s + " >" + k + "</OPTION>\n");
            if(k < i)
            {
                stringbuffer.append("<OPTION VALUE=\"" + k + ".5\">after " + k + "</OPTION>\n");
            }
        }

        stringbuffer.append("<OPTION VALUE=\"" + (i + 1) + "\">last</OPTION>\n");
        return stringbuffer.toString();
    }

    public static int sortType(String s)
    {
        if(s.equals("status"))
        {
            return 2;
        }
        if(s.equals("type"))
        {
            return 3;
        }
        if(s.equals("name"))
        {
            return 1;
        }
        if(s.equals("category"))
        {
            return 4;
        }
        if(s.equals("group"))
        {
            return 5;
        }
        if(s.equals("id"))
        {
            return 6;
        }
        return !s.equals("machine") ? 4 : 7;
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1)
    {
        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)obj;
        COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor)obj1;
        int i = compareMonitors(monitor, monitor1, sortKey);
        if(i == 0)
        {
            i = compareMonitors(monitor, monitor1, secondaryKey);
        }
        return i > 0;
    }

    public int compareMonitors(COM.dragonflow.SiteView.Monitor monitor, COM.dragonflow.SiteView.Monitor monitor1, int i)
    {
        int j = 0;
        switch(i)
        {
        case 3: // '\003'
        case 6: // '\006'
        default:
            break;

        case 5: // '\005'
            j = monitor1.getProperty(COM.dragonflow.SiteView.Monitor.pGroupID).compareTo(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pGroupID));
            break;

        case 7: // '\007'
            j = monitor1.getHostname().compareTo(monitor.getHostname());
            break;

        case 1: // '\001'
            String s = monitor1.getProperty(COM.dragonflow.SiteView.Monitor.pName).toLowerCase();
            String s1 = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName).toLowerCase();
            j = s.compareTo(s1);
            break;

        case 2: // '\002'
            j = monitor1.getProperty(COM.dragonflow.SiteView.Monitor.pStateString).compareTo(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pStateString));
            break;

        case 4: // '\004'
            java.lang.Integer integer = (java.lang.Integer)COM.dragonflow.SiteView.MonitorGroup.categoryMap.get(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pCategory));
            java.lang.Integer integer1 = (java.lang.Integer)COM.dragonflow.SiteView.MonitorGroup.categoryMap.get(monitor1.getProperty(COM.dragonflow.SiteView.Monitor.pCategory));
            if(integer != null && integer1 != null)
            {
                j = integer.intValue() - integer1.intValue();
            }
            break;
        }
        return j;
    }

}
