/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLList.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLList</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.TextUtils;

import java.io.*;
import jgl.Array;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLThread, URLListMonitor

class URLList
{

    Array urls;
    URLListMonitor monitor;
    String proxy;
    String username;
    String password;
    String ignoreErrors;
    int retries;
    int retriesLeft;
    String proxyUsername;
    String proxyPassword;
    Array postData;
    String otherHeaders;
    int timeout;
    Array errorURLs;
    int index;
    long nBadURLs;
    long nGoodURLs;
    long nConnectionErrorURLs;
    long done;
    long duration;
    long threads;
    long pause;
    String goodName;
    String errorName;
    boolean stopped;
    RandomAccessFile goodLog;

    URLList(URLListMonitor urllistmonitor, Array array, long l, long l1, String s, 
            String s1, String s2, String s3, String s4, int i, Array array1, int j, 
            String s5, String s6, String s7, String s8)
    {
        errorURLs = new Array();
        index = 0;
        nBadURLs = 0L;
        nGoodURLs = 0L;
        nConnectionErrorURLs = 0L;
        done = 0L;
        duration = 0L;
        threads = 0L;
        pause = 0L;
        stopped = false;
        goodLog = null;
        monitor = urllistmonitor;
        urls = array;
        goodName = s;
        errorName = s1;
        proxy = s2;
        username = s3;
        password = s4;
        retries = i;
        retriesLeft = i;
        threads = l;
        pause = l1;
        postData = array1;
        timeout = j;
        proxyUsername = s5;
        proxyPassword = s6;
        otherHeaders = s7;
        ignoreErrors = s8;
        openGood();
        updateProperties();
        start();
    }

    void start()
    {
        done = 0L;
        index = 0;
        for(int i = 0; (long)i < threads; i++)
        {
            URLThread urlthread = new URLThread(this, pause);
            urlthread.start();
        }

    }

    void waitForCompletion()
    {
        for(; done < (long)urls.size() && !stopped; Platform.sleep(4000L)) { }
        while(!stopped && retriesLeft > 0) 
        {
            retriesLeft--;
            urls = errorURLs;
            errorURLs = new Array();
            start();
            while(done < (long)urls.size() && !stopped) 
            {
                Platform.sleep(4000L);
            }
        }
        stopListMonitor();
    }

    void openGood()
    {
        if(goodName.length() == 0)
        {
            return;
        }
        try
        {
            goodLog = new RandomAccessFile(goodName, "rw");
            goodLog.seek(goodLog.length());
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not open log file " + goodName + ", " + ioexception);
        }
    }

    void closeGood()
    {
        if(goodLog == null)
        {
            return;
        }
        try
        {
            goodLog.close();
            goodLog = null;
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not close log file " + goodName + ", " + ioexception);
        }
    }

    void logGood(String s)
    {
        if(goodLog == null)
        {
            return;
        }
        try
        {
            goodLog.writeBytes(s);
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not write log file " + goodName + ", " + ioexception);
        }
    }

    void logError(String s)
    {
        if(errorName.length() == 0)
        {
            return;
        }
        try
        {
            RandomAccessFile randomaccessfile = new RandomAccessFile(errorName, "rw");
            randomaccessfile.seek(randomaccessfile.length());
            randomaccessfile.writeBytes(s);
            randomaccessfile.close();
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not write to log file " + errorName + ", " + ioexception);
        }
    }

    synchronized long stopListMonitor()
    {
        long l = (long)urls.size() - done;
        stopped = true;
        closeGood();
        return l;
    }

    synchronized String nextURL()
    {
        if(stopped)
        {
            return null;
        }
        if(index < urls.size())
        {
            return (String)urls.at(index++);
        } else
        {
            return null;
        }
    }

    void total(long l, long l1, String s)
    {
        synchronized(this)
        {
            String s1 = "";
            s1 = s1 + "[" + monitor.getProperty("_name") + "] ";
            s1 = s1 + TextUtils.dateToString(Platform.makeDate());
            s1 = s1 + "," + l + "," + l1 + "," + s;
            if(retries > 0 && retriesLeft < retries)
            {
                s1 = s1 + ",retry #" + (retries - retriesLeft);
            }
            s1 = s1 + "\n";
            done++;
            boolean flag = l == (long)Monitor.kURLok;
            if(!flag && ignoreErrors.length() > 0)
            {
                flag = ignoreErrors.indexOf("" + l) >= 0;
            }
            if(retriesLeft > 0 && !flag)
            {
                errorURLs.add(s);
            } else
            {
                if(flag)
                {
                    nGoodURLs++;
                    duration += l1;
                } else
                {
                    nBadURLs++;
                    logError(s1);
                    if(l < 0L)
                    {
                        nConnectionErrorURLs++;
                    }
                }
                logGood(s1);
                updateProperties();
            }
        }
    }

    void updateProperties()
    {
        long l = 0L;
        if(nGoodURLs != 0L)
        {
            l = duration / nGoodURLs;
        }
        if(nConnectionErrorURLs == (long)urls.size())
        {
            monitor.setProperty(URLListMonitor.pNoData, "n/a");
        }
        long l1 = (long)urls.size() - done;
        monitor.setProperty(URLListMonitor.pDuration, l);
        monitor.setProperty(URLListMonitor.pGood, nGoodURLs);
        monitor.setProperty(URLListMonitor.pBad, nBadURLs);
        monitor.setProperty(URLListMonitor.pLeft, l1);
        String s = "" + nGoodURLs + " good, " + nBadURLs + " errors, " + ((long)urls.size() - done) + " left, avg " + l + " ms";
        monitor.setProperty(URLListMonitor.pStateString, s);
    }
}
