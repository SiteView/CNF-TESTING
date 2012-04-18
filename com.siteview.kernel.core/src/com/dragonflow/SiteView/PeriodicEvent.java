/*
 * 
 * Created on 2005-2-20 5:17:01
 *
 * PeriodicEvent.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>PeriodicEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
// Referenced classes of package com.dragonflow.SiteView:
// ScheduleEvent, Platform, Action
class PeriodicEvent extends ScheduleEvent {

    long frequency;

    PeriodicEvent(Action action, long l) {
        super(action);
        frequency = l;
    }

    PeriodicEvent(Action action, long l, long l1) {
        this(action, l);
        if (l1 + l > Platform.timeMillis()) {
            time = l1 + l;
        }
    }

    long calculateNextTime(long l) {
        if (l >= time) {
            if (time == 0L) {
                time = l;
            } else {
                time = l + frequency;
            }
        }
        return time;
    }
}