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

// Referenced classes of package com.dragonflow.Chart.GD:
// GDFont, GDMediumBoldFontData1, GDMediumBoldFontData2
public class GDMediumBoldFont extends com.dragonflow.Chart.GD.GDFont {

    public GDMediumBoldFont() {
        nchars = 128;
        offset = 0;
        w = 7;
        h = 13;
        descent = 2;
        ascent = h - descent;
        data = new byte[com.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData.length + com.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData.length];
        int i = 0;
        for (int j = 0; j < com.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData.length; j ++) {
            data[i ++] = com.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData[j];
        }

        for (int k = 0; k < com.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData.length; k ++) {
            data[i ++] = com.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData[k];
        }

    }
}
