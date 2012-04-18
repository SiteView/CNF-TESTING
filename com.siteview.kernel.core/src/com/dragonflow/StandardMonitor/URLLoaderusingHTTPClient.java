/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLLoaderusingHTTPClient.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLLoaderusingHTTPClient</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.io.PrintWriter;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;
import com.dragonflow.Log.LogManager;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.Utils.HTMLTagParser;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.TextUtils;
import com.dragonflow.Utils.URLContext;
import com.dragonflow.Utils.URLResults;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLLoadInfousingHTTPClient, URLLoaderThreadusingHTTPClient, URLMonitor

public class URLLoaderusingHTTPClient
{

    AtomicMonitor monitor;
    String proxy;
    String username;
    String password;
    String domain;
    String authenticationWhenRequested;
    String proxyUsername;
    String proxyPassword;
    String otherHeaders;
    int timeout;
    long contentMax;
    long totalTime;
    long startTime;
    long endTime;
    int threads;
    long totalContentMaxBytes;
    HashMap urlMap;
    Array urlList;
    boolean loadFrames;
    boolean loadImages;
    boolean stopped;
    Array contentMatches;
    Array errorMatches;
    URLLoaderThreadusingHTTPClient threadList[];
    public PrintWriter traceStream;
    static String TARGET_TAGS[] = {
        "INPUT", "IMG", "BASE", "BODY", "FRAME", "APPLET", "SCRIPT", "LINK", "IFRAME"
    };

    void clear()
    {
        traceStream = null;
        threadList = null;
        errorMatches = null;
        contentMatches = null;
        urlList = null;
        monitor = null;
    }

    public static void main(String args[])
    {
        int i = 4;
        String s = "http://www.dragonflowinteractive.com/";
        if(args.length > 0)
        {
            s = args[0];
        }
        if(args.length > 1)
        {
            i = TextUtils.toInt(args[1]);
        }
        String s1 = "";
        String s2 = "";
        String s3 = "";
        Array array = null;
        String s4 = "";
        String s5 = "";
        String s6 = "";
        StringBuffer stringbuffer = new StringBuffer();
        long l = 50000L;
        String s7 = "";
        int j = 60000;
        int k = 0;
        java.util.Vector vector = null;
        URLContext urlcontext = new URLContext(null);
        urlcontext.setRedirectBase(s);
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s4, s5, s6, null, s1, s2, s3, vector, 1, k, k);
        URLResults urlresults = URLMonitor.checkURL(httprequestsettings, urlcontext, "", "", array, stringbuffer, l, s7, j, null, null);
        long l1 = urlresults.getTotalBytes();
        System.out.println("status=" + urlresults.getStatus());
        System.out.println("duration=" + urlresults.getTotalDuration() + " ms");
        System.out.println("size=" + urlresults.getTotalBytes() + " bytes");
        HashMap hashmap = new HashMap();
        URLLoaderusingHTTPClient urlloaderusinghttpclient = new URLLoaderusingHTTPClient(null, urlcontext, hashmap, i, true, true, s, "", "", "", "", "", 50000L, stringbuffer, j, s1, s2, s3, null);
        urlloaderusinghttpclient.waitForCompletion();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        long al[] = urlloaderusinghttpclient.getResults("", "", stringbuffer1, stringbuffer2);
        System.out.println("totalDuration=" + al[1]);
        System.out.println("overallResult=" + al[0]);
        System.out.println("totalBytes=" + (al[2] + l1));
        System.out.println("dnsTime=" + al[5]);
        System.out.println("connectTime=" + al[6]);
        System.out.println("responseTime=" + al[7]);
        System.out.println("downloadTime=" + al[8]);
        System.out.println("totalFrameSize=" + al[9]);
        System.out.println("totalFrames=" + al[10]);
        System.out.println("totalFrameErrors=" + al[11]);
        System.out.println("totalImageSize=" + al[12]);
        System.out.println("totalImages=" + al[13]);
        System.out.println("totalImageErrors=" + al[14]);
    }

    public URLLoaderusingHTTPClient(AtomicMonitor atomicmonitor, URLContext urlcontext, HashMap hashmap, int i, boolean flag, boolean flag1, String s, 
            String s1, String s2, String s3, String s4, String s5, long l, 
            StringBuffer stringbuffer, int j, String s6, String s7, String s8, PrintWriter printwriter)
    {
        monitor = null;
        proxy = "";
        username = "";
        password = "";
        domain = "";
        authenticationWhenRequested = "";
        proxyUsername = "";
        proxyPassword = "";
        otherHeaders = "";
        timeout = URLMonitor.DEFAULT_TIMEOUT;
        contentMax = 50000L;
        totalTime = -1L;
        startTime = -1L;
        endTime = -1L;
        threads = 1;
        totalContentMaxBytes = 0L;
        urlMap = null;
        urlList = new Array();
        loadFrames = false;
        loadImages = false;
        stopped = false;
        contentMatches = new Array();
        errorMatches = new Array();
        traceStream = null;
        monitor = atomicmonitor;
        if(atomicmonitor != null)
        {
            AtomicMonitor _tmp = atomicmonitor;
            String s9 = atomicmonitor.getProperty(AtomicMonitor.pClass);
            if(s9.indexOf("Remote") > 0)
            {
                totalContentMaxBytes = TextUtils.toLong(atomicmonitor.getSetting("_urlLoaderMaxBytes"));
            }
        }
        urlMap = hashmap;
        if(hashmap == null)
        {
            urlMap = new HashMap();
        }
        threads = i;
        if(i < 1)
        {
            threads = 1;
        }
        loadFrames = flag;
        loadImages = flag1;
        username = s1;
        password = s2;
        domain = s3;
        authenticationWhenRequested = s4;
        otherHeaders = s5;
        contentMax = l;
        timeout = j;
        proxy = s6;
        proxyUsername = s7;
        proxyPassword = s8;
        traceStream = printwriter;
        URLLoadInfousingHTTPClient urlloadinfousinghttpclient = new URLLoadInfousingHTTPClient(s);
        urlloadinfousinghttpclient.contents = stringbuffer.toString();
        parse(urlloadinfousinghttpclient);
        threadList = new URLLoaderThreadusingHTTPClient[i];
        URLLoaderThreadusingHTTPClient urlloaderthreadusinghttpclient = new URLLoaderThreadusingHTTPClient(this, urlcontext, false);
        threadList[0] = urlloaderthreadusinghttpclient;
        for(int k = 1; k < i; k++)
        {
            URLContext urlcontext1 = new URLContext(null);
            urlcontext1.setCookies(urlcontext.getCookies());
            URLLoaderThreadusingHTTPClient urlloaderthreadusinghttpclient1 = new URLLoaderThreadusingHTTPClient(this, urlcontext1, true);
            threadList[k] = urlloaderthreadusinghttpclient1;
        }

        if(i > 1)
        {
            String s10 = "* no monitor *";
            if(atomicmonitor != null)
            {
                s10 = atomicmonitor.getProperty(URLMonitor.pName);
            }
            LogManager.log("RunMonitor", "Starting " + i + " threaded frame/image load for " + s10);
        }
        startTime = Platform.timeMillis();
        checkDone();
        if(!stopped)
        {
            if(i == 1)
            {
                while(!stopped) 
                {
                    threadList[0].run();
                    checkDone();
                }
            } else
            {
                for(int i1 = 0; i1 < i; i1++)
                {
                    String s11 = "URLLoaderThread #" + (i1 + 1);
                    if(atomicmonitor != null)
                    {
                        s11 = s11 + " for " + atomicmonitor.getProperty(URLMonitor.pName);
                    }
                    Thread thread = new Thread(threadList[i1]);
                    thread.setName(s11);
                    thread.start();
                }

            }
        }
    }

    void checkDone()
    {
        boolean flag;
label0:
        {
            flag = true;
            if(Platform.timeMillis() > startTime + (long)timeout)
            {
                break label0;
            }
            Enumeration enumeration = urlMap.elements();
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)enumeration.nextElement();
            } while(urlloadinfousinghttpclient.done);
            flag = false;
        }
        if(flag)
        {
            stopped = true;
            endTime = Platform.timeMillis();
        }
    }

    public void waitForCompletion()
    {
        while(!stopped) 
        {
            Platform.sleep(2000L);
        }
        stopLoader();
        if(threads > 1)
        {
            String s = "* no monitor *";
            if(monitor != null)
            {
                s = monitor.getProperty(URLMonitor.pName);
            }
            LogManager.log("RunMonitor", "Done with " + threads + " threaded frame/image load for " + s);
        }
    }

    public long[] getResults(String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1)
    {
        long al[] = new long[15];
        long l = URLMonitor.kURLok;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        long l4 = 0L;
        long l5 = 0L;
        long l6 = 0L;
        long l7 = 0L;
        long l8 = 0L;
        long l9 = 0L;
        long l10 = 0L;
        long l11 = 0L;
        long l12 = 0L;
        Enumeration enumeration = urlMap.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)enumeration.nextElement();
            if(urlloadinfousinghttpclient.done)
            {
                if(l == (long)URLMonitor.kURLok)
                {
                    l = urlloadinfousinghttpclient.status;
                }
                l1 += urlloadinfousinghttpclient.duration;
                l2 += urlloadinfousinghttpclient.size;
                l3 += urlloadinfousinghttpclient.dnsTime;
                l4 += urlloadinfousinghttpclient.connectTime;
                l5 += urlloadinfousinghttpclient.responseTime;
                l6 += urlloadinfousinghttpclient.downloadTime;
                if(urlloadinfousinghttpclient.contentType.indexOf("text/html") >= 0)
                {
                    l7 += urlloadinfousinghttpclient.loadCount;
                    l8 += urlloadinfousinghttpclient.size;
                    if(urlloadinfousinghttpclient.status != URLMonitor.kURLok)
                    {
                        if(stringbuffer.length() > 0)
                        {
                            stringbuffer.append("\t");
                        }
                        stringbuffer.append(URLMonitor.lookupStatus(urlloadinfousinghttpclient.status) + ", " + urlloadinfousinghttpclient.url);
                        l9++;
                    } else
                    {
                        if(s.length() > 0)
                        {
                            int i = TextUtils.matchExpression(urlloadinfousinghttpclient.contents, s);
                            if(i != Monitor.kURLok)
                            {
                                String s2 = URLMonitor.getHTMLEncoding(urlloadinfousinghttpclient.contents);
                                TextUtils.matchExpression(urlloadinfousinghttpclient.contents, I18N.UnicodeToString(s, s2));
                            }
                            if(i == URLMonitor.kURLok)
                            {
                                contentMatches.add(urlloadinfousinghttpclient);
                            }
                        }
                        if(s1.length() != 0)
                        {
                            int j = TextUtils.matchExpression(urlloadinfousinghttpclient.contents, s1);
                            if(j != Monitor.kURLok)
                            {
                                String s3 = URLMonitor.getHTMLEncoding(urlloadinfousinghttpclient.contents);
                                TextUtils.matchExpression(urlloadinfousinghttpclient.contents, I18N.UnicodeToString(s1, s3));
                            }
                            if(j == Monitor.kURLok)
                            {
                                errorMatches.add(urlloadinfousinghttpclient);
                            }
                        }
                    }
                } else
                {
                    l10 += urlloadinfousinghttpclient.loadCount;
                    l11 += (int)urlloadinfousinghttpclient.size;
                    if(urlloadinfousinghttpclient.status != URLMonitor.kURLok)
                    {
                        if(stringbuffer1.length() > 0)
                        {
                            stringbuffer1.append("\t");
                        }
                        stringbuffer1.append(URLMonitor.lookupStatus(urlloadinfousinghttpclient.status) + ", " + urlloadinfousinghttpclient.url);
                        l12++;
                    }
                }
            }
        } while(true);
        if(errorMatches.size() > 0)
        {
            l = URLMonitor.kURLContentErrorFound;
        }
        if(totalContentMaxBytes > 0L && l11 + l8 > totalContentMaxBytes)
        {
            AtomicMonitor _tmp = monitor;
            AtomicMonitor _tmp1 = monitor;
            LogManager.log("Error", "Monitor (" + monitor.getProperty(AtomicMonitor.pName) + ") in group (" + monitor.getProperty(AtomicMonitor.pOwnerID) + ") exceeded Frames&Images limit of " + totalContentMaxBytes + " bytes");
        }
        al[0] = l;
        al[1] = l1;
        if(threads > 1)
        {
            al[1] = endTime - startTime;
        }
        al[2] = l2;
        al[5] = l3;
        al[6] = l4;
        al[7] = l5;
        al[8] = l6;
        al[9] = l8;
        al[10] = l7;
        al[11] = l9;
        al[12] = l11;
        al[13] = l10;
        al[14] = l12;
        return al;
    }

    void addToSummary(URLLoadInfousingHTTPClient urlloadinfousinghttpclient, StringBuffer stringbuffer)
    {
        stringbuffer.append("<TR><TD>");
        stringbuffer.append(urlloadinfousinghttpclient.url);
        stringbuffer.append("<TD>");
        stringbuffer.append(URLMonitor.lookupStatus(urlloadinfousinghttpclient.status));
        stringbuffer.append("<TD>");
        stringbuffer.append(URLMonitor.pRoundTripTime.valueString("" + urlloadinfousinghttpclient.duration, 2));
        stringbuffer.append("<TD>");
        stringbuffer.append(TextUtils.bytesToString(urlloadinfousinghttpclient.size));
        stringbuffer.append("<TD>");
        stringbuffer.append(urlloadinfousinghttpclient.loadCount);
        stringbuffer.append("<TD>");
        stringbuffer.append(urlloadinfousinghttpclient.count);
        stringbuffer.append("</TR>\n");
    }

    void addHeaderToSummary(String s, StringBuffer stringbuffer)
    {
        stringbuffer.append("<TABLE BORDER=1 cellspacing=0><TR><TH>" + s + " URL<TH>status<TH>time<TH>size<TH>load count<TH>ref count</TR>\n");
    }

    void addNoneFoundToSummary(String s, StringBuffer stringbuffer)
    {
        stringbuffer.append("<TR><TD COLSPAN=6 ALIGN=CENTER><B>no " + s + " retrieved</B></TD></TR>");
    }

    public void getSummary(StringBuffer stringbuffer, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        addHeaderToSummary("Image", stringbuffer);
        addHeaderToSummary("Frame", stringbuffer1);
        boolean flag = false;
        boolean flag1 = false;
        Enumeration enumeration = (Enumeration) urlList.iterator();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)enumeration.nextElement();
            if(urlloadinfousinghttpclient.contentType.equals("text/html;charset=" + I18N.nullEncoding()))
            {
                addToSummary(urlloadinfousinghttpclient, stringbuffer1);
                flag = true;
                if(urlloadinfousinghttpclient.contents.length() > 0)
                {
                    stringbuffer2.append("\n<HR><B>Frame: " + urlloadinfousinghttpclient.url + "</B><HR><PRE>\n");
                    stringbuffer2.append(TextUtils.escapeHTML(urlloadinfousinghttpclient.contents));
                    stringbuffer2.append("</PRE><P>\n");
                }
            } else
            {
                addToSummary(urlloadinfousinghttpclient, stringbuffer);
                flag1 = true;
            }
        } while(true);
        if(!flag1)
        {
            addNoneFoundToSummary("images", stringbuffer);
        }
        if(!flag)
        {
            addNoneFoundToSummary("frames", stringbuffer1);
        }
        stringbuffer.append("</TABLE>\n");
        stringbuffer1.append("</TABLE>\n");
    }

    public boolean foundContentMatch()
    {
        return contentMatches.size() > 0;
    }

    public String getContentMatchContents()
    {
        if(contentMatches.size() > 0)
        {
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)contentMatches.get(0);
            return urlloadinfousinghttpclient.contents;
        } else
        {
            return "";
        }
    }

    public String getErrorMatchContents()
    {
        if(errorMatches.size() > 0)
        {
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)errorMatches.get(0);
            return urlloadinfousinghttpclient.contents;
        } else
        {
            return "";
        }
    }

    synchronized void stopLoader()
    {
        stopped = true;
        notifyAll();
    }

    void progressMessage(String s)
    {
        if(monitor != null)
        {
            monitor.progressString += s + "\n";
        }
        if(traceStream != null)
        {
            traceStream.println(s + "<BR>");
            traceStream.flush();
        }
    }

    synchronized URLLoadInfousingHTTPClient nextURL(URLLoaderThreadusingHTTPClient urlloaderthreadusinghttpclient)
    {
        if(stopped)
        {
            return null;
        }
        for(Enumeration enumeration = urlMap.elements(); enumeration.hasMoreElements();)
        {
            URLLoadInfousingHTTPClient urlloadinfousinghttpclient = (URLLoadInfousingHTTPClient)enumeration.nextElement();
            if(urlloadinfousinghttpclient.needsLoading())
            {
                urlloadinfousinghttpclient.thread = urlloaderthreadusinghttpclient;
                progressMessage("Retrieving " + urlloadinfousinghttpclient.url);
                return urlloadinfousinghttpclient;
            }
        }

        return null;
    }

    synchronized void updateCookies(URLContext urlcontext, URLLoadInfousingHTTPClient urlloadinfousinghttpclient)
    {
        urlcontext.updateCookies(urlloadinfousinghttpclient.contents, urlloadinfousinghttpclient.url);
    }

    synchronized void addURLToMap(URLLoadInfousingHTTPClient urlloadinfousinghttpclient)
    {
        URLLoadInfousingHTTPClient urlloadinfousinghttpclient1 = (URLLoadInfousingHTTPClient)urlMap.get(urlloadinfousinghttpclient.url);
        boolean flag = false;
        if(urlloadinfousinghttpclient1 != null)
        {
            urlloadinfousinghttpclient1.count++;
            if(urlloadinfousinghttpclient1.isLoaded() && (!urlloadinfousinghttpclient1.cache || urlloadinfousinghttpclient1.status != URLMonitor.kURLok))
            {
                urlloadinfousinghttpclient1.setLoadAgain();
                flag = true;
            }
        } else
        {
            urlMap.put(urlloadinfousinghttpclient.url, urlloadinfousinghttpclient);
            urlList.add(urlloadinfousinghttpclient);
            urlloadinfousinghttpclient.count = 1;
            flag = true;
        }
        if(flag)
        {
            notify();
        }
    }

    void parse(URLLoadInfousingHTTPClient urlloadinfousinghttpclient)
    {
        HTMLTagParser htmltagparser = new HTMLTagParser(URLMonitor.getHTTPContent(urlloadinfousinghttpclient.contents), TARGET_TAGS, true);
        boolean flag = false;
        if(monitor != null)
        {
            if(monitor.getSetting("_ignoreScripts").length() > 0)
            {
                htmltagparser.ignoreScripts = false;
            }
            if(monitor.getSetting("_getExternalFiles").length() > 0)
            {
                flag = true;
            }
        }
        htmltagparser.process();
        String s = "";
        Enumeration enumeration = htmltagparser.findTags("base");
        if(enumeration.hasMoreElements())
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            s = TextUtils.getValue(hashmap, "href");
        }
        if(loadImages && flag)
        {
            Enumeration enumeration1 = htmltagparser.findTags("script");
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap1 = (HashMap)enumeration1.nextElement();
                String s1 = TextUtils.getValue(hashmap1, "src");
                if(s1.length() > 0)
                {
                    s1 = TextUtils.unescapeHTML(s1);
                    s1 = URLMonitor.resolveURL(s1, urlloadinfousinghttpclient, s);
                    URLLoadInfousingHTTPClient urlloadinfousinghttpclient1 = new URLLoadInfousingHTTPClient(s1);
                    addURLToMap(urlloadinfousinghttpclient1);
                }
            } while(true);
            Enumeration enumeration4 = htmltagparser.findTags("link");
            do
            {
                if(!enumeration4.hasMoreElements())
                {
                    break;
                }
                boolean flag1 = false;
                HashMap hashmap6 = (HashMap)enumeration4.nextElement();
                String s6 = TextUtils.getValue(hashmap6, "type");
                s6.toLowerCase();
                if(s6.indexOf("css") >= 0)
                {
                    flag1 = true;
                } else
                {
                    String s7 = TextUtils.getValue(hashmap6, "rel");
                    s7.toLowerCase();
                    if(s7.indexOf("stylesheet") >= 0)
                    {
                        flag1 = true;
                    }
                }
                if(flag1)
                {
                    String s9 = TextUtils.getValue(hashmap6, "href");
                    if(s9.length() > 0)
                    {
                        s9 = TextUtils.unescapeHTML(s9);
                        s9 = URLMonitor.resolveURL(s9, urlloadinfousinghttpclient, s);
                        URLLoadInfousingHTTPClient urlloadinfousinghttpclient7 = new URLLoadInfousingHTTPClient(s9);
                        addURLToMap(urlloadinfousinghttpclient7);
                    }
                }
            } while(true);
        }
        if(loadFrames)
        {
            Enumeration enumeration2 = htmltagparser.findTags("frame");
            do
            {
                if(!enumeration2.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap2 = (HashMap)enumeration2.nextElement();
                String s2 = TextUtils.getValue(hashmap2, "src");
                if(s2.length() > 0)
                {
                    s2 = TextUtils.unescapeHTML(s2);
                    s2 = URLMonitor.resolveURL(s2, urlloadinfousinghttpclient, s);
                    URLLoadInfousingHTTPClient urlloadinfousinghttpclient2 = new URLLoadInfousingHTTPClient(s2);
                    urlloadinfousinghttpclient2.setContentType("text/html;charset=" + I18N.nullEncoding());
                    urlloadinfousinghttpclient2.frameName = TextUtils.getValue(hashmap2, "name");
                    addURLToMap(urlloadinfousinghttpclient2);
                }
            } while(true);
            Enumeration enumeration5 = htmltagparser.findTags("iframe");
            do
            {
                if(!enumeration5.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap4 = (HashMap)enumeration5.nextElement();
                String s4 = TextUtils.getValue(hashmap4, "src");
                if(s4.length() > 0)
                {
                    s4 = TextUtils.unescapeHTML(s4);
                    s4 = URLMonitor.resolveURL(s4, urlloadinfousinghttpclient, s);
                    URLLoadInfousingHTTPClient urlloadinfousinghttpclient4 = new URLLoadInfousingHTTPClient(s4);
                    urlloadinfousinghttpclient4.setContentType("text/html;charset=" + I18N.nullEncoding());
                    urlloadinfousinghttpclient4.frameName = TextUtils.getValue(hashmap4, "name");
                    addURLToMap(urlloadinfousinghttpclient4);
                }
            } while(true);
        }
        if(loadImages)
        {
            Enumeration enumeration3 = htmltagparser.findTags("body");
            if(enumeration3.hasMoreElements())
            {
                HashMap hashmap3 = (HashMap)enumeration3.nextElement();
                String s3 = TextUtils.getValue(hashmap3, "background");
                if(s3.length() > 0)
                {
                    s3 = TextUtils.unescapeHTML(s3);
                    s3 = URLMonitor.resolveURL(s3, urlloadinfousinghttpclient, s);
                    URLLoadInfousingHTTPClient urlloadinfousinghttpclient3 = new URLLoadInfousingHTTPClient(s3);
                    addURLToMap(urlloadinfousinghttpclient3);
                }
            }
            Enumeration enumeration6 = htmltagparser.findTags("img");
            do
            {
                if(!enumeration6.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap5 = (HashMap)enumeration6.nextElement();
                String s5 = TextUtils.getValue(hashmap5, "src");
                if(s5.length() > 0)
                {
                    s5 = TextUtils.unescapeHTML(s5);
                    s5 = URLMonitor.resolveURL(s5, urlloadinfousinghttpclient, s);
                    URLLoadInfousingHTTPClient urlloadinfousinghttpclient5 = new URLLoadInfousingHTTPClient(s5);
                    addURLToMap(urlloadinfousinghttpclient5);
                }
            } while(true);
            Enumeration enumeration7 = htmltagparser.findTags("input");
            do
            {
                if(!enumeration7.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap7 = (HashMap)enumeration7.nextElement();
                if(TextUtils.getValue(hashmap7, "type").equals("image"))
                {
                    String s8 = TextUtils.getValue(hashmap7, "src");
                    if(s8.length() > 0)
                    {
                        s8 = TextUtils.unescapeHTML(s8);
                        s8 = URLMonitor.resolveURL(s8, urlloadinfousinghttpclient, s);
                        URLLoadInfousingHTTPClient urlloadinfousinghttpclient6 = new URLLoadInfousingHTTPClient(s8);
                        addURLToMap(urlloadinfousinghttpclient6);
                    }
                }
            } while(true);
            if(flag)
            {
                URLLoadInfousingHTTPClient urlloadinfousinghttpclient8;
                for(Enumeration enumeration8 = htmltagparser.findTags("applet"); enumeration8.hasMoreElements(); addURLToMap(urlloadinfousinghttpclient8))
                {
                    HashMap hashmap8 = (HashMap)enumeration8.nextElement();
                    String s10 = TextUtils.getValue(hashmap8, "codebase");
                    String s11 = TextUtils.getValue(hashmap8, "code");
                    if(s10 == null || s11 == null)
                    {
                        return;
                    }
                    if(!s10.endsWith("/"))
                    {
                        s10 = s10 + "/";
                    }
                    String s12 = s10 + s11;
                    s12 = TextUtils.unescapeHTML(s12);
                    s12 = URLMonitor.resolveURL(s12, urlloadinfousinghttpclient, s);
                    urlloadinfousinghttpclient8 = new URLLoadInfousingHTTPClient(s12);
                }

            }
        }
    }

}
