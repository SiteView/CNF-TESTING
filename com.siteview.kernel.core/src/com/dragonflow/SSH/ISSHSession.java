/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.SSH;

import com.recursionsw.jgl.Array;

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
public interface ISSHSession extends com.dragonflow.SSH.ISSHCloseable {

    public abstract boolean connect(com.dragonflow.Utils.RemoteCommandLine remotecommandline, com.dragonflow.SiteView.Machine machine, int i, java.io.PrintWriter printwriter);

    public abstract int execute(com.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, int i, java.io.PrintWriter printwriter, Array array);
}
