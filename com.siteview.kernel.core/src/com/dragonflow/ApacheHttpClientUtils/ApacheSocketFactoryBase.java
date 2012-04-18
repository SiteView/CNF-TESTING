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
// IApacheHttpConnectTiming
public class ApacheSocketFactoryBase implements com.dragonflow.ApacheHttpClientUtils.IApacheHttpConnectTiming {

    private long DNSDuration;

    private long ConnectDuration;

    private long ConnectEnd;

    private long ConnectStartTime;

    public ApacheSocketFactoryBase() {
        DNSDuration = 0L;
        ConnectDuration = 0L;
        ConnectEnd = 0L;
    }

    public long getDNSDuration() {
        return DNSDuration;
    }

    public long getConnectDuration() {
        return ConnectDuration;
    }

    public long getConnectEnd() {
        return ConnectEnd;
    }

    public long getDaysUntilCertExpires() {
        return 0L;
    }

    protected java.net.InetAddress resolveDNS(String s) throws java.net.UnknownHostException {
        long l = java.lang.System.currentTimeMillis();
        com.dragonflow.ApacheHttpClientUtils.ApacheSocketFactoryBase.debugSleepToSeeTime();
        java.net.InetAddress inetaddress = java.net.InetAddress.getByName(s);
        DNSDuration = java.lang.System.currentTimeMillis() - l;
        if (DNSDuration == 0L) {
            DNSDuration = 1L;
        }
        ConnectStartTime = java.lang.System.currentTimeMillis();
        com.dragonflow.ApacheHttpClientUtils.ApacheSocketFactoryBase.debugSleepToSeeTime();
        return inetaddress;
    }

    public static void debugSleepToSeeTime() {
        if (java.lang.System.getProperty("http.proveTimingConnect") != null && java.lang.System.getProperty("http.proveTimingConnect").length() > 0) {
            try {
                java.lang.Thread.sleep((new Double(2010D * java.lang.Math.random())).longValue() + 10L);
            } catch (java.lang.Exception exception) {
            }
        }
    }

    protected void calculateEndTime() {
        ConnectEnd = java.lang.System.currentTimeMillis();
        ConnectDuration = ConnectEnd - ConnectStartTime;
    }
}
