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

import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;

// Referenced classes of package COM.dragonflow.ApacheHttpClientUtils:
// ApacheSocketFactoryBase

public class ApacheDefaultProtocolSocketFactory extends COM.dragonflow.ApacheHttpClientUtils.ApacheSocketFactoryBase implements org.apache.commons.httpclient.protocol.ProtocolSocketFactory {

    private org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory originalSocketFactory;

    public ApacheDefaultProtocolSocketFactory() {
        originalSocketFactory = new DefaultProtocolSocketFactory();
    }

    public java.net.Socket createSocket(String s, int i, java.net.InetAddress inetaddress, int j) throws java.io.IOException, java.net.UnknownHostException {
        resolveDNS(s);
        java.net.Socket socket = originalSocketFactory.createSocket(s, i, inetaddress, j);
        calculateEndTime();
        return socket;
    }

    public java.net.Socket createSocket(String s, int i) throws java.io.IOException, java.net.UnknownHostException {
        resolveDNS(s);
        java.net.Socket socket = originalSocketFactory.createSocket(s, i);
        calculateEndTime();
        return socket;
    }
}
