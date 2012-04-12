/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.HTTP;

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

import jgl.Array;
import COM.dragonflow.Page.loginPage;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.User;
import COM.dragonflow.Utils.ThreadPool;

// Referenced classes of package COM.dragonflow.HTTP:
// HTTPRequest, HTTPRequestException, HTTPRequestHandler, CGIRequestHandler,
// DocumentRequestHandler, BinaryFileRequestHandler, HTTPServer, ProxyHandler,
// VirtualDirectory

public class HTTPRequestThread implements java.lang.Runnable {

    public static COM.dragonflow.Utils.ThreadPool httpThreadPool = new ThreadPool("HttpRequest", null);

    private static boolean debug = false;

    public static boolean licenseExpired = false;

    long lastTime;

    COM.dragonflow.HTTP.HTTPServer httpServer;

    java.net.Socket socket;

    java.io.OutputStream rawOutput;

    java.io.PrintWriter outputStream;

    boolean keepAliveEnabled;

    java.io.BufferedReader inputStream;

    static boolean enableProxy = false;

    COM.dragonflow.Utils.ThreadPool.SingleThread thread;

    int nextArg;

    String args;

    void timeStamp() {
        long l = java.lang.System.currentTimeMillis();
        if (lastTime != 0L) {
            java.lang.System.out.print((l - lastTime) + ":");
        }
        lastTime = l;
    }

    HTTPRequestThread(COM.dragonflow.HTTP.HTTPServer httpserver, java.net.Socket socket1, boolean flag) {
        lastTime = 0L;
        socket = null;
        rawOutput = null;
        outputStream = null;
        keepAliveEnabled = false;
        inputStream = null;
        thread = null;
        nextArg = 0;
        args = "";
        thread = httpThreadPool.getThread();
        thread.setNameIfNeeded("HTTPRequestThread");
        socket = socket1;
        keepAliveEnabled = flag;
        Platform.setSocketTimeout(socket1, 0x3a980);
        httpServer = httpserver;
        thread.setPriorityIfNeeded(6);
        thread.activate(this);
    }

    public static String decodeString(String s) {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        char c;
        for (int i = 0; i < s.length(); stringbuffer.append(c)) {
            c = s.charAt(i ++);
            if (c == '+') {
                c = ' ';
                continue;
            }
            if (c != '%') {
                continue;
            }
            try {
                char c1 = s.charAt(i ++);
                char c2 = s.charAt(i ++);
                c = (char) java.lang.Integer.parseInt("" + c1 + c2, 16);
            } catch (java.lang.Exception exception) {
            }
        }

        return stringbuffer.toString();
    }

    String nextURLArg() {
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = false;
        do {
            if (nextArg >= args.length()) {
                break;
            }
            char c = args.charAt(nextArg ++);
            if (c == '"') {
                if (flag) {
                    flag = false;
                } else {
                    flag = true;
                }
                continue;
            }
            if (!flag && c == ' ') {
                break;
            }
            stringbuffer.append(c);
        } while (true);
        String s = stringbuffer.toString();
        return s;
    }

    void handleGetURL(COM.dragonflow.HTTP.HTTPRequest httprequest) throws java.lang.Exception {
        args = COM.dragonflow.HTTP.HTTPRequestThread.decodeString(httprequest.queryString);
        String s = nextURLArg();
        if (s.equals("-ignoreErrors")) {
            s = nextURLArg();
        }
        if (s.equals("-ignoreUnknownCA")) {
            s = nextURLArg();
        }
        jgl.Array array = new Array();
        for (; s.equals("-c"); s = nextURLArg()) {
            String s1 = nextURLArg();
            array.add(s1);
        }

        if (s.equals("-h")) {
            outputStream.println("-ignoreErrors -ignoreUnknownCA -c cookie -x url");
            return;
        }
        if (!s.equals("-x")) {
            throw new Exception("error: " + s + ", " + httprequest.queryString);
        }
        String s2 = nextURLArg();
        int i = 60;
        s = nextURLArg();
        if (s.length() > 0) {
            i = COM.dragonflow.Utils.TextUtils.toInt(s);
            if (i <= 0) {
                i = 60;
            }
        }
        i *= 1000;
        String s3 = nextURLArg();
        String s4 = nextURLArg();
        String s5 = COM.dragonflow.HTTP.HTTPRequestThread.decodeString(nextURLArg());
        String s6 = nextURLArg();
        String s7 = nextURLArg();
        String s8 = nextURLArg();
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer(s2);
        COM.dragonflow.Utils.SocketSession socketsession = COM.dragonflow.Utils.SocketSession.getSession(null);
        long l = 50000L;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        long l1 = COM.dragonflow.Utils.TextUtils.toLong(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_urlContentMatchMax"));
        if (l1 > 1L) {
            l = l1;
        }
        jgl.Array array1 = Platform.split('&', s5);
        int j = -1;
        int k = -1;
        StringBuffer stringbuffer3 = new StringBuffer();
        for (int i1 = 0; i1 < array1.size(); i1 ++) {
            String s9 = (String) array1.at(i1);
            if (s9.indexOf(COM.dragonflow.StandardMonitor.URLSequenceMonitor.refererStartToken) != -1) {
                j = i1;
            }
            if (s9.equals(COM.dragonflow.StandardMonitor.URLSequenceMonitor.refererEndToken)) {
                k = i1;
                break;
            }
            if (j != -1) {
                stringbuffer3.append(s9);
                stringbuffer3.append("&");
            }
        }

        if (j != -1 && k != -1) {
            stringbuffer3.delete(stringbuffer3.length() - 1, stringbuffer3.length());
            array1.remove(j, k);
            array1.insert(j, stringbuffer3.toString());
        }
        for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); array1.add("Set-" + enumeration.nextElement())) {
        }
        String s10 = "";
        String s11 = "";
        String s12 = "";
        String s13 = "";
        int j1 = COM.dragonflow.StandardMonitor.URLMonitor.DEFAULT_MAX_REDIRECTS + 1;
        long al[] = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s2, s11, s10, s6, s7, s8, array1, s3, s4, s12, stringbuffer, l, s13, j1, i, stringbuffer2, stringbuffer1);
        httprequest.setBytesTransferred(stringbuffer.length());
        httprequest.setContentType("text/plain");
        httprequest.printHeader(outputStream);
        outputStream.println(stringbuffer);
        long l2 = al[0];
        String s14;
        if (l2 < 0L) {
            s14 = "-" + filledNumber(-l2, 9);
        } else {
            s14 = filledNumber(l2, 10);
        }
        outputStream.println("URLMonitorStatus: " + s14 + COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
        l2 = al[1];
        if (l2 < 0L) {
            l2 = 0L;
        }
        s14 = filledNumber(l2, 10);
        outputStream.println("URLMonitorDuration: " + s14 + COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
        outputStream.flush();
    }

    String filledNumber(long l, int i) {
        String s = "" + l;
        s = COM.dragonflow.Utils.TextUtils.filledString('0', i - 1 - s.length()) + l;
        if (l < 0L) {
            s = '-' + s;
        } else {
            s = '0' + s;
        }
        return s;
    }

    void handlePortURL(COM.dragonflow.HTTP.HTTPRequest httprequest) throws java.lang.Exception {
        httprequest.setContentType("text/plain");
        httprequest.printHeader(outputStream);
        String s = httprequest.getValue("host");
        String s1 = httprequest.getValue("sendString");
        String s2 = httprequest.getValue("matchString");
        String s3 = httprequest.getValue("portid");
        String s4 = httprequest.getValue("timeout");
        StringBuffer stringbuffer = new StringBuffer();
//        int ai[] = COM.dragonflow.StandardMonitor.PortRemoteMonitor.getPortData(s, s3, s1, s2, s4, stringbuffer);
//        outputStream.println("status=" + ai[0] + COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
//        outputStream.println("time=" + ai[1] + COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
//        outputStream.println("error=" + stringbuffer + COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
        outputStream.flush();
    }

    void handlePingURL(COM.dragonflow.HTTP.HTTPRequest httprequest) throws java.lang.Exception {
        httprequest.setContentType("text/plain");
        httprequest.printHeader(outputStream);
        String s = httprequest.getValue("host");
        String s1 = Platform.pingCommand(s, 5000, 5, 64);
        boolean flag = false;
        try {
            java.lang.Process process = COM.dragonflow.Utils.CommandLine.execSync(s1);
            boolean flag1 = false;
            java.io.BufferedReader bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
            String s2;
            while ((s2 = bufferedreader.readLine()) != null) {
                if (s2.length() > 0) {
                    flag = true;
                    outputStream.println(s2);
                    outputStream.flush();
                    flag1 = false;
                } else if (flag1) {
                    flag = true;
                    outputStream.println();
                    flag1 = false;
                } else {
                    flag1 = true;
                }
            }
            bufferedreader.close();
        } catch (java.io.IOException ioexception) {
            if (!flag) {
                outputStream.println("Could not perform ping command: " + s1);
            }
        }
        outputStream.flush();
    }

    void handleTraceURL(COM.dragonflow.HTTP.HTTPRequest httprequest) throws java.lang.Exception {
        httprequest.setContentType("text/plain");
        httprequest.printHeader(outputStream);
        String s = httprequest.getValue("host");
        String s1 = Platform.traceCommand(s, COM.dragonflow.Utils.TextUtils.getValue(MasterConfig.getMasterConfig(), "_tracerouteCommand"));
        boolean flag = false;
        try {
            java.lang.Process process = COM.dragonflow.Utils.CommandLine.execSync(s1);
            boolean flag1 = false;
            java.io.BufferedReader bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
            String s2;
            while ((s2 = bufferedreader.readLine()) != null) {
                if (s2.length() > 0) {
                    flag = true;
                    outputStream.println(s2);
                    outputStream.flush();
                    flag1 = false;
                } else if (flag1) {
                    flag = true;
                    outputStream.println();
                    flag1 = false;
                } else {
                    flag1 = true;
                }
            }
            bufferedreader.close();
        } catch (java.io.IOException ioexception) {
            if (!flag) {
                outputStream.println("Could not perform traceroute command: " + s1);
            }
        }
        outputStream.flush();
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public void run() {
        COM.dragonflow.HTTP.HTTPRequest httprequest;
        boolean flag;
        boolean flag1;
        httprequest = null;
        flag = false;
        flag1 = false;
        int j;

        try {
            int i = SiteViewGroup.currentSiteView().getSettingAsLong("_httpKeepAliveTimeout", 240);
            Platform.setSocketTimeout(socket, i * 1000);
            inputStream = COM.dragonflow.Utils.FileUtils.MakeInputReader(socket.getInputStream());
            rawOutput = socket.getOutputStream();
            outputStream = null;
            j = 0;

            while (true) {
                String url;
                String page;
                try {
                    httprequest = new HTTPRequest(inputStream, true);
                    url = httprequest.getURL();
					//if(!s.endsWith(".gif")&&!s.endsWith(".css"))
						//s = COM.dragonflow.Utils.I18N.StringToUnicode(HTTPRequest.decodeString(s), COM.dragonflow.Utils.I18N.nullEncoding());
					
                    status("Thread: Requests handled = " + j);
                    if (rawOutput == null) {
                        rawOutput = socket.getOutputStream();
                    }
                    page = httprequest.getValue("page");
                    if (!COM.dragonflow.HTTP.HTTPServer.filter.isEmpty() && !COM.dragonflow.HTTP.HTTPServer.filter.contains(page.toLowerCase())) {
                        if (httprequest != null) {
                            httprequest.writeAccessLog();
                        }
                        break;
                    }
                    if (httprequest.getValue("operation").equalsIgnoreCase("getbin") || httprequest.getValue("operation").startsWith("getFileCompressed")) {
                        flag = true;
                        if (!flag1 && outputStream != null) {
                            status("Switching from ascii to binary (requestsHandled by this thread = " + j);
                            outputStream.close();
                            outputStream = null;
                        }
                    } else {
                        flag = false;
                        if (flag1) {
                            status("Switching from binary to ascii (requestsHandled by this thread = " + j);
                        }
                        if (outputStream == null) {
                            outputStream = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(rawOutput, COM.dragonflow.Utils.I18N.nullEncoding());
                        }
                    }
                    if (flag && enableProxy && httprequest.rawURL.startsWith("http://") || httprequest.rawURL.indexOf(":443") >= 0) {
                        if (!httprequest.rawURL.startsWith("http")) {
                            httprequest.rawURL = "https://" + httprequest.rawURL;
                        }
                        if (!httprequest.getURL().startsWith("http")) {
                            httprequest.setURL("https://" + httprequest.getURL());
                        }
                        COM.dragonflow.Utils.TextUtils.debugPrint("HANDLING PROXY=" + httprequest.rawURL);
                        COM.dragonflow.HTTP.ProxyHandler.handleProxyRequest(httprequest, outputStream);
                        if (httprequest != null) {
                            httprequest.writeAccessLog();
                        }
                        return;
                    }

                    SiteViewGroup siteviewgroup = SiteViewGroup.cCurrentSiteView;
                    if (url.equals("/") || url.equals("/SiteView") || url.equals("/SiteView/") || url.startsWith("/siteview")) {
                        httprequest.setURL("/SiteView/cgi/go.exe/SiteView");
                    }
                    thread.setNameIfNeeded("HTTPRequest " + httprequest.getURL());
                    java.net.InetAddress inetaddress = socket.getInetAddress();
                    String s3 = Platform.dottedIPString(inetaddress.getAddress());
                    httprequest.setRemoteAddress(s3);

                    if (url.indexOf("/groups/") != -1) {
                        throw new HTTPRequestException(404);
                    }
                    if (httpServer.getMaxConnections() > 0 && httpServer.getConnections() > httpServer.getMaxConnections()) {
                        dumpToOutputStream(httprequest.generateHeader(), flag);
                        dumpToOutputStream("<HTML><HEAD><TITLE>Server Busy</TITLE><BODY>The Server is busy. Please try again later</BODY></HTML>", flag);
                        flushOutputStream(flag);
                        if (httprequest != null) {
                            httprequest.writeAccessLog();
                        }
                        return;
                    }

                    if (siteviewgroup != null) {
                        if (licenseExpired) {
                            String s4 = "licenseExpired";
                            if (httprequest.getRawURL().indexOf("?page=xmlApi") != -1 || httprequest.isPost() && httprequest.queryString.indexOf("page=xmlApi") != -1) {
                                httprequest.addValue("licenseExpired", "true");
                            } else if (httprequest.getRawURL().indexOf(s4) == -1 && httprequest.getRawURL().indexOf("shutd0wN") == -1 && httprequest.getRawURL().indexOf("restart") == -1 && httprequest.getRawURL().indexOf("?page") != -1
                                    || httprequest.getRawURL().indexOf("SiteView.html") != -1) {
                                COM.dragonflow.Log.LogManager.log("Error", "License Expired, redicting request for URL: " + httprequest.getRawURL() + ": to the licenseExpiredPage.");
                                String s7 = "/SiteView/cgi/go.exe/SiteView?page=" + s4 + "&account=administrator";
                                COM.dragonflow.Page.CGI.printRefreshHeader(outputStream, "", s7, 0);
                                if (httprequest != null) {
                                    httprequest.writeAccessLog();
                                }
                                return;
                            }
                        } else if (siteviewgroup.isStartingUp()) {
                            dumpToOutputStream(httprequest.generateHeader(), flag);
                            if (httprequest.getRawURL().indexOf("?page=xmlApi") != -1 || httprequest.isPost() && httprequest.queryString.indexOf("page=xmlApi") != -1) {
                                String s5 = (new Long(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_SITEVIEW_NOT_FULLY_STARTED)).toString();
                                dumpToOutputStream("<SiteViewConfigurationRequest>\n<genericCommand errortype=\"operational\" errorcode=\"" + s5 + "\" " + "error" + "=\""
                                        + COM.dragonflow.Resource.SiteViewResource.getFormattedString(s5, new String[0]) + "\">\n" + "</genericCommand>\n</SiteViewConfigurationRequest>", flag);
                            } else {
                                dumpToOutputStream("<HTML><HEAD><TITLE>Starting Up</TITLE><meta HTTP-EQUIV=\"Refresh\" CONTENT=\"30; URL=" + httprequest.rawURL + "\"><BODY>" + "<CENTER><H3>Starting Up</H3></CENTER><BR>\n"
                                        + "The monitoring engine is currently starting up, and no log ins are enabled while it is starting." + " This page will reload itself in 30 seconds, or you can " + "<A HREF=" + httprequest.rawURL
                                        + ">click here</A> to reload it now." + "</BODY></HTML>", flag);
                            }
                            flushOutputStream(flag);
                            if (httprequest != null) {
                                httprequest.writeAccessLog();
                            }
                            return;
                        }

                        String s6 = httprequest.getAccount();
                        if (!flag) {
                            int k = siteviewgroup.checkAuthorization(httprequest);
                            if (k != 200) {
                                if (k == 403) {
                                    throw new HTTPRequestException(403);
                                }
                                if (s6.length() > 0) {
                                    throw new HTTPRequestException(401);
                                }
                                if (siteviewgroup.getSetting("_disableLoginPage").length() > 0) {
                                    throw new HTTPRequestException(401);
                                }
                                httprequest.setStatus(200);
                                COM.dragonflow.Page.loginPage loginpage = new loginPage();
                                loginpage.request = httprequest;
                                loginpage.outputStream = outputStream;
                                loginpage.printBody();
                                if (httprequest != null) {
                                    httprequest.writeAccessLog();
                                }
                                return;
                            }
                            String s9;
                            long l = siteviewgroup.getSettingAsLong("_adminExpireDays");
                            if (l > 0L) {
                                java.util.Date date = Platform.makeDate();
                                java.util.Date date1 = new Date(0L);
                                String s8 = siteviewgroup.getSetting("_adminChangedDate");
                                if (s8.length() > 0) {
                                    date1 = COM.dragonflow.Utils.TextUtils.stringToDate(s8);
                                }
                                long l1 = 0x15180L;
                                long l2 = (date.getTime() - date1.getTime()) / (l1 * 1000L);
                                if (l2 >= l) {
                                    s9 = "";
                                    if (httprequest.getURL().indexOf("page=changepassword") != -1) {
                                        String s10 = httprequest.getValue("adminPassword");
                                        String s11 = httprequest.getValue("adminPassword2");
                                        String s12 = User.checkPassword(MasterConfig.getMasterConfig(), s10, s11);
                                        if (s12.length() > 0) {
                                            s9 = "<b>New password was not saved: " + s12 + "</b>";
                                        } else if (s10.equals(siteviewgroup.getSetting("_adminPassword"))) {
                                            s9 = "<b>New password was not saved: password must be different than previous password</b>";
                                        } else {
                                            siteviewgroup.setProperty("_adminPassword", s10);
                                            siteviewgroup.setProperty("_adminChangedDate", COM.dragonflow.Utils.TextUtils.dateToString());
                                            siteviewgroup.saveSettings();
                                            httprequest.printHeader(outputStream);
                                            outputStream.println("<HTML>");
                                            COM.dragonflow.Page.CGI.printRefreshHeader(outputStream, "", "/SiteView", 0);
                                            outputStream.println("<a href=/SiteView>Go to SiteView</a></BODY></HTML>\n");
                                            if (httprequest != null) {
                                                httprequest.writeAccessLog();
                                            }
                                            return;
                                        }
                                    }

                                    httprequest.printHeader(outputStream);
                                    outputStream.println("<HTML><TITLE>Update Administrator Password</TITLE><BODY>" + s9 + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView/page=changepassword method=POST>\n"
                                            + "<CENTER><H3>Update Administrator Password</H3></CENTER><BR>\n" + "The Administrator Password needs to be updated. <p><br> Please enter a new administrator password.<p>" + "<DL>\n"
                                            + "<P><DT>Administrator Password:\n" + "<TABLE BORDER=0>\n" + "<TR><TD WIDTH=20><TD>Password:<TD><input type=password name=adminPassword size=30 value=\"\"></TR>\n"
                                            + "<TR><TD WIDTH=20><TD>Password (again):<TD><input type=password name=adminPassword2 size=30 value=\"\"></TR>\n" + "</TABLE>\n" + "<p><input type=submit value=\"Change Password\">\n" + "</DL></FORM>"
                                            + "</BODY></HTML>");
                                    outputStream.flush();
                                    if (httprequest != null) {
                                        httprequest.writeAccessLog();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                } catch (COM.dragonflow.HTTP.HTTPRequestException httprequestexception) {
                    if (httprequest != null) {
                        httprequest.status = httprequestexception.getStatusCode();
                        httprequest.writeAccessLog();
                    }
                    if (!flag) {
                        COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(httprequest, httprequestexception.getStatusCode(), httprequestexception.getURL(), httprequestexception, outputStream);
                    }
                    throw httprequestexception;
                } catch (java.lang.Exception exception1) {
                    String s2 = "";
                    if (httprequest != null) {
                        s2 = httprequest.getURL();
                        httprequest.status = 999;
                        httprequest.writeAccessLog();
                    }
                    exception1.printStackTrace();
                    if (!flag) {
                        COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(httprequest, 999, s2, exception1, outputStream);
                    }
                    throw exception1;
                }

                java.lang.Class class1;
                HTTPRequestHandler hrh = null;
                if (!flag) {
                    if (httprequest.isScript()) {
                        String s9 = "";
                        try {
                            s9 = httprequest.getScriptClass();
                            if (s9 != null) {
                                class1 = java.lang.Class.forName(((String) (s9)));
                                hrh = (COM.dragonflow.HTTP.HTTPRequestHandler) class1.newInstance();
                            }
                        } catch (ClassNotFoundException e) {
                            COM.dragonflow.Log.LogManager.log("Error", "Could not find HTTP handler class " + s9);
                            throw new HTTPRequestException(501, httprequest.getURL());
                        } catch (Exception e) {
                            COM.dragonflow.Log.LogManager.log("Error", "page: " + httprequest.getURL() + ", " + e);
                            throw new HTTPRequestException(501, httprequest.getURL());
                        }
                    } else {
                        if (httprequest.getURL().indexOf("/get.exe") != -1) {
                            handleGetURL(httprequest);
                            if (httprequest != null) {
                                httprequest.writeAccessLog();
                            }
                            return;
                        }

                        if (httprequest.getURL().indexOf("/png.exe") != -1) {
                            handlePingURL(httprequest);
                            if (httprequest != null) {
                                httprequest.writeAccessLog();
                            }
                            return;
                        }

                        if (httprequest.getURL().indexOf("/trace.exe") != -1) {
                            handleTraceURL(httprequest);
                            if (httprequest != null) {
                                httprequest.writeAccessLog();
                            }
                            return;
                        }

                        if (httprequest.getURL().indexOf("/port.exe") != -1) {
                            handlePortURL(httprequest);
                            if (httprequest != null) {
                                httprequest.writeAccessLog();
                            }
                            return;
                        }

                        VirtualDirectory vd = httpServer.getVirtualDirectory(httprequest.getURL());
                        if (vd != null && vd.isCGIDirectory()) {
                            hrh = new CGIRequestHandler(httpServer);
                        } else {
                            hrh = new DocumentRequestHandler(httpServer);
                        }
                    }

                    if (hrh != null) {
                        hrh.handleRequest(httprequest, outputStream, rawOutput);
                    } else {
                        throw new HTTPRequestException(501, httprequest.getURL());
                    }
                }

                //BinaryFileRequestHandler bfrh = new BinaryFileRequestHandler(httpServer);
                //bfrh.handleRequest(httprequest, rawOutput);
                //rawOutput.close();
                //rawOutput = null;
                if (httprequest != null) {
                    httprequest.writeAccessLog();
                }

                // throw exception2;
                if (!keepAliveEnabled || httprequest.status != 304 && httprequest.contentLength == -1L || !httprequest.getKeepAlive() || j ++ > 100) {
                    break;
                }
                flushOutputStream(flag);
                flag1 = flag;
            }
        } catch (Exception e) {
            /* empty */
        } finally {
            try {
                closeOutputStream(flag);
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                    if (httpServer.getMaxConnections() > 0) {
                        httpServer.decConnections();
                    }
                }
            } catch (java.io.IOException ioexception) {
                COM.dragonflow.Log.LogManager.log("Error", "HTTP connection cleanup: " + ioexception);
            }
        }
    }

    private void dumpToOutputStream(String s, boolean flag) throws java.io.IOException {
        if (flag) {
            rawOutput.write(COM.dragonflow.Utils.TextUtils.convertStringToBytes(s));
        } else {
            outputStream.println(s);
        }
    }

    private void flushOutputStream(boolean flag) throws java.io.IOException {
        if (flag) {
            if (rawOutput != null) {
                rawOutput.flush();
            }
        } else if (outputStream != null) {
            outputStream.flush();
        }
    }

    private void closeOutputStream(boolean flag) throws java.io.IOException {
        if (flag) {
            if (rawOutput != null) {
                rawOutput.close();
            }
        } else if (outputStream != null) {
            outputStream.close();
        }
    }

    private void status(String s) {
        if (debug) {
            java.lang.System.out.println(s);
        }
    }

    public static boolean isLicenseExpired() {
        return licenseExpired;
    }

    public static void setLicenseExpired(boolean flag) {
        licenseExpired = flag;
    }

    static {
        if (java.lang.System.getProperty("HTTP.enableProxy") != null) {
            enableProxy = true;
        }
        String s = java.lang.System.getProperty("HTTPRequestThread.debug", "false");
        if (s.equalsIgnoreCase("true")) {
            debug = true;
        }
    }
}
