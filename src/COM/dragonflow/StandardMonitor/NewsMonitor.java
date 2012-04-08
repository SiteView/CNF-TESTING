/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NewsMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NewsMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.*;
import java.net.*;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            NewsGroupStatus, NewsAuthorizationException, URLMonitor

public class NewsMonitor extends AtomicMonitor
{

    static StringProperty pServer;
    static StringProperty pNewsGroups;
    static StringProperty pTimeout;
    static StringProperty pUserName;
    static StringProperty pPassword;
    static StringProperty pLocalIP;
    static StringProperty pStatus;
    static StringProperty pRoundTripTime;
    static StringProperty pArticleCount;
    static boolean debug = false;

    public NewsMonitor()
    {
    }

    public static String newsStatusToString(String s)
    {
        int i = StringProperty.toInteger(s);
        switch(i)
        {
        case 200: 
        case 201: 
            return "ready";

        case 400: 
            return "service discontinued";

        case 500: 
            return "command not recognized";

        case 501: 
            return "command syntax error";

        case 502: 
            return "login failed";

        case 503: 
            return "program fault";
        }
        if(i < 200)
        {
            return "";
        }
        if(i < 400)
        {
            return "ok";
        }
        if(i < 500)
        {
            return "error";
        } else
        {
            return "command error";
        }
    }

    public String getHostname()
    {
        return getProperty(pServer);
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=news&server=" + getProperty(pServer);
        if(getProperty(pUserName).length() > 0)
        {
            s = s + "&username=" + getProperty(pUserName) + "&password=" + URLEncoder.encode(TextUtils.obscure(getProperty(pPassword)));
        }
        if(getProperty(pNewsGroups).length() > 0)
        {
            s = s + "&newsgroups=" + getProperty(pNewsGroups);
        }
        return s;
    }

    protected boolean update()
    {
        int i = StringProperty.toInteger(getProperty(pTimeout));
        if(i == 0)
        {
            i = URLMonitor.DEFAULT_TIMEOUT;
        } else
        {
            i *= 1000;
        }
        String as[] = TextUtils.split(getProperty(pNewsGroups), ",");
        NewsGroupStatus anewsgroupstatus[] = new NewsGroupStatus[as.length];
        long l = Platform.timeMillis();
        long l1 = (long)i + Platform.timeMillis();
        StringBuffer stringbuffer = new StringBuffer();
        int j = checkNews(this, getProperty(pServer), stringbuffer, as, anewsgroupstatus, getProperty(pUserName), getProperty(pPassword), l1, null);
        String s = stringbuffer.toString();
        long l2 = Platform.timeMillis() - l;
        if(stillActive())
        {
            synchronized(this)
            {
                if(j == kURLok || j == kMonitorSpecificError)
                {
                    String s1 = TextUtils.floatToString((float)l2 / 1000F, 2) + " sec";
                    setProperty(pRoundTripTime, l2);
                    int k = 0;
                    for(int i1 = 0; i1 < anewsgroupstatus.length; i1++)
                    {
                        if(anewsgroupstatus[i1] == null)
                        {
                            continue;
                        }
                        k += anewsgroupstatus[i1].articleCount;
                        if(anewsgroupstatus[i1].status.equals("211"))
                        {
                            continue;
                        }
                        if(s.length() > 0)
                        {
                            s = s + ", ";
                        }
                        s = s + as[i1] + " " + anewsgroupstatus[i1].message;
                    }

                    if(s.length() > 0)
                    {
                        if(s.startsWith("\t"))
                        {
                            s = s.substring(1);
                        }
                        s = TextUtils.replaceChar(s, '\t', ", ");
                        j = kMonitorSpecificError;
                        setProperty(pStateString, s);
                    } else
                    {
                        String s2 = s1;
                        if(anewsgroupstatus.length > 0)
                        {
                            s2 = s2 + ", " + k + " articles in ";
                            if(anewsgroupstatus.length == 1)
                            {
                                s2 = s2 + as[0];
                            } else
                            {
                                s2 = s2 + anewsgroupstatus.length + " groups";
                            }
                        }
                        setProperty(pStateString, s2);
                    }
                    setProperty(pArticleCount, k);
                } else
                {
                    setProperty(pStateString, URLMonitor.lookupStatus(j));
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pArticleCount, "n/a");
                    setProperty(pNoData, "n/a");
                }
                setProperty(pStatus, j);
            }
        }
        return true;
    }

    public static int checkNews(String s, StringBuffer stringbuffer, String as[], NewsGroupStatus anewsgroupstatus[], String s1, String s2, PrintWriter printwriter)
    {
        return checkNews(null, s, stringbuffer, as, anewsgroupstatus, s1, s2, Platform.timeMillis() + (long)(URLMonitor.DEFAULT_TIMEOUT * 1000), printwriter);
    }

    public static int checkNews(NewsMonitor newsmonitor, String s, StringBuffer stringbuffer, String as[], NewsGroupStatus anewsgroupstatus[], String s1, String s2, long l, PrintWriter printwriter)
    {
        int i = 119;
        int j = s.indexOf(':');
        if(j != -1)
        {
            i = TextUtils.readInteger(s, j + 1);
            s = s.substring(0, j);
        }
        Socket socket = null;
        BufferedReader bufferedreader = null;
        PrintWriter printwriter1 = null;
        int k = URLMonitor.kURLok;
        try
        {
            if(newsmonitor != null)
            {
                String s3 = newsmonitor.getProperty(pLocalIP);
                int i1 = 5656;
                if(s3.length() > 0)
                {
                    int j1 = s3.indexOf(":");
                    if(j1 >= 0)
                    {
                        i1 = TextUtils.toInt(s3.substring(j1 + 1));
                        s3 = s3.substring(0, j1);
                    }
                    socket = getLocalSocket(s, i, s3, i1, 25);
                }
            }
            if(socket == null)
            {
                socket = new Socket(s, i);
            }
            printwriter1 = FileUtils.MakeOutputWriter(socket.getOutputStream());
            bufferedreader = FileUtils.MakeInputReader(socket.getInputStream());
            String s4 = readLine(bufferedreader, socket, l, printwriter);
            boolean flag = true;
            if(s4.startsWith("200") || s4.startsWith("201"))
            {
                try
                {
                    if(flag)
                    {
                        for(int k1 = 0; k1 < as.length; k1++)
                        {
                            if(newsmonitor != null)
                            {
                                newsmonitor.progressString += "Checking group " + as[k1] + "\n";
                            }
                            anewsgroupstatus[k1] = checkNewsgroup(bufferedreader, printwriter1, socket, l, as[k1], s1, s2, printwriter);
                        }

                    }
                }
                catch(NewsAuthorizationException newsauthorizationexception1)
                {
                    s4 = doCommand("QUIT", bufferedreader, printwriter1, socket, l, s1, s2, printwriter);
                    if(!s4.startsWith("205"))
                    {
                        stringbuffer.append("\tCould not quit - " + newsStatusToString(s4.substring(0, 3)));
                    }
                    throw newsauthorizationexception1;
                }
            } else
            if(s4.startsWith("502"))
            {
                stringbuffer.append("\tpermission denied for connection");
            } else
            {
                stringbuffer.append("\tCould not connect - " + newsStatusToString(s4.substring(0, 3)));
            }
        }
        catch(UnknownHostException unknownhostexception)
        {
            k = kURLBadHostNameError;
        }
        catch(SocketException socketexception)
        {
            if(Platform.noRoute(socketexception))
            {
                k = kURLNoRouteToHostError;
            } else
            {
                k = kURLNoConnectionError;
            }
        }
        catch(NewsAuthorizationException newsauthorizationexception)
        {
            k = kMonitorSpecificError;
            stringbuffer.append("\t" + newsauthorizationexception.getMessage());
        }
        catch(InterruptedIOException interruptedioexception)
        {
            k = kURLTimeoutError;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            System.err.println(exception + " error: " + exception.getMessage());
            k = kURLUnknownError;
        }
        finally
        {
            try
            {
                if(printwriter1 != null)
                {
                    printwriter1.close();
                }
                if(bufferedreader != null)
                {
                    bufferedreader.close();
                }
                if(socket != null)
                {
                    socket.close();
                }
            }
            catch(IOException ioexception)
            {
                System.err.println("Could not close NewsMonitor streams");
            }
        }
        return k;
    }

    static NewsGroupStatus checkNewsgroup(BufferedReader bufferedreader, PrintWriter printwriter, Socket socket, long l, String s, String s1, String s2, 
            PrintWriter printwriter1)
        throws IOException, NewsAuthorizationException
    {
        String s3 = doCommand("GROUP " + s, bufferedreader, printwriter, socket, l, s1, s2, printwriter1);
        String as[] = TextUtils.split(s3);
        NewsGroupStatus newsgroupstatus = new NewsGroupStatus();
        newsgroupstatus.line = s3;
        if(as.length > 0)
        {
            newsgroupstatus.status = as[0];
            if(newsgroupstatus.status.equals("411"))
            {
                newsgroupstatus.message = "not found";
            } else
            if(newsgroupstatus.status.equals("211"))
            {
                if(as.length < 5)
                {
                    newsgroupstatus.message = "error in format of response";
                } else
                {
                    newsgroupstatus.articleCount = StringProperty.toInteger(as[1]);
                }
            } else
            {
                newsgroupstatus.message = newsStatusToString(newsgroupstatus.status);
            }
        } else
        {
            newsgroupstatus.message = "no response";
        }
        return newsgroupstatus;
    }

    static String doCommand(String s, BufferedReader bufferedreader, PrintWriter printwriter, Socket socket, long l, String s1, String s2, 
            PrintWriter printwriter1)
        throws IOException, NewsAuthorizationException
    {
        writeLine(printwriter, s, printwriter1);
        String s3 = readLine(bufferedreader, socket, l, printwriter1);
        if(s3.startsWith("480"))
        {
            if(s1.length() == 0)
            {
                throw new NewsAuthorizationException("authorization expected");
            }
            writeLine(printwriter, "AUTHINFO USER " + s1, printwriter1);
            s3 = readLine(bufferedreader, socket, l, printwriter1);
            if(s3.startsWith("381"))
            {
                writeLine(printwriter, "AUTHINFO PASS " + s2, printwriter1);
                s3 = readLine(bufferedreader, socket, l, printwriter1);
                if(s3.startsWith("502"))
                {
                    throw new NewsAuthorizationException("login failed, unauthorized");
                }
                if(!s3.startsWith("281"))
                {
                    throw new NewsAuthorizationException("unknown denied for login");
                }
            }
            writeLine(printwriter, s, printwriter1);
            s3 = readLine(bufferedreader, socket, l, printwriter1);
        }
        return s3;
    }

    static String readLine(BufferedReader bufferedreader, Socket socket, long l, PrintWriter printwriter)
        throws IOException
    {
        int i = (int)(l - Platform.timeMillis());
        if(i <= 0)
        {
            throw new InterruptedIOException();
        }
        Platform.setSocketTimeout(socket, i);
        String s = bufferedreader.readLine();
        if(debug)
        {
            System.out.println("Debug: News IN  =" + s);
        }
        if(printwriter != null)
        {
            printwriter.println("Received: " + s);
        }
        return s;
    }

    static void writeLine(PrintWriter printwriter, String s, PrintWriter printwriter1)
    {
        if(debug)
        {
            System.out.println("Debug: News OUT =" + s);
        }
        if(printwriter1 != null)
        {
            printwriter1.println("Sent: " + s);
        }
        printwriter.print(s + URLMonitor.CRLF);
        printwriter.flush();
    }

    public static Socket getLocalSocket(String s, int i, String s1, int j, int k)
        throws IOException
    {
        int l = 0;
        InetAddress inetaddress = InetAddress.getByName(s1);
        Socket socket;
        for(socket = null; socket == null;)
        {
            l++;
            try
            {
                socket = PlatformNew.createSocketOnLocalPort(s, i, inetaddress, j);
            }
            catch(IOException ioexception)
            {
                if(l > k)
                {
                    throw ioexception;
                }
                System.out.println("Socket open port " + j + " : " + ioexception);
                j++;
            }
        }

        return socket;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pArticleCount);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pServer)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if(stringproperty == pNewsGroups)
        {
            s = TextUtils.removeChars(s, " \n\t");
            return s;
        }
        if(stringproperty == pTimeout)
        {
            if(TextUtils.digits(s) != s.length())
            {
                hashmap.put(stringproperty, "time out must be a number");
            } else
            if(TextUtils.toInt(s) < 1)
            {
                hashmap.put(stringproperty, "time out must be greater than 0");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pDiagnosticText)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            } else
            {
                return diagnostic(getProperty(pServer), getPropertyAsInteger(pStatus));
            }
        } else
        {
            return super.getProperty(stringproperty);
        }
    }

    public static String newsStatus(String s, String s1, String s2, String s3, PrintWriter printwriter)
    {
        String as[] = TextUtils.split(s3, ",");
        NewsGroupStatus anewsgroupstatus[] = new NewsGroupStatus[as.length];
        StringBuffer stringbuffer = new StringBuffer();
        long l = Platform.timeMillis();
        int i = checkNews(s, stringbuffer, as, anewsgroupstatus, s1, s2, printwriter);
        long l1 = Platform.timeMillis() - l;
        String s4 = stringbuffer.toString();
        if(i == kURLok || i == kMonitorSpecificError)
        {
            String s5 = TextUtils.floatToString((float)l1 / 1000F, 2) + " sec";
            String s6 = "";
            int j = 0;
            for(int k = 0; k < anewsgroupstatus.length; k++)
            {
                if(anewsgroupstatus[k] == null)
                {
                    continue;
                }
                j += anewsgroupstatus[k].articleCount;
                if(anewsgroupstatus[k].status.equals("211"))
                {
                    continue;
                }
                if(s4.length() > 0)
                {
                    s4 = s4 + ", ";
                }
                s4 = s4 + as[k] + " " + anewsgroupstatus[k].message;
            }

            if(s4.length() > 0)
            {
                if(s4.startsWith("\t"))
                {
                    s4 = s4.substring(1);
                }
                s6 = TextUtils.replaceChar(s4, '\t', ", ");
            } else
            {
                s6 = s5;
                if(anewsgroupstatus.length > 0)
                {
                    s6 = s6 + ", " + j + " articles in ";
                    if(anewsgroupstatus.length == 1)
                    {
                        s6 = s6 + as[0];
                    } else
                    {
                        s6 = s6 + anewsgroupstatus.length + " groups";
                    }
                }
            }
            return s6;
        } else
        {
            return URLMonitor.lookupStatus(i);
        }
    }

    static 
    {
        pServer = new StringProperty("_server", "", "news server");
        pServer.setDisplayText("News Server", "the IP address or host name of the news server (examples: 206.168.191.21 or news." + Platform.exampleDomain + ")");
        pServer.setParameterOptions(true, 1, false);
        pNewsGroups = new StringProperty("_newsgroups");
        pNewsGroups.setDisplayText("News Groups", "optional news groups that will be checked - separate multiple groups with commas (examples: misc.jobs.offered");
        pNewsGroups.setParameterOptions(true, 2, false);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for the information about the news groups to be retrieved");
        pTimeout.setParameterOptions(true, 3, true);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("User Name", "optional user name if the News server requires authorization");
        pUserName.setParameterOptions(true, 4, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "optional password if the News server requires authorization");
        pPassword.setParameterOptions(true, 5, true);
        pPassword.isPassword = true;
        pLocalIP = new StringProperty("_localIP");
        pLocalIP.setDisplayText("Connect From", "optional IP address to connect from");
        pLocalIP.setParameterOptions(true, 6, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pArticleCount = new NumericProperty("articleCount", "0", "articles");
        pArticleCount.setLabel("number of articles");
        pArticleCount.setStateOptions(2);
        pStatus = new StringProperty("status", "no data");
        StringProperty astringproperty[] = {
            pServer, pNewsGroups, pTimeout, pUserName, pPassword, pLocalIP, pStatus, pRoundTripTime, pArticleCount
        };
        addProperties("COM.dragonflow.StandardMonitor.NewsMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.NewsMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.NewsMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.NewsMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "description", "Connects to a news (NNTP) server and verifies that groups can be retrieved.");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "help", "NewsMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "title", "News");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "class", "NewsMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "toolName", "Check News Server");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "toolDescription", "Check whether a News Server is operational.");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "topazName", "NNTP");
        setClassProperty("COM.dragonflow.StandardMonitor.NewsMonitor", "topazType", "Web Application Server");
        if(System.getProperty("NewsMonitor.debug") != null)
        {
            debug = true;
        }
    }
}
