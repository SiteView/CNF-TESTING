/*
 * 
 * Created on 2005-2-16 16:21:28
 *
 * PQVPrintMainHTML.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PQVPrintMainHTML</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;

import jgl.HashMap;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PQVPrintChooserHTML, PortalSiteView, MonitorGroup, AtomicMonitor,
// SiteViewObject, Portal, Monitor

public class PQVPrintMainHTML extends PQVPrintChooserHTML {

    public PQVPrintMainHTML(CGI cgi, HashMap hashmap, HashMap hashmap1) {
        super(cgi, hashmap, hashmap1);
    }

    void printItem(SiteViewObject siteviewobject, int i, boolean flag,
            boolean flag1) {
        String s = siteviewobject.getFullID();
        String s1 = getIndentHTML(i);
        String s2 = siteviewobject.getProperty("_name");
        if (s2.length() == 0) {
            s2 = siteviewobject.getProperty("_title");
        }
        outputStream.print("<TR><TD>");
        outputStream.print(s1);
        if (flag1) {
            if (flag) {
                outputStream
                        .print("<input type=image name=close"
                                + s
                                + " src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\" border=0>");
            } else {
                outputStream
                        .print("<input type=image name=open"
                                + s
                                + " src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\" border=0>");
            }
        }
        if (siteviewobject instanceof PortalSiteView) {
            printServer((PortalSiteView) siteviewobject);
        } else if (siteviewobject instanceof MonitorGroup) {
            printGroup((MonitorGroup) siteviewobject);
        } else if (siteviewobject instanceof AtomicMonitor) {
            printMonitor((AtomicMonitor) siteviewobject);
        } else {
            TextUtils.debugPrint("UNKNOWN KIND OF OBJECT " + siteviewobject);
        }
    }

    PortalSiteView findSiteView(SiteViewObject siteviewobject) {
        for (; siteviewobject != null; siteviewobject = siteviewobject
                .getOwner()) {
            if (siteviewobject instanceof PortalSiteView) {
                return (PortalSiteView) siteviewobject;
            }
        }

        return null;
    }

    void printServer(SiteViewObject siteviewobject) {
        outputStream.print("<B>"
                + siteviewobject.getProperty(PortalSiteView.pTitle)
                + "</B>&nbsp;&nbsp;");
        if (siteviewobject.getProperty(PortalSiteView.pReadOnly).length() == 0) {
            if (request.actionAllowed("_preference")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("generalPrefs", "")
                        + "&portalserver="
                        + Portal.getServerID(siteviewobject.getFullID())
                        + ":>Edit Preferences</A>&nbsp;&nbsp;");
            }
            if (request.actionAllowed("_groupEdit")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("group", "Add") + "&portalserver="
                        + Portal.getServerID(siteviewobject.getFullID())
                        + ":>Add Group</A>&nbsp;&nbsp;");
            }
            if (request.actionAllowed("_alertEdit")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("alert", "List") + "&portalserver="
                        + Portal.getServerID(siteviewobject.getFullID())
                        + ":>Alerts</A>&nbsp;&nbsp;");
            }
            if (request.actionAllowed("_tools")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("monitor", "Tools")
                        + "&portalserver="
                        + Portal.getServerID(siteviewobject.getFullID())
                        + ":>Diagnostic Tools</A>");
            }
        }
        outputStream.println("</TD></TR>");
    }

    void printGroup(MonitorGroup monitorgroup) {
        outputStream.print("<B>" + monitorgroup.getProperty(Monitor.pName)
                + "</B>&nbsp;&nbsp;");
        boolean flag = false;
        PortalSiteView portalsiteview = findSiteView(monitorgroup);
        if (portalsiteview != null) {
            flag = portalsiteview.getProperty(PortalSiteView.pReadOnly)
                    .length() > 0;
        }
        if (!flag && request.actionAllowed("_groupEdit")) {
            outputStream.print("<A HREF="
                    + page.getPageLink("monitor", "AddList") + "&group="
                    + monitorgroup.getFullID() + "&portalserver="
                    + Portal.getServerID(monitorgroup.getFullID())
                    + ":>Add Monitor</A>&nbsp;&nbsp;");
            outputStream.print("<A HREF=" + page.getPageLink("group", "Add")
                    + "&group=" + monitorgroup.getFullID() + "&parent="
                    + monitorgroup.getProperty(MonitorGroup.pID)
                    + "&portalserver="
                    + Portal.getServerID(monitorgroup.getFullID())
                    + ":>Add SubGroup</A>&nbsp;&nbsp;");
            outputStream.print("<A HREF=" + page.getPageLink("group", "Edit")
                    + "&group=" + monitorgroup.getFullID() + "&portalserver="
                    + Portal.getServerID(monitorgroup.getFullID())
                    + ":>Edit Group Properties</A>&nbsp;&nbsp;");
            outputStream.print("<A HREF=" + page.getPageLink("reorder", "")
                    + "&group=" + monitorgroup.getFullID() + "&portalserver="
                    + Portal.getServerID(monitorgroup.getFullID())
                    + ":>Reorder Monitors</A>&nbsp;&nbsp;");
            outputStream.print("<A HREF=" + page.getPageLink("group", "Delete")
                    + "&group=" + monitorgroup.getFullID() + "&portalserver="
                    + Portal.getServerID(monitorgroup.getFullID())
                    + ":>Delete</A>");
        }
        outputStream.println("</TD></TR>");
    }

    void printMonitor(AtomicMonitor atomicmonitor) {
        outputStream.print("<B>" + atomicmonitor.getProperty(Monitor.pName)
                + "</B>&nbsp;&nbsp;");
        MonitorGroup monitorgroup = (MonitorGroup) atomicmonitor.getParent();
        boolean flag = false;
        PortalSiteView portalsiteview = findSiteView(monitorgroup);
        if (portalsiteview != null) {
            flag = portalsiteview.getProperty(PortalSiteView.pReadOnly)
                    .length() > 0;
        }
        if (!flag) {
            if (request.actionAllowed("_monitorRefresh")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("monitor", "RefreshMonitor")
                        + "&refresh=true&group=" + monitorgroup.getFullID()
                        + "&id=" + atomicmonitor.getProperty(Monitor.pID)
                        + "&portalserver="
                        + Portal.getServerID(monitorgroup.getFullID())
                        + ":>Refresh&nbsp;&nbsp;");
            }
            if (request.actionAllowed("_monitorEdit")) {
                outputStream.print("<A HREF="
                        + page.getPageLink("monitor", "Edit") + "&group="
                        + monitorgroup.getFullID() + "&id="
                        + atomicmonitor.getProperty(Monitor.pID)
                        + "&portalserver="
                        + Portal.getServerID(monitorgroup.getFullID())
                        + ":>Edit&nbsp;&nbsp;");
                outputStream.print("<A HREF="
                        + page.getPageLink("monitor", "Delete") + "&group="
                        + monitorgroup.getFullID() + "&id="
                        + atomicmonitor.getProperty(Monitor.pID)
                        + "&portalserver="
                        + Portal.getServerID(monitorgroup.getFullID())
                        + ":>Delete");
            }
        }
        outputStream.println("</A></TD></TR>");
    }

    public static void main(String args[]) throws IOException {
    }
}
