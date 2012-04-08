/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Log:
// Logger
public class ConsoleErrorLogger extends COM.dragonflow.Log.Logger {

    public ConsoleErrorLogger() {
    }

    public void log(String s, java.util.Date date, String s1) {
        java.lang.System.err.println(COM.dragonflow.Log.ConsoleErrorLogger.dateToString(date) + " " + s1);
    }
}
