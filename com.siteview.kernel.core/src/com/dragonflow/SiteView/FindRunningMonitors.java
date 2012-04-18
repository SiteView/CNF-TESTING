/*
 * 
 * Created on 2005-2-16 15:11:54
 *
 * FindRunningMonitors.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>FindRunningMonitors</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.dragonflow.Properties.FindObjectsOfClassVisitor;
import com.dragonflow.Properties.PropertiedObject;

// Referenced classes of package com.dragonflow.SiteView:
// AtomicMonitor

public class FindRunningMonitors extends FindObjectsOfClassVisitor {

    public FindRunningMonitors() {
        super("com.dragonflow.SiteView.AtomicMonitor");
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