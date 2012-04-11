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

// Referenced classes of package COM.dragonflow.ApacheHttpClientUtils:
// ApacheSocketFactoryBase, UntrustedCertTrustManager, IApacheHttpConnectTiming,
// ApacheHttpUtils
public class UntrustedCertSSLSocketFactory extends COM.dragonflow.ApacheHttpClientUtils.ApacheSocketFactoryBase implements org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory, COM.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming {

    private static final org.apache.commons.logging.Log LOG;

    private static java.security.KeyStore keystore = null;

    private static boolean acceptAllUntrusted = false;

    private static boolean acceptInvalid = false;

    private long daysUntilCertExpires;

    public UntrustedCertSSLSocketFactory(java.security.KeyStore keystore1, boolean flag, boolean flag1) {
        daysUntilCertExpires = 0L;
        COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory _tmp = this;
        keystore = keystore1;
        COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory _tmp1 = this;
        acceptAllUntrusted = flag;
        COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory _tmp2 = this;
        acceptInvalid = flag1;
    }

    public long getDaysUntilCertExpires() {
        return daysUntilCertExpires;
    }

    private static javax.net.ssl.SSLSocketFactory getUntrustedCertSSLSocketFactory() {
        javax.net.ssl.SSLContext sslcontext = null;
        try {
            sslcontext = javax.net.ssl.SSLContext.getInstance("SSL");
            sslcontext.init(null, new javax.net.ssl.TrustManager[] { new UntrustedCertTrustManager(keystore, acceptAllUntrusted, acceptInvalid) }, null);
        } catch (java.lang.Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new RuntimeException(exception.toString());
        }
        return sslcontext.getSocketFactory();
    }

    public java.net.Socket createSocket(String s, int i, java.net.InetAddress inetaddress, int j) throws java.io.IOException, java.net.UnknownHostException {
        java.net.InetAddress inetaddress1 = resolveDNS(s);
        java.net.Socket socket = COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory.getUntrustedCertSSLSocketFactory().createSocket(inetaddress1, i, inetaddress, j);
        calculateEndTime();
        daysUntilCertExpires = COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getDaysUntilCertExpires(socket);
        return socket;
    }

    public java.net.Socket createSocket(String s, int i) throws java.io.IOException, java.net.UnknownHostException {
        java.net.InetAddress inetaddress = resolveDNS(s);
        java.net.Socket socket = COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory.getUntrustedCertSSLSocketFactory().createSocket(inetaddress, i);
        calculateEndTime();
        daysUntilCertExpires = COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getDaysUntilCertExpires(socket);
        return socket;
    }

    public java.net.Socket createSocket(java.net.Socket socket, String s, int i, boolean flag) throws java.io.IOException, java.net.UnknownHostException {
        return COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory.getUntrustedCertSSLSocketFactory().createSocket(socket, s, i, flag);
    }

    static {
        LOG = org.apache.commons.logging.LogFactory.getLog(COM.dragonflow.ApacheHttpClientUtils.UntrustedCertSSLSocketFactory.class);
    }
}
