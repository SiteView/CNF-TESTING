/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Health:
// logEventHealth
public class HealthLogHandler extends java.util.logging.FileHandler {

    public HealthLogHandler() throws java.io.IOException, java.lang.SecurityException {
    }

    public synchronized void publish(java.util.logging.LogRecord logrecord) {
        if (logrecord.getLevel() == java.util.logging.Level.SEVERE) {
            String s = logrecord.getMessage();
//            com.dragonflow.Health.logEventHealth.log(com.dragonflow.SiteView.TopazInfo.getTopazName() + " SEVERE: " + s);
        }
    }
}
