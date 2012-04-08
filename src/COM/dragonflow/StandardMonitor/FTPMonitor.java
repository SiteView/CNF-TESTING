/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * FTPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>FTPMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.FileProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.PlatformNew;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

public class FTPMonitor extends AtomicMonitor {
    static long internalFTPs = 0L;

    static long internalFTPBytes = 0L;

    static long internalFTPDuration = 0L;

    static long internalFTPErrors = 0L;

    static StringProperty pServer;

    static FileProperty pFilename;

    static StringProperty pTimeout;

    static StringProperty pUserName;

    static StringProperty pPassword;

    static StringProperty pProxy;

    static StringProperty pContentMatch;

    static StringProperty pPassive;

    static StringProperty pCheckContent;

    static StringProperty pCheckContentResetTime;

    static StringProperty pProxyUserName;

    static StringProperty pProxyPassword;

    static StringProperty pStatus;

    static StringProperty pRoundTripTime;

    static StringProperty pLastChecksum;

    static StringProperty pSize;

    static StringProperty pLastBufferSize;

    static StringProperty pLastCheckContentTime;

    static StringProperty pLastCheckContentSize;

    static StringProperty pMatchValue;

    static boolean debug = false;

    static HashMap ftpLocks;

    static HashMap ftpGroupLocks;

    static int FTPCounter;

    public static String FTPStatusToString(String string) {
        int i = StringProperty.toInteger(string);
        switch (i) {
        case 200:
            return "ok";
        case 220:
            return "ready";
        case 221:
            return "closing connection";
        case 226:
            return "file retrieved";
        case 250:
            return "file retrieved";
        case 332:
            return "requires login";
        case 421:
            return "service unavailable";
        case 425:
            return "cannot open data connection";
        case 426:
            return "connection closed, transfer aborted";
        case 450:
            return "file unavailable (possibly the file is busy)";
        case 451:
            return "local error in processing";
        case 500:
            return "command not recognized";
        case 501:
            return "command syntax error";
        case 502:
            return "command not implemented";
        case 530:
            return "not logged in";
        case 550:
            return "file not found";
        case 553:
            return "illegal file name";
        default:
            if (i < 200)
                return "";
            if (i < 400)
                return "ok";
            if (i < 500)
                return "error";
            return "command error";
        }
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (getProperty(pCategory).equals("good"))
                return "";
            return diagnostic(getProperty(pServer), getPropertyAsInteger(pStatus));
        }
        return super.getProperty(stringproperty);
    }

    public String getHostname() {
        return getProperty(pServer);
    }

    public String getTestURL() {
        String string = pFilename.getName(this);
        String string_0_ = ("/SiteView/cgi/go.exe/SiteView?page=ftp&server=" + getProperty(pServer) + "&username=" + getProperty(pUserName) + "&password=" + URLEncoder.encode(TextUtils.obscure(getProperty(pPassword))) + "&filename="
                + HTTPRequest.encodeString(string) + "&passive=" + getProperty(pPassive) + "&proxy=" + getProperty(pProxy) + "&proxyUser=" + getProperty(pProxyUserName) + "&proxyPassword=" + URLEncoder.encode(TextUtils
                .obscure(getProperty(pProxyPassword))));
        return string_0_;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    CounterLock getFTPThreadLock() {
        String string = getOwner().getProperty(pID);
        String string_1_ = string;
        int i = TextUtils.toInt(getOwner().getProperty("_ftpMaxThreads"));
        if (i == 0) {
            string_1_ = "_global";
            HashMap hashmap = MasterConfig.getMasterConfig();
            i = TextUtils.toInt(TextUtils.getValue(hashmap, "_ftpMaxThreads"));
        }
        if (i == 0) {
            return null;
        }

        CounterLock counterlock;
        synchronized (ftpLocks) {
            counterlock = (CounterLock) ftpLocks.get(string_1_);
            if (counterlock == null) {
                LogManager.log("RunMonitor", "ftp lock, " + i + ", key=" + string_1_);
                counterlock = new CounterLock(i);
                ftpLocks.put(string_1_, counterlock);
            }
        }
        if (counterlock.current() == 0)
            LogManager.log("RunMonitor", ("ftp block, " + (counterlock.max - counterlock.current() + 1) + ", " + string + ", " + getProperty(pServer)));
        String string_2_ = currentStatus;
        currentStatus = "FTPMonitor ready, waiting for other FTP monitors to complete...";
        counterlock.get();
        currentStatus = string_2_;
        return counterlock;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    CounterLock getFTPGroupLock() {
        int i = TextUtils.toInt(getSetting("_ftpMaximumPerGroup"));
        if (i == 0) {
            return null;
        }
        SiteViewObject siteviewobject = getOwner();
        if (siteviewobject == null) {
            return null;
        }
        String string = siteviewobject.getProperty(pID);
        CounterLock counterlock;

        synchronized (ftpGroupLocks) {
            counterlock = (CounterLock) ftpGroupLocks.get(string);
            if (counterlock == null) {
                LogManager.log("RunMonitor", "ftp group lock, " + i + ", key=" + string);
                counterlock = new CounterLock(i);
                counterlock.name = string;
                ftpGroupLocks.put(string, counterlock);
            }
        }

        if (counterlock.current() == 0)
            LogManager.log("RunMonitor", ("group ftp block, " + (counterlock.max - counterlock.current() + 1) + ", " + string + ", " + getProperty(pServer)));
        String string_3_ = currentStatus;
        currentStatus = "ready, waiting for other FTP monitors in group to complete";
        counterlock.get();
        currentStatus = string_3_;
        return counterlock;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        int i = StringProperty.toInteger(getProperty(pTimeout));
        if (i == 0)
            i = URLMonitor.DEFAULT_TIMEOUT;
        else
            i *= 1000;
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer_4_ = new StringBuffer();
        long l = (long) getSettingAsLong("_ftpContentMatchMax", 50000);
        long l_5_ = (long) getSettingAsLong("_ftpDownloadLimit", -1);
        CounterLock counterlock = null;
        CounterLock counterlock_6_ = null;
        long l_7_ = 0L;
        String string = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
        long[] ls;
        try {
            counterlock_6_ = getFTPGroupLock();
            counterlock = getFTPThreadLock();
            String string_8_ = pFilename.getName(this);
            FTPCounter ++;
            LogManager.log("RunMonitor", ("ftp start, " + FTPCounter + "," + getProperty(pServer)));
            long l_9_ = Platform.timeMillis();
            ls = checkFTP(getProperty(pServer), getProperty(pProxy), getProperty(pProxyUserName), getProperty(pProxyPassword), getPropertyAsBoolean(pPassive), string_8_, getProperty(pUserName), getProperty(pPassword), string, stringbuffer_4_, l,
                    stringbuffer, i, l_5_, null, this);
            FTPCounter --;
            l_7_ = Platform.timeMillis() - l_9_;
            LogManager.log("RunMonitor", ("ftp end, " + FTPCounter + "," + getProperty(pServer)));
        } finally {
            if (counterlock != null)
                counterlock.release();
            if (counterlock_6_ != null)
                counterlock_6_.release();
        }
        if (ls[0] == (long) kURLok && string.length() > 0) {
            Array array = new Array();
            ls[0] = (long) TextUtils.matchExpression(stringbuffer_4_.toString(), string, array, new StringBuffer());
            if (ls[0] != (long) Monitor.kURLok) {
                String string_10_ = URLMonitor.getHTMLEncoding(stringbuffer_4_.toString());
                ls[0] = (long) (TextUtils.matchExpression(stringbuffer_4_.toString(), I18N.UnicodeToString(string, string_10_), array, new StringBuffer()));
            }
            if (array.size() > 0)
                setProperty(pMatchValue, array.at(0));
        }
        long l_11_ = ls[0];
        long l_12_ = ls[2];
        String string_13_ = "";
        if (l_11_ == (long) kURLok && getProperty(pCheckContent).length() > 0) {
            String string_14_ = getProperty(pLastChecksum);
            String string_15_ = getProperty(pLastCheckContentSize);
            String string_16_ = stringbuffer_4_.toString();
            String string_17_ = String.valueOf(PlatformNew.crc(string_16_));
            String string_18_ = "" + l_12_;
            if (string_14_.length() > 0 && !string_17_.equals(string_14_))
                l_11_ = (long) kURLContentChangedError;
            if (string_15_.length() > 0 && !string_18_.equals(string_15_))
                l_11_ = (long) kURLContentChangedError;
            string_13_ = string_14_;
            boolean bool = true;
            if (getProperty(pCheckContent).equals("baseline") && (getPropertyAsLong(pCheckContentResetTime) < getPropertyAsLong(pLastCheckContentTime)) && string_14_.length() > 0)
                bool = false;
            if (bool && getProperty(pCheckContent).equals("baseline"))
                l_11_ = (long) kURLok;
            if (bool) {
                string_13_ = string_17_;
                setProperty(pLastCheckContentTime, Platform.timeMillis());
                setProperty(pLastCheckContentSize, l_12_);
            }
        }
        String string_19_ = stringbuffer.toString();
        internalFTPs ++;
        internalFTPBytes += l_12_;
        internalFTPDuration += l_7_;
        if (l_11_ != 200L)
            internalFTPErrors ++;
        URLMonitor.internalURLs ++;
        URLMonitor.internalURLBytes += l_12_;
        URLMonitor.internalURLDuration += l_7_;
        if (l_11_ != 200L)
            URLMonitor.internalURLErrors ++;
        if (stillActive()) {
            synchronized (this) {
                setProperty(pLastChecksum, string_13_);
                if (l_11_ == (long) kURLok) {
                    float f = (float) l_7_ / 1000.0F;
                    String string_21_ = TextUtils.floatToString(f, 2) + " sec";
                    setProperty(pRoundTripTime, l_7_);
                    setProperty(pMeasurement, getMeasurement(pRoundTripTime, 15000L));
                    setProperty(pSize, l_12_);
                    float f_22_ = (float) l_12_ / 1024.0F;
                    String string_23_ = TextUtils.floatToString(f_22_ / f, 0);
                    setProperty(pStateString, I18N.StringToUnicode((string_21_ + ", " + string_23_ + "Kbytes/sec, " + l_12_ + " bytes"), I18N.nullEncoding()));
                } else {
                    if (l_11_ == (long) kMonitorSpecificError)
                        setProperty(pStateString, I18N.StringToUnicode(string_19_, I18N.nullEncoding()));
                    else
                        setProperty(pStateString, (I18N.StringToUnicode(URLMonitor.lookupStatus(l_11_), I18N.nullEncoding())));
                    setProperty(pSize, "n/a");
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pNoData, "n/a");
                }
                setProperty(pStatus, l_11_);
            }
        }
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param b1
     * @param s6
     * @param s7
     * @param s8
     * @param s9
     * @param stringBuffer1
     * @param l11
     * @param stringBuffer2
     * @param i13
     * @param l14
     * @param printWriter1
     * @param monitor
     * @return
     */
    public static long[] checkFTP(String s1, String s2, String s3, String s4, boolean b1, String s6, String s7, String s8, String s9, StringBuffer stringBuffer1, long l11, StringBuffer stringBuffer2, int i13, long l14, java.io.PrintWriter printWriter1, COM.dragonflow.SiteView.Monitor monitor) {
        long[] la = new long[4];
        la[0] = (long) kURLUnknownError;
        la[1] = 0;
        la[2] = 0;
        la[3] = 0;
        SiteViewGroup currentSSG = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if (currentSSG.getSetting("_manDNSLookup").length() > 0) {
            s1 = COM.dragonflow.SiteView.Platform.resolveDNS(s1);
        }
        if (s2.length() != 0) {
            la = FTPMonitor.retrieveFileViaProxy(s1, s2, s3, s4, s6, s7, s8, stringBuffer1, l11, stringBuffer2, i13, printWriter1, monitor);
        }
        else {
            java.net.Socket socket1 = null;
            java.io.PrintWriter printWriter2 = null;
            java.io.BufferedReader bufferedReader1 = null;
            String s24 = "";
            int i25 = 21;
            int i26 = s1.indexOf(58);
            if (i26 != -1) {
                i25 = COM.dragonflow.Utils.TextUtils.readInteger(s1, i26 + 1);
                s1 = s1.substring(0, i26);
            }
                
            //137
            try {
                long l27 = COM.dragonflow.SiteView.Platform.timeMillis() + ((long) l14);
                socket1 = new java.net.Socket(s1, i25);
                printWriter2 = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(socket1.getOutputStream());
                bufferedReader1 = COM.dragonflow.Utils.FileUtils.MakeInputReader(socket1.getInputStream());
                s24 = FTPMonitor.readStatus(bufferedReader1, socket1, l27, printWriter1);
                if (s24 == null) {
                    stringBuffer2.append("Server not responding");
                  la[0] = (long) kMonitorSpecificError;
                  return la;
                }
                //222
                if (s24.startsWith("220") == false) {
                    stringBuffer2.append(FTPMonitor.FTPStatusToString(s24.substring(0, 3)));
                    la[0] = (long) kMonitorSpecificError;
                    return la;
                }
                
                FTPMonitor.writeLine(printWriter2, new StringBuffer().append("USER ").append(s7).toString(), printWriter1);
                s24 = FTPMonitor.readStatus(bufferedReader1, socket1, l27, printWriter1);
                if (s24.startsWith("331") == false) {
                    stringBuffer2.append("login failed");
                  la[0] = (long) kMonitorSpecificError;
                  return la;
                }
                
                FTPMonitor.writeLine(printWriter2, new StringBuffer().append("PASS ").append(s8).toString(), printWriter1);
                s24 = FTPMonitor.readStatus(bufferedReader1, socket1, l27, printWriter1);
                if (s24.startsWith("230") == false) {
                    stringBuffer2.append("login failed");
                  la[0] = (long) kMonitorSpecificError;
                  return la;
                }
                
                if (stringBuffer1 == null) {
                    stringBuffer1 = new StringBuffer();
                  }
                if (b1) {
                    la = FTPMonitor.retrievePassiveFile(s6, stringBuffer1, l11, stringBuffer2, bufferedReader1, printWriter2, socket1, l27, l14, printWriter1);
                  }
                  else {
                    la = FTPMonitor.retrieveFile(s6, stringBuffer1, l11, stringBuffer2, bufferedReader1, printWriter2, socket1, l27, l14, printWriter1);
                  }

                //490
                try {
                    FTPMonitor.writeLine(printWriter2, "QUIT", printWriter1);
                    s24 = FTPMonitor.readStatus(bufferedReader1, socket1, l27, printWriter1);
                    if (s24.startsWith("221") == false) {
                        stringBuffer2.append("quit failed");
                      la[0] = (long) kMonitorSpecificError;
                      return la;
                    }
                }
                catch (IOException e1) { //551
                    /*empty*/
                }                
            }
            catch (UnknownHostException e) { //559
                la[0] = (long) kURLBadHostNameError;
            }
            catch (SocketException e) { //575
                if (COM.dragonflow.SiteView.Platform.noRoute(e)) {
                    la[0] = (long) kURLNoRouteToHostError;
                  }
                  else {
                    la[0] = (long) kURLNoConnectionError;
                  }
            }
            catch (InterruptedIOException e) { //610
                la[0] = (long) kURLTimeoutError;
            }
            catch (Exception e) { // 626
                e.printStackTrace();
                System.err.println(new StringBuffer().append(e).append(" error: ").append(e.getMessage()).toString());
                la[0] = (long) kURLUnknownError;
            }
            finally { // 689                
                try { //691
                    if (printWriter2 != null) {
                        printWriter2.close();
                      }
                      if (bufferedReader1 != null) {
                        bufferedReader1.close();
                      }
                      if (socket1 != null) {
                        socket1.close();
                      }
                }
                catch (IOException e2) { //724
                    System.err.println("Could not close FTP streams");
                }                
            }//734
        }
        //736
        if (((la[0] - ((long) kURLok)) == 0) && (s9.length() > 0)) {
            la[0] = (long) COM.dragonflow.Utils.TextUtils.matchExpression(stringBuffer1.toString(), s9);
            if ((la[0] - ((long) kURLok)) != 0) {
              String s16 = URLMonitor.getHTMLEncoding(stringBuffer1.toString());
              la[0] = (long) COM.dragonflow.Utils.TextUtils.matchExpression(stringBuffer1.toString(), COM.dragonflow.Utils.I18N.UnicodeToString(s9, s16));
            }
          }
        return la;
    }

    private static long[] retrieveFile(String string, StringBuffer stringbuffer, long l, StringBuffer stringbuffer_84_, BufferedReader bufferedreader, PrintWriter printwriter, Socket socket, long l_85_, long l_86_, PrintWriter printwriter_87_)
            throws IOException {
        SocketSession socketsession = SocketSession.getSession(null);
        ServerSocket serversocket = null;
        long[] ls = new long[4];
        ls[0] = (long) kURLok;
        ls[1] = 0L;
        ls[2] = 0L;
        ls[3] = 0L;
        try {
            serversocket = new ServerSocket(0);
        } catch (IOException ioexception) {
            stringbuffer_84_.append("Could not open local data port:  " + serversocket.getLocalPort() + ", " + ioexception);
            ls[0] = (long) kMonitorSpecificError;
            return ls;
        }
        String string_88_ = sendPort(serversocket.getLocalPort(), bufferedreader, printwriter, socket, l_85_, printwriter_87_);
        if (!string_88_.startsWith("200")) {
            stringbuffer_84_.append(FTPStatusToString(string_88_.substring(0, 3)));
            ls[0] = (long) kMonitorSpecificError;
            return ls;
        }
        writeLine(printwriter, "RETR " + string, printwriter_87_);
        string_88_ = readStatus(bufferedreader, socket, l_85_, printwriter_87_);
        if (string_88_.startsWith("1")) {
            Object object = null;
            Socket socket_89_;
            try {
                setTimedOut(serversocket, l_85_);
                socket_89_ = serversocket.accept();
            } catch (IOException ioexception) {
                stringbuffer_84_.append("Could not accept connection to local data port:  " + serversocket.getLocalPort() + ", " + ioexception);
                ls[0] = (long) kMonitorSpecificError;
                return ls;
            }
            InputStream inputstream = null;
            try {
                inputstream = socket_89_.getInputStream();
                ls[2] = fillBuffer(socketsession, inputstream, socket_89_, l_85_, l_86_, stringbuffer, l);
                if (inputstream != null) {
                    inputstream.close();
                    inputstream = null;
                }
                if (l_86_ > 0L && ls[2] >= l_86_)
                    string_88_ = ("226 Transfer partially completed, download limit reached after " + l_86_ + " bytes");
                else
                    string_88_ = readStatus(bufferedreader, socket, l_85_, printwriter_87_);
            } finally {
                try {
                    if (inputstream != null)
                        inputstream.close();
                    if (socket_89_ != null)
                        socket_89_.close();
                } catch (IOException ioexception) {
                    /* empty */
                }
            }
        } else {
            ls[0] = (long) kMonitorSpecificError;
            stringbuffer_84_.append(FTPStatusToString(string_88_.substring(0, 3)));
        }
        if (string_88_.length() >= 3)
            ls[3] = (long) TextUtils.toInt(string_88_.substring(0, 3));
        return ls;
    }

    public static long[] retrievePassiveFile(String string, StringBuffer stringbuffer, long l, StringBuffer stringbuffer_90_, BufferedReader bufferedreader, PrintWriter printwriter, Socket socket, long l_91_, long l_92_, PrintWriter printwriter_93_)
            throws IOException {
        SocketSession socketsession = SocketSession.getSession(null);
        writeLine(printwriter, "PASV", printwriter_93_);
        String string_94_ = "";
        int i = 0;
        long[] ls = new long[4];
        ls[0] = (long) kURLok;
        ls[1] = 0L;
        ls[2] = 0L;
        ls[3] = 0L;
        String string_95_ = readStatus(bufferedreader, socket, l_91_, printwriter_93_);
        if (string_95_.startsWith("227")) {
            int i_96_ = 0;
            int i_97_ = string_95_.length();
            for (int i_98_ = 4; i_98_ < string_95_.length(); i_98_ ++) {
                char c = string_95_.charAt(i_98_);
                if (i_96_ == 0 && Character.isDigit(c))
                    i_96_ = i_98_;
                if (i_96_ > 0) {
                    i_97_ = i_98_;
                    if (!Character.isDigit(c) && c != ',')
                        break;
                }
            }
            if (i_96_ >= 0 && i_97_ >= 0) {
                String string_99_ = string_95_.substring(i_96_, i_97_);
                String[] strings = TextUtils.split(string_99_, ",");
                if (strings.length == 6) {
                    i = (TextUtils.toInt(strings[4]) * 256 + TextUtils.toInt(strings[5]));
                    string_94_ = (strings[0] + "." + strings[1] + "." + strings[2] + "." + strings[3]);
                }
            }
            if (string_94_.length() == 0) {
                stringbuffer_90_.append("Error in passive port response text - \"" + string_95_ + "\"");
                ls[0] = (long) kMonitorSpecificError;
            }
        } else {
            stringbuffer_90_.append("Could not open passive port - " + FTPStatusToString(string_95_.substring(0, 3)));
            ls[0] = (long) kMonitorSpecificError;
        }
        if (ls[0] == (long) kURLok) {
            if (printwriter_93_ != null)
                printwriter_93_.println("\nConnecting to server " + string_94_ + " port " + i + "\n");
            Socket socket_100_ = null;
            InputStream inputstream = null;
            try {
                socket_100_ = new Socket(string_94_, i);
                inputstream = socket_100_.getInputStream();
                writeLine(printwriter, "RETR " + string, printwriter_93_);
                string_95_ = readStatus(bufferedreader, socket, l_91_, printwriter_93_);
                if (string_95_.startsWith("1")) {
                    ls[2] = fillBuffer(socketsession, inputstream, socket_100_, l_91_, l_92_, stringbuffer, l);
                    if (inputstream != null) {
                        inputstream.close();
                        inputstream = null;
                    }
                    if (l_92_ > 0L && ls[2] >= l_92_)
                        string_95_ = ("226 Transfer partially completed, download limit reached after " + l_92_ + " bytes");
                    else
                        string_95_ = readStatus(bufferedreader, socket, l_91_, printwriter_93_);
                    if (!string_95_.startsWith("226")) {
                        ls[0] = (long) kMonitorSpecificError;
                        stringbuffer_90_.append("Data transfer not completed - " + FTPStatusToString(string_95_.substring(0, 3)));
                    }
                } else {
                    ls[0] = (long) kMonitorSpecificError;
                    stringbuffer_90_.append(FTPStatusToString(string_95_.substring(0, 3)));
                }
            } finally {
                try {
                    if (inputstream != null)
                        inputstream.close();
                    if (socket_100_ != null)
                        socket_100_.close();
                } catch (IOException ioexception) {
                    /* empty */
                }
            }
        }
        if (string_95_.length() >= 3)
            ls[3] = (long) TextUtils.toInt(string_95_.substring(0, 3));
        return ls;
    }

    private static long[] retrieveFileViaProxy(String string, String string_101_, String string_102_, String string_103_, String string_104_, String string_105_, String string_106_, StringBuffer stringbuffer, long l, StringBuffer stringbuffer_107_,
            int i, PrintWriter printwriter, Monitor monitor) {
        string_104_ = string_104_.replace('\\', '/');
        if (!string_104_.startsWith("/"))
            string_104_ = "/" + string_104_;
        String string_108_ = "ftp://" + string + string_104_;
        SocketSession socketsession = SocketSession.getSession(monitor);
        String string_109_ = "";
        Array array = null;
        int i_110_ = 0;
        StringBuffer stringbuffer_111_ = null;
        String string_112_ = "";
        long[] ls = URLMonitor.checkURL(socketsession, string_108_, "", "", string_101_, string_102_, string_103_, array, string_105_, string_106_, string_109_, stringbuffer, l, string_112_, i_110_, i, stringbuffer_111_);
        socketsession.close();
        String string_113_ = stringbuffer.toString();
        String string_114_ = URLMonitor.CRLF + URLMonitor.CRLF;
        int i_115_ = string_113_.indexOf(string_114_);
        if (i_115_ >= 0) {
            stringbuffer.setLength(0);
            stringbuffer.append(string_113_.substring(i_115_ + string_114_.length()));
        }
        if (ls[2] == 0L && stringbuffer.length() > 0)
            ls[2] = (long) stringbuffer.length();
        return ls;
    }

    private static long fillBuffer(SocketSession socketsession, InputStream inputstream, Socket socket, long l, long l_116_, StringBuffer stringbuffer, long l_117_) throws IOException {
        if (stringbuffer != null)
            stringbuffer.setLength(0);
        boolean bool = false;
        long l_118_ = 0L;
        byte[] is = socketsession.getByteBuffer();
        char[] cs = new char[16384];
        do {
            setTimedOut(socket, l);
            int i = inputstream.read(is);
            for (int i_119_ = 0; i_119_ < i; i_119_ ++)
                cs[i_119_] = (char) (is[i_119_] & 0xff);
            if (debug)
                System.out.println(Platform.timeMillis() + ": read: " + i + ", timeout: " + (l - Platform.timeMillis()));
            if (i == -1)
                break;
            if (stringbuffer != null && l_118_ < l_117_) {
                int i_120_ = i;
                int i_121_ = (int) l_117_ - stringbuffer.length();
                if (i_121_ < i_120_)
                    i_120_ = i_121_;
                stringbuffer.append(new String(cs, 0, i_120_));
            }
            l_118_ += (long) i;
        } while (l_116_ <= 0L || l_118_ < l_116_);
        return l_118_;
    }

    private static String sendPort(int i, BufferedReader bufferedreader, PrintWriter printwriter, Socket socket, long l, PrintWriter printwriter_122_) throws IOException {
        InetAddress inetaddress;
        try {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            String string = siteviewgroup.getSetting("_ftpServerAddress");
            if (string.length() > 0)
                inetaddress = InetAddress.getByName(string);
            else
                inetaddress = socket.getLocalAddress();
        } catch (UnknownHostException unknownhostexception) {
            System.out.println("can't get local host");
            return "";
        }
        byte[] is = inetaddress.getAddress();
        short[] is_123_ = new short[4];
        for (int i_124_ = 0; i_124_ <= 3; i_124_ ++) {
            is_123_[i_124_] = (short) is[i_124_];
            if (is_123_[i_124_] < 0)
                is_123_[i_124_] += 256;
        }
        LogManager.log("RunMonitor", ("ftp accept, waiting for connection on " + is_123_[0] + "." + is_123_[1] + "." + is_123_[2] + "." + is_123_[3] + ":" + i));
        writeLine(printwriter, ("PORT " + is_123_[0] + "," + is_123_[1] + "," + is_123_[2] + "," + is_123_[3] + "," + ((i & 0xff00) >> 8) + "," + (i & 0xff)), printwriter_122_);
        return readStatus(bufferedreader, socket, l, printwriter_122_);
    }

    private static String readStatus(BufferedReader bufferedreader, Socket socket, long l, PrintWriter printwriter) throws IOException {
        String string = readLine(bufferedreader, socket, l, printwriter);
        for (;;) {
            if (string == null) {
                string = "";
                break;
            }
            if (TextUtils.isStatusLine(string))
                break;
            string = readLine(bufferedreader, socket, l, printwriter);
        }
        return string;
    }

    private static void setTimedOut(ServerSocket serversocket, long l) throws InterruptedIOException {
        int i = (int) (l - Platform.timeMillis());
        if (i <= 0)
            throw new InterruptedIOException();
        try {
            serversocket.setSoTimeout(i);
        } catch (SocketException socketexception) {
            /* empty */
        }
    }

    private static void setTimedOut(Socket socket, long l) throws InterruptedIOException {
        int i = (int) (l - Platform.timeMillis());
        if (i <= 0)
            throw new InterruptedIOException();
        Platform.setSocketTimeout(socket, i);
    }

    private static String readLine(BufferedReader bufferedreader, Socket socket, long l, PrintWriter printwriter) throws IOException {
        setTimedOut(socket, l);
        String string = bufferedreader.readLine();
        if (debug)
            System.out.println("Debug: FTP IN  =" + string);
        if (printwriter != null)
            printwriter.println("Received: " + string);
        if (string != null)
            string = string.trim();
        return string;
    }

    private static void writeLine(PrintWriter printwriter, String string, PrintWriter printwriter_125_) {
        if (debug)
            System.out.println("Debug: FTP OUT =" + string);
        if (printwriter_125_ != null) {
            String string_126_ = "PASS ";
            String string_127_ = string;
            if (string_127_.startsWith(string_126_)) {
                int i = string_127_.length() - string_126_.length();
                if (i < 0)
                    i = 0;
                string_127_ = string_126_ + TextUtils.filledString('*', i);
            }
            printwriter_125_.println("Sent:     " + string_127_);
        }
        printwriter.print(string + URLMonitor.CRLF);
        printwriter.flush();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pSize);
        return array;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pCheckContent) {
            Vector vector = new Vector();
            vector.addElement("");
            vector.addElement("no content checking");
            vector.addElement("on");
            vector.addElement("compare to last contents ");
            vector.addElement("baseline");
            vector.addElement("compare to saved contents");
            vector.addElement("reset");
            vector.addElement("reset saved contents");
            return vector;
        }
        return super.getScalarValues(scalarproperty, httprequest, cgi);
    }

    public String verify(StringProperty stringproperty, String string, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pServer) {
            if (string.length() == 0)
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            else if (TextUtils.hasSpaces(string))
                hashmap.put(stringproperty, "no spaces are allowed");
            return string;
        }
        if (stringproperty == pTimeout) {
            if (TextUtils.digits(string) != string.length())
                hashmap.put(stringproperty, "time out must be a number");
            else if (TextUtils.toInt(string) < 1)
                hashmap.put(stringproperty, "time out must be greater than 0");
            return string;
        }
        if (stringproperty == pUserName || stringproperty == pPassword || stringproperty == pFilename) {
            if (string.length() == 0)
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            return string;
        }
        if (stringproperty == pContentMatch) {
            String string_128_ = TextUtils.legalMatchString(string);
            if (string_128_.length() > 0)
                hashmap.put(stringproperty, string_128_);
            return string;
        }
        if (stringproperty == pCheckContent) {
            if (string.equals("reset") || (string.equals("baseline") && getProperty(pCheckContentResetTime).length() == 0)) {
                setProperty(pCheckContentResetTime, Platform.timeMillis());
                string = "baseline";
            } else if (string.length() > 0 && !string.equals("on") && !string.equals("baseline"))
                string = "on";
            return string;
        }
        return super.verify(stringproperty, string, httprequest, hashmap);
    }

    public static void main(String[] strings) throws IOException {
        boolean bool = false;
        boolean bool_129_ = false;
        String string = "";
        String string_130_ = "";
        String string_131_ = "";
        String string_132_ = "";
        boolean bool_133_ = false;
        String string_134_ = "";
        String string_135_ = "";
        String string_136_ = "";
        String string_137_ = "";
        if (strings.length == 0) {
            System.out.println("Usage: FTPMonitor  [-t] [-i] -f -s server [-u username] [-p password] [-x proxy] [-xu proxyUsername] [-xp proxyPassword] [-m matchstring] [filename]");
            System.out.println("where:\n-f   means to use passive FTP mode\n-t   means to run tests\n-i   means to run internal tests (used with -t)");
            System.exit(0);
        }
        int i = 0;
        while (i < strings.length) {
            if (strings[i].equals("-s")) {
                string = strings[++ i];
                i ++;
            } else if (strings[i].equals("-u")) {
                string_130_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-p")) {
                string_131_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-m")) {
                string_134_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-x")) {
                string_135_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-xu")) {
                string_136_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-xp")) {
                string_137_ = strings[++ i];
                i ++;
            } else if (strings[i].equals("-t")) {
                bool = true;
                i ++;
            } else if (strings[i].equals("-i")) {
                bool_129_ = true;
                i ++;
            } else if (strings[i].equals("-f")) {
                bool_133_ = true;
                i ++;
            } else
                string_132_ = strings[i ++];
        }
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer_138_ = new StringBuffer();
        long l = 51200L;
        long[] ls = checkFTP(string, string_135_, string_136_, string_137_, bool_133_, string_132_, string_130_, string_131_, string_134_, stringbuffer_138_, l, stringbuffer, URLMonitor.DEFAULT_TIMEOUT, -1L, FileUtils.MakeOutputWriter(System.out), null);
        System.out.println("STATUS=" + lookupStatus(ls[0]));
        System.out.println("TIME=" + ls[1]);
        System.out.println("SIZE=" + ls[2]);
        if (ls[0] == (long) kMonitorSpecificError)
            System.out.println("ERROR=" + (Object) stringbuffer);
        System.out.println("CONTENT\n-------------\n" + (Object) stringbuffer_138_ + "\n-------------");
    }

    static {
        pServer = new StringProperty("_server", "", "FTP server");
        pServer.setDisplayText("FTP Server", ("the IP address or host name of the FTP server (examples: 206.168.191.10, or ftp." + Platform.exampleDomain + " <br> or to specify a different port, ftp." + Platform.exampleDomain + ":89) "));
        pServer.setParameterOptions(true, 1, false);
        pFilename = new FileProperty("_filename", "");
        pFilename.fieldLabels("File", "the file to retrieve from the FTP server (example: /pub/file.txt)");
        pFilename.setParameterOptions(true, 2, false);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("User Name", "user name for the FTP server (i.e. Anonymous)");
        pUserName.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "password for the FTP server (i.e. user@server.com)");
        pPassword.setParameterOptions(true, 4, false);
        pPassword.isPassword = true;
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for the file to be retrieved");
        pTimeout.setParameterOptions(true, 5, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("FTP Proxy", ("optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)"));
        pProxy.setParameterOptions(true, 6, true);
        pPassive = new BooleanProperty("_passive", "");
        pPassive.setDisplayText("Passive Mode", "use FTP's passive mode (if not using a proxy) - passive mode usually allows FTP to work through firewalls.");
        pPassive.setParameterOptions(true, 7, true);
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional text to match against content of the file");
        pContentMatch.setParameterOptions(true, 8, true);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("match value");
        pMatchValue.setIsThreshold(true);
        pCheckContent = new ScalarProperty("_checkContent", "");
        pCheckContent.setDisplayText("Check for Content Changes", "generate error if the content of the file changes - resetting the saved contents updates the contents checked against during the next monitor run");
        pCheckContent.setParameterOptions(true, 9, true);
        pCheckContentResetTime = new StringProperty("_checkContentResetTime", "");
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 10, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 11, true);
        pProxyPassword.isPassword = true;
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pSize = new NumericProperty("size", "0", "bytes");
        pSize.setLabel("size");
        pSize.setIsThreshold(true);
        pLastChecksum = new StringProperty("lastChecksum");
        pLastCheckContentTime = new StringProperty("lastCheckContentTime", "");
        pLastCheckContentSize = new StringProperty("lastCheckContentSize", "");
        pLastBufferSize = new StringProperty("lastBufferSize");
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        StringProperty[] stringpropertys = { pServer, pFilename, pTimeout, pUserName, pPassword, pProxy, pPassive, pContentMatch, pCheckContent, pProxyUserName, pProxyPassword, pStatus, pRoundTripTime, pSize, pLastChecksum, pLastBufferSize,
                pLastCheckContentTime, pCheckContentResetTime, pLastCheckContentSize, pMatchValue };
        addProperties("COM.dragonflow.StandardMonitor.FTPMonitor", stringpropertys);
        addClassElement("COM.dragonflow.StandardMonitor.FTPMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.FTPMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.FTPMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "description", "Tests a FTP server by connecting to it and and verifying that a file can be retrieved.");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "help", "FTPMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "title", "FTP");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "class", "FTPMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "toolName", "Check FTP Server");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "toolDescription", "Check whether an FTP file can be retrieved.");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "topazName", "FTP Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.FTPMonitor", "topazType", "Web Application Server");
        if (System.getProperty("FTPMonitor.debug") != null)
            debug = true;
        ftpLocks = new HashMap();
        ftpGroupLocks = new HashMap();
        FTPCounter = 0;
    }
}
