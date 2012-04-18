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

public interface ICommonMethod {

    public abstract long getResponseDuration();

    public abstract long getDownloadDuration();

    public abstract long getConnectDuration();

    public abstract void setConnectStartTime(long l);

    public abstract String getRequestString(boolean flag);

    public abstract long getDNSDuration();

    public abstract long getDaysUntilCertExpires();

    public abstract int getResponseContentLength();
}
