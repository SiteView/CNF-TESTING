/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StatefulMonitor;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.StatefulMonitor:
// StatefulConnection
public interface StatefulConnectionUser {

    public abstract java.lang.Object getUniqueID();

    public abstract java.lang.Object getConnID(java.lang.Class class1);

    public abstract com.dragonflow.StatefulMonitor.StatefulConnection createConnection(java.lang.Class class1, StringBuffer stringbuffer);
}
