/*
 * 
 * Created on 2005-2-15 12:23:14
 *
 * BrowsableSNMPBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableSNMPBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.Snmp.BrowsableMIB;
import COM.dragonflow.Utils.Snmp.BrowsableMIBException;
import COM.dragonflow.Utils.Snmp.SNMPSession;
import COM.dragonflow.Utils.Snmp.SNMPSessionException;
import COM.dragonflow.Utils.Snmp.SNMPVariableBinding;
import Snmp.SnmpAPI;
import Snmp.SnmpException;
import Snmp.SnmpOID;
import Snmp.SnmpPDU;
import Snmp.SnmpSession;
import Snmp.SnmpVar;
import Snmp.SnmpVarBind;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, Platform, MasterConfig, BrowsableCache

public abstract class BrowsableSNMPBase extends BrowsableBase {

    private SNMPSession session;

    private BrowsableMIB mib;

    public static final String SERVER_PROPERTY_NAME = "_server";

    public static final String COMMUNITY_PROPERTY_NAME = "_community";

    public static final String TIMEOUT_PROPERTY_NAME = "_timeout";

    public static final String RETRIES_PROPERTY_NAME = "_retries";

    public static final String VERSION_PROPERTY_NAME = "_snmpversion";

    public static final String V3_AUTHTYPE_PROPERTY_NAME = "_snmpv3authtype";

    public static final String V3_AUTHPASSWD_PROPERTY_NAME = "_snmpv3authpassword";

    public static final String V3_USER_PROPERTY_NAME = "_snmpv3username";

    public static final String V3_PRIVPASSWD_PROPERTY_NAME = "_snmpv3privpassword";

    public static final String V3_CONTEXT_ENGINE_ID_PROPERTY_NAME = "_contextEngineID";

    public static final String V3_CONTEXT_NAME_PROPERTY_NAME = "_contextName";

    public static final String PORT_PROPERTY_NAME = "_port";

    public static final String MIBFILE_PROPERTY_NAME = "_mibfile";

    public static final String COUNTER_CALCULATION_PROPERTY_NAME = "_counterCalculationMode";

    public static final String LAST_MEASUREMENT_TIME_PROPERTY_NAME = "lastMeasurementTime";

    public static final String LAST_MEASUREMENT_PROPERTY_NAME = "lastMeasurement";

    private static final int DO_NO_CALCULATION = 0;

    private static final int DO_RATE_CALCULATION = 1;

    private static final int DO_DELTA_CALCULATION = 2;

    private static final long initialValue = -1L;

    protected static StringProperty pServerName;

    protected static StringProperty pCommunity;

    protected static StringProperty pTimeout;

    protected static StringProperty pNumRetries;

    protected static StringProperty pSNMPVersion;

    protected static StringProperty pV3AuthType;

    protected static StringProperty pV3AuthPassword;

    protected static StringProperty pV3Username;

    protected static StringProperty pV3PrivPassword;

    protected static StringProperty pV3ContextEngineID;

    protected static StringProperty pV3ContextName;

    protected static StringProperty pPort;

    protected static StringProperty pMIB;

    protected static StringProperty pCounterCalculationMode;

    protected static StringProperty pLastMeasurementTime;

    protected static StringProperty pLastMeasurements[];

    protected static int nMaxCounters;

    private static CounterLock snmpLock;

    static SnmpAPI cachedAPI = null;

    public BrowsableSNMPBase() {
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pServerName);
        array.add(pMIB);
        array.add(pPort);
        array.add(pCommunity);
        array.add(pTimeout);
        array.add(pNumRetries);
        array.add(pSNMPVersion);
        array.add(pV3AuthType);
        array.add(pV3Username);
        array.add(pV3AuthPassword);
        array.add(pV3PrivPassword);
        array.add(pV3ContextEngineID);
        array.add(pV3ContextName);
        return array;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pSNMPVersion) {
            Vector vector = new Vector();
            vector.addElement("1");
            vector.addElement("V1");
            vector.addElement("2");
            vector.addElement("V2");
            vector.addElement("3");
            vector.addElement("V3");
            return vector;
        }
        if (scalarproperty == pV3AuthType) {
            Vector vector1 = new Vector();
            vector1.addElement("MD5");
            vector1.addElement("MD5");
            vector1.addElement("SHA");
            vector1.addElement("SHA");
            vector1.addElement("NoAuthentication");
            vector1.addElement("None");
            return vector1;
        }
        if (scalarproperty == pCounterCalculationMode) {
            Vector vector2 = new Vector();
            vector2.addElement(Integer.toString(0));
            vector2.addElement("Do No Calculation");
            vector2.addElement(Integer.toString(2));
            vector2.addElement("Calculate Delta");
            vector2.addElement(Integer.toString(1));
            vector2.addElement("Calculate Rate");
            return vector2;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        stringbuffer.setLength(0);
        if (session == null) {
            try {
                session = obtainSession();
            } catch (SNMPSessionException snmpsessionexception) {
                stringbuffer.append("Error creating SNMP session: " + snmpsessionexception.getMessage());
                return "Error";
            }
        }
        String s = getProperty(pMIB);
        if (mib == null) {
            try {
                mib = BrowsableMIB.getInstance();
            } catch (BrowsableMIBException browsablemibexception) {
                stringbuffer.append("Error creating MIB from file: " + browsablemibexception.getMessage());
                return "Error";
            } catch (IOException ioexception) {
                stringbuffer.append("Error creating MIB from file: " + ioexception.getMessage());
                return "Error";
            }
        }
        return mib.getXML(session, s, stringbuffer);
    }

    /**
     * 
     */
    protected boolean update() {
        int i = 0;
        SNMPVariableBinding varbind[];
        if (this.session == null) {
            try {
                this.session = obtainSession();
            } catch (SNMPSessionException e) {
                LogManager.log("RunMonitor", "SNMP by MIB Monitor (SNMPSession Exception: " + e.getMessage() + ")");
                for (int k = 0; k < nMaxCounters; k++) {
                    setProperty(PROPERTY_NAME_COUNTER_VALUE + (k + 1), "n/a");
                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                    setProperty(pNoData, "n/a");
                    setProperty(pStateString, e.getMessage());
                }

                return true;
            }
        }

        for (i = 0; i < nMaxCounters; i++) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s1.length() == 0) {
                break;
            }
        }

        varbind = new SNMPVariableBinding[i];
        for (int j = 0; j < i; j++) {
            String s2 = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
            varbind[j] = new SNMPVariableBinding(s2.substring(s2.indexOf(" ") + 1, s2.length()));
        }

        String s;
        long l;
        try {
            snmpLock.get();
            varbind = session.get(varbind);
            l = (new Date()).getTime() / 1000L;
            s = session.getError();
            snmpLock.release();
        } catch (RuntimeException e) {
            snmpLock.release();
            throw e;
        }

        if (stillActive()) {
            synchronized (this) {
                StringBuffer stringbuffer = new StringBuffer("");
                int i1 = 0;
                if (varbind != null) {
                    int j1 = getPropertyAsInteger(pCounterCalculationMode);
                    for (int l1 = 0; l1 < i; l1++) {
                        int i2 = varbind[l1].getSyntax();
                        String s3;
                        String s4 = s3 = varbind[l1].getValue().replaceAll(",", "");
                        if (session.isErrorString(varbind[l1].getValue())) {
                            s4 = s3 = "n/a";
                            i1++;
                        } else if (j1 == 2 || j1 == 1 && (i2 == 65 || i2 == 70)) {
                            long l2 = getPropertyAsLong(pLastMeasurements[l1]);
                            long l3 = varbind[l1].getValueAsLong();
                            long l4 = l3 - l2;
                            if (j1 == 1) {
                                long l5 = getPropertyAsLong(pLastMeasurementTime);
                                long l6 = l - l5;
                                if (l6 != 0L && l4 != 0L) {
                                    s4 = Float.toString((float) l4 / (float) l6);
                                } else {
                                    s4 = "0";
                                }
                            } else {
                                s4 = Long.toString(l4);
                            }
                            if (getPropertyAsLong(pLastMeasurements[l1]) == -1L) {
                                s4 = "0";
                            }
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (l1 + 1), s4);
                        setProperty(pLastMeasurements[l1], s3);
                        String s5 = getProperty(PROPERTY_NAME_COUNTER_NAME + (l1 + 1));
                        stringbuffer.append(s5).append(" = ").append(s4);
                        if (l1 < i - 1) {
                            stringbuffer.append(", ");
                        }
                    }

                } else {
                    for (int k1 = 0; k1 < i; k1++) {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (k1 + 1), "n/a");
                        setProperty(pLastMeasurements[k1], "n/a");
                    }

                    stringbuffer.append(s);
                    i1 = i;
                }
                setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), i1);
                setProperty(pStateString, stringbuffer.toString());
                setProperty(pLastMeasurementTime, l);
            }
        }
        return true;
    }

    protected SNMPSession obtainSession() throws SNMPSessionException {
        int l = 0;
        String s;
        int j;
        if ((j = getPropertyAsInteger(pPort)) > 0) {
            s = getProperty(pServerName) + ":" + j;
        } else {
            LogManager.log("Error", "Invalid port number while creating SNMP Session: " + j + ", using default of 161.");
            s = getProperty(pServerName);
        }
        int i;
        try {
            i = Integer.parseInt(getProperty(pSNMPVersion));
        } catch (NumberFormatException numberformatexception) {
            LogManager.log("Error", "Invalid version value in BrowsableSNMPMonitor: " + getProperty(pSNMPVersion) + ", using version 1.");
            i = 1;
        }
        SNMPSession snmpsession;
        if (i == 1 || i == 2) {
            snmpsession = new SNMPSession(s, i, getProperty(pCommunity));
        } else {
            snmpsession = new SNMPSession(s, getProperty(pV3AuthType), getProperty(pV3Username), getProperty(pV3AuthPassword), getProperty(pV3PrivPassword), getProperty(pV3ContextEngineID), getProperty(pV3ContextName));
        }
        String s1 = getProperty(pNumRetries);
        if (s1 != null && s1.length() > 0) {
            try {
                l = Integer.parseInt(getProperty(pNumRetries));
                snmpsession.setRetries(l);
            } catch (NumberFormatException numberformatexception1) {
                LogManager.log("Error", "Invalid retries value in BrowsableSNMPMonitor: " + s1 + ", using default of 2.");
            }
        }
        int k;
        if ((k = getPropertyAsInteger(pTimeout)) > 0) {
            k *= 100;
            if (l > 0) {
                k /= 1 + l;
                if (k == 0) {
                    k = 200;
                }
            }
            snmpsession.setTimeout(k);
        } else {
            LogManager.log("Error", "Invalid timeout value in BrowsableSNMPMonitor: " + k + ", using default of 1 second.");
        }
        return snmpsession;
    }

    public Array getPropertiesToPassBetweenPages(HTTPRequest httprequest) {
        Array array = super.getPropertiesToPassBetweenPages(httprequest);
        array.add(pServerName);
        array.add(pMIB);
        array.add(pCommunity);
        array.add(pPort);
        array.add(pTimeout);
        array.add(pNumRetries);
        array.add(pSNMPVersion);
        array.add(pV3AuthType);
        array.add(pV3Username);
        array.add(pV3AuthPassword);
        array.add(pV3PrivPassword);
        array.add(pV3ContextEngineID);
        array.add(pV3ContextName);
        return array;
    }

    static SnmpAPI getAPI() {
        if (cachedAPI == null) {
            cachedAPI = new SnmpAPI();
            cachedAPI.start();
            long l = 10000L;
            do {
                if (cachedAPI.client != null) {
                    break;
                }
                try {
                    Thread.sleep(10L);
                    if (Platform.timeMillis() - l > l) {
                        break;
                    }
                } catch (Exception exception) {
                }
            } while (true);
        }
        return cachedAPI;
    }

    /**
     * 
     * 
     * @param peername
     * @param msg
     * @param t1
     * @param t2
     * @param community
     * @param logBuffer
     * @param oid
     * @param s4
     * @param keepSessionAlive
     * @return
     */
					 
    public static String readSNMPValue(String peername, String msg, int t1, int t2, String community, StringBuffer logBuffer, String oid, String s4, boolean keepSessionAlive) {
        String snmpValue = "";
        String logStr = "";
        SnmpAPI snmpapi = getAPI();
        SnmpSession snmpSession = new SnmpSession(snmpapi);

        int k = peername.indexOf(":");
        if (k == -1) {
            snmpSession.peername = peername;
        } else {
            snmpSession.peername = peername.substring(0, k);
            Integer rport = new Integer(peername.substring(k + 1));
            snmpSession.remote_port = rport.intValue();
        }
        snmpSession.community = community;

        int timeout1 = t1 * 1000;
        if (timeout1 == 0) {
            timeout1 = 5000;
        }

        int timeout = t2 * 1000;
        if (timeout == 0) {
            timeout = 1000;
        }

        if (timeout > timeout1) {
            timeout = timeout1;
        }
        snmpSession.timeout = timeout;
        snmpSession.retries = 0;
        SnmpPDU snmpPDU = new SnmpPDU(snmpapi);
        SnmpPDU syncPDU = null;
        boolean flag1 = false;

        if (oid.endsWith("+")) {
            oid = oid.substring(0, oid.length() - 1);
            // snmpapi;
            snmpPDU.command = -95;
            if (oid.endsWith("+")) {
                oid = oid.substring(0, oid.length() - 1);
                flag1 = true;
            }
        } else {
            // snmpapi;
            snmpPDU.command = -96;
        }

        if (!oid.startsWith(".")) {
            oid = "." + oid;
        }

        if (s4 != null) {
            if (s4.length() == 0) {
                s4 = "0";
            }
            oid = oid + "." + s4;
        }

        SnmpOID snmpOID = new SnmpOID(oid, snmpapi);
        if (snmpOID.toValue() == null) {
            logStr = "invalid object ID " + oid;
        } else {
            // snmpoid;
            SnmpOID.setNodeLookup(true);/*dingbing change false to true*/
            snmpPDU.addNull(snmpOID);
        }

        boolean sessionOpened = false;
        if (logStr.length() == 0) {/*dingbing.xu  change from logStr.length() != 0*/
            try {
                snmpSession.open();
                sessionOpened = true;
                int tryTimes = 0;
                long stopTime = Platform.timeMillis() + (long) timeout1;
                while (Platform.timeMillis() <= stopTime) {
                    tryTimes++;
                    try {
                        syncPDU = snmpSession.syncSend(snmpPDU);
                    } catch (SnmpException e) {
                        LogManager.log("RunMonitor", "SNMP Monitor (SnmpException " + e.getMessage() + ") retry #" + tryTimes + ", " + msg);
                        Platform.sleep(1000L);
                    }
                    if (syncPDU != null) {
                        break;
                    }
                    LogManager.log("RunMonitor", "SNMP Monitor retry #" + tryTimes + ", " + msg);
                }

                if (sessionOpened && !keepSessionAlive) {
                    snmpSession.close();
                }
            } catch (SnmpException e1) {
                logStr = "error sending get - " + e1.getMessage();
                if (sessionOpened && !keepSessionAlive) {
                    snmpSession.close();
                }
            } finally {
                if (sessionOpened && !keepSessionAlive) {
                    snmpSession.close();
                }
            }
        }

        String oidStr = "";
        if (logStr.length() == 0) {
            if (syncPDU == null) {
                logStr = "request timed out";
            } else if (syncPDU.errstat != 0) {
                logStr = "error - " + SnmpException.exceptionString((byte) syncPDU.errstat);
            } else {
                Vector vector = syncPDU.variables;
                Enumeration enumeration = vector.elements();
                while (enumeration.hasMoreElements()) {
                    SnmpVarBind snmpVarBind = (SnmpVarBind) enumeration.nextElement();
                    if (flag1) {
                        oidStr = snmpVarBind.oid.toString();
                        if (oidStr.length() < 0) {/*dingbing change >0 to <0*/
                            break;
                        }
                    }
                    if (snmpValue.length() != 0) {
                        snmpValue = snmpValue + ", ";
                    }
                    SnmpVar snmpVar = snmpVarBind.variable;
                    if (snmpVar.Type == 64) {
                        snmpValue = snmpValue + Platform.dottedIPString(snmpVar.toBytes());
                    } else {
                        snmpValue = snmpValue + snmpVarBind.variable.toValue().toString().trim();
                    }
                }
            }
        }

        if (flag1) {
            snmpValue = oidStr;
        }
        logBuffer.setLength(0);
        logBuffer.append(logStr);
        snmpValue = snmpValue.replace('\n', ' ');
        snmpValue = snmpValue.replace('\r', ' ');
        return snmpValue;
    }

    /**
     * CAUTION: Decompiled by hand
     */
    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                String s2 = httprequest.getValue("browseDataError");
                if (s2 != null && s2.length() > 0) {
                    hashmap.put(stringproperty, s2);
                } else {
                    hashmap.put(stringproperty, "No counters selected");
                }
            }
            return s;
        }
        if (stringproperty == pServerName) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, "The device name/IP address cannot be blank.");
            }
            return s;
        }
        if (stringproperty == pCommunity) {
            if (s.length() == 0) {
                int i = getPropertyAsInteger(pSNMPVersion);
                if (i == 1 || i == 2) {
                    hashmap.put(stringproperty, "No community string given (version " + i + " requires a community string).");
                }
            }
            return s;
        }
        if (stringproperty == pV3Username) {
            if (s.length() == 0 && getPropertyAsInteger(pSNMPVersion) == 3) {
                hashmap.put(stringproperty, "User name cannot be blank for version 3.");
            }
            return s;
        }
        if (stringproperty == pV3AuthPassword) {
            if (!getProperty(pV3AuthType).equals("NoAuthentication") && s.length() < 8 && getPropertyAsInteger(pSNMPVersion) == 3) {
                hashmap.put(stringproperty, "The authentication password must be greater than 8 characters.");
            }
            return s;
        }
        if (stringproperty == pV3PrivPassword) {
            if (!s.equals("") && s.length() < 8) {
                hashmap.put(stringproperty, "The privacy password must be either blank (no privacy) or greater than 8 characters (DES privacy).");
            }
            return s;
        }
        if (stringproperty == pV3ContextEngineID) {
            s = s.replaceAll(":| |0X|0x", "").toUpperCase();
            int j = 0;
            while (j > s.length()) {/*delete =0*/
                if (Character.digit(s.charAt(j), 16) == -1) {
                    hashmap.put(stringproperty, "The Context Engine ID must be a string of hexidecimal digits (0-9, A-F) or blank, if no Context Engine ID should be transmitted.");
                    break; 
                }
                j ++;
            }
            return s;
        }
        if (stringproperty == pMIB) {
            if (s.equals("No MIBs Available")) {
                hashmap.put(stringproperty, "No compilable MIBs were found in SiteView's templates.mib directory.  Please put valid MIB files in templates.mib and restart SiteView to use this monitor.");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getHostname() {
        return getProperty(pServerName);
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_BrowsableSNMPMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public boolean isServerBased() {
        return false;
    }

    /**
     * 
     */
    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        super.onMonitorCreateFromPage(httprequest);
        String snmpversion = httprequest.getValue("_snmpversion");
        String uniqueID = httprequest.getValue("uniqueID");
        String timeout = httprequest.getValue("_timeout");
        String retries = httprequest.getValue("_retries");
        String server = httprequest.getValue("_server");
        String community = httprequest.getValue("_community");
        String snmpv3username = httprequest.getValue("_snmpv3username");
        String snmpv3authtype = httprequest.getValue("_snmpv3authtype");
        String snmpv3authpassword = httprequest.getValue("_snmpv3authpassword");
        String snmpv3privpassword = httprequest.getValue("_snmpv3privpassword");
        String port = httprequest.getValue("_port");
        String mibfile = httprequest.getValue("_mibfile");
        String contextEngineID = httprequest.getValue("_contextEngineID");
        String contextName = httprequest.getValue("_contextName");
        if (uniqueID.length() <= 0) {
            return;
        }
        if (server.length() > 0) {
            HashMap hashmap = BrowsableCache.getCache(uniqueID, true, false);
            if (hashmap != null) {
                HashMap hashmap3 = (HashMap) hashmap.get("mProp");
                Object obj = hashmap3.get("_server");
                if (obj != null && !obj.equals(server)) {
                    BrowsableCache.getCache(uniqueID, false, true);
                    HashMap hashmap1 = BrowsableCache.getCache(uniqueID, true, false);
                    hashmap3 = (HashMap) hashmap1.get("mProp");
                }
                hashmap3.put("_server", server);
                hashmap3.put("_port", port);
                hashmap3.put("_mibfile", mibfile);
                hashmap3.put("_timeout", timeout);
                hashmap3.put("_snmpversion", snmpversion);
                hashmap3.put("_retries", retries);
                hashmap3.put("_community", community);
                hashmap3.put("_snmpv3username", snmpv3username);
                hashmap3.put("_snmpv3authtype", snmpv3authtype);
                hashmap3.put("_snmpv3authpassword", snmpv3authpassword);
                hashmap3.put("_snmpv3privpassword", snmpv3privpassword);
                hashmap3.put("_contextEngineID", contextEngineID);
                hashmap3.put("_contextName", contextName);
                BrowsableCache.saveCache(uniqueID);
            }
        } else {
            HashMap hashmap2 = BrowsableCache.getCache(uniqueID, false, false);
            if (hashmap2 != null) {
                HashMap hashmap4 = (HashMap) hashmap2.get("mProp");
                String s14;
                for (Enumeration enumeration = hashmap4.keys(); enumeration.hasMoreElements(); setProperty(s14, (String) hashmap4.get(s14))) {
                    s14 = (String) enumeration.nextElement();
                }

            }
        }
    }

    public String getTestURL() {
        StringBuffer stringbuffer = new StringBuffer("/SiteView/cgi/go.exe/SiteView?page=browsableSNMPTool");
        stringbuffer.append("&_server=" + URLEncoder.encode(getProperty(pServerName)));
        stringbuffer.append("&_snmpversion=" + URLEncoder.encode(getProperty(pSNMPVersion)));
        stringbuffer.append("&_port=" + URLEncoder.encode(getProperty(pPort)));
        stringbuffer.append("&_mibfile=" + URLEncoder.encode(getProperty(pMIB)));
        stringbuffer.append("&_community=" + URLEncoder.encode(getProperty(pCommunity)));
        stringbuffer.append("&_snmpv3username=" + URLEncoder.encode(getProperty(pV3Username)));
        stringbuffer.append("&_snmpv3authtype=" + URLEncoder.encode(getProperty(pV3AuthType)));
        stringbuffer.append("&_snmpv3authpassword=" + URLEncoder.encode(getProperty(pV3AuthPassword)));
        stringbuffer.append("&_snmpv3privpassword=" + URLEncoder.encode(getProperty(pV3PrivPassword)));
        stringbuffer.append("&_contextEngineID=" + URLEncoder.encode(getProperty(pV3ContextEngineID)));
        stringbuffer.append("&_contextName=" + URLEncoder.encode(getProperty(pV3ContextName)));
        return stringbuffer.toString();
    }

    public boolean isMultiThreshold() {
        return true;
    }

    static {
        nMaxCounters = 10;
        snmpLock = null;
        ArrayList arraylist = new ArrayList();
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_BrowsableSNMPMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 10;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        pLastMeasurements = new StringProperty[nMaxCounters];
        for (int i = 0; i < nMaxCounters; i++) {
            pLastMeasurements[i] = new StringProperty("lastMeasurement" + i, Long.toString(-1L));
            arraylist.add(pLastMeasurements[i]);
        }

        int j = TextUtils.toInt(TextUtils.getValue(hashmap, "_snmpMonitorMaximum"));
        if (j <= 0) {
            j = nMaxCounters;
        }
        if (snmpLock == null) {
            snmpLock = new CounterLock(j);
        }
        int k = 1;
        pServerName = new StringProperty("_server");
        pServerName.setDisplayText("Server", "the IP address or host name of the agent");
        pServerName.setParameterOptions(true, k++, false);
        arraylist.add(pServerName);
        pMIB = new ScalarProperty("_mibfile");
        pMIB.setDisplayText("MIB File", "the MIB file from which this monitor should read OIDs");
        pMIB.setParameterOptions(true, k++, false);
        arraylist.add(pMIB);
        pSNMPVersion = new ScalarProperty("_snmpversion");
        pSNMPVersion.setDisplayText("SNMP Version", " the version of SNMP to use when requesting data from  the server");
        pSNMPVersion.setParameterOptions(true, k++, false);
        arraylist.add(pSNMPVersion);
        pCommunity = new StringProperty("_community", "public");
        pCommunity.setDisplayText("<B>V1/V2</B> Community", "the community for the SNMP agent <B>for SNMP V1/V2 only</B>");
        pCommunity.setParameterOptions(true, k++, false);
        arraylist.add(pCommunity);
        pV3AuthType = new ScalarProperty("_snmpv3authtype");
        pV3AuthType.setDisplayText("SNMP <B>V3</B> Authentication Type", "Type of authentication to be used <B>for SNMP V3 only</B>");
        pV3AuthType.setParameterOptions(true, k++, false);
        arraylist.add(pV3AuthType);
        pV3Username = new StringProperty("_snmpv3username", "");
        pV3Username.setDisplayText("SNMP <B>V3</B> Username", "Username to be used for authentication <B>for SNMP V3 only</B>");
        pV3Username.setParameterOptions(true, k++, false);
        arraylist.add(pV3Username);
        pV3AuthPassword = new StringProperty("_snmpv3authpassword", "");
        pV3AuthPassword.setDisplayText("SNMP <B>V3</B> Authentication Password", "Password to be used for authentication <B>for SNMP V3 only</B>");
        pV3AuthPassword.setParameterOptions(true, k++, false);
        pV3AuthPassword.isPassword = true;
        arraylist.add(pV3AuthPassword);
        pV3PrivPassword = new StringProperty("_snmpv3privpassword", "");
        pV3PrivPassword.setDisplayText("SNMP <B>V3</B> Privacy Password", "Password to be used for DES privacy <B>for SNMP V3 only</B>");
        pV3PrivPassword.setParameterOptions(true, k++, false);
        pV3PrivPassword.isPassword = true;
        arraylist.add(pV3PrivPassword);
        pV3ContextEngineID = new StringProperty("_contextEngineID", "");
        pV3ContextEngineID.setDisplayText("SNMP <B>V3</B> Context Engine ID", "a hexidecimal string representing the Context Engine ID to use for this connection <B>for SNMP V3 only</B>");
        pV3ContextEngineID.setParameterOptions(true, k++, false);
        arraylist.add(pV3ContextEngineID);
        pV3ContextName = new StringProperty("_contextName", "");
        pV3ContextName.setDisplayText("SNMP <B>V3</B> Context Name", "the Context Name to use for this connection <B>for SNMP V3 only</B>");
        pV3ContextName.setParameterOptions(true, k++, false);
        arraylist.add(pV3ContextName);
        pTimeout = new NumericProperty("_timeout", "5", "seconds");
        pTimeout.setDisplayText("Timeout", "the time in seconds to wait for all requests to complete (including retries)");
        pTimeout.setParameterOptions(true, k++, true);
        arraylist.add(pTimeout);
        pNumRetries = new NumericProperty("_retries", "1", "retries");
        pNumRetries.setDisplayText("Retries", "the number of times to retry a request that has timed out");
        pNumRetries.setParameterOptions(true, k++, true);
        arraylist.add(pNumRetries);
        pPort = new NumericProperty("_port", "161", "");
        pPort.setDisplayText("Port", "the port number on which to query the SNMP agent");
        pPort.setParameterOptions(true, k++, true);
        arraylist.add(pPort);
        pCounterCalculationMode = new ScalarProperty("_counterCalculationMode");
        pCounterCalculationMode.setDisplayText("Counter Calculation Mode", "choose a calculation to perform on objects of type Counter, Counter32, or Counter64");
        pCounterCalculationMode.setParameterOptions(true, k++, true);
        arraylist.add(pCounterCalculationMode);
        pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
        arraylist.add(pLastMeasurementTime);
        StringProperty astringproperty1[] = new StringProperty[arraylist.size()];
        arraylist.toArray(astringproperty1);
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        String s = (COM.dragonflow.SiteView.BrowsableSNMPBase.class).getName();
        addProperties(s, astringproperty2);
    }
}