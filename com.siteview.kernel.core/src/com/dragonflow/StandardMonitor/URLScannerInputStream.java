/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLScannerInputStream.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLScannerInputStream</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.util.Date;
import java.util.Enumeration;

import com.recursionsw.jgl.HashMap;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.HTMLTagParser;
import com.dragonflow.Utils.TextUtils;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLMonitor

public class URLScannerInputStream
{

    static final String HTTP_VERSION = "http/";
    static final String LOCATION = "location:";
    static final String LAST_MODIFIED = "last-modified:";
    static final String DATE = "date:";
    static final String CONTENT_LENGTH = "content-length:";
    static final String KEEP_ALIVE = "connection:";
    private StringBuffer buffer;
    private String keepAliveLine;
    private String versionLine;
    public long status;
    public long contentLength;
    public long lastModified;
    public long date;
    public String location;
    public boolean refreshRedirect;
    static final String REFRESH_TAGS[] = {
        "META", "/HEAD"
    };
    static final String REFRESH_URL_TAG = "url=";

    boolean isKeepAlive()
    {
        boolean flag = false;
        try
        {
            versionLine = versionLine.toLowerCase();
            int i = versionLine.indexOf("http/");
            if(i != -1)
            {
                float f = TextUtils.toFloat(versionLine.substring("http/".length(), versionLine.length()));
                if((double)f > 1.0D)
                {
                    keepAliveLine = keepAliveLine.toLowerCase();
                    if(keepAliveLine.indexOf("connection: close") == -1)
                    {
                        flag = true;
                    }
                }
            }
        }
        catch(Exception exception) { }
        return flag;
    }

    public URLScannerInputStream(StringBuffer stringbuffer, long l)
    {
        buffer = null;
        keepAliveLine = "";
        versionLine = "";
        status = 0L;
        contentLength = -1L;
        lastModified = 0L;
        date = 0L;
        location = null;
        refreshRedirect = false;
        buffer = stringbuffer;
        status = l;
    }

    public static long parseLastModified(String s, int i)
    {
        i += "last-modified:".length() + 1;
        Date date1;
        try
        {
            String s1 = s.substring(i);
            date1 = new Date(s1);
        }
        catch(Exception exception)
        {
            date1 = new Date();
        }
        return date1.getTime() / 1000L;
    }

    public static long parseDate(String s, int i)
    {
        Date date1;
        try
        {
            i += "date:".length() + 1;
            String s1 = s.substring(i);
            date1 = new Date(s1);
        }
        catch(Exception exception)
        {
            date1 = new Date();
        }
        return date1.getTime() / 1000L;
    }

    public void parse()
    {
label0:
        {
            String s = buffer.toString();
            int i = 0;
            do
            {
                int j = s.indexOf('\n', i + 1);
                String s1;
                if(j == -1)
                {
                    s1 = s.substring(i, s.length());
                } else
                {
                    s1 = s.substring(i, j);
                }
                s1 = s1.trim();
                if(s1.length() == 0)
                {
                    break;
                }
                if(status == (long)URLMonitor.kURLNoStatusError)
                {
                    int k = s1.indexOf(' ') + 1;
                    if(k != -1)
                    {
                        versionLine = s1.substring(0, k);
                        int l = TextUtils.readInteger(s1, k);
                        if(l != 0 && l != -1)
                        {
                            status = l;
                        }
                    }
                } else
                {
                    String s2 = s1.toLowerCase();
                    if(location == null)
                    {
                        int i1 = s2.indexOf("location:");
                        if(i1 >= 0)
                        {
                            location = s1.substring("location:".length()).trim();
                            int j1 = location.indexOf(", ");
                            if(j1 >= 0)
                            {
                                String s4 = "";
                                int k1 = location.indexOf("://");
                                if(k1 >= 0)
                                {
                                    s4 = s4 + location.substring(0, k1 + 3);
                                }
                                k1 = location.lastIndexOf(", ");
                                if(k1 >= 0)
                                {
                                    s4 = s4 + location.substring(k1 + 2);
                                }
                                location = s4.trim();
                            }
                            try
                            {
                                location = location.replaceAll("([^:]) ", "$1%20");
                            }
                            catch(IllegalArgumentException illegalargumentexception)
                            {
                                String s5 = "error attempting to replace blanks with + signs for URL preparation: " + illegalargumentexception.toString();
                                LogManager.log("Error", s5);
                                LogManager.log("Error", FileUtils.stackTraceText(illegalargumentexception));
                                System.out.println(s5);
                                illegalargumentexception.printStackTrace();
                            }
                        }
                    }
                    if(keepAliveLine.length() == 0)
                    {
                        int l1 = s2.indexOf("connection:");
                        if(l1 >= 0)
                        {
                            keepAliveLine = s1;
                        }
                    }
                    if(contentLength == -1L)
                    {
                        int i2 = s2.indexOf("content-length:");
                        if(i2 >= 0)
                        {
                            contentLength = TextUtils.toLong(s1.substring("content-length:".length()).trim());
                        }
                    }
                    if(lastModified == 0L)
                    {
                        int j2 = s2.indexOf("last-modified:");
                        if(j2 >= 0)
                        {
                            lastModified = parseLastModified(s2, j2);
                        }
                    }
                    if(date == 0L)
                    {
                        int k2 = s2.indexOf("date:");
                        if(k2 >= 0)
                        {
                            date = parseDate(s2, k2);
                        }
                    }
                }
                if(j == -1)
                {
                    break;
                }
                i = j + 1;
            } while(true);
            if(location != null || s.indexOf("http-equiv") < 0 && s.indexOf("HTTP-EQUIV") < 0)
            {
                break label0;
            }
            HTMLTagParser htmltagparser = new HTMLTagParser(s, REFRESH_TAGS, true, "/HEAD");
            htmltagparser.process();
            Enumeration enumeration = htmltagparser.findTags("meta");
            String s3;
            int l2;
            int i3;
            do
            {
                do
                {
                    HashMap hashmap;
                    do
                    {
                        if(!enumeration.hasMoreElements())
                        {
                            break label0;
                        }
                        hashmap = (HashMap)enumeration.nextElement();
                    } while(!TextUtils.getValue(hashmap, "http-equiv").equalsIgnoreCase("refresh"));
                    s3 = TextUtils.getValue(hashmap, "content");
                    byte byte0 = -1;
                    i3 = s3.indexOf(";");
                } while(i3 < 0);
                l2 = TextUtils.readInteger(s3.substring(0, i3), 0);
            } while(l2 != 0);
            location = s3.substring(i3 + 1).trim();
            if(TextUtils.startsWithIgnoreCase(location, "url="))
            {
                location = location.substring("url=".length()).trim();
            }
            if(location.startsWith("'") || location.startsWith("\""))
            {
                location = location.substring(1).trim();
            }
            if(location.endsWith("'") || location.endsWith("\""))
            {
                location = location.substring(0, location.length() - 1).trim();
            }
            refreshRedirect = true;
        }
    }

}
