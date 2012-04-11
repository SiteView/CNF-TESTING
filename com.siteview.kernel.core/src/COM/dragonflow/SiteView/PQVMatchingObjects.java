/*
 * 
 * Created on 2005-2-16 16:20:33
 *
 * PQVMatchingObjects.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PQVMatchingObjects</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;

import jgl.Array;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalQueryVisitor, AtomicMonitor, MonitorGroup, PortalSiteView,
// PortalQuery, SiteViewObject, Monitor

public class PQVMatchingObjects extends PortalQueryVisitor {

    Array matchingObjects;

    public PQVMatchingObjects(Array array) {
        matchingObjects = null;
        matchingObjects = array;
    }

    private void addObjectToMatches(SiteViewObject siteviewobject) {
        if (matchingObjects != null) {
            if (siteviewobject instanceof AtomicMonitor) {
                if (portalQuery.findingMonitors()) {
                    matchingObjects.add(siteviewobject);
                }
            } else if (siteviewobject instanceof MonitorGroup) {
                if (portalQuery.findingGroups()) {
                    matchingObjects.add(siteviewobject);
                }
            } else if ((siteviewobject instanceof PortalSiteView)
                    && portalQuery.findingServers()) {
                matchingObjects.add(siteviewobject);
            }
        }
    }

    boolean groupPre(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        addObjectToMatches(monitorgroup);
        return portalQuery.findingGroups() || portalQuery.findingMonitors();
    }

    boolean monitorPre(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        addObjectToMatches(monitor);
        return portalQuery.findingMonitors();
    }

    public static void main(String args[]) throws IOException {
    }
}
