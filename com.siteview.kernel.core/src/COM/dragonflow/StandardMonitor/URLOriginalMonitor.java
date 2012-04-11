/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLOriginalMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLOriginalMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

import jgl.Array;
import jgl.HashMap;

import org.apache.commons.httpclient.Header;

import COM.dragonflow.ApacheHttpClientUtils.ApacheHttpMethod;
import COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils;
import COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PercentProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.PlatformNew;
import COM.dragonflow.SiteView.Portal;
import COM.dragonflow.SiteView.PortalSiteView;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Base64Encoder;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTMLTagParser;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.SocketStream;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.URLInfo;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// URLLoader, URLScannerInputStream, URLSequenceMonitor

public class URLOriginalMonitor extends AtomicMonitor {

    public static final int DEFAULT_MILLISECOND_PRECISION = 2;

    static StringProperty pURL;

    static StringProperty pURLEncoding;

    static StringProperty pContentMatch;

    static StringProperty pTimeout;

    static StringProperty pErrorContent;

    static StringProperty pImages;

    static StringProperty pFrames;

    static StringProperty pProxy;

    static StringProperty pPostData;

    static StringProperty pChallengeResponse;

    static StringProperty pUserName;

    static StringProperty pPassword;

    static StringProperty pProxyUserName;

    static StringProperty pProxyPassword;

    static StringProperty pCheckContent;

    static StringProperty pErrorOnRedirect;

    static StringProperty pMeasureDetails;

    static StringProperty pCheckContentResetTime;

    static StringProperty pDaysUntilCertExpiration;

    static StringProperty pRoundTripTime;

    static StringProperty pDNSTime;

    static StringProperty pResponseTime;

    static StringProperty pDownloadTime;

    static StringProperty pConnectTime;

    public static StringProperty pMonitorRunCount;

    public static StringProperty pRollingBaseValuesRTT;

    public static StringProperty pRollingBaseValuesDNL;

    public static StringProperty pRollingBaseValuesCNT;

    public static StringProperty pRollingBaseValuesRSP;

    public static StringProperty pRollingBaseValuesDNS;

    public static StringProperty pPercentDeviationRTT;

    public static StringProperty pPercentDeviationDNL;

    public static StringProperty pPercentDeviationCNT;

    public static StringProperty pPercentDeviationRSP;

    public static StringProperty pPercentDeviationDNS;

    static StringProperty pSize;

    static StringProperty pStatus;

    static StringProperty pStatusText;

    static StringProperty pLastChecksum;

    static StringProperty pURLHeader;

    static StringProperty pMatchValue;

    static StringProperty pAge;

    static StringProperty pOverallStatus;

    static StringProperty pTotalErrors;

    static StringProperty pLastCheckContentTime;

    static StringProperty pFrameErrorList;

    static StringProperty pImageErrorList;

    static StringProperty pHost;

    static int millisecondPrecision;

    public static String testURL;

    public static StringProperty testProperty;

    public static boolean hasSecureURLs = false;

    public static int kDebugData = 128;

    public static int kDebugSocket = 64;

    public static int kDebugIO = 32;

    public static int kDebugRequest = 16;

    public static int kDebugReply = 8;

    public static int kDebugTransaction = 4;

    public static int kDebugCookie = 2;

    public static int kDebugMin = 1;

    public static int kDebugNone = 0;

    public static int debugURL;

    public static int kURLBufferSize = 4096;

    public static int kHeaderBufferSize = 4096;

    public static long internalURLs = 0L;

    public static long internalURLBytes = 0L;

    public static long internalURLDuration = 0L;

    public static long internalURLErrors = 0L;

    public static long internalSecureURLs = 0L;

    public static long internalSecureBytes = 0L;

    public static long internalSecureDuration = 0L;

    public static long internalSecureErrors = 0L;

    public static long internalJavaURLs = 0L;

    public static long internalJavaBytes = 0L;

    public static long internalJavaDuration = 0L;

    public static long internalJavaErrors = 0L;

    public static long internalSecureJavaURLs = 0L;

    public static long internalSecureJavaBytes = 0L;

    public static long internalSecureJavaDuration = 0L;

    public static long internalSecureJavaErrors = 0L;

    public static long internalRemoteURLs = 0L;

    public static long internalRemoteBytes = 0L;

    public static long internalRemoteDuration = 0L;

    public static long internalRemoteErrors = 0L;

    public static CounterLock sslLock;

    public static StringBuffer concatBuffer = null;

    public static String CRLF = "\r\n";

    public static int DEFAULT_TIMEOUT = 60000;

    static String TARGET_TAGS[] = { "INPUT", "IMG", "BASE", "BODY" };

    public static int DEFAULT_MAX_REDIRECTS = 10;

    public static String NT_CHALLENGE_RESPONSE_TAG = "[NT]";

    public static int SSLCounter = 0;

    static HashMap locationLocks = new HashMap();

    static HashMap sslGroupLocks = new HashMap();

    static HashMap ssl2Cache = null;

    static long kReadChunked = -2L;

    static long kReadUntilEnd = -1L;

    static long kTimedOutValue = 10L;

    public static String CUSTOM_CONTENT = "Custom-Content: ";

    public static int CUSTOM_CONTENT_TYPE = 0;

    public static String CUSTOM_HEADER = "Custom-Header: ";

    public static int CUSTOM_HEADER_TYPE = 1;

    public static String CONTENT_TYPE_HEADER = "Content-Type: ";

    public static int CONTENT_TYPE_HEADER_TYPE = 2;

    public static String HOST_HEADER = "Host: ";

    public static int HOST_HEADER_TYPE = 3;

    public static String USER_AGENT_HEADER = "User-Agent: ";

    public static int USER_AGENT_HEADER_TYPE = 4;

    public static String SET_COOKIE_HEADER = "Set-Cookie:";

    public static int SET_COOKIE_HEADER_TYPE = 5;

    public static String METHOD_HEADER = "Method: ";

    public static int METHOD_HEADER_TYPE = 6;

    public static String REQUEST_PROTOCOL_HEADER = "Protocol: ";

    public static int REQUEST_PROTOCOL_HEADER_TYPE = 7;

    public static String ACTION_HEADER = "Action: ";

    public static int ACTION_HEADER_TYPE = 8;

    public static String SSLGET_HEADER = "sslgetOptions: ";

    public static int SSLGET_HEADER_TYPE = 9;

    public static String CONTENT_TYPE_DEFAULT = "application/x-www-form-urlencoded";

    public static String REQUEST_PROTOCOL_DEFAULT = "HTTP/";

    public URLOriginalMonitor() {
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     */
    public String getHostname() {
        String s;
        s = HTTPUtils.hostFromURL(getProperty(pURL));
        if (getSetting("_urlLookupHost").length() > 0) {
            try {
                InetAddress inetaddress = InetAddress.getByName(s);
                String s1 = inetaddress.getHostAddress();
                InetAddress inetaddress1 = InetAddress.getByName(s1);
                return inetaddress1.getHostName();
            } catch (Exception e) {
                LogManager.log("Error", "Failed to perform reverse lookup " + e);
                return s;
            }
        }
        return s;
    }

    public static String getChallengeResponse(URLOriginalMonitor urloriginalmonitor) {
        return urloriginalmonitor.getProperty(pChallengeResponse);
    }

    public String getTestURL() {
        String s = getProperty(pURL);
        if (TextUtils.isSubstituteExpression(s)) {
            s = TextUtils.substitute(s);
        }
        String s1 = HTTPRequest.encodeString(s);
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=get&host=" + s1;
        String s3 = getProperty(pUserName);
        String s4 = getProperty(pChallengeResponse);
        String s5 = I18N.toDefaultEncoding(getProperty(pID));
        String s6 = HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID)));
        if (s6.length() > 0) {
            s2 = s2 + "&group=" + s6;
        }
        if (s5.length() > 0) {
            s2 = s2 + "&id=" + s5;
        }
        if (s4.length() > 0) {
            s3 = NT_CHALLENGE_RESPONSE_TAG + s3;
        }
        if (s3.length() > 0) {
            s2 = s2 + "&user=" + HTTPRequest.encodeString(s3);
        }
        if (getPropertyAsBoolean(pFrames)) {
            s2 = s2 + "&getFrames=on";
        }
        if (getPropertyAsBoolean(pImages)) {
            s2 = s2 + "&getImages=on";
        }
        String s7 = HTTPRequest.encodeString(TextUtils.obscure(getProperty(pPassword)));
        if (s7.length() > 0) {
            s2 = s2 + "&password=" + s7;
        }
        String s8 = HTTPRequest.encodeString(getProperty(pProxy));
        if (s8.length() > 0) {
            s2 = s2 + "&proxy=" + s8;
        }
        String s9 = HTTPRequest.encodeString(getProperty(pProxyUserName));
        if (s9.length() > 0) {
            s2 = s2 + "&proxyUser=" + s9;
        }
        String s10 = HTTPRequest.encodeString(TextUtils.obscure(getProperty(pProxyPassword)));
        if (s10.length() > 0) {
            s2 = s2 + "&proxyPassword=" + s10;
        }
        Enumeration enumeration = getMultipleValues(pPostData);
        if (enumeration.hasMoreElements()) {
            while (enumeration.hasMoreElements()) {
                String s11 = (String) enumeration.nextElement();
                s2 = s2 + "&postData=" + HTTPRequest.encodeString(s11);
            }
        }
        return s2;
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        String s = (String) hashmap.get("_url");
        if (s != null && s.startsWith("https")) {
            hasSecureURLs = true;
        }
    }

    public boolean testUpdate() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        SocketStream.initialize(hashmap);
        return super.testUpdate();
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        if (!Platform.getVersion().startsWith("7")) {
            return true;
        } else {
            update1("", "");
            return true;
        }
    }

    long[] update1(String s, String s1) {
        long al[] = null;
        String s2 = getProperty(pProxy);
        String s3 = getProperty(pProxyPassword);
        String s4 = getProperty(pProxyUserName);
        Array array = Platform.split(',', s2);
        if (array.size() <= 1) {
            al = update1(s, s1, s2, s3, s4);
        } else {
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s5 = (String) enumeration.nextElement();
                s5 = s5.trim();
                al = update1(s, s1, s5, s3, s4);
                String s6 = getProperty(getLocationProperty(pStateString, s1));
                setProperty(getLocationProperty(pStateString, s1), s6 + ", using proxy " + s5);
                if (!shouldTryNextProxy(al[0])) {
                    break;
                }
            }
        }
        return al;
    }

    public static boolean shouldTryNextProxy(long l) {
        return l == (long) kURLTimeoutError || l == (long) kURLNoConnectionError || l == (long) kURLNoRouteToHostError || l == (long) kURLBadHostNameError;
    }

    long[] update1(String s, String s1, String s2, String s3, String s4) {
        String s5 = getProperty(pURL);
        if (TextUtils.isSubstituteExpression(s5)) {
            s5 = TextUtils.substitute(s5);
        }
        String s6 = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
        String s7 = I18N.UnicodeToString(getProperty(pErrorContent), I18N.nullEncoding());
        String s8 = getProperty(pUserName);
        if (getProperty(pChallengeResponse).length() > 0) {
            s8 = NT_CHALLENGE_RESPONSE_TAG + s8;
        }
        String s9 = getProperty(pPassword);
        Array array = TextUtils.enumToArray(getMultipleValues(pPostData));
        int i = getPropertyAsInteger(pTimeout) * 1000;
        if (i == 0) {
            i = DEFAULT_TIMEOUT;
        }
        StringBuffer stringbuffer = new StringBuffer(kURLBufferSize);
        long l = getSettingAsLong("_urlContentMatchMax", 50000);
        String s10 = getSetting("_urlOtherHeader");
        progressString += "Retrieving URL " + getProperty(pURL) + "\n";
        int j = 0;
        if (getPropertyAsBoolean(pErrorOnRedirect)) {
            long l1 = getSettingAsLong("_urlRedirectMax", DEFAULT_MAX_REDIRECTS);
            j = (int) l1 + 1;
        }
        StringBuffer stringbuffer1 = new StringBuffer(s5);
        SocketSession socketsession = SocketSession.getSession(this);
        long al[] = checkURL(socketsession, s5, s6, s7, s2, s4, s3, array, s8, s9, s, stringbuffer, l, s10, j, i, stringbuffer1);
        long l2 = al[0];
        long l3 = al[1];
        long l4 = al[2];
        long l5 = al[3];
        long l6 = al[4];
        long l7 = al[5];
        long l8 = al[6];
        long l9 = al[7];
        long l10 = al[8];
        int k = 0;
        if (socketsession.context.getSetting("_keepTryingForGoodStatus").length() > 0) {
            k = TextUtils.toInt(socketsession.context.getSetting("_keepTryingForGoodStatus"));
        }
        String s11 = stringbuffer1.toString();
        String s12 = "";
        if (l2 == 200L && getProperty(pCheckContent).length() > 0) {
            String s13 = getProperty(pLastChecksum);
            String s14 = getHTTPContent(stringbuffer.toString());
            String s15 = String.valueOf(PlatformNew.crc(s14));
            if (s13.length() > 0 && !s15.equals(s13)) {
                l2 = kURLContentChangedError;
            }
            s12 = s13;
            boolean flag = true;
            if (getProperty(pCheckContent).equals("baseline") && getPropertyAsLong(pCheckContentResetTime) < getPropertyAsLong(pLastCheckContentTime) && s13.length() > 0) {
                flag = false;
            }
            if (flag && getProperty(pCheckContent).equals("baseline")) {
                l2 = 200L;
            }
            if (flag) {
                s12 = s15;
                setProperty(pLastCheckContentTime, Platform.timeMillis());
            }
        }
        long l11 = getSettingAsLong("_urlHeaderLinesToSave", 15);
        String s16 = "";
        if (l2 != 200L) {
            s16 = parseHeader(stringbuffer.toString(), l11);
        }
        long l12 = l2;
        long l13 = 0L;
        long l14 = 0L;
        String s17 = "";
        long l15 = 0L;
        long l16 = 0L;
        String s18 = "";
        i -= (int) l3;
        boolean flag1 = l2 == 200L && (getPropertyAsBoolean(pFrames) || getPropertyAsBoolean(pImages));
        if (l2 == (long) kURLContentMatchError && getPropertyAsBoolean(pFrames)) {
            flag1 = true;
        }
        String s19 = "";
        String s20 = "";
        if (flag1) {
            socketsession.updateCookies(stringbuffer.toString(), s11);
            StringBuffer stringbuffer2 = new StringBuffer();
            StringBuffer stringbuffer3 = new StringBuffer();
            HashMap hashmap = new HashMap();
            int i1 = getSettingAsLong("_urlLoadThreads", 1);
            String s22 = s10;
            if (s22.length() > 0 && !s22.endsWith(CRLF)) {
                s22 = s22 + CRLF;
            }
            if (array != null) {
                Enumeration enumeration = array.elements();
                do {
                    if (!enumeration.hasMoreElements()) {
                        break;
                    }
                    String s25 = (String) enumeration.nextElement();
                    if (TextUtils.startsWithIgnoreCase(s25, CUSTOM_HEADER)) {
                        s22 = s22 + s25.substring(CUSTOM_HEADER.length()) + CRLF;
                    }
                } while (true);
            }
            URLLoader urlloader = new URLLoader(this, socketsession, hashmap, i1, getPropertyAsBoolean(pFrames), getPropertyAsBoolean(pImages), s11, s8, s9, s22, s, l, stringbuffer, i, s2, s4, s3, null);
            urlloader.waitForCompletion();
            long al1[] = new long[100];
            al1 = urlloader.getResults(s6, s7, stringbuffer2, stringbuffer3);
            if (l2 == (long) kURLContentMatchError) {
                if (urlloader.foundContentMatch()) {
                    l2 = kURLok;
                    s19 = urlloader.getContentMatchContents();
                }
            } else if (al1[0] == (long) kURLContentErrorFound) {
                l2 = al1[0];
                s20 = urlloader.getErrorMatchContents();
            }
            hashmap = null;
            urlloader.clear();
            urlloader = null;
            l12 = al1[0];
            l3 += al1[1];
            l4 += al1[2];
            l7 += al1[5];
            l8 += al1[6];
            l9 += al1[7];
            l10 += al1[8];
            l13 = al1[10];
            l14 = al1[11];
            s17 = stringbuffer2.toString();
            l15 = al1[13];
            l16 = al1[14];
            s18 = stringbuffer3.toString();
        }
        long l17 = l16 + l14;
        String s21 = "";
        unsetProperty(getLocationProperty(pMatchValue, s1));
        if (TextUtils.isValueExpression(s6)) {
            if (s19.length() == 0) {
                s19 = stringbuffer.toString();
            }
            s21 = updateMatchValues(s1, s6, s19, "Content Matched: ", false);
        }
        if (TextUtils.isValueExpression(s7)) {
            if (s20.length() == 0) {
                s20 = stringbuffer.toString();
            }
            if (s21.length() > 0) {
                s21 = s21 + updateMatchValues(s1, s7, s20, ", Error Matched: ", false);
            } else {
                s21 = s21 + updateMatchValues(s1, s7, s20, "Error Matched: ", false);
            }
        }
        socketsession.close();
        if (stillActive()) {
            synchronized (this) {
                setProperty(getLocationProperty(pStatus, s1), String.valueOf(l2));
                if (k > 0) {
                    l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s5, 0, k);
                }
                setProperty(getLocationProperty(pStatusText, s1), lookupStatus(l2));
                setProperty(getLocationProperty(pOverallStatus, s1), String.valueOf(l12));
                setProperty(getLocationProperty(pTotalErrors, s1), String.valueOf(l17));
                setProperty(getLocationProperty(pLastChecksum, s1), s12);
                setProperty(getLocationProperty(pURLHeader, s1), s16);
                setProperty(getLocationProperty(pHost, s1), getHostname());
                if (l2 == 200L) {
                    int j1 = TextUtils.toInt(getProperty(pMonitorRunCount));
                    String s24 = TextUtils.floatToString((float) l3 / 1000F, millisecondPrecision) + " sec";
                    if (j1 > 0) {
                        setRollingBaseProperties(l3, pRollingBaseValuesRTT, pPercentDeviationRTT, j1, s1);
                        setRollingBaseProperties(l7, pRollingBaseValuesDNS, pPercentDeviationDNS, j1, s1);
                        setRollingBaseProperties(l8, pRollingBaseValuesCNT, pPercentDeviationCNT, j1, s1);
                        setRollingBaseProperties(l9, pRollingBaseValuesRSP, pPercentDeviationRSP, j1, s1);
                        setRollingBaseProperties(l10, pRollingBaseValuesDNL, pPercentDeviationDNL, j1, s1);
                    }
                    if (getPropertyAsBoolean(pMeasureDetails)) {
                        s24 = "Total " + s24 + " (DNS " + TextUtils.floatToString((float) l7 / 1000F, millisecondPrecision) + " sec" + ", connect " + TextUtils.floatToString((float) l8 / 1000F, millisecondPrecision) + " sec" + ", response "
                                + TextUtils.floatToString((float) l9 / 1000F, millisecondPrecision) + " sec" + ", download " + TextUtils.floatToString((float) l10 / 1000F, millisecondPrecision) + " sec)";
                    }
                    String s26 = "";
                    String s27 = "";
                    String s28 = "";
                    if (getPropertyAsBoolean(pFrames)) {
                        int k1 = getSettingAsLong("_urlItemErrorDisplayMax", 2);
                        if (l14 > 0L) {
                            String as[] = TextUtils.split(s17, "\t");
                            String s30 = "";
                            long l19 = k1;
                            if (l14 < (long) k1) {
                                l19 = l14;
                            }
                            for (int j2 = 0; (long) j2 < l19; j2 ++) {
                                if (j2 != 0) {
                                    s30 = s30 + ", " + as[j2];
                                } else {
                                    s30 = s30 + as[j2];
                                }
                            }

                            if (l14 > (long) k1) {
                                s30 = s30 + ", ...";
                            }
                            s27 = ", " + l14 + " of " + l13 + " frames in error, " + s30;
                        } else if (l13 == 0L) {
                            s27 = ", no frames";
                        } else if (l13 == 1L) {
                            s27 = ", 1 frame";
                        } else {
                            s27 = ", " + l13 + " frames";
                        }
                    }
                    if (getPropertyAsBoolean(pImages)) {
                        int i2 = getSettingAsLong("_urlItemErrorDisplayMax", 2);
                        if (l16 > 0L) {
                            String as1[] = TextUtils.split(s18, "\t");
                            String s31 = "";
                            long l20 = i2;
                            if (l16 < (long) i2) {
                                l20 = l16;
                            }
                            for (int k2 = 0; (long) k2 < l20; k2 ++) {
                                if (k2 != 0) {
                                    s31 = s31 + ", " + as1[k2];
                                } else {
                                    s31 = s31 + as1[k2];
                                }
                            }

                            if (l16 > (long) i2) {
                                s31 = s31 + ", ...";
                            }
                            s26 = ", " + l16 + " of " + l15 + " images in error, " + s31;
                        } else if (l15 == 0L) {
                            s26 = ", no images";
                        } else if (l15 == 1L) {
                            s26 = ", 1 image";
                        } else {
                            s26 = ", " + l15 + " images";
                        }
                    }
                    setProperty(getLocationProperty(pFrameErrorList, s1), s17);
                    setProperty(getLocationProperty(pImageErrorList, s1), s18);
                    if (al[9] >= 0L) {
                        setProperty(getLocationProperty(pDaysUntilCertExpiration, s1), al[9]);
                    } else {
                        setProperty(getLocationProperty(pDaysUntilCertExpiration, s1), "n/a");
                    }
                    if (getPropertyAsBoolean(pFrames) || getPropertyAsBoolean(pImages)) {
                        s28 = " (" + TextUtils.bytesToString(l4) + " total)";
                    }
                    String s29 = "";
                    if (s21.length() > 0) {
                        s29 = ", ";
                    }
                    setProperty(getLocationProperty(pStateString, s1), I18N.StringToUnicode(s24 + s29 + s21 + s27 + s26 + s28, I18N.nullEncoding()));
                    if (l6 == 0L) {
                        Date date = new Date();
                        l6 = date.getTime() / 1000L;
                    }
                    long l18 = l6 - l5;
                    setProperty(getLocationProperty(pRoundTripTime, s1), String.valueOf(l3));
                    setProperty(getLocationProperty(pDNSTime, s1), String.valueOf(l7));
                    setProperty(getLocationProperty(pConnectTime, s1), String.valueOf(l8));
                    setProperty(getLocationProperty(pResponseTime, s1), String.valueOf(l9));
                    setProperty(getLocationProperty(pDownloadTime, s1), String.valueOf(l10));
                    setProperty(getLocationProperty(pMeasurement, s1), getMeasurement(pRoundTripTime, 4000L));
                    setProperty(getLocationProperty(pSize, s1), l4);
                    setProperty(getLocationProperty(pAge, s1), l18);
                } else {
                    String s23 = s21 + (s21.length() <= 0 ? "" : ", ");
                    if (k > 0) {
                        l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s5, 0, k);
                    }
                    s23 = s23 + lookupStatus(l2);
                    if (!s5.equals(s11)) {
                        s23 = s23 + ", " + s11;
                    }
                    setProperty(pNoData, "n/a");
                    setProperty(getLocationProperty(pStateString, s1), s23);
                    setProperty(getLocationProperty(pMeasurement, s1), String.valueOf(0));
                    setProperty(getLocationProperty(pRoundTripTime, s1), "n/a");
                    setProperty(getLocationProperty(pDNSTime, s1), "n/a");
                    setProperty(getLocationProperty(pConnectTime, s1), "n/a");
                    setProperty(getLocationProperty(pResponseTime, s1), "n/a");
                    setProperty(getLocationProperty(pDownloadTime, s1), "n/a");
                    setProperty(getLocationProperty(pSize, s1), "n/a");
                    setProperty(getLocationProperty(pAge, s1), "n/a");
                }
            }
        }
        al[0] = l2;
        al[1] = l3;
        al[2] = l4;
        al[3] = l5;
        al[4] = l6;
        return al;
    }

    void setRollingBaseProperties(long l, StringProperty stringproperty, StringProperty stringproperty1, int i, String s) {
        String s1 = getProperty(stringproperty);
        Array array = new Array();
        if (s1 != null && s1.length() > 0) {
            array = TextUtils.splitArray(s1, "\t");
        }
        float f = 0.0F;
        int j = 0;
        for (int k = 0; k < array.size(); k ++) {
            float f2 = TextUtils.toFloat((String) array.at(k));
            if (f2 != 0.0F) {
                f += f2;
                j ++;
            }
        }

        float f1 = 0.0F;
        if (j > 0) {
            f1 = f / (float) j;
        }
        if (array.size() >= i) {
            array.popFront();
        }
        array.add(TextUtils.floatToString((float) l / 1000F, millisecondPrecision));
        s1 = "";
        for (int i1 = 0; i1 < array.size(); i1 ++) {
            s1 = s1 + (String) array.at(i1);
            s1 = s1 + "\t";
        }

        unsetProperty(stringproperty);
        setProperty(stringproperty, s1);
        if (f1 == 0.0F) {
            f1 = (float) l / 1000F;
        }
        float f3 = (Math.abs(f1 - (float) l / 1000F) / f1) * 100F;
        if (array.size() < i) {
            return;
        } else {
            setProperty(getLocationProperty(stringproperty1, s), TextUtils.floatToString(f3, 3));
            return;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param l
     * @return
     */
    static String parseHeader(String s, long l) {
        int i = 0;
        int j = 0;
        StringBuffer stringbuffer = new StringBuffer(kHeaderBufferSize);
        while ((long) i < l) {
            int k = s.indexOf('\n', j + 1);
            String s2;
            if (k == -1) {
                s2 = s.substring(j);
            } else {
                int i1 = k - j;
                if (i1 > 300) {
                    i1 = 300;
                }
                s2 = s.substring(j, j + i1);
            }
            stringbuffer.append("  " + s2 + "\n");
            if (k == -1) {
                break;
            }
            i ++;
            j = k + 1;
        }

        String s1 = stringbuffer.toString();
        s1 = s1.replace('\r', ' ');
        s1 = s1.replace('\n', '^');
        return s1;
    }

    public String updateMatchValues(String s, String s1, String s2) {
        return updateMatchValues(s, s1, s2, "matched ", false);
    }

    public String updateMatchValues(String s, String s1, String s2, String s3, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        String s4 = I18N.UnicodeToString(s2, I18N.nullEncoding());
        int i = TextUtils.matchExpression(s4, s1, array, stringbuffer, s3);
        if (i != Monitor.kURLok && I18N.hasUnicode(s1)) {
            String s5 = getHTMLEncoding(s4);
            TextUtils.matchExpression(s4, I18N.UnicodeToString(s1, s5), array, stringbuffer, s3);
        }
        if (array.size() > 0) {
            setProperty(getLocationProperty(pMatchValue, s), array.at(0));
        }
        String s6 = stringbuffer.toString();
        if (flag) {
            int j = getSettingAsLong("_urlContentMatchDisplayMax", 150);
            if (s6.length() > j) {
                s6 = s6.substring(0, j) + "...";
            }
        }
        return s6;
    }

    public static String getHTMLEncoding(String s) {
        if (s.indexOf("charset=") < 0) {
            return "";
        }
        String s1 = s.substring(s.lastIndexOf("charset=") + "charset=".length());
        if (s1.indexOf(">") < 0) {
            return "";
        }
        s1 = s1.substring(0, s1.indexOf(">") - 1);
        if (s1.endsWith("\"")) {
            s1 = s1.substring(0, s1.length() - 1);
        }
        s1.trim();
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public Enumeration getStatePropertyObjects(boolean flag) {
        Enumeration enumeration = super.getStatePropertyObjects(flag);
        Array array = new Array();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty == pDNSTime) {
                if (getPropertyAsBoolean(pMeasureDetails)) {
                    array.add(stringproperty);
                }
            } else if (stringproperty == pConnectTime) {
                if (getPropertyAsBoolean(pMeasureDetails)) {
                    array.add(stringproperty);
                }
            } else if (stringproperty == pResponseTime) {
                if (getPropertyAsBoolean(pMeasureDetails)) {
                    array.add(stringproperty);
                }
            } else if (stringproperty == pDownloadTime) {
                if (getPropertyAsBoolean(pMeasureDetails)) {
                    array.add(stringproperty);
                }
            } else {
                array.add(stringproperty);
            }
        }
        return array.elements();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pStatusText);
        array.add(pSize);
        array.add(pAge);
        array.add(pMatchValue);
        array.add(pDNSTime);
        array.add(pResponseTime);
        array.add(pDownloadTime);
        array.add(pConnectTime);
        array.add(pOverallStatus);
        array.add(pTotalErrors);
        return array;
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (getProperty(pCategory).equals("good")) {
                return "";
            }
            String s = getProperty(pURL);
            String s2 = Platform.hostFromURL(s);
            if (s2 == null) {
                return "URL format error: " + s;
            }
            String s4 = "";
            int i = getPropertyAsInteger(pStatus);
            long l = getSettingAsLong("_urlHeaderLinesToSave", 15);
            if (l != 0L && isContentError(i)) {
                String s5 = getProperty(pURLHeader);
                s5 = s5.replace('^', '\n');
                s4 = "Response from server:\n" + s5 + "\n";
            }
            if (getPropertyAsLong(pTotalErrors) > 0L) {
                if (s4.length() > 0) {
                    s4 = s4 + "\n\n";
                }
                String as[] = TextUtils.split(getProperty(pFrameErrorList), "\t");
                if (as.length > 0) {
                    s4 = s4 + "Frame Errors:\n";
                    for (int j = 0; j < as.length; j ++) {
                        s4 = s4 + as[j] + "\n";
                    }

                    s4 = s4 + "\n";
                }
                as = TextUtils.split(getProperty(pImageErrorList), "\t");
                if (as.length > 0) {
                    s4 = s4 + "Image Errors\n";
                    for (int k = 0; k < as.length; k ++) {
                        s4 = s4 + as[k] + "\n";
                    }

                    s4 = s4 + "\n";
                }
            }
            s4 = s4 + diagnostic(s2, i);
            return s4;
        }
        if (stringproperty == pDiagnosticTraceRoute) {
            if (getProperty(pCategory).equals("good")) {
                return "";
            }
            String s1 = getProperty(pURL);
            String s3 = Platform.hostFromURL(s1);
            if (s3 == null) {
                return "URL format error: " + s1;
            } else {
                return diagnosticTraceRoute(s3);
            }
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public static long[] checkURL(String s) {
        return checkURL(s, "", "", "");
    }

    public static long[] checkURL(String s, String s1, String s2, String s3) {
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = checkURL(socketsession, s, "", "", s1, "", "", null, s2, s3, "", null, 50000L, "", 0, DEFAULT_TIMEOUT, null);
        socketsession.close();
        return al;
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, Array array, String s6, String s7, String s8, StringBuffer stringbuffer, long l, String s9, int i, int j,
            StringBuffer stringbuffer1) {
        return checkURL(socketsession, s, I18N.getDefaultEncoding(), s1, s2, s3, s4, s5, array, s6, s7, s8, stringbuffer, l, s9, i, j, stringbuffer1, null);
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, Array array, String s6, String s7, String s8, StringBuffer stringbuffer, long l, String s9, int i, int j,
            StringBuffer stringbuffer1, StringBuffer stringbuffer2) {
        return checkURL(socketsession, s, I18N.getDefaultEncoding(), s1, s2, s3, s4, s5, array, s6, s7, s8, stringbuffer, l, s9, i, j, stringbuffer1, stringbuffer2);
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, String s6, Array array, String s7, String s8, String s9, StringBuffer stringbuffer, long l, String s10, int i, int j,
            StringBuffer stringbuffer1, StringBuffer stringbuffer2) {
        boolean flag = false;
        if (socketsession == null) {
            socketsession = SocketSession.getSession(null);
            flag = true;
        }
        if (s == null) {
            throw new NullPointerException();
        }
        if (s2 == null) {
            s2 = "";
        }
        if (s3 == null) {
            s3 = "";
        }
        if (s4 == null) {
            s4 = "";
        }
        if (s5 == null) {
            s5 = "";
        }
        if (s6 == null) {
            s6 = "";
        }
        if (s7 == null) {
            s7 = "";
        }
        if (s8 == null) {
            s8 = "";
        }
        if (s9 == null) {
            s9 = "";
        }
        if (s10 == null) {
            s10 = "";
        }
        if (stringbuffer1 == null) {
            stringbuffer1 = new StringBuffer();
        }
        if (stringbuffer2 == null) {
            stringbuffer2 = new StringBuffer();
        }
        String s11 = "";
        long al[] = null;
        if (s9.length() == 0 && s.startsWith("https")) {
            s11 = socketsession.context.getSetting("_httpsLocation");
            if (s11.length() > 0) {
                s9 = s11;
            }
        }
        socketsession.originalUserName = s7;
        socketsession.originalPassword = s8;
        StringBuffer stringbuffer3 = new StringBuffer("_");
        if (stringbuffer2 == null) {
            stringbuffer2 = stringbuffer3;
        }
        al = check1URL(socketsession, s, s, s9, s2, s3, s4, s5, s6, array, s7, s8, s9, stringbuffer, l, s10, i, System.currentTimeMillis() + (long) j, stringbuffer1, stringbuffer2);
        if (s11.length() > 0) {
            LogManager.log("RunMonitor", "remote https, " + al[1] + ", " + stringbuffer1.toString());
            if (al[0] == (long) kURLRemoteMonitoringError) {
                LogManager.log("Error", "https remote failed, " + stringbuffer1.toString());
            }
        }
        if (al == null) {
            al = new long[9];
            al[0] = kURLUnknownError;
            for (int k = 1; k < al.length; k ++) {
                al[k] = 0L;
            }

        }
        if (flag) {
            socketsession.close();
        }
        return al;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @param s
     * @return
     */
    static String encodeParameters(Array array, String s) {
        String s1 = "";
        s = s.toLowerCase();
        if (array != null) {
            Enumeration enumeration = array.elements();
            byte byte0 = -1;
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                if (TextUtils.isSubstituteExpression(s2)) {
                    s2 = TextUtils.substitute(s2);
                }
                int i = getHeaderType(s2);
                if (i == CUSTOM_CONTENT_TYPE) {
                    s1 = s1 + s2.substring(CUSTOM_CONTENT.length()) + CRLF;
                } else if (i <= 0) {
                    if (s.indexOf("urlencoded") != -1) {
                        int j = s2.indexOf("=");
                        if (j != -1) {
                            String s3 = s2.substring(0, j);
                            String s4 = HTTPRequest.encodeString(TextUtils.replaceString(s3, HTMLTagParser.POST_EQUALS_TAG, "="));
                            String s5 = HTTPRequest.encodeString(s2.substring(j + 1, s2.length()));
                            if (s1.length() != 0) {
                                s1 = s1 + "&";
                            }
                            s1 = s1 + s4 + "=" + s5;
                        }
                    } else {
                        s1 = s1 + s2 + CRLF;
                    }
                }
            }
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @return
     */
    static String getRequestCommand(Array array) {
        String s = "GET";
        if (array != null) {
            Enumeration enumeration = array.elements();
            int i = -1;
            while (enumeration.hasMoreElements()) {
                String s1 = (String) enumeration.nextElement();
                i = getHeaderType(s1);
                if (i == METHOD_HEADER_TYPE) {
                    return s1.substring(METHOD_HEADER.length());
                }
                if (i <= 0) {
                    return "POST";
                }
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @return
     */
    static String getContentType(Array array) {
        String s = CONTENT_TYPE_DEFAULT;
        if (array != null) {
            Enumeration enumeration = array.elements();
            String s1;
            while (enumeration.hasMoreElements()) {
                s1 = (String) enumeration.nextElement();
                if (TextUtils.startsWithIgnoreCase(s1, CONTENT_TYPE_HEADER)) {
                    s = s1.substring(CONTENT_TYPE_HEADER.length());
                    break;
                }
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @return
     */
    static String getUserAgent(Array array) {
        String s = "";
        if (array != null) {
            Enumeration enumeration = array.elements();
            String s1;
            while (enumeration.hasMoreElements()) {
                s1 = (String) enumeration.nextElement();
                if (TextUtils.startsWithIgnoreCase(s1, USER_AGENT_HEADER)) {
                    s = s1.substring(USER_AGENT_HEADER.length());
                    break;
                }
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @return
     */
    static String getSslgetOptions(Array array) {
        String s;
        s = "";
        if (array != null) {
            Enumeration enumeration = array.elements();
            String s1;
            while (enumeration.hasMoreElements()) {
                s1 = (String) enumeration.nextElement();
                if (TextUtils.startsWithIgnoreCase(s1, SSLGET_HEADER)) {
                    s = s1.substring(SSLGET_HEADER.length());
                    break;
                }
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    static String getHostHeader(Array array, String s, int i, String s1) {
        String s2;

        s2 = "";
        if (array != null) {
            Enumeration enumeration = array.elements();
            String s3;
            while (enumeration.hasMoreElements()) {
                s3 = (String) enumeration.nextElement();
                if (TextUtils.startsWithIgnoreCase(s3, HOST_HEADER)) {
                    s2 = s3.substring(HOST_HEADER.length());
                    break;
                }
            }
        }

        if (s2.length() == 0) {
            s2 = s;
            if (s1.equals("https")) {
                if (i != 443) {
                    s2 = s2 + ":" + i;
                }
            } else if (i != 80) {
                s2 = s2 + ":" + i;
            }
        }
        return s2;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @return
     */
    static String getRequestProtocol(Array array) {
        String s = REQUEST_PROTOCOL_DEFAULT;
        if (array != null) {
            Enumeration enumeration = array.elements();
            String s1;
            while (enumeration.hasMoreElements()) {
                s1 = (String) enumeration.nextElement();
                if (TextUtils.startsWithIgnoreCase(s1, REQUEST_PROTOCOL_HEADER)) {
                    s = s1.substring(REQUEST_PROTOCOL_HEADER.length());
                    break;
                }
            }
        }
        return s;
    }

    static CounterLock getLocationLock(SiteViewObject siteviewobject, String s, String s1) {
        int i = TextUtils.toInt(siteviewobject.getSetting("_urlRemoteRequestMax"));
        if (i == 0) {
            return null;
        }
        CounterLock counterlock = (CounterLock) locationLocks.get(s);
        if (counterlock == null) {
            LogManager.log("RunMonitor", "location lock, " + i + ", key=" + s);
            counterlock = new CounterLock(i);
            counterlock.name = HTTPUtils.getLocationIDByURL(s);
            if (counterlock.name.length() == 0) {
                counterlock.name = s;
            }
            locationLocks.put(s, counterlock);
        }
        if (counterlock.current() == 0) {
            LogManager.log("RunMonitor", "location block, " + ((counterlock.max - counterlock.current()) + 1) + ", " + counterlock.name + ", " + s1);
        }
        String s2 = siteviewobject.currentStatus;
        siteviewobject.currentStatus = "URLMonitor ready, waiting for other Global monitors in group to complete";
        counterlock.get();
        siteviewobject.currentStatus = s2;
        return counterlock;
    }

    static void releaseLocationLock(CounterLock counterlock) {
        if (counterlock != null) {
            counterlock.release();
        }
    }

    static CounterLock getSSLGroupLock(SiteViewObject siteviewobject) {
        int i = TextUtils.toInt(siteviewobject.getSetting("_sslgetMaximumPerGroup"));
        if (i == 0) {
            return null;
        }
        SiteViewObject siteviewobject1 = siteviewobject.getOwner();
        if (siteviewobject1 == null) {
            return null;
        }
        String s = siteviewobject1.getProperty(pID);
        CounterLock counterlock = (CounterLock) sslGroupLocks.get(s);
        if (counterlock == null) {
            LogManager.log("RunMonitor", "ssl group lock, " + i + ", key=" + s);
            counterlock = new CounterLock(i);
            counterlock.name = s;
            sslGroupLocks.put(s, counterlock);
        }
        if (counterlock.current() == 0) {
            LogManager.log("RunMonitor", "group sll block, " + ((counterlock.max - counterlock.current()) + 1) + ", " + s);
        }
        String s1 = siteviewobject.currentStatus;
        siteviewobject.currentStatus = "URLMonitor ready, waiting for other SSL monitors in group to complete";
        counterlock.get();
        siteviewobject.currentStatus = s1;
        return counterlock;
    }

    static void releaseSSLGroupLock(CounterLock counterlock) {
        if (counterlock != null) {
            counterlock.release();
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    static long sendSSLRequest(String s, SocketSession socketsession, Array array, CounterLock counterlock, Array array1) {
        long l;
        String as[];
        l = -1000L;
        if (debugURL != 0) {
            System.out.println("StatusSSL : " + l);
        }
        as = new String[array.size()];
        for (int i = 0; i < as.length; i ++) {
            as[i] = (String) array.at(i);
        }

        if ((debugURL & kDebugRequest) != 0) {
            for (int j = 0; j < as.length; j ++) {
                LogManager.log("RunMonitor", "arg[" + j + "]=" + as[j]);
            }

        }

        try {
            String s1 = socketsession.context.currentStatus;
            socketsession.context.currentStatus = "URLMonitor ready, waiting for other SSL monitors to complete";
            counterlock.get();
            socketsession.context.currentStatus = s1;
            if (socketsession.context.getSetting("_sslKeepAlive").length() > 0) {
                try {
                    if (socketsession.isSSLKeepAliveConnection(as, s)) {
                        Enumeration enumeration = array.elements();
                        enumeration.nextElement();
                        String s3;
                        while (enumeration.hasMoreElements()) {
                            s3 = (String) enumeration.nextElement();
                            if (s3.startsWith("\"") && s3.endsWith("\"")) {
                                s3 = s3.substring(1, s3.length() - 1);
                            }
                            socketsession.sslOut.println(s3);
                        }

                        socketsession.sslOut.flush();
                    }
                    String s2;
                    while ((s2 = socketsession.sslIn.readLine()) != null) {
                        array1.add(s2);
                        if (s2.startsWith("URLMonitorDuration:")) {
                            break;
                        }
                    }
                } catch (Exception exception) {
                    /* empty */
                }
            } else {
                CommandLine commandline = new CommandLine();
                int k = 0;
                while (k < 4) {
                    array1.copy(commandline.exec(as));
                    l = commandline.getExitValue();
                    if (debugURL != 0) {
                        System.out.println("StatusSSL 1 command.getExitValue(): " + l);
                    }
                    if (l != 200L && debugURL != 0) {
                        System.out.println("Status: BAD SSLURL STATUS cmd: " + as);
                        for (int i1 = 0; i1 < as.length; i1 ++) {
                            System.out.println("Status: BAD SSLURL STATUS cmd: " + as[i1].toString());
                        }

                    }
                    if (l != 0L) {
                        break;
                    }
                    Platform.sleep(2000L);
                    k ++;
                }

                if (l == 0xffffffffc0000005L) {
                    l = kDLLCrashedError;
                }
                if (debugURL != 0) {
                    System.out.println("StatusSSL 2 kDLLCrashedError: " + l);
                }
                if (l == 12045L && l > 12000L && Platform.isWindows()) {
                    HashMap hashmap = MasterConfig.getMasterConfig();
                    if (TextUtils.getValue(hashmap, "_urlRanHTTPSSetup").length() == 0) {
                        String s4 = "cmd /c \"%systemroot%\\system32\\regsvr32 /s /i:u %systemroot%\\system32\\initpki.dll\"";
                        CommandLine commandline1 = new CommandLine();
                        commandline1.exec(s4);
                        LogManager.log("Error", "Ran HTTPS setup script for IE 4.0 (" + commandline1.getExitValue() + ")");
                        hashmap.put("_urlRanHTTPSSetup", "true");
                        try {
                            MasterConfig.saveMasterConfig(hashmap);
                        } catch (Exception exception1) {
                            LogManager.log("Error", "Failed to write master.config: " + exception1);
                        }
                    }
                    array1 = commandline.exec(as);
                    l = commandline.getExitValue();
                    if (debugURL != 0) {
                        System.out.println("StatusSSL 3 getExitValue(): " + l);
                    }
                }
            }
            counterlock.release();
        } catch (RuntimeException e) {
            counterlock.release();
            throw e;
        }
        return l;
    }

    static HashMap getSSL2Only() {
        if (ssl2Cache == null) {
            try {
                String s = Platform.getRoot() + File.separator + "groups" + File.separator + "ssl2.config";
                if ((new File(s)).exists()) {
                    Array array = FrameFile.readFromFile(s);
                    if (!array.isEmpty()) {
                        ssl2Cache = (HashMap) array.front();
                    }
                }
            } catch (IOException ioexception) {
                LogManager.log("Error", "error reading ssl2.config, " + ioexception);
            }
            if (ssl2Cache == null) {
                ssl2Cache = new HashMap();
            }
        }
        return ssl2Cache;
    }

    static void removeSSL2Only(String s) {
        LogManager.log("RunMonitor", "removing ssl2 entry: " + s);
        HashMap hashmap = getSSL2Only();
        hashmap.remove(s);
        try {
            String s1 = Platform.getRoot() + File.separator + "groups" + File.separator + "ssl2.config";
            Array array = new Array();
            array.add(hashmap);
            FrameFile.writeToFile(s1, array);
        } catch (IOException ioexception) {
            LogManager.log("Error", "error writing ssl2.config, " + ioexception);
        }
    }

    static void addSSL2Only(String s, String s1) {
        LogManager.log("RunMonitor", "saving ssl2-only site: " + s);
        HashMap hashmap = getSSL2Only();
        hashmap.put(s, s1);
        try {
            String s2 = Platform.getRoot() + File.separator + "groups" + File.separator + "ssl2.config";
            Array array = new Array();
            array.add(hashmap);
            FrameFile.writeToFile(s2, array);
        } catch (IOException ioexception) {
            LogManager.log("Error", "error writing ssl2.config, " + ioexception);
        }
    }

    static boolean isSSL2Only(String s) {
        HashMap hashmap = getSSL2Only();
        return hashmap.get(s) != null;
    }

    private static long[] check1URL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, Array array, String s8, String s9, String s10, StringBuffer stringbuffer, long l, String s11, int i,
            long l1, StringBuffer stringbuffer1, StringBuffer stringbuffer2) {
        long al[] = checkInternalURL(socketsession, s, s1, s2, s3, s4, s5, s6, s7, array, s8, s9, s10, stringbuffer, l, s11, i, l1, stringbuffer1, stringbuffer2);
        if (al[0] == (long) kSSL2NotFoundError) {
            if (!s8.startsWith(NT_CHALLENGE_RESPONSE_TAG)) {
                s8 = NT_CHALLENGE_RESPONSE_TAG + s8;
            }
            al = checkInternalURL(socketsession, s, s1, s2, s3, s4, s5, s6, s7, array, s8, s9, s10, stringbuffer, l, s11, i, l1, stringbuffer1, stringbuffer2);
            if (al[0] >= 400L) {
                URLInfo urlinfo = new URLInfo(s);
                String s12 = urlinfo.getHost();
                int j = urlinfo.getConnectPort();
                String s13 = s12 + ":" + j;
                removeSSL2Only(s13);
            }
        }
        return al;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    private static long[] checkInternalURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, Array array, String s8, String s9, String s10, StringBuffer stringbuffer, long l, String s11,
            int i, long l1, StringBuffer stringbuffer1, StringBuffer stringbuffer2) {
        String s12 = "";
        if (s.lastIndexOf('#') != -1) {
            s = s.substring(0, s.lastIndexOf('#'));
        }
        if ((debugURL & kDebugRequest) != 0) {
            LogManager.log("RunMonitor", "checking URL... " + s);
        }
        int j = 0;
        if (socketsession.context.getSetting("_keepTryingForGoodStatus").length() > 0) {
            j = TextUtils.toInt(socketsession.context.getSetting("_keepTryingForGoodStatus"));
        }
        long l2 = kURLNoStatusError;
        long l3 = 0L;
        long l4 = System.currentTimeMillis();
        long l6 = 0L;
        long l7 = 0L;
        long l8 = 0L;
        long l9 = 0L;
        long l10 = 0L;
        long l11 = 0L;
        long l12 = 0L;
        boolean flag = false;
        URLInfo urlinfo = new URLInfo(s);
        String s16 = urlinfo.getProtocol();
        String s17 = urlinfo.getHost();
        int k = urlinfo.getConnectPort();
        boolean flag1 = false;
        boolean flag2 = false;
        if (SocketStream.getSSLFactory() != null) {
            flag1 = true;
            if (socketsession.context.getSetting("_sslJavaEnabled").length() > 0) {
                flag1 = true;
            } else if (socketsession.context.getSetting("_sslJavaDisabled").length() > 0) {
                flag1 = false;
            }
        }
        boolean flag3 = false;
        if (s8.startsWith(NT_CHALLENGE_RESPONSE_TAG)) {
            s8 = s8.substring(NT_CHALLENGE_RESPONSE_TAG.length());
            flag3 = true;
        }
        boolean flag4 = false;
        if (socketsession.context.getSetting("_urlMonitorUseApacheHttpClient").length() > 0) {
            flag4 = true;
        }
        SocketStream socketstream = null;
        String s18 = getUserAgent(array);
        if (s18.length() == 0) {
            s18 = socketsession.context.getSetting("_URLUserAgent");
        }
        String s19 = getSslgetOptions(array);
        if (s19.length() == 0) {
            s19 = socketsession.context.getSetting("_sslgetOptions");
        }
        String s20 = socketsession.context.getSetting("_URLMonitorProxyExceptions");
        String s21 = null;
        String s22 = "";
        String s23 = "";
        String s25 = "";
        String s26 = getContentType(array);
        String s27 = encodeParameters(array, s26);
        socketsession.addCookieParameters(array, s);
        if (stringbuffer == null) {
            stringbuffer = new StringBuffer(kURLBufferSize);
        }
        boolean flag5 = socketsession.context.getSetting("_concatURLRedirects").length() != 0;
        if (flag5 && concatBuffer == null) {
            concatBuffer = new StringBuffer(kURLBufferSize);
        }
        if (s8.length() > 0) {
            Base64Encoder base64encoder = new Base64Encoder(s8 + ":" + s9);
            s22 = "Authorization: Basic " + base64encoder.processString() + CRLF;
        }
        if (s6.length() > 0) {
            Base64Encoder base64encoder1 = new Base64Encoder(s6 + ":" + s7);
            String s24 = base64encoder1.processString();
            s25 = "Proxy-Authorization: Basic " + s24 + CRLF;
        }
        long l13 = l1 - System.currentTimeMillis();
        long l14 = l13 / 1000L;
        long l15 = -1L;
        CounterLock counterlock;

        // 617
        try {
            if (s10.length() != 0) {
                String s28 = "";
                if (s27.length() > 0) {
                    s28 = HTTPRequest.encodeString(s27);
                }
                String s32;
                if (s10.indexOf("get.exe") != -1) {
                    String s36 = socketsession.context.getSetting("_URLRemoteOptions");
                    if (s36.length() == 0) {
                        s36 = "-ignoreErrors+-ignoreUnknownCA+-x";
                    }
                    if (flag3 && !s8.startsWith(NT_CHALLENGE_RESPONSE_TAG)) {
                        s8 = NT_CHALLENGE_RESPONSE_TAG + s8;
                    }
                    if (s18.length() > 0) {
                        if (s28.length() > 0) {
                            s28 = s28 + URLEncoder.encode("&");
                        }
                        s28 = s28 + URLEncoder.encode("User-Agent: " + s18);
                    }
                    if (s19.length() > 0) {
                        if (s28.length() > 0) {
                            s28 = s28 + URLEncoder.encode("&");
                        }
                        s28 = s28 + URLEncoder.encode("sslgetOptions: " + s19);
                    }
                    if (s11.length() > 0) {
                        String as[] = TextUtils.split(s11, CRLF);
                        for (int k1 = 0; k1 < as.length; k1 ++) {
                            if (s28.length() > 0) {
                                s28 = s28 + URLEncoder.encode("&");
                            }
                            if (getHeaderType(as[k1]) < 0) {
                                s28 = s28 + URLEncoder.encode("Custom-Header: ");
                            }
                            String s46 = as[k1];
                            int i3 = s46.indexOf("&");
                            if (i3 >= 0 && !s46.startsWith(URLSequenceMonitor.refererStartToken)) {
                                s46 = s46.substring(0, i3);
                            }
                            s28 = s28 + URLEncoder.encode(s46);
                        }

                    }
                    String s13 = socketsession.getCookieHeader(s, true);
                    if (s13.length() > 0) {
                        String s40 = "";
                        if (s36.endsWith("-x")) {
                            s40 = "-x";
                            s36 = s36.substring(0, s36.length() - 2);
                        }
                        String as2[] = TextUtils.split(s13, CRLF);
                        for (int j2 = 0; j2 < as2.length; j2 ++) {
                            s36 = s36 + "-c+%22" + URLEncoder.encode(as2[j2]) + "%22+";
                        }

                        s36 = s36 + s40;
                    }
                    s32 = s10 + "?" + s36 + "+%22" + URLEncoder.encode(s) + "%22" + "+%22" + l14 + "%22" + "+%22" + URLEncoder.encode(s8) + "%22" + "+%22" + URLEncoder.encode(s9) + "%22" + "+%22" + s28 + "%22" + "+%22" + URLEncoder.encode(s5) + "%22"
                            + "+%22" + URLEncoder.encode(s6) + "%22" + "+%22" + URLEncoder.encode(s7) + "%22";
                } else if (s10.indexOf("port.exe") != -1) {
                    s32 = s10 + "&host=" + s;
                } else {
                    s32 = s10 + "?host=" + s;
                }
                Array array2 = null;
                String s41 = "";
                String s43 = "";
                String s47 = "";
                String s49 = "";
                String s51 = "";
                String s54 = "";
                String s56 = "";
                if (Platform.isPortal()) {
                    String s59 = HTTPUtils.getLocationIDByURL(s10);
                    if (Portal.isPortalID(s59)) {
                        PortalSiteView portalsiteview = (PortalSiteView) Portal.getSiteViewForID(s59);
                        if (portalsiteview != null) {
                            s41 = portalsiteview.getProperty(PortalSiteView.pUserName);
                            s43 = portalsiteview.getProperty(PortalSiteView.pPassword);
                            s49 = portalsiteview.getProperty(PortalSiteView.pProxy);
                            s51 = portalsiteview.getProperty(PortalSiteView.pProxyUserName);
                            s54 = portalsiteview.getProperty(PortalSiteView.pProxyPassword);
                        }
                    }
                }
                int k3 = i;
                if (socketsession.context == null)
                    ;
                CounterLock counterlock1 = null;
                if (!socketsession.inRemoteRequest) {
                    counterlock1 = getLocationLock(socketsession.context, s10, s);
                }
                try {
                    long l18 = System.currentTimeMillis() + (l14 + 30L) * 1000L;
                    socketsession.inRemoteRequest = true;
                    long al2[] = check1URL(socketsession, s32, s1, s2, s3, s4, s49, s51, s54, array2, s41, s43, s47, stringbuffer, l, s56, k3, l18, stringbuffer1, stringbuffer2);
                    l2 = al2[0];
                    if (j > 0) {
                        l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s32, 0, j);
                    }
                    if (debugURL != 0) {
                        System.out.println("Status1: " + l2);
                    }
                    l3 = al2[1];
                } finally {
                    socketsession.inRemoteRequest = false;
                    if (counterlock1 != null) {
                        releaseLocationLock(counterlock1);
                    }
                }

                String s65 = stringbuffer.toString();
                int i4 = s65.length();
                stringbuffer.setLength(0);
                int j4 = 0;
                while (j4 < i4) {
                    int k4 = s65.indexOf("\r\r\n", j4);
                    if (k4 < 0) {
                        stringbuffer.append(s65.substring(j4));
                        break;
                    }
                    stringbuffer.append(s65.substring(j4, k4) + "\r\n");
                    j4 = k4 + 3;
                }

                String s68 = stringbuffer.toString();
                if (s10.indexOf("get.exe") != -1) {
                    String s70 = "URLMonitorDuration: ";
                    int k5 = s68.lastIndexOf(s70);
                    if (k5 != -1) {
                        l3 = TextUtils.toLong(s68.substring(k5 + s70.length(), k5 + s70.length() + 10));
                    }
                    s70 = "URLMonitorStatus: ";
                    k5 = s68.lastIndexOf(s70);
                    long l21 = l2;
                    if (k5 != -1) {
                        l21 = TextUtils.toLong(s68.substring(k5 + s70.length(), k5 + s70.length() + 10));
                    }
                    if (l21 != 200L || l2 != (long) kURLContentMatchError && l2 != (long) kURLContentErrorFound) {
                        if (k5 != -1) {
                            l2 = l21;
                            if (debugURL != 0) {
                                System.out.println("Status2 ssl: " + l2);
                            }
                        } else {
                            int j6 = s68.length();
                            if (j6 > 500) {
                                j6 = 500;
                            }
                            String s78 = s68.substring(0, j6);
                            if (j > 0) {
                                l21 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l21, s32, 0, j);
                            }
                            LogManager.log("Error", "Remote URL error, [" + HTTPUtils.getLocationIDByURL(s10) + "] " + lookupStatus(l2) + ", " + s10 + ", " + s + ", detail: " + s78);
                            l2 = kURLRemoteMonitoringError;
                            if (debugURL != 0) {
                                System.out.println("Status3 kURLRemoteMonitoringError: " + l2);
                            }
                        }
                    }
                }
                s68 = getHTTPContent(s68);
                int i5 = s68.lastIndexOf("\r\nURLMonitorStatus: ");
                if (i5 >= 0) {
                    s68 = s68.substring(0, i5);
                }
                stringbuffer.setLength(0);
                stringbuffer.append(s68);
                l12 = stringbuffer.length();
            } else {

                if (flag3 || s16.equals("https") && !flag1 || flag4) {
                    flag2 = true;
                    counterlock = null;
                    try {
                        if (s16.equals("https")) {
                            counterlock = getSSLGroupLock(socketsession.context);
                        }
                        if (flag4) {
                            Vector vector = new Vector();
                            if (socketsession.context.getSetting("_sslKeepAlive").length() > 0) {
                                if (s5.length() > 0) {
                                    vector.add(new Header("Proxy-Connection", "Keep-Alive"));
                                } else {
                                    vector.add(new Header("Connection", "Keep-Alive"));
                                }
                            }
                            vector.add(new Header("User-Agent", s18));
                            Header header = socketsession.getCookieHeader(s);
                            if (header != null) {
                                vector.add(header);
                            }
                            if (s27.length() > 0) {
                                vector.add(new Header("Content-Type", s26));
                            }
                            String s42 = "";
                            HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s8, s9, s42, null, s5, s6, s7, vector, 1, (int) l13, (int) l13);
                            StringBuffer stringbuffer3 = new StringBuffer();
                            ApacheHttpMethod apachehttpmethod = null;
                            long l17 = System.currentTimeMillis();
                            apachehttpmethod = ApacheHttpUtils.getRequest(httprequestsettings, stringbuffer3);
                            l3 = System.currentTimeMillis() - l17;
                            l2 = apachehttpmethod.getStatusCode();
                            if (apachehttpmethod.getResponseBodyAsString() != null) {
                                stringbuffer2.append(apachehttpmethod.getResponseBodyAsString());
                            }
                            if (apachehttpmethod.getResponseBody() != null) {
                                l12 = apachehttpmethod.getResponseBody().length;
                            }
                        } else {
                            if (s5 == null) {
                                s5 = "";
                            }
                            Array array1 = new Array();
                            array1.add(Platform.getRoot() + "/tools/sslget");
                            if (s19.length() > 0) {
                                Array array3 = Platform.split(' ', s19);
                                Enumeration enumeration = array3.elements();
                                while (enumeration.hasMoreElements()) {
                                    array1.add(enumeration.nextElement());
                                }
                            }
                            if (socketsession.context.getSetting("_sslKeepAlive").length() > 0) {
                                array1.add("-keepAlive");
                            }
                            boolean flag6 = socketsession.context.getSetting("_urlUnixSSL").length() == 0;
                            array1.add("-agent");
                            array1.add("\"" + s18 + "\"");
                            String s14 = s11;
                            s14 = socketsession.getCookieHeader(s, flag6) + s14;
                            if (s14.length() > 0) {
                                String as1[] = TextUtils.split(s14, CRLF);
                                for (int i2 = 0; i2 < as1.length; i2 ++) {
                                    array1.add("-c");
                                    array1.add("\"" + as1[i2] + "\"");
                                }

                            }
                            if (s27.length() > 0) {
                                array1.add("-c");
                                array1.add("\"" + CONTENT_TYPE_HEADER + s26 + "\"");
                            }
                            if (array != null) {
                                Enumeration enumeration1 = array.elements();
                                while (enumeration1.hasMoreElements()) {
                                    String s44 = (String) enumeration1.nextElement();
                                    if (TextUtils.startsWithIgnoreCase(s44, CUSTOM_HEADER)) {
                                        array1.add("-c");
                                        array1.add("\"" + s44.substring(CUSTOM_HEADER.length()) + "\"");
                                    }
                                }
                            }
                            array1.add("-x");
                            array1.add(s);
                            array1.add("" + l14);
                            array1.add("\"" + s8 + "\"");
                            array1.add("\"" + s9 + "\"");
                            array1.add("\"" + s27 + "\"");
                            if (!hostIsProxyException(s20, s)) {
                                array1.add("\"" + s5 + "\"");
                            } else {
                                array1.add("\"\"");
                            }
                            array1.add("\"" + s6 + "\"");
                            array1.add("\"" + s7 + "\"");
                            SSLCounter ++;
                            if (sslLock.current() == 0) {
                                LogManager.log("RunMonitor", "ssl block, " + ((sslLock.max - sslLock.current()) + 1) + ", " + s);
                            } else {
                                LogManager.log("RunMonitor", "ssl start, " + SSLCounter + ", " + s);
                            }
                            Array array4 = new Array();
                            l2 = sendSSLRequest(urlinfo.getHost(), socketsession, array1, sslLock, array4);
                            if (debugURL != 0) {
                                System.out.println("Status4 sendSSLRequest: " + l2);
                            }
                            SSLCounter --;
                            Enumeration enumeration2 = array4.elements();
                            int k2 = 0;
                            boolean flag7 = false;
                            l12 = -1L;
                            String s52 = "URLMonitorStatus:";
                            String s55 = "URLMonitorDuration:";
                            String s57 = "URLMonitorDNSDuration:";
                            String s60 = "URLMonitorConnectDuration:";
                            String s62 = "URLMonitorResponseDuration:";
                            String s66 = "URLMonitorDownloadDuration:";
                            boolean flag8 = true;
                            while (enumeration2.hasMoreElements()) {
                                String s69 = (String) enumeration2.nextElement();
                                if (stringbuffer2 != null) {
                                    stringbuffer2.append(s69 + "\n");
                                }
                                if (++ k2 > 2) {
                                    int j5 = s69.indexOf(s52);
                                    if (j5 >= 0) {
                                        String s71 = s69.substring(s69.length() - 10);
                                        l2 = TextUtils.toLong(s71);
                                        if (debugURL != 0) {
                                            System.out.println("Status5 statusString: " + l2);
                                        }
                                        flag8 = false;
                                    }
                                    int i6 = s69.indexOf(s55);
                                    if (i6 >= 0) {
                                        String s72 = s69.substring(s69.length() - 10);
                                        l3 = TextUtils.toLong(s72);
                                    }
                                    i6 = s69.indexOf(s57);
                                    if (i6 >= 0) {
                                        String s73 = s69.substring(s69.length() - 10);
                                        l6 = TextUtils.toLong(s73);
                                    }
                                    i6 = s69.indexOf(s60);
                                    if (i6 >= 0) {
                                        String s74 = s69.substring(s69.length() - 10);
                                        l7 = TextUtils.toLong(s74);
                                    }
                                    i6 = s69.indexOf(s62);
                                    if (i6 >= 0) {
                                        String s75 = s69.substring(s69.length() - 10);
                                        l8 = TextUtils.toLong(s75);
                                    }
                                    i6 = s69.indexOf(s66);
                                    if (i6 >= 0) {
                                        String s76 = s69.substring(s69.length() - 10);
                                        l9 = TextUtils.toLong(s76);
                                    }
                                    if (k2 == 4 && s69.length() == 0) {
                                        flag7 = true;
                                    }
                                    if ((!flag7 || k2 % 2 != 0 || s69.length() != 0) && flag8) {
                                        stringbuffer.append(s69);
                                        stringbuffer.append('\n');
                                    }
                                }
                            }
                            LogManager.log("RunMonitor", "SSL END, " + SSLCounter + ", " + l3 + "   " + s);
                            l12 = stringbuffer.length();
                        }
                        if (counterlock != null) {
                            releaseSSLGroupLock(counterlock);
                        }
                    } catch (Exception exception1) {
                        if (counterlock != null) {
                            releaseSSLGroupLock(counterlock);
                        }
                        throw exception1;
                    }
                } else {
                    String s29 = urlinfo.getFile();
                    String s33 = getHostHeader(array, s17, k, s16);
                    String s37 = null;
                    int j1 = -1;
                    if (s5.length() != 0 && !isProxyException(s20, s17)) {
                        if (!s5.startsWith("http://")) {
                            s5 = "http://" + s5;
                        }
                        j1 = k;
                        s37 = s17;
                        URLInfo urlinfo1 = new URLInfo(s5);
                        s17 = urlinfo1.getHost();
                        k = urlinfo1.getConnectPort();
                        if (!s16.equals("https")) {
                            if (s29.equals("/") && !s.endsWith("/")) {
                                s29 = s + "/";
                            } else {
                                s29 = s;
                            }
                        }
                    }
                    String s45 = socketsession.getCookieHeader(s, false);
                    String s48 = getRequestCommand(array);
                    String s50 = socketsession.getVersion();
                    String s53 = getRequestProtocol(array);
                    if (s53.endsWith("/")) {
                        s53 = s53 + s50;
                    }
                    String s15 = s11;
                    int j3 = -1;
                    String s58 = s15;
                    if ((j3 = s15.indexOf("&" + URLSequenceMonitor.refererEndToken)) != -1) {
                        s15 = s58.substring(0, j3);
                        s15 = s15 + s58.substring(j3 + ("&" + URLSequenceMonitor.refererEndToken).length());
                    }
                    if (s15.length() > 0 && !s15.endsWith(CRLF)) {
                        s15 = s15 + CRLF;
                    }
                    if (array != null) {
                        Enumeration enumeration3 = array.elements();
                        while (enumeration3.hasMoreElements()) {
                            String s63 = (String) enumeration3.nextElement();
                            if (TextUtils.startsWithIgnoreCase(s63, CUSTOM_HEADER)) {
                                s15 = s15 + s63.substring(CUSTOM_HEADER.length()) + CRLF;
                            }
                        }
                    }
                    String s61 = socketsession.getStreamEncoding();
                    String s64 = "";
                    if (s61.length() > 0) {
                        s64 = "Accept-charset: " + s61 + CRLF;
                    }
                    String s67 = s48 + " " + s29 + " " + s53 + CRLF + "User-Agent: " + s18 + CRLF + s64 + "Accept: */*" + CRLF + "Host: " + s33 + CRLF + s22 + s25 + s45 + s15;
                    if (s27.length() == 0) {
                        s67 = s67 + CRLF;
                    } else {
                        if (s61.length() > 0) {
                            s64 = "; charset=" + s61;
                        }
                        s67 = s67 + CONTENT_TYPE_HEADER + s26 + s64 + CRLF + "Content-length: " + s27.length() + CRLF + CRLF + s27;
                    }
                    long l5 = System.currentTimeMillis();
                    long l19 = l5;
                    InetAddress inetaddress = InetAddress.getByName(s17);
                    long l20 = System.currentTimeMillis();
                    l6 = l20 - l19;
                    String s77 = Platform.dottedIPString(inetaddress.getAddress()) + ":" + k;
                    l19 = l20;
                    socketstream = socketsession.connect(s77, inetaddress, k, s16, s37, j1, s6, s7, l1);
                    l20 = System.currentTimeMillis();
                    l7 = l20 - l19;
                    Socket socket = socketstream.socket;
                    int k6 = (int) (l1 - System.currentTimeMillis());
                    if ((long) k6 <= kTimedOutValue) {
                        throw new InterruptedIOException();
                    }
                    if (s16.equals("https") && socketsession.context.getSetting("_urlCertDays").length() > 0) {
                        try {
                            SSLSession sslsession = ((SSLSocket) socketstream.socket).getSession();
                            X509Certificate ax509certificate[] = sslsession.getPeerCertificateChain();
                            int j7 = 0;
                            for (int k7 = 0; k7 < ax509certificate.length; k7 ++) {
                                X509Certificate x509certificate = ax509certificate[k7];
                                long l22 = System.currentTimeMillis();
                                long l23 = l22 + 0x5265c00L;
                                int i8 = 0;
                                while (true) {
                                    Date date = new Date(l23);
                                    try {
                                        x509certificate.checkValidity(date);
                                    } catch (CertificateExpiredException certificateexpiredexception) {
                                        if (i8 == 0) {
                                            i8 ++;
                                        }
                                        break;
                                    } catch (CertificateNotYetValidException certificatenotyetvalidexception) {
                                        break;
                                    }
                                    i8 ++;
                                    l23 += 0x5265c00L;
                                }

                                if (i8 <= 0) {
                                    continue;
                                }
                                if (j7 == 0) {
                                    j7 = i8;
                                    continue;
                                }
                                if (j7 > i8) {
                                    j7 = i8;
                                }
                            }

                            if (j7 > 1) {
                                l15 = j7;
                            }
                            if (j7 == 1) {
                                l15 = 0L;
                            }
                        } catch (SSLPeerUnverifiedException sslpeerunverifiedexception) {
                            /* empty */
                        }
                    }
                    Platform.setSocketTimeout(socket, k6);
                    if ((debugURL & kDebugRequest) != 0) {
                        LogManager.log("RunMonitor", "sending request... " + s67);
                    }
                    l19 = System.currentTimeMillis();
                    socketstream.transmit(s67);
                    if (stringbuffer2 != null) {
                        stringbuffer2.append(s67);
                    }
                    if ((debugURL & kDebugRequest) != 0) {
                        LogManager.log("RunMonitor", "request sent");
                    }
                    long al3[];
                    if (s50.equals("1.0")) {
                        al3 = fillBuffer(socketsession, socketstream, l1, stringbuffer, l, l19);
                    } else {
                        al3 = fillBufferParse(s48, socketsession, socketstream, socket, l1, stringbuffer, l, l19);
                    }
                    if (stringbuffer2 != null) {
                        stringbuffer2.append(stringbuffer.toString());
                    }
                    if (!socketstream.receivedReply || al3.length > 2 && al3[2] == -1L) {
                        socketstream.receivedEndOfStreamOnFirst = false;
                        LogManager.log("RunMonitor", "reopening connection to " + inetaddress);
                        socketstream.reconnect();
                        l19 = l20;
                        socketstream = socketsession.connect(s77, inetaddress, k, s16, s37, j1, s6, s7, l1);
                        l20 = System.currentTimeMillis();
                        l7 += l20 - l19;
                        Socket socket1 = socketstream.socket;
                        int i7 = (int) (l1 - System.currentTimeMillis());
                        if ((long) i7 <= kTimedOutValue) {
                            throw new InterruptedIOException();
                        }
                        Platform.setSocketTimeout(socket1, i7);
                        if (debugURL != 0) {
                            LogManager.log("RunMonitor", "sending request2..." + s67);
                            System.out.println("URLOriginalMonitor : sending request2..." + s67);
                        }
                        l19 = System.currentTimeMillis();
                        socketstream.transmit(s67);
                        if (stringbuffer2 != null) {
                            stringbuffer2.append(s67);
                        }
                        if (debugURL != 0) {
                            LogManager.log("RunMonitor", "request2 sent");
                            System.out.println("URLOriginalMonitor : request2 sent");
                        }
                        if (s50.equals("1.0")) {
                            al3 = fillBuffer(socketsession, socketstream, l1, stringbuffer, l, l19);
                        } else {
                            al3 = fillBufferParse(s48, socketsession, socketstream, socket1, l1, stringbuffer, l, l19);
                        }
                        if (stringbuffer2 != null) {
                            stringbuffer2.append(stringbuffer.toString());
                        }
                    }
                    l12 = al3[0];
                    l8 = al3[1];
                    l20 = System.currentTimeMillis();
                    l9 = l20 - l19 - l8;
                    l3 = l20 - l5;
                    if ((debugURL & kDebugIO) != 0) {
                        LogManager.log("RunMonitor", "total, duration: " + l3 + ", clock: " + System.currentTimeMillis());
                    }
                }
            }
            // 5637

            if ((debugURL & kDebugData) != 0) {
                LogManager.log("RunMonitor", "content=" + stringbuffer);
            }
            URLScannerInputStream urlscannerinputstream = new URLScannerInputStream(stringbuffer, l2);
            urlscannerinputstream.parse();
            if (urlscannerinputstream.contentLength != -1L) {
                l12 = urlscannerinputstream.contentLength;
            }
            l10 = urlscannerinputstream.lastModified;
            l11 = urlscannerinputstream.date;
            s21 = urlscannerinputstream.location;
            l2 = urlscannerinputstream.status;
            if (j > 0) {
                l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s, 0, j);
            }
            if (debugURL != 0) {
                System.out.println("Status6 scanner.status: " + l2);
            }
            if ((debugURL & kDebugReply) != 0) {
                if (l2 != 200L) {
                    LogManager.log("RunMonitor", "status=" + l2 + ", reply=" + stringbuffer);
                } else {
                    LogManager.log("RunMonitor", "status=" + l2);
                }
            }
            if (socketstream != null) {
                socketstream.keepAlive = urlscannerinputstream.isKeepAlive();
            }
            if (!socketsession.inRemoteRequest) {
                flag = urlscannerinputstream.refreshRedirect;
            }
            // 5905
        } catch (UnknownHostException e) {
            l2 = kURLBadHostNameError;
            if (debugURL != 0) {
                System.out.println("Status7 kURLBadHostNameError: " + l2 + " exception: " + e.toString());
            }
        } catch (InterruptedIOException e) {
            l2 = kURLTimeoutError;
            if (debugURL != 0) {
                System.out.println("Status8 kURLTimeoutError: " + l2 + " exception: " + e.toString());
            }
        } catch (SocketException e) {
            if (debugURL != 0) {
                e.printStackTrace();
                LogManager.log("RunMonitor", "socket exception, " + e);
            }
            if (Platform.noRoute(e)) {
                l2 = kURLNoRouteToHostError;
                if (debugURL != 0) {
                    System.out.println("Status9 kURLNoRouteToHostError: " + l2 + " exception: " + e.toString());
                }
            } else {
                l2 = kURLNoConnectionError;
                if (debugURL != 0) {
                    System.out.println("Status10 kURLNoConnectionError: " + l2 + " exception: " + e.toString());
                }
                if (Platform.isWindows()) {
                    l2 = kSSL2NotFoundError;
                    if (debugURL != 0) {
                        System.out.println("Status11 kSSL2NotFoundError: " + l2 + " exception: " + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            String s34 = e.getClass() + ", " + e.getMessage();
            if (s34.indexOf("SSLException") != -1 && Platform.isWindows()) {
                String s38 = s17 + ":" + k;
                addSSL2Only(s38, s + ", " + s34);
                l2 = kSSL2NotFoundError;
                if (debugURL != 0) {
                    System.out.println("Status12 kSSL2NotFoundError: " + l2 + " exception: " + e.toString());
                }
            } else {
                e.printStackTrace();
                l2 = kSSL2NotFoundError;
                if (debugURL != 0) {
                    System.out.println("Status13 kSSL2NotFoundError: " + l2 + " exception: " + e.toString());
                }
            }
        } finally {
            if (j > 0) {
                l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s, 0, j);
            }
            if (socketstream != null) {
                if (socketstream.receivedEndOfStreamOnFirst) {
                    String s79 = s17 + ":" + k;
                    addSSL2Only(s79, s + ", " + " first line null");
                    l2 = kSSL2NotFoundError;
                    if (debugURL != 0) {
                        System.out.println("Status14 kSSL2NotFoundError: " + l2);
                    }
                    socketstream.receivedEndOfStreamOnFirst = false;
                }
                socketsession.release(socketstream);
            }
        }

        // 6637

        if (200L < l2 && l2 < 300L && socketsession.context.getSetting("_urlEnable2xxStatus").length() == 0) {
            l2 = 200L;
        }
        if (debugURL != 0) {
            System.out.println("Status15 200: " + l2);
        }
        if (l2 == 301L || l2 == 302L || l2 == 303L || l2 == 307L || flag) {
            if (s21 != null) {
                s21 = TextUtils.unescapeHTML(s21);
                s21 = resolveURL(s21, new URLInfo(s1), "");
                socketsession.refererURL = s21;
                long l16 = socketsession.context.getSettingAsLong("_urlRedirectMax", DEFAULT_MAX_REDIRECTS);
                if ((long) i <= l16) {
                    if ((debugURL & kDebugRequest) != 0) {
                        LogManager.log("RunMonitor", "redirect=" + s21);
                    }
                    socketsession.updateCookies(stringbuffer.toString(), s);
                    if (stringbuffer1 != null) {
                        stringbuffer1.setLength(0);
                        stringbuffer1.append(s21);
                    }
                    if (flag5 && concatBuffer != null) {
                        concatBuffer.append(stringbuffer.toString());
                    }
                    stringbuffer.setLength(0);
                    s8 = socketsession.originalUserName;
                    s9 = socketsession.originalPassword;
                    long al1[] = check1URL(socketsession, s21, s21, s2, s3, s4, s5, s6, s7, null, s8, s9, s2, stringbuffer, l, s11, i + 1, l1, stringbuffer1, stringbuffer2);
                    l2 = al1[0];
                    if (j > 0) {
                        l2 = getURLStatus_ForBackupToRegularMeansOnly(socketsession, l2, s21, 0, j);
                    }
                    if (debugURL != 0) {
                        System.out.println("Status16 redirectResult: " + l2);
                    }
                    l10 = al1[3];
                    l11 = al1[4];
                    l3 += al1[1];
                    l12 += al1[2];
                    l6 += al1[5];
                    l7 += al1[6];
                    l8 += al1[7];
                    l9 += al1[8];
                }
            }
        } else {
            if (l2 == (long) kURLNoStatusError) {
                if (socketsession.context.getSetting("_urlAllowNoStatus").length() > 0) {
                    l2 = kURLok;
                    if (debugURL != 0) {
                        System.out.println("Status17 kURLok: " + l2);
                    }
                } else {
                    LogManager.log("Error", "URL missing status: " + s);
                }
            }
            if (flag5 && concatBuffer != null) {
                concatBuffer.append(stringbuffer.toString());
                stringbuffer.setLength(0);
                stringbuffer.append(concatBuffer.toString());
                concatBuffer = null;
            }
            String s30 = I18N.UnicodeToString(stringbuffer.toString(), I18N.nullEncoding());
            if (l2 == 200L && s4.length() != 0) {
                int i1 = TextUtils.matchExpression(s30, s4);
                if (i1 != Monitor.kURLok && I18N.hasUnicode(s4)) {
                    String s39 = getHTMLEncoding(s30);
                    i1 = TextUtils.matchExpression(s30, I18N.UnicodeToString(s4, s39));
                }
                if (i1 == 200) {
                    l2 = kURLContentErrorFound;
                }
                if (debugURL != 0) {
                    System.out.println("Status18 kURLContentErrorFound: " + l2);
                }
            }
            if (l2 == 200L && s3.length() != 0) {
                l2 = TextUtils.matchExpression(s30, s3);
                if (debugURL != 0) {
                    System.out.println("Status19 TextUtils.matchExpression(contents,match): " + l2);
                }
                if (l2 != (long) Monitor.kURLok && I18N.hasUnicode(s3)) {
                    String s35 = getHTMLEncoding(s30);
                    l2 = TextUtils.matchExpression(s30, I18N.UnicodeToString(s3, s35));
                    if (debugURL != 0) {
                        System.out.println("Status20 TextUtils.matchExpression(contents, I18N.UnicodeToString(match,encoding): " + l2);
                    }
                }
            }
        }
        if (socketsession.context.getSetting("_urlDetailLogEnabled").length() > 0 && s.indexOf("get.exe") == -1) {
            String s31 = "";
            if (s10.length() > 0) {
                s31 = "[" + HTTPUtils.getLocationIDByURL(s10) + "]";
            }
            LogManager.log(socketsession.context.getSetting(pURLLogName), s31 + s + "\t" + l2 + "\t" + l3 + "\t" + l12 + "\t" + l10 + "\t" + l11 + "\t" + l6 + "\t" + l7 + "\t" + l8 + "\t" + l9 + "\t" + socketsession.context.getProperty(pName) + "\t"
                    + socketsession.context.getProperty(pGroupID) + "\t" + socketsession.context.getProperty(pID));
        }
        if (!socketsession.inRemoteRequest) {
            internalURLs ++;
            internalURLBytes += l12;
            internalURLDuration += l3;
            if (l2 != 200L) {
                internalURLErrors ++;
            }
        }
        if (s10.length() > 0) {
            internalRemoteURLs ++;
            internalRemoteBytes += l12;
            internalRemoteDuration += l3;
            if (l2 != 200L) {
                internalRemoteErrors ++;
            }
        } else if (s16.equals("https")) {
            if (flag2) {
                internalSecureURLs ++;
                internalSecureBytes += l12;
                internalSecureDuration += l3;
                if (l2 != 200L) {
                    internalSecureErrors ++;
                }
            } else {
                internalSecureJavaURLs ++;
                internalSecureJavaBytes += l12;
                internalSecureJavaDuration += l3;
                if (l2 != 200L) {
                    internalSecureJavaErrors ++;
                }
            }
        } else if (!socketsession.inRemoteRequest) {
            internalJavaURLs ++;
            internalJavaBytes += l12;
            internalJavaDuration += l3;
            if (l2 != 200L) {
                internalJavaErrors ++;
            }
        }
        long al[] = new long[10];
        al[0] = l2;
        if (debugURL != 0) {
            System.out.println("Status21 #############################results[0]: " + l2);
        }
        al[1] = l3;
        al[2] = l12;
        al[3] = l10;
        al[4] = l11;
        al[5] = l6;
        al[6] = l7;
        al[7] = l8;
        al[8] = l9;
        al[9] = l15;
        return al;
    }

    private static long getURLStatus_ForBackupToRegularMeansOnly(SocketSession socketsession, long l, String s, int i, int j) {
        if (i >= j) {
            System.out.println("URLOriginalMonitor KEEPTRYING!!! - URL: " + s + "FAILED THIS MANY TIMES: " + i);
            return l;
        }
        if (l != 200L && l != 302L && l != 301L) {
            System.out.println("URLOriginalMonitor KEEPTRYING!!! - URL: " + s + " status: " + l + " times attempted: " + i);
            int k = 0;
            if (socketsession.context.getSetting("_timeout").length() > 0) {
                k = TextUtils.toInt(socketsession.context.getSetting("_timeout")) * 1000;
            } else {
                k = 60000;
            }
            Platform.sleep(k);
            StringBuffer stringbuffer = new StringBuffer();
            try {
                URL url = new URL(s);
                URLConnection urlconnection = url.openConnection();
                System.out.println("URLOriginalMonitor KEEPTRYING Received a : " + urlconnection.getClass().getName());
                System.out.println("URLOriginalMonitor KEEPTRYING Received connection string : " + urlconnection.toString());
                urlconnection.setDoInput(true);
                urlconnection.setUseCaches(false);
                System.out.println("URLOriginalMonitor KEEPTRYING Getting an input stream...");
                java.io.InputStream inputstream = urlconnection.getInputStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                for (String s5 = null; (s5 = bufferedreader.readLine()) != null;) {
                    stringbuffer.append(s5);
                }

                System.out.println("URLOriginalMonitor KEEPTRYING CONTENTS: " + stringbuffer.toString());
            } catch (MalformedURLException malformedurlexception) {
                String s1 = "URLOriginalMonitor KEEPTRYING error attempted backup URL retrieval: " + s + " exception: " + malformedurlexception.toString();
                LogManager.log("Error", s1);
                LogManager.log("Error", FileUtils.stackTraceText(malformedurlexception));
                System.out.println(s1);
                malformedurlexception.printStackTrace();
                return getURLStatus_ForBackupToRegularMeansOnly(socketsession, l, s, ++ i, j);
            } catch (IOException ioexception) {
                String s2 = "URLOriginalMonitor KEEPTRYING error attempted backup URL retrieval: " + s + " exception: " + ioexception.toString();
                LogManager.log("Error", s2);
                LogManager.log("Error", FileUtils.stackTraceText(ioexception));
                System.out.println(s2);
                ioexception.printStackTrace();
                return getURLStatus_ForBackupToRegularMeansOnly(socketsession, l, s, ++ i, j);
            } catch (Exception exception) {
                String s3 = "URLOriginalMonitor KEEPTRYING error attempted backup URL retrieval: " + s + " exception: " + exception.toString();
                LogManager.log("Error", s3);
                LogManager.log("Error", FileUtils.stackTraceText(exception));
                System.out.println(s3);
                exception.printStackTrace();
                return getURLStatus_ForBackupToRegularMeansOnly(socketsession, l, s, ++ i, j);
            } catch (Throwable throwable) {
                String s4 = "URLOriginalMonitor KEEPTRYING error attempted backup URL retrieval: " + s + " exception: " + throwable.toString();
                LogManager.log("Error", s4);
                LogManager.log("Error", FileUtils.stackTraceText(throwable));
                System.out.println(s4);
                throwable.printStackTrace();
                return getURLStatus_ForBackupToRegularMeansOnly(socketsession, l, s, ++ i, j);
            }
            return 200L;
        } else {
            return l;
        }
    }

    static long[] fillBuffer(SocketSession socketsession, SocketStream socketstream, long l, StringBuffer stringbuffer, long l1, long l2) throws IOException {
        byte abyte0[] = socketsession.getByteBuffer();
        return socketstream.fillBuffer(abyte0, l, stringbuffer, l1, l2);
    }

    static long[] readHeader(SocketStream socketstream, StringBuffer stringbuffer, long l, long l1) throws IOException {
        long l2 = 0L;
        if ((debugURL & kDebugIO) != 0) {
            LogManager.log("RunMonitor", "readHeader starting...");
            l2 = Platform.timeMillis();
        }
        long l3 = -1L;
        long l4 = kReadUntilEnd;
        String s = "content-length: ";
        String s1 = "transfer-encoding:";
        long l5 = -1L;
        StringBuffer stringbuffer1 = new StringBuffer(kHeaderBufferSize);
        int i = 0;
        boolean flag = false;
        boolean flag1 = true;
        do {
            String s2 = socketstream.HTTPReadLine();
            if (debugURL != 0) {
                TextUtils.debugPrint("URLOriginalMonitor: headerLine=" + s2);
            }
            if ((debugURL & kDebugIO) != 0) {
                long l6 = Platform.timeMillis();
                LogManager.log("RunMonitor", "readheader, duration: " + (l6 - l2) + ", time: " + System.currentTimeMillis() + ", :" + s2);
                l2 = Platform.timeMillis();
            }
            if (l5 == -1L) {
                l5 = System.currentTimeMillis() - l;
            }
            if (s2 == null) {
                socketstream.receivedEndOfStream = true;
                if (flag1 && socketstream.protocol.equals("https")) {
                    socketstream.receivedEndOfStreamOnFirst = true;
                }
                break;
            }
            socketstream.receivedReply = true;
            if (flag1 && s2.trim().length() == 0) {
                flag1 = false;
                continue;
            }
            flag1 = false;
            stringbuffer1.append(s2 + CRLF);
            if (s2.length() == 0) {
                if (!flag) {
                    break;
                }
                i = 0;
                stringbuffer1 = new StringBuffer(kHeaderBufferSize);
                flag = false;
            } else {
                s2 = s2.trim();
                s2 = s2.toLowerCase();
                if (i == 0) {
                    int j = s2.indexOf(' ') + 1;
                    if (j != -1) {
                        l3 = TextUtils.readInteger(s2, j);
                        if (l3 == 100L) {
                            flag = true;
                        }
                    }
                } else if (s2.indexOf(s) != -1) {
                    if (l4 != kReadChunked) {
                        String s3 = s2.substring(s.length()).trim();
                        l4 = TextUtils.toInt(s3);
                    }
                } else if (s2.indexOf(s1) != -1 && s2.indexOf("chunked") != -1) {
                    l4 = kReadChunked;
                }
                i ++;
            }
        } while (true);
        stringbuffer.append(stringbuffer1.toString());
        if (l1 - System.currentTimeMillis() <= kTimedOutValue) {
            throw new InterruptedIOException();
        }
        if (l5 == -1L) {
            l5 = 0L;
        }
        long al[] = new long[3];
        al[0] = l4;
        al[1] = l5;
        al[2] = l3;
        return al;
    }

    static long readData(SocketSession socketsession, SocketStream socketstream, Socket socket, StringBuffer stringbuffer, long l, long l1, long l2) throws IOException {
        long l3 = 0L;
        if ((debugURL & kDebugIO) != 0) {
            LogManager.log("RunMonitor", "read starting...");
            l3 = Platform.timeMillis();
        }
        long l4 = 0L;
        long l5 = l2;
        try {
            byte abyte0[] = socketsession.getByteBuffer();
            long l6 = abyte0.length;
            if ((debugURL & kDebugIO) != 0) {
                long l7 = Platform.timeMillis();
                LogManager.log("RunMonitor", "read loop, duration: " + (l7 - l3));
                l3 = Platform.timeMillis();
            }
            do {
                if (l5 == 0L) {
                    break;
                }
                int i = (int) (l1 - System.currentTimeMillis());
                if ((long) i <= kTimedOutValue) {
                    throw new InterruptedIOException();
                }
                Platform.setSocketTimeout(socket, i);
                long l8 = l6;
                if (l5 != kReadUntilEnd && l5 < l6) {
                    l8 = l5;
                }
                long l9 = 0L;
                try {
                    l9 = socketstream.read(abyte0, 0, (int) l8);
                } catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
                    LogManager.log("Error", "READSIZE ERROR, read=" + l8 + ", request=" + l2 + ", total=" + l4 + ", " + arrayindexoutofboundsexception);
                    throw arrayindexoutofboundsexception;
                }
                if ((debugURL & kDebugIO) != 0) {
                    long l10 = Platform.timeMillis();
                    LogManager.log("RunMonitor", "read, duration: " + (l10 - l3) + ", bytes: " + l9 + ",timeout: " + i + ", time: " + System.currentTimeMillis());
                    l3 = Platform.timeMillis();
                }
                if (l9 == -1L) {
                    socketstream.receivedEndOfStream = true;
                    break;
                }
                socketstream.receivedReply = true;
                if (l5 != kReadUntilEnd) {
                    l5 -= l9;
                }
                l4 += l9;
                long l11 = l9;
                if ((long) stringbuffer.length() + l9 > l) {
                    l11 = l - (long) stringbuffer.length();
                }
                if (stringbuffer != null) {
                    stringbuffer.append(new String(abyte0, 0, (int) l11, socketstream.getEncoding()));
                }
            } while (true);
        } catch (SocketException socketexception) {
            if (l5 != kReadUntilEnd) {
                throw socketexception;
            }
        }
        if (l1 - System.currentTimeMillis() <= kTimedOutValue) {
            throw new InterruptedIOException();
        } else {
            return l4;
        }
    }

    static long[] fillBufferParse(String s, SocketSession socketsession, SocketStream socketstream, Socket socket, long l, StringBuffer stringbuffer, long l1, long l2) throws IOException {
        if ((debugURL & kDebugIO) != 0) {
            LogManager.log("RunMonitor", "reading HTTP 1.1 reply");
        }
        socketstream.receivedReply = false;
        long l3 = 0L;
        long l4 = 0L;
        long l5 = -1L;
        try {
            if (stringbuffer != null) {
                stringbuffer.setLength(0);
            }
            int i = (int) (l - System.currentTimeMillis());
            if ((long) i <= kTimedOutValue) {
                if ((debugURL & kDebugIO) != 0) {
                    LogManager.log("RunMonitor", "1.1 reply timeout, " + i);
                }
                throw new InterruptedIOException();
            }
            Platform.setSocketTimeout(socket, i);
            long al1[] = readHeader(socketstream, stringbuffer, l2, l);
            l5 = al1[2];
            if (!s.equals("HEAD")) {
                long l6 = al1[0];
                l4 = al1[1];
                if (l6 == kReadChunked) {
                    do {
                        String s1 = socketstream.HTTPReadLine();
                        if ((debugURL & kDebugIO) != 0) {
                            LogManager.log("RunMonitor", "read chunk header=" + s1);
                        }
                        if (s1 == null) {
                            socketstream.receivedEndOfStream = true;
                            break;
                        }
                        socketstream.receivedReply = true;
                        long l7 = TextUtils.readHex(s1);
                        if (l7 == 0L) {
                            break;
                        }
                        l3 += readData(socketsession, socketstream, socket, stringbuffer, l1, l, l7);
                        socketstream.HTTPReadLine();
                    } while (true);
                    String s2;
                    do {
                        s2 = socketstream.HTTPReadLine();
                        if ((debugURL & kDebugIO) != 0) {
                            LogManager.log("RunMonitor", "read chunk footer=" + s2);
                        }
                        if (s2 == null) {
                            socketstream.receivedEndOfStream = true;
                            break;
                        }
                        socketstream.receivedReply = true;
                    } while (s2.length() != 0);
                } else {
                    l3 = readData(socketsession, socketstream, socket, stringbuffer, l1, l, l6);
                }
            }
        } catch (SocketException socketexception) {
            if (l3 != 0L) {
                throw socketexception;
            }
        }
        if (l - System.currentTimeMillis() <= kTimedOutValue) {
            throw new InterruptedIOException();
        } else {
            long al[] = new long[3];
            al[0] = l3;
            al[1] = l4;
            al[2] = l5;
            return al;
        }
    }

    public static boolean isProxyException(String s, String s1) {
        if (s.length() > 0) {
            String as[] = TextUtils.split(s, ",");
            for (int i = 0; i < as.length; i ++) {
                if (s1.equalsIgnoreCase(as[i]) || TextUtils.match(s1, as[i])) {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean hostIsProxyException(String s, String s1) {
        if (s.length() == 0) {
            return false;
        } else {
            return isProxyException(s, Platform.hostFromURL(s1));
        }
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
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pCheckContent) {
            if (s.equals("reset") || s.equals("baseline") && getProperty(pCheckContentResetTime).length() == 0) {
                setProperty(pCheckContentResetTime, Platform.timeMillis());
                s = "baseline";
            } else if (s.length() > 0 && !s.equals("on") && !s.equals("baseline")) {
                s = "on";
            }
        } else {
            String s1 = verifyUrlMonitorProperty(stringproperty, s, httprequest, hashmap);
            if (s1 != null) {
                s = s1;
            } else {
                s = super.verify(stringproperty, s, httprequest, hashmap);
            }
        }
        return s;
    }

    public static String replaceSpacesInURL(String s) {
        s = s.trim();
        s = TextUtils.replaceString(s, " ", "%20");
        return s;
    }

    public static String verifyUrlMonitorProperty(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        boolean flag = stringproperty.getName() == pURL.getName() || stringproperty.getName().regionMatches(0, "_reference", 0, 10);
        boolean flag1 = stringproperty.getName() == pContentMatch.getName() || stringproperty.getName().regionMatches(0, "_content", 0, 8);
        boolean flag2 = stringproperty.getName() == pProxy.getName();
        boolean flag3 = stringproperty.getName() == pErrorContent.getName() || stringproperty.getName().regionMatches(0, "_errorContent", 0, 8);
        if (flag) {
            s = s.trim();
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                s = replaceSpacesInURL(s);
            } else {
                URLInfo urlinfo = new URLInfo(s);
                String s3 = urlinfo.getProtocol();
                if (s3.length() == 0) {
                    s = "http://" + s;
                    s3 = "http";
                }
                if (s3.equals("https")) {
                    if (Platform.isWindows()) {
                        String s4 = "";
                        if (!Platform.hasLibrary("wininet.dll")) {
                            s4 = s4 + "missing wininet.dll, ";
                        }
                        if (!Platform.hasLibrary("schannel.dll")) {
                            s4 = s4 + "missing schannel.dll, ";
                        }
                        if (s4.length() != 0) {
                            hashmap.put(stringproperty, s4 + " <a href=http://www.microsoft.com/ie/ >Download Internet Explorer</a>");
                        }
                    } else if (SocketStream.getSSLFactory() == null) {
                        hashmap.put(stringproperty, "See the online <a href\"http://support.merc-int.com/\">Knowledge Base</a> for info on monitoring HTTPS installation on Solaris");
                    }
                } else if (!s3.equals("http") && !s3.equals("mms")) {
                    hashmap.put(stringproperty, "only HTTP, HTTPS, and MMS are currently supported");
                }
            }
        } else if (flag1) {
            String s1 = TextUtils.legalMatchString(s);
            if (s1.length() > 0) {
                hashmap.put(stringproperty, s1);
            }
        } else if (flag2) {
            if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            } else if (s.length() > 0) {
                int i = s.indexOf(':');
                int j = -1;
                if (i != -1) {
                    j = TextUtils.readInteger(s, i + 1);
                }
                if (j == -1) {
                    hashmap.put(stringproperty, "missing port number in Proxy address");
                }
            }
        } else if (flag3) {
            String s2 = TextUtils.legalMatchString(s);
            if (s2.length() > 0) {
                hashmap.put(stringproperty, s2);
            }
        } else {
            s = null;
        }
        return s;
    }

    static String stripDotDot(String s) {
        do {
            int i = s.lastIndexOf('?');
            int j = s.indexOf("..");
            if (j == -1 || i != -1 && j > i) {
                break;
            }
            int k = -1;
            int l = j - 2;
            do {
                if (l <= 0) {
                    break;
                }
                if (s.charAt(l) == '/') {
                    k = l;
                    break;
                }
                l --;
            } while (true);
            if (k == -1) {
                break;
            }
            if (s.substring(0, k).endsWith(":/")) {
                s = s.substring(0, j) + s.substring(j + 3);
            } else {
                s = s.substring(0, k) + s.substring(j + 2);
            }
        } while (true);
        return s;
    }

    static String stripDotSlash(String s) {
        int i = s.indexOf("//");
        if (i == -1) {
            return s;
        }
        int j = s.indexOf("/", i + 2);
        if (j == -1) {
            return s;
        }
        do {
            int k = s.lastIndexOf('?');
            int l = s.indexOf("./", j + 1);
            if (l != -1 && (k == -1 || l <= k)) {
                s = s.substring(0, l) + s.substring(l + 2);
            } else {
                return s;
            }
        } while (true);
    }

    public static String resolveURL(String s, URLInfo urlinfo, String s1) {
        s = TextUtils.removeChars(s, "\n\r");
        s = s.trim();
        String s2 = urlinfo.getPort();
        if (s2.length() > 0) {
            s2 = ":" + s2;
        }
        boolean flag = true;
        int i = s.indexOf(':');
        if (i != -1) {
            String s3 = s.substring(0, i);
            String s4 = s3.toLowerCase();
            if (TextUtils.onlyChars(s4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
                flag = false;
            }
            if ((s4.equals("http") || s4.equals("https")) && !s.startsWith(s3 + "://")) {
                flag = true;
            }
        } else if (s.startsWith("//")) {
            flag = false;
            s = urlinfo.getProtocol() + ":" + s;
        }
        if (flag) {
            if (TextUtils.startsWithIgnoreCase(s, "http:") || TextUtils.startsWithIgnoreCase(s, "https:")) {
                int j = s.indexOf(":");
                s = s.substring(j + 1);
            }
            if (s.startsWith("/")) {
                if (s1.length() > 0) {
                    if (!s1.endsWith("/")) {
                        s1 = s1 + "/";
                    }
                    URLInfo urlinfo1 = new URLInfo(s1);
                    String s5 = urlinfo1.getPort();
                    if (s5.length() > 0) {
                        s5 = ":" + s5;
                    }
                    s = urlinfo1.getProtocol() + "://" + urlinfo1.getHost() + s5 + s;
                } else {
                    s = urlinfo.getProtocol() + "://" + urlinfo.getHost() + s2 + s;
                }
            } else if (s1.length() == 0) {
                s1 = urlinfo.getFile();
                int k = s1.lastIndexOf("/");
                int l = s1.indexOf("?");
                if (l >= 0 && k > l) {
                    k = s1.lastIndexOf("/", l);
                }
                if (k >= 0 && k < s1.length() - 1) {
                    s1 = s1.substring(0, k + 1);
                }
                if (!s1.endsWith("/")) {
                    s1 = s1 + "/";
                }
                s = urlinfo.getProtocol() + "://" + urlinfo.getHost() + s2 + s1 + s;
            } else {
                if (!s1.endsWith("/")) {
                    s1 = s1 + "/";
                }
                s = s1 + s;
            }
        }
        s = stripDotDot(s);
        s = stripDotSlash(s);
        s = TextUtils.removeChars(s, "\n\r");
        s = s.trim();
        return s;
    }

    public static void addURLToMap(HashMap hashmap, String s) {
        if (!TextUtils.getValue(hashmap, s).equals("done")) {
            hashmap.put(s, "new");
        }
    }

    public static int getHeaderType(String s) {
        if (TextUtils.startsWithIgnoreCase(s, CUSTOM_CONTENT)) {
            return CUSTOM_CONTENT_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, CUSTOM_HEADER)) {
            return CUSTOM_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, CONTENT_TYPE_HEADER)) {
            return CONTENT_TYPE_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, HOST_HEADER)) {
            return HOST_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, USER_AGENT_HEADER)) {
            return USER_AGENT_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, SET_COOKIE_HEADER)) {
            return SET_COOKIE_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, METHOD_HEADER)) {
            return METHOD_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, REQUEST_PROTOCOL_HEADER)) {
            return REQUEST_PROTOCOL_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, ACTION_HEADER)) {
            return ACTION_HEADER_TYPE;
        }
        if (TextUtils.startsWithIgnoreCase(s, SSLGET_HEADER)) {
            return SSLGET_HEADER_TYPE;
        } else {
            return -1;
        }
    }

    public static String getHTTPHeaders(String s) {
        return HTTPUtils.getHTTPPart(s, true);
    }

    public static String getHTTPContent(String s) {
        return HTTPUtils.getHTTPPart(s, false);
    }

    public static long[] sendHTTPRequest(String s, String s1, String s2, String s3, String s4, String s5, Array array, StringBuffer stringbuffer, long l, String s6, int i) {
        String s7 = "";
        String s8 = "";
        String s9 = "";
        int j = 0;
        SocketSession socketsession = SocketSession.getSession(null);
        StringBuffer stringbuffer1 = new StringBuffer();
        long al[] = checkURL(socketsession, s, s8, s9, s3, s4, s5, array, s1, s2, s7, stringbuffer, l, s6, j, i, stringbuffer1);
        socketsession.close();
        return al;
    }

    public static void main(String args[]) {
        if (args.length > 0) {
            if (args[0].equals("-api")) {
                String s = "http://support.merc-int.com/";
                String s3 = "";
                String s5 = "";
                String s8 = "";
                Array array = new Array();
                array.add("KEYWORDS=john");
                array.add("SUBSTRING=substring");
                String s11 = "";
                String s12 = "";
                StringBuffer stringbuffer = new StringBuffer(kURLBufferSize);
                long l = 50000L;
                String s14 = "";
                int j = 60000;
                long al1[] = sendHTTPRequest(s, s11, s12, s3, s5, s8, array, stringbuffer, l, s14, j);
                System.out.println("status=" + al1[0]);
                System.out.println("duration=" + al1[1] + " ms");
                System.out.println("size=" + al1[2] + " bytes");
                System.out.println("contents=" + stringbuffer);
                System.exit(0);
            } else if (!args[0].equals("-testsslget") && args[0].equals("-r")) {
                String s1 = args[1];
                URLInfo urlinfo = new URLInfo(args[2]);
                String s6 = args[3];
                System.out.println(resolveURL(s1, urlinfo, s6));
                System.exit(0);
            }
            if (args[0].startsWith("-")) {
                String s2 = "";
                String s4 = "";
                String s7 = "";
                String s9 = "";
                String s10 = "";
                Array array1 = new Array();
                String s13 = "";
                int i = 0;
                do {
                    if (i >= args.length) {
                        break;
                    }
                    if (args[i].equals("-h")) {
                        s13 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-u")) {
                        s2 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-p")) {
                        s4 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-x")) {
                        s7 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-xu")) {
                        s10 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-xp")) {
                        s9 = args[++ i];
                        i ++;
                    } else if (args[i].equals("-d")) {
                        array1.add(args[++ i]);
                        i ++;
                    }
                } while (true);
                StringBuffer stringbuffer1 = new StringBuffer(kURLBufferSize);
                long al[] = checkURL(SocketSession.getSession(null), s13, "", "", s7, s10, s9, array1, s2, s4, "", stringbuffer1, 50000L, "", 0, DEFAULT_TIMEOUT, null);
                System.err.println("STATUS=" + lookupStatus((int) al[0]));
                System.err.println("TIME=" + al[1]);
                System.err.println("SIZE=" + al[2]);
                System.err.println("HEADERS\n-----------------------------");
                System.out.println(getHTTPHeaders(stringbuffer1.toString()));
                System.out.println("");
                System.err.println("CONTENT\n-----------------------------");
                System.out.println(getHTTPContent(stringbuffer1.toString()));
                System.err.println("--------------------------------------");
            }
        }
    }

    public int getCostInLicensePoints() {
        return 1;
    }

    static {
        millisecondPrecision = 2;
        debugURL = 0;
        sslLock = null;
        HashMap hashmap = MasterConfig.getMasterConfig();
        try {
            int i = TextUtils.toInt(TextUtils.getValue(hashmap, "_sslgetMaximum"));
            if (i <= 0) {
                i = 5;
            }
            if (sslLock == null) {
                sslLock = new CounterLock(i);
            }
        } catch (Exception exception) {
        }
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the URL to be verified.  If the URL starts with https://, then a secure connection will be made using SSL. (example: http://demo." + Platform.exampleDomain + ")");
        pURL.setParameterOptions(true, 1, false);
        pURLEncoding = new StringProperty("_URLEncoding");
        pURLEncoding.setDisplayText("Encoding Character Set", "Enter code page (ie Cp1252 or Shift_JIS or EUC-JP)");
        pURLEncoding.setParameterOptions(true, 17, true);
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional, match against content of URL, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
        pContentMatch.setParameterOptions(true, 3, false);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 4, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional list of proxy servers to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 5, true);
        pImages = new BooleanProperty("_getImages", "");
        pImages.setDisplayText("Retrieve Images", "when selected, all of the graphics externally referenced in the page will also be retrieved and calculated in the total response time");
        pImages.setParameterOptions(true, 6, true);
        pFrames = new BooleanProperty("_getFrames", "");
        pFrames.setDisplayText("Retrieve Frames", "when selected, all of the URLs referenced by frames in a frameset will be retrieved and calculated in the total response time");
        pFrames.setParameterOptions(true, 7, true);
        pErrorContent = new StringProperty("_errorContent");
        pErrorContent.setDisplayText("Error If Match", "optionally generate an error if the content of the URL contains this text");
        pErrorContent.setParameterOptions(true, 8, true);
        pCheckContent = new ScalarProperty("_checkContent", "");
        pCheckContent.setDisplayText("Check for Content Changes", "generate error if the content of the URL changes - resetting the saved contents updates the contents checked against during the next monitor run");
        pCheckContent.setParameterOptions(true, 9, true);
        pMonitorRunCount = new NumericProperty("_monitorRunCount", "0");
        pMonitorRunCount.setDisplayText("Baseline Interval", "The number of monitor runs to be averaged for a <a href=\"/SiteView/docs/RollingBaseline.htm\" target=HELP>Rolling Baseline</a>.");
        pMonitorRunCount.setParameterOptions(true, 17, true);
        pCheckContentResetTime = new StringProperty("_checkContentResetTime", "");
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization");
        pUserName.setParameterOptions(true, 9, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
        pPassword.setParameterOptions(true, 10, true);
        pPassword.isPassword = true;
        pChallengeResponse = new BooleanProperty("_challengeResponse", "");
        pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
        pChallengeResponse.setParameterOptions(true, 11, true);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 12, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 13, true);
        pProxyPassword.isPassword = true;
        pPostData = new StringProperty("_postData", "", "POST Variables");
        pPostData.setDisplayText("POST Data", "optional name=value variables, one per line, to send with a POST request");
        pPostData.setParameterOptions(true, 14, true);
        pPostData.isMultiLine = true;
        pErrorOnRedirect = new BooleanProperty("_errorOnRedirect", "");
        pErrorOnRedirect.setDisplayText("Error If Redirected", "when selected, generate an error if the URL is redirected");
        pErrorOnRedirect.setParameterOptions(true, 15, true);
        pMeasureDetails = new BooleanProperty("_measureDetails", "");
        pMeasureDetails.setDisplayText("Show Detailed Measurements", "when selected, detailed measurement times are displayed for DNS lookup, connecting, server response, and downloading.");
        pMeasureDetails.setParameterOptions(true, 16, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pDNSTime = new NumericProperty("dnsTime", "0", "milliseconds");
        pDNSTime.setLabel("dns time");
        pDNSTime.setStateOptions(2);
        pDNSTime.similarProperty = pRoundTripTime;
        pConnectTime = new NumericProperty("connectTime", "0", "milliseconds");
        pConnectTime.setLabel("connect time");
        pConnectTime.setStateOptions(3);
        pConnectTime.similarProperty = pRoundTripTime;
        pResponseTime = new NumericProperty("responseTime", "0", "milliseconds");
        pResponseTime.setLabel("response time");
        pResponseTime.setStateOptions(4);
        pResponseTime.similarProperty = pRoundTripTime;
        pDownloadTime = new NumericProperty("downloadTime", "0", "milliseconds");
        pDownloadTime.setLabel("download time");
        pDownloadTime.setStateOptions(5);
        pDownloadTime.similarProperty = pRoundTripTime;
        pSize = new NumericProperty("size", "0", "bytes");
        pSize.setLabel("size");
        pSize.setIsThreshold(true);
        pAge = new NumericProperty("age", "0", "seconds");
        pAge.setLabel("age");
        pAge.setIsThreshold(true);
        pPercentDeviationRTT = new PercentProperty("_percentDeviationRTT", "0");
        pPercentDeviationRTT.setLabel("Deviation Percentage(roundtrip time)");
        pPercentDeviationRTT.setStateOptions(6);
        pPercentDeviationRTT.setIsThreshold(true);
        pPercentDeviationDNS = new PercentProperty("_percentDeviationDNS", "0");
        pPercentDeviationDNS.setLabel("Deviation Percentage(dns time)");
        pPercentDeviationDNS.setStateOptions(7);
        pPercentDeviationDNS.setIsThreshold(true);
        pPercentDeviationDNL = new PercentProperty("_percentDeviationDNL", "0");
        pPercentDeviationDNL.setLabel("Deviation Percentage(download time)");
        pPercentDeviationDNL.setStateOptions(8);
        pPercentDeviationDNL.setIsThreshold(true);
        pPercentDeviationCNT = new PercentProperty("_percentDeviationCNT", "0");
        pPercentDeviationCNT.setLabel("Deviation Percentage(connect time)");
        pPercentDeviationCNT.setStateOptions(9);
        pPercentDeviationCNT.setIsThreshold(true);
        pPercentDeviationRSP = new PercentProperty("_percentDeviationRSP", "0");
        pPercentDeviationRSP.setLabel("Deviation Percentage(response time)");
        pPercentDeviationRSP.setStateOptions(10);
        pPercentDeviationRSP.setIsThreshold(true);
        pRollingBaseValuesRTT = new StringProperty("rollingbasevaluesrtt");
        pRollingBaseValuesDNS = new StringProperty("rollingbasevaluesdns");
        pRollingBaseValuesDNL = new StringProperty("rollingbasevaluesdnl");
        pRollingBaseValuesRSP = new StringProperty("rollingbasevaluesrsp");
        pRollingBaseValuesCNT = new StringProperty("rollingbasevaluescnt");
        pTotalErrors = new NumericProperty("totalErrors", "0", "errors");
        pTotalErrors.setLabel("total errors");
        pTotalErrors.setIsThreshold(true);
        pDaysUntilCertExpiration = new NumericProperty("daysUntilCertExpires");
        pDaysUntilCertExpiration.setLabel("Certificate Expiration Days Remaining.");
        pDaysUntilCertExpiration.setIsThreshold(true);
        pStatusText = new StringProperty("statusText");
        pHost = new StringProperty("host");
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        pOverallStatus = new StringProperty("overallStatus");
        pOverallStatus.setLabel("overall status");
        pOverallStatus.setIsThreshold(true);
        pLastChecksum = new StringProperty("lastChecksum");
        pLastCheckContentTime = new StringProperty("lastCheckContentTime", "");
        pURLHeader = new StringProperty("urlHeader");
        pFrameErrorList = new StringProperty("frameErrorList");
        pImageErrorList = new StringProperty("imageErrorList");
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        StringProperty astringproperty[] = { pPercentDeviationDNS, pPercentDeviationDNL, pPercentDeviationCNT, pPercentDeviationRTT, pPercentDeviationRSP, pRollingBaseValuesRTT, pRollingBaseValuesDNS, pRollingBaseValuesCNT, pRollingBaseValuesRSP,
                pRollingBaseValuesDNL, pMonitorRunCount, pURL, pTimeout, pContentMatch, pErrorContent, pProxy, pProxyUserName, pProxyPassword, pPostData, pCheckContent, pErrorOnRedirect, pMeasureDetails, pChallengeResponse, pUserName, pPassword, pImages,
                pFrames, pRoundTripTime, pSize, pAge, pStatusText, pStatus, pOverallStatus, pTotalErrors, pLastChecksum, pURLHeader, pFrameErrorList, pImageErrorList, pDNSTime, pConnectTime, pResponseTime, pDownloadTime, pMatchValue,
                pLastCheckContentTime, pCheckContentResetTime, pHost, pDaysUntilCertExpiration, pURLEncoding };
        addProperties("COM.dragonflow.StandardMonitor.URLOriginalMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.URLOriginalMonitor", Rule.stringToClassifier("status == -1002\tnodata"));
        addClassElement("COM.dragonflow.StandardMonitor.URLOriginalMonitor", Rule.stringToClassifier("status != 200\terror\tstatus != 2xx"));
        addClassElement("COM.dragonflow.StandardMonitor.URLOriginalMonitor", Rule.stringToClassifier("totalErrors > 0\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.URLOriginalMonitor", Rule.stringToClassifier("status == 200\tgood\tstatus == 2xx"));
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "description", "Verifies that web pages (HTTP or HTTPS) can be retrieved.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "help", "URLMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "title", "URL Original");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "class", "URLOriginalMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "toolName", "Get URL");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "toolDescription", "Requests a URL from a server and prints the returned data.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "topazName", "URL Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "topazType", "Web Application Server");
        String s = System.getProperty("URLOriginalMonitor.debug");
        if (s != null) {
            debugURL = TextUtils.toInt(s);
            System.out.println("debugURL=" + debugURL);
        }
        millisecondPrecision = TextUtils.toInt(TextUtils.getValue(hashmap, "_defaultMillisecondPrecision"));
        if (millisecondPrecision == 0) {
            millisecondPrecision = 2;
        }
        setClassProperty("COM.dragonflow.StandardMonitor.URLOriginalMonitor", "addable", "false");
    }
}
