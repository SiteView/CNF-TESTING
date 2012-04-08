/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Chart;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Chart:
// Chart, DataSet, DrawerGD
public class BarChart extends COM.dragonflow.Chart.Chart {

    static final int SPACING = 2;

    public BarChart(int i, int j, java.util.Hashtable hashtable, COM.dragonflow.SiteView.HistoryReport historyreport) {
        super(i, j, hashtable, historyreport);
    }

    boolean drawErrorBar() {
        return false;
    }

    int getOffset() {
        return (width - 2) / (dataSet[0].bucketCount * 2) - 2;
    }

    void drawData() {
        COM.dragonflow.Chart.DataSet dataset = dataSet[0];
        dataset.sampleStart();
        COM.dragonflow.Chart.DataSet dataset1 = null;
        if (maxDataSet != null) {
            dataset1 = maxDataSet[0];
            dataset1.sampleStart();
        }
        int i = 0;
        if (dataset.bucketCount > 0) {
            i = (width + 2) / dataset.bucketCount;
        }
        if ((i -= 2) < 1) {
            i = 1;
        }
        float f = (float) (width - i) / (float) (endTime - startTime);
        do {
            if (!dataset.nextSample()) {
                break;
            }
            if (dataset.sampleTime < startTime) {
                continue;
            }
            if (dataset.sampleTime > endTime) {
                break;
            }
            int j = java.lang.Math.round((float) (dataset.sampleTime - startTime) * f);
            float f1 = dataset.sampleValue;
            if (f1 > vertMax) {
                f1 = vertMax;
            }
            int k = java.lang.Math.round(f1 * dataset.scale);
            int l = 0;
            if (dataset1 != null && dataset1.nextSample()) {
                float f2 = dataset1.sampleValue;
                if (f2 > vertMax) {
                    f2 = vertMax;
                }
                l = java.lang.Math.round(f2 * dataset.scale);
                if (l == k) {
                    l = 0;
                }
            }
            java.awt.Color color;
            if (dataset.isOKSample()) {
                color = report.goodRGB;
            } else if (dataset.isDisabledSample()) {
                color = report.disabledRGB;
            } else if (dataset.isErrorCategorySample()) {
                color = report.errorRGB;
            } else if (dataset.isWarningSample()) {
                color = report.warningRGB;
            } else {
                color = report.nodataRGB;
            }
            if (k < 2) {
                k = 2;
            }
            drawer.setPenColor(color);
            drawer.fillRectangle(translateX(j), translateY(k), i, k);
            if (l > 0) {
                drawer.setPenColor(report.maxRGB);
                drawer.fillRectangle(translateX(j), translateY(l), i, l - k);
            }
        } while (true);
        drawer.setPenColor(java.awt.Color.black);
    }
}
