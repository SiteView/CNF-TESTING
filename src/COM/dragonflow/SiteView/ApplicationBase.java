/*
 * 
 * Created on 2005-2-15 10:44:16
 *
 * ApplicationBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ApplicationBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, ISelectableCounter, Platform, MasterConfig

public abstract class ApplicationBase extends AtomicMonitor implements
        ISelectableCounter {

    public ApplicationBase() {
    }

    public abstract Array getAvailableCounters();

    public abstract String getHostname();

    public abstract String getReturnURL();

    public abstract String getCountersContent();

    public abstract StringProperty getCountersProperty();

    public abstract void setCountersContent(String s);

    public abstract String[] buildThresholdsArray();

    public abstract HashMap getLabels();

    public abstract void clearCounterCache();

    public abstract StringProperty[] getValues();

    public abstract String getTemplateFile();

    public void increaseCounters(int i) {
        for (int j = nMaxCounters; j < i; j++) {
            StringProperty stringproperty = new StringProperty("value" + j);
            stringproperty.setDisplayText("Counter " + (j + 1) + " Value",
                    "The NT Performance Counter");
            stringproperty.setStateOptions(j + 1);
            PropertiedObject.addPropertyToPropertyMap(
                    "COM.dragonflow.SiteView.ApplicationBase", stringproperty);
        }

        nMaxCounters = i;
    }

    public static String getTemplatePath() {
        return path;
    }

    public String getDefaultCounters() {
        return getTemplateContent(getTemplatePath(), getTemplateFile(), false);
    }

    public String getCountersParameter(String s, String s1) {
        String s2 = "";
        Array array = getAvailableCounters();
        boolean flag = true;
        String as[] = TextUtils.split(s, ",");
        int i = 0;
        label0: for (int j = 0; j < as.length; j++) {
            String s3 = as[j].trim();
            if (i >= nMaxCounters)
                break;
            int k = 0;
            do {
                if (k >= array.size())
                    continue label0;
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

    public String getDisplayURLParameter(String s) {
        String s1 = "";
        String s2 = getCountersProperty().getDescription();
        int i = s2.indexOf("&" + s);
        if (i != -1) {
            int j = s2.substring(i).indexOf("=");
            int k = s2.substring(i + j).indexOf("&");
            if (k == -1)
                k = s2.substring(i + j).indexOf(">") - 1;
            s1 = s2.substring(i + j + 1, i + j + k);
        }
        return s1;
    }

    public String replaceDisplayPropertiesForURL(String s, String s1) {
        String s2 = getCountersProperty().getDescription();
        String s3 = "";
        boolean flag = false;
        int i = s2.indexOf("&" + s);
        if (s1.length() > 0)
            flag = true;
        if (i == -1) {
            i = s2.indexOf("<A HREF");
            i = (i += 7) + s2.substring(i).indexOf(">");
            s3 = s2.substring(0, i - 1);
            if (flag)
                s3 = s3 + "&" + s + "=" + s1;
            s3 = s3 + s2.substring(i - 1);
        } else {
            int j = s2.substring(i + 1).indexOf("&");
            if (j == -1)
                j = s2.substring(i + 1).indexOf(">") - 1;
            s3 = s2.substring(0, i);
            if (flag)
                s3 = s3 + "&" + s + "=" + s1;
            s3 = s3 + s2.substring(i + j + 1);
        }
        getCountersProperty().setDisplayText("Counters", s3);
        return s3;
    }

    public static String getTemplateContent(String s, String s1, boolean flag) {
        String s2 = "";
        try {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer = FileUtils.readFile(s + s1);
            String as[] = TextUtils.split(stringbuffer.toString(),
                    URLMonitor.CRLF);
            int i = 0;
            for (int j = 0; j < as.length && (flag || i < nMaxCounters); j++) {
                int k = as[j].indexOf(":Default");
                if (flag || k != -1) {
                    i++;
                    if (j > 0)
                        s2 = s2 + ",";
                    if (k != -1)
                        s2 = s2 + as[j].substring(0, k);
                    else
                        s2 = s2 + as[j];
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return s2;
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = stringproperty.getLabel();
        if (s.startsWith("Counter ") && s.endsWith(" Value"))
            return TextUtils.getValue(getLabels(), s).replace('+', ' ');
        else
            return s.replace('+', ' ');
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty);
    }

    public String getTopazCounterInternalName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        if (s.startsWith("value"))
            return s.substring("value".length());
        else
            return super.getTopazCounterInternalName(stringproperty);
    }

    public StringProperty getTopazValueProperty(String s) {
        StringProperty stringproperty;
        try {
            int i = Integer.parseInt(s);
            stringproperty = getValues()[i];
        } catch (NumberFormatException numberformatexception) {
            return super.getTopazValueProperty(s);
        }
        return stringproperty;
    }

    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        clearCounterCache();
    }

    public static int nMaxCounters;

    public static final String applicationDir = "templates.applications";

    public static final String path;

    static {
        nMaxCounters = 0;
        path = Platform.getRoot() + File.separator + "templates.applications"
                + File.separator;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_ApplicationMonitorMaxCounters"));
        if (nMaxCounters <= 0)
            nMaxCounters = 10;
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("COM.dragonflow.SiteView.ApplicationBase",
                astringproperty);
    }
}
