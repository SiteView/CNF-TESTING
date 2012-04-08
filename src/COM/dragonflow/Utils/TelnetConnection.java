/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

class TelnetConnection {

    java.net.Socket socket;

    java.io.BufferedInputStream in;

    java.io.PrintWriter out;

    COM.dragonflow.SiteView.Machine machine;

    TelnetConnection() {
        socket = null;
        in = null;
        out = null;
        machine = null;
    }
}
