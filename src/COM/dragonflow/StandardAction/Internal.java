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

public class Internal extends COM.dragonflow.SiteView.Action {

    public Internal() {
    }

    public boolean execute() {
        return true;
    }

    static {
        COM.dragonflow.Properties.StringProperty astringproperty[] = new COM.dragonflow.Properties.StringProperty[0];
        COM.dragonflow.StandardAction.Internal.addProperties("COM.dragonflow.StandardAction.Internal", astringproperty);
        COM.dragonflow.StandardAction.Internal.setClassProperty("COM.dragonflow.StandardAction.Internal", "name", "Internal");
        COM.dragonflow.StandardAction.Internal.setClassProperty("COM.dragonflow.StandardAction.Internal", "class", "Internal");
        COM.dragonflow.StandardAction.Internal.setClassProperty("COM.dragonflow.StandardAction.Internal", "loadable", "false");
    }
}
