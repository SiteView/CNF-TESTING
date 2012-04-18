/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLLoader.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLLoader</code>
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
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.HTMLTagParser;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLLoadInfo, URLLoaderThread, URLMonitor

public class URLLoader
{

    AtomicMonitor monitor;
    String proxy;
    String username;
    String password;
    String location;
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
    URLLoaderThread threadList[];
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
        StringBuffer stringbuffer = new StringBuffer();
        long l = 50000L;
        String s6 = "";
        int j = 60000;
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, s, "", "", s1, s2, s3, array, s4, s5, "", stringbuffer, l, s6, 0, j, null);
        long l1 = al[2];
        System.out.println("status=" + al[0]);
        System.out.println("duration=" + al[1] + " ms");
        System.out.println("size=" + al[2] + " bytes");
        HashMap hashmap = new HashMap();
        URLLoader urlloader = new URLLoader(null, socketsession, hashmap, i, true, true, s, "", "", "", "", 50000L, stringbuffer, j, s1, s2, s3, null);
        urlloader.waitForCompletion();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        al = urlloader.getResults("", "", stringbuffer1, stringbuffer2);
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

    public URLLoader(AtomicMonitor atomicmonitor, SocketSession socketsession, HashMap hashmap, int i, boolean flag, boolean flag1, String s, 
            String s1, String s2, String s3, String s4, long l, StringBuffer stringbuffer, 
            int j, String s5, String s6, String s7, PrintWriter printwriter)
    {
        monitor = null;
        proxy = "";
        username = "";
        password = "";
        location = "";
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
            String s8 = atomicmonitor.getProperty(AtomicMonitor.pClass);
            if(s8.indexOf("Remote") > 0)
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
        otherHeaders = s3;
        location = s4;
        contentMax = l;
        timeout = j;
        proxy = s5;
        proxyUsername = s6;
        proxyPassword = s7;
        traceStream = printwriter;
        URLLoadInfo urlloadinfo = new URLLoadInfo(s);
        urlloadinfo.contents = stringbuffer.toString();
        parse(urlloadinfo);
        threadList = new URLLoaderThread[i];
        URLLoaderThread urlloaderthread = new URLLoaderThread(this, socketsession, false);
        threadList[0] = urlloaderthread;
        for(int k = 1; k < i; k++)
        {
            SocketSession socketsession1 = SocketSession.getSession(null);
            socketsession1.cookies = socketsession.cookies;
            URLLoaderThread urlloaderthread1 = new URLLoaderThread(this, socketsession1, true);
            threadList[k] = urlloaderthread1;
        }

        if(i > 1)
        {
            String s9 = "* no monitor *";
            if(atomicmonitor != null)
            {
                s9 = atomicmonitor.getProperty(URLMonitor.pName);
            }
            LogManager.log("RunMonitor", "Starting " + i + " threaded frame/image load for " + s9);
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
                    String s10 = "URLLoaderThread #" + (i1 + 1);
                    if(atomicmonitor != null)
                    {
                        s10 = s10 + " for " + atomicmonitor.getProperty(URLMonitor.pName);
                    }
                    Thread thread = new Thread(threadList[i1]);
                    thread.setName(s10);
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
            URLLoadInfo urlloadinfo;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break label0;
                }
                urlloadinfo = (URLLoadInfo)enumeration.nextElement();
            } while(urlloadinfo.done);
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
            URLLoadInfo urlloadinfo = (URLLoadInfo)enumeration.nextElement();
            if(urlloadinfo.done)
            {
                if(l == (long)URLMonitor.kURLok)
                {
                    l = urlloadinfo.status;
                }
                l1 += urlloadinfo.duration;
                l2 += urlloadinfo.size;
                l3 += urlloadinfo.dnsTime;
                l4 += urlloadinfo.connectTime;
                l5 += urlloadinfo.responseTime;
                l6 += urlloadinfo.downloadTime;
                if(urlloadinfo.contentType.equals("text/html"))
                {
                    l7 += urlloadinfo.loadCount;
                    l8 += urlloadinfo.size;
                    if(urlloadinfo.status != URLMonitor.kURLok)
                    {
                        if(stringbuffer.length() > 0)
                        {
                            stringbuffer.append("\t");
                        }
                        stringbuffer.append(URLMonitor.lookupStatus(urlloadinfo.status) + ", " + urlloadinfo.url);
                        l9++;
                    } else
                    {
                        if(s.length() > 0)
                        {
                            int i = TextUtils.matchExpression(urlloadinfo.contents, s);
                            if(i != Monitor.kURLok)
                            {
                                String s2 = URLMonitor.getHTMLEncoding(urlloadinfo.contents);
                                TextUtils.matchExpression(urlloadinfo.contents, I18N.UnicodeToString(s, s2));
                            }
                            if(i == URLMonitor.kURLok)
                            {
                                contentMatches.add(urlloadinfo);
                            }
                        }
                        if(s1.length() != 0)
                        {
                            int j = TextUtils.matchExpression(urlloadinfo.contents, s1);
                            if(j != Monitor.kURLok)
                            {
                                String s3 = URLMonitor.getHTMLEncoding(urlloadinfo.contents);
                                TextUtils.matchExpression(urlloadinfo.contents, I18N.UnicodeToString(s1, s3));
                            }
                            if(j == Monitor.kURLok)
                            {
                                errorMatches.add(urlloadinfo);
                            }
                        }
                    }
                } else
                {
                    l10 += urlloadinfo.loadCount;
                    l11 += (int)urlloadinfo.size;
                    if(urlloadinfo.status != URLMonitor.kURLok)
                    {
                        if(stringbuffer1.length() > 0)
                        {
                            stringbuffer1.append("\t");
                        }
                        stringbuffer1.append(URLMonitor.lookupStatus(urlloadinfo.status) + ", " + urlloadinfo.url);
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

    void addToSummary(URLLoadInfo urlloadinfo, StringBuffer stringbuffer)
    {
        stringbuffer.append("<TR><TD>");
        stringbuffer.append(urlloadinfo.url);
        stringbuffer.append("<TD>");
        stringbuffer.append(URLMonitor.lookupStatus(urlloadinfo.status));
        stringbuffer.append("<TD>");
        stringbuffer.append(URLMonitor.pRoundTripTime.valueString("" + urlloadinfo.duration, 2));
        stringbuffer.append("<TD>");
        stringbuffer.append(TextUtils.bytesToString(urlloadinfo.size));
        stringbuffer.append("<TD>");
        stringbuffer.append(urlloadinfo.loadCount);
        stringbuffer.append("<TD>");
        stringbuffer.append(urlloadinfo.count);
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
        Enumeration enumeration = urlList.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            URLLoadInfo urlloadinfo = (URLLoadInfo)enumeration.nextElement();
            if(urlloadinfo.contentType.equals("text/html"))
            {
                addToSummary(urlloadinfo, stringbuffer1);
                flag = true;
                if(urlloadinfo.contents.length() > 0)
                {
                    stringbuffer2.append("\n<HR><B>Frame: " + urlloadinfo.url + "</B><HR><PRE>\n");
                    stringbuffer2.append(TextUtils.escapeHTML(urlloadinfo.contents));
                    stringbuffer2.append("</PRE><P>\n");
                }
            } else
            {
                addToSummary(urlloadinfo, stringbuffer);
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
            URLLoadInfo urlloadinfo = (URLLoadInfo)contentMatches.get(0);
            return urlloadinfo.contents;
        } else
        {
            return "";
        }
    }

    public String getErrorMatchContents()
    {
        if(errorMatches.size() > 0)
        {
            URLLoadInfo urlloadinfo = (URLLoadInfo)errorMatches.get(0);
            return urlloadinfo.contents;
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

    synchronized URLLoadInfo nextURL(URLLoaderThread urlloaderthread)
    {
        if(stopped)
        {
            return null;
        }
        for(Enumeration enumeration = urlMap.elements(); enumeration.hasMoreElements();)
        {
            URLLoadInfo urlloadinfo = (URLLoadInfo)enumeration.nextElement();
            if(urlloadinfo.needsLoading())
            {
                urlloadinfo.thread = urlloaderthread;
                progressMessage("Retrieving " + urlloadinfo.url);
                return urlloadinfo;
            }
        }

        return null;
    }

    synchronized void updateCookies(SocketSession socketsession, URLLoadInfo urlloadinfo)
    {
        socketsession.updateCookies(urlloadinfo.contents, urlloadinfo.url);
    }

    synchronized void addURLToMap(URLLoadInfo urlloadinfo)
    {
        URLLoadInfo urlloadinfo1 = (URLLoadInfo)urlMap.get(urlloadinfo.url);
        boolean flag = false;
        if(urlloadinfo1 != null)
        {
            urlloadinfo1.count++;
            if(urlloadinfo1.isLoaded() && (!urlloadinfo1.cache || urlloadinfo1.status != URLMonitor.kURLok))
            {
                urlloadinfo1.setLoadAgain();
                flag = true;
            }
        } else
        {
            urlMap.put(urlloadinfo.url, urlloadinfo);
            urlList.add(urlloadinfo);
            urlloadinfo.count = 1;
            flag = true;
        }
        if(flag)
        {
            notify();
        }
    }

    void parse(URLLoadInfo urlloadinfo)
    {
        HTMLTagParser htmltagparser = new HTMLTagParser(URLMonitor.getHTTPContent(urlloadinfo.contents), TARGET_TAGS, true);
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
                    s1 = URLMonitor.resolveURL(s1, urlloadinfo, s);
                    URLLoadInfo urlloadinfo1 = new URLLoadInfo(s1);
                    addURLToMap(urlloadinfo1);
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
                        s9 = URLMonitor.resolveURL(s9, urlloadinfo, s);
                        URLLoadInfo urlloadinfo7 = new URLLoadInfo(s9);
                        addURLToMap(urlloadinfo7);
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
                    s2 = URLMonitor.resolveURL(s2, urlloadinfo, s);
                    URLLoadInfo urlloadinfo2 = new URLLoadInfo(s2);
                    urlloadinfo2.setContentType("text/html");
                    urlloadinfo2.frameName = TextUtils.getValue(hashmap2, "name");
                    addURLToMap(urlloadinfo2);
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
                    s4 = URLMonitor.resolveURL(s4, urlloadinfo, s);
                    URLLoadInfo urlloadinfo4 = new URLLoadInfo(s4);
                    urlloadinfo4.setContentType("text/html");
                    urlloadinfo4.frameName = TextUtils.getValue(hashmap4, "name");
                    addURLToMap(urlloadinfo4);
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
                    s3 = URLMonitor.resolveURL(s3, urlloadinfo, s);
                    URLLoadInfo urlloadinfo3 = new URLLoadInfo(s3);
                    addURLToMap(urlloadinfo3);
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
                    s5 = URLMonitor.resolveURL(s5, urlloadinfo, s);
                    URLLoadInfo urlloadinfo5 = new URLLoadInfo(s5);
                    addURLToMap(urlloadinfo5);
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
                        s8 = URLMonitor.resolveURL(s8, urlloadinfo, s);
                        URLLoadInfo urlloadinfo6 = new URLLoadInfo(s8);
                        addURLToMap(urlloadinfo6);
                    }
                }
            } while(true);
            if(flag)
            {
                URLLoadInfo urlloadinfo8;
                for(Enumeration enumeration8 = htmltagparser.findTags("applet"); enumeration8.hasMoreElements(); addURLToMap(urlloadinfo8))
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
                    s12 = URLMonitor.resolveURL(s12, urlloadinfo, s);
                    urlloadinfo8 = new URLLoadInfo(s12);
                }

            }
        }
    }

}
