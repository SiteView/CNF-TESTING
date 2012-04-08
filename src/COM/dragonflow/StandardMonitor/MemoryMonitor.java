/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MemoryMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MemoryMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.TextUtils;

import java.net.URLEncoder;
import jgl.Array;

public class MemoryMonitor extends ServerMonitor
{

    static StringProperty pPercentFull;
    static StringProperty pFreeSpace;
    static RateProperty pPageFaultsPerSecond;
    static NumericProperty pLastMeasurement;
    static NumericProperty pLastPageFaults;

    public MemoryMonitor()
    {
    }

    protected boolean update()
    {
        String s = getProperty(pMachineName);
        long l = 0L;
        long l1 = 0L;
        try
        {
            l = Long.parseLong(getProperty(pLastMeasurement));
            l1 = Long.parseLong(getProperty(pLastPageFaults));
        }
        catch(NumberFormatException numberformatexception) { }
        Array array = null;
        if(monitorDebugLevel == 3)
        {
            array = new Array();
        }
        long al[] = Platform.getMemoryFull(s, l1, l, this, array);
        long l2 = al[0];
        long l3 = al[2];
        long l4 = l3 - al[1];
        long l5 = al[3];
        long l6 = al[4];
        l1 = al[5];
        l = al[6];
        long l7 = al[7];
        String s1 = "" + l4 / 0x100000L;
        float f = -1F;
        if(l5 != -1L && l6 != 0L)
        {
            float f1 = (float)l6 / (float)l7;
            f = (float)l5 / f1;
            if((double)f < 0.001D)
            {
                f = 0.0F;
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                setProperty(pLastPageFaults, l1);
                setProperty(pLastMeasurement, l);
                if(l2 == -1L)
                {
                    setProperty(pPercentFull, "n/a");
                    setProperty(pFreeSpace, "n/a");
                    setProperty(pPageFaultsPerSecond, "n/a");
                    setProperty(pMeasurement, 0);
                    setProperty(pStateString, "no data");
                    setProperty(pNoData, "n/a");
                    if(monitorDebugLevel == 3 && array != null)
                    {
                        StringBuffer stringbuffer = new StringBuffer();
                        for(int i = 0; i < array.size(); i++)
                        {
                            stringbuffer.append(array.at(i) + "\n");
                        }

                        LogManager.log("Error", "MemoryMonitor: " + getFullID() + " failed, output:\n" + stringbuffer);
                    }
                } else
                {
                    setProperty(pPercentFull, l2);
                    setProperty(pFreeSpace, l4 / 0x100000L);
                    setProperty(pMeasurement, getMeasurement(pPercentFull));
                    String s2 = l2 + "% used, " + s1 + "MB free";
                    if(f == -1F)
                    {
                        setProperty(pPageFaultsPerSecond, "n/a");
                    } else
                    {
                        setProperty(pPageFaultsPerSecond, f);
                        s2 = s2 + ", " + TextUtils.floatToString(f, 2) + " pages/sec";
                    }
                    setProperty(pStateString, s2);
                }
            }
        }
        return true;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pPercentFull);
        array.add(pFreeSpace);
        array.add(pPageFaultsPerSecond);
        return array;
    }

    public String getTestURL()
    {
        int i = Machine.getOS(getProperty(pMachineName));
        if(Platform.isWindows(i))
        {
            String s = URLEncoder.encode(getProperty(pMachineName));
            String s1 = "/SiteView/cgi/go.exe/SiteView?page=perfCounter&counterObject=Memory&machineName=" + s;
            return s1;
        } else
        {
            return null;
        }
    }

    static 
    {
        pPercentFull = new PercentProperty("percentFull");
        pPercentFull.setLabel("percent used");
        pPercentFull.setStateOptions(1);
        pFreeSpace = new NumericProperty("freeSpace", "0", "MB");
        pFreeSpace.setLabel("MB free");
        pFreeSpace.setStateOptions(2);
        pPageFaultsPerSecond = new RateProperty("pageFaultsPerSecond", "0", "pages", "seconds");
        pPageFaultsPerSecond.setLabel("pages/sec");
        pPageFaultsPerSecond.setStateOptions(3);
        pLastMeasurement = new NumericProperty("lastMeasurement");
        pLastPageFaults = new NumericProperty("lastPageFaults");
        StringProperty astringproperty[] = {
            pPercentFull, pFreeSpace, pPageFaultsPerSecond, pLastMeasurement, pLastPageFaults
        };
        addProperties("COM.dragonflow.StandardMonitor.MemoryMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.MemoryMonitor", Rule.stringToClassifier("percentFull > 90\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.MemoryMonitor", Rule.stringToClassifier("percentFull > 80\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.MemoryMonitor", Rule.stringToClassifier("percentFull == n/a\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.MemoryMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "description", "Measure virtual memory usage");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "help", "MemoryMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "title", "Memory");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "class", "MemoryMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "classType", "server");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "topazName", "Memory");
        setClassProperty("COM.dragonflow.StandardMonitor.MemoryMonitor", "topazType", "System Resources");
    }
}
