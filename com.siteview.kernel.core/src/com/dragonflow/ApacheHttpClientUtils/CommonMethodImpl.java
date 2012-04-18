/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.ApacheHttpClientUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.URI;

// Referenced classes of package com.dragonflow.ApacheHttpClientUtils:
// IApacheHttpConnectTiming, ICommonMethod, ApacheCookieSpec

public class CommonMethodImpl extends org.apache.commons.httpclient.HttpMethodBase implements com.dragonflow.ApacheHttpClientUtils.ICommonMethod {

    private long responseEnd;

    private long downloadDuration;

    private boolean responseEndSet;

    org.apache.commons.httpclient.HttpMethodBase httpMethod;

    private long connectEndTime;

    private long connectStartTime;

    private org.apache.commons.httpclient.HttpConnection conn;

    static final boolean $assertionsDisabled; /* synthetic field */

    public CommonMethodImpl(org.apache.commons.httpclient.HttpMethodBase httpmethodbase) {
        responseEnd = 0L;
        downloadDuration = 0L;
        responseEndSet = false;
        connectEndTime = 0L;
        connectStartTime = 0L;
        conn = null;
        httpMethod = httpmethodbase;
    }

    public String getName() {
        return "";
    }

    public long getResponseDuration() {
        long l = getConnectEnd();
        if (l == 0L) {
            return 0L;
        } else {
            long l1 = responseEnd != 0L ? responseEnd : 0L;
            return l >= l1 ? 0L : l1 - l;
        }
    }

    public long getDownloadDuration() {
        return downloadDuration;
    }

    public long getConnectDuration() {
        long l = 0L;
        if (httpMethod != null) {
            org.apache.commons.httpclient.protocol.ProtocolSocketFactory protocolsocketfactory = httpMethod.getHostConfiguration().getProtocol().getSocketFactory();
            if (protocolsocketfactory instanceof com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) {
                l = ((com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) protocolsocketfactory).getConnectDuration();
            } else if (l == 0L && (httpMethod instanceof com.dragonflow.ApacheHttpClientUtils.ICommonMethod)) {
                if (connectStartTime == 0L || connectEndTime == 0L) {
                    l = 0L;
                } else {
                    l = connectStartTime <= connectEndTime ? connectEndTime - connectStartTime : 0L;
                }
            }
        }
        return l;
    }

    public void setConnectStartTime(long l) {
        connectStartTime = l;
    }

    public int executeTimingInit(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection) throws org.apache.commons.httpclient.HttpException, org.apache.commons.httpclient.HttpRecoverableException,
            java.io.IOException {
        connectEndTime = java.lang.System.currentTimeMillis();
        responseEnd = 0L;
        downloadDuration = 0L;
        responseEndSet = false;
        conn = httpconnection;
        return 1;
    }

    public String getRequestString(boolean flag) {
        httpMethod.setHttp11(flag);
        StringBuffer stringbuffer = new StringBuffer();
        if (conn != null) {
            stringbuffer.append(com.dragonflow.ApacheHttpClientUtils.CommonMethodImpl.generateRequestLine(conn, httpMethod.getName(), httpMethod.getPath(), httpMethod.getQueryString(), httpMethod.isHttp11() ? "HTTP/1.1" : "HTTP/1.0"));
            org.apache.commons.httpclient.Header aheader[] = httpMethod.getRequestHeaders();
            for (int i = 0; i < aheader.length; i ++) {
                stringbuffer.append(aheader[i].toExternalForm());
            }

            if (httpMethod instanceof org.apache.commons.httpclient.methods.EntityEnclosingMethod) {
                try {
                    stringbuffer.append(((org.apache.commons.httpclient.methods.EntityEnclosingMethod) httpMethod).getRequestBodyAsString());
                } catch (java.io.IOException ioexception) {
                    stringbuffer.append("No Post Data pairs: Post Data not available for printing.");
                }
                stringbuffer.append("\r\n");
            }
        } else {
            stringbuffer.append("No Connection: Request not available for printing. The connection may have closed after retrieve");
        }
        return stringbuffer.toString();
    }

    public long getDNSDuration() {
        long l = 0L;
        if (httpMethod != null) {
            org.apache.commons.httpclient.protocol.ProtocolSocketFactory protocolsocketfactory = httpMethod.getHostConfiguration().getProtocol().getSocketFactory();
            if (protocolsocketfactory instanceof com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) {
                l = ((com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) protocolsocketfactory).getDNSDuration();
            }
        }
        return l;
    }

    public long getDaysUntilCertExpires() {
        long l = 0L;
        if (httpMethod != null && httpMethod.getHostConfiguration().getProtocol().getScheme().equalsIgnoreCase("https")) {
            org.apache.commons.httpclient.protocol.ProtocolSocketFactory protocolsocketfactory = httpMethod.getHostConfiguration().getProtocol().getSocketFactory();
            if (protocolsocketfactory instanceof com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) {
                l = ((com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) protocolsocketfactory).getDaysUntilCertExpires();
            }
        }
        return l;
    }

    protected void readStatusLine(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection) throws java.io.IOException, org.apache.commons.httpclient.HttpRecoverableException,
            org.apache.commons.httpclient.HttpException {
        if (!responseEndSet) {
            responseEndSet = true;
            responseEnd = java.lang.System.currentTimeMillis();
        }
    }

    public static void initMethod(org.apache.commons.httpclient.HttpMethodBase httpmethodbase, String s) {
        if (s == null || s.equals("")) {
            s = "/";
        }
        Object obj = null;
        try {
            org.apache.commons.httpclient.URI uri = new URI(s, com.dragonflow.Utils.I18N.getDefaultEncoding());
            httpmethodbase.setPath(uri.getPath() != null ? uri.getPath() : "/");
            httpmethodbase.setQueryString(uri.getQuery());
            if (uri.isAbsoluteURI()) {
                httpmethodbase.setHostConfiguration(new HostConfiguration());
                httpmethodbase.getHostConfiguration().setHost(uri.getHost(), uri.getPort(), uri.getScheme());
            }
        } catch (org.apache.commons.httpclient.URIException uriexception) {
            throw new IllegalArgumentException("Invalid url '" + s + "': " + uriexception.getMessage());
        }
    }

    public void computeDownloadDuration() {
        long l = java.lang.System.currentTimeMillis();
        if (responseEnd == 0L) {
            if (!$assertionsDisabled) {
                throw new AssertionError("HOLD ON THERE BUDY.");
            }
            downloadDuration = 0L;
        } else if (downloadDuration == 0L) {
            if (!$assertionsDisabled && responseEnd <= 0L) {
                throw new AssertionError("we must have a non zero response End time, or the download duration is meaningless.");
            }
            downloadDuration = l - responseEnd;
        }
    }

    private long getConnectEnd() {
        long l = 0L;
        if (httpMethod != null) {
            org.apache.commons.httpclient.protocol.ProtocolSocketFactory protocolsocketfactory = httpMethod.getHostConfiguration().getProtocol().getSocketFactory();
            if (protocolsocketfactory instanceof com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) {
                l = ((com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming) protocolsocketfactory).getConnectEnd();
            } else if (l == 0L && (httpMethod instanceof com.dragonflow.ApacheHttpClientUtils.ICommonMethod)) {
                l = connectEndTime;
            }
        }
        return l;
    }

    public int getResponseContentLength() {
        if (!$assertionsDisabled) {
            throw new AssertionError("Should not be called, use call to any other implementing class with this method.");
        } else {
            return -1;
        }
    }

    public void addCookieRequestHeader(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection, org.apache.commons.httpclient.HeaderGroup headergroup) throws java.io.IOException,
            org.apache.commons.httpclient.HttpException {
        httpMethod.removeRequestHeader("cookie");
        com.dragonflow.ApacheHttpClientUtils.ApacheCookieSpec apachecookiespec = new ApacheCookieSpec();
        org.apache.commons.httpclient.Cookie acookie[] = apachecookiespec.match(httpconnection.getHost(), httpconnection.getPort(), httpMethod.getPath(), httpconnection.isSecure(), httpstate.getCookies());
        if (acookie != null && acookie.length > 0) {
            if (httpMethod.isStrictMode()) {
                headergroup.addHeader(apachecookiespec.formatCookieHeader(acookie));
            } else {
                for (int i = 0; i < acookie.length; i ++) {
                    headergroup.addHeader(apachecookiespec.formatCookieHeader(acookie[i]));
                }

            }
        }
    }

    static {
        $assertionsDisabled = !(com.dragonflow.ApacheHttpClientUtils.CommonMethodImpl.class).desiredAssertionStatus();
    }
}
