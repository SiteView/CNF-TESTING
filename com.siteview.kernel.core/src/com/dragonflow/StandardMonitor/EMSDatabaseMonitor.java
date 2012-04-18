/*
 * 
 * Created on 2005-3-7 1:07:03
 *
 * EMSDatabaseMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>EMSDatabaseMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.PropertiedObject;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteView.SiteViewObject;
import com.dragonflow.SiteViewException.SiteViewException;
import com.dragonflow.Utils.EmsDummyMonitor;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.ems.Shared.EmsConfigFileProperty;
import com.dragonflow.ems.Shared.EmsTimeDiffProperty;
import com.dragonflow.ems.xmlMonitor.StaticInitializer;
//import com.dragonflow.topaz.ems.JDBCSource.enum.EnumeratorHelper;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcConfiguration;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitor;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorCallback;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorContext;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.JdbcMonitorVerificationException;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.impl.JdbcConfigurationImpl;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.impl.JdbcMonitorImpl;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.ResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.jdbcMonitor.util.SimpleResultSetEnumerator;
//import com.dragonflow.topaz.ems.monitorComps.pullMonitor.lifeCycle.MonitorEmulator;

public class EMSDatabaseMonitor extends AtomicMonitor
{
//implements JdbcMonitorCallback {

//    private MonitorEmulator emulator;

    private static final String EMS_NAME = "DB";

    private static final String NOT_AVAILABLE = "n/a";

    private static final String MONITOR_NAME = "com.dragonflow.StandardMonitor.EMSDatabaseMonitor";

    private static StringProperty pStatus;

    private static ScalarProperty pFieldType;

    private static Set mandatoryProps;

    public EMSDatabaseMonitor() {
    }

    public Vector getScalarValues(ScalarProperty property, HTTPRequest request, CGI cgi) {
        Vector result = new Vector();
        if (property == pFieldType) {
//            result.add(EnumeratorHelper.TIME);
//            result.add(EnumeratorHelper.TIME);
//            result.add(EnumeratorHelper.INT);
//            result.add(EnumeratorHelper.INT);
//            result.add(EnumeratorHelper.DOUBLE);
//            result.add(EnumeratorHelper.DOUBLE);
//            result.add(EnumeratorHelper.LONG);
//            result.add(EnumeratorHelper.LONG);
            return result;
        }
        Vector vValues = null;
        try {
            vValues = super.getScalarValues(property, request, cgi);
        } catch (SiteViewException e) {
        }
        return vValues;
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
    }

    public boolean isEmsMaufacturer() {
        return true;
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors) {
        if (mandatoryProps.contains(property.getName())) {
            if (value == null || value.length() == 0) {
                errors.put(property, "value required");
                return value;
            }
        } else if (property.getName().equals("_emsTimeDiff")) {
//            try {
//                SimpleResultSetEnumerator rse = createEnumertor();
//                JdbcMonitorImpl.Test(createConfiguration(), rse);
//            } catch (JdbcMonitorVerificationException e) {
//                switch (e.errorCode()) {
//                case -22:
//                    errors.put(getPropertyObject("_emsConfigFilePath"), e.getMessage());
//                    break;
//
//                case -21:
//                    errors.put(getPropertyObject("_emsConfigFilePath"), e.getMessage());
//                    break;
//
//                case -32:
//                    errors.put(getPropertyObject("_driver"), e.getMessage());
//                    break;
//
//                case -31:
//                    errors.put(getPropertyObject("_database"), e.getMessage());
//                    break;
//
//                case -12:
//                    errors.put(getPropertyObject("_emsTablePrimaryKeys"), e.getMessage());
//                    break;
//
//                case -11:
//                    errors.put(getPropertyObject("_fieldType"), e.getMessage());
//                    break;
//
//                case -14:
//                    errors.put(getPropertyObject("_select"), e.getMessage());
//                    break;
//
//                case -13:
//                    errors.put(getPropertyObject("_resetEmum"), e.getMessage());
//                    break;
//                }
//            }
        }
        return super.verify(property, value, request, errors);
    }

//    private JdbcConfigurationImpl createConfiguration() {
//        EmsConfigFileProperty p = (EmsConfigFileProperty) getPropertyObject("_emsConfigFilePath");
//        String configurationFilePath = p.getFileName(this);
//        long timeDiff = getTimeDiffValue();
//        String select = getProperty("_select");
//        String from = getProperty("_from");
//        String where = getProperty("_where");
//        String orderby = getProperty("_orderBy");
//        String driver = getProperty("_driver");
//        String url = getProperty("_database");
//        String user = getProperty("_user");
//        String password = getProperty("_password");
//        return new JdbcConfigurationImpl(select, from, where, orderby, driver, url, user, password, configurationFilePath, timeDiff);
//    }
//
    public void initialize(HashMap args) {
        super.initialize(args);
//        if (emulator == null) {
//            emulator = MonitorEmulator.createInstance(new Integer(getUniqueInternalId()), this);
//        }
//        JdbcConfiguration cfg = createJdbcConf(args);
//        emulator.init(cfg, isDisabled());
    }

//    private JdbcConfiguration createJdbcConf(HashMap args) {
//        EmsConfigFileProperty p = (EmsConfigFileProperty) getPropertyObject("_emsConfigFilePath");
//        String configurationFilePath = p.getFileName(this);
//        long timeDiff = getTimeDiffValue();
//        return new JdbcConfigurationImpl((String) args.get("_select"), (String) args.get("_from"), (String) args.get("_where"), (String) args.get("_orderBy"), (String) args.get("_driver"), (String) args.get("_database"), (String) args.get("_user"),
//                (String) args.get("_password"), configurationFilePath, timeDiff);
//    }

    public void notifyDelete() {
//        emulator.notifyDelete();
        super.notifyDelete();
    }

    protected void stopMonitor() {
//        emulator.stop();
        super.stopMonitor();
    }

    private void setStartTime(String startTime) {
        setProperty("lastEmum", startTime);
    }

    public void setMonitorStateString(String stateString) {
        setProperty(Monitor.pStateString, stateString);
    }

//    public void setResultSetEnumerator(ResultSetEnumerator rse) {
//        setStartTime(EnumeratorHelper.getHelper(getProperty("_fieldType")).getString2Property(rse));
//    }

    protected boolean update() {
//        SimpleResultSetEnumerator rse = null;
//        try {
//            rse = createEnumertor();
//            emulator.update(new JdbcMonitorContext(getUniqueInternalId(), getTopazParent(), rse), this);
//        } catch (JdbcMonitorVerificationException e) {
//            setMonitorStateString(e.getMessage());
//            setMonitorStatus(2);
//        }
        return true;
    }

    /**
     * 
     * 
     * @return
     * @throws JdbcMonitorVerificationException
     */
//    private SimpleResultSetEnumerator createEnumertor() throws JdbcMonitorVerificationException {
//        String mg = getProperty("_resetEmum");
//        String dyn = getProperty("lastEmum");
//        String type = getProperty("_fieldType");
//        String name = getProperty("_emsTablePrimaryKeys");
//        try {
//            SimpleResultSetEnumerator rse = EnumeratorHelper.getHelper(type).getNewEnumerator(name, mg, dyn);
//            return rse;
//        } catch (NumberFormatException e) {
//            throw new JdbcMonitorVerificationException(e, -13);
//        } catch (IllegalArgumentException e) {
//            throw new JdbcMonitorVerificationException(e, -13);
//        }
//    }

    public void setNumberOfMeasurements(long count) {
        setProperty("pCollected", Long.toString(count));
    }

//    public JdbcMonitor createJdbcMonitor() {
//        return new JdbcMonitorImpl();
//    }

    public Array getLogProperties() {
        Array a = super.getLogProperties();
        a.add(getPropertyObject("pCollected"));
        return a;
    }

    public String getHostname() {
        return "EMS db Monitor";
    }

    static {
        mandatoryProps = new TreeSet();
        mandatoryProps.add("_driver");
        mandatoryProps.add("_database");
        mandatoryProps.add("_select");
        mandatoryProps.add("_from");
        mandatoryProps.add("_emsTablePrimaryKeys");
        mandatoryProps.add("lastEmum");
        mandatoryProps.add("_user");
        mandatoryProps.add("_password");
        String xmlName = Platform.getRoot() + "/classes/COM/dragonflow/topaz/ems/JDBCSource/db.xml";
        StringProperty props[] = StaticInitializer.initialize(xmlName);
        pFieldType = new ScalarProperty("_fieldType");
        pFieldType.setParameterOptions(true, true, 10, false);
        pFieldType.setDisplayText("Enumerating Field Type", "type of field used to order this query");
        pStatus = new StringProperty("pMStatus");
        pStatus.setIsThreshold(false);
        Vector vec = new Vector(Arrays.asList(props));
        vec.add(pStatus);
        vec.add(pFieldType);
        PropertiedObject.addProperties("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", (StringProperty[]) vec.toArray(props));
        SiteViewObject.addClassElement("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", Rule.stringToClassifier("pMStatus == 2\terror", false));
        SiteViewObject.addClassElement("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", Rule.stringToClassifier("pMStatus == 1\twarning", false));
        SiteViewObject.addClassElement("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", Rule.stringToClassifier("pMStatus == 0\tgood", false));
        SiteViewObject.addClassElement("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", Rule.stringToClassifier("always\tgood"));
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "description", "Sends Topaz data retireved from a database by connecting to it and performing a query.\t");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "title", "EMS Database");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "topazName", "EMS Database");
        handleEmsLicense("com.dragonflow.StandardMonitor.EMSDatabaseMonitor");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "class", "EMSDatabaseMonitor");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "topazType", "System Resources");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "help", "EMSDatabaseMonitor.htm");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "toolName", "Database Connection");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "toolDescription", "Provides an interface to test a JDBC or ODBC connection to a database.");
        PropertiedObject.setClassProperty("com.dragonflow.StandardMonitor.EMSDatabaseMonitor", "classType", "advanced");
    }
}