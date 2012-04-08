/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.SSH;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.SSH:
// ISSHCloseable
public class SSHCloser implements java.lang.Runnable {

    private COM.dragonflow.SSH.ISSHCloseable close;

    SSHCloser(COM.dragonflow.SSH.ISSHCloseable isshcloseable) {
        close = isshcloseable;
    }

    public void run() {
        close.close();
    }
}
