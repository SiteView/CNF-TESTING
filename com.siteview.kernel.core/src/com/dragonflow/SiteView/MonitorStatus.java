/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MonitorStatus.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>MonitorStatus</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
public class MonitorStatus {

    public static final String monitorQueueAdd = "adding the monitor to the monitors queue...";

    public static final String monitorQueueWaiting = "ready, waiting for other monitors to complete...";

    public static final String monitorQueueStart = "in monitor queue, starting...";

    public static final String starting = "monitor thread started...";

    public static final String logging = "logging...";

    public static final String runningExclusively = "ready, waiting for Dialup monitors to finish...";

    public static final String startingDispatcher = "starting the dispatcher process...";

    public static final String alertDebug = "inside alert debug...";

    public static final String monitorUpdate = "starting the monitor update...";

    public static final String monitorDone = "monitor update done...";

    public static final String checkingThresholds = "checking thresholds...";

    public static final String checkingDependsOn = "checking depends-on condition...";

    public static final String verifyingError = "verifying error...";

    public static final String dailyLoggerLogging = "logging the results to SiteView daily log...";

    public static final String checkingAlerts = "checking for alerts...";

    public static final String saving = "saving results...";

    public static final String done = "done, getting out of monitor thread...";

    public static final String processPoolGettingLock = "getting a process from the process pool...";

    public static final String processPoolExecuting = "start executing a task in a process from the process pool...";

    public static final String processPoolDoneExecuting = "done executing a task in a process from the process pool...";

    public static final String sendingAlert = "sending alert...";

    public static final String DBGettingDriver = "DatabaseMonitor: getting the driver...";

    public static final String DBInitializingDriver = "DatabaseMonitor: initializing the driver...";

    public static final String DBConnecting = "DatabaseMonitor: connecting...";

    public static final String DBExecuting = "DatabaseMonitor: executing the query...";

    public static final String DBAnalyzingResults = "DatabaseMonitor: analyzing results...";

    public static final String DBGettingWarnings = "DatabaseMonitor: getting warnings...";

    public static final String DBSettingsProperties = "DatabaseMonitor: setting values to properties...";

    public static final String FTPMonitorWaiting = "FTPMonitor ready, waiting for other FTP monitors to complete...";

    public static final String FTPMonitorWaitingGroup = "ready, waiting for other FTP monitors in group to complete";

    public static final String URLMonitorWaitingGlobal = "URLMonitor ready, waiting for other Global monitors in group to complete";

    public static final String URLMonitorWaitingSSLGroup = "URLMonitor ready, waiting for other SSL monitors in group to complete";

    public static final String URLMonitorWaitingSSL = "URLMonitor ready, waiting for other SSL monitors to complete";

    public static final String CPUMonitorAnalyzingResults = "CPUMonitor analayzing results...";

    public static final String CPUMonitorUsedWin = "CPUMonitor retrieving the CPU Used on windows machine...";

    public static final String CPUMonitorUsedUnix = "CPUMonitor retrieving the CPU Used on unix machine...";

    public static final String PingMonitorAnalyzingResults = "PingMonitor analyzing results...";

    public static final String PingCmdExecuting = "PingMonitor executing cmd...";

    public static final String MAPIWaiting = "MAPIMonitor waiting for other MAPIMonitors to finish...";

    public MonitorStatus() {
    }
}
