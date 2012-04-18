 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package com.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Enumeration;

import com.dragonflow.Log.ConsoleErrorLogger;
import com.dragonflow.Log.FileLogger;
import com.dragonflow.Properties.FindObjectsOfClassVisitor;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils, FileUtils, ThreadLister

public class DebugWatcher
    implements java.lang.Runnable
{

    public static com.dragonflow.Utils.DebugWatcher debugWatcher = null;
    long printInterval;
    boolean periodicChecks;

    public static void initializeDebugWatcher(String s, boolean flag, boolean flag1)
    {
        if(flag)
        {
            com.dragonflow.Log.ConsoleErrorLogger consoleerrorlogger = new ConsoleErrorLogger();
            com.dragonflow.Log.LogManager.registerLogger("Debug", consoleerrorlogger);
        }
        try
        {
            com.dragonflow.Log.LogManager.registerLogger("Debug", new FileLogger(s + java.io.File.separator + "logs" + java.io.File.separator + "debug.log", 0xf4240));
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.err.println("Could not open debug log file");
        }
        if(flag1)
        {
            debugWatcher = new DebugWatcher();
        }
    }

    DebugWatcher()
    {
        printInterval = 60000L;
        periodicChecks = true;
        java.lang.Thread thread = new Thread(this);
        thread.setName("Debug Watcher");
        thread.setPriority(7);
        thread.setDaemon(true);
        thread.start();
    }

    public void run()
    {
        do
        {
            do
            {
                com.dragonflow.SiteView.Platform.sleep(printInterval);
            } while(!periodicChecks);
            runChecks();
        } while(true);
    }

    public void runChecks()
    {
        com.dragonflow.Utils.DebugWatcher.checkScheduler();
        com.dragonflow.Utils.DebugWatcher.checkMonitors();
        com.dragonflow.Utils.DebugWatcher.checkThreads();
    }

    public static String checkMonitors()
    {
        if(com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView != null)
        {
            com.dragonflow.Properties.FindObjectsOfClassVisitor findobjectsofclassvisitor = new FindObjectsOfClassVisitor("com.dragonflow.SiteView.AtomicMonitor");
            com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.acceptVisitor(findobjectsofclassvisitor);
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("------- Monitors -------\n");
            int i = 0;
            for(Enumeration enumeration = (Enumeration) findobjectsofclassvisitor.getResults().iterator(); enumeration.hasMoreElements(); stringbuffer.append("\n"))
            {
                com.dragonflow.SiteView.AtomicMonitor atomicmonitor = (com.dragonflow.SiteView.AtomicMonitor)enumeration.nextElement();
                i++;
                stringbuffer.append(atomicmonitor.getProperty(com.dragonflow.SiteView.AtomicMonitor.pRunning));
                stringbuffer.append("  ");
                com.dragonflow.Utils.TextUtils.appendStringRightJustify(stringbuffer, atomicmonitor.getProperty(com.dragonflow.SiteView.AtomicMonitor.pFrequency), 6);
                stringbuffer.append("  ");
                long l = 0L;
                try
                {
                    l = java.lang.Long.parseLong(atomicmonitor.getProperty(com.dragonflow.SiteView.AtomicMonitor.pLastUpdate));
                }
                catch(java.lang.NumberFormatException numberformatexception) { }
                if(l > 0L)
                {
                    stringbuffer.append(com.dragonflow.Utils.TextUtils.dateToString(new Date(l)));
                } else
                {
                    stringbuffer.append("    no sample    ");
                }
                stringbuffer.append("  ");
                stringbuffer.append(atomicmonitor.getProperty(com.dragonflow.SiteView.Monitor.pName));
            }

            stringbuffer.append("===  " + i + " monitors\n");
            String s = stringbuffer.toString();
            com.dragonflow.Log.LogManager.log("Debug", s);
            return s;
        } else
        {
            return "no SiteViewGroup object";
        }
    }

    public static String checkThreads()
    {
        String s = "";
        try
        {
            java.io.ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            java.io.PrintWriter printwriter = com.dragonflow.Utils.FileUtils.MakeOutputWriter(bytearrayoutputstream);
            com.dragonflow.Utils.ThreadLister.listAllThreads(printwriter);
            printwriter.flush();
            bytearrayoutputstream.flush();
            s = bytearrayoutputstream.toString();
            com.dragonflow.Log.LogManager.log("Debug", s);
        }
        catch(java.lang.Exception exception)
        {
            s = exception.toString();
        }
        return s;
    }

    public static String checkScheduler()
    {
        return com.dragonflow.Utils.DebugWatcher.checkScheduler("all");
    }

    public static String checkScheduler(String s)
    {
        String s1 = "";
        java.lang.Object obj = com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.monitorScheduler;
        if(obj != null && (s.equals("all") || s.equalsIgnoreCase("monitorScheduler")))
        {
            String s2 = ((com.dragonflow.SiteView.Scheduler) (obj)).internalState();
            s1 = s1 + s2;
            com.dragonflow.Log.LogManager.log("Debug", s2);
        }
        obj = com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.maintenanceScheduler;
        if(obj != null && (s.equals("all") || s.equalsIgnoreCase("maintenanceScheduler")))
        {
            String s3 = ((com.dragonflow.SiteView.Scheduler) (obj)).internalState();
            s1 = s1 + s3;
            com.dragonflow.Log.LogManager.log("Debug", s3);
        }
        obj = com.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.reportScheduler;
        if(obj != null && (s.equals("all") || s.equalsIgnoreCase("reportScheduler")))
        {
            String s4 = ((com.dragonflow.SiteView.Scheduler) (obj)).internalState();
            s1 = s1 + s4;
            com.dragonflow.Log.LogManager.log("Debug", s4);
        }
        obj = com.dragonflow.SiteView.Action.retryScheduler;
        if(obj != null && (s.equals("all") || s.equalsIgnoreCase("retryScheduler")))
        {
            String s5 = ((com.dragonflow.SiteView.Scheduler) (obj)).internalState();
            s1 = s1 + s5;
            com.dragonflow.Log.LogManager.log("Debug", s5);
        }
        return s1;
    }

    public static String lastLogLines()
    {
        String s = com.dragonflow.SiteView.Platform.getRoot() + "/logs/RunMonitor.log";
        Object obj = null;
        String s1 = "Could not access " + s;
        try
        {
            StringBuffer stringbuffer = new StringBuffer();
            java.io.RandomAccessFile randomaccessfile = new RandomAccessFile(s, "r");
            long l = randomaccessfile.length() - 8000L;
            if(l < 0L)
            {
                l = 0L;
            }
            for(String s2 = randomaccessfile.readLine(); (s2 = randomaccessfile.readLine()) != null;)
            {
                stringbuffer.append(s2);
                stringbuffer.append("\n");
            }

            randomaccessfile.close();
            s1 = stringbuffer.toString();
        }
        catch(java.io.IOException ioexception) { }
        return s1;
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        java.io.PrintWriter printwriter = com.dragonflow.Utils.FileUtils.MakeOutputWriter(java.lang.System.out);
        com.dragonflow.Utils.ThreadLister.listAllThreads(printwriter);
        printwriter.flush();
    }

}
