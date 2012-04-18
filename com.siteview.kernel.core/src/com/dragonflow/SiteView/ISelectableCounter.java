/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * ISelectableCounter.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>ISelectableCounter</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
// Referenced classes of package com.dragonflow.SiteView:
// AtomicMonitor
public interface ISelectableCounter {

    public static final String COUNTER_LABEL_PREFIX = "Counter ";

    public static final String COUNTER_LABEL_SUFFIX = " Value";

    public static final String COUNTER_NAME_PREFIX = "value";

    public static final String MEASUREMENT_NAME_PREFIX = "measurement";

    public static final String LAST_MEASUREMENT_NAME_PREFIX = "lastMeasurement";

    public static final String LAST_BASE_MEASUREMENT_NAME_PREFIX = "lastBaseMeasurement";

    public abstract String getDefaultCounters();

    public abstract void increaseCounters(int i);

    public abstract void setCountersPropertyValue(AtomicMonitor atomicmonitor,
            String s);
}
