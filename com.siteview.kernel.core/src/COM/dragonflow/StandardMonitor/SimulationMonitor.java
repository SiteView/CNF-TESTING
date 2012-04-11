/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SimulationMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SimulationMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import java.util.Random;
import jgl.Array;

public class SimulationMonitor extends AtomicMonitor
{

    static StringProperty pResult;
    static StringProperty pReading;
    static StringProperty pSleepTime;

    public SimulationMonitor()
    {
    }

    public String getHostname()
    {
        return "SimHost";
    }

    protected boolean update()
    {
        Random random = new Random();
        Platform.sleep(getPropertyAsLong(pSleepTime));
        int i = getPropertyAsInteger(pReading);
        int j = Math.abs(random.nextInt()) % 11;
        j -= i / 10;
        i += j;
        if(i < 0)
        {
            i = 0;
        }
        if(i > 100)
        {
            i = 100;
        }
        int k = Math.abs(random.nextInt()) % 3;
        String s = "no data";
        switch(k)
        {
        case 0: // '\0'
            s = "Zero";
            break;

        case 1: // '\001'
            s = "One";
            break;

        case 2: // '\002'
            s = "Two";
            break;
        }
        if(stillActive())
        {
            synchronized(this)
            {
                setProperty(pResult, s);
                setProperty(pReading, String.valueOf(i));
                setProperty(pStateString, getProperty(pReading) + " / " + getProperty(pResult));
            }
        }
        return true;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pReading);
        array.add(pResult);
        return array;
    }

    static 
    {
        pResult = new StringProperty("result");
        pReading = new NumericProperty("reading", "0", "foobars");
        pReading.setLabel("reading");
        pSleepTime = new NumericProperty("_sleepTime", "1000");
        StringProperty astringproperty[] = {
            pResult, pReading, pSleepTime
        };
        addProperties("COM.dragonflow.StandardMonitor.SimulationMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.SimulationMonitor", Rule.stringToClassifier("reading >= 85\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.SimulationMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "description", "Simulated monitor for testing.");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "help", "SimMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "title", "Simulation");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "class", "SimulationMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "topazName", "Simulation");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "topazType", "System Resources");
        setClassProperty("COM.dragonflow.StandardMonitor.SimulationMonitor", "loadable", "false");
    }
}
