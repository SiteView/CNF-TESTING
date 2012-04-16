/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;

// Referenced classes of package COM.dragonflow.StandardPreference:
// ScheduleInstancePreferences

public class RangeScheduleInstancePreferences extends COM.dragonflow.StandardPreference.ScheduleInstancePreferences {

    public static COM.dragonflow.Properties.StringProperty pEnabledSunday;

    public static COM.dragonflow.Properties.StringProperty pEnabledMonday;

    public static COM.dragonflow.Properties.StringProperty pEnabledTuesday;

    public static COM.dragonflow.Properties.StringProperty pEnabledWednesday;

    public static COM.dragonflow.Properties.StringProperty pEnabledThursday;

    public static COM.dragonflow.Properties.StringProperty pEnabledFriday;

    public static COM.dragonflow.Properties.StringProperty pEnabledSaturday;

    public static COM.dragonflow.Properties.StringProperty pFromSunday;

    public static COM.dragonflow.Properties.StringProperty pFromMonday;

    public static COM.dragonflow.Properties.StringProperty pFromTuesday;

    public static COM.dragonflow.Properties.StringProperty pFromWednesday;

    public static COM.dragonflow.Properties.StringProperty pFromThursday;

    public static COM.dragonflow.Properties.StringProperty pFromFriday;

    public static COM.dragonflow.Properties.StringProperty pFromSaturday;

    public static COM.dragonflow.Properties.StringProperty pToSunday;

    public static COM.dragonflow.Properties.StringProperty pToMonday;

    public static COM.dragonflow.Properties.StringProperty pToTuesday;

    public static COM.dragonflow.Properties.StringProperty pToWednesday;

    public static COM.dragonflow.Properties.StringProperty pToThursday;

    public static COM.dragonflow.Properties.StringProperty pToFriday;

    public static COM.dragonflow.Properties.StringProperty pToSaturday;

    public RangeScheduleInstancePreferences() {
    }

    /**
     * 
     */
    public java.util.Vector getPreferenceProperties(String s, String s1, String s2, String s3, int i) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        java.util.Vector vector1;

        try {
            vector1 = new Vector();
            String s4 = "COM.dragonflow.StandardPreference." + s;
            java.lang.Class class1 = java.lang.Class.forName(s4);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences) class1.newInstance();
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            if (hashmap == null) {
                throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_RETRIEVE_MASTER_SETTINGS);
            }
            Object obj1 = null;
            Object obj2 = null;
            if (s1 != null && s1.length() > 0) {
                Enumeration enumeration = hashmap.values(s1);
                while (enumeration.hasMoreElements()) {
                    java.util.HashMap hashmap1 = new HashMap();
                    String s5 = "";
                    boolean flag = false;
                    s5 = (String) enumeration.nextElement();
                    String as[] = COM.dragonflow.Utils.TextUtils.split(s5);
                    for (int l = 0; l < as.length; l ++) {
                        int j1 = as[l].indexOf('=');
                        if (j1 <= 0) {
                            continue;
                        }
                        String s8 = as[l].substring(0, j1);
                        if (!s8.equals("_schedule")) {
                            continue;
                        }
                        String s10 = as[l].substring(j1 + 1).replace('_', ' ');
                        if (!s10.startsWith("*")) {
                            flag = true;
                        }
                    }

                    if (!flag) {
                        continue;
                    }
                    for (int i1 = 0; i1 < as.length; i1 ++) {
                        int k1 = as[i1].indexOf('=');
                        if (k1 <= 0) {
                            continue;
                        }
                        String s9 = as[i1].substring(0, k1);
                        String s11 = as[i1].substring(k1 + 1).replace('_', ' ');
                        if (s9 != null && !s9.equals("_schedule")) {
                            hashmap1.put(s9, s11);
                        } else {
                            hashmap1 = synthesizePropertiesForSchedule(hashmap1, s11);
                        }
                    }

                    if (i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL) {
                        hashmap1.put("_class", s);
                    }
                    if (s3 != null && s3 != "") {
                        String s7 = (String) hashmap1.get(s2);
                        if (!s3.equals(s7)) {
                            continue;
                        }
                        vector.add(hashmap1);
                        break;
                    }
                    vector.add(hashmap1);
                }
            } else {
                java.util.HashMap hashmap2 = new HashMap();
                jgl.Array array = preferences.getProperties();
                for (int k = 0; k < array.size(); k ++) {
                    String s6 = (String) hashmap.get(((COM.dragonflow.Properties.StringProperty) array.at(k)).getName());
                    if (s6 == null) {
                        s6 = "";
                    }
                    hashmap2.put(((COM.dragonflow.Properties.StringProperty) array.at(k)).getName(), s6);
                }

                if (i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL) {
                    hashmap2.put("_class", s);
                }
                vector.add(hashmap2);
            }
            if (vector != null) {
                for (int j = 0; j < vector.size(); j ++) {
                    java.util.HashMap hashmap3 = new HashMap();
                    COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
                    java.util.HashMap hashmap4 = (java.util.HashMap) vector.get(j);
                    java.util.Set set = hashmap4.keySet();
                    java.util.Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String s12 = (String) iterator.next();
                        String s13 = (String) hashmap4.get(s12);
                        hashmap4.put(s12, s13);
                        COM.dragonflow.Properties.StringProperty stringproperty = preferences.getPropertyObject(s12);
                        if (stringproperty != null) {
                            String s14 = verify((COM.dragonflow.Properties.StringProperty) stringproperty, s13, httprequest, hashmap4, hashmap3);
                            if (s14 != null && s14 != "") {
                                hashmap4.put(s12, s14);
                            }
                        }
                    }
                    vector1.add(hashmap4);
                }

                return vector1;
            }
        } catch (SiteViewException e) {
            e.fillInStackTrace();
            throw e;
        } catch (Exception e) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "RangeScheduleInstancePreferences", "getPreferenceProperties" }, 0L, e.getMessage());
        }
        return vector;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty.getName().indexOf("enabled") != -1) {
            checkIfValid((COM.dragonflow.Properties.ScalarProperty) stringproperty, s, httprequest, hashmap1);
        }
        return s;
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        if (scalarproperty.getName().indexOf("enabled") != -1) {
            vector.addElement("E");
            vector.addElement("enabled");
            vector.addElement("D");
            vector.addElement("disabled");
        }
        return vector;
    }

    /**
     * 
     * 
     * @param hashmap
     * @param s
     * @return
     */
    public java.util.HashMap synthesizePropertiesForSchedule(java.util.HashMap hashmap, String s) {
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, COM.dragonflow.Properties.ScheduleProperty.schedule_day_separator);
        for (int i = 0; i < as.length; i ++) {
            for (int j = 0; j < days.length; j ++) {
                if (as[i].startsWith(dayLetters[j]) && as[i].length() > 2) {
                    String s1 = as[i].substring(1, 2);
                    String s2 = as[i].substring(2);
                    int k = s2.indexOf("-");
                    if (k != -1) {
                        if (hashmap.get("from" + days[j]) == null) {
                            hashmap.put("from" + days[j], s2.substring(0, k));
                        } else {
                            String s3 = (String) hashmap.get("from" + days[j]);
                            s3 = appendSchedule(s2.substring(0, k), s3);
                            hashmap.put("from" + days[j], s3);
                        }
                        if (hashmap.get("to" + days[j]) == null) {
                            hashmap.put("to" + days[j], s2.substring(k + 1));
                        } else {
                            String s4 = (String) hashmap.get("to" + days[j]);
                            s4 = appendSchedule(s2.substring(k + 1), s4);
                            hashmap.put("to" + days[j], s4);
                        }
                    } else if (hashmap.get("from" + days[j]) == null) {
                        hashmap.put("from" + days[j], s2);
                    } else {
                        String s5 = (String) hashmap.get("from" + days[j]);
                        s5 = appendSchedule(s2, s5);
                        hashmap.put("from" + days[j], s5);
                    }
                    if (s1.equals("E")) {
                        hashmap.put("enabled" + days[j], "on");
                    } else {
                        hashmap.put("enabled" + days[j], "off");
                    }
                }
             } 
        }

        return hashmap;
    }

    public java.util.HashMap validateProperties(java.util.HashMap hashmap, jgl.Array array, java.util.HashMap hashmap1) throws java.lang.Exception {
        String s = "";
        String s2 = "";
        String s4 = "D";
        String s6 = "";
        String s10 = "";
        java.util.TreeMap treemap = new TreeMap();
        while (true) {
            String s5 = "D";
            String s3 = "";
            String s1 = "";
            boolean flag = false;
            java.util.Set set = hashmap.keySet();
            java.util.Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String s7 = (String) iterator.next();
                if (!s7.matches("from.*day")) {
                    continue;
                }
                s3 = (String) hashmap.get(s7);
                int i = s7.indexOf("from");
                s10 = s7.substring(i + 4);
                flag = true;
                break;
            } 
            if (!flag) {
                break;
            }
            set = hashmap.keySet();
            iterator = set.iterator();
            while (flag && iterator.hasNext()) {
                String s8 = (String) iterator.next();
                int j = s8.indexOf(s10);
                if (j != -1) {
                    if (s8.matches("to.*day")) {
                        s1 = (String) hashmap.get(s8);
                    } else if (s8.matches("enable.*day")) {
                        s5 = (String) hashmap.get(s8);
                        if (s5 != null) {
                            if (s5.equals("off") || s5.length() == 0) {
                                s5 = "D";
                            } else {
                                s5 = "E";
                            }
                        }
                    }
                }
            } 
            if (s1 != "" && s3 != "" && s5 != "") {
                int k = COM.dragonflow.Utils.TextUtils.dayLetterToNumber(getDayLetter(s10));
                if (k >= 0) {
                    String s13 = singleRangeSchedule(s10, s3, s1, s5, hashmap1);
                    treemap.put(new Integer(k), s13);
                }
            }
            String s12 = "to" + s10;
            String s14 = "from" + s10;
            hashmap.remove(s12);
            hashmap.remove(s14);
        } 
        
        java.util.Collection collection = treemap.values();
        String s11 = "";
        for (java.util.Iterator iterator1 = collection.iterator(); iterator1.hasNext();) {
            s11 = appendSchedule((String) iterator1.next(), s11);
        }

        hashmap.put("_schedule", s11);
        java.util.Set set1 = hashmap.keySet();
        java.util.Iterator iterator2 = set1.iterator();
        java.util.HashMap hashmap2 = new HashMap();
        while (iterator2.hasNext()) {
            String s9 = (String) iterator2.next();
            int l = s9.indexOf("enabled");
            if (l == -1) {
                hashmap2.put(s9, hashmap.get(s9));
            }
        }
        return super.validateProperties(hashmap2, array, hashmap1);
    }

    protected String singleRangeSchedule(String s, String s1, String s2, String s3, java.util.HashMap hashmap) {
        StringBuffer stringbuffer = new StringBuffer("");
        StringBuffer stringbuffer1 = new StringBuffer("");
        String s4 = "";
        if (s != null) {
            s4 = getDayLetter(s);
        }
        if (s4 != null && s1 != null && s2 != null) {
            if (s1.indexOf(",") >= 0 || s2.indexOf(",") >= 0) {
                COM.dragonflow.Properties.ScheduleProperty.addCustomEntry(stringbuffer, s4, s3, s1, s2);
            } else if (!s3.equals("E") || s1.length() > 0 || s2.length() > 0) {
                if (!COM.dragonflow.Utils.TextUtils.onlyChars(s2, COM.dragonflow.Properties.ScheduleProperty.permitted)) {
                    hashmap.put("to" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], "Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                s1 = COM.dragonflow.Properties.ScheduleProperty.validateTime(COM.dragonflow.Utils.TextUtils.removeChars(s1, " "), "0:00", stringbuffer1);
                if (stringbuffer1.length() > 0) {
                    hashmap.put("from" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], stringbuffer1.toString());
                }
                s2 = COM.dragonflow.Properties.ScheduleProperty.validateTime(COM.dragonflow.Utils.TextUtils.removeChars(s2, " "), "24:00", stringbuffer1);
                if (stringbuffer1.length() > 0) {
                    hashmap.put("to" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], stringbuffer1.toString());
                }
                int i = COM.dragonflow.Utils.TextUtils.stringToDaySeconds(s1);
                if (i > COM.dragonflow.Utils.TextUtils.DAY_SECONDS) {
                    hashmap.put("from" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], "Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                int j = COM.dragonflow.Utils.TextUtils.stringToDaySeconds(s2);
                if (j > COM.dragonflow.Utils.TextUtils.DAY_SECONDS) {
                    hashmap.put("to" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], "Times must be of the form hh:mm, where the the hour is 0-24, and the minute is 0-59");
                }
                if (i > j) {
                    hashmap.put("from" + days[COM.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], "The start time (from) must be less than the end time (to).");
                }
                stringbuffer.append(s4);
                stringbuffer.append(s3);
                stringbuffer.append(s1);
                stringbuffer.append("-");
                stringbuffer.append(s2);
            }
        }
        return stringbuffer.toString();
    }

    static {
        pEnabledSunday = new ScalarProperty("enabledSunday", "E");
        pEnabledSunday.setDisplayText("Sunday", "");
        pEnabledSunday.setParameterOptions(true, 1, false);
        pEnabledMonday = new ScalarProperty("enabledMonday", "E");
        pEnabledMonday.setDisplayText("Monday", "");
        pEnabledMonday.setParameterOptions(true, 2, false);
        pEnabledTuesday = new ScalarProperty("enabledTuesday", "E");
        pEnabledTuesday.setDisplayText("Tuesday", "");
        pEnabledTuesday.setParameterOptions(true, 3, false);
        pEnabledWednesday = new ScalarProperty("enabledWednesday", "E");
        pEnabledWednesday.setDisplayText("Wednesday", "");
        pEnabledWednesday.setParameterOptions(true, 4, false);
        pEnabledThursday = new ScalarProperty("enabledThursday", "E");
        pEnabledThursday.setDisplayText("Thursday", "");
        pEnabledThursday.setParameterOptions(true, 5, false);
        pEnabledFriday = new ScalarProperty("enabledFriday", "E");
        pEnabledFriday.setDisplayText("Friday", "");
        pEnabledFriday.setParameterOptions(true, 6, false);
        pEnabledSaturday = new ScalarProperty("enabledSaturday", "E");
        pEnabledSaturday.setDisplayText("Saturday", "");
        pEnabledSaturday.setParameterOptions(true, 7, false);
        pFromSunday = new StringProperty("fromSunday");
        pFromSunday.setDisplayText("from", "");
        pFromSunday.setParameterOptions(true, 8, false);
        pFromMonday = new StringProperty("fromMonday");
        pFromMonday.setDisplayText("from", "");
        pFromMonday.setParameterOptions(true, 9, false);
        pFromTuesday = new StringProperty("fromTuesday");
        pFromTuesday.setDisplayText("from", "");
        pFromTuesday.setParameterOptions(true, 10, false);
        pFromWednesday = new StringProperty("fromWednesday");
        pFromWednesday.setDisplayText("from", "");
        pFromWednesday.setParameterOptions(true, 11, false);
        pFromThursday = new StringProperty("fromThursday");
        pFromThursday.setDisplayText("from", "");
        pFromThursday.setParameterOptions(true, 12, false);
        pFromFriday = new StringProperty("fromFriday");
        pFromFriday.setDisplayText("from", "");
        pFromFriday.setParameterOptions(true, 13, false);
        pFromSaturday = new StringProperty("fromSaturday");
        pFromSaturday.setDisplayText("from", "");
        pFromSaturday.setParameterOptions(true, 14, false);
        pToSunday = new StringProperty("toSunday");
        pToSunday.setDisplayText("to", "");
        pToSunday.setParameterOptions(true, 15, false);
        pToMonday = new StringProperty("toMonday");
        pToMonday.setDisplayText("to", "");
        pToMonday.setParameterOptions(true, 16, false);
        pToTuesday = new StringProperty("toTuesday");
        pToTuesday.setDisplayText("to", "");
        pToTuesday.setParameterOptions(true, 17, false);
        pToWednesday = new StringProperty("toWednesday");
        pToWednesday.setDisplayText("to", "");
        pToWednesday.setParameterOptions(true, 18, false);
        pToThursday = new StringProperty("toThursday");
        pToThursday.setDisplayText("to", "");
        pToThursday.setParameterOptions(true, 19, false);
        pToFriday = new StringProperty("toFriday");
        pToFriday.setDisplayText("to", "");
        pToFriday.setParameterOptions(true, 20, false);
        pToSaturday = new StringProperty("toSaturday");
        pToSaturday.setDisplayText("to", "");
        pToSaturday.setParameterOptions(true, 21, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pEnabledSunday, pEnabledMonday, pEnabledTuesday, pEnabledWednesday, pEnabledThursday, pEnabledFriday, pEnabledSaturday, pFromSunday, pFromMonday, pFromTuesday, pFromWednesday,
                pFromThursday, pFromFriday, pFromSaturday, pToSunday, pToMonday, pToTuesday, pToWednesday, pToThursday, pToFriday, pToSaturday };
        COM.dragonflow.StandardPreference.RangeScheduleInstancePreferences.addProperties("COM.dragonflow.StandardPreference.RangeScheduleInstancePreferences", astringproperty);
    }
}
