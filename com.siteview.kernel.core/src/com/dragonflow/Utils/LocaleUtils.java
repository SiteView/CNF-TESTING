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

import java.util.Locale;

public class LocaleUtils {

    private static java.util.Locale locale = null;

    private static java.util.ResourceBundle bundle = null;

    private static java.text.DateFormat timeFormatter = null;

    private static java.text.DateFormat dateFormatter = null;

    private static java.text.DateFormat dateTimeFormatter = null;

    private static java.text.NumberFormat numberFormatter = null;

    public LocaleUtils() {
    }

    public static void setLocale(String s, String s1) {
        if (s1.length() > 0 && s.length() > 0) {
            locale = new Locale(s1, s);
        }
        if (locale == null) {
            locale = java.util.Locale.getDefault();
        }
        timeFormatter = java.text.DateFormat.getTimeInstance(3, locale);
        dateFormatter = java.text.DateFormat.getDateInstance(3, locale);
        dateTimeFormatter = java.text.DateFormat.getDateTimeInstance(3, 3, locale);
        com.dragonflow.Log.LogManager.log("RunMonitor", "Date format: " + locale.getDisplayCountry() + " (" + locale.getDisplayLanguage() + ")");
    }

    public static java.util.Locale getLocale() {
        return locale;
    }

    public static void setResourceBundle() {
        bundle = java.util.ResourceBundle.getBundle("languages.resources", locale);
    }

    public static java.util.ResourceBundle getResourceBundle() {
        if (bundle == null) {
            com.dragonflow.Utils.LocaleUtils.setLocale("US", "en");
            com.dragonflow.Utils.LocaleUtils.setResourceBundle();
        }
        return bundle;
    }

    public static void setNumberFormatter() {
        numberFormatter = java.text.NumberFormat.getNumberInstance(locale);
    }

    public static java.text.NumberFormat getNumberFormatter() {
        return numberFormatter;
    }

    public static java.text.DateFormat getTimeFormatter() {
        return timeFormatter;
    }

    public static java.text.DateFormat getDateFormatter() {
        return dateFormatter;
    }

    public static java.text.DateFormat getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static String createLink(String s, int i, String s1) {
        String s2 = "<a href=" + s1 + ">";
        String s3 = new String("");
        boolean flag = false;
        for (int k = 0; k < i; k ++) {
            int j = s.indexOf(' ');
            String s4 = s.substring(0, j);
            s2 = s2 + s4;
            s = s.substring(j);
        }

        s2 = s2 + "</a>" + s;
        return s2;
    }

}
