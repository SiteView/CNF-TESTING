/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * LogPuller.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>LogPuller</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.Array;
import COM.dragonflow.Log.LogManager;

// Referenced classes of package COM.dragonflow.SiteView:
// LogPullerReadThread, SiteViewGroup, Portal

public class LogPuller {

    private static LogPullerReadThread threads[] = null;

    public LogPuller() {
    }

    public static void startPulling() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("_logCollectorPause", 10) * 1000;
        long l1 = siteviewgroup.getSettingAsLong("_logCollectorConfigRefresh",
                3600) * 1000;
        long l2 = siteviewgroup.getSettingAsLong("_logCollectorThreads", 5);
        if (l2 > (long) getPortals().size()) {
            l2 = getPortals().size();
        }
        if (l2 < 1L) {
            l2 = 1L;
        }
        LogManager.log("RunMonitor", "starting log collector, " + l2
                + " threads, " + l + " ms");
        threads = new LogPullerReadThread[(int) l2];
        for (int i = 0; (long) i < l2; i++) {
            threads[i] = new LogPullerReadThread(l, l1, i);
            threads[i].start();
        }

    }

    public static void stopPulling() {
        if (threads != null) {
            for (int i = 0; i < threads.length; i++) {
                threads[i].done = true;
            }

        }
        threads = null;
    }

    public static Array getPortals() {
        Array array = Portal.getPortal().getRawElements();
        if (array == null) {
            array = new Array();
        }
        return array;
    }

    public static int getThreadCount() {
        if (threads == null) {
            return 0;
        } else {
            return threads.length;
        }
    }

}
