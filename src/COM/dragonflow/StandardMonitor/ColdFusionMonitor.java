/*
 * 
 * Created on 2005-3-7 0:53:01
 *
 * ColdFusionMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ColdFusionMonitor</code>
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

public class ColdFusionMonitor extends NTCounterBase
{

 static String returnURL;
 static String counterObjects;
 private static StringProperty pCounters;

 public ColdFusionMonitor()
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
     String s1 = getSetting("_counterObjectsColdFusionMonitor");
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
     String s1 = getSetting("_counterInstanceColdFusionMonitor");
     String s;
     if(s1 == "")
     {
         s = "cfserver";
     } else
     {
         s = s1;
     }
     return s;
 }

 public String getDefaultCounters()
 {
     return "ColdFusion Server -- Page Hits / Sec -- cfserver,ColdFusion Server -- Bytes Out / Sec -- cfserver,ColdFusion Server -- Queued Requests -- cfserver,ColdFusion Server -- Running Requests -- cfserver,ColdFusion Server -- Avg Queue Time (msec) -- cfserver,ColdFusion Server -- Avg Req Time (msec) -- cfserver";
 }

 protected String getTestMachine()
 {
     return "\\\\TESTWIN2K1.qa.Dragonflow.com";
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
     returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=ColdFusionMonitor";
     counterObjects = "ColdFusion Server";
     pCounters = new StringProperty("_counters", "", "ColdFusion Selected Counters");
     pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
     pCounters.setParameterOptions(false, true, 1, false);
     pCounters.isMultiLine = true;
     StringProperty astringproperty[] = {
         pCounters
     };
     addProperties("COM.dragonflow.StandardMonitor.ColdFusionMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.ColdFusionMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.ColdFusionMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "description", "Monitors ColdFusion server performance metrics.");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "help", "ColdFusionMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "title", "ColdFusion Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "class", "ColdFusionMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "target", "_serverName");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "topazName", "MS ColdFusion Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "topazType", "Web Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "applicationType", "NTCounter");
     if(!Platform.isWindows())
     {
         setClassProperty("COM.dragonflow.StandardMonitor.ColdFusionMonitor", "loadable", "false");
     }
 }
}