/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Chart.GD;

;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Chart.GD:
// GDFont, GDMediumBoldFontData1, GDMediumBoldFontData2
public class GDMediumBoldFont extends COM.dragonflow.Chart.GD.GDFont {

    public GDMediumBoldFont() {
        nchars = 128;
        offset = 0;
        w = 7;
        h = 13;
        descent = 2;
        ascent = h - descent;
        data = new byte[COM.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData.length + COM.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData.length];
        int i = 0;
        for (int j = 0; j < COM.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData.length; j ++) {
            data[i ++] = COM.dragonflow.Chart.GD.GDMediumBoldFontData1.fontData[j];
        }

        for (int k = 0; k < COM.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData.length; k ++) {
            data[i ++] = COM.dragonflow.Chart.GD.GDMediumBoldFontData2.fontData[k];
        }

    }
}
