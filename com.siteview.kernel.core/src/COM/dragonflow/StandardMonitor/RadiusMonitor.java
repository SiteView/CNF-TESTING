/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * RadiusMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>RadiusMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;
import SiteViewMain.MD5;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Random;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor

public class RadiusMonitor extends AtomicMonitor
{

    public static byte RADIUS_REQUEST = 1;
    public static byte RADIUS_OK = 2;
    public static byte RADIUS_UNAUTHORIZED = 3;
    public static byte RADIUS_USERNAME = 1;
    public static byte RADIUS_PASSWORD = 2;
    static StringProperty pServer;
    static StringProperty pSecret;
    static StringProperty pTimeout;
    static StringProperty pPort;
    static StringProperty pUserName;
    static StringProperty pPassword;
    static StringProperty pContentMatch;
    static StringProperty pStatus;
    static StringProperty pRoundTripTime;
    static StringProperty pMatchValue;
    static boolean debug = false;
    static byte nextID = 1;

    public RadiusMonitor()
    {
    }

    public String getHostname()
    {
        return getProperty(pServer);
    }

    protected boolean update()
    {
        if(debug)
        {
            System.out.println("RadiusMonitor.update: ENTERING");
        }
        int i = StringProperty.toInteger(getProperty(pTimeout));
        if(i == 0)
        {
            i = URLMonitor.DEFAULT_TIMEOUT;
        } else
        {
            i *= 1000;
        }
        if(debug)
        {
            System.out.println("RadiusMonitor.update: timeout: " + i);
        }
        StringBuffer stringbuffer = new StringBuffer();
        long l = 0L;
        String s = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
        if(debug)
        {
            System.out.println("RadiusMonitor.update: match: " + s);
        }
        long l1 = Platform.timeMillis();
        if(debug)
        {
            System.out.println("RadiusMonitor.update: starttime: " + l1);
        }
        long l2 = kURLUnknownError;
        try
        {
            l2 = sendRequest(getProperty(pServer), (int)getPropertyAsLong(pPort), getProperty(pSecret), getProperty(pUserName), getProperty(pPassword), i, stringbuffer);
            if(debug)
            {
                System.out.println("RadiusMonitor.update: sendRequest Response status: " + l2);
            }
            l = Platform.timeMillis() - l1;
            if(debug)
            {
                System.out.println("RadiusMonitor.update: totalTime: " + l);
            }
        }
        catch(UnknownHostException unknownhostexception)
        {
            l2 = kURLBadHostNameError;
        }
        catch(InterruptedIOException interruptedioexception)
        {
            l2 = kURLTimeoutError;
        }
        catch(Exception exception)
        {
            LogManager.log("RunMonitor", "unknown error, " + getProperty(pName) + ", " + exception);
        }
        if(l2 == (long)kURLok && s.length() > 0)
        {
            Array array = new Array();
            l2 = TextUtils.matchExpression(stringbuffer.toString(), s, array, new StringBuffer());
            if(l2 != (long)Monitor.kURLok)
            {
                String s1 = URLMonitor.getHTMLEncoding(stringbuffer.toString());
                l2 = TextUtils.matchExpression(stringbuffer.toString(), I18N.UnicodeToString(s, s1), array, new StringBuffer());
            }
            if(array.size() > 0)
            {
                setProperty(pMatchValue, array.at(0));
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                String s2 = "";
                if(l2 == (long)kURLok)
                {
                    float f = (float)l / 1000F;
                    String s3 = TextUtils.floatToString(f, 2) + " sec";
                    setProperty(pRoundTripTime, l);
                    setProperty(pMeasurement, getMeasurement(pRoundTripTime, 1000L));
                    s2 = s3;
                } else
                {
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pNoData, "n/a");
                    s2 = URLMonitor.lookupStatus(l2);
                }
                if(stringbuffer.length() > 0)
                {
                    s2 = s2 + ", " + stringbuffer;
                }
                setProperty(pStateString, I18N.StringToUnicode(s2, I18N.nullEncoding()));
                setProperty(pStatus, l2);
            }
        }
        if(debug)
        {
            System.out.println("RadiusMonitor.update: LEAVING, return true");
        }
        return true;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pServer)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if(stringproperty == pTimeout)
        {
            if(TextUtils.digits(s) != s.length())
            {
                hashmap.put(stringproperty, "time out must be a number");
            } else
            if(TextUtils.toInt(s) < 1)
            {
                hashmap.put(stringproperty, "time out must be greater than 0");
            }
            return s;
        }
        if(stringproperty == pUserName || stringproperty == pPassword || stringproperty == pSecret)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if(stringproperty == pContentMatch)
        {
            String s1 = TextUtils.legalMatchString(s);
            if(s1.length() > 0)
            {
                hashmap.put(stringproperty, s1);
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static int sendRequest(String s, int i, String s1, String s2, String s3, int j, StringBuffer stringbuffer)
        throws Exception
    {
        if(debug)
        {
            System.out.println("RadiusMonitor.sendRequest: ENTERING: hostname: " + s + " port: " + i + " secret: " + s1 + " username: " + s2 + " password: " + s3 + " timeout: " + j);
        }
        int k = kURLUnknownError;
        byte byte0 = 16;
        int l = (short)(s2.length() + byte0 + 24);
        byte abyte0[] = new byte[16];
        Random random = new Random();
        for(int i1 = 0; i1 < 16; i1++)
        {
            abyte0[i1] = (byte)random.nextInt();
        }

        byte abyte1[] = md5(s1, s3, abyte0);
        int j1 = 0;
        byte abyte2[] = new byte[1000];
        abyte2[j1++] = RADIUS_REQUEST;
        abyte2[j1++] = nextID++;
        abyte2[j1++] = 0;
        abyte2[j1++] = (byte)l;
        for(int k1 = 0; k1 < 16; k1++)
        {
            abyte2[j1++] = abyte0[k1];
        }

        abyte2[j1++] = RADIUS_USERNAME;
        abyte2[j1++] = (byte)(s2.length() + 2);
        for(int l1 = 0; l1 < s2.length(); l1++)
        {
            abyte2[j1++] = (byte)s2.charAt(l1);
        }

        abyte2[j1++] = RADIUS_PASSWORD;
        abyte2[j1++] = (byte)(byte0 + 2);
        for(int i2 = 0; i2 < abyte1.length; i2++)
        {
            abyte2[j1++] = abyte1[i2];
        }

        l = j1;
        byte abyte3[] = new byte[j1];
        for(int j2 = 0; j2 < j1; j2++)
        {
            abyte3[j2] = abyte2[j2];
        }

        int k2 = 300;
        if(abyte3.length < k2)
        {
            k2 = abyte3.length;
        }
        for(int i3 = 0; i3 < k2; i3++)
        {
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: our data byte array - up to 300 bytes index: " + i3 + "int value: " + abyte3[i3] + " char value: " + (char)abyte3[i3]);
            }
        }

        try
        {
            DatagramPacket datagrampacket = new DatagramPacket(new byte[4096], 4096);
            datagrampacket.setPort(i);
            datagrampacket.setAddress(InetAddress.getByName(s));
            datagrampacket.setLength(l);
            datagrampacket.setData(abyte3);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: set request with port: " + i + " InetAddress.getByName(hostname): " + InetAddress.getByName(s));
            }
            DatagramSocket datagramsocket = new DatagramSocket();
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: BEFORE connect request using socket");
            }
            datagramsocket.connect(InetAddress.getByName(s), i);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: AFTER connect request using socket");
                System.out.println("RadiusMonitor.sendRequest: ################ SOCKET INFO");
                System.out.println("RadiusMonitor.sendRequest: socket.isConnected: " + datagramsocket.isConnected());
                System.out.println("RadiusMonitor.sendRequest: socket.isBound: " + datagramsocket.isBound());
                System.out.println("RadiusMonitor.sendRequest: socket.isClosed: " + datagramsocket.isClosed());
                System.out.println("RadiusMonitor.sendRequest: socket.getBroadcast: " + datagramsocket.getBroadcast());
                System.out.println("RadiusMonitor.sendRequest: socket.getChannel: " + datagramsocket.getChannel());
                System.out.println("RadiusMonitor.sendRequest: socket.getReceiveBufferSize: " + datagramsocket.getReceiveBufferSize());
                System.out.println("RadiusMonitor.sendRequest: socket.getRemoteSocketAddress: " + datagramsocket.getRemoteSocketAddress());
                System.out.println("RadiusMonitor.sendRequest: socket.getSendBufferSize: " + datagramsocket.getSendBufferSize());
                System.out.println("RadiusMonitor.sendRequest: socket.getChannel: " + datagramsocket.getChannel());
                System.out.println("RadiusMonitor.sendRequest: ############# InetAddress INFO");
            }
            InetAddress inetaddress = datagramsocket.getInetAddress();
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: " + inetaddress.getHostAddress());
                System.out.println("RadiusMonitor.sendRequest: ia.isAnyLocalAddress: " + inetaddress.isAnyLocalAddress());
                System.out.println("RadiusMonitor.sendRequest: ia.getHostName: " + inetaddress.getHostName());
                System.out.println("RadiusMonitor.sendRequest: isLinkLocalAddress: " + inetaddress.isLinkLocalAddress());
                System.out.println("RadiusMonitor.sendRequest: isLoopbackAddress: " + inetaddress.isLoopbackAddress());
                System.out.println("RadiusMonitor.sendRequest: isMCGlobal: " + inetaddress.isMCGlobal());
                System.out.println("RadiusMonitor.sendRequest: isMulticastAddress: " + inetaddress.isMulticastAddress());
                System.out.println("RadiusMonitor.sendRequest: isSiteLocalAddress: " + inetaddress.isSiteLocalAddress());
            }
            datagramsocket.setSoTimeout(j);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: set socket timeout :" + j);
            }
            byte abyte4[] = datagrampacket.getData();
            int l2 = 300;
            if(abyte4.length < l2)
            {
                l2 = abyte4.length;
            }
            for(int j3 = 0; j3 < l2; j3++)
            {
                if(debug)
                {
                    System.out.println("RadiusMonitor.sendRequest: request.getData()up to 300 bytes index: " + j3 + "int value: " + abyte4[j3] + " char value: " + (char)abyte4[j3]);
                }
            }

            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: dataWeSend: " + abyte4);
            }
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: BEFORE send request using socket");
            }
            datagramsocket.send(datagrampacket);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: AFTER sent request using socket");
            }
            DatagramPacket datagrampacket1 = new DatagramPacket(new byte[4096], 4096);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: BEFORE socket.receive(reply) ");
            }
            datagramsocket.receive(datagrampacket1);
            if(debug)
            {
                System.out.println("RadiusMonitor.sendRequest: AFTER socket.receive(reply) ");
            }
            byte abyte5[] = datagrampacket1.getData();
            if(debug)
            {
                for(int k3 = 0; k3 < abyte5.length; k3++)
                {
                    System.out.println("RadiusMonitor.sendRequest: reply.getData() index: " + k3 + " value: " + abyte5[k3]);
                }

            }
            int l3 = datagrampacket1.getLength();
            k = abyte5[0];
            if(k == RADIUS_OK)
            {
                k = kURLok;
            } else
            if(k == RADIUS_UNAUTHORIZED)
            {
                k = 401;
            }
            if(k != kURLok)
            {
                LogManager.log("RunMonitor", "Radius Monitor error, " + s + ":" + i + ", " + k);
                LogManager.log("RunMonitor", "Radius Monitor sent: " + TextUtils.bytesToString(abyte3, l));
                LogManager.log("RunMonitor", "Radius Monitor received: " + TextUtils.bytesToString(abyte5, l3));
            }
            int k4;
            for(int i4 = 20; i4 < l3; i4 += k4)
            {
                int j4 = abyte5[i4] & 0xff;
                k4 = abyte5[i4 + 1] & 0xff;
                String s4 = lookupType(j4) + "=" + lookupValue(j4, abyte5, i4 + 2, k4 - 2);
                if(stringbuffer.length() > 0)
                {
                    stringbuffer.append(", ");
                }
                stringbuffer.append(s4);
            }

        }
        catch(SocketTimeoutException sockettimeoutexception)
        {
            LogManager.log("Error", "RadiusMonitor.sendRequest: SocketTimeoutException (possible login failure): " + sockettimeoutexception.toString());
        }
        catch(Exception exception)
        {
            LogManager.log("Error", "RadiusMonitor.sendRequest: Exception!!: " + exception.toString());
            exception.printStackTrace();
        }
        if(debug)
        {
            System.out.println("RadiusMonitor.sendRequest: LEAVING: result : " + k);
        }
        return k;
    }

    static String bytesToString(byte abyte0[], int i, int j)
    {
        String s = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+{}|:\"<>?,./;'[]\\-= `~";
        StringBuffer stringbuffer = new StringBuffer();
        if(j > 4)
        {
            for(int k = 0; k < j; k++)
            {
                int l = abyte0[k + i];
                if(l < 0)
                {
                    l += 128;
                }
                char c = (char)l;
                if(s.indexOf(c) == -1)
                {
                    stringbuffer.append('.');
                } else
                {
                    stringbuffer.append(c);
                }
            }

        } else
        {
            boolean flag = true;
            for(int i1 = 0; i1 < j; i1++)
            {
                int j1 = abyte0[i1 + i];
                if(j1 < 0)
                {
                    j1 += 128;
                }
                if(j1 != 0 || !flag)
                {
                    flag = false;
                    stringbuffer.append(TextUtils.toHex(j1));
                }
            }

        }
        return stringbuffer.toString();
    }

    static String lookupValue(int i, byte abyte0[], int j, int k)
    {
        return bytesToString(abyte0, j, k);
    }

    static String lookupType(int i)
    {
        switch(i)
        {
        case 1: // '\001'
            return "User-Name";

        case 2: // '\002'
            return "User-Password";

        case 3: // '\003'
            return "CHAP-Password";

        case 4: // '\004'
            return "NAS-IP-Address";

        case 5: // '\005'
            return "NAP-Port";

        case 6: // '\006'
            return "Service-Type";

        case 7: // '\007'
            return "Framed-Protocol";

        case 8: // '\b'
            return "Framed-IP-Address";

        case 9: // '\t'
            return "Framed-IP-Netmask";

        case 10: // '\n'
            return "Framed-Routing";

        case 11: // '\013'
            return "Filter-Id";

        case 12: // '\f'
            return "Framed-MTU";

        case 13: // '\r'
            return "Framed-Compression";

        case 14: // '\016'
            return "Login-IP-Host";

        case 15: // '\017'
            return "Login-Service";

        case 16: // '\020'
            return "Login-TCP-Port";

        case 18: // '\022'
            return "Reply-Message";

        case 19: // '\023'
            return "Callback-Number";

        case 20: // '\024'
            return "Callback-Id";

        case 17: // '\021'
        default:
            return "type-" + i;
        }
    }

    private static byte[] md5(String s, String s1, byte abyte0[])
    {
        MD5 md5_1 = new MD5();
        md5_1.Update(s.getBytes());
        md5_1.Update(abyte0);
        byte abyte1[] = md5_1.Final();
        byte abyte2[] = new byte[16];
        for(int i = 0; i < 16; i++)
        {
            if(i < s1.length())
            {
                abyte2[i] = (byte)(abyte1[i] ^ (byte)s1.charAt(i));
            } else
            {
                abyte2[i] = (byte)(abyte1[i] ^ 0);
            }
        }

        return abyte2;
    }

    public static void main(String args[])
        throws Exception
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i = sendRequest("localhost", 1645, "secret", "mary", "moretest", 5000, stringbuffer);
        System.out.println("result=" + i + ", results=" + stringbuffer);
        stringbuffer = new StringBuffer();
        i = sendRequest("localhost", 1645, "secret", "mary", "moretesta", 5000, stringbuffer);
        System.out.println("result=" + i + ", results=" + stringbuffer);
        stringbuffer = new StringBuffer();
        i = sendRequest("localhost", 1645, "secret", "stan", "callme", 5000, stringbuffer);
        System.out.println("result=" + i + ", results=" + stringbuffer);
    }

    static 
    {
        pServer = new StringProperty("_server", "", "Radius server");
        pServer.setDisplayText("RADIUS Server", "the IP address or host name of the RADIUS server (examples: 206.168.191.21 or radius." + Platform.exampleDomain + ")");
        pServer.setParameterOptions(true, 1, false);
        pSecret = new StringProperty("_serverPassword", "");
        pSecret.setDisplayText("Secret", "the secret phrase used to encrypt all requests to this server");
        pSecret.setParameterOptions(true, 2, false);
        pSecret.isPassword = true;
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("User Name", "the user name to authenticate");
        pUserName.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "the password to authenticate");
        pPassword.setParameterOptions(true, 4, false);
        pPassword.isPassword = true;
        pTimeout = new NumericProperty("_timeout", "30", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for a response");
        pTimeout.setParameterOptions(true, 5, true);
        pPort = new NumericProperty("_port", "1645", "");
        pPort.setDisplayText("Port", "the TCP port number of Radius server");
        pPort.setParameterOptions(true, 6, true);
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional text to match against content of the file");
        pContentMatch.setParameterOptions(true, 7, true);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("match value");
        pMatchValue.setIsThreshold(true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        StringProperty astringproperty[] = {
            pServer, pSecret, pPort, pTimeout, pUserName, pPassword, pContentMatch, pStatus, pRoundTripTime, pMatchValue
        };
        addProperties("COM.dragonflow.StandardMonitor.RadiusMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.RadiusMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.RadiusMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.RadiusMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "description", "Tests a Radius server by sending an authentication request.");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "help", "RadiusMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "title", "Radius");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "class", "RadiusMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "topazName", "Radius");
        setClassProperty("COM.dragonflow.StandardMonitor.RadiusMonitor", "topazType", "Web Application Server");
        String s = System.getProperty("RadiusMonitor.debug");
        if(s != null)
        {
            debug = true;
            System.out.println("RadiusMonitor.debug= '" + alertDebug + "'");
        }
    }
}
