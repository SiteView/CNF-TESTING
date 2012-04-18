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

public abstract class Drawer {

    java.awt.Color penColor;

    int penWidth;

    int penHeight;

    String font;

    String fontStyle;

    int fontSize;

    String saveFont;

    String saveFontStyle;

    int saveFontSize;

    int width;

    int height;

    public static final int LEFT_JUSTIFIED = 0;

    public static final int CENTER_JUSTIFIED = 1;

    public static final int RIGHT_JUSTIFIED = 2;

    public void startDraw() {
    }

    public void endDraw() {
    }

    public void resetPen() {
        penWidth = 1;
        penHeight = 1;
        penColor = java.awt.Color.black;
    }

    public java.awt.Color getPenColor() {
        return penColor;
    }

    public void setPenColor(java.awt.Color color) {
        penColor = color;
    }

    public void setPenSize(int i, int j) {
        penWidth = i;
        penHeight = j;
    }

    public Drawer(int i, int j) {
        penColor = java.awt.Color.black;
        penWidth = 1;
        penHeight = 1;
        font = "";
        fontStyle = "";
        fontSize = 0;
        saveFont = font;
        saveFontStyle = fontStyle;
        saveFontSize = fontSize;
        width = 10;
        height = 10;
        resetPen();
        width = i;
        height = j;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void setFont(String s, String s1, int i);

    public boolean setFontCharacteristics(String s, String s1, int i) {
        if (s.equals(font) && s1.equals(fontStyle) && i == fontSize) {
            return false;
        } else {
            font = s;
            fontStyle = s1;
            fontSize = i;
            return true;
        }
    }

    public void saveFont() {
        saveFont = font;
        saveFontStyle = fontStyle;
        saveFontSize = fontSize;
    }

    public void restoreFont() {
        setFont(saveFont, saveFontStyle, saveFontSize);
    }

    public abstract void drawLine(int i, int j, int k, int l);

    public abstract void drawDashedLine(int i, int j, int k, int l);

    public abstract void drawRectangle(int i, int j, int k, int l);

    public void drawCross(int i, int j, int k, int l) {
        drawLine(i, j, i + k, j + l);
        drawLine(i, j + l, i + k, j);
    }

    public abstract void fillRectangle(int i, int j, int k, int l);

    public abstract void drawString(int i, int j, String s);

    public abstract void drawStringUp(int i, int j, String s);

    public abstract int stringWidth(String s);

    public abstract int stringHeight();

    public abstract void drawStringJustified(int i, int j, String s, int k);

    public void drawStringRightJustified(int i, int j, String s) {
        drawStringJustified(i, j, s, 2);
    }

    public void drawStringCenterJustified(int i, int j, String s) {
        drawStringJustified(i, j, s, 1);
    }

    public void drawStringMaxWidth(int i, int j, String s, int k) {
        if (stringWidth(s) > k) {
            String s1 = "...";
            for (int l = stringWidth(s1); stringWidth(s) + l > k && s.length() != 0; s = s.substring(0, s.length() - 1)) {
            }
            s = s + s1;
        }
        drawString(i, j, s);
    }
}
