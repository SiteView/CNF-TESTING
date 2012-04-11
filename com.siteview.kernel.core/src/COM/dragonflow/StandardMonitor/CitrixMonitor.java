/*
 * 
 * Created on 2005-3-7 0:52:39
 *
 * CitrixMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>CitrixMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.Pair;

import java.util.*;
import jgl.Array;
import jgl.HashMap;

public class CitrixMonitor extends BrowsablePdhBase
 implements IServerPropMonitor
{

 static ServerProperty pCitrixServer;

 public CitrixMonitor()
 {
 }

 public Array getConnectionProperties()
 {
     return new Array();
 }

 public boolean isServerBased()
 {
     return false;
 }

 protected List getBrowsableObjects()
 {
     LinkedList linkedlist = new LinkedList();
     linkedlist.add(new Pair("ICA Session", new Boolean(true)));
     linkedlist.add(new Pair("Terminal Services", new Boolean(false)));
     linkedlist.add(new Pair("Terminal Services Session", new Boolean(true)));
     return linkedlist;
 }

 public String getHostname()
 {
     return getProperty(pCitrixServer);
 }

 public StringProperty getServerProperty()
 {
     return pCitrixServer;
 }

 public boolean remoteCommandLineAllowed()
 {
     return false;
 }

 public Array getPropertiesToPassBetweenPages(HTTPRequest httprequest)
 {
     Array array = new Array();
     array.add(pCitrixServer);
     return array;
 }

 public void onMonitorCreateFromPage(HTTPRequest httprequest)
 {
     String s = httprequest.getValue("uniqueID");
     String s1 = httprequest.getValue(pCitrixServer.getName());
     if(httprequest.hasValue(pCitrixServer.getName()))
     {
         setProperty(pCitrixServer, httprequest.getValue(pCitrixServer.getName()));
         if(s.length() > 0)
         {
             HashMap hashmap = BrowsableCache.getCache(s, true, false);
             if(hashmap != null)
             {
                 HashMap hashmap3 = (HashMap)hashmap.get("mProp");
                 Enumeration enumeration = hashmap3.keys();
                 do
                 {
                     if(!enumeration.hasMoreElements())
                     {
                         break;
                     }
                     String s2 = (String)enumeration.nextElement();
                     if(!s2.equals(pCitrixServer.getName()) || hashmap3.get(s2).equals(s1))
                     {
                         continue;
                     }
                     BrowsableCache.getCache(s, false, true);
                     HashMap hashmap1 = BrowsableCache.getCache(s, true, false);
                     hashmap3 = (HashMap)hashmap1.get("mProp");
                     break;
                 } while(true);
                 hashmap3.put(pCitrixServer.getName(), s1);
                 BrowsableCache.saveCache(s);
             }
         }
     } else
     if(s.length() > 0)
     {
         HashMap hashmap2 = BrowsableCache.getCache(s, false, false);
         if(hashmap2 != null)
         {
             HashMap hashmap4 = (HashMap)hashmap2.get("mProp");
             String s3;
             for(Enumeration enumeration1 = hashmap4.keys(); enumeration1.hasMoreElements(); setProperty(s3, (String)hashmap4.get(s3)))
             {
                 s3 = (String)enumeration1.nextElement();
             }

         }
     }
 }

 public boolean isUsingCountersCache()
 {
     return false;
 }

 static 
 {
     pCitrixServer = null;
     pCitrixServer = new ServerProperty("_server", "");
     pCitrixServer.setParameterOptions(true, 1, false);
     pCitrixServer.setDisplayText("Server", "the server to monitor");
     StringProperty astringproperty[] = {
         pCitrixServer
     };
     String s = (COM.dragonflow.StandardMonitor.CitrixMonitor.class).getName();
     addProperties(s, astringproperty);
     addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror", true));
     addClassElement(s, Rule.stringToClassifier("always\tgood"));
     setClassProperty(s, "description", "Monitors Citrix server performance metrics.");
     setClassProperty(s, "help", "CitrixMon.htm");
     setClassProperty(s, "title", "Citrix Server");
     setClassProperty(s, "class", "CitrixMonitor");
     setClassProperty(s, "target", "_serverName");
     setClassProperty(s, "topazName", "Citrix Server");
     setClassProperty(s, "classType", "application");
     if(!Platform.isWindows() || !DispatcherSitter.isDispatcherInstalled())
     {
         setClassProperty(s, "loadable", "false");
     }
 }
}
