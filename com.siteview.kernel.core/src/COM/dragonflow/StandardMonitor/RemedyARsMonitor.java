/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * RemedyARsMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>RemedyARsMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewObject;
//import COM.dragonflow.SiteView.TopazConfigurator;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory;
//import COM.dragonflow.topaz.ems.RemedyARsSource.ARsTicketsState;
//import COM.dragonflow.topaz.ems.RemedyARsSource.RemedyARsException;
//import COM.dragonflow.topaz.ems.RemedyARsSource.settings.Configuration;
//import COM.dragonflow.topaz.ems.RemedyARsSource.settings.RemedyConfiguration;

public class RemedyARsMonitor extends AtomicMonitor {

    private static StringProperty s_pInterestingTicketsQuery;

    private static StringProperty s_pNumberOfTickets;

    private static StringProperty s_pUserName;

    private static StringProperty s_pPassword;

    private static StringProperty s_pServerName;

    private static StringProperty s_pRemedySchemaDefinitionFile;

    private static EmsConfigFileProperty s_pEmsConfigFilePath;

    private static EmsTimeDiffProperty s_pEmsTimeDiff;

    private static Object s_Lock = new Object();

    private static final int NUMBER_OF_TICKETS_PER_MONITOR_ERROR = 2000;

    private static final int NUMBER_OF_TICKETS_PER_MONITOR_WARNING = 1000;

    public RemedyARsMonitor() {
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors) {
        if (property == s_pNumberOfTickets) {
            try {
                int iNumOfTickets = StringProperty.toInteger(value);
                if (iNumOfTickets < 0) {
                    LogManager.log("Error", "RemedyARsMonitor::verify iNumOfTickets = " + iNumOfTickets);
                    iNumOfTickets = 0;
                } else if (iNumOfTickets > 2000) {
                    errors.put(property, property.getLabel() + " must be less than " + 2000);
                }
                value = String.valueOf(iNumOfTickets);
            } catch (NumberFormatException e) {
                errors.put(property, property.getLabel() + " is not a number");
                LogManager.log("Error", "RemedyARsMonitor::verify: exception: " + e);
                LogManager.logException(e);
            }
            return value;
        }
        if (property == s_pRemedySchemaDefinitionFile) {
            if (getProperty(s_pRemedySchemaDefinitionFile).trim().length() == 0) {
                errors.put(property, property.getLabel() + " should not be empty");
                return "Remedy.def";
            } else {
                return value;
            }
        }
        if (property == s_pEmsTimeDiff) {
            return s_pEmsTimeDiff == null ? "0" : s_pEmsTimeDiff.verify(property, value, request, errors);
        }
        if (property != null && property.getName().equals("_emsConfigFilePath")) {
            return ((EmsConfigFileProperty) property).verifyConfigFile(this, value, errors);
        } else {
            return super.verify(property, value, request, errors);
        }
    }

    public String getHostname() {
        String sEmsHostName = getProperty(s_pServerName);
        return sEmsHostName == null ? "Please select Remedy host name" : sEmsHostName;
    }

    public long getEmsTimeDiff() {
        return s_pEmsTimeDiff == null ? 0L : s_pEmsTimeDiff.getEmsTimeDiff(this);
    }

    public Array getLogProperties() {
        Array logProperties = super.getLogProperties();
        logProperties.add(s_pNumberOfTickets);
        return logProperties;
    }

    public void initialize(HashMap hashMap) {
        try {
            super.initialize(hashMap);
            LogManager.log("RunMonitor", "RemedyARsMonitor::initialize called");
//            if (!LogPolicyFactory.getInstance(Configuration.MONITOR_NAME).isInitialized()) {
//                LogPolicyFactory.getInstance(Configuration.MONITOR_NAME).init(Configuration.getLogFileName());
//            }
            updateConfiguration(true);
        } catch (LinkageError e) {
//            RemedyARsException.setRemedyStatus("Cannot find Remedy API libraries. Please look at the monitor documentation for the possible ways to correct this problem.");
            LogManager.log("Error", "RemedyARsMonitor::initialize failed, exception " + e);
            LogManager.logException(e);
        } catch (Throwable e) {
            LogManager.log("Error", "RemedyARsMonitor::initialize failed, exception " + e);
            LogManager.logException(e);
        }
    }

    /**
     * 
     */
    protected boolean update() {
        synchronized (RemedyARsMonitor.s_Lock) {
            int iNumberOfChangedTickets = 0;
            LogManager.log("RunMonitor", "RemedyARsMonitor::update started");
            if (isDisabled()) {
                LogManager.log("RunMonitor", "RemedyARsMonitor::update ended, monitor is disabled");
                return true;
            }

            try {
//                ARsTicketsState.getInstance().terminate();
//                RemedyARsException.setRemedyStatus("");
//                RemedyConfiguration.getInstance().load(getProperty(s_pRemedySchemaDefinitionFile));
                updateConfiguration(false);
                buildTicketsState();
//                sendTicketsToTopaz(TopazConfigurator.configInTopaz());
            } catch (LinkageError e) {
//                RemedyARsException.setRemedyStatus("Cannot find Remedy API libraries. Please look at the monitor documentation for the possible ways to correct this problem.");
                LogManager.log("Error", "RemedyARsMonitor::update failed, exception " + e);
                LogManager.logException(e);
            } catch (Throwable e) {
                LogManager.log("Error", "RemedyARsMonitor::update failed exception " + e);
                LogManager.logException(e);
            }
//            iNumberOfChangedTickets = ARsTicketsState.getInstance().getNumberOfChangedTickets();
//            String sRemedyStatus = RemedyARsException.getRemedyStatus();
//            if (sRemedyStatus != null && sRemedyStatus.length() > 0) {
//                setProperty(s_pNumberOfTickets, "n/a");
//                setProperty(Monitor.pStateString, sRemedyStatus);
//                setProperty(Monitor.pCategory, Monitor.ERROR_CATEGORY);
//            } else {
//                setProperty(s_pNumberOfTickets, iNumberOfChangedTickets);
//                sRemedyStatus = "" + iNumberOfChangedTickets + " interesting tickets " + (iNumberOfChangedTickets != 1 ? " have" : " has") + " been changed since the last update";
//                setProperty(Monitor.pStateString, sRemedyStatus);
//                LogManager.log("RunMonitor", "RemedyARsMonitor::update ended, number of tickets = " + iNumberOfChangedTickets);
//            }
            return true;
        }
    }

    protected void stopMonitor() {
        LogManager.log("RunMonitor", "RemedyARsMonitor::stopMonitor called");
//        ARsTicketsState.getInstance().terminate();
        super.stopMonitor();
    }

    protected String getEmsConfigFilePath() {
        return s_pEmsConfigFilePath.getFileName(this);
    }

    private boolean sendTicketsToTopaz(boolean bSendToTopaz) {
        try {
            if (bSendToTopaz) {
//                ARsTicketsState.getInstance().sendTicketsToTopaz();
            }
        } catch (Exception e) {
            LogManager.log("Error", "RemedyARsMonitor::sendTicketsToTopaz: exception: " + e);
            LogManager.logException(e);
            return false;
        }
        return true;
    }

    private void updateConfiguration(boolean bCheckInitialized) {
//        RemedyConfiguration.getInstance().setServerName(getProperty(s_pServerName));
//        RemedyConfiguration.getInstance().setUserName(getProperty(s_pUserName));
//        RemedyConfiguration.getInstance().setPassword(getProperty(s_pPassword));
//        RemedyConfiguration.getInstance().setAuthentication(getProperty(s_pPassword));
//        RemedyConfiguration.getInstance().setTicketsQuery(getProperty(s_pInterestingTicketsQuery));
//        Configuration.getInstance().setConfigFilePath(getEmsConfigFilePath());
//        if (!bCheckInitialized || !ARsTicketsState.getInstance().isInitialized()) {
//            ARsTicketsState.getInstance().initialize(getProperty(s_pRemedySchemaDefinitionFile), false);
//        }
//        ARsTicketsState.getInstance().setIsMonitorEnabled(!isDisabled());
//        ARsTicketsState.getInstance().setEmsTimeDiff(getEmsTimeDiff());
    }

    private void buildTicketsState() {
        try {
//            ARsTicketsState.getInstance().getTicketsFromRemedy();
        } catch (Exception e) {
            LogManager.log("Error", "RemedyARsMonitor::buildTicketsState: exception: " + e);
            LogManager.logException(e);
        }
    }

    static {
        try {
            boolean bIsEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
            s_pServerName = new StringProperty("_EmsHostServer", "", "Remedy Server");
            s_pServerName.setDisplayText("Remedy Server Name", "Remedy Server name to connect to");
            s_pServerName.setParameterOptions(true, 1, false);
            s_pUserName = new StringProperty("_EmsUser", "", "Remedy User Name");
            s_pUserName.setDisplayText("Remedy User Name", "user name used to connect to the Remedy server");
            s_pUserName.setParameterOptions(true, 2, false);
            s_pPassword = new StringProperty("_EmsPassword", "", "Remedy Password");
            s_pPassword.setDisplayText("Remedy Password", "password used to connect to the Remedy server");
            s_pPassword.setParameterOptions(true, 3, false);
            s_pPassword.isPassword = true;
//            s_pRemedySchemaDefinitionFile = new StringProperty("_remedySchemaDefinitionFile", Configuration.getConfigPath() + "Remedy.def");
            s_pRemedySchemaDefinitionFile.setDisplayText("Remedy Schema Definition File", "Remedy schema definition file");
            s_pRemedySchemaDefinitionFile.setParameterOptions(true, 4, false);
            s_pInterestingTicketsQuery = new StringProperty("_InterestingTicketsQuery", "'Status'<=\"Resolved\"");
            s_pInterestingTicketsQuery.setDisplayText("Interesting Tickets Query", "The Remedy query to match interesting tickets.");
            s_pInterestingTicketsQuery.setParameterOptions(true, 5, false);
//            s_pEmsConfigFilePath = new EmsConfigFileProperty(Configuration.MONITOR_NAME);
            s_pEmsConfigFilePath.setParameterOptions(true, 3, true);
            s_pNumberOfTickets = new NumericProperty("numberOfTickets");
            s_pNumberOfTickets.setLabel("Number of interesting tickets");
            s_pNumberOfTickets.setIsThreshold(true);
            s_pNumberOfTickets.setStateOptions(1);
            s_pEmsTimeDiff = new EmsTimeDiffProperty("", "0");
            s_pEmsTimeDiff.setParameterOptions(true, 100, true);
            StringProperty aProperties[] = { s_pNumberOfTickets, s_pRemedySchemaDefinitionFile, s_pServerName, s_pUserName, s_pPassword, s_pInterestingTicketsQuery, s_pEmsConfigFilePath, s_pEmsTimeDiff };
            String sFullClassName = (COM.dragonflow.StandardMonitor.RemedyARsMonitor.class).getName();
            PropertiedObject.addProperties(sFullClassName, aProperties);
            SiteViewObject.addClassElement(sFullClassName, Rule.stringToClassifier("numberOfTickets == n/a\terror"));
            SiteViewObject.addClassElement(sFullClassName, Rule.stringToClassifier("numberOfTickets > 1000\twarning", true));
            SiteViewObject.addClassElement(sFullClassName, Rule.stringToClassifier("always\tgood"));
            PropertiedObject.setClassProperty(sFullClassName, "description", "Reads the number of Interesting HelpDesk Tickets on a Remedy HelpDesk Server");
            PropertiedObject.setClassProperty(sFullClassName, "help", "RemedyARsMon.htm");
            PropertiedObject.setClassProperty(sFullClassName, "classType", "advanced");
            PropertiedObject.setClassProperty(sFullClassName, "title", "Remedy Ticketing");
            PropertiedObject.setClassProperty(sFullClassName, "class", "RemedyARsMonitor");
            PropertiedObject.setClassProperty(sFullClassName, "loadable", bIsEmsLicensed ? "true" : "false");
            PropertiedObject.setClassProperty(sFullClassName, "toolName", "Template Monitor Tool");
            PropertiedObject.setClassProperty(sFullClassName, "toolDescription", "Performs Custom Template Monitor test");
            PropertiedObject.setClassProperty(sFullClassName, "toolPageDisable", bIsEmsLicensed ? "true" : "false");
        } catch (Throwable e) {
            LogManager.log("Error", "RemedyARsMonitor::<static>: exception: " + e);
            LogManager.logException(e);
        }
    }
}
