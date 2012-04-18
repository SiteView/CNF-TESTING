/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.dragonflow.Properties.HashMapOrdered;
import com.dragonflow.Utils.Base64Decoder;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.HTTP:
// HTTPRequestException

public class HTTPRequest
{

    static final String SCRIPT_VIRTUAL = "/script/";
    static final String HEADER_DELIMITER = ": ";
    static boolean dumpHeaders = false;
    public String requestMethod;
    public String queryString;
    public HashMap variables;
    public String rawURL;
    public String remoteAddress;
    public com.dragonflow.SiteView.User user;
    String protocol;
    String url;
    java.util.Date ifModifiedSince;
    String userAgent;
    String byteRange;
    long rangeStart;
    long entityLength;
    String username;
    String password;
    String realm;
    String loginCookie;
    int requestContentLength;
    String contentType ="text/html;charset=GBK";
    long contentLength;
    java.util.Date lastModified;
    int status;
    int bytesTransferred;
    boolean keepAlive;
    public static int englishID = 0;
    public static int frenchID = 1;
    public static int germanID = 2;
    public int languageID;
    public Array otherHeaders;
    public static boolean logPOSTs = false;
    public static boolean allowCookieLogin = true;
    public static boolean portalAccessOnly = false;
    public static boolean noCache = false;
    public static String PORTAL_SERVER_NAME = "portalserver";
    static String DAY[] = {
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };
    public static boolean useHTTP11 = false;

    public static String getStatusString(int i)
    {
        switch(i)
        {
        case 200: 
            return "OK";

        case 206: 
            return "Partial content";

        case 304: 
            return "Not Modified";

        case 400: 
            return "Bad Request";

        case 401: 
            return "Unauthorized";

        case 403: 
            return "Unauthorized Client IP Address";

        case 404: 
            return "Not Found";

        case 501: 
            return "Not Implemented";

        case 555: 
            return "Login Account Disabled";

        case 556: 
            return "Unknown Login Account";

        case 557: 
            return "Access Permission Error";

        case 998: 
            return "connection closed by client";
        }
        return "Server Error";
    }

    private static void printErrorMessageTop(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.PrintWriter printwriter, String s)
    {
        com.dragonflow.Page.CGI.printBodyHeader(printwriter, s, "");
        String s1 = "";
        if(httprequest != null)
        {
            s1 = httprequest.getAccount();
        }
        if(s1.length() == 0)
        {
            s1 = "administrator";
        }
        if(s1.equals("user"))
        {
            printwriter.println("<br><A href=/SiteView/userhtml/SiteView.html>Go to the " + com.dragonflow.SiteView.Platform.productName + " User page</a><br>");
        } else
        if(s1.equals("administrator"))
        {
            if(com.dragonflow.SiteView.Platform.isUserAccessAllowed())
            {
                printwriter.println("<br><A href=/SiteView/userhtml/SiteView.html>Go to the " + com.dragonflow.SiteView.Platform.productName + " User page</a><br>");
            }
            printwriter.println("<br><A href=/SiteView/htdocs/SiteView.html>Go to the " + com.dragonflow.SiteView.Platform.productName + " Administrator page</a><br>");
        } else
        {
            printwriter.println("<br><A href=/SiteView?account=" + httprequest.getAccount() + ">Go to the " + com.dragonflow.SiteView.Platform.productName + " page</a><br>");
        }
    }

    public static void printErrorMessage(com.dragonflow.HTTP.HTTPRequest httprequest, int i, String s, java.lang.Exception exception, java.io.PrintWriter printwriter)
    {
        String s1 = com.dragonflow.HTTP.HTTPRequest.getStatusString(i);
        String s2 = "text/html";
        if(i == 401)
        {
            s2 = null;
        }
        com.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, i, s1, s2);
        if(i != 401)
        {
            printwriter.println("<HTML>");
        }
        if(i == 401)
        {
            printwriter.print("WWW-Authenticate: BASIC realm=\"" + httprequest.realm + "\"" + com.dragonflow.StandardMonitor.URLMonitor.CRLF + "Content-type: text/html" + com.dragonflow.StandardMonitor.URLMonitor.CRLF + com.dragonflow.StandardMonitor.URLMonitor.CRLF + "<HTML>\n");
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Unauthorized");
            printwriter.println("<br><hr>Proper authorization is required for access to " + com.dragonflow.SiteView.Platform.productName + ".");
            printwriter.println("<hr>");
        } else
        if(i == 403)
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Unauthorized Client IP Address");
            printwriter.println("<br><hr>Unauthorized Client IP Address.");
            printwriter.println("<br><br>To enable access from this IP address, connect to " + com.dragonflow.SiteView.Platform.productName + " as Administrator and go to the General Preferences page");
            printwriter.println("<hr>");
        } else
        if(i == 404)
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Page Not Found");
            printwriter.println("<br><hr>Page Not Found.");
            printwriter.println("<br><br>page: " + httprequest.rawURL);
            printwriter.println("<hr>");
        } else
        if(i == 555)
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Login Account Disabled");
            printwriter.println("<br><hr>Login Account Disabled.");
            printwriter.println("<br><br>To enable this login account, connect to " + com.dragonflow.SiteView.Platform.productName + " as Administrator and go to the User Preferences page");
            printwriter.println("<hr>");
        } else
        if(i == 556)
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Unknown Login Account");
            printwriter.println("<br><hr>Unknown Login Account.");
            printwriter.println("<hr>");
        } else
        if(i == 557)
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, "Access Permission Error");
            printwriter.println("<br><hr>Access Permission Error.");
            printwriter.println("<hr>");
        } else
        {
            com.dragonflow.HTTP.HTTPRequest.printErrorMessageTop(httprequest, printwriter, com.dragonflow.SiteView.Platform.productName + " error");
            printwriter.println("<br><hr>" + com.dragonflow.SiteView.Platform.productName + " encountered an unexpected error.  ");
            printwriter.println("  Please send the following information  with a description of what you were trying to do to " + com.dragonflow.SiteView.Platform.supportEmail + "<hr>");
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("HTTP ERROR\n\n");
            stringbuffer.append("message: " + s1 + "\n");
            stringbuffer.append("status: " + i);
            stringbuffer.append("url: " + s);
            stringbuffer.append("version: " + com.dragonflow.SiteView.Platform.getVersion());
            stringbuffer.append("------------ Exception ------------\n");
            stringbuffer.append("exception: " + exception + "\n");
            String s3 = "";
            if(httprequest != null)
            {
                s3 = httprequest.getAccount();
            }
            try
            {
                java.io.ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                java.io.PrintWriter printwriter1 = com.dragonflow.Utils.FileUtils.MakeOutputWriter(bytearrayoutputstream);
                exception.printStackTrace(printwriter1);
                printwriter1.flush();
                bytearrayoutputstream.flush();
                stringbuffer.append(bytearrayoutputstream.toString() + "\n");
                printwriter1.close();
                bytearrayoutputstream.close();
            }
            catch(java.io.IOException ioexception) { }
            stringbuffer.append("-----------------------------------\n");
            if(httprequest != null)
            {
                stringbuffer.append("------------ Variables ------------\n");
                String s4;
                String s5;
                for(Enumeration enumeration = httprequest.getVariables(); enumeration.hasMoreElements(); stringbuffer.append(s4 + "=" + s5 + "\n"))
                {
                    s4 = (String)enumeration.nextElement();
                    s5 = httprequest.getValue(s4);
                    s5 = com.dragonflow.HTTP.HTTPRequest.makePrivate(s4, s5);
                }

                stringbuffer.append("-----------------------------------\n");
            }
            com.dragonflow.Page.supportPage.printShortForm(printwriter, s3, stringbuffer);
        }
        printwriter.println("</BODY>\n</HTML>");
        printwriter.flush();
    }

    public static String makePrivate(String s, String s1)
    {
        if(s.indexOf("assword") >= 0)
        {
            s1 = new String("*********");
        }
        return s1;
    }

    HTTPRequest(java.io.BufferedReader bufferedreader, boolean flag)
        throws com.dragonflow.HTTP.HTTPRequestException
    {
        requestMethod = "GET";
        queryString = "";
        rawURL = "";
        remoteAddress = "";
        user = null;
        protocol = "";
        url = "";
        ifModifiedSince = null;
        userAgent = "";
        byteRange = "";
        rangeStart = -1L;
        entityLength = -1L;
        username = "";
        password = "";
        realm = "SiteView";
        loginCookie = "";
        contentType ="text/html;charset=GBK";
        contentLength = -1L;
        lastModified = null;
        status = 200;
        bytesTransferred = 0;
        keepAlive = false;
        languageID = englishID;
        otherHeaders = null;
        boolean flag1 = false;
        boolean flag2 = false;
        String s = "";
        String s1 = "";
        String s2 = "";
        try
        {
            String s3 = bufferedreader.readLine();
            s3 = com.dragonflow.Utils.I18N.UnicodeToString(s3, com.dragonflow.Utils.I18N.nullEncoding());
            if(s3 == null)
            {
                throw new HTTPRequestException(998);
            }
            flag1 = true;
            java.util.StringTokenizer stringtokenizer = new StringTokenizer(s3);
            if(stringtokenizer.hasMoreTokens())
            {
                requestMethod = stringtokenizer.nextToken();
                rawURL = stringtokenizer.nextToken();
            } else
            {
                requestMethod = java.lang.System.getProperty("REQUEST_METHOD");
                rawURL = s3;
            }
            if(dumpHeaders || rawURL.indexOf("DUMP_HEADERS") != -1)
            {
                dumpHeaders = true;
                java.lang.System.out.println(s3);
            }
            if(stringtokenizer.hasMoreTokens())
            {
                protocol = stringtokenizer.nextToken();
            }
            do
            {
                if((s3 = bufferedreader.readLine()) == null)
                {
                    break;
                }
                s3 = com.dragonflow.Utils.I18N.UnicodeToString(s3, com.dragonflow.Utils.I18N.nullEncoding());
                if(dumpHeaders)
                {
                    java.lang.System.out.println(s3);
                }
                int j = s3.indexOf(": ");
                if(j >= 0)
                {
                    String s7 = s3.substring(0, j);
                    String s8 = s3.substring(j + ": ".length());
                    int j1 = s8.indexOf(";");
                    if(j1 >= 0)
                    {
                        s8 = s8.substring(0, j1);
                    }
                    if(s7.equalsIgnoreCase("Content-length"))
                    {
                        try
                        {
                            requestContentLength = java.lang.Integer.parseInt(s8);
                        }
                        catch(java.lang.NumberFormatException numberformatexception) { }
                    } else
                    if(s7.equalsIgnoreCase("Accept-language"))
                    {
                        if(s8.startsWith("fr"))
                        {
                            languageID = frenchID;
                        } else
                        if(s8.startsWith("de"))
                        {
                            languageID = germanID;
                        }
                    } else
                    if(s7.equalsIgnoreCase("User-Agent"))
                    {
                        userAgent = s3.substring(j + ": ".length());
                    } else
                    if(s7.equalsIgnoreCase("Range"))
                    {
                        byteRange = s8;
                    } else
                    if(s7.equalsIgnoreCase("If-Modified-Since"))
                    {
                        try
                        {
                            ifModifiedSince = new Date(s8);
                        }
                        catch(java.lang.IllegalArgumentException illegalargumentexception) { }
                    } else
                    if(s7.equalsIgnoreCase("Connection"))
                    {
                        if(protocol.length() > 0 && (protocol.indexOf("1.1") >= 0 || s8.equalsIgnoreCase("Keep-Alive")))
                        {
                            keepAlive = true;
                        }
                    } else
                    if(s7.equalsIgnoreCase("Authorization"))
                    {
                        String as[] = com.dragonflow.Utils.TextUtils.split(s8);
                        if(as.length >= 2 && as[0].equalsIgnoreCase("basic"))
                        {
                            com.dragonflow.Utils.Base64Decoder base64decoder = new Base64Decoder(as[1]);
                            try
                            {
                                String s10 = base64decoder.processString();
                                int j2 = s10.indexOf(":");
                                if(j2 >= 0)
                                {
                                    username = s10.substring(0, j2);
                                    password = s10.substring(j2 + 1);
                                    flag2 = true;
                                }
                            }
                            catch(java.lang.Exception exception)
                            {
                                com.dragonflow.Log.LogManager.log("Error", "HTTP: Error decoding authorization: components[1]");
                            }
                        }
                    } else
                    if(s7.equalsIgnoreCase("Cookie") && allowCookieLogin)
                    {
                        String s9 = s3.substring(j + ": ".length());
                        int l1 = -1;
                        if((l1 = s9.indexOf(com.dragonflow.SiteView.Platform.productName)) >= 0)
                        {
                            s9 = s9.substring(l1);
                            int i2 = s9.indexOf("=");
                            if(i2 >= 0)
                            {
                                loginCookie = s9.substring(i2 + 1);
                                int k2 = loginCookie.indexOf(";");
                                if(k2 > 0)
                                {
                                    loginCookie = loginCookie.substring(0, k2);
                                }
                                loginCookie = loginCookie.trim();
                                String as1[] = com.dragonflow.Utils.TextUtils.split(loginCookie, "|");
                                if(as1.length >= 1)
                                {
                                    s = as1[0];
                                    if(as1.length >= 2)
                                    {
                                        s1 = as1[1];
                                        if(as1.length >= 3)
                                        {
                                            if(dumpHeaders)
                                            {
                                                java.lang.System.out.println("cookiePassword (raw)=" + as1[2]);
                                            }
                                            s2 = com.dragonflow.Utils.TextUtils.enlighten(as1[2]);
                                        }
                                    }
                                }
                                if(dumpHeaders)
                                {
                                    java.lang.System.out.println("cookieAccount=" + s);
                                    java.lang.System.out.println("cookieUsername=" + s1);
                                    java.lang.System.out.println("cookiePassword=" + s2);
                                }
                            }
                        }
                    }
                }
            } while(s3.length() != 0);
            if(isPost() && requestContentLength > 0)
            {
                char ac[] = new char[requestContentLength];
                int k = 0;
                int i1 = requestContentLength;
                do
                {
                    if(i1 <= 0)
                    {
                        break;
                    }
                    int k1 = bufferedreader.read(ac, k, i1);
                    if(k1 == -1)
                    {
                        break;
                    }
                    k += k1;
                    i1 -= k1;
                } while(true);
                queryString = com.dragonflow.Utils.I18N.UnicodeToString(new String(ac), com.dragonflow.Utils.I18N.nullEncoding());
                if(dumpHeaders)
                {
                    java.lang.System.out.println("POSTDATA=" + queryString);
                }
            }
        }
        catch(java.io.IOException ioexception)
        {
            if(flag1)
            {
                com.dragonflow.Utils.TextUtils.debugPrint("HTTPRequest, " + rawURL + ", " + ioexception);
                ioexception.printStackTrace();
            }
            throw new HTTPRequestException(998);
        }
        url = new String(rawURL);
        if(isGet() || isHead())
        {
            int i = rawURL.indexOf('?');
            if(i > 0)
            {
                url = rawURL.substring(0, i);
                if(rawURL.length() != i + 1)
                {
                    queryString += rawURL.substring(i + 1);
                }
            }
        }
        createVariables();
        if(getValue("account").length() == 0)
        {
            String s4 = getURL();
            String s5 = "/SiteView/accounts/";
            if(s4.startsWith(s5))
            {
                String s6 = s4.substring(s5.length());
                int l = s6.indexOf("/");
                if(l >= 0)
                {
                    s6 = s6.substring(0, l);
                }
                setValue("account", s6);
            } else
            if(s4.startsWith("/SiteView/htdocs"))
            {
                setValue("account", "administrator");
            } else
            if(s4.startsWith("/SiteView/userhtml"))
            {
                setValue("account", "user");
            }
        }
        if(getValue("account").length() == 0 && s.length() > 0)
        {
            setValue("account", s);
        }
        if(s.equals(getValue("account")) && !flag2)
        {
            username = s1;
            password = s2;
        }
        if(dumpHeaders)
        {
            java.lang.System.out.println("account=" + getValue("account"));
            java.lang.System.out.println("username=" + username);
            java.lang.System.out.println("password=" + password);
        }
    }

    public boolean getKeepAlive()
    {
        return keepAlive;
    }

    public HTTPRequest(java.io.BufferedReader bufferedreader)
        throws com.dragonflow.HTTP.HTTPRequestException
    {
        requestMethod = "GET";
        queryString = "";
        rawURL = "";
        remoteAddress = "";
        user = null;
        protocol = "";
        url = "";
        ifModifiedSince = null;
        userAgent = "";
        byteRange = "";
        rangeStart = -1L;
        entityLength = -1L;
        username = "";
        password = "";
        realm = "SiteView";
        loginCookie = "";
		contentType ="text/html;charset=GBK";
        contentLength = -1L;
        lastModified = null;
        status = 200;
        bytesTransferred = 0;
        keepAlive = false;
        languageID = englishID;
        otherHeaders = null;
        try
        {
            if(java.lang.System.getProperty("REQUEST_METHOD") != null)
            {
                requestMethod = java.lang.System.getProperty("REQUEST_METHOD");
            }
            if(java.lang.System.getProperty("QUERY_STRING") != null)
            {
                queryString = java.lang.System.getProperty("QUERY_STRING");
            }
            if(java.lang.System.getProperty("HTTP_USER_AGENT") != null)
            {
                userAgent = java.lang.System.getProperty("HTTP_USER_AGENT");
            }
            if(java.lang.System.getProperty("CONTENT_LENGTH") != null)
            {
                try
                {
                    requestContentLength = java.lang.Integer.parseInt(java.lang.System.getProperty("CONTENT_LENGTH"));
                }
                catch(java.lang.NumberFormatException numberformatexception) { }
            }
            url = java.lang.System.getProperty("SCRIPT_NAME");
            if(url.endsWith("go.exe"))
            {
                url += "/SiteView";
            }
            rawURL = url + "?" + java.lang.System.getProperty("QUERY_STRING");
            if(requestMethod.equals("POST") && requestContentLength > 0)
            {
                char ac[] = new char[requestContentLength];
                int i = 0;
                int j = requestContentLength;
                do
                {
                    if(j <= 0)
                    {
                        break;
                    }
                    int k = bufferedreader.read(ac, i, j);
                    if(k == -1)
                    {
                        break;
                    }
                    i += k;
                    j -= k;
                } while(true);
                queryString = com.dragonflow.Utils.I18N.UnicodeToString(new String(ac), com.dragonflow.Utils.I18N.nullEncoding());
            }
        }
        catch(java.io.IOException ioexception)
        {
            throw new HTTPRequestException(400);
        }
        createVariables();
    }

    public HTTPRequest(String as[])
    {
        requestMethod = "GET";
        queryString = "";
        rawURL = "";
        remoteAddress = "";
        user = null;
        protocol = "";
        url = "";
        ifModifiedSince = null;
        userAgent = "";
        byteRange = "";
        rangeStart = -1L;
        entityLength = -1L;
        username = "";
        password = "";
        realm = "SiteView";
        loginCookie = "";
		contentType ="text/html;charset=GBK";
        contentLength = -1L;
        lastModified = null;
        status = 200;
        bytesTransferred = 0;
        keepAlive = false;
        languageID = englishID;
        otherHeaders = null;
        requestMethod = "POST";
        if(as[0].equalsIgnoreCase("GET"))
        {
            requestMethod = "GET";
        }
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append("&");
            }
            stringbuffer.append(as[i]);
        }

        queryString = stringbuffer.toString();
        createVariables();
    }

    public HTTPRequest()
    {
        requestMethod = "GET";
        queryString = "";
        rawURL = "";
        remoteAddress = "";
        user = null;
        protocol = "";
        url = "";
        ifModifiedSince = null;
        userAgent = "";
        byteRange = "";
        rangeStart = -1L;
        entityLength = -1L;
        username = "";
        password = "";
        realm = "SiteView";
        loginCookie = "";
		contentType ="text/html;charset=GBK";
        contentLength = -1L;
        lastModified = null;
        status = 200;
        bytesTransferred = 0;
        keepAlive = false;
        languageID = englishID;
        otherHeaders = null;
        queryString = "";
        createVariables();
    }

    public String getRawURL()
    {
        return rawURL;
    }

    public String getURL()
    {
        return url;
    }

    public void setURL(String s)
    {
        url = s;
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    void setRemoteAddress(String s)
    {
        remoteAddress = s;
    }

    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setUser(com.dragonflow.SiteView.User user1)
    {
        com.dragonflow.SiteView.User _tmp = user1;
        setValue("account", user1.getProperty(com.dragonflow.SiteView.User.pAccount));
        user = user1;
    }

    public void setUser(String s)
    {
        setValue("account", s);
        user = com.dragonflow.SiteView.User.getUserForAccount(s);
    }

    public com.dragonflow.SiteView.User getUser()
    {
        return user;
    }

    public void setRealm(String s)
    {
        realm = s;
    }

    public String getAccount()
    {
        return getValue("account");
    }

    public String getPortalServer()
    {
        return getValue(PORTAL_SERVER_NAME);
    }

    public String getAccountDirectory()
    {
        String s = getValue("account");
        if(s.equals("administrator"))
        {
            return "htdocs";
        }
        if(s.equals("user"))
        {
            return "userhtml";
        } else
        {
            return "accounts/" + s + "/htdocs";
        }
    }

    public boolean isStandardAccount()
    {
        return com.dragonflow.SiteView.Platform.isStandardAccount(getAccount());
    }

    public boolean isSiteSeerAccount()
    {
        if(!com.dragonflow.SiteView.Platform.isStandardAccount(getAccount()))
        {
            return user == null || user.getProperty("_local").length() <= 0;
        } else
        {
            return false;
        }
    }

    public void setBytesTransferred(int i)
    {
        bytesTransferred = i;
    }

    boolean isHEADRequest()
    {
        return requestMethod.equals("HEAD");
    }

    private void createVariables()
    {
        if(queryString == null)
        {
            return;
        }
        variables = new HashMapOrdered(true);
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(queryString, "&");
        do
        {
            if(!stringtokenizer.hasMoreTokens())
            {
                break;
            }
            String s = com.dragonflow.HTTP.HTTPRequest.decodeString(stringtokenizer.nextToken());
			//String s1=s;
            String s1 = com.dragonflow.Utils.I18N.StringToUnicode(s, com.dragonflow.Utils.I18N.nullEncoding());
            int i = s1.indexOf('=');
            if(i >= 0)
            {
                if(s1.length() != i + 1)
                {
					String value=s1.substring(i + 1);
					
					
					//System.out.println(new String(value.getBytes(),"GBK"));
                    variables.add(s1.substring(0, i), value);
                } else
                {
                    variables.add(s1.substring(0, i), "");
                }
            }
        } while(true);
    }

    public String postDetails()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Account: " + getAccount() + "\n");
        stringbuffer.append("IP: " + getRemoteAddress() + "\n");
        stringbuffer.append("URL: " + rawURL + "\n");
        stringbuffer.append("\n");
        for(Enumeration enumeration = getVariables(); enumeration.hasMoreElements(); stringbuffer.append("\n"))
        {
            String s = (String)enumeration.nextElement();
            stringbuffer.append(s);
            stringbuffer.append("=");
            stringbuffer.append(com.dragonflow.HTTP.HTTPRequest.makePrivate(s, getValue(s)));
        }

        return stringbuffer.toString();
    }

    public static String encodeString(String s)
    {
        return com.dragonflow.HTTP.HTTPRequest.encodeString(s, "");
    }

    public static String encodeString(String s, String s1)
    {
        if(!com.dragonflow.Utils.I18N.isI18N)
        {
            return java.net.URLEncoder.encode(s);
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        int j = 0;
        byte abyte0[] = null;
        for(j = 0; j < i && s.charAt(j) <= '\377'; j++) { }
        if(j < i)
        {
            try
            {
                abyte0 = s1 != null && s1.length() != 0 ? s.getBytes(s1) : s.getBytes();
            }
            catch(java.lang.Exception exception)
            {
                if(s1 != null && s1.length() >= 0)
                {
                    try
                    {
                        abyte0 = s.getBytes();
                    }
                    catch(java.lang.Exception exception1) { }
                }
            }
        } else
        {
            abyte0 = new byte[i];
            for(int k = 0; k < i; k++)
            {
                abyte0[k] = (byte)(s.charAt(k) & 0xff);
            }

        }
        for(int l = 0; abyte0 != null && l < abyte0.length; l++)
        {
            int i1 = abyte0[l] & 0xff;
            if(i1 > 127)
            {
                stringbuffer.append("%" + java.lang.Integer.toHexString(i1 >> 4) + java.lang.Integer.toHexString(i1 & 0xf));
                continue;
            }
            if(i1 >= 65 && i1 <= 90 || i1 >= 97 && i1 <= 122 || i1 >= 48 && i1 <= 57 || i1 == 45 || i1 == 95 || i1 == 46 || i1 == 33 || i1 == 126 || i1 == 42 || i1 == 39 || i1 == 47 || i1 == 40 || i1 == 41)
            {
                stringbuffer.append((char)(i1 & 0xff));
                continue;
            }
            if(i1 == 32)
            {
                stringbuffer.append('+');
            } else
            {
                stringbuffer.append("%" + java.lang.Integer.toHexString(i1 >> 4) + java.lang.Integer.toHexString(i1 & 0xf));
            }
        }

        return stringbuffer.length() <= 0 ? s : stringbuffer.toString();
    }

    public static String decodeString(String s)
    {
        return com.dragonflow.HTTP.HTTPRequest.decodeString(s, null);
    }

    public static String decodeString(String s, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        char c;
        for(int i = 0; i < s.length(); stringbuffer.append(c))
        {
            c = s.charAt(i++);
            if(c == '+')
            {
                c = ' ';
                continue;
            }
            if(c != '%')
            {
                continue;
            }
            try
            {
                char c1 = s.charAt(i++);
                char c2 = s.charAt(i++);
                c = (char)java.lang.Integer.parseInt("" + c1 + c2, 16);
            }
            catch(java.lang.Exception exception) { }
        }

        return stringbuffer.toString();
    }

    public String getValue(String s)
    {
        if(variables != null)
        {
            Enumeration enumeration = getValues(s);
            if(enumeration.hasMoreElements())
            {
                return (String)enumeration.nextElement();
            }
        }
        return "";
    }

    public void setValue(String s, String s1)
    {
        if(variables == null)
        {
            variables = new HashMapOrdered(true);
        }
        variables.put(s, s1);
    }

    public void setValues(String s, Enumeration enumeration)
    {
        if(variables == null)
        {
            variables = new HashMapOrdered(true);
        }
        for(; enumeration.hasMoreElements(); variables.add(s, enumeration.nextElement())) { }
    }

    public void unsetValue(String s)
    {
        if(variables != null)
        {
            variables.remove(s);
        }
    }

    public void addValue(String s, String s1)
    {
        if(variables == null)
        {
            setValue(s, s1);
        } else
        {
            variables.add(s, s1);
        }
    }

    public Enumeration getValues(String s)
    {
        if(variables != null)
        {
            return (Enumeration) variables.values(s);
        } else
        {
            return (Enumeration) (new Array(0)).iterator();
        }
    }

    public boolean inValues(String s, String s1)
    {
        for(Enumeration enumeration = getValues(s); enumeration.hasMoreElements();)
        {
            String s2 = (String)enumeration.nextElement();
            if(s1.equals(s2))
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasValue(String s)
    {
        if(variables != null)
        {
            return variables.count(s) > 0;
        } else
        {
            return false;
        }
    }

    public boolean hasMultipleValues(String s)
    {
        if(variables != null)
        {
            return variables.count(s) > 1;
        } else
        {
            return false;
        }
    }

    public int countValues(String s)
    {
        if(variables != null)
        {
            return variables.count(s);
        } else
        {
            return 0;
        }
    }

    public Enumeration getVariables()
    {
        if(variables != null)
        {
            return (Enumeration) variables.keys();
        } else
        {
            return (Enumeration) (new Array(0)).iterator();
        }
    }

    boolean isScript()
    {
        return url.startsWith("/script/");
    }

    String getScriptClass()
    {
        if(isScript())
        {
            return url.substring("/script/".length()).replace('/', '.');
        } else
        {
            return null;
        }
    }

    public void printHeader(java.io.PrintWriter printwriter)
    {
        com.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, status, com.dragonflow.HTTP.HTTPRequest.getStatusString(status), contentType, contentLength, lastModified, rangeStart, entityLength, otherHeaders);
    }

    public String generateHeader()
    {
        return com.dragonflow.HTTP.HTTPRequest.generateHeader(status, com.dragonflow.HTTP.HTTPRequest.getStatusString(status), contentType, contentLength, lastModified, rangeStart, entityLength, otherHeaders);
    }

    static String twoDigits(int i)
    {
        if(i < 10)
        {
            return "0" + i;
        } else
        {
            return "" + i;
        }
    }

    public static String RFCDateFormat(java.util.Date date)
    {
        String s;
        if(date.getTimezoneOffset() < 0)
        {
            s = "+";
        } else
        {
            s = "-";
        }
        String s1;
        if(date.getYear() > 99)
        {
            s1 = String.valueOf(date.getYear() + 1900);
        } else
        {
            s1 = String.valueOf(date.getYear());
        }
        String as[] = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        };
        String as1[] = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", 
            "Nov", "Dec"
        };
        return as[date.getDay()] + ", " + String.valueOf(date.getDate()) + " " + as1[date.getMonth()] + " " + s1 + " " + com.dragonflow.HTTP.HTTPRequest.twoDigits(date.getHours()) + ":" + com.dragonflow.HTTP.HTTPRequest.twoDigits(date.getMinutes()) + ":" + com.dragonflow.HTTP.HTTPRequest.twoDigits(date.getSeconds()) + " GMT" + s + com.dragonflow.HTTP.HTTPRequest.twoDigits(java.lang.Math.abs(date.getTimezoneOffset()) / 60) + com.dragonflow.HTTP.HTTPRequest.twoDigits(java.lang.Math.abs(date.getTimezoneOffset()) % 60);
    }

    public static void printHeader(java.io.PrintWriter printwriter, int i, String s, String s1)
    {
        com.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, i, s, s1, -1L, null, -1L, 0L);
    }

    public static void printHeader(java.io.PrintWriter printwriter, int i, String s, String s1, long l, java.util.Date date, long l1, long l2)
    {
        com.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, i, s, s1, l, date, l1, l2, null);
    }

    private static String generateHeader(int i, String s, String s1, long l, java.util.Date date, long l1, 
            long l2, Array array)
    {
        java.util.Date date1 = com.dragonflow.SiteView.Platform.makeDate();
        String s2 = "";
        if(date != null)
        {
            s2 = "Last-Modified: " + DAY[date.getDay()] + ", " + date.toGMTString() + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
        }
        String s3 = "";
        if(l1 >= 0L)
        {
            s3 = "Content-range: bytes " + l1 + "-" + ((l1 + l) - 1L) + "/" + l2 + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
        }
        String s4 = "";
        if(l >= 0L)
        {
            s4 = "Content-length: " + l + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
        }
        String s5 = "";
        if(s1 != null)
        {
            s5 = "Content-type: " + s1 + com.dragonflow.StandardMonitor.URLMonitor.CRLF + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
        }
        String s6 = "";
        if(noCache)
        {
            s6 = "Cache-Control: no-cache" + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
            noCache = false;
        }
        String s7 = "";
        if(array != null)
        {
            for(int j = 0; j < array.size(); j++)
            {
                s7 = s7 + array.get(j) + com.dragonflow.StandardMonitor.URLMonitor.CRLF;
            }

        }
        String s8 = "HTTP/1.0";
        if(useHTTP11)
        {
            s8 = "HTTP/1.1";
        }
        String s9 = s8 + " " + i + " " + s + com.dragonflow.StandardMonitor.URLMonitor.CRLF + "Server: " + com.dragonflow.SiteView.Platform.productName + "/" + com.dragonflow.SiteView.Platform.getVersion() + com.dragonflow.StandardMonitor.URLMonitor.CRLF + "Date: " + DAY[date1.getDay()] + ", " + date1.toGMTString() + com.dragonflow.StandardMonitor.URLMonitor.CRLF + s2 + s4 + s3 + s6 + s7 + s5;
        return s9;
    }

    public static void printHeader(java.io.PrintWriter printwriter, int i, String s, String s1, long l, java.util.Date date, long l1, long l2, Array array)
    {
        printwriter.print(com.dragonflow.HTTP.HTTPRequest.generateHeader(i, s, s1, l, date, l1, l2, array));
    }

    public void addOtherHeader(String s)
    {
        if(otherHeaders == null)
        {
            otherHeaders = new Array();
        }
        otherHeaders.add(s);
    }

    public void writeAccessLog()
    {
        if(logPOSTs && isPost())
        {
            com.dragonflow.Log.LogManager.log("POST", postDetails());
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getRemoteAddress());
        stringbuffer.append(" - ");
        if(username.length() > 0)
        {
            stringbuffer.append(username);
        } else
        {
            stringbuffer.append("-");
        }
        java.util.Date date = com.dragonflow.SiteView.Platform.makeDate();
        stringbuffer.append(" [");
        stringbuffer.append(com.dragonflow.HTTP.HTTPRequest.RFCDateFormat(date));
        stringbuffer.append("] \"");
        stringbuffer.append(requestMethod);
        stringbuffer.append(" ");
        stringbuffer.append(rawURL);
        stringbuffer.append(" ");
        stringbuffer.append(protocol);
        stringbuffer.append("\" ");
        stringbuffer.append(String.valueOf(status));
        stringbuffer.append(" ");
        stringbuffer.append(bytesTransferred);
        com.dragonflow.Log.LogManager.log("HTTP", stringbuffer.toString());
    }

    public void setContentType(String s)
    {
        contentType = s;
    }

    public void setContentLength(long l)
    {
        contentLength = l;
    }

    public void setLastModified(java.util.Date date)
    {
        lastModified = date;
    }

    public void setStatus(int i)
    {
        status = i;
    }

    public boolean hasBeenModified(java.util.Date date)
    {
        if(ifModifiedSince == null)
        {
            return true;
        } else
        {
            return ifModifiedSince.before(date);
        }
    }

    public boolean isGet()
    {
        return requestMethod == null ? false : requestMethod.equals("GET");
    }

    public boolean isPost()
    {
        return requestMethod == null ? false : requestMethod.equals("POST");
    }

    public boolean isHead()
    {
        return requestMethod == null ? false : requestMethod.equals("HEAD");
    }

    public boolean usesCookieLogin()
    {
        return loginCookie.length() > 0;
    }

    public boolean actionAllowed(String s)
    {
        boolean flag = false;
//        if(com.dragonflow.TopazIntegration.MAManager.isAttached())
//        {
//            com.dragonflow.SiteView.TopazCentralAuthorization topazcentralauthorization = com.dragonflow.SiteView.TopazCentralAuthorization.getInstance();
//            flag = topazcentralauthorization.accessAllowed(s, user);
//        } else
        {
            flag = com.dragonflow.HTTP.HTTPRequest.isUserAllowed(user, s);
        }
        if(portalAccessOnly && userAgent.indexOf("CentraScope") == -1)
        {
            flag = false;
        }
        return flag;
    }

    public static boolean isUserAllowed(com.dragonflow.SiteView.User user1, String s)
    {
        if(user1 != null)
        {
            if(user1.getProperty("_edit").length() > 0)
            {
                return true;
            } else
            {
                return user1.getProperty(s).length() > 0;
            }
        } else
        {
            return false;
        }
    }

    public String getPermission(String s)
    {
        if(user != null)
        {
            return user.getPermission(s);
        } else
        {
            return "";
        }
    }

    public int getPermissionAsInteger(String s)
    {
        return com.dragonflow.Utils.TextUtils.toInt(getPermission(s));
    }

    public java.util.Vector getPermissions(String s)
    {
        if(user != null)
        {
            return user.getPermissions(s);
        } else
        {
            return new Vector();
        }
    }

    public String getPermission(String s, String s1)
    {
        if(user != null)
        {
            return user.getPermission(s, s1);
        } else
        {
            return "";
        }
    }

    public String getLanguage()
    {
        String s = getUserSetting("_localeLanguage");
        if(s.length() == 0)
        {
            s = "en";
        }
        return s;
    }

    public String getString(String s)
    {
        return com.dragonflow.Utils.LanguageUtils.getString(s, getLanguage());
    }

    public int getPermissionAsInteger(String s, String s1)
    {
        return com.dragonflow.Utils.TextUtils.toInt(getPermission(s, s1));
    }

    public String getUserSetting(String s)
    {
        if(user != null)
        {
            return user.getPermission(s);
        } else
        {
            return "";
        }
    }

    public static void dumpMatch(String s, String s1, String s2)
    {
        int i = s1.indexOf(s);
        if(i >= 0)
        {
            String s3 = s1.substring(i);
            s3 = s3.substring(0, s3.indexOf("&"));
            java.lang.System.out.println("***" + s2 + s3);
            String s4 = com.dragonflow.HTTP.HTTPRequest.decodeString(s3);
            java.lang.System.out.println("***" + s4);
            java.lang.System.out.print("    ");
            for(int j = 0; j < s4.length(); j++)
            {
                java.lang.System.out.print(java.lang.Integer.toHexString(s4.charAt(j)) + ", ");
            }

            java.lang.System.out.print("\n");
        }
    }

    public void overrideParam(String s, String s1)
    {
        addValue(s, s1);
        rawURL = com.dragonflow.HTTP.HTTPRequest.removeParameter(rawURL, s);
        char c = '&';
        if(rawURL.indexOf('?') < 0)
        {
            c = '?';
        }
        rawURL += c + s + "=" + s1;
    }

    public static String removeParameter(String s, String s1)
    {
        String s2 = s;
        int i = s2.indexOf(s1 + "=");
        if(i >= 0)
        {
            char c = s2.charAt(i - 1);
            if(c == '&')
            {
                i--;
            } else
            if(c != '?')
            {
                i = -1;
            }
        }
        for(; i >= 0; i = s2.indexOf("&" + s1 + "="))
        {
            String s3 = "";
            int j = s2.substring(i + s1.length() + 2).indexOf("&");
            if(j >= 0)
            {
                s3 = s2.substring(0, i) + s2.substring(j + i + s1.length() + 2);
            } else
            {
                s3 = s2.substring(0, i);
            }
            s2 = s3;
        }

        return s2;
    }

    public static void main(String args[])
    {
        java.lang.System.out.println("Original:");
        java.lang.System.out.println(args[0]);
        java.lang.System.out.println("Decoded:");
        java.lang.System.out.println(com.dragonflow.HTTP.HTTPRequest.decodeString(args[0]));
    }

    static 
    {
        String s = java.lang.System.getProperty("HTTPRequest.debug");
        if(s != null && s.length() > 0)
        {
            dumpHeaders = true;
        }
    }
}
