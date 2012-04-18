/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * LogReader.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>LogReader</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogReader {

    int lineDate[];

    public static int YEAR = 0;

    public static int MONTH = 1;

    public static int DATE = 2;

    public static int HOUR = 3;

    public static int MINUTE = 4;

    public static int SECOND = 5;

    public PrintWriter progressStream;

    static final String timeDigits = "(\\d{2})";

    static final String dateDigits = "(\\d{2,4})";

    static final String space = "\\s+";

    static final String regex = ".*?(\\d{2}):(\\d{2}):(\\d{2})\\s+(\\d{2,4})/(\\d{2,4})/(\\d{2,4})\\s+.*?";

    static Pattern p = Pattern
            .compile(".*?(\\d{2}):(\\d{2}):(\\d{2})\\s+(\\d{2,4})/(\\d{2,4})/(\\d{2,4})\\s+.*?");

    public LogReader() {
        lineDate = new int[6];
        progressStream = null;
    }

    public static boolean isDateLine(String s) {
        return s.length() >= 20 && s.charAt(2) == ':' && s.charAt(5) == ':'
                && s.charAt(11) == '/' && s.charAt(14) == '/';
    }

    public static boolean getDateInfo(String s, int ai[]) {
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            if (matcher.groupCount() == 6) {
                try {
                    ai[HOUR] = Integer.parseInt(matcher.group(1));
                    ai[MINUTE] = Integer.parseInt(matcher.group(2));
                    ai[SECOND] = Integer.parseInt(matcher.group(3));
                    ai[MONTH] = Integer.parseInt(matcher.group(4)) - 1;
                    ai[DATE] = Integer.parseInt(matcher.group(5));
                    int i = Integer.parseInt(matcher.group(6));
                    if (i > 1900) {
                        i -= 1900;
                    }
                    ai[YEAR] = i;
                } catch (NumberFormatException numberformatexception) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        if (ai[HOUR] > 23) {
            return false;
        }
        if (ai[MINUTE] > 59) {
            return false;
        }
        if (ai[SECOND] > 59) {
            return false;
        }
        if (ai[DATE] > 31) {
            return false;
        }
        if (ai[MONTH] > 11) {
            return false;
        }
        if (ai[HOUR] < 0) {
            return false;
        }
        if (ai[MINUTE] < 0) {
            return false;
        }
        if (ai[SECOND] < 0) {
            return false;
        }
        if (ai[DATE] < 0) {
            return false;
        }
        return ai[MONTH] >= 0;
    }

    public static Date getDate(int ai[]) {
        return new Date(ai[YEAR], ai[MONTH], ai[DATE], ai[HOUR], ai[MINUTE],
                ai[SECOND]);
    }

    Date getDate(String s) {
        if (getDateInfo(s, lineDate)) {
            return getDate(lineDate);
        } else {
            return null;
        }
    }

}
