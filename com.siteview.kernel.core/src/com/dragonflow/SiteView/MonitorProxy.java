/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MonitorProxy.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>MonitorProxy</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.dragonflow.Properties.StringProperty;

// Referenced classes of package com.dragonflow.SiteView:
// Monitor

public class MonitorProxy extends Monitor {

    public MonitorProxy() {
    }

    public String getHostname() {
        return "ProxyHost";
    }

    static {
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("com.dragonflow.SiteView.MonitorProxy", astringproperty);
    }
}
