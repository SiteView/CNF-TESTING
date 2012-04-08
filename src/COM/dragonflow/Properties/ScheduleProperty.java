/*
 * 
 * Created on 2005-2-28 7:06:38
 *
 * ScheduleProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>ScheduleProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Properties:
// ScalarProperty, HashMapOrdered

public class ScheduleProperty extends ScalarProperty {

    public static String absolute_prefix = "*";

    public static char absolute_trigger_separator = '/';

    public static String absolute_trigger_separator_str = "/";

    public static String schedule_day_separator = ",";

    public static String permitted = "0123456789:";

    private boolean printRange;

    private boolean printAbsolute;

    public static final String dayStrings[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    public static final String SCHEDULE_ERROR_MESSAGE = "Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59";

    public static String NO_START_STRING = "0-0";

    public static String NO_END_STRING;

    public ScheduleProperty() {
        super("fakename", "fakevalue");
        printRange = true;
        printAbsolute = true;
    }

    public ScheduleProperty(String s, String s1) {
        super(s, s1);
        printRange = true;
        printAbsolute = true;
    }

    public boolean displayValue(String s, String s1, SiteViewObject siteviewobject) {
        return true;
    }

    public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject,
            HTTPRequest httprequest, HashMap hashmap, boolean flag, boolean flag1, boolean flag2) {
        printRange = flag1;
        printAbsolute = flag2;
        String s = siteviewobject.getProperty(this);
        if (httprequest.getValue("customSchedule").length() == 0 && (s.length() == 0 || s.indexOf("_id") != -1)) {
            super.printProperty(cgi, printwriter, siteviewobject, httprequest, hashmap, flag);
            printwriter.println("<TR><TD></TD><TD ALIGN=LEFT><a href=" + cgi.getPageLink("schedulePrefs", "")
                    + ">Edit</A> Schedules</TD></TR>");
            return;
        }
        Object obj = hashmap.get(this);
        String s1 = "";
        if (obj != null) {
            s1 = (String) obj;
        }
        printwriter.print("<TR><TD ALIGN=RIGHT>" + getLabel() + "</TD><TD><TABLE><TR><TD>");
        printScheduleTable(printwriter, siteviewobject.getProperty(this), s1);
        printwriter.print("</TD></TR><TR><TD COLSPAN=4><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>"
                + "</TD></TR></TABLE></TD><TD><I>" + s1 + "</I></TD></TR>");
    }

    public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject,
            HTTPRequest httprequest, HashMap hashmap, boolean flag) {
        printProperty(cgi, printwriter, siteviewobject, httprequest, hashmap, flag, true, true);
    }

    public static void printScheduleTable(PrintWriter printwriter, String s, String s1) {
        boolean aflag[] = { true, true, true, true, true, true, true };
        String as[] = { "", "", "", "", "", "", "" };
        String as1[] = { "", "", "", "", "", "", "" };
        String as2[] = TextUtils.split(s, ",");
        for (int i = 0; i < as2.length; i++) {
            if (as2[i].length() <= 4) {
                continue;
            }
            int k = TextUtils.dayLetterToNumber(as2[i].substring(0, 1));
            if (k < 0) {
                continue;
            }
            int l = as2[i].indexOf("-");
            if (l < 0) {
                continue;
            }
            aflag[k] = as2[i].substring(1, 2).equalsIgnoreCase("e");
            if (as[k].length() > 0) {
                as[k] += ",";
            }
            if (as1[k].length() > 0) {
                as1[k] += ",";
            }
            as[k] += as2[i].substring(2, l);
            as1[k] += as2[i].substring(l + 1);
            if (as[k].equals("00:00") && as1[k].equals("24:00")) {
                as[k] = "";
                as1[k] = "";
            }
        }

        printwriter.print("<INPUT TYPE=HIDDEN NAME=scheduleType VALUE=\"range\">");
        printwriter.print("<TABLE CELLPADDING=0 BORDER=0 CELLSPACING=0>");
        for (int j = 0; j < 7; j++) {
            printwriter.print("<TR><TD>" + dayStrings[j] + "</TD>" + "<TD>&nbsp;<select size=1 name=scheduleEnable" + j
                    + ">" + "<option value=E " + (aflag[j] ? "selected" : "") + ">enabled</option>\n"
                    + "<option value=D " + (aflag[j] ? "" : "selected") + ">disabled</option>\n" + "</select></TD>"
                    + "<TD>&nbsp;from <input type=text name=scheduleStart" + j + " size=10 value=\"" + as[j]
                    + "\"></TD>" + "<TD>&nbsp;to <input type=text name=scheduleEnd" + j + " size=10 value=\"" + as1[j]
                    + "\"></TD>" + "</TR>");
        }

        printwriter.print("</TABLE>");
    }

    public static void printAbsoluteScheduleTable(PrintWriter printwriter, String s, String s1) {
        String as[] = { "", "", "", "", "", "", "" };
        if (s.length() != 0) {
            String s2 = s.substring(absolute_prefix.length());
            String as1[] = TextUtils.split(s2, ",");
            for (int j = 0; j < as1.length; j++) {
                int k = TextUtils.dayLetterToNumber(as1[j].substring(0, 1));
                if (k >= 0) {
                    String s3 = as1[j].substring(1);
                    as[k] = s3.replace(absolute_trigger_separator, ',');
                }
            }

        }
        printwriter.print("<INPUT TYPE=HIDDEN NAME=scheduleType VALUE=\"absolute\">");
        printwriter.print("<TABLE CELLPADDING=0 BORDER=0 CELLSPACING=0>");
        for (int i = 0; i < 7; i++) {
            printwriter.print("<TR><TD>" + dayStrings[i] + "</TD>" + "<TD>&nbsp;at:&nbsp;</TD>"
                    + "<TD><INPUT TYPE=TEXT NAME=scheduleAt" + i + " SIZE=20 VALUE=\"" + as[i] + "\"></TD>" + "</TR>");
        }

        printwriter.print("</TABLE>");
    }

    public static String validateTime(String s, String s1, StringBuffer stringbuffer) {
        if (s.length() == 0) {
            s = s1;
        }
        if (s.indexOf(":") == -1) {
            s = s + ":00";
        }
        int i = s.indexOf(":");
        int j = TextUtils.toInt(s.substring(0, i));
        int k = TextUtils.toInt(s.substring(i + 1));
        if ((!TextUtils.onlyChars(s, permitted) || j < 0 || j > 24 || k < 0 || k > 60) && stringbuffer.length() == 0) {
            stringbuffer.append("Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
        }
        return s;
    }

    public static String requestToScheduleString(HTTPRequest httprequest, StringBuffer stringbuffer) {
        String s = httprequest.getValue("scheduleType");
        if (s.length() == 0) {
            return "";
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        if (s.equals("range")) {
            for (int i = 0; i < 7; i++) {
                String s1 = httprequest.getValue("scheduleEnable" + i);
                String s2 = httprequest.getValue("scheduleStart" + i).trim();
                s2 = TextUtils.removeChars(s2, " ");
                String s4 = httprequest.getValue("scheduleEnd" + i).trim();
                s4 = TextUtils.removeChars(s4, " ");
                if (s2.indexOf(",") >= 0 || s4.indexOf(",") >= 0) {
                    addCustomEntry(stringbuffer1, TextUtils.dayLetters[i], s1, s2, s4);
                    continue;
                }
                if (s1.equals("E") && s2.length() <= 0 && s4.length() <= 0) {
                    continue;
                }
                if (!TextUtils.onlyChars(s4, permitted) && stringbuffer.length() == 0) {
                    stringbuffer
                            .append("Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                s2 = validateTime(s2, "00:00", stringbuffer);
                s4 = validateTime(s4, "24:00", stringbuffer);
                int i1 = TextUtils.stringToDaySeconds(s2);
                if (i1 > TextUtils.DAY_SECONDS && stringbuffer.length() == 0) {
                    stringbuffer
                            .append("Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                int j1 = TextUtils.stringToDaySeconds(s4);
                if (j1 > TextUtils.DAY_SECONDS && stringbuffer.length() == 0) {
                    stringbuffer
                            .append("Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                if (i1 > j1 && stringbuffer.length() == 0) {
                    stringbuffer.append("The start time (from) must be less than the end time (to).");
                }
                if (stringbuffer1.length() > 0) {
                    stringbuffer1.append(",");
                }
                stringbuffer1.append(TextUtils.dayLetters[i]);
                stringbuffer1.append(s1);
                stringbuffer1.append(s2);
                stringbuffer1.append("-");
                stringbuffer1.append(s4);
            }

        }
        if (s.equals("absolute")) {
            stringbuffer1.append(absolute_prefix);
            boolean flag = false;
            for (int j = 0; j < 7; j++) {
                String s3 = httprequest.getValue("scheduleAt" + j).trim();
                s3 = TextUtils.removeChars(s3, " ");
                s3 = s3.replace(',', absolute_trigger_separator);
                if (s3.length() == 0) {
                    continue;
                }
                boolean flag1 = false;
                String as[] = TextUtils.split(s3, absolute_trigger_separator_str);
                for (int k = 0; k < as.length; k++) {
                    as[k] = validateTime(as[k], "00:00", stringbuffer);
                    flag1 = true;
                }

                if (!flag1) {
                    continue;
                }
                if (flag) {
                    stringbuffer1.append(schedule_day_separator);
                }
                flag = true;
                stringbuffer1.append(TextUtils.dayLetters[j]);
                int l;
                for (l = 0; l < as.length - 1; l++) {
                    stringbuffer1.append(as[l]);
                    stringbuffer1.append(absolute_trigger_separator);
                }

                stringbuffer1.append(as[l]);
            }

        }
        return stringbuffer1.toString();
    }

    public static void addCustomEntry(StringBuffer stringbuffer, String s, String s1, String s2, String s3) {
        String as[] = TextUtils.split(s2, ",");
        String as1[] = TextUtils.split(s3, ",");
        if (as.length == as1.length) {
            for (int i = 0; i < as.length; i++) {
                if (stringbuffer.length() > 0) {
                    stringbuffer.append(',');
                }
                stringbuffer.append(s);
                stringbuffer.append(s1);
                stringbuffer.append(as[i]);
                stringbuffer.append('-');
                stringbuffer.append(as1[i]);
            }

        } else if (as.length > 0 && as1.length > 0) {
            stringbuffer.append(s);
            stringbuffer.append(s1);
            stringbuffer.append(as[0]);
            stringbuffer.append('-');
            stringbuffer.append(as1[0]);
        }
    }

    public static HashMap scheduleStringToHashMap(String s) {
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        String as[] = TextUtils.split(s, ",");
        boolean flag = as.length > 0 && as[0].startsWith("*");
        for (int i = 0; i < as.length; i++) {
            if (as[i].length() <= 4) {
                continue;
            }
            int j = !flag || i != 0 ? 0 : 1;
            String s1 = as[i].substring(j, j + 1);
            boolean flag1 = as[i].substring(j + 1, j + 2).equalsIgnoreCase("e");
            int k = 0;
            int l = TextUtils.DAY_SECONDS;
            int i1 = as[i].indexOf("-");
            if (i1 >= 0) {
                k = TextUtils.stringToDaySeconds(as[i].substring(2, i1));
                l = TextUtils.stringToDaySeconds(as[i].substring(i1 + 1));
            } else if (flag) {
                String as1[] = TextUtils.split(as[i], "/");
                for (int j1 = 0; j1 < as1.length; j1++) {
                    k = TextUtils.stringToDaySeconds(as1[j1].substring(j + 1)) - 30;
                    l = k + 60;
                    insertEnabledEntry(hashmapordered, s1, k, l);
                    j = -1;
                }

                continue;
            }
            if (flag1) {
                if (l - k == TextUtils.DAY_SECONDS) {
                    hashmapordered.add(s1, "0-0");
                    continue;
                }
                if (!insertEnabledEntry(hashmapordered, s1, k, l)) {
                    hashmapordered.put(s1, "0-0");
                    System.out.println("Overlapping times in schedule: " + s);
                }
            } else {
                hashmapordered.add(s1, "" + k + "-" + l);
            }
        }

        return hashmapordered;
    }

    public static boolean insertEnabledEntry(HashMap hashmap, String s, int i, int j) {
        int k = hashmap.count(s) + 2;
        int ai[] = new int[k];
        int ai1[] = new int[k];
        int l = 0;
        ai[l] = 0;
        ai1[l] = i;
        l++;
        ai[l] = j;
        ai1[l] = TextUtils.DAY_SECONDS;
        l++;
        for (Enumeration enumeration = hashmap.values(s); enumeration.hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            int k1 = s1.indexOf("-");
            ai[l] = TextUtils.toInt(s1.substring(0, k1));
            ai1[l] = TextUtils.toInt(s1.substring(k1 + 1));
            l++;
        }

        sortIntegers(ai);
        sortIntegers(ai1);
        int j1 = -1;
        hashmap.remove(s);
        int l1 = 0;
        for (int i1 = 0; i1 < k; i1++) {
            if (ai[i1] == j1) {
                continue;
            }
            j1 = ai[i1];
            if (ai[i1] > ai1[l1]) {
                return false;
            }
            String s2 = "" + ai[i1] + "-" + ai1[l1++];
            if (k != 1 || !s2.equals(NO_START_STRING) && !s2.equals(NO_END_STRING)) {
                hashmap.add(s, s2);
            }
        }

        return true;
    }

    public static void sortIntegers(int ai[]) {
        for (int i = 1; i < ai.length; i++) {
            int j = i;
            int k;
            for (k = ai[i]; j > 0 && ai[j - 1] > k; j--) {
                ai[j] = ai[j - 1];
            }

            ai[j] = k;
        }

    }

    public static boolean isEnabled(String s) {
        return isEnabled(s, Platform.makeDate());
    }

    public static boolean isEnabled(String s, Date date) {
        return isEnabled(scheduleStringToHashMap(s), date);
    }

    public static boolean isEnabled(HashMap hashmap, Date date) {
        int i = date.getDay();
        int j = date.getHours() * TextUtils.HOUR_SECONDS + date.getMinutes() * TextUtils.MINUTE_SECONDS
                + date.getSeconds();
        return isEnabled(hashmap, i, j);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param i
     * @param j
     * @return
     */
    public static boolean isEnabled(HashMap hashmap, int i, int j) {
        String s = TextUtils.dayLetters[i];
        Enumeration enumeration = hashmap.values(s);
        boolean flag = true;
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            int k = s1.indexOf("-");
            if (k < 0) {
                continue;
            }
            int l = TextUtils.stringToDaySeconds(s1.substring(0, k));
            int i1 = TextUtils.stringToDaySeconds(s1.substring(k + 1));
            if (j < l || j >= i1) {
                continue;
            }
            flag = false;
            break;
        }
        return flag;
    }

    static {
        NO_END_STRING = "" + TextUtils.DAY_SECONDS + "-" + TextUtils.DAY_SECONDS;
    }
}