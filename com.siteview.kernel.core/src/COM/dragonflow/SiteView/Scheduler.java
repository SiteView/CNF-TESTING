/*
 * 
 * Created on 2005-2-16 16:45:13
 *
 * Scheduler.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Scheduler</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;
import java.util.Enumeration;

import jgl.HashMap;
import jgl.PriorityQueue;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// GreaterEqualTime, AbsoluteEvent, PeriodicEvent, CompoundEvent,
// DailyEvent, DatedEvent, ScheduleEvent, Platform,
// Action

public class Scheduler implements Runnable {

    public static final long SECOND_MILLISECONDS = 1000L;

    public static final long MINUTE_MILLISECONDS = 60000L;

    public static final long HOUR_MILLISECONDS = 0x36ee80L;

    public static final long DAY_MILLISECONDS = 0x5265c00L;

    static HashMap dayMap;

    volatile boolean running;

    volatile boolean suspended;

    volatile int suspendedCount;

    long heartbeatTime;

    long nextTime;

    public static final String NEXT_ACTION_DESCRIPTION_NOTHING_SCHEDULED = "nothing scheduled";

    String nextActionDescription;

    public static boolean debug = false;

    Thread thread;

    PriorityQueue queue;

    long maxSchedulerSleep;

    public Scheduler() {
        running = true;
        suspended = false;
        suspendedCount = 0;
        heartbeatTime = Platform.timeMillis();
        nextTime = Platform.timeMillis();
        nextActionDescription = "nothing scheduled";
        thread = null;
        queue = new PriorityQueue(new GreaterEqualTime());
        maxSchedulerSleep = 60000L;
    }

    public void startScheduler(String s, boolean flag) {
        if (thread != null) {
            System.out
                    .println("Scheduler Thread is already running - killing it off");
        }
        killActions(false);
        thread = new Thread(this);
        thread.setPriority(10);
        thread.setName(s);
        thread.setDaemon(true);
        thread.start();
    }

    public void startScheduler(String s) {
        if (thread != null) {
            System.out
                    .println("Scheduler Thread is already running - killing it off");
        }
        killActions(false);
        thread = new Thread(this);
        thread.setName(s);
        thread.setDaemon(true);
        thread.start();
    }

    public void setPriority(int i) {
        thread.setPriority(i);
    }

    public void stopScheduler() {
        running = false;
        killActions(true);
    }

    public synchronized void suspendScheduler() {
        suspended = true;
        suspendedCount++;
        if (debug) {
            System.out.println("SUSPENDING SCHEDULER (" + suspendedCount + ") "
                    + thread);
        }
        if (suspendedCount > 0) {
            killActions(true);
        }
    }

    public synchronized void resumeScheduler() {
        suspended = false;
        suspendedCount--;
        if (debug) {
            System.out.println("RESUMING SCHEDULER (" + suspendedCount + ") "
                    + thread);
        }
        if (suspendedCount <= 0) {
            notifyAll();
            killActions(false);
        }
    }

    boolean isSuspended() {
        return suspendedCount > 0;
    }

    public void run() {
        long l = 0L;
        ScheduleEvent scheduleevent = null;
        try {
label0: 	do {
                 long l1;
label1:          {
                    if (!running) 
					{
                        break label0;
                    }
                    do {
                        l1 = maxSchedulerSleep;
                        heartbeatTime = Platform.timeMillis();
                        if (isSuspended()) {
                            break label1;
                        }
                        scheduleevent = firstEvent();
                        if (scheduleevent == null) {
                            break label1;
                        }
                        long l2 = Platform.timeMillis();
                        if (scheduleevent.getTime() > l2) {
                            break;
                        }
                        if (scheduleevent.isRepeated()) {
                            scheduleevent.calculateNextTime();
                            addEvent(scheduleevent);
                        }
                        doAction(scheduleevent);
                    } while (true);
                    addEvent(scheduleevent);
                    nextTime = scheduleevent.getTime();
                    Action action = scheduleevent.getAction();
                    if (action == null) {
                        LogManager.log("Error", "Scheduler error, scheduler: "
                                + Thread.currentThread().getName());
                        LogManager.log("Error",
                                "Scheduler missing action, event: "
                                        + scheduleevent);
                        nextActionDescription = "";
                    } else {
                        nextActionDescription = action.toString();
                    }
                    l1 = scheduleevent.getTime() - Platform.timeMillis();
                    if (l1 < 0L) {
                        l1 = 0L;
                    }
                }
                if (l1 > 0L) {
                    l1 = Math.min(l1, maxSchedulerSleep);
                    synchronized (this) {
                        wait(l1);
                    }
                }
            } while (true);
        } catch (Exception exception) {
            LogManager.log("Error", "Scheduler error, scheduler: "
                    + Thread.currentThread().getName() + ", error: "
                    + exception + ", details: "
                    + FileUtils.stackTraceText(exception));
            LogManager.log("Error", "Scheduler event: " + scheduleevent);
        }
        thread = null;
    }

    protected void doAction(ScheduleEvent scheduleevent) {
        if (debug) {
            System.out.println("*** START " + TextUtils.dateToString() + " "
                    + Thread.currentThread().getName() + " trigger "
                    + scheduleevent.action.toString());
        }
        try {
            scheduleevent.doAction();
        } catch (Exception exception) {
            LogManager.log("Error", "Scheduler error, scheduler: "
                    + Thread.currentThread().getName() + ", error: "
                    + exception + ", event: " + scheduleevent + ", details: "
                    + FileUtils.stackTraceText(exception));
        }
        if (debug) {
            System.out.println("*** DONE  " + TextUtils.dateToString() + " "
                    + Thread.currentThread().getName() + " trigger "
                    + scheduleevent.action.toString());
        }
    }

    public long getHeartbeat() {
        return heartbeatTime;
    }

    public long getNextTime() {
        return nextTime;
    }

    public String getNextActionDescription() {
        return nextActionDescription;
    }

    public void scheduleRepeatedAction(Action action, String s) {
        scheduleRepeatedAction(action, s, 0L);
    }

    public void scheduleRepeatedAction(Action action, String s, long l) {
        scheduleAction(action, s, true, l);
    }

    public void scheduleAction(Action action, String s, boolean flag, long l) {
        String as[] = TextUtils.split(s);
        try {
            if (as.length == 3) {
                if (as[0].equalsIgnoreCase("weekday")) {
                    scheduleDailyAction(action, as[1], as[2], flag, l);
                } else if (as[0].equalsIgnoreCase("monthday")) {
                    scheduleDatedAction(action, as[1], as[2], flag, l);
                }
            } else if (as.length == 2 && as[0].equalsIgnoreCase("periodic")) {
                schedulePeriodicAction(action, Long.parseLong(as[1]), flag, l);
            }
        } catch (NumberFormatException numberformatexception) {
        }
    }

    public void scheduleAbsolutePeriodicAction(Action action, String s, long l) {
        try {
            s = s.substring(1);
            AbsoluteEvent absoluteevent = new AbsoluteEvent(action, s);
            schedule(absoluteevent);
        } catch (Exception exception) {
            LogManager.log("Error", "schedule format error: schedule=" + s
                    + ", error= " + exception);
        }
    }

    public void scheduleRepeatedPeriodicAction(Action action, long l) {
        schedulePeriodicAction(action, l, true, 0L);
    }

    public void scheduleRepeatedPeriodicAction(Action action, long l, long l1) {
        schedulePeriodicAction(action, l, true, l1);
    }

    public void schedulePeriodicAction(Action action, long l) {
        schedulePeriodicAction(action, l, 0L);
    }

    public void schedulePeriodicAction(Action action, long l, long l1) {
        schedulePeriodicAction(action, l, false, l1);
    }

    private void schedulePeriodicAction(Action action, long l, boolean flag,
            long l1) {
        PeriodicEvent periodicevent = new PeriodicEvent(action, l, l1);
        periodicevent.setRepeated(flag);
        schedule(periodicevent);
    }

    public void scheduleRepeatedDailyAction(Action action, String s, String s1) {
        scheduleDailyAction(action, s, s1, true, 0L);
    }

    public void scheduleDailyAction(Action action, String s, String s1,
            boolean flag, long l) {
        CompoundEvent compoundevent = new CompoundEvent(action);
        compoundevent.setRepeated(flag);
        String as[] = TextUtils.split(s1, ",");
        int ai[] = new int[as.length];
        for (int i = 0; i < as.length; i++) {
            ai[i] = TextUtils.stringToDaySeconds(as[i]);
        }

        String as1[] = TextUtils.split(s, ",");
        for (int j = 0; j < as1.length; j++) {
            Integer integer = (Integer) dayMap.get(as1[j].toLowerCase());
            if (integer == null) {
                continue;
            }
            for (int k = 0; k < ai.length; k++) {
                DailyEvent dailyevent = new DailyEvent(null,
                        integer.intValue(), ai[k], l);
                compoundevent.addEvent(dailyevent);
            }

        }

        schedule(compoundevent);
    }

    public void scheduleRepeatedDatedAction(Action action, String s, String s1) {
        scheduleDatedAction(action, s, s1, true, 0L);
    }

    public void scheduleDatedAction(Action action, String s, String s1,
            boolean flag, long l) {
        CompoundEvent compoundevent = new CompoundEvent(action);
        compoundevent.setRepeated(flag);
        String as[] = TextUtils.split(s1, ",");
        int ai[] = new int[as.length];
        for (int i = 0; i < as.length; i++) {
            ai[i] = TextUtils.stringToDaySeconds(as[i]);
        }

        String as1[] = TextUtils.split(s, ",");
        for (int j = 0; j < as1.length; j++) {
            for (int k = 0; k < ai.length; k++) {
                try {
                    int i1 = Integer.parseInt(as1[j]);
                    DatedEvent datedevent = new DatedEvent(null, i1, ai[k], l);
                    compoundevent.addEvent(datedevent);
                } catch (NumberFormatException numberformatexception) {
                }
            }

        }

        schedule(compoundevent);
    }

    private synchronized ScheduleEvent firstEvent() {
        ScheduleEvent scheduleevent = null;
        if (!queue.isEmpty()) {
            scheduleevent = (ScheduleEvent) queue.pop();
        }
        return scheduleevent;
    }

    private synchronized void addEvent(ScheduleEvent scheduleevent) {
        queue.push(scheduleevent);
    }

    protected synchronized void schedule(ScheduleEvent scheduleevent) {
        scheduleevent.calculateNextTime();
        queue.push(scheduleevent);
        notifyAll();
    }

    public synchronized void unschedule(Action action) {
        PriorityQueue priorityqueue = new PriorityQueue(queue);
        queue.clear();
        do {
            if (priorityqueue.isEmpty()) {
                break;
            }
            ScheduleEvent scheduleevent = (ScheduleEvent) priorityqueue.pop();
            if (scheduleevent.getAction() != action) {
                queue.push(scheduleevent);
            }
        } while (true);
    }

    public String internalState() {
        StringBuffer stringbuffer = new StringBuffer();
        if (isSuspended()) {
            stringbuffer.append("*** Scheduler Suspended ***\n");
        }
        stringbuffer.append("------- " + thread + "Schedules -------\n");
        int i = 0;
        for (Enumeration enumeration = queue.elements(); enumeration
                .hasMoreElements(); stringbuffer.append("\n")) {
            ScheduleEvent scheduleevent = (ScheduleEvent) enumeration
                    .nextElement();
            i++;
            if (scheduleevent.isRepeated()) {
                stringbuffer.append("R  ");
            } else {
                stringbuffer.append("   ");
            }
            stringbuffer.append(TextUtils.dateToString(new Date(scheduleevent
                    .getTime())));
            stringbuffer.append("  ");
            stringbuffer.append(scheduleevent.getAction().toString());
        }

        stringbuffer.append("===  " + i + " schedules");
        return stringbuffer.toString();
    }

    public boolean isRunning() {
        return thread != null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param flag
     */
    protected void killActions(boolean flag) {
        Enumeration enumeration = queue.elements();
		int i=0;
        while (enumeration.hasMoreElements()) {
            ScheduleEvent scheduleevent = (ScheduleEvent) enumeration
                    .nextElement();
            if (scheduleevent != null) {
                Action action = scheduleevent.getAction();
                if (action != null) {
                    action.setKill(flag);
                }
            }
        }
		
    }

    static {
        dayMap = new HashMap();
        dayMap.add("sunday", new Integer(0));
        dayMap.add("sun", new Integer(0));
        dayMap.add("su", new Integer(0));
        dayMap.add("u", new Integer(0));
        dayMap.add("monday", new Integer(1));
        dayMap.add("mon", new Integer(1));
        dayMap.add("m", new Integer(1));
        dayMap.add("tuesday", new Integer(2));
        dayMap.add("tue", new Integer(2));
        dayMap.add("t", new Integer(2));
        dayMap.add("wednesday", new Integer(3));
        dayMap.add("wed", new Integer(3));
        dayMap.add("w", new Integer(3));
        dayMap.add("thursday", new Integer(4));
        dayMap.add("thu", new Integer(4));
        dayMap.add("th", new Integer(4));
        dayMap.add("r", new Integer(4));
        dayMap.add("friday", new Integer(5));
        dayMap.add("fri", new Integer(5));
        dayMap.add("f", new Integer(5));
        dayMap.add("saturday", new Integer(6));
        dayMap.add("sat", new Integer(6));
        dayMap.add("s", new Integer(6));
    }
}
