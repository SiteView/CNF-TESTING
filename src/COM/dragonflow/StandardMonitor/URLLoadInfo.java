/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLLoadInfo.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLLoadInfo</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Utils.URLInfo;

import java.io.PrintStream;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLLoaderThread

class URLLoadInfo extends URLInfo
{

    long dnsTime;
    long connectTime;
    long responseTime;
    long downloadTime;
    int loadCount;
    URLLoaderThread thread;
    boolean done;
    boolean cache;
    String frameName;
    String contents;

    URLLoadInfo(String s)
    {
        super(s);
        dnsTime = 0L;
        connectTime = 0L;
        responseTime = 0L;
        downloadTime = 0L;
        loadCount = 0;
        thread = null;
        done = false;
        cache = true;
        frameName = "";
        contents = "";
        duration = 0L;
        size = 0L;
    }

    boolean needsLoading()
    {
        return !isLoaded() && !isLoading();
    }

    boolean isLoading()
    {
        return thread != null;
    }

    boolean isLoaded()
    {
        return status != -1;
    }

    void setLoadAgain()
    {
        status = -1;
        done = false;
    }

    void print()
    {
        System.out.println("URL=" + url);
        System.out.println("STATUS=" + status);
        System.out.println("TIME=" + duration);
        System.out.println("SIZE=" + size);
    }
}
