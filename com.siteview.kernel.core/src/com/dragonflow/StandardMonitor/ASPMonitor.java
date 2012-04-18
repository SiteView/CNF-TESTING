/*
 * 
 * Created on 2005-3-5 14:19:19
 *
 * ASPMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>ASPMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.*;
import java.net.URLEncoder;
import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

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
     addProperties("com.dragonflow.StandardMonitor.ASPMonitor", astringproperty);
     addClassElement("com.dragonflow.StandardMonitor.ASPMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
     addClassElement("com.dragonflow.StandardMonitor.ASPMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "description", "Monitors Microsoft Active Server Pages server performance statistics.");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "help", "ASPServerMon.htm");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "title", "ASP Server");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "class", "ASPMonitor");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "target", "_serverName");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "topazName", "MS Active Server Pages");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "topazType", "Web Application Server");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "classType", "application");
     setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "applicationType", "NTCounter");
     if(!Platform.isWindows())
     {
         setClassProperty("com.dragonflow.StandardMonitor.ASPMonitor", "loadable", "false");
     }
 }
}