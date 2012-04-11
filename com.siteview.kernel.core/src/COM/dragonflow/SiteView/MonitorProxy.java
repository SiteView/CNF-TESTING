/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MonitorProxy.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MonitorProxy</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import COM.dragonflow.Properties.StringProperty;

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor

public class MonitorProxy extends Monitor {

    public MonitorProxy() {
    }

    public String getHostname() {
        return "ProxyHost";
    }

    static {
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("COM.dragonflow.SiteView.MonitorProxy", astringproperty);
    }
}
