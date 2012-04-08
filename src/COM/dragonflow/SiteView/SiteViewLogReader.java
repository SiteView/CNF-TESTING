/*
 * 
 * Created on 2005-2-16 16:52:45
 *
 * SiteViewLogReader.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SiteViewLogReader</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.BufferedRandomAccessFile;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// LogReader, SampleCollector, PortalSiteView, Monitor,
// Portal, SiteViewObject, Platform, SiteViewGroup,
// MasterConfig

public class SiteViewLogReader extends LogReader {

    String basePath;

    String suffix;

    final boolean debug = false;

    final boolean readLineOnly = false;

    final boolean readDateOnly = false;

    final boolean splitOnly = false;

    File logfile;

    static int MAXIMUMStatusLength = 256;

    static final int TIME_INDEX = 0;

    static final int CATEGORY_INDEX = 1;

    static final int GROUP_INDEX = 2;

    static final int NAME_INDEX = 3;

    static final int STATUS_INDEX = 4;

    static final int ID_INDEX = 5;

    public SiteViewLogReader() {
        basePath = "";
        suffix = "";
    }

    public SiteViewLogReader(PrintWriter printwriter) {
        basePath = "";
        suffix = "";
        progressStream = printwriter;
    }

    public SiteViewLogReader(File file, PrintWriter printwriter) {
        basePath = "";
        suffix = "";
        progressStream = printwriter;
        logfile = file;
        String s = file.getAbsolutePath();
        basePath = s;
        int i = s.lastIndexOf(".");
        if (i >= 0) {
            suffix = s.substring(i);
            basePath = s.substring(0, i);
        }
    }

    public SiteViewLogReader(File file) {
        this(file, null);
    }

    public String nextPath(String s, Date date, Date date1) {
        String s1 = s.substring(basePath.length());
        if (s1.equals(suffix)) {
            long l = date.getTime();
            s = basePath + TextUtils.dayToFileName(date) + suffix;
            File file;
            for (file = new File(s); !file.exists() && l <= date1.getTime(); file = new File(
                    s)) {
                GregorianCalendar gregoriancalendar1 = new GregorianCalendar();
                Date date3 = new Date(l);
                gregoriancalendar1.setTime(date3);
                gregoriancalendar1.add(5, 1);
                date3 = gregoriancalendar1.getTime();
                l = date3.getTime();
                s = basePath + TextUtils.dayToFileName(date3) + suffix;
            }

            if (!file.exists()) {
                s = "";
            }
        } else {
            Date date2 = TextUtils.fileNameToDay(s1);
            GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTime(date2);
            gregoriancalendar.add(5, 1);
            date2 = gregoriancalendar.getTime();
            if (date2.after(date1)) {
                return "";
            }
            s = basePath + TextUtils.dayToFileName(date2) + suffix;
        }
        return s;
    }

    public void processPortal(Array array, Date date, Date date1, int i,
            Array array1, Array array2, Array array3, long l, long l1, long l2,
            String s, HashMap hashmap, boolean flag) {
        HashMap hashmap1 = new HashMap();
        for (int j = 0; j < array.size(); j++) {
            SampleCollector samplecollector = (SampleCollector) array.at(j);
            if (hashmap1.get(samplecollector.getIDString()) != null) {
                continue;
            }
            Array array4 = new Array();
            String s1 = samplecollector.getIDString();
            PortalSiteView portalsiteview = (PortalSiteView) Portal
                    .getSiteViewForID(s1);
            if (portalsiteview == null) {
                continue;
            }
            String s2 = Portal.getServerID(s1);
            String s3 = Portal.getGroupID(s1);
            HashMap hashmap2 = portalsiteview.getMasterConfig();
            boolean flag1 = TextUtils.getValue(hashmap2, "_logInGroup")
                    .length() > 0;
            array4.add(samplecollector);
            hashmap1.put(samplecollector.getIDString(), samplecollector);
            for (int k = j + 1; k < array.size(); k++) {
                SampleCollector samplecollector1 = (SampleCollector) array
                        .at(k);
                if (s2.equals(Portal
                        .getServerID(samplecollector1.getIDString()))
                        && (!flag1 || s3.equals(Portal
                                .getGroupID(samplecollector1.getIDString())))) {
                    array4.add(samplecollector1);
                    hashmap1.put(samplecollector1.getIDString(),
                            samplecollector1);
                }
            }

            long l3 = 0L;
            String s4 = Portal.getPortalSiteViewRootPath(s2) + File.separator
                    + "logs" + File.separator + "SiteView.log";
            if (flag1) {
                s4 = Portal.getPortalSiteViewRootPath(s2) + File.separator
                        + "accounts" + File.separator + s3 + File.separator
                        + "logs" + File.separator + "SiteView.log";
            }
            File file = new File(s4);
            Portal.getPortal();
            PortalSiteView portalsiteview1 = (PortalSiteView) Portal
                    .getSiteViewForID(s1);
            if (portalsiteview1 != null) {
                l3 = portalsiteview1
                        .getPropertyAsLong(PortalSiteView.pTimezoneOffsetFromPortal);
                l3 *= 0x36ee80L;
            }
            logfile = file;
            String s5 = file.getAbsolutePath();
            basePath = s5;
            int i1 = s5.indexOf(".log");
            if (i1 >= 0) {
                suffix = s5.substring(i1);
                basePath = s5.substring(0, i1);
            }
            process(array4, date, date1, l3, i, array1, array2, array3, l, l1,
                    l2, hashmap, flag);
        }

    }

    public void process(Array array, Date date, Date date1, int i, int j) {
        process(array, date, date1, i, j, false);
    }

    public void process(Array array, Date date, Date date1, int i, int j,
            boolean flag) {
        process(array, date, date1, i, j, null, null, null, 0L, 0L, 0L, null,
                flag);
    }

    public void process(Array array, Date date, Date date1, int i,
            Array array1, Array array2, Array array3, long l, long l1, long l2,
            HashMap hashmap, boolean flag) {
        process(array, date, date1, 0L, i, array1, array2, array3, l, l1, l2,
                hashmap, flag);
    }

    public void process(Array array, Date date, Date date1, long l, int i,
            Array array1, Array array2, Array array3, long l1, long l2,
            long l3, HashMap hashmap) {
        process(array, date, date1, l, i, array1, array2, array3, l1, l2, l3,
                hashmap, false);
    }

    public void process(Array array, Date date, Date date1, long l, int i,
            Array array1, Array array2, Array array3, long l1, long l2,
            long l3, HashMap hashmap, boolean flag) {
        HashMap hashmap1 = new HashMap(true);
        SampleCollector samplecollector;
        String s;
        for (Enumeration enumeration = array.elements(); enumeration
                .hasMoreElements(); hashmap1.add(s, samplecollector)) {
            samplecollector = (SampleCollector) enumeration.nextElement();
            samplecollector.createBuckets(date.getTime() / 1000L, date1
                    .getTime() / 1000L, i);
            Monitor monitor = samplecollector.getMonitor();
            s = monitor.getProperty(SiteViewObject.pOwnerID)
                    + SiteViewObject.ID_SEPARATOR
                    + monitor.getProperty(SiteViewObject.pID);
        }

        int j = 0;
        Date date2 = new Date(date.getTime() + l);
        Date date3 = new Date(date1.getTime() + l);
        long l4 = Platform.timeMillis();
        int k = 0;
        BufferedRandomAccessFile bufferedrandomaccessfile = null;
        String s1 = basePath + suffix;
        File file = null;
        File file1 = new File(basePath + suffix);
        if (file1.exists()) {
            long l5 = file1.lastModified();
            if (l5 >= date2.getTime()) {
                file = file1;
            }
        }
        if (file == null) {
            s1 = basePath + TextUtils.dayToFileName(date2) + suffix;
            file = new File(s1);
            do {
                if (file.exists()) {
                    break;
                }
                s1 = nextPath(s1, date2, date3);
                if (s1.length() == 0) {
                    break;
                }
                file = new File(s1);
            } while (true);
        }
        if (!file.exists()) {
            LogManager.log("Error", "Could not open log file " + file
                    + " to read data from " + date2 + " to " + date3);
            return;
        }
        try {
            bufferedrandomaccessfile = new BufferedRandomAccessFile(file, "r");
            String s2 = bufferedrandomaccessfile.getNextLine();
            Date date4 = getDate(s2);
            if (date4 != null) {
                int i1 = 0x1b7740;
                Date date5 = new Date(date2.getTime() - (long) i1);
                if (date2.getTime() > date4.getTime() + (long) i1) {
                    long l8 = 0L;
                    long l9 = bufferedrandomaccessfile.length();
                    long l11 = (l8 + l9) / 2L;
                    Date date6 = new Date(0L);
                    Object obj = null;
                    bufferedrandomaccessfile.seek(l11);
                    do {
                        String s3 = bufferedrandomaccessfile.getNextLine();
                        long l12 = bufferedrandomaccessfile.getFilePointer();
                        s3 = bufferedrandomaccessfile.getNextLine();
                        if (s3 == null) {
                            break;
                        }
                        Date date7 = date6;
                        date6 = getDate(s3);
                        if (date6 == null) {
                            break;
                        }
                        if (date6.after(date5) && date6.before(date2)
                                || date6.equals(date7)) {
                            j = (int) l12;
                            break;
                        }
                        if (date6.before(date2)) {
                            l8 = l12;
                            l12 = (l12 + l9) / 2L;
                        } else {
                            l9 = l12;
                            l12 = (l12 + l8) / 2L;
                        }
                        bufferedrandomaccessfile.seek(l12);
                        k++;
                    } while (true);
                }
            }
        } catch (IOException ioexception) {
        } finally {
            if (bufferedrandomaccessfile != null) {
                try {
                    bufferedrandomaccessfile.close();
                } catch (IOException ioexception2) {
                }
            }
        }
        long l6 = date2.getTime() / 1000L;
        long l7 = date3.getTime() / 1000L;
        BufferedRandomAccessFile bufferedrandomaccessfile1 = null;
        try {
            bufferedrandomaccessfile1 = new BufferedRandomAccessFile(s1, "r");
            bufferedrandomaccessfile1.seek(j);
            int j1 = 0;
            long l10 = 0L;
            long l13 = 0L;
            long l15 = 0L;
            int k1 = 0;
            long l17 = Platform.timeMillis();
            long l18 = 0L;
            int i2 = 0;
            int j2 = 0;
            int k2 = 0;
            boolean flag1 = hashmap != null && !hashmap.isEmpty();
            String as[] = new String[200];
            do {
                String s4 = bufferedrandomaccessfile1.getNextLine();
                if (s4 == null) {
                    try {
                        bufferedrandomaccessfile1.close();
                        bufferedrandomaccessfile1 = null;
                    } catch (IOException ioexception3) {
                    }
                    do {
                        s1 = nextPath(s1, date2, date3);
                        if (s1.length() == 0) {
                            break;
                        }
                        file = new File(s1);
                    } while (!file.exists());
                    if (s1.length() == 0 || !file.exists()) {
                        break;
                    }
                    bufferedrandomaccessfile1 = new BufferedRandomAccessFile(
                            s1, "r");
                    s4 = bufferedrandomaccessfile1.getNextLine();
                    if (s4 == null) {
                        break;
                    }
                }
                i2++;
                if (getDateInfo(s4, lineDate)) {
                    if (lineDate[DATE] != j1) {
                        Date date8 = new Date(lineDate[YEAR], lineDate[MONTH],
                                lineDate[DATE]);
                        k1 = date8.getDay();
                        l10 = date8.getTime() / 1000L;
                        j1 = lineDate[DATE];
                    }
                    long l16 = lineDate[HOUR] * 3600 + lineDate[MINUTE] * 60
                            + lineDate[SECOND];
                    long l14 = l10 + l16;
                    if (l14 < l6) {
                        continue;
                    }
                    if (l14 > l7) {
                        break;
                    }
                    if (l18 == 0L) {
                        l18 = Platform.timeMillis();
                    }
                    int i3 = TextUtils.splitChar(s4, '\t', as);
                    int j3 = as[4].length() <= MAXIMUMStatusLength ? as[4]
                            .length() : MAXIMUMStatusLength;
                    if (i3 > 5) {
                        k2++;
                        String s5 = as[5];
                        int k3 = s5.indexOf(":");
                        if (k3 != -1) {
                            s5 = s5.substring(0, k3);
                        }
                        String s6 = as[2] + SiteViewObject.ID_SEPARATOR + s5;
                        int i4 = 0;
                        l14 -= l / 1000L;
                        if (hashmap1.get(s6) != null) {
                            String s7 = as[1];
                            if (flag1
                                    && !ScheduleProperty.isEnabled(hashmap, k1,
                                            (int) l16)) {
                                s7 = Monitor.FILTERED_CATEGORY;
                            }
                            for (Enumeration enumeration1 = hashmap1.values(s6); enumeration1
                                    .hasMoreElements();) {
                                SampleCollector samplecollector1 = (SampleCollector) enumeration1
                                        .nextElement();
                                if (s7.equals(Monitor.ERROR_CATEGORY)) {
                                    String s8 = "";
                                    if (i3 > 1
                                            && (as[i3 - 1]
                                                    .equals(Monitor.FAILURE) || as[i3 - 1]
                                                    .equals(Monitor.NON_FAILURE))) {
                                        s8 = as[i3 - 1];
                                    }
                                    samplecollector1.add(l14, as, i3, s7, s8,
                                            flag);
                                } else {
                                    samplecollector1.add(l14, as, i3, s7, "",
                                            flag);
                                }
                                i4 = 1;
                            }

                            if (!s7.equals(Monitor.FILTERED_CATEGORY)) {
                                if (array1 != null && (long) array1.size() < l1
                                        && as[1].equals("error")) {
                                    array1.add("" + l14 + '\t' + as[3] + '\t'
                                            + as[4].substring(0, j3));
                                }
                                if (array2 != null && (long) array2.size() < l2
                                        && as[1].equals("warning")) {
                                    array2.add("" + l14 + '\t' + as[3] + '\t'
                                            + as[4].substring(0, j3));
                                }
                                if (array3 != null && (long) array3.size() < l3
                                        && as[1].equals("good")) {
                                    array3.add("" + l14 + '\t' + as[3] + '\t'
                                            + as[4].substring(0, j3));
                                }
                            }
                        }
                        j2 += i4;
                    } else {
                        LogManager.log("Error", s1 + " BAD LINE: " + s4);
                    }
                } else if (s4.indexOf("<pre>") < 0) {
                    LogManager.log("Error", s1 + " BAD DATE LINE: " + s4);
                }
            } while (true);
        } catch (FileNotFoundException filenotfoundexception) {
            LogManager.log("Error", "Could not open log file: "
                    + file.getAbsolutePath());
            System.out.println("Could not open log file: "
                    + file.getAbsolutePath());
        } catch (IOException ioexception1) {
            LogManager.log("Error", "I/O error reading file: "
                    + file.getAbsolutePath());
            System.out.println("I/O error reading file: "
                    + file.getAbsolutePath());
        } finally {
            if (bufferedrandomaccessfile1 != null) {
                try {
                    bufferedrandomaccessfile1.close();
                } catch (IOException ioexception4) {
                }
            }
        }
    }

    public static void printLog(String s, PrintWriter printwriter,
            HTTPRequest httprequest) {
        File file = new File(s);
        SiteViewLogReader siteviewlogreader = new SiteViewLogReader(file,
                null);
        siteviewlogreader.printLogPage(s, printwriter, httprequest);
    }

    void printLogPage(String s, PrintWriter printwriter, HTTPRequest httprequest) {
        try {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            BufferedReader bufferedreader = FileUtils
                    .MakeInputReader(new FileInputStream(logfile));
            String as[] = new String[100];
            do {
                String s1 = bufferedreader.readLine();
                if (s1 == null) {
                    break;
                }
                int i = TextUtils.splitChar(s1, '\t', as);
                if (getDateInfo(s1, lineDate) && i > 5) {
                    String s2 = as[5];
                    int j = s2.indexOf(":");
                    if (j != -1) {
                        s2 = s2.substring(0, j);
                    }
                    String s3 = as[2] + SiteViewObject.ID_SEPARATOR + s2;
                    Monitor monitor = (Monitor) siteviewgroup.getElement(s3);
                    if (monitor != null) {
                        Array array = monitor.getLogProperties();
                        if (array != null) {
                            printwriter.println("<sample>");
                            int k = 1;
                            for (Enumeration enumeration = array.elements(); enumeration
                                    .hasMoreElements();) {
                                StringProperty stringproperty = (StringProperty) enumeration
                                        .nextElement();
                                printwriter.print(TextUtils.escapeXML(
                                        stringproperty.getName(), as[k]));
                                k++;
                            }

                            printwriter.println("</sample>");
                        }
                    }
                }
            } while (true);
        } catch (Exception exception) {
            printwriter.println("error reading log, log: " + s + ", error: "
                    + exception);
        }
    }

    public static void main(String args[]) {
        String s = args[0];
        String s1 = args[1];
        String s2 = args[2];
        String s3 = "\t";
        if (args.length >= 4) {
            s3 = args[3];
        }
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Monitor monitor = (Monitor) siteviewgroup.getElement(s + "/" + s1);
        if (monitor == null) {
            System.out.println("Monitor doesn't exist");
        }
        Date date = Platform.makeDate();
        long l = siteviewgroup.getSettingAsLong("_dailyLogKeepDays", 40);
        Date date1 = new Date(date.getTime() - l * (long) TextUtils.DAY_SECONDS
                * 1000L);
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        BufferedRandomAccessFile bufferedrandomaccessfile = null;
        if (!s2.endsWith(File.separator)) {
            s2 = s2 + File.separator;
        }
        s2 = s2 + "SiteView.log";
        File file = new File(s2);
        SiteViewLogReader siteviewlogreader = new SiteViewLogReader(file);
        try {
            bufferedrandomaccessfile = new BufferedRandomAccessFile(s2, "r");
            System.err.println("Reading file " + s2);
            String args1[] = new String[100];
            do {
                String s4 = bufferedrandomaccessfile.getNextLine();
                if (s4 == null) {
                    try {
                        bufferedrandomaccessfile.close();
                        bufferedrandomaccessfile = null;
                    } catch (IOException ioexception1) {
                    }
                    do {
                        s2 = siteviewlogreader.nextPath(s2, date1, date);
                        if (s2.length() == 0) {
                            break;
                        }
                        file = new File(s2);
                    } while (!file.exists());
                    if (s2.length() == 0 || !file.exists()) {
                        break;
                    }
                    bufferedrandomaccessfile = new BufferedRandomAccessFile(s2,
                            "r");
                    System.err.println("Reading file " + s2);
                    s4 = bufferedrandomaccessfile.getNextLine();
                    if (s4 == null) {
                        break;
                    }
                }
                int i = TextUtils.splitChar(s4, '\t', args1);
                if (i > 5) {
                    String s5 = args1[5];
                    int j = s5.indexOf(":");
                    if (j != -1) {
                        s5 = s5.substring(0, j);
                    }
                    if (args1[2].equals(s) && s5.equals(s1)) {
                        System.out.println(s4);
                    }
                }
            } while (true);
        } catch (FileNotFoundException filenotfoundexception) {
            System.err.println("Could not open log file: " + s2);
        } catch (IOException ioexception) {
            System.err.println("I/O error reading file: " + s2);
        } finally {
            if (bufferedrandomaccessfile != null) {
                try {
                    bufferedrandomaccessfile.close();
                } catch (IOException ioexception2) {
                }
            }
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    static {
        HashMap hashmap = MasterConfig.getMasterConfig();
        Enumeration enumeration = hashmap.values("_maxStatusLength");
        String s = "";
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            if (s1 == null || s1.length() <= 1) {
                continue;
            }
            MAXIMUMStatusLength = TextUtils.toInt(s1);
            break;
        }
    }
}
