/*
 * 
 * Created on 2005-2-15 12:46:24
 *
 * DailyEvent.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>DailyEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;

import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.SiteView:
// ScheduleEvent, Action

class DailyEvent extends ScheduleEvent {

    int day;

    int seconds;

    DailyEvent(Action action, int i, int j) {
        super(action);
        day = i;
        seconds = j;
        if (seconds < 0) {
            seconds = TextUtils.DAY_SECONDS + seconds;
            day--;
            if (day < 0) {
                day = 6;
            }
        }
    }

    DailyEvent(Action action, int i, int j, long l) {
        this(action, i, j);
        lastTime = l;
    }

    long calculateNextTime(long l) {
        Date date = new Date(l);
        Date date1 = new Date(date.getYear(), date.getMonth(), date.getDate());
        date1.setTime(date1.getTime() + (long) seconds * 1000L);
        for (; date1.getTime() <= date.getTime() || date1.getDay() != day; date1
                .setTime(date1.getTime() + 0x5265c00L)) {
        }
        if (lastTime > 0L && time == 0L && date.getDay() == day) {
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
