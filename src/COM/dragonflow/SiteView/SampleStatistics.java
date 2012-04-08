/*
 * 
 * Created on 2005-2-16 16:31:19
 *
 * SampleStatistics.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SampleStatistics</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor
public class SampleStatistics {

    String worstCategory;

    String lastCategory;

    int sampleCount;

    float minimum;

    float maximum;

    float total;

    double totalSquared;

    int totalSamples;

    String totalValue;

    public SampleStatistics() {
        worstCategory = Monitor.NODATA_CATEGORY;
        lastCategory = Monitor.NODATA_CATEGORY;
        sampleCount = 0;
        minimum = (0.0F / 0.0F);
        maximum = (0.0F / 0.0F);
        total = (0.0F / 0.0F);
        totalSquared = (0.0D / 0.0D);
        totalSamples = 0;
        totalValue = "";
    }

    int getSampleCount() {
        return sampleCount;
    }

    float getTotal() {
        return total;
    }

    double getTotalSquared() {
        return totalSquared;
    }

    public int getTotalSamples() {
        return totalSamples;
    }

    public String getTotalValue() {
        if (totalValue.length() == 0) {
            return "n/a";
        } else {
            return totalValue;
        }
    }

    public float getAverage() {
        if (Float.isNaN(total) || totalSamples == 0) {
            return (0.0F / 0.0F);
        } else {
            return total / (float) totalSamples;
        }
    }

    public float getStandardDeviation() {
        if (Float.isNaN(total) || totalSamples < 2)
            return Float.NaN;
        float f;
        try {
            double d = Math
                    .sqrt((totalSquared - (double) (total * total / (float) totalSamples))
                            / (double) (totalSamples - 1));
            f = (float) d;
        } catch (ArithmeticException arithmeticexception) {
            return Float.NaN;
        }
        return f;
    }

    public float getMaximum() {
        return maximum;
    }

    public float getMinimum() {
        return minimum;
    }

    String getWorstCategory() {
        return worstCategory;
    }
}
