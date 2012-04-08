/*
 * 
 * Created on 2005-3-7 1:08:02
 *
 * EMSSNMPTrapMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>EMSSNMPTrapMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import java.io.File;

public class EMSSNMPTrapMonitor extends EMSSNMPTrapBase
{

 private static StringProperty pMatch;
 private static StringProperty pAlerting;
 private static EmsConfigFileProperty pEmsConfigFilePath;
 private static StringProperty pValueLabels;
 private static StringProperty pLastAlertsPerMinute;
 private static StringProperty pLastLinesPerMinute;
 private static StringProperty pMatches;
 private static StringProperty pLines;
 private static StringProperty pLastFilePosition;
 private static StringProperty pLastModDate;
 private static StringProperty pLastMeasurement;
 private static StringProperty pMessage;
 private static StringProperty pMatchDetails;
 private static StringProperty pValue;
 private static StringProperty pValue2;
 private static StringProperty pValue3;
 private static StringProperty pValue4;
 private static final String MONITOR_NAME = "SNMPTrap";
 private static final String absolutePathToConfig;

 public EMSSNMPTrapMonitor()
 {
 }

 public String getHostname()
 {
     return "";
 }

 protected StringProperty getAlertingProperty()
 {
     return pAlerting;
 }

 protected StringProperty getValueLabelsProperty()
 {
     return pValueLabels;
 }

 protected StringProperty getMessageProperty()
 {
     return pMessage;
 }

 protected StringProperty getLinesProperty()
 {
     return pLines;
 }

 protected StringProperty getMatchesProperty()
 {
     return pMatches;
 }

 protected StringProperty getLastMeasurementProperty()
 {
     return pLastMeasurement;
 }

 protected StringProperty getLastLinesPerMinuteProperty()
 {
     return pLastLinesPerMinute;
 }

 protected StringProperty getLastAlertsPerMinuteProperty()
 {
     return pLastAlertsPerMinute;
 }

 protected StringProperty getMatchDetailsProperty()
 {
     return pMatchDetails;
 }

 protected StringProperty getLastFilePositionProperty()
 {
     return pLastFilePosition;
 }

 protected StringProperty getValue4Property()
 {
     return pValue4;
 }

 protected StringProperty getValue3Property()
 {
     return pValue3;
 }

 protected StringProperty getValue2Property()
 {
     return pValue2;
 }

 protected StringProperty getValueProperty()
 {
     return pValue;
 }

 protected StringProperty getMatchProperty()
 {
     return pMatch;
 }

 protected String getEmsConfigFilePath()
 {
     return pEmsConfigFilePath.getFileName(this);
 }

 protected String getConfigurationPath()
 {
     return absolutePathToConfig;
 }

 protected StringProperty getEmsConfigFileProprty()
 {
     return pEmsConfigFilePath;
 }

 public String getTestURL()
 {
     String result = "/SiteView/cgi/go.exe/SiteView?page=snmpTrapLog";
     return result;
 }

 static 
 {
     absolutePathToConfig = Platform.getRoot() + File.separator + "ems" + File.separator + "SNMPTrap";
     pMatch = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDesc));
     pMatch.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pMatchDesc), SNMPTrapBase.getBasePropertyDesc(SNMPTrapBase.pMatchDesc));
     pMatch.setParameterOptions(false, 1, false);
     pAlerting = new ScalarProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pAlertingDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pAlertingDesc));
     pAlerting.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pAlertingDesc), SNMPTrapBase.getBasePropertyDesc(SNMPTrapBase.pAlertingDesc));
     pAlerting.setParameterOptions(true, 2, false);
     pValueLabels = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValueLabelsDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pValueLabelsDesc));
     pValueLabels.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pValueLabelsDesc), SNMPTrapBase.getBasePropertyDesc(SNMPTrapBase.pValueLabelsDesc));
     pValueLabels.setParameterOptions(true, 4, true);
     pLines = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLinesDesc));
     pLines.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLinesDesc));
     pLines.setIsThreshold(true);
     pMatches = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchesDesc));
     pMatches.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pMatchesDesc));
     pMatches.setIsThreshold(true);
     pMatchDetails = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDetailsDesc));
     pLastAlertsPerMinute = new RateProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastAlertsPerMinuteDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastAlertsPerMinuteDesc), "lines", "minutes");
     pLastAlertsPerMinute.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLastAlertsPerMinuteDesc));
     pLastAlertsPerMinute.setStateOptions(1);
     pLastLinesPerMinute = new RateProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastLinesPerMinuteDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastLinesPerMinuteDesc), "lines", "minutes");
     pLastLinesPerMinute.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLastAlertsPerMinuteDesc));
     pLastLinesPerMinute.setStateOptions(2);
     pLastModDate = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastModDateDesc));
     pLastFilePosition = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastFilePositionDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastFilePositionDesc));
     pLastMeasurement = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastMeasurementDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastMeasurementDesc));
     pMessage = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMessageDesc));
     pValue = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValueDesc));
     pValue.setStateOptions(3);
     pValue2 = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValue2Desc));
     pValue2.setStateOptions(4);
     pValue3 = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValue3Desc));
     pValue3.setStateOptions(5);
     pValue4 = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValue4Desc));
     pValue4.setStateOptions(6);
     boolean isEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
     pEmsConfigFilePath = new EmsConfigFileProperty("SNMPTrap");
     pEmsConfigFilePath.setParameterOptions(true, 3, true);
     StringProperty myProperties[] = {
         pAlerting, pMatch, pMatches, pLines, pMatchDetails, pLastAlertsPerMinute, pLastLinesPerMinute, pLastFilePosition, pLastModDate, pLastMeasurement, 
         pMessage, pValue, pValue2, pValue3, pValue4, pValueLabels, pEmsConfigFilePath
     };
     String fullClassName = (COM.dragonflow.StandardMonitor.EMSSNMPTrapMonitor.class).getName();
     PropertiedObject.addProperties(fullClassName, myProperties);
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount > 0\terror", true));
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("always\tgood"));
     PropertiedObject.setClassProperty(fullClassName, "description", "Monitors SNMP Traps sent to the SiteView server from other devices");
     PropertiedObject.setClassProperty(fullClassName, "help", "emsSNMPTrapMon.htm");
     PropertiedObject.setClassProperty(fullClassName, "title", "EMS SNMP Trap");
     PropertiedObject.setClassProperty(fullClassName, "class", "EMSSNMPTrapMonitor");
     PropertiedObject.setClassProperty(fullClassName, "target", SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDesc));
     PropertiedObject.setClassProperty(fullClassName, "toolName", "SNMP Trap");
     PropertiedObject.setClassProperty(fullClassName, "toolDescription", "Display the log of SNMP Traps received.");
     PropertiedObject.setClassProperty(fullClassName, "topazName", "SNMP Trap");
     PropertiedObject.setClassProperty(fullClassName, "topazType", "System Resources");
     PropertiedObject.setClassProperty(fullClassName, "loadable", isEmsLicensed ? "true" : "false");
 }
}
