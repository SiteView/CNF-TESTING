/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class SetCategory extends com.dragonflow.SiteView.Action {

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
        return "setCategory on monitor: " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pName);
    }

    static {
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[0];
        com.dragonflow.StandardAction.SetCategory.addProperties("com.dragonflow.StandardAction.SetCategory", astringproperty);
        com.dragonflow.StandardAction.SetCategory.setClassProperty("com.dragonflow.StandardAction.SetCategory", "loadable", "false");
    }
}
