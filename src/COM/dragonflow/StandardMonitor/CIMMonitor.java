/*
 * 
 * Created on 2005-3-7 0:51:47
 *
 * CIMMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

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
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
import COM.dragonflow.ems.xmlMonitor.EmsBaseMonitor;
import COM.dragonflow.ems.xmlMonitor.StaticInitializer;
//import COM.dragonflow.topaz.ems.cim.CimMonitor;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcConfiguration;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitor;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorCallback;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorContext;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import COM.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;

public class CIMMonitor extends AtomicMonitor
{
	// implements JdbcMonitorCallback {

//    private MonitorEmulator emulator;

    private static final String EMS_NAME = "Compaq Insight Manager";

    private static final String NOT_AVAILABLE = "n/a";

    private static final String MONITOR_NAME = "COM.dragonflow.StandardMonitor.CIMMonitor";

    private static StringProperty pStatus;

    public CIMMonitor() {
    }

    /**
     * CAUTION: Decompiled by hand.
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
        PropertiedObject.addProperties("COM.dragonflow.StandardMonitor.CIMMonitor", (StringProperty[]) vec.toArray(props));
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "description", "Delivers " + EmsBaseMonitor.ALERT_TYPE.toLowerCase() + "s from " + "Compaq Insight Manager" + " Server(s) to Topaz");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "title", "Compaq Insight Manager " + EmsBaseMonitor.ALERT_TYPE);
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "topazName", "COM.dragonflow.StandardMonitor.CIMMonitor" + EmsBaseMonitor.ALERT_TYPE);
        handleEmsLicense("COM.dragonflow.StandardMonitor.CIMMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "class", "CIMMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "topazType", "System Resources");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "help", "CIMMonitor.htm");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.CIMMonitor", "classType", "advanced");
    }
}