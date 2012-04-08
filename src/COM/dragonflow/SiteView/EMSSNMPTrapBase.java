/*
 * 
 * Created on 2005-2-15 13:02:11
 *
 * EMSSNMPTrapBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>EMSSNMPTrapBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Observer;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
//import COM.dragonflow.topaz.ems.SNMPTrapSource.dispatcher.EMSIntegrationController;
//import COM.dragonflow.topaz.ems.SNMPTrapSource.util.LoggerRepository;

// Referenced classes of package COM.dragonflow.SiteView:
// SNMPTrapBase

public abstract class EMSSNMPTrapBase extends SNMPTrapBase {

    protected static EmsTimeDiffProperty m_EmsTimeDiff;

//    private EMSIntegrationController _emsIntegrationController;

    public EMSSNMPTrapBase() {
//        _emsIntegrationController = null;
    }

    protected abstract String getEmsConfigFilePath();

    protected abstract String getConfigurationPath();

    protected abstract StringProperty getEmsConfigFileProprty();

    protected String getLogFileName() {
        return "log.config";
    }

    protected String getBrand() {
        return "SNMP Trap Monitor";
    }

    public long getEmsTimeDiff() {
        return m_EmsTimeDiff == null ? 0L : m_EmsTimeDiff.getEmsTimeDiff(this);
    }

    protected void subscribeObserver(Observer ob) {
        SNMPTrapBase.getTrapSubject().addObserver(ob);
    }

    protected void unsubscribeObserver(Observer ob) {
        SNMPTrapBase.getTrapSubject().deleteObserver(ob);
    }

    protected void startMonitor() {
//        LoggerRepository.setLogConfigPath(getConfigurationPath()
//                + File.separator + getLogFileName());
//        _emsIntegrationController = new EMSIntegrationController(getFullID(),
//                getBrand());
//        if (_emsIntegrationController == null) {
//            return;
//        } else {
//            _emsIntegrationController.setEmsTimeDiff(getEmsTimeDiff());
//            _emsIntegrationController.initialize(getEmsConfigFilePath());
//            subscribeObserver(_emsIntegrationController);
//            super.startMonitor();
//            return;
//        }
    }

    protected void stopMonitor() {
//        if (_emsIntegrationController != null) {
//            unsubscribeObserver(_emsIntegrationController);
//            _emsIntegrationController = null;
//        }
        super.stopMonitor();
    }

    public String verify(StringProperty property, String value,
            HTTPRequest request, HashMap errors) {
        if (property == m_EmsTimeDiff) {
            return m_EmsTimeDiff == null ? "0" : m_EmsTimeDiff.verify(property,
                    value, request, errors);
        }
        if (property != null && property.getName().equals("_emsConfigFilePath")) {
            return ((EmsConfigFileProperty) property).verifyConfigFile(this,
                    value, errors);
        } else {
            return super.verify(property, value, request, errors);
        }
    }

    protected boolean isFileExist(String value) {
        File file = new File(value);
        return file.exists();
    }

    static {
        m_EmsTimeDiff = new EmsTimeDiffProperty("", "0");
        m_EmsTimeDiff.setParameterOptions(true, 100, true);
        StringProperty myProperties[] = { m_EmsTimeDiff };
        String fullClassName = (COM.dragonflow.SiteView.EMSSNMPTrapBase.class)
                .getName();
        PropertiedObject.addProperties(fullClassName, myProperties);
    }
}