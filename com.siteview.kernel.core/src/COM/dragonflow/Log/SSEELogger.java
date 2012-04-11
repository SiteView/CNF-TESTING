/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

// Referenced classes of package COM.dragonflow.Log:
// Logger, LogManager

public class SSEELogger extends COM.dragonflow.Log.Logger {
    public class SocketConnectionThread extends java.lang.Thread {

        private boolean m_bIsAlive;

        public boolean isThreadAlive() {
            return m_bIsAlive;
        }

        public void stopThread() {
            m_bIsAlive = false;
        }

        public void run() {
            try {
                try {
                    m_bIsAlive = true;
                    echoSocket = new Socket(respondHostname, respondPort);
                    out = new PrintWriter(echoSocket.getOutputStream());
                    m_bIsAlive = false;
                } catch (java.net.UnknownHostException unknownhostexception) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Don't know about host.");
                } catch (java.io.IOException ioexception) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Couldn't get I/O for the connection.");
                }
            } catch (java.lang.Exception exception) {
                COM.dragonflow.Log.LogManager.log("Error", "ThreadPool: Failed to execute a thread in the thread pool, Exception: " + exception.getMessage());
            }
        }

        public SocketConnectionThread(String s) {
            super(s);
            m_bIsAlive = true;
        }
    }

    public boolean writeDate;

    String directoryPath;

    String suffix;

    String baseFileName;

    String basePath;

    private java.net.Socket echoSocket;

    private java.io.PrintWriter out;

    private String respondHostname;

    private int respondPort;

    private static String ssHostname = "";

    private static int ssPort = 0;

    private static int loadFactor = 1;

    private static String dnRoot = "";

    public SSEELogger(String s, int i, String s1, int j, String s2) throws java.io.IOException {
        writeDate = true;
        directoryPath = "";
        suffix = "";
        baseFileName = "";
        basePath = "";
        echoSocket = null;
        out = null;
        respondHostname = "";
        respondPort = 0;
        respondHostname = s;
        respondPort = i;
        COM.dragonflow.Log.SSEELogger _tmp = this;
        ssHostname = s1;
        COM.dragonflow.Log.SSEELogger _tmp1 = this;
        ssPort = j;
        COM.dragonflow.Log.SSEELogger _tmp2 = this;
        dnRoot = s2;
        SocketConnectionThread socketconnectionthread = new SocketConnectionThread("SocketConnectionThread");
        socketconnectionthread.start();
        java.util.Date date = new Date();
        long l = date.getTime();
        long l1 = 0L;
        do {
            java.util.Date date1 = new Date();
            long l2 = date1.getTime();
            l1 = l2 - l;
        } while (l1 < 2000L && socketconnectionthread.isThreadAlive());
        if (socketconnectionthread.isThreadAlive()) {
            socketconnectionthread.stopThread();
            COM.dragonflow.Log.LogManager.log("Unable to establish a RTS/Heartbeat socket connection to host: " + s + " and port: " + i + ". Check your firewall or network settings to insure that a socket connection is permitted.");
            throw new IOException("Unable to establish a RTS/Heartbeat socket connection on host: " + s1 + " and port: " + j + ". Check your firewall or network settings to insure that a socket connection is permitted.");
        }
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_sseeLoggerLoadFactor");
        if (s3 != null && s3.length() > 0) {
            loadFactor = java.lang.Integer.parseInt(s3);
        }
    }

    public static String getSSHostname() {
        return ssHostname;
    }

    public static int getSSPort() {
        return ssPort;
    }

    public void reinitialize() {
        try {
            close();
            echoSocket = new Socket(respondHostname, respondPort);
            out = new PrintWriter(echoSocket.getOutputStream());
        } catch (java.io.IOException ioexception) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Couldn't re-initialize I/O for the connection.");
        }
    }

    private static void appendSSEEStatusProperties(StringBuffer stringbuffer, jgl.HashMap hashmap, String s, String s1, String s2) {
        boolean flag = true;
        Enumeration enumeration = hashmap.keys();
        while (enumeration.hasMoreElements()) {
            java.lang.Object obj = enumeration.nextElement();
            String s3;
            if (obj instanceof COM.dragonflow.Properties.StringProperty) {
                s3 = ((COM.dragonflow.Properties.StringProperty) obj).getName();
            } else {
                s3 = (String) obj;
            }
            if (s3.equals(s)) {
                flag = false;
                java.lang.Object obj1;
                for (Enumeration enumeration1 = hashmap.values(obj); enumeration1.hasMoreElements(); stringbuffer.append(obj1)) {
                    obj1 = enumeration1.nextElement();
                }

                stringbuffer.append(s2);
            }
        } 
        if (flag) {
            stringbuffer.append(s1 + s2);
        }
    }

    protected static void createAndSendSSEEStatusRecord(COM.dragonflow.Properties.PropertiedObject propertiedobject, jgl.HashMap hashmap, String s) {
        java.util.Vector vector = new Vector();
        vector.add("stateString");
        vector.add("category");
        vector.add("last");
        vector.add("_id");
        try {
            StringBuffer stringbuffer = new StringBuffer();
            COM.dragonflow.Log.SSEELogger.appendSSEEStatusProperties(stringbuffer, hashmap, "stateString", " ", "@");
            COM.dragonflow.Log.SSEELogger.appendSSEEStatusProperties(stringbuffer, hashmap, "category", " ", "@");
            COM.dragonflow.Log.SSEELogger.appendSSEEStatusProperties(stringbuffer, hashmap, "last", "0", "@");
            COM.dragonflow.Log.SSEELogger.appendSSEEStatusProperties(stringbuffer, hashmap, "_id", " ", "@");
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            StringBuffer stringbuffer1 = new StringBuffer();
            COM.dragonflow.Log.SSEELogger.appendSSEEStatusProperties(stringbuffer1, hashmap, "_id", " ", "");
            String s1 = "m=" + stringbuffer1.toString();
            COM.dragonflow.SiteView.MonitorGroup monitorgroup;
            do {
                s1 = s1 + ",g=" + s;
                monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) siteviewgroup.getElementByID(s);
                monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) monitorgroup.getParent();
                if (monitorgroup != null) {
                    s = monitorgroup.getProperty("_id");
                }
            } while (monitorgroup != null);
            s1 = s1 + "," + dnRoot;
            stringbuffer.append(COM.dragonflow.Log.SSEELogger.getSSHostname() + "@" + (new Integer(COM.dragonflow.Log.SSEELogger.getSSPort())).toString() + "@");
            stringbuffer.append(s1);
            byte abyte0[] = new byte[11];
            abyte0[0] = 126;
            abyte0[1] = 126;
            String s2 = stringbuffer.toString();
            int i = s2.length();
            String s3 = (new Integer(i)).toString();
            byte abyte1[] = s2.getBytes();
            int j = 6 - s3.length();
            for (int k = 0; j < 6; k ++) {
                abyte0[j + 2] = java.lang.Byte.decode(s3.substring(k, k + 1)).byteValue();
                j ++;
            }

            abyte0[8] = 2;
            char ac[] = new char[abyte1.length + 9];
            for (int l = 0; l < 9; l ++) {
                ac[l] = (char) abyte0[l];
            }

            for (int i1 = 0; i1 < abyte1.length; i1 ++) {
                ac[i1 + 9] = (char) abyte1[i1];
            }

            for (int j1 = 0; j1 < loadFactor; j1 ++) {
                COM.dragonflow.Log.LogManager.log("SSEELog", new String(ac));
            }

        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
    }

    public synchronized void log(String s, java.util.Date date, COM.dragonflow.Properties.PropertiedObject propertiedobject) {
        if (COM.dragonflow.Log.LogManager.loggerRegistered("SSEELog")) {
            COM.dragonflow.Log.SSEELogger.createAndSendSSEEStatusRecord(propertiedobject, propertiedobject.getValuesTable(), propertiedobject.getProperty("groupID"));
        }
    }

    public synchronized void log(String s, java.util.Date date, String s1) {
        try {
            char ac[] = s1.toCharArray();
            out.write(ac, 0, ac.length);
            out.flush();
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Couldn't get I/O for the connection.");
            try {
                echoSocket = new Socket(respondHostname, respondPort);
                if (echoSocket != null) {
                    out = new PrintWriter(echoSocket.getOutputStream());
                } else {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Couldn't get I/O for the connection. Unregistering SSEE logger.");
                    COM.dragonflow.Log.LogManager.registerLogger("SSEE", null);
                }
            } catch (java.io.IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public void close() {
        out.close();
        try {
            echoSocket.close();
            out = null;
            echoSocket = null;
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

}
