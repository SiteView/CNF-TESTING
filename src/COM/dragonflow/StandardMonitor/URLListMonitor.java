/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLListMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLListMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.File;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLList, URLMonitor

public class URLListMonitor extends AtomicMonitor
{

    static StringProperty pFilename;
    static StringProperty pServer;
    static StringProperty pLogName;
    static StringProperty pErrorLog;
    static StringProperty pThreads;
    static StringProperty pPause;
    static StringProperty pRetries;
    static StringProperty pProxy;
    static StringProperty pUserName;
    static StringProperty pPassword;
    static StringProperty pProxyUsername;
    static StringProperty pProxyPassword;
    static StringProperty pTimeout;
    static StringProperty pPostData;
    static NumericProperty pDuration;
    static NumericProperty pLeft;
    static NumericProperty pGood;
    static NumericProperty pBad;
    static NumericProperty pLastModified;
    URLList urls;
    Array urlData;

    public URLListMonitor()
    {
        urls = null;
        urlData = null;
    }

    public String getHostname()
    {
        return "URLListHost";
    }

    protected boolean update()
    {
        String s = "";
        if(urls != null)
        {
            long l = urls.stopListMonitor();
            if(l != 0L)
            {
                LogManager.log("Error", "URL List did not complete, " + l + " remaining, " + getProperty(pName));
            }
        }
        long l1 = Platform.timeMillis();
        LogManager.log("RunMonitor", "Starting URL List monitor, " + getProperty(pName));
        String s1 = getProperty(pFilename);
        long l2 = getPropertyAsLong(pLastModified);
        File file = new File(s1);
        long l3 = file.lastModified();
        if(urlData == null || l2 != l3)
        {
            try
            {
                long l4 = System.currentTimeMillis();
                String s3 = getProperty(pServer);
                boolean flag = getSetting("_urlListIgnoreFileFormat").length() > 0;
                urlData = getURLsFromFile(s1, s3, flag);
                int j = urlData.size();
                long l6 = System.currentTimeMillis();
                long l7 = l6 - l4;
                LogManager.log("RunMonitor", "Read URL List, " + j + " urls in " + l7 + " ms, " + getProperty(pName));
            }
            catch(Exception exception)
            {
                s = "error reading " + s1 + ": " + exception;
                l3 = -1L;
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                setProperty(pLastModified, l3);
                if(s.length() != 0)
                {
                    setProperty(pLeft, "n/a");
                    setProperty(pGood, "n/a");
                    setProperty(pBad, "n/a");
                    setProperty(pDuration, "n/a");
                    setProperty(pStateString, s);
                    setProperty(pNoData, "n/a");
                }
            }
            if(s.length() == 0)
            {
                int i = getPropertyAsInteger(pTimeout) * 1000;
                if(i == 0)
                {
                    i = URLMonitor.DEFAULT_TIMEOUT;
                }
                String s2 = getSetting("_URLListMonitorIgnoreErrors");
                long l5 = Platform.timeMillis();
                urls = new URLList(this, urlData, getPropertyAsLong(pThreads), getPropertyAsLong(pPause), getProperty(pLogName), getProperty(pErrorLog), getProperty(pProxy), getProperty(pUserName), getProperty(pPassword), getPropertyAsInteger(pRetries), TextUtils.enumToArray(getMultipleValues(pPostData)), i, getProperty(pProxyUsername), getProperty(pProxyPassword), getSetting("_urlOtherHeader"), s2);
                urls.waitForCompletion();
                LogManager.log("RunMonitor", "Completed reading URLs, " + (double)(Platform.timeMillis() - l5) / 1000D + " seconds, " + getProperty(pName));
            }
            LogManager.log("RunMonitor", "Completed URL List monitor, " + (double)(Platform.timeMillis() - l1) / 1000D + " seconds, " + getProperty(pName));
        }
        return true;
    }

    private Array getURLsFromFile(String s, String s1, boolean flag)
        throws Exception
    {
        Array array = new Array();
        if(s1.length() > 0)
        {
            s1 = ";" + s1 + ";";
        }
        String s2 = FileUtils.readFile(s).toString();
        int i = 0;
        int j = 0;
        int k = 0;
        int l;
        for(; i < s2.length(); i = l + 1)
        {
            k++;
            l = s2.indexOf('\n', i);
            if(l == -1)
            {
                l = s2.length();
            }
            String s3 = s2.substring(i, l).trim();
            if(s1.length() != 0 && s3.indexOf(s1) == -1)
            {
                continue;
            }
            String s4 = "";
            int i1 = s3.indexOf(';');
            if(i1 != -1 && !flag)
            {
                int j1 = s3.indexOf(';', i1 + 1);
                if(j1 != -1)
                {
                    int k1 = s3.indexOf(';', j1 + 1);
                    if(k1 != -1)
                    {
                        int l1 = s3.indexOf(';', k1 + 1);
                        if(l1 != -1)
                        {
                            String s5 = s3.substring(j1 + 1, k1);
                            String s6 = s3.substring(k1 + 1, l1);
                            String s7 = s3.substring(l1 + 1);
                            if(s7.startsWith("secure"))
                            {
                                s7 = "https://";
                            } else
                            {
                                s7 = "http://";
                            }
                            String s8 = "";
                            int i2 = s3.indexOf(';', l1 + 1);
                            if(i2 != -1)
                            {
                                s8 = s3.substring(i2 + 1);
                            }
                            s4 = s7 + s5;
                            if(s6.length() > 0)
                            {
                                s4 = s4 + ":" + s6;
                            }
                            if(s8.length() > 0)
                            {
                                if(!s8.startsWith("/"))
                                {
                                    s4 = s4 + "/";
                                }
                                s4 = s4 + s8;
                            }
                        }
                    }
                }
            } else
            {
                s4 = s3;
            }
            if(s4.length() == 0)
            {
                LogManager.log("Error", "URL List format error, " + getProperty(pName) + ", skipping URL " + k + ": " + s3);
            } else
            {
                j++;
                array.add(s4);
            }
        }

        return array;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pDuration);
        array.add(pBad);
        array.add(pGood);
        array.add(pLeft);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pFilename)
        {
            if(s.trim().length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            {
                File file = new File(s);
                if(!file.exists())
                {
                    hashmap.put(stringproperty, stringproperty.getLabel() + " does not exist");
                } else
                if(!file.isFile())
                {
                    hashmap.put(stringproperty, stringproperty.getLabel() + " is not a file");
                }
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public int getNumberOfUrls()
    {
        int i = 0;
        String s = getProperty(pFilename);
        try
        {
            Array array = getURLsFromFile(s, "", false);
            i = array.size();
        }
        catch(Exception exception)
        {
            String s1 = getProperty(pName);
            LogManager.log("Error", s1 + ": error reading file " + s + ", " + exception);
        }
        return i;
    }

    public int getCostInLicensePoints()
    {
        return getNumberOfUrls() * 1;
    }

    static 
    {
        pFilename = new StringProperty("_filename", "");
        if(Platform.isWindows())
        {
            pFilename.setDisplayText("URL List Name", "path name of file containing URL list (e.g. e:/mydir/urls.map)");
        } else
        {
            pFilename.setDisplayText("URL List Name", "path name of file containing URL list (e.g. /usr/mydir/urls.map)");
        }
        pFilename.setParameterOptions(true, 1, false);
        pServer = new StringProperty("_server", "");
        pServer.setDisplayText("Server", "(optional) only monitor URLs whose server field matches this field, default is all URLs");
        pServer.setParameterOptions(true, 1, true);
        pLogName = new StringProperty("_log", "");
        pLogName.setDisplayText("Log", "(optional) path name of log file for all URL results, default is no logging");
        pLogName.setParameterOptions(true, 2, true);
        pErrorLog = new StringProperty("_errors", "");
        pErrorLog.setDisplayText("Error Log", "(optional) path name of log file for URL errors, default is no logging");
        pErrorLog.setParameterOptions(true, 3, true);
        pThreads = new NumericProperty("_threads", "4", "threads");
        pThreads.setDisplayText("Threads", "(optional) number of separate threads to use, default is 4");
        pThreads.setParameterOptions(true, 4, true);
        pPause = new NumericProperty("_pause", "1000", "ms");
        pPause.setDisplayText("Pause", "(optional) pause, in milliseconds, between each URL check, default is 1000");
        pPause.setParameterOptions(true, 5, true);
        pRetries = new NumericProperty("_retries", "0");
        pRetries.setDisplayText("Retries", "(optional) number of times to retry URLs that return errors, default is 0 (never retry error URLs)");
        pRetries.setParameterOptions(true, 6, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional list of proxy servers to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 7, true);
        pProxyUsername = new StringProperty("_proxyusername");
        pProxyUsername.setDisplayText("HTTP Proxy User Name", "The proxy server user name");
        pProxyUsername.setParameterOptions(true, 8, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("HTTP Proxy Password", "The proxy server password");
        pProxyPassword.setParameterOptions(true, 9, true);
        pProxyPassword.isPassword = true;
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URLs require authorization");
        pUserName.setParameterOptions(true, 10, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URLs require authorization");
        pPassword.setParameterOptions(true, 11, true);
        pPassword.isPassword = true;
        pTimeout = new StringProperty("_timeout", "60");
        pTimeout.setDisplayText("Timeout", "timeout per URL");
        pTimeout.setParameterOptions(true, 12, true);
        pPostData = new StringProperty("_postData");
        pBad = new NumericProperty("errors", "0", "URLs");
        pBad.setLabel("errors");
        pBad.setStateOptions(1);
        pLeft = new NumericProperty("left", "0", "URLs");
        pLeft.setLabel("left");
        pLeft.setStateOptions(2);
        pDuration = new NumericProperty("duration", "0", "milliseconds");
        pDuration.setLabel("duration");
        pDuration.setStateOptions(3);
        pGood = new NumericProperty("good", "0", "URLs");
        pLastModified = new NumericProperty("lastModified", "0", "ms");
        StringProperty astringproperty[] = {
            pFilename, pLogName, pErrorLog, pServer, pThreads, pPause, pRetries, pProxy, pUserName, pPassword, 
            pLastModified, pLeft, pGood, pBad, pDuration, pProxyUsername, pProxyPassword, pPostData, pTimeout
        };
        addProperties("COM.dragonflow.StandardMonitor.URLListMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.URLListMonitor", Rule.stringToClassifier("errors > 0\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.URLListMonitor", Rule.stringToClassifier("left = 0 and errors = 0\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.URLListMonitor", Rule.stringToClassifier("lastModified = 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.URLListMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "description", "Monitor a large list of URLs");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "help", "URLListMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "title", "URL List");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "class", "URLListMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "topazName", "URL List");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.URLListMonitor", "classType", "advanced");
    }
}
