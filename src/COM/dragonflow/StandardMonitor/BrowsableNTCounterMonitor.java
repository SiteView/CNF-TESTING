/*
 * 
 * Created on 2005-3-5 14:22:31
 *
 * BrowsableNTCounterMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>BrowsableNTCounterMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jgl.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.JdbcLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.IServerPropMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.NTCounterBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Expression;
import COM.dragonflow.Utils.PerfCounter;
import COM.dragonflow.Utils.PropertyNameLiteral;
import COM.dragonflow.Utils.SimpleExpression;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// NTCounterMonitor

public class BrowsableNTCounterMonitor extends BrowsableBase implements IServerPropMonitor {

    static ServerProperty pHost;

    static ScalarProperty pFile;

    private static StringProperty pValues[];

    private static StringProperty pMeasurements[];

    private static StringProperty pLastMeasurements[];

    private static StringProperty pLastBaseMeasurements[];

    private static StringProperty pLastMeasurementTime;

    private static StringProperty pLastMeasurementTicks;

    private static StringProperty pLastMeasurementWasNA;

    private static Array mConnectionProps;

    static final String xmlFileExtension = ".XML";

    private static final String xmlFileDirName;

    private static final DateFormat mJDBCDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static int nMaxCounters;

    private static Map mBrowseIdMap = new HashMap();

    static final String notAvailable = "n/a";

    private boolean showDebug;

    private Map mIDMap;

    static jgl.HashMap noData = new jgl.HashMap();

    public BrowsableNTCounterMonitor() {
        showDebug = false;
        mIDMap = null;
    }

    protected void startMonitor() {
        if (getSetting("_perfDebug").length() > 0) {
            showDebug = true;
        }
        super.startMonitor();
    }

    protected String getCounterFilename() {
        String s = Platform.getRoot() + File.separator + xmlFileDirName + File.separator + getProperty(pFile);
        return s;
    }

    protected boolean update() {
        String s = getProperty(pHost);
        long l = TextUtils.toLong(getProperty(pLastMeasurementTime));
        long l1 = TextUtils.toLong(getProperty(pLastMeasurementTicks));
        boolean flag = TextUtils.toInt(getProperty(pLastMeasurementWasNA)) == 1;
        long l2 = 0L;
        long l3 = 1L;
        long l4 = 0L;
        StringBuffer stringbuffer = new StringBuffer();
        Map map = (Map) mBrowseIdMap.get(getCounterFilename());
        if (map == null) {
            getBrowseData(new StringBuffer());
            map = (Map) mBrowseIdMap.get(getCounterFilename());
        }
        Array array = new Array();
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        int i = 0;
        for (int j = 0; j < getMaxCounters(); j ++) {
            String s2 = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
            if (s2.length() <= 0) {
                continue;
            }
            StringTokenizer stringtokenizer = new StringTokenizer(s2, " ");
            stringtokenizer.nextToken();
            String s3 = stringtokenizer.nextToken().toLowerCase();
            PerfCounter perfcounter = (PerfCounter) map.get(s3);
            String s5 = PROPERTY_NAME_COUNTER_VALUE + (j + 1);
            String s6 = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
            if (perfcounter != null && perfcounter.object != null && !perfcounter.object.equals("")) {
                array.add(perfcounter);
                vector.addElement(s6);
                vector1.addElement(s5);
                continue;
            }
            i ++;
            setProperty(s5, "n/a");
            if (stringbuffer.length() > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(s6).append("=").append("n/a");
        }

        if (array.size() == 0) {
            setProperty(pLastMeasurementTime, 0);
            for (int k = 0; k < nMaxCounters; k ++) {
                setProperty(pValues[k], "n/a");
                setProperty(pMeasurements[k], 0);
                setProperty(pLastMeasurements[k], 0);
            }

        }
        if (array.size() > nMaxCounters) {
            String s1 = "Too many counters selected";
            setProperty(pStateString, s1);
            setProperty(pNoData, "n/a");
            return false;
        }
        if (mIDMap == null) {
            mIDMap = NTCounterBase.getIDCacheForCounters(s, array);
        }
        long al[] = new long[nMaxCounters];
        long al1[] = new long[nMaxCounters];
        long al2[] = new long[nMaxCounters];
        String s4 = jdbcDateFormat(Platform.makeDate());
        Enumeration enumeration = null;
        long l5 = 0L;
        long l6 = 0L;
        float f = 0.0F;
        boolean flag1 = true;
        Object obj = null;
        Array array2 = null;
        if (monitorDebugLevel == 3) {
            array2 = new Array();
        }
        label0: for (int i1 = 0; flag1 && i1 < 2; i1 ++) {
            if (i1 != 0) {
                Platform.sleep(4000L);
            }
            StringBuffer stringbuffer1 = new StringBuffer();
            Array array1 = NTCounterBase.getPerfData(s, array, stringbuffer1, showDebug, this, array2, mIDMap);
            stringbuffer.append(stringbuffer1.toString());
            enumeration = array1.elements();
            if (enumeration.hasMoreElements()) {
                l2 = TextUtils.toLong((String) enumeration.nextElement());
            }
            if (enumeration.hasMoreElements()) {
                l4 = TextUtils.toLong((String) enumeration.nextElement());
            }
            if (enumeration.hasMoreElements()) {
                l3 = TextUtils.toLong((String) enumeration.nextElement());
            }
            l5 = l2 - l;
            l6 = l4 - l1;
            f = (float) l6 / (float) l3;
            flag1 = l5 <= 0L || l6 <= 0L || l <= 0L || flag || l1 <= 0L;
            if (i1 > 0) {
                break;
            }
            if (!flag1) {
                for (int k1 = 0; k1 < nMaxCounters; k1 ++) {
                    al[k1] = TextUtils.toLong(getProperty(pLastMeasurements[k1]));
                    al2[k1] = TextUtils.toLong(getProperty(pLastBaseMeasurements[k1]));
                }

                continue;
            }
            l = l2;
            l1 = l4;
            flag = false;
            int i2 = 0;
            do {
                if (!enumeration.hasMoreElements()) {
                    continue label0;
                }
                String s7 = (String) enumeration.nextElement();
                if (s7.equals("n/a")) {
                    enumeration.nextElement();
                    enumeration.nextElement();
                    al[i2] = 0L;
                    al2[i2 ++] = 0L;
                } else {
                    al[i2] = TextUtils.toLong((String) enumeration.nextElement());
                    al2[i2 ++] = TextUtils.toLong((String) enumeration.nextElement());
                }
            } while (true);
        }

        int j1 = 0;
        byte byte0 = 2;
        for (; enumeration.hasMoreElements(); j1 ++) {
            PerfCounter perfcounter1 = (PerfCounter) array.at(j1);
            String s8 = (String) enumeration.nextElement();
            float f1 = (0.0F / 0.0F);
            long l7 = 0L;
            boolean flag2 = false;
            boolean flag3 = false;
            if (s8.equals("n/a")) {
                enumeration.nextElement();
                enumeration.nextElement();
            } else {
                al1[j1] = TextUtils.toLong((String) enumeration.nextElement());
                long l8 = al1[j1] - al[j1];
                byte0 = 2;
                l7 = TextUtils.toLong((String) enumeration.nextElement());
                long l9 = l7 - al2[j1];
                if (!flag1) {
                    if (stringbuffer.length() > 0) {
                        stringbuffer.append(", ");
                    }
                    if (al1[j1] >= 0L) {
                        if (s8.equals("PERF_COUNTER_COUNTER")) {
                            if (l8 >= 0L) {
                                if (al[j1] == 0L) {
                                    f1 = 0.0F;
                                } else {
                                    f1 = (float) l8 / f;
                                }
                                flag3 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_TIMER")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / (float) l6;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_TIMER_INV")) {
                            if (l8 >= 0L) {
                                f1 = 1.0F - (float) l8 / f;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_BULK_COUNT")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / f;
                                flag3 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_RAWCOUNT") || s8.equals("PERF_COUNTER_LARGE_RAWCOUNT") || s8.equals("PERF_COUNTER_RAWCOUNT_HEX") || s8.equals("PERF_COUNTER_LARGE_RAWCOUNT_HEX")) {
                            f1 = al1[j1];
                            byte0 = 0;
                        } else if (s8.equals("PERF_ELAPSED_TIME")) {
                            long l10 = al1[j1];
                            if (l10 > l2 || l10 < 0L) {
                                l10 >>>= 32;
                            }
                            f1 = (l2 - l10) / 0x989680L;
                            byte0 = 0;
                        } else if (s8.equals("PERF_RAW_FRACTION")) {
                            if (al1[j1] == 0L) {
                                f1 = 0.0F;
                            } else {
                                f1 = (float) al1[j1] / (float) l7;
                            }
                            flag2 = true;
                        } else if (s8.equals("PERF_SAMPLE_FRACTION")) {
                            flag2 = true;
                            if (l8 <= 0L || l9 <= 0L) {
                                f1 = 0.0F;
                            } else {
                                f1 = (float) l8 / (float) l9;
                            }
                        } else if (s8.equals("PERF_SAMPLE_COUNTER")) {
                            if (l8 >= 0L && l9 > 0L) {
                                f1 = (float) l8 / (float) l9;
                            }
                        } else if (s8.equals("PERF_AVERAGE_TIME")) {
                            f1 = (float) al1[j1] / (float) l7;
                        } else if (s8.equals("PERF_AVERAGE_TIMER")) {
                            if (l8 >= 0L && l9 > 0L) {
                                f1 = (float) l8 / (float) l3 / (float) l9;
                            } else {
                                f1 = 0.0F;
                            }
                        } else if (s8.equals("PERF_AVERAGE_BULK")) {
                            if (l8 >= 0L && l9 > 0L) {
                                f1 = (float) l8 / (float) l9;
                            }
                        } else if (s8.equals("PERF_100NSEC_TIMER")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / (float) l5;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_100NSEC_TIMER_INV")) {
                            if (l8 >= 0L) {
                                float f2 = (float) l8 / (float) l5;
                                f1 = 1.0F - f2;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_MULTI_TIMER")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / f / (float) l7;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_MULTI_TIMER_INV")) {
                            if (l8 >= 0L) {
                                f1 = 1.0F - (float) l8 / f / (float) l7;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_100NSEC_MULTI_TIMER")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / (float) l5 / (float) l7;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_100NSEC_MULTI_TIMER_INV")) {
                            if (l8 >= 0L) {
                                f1 = 1.0F - (float) l8 / (float) l5 / (float) l7;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_COUNTER_QUEUELEN_TYPE")) {
                            f1 = ((float) al[j1] + (float) (l2 * al1[j1])) / (float) l5;
                        } else if (s8.equals("PERF_COUNTER_LARGE_QUEUELEN_TYPE")) {
                            f1 = ((float) al[j1] + (float) (l2 * al1[j1])) / (float) l5;
                        } else if (s8.equals("PERF_PRECISION_100NSEC_TIMER")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / (float) l9;
                                flag2 = true;
                            }
                        } else if (s8.equals("PERF_PRECISION_100NSEC_QUEUELEN")) {
                            if (l8 >= 0L) {
                                f1 = (float) l8 / (float) l5;
                            }
                        } else {
                            LogManager.log("Error", "NTCounterMonitor: could not find the counter type for: " + s8);
                        }
                        if (flag2) {
                            byte0 = 2;
                            f1 *= 100F;
                        }
                    }
                }
                if (showDebug) {
                    LogManager.log("RunMonitor", "Counter Monitor, type=" + s8 + ", value=" + f1 + ", delta=" + l8 + ", deltaBase=" + l9 + ", deltaTime=" + l5);
                }
            }
            String s9 = (String) vector.elementAt(j1);
            String s10 = (String) vector1.elementAt(j1);
            if (!stillActive()) {
                continue;
            }
            synchronized (this) {
                logPerfValue(perfcounter1, s4, f1);
                if (Float.isNaN(f1)) {
                    setProperty(pLastMeasurementTime, 0);
                    setProperty(pLastMeasurementWasNA, 1);
                    setProperty(pValues[j1], "n/a");
                    setProperty(pMeasurements[j1], 0);
                    setProperty(pLastMeasurements[j1], 0);
                    setProperty(s10, "n/a");
                    stringbuffer.append(s9).append(" ").append("n/a");
                    i ++;
                    if (monitorDebugLevel == 3 && array2 != null) {
                        StringBuffer stringbuffer2 = new StringBuffer();
                        for (int j2 = 0; j2 < array2.size(); j2 ++) {
                            stringbuffer2.append(array2.at(j2) + "\n");
                        }

                        LogManager.log("Error", "NTCounterMonitor: " + getFullID() + " failed, output:\n" + stringbuffer2);
                    }
                } else {
                    String s11 = TextUtils.floatToString(f1, byte0);
                    setProperty(s10, s11);
                    setProperty(pValues[j1], s11);
                    setProperty(pLastMeasurements[j1], al1[j1]);
                    setProperty(pLastBaseMeasurements[j1], l7);
                    setProperty(pMeasurements[j1], getMeasurement(pValues[j1], 10L));
                    setProperty(pLastMeasurementWasNA, 0);
                    stringbuffer.append(s9).append("=").append(s11);
                    if (flag2) {
                        stringbuffer.append("%");
                    }
                    if (flag3) {
                        stringbuffer.append("/sec");
                    }
                }
            }
        }

        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), i);
        setProperty(pLastMeasurementTime, l2);
        setProperty(pLastMeasurementTicks, l4);
        if (stringbuffer.length() > 0) {
            setProperty(pStateString, stringbuffer.toString());
        } else {
            setProperty(pStateString, "no data");
        }
        if (flag1) {
            setProperty(pNoData, "n/a");
        }
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param date
     * @return
     */
    String jdbcDateFormat(Date date) {
        synchronized (BrowsableNTCounterMonitor.mJDBCDateFormat) {
            return mJDBCDateFormat.format(date);
        }
    }

    void logPerfValue(PerfCounter perfcounter, String s, float f) {
        if (getProperty("_databaseLogging").length() == 0) {
            return;
        }
        if (JdbcLogger.logger == null) {
            LogManager.log("Error", "database logging not configured");
            return;
        }
        if (Float.isNaN(f)) {
            String s1 = perfcounter.object + "/" + perfcounter.counterName + "/" + perfcounter.instance + ", " + getParent().getProperty(pName) + ", " + getProperty(pName);
            if (noData.get(s1) == null) {
                noData.put(s1, s1);
                LogManager.log("RunMonitor", "n/a perf data, " + s1);
            }
            return;
        }
        String s2 = getProperty(pMonitorDescription);
        String s3 = "-1";
        Array array = Platform.split(' ', s2);
        if (array.size() >= 1) {
            Array array1 = Platform.split('=', (String) array.at(0));
            if (array1.size() == 2) {
                s3 = (String) array1.at(1);
            }
        }
        Array array2 = new Array();
        array2.add(s3);
        array2.add(s);
        array2.add(perfcounter.object);
        array2.add(perfcounter.counterName);
        array2.add(perfcounter.instance);
        array2.add("" + f);
        JdbcLogger.logger.logCustom(this, array2, "perf_data", NTCounterMonitor.perf_data_create, NTCounterMonitor.perf_data_create2, NTCounterMonitor.perf_data_define, NTCounterMonitor.perf_data_insert);
    }

    public void setProperty(StringProperty stringproperty, String s) throws NullPointerException {
        if (stringproperty.getName().startsWith(PROPERTY_NAME_COUNTER_ID)) {
            boolean flag = false;
            try {
                int i = Integer.parseInt(stringproperty.getName().substring(PROPERTY_NAME_COUNTER_ID.length()));
                i --;
                setProperty(pValues[i], "n/a");
                setProperty(pMeasurements[i], 0);
                setProperty(pLastMeasurements[i], 0);
            } catch (Exception exception) {
            }
        }
        super.setProperty(stringproperty, s);
    }

    public boolean isServerBased() {
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public String getBrowseData(StringBuffer stringbuffer) {
        try {
            Document document = parseCounterFile();
            Map map = findCounters(document);
            String s = buildBrowseString(document, map);
            return s;
        } catch (Exception e) {
            LogManager.log("Error", "BrowsableNTCounterMonitor: " + getFullID() + " failed in getBrowseData, " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private String buildBrowseString(Document document, Map map) {
        String s = getCounterFilename();
        HashMap hashmap = new HashMap();
        mBrowseIdMap.put(s, hashmap);
        NodeList nodelist = document.getDocumentElement().getElementsByTagName("object");
        int i = nodelist.getLength();
        LinkedList linkedlist = new LinkedList();
        for (int j = 0; j < i; j ++) {
            Element element = (Element) nodelist.item(j);
            if (element.getAttribute("type").equals("perf")) {
                linkedlist.add(element);
            }
        }

        for (Iterator iterator = linkedlist.iterator(); iterator.hasNext();) {
            Element element1 = (Element) iterator.next();
            String s1 = element1.getAttribute("name");
            String s2 = element1.getAttribute("instances");
            HashSet hashset = null;
            HashSet hashset1 = new HashSet();
            if (s2.length() > 0 && !s2.equals("*")) {
                hashset = new HashSet();
                for (StringTokenizer stringtokenizer = new StringTokenizer(s2, ","); stringtokenizer.hasMoreTokens(); hashset.add(stringtokenizer.nextToken().trim().toLowerCase())) {
                }
            }
            NodeList nodelist2 = element1.getElementsByTagName("counterInfo");
            int i1 = nodelist2.getLength();
            for (int j1 = 0; j1 < i1; j1 ++) {
                Element element4 = (Element) nodelist2.item(j1);
                hashset1.add(element4.getAttribute("name"));
            }

            Map map1 = (Map) map.get(s1);
            if (map1 != null) {
                Iterator iterator2 = map1.entrySet().iterator();
                while (iterator2.hasNext()) {
                    java.util.Map.Entry entry = (java.util.Map.Entry) iterator2.next();
                    String s3 = (String) entry.getKey();
                    if (hashset == null || hashset.contains(s3.toLowerCase()) || s3.equals("SINGLE")) {
                        Element element5 = element1;
                        if (!s3.equals("SINGLE")) {
                            Element element6 = document.createElement("object");
                            element6.setAttribute("name", s3);
                            element1.appendChild(element6);
                            element5 = element6;
                        }
                        List list = (List) entry.getValue();
                        Iterator iterator3 = list.iterator();
                        while (iterator3.hasNext()) {
                            PerfCounter perfcounter = (PerfCounter) iterator3.next();
                            if (hashset1.contains(perfcounter.counterName)) {
                                Element element7 = document.createElement("counter");
                                element5.appendChild(element7);
                                String s4 = createBrowseId(perfcounter);
                                element7.setAttribute("id", s4);
                                element7.setAttribute("name", perfcounter.counterName);
                                hashmap.put(s4.toLowerCase(), perfcounter);
                            }
                        }
                    }
                }
            }
        }

        NodeList nodelist1 = document.getDocumentElement().getElementsByTagName("counterInfo");
        int k = nodelist1.getLength();
        LinkedList linkedlist1 = new LinkedList();
        for (int l = 0; l < k; l ++) {
            Element element2 = (Element) nodelist1.item(l);
            linkedlist1.add(element2);
        }

        Element element3;
        for (Iterator iterator1 = linkedlist1.iterator(); iterator1.hasNext(); element3.getParentNode().removeChild(element3)) {
            element3 = (Element) iterator1.next();
        }

        StringWriter stringwriter = new StringWriter();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(document.getDocumentElement()), new StreamResult(stringwriter));
        } catch (TransformerConfigurationException transformerconfigurationexception) {
            LogManager.log("Error", "Error getting BrowsableNTCounter counters: " + transformerconfigurationexception);
        } catch (TransformerException transformerexception) {
            LogManager.log("Error", "Error getting BrowsableNTCounter counters: " + transformerexception);
        }
        return stringwriter.toString();
    }

    private Document parseCounterFile() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException {
        String s = getCounterFilename();
        File file = new File(s);
        if (!file.exists()) {
            throw new FileNotFoundException(s);
        } else {
            DocumentBuilder documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return documentbuilder.parse(new InputSource(new FileReader(file)));
        }
    }

    private Map findCounters(Document document) {
        Array array = new Array();
        Element element = document.getDocumentElement();
        NodeList nodelist = element.getElementsByTagName("object");
        int i = nodelist.getLength();
        for (int j = 0; j < i; j ++) {
            Element element1 = (Element) nodelist.item(j);
            String s1 = element1.getAttribute("type");
            if (s1.equals("perf")) {
                array.add(element1.getAttribute("name"));
            }
        }

        StringBuffer stringbuffer = new StringBuffer();
        String s = getHostname();
        Array array1 = NTCounterBase.getPerfCounters(s, array, stringbuffer, "");
        HashMap hashmap = new HashMap();
        for (int k = 0; k < array1.size(); k ++) {
            PerfCounter perfcounter = (PerfCounter) array1.at(k);
            Object obj = (Map) hashmap.get(perfcounter.object);
            if (obj == null) {
                obj = new HashMap();
                hashmap.put(perfcounter.object, obj);
            }
            Object obj1 = (List) ((Map) (obj)).get(perfcounter.instance);
            if (obj1 == null) {
                obj1 = new LinkedList();
                ((Map) (obj)).put(perfcounter.instance, obj1);
            }
            ((List) (obj1)).add(perfcounter);
        }

        return hashmap;
    }

    private String createBrowseId(PerfCounter perfcounter) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(perfcounter.object).append("x").append(perfcounter.instance).append("x").append(perfcounter.counterName);
        return stringbuffer.toString().replace(' ', 'x');
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public String getHostname() {
        return getProperty(pHost);
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_BrowsableNTMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public Array getConnectionProperties() {
        return mConnectionProps;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pFile) {
            File file = new File(Platform.getRoot() + File.separator + xmlFileDirName + File.separator);
            String as[] = file.list();
            Vector vector = new Vector();
            for (int i = 0; i < as.length; i ++) {
                String s = as[i].toUpperCase();
                if (s.endsWith(".XML")) {
                    vector.addElement(as[i]);
                    vector.addElement(as[i]);
                }
            }

            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public boolean isMultiThreshold() {
        return true;
    }

    public StringProperty getServerProperty() {
        return pHost;
    }

    public boolean remoteCommandLineAllowed() {
        return false;
    }

    public Array getPropertiesToPassBetweenPages(HTTPRequest httprequest) {
        Array array = super.getPropertiesToPassBetweenPages(httprequest);
        array.add(pHost);
        return array;
    }

    public int getCostInLicensePoints() {
        HashSet hashset = new HashSet();
        for (int i = 0; i < getMaxCounters(); i ++) {
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() <= 0) {
                continue;
            }
            StringTokenizer stringtokenizer = new StringTokenizer(s, " ");
            stringtokenizer.nextToken();
            s = stringtokenizer.nextToken();
            int j = s.indexOf("xSINGLEx");
            if (j > -1) {
                hashset.add(s.substring(0, j));
                continue;
            }
            String s1 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1));
            int k = 0;
            int l = 0;
            int i1 = 0;
            do {
                if (k != 0 && l != 0 || i1 <= -1) {
                    break;
                }
                i1 = s.indexOf("x", i1 + 1);
                if (i1 > -1 && s1.charAt(i1) == '/') {
                    if (k == 0) {
                        k = i1 + 1;
                    } else {
                        l = i1;
                    }
                }
            } while (true);
            if (k > 0 && l > 0) {
                hashset.add(s.substring(0, l));
            }
        }

        return hashset.size();
    }

    protected void ruleApplied(Rule rule) {
        String s = rule.getProperty(Rule.pAction);
        String s1 = "error";
        if (s.endsWith("warning")) {
            s1 = "warning";
        }
        if (s1.equals("error") || s1.equals("warning")) {
            try {
                Expression expression = Expression.parseString(rule.getProperty(Rule.pExpression));
                String s2 = expression.toString(this);
                if ((COM.dragonflow.Utils.SimpleExpression.class).isInstance(expression)) {
                    Expression expression1 = ((SimpleExpression) expression).getLeft();
                    if ((COM.dragonflow.Utils.PropertyNameLiteral.class).isInstance(expression1)) {
                        String s3 = ((PropertyNameLiteral) expression1).getPropertyName();
                        if (s3.startsWith(PROPERTY_NAME_COUNTER_VALUE)) {
                            String s4 = GetPropertyLabel(getPropertyObject(s3), false);
                            s2 = TextUtils.replaceString(s2, s3, s4);
                        }
                    }
                    StringBuffer stringbuffer = new StringBuffer();
                    stringbuffer.append("{ Threshold ").append(s1).append(": ").append(s2).append(" } ");
                    setProperty(pStateString, stringbuffer.toString() + getProperty(pStateString));
                }
            } catch (Exception exception) {
            }
        }
    }

    protected boolean onlyInvokeRuleAppliedForFirstViolator() {
        return false;
    }

    static {
        mConnectionProps = new Array();
        xmlFileDirName = "templates.perfmon" + File.separator + "browsable";
        nMaxCounters = 40;
        try {
            jgl.HashMap hashmap = MasterConfig.getMasterConfig();
            nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_BrowsableNTMaxCounters"));
            if (nMaxCounters == 0) {
                nMaxCounters = 40;
            }
            StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
            pHost = new ServerProperty("_server");
            pHost.setDisplayText("Server", "");
            pHost.setParameterOptions(false, true, 1, false);
            pFile = new ScalarProperty("_xmlfile");
            pFile.setDisplayText("Counter File", "a Performance Monitor setting file that specifies the counters (from " + xmlFileDirName + " directory)");
            pFile.setParameterOptions(false, true, 2, false);
            mConnectionProps.add(pHost);
            mConnectionProps.add(pFile);
            Array array = new Array();
            array.add(pHost);
            array.add(pFile);
            pValues = new StringProperty[nMaxCounters];
            pMeasurements = new StringProperty[nMaxCounters];
            pLastMeasurements = new StringProperty[nMaxCounters];
            pLastBaseMeasurements = new StringProperty[nMaxCounters];
            for (int i = 0; i < nMaxCounters; i ++) {
                pValues[i] = new NumericProperty("value" + i);
                array.add(pValues[i]);
                pMeasurements[i] = new NumericProperty("measurement" + i);
                array.add(pMeasurements[i]);
                pLastMeasurements[i] = new NumericProperty("lastMeasurement" + i);
                array.add(pLastMeasurements[i]);
                pLastBaseMeasurements[i] = new NumericProperty("lastBaseMeasurement" + i);
                array.add(pLastBaseMeasurements[i]);
            }

            pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
            pLastMeasurementTicks = new NumericProperty("lastMeasurementTicks");
            pLastMeasurementWasNA = new NumericProperty("lastMeasurementWasNotAvailable");
            array.add(pLastMeasurementTime);
            array.add(pLastMeasurementTicks);
            array.add(pLastMeasurementWasNA);
            StringProperty astringproperty1[] = new StringProperty[array.size()];
            for (int j = 0; j < array.size(); j ++) {
                astringproperty1[j] = (StringProperty) array.at(j);
            }

            String s = (COM.dragonflow.StandardMonitor.BrowsableNTCounterMonitor.class).getName();
            StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
            System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
            System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
            addProperties(s, astringproperty2);
            addClassElement(s, Rule.stringToClassifier(PROPERTY_NAME_COUNTERS_IN_ERROR + " > 0\terror", true));
            addClassElement(s, Rule.stringToClassifier("always\tgood"));
            setClassProperty(s, "description", "Monitors NT Performance Counters.");
            setClassProperty(s, "title", "Browsable Windows Performance Counters");
            setClassProperty(s, "class", "BrowsableNTCounterMonitor");
            setClassProperty(s, "help", "BrowsableNTCounterMon.htm");
            setClassProperty(s, "target", "_server");
            setClassProperty(s, "topazName", "Browsable NT Counters");
            setClassProperty(s, "classType", "advanced");
            if (!Platform.isWindows()) {
                setClassProperty(s, "loadable", "false");
            }
            setClassProperty(s, "addable", "false");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}