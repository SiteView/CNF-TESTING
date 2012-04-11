/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * LogMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>LogMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.LogMonitorBase;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import java.util.Vector;
import jgl.HashMap;

public class LogMonitor extends LogMonitorBase
{

    private static StringProperty pLogFile;
    private static ScalarProperty pResetFile;
    private static StringProperty pLogName;
    private static StringProperty pRulesFile;
    private static StringProperty pNoFileCheckExist;
    private static StringProperty pMatch;
    private static StringProperty pAlerting;
    private static StringProperty pLastAlertsPerMinute;
    private static StringProperty pLastLinesPerMinute;
    private static StringProperty pMatches;
    private static StringProperty pLines;
    private static StringProperty pLastFilePosition;
    private static StringProperty pStartSearchPosition;
    private static StringProperty pLastModDate;
    private static StringProperty pLastMeasurement;
    private static StringProperty pMessage;
    private static StringProperty pMatchDetails;
    private static StringProperty pValue;
    private static StringProperty pValue2;
    private static StringProperty pValue3;
    private static StringProperty pValue4;
    private static StringProperty pValueLabels;

    public LogMonitor()
    {
    }

    protected StringProperty getMeasurementProperty()
    {
        return pMeasurement;
    }

    protected StringProperty getLogFileProperty()
    {
        return pLogFile;
    }

    protected StringProperty getLogNameProperty()
    {
        return pLogName;
    }

    protected StringProperty getRulesFileProperty()
    {
        return pRulesFile;
    }

    protected StringProperty getNoFileCheckExistProperty()
    {
        return pNoFileCheckExist;
    }

    protected StringProperty getMatchProperty()
    {
        return pMatch;
    }

    protected StringProperty getAlertingProperty()
    {
        return pAlerting;
    }

    protected StringProperty getLastAlertsPerMinuteProperty()
    {
        return pLastAlertsPerMinute;
    }

    protected StringProperty getLastLinesPerMinuteProperty()
    {
        return pLastLinesPerMinute;
    }

    protected StringProperty getMatchesProperty()
    {
        return pMatches;
    }

    protected StringProperty getLinesProperty()
    {
        return pLines;
    }

    protected StringProperty getLastFilePositionProperty()
    {
        return pLastFilePosition;
    }

    protected StringProperty getStartSearchPositionProperty()
    {
        return pStartSearchPosition;
    }

    protected StringProperty getLastModDateProperty()
    {
        return pLastModDate;
    }

    protected StringProperty getLastMeasurementProperty()
    {
        return pLastMeasurement;
    }

    protected StringProperty getMessageProperty()
    {
        return pMessage;
    }

    protected StringProperty getMatchDetailsProperty()
    {
        return pMatchDetails;
    }

    protected StringProperty getValueProperty()
    {
        return pValue;
    }

    protected StringProperty getValue2Property()
    {
        return pValue2;
    }

    protected StringProperty getValue3Property()
    {
        return pValue3;
    }

    protected StringProperty getValue4Property()
    {
        return pValue4;
    }

    protected StringProperty getValueLabelsProperty()
    {
        return pValueLabels;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pResetFile)
        {
            Vector vector = new Vector();
            vector.addElement("none");
            vector.addElement("Never");
            vector.addElement("once");
            vector.addElement("First Time Only");
            vector.addElement("always");
            vector.addElement("Always");
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public long getStartPosition(long l, String s)
    {
        String s1 = getValue("_resetFile");
        if(s1 == null || s1.length() == 0 || s1.equals("none"))
        {
            return super.getStartPosition(l, s);
        }
        if(getValue("_resetFile").equals("once"))
        {
            HashMap hashmap = new HashMap();
            hashmap.put(pResetFile.getName(), "none");
            setProperty(pResetFile, "none");
            saveMonitor(hashmap);
        }
        return 0L;
    }

    static 
    {
        pLogFile = new StringProperty("_logFile");
        pLogFile.setDisplayText("Log File Pathname", "the pathname of the log file to monitor\n<p> In order to monitor remote Unix files choose the 'Choose Server' link above.  For NT, you must specify the UNC path to the file.  For example, \\\\machinename\\sharename\\filename.log.\n<p>Optionally, use a <a href=/SiteView/docs/regexp.htm>regular expression</a> to insert date and time variables <br>(e.g s/ex$shortYear$$0month$$0day$.log/ to match IIS log files)");
        pLogFile.setParameterOptions(true, 1, false);
        pResetFile = new ScalarProperty("_resetFile");
        pResetFile.setDisplayText("Check from Beginning", "Select file checking option as follows:\n <br><b>Never</b> - Check only newly added records (default)\n<br><b>First Time Only</b> - Check the whole file once, then only new records\n<br><b>Always</b> - Always check the whole file\n");
        pResetFile.setValue("none");
        pResetFile.setParameterOptions(true, 2, false);
        pAlerting = new ScalarProperty("_alerting", "");
        pAlerting.setDisplayText("Run Alerts", "How alerts for this monitor are triggered by the following options:  <TR><TD>(1) For <b>'for each log entry matched and report status'</b> the monitor triggers alerts for every matching entry found based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n<TR><TD>(2) For <b>'once, after all log entries have been checked'</b>, the monitor counts up the number of matches and then triggers alerts based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n");
        pAlerting.setParameterOptions(true, 3, false);
        pMatch = new StringProperty("_match");
        pMatch.setDisplayText("Content Match", "enter the text to match in a log entry or a <a href=/SiteView/docs/regexp.htm>regular expression</a>. By default successful match makes monitor alert or error.");
        pMatch.setParameterOptions(true, 4, false);
        pRulesFile = new StringProperty("_rulesFile");
        pRulesFile.setParameterOptions(true, 4, true);
        pRulesFile.setDisplayText("Rules File Pathname", ruleFileDisplayText);
        pNoFileCheckExist = new BooleanProperty("_noFileCheckExist");
        pNoFileCheckExist.setDisplayText("No Error on File Not Found.", "if the file is not found then don't error.");
        pNoFileCheckExist.setParameterOptions(true, 5, true);
        pValueLabels = new StringProperty("_valeLabels", "");
        pValueLabels.setDisplayText("Match Value Labels", "Labels for the values matched on the script output, separated by a \",\"");
        pValueLabels.setParameterOptions(true, 6, true);
        pLines = new NumericProperty("lineCount");
        pLines.setLabel("lines");
        pLines.setIsThreshold(true);
        pMatches = new NumericProperty("matchCount");
        pMatches.setLabel("matches");
        pMatches.setIsThreshold(true);
        pLogName = new StringProperty("logName");
        pLogName.setDisplayText("Don't use in Templates", "this is the path to the log file. However, it <b>should not be used</b>, because it is not loaded all of the time. It depends on the state of the monitor");
        pMatchDetails = new StringProperty("matchDetails");
        pLastAlertsPerMinute = new RateProperty("lastAlertsPerMinute", "0", "lines", "minutes");
        pLastAlertsPerMinute.setLabel("matches/min");
        pLastAlertsPerMinute.setStateOptions(1);
        pLastLinesPerMinute = new RateProperty("lastLinesPerMinute", "0", "lines", "minutes");
        pLastLinesPerMinute.setLabel("lines/min");
        pLastLinesPerMinute.setStateOptions(2);
        pLastModDate = new NumericProperty("lastModDate");
        pLastFilePosition = new NumericProperty("lastFilePosition", "-1");
        pStartSearchPosition = new NumericProperty("startSearchPosition", "-1");
        pLastMeasurement = new NumericProperty("lastMeasurement", "0");
        pMessage = new StringProperty("message");
        pValue = new NumericProperty("value");
        pValue.setStateOptions(3);
        pValue2 = new NumericProperty("value2");
        pValue2.setStateOptions(4);
        pValue3 = new NumericProperty("value3");
        pValue3.setStateOptions(5);
        pValue4 = new NumericProperty("value4");
        pValue4.setStateOptions(6);
        StringProperty astringproperty[] = {
            pLogFile, pResetFile, pRulesFile, pNoFileCheckExist, pAlerting, pMatch, pMatches, pLines, pMatchDetails, pLastAlertsPerMinute, 
            pLastLinesPerMinute, pLogName, pLastFilePosition, pStartSearchPosition, pLastModDate, pLastMeasurement, pMessage, pValue, pValue2, pValue3, 
            pValue4, pValueLabels
        };
        String s = (COM.dragonflow.StandardMonitor.LogMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("matchCount > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Scans log files for specific log entries.");
        setClassProperty(s, "help", "LogFileMon.htm");
        setClassProperty(s, "title", "Log File");
        setClassProperty(s, "class", "LogMonitor");
        setClassProperty(s, "target", "_logFile");
        setClassProperty(s, "topazName", "Log Monitor");
        setClassProperty(s, "topazType", "System Resources");
        setClassProperty(s, "classType", "advanced");
    }
}
