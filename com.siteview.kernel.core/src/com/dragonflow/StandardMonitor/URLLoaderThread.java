/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLLoaderThread.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLLoaderThread</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import com.dragonflow.SiteView.Platform;
import com.dragonflow.Utils.SocketSession;

import java.util.Date;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLLoader, URLLoadInfo, URLMonitor

class URLLoaderThread
    implements Runnable
{

    URLLoader urlLoader;
    SocketSession session;
    boolean closeSession;
    public static final String EXPIRES_HEADER = "expires: ";

    public URLLoaderThread(URLLoader urlloader, SocketSession socketsession, boolean flag)
    {
        closeSession = false;
        urlLoader = urlloader;
        session = socketsession;
        closeSession = flag;
    }

    public void run()
    {
        while(!urlLoader.stopped) 
        {
            URLLoadInfo urlloadinfo = urlLoader.nextURL(this);
            if(urlloadinfo == null)
            {
                synchronized(urlLoader)
                {
                    try
                    {
                        urlLoader.wait();
                    }
                    catch(InterruptedException interruptedexception) { }
                }
                urlloadinfo = urlLoader.nextURL(this);
                if(urlloadinfo == null)
                {
                    return;
                }
            }
            long l = Platform.timeMillis();
            StringBuffer stringbuffer = new StringBuffer();
            int i = 0;
            StringBuffer stringbuffer1 = new StringBuffer();
            int j = urlLoader.timeout - (int)(Platform.timeMillis() - urlLoader.startTime);
            long al[] = URLMonitor.checkURL(session, urlloadinfo.url, "", "", urlLoader.proxy, urlLoader.proxyUsername, urlLoader.proxyPassword, null, urlLoader.username, urlLoader.password, urlLoader.location, stringbuffer, urlLoader.contentMax, urlLoader.otherHeaders, i, j, stringbuffer1);
            if(al[1] == -1L)
            {
                al[1] = Platform.timeMillis() - l;
            }
            updateTotals(urlloadinfo, al, stringbuffer);
        }
        if(closeSession)
        {
            session.close();
        }
        urlLoader = null;
        session = null;
    }

    void updateTotals(URLLoadInfo urlloadinfo, long al[], StringBuffer stringbuffer)
    {
        urlloadinfo.status = (int)al[0];
        urlloadinfo.duration += al[1];
        urlloadinfo.size += al[2];
        urlloadinfo.dnsTime += al[5];
        urlloadinfo.connectTime += al[6];
        urlloadinfo.responseTime += al[7];
        urlloadinfo.downloadTime += al[8];
        urlloadinfo.cache = true;
        urlloadinfo.loadCount++;
        if(urlloadinfo.status == URLMonitor.kURLok && urlloadinfo.contentType.equals("text/html"))
        {
            urlloadinfo.contents = stringbuffer.toString().trim();
            urlLoader.updateCookies(session, urlloadinfo);
            String s = URLMonitor.getHTTPHeaders(urlloadinfo.contents).toLowerCase();
            if(s.indexOf("no-cache") >= 0)
            {
                urlloadinfo.cache = false;
            } else
            {
                int i = s.indexOf("expires: ");
                if(i >= 0)
                {
                    int j = s.indexOf("\n", i + 1);
                    if(j >= 0)
                    {
                        String s1 = s.substring(i + "expires: ".length(), j);
                        try
                        {
                            if(s1.trim().equals("0"))
                            {
                                urlloadinfo.cache = false;
                            } else
                            {
                                Date date = new Date(s1);
                                if(date.getTime() < Platform.timeMillis())
                                {
                                    urlloadinfo.cache = false;
                                }
                            }
                        }
                        catch(Exception exception) { }
                    }
                }
            }
            urlLoader.parse(urlloadinfo);
        }
        urlloadinfo.thread = null;
        urlloadinfo.done = true;
        if(!urlloadinfo.cache && urlloadinfo.loadCount < urlloadinfo.count)
        {
            urlloadinfo.setLoadAgain();
        }
        urlLoader.checkDone();
    }
}
