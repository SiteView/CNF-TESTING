/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Utils:
// PerfChartFile
public class PerfChartLine {

    public String systemName;

    public String objectName;

    public String counterName;

    public String instanceName;

    public String scaleIndex;

    public PerfChartLine() {
    }

    public static String scanString(byte abyte0[], int i, int j) {
        char ac[] = new char[i];
        for (int k = 0; k < i; k ++) {
            ac[k] = (char) abyte0[j + k * 2];
        }

        return new String(ac);
    }

    public String Read(java.io.RandomAccessFile randomaccessfile) throws java.io.IOException {
        byte abyte0[] = new byte[4];
        if (randomaccessfile.read(abyte0) != 4) {
            return "Unable to read line signature";
        }
        byte byte0 = abyte0[0];
        byte byte1 = abyte0[2];
        if (byte0 != 76 || byte1 != 105) {
            return "Incorrect line signature";
        }
        int i = com.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
        byte abyte1[] = new byte[i];
        if (randomaccessfile.read(abyte1) != i) {
            return "Unable to read line";
        }
        int j = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 4);
        int k = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 8);
        systemName = com.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 4);
        if (systemName.equals("....")) {
            systemName = "";
        }
        j = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 12);
        k = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 16);
        objectName = com.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 12);
        j = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 20);
        k = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 24);
        counterName = com.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 20);
        j = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 28);
        k = com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 32);
        instanceName = com.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 28);
        k = 92;
        java.lang.Integer integer = new Integer(com.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, k));
        scaleIndex = integer.toString();
        return "";
    }
}
