/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * GreaterEqualTime.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>GreaterEqualTime</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.recursionsw.jgl.BinaryPredicate;

// Referenced classes of package com.dragonflow.SiteView:
// ScheduleEvent

public class GreaterEqualTime implements BinaryPredicate<Object, Object> {

    public GreaterEqualTime() {
    }

    public boolean execute(Object obj, Object obj1) {
        return ((ScheduleEvent) obj).getTime() >= ((ScheduleEvent) obj1).getTime();
    }
}
