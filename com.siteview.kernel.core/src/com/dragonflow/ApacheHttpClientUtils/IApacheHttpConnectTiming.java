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

public interface IApacheHttpConnectTiming {

    public abstract long getDNSDuration();

    public abstract long getConnectDuration();

    public abstract long getConnectEnd();

    public abstract long getDaysUntilCertExpires();
}
