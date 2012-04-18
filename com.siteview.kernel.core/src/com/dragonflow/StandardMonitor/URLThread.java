/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLThread.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLThread</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import com.dragonflow.SiteView.Platform;
import com.dragonflow.Utils.SocketSession;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLList, URLMonitor

class URLThread extends Thread
{

    URLList urls;
    long pause;

    public URLThread(URLList urllist, long l)
    {
        super((Runnable)null);
        urls = urllist;
        pause = l;
    }

    public void run()
    {
        do
        {
            String s = urls.nextURL();
            if(s == null)
            {
                return;
            }
            long l = Platform.timeMillis();
            SocketSession socketsession = SocketSession.getSession(null);
            String s1 = "";
            String s2 = "";
            String s3 = "";
            StringBuffer stringbuffer = null;
            long l1 = 50000L;
            int i = 0;
            StringBuffer stringbuffer1 = null;
            long al[] = URLMonitor.checkURL(socketsession, s, s1, s2, urls.proxy, urls.proxyUsername, urls.proxyPassword, urls.postData, urls.username, urls.password, s3, stringbuffer, l1, urls.otherHeaders, i, urls.timeout, stringbuffer1);
            socketsession.close();
            long l2 = al[0];
            long l3 = al[1];
            if(l3 == 0L)
            {
                l3 = Platform.timeMillis() - l;
            }
            urls.total(l2, l3, s);
            Platform.sleep(pause);
        } while(true);
    }
}
