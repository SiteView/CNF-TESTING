/*
 * 
 * Created on 2005-3-5 14:16:07
 *
 * ADPerformanceMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ADPerformanceMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import jgl.Array;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.StandardMonitor:
//         URLMonitor

public class ADPerformanceMonitor extends NTCounterBase
{

 static String returnURL;
 static String counterObjects;
 private static StringProperty pCounters;
 static String default_counters_path;

 public ADPerformanceMonitor()
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
     String s1 = getSetting("_counterObjectsADPerformanceMonitor");
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
     String s1 = getSetting("_counterInstanceADPerformanceMonitor");
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
     File file = new File(default_counters_path);
     if(file.exists())
     {
         try
         {
             StringBuffer stringbuffer = FileUtils.readFile(default_counters_path);
             s = TextUtils.replaceString(stringbuffer.toString(), URLMonitor.CRLF, ",");
         }
         catch(IOException ioexception)
         {
             ioexception.printStackTrace();
         }
     } else
     {
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

     }
     return s;
 }

 protected String getTestMachine()
 {
     return "\\\\BCROWE";
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
     returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=ADPerformanceMonitor";
     counterObjects = "FileReplicaConn,FileReplicaSet,NTDS,Database";
     default_counters_path = Platform.getRoot() + File.separator + "templates.applications" + File.separator + "counters.ad";
     HashMap hashmap = MasterConfig.getMasterConfig();
     pCounters = new StringProperty("_counters", "", "Selected Counters");
     pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&counterobjects=" + counterObjects + "&type=NTCounter\">choose counters</A>");
     pCounters.setParameterOptions(false, true, 1, false);
     pCounters.isMultiLine = true;
     StringProperty astringproperty[] = {
         pCounters
     };
     addProperties("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", Rule.stringToClassifier("value0 == n/a\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "description", "Monitors Microsoft Active Directory performance metrics.");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "help", "ADPerfMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "title", "Active Directory Performance");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "class", "ADPerformanceMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "target", "_serverName");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "topazName", "Active Directory Perf");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "classType", "beta");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "topazType", "Application Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "applicationType", "NTCounter");
     if(TextUtils.getValue(hashmap, "_allowADPerfromanceMonitor").length() <= 0 || !Platform.isWindows())
     {
         setClassProperty("COM.dragonflow.StandardMonitor.ADPerformanceMonitor", "loadable", "false");
     }
 }
}