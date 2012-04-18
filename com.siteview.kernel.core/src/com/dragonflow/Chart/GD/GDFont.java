/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Chart.GD;

;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class GDFont {

    public int nchars;

    public int offset;

    public int w;

    public int h;

    public int ascent;

    public int descent;

    byte data[];

    public GDFont() {
    }

    public int getStringWidth(String s) {
        return s.length() * w;
    }

    public int getHeight() {
        return h;
    }
}
