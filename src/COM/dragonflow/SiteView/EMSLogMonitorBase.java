/*
 * 
 * Created on 2005-2-15 12:59:51
 *
 * EMSLogMonitorBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>EMSLogMonitorBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.util.Vector;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
//import COM.dragonflow.topaz.ems.GenericProbe.Probe;
//import COM.dragonflow.topaz.ems.GenericProbe.EventActions.senders.measurements.ManufacturedMonitorContext;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogHelper;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory;
//import COM.dragonflow.topaz.ems.LogFileSource.LogFileSource;
//import COM.dragonflow.topaz.ems.LogFileSource.LogMonitorHelper;
//import COM.dragonflow.topaz.ems.monitorComps.EMSMonitorException;
//import COM.dragonflow.topaz.ems.monitorComps.EmsMonitorContext;
//import COM.dragonflow.topaz.ems.monitorComps.EmsReportTraits;

// Referenced classes of package COM.dragonflow.SiteView:
// LogMonitorBase, Monitor

public abstract class EMSLogMonitorBase extends LogMonitorBase {

    protected static EmsTimeDiffProperty m_EmsTimeDiff;

    private static BooleanProperty pIsErrorInStart;

//    private LogMonitorHelper helper;

    public static final String working_dir = "ems_work_dir_";

    public EMSLogMonitorBase() {
//        helper = null;
    }

    protected abstract String getEmsConfigFilePath();

    protected abstract ScalarProperty getResetFileProperty();

    protected abstract StringProperty getEmsConfigWorkingDir();

    protected abstract StringProperty getEmsConfigFilePathProperty();

    protected void startMonitor() {
        super.startMonitor();
//        LogHelper logger = InitLogger(getEmsConfigWorkingDir());
//        helper = new LogMonitorHelper(getFullID());
//        if (null != logger) {
//            helper.setLogger(logger);
//        }
//        LogFileSource source = new LogFileSource((String) getClassProperty("class"));
//        if (source != null) {
//            source.setEmsTimeDiff(getEmsTimeDiff());
//        }
//        EmsMonitorContext context = new EmsMonitorContext(getFullID(), getEmsConfigFilePath(), new Probe(new ManufacturedMonitorContext(getUniqueInternalId(), getTopazParent())), source);
//        try {
//            helper.onStartMonitor(context);
//        } catch (EMSMonitorException e) {
//            String s = "error: " + e.getMessage();
//            setProperty(Monitor.pStateString, I18N.StringToUnicode(s, I18N.nullEncoding()));
//            setProperty(Monitor.pCategory, Monitor.ERROR_CATEGORY);
//            setProperty(getMatchesProperty(), "n/a");
//            setProperty(pIsErrorInStart, "true");
//            return;
//        }
        setProperty(Monitor.pCategory, Monitor.GOOD_CATEGORY);
        setProperty(getMatchesProperty(), "0");
        setProperty(pIsErrorInStart, "false");
    }

    protected boolean update() {
        if (getProperty(pIsErrorInStart).equals("true")) {
            return false;
        } else {
            return super.update();
        }
    }

    protected int forwardAlerts(String message, String ruleFilter, StringBuffer details, long detailsMax) {
        int totalSent = 0;
//        try {
//            totalSent = helper.onForwardAlerts(message, ruleFilter, details, detailsMax, getProperty(getMatchProperty()), getPropertyAsInteger(getLinesProperty()));
//        } catch (EMSMonitorException e) {
//            setProperty(Monitor.pStateString, I18N.StringToUnicode("error: " + e.getMessage(), I18N.nullEncoding()));
//        }
        return totalSent;
    }

    public Vector getScalarValues(ScalarProperty property, HTTPRequest request, CGI cgi) {
        if (property == getResetFileProperty()) {
            Vector v = new Vector();
            v.addElement("none");
            v.addElement("Never");
            v.addElement("once");
            v.addElement("First Time Only");
            v.addElement("always");
            v.addElement("Always");
            return v;
        }
        Vector vValues = null;
        try {
            vValues = super.getScalarValues(property, request, cgi);
        } catch (SiteViewException e) {
            LogManager.log("Error", "Exception " + e);
        }
        return vValues;
    }

    public long getStartPosition(long endPosition, String logName) {
        String value = getValue("_resetFile");
        if (value == null || value.length() == 0 || value.equals("none")) {
            return super.getStartPosition(endPosition, logName);
        }
        if (getValue("_resetFile").equals("once")) {
            HashMap map = new HashMap();
            map.put(getResetFileProperty().getName(), "none");
            setProperty(getResetFileProperty(), "none");
            saveMonitor(map);
        }
        return 0L;
    }

    public long getEmsTimeDiff() {
        return m_EmsTimeDiff == null ? 0L : m_EmsTimeDiff.getEmsTimeDiff(this);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param path
     * @return
     */
//    protected LogHelper InitLogger(StringProperty path) {
//        try {
//            String name = (String) getClassProperty("class");
//            String configFilePath = getProperty(path);
////            LogPolicyFactory jdbcLogFactory = LogPolicyFactory.getInstance(name);
////            if (jdbcLogFactory != null) {
////
////                if (!jdbcLogFactory.isInitialized()) {
////                    jdbcLogFactory.init(configFilePath + "log.config");
////                }
////                return jdbcLogFactory.getLogger(name);
//            }
//            LogManager.log("Error", "Could not read from file " + path + "log.config .\nLogFactory not initialized");
//
//        } catch (IOException e) {
//            LogManager.log("Error", "Could not read from file " + path + "log.config .\nPlease make sure the file exsists and is readable.");
//        }
//        return null;
//    }

    protected static String chooseConfigFile() {
//        return EmsReportTraits.useUdx() ? "event.config" : "main.config";
      return "";
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors) {
        if (property == m_EmsTimeDiff) {
            return m_EmsTimeDiff == null ? "0" : m_EmsTimeDiff.verify(property, value, request, errors);
        }
        if (property != null && property.getName().equals("_emsConfigFilePath")) {
            return ((EmsConfigFileProperty) property).verifyConfigFile(this, value, errors);
        } else {
            return super.verify(property, value, request, errors);
        }
    }

    public boolean isEmsMaufacturer() {
        return true;
    }

    static {
        m_EmsTimeDiff = new EmsTimeDiffProperty("", "0");
        pIsErrorInStart = new BooleanProperty("isErrorInStart", "false");
        m_EmsTimeDiff.setParameterOptions(true, 100, true);
        StringProperty myProperties[] = { m_EmsTimeDiff, pIsErrorInStart };
        String fullClassName = (COM.dragonflow.SiteView.EMSLogMonitorBase.class).getName();
        PropertiedObject.addProperties(fullClassName, myProperties);
    }
}
