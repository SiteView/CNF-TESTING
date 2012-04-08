/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * LogMonitorBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>LogMonitorBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Braf;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.LineReader;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ServerMonitor, Monitor, Machine, Platform,
// OSAdapter, Rule

public abstract class LogMonitorBase extends ServerMonitor {

    protected static final String logFileName = "_logFile";

    protected static final String logNameName = "logName";

    protected static final String rulesFileName = "_rulesFile";

    protected static final String noFileCheckExistName = "_noFileCheckExist";

    protected static final String matchName = "_match";

    protected static final String alertingName = "_alerting";

    protected static final String lastAlertsPerMinuteName = "lastAlertsPerMinute";

    protected static final String lastLinesPerMinuteName = "lastLinesPerMinute";

    protected static final String matchesName = "matchCount";

    protected static final String linesName = "lineCount";

    protected static final String lastFilePositionName = "lastFilePosition";

    protected static final String startSearchPositionName = "startSearchPosition";

    protected static final String lastModDateName = "lastModDate";

    protected static final String lastMeasurementName = "lastMeasurement";

    protected static final String messageName = "message";

    protected static final String matchDetailsName = "matchDetails";

    protected static final String valueName = "value";

    protected static final String value2Name = "value2";

    protected static final String value3Name = "value3";

    protected static final String value4Name = "value4";

    protected static final String valueLabelsName = "_valeLabels";

    protected static final String logFileNameDispalyText = "the pathname of the log file to monitor\n<p> In order to monitor remote Unix files choose the 'Choose Server' link above.  For NT, you must specify the UNC path to the file.  For example, \\\\machinename\\sharename\\filename.log.\n<p>Optionally, use a <a href=/SiteView/docs/regexp.htm>regular expression</a> to insert date and time variables <br>(e.g s/ex$shortYear$$0month$$0day$.log/ to match IIS log files)";

    protected static final String alertingNameDisplayText = "How alerts for this monitor are triggered by the following options:  <TR><TD>(1) For <b>'for each log entry matched and report status'</b> the monitor triggers alerts for every matching entry found based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n<TR><TD>(2) For <b>'once, after all log entries have been checked'</b>, the monitor counts up the number of matches and then triggers alerts based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n";

    protected static final String matchNameDisplayText = "enter the text to match in a log entry or a <a href=/SiteView/docs/regexp.htm>regular expression</a>. By default successful match makes monitor alert or error.";

    protected static final String ruleFileDisplayText = "optional, pathname to a custom rules file, used instead of Content Match (for example "
            + (Platform.isWindows() ? "C:" : "/usr") + "/SiteView/classes/CustomMonitor/examples/sample.rules";

    protected static final String noFileCheckExistDisplayText = "if the file is not found then don't error.";

    protected static final String valueLabelsDisplayText = "Labels for the values matched on the script output, separated by a \",\"";

    protected static final String logNameisplayText = "this is the path to the log file. However, it <b>should not be used</b>, because it is not loaded all of the time. It depends on the state of the monitor";

    HashMap labelsCache;

    boolean usingRules;

    Vector rules;

    long lastRulesModDate;

    public LogMonitorBase() {
        labelsCache = null;
        usingRules = false;
        rules = new Vector();
        lastRulesModDate = 0L;
    }

    protected abstract StringProperty getMeasurementProperty();

    protected abstract StringProperty getLogFileProperty();

    protected abstract StringProperty getLogNameProperty();

    protected abstract StringProperty getRulesFileProperty();

    protected abstract StringProperty getNoFileCheckExistProperty();

    protected abstract StringProperty getMatchProperty();

    protected abstract StringProperty getAlertingProperty();

    protected abstract StringProperty getLastAlertsPerMinuteProperty();

    protected abstract StringProperty getLastLinesPerMinuteProperty();

    protected abstract StringProperty getMatchesProperty();

    protected abstract StringProperty getLinesProperty();

    protected abstract StringProperty getLastFilePositionProperty();

    protected abstract StringProperty getStartSearchPositionProperty();

    protected abstract StringProperty getLastModDateProperty();

    protected abstract StringProperty getLastMeasurementProperty();

    protected abstract StringProperty getMessageProperty();

    protected abstract StringProperty getMatchDetailsProperty();

    protected abstract StringProperty getValueProperty();

    protected abstract StringProperty getValue2Property();

    protected abstract StringProperty getValue3Property();

    protected abstract StringProperty getValue4Property();

    protected abstract StringProperty getValueLabelsProperty();

    public boolean runOwnRules() {
        return getProperty(getAlertingProperty()).equals("each") && getProperty(getRulesFileProperty()).length() == 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            if (getProperty(getValueLabelsProperty()).length() > 0) {
                String as[] = TextUtils.split(getProperty(getValueLabelsProperty()), ",");
                labelsCache.add("value", as[0].trim());
                if (as.length >= 2) {
                    labelsCache.add("value2", as[1].trim());
                }
                if (as.length >= 3) {
                    labelsCache.add("value3", as[2].trim());
                }
                if (as.length >= 4) {
                    labelsCache.add("value4", as[3].trim());
                }
            } else {
                labelsCache.add("value", "value");
                labelsCache.add("value2", "value2");
                labelsCache.add("value3", "value3");
                labelsCache.add("value4", "value4");
            }
            Array array = getProperties();
            
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                StringProperty stringproperty = (StringProperty) enumeration.nextElement();
                if (stringproperty.isThreshold() && stringproperty.getName().indexOf("value") == -1) {
                    labelsCache.add(stringproperty.getLabel(), stringproperty.getLabel());
                }
            }
        }
        return labelsCache;
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if (s1.length() == 0) {
            s1 = s;
        }
        return s1;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if (s1.length() != 0) {
            return s1;
        }
        if (flag) {
            return "";
        } else {
            return s;
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
            throws SiteViewException {
        if (scalarproperty == getAlertingProperty()) {
            Vector vector = new Vector();
            vector.addElement("each");
            vector.addElement("for each log entry matched");
            vector.addElement("once");
            vector.addElement("once, after all log entries have been checked");
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public boolean isPropertyExcluded(StringProperty stringproperty, HTTPRequest httprequest) {
        return stringproperty == pVerifyError;
    }

    protected boolean update() {
        if (getPropertyAsBoolean(pVerifyError)) {
            setProperty(pVerifyError, "");
        }
        long l = 0L;
        try {
            l = Long.parseLong(getProperty(getLastMeasurementProperty()));
        } catch (NumberFormatException numberformatexception) {
        }
        Object obj = null;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        float f = -1F;
        float f1 = -1F;
        String s = readRules();
        if (s.length() == 0) {
            StringBuffer stringbuffer = new StringBuffer();
            long l4 = getSettingAsLong("_logMonitorDetailsMax", 1000);
            long al[] = checkLog(stringbuffer, l4);
            setProperty(getMatchDetailsProperty(), stringbuffer.toString().replace('\n', '^'));
            if (al.length > 1) {
                l1 = al[0];
                l2 = al[1];
                l3 = al[2];
            } else {
                if (al[0] != -1L) {
                    setProperty(pCategory, ERROR_CATEGORY);
                    String s2 = Monitor.lookupStatus(al[0]);
                    setProperty(pStateString, s2);
                    return true;
                }
                if (getProperty(getNoFileCheckExistProperty()).length() == 0) {
                    s = "unable to read log file";
                } else {
                    f = 0.0F;
                    f1 = 0.0F;
                    l3 = 0L;
                }
            }
            float f2 = (l3 - l) / 1000L;
            if (f2 > 0.0F) {
                f = 60F * ((float) l1 / f2);
                if (f < 0.0F) {
                    f = 0.0F;
                }
                f1 = 60F * ((float) l2 / f2);
                if (f1 < 0.0F) {
                    f1 = 0.0F;
                }
            }
        }
        if (runOwnRules()) {
            if (s.length() == 0) {
                setProperty(pCategory, GOOD_CATEGORY);
            } else {
                setProperty(pCategory, ERROR_CATEGORY);
            }
        }
        if (stillActive()) {
            synchronized (this) {
                if (s.length() == 0) {
                    setProperty(getLastAlertsPerMinuteProperty(), String.valueOf(f));
                    setProperty(getLastLinesPerMinuteProperty(), String.valueOf(f1));
                    setProperty(getLastMeasurementProperty(), String.valueOf(l3));
                    setProperty(getMatchesProperty(), l1);
                    setProperty(getLinesProperty(), l2);
                    setProperty(getMeasurementProperty(), getMeasurement(getLastAlertsPerMinuteProperty(), 10L));
                    setProperty(pStateString, l1 + " matches, " + TextUtils.floatToString(f, 2) + " matches/min, "
                            + TextUtils.floatToString(f1, 2) + " log entries/min");
                    String s1 = getProperty(getLogFileProperty());
                    if (TextUtils.isSubstituteExpression(s1)) {
                        s1 = TextUtils.substitute(s1);
                    }
                    setProperty(getLogNameProperty(), s1);
                } else {
                    setProperty(pStateString, s);
                    setProperty(getMeasurementProperty(), 0);
                    setProperty(getLastAlertsPerMinuteProperty(), "n/a");
                    setProperty(getLastLinesPerMinuteProperty(), "n/a");
                    setProperty(getMatchesProperty(), "n/a");
                    setProperty(getLinesProperty(), "n/a");
                    setProperty(getLastMeasurementProperty(), String.valueOf(0));
                    setProperty(getLogNameProperty(), "n/a");
                    setProperty(pNoData, "n/a");
                }
            }
        }
        return true;
    }

    long getRemoteLogFileSize(String s) {
        String s1 = getProperty(pMachineName);
        OSAdapter osadapter = Machine.getAdapter(s1);
        if (osadapter == null) {
            LogManager.log("Error", "Could not get adapter for machine " + s1);
            return -1L;
        }
        HashMap hashmap = new HashMap();
        hashmap.put("file", s);
        String s2 = Machine.getCommandString("fileExists", s1, hashmap);
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s2, s1, Platform.getLock(s1));
        int i = commandline.getExitValue();
        if (i < 0) {
            return (long) i;
        }
        LineReader linereader = new LineReader(array, osadapter, "fileExists");
        String s3 = osadapter.getCommandSetting("fileExists", "match");
        String s4 = osadapter.getCommandSetting("fileExists", "changeDirectory");
        do {
            if (!linereader.processLine()) {
                break;
            }
            if (linereader.skipLine()) {
                continue;
            }
            String s5 = linereader.getCurrentLine();
            s5 = s5.trim();
            if (!s5.startsWith(s3)) {
                return -1L;
            }
            break;
        } while (true);
        s2 = Machine.getCommandString("filesize", s1, hashmap);
        commandline = new CommandLine();
        array = commandline.exec(s2, s1, Platform.getLock(s1));
        i = commandline.getExitValue();
        if (i < 0) {
            return (long) i;
        }
        int j = osadapter.getCommandSettingAsInteger("filesize", "size");
        String s6 = "";
        linereader = new LineReader(array, osadapter, "filesize");
        do {
            if (!linereader.processLine()) {
                break;
            }
            if (linereader.skipLine()) {
                continue;
            }
            s6 = linereader.readColumn(j, "size");
            if (s6.length() > 0 && TextUtils.isInteger(s6)) {
                break;
            }
            s6 = "-1";
        } while (true);
        LogManager.log("RunMonitor", "* getRemoteLogFileSize(" + s + ") returning " + TextUtils.toLong(s6));
        return TextUtils.toLong(s6);
    }

    Array readRemoteLogFile(String s, long l) {
        String s1 = getProperty(pMachineName);
        OSAdapter osadapter = Machine.getAdapter(s1);
        if (osadapter == null) {
            LogManager.log("Error", "Could not get adapter for machine " + s1);
            return null;
        } else {
            HashMap hashmap = new HashMap();
            hashmap.put("file", getProperty(getLogNameProperty()));
            hashmap.put("bytes", "" + l);
            String s2 = Machine.getCommandString("tail", s1, hashmap);
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s2, s1, Platform.getLock(s1));
            return array;
        }
    }

    String getNextLine(Braf braf, Array array, int i, boolean flag) throws IOException {
        String s = null;
        if (braf != null) {
            s = braf.readLine(flag);
        } else if (i < array.size()) {
            s = (String) array.at(i);
        }
        return s;
    }

    long getEndPosition(String s, String s1) {
        long l = -1L;
        if (s1.length() == 0) {
            File file = new File(s);
            if (!file.exists()) {
                return -1L;
            }
            l = file.length();
        } else {
            l = getRemoteLogFileSize(s);
        }
        LogManager.log("RunMonitor", "* getEndPosition(" + s + ") returning " + l);
        return l;
    }

    public long getStartPosition(long l, String s) {
        long l1 = -1L;
        long l2 = -1L;
        long l3 = -1L;
        try {
            l2 = Long.parseLong(getProperty(getLastFilePositionProperty()));
            l3 = Long.parseLong(getProperty(getStartSearchPositionProperty()));
        } catch (NumberFormatException numberformatexception) {
        }
        LogManager
                .log("RunMonitor", s + ": endPosition=" + l + ", prevEofPosition=" + l2 + ", prevStartPosition=" + l3);
        String s1 = getProperty(getLastLinesPerMinuteProperty());
        if (0L <= l3 && l3 < l2) {
            l1 = l3;
        } else {
            l1 = l2;
        }
        if (l2 == l) {
            LogManager.log("RunMonitor", "getStartPosition(" + s + "): file size hasn't changed");
            l1 = -1L;
        } else if (l1 == -1L && s1.equals("n/a")) {
            LogManager.log("RunMonitor", "getStartPosition(" + s
                    + "): Previous run has no data, setting start position to start of log");
            l1 = 0L;
        } else if (l1 == -1L) {
            LogManager.log("RunMonitor", "getStartPosition(" + s
                    + "): Appears to be a new monitor, setting start position to end of log");
            l1 = l;
        } else if (l1 > l) {
            LogManager.log("RunMonitor", "getStartPosition(" + s
                    + "): This file smaller than previous, setting start position to beginning of log");
            l1 = 0L;
        } else {
            LogManager.log("RunMonitor", "getStartPosition(" + s + "): Log file has grown, setting start position to "
                    + l1);
        }
        LogManager.log("RunMonitor", "* getStartPosition(" + s + ") returning " + l1);
        return l1;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringbuffer
     * @param l
     * @return
     */
    long[] checkLog(StringBuffer stringbuffer, long l) {
        long [] result = null;
        Braf braf = null;
        String machineName = getProperty(pMachineName);
        String logFileName = getProperty(getLogFileProperty());
        if (TextUtils.isSubstituteExpression(logFileName)) {
            logFileName = TextUtils.substitute(logFileName);
        }
        if (machineName.length() == 0 && logFileName.indexOf("\\\\") == 0) {
            File file = new File(logFileName);
            String s2;
            if (!file.exists()) {
                s2 = Platform.closeAndConnectNetBIOSIfRemoteDefined(logFileName);
            }
        }
        
        setProperty(getMessageProperty(), "");
        long lineLen = 0L;
        
        try {
            long startTime = Platform.timeMillis();
            long lineCount = 0L;
            long faCount = 0L;
            long endSearchPosition= getEndPosition(logFileName, machineName);
            if (endSearchPosition < 0L) {
                result = new long[1];
                result[0] = endSearchPosition;                          
                return  result;
            }
            long startSearchPosition = getStartPosition(endSearchPosition, logFileName);
            long currentPos = startSearchPosition;
            
            if (0L <= startSearchPosition && startSearchPosition < endSearchPosition) {
                Array rlf = null;
                if (machineName.length() > 0) {
                    currentPos += 1;
                    if (currentPos <= endSearchPosition) {
                        rlf = readRemoteLogFile(logFileName, currentPos);
                    } else {
                        rlf = new Array();
                    }
                } else {
                    braf = new Braf(logFileName, currentPos);
                }
                
                int linesBufSize = 50;
                String tmpLine = "";
                String concatLine = "";
                String [] linesBuf = new String[linesBufSize];
                for (int i = 0; i < linesBufSize; i++) {
                    linesBuf[i] = "";
                }

                boolean mlRegEx = TextUtils.isMultiLineRegularExpression(getProperty(getMatchProperty()));
                LogManager.log("RunMonitor", "* checkLog(" + logFileName + "): isMultiLineSearch=" + mlRegEx);
               
                while (stillActive()) {
                    if ((tmpLine = getNextLine(braf, rlf, (int) lineCount, mlRegEx)) == null) {
                        break;
                    }
                    if (braf != null) {
                        lineLen += braf.lastLineLength();
                    }
                    
                    if (mlRegEx) {
                        concatLine = "";
                        currentPos += linesBuf[0].length();
                        for (int i = 0; i < linesBufSize - 1; i++) {
                            linesBuf[i] = linesBuf[i + 1];
                            concatLine = concatLine + linesBuf[i];
                        }

                        linesBuf[linesBufSize - 1] = tmpLine;
                        concatLine = concatLine + l;
                    } else {
                        concatLine = tmpLine;
                    }
                    
                    long fa = forwardAlerts(concatLine, "", stringbuffer, l);
                    faCount += fa;
                    
                    if (fa > 0L && mlRegEx) {
                        currentPos += concatLine.length();
                        for (int i = 0; i < linesBufSize; i++) {
                            linesBuf[i] = "";
                        }
                        concatLine = "";
                    }
                    lineCount ++;
                } 
                LogManager.log("RunMonitor", "* checkLog(" + logFileName + "): read " + lineCount + " lines from offset " + startSearchPosition + " to " + endSearchPosition);
                
                long eol = startSearchPosition + lineLen;
                if (mlRegEx) {
                    startSearchPosition = currentPos;
                } else if (endSearchPosition < eol) {
                    startSearchPosition = eol;
                    endSearchPosition = eol;
                } else {
                    startSearchPosition = endSearchPosition;
                }
            } else {
                setProperty(getValueProperty(), "");
                setProperty(getValue2Property(), "");
                setProperty(getValue3Property(), "");
                setProperty(getValue4Property(), "");
            }
            
            LogManager.log("RunMonitor", "* checkLog(" + logFileName + "): setting endPosition=" + endSearchPosition);
            setProperty(getLastFilePositionProperty(), String.valueOf(endSearchPosition));
            if (startSearchPosition >= 0L) {
                LogManager.log("RunMonitor", "* checkLog(" + logFileName + "): setting startSearchPosition=" + startSearchPosition);
                setProperty(getStartSearchPositionProperty(), String.valueOf(startSearchPosition));
            }
            
            result = new long[3];
            result[0] = faCount;
            result[1] = lineCount;
            result[2] = startTime;
        } catch (Exception e) {
            LogManager.log("Error", "reading log file: " + logFileName + ", e:" + e);
        }

        try {
            if (braf != null) {
                braf.close();
            }
        } catch (Exception e) {
            /*empty*/
        }
        
        OSAdapter osAdapter = Machine.getAdapter(machineName);
        if (osAdapter != null) {
            CommandLine commandline = new CommandLine();
            String osCDCmd = osAdapter.getCommandSetting("fileExists", "changeDirectory");
            String cdCmd;
            if (osCDCmd.length() > 0) {
                cdCmd = osCDCmd;
            } else {
                cdCmd = "/usr/bin/cd";
            }
            commandline.exec(cdCmd, machineName, Platform.getLock(machineName));
        }
        
        return result;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(getLastAlertsPerMinuteProperty());
        array.add(getLastLinesPerMinuteProperty());
        addValuesPropertiesToArray(array);
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        addValuesPropertiesToArray(array);
        array.add(getLastAlertsPerMinuteProperty());
        array.add(getLastLinesPerMinuteProperty());
        return array.elements();
    }

    boolean checkExpression(String s, String s1, int i) {
        Rule rule = Rule.stringToAction(s1 + "\t\t" + getProperty(pID));
        rule.setProperty(pID, i);
        addElement(rule);
        boolean flag = rule.match(this);
        if (flag) {
            IncrementAlertProperties(rule.getFullID(), s);
        }
        removeElement(rule);
        return flag;
    }

    void runAction(String s, int i) {
        Rule rule = Rule.stringToAction("always\t" + s + "\t" + getProperty(pID));
        rule.setProperty(pID, i);
        addElement(rule);
        rule.doAction(this);
        removeElement(rule);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param stringbuffer
     * @param l
     * @return
     */
    protected int forwardAlerts(String s, String s1, StringBuffer stringbuffer, long l) {
        //TODO need review
        int i = 0;
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        if (!usingRules) {
            String s6 = getProperty(getMatchProperty());
            Array array = new Array();
            StringBuffer stringbuffer1 = new StringBuffer();
            int k = TextUtils.matchExpression(s, s6, array, stringbuffer1);
            if (k == Monitor.kURLok) {
                i++;
                boolean flag = false;
                if (getSetting("_showValsInMessage").length() > 0) {
                    flag = true;
                }
                StringBuffer stringbuffer3 = new StringBuffer();
                if (array.size() == 1) {
                    s2 = (String) array.at(0);
                    if (flag) {
                        stringbuffer3.append("match1: " + array.at(0));
                    }
                } else {
                    if (array.size() >= 1) {
                        s2 = (String) array.at(0);
                        if (flag) {
                            stringbuffer3.append("match1: " + array.at(0));
                        }
                    }
                    if (array.size() >= 2) {
                        s3 = (String) array.at(1);
                        if (flag) {
                            stringbuffer3.append(" match2: " + array.at(1));
                        }
                    }
                    if (array.size() >= 3) {
                        s4 = (String) array.at(2);
                        if (flag) {
                            stringbuffer3.append(" match3: " + array.at(2));
                        }
                    }
                    if (array.size() >= 4) {
                        s5 = (String) array.at(3);
                        if (flag) {
                            stringbuffer3.append(" match4: " + array.at(3));
                        }
                    }
                }
                setProperty(getValueProperty(), s2);
                setProperty(getValue2Property(), s3);
                setProperty(getValue3Property(), s4);
                setProperty(getValue4Property(), s5);
                setProperty(getMessageProperty(), s + stringbuffer3);
                if (runOwnRules()) {
                    String s8 = ERROR_CATEGORY;
                    ResetCategoryProperties(s8);
                    if (getSetting("_alertNoPrefix").length() > 0) {
                        setProperty(pStateString, s);
                    } else {
                        setProperty(pStateString, "matched: " + s);
                    }
                    setProperty(pCategory, s8);
                    IncrementCategoryProperties(s8, s8);
                    runActionRules(this, getProperty(pCategory));
                } else {
                    TextUtils.appendAndTerminateString(s, stringbuffer, l, " _logMonitorDetailsMax=" + l
                            + " defined in groups/master.config file.");
                }
            }
        } else {
            Enumeration enumeration = rules.elements();
            int j = 0;
            String s10;
            label0: do {
                Array array1;
                Array array2;
                String s11;
                boolean flag1;
                do {
                    if (!enumeration.hasMoreElements()) {
                        break label0;
                    }
                    j++;
                    array1 = new Array();
                    StringBuffer stringbuffer2 = new StringBuffer();
                    array2 = (Array) enumeration.nextElement();
                    String s7 = (String) array2.at(0);
                    String s9 = (String) array2.at(1);
                    s10 = (String) array2.at(2);
                    s11 = (String) array2.at(3);
                    flag1 = false;
                    if (s1.length() > 0) {
                        if (s7.equals("RULE") && array2.size() > 4) {
                            String s12 = (String) array2.at(4);
                            String s14 = s9;
                            if (s14.equals(s1)) {
                                flag1 = checkExpression(getProperty(s9), s12, j);
                            }
                        }
                    } else {
                        String s13 = s;
                        int j1 = TextUtils.toInt(s7);
                        if (j1 > 0) {
                            s13 = TextUtils.readColumn(s, j1);
                        }
                        if (s9.equals("ANY")) {
                            flag1 = true;
                        } else {
                            int l1 = TextUtils.matchExpression(s13, s9, array1, stringbuffer2);
                            if (l1 == Monitor.kURLok) {
                                flag1 = true;
                            }
                        }
                    }
                } while (!flag1);
                
                try {
                    setProperty(getMessageProperty(), s);
                    setProperty(pStateString, "matched: " + s);
                    setProperty(pCategory, ERROR_CATEGORY);
                    int i1 = array1.size() >= 4 ? 4 : array1.size();
                    switch (i1) {
                    case 4: // '\004'
                        s5 = (String) array1.at(3);
                    // fall through

                    case 3: // '\003'
                        s4 = (String) array1.at(2);
                    // fall through

                    case 2: // '\002'
                        s3 = (String) array1.at(1);
                    // fall through

                    case 1: // '\001'
                        s2 = (String) array1.at(0);
                    // fall through

                    default:
                        setProperty(getValueProperty(), s2);
                        break;
                    }
                    setProperty(getValue2Property(), s3);
                    setProperty(getValue3Property(), s4);
                    setProperty(getValue4Property(), s5);
                    runAction(s11 + (array2.size() <= 4 ? "" : " " + array2.at(4)), j);
                    if (s11.startsWith("SetCategory")) {
                        int k1 = s11.indexOf(' ');
                        int i2 = s11.indexOf(' ', k1 + 1);
                        String s15 = s11.substring(k1 + 1, i2);
                        i += forwardAlerts(s, s15, stringbuffer, l);
                    } else {
                        i++;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } while (s10.equals("y"));
        }
        return i;
    }

    public boolean hasProperty(String s) {
        return getPropertyObject(s) != null;
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            if (getProperty(pCategory).equals("good")) {
                return "";
            }
            String s = getProperty(getMatchDetailsProperty());
            String s1 = "";
            if (s.length() > 0) {
                s = s.replace('^', '\n');
                s1 = "Matched Log Entries:\n" + s + "\n";
            }
            return s1;
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public String getProperty(String s) throws NullPointerException {
        String s1 = "message[";
        if (s.startsWith(s1)) {
            String s2 = "";
            int i = TextUtils.readInteger(s, s1.length());
            Array array = Platform.split(',', getProperty(getMessageProperty()));
            if (i >= 0 && i < array.size()) {
                s2 = (String) array.at(i);
            }
            return s2;
        } else {
            return super.getProperty(s);
        }
    }

    String readRules() {
        String s = getProperty(getRulesFileProperty());
        if (s.length() == 0) {
            return "";
        }
        usingRules = true;
        File file = new File(s);
        long l = file.lastModified();
        if (l == lastRulesModDate) {
            return "";
        }
        lastRulesModDate = l;
        rules = new Vector();
        FileInputStream fileinputstream = null;
        BufferedReader bufferedreader = null;
        try {
            fileinputstream = new FileInputStream(file);
            bufferedreader = FileUtils.MakeInputReader(fileinputstream);
            do {
                String s1 = bufferedreader.readLine();
                if (s1 == null) {
                    break;
                }
                if (!s1.startsWith("#") && s1.length() != 0) {
                    char c = ',';
                    if (s1.indexOf('\t') != -1) {
                        c = '\t';
                    }
                    Array array = Platform.split(c, s1);
                    if (array.size() < 4 || array.size() > 5) {
                        LogManager.log("Error", "parsing rule, file: " + getProperty(getRulesFileProperty())
                                + ", rule:" + s1 + ", parts:" + array.size());
                    } else {
                        rules.addElement(array);
                    }
                }
            } while (true);
        } catch (Exception exception) {
            LogManager.log("Error", "reading rule file: " + getProperty(getRulesFileProperty()) + ", e:" + exception);
        }
        try {
            if (bufferedreader != null) {
                bufferedreader.close();
            }
            if (fileinputstream != null) {
                fileinputstream.close();
            }
        } catch (Exception exception1) {
        }
        LogManager.log("RunMonitor", "read " + rules.size() + " rules from " + s);
        if (rules.size() == 0) {
            return "unable to read rule file";
        } else {
            return "";
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getRulesFileProperty()) {
            if (s.length() != 0) {
                File file = new File(s);
                if (!file.exists()) {
                    hashmap.put(stringproperty, "could not find rules file");
                }
            }
            return s;
        }
        if (stringproperty == getLogFileProperty()) {
            if (s.length() == 0) {
                hashmap.put(getLogFileProperty(), getLogFileProperty().getLabel() + " missing file name");
            }
            return s;
        }
        if (stringproperty == getNoFileCheckExistProperty()) {
            String s1 = getProperty(getLogFileProperty());
            if (s1.length() > 0 && !TextUtils.isSubstituteExpression(s1) && getProperty(pMachineName).length() == 0) {
                File file1 = new File(s1);
                String s2 = "";
                boolean flag = s.length() == 0;
                if (!file1.exists()) {
                    s2 = Platform.closeAndConnectNetBIOSIfRemoteDefined(s1);
                }
                if (flag) {
                    if (s2.length() > 0) {
                        hashmap.put(getLogFileProperty(), getLogFileProperty().getLabel() + ": " + s2);
                    } else if (!file1.exists()) {
                        hashmap.put(getLogFileProperty(), "could not find log file");
                    }
                }
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public static void main(String args[]) throws Exception {
        long l = System.currentTimeMillis();
        System.out.println("start: " + (System.currentTimeMillis() - l));
        String s = "../logs/SiteView.log";
        if (args.length > 0) {
            s = args[0];
        }
        File file = new File(s);
        int i = 50000;
        if (args.length > 2) {
            i = TextUtils.toInt(args[2]);
        }
        System.out.println("reading: " + i);
        System.out.println("Reading using Braf");
        long l1 = file.length();
        Braf braf = new Braf(s, l1 - (long) i);
        int j = 0;
        System.out.println("skip: " + (System.currentTimeMillis() - l));
        String s1;
        do {
            j++;
            s1 = braf.readLine();
        } while (s1 != null);
        System.out.println("read: " + j + ", time: " + (System.currentTimeMillis() - l));
    }

    private void addValuesPropertiesToArray(Array array) {
        String s = getProperty(getMatchProperty());
        int i = 0;
        if (s.length() > 0) {
            array.add(getValueProperty());
            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c == '(' && j > 0 && s.charAt(j - 1) != '\\') {
                    i++;
                }
            }

            if (i > 1) {
                array.add(getValue2Property());
            }
            if (i > 2) {
                array.add(getValue3Property());
            }
            if (i > 3) {
                array.add(getValue4Property());
            }
        }
    }

    static {
        StringProperty astringproperty[] = new StringProperty[0];
        String s = (COM.dragonflow.SiteView.LogMonitorBase.class).getName();
        addProperties(s, astringproperty);
    }
}
