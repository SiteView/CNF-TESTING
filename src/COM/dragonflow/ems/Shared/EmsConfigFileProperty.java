// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:15:44
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EmsConfigFileProperty.java

package COM.dragonflow.ems.Shared;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
//import COM.dragonflow.topaz.ems.GenericProbe.Config.TestConfig;
//import COM.dragonflow.topaz.ems.GenericProbe.ExpressionLib.CompilationError;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogHelper;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory;
//import COM.dragonflow.topaz.ems.monitorComps.EmsReportTraits;
import java.io.File;
import java.io.FileNotFoundException;
import jgl.HashMap;

public class EmsConfigFileProperty extends StringProperty
{

    public EmsConfigFileProperty(String sMonitorName)
    {
        super("_emsConfigFilePath", "");
        m_sDefaultConfigFilePath = "";
        m_sConfigFilePath = "";
//        m_Logger = null;
        init(sMonitorName, "");
        setDisplayText("EMS Configuration File Path", "the path to the EMS integration configuration file.");
    }

    public EmsConfigFileProperty(String sMonitorName, String sDefaultConfigFileName)
    {
        super("_emsConfigFilePath", "");
        m_sDefaultConfigFilePath = "";
        m_sConfigFilePath = "";
//        m_Logger = null;
        init(sMonitorName, sDefaultConfigFileName);
        setDisplayText("EMS Configuration File Path", "the path to the EMS integration configuration file.");
    }

    public String getFileName(AtomicMonitor monitor)
    {
        if(monitor == null)
            return m_sDefaultConfigFilePath;
        m_sConfigFilePath = monitor.getProperty(this);
        if(m_sConfigFilePath != null)
            m_sConfigFilePath.trim();
        return m_sConfigFilePath == null || m_sConfigFilePath.equals("") ? m_sDefaultConfigFilePath : m_sConfigFilePath;
    }

    public void setFileName(String sConfigFilePath)
    {
        m_sConfigFilePath = sConfigFilePath;
    }

    protected void init(String sMonitorName, String sDefaultConfigFileName)
    {
        if(sMonitorName == null || sMonitorName.equals(""))
            return;
        if(sDefaultConfigFileName == null)
            sDefaultConfigFileName = "";
        if(sDefaultConfigFileName != null && !sDefaultConfigFileName.trim().equals(""))
            m_sDefaultConfigFilePath = createConfigFileName(sMonitorName, sDefaultConfigFileName);
        else
            m_sDefaultConfigFilePath = createConfigFileName(sMonitorName, getDefaultValue());
//        m_Logger = LogPolicyFactory.getInstance(sMonitorName).getLogger(sMonitorName);
//        if(m_Logger != null)
//            m_Logger.flow("EmsConfigFileProperty: setting default config file path to " + m_sDefaultConfigFilePath);
    }

    protected String getDefaultValue()
    {
//        if(EmsReportTraits.useUdx())
//            return "event.config";
//        else
            return "main.config";
    }

    protected static String createConfigFileName(String emsName, String fileName)
    {
        return EMS_ROOT_DIR + emsName + File.separator + fileName;
    }

    public static String getEventConfigFileName(String emsName, String val)
    {
        String name = "event.config";
        if(val != null && val.length() > 0)
            return val;
//        if(!EmsReportTraits.useUdx())
//            name = "main.config";
        return EMS_ROOT_DIR + emsName + File.separator + name;
    }

    public String verifyConfigFile(AtomicMonitor monitor, String value, HashMap errors)
    {
        String checkValue = "";
        if(value.length() > 0)
            checkValue = value;
        else
            checkValue = getFileName(monitor);
//        try
//        {
//            CompilationError warningsHolder = new CompilationError(-1, "");
//            TestConfig.testConfig(checkValue, warningsHolder);
//        }
//        catch(FileNotFoundException e)
//        {
//            errors.put(this, "The file \"" + checkValue + "\" does not exist");
//            return checkValue;
//        }
//        catch(CompilationError e)
//        {
//            errors.put(this, "Error in config file: " + e.getMessage());
//            return checkValue;
//        }
        return checkValue;
    }

    public static final String EMS_DIR_NAME = "ems";
    public static final String MAIN_CONFIG_FILE_NAME = "main.config";
    public static final String EVENT_CONFIG_FILE_NAME = "event.config";
    public static final String MEASUREMENT_CONFIG_FILE_NAME = "measurement.config";
    public static final String PN_EMS_CONFIG_FILE = "_emsConfigFilePath";
    protected static final String EMS_CONFIG_FILE_TEXT = "EMS Configuration File Path";
    protected static final String EMS_CONFIG_FILE_DESCRIPTION = "the path to the EMS integration configuration file.";
    private static String EMS_ROOT_DIR;
    private String m_sDefaultConfigFilePath;
    private String m_sConfigFilePath;
//    private LogHelper m_Logger;

    static 
    {
        EMS_ROOT_DIR = Platform.getRoot() + File.separator + "ems" + File.separator;
    }
}