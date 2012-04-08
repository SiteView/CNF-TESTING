/*
 * 
 * Created on 2005-2-28 7:07:53
 *
 * StringProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>StringProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import jgl.Sorting;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Properties:
// FileProperty, GreaterEqualOrder, PropertiedObject

public class StringProperty {

	String testString="ÄãºÃ!";
	
    String encoding;

    String name;

    String defaultValue;

    String label;

    String description;

    String value;

    public boolean isParameter;

    public boolean isEditable;

    public boolean isConfigurable;

    public boolean isAdvanced;

    public boolean isMultiLine;

    public boolean isPassword;

    public boolean isStateProperty;

    public boolean isTopazLogProperty;

    public boolean isPrimaryStateProperty;

    public static final String DEFAULT_PASSWORD = "*********";

    boolean useDefaultThreshold;

    boolean isThresholdProperty;

    public int order;

    public String multiLineDelimiter;

    String measID;

    String threshID;

    String monitorID;

    String targetID;

    String schedID;

    String categoryID;

    String connectionID;

    public boolean isMultivalued;

    public boolean isOptional;

    public boolean isVariablePropertyCountKey;

    public boolean isVariablePropertyCountDependent;

    public int minVariablePropertyCount;

    public int maxVariablePropertyCount;

    public StringProperty similarProperty;

    public float displayMaximum;

    boolean platforms[];

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static int toInteger(String s) {
        try {
            return Integer.valueOf(s).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static float toFloat(String s) {
        try {
            if (s.equals("n/a")) {
                return (0.0F / 0.0F);
            }
            return Float.valueOf(s).floatValue();
        } catch (NumberFormatException e) {
            return 0.0F;
        }

    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static long toLong(String s) {
        try {
            return Long.valueOf(s).longValue();
        } catch (NumberFormatException e) {
            return 0L;
        }

    }

    public void setEncoding(String s) {
        encoding = s;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String s) {
        value = s;
    }

    public StringProperty(String s) {
        encoding = I18N.getDefaultEncoding();
        defaultValue = "";
        description = "no description";
        value = null;
        isParameter = false;
        isEditable = false;
        isConfigurable = false;
        isAdvanced = false;
        isMultiLine = false;
        isPassword = false;
        isStateProperty = false;
        isTopazLogProperty = true;
        isPrimaryStateProperty = false;
        useDefaultThreshold = true;
        isThresholdProperty = true;
        order = 0;
        multiLineDelimiter = ",";
        measID = "-1";
        threshID = "-1";
        monitorID = "-1";
        targetID = "-1";
        schedID = "-1";
        categoryID = "-1";
        connectionID = "-1";
        isMultivalued = false;
        isOptional = false;
        isVariablePropertyCountKey = false;
        isVariablePropertyCountDependent = false;
        minVariablePropertyCount = 0;
        maxVariablePropertyCount = 0;
        similarProperty = null;
        displayMaximum = 0.0F;
        platforms = new boolean[8];
        name = s;
        label = s;
        for (int i = 0; i < platforms.length; i ++) {
            platforms[i] = true;
        }

        if (s.startsWith("_")) {
            isParameter = true;
        } else {
            isStateProperty = true;
        }
    }

    public String getUniqueNegID(HashMap hashmap) {
        for (int i = -1; i > -1000; i --) {
            String s = String.valueOf(i);
            if (hashmap.get(s) == null) {
                return s;
            }
        }

        return "-1";
    }

    public StringProperty(String s, String s1) {
        this(s);
        defaultValue = s1;
    }

    public StringProperty(String s, String s1, String s2) {
        this(s, s1);
        label = s2;
    }

    public boolean isThreshold() {
        if (!useDefaultThreshold) {
            return isThresholdProperty;
        } else {
            return false;
        }
    }

    public boolean isVariableCountProperty() {
        return isVariablePropertyCountKey || isVariablePropertyCountDependent;
    }

    public boolean shouldPrintVariableCountProperty(int i) {
        int j = TextUtils.readIntegerFromEnd(getName());
        return j != -1 && j <= i;
    }

    void printBytes(PrintWriter printwriter, String s) {
        printwriter.print(s);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param cgi
     * @param printwriter
     * @param siteviewobject
     * @param httprequest
     * @param hashmap
     * @param flag
     */
    public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, HashMap hashmap, boolean flag) {
        Object obj = hashmap.get(this);
        String s = "";
        if (encoding == null || encoding.length() == 0) {
            encoding = I18N.getDefaultEncoding();
        }
        if (obj != null) {
            s = (String) obj;
        }
        if (isMultiLine) {
            printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>" + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><TEXTAREA name=" + getName() + " rows=4 cols=50>");
            Enumeration enumeration = siteviewobject.getMultipleValues(this);
            if (enumeration.hasMoreElements()) {
                while (enumeration.hasMoreElements()) {
                    String s2 = (String) enumeration.nextElement();
                    if (s2.length() > 0) {
                        printwriter.println(s2);
                    }
                }
            } else {
                String as[] = TextUtils.split(getDefault(), multiLineDelimiter);
                for (int i = 0; i < as.length; i ++) {
                    printwriter.println(as[i]);
                }

            }
            printwriter.println("</TEXTAREA></TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>");
        } else {
            String s1 = "text";
            String s3 = siteviewobject.getProperty(this);
            String s4 = I18N.escapeString(s3, encoding);
            if (isPassword) {
                s1 = "password";
                s4 = s3.length() <= 0 ? "" : new String("*********");
            }
            boolean flag1 = true;
            if (siteviewobject instanceof AtomicMonitor) {
                flag1 = ((AtomicMonitor) siteviewobject).isEditableProperty(this, httprequest);
            }
            String s5 = "<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>" + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><input type=" + s1 + " name=" + getName();
            if (flag1) {
                s5 = s5 + " size=50 value=\"";
            } else {
                s5 = s5 + " disabled size=" + s3.length() + " value=\"";
            }
            printwriter.print(s5);
            printBytes(printwriter, s4);
            s5 = "\">";
            if (isPassword && s3.length() > 0) {
                s5 = s5 + "<input type=hidden name=hidden" + getName() + " value=\"" + TextUtils.obscure(s3) + "\">";
            }
            boolean flag2 = false;
            if (this instanceof FileProperty) {
                flag2 = ((FileProperty) this).isBrowse();
            }
            if (flag1 && flag2) {
                s5 = s5 + "<input type=button id=browseButton value=Browse... onclick=fileElem.click()><input type=file id=fileElem style=\"visibility:hidden\" onpropertychange=document.getElementsByName(\"" + getName()
                        + "\").item(0).value=document.getElementById(\"fileElem\").value>" + "<script> if (navigator.appName != \"Microsoft Internet Explorer\")" + " document.getElementById(\"browseButton\").style.visibility=\"hidden\"; </script>";
            }
            s5 = s5 + "</TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>";
            if (!flag1) {
                s5 = s5 + "<input type=hidden name=\"" + getName() + "\" value=\"" + s3 + "\">\n";
            }
            printwriter.println(s5);
        }
    }

    public static String getPrivate(HTTPRequest httprequest, String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1) {
        String s2 = httprequest.getValue(s);
        if (s2.equals("*********")) {
            s2 = TextUtils.enlighten(httprequest.getValue(s1));
        } else {
            s2 = TextUtils.enlighten(s2);
        }
        if (stringbuffer != null) {
            stringbuffer.append("<input type=password name=" + s + " value=\"" + (s2.length() <= 0 ? "" : "*********") + "\"");
        }
        if (stringbuffer1 != null) {
            stringbuffer1.append("<input type=hidden name=" + s1 + " value=\"" + TextUtils.obscure(s2) + "\">");
        }
        return s2;
    }

    public static String getPrivate(Monitor monitor, String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1) {
        String s2 = monitor.getValue(s);
        s2 = TextUtils.enlighten(s2);
        if (stringbuffer != null) {
            stringbuffer.append("<input type=password name=" + s + " value=\"" + (s2.length() <= 0 ? "" : "*********") + "\"");
        }
        if (stringbuffer1 != null) {
            stringbuffer1.append("<input type=hidden name=" + s1 + " value=\"" + TextUtils.obscure(s2) + "\">");
        }
        return s2;
    }

    public static String getPrivate(String s, String s1, String s2, StringBuffer stringbuffer, StringBuffer stringbuffer1) {
        s = TextUtils.enlighten(s);
        if (stringbuffer != null) {
            stringbuffer.append("<input type=password name=" + s1 + " value=\"" + (s.length() <= 0 ? "" : "*********") + "\"");
        }
        if (stringbuffer1 != null) {
            stringbuffer1.append("<input type=hidden name=" + s2 + " value=\"" + TextUtils.obscure(s) + "\">");
        }
        return s;
    }

    public void setIsParameter(boolean flag) {
        isParameter = flag;
        if (flag) {
            isStateProperty = false;
        }
    }

    public void setIsStateProperty(boolean flag) {
        isStateProperty = flag;
        if (flag) {
            isParameter = false;
        }
    }

    public void setLabel(String s) {
        label = s;
    }

    public void setDescription(String s) {
        description = s;
    }

    public void setDisplayText(String s, String s1) {
        setLabel(s);
        setDescription(s1);
    }

    public void setEditable(boolean flag) {
        isEditable = flag;
    }

    public void setConfigurable(boolean flag) {
        isConfigurable = flag;
    }

    public void setIsThreshold(boolean flag) {
        isThresholdProperty = flag;
        useDefaultThreshold = false;
    }

    public void setOrder(int i) {
        order = i;
    }

    public void setMonitorID(String s) {
        monitorID = s;
    }

    public void setMeasID(String s) {
        measID = s;
    }

    public void setSchedID(String s) {
        schedID = s;
    }

    public void setTargetID(String s) {
        targetID = s;
    }

    public void setThreshID(String s) {
        threshID = s;
    }

    public void setCategoryID(String s) {
        categoryID = s;
    }

    public void setConnectionID(String s) {
        connectionID = s;
    }

    public void setParameterOptions(boolean flag, boolean flag1, int i, boolean flag2) {
        setConfigurable(flag1);
        setEditable(flag);
        setOrder(i);
        isAdvanced = flag2;
    }

    public void setParameterOptions(boolean flag, int i, boolean flag1) {
        setConfigurable(flag);
        setEditable(flag);
        setOrder(i);
        isAdvanced = flag1;
    }

    public void setStateOptions(int i) {
        setOrder(i);
        if (i == 1) {
            isPrimaryStateProperty = true;
        }
    }

    public String verify(String s) {
        return s;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defaultValue;
    }

    public int getOrder() {
        return order;
    }

    public String getMeasID() {
        return measID;
    }

    public String getConnectionID() {
        return connectionID;
    }

    public String getThreshID() {
        return threshID;
    }

    public String getSchedID() {
        return schedID;
    }

    public String getMonitorID() {
        return monitorID;
    }

    public String getTargetID() {
        return targetID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String printString(String s) {
        return valueString(s);
    }

    public String printString(String s, String s1) {
        return valueString(s, s1);
    }

    public String printString() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public String getFullLabel() {
        return getLabel();
    }

    public String getDescription() {
        return description;
    }

    public String valueString(String s) {
        return s;
    }

    public String valueOnlyString(String s) {
        return valueString(s);
    }

    public String valueString(String s, int i) {
        return valueString(s);
    }

    public String valueOnlyString(String s, int i) {
        return valueString(s);
    }

    public String valueString(float f) {
        if (Float.isNaN(f)) {
            return "n/a";
        } else {
            return valueString(String.valueOf(f));
        }
    }

    public String valueString(long l) {
        return valueString(String.valueOf(l));
    }

    public String valueString(int i) {
        return valueString(String.valueOf(i));
    }

    public String valueString(float f, int i) {
        if (Float.isNaN(f)) {
            return "n/a";
        } else {
            return valueString(String.valueOf(f), i);
        }
    }

    public String valueString(long l, int i) {
        return valueString(String.valueOf(l), i);
    }

    public String valueString(int i, int j) {
        return valueString(String.valueOf(i), j);
    }

    public String valueOnlyString(float f) {
        if (Float.isNaN(f)) {
            return "n/a";
        } else {
            return valueOnlyString(String.valueOf(f));
        }
    }

    public String displayValueToNativeValue(String s) {
        return s;
    }

    public String valueString(String s, String s1) {
        return s;
    }

    public String valueFormattedString(float f) {
        if (Float.isNaN(f)) {
            return "n/a";
        } else {
            return valueFormattedString(String.valueOf(f));
        }
    }

    public String valueFormattedString(String s) {
        return s;
    }

    public void increment(PropertiedObject propertiedobject) {
    }

    public boolean isSimilarTo(StringProperty stringproperty) {
        if (stringproperty == this) {
            return true;
        }
        return (similarProperty != null || stringproperty.similarProperty != null) && (stringproperty == similarProperty || stringproperty.similarProperty == this || stringproperty.similarProperty == similarProperty);
    }

    public float getDisplayMaximum(SiteViewObject siteviewobject) {
        return getDisplayMaximum(siteviewobject, "");
    }

    public float getDisplayMaximum(SiteViewObject siteviewobject, String s) {
        float f = 0.0F;
        if (s.length() > 0) {
            f = TextUtils.toFloat(s);
        }
        if (f <= 0.0F) {
            String s1 = siteviewobject.getClassPropertyString("class");
            if (s1.length() > 0) {
                String s2 = siteviewobject.getSetting("_max" + s1 + "_" + getName());
                if (s2.length() > 0) {
                    f = TextUtils.toFloat(s2);
                }
            }
        }
        if (f <= 0.0F) {
            f = displayMaximum;
        }
        return f;
    }

    public static long closestPowerOfTen(float f) {
        long l;
        for (l = 1L; f >= (float) l; l *= 10L) {
        }
        return l;
    }

    public static Array sortByOrder(Array array) {
        Sorting.sort(array, new GreaterEqualOrder(true));
        return array;
    }

    public boolean onPlatform(int i) {
        return platforms[i - 1];
    }

    public void setPlatform(int i) {
        for (int j = 0; j < platforms.length; j ++) {
            platforms[j] = i - 1 == j;
        }

    }

    public void addPlatform(int i) {
        platforms[i - 1] = true;
    }

    public void setWindowsPlatforms() {
        for (int i = 1; i <= 8; i ++) {
            platforms[i - 1] = Platform.isWindows(i);
        }

    }

    public void setUnixPlatforms() {
        for (int i = 1; i <= 8; i ++) {
            platforms[i - 1] = Platform.isUnix(i);
        }

    }
}