/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.ApacheHttpClientUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import com.sun.net.ssl.internal.ssl.Provider;

// Referenced classes of package COM.dragonflow.ApacheHttpClientUtils:
// ApacheHttpMethod, ApachePostMethod, ICommonMethod, ApacheFTPMethod,
// ApacheGetMethod, UntrustedCertSSLSocketFactory,
// ApacheDefaultProtocolSocketFactory, ApacheSSLProtocolSocketFactory,
// HTTPRequestSettings

public class ApacheHttpUtils {

    static boolean debug;

    static final boolean $assertionsDisabled; /* synthetic field */

    public ApacheHttpUtils() {
    }

    protected static java.security.KeyStore loadKeystore(String s, String s1) {
        java.security.KeyStore keystore = null;
        try {
            keystore = java.security.KeyStore.getInstance("JKS");
            java.io.FileInputStream fileinputstream = new FileInputStream(s);
            keystore.load(fileinputstream, s1.toCharArray());
        } catch (java.lang.Exception exception) {
            keystore = null;
            COM.dragonflow.Log.LogManager.log("Error", "Failed to load keystore '" + s + "', exception: " + exception.getMessage());
        }
        return keystore;
    }

    public static COM.dragonflow.ApacheHttpClientUtils.ApacheHttpMethod postBinaryData(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings, java.io.InputStream inputstream, StringBuffer stringbuffer) {
        org.apache.commons.httpclient.methods.PostMethod postmethod = new PostMethod(httprequestsettings.getUrl());
        try {
            postmethod.setRequestBody(inputstream);
            postmethod.setRequestContentLength(-2);
            COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.processRequest(postmethod, httprequestsettings, stringbuffer);
        } catch (java.lang.Exception exception) {
            stringbuffer.append(" ApacheHttpUtils failed to post binary data to: " + httprequestsettings.getUrl() + ", exception: " + exception.getMessage());
        }
        return new ApacheHttpMethod(postmethod);
    }

    public static COM.dragonflow.ApacheHttpClientUtils.ApacheHttpMethod postPairs(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings, java.util.Vector vector, StringBuffer stringbuffer) {
        COM.dragonflow.ApacheHttpClientUtils.ApachePostMethod apachepostmethod = new ApachePostMethod(httprequestsettings.getUrl());
        ((COM.dragonflow.ApacheHttpClientUtils.ApachePostMethod) apachepostmethod).setEncodePostData(httprequestsettings.getEncodePostData());
        try {
            COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.reformatPostData(vector, apachepostmethod);
            COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.processRequest(apachepostmethod, httprequestsettings, stringbuffer);
        } catch (java.lang.Exception exception) {
            stringbuffer.append(" ApacheHttpUtils failed to post data to: " + httprequestsettings.getUrl() + ", exception: " + exception.getMessage());
        }
        return new ApacheHttpMethod(apachepostmethod);
    }

    private static void reformatPostData(java.util.Vector vector, org.apache.commons.httpclient.methods.PostMethod postmethod) {
        String s = "";
        java.util.Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            java.lang.Object obj = iterator.next();
            if (obj instanceof String) {
                s = s + obj;
                iterator.remove();
            }
        }
        if (s.length() > 0) {
            postmethod.setRequestBody(s);
        }
        if (vector.size() > 0) {
            org.apache.commons.httpclient.NameValuePair anamevaluepair[] = new org.apache.commons.httpclient.NameValuePair[vector.size()];
            vector.copyInto(anamevaluepair);
            postmethod.setRequestBody(anamevaluepair);
        }
    }

    /**
     * 
     * 
     * @param httpmethodbase
     * @param httprequestsettings
     * @param stringbuffer
     * @return
     */
    private static boolean processRequest(org.apache.commons.httpclient.HttpMethodBase httpmethodbase, COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings, StringBuffer stringbuffer) {
        boolean flag;
        httpmethodbase.setHostConfiguration(COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getHostConfiguration(httprequestsettings));
        flag = false;
        httpmethodbase.setHttp11(httprequestsettings.getHttp11());

        try {
            org.apache.commons.httpclient.HttpClient httpclient = new HttpClient();
            httpclient.getState().setCookiePolicy(0);
            httpclient.getState().addCookies(httprequestsettings.getCookies());
            httpclient.setTimeout(httprequestsettings.getRequestTimeoutMS());
            httpclient.setConnectionTimeout(httprequestsettings.getConnectionTimeoutMS());
            if (httprequestsettings.getAuthUserName() != null && httprequestsettings.getAuthUserName().length() > 0) {
                httpclient.getState().setCredentials(null, httprequestsettings.getHost(),
                        new NTCredentials(httprequestsettings.getAuthUserName(), httprequestsettings.getAuthPassword(), httprequestsettings.getHost(), httprequestsettings.getAuthNTLMDomain()));
                httpmethodbase.setDoAuthentication(true);
                java.lang.Boolean boolean1 = httprequestsettings.getAuthenticationOnFirstRequest();
                httpclient.getState().setAuthenticationPreemptive(boolean1 != null ? httprequestsettings.getAuthenticationOnFirstRequest().booleanValue() : true);
            }
            if (httprequestsettings.getProxy() != null && httprequestsettings.getProxy().length() > 0) {
                httpclient.getHostConfiguration().setProxy(httprequestsettings.getProxyHost(), httprequestsettings.getProxyPort());
                if (httprequestsettings.getProxyUserName() != null && httprequestsettings.getProxyUserName().length() > 0) {
                    httpclient.getState().setProxyCredentials(null, httprequestsettings.getProxyHost(),
                            new NTCredentials(httprequestsettings.getProxyUserName(), httprequestsettings.getProxyPassword(), httprequestsettings.getProxyHost(), httprequestsettings.getProxyAuthDomain()));
                    httpmethodbase.setDoAuthentication(true);
                    java.lang.Boolean boolean2 = httprequestsettings.getAuthenticationOnFirstRequest();
                    httpclient.getState().setAuthenticationPreemptive(boolean2 != null ? httprequestsettings.getAuthenticationOnFirstRequest().booleanValue() : true);
                }
            }
            httpclient.setStrictMode(true);
            httpmethodbase.setFollowRedirects(false);
            if (httprequestsettings.getHeaders() != null) {
                for (int i = 0; i < httprequestsettings.getHeaders().size(); i ++) {
                    httpmethodbase.setRequestHeader((org.apache.commons.httpclient.Header) httprequestsettings.getHeaders().get(i));
                }

            }
            int j = -1;
            for (int k = 0; j == -1 && k <= httprequestsettings.getRetries(); k ++) {
                stringbuffer.setLength(0);
                try {
                    if (httprequestsettings.getProxyHost() != null && httprequestsettings.getScheme().equalsIgnoreCase("https") && (httpmethodbase instanceof COM.dragonflow.ApacheHttpClientUtils.ICommonMethod)) {
                        ((COM.dragonflow.ApacheHttpClientUtils.ICommonMethod) httpmethodbase).setConnectStartTime(java.lang.System.currentTimeMillis());
                    }
                    j = httpclient.executeMethod(httpmethodbase);
                } catch (org.apache.commons.httpclient.HttpRecoverableException httprecoverableexception) {
                    stringbuffer.append(COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getFormattedErrorInfo(httprequestsettings, httprecoverableexception, k));
                } catch (java.io.IOException ioexception) {
                    stringbuffer.append(COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getFormattedErrorInfo(httprequestsettings, ioexception, k));
                } catch (java.lang.Exception exception1) {
                    stringbuffer.append(COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getFormattedErrorInfo(httprequestsettings, exception1, k));
                }
            }

            if (j != -1) {
                httpmethodbase.getResponseBody();
                if (httpmethodbase.getStatusCode() == 200) {
                    flag = true;
                } else {
                    org.apache.commons.httpclient.StatusLine statusline = httpmethodbase.getStatusLine();
                    stringbuffer.append(" ApacheHttpUtils: Request failed. URL: " + httprequestsettings.getUrl() + ", status code: " + httpmethodbase.getStatusCode() + (statusline == null ? "" : ", status string: " + statusline.toString()));
                }
            }
            httpmethodbase.releaseConnection();
        } catch (java.lang.Exception exception) {
            stringbuffer.append(" ApacheHttpUtils: Failed to process request");
            httpmethodbase.releaseConnection();
        }
        return flag;
    }

    public static COM.dragonflow.ApacheHttpClientUtils.ApacheHttpMethod getRequest(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings, StringBuffer stringbuffer) {
        java.lang.Object obj = null;
        if (httprequestsettings.getUrl().startsWith("ftp")) {
            httprequestsettings.setUrl(httprequestsettings.getUrl().replaceFirst("ftp", "http"));
            obj = new ApacheFTPMethod(httprequestsettings.getUrl());
        } else {
            obj = new ApacheGetMethod(httprequestsettings.getUrl());
        }
        COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.processRequest(((org.apache.commons.httpclient.HttpMethodBase) (obj)), httprequestsettings, stringbuffer);
        return new ApacheHttpMethod(((org.apache.commons.httpclient.HttpMethodBase) (obj)));
    }

    public static org.apache.commons.httpclient.HostConfiguration getHostConfiguration(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings) {
        org.apache.commons.httpclient.HostConfiguration hostconfiguration = new HostConfiguration();
        boolean flag = httprequestsettings.getAcceptAllUntrustedCerts().booleanValue();
        boolean flag1 = httprequestsettings.getAcceptInvalidCerts().booleanValue();
        java.security.KeyStore keystore = null;
        if (httprequestsettings.getCertFilename() != null && httprequestsettings.getCertFilename().length() > 0) {
            keystore = COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.loadKeystore(httprequestsettings.getCertFilename(), httprequestsettings.getCertPassword());
        }
        org.apache.commons.httpclient.protocol.Protocol protocol = null;
        if (httprequestsettings.getScheme().equalsIgnoreCase("https") && (flag || flag1 || keystore != null)) {
            protocol = new Protocol("https", new UntrustedCertSSLSocketFactory(keystore, flag, flag1), COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.URLUsingPortAndNoProxy(httprequestsettings) ? httprequestsettings.getPort() : 443);
        } else if (httprequestsettings.getScheme().equalsIgnoreCase("http")) {
            protocol = new Protocol("http", new ApacheDefaultProtocolSocketFactory(), COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.URLUsingPortAndNoProxy(httprequestsettings) ? httprequestsettings.getPort() : 80);
        } else {
            protocol = new Protocol("https", new ApacheSSLProtocolSocketFactory(), COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.URLUsingPortAndNoProxy(httprequestsettings) ? httprequestsettings.getPort() : 443);
        }
        if (!$assertionsDisabled && protocol == null) {
            throw new AssertionError("Failed to create a socketFactory for: " + httprequestsettings.getUrl());
        } else {
            hostconfiguration.setHost(httprequestsettings.getHost() != null ? httprequestsettings.getHost() : "", COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.URLUsingPortAndNoProxy(httprequestsettings) ? -1 : httprequestsettings.getPort(),
                    protocol);
            return hostconfiguration;
        }
    }

    public static boolean URLUsingPortAndNoProxy(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings) {
        return httprequestsettings.getPort() > 0 && (httprequestsettings.getProxyHost() == null || httprequestsettings.getProxyHost() == "");
    }

    public static long getDaysUntilCertExpires(java.net.Socket socket) throws java.net.UnknownHostException {
        long l = 0L;
        if (socket != null && (socket instanceof javax.net.ssl.SSLSocket)) {
            try {
                if (!socket.isClosed()) {
                    javax.net.ssl.SSLSession sslsession = ((javax.net.ssl.SSLSocket) socket).getSession();
                    javax.security.cert.X509Certificate ax509certificate[] = sslsession.getPeerCertificateChain();
                    int i = 0;
                    for (int j = 0; j < ax509certificate.length; j ++) {
                        javax.security.cert.X509Certificate x509certificate = ax509certificate[j];
                        long l1 = java.lang.System.currentTimeMillis();
                        long l2 = l1 + 0x5265c00L;
                        int k = 0;
                        do {
                            java.util.Date date = new Date(l2);
                            try {
                                x509certificate.checkValidity(date);
                            } catch (javax.security.cert.CertificateExpiredException certificateexpiredexception) {
                                if (k == 0) {
                                    k ++;
                                }
                                break;
                            } catch (javax.security.cert.CertificateNotYetValidException certificatenotyetvalidexception) {
                                break;
                            }
                            k ++;
                            l2 += 0x5265c00L;
                        } while (true);
                        if (k <= 0) {
                            continue;
                        }
                        if (i == 0) {
                            i = k;
                            continue;
                        }
                        if (i > k) {
                            i = k;
                        }
                    }

                    if (i > 1) {
                        l = i;
                    }
                    if (i == 1) {
                        l = 0L;
                    }
                }
            } catch (javax.net.ssl.SSLPeerUnverifiedException sslpeerunverifiedexception) {
                String s = " Peer certificate: " + sslpeerunverifiedexception.getMessage() + " isSocketClosed? " + socket.isClosed();
                COM.dragonflow.Log.LogManager.log("Error", s);
                throw new UnknownHostException(s);
            }
        }
        return l;
    }

    public static String getFormattedErrorInfo(COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings, java.lang.Exception exception, int i) {
        String s = null != httprequestsettings.getProxyHost() ? httprequestsettings.getProxyHost() : httprequestsettings.getHost();
        int j = null != httprequestsettings.getProxyHost() ? httprequestsettings.getProxyPort() : httprequestsettings.getPort();
        boolean flag = httprequestsettings.getScheme().equalsIgnoreCase("https") && null == httprequestsettings.getProxyHost();
        boolean flag1 = null != httprequestsettings.getProxyHost();
        return " URL: " + httprequestsettings.getUrl() + ", host: " + s + ", port: " + j + ", UsingProxy: " + flag1 + ", isHTTPS(SSL): " + flag + ", " + exception.toString() + ", currentRetry: " + i;
    }

    static {
        $assertionsDisabled = !(COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.class).desiredAssertionStatus();
        debug = false;
        String s = java.lang.System.getProperty("ApacheHttpUtils.debug");
        if (s != null && s.length() > 0) {
            debug = true;
        }
        if (debug) {
            java.lang.System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            java.lang.System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            java.lang.System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
            java.lang.System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "trace");
        }
        java.security.Security.addProvider(new Provider());
        java.lang.System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
    }
}
