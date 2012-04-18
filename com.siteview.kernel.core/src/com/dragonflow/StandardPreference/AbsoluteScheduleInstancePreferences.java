/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;

import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteViewException.SiteViewException;
import com.dragonflow.SiteViewException.SiteViewOperationalException;
import com.dragonflow.Utils.jglUtils;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.StandardPreference:
// ScheduleInstancePreferences

public class AbsoluteScheduleInstancePreferences extends com.dragonflow.StandardPreference.ScheduleInstancePreferences {

    public static final int SUNDAY = 0;

    public static final int MONDAY = 1;

    public static final int TUESDAY = 2;

    public static final int WEDNESDAY = 3;

    public static final int THURSDAY = 4;

    public static final int FRIDAY = 5;

    public static final int SATURDAY = 6;

    public static com.dragonflow.Properties.StringProperty pAtSunday;

    public static com.dragonflow.Properties.StringProperty pAtMonday;

    public static com.dragonflow.Properties.StringProperty pAtTuesday;

    public static com.dragonflow.Properties.StringProperty pAtWednesday;

    public static com.dragonflow.Properties.StringProperty pAtThursday;

    public static com.dragonflow.Properties.StringProperty pAtFriday;

    public static com.dragonflow.Properties.StringProperty pAtSaturday;

    public AbsoluteScheduleInstancePreferences() {
    }

    /**
     * 
     */
    public java.util.Vector getPreferenceProperties(String s, String s1, String s2, String s3, int i) throws com.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        java.util.Vector vector1;

        try {
            vector1 = new Vector();
            String s4 = "com.dragonflow.StandardPreference." + s;
            java.lang.Class class1 = java.lang.Class.forName(s4);
            com.dragonflow.SiteView.Preferences preferences = (com.dragonflow.SiteView.Preferences) class1.newInstance();
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            if (hashmap == null) {
                throw new SiteViewOperationalException(com.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_RETRIEVE_MASTER_SETTINGS);
            }
            Object obj1 = null;
            Object obj2 = null;
            if (s1 != null && s1.length() > 0) {
                Enumeration enumeration = (Enumeration) hashmap.values(s1);
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap1 = new HashMap();
                    String s5 = "";
                    boolean flag = false;
                    s5 = (String) enumeration.nextElement();
                    String as[] = com.dragonflow.Utils.TextUtils.split(s5);
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
                        if (s10.startsWith("*")) {
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

                    if (i == com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL) {
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
                HashMap hashmap2 = new HashMap();
                Array array = preferences.getProperties();
                for (int k = 0; k < array.size(); k ++) {
                    String s6 = (String) hashmap.get(((com.dragonflow.Properties.StringProperty) array.get(k)).getName());
                    if (s6 == null) {
                        s6 = "";
                    }
                    hashmap2.put(((com.dragonflow.Properties.StringProperty) array.get(k)).getName(), s6);
                }

                if (i == com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL) {
                    hashmap2.put("_class", s);
                }
                vector.add(hashmap2);
            }
            if (vector != null) {
                for (int j = 0; j < vector.size(); j ++) {
                    HashMap hashmap3 = new HashMap();
                    com.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
                    HashMap hashmap4 = (HashMap) vector.get(j);

                    java.util.Iterator iterator = hashmap4.iterator();
                    while (iterator.hasNext()) {
                        String s12 = (String) iterator.next();
                        String s13 = (String) hashmap4.get(s12);
                        hashmap4.put(s12, s13);
                        com.dragonflow.Properties.StringProperty stringproperty = preferences.getPropertyObject(s12);
                        if (stringproperty != null) {
                            String s14 = verify((com.dragonflow.Properties.StringProperty) stringproperty, s13, httprequest, jglUtils.fromJgl(hashmap4), jglUtils.fromJgl(hashmap3));
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
            throw new SiteViewOperationalException(com.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "AbsoluteScheduleInstancePreferences", "getPreferenceProperties" }, 0L, e.getMessage());
        }
        return vector;
    }

    protected String singleAbsoluteSchedule(String s, String s1, java.util.HashMap hashmap) {
        StringBuffer stringbuffer = new StringBuffer("");
        StringBuffer stringbuffer1 = new StringBuffer("");
        if (s1 != null) {
            s1 = com.dragonflow.Utils.TextUtils.removeChars(s1, " ");
            s1 = s1.replace(',', com.dragonflow.Properties.ScheduleProperty.absolute_trigger_separator);
            boolean flag = false;
            if (s1.length() != 0) {
                boolean flag1 = false;
                String as[] = com.dragonflow.Utils.TextUtils.split(s1, com.dragonflow.Properties.ScheduleProperty.absolute_trigger_separator_str);
                for (int i = 0; i < as.length; i ++) {
                    as[i] = com.dragonflow.Properties.ScheduleProperty.validateTime(as[i], "00:00", stringbuffer1);
                    if (stringbuffer1.length() > 0) {
                        hashmap.put("at" + days[com.dragonflow.Utils.TextUtils.dayLetterToNumber(s)], stringbuffer1.toString());
                        return stringbuffer.toString();
                    }
                    flag1 = true;
                }

                if (flag1) {
                    if (flag) {
                        stringbuffer.append(com.dragonflow.Properties.ScheduleProperty.schedule_day_separator);
                    }
                    flag = true;
                    String s2 = getDayLetter(s);
                    stringbuffer.append(s2);
                    int j;
                    for (j = 0; j < as.length - 1; j ++) {
                        stringbuffer.append(as[j]);
                        stringbuffer.append(com.dragonflow.Properties.ScheduleProperty.absolute_trigger_separator);
                    }

                    stringbuffer.append(as[j]);
                }
            }
        }
        return stringbuffer.toString();
    }

    public HashMap synthesizePropertiesForSchedule(HashMap hashmap, String s) {
        String as[] = com.dragonflow.Utils.TextUtils.split(s, com.dragonflow.Properties.ScheduleProperty.schedule_day_separator);
        for (int i = 0; i < days.length; i ++) {
            boolean flag = false;
            for (int j = 0; j < as.length; j ++) {
                if (as[j].startsWith(com.dragonflow.Properties.ScheduleProperty.absolute_prefix + dayLetters[i])) {
                    if (as[j].length() > 2) {
                        String s1 = as[j].substring(2);
                        s1 = s1.replaceAll(com.dragonflow.Properties.ScheduleProperty.absolute_trigger_separator_str, com.dragonflow.Properties.ScheduleProperty.schedule_day_separator);
                        hashmap.put("at" + days[i], s1);
                        flag = true;
                    }
                    continue;
                }
                if (as[j].startsWith(dayLetters[i]) && as[j].length() > 2) {
                    String s2 = as[j].substring(1);
                    s2 = s2.replaceAll(com.dragonflow.Properties.ScheduleProperty.absolute_trigger_separator_str, com.dragonflow.Properties.ScheduleProperty.schedule_day_separator);
                    hashmap.put("at" + days[i], s2);
                    flag = true;
                }
            }

            if (!flag) {
                hashmap.put("at" + days[i], "");
            }
        }

        return hashmap;
    }

    public java.util.HashMap validateProperties(java.util.HashMap hashmap, Array array, java.util.HashMap hashmap1) throws java.lang.Exception {
        java.util.Set set = hashmap.keySet();
        java.util.Iterator iterator = set.iterator();
        java.util.HashMap hashmap2 = new java.util.HashMap();
        String s = "";
        java.util.TreeMap treemap = new TreeMap();
        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();
            String s2 = (String) hashmap.get(s1);
            if (s1.matches("at.*")) {
                int i = com.dragonflow.Utils.TextUtils.dayLetterToNumber(getDayLetter(s1));
                if (i >= 0) {
                    s2 = singleAbsoluteSchedule(s1, s2, hashmap1);
                    treemap.put(new Integer(i), s2);
                }
            } else {
                hashmap2.put(s1, s2);
            }
        } 
        java.util.Collection collection = treemap.values();
        for (java.util.Iterator iterator1 = collection.iterator(); iterator1.hasNext();) {
            s = appendSchedule((String) iterator1.next(), s);
        }

        s = com.dragonflow.Properties.ScheduleProperty.absolute_prefix + s;
        hashmap2.put("_schedule", s);
        return super.validateProperties(hashmap2, array, hashmap1);
    }

    static {
        pAtSunday = new StringProperty("atSunday");
        pAtSunday.setDisplayText("at", "");
        pAtSunday.setParameterOptions(true, 1, false);
        pAtMonday = new StringProperty("atMonday");
        pAtMonday.setDisplayText("at", "");
        pAtMonday.setParameterOptions(true, 2, false);
        pAtTuesday = new StringProperty("atTuesday");
        pAtTuesday.setDisplayText("at", "");
        pAtTuesday.setParameterOptions(true, 3, false);
        pAtWednesday = new StringProperty("atWednesday");
        pAtWednesday.setDisplayText("at", "");
        pAtWednesday.setParameterOptions(true, 4, false);
        pAtThursday = new StringProperty("atThursday");
        pAtThursday.setDisplayText("at", "");
        pAtThursday.setParameterOptions(true, 5, false);
        pAtFriday = new StringProperty("atFriday");
        pAtFriday.setDisplayText("at", "");
        pAtFriday.setParameterOptions(true, 6, false);
        pAtSaturday = new StringProperty("atSaturday");
        pAtSaturday.setDisplayText("at", "");
        pAtSaturday.setParameterOptions(true, 7, false);
        com.dragonflow.Properties.StringProperty astringproperty[] = { pAtSunday, pAtMonday, pAtTuesday, pAtWednesday, pAtThursday, pAtFriday, pAtSaturday };
        com.dragonflow.StandardPreference.AbsoluteScheduleInstancePreferences.addProperties("com.dragonflow.StandardPreference.AbsoluteScheduleInstancePreferences", astringproperty);
    }
}
