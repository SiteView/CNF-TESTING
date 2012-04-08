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

public class Print extends COM.dragonflow.SiteView.Action {

    public Print() {
        runType = 3;
        maxRuns = 50;
        attemptDelay = 7000L;
    }

    public boolean execute() {
        java.lang.System.out.print("Print: ");
        for (int i = 0; i < args.length; i ++) {
            java.lang.System.out.print(args[i] + " ");
        }

        java.lang.System.out.println(" (" + monitor + ")");
        boolean flag = triggerCount > 35;
        if (flag) {
            messageBuffer.append("Print succeeded");
        } else {
            messageBuffer.append("PRINT FAILED: " + args[0]);
        }
        return flag;
    }
}
