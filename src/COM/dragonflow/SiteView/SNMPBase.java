/*
 * 
 * Created on 2005-2-16 16:53:56
 *
 * SNMPBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SNMPBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ApplicationBase, MasterConfig, AtomicMonitor

public abstract class SNMPBase extends ApplicationBase {

    protected static CounterLock snmpLock;

    protected static StringProperty pValues[];

    public static final String notAvailable = "n/a";

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";

    private HashMap labelsCache;

    int precision;

    public SNMPBase() {
        labelsCache = null;
        precision = 0;
    }

    public abstract String getHostname();

    public boolean remoteCommandLineAllowed() {
        return false;
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
    }

    int getActiveCounters() {
        Array array = getCounters(getCountersContent());
        if (array == null) {
            return 0;
        }
        int i = array.size();
        if (i > nMaxCounters) {
            i = nMaxCounters;
        }
        return i;
    }

    public synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            Array array = getCounters(getCountersContent());
            for (int i = 0; i < array.size(); i++) {
                String s = (String) array.at(i);
                String s1 = s.substring(0, s.indexOf(":"));
                labelsCache.add("Counter " + (i + 1) + " Value", s1);
            }

        }
        return labelsCache;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        String as[] = buildThresholdsArray();
        for (int i = 0; i < as.length; i++) {
            if (getProperty("value1").length() > 0) {
                array.add(getPropertyObject("value" + i));
            }
        }

        return array.elements();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        int i = getActiveCounters();
        for (int j = 0; j < i; j++) {
            if (getProperty("value1").length() > 0) {
                array.add(getPropertyObject("value" + j));
            }
        }

        return array;
    }

    public Array getCounters(String s) {
        return getSNMPCounters(s, false);
    }

    public Array getAvailableCounters() {
        return getSNMPCounters("", true);
    }

    public String getDefaultPort() {
        return ":161";
    }

    public String getCountersParameter(String s, String s1) {
        String s2 = "";
        Array array = getAvailableCounters();
        boolean flag = true;
        String as[] = TextUtils.split(s, ",");
        int i = 0;
        label0: for (int j = 0; j < as.length; j++) {
            String s3 = as[j].trim();
            if (i >= nMaxCounters) {
                break;
            }
            int k = 0;
            do {
                if (k >= array.size()) {
                    continue label0;
                }
                if (s3.equals(array.at(k))) {
                    i++;
                    if (flag) {
                        s2 = s2 + k;
                        flag = false;
                    } else {
                        s2 = s2 + "," + k;
                    }
                    continue label0;
                }
                k++;
            } while (true);
        }

        return s2;
    }

    protected abstract boolean update();

    private Array getSNMPCounters(String s, boolean flag) {
        Array array = null;
        try {
            String s1 = "";
            if (!flag && s.length() > 0) {
                s1 = s;
            } else {
                s1 = getTemplateContent(getTemplatePath(), getTemplateFile(),
                        flag);
            }
            array = new Array();
            String as[];
            if (s1.indexOf(",") == -1) {
                as = TextUtils.split(s1, URLMonitor.CRLF);
            } else {
                as = TextUtils.split(s1, ",");
            }
            for (int i = 0; i < as.length; i++) {
                if (as[i].length() > 0) {
                    array.add(as[i]);
                }
            }

        } catch (Exception exception) {
            LogManager.log("Error", "error reading template file, "
                    + getTemplateFile() + ", " + exception);
        }
        return array;
    }

    public void setCountersPropertyValue(AtomicMonitor atomicmonitor, String s) {
        setCountersContent(s);
    }

    public void clearCounterCache() {
        labelsCache = null;
    }

    public abstract String getAppServerTestOID();

    protected abstract void getSNMPData(String s, Array array);

    protected String getTestMachine() {
        return "testwin2k1.qa.Dragonflow.com";
    }

    public String[] buildThresholdsArray() {
        String as[] = null;
        as = TextUtils.split(getCountersContent(), ",");
        for (int i = 0; i < as.length; i++) {
            int j = 0;
            do {
                if (j >= as[i].length()) {
                    break;
                }
                if (as[i].charAt(j) == ':') {
                    as[i] = as[i].substring(0, j);
                    break;
                }
                j++;
            } while (true);
            as[i] = as[i].trim();
        }

        return as;
    }

    public int getCostInLicensePoints() {
        int i = buildThresholdsArray().length;
        return 1 * i;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public StringProperty[] getValues() {
        int i = getActiveCounters();
        StringProperty astringproperty[] = new StringProperty[i];
        for (int j = 0; j < i; j++) {
            if (getProperty("value1").length() > 0) {
                astringproperty[j] = getPropertyObject("value" + j);
            }
        }

        return astringproperty;
    }

    static {
        snmpLock = null;
        HashMap hashmap = MasterConfig.getMasterConfig();
        int i = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_snmpMonitorMaximum"));
        if (i <= 0) {
            i = 10;
        }
        if (snmpLock == null) {
            snmpLock = new CounterLock(i);
        }
        Array array = new Array();
        pValues = new StringProperty[nMaxCounters];
        for (int j = 0; j < nMaxCounters; j++) {
            pValues[j] = new NumericProperty("value" + j);
            pValues[j].setDisplayText("Counter " + (j + 1) + " Value",
                    "SNMP Counter");
            pValues[j].setStateOptions(j + 1);
            array.add(pValues[j]);
        }

        array.add(pStateString);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int k = 0; k < array.size(); k++) {
            astringproperty[k] = (StringProperty) array.at(k);
        }

        addProperties("COM.dragonflow.SiteView.SNMPBase", astringproperty);
        setClassProperty("COM.dragonflow.SiteView.AtomicMonitor",
                "ApplicationType", "SNMP");
    }
}