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

public class External extends COM.dragonflow.SiteView.Action {

    public External() {
    }

    public boolean execute() {
        return true;
    }

    static {
        COM.dragonflow.Properties.StringProperty astringproperty[] = new COM.dragonflow.Properties.StringProperty[0];
        COM.dragonflow.StandardAction.External.addProperties("COM.dragonflow.StandardAction.External", astringproperty);
        COM.dragonflow.StandardAction.External.setClassProperty("COM.dragonflow.StandardAction.External", "name", "External");
        COM.dragonflow.StandardAction.External.setClassProperty("COM.dragonflow.StandardAction.External", "class", "External");
        COM.dragonflow.StandardAction.External.setClassProperty("COM.dragonflow.StandardAction.External", "loadable", "false");
    }
}
