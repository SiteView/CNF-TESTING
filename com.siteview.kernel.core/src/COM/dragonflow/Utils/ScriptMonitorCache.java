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

import java.io.File;
import java.util.Date;

import jgl.Array;

// Referenced classes of package COM.dragonflow.Utils:
// FileUtils, TextUtils

public class ScriptMonitorCache {

    java.io.File f;

    String path;

    int exitValue;

    jgl.Array output;

    int cacheLifeTime;

    public ScriptMonitorCache(String s, String s1, int i) {
        this(s, s1, COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "cache" + java.io.File.separator + "scripts", i);
    }

    public ScriptMonitorCache(String s, String s1, String s2, int i) {
        path = s2;
        f = new File(s2 + java.io.File.separator + s + "." + s1);
        exitValue = -1;
        output = new Array();
        cacheLifeTime = i;
        init();
    }

    private void init() {
        load();
    }

    public boolean isFresh() {
        return f.exists() && java.lang.System.currentTimeMillis() < f.lastModified() + (long) (cacheLifeTime * 1000);
    }

    /**
     * 
     * 
     * @return
     */
    public synchronized jgl.Array load() {
        String as[] = null;
        output.clear();
        try {
            if (f.exists() && f.length() > 0L) {
                StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readFile(f.getAbsolutePath());
                as = COM.dragonflow.Utils.TextUtils.split(stringbuffer.toString(), "\n");
            }
            if (as == null || as.length == 0) {
                return output;
            }

            exitValue = (new Integer(as[0])).intValue();
            for (int i = 0; i < as.length; i ++) {
                String s = as[i];
                output.add(s);
            }

        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
        } catch (java.lang.NumberFormatException numberformatexception) {
            numberformatexception.printStackTrace();
        }
        return output;
    }

    public void update(int i, jgl.Array array) {
        StringBuffer stringbuffer;
        stringbuffer = new StringBuffer();
        stringbuffer.append("" + i + "\n");
        if (array == null) {
            return;
        }
        try {
            for (int j = 0; j < array.size(); j ++) {
                stringbuffer.append((String) array.at(j) + "\n");
            }

            java.io.File file = (new File(f.getAbsolutePath())).getParentFile();
            if (file != null) {
                file.mkdirs();
            }
            COM.dragonflow.Utils.FileUtils.writeFile(f.getAbsolutePath(), stringbuffer.toString());
            exitValue = i;
            output = array;
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
        }
        return;
    }

    public java.util.Date getLastModDate() {
        return new Date(f.lastModified());
    }

    public int getCacheLifeTime() {
        return cacheLifeTime;
    }

    public synchronized int getExitValue() {
        return exitValue;
    }

    public synchronized jgl.Array getOutput() {
        return output;
    }
}
