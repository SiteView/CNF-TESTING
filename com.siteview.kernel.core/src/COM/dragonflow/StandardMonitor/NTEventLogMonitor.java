/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NTEventLogMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NTEventLogMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.*;

import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.*;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor

public class NTEventLogMonitor extends ServerMonitor
{

    static StringProperty pLogName;
    static StringProperty pEventKey;
    static StringProperty pNotMatchEventKey;
    static StringProperty pEventType;
    static StringProperty pPositiveMatch;
    static StringProperty pNegativeMatch;
    static StringProperty pEventCategory;
    static StringProperty pEventMachine;
    static StringProperty pAlerting;
    static StringProperty pInterval;
    static StringProperty pMatchCount;
    static StringProperty pRecordCount;
    static StringProperty pNextRecord;
    static StringProperty pIntervalEvents;
    static StringProperty pIntervalMatchCount;
    static StringProperty pMessages;
    static StringProperty pCurrentEventID;
    static StringProperty pCurrentEventType;
    static StringProperty pCurrentEventMachine;
    static StringProperty pCurrentEventSource;
    static StringProperty pCurrentEventCategory;
    static StringProperty pCurrentEventMessage;
    static StringProperty pMatchDetails;
    static StringProperty pCurrentEventTime;
    static StringProperty pLogCleared;
    public static String ERROR_OR_WARNING_PATTERN;
    public static String ANY_PATTERN = "/.*/";
    static StringProperty pValue;
    static StringProperty pValue2;
    static StringProperty pValue3;
    static StringProperty pValue4;
    public static String TYPE_LABEL = "Type: ";
    public static String ID_LABEL = "ID: ";
    public static String CATEGORY_LABEL = "Category: ";
    public static String RECORD_LABEL = "Record: ";
    public static String SOURCE_LABEL = "Source: ";
    public static String MESSAGE_LABEL = "Message: ";
    public static String NEXT_RECORD_LABEL = "Next Record: ";
    public static String MACHINE_LABEL = "Machine: ";
    public static String TIME_LABEL = "Time: ";

    public NTEventLogMonitor()
    {
    }

    public String getTestURL()
    {
        String s = URLEncoder.encode(getProperty(pMachineName));
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=eventLog&machine=" + s;
        s1 = s1 + "&logName=" + getProperty(pLogName);
        return s1;
    }

    public boolean remoteCommandLineAllowed()
    {
        return false;
    }

    public boolean runOwnRules()
    {
        return getProperty(pAlerting).length() == 0;
    }

    public boolean isPropertyExcluded(StringProperty stringproperty, HTTPRequest httprequest)
    {
        return stringproperty == pVerifyError;
    }

    protected boolean update()
    {
        boolean flag = false;
        if(getPropertyAsBoolean(pVerifyError))
        {
            setProperty(pVerifyError, "");
        }
        String s = getProperty(pMachineName);
        int i = TextUtils.toInt(getProperty(pNextRecord));
        int j = TextUtils.toInt(getProperty(pLogCleared));
        String s3 = "";
        if(i == 0 && j == 0)
        {
            String s1 = "-elast";
            s3 = Platform.perfexCommand(s) + " " + s1 + " " + "\"" + getProperty(pLogName) + "\"";
        } else
        {
            String s2 = "-elog";
            s3 = Platform.perfexCommand(s) + " " + s2 + " " + "\"" + getProperty(pLogName) + "\"" + " " + i;
        }
        String s4 = getProperty(pEventKey);
        String s5 = getProperty(pNotMatchEventKey);
        String s6 = getProperty(pEventType);
        String s7 = getProperty(pPositiveMatch);
        String s8 = getProperty(pNegativeMatch);
        String s9 = getProperty(pEventCategory);
        String s10 = getProperty(pEventMachine);
        String s11 = s;
        if(s11.startsWith("\\\\"))
        {
            s11 = s11.substring(2);
        }
        Machine machine = Machine.getNTMachine(s11);
        Array array = null;
        int k = 0;
        if(machine != null && Machine.isNTSSH(s11))
        {
            if(s3.indexOf("\\\\" + s11) > 0)
            {
                s3 = TextUtils.replaceString(s3, "\\\\" + s11, "");
            }
            s3 = s3.substring(s3.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array = sshcommandline.exec(s3, machine, false);
            k = sshcommandline.exitValue;
        } else
        {
            CommandLine commandline = new CommandLine();
            array = commandline.exec(s3, Platform.getLock(s));
            k = commandline.getExitValue();
        }
        Enumeration enumeration = array.elements();
        String s12 = "";
        resetStateProperties();
        String s13 = getIntervalEvents(getProperty(pInterval), getProperty(pIntervalEvents));
        boolean flag1 = runOwnRules();
        int l = 0;
        int i1 = -1;
        int j1 = 0;
        Array array1 = new Array();
        long l1 = getSettingAsLong("_eventLogMessagesToSave", 10);
        StringBuffer stringbuffer = new StringBuffer();
        long l2 = getSettingAsLong("_NTEventLogMonitorDetailsMax", 1000);
        do
        {
            if(!enumeration.hasMoreElements() || flag1 && !stillActive())
            {
                break;
            }
            String s14 = (String)enumeration.nextElement();
            if(s14.startsWith(TYPE_LABEL))
            {
                String s16 = s14.substring(TYPE_LABEL.length());
                if(s16.equals("AuditFailure"))
                {
                    LogManager.log("RunMonitor", getProperty(pName) + ": mapping NT Event Log (AuditFailure -> Error)");
                    s16 = "Error";
                } else
                if(s16.equals("AuditSuccess"))
                {
                    LogManager.log("RunMonitor", getProperty(pName) + ": mapping NT Event Log (AuditSuccess -> Information)");
                    s16 = "Information";
                }
                setProperty(pCurrentEventType, s16);
            } else
            if(s14.startsWith(ID_LABEL))
            {
                setProperty(pCurrentEventID, s14.substring(ID_LABEL.length()));
            } else
            if(s14.startsWith(RECORD_LABEL))
            {
                s12 = s14.substring(RECORD_LABEL.length());
            } else
            if(s14.startsWith(CATEGORY_LABEL))
            {
                setProperty(pCurrentEventCategory, s14.substring(CATEGORY_LABEL.length()));
                if(getPropertyAsInteger(pCurrentEventCategory) == 0)
                {
                    setProperty(pCurrentEventCategory, "None");
                }
            } else
            if(s14.startsWith(SOURCE_LABEL))
            {
                setProperty(pCurrentEventSource, s14.substring(SOURCE_LABEL.length()));
            } else
            if(s14.startsWith(MACHINE_LABEL))
            {
                setProperty(pCurrentEventMachine, s14.substring(MACHINE_LABEL.length()));
            } else
            if(s14.startsWith(TIME_LABEL))
            {
                setProperty(pCurrentEventTime, s14.substring(TIME_LABEL.length()));
            } else
            if(s14.startsWith(MESSAGE_LABEL))
            {
                setProperty(pCurrentEventMessage, s14.substring(MESSAGE_LABEL.length()));
                if(s12.length() > 0)
                {
                    j1++;
                    String s17 = "";
                    boolean flag2 = true;
                    String s18 = getProperty(pCurrentEventSource) + ":" + getProperty(pCurrentEventID);
                    if(flag2 && s4.length() != 0)
                    {
                        if(TextUtils.isRegularExpression(s4))
                        {
                            flag2 = TextUtils.match(s18, s4);
                            if(flag2)
                            {
                                s17 = "Event: " + s18 + " ";
                            }
                        } else
                        {
                            String as[] = new String[2];
                            int j2 = TextUtils.splitChar(s4, ':', as);
                            if(j2 > 0 && as[0].length() > 0 && !as[0].equals("*") && as[0].indexOf(getProperty(pCurrentEventSource)) == -1)
                            {
                                flag2 = false;
                            }
                            if(j2 > 1 && as[1].length() > 0 && !as[1].equals("*") && !as[1].equals(getProperty(pCurrentEventID)))
                            {
                                flag2 = false;
                            }
                            if(flag2)
                            {
                                s17 = s17 + "Event: " + s18 + " ";
                            }
                        }
                    }
                    if(flag2 && s5.length() != 0)
                    {
                        if(TextUtils.isRegularExpression(s5))
                        {
                            if(TextUtils.match(s18, s5))
                            {
                                flag2 = false;
                            }
                        } else
                        {
                            String as1[] = new String[2];
                            int k2 = TextUtils.splitChar(s5, ':', as1);
                            boolean flag3 = false;
                            boolean flag4 = false;
                            if(k2 == 1)
                            {
                                flag4 = true;
                            }
                            if(k2 > 0 && as1[0].length() > 0)
                            {
                                if(as1[0].equals("*"))
                                {
                                    flag3 = true;
                                } else
                                if(as1[0].indexOf(getProperty(pCurrentEventSource)) > -1)
                                {
                                    flag3 = true;
                                }
                            }
                            if(k2 > 1 && as1[1].length() > 0)
                            {
                                if(as1[1].equals("*"))
                                {
                                    flag4 = true;
                                } else
                                if(as1[1].equals(getProperty(pCurrentEventID)))
                                {
                                    flag4 = true;
                                }
                            }
                            if(flag3 && flag4)
                            {
                                flag2 = false;
                            }
                        }
                    }
                    if(flag2 && s9.length() != 0)
                    {
                        if(!exactTextMatch(getProperty(pCurrentEventCategory), s9))
                        {
                            flag2 = false;
                        } else
                        {
                            s17 = s17 + "Category: " + s9 + " ";
                        }
                    }
                    if(flag2 && s6.length() != 0)
                    {
                        if(!exactTextMatch(getProperty(pCurrentEventType), s6))
                        {
                            flag2 = false;
                        } else
                        {
                            s17 = s17 + "Type: " + getProperty(pCurrentEventType) + " ";
                        }
                    }
                    if(flag2 && s10.length() != 0)
                    {
                        if(!TextUtils.match(getProperty(pCurrentEventMachine), s10))
                        {
                            flag2 = false;
                        } else
                        {
                            s17 = s17 + "Machine: " + s10 + " ";
                        }
                    }
                    if(flag2 && s7.length() != 0)
                    {
                        Array array2 = new Array();
                        StringBuffer stringbuffer2 = new StringBuffer();
                        int i3 = TextUtils.matchExpression(getProperty(pCurrentEventMessage), s7, array2, stringbuffer2);
                        if(i3 != Monitor.kURLok)
                        {
                            String s21 = URLMonitor.getHTMLEncoding(getProperty(pCurrentEventMessage));
                            i3 = TextUtils.matchExpression(getProperty(pCurrentEventMessage), I18N.UnicodeToString(s7, s21), array2, stringbuffer2);
                        }
                        if(i3 == Monitor.kURLok)
                        {
                            if(array2.size() > 0)
                            {
                                flag = true;
                            }
                            if(array2.size() == 1)
                            {
                                setProperty(pValue, array2.at(0));
                                s17 = s17 + "Positive: " + array2.at(0) + " ";
                            } else
                            {
                                if(array2.size() >= 1)
                                {
                                    setProperty(pValue, array2.at(0));
                                    s17 = s17 + "value: " + array2.at(0) + " ";
                                }
                                if(array2.size() >= 2)
                                {
                                    setProperty(pValue2, array2.at(1));
                                    s17 = s17 + "value2: " + array2.at(1) + " ";
                                }
                                if(array2.size() >= 3)
                                {
                                    setProperty(pValue3, array2.at(2));
                                    s17 = s17 + " value3: " + array2.at(2) + " ";
                                }
                                if(array2.size() >= 4)
                                {
                                    setProperty(pValue4, array2.at(3));
                                    s17 = s17 + "value4: " + array2.at(3) + " ";
                                }
                            }
                        } else
                        {
                            flag2 = false;
                        }
                    }
                    if(flag2 && s8.length() != 0)
                    {
                        if(TextUtils.match(getProperty(pCurrentEventMessage), s8))
                        {
                            flag2 = false;
                        } else
                        {
                            s17 = s17 + "Negative: " + s8 + " ";
                        }
                    }
                    if(flag2)
                    {
                        l++;
                        if(flag1)
                        {
                            String s19 = getProperty(pCurrentEventType);
                            if(s19.equals("Error"))
                            {
                                s19 = ERROR_CATEGORY;
                            } else
                            if(s19.equals("Warning"))
                            {
                                s19 = WARNING_CATEGORY;
                            } else
                            if(s19.equals("Information"))
                            {
                                s19 = GOOD_CATEGORY;
                            }
                            ResetCategoryProperties(s19);
                            setProperty(pCategory, s19);
                            IncrementCategoryProperties(s19, s19);
                            setProperty(pStateString, getProperty(pCurrentEventSource) + ":" + getProperty(pCurrentEventID) + " on " + getProperty(pCurrentEventMachine) + ", category: " + getProperty(pCurrentEventCategory) + ", message: " + getProperty(pCurrentEventMessage) + ", Matched on: " + s17 + ", timestamp: " + getProperty(pCurrentEventTime));
                            runActionRules(this, getProperty(pCategory));
                        } else
                        {
                            if((long)array1.size() < l1)
                            {
                                array1.add(s18 + "\n" + getProperty(pCurrentEventMessage));
                            }
                            if(getPropertyAsInteger(pInterval) > 0)
                            {
                                Date date = TextUtils.stringToDate(getProperty(pCurrentEventTime));
                                if(s13.length() > 0)
                                {
                                    s13 = s13 + ",";
                                }
                                s13 = s13 + "" + date.getTime();
                            }
                        }
                    }
                }
                if(!flag1)
                {
                    appendMatches(stringbuffer, l2);
                }
                resetStateProperties();
            } else
            if(s14.startsWith(NEXT_RECORD_LABEL))
            {
                i1 = TextUtils.toInt(s14.substring(NEXT_RECORD_LABEL.length()));
                if(i1 < i)
                {
                    i1 = 0;
                    setProperty(pLogCleared, "1");
                    LogManager.log("RunMonitor", getProperty(pName) + ": wrapping to start of NT Event Log");
                } else
                {
                    setProperty(pLogCleared, "0");
                }
            }
        } while(true);
        setProperty(pMatchDetails, stringbuffer.toString().replace('\n', '^'));
        if(flag1)
        {
            setProperty(pCategory, GOOD_CATEGORY);
        }
        String s15 = "" + l + " matches in " + j1 + " entries";
        if(stillActive())
        {
            synchronized(this)
            {
                int k1 = 0;
                if(s13.length() > 0)
                {
                    k1 = 1;
                    for(int i2 = s13.indexOf(","); i2 >= 0; i2 = s13.indexOf(",", i2 + 1))
                    {
                        k1++;
                    }

                }
                Enumeration enumeration1 = array1.elements();
                StringBuffer stringbuffer1 = new StringBuffer();
                for(; enumeration1.hasMoreElements(); stringbuffer1.append("\n\n"))
                {
                    String s20 = (String)enumeration1.nextElement();
                    stringbuffer1.append(s20);
                }

                setProperty(pMessages, stringbuffer1.toString().replace('\n', '^'));
                if(!flag1 && getPropertyAsInteger(pInterval) > 0)
                {
                    s15 = s15 + ", " + k1 + " matches in last ";
                    if(getPropertyAsInteger(pInterval) > 1)
                    {
                        s15 = s15 + getProperty(pInterval) + " minutes";
                    } else
                    {
                        s15 = s15 + "minute";
                    }
                }
                if(flag)
                {
                    s15 = appendRetainedValues(s15);
                }
                setProperty(pIntervalEvents, s13);
                setProperty(pIntervalMatchCount, k1);
                setProperty(pStateString, s15);
                if(i1 == -1)
                {
                    setProperty(pMatchCount, "n/a");
                    setProperty(pRecordCount, "n/a");
                    if(getSetting("_usePreviousRecordCount").length() == 0)
                    {
                        setProperty(pNextRecord, "0");
                    }
                    setProperty(pNoData, "n/a");
                    setProperty(pStateString, "Could not retrieve event log information");
                    if(flag1)
                    {
                        setProperty(pCategory, ERROR_CATEGORY);
                    }
                    LogManager.log("RunMonitor", getProperty(pName) + ": error reading NT Event Log, exit status = " + k);
                } else
                {
                    setProperty(pMatchCount, l);
                    setProperty(pRecordCount, j1);
                    setProperty(pNextRecord, i1);
                }
            }
        }
        return true;
    }

    private void appendMatches(StringBuffer stringbuffer, long l)
    {
        String s = "Type: '" + getProperty(pCurrentEventType) + "' Event Time: '" + getProperty(pCurrentEventTime) + "' Source: '" + getProperty(pCurrentEventSource) + "' ID: '" + getProperty(pCurrentEventID) + "' Category: '" + getProperty(pCurrentEventCategory) + "' Machine: '" + getProperty(pCurrentEventMachine) + "' Message: '" + getProperty(pCurrentEventMessage) + "'^";
        TextUtils.appendAndTerminateString(s, stringbuffer, l, " _NTEventLogMonitorDetailsMax=" + l + " defined in groups/master.config file.");
    }

    void resetStateProperties()
    {
        unsetProperty(pCurrentEventID);
        unsetProperty(pCurrentEventType);
        unsetProperty(pCurrentEventCategory);
        unsetProperty(pCurrentEventMachine);
        unsetProperty(pCurrentEventSource);
        unsetProperty(pCurrentEventMessage);
        unsetProperty(pCurrentEventTime);
    }

    private String appendRetainedValues(String s)
    {
        String as[] = {
            getProperty(pValue), getProperty(pValue2), getProperty(pValue3), getProperty(pValue4)
        };
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            if(s1 != null && s1.length() > 0)
            {
                s = s + ", " + getMatchLabel(i) + " = " + s1;
            }
        }

        return s;
    }

    private String getMatchLabel(int i)
    {
        if(i == 0)
        {
            return "value";
        } else
        {
            return "value" + (i + 1);
        }
    }

    static String getIntervalEvents(String s, String s1)
    {
        long l = TextUtils.toLong(s) * 60L * 1000L;
        if(l > 0L)
        {
            long l1 = Platform.timeMillis() - l;
            int i = 0;
            for(int j = s1.indexOf(","); j >= 0; j = s1.indexOf(",", i))
            {
                String s2 = s1.substring(i, j);
                if(TextUtils.toLong(s2) >= l1)
                {
                    return s1.substring(i);
                }
                i = j + 1;
            }

            if(s1.length() > 0)
            {
                String s3 = s1.substring(i);
                if(TextUtils.toLong(s3) >= l1)
                {
                    return s1;
                }
            }
        }
        return "";
    }

    boolean exactTextMatch(String s, String s1)
    {
        if(TextUtils.isRegularExpression(s1))
        {
            return TextUtils.match(s, s1);
        } else
        {
            return s.equals(s1);
        }
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pMatchCount);
        array.add(pRecordCount);
        array.add(pIntervalMatchCount);
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Enumeration enumeration = super.getStatePropertyObjects(flag);
        Array array = new Array();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            StringProperty stringproperty = (StringProperty)enumeration.nextElement();
            if(stringproperty == pIntervalMatchCount)
            {
                if(getPropertyAsInteger(pInterval) > 0)
                {
                    array.add(stringproperty);
                }
            } else
            {
                array.add(stringproperty);
            }
        } while(true);
        return array.elements();
    }

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pDiagnosticText)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            }
            String s = getProperty(pMessages);
            long l = getSettingAsLong("_eventLogMessagesToSave", 10);
            String s1 = "";
            if(l != 0L && s.length() > 0)
            {
                s = s.replace('^', '\n');
                s1 = "Event Description(s):\n" + s + "\n";
            }
            return s1;
        } else
        {
            return super.getProperty(stringproperty);
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pEventKey)
        {
            if(s.startsWith("\""))
            {
                s = s.substring(1);
            }
            if(s.endsWith("\""))
            {
                s = s.substring(0, s.length() - 1);
            }
            return s;
        }
        if(stringproperty == pInterval)
        {
            if(getProperty(pAlerting).length() == 0)
            {
                s = "";
            }
            if(s.length() > 0 && !TextUtils.onlyChars(s, "0123456789"))
            {
                hashmap.put(stringproperty, "interval must be a number of minutes in the interval");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pLogName)
        {
            Vector vector = new Vector();
            vector.addElement("System");
            vector.addElement("System");
            vector.addElement("Application");
            vector.addElement("Application");
            vector.addElement("Security");
            vector.addElement("Security");
            vector.addElement("Directory Service");
            vector.addElement("Directory Service");
            vector.addElement("DNS");
            vector.addElement("DNS");
            vector.addElement("File Replication Service");
            vector.addElement("File Replication Service");
            return vector;
        }
        if(scalarproperty == pEventType)
        {
            Vector vector1 = new Vector();
            vector1.addElement(ANY_PATTERN);
            vector1.addElement("Any");
            vector1.addElement("Error");
            vector1.addElement("Error");
            vector1.addElement("Warning");
            vector1.addElement("Warning");
            vector1.addElement(ERROR_OR_WARNING_PATTERN);
            vector1.addElement("Error or Warning");
            vector1.addElement("Information");
            vector1.addElement("Information");
            return vector1;
        }
        if(scalarproperty == pAlerting)
        {
            Vector vector2 = new Vector();
            vector2.addElement("each");
            vector2.addElement("for each event matched");
            vector2.addElement("once");
            vector2.addElement("once, after all events have been checked");
            return vector2;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String defaultTitle()
    {
        String s = "NT " + getProperty(pLogName) + " Log";
        if(getProperty(pEventKey).length() > 0 && !TextUtils.isRegularExpression(getProperty(pEventKey)))
        {
            s = s + " " + getProperty(pEventKey);
        }
        return s;
    }

    public static void main(String args[])
    {
        String s = "5";
        long l = Platform.timeMillis();
        String s1 = "";
        for(int i = 10; i >= 1; i--)
        {
            if(i < 10)
            {
                s1 = s1 + ",";
            }
            s1 = s1 + "" + (l - (long)(60000 * i));
        }

        System.out.println("START=" + s1);
        System.out.println("INTERVALEVENTS=" + getIntervalEvents(s, s1));
        System.out.println("INTERVALEVENTSEMPTY=" + getIntervalEvents(s, ""));
        System.out.println("INTERVALEVENTSONE=" + getIntervalEvents(s, "" + (l - 10L)));
        System.out.println("INTERVALEVENTSOLD=" + getIntervalEvents(s, "" + (l - 0x124f80L)));
    }

    static 
    {
        ERROR_OR_WARNING_PATTERN = "/^Error|Warning$/";
        pLogName = new ScalarProperty("_logName", "System");
        pLogName.setDisplayText("Log Name", "the event log to monitor");
        pLogName.setParameterOptions(true, 1, false);
        pEventType = new ScalarProperty("_eventType", ERROR_OR_WARNING_PATTERN);
        pEventType.setDisplayText("Event Type", "the event types to match");
        pEventType.setParameterOptions(true, 2, false);
        pAlerting = new ScalarProperty("_alerting", "");
        pAlerting.setDisplayText("Run Alerts", "How alerts for this monitor are triggered by the following options:  <TR><TD>(1) For <b>'for each event matched and report status'</b> the monitor triggers alerts for every matching event found based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n<TR><TD>(2) For <b>'once, after all events have been checked'</b>, the events received since the last monitor run are checked and alerts are triggered based on the <b>Error If</b> and <b>Warning If</b> thresholds defined for the monitor. </TD></TR>\n");
        pAlerting.setParameterOptions(true, 3, false);
        pEventKey = new StringProperty("_eventKey");
        pEventKey.setDisplayText("Source and ID Match", "optional match for event Source:ID - leaving this blanks matches events with any source or ID.\n For example, entering \"Print\" matches the Print source, and entering \"Print:10\" matches the Print source with event ID 10");
        pEventKey.setParameterOptions(true, 4, true);
        pNotMatchEventKey = new StringProperty("_notMatchEventKey");
        pNotMatchEventKey.setDisplayText("Source and ID NOT Match", "ignore events that match Source:ID - leaving this blank matches events with any source or ID.\n For example, entering \"Print\" will ignore the Print source events, and entering \"Print:10\" ignore Print source with event ID 10");
        pNotMatchEventKey.setParameterOptions(true, 5, true);
        pPositiveMatch = new StringProperty("_positiveMatch");
        pPositiveMatch.setDisplayText("Description Match", "optional text or <a href=/SiteView/docs/regexp.htm>regular expression</a> to match only those events with descriptions that include this text");
        pPositiveMatch.setParameterOptions(true, 6, true);
        pNegativeMatch = new StringProperty("_negativeMatch");
        pNegativeMatch.setDisplayText("Description Not Match", "optional text or <a href=/SiteView/docs/regexp.htm>regular expression</a> to match only those events with descriptions that do not include this text");
        pNegativeMatch.setParameterOptions(true, 7, true);
        pEventCategory = new StringProperty("_eventCategory");
        pEventCategory.setDisplayText("Event Category", "optional event category - leaving this blanks matches events with any category");
        pEventCategory.setParameterOptions(true, 8, true);
        pEventMachine = new StringProperty("_eventMachine");
        pEventMachine.setDisplayText("Event Machine", "optional event machine - leaving this blanks matches events from any machine");
        pEventMachine.setParameterOptions(true, 9, true);
        pInterval = new NumericProperty("_interval", "");
        pInterval.setDisplayText("Interval", "optional interval (in minutes) keep the \"matches in interval\" count over - this is only used if \"Run Alerts\" above is set to \"once\".");
        pInterval.setParameterOptions(true, 10, true);
        pMatchCount = new NumericProperty("matchCount");
        pMatchCount.setDisplayText("match count", "match count");
        pMatchCount.setStateOptions(1);
        pRecordCount = new NumericProperty("recordCount");
        pRecordCount.setDisplayText("records examined", "records examined");
        pRecordCount.setStateOptions(2);
        pIntervalMatchCount = new NumericProperty("intervalMatchCount");
        pIntervalMatchCount.setDisplayText("matches in interval", "match in interval");
        pIntervalMatchCount.setStateOptions(3);
        pMessages = new StringProperty("messages");
        pIntervalEvents = new StringProperty("intervalEvents");
        pNextRecord = new NumericProperty("nextRecord");
        pCurrentEventID = new StringProperty("eventID");
        pCurrentEventType = new StringProperty("eventType");
        pCurrentEventMachine = new StringProperty("eventMachine");
        pCurrentEventSource = new StringProperty("eventSource");
        pCurrentEventCategory = new StringProperty("eventCategory");
        pCurrentEventMessage = new StringProperty("eventMessage");
        pCurrentEventTime = new StringProperty("eventTime");
        pMatchDetails = new StringProperty("matchDetails");
        pMatchDetails.setParameterOptions(false, false, 200, true);
        pLogCleared = new StringProperty("logCleared");
        pValue = new StringProperty("value");
        pValue.setStateOptions(4);
        pValue.setIsThreshold(true);
        pValue2 = new StringProperty("value2");
        pValue2.setStateOptions(5);
        pValue2.setIsThreshold(true);
        pValue3 = new StringProperty("value3");
        pValue3.setStateOptions(6);
        pValue3.setIsThreshold(true);
        pValue4 = new StringProperty("value4");
        pValue4.setStateOptions(7);
        pValue4.setIsThreshold(true);
        StringProperty astringproperty[] = {
            pLogName, pEventKey, pNotMatchEventKey, pEventType, pPositiveMatch, pNegativeMatch, pEventCategory, pEventMachine, pAlerting, pInterval, 
            pMatchCount, pRecordCount, pNextRecord, pIntervalEvents, pIntervalMatchCount, pMessages, pCurrentEventID, pCurrentEventType, pCurrentEventMachine, pCurrentEventSource, 
            pCurrentEventCategory, pCurrentEventMessage, pCurrentEventTime, pMatchDetails, pLogCleared, pValue, pValue2, pValue3, pValue4
        };
        addProperties("COM.dragonflow.StandardMonitor.NTEventLogMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.NTEventLogMonitor", Rule.stringToClassifier("matchCount > 0\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.NTEventLogMonitor", Rule.stringToClassifier("matchCount == 'n/a'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.NTEventLogMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "description", "Scans Windows NT Event Log for events");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "help", "NTEvtLogMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "title", "Windows Event Log");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "class", "NTEventLogMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "target", "_logName");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "runOwnRules", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "toolName", "Event Log");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "toolDescription", "Display portions of the Event Log.");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "topazName", "NT Event Log");
        setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "topazType", "System Resources");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.NTEventLogMonitor", "loadable", "false");
        }
    }
}
