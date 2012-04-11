/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.util.Enumeration;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class J2EERepostMetricsPage extends COM.dragonflow.Page.CGI {

    public J2EERepostMetricsPage() {
    }

    public void printBody() throws java.lang.Exception {
        printBodyHeader("Repost Metrics List");
        String s = request.getValue("monitor");
        if (request.getValue("action").equals("go")) {
            COM.dragonflow.SiteView.Monitor monitor = findMonitor(s);
            COM.dragonflow.StatefulMonitor.StatefulConnsMgr statefulconnsmgr = COM.dragonflow.StatefulMonitor.StatefulConnsMgr
                    .getManager(COM.dragonflow.StatefulMonitor.J2EEConnection.class);
            COM.dragonflow.StatefulMonitor.J2EEConnection j2eeconnection = (COM.dragonflow.StatefulMonitor.J2EEConnection) statefulconnsmgr
                    .getConnection(
                            (COM.dragonflow.StatefulMonitor.StatefulConnectionUser) monitor,
                            null);
            j2eeconnection.postMetricList();
            java.lang.Thread.currentThread();
            java.lang.Thread.sleep(2000L);
            outputStream.println("<H2>Done</H2><P>");
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) monitor
                    .getParent();
            String s1 = monitor.getProperty("_id");
            String s2 = monitor.getProperty("groupID");
            printRefreshPage(getPageLink("monitor", "RefreshMonitor")
                    + "&refresh=true&group="
                    + COM.dragonflow.HTTP.HTTPRequest.encodeString(s2) + "&id="
                    + s1, 1);
        } else {
            outputStream
                    .println("<H2>Repost Metrics</H2><P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=GET>Reposting Metrics will correct a probe that has gone 'out of sync'.<p><input type=hidden name=page value=\"J2EERepostMetrics\"><input type=hidden name=action value=\"go\"><input type=hidden name=monitor value=\""
                            + s
                            + "\">"
                            + "<input type=submit value=\"Repost Metrics\">"
                            + "</FORM>");
        }
        printFooter(outputStream);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private COM.dragonflow.SiteView.Monitor findMonitor(String s) {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        Enumeration enumeration = siteviewgroup.getMonitors();
        COM.dragonflow.SiteView.Monitor monitor;
        while (true) {
            if (enumeration.hasMoreElements()) {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) enumeration
                        .nextElement();
                Enumeration enumeration1 = monitorgroup.getMonitors();
                while (enumeration1.hasMoreElements()) {
                    monitor = (COM.dragonflow.SiteView.Monitor) enumeration1
                            .nextElement();
                if (monitor.getFullID().equalsIgnoreCase(s)) {
                break;
                }
                return monitor;
            }
            } else {
                return null;
            }
        } 
        
    }
}
