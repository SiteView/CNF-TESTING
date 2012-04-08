/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebServerMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.FileProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.RateProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Braf;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.TextUtils;

public class WebServerMonitor extends ServerMonitor {

    static ScalarProperty pServerName;

    static FileProperty pFileName;

    static StringProperty pBytesColumn;

    static StringProperty pLastHits;

    static StringProperty pLastBytes;

    static StringProperty pLastHitsPerMinute;

    static StringProperty pLastBytesPerMinute;

    static StringProperty pLastRawHits;

    static StringProperty pLastRawBytes;

    static StringProperty pLastMeasurement;

    static StringProperty pLastFilePosition;

    private static final long ERROR_RESULT[] = { -1L, -1L, -1L, -1L, -1L };

    public WebServerMonitor() {
    }

    public static void main(String args[]) throws Exception {
        long l = System.currentTimeMillis();
        File file = new File(args[0]);
        long l1 = file.length();
        Braf braf = new Braf(args[0], l1 - 10000L);
        System.out.println("skip: " + (System.currentTimeMillis() - l));
        int i = 0;
        String s;
        do {
            i ++;
            s = braf.readLine();
        } while (s != null);
        System.out.println("lines: " + i + ", skip: " + (System.currentTimeMillis() - l));
    }

    boolean isError(long al[]) {
        return al[0] == -1L || al[2] == -1L || al[3] == -1L;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pServerName && Platform.isWindows()) {
            return Platform.getWebServers(getProperty(pMachineName));
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public boolean remoteCommandLineAllowed() {
        return false;
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        String s = super.getProperty(stringproperty);
        if (stringproperty == pServerName) {
            int i = s.indexOf('|');
            if (i == -1) {
                String s1 = (String) MasterConfig.getMasterConfig().get("_webServerType");
                if (s1 != null) {
                    s = s1 + "|" + s;
                }
            }
        }
        return s;
    }

    protected boolean update() {
        long l = 0L;
        long l1 = 0L;
        long l2 = 0L;
        try {
            l = Long.parseLong(getProperty(pLastRawHits));
            l1 = Long.parseLong(getProperty(pLastRawBytes));
            l2 = Long.parseLong(getProperty(pLastMeasurement));
        } catch (NumberFormatException numberformatexception) {
        }
        boolean flag = true;
        if (l2 == 0L) {
            long al[] = getStats();
            if (!isError(al)) {
                l = al[0];
                l1 = al[1];
                l2 = al[2];
                Platform.sleep(1000L);
            } else {
                flag = false;
            }
        }
        long al1[] = getStats();
        long l3 = al1[0];
        long l4 = al1[1];
        long l5 = al1[2];
        long l6 = al1[3];
        long l7 = l3;
        long l8 = l4;
        if (al1[4] == 1L) {
            l7 -= l;
            l8 -= l1;
        }
        float f = l5 - l2;
        float f1 = -1F;
        float f2 = -1F;
        if (!isError(al1) && f > 0.0F && l6 > 0L) {
            float f3 = f / (float) l6;
            f1 = 60F * ((float) l7 / f3);
            if (f1 < 0.0F) {
                f1 = 0.0F;
            }
            if (l4 != -1L) {
                f2 = 60F * ((float) l8 / f3);
                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }
            }
        } else {
            flag = false;
        }
        if (stillActive()) {
            synchronized (this) {
                if (flag) {
                    setProperty(pLastHits, String.valueOf(l7));
                    setProperty(pLastBytes, String.valueOf(l8));
                    setProperty(pLastHitsPerMinute, String.valueOf(f1));
                    setProperty(pLastRawHits, String.valueOf(l3));
                    setProperty(pLastRawBytes, String.valueOf(l4));
                    setProperty(pLastMeasurement, String.valueOf(l5));
                    setProperty(pMeasurement, getMeasurement(pLastHitsPerMinute, 100L));
                    String s = TextUtils.floatToString(f1, 2) + " hits/min";
                    if (f2 != -1F) {
                        s = s + ", " + TextUtils.floatToString(f2, 0) + " bytes/min";
                        setProperty(pLastBytesPerMinute, String.valueOf(f2));
                    } else {
                        setProperty(pLastBytesPerMinute, "n/a");
                    }
                    setProperty(pStateString, s);
                } else {
                    if (isError(al1)) {
                        setProperty(pStateString, "web server not found");
                    } else {
                        setProperty(pStateString, "no data");
                    }
                    setProperty(pMeasurement, 0);
                    setProperty(pNoData, "n/a");
                    setProperty(pLastHits, "n/a");
                    setProperty(pLastBytes, "n/a");
                    setProperty(pLastHitsPerMinute, "n/a");
                    setProperty(pLastBytesPerMinute, "n/a");
                    unsetProperty(pLastRawHits);
                    unsetProperty(pLastRawBytes);
                    setProperty(pLastMeasurement, String.valueOf(0));
                }
            }
        }
        return flag;
    }

    long[] getStats() {
        File file = pFileName.getValue(this);
        if (file == null || !file.exists()) {
            if (Platform.isWindows()) {
                return getStatsFromPerfex();
            } else {
                return ERROR_RESULT;
            }
        } else {
            return getStatsFromLogFile(file);
        }
    }

    long[] getStatsFromPerfex() {
        String s = getProperty(pMachineName);
        String s1 = getProperty(pServerName);
        String s2 = Platform.perfexCommand(s);
        String s3 = "unknown";
        String s4 = "unknown";
        int i = s1.indexOf('|');
        if (i != -1) {
            s4 = s1.substring(0, i);
            s3 = s1.substring(i + 1, s1.length());
        }
        String s5 = null;
        String s6 = null;
        String s7 = null;
        String s8 = null;
        String s9 = null;
        String s10 = null;
        boolean flag = false;
        if (s4.equals("WebSite")) {
            s5 = "WebServer";
            s6 = "Requests/sec:";
            s7 = null;
            s8 = "WebServer KBytes/sec:";
            s9 = null;
            s10 = "Data Bytes Sent/Sec:";
        } else if (s4.equals("Microsoft")) {
            s5 = "HTTP Service";
            s6 = "Get Requests:";
            s7 = "PERF_COUNTER_RAWCOUNT";
            s8 = "Bytes Total/sec:";
            s9 = "PERF_COUNTER_BULK_COUNT";
        } else if (s4.equals("Microsoft4")) {
            s5 = "Web Service";
            s6 = "Total Get Requests:";
            s7 = "PERF_COUNTER_RAWCOUNT";
            s8 = "Bytes Total/sec:";
            s9 = "PERF_COUNTER_BULK_COUNT";
        } else if (s4.equals("Netscape")) {
            s5 = "Netscape Server";
            s6 = "Server Total Requests:";
            s7 = "PERF_COUNTER_RAWCOUNT";
            s8 = "Server Total Bytes:";
            s9 = "PERF_COUNTER_RAWCOUNT";
        } else if (s4.startsWith("Netscape3")) {
            s5 = "Netscape Enterprise 3.0";
            if (s4.equals("Netscape35")) {
                s5 = "Netscape Enterprise 3.5";
            }
            s6 = "Server Total Requests:";
            s7 = "PERF_COUNTER_RAWCOUNT";
            s8 = "Server Total Bytes:";
            s9 = "PERF_COUNTER_RAWCOUNT";
        } else {
            LogManager.log("Error", "Unsupported web server type: " + s4);
            return ERROR_RESULT;
        }
        String s11 = s2 + " \"" + s5 + "\"";
        if (s.startsWith("http://")) {
            s11 = s + "perfex.exe?" + URLEncoder.encode("-cgi " + s5);
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s11, Platform.getLock(s));
        Enumeration enumeration = array.elements();
        long l = -1L;
        long l1 = -1L;
        long l2 = -1L;
        long l3 = -1L;
        boolean flag1 = false;
        if (s3 == null || s3.length() == 0) {
            flag1 = true;
        }
        while (enumeration.hasMoreElements()) {
            String s12 = (String) enumeration.nextElement();
            if (l < 0L) {
                l = TextUtils.findLong(s12, "PerfFreq:", null);
            }
            if (l1 < 0L) {
                l1 = TextUtils.findLong(s12, "PerfTime:", null);
            }
            if (flag1) {
                if (l3 < 0L) {
                    l3 = TextUtils.findLong(s12, s6, s7);
                }
                if (l2 < 0L) {
                    l2 = TextUtils.findLong(s12, s8, s9);
                }
                if (l2 < 0L && s10 != null) {
                    l2 = TextUtils.findLong(s12, s10, null);
                    flag = true;
                }
            } else if (s12.equals("name: " + s3)) {
                flag1 = true;
            }
            if (l3 >= 0L && l2 >= 0L) {
                break;
            }
        }

        if (s4.equals("WebSite") && !flag) {
            l2 *= 1024L;
        }
        long al[] = { l3, l2, l1, l, 1L };
        return al;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param file
     * @return
     */
    long[] getStatsFromLogFile(File file) {
        Braf braf = null;
        long al[];
        try {
            long l = 0L;
            long l1 = getPropertyAsLong(pLastMeasurement);
            try {
                l = Long.parseLong(getProperty(pLastFilePosition));
            } catch (NumberFormatException numberformatexception) {
                /* empty */
            }
            long l2 = file.length();
            if (l == 0L) {
                l = l2;
            } else if (l > l2) {
                l = l2;
            } else if (Platform.timeMillis() / 1000L - l1 > 0x2932e00L) {
                l = l2;
            }
            braf = new Braf(file.getPath(), l);
            setProperty(pLastFilePosition, String.valueOf(l2));
            int i = getPropertyAsInteger(pBytesColumn);
            long l3 = Platform.timeMillis() / 1000L;
            long l4 = 1L;
            long l5 = getSettingAsLong("_maxAmountToRead");
            long l6 = 0L;
            long l7 = 0L;
            try {
                String s = braf.readLine();
                long l8 = 0L;
                boolean flag = false;
                if (l2 - l > l5) {
                    flag = true;
                }
                for (; s != null; s = braf.readLine()) {
                    l6 ++;
                    l8 += s.length();
                    if (flag && l6 > 20L) {
                        long l9 = l8 / l6;
                        long l10 = (l2 - l) / l9;
                        boolean flag1 = false;
                        l6 = l10;
                        l7 = -1L;
                        break;
                    }
                    try {
                        int j = 0;
                        if (i != 0) {
                            j = TextUtils.toInt(TextUtils.readColumn(s, i));
                        } else {
                            int k = s.indexOf('"');
                            if (k != -1) {
                                int i1 = s.indexOf('"', k + 1);
                                if (i1 != -1) {
                                    int j1 = s.indexOf(' ', i1 + 2);
                                    if (j1 != -1) {
                                        j = TextUtils.readInteger(s, j1 + 1);
                                        if (j < 0) {
                                            j = 0;
                                        }
                                    }
                                }
                            }
                        }
                        l7 += j;
                    } catch (Exception exception) {
                        System.out.println("WebServerMonitor: " + exception);
                    }
                }

                braf.close();
            } catch (IOException ioexception) {
                System.err.println("Could not read log file");
            }
            al = (new long[] { l6, l7, l3, l4, 0L });
            return al;
        } catch (FileNotFoundException e) {
            return ERROR_RESULT;
        } catch (IOException e) {
            return ERROR_RESULT;
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pLastHits);
        array.add(pLastBytes);
        array.add(pLastHitsPerMinute);
        array.add(pLastBytesPerMinute);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pServerName) {
            return s;
        }
        if (stringproperty == pFileName) {
            pFileName.verify(s, hashmap, true, false);
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static {
        pServerName = new ScalarProperty("_serverName", true);
        pServerName.setDisplayText("Web Server", "the web server application to monitor");
        if (Platform.isWindows()) {
            pServerName.setParameterOptions(true, 1, false);
        }
        pFileName = new FileProperty("_fileName", "");
        String s = Platform.isUnix() ? "the full pathname of the web server log file to be monitored" : "log file pathname for servers which do not support performance registry";
        pFileName.fieldLabels("Log File Pathname", s);
        pFileName.setParameterOptions(true, 1, !Platform.isUnix());
        pBytesColumn = new StringProperty("_bytesColumn");
        pBytesColumn.setDisplayText("Request Size Column", "optional log file column for request size,  default uses common log format");
        pBytesColumn.setParameterOptions(true, 2, true);
        pLastHitsPerMinute = new RateProperty("lastHitsPerMinute", "0", "hits", "minutes");
        pLastHitsPerMinute.setLabel("hits/min");
        pLastHitsPerMinute.setStateOptions(1);
        pLastBytesPerMinute = new RateProperty("lastBytesPerMinute", "0", "bytes", "minutes");
        pLastBytesPerMinute.setLabel("bytes/min");
        pLastBytesPerMinute.setStateOptions(2);
        pLastHits = new NumericProperty("lastHits", "0", "hits");
        pLastHits.setLabel("hits");
        pLastBytes = new NumericProperty("lastBytes", "0", "bytes");
        pLastBytes.setLabel("bytes transferred");
        pLastRawHits = new NumericProperty("lastRawHits");
        pLastRawBytes = new NumericProperty("lastRawBytes");
        pLastMeasurement = new NumericProperty("lastMeasurement");
        pLastFilePosition = new NumericProperty("lastFilePosition", "0");
        StringProperty astringproperty[] = { pServerName, pFileName, pBytesColumn, pLastHits, pLastBytes, pLastHitsPerMinute, pLastBytesPerMinute, pLastRawHits, pLastRawBytes, pLastMeasurement, pLastFilePosition };
        addProperties("COM.dragonflow.StandardMonitor.WebServerMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.WebServerMonitor", Rule.stringToClassifier("lastHits == n/a\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.WebServerMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "description", "Monitors web server load.");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "help", "WebServerMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "title", "Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "class", "WebServerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "target", "_serverName");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "classType", "server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "topazName", "Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebServerMonitor", "topazType", "Web Server");
    }
}
