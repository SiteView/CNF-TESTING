/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Utils:
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
        int i = COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
        byte abyte1[] = new byte[i];
        if (randomaccessfile.read(abyte1) != i) {
            return "Unable to read line";
        }
        int j = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 4);
        int k = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 8);
        systemName = COM.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 4);
        if (systemName.equals("....")) {
            systemName = "";
        }
        j = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 12);
        k = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 16);
        objectName = COM.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 12);
        j = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 20);
        k = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 24);
        counterName = COM.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 20);
        j = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 28);
        k = COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, 32);
        instanceName = COM.dragonflow.Utils.PerfChartLine.scanString(abyte1, j, k + 28);
        k = 92;
        java.lang.Integer integer = new Integer(COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte1, k));
        scaleIndex = integer.toString();
        return "";
    }
}
