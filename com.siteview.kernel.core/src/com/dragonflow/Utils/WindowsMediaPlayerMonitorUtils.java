/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Utils:
// MediaPlayerMonitorUtils
public class WindowsMediaPlayerMonitorUtils extends com.dragonflow.Utils.MediaPlayerMonitorUtils {

    private static String VugenScriptPath = com.dragonflow.SiteView.Platform.getRoot() + "\\media\\mms\\media.usr";

    private static String logFileRoot = "\\WindowsMedia";

    public WindowsMediaPlayerMonitorUtils() {
    }

    protected String getLogFileRoot() {
        return logFileRoot;
    }

    protected String getVugenScriptPath() {
        return VugenScriptPath;
    }

}
