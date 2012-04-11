/*
 * 
 * Created on 2005-2-15 12:37:34
 *
 * CompoundEvent.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>CompoundEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import jgl.Array;

// Referenced classes of package COM.dragonflow.SiteView:
// ScheduleEvent, Action

class CompoundEvent extends ScheduleEvent {

    Array events;

    CompoundEvent(Action action) {
        super(action);
        events = new Array();
    }

    void addEvent(ScheduleEvent scheduleevent) {
        events.add(scheduleevent);
    }

    long calculateNextTime(long l) {
        Enumeration enumeration = events.elements();
        ScheduleEvent scheduleevent;
        for (time = 0x7fffffffffffffffL; enumeration.hasMoreElements(); time = Math
                .min(time, scheduleevent.calculateNextTime())) {
            scheduleevent = (ScheduleEvent) enumeration.nextElement();
        }

        if (time == 0x7fffffffffffffffL) {
            time = l - 1L;
        }
        return time;
    }
}
