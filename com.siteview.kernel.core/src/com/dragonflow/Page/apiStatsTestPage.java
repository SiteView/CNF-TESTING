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

import com.dragonflow.Api.APISiteView;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class apiStatsTestPage extends com.dragonflow.Page.CGI {

    private String server;

    private String port;

    public apiStatsTestPage() {
        server = "localhost";
        port = "8888";
    }

    public void printBody() throws java.lang.Exception {
        com.dragonflow.Api.APISiteView apisiteview = new APISiteView();
        if (!request.hasValue("testName") || request.hasValue("testName")
                && request.getValue("testName").equals("logMonitorStats")) {
            outputStream.println("<H2>From the Health Page...</H2>\n");
            outputStream.println("<p>Log Monitors:<br>");
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName").equals("serverLoadStats")) {
            outputStream.println("<H2>From the Health Page...</H2>\n");
            outputStream.println("<p>Server Load:<br>");
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName").equals(
                        "currentMonitorsPerMinute")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats = apisiteview
                    .getCurrentMonitorsPerMinute();
            outputStream.println("<p>" + sshealthstats.getName() + ": ");
            java.lang.Float float1 = (java.lang.Float) sshealthstats
                    .getMeasurement1();
            outputStream.println(com.dragonflow.Utils.TextUtils.floatToString(
                    float1.floatValue(), 2));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName")
                        .equals("currentMonitorsRunning")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats1 = apisiteview
                    .getCurrentMonitorsRunning();
            outputStream.println("<p>" + sshealthstats1.getName() + ": ");
            java.lang.Integer integer = (java.lang.Integer) sshealthstats1
                    .getMeasurement1();
            outputStream.println(com.dragonflow.Utils.TextUtils
                    .numberToString(integer.intValue()));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName")
                        .equals("currentMonitorsWaiting")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats2 = apisiteview
                    .getCurrentMonitorsWaiting();
            outputStream.println("<p>" + sshealthstats2.getName() + ": ");
            java.lang.Integer integer1 = (java.lang.Integer) sshealthstats2
                    .getMeasurement1();
            outputStream.println(com.dragonflow.Utils.TextUtils
                    .numberToString(integer1.intValue()));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName").equals(
                        "maximumMonitorsPerMinute")) {
            outputStream.println("<p>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats3 = apisiteview
                    .getMaximumMonitorsPerMinute();
            outputStream.println("<br>" + sshealthstats3.getName() + ": ");
            java.lang.Float float2 = (java.lang.Float) sshealthstats3
                    .getMeasurement1();
            java.lang.Float float5 = (java.lang.Float) sshealthstats3
                    .getMeasurement2();
            outputStream.println(com.dragonflow.Utils.TextUtils.floatToString(
                    float2.floatValue(), 2)
                    + " at "
                    + com.dragonflow.Utils.TextUtils.prettyDate(float5
                            .longValue()));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName")
                        .equals("maximumMonitorsRunning")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats4 = apisiteview
                    .getMaximumMonitorsRunning();
            outputStream.println("<p>" + sshealthstats4.getName() + ": ");
            java.lang.Float float3 = (java.lang.Float) sshealthstats4
                    .getMeasurement1();
            java.lang.Float float6 = (java.lang.Float) sshealthstats4
                    .getMeasurement2();
            outputStream.println(com.dragonflow.Utils.TextUtils
                    .numberToString(float3.intValue())
                    + " at "
                    + com.dragonflow.Utils.TextUtils.prettyDate(float6
                            .longValue()));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName")
                        .equals("maximumMonitorsWaiting")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            com.dragonflow.Api.SSHealthStats sshealthstats5 = apisiteview
                    .getMaximumMonitorsWaiting();
            outputStream.println("<p>" + sshealthstats5.getName() + ": ");
            java.lang.Float float4 = (java.lang.Float) sshealthstats5
                    .getMeasurement1();
            java.lang.Float float7 = (java.lang.Float) sshealthstats5
                    .getMeasurement2();
            outputStream.println(com.dragonflow.Utils.TextUtils
                    .numberToString(float4.intValue())
                    + " at "
                    + com.dragonflow.Utils.TextUtils.prettyDate(float7
                            .longValue()));
            outputStream
                    .println("\n<p><a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else if (!request.hasValue("testName")
                || request.hasValue("testName")
                && request.getValue("testName").equals("runningMonitorStats")) {
            outputStream.println("<H2>From the Progress Report Page...</H2>\n");
            outputStream.println("<p>Running Monitor Stats:<br>");
            com.dragonflow.Api.SSHealthStats asshealthstats[] = apisiteview
                    .getRunningMonitorStats();
            if (asshealthstats.length > 0) {
                for (int i = 0; i < asshealthstats.length; i++) {
                    outputStream.println("<p>");
                    outputStream.println("<br>Name: "
                            + asshealthstats[i].getName());
                    outputStream.println("<br>Status: "
                            + asshealthstats[i].getStatus());
                    outputStream.println("<br>"
                            + asshealthstats[i].getMeasurement1());
                    outputStream.println("<br>"
                            + asshealthstats[i].getMeasurement2());
                }

            } else {
                outputStream.println("<p>No Monitors are currently running!");
            }
            outputStream
                    .println("\n<a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else {
            outputStream
                    .println("NO TEST SELECTED! <a href=http://"
                            + server
                            + ":"
                            + port
                            + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        }
    }
}
