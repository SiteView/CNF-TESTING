/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NetIQMeasurementMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NetIQMeasurementMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.*;
import COM.dragonflow.ems.xmlMonitor.EmsBaseMonitor;
import COM.dragonflow.ems.xmlMonitor.StaticInitializer;
//import COM.dragonflow.topaz.ems.NetIQ.NetIQMonitor;
//import COM.dragonflow.topaz.ems.NetIQ.NetIQMonitorConfig;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.*;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;
import java.util.Arrays;
import java.util.Vector;
import jgl.Array;
import jgl.HashMap;

public class NetIQMeasurementMonitor extends AtomicMonitor
//    implements JdbcMonitorCallback
{

    public static final String TOPOLOGY_FILE = "_pTopologyFile";
    private static final String EMS_NAME = "NetIQ";
    private static final String MONITOR_NAME = "COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor";
    private static StringProperty pStatus;
//    private MonitorEmulator emulator;

    public NetIQMeasurementMonitor()
    {
    }

    protected boolean update()
    {
        long start = getStartTime();
//        emulator.update(new JdbcMonitorContext(getUniqueInternalId(), getTopazParent(), new SimpleResultSetEnumerator(start, "ModificationTime")), this);
        runActionRules(this, getProperty(Monitor.pCategory));
        return true;
    }

    public void initialize(HashMap args)
    {
        super.initialize(args);
//        if(emulator == null)
//        {
//            emulator = MonitorEmulator.createInstance(new Integer(getUniqueInternalId()), this);
//        }
//        emulator.init(getConfig(args), isDisabled());
    }

//    protected NetIQMonitorConfig getConfig(HashMap args)
//    {
//        String userName = (String)args.get("_pDbUserName");
//        String password = (String)args.get("_pDbPassword");
//        String url = (String)args.get("_pDbUrl");
//        String driverName = (String)args.get("_pDbDriverName");
//        String configFilePath = getMeasurmentConfigFilePath("NetIQ", (String)args.get("_emsConfigFilePath"));
//        String topologyFilePath = (String)args.get("_pTopologyFile");
//        return new NetIQMonitorConfig(driverName, url, userName, password, configFilePath, topologyFilePath, getTimeDiffValue());
//    }

    public void notifyDelete()
    {
//        emulator.notifyDelete();
        super.notifyDelete();
    }

    public String getHostname()
    {
        return "NetIQ";
    }

    protected void stopMonitor()
    {
//        emulator.stop();
        super.stopMonitor();
    }

    public void setStartTime(String startTime)
    {
        setProperty("pStartTime", startTime);
    }

    private long getStartTime()
    {
        String measure = getProperty("pStartTime");
        long start = -1L;
        try
        {
            start = getLong(measure, start);
        }
        finally
        {
            if(start == -1L)
            {
                start = System.currentTimeMillis() / 1000L - getTimeDiffValue();
            }
        }
        return start;
    }

    public void setMonitorStateString(String stateString)
    {
        setProperty(Monitor.pStateString, stateString);
    }

//    public void setResultSetEnumerator(ResultSetEnumerator rse)
//    {
//        setStartTime(rse.getValue());
//    }
//
//    public JdbcMonitor createJdbcMonitor()
//    {
//        return new NetIQMonitor();
//    }

    protected static void handleEmsLicense(String className)
    {
        PropertiedObject.setClassProperty(className, "loadable", LUtils.isValidSSforXLicense(new EmsDummyMonitor()) ? "true" : "false");
    }

    protected String getMeasurmentConfigFilePath(String name, String value)
    {
        return EmsMeasurementConfigFileProperty.getMeasurementConfigFilePath(name, value);
    }

    protected long getTimeDiffValue()
    {
        long l = 0L;
        EmsTimeDiffProperty p = (EmsTimeDiffProperty)getPropertyObject("_emsTimeDiff");
        if(p != null)
        {
            l = p.getEmsTimeDiff(this);
        }
        return l;
    }

    public void setMonitorStatus(int status)
    {
        setProperty("pMStatus", Integer.toString(status));
        switch(status)
        {
        case 2: // '\002'
            setProperty(Monitor.pCategory, Monitor.ERROR_CATEGORY);
            break;

        case 1: // '\001'
            setProperty(Monitor.pCategory, Monitor.WARNING_CATEGORY);
            break;

        default:
            setProperty(Monitor.pCategory, Monitor.GOOD_CATEGORY);
            break;
        }
    }

    public boolean isEmsMaufacturer()
    {
        return true;
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors)
    {
        EmsTimeDiffProperty p = (EmsTimeDiffProperty)getPropertyObject("_emsTimeDiff");
        StringProperty ps = getPropertyObject("_pDbPassword");
        StringProperty user = getPropertyObject("_pDbUserName");
        if(property == p)
        {
            if(p != null)
            {
                return p.verify(property, value, request, errors);
            } else
            {
                return "0";
            }
        }
        if(property != null && property == ps || property == user)
        {
            if(value == null || "null".equals(value) || value.length() == 0)
            {
                errors.add(property, "You must enter Database user name and password");
            }
            return value;
        }
        if(property != null && property.getName().equals("_emsConfigFilePath"))
        {
            return ((EmsConfigFileProperty)property).verifyConfigFile(this, value, errors);
        } else
        {
            return super.verify(property, value, request, errors);
        }
    }

    public boolean runOwnRules()
    {
        return true;
    }

    public void setNumberOfMeasurements(long count)
    {
        setProperty("pCollected", Long.toString(count));
    }

    protected long getLong(String s, long defValue)
    {
        long l = defValue;
        if(s != null && s.length() > 0)
        {
            try
            {
                l = Long.parseLong(s);
            }
            catch(NumberFormatException e) { }
        }
        return l;
    }

    public Array getLogProperties()
    {
        Array a = super.getLogProperties();
        a.add(getPropertyObject("pCollected"));
        return a;
    }

    static 
    {
        String xmlName = Platform.getRoot() + "/classes/COM/dragonflow/topaz/ems/NetIQ/NetIQ.xml";
        StringProperty props[] = StaticInitializer.initialize(xmlName);
        pStatus = new StringProperty("pMStatus", Integer.toString(0));
        Vector vec = new Vector(Arrays.asList(props));
        vec.add(pStatus);
        PropertiedObject.addProperties("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", (StringProperty[])vec.toArray(props));
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "description", "Delivers " + EmsBaseMonitor.MEASUREMENT_TYPE.toLowerCase() + "s from " + "NetIQ" + " Server(s) to Topaz");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "title", "NetIQ " + EmsBaseMonitor.MEASUREMENT_TYPE);
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "topazName", "COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor" + EmsBaseMonitor.MEASUREMENT_TYPE);
        handleEmsLicense("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "class", "NetIQMeasurementMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "topazType", "System Resources");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "help", "NetIQMonitor.htm");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor", "classType", "<class_type>");
    }
}
