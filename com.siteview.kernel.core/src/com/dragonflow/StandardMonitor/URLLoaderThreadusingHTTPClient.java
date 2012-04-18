/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLLoaderThreadusingHTTPClient.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLLoaderThreadusingHTTPClient</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import com.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.Utils.*;

import java.util.Date;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLLoaderusingHTTPClient, URLLoadInfousingHTTPClient, URLMonitor

class URLLoaderThreadusingHTTPClient
    implements Runnable
{

    URLLoaderusingHTTPClient urlLoader;
    URLContext urlContext;
    boolean closeSession;
    public static final String EXPIRES_HEADER = "expires: ";

    public URLLoaderThreadusingHTTPClient(URLLoaderusingHTTPClient urlloaderusinghttpclient, URLContext urlcontext, boolean flag)
    {
        closeSession = false;
        urlLoader = urlloaderusinghttpclient;
        urlContext = urlcontext;
        closeSession = flag;
    }

    public void run()
    {
        while(!urlLoader.stopped) 
        {
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = urlLoader.nextURL(this);
            if(urlloadinfousinghttpclient == null)
            {
                synchronized(urlLoader)
                {
                    try
                    {
                        urlLoader.wait();
                    }
                    catch(InterruptedException interruptedexception) { }
                }
                urlloadinfousinghttpclient = urlLoader.nextURL(this);
                if(urlloadinfousinghttpclient == null)
                {
                    return;
                }
            }
            long l = Platform.timeMillis();
            StringBuffer stringbuffer = new StringBuffer();
            StringBuffer stringbuffer1 = new StringBuffer();
            int i = urlLoader.timeout - (int)(Platform.timeMillis() - urlLoader.startTime);
            int j = 0;
            java.util.Vector vector = null;
            URLContext urlcontext = new URLContext(null);
            urlcontext.setRedirectBase(urlloadinfousinghttpclient.url);
            Boolean boolean1 = URLMonitor.authOnFirstRequest(urlLoader.authenticationWhenRequested);
            HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(urlloadinfousinghttpclient.url, urlLoader.username, urlLoader.password, urlLoader.domain, boolean1, urlLoader.proxy, urlLoader.proxyUsername, urlLoader.proxyPassword, vector, 1, j, j);
            URLResults urlresults = URLMonitor.checkURL(httprequestsettings, urlcontext, "", "", null, stringbuffer, urlLoader.contentMax, urlLoader.otherHeaders, i, stringbuffer1, null);
            if(urlresults.getTotalDuration() == -1L)
            {
                urlresults.setTotalDuration(Platform.timeMillis() - l);
            }
            updateTotals(urlloadinfousinghttpclient, urlresults, stringbuffer);
        }
        urlLoader = null;
        urlContext = null;
    }

    void updateTotals(URLLoadInfousingHTTPClient urlloadinfousinghttpclient, URLResults urlresults, StringBuffer stringbuffer)
    {
        urlloadinfousinghttpclient.status = (int)urlresults.getStatus();
        urlloadinfousinghttpclient.duration += urlresults.getTotalDuration();
        urlloadinfousinghttpclient.size += urlresults.getTotalBytes();
        urlloadinfousinghttpclient.dnsTime += urlresults.getDnsTime();
        urlloadinfousinghttpclient.connectTime += urlresults.getConnectTime();
        urlloadinfousinghttpclient.responseTime += urlresults.getResponseTime();
        urlloadinfousinghttpclient.downloadTime += urlresults.getDownloadTime();
        urlloadinfousinghttpclient.cache = true;
        urlloadinfousinghttpclient.loadCount++;
        if(urlloadinfousinghttpclient.status == URLMonitor.kURLok && urlloadinfousinghttpclient.contentType.equals("text/html;charset=" + I18N.nullEncoding()))
        {
            urlloadinfousinghttpclient.contents = stringbuffer.toString().trim();
            urlLoader.updateCookies(urlContext, urlloadinfousinghttpclient);
            String s = URLMonitor.getHTTPHeaders(urlloadinfousinghttpclient.contents).toLowerCase();
            if(s.indexOf("no-cache") >= 0)
            {
                urlloadinfousinghttpclient.cache = false;
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
                                urlloadinfousinghttpclient.cache = false;
                            } else
                            {
                                Date date = new Date(s1);
                                if(date.getTime() < Platform.timeMillis())
                                {
                                    urlloadinfousinghttpclient.cache = false;
                                }
                            }
                        }
                        catch(Exception exception) { }
                    }
                }
            }
            urlLoader.parse(urlloadinfousinghttpclient);
        }
        urlloadinfousinghttpclient.thread = null;
        urlloadinfousinghttpclient.done = true;
        if(!urlloadinfousinghttpclient.cache && urlloadinfousinghttpclient.loadCount < urlloadinfousinghttpclient.count)
        {
            urlloadinfousinghttpclient.setLoadAgain();
        }
        urlLoader.checkDone();
    }
}
