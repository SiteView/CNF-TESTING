/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class SetProperty extends COM.dragonflow.SiteView.Action {

    public SetProperty() {
    }

    public boolean execute() {
        if (args.length == 2) {
            monitor.setProperty(args[0], args[1]);
        } else {
            java.lang.System.err.println("Improper number of arguments to SetProperty");
            java.lang.System.err.println("SetProperty argc: " + args.length);
            for (int i = 0; i < args.length; i ++) {
                java.lang.System.err.println("SetProperty arg: " + args[i]);
            }

            return false;
        }
        return true;
    }

    public String toString() {
        return "setProperty on monitor: " + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName);
    }

    static {
        COM.dragonflow.Properties.StringProperty astringproperty[] = new COM.dragonflow.Properties.StringProperty[0];
        COM.dragonflow.StandardAction.SetProperty.addProperties("COM.dragonflow.StandardAction.SetProperty", astringproperty);
        COM.dragonflow.StandardAction.SetProperty.setClassProperty("COM.dragonflow.StandardAction.SetProperty", "loadable", "false");
    }
}
