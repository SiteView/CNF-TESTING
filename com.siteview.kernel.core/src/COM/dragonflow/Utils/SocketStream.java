/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

import com.recursionsw.jgl.HashMap;
import sun.misc.BASE64Encoder;
import COM.dragonflow.StandardMonitor.URLScannerInputStream;

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils, SSLSocketStream, SocketSession, FileUtils,
// I18N

public class SocketStream {

    private java.lang.Object readLock;

    private static int maxHeaderLineLength = 10240;

    private static final int READ_LINE_BUF_SIZE = 256;

    static javax.net.SocketFactory sslSocketFactory = null;

    static javax.net.ServerSocketFactory sslServerSocketFactory = null;

    static HashMap sslFactories = new HashMap();

    public java.net.Socket socket;

    private static final int CR = 13;

    private static final int LF = 10;

    private java.io.PrintWriter out;

    private java.io.BufferedInputStream in;

    public java.net.Socket tunnelSocket;

    private java.io.PrintWriter tunnelOut;

    private java.io.BufferedInputStream tunnelIn;

    public java.lang.Process process;

    public String key;

    public String protocol;

    private int port;

    private java.net.InetAddress address;

    private COM.dragonflow.Utils.SocketSession session;

    private String tunnelHost;

    private int tunnelPort;

    private long tunnelTimeout;

    private String proxyPassword;

    private String proxyUserId;

    public boolean keepAlive;

    public boolean receivedEndOfStream;

    public boolean receivedEndOfStreamOnFirst;

    public boolean receivedReply;

    private String socketEncoding;

    static long kTimedOutValue = 10L;

    public static void initialize(HashMap hashmap) {
        String s = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/serverKeystore";
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpSecureKeystorePassword");
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpSecureKeyPassword");
        if (s2.length() <= 0) {
            s2 = s1;
        }
        String s3 = COM.dragonflow.Utils.SSLSocketStream.initialize(s, s1, s2);
        sslSocketFactory = COM.dragonflow.Utils.SSLSocketStream.getSocketFactory();
        if (sslSocketFactory != null) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Using java SSL libraries");
        } else if (COM.dragonflow.SiteView.Platform.isWindows()) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Using WinInet SSL libraries");
        } else {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "No SSL libraries found");
        }
        sslServerSocketFactory = COM.dragonflow.Utils.SSLSocketStream.getServerSocketFactory();
        COM.dragonflow.Log.LogManager.log("RunMonitor", s3);
        if ((maxHeaderLineLength = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_maxHTTPHeaderLineLength"))) < 256) {
            maxHeaderLineLength = 256;
        }
    }

    public static javax.net.SocketFactory getSSLFactory() {
        return sslSocketFactory;
    }

    public javax.net.SocketFactory getSSLCertFactory() {
        javax.net.SocketFactory socketfactory = sslSocketFactory;
        String s = session.certFilename;
        if (s.length() > 0) {
            String s1 = session.certPassword;
            String s2 = s + "/" + s1;
            socketfactory = (javax.net.SocketFactory) sslFactories.get(s2);
            if (socketfactory == null) {
                StringBuffer stringbuffer = new StringBuffer();
                socketfactory = COM.dragonflow.Utils.SSLSocketStream.getSSLFactory(s, s1, stringbuffer);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "loading client certificate, " + s + ", " + stringbuffer);
                if (socketfactory == null) {
                    socketfactory = sslSocketFactory;
                }
                sslFactories.put(s2, socketfactory);
            }
        }
        return socketfactory;
    }

    public static javax.net.ServerSocketFactory getSSLServerFactory() {
        return sslServerSocketFactory;
    }

    SocketStream(COM.dragonflow.Utils.SocketSession socketsession, String s, java.net.InetAddress inetaddress, int i, String s1) throws java.io.IOException {
        readLock = new Object();
        socket = null;
        out = null;
        in = null;
        tunnelSocket = null;
        tunnelOut = null;
        tunnelIn = null;
        process = null;
        key = "";
        protocol = "";
        port = 80;
        address = null;
        session = null;
        tunnelHost = null;
        tunnelPort = -1;
        tunnelTimeout = -1L;
        proxyPassword = null;
        proxyUserId = null;
        keepAlive = false;
        receivedEndOfStream = false;
        receivedEndOfStreamOnFirst = false;
        receivedReply = false;
        socketEncoding = "";
        initSocketStream(socketsession, s, inetaddress, i, s1, tunnelHost, tunnelPort, proxyUserId, proxyPassword, tunnelTimeout);
    }

    SocketStream(COM.dragonflow.Utils.SocketSession socketsession, String s, java.net.InetAddress inetaddress, int i, String s1, String s2, int j, String s3, String s4, long l, String s5)
            throws java.io.IOException {
        readLock = new Object();
        socket = null;
        out = null;
        in = null;
        tunnelSocket = null;
        tunnelOut = null;
        tunnelIn = null;
        process = null;
        key = "";
        protocol = "";
        port = 80;
        address = null;
        session = null;
        tunnelHost = null;
        tunnelPort = -1;
        tunnelTimeout = -1L;
        proxyPassword = null;
        proxyUserId = null;
        keepAlive = false;
        receivedEndOfStream = false;
        receivedEndOfStreamOnFirst = false;
        receivedReply = false;
        socketEncoding = "";
        if (s5 != null && s5.length() > 0) {
            socketEncoding = new String(s5);
        }
        initSocketStream(socketsession, s, inetaddress, i, s1, s2, j, s3, s4, l);
    }

    void initSocketStream(COM.dragonflow.Utils.SocketSession socketsession, String s, java.net.InetAddress inetaddress, int i, String s1, String s2, int j, String s3, String s4, long l)
            throws java.io.IOException {
        key = s;
        session = socketsession;
        protocol = s1;
        address = inetaddress;
        port = i;
        tunnelHost = s2;
        tunnelPort = j;
        proxyPassword = s4;
        proxyUserId = s3;
        tunnelTimeout = l;
        connect();
    }

    public String toString() {
        return COM.dragonflow.SiteView.Platform.dottedIPString(address.getAddress()) + ":" + port;
    }

    private void connect() throws java.io.IOException {
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugSocket) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "connection opening... " + this);
        }
        if (!protocol.equals("https")) {
            socket = new Socket(address, port);
        } else if (tunnelHost == null) {
            socket = getSSLCertFactory().createSocket(address, port);
        } else {
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugSocket) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "tunnel socket to ... " + address + ":" + port);
            }
            tunnelSocket = new Socket(address, port);
            if (socketEncoding.length() == 0) {
                tunnelOut = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(tunnelSocket.getOutputStream());
                socketEncoding = COM.dragonflow.Utils.I18N.nullEncoding();
            } else {
                tunnelOut = COM.dragonflow.Utils.FileUtils.MakeEncodedOutputWriter(tunnelSocket.getOutputStream(), socketEncoding);
            }
            tunnelIn = new BufferedInputStream(tunnelSocket.getInputStream());
            tunnelThruProxy();
            try {
                javax.net.ssl.SSLSocketFactory sslsocketfactory = (javax.net.ssl.SSLSocketFactory) getSSLCertFactory();
                socket = COM.dragonflow.Utils.SSLSocketStream.createSSLFactorySocket(sslsocketfactory, tunnelSocket, tunnelHost, tunnelPort, true);
            } catch (java.lang.Exception exception) {
                String s = "createSSLFactorySocket(socket, " + tunnelHost + ", " + tunnelPort + ", true) exception " + exception.getMessage();
                COM.dragonflow.Log.LogManager.log("RunMonitor", s);
                COM.dragonflow.Log.LogManager.log("Error", s);
                throw new IOException(s);
            }
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugSocket) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "connect(): socket (" + socket.getInetAddress() + ") tunnel overlaid socket created, " + this);
            }
        }
        if (socketEncoding.length() == 0) {
            out = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(socket.getOutputStream());
            socketEncoding = COM.dragonflow.Utils.I18N.nullEncoding();
        } else {
            out = COM.dragonflow.Utils.FileUtils.MakeEncodedOutputWriter(socket.getOutputStream(), socketEncoding);
        }
        in = new BufferedInputStream(socket.getInputStream());
        receivedEndOfStream = false;
        receivedReply = false;
    }

    void tunnelThruProxy() throws java.io.IOException {
        if (protocol.equals("https") && tunnelHost != null) {
            String s = tunnelHost + ":" + tunnelPort;
            String s1 = "CONNECT " + s + " HTTP/1.0" + COM.dragonflow.StandardMonitor.URLMonitor.CRLF;
            s1 = s1 + "User-Agent: " + sun.net.www.protocol.http.HttpURLConnection.userAgent + COM.dragonflow.StandardMonitor.URLMonitor.CRLF;
            s1 = s1 + "Proxy-Authorization:Basic ";
            String s2 = "";
            if (proxyUserId != null) {
                s2 = proxyUserId;
            }
            s2 = s2 + ':';
            if (proxyPassword != null) {
                s2 = s2 + proxyPassword;
            }
            if (s2.length() > 1) {
                if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "tunnelAuthenticationString:  " + s2);
                }
                s1 = s1 + (new BASE64Encoder()).encode(s2.getBytes());
            }
            s1 = s1 + COM.dragonflow.StandardMonitor.URLMonitor.CRLF + COM.dragonflow.StandardMonitor.URLMonitor.CRLF;
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "tunnelString:  " + s1);
            }
            transmit(tunnelOut, s1);
            int i = 1024;
            byte abyte0[] = new byte[i];
            StringBuffer stringbuffer = new StringBuffer(i);
            long al[] = fillBuffer(tunnelSocket, tunnelIn, abyte0, tunnelTimeout, stringbuffer, i, java.lang.System.currentTimeMillis(), 2);
            if (al[0] <= 0L) {
                throw new IOException("Tunnel Failed, no reply");
            }
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugData) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "tunnelReply=" + stringbuffer);
            }
            COM.dragonflow.StandardMonitor.URLScannerInputStream urlscannerinputstream = new URLScannerInputStream(stringbuffer, COM.dragonflow.StandardMonitor.URLMonitor.kURLok);
            urlscannerinputstream.parse();
            if (urlscannerinputstream.status != (long) COM.dragonflow.StandardMonitor.URLMonitor.kURLok) {
                throw new IOException("Tunnel Failed, status error = " + urlscannerinputstream.status);
            }
        }
    }

    public void transmit(String s) throws java.io.IOException {
        transmit(s, ((StringBuffer) (null)));
    }

    public void transmit(String s, StringBuffer stringbuffer) throws java.io.IOException {
        transmit(out, s, stringbuffer);
    }

    public void transmit(java.io.PrintWriter printwriter, String s) throws java.io.IOException {
        transmit(printwriter, s, null);
    }

    public void transmit(java.io.PrintWriter printwriter, String s, StringBuffer stringbuffer) throws java.io.IOException {
        printwriter.write(s);
        printwriter.flush();
        if (stringbuffer != null) {
            stringbuffer.append(s);
        }
    }

    /**
     * 
     * 
     * @param abyte0
     * @param l
     * @param stringbuffer
     * @param l1
     * @param l2
     * @return
     * @throws java.io.IOException
     */
    public long[] fillBuffer(byte abyte0[], long l, StringBuffer stringbuffer, long l1, long l2) throws java.io.IOException {
        synchronized (readLock) {
            try {
                return fillBuffer(socket, in, abyte0, l, stringbuffer, l1, l2, -1);
            } catch (IOException exception) {
                throw exception;
            }
        }
    }

    /**
     * 
     * 
     * @param socket1
     * @param bufferedinputstream
     * @param abyte0
     * @param l
     * @param stringbuffer
     * @param l1
     * @param l2
     * @param i
     * @return
     * @throws java.io.IOException
     */
    long[] fillBuffer(java.net.Socket socket1, java.io.BufferedInputStream bufferedinputstream, byte abyte0[], long l, StringBuffer stringbuffer, long l1, long l2, int i) throws java.io.IOException {
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "reading HTTP reply");
        }
        receivedReply = false;
        long al[] = new long[2];
        if (stringbuffer != null) {
            stringbuffer.setLength(0);
        }
        boolean flag = false;
        long l3 = 0L;
        long l4 = -1L;
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "read, clock: " + java.lang.System.currentTimeMillis() + " timeout value = " + l);
        }
        int k = 0;
        int i1 = 0;
        while (true) {
            int j1 = (int) (l - java.lang.System.currentTimeMillis());
            if ((long) j1 <= kTimedOutValue) {
                throw new InterruptedIOException();
            }
            COM.dragonflow.SiteView.Platform.setSocketTimeout(socket1, j1);
            int j = bufferedinputstream.read(abyte0, 0, abyte0.length);
            if (l4 == -1L && j != 0) {
                l4 = java.lang.System.currentTimeMillis() - l2;
            }
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "read, size: " + j + ", clock: " + java.lang.System.currentTimeMillis());
            }
            if (j == -1) {
                receivedEndOfStream = true;
                if (k == 0) {
                    receivedEndOfStreamOnFirst = true;
                }
                continue;
            }
            receivedReply = true;
            l3 += j;
            if (stringbuffer != null && l3 < l1) {
                stringbuffer.append(new String(abyte0, 0, j, socketEncoding));
            }
            if (i > 0) {
                int k1;
                while ((k1 = stringbuffer.indexOf("\n", i1)) != -1) {
                    k ++;
                    i1 = k1 + 1;
                }
                if (k <= i) {
                    break;
                }
            }
        }
        if (l4 == -1L) {
            l4 = 0L;
        }
        al[0] = l3;
        al[1] = l4;
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugIO) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "read, total size: " + l3 + ", clock: " + java.lang.System.currentTimeMillis());
        }
        if (l - java.lang.System.currentTimeMillis() <= kTimedOutValue) {
            throw new InterruptedIOException();
        } else {
            return al;
        }
    }

    public String getEncoding() {
        return socketEncoding;
    }

    /**
     * 
     * 
     * @param abyte0
     * @param i
     * @param j
     * @return
     * @throws java.io.IOException
     */
    public int read(byte abyte0[], int i, int j) throws java.io.IOException {
        synchronized (readLock) {
            try {
                return in.read(abyte0, i, j);
            } catch (IOException exception) {
                throw exception;
            }
        }
    }

    public String HTTPReadLine() throws java.io.IOException {
        byte abyte0[] = new byte[256];
        int i = 0;
        int j;
        synchronized (readLock) {
            while ((j = in.read()) != -1) {
                if (j == 13) {
                    if (in.available() <= 0) {
                        break;
                    }
                    in.mark(1);
                    if (in.read() != 10) {
                        in.reset();
                    }
                    break;
                }
                if (j == 10 || i == maxHeaderLineLength) {
                    break;
                }
                if (i >= abyte0.length) {
                    abyte0 = increaseBuffer(abyte0, 256, maxHeaderLineLength);
                }
                abyte0[i ++] = (byte) j;
            }
        }
        if (i == 0 && j == -1) {
            return null;
        } else {
            return new String(abyte0, 0, i, socketEncoding);
        }
    }

    private byte[] increaseBuffer(byte abyte0[], int i, int j) {
        if (abyte0.length >= j) {
            return abyte0;
        }
        int k = abyte0.length + i;
        if (k >= j) {
            k = j;
        }
        byte abyte1[] = new byte[k];
        java.lang.System.arraycopy(abyte0, 0, abyte1, 0, abyte0.length);
        return abyte1;
    }

    public void reconnect() throws java.io.IOException {
        close();
        connect();
    }

    void close() {
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugSocket) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "connection closing... " + this);
        }
        try {
            if (process != null) {
                process.destroy();
            }
        } catch (java.lang.Exception exception) {
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (java.lang.Exception exception1) {
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (java.io.IOException ioexception) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (java.lang.Exception exception2) {
        }
        try {
            if (tunnelOut != null) {
                tunnelOut.close();
            }
        } catch (java.lang.Exception exception3) {
        }
        try {
            if (tunnelIn != null) {
                tunnelIn.close();
            }
        } catch (java.io.IOException ioexception1) {
        }
        try {
            if (tunnelSocket != null) {
                tunnelSocket.close();
            }
        } catch (java.lang.Exception exception4) {
        }
        socket = null;
        in = null;
        out = null;
        tunnelSocket = null;
        tunnelIn = null;
        tunnelOut = null;
        process = null;
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugSocket) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "connection closed " + this);
        }
    }

}
