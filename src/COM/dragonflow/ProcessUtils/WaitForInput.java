/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.ProcessUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.ProcessUtils:
// WaitForInputEvents
public class WaitForInput implements java.lang.Runnable {

    public static final int STATUS_RECEIVED = 0;

    public static final int STATUS_TIMEOUT = 1;

    public static final int STATUS_ERROR = 2;

    private java.io.InputStream in;

    String waitString;

    String timeoutString;

    COM.dragonflow.ProcessUtils.WaitForInputEvents events;

    public WaitForInput(java.io.InputStream inputstream, String s, String s1, COM.dragonflow.ProcessUtils.WaitForInputEvents waitforinputevents) {
        waitString = "";
        timeoutString = "";
        events = null;
        in = inputstream;
        waitString = s;
        timeoutString = s1;
        events = waitforinputevents;
    }

    public void run() {
        java.io.BufferedReader bufferedreader;
        try {
            bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(in);
        } catch (java.io.IOException ioexception) {
            COM.dragonflow.Log.LogManager.log("Error", "WaitForInput: Failed to get the stdin, exception: " + ioexception.getMessage());
            events.onInputReceived(2);
            return;
        }
        String s;
        do {
            try {
                s = bufferedreader.readLine();
            } catch (java.io.IOException ioexception1) {
                COM.dragonflow.Log.LogManager.log("Error", "WaitForInput: Failed to read line, exception: " + ioexception1.getMessage());
                events.onInputReceived(2);
                return;
            }
            if (s == null) {
                events.onInputReceived(2);
                return;
            }
            if (s.equals(waitString)) {
                events.onInputReceived(0);
                return;
            }
        } while (!s.equals(timeoutString));
        events.onInputReceived(1);
    }
}
