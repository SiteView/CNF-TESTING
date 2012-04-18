/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.ProcessUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.StringTokenizer;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.ProcessUtils:
// ProcessPool

public class ProcessMgr {

    public static final int LOG_LEVEL_NONE = 0;

    public static final int LOG_LEVEL_ERROR = 1;

    public static final int LOG_LEVEL_WARNING = 2;

    public static final int LOG_LEVEL_INFO = 3;

    public static final int LOG_LEVEL_ALL_DATA = 4;

    public static HashMap ProcessPoolMap;

    public static int processTimeout = -1;

    public static int maxProcessesPerPool = 20;

    public static int processKillTimeout = 0x1d4c0;

    public static int logLevel = 0;

    public ProcessMgr() {
    }

    public static boolean exec(String s, Array array, int ai[], com.dragonflow.SiteView.AtomicMonitor atomicmonitor) throws java.io.IOException {
        if (s == null || s.trim().equals("")) {
            return false;
        }
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(s);
        String s1 = stringtokenizer.nextToken();
        com.dragonflow.ProcessUtils.ProcessPool processpool = (com.dragonflow.ProcessUtils.ProcessPool) ProcessPoolMap.get(com.dragonflow.ProcessUtils.ProcessMgr.getExeName(s1));
        if (processpool != null) {
            boolean flag = processpool.exec(s, array, ai, atomicmonitor);
            if (!flag) {
                flag = processpool.exec(s, array, ai, atomicmonitor);
            }
            return true;
        } else {
            return false;
        }
    }

    private static String getExeName(String s) {
        int i = s.lastIndexOf("\\");
        int j = s.lastIndexOf("/");
        String s1 = s.substring(i <= j ? j + 1 : i + 1);
        int k = s1.indexOf(".");
        if (k > -1) {
            s1 = s1.substring(0, k);
        }
        return s1;
    }

    public static void stopAllProcesses() {
        for (Enumeration enumeration = ProcessPoolMap.elements(); enumeration.hasMoreElements(); ((com.dragonflow.ProcessUtils.ProcessPool) enumeration.nextElement()).stopProcesses()) {
        }
    }

    static {
        ProcessPoolMap = new HashMap();
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_processPoolTimeout");
        if (s.length() > 0) {
            processTimeout = com.dragonflow.Utils.TextUtils.toInt(s);
        } else {
            com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            processTimeout = (siteviewgroup.getSettingAsLong("_restartDelay", 24) + 1) * 3600;
        }
        String s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_processPoolMaxPerPool");
        if (s1.length() > 0) {
            maxProcessesPerPool = com.dragonflow.Utils.TextUtils.toInt(s1);
        }
        String s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_processPoolKillTimeout");
        if (s2.length() > 0) {
            processKillTimeout = com.dragonflow.Utils.TextUtils.toInt(s2);
        }
        String s3 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_processPoolDebugLevel");
        if (s3.length() > 0) {
            logLevel = com.dragonflow.Utils.TextUtils.toInt(s3);
        }
        ProcessPoolMap.put("perfex", new ProcessPool(com.dragonflow.SiteView.Platform.perfexCommand("")));
    }
}
