/*
 * 
 * Created on 2005-2-16 16:26:43
 *
 * RollingAverage.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>RollingAverage</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.SiteView:
// Platform

public class RollingAverage {

    int numberOfBuckets;

    long bucketInterval;

    float bucketTotal[];

    int bucketCount[];

    long bucketStart[];

    float maxAverage;

    long maxAverageTime;

    float maxMax;

    long maxMaxTime;

    float maxCountPerTimePeriod;

    long maxCountPerTimePeriodTime;

    float overallTotal;

    int overallCount;

    int reportPeriodSeconds;

    int current;

    public RollingAverage(int i, long l) {
        numberOfBuckets = 10;
        bucketInterval = 60L;
        bucketTotal = null;
        bucketCount = null;
        bucketStart = null;
        maxAverage = 0.0F;
        maxAverageTime = 0L;
        maxMax = 0.0F;
        maxMaxTime = 0L;
        maxCountPerTimePeriod = 0.0F;
        maxCountPerTimePeriodTime = 0L;
        overallTotal = 0.0F;
        overallCount = 0;
        reportPeriodSeconds = 60;
        current = 0;
        numberOfBuckets = i;
        bucketInterval = l * 1000L;
        initializeAll(Platform.timeMillis());
    }

    private void initializeAll(long l) {
        bucketTotal = new float[numberOfBuckets];
        bucketCount = new int[numberOfBuckets];
        bucketStart = new long[numberOfBuckets];
        current = 0;
        initializeBucket(current, l);
        bucketStart[current] = l;
    }

    private void initializeBucket(int i, long l) {
        bucketStart[i] = l;
        bucketTotal[i] = 0.0F;
        bucketCount[i] = 0;
    }

    public void add(long l, long l1) {
        add(l, l1);
    }

    public void add(int i, long l) {
        add(i, l);
    }

    public void add(String s, long l) {
        add(TextUtils.toFloat(s), l);
    }

    public void increment(long l) {
        add(0, l);
    }

    public synchronized void add(float f, long l) {
        if (Float.isNaN(f)) {
            return;
        }
        int i = current;
        do {
            if (l < bucketStart[current] + bucketInterval) {
                break;
            }
            int j = (current + 1) % numberOfBuckets;
            if (j == i) {
                initializeAll(l);
                break;
            }
            initializeBucket(j, bucketStart[current] + bucketInterval);
            current = j;
        } while (true);
        bucketCount[current]++;
        bucketTotal[current] += f;
        overallCount++;
        overallTotal += f;
        if (f > maxMax) {
            maxMax = f;
            maxMaxTime = l;
        }
        float f1 = getAverage();
        if (f1 > maxAverage) {
            maxAverage = f1;
            maxAverageTime = l;
        }
        float f2 = getCountPerTimePeriod();
        if (f2 > maxCountPerTimePeriod) {
            maxCountPerTimePeriod = f2;
            maxCountPerTimePeriodTime = l;
        }
    }

    public synchronized float getRollingAverage() {
        float f = 0.0F;
        int i = 0;
        for (int j = 0; j < numberOfBuckets; j++) {
            if (bucketStart[j] != 0L) {
                f += bucketTotal[j];
                i += bucketCount[j];
            }
        }

        if (i == 0) {
            return (0.0F / 0.0F);
        } else {
            return f / (float) i;
        }
    }

    public synchronized float getMaximum() {
        return maxMax;
    }

    public synchronized long getMaximumTime() {
        return maxMaxTime;
    }

    public synchronized float getMaximumCountPerTimePeriod() {
        return maxCountPerTimePeriod;
    }

    public synchronized long getMaximumCountPerTimePeriodTime() {
        return maxCountPerTimePeriodTime;
    }

    public synchronized float getMaximumRollingAverage() {
        return maxAverage;
    }

    public synchronized float getAverage() {
        if (overallCount == 0) {
            return 0.0F;
        } else {
            return overallTotal / (float) overallCount;
        }
    }

    public synchronized float getCountPerTimePeriod() {
        return getCountPerTimePeriod(reportPeriodSeconds);
    }

    public synchronized float getCountPerTimePeriod(int i) {
        long l = 0L;
        long l1 = 0L;
        float f = 0.0F;
        for (int j = 0; j < numberOfBuckets; j++) {
            if (bucketStart[j] == 0L) {
                continue;
            }
            if (l == 0L || bucketStart[j] < l) {
                l = bucketStart[j];
            }
            long l2 = bucketStart[j] + bucketInterval;
            if (l1 == 0L || l2 > l1) {
                l1 = l2;
            }
            f += bucketCount[j];
        }

        if (l == 0L || l1 == 0L || l1 - l == 0L || i == 0) {
            return 0.0F;
        } else {
            float f1 = (float) (l1 - l) / (float) (i * 1000);
            return f / f1;
        }
    }

    public void print() {
        System.out.println("Number Of Buckets: " + numberOfBuckets);
        System.out.println("Bucket Interval (in ms): " + bucketInterval);
        System.out.println("Current Bucket: " + current);
        System.out.println("Max: " + maxMax);
        System.out.println("Max Per Time Period: " + maxCountPerTimePeriod);
        for (int i = 0; i < numberOfBuckets; i++) {
            System.out.println("Bucket " + i);
            System.out.println("   Count: " + bucketCount[i]);
            System.out.println("   Total: " + bucketTotal[i]);
            System.out.println("   Start: " + bucketStart[i]);
        }

    }

    public static void main(String args[]) {
        RollingAverage rollingaverage = new RollingAverage(3, 60L);
        long l = Platform.timeMillis();
        rollingaverage.increment(l + 20000L);
        rollingaverage.increment(l + 20000L);
        rollingaverage.increment(l + 20000L);
        rollingaverage.increment(l + 20000L);
        rollingaverage.increment(l + 0x13880L);
        rollingaverage.increment(l + 0x13880L);
        rollingaverage.increment(l + 0x13880L);
        rollingaverage.increment(l + 0x13880L);
        rollingaverage.increment(l + 0x13880L);
        rollingaverage.increment(l + 0x249f0L);
        rollingaverage.increment(l + 0x249f0L);
        rollingaverage.increment(l + 0x249f0L);
        rollingaverage.increment(l + 0x35b60L);
        rollingaverage.increment(l + 0x4baf0L);
        rollingaverage.increment(l + 0x4baf0L);
        rollingaverage.increment(l + 0x4baf0L);
        rollingaverage.print();
        System.out.println("Average Per Minute: "
                + rollingaverage.getCountPerTimePeriod(60));
    }
}