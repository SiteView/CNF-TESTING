/*
 * 
 * Created on 2005-3-7 0:54:08
 *
 * ConcordSisMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>ConcordSisMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.PropertiedObject;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.*;
import com.dragonflow.Utils.EmsDummyMonitor;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.ems.Shared.*;
import com.dragonflow.ems.xmlMonitor.EmsBaseMonitor;
import com.dragonflow.ems.xmlMonitor.StaticInitializer;
//import com.dragonflow.topaz.ems.concord.ConcordMonitor;
//import com.dragonflow.topaz.ems.concord.ConcordMonitorConfiguration;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.*;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;
import java.util.Arrays;
import java.util.Vector;
import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class ConcordSisMonitor extends AtomicMonitor
// implements JdbcMonitorCallback
{

// private MonitorEmulator emulator;
 private static final String EMS_NAME = "Concord";
 private static final String NOT_AVAILABLE = "n/a";
 private static final String MONITOR_NAME = "com.dragonflow.StandardMonitor.ConcordSisMonitor";
 private static StringProperty pStatus;

 public ConcordSisMonitor()
 {
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
     EmsConfigFileProperty path = (EmsConfigFileProperty)getPropertyObject("_emsConfigFilePath");
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

 public void initialize(HashMap args)
 {
     super.initialize(args);
//     if(emulator == null)
//     {
//         emulator = MonitorEmulator.createInstance(new Integer(getUniqueInternalId()), this);
//     }
//     JdbcConfiguration cfg = createJdbcConf(args);
//     emulator.init(cfg, isDisabled());
 }

// private JdbcConfiguration createJdbcConf(HashMap args)
// {
//     Object obj[] = new Object[7];
//     obj[0] = args.get("_pDbSelect");
//     obj[1] = args.get("_pDbWhere");
//     obj[2] = args.get("_pDbDriverName");
//     obj[3] = args.get("_pDbUrl");
//     obj[4] = args.get("_pDbUserName");
//     obj[5] = args.get("_pDbPassword");
//     obj[6] = getMeasurmentConfigFilePath("Concord", (String)args.get("_emsConfigFilePath"));
//     for(int i = 0; i < obj.length; i++)
//     {
//         obj[i] = obj[i] != null ? obj[i] : ((Object) (new String("")));
//     }
//
//     return new ConcordMonitorConfiguration(obj[0].toString(), obj[1].toString(), obj[2].toString(), obj[3].toString(), obj[4].toString(), obj[5].toString(), obj[6].toString(), getTimeDiffValue());
// }

 public void notifyDelete()
 {
//     emulator.notifyDelete();
     super.notifyDelete();
 }

 protected void stopMonitor()
 {
//     emulator.stop();
     super.stopMonitor();
 }

 public void setStartTime(String startTime)
 {
     setProperty("pStartTime", startTime);
 }

 public void setMonitorStateString(String stateString)
 {
     setProperty(Monitor.pStateString, stateString);
 }

 public String getHostname()
 {
     return "concord";
 }

// public void setResultSetEnumerator(ResultSetEnumerator rse)
// {
//     setStartTime(rse.getValue());
// }

 protected boolean update()
 {
     long start = getStartTime();
//     emulator.update(new JdbcMonitorContext(getUniqueInternalId(), getTopazParent(), new SimpleResultSetEnumerator(start, "SAMPLE_TIME")), this);
     return true;
 }

 public void setNumberOfMeasurements(long count)
 {
     setProperty("pCollected", Long.toString(count));
 }

// public JdbcMonitor createJdbcMonitor()
// {
//     return new ConcordMonitor();
// }

 public Array getLogProperties()
 {
     Array a = super.getLogProperties();
     a.add(getPropertyObject("pCollected"));
     return a;
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

 static 
 {
     String xmlName = Platform.getRoot() + "/classes/COM/dragonflow/topaz/ems/concord/concord.xml";
     StringProperty props[] = StaticInitializer.initialize(xmlName);
     pStatus = new StringProperty("pMStatus");
     Vector vec = new Vector(Arrays.asList(props));
     vec.add(pStatus);
     PropertiedObject.addProperties("com.dragonflow.StandardMonitor.ConcordSisMonitor", (StringProperty[])vec.toArray(props));
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "description", "Delivers " + EmsBaseMonitor.MEASUREMENT_TYPE.toLowerCase() + "s from " + "Concord" + " Server(s) to Topaz");
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "title", "Concord " + EmsBaseMonitor.MEASUREMENT_TYPE);
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "topazName", "com.dragonflow.StandardMonitor.ConcordSisMonitor" + EmsBaseMonitor.MEASUREMENT_TYPE);
     handleEmsLicense("com.dragonflow.StandardMonitor.ConcordSisMonitor");
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "class", "ConcordSisMonitor");
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "topazType", "System Resources");
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "help", "ConcordMonitor.htm");
     PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.ConcordSisMonitor", "classType", "advanced");
 }
}
