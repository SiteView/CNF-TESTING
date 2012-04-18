/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.SSH;

import com.recursionsw.jgl.Array;

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
public interface ISSHSession extends COM.dragonflow.SSH.ISSHCloseable {

    public abstract boolean connect(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, COM.dragonflow.SiteView.Machine machine, int i, java.io.PrintWriter printwriter);

    public abstract int execute(COM.dragonflow.Utils.RemoteCommandLine remotecommandline, String s, int i, java.io.PrintWriter printwriter, Array array);
}
