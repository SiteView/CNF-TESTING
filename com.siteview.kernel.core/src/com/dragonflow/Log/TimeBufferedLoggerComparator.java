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

// Referenced classes of package com.dragonflow.Log:
// TimeBufferedLogger
class TimeBufferedLoggerComparator implements java.util.Comparator, java.io.Serializable {

    TimeBufferedLoggerComparator() {
    }

    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        if (obj.equals(obj1)) {
            return 0;
        }
        com.dragonflow.Log.TimeBufferedLogger timebufferedlogger = (com.dragonflow.Log.TimeBufferedLogger) obj;
        long l = timebufferedlogger.getExpiration().getTime();
        com.dragonflow.Log.TimeBufferedLogger timebufferedlogger1 = (com.dragonflow.Log.TimeBufferedLogger) obj1;
        long l1 = timebufferedlogger1.getExpiration().getTime();
        if (l < l1) {
            return -1;
        }
        if (l > l1) {
            return 1;
        } else {
            return timebufferedlogger.getUniqueID() - timebufferedlogger1.getUniqueID();
        }
    }
}
