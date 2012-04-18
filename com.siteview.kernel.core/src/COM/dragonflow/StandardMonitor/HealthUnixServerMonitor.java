/*
 * 
 * Created on 2005-3-7 1:21:56
 *
 * HealthUnixServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>HealthUnixServerMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.MultiContentBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.TextUtils;

public class HealthUnixServerMonitor extends MultiContentBase {

    static StringProperty pCounters;

    protected static StringProperty pStatus;

    protected static StringProperty pHealthValues[];

    private static Array scriptOut = new Array();

    static String returnURL;

    public static int nMaxCounters;

    public HealthUnixServerMonitor() {
    }

    protected StringProperty getStateValue(int i) {
        return pHealthValues[i];
    }

    protected StringProperty[] getStateValues() {
        return pHealthValues;
    }

    protected String getVal(String s, Array array) {
        for (int i = 0; i < array.size(); i ++) {
            String s1 = (String) array.get(i);
            if (s1 == null || s1.length() == 0) {
                continue;
            }
            String as[] = TextUtils.split(s1, "=");
            if (as == null || as.length != 2 || !s.equals(as[0])) {
                continue;
            }
            int j;
            for (j = as[1].length(); j > 0 && !Character.isDigit(as[1].charAt(-- j));) {
            }
            return as[1].substring(0, j + 1);
        }

        return "";
    }

    /**
     * 
     */
    protected boolean update() {
        try {
            for (int i = 0; i < pHealthValues.length; i ++) {
                unsetProperty(pHealthValues[i]);
            }

            String as[] = buildThresholdsArray();
            StringBuffer stringbuffer = new StringBuffer(as.length * 50);
            Array array = runScript();
            for (int j = 0; j < as.length; j ++) {
                String s = getVal(as[j], array);
                setProperty(pHealthValues[j], s);
                pHealthValues[j].setLabel(as[j]);
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

    public synchronized HashMap getLabels() {
        HashMap hashmap = new HashMap();
        Array array = getAvailableCounters();
        for (int i = 0; i < array.size(); i ++) {
            String s = (String) array.get(i);
            String s1 = s;
            int j = s.indexOf(":");
            if (j >= 0) {
                s1 = s.substring(0, j);
            }
            hashmap.add("Counter " + (i + 1) + " Value", s1);
        }

        return hashmap;
    }

    public static String getAllCounterContent() {
        return getLabels(getScriptCounters());
    }

    public String getContentCounters(boolean flag) {
        if (flag) {
            return getLabels(getScriptCounters());
        } else {
            return getProperty(pCounters);
        }
    }

    private static synchronized Array runScript() {
        String s = Platform.getRoot() + "/templates.health/health_siteview_server.sh";
        if (Platform.isWindows(Platform.getOs())) {
            s = Platform.getRoot() + "/templates.health/uberscript.bat";
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s, "", Platform.monitorLock, -1);
        return array;
    }

    private static String getLabels(Array array) {
        if (array == null) {
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < array.size(); i ++) {
            String s = (String) array.get(i);
            if (s != null && s.length() != 0) {
                String as[] = TextUtils.split((String) array.get(i), "=");
                stringbuffer.append((stringbuffer.length() <= 0 ? "" : ",") + as[0]);
            }
        }

        return stringbuffer.toString();
    }

    public Array getAvailableCounters() {
        String s = getLabels(getScriptCounters());
        String as[] = TextUtils.split(s, ",");
        Array array = new Array();
        for (int i = 0; i < as.length; i ++) {
            array.add(as[i]);
        }

        return array;
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

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public int getCostInLicensePoints() {
        return 0;
    }

    private static Array getScriptCounters() {
        if (scriptOut.size() == 0) {
            scriptOut = runScript();
        }
        return scriptOut;
    }

    static {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=HealthUnixServerMonitor";
        nMaxCounters = 30;
        String s = (COM.dragonflow.StandardMonitor.HealthUnixServerMonitor.class).getName();
        pCounters = new StringProperty("_counters", "", "Monitor Load Selected Counters");
        try {
            pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL, "UTF-8") + "&maxcounters=" + nMaxCounters
                    + "&type=DynamicHealth\">choose counters</A>");
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            LogManager.log("error", "Failed to encode URL in HealthUnixServerMonitor static initializer: " + unsupportedencodingexception);
        }
        pCounters.setParameterOptions(true, 1, false);
        pCounters.isMultiLine = true;
        pStatus = new StringProperty("status", "n/a");
        StringProperty astringproperty[] = new StringProperty[nMaxCounters + 2];
        astringproperty[0] = pStatus;
        astringproperty[1] = pCounters;
        pHealthValues = staticInitializer(nMaxCounters);
        for (int i = 0; i < nMaxCounters; i ++) {
            astringproperty[i + 2] = pHealthValues[i];
        }

        addProperties(s, astringproperty);
        setClassProperty(s, "class", "HealthUnixServerMonitor");
        setClassProperty(s, "description", "Monitors the health of the SiteView server");
        setClassProperty(s, "title", "Health of Unix Server");
        setClassProperty(s, "help", "HealthUnixServerMonitor.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Health Server Load Monitor");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "applicationType", "DynamicHealth");
        setClassProperty(s, "addable", "false");
        addClassElement(s, Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement(s, Rule.stringToClassifier("status == 'ok'\tgood"));
    }
}