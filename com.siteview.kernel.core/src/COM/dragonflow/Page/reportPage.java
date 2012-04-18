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
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.SiteView.CompareSlot;
import COM.dragonflow.SiteView.PortalFilter;

// Referenced classes of package COM.dragonflow.Page:
// CGI, adhocReportPage, treeControl, portalChooserPage

public class reportPage extends COM.dragonflow.Page.CGI
{

    static final int MINUTE_SECONDS = 60;
    static final int HOUR_SECONDS = 3600;
    static final int DAY_SECONDS = 0x15180;
    static final int WEEK_SECONDS = 0x93a80;
    static final int MONTH_SECONDS = 0x278d00;
    static final int nScheduledTimePeriods[] = {
        3600, 7200, 10800, 14400, 21600, 28800, 43200, 57600, 0x15180, 0x2a300, 
        0x3f480, 0x54600, 0x69780, 0x93a80, 0x278d00
    };
    static final int nAdhocTimePeriods[] = {
        900, 1800, 3600, 7200, 10800, 14400, 18000, 21600, 25200, 28800, 
        32400, 36000, 39600, 43200, 57600, 0x15180, 0x2a300, 0x3f480, 0x54600, 0x69780, 
        0x7e900, 0x93a80, 0x127500, 0x1baf80, 0x278d00
    };
    static final String TOPAZ_CONFIG_REPORT = "siteview/conf/sample_dispatcher?action=log";
    static final String strValueMonthToDate = "monthToDate";
    static final String strLabelMonthToDate = "month-to-date";
    static final String timeScaleSettings[] = {
        "default", "60", "120", "300", "600", "900", "1800", "3600", "7200", "21600", 
        "43200", "86400"
    };
    String vertScaleSettings[] = {
        "", "1", "5", "10", "20", "50", "100", "500", "1000", "5000", 
        "10000", "20000", "50000", "100000", "1000000", "10000000"
    };
    String email;
    String emailData;
    String xmlEmailData;
    String reportTitle;
    String mailTemplate;
    String queryID;
    String portalQuery;
    String title;
    String description;
    String disabledChecked;
    String detailedChecked;
    String attachReportChecked;
    String tabfileChecked;
    String xmlfileChecked;
    String bestCaseCalcChecked;
    String statusFilter;
    String schedFilter;
    String operation;
    String page;
    String operationString;
    String helpFile;
    String errors;
    String hiddenQueryID;
    String hoursOptions;
    String minutesOptions;
    String windowsOptions;
    String precisionOptions;
    String formatOptions;
    String vmaxOptions;
    String startHourOptions;
    String startTimeHTML;
    String endTimeHTML;
    String monitorOptionsHTML;
    String reportTypeHTML;
    int scheduleHour;
    int scheduleMinute;
    boolean readOnly;

    public reportPage()
    {
        email = "";
        emailData = "";
        xmlEmailData = "";
        reportTitle = "";
        mailTemplate = "";
        queryID = "";
        portalQuery = "";
        title = "History";
        description = "";
        disabledChecked = "";
        detailedChecked = "";
        attachReportChecked = "";
        tabfileChecked = "";
        xmlfileChecked = "";
        bestCaseCalcChecked = "";
        statusFilter = "";
        schedFilter = "";
        operation = "adhoc";
        page = "report";
        operationString = "Show Quick";
        helpFile = "AddHist.htm";
        errors = "";
        hiddenQueryID = "";
        hoursOptions = "";
        minutesOptions = "";
        windowsOptions = "";
        precisionOptions = "";
        formatOptions = "";
        vmaxOptions = "";
        startHourOptions = "";
        startTimeHTML = "";
        endTimeHTML = "";
        monitorOptionsHTML = "";
        reportTypeHTML = "";
        scheduleHour = 1;
        scheduleMinute = 0;
        readOnly = false;
    }

    public COM.dragonflow.Page.CGI.menus getNavItems(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        COM.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if(httprequest.actionAllowed("_progress"))
        {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        if(httprequest.actionAllowed("_reportEdit"))
        {
            menus1.add(new CGI.menuItems("Add Report", "report", "add", "operation", "Add a new automated report"));
        }
        if(httprequest.actionAllowed("_reportAdhoc"))
        {
            menus1.add(new CGI.menuItems("Quick Report", "report", "adhoc", "operation", "Create an adhoc report"));
        }
        return menus1;
    }

    public static void printReportPage(java.io.PrintWriter printwriter, String s, COM.dragonflow.HTTP.HTTPRequest httprequest)
        throws java.lang.Exception
    {
        int i = s.indexOf("/accounts");
        int j = s.indexOf("/htdocs");
        if(i != -1 && j != -1)
        {
            s = s.substring(0, i) + s.substring(j);
        }
        String s1 = COM.dragonflow.Utils.FileUtils.readEncFile(s, COM.dragonflow.Utils.I18N.nullEncoding()).toString();
        if(!httprequest.getAccount().equals("administrator") && !httprequest.getAccount().equals("user") && s.endsWith(".html"))
        {
            if(!httprequest.actionAllowed("_reportGenerate"))
            {
                int k = s1.indexOf("<FORM");
                if(k != -1)
                {
                    int l = s1.indexOf("<CENTER>", k);
                    if(l != -1)
                    {
                        s1 = s1.substring(0, k) + s1.substring(l);
                    }
                }
            }
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "/SiteView/htdocs/Reports-", "/SiteView/accounts/" + httprequest.getAccount() + "/htdocs/Reports-");
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "/SiteView/accounts/administrator/htdocs", "/SiteView/accounts/" + httprequest.getAccount() + "/htdocs");
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "/SiteView/htdocs/SiteView.html", "/SiteView/accounts/" + httprequest.getAccount() + "/htdocs/SiteView.html");
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "name=account value=administrator", "name=account value=" + httprequest.getAccount());
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "account=administrator", "account=" + httprequest.getAccount());
        }
        printwriter.print(s1);
    }

    public void printBody()
        throws java.lang.Exception
    {
        operation = request.getValue("operation");
        if(operation.length() == 0)
        {
            operation = "List";
        }
        if(operation.equals("List"))
        {
            printList();
            return;
        } else
        {
            printReportBody();
            return;
        }
    }

    private void printReportBody()
        throws java.lang.Exception
    {
label0:
        {
            int i;
            java.lang.Object obj;
label1:
            {
                if(operation.toLowerCase().indexOf("add") >= 0)
                {
                    operationString = "Add";
                } else
                if(operation.toLowerCase().indexOf("edit") >= 0)
                {
                    operationString = "Edit";
                } else
                if(operation.toLowerCase().indexOf("adhoc") >= 0)
                {
                    operationString = "Show Quick";
                }
                queryID = request.getValue("queryID");
                boolean flag = true;
                i = COM.dragonflow.Utils.TextUtils.toInt(request.getUserSetting("_timeOffset"));
                obj = null;
                if(!COM.dragonflow.Page.treeControl.useTree())
                {
                    if(operation.equals("adhoc"))
                    {
                        page = "adhocReport";
                    }
                } else
                if(request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request) && operation.equals("adhoc"))
                {
                    page = "adhocReport";
                    COM.dragonflow.Page.adhocReportPage adhocreportpage = new adhocReportPage();
                    adhocreportpage.request = request;
                    adhocreportpage.outputStream = outputStream;
                    adhocreportpage.printBody();
                    return;
                }
                if(operation.equals("Delete"))
                {
                    flag = false;
                    if(request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request))
                    {
                        deleteHistoryReport(queryID);
                        printRefreshHeader();
                    } else
                    {
                        outputStream.println("<FONT SIZE=+1>Are you sure you want to delete the report?</FONT><p><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=page value=report><input type=hidden name=operation value=\"" + operation + "\">" + "<input type=hidden name=queryID value=\"" + queryID + "\">" + "<input type=hidden name=account value=" + request.getAccount() + ">" + "<TABLE WIDTH=\"100%\" BORDER=0><TR>" + "<TD WIDTH=\"6%\"></TD><TD WIDTH=\"41%\"><input type=submit value=\"" + operation + " Report\"></TD>" + "<TD WIDTH=\"6%\"></TD><TD ALIGN=RIGHT WIDTH=\"41%\"><A HREF=/SiteView/cgi/go.exe/SiteView?page=report&operation=List&account=" + request.getAccount() + ">Return to Detail</A></TD><TD WIDTH=\"6%\"></TD>" + "</TR></TABLE></FORM>");
                        printFooter(outputStream);
                    }
                }
                if(flag && request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request))
                {
                    obj = new HashMapOrdered(true);
                    boolean flag1 = COM.dragonflow.Page.reportPage.setReportOptions(request, ((HashMap) (obj)));
                    Enumeration enumeration = request.getValues("monitors");
                    if(!enumeration.hasMoreElements() && !flag1 && request.getValue("query").length() == 0)
                    {
                        errors += "\tNo monitors were selected for this report";
                    }
                    String s2;
                    for(; enumeration.hasMoreElements(); ((HashMap) (obj)).add("monitors", s2))
                    {
                        s2 = (String)enumeration.nextElement();
                        if(COM.dragonflow.Page.treeControl.useTree())
                        {
                            s2 = COM.dragonflow.HTTP.HTTPRequest.decodeString(s2);
                        }
                    }

                    if(queryID.length() > 0)
                    {
                        ((HashMap) (obj)).put("id", queryID);
                    }
                    ((HashMap) (obj)).put("window", request.getValue("window"));
                    ((HashMap) (obj)).put("precision", request.getValue("precision"));
                    ((HashMap) (obj)).put("format", request.getValue("format"));
                    ((HashMap) (obj)).put("vmax", request.getValue("vmax"));
                    String s3 = request.getValue("startHour");
                    if(!s3.equals("now"))
                    {
                        s3 = String.valueOf(COM.dragonflow.Utils.TextUtils.toLong(s3) - (long)i);
                    }
                    if(COM.dragonflow.SiteView.Platform.isPortal())
                    {
                        ((HashMap) (obj)).put("query", COM.dragonflow.Page.portalChooserPage.getQueryChooseListSelectedItem(request));
                    }
                    ((HashMap) (obj)).put("startHour", s3);
                    ((HashMap) (obj)).put("startDay", request.getValue("startDay"));
                    ((HashMap) (obj)).put("relative", request.getValue("relative"));
                    ((HashMap) (obj)).put("email", COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("email")));
                    ((HashMap) (obj)).put("emailData", COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("emailData")));
                    ((HashMap) (obj)).put("xmlEmailData", COM.dragonflow.Utils.TextUtils.toEmailList(request.getValue("xmlEmailData")));
                    ((HashMap) (obj)).put("mailTemplate", request.getValue("mailTemplate"));
                    ((HashMap) (obj)).put("title", request.getValue("title"));
                    ((HashMap) (obj)).put("description", request.getValue("description"));
                    ((HashMap) (obj)).put("statusFilter", request.getValue("statusFilter"));
                    ((HashMap) (obj)).put("schedFilter", request.getValue("schedFilter"));
                    if(COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "mailTemplate").equals("HistoryMail"))
                    {
                        ((HashMap) (obj)).remove("mailTemplate");
                    }
                    ((HashMap) (obj)).put("noSlotFilter", "true");
                    if(request.getValue("tabfile").length() > 0)
                    {
                        ((HashMap) (obj)).put("tabfile", "yes");
                    }
                    if(request.getValue("xmlfile").length() > 0)
                    {
                        ((HashMap) (obj)).put("xmlfile", "yes");
                    }
                    if(request.getValue("disabled").length() > 0)
                    {
                        ((HashMap) (obj)).put("disabled", "checked");
                    }
                    if(request.getValue("detailed").length() == 0)
                    {
                        ((HashMap) (obj)).put("basic", "checked");
                    }
                    if(request.getValue("attachReport").length() > 0)
                    {
                        ((HashMap) (obj)).put("attachReport", "checked");
                    }
                    if(request.getValue("bestCaseCalc").length() > 0)
                    {
                        ((HashMap) (obj)).put("bestCaseCalc", "checked");
                    }
                    int k = 0x15180;
                    int l = 1;
                    int i1 = 0;
                    try
                    {
                        k = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("window"));
                        l = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("hours"));
                        i1 = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("minutes"));
                    }
                    catch(java.lang.NumberFormatException numberformatexception)
                    {
                        COM.dragonflow.Log.LogManager.log("Error", "A number was passed from the browser reportPage that did not parse as an integer. It was 'window', 'hours', or 'minutes': " + numberformatexception.toString());
                        COM.dragonflow.Log.LogManager.log("Error", "reportPage is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(numberformatexception));
                    }
                    int j1 = (l + i1) - i;
                    String s9;
                    if(k == 0x278d00)
                    {
                        s9 = "monthday\t1\t" + j1;
                    } else
                    if(k == 0x93a80)
                    {
                        s9 = "weekday\tU\t" + j1;
                    } else
                    {
                        s9 = "weekday\tM,T,W,R,F,S,U\t" + j1;
                    }
                    ((HashMap) (obj)).put("schedule", s9);
                    if(COM.dragonflow.SiteView.Platform.isPortal())
                    {
                        ((HashMap) (obj)).put("account", request.getAccount());
                    }
                    if(errors.length() == 0)
                    {
                        flag = false;
                        if(operation.equals("add"))
                        {
                            addHistoryReport(((HashMap) (obj)));
                        } else
                        {
                            changeHistoryReport(((HashMap) (obj)));
                        }
                        printRefreshHeader();
                    }
                }
                if(!flag)
                {
                    break label0;
                }
                title = operationString + " Management Report";
                if(obj != null || queryID.length() <= 0)
                {
                    break label1;
                }
                Array array = COM.dragonflow.Page.reportPage.getReportFrames(request.getAccount());
                Enumeration enumeration1 = (Enumeration) array.iterator();
                HashMap hashmap;
                String s5;
                do
                {
                    if(!enumeration1.hasMoreElements())
                    {
                        break label1;
                    }
                    hashmap = (HashMap)enumeration1.nextElement();
                    s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "id");
                } while(!s5.equals(queryID));
                obj = hashmap;
                hiddenQueryID = "<input type=hidden name=queryID value=" + queryID + ">\n";
            }
            if(obj == null)
            {
                if(!operation.equals("adhoc"))
                {
                    Array array1 = COM.dragonflow.Page.reportPage.getReportFrames(request.getAccount());
                    int j = request.getPermissionAsInteger("_maximumReportsCount");
                    if(j > 0 && array1.size() >= j)
                    {
                        printTooManyReports(request.getAccount(), j);
                        return;
                    }
                }
                obj = new HashMap();
                ((HashMap) (obj)).add("precision", "default");
                ((HashMap) (obj)).add("format", "");
                ((HashMap) (obj)).add("vmax", "");
                ((HashMap) (obj)).add("startDay", "today");
                ((HashMap) (obj)).add("startHour", "now");
                ((HashMap) (obj)).add("basic", "checked");
                if(operation.equals("adhoc"))
                {
                    ((HashMap) (obj)).add("window", String.valueOf(3600));
                } else
                {
                    ((HashMap) (obj)).add("schedule", "weekday\tM,T,W,R,F,S,U\t3600");
                    ((HashMap) (obj)).add("window", String.valueOf(0x15180));
                }
                ((HashMap) (obj)).add("relative", "-1");
            }
            String s = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "tabfile");
            if(s != null && s.length() > 0)
            {
                tabfileChecked = "CHECKED";
            }
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "xmlfile");
            if(s1 != null && s1.length() > 0)
            {
                xmlfileChecked = "CHECKED";
            }
            String s4 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "disabled");
            if(s4.length() > 0)
            {
                disabledChecked = "CHECKED";
            }
            String s6 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "basic");
            if(s6.length() == 0)
            {
                detailedChecked = "CHECKED";
            }
            String s7 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "attachReport");
            if(s7.length() > 0)
            {
                attachReportChecked = "CHECKED";
            }
            String s8 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "bestCaseCalc");
            if(s8.length() > 0)
            {
                bestCaseCalcChecked = "CHECKED";
            }
            email = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "email");
            emailData = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "emailData");
            xmlEmailData = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "xmlEmailData");
            reportTitle = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "title");
            description = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "description");
            statusFilter = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "statusFilter");
            schedFilter = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "schedFilter");
            if(reportTitle.length() > 0)
            {
                title = operationString + " : " + reportTitle;
            }
            mailTemplate = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "mailTemplate");
            String s10 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "schedule");
            scheduleHour = 1;
            scheduleMinute = 0;
            if(s10 != null && s10.length() > 0)
            {
                String as[] = COM.dragonflow.Utils.TextUtils.split(s10, "\t");
                int k1 = COM.dragonflow.Utils.TextUtils.stringToDaySeconds(as[2]);
                k1 += i;
                scheduleHour = k1 / 3600;
                scheduleMinute = (k1 % 3600) / 60;
            }
            computeMonitorOptions(((HashMap) (obj)));
            hoursOptions = getHoursHTML(scheduleHour);
            minutesOptions = getMinutesHTML(scheduleMinute);
            String s11 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "window");
            windowsOptions = getTimePeriodHTML(s11);
            String s12 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "precision");
            precisionOptions = getTimeScaleHTML(s12);
            String s13 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "format");
            formatOptions = getFormatHTML(s13);
            String s14 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "vmax");
            vmaxOptions = getVertScaleHTML(s14);
            String s15 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "startHour");
            startHourOptions = getStartHourHTML(s15, i);
            startTimeHTML = COM.dragonflow.Page.reportPage.getStartTimeHTML(i);
            endTimeHTML = COM.dragonflow.Page.reportPage.getEndTimeHTML(i);
            reportTypeHTML = getReportTypeHTML(((HashMap) (obj)));
            if(COM.dragonflow.SiteView.Platform.isPortal())
            {
                helpFile = "CentraReports.htm";
            }
            if(operation.equals("adhoc"))
            {
                helpFile = "EditQuick.htm";
                if(COM.dragonflow.SiteView.Platform.isPortal())
                {
                    helpFile = "QuickReport.htm";
                }
            }
            if(operation.equals("edit") || operation.equals("add"))
            {
                helpFile = "EditHist.htm";
                if(COM.dragonflow.SiteView.Platform.isPortal())
                {
                    helpFile = "MgntReport.htm";
                }
            }
            if(request.getValue("item").length() > 0)
            {
                portalQuery = null;
            } else
            {
                portalQuery = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "query");
            }
            printHeader();
            printForm();
            printFooter(outputStream);
        }
    }

    void deleteHistoryReport(String s)
    {
        adjustHistoryConfig(null, s);
    }

    void addHistoryReport(HashMap hashmap)
    {
        adjustHistoryConfig(hashmap, null);
    }

    void changeHistoryReport(HashMap hashmap)
    {
        adjustHistoryConfig(hashmap, COM.dragonflow.Utils.TextUtils.getValue(hashmap, "id"));
    }

    void adjustHistoryConfig(HashMap hashmap, String s)
    {
        Array array = null;
        array = getReportFrameList();
        Array array1;
        if(hashmap != null && s == null)
        {
            try
            {
                HashMap hashmap1 = getMasterConfig();
                if(!request.isStandardAccount())
                {
                    hashmap1 = (HashMap)array.get(0);
                } else
                {
                    hashmap1 = getMasterConfig();
                }
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_nextReportID");
                if(s.length() == 0)
                {
                    s = "10";
                }
                hashmap1.put("_nextReportID", COM.dragonflow.Utils.TextUtils.increment(s));
                if(request.isStandardAccount())
                {
                    saveMasterConfig(hashmap1);
                }
                hashmap.put("id", s);
            }
            catch(java.io.IOException ioexception)
            {
                java.lang.System.err.println("Could not read history configuration");
                COM.dragonflow.Log.LogManager.log("Error", "reportPage is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(ioexception));
            }
            array1 = array;
            array1.add(hashmap);
        } else
        {
            array1 = new Array();
            Enumeration enumeration = (Enumeration) array.iterator();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap2 = (HashMap)enumeration.nextElement();
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "id");
                if(s1 != null && s1.equals(s) && COM.dragonflow.SiteView.Monitor.isReportFrame(hashmap2))
                {
                    if(hashmap != null)
                    {
                        array1.add(hashmap);
                    }
                } else
                {
                    array1.add(hashmap2);
                }
            } while(true);
        }
        saveReportFrameList(array1, request.getAccount());
    }

    public static boolean setReportOptions(COM.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap)
    {
        String s = httprequest.getValue("reportType");
        hashmap.put("reportType", s);
        boolean flag = false;
        boolean flag1 = false;
        if(httprequest.getValue("showThresholdSummary").length() > 0)
        {
            hashmap.put("_showReportThresholdSummary", "checked");
        } else
        {
            hashmap.remove("_showReportThresholdSummary");
            flag = true;
        }
        if(httprequest.getValue("showSummaryTable").length() > 0)
        {
            hashmap.remove("_hideReportSummary");
            flag = true;
        } else
        {
            hashmap.put("_hideReportSummary", "checked");
        }
        if(httprequest.getValue("showErrorTimeSummary").length() == 0)
        {
            hashmap.remove("_showReportErrorTimeSummary");
            flag = true;
        } else
        {
            hashmap.put("_showReportErrorTimeSummary", "checked");
        }
        if(httprequest.getValue("showGraphs").length() > 0)
        {
            hashmap.remove("_hideReportGraphs");
            hashmap.remove("_hideReportCharts");
            flag = true;
        } else
        {
            hashmap.put("_hideReportGraphs", "checked");
            hashmap.put("_hideReportCharts", "checked");
        }
        if(httprequest.getValue("showTables").length() > 0)
        {
            hashmap.remove("_hideReportTables");
            flag = true;
        } else
        {
            hashmap.put("_hideReportTables", "checked");
        }
        if(httprequest.getValue("showErrors").length() > 0)
        {
            hashmap.remove("_hideReportErrors");
            flag = true;
        } else
        {
            hashmap.put("_hideReportErrors", "checked");
        }
        if(httprequest.getValue("showWarnings").length() > 0)
        {
            hashmap.remove("_hideReportWarnings");
            flag = true;
        } else
        {
            hashmap.put("_hideReportWarnings", "checked");
        }
        if(httprequest.getValue("warningNotIncluded").length() > 0)
        {
            hashmap.put("_warningNotIncluded", "checked");
        }
        if(httprequest.getValue("upTimeIncludeWarning").length() > 0)
        {
            hashmap.put("_upTimeIncludeWarning", "checked");
        }
        if(httprequest.getValue("failureNotIncluded").length() > 0)
        {
            hashmap.put("_failureNotIncluded", "checked");
        }
        if(httprequest.getValue("showGoods").length() > 0)
        {
            hashmap.remove("_hideReportGoods");
            flag = true;
        } else
        {
            hashmap.put("_hideReportGoods", "checked");
        }
        hashmap.remove("showAlertsOnly");
        if(httprequest.getValue("showAlerts").length() > 0)
        {
            if(!flag)
            {
                hashmap.put("showAlertsOnly", "true");
                flag1 = true;
            }
            hashmap.put("_showReportAlerts", httprequest.getValue("alertDetailLevel"));
        } else
        {
            hashmap.remove("_showReportAlerts");
        }
        return flag1;
    }

    void printForm()
    {
        String s = !operation.equals("adhoc") || COM.dragonflow.Page.treeControl.useTree() ? "" : " target=reports";
        outputStream.print("<FORM action=/SiteView/cgi/go.exe/SiteView METHOD=POST" + s + ">\n" + hiddenQueryID + "<input type=hidden name=page value=" + page + ">\n" + "<input type=hidden name=operation value=" + operation + ">\n" + "<input type=hidden name=account value=" + request.getAccount() + ">" + "<table border=0 cellspacing=4><tr><td><img src=\"/SiteView/htdocs/artwork/LabelSpacer.gif\"></td><td></td><td></td></tr>\n");
        outputStream.print("<tr><td colspan=3><hr></td></tr>");
        if(COM.dragonflow.SiteView.Platform.isPortal())
        {
            COM.dragonflow.Page.portalChooserPage.printQueryChooseList(outputStream, request, "<B>Report Subject(s)</B>", "Create a report from a request", portalQuery);
        } else
        if(COM.dragonflow.Page.treeControl.useTree())
        {
            outputStream.print(monitorOptionsHTML);
        } else
        {
            outputStream.print("<TABLE>\n<TR>\n<TD VALIGN=TOP>Show history for the selected items</TD>\n</TR>\n<TR>\n<TD><IMG SRC=/SiteView/htdocs/artwork/empty.gif WIDTH=25><select multiple name=monitors size=6> " + monitorOptionsHTML + "</select></TD>\n" + "</TR>\n" + "</TABLE>\n" + "<DD>To select several items, hold down the Control key (on most platforms) while clicking item names.\n" + "</DL></BLOCKQUOTE>\n");
        }
        outputStream.print("<TR><TD COLSPAN=3><hr></TD></TR><TR><TD ALIGN=RIGHT VALIGN=TOP>Time Period</TD><td></td></tr>\n<TR><td></td><TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR>\n");
        if(operation.equals("adhoc"))
        {
            outputStream.println("<TD>From " + startTimeHTML + " to " + endTimeHTML);
        } else
        {
            outputStream.println("<TD>Show data for last <select name=window size=1>\n" + windowsOptions + "</select>\n" + "<input type=hidden name=relative value=\"-1\">\n" + "</TD>\n");
        }
        outputStream.println("</TR>\n<TR><TD>\nPeriod of time to be included in the report.\n</TD></TR></TABLE></TD></TR>\n");
        outputStream.print(reportTypeHTML);
        if(!operation.equals("adhoc"))
        {
            printReportForm(mailTemplate);
        } else
        {
            printReportAdhocForm();
        }
        outputStream.print("</TABLE><p><input type=submit value=\"" + operationString + "\"> Management Report</p>\n");
        printWarningHTML();
        outputStream.print("<HR>\n<H3>Advanced Options</H3><table border=0 cellspacing=4><tr><td><img src=\"/SiteView/htdocs/artwork/LabelSpacer.gif\"></td><td></td><td></td></tr>\n");
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Show Detail</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n\t<input type=checkbox name=detailed " + detailedChecked + ">Show detailed monitor information\n" + "<BR>If this box is checked, then all of the information gathered for each monitor is displayed\n" + "on the report.  Otherwise, only the primary data is displayed for each monitor.<P>\n" + "</TD></TR>\n");
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Show Monitors</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n\t<select name=statusFilter>" + statusFilterHTML(statusFilter) + "</select>\n" + "<BR>Show only monitors that have been in the chosen status during the time period of" + " the report.<P>\n" + "</TD></TR>\n");
        if(!COM.dragonflow.SiteView.Platform.isPortal())
        {
            outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Schedule Filter</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n\t<select name=schedFilter>" + scheduleFilterHTML(schedFilter) + "</select>\n" + "<BR>Use only the data from samples that occur during this schedule period.\n" + "<BR><A HREF=\"/SiteView/cgi/go.exe/SiteView?page=schedulePrefs&operation=List&account=" + request.getAccount() + "\">Edit</A> Schedules" + "</TD></TR>\n");
        }
        if(!operation.equals("adhoc"))
        {
            outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Disable Report</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n\t<input type=checkbox name=disabled " + disabledChecked + ">&nbsp;Disable this report temporarily\n" + "<BR>If this box is checked, then the report will not be automatically generated.<P>\n" + "</TD></TR>\n");
            outputStream.print("<tr><td colspan=3><hr></td></tr><TR><TD ALIGN=RIGHT VALIGN=TOP>Comma-Delimited Output</TD><td></td></tr>\n<TR><td></td><TD ALIGN=LEFT VALIGN=TOP>\n\t<input type=checkbox name=tabfile " + tabfileChecked + ">Generate report as comma-delimited file\n" + "<BR>If this box is checked, then whenever the report is generated, a comma-delimited text file\n" + "(suitable for importing into a spreadsheet application such as Excel) will also be generated.\n" + "<p>\n" + "\tE-mail:<input size=60 name=emailData value=" + emailData + ">\n" + "<BR>If the box is checked and an e-mail address is entered, then a copy of the comma-delimited text file\n" + "  will be sent as an e-mail message.  Separate multiple addresses using commas.<p>\n" + "</TD></TR>\n");
            outputStream.print("<tr><td colspan=3><hr></td></tr><TR><TD ALIGN=RIGHT VALIGN=TOP>XML Output</TD><td></td></tr>\n<TR><td></td><TD ALIGN=LEFT VALIGN=TOP>\n\t<input type=checkbox name=xmlfile " + xmlfileChecked + ">Generate report as XML file\n" + "<BR>If this box is checked, then whenever the report is generated, a XML text file\n" + "will also be generated.\n" + "<p>\n" + "\tE-mail:<input size=60 name=xmlEmailData value=" + xmlEmailData + ">\n" + "<BR>If the box is checked and an e-mail address is entered, then a copy of the XML text file\n" + "  will be sent as an e-mail message.  Separate multiple addresses using commas.<p>\n" + "</TD></TR>\n" + "<tr><td colspan=3><hr></td></tr>");
        }
        String s1 = "<input type=hidden name=startDay value=\"today\">";
        String s2 = "";
        if(operation.equals("adhoc"))
        {
            s1 = "date: <input type=text name=startDay size=10 value=\"today\">";
            s2 = " Specify the date using a format of mm/dd/yy.  For example, 7/31/97.";
        }
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Uptime Calculation</TD><TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR><TD>\n<input type=\"checkbox\" name=\"bestCaseCalc\"" + bestCaseCalcChecked + ">Best Case Calculation\n" + "</TD></TR>\n" + "<TR><TD>\n" + "Calculate the monitor uptime %, warning % and error % using a best case scenario.  " + "In this scenario, monitor time in error is calculated from the first known Error run instead of the last known Good run.\n" + "<p></TD></TR>" + "</TABLE>" + "" + "</TD>" + "</TR>\n");
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Time Scale</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR>\n<TD>\nShow samples for every <select name=precision size=1>\n" + precisionOptions + "</select>\n" + "</TD>\n" + "</TR>\n" + "<TR><TD>\n" + "The time between samples.  Automatic will scale the time between samples to an appropriate value for the\n" + "time period chosen.<p></TD></TR></TABLE></TD></TR>\n");
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Vertical Scale</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR>\n<TD>\nMaximum graph value <select name=vmax size=1>\n" + vmaxOptions + "</select>\n" + "</TD>\n" + "</TR>\n" + "<TR><TD>\n" + "The maximum value displayed on the graph.  Automatic will use the maximum sample value.  Choosing this option makes it easier to compare graphs.<p>\n" + "</TD></TR></TABLE></TD></TR>\n");
        if(!operation.equals("adhoc"))
        {
            outputStream.println("<TR><TD ALIGN=RIGHT VALIGN=TOP>End Time</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR>\n<TD>\nReporting period ends <select name=startHour size=1>\n" + startHourOptions + "</select>\n" + s1 + "</TD>\n" + "</TR>\n" + "<TR><TD>\n" + "The end time of the report period. The report samples will end at this time.  \n" + s2 + "<P></TD></TR></TABLE></TD></TR>\n");
            outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Report Schedule</TD>\n<TD ALIGN=LEFT VALIGN=TOP>\n<TABLE BORDER=0>\n<TR>\n<TD ALIGN=LEFT> Generate this report at: &nbsp;\n<select name=hours size=1>\n" + hoursOptions + "</select>\n" + "<FONT SIZE=+2><B>:</B></FONT>\n" + "<select name=minutes size=1>\n" + minutesOptions + "</select>\n" + "</TD>\n" + "</TR>\n" + "\n" + "<TR><TD>Select the time of day that the report should be generated. The report covers the time period specified under &quot;Time Period&quot; above, ending at the time\n" + "that the report is run.  If you change the End Time above, then you can change this so that the report runs after the End Time.<p></TD></TR> \n" + "</TABLE></TD></TR>\n");
        } else
        {
            printReportTitle();
        }
        outputStream.print("</table><P><input type=submit value=\"" + operationString + "\"> Management Report\n");
        outputStream.println("</FORM>\n");
    }

    String getReportTypeHTML(HashMap hashmap)
    {
        String s = COM.dragonflow.Page.reportPage.getValue(hashmap, "reportType");
        boolean flag = COM.dragonflow.Page.reportPage.getValue(hashmap, "_showReportThresholdSummary").length() == 0;
        boolean flag1 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportSummary").length() > 0;
        boolean flag2 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_showReportErrorTimeSummary").length() > 0;
        boolean flag3 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportCharts").length() > 0;
        boolean flag4 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportGraphs").length() > 0;
        boolean flag5 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportTables").length() > 0;
        boolean flag6 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportErrors").length() > 0;
        boolean flag7 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportWarnings").length() > 0;
        boolean flag8 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_hideReportGoods").length() > 0;
        boolean flag9 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_showReportAlerts").length() == 0;
        boolean flag10 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_warningNotIncluded").length() > 0;
        boolean flag11 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_upTimeIncludeWarning").length() > 0;
        boolean flag12 = COM.dragonflow.Page.reportPage.getValue(hashmap, "_failureNotIncluded").length() > 0;
        String s1 = "basic";
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_showReportAlerts").length() > 0)
        {
            flag9 = false;
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_showReportAlerts");
        }
        if(flag3)
        {
            flag4 = true;
        }
        StringBuffer stringbuffer = new StringBuffer();
        java.util.Vector vector = new Vector();
        vector.addElement("barGraph");
        vector.addElement("bar graph - one graph per measurement");
        vector.addElement("lineGraph");
        vector.addElement("line graph - one graph per measurement");
        vector.addElement("lineGraph,similarProperties");
        vector.addElement("line graph - one graph per monitor");
        vector.addElement("lineGraph,multipleMonitors");
        vector.addElement("line graph - one graph per type of measurement");
        vector.addElement("lineGraph,similarProperties,multipleMonitors");
        vector.addElement("line graph - one graph for all measurements");
        if(operation.equals("adhoc"))
        {
            vector.addElement("textFile");
            vector.addElement("comma-delimited text");
            vector.addElement("xmlFile");
            vector.addElement("XML text");
        }
        if(s.length() == 0)
        {
            s = (String)vector.elementAt(0);
        }
        stringbuffer.append("<TR><TD colspan=3><hr></td></tr>\n");
        stringbuffer.append("<TR><TD ALIGN=RIGHT VALIGN=TOP><B>Report Sections</B></TD><td></td><td></td></tr>\n<tr><td></td><TD ALIGN=LEFT>Select the sections to include in the report</td></tr><tr><td></td><TD ALIGN=LEFT VALIGN=TOP><DL><DT><input type=checkbox name=showThresholdSummary " + (flag ? "" : "CHECKED") + ">" + "Summary of Monitor Thresholds<BR>" + "<input type=checkbox name=showSummaryTable " + (flag1 ? "" : "CHECKED") + ">Summary of Uptime and Readings<BR>" + "<DD><input type=checkbox name=upTimeIncludeWarning " + (flag11 ? "CHECKED" : "") + ">Count warning time the same as good time in UpTime<BR>" + "<DT><input type=checkbox name=showErrorTimeSummary " + (flag2 ? "CHECKED" : "") + ">Time in Error Summary<BR>" + "<DT><input type=checkbox name=showGraphs " + (flag4 ? "" : "CHECKED") + ">" + "<select name=reportType>\n");
        stringbuffer.append(COM.dragonflow.Page.reportPage.getOptionsHTML(vector, s));
        String s2 = "";
        if(operation.equals("adhoc"))
        {
            s2 = "Comma-delimited files are suitable for importing into spreadsheets.\n";
        }
        stringbuffer.append("</select>\n<DD>Bar graphs show one monitor per graph, line graphs can show multiple monitors or statistics per graph.\n" + s2);
        stringbuffer.append("<DT><input type=checkbox name=showTables " + (flag5 ? "" : "CHECKED") + ">Table of Monitor Readings<BR>" + "<DT><input type=checkbox name=showErrors " + (flag6 ? "" : "CHECKED") + ">Listing of Errors<BR>" + "<DT><input type=checkbox name=showWarnings " + (flag7 ? "" : "CHECKED") + ">Listing of Warnings<BR>" + "<DT><input type=checkbox name=showGoods " + (flag8 ? "" : "CHECKED") + ">Listing of Goods<BR>" + "<DT><input type=checkbox name=warningNotIncluded " + (flag10 ? "CHECKED" : "") + ">Don't display warning % column in Summary of Uptime and Readings<BR>" + "<DT><input type=checkbox name=failureNotIncluded " + (flag12 ? "CHECKED" : "") + ">Don't display failure column in Summary of Uptime and Readings<BR>");
        if(!COM.dragonflow.SiteView.Platform.isPortal())
        {
            stringbuffer.append("<DT><input type=checkbox name=showAlerts " + (flag9 ? "" : "CHECKED") + ">Listing of Alerts Sent - Detail Level: ");
            java.util.Vector vector1 = new Vector();
            vector1.addElement("basic");
            vector1.addElement("Basic");
            vector1.addElement("detailonfail");
            vector1.addElement("Show Detail for Failed Alerts");
            vector1.addElement("detail");
            vector1.addElement("Show Detail for All Alerts");
            stringbuffer.append("<select name=alertDetailLevel size=1>" + COM.dragonflow.Page.reportPage.getOptionsHTML(vector1, s1) + "</select><BR>");
        }
        stringbuffer.append("<DT>Formatting <select name=format size=1>\n" + formatOptions + "</select><br>\n");
        stringbuffer.append("</DL></TD></TR>\n");
        stringbuffer.append("<TR><TD colspan=3><hr></td></tr>\n");
        return stringbuffer.toString();
    }

    void printHeader()
    {
        printBodyHeader(title);
        printMenuItems(helpFile, "Reports");
        outputStream.println("<p><H2>" + title + "</H2>\n");
        if(errors.length() > 0)
        {
            String as[] = COM.dragonflow.Utils.TextUtils.split(errors, "\t");
            outputStream.print("<UL>\n");
            for(int i = 0; i < as.length; i++)
            {
                if(as[i].length() > 0)
                {
                    outputStream.print("<LI><B>" + as[i] + "</B>\n");
                }
            }

            outputStream.print("</UL><HR>\n");
        }
    }

    void printRefreshHeader()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=report&account=" + request.getAccount();
        printRefreshHeader("", s, 0);
    }

    void printWarningHTML()
    {
        if(operation.equals("adhoc"))
        {
            outputStream.print("&#32;<BR>(<B>Note: </B>This may take a few moments, depending on the speed of the web server machine, the number\nof monitors and the time period of the report)\n");
        }
    }

    void printTooManyReports(String s, int i)
    {
        printHeader();
        String s1 = "You have reached your limit of " + i + " reports for this account.";
        outputStream.print("<P><HR>" + s1 + "<HR><P><A HREF=/SiteView/cgi/go.exe/SiteView?page=report&operation=List&account=" + request.getAccount() + ">Return to Reports</A>\n");
        printFooter(outputStream);
    }

    void printReportAdhocForm()
    {
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP><B>Send Report by E-mail</B></TD>\n<TD ALIGN=LEFT VALIGN=TOP>\nE-mail:<input size=60 name=email value=" + email + ">\n" + "<BR>If an e-mail address is entered, when the quick management report is generated, an html format of the \n" + " report will be sent as an e-mail message.  Separate multiple addresses using commas.<p>\n" + "</TD></TR>\n");
    }

    void printReportForm(String s)
    {
        java.util.Vector vector = COM.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.history", request);
        String s1 = "";
        if(s.length() == 0)
        {
            s = "HistoryMail";
        }
        for(int i = 0; i < vector.size(); i++)
        {
            String s2 = (String)vector.elementAt(i);
            boolean flag = s2.equals(s);
            s1 = s1 + COM.dragonflow.Page.reportPage.getListOptionHTML(s2, s2, flag);
        }

        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP><B>Send Report by E-mail</B></TD><td></td></tr>\n<TR><td></td><TD ALIGN=LEFT VALIGN=TOP><DL>\n<DT>E-mail:<input size=60 name=email value=" + email + ">\n" + "<DD>If an e-mail address is entered, then whenever the report is generated, a summary of the report\n" + "  will be sent as an e-mail message.  Separate multiple addresses using commas.\n" + "<P>" + "<DT><input type=checkbox name=attachReport " + attachReportChecked + ">Send using HTML format\n" + "<DD>If this box is checked, then the report is sent as an HTML document. \n" + "Otherwise, a text summary of the report is sent.\n" + "<P>" + "<DT>Template:<select size=1 name=mailTemplate>" + s1 + "</select>\n" + "<DD>The template is used to format, add information, and include links to report pages in SiteView. " + "Use 'HistoryMail' to include a link to the SiteView Reports page as an Administrator and " + "'HistoryUserMail' to include a link to the Site SiteView Reports page as a User. You will " + "need to " + "<a href=\"" + "/SiteView/cgi/go.exe/SiteView?page=userPrefs&account=" + request.getAccount() + "&user=user&operation=Edit\">enable</a>" + " the User login to use 'HistoryUserMail'.\n" + "</DL></TD></TR>\n");
        printReportTitle();
    }

    void printReportTitle()
    {
        outputStream.print("<TR><TD ALIGN=RIGHT VALIGN=TOP>Report Text</TD><td></td></tr>\n<TR><td></td><TD ALIGN=LEFT VALIGN=TOP><DL>\n<DT>\n\tTitle:&nbsp;&nbsp;&nbsp;&nbsp;<input size=60 name=title value=\"" + reportTitle + "\">\n" + "<DD>(optional) Title for the report - shown at the top of the report and in the list of reports. If this\n" + "is blank, then a name describing the monitors and groups in the report will be used.\n" + "<DT>\n" + "\tDescription:<input size=60 name=description value=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML(description) + "\">\n" + "<DD>(optional) Description for the report - shown at the top of the report\n" + "</DL></TD></TR>\n");
    }

    void computeMonitorOptions(HashMap hashmap)
        throws java.lang.Exception
    {
        Array array = new Array();
        for(Enumeration enumeration = (Enumeration) hashmap.values("groups"); enumeration.hasMoreElements(); array.add(enumeration.nextElement())) { }
        for(Enumeration enumeration1 = (Enumeration) hashmap.values("monitors"); enumeration1.hasMoreElements(); array.add(enumeration1.nextElement())) { }
        if(COM.dragonflow.Page.treeControl.useTree())
        {
            StringBuffer stringbuffer = new StringBuffer();
            String s = "Show history for the selected items. Use the <img src=/SiteView/htdocs/artwork/Plus.gif border=0> to expand a group or the <img src=/SiteView/htdocs/artwork/Minus.gif border=0> to close a group.\n";
            byte byte0 = 35;
            COM.dragonflow.Page.treeControl treecontrol = new treeControl(request, "monitors", false);
            treecontrol.process("Report Subject(s)", "", s, array, null, null, byte0, this, stringbuffer);
            monitorOptionsHTML = stringbuffer.toString();
        } else
        {
            monitorOptionsHTML = getMonitorOptionsHTML(array, null, null, 35);
        }
    }

    String getTimePeriodHTML(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        int ai[] = nAdhocTimePeriods;
        if(!operation.equals("adhoc"))
        {
            ai = nScheduledTimePeriods;
        }
        String s1 = "";
        String s2 = "";
        boolean flag = false;
        for(int i = 0; i < ai.length; i++)
        {
            s1 = String.valueOf(ai[i]);
            s2 = secondsToString(ai[i]);
            flag = s.equals(s1);
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s2, flag));
        }

        s1 = "monthToDate";
        s2 = "month-to-date";
        flag = s.equals(s1);
        stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s2, flag));
        return stringbuffer.toString();
    }

    String getFormatHTML(String s)
    {
        boolean flag = s.length() == 0;
        String s1 = "\"\"";
        String s3 = "color background (default)";
        boolean flag1 = flag;
        String s5 = COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s3, flag1);
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        Enumeration enumeration = siteviewgroup.getMultipleSettings("_reportFormat");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
            String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_title").replace('_', ' ');
            boolean flag2 = !flag && s2.equals(s);
            s5 = s5 + COM.dragonflow.Page.reportPage.getListOptionHTML(s2, s4, flag2);
            if(flag2)
            {
                flag = true;
            }
        } while(true);
        return s5;
    }

    String statusFilterHTML(String s)
    {
        java.util.Vector vector = new Vector();
        vector.addElement("");
        vector.addElement("show all monitors");
        vector.addElement("error or warning");
        vector.addElement("show only monitors that had errors or warnings");
        vector.addElement("error");
        vector.addElement("show only monitors that had errors");
        vector.addElement("warning");
        vector.addElement("show only monitors that had warnings");
        vector.addElement("good");
        vector.addElement("show only monitors that were OK");
        if(s.length() == 0)
        {
            s = (String)vector.elementAt(0);
        }
        return COM.dragonflow.Page.reportPage.getOptionsHTML(vector, s);
    }

    String scheduleFilterHTML(String s)
    {
        java.util.Vector vector = new Vector();
        vector.addElement("");
        vector.addElement("every day, all day");
        HashMap hashmap;
        for(Enumeration enumeration = COM.dragonflow.Page.CGI.getValues(getSettings(), "_additionalSchedule"); enumeration.hasMoreElements(); vector.addElement(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_name")))
        {
            String s1 = (String)enumeration.nextElement();
            hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap(s1);
            vector.addElement("_id=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id"));
        }

        if(s.length() == 0)
        {
            s = (String)vector.elementAt(0);
        }
        return COM.dragonflow.Page.reportPage.getOptionsHTML(vector, s);
    }

    String getTimeScaleHTML(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < timeScaleSettings.length; i++)
        {
            boolean flag = false;
            String s1 = timeScaleSettings[i];
            if(s.equals(s1))
            {
                flag = true;
            }
            String s2 = secondsToString(timeScaleSettings[i]);
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s2, flag));
        }

        return stringbuffer.toString();
    }

    String getVertScaleHTML(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < vertScaleSettings.length; i++)
        {
            boolean flag = false;
            String s1 = vertScaleSettings[i];
            if(s.equals(s1))
            {
                flag = true;
            }
            s1 = '"' + s1 + '"';
            String s2 = vertScaleSettings[i];
            if(s2.equals(""))
            {
                s2 = "automatic";
            }
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s2, flag));
        }

        return stringbuffer.toString();
    }

    String getStartHourHTML(String s, int i)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = "now";
        String s3 = operation.equals("adhoc") ? s1 : "at the time the report is run";
        boolean flag = s.equals(s1);
        stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s1, s3, flag));
        if(!s.equals("now"))
        {
            s = String.valueOf(COM.dragonflow.Utils.TextUtils.toInt(s) + i);
        } else
        {
            s = "-1";
        }
        for(int j = 0; j < 24; j++)
        {
            int k = j * 3600;
            String s2 = String.valueOf(k);
            String s4 = "at " + COM.dragonflow.Utils.TextUtils.dateToMilitaryTime(new Date(0, 0, 0, j, 0));
            boolean flag1 = COM.dragonflow.Utils.TextUtils.toInt(s) == k;
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s2, s4, flag1));
        }

        return stringbuffer.toString();
    }

    String getHoursHTML(int i)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int j = 0; j < 24; j++)
        {
            int k = j * 3600;
            String s = String.valueOf(k);
            String s1 = COM.dragonflow.Utils.TextUtils.numberToString(j);
            boolean flag = i == j;
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s, s1, flag));
        }

        return stringbuffer.toString();
    }

    String getMinutesHTML(int i)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int j = 0; j < 60; j++)
        {
            int k = j * 60;
            String s = String.valueOf(k);
            String s1 = COM.dragonflow.Utils.TextUtils.numberToString(j);
            boolean flag = i == j;
            stringbuffer.append(COM.dragonflow.Page.reportPage.getListOptionHTML(s, s1, flag));
        }

        return stringbuffer.toString();
    }

    String secondsToString(int i)
    {
        return secondsToString(String.valueOf(i));
    }

    String secondsToString(String s)
    {
        if(s.equals("default"))
        {
            return "automatic";
        }
        int i = 0;
        try
        {
            i = COM.dragonflow.Utils.TextUtils.toInt(s);
        }
        catch(java.lang.NumberFormatException numberformatexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "A number '" + s + "' was passed from the browser reportPage that did not parse as an integer: " + numberformatexception.toString());
            COM.dragonflow.Log.LogManager.log("Error", "reportPage is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(numberformatexception));
        }
        String s1 = "minute";
        int j = 0;
        if(i >= 0x278d00)
        {
            s1 = "month";
            j = i / 0x278d00;
        } else
        if(i >= 0x93a80)
        {
            s1 = "week";
            j = i / 0x93a80;
        } else
        if(i >= 0x15180)
        {
            s1 = "day";
            j = i / 0x15180;
        } else
        if(i >= 3600)
        {
            s1 = "hour";
            j = i / 3600;
        } else
        {
            j = i / 60;
        }
        if(j != 1)
        {
            s1 = s1 + "s";
        }
        if(j == 1)
        {
            return s1;
        } else
        {
            return j + " " + s1;
        }
    }

    public void printList()
    {
        printBodyHeader("Management Reports");
        if(COM.dragonflow.SiteView.Platform.isPortal())
        {
            helpFile = "CentraReports.htm";
        } else
        {
            helpFile = "HReports.htm";
        }
        printMenuItems(helpFile, "Reports");
        outputStream.println("<p>\n<H2>Management Reports</H2>\n<p>\n");
        COM.dragonflow.Page.reportPage.printReportTable(outputStream, request);
        outputStream.print("<P>To view management reports and summaries, click the link in the Reports column above.<P>");
        outputStream.print("<hr><font size=+1><b>Report Actions:</b></font>\n");
        outputStream.print("\n<TABLE BORDER=0 CELLSPACING=4 WIDTH=100%><tr><td width=15%>");
        String s = "/SiteView/cgi/go.exe/SiteView?page=report&account=" + request.getAccount();
        if(request.actionAllowed("_reportEdit"))
        {
            outputStream.print("<tr><td><A HREF=" + s + "&operation=add>Add</A> </td><td>Add a new scheduled management report for a specified time interval</td></tr>");
        }
        if(request.actionAllowed("_reportAdhoc"))
        {
            outputStream.print("<tr><td><A HREF=" + s + "&operation=adhoc>Quick</A> </td><td>Generate a Quick report for a custom time period and selected monitors</td></tr>");
        }
        if(request.actionAllowed("_progress"))
        {
            outputStream.print("<tr><td><A HREF=/SiteView/" + request.getAccountDirectory() + "/Progress.html>Progress</A> </td><td>View the SiteView Progress page for monitor load information</td></tr>");
        }
        if(!COM.dragonflow.SiteView.Platform.isPortal() && request.actionAllowed("_browse"))
        {
            outputStream.print("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=monitorSummary&account=" + request.getAccount() + ">Monitor Description</A> </td><td>Generate a report of monitor configuration settings by group or installation</td></tr>");
        }
//        if(COM.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings().isConnected() && request.actionAllowed("_topazConfigChangesReport"))
//        {
//            outputStream.print("<tr><td><A HREF=" + COM.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings().getAdminServerUrl() + "siteview/conf/sample_dispatcher?action=log" + ">" + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Configuration Changes Report</A> </td><td>View the configuration changes reported to " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + "</td></tr>");
//        }
        outputStream.print("</table><hr>");
        outputStream.print("<center><p>\n");
        printFooter(outputStream);
        outputStream.print("</HTML>\n");
    }

    static boolean portalIsReportAllowed(HashMap hashmap, String s)
    {
        return COM.dragonflow.Utils.TextUtils.getValue(hashmap, "account").equals(s);
    }

    static boolean isReportAllowed(HashMap hashmap, Array array)
    {
label0:
        {
            if(array.size() == 0)
            {
                break label0;
            }
            for(Enumeration enumeration = (Enumeration) hashmap.values("groups"); enumeration.hasMoreElements();)
            {
                String s = (String)enumeration.nextElement();
                if(!COM.dragonflow.Page.CGI.allowedByGroupFilter(s, array))
                {
                    return false;
                }
            }

            Enumeration enumeration1 = (Enumeration) hashmap.values("monitors");
            String as[];
label1:
            do
            {
                do
                {
                    if(!enumeration1.hasMoreElements())
                    {
                        break label0;
                    }
                    String s1 = (String)enumeration1.nextElement();
                    as = COM.dragonflow.Utils.TextUtils.split(s1);
                    if(as.length != 1)
                    {
                        continue label1;
                    }
                } while(COM.dragonflow.Page.CGI.allowedByGroupFilter(as[0], array));
                return false;
            } while(as.length <= 1 || COM.dragonflow.Page.CGI.allowedByGroupFilter(as[1], array));
            return false;
        }
        return true;
    }

    public static void printReportTable(java.io.PrintWriter printwriter, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        Array array = COM.dragonflow.Page.reportPage.getReportFrames(httprequest.getAccount());
        String s = httprequest.getAccount();
        printwriter.println("<TABLE BORDER=1 cellspacing=0 WIDTH=100%>\n<TR CLASS=\"tabhead\"><TH WIDTH=50%>Reports</TH><TH>Time Period</TH>\n");
        boolean flag = httprequest.actionAllowed("_reportEdit");
        if(flag)
        {
            printwriter.print("<TH>Edit</TH><TH WIDTH=3%>Del</TH>");
        }
        printwriter.print("</TR>");
        Enumeration enumeration = (Enumeration) array.iterator();
        if(!enumeration.hasMoreElements())
        {
            byte byte0 = 4;
            if(flag)
            {
                byte0 = 2;
            }
            printwriter.print("<TR><TD ALIGN=CENTER COLSPAN=" + byte0 + "><B>No Management Reports Scheduled</B></TD></TR>\n");
        } else
        {
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap = (HashMap)enumeration.nextElement();
                if(hashmap.get("isQuick") == null || ((String)hashmap.get("isQuick")).length() <= 0)
                {
                    String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "title");
                    if(s1.length() == 0)
                    {
                        String s2 = COM.dragonflow.Page.reportPage.getDefaultReportTitle(hashmap);
                        hashmap.put("title", s2);
                    }
                }
            } while(true);
            Sorting.sort(array, new CompareSlot("title", COM.dragonflow.SiteView.CompareSlot.DIRECTION_LESS));
            enumeration = (Enumeration) array.iterator();
            Array array1 = COM.dragonflow.Page.CGI.getGroupFilterForAccount(httprequest);
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap1 = (HashMap)enumeration.nextElement();
                if((hashmap1.get("isQuick") == null || ((String)hashmap1.get("isQuick")).length() <= 0) && (COM.dragonflow.SiteView.Platform.isPortal() ? COM.dragonflow.Page.reportPage.portalIsReportAllowed(hashmap1, s) : COM.dragonflow.Page.reportPage.isReportAllowed(hashmap1, array1)))
                {
                    String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "title");
                    String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "window");
                    int i = COM.dragonflow.Properties.StringProperty.toInteger(s4);
                    String s5;
                    switch(i)
                    {
                    case 3600: 
                        s5 = "last hour";
                        break;

                    case 86400: 
                        s5 = "last day";
                        break;

                    case 604800: 
                        s5 = "last week";
                        break;

                    case 2592000: 
                        s5 = "last month";
                        break;

                    default:
                        String s6 = "minutes";
                        int j = 0;
                        if(i >= 0x2a300)
                        {
                            s6 = "days";
                            j = i / 0x15180;
                        } else
                        if(i >= 7200)
                        {
                            s6 = "hours";
                            j = i / 3600;
                        } else
                        {
                            j = i / 60;
                        }
                        if(s4.equals("monthToDate"))
                        {
                            s5 = "month-to-date";
                        } else
                        {
                            s5 = "last " + j + " " + s6;
                        }
                        break;
                    }
                    String s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "id");
                    java.io.File file = COM.dragonflow.Page.reportPage.createReportIndexFile(s, s7, flag);
                    String s8 = "";
                    String s9 = "";
                    if(file.exists())
                    {
                        s8 = "<A HREF=" + COM.dragonflow.SiteView.Platform.getURLPath(COM.dragonflow.SiteView.HistoryReport.accountToDirectory(s), s) + "/Reports-" + s7 + "/index.html>";
                        s9 = "</A>";
                    }
                    if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "disabled").length() > 0)
                    {
                        s3 = "<B>(disabled)</B> " + s3;
                    }
                    printwriter.print("<TR><TD>" + s8 + s3 + s9 + "</TD>" + "<TD>" + s5 + "</TD>");
                    if(flag)
                    {
                        printwriter.print("<TD><A HREF=/SiteView/cgi/go.exe/SiteView?page=report&operation=edit&queryID=" + s7 + "&account=" + s + ">Edit</A></TD>" + "<TD><A HREF=/SiteView/cgi/go.exe/SiteView?page=report&operation=Delete&queryID=" + s7 + "&account=" + s + ">X</A></TD>");
                    }
                    printwriter.print("</TR>\n");
                }
            } while(true);
        }
        printwriter.print("</TABLE>");
    }

    private static java.io.File createReportIndexFile(String s, String s1, boolean flag)
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getDirectoryPath(COM.dragonflow.SiteView.HistoryReport.accountToDirectory(s), s) + java.io.File.separator + "Reports-" + s1 + java.io.File.separator + "index.html");
        if(!file.exists())
        {
            if(COM.dragonflow.SiteView.Platform.isStandardAccount(s))
            {
                COM.dragonflow.SiteView.HistoryReport.generateIndexPage("administrator", s1, "true");
                if(COM.dragonflow.SiteView.Platform.isUserAccessAllowed())
                {
                    COM.dragonflow.SiteView.HistoryReport.generateIndexPage("user", s1, "");
                }
            } else
            {
                COM.dragonflow.SiteView.HistoryReport.generateIndexPage(s, s1, flag ? "true" : "");
            }
        }
        return file;
    }

    public static void createReportsIndexFiles(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        Enumeration enumeration = COM.dragonflow.Page.reportPage.getReportFrames(null).elements();
        Array array = COM.dragonflow.Page.CGI.getGroupFilterForAccount(httprequest);
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap = (HashMap)enumeration.nextElement();
            if((hashmap.get("isQuick") == null || ((String)hashmap.get("isQuick")).length() <= 0) && (COM.dragonflow.SiteView.Platform.isPortal() ? COM.dragonflow.Page.reportPage.portalIsReportAllowed(hashmap, httprequest.getAccount()) : COM.dragonflow.Page.reportPage.isReportAllowed(hashmap, array)))
            {
                String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "id");
                COM.dragonflow.Page.reportPage.createReportIndexFile(httprequest.getAccount(), s, httprequest.actionAllowed("_reportEdit"));
            }
        } while(true);
    }

    public static String getDefaultReportTitle(HashMap hashmap)
    {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        int i = 0;
        int j = 0;
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        Enumeration enumeration = (Enumeration) hashmap.values("groups");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s);
            if(monitorgroup != null)
            {
                if(stringbuffer.length() > 0)
                {
                    stringbuffer.append(", ");
                }
                stringbuffer.append(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                i++;
            }
        } while(true);
        enumeration = (Enumeration) hashmap.values("monitors");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding((String)enumeration.nextElement());
            String as[] = COM.dragonflow.Utils.TextUtils.split(s1);
            if(as.length == 1)
            {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(as[0]);
                if(monitorgroup1 != null)
                {
                    if(stringbuffer.length() > 0)
                    {
                        stringbuffer.append(", ");
                    }
                    stringbuffer.append(monitorgroup1.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                    i++;
                }
            } else
            if(as.length > 1)
            {
                COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)siteviewgroup.getElement(as[1] + "/" + as[0]);
                if(monitor != null)
                {
                    if(stringbuffer1.length() > 0)
                    {
                        stringbuffer1.append(", ");
                    }
                    stringbuffer1.append(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                    j++;
                }
            }
        } while(true);
        StringBuffer stringbuffer2 = new StringBuffer();
        if(i > 0)
        {
            stringbuffer2.append(stringbuffer.toString());
            if(i == 1)
            {
                stringbuffer2.append(" Group");
            } else
            {
                stringbuffer2.append(" Groups");
            }
        }
        if(j > 0)
        {
            if(stringbuffer2.length() > 0)
            {
                stringbuffer2.append(" and ");
            }
            stringbuffer2.append(stringbuffer1.toString());
            if(j == 1)
            {
                stringbuffer2.append(" Monitor");
            } else
            {
                stringbuffer2.append(" Monitors");
            }
        }
        if(j == 0 && i == 0)
        {
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "showAlertsOnly").length() == 0)
            {
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "query").length() > 0)
                {
                    COM.dragonflow.SiteView.PortalFilter portalfilter = new PortalFilter(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "query"));
                    stringbuffer2.append(portalfilter.getDescription());
                } else
                {
                    stringbuffer2.append("No monitors in report");
                }
            } else
            {
                stringbuffer2.append("All Alerts");
            }
        }
        return stringbuffer2.toString();
    }

    public static Array getReportFrames(String s)
    {
        Array array = null;
        try
        {
            if(s != null && !COM.dragonflow.SiteView.Platform.isStandardAccount(s))
            {
                Array array1 = COM.dragonflow.Page.reportPage.ReadGroupFrames(s, null);
                array = new Array();
                Enumeration enumeration =  (Enumeration) array1.iterator();
                HashMap hashmap = (HashMap)enumeration.nextElement();
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    HashMap hashmap1 = (HashMap)enumeration.nextElement();
                    if(COM.dragonflow.SiteView.Monitor.isReportFrame(hashmap1))
                    {
                        array.add(hashmap1);
                    }
                } while(true);
            } else
            {
                String s1 = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config";
                java.io.File file = new File(s1);
                if(!file.exists())
                {
                    array = new Array();
                } else
                {
                    array = COM.dragonflow.Properties.FrameFile.readFromFile(s1);
                }
            }
        }
        catch(java.io.IOException ioexception)
        {
            array = new Array();
        }
        return array;
    }

    public Array getReportFrameList()
    {
        Array array = null;
        try
        {
            if(!request.isStandardAccount())
            {
                array = ReadGroupFrames(request.getAccount());
            } else
            {
                String s = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config";
                java.io.File file = new File(s);
                if(!file.exists())
                {
                    array = new Array();
                } else
                {
                    array = COM.dragonflow.Properties.FrameFile.readFromFile(s);
                }
            }
        }
        catch(java.io.IOException ioexception)
        {
            array = new Array();
        }
        return array;
    }

    public void saveReportFrameList(Array array, String s)
    {
        try
        {
            if(!COM.dragonflow.SiteView.Platform.isStandardAccount(s))
            {
                WriteGroupFrames(s, array);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s);
            } else
            {
                COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config", array);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
            }
        }
        catch(java.io.IOException ioexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "reportPage.saveReportFrameList() is unhappy with report config file, history.config: " + ioexception.toString());
            COM.dragonflow.Log.LogManager.log("Error", "reportPage.saveReportFrameList() is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(ioexception));
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.reportPage reportpage = new reportPage();
        if(args.length > 0)
        {
            reportpage.args = args;
        }
        reportpage.handleRequest();
    }

    private void printMenuItems(String s, String s1)
    {
        COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
        printButtonBar(s, s1, menus1);
    }

}
