/*
 * 
 * Created on 2005-2-16 15:11:54
 *
 * FindRunningMonitors.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>FindRunningMonitors</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import COM.dragonflow.Properties.FindObjectsOfClassVisitor;
import COM.dragonflow.Properties.PropertiedObject;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor

public class FindRunningMonitors extends FindObjectsOfClassVisitor {

    public FindRunningMonitors() {
        super("COM.dragonflow.SiteView.AtomicMonitor");
    }

    protected boolean test(PropertiedObject propertiedobject) {
        if (super.test(propertiedobject)) {
            AtomicMonitor atomicmonitor = (AtomicMonitor) propertiedobject;
            return atomicmonitor.isRunning();
        } else {
            return false;
        }
    }
}