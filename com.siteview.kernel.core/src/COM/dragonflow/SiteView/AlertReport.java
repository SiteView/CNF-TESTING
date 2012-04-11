/*
 * 
 * Created on 2005-2-15 10:43:09
 *
 * AlertReport.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>AlertReport</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import jgl.Sorting;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AlertLogReader, Action, CompareSlot, Platform,
// SiteViewGroup

public class AlertReport {

    public AlertReport() {
    }

    public static Array readAlertData(HashMap hashmap, Date date, Date date1,
            String s, HashMap hashmap1) {
        AlertLogReader alertlogreader = new AlertLogReader(new File(Platform
                .getDirectoryPath("logs", s)
                + File.separator + "alert.log"));
        Array array = alertlogreader.process(hashmap, date, date1, hashmap1);
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        if (siteviewgroup.getSetting("_includeAlertLogOld").length() > 0) {
            AlertLogReader alertlogreader1 = new AlertLogReader(new File(
                    Platform.getDirectoryPath("logs", s) + File.separator
                            + "alert.log.old"));
            Array array1 = alertlogreader1.process(hashmap, date, date1,
                    hashmap1);
            for (int i = 0; i < array.size(); i++)
                array1.add(array.at(i));

            return array1;
        } else {
            return array;
        }
    }

    public static void generateReport(PrintWriter printwriter, HashMap hashmap,
            String s, boolean flag, Date date, Date date1, int i, String s1) {
        String s2 = "";
        String s3 = "";
        String s4 = "";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s5 = siteviewgroup.getSetting("_reportTableHTML");
        if (s5.length() > 0)
            s4 = s5;
        s5 = siteviewgroup.getSetting("_reportTableHeaderHTML");
        if (s5.length() > 0)
            s2 = s5;
        s5 = siteviewgroup.getSetting("_reportTableDataHTML");
        if (s5.length() > 0)
            s3 = s5;
        Array array = readAlertData(hashmap, date, date1, s1, null);
        generateReport(array, printwriter, s, flag, date, date1, i, s4, s2, s3);
    }

    public static void generateXMLReport(Array array, PrintWriter printwriter,
            Date date, Date date1, int i) {
        printwriter.println("<alerts>");
        if (date != null && date1 != null) {
            Date date2 = date;
            Date date3 = date1;
            if (i != 0) {
                date2 = new Date(date.getTime() + (long) (i * 1000));
                date3 = new Date(date1.getTime() + (long) (i * 1000));
            }
            printwriter.println(TextUtils.escapeXML("startdate", TextUtils
                    .prettyDate(date2)));
            printwriter.println(TextUtils.escapeXML("enddate", TextUtils
                    .prettyDate(date3)));
            printwriter.println(TextUtils.escapeXML("timeoffset", Platform
                    .timeZoneName(i)));
        }
        for (int j = 0; j < array.size(); j++) {
            HashMap hashmap = (HashMap) array.at(j);
            Enumeration enumeration = hashmap.keys();
            printwriter.print("<alert>");
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                if (!s.startsWith("extra"))
                    if (s.equals("date")) {
                        Date date4 = (Date) hashmap.get(s);
                        if (i != 0)
                            date4 = new Date(date4.getTime()
                                    + (long) (i * 1000));
                        printwriter.println(TextUtils.escapeXML("date",
                                TextUtils.prettyDate(date4)));
                    } else {
                        printwriter.println(TextUtils.escapeXML(s, TextUtils
                                .getValue(hashmap, s)));
                    }
            } 
            printwriter.println("</alert>");
        }

        printwriter.println("</alerts>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @param printwriter
     * @param s
     * @param flag
     * @param date
     * @param date1
     * @param i
     * @param s1
     * @param s2
     * @param s3
     */
    public static void generateReport(Array array, PrintWriter printwriter,
            String s, boolean flag, Date date, Date date1, int i, String s1,
            String s2, String s3) {
        Array array1 = new Array();
        array1.add("date");
        array1.add("alert-type");
        array1.add("alert-message");
        array1.add("alert-monitor");
        String s4 = "Alerts";
        if (date != null && date1 != null) {
            Date date2 = date;
            Date date3 = date1;
            if (i != 0) {
                date2 = new Date(date.getTime() + (long) (i * 1000));
                date3 = new Date(date1.getTime() + (long) (i * 1000));
            }
            String s5 = TextUtils.prettyDate(date2) + " to "
                    + TextUtils.prettyDate(date3);
            if (i != 0)
                s5 = s5 + ", " + Platform.timeZoneName(i);
            s4 = s4 + " from " + s5;
        }
        printwriter
                .print("<A name=alertReport></A><P>\n<TABLE WIDTH=\"100%\" "
                        + s1
                        + ">"
                        + "<CAPTION><B>"
                        + s4
                        + "</B></CAPTION>"
                        + "<TR "
                        + s2
                        + "><TH>Time</TH><TH>Type</TH><TH>Message</TH><TH>Monitor</TH>");
        if (flag) {
            printwriter.print("<TH>Group</TH>");
            array1.add("alert-group");
        }
        printwriter.println("</TR>");
        if (array.size() == 0) {
            printwriter.println("<TR " + s3 + "><TD COLSPAN=" + array1.size()
                    + " ALIGN=CENTER>No alerts found</TD></TR>");
        } else {
            for (int j = 0; j < array.size(); j++) {
                HashMap hashmap = (HashMap) array.at(j);
                Enumeration enumeration = array1.elements();
                printwriter.print("<TR " + s3 + ">");
                boolean flag1 = TextUtils.getValue(hashmap, "alert-failed")
                        .length() > 0;
                boolean flag2 = TextUtils.getValue(hashmap, "alert-test")
                        .length() > 0;
                while (enumeration.hasMoreElements()) {
                    String s6 = (String) enumeration.nextElement();
                    printwriter.print("<TD>");
                    if (s6.equals("date")) {
                        Date date4 = (Date) hashmap.get(s6);
                        if (i != 0)
                            date4 = new Date(date4.getTime()
                                    + (long) (i * 1000));
                        printwriter.print(TextUtils.prettyDate(date4));
                    } else {
                        String s7 = TextUtils.getValue(hashmap, s6);
                        if (s6.equals("alert-type")) {
                            if (flag2)
                                s7 = "Test " + s7;
                            if (flag1)
                                s7 = "<B>" + s7 + "</B>";
                        }
                        printwriter.print(s7);
                    }
                }
                boolean flag3 = s.equals("detail") || s.equals("detailonfail")
                        && flag1;
                if (!flag3)
                    continue;
                printwriter.println("<TR><TD></TD><TD COLSPAN="
                        + (array1.size() - 1) + ">");
                String s8 = "1";
                for (String s9 = (String) hashmap.get("extra" + s8); s9 != null; s9 = (String) hashmap
                        .get("extra" + s8)) {
                    String s10 = TextUtils.getValue(hashmap, s9);
                    printwriter.println("<PRE><B>" + s9 + "</B>: "
                            + TextUtils.escapeHTML(s10) + "</PRE>");
                    s8 = TextUtils.increment(s8);
                }

                printwriter.println("</TR>");
            }

        }
        printwriter.println("</TABLE>");
    }

    public static Action getActionOfClass(String s) {
        Action action = (Action) actionCache.get(s);
        if (action == null) {
            action = Action.createActionObject(s);
            if (action != null)
                actionCache.add(s, action);
        }
        return action;
    }

    public static String[] createSummaryMessage(Array array, long l) {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        HashMap hashmap = new HashMap();
        Array array1 = new Array();
        String s = "Alert Type";
        int i = s.length();
        if (array.size() == 0) {
            stringbuffer.append("No alerts were triggered");
            stringbuffer1.append("No alerts were triggered");
        } else {
            for (int j = 0; j < array.size(); j++) {
                HashMap hashmap1 = (HashMap) array.at(j);
                String s3 = TextUtils
                        .prettyDate((Date) hashmap1.get("date"), l);
                TextUtils.appendStringRightJustify(stringbuffer1, TextUtils
                        .prettyDateTime(s3), 8);
                TextUtils.appendStringRightJustify(stringbuffer1, TextUtils
                        .prettyDateDate(s3), 11);
                String s4 = TextUtils.readStringFromEnd(TextUtils.getValue(
                        hashmap1, "alert-id"), ":");
                if (s4.length() > 0) {
                    Action action = getActionOfClass(s4);
                    if (action != null) {
                        stringbuffer1.append("  ");
                        stringbuffer1.append(action
                                .alertLogEntrySummary(hashmap1));
                        HashMap hashmap3 = (HashMap) hashmap.get(s4);
                        if (hashmap3 == null) {
                            hashmap3 = new HashMap();
                            hashmap3.put("count", "0");
                            hashmap3.put("failed", "0");
                            String s7 = action.getClassPropertyString("title");
                            hashmap3.put("title", s7);
                            hashmap.put(s4, hashmap3);
                            array1.add(hashmap3);
                            if (s7.length() > i)
                                i = s7.length();
                        }
                        TextUtils.incrementEntry(hashmap3, "count");
                        if (TextUtils.getValue(hashmap1, "alert-failed")
                                .length() > 0)
                            TextUtils.incrementEntry(hashmap3, "failed");
                    }
                }
                stringbuffer1.append("\n");
            }

            String s1 = "# triggered";
            String s2 = "# failed";
            TextUtils.appendStringLeftJustify(stringbuffer, s, i);
            stringbuffer.append("  ");
            stringbuffer.append(s1);
            stringbuffer.append("  ");
            stringbuffer.append(s2);
            stringbuffer.append("\n");
            stringbuffer.append(TextUtils.filledString('-', i));
            stringbuffer.append("  ");
            stringbuffer.append(TextUtils.filledString('-', s1.length()));
            stringbuffer.append("  ");
            stringbuffer.append(TextUtils.filledString('-', s2.length()));
            stringbuffer.append("\n");
            Sorting
                    .sort(array1, new CompareSlot("count",
                            CompareSlot.DIRECTION_GREATER,
                            CompareSlot.NUMERIC_COMPARE));
            for (Enumeration enumeration = array1.elements(); enumeration
                    .hasMoreElements(); stringbuffer.append("\n")) {
                HashMap hashmap2 = (HashMap) enumeration.nextElement();
                String s5 = TextUtils.getValue(hashmap2, "title");
                String s6 = TextUtils.getValue(hashmap2, "count");
                String s8 = TextUtils.getValue(hashmap2, "failed");
                TextUtils.appendStringLeftJustify(stringbuffer, s5, i);
                stringbuffer.append("  ");
                TextUtils.appendStringRightJustify(stringbuffer, s6, s1
                        .length());
                stringbuffer.append("  ");
                TextUtils.appendStringRightJustify(stringbuffer, s8, s2
                        .length());
            }

            stringbuffer.append("\n");
        }
        String as[] = new String[2];
        as[0] = stringbuffer.toString();
        as[1] = stringbuffer1.toString();
        return as;
    }

    public static HashMap actionCache = new HashMap();

}
