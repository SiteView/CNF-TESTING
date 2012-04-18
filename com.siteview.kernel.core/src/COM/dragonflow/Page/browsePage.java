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

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Page:
// CGI, monitorPage

public class browsePage extends COM.dragonflow.Page.CGI
    implements BinaryPredicate
{

    public static final int SORT_NAME = 1;
    public static final int SORT_STATUS = 2;
    public static final int SORT_MONITOR_TYPE = 3;
    public static final int SORT_CATEGORY = 4;
    public static final int SORT_GROUP = 5;
    public static final int SORT_ID = 6;
    public static final int SORT_MACHINE = 7;
    int sortKey;
    int secondaryKey;

    public browsePage()
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

    void printXML(COM.dragonflow.SiteView.Monitor monitor)
    {
        HashMap hashmap = monitor.getValuesTable();
        java.lang.Object obj;
        String s;
        for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements(); outputStream.println(COM.dragonflow.Utils.TextUtils.escapeXML("" + obj, s)))
        {
            obj = enumeration.nextElement();
            s = "" + hashmap.get(obj);
            s = s.replace('<', '*');
            s = s.replace('>', '*');
        }

    }

    /**
     * 
     */
    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_browse"))
        {
            throw new HTTPRequestException(557);
        }
        boolean flag = request.getValue("xml").length() > 0;
        if(flag && request.getValue("all").length() > 0)
        {
            outputStream.println("<?xml version=\"1.0\"?>");
            outputStream.println("<siteview>");
            Array array = getAllowedGroupIDs();
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            for(int i = 0; i < array.size(); i++)
            {
                String s2 = (String)array.get(i);
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s2);
                if(monitorgroup == null)
                {
                    continue;
                }
                Enumeration enumeration1 = monitorgroup.getMonitors();
                outputStream.println("<group>");
                printXML(monitorgroup);
                outputStream.println(TextUtils.escapeXML("category", monitorgroup.getProperty(COM.dragonflow.SiteView.MonitorGroup.pCategory)));
                outputStream.println("<monitors>");
                for(; enumeration1.hasMoreElements(); outputStream.println("</monitor>"))
                {
                    COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)enumeration1.nextElement();
                    outputStream.println("<monitor>");
                    printXML(monitor);
                }

                outputStream.println("</monitors>");
                outputStream.println("</group>");
            }

            outputStream.println("</siteview>");
            return;
        }
        sortKey = COM.dragonflow.Page.browsePage.sortType(request.getValue("sort"));
        String s = request.getValue("categorySelect");
        if(s.length() == 0)
        {
            s = COM.dragonflow.SiteView.Monitor.ERROR_CATEGORY + COM.dragonflow.SiteView.Monitor.WARNING_CATEGORY;
        }
        Enumeration enumeration = request.getValues("monitorTypeSelect");
        String s1;
        for(s1 = ""; enumeration.hasMoreElements(); s1 = s1 + enumeration.nextElement()) { }
        String s3 = request.getValue("statusSelect");
        String s4 = request.getValue("monitorNameSelect");
        String s5 = request.getValue("machineNameSelect");
        String s6 = request.getValue("refresh");
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup1 = null;
        HashMap hashmap = null;
        Array array1 = getAllowedGroupIDs();
        siteviewgroup1 = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s7 = siteviewgroup1.getValue("_browseRefreshDefault");
        if(s6.length() == 0)
        {
            s6 = s7;
        } else
        if(!s7.equals(s6))
        {
            siteviewgroup1.setProperty("_browseRefreshDefault", s6);
            siteviewgroup1.saveSettings();
        }
        hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s8 = request.getValue("onlyBrowserUnacknowledge");
        if(!flag)
        {
            String s9 = "";
            s9 = "/SiteView/cgi/go.exe/SiteView?page=browse";
            if(s6.length() > 0)
            {
                s9 = "/SiteView/cgi/go.exe/SiteView?page=browse&sort=" + request.getValue("sort") + "&account=" + request.getValue("account") + "&categorySelect=" + request.getValue("categorySelect") + "&statusSelect=" + request.getValue("statusSelect") + "&monitorNameSelect=" + request.getValue("monitorNameSelect") + "&machineNameSelect=" + request.getValue("machineNameSelect") + "&refresh=" + request.getValue("refresh") + "&onlyBrowserUnacknowledge=" + request.getValue("onlyBrowserUnacknowledge") + "&hide=" + request.getValue("hide");
                for(Enumeration enumeration2 = request.getValues("monitorTypeSelect"); enumeration2.hasMoreElements();)
                {
                    s9 = s9 + "&monitorTypeSelect=" + enumeration2.nextElement();
                }

            }
            String s10 = "Browse SiteView Monitors";
            s6 = getRefreshSelectHTML(s6, true);
            if(s6.length() > 0 && !s6.equals("0"))
            {
                printRefreshHeader(s10, s9, COM.dragonflow.Utils.TextUtils.toInt(s6));
            } else
            {
                printBodyHeader(s10);
            }
            COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("MonBrows.htm", "", menus1);
        }
        Array array2 = new Array();
        int j = 0;
label0:
        for(int k = 0; k < array1.size(); k++)
        {
            String s12 = (String)array1.get(k);
            COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup1.getElement(s12);
            if(monitorgroup1 == null)
            {
                continue;
            }
            Enumeration enumeration4 = monitorgroup1.getMonitors();
            do
            {
                COM.dragonflow.SiteView.Monitor monitor2;
                do
                {
                    if(!enumeration4.hasMoreElements())
                    {
                        continue label0;
                    }
                    monitor2 = (COM.dragonflow.SiteView.Monitor)enumeration4.nextElement();
                } while(monitor2 instanceof COM.dragonflow.SiteView.SubGroup);
                j++;
                if(!s.equals("ALL"))
                {
                    String s17 = monitor2.getProperty(COM.dragonflow.SiteView.Monitor.pCategory);
                    if(monitor2.isDisabled())
                    {
                        s17 = "DISABLED";
                    } else
                    if(monitor2.isAcknowledged() && s.startsWith("ACKNOWLEDGE"))
                    {
                        s17 = "ACKNOWLEDGED";
                    } else
                    if((s.startsWith(COM.dragonflow.SiteView.Monitor.ERROR_CATEGORY) || s.startsWith(COM.dragonflow.SiteView.Monitor.WARNING_CATEGORY)) && s8.length() > 0 && monitor2.isAcknowledged())
                    {
                        continue;
                    }
                    if(s.startsWith("NOT") ? s.substring(3).indexOf(s17) != -1 : s.indexOf(s17) == -1)
                    {
                        continue;
                    }
                }
                if(s3.length() > 0)
                {
                    String s19 = monitor2.getTableStatusString(request);
                    Array array3 = new Array();
                    int l = COM.dragonflow.Utils.TextUtils.matchExpression(s19, s3, array3, new StringBuffer());
                    if(l != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        l = COM.dragonflow.Utils.TextUtils.matchExpression(s19, s3, array3, new StringBuffer());
                    }
                    if(l != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        continue;
                    }
                }
                if(s4.length() > 0)
                {
                    String s20 = monitor2.getProperty(COM.dragonflow.SiteView.Monitor.pName) + monitor2.getProperty(COM.dragonflow.SiteView.Monitor.pDescription);
                    Array array4 = new Array();
                    int i1 = COM.dragonflow.Utils.TextUtils.matchExpression(s20, s4, array4, new StringBuffer());
                    if(i1 != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        i1 = COM.dragonflow.Utils.TextUtils.matchExpression(s20, s4, array4, new StringBuffer());
                    }
                    if(i1 != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        continue;
                    }
                }
                if(s5.length() > 0)
                {
                    String s21 = monitor2.getHostname();
                    Array array5 = new Array();
                    int j1 = COM.dragonflow.Utils.TextUtils.matchExpression(s21, s5, array5, new StringBuffer());
                    if(j1 != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        j1 = COM.dragonflow.Utils.TextUtils.matchExpression(s21, s5, array5, new StringBuffer());
                    }
                    if(j1 != COM.dragonflow.SiteView.Monitor.kURLok)
                    {
                        continue;
                    }
                }
                if(s1.length() <= 0 || s1.indexOf((String)monitor2.getClassProperty("class")) != -1)
                {
                    array2.add(monitor2);
                }
            } while(true);
        }

        if(array2.size() > 1)
        {
            Sorting.sort(array2, this);
        }
        if(flag)
        {
            outputStream.println("<?xml version=\"1.0\"?>");
            outputStream.println("<siteview>");
            for(Enumeration enumeration3 =  (Enumeration) array2.iterator(); enumeration3.hasMoreElements(); outputStream.println("</monitor>"))
            {
                COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor)enumeration3.nextElement();
                outputStream.println("<monitor>");
                printXML(monitor1);
            }

            outputStream.println("</siteview>");
        } else
        {
            String s11 = request.getValue("sort").equals("group") ? "selected" : "";
            String s13 = request.getValue("sort").equals("name") ? "selected" : "";
            String s14 = request.getValue("sort").equals("status") ? "selected" : "";
            String s15 = request.getValue("sort").equals("category") ? "selected" : "";
            String s16 = request.getValue("sort").equals("machine") ? "selected" : "";
            String s18 = request.getValue("hide");
            if(s18.length() == 0)
            {
                outputStream.println("<br><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=GET><input type=hidden name=page value=browse><input type=hidden name=account value=" + request.getAccount() + ">" + "<input type=hidden name=hide value=\"checked\">" + "<input type=hidden name=machineNameSelect value=\"" + s5 + "\">" + "<input type=hidden name=sort value=" + request.getValue("sort") + ">" + "<input type=hidden name=monitorNameSelect value=\"" + s4 + "\">");
                for(Enumeration enumeration5 = request.getValues("monitorTypeSelect"); enumeration5.hasMoreElements(); outputStream.println("<input type=hidden name=monitorTypeSelect value=" + enumeration5.nextElement() + ">")) { }
                outputStream.println("<input type=hidden name=refresh value=" + request.getValue("refresh") + ">" + "<input type=hidden name=statusSelect value=\"" + s3 + "\">" + "<input type=hidden name=categorySelect value=" + request.getValue("categorySelect") + ">");
                outputStream.println("<input type=submit value=\"&lt;&lt; Hide Controls\">");
                outputStream.println("</form>");
                outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=GET><input type=hidden name=page value=browse><input type=hidden name=account value=" + request.getAccount() + ">");
                outputStream.println("<table border=0 width=100% cellspacing=5><tr><td colspan=8><hr></td></tr>");
                outputStream.println("<tr>");
                String as[] = {
                    COM.dragonflow.SiteView.Monitor.ERROR_CATEGORY + COM.dragonflow.SiteView.Monitor.WARNING_CATEGORY, COM.dragonflow.SiteView.Monitor.ERROR_CATEGORY, COM.dragonflow.SiteView.Monitor.WARNING_CATEGORY, COM.dragonflow.SiteView.Monitor.GOOD_CATEGORY, COM.dragonflow.SiteView.Monitor.NODATA_CATEGORY, "DISABLED", "ACKNOWLEDGED"
                };
                String as1[] = {
                    "Error or Warning", "Error", "Warning", "OK", "No Data", "Disabled", "Acknowledged"
                };
                outputStream.println("<td valign=top>Category to Show/Hide: </td><td><select name=categorySelect>");
                boolean flag1 = COM.dragonflow.Utils.TextUtils.getValue(getMasterConfig(), "_acknowledgeMonitors").equalsIgnoreCase("CHECKED");
                for(int k1 = 0; k1 < 2; k1++)
                {
                    String s26 = k1 != 0 ? "Hide " : "Show ";
                    String s27 = k1 != 0 ? "NOT" : "";
                    for(int l1 = 0; l1 < as1.length; l1++)
                    {
                        if(!as1[l1].equals("Acknowledged") || flag1)
                        {
                            outputStream.println("<option value=" + s27 + as[l1] + (s.equals(s27 + as[l1]) ? " selected>" : ">") + s26 + as1[l1] + "</option>\n");
                        }
                    }

                }

                outputStream.println("<option value=ALL" + (s.equals("ALL") ? " selected" : "") + ">Show All Categories</option>\n");
                outputStream.println("</select></td>\n");
                outputStream.println("<td valign=top>&nbsp;&nbsp;Match Status:</td><td valign=top><input type=text name=statusSelect size=15 value=\"" + s3 + "\"></td>");
                outputStream.println("<td valign=top>&nbsp;&nbsp;Refresh Option:</td><td valign=top>");
                getRefreshSelectHTML(s6, false);
                outputStream.println("</td>");
                outputStream.println("</tr>");
                outputStream.println("<tr>");
                outputStream.println(getMonitorTypeSelectHTML(s1));
                outputStream.println("<td valign=top>&nbsp;&nbsp;Match Name:</td><td valign=top><input type=text name=monitorNameSelect size=15 value=\"" + s4 + "\"></td>");
                outputStream.println("</tr>");
                outputStream.println("<tr>");
                outputStream.println("<td valign=top>Sort by: </td><td valign=top><select size=1 name=sort>\n<option value=category " + s15 + ">status</option>\n" + "<option value=group " + s11 + ">group</option>\n" + "<option value=name " + s13 + ">name</option>\n" + "<option value=status " + s14 + ">status text</option>\n" + "<option value=machine " + s16 + ">machine</option>\n" + "</select></td>\n");
                outputStream.println("<td valign=top>&nbsp;Match Machine:</td><td valign=top><input type=text name=machineNameSelect size=15 value=\"" + s5 + "\"></td>");
                outputStream.println("<td valign=top>&nbsp;</td>");
                outputStream.println("<td valign=top><input type=submit value=\"Update and Refresh\"></td>");
                outputStream.println("</tr>");
                outputStream.println("</table><br clear=all>");
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_acknowledgeMonitors").equalsIgnoreCase("CHECKED"))
                {
                    String s25 = s8.equals("CHECKED") ? "checked" : "";
                    outputStream.println("<input type=checkbox " + s25 + " value=CHECKED name=onlyBrowserUnacknowledge" + COM.dragonflow.Page.browsePage.getValue(hashmap, "_onlyBrowserUnacknowledged") + ">Only display unacknowledged monitors that are in error or warning\n<br>");
                }
                outputStream.println("</form>");
            } else
            {
                outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=GET><input type=hidden name=page value=browse><input type=hidden name=account value=" + request.getAccount() + ">" + "<input type=hidden name=hide value=>" + "<input type=hidden name=machineNameSelect value=\"" + s5 + "\">" + "<input type=hidden name=sort value=" + request.getValue("sort") + ">" + "<input type=hidden name=monitorNameSelect value=\"" + s4 + "\">");
                for(Enumeration enumeration6 = request.getValues("monitorTypeSelect"); enumeration6.hasMoreElements(); outputStream.println("<input type=hidden name=monitorTypeSelect value=" + enumeration6.nextElement() + ">")) { }
                outputStream.println("<input type=hidden name=refresh value=" + request.getValue("refresh") + ">" + "<input type=hidden name=statusSelect value=\"" + s3 + "\">" + "<input type=hidden name=categorySelect value=" + request.getValue("categorySelect") + ">");
                outputStream.println("<input type=submit value=\"Show Controls &gt;&gt;\">");
                outputStream.println("</form>");
            }
            int ai[] = {
                COM.dragonflow.SiteView.MonitorGroup.CATEGORY_COLUMN, COM.dragonflow.SiteView.MonitorGroup.ACKNOWLEDGE_COLUMN, COM.dragonflow.SiteView.MonitorGroup.GAUGE_COLUMN, COM.dragonflow.SiteView.MonitorGroup.STATUS_COLUMN, COM.dragonflow.SiteView.MonitorGroup.NAME_COLUMN, COM.dragonflow.SiteView.MonitorGroup.GROUP_COLUMN, COM.dragonflow.SiteView.MonitorGroup.EDIT_COLUMN, COM.dragonflow.SiteView.MonitorGroup.REFRESH_COLUMN, COM.dragonflow.SiteView.MonitorGroup.CUSTOM_COLUMN, COM.dragonflow.SiteView.MonitorGroup.MACHINE_COLUMN, 
                COM.dragonflow.SiteView.MonitorGroup.UPDATED_COLUMN, COM.dragonflow.SiteView.MonitorGroup.ALERT_COLUMN, COM.dragonflow.SiteView.MonitorGroup.REPORT_COLUMN
            };
            String s22 = request.actionAllowed("_browse") ? "<br><font size=\"-1\" style=\"font-weight:normal\"> Use the <A HREF=/SiteView/cgi/go.exe/SiteView?page=monitorSummary&account=" + request.getAccount() + " >Monitor Description Report</a> to view current monitor configuration settings.</font>" : "";
            String s23 = "<h2>Browse Monitors (" + array2.size() + " out of " + j + ")</h2>" + s22;
            if(request.getValue("justTitle").length() == 0)
            {
                String s24 = COM.dragonflow.SiteView.MonitorGroup.printMonitorTable(outputStream, request, s23, "", ai,  (Enumeration) array2.iterator());
                COM.dragonflow.SiteView.MonitorGroup.printCategoryInsertHTML(s24, siteviewgroup1, outputStream);
            }
            printFooter(outputStream);
        }
    }

    String getRefreshSelectHTML(String s, boolean flag)
    {
        String as[] = {
            "0", "15", "30", "60", "120", "300"
        };
        String as1[] = {
            "Manual refresh", "Refresh 15 seconds", "Refresh 30 seconds", "Refresh every minute", "Refresh every two minutes", "Refresh every five minutes"
        };
        String s1 = "<select size=1 name=refresh>\n";
        String s2 = new String(s);
        for(int i = 0; i < as.length && !s.equals(as[i]); i++)
        {
            if(i == as.length - 1)
            {
                s2 = new String(as[0]);
            }
        }

        for(int j = 0; j < as.length; j++)
        {
            String s3 = s2.equals(as[j]) ? " selected" : "";
            s1 = s1 + "<option value=\"" + as[j] + "\"" + s3 + ">" + as1[j] + "</option>\n";
        }

        s1 = s1 + "</select>\n";
        if(!flag)
        {
            outputStream.println(s1);
        }
        return s2;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    String getMonitorTypeSelectHTML(String s)
    {
        String s1;
        Enumeration enumeration;
        Array array1;
        s1 = "<td valign=top>For Monitor Type: </td><td valign=top><SELECT NAME=monitorTypeSelect><option value=\"\">All types</option>\n";
        Array array = COM.dragonflow.Page.monitorPage.getMonitorClasses(true);
        enumeration = (Enumeration) array.iterator();
        array1 = _getUsedMonitorClasses();

        java.lang.Class class1;
        while (enumeration.hasMoreElements())
        {
        class1 = (java.lang.Class)enumeration.nextElement();
        COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
        String s3;
        try
        {
        atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)class1.newInstance();
        s3 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s3.length() == 0)
        {
            s3 = request.getPermission("_monitorType", "default");
        }
        if(!s3.equals("hidden"))
        {
                String s4 = (String)atomicmonitor.getClassProperty("title");
                String s5 = (String)atomicmonitor.getClassProperty("class");
                if(array1.indexOf(s5) != -1)
                {
                    String s2 = s.indexOf(s5) == -1 ? "" : " selected";
                    s1 = s1 + "<option value=" + s5 + s2 + ">" + s4 + "</option>\n";
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println("Could not create instance of " + class1);
        }
        }

        s1 = s1 + "</select></td>\n";
        return s1;
    }

    public void printCGIHeader()
    {
        request.printHeader(outputStream);
    }

    public void printCGIFooter()
    {
        outputStream.println("");
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.browsePage browsepage = new browsePage();
        if(args.length > 0)
        {
            browsepage.args = args;
        }
        browsepage.handleRequest();
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
