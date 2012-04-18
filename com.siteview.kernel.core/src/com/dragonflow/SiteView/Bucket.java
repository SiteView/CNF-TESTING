/*
 * 
 * Created on 2005-2-15 12:29:18
 *
 * Bucket.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>Bucket</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.SiteView:
// SampleStatistics, Monitor, SampleCollector

class Bucket extends SampleStatistics {

    SampleCollector collector;

    Bucket(SampleCollector samplecollector) {
        collector = samplecollector;
    }

    void add(long l, String s, String s1, boolean flag) {
        sampleCount++;
        worstCategory = Monitor.getWorstCategory(worstCategory, s1);
        if (!flag) {
            if (collector.isNumeric()) {
                float f = TextUtils.toFloat(s);
                if (!Float.isNaN(f)) {
                    if (Float.isNaN(total)) {
                        total = 0.0F;
                    }
                    total += f;
                    totalSamples++;
                    if (Float.isNaN(minimum) || f < minimum) {
                        minimum = f;
                    }
                    if (Float.isNaN(maximum) || f > maximum) {
                        maximum = f;
                    }
                }
            } else if (worstCategory.equals(s1) || totalValue.length() == 0) {
                totalValue = s;
            }
        }
    }
}
