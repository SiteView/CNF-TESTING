/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class PerfResult {

    public String counterType;

    public long measurement;

    public long baseMeasurement;

    public long lastMeasurement;

    public long lastBaseMeasurement;

    public long measurementFreq;

    public long measurementTicks;

    public long measurementTime;

    public long lastMeasurementTime;

    public long lastMeasurementTicks;

    public boolean percent;

    public boolean perSec;

    public int precision;

    public float value;

    public PerfResult() {
        counterType = "";
        measurement = 0L;
        baseMeasurement = 0L;
        lastMeasurement = 0L;
        lastBaseMeasurement = 0L;
        measurementFreq = 0L;
        measurementTicks = 0L;
        measurementTime = 0L;
        lastMeasurementTime = 0L;
        lastMeasurementTicks = 0L;
        percent = false;
        perSec = false;
        precision = 0;
        value = (0.0F / 0.0F);
    }

    public void calcPerfResult() {
        long l = measurementTime - lastMeasurementTime;
        long l1 = measurementTicks - lastMeasurementTicks;
        float f = (float) l1 / (float) measurementFreq;
        long l2 = measurement - lastMeasurement;
        long l3 = baseMeasurement - lastBaseMeasurement;
        if (counterType.equals("PERF_COUNTER_COUNTER")) {
            if (l2 >= 0L) {
                if (lastMeasurement == 0L) {
                    value = 0.0F;
                } else {
                    value = (float) l2 / f;
                }
                perSec = true;
            }
        } else if (counterType.equals("PERF_COUNTER_TIMER")) {
            if (l2 >= 0L) {
                value = (float) l2 / (float) l1;
                percent = true;
            }
        } else if (counterType.equals("PERF_COUNTER_TIMER_INV")) {
            if (l2 >= 0L) {
                value = 1.0F - (float) l2 / f;
                percent = true;
            }
        } else if (counterType.equals("PERF_COUNTER_BULK_COUNT")) {
            if (l2 >= 0L) {
                value = (float) l2 / f;
                perSec = true;
            }
        } else if (counterType.equals("PERF_COUNTER_RAWCOUNT") || counterType.equals("PERF_COUNTER_LARGE_RAWCOUNT") || counterType.equals("PERF_COUNTER_RAWCOUNT_HEX") || counterType.equals("PERF_COUNTER_LARGE_RAWCOUNT_HEX")) {
            value = measurement;
            precision = 0;
        } else if (counterType.equals("PERF_ELAPSED_TIME")) {
            long l4 = measurement;
            if (l4 > measurementTicks || l4 < 0L) {
                l4 >>>= 32;
            }
            value = (measurementTicks - l4) / measurementFreq;
            precision = 0;
        } else if (counterType.equals("PERF_RAW_FRACTION")) {
            if (measurement == 0L) {
                value = 0.0F;
            } else {
                value = (float) measurement / (float) baseMeasurement;
            }
            percent = true;
        } else if (counterType.equals("PERF_SAMPLE_FRACTION")) {
            percent = true;
            if (l2 <= 0L || l3 <= 0L) {
                value = 0.0F;
            } else {
                value = (float) l2 / (float) l3;
            }
        } else if (counterType.equals("PERF_SAMPLE_COUNTER")) {
            if (l2 >= 0L && l3 > 0L) {
                value = (float) l2 / (float) l3;
            }
        } else if (counterType.equals("PERF_AVERAGE_TIME")) {
            value = (float) measurement / (float) baseMeasurement;
        } else if (counterType.equals("PERF_AVERAGE_BULK")) {
            if (l2 >= 0L && l3 > 0L) {
                value = (float) l2 / (float) l3;
            }
        } else if (counterType.equals("PERF_100NSEC_TIMER")) {
            if (l2 >= 0L) {
                value = (float) l2 / (float) l;
                percent = true;
            }
        } else if (counterType.equals("PERF_100NSEC_TIMER_INV")) {
            if (l2 >= 0L) {
                value = 1.0F - (float) l2 / (float) l;
                percent = true;
            }
        } else if (counterType.equals("PERF_COUNTER_MULTI_TIMER")) {
            if (l2 >= 0L) {
                value = (float) l2 / f / (float) baseMeasurement;
                percent = true;
            }
        } else if (counterType.equals("PERF_COUNTER_MULTI_TIMER_INV")) {
            if (l2 >= 0L) {
                value = 1.0F - (float) l2 / f / (float) baseMeasurement;
                percent = true;
            }
        } else if (counterType.equals("PERF_100NSEC_MULTI_TIMER")) {
            if (l2 >= 0L) {
                value = (float) l2 / (float) l / (float) baseMeasurement;
                percent = true;
            }
        } else if (counterType.equals("PERF_100NSEC_MULTI_TIMER_INV")) {
            if (l2 >= 0L) {
                value = 1.0F - (float) l2 / (float) l / (float) baseMeasurement;
                percent = true;
            }
        } else if (counterType.equals("PERF_COUNTER_QUEUELEN_TYPE")) {
            value = ((float) lastMeasurement + (float) (measurementTime * measurement)) / (float) l;
        } else if (counterType.equals("PERF_COUNTER_LARGE_QUEUELEN_TYPE")) {
            value = ((float) lastMeasurement + (float) (measurementTime * measurement)) / (float) l;
        } else if (counterType.equals("PERF_PRECISION_100NSEC_TIMER") && l2 >= 0L) {
            value = (float) l2 / (float) l3;
            percent = true;
        }
        if (percent) {
            precision = 2;
            value = value * 100F;
        }
    }
}
