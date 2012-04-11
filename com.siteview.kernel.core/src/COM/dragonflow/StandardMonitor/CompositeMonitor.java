/*
 * 
 * Created on 2005-3-7 0:53:49
 *
 * CompositeMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>CompositeMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Enumeration;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.CompositeBase;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;

public class CompositeMonitor extends CompositeBase
{

 static StringProperty pDeepCheck;
 static StringProperty pRunMonitors;
 static StringProperty pDelay;

 public CompositeMonitor()
 {
 }

 public String getHostname()
 {
     return "CompositeHost";
 }

 protected boolean update()
 {
     int ai[] = initializeStats();
     String as[] = initializeNameList();
     Enumeration enumeration = getMultipleValues(pItems);
     SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
     boolean flag = getPropertyAsBoolean(pDeepCheck);
     long l = getPropertyAsLong(pDelay) * 1000L;
     if(getPropertyAsBoolean(pRunMonitors))
     {
         flag = true;
         checkSequentially(ai, as, false, l, "");
     } else
     {
         do
         {
             if(!enumeration.hasMoreElements())
             {
                 break;
             }
             String s = (String)enumeration.nextElement();
             Monitor monitor = (Monitor)siteviewgroup.getElement(s.replace(' ', '/'));
             if(monitor != this)
             {
                 if(monitor != null)
                 {
                     if((monitor instanceof MonitorGroup) && flag)
                     {
                         checkGroup(ai, as, (MonitorGroup)monitor);
                     } else
                     {
                         updateStats(ai, as, monitor);
                     }
                 } else
                 {
                     LogManager.log("Error", "Could not get monitor " + s + " in Composite Monitor " + getProperty(pName));
                 }
             }
         } while(true);
     }
     updateProperties(ai, as, getPropertyAsBoolean(pRunMonitors));
     return true;
 }

 public String defaultTitle()
 {
     String s = super.defaultTitle();
     return "Composite: " + s;
 }

 static 
 {
     pRunMonitors = new BooleanProperty("_checkSequentially", "");
     pRunMonitors.setDisplayText("Run Monitors", "Run each monitor before checking.");
     pRunMonitors.setParameterOptions(true, 2, true);
     pDelay = new NumericProperty("_delay", "0");
     pDelay.setDisplayText("Monitor Delay", "If running each monitor, delay in seconds between monitors");
     pDelay.setParameterOptions(true, 5, true);
     pDeepCheck = new BooleanProperty("_deepCheck", "");
     pDeepCheck.setDisplayText("Check All Monitors in Group(s)", "By default, a group is counted as a single item when checking status.  If this box is checked, all of the monitors in selected groups (and their subgroups) are checked and counted towards the totals.");
     pDeepCheck.setParameterOptions(true, 6, true);
     StringProperty astringproperty[] = {
         pDeepCheck, pRunMonitors, pDelay
     };
     addProperties("COM.dragonflow.StandardMonitor.CompositeMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInError > 0\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInError == 'n/a'\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInWarning > 0\twarning", true));
     addClassElement("COM.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "description", "Monitors the statuses of a set of groups and/or monitors.");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "help", "CompositeMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "title", "Composite");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "class", "CompositeMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "classType", "advanced");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "topazName", "Composite");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "topazType", "System Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.CompositeMonitor", "target", "_monitor");
 }
}