/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.util.Enumeration;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class J2EERepostMetricsPage extends com.dragonflow.Page.CGI {

    public J2EERepostMetricsPage() {
    }

    public void printBody() throws java.lang.Exception {
        printBodyHeader("Repost Metrics List");
        String s = request.getValue("monitor");
        if (request.getValue("action").equals("go")) {
            com.dragonflow.SiteView.Monitor monitor = findMonitor(s);
            com.dragonflow.StatefulMonitor.StatefulConnsMgr statefulconnsmgr = com.dragonflow.StatefulMonitor.StatefulConnsMgr
                    .getManager(com.dragonflow.StatefulMonitor.J2EEConnection.class);
            com.dragonflow.StatefulMonitor.J2EEConnection j2eeconnection = (com.dragonflow.StatefulMonitor.J2EEConnection) statefulconnsmgr
                    .getConnection(
                            (com.dragonflow.StatefulMonitor.StatefulConnectionUser) monitor,
                            null);
            j2eeconnection.postMetricList();
            java.lang.Thread.currentThread();
            java.lang.Thread.sleep(2000L);
            outputStream.println("<H2>Done</H2><P>");
            com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) monitor
                    .getParent();
            String s1 = monitor.getProperty("_id");
            String s2 = monitor.getProperty("groupID");
            printRefreshPage(getPageLink("monitor", "RefreshMonitor")
                    + "&refresh=true&group="
                    + com.dragonflow.HTTP.HTTPRequest.encodeString(s2) + "&id="
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
     * 
     * 
     * @param s
     * @return
     */
    private com.dragonflow.SiteView.Monitor findMonitor(String s) {
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        Enumeration enumeration = siteviewgroup.getMonitors();
        com.dragonflow.SiteView.Monitor monitor;
        while (true) {
            if (enumeration.hasMoreElements()) {
                com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) enumeration
                        .nextElement();
                Enumeration enumeration1 = monitorgroup.getMonitors();
                while (enumeration1.hasMoreElements()) {
                    monitor = (com.dragonflow.SiteView.Monitor) enumeration1
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
