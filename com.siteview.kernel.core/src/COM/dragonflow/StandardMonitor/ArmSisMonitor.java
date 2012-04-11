/*
 * 
 * Created on 2005-3-5 14:18:52
 *
 * ArmSisMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ArmSisMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsMeasurementConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
import COM.dragonflow.ems.xmlMonitor.EmsBaseMonitor;
import COM.dragonflow.ems.xmlMonitor.StaticInitializer;
//import COM.dragonflow.topaz.ems.arm.ArmMonitor;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcConfiguration;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitor;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorCallback;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorContext;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;

public class ArmSisMonitor extends AtomicMonitor //implements JdbcMonitorCallback 
{

//    private MonitorEmulator emulator;

    private static final String EMS_NAME = "Application Relationship Mapping";

    private static final String NOT_AVAILABLE = "n/a";

    private static final String MONITOR_NAME = "COM.dragonflow.StandardMonitor.ArmSisMonitor";

    private static StringProperty pStatus;

    public ArmSisMonitor() {
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private Timestamp getStartTime() {
        String measure = getProperty("pStartTime");
        Timestamp start = null;
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
        EmsConfigFileProperty path = (EmsConfigFileProperty) getPropertyObject("_emsConfigFilePath");
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
//        obj[4] = EmsMeasurementConfigFileProperty.getMeasurementConfigFilePath("ApplicationRelationshipMappings", (String) args.get("_emsConfigFilePath"));
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

    public String getHostname() {
        return "Arm";
    }

//    public void setResultSetEnumerator(ResultSetEnumerator rse) {
//        setStartTime(Long.toString(rse.valueAsLong()));
//    }

    protected boolean update() {
        Timestamp start = getStartTime();
//        emulator.update(new JdbcMonitorContext(getUniqueInternalId(), getTopazParent(), new SimpleResultSetEnumerator(start, "ar.A_UPDATETIME")), this);
        return true;
    }

    public void setNumberOfMeasurements(long count) {
        setProperty("pCollected", Long.toString(count));
    }

//    public JdbcMonitor createJdbcMonitor() {
//        return new ArmMonitor();
//    }

    public Array getLogProperties() {
        Array a = super.getLogProperties();
        a.add(getPropertyObject("pCollected"));
        return a;
    }

    static {
        String xmlName = Platform.getRoot() + "/classes/COM/dragonflow/topaz/ems/arm/arm.xml";
        StringProperty props[] = StaticInitializer.initialize(xmlName);
        pStatus = new StringProperty("pMStatus");
        Vector vec = new Vector(Arrays.asList(props));
        vec.add(pStatus);
        PropertiedObject.addProperties("COM.dragonflow.StandardMonitor.ArmSisMonitor", (StringProperty[]) vec.toArray(props));
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "description", "Delivers " + EmsBaseMonitor.MEASUREMENT_TYPE.toLowerCase() + "s from " + "Application Relationship Mapping" + " Server(s) to Topaz");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "title", "Application Relationship Mapping " + EmsBaseMonitor.MEASUREMENT_TYPE);
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "topazName", "COM.dragonflow.StandardMonitor.ArmSisMonitor" + EmsBaseMonitor.MEASUREMENT_TYPE);
        handleEmsLicense("COM.dragonflow.StandardMonitor.ArmSisMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "class", "ArmSisMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "topazType", "System Resources");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "help", "AppRelMapMonitor.htm");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.ArmSisMonitor", "classType", "advanced");
    }
}