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

// Referenced classes of package com.dragonflow.ApacheHttpClientUtils:
// ApacheGetMethod
public class ApacheFTPMethod extends com.dragonflow.ApacheHttpClientUtils.ApacheGetMethod {

    public ApacheFTPMethod(String s) {
        super(s);
    }

    public void writeRequestLine(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection) throws java.io.IOException, org.apache.commons.httpclient.HttpException {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getName());
        stringbuffer.append(" ");
        if (!httpconnection.isTransparent()) {
            stringbuffer.append("ftp");
            stringbuffer.append("://");
            stringbuffer.append(httpconnection.getHost());
            org.apache.commons.httpclient.protocol.Protocol protocol = httpconnection.getProtocol();
            if (httpconnection.getPort() != -1 && httpconnection.getPort() != protocol.getDefaultPort()) {
                stringbuffer.append(":");
                stringbuffer.append(httpconnection.getPort());
            }
        }
        if (getPath() == null) {
            stringbuffer.append("/");
        } else {
            if (!httpconnection.isTransparent() && !getPath().startsWith("/")) {
                stringbuffer.append("/");
            }
            stringbuffer.append(getPath());
        }
        if (getQueryString() != null) {
            if (getQueryString().indexOf("?") != 0) {
                stringbuffer.append("?");
            }
            stringbuffer.append(getQueryString());
        }
        stringbuffer.append(" ");
        stringbuffer.append(isHttp11() ? "HTTP/1.1" : "HTTP/1.0");
        stringbuffer.append("\r\n");
        httpconnection.print(stringbuffer.toString());
    }
}
