/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SNMPTrapMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SNMPTrapMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SNMPTrapBase;

public class SNMPTrapMonitor extends SNMPTrapBase
{

    private static StringProperty pMatch;
    private static StringProperty pAlerting;
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

    public SNMPTrapMonitor()
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

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=snmpTrapLog";
        return s;
    }

    static 
    {
        pMatch = new StringProperty(getBasePropertyName(pMatchDesc));
        pMatch.setDisplayText(getBasePropertyTitle(pMatchDesc), getBasePropertyDesc(pMatchDesc));
        pMatch.setParameterOptions(true, 1, false);
        pAlerting = new ScalarProperty(getBasePropertyName(pAlertingDesc), getBasePropertyDefValue(pAlertingDesc));
        pAlerting.setDisplayText(getBasePropertyTitle(pAlertingDesc), getBasePropertyDesc(pAlertingDesc));
        pAlerting.setParameterOptions(true, 2, false);
        pValueLabels = new StringProperty(getBasePropertyName(pValueLabelsDesc), getBasePropertyDefValue(pValueLabelsDesc));
        pValueLabels.setDisplayText(getBasePropertyTitle(pValueLabelsDesc), getBasePropertyDesc(pValueLabelsDesc));
        pValueLabels.setParameterOptions(true, 4, true);
        pLines = new NumericProperty(getBasePropertyName(pLinesDesc));
        pLines.setLabel(getBasePropertyLabel(pLinesDesc));
        pLines.setIsThreshold(true);
        pMatches = new NumericProperty(getBasePropertyName(pMatchesDesc));
        pMatches.setLabel(getBasePropertyLabel(pMatchesDesc));
        pMatches.setIsThreshold(true);
        pMatchDetails = new StringProperty(getBasePropertyName(pMatchDetailsDesc));
        pLastAlertsPerMinute = new RateProperty(getBasePropertyName(pLastAlertsPerMinuteDesc), getBasePropertyDefValue(pLastAlertsPerMinuteDesc), "lines", "minutes");
        pLastAlertsPerMinute.setLabel(getBasePropertyLabel(pLastAlertsPerMinuteDesc));
        pLastAlertsPerMinute.setStateOptions(1);
        pLastLinesPerMinute = new RateProperty(getBasePropertyName(pLastLinesPerMinuteDesc), getBasePropertyDefValue(pLastLinesPerMinuteDesc), "lines", "minutes");
        pLastLinesPerMinute.setLabel(getBasePropertyLabel(pLastAlertsPerMinuteDesc));
        pLastLinesPerMinute.setStateOptions(2);
        pLastModDate = new NumericProperty(getBasePropertyName(pLastModDateDesc));
        pLastFilePosition = new NumericProperty(getBasePropertyName(pLastFilePositionDesc), getBasePropertyDefValue(pLastFilePositionDesc));
        pLastMeasurement = new NumericProperty(getBasePropertyName(pLastMeasurementDesc), getBasePropertyDefValue(pLastMeasurementDesc));
        pMessage = new StringProperty(getBasePropertyName(pMessageDesc));
        pValue = new NumericProperty(getBasePropertyName(pValueDesc));
        pValue.setStateOptions(3);
        pValue2 = new NumericProperty(getBasePropertyName(pValue2Desc));
        pValue2.setStateOptions(4);
        pValue3 = new NumericProperty(getBasePropertyName(pValue3Desc));
        pValue3.setStateOptions(5);
        pValue4 = new NumericProperty(getBasePropertyName(pValue4Desc));
        pValue4.setStateOptions(6);
        StringProperty astringproperty[] = {
            pAlerting, pMatch, pMatches, pLines, pMatchDetails, pLastAlertsPerMinute, pLastLinesPerMinute, pLastFilePosition, pLastModDate, pLastMeasurement, 
            pMessage, pValue, pValue2, pValue3, pValue4, pValueLabels
        };
        String s = (COM.dragonflow.StandardMonitor.SNMPTrapMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("matchCount > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors SNMP Traps sent to the SiteView server from other devices");
        setClassProperty(s, "help", "SNMPTrapMon.htm");
        setClassProperty(s, "title", "SNMP Trap");
        setClassProperty(s, "class", "SNMPTrapMonitor");
        setClassProperty(s, "target", getBasePropertyName(pMatchDesc));
        setClassProperty(s, "toolName", "SNMP Trap");
        setClassProperty(s, "toolDescription", "Display the log of SNMP Traps received.");
        setClassProperty(s, "topazName", "SNMP Trap");
        setClassProperty(s, "topazType", "System Resources");
        setClassProperty(s, "loadable", "true");
    }
}
