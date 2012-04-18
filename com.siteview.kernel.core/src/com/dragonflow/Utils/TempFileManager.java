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

import java.io.File;
import java.util.Date;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;

// Referenced classes of package com.dragonflow.Utils:
// GreaterFileModified, TextUtils, FileUtils

public class TempFileManager implements java.lang.Runnable {

    static String CACHE_DIR = "cache";

    static String TEMP_DIR = "temp";

    static String DELETABLE_DIR = "tempbysize";

    static String TEMP_BY_AGE_DIR = "tempbyage";

    static String PERSISTENCE_DIR = "persistent";

    static final String ageDelimiter = "--";

    public TempFileManager() {
    }

    public static String getCacheRoot() {
        return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + CACHE_DIR;
    }

    public static String getTempDirPath() {
        return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + CACHE_DIR + java.io.File.separator + TEMP_DIR;
    }

    public static String getTempAccordingToAgeDirPath() {
        return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + CACHE_DIR + java.io.File.separator + TEMP_BY_AGE_DIR;
    }

    public static String getTempAccordingToSizeDirPath() {
        return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + CACHE_DIR + java.io.File.separator + DELETABLE_DIR;
    }

    public static String getCachePersistencePath() {
        return com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + CACHE_DIR + java.io.File.separator + PERSISTENCE_DIR;
    }

    private static String getTempByAgeFileName(java.io.File file, long l) {
        String s = com.dragonflow.Utils.TextUtils.dateToFileName(new Date(com.dragonflow.SiteView.Platform.timeMillis()));
        return s + "--" + l + "--" + file.getName();
    }

    private static boolean timeHasExpired(java.io.File file, long l) {
        String s = file.getName();
        String as[] = s.split("--");
        if (as.length != 3) {
            return file.exists() && java.lang.System.currentTimeMillis() - file.lastModified() > l * 60L * 60L * 1000L;
        }
        java.util.Date date = com.dragonflow.Utils.TextUtils.fileNameToDate(as[0]);
        long l1 = java.lang.Long.parseLong(as[1]);
        long l2 = com.dragonflow.SiteView.Platform.timeMillis();
        return l2 - date.getTime() >= l1 * 60L * 60L * 1000L;
    }

    public static void addTempByAgeFile(java.io.File file, long l) {
        String s = com.dragonflow.Utils.TempFileManager.getTempByAgeFileName(file, l);
        java.io.File file1 = new File(com.dragonflow.Utils.TempFileManager.getTempAccordingToAgeDirPath(), s);
        com.dragonflow.Utils.FileUtils.copyFile(file, file1);
    }

    public void run() {
        java.io.File file = new File(com.dragonflow.Utils.TempFileManager.getTempDirPath());
        if (file.exists()) {
            java.io.File afile[] = file.listFiles();
            for (int i = 0; i < afile.length; i ++) {
                afile[i].delete();
            }

        }
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        int j = com.dragonflow.Utils.TextUtils.toInt(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_tempDirMaxSize"));
        int k = com.dragonflow.Utils.TextUtils.toInt(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_defaultTempFileAge"));
        if (k <= 0) {
            k = 24;
        }
        j *= 1024;
        java.io.File file1 = new File(com.dragonflow.Utils.TempFileManager.getTempAccordingToSizeDirPath());
        com.dragonflow.Utils.TempFileManager.controlDirSize(file1, j, null);
        java.io.File file2 = new File(com.dragonflow.Utils.TempFileManager.getTempAccordingToAgeDirPath());
        if (file2.exists()) {
            java.io.File afile1[] = file2.listFiles();
            for (int l = 0; l < afile1.length; l ++) {
                java.io.File file3 = afile1[l];
                if (file3.isDirectory()) {
                    if (com.dragonflow.Utils.TempFileManager.timeHasExpired(file3, k) && !deleteDirectory(file3)) {
                        com.dragonflow.Log.LogManager.log("Error", "SiteView unable to delete temp by age directory: " + file3.getAbsolutePath());
                    }
                    continue;
                }
                if (com.dragonflow.Utils.TempFileManager.timeHasExpired(file3, k)) {
                    file3.delete();
                }
            }

        }
    }

    private boolean deleteDirectory(java.io.File file) {
        boolean flag = true;
        if (file.isDirectory()) {
            java.io.File afile[] = file.listFiles();
            for (int i = 0; i < afile.length; i ++) {
                java.io.File file1 = afile[i];
                if (file1.isDirectory()) {
                    if (!deleteDirectory(file1)) {
                        flag = false;
                    }
                    continue;
                }
                if (!file1.delete()) {
                    flag = false;
                }
            }

            if (!file.delete()) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public static void controlDirSize(java.io.File file, int i, java.io.FilenameFilter filenamefilter) {
        if (file.exists()) {
            Array array = new Array(file.listFiles(filenamefilter));
            Sorting.sort(array, new GreaterFileModified(false));
            long l = 0L;
            for (int j = 0; j < array.size(); j ++) {
                java.io.File file1 = (java.io.File) array.get(j);
                if (l < (long) i) {
                    l += file1.length();
                } else {
                    file1.delete();
                }
            }

        }
    }

    static {
        java.io.File file = new File(com.dragonflow.Utils.TempFileManager.getCacheRoot());
        if (!file.exists()) {
            file.mkdir();
        }
        java.io.File file1 = new File(com.dragonflow.Utils.TempFileManager.getTempAccordingToSizeDirPath());
        if (!file1.exists()) {
            file1.mkdir();
        }
        java.io.File file2 = new File(com.dragonflow.Utils.TempFileManager.getTempAccordingToAgeDirPath());
        if (!file2.exists()) {
            file2.mkdir();
        }
        java.io.File file3 = new File(com.dragonflow.Utils.TempFileManager.getTempDirPath());
        if (!file3.exists()) {
            file3.mkdir();
        }
        java.io.File file4 = new File(com.dragonflow.Utils.TempFileManager.getCachePersistencePath());
        if (!file4.exists()) {
            file4.mkdir();
        }
    }
}
