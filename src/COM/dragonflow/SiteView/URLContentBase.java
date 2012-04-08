/*
 * 
 * Created on 2005-2-16 17:43:31
 *
 * URLContentBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>URLContentBase</code>
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
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ApplicationBase, AtomicMonitor, Monitor

public abstract class URLContentBase extends ApplicationBase {

    protected static StringProperty pValues[];

    public static final String notAvailable = "n/a";

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";

    HashMap labelsCache;

    public URLContentBase() {
        labelsCache = null;
    }

    public abstract String buildRegExp();

    public static String getTemplatePath() {
        return path;
    }

    public void setCountersProperty(AtomicMonitor atomicmonitor, String s) {
        atomicmonitor.setProperty(getCountersProperty(), s);
        ((URLContentBase) atomicmonitor).clearCounterCache();
    }

    public void setCountersPropertyValue(AtomicMonitor atomicmonitor, String s) {
        setCountersContent(s);
    }

    public String getTemplateFile() {
        return null;
    }

    public Array getAvailableCounters() {
        return getURLContentCounters("", true);
    }

    private Array getURLContentCounters(String s, boolean flag) {
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

    public synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            Array array = getURLContentCounters(
                    getProperty(getCountersProperty()), false);
            for (int i = 0; i < array.size(); i++) {
                String s = (String) array.at(i);
                labelsCache.add("Counter " + (i + 1) + " Value", s);
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
        for (int i = 0; i < as.length; i++) {
            array.add(getPropertyObject("value" + i));
        }

        return array.elements();
    }

    public void updateErrorValues(String s, String s1) {
        String as[] = buildThresholdsArray();
        for (int i = 0; i < as.length && i < pValues.length; i++) {
            if (getProperty("value" + i).length() > 0) {
                setProperty(getLocationProperty(getPropertyObject("value" + i),
                        s), "n/a");
            }
        }

    }

    public String updateMatchValues(String s, String s1, String s2, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        String as[] = buildThresholdsArray();
        int i = TextUtils.matchExpression(s2, s1, array, stringbuffer);
        if (i != Monitor.kURLok) {
            String s3 = URLMonitor.getHTMLEncoding(s2);
            TextUtils.matchExpression(s2, I18N.UnicodeToString(s1, s3), array,
                    stringbuffer);
        }
        String s4 = "";
        for (int j = 0; j < nMaxCounters; j++) {
            if (array.size() <= j || nMaxCounters <= j || as.length <= j) {
                continue;
            }
            if (s4.length() > 0) {
                s4 = s4 + ", ";
            }
            if (getProperty("value" + j).length() > 0) {
                setProperty(getLocationProperty(getPropertyObject("value" + j),
                        s), array.at(j));
            }
            s4 = s4 + as[j] + " = " + array.at(j);
        }

        if (flag) {
            int k = getSettingAsLong("_urlContentMatchDisplayMax", 150);
            if (s4.length() > k) {
                s4 = s4.substring(0, k) + "...";
            }
        }
        return s4;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        for (int i = 0; i < nMaxCounters; i++) {
            if (getProperty("value" + i).length() > 0) {
                array.add(getPropertyObject("value" + i));
            }
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
        String as[] = buildThresholdsArray();
        StringProperty astringproperty[] = new StringProperty[as.length];
        for (int i = 0; i < as.length; i++) {
            if (getProperty("value1").length() > 0) {
                astringproperty[i] = getPropertyObject("value" + i);
            }
        }

        return astringproperty;
    }

    static {
        Array array = new Array();
        pValues = new StringProperty[nMaxCounters];
        for (int i = 0; i < nMaxCounters; i++) {
            pValues[i] = new NumericProperty("value" + i, "n/a");
            pValues[i].setDisplayText("Counter " + (i + 1) + " Value",
                    "URL Content Counter");
            pValues[i].setStateOptions(i + 1);
            array.add(pValues[i]);
        }

        array.add(pStateString);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int j = 0; j < array.size(); j++) {
            astringproperty[j] = (StringProperty) array.at(j);
        }

        addProperties("COM.dragonflow.SiteView.URLContentBase", astringproperty);
        setClassProperty("COM.dragonflow.SiteView.AtomicMonitor",
                "ApplicationType", "URLContent");
    }
}
