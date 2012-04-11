/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * PortMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>PortMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
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
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// PortInfo, URLMonitor

public class PortMonitor extends AtomicMonitor {

    static StringProperty pHost;

    static StringProperty pPort;

    static StringProperty pSendString;

    static StringProperty pMatchString;

    static StringProperty pTimeout;

    static StringProperty pStatus;

    static StringProperty pStatusText;

    static StringProperty pRoundTripTime;

    static StringProperty pValue;

    String portType;

    int receiveBufferSize;

    int receiveMax;

    int receivedTotal;

    byte receiveBytes[];

    public PortMonitor() {
        portType = "TCP";
        receiveBufferSize = 300;
        receiveMax = receiveBufferSize / 2;
        receivedTotal = 0;
        receiveBytes = new byte[receiveBufferSize];
    }

    public String getHostname() {
        return getProperty(pHost);
    }

    public String getTestURL() {
        return "/SiteView/cgi/go.exe/SiteView?page=ping&host=" + getProperty(pHost);
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        String s;
        int i;
        int j;
        String s2;
        String s3;
        boolean flag;
        int k;
        int l;
        String s4;
        long l1;
        s = getProperty(pHost);
        String s1 = getProperty(pPort);
        i = TextUtils.toInt(s1);
        j = StringProperty.toInteger(getProperty(pTimeout));
        if (j == 0) {
            j = URLMonitor.DEFAULT_TIMEOUT;
        } else {
            j *= 1000;
        }
        s2 = getProperty(pSendString);
        s3 = getProperty(pMatchString);
        flag = false;
        k = 0;
        PortInfo portinfo = getPortInfo(s1);
        if (portinfo != null) {
            flag = portinfo.udp;
            k = portinfo.udpPort;
            if (s2.length() == 0 && s3.length() == 0) {
                s2 = portinfo.sendString;
                s3 = portinfo.matchString;
            } else if (s2.length() == 0 && s3.length() > 0) {
                s2 = portinfo.sendString;
            }
        }
        l = URLMonitor.kURLok;
        s4 = "";
        l1 = Platform.timeMillis();
        byte abyte0[];
        byte abyte1[];
        DatagramSocket datagramsocket;

        try {
            abyte0 = TextUtils.stringToBytes(s2);
            abyte1 = TextUtils.stringToBytes(s3);
            if (getSetting("_manDNSLookup").length() > 0) {
                s = Platform.resolveDNS(s);
            }
            setProperty(pValue, "");
            if (flag) {
                portType = "UDP";
                datagramsocket = null;

                try {
                    InetAddress inetaddress = InetAddress.getByName(s);
                    if (k == 0) {
                        datagramsocket = new DatagramSocket();
                    } else {
                        LogManager.log("RunMonitor", "Port Monitor listening on (" + portType + ") port:" + k);
                        datagramsocket = new DatagramSocket(k);
                    }
                    Platform.setSocketTimeout(datagramsocket, j);
                    DatagramPacket datagrampacket = new DatagramPacket(abyte0, abyte0.length, inetaddress, i);
                    datagramsocket.send(datagrampacket);
                    LogManager.log("RunMonitor", "Port Monitor sent to " + s + ":" + i + "(" + portType + "), " + TextUtils.bytesToString(abyte0, abyte0.length));
                    byte abyte2[] = new byte[4096];
                    DatagramPacket datagrampacket1 = new DatagramPacket(abyte2, abyte2.length);
                    datagramsocket.receive(datagrampacket1);
                    LogManager.log("RunMonitor", "Port Monitor (" + portType + ") matching: " + TextUtils.bytesToString(abyte1, abyte1.length));
                    LogManager.log("RunMonitor", "Port Monitor (" + portType + ") got: " + TextUtils.bytesToString(abyte2, datagrampacket1.getLength()));
                    setProperty(pValue, TextUtils.bytesToString(abyte2, datagrampacket1.getLength()));
                    if (matchIndex(abyte2, datagrampacket1.getLength(), abyte1) == -1) {
                        l = URLMonitor.kURLContentMatchError;
                        s4 = "expected: " + TextUtils.bytesToString(abyte1, abyte1.length) + ", got: " + TextUtils.bytesToString(abyte2, datagrampacket1.getLength());
                    }
                    if (datagramsocket != null) {
                        datagramsocket.close();
                    }
                } catch (Exception e) {
                    if (datagramsocket != null) {
                        datagramsocket.close();
                    }
                    throw e;
                }
            }

            Socket socket = null;
            BufferedOutputStream bufferedoutputstream = null;
            BufferedInputStream bufferedinputstream = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(s, i), j);
                j -= (int) (System.currentTimeMillis() - l1);
                if (s2.length() > 0 || s3.length() > 0) {
                    bufferedinputstream = new BufferedInputStream(socket.getInputStream());
                    bufferedoutputstream = new BufferedOutputStream(socket.getOutputStream());
                    bufferedoutputstream.write(abyte0);
                    bufferedoutputstream.flush();
                    LogManager.log("RunMonitor", "Port Monitor sent to " + s + ":" + i + "(" + portType + "),  " + TextUtils.bytesToString(abyte0, abyte0.length));
                    clearReceiveBuffer();
                    String as[] = match(bufferedinputstream, socket, (long) j + Platform.timeMillis(), abyte1);
                    setProperty(pValue, as[1]);
                    if (abyte1.length == 0) {
                        l = URLMonitor.kURLok;
                    } else {
                        l = as[0].length() != 0 ? URLMonitor.kURLContentMatchError : URLMonitor.kURLok;
                    }
                    LogManager.log("RunMonitor", "Port Monitor (" + portType + ") got: " + as[1]);
                    s4 = "expected: " + s3 + ", got: " + as[0];
                }
            } catch (Exception e) {
                /* empty */
            }

            finally {
                if (bufferedoutputstream != null) {
                    bufferedoutputstream.close();
                }
                if (bufferedinputstream != null) {
                    bufferedinputstream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            }
        } catch (UnknownHostException e) {
            l = kURLBadHostNameError;
        } catch (SocketException e) {
            if (Platform.noRoute(e)) {
                l = kURLNoRouteToHostError;
            } else {
                l = kURLNoConnectionError;
            }
        } catch (InterruptedIOException e) {
            l = kURLTimeoutError;
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.log("Error", "Port Monitor (" + portType + ") unknown error: " + e.getMessage());
            l = kURLUnknownError;
        }

        long l2 = Platform.timeMillis() - l1;

        if (stillActive()) {
            synchronized (this) {
                setProperty(pStatus, l);
                setProperty(pStatusText, URLMonitor.lookupStatus(l));
                if (l == URLMonitor.kURLok) {
                    String s5 = TextUtils.floatToString((float) l2 / 1000F, 2) + " sec";
                    setProperty(pStateString, s5);
                    setProperty(pRoundTripTime, l2);
                } else {
                    if (l == URLMonitor.kURLContentMatchError) {
                        setProperty(pStateString, "unexpected response from (" + portType + ") port; " + s4);
                    } else {
                        setProperty(pStateString, URLMonitor.lookupStatus(l));
                        setProperty(pNoData, "n/a");
                    }
                    setProperty(pRoundTripTime, "n/a");
                }
            }
        }
        return true;
    }

    void clearReceiveBuffer() {
        receivedTotal = 0;
    }

    void slideReceiveBuffer(int i) {
        receivedTotal -= i;
        for (int j = 0; j < receivedTotal; j ++) {
            receiveBytes[j] = receiveBytes[j + i];
        }

    }

    String[] match(InputStream inputstream, Socket socket, long l, byte abyte0[]) throws Exception {
        String as[] = new String[2];
        LogManager.log("RunMonitor", "Port Monitor (" + portType + ") matching: " + TextUtils.bytesToString(abyte0, abyte0.length));
        int i = (int) (l - Platform.timeMillis());
        if (i <= 0) {
            throw new InterruptedIOException();
        }
        Platform.setSocketTimeout(socket, i);
        boolean flag = false;
        do {
            if (receivedTotal > receiveMax) {
                slideReceiveBuffer(receivedTotal - receiveMax);
            }
            int j = 0;
            try {
                j = inputstream.read(receiveBytes, receivedTotal, receiveMax);
            } catch (InterruptedIOException interruptedioexception) {
                if (!flag) {
                    throw new InterruptedIOException(interruptedioexception.toString());
                } else {
                    as[0] = TextUtils.bytesToString(receiveBytes, receivedTotal);
                    as[1] = TextUtils.bytesToString(receiveBytes, receivedTotal);
                    return as;
                }
            }
            flag = true;
            if (j != -1) {
                receivedTotal += j;
                LogManager.log("RunMonitor", "Port Monitor (" + portType + ") got: " + TextUtils.bytesToString(receiveBytes, receivedTotal));
                if (abyte0.length > 0) {
                    int k = matchIndex(receiveBytes, receivedTotal, abyte0);
                    as[1] = TextUtils.bytesToString(receiveBytes, receivedTotal);
                    if (k != -1) {
                        slideReceiveBuffer(k + abyte0.length);
                        as[0] = "";
                        return as;
                    }
                }
            } else {
                as[0] = TextUtils.bytesToString(receiveBytes, receivedTotal);
                as[1] = TextUtils.bytesToString(receiveBytes, receivedTotal);
                return as;
            }
        } while (true);
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        return array;
    }

    public String defaultTitle() {
        String s = getProperty(pPort);
        PortInfo portinfo = getPortInfo(s);
        if (portinfo != null) {
            return portinfo.name + " on " + getProperty(pHost);
        } else {
            return "Port " + s + " on " + getProperty(pHost);
        }
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
        if (stringproperty == pPort) {
            if (TextUtils.digits(s) != s.length()) {
                hashmap.put(stringproperty, "port must be a number");
            }
            return s;
        }
        if (stringproperty == pTimeout) {
            if (TextUtils.digits(s) != s.length()) {
                hashmap.put(stringproperty, "time out must be a number");
            } else if (TextUtils.toInt(s) < 1) {
                hashmap.put(stringproperty, "time out must be greater than 0");
            }
            return s;
        }
        if (stringproperty == pSendString || stringproperty == pMatchString) {
            try {
                TextUtils.stringToBytes(s);
            } catch (Exception exception) {
                hashmap.put(stringproperty, "error in escape characters - \\n,\\r,\\f,\\t,\\b,\\a,\\e,\\xNN,\\0NN are supported");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pPort) {
            Vector vector = new Vector();
            Enumeration enumeration = getPortMonitors();
            String as[] = new String[5];
            do {
                if (!enumeration.hasMoreElements()) {
                    break;
                }
                String s = (String) enumeration.nextElement();
                int i = TextUtils.splitChar(s, '\t', as);
                if (i >= 2) {
                    vector.addElement(as[0]);
                    String s1 = as[1];
                    s1 = s1 + "(" + as[0];
                    if (i >= 5) {
                        s1 = s1 + " udp";
                    }
                    s1 = s1 + ")";
                    vector.addElement(s1);
                }
            } while (true);
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (getProperty(pCategory).equals("good")) {
                return "";
            } else {
                return diagnostic(getProperty(pHost), getPropertyAsInteger(pStatus));
            }
        } else {
            return super.getProperty(stringproperty);
        }
    }

    static Enumeration getPortMonitors() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        return siteviewgroup.getMultipleValues("_portMonitor");
    }

    static PortInfo getPortInfo(String s) {
        String as[] = new String[5];
        for (Enumeration enumeration = getPortMonitors(); enumeration.hasMoreElements();) {
            as[0] = "";
            as[1] = "";
            as[2] = "";
            as[3] = "";
            int i = TextUtils.splitChar((String) enumeration.nextElement(), '\t', as);
            boolean flag = false;
            int j = 0;
            if (i > 4) {
                flag = true;
                String s1 = as[4];
                String s2 = "UDP:";
                int k = s1.indexOf(s2);
                if (k != -1) {
                    j = TextUtils.toInt(s1.substring(s2.length()));
                }
            }
            if (i >= 2 && as[0].equals(s)) {
                return new PortInfo(as[0], as[1], as[2], as[3], flag, j);
            }
        }

        return null;
    }

    public static int matchIndex(byte abyte0[], int i, byte abyte1[]) {
        if (i < abyte1.length) {
            return -1;
        }
        for (int j = 0; j <= i - abyte1.length; j ++) {
            boolean flag = true;
            for (int k = 0; k < abyte1.length; k ++) {
                if (abyte0[j + k] != abyte1[k]) {
                    flag = false;
                }
            }

            if (flag) {
                return j;
            }
        }

        return -1;
    }

    static {
        pHost = new StringProperty("_hostname", "", "host name");
        pHost.setDisplayText("Host Name", "the IP address or host name that will be connected to (examples: 206.168.191.21 or demo." + Platform.exampleDomain + ")");
        pHost.setParameterOptions(true, 1, false);
        pPort = new ScalarProperty("_port", "");
        pPort.setDisplayText("Port Number", "the port that will be connected to. Either choose one of the commonly used ports from the menu, or enter a port number.");
        pPort.setParameterOptions(true, 2, false);
        ((ScalarProperty) pPort).allowOther = true;
        pSendString = new StringProperty("_sendString");
        pSendString.setDisplayText("Send String", "Override the default string that is be sent to the port after connecting");
        pSendString.setParameterOptions(true, 3, true);
        pMatchString = new StringProperty("_matchString");
        pMatchString.setDisplayText("Match String", "Override the default string that is matched against the port's response");
        pMatchString.setParameterOptions(true, 4, true);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for connection and reply");
        pTimeout.setParameterOptions(true, 5, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status", "no data");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        pStatusText = new StringProperty("statusText", "no data");
        pValue = new StringProperty("value", "no data");
        pValue.setLabel("port response");
        pValue.setIsThreshold(true);
        StringProperty astringproperty[] = { pHost, pPort, pSendString, pMatchString, pTimeout, pStatus, pStatusText, pRoundTripTime, pValue };
        addProperties("COM.dragonflow.StandardMonitor.PortMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.PortMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.PortMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.PortMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "description", "Determines whether a service on a port can be connected to.");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "help", "PortMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "title", "Port");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "class", "PortMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "target", "_hostname");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "topazName", "Port");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "topazType", "System Resources");
        setClassProperty("COM.dragonflow.StandardMonitor.PortMonitor", "toolPageDisable", "true");
    }
}
