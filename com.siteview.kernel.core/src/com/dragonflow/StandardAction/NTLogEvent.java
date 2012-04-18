/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.Utils.CommandLine;
import com.dragonflow.Utils.CounterLock;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class NTLogEvent extends com.dragonflow.SiteView.Action {

    static com.dragonflow.Properties.StringProperty pTemplate;

    static com.dragonflow.Properties.StringProperty pEventSource;

    static com.dragonflow.Properties.StringProperty pEventID;

    static com.dragonflow.Properties.StringProperty pEventType;

    static com.dragonflow.Properties.StringProperty pEventCategory;

    static com.dragonflow.Properties.StringProperty pEventMachine;

    static com.dragonflow.Properties.StringProperty pMessage;

    static final String DEFAULT_TEMPLATE = "Default";

    static final String DEFAULT_SOURCE = "SiteView";

    static final String DEFAULT_CATEGORY_ID = "0";

    static final String DEFAULT_TYPE = "default";

    static final String DEFAULT_ID = "1000";

    static final String DEFAULT_MACHINE = "localhost";

    static final String DEFAULT_MESSAGE = "_";

    static com.dragonflow.Utils.CounterLock eventLogLock = new CounterLock(1);

    public void initializeFromArguments(Array array, HashMap hashmap) {
        if (array.size() > 0) {
            setProperty(pTemplate, array.get(0));
        } else {
            setProperty(pTemplate, "Default");
        }
        if (array.size() > 1) {
            setProperty(pEventSource, array.get(1));
        } else {
            setProperty(pEventSource, "SiteView");
        }
        if (array.size() > 2) {
            setProperty(pEventID, array.get(2));
        } else {
            setProperty(pEventID, "1000");
        }
        if (array.size() > 3) {
            setProperty(pEventType, array.get(3));
        } else {
            setProperty(pEventType, "default");
        }
        if (array.size() > 4) {
            setProperty(pEventCategory, array.get(4));
        } else {
            setProperty(pEventCategory, "0");
        }
        if (array.size() > 5) {
            setProperty(pEventMachine, array.get(5));
        } else {
            setProperty(pEventMachine, "localhost");
        }
        if (array.size() > 6) {
            setProperty(pMessage, ((String) array.get(6)).replace('_', ' '));
        } else {
            setProperty(pMessage, "_");
        }
    }

    public String getActionString() {
        String s = getProperty(pMessage);
        if (s.length() == 0) {
            s = "_";
        }
        s = s.replace(' ', '_');
        return "NTLogEvent " + getProperty(pTemplate) + " " + getProperty(pEventSource) + " " + getProperty(pEventID) + " " + getProperty(pEventType) + " " + getProperty(pEventCategory) + " " + getProperty(pEventMachine) + " " + s;
    }

    public String getActionDescription() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Log ");
        if (!getProperty(pEventType).equals("default")) {
            stringbuffer.append(com.dragonflow.Utils.TextUtils.toInitialUpper(getProperty(pEventType)));
            stringbuffer.append(" ");
        }
        stringbuffer.append("Event");
        if (!getProperty(pTemplate).equals("Default")) {
            stringbuffer.append(" " + getProperty(pTemplate));
        }
        String s = getProperty(pEventSource);
        String s1 = getProperty(pEventID);
        if (!s.equals("SiteView") || !s1.equals("1000")) {
            stringbuffer.append(" " + s + ":" + s1);
        }
        return stringbuffer.toString();
    }

    public boolean defaultsAreSet(HashMap hashmap) {
        return true;
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pEventSource) {
            if (s.indexOf(' ') >= 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " no spaces allowed");
            }
            return s;
        }
        if (stringproperty == pEventID) {
            if (!com.dragonflow.Utils.TextUtils.onlyChars(s, "0123456789")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " must be a number");
            } else if (s.length() > 0 && com.dragonflow.Utils.TextUtils.toInt(s) > 65535) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " must be between 0 and 65535");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest, com.dragonflow.Page.CGI cgi) throws com.dragonflow.SiteViewException.SiteViewException {
        if (scalarproperty == pTemplate) {
            java.util.Vector vector = com.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.eventlog", httprequest);
            java.util.Vector vector2 = new Vector();
            for (int i = 0; i < vector.size(); i ++) {
                vector2.addElement(vector.elementAt(i));
                vector2.addElement(vector.elementAt(i));
            }

            return vector2;
        }
        if (scalarproperty == pEventType) {
            java.util.Vector vector1 = new Vector();
            vector1.addElement("default");
            vector1.addElement("Use Monitor Status");
            vector1.addElement("error");
            vector1.addElement("Error");
            vector1.addElement("warning");
            vector1.addElement("Warning");
            vector1.addElement("info");
            vector1.addElement("Information");
            return vector1;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public NTLogEvent() {
        runType = 2;
    }

    /**
     * 
     */
    public boolean execute() {
        boolean flag;
        flag = false;
        eventLogLock.get();

        try {
            long l = -1L;
            String s = com.dragonflow.SiteView.Platform.perfexCommand("") + " -e ";
            String s1 = "Default";
            String s2 = "SiteView";
            String s3 = "1000";
            String s4 = "default";
            String s5 = "0";
            String s6 = "localhost";
            String s7 = "";
            String s8 = monitor.getProperty(com.dragonflow.SiteView.Monitor.pCategory);
            if (args.length > 0 && args[0].length() > 0) {
                s1 = args[0];
            }
            if (args.length > 1 && args[1].length() > 0) {
                s2 = monitor.createFromTemplate(args[1]);
            }
            if (args.length > 2 && args[2].length() > 0) {
                s3 = monitor.createFromTemplate(args[2]);
            }
            if (args.length > 3 && args[3].length() > 0) {
                s4 = monitor.createFromTemplate(args[3]);
            }
            if (args.length > 4 && args[3].length() > 0) {
                s5 = monitor.createFromTemplate(args[4]);
            }
            if (args.length > 5 && args[5].length() > 0) {
                s6 = monitor.createFromTemplate(args[5]);
            }
            if (args.length > 6 && args[6].length() > 0) {
                s7 = monitor.createFromTemplate(args[6]);
            }
            if (s7.equals("_")) {
                s7 = "";
            }
            if (s4.equals("default")) {
                s4 = "error";
                if (s8.equals(com.dragonflow.SiteView.Monitor.WARNING_CATEGORY)) {
                    s4 = "warning";
                } else if (s8.equals(com.dragonflow.SiteView.Monitor.GOOD_CATEGORY)) {
                    s4 = "info";
                }
            }
            StringBuffer stringbuffer = new StringBuffer();
            String s9 = createMessage(stringbuffer, "templates.eventlog", s1);
            String s10 = "";
            if (s9.length() == 0) {
                s7 = s7 + stringbuffer.toString();
                s7 = com.dragonflow.Utils.TextUtils.replaceString(s7, "\n", ", ");
                s7 = com.dragonflow.Utils.TextUtils.replaceString(s7, "\r", "");
                if (s6.equals("localhost")) {
                    s6 = "";
                }
                s = s + "\"" + s7 + "\" \"" + s3 + "\" " + s5 + " " + s4 + " \"" + s2 + "\" " + s6;
                com.dragonflow.Utils.CommandLine commandline = new CommandLine();
                Array array = commandline.exec(s);
                l = commandline.getExitValue();
                s9 = s + " (" + l + ")";
                if (l == 0L) {
                    flag = true;
                }
                for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();) {
                    s10 = s10 + (String) enumeration.nextElement() + com.dragonflow.SiteView.Platform.FILE_NEWLINE;
                }

            }
            String s11 = "Event log alert performed";
            if (!flag) {
                s11 = "EVENT LOG ALERT ERROR RESULT";
            }
            messageBuffer.append(s11 + ", " + s9);
            logAlert(baseAlertLogEntry(s11, s9, flag) + " alert-result: " + l + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s10 + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE);
            eventLogLock.release();
        } catch (RuntimeException exception) {
            eventLogLock.release();
            throw exception;
        }
        return flag;
    }

    static {
        pTemplate = new ScalarProperty("_template", "Default");
        pTemplate.setDisplayText("Template", "the template used to create the event message.");
        pTemplate.setParameterOptions(true, 2, false);
        pMessage = new StringProperty("_message", "_");
        pMessage.setDisplayText("Message", "optional, enter text that is inserted in front of the event message.");
        pMessage.setParameterOptions(true, 3, true);
        pEventSource = new StringProperty("_eventSource", "SiteView");
        pEventSource.setDisplayText("Event Source", "The Source used for the event, by default this is \"SiteView\"");
        pEventSource.setParameterOptions(true, 4, true);
        pEventID = new StringProperty("_eventID", "1000");
        pEventID.setDisplayText("Event ID", "Event ID used for the event, by default this is 1000");
        pEventID.setParameterOptions(true, 5, true);
        pEventType = new ScalarProperty("_eventType", "default");
        pEventType.setDisplayText("Event Type", "Event Type used for the event, by default this is uses the status of the monitor - Error for error, Warning for warning, Informational for OK");
        pEventType.setParameterOptions(true, 6, true);
        pEventCategory = new StringProperty("_eventCategoryID", "0");
        pEventCategory.setDisplayText("Event Category ID", "Enter a number to used as the category ID for the event, by default this is 0");
        pEventCategory.setParameterOptions(true, 7, true);
        pEventMachine = new StringProperty("_eventMachine", "localhost");
        pEventMachine.setDisplayText("Send To", "Enter the name of the NT machine where the event is logged.  By default, the event is added to the event log of the local machine.");
        pEventMachine.setParameterOptions(true, 8, true);
        com.dragonflow.Properties.StringProperty astringproperty[] = { pTemplate, pMessage, pEventMachine, pEventSource, pEventID, pEventType, pEventCategory };
        com.dragonflow.StandardAction.NTLogEvent.addProperties("com.dragonflow.StandardAction.NTLogEvent", astringproperty);
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "description", "Logs an event to the Windows NT Event Log");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "help", "AlertNTLogEvent.htm");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "title", "Log Event");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "label", "Log Event");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "name", "Log Event");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "class", "NTLogEvent");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "prefs", "");
        com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "classType", "advanced");
        if (!com.dragonflow.SiteView.Platform.isWindows()) {
            com.dragonflow.StandardAction.NTLogEvent.setClassProperty("com.dragonflow.StandardAction.NTLogEvent", "loadable", "false");
        }
    }
}
