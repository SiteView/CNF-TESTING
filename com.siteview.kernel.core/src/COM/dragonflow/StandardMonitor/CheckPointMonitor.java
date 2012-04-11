/*
 * 
 * Created on 2005-3-7 0:51:22
 *
 * CheckPointMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>CheckPointMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.net.URLEncoder;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SNMPBase;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// SNMPMonitor

public class CheckPointMonitor extends SNMPBase {

    public static String returnURL;

    public static String templateFile;

    protected static StringProperty pOIDs;

    protected static StringProperty pHost;

    protected static StringProperty pTimeout;

    protected static StringProperty pRetryDelay;

    protected static StringProperty pCommunity;

    protected static StringProperty pIndex;

    private static StringProperty pCounters;

    public CheckPointMonitor() {
    }

    public String getHostname() {
        return getProperty(pHost);
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        setCountersContent(getCountersContent());
    }

    protected boolean update() {
        Array array = getCounters(getCountersContent());
        String s = getProperty(pHost);
        if (s.indexOf(":") == -1) {
            s = s + getDefaultPort();
        }
        getSNMPData(s, array);
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected void getSNMPData(String s, Array array) {
        StringBuffer stringbuffer = new StringBuffer();
        int i = getPropertyAsInteger(pTimeout);
        int j = getPropertyAsInteger(pRetryDelay);
        String s1 = getProperty(pCommunity);
        String s2 = getProperty(pIndex);
        String s3 = SNMPMonitor.readSNMPValue(s, "appServerTest", i, j, s1, stringbuffer, getAppServerTestOID(), s2, false, "V1");
        int k = -1;
        if (s3.length() > 0) {
            k = Integer.valueOf(s3).intValue();
        }
        if (k >= 0) {
            Enumeration enumeration = array.elements();
            String[] as = new String[array.size()];
            String[] as1 = new String[array.size()];
            int i1 = 0;

            while (enumeration.hasMoreElements()) {
                try {
                    String s4 = (String) enumeration.nextElement();
                    String s5 = s4.substring(s4.indexOf(":") + 1, s4.length());
                    String s7 = s4.substring(0, s4.indexOf(":"));
                    snmpLock.get();
                    as[i1] = SNMPMonitor.readSNMPValue(s, s7, i, j, s1, stringbuffer, s5, s2, false, "V1");
                    if (stringbuffer.length() > 0) {
                        as1[i1] = stringbuffer.toString();
                        stringbuffer = new StringBuffer();
                    }
                    i1 ++;
                    snmpLock.release();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    snmpLock.release();
                }
            }

            if (stillActive()) {
                synchronized (this) {
                    String s6 = "";
                    if (stringbuffer.length() <= 0) {
                        HashMap hashmap = getLabels();
                        for (int k1 = 0; k1 < as.length; k1 ++) {
                            String s8 = (String) hashmap.get("Counter " + (k1 + 1) + " Value");
                            if (as[k1].equals("")) {
                                setProperty(pValues[k1], "n/a");
                                s6 = s6 + s8 + " = " + as1[k1];
                                if (k1 != as.length - 1) {
                                    s6 = s6 + ", ";
                                }
                            } else {
                                setProperty(pValues[k1], TextUtils.removeChars(as[k1], ","));
                                s6 = s6 + s8 + " = " + as[k1];
                                if (k1 != as.length - 1) {
                                    s6 = s6 + ", ";
                                }
                            }
                        }

                    } else {
                        for (int j1 = 0; j1 < pValues.length; j1 ++) {
                            setProperty(pValues[j1], "n/a");
                        }

                        s6 = stringbuffer.toString();
                        setProperty(pNoData, "n/a");
                    }
                    setProperty(pStateString, s6);
                }
            }
        } else {
            for (int l = 0; l < pValues.length; l ++) {
                setProperty(pValues[l], "n/a");
            }

            setProperty(pNoData, "n/a");
            setProperty(pStateString, "Application Server not available on host");
            LogManager.log("Error", "Application Server not available on host: " + s);
        }
    }

    public String getCountersContent() {
        return getProperty(pCounters);
    }

    public void setCountersContent(String s) {
        setProperty(pCounters, s);
    }

    public String getReturnURL() {
        return HTTPRequest.encodeString(returnURL);
    }

    public StringProperty getCountersProperty() {
        return pCounters;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public String getAppServerTestOID() {
        return "1.3.6.1.4.1.2620.1.1.7";
    }

    protected String getTestMachine() {
        return "testwin2k1.qa.Dragonflow.com";
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pHost) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if (stringproperty == pCounters) {
            if (s.equals("No Counters available for this machine")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pIndex) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pCommunity) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=CheckPointMonitor";
        templateFile = "counters.cp";
        pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "CheckPoint Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename="
                + templateFile + "&type=SNMP\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        pHost = new StringProperty("_host", "", "host name");
        pHost.setDisplayText("Host Name", "the IP address or host name of the CheckPoint Server to be monitored.");
        pHost.setParameterOptions(true, 2, false);
        pIndex = new StringProperty("_index", "0");
        pIndex.setDisplayText("Index", "the index of the SNMP object - for non-table object IDs, this is 0");
        pIndex.setParameterOptions(true, 3, false);
        pCommunity = new StringProperty("_community", "public");
        pCommunity.setDisplayText("Community", "Community for the SNMP object");
        pCommunity.setParameterOptions(true, 4, false);
        pRetryDelay = new NumericProperty("_retryDelay", "1", "seconds");
        pRetryDelay.setDisplayText("Retry Delay", "the time, in seconds, to wait before retrying the request");
        pRetryDelay.setParameterOptions(true, 1, true);
        pTimeout = new NumericProperty("_timeout", "5", "seconds");
        pTimeout.setDisplayText("Timeout", "the total time, in seconds, to wait for a successful reply");
        pTimeout.setParameterOptions(true, 2, true);
        StringProperty astringproperty[] = { pHost, pCounters, pIndex, pCommunity, pRetryDelay, pTimeout };
        addProperties("COM.dragonflow.StandardMonitor.CheckPointMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.CheckPointMonitor", Rule.stringToClassifier("value0 == 'n/a'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.CheckPointMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "description", "Monitors CheckPoint FireWall-1 server performance metrics.");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "help", "CheckPointMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "title", "CheckPoint Firewall-1");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "class", "CheckPointMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "target", "_CheckPoint");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "toolPageDisable", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "topazName", "CheckPoint");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "applicationType", "SNMP");
        setClassProperty("COM.dragonflow.StandardMonitor.CheckPointMonitor", "loadable", "true");
    }
}
