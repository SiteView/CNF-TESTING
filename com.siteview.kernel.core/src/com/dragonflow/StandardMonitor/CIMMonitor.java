/*
 * 
 * Created on 2005-3-7 0:51:47
 *
 * CIMMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>CIMMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.PropertiedObject;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.Utils.EmsDummyMonitor;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.ems.Shared.EmsConfigFileProperty;
import com.dragonflow.ems.Shared.EmsTimeDiffProperty;
import com.dragonflow.ems.xmlMonitor.EmsBaseMonitor;
import com.dragonflow.ems.xmlMonitor.StaticInitializer;
//import com.dragonflow.topaz.ems.cim.CimMonitor;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcConfiguration;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitor;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorCallback;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorContext;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;

public class CIMMonitor extends AtomicMonitor
{
	// implements JdbcMonitorCallback {

//    private MonitorEmulator emulator;

    private static final String EMS_NAME = "Compaq Insight Manager";

    private static final String NOT_AVAILABLE = "n/a";

    private static final String MONITOR_NAME = "com.dragonflow.StandardMonitor.CIMMonitor";

    private static StringProperty pStatus;

    public CIMMonitor() {
    }

    /**
     * 
     * 
     * @return
     */
    private Timestamp getStartTime() {
        Timestamp start;
        String measure = getProperty("pStartTime");
        start = null;
        try {
            if (measure != null && measure.length() > 4) {
                start = new Timestamp(Long.parseLong(measure));
            }
            if (start == null) {
                start = new Timestamp(System.currentTimeMillis() - getTimeDiffValue() * 1000L);
            }
        } catch (Throwable t) {
            if (start == null) {
                start = new Timestamp(System.currentTimeMillis() - getTimeDiffValue() * 1000L);
            }
        } finally {
            if (start == null) {
                start = new Timestamp(System.currentTimeMillis() - getTimeDiffValue() * 1000L);
            }
        }
        return start;
    }

    protected long getLong(String s, long defValue) {
        long l = defValue;
        if (s != null && s.length() > 0) {
            try {
                l = Long.parseLong(s);
            } catch (NumberFormatException e) {
            }
        }
        return l;
    }

    protected static void handleEmsLicense(String className) {
        PropertiedObject.setClassProperty(className, "loadable", LUtils.isValidSSforXLicense(new EmsDummyMonitor()) ? "true" : "false");
    }

    protected long getTimeDiffValue() {
        long l = 0L;
        EmsTimeDiffProperty p = (EmsTimeDiffProperty) getPropertyObject("_emsTimeDiff");
        if (p != null) {
            l = p.getEmsTimeDiff(this);
        }
        return l;
    }

    public void setMonitorStatus(int status) {
        setProperty("pMStatus", Integer.toString(status));
        switch (status) {
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

    public boolean isEmsMaufacturer() {
        return true;
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors) {
        EmsTimeDiffProperty p = (EmsTimeDiffProperty) getPropertyObject("_emsTimeDiff");
        StringProperty ps = getPropertyObject("_pDbPassword");
        StringProperty user = getPropertyObject("_pDbUserName");
        if (property == p) {
            if (p != null) {
                return p.verify(property, value, request, errors);
            } else {
                return "0";
            }
        }
        if (property != null && property == ps || property == user) {
            if (value == null || "null".equals(value) || value.length() == 0) {
                errors.add(property, "You must enter Database user name and password");
            }
            return value;
        }
        if (property != null && property.getName().equals("_emsConfigFilePath")) {
            return ((EmsConfigFileProperty) property).verifyConfigFile(this, value, errors);
        } else {
            return super.verify(property, value, request, errors);
        }
    }

    public boolean runOwnRules() {
        return true;
    }

    public void initialize(HashMap args) {
        super.initialize(args);
//        if (emulator == null) {
//            emulator = MonitorEmulator.createInstance(new Integer(getUniqueInternalId()), this);
//        }
//        JdbcConfiguration cfg = createJdbcConf(args);
//        emulator.init(cfg, isDisabled());
    }

//    private JdbcConfiguration createJdbcConf(HashMap args) {
//        Object obj[] = new Object[5];
//        obj[0] = args.get("_pDbDriverName");
//        obj[1] = args.get("_pDbUrl");
//        obj[2] = args.get("_pDbUserName");
//        obj[3] = args.get("_pDbPassword");
//        obj[4] = EmsConfigFileProperty.getEventConfigFileName("CIM", (String) args.get("_emsConfigFilePath"));
//        for (int i = 0; i < obj.length; i ++) {
//            obj[i] = obj[i] != null ? obj[i] : ((Object) (new String("")));
//        }
//
//        return new JdbcConfiguration(obj[0].toString(), obj[1].toString(), obj[2].toString(), obj[3].toString(), obj[4].toString(), getTimeDiffValue());
//    }

    public void notifyDelete() {
//        emulator.notifyDelete();
        super.notifyDelete();
    }

    protected void stopMonitor() {
//        emulator.stop();
        super.stopMonitor();
    }

    public void setStartTime(String startTime) {
        setProperty("pStartTime", startTime);
    }

    public void setMonitorStateString(String stateString) {
        setProperty(Monitor.pStateString, stateString);
    }

//    public void setResultSetEnumerator(ResultSetEnumerator rse) {
//        setStartTime(Long.toString(rse.valueAsLong()));
//    }

    protected boolean update() {
        Timestamp start = getStartTime();
//        emulator.update(new JdbcMonitorContext(getUniqueInternalId(), getTopazParent(), new SimpleResultSetEnumerator(start, "notices.LastModified")), this);
        return true;
    }

    public void setNumberOfMeasurements(long count) {
        setProperty("pCollected", Long.toString(count));
    }

//    public JdbcMonitor createJdbcMonitor() {
//        return new CimMonitor();
//    }

    public Array getLogProperties() {
        Array a = super.getLogProperties();
        a.add(getPropertyObject("pCollected"));
        return a;
    }

    public String getHostname() {
        return "CIM";
    }

    static {
        String xmlName = Platform.getRoot() + "/classes/COM/dragonflow/topaz/ems/cim/cim.xml";
        StringProperty props[] = StaticInitializer.initialize(xmlName);
        pStatus = new StringProperty("pMStatus");
        Vector vec = new Vector(Arrays.asList(props));
        vec.add(pStatus);
        PropertiedObject.addProperties("com.dragonflow.StandardMonitor.CIMMonitor", (StringProperty[]) vec.toArray(props));
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "description", "Delivers " + EmsBaseMonitor.ALERT_TYPE.toLowerCase() + "s from " + "Compaq Insight Manager" + " Server(s) to Topaz");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "title", "Compaq Insight Manager " + EmsBaseMonitor.ALERT_TYPE);
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "topazName", "com.dragonflow.StandardMonitor.CIMMonitor" + EmsBaseMonitor.ALERT_TYPE);
        handleEmsLicense("com.dragonflow.StandardMonitor.CIMMonitor");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "class", "CIMMonitor");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "topazType", "System Resources");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "help", "CIMMonitor.htm");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.CIMMonitor", "classType", "advanced");
    }
}