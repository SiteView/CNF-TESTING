/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebLogic5xMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebLogic5xMonitor</code>
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
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SNMPBase;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// SNMPMonitor

public class WebLogic5xMonitor extends SNMPBase {

    public static String returnURL;

    public static String templateFile;

    protected static StringProperty pOIDs;

    protected static StringProperty pHost;

    protected static StringProperty pTimeout;

    protected static StringProperty pRetryDelay;

    protected static StringProperty pCommunity;

    protected static StringProperty pIndex;

    private static StringProperty pCounters;

    public WebLogic5xMonitor() {
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
        Enumeration enumeration = array.elements();
        String[] as = new String[array.size()];
        String[] as1 = new String[array.size()];

        int k = 0;
        while (enumeration.hasMoreElements()) {
            try {
                String s3 = (String) enumeration.nextElement();
                String s4 = s3.substring(s3.indexOf(":") + 1, s3.length());
                String s6 = s3.substring(0, s3.indexOf(":"));
                snmpLock.get();
                as[k] = SNMPMonitor.readSNMPValue(s, s6, i, j, s1, stringbuffer, s4, s2, false, "V1");
                if (stringbuffer.length() > 0) {
                    as1[k] = stringbuffer.toString();
                    stringbuffer = new StringBuffer();
                }
                k ++;
                snmpLock.release();
            }

            catch (Exception exception) {
                exception.printStackTrace();
                snmpLock.release();
            }
        }

        if (stillActive()) {
            synchronized (this) {
                String s5 = "";
                if (stringbuffer.length() <= 0) {
                    HashMap hashmap = getLabels();
                    for (int i1 = 0; i1 < as.length; i1 ++) {
                        String s7 = (String) hashmap.get("Counter " + (i1 + 1) + " Value");
                        if (as[i1].equals("")) {
                            setProperty(pValues[i1], "n/a");
                            s5 = s5 + s7 + " = " + as1[i1];
                            if (i1 != as.length - 1) {
                                s5 = s5 + ", ";
                            }
                        } else {
                            setProperty(pValues[i1], TextUtils.removeChars(as[i1], ","));
                            s5 = s5 + s7 + " = " + as[i1];
                            if (i1 != as.length - 1) {
                                s5 = s5 + ", ";
                            }
                        }
                    }

                } else {
                    for (int l = 0; l < pValues.length; l ++) {
                        setProperty(pValues[l], "n/a");
                    }

                    setProperty(pNoData, "n/a");
                    s5 = stringbuffer.toString();
                }
                setProperty(pStateString, s5);
            }
        }
        return;
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
        return ".1.3.6.1.4.1.140.600.20.1.20";
    }

    protected String getTestMachine() {
        return "testwin2k2.qa.dragonflow.com";
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
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=WebLogic5xMonitor";
        templateFile = "counters.beawl";
        pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "WebLogic5x Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename="
                + templateFile + "&type=SNMP\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        pHost = new StringProperty("_host", "", "host name");
        pHost.setDisplayText("Host Name", "the IP address or host name of the WebLogic5x Server to be monitored.");
        pHost.setParameterOptions(true, 2, false);
        pIndex = new StringProperty("_index", "0");
        pIndex.setDisplayText("Index", "the index of the SNMP object - this must be a non-zero value, <A HREF=\"/SiteView/docs/WebLogic5xMon.htm#OIDindex\" target=\"help\">see help page</A> for more information");
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
        addProperties("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", Rule.stringToClassifier("value0 == 'n/a'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "description", "Monitors BEA WebLogic Server 5.x performance statistics.");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "help", "WebLogic5xMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "title", "WebLogic 5.x Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "class", "WebLogic5xMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "target", "_WebLogic5x");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "toolPageDisable", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "topazName", "BEA WebLogic5x");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "applicationType", "SNMP");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "loadable", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.WebLogic5xMonitor", "addable", "false");
    }
}
