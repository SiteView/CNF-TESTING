/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.SSH;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.SSH:
// ISSHCloseable
public class SSHCloser implements java.lang.Runnable {

    private com.dragonflow.SSH.ISSHCloseable close;

    SSHCloser(com.dragonflow.SSH.ISSHCloseable isshcloseable) {
        close = isshcloseable;
    }

    public void run() {
        close.close();
    }
}
