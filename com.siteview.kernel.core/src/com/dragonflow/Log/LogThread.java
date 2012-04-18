/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Date;
import java.util.TreeSet;

// Referenced classes of package com.dragonflow.Log:
// TimeBufferedLoggerComparator, TimeBufferedLogger, LogManager, Logger

public class LogThread extends java.lang.Thread {

    protected java.util.TreeSet loggers;

    protected boolean timeToEnd;

    public LogThread() {
        loggers = new TreeSet(new TimeBufferedLoggerComparator());
        timeToEnd = false;
    }

    /**
     * 
     */
    public void run() {
        while (!timeToEnd) {

            long l;
            long l1;
            com.dragonflow.Log.TimeBufferedLogger timebufferedlogger;

            try {
                while (true) {
                    l = (new Date()).getTime();
                    l1 = -1L;
                    timebufferedlogger = null;
                    synchronized (this) {
                        if (loggers.size() <= 0) {
                            break;
                        }
                        com.dragonflow.Log.TimeBufferedLogger timebufferedlogger1 = (com.dragonflow.Log.TimeBufferedLogger) loggers.first();
                        l1 = timebufferedlogger1.getExpiration().getTime();
                        if (l1 <= l) {
                            timebufferedlogger = timebufferedlogger1;
                            loggers.remove(timebufferedlogger1);
                            if (timebufferedlogger.getBufferDuration() > 0L) {
                                timebufferedlogger.updateExpiration();
                                loggers.add(timebufferedlogger);
                                break;
                            }
                        }
                    }

                    if (timebufferedlogger != null) {
                        timebufferedlogger.log();
                    }
                }

                synchronized (this) {
                    try {
                        if (l1 > 0L) {
                            wait(l1 - l);
                        } else {
                            wait();
                        }
                    } catch (java.lang.InterruptedException interruptedexception) {
                        try {
                            java.lang.System.err.println("exception while waiting in logThread: " + interruptedexception.toString());
                        } catch (java.lang.Exception exception2) {
                        }
                    }
                }

            } catch (java.lang.Exception exception) {
                com.dragonflow.Log.LogManager.log("error", "failure flushing logger: " + exception.toString());

            }
        }
    }

    public synchronized void addLogger(com.dragonflow.Log.Logger logger) {
        long l = logger.getBufferDuration();
        if (l > 0L) {
            com.dragonflow.Log.TimeBufferedLogger timebufferedlogger = new TimeBufferedLogger(logger);
            loggers.add(timebufferedlogger);
            notify();
        }
    }

    /**
     * 
     * 
     * @param logger
     */
    public synchronized void removeLogger(com.dragonflow.Log.Logger logger) {
        java.util.Iterator iterator = loggers.iterator();
        while (iterator.hasNext()) {
            com.dragonflow.Log.TimeBufferedLogger timebufferedlogger = (com.dragonflow.Log.TimeBufferedLogger) iterator.next();
            if (timebufferedlogger.getLogger() == logger) {
                loggers.remove(timebufferedlogger);
                notify();
                break;
            }
        }
    }

    public synchronized void triggerLogging(com.dragonflow.Log.Logger logger) {
        com.dragonflow.Log.TimeBufferedLogger timebufferedlogger = null;
        java.util.Iterator iterator = loggers.iterator();
        while (iterator.hasNext()) {
            com.dragonflow.Log.TimeBufferedLogger timebufferedlogger1 = (com.dragonflow.Log.TimeBufferedLogger) iterator.next();
            if (timebufferedlogger1.getLogger() == logger) {
                timebufferedlogger = timebufferedlogger1;
                loggers.remove(timebufferedlogger1);
                break;
            }
        }
        if (timebufferedlogger == null) {
            timebufferedlogger = new TimeBufferedLogger(logger, true);
        } else {
            timebufferedlogger.forceImmediate();
        }
        loggers.add(timebufferedlogger);
        notify();
    }

    public void end() {
        timeToEnd = true;
        synchronized (this) {
            notify();
        }
    }
}
