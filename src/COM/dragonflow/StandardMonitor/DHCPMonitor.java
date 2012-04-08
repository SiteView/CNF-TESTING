/*
 * 
 * Created on 2005-3-7 0:59:25
 *
 * DHCPMonitor.java
 * 
 * NOTE: Download jDHCP from http://www.dhcp.org/javadhcp/
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DHCPMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.StringTokenizer;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.TextUtils;
import edu.bucknell.net.JDHCP.DHCPMessage;
import edu.bucknell.net.JDHCP.DHCPSocket;

public class DHCPMonitor extends AtomicMonitor {

    boolean showDebug;

    long startTime;

    private boolean unavailable;

    private String status;

    static final byte DHCP_BOOTREQUEST = 1;

    static final byte DHCP_HADDR_TYPE = 1;

    static final byte DHCP_HADDR_LEN = 6;

    static final byte DHCP_HOPS = 0;

    static final int DHCP_HWADDR_BUF_LEN = 16;

    static final int MAX_TRIES = 10;

    static final int LEASE_DURATION = 1;

    static final int OP_REQUESTED_ID = 50;

    static final int OP_LEASE_TIME = 51;

    static final int OP_MESSAGE_TYPE = 53;

    static final int OP_SERVER_ID = 54;

    static final int OP_T1 = 58;

    static final int OP_T2 = 59;

    static NumericProperty pTimeout;

    static StringProperty pRequestAddress;

    static StringProperty pLeaseAddress;

    static StringProperty pStatus;

    static StringProperty pStatusText;

    static NumericProperty pRoundTripTime;

    public DHCPMonitor() {
        showDebug = false;
        startTime = 0L;
        unavailable = false;
        status = "";
    }

    public String getHostname() {
        String s = "";
        try {
            s = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException unknownhostexception) {
        }
        return s;
    }

    protected boolean update() {
        if (System.getProperty("DHCPMonitor.debug") != null) {
            showDebug = true;
        }
        try {
            Class.forName("edu.bucknell.net.JDHCP.DHCPSocket");
        } catch (ClassNotFoundException classnotfoundexception) {
            setProperty(pMeasurement, "0");
            setProperty(pRoundTripTime, "0");
            setProperty(pStatus, "error");
            setProperty(pStatusText, "JDHCP libraries unavailable (see Help document for DHCP Monitor)");
            setProperty(pStateString, "JDHCP libraries unavailable (see Help document for DHCP Monitor)");
            return true;
        }
        int i = TextUtils.toInt(getProperty(pTimeout));
        Random random = new Random();
        int j = random.nextInt();
        byte abyte0[] = new byte[16];
        for (int k = 0; k < 16; k ++) {
            abyte0[k] = 51;
        }

        startTime = System.currentTimeMillis();
        Object aobj[] = acquireAddress(j, abyte0, i);
        if (aobj[0].equals("n/a")) {
            setProperty(pNoData, "n/a");
        }
        setProperty(pMeasurement, aobj[0]);
        setProperty(pRoundTripTime, aobj[1]);
        setProperty(pStatus, aobj[2]);
        setProperty(pStatusText, aobj[3]);
        setProperty(pStateString, aobj[4]);
        return true;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pRoundTripTime);
        array.add(pLeaseAddress);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pTimeout) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pRequestAddress && s.length() > 0) {
            StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
            if (stringtokenizer.countTokens() != 4) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " invalid (must be an IPV4 " + "address, e.g. 10.0.0.10)");
                return s;
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    private DHCPSocket openSocket(DHCPSocket dhcpsocket) {
        try {
            dhcpsocket = new DHCPSocket(68);
        } catch (SocketException socketexception) {
            if (showDebug) {
                TextUtils.debugPrint("DHCPSocket failed to open");
            }
            TextUtils.debugPrint("error was " + socketexception.getMessage());
            status = socketexception.getMessage();
            return null;
        }
        if (showDebug) {
            TextUtils.debugPrint("DHCPSocket opened successfully");
        }
        return dhcpsocket;
    }

    private Object[] acquireAddress(int i, byte abyte0[], int j) {
        Object aobj[] = new Object[5];
        aobj[0] = "n/a";
        aobj[1] = String.valueOf(0);
        aobj[2] = "error";
        aobj[3] = "no data";
        aobj[4] = "no data";
        int k = -1;
        DHCPSocket dhcpsocket = null;
        DHCPMessage dhcpmessage = new DHCPMessage();
        DHCPMessage dhcpmessage1 = new DHCPMessage();
        byte abyte1[] = new byte[4];
        Random random = new Random();
        if (showDebug) {
            TextUtils.debugPrint("Opening initial DHCPSocket");
        }
        if ((dhcpsocket = openSocket(dhcpsocket)) == null) {
            aobj[3] = aobj[4] = status;
            return aobj;
        }
        dhcpmessage.setChaddr(abyte0);
        if (getProperty(pRequestAddress).length() > 0) {
            if (showDebug) {
                TextUtils.debugPrint("Requesting Address " + getProperty(pRequestAddress));
            }
            dhcpmessage.setOption(50, stringToIP(getProperty(pRequestAddress)));
        }
        for (int l = 0; l < 10; l ++) {
            if (showDebug) {
                TextUtils.debugPrint("Sending DHCPDISCOVER...");
            }
            if (!sendMessage(status, dhcpmessage, dhcpsocket, i, 1)) {
                aobj[3] = aobj[4] = status;
                return aobj;
            }
            if (showDebug) {
                TextUtils.debugPrint("Sleeping " + j / 10 + " seconds...");
            }
            Platform.sleep((j / 10) * 1000);
            if (dhcpsocket.receive(dhcpmessage1)) {
                if (showDebug) {
                    TextUtils.debugPrint("Received DHCPOFFER.  Checking...");
                }
                if (checkOffer(dhcpmessage1, i)) {
                    abyte1 = dhcpmessage1.getYiaddr();
                    if (showDebug) {
                        printAddr("yiaddr", "DHCPOFFER", abyte1);
                    }
                    break;
                }
                if (unavailable) {
                    aobj[3] = aobj[4] = status;
                    return aobj;
                }
            }
            if (l == 9) {
                aobj[3] = aobj[4] = "Timed out waiting for DHCPOFFER";
                return aobj;
            }
        }

        k = bytesToInt(dhcpmessage1.getOption(51));
        if (k > 1) {
            k = 1;
        }
        dhcpmessage.setOption(54, dhcpmessage1.getOption(54));
        dhcpmessage.setOption(58, dhcpmessage1.getOption(58));
        dhcpmessage.setOption(59, dhcpmessage1.getOption(59));
        if (showDebug) {
            TextUtils.debugPrint("Sending DHCPREQUEST for address " + ipToString(abyte1) + "...");
        }
        sendMessage(status, dhcpmessage, dhcpsocket, i, 3, abyte1);
        if (!dhcpsocket.receive(dhcpmessage1)) {
            if (showDebug) {
                TextUtils.debugPrint("Timed out waiting for DHCPACK");
            }
            aobj[3] = aobj[4] = "Timed out waiting for DHCPACK";
            return aobj;
        }
        if (dhcpmessage1.getXid() != i) {
            if (showDebug) {
                TextUtils.debugPrint("Received invalid DHCPACK from server (invalid xid)");
            }
            aobj[3] = aobj[4] = "Received invalid DHCPACK from server";
            return aobj;
        }
        if (showDebug) {
            TextUtils.debugPrint("Received address " + ipToString(dhcpmessage1.getYiaddr()));
            TextUtils.debugPrint("sending DHCPRELEASE in " + k + " seconds.");
        }
        Platform.sleep(bytesToInt(dhcpmessage1.getOption(51)) * 1000);
        dhcpmessage.setCiaddr(abyte1);
        dhcpmessage.setOption(50, new byte[4]);
        dhcpmessage.setOption(51, new byte[4]);
        dhcpmessage.setOption(54, dhcpmessage1.getOption(54));
        if (showDebug) {
            TextUtils.debugPrint("Sending DHCPRELEASE for " + ipToString(dhcpmessage1.getYiaddr()));
        }
        sendMessage(status, dhcpmessage, dhcpsocket, random.nextInt(), 7);
        dhcpsocket.close();
        long l1 = System.currentTimeMillis() - startTime;
        String s = "leased address " + ipToString(abyte1);
        aobj[0] = aobj[1] = (new Long(l1)).toString();
        aobj[2] = "ok";
        aobj[3] = "leased address " + ipToString(abyte1);
        s = s + ", " + TextUtils.floatToString((float) l1 / 1000F, 2) + " sec";
        aobj[4] = s;
        return aobj;
    }

    private boolean sendMessage(String s, DHCPMessage dhcpmessage, DHCPSocket dhcpsocket, int i, int j, byte abyte0[]) {
        if (j == 3) {
            dhcpmessage.setOption(50, abyte0);
        }
        return sendMessage(s, dhcpmessage, dhcpsocket, i, j);
    }

    private boolean sendMessage(String s, DHCPMessage dhcpmessage, DHCPSocket dhcpsocket, int i, int j) {
        dhcpmessage.setOp((byte) 1);
        dhcpmessage.setHtype((byte) 1);
        dhcpmessage.setHlen((byte) 6);
        dhcpmessage.setHops((byte) 0);
        dhcpmessage.setXid(i);
        dhcpmessage.setSecs((short) 0);
        dhcpmessage.setFlags((short) 0);
        byte abyte0[] = new byte[4];
        for (int k = 0; k < 4; k ++) {
            abyte0[k] = 0;
        }

        dhcpmessage.setCiaddr(abyte0);
        dhcpmessage.setYiaddr(abyte0);
        dhcpmessage.setSiaddr(abyte0);
        dhcpmessage.setGiaddr(abyte0);
        byte abyte1[] = new byte[1];
        abyte1[0] = (byte) j;
        dhcpmessage.setOption(53, abyte1);
        dhcpmessage.setOption(51, intToBytes(1));
        try {
            dhcpsocket.send(dhcpmessage);
        } catch (IOException ioexception) {
            s = ioexception.getMessage();
            return false;
        }
        return true;
    }

    private boolean checkOffer(DHCPMessage dhcpmessage, int i) {
        byte abyte0[] = new byte[1];
        abyte0 = dhcpmessage.getOption(53);
        if (abyte0[0] != 2) {
            if (showDebug) {
                TextUtils.debugPrint("DHCP: message was not DHCPOFFER, retrying");
            }
            return false;
        }
        if (dhcpmessage.getXid() != i) {
            if (showDebug) {
                TextUtils.debugPrint("DHCP: XIDs did not match, retrying");
            }
            return false;
        }
        String s = ipToString(dhcpmessage.getYiaddr());
        String s1 = getProperty(pRequestAddress);
        if (showDebug) {
            TextUtils.debugPrint("offered address " + s);
        }
        if (getProperty(pRequestAddress).length() > 0 && !s.equals(s1)) {
            status = "Requested address (" + getProperty(pRequestAddress) + ") unavailable";
            unavailable = true;
            return false;
        } else {
            return true;
        }
    }

    private void printAddr(String s, String s1, byte abyte0[]) {
        if (showDebug) {
            System.out.print("*** " + s + " from " + s1 + ": ");
            System.out.print(ipToString(abyte0));
            System.out.println("");
        }
    }

    private String ipToString(byte abyte0[]) {
        String s = "";
        for (int j = 0; j < 4; j ++) {
            int i = (char) abyte0[j] % 256;
            s = s + "" + i;
            if (j < 3) {
                s = s + ".";
            }
        }

        return s;
    }

    private byte[] intToBytes(int i) {
        int j = i;
        byte abyte0[] = new byte[4];
        for (int k = 3; k > -1; k --) {
            abyte0[k] = (byte) ((char) j % 256);
            j /= 256;
        }

        return abyte0;
    }

    private int bytesToInt(byte abyte0[]) {
        int i = 0;
        boolean flag = false;
        for (int k = 0; k < 4; k ++) {
            int j = (int) Math.pow(2D, 8 * (3 - k));
            i += abyte0[k] * j;
        }

        return i;
    }

    private byte[] stringToIP(String s) {
        byte abyte0[] = new byte[4];
        StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
        if (stringtokenizer.countTokens() != 4) {
            return abyte0;
        }
        for (int i = 0; i < 4; i ++) {
            Integer integer = new Integer(stringtokenizer.nextToken());
            abyte0[i] = integer.byteValue();
        }

        return abyte0;
    }

    public static void main(String args[]) {
        System.out.println("\n---------------------------");
        System.out.println("  Testing DHCP Monitor");
        System.out.println("---------------------------\n");
        DHCPMonitor dhcpmonitor = new DHCPMonitor();
        if (System.getProperty("DHCPMonitor.debug") != null) {
            System.out.println("    (debug output enabled)");
            dhcpmonitor.showDebug = true;
        }
        dhcpmonitor.update();
        System.out.println("\n--------");
        System.out.println("  done");
        System.out.println("--------\n");
    }

    static {
        pTimeout = new NumericProperty("_timeout", "10", "seconds");
        pTimeout.setDisplayText("Timeout", "The amount of time, in seconds, to wait for a response from a DHCP server");
        pTimeout.setParameterOptions(true, 1, false);
        pRequestAddress = new StringProperty("_requestAddress");
        pRequestAddress.setDisplayText("Requested Client Address", "(Optional) The IP Address to request from the DHCP Server");
        pRequestAddress.setParameterOptions(true, 2, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pLeaseAddress = new StringProperty("leasedAddress", "none");
        pLeaseAddress.setLabel("Leased IP Address");
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        pStatusText = new StringProperty("statusText");
        StringProperty astringproperty[] = { pTimeout, pRequestAddress, pRoundTripTime, pLeaseAddress, pStatus, pStatusText };
        addProperties("COM.dragonflow.StandardMonitor.DHCPMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.DHCPMonitor", Rule.stringToClassifier("status != ok\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.DHCPMonitor", Rule.stringToClassifier("status == ok\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.DHCPMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "description", "Determines whether an IP address can be obtained from a DHCP server.");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "help", "DHCPMonitor.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "title", "DHCP");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "class", "DHCPMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "classType", "server");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "target", "_hostname");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "topazName", "DHCP");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "topazType", "System Resources");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "toolPageDisable", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "loadable", "true");
        try {
            Class.forName("edu.bucknell.net.JDHCP.DHCPSocket");
        } catch (ClassNotFoundException classnotfoundexception) {
            setClassProperty("COM.dragonflow.StandardMonitor.DHCPMonitor", "loadable", "false");
        }
    }
}
