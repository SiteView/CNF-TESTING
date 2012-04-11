/*
 * 
 * Created on 2005-3-5 14:20:22
 *
 * AvalonMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>AvalonMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import java.io.File;
import java.util.Vector;
import jgl.HashMap;

public class AvalonMonitor extends EMSSNMPTrapBase
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
 private static final String EMS_FOLDER_NAME = "ems";
 private static final String EMS_INTEGRATION_FOLDER_NAME = "Avalon";
 private static final String absolutePathToConfig;
 private final String uniqueID = "1.3.6.1.4.1.12251";
 private static String itemName;
 private static String specificAlertDescription;

 public AvalonMonitor()
 {
 }

 public String getHostname()
 {
     return "AvalonTrapHost";
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

 protected String getBrand()
 {
     return itemName + " Monitor";
 }

 public void initialize(HashMap hashMap)
 {
     setProperty(pMatch, "1.3.6.1.4.1.12251");
     super.initialize(hashMap);
 }

 public Vector getScalarValues(ScalarProperty property, HTTPRequest request, CGI cgi)
 {
     if(property == pAlerting)
     {
         Vector v = new Vector();
         v.addElement("each");
         v.addElement("for each " + itemName + " entry matched");
         v.addElement("once");
         v.addElement("once, after all " + itemName + "s have been checked");
         return v;
     }
     Vector vValues = null;
     try
     {
         vValues = super.getScalarValues(property, request, cgi);
     }
     catch(SiteViewException e) { }
     return vValues;
 }

 protected boolean isFileExist(String value)
 {
     return true;
 }

 static 
 {
     absolutePathToConfig = Platform.getRoot() + File.separator + "ems" + File.separator + "Avalon";
     itemName = "Avalon Alert";
     specificAlertDescription = "How alerts for this monitor are triggered.  For <b>'once, after all " + itemName + "s have been checked'</b>, " + " the traps received since the last monitor run are checked and alerts are triggered  based on the " + " the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. " + " For the option <b>'for each " + itemName + " entry matched'</b>" + " the monitor triggers alerts for every matching " + itemName + " entry found" + " and the monitor will remain 'good'. ";
     pMatch = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDesc));
     pMatch.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pMatchDesc), SNMPTrapBase.getBasePropertyDesc(SNMPTrapBase.pMatchDesc));
     pMatch.setParameterOptions(false, 1, false);
     pAlerting = new ScalarProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pAlertingDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pAlertingDesc));
     pAlerting.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pAlertingDesc), specificAlertDescription);
     pAlerting.setParameterOptions(true, 2, false);
     pValueLabels = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pValueLabelsDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pValueLabelsDesc));
     pValueLabels.setDisplayText(SNMPTrapBase.getBasePropertyTitle(SNMPTrapBase.pValueLabelsDesc), SNMPTrapBase.getBasePropertyDesc(SNMPTrapBase.pValueLabelsDesc));
     pValueLabels.setParameterOptions(false, 4, true);
     pLines = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLinesDesc));
     pLines.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLinesDesc));
     pLines.setIsThreshold(true);
     pMatches = new NumericProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchesDesc));
     pMatches.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pMatchesDesc));
     pMatches.setIsThreshold(true);
     pMatchDetails = new StringProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDetailsDesc));
     pLastAlertsPerMinute = new RateProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastAlertsPerMinuteDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastAlertsPerMinuteDesc), "lines", "minutes");
     pLastAlertsPerMinute.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLastAlertsPerMinuteDesc));
     pLastAlertsPerMinute.setStateOptions(2);
     pLastLinesPerMinute = new RateProperty(SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pLastLinesPerMinuteDesc), SNMPTrapBase.getBasePropertyDefValue(SNMPTrapBase.pLastLinesPerMinuteDesc), "lines", "minutes");
     pLastLinesPerMinute.setLabel(SNMPTrapBase.getBasePropertyLabel(SNMPTrapBase.pLastAlertsPerMinuteDesc));
     pLastLinesPerMinute.setStateOptions(3);
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
     pEmsConfigFilePath = new EmsConfigFileProperty("Avalon");
     pEmsConfigFilePath.setParameterOptions(true, 3, true);
     StringProperty myProperties[] = {
         pAlerting, pMatch, pMatches, pLines, pMatchDetails, pLastAlertsPerMinute, pLastLinesPerMinute, pLastFilePosition, pLastModDate, pLastMeasurement, 
         pMessage, pValue, pValue2, pValue3, pValue4, pValueLabels, pEmsConfigFilePath
     };
     String fullClassName = (COM.dragonflow.StandardMonitor.AvalonMonitor.class).getName();
     PropertiedObject.addProperties(fullClassName, myProperties);
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount > 0\terror", true));
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
     SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("always\tgood"));
     PropertiedObject.setClassProperty(fullClassName, "description", "Monitors " + itemName + "s sent to the SiteView server from other devices");
     PropertiedObject.setClassProperty(fullClassName, "help", "AvalonAlertMon.htm");
     PropertiedObject.setClassProperty(fullClassName, "classType", "advanced");
     PropertiedObject.setClassProperty(fullClassName, "title", itemName);
     PropertiedObject.setClassProperty(fullClassName, "class", "AvalonMonitor");
     PropertiedObject.setClassProperty(fullClassName, "target", SNMPTrapBase.getBasePropertyName(SNMPTrapBase.pMatchDesc));
     PropertiedObject.setClassProperty(fullClassName, "topazName", "" + itemName + "");
     PropertiedObject.setClassProperty(fullClassName, "topazType", "System Resources");
     PropertiedObject.setClassProperty(fullClassName, "loadable", isEmsLicensed ? "true" : "false");
 }
}