/*
 * 
 * Created on 2005-2-15 11:36:54
 *
 * BrowsableBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.BrowsableProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, BrowsableMonitor

public abstract class BrowsableBase extends AtomicMonitor implements
        BrowsableMonitor {

    public BrowsableBase() {
    }

    public static StringProperty[] staticInitializer(int i, boolean flag) {
        Object obj = null;
        ArrayList arraylist = new ArrayList();
        BrowsableProperty browsableproperty = new BrowsableProperty(
                PROPERTY_NAME_BROWSABLE, PROPERTY_VALUE_BROWSABLE);
        browsableproperty.setDisplayText("Counters",
                "Current selection of counters.");
        browsableproperty.setParameterOptions(true, false,
                COUNTER_PROPERTY_INDEX, false);
        arraylist.add(browsableproperty);
        arraylist.addAll(createNewBrowsableCounters(0, i));
        if (flag) {
            NumericProperty numericproperty = new NumericProperty(
                    "countersInError");
            numericproperty.setLabel("counters in error");
            numericproperty.setStateOptions(i + 1);
            numericproperty.setIsThreshold(true);
            arraylist.add(numericproperty);
        }
        StringProperty astringproperty[] = new StringProperty[arraylist.size()];
        for (int j = 0; j < arraylist.size(); j++)
            astringproperty[j] = (StringProperty) arraylist.get(j);

        return astringproperty;
    }

    public static List createNewBrowsableCounters(int i, int j) {
        ArrayList arraylist = new ArrayList((j - i) * 3);
        for (int k = i; k < j; k++) {
            arraylist.add(createBrowsableCounterNameProp(k));
            arraylist.add(createBrowsableCounterIdProp(k, i));
            arraylist.add(createBrowsableValueProp(k));
        }

        return arraylist;
    }

    private static StringProperty createBrowsableValueProp(int i) {
        NumericProperty numericproperty = new NumericProperty(
                PROPERTY_NAME_COUNTER_VALUE + (i + 1), "n/a");
        numericproperty.setStateOptions(i + 1);
        numericproperty.setIsThreshold(true);
        numericproperty.setOrder(i);
        numericproperty.setLabel(PROPERTY_NAME_COUNTER_VALUE + (i + 1));
        return numericproperty;
    }

    private static StringProperty createBrowsableCounterIdProp(int i, int j) {
        StringProperty stringproperty = new StringProperty(
                PROPERTY_NAME_COUNTER_ID + (i + 1));
        stringproperty.setParameterOptions(false, j + i + 4, false);
        stringproperty.setConfigurable(true);
        return stringproperty;
    }

    private static StringProperty createBrowsableCounterNameProp(int i) {
        StringProperty stringproperty = new StringProperty(
                PROPERTY_NAME_COUNTER_NAME + (i + 1));
        stringproperty.setParameterOptions(false, i + 4, false);
        stringproperty.setConfigurable(true);
        return stringproperty;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        if (stringproperty == null)
            return null;
        if (stringproperty.getName().startsWith(PROPERTY_NAME_COUNTER_VALUE)) {
            int i = TextUtils.toInt(stringproperty.getName().substring(
                    PROPERTY_NAME_COUNTER_VALUE.length()));
            return getProperty(PROPERTY_NAME_COUNTER_NAME + i);
        } else {
            return stringproperty.printString();
        }
    }

    public String getPropertyName(StringProperty stringproperty) {
        if (stringproperty.getName().startsWith(PROPERTY_NAME_COUNTER_VALUE)) {
            int i = TextUtils.toInt(stringproperty.getName().substring(
                    PROPERTY_NAME_COUNTER_VALUE.length()));
            return getProperty(PROPERTY_NAME_COUNTER_NAME + i);
        } else {
            return super.getPropertyName(stringproperty);
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        StringProperty stringproperty = getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR);
        if (stringproperty != null)
            array.add(stringproperty);
        for (int i = 0; i < getMaxCounters()
                && getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1)).length() > 0; i++)
            array.add(getPropertyObject(PROPERTY_NAME_COUNTER_VALUE + (i + 1)));

        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        StringProperty stringproperty = getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR);
        if (stringproperty != null)
            array.add(stringproperty);
        for (int i = 0; i < getMaxCounters()
                && getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1)).length() > 0; i++)
            array.add(getPropertyObject(PROPERTY_NAME_COUNTER_VALUE + (i + 1)));

        return array.elements();
    }

    public String getBrowseName() {
        return PROPERTY_NAME_COUNTER_NAME;
    }

    public String getBrowseID() {
        return PROPERTY_NAME_COUNTER_ID;
    }

    public String setBrowseName(Array array) {
        String s = "";
        if (array.size() <= 0)
            return s;
        for (int i = array.size() - 1; i >= 0; i--) {
            if (s.length() > 0)
                s = s + "/";
            s = s + (String) array.at(i);
        }

        return s;
    }

    public String setBrowseID(Array array) {
        String s = "";
        if (array.size() <= 0)
            return s;
        for (int i = array.size() - 1; i >= 0; i--) {
            String s1 = (String) array.at(i);
            s = s + s1.length() + " " + s1;
        }

        return s;
    }

    public boolean isServerBased() {
        return true;
    }

    public boolean isUsingCountersCache() {
        return true;
    }

    public int getCostInLicensePoints() {
        int i = 0;
        for (int j = 0; j < getMaxCounters()
                && getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1)).length() > 0; j++)
            i++;

        return i * 1;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0)
                hashmap.put(stringproperty, "No counters selected");
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public boolean isSortedCounters() {
        return false;
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty, true);
    }

    public Enumeration getConfigurationAddProperties(Vector vector,
            boolean flag, int i) {
        Enumeration enumeration = super.getConfigurationAddProperties(vector,
                flag, i);
        Array array = getConnectionProperties();
        StringProperty stringproperty;
        for (; enumeration.hasMoreElements(); array.add(stringproperty))
            stringproperty = (StringProperty) enumeration.nextElement();

        return array.elements();
    }

    public Enumeration getConfigurationEditProperties(Vector vector,
            boolean flag, int i) {
        Enumeration enumeration = super.getConfigurationEditProperties(vector,
                flag, i);
        Array array = getConnectionProperties();
        StringProperty stringproperty;
        for (; enumeration.hasMoreElements(); array.add(stringproperty))
            stringproperty = (StringProperty) enumeration.nextElement();

        return array.elements();
    }

    public boolean manageBrowsableSelectionsByID() {
        return false;
    }

    public boolean areBrowseIDsEqual(String s, String s1) {
        if (s == null || s1 == null)
            return false;
        else
            return s.equals(s1);
    }

    public static String PROPERTY_NAME_COUNTER_NAME = "_browseName";

    public static String PROPERTY_NAME_COUNTER_ID = "_browseNameid";

    public static String PROPERTY_NAME_COUNTER_VALUE = "browsableValue";

    public static String PROPERTY_NAME_COUNTERS_IN_ERROR = "countersInError";

    public static String PROPERTY_NAME_BROWSABLE = "_browse";

    public static String PROPERTY_VALUE_BROWSABLE = "browseName";

    public static int COUNTER_PROPERTY_INDEX = 100;

    static {
        addProperties((COM.dragonflow.SiteView.BrowsableBase.class).getName(),
                new StringProperty[0]);
    }
}
