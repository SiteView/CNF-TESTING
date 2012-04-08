/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SQLServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SQLServerMonitor</code>
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

public class SQLServerMonitor extends NTCounterBase
{

    static String returnURL;
    static String counterObjects;
    private static StringProperty pCounters;

    public SQLServerMonitor()
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
        String s1 = getSetting("_counterObjectsSQLServerMonitor");
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
        String s1 = getSetting("_counterInstanceSQLServerMonitor");
        String s;
        if(s1 == "")
        {
            s = "_Total";
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
        return "\\\\testwin2k9.qa.dragonflow.com";
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
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=SQLServerMonitor";
        counterObjects = "SQLServer:Access Methods,SQLServer:Backup Device,SQLServer:Buffer Manager,SQLServer:Buffer Partition,SQLServer:Cache Manager,SQLServer:Databases,SQLServer:General Statistics,SQLServer:Latches,SQLServer:Locks,SQLServer:Memory Manager,SQLServer:Replication Agents,SQLServer:Replication Dist.,SQLServer:Replication Logreader,SQLServer:Replicaton Merge,SQLServer:Replication Snapshot,SQLServer:SQL Statistics,SQLServer:User Settable";
        pCounters = new StringProperty("_counters", "", "Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
        pCounters.setParameterOptions(false, true, 1, false);
        pCounters.isMultiLine = true;
        StringProperty astringproperty[] = {
            pCounters
        };
        addProperties("COM.dragonflow.StandardMonitor.SQLServerMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.SQLServerMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.SQLServerMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "description", "Monitors Microsoft SQL Server performance metrics.");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "help", "SQLServerMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "title", "SQL Server");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "class", "SQLServerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "target", "_serverName");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "topazName", "MS SQL Server");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "topazType", "Database Server");
        setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "applicationType", "NTCounter");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.SQLServerMonitor", "loadable", "false");
        }
    }
}
