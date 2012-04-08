// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:17
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.SiteViewGroup;
import java.io.PrintStream;

public class RunMonitor
{

    public RunMonitor()
    {
    }

    public static void main(String args[])
    {
        byte byte0 = 0;
        try
        {
            String s = args[0];
            String s1 = args[1];
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            AtomicMonitor atomicmonitor = (AtomicMonitor)siteviewgroup.getElement(s + "/" + s1);
            if(atomicmonitor == null)
            {
                System.out.println("monitor not found, group: " + s + ", id: " + s1);
                byte0 = 4;
            } else
            {
                atomicmonitor.testUpdate();
                System.out.println("group: " + s);
                System.out.println("monitor: " + s1);
                System.out.println("name: " + atomicmonitor.getProperty(AtomicMonitor.pName));
                System.out.println("status: " + atomicmonitor.getProperty(AtomicMonitor.pStateString));
                System.out.println("category: " + atomicmonitor.getProperty(AtomicMonitor.pCategory));
                String s2 = atomicmonitor.getProperty(AtomicMonitor.pCategory);
                if(s2.equals("good"))
                    byte0 = 1;
                else
                if(s2.equals("warning"))
                    byte0 = 2;
                else
                if(s2.equals("error"))
                    byte0 = 3;
            }
        }
        catch(Exception exception)
        {
            System.out.println("error: " + exception);
        }
        System.exit(byte0);
    }
}