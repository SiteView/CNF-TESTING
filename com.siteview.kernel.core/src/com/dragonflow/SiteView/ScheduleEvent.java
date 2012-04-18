/*
 * 
 * Created on 2005-2-16 16:43:31
 *
 * ScheduleEvent.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>ScheduleEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
// Referenced classes of package com.dragonflow.SiteView:
// Platform, Action
public abstract class ScheduleEvent {

    long time;

    Action action;

    boolean repeat;

    long lastTime;

    ScheduleEvent(Action action1) {
        time = 0L;
        repeat = true;
        action = action1;
    }

    public long calculateNextTime() {
        return calculateNextTime(Platform.timeMillis());
    }

    abstract long calculateNextTime(long l);

    public long getTime() {
        return time;
    }

    public Action getAction() {
        return action;
    }

    public boolean isRepeated() {
        return repeat;
    }

    public String toString() {
        return "[ScheduleEvent,time=" + time + ", action=" + action + "]";
    }

    public void setRepeated(boolean flag) {
        repeat = flag;
    }

    public void doAction() {
        action.trigger();
    }
}
