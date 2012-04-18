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

import java.io.FileOutputStream;
import java.util.Date;
import java.util.Enumeration;

import com.dragonflow.SiteView.FindRunningMonitors;
import com.dragonflow.SiteView.ProgressLogger;

import com.recursionsw.jgl.Array;



// Referenced classes of package com.dragonflow.Page:
// CGI

public class GenerateProgress extends com.dragonflow.SiteView.Action {

    java.io.File file;

    com.dragonflow.HTTP.HTTPRequest savedRequest;

    java.util.Date startTime;

    boolean showDebug;

    long lastHeartbeat;

    public GenerateProgress(java.io.File file1,
            com.dragonflow.HTTP.HTTPRequest httprequest) {
        file = null;
        savedRequest = null;
        startTime = null;
        showDebug = false;
        lastHeartbeat = 0L;
        showDebug = java.lang.System.getProperty("CommandLine.debug") != null;
        file = file1;
        savedRequest = httprequest;
        runType = 1;
        startTime = com.dragonflow.SiteView.Platform.makeDate();
    }

    public boolean execute() {
        if (showDebug) {
            com.dragonflow.Log.LogManager.log("RunMonitor",
                    "Generate Progress file: " + file.getAbsolutePath());
        }
        long l = com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.monitorScheduler
                .getHeartbeat();
        if (l != -1L) {
            if (showDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor",
                        "Generate Progress heartbeat " + l + "("
                                + lastHeartbeat + ")" + ": "
                                + file.getAbsolutePath());
            }
            if (l <= lastHeartbeat) {
                if (showDebug) {
                    com.dragonflow.Log.LogManager.log("RunMonitor",
                            "Generate Progress heartbeat too soon: "
                                    + file.getAbsolutePath());
                }
                return true;
            }
            lastHeartbeat = l;
        }
        java.io.FileOutputStream fileoutputstream = null;
        java.io.PrintWriter printwriter = null;
        try {
            if (showDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor",
                        "Generate Progress opening: " + file.getAbsolutePath());
            }
            fileoutputstream = new FileOutputStream(file);
            printwriter = com.dragonflow.Utils.FileUtils
                    .MakeOutputWriter(fileoutputstream);
            if (showDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor",
                        "Generate Progress opened: " + file.getAbsolutePath());
            }
            generate(printwriter, savedRequest);
            if (showDebug) {
                com.dragonflow.Log.LogManager.log("RunMonitor",
                        "Generate Progress generated: "
                                + file.getAbsolutePath());
            }
        } catch (java.io.IOException ioexception) {
            com.dragonflow.Log.LogManager.log("RunMonitor",
                    "Could not generate Progress file: "
                            + file.getAbsolutePath());
        } finally {
            try {
                if (showDebug) {
                    com.dragonflow.Log.LogManager.log("RunMonitor",
                            "Generate Progress closing: "
                                    + file.getAbsolutePath());
                }
                if (printwriter != null) {
                    printwriter.close();
                }
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
            } catch (java.io.IOException ioexception1) {
                com.dragonflow.Log.LogManager.log("RunMonitor",
                        "Could not close Progress file: "
                                + file.getAbsolutePath());
            }
        }
        if (showDebug) {
            com.dragonflow.Log.LogManager.log("RunMonitor",
                    "Generate Progress done: " + file.getAbsolutePath());
        }
        return true;
    }

    public String toString() {
        return "generate page";
    }

    public com.dragonflow.Page.CGI.menus getNavItems(
            com.dragonflow.HTTP.HTTPRequest httprequest) {
        com.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page",
                    "Browse Monitors"));
        }
        if (httprequest.actionAllowed("_tools")) {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools",
                    "operation", "Use monitor diagnostic tools"));
        }
        if (httprequest.actionAllowed("_progress")) {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url",
                    "View current monitoring progress"));
        }
        if (httprequest.actionAllowed("_browse")) {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "",
                    "page", "View current monitor settings"));
        }
        if (httprequest.actionAllowed("_reportEdit")) {
            menus1.add(new CGI.menuItems("Add Report", "report", "add",
                    "operation", "Add a new automated report"));
        }
        if (httprequest.actionAllowed("_reportAdhoc")) {
            menus1.add(new CGI.menuItems("Quick Report", "report", "adhoc",
                    "operation", "Create a new adhoc report"));
        }
        return menus1;
    }

    /**
     * 
     * 
     * @param printwriter
     * @param monitor
     * @param i
     */
    private void printGroupEntry(java.io.PrintWriter printwriter,
            com.dragonflow.SiteView.Monitor monitor, int i) {
        String s = monitor
                .getProperty(com.dragonflow.SiteView.Monitor.pStateString);
        if (s.length() == 0) {
            s = " ";
        }
        if (s.length() > 100) {
            s = s.substring(0, 100) + "...";
        }
        s = s.replace('\t', ' ');
        s = s.replace('\n', ' ');
        if (monitor instanceof com.dragonflow.SiteView.MonitorGroup) {
            if (!(monitor instanceof com.dragonflow.SiteView.SiteViewGroup)) {
                String s1 = monitor
                        .getProperty(com.dragonflow.SiteView.Monitor.pCategory);
                String s3 = monitor
                        .getProperty(com.dragonflow.SiteView.Monitor.pName);
                String s5 = monitor.getSetting("_webserverAddress");
                if (s5.length() == 0) {
                    s5 = "localhost";
                }
                printwriter
                        .print(s5
                                + "\t"
                                + monitor
                                        .getProperty(com.dragonflow.SiteView.SiteViewObject.pID)
                                + "\t" + s3 + "\t" + s1 + "\t" + s + "\t" + i
                                + "\n");
            }
        } else {
            String s2 = monitor
                    .getProperty(com.dragonflow.SiteView.Monitor.pCategory);
            String s4 = monitor
                    .getProperty(com.dragonflow.SiteView.Monitor.pName);
            if (monitor.isDisabled()) {
                s2 = "nodata";
                s4 = "(disabled) " + s4;
            }
            printwriter
                    .print(monitor
                            .getProperty(com.dragonflow.SiteView.SiteViewGroup.pOwnerID)
                            + "\t"
                            + monitor
                                    .getProperty(com.dragonflow.SiteView.Monitor.pID)
                            + "\t" + s4 + "\t" + s2 + "\t" + s + "\n");
        }
        Enumeration enumeration = monitor.getMonitors();
        while (enumeration.hasMoreElements()) {
            com.dragonflow.SiteView.Monitor monitor1 = (com.dragonflow.SiteView.Monitor) enumeration
                    .nextElement();
            if (!(monitor1 instanceof com.dragonflow.SiteView.SubGroup)) {
                printGroupEntry(printwriter, monitor1, i);
            }
        }

        enumeration = monitor.getMonitors();
        while (enumeration.hasMoreElements()) {
            com.dragonflow.SiteView.Monitor monitor2 = (com.dragonflow.SiteView.Monitor) enumeration
                    .nextElement();
            if (monitor2 instanceof com.dragonflow.SiteView.SubGroup) {
                com.dragonflow.SiteView.MonitorGroup monitorgroup = ((com.dragonflow.SiteView.SubGroup) monitor2)
                        .lookupGroup();
                if (monitorgroup != null) {
                    printGroupEntry(printwriter,
                            ((com.dragonflow.SiteView.Monitor) (monitorgroup)),
                            i + 1);
                }
            }
        }
    }

    /**
     * 
     * 
     * @param printwriter
     * @param httprequest
     */
    public void generate(java.io.PrintWriter printwriter,
            com.dragonflow.HTTP.HTTPRequest httprequest) {
        if (!httprequest.actionAllowed("_progress")) {
            printwriter.println("access permission error");
            return;
        }
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        int i = siteviewgroup.getSettingAsLong("_progressRefreshRate", 20);
        String s = com.dragonflow.Utils.TextUtils.prettyDate();
        com.dragonflow.Page.CGI.printBodyHeader(printwriter,
                com.dragonflow.SiteView.Platform.productName + " Status",
                siteviewgroup.refreshTag(i, "Progress.html"));
        printwriter.print("<!--HEARTBEATS\n");
        printwriter
                .print("progress\t"
                        + com.dragonflow.Utils.TextUtils
                                .dateToString(com.dragonflow.SiteView.ProgressLogger.cProgressLogger.lastProgressTime)
                        + "\n");
        printwriter.print("monitorScheduler\t"
                + com.dragonflow.Utils.TextUtils
                        .dateToString(siteviewgroup.monitorScheduler
                                .getHeartbeat()) + "\n");
        printwriter.print("reportScheduler\t"
                + com.dragonflow.Utils.TextUtils
                        .dateToString(siteviewgroup.reportScheduler
                                .getHeartbeat()) + "\n");
        printwriter.print("maintenanceScheduler\t"
                + com.dragonflow.Utils.TextUtils
                        .dateToString(siteviewgroup.maintenanceScheduler
                                .getHeartbeat()) + "\n");
        printwriter.print("ENDHEARTBEATS-->\n");
        if (httprequest.getValue("hideState").length() == 0) {
            printwriter.print("<!--CURRENTSTATE\n");
            if (!httprequest.isStandardAccount()) {
                Array array = com.dragonflow.Page.CGI
                        .getAllowedGroupIDsForAccount(httprequest);
                Enumeration enumeration = (Enumeration) array.iterator();
                while (enumeration.hasMoreElements()) {
                    String s2 = (String) enumeration
                            .nextElement();
                    com.dragonflow.SiteView.MonitorGroup monitorgroup1 = (com.dragonflow.SiteView.MonitorGroup) siteviewgroup
                            .getElement(s2);
                    if (monitorgroup1 != null
                            && monitorgroup1
                                    .getProperty(
                                            com.dragonflow.SiteView.MonitorGroup.pParent)
                                    .equals("")) {
                        printGroupEntry(printwriter, monitorgroup1, 0);
                    }
                }
            } else {
                Array array1 = siteviewgroup
                        .getTopLevelMonitors(httprequest);
                com.dragonflow.SiteView.MonitorGroup monitorgroup;
                for (Enumeration enumeration1 =  (Enumeration) array1.iterator(); enumeration1
                        .hasMoreElements(); printGroupEntry(printwriter,
                        monitorgroup, 0)) {
                    monitorgroup = (com.dragonflow.SiteView.MonitorGroup) enumeration1
                            .nextElement();
                }

            }
            printwriter.print("ENDCURRENTSTATE-->\n");
        }
        String s1 = "Progress.htm";
        if (com.dragonflow.SiteView.Platform.isPortal()) {
            s1 = "ProgressReport.htm";
        }
        com.dragonflow.Page.CGI.menus menus1 = getNavItems(httprequest);
        com.dragonflow.Page.CGI.printButtonBar(printwriter, s1, "", httprequest,
                com.dragonflow.SiteView.MasterConfig.getMasterConfig(), menus1,
                false);
        printwriter.print("<H2>" + com.dragonflow.SiteView.Platform.productName
                + " Progress Report</H2><P>");
        boolean flag = httprequest.getValue("hideRecent").length() == 0
                && httprequest.isStandardAccount();
        boolean flag1 = httprequest.getValue("hideStats").length() == 0
                && httprequest.isStandardAccount();
        boolean flag2 = false;
        if (com.dragonflow.SiteView.AtomicMonitor.isSuspended()) {
            flag = false;
            flag1 = false;
        }
        if (com.dragonflow.SiteView.Platform.isPortal()) {
            flag2 = true;
            flag = false;
            flag1 = false;
        }
        if (httprequest.getValue("hidePortal").length() > 0) {
            flag2 = false;
        }
        if (flag1) {
            float f = com.dragonflow.SiteView.AtomicMonitor.monitorStats
                    .getCountPerTimePeriod();
            float f1 = com.dragonflow.SiteView.AtomicMonitor.monitorStats
                    .getMaximumCountPerTimePeriod();
            long l = com.dragonflow.SiteView.AtomicMonitor.monitorStats
                    .getMaximumCountPerTimePeriodTime();
            int k = com.dragonflow.SiteView.MonitorQueue.getRunningCount();
            int i1 = (int) com.dragonflow.SiteView.AtomicMonitor.monitorStats
                    .getMaximum();
            long l1 = com.dragonflow.SiteView.AtomicMonitor.monitorStats
                    .getMaximumTime();
            int k1 = com.dragonflow.SiteView.MonitorQueue.readyMonitors.size();
            int i2 = com.dragonflow.SiteView.MonitorQueue.maxReadyMonitors;
            long l4 = com.dragonflow.SiteView.MonitorQueue.maxReadyMonitorsTime;
            printwriter
                    .println("<TABLE BORDER=1 cellspacing=0><CAPTION>Monitoring Load</CAPTION>\n<TR><TH></TH><TH>Monitors Run Per Minute</TH><TH>Monitors Running</TH><TH>Monitors Waiting</TH></TR>\n<TR><TD>Current</TD><TD>"
                            + com.dragonflow.Utils.TextUtils.floatToString(f, 2)
                            + "</TD><TD>"
                            + k
                            + "</TD><TD>"
                            + k1
                            + "</TD></TR>\n"
                            + "<TR><TD>Maximum</TD><TD>"
                            + com.dragonflow.Utils.TextUtils
                                    .floatToString(f1, 2)
                            + " at "
                            + com.dragonflow.Utils.TextUtils.prettyDate(l)
                            + "</TD><TD>"
                            + i1
                            + " at "
                            + com.dragonflow.Utils.TextUtils.prettyDate(l1)
                            + "</TD><TD>"
                            + i2
                            + " at "
                            + com.dragonflow.Utils.TextUtils.prettyDate(l4)
                            + "</TD></TR>\n</TABLE>"
                            + "<font size=-1>Maximums are since last startup at "
                            + com.dragonflow.Utils.TextUtils
                                    .prettyDate(startTime)
                            + "</font>\n"
                            + "<P>");
        }
        if (flag) {
            String s3 = "";
            if (siteviewgroup.isStartingUp()) {
                printwriter
                        .print("<CENTER><TABLE width=200 border=1 cellspacing=0><CAPTION>Recent Monitors</CAPTION><TR><TD ALIGN=CENTER>SiteView is starting up</TD></TR></TABLE></CENTER>\n");
                s3 = "starting up...";
            } else {
                String s4 = "Group";
                printwriter
                        .print("<TABLE width=100% border=1 cellspacing=0><CAPTION>Recent Monitors</CAPTION><TR><TH>Date</TH><TH>"
                                + s4
                                + "</TH><TH>Monitor</TH><TH>Status</TH></TR>\n");
                com.dragonflow.SiteView.FindRunningMonitors findrunningmonitors = new FindRunningMonitors();
                siteviewgroup.acceptVisitor(findrunningmonitors);
                Enumeration enumeration3 = findrunningmonitors
                        .getResultsElements();
                if (enumeration3.hasMoreElements()) {
                    while (enumeration3.hasMoreElements()) {
                        com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) enumeration3
                                .nextElement();
                        String s7 = monitor
                                .getProperty(com.dragonflow.SiteView.Monitor.pName);
                        String s10 = monitor.currentStatus;
                        long l2 = monitor
                                .getPropertyAsLong(com.dragonflow.SiteView.Monitor.pLastUpdate);
                        if (l2 > 0L) {
                            long l3 = com.dragonflow.Utils.TextUtils
                                    .toLong(httprequest
                                            .getUserSetting("_timeOffset")) * 1000L;
                            String s12 = com.dragonflow.Utils.TextUtils
                                    .prettyDate(new Date(l2 + l3));
                            s10 = s10 + " (previous update at " + s12 + ")";
                        }
                        String s11 = monitor
                                .getProperty(com.dragonflow.SiteView.SiteViewObject.pOwnerID);
                        com.dragonflow.SiteView.Monitor monitor1 = (com.dragonflow.SiteView.Monitor) siteviewgroup
                                .getElement(com.dragonflow.Utils.I18N
                                        .toDefaultEncoding(s11));
                        if (monitor1 != null) {
                            s11 = monitor1
                                    .getProperty(com.dragonflow.SiteView.Monitor.pName);
                        }
                        printwriter.print("<TR><TD><B>" + s + "</B></TD>"
                                + "<TD><B>" + s11 + "</B></TD>" + "<TD><B>"
                                + s7 + "</B></TD>" + "<TD>" + s10
                                + "</TD></TR>\n");
                        if (s3.length() == 0) {
                            s3 = "checking " + s7 + "...";
                        }
                    }
                } else {
                    String s5 = siteviewgroup.monitorScheduler
                            .getNextActionDescription();
                    String s8 = (siteviewgroup.monitorScheduler
                            .getNextTime() - com.dragonflow.SiteView.Platform
                            .timeMillis())
                            / 1000L + " seconds";
                    if (s5.equals("nothing scheduled")) {
                        printwriter.print("<TR><TD>"
                                + com.dragonflow.Utils.TextUtils.prettyDate()
                                + "</TD>" + "<TD></TD><TD><B>" + s5
                                + "</B></TD>" + "</TR>\n");
                        s3 = s5;
                    } else {
                        printwriter.print("<TR><TD>"
                                + com.dragonflow.Utils.TextUtils.prettyDate()
                                + "</TD>" + "<TD></TD><TD><B>will " + s5
                                + "</B></TD>" + "<TD>in " + s8 + "</TD>"
                                + "</TR>\n");
                        s3 = "will " + s5 + " in " + s8;
                    }
                }
                printwriter.print("<TR><TD></TD></TR><TR><TD></TD></TR>");
                for (Enumeration enumeration4 = (Enumeration) com.dragonflow.SiteView.ProgressLogger.cProgressLogger.history
                        .iterator(); enumeration4.hasMoreElements(); printwriter
                        .print("</TR>\n")) {
                    String s9 = (String) enumeration4
                            .nextElement();
                    String as[] = com.dragonflow.Utils.TextUtils
                            .split(s9, "\t");
                    printwriter.print("<TR>");
                    for (int j1 = 0; j1 < as.length; j1++) {
                        printwriter.print("<TD>" + as[j1] + "</TD>");
                    }

                }

                printwriter.print("</TABLE><P>");
            }
            if (siteviewgroup != null) {
                siteviewgroup.setProgressSummary(s3);
            }
        }
        if (flag2) {
            int j = com.dragonflow.SiteView.LogPuller.getThreadCount();
            if (j > 1) {
                printwriter
                        .println("<TABLE BORDER=1 cellspacing=0><CAPTION>Portal Log Collectors, "
                                + j
                                + " threads</CAPTION>\n"
                                + "<TR><TH>Last Update<TH>Server<TH>Last Status</TR>");
                Array array2 = com.dragonflow.SiteView.LogPuller
                        .getPortals();
                com.dragonflow.SiteView.PortalSiteView portalsiteview;
                Enumeration enumeration2 =  (Enumeration) array2.iterator();
                while (enumeration2.hasMoreElements()) {
                    portalsiteview = (com.dragonflow.SiteView.PortalSiteView) enumeration2
                            .nextElement();
                    String s6 = "";
                    if (portalsiteview.logCollectorTimestamp != 0L) {
                        s6 = com.dragonflow.Utils.TextUtils
                                .prettyDate(portalsiteview.logCollectorTimestamp);
                    }
                    com.dragonflow.SiteView.PortalSiteView _tmp = portalsiteview;
                    printwriter
                            .println("<TR><TD>"
                                    + s6
                                    + "<TD>"
                                    + portalsiteview
                                            .getProperty(com.dragonflow.SiteView.PortalSiteView.pTitle)
                                    + "<TD>"
                                    + portalsiteview.logCollectorStatus
                                    + "</TD></TR>");
                }

                printwriter.println("</TABLE>");
            }
        }
        printwriter.print("<P><CENTER> <font size=-1>(page will refresh every "
                + i + " seconds)</font></center><BR>\n");
        com.dragonflow.Page.CGI.printFooter(printwriter, httprequest);
        printwriter.println("</HTML>");
        printwriter.flush();
    }
}
