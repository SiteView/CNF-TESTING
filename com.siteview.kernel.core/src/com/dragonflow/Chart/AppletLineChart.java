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
// AppletDataSet, Drawer

public class AppletLineChart {

    static final int VERTICAL_TICK_COUNT = 5;

    static final int HORIZONTAL_TICK_COUNT = 5;

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

    com.dragonflow.Chart.Drawer drawer;

    com.dragonflow.Chart.AppletDataSet dataSet[];

    com.dragonflow.Chart.AppletDataSet maxDataSet[];

    java.util.Hashtable properties;

    public static String AVG_PREFIX = "Avg: ";

    public static String MAX_PREFIX = "Max: ";

    public static int MINUTE_SECONDS;

    public static int HOUR_SECONDS;

    public static int DAY_SECONDS;

    public static final int MONTH_DAYS[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public String getValue(String s) {
        String s1 = (String) properties.get(s);
        if (s1 == null) {
            return "";
        } else {
            return s1;
        }
    }

    public AppletLineChart(com.dragonflow.Chart.Drawer drawer1, java.util.Hashtable hashtable) {
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
        startTime = 0L;
        endTime = 0L;
        timeOffset = 0L;
        vertMax = 100F;
        timeScale = 1.0F;
        drawer = null;
        dataSet = null;
        maxDataSet = null;
        properties = new Hashtable();
        properties = hashtable;
        drawer = drawer1;
        startTime = com.dragonflow.Chart.AppletLineChart.stringToDate(getValue("startTime")).getTime();
        endTime = com.dragonflow.Chart.AppletLineChart.stringToDate(getValue("endTime")).getTime();
        timeOffset = com.dragonflow.Chart.AppletLineChart.toLong(getValue("timeOffset"));
        imageWidth = drawer.getWidth();
        imageHeight = drawer.getHeight();
        java.util.Vector vector = new Vector();
        for (int i = 1; i <= 20 && getValue("name" + i).length() != 0; i ++) {
            vector.addElement(getValue("data" + i));
        }

        dataSet = new com.dragonflow.Chart.AppletDataSet[vector.size()];
        maxDataSet = new com.dragonflow.Chart.AppletDataSet[vector.size()];
        for (int j = 0; j < dataSet.length; j ++) {
            dataSet[j] = new AppletDataSet((String) vector.elementAt(j));
            dataSet[j].index = j;
            dataSet[j].name = getValue("name" + (j + 1));
            dataSet[j].average = getValue("average" + (j + 1));
            dataSet[j].maximum = getValue("maximum" + (j + 1));
            dataSet[j].color = getColor(j);
            dataSet[j].averagedSamples = getValue("averagedSamples" + (j + 1)).equalsIgnoreCase("true");
            String s1 = getValue("maxdata" + (j + 1));
            if (s1.length() > 0) {
                maxDataSet[j] = new AppletDataSet(s1);
                maxDataSet[j].index = j;
                maxDataSet[j].color = getColor(j);
                maxDataSet[j].averagedSamples = getValue("averagedSamples" + (j + 1)).equalsIgnoreCase("true");
            }
        }

        String s = getValue("vertMax");
        if (s.length() > 0) {
            vertMax = com.dragonflow.Chart.AppletLineChart.toFloat(s);
        }
        drawer.startDraw();
        drawChart();
        drawer.endDraw();
    }

    public void drawChart() {
        drawer.setFont("Helvetica", "plain", 10);
        rightPad = 20;
        topPad = 20;
        computeTitleHeight();
        drawer.setPenColor(java.awt.Color.white);
        drawer.fillRectangle(0, 0, imageWidth, imageHeight);
        drawer.setPenColor(java.awt.Color.black);
        errorBarHeight = dataSet.length * 4;
        errorBarHeight = java.lang.Math.max(drawer.stringHeight() * 2, errorBarHeight);
        horizontalUnitsHeight = drawer.stringHeight() * 3;
        bottomPad = (drawer.stringHeight() + 3) * (2 + dataSet.length) + 16 + errorBarHeight;
        leftPad = drawer.stringWidth("99999999") + 10;
        height = imageHeight - (topPad + bottomPad);
        width = imageWidth - (leftPad + rightPad);
        computeScale();
        drawTitle();
        drawVerticalUnits();
        drawHorizontalUnits();
        drawData();
        drawAxes();
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
            topPad = drawer.stringHeight() * 3;
            if (getValue("subtitle").length() > 0) {
                drawer.setFont("Helvetica", "plain", 9);
                topPad += drawer.stringHeight() * 2;
            }
        }
        drawer.restoreFont();
    }

    void drawTitle() {
        String s = getValue("title");
        if (s.length() > 0) {
            drawer.saveFont();
            drawer.setFont("Helvetica", "bold", 12);
            int i = topPad / 2;
            drawer.drawStringJustified(imageWidth / 2, i, s, 1);
            String s1 = getValue("subtitle");
            if (s1.length() > 0) {
                drawer.setFont("Helvetica", "plain", 9);
                drawer.drawStringJustified(imageWidth / 2, i + drawer.stringHeight() + 3, s1, 1);
            }
            drawer.restoreFont();
        }
    }

    void drawAxes() {
        drawer.drawLine(translateX(-yAxesSlop), translateY(0), translateX(width + yAxesSlop), translateY(0));
        drawer.drawLine(translateX(-yAxesSlop), translateY(0), translateX(-yAxesSlop), translateY(height + topAxesSlop));
        drawer.drawLine(translateX(-yAxesSlop), translateY(height + topAxesSlop), translateX(width + yAxesSlop), translateY(height + topAxesSlop));
        drawer.drawLine(translateX(width + yAxesSlop), translateY(0), translateX(width + yAxesSlop), translateY(height + topAxesSlop));
    }

    void drawHorizontalUnits() {
        byte byte0 = 5;
        float f = (float) width / (float) (byte0 - 1);
        boolean flag = false;
        float f1 = 0.0F;
        boolean flag1 = endTime - startTime > 0x5265c00L;
        int j = translateY((-5 - horizontalUnitsHeight) + drawer.stringHeight());
        for (int k = 0; k < byte0; k ++) {
            long l = startTime + ((endTime - startTime) / (long) (byte0 - 1)) * (long) k;
            int i = java.lang.Math.round(f1);
            byte byte1 = 1;
            if (k == 0) {
                byte1 = 0;
                i = -yAxesSlop;
            } else if (k == byte0 - 1) {
                byte1 = 2;
                i = width + yAxesSlop;
            }
            drawer.drawLine(translateX(i), translateY(0), translateX(i), translateY(-3));
            drawer.drawStringJustified(translateX(i), translateY(-3 - drawer.stringHeight()), prettyDateTime(new Date(l)), byte1);
            if (k == 0 || k == byte0 - 1 || flag1) {
                drawer.drawStringJustified(translateX(i), translateY(-3 - drawer.stringHeight() * 2), prettyDateDate(new Date(l)), byte1);
            }
            if (k > 0 && k < byte0 - 1) {
                drawer.setPenColor(java.awt.Color.lightGray);
                drawer.drawLine(translateX(i), j, translateX(i), j + errorBarHeight);
                drawer.drawLine(translateX(i), translateY(0), translateX(i), translateY(height + topAxesSlop));
                drawer.setPenColor(java.awt.Color.black);
            }
            f1 += f;
        }

        java.awt.Color color = drawer.getPenColor();
        drawer.drawString(translateX(-leftPad + 5), j + drawer.stringHeight(), "Error:");
        drawer.setPenColor(java.awt.Color.lightGray);
        drawer.drawLine(translateX(-yAxesSlop), j, translateX(width + yAxesSlop), j);
        drawer.drawLine(translateX(-yAxesSlop), j + errorBarHeight, translateX(width + yAxesSlop), j + errorBarHeight);
        drawer.drawLine(translateX(-yAxesSlop), j, translateX(-yAxesSlop), j + errorBarHeight);
        drawer.drawLine(translateX(width + yAxesSlop), j, translateX(width + yAxesSlop), j + errorBarHeight);
        int i1 = -5 - (horizontalUnitsHeight + errorBarHeight);
        int j1 = 0;
        int k1 = 0;
        for (int l1 = 0; l1 < dataSet.length; l1 ++) {
            int i2 = drawer.stringWidth(AVG_PREFIX + dataSet[l1].average);
            if (i2 > j1) {
                j1 = i2;
            }
            i2 = drawer.stringWidth(MAX_PREFIX + dataSet[l1].maximum);
            if (i2 > k1) {
                k1 = i2;
            }
        }

        byte byte2 = 5;
        byte byte3 = 20;
        int j2 = java.lang.Math.min(k1, 170);
        int k2 = imageWidth - (j2 + 5);
        int l2 = java.lang.Math.min(j1, 95);
        int i3 = k2 - (l2 + 10);
        int j3 = byte2 + byte3 + 5;
        int k3 = i3 - j3 - 10;
        for (int l3 = 0; l3 < dataSet.length; l3 ++) {
            drawer.setPenColor(dataSet[l3].color);
            drawer.fillRectangle(translateX(-leftPad + 5), translateY((i1 + drawer.stringHeight()) - 3), 20, 10);
            drawer.setPenColor(java.awt.Color.black);
            drawer.drawStringMaxWidth(j3, translateY(i1), dataSet[l3].name, k3);
            drawer.drawStringMaxWidth(i3, translateY(i1), AVG_PREFIX + dataSet[l3].average, l2);
            drawer.drawStringMaxWidth(k2, translateY(i1), MAX_PREFIX + dataSet[l3].maximum, j2);
            i1 -= drawer.stringHeight() + 3;
        }

        drawer.setPenColor(color);
    }

    void drawVerticalUnits() {
        byte byte0 = 5;
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
            drawer.drawStringJustified(translateX(-8), translateY(j + i), com.dragonflow.Chart.AppletLineChart.floatToString(f2, byte1), 2);
            f1 += f;
        }

        String s = getValue("propertyName");
        if (s.length() > 0) {
            int l = drawer.stringWidth(s);
            drawer.drawStringUp(translateX(-leftPad + drawer.stringHeight() + 5), translateY((height - l) / 2), s);
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

    void drawData(com.dragonflow.Chart.AppletDataSet appletdataset) {
        drawData(appletdataset, false, true);
    }

    void drawDashedData(com.dragonflow.Chart.AppletDataSet appletdataset) {
        drawData(appletdataset, true, false);
    }

    void drawData(com.dragonflow.Chart.AppletDataSet appletdataset, boolean flag, boolean flag1) {
        int i = -1;
        int j = -1;
        int k = ((-5 - horizontalUnitsHeight) + drawer.stringHeight()) - 1 - appletdataset.index * 4;
        int l = -1;
        drawer.setPenColor(appletdataset.color);
        appletdataset.sampleStart();
        do {
            if (!appletdataset.nextSample()) {
                break;
            }
            if (appletdataset.sampleTime < startTime) {
                continue;
            }
            if (appletdataset.sampleTime > endTime) {
                break;
            }
            int i1 = java.lang.Math.round((float) (appletdataset.sampleTime - startTime) * timeScale);
            if (l >= 0) {
                drawer.drawLine(translateX(l), translateY(k - 1), translateX(i1), translateY(k - 1));
                drawer.drawRectangle(translateX(l + 1), translateY(k + 1), 3, 3);
            }
            if (appletdataset.isErrorSample()) {
                l = i1;
            } else {
                l = -1;
            }
            if (appletdataset.sampleHasValue) {
                float f = appletdataset.sampleValue;
                if (f > vertMax) {
                    f = vertMax;
                }
                int j1 = java.lang.Math.round(f * appletdataset.scale);
                if (appletdataset.isDisabledSample()) {
                    j1 = 0;
                }
                if (appletdataset.averagedSamples) {
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
                    if (appletdataset.isDisabledSample()) {
                        drawer.drawCross(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                    } else {
                        drawer.drawRectangle(translateX(i1 - 1), translateY(j1 + 1), 3, 3);
                    }
                } else if (appletdataset.isDisabledSample()) {
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

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static float toFloat(String s) {
        float f = 0.0F;
        try {
            java.lang.Float float1 = new Float(s);
            return float1.floatValue();
        } catch (java.lang.NumberFormatException numberformatexception) {
            return f;
        }
    }

    public static long toLong(String s) {
        long l = 0L;
        try {
            l = java.lang.Long.parseLong(s);
        } catch (java.lang.NumberFormatException numberformatexception) {
        }
        return l;
    }

    public String prettyDate() {
        return prettyDate(new Date());
    }

    public long timeSeconds() {
        java.util.Date date = new Date();
        return date.getTime() / 1000L;
    }

    public String prettyDate(long l) {
        return prettyDate(new Date(l));
    }

    public String prettyDateDate(java.util.Date date) {
        return prettyDatePart(date, false);
    }

    public String prettyDateTime(java.util.Date date) {
        return prettyDatePart(date, true);
    }

    public String prettyDatePart(java.util.Date date, boolean flag) {
        String s = prettyDate(date);
        int i = s.lastIndexOf(" ");
        if (i >= 0) {
            if (flag) {
                return s.substring(0, i);
            } else {
                return s.substring(i + 1);
            }
        } else {
            return s;
        }
    }

    public String prettyDate(java.util.Date date) {
        return prettyDate(date, " ");
    }

    public String prettyDate(java.util.Date date, String s) {
        if (timeOffset != 0L) {
            date = new Date(date.getTime() + timeOffset * 1000L);
        }
        int i = date.getHours();
        String s1 = "am";
        if (i == 0) {
            i = 12;
        } else if (i == 12) {
            s1 = "pm";
        } else if (i > 12) {
            s1 = "pm";
            i -= 12;
        }
        return i + ":" + com.dragonflow.Chart.AppletLineChart.numberToString(date.getMinutes()) + " " + s1 + s + (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getYear() % 100;
    }

    public static String prettyHour(int i) {
        String s = "am";
        if (i == 0) {
            i = 12;
        } else if (i == 12) {
            s = "pm";
        } else if (i > 12) {
            s = "pm";
            i -= 12;
        }
        return "" + i + " " + s;
    }

    public static String numberToString(int i) {
        if (i > 9) {
            return String.valueOf(i);
        } else {
            return "0" + String.valueOf(i);
        }
    }

    public static java.util.Date stringToDate(String s) {
        int i = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 0);
        int j = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 3);
        int k = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 6);
        int l = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 9) - 1;
        int i1 = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 12);
        int j1 = com.dragonflow.Chart.AppletLineChart.readIntegerSafe(s, 15);
        if (j1 > 1900) {
            j1 -= 1900;
        }
        return new Date(j1, l, i1, i, j, k);
    }

    public static int readIntegerSafe(String s, int i) throws java.lang.NumberFormatException {
        int j = com.dragonflow.Chart.AppletLineChart.readInteger(s, i);
        if (j == -1) {
            throw new NumberFormatException("Integer not found at offset " + i + " in: " + s);
        } else {
            return j;
        }
    }

    public static int readInteger(String s, int i) {
        long l = com.dragonflow.Chart.AppletLineChart.readLong(s, i);
        if (l <= 0x7fffffffL) {
            return (int) l;
        } else {
            return -1;
        }
    }

    public static long readLong(String s, int i) {
        long l = -1L;
        StringBuffer stringbuffer = new StringBuffer();
        int j = s.length();
        do {
            if (i >= j) {
                break;
            }
            char c = s.charAt(i ++);
            if (!java.lang.Character.isDigit(c)) {
                break;
            }
            stringbuffer.append(c);
        } while (true);
        if (stringbuffer.length() > 0) {
            l = java.lang.Long.valueOf(stringbuffer.toString()).longValue();
        }
        return l;
    }

    public static String filledString(char c, int i) {
        StringBuffer stringbuffer = new StringBuffer(i);
        for (; i > 0; i --) {
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    public static String floatToString(float f, int i) {
        if (i == 0) {
            return "" + java.lang.Math.round(f);
        }
        String s = String.valueOf(f);
        int j = s.indexOf(".");
        if (j < 0) {
            return s + "." + com.dragonflow.Chart.AppletLineChart.filledString('0', i);
        }
        int k = s.length() - j - 1;
        if (k > i) {
            s = s.substring(0, j + i + 1);
        } else if (k < i) {
            s = s + com.dragonflow.Chart.AppletLineChart.filledString('0', i - k);
        }
        return s;
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

    static {
        MINUTE_SECONDS = 60;
        HOUR_SECONDS = 60 * MINUTE_SECONDS;
        DAY_SECONDS = 24 * HOUR_SECONDS;
    }
}
