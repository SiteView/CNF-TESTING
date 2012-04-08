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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;

import org.apache.commons.httpclient.Cookie;

import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Utils:
// URLInfo, I18N, FileUtils, TextUtils

public class URLContext {

    private COM.dragonflow.SiteView.Monitor context;

    private jgl.Array cookies;

    private String refererURL;

    private String encodingForStream;

    private String redirectBase;

    static boolean debug = false;

    private String encodePostData;

    private String match;

    private String errorContent;

    private jgl.Array postData;

    private StringBuffer contentBuffer;

    private long contentMax;

    private int currentRedirect;

    private long timedOut;

    private StringBuffer redirectBuffer;

    private StringBuffer sessionBuffer;

    static final boolean $assertionsDisabled; /* synthetic field */

    public String getMatch() {
        return match;
    }

    public void setMatch(String s) {
        match = s;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(String s) {
        errorContent = s;
    }

    public jgl.Array getPostData() {
        return postData;
    }

    public void setPostData(jgl.Array array) {
        postData = array;
    }

    public StringBuffer getContentBuffer() {
        return contentBuffer;
    }

    public void setContentBuffer(StringBuffer stringbuffer) {
        contentBuffer = stringbuffer;
    }

    public long getContentMax() {
        return contentMax;
    }

    public void setContentMax(long l) {
        contentMax = l;
    }

    public int getCurrentRedirect() {
        return currentRedirect;
    }

    public void setCurrentRedirect(int i) {
        currentRedirect = i;
    }

    public long getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(long l) {
        timedOut = l;
    }

    public StringBuffer getRedirectBuffer() {
        return redirectBuffer;
    }

    public void setRedirectBuffer(StringBuffer stringbuffer) {
        redirectBuffer = stringbuffer;
    }

    public StringBuffer getSessionBuffer() {
        return sessionBuffer;
    }

    public void setSessionBuffer(StringBuffer stringbuffer) {
        sessionBuffer = stringbuffer;
    }

    public URLContext(COM.dragonflow.SiteView.Monitor monitor) {
        context = null;
        cookies = new Array();
        refererURL = "";
        encodingForStream = COM.dragonflow.Utils.I18N.nullEncoding();
        redirectBase = "";
        encodePostData = null;
        context = monitor;
        if (context == null) {
            context = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        }
    }

    public COM.dragonflow.SiteView.Monitor getMonitor() {
        return context;
    }

    public String getRefererURL() {
        return refererURL;
    }

    public void setRefererURL(String s) {
        refererURL = s;
    }

    public void setStreamEncoding(String s) {
        if (s != null) {
            encodingForStream = new String(s);
        }
    }

    public String getStreamEncoding() {
        return new String(encodingForStream);
    }

    public String getRedirectBase() {
        return redirectBase;
    }

    public void setRedirectBase(String s) {
        redirectBase = s;
    }

    public String getEncodePostData() {
        if (encodePostData == null) {
            return COM.dragonflow.StandardMonitor.URLMonitor.urlencodedDropDown[0];
        } else {
            return encodePostData;
        }
    }

    public void setEncodePostData(String s) {
        encodePostData = s;
    }

    public jgl.Array getCookies() {
        return cookies;
    }

    public void setCookies(jgl.Array array) {
        cookies = array;
    }

    private static String addPeriodToDomainIfNeeded(String s) {
        if (s != null && !s.startsWith(".")) {
            s = "." + s;
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @return
     */
    private static boolean expiredCookie(jgl.HashMap hashmap) {
        try {
            java.util.Date date;
            java.util.Date date1;
            String s = (String) hashmap.get("expires");
            if (s != null) {
                date = new Date(s);
                date1 = new Date();
                if (date.before(date1)) {
                    return true;
                }
            }
        } catch (java.lang.Exception exception) {
            /* empty */
        }
        return false;
    }

    private jgl.Array addCookie(jgl.Array array, jgl.HashMap hashmap) {
        boolean flag = false;
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
            java.lang.Exception exception = new Exception();
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie(): " + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
        }
        boolean flag1 = COM.dragonflow.Utils.URLContext.expiredCookie(hashmap);
        if (flag1 && (COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - expired-cookie");
        }
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "key");
        int i = 0;
        do {
            if (i >= array.size()) {
                break;
            }
            jgl.HashMap hashmap1 = (jgl.HashMap) array.at(i);
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           key=" + hashmap1.get("key"));
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           value=" + hashmap1.get("value"));
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           domain=" + hashmap1.get("domain"));
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           path=" + hashmap1.get("path"));
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           expires=" + hashmap1.get("expires"));
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - OldCookie at(" + i + ")           secure=" + hashmap1.get("secure"));
            }
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key").equalsIgnoreCase(s) && COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "domain").equalsIgnoreCase(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "domain"))
                    && COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path").equalsIgnoreCase(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "path"))) {
                if (flag1) {
                    array.remove(i);
                    if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() EXPIRED - remove-cookie");
                    }
                } else {
                    if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - old-cookie");
                    }
                    array.put(i, hashmap);
                    if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - replace-cookie");
                    }
                    flag = true;
                }
                break;
            }
            i ++;
        } while (true);
        if (!flag1 && !flag) {
            array.add(hashmap);
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - append-cookie");
            }
        }
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            key=" + hashmap.get("key"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            value=" + hashmap.get("value"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            domain=" + hashmap.get("domain"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            path=" + hashmap.get("path"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            expires=" + hashmap.get("expires"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.addCookie() - newCookie            secure=" + hashmap.get("secure"));
        }
        return array;
    }

    public static void printCookie(jgl.Array array) {
        COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() START");
        java.lang.Exception exception = new Exception();
        COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie(): " + COM.dragonflow.Utils.FileUtils.stackTraceText(exception));
        for (int i = 0; i < array.size(); i ++) {
            jgl.HashMap hashmap = (jgl.HashMap) array.at(i);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           key=" + hashmap.get("key"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           value=" + hashmap.get("value"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           domain=" + hashmap.get("domain"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           path=" + hashmap.get("path"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           expires=" + hashmap.get("expires"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "URLContext.printCookie() - cookie at(" + i + ")           secure=" + hashmap.get("secure"));
        }

    }

    private static jgl.HashMap MakeCookie(COM.dragonflow.Utils.URLInfo urlinfo) {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        if (urlinfo != null) {
            hashmapordered.put("domain", urlinfo.getHost());
            String s = urlinfo.getFile();
            int i = s.indexOf('?');
            if (i > 0) {
                s = s.substring(0, i);
            }
            if (!s.endsWith("/")) {
                int j = s.lastIndexOf('/');
                if (j != -1) {
                    s = s.substring(0, j);
                }
            }
            hashmapordered.put("path", s);
        }
        return hashmapordered;
    }

    private void updateOrAddOneCookie(jgl.Array array, String s, String s1) {
        if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(s, COM.dragonflow.StandardMonitor.URLMonitor.SET_COOKIE_HEADER)) {
            if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "parse-cookie, line=" + s + ", url=" + s1);
            }
            boolean flag = true;
            COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo(s1);
            jgl.HashMap hashmap = COM.dragonflow.Utils.URLContext.MakeCookie(urlinfo);
            String as[] = COM.dragonflow.Utils.TextUtils.split(s.substring(COM.dragonflow.StandardMonitor.URLMonitor.SET_COOKIE_HEADER.length()).trim(), ";");
            for (int i = 0; i < as.length; i ++) {
                as[i] = as[i].trim();
                if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "path=")) {
                    hashmap.put("path", as[i].substring("path=".length()));
                    continue;
                }
                if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "domain=")) {
                    String s2 = as[i].substring("domain=".length());
                    int k = s2.indexOf(":");
                    if (k != -1) {
                        s2 = s2.substring(0, k);
                    }
                    if (!urlinfo.getHost().equals(s2)) {
                        s2 = COM.dragonflow.Utils.URLContext.addPeriodToDomainIfNeeded(s2);
                    }
                    hashmap.put("domain", s2);
                    continue;
                }
                if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "secure") && as[i].indexOf("=") == -1) {
                    hashmap.put("secure", "");
                    continue;
                }
                if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "expires=")) {
                    hashmap.put("expires", as[i].substring("expires=".length()));
                    continue;
                }
                if (as[i].indexOf("=") != -1 && flag) {
                    flag = false;
                    int j = as[i].indexOf("=");
                    hashmap.put("key", as[i].substring(0, j));
                    hashmap.put("value", as[i].substring(j + 1));
                }
            }

            addCookie(array, hashmap);
        }
    }

    public void updateCookies(String s, String s1) {
        String s2 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPHeaders(s);
        int i = 0;
        int j;
        do {
            j = s2.indexOf('\n', i + 1);
            String s3;
            if (j == -1) {
                s3 = s2.substring(i, s2.length()).trim();
            } else {
                s3 = s2.substring(i, j).trim();
            }
            updateOrAddOneCookie(cookies, s3, s1);
            i = j;
        } while (j != -1);
    }

    public void addCookieParameters(jgl.Array array, String s) {
        if (array != null) {
            String s1;
            for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); updateOrAddOneCookie(cookies, s1, s)) {
                s1 = (String) enumeration.nextElement();
            }

        }
    }

    public java.util.Vector getCookieHeader(String s) {
        java.util.Vector vector = new Vector();
        jgl.HashMap hashmap = getCookies(s);
        if (hashmap != null) {
            Enumeration enumeration = cookies.elements();
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap1 = (jgl.HashMap) enumeration.nextElement();
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key");
                if (hashmap.get(s1) == hashmap1) {
                    String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "domain");
                    String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key");
                    String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path");
                    java.util.Date date = null;
                    String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "expires");
                    if (s6.length() > 0) {
                        try {
                            java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat();
                            date = simpledateformat.parse(s6);
                        } catch (java.lang.Exception exception) {
                            date = null;
                        }
                    }
                    org.apache.commons.httpclient.Cookie cookie = new Cookie(s2, s3, s4, s5, date, false);
                    vector.add(cookie);
                }
            } 
            if (!$assertionsDisabled && hashmap.size() != vector.size()) {
                throw new AssertionError(" URL: " + s + " We meant to send: " + hashmap.size() + " cookies, but only sent: " + vector.size());
            }
        }
        return vector;
    }

    private jgl.HashMap getCookies(String s) {
        if ((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "get-cookie=" + s);
        }
        COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo(s);
        if (cookies == null) {
            return null;
        }
        Enumeration enumeration = cookies.elements();
        jgl.HashMap hashmap = new HashMap();
        while (enumeration.hasMoreElements()) {
            jgl.HashMap hashmap1 = (jgl.HashMap) enumeration.nextElement();
            if (!urlinfo.getProtocol().equalsIgnoreCase("http") || hashmap1.get("secure") == null) {
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key");
                jgl.HashMap hashmap2 = (jgl.HashMap) hashmap.get(s1);
                if (hashmap2 != null) {
                    if (COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path").length() > COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "path").length()) {
                        hashmap.put(s1, hashmap1);
                    }
                } else {
                    hashmap.put(s1, hashmap1);
                }
            }
        } 
        return hashmap;
    }

    static {
        $assertionsDisabled = !(COM.dragonflow.Utils.URLContext.class).desiredAssertionStatus();
        debug = java.lang.System.getProperty("URLContext.debug") != null;
    }
}
