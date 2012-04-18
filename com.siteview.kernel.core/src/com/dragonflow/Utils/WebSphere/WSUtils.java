/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;

public class WSUtils {

    static final int kWIN = 1;

    static final int kSUN = 2;

    static final int kSGI = 3;

    static final int kMac = 4;

    static final int kHP = 5;

    static final int kLinux = 6;

    static final int kMacOSX = 7;

    static final int kOtherUnix = 8;

    public WSUtils() {
    }

    public static String getRoot() {
        String s = null;
        if (com.dragonflow.Utils.WebSphere.WSUtils.getOs() == 4 && com.dragonflow.Utils.WebSphere.WSUtils.getRoot().equals(".")) {
            s = "/MacintoshHD/dev/SiteView/classes";
        }
        String s1 = java.lang.System.getProperty("PATH_TRANSLATED", java.lang.System.getProperty("user.dir"));
        int i = s1.toLowerCase().lastIndexOf("siteview");
        if (i != -1) {
            s = s1.substring(0, i + 9);
        } else {
//            int j = s1.toLowerCase().lastIndexOf("sitesc~");
			int j = s1.toLowerCase().lastIndexOf("sitevi~");			
            if (j != -1) {
                s = s1.substring(0, j + 8);
            }
        }
        return s;
    }

    public static int getOs() {
        byte byte0 = -1;
        String s = java.lang.System.getProperty("os.name").toUpperCase();
        if (s.startsWith("WINDOWS")) {
            byte0 = 1;
        } else if (s.equals("IRIX")) {
            byte0 = 3;
        } else if (s.equals("SOLARIS") || s.equals("SUNOS")) {
            byte0 = 2;
        } else if (s.equals("HP-UX")) {
            byte0 = 5;
        } else if (s.equals("LINUX")) {
            byte0 = 6;
        } else if (s.equals("MAC OS") || s.equals("MACOS")) {
            byte0 = 4;
        } else if (s.equals("MacOSX")) {
            byte0 = 7;
        }
        return byte0;
    }

    static String indent(int i) {
        char ac[] = new char[i];
        for (int j = 0; j < i; j ++) {
            ac[j] = ' ';
        }

        return new String(ac);
    }
}
