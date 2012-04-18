/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class UpdateMonitor extends com.dragonflow.SiteView.Action {

    public UpdateMonitor(com.dragonflow.SiteView.Monitor monitor) {
        runType = 1;
        setMonitor(monitor);
    }

    public boolean execute() {
        com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) monitor.getParent();
        if (monitorgroup.groupSchedulerActive(false)) {
            return true;
        } else {
            ((com.dragonflow.SiteView.AtomicMonitor) monitor).runUpdate(false);
            return true;
        }
    }

    static String getFullGroupName(com.dragonflow.SiteView.Monitor monitor) {
        String s = "";
        String s1 = monitor.getProperty(com.dragonflow.SiteView.SiteViewObject.pGroupID);
        if (s1 != null) {
            s = s + s1 + ":" + monitor.getProperty(com.dragonflow.SiteView.SiteViewObject.pID) + "  ";
        }
        s = s + monitor.getProperty(com.dragonflow.SiteView.Monitor.pName);
        return s;
    }

    public String toString() {
        String s = "update monitor ";
        if (monitor == null) {
            s = s + "<unknown>";
        } else {
            s = s + com.dragonflow.StandardAction.UpdateMonitor.getFullGroupName(monitor);
        }
        return s;
    }

    static {
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[0];
        com.dragonflow.StandardAction.UpdateMonitor.addProperties("com.dragonflow.StandardAction.UpdateMonitor", astringproperty);
        com.dragonflow.StandardAction.UpdateMonitor.setClassProperty("com.dragonflow.StandardAction.UpdateMonitor", "loadable", "false");
    }
}
