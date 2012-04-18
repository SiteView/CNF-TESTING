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

import java.awt.Color;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

// Referenced classes of package com.dragonflow.Chart:
// DataSet, DrawerGD

public abstract class Chart {

    static final int VERTICAL_TICK_COUNT = 11;

    static final int HORIZONTAL_TICK_COUNT = 9;

    static final int ERROR_BAR_HEIGHT = 3;

    static final int ERROR_BAR_SPACE = 1;

    int leftPad;

    int rightPad;

    int topPad;

    int bottomPad;

    int errorBarHeight;

    int horizontalUnitsHeight;

    int yAxesSlop;

    int topAxesSlop;

    int width;

    int height;

    java.awt.Color lightGray;

    java.awt.Color mediumGray;

    java.awt.Color colorList[] = { new Color(0, 0, 255), new Color(0, 204, 0), new Color(204, 0, 0), new Color(51, 51, 51), new Color(255, 102, 51), new Color(153, 0, 51), new Color(51, 204, 255), new Color(102, 102, 0), new Color(255, 51, 204),
            new Color(255, 204, 153), new Color(255, 204, 0), new Color(102, 51, 0), new Color(204, 102, 102), new Color(153, 255, 0), new Color(102, 102, 102), new Color(102, 51, 255), new Color(153, 153, 255), new Color(51, 0, 204),
            new Color(0, 102, 102) };

    long startTime;

    long endTime;

    long timeOffset;

    float vertMax;

    float timeScale;

    int imageHeight;

    int imageWidth;

    com.dragonflow.Chart.DrawerGD drawer;

    com.dragonflow.Chart.DataSet dataSet[];

    com.dragonflow.Chart.DataSet maxDataSet[];

    java.util.Hashtable properties;

    com.dragonflow.SiteView.HistoryReport report;

    int bottomLabels;

    public static String AVG_PREFIX = "Avg: ";

    public static String MAX_PREFIX = "Max: ";

    public static String PEAK_LABEL = "Maximum: ";

    public static String AVERAGE_LABEL = "Average: ";

    public static String LOW_LABEL = "Low: ";

    public static String ERROR_TIME_LABEL = "Time in Error: ";

    public static String ERROR_PERCENTAGE_LABEL = "Error %: ";

    public String getValue(String s) {
        String s1 = (String) properties.get(s);
        if (s1 == null) {
            return "";
        } else {
            return s1;
        }
    }

    public com.dragonflow.Chart.DrawerGD getDrawer() {
        return drawer;
    }

    public Chart(int i, int j, java.util.Hashtable hashtable, com.dragonflow.SiteView.HistoryReport historyreport) {
        leftPad = 0;
        rightPad = 0;
        topPad = 0;
        bottomPad = 0;
        errorBarHeight = 0;
        horizontalUnitsHeight = 0;
        yAxesSlop = 2;
        topAxesSlop = 5;
        width = 100;
        height = 100;
        lightGray = new Color(204, 204, 204);
        mediumGray = new Color(102, 102, 102);
        startTime = 0L;
        endTime = 0L;
        timeOffset = 0L;
        vertMax = 100F;
        timeScale = 1.0F;
        drawer = null;
        dataSet = null;
        maxDataSet = null;
        properties = new Hashtable();
        report = null;
        bottomLabels = 60;
        properties = hashtable;
        report = historyreport;
        startTime = com.dragonflow.Utils.TextUtils.stringToDate(getValue("startTime")).getTime();
        endTime = com.dragonflow.Utils.TextUtils.stringToDate(getValue("endTime")).getTime();
        timeOffset = com.dragonflow.Utils.TextUtils.toLong(getValue("timeOffset")) * 1000L;
        imageWidth = i;
        imageHeight = j;
        java.util.Vector vector = new Vector();
        for (int k = 1; k <= 100 && getValue("name" + k).length() != 0; k ++) {
            vector.addElement(getValue("data" + k));
        }

        dataSet = new com.dragonflow.Chart.DataSet[vector.size()];
        maxDataSet = new com.dragonflow.Chart.DataSet[vector.size()];
        for (int l = 0; l < dataSet.length; l ++) {
            dataSet[l] = new DataSet((String) vector.elementAt(l));
            dataSet[l].index = l;
            dataSet[l].name = getValue("name" + (l + 1));
            dataSet[l].average = getValue("average" + (l + 1));
            dataSet[l].maximum = getValue("maximum" + (l + 1));
            dataSet[l].maximumTime = getValue("maximumTime" + (l + 1));
            dataSet[l].minimum = getValue("minimum" + (l + 1));
            dataSet[l].minimumTime = getValue("minimumTime" + (l + 1));
            dataSet[l].errorTime = getValue("errorTime" + (l + 1));
            dataSet[l].errorPercentage = getValue("errorPercentage" + (l + 1));
            dataSet[l].color = getColor(l);
            dataSet[l].averagedSamples = getValue("averagedSamples" + (l + 1)).equalsIgnoreCase("true");
            dataSet[l].bucketCount = com.dragonflow.Utils.TextUtils.toInt(getValue("bucketCount" + (l + 1)));
            String s1 = getValue("maxdata" + (l + 1));
            if (s1.length() > 0) {
                maxDataSet[l] = new DataSet(s1);
                maxDataSet[l].index = l;
                maxDataSet[l].color = getColor(l);
                maxDataSet[l].averagedSamples = getValue("averagedSamples" + (l + 1)).equalsIgnoreCase("true");
            }
        }

        String s = getValue("vertMax");
        if (s.length() > 0) {
            vertMax = com.dragonflow.Utils.TextUtils.toFloat(s);
        }
        drawChart();
        drawer.endDraw();
    }

    boolean drawErrorBar() {
        return true;
    }

    public void drawChart() {
        horizontalUnitsHeight = 65;
        rightPad = 40;
        topPad = 60;
        int i = 35 + com.dragonflow.Utils.TextUtils.floatToFormattedString(vertMax, 3).length() * 7;
        if (i < 65) {
            i = 65;
        }
        leftPad = i;
        if (leftPad > 65) {
            imageWidth = (imageWidth + leftPad) - 65;
        }
        drawer = new DrawerGD(imageWidth, imageHeight);
        drawer.startDraw();
        drawer.setFont("Helvetica", "plain", 10);
        bottomPad = horizontalUnitsHeight;
        computeTitleHeight();
        if (drawErrorBar()) {
            errorBarHeight = dataSet.length * 4;
            errorBarHeight = java.lang.Math.max(drawer.stringHeight() * 2, errorBarHeight);
        }
        if (dataSet.length == 1) {
            bottomPad += errorBarHeight;
        } else {
            bottomPad += (drawer.stringHeight() + 3) * dataSet.length + 5 + errorBarHeight;
        }
        height = imageHeight - (topPad + bottomPad);
        width = imageWidth - (leftPad + rightPad);
        drawer.setPenColor(report.backgroundRGB);
        drawer.fillRectangle(0, 0, imageWidth, imageHeight);
        drawer.setPenColor(java.awt.Color.white);
        drawer.fillRectangle(leftPad, topPad - topAxesSlop, width, height + topAxesSlop);
        drawer.setPenColor(mediumGray);
        drawer.drawRectangle(0, 0, imageWidth - 1, imageHeight - 1);
        drawer.setPenColor(java.awt.Color.black);
        computeScale();
        drawTitle();
        drawVerticalUnits();
        drawHorizontalUnits();
        drawData();
        drawAverage();
        drawAxes();
    }

    void drawAverage() {
        if (dataSet.length == 1) {
            drawer.setPenColor(mediumGray);
            float f = com.dragonflow.Utils.TextUtils.readFloat(dataSet[0].average, 0);
            if (f > vertMax) {
                f = vertMax;
            }
            int i = java.lang.Math.round(f * dataSet[0].scale);
            if (i > 0) {
                drawer.drawDashedLine(translateX(0), translateY(i), translateX(width), translateY(i));
            }
            drawer.setPenColor(java.awt.Color.black);
        }
    }

    void setStartTime(long l) {
        startTime = l;
    }

    void setEndTime(long l) {
        endTime = l;
    }

    void computeTitleHeight() {
        drawer.saveFont();
        drawer.setFont("Helvetica", "bold", 12);
        if (getValue("title").length() > 0) {
            topPad = drawer.stringHeight() * 5;
            if (getValue("subtitle").length() > 0) {
                drawer.setFont("Helvetica", "plain", 9);
                topPad += drawer.stringHeight() * 2;
            }
        }
        drawer.restoreFont();
    }

    void drawTitle() {
        drawer.saveFont();
        int i = drawer.stringHeight() + 5;
        String s = getValue("subtitle");
        if (s.length() > 0) {
            drawer.setFont("Helvetica", "plain", 9);
            i += drawer.stringHeight() + 3;
            drawer.drawStringJustified(imageWidth / 2, i, s, 1);
        }
        if (dataSet[0].errorTime.length() > 0) {
            drawer.setFont("Helvetica", "bold", 12);
            com.dragonflow.Chart.DataSet dataset = dataSet[0];
            String s1 = PEAK_LABEL + dataset.maximum + "  " + AVERAGE_LABEL + dataset.average;
            int j = drawer.stringHeight() + 3;
            drawer.drawStringJustified(imageWidth / 2, topPad - j * 2, s1, 1);
            String s2 = ERROR_TIME_LABEL + dataset.errorTime + "  " + ERROR_PERCENTAGE_LABEL + dataset.errorPercentage;
            drawer.drawStringJustified(imageWidth / 2, topPad - j, s2, 1);
            drawer.setFont("Helvetica", "plain", 10);
            drawer.setPenColor(java.awt.Color.black);
            drawer.restoreFont();
        }
        drawer.restoreFont();
    }

    void drawAxes() {
        drawer.drawLine(translateX(-yAxesSlop), translateY(0), translateX(width + yAxesSlop), translateY(0));
        drawer.drawLine(translateX(-yAxesSlop), translateY(0), translateX(-yAxesSlop), translateY(height + topAxesSlop));
        drawer.drawLine(translateX(-yAxesSlop), translateY(height + topAxesSlop), translateX(width + yAxesSlop), translateY(height + topAxesSlop));
        drawer.drawLine(translateX(width + yAxesSlop), translateY(0), translateX(width + yAxesSlop), translateY(height + topAxesSlop));
    }

    int getOffset() {
        return 0;
    }

    void drawHorizontalUnits() {
        int i = getOffset();
        int j = width - 2 * i;
        int k = j / (3 * drawer.stringHeight() + 2) + 1;
        int l = dataSet[0].bucketCount;
        int i1;
        for (i1 = l + (l != 0 ? 0 : 9); i1 > k; i1 = i1 / 2 + i1 % 2) {
        }
        float f = (float) j / (float) (i1 - 1);
        boolean flag = false;
        float f1 = i;
        boolean flag1 = endTime - startTime > 0x5265c00L;
        int k1 = drawer.stringHeight();
        int l1 = translateY((-5 - horizontalUnitsHeight) + k1);
        for (int i2 = 0; i2 < i1; i2 ++) {
            long l2 = startTime + ((endTime - startTime) / (long) (i1 - 1)) * (long) i2;
            int j1 = java.lang.Math.round(f1);
            drawer.drawLine(translateX(j1), translateY(0), translateX(j1), translateY(-3));
            int i3 = j1 - 3;
            if (i2 == i1 - 1) {
                i3 -= k1;
            }
            String s = com.dragonflow.Utils.TextUtils.prettyDateTime(new Date(l2 + timeOffset));
            drawer.drawStringUp(translateX(i3 + k1 / 2), translateY(-3 - drawer.stringWidth(s)), s);
            if (i2 == 0 || i2 == i1 - 1 || flag1) {
                String s1 = com.dragonflow.Utils.TextUtils.prettyDateDate(new Date(l2 + timeOffset));
                drawer.drawStringUp(translateX(i3 + k1 / 2 + drawer.stringHeight()), translateY(-3 - drawer.stringWidth(s1)), s1);
            }
            if (i2 > 0 && i2 < i1 - 1 || i > 0) {
                drawer.setPenColor(java.awt.Color.lightGray);
                drawer.drawLine(translateX(j1), l1, translateX(j1), l1 + errorBarHeight);
                drawer.drawLine(translateX(j1), translateY(0), translateX(j1), translateY(height + topAxesSlop));
                drawer.setPenColor(java.awt.Color.black);
            }
            f1 += f;
        }

        java.awt.Color color = drawer.getPenColor();
        if (drawErrorBar()) {
            drawer.drawString(translateX(-leftPad + 5), l1 + drawer.stringHeight(), "Error:");
            drawer.setPenColor(java.awt.Color.lightGray);
            drawer.drawLine(translateX(-yAxesSlop), l1, translateX(width + yAxesSlop), l1);
            drawer.drawLine(translateX(-yAxesSlop), l1 + errorBarHeight, translateX(width + yAxesSlop), l1 + errorBarHeight);
            drawer.drawLine(translateX(-yAxesSlop), l1, translateX(-yAxesSlop), l1 + errorBarHeight);
            drawer.drawLine(translateX(width + yAxesSlop), l1, translateX(width + yAxesSlop), l1 + errorBarHeight);
        }
        int j2 = -5 - (horizontalUnitsHeight + errorBarHeight);
        if (dataSet.length != 1) {
            int k2 = 0;
            int j3 = 0;
            for (int k3 = 0; k3 < dataSet.length; k3 ++) {
                int l3 = drawer.stringWidth(AVG_PREFIX + dataSet[k3].average);
                if (l3 > k2) {
                    k2 = l3;
                }
                l3 = drawer.stringWidth(MAX_PREFIX + dataSet[k3].maximum);
                if (l3 > j3) {
                    j3 = l3;
                }
            }

            byte byte0 = 5;
            byte byte1 = 20;
            int i4 = java.lang.Math.min(j3, 170);
            int j4 = imageWidth - (i4 + 5);
            int k4 = java.lang.Math.min(k2, 95);
            int l4 = j4 - (k4 + 10);
            int i5 = byte0 + byte1 + 5;
            int j5 = l4 - i5 - 10;
            for (int k5 = 0; k5 < dataSet.length; k5 ++) {
                drawer.setPenColor(dataSet[k5].color);
                drawer.fillRectangle(translateX(-leftPad + 5), translateY((j2 + drawer.stringHeight()) - 3), 20, 10);
                drawer.setPenColor(java.awt.Color.black);
                drawer.drawStringMaxWidth(i5, translateY(j2), dataSet[k5].name, j5);
                drawer.drawStringMaxWidth(l4, translateY(j2), AVG_PREFIX + dataSet[k5].average, k4);
                drawer.drawStringMaxWidth(j4, translateY(j2), MAX_PREFIX + dataSet[k5].maximum, i4);
                j2 -= drawer.stringHeight() + 3;
            }

            drawer.setPenColor(color);
        }
    }

    void drawVerticalUnits() {
        byte byte0 = 11;
        float f = (float) height / (float) (byte0 - 1);
        int i = -1 * (drawer.stringHeight() / 3);
        boolean flag = false;
        float f1 = 0.0F;
        byte byte1 = 0;
        if (vertMax < 10F) {
            byte1 = 2;
        }
        for (int k = 0; k < byte0; k ++) {
            float f2 = (vertMax / (float) (byte0 - 1)) * (float) k;
            int j = java.lang.Math.round(f1);
            if (k == byte0 - 1) {
                j = height;
            }
            drawer.drawLine(translateX(-5), translateY(j), translateX(-yAxesSlop), translateY(j));
            drawer.setPenColor(java.awt.Color.lightGray);
            drawer.drawLine(translateX(-yAxesSlop), translateY(j), translateX(width + yAxesSlop), translateY(j));
            drawer.setPenColor(java.awt.Color.black);
            if (k % 2 == 0) {
                drawer.drawStringJustified(translateX(-8), translateY(j + i), com.dragonflow.Utils.TextUtils.floatToFormattedString(f2, byte1), 2);
            }
            f1 += f;
        }

        String s = getValue("propertyName");
        if (s.length() > 0) {
            byte byte2 = 12;
            drawer.saveFont();
            int l = (imageHeight * 42) / 282 - 18;
            if (s.length() >= l) {
                String s1 = " ";
                if (s.indexOf(" : ") > 0) {
                    s1 = " : ";
                } else if (s.indexOf("/") > 0 && s.indexOf(" : ") < 0 && s.indexOf(" ") < 0) {
                    s1 = "/";
                } else if (s.indexOf(";") > 0 && s.indexOf(" ") < 0) {
                    s1 = ";";
                } else {
                    s1 = " ";
                }
                String s2;
                String s3;
                if (!s1.equals(" ")) {
                    s2 = s.substring(0, s.indexOf(s1));
                    String s4 = s.substring(0, s.length() - (s.lastIndexOf(s1) - 1));
                    if (s4.lastIndexOf(s1) > 0) {
                        s3 = s.substring(s4.lastIndexOf(s1) + 1, s.length());
                    } else {
                        s3 = s.substring(s.lastIndexOf(s1) + 1, s.length());
                    }
                } else {
                    s2 = s.substring(0, 12);
                    s3 = s.substring(s.length() - l, s.length());
                }
                if (s3.length() <= l) {
                    s = s3;
                    if (s3.length() + s2.length() <= l) {
                        s = s2 + "..." + s3;
                    }
                } else {
                    s = s.substring(s.length() - l, s.length());
                }
            }
            int i1 = (s.length() * 282) / 42;
            drawer.setFont("Helvetica", "bold", byte2);
            int j1 = com.dragonflow.Utils.TextUtils.floatToFormattedString(vertMax, 3).length() * 7;
            if (j1 < 35) {
                j1 = 35;
            }
            int k1 = -drawer.stringHeight() - j1;
            drawer.drawStringUp(translateX(k1), translateY((height - i1) / 2), s);
            drawer.restoreFont();
        }
    }

    int translateX(int i) {
        return i + leftPad;
    }

    int translateY(int i) {
        return imageHeight - (i + bottomPad);
    }

    void computeScale() {
        for (int i = 0; i < dataSet.length; i ++) {
            dataSet[i].scale = (float) height / vertMax;
            if (maxDataSet[i] != null) {
                maxDataSet[i].scale = (float) height / vertMax;
            }
        }

        timeScale = (float) width / (float) (endTime - startTime);
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

    public java.awt.Color getColor(int i) {
        if (i < colorList.length) {
            return colorList[i];
        }
        i %= colorList.length;
        int j = i / colorList.length;
        java.awt.Color color = colorList[i];
        if (j == 1) {
            color = makeColor(color, 51, 51, 0);
        } else if (j == 2) {
            color = makeColor(color, 0, -51, -51);
        } else if (j == 3) {
            color = makeColor(color, 102, 0, 51);
        } else if (j == 4) {
            color = makeColor(color, -51, 0, -102);
        }
        return color;
    }

    public java.awt.Color makeColor(java.awt.Color color, int i, int j, int k) {
        int l = color.getRed() + i;
        int i1 = color.getGreen() + j;
        int j1 = color.getBlue() + k;
        if (l > 255) {
            l = 255;
        }
        if (l < 0) {
            l = 0;
        }
        if (i1 > 255) {
            i1 = 255;
        }
        if (i1 < 0) {
            i1 = 0;
        }
        if (j1 > 255) {
            j1 = 255;
        }
        if (j1 < 0) {
            j1 = 0;
        }
        return new Color(l, i1, j1);
    }

}
