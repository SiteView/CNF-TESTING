/*
 * 
 * Created on 2005-2-16 16:50:45
 *
 * SimulatedRun.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>SimulatedRun</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import com.dragonflow.Properties.StringProperty;
import com.dragonflow.Utils.CommandLine;

// Referenced classes of package com.dragonflow.SiteView:
// AtomicMonitor

public class SimulatedRun {

    public SimulatedRun() {
    }

    public static void simulateRun(AtomicMonitor atomicmonitor, int i, String s)
            throws Throwable {
        AtomicMonitor _tmp = atomicmonitor;
        atomicmonitor.setProperty(AtomicMonitor.pStateString,
                "Simulated run of monitor " + atomicmonitor.getFullID());
        StringProperty stringproperty;
        for (Enumeration enumeration = atomicmonitor.getStatePropertyObjects(); enumeration
                .hasMoreElements(); stringproperty.setValue("0")) {
            stringproperty = (StringProperty) enumeration.nextElement();
        }

        if (i > 0) {
            Thread.currentThread();
            Thread.sleep(i);
        }
        if (s.length() > 0) {
            (new CommandLine()).exec(s);
        }
    }
}
