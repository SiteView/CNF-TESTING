/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.ApacheHttpClientUtils.*;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.*;

import java.io.*;
import java.util.*;
import jgl.Array;
import jgl.HashMap;
import org.apache.commons.httpclient.*;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLLoaderusingHTTPClient, URLSequenceMonitor

public class URLMonitor extends AtomicMonitor
{

    protected static final int DEFAULT_MILLISECOND_PRECISION = 2;
    static StringProperty pURL;
    static StringProperty pURLEncoding;
    static StringProperty pContentMatch;
    static StringProperty pTimeout;
    static StringProperty pErrorContent;
    static StringProperty pImages;
    static StringProperty pFrames;
    static StringProperty pProxy;
    static StringProperty pPostData;
    static StringProperty pUserName;
    static StringProperty pPassword;
    static StringProperty pDomain;
    static StringProperty pWhenToAuthenticate;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static StringProperty pCheckContent;
    static StringProperty pErrorOnRedirect;
    static StringProperty pMeasureDetails;
    static StringProperty pCheckContentResetTime;
    static StringProperty pDaysUntilCertExpiration;
    static StringProperty pHTTPVersion10;
    static StringProperty pAcceptAllUntrustedCerts;
    static StringProperty pAcceptInvalidCerts;
    static StringProperty pRetries;
    static StringProperty pEncodePostData;
    static StringProperty pRoundTripTime;
    static StringProperty pDNSTime;
    static StringProperty pConnectTime;
    static StringProperty pResponseTime;
    static StringProperty pDownloadTime;
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
    public static StringBuffer concatBuffer = null;
    static int millisecondPrecision;
    public static String NT_CHALLENGE_RESPONSE_TAG = "[NT]";
    static final String encoding = I18N.nullEncoding();
    public static String CONTENT_TYPE_DEFAULT = "application/x-www-form-urlencoded";
    public static String REQUEST_PROTOCOL_DEFAULT = "HTTP/";
    public static String CRLF = "\r\n";
    public static int DEFAULT_TIMEOUT = 60000;
    public static int DEFAULT_MAX_REDIRECTS = 10;
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
    public static final String urlencodedDropDown[] = {
        "contentTypeUrlencoded", "Use content-type:", "forceEncode", "force url encoding", "forceNoEncode", "force NO url encoding"
    };
    public static final String authOn401DropDown[] = {
        "", "Use Global Preference", "authOnFirst", "Authenticate first request", "authOnSecond", "Authenticate if requested"
    };
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
    static final boolean $assertionsDisabled; /* synthetic field */

    public URLMonitor()
    {
    }

    public String getHostname()
    {
        String s = "";
        Object obj = null;
        try
        {
            URI uri = new URI(getProperty(pURL));
            s = uri.getHost();
        }
        catch(URIException uriexception)
        {
            s = getProperty(pURL);
        }
        if(s == null)
        {
            s = "";
        }
        return s;
    }

    public String getTestURL()
    {
        String s = getProperty(pURL);
        if(TextUtils.isSubstituteExpression(s))
        {
            s = TextUtils.substitute(s);
        }
        String s1 = HTTPRequest.encodeString(s);
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=get&host=" + s1;
        String s3 = getProperty(pUserName);
        String s4 = getProperty(pID);
        String s5 = HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID)));
        if(s5.length() > 0)
        {
            s2 = s2 + "&group=" + s5;
        }
        if(s4.length() > 0)
        {
            s2 = s2 + "&id=" + s4;
        }
        if(s3.length() > 0)
        {
            s2 = s2 + "&user=" + HTTPRequest.encodeString(s3);
        }
        if(getPropertyAsBoolean(pFrames))
        {
            s2 = s2 + "&getFrames=on";
        }
        if(getPropertyAsBoolean(pImages))
        {
            s2 = s2 + "&getImages=on";
        }
        String s6 = HTTPRequest.encodeString(TextUtils.obscure(getProperty(pPassword)));
        if(s6.length() > 0)
        {
            s2 = s2 + "&password=" + s6;
        }
        String s7 = HTTPRequest.encodeString(getProperty(pDomain));
        if(s7.length() > 0)
        {
            s2 = s2 + "&domain=" + s7;
        }
        String s8 = HTTPRequest.encodeString(getProperty(pProxy));
        if(s8.length() > 0)
        {
            s2 = s2 + "&proxy=" + s8;
        }
        String s9 = HTTPRequest.encodeString(getProperty(pProxyUserName));
        if(s9.length() > 0)
        {
            s2 = s2 + "&proxyUser=" + s9;
        }
        String s10 = HTTPRequest.encodeString(TextUtils.obscure(getProperty(pProxyPassword)));
        if(s10.length() > 0)
        {
            s2 = s2 + "&proxyPassword=" + s10;
        }
        Enumeration enumeration = getMultipleValues(pPostData);
        if(enumeration.hasMoreElements())
        {
            while(enumeration.hasMoreElements()) 
            {
                String s11 = (String)enumeration.nextElement();
                s2 = s2 + "&postData=" + HTTPRequest.encodeString(s11);
            }
        }
        return s2;
    }

    protected boolean update()
    {
        String s = getProperty(pProxy);
        String s1 = getProperty(pProxyPassword);
        String s2 = getProperty(pProxyUserName);
        Array array = Platform.split(',', s);
        long l;
        if(array.size() <= 1)
        {
            l = getResults_URLAndImagesAndFrames(s, s1, s2);
        } else
        {
            Enumeration enumeration = array.elements();
            long l1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s3 = (String)enumeration.nextElement();
                s3 = s3.trim();
                l1 = getResults_URLAndImagesAndFrames(s3, s1, s2);
                String s4 = getProperty(pStateString);
                setProperty(pStateString, s4 + ", using proxy " + s3);
            } while(shouldTryNextProxy(l1));
        }
        return true;
    }

    public static boolean shouldTryNextProxy(long l)
    {
        return l == (long)kURLTimeoutError || l == (long)kURLNoConnectionError || l == (long)kURLNoRouteToHostError || l == (long)kURLBadHostNameError || l == -1L;
    }

    protected long getResults_URLAndImagesAndFrames(String s, String s1, String s2)
    {
        String s3 = getProperty(pURL);
        if(TextUtils.isSubstituteExpression(s3))
        {
            s3 = TextUtils.substitute(s3);
        }
        String s4 = getProperty(pContentMatch);
        String s5 = getProperty(pErrorContent);
        String s6 = getProperty(pUserName);
        String s7 = getProperty(pPassword);
        String s8 = getProperty(pDomain);
        String s9 = getProperty(pWhenToAuthenticate);
        Array array = TextUtils.enumToArray(getMultipleValues(pPostData));
        int i = getPropertyAsInteger(pTimeout) * 1000;
        if(i == 0)
        {
            i = DEFAULT_TIMEOUT;
        }
        int j = getPropertyAsInteger(pRetries) >= 11 ? 10 : getPropertyAsInteger(pRetries);
        StringBuffer stringbuffer = new StringBuffer(kURLBufferSize);
        long l = getSettingAsLong("_urlContentMatchMax", 50000);
        String s10 = getSetting("_urlOtherHeader");
        progressString += "Retrieving URL " + getProperty(pURL) + "\n";
        StringBuffer stringbuffer1 = new StringBuffer(s3);
        int k = 0;
        Vector vector = null;
        URLContext urlcontext = new URLContext(this);
        urlcontext.setStreamEncoding(getProperty(pURLEncoding));
        urlcontext.setRedirectBase(s3);
        urlcontext.setEncodePostData(getProperty(pEncodePostData));
        Boolean boolean1 = authOnFirstRequest(s9);
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s3, s6, s7, s8, boolean1, s, s2, s1, vector, j, k, k);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, s4, s5, array, stringbuffer, l, s10, i, stringbuffer1, null);
        long l1 = urlresults.getStatus();
        long l2 = urlresults.getTotalDuration();
        long l3 = urlresults.getTotalBytes();
        long l4 = urlresults.getLastModified();
        long l5 = urlresults.getCurrentDate();
        long l6 = urlresults.getDnsTime();
        long l7 = urlresults.getConnectTime();
        long l8 = urlresults.getResponseTime();
        long l9 = urlresults.getDownloadTime();
        String s11 = stringbuffer1.toString();
        String s12 = "";
        if(l1 == 200L && getProperty(pCheckContent).length() > 0)
        {
            String s13 = getProperty(pLastChecksum);
            String s14 = getHTTPContent(stringbuffer.toString());
            String s15 = String.valueOf(PlatformNew.crc(s14));
            if(s13.length() > 0 && !s15.equals(s13))
            {
                l1 = kURLContentChangedError;
            }
            s12 = s13;
            boolean flag = true;
            if(getProperty(pCheckContent).equals("baseline") && getPropertyAsLong(pCheckContentResetTime) < getPropertyAsLong(pLastCheckContentTime) && s13.length() > 0)
            {
                flag = false;
            }
            if(flag && getProperty(pCheckContent).equals("baseline"))
            {
                l1 = 200L;
            }
            if(flag)
            {
                s12 = s15;
                setProperty(pLastCheckContentTime, Platform.timeMillis());
            }
        }
        long l10 = getSettingAsLong("_urlHeaderLinesToSave", 15);
        String s16 = "";
        if(l1 != 200L)
        {
            s16 = parseHeader(stringbuffer.toString(), l10);
        }
        long l11 = l1;
        long l12 = 0L;
        long l13 = 0L;
        String s17 = "";
        long l14 = 0L;
        long l15 = 0L;
        String s18 = "";
        i -= (int)l2;
        boolean flag1 = l1 == 200L && (getPropertyAsBoolean(pFrames) || getPropertyAsBoolean(pImages));
        if(l1 == (long)kURLContentMatchError && getPropertyAsBoolean(pFrames))
        {
            flag1 = true;
        }
        String s19 = "";
        String s20 = "";
        if(flag1)
        {
            urlcontext.updateCookies(stringbuffer.toString(), s11);
            StringBuffer stringbuffer2 = new StringBuffer();
            StringBuffer stringbuffer3 = new StringBuffer();
            HashMap hashmap = new HashMap();
            int i1 = getSettingAsLong("_urlLoadThreads", 1);
            String s22 = "";
            if(array != null)
            {
                Enumeration enumeration = array.elements();
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    String s25 = (String)enumeration.nextElement();
                    if(TextUtils.startsWithIgnoreCase(s25, CUSTOM_HEADER))
                    {
                        s22 = s22 + s25 + CRLF;
                    }
                } while(true);
            }
            URLLoaderusingHTTPClient urlloaderusinghttpclient = new URLLoaderusingHTTPClient(this, urlcontext, hashmap, i1, getPropertyAsBoolean(pFrames), getPropertyAsBoolean(pImages), s11, s6, s7, s8, s9, s22, l, stringbuffer, i, s, s2, s1, null);
            urlloaderusinghttpclient.waitForCompletion();
            long al[] = new long[100];
            al = urlloaderusinghttpclient.getResults(s4, s5, stringbuffer2, stringbuffer3);
            if(l1 == (long)kURLContentMatchError)
            {
                if(urlloaderusinghttpclient.foundContentMatch())
                {
                    l1 = kURLok;
                    s19 = urlloaderusinghttpclient.getContentMatchContents();
                }
            } else
            if(al[0] == (long)kURLContentErrorFound)
            {
                l1 = al[0];
                s20 = urlloaderusinghttpclient.getErrorMatchContents();
            }
            hashmap = null;
            urlloaderusinghttpclient.clear();
            urlloaderusinghttpclient = null;
            l11 = al[0];
            l2 += al[1];
            l3 += al[2];
            l6 += al[5];
            l7 += al[6];
            l8 += al[7];
            l9 += al[8];
            l12 = al[10];
            l13 = al[11];
            s17 = stringbuffer2.toString();
            l14 = al[13];
            l15 = al[14];
            s18 = stringbuffer3.toString();
        }
        long l16 = l15 + l13;
        String s21 = "";
        unsetProperty(pMatchValue);
        if(TextUtils.isValueExpression(s4))
        {
            if(s19.length() == 0)
            {
                s19 = stringbuffer.toString();
            }
            s21 = updateMatchValuesAndProperty(s4, s19, "Content Matched: ");
        }
        if(TextUtils.isValueExpression(s5))
        {
            if(s20.length() == 0)
            {
                s20 = stringbuffer.toString();
            }
            if(s21.length() > 0)
            {
                s21 = s21 + updateMatchValuesAndProperty(s4, s19, ", Error Matched: ");
            } else
            {
                s21 = s21 + updateMatchValuesAndProperty(s4, s19, "Error Matched: ");
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                setProperty(pStatus, String.valueOf(l1));
                setProperty(pStatusText, lookupStatus(l1));
                setProperty(pOverallStatus, String.valueOf(l11));
                setProperty(pTotalErrors, String.valueOf(l16));
                setProperty(pLastChecksum, s12);
                setProperty(pURLHeader, s16);
                setProperty(pHost, getHostname());
                if(l1 == 200L)
                {
                    int j1 = TextUtils.toInt(getProperty(pMonitorRunCount));
                    String s24 = TextUtils.floatToString((float)l2 / 1000F, millisecondPrecision) + " sec";
                    if(j1 > 0)
                    {
                        setRollingBaseProperties(l2, pRollingBaseValuesRTT, pPercentDeviationRTT, j1);
                        setRollingBaseProperties(l6, pRollingBaseValuesDNS, pPercentDeviationDNS, j1);
                        setRollingBaseProperties(l7, pRollingBaseValuesCNT, pPercentDeviationCNT, j1);
                        setRollingBaseProperties(l8, pRollingBaseValuesRSP, pPercentDeviationRSP, j1);
                        setRollingBaseProperties(l9, pRollingBaseValuesDNL, pPercentDeviationDNL, j1);
                    }
                    if(getPropertyAsBoolean(pMeasureDetails))
                    {
                        s24 = "Total " + s24 + " (DNS " + TextUtils.floatToString((float)l6 / 1000F, millisecondPrecision) + " sec" + ", connect " + TextUtils.floatToString((float)l7 / 1000F, millisecondPrecision) + " sec" + ", response " + TextUtils.floatToString((float)l8 / 1000F, millisecondPrecision) + " sec" + ", download " + TextUtils.floatToString((float)l9 / 1000F, millisecondPrecision) + " sec)";
                    }
                    String s26 = "";
                    String s27 = "";
                    String s28 = "";
                    if(getPropertyAsBoolean(pFrames))
                    {
                        int k1 = getSettingAsLong("_urlItemErrorDisplayMax", 2);
                        if(l13 > 0L)
                        {
                            String as[] = TextUtils.split(s17, "\t");
                            String s30 = "";
                            long l18 = k1;
                            if(l13 < (long)k1)
                            {
                                l18 = l13;
                            }
                            for(int j2 = 0; (long)j2 < l18; j2++)
                            {
                                if(j2 != 0)
                                {
                                    s30 = s30 + ", " + as[j2];
                                } else
                                {
                                    s30 = s30 + as[j2];
                                }
                            }

                            if(l13 > (long)k1)
                            {
                                s30 = s30 + ", ...";
                            }
                            s27 = ", " + l13 + " of " + l12 + " frames in error, " + s30;
                        } else
                        if(l12 == 0L)
                        {
                            s27 = ", no frames";
                        } else
                        if(l12 == 1L)
                        {
                            s27 = ", 1 frame";
                        } else
                        {
                            s27 = ", " + l12 + " frames";
                        }
                    }
                    if(getPropertyAsBoolean(pImages))
                    {
                        int i2 = getSettingAsLong("_urlItemErrorDisplayMax", 2);
                        if(l15 > 0L)
                        {
                            String as1[] = TextUtils.split(s18, "\t");
                            String s31 = "";
                            long l19 = i2;
                            if(l15 < (long)i2)
                            {
                                l19 = l15;
                            }
                            for(int k2 = 0; (long)k2 < l19; k2++)
                            {
                                if(k2 != 0)
                                {
                                    s31 = s31 + ", " + as1[k2];
                                } else
                                {
                                    s31 = s31 + as1[k2];
                                }
                            }

                            if(l15 > (long)i2)
                            {
                                s31 = s31 + ", ...";
                            }
                            s26 = ", " + l15 + " of " + l14 + " images in error, " + s31;
                        } else
                        if(l14 == 0L)
                        {
                            s26 = ", no images";
                        } else
                        if(l14 == 1L)
                        {
                            s26 = ", 1 image";
                        } else
                        {
                            s26 = ", " + l14 + " images";
                        }
                    }
                    setProperty(pFrameErrorList, s17);
                    setProperty(pImageErrorList, s18);
                    if(urlresults.getDaysUntilCertExpires() >= 0L)
                    {
                        setProperty(pDaysUntilCertExpiration, urlresults.getDaysUntilCertExpires());
                    } else
                    {
                        setProperty(pDaysUntilCertExpiration, "n/a");
                    }
                    if(getPropertyAsBoolean(pFrames) || getPropertyAsBoolean(pImages))
                    {
                        s28 = " (" + TextUtils.bytesToString(l3) + " total)";
                    }
                    String s29 = "";
                    if(s21.length() > 0)
                    {
                        s29 = ", ";
                    }
                    setProperty(pStateString, s24 + s29 + s21 + s27 + s26 + s28);
                    if(l5 == 0L)
                    {
                        Date date = new Date();
                        l5 = date.getTime() / 1000L;
                    }
                    long l17 = l5 - l4;
                    setProperty(pRoundTripTime, String.valueOf(l2));
                    setProperty(pDNSTime, String.valueOf(l6));
                    setProperty(pConnectTime, String.valueOf(l7));
                    setProperty(pResponseTime, String.valueOf(l8));
                    setProperty(pDownloadTime, String.valueOf(l9));
                    setProperty(pMeasurement, getMeasurement(pRoundTripTime, 4000L));
                    setProperty(pSize, l3);
                    setProperty(pAge, l17);
                } else
                {
                    String s23 = s21 + (s21.length() <= 0 ? "" : ", ");
                    s23 = s23 + lookupStatus(l1);
                    if(!s3.equals(s11))
                    {
                        s23 = s23 + ", " + s11;
                    }
                    setProperty(pNoData, "n/a");
                    setProperty(pStateString, s23 + " " + urlresults.getErrorMessage());
                    setProperty(pMeasurement, String.valueOf(0));
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pDNSTime, "n/a");
                    setProperty(pConnectTime, "n/a");
                    setProperty(pResponseTime, "n/a");
                    setProperty(pDownloadTime, "n/a");
                    setProperty(pSize, "n/a");
                    setProperty(pAge, "n/a");
                }
            }
        }
        return l1;
    }

    public static Boolean authOnFirstRequest(String s)
    {
        Boolean boolean1 = null;
        if(s.equals(authOn401DropDown[0]))
        {
            boolean1 = null;
        } else
        if(s.equals(authOn401DropDown[2]))
        {
            boolean1 = new Boolean(true);
        } else
        if(s.equals(authOn401DropDown[4]))
        {
            boolean1 = new Boolean(false);
        }
        return boolean1;
    }

    public static String[] splitUserDomain_If_NTChallengeInMonitor(String s, String s1)
    {
        if(s.indexOf("\\") > 0 && s1.length() > 0)
        {
            String as[] = s.split("\\\\");
            if(as.length == 2 && as[0].length() > 0 && as[1].length() > 0)
            {
                return as;
            }
        }
        return null;
    }

    protected String updateMatchValuesAndProperty(String s, String s1, String s2)
    {
        Array array = new Array();
        String s3 = updateMatchValues(s, s1, s2, false, array, getSettingAsLong("_urlContentMatchDisplayMax", 150));
        if(array.size() > 0)
        {
            setProperty(pMatchValue, array.at(0));
        }
        return s3;
    }

    void setRollingBaseProperties(long l, StringProperty stringproperty, StringProperty stringproperty1, int i)
    {
        String s = getProperty(stringproperty);
        Array array = new Array();
        if(s != null && s.length() > 0)
        {
            array = TextUtils.splitArray(s, "\t");
        }
        float f = 0.0F;
        int j = 0;
        for(int k = 0; k < array.size(); k++)
        {
            float f2 = TextUtils.toFloat((String)array.at(k));
            if(f2 != 0.0F)
            {
                f += f2;
                j++;
            }
        }

        float f1 = 0.0F;
        if(j > 0)
        {
            f1 = f / (float)j;
        }
        if(array.size() >= i)
        {
            array.popFront();
        }
        array.add(TextUtils.floatToString((float)l / 1000F, millisecondPrecision));
        s = "";
        for(int i1 = 0; i1 < array.size(); i1++)
        {
            s = s + (String)array.at(i1);
            s = s + "\t";
        }

        unsetProperty(stringproperty);
        setProperty(stringproperty, s);
        if(f1 == 0.0F)
        {
            f1 = (float)l / 1000F;
        }
        float f3 = (Math.abs(f1 - (float)l / 1000F) / f1) * 100F;
        if(array.size() < i)
        {
            return;
        } else
        {
            setProperty(stringproperty1, TextUtils.floatToString(f3, 3));
            return;
        }
    }

    static String parseHeader(String s, long l)
    {
        int i = 0;
        int j = 0;
        StringBuffer stringbuffer = new StringBuffer(kHeaderBufferSize);
        do
        {
            if((long)i >= l)
            {
                break;
            }
            int k = s.indexOf('\n', j + 1);
            String s2;
            if(k == -1)
            {
                s2 = s.substring(j);
            } else
            {
                int i1 = k - j;
                if(i1 > 4096)
                {
                    i1 = 4096;
                }
                s2 = s.substring(j, j + i1);
            }
            stringbuffer.append("  " + s2 + "\n");
            if(k == -1)
            {
                break;
            }
            i++;
            j = k + 1;
        } while(true);
        String s1 = stringbuffer.toString();
        s1 = s1.replace('\r', ' ');
        s1 = s1.replace('\n', '^');
        return s1;
    }

    public static String updateMatchValues(String s, String s1, String s2, boolean flag, Array array, int i)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s3 = s1;
        int j = TextUtils.matchExpression(s3, s, array, stringbuffer, s2);
        if(j != Monitor.kURLok && I18N.hasUnicode(s))
        {
            String s4 = getHTMLEncoding(s3);
            TextUtils.matchExpression(s3, I18N.UnicodeToString(s, s4), array, stringbuffer, s2);
        }
        String s5 = stringbuffer.toString();
        if(flag && s5.length() > i)
        {
            s5 = s5.substring(0, i) + "...";
        }
        return s5;
    }

    public static String getChallengeResponse(URLMonitor urlmonitor)
    {
        return "";
    }

    public static String getHTMLEncoding(String s)
    {
        if(s.indexOf("charset=") < 0)
        {
            return "";
        }
        String s1 = s.substring(s.lastIndexOf("charset=") + "charset=".length());
        if(s1.indexOf(">") < 0)
        {
            return "";
        }
        s1 = s1.substring(0, s1.indexOf(">") - 1);
        if(s1.endsWith("\""))
        {
            s1 = s1.substring(0, s1.length() - 1);
        }
        s1.trim();
        return s1;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Enumeration enumeration = super.getStatePropertyObjects(flag);
        Array array = new Array();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            StringProperty stringproperty = (StringProperty)enumeration.nextElement();
            if(stringproperty == pDNSTime || stringproperty == pConnectTime || stringproperty == pResponseTime || stringproperty == pDownloadTime)
            {
                if(getPropertyAsBoolean(pMeasureDetails))
                {
                    array.add(stringproperty);
                }
            } else
            {
                array.add(stringproperty);
            }
        } while(true);
        return array.elements();
    }

    public Array getLogProperties()
    {
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

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pDiagnosticText)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            }
            String s = getProperty(pURL);
            String s2 = getHostname();
            if(s2.equals(""))
            {
                return "URL format error: " + s;
            }
            String s4 = "";
            int i = getPropertyAsInteger(pStatus);
            long l = getSettingAsLong("_urlHeaderLinesToSave", 15);
            if(l != 0L && isContentError(i))
            {
                String s5 = getProperty(pURLHeader);
                s5 = s5.replace('^', '\n');
                s4 = "Response from server:\n" + s5 + "\n";
            }
            if(getPropertyAsLong(pTotalErrors) > 0L)
            {
                if(s4.length() > 0)
                {
                    s4 = s4 + "\n\n";
                }
                String as[] = TextUtils.split(getProperty(pFrameErrorList), "\t");
                if(as.length > 0)
                {
                    s4 = s4 + "Frame Errors:\n";
                    for(int j = 0; j < as.length; j++)
                    {
                        s4 = s4 + as[j] + "\n";
                    }

                    s4 = s4 + "\n";
                }
                as = TextUtils.split(getProperty(pImageErrorList), "\t");
                if(as.length > 0)
                {
                    s4 = s4 + "Image Errors\n";
                    for(int k = 0; k < as.length; k++)
                    {
                        s4 = s4 + as[k] + "\n";
                    }

                    s4 = s4 + "\n";
                }
            }
            s4 = s4 + diagnostic(s2, i);
            return s4;
        }
        if(stringproperty == pDiagnosticTraceRoute)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            }
            String s1 = getProperty(pURL);
            String s3 = getHostname();
            if(s3.equals(""))
            {
                return "URL format error: " + s1;
            } else
            {
                return diagnosticTraceRoute(s3);
            }
        } else
        {
            return super.getProperty(stringproperty);
        }
    }

    public static long[] checkURL(String s)
    {
        return checkURL(s, "", "", "");
    }

    public static long[] checkURL(String s, String s1, String s2, String s3)
    {
        int i = 0;
        Vector vector = null;
        URLContext urlcontext = new URLContext(null);
        urlcontext.setRedirectBase(s);
        urlcontext.setEncodePostData(null);
        String s4 = "";
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s2, s3, s4, null, s1, "", "", vector, 1, i, i);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, "", "", null, null, 50000L, "", DEFAULT_TIMEOUT, null, null);
        return urlresults.getResultsAsArray();
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, Array array, 
            String s6, String s7, String s8, StringBuffer stringbuffer, long l, String s9, 
            int i, int j, StringBuffer stringbuffer1)
    {
        if(s8 == null);
        URLContext urlcontext = new URLContext(socketsession.context);
        if(i > 0)
        {
            urlcontext.getMonitor().setProperty("_errorOnRedirect", "true");
        }
        urlcontext.setCookies(socketsession.cookies);
        urlcontext.setRefererURL(socketsession.refererURL);
        urlcontext.setEncodePostData(socketsession.getEncodePostData());
        String s10 = socketsession.getDomain();
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s6, s7, s10, null, s3, s4, s5, null, 3, 0, 0);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, s1, s2, array, stringbuffer, l, s9, j, stringbuffer1, null);
        socketsession.refererURL = urlcontext.getRefererURL();
        return urlresults.getResultsAsArray();
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, Array array, 
            String s6, String s7, String s8, StringBuffer stringbuffer, long l, String s9, 
            int i, int j, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        s8.length();
        URLContext urlcontext = new URLContext(socketsession.context);
        if(i > 0)
        {
            urlcontext.getMonitor().setProperty("_errorOnRedirect", "true");
        }
        urlcontext.setCookies(socketsession.cookies);
        urlcontext.setRefererURL(socketsession.refererURL);
        urlcontext.setEncodePostData(socketsession.getEncodePostData());
        String s10 = socketsession.getDomain();
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s6, s7, s10, null, s3, s4, s5, null, 3, 0, 0);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, s1, s2, array, stringbuffer, l, s9, j, stringbuffer1, stringbuffer2);
        socketsession.refererURL = urlcontext.getRefererURL();
        return urlresults.getResultsAsArray();
    }

    public static long[] checkURL(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            Array array, String s7, String s8, String s9, StringBuffer stringbuffer, long l, 
            String s10, int i, int j, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        URLResults urlresults = checkURLSeq(socketsession, s, s1, s2, s3, s4, s5, s6, array, s7, s8, s9, stringbuffer, l, s10, i, j, stringbuffer1, stringbuffer2);
        return urlresults.getResultsAsArray();
    }

    public static URLResults checkURLSeq(SocketSession socketsession, String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            Array array, String s7, String s8, String s9, StringBuffer stringbuffer, long l, 
            String s10, int i, int j, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        s9.length();
        URLContext urlcontext = new URLContext(socketsession.context);
        if(i > 0)
        {
            urlcontext.getMonitor().setProperty("_errorOnRedirect", "true");
        }
        urlcontext.setCookies(socketsession.cookies);
        urlcontext.setRefererURL(socketsession.refererURL);
        urlcontext.setStreamEncoding(s1);
        urlcontext.setEncodePostData(socketsession.getEncodePostData());
        String s11 = socketsession.getDomain();
        String s12 = socketsession.getAuthenticationWhenRequested();
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s7, s8, s11, authOnFirstRequest(s12), s4, s5, s6, null, 3, 0, 0);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, s2, s3, array, stringbuffer, l, s10, j, stringbuffer1, stringbuffer2);
        socketsession.refererURL = urlcontext.getRefererURL();
        return urlresults;
    }

    public static URLResults checkURL(HTTPRequestSettings httprequestsettings, URLContext urlcontext, String s, String s1, Array array, StringBuffer stringbuffer, long l, 
            String s2, int i, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        if(s == null)
        {
            s = "";
        }
        if(s1 == null)
        {
            s1 = "";
        }
        if(s2 == null)
        {
            s2 = "";
        }
        if(stringbuffer1 == null)
        {
            stringbuffer1 = new StringBuffer();
        }
        if(array == null)
        {
            array = new Array();
        }
        add_urlOtherHeadersTo_postData(s2, array);
        if(urlcontext.getStreamEncoding().equals(""))
        {
            urlcontext.setStreamEncoding(I18N.getDefaultEncoding());
        }
        urlcontext.setRedirectBase(httprequestsettings.getUrl());
        URLResults urlresults = checkURLRetrieveDoneHere(httprequestsettings, urlcontext, s, s1, array, stringbuffer, l, 0, System.currentTimeMillis() + (long)i, stringbuffer1, stringbuffer2);
        if(urlresults == null)
        {
            urlresults = new URLResults();
            urlresults.setStatus(kURLUnknownError);
        }
        return urlresults;
    }

    private static void add_urlOtherHeadersTo_postData(String s, Array array)
    {
        if(s.length() > 0)
        {
            String as[] = TextUtils.split(s, CRLF);
            for(int i = 0; i < as.length; i++)
            {
                String s1 = "";
                if(getHeaderType(as[i]) < 0)
                {
                    s1 = s1 + "Custom-Header: ";
                }
                String s2 = as[i];
                int j = s2.indexOf("&");
                if(j >= 0 && !s2.startsWith(URLSequenceMonitor.refererStartToken))
                {
                    s2 = s2.substring(0, j);
                }
                s1 = s1 + s2;
                if(array != null)
                {
                    array.add(s1);
                }
            }

        }
    }

    static Vector prepareParametersForApache(Array array)
    {
        Vector vector = new Vector();
        if(array != null)
        {
            Enumeration enumeration = array.elements();
            byte byte0 = -1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s = (String)enumeration.nextElement();
                if(TextUtils.isSubstituteExpression(s))
                {
                    s = TextUtils.substitute(s);
                }
                int i = getHeaderType(s);
                if(i == CUSTOM_CONTENT_TYPE)
                {
                    vector.add(s.substring(CUSTOM_CONTENT.length()) + CRLF);
                } else
                if(s.startsWith("<"))
                {
                    vector.add(s);
                } else
                if(i <= 0)
                {
                    int j = s.indexOf("=");
                    if(j != -1)
                    {
                        String s1 = s.substring(0, j);
                        String s2 = TextUtils.replaceString(s1, HTMLTagParser.POST_EQUALS_TAG, "=");
                        String s3 = s.substring(j + 1, s.length());
                        NameValuePair namevaluepair = new NameValuePair(s2, s3);
                        vector.add(namevaluepair);
                    }
                }
            } while(true);
        }
        return vector;
    }

    static String getRequestCommand(Array array)
    {
        String s;
label0:
        {
            s = "GET";
            if(array == null)
            {
                break label0;
            }
            Enumeration enumeration = array.elements();
            int i = -1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                String s1 = (String)enumeration.nextElement();
                i = getHeaderType(s1);
                if(i == METHOD_HEADER_TYPE)
                {
                    return s1.substring(METHOD_HEADER.length());
                }
            } while(i > 0);
            return "POST";
        }
        return s;
    }

    static String getContentType(Array array)
    {
        String s;
label0:
        {
            s = CONTENT_TYPE_DEFAULT;
            if(array == null)
            {
                break label0;
            }
            Enumeration enumeration = array.elements();
            String s1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                s1 = (String)enumeration.nextElement();
            } while(!TextUtils.startsWithIgnoreCase(s1, CONTENT_TYPE_HEADER));
            s = s1.substring(CONTENT_TYPE_HEADER.length());
        }
        return s;
    }

    static String getUserAgent(Array array)
    {
        String s;
label0:
        {
            s = "";
            if(array == null)
            {
                break label0;
            }
            Enumeration enumeration = array.elements();
            String s1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                s1 = (String)enumeration.nextElement();
            } while(!TextUtils.startsWithIgnoreCase(s1, USER_AGENT_HEADER));
            s = s1.substring(USER_AGENT_HEADER.length());
        }
        return s;
    }

    static String getHostHeader(Array array, String s, int i, String s1)
    {
        String s2;
label0:
        {
            s2 = "";
            if(array == null)
            {
                break label0;
            }
            Enumeration enumeration = array.elements();
            String s3;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                s3 = (String)enumeration.nextElement();
            } while(!TextUtils.startsWithIgnoreCase(s3, HOST_HEADER));
            s2 = s3.substring(HOST_HEADER.length());
        }
        if(s2.length() == 0)
        {
            s2 = s;
            if(s1.equals("https"))
            {
                if(i != 443)
                {
                    s2 = s2 + ":" + i;
                }
            } else
            if(i != 80)
            {
                s2 = s2 + ":" + i;
            }
        }
        return s2;
    }

    static String checkPostFieldForRequestProtocol(Array array)
    {
        String s;
label0:
        {
            s = REQUEST_PROTOCOL_DEFAULT;
            if(array == null)
            {
                break label0;
            }
            Enumeration enumeration = array.elements();
            String s1;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                s1 = (String)enumeration.nextElement();
            } while(!TextUtils.startsWithIgnoreCase(s1, REQUEST_PROTOCOL_HEADER));
            if(!$assertionsDisabled)
            {
                throw new AssertionError("URLMonitor - User entered custom " + s1 + ", in POST data  field. Not supported, only HTTP/");
            }
        }
        return s;
    }

    private static URLResults checkURLRetrieveDoneHere(HTTPRequestSettings httprequestsettings, URLContext urlcontext, String s, String s1, Array array, StringBuffer stringbuffer, long l, 
            int i, long l1, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        if((debugURL & kDebugRequest) != 0)
        {
            toString(urlcontext, httprequestsettings.getUrl(), urlcontext.getRedirectBase(), s, s1, httprequestsettings.getProxy(), httprequestsettings.getProxyUserName(), httprequestsettings.getProxyPassword(), array, httprequestsettings.getAuthUserName(), httprequestsettings.getAuthPassword(), stringbuffer, l, i, l1, stringbuffer1, stringbuffer2);
        }
        if((debugURL & kDebugRequest) != 0)
        {
            LogManager.log("RunMonitor", "checking URL... " + httprequestsettings.getUrl());
        }
        long l2 = kURLNoStatusError;
        long l3 = 0L;
        long l4 = 0L;
        long l5 = 0L;
        long l6 = 0L;
        long l7 = 0L;
        long l8 = 0L;
        long l9 = 0L;
        long l10 = 0L;
        long l11 = -1L;
        long l12 = 0L;
        boolean flag = false;
        String s2 = "";
        String s3 = null;
        urlcontext.addCookieParameters(array, httprequestsettings.getUrl());
        if(stringbuffer == null)
        {
            stringbuffer = new StringBuffer(kURLBufferSize);
            if((debugURL & kDebugData) != 0)
            {
                LogManager.log("RunMonitor", "URLMonitor.checkInternalURL() - CREATE contentBuffer size()=" + stringbuffer.length());
            }
        }
        boolean flag1 = urlcontext.getMonitor().getSetting("_concatURLRedirects").length() != 0;
        if(flag1 && concatBuffer == null)
        {
            concatBuffer = new StringBuffer(kURLBufferSize);
        }
        StringBuffer stringbuffer3 = new StringBuffer();
        String s4 = httprequestsettings.getProxy();
        try
        {
            Vector vector = finalHTTPClientlRequestPreparation(urlcontext, httprequestsettings, array, l1);
            long l14 = System.currentTimeMillis();
            ApacheHttpMethod apachehttpmethod = null;
            if((debugURL & kDebugData) != 0)
            {
                LogManager.log("RunMonitor", "URLMonitor.checkURLRetrieveDoneHere: url: " + httprequestsettings.getUrl());
            }
            if(vector.size() <= 0)
            {
                apachehttpmethod = ApacheHttpUtils.getRequest(httprequestsettings, stringbuffer3);
            } else
            {
                apachehttpmethod = ApacheHttpUtils.postPairs(httprequestsettings, vector, stringbuffer3);
            }
            l3 = System.currentTimeMillis() - l14;
            httprequestsettings.setProxy(s4);
            l11 = apachehttpmethod.getDaysUntilCertExpires();
            l2 = apachehttpmethod.getStatusCode();
            if((debugURL & kDebugData) != 0)
            {
                LogManager.log("RunMonitor", "URLMonitor.checkURLRetrieveDoneHere: status: " + l2);
            }
            if(l1 - System.currentTimeMillis() <= 0L)
            {
                l2 = kURLTimeoutError;
            }
            l12 = fillContentBuffer(stringbuffer, apachehttpmethod, l, httprequestsettings);
            if(stringbuffer2 != null)
            {
                if(stringbuffer2.length() > 0)
                {
                    stringbuffer2.append("SITEVIEW                                         BLANK LINE" + CRLF);
                }
                stringbuffer2.append(stringbuffer.toString());
            }
            l4 = apachehttpmethod.getDNSDuration();
            l5 = apachehttpmethod.getConnectDuration();
            l6 = apachehttpmethod.getResponseDuration();
            l7 = apachehttpmethod.getDownloadDuration();
            if((debugURL & kDebugData) != 0)
            {
                LogManager.log("RunMonitor", "URLMonitor.checkURLRetrieveDoneHere - getDNSDuration()=" + l4 + " and getConnectDuration()=" + l5 + " on class ");
                LogManager.log("RunMonitor", "URLMonitor.checkURLRetrieveDoneHere - getResponseDuration()=" + l6 + " and getDownloadDuration()=" + l7 + " on class ");
            }
            l10 = stringbuffer.length();
            if((debugURL & kDebugData) != 0)
            {
                LogManager.log("RunMonitor", "URLMonitor.checkInternalURL() - contentBuffer=" + stringbuffer);
            }
            if(apachehttpmethod.getContentLength() != -1L)
            {
                l10 = apachehttpmethod.getContentLength();
            }
            l8 = apachehttpmethod.getLastModified();
            l9 = apachehttpmethod.getDate();
            s3 = apachehttpmethod.getLocation();
            flag = apachehttpmethod.getRefreshRedirect();
            if(debugURL != 0)
            {
                System.out.println("Status6 header.status: " + l2);
            }
            if((debugURL & kDebugReply) != 0)
            {
                if(l2 != 200L)
                {
                    LogManager.log("RunMonitor", "status=" + l2 + ", reply=" + stringbuffer);
                } else
                {
                    LogManager.log("RunMonitor", "status=" + l2);
                }
            }
        }
        catch(Exception exception)
        {
            String s6 = exception.getClass() + ", " + exception.getMessage();
            LogManager.log("Error", "url error, " + httprequestsettings.getUrl() + ", " + s6 + ", details: " + FileUtils.stackTraceText(exception));
            if(exception.getMessage().indexOf("timed out") >= 0)
            {
                l2 = kURLTimeoutError;
            } else
            {
                l2 = kURLNoStatusError;
                s2 = "" + kURLNoStatusError + " " + exception.toString();
            }
            if(debugURL != 0)
            {
                System.out.println("Status12 kSSL2NotFoundError: " + l2 + " exception: " + exception.toString());
            }
        }
        finally
        {
            if(stringbuffer3.length() > 0)
            {
                s2 = "status=" + l2 + " " + stringbuffer3.toString();
                if(l2 != 301L && l2 != 302L && l2 != 303L && l2 != 307L)
                {
                    LogManager.log("Error", "URLMonitor. " + s2);
                }
            }
        }
        if(200L < l2 && l2 < 300L && urlcontext.getMonitor().getSetting("_urlEnable2xxStatus").length() == 0)
        {
            l2 = 200L;
        }
        if(debugURL != 0)
        {
            System.out.println("Status15 200: " + l2);
        }
        urlcontext.updateCookies(stringbuffer.toString(), httprequestsettings.getUrl());
        if(l2 == 301L || l2 == 302L || l2 == 303L || l2 == 307L || flag)
        {
            if(s3 != null && s3.length() > 0)
            {
                s3 = TextUtils.unescapeHTML(s3);
                s3 = resolveURL(s3, new URLInfo(urlcontext.getRedirectBase()), "");
                long l13 = urlcontext.getMonitor().getSettingAsLong("_urlRedirectMax", DEFAULT_MAX_REDIRECTS);
                boolean flag2 = urlcontext.getMonitor().getPropertyAsBoolean(pErrorOnRedirect);
                if((long)i <= l13 && !flag2)
                {
                    if((debugURL & kDebugRequest) != 0)
                    {
                        LogManager.log("RunMonitor", "URLMonitor.checkURLRetrieveDoneHere: redirect=" + s3);
                    }
                    if(stringbuffer1 != null)
                    {
                        stringbuffer1.setLength(0);
                        stringbuffer1.append(s3);
                    }
                    if(flag1 && concatBuffer != null)
                    {
                        concatBuffer.append(stringbuffer.toString());
                    }
                    httprequestsettings.setUrl(s3);
                    urlcontext.setRedirectBase(s3);
                    urlcontext.setRefererURL(s3);
                    stringbuffer.setLength(0);
                    URLResults urlresults1 = checkURLRetrieveDoneHere(httprequestsettings, urlcontext, s, s1, null, stringbuffer, l, i + 1, l1, stringbuffer1, stringbuffer2);
                    l2 = urlresults1.getStatus();
                    l3 += urlresults1.getTotalDuration();
                    l10 += urlresults1.getTotalBytes();
                    l8 = urlresults1.getLastModified();
                    l9 = urlresults1.getCurrentDate();
                    l4 += urlresults1.getDnsTime();
                    l5 += urlresults1.getConnectTime();
                    l6 += urlresults1.getResponseTime();
                    l7 += urlresults1.getDownloadTime();
                    s2 = urlresults1.getErrorMessage();
                    l12 = urlresults1.getHtmlTruncatedIfNonZero();
                    if(debugURL != 0)
                    {
                        System.out.println("Status16 redirectResult: " + l2);
                    }
                }
            }
        } else
        {
            if(l2 == (long)kURLNoStatusError)
            {
                if(urlcontext.getMonitor().getSetting("_urlAllowNoStatus").length() > 0)
                {
                    l2 = kURLok;
                    if(debugURL != 0)
                    {
                        System.out.println("Status17 kURLok: " + l2);
                    }
                } else
                {
                    LogManager.log("Error", "URL missing status: " + httprequestsettings.getUrl());
                }
            }
            if(flag1 && concatBuffer != null)
            {
                concatBuffer.append(stringbuffer.toString());
                stringbuffer.setLength(0);
                stringbuffer.append(concatBuffer.toString());
                concatBuffer = null;
                if((debugURL & kDebugData) != 0)
                {
                    LogManager.log("RunMonitor", "URLMonitor.checkInternalURL() - added concatBuffer to front contentBuffer size()=" + stringbuffer.length());
                }
            }
            String s5 = stringbuffer.toString();
            if(l2 == 200L && s1.length() != 0)
            {
                int j = TextUtils.matchExpression(s5, s1);
                if(j != Monitor.kURLok && I18N.hasUnicode(s1))
                {
                    String s8 = getHTMLEncoding(s5);
                    j = TextUtils.matchExpression(s5, I18N.UnicodeToString(s1, s8));
                }
                if(j == 200)
                {
                    l2 = kURLContentErrorFound;
                }
                if(debugURL != 0)
                {
                    System.out.println("Status18 kURLContentErrorFound: " + l2);
                }
            }
            if(l2 == 200L && s.length() != 0)
            {
                l2 = TextUtils.matchExpression(s5, s);
                if(debugURL != 0)
                {
                    System.out.println("Status19 TextUtils.matchExpression(contents,match): " + l2);
                }
                if(l2 != (long)Monitor.kURLok && I18N.hasUnicode(s))
                {
                    String s7 = getHTMLEncoding(s5);
                    l2 = TextUtils.matchExpression(s5, I18N.UnicodeToString(s, s7));
                    if(debugURL != 0)
                    {
                        System.out.println("Status20 TextUtils.matchExpression(contents, I18N.UnicodeToString(match,encoding): " + l2);
                    }
                }
            }
        }
        if(urlcontext.getMonitor().getSetting("_urlDetailLogEnabled").length() > 0 && httprequestsettings.getUrl().indexOf("get.exe") == -1)
        {
            LogManager.log(urlcontext.getMonitor().getSetting(pURLLogName), httprequestsettings.getUrl() + "\t" + l2 + "\t" + l3 + "\t" + l10 + "\t" + l8 + "\t" + l9 + "\t" + l4 + "\t" + l5 + "\t" + l6 + "\t" + l7 + "\t" + urlcontext.getMonitor().getProperty(pName) + "\t" + urlcontext.getMonitor().getProperty(pGroupID) + "\t" + urlcontext.getMonitor().getProperty(pID));
        }
        URLResults urlresults = new URLResults();
        urlresults.setStatus(l2);
        urlresults.setTotalDuration(l3);
        urlresults.setTotalBytes(l10);
        urlresults.setLastModified(l8);
        urlresults.setCurrentDate(l9);
        urlresults.setDnsTime(l4);
        urlresults.setConnectTime(l5);
        urlresults.setResponseTime(l6);
        urlresults.setDownloadTime(l7);
        urlresults.setDaysUntilCertExpires(l11);
        urlresults.setHtmlTruncatedIfNonZero(l12);
        urlresults.setErrorMessage(s2);
        if(debugURL != 0)
        {
            System.out.println("Status21 #############################results[0]: " + l2);
        }
        return urlresults;
    }

    private static long fillContentBuffer(StringBuffer stringbuffer, ApacheHttpMethod apachehttpmethod, long l, HTTPRequestSettings httprequestsettings)
    {
        long l1 = 0L;
        if(stringbuffer == null)
        {
            stringbuffer = new StringBuffer();
        }
        stringbuffer.append("SITEVIEW HTTP REQUEST HTTP REQUEST HTTP REQUEST HTTP REQUEST HTTP REQUEST HTTP REQUEST");
        stringbuffer.append(CRLF);
        stringbuffer.append(apachehttpmethod.getRequestString(httprequestsettings.getHttp11()));
        stringbuffer.append("SITEVIEW                                         BLANK LINE" + CRLF);
        if(apachehttpmethod.getResponseBodyAsString() != null)
        {
            stringbuffer.append("SITEVIEW HTTP RESPONSE HEADERS");
            stringbuffer.append(CRLF);
            stringbuffer.append(apachehttpmethod.getResponseHeadersAsString());
            stringbuffer.append("SITEVIEW HTTP RESPONSE BODY");
            stringbuffer.append(CRLF);
            stringbuffer.append(CRLF);
            int i = stringbuffer.length();
            int j = apachehttpmethod.getResponseBodyAsString().length();
            if((long)i < l)
            {
                long l2 = l - (long)i;
                if((long)j <= l2)
                {
                    stringbuffer.append(apachehttpmethod.getResponseBodyAsString());
                } else
                {
                    if(!$assertionsDisabled && l2 >= (long)j)
                    {
                        throw new AssertionError("Attempting to read past the end of responseBody; availableLength: " + l2 + " responseBodyLength: " + j);
                    }
                    stringbuffer.append(apachehttpmethod.getResponseBodyAsString().substring(0, (int)l2));
                    stringbuffer.append(CRLF + "SITEVIEW HTML page truncated by _urlContentMatchMax=" + l + " defined in groups/master.config file. Current buffer with response headers: " + i + " bytes. Attempted to append: " + j + " bytes." + CRLF);
                    l1 = l;
                }
            } else
            {
                stringbuffer.append("SITEVIEW HTML page truncated by _urlContentMatchMax=" + l + " defined in groups/master.config file. Current buffer with response headers: " + i + " bytes." + CRLF);
                l1 = l;
            }
        }
        return l1;
    }

    private static Vector finalHTTPClientlRequestPreparation(URLContext urlcontext, HTTPRequestSettings httprequestsettings, Array array, long l)
        throws Exception
    {
        if(httprequestsettings.getAuthUserName() == null || httprequestsettings.getAuthUserName().length() == 0)
        {
            String s = urlcontext.getMonitor().getSetting("_defaultAuthUsername");
            if(s.length() > 0)
            {
                httprequestsettings.setAuthUserName(s);
                httprequestsettings.setAuthNTLMDomain(urlcontext.getMonitor().getSetting("_defaultAuthNTLMDomain"));
                httprequestsettings.setAuthPassword(urlcontext.getMonitor().getSetting("_defaultAuthPassword"));
            }
        }
        if(httprequestsettings.getAuthenticationOnFirstRequest() == null)
        {
            String s1 = urlcontext.getMonitor().getSetting("_defaultAuthWhenToAuthenticate");
            if(!$assertionsDisabled && s1.length() <= 0)
            {
                throw new AssertionError("add or set _defaultAuthWhenToAuthenticate=authOnFirst setting in master.config");
            }
            httprequestsettings.setAuthenticationOnFirstRequest(authOnFirstRequest(s1));
        }
        if(httprequestsettings.getAuthNTLMDomain().length() <= 0)
        {
            String as[] = splitUserDomain_If_NTChallengeInMonitor(httprequestsettings.getAuthUserName(), urlcontext.getMonitor().getSetting("_challengeResponse"));
            if(as != null)
            {
                httprequestsettings.setAuthNTLMDomain(as[0]);
                httprequestsettings.setAuthUserName(as[1]);
            }
        }
        long l1 = l - System.currentTimeMillis();
        if(l1 <= 0L)
        {
            throw new Exception("timed out");
        }
        httprequestsettings.setConnectionTimeoutMS((int)l1);
        httprequestsettings.setRequestTimeoutMS((int)l1);
        String s2 = urlcontext.getMonitor().getSetting("_URLMonitorProxyExceptions");
        String s3 = httprequestsettings.getHost();
        if(httprequestsettings.getProxy().length() != 0 && isProxyExcluded(s2, s3))
        {
            httprequestsettings.setProxy("");
        }
        Vector vector = new Vector();
        if(urlcontext.getMonitor().getSetting("_sslKeepAlive").length() > 0)
        {
            if(httprequestsettings.getProxy().length() > 0)
            {
                vector.add(new Header("Proxy-Connection", "Keep-Alive"));
            } else
            {
                vector.add(new Header("Connection", "Keep-Alive"));
            }
        }
        String s4 = getUserAgent(array);
        if(s4.length() == 0)
        {
            s4 = urlcontext.getMonitor().getSetting("_URLUserAgent");
        }
        vector.add(new Header("User-Agent", s4));
        if(array != null)
        {
            Enumeration enumeration = array.elements();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s5 = (String)enumeration.nextElement();
                if(TextUtils.startsWithIgnoreCase(s5, CUSTOM_HEADER))
                {
                    String s7 = s5.substring(CUSTOM_HEADER.length());
                    String as1[] = s7.split(":", 2);
                    if(!$assertionsDisabled && (as1 == null || as1.length <= 0))
                    {
                        throw new AssertionError();
                    }
                    if(as1.length > 1)
                    {
                        vector.add(new Header(as1[0].trim(), as1[1].trim()));
                    } else
                    {
                        vector.add(new Header(as1[0].trim(), ""));
                    }
                }
            } while(true);
        }
        Vector vector1 = urlcontext.getCookieHeader(httprequestsettings.getUrl());
        if(vector1 != null)
        {
            if(debugURL != 0)
            {
                System.out.println("URLMonitor.checkURLRetrieveDoneHere: COOKIE ADD TO HEADERS: " + vector1.toString());
            }
            httprequestsettings.setCookies(vector1);
        } else
        if(debugURL != 0)
        {
            System.out.println("URLMonitor.checkURLRetrieveDoneHere: COOKIE ADD TO HEADERS: NULL - NONE TO ADD ");
        }
        String s6 = urlcontext.getStreamEncoding();
        if(s6.length() > 0)
        {
            vector.add(new Header("Accept-charset", s6));
        }
        vector.add(new Header("Accept", "*/*"));
        Vector vector2 = prepareParametersForApache(array);
        if(vector2.size() > 0)
        {
            String s8 = "";
            if(s6.length() > 0)
            {
                s8 = "; charset=" + s6;
            }
            String s9 = getContentType(array);
            vector.add(new Header("Content-Type", s9 + s8));
        }
        checkPostFieldForRequestProtocol(array);
        long l2 = urlcontext.getMonitor().getSettingAsLong("_urlKeepAlive");
        if(urlcontext.getMonitor().getSetting("_HTTPVersion10").length() > 0)
        {
            httprequestsettings.setHttp11(false);
        }
        if(urlcontext.getMonitor().getSetting("_sslAcceptAllUntrustedCerts").length() > 0)
        {
            httprequestsettings.setAcceptAllUntrustedCerts(true);
        }
        if(urlcontext.getMonitor().getSetting("_sslAcceptInvalidCerts").length() > 0)
        {
            httprequestsettings.setAcceptInvalidCerts(true);
        }
        httprequestsettings.setHeaders(vector);
        String s10 = urlcontext.getEncodePostData();
        if(s10.equals(urlencodedDropDown[0]) || s10.equals(""))
        {
            Vector vector3 = httprequestsettings.getHeaders();
            httprequestsettings.setEncodePostData(false);
            Iterator iterator = vector3.iterator();
            do
            {
                if(!iterator.hasNext())
                {
                    break;
                }
                Header header = (Header)iterator.next();
                if(!header.getName().equalsIgnoreCase("Content-Type") || header.getValue().toLowerCase().indexOf("urlencoded") < 0)
                {
                    continue;
                }
                httprequestsettings.setEncodePostData(true);
                break;
            } while(true);
            String s12 = urlcontext.getMonitor().getSetting("_urlMonitorEncodePostData");
            if(s12.length() > 0)
            {
                httprequestsettings.setEncodePostData(s12.equalsIgnoreCase("true"));
            }
        } else
        if(s10.equals(urlencodedDropDown[2]))
        {
            httprequestsettings.setEncodePostData(true);
        } else
        if(s10.equals(urlencodedDropDown[4]))
        {
            httprequestsettings.setEncodePostData(false);
        } else
        if(!$assertionsDisabled)
        {
            throw new AssertionError("_dontEncodePostData= " + s10 + ". Must be contentTypeUrlencoded, forceEncode, or forceNoEncode.");
        }
        int i = 0;
        String s11 = urlcontext.getMonitor().getSetting("_keepTryingForGoodStatus");
        if(s11.length() > 0)
        {
            i = TextUtils.toInt(s11);
        }
        if(i > 0)
        {
            httprequestsettings.setRetries(i);
        }
        setClientSideCertSettings(urlcontext, httprequestsettings);
        return vector2;
    }

    private static void setClientSideCertSettings(URLContext urlcontext, HTTPRequestSettings httprequestsettings)
    {
        String s = urlcontext.getMonitor().getSetting("_urlClientCert");
        if(s.length() > 0)
        {
            String s1 = urlcontext.getMonitor().getSetting("_urlClientCertPassword");
            httprequestsettings.setCertPassword(s1);
            String s2 = urlcontext.getMonitor().getSetting("groupID");
            String s3 = "templates.certificates";
            if(s2.length() > 0)
            {
                s = Platform.getUsedDirectoryPath(s3, s2) + File.separator + s;
            } else
            {
                s = Platform.getRoot() + File.separator + s3 + File.separator + s;
            }
            httprequestsettings.setCertFilename(s);
        }
    }

    public static boolean isProxyExcluded(String s, String s1)
    {
        if(s.length() > 0)
        {
            String as[] = TextUtils.split(s, ",");
            for(int i = 0; i < as.length; i++)
            {
                if(s1.equalsIgnoreCase(as[i]) || TextUtils.match(s1, as[i]))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pCheckContent)
        {
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
        if(scalarproperty == pEncodePostData)
        {
            Vector vector1 = new Vector();
            vector1.addElement(urlencodedDropDown[0]);
            vector1.addElement(urlencodedDropDown[1]);
            vector1.addElement(urlencodedDropDown[2]);
            vector1.addElement(urlencodedDropDown[3]);
            vector1.addElement(urlencodedDropDown[4]);
            vector1.addElement(urlencodedDropDown[5]);
            return vector1;
        }
        if(scalarproperty == pWhenToAuthenticate)
        {
            Vector vector2 = new Vector();
            vector2.addElement(authOn401DropDown[0]);
            vector2.addElement(authOn401DropDown[1]);
            vector2.addElement(authOn401DropDown[2]);
            vector2.addElement(authOn401DropDown[3]);
            vector2.addElement(authOn401DropDown[4]);
            vector2.addElement(authOn401DropDown[5]);
            return vector2;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pCheckContent)
        {
            if(s.equals("reset") || s.equals("baseline") && getProperty(pCheckContentResetTime).length() == 0)
            {
                setProperty(pCheckContentResetTime, Platform.timeMillis());
                s = "baseline";
            } else
            if(s.length() > 0 && !s.equals("on") && !s.equals("baseline"))
            {
                s = "on";
            }
        } else
        if(stringproperty == pURLEncoding)
        {
            if(s.length() > 0)
            {
                try
                {
                    String s1 = "this is a test";
                    s1.getBytes(s);
                }
                catch(UnsupportedEncodingException unsupportedencodingexception)
                {
                    hashmap.put(stringproperty, "couldn't interpret encoding: " + unsupportedencodingexception);
                    return I18N.getDefaultEncoding();
                }
            }
        } else
        {
            if(stringproperty == pRetries)
            {
                int i = 0;
                try
                {
                    i = Integer.parseInt(s);
                }
                catch(NumberFormatException numberformatexception)
                {
                    return "0";
                }
                if(i > 10)
                {
                    i = 10;
                }
                s = "" + i;
                return s;
            }
            String s2 = verifyUrlMonitorProperty(stringproperty, s, hashmap);
            if(s2 != null)
            {
                s = s2;
            } else
            {
                s = super.verify(stringproperty, s, httprequest, hashmap);
            }
        }
        return s;
    }

    public static String verifyUrlMonitorProperty(StringProperty stringproperty, String s, HashMap hashmap)
    {
        boolean flag = stringproperty.getName() == pURL.getName() || stringproperty.getName().regionMatches(0, "_reference", 0, 10);
        boolean flag1 = stringproperty.getName() == pContentMatch.getName() || stringproperty.getName().regionMatches(0, "_content", 0, 8);
        boolean flag2 = stringproperty.getName() == pProxy.getName();
        boolean flag3 = stringproperty.getName() == pErrorContent.getName() || stringproperty.getName().regionMatches(0, "_errorContent", 0, 8);
        if(flag)
        {
            s = s.trim();
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            {
                URLInfo urlinfo = new URLInfo(s);
                String s3 = urlinfo.getProtocol();
                if(s3.length() == 0)
                {
                    s = "http://" + s;
                    s3 = "http";
                }
                if(!s3.equals("http") && !s3.equals("https") && !s3.equals("mms"))
                {
                    hashmap.put(stringproperty, "only HTTP, HTTPS, and MMS are currently supported");
                }
            }
        } else
        if(flag1)
        {
            String s1 = TextUtils.legalMatchString(s);
            if(s1.length() > 0)
            {
                hashmap.put(stringproperty, s1);
            }
        } else
        if(flag2)
        {
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            } else
            if(s.length() > 0)
            {
                int i = s.indexOf(':');
                int j = -1;
                if(i != -1)
                {
                    j = TextUtils.readInteger(s, i + 1);
                }
                if(j == -1)
                {
                    hashmap.put(stringproperty, "missing port number in Proxy address");
                }
            }
        } else
        if(flag3)
        {
            String s2 = TextUtils.legalMatchString(s);
            if(s2.length() > 0)
            {
                hashmap.put(stringproperty, s2);
            }
        } else
        {
            s = null;
        }
        return s;
    }

    static String stripDotDot(String s)
    {
        do
        {
            int i = s.lastIndexOf('?');
            int j = s.indexOf("..");
            if(j == -1 || i != -1 && j > i)
            {
                break;
            }
            int k = -1;
            int l = j - 2;
            do
            {
                if(l <= 0)
                {
                    break;
                }
                if(s.charAt(l) == '/')
                {
                    k = l;
                    break;
                }
                l--;
            } while(true);
            if(k == -1)
            {
                break;
            }
            if(s.substring(0, k).endsWith(":/"))
            {
                s = s.substring(0, j) + s.substring(j + 3);
            } else
            {
                s = s.substring(0, k) + s.substring(j + 2);
            }
        } while(true);
        return s;
    }

    static String stripDotSlash(String s)
    {
        int i = s.indexOf("//");
        if(i == -1)
        {
            return s;
        }
        int j = s.indexOf("/", i + 2);
        if(j == -1)
        {
            return s;
        }
        do
        {
            int k = s.lastIndexOf('?');
            int l = s.indexOf("./", j + 1);
            if(l != -1 && (k == -1 || l <= k))
            {
                s = s.substring(0, l) + s.substring(l + 2);
            } else
            {
                return s;
            }
        } while(true);
    }

    public static String resolveURL(String s, URLInfo urlinfo, String s1)
    {
        s = TextUtils.removeChars(s, "\n\r");
        s = s.trim();
        String s2 = urlinfo.getPort();
        if(s2.length() > 0)
        {
            s2 = ":" + s2;
        }
        boolean flag = true;
        int i = s.indexOf(':');
        if(i != -1)
        {
            String s3 = s.substring(0, i);
            String s4 = s3.toLowerCase();
            if(TextUtils.onlyChars(s4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"))
            {
                flag = false;
            }
            if((s4.equals("http") || s4.equals("https")) && !s.startsWith(s3 + "://"))
            {
                flag = true;
            }
        } else
        if(s.startsWith("//"))
        {
            flag = false;
            s = urlinfo.getProtocol() + ":" + s;
        }
        if(flag)
        {
            if(TextUtils.startsWithIgnoreCase(s, "http:") || TextUtils.startsWithIgnoreCase(s, "https:"))
            {
                int j = s.indexOf(":");
                s = s.substring(j + 1);
            }
            if(s.startsWith("/"))
            {
                if(s1.length() > 0)
                {
                    if(!s1.endsWith("/"))
                    {
                        s1 = s1 + "/";
                    }
                    URLInfo urlinfo1 = new URLInfo(s1);
                    String s5 = urlinfo1.getPort();
                    if(s5.length() > 0)
                    {
                        s5 = ":" + s5;
                    }
                    s = urlinfo1.getProtocol() + "://" + urlinfo1.getHost() + s5 + s;
                } else
                {
                    s = urlinfo.getProtocol() + "://" + urlinfo.getHost() + s2 + s;
                }
            } else
            if(s1.length() == 0)
            {
                s1 = urlinfo.getFile();
                int k = s1.lastIndexOf("/");
                int l = s1.indexOf("?");
                if(l >= 0 && k > l)
                {
                    k = s1.lastIndexOf("/", l);
                }
                if(k >= 0 && k < s1.length() - 1)
                {
                    s1 = s1.substring(0, k + 1);
                }
                if(!s1.endsWith("/"))
                {
                    s1 = s1 + "/";
                }
                s = urlinfo.getProtocol() + "://" + urlinfo.getHost() + s2 + s1 + s;
            } else
            {
                if(!s1.endsWith("/"))
                {
                    s1 = s1 + "/";
                }
                s = s1 + s;
            }
        }
        s = stripDotDot(s);
        s = stripDotSlash(s);
        s = TextUtils.removeChars(s, "\n\r");
        s = s.trim();
        try
        {
            URI uri = new URI(s.toCharArray());
            if(!$assertionsDisabled && !uri.isAbsoluteURI())
            {
                throw new AssertionError("The url is not complete for the redirect: " + s);
            }
            int i1 = uri.getPort();
            if(uri.getHost().equalsIgnoreCase(urlinfo.getHost()) && urlinfo.getPort().length() > 0 && i1 == -1)
            {
                int j1 = s.indexOf(uri.getHost()) + uri.getHost().length();
                String s6 = s.substring(0, j1);
                String s7 = s.substring(j1);
                if(!$assertionsDisabled && s7.startsWith(":"))
                {
                    throw new AssertionError("This url already has port: " + s);
                }
                s = s6 + ":" + urlinfo.getPort() + s7;
            }
        }
        catch(URIException uriexception) { }
        catch(Exception exception)
        {
            LogManager.log("Error", "URLMonitor.resolveURL: URL: " + s + " Had exception: " + exception.toString());
        }
        return s;
    }

    public static int getHeaderType(String s)
    {
        if(TextUtils.startsWithIgnoreCase(s, CUSTOM_CONTENT))
        {
            return CUSTOM_CONTENT_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, CUSTOM_HEADER))
        {
            return CUSTOM_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, CONTENT_TYPE_HEADER))
        {
            return CONTENT_TYPE_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, HOST_HEADER))
        {
            return HOST_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, USER_AGENT_HEADER))
        {
            return USER_AGENT_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, SET_COOKIE_HEADER))
        {
            return SET_COOKIE_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, METHOD_HEADER))
        {
            return METHOD_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, REQUEST_PROTOCOL_HEADER))
        {
            return REQUEST_PROTOCOL_HEADER_TYPE;
        }
        if(TextUtils.startsWithIgnoreCase(s, ACTION_HEADER))
        {
            return ACTION_HEADER_TYPE;
        } else
        {
            return -1;
        }
    }

    public static String getHTTPHeaders(String s)
    {
        return HTTPUtils.getHTTPPart(s, true);
    }

    public static String getHTTPContent(String s)
    {
        return HTTPUtils.getHTTPPart(s, false);
    }

    public static long[] sendHTTPRequest(String s, String s1, String s2, String s3, String s4, String s5, Array array, StringBuffer stringbuffer, 
            long l, String s6, int i)
    {
        String s7 = "";
        String s8 = "";
        StringBuffer stringbuffer1 = new StringBuffer();
        int j = 0;
        Vector vector = null;
        URLContext urlcontext = new URLContext(null);
        urlcontext.setRedirectBase(s);
        String s9 = "";
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s1, s2, s9, null, s3, s4, s5, vector, 3, j, j);
        URLResults urlresults = checkURL(httprequestsettings, urlcontext, s7, s8, array, stringbuffer, l, s6, i, stringbuffer1, null);
        return urlresults.getResultsAsArray();
    }

    public static void main(String args[])
    {
        if(args.length > 0)
        {
            if(args[0].equals("-api"))
            {
                String s = "http://support.merc-int.com/";
                String s3 = "";
                String s5 = "";
                String s8 = "";
                Array array = new Array();
                array.add("KEYWORDS=john");
                array.add("SUBSTRING=substring");
                String s11 = "";
                String s13 = "";
                String s14 = "";
                StringBuffer stringbuffer = new StringBuffer(kURLBufferSize);
                long l = 50000L;
                String s16 = "";
                int k = 60000;
                String s17 = "";
                String s18 = "";
                StringBuffer stringbuffer2 = new StringBuffer();
                int i1 = 0;
                Vector vector1 = null;
                URLContext urlcontext1 = new URLContext(null);
                urlcontext1.setRedirectBase(s);
                HTTPRequestSettings httprequestsettings1 = new HTTPRequestSettings(s, s11, s13, s14, null, s3, s5, s8, vector1, 3, i1, i1);
                URLResults urlresults1 = checkURL(httprequestsettings1, urlcontext1, s17, s18, array, stringbuffer, l, s16, k, stringbuffer2, null);
                System.out.println("status=" + urlresults1.getStatus());
                System.out.println("duration=" + urlresults1.getTotalDuration() + " ms");
                System.out.println("size=" + urlresults1.getTotalBytes() + " bytes");
                System.out.println("contents=" + stringbuffer);
                System.exit(0);
            } else
            if(args[0].equals("-r"))
            {
                String s1 = args[1];
                URLInfo urlinfo = new URLInfo(args[2]);
                String s6 = args[3];
                System.out.println(resolveURL(s1, urlinfo, s6));
                System.exit(0);
            }
            if(args[0].startsWith("-"))
            {
                String s2 = "";
                String s4 = "";
                String s7 = "";
                String s9 = "";
                String s10 = "";
                String s12 = "";
                Array array1 = new Array();
                String s15 = "";
                int i = 0;
                do
                {
                    if(i >= args.length)
                    {
                        break;
                    }
                    if(args[i].equals("-h"))
                    {
                        s15 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-u"))
                    {
                        s2 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-p"))
                    {
                        s4 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-m"))
                    {
                        s7 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-x"))
                    {
                        s9 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-xu"))
                    {
                        s12 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-xp"))
                    {
                        s10 = args[++i];
                        i++;
                    } else
                    if(args[i].equals("-d"))
                    {
                        array1.add(args[++i]);
                        i++;
                    }
                } while(true);
                int j = 0;
                Vector vector = null;
                URLContext urlcontext = new URLContext(null);
                urlcontext.setRedirectBase(s15);
                HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s15, s2, s4, s7, null, s9, s12, s10, vector, 3, j, j);
                StringBuffer stringbuffer1 = new StringBuffer(kURLBufferSize);
                URLResults urlresults = checkURL(httprequestsettings, urlcontext, "", "", array1, stringbuffer1, 50000L, "", DEFAULT_TIMEOUT, null, null);
                System.err.println("STATUS=" + lookupStatus((int)urlresults.getStatus()));
                System.err.println("TIME=" + urlresults.getTotalDuration());
                System.err.println("SIZE=" + urlresults.getTotalBytes());
                System.err.println("HEADERS\n-----------------------------");
                System.out.println(getHTTPHeaders(stringbuffer1.toString()));
                System.out.println("");
                System.err.println("CONTENT\n-----------------------------");
                System.out.println(getHTTPContent(stringbuffer1.toString()));
                System.err.println("--------------------------------------");
            }
        }
    }

    public int getCostInLicensePoints()
    {
        return 1;
    }

    private static void toString(URLContext urlcontext, String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            Array array, String s7, String s8, StringBuffer stringbuffer, long l, int i, 
            long l1, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        LogManager.log("RunMonitor", "        URLMonitor.toString() ENTERING ENTERING ENTERING ENTERING ENTERING ENTERING ");
        LogManager.log("RunMonitor", "        URLMonitor.toString() MONITOR OBJECT urlContext.getMonitor()=" + urlcontext.getMonitor());
        URLContext _tmp = urlcontext;
        URLContext.printCookie(urlcontext.getCookies());
        LogManager.log("RunMonitor", "        URLMonitor.toString() urlContext.refererURL=" + urlcontext.getRefererURL());
        LogManager.log("RunMonitor", "        URLMonitor.toString() urlString =" + s);
        LogManager.log("RunMonitor", "        URLMonitor.toString() redirectBase =" + s1);
        LogManager.log("RunMonitor", "        URLMonitor.toString() match =" + s2);
        LogManager.log("RunMonitor", "        URLMonitor.toString() errorContent=" + s3);
        LogManager.log("RunMonitor", "        URLMonitor.toString() proxy =" + s4);
        LogManager.log("RunMonitor", "        URLMonitor.toString() proxyUserName =" + s5);
        LogManager.log("RunMonitor", "        URLMonitor.toString() proxyPassword=" + s6);
        if(array != null)
        {
            for(int j = 0; j < array.size(); j++)
            {
                Object obj = array.at(j);
                if(obj instanceof String)
                {
                    LogManager.log("RunMonitor", "        URLMonitor.toString() jgl.Array postData(" + j + ")=" + (String)obj);
                }
            }

        }
        LogManager.log("RunMonitor", "        URLMonitor.toString() userName =" + s7);
        LogManager.log("RunMonitor", "        URLMonitor.toString() password =" + s8);
        LogManager.log("RunMonitor", "        URLMonitor.toString() contentBuffer =" + stringbuffer);
        LogManager.log("RunMonitor", "        URLMonitor.toString() contentMax =" + l);
        LogManager.log("RunMonitor", "        URLMonitor.toString() retries =" + i);
        LogManager.log("RunMonitor", "        URLMonitor.toString() timedOut =" + l1);
        LogManager.log("RunMonitor", "        URLMonitor.toString() redirectBuffer =" + stringbuffer1.toString());
        if(stringbuffer2 != null)
        {
            LogManager.log("RunMonitor", "        URLMonitor.toString() sessionBuffer=" + stringbuffer2.toString());
        }
        LogManager.log("RunMonitor", "        URLMonitor.toString() LEAVING LEAVING LEAVING");
    }

    public static String getURLContentMatchMaxTruncateError(long l)
    {
        return "SITEVIEW HTML page truncated by _urlContentMatchMax=" + l + " defined in groups/master.config file. This may cause content match errors or missing links, forms and other HTML objects.";
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.StandardMonitor.URLMonitor.class).desiredAssertionStatus();
        millisecondPrecision = 2;
        debugURL = 0;
        HashMap hashmap = MasterConfig.getMasterConfig();
        int i = 1;
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the URL to be verified.  If the URL starts with https://, then a secure connection will be made using SSL. (example: http://demo." + Platform.exampleDomain + ")");
        pURL.setParameterOptions(true, i++, false);
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional, match against content of URL, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
        pContentMatch.setParameterOptions(true, i++, false);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, i++, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional list of proxy servers to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, i++, true);
        pImages = new BooleanProperty("_getImages", "");
        pImages.setDisplayText("Retrieve Images", "when selected, all of the graphics externally referenced in the page will also be retrieved and calculated in the total response time");
        pImages.setParameterOptions(true, i++, true);
        pFrames = new BooleanProperty("_getFrames", "");
        pFrames.setDisplayText("Retrieve Frames", "when selected, all of the URLs referenced by frames in a frameset will be retrieved and calculated in the total response time");
        pFrames.setParameterOptions(true, i++, true);
        pErrorContent = new StringProperty("_errorContent");
        pErrorContent.setDisplayText("Error If Match", "optionally generate an error if the content of the URL contains this text");
        pErrorContent.setParameterOptions(true, i++, true);
        pCheckContent = new ScalarProperty("_checkContent", "");
        pCheckContent.setDisplayText("Check for Content Changes", "generate error if the content of the URL changes - resetting the saved contents updates the contents checked against during the next monitor run");
        pCheckContent.setParameterOptions(true, i++, true);
        pCheckContentResetTime = new StringProperty("_checkContentResetTime", "");
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization.              See the <a href=\"/SiteView/docs/URLMon.htm#authorization\" TARGET=Help>               documentation</a> for more information.                           </p>");
        pUserName.setParameterOptions(true, i++, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
        pPassword.setParameterOptions(true, i++, true);
        pPassword.isPassword = true;
        pDomain = new StringProperty("_domain");
        pDomain.setDisplayText("Authorization NTLM Domain", "optional domain if the URL requires for NTLM authorization");
        pDomain.setParameterOptions(true, i++, true);
        pWhenToAuthenticate = new ScalarProperty("_whenToAuthenticate", "");
        pWhenToAuthenticate.setDisplayText("Preemptive Authorization", "              See the <a href=\"/SiteView/docs/URLMon.htm#authorization\" TARGET=Help>               documentation</a> for more information.                           </p>");
        pWhenToAuthenticate.setParameterOptions(true, i++, true);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, i++, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, i++, true);
        pProxyPassword.isPassword = true;
        pPostData = new StringProperty("_postData", "", "POST Variables");
        pPostData.setDisplayText("POST Data", "optional name=value variables, one per line, to send with a POST request");
        pPostData.setParameterOptions(true, i++, true);
        pPostData.isMultiLine = true;
        pErrorOnRedirect = new BooleanProperty("_errorOnRedirect", "");
        pErrorOnRedirect.setDisplayText("Error If Redirected", "when selected, generate an error if the URL is redirected");
        pErrorOnRedirect.setParameterOptions(true, i++, true);
        pMeasureDetails = new BooleanProperty("_measureDetails", "");
        pMeasureDetails.setDisplayText("Show Detailed Measurements", "when selected, detailed measurement times are displayed for DNS lookup, connecting, server response, and downloading.");
        pMeasureDetails.setParameterOptions(true, i++, true);
        pURLEncoding = new StringProperty("_URLEncoding");
        pURLEncoding.setDisplayText("Encoding Character Set", "Enter code page (ie Cp1252 or Shift_JIS or EUC-JP)");
        pURLEncoding.setParameterOptions(true, i++, true);
        pMonitorRunCount = new NumericProperty("_monitorRunCount", "0");
        pMonitorRunCount.setDisplayText("Baseline Interval", "The number of monitor runs to be averaged for a <a href=\"/SiteView/docs/RollingBaseline.htm\" target=HELP>Rolling Baseline</a>.");
        pMonitorRunCount.setParameterOptions(true, i++, true);
        pHTTPVersion10 = new BooleanProperty("_HTTPVersion10", "");
        pHTTPVersion10.setDisplayText("HTTP Version", "when unselected, use HTTP Version 1.1 in the request header; when selected, use 1.0");
        pHTTPVersion10.setParameterOptions(true, i++, true);
        pRetries = new NumericProperty("_retries", "0", "Retries");
        pRetries.setDisplayText("Retries", "The number of times (0-10) to retry the request on recoverable errors, if monitor times out retries are cut short.");
        pRetries.setParameterOptions(true, i++, true);
        pAcceptAllUntrustedCerts = new BooleanProperty("_sslAcceptAllUntrustedCerts", "");
        pAcceptAllUntrustedCerts.setDisplayText("Accept Untrusted Certs for HTTPS", "Accept certificates that are untrusted in the cert chain.");
        pAcceptAllUntrustedCerts.setParameterOptions(true, i++, true);
        pAcceptInvalidCerts = new BooleanProperty("_sslAcceptInvalidCerts", "");
        pAcceptInvalidCerts.setDisplayText("Accept Invalid Certs for HTTPS", "Accept certificates even if todays date in not in the date ranges in the cert chain.");
        pAcceptInvalidCerts.setParameterOptions(true, i++, true);
        pEncodePostData = new ScalarProperty("_URLDropDownEncodePostData", "");
        pEncodePostData.setDisplayText("When to Encode Post Data", "By default if Content-Type: urlencoded found, then encode, otherwise force according to the selected option.");
        pEncodePostData.setParameterOptions(true, i++, true);
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
        StringProperty astringproperty[] = {
            pPercentDeviationDNS, pPercentDeviationDNL, pPercentDeviationCNT, pPercentDeviationRTT, pPercentDeviationRSP, pRollingBaseValuesRTT, pRollingBaseValuesDNS, pRollingBaseValuesCNT, pRollingBaseValuesRSP, pRollingBaseValuesDNL, 
            pMonitorRunCount, pURL, pTimeout, pContentMatch, pErrorContent, pProxy, pProxyUserName, pProxyPassword, pPostData, pCheckContent, 
            pErrorOnRedirect, pMeasureDetails, pHTTPVersion10, pRetries, pEncodePostData, pUserName, pPassword, pDomain, pWhenToAuthenticate, pImages, 
            pFrames, pRoundTripTime, pSize, pAge, pStatusText, pStatus, pOverallStatus, pTotalErrors, pLastChecksum, pURLHeader, 
            pFrameErrorList, pImageErrorList, pDNSTime, pConnectTime, pResponseTime, pDownloadTime, pMatchValue, pLastCheckContentTime, pCheckContentResetTime, pHost, 
            pDaysUntilCertExpiration, pAcceptAllUntrustedCerts, pAcceptInvalidCerts, pURLEncoding
        };
        addProperties("COM.dragonflow.StandardMonitor.URLMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.URLMonitor", Rule.stringToClassifier("status == -1002\tnodata"));
        addClassElement("COM.dragonflow.StandardMonitor.URLMonitor", Rule.stringToClassifier("status != 200\terror\tstatus != 2xx"));
        addClassElement("COM.dragonflow.StandardMonitor.URLMonitor", Rule.stringToClassifier("totalErrors > 0\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.URLMonitor", Rule.stringToClassifier("status == 200\tgood\tstatus == 2xx"));
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "description", "Verifies that web pages (HTTP or HTTPS) can be retrieved.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "help", "URLMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "title", "URL");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "class", "URLMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "toolName", "Get URL");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "toolDescription", "Requests a URL from a server and prints the returned data.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "topazName", "URL Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLMonitor", "topazType", "Web Application Server");
        String s = System.getProperty("URLMonitor.debug");
        if(s != null)
        {
            debugURL = TextUtils.toInt(s);
            System.out.println("debugURL=" + debugURL);
        }
        millisecondPrecision = TextUtils.toInt(TextUtils.getValue(hashmap, "_defaultMillisecondPrecision"));
        if(millisecondPrecision == 0)
        {
            millisecondPrecision = 2;
        }
    }
}
