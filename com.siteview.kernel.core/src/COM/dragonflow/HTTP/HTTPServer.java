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

import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.HashSet;

import COM.dragonflow.SiteView.SiteViewGroup;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.HTTP:
// HTTPRequestThread, VirtualDirectory

public class HTTPServer
    implements java.lang.Runnable
{

    private java.net.ServerSocket serverSocket;
    private boolean running;
    private Array virtualDirectories;
    private HashMap typeMap;
    private boolean sslEnabled;
    public boolean keepAliveEnabled;
    public int port;
    private int numConnections;
    private int maxConnections;
    protected java.lang.Thread serverThread;
    static boolean debug = false;
    static HashMap config;
    static String strPages[];
    static java.util.HashSet filter;
    long lastTime;

    public HTTPServer(int i, int j, boolean flag, boolean flag1)
    {
        serverSocket = null;
        running = true;
        virtualDirectories = new Array();
        typeMap = new HashMap();
        sslEnabled = false;
        keepAliveEnabled = false;
        port = 9999;
        numConnections = 0;
        maxConnections = 0;
        lastTime = 0L;
        port = i;
        sslEnabled = flag;
        keepAliveEnabled = flag1;
        if(j > 0)
        {
            maxConnections = j;
        }
        addType(".old", "text/plain");
        addType(".log", "text/plain");
        addType(".config", "text/plain");
        addType(".dyn", "text/plain");
        addType(".mg", "text/plain");
        addType(".html", "text/html");
        addType(".pdf", "application/pdf");
        addType(".htm", "text/html");
        addType(".gif", "image/gif");
        addType(".png", "image/png");
        addType(".jpg", "image/jpeg");
        addType(".jpeg", "image/jpeg");
        addType(".au", "audio/basic");
        addType(".wav", "audio/x-wav");
    }

    static void dump(String as[])
    {
        for(int i = 0; i < as.length; i++)
        {
            COM.dragonflow.Utils.TextUtils.debugPrint(i + ": " + as[i]);
        }

    }

    public void startServer()
        throws java.io.IOException
    {
        SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s = siteviewgroup.getSetting("_httpListenerIP");
        javax.net.ServerSocketFactory serversocketfactory = null;
        String s1 = "HTTP";
        if(sslEnabled)
        {
            serversocketfactory = COM.dragonflow.Utils.SocketStream.getSSLServerFactory();
            s1 = "HTTPS";
        }
        if(s.length() > 0)
        {
            java.net.InetAddress inetaddress = java.net.InetAddress.getByName(s);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Starting " + s1 + " Server listener on " + s + ":" + port);
            int i = 0;
            if(maxConnections > 0)
            {
                i = maxConnections;
            } else
            {
                i = 20;
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Number of maximum connections accepted is : " + i + " on " + s + ":" + port);
            if(serversocketfactory != null)
            {
                serverSocket = serversocketfactory.createServerSocket(port, i, inetaddress);
            } else
            {
                serverSocket = new ServerSocket(port, i, inetaddress);
            }
        } else
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Starting " + s1 + " Server listener on " + port);
            if(serversocketfactory != null)
            {
                serverSocket = serversocketfactory.createServerSocket(port);
            } else
            {
                serverSocket = new ServerSocket(port);
            }
        }
        if(sslEnabled)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", COM.dragonflow.Utils.SSLSocketStream.certificateDescription);
        }
        serverThread = new Thread(this);
        serverThread.setName("HTTP Server on port " + port);
        serverThread.setPriority(6);
        serverThread.start();
    }

    public void stopServer()
    {
        running = false;
        serverThread.stop();
        try
        {
            serverThread.join(15000L);
        }
        catch(java.lang.InterruptedException interruptedexception) { }
        COM.dragonflow.Log.LogManager.log("RunMonitor", "Shut down HTTP Server on port " + port);
        serverThread = null;
    }

    void timeStamp()
    {
        long l = java.lang.System.currentTimeMillis();
        if(lastTime != 0L)
        {
            java.lang.System.out.print((l - lastTime) + ":");
        }
        lastTime = l;
    }

    public void incConnections()
    {
        numConnections++;
    }

    public void decConnections()
    {
        numConnections--;
    }

    public int getConnections()
    {
        return numConnections;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public void run()
    {
        int i = 10000;
        try
        {
            do
            {
                if(!running)
                {
                    break;
                }
                i++;
                java.net.Socket socket = null;
                try
                {
                    if(debug)
                    {
                        java.lang.System.out.println(i + "-HTTPSERVER - waiting for a request ");
                    }
                    socket = serverSocket.accept();
                    if(debug)
                    {
                        java.lang.System.out.println(i + "-HTTPSERVER - got a request ");
                    }
                    if(getMaxConnections() > 0)
                    {
                        if(debug)
                        {
                            java.lang.System.out.println(i + "-HTTPSERVER - maxConnections > 0");
                        }
                        incConnections();
                    }
                }
                catch(java.io.IOException ioexception)
                {
                    java.lang.System.out.println("Could not accept() on port " + port + ": " + ioexception.getMessage());
                    continue;
                }
                if(debug)
                {
                    java.lang.System.out.println(i + "-HTTPSERVER - making new HTTPRequestThread ");
                }
                new HTTPRequestThread(this, socket, keepAliveEnabled);
                if(debug)
                {
                    java.lang.System.out.println(i + "-HTTPSERVER - back from HTTPRequestThread ");
                }
            } while(true);
        }
        finally
        {
            try
            {
                serverSocket.close();
            }
            catch(java.io.IOException ioexception1)
            {
                java.lang.System.out.println("Could not close server socket." + ioexception1.getMessage());
            }
            catch(java.lang.Exception exception1)
            {
                exception1.printStackTrace();
            }
        }
    }

    public void addVirtualDirectory(String s, String s1)
    {
        virtualDirectories.add(new VirtualDirectory(s, s1));
    }

    public void addCGIVirtualDirectory(String s, String s1)
    {
        virtualDirectories.add(new VirtualDirectory(s, s1, true));
    }

    COM.dragonflow.HTTP.VirtualDirectory getVirtualDirectory(String s)
    {
        for(Enumeration enumeration = (Enumeration) virtualDirectories.iterator(); enumeration.hasMoreElements();)
        {
            COM.dragonflow.HTTP.VirtualDirectory virtualdirectory = (COM.dragonflow.HTTP.VirtualDirectory)enumeration.nextElement();
            String s1 = virtualdirectory.getFullDocumentPath(s);
            if(s1 != null)
            {
                return virtualdirectory;
            }
        }

        return null;
    }

    public void addType(String s, String s1)
    {
        typeMap.put(s, s1);
    }

    String getType(String s)
    {
        String s1 = (String)typeMap.get(s);
        if(s1 == null)
        {
            s1 = "text/plain";
        }
        return s1;
    }

    static 
    {
        config = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        strPages = COM.dragonflow.Utils.TextUtils.split(COM.dragonflow.Utils.TextUtils.getValue(config, "_serverFilter").toLowerCase(), ";");
        filter = new HashSet();
        String s = java.lang.System.getProperty("HTTPServer.debug");
        if(s != null && s.length() > 0)
        {
            debug = true;
        }
        for(int i = 0; i < strPages.length; i++)
        {
            filter.add(strPages[i]);
        }

    }
}
