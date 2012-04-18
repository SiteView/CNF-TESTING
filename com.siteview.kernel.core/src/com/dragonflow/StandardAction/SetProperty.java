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

public class SetProperty extends com.dragonflow.SiteView.Action {

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
        return "setProperty on monitor: " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pName);
    }

    static {
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[0];
        com.dragonflow.StandardAction.SetProperty.addProperties("com.dragonflow.StandardAction.SetProperty", astringproperty);
        com.dragonflow.StandardAction.SetProperty.setClassProperty("com.dragonflow.StandardAction.SetProperty", "loadable", "false");
    }
}
