/*
 * 
 * Created on 2005-2-16 17:27:08
 *
 * XMLMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>XMLMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import jgl.Array;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import COM.datachannel.xml.om.Document;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.browsablePage;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.FrequencyProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.XSLUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, Monitor, Platform, MasterConfig

public class XMLMonitor extends BrowsableBase {

    protected static StringProperty pHostname;

    static BooleanProperty pSecure;

    static StringProperty pProxy;

    static StringProperty pProxyUser;

    static StringProperty pProxyPassword;

    static StringProperty pUser;

    static StringProperty pPassword;

    protected static StringProperty pTimeout;

    static FrequencyProperty pPostMetricsFrequency;

    static NumericProperty pPostMetricsLast;

    static StringProperty pStatus;

    public static int nMaxCounters;

    private String monitorTitle;

    static long xslTime = 0L;

    long networkTime;

    public XMLMonitor() {
        monitorTitle = getClassPropertyString("title") + " Monitor";
        networkTime = 0L;
    }

    public int getPort() {
        boolean flag = getPropertyAsBoolean(pSecure);
        return flag ? 443 : 80;
    }

    public boolean isNeedPostMetricsError(String s) {
        return false;
    }

    public String getMetricListURL() {
        return null;
    }

    public String getPostMetricURL() {
        return null;
    }

    public String getMetricDataURL() {
        return null;
    }

    public String getMetricListXSL() {
        return null;
    }

    public String getPostMetricXSL() {
        return null;
    }

    public String getMetricDataXSL() {
        return null;
    }

    public boolean isPostAllMetrics() {
        return false;
    }

    protected void doUpdateError(String s, boolean flag) {
        setProperty(pStatus, "error");
        setProperty(Monitor.pStateString, s);
        if (flag) {
            for (int i = 0; i < getMaxCounters()
                    && !getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1))
                            .equals(""); i++) {
                setProperty(PROPERTY_NAME_COUNTER_VALUE + (i + 1), "n/a");
            }

            setProperty(pNoData, "n/a");
            StringProperty stringproperty = getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR);
            if (stringproperty != null) {
                setProperty(stringproperty, getMaxCounters());
            }
        }
    }

    static Element getChildElement(Element element, String s, String s1,
            boolean flag) {
        Object obj = null;
        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            String s2 = nodelist.item(i).getNodeName();
            if (!s2.equals(s)) {
                continue;
            }
            Element element1 = (Element) nodelist.item(i);
            String s3 = element1.getAttribute("name");
            if (s3.equals(s1)) {
                return element1;
            }
        }

        if (flag) {
            Element element2 = element.getOwnerDocument().createElement(s);
            element2.setAttribute("name", s1);
            element.appendChild(element2);
            return element2;
        } else {
            return null;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param xslFileName
     * @param stringbuffer
     * @return
     */
    protected static boolean transform(String s, String xslFileName,
            StringBuffer stringbuffer) {
        long l = System.currentTimeMillis();

        try {
            if (xslFileName == null) {
                stringbuffer.append(s);
                return true;
            }
            xslFileName = Platform.getRoot() + "/" + xslFileName;
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            StringBuffer fileContent = FileUtils.readFile(xslFileName);
            XSLUtils.convert(s, fileContent.toString(), new PrintWriter(
                    charArrayWriter));
            stringbuffer.append(charArrayWriter.toCharArray());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            stringbuffer.append("Failed to read XSL file: " + xslFileName);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            stringbuffer.append("Exception: " + e);
            return false;
        } finally {
            l = System.currentTimeMillis() - l;
            System.out.println("Transform: " + l);
            xslTime += l;
        }
    }

    protected String getPostXML() {
        Document document = new Document();
        String s = "";
        if (isPostAllMetrics()) {
            LogManager.log("RunMonitor", monitorTitle
                    + ": Retrieving metricList from server for postMetrics()");
            StringBuffer stringbuffer = new StringBuffer();
            s = getBrowseData(stringbuffer);
            if (s.equals("")) {
                LogManager
                        .log(
                                "RunMonitor",
                                monitorTitle
                                        + ": Failed to load metric list, constructing list from active counters");
            } else {
                try {
                    document.loadXML(s);
                    NodeList nodelist = document
                            .getElementsByTagName("counter");
                    for (int j = 0; j < nodelist.getLength(); j++) {
                        Element element1 = (Element) nodelist.item(j);
                        element1.setAttribute("enabled", "1");
                    }

                } catch (Exception exception) {
                    LogManager
                            .log(
                                    "warning",
                                    monitorTitle
                                            + ": Bad XML found when asking for metrics, constructing list from active counters");
                }
            }
        }
        if (s.equals("")) {
            Element element = document.createElement("browse_data");
            document.appendChild(element);
            int i = 0;
            do {
                if (i >= getMaxCounters()) {
                    break;
                }
                String s1 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1));
                if (s1.equals("")) {
                    break;
                }
                Array array = setBrowseName(s1);
                String s2 = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
                Array array1;
                if (s2.equals(s1)) {
                    array1 = null;
                } else {
                    array1 = setBrowseID(s2);
                }
                Element element2 = element;
                for (int k = 0; k < array.size(); k++) {
                    boolean flag = k == array.size() - 1;
                    String s3 = flag ? "counter" : "object";
                    element2 = getChildElement(element2, s3, (String) array
                            .at(k), true);
                    if (array1 != null) {
                        int l = array.size() - array1.size();
                        if (k - l >= 0) {
                            element2.setAttribute("id", (String) array1.at(k
                                    - l));
                        }
                    }
                    if (flag) {
                        element2.setAttribute("enabled", "1");
                    }
                }

                i++;
            } while (true);
        }
        return document.getXML();
    }

    protected boolean postMetrics() {
        String s = null;
        if (getPostMetricURL() != null) {
            long l = System.currentTimeMillis() / 1000L;
            long l1 = Integer.parseInt(getProperty(pPostMetricsLast));
            long l2 = getPropertyAsLong(pPostMetricsFrequency);
            if (l - l1 >= l2) {
                System.out.println("Before getPostXML");
                s = getPostXML();
                System.out.println("After getPostXML");
                setProperty(pPostMetricsLast, l);
            }
        }
        boolean flag = true;
        if (s != null) {
            StringBuffer stringbuffer = new StringBuffer();
            System.out.println("Before Transform");
            boolean flag1 = XMLMonitor.transform(s, this.getPostMetricXSL(),
                    stringbuffer);
            System.out.println("After Transform");
            if (!flag1) {
                System.out.println("transform failed1");
                System.out.println("**** " + stringbuffer);
                doUpdateError(stringbuffer.toString(), true);
                return false;
            }
            StringBuffer stringbuffer1 = new StringBuffer();
            flag1 = doRequest(getPostMetricURL(), null,
                    stringbuffer.toString(), stringbuffer1);
            if (!flag1) {
                System.out.println("*****  Post failed");
                System.out.println("**** " + stringbuffer1);
                doUpdateError(stringbuffer1.toString(), true);
                return false;
            }
        }
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        this.networkTime = 0L;
        XMLMonitor.xslTime = 0L;
        long updateStartTime = System.currentTimeMillis();
        if (this.stillActive()) {
            synchronized (this) {
                System.out.println("Before post metrics");
                boolean pmResult = this.postMetrics();
                System.out.println("After post metrics");
                if (!pmResult) {
                    return true;
                }

                StringBuffer statusBuffer = new StringBuffer();
                boolean drResult = this.doRequest(this.getMetricDataURL(), this
                        .getMetricDataXSL(), null, statusBuffer);
                String requestStatus = statusBuffer.toString();
                if (this.getPostMetricURL() == null
                        || !this.isNeedPostMetricsError(requestStatus)) {
                    if (!drResult) {
                        this.doUpdateError(requestStatus, true);
                        return true;
                    }
                } else {
                    LogManager.log("RunMonitor", this.monitorTitle
                            + ": Forcing a postMetrics()");
                    this.setProperty(XMLMonitor.pPostMetricsLast, 0);
                    if (!this.postMetrics()) {
                        return true;
                    }
                    statusBuffer = new StringBuffer();
                    drResult = this.doRequest(getMetricDataURL(),
                            getMetricDataXSL(), null, statusBuffer);
                    requestStatus = statusBuffer.toString();
                }

                int browseDataIndex = requestStatus.indexOf("<browse_data>");
                if (browseDataIndex < 0) {
                    doUpdateError("Invalid Data Returned", true);
                    LogManager.log("error", this.monitorTitle
                            + ": Invalid Data Returned:");
                    LogManager.log("error", this.monitorTitle
                            + ": ---------Start of returned data----------");
                    LogManager.log("error", this.monitorTitle + ": "
                            + requestStatus);
                    LogManager.log("error", this.monitorTitle
                            + ": ---------End of returned data------------");
                    return true;
                } else if (browseDataIndex > 0) {
                    requestStatus = requestStatus.substring(browseDataIndex);
                }

                Document document = new Document();
                document.loadXML(requestStatus);
                Element root = (Element) document.getFirstChild();
                StringBuffer stateString = new StringBuffer();
                long filterStartTime = System.currentTimeMillis();
                HashMap countersHashMap = new HashMap();
                int counterNumbers = 0;
                while (counterNumbers < this.getMaxCounters()) {
                    String counterName = getProperty(BrowsableBase.PROPERTY_NAME_COUNTER_NAME
                            + (counterNumbers + 1));
                    if (counterName.equals("")) {
                        break;
                    }
                    countersHashMap
                            .put(
                                    counterName,
                                    getPropertyObject(BrowsableBase.PROPERTY_NAME_COUNTER_VALUE
                                            + (counterNumbers + 1)));
                    counterNumbers++;
                }

                NodeList nodelist = root.getElementsByTagName("counter");
                int nodeListLength = nodelist.getLength();
                int k = 0;
                for (int i = 0; i < nodeListLength; i++) {
                    Element element = (Element) nodelist.item(i);
                    Array displayNames = browsablePage
                            .getNodeDisplayNames(element);
                    String browseName = this.setBrowseName(displayNames);
                    StringProperty browseNameProperty = (StringProperty) countersHashMap
                            .get(browseName);
                    if (browseNameProperty == null) {
                        continue;
                    }
                    countersHashMap.remove(browseName);

                    String value = element.getAttribute("value");
                    if (value != null && value.equals("")) {
                        value = "0.0";
                    }
                    this.setProperty(browseNameProperty, value);
                    if (countersHashMap.size() == 0) {
                        break;
                    }
                }

                for (Iterator iterator = countersHashMap.values().iterator(); iterator
                        .hasNext();) {
                    StringProperty stringproperty1 = (StringProperty) iterator
                            .next();
                    this.setProperty(stringproperty1, "n/a");
                    k++;
                }

                StringProperty countersInErrorProperty = getPropertyObject(BrowsableBase.PROPERTY_NAME_COUNTERS_IN_ERROR);
                if (countersInErrorProperty != null) {
                    this.setProperty(countersInErrorProperty, k);
                }

                for (int i = 0; i < counterNumbers; i++) {
                    stateString
                            .append(getProperty(BrowsableBase.PROPERTY_NAME_COUNTER_NAME
                                    + (i + 1))
                                    + "="
                                    + getProperty(BrowsableBase.PROPERTY_NAME_COUNTER_VALUE
                                            + (i + 1)));
                    if (i < counterNumbers - 1) {
                        stateString.append(", ");
                    }
                }

                long updateStopTime = System.currentTimeMillis();

                System.out.println("Network Time: " + this.networkTime);
                System.out.println("XSL Time: " + XMLMonitor.xslTime);
                System.out.println("Time to update " + counterNumbers
                        + " counters: " + (updateStopTime - updateStartTime));
                System.out.println("Time to filter " + counterNumbers
                        + " counters: " + (updateStopTime - filterStartTime));
                setProperty(XMLMonitor.pStateString, stateString);
                setProperty(XMLMonitor.pStatus, "OK");
            }
        }
        return true;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        String s = "";
        String s1 = getMetricListURL();
        if (s1 == null) {
            stringbuffer
                    .append("Monitor implementation is not complete. Must implement method: getMetricListURL()");
            return "";
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        boolean flag = doRequest(s1, getMetricListXSL(), null, stringbuffer1);
        if (!flag) {
            stringbuffer.append(stringbuffer1.toString());
            return "";
        }
        s = stringbuffer1.toString();
        int i = s.indexOf("<browse_data>");
        if (i > 0) {
            s = s.substring(i);
        }
        return s;
    }

    public String setBrowseName(Array array) {
        String s = "";
        for (int i = array.size(); i > 0; i--) {
            if (i < array.size()) {
                s = s + '/';
            }
            s = s + escapeString((String) array.at(i - 1));
        }

        return s;
    }

    private Array parseString(String s) {
        Array array = new Array();
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                if (++i < s.length()) {
                    stringbuffer.append(s.charAt(i));
                }
                continue;
            }
            if (c == '/') {
                array.add(stringbuffer.toString());
                stringbuffer = new StringBuffer();
            } else {
                stringbuffer.append(c);
            }
        }

        array.add(stringbuffer.toString());
        return array;
    }

    private String escapeString(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '/' || c == '\\') {
                stringbuffer.append('\\');
            }
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    public Array setBrowseName(String s) {
        return parseString(s);
    }

    public String setBrowseID(Array array) {
        String s = "";
        for (int i = array.size(); i > 0; i--) {
            if (i < array.size()) {
                s = s + '/';
            }
            s = s + escapeString((String) array.at(i - 1));
        }

        return s;
    }

    public Array setBrowseID(String s) {
        return parseString(s);
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        }
        if (stringproperty == pPostMetricsFrequency) {
            String s2 = httprequest
                    .getValue(stringproperty.getName() + "Units");
            int i = FrequencyProperty.toSeconds(TextUtils.toInt(s), s2);
            if (i < 300) {
                hashmap.put(stringproperty, i + "  was less than 5 minutes");
            }
            return String.valueOf(i);
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getHostname() {
        return getProperty(pHostname);
    }

    public Array getConnectionProperties() {
        Array array = getConnectionStandardProperties();
        Array array1 = getConnectionAdvancedProperties();
        for (int i = 0; i < array1.size(); i++) {
            array.add(array1.at(i));
        }

        return array;
    }

    public Array getConnectionStandardProperties() {
        Array array = new Array();
        array.add(pHostname);
        return array;
    }

    public Array getConnectionAdvancedProperties() {
        Array array = new Array();
        array.add(pSecure);
        array.add(pProxy);
        array.add(pProxyUser);
        array.add(pProxyPassword);
        array.add(pUser);
        array.add(pPassword);
        return array;
    }

    protected boolean doRequest(String metricDataURL, String metricDataXSL,
            String customContent, StringBuffer stringbuffer) {
        if (metricDataURL == null) {
            return true;
        }
        SocketSession socketsession = SocketSession.getSession(this);
        String s3 = getProperty(pProxy);
        String s4 = getProperty(pProxyUser);
        String s5 = getProperty(pProxyPassword);
        String s6 = getProperty(pUser);
        String s7 = getProperty(pPassword);
        StringBuffer stringbuffer1 = new StringBuffer(10000);
        boolean flag = getPropertyAsBoolean(pSecure);
        String s8 = (flag ? "https" : "http") + "://" + getProperty(pHostname)
                + ":" + getPort() + metricDataURL;
        Array array = null;
        if (customContent != null) {
            array = new Array();
            array.add("Custom-Content: " + customContent);
        }
        long l = System.currentTimeMillis();
        long al[] = URLMonitor.checkURL(socketsession, s8, null, null, s3, s4,
                s5, array, s6, s7, null, stringbuffer1, 0x989680L, null, 0,
                getPropertyAsInteger(pTimeout) * 1000, null);
        boolean flag1 = false;
        String s9 = URLMonitor.getHTTPContent(stringbuffer1.toString());
        l = System.currentTimeMillis() - l;
        networkTime += l;
        if (al[0] == 200L) {
            if (!s9.trim().startsWith("<")) {
                stringbuffer.append(s9);
                flag1 = true;
            } else {
                flag1 = transform(s9, metricDataXSL, stringbuffer);
            }
        } else {
            stringbuffer.append(URLMonitor.lookupStatus(al[0]));
        }
        return flag1;
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_XMLMonitorMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    static {
        nMaxCounters = 30;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_XMLMonitorMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(
                nMaxCounters, true);
        Array array = new Array();
        pHostname = new StringProperty("_server");
        pHostname.setDisplayText("Hostname", "the name of the server");
        pHostname.setParameterOptions(false, true, 4, false);
        array.add(pHostname);
        pSecure = new BooleanProperty("_secure");
        pSecure.setDisplayText("Secure Server",
                "check here if server is secure");
        pSecure.setParameterOptions(false, true, 4, false);
        array.add(pSecure);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("Proxy Server",
                "the name of the proxy server server if needed");
        pProxy.setParameterOptions(false, true, 4, false);
        array.add(pProxy);
        pProxyUser = new StringProperty("_proxyUser");
        pProxyUser.setDisplayText("Proxy Username",
                "the username for the proxy server");
        pProxyUser.setParameterOptions(false, true, 4, false);
        array.add(pProxyUser);
        pProxyPassword = new StringProperty("_proxyPassword");
        pProxyPassword.setDisplayText("Proxy Password",
                "the password for the proxy server");
        pProxyPassword.setParameterOptions(false, true, 4, false);
        array.add(pProxyPassword);
        pUser = new StringProperty("_user");
        pUser.setDisplayText("User", "the username for the URL");
        pUser.setParameterOptions(false, true, 4, false);
        array.add(pUser);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "the password for the URL");
        pPassword.setParameterOptions(false, true, 4, false);
        array.add(pPassword);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout",
                "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 19, true);
        array.add(pTimeout);
        pPostMetricsFrequency = new FrequencyProperty("_postMetricsFrequency",
                "600");
        pPostMetricsFrequency.setDisplayText(
                "Refresh Selected Metrics Frequency",
                "how often to tell server which metrics we want to monitor.");
        pPostMetricsFrequency.setParameterOptions(true, 20, true);
        array.add(pPostMetricsFrequency);
        pPostMetricsLast = new NumericProperty("_postMetricsLast", "0");
        pPostMetricsLast.setParameterOptions(false, 1, true);
        array.add(pPostMetricsLast);
        pStatus = new StringProperty("status");
        array.add(pStatus);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i++) {
            astringproperty1[i] = (StringProperty) array.at(i);
        }

        String s = (COM.dragonflow.SiteView.XMLMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length
                + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0,
                astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2,
                astringproperty1.length, astringproperty.length);
        addProperties(s, astringproperty2);
    }
}
