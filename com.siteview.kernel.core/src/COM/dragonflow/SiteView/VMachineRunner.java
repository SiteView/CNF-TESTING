/*
 * 
 * Created on 2005-2-16 17:38:51
 *
 * VMachineRunner.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>VMachineRunner</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import COM.dragonflow.Log.LogManager;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, VirtualMachine

public class VMachineRunner extends Action {

    VirtualMachine vMachine;

    public VMachineRunner(VirtualMachine virtualmachine) {
        vMachine = null;
        vMachine = virtualmachine;
        runType = 1;
    }

    public boolean execute() {
        LogManager.log("RunMonitor", " checking for new nodes on " + vMachine.getProperty("_host"));
        vMachine.checkForNodes();
        return true;
    }
}
