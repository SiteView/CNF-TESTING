/*
 * 
 * Created on 2005-2-15 10:39:56
 *
 * AlertLogReader.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>AlertLogReader</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Utils.Braf;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// LogReader, Action, SiteViewGroup, Platform

public class AlertLogReader extends LogReader {

    public AlertLogReader(File file) {
        logfile = null;
        logfile = file;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param array
     * @param hashmap1
     */
    public void addAlertEntry(HashMap hashmap, Array array, HashMap hashmap1) {
        if (hashmap1 != null) {
            Enumeration enumeration = hashmap1.keys();
            Object obj = null;
            String s1 = null;
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                if (!s.equals(s1)) {
                    String s2 = "";
                    s2 = TextUtils.getValue(hashmap, s);
                    if (s.equals("alert-type")) {
                        String s3 = TextUtils.getValue(hashmap, "alert-id");
                        s2 = TextUtils.readStringFromEnd(s3, ":");
                    } else if (s.equals("alert-id"))
                        s2 = TextUtils.readStringFromStart(s2, ":");
                    else if (s.equals("alert-monitor-id"))
                        s2 = TextUtils.readStringFromStart(s2, ":");
                    if (hashmap1.countValues(s2) > 0)
                        array.add(hashmap);
                    s1 = s;
                }
            }
        }
    }

    public void addAlertTag(HashMap hashmap, String s, String s1) {
        hashmap.put(s, s1);
        if (!Action.isBaseEntry(s)) {
            String s2 = TextUtils.getValue(hashmap, "extraCount");
            if (s2.length() == 0)
                s2 = "0";
            s2 = TextUtils.increment(s2);
            hashmap.put("extra" + s2, s);
            hashmap.put("extraCount", s2);
        }
    }

    public Array process(HashMap hashmap, Date date, Date date1,
            HashMap hashmap1) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("_alertLogReadChunkSize",
                0x19000);
        Array array = new Array();
        if (logfile == null || !logfile.exists()) {
            LogManager.log("Error", "Alert log " + logfile.getAbsolutePath()
                    + " does not exist");
            return array;
        }
        if (!logfile.canRead()) {
            LogManager.log("Error", "Alert log " + logfile.getAbsolutePath()
                    + " cannot be read");
            return array;
        }
        boolean flag = false;
        String s = logfile.getAbsolutePath();
        long l1 = Platform.FILE_NEWLINE.length();
        long l2 = logfile.length();
        boolean flag1 = false;
        if (hashmap1 != null)
            flag1 = !hashmap1.isEmpty();
        long l3 = 0L;
        long l4 = 0x7fffffffffffffffL;
        if (date != null)
            l3 = date.getTime();
        if (date1 != null)
            l4 = date1.getTime();
        long l5 = 0x7fffffffffffffffL;
        long l6 = 0L;
        Braf braf = null;
        try {
            do {
                if (flag)
                    break;
                long l7 = l2 - l;
                long l8 = l2;
                l2 = -1L;
                if (l7 < 0L)
                    l7 = 0L;
                long l9 = l7;
                braf = new Braf(s, l7);
                HashMap hashmap2 = null;
                Array array1 = new Array();
                String s2 = "";
                StringBuffer stringbuffer = new StringBuffer();
                label0: do {
                    String s1;
                    do
                        if ((s1 = braf.readLine()) == null)
                            break label0; while (s1.indexOf("<pre>") >= 0);
                    if (isDateLine(s1) && getDateInfo(s1, lineDate)) {
                        if (l2 < 0L)
                            l2 = l9 - 1L;
                        if (hashmap2 != null) {
                            if (s2.length() > 0) {
                                addAlertTag(hashmap2, s2, stringbuffer
                                        .toString());
                                s2 = "";
                                stringbuffer.setLength(0);
                            }
                            addAlertEntry(hashmap2, array1, hashmap);
                        }
                        Date date2 = getDate(lineDate);
                        long l10 = date2.getTime();
                        hashmap2 = null;
                        if (l10 >= l3 && l10 <= l4)
                            if (flag1) {
                                if (ScheduleProperty.isEnabled(hashmap1, date2)) {
                                    hashmap2 = new HashMap();
                                    hashmap2.put("date", date2);
                                }
                            } else {
                                hashmap2 = new HashMap();
                                hashmap2.put("date", date2);
                            }
                        if (l10 < l5)
                            l5 = l10;
                        if (l10 > l6)
                            l6 = l10;
                    } else if (hashmap2 != null)
                        if (s1.startsWith(ALERT_LINE_PREFIX)) {
                            if (s2.length() > 0) {
                                addAlertTag(hashmap2, s2, stringbuffer
                                        .toString());
                                s2 = "";
                                stringbuffer.setLength(0);
                            }
                            int i = s1.indexOf(COLON_SUFFIX);
                            if (i >= 0) {
                                s2 = s1.substring(1, i);
                                stringbuffer.setLength(0);
                                stringbuffer.append(s1.substring(i
                                        + COLON_SUFFIX.length()));
                            }
                        } else if (s2.length() > 0) {
                            if (stringbuffer.length() > 0)
                                stringbuffer.append("\n");
                            stringbuffer.append(s1);
                        }
                    l9 += (long) s1.length() + l1;
                } while (l9 < l8);
                if (hashmap2 != null) {
                    if (s2.length() > 0)
                        addAlertTag(hashmap2, s2, stringbuffer.toString());
                    addAlertEntry(hashmap2, array1, hashmap);
                }
                for (int j = 0; j < array.size(); j++)
                    array1.add(array.at(j));

                array = array1;
                if (l7 == 0L)
                    flag = true;
                braf.close();
                braf = null;
                if (l5 <= l3 && l6 >= l4)
                    flag = true;
            } while (true);
        } catch (IOException ioexception) {
            LogManager.log("Error", "I/O error reading file: " + s);
            System.out.println("I/O error reading file: " + s);
        } finally {
            if (braf != null)
                try {
                    braf.close();
                } catch (IOException ioexception1) {
                }
        }
        return array;
    }

    public static void main(String args[]) {
        File file = new File(Platform.getRoot() + "/logs/alert.log");
        AlertLogReader alertlogreader = new AlertLogReader(file);
        HashMap hashmap = new HashMap();
        hashmap.put("alert-type", "Email");
        Array array = alertlogreader.process(hashmap, null, null, null);
        System.out
                .println("***************************************************");
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap1 = (HashMap) array.at(i);
            System.out.println(TextUtils.dateToString((Date) hashmap1
                    .get("date"))
                    + "   " + hashmap1.get("alert-message"));
        }

    }

    File logfile;

    static String ALERT_LINE_PREFIX = " alert-";

    static String COLON_SUFFIX = ": ";

}