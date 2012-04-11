/*
 * 
 * Created on 2005-2-16 16:16:26
 *
 * PlatformNew.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PlatformNew</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.zip.CRC32;

public class PlatformNew {

    public static int timeZoneOffset = -1;

    public static int gmtOffset = -1;

    public PlatformNew() {
    }

    public static void printTimezone() {
        System.out.println("timezone: " + TimeZone.getDefault().getID()
                + ", offset: " + TimeZone.getDefault().getRawOffset());
    }

    public static long crc(String s) {
        CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        return crc32.getValue();
    }

    public static boolean noRoute(Exception exception) {
        return exception instanceof NoRouteToHostException;
    }

    public static void fixTimeZoneDefault(int i) {
        timeZoneOffset = i;
        SimpleTimeZone simpletimezone = new SimpleTimeZone(-i, "NT");
        TimeZone.setDefault(simpletimezone);
        gmtOffset = simpletimezone.getRawOffset();
    }

    public static void runFinalizersOnExit(boolean flag) {
        System.runFinalizersOnExit(flag);
    }

    public static void setSocketTimeout(Socket socket, int i) {
        if (i <= 0) {
            System.out.println("*** timeout already expired: " + i);
            i = 1;
        }
        if (socket == null) {
            return;
        }
        try {
            socket.setSoTimeout(i);
        } catch (SocketException socketexception) {
            System.out.println(socketexception);
        }
        return;
    }

    public static void setSocketTimeout(DatagramSocket datagramsocket, int i) {
        if (i <= 0) {
            System.out.println("*** timeout already expired: " + i);
            i = 1;
        }
        try {
            datagramsocket.setSoTimeout(i);
        } catch (SocketException socketexception) {
            System.out.println(socketexception);
        }
    }

    public static Socket createSocketOnLocalPort(String s, int i, int j)
            throws IOException {
        InetAddress inetaddress;
        try {
            inetaddress = InetAddress.getLocalHost();
        } catch (UnknownHostException unknownhostexception) {
            inetaddress = null;
        }
        return new Socket(s, i, inetaddress, j);
    }

    public static Socket createSocketOnLocalPort(String s, int i,
            InetAddress inetaddress, int j) throws IOException {
        return new Socket(s, i, inetaddress, j);
    }

}