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

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.SiteView.SampleCollector;
import com.dragonflow.SiteView.SiteViewLogReader;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class netuitivePage extends com.dragonflow.Page.CGI
{

    public netuitivePage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_browse"))
        {
            throw new HTTPRequestException(557);
        }
        long l = getRequestTime();
        java.util.GregorianCalendar gregoriancalendar = new GregorianCalendar();
        long l1 = gregoriancalendar.getTime().getTime();
        for(Enumeration enumeration = request.getValues("id"); enumeration.hasMoreElements();)
        {
            String s = enumeration.nextElement().toString();
            Array array = com.dragonflow.Page.netuitivePage.stringToArray(s, ':');
            if(array.size() == 3)
            {
                String s1 = (String)array.get(0);
                String s2 = (String)array.get(1);
                String s3 = (String)array.get(2);
                com.dragonflow.SiteView.MonitorGroup monitorgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroup(s1);
                if(monitorgroup != null)
                {
                    Enumeration enumeration1 = monitorgroup.getMonitors();
                    while(enumeration1.hasMoreElements()) 
                    {
                        com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor)enumeration1.nextElement();
                        if(s2.equalsIgnoreCase(monitor.getProperty("_id")))
                        {
                            Enumeration enumeration2 = monitor.getStatePropertyObjects(true);
                            while(enumeration2.hasMoreElements()) 
                            {
                                com.dragonflow.Properties.StringProperty stringproperty = (com.dragonflow.Properties.StringProperty)enumeration2.nextElement();
                                if(stringproperty.getLabel().equalsIgnoreCase(s3))
                                {
                                    long l2 = com.dragonflow.Utils.TextUtils.toLong(monitor.getValue("_frequency"));
                                    if(l < l1 - (l2 * 1000L) / 2L)
                                    {
                                        long l3 = l - 500L * l2;
                                        long l4 = l + 500L * l2;
                                        int i = (int)((l4 - l3) / 1000L);
                                        java.util.Date date = new Date(l3);
                                        java.util.Date date1 = new Date(l4);
                                        Array array1 = new Array();
                                        com.dragonflow.SiteView.SampleCollector samplecollector = new SampleCollector(monitor, stringproperty);
                                        array1.add(samplecollector);
                                        java.io.File file = new File(com.dragonflow.SiteView.Platform.getDirectoryPath("logs", "administrator") + java.io.File.separator + "SiteView.log");
                                        com.dragonflow.SiteView.SiteViewLogReader siteviewlogreader = new SiteViewLogReader(file);
                                        siteviewlogreader.process(array1, date, date1, 0L, i, null, null, null, 0L, 0L, 0L, new HashMap());
                                        if(samplecollector.getSampleCount(0) > 0)
                                        {
                                            outputStream.println(s + '\t' + samplecollector.getAverage(0));
                                        }
                                    } else
                                    {
                                        outputStream.println(s + '\t' + monitor.getProperty(stringproperty));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static Array stringToArray(String s, char c)
    {
        Array array = new Array();
        while(s.length() > 0) 
        {
            int i = s.indexOf(c);
            if(i == -1)
            {
                array.add(s);
                s = "";
            } else
            {
                array.add(s.substring(0, i));
                s = s.substring(i + 1);
            }
        }
        return array;
    }

    protected long getRequestTime()
    {
        long l = com.dragonflow.Utils.TextUtils.toLong(request.getValue("time"));
        if(l != 0L)
        {
            java.util.TimeZone timezone = java.util.TimeZone.getDefault();
            int i = timezone.getRawOffset();
            l = l * 1000L + (long)i;
        }
        return l;
    }

    public void printCGIHeader()
    {
        request.printHeader(outputStream);
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        com.dragonflow.Page.netuitivePage netuitivepage = new netuitivePage();
        if(args.length > 0)
        {
            netuitivepage.args = args;
        }
        netuitivepage.handleRequest();
    }
}
