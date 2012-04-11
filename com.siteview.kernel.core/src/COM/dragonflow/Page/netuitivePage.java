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
import java.util.GregorianCalendar;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.SiteView.SampleCollector;
import COM.dragonflow.SiteView.SiteViewLogReader;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class netuitivePage extends COM.dragonflow.Page.CGI
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
            jgl.Array array = COM.dragonflow.Page.netuitivePage.stringToArray(s, ':');
            if(array.size() == 3)
            {
                String s1 = (String)array.at(0);
                String s2 = (String)array.at(1);
                String s3 = (String)array.at(2);
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroup(s1);
                if(monitorgroup != null)
                {
                    Enumeration enumeration1 = monitorgroup.getMonitors();
                    while(enumeration1.hasMoreElements()) 
                    {
                        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)enumeration1.nextElement();
                        if(s2.equalsIgnoreCase(monitor.getProperty("_id")))
                        {
                            Enumeration enumeration2 = monitor.getStatePropertyObjects(true);
                            while(enumeration2.hasMoreElements()) 
                            {
                                COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration2.nextElement();
                                if(stringproperty.getLabel().equalsIgnoreCase(s3))
                                {
                                    long l2 = COM.dragonflow.Utils.TextUtils.toLong(monitor.getValue("_frequency"));
                                    if(l < l1 - (l2 * 1000L) / 2L)
                                    {
                                        long l3 = l - 500L * l2;
                                        long l4 = l + 500L * l2;
                                        int i = (int)((l4 - l3) / 1000L);
                                        java.util.Date date = new Date(l3);
                                        java.util.Date date1 = new Date(l4);
                                        jgl.Array array1 = new Array();
                                        COM.dragonflow.SiteView.SampleCollector samplecollector = new SampleCollector(monitor, stringproperty);
                                        array1.add(samplecollector);
                                        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getDirectoryPath("logs", "administrator") + java.io.File.separator + "SiteView.log");
                                        COM.dragonflow.SiteView.SiteViewLogReader siteviewlogreader = new SiteViewLogReader(file);
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

    public static jgl.Array stringToArray(String s, char c)
    {
        jgl.Array array = new Array();
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
        long l = COM.dragonflow.Utils.TextUtils.toLong(request.getValue("time"));
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
        COM.dragonflow.Page.netuitivePage netuitivepage = new netuitivePage();
        if(args.length > 0)
        {
            netuitivepage.args = args;
        }
        netuitivepage.handleRequest();
    }
}
