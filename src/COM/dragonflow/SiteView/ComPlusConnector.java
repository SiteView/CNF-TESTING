/*
 * 
 * Created on 2005-2-15 12:32:31
 *
 * ComPlusConnector.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ComPlusConnector</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jgl.Array;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ComPlusProbeResetException, Monitor

public class ComPlusConnector {

    private static Map siteViewMetricsMap = Collections.synchronizedMap(new HashMap());

    private static long probeUpdateInterval = 0L;

    private boolean gettingData[] = { false };

    private boolean probeReset;

    private StringBuffer dataStore;

    private Map myMetricsMap;

    private String hostName;

    private String hostPort;

    private String authUserName;

    private String authPassword;

    private String proxyURL;

    private String proxyUserName;

    private String proxyPassword;

    private int timeout;

    private static final String HTTP_GET_METRICLIST_CMD = "/ComPlus?op=list";

    private static final String HTTP_GET_METRICVALUES_CMD = "/ComPlus?op=values";

    private static final String HTTP_REMOVE_PROBEMETRIC_CMD = "/ComPlus?op=remove";

    private static final String HTTP_ADD_PROBEMETRIC_CMD = "/ComPlus?op=add";

    private static final String HTTP_RESET_PROBEMETRICLIST_CMD = "/ComPlus?op=reset";

    private static final String HTTP_GET_PROBEMETRICLISTSIZE_CMD = "/ComPlus?op=count";

    private static boolean debug = false;

    private static HashSet debug_probeMetricsList = new HashSet();

    public ComPlusConnector(String s, String s1, String s2, String s3, String s4, String s5, String s6) {
        hostName = s;
        hostPort = s1;
        authUserName = s2;
        authPassword = s3;
        proxyURL = s4;
        proxyUserName = s5;
        proxyPassword = s6;
        timeout = 60;
        probeReset = false;
        dataStore = new StringBuffer();
        myMetricsMap = Collections.synchronizedMap(new HashMap());
        printDebug("New comPlusConnector created for " + s + ":" + s1);
    }

    public static ComPlusConnector getComPlusConnection(String s, String s1, String s2, String s3, String s4, String s5, String s6, Monitor monitor, int i) {
        ComPlusConnector complusconnector = null;
        String s7 = s + ":" + s1;
        synchronized (siteViewMetricsMap) {
            complusconnector = (ComPlusConnector) siteViewMetricsMap.get(s7);
            if (complusconnector == null) {
                complusconnector = new ComPlusConnector(s, s1, s2, s3, s4, s5, s6);
                siteViewMetricsMap.put(s7, complusconnector);
            }
        }
        complusconnector.timeout = i;
        if (!complusconnector.probeReset && complusconnector.resetProbeMetricList(monitor)) {
            complusconnector.probeReset = true;
        }
        if (debug) {
            complusconnector.printConnectorTable();
        }
        return complusconnector;
    }

    public static void setProbeUpdateInterval(long l) {
        probeUpdateInterval = l;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringbuffer
     * @param monitor
     * @return
     */
    public String getMetricValuesFromProbe(StringBuffer stringbuffer, Monitor monitor) {
        synchronized (this.gettingData) {
            if (gettingData[0]) {
                try {
                    StringBuffer stringbuffer1;
                    stringbuffer1 = dataStore;
                    gettingData.wait();
                    return stringbuffer1.toString();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gettingData[0] = true;
                dataStore = new StringBuffer();
            }
        }

        getNewMetricValuesFromProbe(dataStore, stringbuffer, monitor);
        synchronized (this.gettingData) {
            gettingData[0] = false;
            gettingData.notifyAll();
            return dataStore.toString();
        }
    }

    public Map getMetricsMap() {
        return myMetricsMap;
    }

    private boolean resetProbeMetricList(Monitor monitor) {
        boolean flag = true;
        if (debug) {
            debug_probeMetricsList = new HashSet();
            printDebugProbeMetricList("resetProbeMetricList");
        }
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        Array array = new Array();
        try {
            flag = sendCommandToProbe("/ComPlus?op=reset", array, stringbuffer, stringbuffer1, false, monitor, null, 0);
        } catch (ComPlusProbeResetException complusproberesetexception) {
        }
        return flag;
    }

    public boolean getMetricListFromProbe(StringBuffer stringbuffer, StringBuffer stringbuffer1, Monitor monitor, int i) {
        boolean flag = true;
        Array array = new Array();
        try {
            flag = sendCommandToProbe("/ComPlus?op=list", array, stringbuffer, stringbuffer1, false, monitor, null, i);
        } catch (ComPlusProbeResetException complusproberesetexception) {
        }
        return flag;
    }

    private boolean addProbeMetric(ArrayList arraylist, StringBuffer stringbuffer, boolean flag, Monitor monitor) throws ComPlusProbeResetException {
        boolean flag1 = true;
        Array array = new Array();
        StringBuffer stringbuffer1 = new StringBuffer();
        if (arraylist.size() <= 0) {
            return flag1;
        }
        for (int i = 0; i < arraylist.size(); i ++) {
            String s = (String) arraylist.get(i);
            if (debug) {
                debug_probeMetricsList.add(s);
            }
            array.add(URLMonitor.CUSTOM_CONTENT + s);
        }

        if (debug) {
            printDebugProbeMetricList("addProbeMetric");
        }
        flag1 = sendCommandToProbe("/ComPlus?op=add", array, stringbuffer1, stringbuffer, flag, monitor, null, 0);
        try {
            Thread.sleep(probeUpdateInterval);
        } catch (InterruptedException interruptedexception) {
        }
        return flag1;
    }

    public boolean removeProbeMetric(ArrayList arraylist, StringBuffer stringbuffer, Monitor monitor) throws ComPlusProbeResetException {
        Array array = new Array();
        StringBuffer stringbuffer1 = new StringBuffer();
        for (int i = 0; i < arraylist.size(); i ++) {
            String s = (String) arraylist.get(i);
            if (debug) {
                debug_probeMetricsList.remove(s);
                printDebugProbeMetricList("removeProbeMetric");
            }
            array.add(URLMonitor.CUSTOM_CONTENT + s);
        }

        return sendCommandToProbe("/ComPlus?op=remove", array, stringbuffer1, stringbuffer, true, monitor, null, 0);
    }

    private boolean sendCommandToProbe(String s, Array array, StringBuffer stringbuffer, StringBuffer stringbuffer1, boolean flag, Monitor monitor, String s1, int i) throws ComPlusProbeResetException {
        boolean flag1 = true;
        String s2 = null;
        int j = timeout;
        String s3 = "http://" + hostName + ":" + hostPort + s;
        SocketSession socketsession = SocketSession.getSession(monitor);
        if (s1 != null) {
            s3 = s1;
        }
        if (flag && (s.equals("/ComPlus?op=remove") || s.equals("/ComPlus?op=add"))) {
            try {
                PingProbeToCheckResetState(monitor);
            } catch (ComPlusProbeResetException complusproberesetexception) {
                refreshProbeMetricsList(stringbuffer1, monitor);
                throw complusproberesetexception;
            }
        }
        if (i > 0) {
            j = i;
        }
        printDebug("Sending cmd to probe with timeout " + j + " seconds");
        socketsession.setEncodePostData("forceNoEncode");
        long al[] = URLMonitor.checkURL(socketsession, s3, null, null, proxyURL, proxyUserName, proxyPassword, array, authUserName, authPassword, null, stringbuffer, 0x7fffffffffffffffL, null, 0, j * 1000, null);
        if (al[0] != 200L || al[0] == -996L && stringbuffer.length() == 0) {
            stringbuffer1.append(al[0] + "\n Explanation: " + URLMonitor.lookupStatus(al[0]));
            return false;
        }
        s2 = stringbuffer.toString();
        if (s.equals("/ComPlus?op=values") && probeAppearsReStarted(s2)) {
            flag1 = refreshProbeMetricsList(stringbuffer1, monitor);
            throw new ComPlusProbeResetException(stringbuffer1.toString());
        } else {
            return flag1;
        }
    }

    private boolean probeAppearsReStarted(String s) {
        boolean flag = true;
        int i = s.indexOf("Content-Length:");
        if (i > 0) {
            int j = s.indexOf("\r\n", i);
            if (j > 0) {
                String s1 = s.substring(i + 15, j);
                int k = Integer.parseInt(s1.trim());
                if (k > 0) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    private boolean PingProbeToCheckResetState(Monitor monitor) throws ComPlusProbeResetException {
        boolean flag = true;
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        Array array = new Array();
        boolean flag1 = sendCommandToProbe("/ComPlus?op=count", array, stringbuffer, stringbuffer1, false, monitor, null, 0);
        if (flag1) {
            String s = stringbuffer.toString();
            int i = s.indexOf("\r\n\r\n");
            if (i > 0) {
                int j = s.indexOf("active metrics", i + 4);
                if (j > 0) {
                    String s1 = s.substring(i + 4, j - 1);
                    int k = Integer.parseInt(s1.trim());
                    if (k > 0) {
                        flag = false;
                    }
                }
            }
        }
        if (flag) {
            throw new ComPlusProbeResetException("Probe has zero active monitors.");
        } else {
            return flag;
        }
    }

    private String endTrimCRLF(String s) {
        String s1 = s;
        int i = s.indexOf("\r\n");
        if (i <= 0) {
            i = s.indexOf('\r');
        }
        if (i > 0) {
            s1 = s.substring(0, i);
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param stringbuffer
     * @param monitor
     * @return
     */
    public boolean syncProbeWithSSMetricsMap(HashMap hashmap, StringBuffer stringbuffer, Monitor monitor) {
        boolean flag = true;
        boolean flag1 = false;
        hashmap.clear();
        int i = 0;

        synchronized (this.myMetricsMap) {

            String s2 = getMetricValuesFromProbe(stringbuffer, monitor);
            if (stringbuffer.length() != 0) {
                return false;
            }
            i = parseProbeReturnValuesIntoHashMap(hashmap, s2);
            if (i == 0) {
                return true;
            }

            Set set = myMetricsMap.keySet();
            Iterator iterator = set.iterator();
            ArrayList arraylist = new ArrayList();
            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                if (!hashmap.containsKey(s)) {
                    arraylist.add(s);
                }
            }

            if (arraylist.size() > 0) {
                try {
                    flag = addProbeMetric(arraylist, stringbuffer, true, monitor);
                } catch (ComPlusProbeResetException e) {
                    return true;
                }
            }
            flag1 = true;

            Iterator iterator1 = hashmap.keySet().iterator();
            arraylist.clear();
            while (iterator1.hasNext()) {
                String s1 = (String) iterator1.next();
                if (!set.contains(s1)) {
                    arraylist.add(s1);
                }
            }

            if (arraylist.size() < 0) {
                try {
                    flag = removeProbeMetric(arraylist, stringbuffer, monitor);
                } catch (ComPlusProbeResetException e) {
                    return true;
                }

            }
            flag1 = true;

            if (flag1) {
                hashmap.clear();
            }
            return flag;
        }
    }

    private boolean refreshProbeMetricsList(StringBuffer stringbuffer, Monitor monitor) {
        printDebug("refreshProbeMetricList called.\n");
        boolean flag = true;
        ArrayList arraylist = new ArrayList();
        synchronized (myMetricsMap) {
            String s;
            for (Iterator iterator = myMetricsMap.keySet().iterator(); iterator.hasNext(); arraylist.add(s)) {
                s = (String) iterator.next();
            }

            if (arraylist.size() > 0) {
                try {
                    flag = addProbeMetric(arraylist, stringbuffer, false, monitor);
                } catch (ComPlusProbeResetException complusproberesetexception) {
                }
            }
        }
        return flag;
    }

    public int parseProbeReturnValuesIntoHashMap(HashMap hashmap, String s) {
        int i = s.indexOf("\r\n\r\n");
        int j = 0;
        if (i < 0) {
            return j;
        }
        if (i + 5 >= s.length()) {
            return j;
        }
        String as[] = TextUtils.split(s.substring(i + 4), "\n");
        for (int k = 0; k < as.length; k ++) {
            String s1 = as[k];
            int l = s1.indexOf("=");
            if (l > 0) {
                String s2 = s1.substring(0, l).trim();
                String s3 = endTrimCRLF(s1.substring(l + 1));
                hashmap.put(s2, s3);
                j ++;
            }
        }

        return j;
    }

    private boolean getNewMetricValuesFromProbe(StringBuffer stringbuffer, StringBuffer stringbuffer1, Monitor monitor) {
        boolean flag = true;
        String s = null;
        Array array = new Array();
        try {
            flag = sendCommandToProbe("/ComPlus?op=values", array, stringbuffer, stringbuffer1, true, monitor, s, 0);
        } catch (ComPlusProbeResetException complusproberesetexception) {
            flag = false;
        }
        return flag;
    }

    private void printDebugProbeMetricList(String s) {
        Iterator iterator = debug_probeMetricsList.iterator();
        System.out.println(s + " CALLED! PROBE METRIC LIST is Now: ");
        String s1;
        for (; iterator.hasNext(); System.out.println(s1 + ", ")) {
            s1 = (String) iterator.next();
        }

        System.out.println("\n");
    }

    private void printConnectorTable() {
        System.out.println("ComPlusConnect Table elements: ");
        String s;
        for (Iterator iterator = siteViewMetricsMap.keySet().iterator(); iterator.hasNext(); System.out.println(s + ", ")) {
            s = (String) iterator.next();
        }

        System.out.println("\n");
    }

    private void printDebug(String s) {
        System.out.println(s);
    }

    static {
        String s = System.getProperty("ComPlusConnector.debug", "false");
        debug = s.equalsIgnoreCase("true");
    }
}
