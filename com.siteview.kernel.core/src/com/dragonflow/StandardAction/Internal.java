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

public class Internal extends com.dragonflow.SiteView.Action {

    public Internal() {
    }

    public boolean execute() {
        return true;
    }

    static {
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[0];
        com.dragonflow.StandardAction.Internal.addProperties("com.dragonflow.StandardAction.Internal", astringproperty);
        com.dragonflow.StandardAction.Internal.setClassProperty("com.dragonflow.StandardAction.Internal", "name", "Internal");
        com.dragonflow.StandardAction.Internal.setClassProperty("com.dragonflow.StandardAction.Internal", "class", "Internal");
        com.dragonflow.StandardAction.Internal.setClassProperty("com.dragonflow.StandardAction.Internal", "loadable", "false");
    }
}
