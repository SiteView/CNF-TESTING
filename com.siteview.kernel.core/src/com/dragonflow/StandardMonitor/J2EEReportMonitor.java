/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * J2EEReportMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>J2EEReportMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.FrequencyProperty;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.J2EEMonitor;
import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.StatefulMonitor.J2EEConnection;
import com.dragonflow.Utils.J2EEDummyMonitor;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.Utils.TextUtils;

public class J2EEReportMonitor extends J2EEMonitor {
    private static class CounterSpec {

        public String type;

        public int index;

        CounterSpec(String s, int i) {
            type = s;
            index = i;
        }
    }

    private static int nMaxCounters;

    static boolean sendZeroCounters;

    NumericProperty pValuesART[];

    String artValues[];

    NumericProperty pValuesHPS[];

    String hpsValues[];

    protected static final String VALUE_STR = "value";

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static CounterSpec parseCounterSpec(String s) {

        try {
            if (!s.startsWith("value")) {
                return null;
            }
            String s1;
            String s2;
            int i = "value".length();
            int j = s.lastIndexOf(":");
            s1 = s.substring(i, j);
            s2 = s.substring(j + 1);
            return new CounterSpec(s1, Integer.parseInt(s2));
        } catch (Exception e) {
            return null;
        }
    }

    public J2EEReportMonitor() {
        pValuesART = new NumericProperty[nMaxCounters];
        artValues = new String[nMaxCounters];
        pValuesHPS = new NumericProperty[nMaxCounters];
        hpsValues = new String[nMaxCounters];
        for (int i = 0; i < nMaxCounters; i ++) {
            pValuesART[i] = new NumericProperty("valueART:" + (i + 1));
//            pValuesART[i].setDisplayText("Counter " + (i + 1) + " Value", TopazInfo.getTopazName() + " Counter Value");
            pValuesART[i].setStateOptions(1);
            pValuesART[i].defaultPrecision = 2;
            artValues[i] = "";
            pValuesHPS[i] = new NumericProperty("valueHPS:" + (i + 1));
//            pValuesHPS[i].setDisplayText("Counter " + (i + 1) + " Value", TopazInfo.getTopazName() + " Counter Value");
            pValuesHPS[i].setStateOptions(1);
            pValuesHPS[i].defaultPrecision = 5;
            hpsValues[i] = "";
        }

    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    protected int getMaxValues() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_J2EEReportMonitorMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        addProperties(array);
        return (Enumeration) array.iterator();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        addProperties(array);
        return array;
    }

    /**
     * 
     */
    protected boolean update() {
        if (stillActive()) {
            synchronized (this) {
                return super.update();
            }
        }

        return true;
    }

    private void addProperties(Array array) {
        int i = array.size();
        int j = 0;
        do {
            if (j >= getMaxValues()) {
                break;
            }
            String s = getProperty(pCounterNames[j]);
            if (s.length() == 0) {
                break;
            }
            array.add(pValuesART[j]);
            array.add(pValuesHPS[j]);
            String s1 = J2EEConnection.getGroupFromCounterName(s);
            if (s1 != null)
                ;
            j ++;
        } while (true);
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = getPropertyRealName(stringproperty);
        if (s == null) {
            s = super.GetPropertyLabel(stringproperty, flag);
        }
        return s;
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = getPropertyRealName(stringproperty);
        if (s == null) {
            s = super.getPropertyName(stringproperty);
        }
        return s;
    }

    private String getPropertyRealName(StringProperty stringproperty) {
        CounterSpec counterspec = parseCounterSpec(stringproperty.getName());
        if (counterspec == null) {
            return null;
        } else {
            return getProperty(pCounterNames[counterspec.index - 1]) + "/" + counterspec.type;
        }
    }

    protected void processResults(Map map, String s) {
        J2EEConnection j2eeconnection = (J2EEConnection) conMgr.getConnection(this, null);
        HashSet hashset = new HashSet();
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        for (int i = 0; i < getMaxValues(); i ++) {
            String s1 = getProperty(pCounterNames[i]);
            if (s1.length() == 0) {
                break;
            }
            com.dragonflow.SiteView.J2EEMonitor.CounterResult counterresult = (com.dragonflow.SiteView.J2EEMonitor.CounterResult) map.get(s1);
            String s2 = J2EEConnection.getGroupFromCounterName(s1);
            if (s2 != null && !hashset.contains(s2)) {
                hashset.add(s2);
                int k = s1.indexOf("group");
                k = s1.indexOf('/', k);
                String s4 = s1.substring(0, k);
                arraylist.add(s2);
                arraylist1.add(s4);
            }
            setProperty(pValuesART[i], counterresult.getART());
            setProperty(pValuesHPS[i], counterresult.getHPS());
            if (sendZeroCounters) {
                continue;
            }
            boolean flag;
            try {
                double d = Double.parseDouble(counterresult.getHPS());
                flag = d > 0.0D;
            } catch (Exception exception) {
                flag = true;
            }
            pValuesART[i].isTopazLogProperty = flag;
            pValuesHPS[i].isTopazLogProperty = flag;
        }

        String as[] = new String[arraylist.size()];
        String as1[] = new String[as.length];
        for (int j = 0; j < as.length; j ++) {
            com.dragonflow.SiteView.J2EEMonitor.CounterResult counterresult1 = (com.dragonflow.SiteView.J2EEMonitor.CounterResult) map.get(arraylist1.get(j));
            as[j] = counterresult1.getART();
            as1[j] = counterresult1.getHPS();
        }

        if (s == null) {
            StringBuffer stringbuffer = new StringBuffer();
            String s3 = "";
            for (int l = 0; l < arraylist.size(); l ++) {
                if (!as[l].equals("n/a")) {
                    stringbuffer.append(s3 + arraylist.get(l) + " Avg: ART=" + as[l] + "/HPS=" + as1[l]);
                    s3 = ", ";
                }
            }

            setProperty(pStateString, stringbuffer.toString());
            setProperty(pStatus, "OK");
        } else {
            setProperty(pStateString, s);
            setProperty(pStatus, "error");
        }
    }

    public Set getAllCounters() {
        HashSet hashset = new HashSet();
        for (int i = 0; i < nMaxCounters; i ++) {
            String s = getProperty(pCounterNames[i]);
            if (s.equals("")) {
                break;
            }
            hashset.add(s);
            int j = s.indexOf("group: ");
            if (j >= 0) {
                j = s.indexOf('/', j);
                String s1 = s.substring(0, j);
                hashset.add(s1);
            }
        }

        return hashset;
    }

    public void setProperty(StringProperty stringproperty, String s) {
        CounterSpec counterspec = parseCounterSpec(stringproperty.getName());
        if (counterspec == null) {
            super.setProperty(stringproperty, s);
            return;
        }
        String as[];
        if (counterspec.type.endsWith("ART")) {
            as = artValues;
        } else if (counterspec.type.endsWith("HPS")) {
            as = hpsValues;
        } else {
            super.setProperty(stringproperty, s);
            return;
        }
        int i = counterspec.index - 1;
        if (i < 0 || i >= nMaxCounters) {
            super.setProperty(stringproperty, s);
            return;
        } else {
            as[i] = s;
            return;
        }
    }

    public String getProperty(StringProperty stringproperty) {
        String s = stringproperty.getName();
        if (s.startsWith("value")) {
            return getProperty(s);
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public String getProperty(String s) {
        CounterSpec counterspec = parseCounterSpec(s);
        if (counterspec == null) {
            return super.getProperty(s);
        }
        String as[];
        if (counterspec.type.endsWith("ART")) {
            as = artValues;
        } else if (counterspec.type.endsWith("HPS")) {
            as = hpsValues;
        } else {
            return super.getProperty(s);
        }
        int i = counterspec.index - 1;
        if (i < 0 || i >= nMaxCounters) {
            return super.getProperty(s);
        } else {
            return as[i];
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pFrequency) {
            HashMap hashmap1 = MasterConfig.getMasterConfig();
            int i = TextUtils.toInt(TextUtils.getValue(hashmap1, "_J2EEReportMonitorMinRefreshTime"));
            if (i < 15) {
                i = 300;
            }
            String s1 = httprequest.getValue(stringproperty.getName() + "Units");
            int j = FrequencyProperty.toSeconds(TextUtils.toInt(s), s1);
            if (j < i && j != 0) {
                hashmap.put(stringproperty, j + "  was less than " + i + " seconds");
            }
            return Integer.toString(j);
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public StringProperty getTopazValueProperty(String s) {
        StringProperty stringproperty = super.getTopazValueProperty(s);
        if (stringproperty.isTopazLogProperty) {
            return stringproperty;
        } else {
            return null;
        }
    }

    public StringProperty getPropertyObject(String s) {
        CounterSpec counterspec = parseCounterSpec(s);
        if (counterspec != null) {
            NumericProperty anumericproperty[] = null;
            if (counterspec.type.endsWith("ART")) {
                anumericproperty = pValuesART;
            } else if (counterspec.type.endsWith("HPS")) {
                anumericproperty = pValuesHPS;
            }
            if (anumericproperty != null) {
                int i = counterspec.index - 1;
                if (i >= 0 && i < nMaxCounters) {
                    return anumericproperty[i];
                }
            }
        }
        return super.getPropertyObject(s);
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return getPropertyName(stringproperty);
    }

    static {
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_J2EEReportMonitorMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 1000;
        }
        String s = TextUtils.getValue(hashmap, "_J2EEReportMonitorSendZeroCounters");
        if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("0") || s.length() == 0) {
            sendZeroCounters = false;
        } else {
            sendZeroCounters = true;
        }
        Array array = new Array();
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i ++) {
            astringproperty[i] = (StringProperty) array.get(i);
        }

        addProperties("com.dragonflow.StandardMonitor.J2EEReportMonitor", astringproperty);
        addClassElement("com.dragonflow.StandardMonitor.J2EEReportMonitor", Rule.stringToClassifier("status != OK\terror"));
        addClassElement("com.dragonflow.StandardMonitor.J2EEReportMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "description", "Monitor J2EE systems.");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "help", "J2EEReportMon.htm");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "title", "J2EE (Report)");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "class", "J2EEReportMonitor");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "target", "_target");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "topazName", "J2EE (Report)");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "classType", "application");
        setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "topazType", "Web Application Server");
        if (LUtils.isValidSSforXLicense(new J2EEDummyMonitor())) {
            setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "loadable", "true");
        } else {
            setClassProperty("com.dragonflow.StandardMonitor.J2EEReportMonitor", "loadable", "false");
        }
    }
}
