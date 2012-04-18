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

import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

// Referenced classes of package com.dragonflow.ApacheHttpClientUtils:
// ApacheSocketFactoryBase, IApacheHttpConnectTiming, ApacheHttpUtils

public class ApacheSSLProtocolSocketFactory extends com.dragonflow.ApacheHttpClientUtils.ApacheSocketFactoryBase implements org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory, com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming {

    private org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory originalSocketFactory;

    private long daysUntilCertExpires;

    public ApacheSSLProtocolSocketFactory() {
        originalSocketFactory = new SSLProtocolSocketFactory();
        daysUntilCertExpires = 0L;
    }

    public long getDaysUntilCertExpires() {
        return daysUntilCertExpires;
    }

    public java.net.Socket createSocket(String s, int i, java.net.InetAddress inetaddress, int j) throws java.io.IOException, java.net.UnknownHostException {
        resolveDNS(s);
        java.net.Socket socket = originalSocketFactory.createSocket(s, i, inetaddress, j);
        calculateEndTime();
        daysUntilCertExpires = com.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getDaysUntilCertExpires(socket);
        return socket;
    }

    public java.net.Socket createSocket(String s, int i) throws java.io.IOException, java.net.UnknownHostException {
        resolveDNS(s);
        java.net.Socket socket = originalSocketFactory.createSocket(s, i);
        calculateEndTime();
        daysUntilCertExpires = com.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getDaysUntilCertExpires(socket);
        return socket;
    }

    public java.net.Socket createSocket(java.net.Socket socket, String s, int i, boolean flag) throws java.io.IOException, java.net.UnknownHostException {
        socket = originalSocketFactory.createSocket(socket, s, i, flag);
        return socket;
    }
}
