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

import java.util.Date;

import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.SiteViewException.SiteViewParameterException;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class adhocReportPage extends com.dragonflow.Page.CGI
{

    public adhocReportPage()
        throws java.io.IOException
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        try
        {
            if(request.hasValue("queryID"))
            {
                if(!request.actionAllowed("_reportGenerate"))
                {
                    throw new HTTPRequestException(557);
                }
                HashMap hashmap = new HashMap();
                hashmap.put("mainLink", request.getValue("mainLink"));
                hashmap.put("aboutLink", request.getValue("aboutLink"));
                com.dragonflow.SiteView.HistoryReport.generateReportFromQueryID(request.getValue("queryID"), outputStream, request.getAccount(), hashmap);
            } else
            {
                request.setValue("mainLink", "true");
                request.setValue("aboutLink", "true");
                if(!request.actionAllowed("_reportAdhoc") && !request.actionAllowed("_monitorRecent"))
                {
                    throw new HTTPRequestException(557);
                }
                if(request.getValue("startDay").length() <= 0 || request.getValue("startHour").length() <= 0 || request.getValue("window").length() <= 0)
                {
                    if(!request.getValues("groups").hasMoreElements() && !request.getValues("monitors").hasMoreElements())
                    {
                        throw new SiteViewParameterException(com.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_REPORT_NO_MONITORS);
                    }
                    long l = com.dragonflow.Utils.TextUtils.toLong(request.getUserSetting("_timeOffset")) * 1000L;
                    if(request.getValue("startTimeDate").length() > 0)
                    {
                        String s2 = request.getValue("startTimeDate");
                        checkDate(s2);
                        String s4 = request.getValue("startTimeTime");
                        checkTime(s4);
                        String s5 = request.getValue("endTimeDate");
                        checkDate(s5);
                        String s6 = request.getValue("endTimeTime");
                        checkTime(s6);
                        long l1 = com.dragonflow.Utils.TextUtils.dateStringToSeconds(request.getValue("startTimeDate"), request.getValue("startTimeTime"));
                        long l2 = com.dragonflow.Utils.TextUtils.dateStringToSeconds(request.getValue("endTimeDate"), request.getValue("endTimeTime"));
                        long l3 = l1 * 1000L - l;
                        long l4 = l2 * 1000L - l;
                        java.util.Date date = new Date(l4);
                        request.setValue("startHour", date.getHours() + ":" + com.dragonflow.Utils.TextUtils.numberToString(date.getMinutes()));
                        request.setValue("startDay", "" + (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900));
                        long l5 = (l4 - l3) / 1000L;
                        long l6 = 0x1e28500L;
                        if(l5 > l6)
                        {
                            l5 = l6;
                        }
                        request.setValue("window", "" + l5);
                    }
                }
                String s = "";
                if(request.isGet())
                {
                    s = "<a href=\"javascript:window.close()\">Close Window</a>";
                } else
                {
                    s = "<a href=\"/SiteView/cgi/go.exe/SiteView?page=report&account=" + request.getAccount() + "&operation=adhoc\">Quick Report Form</a>";
                }
                request.setValue("isadhoc", "true");
                com.dragonflow.SiteView.HistoryReport.generateReportFromRequest(request, outputStream, s);
            }
        }
        catch(com.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            java.lang.Object obj = new java.util.HashMap();
            if(siteviewexception instanceof com.dragonflow.SiteViewException.SiteViewParameterException)
            {
                com.dragonflow.SiteViewException.SiteViewParameterException siteviewparameterexception = (com.dragonflow.SiteViewException.SiteViewParameterException)siteviewexception;
                obj = siteviewparameterexception.getErrorParameterMap();
            }
            printBodyHeader("Management Report Error");
            outputStream.print("<H2>Management Report Error</H2><P><HR><UL>\n");
            if(((java.util.Map) (obj)).size() > 0)
            {
                java.util.Collection collection = ((java.util.Map) (obj)).values();
                java.util.Iterator iterator = collection.iterator();
                do
                {
                    if(!iterator.hasNext())
                    {
                        break;
                    }
                    String s3 = (String)iterator.next();
                    if(s3.length() > 0)
                    {
                        outputStream.print("<LI>" + s3 + "\n");
                    }
                } while(true);
            } else
            {
                String s1 = siteviewexception.getMessage();
                if(s1.length() > 0)
                {
                    outputStream.print("<LI>" + s1 + "\n");
                }
            }
            outputStream.print("</UL><HR></BODY></HTML>");
        }
    }

    private void checkDate(String s)
        throws com.dragonflow.SiteViewException.SiteViewException
    {
        if(!com.dragonflow.Utils.TextUtils.isDateStringValid(s))
        {
            String as[] = {
                s
            };
            throw new SiteViewParameterException(com.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_REPORT_INVALID_DATE, as);
        } else
        {
            return;
        }
    }

    private void checkTime(String s)
        throws com.dragonflow.SiteViewException.SiteViewException
    {
        if(!com.dragonflow.Utils.TextUtils.isTimeStringValid(s))
        {
            String as[] = {
                s
            };
            throw new SiteViewParameterException(com.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_REPORT_INVALID_TIME, as);
        } else
        {
            return;
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.adhocReportPage adhocreportpage = new adhocReportPage();
        if(args.length > 0)
        {
            adhocreportpage.args = args;
        }
        adhocreportpage.handleRequest();
    }
}
