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

public class SetCategory extends COM.dragonflow.SiteView.Action {

    public SetCategory() {
    }

    public boolean execute() {
        if (args.length == 2) {
            String s = args[0];
            String s1 = args[1];
            String s2 = monitor.getProperty(s);
            monitor.setProperty(s, s1);
            monitor.IncrementCategoryProperties(s2, s1);
        } else {
            java.lang.System.err.println("Improper number of arguments to SetCategory");
            java.lang.System.err.println("SetCategory argc: " + args.length);
            for (int i = 0; i < args.length; i ++) {
                java.lang.System.err.println("SetCategory arg: " + args[i]);
            }

            return false;
        }
        return true;
    }

    public String toString() {
        return "setCategory on monitor: " + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName);
    }

    static {
        COM.dragonflow.Properties.StringProperty astringproperty[] = new COM.dragonflow.Properties.StringProperty[0];
        COM.dragonflow.StandardAction.SetCategory.addProperties("COM.dragonflow.StandardAction.SetCategory", astringproperty);
        COM.dragonflow.StandardAction.SetCategory.setClassProperty("COM.dragonflow.StandardAction.SetCategory", "loadable", "false");
    }
}
