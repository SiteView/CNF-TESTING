/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WhatsUpMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WhatsUpMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.ems.Shared.EmsConfigFileProperty;
import java.io.File;
import java.util.Calendar;
import jgl.HashMap;

public class WhatsUpMonitor extends EMSLogMonitorBase
{

    private static StringProperty pWhatsUpInstallDir;
    private static StringProperty pLogFile;
    private static StringProperty pLogName;
    private static StringProperty pRulesFile;
    private static StringProperty pNoFileCheckExist;
    private static StringProperty pMatch;
    private static StringProperty pAlerting;
    private static ScalarProperty pResetFile;
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
    private static EmsConfigFileProperty pEmsConfigFilePath;
    private static StringProperty pEMSWorkingDir;
    private static final String MONITOR_NAME = "WhatsUp";
    private static final String EMS_LOG_DIR;

    public WhatsUpMonitor()
    {
    }

    protected StringProperty getMeasurementProperty()
    {
        return Monitor.pMeasurement;
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

    protected StringProperty getEmsConfigFilePathProperty()
    {
        return pEmsConfigFilePath;
    }

    protected boolean update()
    {
        setProperty(pLogFile, generateLogFileName(getProperty(pWhatsUpInstallDir)));
        return super.update();
    }

    private String generateLogFileName(String dirName)
    {
        if(!dirName.endsWith(File.separator))
        {
            dirName = dirName + File.separator;
        }
        return dirName + getWhatsUpLogfileName();
    }

    protected String getEmsConfigFilePath()
    {
        return pEmsConfigFilePath.getFileName(this);
    }

    protected ScalarProperty getResetFileProperty()
    {
        return pResetFile;
    }

    protected StringProperty getEmsConfigWorkingDir()
    {
        return pEMSWorkingDir;
    }

    public String getEmsWorkingDirectory()
    {
        return EMS_LOG_DIR;
    }

    private static String getWhatsUpLogfileName()
    {
        String logFileName = "EV-";
        Calendar c = Calendar.getInstance();
        c.set(7, c.getFirstDayOfWeek());
        return logFileName + c.get(1) + "-" + (c.get(2) + 1 >= 10 ? "" : "0") + (c.get(2) + 1) + "-" + (c.get(5) >= 10 ? "" : "0") + c.get(5) + ".tab";
    }

    public String verify(StringProperty property, String value, HTTPRequest httpRequest, HashMap errors)
    {
        if(property == pWhatsUpInstallDir)
        {
            LogManager.log("RunMonitor", "verifying file exists: " + value);
            String generatedFileName = generateLogFileName(value);
            File file = new File(value);
            File file2 = new File(generatedFileName);
            if(file.exists() && !file.isDirectory())
            {
                LogManager.log("RunMonitor", "file exists: " + value);
                setProperty(pLogFile, value);
            } else
            if(file2.exists() && !file2.isDirectory())
            {
                LogManager.log("RunMonitor", "file exists: " + generatedFileName);
                setProperty(pLogFile, generatedFileName);
            } else
            {
                errors.put(property, "the path does not point to a location of a whatsup installation");
            }
            return value;
        }
        if(property == getEmsConfigFilePathProperty() && value.equals(""))
        {
            return value;
        } else
        {
            return super.verify(property, value, httpRequest, errors);
        }
    }

    static 
    {
        EMS_LOG_DIR = Platform.getRoot() + File.separator + "ems" + File.separator + "WhatsUp" + File.separator;
        pWhatsUpInstallDir = new StringProperty("_whatsUpInstallDir", "");
        pWhatsUpInstallDir.setDisplayText("WhatsUp installation Directory", "Directory where whatsup is installed");
        pWhatsUpInstallDir.setParameterOptions(true, 2, false);
        pLogFile = new StringProperty("_logFile", "");
        pLogFile.setParameterOptions(false, 2, false);
        pMatch = new StringProperty("_match", "/(\\d{4})(\\d{2})(\\d{2})\\s(\\d{2})(\\d{2})(\\d{2})\\s([\\w\\W\\\\\\. ]*.wup)\\s(\\w*)\\s([\\.\\w]*)\\s([\\w\\.]*)\\s(.*)/");
        pMatch.setDisplayText("Content Match", "enter the text to match in a log entry or a <a href=/SiteView/docs/regexp.htm>regular expression</a>. By default successful match makes monitor alert or error.");
        pMatch.setParameterOptions(true, 3, false);
        pAlerting = new ScalarProperty("_alerting", "");
        pAlerting.setDisplayText("Run Alerts", "How alerts for this monitor are triggered by the following options:  <TR><TD>(1) For <b>'for each log entry matched and report status'</b> the monitor triggers alerts for every matching entry found based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n<TR><TD>(2) For <b>'once, after all log entries have been checked'</b>, the monitor counts up the number of matches and then triggers alerts based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n");
        pAlerting.setParameterOptions(true, 4, false);
        pRulesFile = new StringProperty("_rulesFile");
        pRulesFile.setParameterOptions(false, 4, true);
        pRulesFile.setDisplayText("Rules File Pathname", LogMonitorBase.ruleFileDisplayText);
        pNoFileCheckExist = new BooleanProperty("_noFileCheckExist");
        pNoFileCheckExist.setDisplayText("No Error on File Not Found.", "if the file is not found then don't error.");
        pNoFileCheckExist.setParameterOptions(true, 5, true);
        pValueLabels = new StringProperty("_valeLabels", "");
        pValueLabels.setDisplayText("Match Value Labels", "Labels for the values matched on the script output, separated by a \",\"");
        pValueLabels.setParameterOptions(false, 6, true);
        pEMSWorkingDir = new StringProperty("ems_work_dir_", EMS_LOG_DIR);
        pEMSWorkingDir.setDisplayText("", "");
        pEMSWorkingDir.setParameterOptions(false, 7, true);
        pResetFile = new ScalarProperty("_resetFile");
        pResetFile.setDisplayText("Check from Beginning", "Select file checking option as follows:\n <br><b>Never</b> - Check only newly added records (default)\n<br><b>First Time Only</b> - Check the whole file once, then only new records\n<br><b>Always</b> - Always check the whole file\n");
        pResetFile.setValue("none");
        pResetFile.setParameterOptions(false, 4, true);
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
        boolean isEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
        pEmsConfigFilePath = new EmsConfigFileProperty("WhatsUp");
        pEmsConfigFilePath.setParameterOptions(true, 3, true);
        StringProperty myProperties[] = {
            pLogFile, pRulesFile, pNoFileCheckExist, pAlerting, pMatch, pMatches, pLines, pResetFile, pMatchDetails, pLastAlertsPerMinute, 
            pLastLinesPerMinute, pLogName, pLastFilePosition, pStartSearchPosition, pLastModDate, pLastMeasurement, pMessage, pValue, pValue2, pValue3, 
            pValue4, pValueLabels, pEmsConfigFilePath, pEMSWorkingDir, pWhatsUpInstallDir
        };
        String fullClassName = (COM.dragonflow.StandardMonitor.WhatsUpMonitor.class).getName();
        PropertiedObject.addProperties(fullClassName, myProperties);
        SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount > 0\terror", true));
        SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
        SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("always\tgood"));
        PropertiedObject.setClassProperty(fullClassName, "description", "Sends WhatsUp Gold alerts to Topaz via WhatsUp Gold log file.");
        PropertiedObject.setClassProperty(fullClassName, "help", "WhatsUpMon.htm");
        PropertiedObject.setClassProperty(fullClassName, "classType", "advanced");
        PropertiedObject.setClassProperty(fullClassName, "title", "WhatsUp");
        PropertiedObject.setClassProperty(fullClassName, "class", "WhatsUpMonitor");
        PropertiedObject.setClassProperty(fullClassName, "target", "_ems_logFile");
        PropertiedObject.setClassProperty(fullClassName, "topazName", "WhatsUp Monitor");
        PropertiedObject.setClassProperty(fullClassName, "topazType", "System Resources");
        PropertiedObject.setClassProperty(fullClassName, "classType", "advanced");
        PropertiedObject.setClassProperty(fullClassName, "loadable", isEmsLicensed ? "true" : "false");
    }
}
