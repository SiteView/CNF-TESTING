/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MonitorQueue.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MonitorQueue</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.Array;
import COM.dragonflow.Utils.CounterLock;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, Platform

public class MonitorQueue extends Thread {

    public static Array readyMonitors = new Array();

    public static int maxReadyMonitors = 0;

    public static long maxReadyMonitorsTime = 0L;

    public boolean done;

    public static CounterLock monitorThrottle = new CounterLock(30);

    static MonitorQueue monitorStarter = null;

    public MonitorQueue() {
        done = false;
    }

    public static synchronized void add(AtomicMonitor atomicmonitor,
            boolean flag) {
        atomicmonitor.currentStatus = "adding the monitor to the monitors queue...";
        if (flag) {
            startOne(atomicmonitor, false);
        } else {
            if (monitorStarter == null) {
                monitorStarter = init();
            }
            synchronized (monitorStarter) {
                atomicmonitor.currentStatus = "ready, waiting for other monitors to complete...";
                readyMonitors.add(atomicmonitor);
                monitorStarter.notify();
                if (readyMonitors.size() > maxReadyMonitors) {
                    maxReadyMonitors = readyMonitors.size();
                    maxReadyMonitorsTime = Platform.makeDate().getTime();
                }
            }
        }
    }

    public static int release(AtomicMonitor atomicmonitor) {
        return monitorThrottle.release();
    }

    public static int getRunningCount() {
        int i = 0;
        if (monitorThrottle != null) {
            i = monitorThrottle.used();
        }
        return i;
    }

    public static MonitorQueue init() {
        MonitorQueue monitorqueue = new MonitorQueue();
        monitorqueue.setPriority(8);
        monitorqueue.setName("Monitor Queue");
        monitorqueue.setDaemon(true);
        monitorqueue.start();
        return monitorqueue;
    }

    public static void stopQueue() {
        if (monitorStarter != null) {
            synchronized (monitorStarter) {
                monitorStarter.done = true;
                monitorStarter.notify();
            }
        }
    }

    public void run() {
        do {
            if (done) {
                break;
            }
            AtomicMonitor atomicmonitor = null;
            synchronized (this) {
                try {
                    if (readyMonitors.isEmpty()) {
                        wait();
                    }
                } catch (InterruptedException interruptedexception) {
                }
                if (!readyMonitors.isEmpty()) {
                    atomicmonitor = (AtomicMonitor) readyMonitors.popFront();
                }
            }
            if (atomicmonitor != null) {
                monitorThrottle.get();
                startOne(atomicmonitor, true);
            }
        } while (true);
    }

    static void startOne(AtomicMonitor atomicmonitor, boolean flag) {
        atomicmonitor.currentStatus = "in monitor queue, starting...";
        COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = atomicmonitor
                .getThread();
        singlethread.setCustomProperty(flag);
        singlethread.activate(atomicmonitor);
    }

}
