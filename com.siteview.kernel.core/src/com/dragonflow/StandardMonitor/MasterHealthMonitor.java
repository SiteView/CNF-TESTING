/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MasterHealthMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>MasterHealthMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import com.dragonflow.Health.MasterHealth;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.Rule;

// Referenced classes of package com.dragonflow.StandardMonitor:
// HealthMonitorBase

public class MasterHealthMonitor extends HealthMonitorBase {

    public MasterHealthMonitor() {
    }

    /**
     * CAUTION: Drcompiled by hand.
     */
    protected boolean update() {
        try {
            setStatus(new MasterHealth());
            return true;
        } catch (Exception exception) {
            setProperty(pStateString, exception.getMessage());
            return false;
        }
    }

    public static void main(String args[]) {
        MasterHealthMonitor masterhealthmonitor = new MasterHealthMonitor();
        masterhealthmonitor.update();
        System.out.println("health: " + masterhealthmonitor.getProperty(HealthMonitorBase.pNumErrors));
        System.exit(0);
    }

    static {
        String s = (com.dragonflow.StandardMonitor.MasterHealthMonitor.class).getName();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties(s, astringproperty);
        setClassProperty(s, "class", "MasterHealthMonitor");
        setClassProperty(s, "description", "Monitors health of master.config configuration file");
        setClassProperty(s, "title", "Master Health Monitor");
        setClassProperty(s, "help", "MasterHealthMonitor.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Master Health Monitor");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "addable", "false");
        addClassElement(s, Rule.stringToClassifier("numErrors > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
    }
}
