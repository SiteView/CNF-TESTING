/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StatefulMonitor;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import com.dragonflow.SiteView.Scheduler;

// Referenced classes of package com.dragonflow.StatefulMonitor:
// StatefulConnection, TopazWatchdogLastReportedDataCache,
// StatefulConnectionUser

public class AgentLastRepotedDataConnection extends com.dragonflow.StatefulMonitor.StatefulConnection {

    String error;

    boolean isInError;

    boolean bCacheThreadStarted;

    boolean bCacheThreadResumed;

    private static com.dragonflow.SiteView.Scheduler _twdLastDataCacheScheduler = null;

//    private static com.dragonflow.StatefulMonitor.TopazWatchdogLastReportedDataCache _lastReportedDataCache = null;

    private static boolean bInitialized = false;

    boolean connected;

    public AgentLastRepotedDataConnection(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        super(statefulconnectionuser);
        isInError = false;
        bCacheThreadStarted = false;
        bCacheThreadResumed = false;
        connected = false;
        ScheduleAgentLastRepotedDataCache();
    }

    public synchronized boolean ScheduleAgentLastRepotedDataCache() {
        connected = true;
//        if (!bInitialized) {
//            _twdLastDataCacheScheduler = new Scheduler();
//            _lastReportedDataCache = new TopazWatchdogLastReportedDataCache();
//            int i = com.dragonflow.TopazWatchdog.WatchdogConfig.getDataTimeCacheUpdateFrequencyInSeconds();
//            _twdLastDataCacheScheduler.scheduleRepeatedPeriodicAction(_lastReportedDataCache, i * 1000);
//            _twdLastDataCacheScheduler.startScheduler(com.dragonflow.SiteView.TopazInfo.getTopazName() + " Watchdog Last Reported Data Time Cache Update Scheduler");
//            bInitialized = true;
//        } else 
		{
            _twdLastDataCacheScheduler.resumeScheduler();
        }
        return true;
    }

    public synchronized void disconnect() {
        connected = false;
        if (null != _twdLastDataCacheScheduler) {
            _twdLastDataCacheScheduler.suspendScheduler();
        }
    }

//    public com.dragonflow.StatefulMonitor.TopazWatchdogLastReportedDataCache getData() {
//        return _lastReportedDataCache;
//    }

}
