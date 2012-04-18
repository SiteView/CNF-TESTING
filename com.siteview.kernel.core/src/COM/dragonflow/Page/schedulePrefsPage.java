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

import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.StandardAction.Page;

// Referenced classes of package COM.dragonflow.Page:
// settingsPrefsPage, CGI

public class schedulePrefsPage extends COM.dragonflow.Page.settingsPrefsPage
{

    public static final String type = "Schedule";
    private static final String edit_range_tag = "new_range";
    private static final String edit_absolute_tag = "new_absolute";
    private static final String config_schedule_tag = "_additionalSchedule";
    private static final int show_list_mode = 0;
    private static final int edit_range_mode = 1;
    private static final int edit_absolute_mode = 2;

    public schedulePrefsPage()
    {
    }

    public boolean validateSchedule(StringBuffer stringbuffer)
    {
        COM.dragonflow.Properties.ScheduleProperty.requestToScheduleString(request, stringbuffer);
        return stringbuffer.length() == 0;
    }

    public String saveAdditionalSchedulePrefs()
    {
        HashMap hashmap = new HashMap();
        StringBuffer stringbuffer = new StringBuffer();
        String s = COM.dragonflow.Properties.ScheduleProperty.requestToScheduleString(request, stringbuffer);
        if(s.length() > 0)
        {
            hashmap.put("_schedule", s);
        }
        String s1 = request.getValue("name");
        if(s1.length() == 0)
        {
            boolean flag = false;
            if(s.startsWith("*"))
            {
                flag = true;
                s1 = s.substring(1);
            } else
            {
                s1 = s;
            }
            String s2 = COM.dragonflow.Properties.ScheduleProperty.dayStrings[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s1.substring(0, 1))];
            s1 = s1.substring(1);
            if(flag)
            {
                s2 = s2 + " at ";
            } else
            {
                if(s1.startsWith("D"))
                {
                    s2 = "Disable on " + s2;
                } else
                {
                    s2 = "Enable on " + s2;
                }
                s2 = s2 + " from ";
                s1 = s1.substring(1);
            }
            int i = s1.indexOf(",");
            String s4;
            if(i != -1)
            {
                s4 = s1.substring(0, i);
            } else
            {
                s4 = s1;
            }
            if(flag)
            {
                s2 = s2 + s4.replace('/', ',');
            } else
            {
                s2 = s2 + s4;
            }
            if(i != -1)
            {
                s2 = s2 + "...";
            }
            s1 = s2;
        }
        hashmap.put("_name", s1);
        String s3 = saveAdditionalPrefs("Schedule", request.getValue("schedule_data"), hashmap);
        COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
        schedulemanager.updateSchedule(s3, (String)hashmap.get("_schedule"));
        return s3;
    }

    void printPreferencesSaved()
    {
        printRefreshPage(getScheduleURL(), 0);
    }

    void savePreferences()
    {
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            if(!validateSchedule(stringbuffer))
            {
                printError("The schedule has an invalid time format", stringbuffer.toString(), getScheduleURL());
            } else
            {
                saveAdditionalSchedulePrefs();
                printPreferencesSaved();
            }
        }
        catch(java.lang.Exception exception)
        {
            printError("The preferences could not be saved", "master.config file could not be saved", getScheduleURL());
        }
    }

    void printDeleteForm(String s)
    {
        String s1 = request.getValue("schedule_data");
        HashMap hashmap = getSettings();
        HashMap hashmap1 = null;
        Enumeration enumeration = COM.dragonflow.Page.schedulePrefsPage.getValues(hashmap, "_additionalSchedule");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap2 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            if(COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap2, "_id").equals(s1))
            {
                hashmap1 = hashmap2;
            }
        } while(true);
        if(request.isPost())
        {
            COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
            if(!schedulemanager.hasMonitorReferences(s1))
            {
                deleteAdditionalSetting("Schedule", s1);
                printRefreshPage(getPageLink("schedulePrefs", "") + "#schedule_data", 0);
            } else
            {
                outputStream.print("<P>Unable to delete schedule when monitors are referencing this schedule.");
                outputStream.print("<p>&nbsp;</p><A HREF=" + getPageLink("schedulePrefs", "") + ">Back to Schedule Preferences</A></TD>" + "</FORM>\n");
            }
        } else
        {
            String s2 = "";
            if(hashmap1 != null)
            {
                s2 = COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_name");
            }
            outputStream.print("<FONT SIZE=+1><BR>Are you sure you want to delete the schedule item named <B>\"" + s2 + "\"</B>?</FONT>" + "<P>" + getPagePOST("schedulePrefs", s) + "<INPUT TYPE=HIDDEN NAME=schedule_data VALUE=\"" + s1 + "\">" + "<TABLE WIDTH=\"100%\" BORDER=0><TR>" + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\">" + "<INPUT type=submit VALUE=\"" + s + " Schedule Setting\"></TD>" + "<TD WIDTH=\"6%\"></TD>" + "<TD ALIGN=RIGHT WIDTH=\"41%\">" + "<A HREF=" + getScheduleURL() + ">Return to Schedule Preferences</A>" + "</TD><TD WIDTH=\"6%\"></TD></TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    void printDebugForm()
    {
        outputStream.print("<P><CENTER><H2>Debug Output for Schedule List</H2></CENTER><P>");
        HashMap hashmap = getSettings();
        HashMap hashmap2;
        for(Enumeration enumeration = COM.dragonflow.Page.schedulePrefsPage.getValues(hashmap, "_additionalSchedule"); enumeration.hasMoreElements(); outputStream.print("<br>" + hashmap2))
        {
            HashMap hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            outputStream.print("<P>schedule id: " + COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_id"));
            outputStream.print("<br>schedule name: " + COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_name"));
            outputStream.print("<br>schedule: " + COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_schedule"));
            hashmap2 = COM.dragonflow.Properties.ScheduleProperty.scheduleStringToHashMap(COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_schedule"));
            outputStream.print("<br>Hashmap dump of this schedule showing an array of \"disabled\" times assoicated with specific days");
        }

    }

    void printForm()
    {
        java.lang.Object obj = getSettings();
        byte byte0 = 0;
        String s = request.getValue("schedule_data");
        if(s.length() > 0)
        {
            Enumeration enumeration = COM.dragonflow.Page.schedulePrefsPage.getValues(((HashMap) (obj)), "_additionalSchedule");
            obj = null;
            if(!s.startsWith("new"))
            {
                String s2 = request.getValue("schedule_data");
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    HashMap hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                    if(!COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap, "_id").equals(s2))
                    {
                        continue;
                    }
                    obj = hashmap;
                    break;
                } while(true);
                if(COM.dragonflow.Page.schedulePrefsPage.getValue(((HashMap) (obj)), "_schedule").startsWith(COM.dragonflow.Properties.ScheduleProperty.absolute_prefix))
                {
                    byte0 = 2;
                } else
                {
                    byte0 = 1;
                }
            } else
            {
                if(s.equals("new_range"))
                {
                    byte0 = 1;
                }
                if(s.equals("new_absolute"))
                {
                    byte0 = 2;
                }
            }
            if(obj == null)
            {
                obj = new HashMapOrdered(true);
            }
        }
        String s1 = "Schedule Preferences";
        printBodyHeader(s1);
        String s3 = "SchedulePref.htm";
        printButtonBar(s3, "", getSecondNavItems(request));
        printPrefsBar("Schedule");
        outputStream.print("<P><CENTER><H2>" + s1 + "</H2></CENTER><P>");
        outputStream.print(getPagePOST("schedulePrefs", "save"));
        if(byte0 != 0)
        {
            outputStream.print("<P>Schedule name: <INPUT VALUE=\"");
            outputStream.print(COM.dragonflow.Page.schedulePrefsPage.getValue(((HashMap) (obj)), "_name"));
            outputStream.print("\" NAME=name> &nbsp;&nbsp;&nbsp;Enter a display name for this schedule.<BR><BR>");
            outputStream.print("<INPUT TYPE=HIDDEN NAME=schedule_data VALUE=\"" + s + "\">");
            if(byte0 == 1)
            {
                COM.dragonflow.Properties.ScheduleProperty.printScheduleTable(outputStream, COM.dragonflow.Page.schedulePrefsPage.getValue(((HashMap) (obj)), "_schedule"), "");
                outputStream.print("<P>Schedule time range for monitors to be enabled or disabled - for example, to enable on Sunday from 10:00 to 22:00<BR>To enter 2 ranges in the same day, separate values by a comma. For example, a schedule to enable monitors from  midnight to 1 a.m. and then again from 5 a.m. to 10 p.m. would be entered as <tt>00:00,05:00</tt> in the &quot;from&quot; textbox, and <tt>01:00,22:00</tt> in the &quot;to&quot; textbox. ");
                if(COM.dragonflow.SiteView.Platform.isSiteSeerAccount(request.getAccount()))
                {
                    outputStream.print("<BR><B>Note:</B> Times entered must be Mountain Time Zone in the U.S.</p>");
                }
            }
            if(byte0 == 2)
            {
                COM.dragonflow.Properties.ScheduleProperty.printAbsoluteScheduleTable(outputStream, COM.dragonflow.Page.schedulePrefsPage.getValue(((HashMap) (obj)), "_schedule"), "");
                outputStream.print("<P>Absolute schedule of events for monitors. Separate times ranges in the same day must be separated by a &quot;,&quot; (comma).  For example, entering <tt>2:30,4,15:44</tt> will trigger events at 2:30am, 4:00am, and 3:44pm for a given day.");
                if(COM.dragonflow.SiteView.Platform.isSiteSeerAccount(request.getAccount()))
                {
                    outputStream.print("<BR><B>Note:</B> Times entered must be Mountain Time Zone in the U.S.</p>");
                }
            }
            outputStream.print("<p>&nbsp;</p><TABLE border=\"0\" width=\"90%\"><tr><td>\n<INPUT TYPE=SUBMIT VALUE=\"Save Changes\"></TD><td width=\"25%\">&nbsp;</td>\n<td align=right><A HREF=" + getPageLink("schedulePrefs", "") + ">Back to Schedule Preferences</A></TD>" + "</TR></TABLE></FORM>\n");
        }
        if(byte0 == 0)
        {
            Enumeration enumeration1 = COM.dragonflow.Page.schedulePrefsPage.getValues(((HashMap) (obj)), "_additionalSchedule");
            outputStream.print("<P><HR><A NAME=additionalSchedule>Schedules allow you to create named schedules, which you can then specify to trigger monitors.</A><P><CENTER><TABLE WIDTH=\"100%\" BORDER=2 cellspacing=0><CAPTION align=left><font SIZE=4><b>Schedules</b></font></CAPTION>\n<TR CLASS=\"tabhead\"><TH WIDTH=\"60%\">Name</TH><TH WIDTH=\"10%\">Type</TH><TH WIDTH=\"2%\">Edit</TH><TH WIDTH=\"1%\">Del</TH></TR>");
            if(!enumeration1.hasMoreElements())
            {
                outputStream.print("<TR><TD ALIGN=CENTER>no additional schedules</TD></TR>");
            } else
            {
                HashMap hashmap1;
                for(; enumeration1.hasMoreElements(); outputStream.print("<TD ALIGN=CENTER><A HREF=" + getPageLink("schedulePrefs", "Delete") + "&schedule_data=" + COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_id") + ">X</A></TD></TR>"))
                {
                    hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration1.nextElement());
                    String s4 = COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_name");
                    outputStream.print("<TR><TD><B>" + s4 + "</B></TD><TD ALIGN=CENTER>");
                    String s5 = COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_schedule");
                    if(s5.startsWith(COM.dragonflow.Properties.ScheduleProperty.absolute_prefix))
                    {
                        outputStream.print("absolute");
                    } else
                    {
                        outputStream.print("range");
                    }
                    outputStream.print("</TD><TD ALIGN=CENTER><A HREF=" + getPageLink("schedulePrefs", "") + "&schedule_data=" + COM.dragonflow.Page.schedulePrefsPage.getValue(hashmap1, "_id") + ">Edit</A></TD>");
                }

            }
            outputStream.print("</TABLE></CENTER>");
            outputStream.print("<P><A HREF=" + getPageLink("schedulePrefs", "") + "&schedule_data=" + "new_range" + "><STRONG>Add additional range schedule</STRONG></A>");
            outputStream.print("<UL>");
            outputStream.print("Range schedules can be used to disable or enable monitors in a range of times for each day of the week.  The monitors fire at the monitor's specified frequency when enabled by a range schedule.");
            outputStream.print("</UL>");
            outputStream.print("<P><A HREF=" + getPageLink("schedulePrefs", "") + "&schedule_data=" + "new_absolute" + "><STRONG>Add additional absolute schedule</STRONG></A>");
            outputStream.print("<UL>");
            outputStream.print("Absolute schedules can be used to fire monitors at specific times in the day for each day of the week.");
            outputStream.print("</UL>");
        }
        outputStream.print("</FORM>");
        printFooter(outputStream);
    }

    COM.dragonflow.StandardAction.Page getPageObject(HashMap hashmap)
    {
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSettingsGroup();
        COM.dragonflow.StandardAction.Page page = new Page();
        if(hashmap != null)
        {
            Enumeration enumeration = (Enumeration) hashmap.keys();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s = (String)enumeration.nextElement();
                if(s.startsWith("_schedule"))
                {
                    page.setProperty(s, (String)hashmap.get(s));
                }
            } while(true);
        }
        siteviewobject.addElement(page);
        return page;
    }

    String getScheduleURL()
    {
        return getPageLink("schedulePrefs", "List");
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_preference"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("operation");
        if(s.equals("Delete"))
        {
            printDeleteForm(s);
        } else
        if(s.equals("Debug"))
        {
            printDebugForm();
        } else
        if(request.isPost())
        {
            if(s.equals("save") || s.equals("setup"))
            {
                savePreferences();
            } else
            {
                printError("The link was incorrect", "unknown operation", getScheduleURL());
            }
        } else
        {
            printForm();
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.schedulePrefsPage scheduleprefspage = new schedulePrefsPage();
        if(args.length > 0)
        {
            scheduleprefspage.args = args;
        }
        scheduleprefspage.handleRequest();
    }
}
