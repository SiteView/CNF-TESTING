/*
 * 
 * Created on 2005-3-5 14:19:19
 *
 * ASPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ASPMonitor</code>
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
import jgl.Array;
import jgl.HashMap;

public class ASPMonitor extends NTCounterBase
{

 static String returnURL;
 static String counterObjects;
 private static StringProperty pCounters;

 public ASPMonitor()
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
     String s1 = getSetting("_counterObjectsASPMonitor");
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
     String s1 = getSetting("_counterInstanceASPMonitor");
     String s;
     if(s1 == "")
     {
         s = "SINGLE";
     } else
     {
         s = s1;
     }
     return s;
 }

 public String getDefaultCounters()
 {
     return "Active Server Pages -- Errors/Sec -- SINGLE,Active Server Pages -- Requests/Sec -- SINGLE,Active Server Pages -- Bytes Total/sec -- SINGLE,Active Server Pages -- Transactions/Sec -- SINGLE,Active Server Pages -- Errors During Script Runtime -- SINGLE,Active Server Pages -- Request Wait Time -- SINGLE,Active Server Pages -- Requests Executing -- SINGLE,Active Server Pages -- Requests Not Found -- SINGLE,Active Server Pages -- Requests Queued -- SINGLE,Active Server Pages -- Requests Rejected -- SINGLE";
 }

 protected String getTestMachine()
 {
     return "\\\\TESTWIN2K1.qa.Dragonflow.com";
 }

 boolean _testGetPerformanceData(String s, Array array)
 {
     return getPerformanceData(s, array, nMaxCounters, 1.0F, "");
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
     returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=ASPMonitor";
     counterObjects = "Active Server Pages";
     pCounters = new StringProperty("_counters", "", "Selected Counters");
     pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
     pCounters.setParameterOptions(false, true, 1, false);
     pCounters.isMultiLine = true;
     StringProperty astringproperty[] = {
         pCounters
     };
     addProperties("COM.dragonflow.StandardMonitor.ASPMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.ASPMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.ASPMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "description", "Monitors Microsoft Active Server Pages server performance statistics.");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "help", "ASPServerMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "title", "ASP Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "class", "ASPMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "target", "_serverName");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "topazName", "MS Active Server Pages");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "topazType", "Web Application Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "applicationType", "NTCounter");
     if(!Platform.isWindows())
     {
         setClassProperty("COM.dragonflow.StandardMonitor.ASPMonitor", "loadable", "false");
     }
 }
}