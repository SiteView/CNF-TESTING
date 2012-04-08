/*
 * 
 * Created on 2005-2-15 12:47:17
 *
 * DatedEvent.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>DatedEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;

import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ScheduleEvent, Action

class DatedEvent extends ScheduleEvent {

    int dayOfMonth;

    int seconds;

    boolean lastDayOfMonth;

    DatedEvent(Action action, int i, int j) {
        super(action);
        lastDayOfMonth = false;
        dayOfMonth = i;
        seconds = j;
        if (j < 0) {
            seconds = TextUtils.DAY_SECONDS + j;
            dayOfMonth--;
            if (dayOfMonth <= 0) {
                lastDayOfMonth = true;
            }
        }
        if (i > 31) {
            lastDayOfMonth = true;
        }
    }

    DatedEvent(Action action, int i, int j, long l) {
        this(action, i, j);
        lastTime = l;
    }

    long calculateNextTime(long l) {
        Date date = new Date(l);
        Date date1 = new Date(date.getYear(), date.getMonth(), date.getDate());
        if (lastDayOfMonth) {
            dayOfMonth = TextUtils.MONTH_DAYS[date.getMonth()];
        }
        date1.setTime(date1.getTime() + (long) seconds * 1000L);
        for (; date1.getTime() <= date.getTime()
                || date1.getDate() != dayOfMonth; date1
                .setTime(date1.getTime() + 0x5265c00L)) {
            if (date1.getMonth() != date.getMonth() && lastDayOfMonth) {
                dayOfMonth = TextUtils.MONTH_DAYS[date1.getMonth()];
            }
        }

        if (lastTime > 0L && time == 0L && date.getDate() == dayOfMonth) {
            Date date2 = new Date(lastTime);
            if (date2.getYear() != date.getYear()
                    || date2.getMonth() != date.getMonth()
                    || date2.getDate() != date.getDate()) {
                Date date3 = new Date(date.getYear(), date.getMonth(), date
                        .getDate());
                date3.setTime(date3.getTime() + (long) seconds * 1000L);
                if (date3.before(date)) {
                    time = date.getTime() - 1L;
                    return time;
                }
            }
        }
        time = date1.getTime();
        return time;
    }
}
