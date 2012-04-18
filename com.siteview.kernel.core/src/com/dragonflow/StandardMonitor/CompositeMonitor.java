/*
 * 
 * Created on 2005-3-7 0:53:49
 *
 * CompositeMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>CompositeMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Enumeration;

import com.dragonflow.Log.LogManager;
import com.dragonflow.Properties.BooleanProperty;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.CompositeBase;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.MonitorGroup;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteView.SiteViewGroup;

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
     addProperties("com.dragonflow.StandardMonitor.CompositeMonitor", astringproperty);
     addClassElement("com.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInError > 0\terror", true));
     addClassElement("com.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInError == 'n/a'\terror"));
     addClassElement("com.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("itemsInWarning > 0\twarning", true));
     addClassElement("com.dragonflow.StandardMonitor.CompositeMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "description", "Monitors the statuses of a set of groups and/or monitors.");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "help", "CompositeMon.htm");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "title", "Composite");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "class", "CompositeMonitor");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "classType", "advanced");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "topazName", "Composite");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "topazType", "System Resources");
     setClassProperty("com.dragonflow.StandardMonitor.CompositeMonitor", "target", "_monitor");
 }
}