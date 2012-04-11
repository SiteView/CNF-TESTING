/*
 * 
 * Created on 2005-2-16 16:30:48
 *
 * SampleCollector.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SampleCollector</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;

import jgl.Array;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PercentProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SampleStatistics, Bucket, Monitor, SiteViewObject

public class SampleCollector extends SampleStatistics {

    Monitor monitor;

    StringProperty property;

    int propertyIndex;

    String idString;

    long startTime;

    long endTime;

    long precision;

    long actualStartTime;

    long actualEndTime;

    String lastValue;

    long lastTime;

    long minimumTime;

    long maximumTime;

    int errorTime;

    int warningTime;

    int goodTime;

    int naTime;

    int totalTime;

    static boolean carryBuckets = false;

    StringBuffer sampleBuffer;

    long bufferLastTime;

    boolean averagedSamples;

    StringBuffer maxSampleBuffer;

    boolean warningNotIncluded;

    boolean failureNotIncluded;

    static final boolean saveErrors = true;

    boolean showFullMonitorName;

    boolean debug;

    Bucket buckets[];

    SampleCollector() {
        startTime = 0L;
        endTime = 0L;
        precision = 0L;
        actualStartTime = 0L;
        actualEndTime = 0L;
        lastValue = "n/a";
        lastTime = 0L;
        minimumTime = 0L;
        maximumTime = 0L;
        errorTime = 0;
        warningTime = 0;
        goodTime = 0;
        naTime = 0;
        totalTime = 0;
        sampleBuffer = null;
        bufferLastTime = 0L;
        averagedSamples = false;
        maxSampleBuffer = null;
        warningNotIncluded = false;
        failureNotIncluded = false;
        showFullMonitorName = false;
        debug = false;
        buckets = null;
    }

    SampleCollector(Monitor monitor1, StringProperty stringproperty,
            boolean flag, boolean flag1, boolean flag2) {
        this(monitor1, stringproperty);
        warningNotIncluded = flag1;
        failureNotIncluded = flag2;
        if (flag) {
            sampleBuffer = new StringBuffer();
        }
        String s = System.getProperty("SampleCollector.debug");
        if (s != null && s.length() > 0) {
            debug = true;
        }
    }

    public SampleCollector(Monitor monitor1, StringProperty stringproperty) {
        startTime = 0L;
        endTime = 0L;
        precision = 0L;
        actualStartTime = 0L;
        actualEndTime = 0L;
        lastValue = "n/a";
        lastTime = 0L;
        minimumTime = 0L;
        maximumTime = 0L;
        errorTime = 0;
        warningTime = 0;
        goodTime = 0;
        naTime = 0;
        totalTime = 0;
        sampleBuffer = null;
        bufferLastTime = 0L;
        averagedSamples = false;
        maxSampleBuffer = null;
        warningNotIncluded = false;
        failureNotIncluded = false;
        showFullMonitorName = false;
        debug = false;
        buckets = null;
        monitor = monitor1;
        property = stringproperty;
        Array array = monitor1.getLogProperties();
        propertyIndex = array.indexOf(stringproperty) + 1;
        idString = monitor1.getFullID() + SiteViewObject.ID_SEPARATOR
                + TextUtils.numberToString(stringproperty.getOrder())
                + stringproperty.getName();
        showFullMonitorName = monitor1.getSetting("_fullMonitorName").length() > 0;
    }

    public void createBuckets(long l, long l1, long l2) {
        startTime = l;
        endTime = l1;
        precision = l2;
        long l3 = l1 - l;
        long l4 = l3 / l2 + 1L;
        buckets = new Bucket[(int) l4];
        for (int i = 0; (long) i < l4; i++) {
            buckets[i] = new Bucket(this);
        }

    }

    public Monitor getMonitor() {
        return monitor;
    }

    public String getPropertyLabel() {
        if (monitor != null) {
            return monitor.GetPropertyLabel(property);
        } else {
            return property.getLabel();
        }
    }

    public String getMonitorName() {
        String s = "";
        if (showFullMonitorName) {
            s = CGI.getGroupFullName(monitor.getProperty(Monitor.pGroupID));
            s = s + ": " + monitor.getProperty(Monitor.pName);
        } else {
            s = monitor.getProperty(Monitor.pName);
        }
        return s;
    }

    public String getMonitorDescription() {
        return monitor.getProperty(Monitor.pDescription);
    }

    public String getMonitorFullID() {
        return monitor.getProperty(Monitor.pFullID);
    }

    public String getMonitorTitle() {
        Object obj = monitor.getClassProperty("title");
        if (obj != null) {
            return (String) obj;
        } else {
            return "";
        }
    }

    public StringProperty getProperty() {
        return property;
    }

    long getMaximumTime() {
        return maximumTime;
    }

    long getMinimumTime() {
        return minimumTime;
    }

    String getLastCategory() {
        return lastCategory;
    }

    String getLastValue() {
        return lastValue;
    }

    long getLastTime() {
        return lastTime;
    }

    int getErrorTime() {
        return errorTime;
    }

    int getWarningTime() {
        return warningTime;
    }

    int getGoodTime() {
        return goodTime;
    }

    int getNaTime() {
        return naTime;
    }

    int getTotalTime() {
        return totalTime;
    }

    public int getBucketCount() {
        return buckets.length;
    }

    public String getIDString() {
        return idString;
    }

    public boolean isNumeric() {
        return property instanceof NumericProperty;
    }

    public boolean isPercentage() {
        return property instanceof PercentProperty;
    }

    long getStartTime() {
        return actualStartTime;
    }

    long getEndTime() {
        return actualEndTime;
    }

    public long getBucketStartTime(int i) {
        return startTime + (long) i
                * ((endTime - startTime) / (long) (buckets.length - 1));
    }

    public int getSampleCount(int i) {
        return buckets[i].sampleCount;
    }

    public float getTotal(int i) {
        return buckets[i].total;
    }

    public float getTotalSamples(int i) {
        return (float) buckets[i].totalSamples;
    }

    public String getTotalValue(int i) {
        String s = buckets[i].totalValue;
        if (s.length() == 0) {
            s = "n/a";
        }
        return s;
    }

    public String getTotalValueCarry(int i) {
        String s = getNonEmptyBucket(i).totalValue;
        if (s.length() == 0) {
            s = "n/a";
        }
        return s;
    }

    public String getWorstCategory(int i) {
        return buckets[i].worstCategory;
    }

    public String getWorstCategoryCarry(int i) {
        return getNonEmptyBucket(i).worstCategory;
    }

    public float getAverage(int i) {
        return buckets[i].getAverage();
    }

    public float getAverageCarry(int i) {
        return getNonEmptyBucket(i).getAverage();
    }

    public float getMaximum(int i) {
        return buckets[i].maximum;
    }

    public float getMaximumCarry(int i) {
        return getNonEmptyBucket(i).maximum;
    }

    public float getMinimum(int i) {
        return buckets[i].minimum;
    }

    public float getMinimumCarry(int i) {
        return getNonEmptyBucket(i).minimum;
    }

    Bucket getNonEmptyBucket(int i) {
        while (buckets[i].sampleCount == 0 && --i >= 0)
            ;
        return buckets[i];
    }

    void add(long l, String as[], int i, String s, String s1) {
        add(l, as, i, s, s1, false);
    }

    void add(long l, String as[], int i, String s, String s1, boolean flag) {
        if (propertyIndex < i) {
            add(l, as[propertyIndex], s, s1, flag);
        }
    }

    void add(long l, String s, String s1, String s2, boolean flag) {
        if (s2 == null) {
            s2 = new String("");
        }
        if (actualStartTime == 0L) {
            actualStartTime = l;
        }
        actualEndTime = l;
        int i = (int) ((l - startTime) / precision);
        if (i < 0 || i >= buckets.length) {
            System.out.println("BUCKET NUMBER ERROR: " + i);
            return;
        }
        boolean flag1 = false;
        if (!flag) {
            flag1 = s1.equals(Monitor.DISABLED_CATEGORY)
                    || s1.equals(Monitor.FILTERED_CATEGORY);
        } else {
            flag1 = getLastCategory().equals(Monitor.DISABLED_CATEGORY)
                    || getLastCategory().equals(Monitor.FILTERED_CATEGORY);
        }
        buckets[i].add(l, s, s1, flag1);
        int j = 0;
        if (lastTime > 0L) {
            j = (int) (l - lastTime);
            if (!flag1) {
                totalTime += j;
            }
        }
        if (!flag) {
            calculate(s1, j, s2);
        } else {
            calculate(getLastCategory(), j, s2);
        }
        if (debug) {
            Monitor _tmp = monitor;
            TextUtils.debugPrint("SampleCollector.add() monitor.Name='"
                    + monitor.getProperty(Monitor.pName)
                    + "' warningNotIncluded='" + warningNotIncluded
                    + "' failureNotIncluded='" + failureNotIncluded
                    + "' errorType='" + s2 + "' category='" + s1
                    + "' interval='" + j + "' errorTime='" + errorTime
                    + "' warningTime='" + warningTime + "' goodTime='"
                    + goodTime + "' naTime='" + naTime, null);
        }
        if (!s1.equals(lastCategory)) {
            worstCategory = Monitor.getWorstCategory(worstCategory, s1);
        }
        lastTime = l;
        lastValue = s;
        lastCategory = s1;
        sampleCount++;
        if (!flag1) {
            if (isNumeric()) {
                float f = TextUtils.toFloat(s);
                if (!Float.isNaN(f)) {
                    if (Float.isNaN(maximum) || f > maximum) {
                        maximum = f;
                        maximumTime = l;
                    }
                    if (Float.isNaN(minimum) || f < minimum) {
                        minimum = f;
                        minimumTime = l;
                    }
                    if (Float.isNaN(total)) {
                        total = 0.0F;
                        totalSquared = 0.0D;
                    }
                    total += f;
                    totalSquared += Math.pow(f, 2D);
                    totalSamples++;
                }
            } else if (totalValue.length() == 0
                    || !worstCategory.equals(Monitor.getWorstCategory(
                            worstCategory, s1))) {
                totalValue = s;
            }
            if (sampleBuffer != null) {
                if (bufferLastTime == 0L) {
                    sampleBuffer
                            .append(TextUtils.dateToString(l * 1000L) + "$");
                } else {
                    sampleBuffer.append('|');
                    sampleBuffer.append(l - bufferLastTime);
                }
                bufferLastTime = l;
                sampleBuffer.append(s1.charAt(0));
                sampleBuffer.append(property.valueOnlyString(s));
            }
        }
    }

    private void calculate(String s, int i, String s1) {
        if (s.indexOf(Monitor.ERROR_CATEGORY) >= 0) {
            if (s1.equals(Monitor.NON_FAILURE) || !failureNotIncluded) {
                errorTime += i;
            }
        } else if (s.equals(Monitor.WARNING_CATEGORY)) {
            if (warningNotIncluded) {
                goodTime += i;
            } else {
                warningTime += i;
            }
        } else if (s.equals(Monitor.GOOD_CATEGORY)) {
            goodTime += i;
        } else if (s.equals(Monitor.NODATA_CATEGORY)) {
            naTime += i;
        }
    }

    void clearSampleBuffer() {
        sampleBuffer = null;
    }

    StringBuffer getMaxSampleBuffer() {
        getSampleBuffer();
        return maxSampleBuffer;
    }

    StringBuffer getSampleBuffer() {
        if (sampleBuffer == null) {
            averagedSamples = true;
            sampleBuffer = new StringBuffer();
            maxSampleBuffer = new StringBuffer();
            for (int i = 0; i < buckets.length; i++) {
                long l = getBucketStartTime(i);
                if (bufferLastTime == 0L) {
                    sampleBuffer
                            .append(TextUtils.dateToString(l * 1000L) + "$");
                    maxSampleBuffer.append(TextUtils.dateToString(l * 1000L)
                            + "$");
                } else {
                    sampleBuffer.append('|');
                    sampleBuffer.append(l - bufferLastTime);
                    maxSampleBuffer.append('|');
                    maxSampleBuffer.append(l - bufferLastTime);
                }
                bufferLastTime = l;
                sampleBuffer.append(buckets[i].worstCategory.charAt(0));
                maxSampleBuffer.append(buckets[i].worstCategory.charAt(0));
                if (isNumeric()) {
                    sampleBuffer.append(property.valueOnlyString(buckets[i]
                            .getAverage()));
                    maxSampleBuffer.append(property.valueOnlyString(buckets[i]
                            .getMaximum()));
                } else {
                    sampleBuffer.append(buckets[i].totalValue);
                    maxSampleBuffer.append(buckets[i].totalValue);
                }
            }

        }
        return sampleBuffer;
    }

    boolean samplesAreAveraged() {
        return averagedSamples;
    }

    public String toString() {
        return idString;
    }

    public boolean equals(Object obj) {
        return (obj instanceof SampleCollector)
                && idString.equals(((SampleCollector) obj).getIDString());
    }

    public int hashCode() {
        return idString.hashCode();
    }

    public void print() {
        print(true);
    }

    public void print(boolean flag) {
        TextUtils.debugPrint("Monitor: " + monitor.getProperty(Monitor.pName)
                + "    Property: " + property.getName(), null);
        TextUtils.debugPrint("Buckets: " + getSampleCount(), null);
        TextUtils.debugPrint("Total Samples: " + getTotalSamples(), null);
        TextUtils.debugPrint("Total: " + getTotal(), null);
        TextUtils.debugPrint("Total Time: " + getTotalTime(), null);
        TextUtils.debugPrint("Computed Total Time: "
                + (actualEndTime - actualStartTime), null);
        TextUtils.debugPrint("Computed Time Totals: "
                + (errorTime + warningTime + goodTime + naTime), null);
        TextUtils.debugPrint("Good Time: " + getGoodTime(), null);
        TextUtils.debugPrint("Error Time: " + getErrorTime(), null);
        TextUtils.debugPrint("Warning Time: " + getWarningTime(), null);
        TextUtils.debugPrint("Average: " + getAverage(), null);
        TextUtils.debugPrint("Maximum: " + getMaximum(), null);
        TextUtils.debugPrint("Max Time: " + new Date(getMaximumTime() * 1000L),
                null);
        TextUtils.debugPrint("Minimum: " + getMinimum(), null);
        TextUtils.debugPrint("Worst: " + getWorstCategory(), null);
        if (flag) {
            for (int i = 0; i < getBucketCount(); i++) {
                TextUtils.debugPrint(" Bucket StartTime: "
                        + new Date(getBucketStartTime(i) * 1000L) + "    ",
                        null);
                TextUtils.debugPrint("SC=" + getSampleCount(i) + "   WC="
                        + getWorstCategory(i), null);
                if (isNumeric()) {
                    TextUtils.debugPrint("AVG=" + getAverage(i) + "  MAX="
                            + getMaximum(i), null);
                } else {
                    TextUtils.debugPrint("READ=" + getTotalValue(i), null);
                }
            }

        }
    }

}
