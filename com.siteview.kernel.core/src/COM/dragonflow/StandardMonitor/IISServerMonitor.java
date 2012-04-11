/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * IISServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>IISServerMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import java.net.URLEncoder;
import jgl.HashMap;

public class IISServerMonitor extends NTCounterBase
{

    static String returnURL;
    static String counterObjects;
    private static StringProperty pCounters;

    public IISServerMonitor()
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
        String s1 = getSetting("_counterObjectsIISServerMonitor");
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
        String s1 = getSetting("_counterInstanceIISServerMonitor");
        String s;
        if(s1 == "")
        {
            s = "Default Web Site";
        } else
        {
            s = s1;
        }
        return s;
    }

    public String getDefaultCounters()
    {
        String s = getDefaultInstance();
        return "Web Service -- Bytes Sent/sec -- " + s + "," + "Web Service -- Bytes Received/sec -- " + s + "," + "Web Service -- Bytes Total/sec -- " + s + "," + "Web Service -- Get Requests/sec -- " + s + "," + "Web Service -- Post Requests/sec -- " + s + "," + "Web Service -- Current Connections -- " + s + "," + "Web Service -- Maximum Connections -- " + s + "," + "Web Service -- Current NonAnonymous Users -- " + s + "," + "Web Service -- Total Not Found Errors -- " + s + "," + "Web Service -- Private Bytes (inetinfo) -- " + s;
    }

    protected String getTestMachine()
    {
        return "\\\\TESTWIN2K8.qa.dragonflow.com";
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
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=IISServerMonitor";
        counterObjects = "Web Service";
        pCounters = new StringProperty("_counters", "", "IIS Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
        pCounters.setParameterOptions(false, true, 1, false);
        pCounters.isMultiLine = true;
        StringProperty astringproperty[] = {
            pCounters
        };
        addProperties("COM.dragonflow.StandardMonitor.IISServerMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.IISServerMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.IISServerMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "description", "Monitors Microsoft IIS web server performance statistics.");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "help", "IISServerMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "title", "IIS Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "class", "IISServerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "target", "_serverName");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "topazName", "MS IIS Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "topazType", "Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "applicationType", "NTCounter");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.IISServerMonitor", "loadable", "false");
        }
    }
}
