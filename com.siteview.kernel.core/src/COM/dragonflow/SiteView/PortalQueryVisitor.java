/*
 * 
 * Created on 2005-2-16 16:19:04
 *
 * PortalQueryVisitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PortalQueryVisitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalQuery, PortalSiteView, MonitorGroup, Monitor,
// Rule

public abstract class PortalQueryVisitor {

    PortalQuery portalQuery;

    HTTPRequest request;

    public PortalQueryVisitor() {
        portalQuery = null;
        request = null;
    }

    void initialize(HashMap hashmap, PortalQuery portalquery) {
        portalQuery = portalquery;
        request = portalQuery.request;
    }

    boolean enterprisePre() {
        return true;
    }

    void enterprisePost() {
    }

    boolean siteviewPre(PortalSiteView portalsiteview) {
        return true;
    }

    void siteviewPost(PortalSiteView portalsiteview) {
    }

    boolean groupPre(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        return true;
    }

    void groupPost(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
    }

    boolean monitorPre(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        return true;
    }

    void monitorPost(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
    }

    boolean alertPre(Rule rule, Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        return true;
    }

    void alertPost(Rule rule, Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
    }

    public static void main(String args[]) throws IOException {
    }
}
