/*
 * 
 * Created on 2005-3-5 14:23:28
 *
 * BrowsableWMIMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>BrowsableWMIMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jgl.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.WMIUtils;
//import COM.dragonflow.WMIMonitor.xdr.property_pair;
//import COM.dragonflow.WMIMonitor.xdr.property_pair_seq;
//import COM.dragonflow.WMIMonitor.xdr.wmi_connect;
//import COM.dragonflow.WMIMonitor.xdr.wmi_instance_result;
//import COM.dragonflow.WMIMonitor.xdr.wmi_instance_seq;
//import COM.dragonflow.WMIMonitor.xdr.wmi_measurement;
//import COM.dragonflow.WMIMonitor.xdr.wmi_measurement_seq;
//import COM.dragonflow.WMIMonitor.xdr.wmi_run_measurements;
//import COM.dragonflow.WMIMonitor.xdr.wmi_string_seq;

public class BrowsableWMIMonitor extends BrowsableBase {

    static StringProperty pHost;

    static ScalarProperty pFile;

    static StringProperty pUsername;

    static StringProperty pPassword;

    static HashMap mCounterNodes;

    public static final String wmiFileDirName = "templates.wmi";

    static final String xmlFileExtension = ".XML";

    private static Array mConnectionProps;

    static int nMaxCounters;

    public BrowsableWMIMonitor() {
    }

    public String getHostname() {
        return getProperty(pHost);
    }

    protected boolean update() {
        if (mCounterNodes == null) {
            getBrowseData(new StringBuffer());
        }
//        wmi_measurement_seq wmi_measurement_seq1 = new wmi_measurement_seq();
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        for (int i = 0; i < nMaxCounters; i ++) {
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() <= 0) {
                break;
            }
            vector1.addElement(PROPERTY_NAME_COUNTER_VALUE + (i + 1));
            vector.addElement(getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1)));
            StringTokenizer stringtokenizer = new StringTokenizer(s, " ");
            stringtokenizer.nextToken();
            String s1 = stringtokenizer.nextToken();
            Element element = (Element) mCounterNodes.get(s1);
            if (element == null) {
                continue;
            }
//            wmi_measurement wmi_measurement1 = new wmi_measurement();
//            wmi_measurement_seq1.add(wmi_measurement1);
//            wmi_measurement1.set_nameSpace(element.getAttribute("nameSpace"));
//            String s2 = element.getAttribute("query");
//            if (s2.length() > 0) {
//                s2 = WMIUtils.applyDateFunctionsToQuery(s2);
//                wmi_measurement1.set_query(s2);
//            }
//            String s4 = element.getAttribute("function");
//            if (s4.equals("MAX")) {
//                wmi_measurement1.set_queryFunction(2);
//            } else if (s4.equals("MIN")) {
//                wmi_measurement1.set_queryFunction(3);
//            } else if (s4.equals("COUNT")) {
//                wmi_measurement1.set_queryFunction(1);
//            } else if (s4.equals("AVG")) {
//                wmi_measurement1.set_queryFunction(4);
//            } else if (s4.equals("SUM")) {
//                wmi_measurement1.set_queryFunction(5);
//            }
//            String s5 = element.getAttribute("properties");
//            if (s5.length() > 0) {
//                wmi_string_seq wmi_string_seq1 = new wmi_string_seq();
//                String s8;
//                for (StringTokenizer stringtokenizer1 = new StringTokenizer(s5, " ,"); stringtokenizer1.hasMoreTokens(); wmi_string_seq1.add(s8)) {
//                    s8 = stringtokenizer1.nextToken();
//                }
//
//                wmi_measurement1.set_properties(wmi_string_seq1);
//            }
//            String s6 = element.getAttribute("collapseBy");
//            if (s6.length() > 0) {
//                wmi_measurement1.set_collapseBy(s6);
//            }
//            String s7 = element.getAttribute("orderBy");
//            if (s7.length() > 0) {
//                wmi_measurement1.set_orderBy(s7);
//            }
//            String s9 = element.getAttribute("orderDirection");
//            if (s9.length() > 0) {
//                wmi_measurement1.set_orderDirection(s9);
//            }
//            String s10 = element.getAttribute("orderType");
//            if (s10.length() > 0) {
//                wmi_measurement1.set_orderType(s10);
//            }
//            String s11 = element.getAttribute("maxItems");
//            if (s11.length() <= 0) {
//                continue;
//            }
//            try {
//                wmi_measurement1.set_maxItems(Integer.parseInt(s11));
//            } catch (Exception exception) {
//            }
//        }
//
//        wmi_measurement_seq wmi_measurement_seq2 = new wmi_measurement_seq();
//        StringBuffer stringbuffer = new StringBuffer();
//        if (!WMIUtils.sendWmiRequest(6, new wmi_run_measurements(getConnectionInfo(), wmi_measurement_seq1), wmi_measurement_seq2, stringbuffer, 0)) {
//            setProperty(pNoData, "n/a");
//            setProperty(pStateString, stringbuffer.toString());
//            setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), "" + wmi_measurement_seq1.size());
//            return true;
//        }
//        int j = 0;
//        StringBuffer stringbuffer1 = new StringBuffer();
//        for (int k = 0; k < wmi_measurement_seq2.size(); k ++) {
//            wmi_measurement wmi_measurement2 = wmi_measurement_seq2.get(k);
//            String s3 = (String) vector.get(k);
//            int l = wmi_measurement2.get_result_type();
//            if (stringbuffer1.length() > 0) {
//                stringbuffer1.append(", ");
//            }
//            if (s3.length() > 0) {
//                stringbuffer1.append(s3).append("=");
//            }
//            if (l == 2 || l == 0) {
//                if (l == 2) {
//                    j ++;
//                }
//                stringbuffer1.append(wmi_measurement2.get_scalar_result());
//                setProperty((String) vector1.get(k), wmi_measurement2.get_scalar_result());
//                continue;
//            }
//            stringbuffer1.append(" [");
//            wmi_instance_seq wmi_instance_seq1 = wmi_measurement2.get_list_result();
//            int i1 = wmi_instance_seq1.size();
//            for (int j1 = 0; j1 < i1; j1 ++) {
//                stringbuffer1.append(" [");
//                wmi_instance_result wmi_instance_result1 = wmi_instance_seq1.get(j1);
//                property_pair_seq property_pair_seq1 = wmi_instance_result1.get_properties();
//                int k1 = property_pair_seq1.size();
//                for (int l1 = 0; l1 < k1; l1 ++) {
//                    property_pair property_pair1 = property_pair_seq1.get(l1);
//                    if (l1 > 0) {
//                        stringbuffer1.append(", ");
//                    }
//                    stringbuffer1.append(property_pair1.get_prop_key()).append("=").append(property_pair1.get_prop_value());
//                }
//
//                stringbuffer1.append("] ");
//            }
//
//            stringbuffer1.append("] ");
        }

//        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), j);
//        setProperty(pStateString, stringbuffer1.toString());
        return true;
    }

    public String defaultTitle() {
        return "WMI on " + getProperty(pHost);
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pHost) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public Array getConnectionProperties() {
        return mConnectionProps;
    }

    public String getBrowseFilename() {
        return Platform.getRoot() + File.separator + "templates.wmi" + File.separator + getProperty(pFile);
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public String getBrowseData(StringBuffer stringbuffer) {
        try {
            String s = getBrowseFilename();
            File file = new File(s);
            if (file.exists()) {
                byte abyte0[] = new byte[(int) file.length()];
                FileInputStream fileinputstream = new FileInputStream(file);
                fileinputstream.read(abyte0);
                String s1 = new String(abyte0);
                createCounterCache(s1);
                return s1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_BrowsableWMIMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public boolean isServerBased() {
        return true;
    }

//    private wmi_connect getConnectionInfo() {
//        return new wmi_connect(getProperty(pHost), getProperty(pUsername), getProperty(pPassword));
//    }

    private void createCounterCache(String s) {
        try {
            DocumentBuilder documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentbuilder.parse(new InputSource(new StringReader(s)));
            mCounterNodes = new HashMap();
            Element element = document.getDocumentElement();
            NodeList nodelist = element.getElementsByTagName("counter");
            for (int i = 0; i < nodelist.getLength(); i ++) {
                Element element1 = (Element) nodelist.item(i);
                mCounterNodes.put(element1.getAttribute("id"), element1);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isUsingCountersCache() {
        return true;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pFile) {
            File file = new File(Platform.getRoot() + File.separator + "templates.wmi" + File.separator);
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

    static {
        mConnectionProps = new Array();
        nMaxCounters = 10;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_BrowsableWMIMaxCounters"));
        if (nMaxCounters == 0) {
            nMaxCounters = 10;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        pHost = new StringProperty("_host");
        pHost.setDisplayText("Server", "");
        pHost.setParameterOptions(false, 1, false);
        pFile = new ScalarProperty("_browsefile");
        pFile.setDisplayText("File", "Setting file that specifies the WMI counters (from templates.wmi directory)");
        pFile.setParameterOptions(false, 2, false);
        pUsername = new StringProperty("_username");
        pUsername.setDisplayText("Username", "");
        pUsername.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "");
        pPassword.setParameterOptions(true, 4, false);
        mConnectionProps.add(pHost);
        mConnectionProps.add(pFile);
        StringProperty astringproperty1[] = { pHost, pFile, pUsername, pPassword };
        String s = (COM.dragonflow.StandardMonitor.BrowsableWMIMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        addProperties(s, astringproperty2);
        addClassElement(s, Rule.stringToClassifier(PROPERTY_NAME_COUNTERS_IN_ERROR + " > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "WMI metrics");
        setClassProperty(s, "title", "Browsable WMI metrics");
        setClassProperty(s, "class", "BrowsableWMIMonitor");
        setClassProperty(s, "loadable", "false");
    }
}