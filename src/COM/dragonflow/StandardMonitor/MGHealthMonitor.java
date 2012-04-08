/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MGHealthMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MGHealthMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import COM.dragonflow.Health.MgHealth;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Rule;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// HealthMonitorBase

public class MGHealthMonitor extends HealthMonitorBase {

    public MGHealthMonitor() {
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        try {
            setStatus(new MgHealth());
            return true;
        } catch (Exception e) {
            setProperty(pStateString, e.getMessage());
            return false;
        }
    }

    public static void main(String args[]) {
        MGHealthMonitor mghealthmonitor = new MGHealthMonitor();
        mghealthmonitor.update();
        System.out.println("health: " + mghealthmonitor.getProperty(HealthMonitorBase.pNumErrors));
        System.exit(0);
    }

    static {
        String s = (COM.dragonflow.StandardMonitor.MGHealthMonitor.class).getName();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties(s, astringproperty);
        setClassProperty(s, "class", "MGHealthMonitor");
        setClassProperty(s, "description", "Monitors health of monitor group configuration files");
        setClassProperty(s, "title", "MG Health Monitor");
        setClassProperty(s, "help", "MGHealthMonitor.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "MG Health Monitor");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "addable", "false");
        addClassElement(s, Rule.stringToClassifier("numErrors > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
    }
}
