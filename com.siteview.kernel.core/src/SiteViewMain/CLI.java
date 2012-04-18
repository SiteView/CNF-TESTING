// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:17
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.StandardMonitor.URLMonitor;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.SocketSession;
import com.dragonflow.Utils.TextUtils;

public class CLI extends Thread
{

    public static void main(String args[])
        throws Exception
    {
        if(args.length < 1)
        {
            log("Usage: CLI");
            log("  -server=<domain name or ip address>");
            log("  method=edit");
            log("  object=globals | user");
            log("  name=<name of object>");
            log(" [<paramName>=<paramValue>]");
            log(" [-username=<siteview admin username>]");
            log(" [-password=<siteview admin password>]");
            log(" [-verbose=true]");
            log(" [-xml=true]");
            log(" [-file=<file of commands>");
            log(" [-threads=<number of parallel threads>");
            log(" [-proxy=<proxy server>");
            log(" [-proxyUsername=<username for proxy>");
            log(" [-proxyPassword=<password for proxy>");
            log(" [-timeout=<timeout in seconds for request>");
            log(" ");
            log("examples:");
            log(" CLI -server=demo.siteview.com");
            log(" ");
            log(" CLI -server=demo.siteview.com method=edit object=user name=administrator _password=abcd");
            log(" ");
            System.exit(0);
        }
        globals = parseParams(args);
        String s = (String)globals.get("-file");
        if(s != null)
        {
            StringBuffer stringbuffer = FileUtils.readFile(s);
            String args1[] = TextUtils.split(stringbuffer.toString(), "\n");
            for(int i = 0; i < args1.length; i++)
                requests.add(args1[i].trim());

        } else
        if(globals.get("-server") != null)
        {
            String s1 = (String)globals.get("-server");
            requests.add(s1);
        } else
        {
            log("error: missing server name");
        }
        runCommands();
        System.exit(0);
    }

    static HashMap parseParams(String as[])
    {
        HashMap hashmap = new HashMap();
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            int j = s.indexOf('=');
            if(j == -1)
            {
                if(hashmap.get("-server") == null)
                    hashmap.put("-server", s);
                else
                    log("error: parameter " + (i + 1) + " format, expected name=value, got " + s);
            } else
            {
                String s2 = s.substring(0, j);
                String s3 = s.substring(j + 1);
                hashmap.put(s2, s3);
            }
        }

        if(globals != null)
        {
            Enumeration enumeration = (Enumeration) globals.keys();
            do
            {
                if(!enumeration.hasMoreElements())
                    break;
                String s1 = (String)enumeration.nextElement();
                if(hashmap.get(s1) == null)
                    hashmap.put(s1, globals.get(s1));
            } while(true);
        }
        if(hashmap.get("-page") == null)
            hashmap.put("-page", "command");
        if(hashmap.get("-port") == null)
            hashmap.put("-port", "8888");
        return hashmap;
    }

    static void runCommands()
    {
        int i = TextUtils.toInt(TextUtils.getValue(globals, "-threads"));
        if(i <= 0)
            i = 1;
        if(i > 1)
            log("creating " + i + " threads");
        for(int j = 0; j < i; j++)
        {
            CLI cli = new CLI(j);
            cli.start();
        }

        activeThreads = i;
        do
            try
            {
                synchronized(signal)
                {
                    signal.wait();
                }
            }
            catch(InterruptedException interruptedexception) { }
        while(activeThreads != 0);
        log("exiting, successes=" + successes);
        if(errors != 0)
            log("exiting, errors=" + errors);
        log("exiting, requests=" + (errors + successes));
    }

    synchronized String nextRequest()
    {
        if(requests.size() == 0)
            return null;
        else
            return (String)requests.popFront();
    }

    public CLI(int i)
    {
        super((Runnable)null);
        index = 0;
        index = i;
    }

    public void run()
    {
        do
        {
            String s = nextRequest();
            if(s == null)
                break;
            handleRequest(s);
            try
            {
                Thread.sleep(100L);
            }
            catch(InterruptedException interruptedexception) { }
        } while(true);
        synchronized(signal)
        {
            activeThreads--;
            signal.notify();
        }
    }

    static synchronized void log(String s)
    {
        System.out.println(s);
        System.out.flush();
    }

    void handleRequest(String s)
    {
        HashMap hashmap = parseParams(TextUtils.split(s, ","));
        StringBuffer stringbuffer = null;
        if(hashmap.get("-verbose") != null)
            stringbuffer = new StringBuffer();
        String s1 = TextUtils.getValue(hashmap, "-port");
        String s2 = TextUtils.getValue(hashmap, "-server");
        String s3 = TextUtils.getValue(hashmap, "-username");
        String s4 = TextUtils.getValue(hashmap, "-password");
        String s5 = TextUtils.getValue(hashmap, "-proxy");
        String s6 = TextUtils.getValue(hashmap, "-proxyPassword");
        String s7 = TextUtils.getValue(hashmap, "-proxyUsername");
        int i = TextUtils.toInt(TextUtils.getValue(hashmap, "-timeout"));
        if(i <= 0)
            i = 60;
        i *= 1000;
        String s8 = TextUtils.getValue(hashmap, "-page");
        int j = TextUtils.toInt(TextUtils.getValue(hashmap, "-maxSize"));
        if(j <= 0)
            j = 50000;
        String s9 = "http://" + s2 + ":" + s1 + "/SiteView/cgi/go.exe/SiteView";
        if(stringbuffer != null)
            stringbuffer.append("sending request...\n url=" + s9 + "\n");
        Array array = new Array();
        array.add("page=" + s8);
        Enumeration enumeration = (Enumeration) hashmap.keys();
        do
        {
            if(!enumeration.hasMoreElements())
                break;
            String s10 = (String)enumeration.nextElement();
            if(!s10.startsWith("-"))
            {
                String s11 = s10 + "=" + hashmap.get(s10);
                if(stringbuffer != null)
                    stringbuffer.append(" " + s11 + "\n");
                array.add(s11);
            }
        } while(true);
        if(stringbuffer != null)
            stringbuffer.append("");
        StringBuffer stringbuffer1 = new StringBuffer();
        if(stringbuffer != null)
            log(stringbuffer.toString());
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, s9, "", "", s5, s7, s6, array, s3, s4, "", stringbuffer1, j, "", 0, i, null);
        long l = al[0];
        String s12 = stringbuffer1.toString();
        if(l == 200L)
        {
            String s13 = getXML(s12, "siteview-error");
            if(s13.length() > 0)
                l = TextUtils.toInt(s13);
            else
            if(s12.indexOf("SiteView Login") != -1)
                l = 401L;
        }
        String s14 = getXML(s12, "siteview-errormessage");
        if(s14.length() == 0)
            s14 = URLMonitor.lookupStatus(l);
        if(l == 200L)
            successes++;
        else
            errors++;
        socketsession.close();
        if(stringbuffer != null)
            log(stringbuffer1.toString());
        if(hashmap.get("-xml") != null)
        {
            String s15 = getXML(s12, "SOAP-Envelope");
            if(s15.length() == 0)
                s15 = "<SOAP-Fault>\n <faultcode>SOAP-Client</faultcode>\n <faultstring>Client Error</faultstring>\n <detail>\n <siteview-fault>\n  <siteview-error>" + l + "</siteview-error>\n" + "  <siteview-errormessage>" + s14 + "</siteview-errormessage>\n" + " </siteview-fault>\n" + " </detail>\n" + "</SOAP-Fault>";
            log(s15);
        } else
        {
            log("status=" + l + ", " + s14 + ", " + s2);
        }
    }

    String getXML(String s, String s1)
    {
        String s2 = "";
        String s3 = "<" + s1 + ">";
        String s4 = "</" + s1 + ">";
        int i = s.indexOf(s3);
        int j = s.indexOf(s4);
        if(i != -1 && j != -1 && i < j)
            s2 = s.substring(i + s3.length(), j);
        return s2;
    }

    static Object signal = new Object();
    static HashMap globals = null;
    static int activeThreads = 0;
    static Array requests = new Array();
    static int errors = 0;
    static int successes = 0;
    int index;

}