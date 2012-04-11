/*
 * 
 * Created on 2005-3-5 14:21:24
 *
 * BMCPatrolProbeMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>BMCPatrolProbeMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.BMCProbeComponent;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.PtiServiceRepository;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.pti.PtiService;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.settings.BMCPatrolLoggerRepository;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.settings.LoggerCategory;
//import COM.dragonflow.topaz.ems.BMCPatrolSource.statistics.StatisticsCollector;
//import COM.dragonflow.topaz.ems.GenericProbe.util.log.LogHelper;
//import COM.dragonflow.topaz.ems.tdm.backend.bmc.settings.BackendParameters;
//import COM.dragonflow.topaz.ems.tdm.backend.bmc.settings.BackendSettings;
import java.io.File;
import java.net.InetAddress;
import java.util.Vector;
import jgl.Array;
import jgl.HashMap;

public class BMCPatrolProbeMonitor extends AtomicMonitor
{

 private static final String FQDN_CLASSNAME = "COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor";
 static StringProperty pBmcUseActiveDiscovery;
 static StringProperty pCurrentlyConnectedPTIs;
 static StringProperty pNumOfConnectedPTIs;
 static StringProperty pTotalMessageCount;
 static StringProperty pTotalSentMessageCount = new StringProperty("totalSentMessageCount");
 static StringProperty pTotalAcceptedMessageCount = new StringProperty("totalAcceptedMessageCount");
 static StringProperty pTotalRejectedMessageCount = new StringProperty("totalRejectedMessageCount");
 static StringProperty pBmcDomainSubnet;
 static StringProperty pBmcDomainMask;
 static StringProperty pOptionalUserNameForPconfig;
 static StringProperty pPasswordForPconfig;
 static NumericProperty pBmcAgentPort;
 static StringProperty pPTIServerPort;
 static EmsConfigFileProperty pEmsConfigFilePath;
 static StringProperty pDiscoveredAgentCSVlist;
 static StringProperty pLastUpdateTimeStamp;
 static StringProperty pDisableUDP;
 static StringProperty pUsePatrolApiInsteadOfPconfig;
// private BMCProbeComponent bmcProbeComponent;
 private static final String DEFAULT_PTI_PORT = "1789";
 private static final String DEFAULT_BMC_USERNAME = "patrol";
 private static final String DEFAULT_BMC_AGENT_PORT = "3181";
 private final int MAX_TCP_PORT_VALUE = 0x1ffff;
 public static int monitorCounter = 0;
 private Integer monitorId;
 private static final String MONITOR_NAME = "BMCPatrol";
 static StringProperty pEmsIntegrationState;
 private static final String BMC_PATROL_NAME = "BMC PATROL (Classic)";
 protected static EmsTimeDiffProperty m_EmsTimeDiff;
 private static final String NOT_AVAILABLE = "n/a";

 public BMCPatrolProbeMonitor()
 {
//     bmcProbeComponent = null;
     monitorId = null;
 }

 protected void startMonitor()
 {
     setMonitorId();
//     BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("startMonitor called ");
//     if(getPropertyAsBoolean(Monitor.pDisabled))
//     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).warning("Monitor Disabled, bailing out");
//     } else
     {
         initializeEmsComponent();
         initializeBackendWithProperties();
     }
     super.startMonitor();
 }

 private synchronized void setMonitorId()
 {
     if(monitorId == null)
     {
         monitorId = new Integer(++monitorCounter);
     }
 }

 private void initializeBackendWithProperties()
 {
//     try
//     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Creating new instance of BMC Backend Parameters");
//         BackendParameters lBackendParams = (new BackendParameters()).setDomainSubnet(getPatrolDomainSubnet()).setDomainMask(getPatrolDomainMask()).setPatrolPassword(getPatrolPassword()).setPatrolUsername(getPatrolUsername()).setActiveDiscovery(getActiveDiscovery()).setPconfigConfigurationMethod(getUsingPconfigForConfiguration()).setUdpDisabled(getUdpDisabled()).setPatrolPort(getPatrolPort());
//         lBackendParams.setPconfigConfigurationMethod(false);
//         lBackendParams.setMonitorId(monitorId);
//         Vector lConnectedAgents = getStatisticsCollector().getCurrentlyConnectedPTIs();
//         for(int i = 0; i < lConnectedAgents.size(); i++)
//         {
//             InetAddress lAgentAddress = (InetAddress)lConnectedAgents.elementAt(i);
//             BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Passing Agent " + lAgentAddress + " to the backend ");
//             lBackendParams.addPatrolAgentAddress(lAgentAddress);
//         }
//
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Successfully created new instance of BMC Backend Parameters");
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Parameters:\nActiveDiscovery     :" + lBackendParams.isActiveDiscoveryEnabled() + "\nDomainSubnet        :" + lBackendParams.getDomainSubnet() + "\nDomainMask          :" + lBackendParams.getDomainMask() + "\nPatrol Username     :" + lBackendParams.getPatrolUsername());
//         if(lBackendParams.isValid())
//         {
//             BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Parameters are valid, storing these in the Backend Settings");
//             BackendSettings.getInstance().storeParameters(lBackendParams);
//         } else
//         {
//             BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).error("Parameters are NOT valid, not storing these in the Backend Settings");
//         }
//     }
//     catch(IllegalArgumentException e)
//     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).warning("Error setting backend parameters " + e.getMessage());
//     }
 }

 private boolean getUdpDisabled()
 {
     return getPropertyAsBoolean(pDisableUDP);
 }

 private boolean getUsingPconfigForConfiguration()
 {
     return !getPropertyAsBoolean(pUsePatrolApiInsteadOfPconfig);
 }

 private void initializeEmsComponent()
 {
//     BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).flow("Setting up EMS/BMC integration component");
//     if(bmcProbeComponent == null)
//     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("No BMC component exists; creating new BMC Probe component");
//         bmcProbeComponent = new BMCProbeComponent();
//     } else
//     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Using existing BMC Probe component, not creating new one");
//     }
//     String lPath = getEmsConfigFilePath();
//     BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Setting EMS Configuration path to " + lPath);
//     bmcProbeComponent.setEmsConfigurationFilePath(lPath);
//     bmcProbeComponent.setEmsTimeDiff(getEmsTimeDiff());
//     int lPtiPort = getPtiPort();
//     BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("Starting up the BMC Probe component on port " + lPtiPort);
//     bmcProbeComponent.startUp((short)lPtiPort);
 }

 private int getPropertyAsPort(StringProperty prop, int dflt)
 {
     if(dflt < 0 || dflt >= 0x1ffff)
     {
         throw new IllegalArgumentException("Illegal value: " + dflt + " passed as default value for TCP port number. " + "Legal values: 0 <= value < " + 0x1ffff);
     }
     String rawValue = getProperty(prop);
     int checkedVal;
     try
     {
         checkedVal = Integer.parseInt(rawValue);
     }
     catch(NumberFormatException e)
     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).warning("BMC Patrol Monitor " + prop.getName() + " unparseable value : " + rawValue + " setting to default: " + dflt);
         checkedVal = dflt;
     }
     if(checkedVal < 0 || checkedVal > 0x1ffff)
     {
//         BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).warning("BMC Patrol Monitor " + prop.getName() + " illegal value: " + checkedVal + " setting to default: " + dflt);
         checkedVal = dflt;
     }
     return checkedVal;
 }

 private boolean getActiveDiscovery()
 {
     return getPropertyAsBoolean(pBmcUseActiveDiscovery);
 }

 private int getPtiPort()
 {
     return getPropertyAsPort(pPTIServerPort, 1789);
 }

 protected String getEmsConfigFilePath()
 {
     return pEmsConfigFilePath.getFileName(this);
 }

 private int getPatrolPort()
 {
     return getPropertyAsPort(pBmcAgentPort, 3181);
 }

 private String getPatrolDomainSubnet()
 {
     return getProperty(pBmcDomainSubnet);
 }

 private String getPatrolDomainMask()
 {
     return getProperty(pBmcDomainMask);
 }

 private String getPatrolUsername()
 {
     return getProperty(pOptionalUserNameForPconfig);
 }

 private String getPatrolPassword()
 {
     return getProperty(pPasswordForPconfig);
 }

 private long getEmsTimeDiff()
 {
     return getPropertyAsLong(m_EmsTimeDiff);
 }

 public void stopMonitor()
 {
//     if(bmcProbeComponent != null)
//     {
//         bmcProbeComponent.terminate();
//         bmcProbeComponent = null;
//     }
//     BackendSettings.getInstance().removeParameter(monitorId);
     super.stopMonitor();
 }

 public String getHostname()
 {
     return "BMCPatrolProbe";
 }

 protected boolean update()
 {
     String error = "";
     String totalEntries = "n/a";
     String succesfullySentEntries = "n/a";
     String failureSentEntries = "n/a";
     String totalSentEntries = "n/a";
     String ptiCount = "n/a";
     try
     {
         if(!(new File(getEmsConfigFilePath())).exists())
         {
             error = "File " + getEmsConfigFilePath() + " does not exists ";
         }
//         totalEntries = String.valueOf(getStatisticsCollector().getReceivedEntries());
//         succesfullySentEntries = String.valueOf(getStatisticsCollector().getSentAndAcceptedEntries());
//         failureSentEntries = String.valueOf(getStatisticsCollector().getSentAndRejectedEntries());
//         totalSentEntries = String.valueOf(getStatisticsCollector().getSentAndAcceptedEntries() + getStatisticsCollector().getSentAndRejectedEntries());
//         ptiCount = String.valueOf(getStatisticsCollector().getPTICount());
//         getStatisticsCollector().reset();
     }
     catch(Throwable t)
     {
         error = new String(t.getMessage());
         totalEntries = "n/a";
         succesfullySentEntries = "n/a";
         failureSentEntries = "n/a";
         totalSentEntries = "n/a";
         ptiCount = "n/a";
     }
     synchronized(this)
     {
         if(error.length() == 0)
         {
             setProperty(Monitor.pStateString, "" + totalEntries + " received, connected Agents: " + ptiCount);
             setProperty(pEmsIntegrationState, "1");
         } else
         {
             setProperty(pEmsIntegrationState, "0");
             setProperty(Monitor.pStateString, error);
         }
         setProperty(pTotalMessageCount, String.valueOf(totalEntries));
         setProperty(pTotalSentMessageCount, String.valueOf(totalSentEntries));
         setProperty(pTotalAcceptedMessageCount, String.valueOf(succesfullySentEntries));
         setProperty(pTotalRejectedMessageCount, String.valueOf(failureSentEntries));
         setProperty(pCurrentlyConnectedPTIs, ptiCount);
         setProperty(pNumOfConnectedPTIs, ptiCount);
         initializeBackendWithProperties();
     }
     return true;
 }

 public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors)
 {
     if(property == m_EmsTimeDiff)
     {
         return m_EmsTimeDiff == null ? "0" : m_EmsTimeDiff.verify(property, value, request, errors);
     }
     if(property != null && property.getName().equals("_emsConfigFilePath"))
     {
         return ((EmsConfigFileProperty)property).verifyConfigFile(this, value, errors);
     } else
     {
         return super.verify(property, value, request, errors);
     }
 }

// private StatisticsCollector getStatisticsCollector()
// {
//     return PtiServiceRepository.getServiceForPort((short)getPtiPort()).getStats();
// }

 public Array getLogProperties()
 {
     Array a = super.getLogProperties();
     a.add(pTotalMessageCount);
     a.add(pNumOfConnectedPTIs);
     return a;
 }

 int forwardAlerts(String message, String ruleFilter, StringBuffer details, long detailsMax)
 {
     int totalSent = 0;
     return totalSent;
 }

 static 
 {
//     BMCPatrolLoggerRepository.getLogger(LoggerCategory.MONITOR).debug("BMC Patrol Static Initialization Routine");
     boolean isEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
     pEmsConfigFilePath = new EmsConfigFileProperty("BMCPatrol");
     pEmsConfigFilePath.setParameterOptions(true, 3, true);
     pEmsIntegrationState = new NumericProperty("pEmsIntegrationState");
     pEmsIntegrationState.setLabel("EmsIntegrationState");
     pEmsIntegrationState.setDisplayText("EMS integration state", "state of the EMS integration state");
     pPTIServerPort = new StringProperty("_ptiPort", "1789");
     pPTIServerPort.setLabel("PTIPort");
     pPTIServerPort.setDisplayText("PATROL Topaz Integration TCP Port", "TCP port number for incoming connections. The PTI (PATROL Topaz Integration) BMC PATROL (Classic) application must be configured to report alerts to this port.");
     pPTIServerPort.setParameterOptions(true, 4, true);
     pUsePatrolApiInsteadOfPconfig = new BooleanProperty("_pBmcPatrolApi");
     pUsePatrolApiInsteadOfPconfig.setLabel("Use PATROL API for configuration retrival ");
     pUsePatrolApiInsteadOfPconfig.setDisplayText("PATROL API <b>(not recommended for general use, requires username and password)</b>", "");
     pUsePatrolApiInsteadOfPconfig.setParameterOptions(true, 5, true);
     pDisableUDP = new BooleanProperty("_pBmcDisableUDP");
     pDisableUDP.setLabel("Disable UDP PATROL Ping");
     pDisableUDP.setDisplayText("Do not use the UDP PATROL Ping", "By default, UDP based PATROL Ping is used to lower latency of the SiteView/PATROL communication. Use this option if your environment barriers UDP connectivity");
     pDisableUDP.setParameterOptions(true, 6, true);
     pBmcAgentPort = new NumericProperty("_pBmcAgentPort", "3181");
     pBmcAgentPort.setLabel("PATROL Port");
     pBmcAgentPort.setDisplayText("PATROL Agent Configuration Port", "UDP Port used to access the BMC PATROL (Classic) Configuration");
     pBmcAgentPort.setParameterOptions(true, 15, true);
     pOptionalUserNameForPconfig = new StringProperty("_pBmcUserName", "patrol");
     pOptionalUserNameForPconfig.setLabel("PConfig User name");
     pOptionalUserNameForPconfig.setDisplayText("PATROL Configuration User Name", "Optional username for read access the PATROL Agent Configuration. By default, this is not needed. However, if your PATROL security policy prohibits anonymous read access to PATROL Agent Configuration, you will have to provide appropriate username here.");
     pOptionalUserNameForPconfig.setParameterOptions(true, 18, true);
     pPasswordForPconfig = new StringProperty("_pBmcPassword");
     pPasswordForPconfig.setLabel("PATROL User Password");
     pPasswordForPconfig.setDisplayText("PATROL Configuration password", "If the abovementioned username requires a password, provide it here.");
     pPasswordForPconfig.setParameterOptions(true, 19, true);
     pPasswordForPconfig.isPassword = true;
     pTotalMessageCount = new NumericProperty("totalMessageCount");
     pTotalMessageCount.setLabel("Received Patrol Alerts");
     pTotalMessageCount.setIsThreshold(true);
     pTotalMessageCount.setStateOptions(1);
     pCurrentlyConnectedPTIs = new StringProperty("pCurrentlyConnectedPTIs");
     pCurrentlyConnectedPTIs.setLabel("");
     pCurrentlyConnectedPTIs.setDisplayText("", "");
     pNumOfConnectedPTIs = new NumericProperty("numOfConnectedPTIs");
     pNumOfConnectedPTIs.setLabel("Number of connected Agents");
     pNumOfConnectedPTIs.setIsThreshold(true);
     pNumOfConnectedPTIs.setStateOptions(2);
     m_EmsTimeDiff = new EmsTimeDiffProperty("", "0");
     m_EmsTimeDiff.setParameterOptions(true, 100, true);
     StringProperty myProperties[] = {
         pPTIServerPort, pEmsConfigFilePath, pEmsIntegrationState, pTotalMessageCount, pNumOfConnectedPTIs, pDisableUDP, pOptionalUserNameForPconfig, pPasswordForPconfig, pBmcAgentPort, m_EmsTimeDiff
     };
     PropertiedObject.addProperties("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", myProperties);
     SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", Rule.stringToClassifier("pEmsIntegrationState == 0\terror"));
     SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", Rule.stringToClassifier("numOfConnectedPTIs == n/a\terror"));
     SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", Rule.stringToClassifier("totalMessageCount == n/a\terror"));
     SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", Rule.stringToClassifier("numOfConnectedPTIs == 0\twarning", true));
     SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", Rule.stringToClassifier("always\tgood"));
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "description", "Delivers alerts from BMC PATROL (Classic) Agent(s) to Topaz");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "help", "BMCPatrolMon.htm");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "title", "BMC PATROL (Classic) Alert");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "class", "BMCPatrolProbeMonitor");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "topazName", "BMC Patrol Alerts");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "topazType", "System Resources");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "loadable", isEmsLicensed ? "true" : "false");
     PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor", "toolPageDisable", "true");
 }
}
