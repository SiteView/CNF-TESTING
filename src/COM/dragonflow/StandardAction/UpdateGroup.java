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

import COM.dragonflow.Api.APIGroup;

public class UpdateGroup extends COM.dragonflow.SiteView.Action {

    public UpdateGroup(COM.dragonflow.SiteView.Monitor monitor) {
        runType = 1;
        setMonitor(monitor);
    }

    public boolean execute() {
        try {
            (new APIGroup()).refreshGroup(monitor.getProperty(COM.dragonflow.SiteView.MonitorGroup.pGroupID), false);
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "UpdateGroup: failed to refresh group: " + COM.dragonflow.SiteView.MonitorGroup.pGroupID + ", exception: " + exception.getMessage());
            return false;
        }
        return true;
    }

    public String toString() {
        String s = "update group ";
        if (monitor == null) {
            s = s + "<unknown>";
        } else {
            String s1 = "";
            String s2 = monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pGroupID);
            if (s2 != null) {
                s1 = s1 + s2 + ":" + monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pID) + "  ";
            }
            s1 = s1 + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName);
            s = s + s1;
        }
        return s;
    }
}
