// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:18
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.SiteView.SiteViewGroup;
import com.dragonflow.Utils.CommandLine;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.MailUtils;
import com.dragonflow.Utils.TextUtils;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package SiteViewMain:
//            ServicePrinter

public class Service
{

    public Service()
    {
    }

    public static void main(String args[])
    {
        progressFile = new File(Platform.getRoot() + "/htdocs/Progress.html");
        if(args.length >= 1)
            pid = args[0];
        System.out.println("starting service process: " + pid);
        String s = "";
        if(args.length >= 2)
            s = " " + args[1];
        String s1 = Platform.getRoot() + "/groups/pid";
        Platform.setService(true);
        try
        {
            FileOutputStream fileoutputstream = new FileOutputStream(s1);
            PrintWriter printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            printwriter.println(pid);
            printwriter.close();
            fileoutputstream.close();
        }
        catch(Exception exception)
        {
            System.out.println("error writing pid: " + s1);
            System.out.println(exception);
        }
        String s2 = Platform.getRoot() + "/classes/start-monitor x " + pid + s;
        startProcess(s2);
        do
        {
            if((new File(Platform.expiredName())).exists())
            {
                System.out.println(Platform.productName + "stopping: evaluation license expired");
                break;
            }
            Platform.sleep(checkInterval);
            if(!processOK())
                startProcess(s2);
        } while(true);
    }

    static void startProcess(String s)
    {
        System.out.println("starting monitor process: " + s);
        try
        {
            Process process = CommandLine.execSync(s);
            com.dragonflow.Utils.ThreadPool.SingleThread singlethread = ServicePrinter.servicePrinterThreadPool.getThread();
            ServicePrinter serviceprinter = new ServicePrinter();
            serviceprinter.stream = FileUtils.MakeInputReader(process.getInputStream());
            singlethread.activate(serviceprinter);
            com.dragonflow.Utils.ThreadPool.SingleThread singlethread1 = ServicePrinter.servicePrinterThreadPool.getThread();
            ServicePrinter serviceprinter1 = new ServicePrinter();
            serviceprinter1.stream = FileUtils.MakeInputReader(process.getErrorStream());
            singlethread1.activate(serviceprinter1);
            Platform.sleep(10000L);
        }
        catch(Exception exception) { }
    }

    static void statusMail(String s, String s1)
    {
        System.out.println(s1);
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s2 = TextUtils.getValue(hashmap, "_autoEmail");
        String s3 = TextUtils.getValue(hashmap, "_autoStartup");
        if(s3.length() != 0 && s2.length() != 0)
        {
            Array array = new Array();
            try
            {
                array = (new CommandLine()).exec("/bin/tail " + Platform.getRoot() + "/logs/RunMonitor.log");
            }
            catch(Exception exception) { }
            String s4 = TextUtils.getValue(hashmap, "_webserverAddress");
            if(s4.length() != 0)
                s = s + "(" + s4 + ")";
            s1 = s1 + "\n\nSiteView Log\n\n";
            for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();)
            {
                String s5 = (String)enumeration.nextElement();
                s1 = s1 + s5 + "\n";
            }

            MailUtils.mail(hashmap, s2, s, s1);
        }
    }

    static boolean processOK()
    {
        boolean flag = true;
        int i = SiteViewGroup.monitorPID();
        String s = Platform.processOK("" + i);
        if(s.length() == 0)
        {
            System.out.println(Platform.productName + " monitor process: " + i + " is running");
            if(progressFile.exists())
            {
                long l = progressFile.lastModified();
                long l1 = Platform.timeMillis();
                if(l1 - l > (long)maxWait)
                {
                    flag = false;
                    kill();
                    statusMail(Platform.productName + " restarted", Platform.productName + " monitoring process restarted, process not responding");
                }
            }
        } else
        {
            if(s.equals("defunct"))
                kill();
            System.out.println(Platform.productName + " monitor process: " + i + " is " + s);
            statusMail(Platform.productName + " restarted", Platform.productName + " monitoring process restarted, process " + s);
            flag = false;
        }
        return flag;
    }

    static void kill()
    {
        int i = SiteViewGroup.monitorPID();
        System.out.println("stopping monitor process: " + i);
        if(i != 0)
        {
            String s = "kill -9 " + i;
            try
            {
                String as[] = {
                    "/bin/sh", "-c", s
                };
                CommandLine.execSync(as);
            }
            catch(Exception exception) { }
        }
    }

    static File progressFile;
    static int maxWait = 0xdbba0;
    static int checkInterval = 60000;
    static String pid = "";

}