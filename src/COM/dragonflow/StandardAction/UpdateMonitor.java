/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class UpdateMonitor extends COM.dragonflow.SiteView.Action {

    public UpdateMonitor(COM.dragonflow.SiteView.Monitor monitor) {
        runType = 1;
        setMonitor(monitor);
    }

    public boolean execute() {
        COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) monitor.getParent();
        if (monitorgroup.groupSchedulerActive(false)) {
            return true;
        } else {
            ((COM.dragonflow.SiteView.AtomicMonitor) monitor).runUpdate(false);
            return true;
        }
    }

    static String getFullGroupName(COM.dragonflow.SiteView.Monitor monitor) {
        String s = "";
        String s1 = monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pGroupID);
        if (s1 != null) {
            s = s + s1 + ":" + monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pID) + "  ";
        }
        s = s + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName);
        return s;
    }

    public String toString() {
        String s = "update monitor ";
        if (monitor == null) {
            s = s + "<unknown>";
        } else {
            s = s + COM.dragonflow.StandardAction.UpdateMonitor.getFullGroupName(monitor);
        }
        return s;
    }

    static {
        COM.dragonflow.Properties.StringProperty astringproperty[] = new COM.dragonflow.Properties.StringProperty[0];
        COM.dragonflow.StandardAction.UpdateMonitor.addProperties("COM.dragonflow.StandardAction.UpdateMonitor", astringproperty);
        COM.dragonflow.StandardAction.UpdateMonitor.setClassProperty("COM.dragonflow.StandardAction.UpdateMonitor", "loadable", "false");
    }
}
