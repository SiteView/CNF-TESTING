/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * RealMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>RealMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.PerfCounter;

import java.net.URLEncoder;
import jgl.Array;
import jgl.HashMap;

public class RealMonitor extends NTCounterBase
{

    public static String returnURL;
    public static String counterObjects;
    private static StringProperty pCounters;

    public RealMonitor()
    {
    }

    public String getCountersContent()
    {
        return getProperty(pCounters);
    }

    public void setCountersContent(String s)
    {
        setProperty(pCounters, s);
    }

    public StringProperty getCountersProperty()
    {
        return pCounters;
    }

    public String getCounterObjects()
    {
        String s1 = getSetting("_counterObjectsRealMonitor");
        String s;
        if(s1 == "")
        {
            s = counterObjects;
        } else
        {
            s = s1;
        }
        return s;
    }

    public String getReturnURL()
    {
        return HTTPRequest.encodeString(returnURL);
    }

    public String getDefaultInstance()
    {
        String s1 = getSetting("_counterInstanceRealMonitor");
        String s;
        if(s1 == "")
        {
            s = "RMServer";
        } else
        {
            s = s1;
        }
        return s;
    }

    public String getDefaultCounters()
    {
        boolean flag = true;
        String s = "";
        String s1 = getDefaultInstance();
        Array array = getAvailableCounters();
        int i = 0;
        for(int j = 0; j < array.size(); j++)
        {
            if(!((PerfCounter)array.at(j)).instance.equals(s1))
            {
                continue;
            }
            if(i >= nMaxCounters)
            {
                break;
            }
            if(!flag)
            {
                s = s + ",";
            } else
            {
                flag = false;
            }
            s = s + ((PerfCounter)array.at(j)).object + " -- ";
            s = s + ((PerfCounter)array.at(j)).counterName;
            if(((PerfCounter)array.at(j)).instance.length() > 0)
            {
                s = s + " -- " + ((PerfCounter)array.at(j)).instance;
            }
            i++;
        }

        return s;
    }

    protected String getTestMachine()
    {
        return "\\\\TESTWIN2K4.qa.dragonflow.com";
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pCounters)
        {
            if(s.equals("No Counters available for this machine"))
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static 
    {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=RealMonitor";
        counterObjects = "RMServer";
        pCounters = new StringProperty("_counters", "", "Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
        pCounters.setParameterOptions(false, true, 1, false);
        pCounters.isMultiLine = true;
        StringProperty astringproperty[] = {
            pCounters
        };
        addProperties("COM.dragonflow.StandardMonitor.RealMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.RealMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.RealMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "description", "Monitors RealNetworks RealSystem Media Server performance metrics.");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "help", "RealMediaMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "title", "Real Media Server");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "class", "RealMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "target", "_serverName");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "topazName", "Real Media Server");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "topazType", "Streaming Media");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "applicationType", "NTCounter");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.RealMonitor", "loadable", "false");
        }
    }
}
