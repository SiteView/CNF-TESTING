/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MultiContentBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MultiContentBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ApplicationBase, AtomicMonitor, Platform, MasterConfig

public abstract class MultiContentBase extends ApplicationBase {

    public static final String applicationDir = "templates.applications";

    public static final String path;

    public static final String notAvailable = "n/a";

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";

    public static int nMaxCounters;

    HashMap labelsCache;

    public MultiContentBase() {
        labelsCache = null;
    }

    protected abstract StringProperty getStateValue(int i);

    protected abstract StringProperty[] getStateValues();

    public static StringProperty[] staticInitializer(int i) {
        StringProperty astringproperty[] = new StringProperty[i];
        for (int j = 0; j < i; j++) {
            astringproperty[j] = new NumericProperty("value" + j);
            astringproperty[j].setDisplayText("Counter " + (j + 1) + " Value",
                    "Log Content Counter");
            astringproperty[j].setStateOptions(j + 1);
        }

        return astringproperty;
    }

    public static String getTemplatePath() {
        return path;
    }

    public void setCountersProperty(AtomicMonitor atomicmonitor, String s) {
        atomicmonitor.setProperty(getCountersProperty(), s);
        ((MultiContentBase) atomicmonitor).clearCounterCache();
    }

    public void setCountersPropertyValue(AtomicMonitor atomicmonitor, String s) {
        setCountersContent(s);
    }

    public String getTemplateFile() {
        return null;
    }

    public Array getAvailableCounters() {
        return getMultiContentBaseCounters("", true);
    }

    private Array getMultiContentBaseCounters(String s, boolean flag) {
        Array array = null;
        System.out.println("getMultiContentBaseCounters()");
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

    public synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            Array array = getMultiContentBaseCounters(
                    getProperty(getCountersProperty()), false);
            for (int i = 0; i < array.size(); i++) {
                String s = (String) array.at(i);
                String s1 = s;
                int j = s.indexOf(":");
                if (j >= 0) {
                    s1 = s.substring(0, j);
                }
                labelsCache.add("Counter " + (i + 1) + " Value", s1);
            }

        }
        return labelsCache;
    }

    public void clearCounterCache() {
        labelsCache = null;
    }

    public String getHostname() {
        return "";
    }

    public String[] buildThresholdsArray() {
        String as[] = null;
        as = TextUtils.split(getCountersContent(), ",");
        for (int i = 0; i < as.length; i++) {
            as[i] = as[i].trim();
        }

        return as;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        String as[] = buildThresholdsArray();
        if (as != null && as.length > 0) {
            for (int i = 0; i < as.length; i++) {
                array.add(getStateValue(i));
            }

            return array.elements();
        } else {
            return null;
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        for (int i = 0; i < nMaxCounters; i++) {
            array.add(getStateValue(i));
        }

        return array;
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
        return getStateValues();
    }

    static {
        path = Platform.getRoot() + File.separator + "templates.applications"
                + File.separator;
        nMaxCounters = 0;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_ApplicationMonitorMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 20;
        }
        Array array = new Array();
        array.add(pStateString);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i++) {
            astringproperty[i] = (StringProperty) array.at(i);
        }

        addProperties("COM.dragonflow.SiteView.MultiContentBase",
                astringproperty);
        setClassProperty("COM.dragonflow.SiteView.AtomicMonitor",
                "ApplicationType", "MultiContentBase");
    }
}
