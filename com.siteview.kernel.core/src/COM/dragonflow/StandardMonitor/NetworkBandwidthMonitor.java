/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NetworkBandwidthMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;


import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableCache;
import COM.dragonflow.SiteView.BrowsableSNMPBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.RealTimeReportingData;
import COM.dragonflow.SiteView.RealTimeReportingMonitor;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SerializerUtils;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.Snmp.SNMPSession;
import COM.dragonflow.Utils.Snmp.SNMPSessionException;
import COM.dragonflow.Utils.Snmp.Monitoring.Device;
import COM.dragonflow.Utils.Snmp.Monitoring.IFTable;
import COM.dragonflow.Utils.Snmp.Monitoring.NetInterface;
import COM.dragonflow.Utils.Snmp.Monitoring.NetworkBandwidthConfig;

public class NetworkBandwidthMonitor extends BrowsableSNMPBase implements RealTimeReportingMonitor {

    private static final String PROPERTY_NAME_DELIMITER = "_";
    private static final String NO_DEVICE_TYPE = "NO_DEVICE";
    private static final String DEVICE_SPECIFIC_METRIC_ID = "_deviceSpecificMetricID";
    private static final String DEVICE_SPECIFIC_METRIC_NAME = "_deviceSpecificMetricName";
    private static final String DEVICE_SPECIFIC_METRIC_VALUE = "deviceSpecificMetricVal";
    private static final String DEVICE_SPECIFIC_RT_METRIC = "deviceSpecificRTMetric";
    private static final String SHOW_RT_TRAFFIC = "_showRTTraffic";
    private static final String INDEXING_METHOD = "_indexingMethod";
    private static final String DEVICE_TYPE = "_deviceType";
    private static final String DUPLEX_STATE = "_duplexState";
    private static final String MAX_VERT_AXIS = "_maxRTDataVerticalAxis";
    private static final String MAX_RT_DATA_WIN = "_maxRTDataWindow";
    private static final String MONITOR_VERSION = "_nbmonitorVersion";
    private static final String initialRTPropertyValue = "-1";
    private static final String getNextTestNode = ".1.3";
    private static StringProperty pShowRTTraffic;
    private static StringProperty pDuplexState;
    private static StringProperty pMaxRTDataWindow;
    private static StringProperty pMaxRTDataVerticalAxis;
    private static StringProperty pIndexingMethod;
    private static StringProperty pDeviceType;
    private static StringProperty pInterfaces[][];
    private static StringProperty pDeviceSpecificProperties[];
    private static StringProperty pDeviceSpecificIDs[];
    private static StringProperty pDeviceSpecificNames[];
    private static StringProperty pRTDeviceSpecificProps[];
    private static StringProperty pRTPropsPerInterface[][];
    private static StringProperty pLastRunTime;
    private static StringProperty pRTDeviceSpecificPropNameToLabel;
    private static StringProperty pDeviceSpecificPropNameToLabel;
    private static StringProperty pRTDeviceSpecificPropNameToGraphLabel;
    private static StringProperty pRTPropertiesOnSameGraphMap;
    private static StringProperty pLogOrStateProperties;
    private static StringProperty pMonitorVersion;

    private static final int VERSION_ONE = 1;
    private static final int VERSION_TWO = 2;
    private static int RTPropDefaultValue;
    private static int maxInterfaces;
    private static int maxDeviceSpecificProperties;
    private static String className;
    private SNMPSession session;

    private Vector interfaces;

    private IFTable ifTable;

    private Vector realTimeProps;

    private Device device;

    private int maxRealTimeWindow;

    private Object realTimeReportLock;

    private static final String VERSION_PREFIX = "Version:";

    private static boolean realTimeDataDisabled;

    public NetworkBandwidthMonitor() {
        realTimeProps = new Vector();
        realTimeReportLock = new Object();
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        if (getPropertyAsInteger(pMonitorVersion) < 1) {
            checkAndSetVersion();
        }
        stringbuffer.setLength(0);
        if (session == null) {
            try {
                session = obtainSession();
            } catch (SNMPSessionException snmpsessionexception) {
                LogManager.log("RunMonitor", "Network Bandwidth Monitor (SNMPSession Exception: " + snmpsessionexception.getMessage() + ")");
                stringbuffer.append("Error creating SNMP session: " + snmpsessionexception.getMessage());
                return "Error";
            }
        }
        COM.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = session.getNext(".1.3");
        if (snmpvariablebinding == null) {
            stringbuffer.append("Could not retrieve data from ").append(getProperty(pServerName));
            stringbuffer.append(" on port ").append(getProperty(pPort)).append(" with the given connection parameters.");
            return "Error";
        }
        if (ifTable == null) {
            ifTable = new IFTable(session);
        }
        ifTable.refreshTable(stringbuffer);
        if (stringbuffer.length() > 0) {
            LogManager.log("Error", "Network Bandwidth Monitor could not refresh the IFTable: " + stringbuffer);
            stringbuffer.insert(0, "Could not get list of interfaces from ifTable: ");
            return "Error";
        }
        DocumentImpl documentimpl = new DocumentImpl();
        Element element = documentimpl.createElement("browse_data");
        documentimpl.appendChild(element);
        if (getPropertyAsInteger(pMonitorVersion) == 1) {
            Vector vector = ifTable.getActiveInterfaces(1);
            createVersionOneInterfaceList(vector, documentimpl, element);
        } else {
            Vector vector1 = ifTable.getActiveInterfaces(2);
            createInterfaceList(vector1, documentimpl, element);
        }
        OutputFormat outputformat = new OutputFormat();
        outputformat.setIndent(3);
        outputformat.setOmitXMLDeclaration(true);
        outputformat.setOmitDocumentType(true);
        XMLSerializer xmlserializer = new XMLSerializer(outputformat);
        StringWriter stringwriter = new StringWriter();
        try {
            xmlserializer.setOutputCharStream(stringwriter);
            xmlserializer.serialize(documentimpl);
        } catch (Exception exception) {
            LogManager.log("Error", "XML serialization failed: " + exception.toString());
        }
        return stringwriter.toString();
    }

    private void createVersionOneInterfaceList(Vector vector, Document document, Element element) {
        for (int i = 0; i < vector.size(); i ++) {
            NetInterface netinterface = (NetInterface) vector.get(i);
            Element element1 = document.createElement("counter");
            StringBuffer stringbuffer = (new StringBuffer("Interface: ")).append(netinterface.getDescription());
            if (netinterface.getPhysAddress().length() > 0) {
                stringbuffer.append(": ").append(netinterface.getPhysAddress());
            }
            element1.setAttribute("name", stringbuffer.toString());
            String s = Integer.toString(netinterface.getRow()) + " " + netinterface.getPhysAddress() + " " + netinterface.getDescription();
            element1.setAttribute("id", s);
            element.appendChild(element1);
        }

    }

    private void createInterfaceList(Vector vector, Document document, Element element) {
        for (int i = 0; i < vector.size(); i ++) {
            NetInterface netinterface = (NetInterface) vector.get(i);
            String s = netinterface.getDescription();
            if (s == null) {
                s = "";
            }
            String s1 = netinterface.getAlias();
            if (s1 == null) {
                s1 = "";
            }
            String s2 = netinterface.getName();
            if (s2 == null) {
                s2 = "";
            }
            String s3 = netinterface.getPhysAddress();
            if (s3 == null) {
                s3 = "";
            }
            Element element1 = document.createElement("counter");
            StringBuffer stringbuffer = new StringBuffer();
            StringBuffer stringbuffer1 = new StringBuffer();
            StringBuffer stringbuffer2 = new StringBuffer();
            stringbuffer.append("Interface ");
            String s4;
            if (s2.length() > 0) {
                stringbuffer.append(s2).append(" (").append(s).append(")");
                s4 = s2;
            } else {
                stringbuffer.append(netinterface.getRow()).append(" (").append(s).append(")");
                s4 = (new StringBuffer(stringbuffer.toString())).toString();
            }
            stringbuffer1.append("Version:").append(" ").append(2).append(" ").append(Integer.toString(netinterface.getRow())).append(" ").append(s3).append(" ").append(s4);
            stringbuffer2.append("Alias: ");
            if (s1.length() > 0) {
                stringbuffer2.append(s1);
            } else {
                stringbuffer2.append("(none) ");
            }
            stringbuffer2.append("\nDescription: ");
            if (s.length() > 0) {
                stringbuffer2.append(s);
            } else {
                stringbuffer2.append("(none) ");
            }
            stringbuffer2.append("\nPhysical Address: ");
            if (s3.length() > 0) {
                stringbuffer2.append(s3);
            } else {
                stringbuffer2.append("(none) ");
            }
            element1.setAttribute("name", stringbuffer.toString());
            element1.setAttribute("id", stringbuffer1.toString());
            element1.setAttribute("desc", stringbuffer2.toString());
            element.appendChild(element1);
        }

    }

    private int findNumInterfaces() {
        int i = 0;
        for (i = 0; i < nMaxCounters; i ++) {
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() == 0) {
                break;
            }
        }
        return i;
    }

    public Array getPropertiesToPassBetweenPages(HTTPRequest httprequest) {
        Array array = super.getPropertiesToPassBetweenPages(httprequest);
        array.add(pShowRTTraffic);
        array.add(pIndexingMethod);
        array.add(pDeviceType);
        array.add(pMaxRTDataVerticalAxis);
        array.add(pMaxRTDataWindow);
        array.add(pDuplexState);
        return array;
    }

    protected boolean update() {
        checkAndSetVersion();
        synchronized (realTimeReportLock) {
            setProperty(pLastRunTime, (new Date()).getTime());
            if (session == null) {
                try {
                    session = obtainSession();
                } catch (SNMPSessionException snmpsessionexception) {
                    setStateStringError("Network Bandwidth Monitor (SNMPSession Exception: " + snmpsessionexception.getMessage() + ")", "No Data: " + snmpsessionexception.getMessage());
                    return false;
                }

                if (!getProperty(pDeviceType).equals("NO_DEVICE")) {
                    reInitializeDevice();
                }
                ifTable = new IFTable(session);
                session.setTimeout(calculateTimeout());
                reInitializeInterfaceVector();
            }

            StringBuffer stringbuffer = new StringBuffer();
            if (!ifTable.refreshTable(stringbuffer)) {
                setStateStringError("Network Bandwidth Monitor: " + stringbuffer.toString(), "No Data: " + stringbuffer.toString());
                return false;
            }

            if (device != null && !device.refreshMetrics(stringbuffer)) {
                setStateStringError("Network Bandwidth Monitor: " + stringbuffer.toString(), "Could not get device specific metrics: " + stringbuffer.toString());
                return false;
            }

            int i = findNumInterfaces();
            if (i != interfaces.size()) {
                setStateStringError("Network Bandwidth Monitor error reading newly selected interfaces.", "No Data: " + stringbuffer.toString());
                return false;
            }

            for (int j = 0; j < i; j ++) {
                NetInterface netinterface = (NetInterface) interfaces.get(j);
                netinterface.setMetrics(ifTable);
                netinterface.setLastMetrics(pInterfaces[j], this);
            }

            if (stillActive()) {
                synchronized (this) {
                    maxRealTimeWindow = getPropertyAsInteger(pMaxRTDataWindow) * 60 * 60;
                    for (int k = 0; k < interfaces.size(); k ++) {
                        NetInterface netinterface1 = (NetInterface) interfaces.get(k);
                        for (int k1 = 0; k1 < pInterfaces[k].length; k1 ++) {
                            String s = netinterface1.getMetricByName(pInterfaces[k][k1].getName());
                            setProperty(pInterfaces[k][k1], s);
                        }

                        if (realTimeDataDisabled) {
                            continue;
                        }
                        for (int l1 = 0; l1 < pRTPropsPerInterface[k].length; l1 ++) {
                            String s1 = netinterface1.getMetricByName(pRTPropsPerInterface[k][l1].getName());
                            populateRealTimeProperty(pRTPropsPerInterface[k][l1], netinterface1.getTime(), s1);
                        }

                    }

                    if (device != null) {
                        device.setRealTimeDataWindow(maxRealTimeWindow);
                        device.populateRegularProperties(pDeviceSpecificProperties, this);
                        device.updateRegularPropertyNameToLabelMap(pDeviceSpecificPropNameToLabel, this);
                        if (!realTimeDataDisabled) {
                            device.populateRTProperties(pRTDeviceSpecificProps, this, pRTPropertiesOnSameGraphMap);
                            device.updateRTPropertyNameToLabelMap(pRTDeviceSpecificPropNameToLabel, this);
                            device.updateRTPropertyNameToGraphLabelMap(pRTDeviceSpecificPropNameToGraphLabel, this);
                        }
                    }
                    for (int l = interfaces.size(); l < nMaxCounters; l ++) {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), "noInterface");
                    }

                    int i1 = 0;
                    StringBuffer stringbuffer1 = new StringBuffer("Monitored interfaces:");
                    for (int j1 = 0; j1 < interfaces.size(); j1 ++) {
                        NetInterface netinterface2 = (NetInterface) interfaces.get(j1);
                        stringbuffer1.append(" ").append(netinterface2.getName()).append(": ");
                        if (netinterface2.getStatus() == 1L) {
                            stringbuffer1.append("up");
                            setProperty(PROPERTY_NAME_COUNTER_VALUE + (j1 + 1), "up");
                        } else {
                            stringbuffer1.append("down");
                            setProperty(PROPERTY_NAME_COUNTER_VALUE + (j1 + 1), "down");
                            i1 ++;
                        }
                        if (j1 < interfaces.size() - 1) {
                            stringbuffer1.append(",");
                        }
                    }

                    setProperty(pStateString, stringbuffer1);
                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), i1);
                }
            }
        }
        return true;
    }

    private long calculateTimeout() {
        int i = 0;
        if (ifTable != null) {
            i += ifTable.getNumRequests();
        }
        if (device != null) {
            i += device.getNumRequests();
        }
        long l = getPropertyAsLong(pTimeout);
        int j = getPropertyAsInteger(pNumRetries);
        long l1 = (l * 100L) / (long) (i + j + 1);
        if (l1 == 0L) {
            l1 = 100L;
            setProperty(pTimeout, 1 * (i + j + 1));
        }
        return l1;
    }

    private void setStateStringError(String s, String s1) {
        LogManager.log("Error", s);
        for (int i = 0; i < nMaxCounters; i ++) {
            setProperty(PROPERTY_NAME_COUNTER_VALUE + (i + 1), "n/a");
        }

        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
        setProperty(pNoData, "n/a");
        setProperty(pStateString, s1);
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pDuplexState) {
            Vector vector = new Vector();
            vector.addElement("full-duplex");
            vector.addElement("Full-Duplex");
            vector.addElement("half-duplex");
            vector.addElement("Half-Duplex");
            return vector;
        }
        if (scalarproperty == pDeviceType) {
            Vector vector1 = NetworkBandwidthConfig.getIDsandDisplayNames();
            vector1.insertElementAt(" Do not monitor device-specific metrics", 0);
            vector1.insertElementAt("NO_DEVICE", 0);
            return vector1;
        }
        if (scalarproperty == pIndexingMethod) {
            checkAndSetVersion();
            Vector vector2 = new Vector();
            vector2.addElement(Integer.toString(2));
            vector2.addElement("Indexed by ifTable Row Number");
            if (getPropertyAsInteger(pMonitorVersion) != 1) {
                vector2.addElement(Integer.toString(3));
                vector2.addElement("Indexed by Interface Name");
            }
            vector2.addElement(Integer.toString(1));
            vector2.addElement("Indexed by Physical Address");
            if (getPropertyAsInteger(pMonitorVersion) == 1) {
                vector2.addElement(Integer.toString(0));
                vector2.addElement("Indexed by Description");
            }
            return vector2;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    private void populateRealTimeProperty(StringProperty stringproperty, long l, String s) {
        String s1 = getProperty(stringproperty);
        Vector vector;
        if (s1.length() == 0 || s1.equals("-1")) {
            vector = new Vector();
        } else {
            vector = (Vector) SerializerUtils.decodeJavaObjectFromStringBase64(s1);
            truncateDataOutsideWindow(vector);
        }
        RealTimeReportingData realtimereportingdata = new RealTimeReportingData(l, s);
        vector.add(realtimereportingdata);
        setProperty(stringproperty, SerializerUtils.encodeObjectBase64(vector, false));
    }

    private void truncateDataOutsideWindow(Vector vector) {
        if (vector == null) {
            return;
        }
        long l = (new Date()).getTime() / 1000L;
        java.util.ListIterator listiterator = vector.listIterator();
        do {
            if (!listiterator.hasNext()) {
                break;
            }
            RealTimeReportingData realtimereportingdata = (RealTimeReportingData) listiterator.next();
            if (l - realtimereportingdata.getTime() <= (long) maxRealTimeWindow) {
                break;
            }
            listiterator.remove();
        } while (true);
    }

    private void reInitializeInterfaceVector() {
        checkAndSetVersion();
        if (interfaces == null) {
            interfaces = new Vector();
        } else {
            interfaces.clear();
        }
        int i = findNumInterfaces();
        for (int j = 0; j < i; j ++) {
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
            int k = parseRowFromCounter(s);
            if (k < 0) {
                LogManager.log("Error", "Invalid row number parsed from counter name in Network Bandwidth Monitor: " + k);
                continue;
            }
            String s1 = parseInterfaceNameFromCounter(s);
            String s2 = parsePhysAddressFromCounter(s);
            boolean flag = false;
            if (getPropertyAsInteger(pMonitorVersion) == 1) {
                flag = true;
            }
            NetInterface netinterface = new NetInterface(s1, s2, k, getProperty(pDuplexState), getPropertyAsInteger(pIndexingMethod), flag);
            interfaces.add(netinterface);
        }

    }

    private int parseRowFromCounter(String s) {
        byte byte0;
        byte byte1;
        if (getPropertyAsInteger(pMonitorVersion) == 1) {
            byte0 = 4;
            byte1 = 1;
        } else {
            byte0 = 6;
            byte1 = 3;
        }
        String as[] = s.split(" ", byte0);
        if (as.length < byte0) {
            return -1;
        }
        int i;
        try {
            i = Integer.parseInt(as[byte1]);
        } catch (NumberFormatException numberformatexception) {
            i = -1;
        }
        return i;
    }

    private String parseInterfaceNameFromCounter(String s) {
        byte byte0;
        byte byte1;
        if (getPropertyAsInteger(pMonitorVersion) == 1) {
            byte0 = 4;
            byte1 = 3;
        } else {
            byte0 = 6;
            byte1 = 5;
        }
        String as[] = s.split(" ", byte0);
        if (as.length < byte0) {
            return "";
        } else {
            return as[byte1];
        }
    }

    private String parsePhysAddressFromCounter(String s) {
        byte byte0;
        byte byte1;
        if (getPropertyAsInteger(pMonitorVersion) == 1) {
            byte0 = 4;
            byte1 = 2;
        } else {
            byte0 = 6;
            byte1 = 4;
        }
        String as[] = s.split(" ", byte0);
        if (as.length < byte0) {
            return "";
        } else {
            return as[byte1];
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public RealTimeReportingData[] getRealTimeData(StringProperty stringproperty) {
        RealTimeReportingData arealtimereportingdata[] = null;
        synchronized (realTimeReportLock) {
            Vector vector;
            vector = null;
            if (isDeviceSpecific(stringproperty)) {
                if (device == null && !getProperty(pDeviceType).equals("NO_DEVICE")) {
                    reInitializeDevice();
                }
                if (device != null) {
                    vector = device.getRTDataFromProperty(getProperty(stringproperty));
                }
            } else {
                vector = (Vector) SerializerUtils.decodeJavaObjectFromStringBase64(getProperty(stringproperty));
            }
            if (vector == null || vector.size() == 0) {
                return null;
            }
            arealtimereportingdata = new RealTimeReportingData[vector.size()];
            vector.copyInto(arealtimereportingdata);
            return arealtimereportingdata;
        }
    }

    public long lastRunTime() {
        long l;
        synchronized (realTimeReportLock) {
            l = getPropertyAsLong(pLastRunTime);
        }
        return l;
    }

    public String getRTGraphLabel(StringProperty stringproperty) {
        String s;
        if (isDeviceSpecific(stringproperty)) {
            if (device != null) {
                return device.getRTGraphLabel(stringproperty.getName());
            } else {
                return GetPropertyLabel(stringproperty);
            }
        }
        s = GetPropertyLabel(stringproperty);
        if (s.indexOf(": Bytes In") > 0 || s.indexOf(": Bytes Out") > 0) {
            int i = s.indexOf(':');
            if (i > 0) {
                return s.substring(0, i) + ": Traffic";
            }
        }
        return s;
    }

    public long getRTVerticalMax(StringProperty stringproperty) {
        if (stringproperty.getName().startsWith("percentBWUsed")) {
            return 100L;
        }
        long l;
        synchronized (realTimeReportLock) {
            l = getPropertyAsLong(pMaxRTDataVerticalAxis);
        }
        return l;
    }

    private boolean isDeviceSpecific(StringProperty stringproperty) {
        String s = stringproperty.getName();
        return s.startsWith("deviceSpecificMetricVal") || s.startsWith("deviceSpecificRTMetric");
    }

    private void getLogOrStateProperties(Array array) {
        getValidIFTableProperties(array);
        getDeviceSpecificProperties(array);
    }

    private void getDeviceSpecificProperties(Array array) {
        for (int i = 0; i < pDeviceSpecificIDs.length; i ++) {
            if (getProperty(pDeviceSpecificIDs[i]).length() > 0) {
                array.add(getPropertyObject("deviceSpecificMetricVal" + i));
            }
        }

    }

    private void getValidIFTableProperties(Array array) {
        if (findNumInterfaces() > 0 && getProperty(pLogOrStateProperties).length() == 0) {
            StringBuffer stringbuffer = new StringBuffer();
            storeValidIFTableProperties(stringbuffer);
            if (stringbuffer.length() != 0) {
                LogManager.log("Error", stringbuffer.toString());
            }
        }
        String as[] = getProperty(pLogOrStateProperties).split(",");
        for (int i = 0; i < as.length; i ++) {
            StringProperty stringproperty = getPropertyObject(as[i]);
            if (stringproperty != null) {
                array.add(stringproperty);
            }
        }

    }

    private void storeValidIFTableProperties(StringBuffer stringbuffer) {
        if (session == null) {
            try {
                session = obtainSession();
            } catch (SNMPSessionException snmpsessionexception) {
                stringbuffer.append("Could not instantiate SNMPSession while verifying Network Bandwidth Monitor properties: " + snmpsessionexception.getMessage());
                return;
            }
        }
        ifTable = new IFTable(session);
        ifTable.refreshTable(stringbuffer);
        if (stringbuffer.length() > 0) {
            return;
        }
        if (ifTable.getError().length() > 0) {
            LogManager.log("RunMonitor", "IFTable error while verifying Network Bandwidth Monitor properties: " + ifTable.getError());
        }
        reInitializeInterfaceVector();
        StringBuffer stringbuffer1 = new StringBuffer("");
        int i = interfaces.size();
        for (int j = 0; j < i; j ++) {
            NetInterface netinterface = (NetInterface) interfaces.get(j);
            netinterface.setMetrics(ifTable);
            for (int k = 0; k < pInterfaces[j].length; k ++) {
                String s = pInterfaces[j][k].getName();
                if (netinterface.isStateProperty(s)) {
                    pInterfaces[j][k].setIsStateProperty(true);
                    stringbuffer1.append(s).append(",");
                }
            }

        }

        setProperty(pLogOrStateProperties, stringbuffer1.toString());
    }

    public String getTestURL() {
        String s = I18N.toDefaultEncoding(getProperty(pGroupID));
        s = HTTPRequest.encodeString(s);
        String s1 = I18N.toDefaultEncoding(getProperty(pID));
        String s2 = getHistoryParam(s1, s);
        StringBuffer stringbuffer = new StringBuffer("/SiteView/cgi/go.exe/SiteView?");
        stringbuffer.append("page=adhocReport");
        stringbuffer.append("&realTime=true");
        stringbuffer.append("&").append(s2);
        stringbuffer.append("&group=").append(s);
        stringbuffer.append("&reportType=lineGraph,similarProperties");
        stringbuffer.append("&showGraphs=true");
        stringbuffer.append("&title=").append(URLEncoder.encode("Bandwidth Metrics for " + getProperty(pServerName)));
        stringbuffer.append("&realTimeRefresh=").append(getPropertyAsInteger(pFrequency));
        stringbuffer.append("&precision=").append(getPropertyAsInteger(pFrequency));
        stringbuffer.append(" target=").append(s2);
        return stringbuffer.toString();
    }

    public String getTestTitle() {
        return "Real-Time Metrics";
    }

    public Enumeration getRealTimeProperties() {
        realTimeProps.clear();
        boolean flag = getPropertyAsBoolean(pShowRTTraffic);
        if (interfaces == null) {
            reInitializeInterfaceVector();
        }
        int i = interfaces.size();
        for (int j = 0; j < i; j ++) {
            for (int l = 0; l < pRTPropsPerInterface[j].length; l ++) {
                StringProperty stringproperty = pRTPropsPerInterface[j][l];
                if (stringproperty.getName().startsWith("percentBWUsed")) {
                    realTimeProps.add(stringproperty);
                    continue;
                }
                if (flag) {
                    realTimeProps.add(stringproperty);
                }
            }

        }

        if (!getProperty(pDeviceType).equals("NO_DEVICE")) {
            for (int k = 0; k < pRTDeviceSpecificProps.length && getPropertyAsInteger(pRTDeviceSpecificProps[k]) != RTPropDefaultValue; k ++) {
                realTimeProps.add(pRTDeviceSpecificProps[k]);
            }

        }
        return realTimeProps.elements();
    }

    public StringProperty[] getPropertiesOnSameGraph(StringProperty stringproperty) {
        if (stringproperty.getName().startsWith("deviceSpecificRTMetric")) {
            if (device == null) {
                return (new StringProperty[] { stringproperty });
            } else {
                return device.getPropertiesOnSameGraph(this, stringproperty, pRTPropertiesOnSameGraphMap);
            }
        } else {
            return NetInterface.getPropertiesOnSameGraph(stringproperty);
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        getLogOrStateProperties(array);
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        for (Enumeration enumeration = super.getStatePropertyObjects(flag); enumeration.hasMoreElements(); array.add(enumeration.nextElement())) {
        }
        getLogOrStateProperties(array);
        return array.elements();
    }

    public Array getProperties() {
        Array array = super.getProperties();
        array.remove(pMIB);
        array.remove(pCounterCalculationMode);
        return array;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        int i = parseInterfaceIndexFromPropName(stringproperty.getName());
        if (i >= 0) {
            if (interfaces == null) {
                reInitializeInterfaceVector();
            }
            if (i < interfaces.size()) {
                NetInterface netinterface = (NetInterface) interfaces.get(i);
                return netinterface.getPropertyLabel(parseInterfaceNameFromPropName(stringproperty.getName()));
            }
        } else {
            if (stringproperty.getName().startsWith("deviceSpecificMetricVal")) {
                int j = TextUtils.toInt(stringproperty.getName().substring("deviceSpecificMetricVal".length()));
                return getProperty("_deviceSpecificMetricName" + j);
            }
            if (stringproperty.getName().startsWith("deviceSpecificRTMetric") && (device != null || (device = NetworkBandwidthConfig.getDeviceInstance(getProperty(pDeviceType), session, maxRealTimeWindow)) != null)) {
                String s = device.getLabelFromPropertyName(stringproperty.getName());
                if (s != null && !s.equals("")) {
                    return s;
                }
            }
        }
        return super.GetPropertyLabel(stringproperty, flag);
    }

    private String parseInterfaceNameFromPropName(String s) {
        String as[] = s.split("_");
        if (as.length >= 2) {
            return as[0];
        } else {
            return s;
        }
    }

    private int parseInterfaceIndexFromPropName(String s) {
        String as[] = s.split("_");
        if (as.length >= 2) {
            int i;
            try {
                i = Integer.parseInt(as[1]);
            } catch (NumberFormatException numberformatexception) {
                return -1;
            }
            return i;
        } else {
            return -1;
        }
    }

    private void reInitializeDevice() {
        device = NetworkBandwidthConfig.getDeviceInstance(getProperty(pDeviceType), session, maxRealTimeWindow);
        if (device != null) {
            device.setRegularyPropertyNameToLabelMap(pDeviceSpecificPropNameToLabel, this);
            device.setRTPropertyNameToLabelMap(pRTDeviceSpecificPropNameToLabel, this);
            device.setRTPropertyNameToGraphLabelMap(pRTDeviceSpecificPropNameToGraphLabel, this);
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pMaxRTDataWindow) {
            int i;
            try {
                i = Integer.parseInt(s);
            } catch (Exception exception) {
                hashmap.put(stringproperty, "The Real-Time Data Time Window must be a number between 1 and 24.");
                return s;
            }
            if (i > 24 || i < 1) {
                hashmap.put(stringproperty, "The Real-Time Data Time Window must be a number between 1 and 24.");
            }
            return s;
        }
        if (stringproperty == pMaxRTDataVerticalAxis) {
            if (s.equals("")) {
                return s;
            }
            int j;
            try {
                j = Integer.parseInt(s);
            } catch (Exception exception1) {
                hashmap.put(stringproperty, "The Real-Time Data Vertical Axis must be a number greater than 0.");
                return s;
            }
            if (j <= 0) {
                hashmap.put(stringproperty, "The Real-Time Data Vertical Axis must be a number greater than 0.");
            }
            return s;
        }
        if (stringproperty == pMonitorVersion && s.equals("")) {
            checkAndSetVersion();
            return getProperty(pMonitorVersion);
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public void verifyAll(HashMap hashmap) {
        checkAndSetVersion();
        try {
            session = obtainSession();
        } catch (SNMPSessionException snmpsessionexception) {
            StringBuffer stringbuffer = (new StringBuffer("Failed to create SNMP session for ")).append(getProperty(pServerName));
            stringbuffer.append(": ").append(snmpsessionexception.getMessage());
            hashmap.put(pServerName, stringbuffer.toString());
            return;
        }
        COM.dragonflow.Utils.Snmp.SNMPVariableBinding snmpvariablebinding = session.getNext(".1.3");
        if (snmpvariablebinding == null) {
            StringBuffer stringbuffer1 = (new StringBuffer("Could not retrieve data from ")).append(getProperty(pServerName));
            stringbuffer1.append(" on port ").append(getProperty(pPort)).append(" with the given connection parameters.");
            hashmap.put(pServerName, stringbuffer1.toString());
            return;
        }
        StringBuffer stringbuffer2 = new StringBuffer();
        if (!getProperty(pDeviceType).equals("NO_DEVICE")) {
            device = NetworkBandwidthConfig.getDeviceInstance(getProperty(pDeviceType), session, maxRealTimeWindow);
            if (device == null) {
                StringBuffer stringbuffer3 = new StringBuffer("There is an error in the device-specific config file (");
                stringbuffer3.append(NetworkBandwidthConfig.configFilePath).append(").");
                hashmap.put(pDeviceType, stringbuffer3.toString());
                return;
            }
            int i = device.populateNamesAndIDs(pDeviceSpecificNames, pDeviceSpecificIDs, this, stringbuffer2);
            if (stringbuffer2.length() > 0 || i == 0) {
                hashmap.put(pDeviceType, "Could not find any device-specific metrics on this device.");
                return;
            }
        }
        storeValidIFTableProperties(stringbuffer2);
        if (stringbuffer2.length() > 0) {
            StringBuffer stringbuffer4 = (new StringBuffer("Error while updating list of valid objects from ifTable: ")).append(stringbuffer2);
            hashmap.put(getPropertyObject(PROPERTY_NAME_BROWSABLE), stringbuffer4.toString());
        }
        if (getPropertyAsInteger(pIndexingMethod) == 3 && ifTable != null && !ifTable.isIfNameObjectAvailable()) {
            hashmap.put(pIndexingMethod, "This device does not support indexing by ifName (ifName is not implemented).");
            return;
        } else {
            super.verifyAll(hashmap);
            return;
        }
    }

    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        super.onMonitorCreateFromPage(httprequest);
        String s = httprequest.getValue("_server");
        boolean flag = s.length() > 0;
        String uniqueID = httprequest.getValue("uniqueID");
        String s2 = httprequest.getValue("_indexingMethod");
        String s3 = httprequest.getValue("_maxRTDataWindow");
        String s4 = httprequest.getValue("_maxRTDataVerticalAxis");
        String s5 = httprequest.getValue("_duplexState");
        String deviceType = httprequest.getValue("_deviceType");
        String showRTTraffic = httprequest.getValue("_showRTTraffic");
        if (uniqueID.length() <= 0) {
            return;
        }
        if (flag) {
            HashMap hashmap = BrowsableCache.getCache(uniqueID, true, false);
            if (hashmap != null) {
                HashMap hashmap3 = (HashMap) hashmap.get("mProp");
                Object obj = hashmap3.get("_server");
                if (obj != null && !obj.equals(s)) {
                    BrowsableCache.getCache(uniqueID, false, true);
                    HashMap hashmap1 = BrowsableCache.getCache(uniqueID, true, false);
                    hashmap3 = (HashMap) hashmap1.get("mProp");
                }
                hashmap3.put("_indexingMethod", s2);
                hashmap3.put("_maxRTDataWindow", s3);
                hashmap3.put("_maxRTDataVerticalAxis", s4);
                hashmap3.put("_duplexState", s5);
                hashmap3.put("_deviceType", deviceType);
                hashmap3.put("_showRTTraffic", showRTTraffic);
                BrowsableCache.saveCache(uniqueID);
            }
        } else {
            HashMap hashmap2 = BrowsableCache.getCache(uniqueID, false, false);
            if (hashmap2 != null) {
                HashMap hashmap4 = (HashMap) hashmap2.get("mProp");
                String s8;
                for (Enumeration enumeration = hashmap4.keys(); enumeration.hasMoreElements(); setProperty(s8, (String) hashmap4.get(s8))) {
                    s8 = (String) enumeration.nextElement();
                }

            }
        }
    }

    public Array getConnectionProperties() {
        Array array = super.getConnectionProperties();
        array.add(pMonitorVersion);
        return array;
    }

    private void checkAndSetVersion() {
        if (getPropertyAsInteger(pMonitorVersion) < 1) {
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s.length() == 0) {
                setProperty(pMonitorVersion, 2);
                return;
            }
            String as[] = s.split(" ", 4);
            int i;
            if (as.length >= 3 && as[1].equals("Version:")) {
                try {
                    i = Integer.parseInt(as[2]);
                } catch (NumberFormatException numberformatexception) {
                    i = 1;
                }
            } else {
                i = 1;
            }
            setProperty(pMonitorVersion, i);
        }
    }

    static {
        RTPropDefaultValue = -1;
        maxInterfaces = nMaxCounters = 10;
        className = "COM.dragonflow.StandardMonitor.NetworkBandwidthMonitor";
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (TextUtils.getValue(hashmap, "_disableNetworkBandwidthRealTimeData").length() > 0) {
            realTimeDataDisabled = true;
        }
        Vector vector = new Vector();
        int i = 0;
        pDeviceType = new ScalarProperty("_deviceType");
        pDeviceType.setDisplayText("Device Type", "by specifying a device type, you will enable the Network Bandwidth monitor to watch certain device-specific metrics");
        pDeviceType.setParameterOptions(true, i ++, true);
        vector.add(pDeviceType);
        pDuplexState = new ScalarProperty("_duplexState");
        pDuplexState.setParameterOptions(true, i ++, true);
        pDuplexState.setDisplayText("Duplex or Half-Duplex", "the duplex state to use when calculating percent bandwidth utilized for all selected interfaces on this device");
        vector.add(pDuplexState);
        pIndexingMethod = new ScalarProperty("_indexingMethod");
        pIndexingMethod.setParameterOptions(true, i ++, true);
        pIndexingMethod
                .setDisplayText(
                        "Interface Index",
                        "specify how SiteView should keep track of the interfaces it is monitoring.  Some network devices do not persist the index used to identify a particular interface between reboots.  The interface description or physical address will usually remain the same, however.");
        vector.add(pIndexingMethod);
        pShowRTTraffic = new BooleanProperty("_showRTTraffic");
        pShowRTTraffic.setParameterOptions(true, i ++, true);
        pShowRTTraffic.setDisplayText("Show Bytes In/Out", "display a graph for bytes in/out along with percent bandwidth utilized on the  Real-Time Metrics page");
        vector.add(pShowRTTraffic);
        pMaxRTDataWindow = new NumericProperty("_maxRTDataWindow", "24", "hours");
        pMaxRTDataWindow.setParameterOptions(true, i ++, true);
        pMaxRTDataWindow.setDisplayText("Real-Time Data Time Window", "the number of hours for which real-time graph data should be stored");
        vector.add(pMaxRTDataWindow);
        pMaxRTDataVerticalAxis = new NumericProperty("_maxRTDataVerticalAxis", "");
        pMaxRTDataVerticalAxis.setParameterOptions(true, i ++, true);
        pMaxRTDataVerticalAxis.setDisplayText("Real-Time Data Vertical Axis", "the maximum value on the vertical axis for real-time graphs (leave blank to have this automatically calculated)");
        vector.add(pMaxRTDataVerticalAxis);
        pMonitorVersion = new NumericProperty("_nbmonitorVersion", "-1");
        pMonitorVersion.setEditable(false);
        pMonitorVersion.setConfigurable(true);
        vector.add(pMonitorVersion);
        pInterfaces = new StringProperty[maxInterfaces][];
        for (int j = 0; j < maxInterfaces; j ++) {
            pInterfaces[j] = NetInterface.createRegularProperties("_" + j, vector);
        }

        pRTPropsPerInterface = new StringProperty[maxInterfaces][];
        for (int k = 0; k < maxInterfaces; k ++) {
            pRTPropsPerInterface[k] = NetInterface.createRealTimeProperties("_" + k, vector, "-1");
        }

        maxDeviceSpecificProperties = NetworkBandwidthConfig.getMaxProperties();
        if (maxDeviceSpecificProperties > 0) {
            pDeviceSpecificProperties = new StringProperty[maxDeviceSpecificProperties];
            for (int l = 0; l < maxDeviceSpecificProperties; l ++) {
                pDeviceSpecificProperties[l] = new StringProperty("deviceSpecificMetricVal" + l);
                pDeviceSpecificProperties[l].isStateProperty = false;
                vector.add(pDeviceSpecificProperties[l]);
            }

            pDeviceSpecificNames = new StringProperty[maxDeviceSpecificProperties];
            for (int i1 = 0; i1 < maxDeviceSpecificProperties; i1 ++) {
                pDeviceSpecificNames[i1] = new StringProperty("_deviceSpecificMetricName" + i1);
                pDeviceSpecificNames[i1].isStateProperty = false;
                vector.add(pDeviceSpecificNames[i1]);
            }

            pDeviceSpecificIDs = new StringProperty[maxDeviceSpecificProperties];
            for (int j1 = 0; j1 < maxDeviceSpecificProperties; j1 ++) {
                pDeviceSpecificIDs[j1] = new StringProperty("_deviceSpecificMetricID" + j1);
                pDeviceSpecificIDs[j1].isStateProperty = false;
                vector.add(pDeviceSpecificIDs[j1]);
            }

            pRTDeviceSpecificProps = new StringProperty[maxDeviceSpecificProperties];
            for (int k1 = 0; k1 < maxDeviceSpecificProperties; k1 ++) {
                pRTDeviceSpecificProps[k1] = new NumericProperty("deviceSpecificRTMetric" + k1, Integer.toString(RTPropDefaultValue));
                pRTDeviceSpecificProps[k1].isStateProperty = false;
                vector.add(pRTDeviceSpecificProps[k1]);
            }

        }
        pLastRunTime = new NumericProperty("lastRunTime");
        vector.add(pLastRunTime);
        pDeviceSpecificPropNameToLabel = new StringProperty("NameToLabelMap");
        vector.add(pDeviceSpecificPropNameToLabel);
        pRTDeviceSpecificPropNameToLabel = new StringProperty("RTNameToLabelMap");
        vector.add(pRTDeviceSpecificPropNameToLabel);
        pRTDeviceSpecificPropNameToGraphLabel = new StringProperty("RTNameToGraphLabelMap");
        vector.add(pRTDeviceSpecificPropNameToGraphLabel);
        pRTPropertiesOnSameGraphMap = new StringProperty("RTPropertiesOnSameGraphMap");
        vector.add(pRTPropertiesOnSameGraphMap);
        pLogOrStateProperties = new StringProperty("_logOrStateProperties");
        vector.add(pLogOrStateProperties);
        StringProperty astringproperty[] = new StringProperty[vector.size()];
        for (int l1 = 0; l1 < vector.size(); l1 ++) {
            astringproperty[l1] = (StringProperty) vector.get(l1);
        }

        addProperties(className, astringproperty);
        addClassElement(className, Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement(className, Rule.stringToClassifier("always\tgood"));
        setClassProperty(className, "description", "Uses SNMP to monitor the level of traffic through a network device.");
        setClassProperty(className, "help", "NetworkBandwidthMonitor.htm");
        setClassProperty(className, "title", "Network Bandwidth");
        setClassProperty(className, "class", "NetworkBandwidthMonitor");
        setClassProperty(className, "target", "_server");
        setClassProperty(className, "topazName", "Network Bandwidth Monitor");
        setClassProperty(className, "topazType", "System Resources");
        setClassProperty(className, "classType", "");
    }
}
