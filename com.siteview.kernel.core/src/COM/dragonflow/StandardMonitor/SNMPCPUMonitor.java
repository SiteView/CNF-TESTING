package COM.dragonflow.StandardMonitor;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

//import com.netaphor.snmp.CommunityTarget;
import com.netaphor.snmp.Counter;
import com.netaphor.snmp.Counter64;
//import com.netaphor.snmp.GenericAddress;
import com.netaphor.snmp.Integer32;
import com.netaphor.snmp.IpAddress;
//import com.netaphor.snmp.OID;
//import com.netaphor.snmp.OctetString;
//import com.netaphor.snmp.PDU;
import com.netaphor.snmp.SecureTarget;
import com.netaphor.snmp.Session;
//import com.netaphor.snmp.Target;
import com.netaphor.snmp.TimeTicks;
import com.netaphor.snmp.UnsignedInteger32;
import com.netaphor.snmp.UsmUser;
import com.netaphor.snmp.Variable;
//import com.netaphor.snmp.VariableBinding;
//import com.sun.xml.internal.ws.wsdl.writer.document.http.Address;

public class SNMPCPUMonitor extends AtomicMonitor{
	
	public static int port=161;
	
    static StringProperty pHost;

    static StringProperty pOID;

    static StringProperty pIndex;

    static StringProperty pCommunity;

    static StringProperty p1ContentMatch;

    static StringProperty p2OID;

    static StringProperty p2Index;

    static boolean hasSecondOID;

    static StringProperty pTimeout;

    static StringProperty pRetryDelay;

    static StringProperty pScale;

    static StringProperty pContentMatch;

    static StringProperty pUnits;

    static StringProperty pMeasurementDesc;

    static StringProperty pMeasureDelta;

    static StringProperty pMeasureRate;

    static StringProperty pPercentageBase;

    static StringProperty pPercentageDelta;

    static StringProperty pSNMPVersion;

    static StringProperty pSNMPV3Passoword;

    static StringProperty pSNMPV3Username;

    static StringProperty pStatus;

    static StringProperty pSNMPValue;

    static StringProperty pMatchValue;

    static StringProperty pSNMPMeasurement;

    static StringProperty pBaseMeasurement;

    static StringProperty pMeasurementTime;

    static StringProperty pGaugeMaximum;

    static String defaultValue;

    public static CounterLock snmpLock;

    HashMap labelsCache;

    private static Snmp snmp;
	private static Target target;
	
    public SNMPCPUMonitor() {
        labelsCache = null;
    }

    public String getTestURL() {
        String s = "/SiteView/cgi/go.exe/SiteView?page=SNMP&";
        s = s + "host=" + URLEncoder.encode(getProperty(pHost));
        s = s + "&index=" + URLEncoder.encode(getProperty(pIndex));
        s = s + "&nextoid=" + URLEncoder.encode(getProperty(pOID));
        s = s + "&community=" + URLEncoder.encode(getProperty(pCommunity));
        s = s + "&version=" + URLEncoder.encode(getProperty(pSNMPVersion));
        return s;
    }

    public String getHostname() {
        return getProperty(pHost);
    }

    public static String readSNMPValue(String s, String s1, int i, int j, String s2, StringBuffer stringbuffer, String s3, String s4, boolean flag, String s5) {
        return readSNMPValue(s, s1, i, j, s2, stringbuffer, s3, s4, flag, s5, "", "");
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param i
     * @param j
     * @param s2
     * @param stringbuffer
     * @param s3
     * @param s4
     * @param flag
     * @param s5
     * @param s6
     * @param s7
     * @return
     */
    public static String readSNMPValue(String s, String s1, int i, int j, String s2, StringBuffer stringbuffer, String s3, String s4, boolean flag, String s5, String s6, String s7) {
    	SNMPCPUMonitor spMonitor=new SNMPCPUMonitor();
    	snmp=spMonitor.createSnmp(s, port, s2,i, j);
    	List<String> list=getTree(s3);
    	StringBuilder sb=new StringBuilder();
    	for(int list_i=0;list_i<list.size();list_i++)
    	{
    		String str[]=list.get(list_i).split("&");
    		if(Integer.valueOf(str[1])>0)
    		{
	    		sb.append("core"+str[0]+" Utilization:");
	    		sb.append(str[1]+"%\t");
	    	}
    	}
    	 return sb.toString();
    }

    /**
     * CAUTION: Decompile by hand.
     * 
     * @return
     */
    synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            if (getProperty(pMeasurementDesc).length() > 0) {
                labelsCache.add(defaultValue, getProperty(pMeasurementDesc));
            } else {
                labelsCache.add(defaultValue, defaultValue);
            }
            Array array = getProperties();
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                StringProperty stringproperty = (StringProperty) enumeration.nextElement();
                if (stringproperty.isThreshold() && stringproperty.getName().indexOf("default") == -1) {
                    labelsCache.add(stringproperty.getLabel(), stringproperty.getLabel());
                }
            }
        }
        return labelsCache;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if (s1.length() != 0) {
            return s1;
        }
        if (flag) {
            return "";
        } else {
            return s;
        }
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if (s1.length() == 0) {
            s1 = s;
        }
        return s1;
    }

    /**
     * 
     */
    protected boolean update() {
        long l = getPropertyAsLong(pMeasurementTime);
        String s = getProperty(pHost);
        String s1 = getProperty(pCommunity);
        int i = getPropertyAsInteger(pTimeout);
        int j = getPropertyAsInteger(pRetryDelay);
        String s2 = getProperty(pName);
        String s3 = getProperty(pSNMPVersion);
        String s4 = getProperty(pSNMPV3Username);
        String s5 = getProperty(pSNMPV3Passoword);

		
        if (s3.length() <= 0) {
            s3 = "V1";
        }
        StringBuffer stringbuffer = new StringBuffer();
        String s6 = getProperty(pMeasurementDesc);
        boolean flag = p2OID != null && getProperty(p2OID).length() > 0 && getProperty(p1ContentMatch).length() > 0;
        String s7 = getProperty(pOID);
        String s8 = getProperty(pIndex);
        String s9 = "";
        String s12 = "";
        if (flag) {
            snmpLock.get();
            try {
                s6 = readSNMPValue(s, s2, i, j, s1, stringbuffer, s7, s8, false, s3, s4, s5);
                snmpLock.release();
            } catch (Exception exception) {
                snmpLock.release();
            } finally {
                snmpLock.release();
            }

            String s10 = s6;
            String s13 = stringbuffer.toString();
            if (s13.length() == 0) {
                String s16 = I18N.UnicodeToString(getProperty(p1ContentMatch), I18N.nullEncoding());
                if (s16.length() != 0) {
                    StringBuffer stringbuffer1 = new StringBuffer();
                    Array array = new Array();
                    int k = TextUtils.matchExpression(s10, s16, array, stringbuffer1);
                    if (k != Monitor.kURLok) {
                        String s18 = URLMonitor.getHTMLEncoding(s10);
                        k = TextUtils.matchExpression(s10, I18N.UnicodeToString(s16, s18), array, stringbuffer1);
                    }
                    String s14;
                    if (k != 200) {
                        s14 = "content match error, " + s10;
                    }
                    s7 = getProperty(p2OID);
                    s8 = "0";
                    if (array.size() > 0) {
                        s8 = (String) array.at(0);
                    }
                }
            }
        }

        snmpLock.get();
        try {
            s6 = readSNMPValue(s, s2, i, j, s1, stringbuffer, s7, s8, false, s3, s4, s5);
            snmpLock.release();
        } catch (Exception exception1) {
            snmpLock.release();
        } finally {
            snmpLock.release();
        }
        snmpLock.release();

        String s17 = "";
        long l1 = Platform.timeMillis();
        String s11 = s6;
        double d = TextUtils.toDouble(s11);
        boolean flag1 = false;
        String s15 = stringbuffer.toString();
        String s19 = "";
        if (s15.length() == 0 && getPropertyAsBoolean(pMeasureDelta)) {
            flag1 = true;
            String s20 = getProperty(pSNMPMeasurement);
            HashMap hashmap = MasterConfig.getMasterConfig();
            long l3 = TextUtils.toLong(TextUtils.getValue(hashmap, "_snmpMaxCounter"));
            if (l3 <= 0L) {
                l3 = 0x100000000L;
            }
            if (s20.length() == 0) {
                Platform.sleep(1000L);
                s20 = s6;
                s6 = readSNMPValue(s, s2, i, j, s1, stringbuffer, getProperty(pOID), getProperty(pIndex), false, s3, s4, s5);
                l1 = Platform.timeMillis();
                s15 = stringbuffer.toString();
            }
            if (s15.length() == 0) {
                d = TextUtils.toLong(s6) - TextUtils.toLong(s20);
                if (d < 0.0D) {
                    d += l3;
                }
            }
        }
        if (s15.length() == 0 && getPropertyAsBoolean(pMeasureRate)) {
            flag1 = true;
            double d1 = (double) (l1 - l) / 1000D;
            d /= d1;
        }
        if (s15.length() == 0) {
            String s21 = getProperty(pPercentageBase);
            if (s21.length() > 0) {
                flag1 = true;
                long l2;
                if (s21.indexOf('.') == -1) {
                    l2 = TextUtils.toLong(s21);
                } else {
                    l2 = TextUtils.toLong(readSNMPValue(s, s2, i, j, s1, stringbuffer, s21, getProperty(pIndex), false, s3, s4, s5));
                    s15 = stringbuffer.toString();
                    if (s15.length() == 0 && getPropertyAsBoolean(pPercentageDelta)) {
                        String s25 = getProperty(pBaseMeasurement);
                        if (s25.length() == 0) {
                            Platform.sleep(1000L);
                            s25 = s17;
                            s17 = readSNMPValue(s, s2, i, j, s1, stringbuffer, getProperty(pOID), getProperty(pIndex), false, s3, s4, s5);
                            l1 = Platform.timeMillis();
                            s15 = stringbuffer.toString();
                        }
                        if (s15.length() == 0) {
                            l2 -= TextUtils.toLong(s25);
                        }
                    }
                }
                if (s15.length() == 0) {
                    if (l2 == 0L) {
                        d = (0.0D / 0.0D);
                    } else {
                        d = (d / (double) l2) * 100D;
                    }
                    s19 = "true";
                }
            }
        }
        if (s15.length() == 0) {
            String s22 = getProperty(pScale);
            if (s22.length() != 0) {
                flag1 = true;
                double d2 = TextUtils.toDouble(s22);
                if (d2 != 0.0D) {
                    d /= d2;
                }
            }
        }
        if (s15.length() == 0) {
            String s23 = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
            if (s23.length() != 0) {
                StringBuffer stringbuffer2 = new StringBuffer();
                Array array1 = new Array();
                int i1 = TextUtils.matchExpression(s11, s23, array1, stringbuffer2);
                if (i1 != Monitor.kURLok) {
                    String s26 = URLMonitor.getHTMLEncoding(s11);
                    i1 = TextUtils.matchExpression(s11, I18N.UnicodeToString(s23, s26), array1, stringbuffer2);
                }
                if (i1 != 200) {
                    s15 = "content match error, " + s11;
                }
                if (array1.size() > 0) {
                    s11 = "matched " + array1.at(0);
                    setProperty(pMatchValue, array1.at(0));
                }
            }
        }
        if (stillActive()) {
            synchronized (this) {
                if (s15.length() == 0) {
                    setProperty(pMeasurementTime, l1);
                    if (flag1) {
                        s11 = TextUtils.floatToString((float) d, 2);
                    }
                    setProperty(pSNMPValue, s11);
                    setProperty(pSNMPMeasurement, s6);
                    setProperty(pBaseMeasurement, s17);
                    setProperty(pStatus, "ok");
                    if (getProperty(pGaugeMaximum).length() > 0) {
                        setProperty(pMeasurement, getMeasurement(pSNMPValue, getPropertyAsLong(pGaugeMaximum)));
                    }
                    if (getProperty(pUnits).length() > 0) {
                        s11 = s11 + " " + getProperty(pUnits);
                    }
                    if (s19.length() > 0) {
                        s11 = s11 + "%";
                    }
                    String s24 = getProperty(pMeasurementDesc) + " = " + s11;
                    setProperty(pStateString, I18N.StringToUnicode(s24, I18N.nullEncoding()));
                } else {
                    setProperty(pSNMPValue, "n/a");
                    setProperty(pSNMPMeasurement, "");
                    setProperty(pBaseMeasurement, "");
                    setProperty(pStatus, s15);
                    setProperty(pNoData, "n/a");
                    setProperty(pStateString, I18N.StringToUnicode(s15, I18N.nullEncoding()));
                    setProperty(pMeasurement, "0");
                }
            }
        }
        return true;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pSNMPValue);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pHost) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if (stringproperty == pPercentageBase) {
            if (!TextUtils.onlyChars(s, "0123456789.")) {
                hashmap.put(stringproperty, "must be a number or an object id");
            }
            return s;
        }
        if (stringproperty == pOID || stringproperty == p2OID) {
            if (!TextUtils.onlyChars(s, "0123456789.+")) {
                hashmap.put(stringproperty, "object ids must be of the form 1.3.6.1.2.1");
            }
            return s;
        }
        if (stringproperty == pIndex) {
            if (!TextUtils.onlyChars(s, "0123456789.")) {
                hashmap.put(stringproperty, "index must be a number");
            } else {
                String s1 = getProperty(stringproperty != pIndex ? p2OID : pOID);
                if (!s1.startsWith(".")) {
                    s1 = "." + s1;
                }
                if (s1.endsWith("+")) {
                    s1 = s1.substring(0, s1.length() - 1);
                }
                if (s.length() == 0) {
                    s = "0";
                }
                s1 = s1 + "." + s;
                OID oid = new OID(s1);
                if (oid.getValue() == null) {
                    hashmap.put(stringproperty != pIndex ? ((Object) (p2OID)) : ((Object) (pOID)), "Invalid OID");
                }
            }
            return s;
        }
        if (stringproperty == pScale) {
            if (!TextUtils.onlyChars(s, "0123456789.")) {
                hashmap.put(stringproperty, "scale must be a number");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String defaultTitle() {
        String s = super.defaultTitle() + " " + getOIDTitle(getProperty(pOID));
        String s1 = getProperty(pIndex);
        if (!s1.equals("0")) {
            s = s + "[" + s1 + "]";
        }
        return s;
    }

    String getOIDTitle(String s) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        for (Enumeration enumeration = siteviewgroup.getMultipleValues("_snmpcpuMonitor"); enumeration.hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            int i = s1.indexOf('\t');
            if (i != -1) {
                String s2 = s1.substring(0, i);
                String s3 = s1.substring(i + 1);
                if (s.equals(s2)) {
                    return s3;
                }
            }
        }

        return s;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pOID || scalarproperty == p2OID || scalarproperty == pPercentageBase) {
            Vector vector = new Vector();
            if (scalarproperty == pPercentageBase) {
                vector.addElement("");
                vector.addElement("no percentage base");
            }
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            Enumeration enumeration = siteviewgroup.getMultipleValues("_snmpcpuMonitor");
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                int i = s.indexOf('\t');
                if (i != -1) {
                    String s1 = s.substring(0, i);
                    String s2 = s.substring(i + 1);
                    vector.addElement(s1);
                    vector.addElement(s2);
                }
            } 
            return vector;
        }
        if (scalarproperty == pSNMPVersion) {
            Vector vector1 = new Vector();
            vector1.addElement("V1");
            vector1.addElement("V1");
            vector1.addElement("V2");
            vector1.addElement("V2");
            vector1.addElement("V3");
            vector1.addElement("V3");
            return vector1;
        }
        if (scalarproperty == pScale) {
            Vector vector2 = new Vector();
            vector2.addElement("");
            vector2.addElement("no scaling");
            vector2.addElement("6000");
            vector2.addElement("TimeTicks to minutes");
            vector2.addElement("360000");
            vector2.addElement("TimeTicks to hours");
            vector2.addElement("60");
            vector2.addElement("seconds to minutes");
            vector2.addElement("3600");
            vector2.addElement("seconds to hours");
            vector2.addElement(".125");
            vector2.addElement("bytes to bits");
            vector2.addElement("8");
            vector2.addElement("bits to bytes");
            vector2.addElement("1024");
            vector2.addElement("bytes to kilobytes");
            vector2.addElement("1048576");
            vector2.addElement("bytes to megabytes");
            vector2.addElement("10");
            vector2.addElement("divide by 10");
            vector2.addElement("100");
            vector2.addElement("divide by 100");
            vector2.addElement("1000");
            vector2.addElement("divide by 1000");
            vector2.addElement("10000");
            vector2.addElement("divide by 10000");
            vector2.addElement("100000");
            vector2.addElement("divide by 100000");
            return vector2;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    static {
        defaultValue = null;
        snmpLock = null;
        hasSecondOID = false;
        defaultValue = "value";
        pHost = new StringProperty("_host", "", "host name");
        pHost.setDisplayText("Host Name", "the IP address or host name from which the snmp value will be retrieved)");
        pHost.setParameterOptions(true, 1, false);
        pOID = new ScalarProperty("_oid");
        pOID.setDisplayText("Object ID", "the object ID of the main SNMP object to be queried (or click on <A href=/SiteView/cgi/go.exe/SiteView?page=mib TARGET=MIB>MIB</A> Help)");
        pOID.setParameterOptions(true, 2, false);
        ((ScalarProperty) pOID).allowOther = true;
        pIndex = new StringProperty("_oidIndex", "0");
        pIndex.setDisplayText("Index", "the index of the main SNMP object - for non-table object IDs, this is 0");
        pIndex.setParameterOptions(true, 3, false);
        try {
            HashMap hashmap = MasterConfig.getMasterConfig();
            if (TextUtils.getValue(hashmap, "_enableSecondSNMP").length() > 0) {
                hasSecondOID = true;
                p1ContentMatch = new StringProperty("_content1");
                p1ContentMatch
                        .setDisplayText(
                                "Secondary Match Content",
                                "match against main SNMP value, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>Used to setup secondary SNMP index. Example: /(\\d)/ will get the first digit and use it in secondary index.");
                p1ContentMatch.setParameterOptions(true, 4, false);
                p2OID = new ScalarProperty("_oid2");
                p2OID.setDisplayText("Secondary Object ID", "the object ID of the Secondary SNMP object to be queried (or click on <A href=/SiteView/cgi/go.exe/SiteView?page=mib TARGET=MIB>MIB</A> Help)");
                p2OID.setParameterOptions(true, 5, false);
                ((ScalarProperty) p2OID).allowOther = true;
                p2Index = new StringProperty("_oidIndex2", "0");
                p2Index.setDisplayText("Secondary Index", "the index of the Secondary SNMP object - for non-table object IDs, this is 0<br><b>Or $1 to substitute the above content match value.</b>");
            }
        } catch (Exception exception) {
        }
        pCommunity = new StringProperty("_community", "public");
        pCommunity.setDisplayText("Community", "the community the SNMP object");
        pCommunity.setParameterOptions(true, 7, false);
        pTimeout = new NumericProperty("_timeout", "5", "seconds");
        pTimeout.setDisplayText("Timeout", "the total time, in seconds, to wait for a successful reply");
        pTimeout.setParameterOptions(true, 5, true);
        pSNMPVersion = new ScalarProperty("_snmpversion");
        pSNMPVersion.setDisplayText("SNMP Version", " the version of the mib that the oid belongs to");
        pSNMPVersion.setParameterOptions(true, 6, true);
        pRetryDelay = new NumericProperty("_retryDelay", "1", "seconds");
        pRetryDelay.setDisplayText("Retry Delay", "the time, in seconds, to wait before retrying the request");
        pRetryDelay.setParameterOptions(true, 7, true);
        pScale = new ScalarProperty("_scale");
        pScale.setDisplayText("Scaling", "divide the value by a scaling factor");
        pScale.setParameterOptions(true, 8, true);
        ((ScalarProperty) pScale).allowOther = true;
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional, match against SNMP value, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
        pContentMatch.setParameterOptions(true, 9, true);
        pUnits = new StringProperty("_units");
        pUnits.setDisplayText("Units", "optional units string to append when displaying the value of this counter.");
        pUnits.setParameterOptions(true, 10, true);
        pMeasurementDesc = new StringProperty("_measurementDesc");
        pMeasurementDesc.setDisplayText("Measurement Label", "optional label for the status string and measurements column of reports.");
        pMeasurementDesc.setParameterOptions(true, 11, true);
        pMeasureDelta = new BooleanProperty("_measureDelta", "");
        pMeasureDelta.setDisplayText("Measure as Delta", "when selected, the measurement reported is the difference between the current value and the previous value");
        pMeasureDelta.setParameterOptions(true, 12, true);
        pMeasureRate = new BooleanProperty("_measureRate", "");
        pMeasureRate.setDisplayText("Measure as Rate per Second", "when selected, the measurement reported is divided by the number of seconds since the last measurement");
        pMeasureRate.setParameterOptions(true, 13, true);
        pPercentageBase = new ScalarProperty("_percentageBase");
        pPercentageBase.setDisplayText("Percentage Base", "optional, the measurement will be divided by this value to calculate a percentage, enter a number or SNMP object ID,  if an object ID if entered the Index from above will be used");
        pPercentageBase.setParameterOptions(true, 14, true);
        ((ScalarProperty) pPercentageBase).allowOther = true;
        pPercentageDelta = new BooleanProperty("_percentageDelta", "");
        pPercentageDelta.setDisplayText("Measure Base as Delta",
                "optional, calculate the Percentage Base as the difference between the current base and the previous base.  use this option when an SNMP object ID is used for Percentage Base and the object is not a fixed value");
        pPercentageDelta.setParameterOptions(true, 15, true);
        pGaugeMaximum = new StringProperty("_gaugeMax", "");
        pGaugeMaximum.setDisplayText("Gauge Maximum", "optional, enter a maximum value for this object ID.  the maximum is calculate to create the gauge display");
        pGaugeMaximum.setParameterOptions(true, 16, true);
        pSNMPV3Username = new StringProperty("_snmpv3username", "");
        pSNMPV3Username.setDisplayText("SNMP V3 Username", "Username to be used for authentication for SNMP V3");
        pSNMPV3Username.setParameterOptions(true, 17, true);
        pSNMPV3Passoword = new StringProperty("_snmpv3password", "");
        pSNMPV3Passoword.setDisplayText("SNMP V3 Password", "Password to be used for authentication for SNMP V3");
        pSNMPV3Passoword.setParameterOptions(true, 18, true);
        pSNMPV3Passoword.isPassword = true;
        pSNMPValue = new NumericProperty("snmpValue");
        pSNMPValue.setLabel(defaultValue);
        pSNMPValue.setStateOptions(1);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        pSNMPMeasurement = new NumericProperty("snmpMeasurement");
        pBaseMeasurement = new NumericProperty("baseMeasurement");
        pMeasurementTime = new NumericProperty("snmpTime");
        pStatus = new StringProperty("status", "no data");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        if (hasSecondOID) {
            StringProperty astringproperty[] = { pHost, pOID, pIndex, p1ContentMatch, p2OID, p2Index, pCommunity, pTimeout, pRetryDelay, pScale, pContentMatch, pUnits, pMeasurementDesc, pMeasureDelta, pPercentageDelta, pMeasureRate, pPercentageBase,
                    pGaugeMaximum, pStatus, pSNMPValue, pMeasurementTime, pBaseMeasurement, pSNMPMeasurement, pMatchValue, pSNMPVersion, pSNMPV3Passoword, pSNMPV3Username };
            addProperties("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", astringproperty);
        } else {
            StringProperty astringproperty1[] = { pHost, pOID, pIndex, pCommunity, pTimeout, pRetryDelay, pScale, pContentMatch, pUnits, pMeasurementDesc, pMeasureDelta, pPercentageDelta, pMeasureRate, pPercentageBase, pGaugeMaximum, pStatus, pSNMPValue,
                    pMeasurementTime, pBaseMeasurement, pSNMPMeasurement, pMatchValue, pSNMPVersion, pSNMPV3Passoword, pSNMPV3Username };
            addProperties("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", astringproperty1);
        }
        addClassElement("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", Rule.stringToClassifier("status == 'ok'\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "description", "Returns the value of an SNMP get.");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "help", "SNMPMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "title", "SNMPCPU");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "class", "SNMPCPUMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "target", "_host");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "topazName", "SNMP");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "toolName", "SNMP");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "toolDescription", "Walks an SNMP mib.");
        setClassProperty("COM.dragonflow.StandardMonitor.SNMPCPUMonitor", "topazType", "System Resources");
        try {
            HashMap hashmap1 = MasterConfig.getMasterConfig();
            int i = TextUtils.toInt(TextUtils.getValue(hashmap1, "_snmpcpuMonitorMaximum"));
            if (i <= 0) {
                i = 10;
            }
            if (snmpLock == null) {
                snmpLock = new CounterLock(i);
            }
            int j = TextUtils.toInt(TextUtils.getValue(hashmap1, "_defaultPrecision"));
            if (j > 0 && (pSNMPValue instanceof NumericProperty)) {
                ((NumericProperty) pSNMPValue).defaultPrecision = j;
            }
        } catch (Exception exception1) {
        }
    } 

	/**
	 * @param args
	 */
	public static List<String> getTree(String stOID){
		List<String> varibleTreeList=new Vector<String>();
		PDUFactory pduFactory = new DefaultPDUFactory(PDU.GET);
		TreeUtils treeUtil=new TreeUtils(snmp,pduFactory);
		List<TreeEvent> listEvent=treeUtil.getSubtree(target,new OID(stOID));
        for(TreeEvent treeEvent:listEvent){
        	org.snmp4j.smi.VariableBinding[] vbArray=treeEvent.getVariableBindings();
        	if(vbArray==null){
        		continue;
        	}
        	for(org.snmp4j.smi.VariableBinding var:vbArray){
        		int oid[]=var.getOid().getValue();
//        		varibleTreeList.add(var.toString());
        		varibleTreeList.add(oid[oid.length-1]+"&"+var.toValueString());
        	}
        }
        
        return varibleTreeList;
	}
	private Target getTarget(String stIP,int port,String community,int timeout,int retries) {
        Address targetAddress = GenericAddress.parse(stIP+"/"+port);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community)); 
        target.setAddress(targetAddress);
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(timeout);
        target.setRetries(retries);
        return target;
    }
	private Snmp createSnmp(String stIP,int port,String community,int timeout,int retries){
		try{
			
			
			target=getTarget(stIP,port,community,timeout,retries);
			if(target instanceof TcpAddress){
				snmp=new Snmp(new DefaultTcpTransportMapping());
			}else if(target instanceof UdpAddress){
				snmp=new Snmp(new DefaultUdpTransportMapping());
			}else{
				snmp=new Snmp(new DefaultUdpTransportMapping());
			}
			snmp.listen();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return snmp;
    }
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub 
//		
//		SNMPCPUMonitor spMonitor=new SNMPCPUMonitor();
////		snmp=createSnmp(stIP, port, community,timeout,retries);
//		snmp=spMonitor.createSnmp("192.168.0.248", 161, "dragon",1, 2); 
////		spMonitor.update();
//		List<String> list=spMonitor.getTree("1.3.6.1.2.1.2.2.1.1");
//		for(int i=0;i<list.size();i++)
//		{
//			System.out.println(list.get(i).toString());
//		}
//		//("192.168.0.248",161,"dragon")
//		//System.out.println(spMonitor.getEquipmentFactoryName());
//		//spMonitor.getAllArpTable();
//		//spMonitor.getAllInterfaceTable();
//		//spMonitor.getAllAtfTable();
//		//spMonitor.getAllBGP();
//		//spMonitor.getAllCDP();
////		spMonitor.getAllInterfaceTable();
//	}
	

}
