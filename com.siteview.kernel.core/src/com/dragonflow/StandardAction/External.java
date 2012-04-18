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

public class External extends com.dragonflow.SiteView.Action {

    public External() {
    }

    public boolean execute() {
        return true;
    }

    static {
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[0];
        com.dragonflow.StandardAction.External.addProperties("com.dragonflow.StandardAction.External", astringproperty);
        com.dragonflow.StandardAction.External.setClassProperty("com.dragonflow.StandardAction.External", "name", "External");
        com.dragonflow.StandardAction.External.setClassProperty("com.dragonflow.StandardAction.External", "class", "External");
        com.dragonflow.StandardAction.External.setClassProperty("com.dragonflow.StandardAction.External", "loadable", "false");
    }
}
