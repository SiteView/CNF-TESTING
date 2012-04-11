/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MonitorLoadMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MonitorLoadMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MonitorQueue;
import COM.dragonflow.SiteView.MultiContentBase;
import COM.dragonflow.SiteView.Rule;

public class MonitorLoadMonitor extends MultiContentBase {

    static StringProperty pCounters;

    protected static StringProperty pStatus;

    protected static StringProperty pLoadValues[];

    static String returnURL;

    public static String templateFile;

    public MonitorLoadMonitor() {
    }

    protected StringProperty getStateValue(int i) {
        return pLoadValues[i];
    }

    protected StringProperty[] getStateValues() {
        return pLoadValues;
    }

    protected String getVal(String s) {
        if (s.equals("Current Monitors Run Per Minute")) {
            return Float.toString(AtomicMonitor.monitorStats.getCountPerTimePeriod());
        }
        if (s.equals("Current Monitors Running")) {
            return Integer.toString(MonitorQueue.getRunningCount());
        }
        if (s.equals("Current Monitors Waiting")) {
            return Integer.toString(MonitorQueue.readyMonitors.size());
        }
        if (s.equals("Maximum Monitors Run Per Minute")) {
            StringBuffer stringbuffer = new StringBuffer(Float.toString(AtomicMonitor.monitorStats.getMaximumCountPerTimePeriod()));
            return stringbuffer.toString();
        }
        if (s.equals("Maximum Monitors Running")) {
            StringBuffer stringbuffer1 = new StringBuffer(Integer.toString((int) AtomicMonitor.monitorStats.getMaximum()));
            return stringbuffer1.toString();
        }
        if (s.equals("Maximum Monitors Waiting")) {
            StringBuffer stringbuffer2 = new StringBuffer(Integer.toString(MonitorQueue.maxReadyMonitors));
            return stringbuffer2.toString();
        } else {
            return "";
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        try {
            for (int i = 0; i < pLoadValues.length; i ++) {
                unsetProperty(pLoadValues[i]);
            }

            String as[] = buildThresholdsArray();
            StringBuffer stringbuffer = new StringBuffer(as.length * 50);
            for (int j = 0; j < as.length; j ++) {
                String s = getVal(as[j]);
                setProperty(pLoadValues[j], s);
                pLoadValues[j].setLabel(as[j]);
                stringbuffer.append(as[j]).append(":&nbsp;").append(s);
                if (j < as.length - 1) {
                    stringbuffer.append(", ");
                }
            }

            setProperty(pStatus, "ok");
            setProperty(pStateString, stringbuffer.toString());
            return true;
        } catch (Exception e) {
            setProperty(pStateString, e.getMessage());
            setProperty(pStatus, "Error");
            return false;
        }
    }

    public String getCountersContent() {
        return getProperty(pCounters);
    }

    public StringProperty getCountersProperty() {
        return pCounters;
    }

    public void setCountersContent(String s) {
        setProperty(pCounters, s);
    }

    public String getReturnURL() {
        return returnURL;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getCountersProperty()) {
            String s1 = getCountersContent();
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public int getCostInLicensePoints() {
        return 0;
    }

    static {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=MonitorLoadMonitor";
        templateFile = "counters.monitorLoad";
        String s = (COM.dragonflow.StandardMonitor.MonitorLoadMonitor.class).getName();
        pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "Monitor Load Selected Counters");
        try {
            pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL, "UTF-8") + "&maxcounters=" + nMaxCounters
                    + "&filename=" + templateFile + "&type=URLContent\">choose counters</A>");
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            LogManager.log("error", "Failed to encode URL in MonitorLoadhMonitor static initializer: " + unsupportedencodingexception);
        }
        pCounters.setParameterOptions(true, 1, false);
        pCounters.isMultiLine = true;
        pStatus = new StringProperty("status", "n/a");
        StringProperty astringproperty[] = new StringProperty[nMaxCounters + 2];
        astringproperty[0] = pStatus;
        astringproperty[1] = pCounters;
        pLoadValues = staticInitializer(nMaxCounters);
        for (int i = 0; i < nMaxCounters; i ++) {
            astringproperty[i + 2] = pLoadValues[i];
        }

        addProperties(s, astringproperty);
        setClassProperty(s, "class", "MonitorLoadMonitor");
        setClassProperty(s, "description", "Monitors monitor load");
        setClassProperty(s, "title", "Monitor Load Monitor");
        setClassProperty(s, "help", "MonitorLoadMonitor.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Monitor Load Monitor");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "applicationType", "MultiContentBase");
        setClassProperty(s, "addable", "false");
        addClassElement(s, Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement(s, Rule.stringToClassifier("status == 'ok'\tgood"));
    }
}
