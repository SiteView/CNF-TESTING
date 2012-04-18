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
// UniqueNumber, TempFileManager
public abstract class MediaPlayerMonitorUtils {

    protected static final String VugenPath;

    protected static final String MDRVCommand = "mdrv";

    protected static final String tempDir = com.dragonflow.Utils.TempFileManager.getTempDirPath();

    public MediaPlayerMonitorUtils() {
    }

    protected abstract String getLogFileRoot();

    protected abstract String getVugenScriptPath();

    public String getNewMediaLog() {
        return tempDir + getLogFileRoot() + "_" + com.dragonflow.Utils.UniqueNumber.getInstance().getNumber() + ".log";
    }

    public String getMediaCommand(String s, String s1, String s2) {
        StringBuffer stringbuffer = new StringBuffer(VugenPath);
        stringbuffer.append("mdrv");
        stringbuffer.append(" -usr \"").append(getVugenScriptPath());
        stringbuffer.append("\" -no_popups \"").append(tempDir).append("\\mdrvpopup.log\"");
        stringbuffer.append(" -url \"").append(s);
        stringbuffer.append("\" -duration ").append(s1);
        stringbuffer.append(" -log \"").append(s2);
        stringbuffer.append("\" -drv_log_file \"").append(s2);
        stringbuffer.append("_\"");
        return stringbuffer.toString();
    }

    static {
        VugenPath = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "bin" + java.io.File.separator;
    }
}
