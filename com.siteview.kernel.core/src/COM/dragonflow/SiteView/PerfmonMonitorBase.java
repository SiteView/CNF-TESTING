/*
 * 
 * Created on 2005-2-20 5:30:57
 *
 * PerfmonMonitorBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PerfmonMonitorBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.perfmonPage;
import COM.dragonflow.Properties.CounterValueXMLProperty;
import COM.dragonflow.Properties.CounterXMLProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PerfmonProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Properties.XMLProperty;
import COM.dragonflow.Utils.ArgsPackagerUtil;
import COM.dragonflow.Utils.StringPropertyUtil;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, BrowsableCache

public abstract class PerfmonMonitorBase extends AtomicMonitor {

    static final String PERFMON_PROP_NAME = "_perfmonProp";

    public static final String MSMT_PROP_PREFIX = "_perfmonMsmtProp";

    public static final String MSMT_PROPS_PROP_NAME = "_perfmonMsmtsProp";

    public static final String VALUE_PROP_PREFIX = "perfmonValueProp";

    public static final String VALUE_PROPS_PROP_NAME = "perfmonValuesProp";

    public static int PERFMON_PROPERTY_INDEX;

    static PerfmonProperty pPerfmon;

    static String XMLTAG_COUNTERINFO_NAME = "CounterInfo";

    static String XMLTAG_COUNTER_NAME = "Counters";

    static String XMLTAG_ATTRIBUTE_OBJECT_NAME = "Object";

    static String XMLTAG_ATTRIBUTE_INSTANCE_NAME = "Instance";

    public static CounterXMLProperty pMsmtProps;

    public static CounterValueXMLProperty pValueProps;

    public static StringProperty pCountersInError;

    public static String KEY_RETURN_URL = "perfmonReturnUrl";

    StringProperty measurements[];

    StringProperty values[];

    boolean valuesIsInitialized;

    Object valuesLock;

    String cacheId;

    String returnUrl;

    public PerfmonMonitorBase() {
        measurements = new StringProperty[0];
        values = new StringProperty[0];
        valuesIsInitialized = false;
        valuesLock = new Object();
        cacheId = "";
        returnUrl = "";
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        String s = getProperty(pMsmtProps);
        measurements = createMeasurements(s);
        setProperty(pMsmtProps, CounterXMLProperty.encodeDocument(measurements));
    }

    public static StringProperty[] createMeasurements(String s) {
        Document document = XMLProperty.parseString(s);
        StringProperty astringproperty[];
        if (document == null) {
            astringproperty = StringPropertyUtil.loadProperties(s);
        } else {
            astringproperty = CounterXMLProperty.decodeDocument(
                    "_perfmonMsmtProp", document);
        }
        return astringproperty;
    }

    public void updateValues() {
        synchronized (valuesLock) {
            values = new StringProperty[measurements.length];
            for (int i = 0; i < measurements.length; i++) {
                NumericProperty numericproperty = new NumericProperty(
                        "perfmonValueProp" + i);
                numericproperty.setValue(measurements[i].getValue());
                values[i] = numericproperty;
            }

            setProperty(pValueProps, CounterValueXMLProperty
                    .encodeDocument(values));
            valuesIsInitialized = true;
        }
    }

    public abstract boolean getAvailableObjects(Object aobj[],
            StringBuffer stringbuffer);

    public abstract boolean getAvailableInstances(String s, Object aobj[],
            StringBuffer stringbuffer);

    public abstract boolean getAvailableCounters(String s, Object aobj[],
            StringBuffer stringbuffer);

    public StringProperty[] getPerfmonMeasurements() {
        return measurements;
    }

    public StringProperty[] getPerfmonValues() {
        return values;
    }

    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        super.onMonitorCreateFromPage(httprequest);
        loadCache(httprequest);
        String s = httprequest.getValue("perfmonPageOp");
        if (s.equals("saveMsmts")) {
            loadNewMeasurements(httprequest);
        } else if (s.equals("selectMsmts")) {
            Array array = getPropertiesToPassBetweenPages(httprequest);
            loadPropsFromRequest(httprequest, array);
        } else if (s == null || s.length() == 0) {
            returnUrl = httprequest.rawURL;
        }
        saveToCache(httprequest);
    }

    private void loadCache(HTTPRequest httprequest) {
        cacheId = perfmonPage.getPerfmonUniqueIdFromRequest(httprequest);
        if (cacheId != null && cacheId.length() > 0) {
            HashMap hashmap = BrowsableCache.getCache(cacheId, false, false);
            HashMap hashmap1 = null;
            if (hashmap != null) {
                hashmap1 = (HashMap) hashmap.get("mProp");
            }
            if (hashmap1 != null) {
                String s = (String) hashmap1.get(pMsmtProps.getName());
                if (s != null && s != "") {
                    setProperty(pMsmtProps, s);
                    Document document = getPropertyAsDocument(pMsmtProps);
                    measurements = CounterXMLProperty.decodeDocument(
                            "_perfmonMsmtProp", document);
                }
                returnUrl = (String) hashmap1.get(KEY_RETURN_URL);
                Array array = getPropertiesToPassBetweenPages(httprequest);
                for (int i = 0; array != null && i < array.size(); i++) {
                    StringProperty stringproperty = (StringProperty) array
                            .at(i);
                    String s1 = (String) hashmap1.get(stringproperty.getName());
                    if (s1 != null) {
                        setProperty(stringproperty, (String) hashmap1
                                .get(stringproperty.getName()));
                    }
                }

                return;
            }
        }
        cacheId = BrowsableCache.getUniqueID();
    }

    private void saveToCache(HTTPRequest httprequest) {
        HashMap hashmap = BrowsableCache.getCache(cacheId, true, false);
        HashMap hashmap1 = (HashMap) hashmap.get("mProp");
        hashmap1.put(pMsmtProps.getName(), getProperty(pMsmtProps));
        hashmap1.put(KEY_RETURN_URL, returnUrl);
        Array array = getPropertiesToPassBetweenPages(httprequest);
        for (int i = 0; array != null && i < array.size(); i++) {
            StringProperty stringproperty = (StringProperty) array.at(i);
            hashmap1.put(stringproperty.getName(), getProperty(stringproperty));
        }

        BrowsableCache.saveCache(cacheId);
    }

    void loadNewMeasurements(HTTPRequest httprequest) {
        measurements = perfmonPage.getNewMeasurementsFromRequest(httprequest);
        setProperty(pMsmtProps, CounterXMLProperty.encodeDocument(measurements));
        values = new StringProperty[measurements.length];
        for (int i = 0; i < measurements.length; i++) {
            NumericProperty numericproperty = new NumericProperty(
                    "perfmonValueProp" + i);
            numericproperty.setValue("");
            values[i] = numericproperty;
        }

        setProperty(pValueProps, CounterValueXMLProperty.encodeDocument(values));
    }

    public int getCostInLicensePoints() {
        int i = 0;
        if (measurements != null) {
            Document document = CounterXMLProperty.encodeDocument(measurements);
            NodeList nodelist = document.getChildNodes();
            java.util.HashMap hashmap = new java.util.HashMap();
            findInstances(nodelist, hashmap);
            i = hashmap.size();
        }
        return i;
    }

    public void findInstances(NodeList nodelist, java.util.HashMap hashmap) {
        label0: for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (XMLTAG_COUNTERINFO_NAME.equals(node.getNodeName())) {
                NamedNodeMap namednodemap = node.getAttributes();
                int j = 0;
                do {
                    if (j >= namednodemap.getLength()) {
                        continue label0;
                    }
                    Node node1 = namednodemap.item(j);
                    if (XMLTAG_ATTRIBUTE_INSTANCE_NAME.equals(node1
                            .getNodeName())) {
                        String s = node1.getNodeValue();
                        if (s.equals("")) {
                            Node node2 = namednodemap
                                    .getNamedItem(XMLTAG_ATTRIBUTE_OBJECT_NAME);
                            hashmap.put(node2.getNodeName(), "");
                        } else {
                            hashmap.put(s, "");
                        }
                    }
                    j++;
                } while (true);
            }
            if (XMLTAG_COUNTER_NAME.equals(node.getNodeName())) {
                findInstances(node.getChildNodes(), hashmap);
            }
        }

    }

    public String getPerfmonUniqueId() {
        return cacheId;
    }

    public void setReturnUrl(HTTPRequest httprequest) {
        returnUrl = httprequest.rawURL;
        saveToCache(httprequest);
    }

    public java.util.HashMap getRequiredRequestCreationProps() {
        java.util.HashMap hashmap = new java.util.HashMap();
        hashmap.put("perfmonCacheId", getPerfmonUniqueId());
        String s = getClass().getName();
        String s1 = s.substring(s.lastIndexOf(".") + 1);
        hashmap.put("perfmonClass", s1);
        return hashmap;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (isPerfmonMsmtProp(stringproperty.getName())
                || isPerfmonValueProp(stringproperty.getName())) {
            initializeValues();
            String s = stringproperty.getValue();
            if (s == null) {
                s = "";
            }
            return s;
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public String getProperty(String s) throws NullPointerException {
        if (isPerfmonMsmtProp(s)) {
            initializeValues();
            int i = TextUtils.toInt(s.substring("_perfmonMsmtProp".length()));
            return measurements[i].getValue();
        }
        if (isPerfmonValueProp(s)) {
            initializeValues();
            int j = TextUtils.toInt(s.substring("perfmonValueProp".length()));
            return values[j].getValue();
        } else {
            return super.getProperty(s);
        }
    }

    void initializeValues() {
        if (!valuesIsInitialized) {
            synchronized (valuesLock) {
                if (!valuesIsInitialized) {
                    String s = getProperty(pValueProps);
                    if (s == null || s == "") {
                        updateValues();
                    } else {
                        Document document = getPropertyAsDocument(pValueProps);
                        values = CounterValueXMLProperty.decodeDocument(
                                "perfmonValueProp", document);
                        for (int i = 0; i < measurements.length; i++) {
                            measurements[i].setValue(values[i].getValue());
                        }

                    }
                    valuesIsInitialized = true;
                }
            }
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pCountersInError);
        for (int i = 0; i < measurements.length; i++) {
            array.add(measurements[i]);
        }

        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Vector vector = new Vector();
        vector.add(pCountersInError);
        for (int i = 0; i < measurements.length; i++) {
            vector.add(measurements[i]);
        }

        return vector.elements();
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        if (isPerfmonMsmtProp(stringproperty.getName())) {
            return getMsmtLabel(stringproperty);
        } else {
            return super.GetPropertyLabel(stringproperty, flag);
        }
    }

    private boolean isPerfmonMsmtProp(String s) {
        return s.startsWith("_perfmonMsmtProp");
    }

    private boolean isPerfmonValueProp(String s) {
        return s.startsWith("perfmonValueProp");
    }

    public static String getMsmtLabel(StringProperty stringproperty) {
        String as[] = ArgsPackagerUtil.unpackageArgsToStrArray(stringproperty
                .getLabel());
        String s = "";
        if (as[1] != null && as[1].length() > 0) {
            s = as[1] + "\\";
        }
        return as[0] + "\\" + s + as[2];
    }

    public StringProperty getPropertyObject(String s) {
        if (isPerfmonMsmtProp(s)) {
            int i = TextUtils.toInt(s.substring("_perfmonMsmtProp".length()));
            return measurements[i];
        } else {
            return super.getPropertyObject(s);
        }
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty);
    }

    static {
        PERFMON_PROPERTY_INDEX = 100;
        pMsmtProps = null;
        pValueProps = null;
        pCountersInError = null;
        String s = (COM.dragonflow.SiteView.PerfmonMonitorBase.class).getName();
        pPerfmon = new PerfmonProperty("_perfmonProp");
        pPerfmon.setParameterOptions(true, PERFMON_PROPERTY_INDEX, false);
        pMsmtProps = new CounterXMLProperty("_perfmonMsmtsProp");
        pMsmtProps.setParameterOptions(false, true, 1, false);
        pValueProps = new CounterValueXMLProperty("perfmonValuesProp");
        pCountersInError = new NumericProperty("countersInError");
        pCountersInError.setLabel("counters in error");
        pCountersInError.setIsThreshold(true);
        addProperties(s, new StringProperty[] { pPerfmon, pMsmtProps,
                pValueProps, pCountersInError });
    }
}
