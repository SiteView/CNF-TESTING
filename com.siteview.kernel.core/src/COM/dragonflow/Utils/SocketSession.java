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

import java.util.Date;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

import org.apache.commons.httpclient.Header;

import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Utils:
// SocketStream, URLInfo, CommandLine, FileUtils,
// SSLSocketStream, TextUtils

public class SocketSession
{

    public static boolean hasJavaSSL = false;
    HashMap cache;
    long maxCachedSockets;
    public COM.dragonflow.SiteView.Monitor context;
    public boolean inRemoteRequest;
    public Array cookies;
    public String originalUserName;
    public String originalPassword;
    public String refererURL;
    private String encodePostData;
    private String domain;
    private String authenticationWhenRequested;
    public boolean allowClose;
    static boolean debug = false;
    public String certFilename;
    public String certPassword;
    String encodingForStream;
    public java.io.PrintWriter sslOut;
    public java.io.BufferedReader sslIn;
    public java.lang.Process sslProcess;
    public String sslKey;
    private byte byteBuffer[];
    private char charBuffer[];

    public static COM.dragonflow.Utils.SocketSession getSession(COM.dragonflow.SiteView.Monitor monitor)
    {
        COM.dragonflow.Utils.SocketSession socketsession = null;
        if(monitor != null)
        {
            socketsession = monitor.cachedSocketSession;
        }
        if(socketsession == null)
        {
            socketsession = new SocketSession();
        }
        socketsession.initialize(monitor);
        return socketsession;
    }

    private SocketSession()
    {
        cache = new HashMap();
        maxCachedSockets = 0L;
        context = null;
        inRemoteRequest = false;
        cookies = new Array();
        originalUserName = "";
        originalPassword = "";
        refererURL = "";
        encodePostData = null;
        domain = "";
        authenticationWhenRequested = "";
        allowClose = true;
        certFilename = "";
        certPassword = "";
        encodingForStream = "";
        sslOut = null;
        sslIn = null;
        sslProcess = null;
        sslKey = "";
        byteBuffer = null;
        charBuffer = null;
    }

    public void setStreamEncoding(String s)
    {
        if(s != null)
        {
            encodingForStream = new String(s);
        }
    }

    public String getStreamEncoding()
    {
        return new String(encodingForStream);
    }

    public String getEncodePostData()
    {
        if(encodePostData == null)
        {
            return COM.dragonflow.StandardMonitor.URLMonitor.urlencodedDropDown[0];
        } else
        {
            return encodePostData;
        }
    }

    public void setEncodePostData(String s)
    {
        encodePostData = s;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String s)
    {
        domain = s;
    }

    public String getAuthenticationWhenRequested()
    {
        if(authenticationWhenRequested == null)
        {
            return COM.dragonflow.StandardMonitor.URLMonitor.authOn401DropDown[0];
        } else
        {
            return authenticationWhenRequested;
        }
    }

    public void setAuthenticationWhenRequested(String s)
    {
        authenticationWhenRequested = s;
    }

    private void initialize(COM.dragonflow.SiteView.Monitor monitor)
    {
        context = monitor;
        if(context == null)
        {
            context = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        }
        certFilename = context.getSetting("_urlClientCert");
        if(certFilename.length() > 0)
        {
            certPassword = context.getSetting("_urlClientCertPassword");
            String s = context.getSetting("groupID");
            String s1 = "templates.certificates";
            if(s.length() > 0)
            {
                certFilename = COM.dragonflow.SiteView.Platform.getUsedDirectoryPath(s1, s) + java.io.File.separator + certFilename;
            } else
            {
                certFilename = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + s1 + java.io.File.separator + certFilename;
            }
        }
        maxCachedSockets = 4L;
        long l = context.getSettingAsLong("_urlKeepAlive");
        if(l < 0L)
        {
            maxCachedSockets = 0L;
        } else
        if(l > 0L)
        {
            maxCachedSockets = l;
        }
        if(debug)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession created, max=" + maxCachedSockets);
        }
    }

    public byte[] getByteBuffer()
    {
        if(byteBuffer == null)
        {
            byteBuffer = new byte[16384];
        }
        return byteBuffer;
    }

    public char[] getCharBuffer()
    {
        if(charBuffer == null)
        {
            charBuffer = new char[16384];
        }
        return charBuffer;
    }

    void closeSSL()
    {
        if(sslProcess != null)
        {
            try
            {
                sslProcess.destroy();
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "ssl process close, " + sslKey + ", " + exception);
            }
            sslProcess = null;
        }
        if(sslOut != null)
        {
            try
            {
                sslOut.close();
            }
            catch(java.lang.Exception exception1)
            {
                COM.dragonflow.Log.LogManager.log("Error", "ssl output close, " + sslKey + ", " + exception1);
            }
            sslOut = null;
        }
        if(sslIn != null)
        {
            try
            {
                sslIn.close();
            }
            catch(java.lang.Exception exception2)
            {
                COM.dragonflow.Log.LogManager.log("Error", "ssl input close, " + sslKey + ", " + exception2);
            }
            sslIn = null;
        }
        sslKey = "";
    }

    public boolean isSSLKeepAliveConnection(String as[], String s)
        throws java.io.IOException
    {
        if(sslProcess != null && sslKey.equals(s))
        {
            return true;
        }
        closeSSL();
        sslProcess = COM.dragonflow.Utils.CommandLine.execSync(as);
        if(encodingForStream.length() == 0)
        {
            sslOut = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(sslProcess.getOutputStream());
            sslIn = COM.dragonflow.Utils.FileUtils.MakeInputReader(sslProcess.getInputStream());
        } else
        {
            sslOut = COM.dragonflow.Utils.FileUtils.MakeEncodedOutputWriter(sslProcess.getOutputStream(), encodingForStream);
            sslIn = COM.dragonflow.Utils.FileUtils.MakeEncodedInputReader(sslProcess.getInputStream(), encodingForStream);
        }
        sslKey = s;
        return false;
    }

    public COM.dragonflow.Utils.SocketStream get(String s)
    {
        return (COM.dragonflow.Utils.SocketStream)cache.get(s);
    }

    public void put(String s, COM.dragonflow.Utils.SocketStream socketstream)
    {
        cache.put(s, socketstream);
    }

    public COM.dragonflow.Utils.SocketStream connect(String s, java.net.InetAddress inetaddress, int i, String s1)
        throws java.io.IOException
    {
        return connect(s, inetaddress, i, s1, null, null, -1, null, null, null, -1L);
    }

    public COM.dragonflow.Utils.SocketStream connect(String s, java.net.InetAddress inetaddress, int i, String s1, String s2, int j, String s3, 
            String s4, long l)
        throws java.io.IOException
    {
        return connect(s, inetaddress, i, s1, null, s2, j, null, s3, s4, l);
    }

    public COM.dragonflow.Utils.SocketStream connect(String s, java.net.InetAddress inetaddress, int i, String s1, java.net.InetAddress inetaddress1, String s2, int j, 
            String s3, String s4, String s5, long l)
        throws java.io.IOException
    {
        COM.dragonflow.Utils.SocketStream socketstream = (COM.dragonflow.Utils.SocketStream)cache.get(s);
        if(socketstream == null)
        {
            socketstream = new SocketStream(this, s, inetaddress, i, s1, s2, j, s4, s5, l, encodingForStream);
            if(debug)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.getSocket, created, " + s + ", " + socketstream.socket);
            }
        } else
        if(debug)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.getSocket, cached, " + s + ", " + socketstream.socket);
        }
        return socketstream;
    }

    public void release(COM.dragonflow.Utils.SocketStream socketstream)
    {
        if(socketstream.keepAlive && !socketstream.receivedEndOfStream && maxCachedSockets != 0L)
        {
            if(debug)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.releaseSocket, cached, " + socketstream.key + ", " + socketstream.socket);
            }
            cache.put(socketstream.key, socketstream);
            if((long)cache.size() > maxCachedSockets)
            {
                Enumeration enumeration = cache.elements();
                COM.dragonflow.Utils.SocketStream socketstream1 = (COM.dragonflow.Utils.SocketStream)enumeration.nextElement();
                if(debug)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.releaseSocket, expired, " + socketstream1.key);
                }
                socketstream1.close();
                cache.remove(socketstream1.key);
            }
        } else
        {
            if(debug)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.releaseSocket, closed, " + socketstream.key + ", " + socketstream.socket);
            }
            if(context == null)
            {
                context = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            }
            if(context.getSetting("_sslSessionKeepAlive").length() <= 0)
            {
                COM.dragonflow.Utils.SSLSocketStream.invalidateSSLSession(socketstream.socket);
            }
            socketstream.close();
            if(cache != null)
            {
                cache.remove(socketstream.key);
            }
        }
    }

    public String getVersion()
    {
        if(maxCachedSockets > 0L)
        {
            return "1.1";
        } else
        {
            return "1.0";
        }
    }

    public void close()
    {
        if(allowClose)
        {
            if(cache != null)
            {
                COM.dragonflow.Utils.SocketStream socketstream;
                for(Enumeration enumeration = cache.elements(); enumeration.hasMoreElements(); socketstream.close())
                {
                    socketstream = (COM.dragonflow.Utils.SocketStream)enumeration.nextElement();
                    if(debug)
                    {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.closeSocketCache, closed, " + socketstream.key + ", " + socketstream.socket);
                    }
                }

                cache.clear();
            }
            closeSSL();
            if(debug)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "SocketSession.socketSession, closed");
            }
        }
    }

    /**
     * 
     * 
     * @param hashmap
     * @return
     */
    private static boolean expiredCookie(HashMap hashmap)
    {
        java.util.Date date;
        java.util.Date date1;
        try {
        String s = (String)hashmap.get("expires");
        if(s != null)
        {
        date = new Date(s);
        date1 = new Date();
        if(date.before(date1))
        {
            return true;
        }
        }
        }
        catch (java.lang.Exception exception) {
            /* empty */
        }
        return false;
    }

    private static Array addCookie(Array array, HashMap hashmap)
    {
        boolean flag = false;
        boolean flag1 = COM.dragonflow.Utils.SocketSession.expiredCookie(hashmap);
        if(flag1 && (COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "expired-cookie");
        }
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "key");
        int i = 0;
        do
        {
            if(i >= array.size())
            {
                break;
            }
            HashMap hashmap1 = (HashMap)array.get(i);
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key").equalsIgnoreCase(s) && COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "domain").equalsIgnoreCase(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "domain")) && COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path").equalsIgnoreCase(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "path")))
            {
                if(flag1)
                {
                    array.remove(i);
                    if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
                    {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "remove-cookie");
                    }
                } else
                {
                    array.put(i, hashmap);
                    if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
                    {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "replace-cookie");
                    }
                    flag = true;
                }
                break;
            }
            i++;
        } while(true);
        if(!flag1 && !flag)
        {
            array.add(hashmap);
            if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "append-cookie");
            }
        }
        if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            key=" + hashmap.get("key"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            value=" + hashmap.get("value"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            domain=" + hashmap.get("domain"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            path=" + hashmap.get("path"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            expires=" + hashmap.get("expires"));
            COM.dragonflow.Log.LogManager.log("RunMonitor", "            secure=" + hashmap.get("secure"));
        }
        return array;
    }

    private static HashMap MakeCookie(String s)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        if(s != null)
        {
            COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo(s);
            hashmapordered.put("domain", urlinfo.getHost());
            String s1 = urlinfo.getFile();
            int i = s1.indexOf('?');
            if(i > 0)
            {
                s1 = s1.substring(0, i);
            }
            if(!s1.endsWith("/"))
            {
                int j = s1.lastIndexOf('/');
                if(j != -1)
                {
                    s1 = s1.substring(0, j);
                }
            }
            hashmapordered.put("path", s1);
        }
        return hashmapordered;
    }

    private static void update1Cookie(Array array, String s, String s1)
    {
        if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(s, COM.dragonflow.StandardMonitor.URLMonitor.SET_COOKIE_HEADER))
        {
            if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "parse-cookie, line=" + s + ", url=" + s1);
            }
            boolean flag = true;
            HashMap hashmap = COM.dragonflow.Utils.SocketSession.MakeCookie(s1);
            String as[] = COM.dragonflow.Utils.TextUtils.split(s.substring(COM.dragonflow.StandardMonitor.URLMonitor.SET_COOKIE_HEADER.length()).trim(), ";");
            for(int i = 0; i < as.length; i++)
            {
                as[i] = as[i].trim();
                if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "path="))
                {
                    hashmap.put("path", as[i].substring("path=".length()));
                    continue;
                }
                if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "domain="))
                {
                    String s2 = as[i].substring("domain=".length());
                    int k = s2.indexOf(":");
                    if(k != -1)
                    {
                        s2 = s2.substring(0, k);
                    }
                    hashmap.put("domain", s2);
                    continue;
                }
                if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "secure") && as[i].indexOf("=") == -1)
                {
                    hashmap.put("secure", "");
                    continue;
                }
                if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(as[i], "expires="))
                {
                    hashmap.put("expires", as[i].substring("expires=".length()));
                    continue;
                }
                if(as[i].indexOf("=") != -1 && flag)
                {
                    flag = false;
                    int j = as[i].indexOf("=");
                    hashmap.put("key", as[i].substring(0, j));
                    hashmap.put("value", as[i].substring(j + 1));
                }
            }

            COM.dragonflow.Utils.SocketSession.addCookie(array, hashmap);
        }
    }

    public void updateCookies(String s, String s1)
    {
        String s2 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPHeaders(s);
        int i = 0;
        int j;
        do
        {
            j = s2.indexOf('\n', i + 1);
            String s3;
            if(j == -1)
            {
                s3 = s2.substring(i, s2.length()).trim();
            } else
            {
                s3 = s2.substring(i, j).trim();
            }
            COM.dragonflow.Utils.SocketSession.update1Cookie(cookies, s3, s1);
            i = j;
        } while(j != -1);
    }

    public void addCookieParameters(Array array, String s)
    {
        if(array != null)
        {
            String s1;
            for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); COM.dragonflow.Utils.SocketSession.update1Cookie(cookies, s1, s))
            {
                s1 = (String)enumeration.nextElement();
            }

        }
    }

    public org.apache.commons.httpclient.Header getCookieHeader(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        org.apache.commons.httpclient.Header header = null;
        HashMap hashmap = getCookies(s);
        if(hashmap != null)
        {
            Enumeration enumeration = (Enumeration) cookies.iterator();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap)enumeration.nextElement();
                if(hashmap.get(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key")) == hashmap1)
                {
                    String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key") + "=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    if(stringbuffer.length() > 0)
                    {
                        stringbuffer.append("; ");
                    }
                    stringbuffer.append(s1);
                }
            } 
            header = new Header("Cookie", stringbuffer.toString());
        }
        return header;
    }

    private HashMap getCookies(String s)
    {
        if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "get-cookie=" + s);
        }
        COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo(s);
        if(cookies == null)
        {
            return null;
        }
        Enumeration enumeration = (Enumeration) cookies.iterator();
        HashMap hashmap = new HashMap();
        while (enumeration.hasMoreElements()) {
            HashMap hashmap1 = (HashMap)enumeration.nextElement();
            String s1 = urlinfo.getHost();
            if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(COM.dragonflow.Utils.TextUtils.reverse(s1), COM.dragonflow.Utils.TextUtils.reverse(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "domain"))) && COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(urlinfo.getFile(), COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path")) && (!urlinfo.getProtocol().equalsIgnoreCase("http") || hashmap1.get("secure") == null))
            {
                String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key");
                HashMap hashmap2 = (HashMap)hashmap.get(s2);
                if(hashmap2 != null)
                {
                    if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "path").length() > COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "path").length())
                    {
                        hashmap.put(s2, hashmap1);
                    }
                } else
                {
                    hashmap.put(s2, hashmap1);
                }
            }
        } 
        return hashmap;
    }

    public String getCookieHeader(String s, boolean flag)
    {
        HashMap hashmap = getCookies(s);
        String s1 = "";
        if(hashmap != null)
        {
            Enumeration enumeration = (Enumeration) cookies.iterator();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap)enumeration.nextElement();
                if(hashmap.get(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key")) == hashmap1)
                {
                    if(s1.length() > 0)
                    {
                        if(flag)
                        {
                            s1 = s1 + COM.dragonflow.StandardMonitor.URLMonitor.CRLF + "Cookie: ";
                        } else
                        {
                            s1 = s1 + "; ";
                        }
                    } else
                    {
                        s1 = "Cookie: ";
                    }
                    s1 = s1 + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "key") + "=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                }
            } 
            if(s1.length() > 0)
            {
                s1 = s1 + COM.dragonflow.StandardMonitor.URLMonitor.CRLF;
            }
            if((COM.dragonflow.StandardMonitor.URLMonitor.debugURL & COM.dragonflow.StandardMonitor.URLMonitor.kDebugCookie) != 0)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "cookie-header=" + s1);
            }
        }
        return s1;
    }

    static 
    {
        debug = java.lang.System.getProperty("SocketSession.debug") != null;
    }
}
