/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Chart;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Chart:
// Chart, DrawerGD, DataSet
public class LineChart extends com.dragonflow.Chart.Chart {

    public LineChart(int i, int j, java.util.Hashtable hashtable, com.dragonflow.SiteView.HistoryReport historyreport) {
        super(i, j, hashtable, historyreport);
    }

    void drawData() {
        java.awt.Color color = drawer.getPenColor();
        for (int i = dataSet.length - 1; i >= 0; i --) {
            drawData(dataSet[i]);
            if (maxDataSet[i] != null) {
                drawDashedData(maxDataSet[i]);
            }
        }

        drawer.setPenColor(color);
    }

    void drawData(com.dragonflow.Chart.DataSet dataset) {
        drawData(dataset, false, true);
    }

    void drawDashedData(com.dragonflow.Chart.DataSet dataset) {
        drawData(dataset, true, false);
    }

    void drawData(com.dragonflow.Chart.DataSet dataset, boolean flag, boolean flag1) {
        int i = -1;
        int j = -1;
        int k = ((-5 - horizontalUnitsHeight) + drawer.stringHeight()) - 1 - dataset.index * 4;
        int l = -1;
        drawer.setPenColor(dataset.color);
        dataset.sampleStart();
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
            int i1 = java.lang.Math.round((float) (dataset.sampleTime - startTime) * timeScale);
            if (l >= 0) {
                drawer.drawLine(translateX(l), translateY(k - 1), translateX(i1), translateY(k - 1));
                drawer.drawRectangle(translateX(l + 1), translateY(k + 1), 3, 3);
            }
            if (dataset.isErrorSample()) {
                l = i1;
            } else {
                l = -1;
            }
            if (dataset.sampleHasValue) {
                float f = dataset.sampleValue;
                if (f > vertMax) {
                    f = vertMax;
                }
                int j1 = java.lang.Math.round(f * dataset.scale);
                if (dataset.isDisabledSample()) {
                    j1 = 0;
                }
                if (dataset.averagedSamples) {
                    if (i >= 0) {
                        if (flag) {
                            drawer.drawDashedLine(translateX(i), translateY(j), translateX(i1), translateY(j));
                            drawer.drawDashedLine(translateX(i1), translateY(j), translateX(i1), translateY(j1));
                        } else {
                            drawer.drawLine(translateX(i), translateY(j), translateX(i1), translateY(j));
                            drawer.drawLine(translateX(i1), translateY(j), translateX(i1), translateY(j1));
                        }
                    }
                } else if (i >= 0) {
                    if (flag) {
                        drawer.drawDashedLine(translateX(i), translateY(j), translateX(i1), translateY(j1));
                    } else {
                        drawer.drawLine(translateX(i), translateY(j), translateX(i1), translateY(j1));
                    }
                    if (dataset.isDisabledSample()) {
                        drawer.drawCross(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                    } else {
                        drawer.drawRectangle(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                    }
                } else if (dataset.isDisabledSample()) {
                    drawer.drawCross(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                } else {
                    drawer.drawRectangle(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                }
                i = i1;
                j = j1;
            } else {
                i = -1;
                j = -1;
            }
        } while (true);
    }
}
