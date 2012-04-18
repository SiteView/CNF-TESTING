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
import java.awt.Font;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

// Referenced classes of package com.dragonflow.Chart:
// Drawer

public class DrawerAWT extends com.dragonflow.Chart.Drawer {

    java.awt.Image image;

    java.awt.Graphics g;

    java.awt.FontMetrics fontMetrics;

    java.applet.Applet applet;

    public static final int LEFT_JUSTIFIED = 0;

    public static final int CENTER_JUSTIFIED = 1;

    public static final int RIGHT_JUSTIFIED = 2;

    public DrawerAWT(java.applet.Applet applet1, int i, int j) {
        super(i, j);
        image = null;
        g = null;
        fontMetrics = null;
        applet = null;
        applet = applet1;
    }

    public void startDraw() {
        super.startDraw();
        image = applet.createImage(width, height);
        g = image.getGraphics();
        setFont("Helvetica", "plain", 10);
        fontMetrics = g.getFontMetrics(g.getFont());
    }

    public void endDraw() {
        super.endDraw();
        g.dispose();
        g = null;
    }

    public java.awt.Image getImage() {
        return image;
    }

    public void setPenColor(java.awt.Color color) {
        super.setPenColor(color);
        g.setColor(color);
    }

    public void drawLine(int i, int j, int k, int l) {
        g.drawLine(i, j, k, l);
    }

    public void drawDashedLine(int i, int j, int k, int l) {
        double d = 1.0D;
        double d1 = 4D;
        double d2 = java.lang.Math.sqrt((k - i) * (k - i) + (l - j) * (l - j));
        if (d2 < 0.5D) {
            return;
        }
        double d3 = (double) (k - i) / (d2 / (d + d1));
        double d4 = (double) (l - j) / (d2 / (d + d1));
        double d5 = (double) (k - i) / (d2 / d);
        double d6 = (double) (l - j) / (d2 / d);
        int i1 = 0;
        for (double d7 = 0.0D; d7 < d2 - d; d7 += d + d1) {
            g.drawLine((int) ((double) i + d3 * (double) i1), (int) ((double) j + d4 * (double) i1), (int) ((double) i + d3 * (double) i1 + d5), (int) ((double) j + d4 * (double) i1 + d6));
            i1 ++;
        }

        if ((d + d1) * (double) i1 <= d2) {
            g.drawLine((int) ((double) i + d3 * (double) i1), (int) ((double) j + d4 * (double) i1), k, l);
        }
    }

    public void drawRectangle(int i, int j, int k, int l) {
        g.drawRect(i, j, k, l);
    }

    public void fillRectangle(int i, int j, int k, int l) {
        g.fillRect(i, j, k, l);
    }

    public void drawString(int i, int j, String s) {
        g.drawString(s, i, j);
    }

    public void drawStringUp(int i, int j, String s) {
        int k = stringWidth(s);
        int l = stringHeight();
        java.awt.Image image1 = applet.createImage(k, l);
        java.awt.Graphics g1 = image1.getGraphics();
        g1.setFont(new Font(font, getAWTFontStyle(fontStyle), fontSize));
        g1.setColor(java.awt.Color.white);
        g1.fillRect(0, 0, k, l);
        g1.setColor(java.awt.Color.black);
        g1.drawString(s, 0, fontMetrics.getAscent());
        try {
            java.lang.Thread.sleep(5000L);
        } catch (java.lang.InterruptedException interruptedexception) {
        }
        int ai[] = new int[k * l];
        java.awt.image.PixelGrabber pixelgrabber = new PixelGrabber(image1, 0, 0, k, l, ai, 0, k);
        try {
            pixelgrabber.grabPixels();
        } catch (java.lang.InterruptedException interruptedexception1) {
            java.lang.System.out.println("Interrupted while grabbing pixels");
        }
        if ((pixelgrabber.status() & 0x80) != 0) {
            java.lang.System.out.println("image  fetch  aborted  or  errored");
            return;
        }
        int ai1[] = new int[k * l];
        for (int i1 = 0; i1 < l; i1 ++) {
            for (int j1 = 0; j1 < k; j1 ++) {
                ai1[(k - 1 - j1) * l + i1] = ai[i1 * k + j1];
            }

        }

        java.awt.image.MemoryImageSource memoryimagesource = new MemoryImageSource(l, k, ai1, 0, l);
        java.awt.Image image2 = applet.createImage(memoryimagesource);
        g.drawImage(image2, i - fontMetrics.getAscent(), j - k, applet);
    }

    public int stringWidth(String s) {
        return fontMetrics.stringWidth(s);
    }

    public int stringHeight() {
        return fontMetrics.getHeight();
    }

    int getAWTFontStyle(String s) {
        byte byte0 = 0;
        if (s.equals("bold")) {
            byte0 = 1;
        } else if (s.equals("italic")) {
            byte0 = 2;
        }
        return byte0;
    }

    public void setFont(String s, String s1, int i) {
        if (setFontCharacteristics(s, s1, i)) {
            g.setFont(new Font(s, getAWTFontStyle(s1), i));
            fontMetrics = g.getFontMetrics();
        }
    }

    public void drawStringJustified(int i, int j, String s, int k) {
        if (k == 1) {
            i -= fontMetrics.stringWidth(s) / 2;
        } else if (k == 2) {
            i -= fontMetrics.stringWidth(s);
        }
        g.drawString(s, i, j);
    }
}
