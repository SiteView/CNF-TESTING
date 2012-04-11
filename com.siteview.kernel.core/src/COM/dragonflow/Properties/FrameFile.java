/*
 * 
 * Created on 2005-2-28 6:56:56
 *
 * FrameFile.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>FrameFile</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import jgl.Sorting;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.DetectConfigurationChange;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.PlatformNew;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TempFileManager;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Properties:
// LessEqualPropertyName, StringProperty, HashMapOrdered

public class FrameFile {

    public static final boolean SORTED = true;

    public static final boolean UNSORTED = false;

    static boolean allowNestedFrames = false;

    private static boolean readMasterConfig = false;

    public static boolean forceMangleInited = false;

    public static boolean forceMangleOnRead = false;

    public static boolean forceMangleOnWrite = false;

    public FrameFile() {
    }

    public static void writeToFile(String s, HashMap hashmap) throws IOException {
        writeToFile(s, hashmap, false);
    }

    public static void writeToFile(String s, HashMap hashmap, boolean flag) throws IOException {
        Array array = new Array();
        array.add(hashmap);
        writeToFile(s, array, null, false, flag);
    }

    public static void writeToFile(String s, Array array) throws IOException {
        writeToFile(s, array, false);
    }

    public static void writeToFile(String s, Array array, boolean flag) throws IOException {
        writeToFile(s, array, null, false, flag);
    }

    public static void writeToFile(String s, Array array, String s1, boolean flag) throws IOException {
        writeToFile(s, array, s1, flag, false);
    }

    public static void removeOlderBackFiles(int i) {
        File file = new File(Platform.getRoot() + "/groups");
        String as[] = file.list();
        for (int j = 0; as != null && j < as.length; j++) {
            if (as[j].indexOf("bak.") < 0) {
                continue;
            }
            int k = TextUtils.toInt(as[j].substring(as[j].indexOf("bak.") + 4));
            if (i > 1 && k <= i) {
                continue;
            }
            File file1 = new File(Platform.getRoot() + "/groups/" + as[j]);
            if (k == 1) {
                String s = Platform.getRoot() + "/groups/" + as[j];
                s = s.substring(0, s.lastIndexOf("."));
                File file2 = new File(s);
                if (file2.exists()) {
                    file2.delete();
                }
                file1.renameTo(file2);
            } else {
                file1.delete();
            }
        }

    }

    private static int getFileNum(String s) {
        int i = 0;
        try {
            i = Integer.parseInt(s.substring(s.lastIndexOf(".") + 1));
        } catch (NumberFormatException numberformatexception) {
        }
        return i;
    }

    private static File pushBackup(File file, String s, String s1, int i, boolean flag) throws IOException {
        File file1 = new File(s1);
        if (i > 1) {
            String s2 = new String(s);
            int j = s2.lastIndexOf("/");
            int k = s2.lastIndexOf("\\");
            s2 = s2.substring(0, Math.max(j, k) + 1);
            String s3 = s.substring(Math.max(j, k) + 1);
            File file2 = new File(s2);
            String as[] = file2.list();
            Array array = new Array();
            for (int l = 0; l < as.length; l++) {
                if (as[l].indexOf(s3 + ".bak.") >= 0) {
                    array.add(as[l]);
                }
            }

            File file3 = new File(s2 + s3 + ".bak");
            int i1 = array == null ? 0 : array.size();
            Array array1 = new Array();
            if (file3.exists()) {
                array1.add(s3 + ".bak");
                if (i1 == 0) {
                    array1.add(s3 + ".bak.1");
                }
                for (int j1 = 1; j1 < i1 + 1; j1++) {
                    array1.add(array.at(j1 - 1));
                }

                if (i1 < i) {
                    array1.add(s3 + ".bak." + ++i1);
                }
                array = array1;
            }
            for (int k1 = 1; k1 < array.size(); k1++) {
                int i2 = getFileNum((String) array.at(k1));
                String s4 = new String((String) array.at(k1));
                int k2;
                for (k2 = k1 - 1; k2 >= 0 && i2 > getFileNum((String) array.at(k2)); k2--) {
                    array.put(k2 + 1, array.at(k2));
                }

                array.put(k2 + 1, s4);
            }

            for (int l1 = 0; l1 < array.size(); l1++) {
                File file4 = new File(s2 + (String) array.at(l1));
                int j2 = array.size() - l1;
                if (j2 >= i && file4.exists()) {
                    file4.delete();
                }
                if (j2 < i) {
                    File file5 = new File(s2 + s3 + ".bak." + (j2 + 1));
                    file4.renameTo(file5);
                }
            }

            file1 = new File(s + ".bak.1");
            if (file1.exists()) {
                file1.delete();
            }
        }
        if (file.exists() && flag) {
            try {
                FileUtils.copyFileThrow(file, file1);
            } catch (IOException ioexception) {
                file1.delete();
                throw new IOException(ioexception.toString());
            }
        }
        return file1;
    }

    private static boolean popBackup(File file, File file1, String s, int i) throws IOException {
        boolean flag = true;
        if (file1.exists()) {
            FileUtils.copyFileThrow(file1, file);
        } else {
            flag = false;
        }
        if (i > 1) {
            String s1 = new String(s);
            int j = s1.lastIndexOf("/");
            int k = s1.lastIndexOf("\\");
            s1 = s1.substring(0, Math.max(j, k) + 1);
            String s2 = s.substring(Math.max(j, k) + 1);
            File file2 = new File(s1);
            String as[] = file2.list();
            Array array = new Array();
            for (int l = 0; l < as.length; l++) {
                if (as[l].indexOf(s2 + ".bak.") >= 0) {
                    array.add(as[l]);
                }
            }

            File file3 = new File(s1 + s2 + ".bak");
            if (file3.exists()) {
                return flag;
            }
            for (int i1 = 1; i1 < array.size(); i1++) {
                int k1 = getFileNum((String) array.at(i1));
                String s3 = new String((String) array.at(i1));
                int l1;
                for (l1 = i1 - 1; l1 >= 0 && k1 < getFileNum((String) array.at(l1)); l1--) {
                    array.put(l1 + 1, array.at(l1));
                }

                array.put(l1 + 1, s3);
            }

            for (int j1 = 0; j1 < array.size() - 1; j1++) {
                File file4 = new File(s1 + (String) array.at(j1));
                if (file4.exists()) {
                    file4.delete();
                }
                File file5 = new File(s1 + (String) array.at(j1 + 1));
                file5.renameTo(file4);
            }

        }
        return flag;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param s1
     * @param flag
     * @param flag1
     * @throws IOException
     */
    public static void writeToFile(String s, Array array, String s1, boolean flag, boolean flag1) throws IOException {
        synchronized (FileUtils.getFileLock(s)) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            int i = siteviewgroup.getSettingAsLong("_backups2Keep", 1);
            File file = new File(s);
            if (!file.exists() && I18N.isI18N(s)) {
                String s2 = I18N.toDefaultEncoding(s);
                File file1 = new File(s2);
                if (file1.exists()) {
                    s = s2;
                    file = file1;
                }
            }
            String s3 = s + ".bak" + (i <= 1 ? "" : ".1");
            boolean flag2 = siteviewgroup.getSetting("_backupDyns").length() == 0 || !s.endsWith("dyn");
            File file2 = pushBackup(file, s, s3, i, flag2);
            if (s.indexOf("master.config") != -1) {
                TempFileManager.addTempByAgeFile(file, siteviewgroup.getSettingAsLong("_tempByAgeTTL", 168));
            }
            if (printFile(file, array, s1, flag, flag1)) {
                popBackup(file, file2, s, i);
            }
        }
        DetectConfigurationChange.getInstance().resetFileTimeStamp(s);
    }

    static StringBuffer mangle(StringBuffer stringbuffer) {
        String s = stringbuffer.toString();
        s = TextUtils.replaceChar(s, '\r', "");
        long l = PlatformNew.crc(s);
        String s1 = TextUtils.obscure(s);
        return new StringBuffer("=" + l + s1);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param file
     * @param array
     * @param s
     * @param flag
     * @param flag1
     * @return
     * @throws IOException
     */
    public static boolean printFile(File file, Array array, String s, boolean flag, boolean flag1) throws IOException {
        PrintWriter printwriter = null;
        FileOutputStream fileoutputstream = null;
        boolean flag2 = false;
        try {
            StringBuffer stringbuffer = new StringBuffer();
            printFrames(stringbuffer, array, s, flag, flag1);
            if (forceMangleOnWrite) {
                stringbuffer = mangle(stringbuffer);
            }
            fileoutputstream = new FileOutputStream(file);
            printwriter = FileUtils.MakeUTF8OutputWriter(fileoutputstream);
            printwriter.print(stringbuffer);
            flag2 = printwriter.checkError();
        } catch (IOException e) {
            LogManager.log("Error", " IOException during write of " + file.getAbsolutePath() + " "
                    + e.getMessage());
            throw e;
        } finally {
            if (printwriter != null) {
                printwriter.close();
            }
            try {
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
            } catch (IOException e) {
                LogManager.log("Error", "Exception in FrameFile.printFile()" + e.getMessage());
                e.printStackTrace();
            }
        }
        return flag2;
    }

    public static void printFrames(PrintWriter printwriter, Array array, String s, boolean flag, boolean flag1) {
    }

    static void printFrames(StringBuffer stringbuffer, Array array, String s, boolean flag, boolean flag1) {
        Enumeration enumeration = array.elements();
        for (boolean flag2 = true; enumeration.hasMoreElements(); flag2 = false) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            if (!flag2) {
                stringbuffer.append("#" + Platform.FILE_NEWLINE);
            } else if (TextUtils.getValue(hashmap, "_fileEncoding").length() == 0) {
                hashmap.put("_fileEncoding", "UTF-8");
            }
            printFrame(stringbuffer, hashmap, s, flag, flag1);
        }

    }

    static void printFrame(StringBuffer stringbuffer, HashMap hashmap, String s, boolean flag, boolean flag1) {
        boolean flag2 = s != null;
        if (hashmap.get("noSlotFilter") != null) {
            flag2 = false;
        }
        Enumeration enumeration = hashmap.keys();
        if (flag1) {
            Array array = new Array();
            for (; enumeration.hasMoreElements(); array.add(enumeration.nextElement())) {
            }
            Sorting.sort(array, new LessEqualPropertyName());
            enumeration = array.elements();
        }
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            String s1;
            if (obj instanceof StringProperty) {
                s1 = ((StringProperty) obj).getName();
            } else {
                s1 = (String) obj;
            }
            Enumeration enumeration1 = hashmap.values(obj);
            while (enumeration1.hasMoreElements()) {
                Object obj1 = enumeration1.nextElement();
                boolean flag3 = true;
                if (flag2) {
                    flag3 = s1.startsWith(s);
                    if (!flag) {
                        flag3 = !flag3;
                    }
                }
                if (flag3) {
                    if (allowNestedFrames && (obj1 instanceof HashMap)) {
                        stringbuffer.append(s1 + "={" + Platform.FILE_NEWLINE);
                        printFrame(stringbuffer, (HashMap) obj1, s, flag, flag1);
                        stringbuffer.append("}" + Platform.FILE_NEWLINE);
                    } else if (obj1 instanceof Array) {
                        Enumeration enumeration2 = ((Array) obj1).elements();
                        while (enumeration2.hasMoreElements()) {
                            stringbuffer.append(s1 + "=" + enumeration2.nextElement() + Platform.FILE_NEWLINE);
                        }
                    } else {
                        if (containsPasswordInName(s1)) {
                            obj1 = "" + TextUtils.obscure((String) obj1);
                        }
                        String s2 = ("" + obj1).trim();
                        if (s2.length() != 0 || flag1) {
                            stringbuffer.append(s1 + "=" + s2 + Platform.FILE_NEWLINE);
                        }
                    }
                }
            }
        }
    }

    public static boolean forceMangleOnReading() throws IOException {
        if (!forceMangleInited) {
            String s = Platform.getRoot() + File.separator + "classes" + File.separator + "Scrambler.class";
            if ((new File(s)).exists()) {
                forceMangleOnRead = true;
                forceMangleOnWrite = true;
            } else {
                forceMangleOnRead = false;
                forceMangleOnWrite = false;
            }
            forceMangleInited = true;
            if (forceMangleOnRead) {
                safetyChecks();
            }
        }
        return forceMangleOnRead;
    }

    public static String safetyChecks() {
        String s = "";
        try {
            String s1 = Platform.getRoot() + File.separator + "groups" + File.separator + "master.config";
            File file = new File(s1);
            if (!file.exists()) {
                throwError("master.config", "missing");
            }
            Array array = readFromFile(s1);
            if (array.size() != 1) {
                throwError("master.config", "truncated");
            }
            HashMap hashmap = (HashMap) array.at(0);
            if (hashmap.get("_initialMonitorDelay") == null) {
                throwError("master.config", "corrupt");
            }
            String s2 = Platform.getRoot() + File.separator + "groups" + File.separator + "users.config";
            File file1 = new File(s2);
            if (!file1.exists()) {
                throwError("users.config", "missing");
            }
            array = readFromFile(s2);
            if (array.size() < 3) {
                throwError("users.config", "truncated");
            }
            HashMap hashmap1 = (HashMap) array.at(1);
            if (hashmap1.get("_realName") == null) {
                throwError("users.config", "corrupt");
            }
        } catch (Exception exception) {
            s = "error: " + exception;
        }
        return s;
    }

    public static void setMangle(boolean flag, StringBuffer stringbuffer) throws IOException {
        String s = Platform.getRoot() + File.separator + "classes" + File.separator + "Scramble.class";
        String s1 = Platform.getRoot() + File.separator + "classes" + File.separator + "Scrambler.class";
        if (flag) {
            stringbuffer.append("Encoding\n");
            FileUtils.copyFile(s, s1);
        } else {
            stringbuffer.append("Decoding\n");
            (new File(s1)).delete();
        }
        forceMangleInited = true;
        forceMangleOnWrite = flag;
        forceMangleOnRead = false;
        File file = new File(Platform.getRoot() + File.separator + "groups");
        String as[] = file.list();
        for (int i = 0; i < as.length; i++) {
            String s2 = as[i];
            if (isConfig(s2)) {
                stringbuffer.append(s2 + '\n');
                String s3 = Platform.getRoot() + File.separator + "groups" + File.separator + s2;
                Array array = readFromFile(s3);
                writeToFile(s3, array);
            }
        }

        forceMangleOnRead = flag;
        stringbuffer.append(safetyChecks());
    }

    public static void logError(String s, String s1) {
        try {
            throwError(s, s1);
        } catch (Exception exception) {
        }
    }

    public static boolean containsPasswordInName(String s) {
        return s.indexOf("assword") != -1 || s.equals("_pwd") || s.equals("_senderUserPwd") || s.equals("_userPwd");
    }

    public static void throwError(String s, String s1) throws IOException {
        System.out.println("*** error: " + s + ", " + s1);
        File file = new File(Platform.getRoot() + File.separator + "logs" + File.separator + "config.log");
        RandomAccessFile randomaccessfile = null;
        try {
            randomaccessfile = new RandomAccessFile(file, "rw");
            randomaccessfile.seek(randomaccessfile.length());
            String s2 = "" + new Date();
            String s3 = s2 + '\t' + s + '\t' + s1 + Platform.FILE_NEWLINE;
            randomaccessfile.writeBytes(s3);
        } catch (IOException ioexception) {
            System.err.println("Could not open log file " + file);
        } finally {
            try {
                if (randomaccessfile != null) {
                    randomaccessfile.close();
                }
            } catch (IOException ioexception1) {
            }
        }
        throw new IOException(s + ", " + s1);
    }

    public static Array readFromFile(String s) throws IOException {
        boolean flag = forceMangleOnReading();
        return readFromFile(s, flag);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @return
     * @throws IOException
     */
    static Array readFromFile(String s, boolean flag) throws IOException {
        synchronized (FileUtils.getFileLock(s)) {
            StringBuffer stringbuffer = null;
            Array array = null;
            try {
                stringbuffer = FileUtils.readUTF8File(s);
            } catch (IOException e) {
                if (stringbuffer == null || stringbuffer.length() == 0) {
                    if (readMasterConfig && s.indexOf("master.config") >= 0) {
                        LogManager.log("error", "File(" + s + ") is missing");
                    }
                    File file = new File(s);
                    String s2 = file.getParent();
                    File file1 = null;
                    Object obj1 = null;
                    int j = 100;
                    if (readMasterConfig) {
                        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                        j = siteviewgroup.getSettingAsLong("_backups2Keep", 1);
                        String s3 = s + ".bak" + (j <= 1 ? "" : ".1");
                        file1 = new File(s3);
                    } else {
                        String s4 = s + ".bak";
                        file1 = new File(s4);
                        if (!file1.exists()) {
                            file1 = new File(s4 + ".1");
                        }
                    }
                    try {
                        if (popBackup(file, file1, s2, j) && readMasterConfig) {
                            LogManager.log("error", "File(" + s + ") replacing with backup");
                        } else if (readMasterConfig && s.indexOf("master.config") >= 0) {
                            LogManager.log("error", "File(" + s + ") CANNOT replace backup. No backup found.");
                        }
                    } catch (IOException e1) {
                        if (readMasterConfig && s.indexOf("master.config") >= 0) {
                            LogManager.log("error", "File(" + s + ") Exception(" + e1.getMessage()
                                    + ") replacing backup");
                        }
                    }
                    stringbuffer = FileUtils.readUTF8File(s);
                }
            }
            array = mangleIt(s, stringbuffer.toString(), flag);
            if (s.indexOf("master.config") >= 0) {
                readMasterConfig = true;
                for (int i = 0; i < array.size(); i++) {
                    String s1 = (String) array.at(i);
                    if (s1.trim().startsWith("#")) {
                        array.remove(i);
                        i--;
                    }
                }

            }
            return readFrames(array.elements());
        }
    }

    static Array mangleIt(String s, String s1, boolean flag) throws IOException {
        boolean flag1 = s1.startsWith("=");
        if (flag1) {
            int i = s1.indexOf("(0x)");
            if (i == -1) {
                throwError(s, "format");
            }
            long l = TextUtils.toLong(s1.substring(1, i));
            s1 = s1.substring(i);
            s1 = TextUtils.enlighten(s1);
            long l1 = PlatformNew.crc(s1);
            if (l != l1) {
                logError(s, "checksum");
            }
        } else if (flag && s.indexOf("groups") != -1) {
            throwError(s, "encoding problem");
        }
        s1 = TextUtils.replaceChar(s1, '\r', "");
        Array array = Platform.split('\n', s1);
        return array;
    }

    public static Array readFrames(BufferedReader bufferedreader) throws IOException {
        throw new IOException("unimplemented");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param enumeration
     * @return
     * @throws IOException
     */
    static Array readFrames(Enumeration enumeration) throws IOException {
        Array array = new Array();
        while (true) {
            HashMap hashmap = readFrame(enumeration, "#");
            if (hashmap != null) {
                array.add(hashmap);
            } else {
                return array;
            }
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param enumeration
     * @param s
     * @return
     * @throws IOException
     */
    private static HashMap readFrame(Enumeration enumeration, String s) throws IOException {
        HashMapOrdered hashmapordered = null;
	int iii=0;
        while (enumeration.hasMoreElements()) {
			if(iii>100)
				System.out.println(++iii);
            String s1 = (String) enumeration.nextElement();
            if (s1 == null) {
                break;
            }
            if (!s1.startsWith("//")) {
                if (hashmapordered == null) {
                    hashmapordered = new HashMapOrdered(true);
                }
                if (s1.startsWith(s)) {
                    return hashmapordered;
                }
                int i = s1.indexOf('=');
                if (i > 0) {
                    String s2 = s1.substring(0, i);
                    String s3 = s1.substring(i + 1);
                    if (allowNestedFrames && s3.startsWith("{")) {
                        HashMap hashmap = readFrame(enumeration, "}");
                        if (hashmap != null) {
                            hashmapordered.add(s2, hashmap);
                        }
                    } else {
                        if (containsPasswordInName(s2)) {
                            s3 = TextUtils.enlighten(s3);
                        }
                        hashmapordered.add(s2, s3);
                    }
                }
            }
        }
        return hashmapordered;
    }

    public static void touchFile(String s) {
        synchronized (FileUtils.getFileLock(s)) {
            RandomAccessFile randomaccessfile = null;
            try {
                randomaccessfile = new RandomAccessFile(s, "rw");
                randomaccessfile.seek(0L);
                byte abyte0[] = new byte[1];
                randomaccessfile.read(abyte0, 0, abyte0.length);
                randomaccessfile.seek(0L);
                randomaccessfile.write(abyte0, 0, abyte0.length);
            } catch (IOException ioexception) {
                LogManager.log("Error", "Exception in FrameFile.touchFile()" + ioexception.getMessage());
                ioexception.printStackTrace();
            } finally {
                try {
                    if (randomaccessfile != null) {
                        randomaccessfile.close();
                    }
                } catch (IOException ioexception1) {
                    LogManager.log("Error", "Exception in FrameFile.touchFile()" + ioexception1.getMessage());
                    ioexception1.printStackTrace();
                }
            }
        }
    }

    static int gi(HashMap hashmap, String s) {
        String s1 = TextUtils.getValue(hashmap, s);
        s1 = s1.trim();
        if (s1.equals("most important")) {
            return 3;
        }
        if (s1.equals("important")) {
            return 2;
        }
        if (s1.equals("a little important")) {
            return 1;
        }
        return !s1.equals("not important") ? 0 : 0;
    }

    static String gv(HashMap hashmap, String s) {
        String s1 = TextUtils.getValue(hashmap, s);
        s1 = TextUtils.replaceString(s1, "   ", "\\n");
        s1 = s1.trim();
        s1 = s1.replace(',', ' ');
        return s1;
    }

    public static boolean isConfig(String s) {
        return s.endsWith(".mg") || s.endsWith(".dyn") || s.endsWith(".config");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        if (args[0].indexOf("mangle") != -1) {
            boolean flag = true;
            int i = 0;
            if (args[0].equals("-demangle")) {
                flag = false;
            }
            StringBuffer stringbuffer = new StringBuffer();
            if (args.length == 2) {
                String s2 = args[1];
                Array array = readFromFile(s2, false);
                boolean flag6 = s2.endsWith(".config");
                printFrames(stringbuffer, array, null, false, flag6);
                if (flag) {
                    stringbuffer = mangle(stringbuffer);
                }
            } else {
                try {
                    setMangle(flag, stringbuffer);
                } catch (Exception exception) {
                    System.out.println("error: " + exception);
                    i = -1;
                }
            }
            System.out.print(stringbuffer);
            System.out.flush();
            System.exit(i);
        }
        if (args.length < 2) {
            System.out.println("unknown command");
            System.exit(-1);
        }
        if (args[0].startsWith("-test")) {
            File file = new File(args[1]);
            String args1[] = file.list();
            int j = 0;
            int k = 0;
            String s4 = args[1];
            String s6 = args[2];
            for (int i1 = 0; i1 < args1.length; i1++) {
                if (!args1[i1].endsWith(".mg") && !args1[i1].endsWith(".config")) {
                    continue;
                }
                Array array2 = readFromFile(s4 + "/" + args1[i1]);
                Array array3 = new Array();
                Enumeration enumeration = array2.elements();
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap = (HashMap) enumeration.nextElement();
                    String s9 = TextUtils.getValue(hashmap, "_class");
                    if (!s9.equals("SiteSeer2Monitor")) {
                        hashmap.remove("_alertCondition");
                        hashmap.remove("email");
                        hashmap.remove("_mailServer");
                        hashmap.remove("_mailServerBackup");
                        hashmap.remove("_pagerPort");
                        hashmap.remove("_pagerPortBackup");
                        hashmap.remove("_errorFrequency");
                        array3.add(hashmap);
                    }
                } 
                if (array3.size() > 0) {
                    k++;
                    System.out.println("writing " + s6 + "/" + args1[i1]);
                    writeToFile(s6 + "/" + args1[i1], array3);
                }
            }

            System.out.println("------------------");
            System.out.println("groups: " + k);
            System.out.println("monitors: " + j);
            System.exit(0);
        }
        if (args[0].startsWith("-monitors")) {
            File file1 = new File(args[1]);
            boolean flag2 = args[0].equals("-monitorsGlobal");
            boolean flag3 = args[0].equals("-monitorsAndReports");
            boolean flag4 = args[0].equals("-monitorsTrans");
            boolean flag5 = args[0].equals("-monitorsAndFrame0");
            String args2[] = file1.list();
            int j1 = 0;
            int k1 = 0;
            String s7 = args[1];
            String s8 = args[2];
            for (int l1 = 0; l1 < args2.length; l1++) {
                if (!args2[l1].endsWith(".mg")) {
                    continue;
                }
                Array array4 = readFromFile(s7 + "/" + args2[l1]);
                Array array5 = new Array();
                Enumeration enumeration1 = array4.elements();
                HashMap hashmap1 = new HashMap();
                if (enumeration1.hasMoreElements()) {
                    hashmap1 = (HashMap) enumeration1.nextElement();
                }
                String s10 = TextUtils.getValue(hashmap1, "_disabled");
                if (s10.equals("true")) {
                    continue;
                }
                while (enumeration1.hasMoreElements()) {
                    HashMap hashmap2 = (HashMap) enumeration1.nextElement();
                    if (Monitor.isReportFrame(hashmap2) && flag3) {
                        hashmap2.remove("tabfile");
                        hashmap2.remove("emailData");
                        hashmap2.remove("xmlfile");
                        hashmap2.remove("xmlEmailData");
                        hashmap2.remove("email");
                        hashmap2.remove("attachReport");
                        array5.add(hashmap2);
                    } else if (Monitor.isMonitorFrame(hashmap2)) {
                        String s11 = TextUtils.getValue(hashmap2, "_class");
                        if (!s11.equals("SiteSeer2Monitor") && (!flag2 || s11.startsWith("URLRemote"))
                                && (!flag4 || s11.startsWith("URLSequence") || s11.startsWith("URLRemoteSequence"))) {
                            if (s11.equals("FTPMonitor")) {
                                hashmap2.put("_disabled", "checked");
                            }
                            if (TextUtils.getValue(hashmap2, "_disabled").length() <= 0) {
                                hashmap2.put("_frequency", "6000000");
                                hashmap2.remove("_errorFrequency");
                                hashmap2.remove("_alertCondition");
                                hashmap2.remove("email");
                                hashmap2.remove("_mailServer");
                                hashmap2.remove("_mailServerBackup");
                                hashmap2.remove("_pagerPort");
                                hashmap2.remove("_pagerPortBackup");
                                array5.add(hashmap2);
                            }
                        }
                    }
                } 
                if (array5.size() <= 0) {
                    continue;
                }
                if (flag5) {
                    hashmap1.remove("_alertCondition");
                    array5.pushFront(hashmap1);
                } else {
                    HashMap hashmap3 = new HashMap();
                    hashmap3.put("_nextID", "10000");
                    hashmap3.put("_disabled", TextUtils.getValue(hashmap1, "_disabled"));
                    hashmap3.put("_name", TextUtils.getValue(hashmap1, "_name"));
                    array5.pushFront(hashmap3);
                }
                k1++;
                System.out.println("writing " + s8 + "/" + args2[l1]);
                writeToFile(s8 + "/" + args2[l1], array5);
            }

            System.out.println("------------------");
            System.out.println("groups: " + k1);
            System.out.println("monitors: " + j1);
            System.exit(0);
        }
        boolean flag1 = false;
        String s = "";
        String s1 = "";
        String s3 = "";
        String s5 = "";
        for (int l = 0; l < args.length; l++) {
            if (args[l].equals("-s")) {
                flag1 = true;
                continue;
            }
            if (s.length() == 0) {
                s = args[l];
                continue;
            }
            if (s1.length() == 0) {
                s1 = args[l];
                continue;
            }
            if (s3.length() == 0) {
                s3 = args[l];
                continue;
            }
            if (s5.length() == 0) {
                s5 = args[l];
            }
        }

        if (s1.length() == 0) {
            s1 = s;
        }
        try {
            System.out.print("Reading " + s + "...");
            Array array1 = readFromFile(s);
            if (s3.length() > 0 && s5.length() > 0) {
                String args3[] = { s3, s5 };
                TextUtils.replaceInHashMapList(array1, args3, null);
            }
            System.out.println("done");
            System.out.println("Writing " + s1 + "...");
            writeToFile(s1, array1, flag1);
            System.out.println("done");
        } catch (Exception exception1) {
            System.out.println("Exception: " + exception1);
        }
        System.exit(0);
    }

}
