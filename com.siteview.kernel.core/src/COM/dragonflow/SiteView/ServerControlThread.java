/*
 * 
 * Created on 2005-2-16 16:48:49
 *
 * ServerControlThread.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ServerControlThread</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;

// Referenced classes of package COM.dragonflow.SiteView:
// ServerReadThread, Platform, SiteViewGroup, Server

class ServerControlThread extends Thread {

    long refreshRate;

    long pause;

    int threadCount;

    boolean done;

    ServerReadThread threads[];

    static long updatingTimeout = 0L;

    static ServerControlThread serverControlThread = null;

    int nextServer;

    ServerControlThread(int i, long l, long l1) {
        threadCount = 5;
        done = false;
        threads = null;
        nextServer = 0x7fffffff;
        refreshRate = l;
        pause = l1;
        threadCount = i;
    }

    public static synchronized void startUpdating() {
        updatingTimeout = Platform.timeMillis() + 0x493e0L;
        if (serverControlThread == null) {
            LogManager.log("RunMonitor", "starting multi-view update thread");
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            long l = siteviewgroup.getSettingAsLong("_serverReadRefresh",
                    60000);
            long l1 = siteviewgroup.getSettingAsLong("_serverReadPause", 1000);
            long l2 = siteviewgroup.getSettingAsLong("_serverReadThreadCount",
                    5);
            serverControlThread = new ServerControlThread((int) l2, l, l1);
            serverControlThread.start();
            Platform.sleep(2000L);
        }
    }

    public static synchronized void stopUpdating() {
        if (serverControlThread != null) {
            serverControlThread.done = true;
            serverControlThread.interrupt();
            serverControlThread = null;
        }
    }

    void startReading() {
        if (threads == null) {
            threads = new ServerReadThread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new ServerReadThread(this, pause, i);
                threads[i].start();
            }

        }
    }

    void stopReading() {
        if (threads != null) {
            for (int i = 0; i < threads.length; i++) {
                threads[i].done = true;
            }

            synchronized (this) {
                notifyAll();
            }
        }
        threads = null;
    }

    public void run() {
        try {
            startReading();
            for (; !done && Platform.timeMillis() <= updatingTimeout; Platform
                    .sleep(refreshRate)) {
                synchronized (this) {
                    nextServer = 0;
                    notifyAll();
                }
            }

            stopReading();
            LogManager.log("RunMonitor", "stopping multi-view update thread");
        } catch (Exception exception) {
            LogManager.log("Error",
                    "unknown error in multi-view update thread, " + exception);
        }
        serverControlThread = null;
    }

    synchronized HashMap nextServer() {
        HashMap hashmap = null;
        if (Server.serversCache != null
                && nextServer < Server.serversCache.size()) {
            hashmap = (HashMap) Server.serversCache.at(nextServer);
            nextServer++;
        }
        return hashmap;
    }

}
