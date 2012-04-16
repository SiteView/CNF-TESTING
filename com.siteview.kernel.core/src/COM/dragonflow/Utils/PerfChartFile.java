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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import jgl.Array;

// Referenced classes of package COM.dragonflow.Utils:
// PerfCounter, PerfChartLine, FileUtils, TextUtils

public class PerfChartFile {

    private static final int PerfSignatureLen = 20;

    public static final int linevisualLen = 36;

    private static final int graphoptionsLen = 40;

    private static final int optionsLen = 16;

    private static final int chartFileType = 1;

    private static final int workspaceFileType = 2;

    public PerfChartFile() {
    }

    static int convertDWORD(byte abyte0[], int i) {
        byte byte0 = abyte0[i];
        int j = byte0 >= 0 ? ((int) (byte0)) : 256 + byte0;
        byte0 = abyte0[i + 1];
        int k = byte0 >= 0 ? ((int) (byte0)) : 256 + byte0;
        byte0 = abyte0[i + 2];
        int l = byte0 >= 0 ? ((int) (byte0)) : 256 + byte0;
        byte0 = abyte0[i + 3];
        int i1 = byte0 >= 0 ? ((int) (byte0)) : 256 + byte0;
        return j + (k << 8) + (l << 16) + (i1 << 24);
    }

    static int readDWORD(java.io.RandomAccessFile randomaccessfile) throws java.io.IOException {
        byte abyte0[] = new byte[4];
        if (randomaccessfile.read(abyte0) != 4) {
            throw new IOException("Unable to read DWORD");
        } else {
            return COM.dragonflow.Utils.PerfChartFile.convertDWORD(abyte0, 0);
        }
    }

    public static jgl.Array GetWin2kSettings(String s) throws java.io.IOException {
        jgl.Array array = new Array();
        try {
            java.io.File file = new File(s);
            if (!file.exists()) {
                throw new IOException("File not found: " + s);
            }
            StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readFile(s);
            String s1 = stringbuffer.toString();
            if (s1.charAt(3) == 0) {
                char ac[] = new char[s1.length()];
                int j = 0;
                for (int k = 0; k < s1.length(); k += 2) {
                    ac[j ++] = s1.charAt(k);
                }

                s1 = new String(ac);
            }
            int i = 0;
            do {
                String s2 = ".Path\" VALUE=\"";
                int l = s1.indexOf(s2, i);
                if (l == -1) {
                    break;
                }
                i = s1.indexOf("\">", l);
                if (i == -1) {
                    i = s1.indexOf("\"/>", l);
                }
                if (i == -1) {
                    break;
                }
                l += s2.length();
                String s3 = s1.substring(l, i);
                String s4 = "";
                int i1 = s3.indexOf("(");
                int j1 = s3.indexOf(")");
                if (i1 != -1 && j1 != -1 && s3.matches(".*?\\\\.*?\\(.*?\\).*?\\\\.*?")) {
                    s4 = s3.substring(i1 + 1, j1);
                    s3 = s3.substring(0, i1) + s3.substring(j1 + 1);
                }
                if (s3.startsWith("\\\\")) {
                    s3 = s3.substring(2, s3.length());
                }
                String as[] = COM.dragonflow.Utils.TextUtils.split(s3, "\\");
                COM.dragonflow.Utils.PerfCounter perfcounter = new PerfCounter();
                perfcounter.object = as[1];
                perfcounter.counterName = as[2];
                if (as.length > 3) {
                    perfcounter.logID = as[3];
                }
                perfcounter.instance = s4;
                array.add(perfcounter);
            } while (true);
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Utils.TextUtils.debugPrint("Exception: " + exception);
            exception.printStackTrace();
            COM.dragonflow.Log.LogManager.log("Error", "PerChartFile.java is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
        }
        return array;
    }

    /**
     * 
     * 
     * @param s
     * @return
     * @throws java.io.IOException
     */
    public static jgl.Array GetSettings(String s) throws java.io.IOException {
        jgl.Array array;
        java.io.RandomAccessFile randomaccessfile;
        java.lang.Exception exception1;
        if (s.toLowerCase().indexOf(".htm") != -1) {
            return COM.dragonflow.Utils.PerfChartFile.GetWin2kSettings(s);
        }
        randomaccessfile = null;
        try {
            java.io.File file = new File(s);
            if (!file.exists()) {
                throw new IOException("File not found: " + s);
            }
            if (!file.canRead()) {
                throw new IOException("File cannot be read: " + s);
            }
            randomaccessfile = new RandomAccessFile(file, "r");
            byte abyte0[] = new byte[40];
            if (randomaccessfile.read(abyte0) != 40) {
                throw new IOException("Unable to read signature from file: " + s);
            }
            char ac[] = new char[20];
            for (int i = 0; i < 20; i ++) {
                ac[i] = (char) abyte0[i * 2];
            }

            String s1 = new String(ac);
            byte byte0;
            if (s1.startsWith("PERF WORKSPACE")) {
                byte0 = 2;
            } else if (s1.startsWith("PERF CHART")) {
                byte0 = 1;
            } else {
                throw new Exception("Unknown File Signature in file: " + s);
            }
            int j = COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
            int k = COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
            if (byte0 == 1 && (j != 1 || k != 3)) {
                throw new IOException("Incorrect Chart File version (want 1.3, " + j + "." + k + " found) in file: " + s);
            }
            if (byte0 == 2 && (j != 1 || k != 6)) {
                throw new IOException("Incorrect Workspace File version (want 1.3, " + j + "." + k + " found) in file: " + s);
            }
            byte abyte1[] = new byte[100];
            if (randomaccessfile.read(abyte1) != 100) {
                throw new IOException("Error Reading Header in file: " + s);
            }
            if (byte0 == 2) {
                COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
                int l = COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
                char c = '\234';
                int j1 = l - c;
                byte abyte4[] = new byte[j1];
                if (randomaccessfile.read(abyte4) != j1) {
                    throw new IOException("Error seeking to chart section in file: " + s);
                }
            }
            int i1 = COM.dragonflow.Utils.PerfChartFile.readDWORD(randomaccessfile);
            randomaccessfile.readInt();
            byte abyte2[] = new byte[36];
            if (randomaccessfile.read(abyte2) != 36) {
                throw new IOException("Error Reading Line Visual in file: " + s);
            }
            byte abyte3[] = new byte[40];
            if (randomaccessfile.read(abyte3) != 40) {
                throw new IOException("Error Reading Graph Options in file: " + s);
            }
            randomaccessfile.readInt();
            byte abyte5[] = new byte[16];
            if (randomaccessfile.read(abyte5) != 16) {
                throw new IOException("Error Reading Options in file: " + s);
            }
            int k1 = 0;
            String s2 = "";
            COM.dragonflow.Utils.PerfChartLine aperfchartline[] = new COM.dragonflow.Utils.PerfChartLine[i1];
            for (; s2.length() != 0 || k1 < i1; k1 ++) {
                aperfchartline[k1] = new PerfChartLine();
                s2 = aperfchartline[k1].Read(randomaccessfile);
            }

            if (s2.length() != 0) {
                throw new IOException(s2);
            }
            array = new Array();
            for (int l1 = 0; l1 < i1; l1 ++) {
                COM.dragonflow.Utils.PerfCounter perfcounter = new PerfCounter();
                perfcounter.object = aperfchartline[l1].objectName;
                perfcounter.counterName = aperfchartline[l1].counterName;
                perfcounter.instance = aperfchartline[l1].instanceName;
                array.add(perfcounter);
            }
            if (randomaccessfile != null) {
                randomaccessfile.close();
            }
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "PerChartFile.java is unhappy: " + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
            throw new IOException("Exception: " + exception.toString() + " in file: " + s);
        } finally {
            if (randomaccessfile != null) {
                randomaccessfile.close();
            }
        }
        return array;
    }
}
