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

import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class prefsPage extends com.dragonflow.Page.CGI {

//    private static String topazShortName = com.dragonflow.SiteView.TopazInfo
//            .getTopazNameShort();
//
//    private static String topazLongName = com.dragonflow.SiteView.TopazInfo
//            .getTopazName();

    public prefsPage() {
    }

    public void printBody() throws java.lang.Exception {
    }

    void printPreferencesSaved(String s, String s1, int i) {
        printRefreshHeader("", s, i);
        outputStream.println(s1 + "</BODY>\n");
    }

    com.dragonflow.Page.CGI.menus getSecondNavItems(
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
        return menus1;
    }

    void printPrefsBar(String s) {
        if (request.getPermission("_link", "PrefsButtonBar").equals("hidden")) {
            return;
        }
        com.dragonflow.SiteView.User user = request.getUser();
        String s1 = user.getProperty("_buttonBar");
        if (s1.length() > 0) {
            return;
        }
        outputStream.println("<CENTER>");
        if (s.equals("General")) {
            outputStream.println("<B>General</B>");
        } else {
            outputStream.println("<a href=" + getPageLink("generalPrefs", "")
                    + " title=\"License, Port, Gauges, etc.\">General</a>");
        }
        outputStream.print(" | ");
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig
                .getMasterConfig();
        if (com.dragonflow.Utils.TextUtils.getValue(hashmap,
                "_configJdbcShowDDCLink").length() > 0) {
            if (s.equals("DDC")) {
                outputStream.println("<B>DDC</B>");
            } else {
                outputStream
                        .println("<a href="
                                + getPageLink("jdbcConfigPrefs", "")
                                + " title=\"Database Driven Configuration: Control SiteView groups and monitors via database\">DDC</a>");
            }
            outputStream.print(" | ");
        }
        if (s.equals("Dynamic Update")) {
            outputStream.println("<B>Dynamic Update</B>");
        } else {
            outputStream
                    .println("<a href="
                            + getPageLink("vMachine", "")
                            + " title=\"Dynamic update for creating template sets\">Dynamic Update</a>");
        }
        outputStream.print(" | ");
        if (s.equals("E-mail")) {
            outputStream.println("<B>E-mail</B>");
        } else {
            outputStream.println("<a href=" + getPageLink("mailPrefs", "")
                    + " title=\"eMail Lists, server info\">E-mail</a>");
        }
        outputStream.print(" | ");
        if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_haInstall")
                .length() > 0) {
            if (s.equals("Fail-Over")) {
                outputStream.println("<B>Fail-Over</B>");
            } else {
                outputStream
                        .println("<a href="
                                + getPageLink("mirrorPrefs", "")
                                + " title=\"Set up a fail-over of backup SiteView\">Fail-Over</a>");
            }
            outputStream.print(" | ");
        }
        if (s.equals("Log")) {
            outputStream.println("<B>Log</B>");
        } else {
            outputStream.println("<a href=" + getPageLink("logPrefs", "")
                    + " title=\"Log to Database\">Log</a>");
        }
        outputStream.print(" | ");
        if (s.equals("Pager")) {
            outputStream.println("<B>Pager</B>");
        } else {
            outputStream.println("<a href=" + getPageLink("pagerPrefs", "")
                    + " title=\"Set up pager and add other pagers\">Pager</a>");
        }
        outputStream.print(" | ");
        if (s.equals("Schedule")) {
            outputStream.println("<B>Schedule</B>");
        } else {
            outputStream
                    .println("<a href="
                            + getPageLink("schedulePrefs", "")
                            + " title=\"Create one or more run schedules for monitors\">Schedule</a>");
        }
        outputStream.print(" | ");
        if (!isPortalServerRequest()) {
            if (s.equals("SiteSeer")) {
                outputStream.println("<B>SiteSeer</B>");
            } else {
                outputStream
                        .println("<a href="
                                + getPageLink("siteseerPrefs", "")
                                + " title=\"Add SiteSeer group to SiteView or delete it\">SiteSeer</a>");
            }
            outputStream.print(" | ");
        }
//        if (s.equals(topazLongName)) {
//            outputStream.println("<B>" + topazShortName + "</B>");
//        } else {
//            outputStream.println("<a href=" + getPageLink("topazPrefs", "")
//                    + "&topazMS=" + " title=\"Allow " + topazLongName
//                    + " to talk to SiteView\">" + topazShortName + "</a>");
//        }
//        outputStream.print(" | ");
//        if (s.equals(topazLongName + " Managed Services")) {
//            outputStream.println("<B>MMS</B>");
//        } else {
//            outputStream.println("<a href=" + getPageLink("topazPrefs", "")
//                    + "&topazMS=true" + " title=\"Allow " + topazLongName
//                    + " Managed Services to talk to SiteView\">MMS</a>");
//        }
        outputStream.print(" | ");
        if (s.equals("SNMP")) {
            outputStream.println("<B>SNMP</B>");
        } else {
            outputStream.println("<a href=" + getPageLink("snmpPrefs", "")
                    + " title=\"Server info and ports, etc.\">SNMP</a>");
        }
        outputStream.print(" | ");
        if (s.equals("Unix Remotes")) {
            outputStream.println("<B>Remote UNIX</B>");
        } else {
            outputStream
                    .println("<a href="
                            + getPageLink("machine", "")
                            + " title=\"Set up connections for monitoring remote UNIX/Linux machines\">Remote UNIX</a>");
        }
        outputStream.print(" | ");
        if (com.dragonflow.SiteView.Platform.isWindows()
                || com.dragonflow.Utils.TextUtils.getValue(hashmap,
                        "_allowUnixToNT").length() > 0) {
            if (s.equals("NT Remotes")) {
                outputStream.println("<B>Remote NT</B>");
            } else {
                outputStream
                        .println("<a href="
                                + getPageLink("ntmachine", "")
                                + " title=\"Set up connections for monitoring remote NT/Win2K machines\">Remote NT</a>");
            }
            outputStream.print(" | ");
        }
        if (!isPortalServerRequest()) {
            if (s.equals("Users")) {
                outputStream.println("<B>Users</B>");
            } else {
                String s2 = "userPrefs";
                if (com.dragonflow.SiteView.Platform.isPortal()
                        && !isPortalServerRequest()) {
                    s2 = "portalUserPrefs";
                }
                outputStream
                        .println("<a href="
                                + getPageLink(s2, "")
                                + " title=\"Set up SiteView users with permissions\">Users</a>");
            }
        }
        if (com.dragonflow.SiteView.Platform.isQTInstalled()
                || com.dragonflow.SiteView.Platform.isALTInstalled()) {
            outputStream.print(" | ");
            if (s.equals("Recorder")) {
                outputStream.println("<B>Recorder</B>");
            } else {
                outputStream
                        .println("<a href="
                                + getPageLink("recorderPrefs", "")
                                + " title=\"Allow running QTP/ALT Scripts Monitors from SiteView\">Recorder</a>");
            }
        }
        outputStream.println("</CENTER>");
    }

    com.dragonflow.SiteView.SiteViewObject getSettingsGroup() {
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        java.lang.Object obj = siteviewgroup;
        if (!request.isStandardAccount()) {
            obj = siteviewgroup.getElement(request.getAccount());
        }
        return ((com.dragonflow.SiteView.SiteViewObject) (obj));
    }

}
